package com.inisteel.cim.yd.testYD.session;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecordSet;
import java.util.Vector;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdDBAssist;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSpecDao.YdCrnSpecDao;
import com.inisteel.cim.yd.common.dao.ydPrepMtlDao.YdPrepMtlDao;
import com.inisteel.cim.yd.common.dao.ydPrepSchDao.YdPrepSchDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCarSpecDao.YdCarSpecDao;
import com.inisteel.cim.yd.common.dao.ydTcarFtmvMtlDao.YdTcarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydWrkPlnSimulationDao.YdWrkPlnSimulationDao;
import  com.inisteel.cim.yd.common.dao.ydLocSrchRngDao.YdLocSrchRngDao;




import com.inisteel.cim.yd.common.delegate.YdDeleComm;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.tcconst.TcConstMgr;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.delegate.YdDeleComm;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

/**
 * YD 업무로직테스트용  Session EJB
 *
 * @ejb.bean name="YdSimSeEJB" jndi-name="YdSimSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class YdSimSeEJBBean extends BaseSessionBean {

	// Session Name
	private String szSessionName=getClass().getName();
	
	private YdUtils ydUtils =new YdUtils();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	private YdTcConst ydTcConst =new YdTcConst();
	
	private YdDBAssist ydDBAssist = new YdDBAssist();
	
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	
	YDDataUtil  yddatautil = new YDDataUtil();
	
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
	 * 오퍼레이션명 : BED 금지/해제
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void bedProhRel(JDTORecord msgRecord)throws JDTOException  {
		//적치bed DAO
		YdStkBedDao ydStkBedDao = new  YdStkBedDao();

		//파라미터 레코드 생성
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		//파라미터 string
		String szV_YD_STK_COL_GP       = null;
		String szV_YD_STK_BED_NO       = null;
		String szV_YD_STK_BED_ACT_STAT = null;
		String szV_MODIFIER            = null;
		
		
		
		
		int intRtnVal = 0;
		
		
		String szMsg="";
		String szMethodName="bedProhRel";
		
		

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
		
		//파라미터 null 체크
		szV_YD_STK_COL_GP       = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
		szV_YD_STK_BED_NO       = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
		szV_YD_STK_BED_ACT_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_ACT_STAT");
		szV_MODIFIER            = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");

		
		//파라미터 레코드 편집
		recPara.setField("YD_STK_COL_GP",       szV_YD_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO",       szV_YD_STK_BED_NO);
		recPara.setField("YD_STK_BED_ACT_STAT", szV_YD_STK_BED_ACT_STAT);
		recPara.setField("MODIFIER",            szV_MODIFIER);
		
		//적치bedDao 업데이트 실행
		intRtnVal = ydStkBedDao.updYdStkbed(recPara, 0);

		if (intRtnVal > 0) {
			szMsg="BED 금지/해제 처리("+szMethodName+") 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} else {
			szMsg="BED 금지/해제 처리("+szMethodName+") 실패" + " intRtnVal: " +intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
	
	}// end of bedProhRel()
	
	
	
	

	/**
	 * 오퍼레이션명 : 스케줄 금지/해제
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void schProhRel(JDTORecord msgRecord)throws JDTOException  {
		//크레인스케줄 DAO
		YdCrnSchDao  ydCrnSchDao  = new  YdCrnSchDao();
		//스케줄기준 DAO
		YdSchRuleDao ydSchRuleDao = new  YdSchRuleDao();
		
		JDTORecordSet rsPara = JDTORecordFactory.getInstance().createRecordSet("temp");
		//파라미터 레코드 생성
		JDTORecord recPara   = JDTORecordFactory.getInstance().create();
		JDTORecord recGetVal = JDTORecordFactory.getInstance().create();
		
		//파라미터 string
		String szV_YD_CRN_SCH_ID    = null;
		String szV_YD_SCH_CD        = null;
		String szV_YD_WRK_PROG_STAT = null;
		String szV_YD_SCH_PROH_EXN  = null;
		String szV_MODIFIER         = null;
		
		int intRtnVal = 0;
		
		
		String szMsg="";
		String szMethodName="schProhRel";
		
		

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
		//========스케줄 기준 활성상태 변경==========//
		//파라미터 null 체크
		//크레인스케줄ID
		szV_YD_CRN_SCH_ID    = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
		//스케줄코드
		szV_YD_SCH_CD        = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
		//크레인작업진행상태
		szV_YD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");
		//스케줄 금지 유무
		szV_YD_SCH_PROH_EXN  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_PROH_EXN");
		//수정자
		szV_MODIFIER         = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
		
		//파라미터 레코드 편집
		recPara.setField("YD_SCH_CD",        szV_YD_SCH_CD);
		recPara.setField("YD_CRN_SCH_ID",    szV_YD_CRN_SCH_ID);
		recPara.setField("YD_WRK_PROG_STAT", szV_YD_WRK_PROG_STAT);
		recPara.setField("YD_SCH_PROH_EXN",  szV_YD_SCH_PROH_EXN);
		recPara.setField("MODIFIER",         szV_MODIFIER);
		
		//스케줄기준Dao 업데이트 실행
		intRtnVal = ydSchRuleDao.updYdSchrule(recPara, 0);
		
		if (intRtnVal > 0) {
			szMsg="스케줄 기준 활성상태 변경("+szMethodName+") 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} else {
			szMsg="스케줄 기준 활성상태 변경("+szMethodName+") 실패" + " intRtnVal: " +intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		
		//========크레인스케줄 야드설비 작업상태 변경==========//
		
		//기존 상태값을 가져오기 위해 SELECT
		intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsPara, 6);
		if (intRtnVal < 1) {
				szMsg="크레인스케줄 야드설비 작업상태 변경("+szMethodName+") 실패" + " intRtnVal: " +intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return;
		}
		
		//select 레코드셋에서 레코드 추출
		rsPara.first();
		recGetVal = rsPara.getRecord();
		
		for (int Loop_i = 0; Loop_i < rsPara.size(); Loop_i++) {
			
			//현재 크레인스케줄ID SETTING
			recPara.setField("YD_CRN_SCH_ID", recGetVal.getFieldString("YD_CRN_SCH_ID"));
			
			//입력 상태값이 W 이면 (작업금지(C) -> 작업대기(W))
			if (szV_YD_WRK_PROG_STAT.equals("W")) {
				
				//현재 상태값이 C 이면 상태값 업데이트 (작업금지(C) -> 작업대기(W))
				if (recGetVal.getFieldString("YD_WRK_PROG_STAT").equals("C")) {
					
					//상태값 업데이트
					intRtnVal = ydCrnSchDao.updYdCrnsch(recPara, 0);
					if (intRtnVal > 0) {
						szMsg="크레인스케줄 야드설비 작업상태 변경("+szMethodName+") 완료" + " intRtnVal: " +intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					} else {
						szMsg="크레인스케줄 야드설비 작업상태 변경("+szMethodName+") 실패" + " intRtnVal: " +intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
				//상태값 이상이면 에러 리턴
				} else {
					szMsg="크레인스케줄 야드설비 작업상태 변경("+szMethodName+") 실패" + 
					      " 기존상태코드: " + recGetVal.getFieldString("YD_EQP_WRK_STAT") +
					      ", 변경상태코드: " + szV_YD_WRK_PROG_STAT;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			//입력 상태값이 C 이면 (작업대기(W) -> 작업금지(C))
			} else if (szV_YD_WRK_PROG_STAT.equals("C")) {
				//변경 상태값이 C 이면 상태값 업데이트 (작업대기(W) -> 작업금지(C))
				if (recGetVal.getFieldString("YD_WRK_PROG_STAT").equals("W")) {
					//상태값 업데이트
					intRtnVal = ydCrnSchDao.updYdCrnsch(recPara, 0);
					if (intRtnVal > 0) {
						szMsg="크레인스케줄 야드설비 작업상태 변경("+szMethodName+") 완료" + " intRtnVal: " +intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					} else {
						szMsg="크레인스케줄 야드설비 작업상태 변경("+szMethodName+") 실패" + " intRtnVal: " +intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
				} else {
					szMsg="크레인스케줄 야드설비 작업상태 변경("+szMethodName+") 실패" + 
					      " 기존상태코드: " + recGetVal.getFieldString("YD_WRK_PROG_STAT") +
					      ", 변경상태코드: " + szV_YD_WRK_PROG_STAT;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			} //end of if
			
			//다음 레코드로
			rsPara.next();
			recGetVal = rsPara.getRecord();
			
		} //end of for(Loop_i)

	}// end of schProhRel()
	
	
	
	

	/**
	 * 오퍼레이션명 : 스케줄 삭제
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String schCncl(JDTORecord msgRecord)throws JDTOException  {
		
		//크레인스케줄 DAO
		YdCrnSchDao ydCrnSchDao  = new  YdCrnSchDao();
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		//재료 DAO 
		YdStockDao ydStockDao = new YdStockDao();
		
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao(); 
		
		//설비 DAO (2009.10.06 추가-이현성)
		YdEqpDao ydEqpDao = new YdEqpDao();

		//크레인 작업재료 DAO
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		
		//wan
		//파라미터 레코드 생성
		JDTORecord recPara   	= JDTORecordFactory.getInstance().create();
		JDTORecord recDelPara   = JDTORecordFactory.getInstance().create();
		JDTORecord recParaStock = JDTORecordFactory.getInstance().create();
		JDTORecord recEqpPara   = JDTORecordFactory.getInstance().create();
		
		//크레인스케줄 데이터  레코드셋 생성
		JDTORecordSet rsGetCrnSch = null;
		//크레인스케줄 레코드
		JDTORecord recGetCrnSch   = null;
		
		//크레인작업재료 데이터  레코드셋 생성
		JDTORecordSet rsGetCrnMtl = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		//크레인작업재료 데이터  레코드셋 생성
		JDTORecordSet rsGetBedInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
	
		//크레인작업재료 레코드
		JDTORecord recGetCrnMtl   = null;
		//적치단 업데이트 레코드
		JDTORecord recSetStkLyr = JDTORecordFactory.getInstance().create();
		
		//적치베드 정보 UPDATE 레코드
		JDTORecord recSetStkBed = JDTORecordFactory.getInstance().create();
		
		JDTORecord inRec =null;
		
		int intRtnVal 			= 0;
		int intRsGetCrnMtlSize 	= 0;
		String szStkLyrPlus 	= null;
		
		//파라미터 string
		String szV_YD_CRN_SCH_ID  = null;
		String szV_YD_SCH_CD      = null;
		String szV_DEL_YN         = null;
		String szV_MODIFIER       = null;
		String szV_YD_UP_WO_LOC   = null;
		String szV_YD_UP_WO_LAYER = null;
		String szV_YD_DN_WO_LOC   = null;
		String szV_YD_DN_WO_LAYER = null;
		
		String szMsg			= "";
		String szMethodName		= "schCncl";
		
		String szJMS_TC_CD 		= "";
		String szYdSchId 		= "";
		String szYdWrkProgStat 	= "";
		String szYdGp 			= "";
		String szEqpId 			= "";
		String szOperationName 	= "스케줄 삭제";
		
		String szUpdEqpstat 	= "";
		JDTORecordSet rsCrnSchInfo = null;
		String szWbookId 		= "";
		
		try{
			szMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작   ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//========크레인스케줄 삭제==========//
			
			//파라미터 null 체크
			szV_YD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
			szV_YD_SCH_CD     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			szV_DEL_YN        = ydDaoUtils.paraRecChkNull(msgRecord, "DEL_YN");
			szV_MODIFIER      = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
			
			//파라미터 레코드 편집
			recPara.setField("YD_CRN_SCH_ID", szV_YD_CRN_SCH_ID);
			recPara.setField("YD_SCH_CD",     szV_YD_SCH_CD);
			recPara.setField("DEL_YN",        szV_DEL_YN);
			recPara.setField("MODIFIER",      szV_MODIFIER);
			
			//스케줄코드가 동일하고 크레인스케줄 ID이상인 크레인스케줄ID를 SELECT ( 추가 : 스케줄 ID에 포함된 같은 작업예약정보에서만 추출)
			rsGetCrnSch = JDTORecordFactory.getInstance().createRecordSet("YD");
			
			szMsg = "[Jsp Session : "+szOperationName+"] 스케줄코드가 동일하고 크레인스케줄 ID이상인 크레인스케줄ID를 SELECT";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsGetCrnSch, 5);
			
			//더 이상 삭제 작업이 없는경우
			if (intRtnVal < 1) {
				szMsg = "[Jsp Session : "+szOperationName+"] 삭제 작업이 완료되었습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				return "삭제 작업이 완료되었습니다";
			}
			
			//레코드셋을 역순으로
			szMsg = "[Jsp Session : "+szOperationName+"] 레코드셋을 역순으로 정렬 - reverseOrder";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsGetCrnSch.reverseOrder();
			//레코드셋의 커서를 처음으로
			
			szMsg = "[Jsp Session : "+szOperationName+"] 레코드셋처음으로 이동";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			rsGetCrnSch.first();
			
			szMsg = "[Jsp Session : "+szOperationName+"] 선택된 건수 :" + rsGetCrnSch.size()  ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//크레인스케줄 데이터 만큼 루프를 돌아서 크레인스케줄ID에 편성된 재료를 찾아 적치단을 CLEAR한다.
			for (int Loop_i = 0; Loop_i < rsGetCrnSch.size(); Loop_i++) {
				
				//크레인스케줄 데이터의 레코드를 추출(작업상태 체크를 위해 미리 추출)
				//ADD
				recGetCrnSch = JDTORecordFactory.getInstance().create();
				recGetCrnSch = rsGetCrnSch.getRecord(Loop_i);
				
				recPara = JDTORecordFactory.getInstance().create();
			
				szYdSchId =  ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_CRN_SCH_ID");
				recPara.setField("YD_CRN_SCH_ID",szYdSchId);
				
				szWbookId 		= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_WBOOK_ID");
							
				szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_WRK_PROG_STAT");
				/*
				//크레인이 작업지시대기인지를 체크한다.
				szMsg = "[Jsp Session : "+szOperationName+"] 작업진행상태 체크 "  ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if ((!szYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_IDLE)) && (!szYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_WO)) ) {
								
					szMsg = "[Jsp Session : "+szOperationName+"]" + "적치단 초기화실패! 상태가 (W,1)가 아님" + " 진행상태: " +   ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_WRK_PROG_STAT");
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
					return szMsg;
				}
				*/
				
				// 설비번호를 얻는다.(YD_EQP_ID) => 설비상태를  'W'(대기)상태로 만들어주기 위함
				szEqpId =  ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_EQP_ID");
				
				szMsg = "[Jsp Session : "+szOperationName+"] 스케줄에 편성된 설비번호: " + szEqpId  ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//권상 지시위치
				szV_YD_UP_WO_LOC   = ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_UP_WO_LOC");
				//권상 지시단
				szV_YD_UP_WO_LAYER = ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_UP_WO_LAYER");
				//권하 지시위치
				szV_YD_DN_WO_LOC   = ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_DN_WO_LOC");
				//권하 지시단
				szV_YD_DN_WO_LAYER = ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_DN_WO_LAYER");
				
				//해당 크레인스케줄ID로 크레인작업재료를 SELECT
				szMsg = "[Jsp Session : "+szOperationName+"] 해당 크레인스케줄ID로 크레인작업재료를 SELECT "  ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsGetCrnMtl = JDTORecordFactory.getInstance().createRecordSet("temp");
				intRtnVal = ydCrnSchDao.getYdCrnsch(recGetCrnSch, rsGetCrnMtl, 3);
				
				//에러리턴
				if (intRtnVal < 0) {
					
					szMsg = "[Jsp Session : "+szOperationName+"] 실패! 해당 작업재료 조회 ERROR :" +  intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return szMsg;
				}
				//------------------------------------------------------------------------------------------------
				// 권상지시 상태(작업지시가 내려간경우) - 취소 처리 
				//------------------------------------------------------------------------------------------------
				
				if( szYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_WO) ){
	
					//------------------------------------------------------------------------------------------------
					//  작업지시 취소 전문 : YD_CRN_SCH_ID,YD_WRK_PROG_STAT, MSG_GP = 'D'
					//------------------------------------------------------------------------------------------------
					szYdGp = szV_YD_SCH_CD.substring(0,1);	// 스케줄 코드에서 야드구분을 가져옴
	
					szMsg = "[Jsp Session : "+szOperationName+"] 작업지시취소전문 -  야드구분[" + szYdGp + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp)  ){						//C연주 슬라브 야드 [A]
						szJMS_TC_CD = "YDY1L004";
					}else if(YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYdGp)){	//항만슬라브야드 기능추가 - 2016.01.04 LeeJY
						szJMS_TC_CD = "YDE7L004";
					}else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp)){			//A후판 슬라브야드[D]
						szJMS_TC_CD = "YDY3L004";
					}else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp)){				//후판제품야드 [K]
						szJMS_TC_CD = "YDY4L004";
					}else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)){			//C열연 코일야드[H]
						szJMS_TC_CD = "YDY5L004";
					}else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)){			//C열연 제품야드[J]
						szJMS_TC_CD = "YDY5L004";
					} 
					
					if(szYdGp.equals(YdConstant.YD_GP_C_SLAB_YARD)
							|| YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp)
							|| YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYdGp)  //항만슬라브야드 기능추가 - 2016.01.04 LeeJY
							|| YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp)
							|| YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)
							|| YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)	){
						
						
						recDelPara   = JDTORecordFactory.getInstance().create();
						recDelPara.setField("MSG_ID",           szJMS_TC_CD        );
						recDelPara.setField("YD_CRN_SCH_ID",    szYdSchId          ); 
						recDelPara.setField("YD_WRK_PROG_STAT", szYdWrkProgStat    );   // 이모듈을 탈려면 항상 '1'의값이 들어옴
						recDelPara.setField("MSG_GP",           "D"                );
						
						YdDelegate ydDelegate = new YdDelegate();					
						ydUtils.displayRecord(szOperationName, recDelPara);					
						ydDelegate.sendMsg(recDelPara);
					}
					//------------------------------------------------------------------------------------------------
						
					//------------------------------------------------------------------------------------------------
					//설비별 보급 스케줄이 잡힌 상태
					// T/C 열연정정보급완료실적 (YDHRJ001) : 보급취소
					//------------------------------------------------------------------------------------------------
					
					if(szV_YD_SCH_CD.equals("HDFE03UM") ||
							szV_YD_SCH_CD.equals("HEDE01UM") ||
							szV_YD_SCH_CD.equals("HFFE02UM") ||
							szV_YD_SCH_CD.equals("HGFE01UM") ||
							szV_YD_SCH_CD.equals("HHKE01UM")     ){
							
	
						// 해당 스케줄일 경우 HR로 전문을 전송시킨다.
						// MSG_ID : YDHRJ001, STL_NO : 재료번호, TREAT_GP : 2
						szMsg = "[Jsp Session : "+szOperationName+"] 보급취소 : 스케줄 코드 [ "+ szV_YD_SCH_CD +"] 이므로 보급취소 전문을 전송한다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						inRec = JDTORecordFactory.getInstance().create();
						inRec.setField("YD_WBOOK_ID", szWbookId);
	
						JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("temRs");
						intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(inRec, outRecSet, 1);
	
						if(intRtnVal< 0){
							szMsg = "[Jsp Session : "+szOperationName+"] : 분기 작업 작업예약 조회시 작업예약 DATA ERROR";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							
						}else if (intRtnVal == 0){
							szMsg = "[Jsp Session : "+szOperationName+"] : 분기 작업 작업예약 조회시 작업예약 DATA 없음";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
						}					
						else{			
													
							szMsg = "[Jsp Session : "+szOperationName+"] : 분기 작업 작업예약 조회시 작업예약 DATA 존재함";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							
							outRecSet.first();
							inRec = JDTORecordFactory.getInstance().create();
							inRec = outRecSet.getRecord();
							
							String szTempStlNo = null;
							szTempStlNo = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");
							
							recPara = JDTORecordFactory.getInstance().create();
							recPara.setField("MSG_ID", "YDHRJ001");
							recPara.setField("STL_NO",  szTempStlNo);
							recPara.setField("TREAT_GP","2");		
	
							if(szV_YD_DN_WO_LOC.length() != 8){
								szMsg =  "[Jsp Session : "+szOperationName+"] 권하지시위치정보 보급취소 전문을 전송할 수 없습니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
							}else{
								recPara.setField("YD_EQP_ID",szV_YD_DN_WO_LOC.substring(0,6));
								recPara.setField("YD_STK_BED_NO",szV_YD_DN_WO_LOC.substring(6,8));
								
								YdDelegate ydDelegate = new YdDelegate();
								
								ydUtils.displayRecord("스케줄 취소중 보급취소전문", recPara);
								ydDelegate.sendMsg(recPara);
							}
						}
					} 
					//------------------------------------------------------------------------------------------------
				}
				//------------------------------------------------------------------------------------------------
				
				//------------------------------------------------------------------------------------------------
				// 권상/ 권하 위치 Log
				//------------------------------------------------------------------------------------------------
				szMsg="권상지시위치 "+szV_YD_UP_WO_LOC+"\n";
				szMsg+="권상 지시단 "+szV_YD_UP_WO_LAYER+"\n";
				szMsg+="권하 지시위치 "+szV_YD_DN_WO_LOC+"\n";
				szMsg+="권하 지시단 "+szV_YD_DN_WO_LAYER+"\n";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//------------------------------------------------------------------------------------------------
				
				//------------------------------------------------------------------------------------------------
				// 권하위치 원복
				//
				// - 2009.10.07 스케줄/작업 취소시 권하위치가 XX010101 BED로 잡혀있는경우나
				//   권하지시위치 정보가 올바르게 들어있지않는 정보는 돌려줄 수 없다.
				//------------------------------------------------------------------------------------------------
//SJH05001
				if (!( szV_YD_DN_WO_LOC.equals("") 
						|| szV_YD_DN_WO_LAYER.equals("")  
						|| szV_YD_DN_WO_LOC.equals("XX010101")
						|| szV_YD_DN_WO_LOC.equals("XXYY0101") )  ){
	
					//레코드의 커서를 처음으로
					szMsg = "[Jsp Session : "+szOperationName+"] 권상지시위치 " + szV_YD_DN_WO_LOC + "-" + szV_YD_DN_WO_LAYER ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					szMsg = "[Jsp Session : "+szOperationName+"] 크레인 작업재료 매수  " + rsGetCrnMtl.size() ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
					rsGetCrnMtl.first();
									
					//레코드 갯수를 구한다.
					intRsGetCrnMtlSize = rsGetCrnMtl.size();
					
					//크레인스케줄의 작업 재료 만큼 루프를 돌아 권상권하 대기 정보를 초기화한다.
					for (int Loop_j = 0; Loop_j < intRsGetCrnMtlSize; Loop_j++) {
						
						//크레인작업재료 데이터의 레코드를 추출
						recGetCrnMtl = JDTORecordFactory.getInstance().create();					
						recGetCrnMtl = rsGetCrnMtl.getRecord(Loop_j);
						
						/*
						//권하지시 적치열구분 (권하지시위치 = 적치열(6) + 적치BED(2))
						recSetStkLyr.setField("YD_STK_COL_GP",       szV_YD_DN_WO_LOC.substring(0, 6));
					
						//권하지시 적치BED번호
						recSetStkLyr.setField("YD_STK_BED_NO",       szV_YD_DN_WO_LOC.substring(6, 8));
					
						//권하지시 적치단
						szStkLyrPlus = ydDaoUtils.stringPlusInt(szV_YD_DN_WO_LAYER, Loop_j);
						recSetStkLyr.setField("YD_STK_LYR_NO",       szStkLyrPlus);
					
						//재료번호 CLEAR
						recSetStkLyr.setField("STL_NO",              "");
						
						//적치단재료상태 적치가능("E")으로 SET
						recSetStkLyr.setField("YD_STK_LYR_MTL_STAT", "E");
						
						szMsg = "[Jsp Session : "+szOperationName+"] : 권하 재료 정보 복원";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						//적치단 테이블에 권하지시 CLEAR 업데이트
						
						szMsg = "[Jsp Session : "+szOperationName+"] : 권하 지시 위치 CLEAR";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						ydUtils.displayRecord(szOperationName, recSetStkLyr);
						
						intRtnVal = ydStkLyrDao.updYdStklyr(recSetStkLyr, 0);
						*/
						
						// 기존 지시위치 에 쌓여 있는 정보 Clear
						recSetStkLyr = JDTORecordFactory.getInstance().create();
						recSetStkLyr.setField("YD_STK_COL_GP",       szV_YD_DN_WO_LOC.substring(0, 6));    
						recSetStkLyr.setField("YD_STK_BED_NO",       szV_YD_DN_WO_LOC.substring(6, 8));   
						/*
		                 * 2011.03.02 슬라브 권상모음 스케쥴 삭제시 문제발생 
		                 * 아래 파라미터 막음.
		                 */
		                //recSetStkLyr.setField("YD_STK_LYR_MTL_STAT", "D");
						recSetStkLyr.setField("STL_NO",				 yddatautil.setDataDefault(recGetCrnMtl.getField("STL_NO"),""));
	
						szMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
						intRtnVal = ydStkLyrDao.updYdStklyrWithColStockStat(recSetStkLyr);
	
						szMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear 성공 [ " + intRtnVal + " ] ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						//에러리턴
						if (intRtnVal < 1) {
							szMsg = "[Jsp Session : "+szOperationName+"] : 권하 지시 위치 CLEAR 실패";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
						}
						
						szMsg = "[Jsp Session : "+szOperationName+"] : 권하 지시 위치 CLEAR 성공";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
											
						szMsg = "[Jsp Session : "+szOperationName+"] : 권하위치 Bed 정보 조회";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
						rsGetBedInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
						intRtnVal =  ydStkBedDao.getYdStkbed(recSetStkLyr, rsGetBedInfo, 0);
						
						if(intRtnVal > 0){
						
							//------------------------------------------------------------------------------------------------
							// 야드적치BED입출고상태 변경
							// 권하위치 정보의 BED정보를 읽는다 -> 
							// 완산베드 상태를 적치가능상태로 바꾸어준다.
							//------------------------------------------------------------------------------------------------
							rsGetBedInfo.first();
							recSetStkBed = rsGetBedInfo.getRecord();
							
							if(YdConstant.YD_STK_BED_WHIO_FULL.equals(ydDaoUtils.paraRecChkNull(recSetStkBed, "YD_STK_BED_WHIO_STAT")) ){
								recSetStkBed.setField("YD_STK_BED_WHIO_STAT", YdConstant.YD_STK_BED_WHIO_ENABLE);
								recSetStkBed.setField("MODIFIER", szV_MODIFIER);
								
								szMsg = "[Jsp Session : "+szOperationName+"] : 완산베드 상태를 적치가능상태로 변경한다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
								intRtnVal = ydStkBedDao.updYdStkbed(recSetStkBed, 0);
								
								if(intRtnVal < 0 ){
									szMsg = "[Jsp Session : "+szOperationName+"] : 야드적치Bed입출고상태 변경 UPDATE ERROR 발생 .";
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
								}else{
									szMsg = "[Jsp Session : "+szOperationName+"] : 야드적치Bed입출고상태 변경 UPDATE 완료.";
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								}
							}
							//------------------------------------------------------------------------------------------------
						}else{
							//해당 베드의 정보가 존재 하지 않습니다.
							szMsg = "[Jsp Session : "+szOperationName+"] : 해당 베드의 정보가 존재 하지 않습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
						}
					}
				}else{
					//권하위치가 올바르게 잡혀있지 않을때 에러처리를 원한다면 RollBack 을 시킬수 있다.				
					szMsg = "[Jsp Session : "+szOperationName+"] : 권하위치가 올바른형식이 아니라 원복시킬수 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);				
				}
					
				//------------------------------------------------------------------------------------------------
				//	권상위치 원복 
				//------------------------------------------------------------------------------------------------
				if (!( szV_YD_UP_WO_LOC.equals("") || szV_YD_UP_WO_LAYER.equals("") )){
	
					//레코드의 커서를 처음으로
					
					szMsg = "[Jsp Session : "+szOperationName+"] : 권상지시 정보  :" + szV_YD_UP_WO_LOC + "-" + szV_YD_UP_WO_LAYER;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
					
					rsGetCrnMtl.first();
					
					//레코드 갯수를 구한다.
					intRsGetCrnMtlSize = rsGetCrnMtl.size();
					
					
					//------------------------------------------------------------------------------------------------
					//	크레인스케줄의 작업 재료 만큼 루프를 돌아 권상대기 정보를 초기화한다. 
					//------------------------------------------------------------------------------------------------
					
					
					for (int Loop_j = 0; Loop_j < intRsGetCrnMtlSize; Loop_j++) {
						
						//크레인작업재료 데이터의 레코드를 추출
						
						//ADD
						recGetCrnMtl = JDTORecordFactory.getInstance().create();					
						recGetCrnMtl = rsGetCrnMtl.getRecord(Loop_j);
		
						//권상지시 적치열구분 (권상지시위치 = 적치열(6) + 적치BED(2))
						recSetStkLyr.setField("YD_STK_COL_GP",       szV_YD_UP_WO_LOC.substring(0, 6));
						
						//권상지시 적치BED번호
						recSetStkLyr.setField("YD_STK_BED_NO",       szV_YD_UP_WO_LOC.substring(6, 8));
						
						//권상지시 적치단
						szStkLyrPlus = ydDaoUtils.stringPlusInt(szV_YD_UP_WO_LAYER, Loop_j);
						recSetStkLyr.setField("YD_STK_LYR_NO",       szStkLyrPlus);
			
						
						recSetStkLyr.setField("STL_NO", recGetCrnMtl.getField("STL_NO"));
						recSetStkLyr.setField("YD_STK_LYR_MTL_STAT", "C");
						
	
						szMsg = "[Jsp Session : "+szOperationName+"] : 권상지시 정보  복원" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						ydUtils.displayRecord(szOperationName, recSetStkLyr);
						
						
						//적치단 테이블에 권상지시 CLEAR 업데이트 ('U' -> 'C')
						intRtnVal = ydStkLyrDao.updYdStklyr(recSetStkLyr, 0);
						
						//에러리턴
						if (intRtnVal < 1) {
							szMsg = "[Jsp Session : "+szOperationName+"] : 적치단 테이블에 권상지시 CLEAR 업데이트 실패" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}
						
						szMsg = "[Jsp Session : "+szOperationName+"] : 적치단 테이블에 권상지시 CLEAR 업데이트 성공" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						//다음 스케줄 작업 재료 레코드로가기 전 작업한 스케줄 재료를 삭제 처리 해준다.
											
						szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄 작업 재료 삭제처리" ;
						
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						recPara.setField("YD_CRN_SCH_ID",szYdSchId);
						recPara.setField("DEL_YN", "Y");
						recPara.setField("MODIFIER",szV_MODIFIER);
						recPara.setField("STL_NO", recGetCrnMtl.getField("STL_NO"));	
						
						intRtnVal = ydCrnWrkMtlDao.updYdCrnwrkmtl(recPara, 0);
						 
						
						if(intRtnVal < 0){
							szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄 작업 재료 삭제처리시 ERROR" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else if(intRtnVal == 0 ){
							szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄 작업 재료 삭제처리 대상 없음" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
						}else{
							szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄 작업 재료 삭제처리 성공" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}
						
						
						//------------------------------------------------------------------------------------------------
						// 스케줄  작업 재료 삭제시 저장품에 등록된 작업예약 ID, 스케줄 CD Clear   
						//------------------------------------------------------------------------------------------------
						
						szMsg = "[Jsp Session : "+szOperationName+"] : 저장품  작업예약 ID, 스케줄코드 CLEAR" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
											
						recParaStock.setField("STL_NO",  recGetCrnMtl.getField("STL_NO"));	
						recParaStock.setField("MODIFIER",szV_MODIFIER);
						recParaStock.setField("YD_WBOOK_ID","" );
						recParaStock.setField("YD_SCH_CD","" );
						
						ydUtils.displayRecord(szOperationName, recParaStock);
						
						intRtnVal = ydStockDao.updYdStock(recParaStock, 0);
						
						if(intRtnVal < 0){
							szMsg = "[Jsp Session : "+szOperationName+"] : 저장품  작업예약 ID, 스케줄코드 CLEAR ERROR" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else if(intRtnVal == 0 ){
							szMsg = "[Jsp Session : "+szOperationName+"] : 저장품  작업예약 ID, 스케줄코드 CLEAR 대상 없음" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
						}else{
							szMsg = "[Jsp Session : "+szOperationName+"] : 저장품  작업예약 ID, 스케줄코드 CLEAR 성공" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}
						
						//------------------------------------------------------------------------------------------------
						
						
						
						
					}
					//------------------------------------------------------------------------------------------------
					
				
				
	
					//------------------------------------------------------------------------------------------------
					//	크레인스케줄 삭제처리 
					//------------------------------------------------------------------------------------------------
					
					szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄  삭제처리" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					
						recPara.setField("DEL_YN", "Y");
						recPara.setField("MODIFIER", szV_MODIFIER);		
						
						ydUtils.displayRecord(szOperationName, recPara);
						
						intRtnVal = ydCrnSchDao.updYdCrnsch(recPara, 0);
						
					
					if (intRtnVal > 0) {
						szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄  삭제처리 완료" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					} else {					
						szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄  삭제처리 실패" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						
					}
					
					//------------------------------------------------------------------------------------------------
					
					
					
					
					
					//------------------------------------------------------------------------------------------------
					// 2009.12.14  (이현성)
					// 설비 상태를 진행상태에 맞도록 변경 시킨다. 
					// 해당 작업 예약 ID으로 스케줄 정보 조회시에 하나도 존재 하지 않을경우에
					// - 해당 스케줄 코드로 전체 스케줄 조회시 남은스케줄 첫번째 진행상태 정보로 UPDATE 
					// - 해당 스케줄 코드로 전체 스케줄 조회시 남아있는것이 없을경우는 대기상태로 UPDAT 해준다.
					//------------------------------------------------------------------------------------------------
					
					recEqpPara   = JDTORecordFactory.getInstance().create();
					rsCrnSchInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
					recEqpPara.setField("YD_WBOOK_ID", szWbookId);
					
					intRtnVal = ydCrnSchDao.getYdCrnsch(recEqpPara, rsCrnSchInfo, 28);
					
					//설비 상태 UPDATE 유무 체크 FLAG 
					boolean lb_updEqpFlag  = false;
					
					if(intRtnVal < 0 ){
						
						szMsg = "[Jsp Session : "+szOperationName+"] : 해당 작업예약 정보에  남은 스케줄 조회시 ERROR" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						
						
						lb_updEqpFlag  = false;
						
					} else if (intRtnVal ==0){
						
						szMsg = "[Jsp Session : "+szOperationName+"] : 해당 작업예약 정보에  남은 스케줄 정보가 없으므로 작업대기 상태로 UPDATE 합니다." ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						
						
						//해당 스케줄 코드로 같은 편성된 스케줄이 없는지 확인한다.(다른작업예약 ID가 편성되었을경우)
						recEqpPara   = JDTORecordFactory.getInstance().create();
						rsCrnSchInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
						recEqpPara.setField("YD_SCH_CD", szV_YD_SCH_CD);
						
						
						szMsg = "[Jsp Session : "+szOperationName+"] : 해당 스케줄 코드로 같은 편성된 스케줄이 없는지 확인한다." ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						
						intRtnVal = ydCrnSchDao.getYdCrnsch(recEqpPara, rsCrnSchInfo, 6);
						
						
						
						if(intRtnVal < 0 ){
							szMsg="[Jsp Session : "+szOperationName+"] :남은 스케줄코드로 스케줄 조회시 ERROR";				
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							lb_updEqpFlag  = false;
						
						}  else if (intRtnVal == 0){
							szMsg="[Jsp Session : "+szOperationName+"] :남은 스케줄 정보가 없으므로 작업대기 상태로 UPDATE 합니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							szUpdEqpstat = YdConstant.YD_EQP_STAT_IDLE;
							lb_updEqpFlag  = true;
						} else{
							szMsg="[Jsp Session : "+szOperationName+"] :해당 스케줄 코드에 스케줄 정보가 남아 있어 설비정보는 남은스케줄 첫번째 진행상태 정보로 UPDATE 합니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							recEqpPara   = JDTORecordFactory.getInstance().create(); 
							rsCrnSchInfo.first();
							recEqpPara = rsCrnSchInfo.getRecord();
							szUpdEqpstat = ydDaoUtils.paraRecChkNull(recEqpPara, "YD_WRK_PROG_STAT");
							lb_updEqpFlag  = true;
							
						}
					
					} else{
						
						szMsg="[Jsp Session : "+szOperationName+"] 해당 작업예약 정보에 스케줄 정보가 남아 있어 설비정보는 UPDATE 하지 않습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						lb_updEqpFlag  = false;
						
					}
					
					
	                    if(lb_updEqpFlag){
						
						//설비정보 업데이트 하기전에 설비상태 체크해준다.
						JDTORecord recInfo   = JDTORecordFactory.getInstance().create();
				
						String szRtnMsg = YdCommonUtils.checkCrnStat(szEqpId, recInfo);
						
						
						if(szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
							
	
							if( ydDaoUtils.paraRecChkNull(recInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_UP_WO)
								|| ydDaoUtils.paraRecChkNull(recInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_UP_CMPL)
								|| ydDaoUtils.paraRecChkNull(recInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_DN_WO)
								|| ydDaoUtils.paraRecChkNull(recInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_DN_CMPL)
								){
	
								szMsg="설비상태가 대기 상태가 아닌 작업상태이기때문에 값을 변경 할수 없습니다.";				
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
								
							}else{
								
								recEqpPara   = JDTORecordFactory.getInstance().create();
								recEqpPara.setField("YD_EQP_ID", szEqpId);
								recEqpPara.setField("YD_EQP_STAT", szUpdEqpstat);
								recEqpPara.setField("MODIFIER",szV_MODIFIER);
								
								szMsg="++++++++++ 해당 스케줄 크레인("+ szEqpId +") 설비상태 [" + szUpdEqpstat +"]로 변경 ++++++++++++++++++";				
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
								ydUtils.displayRecord(szOperationName, recEqpPara);
								intRtnVal = ydEqpDao.updYdEqp(recEqpPara, 0);
								
								if(intRtnVal < 0 ){
									szMsg=szEqpId +"설비정보를 변경 실패 하였습니다.";	
									ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.ERROR);
									
								}
							}
						}
					}
					
					//------------------------------------------------------------------------------------------------
				}
				
				
				
				//------------------------------------------------------------------------------------------------
				
				
			}
			
			
			
	//		 작업 예약 /재료 삭제
			// 크레인 작업 재지시를 위하여  설비 아이디 , 스케줄 코드를 넘겨준다.
			
			msgRecord.setField("YD_EQP_ID", szEqpId);
			msgRecord.setField("YD_SCH_CD", szV_YD_SCH_CD);
			
			
			
			szMsg="[Jsp Session : "+szOperationName+"] :작업예약 삭제 호출";				
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg = this.delWBook(msgRecord);
			
			return szMsg;
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);			
		}finally { }
	
		
	}// end of schCncl()
	
	
	
	

	/**
	 * 오퍼레이션명 : 작업 취소
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String wrkCncl(JDTORecord msgRecord)throws JDTOException  {
		
		//크레인스케줄 DAO
		YdCrnSchDao  ydCrnSchDao        = new  YdCrnSchDao();
		//작업예약 DAO
		YdWrkbookDao  ydWrkbookDao      = new  YdWrkbookDao();
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new  YdWrkbookMtlDao();
		
		//리턴레코드셋
		JDTORecordSet rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
		//파라미터 레코드 생성
		JDTORecord recPara  = JDTORecordFactory.getInstance().create();
		JDTORecord recCheck  = JDTORecordFactory.getInstance().create();
		
		//파라미터 스크링 변수
		String szV_YD_CRN_SCH_ID  = null;
		String szV_YD_SCH_CD      = null;
		String szV_DEL_YN         = null;
		String szV_MODIFIER       = null;
		
		//리턴값
		int intRtnVal = 0;
		
		//체크 값
		String szC_YD_WRK_PROG_STAT= null;
		
		String szMsg		= "";
		String szMethodName	= "wrkCncl";
		
		//크레인스케줄 ID
		szV_YD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
		
		if (szV_YD_CRN_SCH_ID.equals("")) {
			
			szMsg="스케줄 취소 처리("+szMethodName+") 실패, YD_CRN_SCH_ID값이 없음";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szMsg;
		}
		
		//파라미터 레코드 필수항목 null 체크 및 스트링 편집
		//스케줄 코드
		szV_YD_SCH_CD     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
		//삭제유무
		szV_DEL_YN        = ydDaoUtils.paraRecChkNull(msgRecord, "DEL_YN");
		//수정자
		szV_MODIFIER      = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
		
		//파라미터 레코드 setting
		recPara  = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_CRN_SCH_ID", szV_YD_CRN_SCH_ID);
		recPara.setField("YD_SCH_CD",     szV_YD_SCH_CD);
		recPara.setField("DEL_YN",        szV_DEL_YN);		
		recPara.setField("MODIFIER",      szV_MODIFIER);
		/*
		 * 크레인 스케줄에서 선택된 스케줄 ID로 작업예약을 조회한 후
		 * 삭제되지 않는 작업예약에 포함된 크레인 스케줄중 첫번째 값이
		 * 2,3 인 경우 후처리를 하지않기 위해  Check Logic 반영 
		 */
		// com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCheckYdCrnSchId
		
		rsRtnVal 	= JDTORecordFactory.getInstance().createRecordSet("temRs");
		intRtnVal 	= ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 36);
		
		if (intRtnVal < 1){
			//szMsg = "대상 스케줄이 존재 하지않습니다";
			szMsg = "취소 작업을 완료 하였습니다.";
			return szMsg;
		}
		
		rsRtnVal.first();		
		recCheck = rsRtnVal.getRecord();
		
		szC_YD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recCheck, "YD_WRK_PROG_STAT"); 
		
		//2,3, 인 경우 와 혹시모를 4이면서도 스케줄 삭제가 되지 않은 경우
		if(szC_YD_WRK_PROG_STAT.equals("2") || szC_YD_WRK_PROG_STAT.equals("3") || szC_YD_WRK_PROG_STAT.equals("4")){
			szMsg = "크레인 작업이 완료되지 않았습니다!!";
			return szMsg;
		}
		
		/*
		 * 2,3이 아닌 경우 스케줄 취소기능에  첫번째 크레인 스케줄 ID 정보를 전송
		 */		
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recCheck, "YD_CRN_SCH_ID"));
		recPara.setField("YD_SCH_CD",     ydDaoUtils.paraRecChkNull(recCheck, "YD_SCH_CD"));
		recPara.setField("DEL_YN",        ydDaoUtils.paraRecChkNull(recCheck, "DEL_YN"));		
		recPara.setField("MODIFIER",      szV_MODIFIER);
		
		//크레인스케줄 ID보다 이상인 ID 삭제  업데이트 실행(적치단정보까지 CLEAR)
		//스케줄취소
		szMsg = this.schCncl(recPara);

		return szMsg;
	
	}// end of wrkCncl()
	
	
	/**
	 * 오퍼레이션명 : 작업예약 삭제
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord (YD_CRN_SCH_ID)
	 * @return
	 * @throws JDTOException
	 */
	public String delWBook(JDTORecord msgRecord)throws JDTOException  {
		
		
		//크레인스케줄 DAO
		YdCrnSchDao  ydCrnSchDao        = new  YdCrnSchDao();
		//작업예약 DAO
		YdWrkbookDao  ydWrkbookDao      = new  YdWrkbookDao();
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new  YdWrkbookMtlDao();
		
		YdDelegate ydDelegate = new YdDelegate();
		
		//대차 , 차량스케줄 DAO
		
		YdCarSchDao  ydCarSchDao = new YdCarSchDao();
		YdTcarSchDao  ydTcarSchDao = new YdTcarSchDao();
		
		
		
		//리턴레코드셋
		JDTORecordSet rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
		//파라미터 레코드 생성
		JDTORecord recPara  = JDTORecordFactory.getInstance().create();
		JDTORecord recCheck  = JDTORecordFactory.getInstance().create();
		JDTORecord inRec  = JDTORecordFactory.getInstance().create();
		
		JDTORecord recTemp  = JDTORecordFactory.getInstance().create();
		
		//파라미터 스크링 변수
		String szV_YD_CRN_SCH_ID    = null;	
		String szV_DEL_YN          = null;
		String szV_MODIFIER        = null;
		String szV_YD_WBOOK_ID     = null;
		String szOperationName		= "작업예약 삭제";
		
		
		String szSchCd = null;
		String szCarGp = null;
		String szULGp = null;
		
		//리턴값
		int intRtnVal = 0;
		
		//체크 값
		String szC_YD_WRK_PROG_STAT= null;
		
		String szMsg="";
		String szMethodName="wrkCncl";
		String szStlNo = "";
		
		
		// 크레인 작업 지시 EJB Call 시 필요한 변수
		String szEjbConName = "";
		String szLogMsg = "";
		String szJMS_TC_CD = "";
		EJBConnector ejbConn = null;
		String szYdGp = "";
		JDTORecord recDelPara = null;
		String szEqpId = "";
		String szV_YD_SCH_CD = "";
		String szYD_USER_ID = "";
		
		
		
		
		
		//들어온데이타  display 
		
		 szMsg="작업예약 삭제 처리 기능 시작";
	     ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.DEBUG);
		
//		 ydUtils.displayRecord(szOperationName, msgRecord);
		 
		 ydUtils.displayRecord("작업예약 삭제 처리 기능 IN-PARA", msgRecord);

		 
		 szYD_USER_ID = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
		
		
		
	   if (ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID").equals("")) {		
			//szMsg="스케줄 취소 처리("+szMethodName+") 실패, YD_CRN_SCH_ID값이 없음";
		   szMsg="스케줄 ID 정보가 없어서 작업예약 삭제처리를 하지 못하였습니다";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szMsg;
		}
		
		
		//크레인스케줄 ID
		szV_YD_CRN_SCH_ID  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
		szSchCd            = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
				
		//파라미터 레코드 setting
		recPara.setField("YD_CRN_SCH_ID", szV_YD_CRN_SCH_ID);
		
		rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
		intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 0);
		
		if (intRtnVal < 1 ){
			szMsg="해당크레인 스케줄이 존재하지않습니다";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szMsg;
		}
		
		rsRtnVal.first();
		recCheck = rsRtnVal.getRecord();
		szV_YD_WBOOK_ID = ydDaoUtils.paraRecChkNull(recCheck, "YD_WBOOK_ID");
	
		
		if (szV_YD_WBOOK_ID.equals("")){
			szMsg="해당크레인 스케줄에 작업예약 정보가 존재하지않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szMsg;
		}
		
		rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
		intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 36);
		
		if (intRtnVal < 0){
			szMsg = YdConstant.RETN_CD_FAILURE;
			return szMsg;
		} else  if (intRtnVal > 0){
			szMsg = "스케줄 정보가 남아 있습니다.";
			return szMsg;
		}
		
		
		
		//------------------------------------------------------------------------------------------------
		//	차량 / 대차 작업과 관계있는 작업 Clear (2010.01.13 이현성 수정)
		//------------------------------------------------------------------------------------------------
		
		String szRtnMsg = "";
		// 차량 또는 대차 스케줄에 있는 작업예약 ID를 Clear
		inRec    = JDTORecordFactory.getInstance().create();
		inRec.setField("MODIFIER", szYD_USER_ID);
		inRec.setField("YD_WBOOK_ID", szV_YD_WBOOK_ID);
		
		
		szRtnMsg = yddatautil.delWBookBefoCarOrTCar(inRec);
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szV_YD_WBOOK_ID+"] 대차/차량 스케줄 Clear성공 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		}else if(szRtnMsg.equals(YdConstant.RETN_CD_FAILURE)){
			szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szV_YD_WBOOK_ID+"] 대차/차량 스케줄 Clear 실패 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
		}else{
			szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szV_YD_WBOOK_ID+"]  " + szRtnMsg ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		

		//------------------------------------------------------------------------------------------------
		//	작업예약/재료 삭제
		//------------------------------------------------------------------------------------------------
		
		szRtnMsg = YdCommonUtils.delYdWrkbookNMtl(szV_YD_WBOOK_ID, szYD_USER_ID);
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szV_YD_WBOOK_ID+"]삭제 성공";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}else{
			szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szV_YD_WBOOK_ID+"]삭제 실패 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		//------------------------------------------------------------------------------------------------
		


		String szYD_WBOOK_ID = szV_YD_WBOOK_ID;
		JDTORecordSet outRecSet = null;
		//준비스케줄 원복 
		/*
		 * 상차작업예약ID와 연관된 준비스케줄과 준비재료의 DEL_YN항목을 N으로 설정
		 * 수정자 : 임춘수
		 * 수정일 : 2009.10.26
		 */
		szMsg = "[JSP Session : "+szOperationName+"] 삭제된 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 조회 시작";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		recPara         = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
		
		YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();
		
		intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 8);
		
		if( intRtnVal < 0  ) {
			szMsg = "[JSP Session : "+szOperationName+"] 삭제된 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 조회 시 오류발생 : 반환값 - " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}else if( intRtnVal == 0  ) {
			szMsg = "[JSP Session : "+szOperationName+"] 삭제된 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 조회 시 존재하지 않음 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}else{
			
			outRecSet.first();
			recPara = outRecSet.getRecord();
			
			String szYD_PREP_SCH_ID = ydDaoUtils.paraRecChkNull(recPara, "YD_PREP_SCH_ID");
			
			szMsg = "[JSP Session : "+szOperationName+"] 삭제된 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄[" + szYD_PREP_SCH_ID + "]이 존재하므로 DEL_YN => N으로 설정 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			YdPrepMtlDao ydPrepMtlDao = new YdPrepMtlDao();
			
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_PREP_SCH_ID",   	szYD_PREP_SCH_ID);
			recPara.setField("DEL_YN",   			"N");
			recPara.setField("MODIFIER",   			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
			//준비재료
			intRtnVal = ydPrepMtlDao.uptDelYdPrepmtlByPrepSchId(recPara);
			
			//준비스케줄
			recPara.setField("YD_WBOOK_ID",   		"");
			intRtnVal =  ydPrepSchDao.updDelYdPrepsch(recPara);
			
			szMsg = "[JSP Session : "+szOperationName+"] 삭제된 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄[" + szYD_PREP_SCH_ID + "]과 준비재료 DEL_YN => N으로 설정 성공";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		
		//작업 예약이 성공적으로 삭제 되었으므로 작업재지시 모듈을 호출한다.					
		//작업 취소전문이 발생 후 크레인 작업지시 (2009.10.28 요청사항) - 
		// (2009.12.16일 스케줄 삭제 모듈에서 작업예약 모듈로 이동) 작업예약 정보를 완전히 삭제 후 호출한다. 
		//추가 Parameter YD_EQP_ID, YD_SCH_CD
			
	
		
		szEqpId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"); 
		szV_YD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			
		
		//설비 ID 정보와 스케줄 코드가 들어왔을때만 실행한다.
		if (   szEqpId.equals("")  || szV_YD_SCH_CD.equals("")) {
			
			szMsg = "[JSP Session : "+szOperationName+"] 설비 ID 정보" + szEqpId + "스케줄 코드 [" + szV_YD_SCH_CD + "]" 
			+ "중 누락된 정보가 발생하여 해당 크레인 작업지시를 호출하지 않고 마칩니다";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			return YdConstant.RETN_CD_SUCCESS ;
			
		}
		
		szMsg = "[JSP Session : "+szOperationName+"] 설비 ID 정보" + szEqpId + "스케줄 코드 [" + szV_YD_SCH_CD + "]" ;
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
		szYdGp = szEqpId.substring(0,1);
		
		szLogMsg = "[JSP Session] - 작업예약 삭제 - 크레인 작업지시 : 야드구분[" + szYdGp + "]";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp)  ){						//C연주 슬라브 야드 [A]
			szJMS_TC_CD = "YDYDJ640";
			szEjbConName = "procY1CrnWrkOrdReq";			
			
		}else if(YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYdGp)){			//항만슬라브야드 기능추가 - 2016.01.04 LeeJY		
			szJMS_TC_CD = "YDYDJ640";
			szEjbConName = "procY1CrnWrkOrdReq";
		
		}else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp)){			//A후판 슬라브야드[D]
			
			szJMS_TC_CD = "YDYDJ641";
			szEjbConName = "procY3CrnWrkOrdReq";
		
		}else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp)){				//후판제품야드 [K]
			
			szJMS_TC_CD = "YDYDJ642";
			szEjbConName = "procY4CrnWrkOrdReq";
			
		}else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)){			//C열연 코일야드[H]
		
			szJMS_TC_CD = "YDYDJ643";
			szEjbConName = "procY5CrnWrkOrdReq";
			
			
		}else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)){			//C열연 제품야드[J]
			
			szJMS_TC_CD = "YDYDJ643";
			szEjbConName = "procY5CrnWrkOrdReq";
			
		} 	else if(YdConstant.YD_GP_INTGR_YARD.equals(szYdGp)){					//통합제품야드[S]
			
			szJMS_TC_CD = "YDYDJ644";
			szEjbConName = "procY0CrnWrkOrdReq";
		} 
		
		
		//JMS => EJB CALL 형식으로 수정요청 
		
		szMsg = "[JSP Session : "+szOperationName+"] 크레인 작업지시 정보를 EJB CALL 합니다"; 				
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
		recDelPara   = JDTORecordFactory.getInstance().create();
//SJH03004
		recDelPara.setField("MSG_ID",       szJMS_TC_CD        );
		recDelPara.setField("YD_EQP_ID"				, szEqpId            );					   
		recDelPara.setField("YD_WRK_PROG_STAT"		, YdConstant.YD_EQP_STAT_DN_CMPL );
		recDelPara.setField("YD_SCH_CD"				, szV_YD_SCH_CD );  
		
		ydUtils.displayRecord(szOperationName, recDelPara);

		try{

			ejbConn = new EJBConnector("default", this);	

			if( szEjbConName.equals("procY5CrnWrkOrdReq") ) {
				ejbConn.trx("CoilCraneLdHdSeEJB", szEjbConName, recDelPara);
			} else{
				ejbConn.trx("CraneLdHdSeEJB", szEjbConName, recDelPara);	
			}
//			ejbConn.trx("CraneLdHdSeEJB", szEjbConName, recDelPara);

			
		} catch (Exception e) {
			szMsg = "[JSP Session : "+szOperationName+"] 크레인 작업지시 정보 EJB CALL 시 에러가 발생하였습니다"; 				
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
		}
		
		return YdConstant.RETN_CD_SUCCESS ;
	
	}// end of delWBook()
	
	
	
	

	

	/**
	 * 오퍼레이션명 : C연주 TAKE_OUT 완료수신
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void ccTakeOutCmpl(JDTORecord msgRecord)throws JDTOException {
		
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao    = new YdStkLyrDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils      = new YdDaoUtils();
		//DELEGATE
		YdDelegate ydDelegate = new YdDelegate();
		//공용 METHOD
		YdUtils ydutils            = new YdUtils();
		
		//리턴값(int)
		int intRtnVal          = 0;
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "ccTakeOutCmpl";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord    recPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		
		//설비ID(열구분과 동일)
		String szYD_EQP_ID           = null;
		//적치BED번호
		String szYD_STK_BED_NO       = null;
		//CARRY_OUT 요구 구분
		String szCARRY_OUT_REQ_GP    = null;
		//TAKE_OUT 재료번호
		String szTAKE_OUT_STL_NO     = null;
		//재료 매수(String)
		String szYD_STK_BED_STL_SH   = null;
		//재료 매수(int)
		int intMtlCnt                = 0;
		//재료번호
		String [] szSTL_NO           = new String[5];
		//적치단재료상태
		String szYD_STK_LYR_MTL_STAT = null;
		//전문발생일시
		String szDate                = null;

		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		
		//에러 리턴
		if (szRcvTcCode == null) {
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try{
			
			
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치Bed번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치Bed번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//TAKE_OUT 재료번호
			szTAKE_OUT_STL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "TAKE_OUT_STL_NO");
			if (szTAKE_OUT_STL_NO.equals("")) {
				
				szMsg = "[전문 이상] TAKE_OUT 재료번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//CARRY_OUT 요구 구분
			szCARRY_OUT_REQ_GP  = ydDaoUtils.paraRecChkNull(msgRecord, "CARRY_OUT_REQ_GP");
			//재료 매수(String)
			szYD_STK_BED_STL_SH = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_STL_SH");
			//재료 매수(int)
			intMtlCnt           = Integer.parseInt(szYD_STK_BED_STL_SH);
			//재료번호
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO" + Loop_i);
				
			}
			
			//저장품 테이블에 해당 재료번호의 데이터가 있는지 체크한다.
			blnRtnVal = this.chkStock(szTAKE_OUT_STL_NO);
			if (!blnRtnVal) return;

			//조회결과 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			
			//적치단정보 조회
			blnRtnVal = this.chkGetStkLyr(szYD_EQP_ID, szYD_STK_BED_NO, "00" + intMtlCnt, rsResult);
			if (!blnRtnVal) return;

			//적치단정보 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//적치단 재료상태
			szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT");
			
			//적치단 재료상태가 적치가능('E')가 아니면 에러 리턴
			if (!szYD_STK_LYR_MTL_STAT.equals("E")) {
				
				szMsg = "적치단 재료상태(" + szYD_STK_LYR_MTL_STAT + ")!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//INSERT항목 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//적치단 재료상태가 적치 가능이면 재료 등록
			//적치단 테이블 업데이트
			//적치열구분 = 설비ID
			recPara.setField("YD_STK_COL_GP", 	    szYD_EQP_ID);
			recPara.setField("YD_STK_BED_NO", 	    szYD_STK_BED_NO);
			recPara.setField("YD_STK_LYR_NO", 	    "00" + intMtlCnt);
			recPara.setField("MODIFIER", 		    szUser);
			recPara.setField("YD_STK_LYR_MTL_STAT", "C");
			recPara.setField("STL_NO", 			    szTAKE_OUT_STL_NO);
			
			//업데이트 실행
			intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);
			
			//실행결과 디스플레이
			if (intRtnVal == 1) {
				szMsg = "적치단 Update 성공!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			} else {
				szMsg = "적치단 Update 실패!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			
			// CARRY_OUT 요구 구분항목이 "Y"이면 CARRY_OUT 요구 전문 송신
			if (szCARRY_OUT_REQ_GP.equals("Y")) {
				
				//전문 발생 일시
				szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
				//큐전송 항목 저장 레코드 생성
				recPara = JDTORecordFactory.getInstance().create();
				//JMS TC CODE
				recPara.setField("JMS_TC_CD",          "YDYDJ201");
				//발생 일시
				recPara.setField("JMS_TC_CREATE_DDTT", szDate);
				//설비ID
				recPara.setField("YD_EQP_ID",          szYD_EQP_ID);
				//적치BED번호
				recPara.setField("YD_STK_BED_NO",      szYD_STK_BED_NO);
				//Take-Out 재료번호
				recPara.setField("TAKE_OUT_STL_NO",    szTAKE_OUT_STL_NO);
				//적치재료매수
				recPara.setField("YD_STK_BED_STL_SH",  szYD_STK_BED_STL_SH);
				//재료번호
				for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

					recPara.setField("STL_NO" + Loop_i, szSTL_NO[Loop_i]);
					
				}
				
				//전문 송신
				ydDelegate.sendMsg(recPara);
				szMsg = "C연주 TAKE_OUT 처리 완료후 CARRY_OUT 송신 완료!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			}	// end if
				
		
		} catch (Exception e) {
		
			szMsg = "C연주 TAKE_OUT 처리중 ERROR : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		} //end try catch
		
		
		szMsg = "C연주 TAKE_OUT 완료수신(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
	
	} //end of ccTakeOutCmpl()
	

	
	
	/**
	 * 오퍼레이션명 : C연주불출구 CARRY_OUT 요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void ccExtSectCarryOutDmd(JDTORecord msgRecord)throws JDTOException  {
		
		//작업예약 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal               = false;
		//리턴값(int)
		int intRtnVal                   = 0;
		//메세지
		String szMsg                    = "";
		//METHOD명
		String szMethodName             = "ccExtSectCarryOut";
		//사용자
		String szUser                   = "SYSTEM";
		
		//레코드 선언
		JDTORecord recPara              = null;
		//레코드셋 선언
		JDTORecordSet rsResult          = null;
		
		//설비ID(열구분)
		String szYD_EQP_ID         = null;
		//적치BED번호
		String szYD_STK_BED_NO     = null;
		//TAKE_OUT 재료번호
		String szTAKE_OUT_STL_NO   = null;
		//재료매수(String)
		String szYD_STK_BED_STL_SH = null;
		//재료매수(int)
		int intMtlCnt              = 0;
		//재료번호
		String [] szSTL_NO         = new String[5];
		//스케줄코드
		String szYD_SCH_CD         = null;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN   = null;
		//작업크레인
		String szYD_WRK_CRN        = null;
		//대체크레인
		String szYD_ALT_CRN        = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN     = null;
		//선택크레인
		String szCrn               = null;
		//야드구분
		String szYD_GP             = null;
		//동구분
		String szYD_BAY_GP         = null;
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//설비ID(적치열)
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 야드설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//TAKE_OUT 재료번호
			szTAKE_OUT_STL_NO   = ydDaoUtils.paraRecChkNull(msgRecord,"TAKE_OUT_STL_NO");
			if (szTAKE_OUT_STL_NO.equals("")) {
				
				szMsg = "[전문 이상] TAKE_OUT 재료번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//재료매수(String)
			szYD_STK_BED_STL_SH = ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_BED_STL_SH");
			if (szYD_STK_BED_STL_SH.equals("")) {
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//재료매수(int)
			intMtlCnt = Integer.parseInt(szYD_STK_BED_STL_SH);
			
			//재료번호
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				
			}
			
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD  = "ADBHPULM";
			//=================================================================================
			
			
			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//스케줄CD 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				
			}

			//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			//작업예약재료의 등록 여부 체크
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal) return;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
			}			

			// INSERT 항목 RECORD 생성
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
			//레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			//작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			//야드구분
			szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			szYD_BAY_GP   = szYD_SCH_CD.substring(1, 2);
			//레코드 재생성
			recPara       = JDTORecordFactory.getInstance().create();
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		szYD_GP);
			recPara.setField("YD_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("REGISTER", 	szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//작업예약재료 정보 SET
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);
			
			//재료 매수만큼 작업예약재료 테이블에 저장한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				recPara.setField("STL_NO", 		   szSTL_NO[Loop_i]);
				recPara.setField("YD_STK_LYR_NO",  "00" + Loop_i);
				recPara.setField("YD_UP_COLL_SEQ", "" + (intMtlCnt - (Loop_i - 1)));
				
				//작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
				
				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "C연주불출구 CARRY_OUT 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg = "C연주불출구 CARRY_OUT 요구(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of ccExtSectCarryOutDmd()
    
	
	
	/**
	 * 오퍼레이션명 : C연주 OHC TAKE_OUT 완료수신
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void ccOHCTakeOutCmpl(JDTORecord msgRecord)throws JDTOException {
		
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao    = new YdStkLyrDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils      = new YdDaoUtils();
		//DELEGATE
		YdDelegate ydDelegate = new YdDelegate();
		//공용 METHOD
		YdUtils ydutils            = new YdUtils();
		
		//리턴값(int)
		int intRtnVal          = 0;
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "ccOHCTakeOutCmpl";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord    recPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		

		//설비ID(열구분과 동일)
		String szYD_EQP_ID           = null;
		//적치BED번호
		String szYD_STK_BED_NO       = null;
		//CARRY_OUT 요구 구분
		String szCARRY_OUT_REQ_GP    = "Y";
		//TAKE_OUT 재료번호
		String szOHC_TAKE_OUT_STL_NO = null;
		//적치단재료상태
		String szYD_STK_LYR_MTL_STAT = null;
		//전문발생일시
		String szDate                = null;

		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		
		//에러 리턴
		if (szRcvTcCode == null) {
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try{
			
			
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치Bed번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치Bed번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//OHC TAKE_OUT 재료번호
			szOHC_TAKE_OUT_STL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "OHC_TAKE_OUT_STL_NO");
			if (szOHC_TAKE_OUT_STL_NO.equals("")) {
				
				szMsg = "[전문 이상] OHC TAKE_OUT 재료번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//저장품 테이블에 해당 재료번호의 데이터가 있는지 체크한다.
			blnRtnVal = this.chkStock(szOHC_TAKE_OUT_STL_NO);
			if (!blnRtnVal) return;
			
			//조회결과 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//적치단정보 조회
			blnRtnVal = this.chkGetStkLyr(szYD_EQP_ID, szYD_STK_BED_NO, "001", rsResult);
			if (!blnRtnVal) return;
			
			//적치단정보 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//적치단 재료상태
			szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT");
			
			//적치단 재료상태가 적치가능('E')가 아니면 에러 리턴
			if (!szYD_STK_LYR_MTL_STAT.equals("E")) {
				
				szMsg = "적치단 재료상태(" + szYD_STK_LYR_MTL_STAT + ") 적치가능 상태가 아닙니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//INSERT항목 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//적치단 재료상태가 적치 가능이면 재료 등록
			//적치단 테이블 업데이트
			//적치열구분 = 설비ID
			recPara.setField("YD_STK_COL_GP", 	    szYD_EQP_ID);
			recPara.setField("YD_STK_BED_NO", 	    szYD_STK_BED_NO);
			recPara.setField("YD_STK_LYR_NO", 	    "001");
			recPara.setField("MODIFIER", 		    szUser);
			recPara.setField("YD_STK_LYR_MTL_STAT", "U");
			recPara.setField("STL_NO", 			    szOHC_TAKE_OUT_STL_NO);
			
			//업데이트 실행
			intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);
			
			//실행결과 디스플레이
			if (intRtnVal == 1) {
				szMsg = "적치단 Update 성공!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			} else {
				szMsg = "적치단 Update 실패!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			
			// CARRY_OUT 요구 구분항목이 "Y"이면 CARRY_OUT 요구 전문 송신
			if (szCARRY_OUT_REQ_GP.equals("Y")) {
				
				//전문 발생 일시
				szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
				//큐전송 항목 저장 레코드 생성
				recPara = JDTORecordFactory.getInstance().create();
				//JMS TC CODE
				recPara.setField("JMS_TC_CD",           "YDYD9903");
				//발생 일시
				recPara.setField("JMS_TC_CREATE_DDTT",  szDate);
				//설비ID
				recPara.setField("YD_EQP_ID",           szYD_EQP_ID);
				//적치BED번호
				recPara.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);
				//Take-Out 재료번호
				recPara.setField("OHC_TAKE_OUT_STL_NO", szOHC_TAKE_OUT_STL_NO);

				
				//전문 송신
				ydDelegate.sendMsg(recPara);
				szMsg = "C연주 OHC TAKE_OUT 처리 완료후 OHC CARRY_OUT 송신 완료!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			}	// end if
				
		
		} catch (Exception e) {
		
			szMsg = "C연주 OHC TAKE_OUT 처리중 ERROR : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		} //end try catch
		
		
		szMsg = "C연주 OHC TAKE_OUT 완료수신(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
	
	} //end of ccOHCTakeOutCmpl()
	


	
	
	
	/**
	 * 오퍼레이션명 : C연주 OHC CARRY_OUT 요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void ccOHCCarryOutDmd(JDTORecord msgRecord)throws JDTOException  {
		
		//작업예약 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal               = false;
		//리턴값(int)
		int intRtnVal                   = 0;
		//메세지
		String szMsg                    = "";
		//METHOD명
		String szMethodName             = "ccOHCCarryOutDmd";
		//사용자
		String szUser                   = "SYSTEM";
		
		//레코드 선언
		JDTORecord recPara              = null;
		//레코드셋 선언
		JDTORecordSet rsResult          = null;
		
		//설비ID(열구분)
		String szYD_EQP_ID            = null;
		//적치BED번호
		String szYD_STK_BED_NO        = null;
		//OHC TAKE_OUT 재료번호
		String szOHC_TAKE_OUT_STL_NO  = null;
		//스케줄코드
		String szYD_SCH_CD            = null;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN      = null;
		//작업크레인
		String szYD_WRK_CRN           = null;
		//대체크레인
		String szYD_ALT_CRN           = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN        = null;
		//선택크레인
		String szCrn                  = null;
		//야드구분
		String szYD_GP                = null;
		//동구분
		String szYD_BAY_GP            = null;
		//작업예약ID
		String szYD_WBOOK_ID          = null;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
		
			//받은 전문 편집
			//설비ID(적치열)
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치BED번호
			szYD_STK_BED_NO     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//OHC TAKE_OUT 재료번호
			szOHC_TAKE_OUT_STL_NO   = ydDaoUtils.paraRecChkNull(msgRecord, "OHC_TAKE_OUT_STL_NO");
			if (szOHC_TAKE_OUT_STL_NO.equals("")) {
				
				szMsg = "[전문 이상] OHC TAKE_OUT 재료번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
						
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD  = szYD_EQP_ID + "I" + "A";
			//=================================================================================
			
			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				
			}

			//크레인사양과 저장품 사양을 체크(길이,폭,중량)
			blnRtnVal = chkCrnSpecMtlSpec(szOHC_TAKE_OUT_STL_NO, szCrn);
			if (!blnRtnVal) return;

			//다른 작업예약에 재료가 등록되어있는지 체크한다.
			blnRtnVal = this.chkYdWrkBookMtl(szOHC_TAKE_OUT_STL_NO);
			if (!blnRtnVal) return;

			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
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
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		szYD_GP);
			recPara.setField("YD_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("REGISTER", 	szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//작업예약재료 정보 SET
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);
			recPara.setField("STL_NO", 		  szOHC_TAKE_OUT_STL_NO);
			recPara.setField("YD_STK_LYR_NO", "001");
				
			//작업예약재료 테이블에 등록한다.
			intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
			
			if (intRtnVal < 1) {
				szMsg = "작업예약재료 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

		} catch(Exception e) {
			szMsg = "C연주 OHC CARRY_OUT 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg = "C연주 OHC CARRY_OUT 요구(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of ccOHCCarryOutDmd()
	
	/**
	 * 오퍼레이션명 : A후판 슬라브야드 TAKE_OUT 완료수신
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void plSlabYdTakeOutCmpl(JDTORecord msgRecord)throws JDTOException  {
		
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao    = new YdStkLyrDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils      = new YdDaoUtils();
		//DELEGATE
		YdDelegate ydDelegate = new YdDelegate();
		//공용 METHOD
		YdUtils ydutils            = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "plSlabYdTakeOutCmpl";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord    recPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		
		//설비ID(열구분과 동일)
		String szYD_EQP_ID         = null;
		//적치BED번호
		String szYD_STK_BED_NO     = null;
		//CARRY_OUT 요구 구분
		String szCARRY_OUT_REQ_GP  = null;
		//TAKE_OUT 재료번호
		String szTAKE_OUT_STL_NO   = null;
		//재료 매수(String)
		String szYD_STK_BED_STL_SH = null;
		//재료 매수(int)
		int intMtlCnt              = 0;
		//재료번호
		String [] szSTL_NO         = new String[5];
		//전문발생일시
		String szDate              = null;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if (szRcvTcCode == null) {
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try{
			
			
			//받은 전문 편집
			//설비ID(열구분)
			szYD_EQP_ID         = ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}			
			//적치Bed번호
			szYD_STK_BED_NO     = ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치Bed번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//TAKE_OUT 재료번호
			szTAKE_OUT_STL_NO   = ydDaoUtils.paraRecChkNull(msgRecord,"TAKE_OUT_STL_NO");
			if (szTAKE_OUT_STL_NO.equals("")) {
				
				szMsg = "[전문 이상] TAKE_OUT 재료번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//CARRY_OUT 요구 구분
			szCARRY_OUT_REQ_GP  = ydDaoUtils.paraRecChkNull(msgRecord,"CARRY_OUT_REQ_GP");
			//재료매수(String)
			szYD_STK_BED_STL_SH = ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_BED_STL_SH");
			//재료매수(int)
			intMtlCnt = Integer.parseInt(szYD_STK_BED_STL_SH);
			//재료번호
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				
			}
			
			//저장품 테이블에 해당 재료번호의 데이터가 있는지 체크한다.
			blnRtnVal = this.chkStock(szTAKE_OUT_STL_NO);
			if (!blnRtnVal) return;
			
			//조회결과 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//적치단정보 조회
			blnRtnVal = this.chkGetStkLyr(szYD_EQP_ID, szYD_STK_BED_NO, "00" + intMtlCnt, rsResult);
			if (!blnRtnVal) return;
			
			//적치단정보 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//적치단 재료상태
			String szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT");
			//적치단 재료상태가 적치가능('E')가 아니면 에러 리턴
			if (!szYD_STK_LYR_MTL_STAT.equals("E")) {
				
				szMsg = "적치단 재료상태(" + szYD_STK_LYR_MTL_STAT + ") 적치가능 상태가 아닙니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//UPDATE 항목 record  생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//적치단 재료상태가 적치 가능이면 재료 등록
			//적치단 테이블 업데이트
			//적치열구분 = 설비ID
			recPara.setField("YD_STK_COL_GP", 	    szYD_EQP_ID);
			recPara.setField("YD_STK_BED_NO", 	    szYD_STK_BED_NO);
			recPara.setField("YD_STK_LYR_NO", 	    "00" + intMtlCnt);
			recPara.setField("MODIFIER", 		    szUser);
			recPara.setField("YD_STK_LYR_MTL_STAT", "U");
			recPara.setField("STL_NO", 			    szTAKE_OUT_STL_NO);
			
			//업데이트 실행
			intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);
			
			//실행결과 디스플레이
			if (intRtnVal == 1) {
				szMsg = "적치단 Update 성공!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			} else {
				szMsg = "적치단 Update 실패! intRtnVal: " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			
			// Carry-Out 요구 구분항목이 "Y"이면 Carry-Out 요구 전문 송신
			if (szCARRY_OUT_REQ_GP.equals("Y")) {
				
				//전문 발생 일시
				szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
				//큐전송 항목 레코드 생성
				recPara = JDTORecordFactory.getInstance().create();
				//JMS TC CODE
				recPara.setField("JMS_TC_CD",          "YDYD9901");
				//발생 일시
				recPara.setField("JMS_TC_CREATE_DDTT", szDate);
				//설비ID
				recPara.setField("YD_EQP_ID",          szYD_EQP_ID);
				//적치Bed번호
				recPara.setField("YD_STK_BED_NO",      szYD_STK_BED_NO);
				//Take-Out 재료번호
				recPara.setField("TAKE_OUT_STL_NO",    szTAKE_OUT_STL_NO);
				//적치Bed재료매수
				recPara.setField("YD_STK_BED_STL_SH",  szYD_STK_BED_STL_SH);
				//재료번호
				for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
					
					recPara.setField("STL_NO" + Loop_i, szSTL_NO[Loop_i]);
					
				}
				
				//전문 송신
				ydDelegate.sendMsg(recPara);
				szMsg = "A후판 슬라브야드 TAKE_OUT 처리 완료후 CARRY_OUT 송신 완료!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			}	// end if
				
		
		} catch (Exception e) {
		
			szMsg = "A후판 슬라브야드 TAKE_OUT 처리중 ERROR : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}	// end try catch
		
		
		szMsg = "A후판 슬라브야드 TAKE_OUT 완료수신(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
	
	} //end of plSlabYdTakeOutCmpl()
	
	
	/**
	 * 오퍼레이션명 : A후판 슬라브야드 CARRY_OUT 요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void plSlabYdCarryOutDmd(JDTORecord msgRecord)throws JDTOException  {
		
		//스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//스케줄기준 DAO
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
		String szMethodName    = "plSlabYdCarryOutDmd";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord recPara     = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//설비ID(열구분)
		String szYD_EQP_ID         = null;
		//적치BED번호
		String szYD_STK_BED_NO     = null;
		//TAKE_OUT 재료번호
		String szTAKE_OUT_STL_NO   = null;
		//재료매수(String)
		String szYD_STK_BED_STL_SH = null;
		//재료매수(int)
		int intMtlCnt              = 0;
		//재료번호
		String[] szSTL_NO          = new String[5];
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
			return;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
		
			//받은 전문 편집
			//설비ID(적치열)
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치Bed재료매수(String)
			szYD_STK_BED_STL_SH = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_STL_SH");
			if (szYD_STK_BED_STL_SH.equals("")) {
				
				szMsg = "[전문 이상] 적치Bed재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치Bed재료매수(int)
			intMtlCnt = Integer.parseInt(szYD_STK_BED_STL_SH);
			
			// Take-Out재료번호
			szTAKE_OUT_STL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "TAKE_OUT_STL_NO");		

			// 재료번호
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				
			}
			
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD  = szYD_EQP_ID + "I" + "A";
			//=================================================================================

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
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
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
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
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal) return;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
			}	
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
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
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				recPara.setField("STL_NO", 		  szSTL_NO[Loop_i]);
				recPara.setField("YD_STK_LYR_NO", "00" + Loop_i);
				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
				
				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "A후판 슬라브야드 CARRY_OUT 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg = "A후판 슬라브야드 CARRY_OUT 요구(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of plSlabYdCarryOutDmd()
	
	
	
	
	
	/**
	 * 오퍼레이션명 : A후판 창고야드 BOOK_OUT 완료수신
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void plWhYdBookOutCmpl(JDTORecord msgRecord)throws JDTOException  {
		
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao    = new YdStkLyrDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils      = new YdDaoUtils();
		//DELEGATE
		YdDelegate ydDelegate = new YdDelegate();
		//공용 METHOD
		YdUtils ydutils            = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal   = false;
		//리턴값(int)
		int intRtnVal       = 0;
		//메세지
		String szMsg        = "";
		//METHOD명
		String szMethodName = "plWhYdBookOutCmpl";
		//사용자
		String szUser       = "SYSTEM";
		
		//레코드 선언
		JDTORecord recPara  = null;
		
		//BOOK_OUT위치
		String szYD_BOOK_OUT_LOC   = null;
		//재료 매수
		String szYD_STK_BED_STL_SH = null;
		//재료 매수(int)
		int intMtlCnt              = 0;
		//CARRY_OUT요구 구분
		String szCARRY_OUT_REQ_GP  = null;
		//재료번호
		String[] szSTL_NO          = null;
		//저장위치
		String szSTK_LOC           = null;
		//전문발생일시
		String szDate              = null;

		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try{
			//받은 전문 편집
			//BOOK_OUT 위치
			szYD_BOOK_OUT_LOC   = ydDaoUtils.paraRecChkNull(msgRecord,"YD_BOOK_OUT_LOC");
			if (szYD_BOOK_OUT_LOC.equals("")) {
				
				szMsg = "[전문 이상] BOOK_OUT 위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}			
			//재료 매수
			szYD_STK_BED_STL_SH = ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_BED_STL_SH");
			if (szYD_STK_BED_STL_SH.equals("")) {
				
				szMsg = "[전문 이상] 재료 매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료 매수(int)
			intMtlCnt = Integer.parseInt(szYD_STK_BED_STL_SH);
			//재료번호 배열 생성
			szSTL_NO  = new String[intMtlCnt + 1];
			//CARRY_OUT 구분
			szCARRY_OUT_REQ_GP  = ydDaoUtils.paraRecChkNull(msgRecord,"CARRY_OUT_REQ_GP");
			if (szCARRY_OUT_REQ_GP.equals("")) {
				
				szMsg = "[전문 이상] CARRY_OUT 구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료 번호 * 재료 매수
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO" + Loop_i);
				
				//저장품 테이블에 해당 재료번호의 데이터가 있는지 체크한다.
				blnRtnVal = this.chkStock(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
			}

			//재료 길이 구분과 저장위치 길이 구분 비교
			szSTK_LOC = chkMtlLength(szSTL_NO[1], szYD_BOOK_OUT_LOC);
			if (szSTK_LOC.equals("")) {
				
				szMsg = "재료번호: " + szSTL_NO[1] + "의 길이구분에 속하는 저장위치가  없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//적치단 등록 데이터 편집
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP",       szSTK_LOC.substring(0, 6));
			recPara.setField("YD_STK_BED_NO",       szSTK_LOC.substring(6, 8));
			recPara.setField("MODIFIER",            szUser);
			recPara.setField("YD_STK_BED_ACT_STAT", "L");
			
			//BED 활성상태를 'L'(사용가능)으로 변경
			this.bedProhRel(recPara);
			
			//적치단 재료 상태 적치중('C')으로 SET
			recPara.setField("YD_STK_LYR_MTL_STAT", "C");
			
			//재료매수만큼 적치단에 재료등록
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				recPara.setField("YD_STK_LYR_NO", "00" + Loop_i);
				recPara.setField("STL_NO",        szSTL_NO[Loop_i]);
				
				intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);
				if (intRtnVal < 1) {
					szMsg = "Update ERROR 위치: " + szSTK_LOC   + 
					        ", 적치단: 00"         + Loop_i      +
					        ", 재료번호: "         + szSTL_NO[Loop_i];
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
				
			}
			
			
			// Carry-Out 요구 구분항목이 "Y"이면 Carry-Out 요구 전문 송신
			if (szCARRY_OUT_REQ_GP.equals("Y")) {
				
				//전문 발생 일시
				szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
				//큐전송 항목 저장 레코드 생성
				recPara = JDTORecordFactory.getInstance().create();
				//JMS TC CODE
				recPara.setField("JMS_TC_CD",          "YDYDJ203");
				//발생 일시
				recPara.setField("JMS_TC_CREATE_DDTT", szDate);
				//적치열구분
				recPara.setField("YD_EQP_ID",          szSTK_LOC.substring(0, 6));
				//적치BED번호
				recPara.setField("YD_STK_BED_NO",      szSTK_LOC.substring(6, 8));
				//적치재료매수
				recPara.setField("YD_STK_BED_STL_SH",  szYD_STK_BED_STL_SH);
				//재료번호
				for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
					
					recPara.setField("STL_NO" + Loop_i, szSTL_NO[Loop_i]);
					
				}
				
				//전문 송신
				ydDelegate.sendMsg(recPara);
				szMsg = "A후판 창고 야드 BOOK_OUT 처리 완료후 CARRY_OUT 송신 완료!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			}	// end if
				
		
		} catch (Exception e) {
		
			szMsg = "A후판 창고 야드 BOOK_OUT 처리중 ERROR : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}	// end try catch
		
		
		szMsg = "A후판 창고 야드 BOOK_OUT 완료수신(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
	
	} // end of plWhYdBookOutCmpl()
	
	
	
	/**
	 * 오퍼레이션명 : A후판 창고야드 CARRY_OUT 요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void plWhYdCarryOutDmd(JDTORecord msgRecord)throws JDTOException  {
		
		//스케줄기준 DAO
		YdSchRuleDao ydSchRuleDao       = new YdSchRuleDao();
		//작업예약 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//레코드 선언
		JDTORecord recPara     = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "plWhYdCarryOutDmd";
		//사용자
		String szUser          = "SYSTEM";
		
		
		//적치열구분
		String szYD_EQP_ID         = null;
		//적치BED번호
		String szYD_STK_BED_NO     = null;
		//적치재료매수(String)
		String szYD_STK_BED_STL_SH = null;
		//적치재료매수(int)
		int intMtlCnt              = 0;
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
		//야드구분
		String szYD_GP             = null;
		//동구분
		String szYD_BAY_GP         = null;
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//재료번호
		String [] szSTL_NO         = new String[6];

		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
		
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치Bed번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치Bed번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료매수
			szYD_STK_BED_STL_SH = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_STL_SH");
			if (szYD_STK_BED_STL_SH.equals("")) {
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료매수(int)
			intMtlCnt = Integer.parseInt(szYD_STK_BED_STL_SH);
			//재료번호
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO" + Loop_i);
				
			}
			
			
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD = "KCPGRTLL";
			//=================================================================================
			
			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
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
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
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
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal) return;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
			}

			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
			//레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			//작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			
			//작업예약 테이블 INSERT할 항목 레코드 생성
			recPara       = JDTORecordFactory.getInstance().create();
			//야드구분
			szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		szYD_GP);
			recPara.setField("YD_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("REGISTER",    szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 Table 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			//작업예약재료 테이블 조회 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER",      szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				recPara.setField("STL_NO", 		  szSTL_NO[Loop_i]);
				recPara.setField("YD_STK_LYR_NO", "00" + Loop_i);
				recPara.setField("YD_UP_COLL_SEQ", "" + (intMtlCnt - (Loop_i - 1)));
				

				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
				
				if(intRtnVal < 1){
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "A후판 창고야드 CARRY_OUT 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "A후판 창고야드 CARRY_OUT 요구(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of plWhYdCarryOutDmd()
	
	
	
	
	/**
	 * 오퍼레이션명 : A후판 차량 하차작업 요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mAPLCarCarudWrkDmd(JDTORecord msgRecord)throws JDTOException  {
		
		//작업예약 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//레코드 선언
		JDTORecord recPara     = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "mAPLCarCarudWrkDmd";
		//사용자
		String szUser          = "SYSTEM";
		
		
		//차량스케줄ID
		String szYD_CAR_SCH_ID    = null;
		//적치열구분
		String szYD_STK_COL_GP    = null;
		//적치재료매수(int)
		int intMtlCnt             = 0;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN  = null;
		//작업크레인
		String szYD_WRK_CRN       = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN    = null;
		//대체크레인
		String szYD_ALT_CRN       = null;
		//선택크레인
		String szCrn              = null;
		//야드구분
		String szYD_GP            = null;
		//동구분
		String szYD_BAY_GP        = null;
		//작업예약ID
		String szYD_WBOOK_ID      = null;
		//스케줄코드
		String szYD_SCH_CD        = null;
		//재료번호
		String [] szSTL_NO        = null;
		//적치BED번호
		String [] szYD_STK_BED_NO = null;
		//적치단번호
		String [] szYD_STK_LYR_NO = null;

		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//차량스케줄ID
			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_ID");
			if (szYD_CAR_SCH_ID.equals("")) {
				
				szMsg = "[전문 이상] 차량스케줄ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분(하차위치)
			szYD_STK_COL_GP     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 저장위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//차량스케줄 데이터 체크
			blnRtnVal = this.chkGetCarSch(szYD_CAR_SCH_ID, rsResult);
			if (!blnRtnVal) return;
			
			//차량스케줄 데이터 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			//차량 정보 정합성 체크
			blnRtnVal = this.chkCarInfo(recPara, msgRecord);
			if (!blnRtnVal) return;

			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD = szYD_STK_COL_GP + "I" + "A";
			//=================================================================================
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
			//스케줄 기준 데이터 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄기준 체크
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
			}
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약 재료 조회
			blnRtnVal = this.chkGetCarftmvmtl(szYD_CAR_SCH_ID, rsResult);
			if (!blnRtnVal) return;
			
			//차량이송재료 갯수
			intMtlCnt       = rsResult.size();
			//재료번호
			szSTL_NO        = new String[intMtlCnt + 1];
			//적치BED번호
			szYD_STK_BED_NO = new String[intMtlCnt + 1];
			//적치단번호
			szYD_STK_LYR_NO = new String[intMtlCnt + 1];
			//커서 첨으로 이동
			rsResult.first();
			//차량이송재료 갯수만큼 재료번호, 적치BED번호, 적치단번호 배열에 대입, 작업예약등록여부체크
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				//레코드 추출
				recPara                 = rsResult.getRecord();
				//재료번호
				szSTL_NO[Loop_i]        = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				//적치BED번호
				szYD_STK_BED_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				//적치단번호
				szYD_STK_LYR_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
				//다음 레코드로 이동
				rsResult.next();
			}
			
//			//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다
//			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
//				
//				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
//				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
//				if (!blnRtnVal) return;
//				
//			}

			//작업예약 테이블 INSERT할 항목 레코드 생성
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = this.getYdWbookId(rsResult);
			if (!blnRtnVal) return;
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			//작업예약ID
			szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(recPara, "YD_WBOOK_ID");
			
			recPara       = JDTORecordFactory.getInstance().create();
			//야드구분
			szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		szYD_GP);
			recPara.setField("YD_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("REGISTER",    szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 TABLE 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			//작업예약재료 테이블 조회 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("REGISTER",      szUser);
			//재료매수만큼 루프를 돌아서 작업예약재료를 등록한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				recPara.setField("STL_NO", 		  szSTL_NO[Loop_i]);
				recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO[Loop_i]);
				recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO[Loop_i]);
				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
				
				if(intRtnVal < 1){
					szMsg = "작업예약재료 TABLE 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "A후판 차량 하차작업 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "A후판 차량 하차작업 요구(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mAPLCarCarudWrkDmd()
	
	
	/**
	 * 오퍼레이션명 : C연주 차량 하차작업 요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCCCCarCarudWrkDmd(JDTORecord msgRecord)throws JDTOException  {
		
		//차량이송재료 DAO
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		//스케줄기준 DAO
		YdSchRuleDao ydSchRuleDao       = new YdSchRuleDao();
		//작업예약 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//레코드 선언
		JDTORecord recPara     = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "mCCCCarCarudWrkDmd";
		//사용자
		String szUser          = "SYSTEM";

		//차량스케줄ID
		String szYD_CAR_SCH_ID    = null;
		//적치열구분
		String szYD_STK_COL_GP    = null;
		//적치재료매수(int)
		int intMtlCnt             = 0;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN  = null;
		//작업크레인
		String szYD_WRK_CRN       = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN    = null;
		//대체크레인
		String szYD_ALT_CRN       = null;
		//선택크레인
		String szCrn              = null;
		//야드구분
		String szYD_GP            = null;
		//동구분
		String szYD_BAY_GP        = null;
		//작업예약ID
		String szYD_WBOOK_ID      = null;
		//스케줄코드
		String szYD_SCH_CD        = null;
		//재료번호
		String [] szSTL_NO        = null;
		//적치BED번호
		String [] szYD_STK_BED_NO = null;
		//적치단번호
		String [] szYD_STK_LYR_NO = null;

		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//차량스케줄ID
			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_ID");
			if (szYD_CAR_SCH_ID.equals("")) {
				
				szMsg = "[전문 이상] 차량스케줄ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분(하차위치)
			szYD_STK_COL_GP     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 저장위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//차량스케줄 데이터 체크
			blnRtnVal = this.chkGetCarSch(szYD_CAR_SCH_ID, rsResult);
			if (!blnRtnVal) return;
			
			//차량스케줄 데이터 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			//차량 정보 정합성 체크
			blnRtnVal = this.chkCarInfo(recPara, msgRecord);
			if (!blnRtnVal) return;

			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD = szYD_STK_COL_GP + "I" + "A";
			//=================================================================================
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
			//스케줄 기준 데이터 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄기준 체크
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				
			}
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			blnRtnVal = this.chkGetCarftmvmtl(szYD_CAR_SCH_ID, rsResult);
			if (!blnRtnVal) return;
			
			//차량이송재료 갯수
			intMtlCnt       = rsResult.size();
			//재료번호
			szSTL_NO        = new String[intMtlCnt + 1];
			//적치BED번호
			szYD_STK_BED_NO = new String[intMtlCnt + 1];
			//적치단번호
			szYD_STK_LYR_NO = new String[intMtlCnt + 1];
			//커서 첨으로 이동
			rsResult.first();
			//차량이송재료 갯수만큼 재료번호, 적치BED번호, 적치단번호 배열에 대입, 작업예약등록여부체크
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				//레코드 추출
				recPara                 = rsResult.getRecord();
				szSTL_NO[Loop_i]        = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				szYD_STK_BED_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				szYD_STK_LYR_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
				//다음 레코드로 이동
				rsResult.next();
			}
			
//			//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다
//			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
//				
//				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
//				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
//				if (!blnRtnVal) return;
//				
//			}			

			//작업예약 테이블 INSERT할 항목 레코드 생성
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = this.getYdWbookId(rsResult);
			if (!blnRtnVal) return;
			rsResult.first();
			recPara = rsResult.getRecord();
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			
			recPara       = JDTORecordFactory.getInstance().create();
			//야드구분
			szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		szYD_GP);
			recPara.setField("YD_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("REGISTER",    szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 Table 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			//작업예약재료 테이블 조회 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("REGISTER",      szUser);
			//재료매수만큼 루프를 돌아서 작업예약재료를 등록한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				recPara.setField("STL_NO", 		  szSTL_NO[Loop_i]);
				recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO[Loop_i]);
				recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO[Loop_i]);
				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
				
				if(intRtnVal < 1){
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "C연주 차량 하차작업 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "C연주 차량 하차작업 요구(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mCCCCarCarudWrkDmd()
	
	

	
	/**
	 * 오퍼레이션명 : C열연 차량 하차작업 요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCHRCarCarudWrkDmd(JDTORecord msgRecord)throws JDTOException  {
		
		//차량이송재료 DAO
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		//스케줄기준 DAO
		YdSchRuleDao ydSchRuleDao       = new YdSchRuleDao();
		//작업예약 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//레코드 선언
		JDTORecord recPara     = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "mCCCCarCarudWrkDmd";
		//사용자
		String szUser          = "SYSTEM";
		
		
		//차량스케줄ID
		String szYD_CAR_SCH_ID    = null;
		//적치열구분
		String szYD_STK_COL_GP    = null;
		//적치재료매수(int)
		int intMtlCnt             = 0;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN  = null;
		//작업크레인
		String szYD_WRK_CRN       = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN    = null;
		//대체크레인
		String szYD_ALT_CRN       = null;
		//선택크레인
		String szCrn              = null;
		//야드구분
		String szYD_GP            = null;
		//동구분
		String szYD_BAY_GP        = null;
		//작업예약ID
		String szYD_WBOOK_ID      = null;
		//스케줄코드
		String szYD_SCH_CD        = null;
		//재료번호
		String [] szSTL_NO        = null;
		//적치BED번호
		String [] szYD_STK_BED_NO = null;
		//적치단번호
		String [] szYD_STK_LYR_NO = null;

		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//차량스케줄ID
			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_ID");
			if (szYD_CAR_SCH_ID.equals("")) {
				
				szMsg = "[전문 이상] 차량스케줄ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분(하차위치)
			szYD_STK_COL_GP     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 저장위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//차량스케줄 데이터 체크
			blnRtnVal = this.chkGetCarSch(szYD_CAR_SCH_ID, rsResult);
			if (!blnRtnVal) return;
			
			//차량스케줄 데이터 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			//차량 정보 정합성 체크
			blnRtnVal = this.chkCarInfo(recPara, msgRecord);
			if (!blnRtnVal) return;

			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD = szYD_STK_COL_GP + "I" + "A";
			//=================================================================================
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
			//스케줄 기준 데이터 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄기준 체크
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				
			}
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			blnRtnVal = this.chkGetCarftmvmtl(szYD_CAR_SCH_ID, rsResult);
			if (!blnRtnVal) return;
			
			//차량이송재료 갯수
			intMtlCnt       = rsResult.size();
			//재료번호
			szSTL_NO        = new String[intMtlCnt + 1];
			//적치BED번호
			szYD_STK_BED_NO = new String[intMtlCnt + 1];
			//적치단번호
			szYD_STK_LYR_NO = new String[intMtlCnt + 1];
			//커서 첨으로 이동
			rsResult.first();
			//차량이송재료 갯수만큼 재료번호, 적치BED번호, 적치단번호 배열에 대입, 작업예약등록여부체크
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				//레코드 추출
				recPara                 = rsResult.getRecord();
				szSTL_NO[Loop_i]        = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				szYD_STK_BED_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				szYD_STK_LYR_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
				//다음 레코드로 이동
				rsResult.next();
			}
			
//			//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다
//			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
//				
//				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
//				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
//				if (!blnRtnVal) return;
//				
//			}			

			//작업예약 테이블 INSERT할 항목 레코드 생성
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = this.getYdWbookId(rsResult);
			if (!blnRtnVal) return;
			rsResult.first();
			recPara = rsResult.getRecord();
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			
			recPara       = JDTORecordFactory.getInstance().create();
			//야드구분
			szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		szYD_GP);
			recPara.setField("YD_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("REGISTER",    szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 Table 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			//작업예약재료 테이블 조회 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("REGISTER",      szUser);
			//재료매수만큼 루프를 돌아서 작업예약재료를 등록한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				recPara.setField("STL_NO", 		  szSTL_NO[Loop_i]);
				recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO[Loop_i]);
				recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO[Loop_i]);
				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
				
				if(intRtnVal < 1){
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "C열연 차량 하차작업 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "C열연 차량 하차작업 요구(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mCHRCarCarudWrkDmd()
	
	/**
	 * 오퍼레이션명 : C열연 소재 임가공 LOT 편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCHRMatlRentProcLotGp (JDTORecord msgRecord) throws JDTOException  {
		
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
		String szMethodName    = "mCHRMatlRentProcLotGp";
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
		//임가공사코드
		String szRENTPROC_CD       = null;
//		//목표동구분
//		String szYD_AIM_BAY_GP     = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//대차이송재료매수
		int intYD_CARLD_SH         = 0;
		//대상 재료 중량 합계
		long lngSumMtlWt           = 0;
		//야드작업허용중량
		long lngYD_WRK_ALW_WT      = 0;


		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
			if (szYD_CARLD_STOP_LOC.equals("")) {
				
				szMsg = "[전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료품목
			szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MTL_ITEM");
			if (szYD_MTL_ITEM.equals("")) {
				
				szMsg = "[전문 이상] 재료품목이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
			if (szYD_AIM_RT_GP.equals("")) {
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//야드작업허용중량
			lngYD_WRK_ALW_WT = ydDaoUtils.paraRecChkNullLong(msgRecord, "YD_WRK_ALW_WT");
			if (lngYD_WRK_ALW_WT == 0) {
				
				szMsg = "[전문 이상] 야드작업허용중량이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//임가공사코드
			szRENTPROC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "RENTPROC_CD");
			if (szRENTPROC_CD.equals("")) {
				
				szMsg = "[전문 이상] 임가공사코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
//			//목표동구분
//			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP");
//			if (szYD_AIM_BAY_GP.equals("")) {
//				
//				szMsg = "[전문 이상] 목표동구분이 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
	
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD  = "HDCMTRUM";
			//=================================================================================

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//상차 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetCHRMatlRentProcLotGp(msgRecord, rsResult);
			if (!blnRtnVal) return;			
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			
			//첫 레코드로 커서이동
			rsResult.first();
			
			//대상 소재 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				
				//레코드 추출
				recPara = rsResult.getRecord();
				//대상 재료 중량 합계
				lngSumMtlWt = lngSumMtlWt + ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");
				//대차작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
				if (lngYD_WRK_ALW_WT < lngSumMtlWt) break;
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
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYDJ248");
			//발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			//스케줄코드
			recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
			//CARRY_IN 재료매수
			recOutPara.setField("YD_CARLD_SH",        "" + intYD_CARLD_SH);
			//적치열구분
			recOutPara.setField("YD_STK_COL_GP",      szYD_CARLD_STOP_LOC);
			//적치BED번호
			recOutPara.setField("YD_STK_BED_NO",      "01");
			
			//전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "C열연 소재 임가공 LOT 편성 후 차량상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e) {
			szMsg = "C열연 소재 임가공 LOT 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "C열연 소재 임가공 LOT 편성(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} //end of mCHRMatlRentProcLotGp
	
	
	/**
	 * 오퍼레이션명 : C열연 차량상차 작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCHRCarLoadDmd(JDTORecord msgRecord)throws JDTOException  {
		
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
		String szMethodName    = "mCHRCarLoadDmd";
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
			return;
		
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
			if (szYD_SCH_CD.equals("")) {
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if (intMtlCnt == 0) {
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}

			//재료번호
			szSTL_NO          = new String[intMtlCnt + 1];
			//권상모음순서
			szYD_UP_COLL_SEQ  = new String[intMtlCnt + 1];
			
			
			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;	
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
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
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
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
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal) return;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
			}	
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
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
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
//			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
//			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				if (!blnRtnVal) return;
				
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
				
				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "C열연 차량상차 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg = "C열연 차량상차 작업요구 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mCHRCarLoadDmd()
	
	
	
	/**
	 * 오퍼레이션명 : C열연 대차상차 작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCHRTcarLoadDmd(JDTORecord msgRecord)throws JDTOException  {
		
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
		String szMethodName    = "mCHRTcarLoadDmd";
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
			return;
		
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
			if (szYD_SCH_CD.equals("")) {
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if (intMtlCnt == 0) {
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}

			//재료번호
			szSTL_NO          = new String[intMtlCnt + 1];
			//권상모음순서
			szYD_UP_COLL_SEQ  = new String[intMtlCnt + 1];
			
			
			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;	
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
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
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
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
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal) return;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
			}	
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
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
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
//			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
//			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				if (!blnRtnVal) return;
				
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
				
				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "C열연 대차상차 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg = "C열연 대차상차 작업요구 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mCHRTcarLoadDmd()
	
	
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
			if (intRtnVal >= 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "C열연 소재 임가공 Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "C열연 소재 임가공 Lot 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "C열연 소재 임가공 Lot 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "C열연 소재 임가공 Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetCHRMatlRentProcLotGp
	
	
	
	
	/**
	 * 오퍼레이션명 : 후판창고 차량 하차작업 요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mPLWHCarCarudWrkDmd(JDTORecord msgRecord)throws JDTOException  {
		
		//차량이송재료 DAO
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		//스케줄기준 DAO
		YdSchRuleDao ydSchRuleDao       = new YdSchRuleDao();
		//작업예약 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//레코드 선언
		JDTORecord recPara     = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "mPLWHCarCarudWrkDmd";
		//사용자
		String szUser          = "SYSTEM";
		
		
		//차량스케줄ID
		String szYD_CAR_SCH_ID    = null;
		//적치열구분
		String szYD_STK_COL_GP    = null;
		//적치재료매수(int)
		int intMtlCnt             = 0;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN  = null;
		//작업크레인
		String szYD_WRK_CRN       = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN    = null;
		//대체크레인
		String szYD_ALT_CRN       = null;
		//선택크레인
		String szCrn              = null;
		//야드구분
		String szYD_GP            = null;
		//동구분
		String szYD_BAY_GP        = null;
		//작업예약ID
		String szYD_WBOOK_ID      = null;
		//스케줄코드
		String szYD_SCH_CD        = null;
		//재료번호
		String [] szSTL_NO        = null;
		//적치BED번호
		String [] szYD_STK_BED_NO = null;
		//적치단번호
		String [] szYD_STK_LYR_NO = null;

		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//차량스케줄ID
			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_ID");
			if (szYD_CAR_SCH_ID.equals("")) {
				
				szMsg = "[전문 이상] 차량스케줄ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분(하차위치)
			szYD_STK_COL_GP     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 저장위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//차량스케줄 데이터 체크
			blnRtnVal = this.chkGetCarSch(szYD_CAR_SCH_ID, rsResult);
			if (!blnRtnVal) return;
			
			//차량스케줄 데이터 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			//차량 정보 정합성 체크
			blnRtnVal = this.chkCarInfo(recPara, msgRecord);
			if (!blnRtnVal) return;

			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD = szYD_STK_COL_GP + "I" + "A";
			//=================================================================================
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
			//스케줄 기준 데이터 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄기준 체크
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				
			}
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			blnRtnVal = this.chkGetCarftmvmtl(szYD_CAR_SCH_ID, rsResult);
			if (!blnRtnVal) return;
			
			//차량이송재료 갯수
			intMtlCnt       = rsResult.size();
			//재료번호
			szSTL_NO        = new String[intMtlCnt + 1];
			//적치BED번호
			szYD_STK_BED_NO = new String[intMtlCnt + 1];
			//적치단번호
			szYD_STK_LYR_NO = new String[intMtlCnt + 1];
			//커서 첨으로 이동
			rsResult.first();
			//차량이송재료 갯수만큼 재료번호, 적치BED번호, 적치단번호 배열에 대입, 작업예약등록여부체크
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				//레코드 추출
				recPara                 = rsResult.getRecord();
				szSTL_NO[Loop_i]        = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				szYD_STK_BED_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				szYD_STK_LYR_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
				//다음 레코드로 이동
				rsResult.next();
			}
			
			//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal) return;
				
			}			

			//작업예약 테이블 INSERT할 항목 레코드 생성
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = this.getYdWbookId(rsResult);
			if (!blnRtnVal) return;
			rsResult.first();
			recPara = rsResult.getRecord();
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			
			recPara       = JDTORecordFactory.getInstance().create();
			//야드구분
			szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		szYD_GP);
			recPara.setField("YD_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("REGISTER",    szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 Table 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			//작업예약재료 테이블 조회 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("REGISTER",      szUser);
			//재료매수만큼 루프를 돌아서 작업예약재료를 등록한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				recPara.setField("STL_NO", 		  szSTL_NO[Loop_i]);
				recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO[Loop_i]);
				recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO[Loop_i]);
				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
				
				if(intRtnVal < 1){
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "후판창고 차량 하차작업 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "후판창고 차량 하차작업 요구(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mPLWHCarCarudWrkDmd()
	
	
	/**
	 * 오퍼레이션명 : C연주 대차 하차작업 요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCCCTcarCarudWrkDmd(JDTORecord msgRecord)throws JDTOException  {

		//작업예약 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//레코드 선언
		JDTORecord recPara     = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "mCCCTcarCarudWrkDmd";
		//사용자
		String szUser          = "SYSTEM";
		
		
		//차량스케줄ID
		String szYD_TCAR_SCH_ID   = null;
		//적치열구분
		String szYD_STK_COL_GP    = null;
		//적치재료매수(int)
		int intMtlCnt             = 0;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN  = null;
		//작업크레인
		String szYD_WRK_CRN       = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN    = null;
		//대체크레인
		String szYD_ALT_CRN       = null;
		//선택크레인
		String szCrn              = null;
		//야드구분
		String szYD_GP            = null;
		//동구분
		String szYD_BAY_GP        = null;
		//작업예약ID
		String szYD_WBOOK_ID      = null;
		//스케줄코드
		String szYD_SCH_CD        = null;
		//재료번호
		String [] szSTL_NO        = null;
		//적치BED번호
		String [] szYD_STK_BED_NO = null;
		//적치단번호
		String [] szYD_STK_LYR_NO = null;

		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//차량스케줄ID
			szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_SCH_ID");
			if (szYD_TCAR_SCH_ID.equals("")) {
				
				szMsg = "[전문 이상] 대차스케줄ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분(하차위치)
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 저장위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}

			//대차스케줄 데이터 유무 체크
			blnRtnVal = this.chkTcarSch(szYD_TCAR_SCH_ID);
			if (!blnRtnVal) return;
			
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD = szYD_STK_COL_GP + "I" + "A";
			//=================================================================================

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//스케줄기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//대차이송재료 체크
			blnRtnVal = this.chkGetTcarftmvmtl(szYD_TCAR_SCH_ID, rsResult);
			if (!blnRtnVal) return;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//대차이송재료 갯수
			intMtlCnt       = rsResult.size();
			//재료번호
			szSTL_NO        = new String[intMtlCnt + 1];
			//적치BED번호
			szYD_STK_BED_NO = new String[intMtlCnt + 1];
			//적치단번호
			szYD_STK_LYR_NO = new String[intMtlCnt + 1];
			//커서 첨으로 이동
			rsResult.first();
			//대차이송재료 갯수만큼 재료번호, 적치BED번호, 적치단번호 배열에 대입, 작업예약등록여부체크
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				//레코드 추출
				recPara                 = rsResult.getRecord();
				szSTL_NO[Loop_i]        = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				szYD_STK_BED_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				szYD_STK_LYR_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
				//다음 레코드로 이동
				rsResult.next();
			}
			
//			//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다
//			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
//				
//				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
//				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
//				if (!blnRtnVal) return;
//				
//			}			

			//작업예약 테이블 INSERT할 항목 레코드 생성
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = this.getYdWbookId(rsResult);
			if (!blnRtnVal) return;
			rsResult.first();
			recPara = rsResult.getRecord();
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			
			recPara       = JDTORecordFactory.getInstance().create();
			//야드구분
			szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		szYD_GP);
			recPara.setField("YD_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("REGISTER",    szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 Table 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			//작업예약재료 테이블 조회 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("REGISTER",      szUser);
			//재료매수만큼 루프를 돌아서 작업예약재료를 등록한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				recPara.setField("STL_NO", 		  szSTL_NO[Loop_i]);
				recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO[Loop_i]);
				recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO[Loop_i]);
				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
				
				if(intRtnVal < 1){
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "C연주 대차 하차작업 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "C연주 대차 하차작업 요구(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mCCCTcarCarudWrkDmd
	
	
	/**
	 * 오퍼레이션명 : C열연 대차 하차작업 요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCHRTcarCarudWrkDmd(JDTORecord msgRecord) throws JDTOException  {

		//작업예약 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//레코드 선언
		JDTORecord recPara     = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "mCHRTcarCarudWrkDmd";
		//사용자
		String szUser          = "SYSTEM";
		
		
		//차량스케줄ID
		String szYD_TCAR_SCH_ID   = null;
		//적치열구분
		String szYD_STK_COL_GP    = null;
		//적치재료매수(int)
		int intMtlCnt             = 0;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN  = null;
		//작업크레인
		String szYD_WRK_CRN       = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN    = null;
		//대체크레인
		String szYD_ALT_CRN       = null;
		//선택크레인
		String szCrn              = null;
		//야드구분
		String szYD_GP            = null;
		//동구분
		String szYD_BAY_GP        = null;
		//작업예약ID
		String szYD_WBOOK_ID      = null;
		//스케줄코드
		String szYD_SCH_CD        = null;
		//재료번호
		String [] szSTL_NO        = null;
		//적치BED번호
		String [] szYD_STK_BED_NO = null;
		//적치단번호
		String [] szYD_STK_LYR_NO = null;

		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//차량스케줄ID
			szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_SCH_ID");
			if (szYD_TCAR_SCH_ID.equals("")) {
				
				szMsg = "[전문 이상] 대차스케줄ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분(하차위치)
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 저장위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}

			//대차스케줄 데이터 유무 체크
			blnRtnVal = this.chkTcarSch(szYD_TCAR_SCH_ID);
			if (!blnRtnVal) return;
			
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD = szYD_STK_COL_GP + "I" + "A";
			//=================================================================================

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//스케줄기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//대차이송재료 체크
			blnRtnVal = this.chkGetTcarftmvmtl(szYD_TCAR_SCH_ID, rsResult);
			if (!blnRtnVal) return;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//대차이송재료 갯수
			intMtlCnt       = rsResult.size();
			//재료번호
			szSTL_NO        = new String[intMtlCnt + 1];
			//적치BED번호
			szYD_STK_BED_NO = new String[intMtlCnt + 1];
			//적치단번호
			szYD_STK_LYR_NO = new String[intMtlCnt + 1];
			//커서 첨으로 이동
			rsResult.first();
			//대차이송재료 갯수만큼 재료번호, 적치BED번호, 적치단번호 배열에 저장 
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				//레코드 추출
				recPara                 = rsResult.getRecord();
				szSTL_NO[Loop_i]        = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				szYD_STK_BED_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				szYD_STK_LYR_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
				//다음 레코드로 이동
				rsResult.next();
			}
			
//			//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다
//			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
//				
//				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
//				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
//				if (!blnRtnVal) return;
//				
//			}			

			//작업예약 테이블 INSERT할 항목 레코드 생성
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = this.getYdWbookId(rsResult);
			if (!blnRtnVal) return;
			rsResult.first();
			recPara = rsResult.getRecord();
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			
			recPara       = JDTORecordFactory.getInstance().create();
			//야드구분
			szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		szYD_GP);
			recPara.setField("YD_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("REGISTER",    szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 Table 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			//작업예약재료 테이블 조회 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("REGISTER",      szUser);
			//재료매수만큼 루프를 돌아서 작업예약재료를 등록한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				recPara.setField("STL_NO", 		  szSTL_NO[Loop_i]);
				recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO[Loop_i]);
				recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO[Loop_i]);
				
				//이미 작업예약에 등록된 재료번호이면 에러 리턴
				if (!blnRtnVal) {
					
					szMsg = "재료번호(" + szSTL_NO[Loop_i] + ") 작업예약재료 등록 불가!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
				
				if(intRtnVal < 1){
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "C열연 대차 하차작업 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "C열연 대차 하차작업 요구(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mCHRTcarCarudWrkDmd
	
	
	/**
	 * 오퍼레이션명 : A후판Take-In재료등록
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mAPLTakeInCmpl (JDTORecord msgRecord) throws JDTOException  {
    	
		//TC_CODE: Y3YDL013
    	//A후판 TAKE_IN 완료 전문을 수신 받아 저장위치를 Clear 하고 마지막 TAKE_IN 재료이면
    	//보급로트편성 및 Carry_in 요구를 한다
		
		YdDelegate ydDelegate = new YdDelegate();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils       = new YdUtils();
		
		//레코드 선언
		JDTORecord recPara    = null;
		
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//메세지
		String szMsg          = "";
		//메소드명
		String szMethodName   = "mAPLTakeInCmpl";
		//사용자
		String szUser          = "SYSTEM";
		
		//설비ID
		String szYD_EQP_ID            = null;
		//적치BED번호
		String szYD_STK_BED_NO        = null;
		//적치Bed입출고상태
		String szYD_STK_BED_WHIO_STAT = null;
		//TAKE_IN 재료번호
		String szTAKE_IN_STL_NO       = null;
		//적치 재료 매수
		String szYD_STK_BED_STL_SH    = null;
		//재료 매수
		int intMtlCnt                 = 0;
		//재료번호
		String[] szSTL_NO             = new String[5];
		//CARRY_IN 요구 구분
		String szCARRY_IN_REQ_GP      = "N";
		//전문 생성 일시
		String szDate                 = null;


		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//TAKE_IN 재료번호
			szTAKE_IN_STL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "TAKE_IN_STL_NO");
			if (szTAKE_IN_STL_NO.equals("")) {
				
				szMsg = "[전문 이상] TAKE_IN 재료번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치 재료 매수
			szYD_STK_BED_STL_SH = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_STL_SH");
			if (szYD_STK_BED_STL_SH.equals("")) {
				
				szMsg = "[전문 이상] 적치 재료 매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료 매수
			intMtlCnt = Integer.parseInt(szYD_STK_BED_STL_SH) + 1;
			//재료번호
			for(int Loop_i = intMtlCnt; Loop_i >= 1; Loop_i--) {
				
				if (Loop_i == 1)
					szSTL_NO[Loop_i] = szTAKE_IN_STL_NO;
				else
					szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO" + Loop_i);
				
			}
			//야드적치Bed입출고상태
			szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_WHIO_STAT");
			
			//====================================================================================
			//현재 테스트를 위해 마지막 재료 이면 CARRY_IN 요구 구분을 "Y"로 바꾼다.
			//추후 기준 설정
			if (intMtlCnt == 1) szCARRY_IN_REQ_GP = "Y";
			//====================================================================================
			
			//레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//적치단 초기화 데이터 편집
			//적치열구분
			recPara.setField("YD_STK_COL_GP",       szYD_EQP_ID);
			//적치BED번호
			recPara.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);
			//적치단번호
			recPara.setField("YD_STK_LYR_NO",       "00" + intMtlCnt);
			//재료번호
			recPara.setField("STL_NO",              "");
			//적치단 재료상태
			recPara.setField("YD_STK_LYR_MTL_STAT", "E");

//==========================================================================================                    
//          기준이기 때문에 클리어 되면 안됨
//          2009.09.25 권오창
//            
//			//야드적치단X축
//			recPara.setField("YD_STK_LYR_XAXIS",    "0");
//			//야드적치단Y축
//			recPara.setField("YD_STK_LYR_YAXIS",    "0");
//			//야드적치단Z축
//			recPara.setField("YD_STK_LYR_ZAXIS",    "0");
//==========================================================================================                    

			//수정자
			recPara.setField("MODIFIER",            szUser);
			
			//적치단 업데이트
			blnRtnVal = this.setStkLyr(recPara, 0);
			if (!blnRtnVal) return;
			
			//====================================================================================
			//생산통제-장입진행실적 송신 연결
			//추후 구현
			//====================================================================================
			
			//CARRY_IN 요구 구분이 "Y"이면 CARRY_IN 요구 전문 생성 및 전송
			if (szCARRY_IN_REQ_GP.equals("Y")) {
				
				//레코드 생성
				recPara = JDTORecordFactory.getInstance().create();
				//전문 발생 일시
				szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
				//JMS TC CODE
				recPara.setField("JMS_TC_CD",              "YDYDJ231");
				//발생 일시
				recPara.setField("JMS_TC_CREATE_DDTT",     szDate);
				//설비ID 
				recPara.setField("YD_EQP_ID",              szYD_EQP_ID);
				//야드적치Bed입출고상태
				recPara.setField("szYD_STK_BED_WHIO_STAT", szYD_STK_BED_WHIO_STAT);
				//적치BED번호
				recPara.setField("YD_STK_BED_NO",          szYD_STK_BED_NO);
				//=====================================================================
				//목표행선구분(wan)-어떻게 보낼 것인가
				recPara.setField("YD_AIM_RT_GP",           "D1");
				//=====================================================================
				
				//전문 송신
				ydDelegate.sendMsg(recPara);
				szMsg = "A후판Take-In재료등록 후 A후판 가열로 보급 Lot 편성 송신 완료!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}
		} catch(Exception e) {
			szMsg = "A후판Take-In재료등록 예외발생, 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "A후판Take-In재료등록(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} //end of mAPLTakeInCmpl
	
	/**
	 * 오퍼레이션명 : A후판 가열로 보급 Lot 편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mAPLREFURSupplyLotGp (JDTORecord msgRecord) throws JDTOException  {
		
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
		String szMethodName    = "mAPLREFURSupplyLotGp";
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


		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//스케줄코드
			System.out.println("ok session1");
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
			if (szYD_AIM_RT_GP.equals("")) {
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치Bed입출고상태
			szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_WHIO_STAT");
//			if (szYD_STK_BED_WHIO_STAT.equals("")) {
//				
//				szMsg = "[전문 이상] 야드적치Bed입출고상태가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD  = "DASMPUUM";
			//=================================================================================
			
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//보급 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetCHRSupplyLotGp(szYD_SCH_CD, szYD_AIM_RT_GP, rsResult);
			if (!blnRtnVal) return;
			
			//지시 재료 매수가 Carry In 재료기준매수(현재4매)보다 작으면 지시 재료 매수를 재료기준매수에 대입한다.
			if (intCarryInCnt > rsResult.size()) intCarryInCnt = rsResult.size();
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//Carry In 재료기준매수(현재4매)만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intCarryInCnt; Loop_i++) {
				
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
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYDJ245");
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
			szMsg = "A후판 가열로 보급 Lot 편성 후 CARRY_IN 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e) {
			szMsg = "A후판 가열로 보급 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "A후판 가열로 Lot 편성(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} //end of mAPLREFURSupplyLotGp
	
	
	/**
	 * 오퍼레이션명 : A후판 Carry-In 작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mAPLCarryInDmd(JDTORecord msgRecord)throws JDTOException  {
		
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
		String szMethodName    = "mAPLCarryInDmd";
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
		String[] szSTL_NO          = new String[5];
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = new String[5];
		//적치단
		String[] szYD_STK_LYR_NO   = new String[5];
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
			return;
		
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
			if (szYD_SCH_CD.equals("")) {
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARRY_IN_SH");
			if (intMtlCnt == 0) {
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}

			//재료번호, 권상모음순서, 적치단(테스트용)
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;	
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
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
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
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
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal) return;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
			}	
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
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
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
//			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
//			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				if (!blnRtnVal) return;
				
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
				
				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "A후판 Carry-In 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg = "A후판 Carry-In 작업요구 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mAPLCarryInDmd()
	
	
	/**
	 * 오퍼레이션명 : A후판 소재이송 상차 Lot 편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mAPLMtlFtmvCarLoadLotGp (JDTORecord msgRecord) throws JDTOException  {
		
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
		String szMethodName    = "mAPLMtlFtmvCarLoadLotGp";
		//사용자
		String szUser          = "SYSTEM";

		//전문 생성 일시
		String szDate             = null;

		//설비ID
		String szYD_EQP_ID         = null;
		//상차정지위치
		String szYD_CARLD_STOP_LOC = null;
		//차량사용구분
		String szYD_CAR_USE_GP     = null;
		//운송장비코드
		String szTRN_EQP_CD        = null;
		//재료품목
		String szYD_MTL_ITEM       = null;
		//목표행선구분
		String szYD_AIM_RT_GP      = null;
		//목표야드구분
		String szYD_AIM_YD_GP      = null;
		//목표동구분
		String szYD_AIM_BAY_GP     = null;
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


		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
			if (szYD_CARLD_STOP_LOC.equals("")) {
				
				szMsg = "[전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
			if (szYD_CAR_USE_GP.equals("") || !szYD_CAR_USE_GP.equals("L")) {
				
				szMsg = "[전문 이상] 차량사용구분(" + szYD_CAR_USE_GP + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//운송장비코드
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
			if (szTRN_EQP_CD.equals("")) {
				
				szMsg = "[전문 이상] 운송장비코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료품목
			szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MTL_ITEM");
			if (szYD_MTL_ITEM.equals("")) {
				
				szMsg = "[전문 이상] 재료품목이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
			if (szYD_AIM_RT_GP.equals("")) {
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//목표야드구분
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_YD_GP");
			if (szYD_AIM_YD_GP.equals("")) {
				
				szMsg = "[전문 이상] 목표야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP");
			if (szYD_AIM_BAY_GP.equals("")) {
				
				szMsg = "[전문 이상] 목표동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
	
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD  = "DASPPTUM";
			//=================================================================================
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//차량사양 Select
			blnRtnVal = chkGetCarSpec(szYD_EQP_ID, szYD_CAR_USE_GP, szTRN_EQP_CD, rsResult);
			if (!blnRtnVal) return;

			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//차량작업허용매수
			intYD_WRK_ALW_SH = ydDaoUtils.paraRecChkNullInt(recPara, "YD_WRK_ALW_SH");
			//차량작업허용중량
			lngYD_WRK_ALW_WT = ydDaoUtils.paraRecChkNullLong(recPara, "YD_WRK_ALW_WT");
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//상차 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetMtlFtmvCarLoadLotGp(msgRecord, rsResult);
			if (!blnRtnVal) return;			
			
			//이송 재료 매수가 차량 이송 가능 매수보다 작으면 이송 재료 매수를 차량 이송 가능 매수에 대입한다.
			if (intYD_WRK_ALW_SH > rsResult.size()) intYD_WRK_ALW_SH = rsResult.size();
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//차량 이송 가능 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intYD_WRK_ALW_SH; Loop_i++) {
				
				//대상 재료 중량 합계
				lngSumMtlWt = lngSumMtlWt + Long.parseLong(ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_WT"));
				//차량작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
				if (lngYD_WRK_ALW_WT < lngSumMtlWt) break;
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
			
			//전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYDJ246");
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
			
			//전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "A후판 소재이송 상차 Lot 편성 후 차량상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e) {
			szMsg = "A후판 소재이송 상차 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "A후판 소재이송 상차 Lot 편성(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} //end of mAPLMtlFtmvCarLoadLotGp
	
	
	/**
	 * 오퍼레이션명 : A후판 차량상차 작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mAPLCarLoadDmd(JDTORecord msgRecord)throws JDTOException  {
		
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
		String szMethodName    = "mAPLCarLoadDmd";
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
			return;
		
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
			if (szYD_SCH_CD.equals("")) {
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if (intMtlCnt == 0) {
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}

			//재료번호
			szSTL_NO          = new String[intMtlCnt + 1];
			//권상모음순서
			szYD_UP_COLL_SEQ  = new String[intMtlCnt + 1];
			
			
			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;	
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
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
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
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
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal) return;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
			}	
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
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
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
//			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
//			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				if (!blnRtnVal) return;
				
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
				
				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "A후판 차량상차 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg = "A후판 차량상차 작업요구 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mAPLCarLoadDmd()
	
	
	/**
	 * 오퍼레이션명 : 후판 창고 차량상차 작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mPLWHCarLoadDmd(JDTORecord msgRecord)throws JDTOException  {
		
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
		String szMethodName    = "mPLWHCarLoadDmd";
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
			return;
		
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
			if (szYD_SCH_CD.equals("")) {
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if (intMtlCnt == 0) {
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}

			//재료번호
			szSTL_NO          = new String[intMtlCnt + 1];
			//권상모음순서
			szYD_UP_COLL_SEQ  = new String[intMtlCnt + 1];
			
			
			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;	
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
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
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
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
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal) return;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
			}	
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
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
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
//			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
//			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				if (!blnRtnVal) return;
				
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
				
				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "후판 창고 차량상차 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg = "후판 창고 차량상차 작업요구 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mPLWHCarLoadDmd()
	
	
	/**
	 * 오퍼레이션명 : C연주Take-In재료등록
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCCCTakeInCmpl (JDTORecord msgRecord) throws JDTOException  {
		
		YdDelegate ydDelegate = new YdDelegate();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils       = new YdUtils();
		
		//레코드 선언
		JDTORecord recPara    = null;
		
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//메세지
		String szMsg          = "";
		//메소드명
		String szMethodName   = "mCCCTakeInCmpl";
		//사용자
		String szUser          = "SYSTEM";
		
		//설비ID
		String szYD_EQP_ID            = null;
		//적치BED번호
		String szYD_STK_BED_NO        = null;
		//적치Bed입출고상태
		String szYD_STK_BED_WHIO_STAT = null;
		//TAKE_IN 재료번호
		String szTAKE_IN_STL_NO       = null;
		//적치 재료 매수
		String szYD_STK_BED_STL_SH    = null;
		//재료 매수
		int intMtlCnt                 = 0;
		//재료번호
		String[] szSTL_NO             = new String[5];
		//CARRY_IN 요구 구분
		String szCARRY_IN_REQ_GP      = "N";
		//전문 생성 일시
		String szDate                 = null;


		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//TAKE_IN 재료번호
			szTAKE_IN_STL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "TAKE_IN_STL_NO");
			if (szTAKE_IN_STL_NO.equals("")) {
				
				szMsg = "[전문 이상] TAKE_IN 재료번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치 재료 매수
			szYD_STK_BED_STL_SH = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_STL_SH");
			if (szYD_STK_BED_STL_SH.equals("")) {
				
				szMsg = "[전문 이상] 적치 재료 매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료 매수
			intMtlCnt = Integer.parseInt(szYD_STK_BED_STL_SH) + 1;
			//재료번호
			for(int Loop_i = intMtlCnt; Loop_i >= 1; Loop_i--) {
				
				if (Loop_i == 1)
					szSTL_NO[Loop_i] = szTAKE_IN_STL_NO;
				else
					szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO" + Loop_i);
				
			}
			//야드적치Bed입출고상태
			szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_WHIO_STAT");
			
			//====================================================================================
			//현재 테스트를 위해 마지막 재료 이면 CARRY_IN 요구 구분을 "Y"로 바꾼다.
			//추후 기준 설정
			if (intMtlCnt == 1) szCARRY_IN_REQ_GP = "Y";
			//====================================================================================
			
			//레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//적치단 초기화 데이터 편집
			//적치열구분
			recPara.setField("YD_STK_COL_GP",       szYD_EQP_ID);
			//적치BED번호
			recPara.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);
			//적치단번호
			recPara.setField("YD_STK_LYR_NO",       "00" + intMtlCnt);
			//재료번호
			recPara.setField("STL_NO",              "");
			//적치단 재료상태
			recPara.setField("YD_STK_LYR_MTL_STAT", "E");

//==========================================================================================                    
//          기준이기 때문에 클리어 되면 안됨
//          2009.09.25 권오창
//            
//			//야드적치단X축
//			recPara.setField("YD_STK_LYR_XAXIS",    "0");
//			//야드적치단Y축
//			recPara.setField("YD_STK_LYR_YAXIS",    "0");
//			//야드적치단Z축
//			recPara.setField("YD_STK_LYR_ZAXIS",    "0");
//==========================================================================================                    

			//수정자
			recPara.setField("MODIFIER",            szUser);
			
			//적치단 업데이트
			blnRtnVal = this.setStkLyr(recPara, 0);
			if (!blnRtnVal) return;
			
			//====================================================================================
			//생산통제-장입진행실적 송신 연결
			//추후 구현
			//====================================================================================
			
			//CARRY_IN 요구 구분이 "Y"이면 CARRY_IN 요구 전문 생성 및 전송
			if (szCARRY_IN_REQ_GP.equals("Y")) {
				
				//레코드 생성
				recPara = JDTORecordFactory.getInstance().create();
				//전문 발생 일시
				szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
				//JMS TC CODE
				recPara.setField("JMS_TC_CD",              "YDYDJ231");
				//발생 일시
				recPara.setField("JMS_TC_CREATE_DDTT",     szDate);
				//설비ID 
				recPara.setField("YD_EQP_ID",              szYD_EQP_ID);
				//야드적치Bed입출고상태
				recPara.setField("szYD_STK_BED_WHIO_STAT", szYD_STK_BED_WHIO_STAT);
				//적치BED번호
				recPara.setField("YD_STK_BED_NO",          szYD_STK_BED_NO);
				//=====================================================================
				//목표행선구분(wan)-어떻게 보낼 것인가
				recPara.setField("YD_AIM_RT_GP",           "C1");
				//=====================================================================
				
				//전문 송신
				ydDelegate.sendMsg(recPara);
				szMsg = "C연주Take-In재료등록 후 C연주 C열연 보급 Lot 편성 송신 완료!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}
		} catch(Exception e) {
			szMsg = "C연주Take-In재료등록 예외발생, 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "C연주Take-In재료등록(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} //end of mCCCTakeInCmpl
	
	
	/**
	 * 오퍼레이션명 : C연주 C열연 보급 Lot 편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCCCCHRSupplyLotGp (JDTORecord msgRecord) throws JDTOException  {
		
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
		String szMethodName    = "mCCCCHRSupplyLotGp";
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


		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//스케줄코드
			System.out.println("ok session1");
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
			if (szYD_AIM_RT_GP.equals("")) {
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치Bed입출고상태
			szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_WHIO_STAT");
//			if (szYD_STK_BED_WHIO_STAT.equals("")) {
//				
//				szMsg = "[전문 이상] 야드적치Bed입출고상태가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD  = "AASHPUUM";
			//=================================================================================
			
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//보급 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetCHRSupplyLotGp(szYD_SCH_CD, szYD_AIM_RT_GP, rsResult);
			if (!blnRtnVal) return;
			
			//지시 재료 매수가 Carry In 재료기준매수(현재4매)보다 작으면 지시 재료 매수를 재료기준매수에 대입한다.
			if (intCarryInCnt > rsResult.size()) intCarryInCnt = rsResult.size();
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//Carry In 재료기준매수(현재4매)만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intCarryInCnt; Loop_i++) {
				
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
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYDJ241");
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
			szMsg = "C연주 C열연 보급 Lot 편성 후 CARRY_IN 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e) {
			szMsg = "C연주 C열연 보급 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "C연주 C열연 보급 Lot 편성(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} //end of mCCCCHRSupplyLotGp
	
	
	/**
	 * 오퍼레이션명 : C연주 M-Scarfing 보급 Lot 편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCCCMScarfingSupplyLotGp (JDTORecord msgRecord) throws JDTOException  {
		
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
		String szMethodName    = "mCCCMScarfingSupplyLotGp";
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


		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//스케줄코드
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
//			//목표행선구분
//			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
//			if (szYD_AIM_RT_GP.equals("")) {
//				
//				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
			//적치Bed입출고상태
			szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_WHIO_STAT");
//			if (szYD_STK_BED_WHIO_STAT.equals("")) {
//				
//				szMsg = "[전문 이상] 야드적치Bed입출고상태가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD  = "ACSHPUUM";
			//=================================================================================
			
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//보급 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetMScarfingSupplyLotGp(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
			//지시 재료 매수가 Carry In 재료기준매수(현재4매)보다 작으면 지시 재료 매수를 재료기준매수에 대입한다.
			if (intCarryInCnt > rsResult.size()) intCarryInCnt = rsResult.size();
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//Carry In 재료기준매수(현재4매)만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intCarryInCnt; Loop_i++) {
				
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
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYD9910");
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
			szMsg = "C연주 M-Scarfing 보급 Lot 편성 후 CARRY_IN 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e) {
			szMsg = "C연주 M-Scarfing 보급 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "C연주 M-Scarfing 보급 Lot 편성(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} //end of mCCCMScarfingSupplyLotGp
	
	
	
	/**
	 * 오퍼레이션명 : C연주 정정 보급 Lot 편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCCCSHEARSupplyLotGp (JDTORecord msgRecord) throws JDTOException  {
		
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
		String szMethodName    = "mCCCSHEARSupplyLotGp";
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


		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//스케줄코드
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
//			//목표행선구분
//			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
//			if (szYD_AIM_RT_GP.equals("")) {
//				
//				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
			//적치Bed입출고상태
			szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_WHIO_STAT");
//			if (szYD_STK_BED_WHIO_STAT.equals("")) {
//				
//				szMsg = "[전문 이상] 야드적치Bed입출고상태가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD  = "ACPBPUUM";
			//=================================================================================
			
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//보급 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetSHEARSupplyLotGp(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
			//지시 재료 매수가 Carry In 재료기준매수(현재4매)보다 작으면 지시 재료 매수를 재료기준매수에 대입한다.
			if (intCarryInCnt > rsResult.size()) intCarryInCnt = rsResult.size();
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//Carry In 재료기준매수(현재4매)만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intCarryInCnt; Loop_i++) {
				
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
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYD9910");
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
			szMsg = "C연주 정정 보급 Lot 편성 후 CARRY_IN 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e) {
			szMsg = "C연주 정정 보급 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "C연주 정정 보급 Lot 편성(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} //end of mCCCSHEARSupplyLotGp
	
	/**
	 * 오퍼레이션명 : C연주 소재이송 상차 LOT 편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCCCMtlFtmvCarLoadLotGp (JDTORecord msgRecord) throws JDTOException  {
		
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
		String szMethodName    = "mCCCMtlFtmvCarLoadLotGp";
		//사용자
		String szUser          = "SYSTEM";

		//전문 생성 일시
		String szDate             = null;

		//설비ID
		String szYD_EQP_ID         = null;
		//상차정지위치
		String szYD_CARLD_STOP_LOC = null;
		//차량사용구분
		String szYD_CAR_USE_GP     = null;
		//운송장비코드
		String szTRN_EQP_CD        = null;
		//재료품목
		String szYD_MTL_ITEM       = null;
		//목표행선구분
		String szYD_AIM_RT_GP      = null;
		//목표야드구분
		String szYD_AIM_YD_GP      = null;
		//목표동구분
		String szYD_AIM_BAY_GP     = null;
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


		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
			if (szYD_CARLD_STOP_LOC.equals("")) {
				
				szMsg = "[전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
			if (szYD_CAR_USE_GP.equals("") || !szYD_CAR_USE_GP.equals("L")) {
				
				szMsg = "[전문 이상] 차량사용구분(" + szYD_CAR_USE_GP + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//운송장비코드
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
			if (szTRN_EQP_CD.equals("")) {
				
				szMsg = "[전문 이상] 운송장비코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료품목
			szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MTL_ITEM");
			if (szYD_MTL_ITEM.equals("")) {
				
				szMsg = "[전문 이상] 재료품목이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
			if (szYD_AIM_RT_GP.equals("")) {
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//목표야드구분
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_YD_GP");
			if (szYD_AIM_YD_GP.equals("")) {
				
				szMsg = "[전문 이상] 목표야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP");
			if (szYD_AIM_BAY_GP.equals("")) {
				
				szMsg = "[전문 이상] 목표동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
	
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD  = "ADSHPTUM";
			//=================================================================================
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//차량사양 Select
			blnRtnVal = chkGetCarSpec(szYD_EQP_ID, szYD_CAR_USE_GP, szTRN_EQP_CD, rsResult);
			if (!blnRtnVal) return;

			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//차량작업허용매수
			intYD_WRK_ALW_SH = ydDaoUtils.paraRecChkNullInt(recPara, "YD_WRK_ALW_SH");
			//차량작업허용중량
			lngYD_WRK_ALW_WT = ydDaoUtils.paraRecChkNullLong(recPara, "YD_WRK_ALW_WT");
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//상차 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetMtlFtmvCarLoadLotGp(msgRecord, rsResult);
			if (!blnRtnVal) return;			
			
			//이송 재료 매수가 차량 이송 가능 매수보다 작으면 이송 재료 매수를 차량 이송 가능 매수에 대입한다.
			if (intYD_WRK_ALW_SH > rsResult.size()) intYD_WRK_ALW_SH = rsResult.size();
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//차량 이송 가능 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intYD_WRK_ALW_SH; Loop_i++) {
				
				//대상 재료 중량 합계
				lngSumMtlWt = lngSumMtlWt + Long.parseLong(ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_WT"));
				//차량작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
				if (lngYD_WRK_ALW_WT < lngSumMtlWt) break;
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
			
			//전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYDJ244");
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
			
			//전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "C연주 소재이송 상차 LOT 편성 후 차량상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e) {
			szMsg = "C연주 소재이송 상차 LOT 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "C연주 소재이송 상차 LOT 편성(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} //end of mCCCMtlFtmvCarLoadLotGp
	
	
	
	/**
	 * 오퍼레이션명 : C연주 외판출하 상차 LOT 편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCCCOutPlDistCarLoadLotGp(JDTORecord msgRecord) throws JDTOException  {
		
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
		String szMethodName    = "mCCCOutPlDistCarLoadLotGp";
		//사용자
		String szUser          = "SYSTEM";

		//전문 생성 일시
		String szDate             = null;

		//설비ID
		String szYD_EQP_ID         = null;
		//상차정지위치
		String szYD_CARLD_STOP_LOC = null;
		//차량사용구분
		String szYD_CAR_USE_GP     = null;
		//차량번호
		String szCAR_NO            = null;
		//카드번호
		String szCARD_NO            = null;
		//재료품목
		String szYD_MTL_ITEM       = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//차량이송재료매수
		int intYD_CARLD_SH         = 0;
		//차량작업허용매수
		int intYD_WRK_ALW_SH       = 1;
//		//대상 재료 중량 합계
//		long lngSumMtlWt           = 0;
//		//차량작업허용중량
//		long lngYD_WRK_ALW_WT      = 0;


		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
			if (szYD_CARLD_STOP_LOC.equals("")) {
				
				szMsg = "[전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
			if (szYD_CAR_USE_GP.equals("") || !szYD_CAR_USE_GP.equals("G")) {
				
				szMsg = "[전문 이상] 차량사용구분(" + szYD_CAR_USE_GP + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//차량번호
			szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
			if (szCAR_NO.equals("")) {
				
				szMsg = "[전문 이상] 차량번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			//PIDEV 
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "YdSimSeEJBBean => mCCCOutPlDistCarLoadLotGp", "APPPI0", "*", "*");
			
//			if( "N".equals(sApplyYnPI) ) {
//				
//				//카드번호
//				szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");
//				if (szCARD_NO.equals("")) {
//					
//					szMsg = "[전문 이상] 카드번호가 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return;
//					
//				}
//				
//			}
			

			//재료품목
			szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MTL_ITEM");
			if (szYD_MTL_ITEM.equals("")) {
				
				szMsg = "[전문 이상] 재료품목이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
	
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD  = "ADSGTRUM";
			//=================================================================================
			
//			//리턴 recordSet 생성
//			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//			//차량사양 Select
//			blnRtnVal = chkGetCarSpec(szYD_EQP_ID, szYD_CAR_USE_GP, szTRN_EQP_CD, rsResult);
//			if (!blnRtnVal) return;
//
//			//레코드 추출
//			rsResult.first();
//			recPara = rsResult.getRecord();
//			
//			//차량작업허용매수
//			intYD_WRK_ALW_SH = ydDaoUtils.paraRecChkNullInt(recPara, "YD_WRK_ALW_SH");
//			//차량작업허용중량
//			lngYD_WRK_ALW_WT = ydDaoUtils.paraRecChkNullLong(recPara, "YD_WRK_ALW_WT");
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//상차 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetOutPlDistCarLoadLotGp(msgRecord, rsResult);
			if (!blnRtnVal) return;			
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//차량 이송 가능 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intYD_WRK_ALW_SH; Loop_i++) {
				
//				//대상 재료 중량 합계
//				lngSumMtlWt = lngSumMtlWt + Long.parseLong(ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_WT"));
//				//차량작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
//				if (lngYD_WRK_ALW_WT < lngSumMtlWt) break;
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
			
			//전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYDJ244");
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
			
			//전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "C연주 외판출하 상차 LOT 편성 후 차량상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e) {
			szMsg = "C연주 외판출하 상차 LOT 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "C연주 외판출하 상차 LOT 편성(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} //end of mCCCOutPlDistCarLoadLotGp
	
	
	/**
	 * 오퍼레이션명 : C연주 대차 상차 LOT 편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCCCTcarLoadLotGp (JDTORecord msgRecord) throws JDTOException  {
		
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
		String szMethodName    = "mCCCTcarLoadLotGp";
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
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
			if (szYD_CARLD_STOP_LOC.equals("")) {
				
				szMsg = "[전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료품목
			szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MTL_ITEM");
			if (szYD_MTL_ITEM.equals("")) {
				
				szMsg = "[전문 이상] 재료품목이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
			if (szYD_AIM_RT_GP.equals("")) {
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP");
			if (szYD_AIM_BAY_GP.equals("")) {
				
				szMsg = "[전문 이상] 목표동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
	
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD  = "ABSH01MM";
			//=================================================================================

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//상차 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetMtlFtmvCarLoadLotGp(msgRecord, rsResult);
			if (!blnRtnVal) return;			
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			
			//첫 레코드로 커서이동
			rsResult.first();
			
			//대상 소재 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				
				//레코드 추출
				recPara = rsResult.getRecord();
				//대상 재료 중량 합계
				lngSumMtlWt = lngSumMtlWt + Long.parseLong(ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_WT"));
				//대차작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
				if (lngYD_WRK_ALW_WT < lngSumMtlWt) break;
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
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYDJ249");
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
			
			//전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "C연주 대차 상차 LOT 편성 후 차량상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e) {
			szMsg = "C연주 대차 상차 LOT 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "C연주 대차 상차 LOT 편성(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} //end of mCCCTcarLoadLotGp
	
	
	/**
	 * 오퍼레이션명 : C연주 차량상차 작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCCCCarLoadDmd(JDTORecord msgRecord)throws JDTOException  {
		
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
		String szMethodName    = "mCCCCarLoadDmd";
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
			return;
		
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
			if (szYD_SCH_CD.equals("")) {
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if (intMtlCnt == 0) {
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}

			//재료번호
			szSTL_NO          = new String[intMtlCnt + 1];
			//권상모음순서
			szYD_UP_COLL_SEQ  = new String[intMtlCnt + 1];
			
			
			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;	
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
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
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
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
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal) return;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
			}	
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
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
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
//			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
//			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				if (!blnRtnVal) return;
				
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
				
				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "C연주 차량상차 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg = "C연주 차량상차 작업요구 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mCCCCarLoadDmd()
	
	
	
	/**
	 * 오퍼레이션명 : C연주 대차상차 작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCCCTcarLoadDmd(JDTORecord msgRecord)throws JDTOException  {
		
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
		String szMethodName    = "mCCCTcarLoadDmd";
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
			return;
		
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
			if (szYD_SCH_CD.equals("")) {
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if (intMtlCnt == 0) {
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}

			//재료번호
			szSTL_NO          = new String[intMtlCnt + 1];
			//권상모음순서
			szYD_UP_COLL_SEQ  = new String[intMtlCnt + 1];
			
			
			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;	
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
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
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
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
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal) return;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
			}	
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
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
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
//			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
//			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				if (!blnRtnVal) return;
				
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
				
				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "C연주 대차상차 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg = "C연주 대차상차 작업요구 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mCCCTcarLoadDmd()
	
	
	
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
			recPara.setField("YD_EQP_ID",    szEqpId);
			//차량사용구분
			recPara.setField("YD_CAR_USE_GP", szCarUseGp);
			//운송장비코드
			recPara.setField("TRN_EQP_CD", szTrnEqpCd);
			
			//설비 테이블 조회
			intRtnVal = ydCarSpecDao.getYdCarspec(recPara, rsResult, 1);

			//리턴값 메세지처리
			if (intRtnVal > 1) {
				
				szMsg = "설비ID("      + szEqpId    + ") " +
				        "운송장비코드(" + szTrnEqpCd + ")에 대한 설비 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "설비ID("      + szEqpId    + ") " +
		                "운송장비코드(" + szTrnEqpCd + ")에 대한 설비 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
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
		} catch(Exception e) {
			szMsg = "차량사양 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetCarSpec
	
	
	
	
	/**
	 * 오퍼레이션명 : C연주C열연보급Carry-In작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCCCCHRGpCarryInDmd(JDTORecord msgRecord)throws JDTOException  {
		
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
		String szMethodName    = "mCCCCHRGpCarryInDmd";
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
		String[] szSTL_NO          = new String[5];
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = new String[5];
		//적치단
		String[] szYD_STK_LYR_NO   = new String[5];
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
			return;
		
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
			if (szYD_SCH_CD.equals("")) {
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARRY_IN_SH");
			if (intMtlCnt == 0) {
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}

			//재료번호, 권상모음순서, 적치단(테스트용)
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;	
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
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
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
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
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal) return;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
			}	
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
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
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
//			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
//			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				if (!blnRtnVal) return;
				
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
				
				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "C연주C열연보급Carry-In작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg = "C연주C열연보급Carry-In작업요구 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mCCCCHRGpCarryInDmd()
	
	/**
	 * 오퍼레이션명 : C연주 M-Scarfing 보급Carry-In작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCCCMScarfingGpCarryInDmd(JDTORecord msgRecord)throws JDTOException  {
		
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
		String szMethodName    = "mCCCMScarfingGpCarryInDmd";
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
		String[] szSTL_NO          = new String[5];
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = new String[5];
		//적치단
		String[] szYD_STK_LYR_NO   = new String[5];
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
			return;
		
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
			if (szYD_SCH_CD.equals("")) {
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARRY_IN_SH");
			if (intMtlCnt == 0) {
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}

			//재료번호, 권상모음순서, 적치단(테스트용)
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;	
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
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
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
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
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal) return;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
			}	
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
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
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				if (!blnRtnVal) return;
				
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
				
				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "C연주 M-Scarfing 보급Carry-In작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg = "C연주 M-Scarfing 보급Carry-In작업요구 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mCCCMScarfingGpCarryInDmd()
	
	
	/**
	 * 오퍼레이션명 : C연주 정정 보급Carry-In작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCCCSHEARGpCarryInDmd(JDTORecord msgRecord)throws JDTOException  {
		
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
		String szMethodName    = "mCCCSHEARGpCarryInDmd";
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
		String[] szSTL_NO          = new String[5];
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = new String[5];
		//적치단
		String[] szYD_STK_LYR_NO   = new String[5];
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
			return;
		
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
			if (szYD_SCH_CD.equals("")) {
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARRY_IN_SH");
			if (intMtlCnt == 0) {
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}

			//재료번호, 권상모음순서, 적치단(테스트용)
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;	
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
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
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
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
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal) return;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
			}	
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
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
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				if (!blnRtnVal) return;
				
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
				
				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "C연주 정정 보급Carry-In작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg = "C연주 정정 보급Carry-In작업요구 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mCCCSHEARGpCarryInDmd()
	
	
	
	/**
	 * 오퍼레이션명 : C열연 압연분기 Line-Off 작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCHRMillBrLineOffDmd(JDTORecord msgRecord)throws JDTOException  {
		
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao    = new YdStkLyrDao();
		//작업예약 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils      = new YdDaoUtils();
		//DELEGATE
		YdDelegate ydDelegate = new YdDelegate();
		//공용 METHOD
		YdUtils ydutils            = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "mCHRMillBrLineOffDmd";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord    recPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		
		//설비ID(열구분과 동일)
		String szYD_EQP_ID         = null;
		//재료번호
		String szSTL_NO            = null;
		//적치BED번호
		String szYD_STK_BED_NO     = null;
		//스케줄코드
		String szYD_SCH_CD            = null;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN      = null;
		//작업크레인
		String szYD_WRK_CRN           = null;
		//대체크레인
		String szYD_ALT_CRN           = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN        = null;
		//선택크레인
		String szCrn                  = null;
		//야드구분
		String szYD_GP                = null;
		//동구분
		String szYD_BAY_GP            = null;
		//작업예약ID
		String szYD_WBOOK_ID          = null;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if (szRcvTcCode == null) {
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try{
			
			
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}					
			//적치Bed번호
			szYD_STK_BED_NO     = ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치Bed번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료번호
			szSTL_NO   = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO");
			if (szSTL_NO.equals("")) {
				
				szMsg = "[전문 이상] 재료번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//=================================================================================
			//스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			//C열연코일소재야드(1)+D동(1)+코일소재(2)+컨베어(2)+입고(1)+좌우구분(1)
			szYD_SCH_CD  = "HDCMCVLM";
			//=================================================================================
			
			//저장품 테이블에 해당 재료번호의 데이터가 있는지 체크한다.
			blnRtnVal = this.chkStock(szSTL_NO);
			if (!blnRtnVal) return;
			
			//컨베어 BED SHIFT
			blnRtnVal = this.setShiftStkLyr(szYD_EQP_ID);
			if (!blnRtnVal) return;
			
			//조회결과 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//적치단정보 조회
			blnRtnVal = this.chkGetStkLyr(szYD_EQP_ID, szYD_STK_BED_NO, "001", rsResult);
			if (!blnRtnVal) return;
			
			//적치단정보 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//적치단 재료상태
			String szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT");
			//적치단 재료상태가 적치가능('E')가 아니면 에러 리턴
			if (!szYD_STK_LYR_MTL_STAT.equals("E")) {
				
				szMsg = "적치단 재료상태(" + szYD_STK_LYR_MTL_STAT + ") 적치가능 상태가 아닙니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//UPDATE 항목 record  생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//적치단 재료상태가 적치 가능이면 재료 등록
			//적치단 테이블 업데이트
			//적치열구분 = 설비ID
			recPara.setField("YD_STK_COL_GP", 	    szYD_EQP_ID);
			recPara.setField("YD_STK_BED_NO", 	    szYD_STK_BED_NO);
			recPara.setField("YD_STK_LYR_NO", 	    "001");
			recPara.setField("MODIFIER", 		    szUser);
			recPara.setField("YD_STK_LYR_MTL_STAT", "C");
			recPara.setField("STL_NO", 			    szSTL_NO);
			
			//업데이트 실행
			blnRtnVal = this.setStkLyr(recPara, 0);
			if (!blnRtnVal) return;
			
			
			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				
			}

			//다른 작업예약에 재료가 등록되어있는지 체크한다.
			blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO);
			if (!blnRtnVal) return;

			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
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
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		szYD_GP);
			recPara.setField("YD_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("REGISTER", 	szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//작업예약재료 정보 SET
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);
			recPara.setField("STL_NO", 		  szSTL_NO);
			recPara.setField("YD_STK_LYR_NO", "001");
				
			//작업예약재료 테이블에 등록한다.
			intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
			
			if (intRtnVal < 1) {
				szMsg = "작업예약재료 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
		} catch (Exception e) {
		
			szMsg = "C열연 압연분기 Line-Off 작업요구 처리중 ERROR : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}	// end try catch
		
		
		szMsg = "C열연 압연분기 Line-Off 작업요구 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
	
	} //end of mCHRMillBrLineOffDmd
	
	
	/**
	 * 오퍼레이션명 : C열연 수냉탱크 Line-Off 요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCHRWtClTnkLineOffDmd(JDTORecord msgRecord)throws JDTOException  {
		
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao    = new YdStkLyrDao();
		//작업예약 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils      = new YdDaoUtils();
		//DELEGATE
		YdDelegate ydDelegate = new YdDelegate();
		//공용 METHOD
		YdUtils ydutils            = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "mCHRWtClTnkLineOffDmd";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord    recPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		
		//설비ID(열구분과 동일)
		String szYD_EQP_ID         = null;
		//재료번호
		String[] szSTL_NO            = null;
		//적치BED번호
		String[] szYD_STK_BED_NO      = null;
		//스케줄코드
		String szYD_SCH_CD            = null;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN      = null;
		//작업크레인
		String szYD_WRK_CRN           = null;
		//대체크레인
		String szYD_ALT_CRN           = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN        = null;
		//선택크레인
		String szCrn                  = null;
		//야드구분
		String szYD_GP                = null;
		//동구분
		String szYD_BAY_GP            = null;
		//작업예약ID
		String szYD_WBOOK_ID          = null;
		//재료갯수
		int intMtlCnt                 = 0;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if (szRcvTcCode == null) {
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try{
			
			
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}			
//			//적치Bed번호
//			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_BED_NO");
//			if (szYD_STK_BED_NO.equals("")) {
//				
//				szMsg = "[전문 이상] 적치Bed번호가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
//			//재료번호
//			szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO");
//			if (szSTL_NO.equals("")) {
//				
//				szMsg = "[전문 이상] 재료번호가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
			
			//=================================================================================
			//스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			//C열연코일소재야드(1)+G동(1)+코일소재(2)+수냉탱크(2)+입고(1)+좌우구분(1)
			szYD_SCH_CD  = "HGCMWTLM";
			//=================================================================================
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//해당 수냉탱크에 적치된 재료를 읽어온다.
			blnRtnVal = this.chkGetStkLyr(szYD_EQP_ID, rsResult);
			if (!blnRtnVal) return;
			
			//재료갯수
			intMtlCnt = rsResult.size();
			//재료번호 배열 생성
			szSTL_NO        = new String[intMtlCnt + 1];
			//적치BED 배열 생성
			szYD_STK_BED_NO = new String[intMtlCnt + 1];
			rsResult.first();
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				recPara = rsResult.getRecord();
				//적치중이 아니면 다음 레코드로
				if (!ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT").equals("C")) {
					rsResult.next();
					continue;
				}
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				//적치BED번호
				szYD_STK_BED_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				
				//저장품 테이블에 해당 재료번호의 데이터가 있는지 체크한다.
				blnRtnVal = this.chkStock(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
				//다음 레코드로
				rsResult.next();

			}
			
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				
			}

			

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
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
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		szYD_GP);
			recPara.setField("YD_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("REGISTER", 	szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//작업예약재료 정보 SET
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
			recPara.setField("REGISTER", 	  szUser);
			recPara.setField("YD_STK_LYR_NO", "001");
			rsResult.first();
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO[Loop_i]);
				recPara.setField("STL_NO",        szSTL_NO[Loop_i]);
				
				//작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
				
				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
			
		} catch (Exception e) {
		
			szMsg = "C열연 수냉탱크 Line-Off 요구 처리중 ERROR : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}	// end try catch
		
		
		szMsg = "C열연 수냉탱크 Line-Off 요구(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
	
	} //end of mCHRWtClTnkLineOffDmd
	
	
	/**
	 * 오퍼레이션명 : C열연 정정출측 Line-Off 요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCHRShearOutLineOffDmd(JDTORecord msgRecord)throws JDTOException  {
		
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao    = new YdStkLyrDao();
		//작업예약 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils      = new YdDaoUtils();
		//DELEGATE
		YdDelegate ydDelegate = new YdDelegate();
		//공용 METHOD
		YdUtils ydutils            = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "mCHRShearOutLineOffDmd";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord    recPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		
		//설비ID(열구분과 동일)
		String szYD_EQP_ID         = null;
		//재료번호
		String szSTL_NO            = null;
		//적치BED번호
		String szYD_STK_BED_NO     = null;
		//스케줄코드
		String szYD_SCH_CD            = null;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN      = null;
		//작업크레인
		String szYD_WRK_CRN           = null;
		//대체크레인
		String szYD_ALT_CRN           = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN        = null;
		//선택크레인
		String szCrn                  = null;
		//야드구분
		String szYD_GP                = null;
		//동구분
		String szYD_BAY_GP            = null;
		//작업예약ID
		String szYD_WBOOK_ID          = null;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if (szRcvTcCode == null) {
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try{
			
			
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}			
			//적치Bed번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[전문 이상] 적치Bed번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//재료번호
			szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO");
			if (szSTL_NO.equals("")) {
				
				szMsg = "[전문 이상] 재료번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//=================================================================================
			//스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			//C열연코일소재야드(1)+E동(1)+코일소재(2)+SPM2출측(2)+입고(1)+좌우구분(1)
			szYD_SCH_CD  = "HECMDDLM";
			//=================================================================================
			
			//저장품 테이블에 해당 재료번호의 데이터가 있는지 체크한다.
			blnRtnVal = this.chkStock(szSTL_NO);
			if (!blnRtnVal) return;
			
			//컨베어 BED SHIFT
			blnRtnVal = this.setShiftStkLyr(szYD_EQP_ID);
			if (!blnRtnVal) return;
			
			//조회결과 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//적치단정보 조회
			blnRtnVal = this.chkGetStkLyr(szYD_EQP_ID, szYD_STK_BED_NO, "001", rsResult);
			if (!blnRtnVal) return;
			
			//적치단정보 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//적치단 재료상태
			String szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT");
			//적치단 재료상태가 적치가능('E')가 아니면 에러 리턴
			if (!szYD_STK_LYR_MTL_STAT.equals("E")) {
				
				szMsg = "적치단 재료상태(" + szYD_STK_LYR_MTL_STAT + ") 적치가능 상태가 아닙니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//UPDATE 항목 record  생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//적치단 재료상태가 적치 가능이면 재료 등록
			//적치단 테이블 업데이트
			//적치열구분 = 설비ID
			recPara.setField("YD_STK_COL_GP", 	    szYD_EQP_ID);
			recPara.setField("YD_STK_BED_NO", 	    szYD_STK_BED_NO);
			recPara.setField("YD_STK_LYR_NO", 	    "001");
			recPara.setField("MODIFIER", 		    szUser);
			recPara.setField("YD_STK_LYR_MTL_STAT", "C");
			recPara.setField("STL_NO", 			    szSTL_NO);
			
			//업데이트 실행
			blnRtnVal = this.setStkLyr(recPara, 0);
			if (!blnRtnVal) return;
			
			
			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				
			}

			//다른 작업예약에 재료가 등록되어있는지 체크한다.
			blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO);
			if (!blnRtnVal) return;

			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
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
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		szYD_GP);
			recPara.setField("YD_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("REGISTER", 	szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//작업예약재료 정보 SET
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);
			recPara.setField("STL_NO", 		  szSTL_NO);
			recPara.setField("YD_STK_LYR_NO", "001");
				
			//작업예약재료 테이블에 등록한다.
			intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
			
			if (intRtnVal < 1) {
				szMsg = "작업예약재료 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
		} catch (Exception e) {
		
			szMsg = "C열연 정정출측 Line-Off 요구 처리중 ERROR : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}	// end try catch
		
		
		szMsg = "C열연 정정출측 Line-Off 요구(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
	
	} //end of mCHRShearOutLineOffDmd
	
	
	/**
	 * 오퍼레이션명 : C열연 수냉탱크 보급 Lot 편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCHRWtClTnkSupplyLotGp (JDTORecord msgRecord) throws JDTOException  {
		
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
		String szMethodName    = "mCHRWtClTnkSupplyLotGp";
		//사용자
		String szUser          = "SYSTEM";
		
		//Line_In 재료 기준 매수
		int intLineInCnt         = 0;
		//전문 생성 일시
		String szDate             = null;

		//설비ID
		String szYD_EQP_ID             = null;
		//목표행선구분
		String szYD_AIM_RT_GP          = null;
//		//적치BED번호
//		String szYD_STK_BED_NO         = null;
//		//야드적치Bed입출고상태
//		String szYD_STK_BED_WHIO_STAT  = null;
		//스케줄코드
		String szYD_SCH_CD             = null;


		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//스케줄코드
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
			if (szYD_AIM_RT_GP.equals("")) {
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치Bed입출고상태
//			szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_WHIO_STAT");
//			if (szYD_STK_BED_WHIO_STAT.equals("")) {
//				
//				szMsg = "[전문 이상] 야드적치Bed입출고상태가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
//			//적치BED번호
//			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
//			if (szYD_STK_BED_NO.equals("")) {
//				
//				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD  = "HGCMWTUM";
			//=================================================================================
			
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//수냉탱크 Bed수 Select
			blnRtnVal = this.chkGetStkLyr(szYD_EQP_ID, rsResult);
			if (!blnRtnVal) return;
			
			//수냉탱크 Line In 롤수(수냉탱크 Bed수)
			intLineInCnt = rsResult.size();
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//보급 Lot 편성 대상 재료 Select
			blnRtnVal = this.chkGetWtClTnkSupplyLotGp(msgRecord, rsResult);
			if (!blnRtnVal) return;
			
			//지시 재료 롤수가 수냉탱크 Bed수 보다 작으면 지시재료 롤수를 수냉탱크 Line In 롤수에 대입한다.
			if (intLineInCnt > rsResult.size()) intLineInCnt = rsResult.size();
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//Line In 재료기준매수(현재4매)만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intLineInCnt; Loop_i++) {
				
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
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYDJ241");
			//발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			//스케줄코드
			recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
			//LINE_IN 재료매수
			recOutPara.setField("YD_LINE_IN_SH",     "" + intLineInCnt);
			//적치열구분
			recOutPara.setField("YD_STK_COL_GP",      szYD_EQP_ID);
			
			//전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "C열연 수냉탱크 보급 Lot 편성 후 C열연 수냉탱크 Line_in 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e) {
			szMsg = "C열연 수냉탱크 보급 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "C열연 수냉탱크 보급 Lot 편성(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} //end of mCHRWtClTnkSupplyLotGp
	
	
	/**
	 * 오퍼레이션명 : C열연 수냉탱크 Line-In 요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCHRWtClTnkLineInDmd(JDTORecord msgRecord)throws JDTOException  {
		
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
		String szMethodName    = "mCHRWtClTnkLineInDmd";
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
			return;
		
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
			if (szYD_SCH_CD.equals("")) {
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
//			//적치BED번호
//			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
//			if (szYD_STK_BED_NO.equals("")) {
//				
//				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LINE_IN_SH");
			if (intMtlCnt == 0) {
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//재료번호
			szSTL_NO          = new String[intMtlCnt + 1];
			//권상모음순서
			szYD_UP_COLL_SEQ  = new String[intMtlCnt + 1];

			//재료번호, 권상모음순서, 적치단(테스트용)
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;	
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
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
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
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
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
			}	
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
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
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				if (!blnRtnVal) return;
				
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
				
				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "C열연 수냉탱크 Line-In 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg = "C열연 수냉탱크 Line-In 요구 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mCHRWtClTnkLineInDmd()
	
	
	
	/**
	 * 오퍼레이션명 : C열연 정정입측 보급Lot 편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCHRShearInSupplyLotGp (JDTORecord msgRecord) throws JDTOException  {
		
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
		String szMethodName    = "mCHRShearInSupplyLotGp";
		//사용자
		String szUser          = "SYSTEM";
		
		//Line_In 재료 기준 매수
		int intLineInCnt         = 1;
		//전문 생성 일시
		String szDate             = null;

		//설비ID
		String szYD_EQP_ID             = null;
		//목표행선구분
		String szYD_AIM_RT_GP          = null;
//		//적치BED번호
//		String szYD_STK_BED_NO         = null;
//		//야드적치Bed입출고상태
//		String szYD_STK_BED_WHIO_STAT  = null;
		//스케줄코드
		String szYD_SCH_CD             = null;
		//공정구분
		String szPROC_GP               = null;

		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//스케줄코드
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
			if (szYD_AIM_RT_GP.equals("")) {
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치Bed입출고상태
//			szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_WHIO_STAT");
//			if (szYD_STK_BED_WHIO_STAT.equals("")) {
//				
//				szMsg = "[전문 이상] 야드적치Bed입출고상태가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
//			//적치BED번호
//			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
//			if (szYD_STK_BED_NO.equals("")) {
//				
//				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD  = "HGCMFEUM";
			//=================================================================================
			

			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//보급 Lot 편성 대상 재료 Select
			blnRtnVal = this.chkGetShearInSupplyLotGp(msgRecord, rsResult);
			if (!blnRtnVal) return;
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			
			//커서 처음으로
			rsResult.first();
			
			
			//Line In 재료기준매수(현재1매)만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intLineInCnt; Loop_i++) {
				
				//레코드 추출
				recPara = rsResult.getRecord();
				//재료번호
				recOutPara.setField("STL_NO" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				//권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);

				//다음 레코드 추출
				rsResult.next();
				
			}
			
			//전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYDJ252");
			//발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			//스케줄코드
			recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
			//LINE_IN 재료매수
			recOutPara.setField("YD_LINE_IN_SH",     "" + intLineInCnt);
			//적치열구분
			recOutPara.setField("YD_STK_COL_GP",      szYD_EQP_ID);
			
			//전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "C열연 정정입측 보급Lot 편성 후C열연 정정입측 Line_in 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e) {
			szMsg = "C열연 정정입측 보급Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg = "C열연 정정입측 보급Lot 편성(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} //end of mCHRShearInSupplyLotGp
	
	/**
	 * 오퍼레이션명 : C열연 정정입측 Line-In 요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mCHRShearInLineInDmd(JDTORecord msgRecord)throws JDTOException  {
		
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
		String szMethodName    = "mCHRShearInLineInDmd";
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
			return;
		
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
			if (szYD_SCH_CD.equals("")) {
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
//			//적치BED번호
//			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
//			if (szYD_STK_BED_NO.equals("")) {
//				
//				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LINE_IN_SH");
			if (intMtlCnt == 0) {
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//재료번호
			szSTL_NO          = new String[intMtlCnt + 1];
			//권상모음순서
			szYD_UP_COLL_SEQ  = new String[intMtlCnt + 1];

			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;	
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return;
			
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
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
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
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
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
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) return;
				
			}	
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) return;
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
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				if (!blnRtnVal) return;
				
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
				
				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
		
		} catch(Exception e) {
			szMsg = "C열연 정정입측 Line-In 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg = "C열연 정정입측 Line-In 요구 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	} // end of mCHRShearInLineInDmd()
	
	
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : C열연 정정입측 보급Lot 편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  JDTORecord    recInPara 파라미터 레코드
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetShearInSupplyLotGp(JDTORecord recInPara, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetShearInSupplyLotGp";
		String szMsg          = null;
		//설비ID
		String szYD_EQP_ID    = null;
		//공정구분
		String szPROC_GP      = null;
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();

			//목표행선구분
			recPara.setField("YD_AIM_RT_GP",  ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_RT_GP"));
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID");
			//FROM 야드동
			recPara.setField("YD_STK_COL_GP", szYD_EQP_ID.substring(0, 2));
			
			//동구분을 공정구분으로 변환
			//E동->R(SPM2), G동->H(#1HFL), H동->K(#1HSL) 
			szPROC_GP = szYD_EQP_ID.substring(1,2);
			if      (szPROC_GP.equals("E")) szPROC_GP = "R";
			else if (szPROC_GP.equals("G")) szPROC_GP = "H";
			else if (szPROC_GP.equals("H")) szPROC_GP = "K";
			else {
				
				szMsg = "동구분(" + szPROC_GP + ")에는 정정라인이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;
				
			}
			//공정구분
			recPara.setField("PROC_GP", szPROC_GP);
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 19);
			
			//리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "C열연 정정입측 보급Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "C열연 정정입측 보급Lot 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "C열연 정정입측 보급Lot 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "C열연 정정입측 보급Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetShearInSupplyLotGp
	
	
	
	/**
	 * 오퍼레이션명 : 컨베어 BED SHIFT
	 *  
	 * @param  String        szYD_EQP_ID     설비ID
	 *         String        szYD_STK_BED_NO 적치BED번호
	 *         JDTORecordSet rsResult        결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean setShiftStkLyr(String szYD_EQP_ID)throws JDTOException  {
	
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		String szMsg        = null;
		String szMethodName = "setShiftStkLyr";
		int intRtnVal       = 0;
		boolean blnRtnVal   = false;
		
		JDTORecord recPara     = null;
		JDTORecordSet rsResult = null;
		
		try {
			//결과레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//해당 적치열의 모든 BED 조회
			blnRtnVal = this.chkGetStkLyr(szYD_EQP_ID, rsResult);
			if (!blnRtnVal) return blnRtnVal;
			
			rsResult.first();
			rsResult.next();
			//적치열의 BED수 만큼 루프를 돌아 적치 데이터를 쉬프트한다.
			for (int Loop_i = rsResult.size(); Loop_i > 1; Loop_i--) {
				
				recPara = rsResult.getRecord();
				recPara.setField("YD_STK_BED_NO", ydUtils.fillSpZr("" + Loop_i, 2, 0));
				
				blnRtnVal = this.setStkLyr(recPara, 0);
				if (!blnRtnVal) return blnRtnVal;
				
				rsResult.next();
			}
			recPara = JDTORecordFactory.getInstance().create();
			//해당 적치열의 01 BED 초기화
			//적치열
			recPara.setField("YD_STK_COL_GP",       szYD_EQP_ID);
			//적치BED
			recPara.setField("YD_STK_BED_NO",       "01");
			//적치단
			recPara.setField("YD_STK_LYR_NO",       "001");
			//재료번호
			recPara.setField("STL_NO",              "");
			//적치단 재료상태
			recPara.setField("YD_STK_LYR_MTL_STAT", "E");

//==========================================================================================                    
//          기준이기 때문에 클리어 되면 안됨
//          2009.09.25 권오창
//            
//			//야드적치단X축
//			recPara.setField("YD_STK_LYR_XAXIS",    "0");
//			//야드적치단Y축
//			recPara.setField("YD_STK_LYR_YAXIS",    "0");
//			//야드적치단Z축
//			recPara.setField("YD_STK_LYR_ZAXIS",    "0");
//==========================================================================================                    
			
			blnRtnVal = this.setStkLyr(recPara, 0);
			if (!blnRtnVal) return blnRtnVal;
			
		} catch(Exception e) {
			szMsg = "적치단 업데이트 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of setShiftStkLyr
	
	
	
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
			if (intRtnVal >= 1) {
				
				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				szMsg = "적치열구분("  + recPara.getFieldString("YD_STK_COL_GP") + ")," +
				        "적치BED번호(" + recPara.getFieldString("YD_STK_BED_NO") + ")," +
				        "적치단번호("  + recPara.getFieldString("YD_STK_LYR_NO") + ")" +
						" 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -1) {
				szMsg = "적치열구분("  + recPara.getFieldString("YD_STK_COL_GP") + ")," +
				        "적치BED번호(" + recPara.getFieldString("YD_STK_BED_NO") + ")," +
				        "적치단번호("  + recPara.getFieldString("YD_STK_LYR_NO") + ")" +
				        "로 적치단 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
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
		} catch(Exception e) {
			szMsg = "적치단 업데이트 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of setStkLyr
	
	
	
	/**
	 * 오퍼레이션명 : 재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szStlNo   재료번호
	 *         String        szMtlStat 적치단재료상태
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetStlStkLyr(String szStlNo, String szMtlStat, JDTORecordSet rsResult)throws JDTOException  {
		
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
			if (intRtnVal > 1) {
				
				szMsg = "재료번호("      + szStlNo   + ")," +
				        "적치단재료상태(" + szMtlStat + ")," +
				        " 에 대한 적치단 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {
				
				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				szMsg = "재료번호("      + szStlNo   + ")," +
		                "적치단재료상태(" + szMtlStat + ")," +
						" 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				szMsg = "재료번호("      + szStlNo   + ")," +
                        "적치단재료상태(" + szMtlStat + ")," +
				        "로 적치단 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				szMsg = "재료번호("      + szStlNo   + ")," +
                        "적치단재료상태(" + szMtlStat + ")," +
				        " 로 적치단 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetStlStkLyr
	
	
	
	
	/**
	 * 오퍼레이션명 : C연주 C열연 보급Lot 편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  String        szSchCd   스케줄코드
	 *         String        szAimRtGp 목표행선구분
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCHRSupplyLotGp(String szSchCd, String szAimRtGp, JDTORecordSet rsResult)throws JDTOException  {
		
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
		JDTORecord recPara        = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			//재료품목
			recPara.setField("YD_MTL_ITEM",  szSchCd.substring(2, 4));
			//목표행선구분
			recPara.setField("YD_AIM_RT_GP", szAimRtGp);
			//야드구분
			recPara.setField("YD_GP",        szSchCd.substring(0, 1));
			//동구분
			recPara.setField("YD_BAY_GP",    szSchCd.substring(1, 2));
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 14);
			
			//리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 C열연 보급Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 C열연 보급Lot 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 C열연 보급Lot 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "C연주 C열연 보급Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetCHRSupplyLotGp
	
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
			if (intRtnVal >= 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 M-Scarfing 보급Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 M-Scarfing 보급Lot 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 M-Scarfing 보급Lot 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "C연주 M-Scarfing 보급Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetScarfingSupplyLotGp
	
	
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
			recPara.setField("YD_MTL_ITEM",  szSchCd.substring(2, 4));
			//야드구분
			recPara.setField("YD_GP",        szSchCd.substring(0, 1));
			//동구분
			recPara.setField("YD_BAY_GP",    szSchCd.substring(1, 2));
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 15);
			
			//리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 정정 보급Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 정정 보급Lot 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 정정 보급Lot 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "C연주 정정 보급Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetSHEARSupplyLotGp
	
	
	/**
	 * 오퍼레이션명 : C열연 수냉탱크 LOT 편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  JDTORecord    recInPara 파라미터 레코드
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetWtClTnkSupplyLotGp(JDTORecord recInPara, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetWtClTnkSupplyLotGp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();

			//목표행선구분
			recPara.setField("YD_AIM_RT_GP",  ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_RT_GP"));

			//FROM 야드동
			recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID").substring(0, 2));
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 17);
			
			//리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "C열연 수냉탱크 LOT 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "C열연 수냉탱크 LOT 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "C열연 수냉탱크 LOT 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "C열연 수냉탱크 LOT 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetWtClTnkSupplyLotGp
	
	
	/**
	 * 오퍼레이션명 : C연주 소재이송 상차 LOT 편성 데이터 유무체크 및 데이터 반환
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
			if (intRtnVal >= 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "C연주 소재이송 상차 LOT 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "C연주 소재이송 상차 LOT 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "C연주 소재이송 상차 LOT 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "C연주 소재이송 상차 LOT 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetMtlFtmvCarLoadLotGp
	
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
			recPara.setField("YD_MTL_ITEM",   ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_ITEM"));
//			//목표행선구분
//			recPara.setField("YD_AIM_RT_GP",  ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_RT_GP"));
//			//목표야드구분
//			recPara.setField("YD_AIM_YD_GP",  ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_YD_GP"));
//			//목표동구분
//			recPara.setField("YD_AIM_BAY_GP", ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_BAY_GP"));
			//FROM 야드동
			recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recInPara, "YD_CARLD_STOP_LOC").substring(0, 2));
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 18);
			
			//리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "C연주 외판출하 상차 LOT 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "C연주 외판출하 상차 LOT 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "C연주 외판출하 상차 LOT 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "C연주 외판출하 상차 LOT 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetOutPlDistCarLoadLotGp
	
	
	/**
	 * 오퍼레이션명 : 적치단 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szStkColGp 적치열구분
	 *         JDTORecordSet rsResult   결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetStkLyr(String szStkColGp, JDTORecordSet rsResult)throws JDTOException  {
		
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		String szMsg        = null;
		String szMethodName = "chkGetStkLyr";
		int intRtnVal       = 0;
		boolean blnRtnVal   = false;
		JDTORecord recPara  = null;
		
		try {
			
			//조회 항목  record 생성
			recPara = JDTORecordFactory.getInstance().create();

			//조회 파라미터 레코드 set
			recPara.setField("YD_STK_COL_GP", 	szStkColGp);
			
			//적치단정보 조회
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 5);

			//리턴값 메세지처리
			if (intRtnVal >= 1) {
				
				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				szMsg = "적치열구분("  + szStkColGp + ")" +
						" 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				szMsg = "적치열구분("  + szStkColGp + ")" +
				        "로 적치단 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				szMsg = "적치열구분("  + szStkColGp + ")" +
				        " 로 적치단 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "적치단 유무체크 및 조회결과 데이터 반환 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetStkLyr
	
	/**
	 * 오퍼레이션명 : 적치단 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szStkColGp 적치열구분
	 *         String        szStkBedNo 적치BED번호
	 *         JDTORecordSet rsResult   결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetStkLyr(String szStkColGp, String szStkBedNo, JDTORecordSet rsResult)throws JDTOException  {
		
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		String szMsg        = null;
		String szMethodName = "chkGetStkLyr";
		int intRtnVal       = 0;
		boolean blnRtnVal   = false;
		JDTORecord recPara  = null;
		
		try {
			
			//조회 항목  record 생성
			recPara = JDTORecordFactory.getInstance().create();

			//조회 파라미터 레코드 set
			recPara.setField("YD_STK_COL_GP", 	szStkColGp);
			recPara.setField("YD_STK_BED_NO", 	szStkBedNo);
			
			//적치단정보 조회
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 0);

			//리턴값 메세지처리
			if (intRtnVal > 1) {
				
				szMsg = "적치열구분("  + szStkColGp + ")," +
				        "적치BED번호(" + szStkBedNo + ")" +
				        " 에 대한 적치단 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {
				
				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				szMsg = "적치열구분("  + szStkColGp + ")," +
				        "적치BED번호(" + szStkBedNo + ")" +
						" 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				szMsg = "적치열구분("  + szStkColGp + ")," +
				        "적치BED번호(" + szStkBedNo + ")" +
				        "로 적치단 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				szMsg = "적치열구분("  + szStkColGp + ")," +
				        "적치BED번호(" + szStkBedNo + ")" +
				        " 로 적치단 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "적치단 유무체크 및 조회결과 데이터 반환 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetStkLyr
	
	
	/**
	 * 오퍼레이션명 : 적치단 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szStkColGp 적치열구분
	 *         String        szStkBedNo 적치BED번호
	 *         String        szStkLyrNo 적치단번호
	 *         JDTORecordSet rsResult   결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetStkLyr(String szStkColGp, String szStkBedNo, String szStkLyrNo, JDTORecordSet rsResult)throws JDTOException  {
		
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		String szMsg        = null;
		String szMethodName = "chkGetStkLyr";
		int intRtnVal       = 0;
		boolean blnRtnVal   = false;
		JDTORecord recPara  = null;
		
		try {
			
			//조회 항목  record 생성
			recPara = JDTORecordFactory.getInstance().create();

			//조회 파라미터 레코드 set
			recPara.setField("YD_STK_COL_GP", 	szStkColGp);
			recPara.setField("YD_STK_BED_NO", 	szStkBedNo);
			recPara.setField("YD_STK_LYR_NO", 	szStkLyrNo);
			
			//적치단정보 조회
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 0);

			//리턴값 메세지처리
			if (intRtnVal > 1) {
				
				szMsg = "적치열구분("  + szStkColGp + ")," +
				        "적치BED번호(" + szStkBedNo + ")," +
				        "적치단번호("  + szStkLyrNo + ")" +
				        " 에 대한 적치단 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {
				
				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				szMsg = "적치열구분("  + szStkColGp + ")," +
				        "적치BED번호(" + szStkBedNo + ")," +
				        "적치단번호("  + szStkLyrNo + ")" +
						" 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				szMsg = "적치열구분("  + szStkColGp + ")," +
				        "적치BED번호(" + szStkBedNo + ")," +
				        "적치단번호("  + szStkLyrNo + ")" +
				        "로 적치단 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				szMsg = "적치열구분("  + szStkColGp + ")," +
				        "적치BED번호(" + szStkBedNo + ")," +
				        "적치단번호("  + szStkLyrNo + ")" +
				        " 로 적치단 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "적치단 유무체크 및 조회결과 데이터 반환 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetStkLyr

	
	
	/**
	 * 오퍼레이션명 : 대차이송재료 체크 및 데이터 반환
	 *  
	 * @param  String     szCarSchId 차량스케줄ID
	 * @return boolean    true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetTcarftmvmtl(String szTcarSchId, JDTORecordSet rsResult)throws JDTOException  {
		
		//대차이송재료 DAO
		YdTcarFtmvMtlDao ydTcarFtmvMtlDao = new YdTcarFtmvMtlDao();

		String szMsg              = null;
		String szMethodName       = "chkGetTcarftmvmtl";
		int intRtnVal             = 0;
		boolean blnRtnVal         = false;
		JDTORecord recPara        = null;
		
		try {
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//대차스케줄ID
			recPara.setField("YD_TCAR_SCH_ID", szTcarSchId);
			
			//대차이송재료 조회
			intRtnVal = ydTcarFtmvMtlDao.getYdTcarftmvmtl(recPara, rsResult, 1);

			//리턴값 메세지처리
			if (intRtnVal > 0) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "대차스케줄ID(" + szTcarSchId + ")에 대한 대차이송재료 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "대차스케줄ID(" + szTcarSchId + ")로 대차이송재료 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "대차스케줄ID(" + szTcarSchId + ")로 대차이송재료 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "대차이송재료 체크 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal = true;
		
	} //end of chkGetTcarftmvmtl
	
	
	/**
	 * 오퍼레이션명 : 차량이송재료 체크 및 데이터 반환
	 *  
	 * @param  String     szCarSchId 차량스케줄ID
	 * @return boolean    true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCarftmvmtl(String szCarSchId, JDTORecordSet rsResult)throws JDTOException  {
		
		//차량이송재료 DAO
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();

		String szMsg              = null;
		String szMethodName       = "chkGetCarftmvmtl";
		int intRtnVal             = 0;
		boolean blnRtnVal         = false;
		JDTORecord recPara        = null;
		
		try {
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//차량스케줄ID
			recPara.setField("YD_CAR_SCH_ID", szCarSchId);
			
			//차량이송재료 조회
			intRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recPara, rsResult, 1);

			//리턴값 메세지처리
			if (intRtnVal > 0) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "차량스케줄ID(" + szCarSchId + ")에 대한 차량이송재료 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "차량스케줄ID(" + szCarSchId + ")로 차량이송재료 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "차량스케줄ID(" + szCarSchId + ")로 차량이송재료 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "차량이송재료 체크 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal = true;
		
	} //end of chkGetCarftmvmtl
	
	
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
			if (intRtnVal > 1) {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "스케줄기준 체크 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal = true;
		
	} //end of chkGetSchRule
	
	
	/**
	 * 오퍼레이션명 : 대차스케줄 유무 체크
	 *  
	 * @param  String     szTcarSchID 대차스케줄ID
	 * @return boolean    true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkTcarSch(String szTcarSchID)throws JDTOException  {
		
		//대차스케줄 DAO
		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();

		String szMsg           = null;
		String szMethodName    = "chkTcarSch";
		int intRtnVal          = 0;
		boolean blnRtnVal      = false;
		
		JDTORecord recPara     = null;
		JDTORecordSet rsResult = null;
		
		try {
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//대차스케줄ID
			recPara.setField("YD_TCAR_SCH_ID", szTcarSchID);
			
			//대차스케줄ID로 대차 스케줄 테이블 조회
			intRtnVal = ydTcarSchDao.getYdTcarsch(recPara, rsResult, 0);

			//리턴값 메세지처리
			if (intRtnVal > 1) {
				
				szMsg = "대차스케줄ID(" + szTcarSchID + ")에 대한 대차스케줄 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "대차스케줄ID(" + szTcarSchID + ")에 대한 대차스케줄 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "대차스케줄ID(" + szTcarSchID + ")로 대차스케줄 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "대차스케줄ID(" + szTcarSchID + ")로 대차스케줄 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "대차스케줄 유무 체크 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal = true;
		
	} //end of chkTcarSch
	
	
	
	/**
	 * 오퍼레이션명 : 차량스케줄 유무 체크
	 *  
	 * @param  String     szCarSchID 차량스케줄ID
	 * @return boolean    true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkCarSch(String szCarSchID)throws JDTOException  {
		
		//차량스케줄 DAO
		YdCarSchDao ydCarSchDao = new YdCarSchDao();

		String szMsg           = null;
		String szMethodName    = "chkCarSch";
		int intRtnVal          = 0;
		boolean blnRtnVal      = false;
		
		JDTORecord recPara     = null;
		JDTORecordSet rsResult = null;
		
		try {
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//차량스케줄ID
			recPara.setField("YD_CAR_SCH_ID", szCarSchID);
			
			//차량스케줄ID로 차량스케줄 테이블 조회
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult, 0);

			//리턴값 메세지처리
			if (intRtnVal > 1) {
				
				szMsg = "차량스케줄ID(" + szCarSchID + ")에 대한 차량스케줄 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "차량스케줄ID(" + szCarSchID + ")에 대한 차량스케줄 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "차량스케줄ID(" + szCarSchID + ")로 차량스케줄 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "차량스케줄ID(" + szCarSchID + ")로 차량스케줄 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "차량스케줄 유무 체크 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
		
	} //end of chkCarSch
	
	
	/**
	 * 오퍼레이션명 : 차량스케줄 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szCarSchID 차량스케줄ID
	 *         JDTORecordSet rsResult   결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCarSch(String szCarSchID, JDTORecordSet rsResult)throws JDTOException  {
		
		//차량스케줄 DAO
		YdCarSchDao ydCarSchDao = new YdCarSchDao();

		String szMsg        = null;
		String szMethodName = "chkGetCarSch";
		int intRtnVal       = 0;
		boolean blnRtnVal   = false;
		JDTORecord recPara  = null;
		
		try {
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//차량스케줄ID
			recPara.setField("YD_CAR_SCH_ID", szCarSchID);
			
			//차량스케줄ID로 차량스케줄 테이블 조회
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult, 0);

			//리턴값 메세지처리
			if (intRtnVal > 1) {
				
				szMsg = "차량스케줄ID(" + szCarSchID + ")에 대한 차량스케줄 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {
				
				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "차량스케줄ID(" + szCarSchID + ")에 대한 차량스케줄 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "차량스케줄ID(" + szCarSchID + ")로 차량스케줄 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "차량스케줄ID(" + szCarSchID + ")로 차량스케줄 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "차량스케줄 유무체크 및 조회결과 데이터 반환 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetCarSch
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 차량 정보 체크
	 *  
	 * @param  JDTORecord recCarSch 차량스케줄 레코드
	 *         JDTORecord recMsg    전문 레코드
	 * @return boolean    true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkCarInfo(JDTORecord recCarSch, JDTORecord recMsg)throws JDTOException  {

		String szMsg        = null;
		String szMethodName = "chkCarInfo";
		boolean blnRtnVal   = false;
		
		//차량 사용 구분
		String szYD_CAR_USE_GP = null;
		//운송장비코드
		String szTRN_EQP_CD    = null;
		//차량번호
		String szCAR_NO        = null;
		//카드번호
		String szCARD_NO       = null;
		
		
		try {
			//운송장비코드
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(recMsg, "TRN_EQP_CD");
			//차량번호
			szCAR_NO     = ydDaoUtils.paraRecChkNull(recMsg, "CAR_NO");
			//카드번호
			szCARD_NO    = ydDaoUtils.paraRecChkNull(recMsg, "CARD_NO");

			
			//차량 사용 구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recCarSch, "YD_CAR_USE_GP");
	
			//제품 출하("G")이면 차량번호와 카드번호 체크
			if (szYD_CAR_USE_GP.equals("G")) {
				
				//차량번호 비교 후 다르면 에러 처리후 리턴
				if (!szCAR_NO.equals(ydDaoUtils.paraRecChkNull(recCarSch, "CAR_NO"))) {
					
					szMsg = "차량스케줄ID("      + ydDaoUtils.paraRecChkNull(recCarSch, "YD_CAR_SCH_ID") + ")에 해당하는" +
					        "전문 차량번호("      + szCAR_NO                                              + ")와 "        +
					        "차량스케줄 차량번호(" + ydDaoUtils.paraRecChkNull(recCarSch, "CAR_NO")        + ")가 다릅니다.!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return blnRtnVal = false;
					
				}
				
				//PIDEV
//				String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "YdSimSeEJBBean => chkCarInfo", "APPPI0", "*", "*");
				
//				if( "N".equals(sApplyYnPI) ) {
//					//카드번호 비교 후 다르면 에러 처리후 리턴
//					if (!szCARD_NO.equals(ydDaoUtils.paraRecChkNull(recCarSch, "CARD_NO"))) {
//						
//						szMsg = "차량스케줄ID("      + ydDaoUtils.paraRecChkNull(recCarSch, "YD_CAR_SCH_ID") + ")에 해당하는" +
//						        "전문 카드번호("      + szCARD_NO                                             + ")와 "        +
//						        "차량스케줄 카드번호(" + ydDaoUtils.paraRecChkNull(recCarSch, "CARD_NO") + ")가 다릅니다.!";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//						return blnRtnVal = false;
//						
//					}
//				}
				
				//차량번호 카드번호 비교후 같으면 true 리턴
				blnRtnVal = true;
			//구내 운송("L")이면 운송장비코드 체크
			} else if (szYD_CAR_USE_GP.equals("L")) {
				//운송장비코드 비교 후 다르면 에러 처리후 리턴
				if (!szTRN_EQP_CD.equals(ydDaoUtils.paraRecChkNull(recCarSch, "TRN_EQP_CD"))) {
					
					szMsg = "차량스케줄ID("         + ydDaoUtils.paraRecChkNull(recCarSch, "YD_CAR_SCH_ID") + ")에 해당하는" +
					        "전문 운송장비코드("      + szTRN_EQP_CD                                          + ")와 "       +
					        "차량스케줄 운송장비코드(" + ydDaoUtils.paraRecChkNull(recCarSch, "TRN_EQP_CD") + ")가 다릅니다.!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return blnRtnVal = false;
					
				}
				//운송장비코드 비교후 같으면 true 리턴
				blnRtnVal = true;
			//차량 사용구분 error
			} else {
				
				szMsg = "차량스케줄ID(" + ydDaoUtils.paraRecChkNull(recCarSch, "YD_CAR_SCH_ID") + ")에 해당하는 " +
						"차량사용구분(" + szYD_CAR_USE_GP                                        + ") 에러!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
			}
		} catch(Exception e) {
			szMsg = "차량 정보 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal;
		
	} //end of chkCarInfo
	
	
	/**
	 * 오퍼레이션명 : 저장품유무체크
	 *  
	 * @param  String  szStlNo 재료번호
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkStock(String szStlNo)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkStock";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;
		//레코드셋 선언
		JDTORecordSet rsResult    = null;		
		
		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			//레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//재료번호
			recPara.setField("STL_NO", szStlNo);
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 0);
			
			//리턴값 메세지처리
			if (intRtnVal > 1) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {
				
				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "저장품유무체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkStock
	
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
			if (intRtnVal > 1) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
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
	} //end of chkGetEqp
	
	
	
	
	
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
			if (intRtnVal > 1) {
				
				szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "크레인설비ID(" + szEqpId + ")로 크레인사양 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "크레인설비ID(" + szEqpId + ")로 크레인사양 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "크레인사양 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetCrnSpec
	
	
	
	/**
	 * 오퍼레이션명 : 저장품길이로 저장위치 찾기
	 *  
	 * @param   String szStlNo 재료번호
	 * @return  String 저장위치(성공), ""(실패)
	 * @throws  JDTOException
	 */
	public String chkMtlLength(String szStlNo, String szBookOutLoc)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//리턴값(String)
		String szRtnVal       = null;
		//메세지
		String szMsg          = null;
		//메소드명
		String szMethodName   = "chkMtlLength";
		//재료길이구분
		String szYD_MTL_L_GP  = null;
		//저장위치1
		String szSTK_LOC1     = null;
		//저장위치2
		String szSTK_LOC2     = null;
		
		//레코드 선언
		JDTORecord recPara        = null;
		//레코드셋 선언
		JDTORecordSet rsResult    = null;		

		try {
			//============================================================
			//BRE RULE에서 BOOK_OUT 위치로  저장위치(적치열+적치BED)를 가져온다.
			//L1열의 01 bed와 S1열의 02 bed는 BOOK_OUT 위치 중복
			//테스트를 위하여 임의 생성
			//야드구분(1)+동구분(1)+설비구분(2)+열구분(2)+BED번호(2)
			szSTK_LOC1 = szBookOutLoc;
			szSTK_LOC2 = "KCRTS102";
			//============================================================

			//레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//저장품 테이블 조회
			blnRtnVal = chkGetStock(szStlNo, rsResult);
			//에러리턴
			if (!blnRtnVal) return szRtnVal = "";
			
			//데이터 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//재료길이구분('L2')
			szYD_MTL_L_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_L_GP");
			
			//재료길이구분이 없으면 에러 리턴
			if (szYD_MTL_L_GP.equals("") || szYD_MTL_L_GP.length() < 2) {
				
				szMsg = "재료번호(" + szStlNo + ")에 해당하는 길이구분 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal = "";
				
			}
			
			//저장위치1 데이터가 없거나 길이가 맞지 않으면 에러 리턴
			if (szSTK_LOC1.equals("") || szSTK_LOC1.length() < 6) {
				
				szMsg = "저장위치1 데이터(STK_LOC1)가 잘못되었습니다. szSTK_LOC1: " + szSTK_LOC1;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal = "";
				
			}
			
			//재료 길이 구분의 구분코드와 저장위치의 구분코드가 일치하면 해당 저장위치를 리턴한다.
			if (szYD_MTL_L_GP.substring(0, 1).equals(szSTK_LOC1.substring(4, 5))) {
				
				szRtnVal = szSTK_LOC1;
				return szRtnVal;
	
			}
			
			//저장위치2 데이터가 없거나 길이가 맞지 않으면 에러 리턴
			if (szSTK_LOC2.equals("") || szSTK_LOC2.length() < 6) {
				
				szMsg = "저장위치2 데이터(STK_LOC2)가 잘못되었습니다. szSTK_LOC2: " + szSTK_LOC2;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal = "";
				
			}
			
			//재료 길이 구분의 구분코드와 저장위치의 구분코드가 일치하면 해당 저장위치를 리턴한다.
			if (szYD_MTL_L_GP.substring(0, 1).equals(szSTK_LOC2.substring(4, 5))) {
				
				szRtnVal = szSTK_LOC2;
				return szRtnVal;
				
			}
		} catch(Exception e) {
			szMsg = "저장품 길이로 저장위치 찾기 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = "";
		}
		return szRtnVal = "";
		
	} //end of chkMtlLength
	
	
	
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
			if (!blnRtnVal) return blnRtnVal;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//설비상태
			szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
			
			//크레인의 상태가 'T'이면 false 리턴.
			//상수값으로 변경 - [2009.12.03 이현성]
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
		
	} //end of eqpStatCheck
	
	
	
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
			if (!blnRtnVal) return blnRtnVal;
			
			//결과 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 폭
			long lngMtlW     = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_W");
			// 길이
			long lngMtlL     = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_L");
			// 중량			
			long lngMtlWt    = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");

			//레코드셋 재생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//크레인사양 체크 및 조회
			blnRtnVal = this.chkGetCrnSpec(szEqpId, rsResult);
			if (!blnRtnVal) return blnRtnVal;
			
			//크레인사양 추출
			rsResult.first();
			recPara = rsResult.getRecord();
	
			// 크레인 작업 능력
			// 작업가능길이
			long lngAbleL  = ydDaoUtils.paraRecChkNullLong(recPara, "YD_WRK_ABLE_L");
			// 작업가능폭
			long lngAbleW  = ydDaoUtils.paraRecChkNullLong(recPara, "YD_WRK_ABLE_W");
			// 작업가능중량
			long lngAbleWt = ydDaoUtils.paraRecChkNullLong(recPara, "YD_WRK_ABLE_WT");
			
			//크레인 작업가능 길이와 재료의 길이 비교
			if (lngAbleL < lngMtlL) {
				szMsg = "크레인 작업가능 길이(" + lngAbleL + ") 보다 재료의 길이(" + lngMtlL + ")가 더 큽니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;
			}
	
			//크레인 작업가능 폭과 재료의 폭 비교
			if (lngAbleW < lngMtlW) {
				szMsg = "크레인 작업가능 폭(" + lngAbleW + ")보다 재료의 폭(" + lngMtlW + ")이 더 큽니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;
			}
			
			//크레인 작업가능 폭과 재료의 폭 비교
			if (lngAbleWt < lngMtlWt) {
				szMsg = "크레인 작업가능 중량(" + lngAbleWt + ")보다 재료의 중량(" + lngMtlWt + ")이 더 큽니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;
			}
		
		} catch(Exception e) {
			szMsg = "크레인작업가능사양과 재료사양을 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal = true;
	} //end of chkCrnSpecMtlSpec
	
	
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
			if (intRtnVal > 1) {
				
				szMsg = "작업예약ID 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "작업예약ID를 구하지 못했습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "작업예약ID 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "작업예약ID 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "작업예약ID생성 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of getYdWbookId
	

	

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
		boolean blnRtnVal = false;
		//리턴값(int)
		int intRtnVal = 0;
		//레코드 선언
		JDTORecord recPara     = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
			
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
			if (intRtnVal > 0) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 이미 등록되어 있습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 등록 가능합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = true;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "재료번호(" + szStlNo + ")로 작업예약재료 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "재료번호(" + szStlNo + ")로 작업예약재료 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "작업예약재료 등록여부 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
		
	} //end of chkYdWrkBookMtl
	
//=================================================================================================
//	김진욱 BEGIN
//=================================================================================================	
	


    /**
     * 오퍼레이션명 : 권상실적처리
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void craneLdHd(JDTORecord msgRecord)throws JDTOException  {
        //////////////////////
    	//TCCODE :	Y1YDR008//
    	//////////////////////
    	
    	//업데이트 할 크레인 스케줄 Data 항목 set
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();
        
        //파라미터 null check 후 받아온 Data
        JDTORecord getParamRecord = JDTORecordFactory.getInstance().create();


        //스케줄Table의 컬럼을 저장하기위해 생성
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
        //레코드 셋의 레코드값을 받음
        JDTORecord getRecord = JDTORecordFactory.getInstance().create();

        int intRtnVal 					= 0 ;
        
        String szMsg              		= "";
        String szMethodName       		= "craneLdHd";
        
        
        
        
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

	        //파라미터 check
	        intRtnVal = this.ParamCheck(msgRecord, getParamRecord, 0) ;
	        if(intRtnVal == -1) {
                szMsg = "파라미터 Check중 Error	: " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
	        }
	        


	        setCrnschRecord.setField("YD_CRN_SCH_ID",         getParamRecord.getFieldString("YD_CRN_SCH_ID"));
	        setCrnschRecord.setField("YD_SCH_CD",             getParamRecord.getFieldString("YD_SCH_CD")) ;
	        setCrnschRecord.setField("YD_UP_WR_LOC",          getParamRecord.getFieldString("YD_UP_WR_LOC")) ;
	        setCrnschRecord.setField("YD_UP_WR_LAYER",        getParamRecord.getFieldString("YD_UP_WR_LAYER")) ;
	        
	        
	        //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
	        intRtnVal = this.updYdCrnsch(setCrnschRecord, 0) ;
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	                return ;
	        	case -1	:
	                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        	case -2	:
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        	case -3	:
	                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        }


	        
	        
	        
	        //Key Data Check!			키값은 Null이나 ""가 되어서는 안됨.
	        if(setCrnschRecord.getFieldString("YD_CRN_SCH_ID").equals("") || setCrnschRecord.getFieldString("YD_CRN_SCH_ID") == null) {
                szMsg = "'YD_CRN_SCH_ID' Data Error	: 크레인스케줄ID가 없습니다." ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
	        }
	        //대상 데이터 SELECT
	        intRtnVal = this.getYdCrnsch(setCrnschRecord, getRecSet,3);
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	                return ;
	        	case -2	:
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        }

	        
	        
	        getRecSet.first();
	        //레코드셋의 사이즈값으로 ErrorCheck
	        if(getRecSet.size() == 0){
	            szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            return ;
	        }
	        getRecord = getRecSet.getRecord();
	        
	        
	        
	        //조회한 크레인 스케줄의 작업활성상태가 check를 한다.
	        if (getRecord.getFieldString("YD_WRK_PROG_STAT").equals("1")
	                || getRecord.getFieldString("YD_WRK_PROG_STAT").equals("W")) {
	            

	        	// 적치단 정보 Clear			(1개의 크레인스케줄에 잡혀있는 크레인작업재료의 정보를 모두 Check!)
	        	intRtnVal = this.clearYdStklyr(getRecSet, 0);
    	        switch (intRtnVal) {
		        	case 0	:
		                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
		                return ;
		        	case -1	:
		                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
		        	case -2	:
		                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
		                return ;
		        	case -3	:
		                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
    	        }
		        
		        
	            //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
		        setCrnschRecord = JDTORecordFactory.getInstance().create();
		        setCrnschRecord.setField("YD_CRN_SCH_ID",      getParamRecord.getFieldString("YD_CRN_SCH_ID"));
		        setCrnschRecord.setField("YD_WRK_PROG_STAT",   getParamRecord.getFieldString("YD_WRK_PROG_STAT"));
		        setCrnschRecord.setField("YD_EQP_ID",     	  getParamRecord.getFieldString("YD_EQP_ID"));
		        setCrnschRecord.setField("YD_UP_WR_LOC",       getParamRecord.getFieldString("YD_UP_WR_LOC"));
		        setCrnschRecord.setField("YD_UP_WR_LAYER",     getParamRecord.getFieldString("YD_UP_WR_LAYER"));
		        setCrnschRecord.setField("YD_UP_WRK_ACT_GP",   getParamRecord.getFieldString("YD_UP_WRK_ACT_GP"));
		        setCrnschRecord.setField("YD_UP_WR_XAXIS",     getParamRecord.getFieldString("YD_CRN_XAXIS"));
		        setCrnschRecord.setField("YD_UP_WR_YAXIS",     getParamRecord.getFieldString("YD_CRN_YAXIS"));
		        setCrnschRecord.setField("YD_UP_WR_ZAXIS",     getParamRecord.getFieldString("YD_CRN_ZAXIS"));
	
	            intRtnVal = this.updYdCrnsch(setCrnschRecord, 0);
	            if(intRtnVal <= 0) return ;
		        switch (intRtnVal) {
		        	case 0	:
		                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
		                return ;
		        	case -1	:
		                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
		        	case -2	:
		                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
		                return ;
		        	case -3	:
		                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
		        }
		        
			    //대차 및 차량 스케줄 이송재료 Handling    
	            if(ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TC")){
	            	
	            	intRtnVal = this.setYdTcar(getRecSet, 0) ; 	
	            	//setYdTcar에서 Error Message를 보여준다.
	            	if(intRtnVal <= 0) {
		        		return ;
	            	}else{
		        		szMsg = "대차이송재료 삭제 완료" ;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            	}
	
	            }else if(ydDaoUtils.paraRecChkNull(getParamRecord,"YD_UP_WR_LOC").substring(2, 4).equals("PT")
	            		|| ydDaoUtils.paraRecChkNull(getParamRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TR")){
	            	
	            	intRtnVal = this.setYdCar(getRecSet, 0) ; 	
	            	//setYdCar에서 Error Message를 보여준다.
	            	if(intRtnVal <= 0) {
		        		return ;
	            	}else{
		        		szMsg = "차량이송재료 삭제 완료" ;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            	}
	            }
    
	        }else{
	            szMsg = "YD_WRK_PROG_STAT data : '1' or 'w' not" ;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            return ;
	        }
	            
	        szMsg="권상 완료 실적 처리 완료";
	        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	    }catch(JDTOException e) {
			System.out.println("JDTOError :  "+ e.getLocalizedMessage());
	   
	    }catch(Exception e) {
	    	System.out.println("Error :  "+ e.getLocalizedMessage());
	    }//end of try~catch
	    
    }// end of craneLdHd()
    

    
    
    
    
    /**
     * 오퍼레이션명 : 권하실적처리
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void craneUdHd(JDTORecord msgRecord)throws JDTOException  {
        //////////////////////
    	//TCCODE :	Y1YDR009//
    	//////////////////////
    	
    	
        int intRtnVal = 0;
        
        
        //DATA SETTING시 사용
        JDTORecord setRecord 				= JDTORecordFactory.getInstance().create();
        
        //레코드셋에서 레코드값을 읽어 올 때 사용
        JDTORecord getRecord                = JDTORecordFactory.getInstance().create(); ;
        
        //작업예약 업데이트 항목
        JDTORecord bookrecord               = JDTORecordFactory.getInstance().create();
        
        //크레인 스케줄 야드작업진행상태 비교		 (작업예약이 끝났는지 확인하기위해 카운터 값을 가져옴)
        JDTORecord outRecord                = JDTORecordFactory.getInstance().create();         
        
        //권하실적완료처리호출시에 받아온 파라미터값을 NULL CHECK후 사용
        JDTORecord getCrnschRecord 			= JDTORecordFactory.getInstance().create();
       

        //타 메소드를 호출하여 레코드셋값을 받을때 사용..
        JDTORecordSet getRecSet 			= JDTORecordFactory.getInstance().createRecordSet("temp");
        
        

        
        String szMsg                        = "" ;
        String szMethodName                 = "craneUdHd";
        
        
        //크레인 XYZ축 			파라미터에서 값이 오지 않았을때 지시위치값을 저장해서 실적등록에 사용
        String szYD_CRN_XAXIS     			= "" ;
        String szYD_CRN_YAXIS     			= "" ;
        String szYD_CRN_ZAXIS     			= "" ;
        //WBOOK_ID				작업예약 완료 처리시 사용
        String szYdWbookId              	= "" ;
        
        
        
        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null){
            szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            
            return ;
        }
        
        
        
        if(bDebugFlag){
            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        
        
        
        try{
        	
	        intRtnVal = this.ParamCheck(msgRecord, getCrnschRecord, 1) ;
	        if(intRtnVal == -1) {
	            szMsg = "파라미터 Check중 Error	: " + intRtnVal;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            return ;
	        }
        	
	        //파라미터 레코드 편집
	        setRecord.setField("YD_CRN_SCH_ID",       	getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("YD_SCH_CD",           	getCrnschRecord.getFieldString("YD_SCH_CD"));
	        setRecord.setField("YD_DN_WR_LOC",        	getCrnschRecord.getFieldString("YD_DN_WR_LOC"));
	        setRecord.setField("YD_DN_WR_LAYER",      	getCrnschRecord.getFieldString("YD_DN_WR_LAYER"));
	        
	
	        
	        //Key Data Check!			키값은 Null이나 ""가 되어서는 안됨.
	        if(setRecord.getFieldString("YD_CRN_SCH_ID").equals("") || setRecord.getFieldString("YD_CRN_SCH_ID") == null) {
                szMsg = "'YD_CRN_SCH_ID' Data Error	: 크레인스케줄ID가 없습니다." ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
	        }
	        //크레인스케줄 업데이트	
	        intRtnVal = this.updYdCrnsch(setRecord, 0);
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	                return ;
	        	case -1	:
	                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        	case -2	:
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        	case -3	:
	                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        }

	        
	        
	        //대상 데이터 SELECT
	        intRtnVal = this.getYdCrnsch(setRecord, getRecSet,3);
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	                return ;
	        	case -2	:
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        }
	        
	        
	        getRecSet.first();
	        //레코드셋의 사이즈값으로 ErrorCheck
	        if(getRecSet.size() == 0){
	            szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            return ;
	        }
	        getRecord = getRecSet.getRecord();
	        
	     
	        
	        //작업예약 테이블 데이터를 가져오기 위해 예약ID 저장
	        szYdWbookId = ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID") ;
	        
	        
	        //작업진행상태 체크(2, 3) 2,3이 아니면 에러 메시지 출력후 리턴
	        if ((!ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT").equals("2")) 
	        		&& (!ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT").equals("3")) ) {
	            szMsg = "작업진행상태가   권상('2') 또는 권하대기('3')이 아닙니다., ErrorCode:" ;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            return ;
	        }
	            
	        
	        //IF 다르면 지시정보 CLEAR
	        if (!getRecord.getFieldString("YD_DN_WR_LOC").equals(getRecord.getFieldString("YD_DN_WO_LOC"))) {
	            
	        	//크레인XYZ축이 없다면 지시정보의 XYZ축을 쓰도록한다.
	        	if(szYD_CRN_XAXIS.equals("") && szYD_CRN_YAXIS.equals("") && szYD_CRN_ZAXIS.equals("")) {
	        		szYD_CRN_XAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_XAXIS");
	        		szYD_CRN_YAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_YAXIS");
	        		szYD_CRN_ZAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_ZAXIS");
	        	}
	        	
	        	intRtnVal = this.clearYdStklyr(getRecSet,1) ;
    	        switch (intRtnVal) {
		        	case 0	:
		                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
		                return ;
		        	case -1	:
		                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
		        	case -2	:
		                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
		                return ;
		        	case -3	:
		                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
		        }
	        }
	
	        
	        //적치단의 정보등록 수행
	        if(getRecord.equals(null)) return ;
	        //적치단 권하정보등록
	        intRtnVal = this.regYdStklyr(getRecSet,1) ;
	        //regYdStklyr메소드에서 Error발생시 Message를 보여준다.
	        if(intRtnVal<=0) return ;
	      
	        //크레인스케줄 table 업데이트
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("YD_DN_WR_LOC",       getCrnschRecord.getFieldString("YD_DN_WR_LOC")) ;
	        setRecord.setField("YD_DN_WR_LAYER",     getCrnschRecord.getFieldString("YD_DN_WR_LAYER")) ;
	        //setRecord.setField("YD_DN_CMPL_DT",      getCrnschRecord.getFieldString("TIME+DATE") );
	        setRecord.setField("YD_DN_WRK_ACT_GP",   getCrnschRecord.getFieldString("YD_DN_WRK_ACT_GP") );
	        setRecord.setField("YD_DN_WR_XAXIS",     getCrnschRecord.getFieldString("YD_CRN_XAXIS"));
	        setRecord.setField("YD_DN_WR_YAXIS",     getCrnschRecord.getFieldString("YD_CRN_YAXIS"));
	        setRecord.setField("YD_DN_WR_ZAXIS",     getCrnschRecord.getFieldString("YD_CRN_ZAXIS"));
	        setRecord.setField("YD_WRK_PROG_STAT",   getCrnschRecord.getFieldString("YD_WRK_PROG_STAT"));
	        setRecord.setField("YD_GP",   getCrnschRecord.getFieldString("YD_GP"));
	        intRtnVal = this.updYdCrnsch(setRecord, 0);
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	                return ;
	        	case -1	:
	                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        	case -2	:
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        	case -3	:
	                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        }
	        

	        
		    //대차 및 차량 스케줄 이송재료 Handling    
            if(ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(2, 4).equals("TC")){
            	
            	intRtnVal = this.setYdTcar(getRecSet, 1) ; 	
            	if(intRtnVal == -1) {
            		return ;
            	}else{
	        		szMsg = "대차이송재료 등록 완료" ;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            	}

            }else if(ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(2, 4).equals("PT")
            		|| ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(2, 4).equals("TR")){
            	
            	intRtnVal = this.setYdCar(getRecSet, 1) ; 
            	if(intRtnVal == -1) {
            		return ;
            	}else{
	        		szMsg = "차량이송재료 등록 완료" ;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            	}
            }
            
	        //Key Data Check!			키값은 Null이나 ""가 되어서는 안됨.
	        if(getRecord.getFieldString("YD_WBOOK_ID").equals("") || getRecord.getFieldString("YD_WBOOK_ID") == null) {
                szMsg = "YD_WBOOK_ID  Data Error	: 크레인스케줄ID가 없습니다." ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
	        }
	        //작업예약완료 CHECK
	        getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
			intRtnVal = this.getYdCrnsch(getRecord, getRecSet, 4);
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	                return ;
	        	case -2	:
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        }
	        
	        
	        
			getRecSet.first();
	        //레코드셋의 사이즈값으로 ErrorCheck
	        if(getRecSet.size() == 0){
	            szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            return ;
	        }
	        outRecord = getRecSet.getRecord();
	        
	        
	        int schcnt = outRecord.getFieldInt("SCH_CNT");
	        int endcnt = outRecord.getFieldInt("END_CNT");
	        

            
	        if (schcnt == endcnt) {
	            bookrecord.setField("YD_WBOOK_ID",        szYdWbookId) ;
	            bookrecord.setField("DEL_YN",             "Y") ;
	            bookrecord.setField("YD_SCH_PROG_STAT",   "E") ;
	            
	            intRtnVal = this.updYdWrkbook(bookrecord, 0);
		        switch (intRtnVal) {
		        	case 0	:
		                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
		                return ;
		        	case -1	:
		                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
		        	case -2	:
		                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
		                return ;
		        	case -3	:
		                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
		        	case -4	:
		                szMsg = "Exception Error!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
		        }

	            
		        
	            szMsg = "작업 예약 처리 완료" ;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	
	        }else{
	
	            szMsg = "작업 예약 진행 중" ;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	        }
	        
	        
	       
	        
	
	        szMsg = "권하 완료 실적 처리 완료 !!" ; 
	        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	        
        }catch(Exception e) {
        	System.out.println("Error :  "+ e.getLocalizedMessage());
        }//end of try~catch
        
    }// end of craneUdHd()
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 권상,권하 파라미터 체크
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int ParamCheck (JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        String szMsg                        = "" ;
        String szMethodName                 = "craneUdHd";
        int intRtnVal = 0 ;
        
    	try{
            
			setRecord.setField("YD_CRN_SCH_ID"			, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID")) ;
			setRecord.setField("YD_SCH_CD"        		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD")) ;
			setRecord.setField("MSG_ID"          		, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_ID")) ;
			setRecord.setField("DATE"          			, ydDaoUtils.paraRecChkNull(msgRecord,"DATE")) ;
			setRecord.setField("TIME"          			, ydDaoUtils.paraRecChkNull(msgRecord,"TIME")) ;
			setRecord.setField("MSG_GP"          		, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_GP")) ;
			setRecord.setField("YD_EQP_ID"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID")) ;
			setRecord.setField("YD_EQP_WRK_MODE"        , ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE")) ;
			setRecord.setField("YD_WRK_PROG_STAT"       , ydDaoUtils.paraRecChkNull(msgRecord,"YD_WRK_PROG_STAT")) ;
			setRecord.setField("YD_CRN_XAXIS"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_XAXIS")) ;
			setRecord.setField("YD_CRN_YAXIS"         	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_YAXIS")) ;
			setRecord.setField("YD_CRN_ZAXIS"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_ZAXIS")) ;
    		if(intGp == 0){
    			setRecord.setField("YD_UP_WR_LOC"            	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LOC")) ;
				setRecord.setField("YD_UP_WR_LAYER"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LAYER")) ;
    		
		        //전문 송신지 위치 Check				:AUTO MANUAL BACKUP구분
		        if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("1")) {
		        	setRecord.setField("YD_UP_WRK_ACT_GP", "A") ;
		        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("9")) {
		        	setRecord.setField("YD_UP_WRK_ACT_GP", "B") ;
		        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("0")) {
		        	setRecord.setField("YD_UP_WRK_ACT_GP", "M") ;
		        }
    		}
    		
    		if(intGp == 1){
    			setRecord.setField("YD_DN_WR_LOC"            	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LOC")) ;
				setRecord.setField("YD_DN_WR_LAYER"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LAYER")) ;

				//실적위치 Error Check					:  스케줄코드와 설비id가 같지않다면 Error 현재 테스트동안은 실적위치랑 맞지 않기에 스케줄코드와 비교중..
				//if (!setRecord.getFieldString("YD_DN_WR_LOC").substring(0,6).equals(setRecord.getFieldString("YD_EQP_ID") ) ) {
				//if (!setRecord.getFieldString("YD_SCH_CD").substring(0,6).equals(setRecord.getFieldString("YD_EQP_ID") ) ) {
		        //    setRecord.setField("ERROR_CHECK", "E") ; 
		        //    return setRecord ;
		        //}
				
		        
	            //전문 송신지 위치 Check				:AUTO MANUAL BACKUP구분
		        if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("1")) {
		        	setRecord.setField("YD_DN_WRK_ACT_GP", "A") ;
		        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("9")) {
		        	setRecord.setField("YD_DN_WRK_ACT_GP", "B") ;
		        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("0")) {
		        	setRecord.setField("YD_DN_WRK_ACT_GP", "M") ;
		        }
    		}
    		
    		outRecord.addRecord(setRecord) ;
            
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -1 ;
			return intRtnVal;
        }//end of try~catch
        
        intRtnVal = 1 ;
        return intRtnVal;
        
    }//end of ParamCheck()
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 크레인스케줄 Select
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int getYdCrnsch (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
    	
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	
        try{
        	
	        intRtnVal = ydCrnschDao.getYdCrnsch(msgRecord, getRecSet, intGp);  
	        outRecSet.addAll(getRecSet);

        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -1 ;
        }//end of try~catch

        return intRtnVal ;
    	
    }//end of getYdCrnsch()
    
    
    
    
    
    /**
     * 오퍼레이션명 : 크레인스케줄 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int updYdCrnsch(JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

    	int intRtnVal = 0 ;

		try{
			
			intRtnVal = ydCrnschDao.updYdCrnsch(msgRecord, intGp);		        
			
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of updYdCrnsch
    
    

    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 Clear
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int clearYdStklyr (JDTORecordSet getRecSet, int intGp) throws JDTOException {
    	
    	JDTORecord getRecord = JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
    	
    	int intRtnVal 		= 0;
    	String szMsg 		= "";
    	String szMethodName = "";
    	
    	try{
    		int rowsize = getRecSet.size();
            getRecSet.first();
            getRecord = getRecSet.getRecord();
            
    		for(int i=0; i<rowsize ; i++){
    			//권상 지시위치 Clear
                if(intGp == 0) {
        			String szYD_UP_WR_LOC             = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC") ;
                    String szYD_UP_WR_LAYER           = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LAYER") ;
                    
                    setRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
                    setRecord.setField("YD_STK_BED_NO",       szYD_UP_WR_LOC.substring(6,8));   
                    //적치단 설정
                    String szStkLyr =ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, i);
                    setRecord.setField("YD_STK_LYR_NO",       szStkLyr) ;

//==========================================================================================                    
//                  기준이기 때문에 클리어 되면 안됨
//                  2009.09.25 권오창
//                    
//                  setRecord.setField("YD_STK_LYR_XAXIS",       "") ;
//                  setRecord.setField("YD_STK_LYR_YAXIS",       "") ;
//                  setRecord.setField("YD_STK_LYR_ZAXIS",       "") ;
//==========================================================================================                    
                }
                
                //권하 지시위치 Clear
                if(intGp == 1) {
	    			String szYD_DN_WO_LOC             = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC") ;
	                String szYD_DN_WO_LAYER           = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LAYER") ;
	                
	                setRecord.setField("YD_STK_COL_GP",       szYD_DN_WO_LOC.substring(0,6));    
	                setRecord.setField("YD_STK_BED_NO",       szYD_DN_WO_LOC.substring(6,8));   
	                //적치단 설정
	                String szStkLyr =ydDaoUtils.stringPlusInt(szYD_DN_WO_LAYER, i);
	                setRecord.setField("YD_STK_LYR_NO",       szStkLyr) ;
                }
                
                setRecord.setField("YD_STK_LYR_MTL_STAT", "E");
                setRecord.setField("STL_NO",              "");
                
                intRtnVal = this.updYdStklyr(setRecord, 1);  //적치단의 재료정보 Clear
                if(intRtnVal <= 0) {
                	return intRtnVal ;
                }

                getRecSet.next();
                getRecord = getRecSet.getRecord();
            } //end of for
    		
    		
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -1 ;
			return intRtnVal;
	    }//end of try~catch
		
		intRtnVal = 1 ;
		return  intRtnVal;
    }//end of clearYdStklyr()
    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 등록
     *  
     * @param  ● sgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int regYdStklyr (JDTORecordSet getRecSet, int intGp)throws JDTOException {
    	
    	JDTORecord getRecord 			= JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	String szMsg 					= "" ;
        String szMethodName				= "regYdStklyr" ;
        int intRtnVal 					= 0 ;
        
    	//JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
        String szYdWbookId 				= "" ;
    	
    	try{
    		int rowsize = getRecSet.size();
            
    		getRecSet.first();
    		getRecord 	= getRecSet.getRecord();

    		szYdWbookId = ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID") ;
        	for(int i=0; i<rowsize; i++) {
        		//권하 실적위치 등록
        		String szYD_DN_WR_LOC	   = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC") ;
        		String szYD_DN_WR_LAYER	   = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER") ;
        		String szSTL_NO	 		   = ydDaoUtils.paraRecChkNull(getRecord,"STL_NO") ;
        		
        		
        		setRecord.setField("YD_STK_COL_GP",       	szYD_DN_WR_LOC.substring(0,6));   
        		setRecord.setField("YD_STK_BED_NO",       	szYD_DN_WR_LOC.substring(6,8));               
                String szStkLyr = ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, i);
                setRecord.setField("YD_STK_LYR_NO", 			szStkLyr) ;
                setRecord.setField("STL_NO",              	szSTL_NO);                            
                setRecord.setField("YD_STK_LYR_MTL_STAT", 	"C");           
                setRecord.setField("YD_STK_LYR_XAXIS",      ydDaoUtils.paraRecChkNull(getRecord,"YD_STK_LYR_XAXIS")) ;
                setRecord.setField("YD_STK_LYR_YAXIS",      ydDaoUtils.paraRecChkNull(getRecord,"YD_STK_LYR_YAXIS")) ;
                setRecord.setField("YD_STK_LYR_ZAXIS",      ydDaoUtils.paraRecChkNull(getRecord,"YD_STK_LYR_ZAXIS")) ;
                
                //적치단dao를 호출해서 업데이트를 한다.
                intRtnVal = this.updYdStklyr(setRecord, 0); 
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
		        }

//    	        intRtnVal = this.setYdWrkplnsimulation(getRecord, 1) ;
//    	        if(intRtnVal <= 0 ) return intRtnVal;
//    	        //에러 메시지
//    	        
//    	        //차량 하차작업 일 경우			공통테이블에 진도코드를 갱신한다.
//    	        if(ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("PT")
//                		|| ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TR")){
//    	        	//진도코드 갱신
//    	        	intRtnVal = this.setProgCode(getRecord) ;
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
//    	        intRtnVal = this.setYdStrLoc(setRecord) ;
//    	        if(intRtnVal <= 0 ) return intRtnVal;
//    	        System.out.println("NO3=========================================================");
                getRecSet.next();
                getRecord = getRecSet.getRecord();
        	}
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -1 ;
			return intRtnVal ;
	    }//end of try~catch
		
		intRtnVal = 1 ;
		return intRtnVal ;
    
    }//end of regYdStklyr()
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 Select
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int getYdStklyr (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	String szMsg = "" ;
    	String szMethodName = "" ; 
    	int intRtnVal = 0 ;
        
        try{

	    	intRtnVal = ydStklyrDao.getYdStklyr(msgRecord, getRecSet, intGp);  
	    	
	    	outRecSet.addAll(getRecSet)  ; 
	        if (intRtnVal <= 0) {
	        	return intRtnVal;
	        }
	        
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
	    }//end of try~catch
		
		return intRtnVal ;
		
    }//end of getYdStklyr

    
    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● int execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
     * @throws ● JDTOException
     */
    public int updYdStklyr (JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
    	
    	int intRtnVal = 0 ;
        
        try{
        	
	    	intRtnVal = ydStklyrDao.updYdStklyr(msgRecord, intGp);  //적치단의 재료정보 Clear
	    	
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
	    }//end of try~catch
		
		return intRtnVal ;
		
    }//end of updYdStklyr
	

    
    
    
    
    /**
     * 오퍼레이션명 : 작업예약 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● int execution count(성공), 0:no data found, -1:dup_val_on_index, -2:parameter error, -3:execution failed, -4:Exception error
     * @throws ● JDTOException
     */
    public int updYdWrkbook (JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdWrkbookDao ydWrkbookDao   = new YdWrkbookDao(); 
    	
    	int intRtnVal = 0 ;
        
        try{
        	
            intRtnVal = ydWrkbookDao.updYdWrkbook(msgRecord, intGp);
            
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -4 ;
			return intRtnVal ;
	    }//end of try~catch
		
		return intRtnVal ;
    }//end of updYdWrkbook
    
    
    
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////   대차관련 METHOD   /////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 오퍼레이션명 : 대차 Setting
     *  
     * @param  ● inRecordSet, intGp(1:상차) 크레인스케줄을 조회한 레코드셋
     * @return ● int '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int setYdTcar (JDTORecordSet inRecordSet, int intGp) throws JDTOException{
    	//Data Setting
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	//data를 받음
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	//대차 스케줄 레코드셋의 레코드값을 받음
    	JDTORecord 	  getTcarRecord 		= JDTORecordFactory.getInstance().create();
    	
    	//대차 스케줄의 레코드셋
    	JDTORecordSet outRecSet  			= JDTORecordFactory.getInstance().createRecordSet("temp");
    	int intRtnVal 						= 0 ;
    	
    	String szMethodName 				= "setYdTcar" ;
    	String szMsg 						= "" ;
    	
    	//대차 스케줄 ID
    	String szYD_TCAR_SCH_ID 			= "" ;
    	
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
	    	intRtnVal = this.getYdTcarsch(setRecord, outRecSet, 1) ;
	    	if (intRtnVal <= 0) return intRtnVal ;
	    	
	    	
	    	// 대차스케줄 Data
	    	outRecSet.first() ;
	    	getTcarRecord = outRecSet.getRecord() ;
	    	// 대차스케줄 ID를 추출한다
	    	szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_TCAR_SCH_ID");
	    	
	    	//setRecord 초기화
	    	setRecord 			= JDTORecordFactory.getInstance().create();
	    	int szRowSize = inRecordSet.size(); 
	    	
	    	

	    	// 권상한 재료만큼 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)  

	    	for(int i = 0; i < szRowSize; i++){
		    	
	    		// 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)        
	    		setRecord.setField("YD_TCAR_SCH_ID",       	szYD_TCAR_SCH_ID);
	    		setRecord.setField("STL_NO",              	ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
	    		
	    		//대차 이송재료 등록 (하차 )
	    		if(intGp == 0) {
		    		setRecord.setField("DEL_YN",       			"Y");
		    		intRtnVal = this.updTcarftmvmtl(setRecord, 0) ;
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
			        }
			    	
			    //대차 이송재료 등록 (상차 )	
	    		}else if(intGp == 1) {
		    		//setRecord.setField("DEL_YN",       			"N");
		    		setRecord.setField("YD_STK_BED_NO",       	ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(6,8)) ;
		    		setRecord.setField("YD_STK_LYR_NO",       	ydDaoUtils.stringPlusInt(ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER"), i)) ;
		    		intRtnVal = this.insYdTcarftmvmtl(setRecord) ;
			    	switch (intRtnVal) {
			        	case -2	:
			                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
			                return intRtnVal;
			        	case -3	:
			                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return intRtnVal;
			        }
	    		}
		    	
	    		inRecordSet.next() ;
		    	getRecord = inRecordSet.getRecord();
		    	
	    	}
	    	
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -1 ;
		}//end of try~catch
    	
		return 1 ;
    	
    }//end of setYdTcar()
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 대차 스케줄 Select
     *  
     * @param  ● msgRecord, intGp(1:상하차)
     * @return ● int record count:성공, 0:no data found, -2:parameter error, -3:Exception error
     * @throws ● 
     */
    public int getYdTcarsch (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
    	YdTcarSchDao ydTcarschDao = new YdTcarSchDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	
	        intRtnVal = ydTcarschDao.getYdTcarsch(msgRecord, getRecSet, intGp);
	        if (intRtnVal <= 0) {
	        	return intRtnVal;
	        }
	        outRecset.addAll(getRecSet)  ;  
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }
        return intRtnVal ;
    }//end of getYdTcarsch
    

    
    
    /**
     * 오퍼레이션명 : 대차이송재료 Update
     *  
     * @param inRecordSet, intGp(1:상차) 크레인스케줄을 조회한 레코드셋
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws JDTOException
     */
    public int updTcarftmvmtl (JDTORecord inRecord, int intGp) throws JDTOException {
    	YdTcarFtmvMtlDao ydTcarftmvmtlDao   = new YdTcarFtmvMtlDao(); 
    	
    	int intRtnVal = 0 ;
        
        try{
        	
            intRtnVal = ydTcarftmvmtlDao.updYdTcarftmvmtl(inRecord, intGp) ;
            
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -3 ;
			return intRtnVal ;
	    }	
		
		return intRtnVal ;
    }//end of updTcarftmvmtl
    
    
    
    
    
    /**
     * 오퍼레이션명 : 대차이송재료 Insert
     *  
     * @param msgRecord, intGp(1:상하차)
     * @return int execution count, -2:parameter error, -3:Exception error
     * @throws 
     */
    public int insYdTcarftmvmtl(JDTORecord msgRecord){
    	YdTcarFtmvMtlDao ydTcarftmvmtlDao = new YdTcarFtmvMtlDao();
    	
    	int intRtnVal 			= 0 ;

        
        try{
        	
        	intRtnVal = ydTcarftmvmtlDao.insYdTcarftmvmtl(msgRecord);
        	if(intRtnVal == -2) return intRtnVal ;
        	
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }
        return intRtnVal ;
    	
    }//end of insYdTcarftmvmtl
    
    
    
    
    
    /**
     * 오퍼레이션명 : 대차스케줄 Update
     *  
     * @param msgRecord, intGp(0: 대차스케줄ID)
     * @return int '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int updYdTcarsch (JDTORecord msgRecord, int intGp){
    	YdTcarSchDao ydTcarschDao = new YdTcarSchDao();
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	
        	intRtnVal = ydTcarschDao.updYdTcarsch(msgRecord, intGp);
	        if (intRtnVal <= 0) {
	        	return -2;
	        }
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
        }//end of try~catch
        
        
        return 1 ;
    	
    }//end of updYdTcarsch
    
    
    
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////    차량관련 METHOD    ///////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * 오퍼레이션명 : 차량 Setting
     *  
     * @param inRecordSet, intGp(1:상차) 크레인스케줄을 조회한 레코드셋
     * @return int '1'이상: 성공   '0'이하: 실패
     * @throws JDTOException
     */
    public int setYdCar (JDTORecordSet inRecordSet, int intGp) throws JDTOException{
    	//Data Setting
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//data를 받음
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//차량 스케줄 레코드셋의 레코드값을 받음
    	JDTORecord 	  getTcarRecord 		= JDTORecordFactory.getInstance().create();
    	
    	//차량 스케줄의 레코드셋
    	JDTORecordSet outRecSet  			= JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	String szMethodName 				= "setYdCar" ;
    	String szMsg 						= "" ;
    	
    	int intRtnVal = 0 ;
    	
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
	    	intRtnVal = this.getYdCarsch(setRecord, outRecSet, 3) ;
	    	if (intRtnVal <= 0) return -1 ;
	    	
	    	
	    	// 차량스케줄 Data
	    	outRecSet.first() ;
	    	getTcarRecord = outRecSet.getRecord() ;
	    	// 차량스케줄 ID를 추출한다
	    	szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_CAR_SCH_ID");
	    	
	    	//setRecord 초기화
	    	setRecord 			= JDTORecordFactory.getInstance().create();
	    	int szRowSize = inRecordSet.size(); 
	    	
	    	

	    	// 권상한 재료만큼 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)  

	    	for(int i = 0; i < szRowSize; i++){
		    	// 차량스케줄 ID로 차량이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)        
	    		setRecord.setField("YD_CAR_SCH_ID",       	szYD_CAR_SCH_ID);
	    		setRecord.setField("STL_NO",              	ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
	    		
	    		//차량 이송재료 등록 (하차 )
	    		if(intGp == 0) {
		    		setRecord.setField("DEL_YN",       			"Y");
		    		intRtnVal = this.updCarftmvmtl(setRecord, 0) ;
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
		    		//setRecord.setField("DEL_YN",       			"N");
		    		setRecord.setField("YD_STK_BED_NO",       	ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(6,8)) ;
		    		setRecord.setField("YD_STK_LYR_NO",       	ydDaoUtils.stringPlusInt(ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER"), i)) ;
		    		intRtnVal = this.insYdCarftmvmtl(setRecord) ;
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
	    	
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -1 ;
		}//end of try~catch
		
    	return 1 ;
    	
    }//end of setYdCar()

    
    
    
    /**
     * 오퍼레이션명 : 차량 스케줄 Select
     *  
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int getYdCarsch (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
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
        
    }//end of getYdCarsch
    
    
    
    
    
    /**
     * 오퍼레이션명 : 차량 이송재료 Update
     *  
     * @param inRecordSet, intGp(1:상차) 크레인스케줄을 조회한 레코드셋
     * @return int execution count(성공), 0:no data found, -1:dup_val_on_index, -2:parameter error, -3:execution failed -4:Exception error
     * @throws JDTOException
     */
    public int updCarftmvmtl (JDTORecord inRecord, int intGp) throws JDTOException {
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
		
    }//end of updCarftmvmtl
    

    
    
    
    /**
     * 오퍼레이션명 : 차량이송재료 Insert
     *  
     * @param msgRecord, intGp(1:상하차)
     * @return int execution count, -2:parameter error, -3:Exception error
     * @throws 
     */
    public int insYdCarftmvmtl(JDTORecord msgRecord){
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
    	
    }//end of insYdCarftmvmtl


    
    
    /**
     * 오퍼레이션명 : 차량스케줄 Update
     *  
     * @param msgRecord, intGp(0: 대차스케줄ID)
     * @return getRecSet
     * @throws 
     */
    public int updYdCarsch (JDTORecord msgRecord, int intGp){
    	YdCarSchDao ydCarschDao = new YdCarSchDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
        String szMsg            = "";
        String szMethodName     = "craneLdHd";
        
        try{
        	
        	intRtnVal = ydCarschDao.updYdCarsch(msgRecord, intGp);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	            return intRtnVal ;
	        }
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
        }//end of try~catch
        
        
        return intRtnVal ;
    	
    }//end of updYdCarsch
    
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 저장품 Update
     *  
     * @param msgRecord, intGp(0: 대차스케줄ID)
     * @return int execution count(성공), 0:no data found, -1:dup_val_on_index, -2:parameter error, -3:execution failed
     * @throws 
     */
    public int updYdStock (JDTORecord msgRecord, int intGp){
    	YdStockDao ydStockDao = new YdStockDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
        String szMsg            = "";
        String szMethodName     = "craneLdHd";
        
        try{
        	intRtnVal = ydStockDao.updPtComm_LOC(msgRecord, intGp);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	            return intRtnVal ;
	        }
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
        }//end of try~catch
        
        
        return intRtnVal ;
    	
    }//end of updYdStock()
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 저장품 Select
     *  
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int getYdStock (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
    	YdStockDao ydStockDao = new YdStockDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	String szMsg			= "" ;
    	String szMethodName		= "getYdStock" ;
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	
        	intRtnVal = ydStockDao.getYdStock(msgRecord, getRecSet, intGp);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	            return intRtnVal ;
	        }
	        outRecset.addAll(getRecSet)  ; 
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of getYdStock()
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 작업계획 Simulation 삭제 Setting  
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int setYdWrkplnsimulation (JDTORecord msgRecord, int intGp){
    	YdWrkPlnSimulationDao ydWrkplnsimulationDao = new YdWrkPlnSimulationDao();
    	//작업계획 Sim 조회 한 값
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	//getRecSet의 첫번째 레코드 값을 저장
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	
    	String szMsg			= "" ;
    	String szMethodName		= "getYdWrkplnsimulation" ;
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	//작업계획 Simulation Select				msgRecord에는 스케줄코드와 재료번호가 있음
        	intRtnVal = this.getYdWrkplnsimulation(msgRecord, getRecSet, intGp);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	            return intRtnVal ;
	        }
	        getRecSet.first();
	        getRecord = getRecSet.getRecord();
	        getRecord.setField("DEL_YN", "Y") ;
	        getRecord.setField("MODIFIER", "SYSTEM") ;
	        
	        intRtnVal = this.updYdWrkplnsimulation(getRecord) ;
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	            return intRtnVal ;
	        }
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of setYdWrkplnsimulation()
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 작업계획 Simulation Select
     *  
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int getYdWrkplnsimulation (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
    	YdWrkPlnSimulationDao ydWrkplnsimulationDao = new YdWrkPlnSimulationDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	String szMsg			= "" ;
    	String szMethodName		= "getYdWrkplnsimulation" ;
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	
        	intRtnVal = ydWrkplnsimulationDao.getYdWrkplnsimulation(msgRecord, getRecSet, intGp);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	            return intRtnVal ;
	        }
	        outRecset.addAll(getRecSet)  ; 
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of getYdWrkplnsimulation()
    
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 작업계획 Simulation Update
     *  
     * @param msgRecord, int intGp 구분(0:STL_NO,YD_SCH_CD)
     * @return int execution count(성공), -2:parameter error, -3:execution failed
     * @throws 
     */
    public int updYdWrkplnsimulation (JDTORecord msgRecord){
    	YdWrkPlnSimulationDao ydWrkplnsimulationDao = new YdWrkPlnSimulationDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	String szMsg			= "" ;
    	String szMethodName		= "updYdWrkplnsimulation" ;
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	
        	intRtnVal = ydWrkplnsimulationDao.updYdWrkplnsimulationPlnIdAndLess(msgRecord);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == -2) {
	                szMsg = "parameter error	Error code:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -3) {
	                szMsg = "execution failed	Error code:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	            return intRtnVal ;
	        }
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of getYdWrkplnsimulation()
    
    
    


    
    
    
    
    /**
     * 오퍼레이션명 : 진도코드갱신 Setting 
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int setProgCode (JDTORecord msgRecord){
    	
    	
    	//작업계획 Sim 조회 한 값
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	//공통테이블 정보를 담기위한 값
    	JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	//getRecSet의 첫번째 레코드 값을 저장
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	//Update할 레코드 셋팅
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	String szCurrProgCd					= "" ;
    	String szBefoProgCd					= "" ;
    	
    	String szMsg						= "" ;
    	String szMethodName					= "getYdWrkplnsimulation" ;
    	//재료품목 정의
    	String szYdMtlItem					= "" ;
    	//재료종류별 번호
    	String szStlNo						= "" ;
    	int intRtnVal 						= 0 ;
        
        try{
        	
        	//재료품목으로 비교하고 품목에따라 맞는 공통테이블을 조회한다.
        	szYdMtlItem = msgRecord.getFieldString("YD_SCH_CD").substring(2, 4) ;
        	szStlNo 	= msgRecord.getFieldString("STL_NO") ;
        	
        	if(szYdMtlItem.equals("BM")){
        		//주편 공통
        		msgRecord.setField("MSLAB_NO", szStlNo) ;
        		intRtnVal = this.getYdStock(msgRecord, getRecSet, 6);
        		//에러 메시지
        		//
        		if(intRtnVal<0) return intRtnVal ;
        	}else if (szYdMtlItem.equals("SM") || szYdMtlItem.equals("SG") || szYdMtlItem.equals("SZ")) {
        		//슬라브 공통
        		msgRecord.setField("SLAB_NO", szStlNo) ;
        		intRtnVal = this.getYdStock(msgRecord, getRecSet, 2);
        		//에러 메시지
        		//
        		if(intRtnVal<0) return intRtnVal ;
        	}
        	
        	getRecSet.first();
        	getRecord = getRecSet.getRecord() ;
        	//읽어온 값의 항목을 저장
        	szCurrProgCd = getRecord.getFieldString("CURR_PROG_CD") ;
        	szBefoProgCd = getRecord.getFieldString("BEFO_PROG_CD") ;
        	
        	//현재진도코드 = 이송지시대기'D'
        	if(getRecord.getFieldString("CURR_PROG_CD").equals("D")) {
        		if(getRecord.getFieldInt("HRSHR_WO_ORDRMN_GP")== 1){
        			if(getRecord.getFieldString("SCARFING_YN").equals("Y")) {
        				//정정작업대기(작업대기)
        				setRecord.setField("CURR_PROG_CD", "C") ;
        			}else{
        				//압연지시대(지시대기)
        				setRecord.setField("CURR_PROG_CD", "B") ;
        			}
        		}else{
        			//충당대기
        			setRecord.setField("CURR_PROG_CD", "Z") ;
        		}
        	}
        	//전 진도코드 = 현재 진도코드,   전전진도코드 = 전진도코드
			setRecord.setField("BEFO_PROG_CD", 					szCurrProgCd) ;
			setRecord.setField("BEFOBEFO_PROG_CD",  			szBefoProgCd) ; 
			//현재시간
			setRecord.setField("CURR_PROG_REG_DDTT", 			ydUtils.getCurDate("yyyyMMddHHmmss")) ;
			setRecord.setField("BEFO_PROG_REG_DDTT",  			ydUtils.getCurDate("yyyyMMddHHmmss")) ; 
			setRecord.setField("BEFOBEFO_PROG_REG_DDTT", 		ydUtils.getCurDate("yyyyMMddHHmmss")) ;

			

        	//업데이트 항목 추가해야함 ///////////////////////////////////////////////////////
        	if(szYdMtlItem.equals("BM")){
        		//주편 공통
        		//구분자 설정 다시해야함!
        		intRtnVal = this.updYdStock(msgRecord,  6);
        		//에러 메시지
        		//
        		if(intRtnVal<0) return intRtnVal ;
        	}else if (szYdMtlItem.equals("SM") || szYdMtlItem.equals("SG") || szYdMtlItem.equals("SZ")) {
        		//슬라브 공통
        		intRtnVal = this.updYdStock(msgRecord,  2);
        		//에러 메시지
        		//
        		if(intRtnVal<0) return intRtnVal ;
        	}
        	
        	
        	
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of setProgCode()
    
    
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 저장위치 Setting 
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int setYdStrLoc (JDTORecord msgRecord){
    	//작업계획 Sim 조회 한 값
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	//getRecSet의 첫번째 레코드 값을 저장
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	//Update할 레코드 셋팅
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//현재저장위치
    	String szYdStrLoc					= "" ;
    	//이전저장위치
    	String szYdStrLocHis1				= "" ;

		
    	String szMsg						= "" ;
    	String szMethodName					= "getYdWrkplnsimulation" ;
    	//재료품목 정의
    	String szYdMtlItem					= "" ;
    	
    	//크레인스케줄의 정보				현재저장위치를 생성하기 위해...
    	String szYdGp						= "" ;
    	String szYdBayGp					= "" ;
    	String szYdEqpId					= "" ;
    	String szYdStkColNo					= "" ;
    	String szYdStkBedNo					= "" ;
    	String szYdStkLyrNo					= "" ;
    	
    	int intRtnVal 						= 0 ;
        
        try{



        	//재료품목으로 비교하고 품목에따라 맞는 공통테이블을 조회한다.
			szYdMtlItem = msgRecord.getFieldString("YD_SCH_CD").substring(2, 4) ;
			

        	if(szYdMtlItem.equals("BM")){
        		//주편 공통
        		intRtnVal = this.getYdStock(msgRecord, getRecSet, 6);
        		//에러 메시지
        		//
        		if(intRtnVal<0) return intRtnVal ;
        	}else if (szYdMtlItem.equals("SM") || szYdMtlItem.equals("SG") || szYdMtlItem.equals("SZ")) {
        		//슬라브 공통
        		intRtnVal = this.getYdStock(msgRecord, getRecSet, 2);
        		//에러 메시지
        		//
        		if(intRtnVal<0) return intRtnVal ;
        	}
        	
        	getRecSet.first();
        	getRecord 			= getRecSet.getRecord() ;
        	szYdStrLoc 			= getRecord.getFieldString("YD_STR_LOC") ;
        	szYdStrLocHis1 		= getRecord.getFieldString("YD_STR_LOC_HIS1") ;


        	szYdGp 				= msgRecord.getFieldString("YD_GP"); 
        	szYdBayGp 			= msgRecord.getFieldString("YD_BAY_GP");
        	szYdEqpId 			= msgRecord.getFieldString("YD_EQP_ID"); 
        	szYdStkColNo 		= msgRecord.getFieldString("YD_STK_CIL_NO"); 
        	szYdStkBedNo 		= msgRecord.getFieldString("YD_STK_BED_NO"); 
        	szYdStkLyrNo		= msgRecord.getFieldString("YD_STK_LYR_NO");
	        
	        
        	
	        
        	//전저장위치 = szYdStrLoc,	 전전저장위치 = szYdStrLocHis1
        	setRecord.setField("YD_STR_LOC_HIS1", 	szYdStrLoc) ;
        	setRecord.setField("YD_STR_LOC_HIS2", 	szYdStrLocHis1) ;
        	//현재 저장위치 = 실적위치+실적단 주편일경우와 슬라브일경우
        	if(szYdMtlItem.equals("BM")){
            	
        		setRecord.setField("MSLAB_NO",   msgRecord.getFieldString("MSLAB_NO")); 
        		setRecord.setField("YD_STR_LOC", szYdGp+szYdBayGp+szYdEqpId+szYdStkColNo+szYdStkBedNo.substring(1)+szYdStkLyrNo) ;
        		//주편 공통 업데이트
        		intRtnVal = this.updYdStock(setRecord,  6);
        		//에러메시지
        		//
        		if(intRtnVal<0) return intRtnVal ;
        		
        	}else if (szYdMtlItem.equals("SM") || szYdMtlItem.equals("SG") || szYdMtlItem.equals("SZ")) {
            	
        		setRecord.setField("SLAB_NO",    msgRecord.getFieldString("SLAB_NO")); 
        		setRecord.setField("YD_STR_LOC", szYdGp+szYdBayGp+szYdEqpId+szYdStkColNo+szYdStkBedNo+szYdStkLyrNo.substring(1)) ;
        		//슬라브 공통 업데이트
        		intRtnVal = this.updYdStock(setRecord,  2);
        		//에러메시지
        		//
        		if(intRtnVal<0) return intRtnVal ;
         	}
        	
        	
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of setYdStrLoc()
    

    
    
  
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////	코일 권상 권하	  /////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    

    
    
	/**
     * 오퍼레이션명 : 권상처리
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void craneLdHdCoil(JDTORecord msgRecord)throws JDTOException  {
        //////////////////////
    	//TCCODE :	Y1YDR008//
    	//////////////////////
    	
    	//업데이트 할 크레인 스케줄 Data 항목 set
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();
        
        //파라미터 null check 후 받아온 Data
        JDTORecord getParamRecord = JDTORecordFactory.getInstance().create();


        //스케줄Table의 컬럼을 저장하기위해 생성
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
        //레코드 셋의 레코드값을 받음
        JDTORecord getRecord = JDTORecordFactory.getInstance().create();

        int intRtnVal 					= 0 ;
        
        String szMsg              		= "";
        String szMethodName       		= "craneLdHdCoil";
        
        
        
        
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

	        //파라미터 check
	        intRtnVal = this.ParamCheckCoil(msgRecord, getParamRecord, 0) ;
	        if(intRtnVal == -1) {
                szMsg = "파라미터 Check중 Error	: " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
	        }
	        


	        setCrnschRecord.setField("YD_CRN_SCH_ID",         getParamRecord.getFieldString("YD_CRN_SCH_ID"));
	        setCrnschRecord.setField("YD_SCH_CD",             getParamRecord.getFieldString("YD_SCH_CD")) ;
	        setCrnschRecord.setField("YD_UP_WR_LOC",          getParamRecord.getFieldString("YD_UP_WR_LOC")) ;
	        setCrnschRecord.setField("YD_UP_WR_LAYER",        getParamRecord.getFieldString("YD_UP_WR_LAYER")) ;
	        
	        
	        //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
	        intRtnVal = this.updYdCrnschCoil(setCrnschRecord, 0) ;
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	                return ;
	        	case -1	:
	                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        	case -2	:
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        	case -3	:
	                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        }


	        
	        
	        
	        //Key Data Check!			키값은 Null이나 ""가 되어서는 안됨.
	        if(setCrnschRecord.getFieldString("YD_CRN_SCH_ID").equals("") || setCrnschRecord.getFieldString("YD_CRN_SCH_ID") == null) {
                szMsg = "'YD_CRN_SCH_ID' Data Error	: 크레인스케줄ID가 없습니다." ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
	        }
	        //대상 데이터 SELECT
	        intRtnVal = this.getYdCrnschCoil(setCrnschRecord, getRecSet,3);
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	                return ;
	        	case -2	:
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        }

	        
	        
	        getRecSet.first();
	        //레코드셋의 사이즈값으로 ErrorCheck
	        if(getRecSet.size() == 0){
	            szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            return ;
	        }
	        getRecord = getRecSet.getRecord();
	        
	        
	        
	        //조회한 크레인 스케줄의 작업활성상태가 check를 한다.
	        if (getRecord.getFieldString("YD_WRK_PROG_STAT").equals("1")
	                || getRecord.getFieldString("YD_WRK_PROG_STAT").equals("W")) {
	            

	        	// 적치단 정보 Clear			(1개의 크레인스케줄에 잡혀있는 크레인작업재료의 정보를 모두 Check!)
	        	intRtnVal = this.clearYdStklyrCoil(getRecSet, 0);
    	        switch (intRtnVal) {
		        	case 0	:
		                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
		                return ;
		        	case -1	:
		                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
		        	case -2	:
		                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
		                return ;
		        	case -3	:
		                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
    	        }
		        
		        
	            //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
		        setCrnschRecord = JDTORecordFactory.getInstance().create();
		        setCrnschRecord.setField("YD_CRN_SCH_ID",      getParamRecord.getFieldString("YD_CRN_SCH_ID"));
		        setCrnschRecord.setField("YD_WRK_PROG_STAT",   getParamRecord.getFieldString("YD_WRK_PROG_STAT"));
		        setCrnschRecord.setField("YD_EQP_ID",     	  getParamRecord.getFieldString("YD_EQP_ID"));
		        setCrnschRecord.setField("YD_UP_WR_LOC",       getParamRecord.getFieldString("YD_UP_WR_LOC"));
		        setCrnschRecord.setField("YD_UP_WR_LAYER",     getParamRecord.getFieldString("YD_UP_WR_LAYER"));
		        setCrnschRecord.setField("YD_UP_WRK_ACT_GP",   getParamRecord.getFieldString("YD_UP_WRK_ACT_GP"));
		        setCrnschRecord.setField("YD_UP_WR_XAXIS",     getParamRecord.getFieldString("YD_CRN_XAXIS"));
		        setCrnschRecord.setField("YD_UP_WR_YAXIS",     getParamRecord.getFieldString("YD_CRN_YAXIS"));
		        setCrnschRecord.setField("YD_UP_WR_ZAXIS",     getParamRecord.getFieldString("YD_CRN_ZAXIS"));
		        
	
	            intRtnVal = this.updYdCrnschCoil(setCrnschRecord, 0);
	            if(intRtnVal <= 0) return ;
		        switch (intRtnVal) {
		        	case 0	:
		                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
		                return ;
		        	case -1	:
		                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
		        	case -2	:
		                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
		                return ;
		        	case -3	:
		                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
		        }
		        
			    //대차 및 차량 스케줄 이송재료 Handling    
	            if(ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TC")){
	            	
	            	intRtnVal = this.setYdTcarCoil(getRecSet, 0) ; 	
	            	//setYdTcarCoil에서 Error Message를 보여준다.
	            	if(intRtnVal <= 0) {
		        		return ;
	            	}else{
		        		szMsg = "대차이송재료 삭제 완료" ;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            	}
	
	            }else if(ydDaoUtils.paraRecChkNull(getParamRecord,"YD_UP_WR_LOC").substring(2, 4).equals("PT")
	            		|| ydDaoUtils.paraRecChkNull(getParamRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TR")){
	            	
	            	intRtnVal = this.setYdCarCoil(getRecSet, 0) ; 	
	            	//setYdCarCoil에서 Error Message를 보여준다.
	            	if(intRtnVal <= 0) {
		        		return ;
	            	}else{
		        		szMsg = "차량이송재료 삭제 완료" ;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            	}
	            }
    
	        }else{
	            szMsg = "YD_WRK_PROG_STAT data : '1' or 'w' not" ;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            return ;
	        }
	            
	        szMsg="권상 완료 실적 처리 완료";
	        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	    }catch(JDTOException e) {
			System.out.println("JDTOError :  "+ e.getLocalizedMessage());
	   
	    }catch(Exception e) {
	    	System.out.println("Error :  "+ e.getLocalizedMessage());
	    }//end of try~catch
	    
    }// end of craneLdHdCoil()
    

    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 권하처리
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void craneUdHdCoil(JDTORecord msgRecord)throws JDTOException  {
        //////////////////////
    	//TCCODE :	Y1YDR009//
    	//////////////////////
    	
    	
        int intRtnVal = 0;
        
        
        //DATA SETTING시 사용
        JDTORecord setRecord 				= JDTORecordFactory.getInstance().create();
        
        //레코드셋에서 레코드값을 읽어 올 때 사용
        JDTORecord getRecord                = JDTORecordFactory.getInstance().create(); ;
        
        //작업예약 업데이트 항목
        JDTORecord bookrecord               = JDTORecordFactory.getInstance().create();
        
        //크레인 스케줄 야드작업진행상태 비교		 (작업예약이 끝났는지 확인하기위해 카운터 값을 가져옴)
        JDTORecord outRecord                = JDTORecordFactory.getInstance().create();         
        
        //권하실적완료처리호출시에 받아온 파라미터값을 NULL CHECK후 사용
        JDTORecord getCrnschRecord 			= JDTORecordFactory.getInstance().create();
       

        //타 메소드를 호출하여 레코드셋값을 받을때 사용..
        JDTORecordSet getRecSet 			= JDTORecordFactory.getInstance().createRecordSet("temp");
        
        

        
        String szMsg                        = "" ;
        String szMethodName                 = "craneUdHdCoil";
        
        
        //크레인 XYZ축 			파라미터에서 값이 오지 않았을때 지시위치값을 저장해서 실적등록에 사용
        String szYD_CRN_XAXIS     			= "" ;
        String szYD_CRN_YAXIS     			= "" ;
        String szYD_CRN_ZAXIS     			= "" ;
        //WBOOK_ID				작업예약 완료 처리시 사용
        String szYdWbookId              	= "" ;
        
        
        
        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null){
            szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            
            return ;
        }
        
        
        
        if(bDebugFlag){
            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        
        
        
        try{
        	
	        intRtnVal = this.ParamCheckCoil(msgRecord, getCrnschRecord, 1) ;
	        if(intRtnVal == -1) {
	            szMsg = "파라미터 Check중 Error	: " + intRtnVal;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            return ;
	        }
        	
	        //파라미터 레코드 편집
	        setRecord.setField("YD_CRN_SCH_ID",       	getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("YD_SCH_CD",           	getCrnschRecord.getFieldString("YD_SCH_CD"));
	        setRecord.setField("YD_DN_WR_LOC",        	getCrnschRecord.getFieldString("YD_DN_WR_LOC"));
	        setRecord.setField("YD_DN_WR_LAYER",      	getCrnschRecord.getFieldString("YD_DN_WR_LAYER"));
	        
	
	        
	        //Key Data Check!			키값은 Null이나 ""가 되어서는 안됨.
	        if(setRecord.getFieldString("YD_CRN_SCH_ID").equals("") || setRecord.getFieldString("YD_CRN_SCH_ID") == null) {
                szMsg = "'YD_CRN_SCH_ID' Data Error	: 크레인스케줄ID가 없습니다." ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
	        }
	        //크레인스케줄 업데이트	
	        intRtnVal = this.updYdCrnschCoil(setRecord, 0);
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	                return ;
	        	case -1	:
	                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        	case -2	:
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        	case -3	:
	                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        }

	        
	        
	        //대상 데이터 SELECT
	        intRtnVal = this.getYdCrnschCoil(setRecord, getRecSet,3);
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	                return ;
	        	case -2	:
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        }
	        
	        
	        getRecSet.first();
	        //레코드셋의 사이즈값으로 ErrorCheck
	        if(getRecSet.size() == 0){
	            szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            return ;
	        }
	        getRecord = getRecSet.getRecord();
	        
	     
	        
	        //작업예약 테이블 데이터를 가져오기 위해 예약ID 저장
	        szYdWbookId = ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID") ;
	        
	        
	        //작업진행상태 체크(2, 3) 2,3이 아니면 에러 메시지 출력후 리턴
	        if ((!ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT").equals("2")) 
	        		&& (!ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT").equals("3")) ) {
	            szMsg = "작업진행상태가   권상('2') 또는 권하대기('3')이 아닙니다., ErrorCode:" ;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            return ;
	        }
	            
	        
	        //IF 다르면 지시정보 CLEAR
	        if (!getRecord.getFieldString("YD_DN_WR_LOC").equals(getRecord.getFieldString("YD_DN_WO_LOC"))) {
	            
	        	//크레인XYZ축이 없다면 지시정보의 XYZ축을 쓰도록한다.
	        	if(szYD_CRN_XAXIS.equals("") && szYD_CRN_YAXIS.equals("") && szYD_CRN_ZAXIS.equals("")) {
	        		szYD_CRN_XAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_XAXIS");
	        		szYD_CRN_YAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_YAXIS");
	        		szYD_CRN_ZAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_ZAXIS");
	        	}
	        	
	        	intRtnVal = this.clearYdStklyrCoil(getRecSet,1) ;
    	        switch (intRtnVal) {
		        	case 0	:
		                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
		                return ;
		        	case -1	:
		                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
		        	case -2	:
		                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
		                return ;
		        	case -3	:
		                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
		        }
	        }
	
	        
	        //적치단의 정보등록 수행
	        if(getRecord.equals(null)) return ;
	        //적치단 권하정보등록
	        intRtnVal = this.regYdStklyrCoil(getRecSet,1) ;
	        //regYdStklyrCoil메소드에서 Error발생시 Message를 보여준다.
	        if(intRtnVal<=0) return ;
	      
	        //크레인스케줄 table 업데이트
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("YD_DN_WR_LOC",       getCrnschRecord.getFieldString("YD_DN_WR_LOC")) ;
	        setRecord.setField("YD_DN_WR_LAYER",     getCrnschRecord.getFieldString("YD_DN_WR_LAYER")) ;
	        //setRecord.setField("YD_DN_CMPL_DT",      getCrnschRecord.getFieldString("TIME+DATE") );
	        setRecord.setField("YD_DN_WRK_ACT_GP",   getCrnschRecord.getFieldString("YD_DN_WRK_ACT_GP") );
	        setRecord.setField("YD_DN_WR_XAXIS",     getCrnschRecord.getFieldString("YD_CRN_XAXIS"));
	        setRecord.setField("YD_DN_WR_YAXIS",     getCrnschRecord.getFieldString("YD_CRN_YAXIS"));
	        setRecord.setField("YD_DN_WR_ZAXIS",     getCrnschRecord.getFieldString("YD_CRN_ZAXIS"));
	        setRecord.setField("YD_WRK_PROG_STAT",   getCrnschRecord.getFieldString("YD_WRK_PROG_STAT"));
	        setRecord.setField("YD_GP",   getCrnschRecord.getFieldString("YD_GP"));
	        intRtnVal = this.updYdCrnschCoil(setRecord, 0);
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	                return ;
	        	case -1	:
	                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        	case -2	:
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        	case -3	:
	                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        }
	        
	        
	        
	        
		    //대차 및 차량 스케줄 이송재료 Handling    
            if(ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(2, 4).equals("TC")){
            	
            	intRtnVal = this.setYdTcarCoil(getRecSet, 1) ; 	
            	if(intRtnVal == -1) {
            		return ;
            	}else{
	        		szMsg = "대차이송재료 등록 완료" ;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            	}

            }else if(ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(2, 4).equals("PT")
            		|| ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(2, 4).equals("TR")){
            	intRtnVal = this.setYdCarCoil(getRecSet, 1) ; 
            	if(intRtnVal == -1) {
            		return ;
            	}else{
	        		szMsg = "차량이송재료 등록 완료" ;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            	}
            }
            
	        
            
            
            
            //Key Data Check!			키값은 Null이나 ""가 되어서는 안됨.
	        if(getRecord.getFieldString("YD_WBOOK_ID").equals("") || getRecord.getFieldString("YD_WBOOK_ID") == null) {
                szMsg = "YD_WBOOK_ID  Data Error	: 크레인스케줄ID가 없습니다." ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
	        }
	        //작업예약완료 CHECK
	        getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
			intRtnVal = this.getYdCrnschCoil(getRecord, getRecSet, 4);
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	                return ;
	        	case -2	:
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        }

			getRecSet.first();
	        //레코드셋의 사이즈값으로 ErrorCheck
	        if(getRecSet.size() == 0){
	            szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            return ;
	        }
	        outRecord = getRecSet.getRecord();
	        
	        
	        int schcnt = outRecord.getFieldInt("SCH_CNT");
	        int endcnt = outRecord.getFieldInt("END_CNT");
	        
    
	        if (schcnt == endcnt) {
	            bookrecord.setField("YD_WBOOK_ID",        szYdWbookId) ;
	            bookrecord.setField("DEL_YN",             "Y") ;
	            bookrecord.setField("YD_SCH_PROG_STAT",   "E") ;
	            
	            intRtnVal = this.updYdWrkbookCoil(bookrecord, 0);
		        switch (intRtnVal) {
		        	case 0	:
		                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
		                return ;
		        	case -1	:
		                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
		        	case -2	:
		                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
		                return ;
		        	case -3	:
		                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
		        	case -4	:
		                szMsg = "Exception Error!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return ;
		        }

	            szMsg = "작업예약 처리 완료" ;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	
	        }else{
	
	            szMsg = "작업예약 진행 중" ;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	        }
	
	        szMsg = "권하 완료 실적 처리 완료 !!" ; 
	        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	        
        }catch(Exception e) {
        	System.out.println("Error :  "+ e.getLocalizedMessage());
        }//end of try~catch
        
    }// end of craneUdHdCoil()
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 권상,권하 파라미터 체크
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int ParamCheckCoil (JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        String szMsg                        = "" ;
        String szMethodName                 = "ParamCheckCoil";
        int intRtnVal = 0 ;
        
    	try{
            
			setRecord.setField("YD_CRN_SCH_ID"			, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID")) ;
			setRecord.setField("YD_SCH_CD"        		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD")) ;
			setRecord.setField("MSG_ID"          		, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_ID")) ;
			setRecord.setField("DATE"          			, ydDaoUtils.paraRecChkNull(msgRecord,"DATE")) ;
			setRecord.setField("TIME"          			, ydDaoUtils.paraRecChkNull(msgRecord,"TIME")) ;
			setRecord.setField("MSG_GP"          		, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_GP")) ;
			setRecord.setField("YD_EQP_ID"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID")) ;
			setRecord.setField("YD_EQP_WRK_MODE"        , ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE")) ;
			setRecord.setField("YD_WRK_PROG_STAT"       , ydDaoUtils.paraRecChkNull(msgRecord,"YD_WRK_PROG_STAT")) ;
			setRecord.setField("YD_CRN_XAXIS"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_XAXIS")) ;
			setRecord.setField("YD_CRN_YAXIS"         	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_YAXIS")) ;
			setRecord.setField("YD_CRN_ZAXIS"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_ZAXIS")) ;
    		if(intGp == 0){
    			setRecord.setField("YD_UP_WR_LOC"            	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LOC")) ;
				setRecord.setField("YD_UP_WR_LAYER"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LAYER")) ;
    		
		        //전문 송신지 위치 Check				:AUTO MANUAL BACKUP구분
		        if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("1")) {
		        	setRecord.setField("YD_UP_WRK_ACT_GP", "A") ;
		        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("9")) {
		        	setRecord.setField("YD_UP_WRK_ACT_GP", "B") ;
		        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("0")) {
		        	setRecord.setField("YD_UP_WRK_ACT_GP", "M") ;
		        }
    		}
    		
    		if(intGp == 1){
    			setRecord.setField("YD_DN_WR_LOC"            	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LOC")) ;
				setRecord.setField("YD_DN_WR_LAYER"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LAYER")) ;

				//실적위치 Error Check					:  스케줄코드와 설비id가 같지않다면 Error 현재 테스트동안은 실적위치랑 맞지 않기에 스케줄코드와 비교중..
				//if (!setRecord.getFieldString("YD_DN_WR_LOC").substring(0,6).equals(setRecord.getFieldString("YD_EQP_ID") ) ) {
				//if (!setRecord.getFieldString("YD_SCH_CD").substring(0,6).equals(setRecord.getFieldString("YD_EQP_ID") ) ) {
		        //    setRecord.setField("ERROR_CHECK", "E") ; 
		        //    return setRecord ;
		        //}
				
		        
	            //전문 송신지 위치 Check				:AUTO MANUAL BACKUP구분
		        if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("1")) {
		        	setRecord.setField("YD_DN_WRK_ACT_GP", "A") ;
		        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("9")) {
		        	setRecord.setField("YD_DN_WRK_ACT_GP", "B") ;
		        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("0")) {
		        	setRecord.setField("YD_DN_WRK_ACT_GP", "M") ;
		        }
    		}
    		
    		outRecord.addRecord(setRecord) ;
            
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -1 ;
			return intRtnVal;
        }//end of try~catch
        
        intRtnVal = 1 ;
        return intRtnVal;
        
    }//end of ParamCheckCoil()
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 크레인스케줄 Select
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int getYdCrnschCoil (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
    	
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	
        try{
        	
	        intRtnVal = ydCrnschDao.getYdCrnsch(msgRecord, getRecSet, intGp);  
	        outRecSet.addAll(getRecSet);

        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -1 ;
        }//end of try~catch

        return intRtnVal ;
    	
    }//end of getYdCrnschCoil()
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 크레인스케줄 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int updYdCrnschCoil(JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

    	int intRtnVal = 0 ;

		try{
			intRtnVal = ydCrnschDao.updYdCrnsch(msgRecord, intGp);		        
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of updYdCrnschCoil
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 Clear
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int clearYdStklyrCoil (JDTORecordSet getRecSet, int intGp) throws JDTOException {
    	
    	JDTORecord getRecord = JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
    	
    	//outRecSet의 첫번째 레코드값
    	JDTORecord outRecord 			= JDTORecordFactory.getInstance().create();
    	//적치단 조회
    	JDTORecordSet outRecSet 		= JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	
    	int intRtnVal 		= 0;
    	String szMsg 		= "";
    	String szMethodName = "";
    	
    	try{
    		int rowsize = getRecSet.size();
            getRecSet.first();
            getRecord = getRecSet.getRecord();
            
    		for(int i=0; i<rowsize ; i++){
    			
    			//권상 실적위치 Clear
                if(intGp == 0) {
        			String szYD_UP_WR_LOC             = 	  	 ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC") ;
                    String szYD_UP_WR_LAYER           = 	  	 ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LAYER") ;
                    
                	//권상 실적위치가 2단 일 경우 
                    setRecord.setField("YD_STK_COL_GP",       	 szYD_UP_WR_LOC.substring(0,6));    
                    setRecord.setField("YD_STK_BED_NO",       	 szYD_UP_WR_LOC.substring(6,8));   
                    //적치단 설정
                    String szStkLyr =ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, i);
                    setRecord.setField("YD_STK_LYR_NO",       	 szStkLyr) ;
                    setRecord.setField("STL_NO",             	 "");
                    setRecord.setField("YD_STK_LYR_MTL_STAT", 	 "E");

//==========================================================================================                    
//                  기준이기 때문에 클리어 되면 안됨
//                  2009.09.25 권오창
//                    
//                  setRecord.setField("YD_STK_LYR_XAXIS",       "") ;
//                  setRecord.setField("YD_STK_LYR_YAXIS",       "") ;
//                  setRecord.setField("YD_STK_LYR_ZAXIS",       "") ;
//==========================================================================================                    
                    intRtnVal = this.updYdStklyrCoil(setRecord, 1);  //적치단의 재료정보 Clear
                    if(intRtnVal <= 0) {
                    	return intRtnVal ;
                    }
                    //에러 메시지
                    //
                    
                    //권상 실적위치가 1단 일 경우
                    if (szYD_UP_WR_LAYER.equals("001")){
	                    setRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
	                    setRecord.setField("YD_STK_BED_NO",       szYD_UP_WR_LOC.substring(6,8));   
	                    //적치단 설정
	                    szStkLyr =ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, i);

	                    
	                    
	                    //적치BED의 앞쪽 DATA NULL CHECK
	                    //실적위치Bed Right 2단 Check			적치상태를 사용불가
	                    setRecord = JDTORecordFactory.getInstance().create();
	                    setRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
	                    setRecord.setField("YD_STK_BED_NO",       szYD_UP_WR_LOC.substring(6,8));  
	                    setRecord.setField("YD_STK_LYR_NO",       "002") ;
	                    setRecord.setField("YD_STK_LYR_MTL_STAT", "X");
	                    setRecord.setField("STL_NO",              "");
	                    intRtnVal = this.getYdStklyrCoil(setRecord, outRecSet, 0); 
	                    if(intRtnVal>0) {

		                    //실적위치Bed Right 2단 Check			적치상태를 사용불가
		                    setRecord = JDTORecordFactory.getInstance().create();
		                    setRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
		                    setRecord.setField("YD_STK_BED_NO",       szYD_UP_WR_LOC.substring(6,8));  
		                    setRecord.setField("YD_STK_LYR_NO",       "002") ;
		                    setRecord.setField("YD_STK_LYR_MTL_STAT", "X");
		                    setRecord.setField("STL_NO",              "");
		                    intRtnVal = this.updYdStklyrCoil(setRecord, 1); 
		                    //에러 메시지
		                    //
	                    }
	                    
	                    
	                    //적치BED의 앞쪽 DATA NULL CHECK
	                    //실적위치Bed Left 2단 Check 		적치상태를 사용불가
                    	setRecord = JDTORecordFactory.getInstance().create() ;
                    	//실적위치 Bed -1 = 실적위치Bed 왼쪽 2단
	                    String szStkBedNo =ydDaoUtils.stringPlusInt(szYD_UP_WR_LOC.substring(6,8), -1);
	                    setRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
	                    setRecord.setField("YD_STK_BED_NO",       szStkBedNo); 
	                    setRecord.setField("YD_STK_LYR_NO",       "002") ;
	                    setRecord.setField("YD_STK_LYR_MTL_STAT", "X");
	                    setRecord.setField("STL_NO",              "");
	                    intRtnVal = this.updYdStklyrCoil(setRecord, 1); 
	                    if(intRtnVal>0){
	
		                    //실적위치Bed Left 2단 Check 		적치상태를 사용불가
	                    	setRecord = JDTORecordFactory.getInstance().create() ;
	                    	//실적위치 Bed -1 = 실적위치Bed 왼쪽 2단
		                    szStkBedNo =ydDaoUtils.stringPlusInt(szYD_UP_WR_LOC.substring(6,8), -1);
		                    setRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
		                    setRecord.setField("YD_STK_BED_NO",       szStkBedNo); 
		                    setRecord.setField("YD_STK_LYR_NO",       "002") ;
		                    setRecord.setField("YD_STK_LYR_MTL_STAT", "X");
		                    setRecord.setField("STL_NO",              "");
		                    intRtnVal = this.updYdStklyrCoil(setRecord, 1); 
		                    //에러 메시지
		                    //
 
	                    }
	                    intRtnVal = 1 ;
                    }
   
                }
                
                
                
                //권하 지시위치 Clear
                if(intGp == 1) {
                	String szYD_DN_WO_LOC             = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC") ;
                    String szYD_DN_WO_LAYER           = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LAYER") ;
                    
                    //권상 실적위치가 2단 일 경우 
                    setRecord.setField("YD_STK_COL_GP",       szYD_DN_WO_LOC.substring(0,6));    
                    setRecord.setField("YD_STK_BED_NO",       szYD_DN_WO_LOC.substring(6,8));   
                    //적치단 설정
                    String szStkLyr =ydDaoUtils.stringPlusInt(szYD_DN_WO_LAYER, i);
                    setRecord.setField("YD_STK_LYR_NO",       szStkLyr) ;

//==========================================================================================                    
//                  기준이기 때문에 클리어 되면 안됨
//                  2009.09.25 권오창
//                    
//                    setRecord.setField("YD_STK_LYR_XAXIS",       "") ;
//                    setRecord.setField("YD_STK_LYR_YAXIS",       "") ;
//                    setRecord.setField("YD_STK_LYR_ZAXIS",       "") ;
//==========================================================================================                    
                    intRtnVal = this.updYdStklyrCoil(setRecord, 1);  //적치단의 재료정보 Clear
                    
                    //권상 실적위치가 1단 일 경우
                    if (szYD_DN_WO_LAYER == "001") {
	                    setRecord.setField("YD_STK_COL_GP",       szYD_DN_WO_LOC.substring(0,6));    
	                    setRecord.setField("YD_STK_BED_NO",       szYD_DN_WO_LOC.substring(6,8));   

	                    
	                    //실적위치Bed Right 2단 Check			적치상태를 사용불가
	                    setRecord = JDTORecordFactory.getInstance().create();
	                    setRecord.setField("YD_STK_COL_GP",       szYD_DN_WO_LOC.substring(0,6));    
	                    setRecord.setField("YD_STK_BED_NO",       szYD_DN_WO_LOC.substring(6,8));  
	                    setRecord.setField("YD_STK_LYR_NO",       "002") ;
	                    setRecord.setField("YD_STK_LYR_MTL_STAT", "X");
	                    setRecord.setField("STL_NO",              "");
	                    intRtnVal = this.updYdStklyrCoil(setRecord, 1); 
                    	
	                    
	                    //실적위치Bed Left 2단 Check 		적치상태를 사용불가
                    	setRecord = JDTORecordFactory.getInstance().create();
                    	//실적위치 Bed -1 = 실적위치Bed 왼쪽 2단
	                    String szStkBedNo =ydDaoUtils.stringPlusInt(szYD_DN_WO_LOC.substring(6,8), -1);
	                    setRecord.setField("YD_STK_COL_GP",       szYD_DN_WO_LOC.substring(0,6));    
	                    setRecord.setField("YD_STK_BED_NO",       szStkBedNo); 
	                    setRecord.setField("YD_STK_LYR_NO",       "002") ;
	                    setRecord.setField("YD_STK_LYR_MTL_STAT", "X");
	                    setRecord.setField("STL_NO",              "");
	                    intRtnVal = this.updYdStklyrCoil(setRecord, 1); 
                    }
                    	
                }
                    
                //권하처리값 리턴
                if(intRtnVal <= 0) {
                	return intRtnVal ;
                }
                
                getRecSet.next();
                getRecord = getRecSet.getRecord();
            } //end of for
    		
    		
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -1 ;
			return intRtnVal;
	    }//end of try~catch
		
		intRtnVal = 1 ;
		return  intRtnVal;
    }//end of clearYdStklyrCoil()
    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 등록
     *  
     * @param  ● sgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int regYdStklyrCoil (JDTORecordSet getRecSet, int intGp)throws JDTOException {
    	//getRecSet의 첫번째 레코드값
    	JDTORecord getRecord 			= JDTORecordFactory.getInstance().create();
    	//업데이트 data 셋팅
    	JDTORecord setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//outRecSet의 첫번째 레코드값
    	JDTORecord outRecord 			= JDTORecordFactory.getInstance().create();
    	//적치단 조회
    	JDTORecordSet outRecSet 		= JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	
    	String szMsg 					= "" ;
        String szMethodName				= "regYdStklyrCoil" ;
        int intRtnVal 					= 0 ;
        String szStkBedNo				= "" ;
        
    	//JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
        String szYdWbookId 				= "" ;
    	
    	try{
    		int rowsize = getRecSet.size();
            
    		getRecSet.first();
    		getRecord 	= getRecSet.getRecord();

    		szYdWbookId = ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID") ;
        	for(int i=0; i<rowsize; i++) {
        		//권하 실적위치 등록
        		String szYD_DN_WR_LOC	   = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC") ;
        		String szYD_DN_WR_LAYER	   = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER") ;
        		String szSTL_NO	 		   = ydDaoUtils.paraRecChkNull(getRecord,"STL_NO") ;
        		
        		
        		setRecord.setField("YD_STK_COL_GP",       	szYD_DN_WR_LOC.substring(0,6));   
        		setRecord.setField("YD_STK_BED_NO",       	szYD_DN_WR_LOC.substring(6,8));               
                String szStkLyr = ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, i);
                setRecord.setField("YD_STK_LYR_NO", 			szStkLyr) ;
                setRecord.setField("STL_NO",              	szSTL_NO);                            
                setRecord.setField("YD_STK_LYR_MTL_STAT", 	"C");           
                setRecord.setField("YD_STK_LYR_XAXIS",      ydDaoUtils.paraRecChkNull(getRecord,"YD_STK_LYR_XAXIS")) ;
                setRecord.setField("YD_STK_LYR_YAXIS",      ydDaoUtils.paraRecChkNull(getRecord,"YD_STK_LYR_YAXIS")) ;
                setRecord.setField("YD_STK_LYR_ZAXIS",      ydDaoUtils.paraRecChkNull(getRecord,"YD_STK_LYR_ZAXIS")) ;
                //적치단dao를 호출해서 업데이트를 한다.
                intRtnVal = this.updYdStklyrCoil(setRecord, 0); 
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
		        }

    	        if(szStkLyr.equals("001")) {
    	        	
                    //실적위치의 좌측 Bed 1단 Select
    	        	outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
                    setRecord = JDTORecordFactory.getInstance().create();
                    szStkBedNo = "0" + ( (Integer.parseInt(szYD_DN_WR_LOC.substring(6,8)) - 1 ) );
                    setRecord.setField("YD_STK_COL_GP",       szYD_DN_WR_LOC.substring(0,6));    
                    setRecord.setField("YD_STK_BED_NO",       szStkBedNo);  
                    setRecord.setField("YD_STK_LYR_NO",       "001") ;
                    //적치단 상태조회		:적치불가인지 아닌지 알기위해서...
                    intRtnVal = this.getYdStklyrCoil(setRecord, outRecSet, 0);
                    //에러 메시지
                    outRecSet.first() ;
                    outRecord = outRecSet.getRecord() ;
                    
                    //적치불가라면 적치가능으로 업데이트
                    if(outRecord.getFieldString("YD_STK_LYR_MTL_STAT").equals("C")) {

                    	//실적위치 좌측 Bed 2단 Select	
                    	outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
                        setRecord = JDTORecordFactory.getInstance().create();
                        szStkBedNo = "0" + ( (Integer.parseInt(szYD_DN_WR_LOC.substring(6,8)) - 1 ) );
                        setRecord.setField("YD_STK_COL_GP",       szYD_DN_WR_LOC.substring(0,6));    
                        setRecord.setField("YD_STK_BED_NO",       szStkBedNo);  
                        setRecord.setField("YD_STK_LYR_NO",       "002") ;
                        //적치단 상태조회		:적치불가인지 아닌지 알기위해서...
                        intRtnVal = this.getYdStklyrCoil(setRecord, outRecSet, 0);
                        //에러 메시지
                        outRecSet.first() ;
                        outRecord = outRecSet.getRecord() ;
                        
                        //적치불가라면 적치가능으로 업데이트
                        if(outRecord.getFieldString("YD_STK_LYR_MTL_STAT").equals("X")) {
                        	//실적위치Bed 좌측 Bed 2단 Check 		적치상태를 적치가능
                        	outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	                	setRecord = JDTORecordFactory.getInstance().create() ;
    	                	//실적위치 Bed -1 = 실적위치Bed 왼쪽 2단
    	                	szStkBedNo = "0" + ( (Integer.parseInt(szYD_DN_WR_LOC.substring(6,8)) - 1 ) );
    	                    setRecord.setField("YD_STK_COL_GP",       szYD_DN_WR_LOC.substring(0,6));    
    	                    setRecord.setField("YD_STK_BED_NO",       szStkBedNo); 
    	                    setRecord.setField("YD_STK_LYR_NO",       "002") ;
    	                    setRecord.setField("YD_STK_LYR_MTL_STAT", "E");
                        	intRtnVal = this.updYdStklyrCoil(setRecord, 1); 
                        	//에러 메시지
                        	//
                        }
                    	
                    }
    	        	
                    
                    //실적위치의 우측 Bed Select
                    outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
                    setRecord = JDTORecordFactory.getInstance().create();
                    szStkBedNo = "0" + ( (Integer.parseInt(szYD_DN_WR_LOC.substring(6,8)) + 1 ) );
                    setRecord.setField("YD_STK_COL_GP",       szYD_DN_WR_LOC.substring(0,6));    
                    setRecord.setField("YD_STK_BED_NO",       szStkBedNo);  
                    setRecord.setField("YD_STK_LYR_NO",       "001") ;
                    //적치단 상태조회		:적치불가인지 아닌지 알기위해서...
                    intRtnVal = this.getYdStklyrCoil(setRecord, outRecSet, 0);
                    //에러 메시지
                    outRecSet.first() ;
                    outRecord = outRecSet.getRecord() ;
                    
                    //적치불가라면 적치가능으로 업데이트
                    if(outRecord.getFieldString("YD_STK_LYR_MTL_STAT").equals("C")) {
                    
                    	//실적위치Bed 왼쪽 2단 Select
                    	outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
                    	setRecord = JDTORecordFactory.getInstance().create() ;
                    	//실적위치 Bed -1 = 실적위치Bed 왼쪽 2단
       
                        setRecord.setField("YD_STK_COL_GP",       szYD_DN_WR_LOC.substring(0,6));    
                        setRecord.setField("YD_STK_BED_NO",       szYD_DN_WR_LOC.substring(6,8)); 
                        setRecord.setField("YD_STK_LYR_NO",       "002") ;
                        
                        //적치단 상태조회		: 적치불가인지 아닌지 알기위해서...
                        intRtnVal = this.getYdStklyrCoil(setRecord, outRecSet, 0);
                        //에러 메시지
                        outRecSet.first() ;
                        outRecord = outRecSet.getRecord() ;
                        //적치불가라면 적치가능으로 업데이트
                       
                        if(outRecord.getFieldString("YD_STK_LYR_MTL_STAT").equals("X")) {
                        	//실적위치Bed 왼쪽 2단 Check 		적치상태를 적치가능
                        	outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	                	setRecord = JDTORecordFactory.getInstance().create() ;
    	                	//실적위치 Bed -1 = 실적위치Bed 왼쪽 2단
                            setRecord.setField("YD_STK_COL_GP",       szYD_DN_WR_LOC.substring(0,6));    
                            setRecord.setField("YD_STK_BED_NO",       szYD_DN_WR_LOC.substring(6,8)); 
                            setRecord.setField("YD_STK_LYR_NO",       "002") ;
    	                    setRecord.setField("YD_STK_LYR_MTL_STAT", "E");
                        	intRtnVal = this.updYdStklyrCoil(setRecord, 1); 
                        }
                    	
                    }

    	        }
    	        
//    	        intRtnVal = this.setYdWrkplnsimulation(getRecord, 1) ;
//    	        if(intRtnVal <= 0 ) return intRtnVal;
//    	        //에러 메시지
//    	        
//    	        //차량 하차작업 일 경우			공통테이블에 진도코드를 갱신한다.
//    	        if(ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("PT")
//                		|| ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TR")){
//    	        	//진도코드 갱신
//    	        	intRtnVal = this.setProgCode(getRecord) ;
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
//    	        intRtnVal = this.setYdStrLoc(setRecord) ;
//    	        if(intRtnVal <= 0 ) return intRtnVal;
    	        System.out.println("NO3=========================================================");
    	        
                getRecSet.next();
                getRecord = getRecSet.getRecord();
        	
        	}//end of for
        	
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -1 ;
			return intRtnVal ;
	    }//end of try~catch
		
		intRtnVal = 1 ;
		return intRtnVal ;
    
    }//end of regYdStklyrCoil()
    
    
    
    

    
   
    /**
     * 오퍼레이션명 : 적치단 Select
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int getYdStklyrCoil (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
    	
    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	
        try{
        	
	        intRtnVal = ydStklyrDao.getYdStklyr(msgRecord, getRecSet, intGp);  
	        outRecSet.addAll(getRecSet);

        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -1 ;
        }//end of try~catch

        return intRtnVal ;
    	
    }//end of getYdStklyrCoil()
    
    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int updYdStklyrCoil (JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
    	
    	int intRtnVal = 0 ;
        
        try{
        	
	    	intRtnVal = ydStklyrDao.updYdStklyr(msgRecord, intGp);  //적치단의 재료정보 Clear
	    	
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
	    }//end of try~catch
		
		return intRtnVal ;
		
    }//end of updYdStklyrCoil
	

    
    
    
    
    /**
     * 오퍼레이션명 : 작업예약 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● int execution count(성공), 0:no data found, -1:dup_val_on_index, -2:parameter error, -3:execution failed, -4:Exception error
     * @throws ● JDTOException
     */
    public int updYdWrkbookCoil (JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdWrkbookDao ydWrkbookDao   = new YdWrkbookDao(); 
    	
    	int intRtnVal = 0 ;
        
        try{
        	
            intRtnVal = ydWrkbookDao.updYdWrkbook(msgRecord, intGp);

		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -4 ;
			return intRtnVal ;
	    }//end of try~catch
		
		return intRtnVal ;
    }//end of updYdWrkbookCoil
    
    
    
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////   대차관련 METHOD   /////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 오퍼레이션명 : 대차 Setting
     *  
     * @param  ● inRecordSet, intGp(1:상차) 크레인스케줄을 조회한 레코드셋
     * @return ● int '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int setYdTcarCoil (JDTORecordSet inRecordSet, int intGp) throws JDTOException{
    	//Data Setting
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	//data를 받음
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	//대차 스케줄 레코드셋의 레코드값을 받음
    	JDTORecord 	  getTcarRecord 		= JDTORecordFactory.getInstance().create();
    	
    	//대차 스케줄의 레코드셋
    	JDTORecordSet outRecSet  			= JDTORecordFactory.getInstance().createRecordSet("temp");
    	int intRtnVal 						= 0 ;
    	
    	String szMethodName 				= "setYdTcarCoil" ;
    	String szMsg 						= "" ;
    	
    	//대차 스케줄 ID
    	String szYD_TCAR_SCH_ID 			= "" ;
    	
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
	    	intRtnVal = this.getYdTcarschCoil(setRecord, outRecSet, 1) ;
	    	if (intRtnVal <= 0) return intRtnVal ;
	    	
	    	
	    	// 대차스케줄 Data
	    	outRecSet.first() ;
	    	getTcarRecord = outRecSet.getRecord() ;
	    	// 대차스케줄 ID를 추출한다
	    	szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_TCAR_SCH_ID");
	    	
	    	//setRecord 초기화
	    	setRecord 			= JDTORecordFactory.getInstance().create();
	    	int szRowSize = inRecordSet.size(); 
	    	
	    	

	    	// 권상한 재료만큼 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)  

	    	for(int i = 0; i < szRowSize; i++){
		    	
	    		// 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)        
	    		setRecord.setField("YD_TCAR_SCH_ID",       	szYD_TCAR_SCH_ID);
	    		setRecord.setField("STL_NO",              	ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
	    		
	    		//대차 이송재료 등록 (하차 )
	    		if(intGp == 0) {
		    		setRecord.setField("DEL_YN",       			"Y");
		    		intRtnVal = this.updTcarftmvmtlCoil(setRecord, 0) ;
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
			        }
			    	
			    //대차 이송재료 등록 (상차 )	
	    		}else if(intGp == 1) {
		    		setRecord.setField("DEL_YN",       			"N");
		    		setRecord.setField("YD_STK_BED_NO",       	ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(6,8)) ;
		    		setRecord.setField("YD_STK_LYR_NO",       	ydDaoUtils.stringPlusInt(ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER"), i)) ;
		    		intRtnVal = this.insYdTcarftmvmtlCoil(setRecord) ;
			    	switch (intRtnVal) {
			        	case -2	:
			                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
			                return intRtnVal;
			        	case -3	:
			                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return intRtnVal;
			        }
	    		}
		    	
	    		inRecordSet.next() ;
		    	getRecord = inRecordSet.getRecord();
	    	}
	    	
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -1 ;
		}//end of try~catch
    	
		return 1 ;
    	
    }//end of setYdTcarCoil()
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 대차 스케줄 Select
     *  
     * @param  ● msgRecord, intGp(1:상하차)
     * @return ● int record count:성공, 0:no data found, -2:parameter error, -3:Exception error
     * @throws ● 
     */
    public int getYdTcarschCoil (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
    	YdTcarSchDao ydTcarschDao = new YdTcarSchDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	
	        intRtnVal = ydTcarschDao.getYdTcarsch(msgRecord, getRecSet, intGp);
	        if (intRtnVal <= 0) {
	        	return intRtnVal;
	        }
	        outRecset.addAll(getRecSet)  ;  
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }
        return intRtnVal ;
    }//end of getYdTcarschCoil
    

    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 대차이송재료 Update
     *  
     * @param inRecordSet, intGp(1:상차) 크레인스케줄을 조회한 레코드셋
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws JDTOException
     */
    public int updTcarftmvmtlCoil (JDTORecord inRecord, int intGp) throws JDTOException {
    	YdTcarFtmvMtlDao ydTcarftmvmtlDao   = new YdTcarFtmvMtlDao(); 
    	
    	int intRtnVal = 0 ;
        
        try{
        	
            intRtnVal = ydTcarftmvmtlDao.updYdTcarftmvmtl(inRecord, intGp) ;
            
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -3 ;
			return intRtnVal ;
	    }	
		
		return intRtnVal ;
    }//end of updTcarftmvmtlCoil
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 대차이송재료 Insert
     *  
     * @param msgRecord, intGp(1:상하차)
     * @return int execution count, -2:parameter error, -3:Exception error
     * @throws 
     */
    public int insYdTcarftmvmtlCoil(JDTORecord msgRecord){
    	YdTcarFtmvMtlDao ydTcarftmvmtlDao = new YdTcarFtmvMtlDao();
    	
    	int intRtnVal 			= 0 ;

        
        try{
        	
        	intRtnVal = ydTcarftmvmtlDao.insYdTcarftmvmtl(msgRecord);
        	if(intRtnVal == -2) return intRtnVal ;
        	
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }
        return intRtnVal ;
    	
    }//end of insYdTcarftmvmtlCoil
    
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 대차스케줄 Update
     *  
     * @param msgRecord, intGp(0: 대차스케줄ID)
     * @return int '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int updYdTcarschCoil (JDTORecord msgRecord, int intGp){
    	YdTcarSchDao ydTcarschDao = new YdTcarSchDao();
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	
        	intRtnVal = ydTcarschDao.updYdTcarsch(msgRecord, intGp);
	        if (intRtnVal <= 0) {
	        	return -2;
	        }
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
        }//end of try~catch
        
        
        return 1 ;
    	
    }//end of updYdTcarschCoil
    
    
    
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////    차량관련 METHOD    ///////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * 오퍼레이션명 : 차량 Setting
     *  
     * @param inRecordSet, intGp(1:상차) 크레인스케줄을 조회한 레코드셋
     * @return int '1'이상: 성공   '0'이하: 실패
     * @throws JDTOException
     */
    public int setYdCarCoil (JDTORecordSet inRecordSet, int intGp) throws JDTOException{
    	//Data Setting
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//data를 받음
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//차량 스케줄 레코드셋의 레코드값을 받음
    	JDTORecord 	  getTcarRecord 		= JDTORecordFactory.getInstance().create();
    	
    	//차량 스케줄의 레코드셋
    	JDTORecordSet outRecSet  			= JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	String szMethodName 				= "setYdCarCoil" ;
    	String szMsg 						= "" ;
    	
    	int intRtnVal = 0 ;
    	
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
	    	intRtnVal = this.getYdCarschCoil(setRecord, outRecSet, 3) ;
	    	if (intRtnVal <= 0) return -1 ;
	    	
	    	
	    	// 차량스케줄 Data
	    	outRecSet.first() ;
	    	getTcarRecord = outRecSet.getRecord() ;
	    	// 차량스케줄 ID를 추출한다
	    	szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_CAR_SCH_ID");
	    	
	    	//setRecord 초기화
	    	setRecord 			= JDTORecordFactory.getInstance().create();
	    	int szRowSize = inRecordSet.size(); 
	    	
	    	

	    	// 권상한 재료만큼 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)  

	    	for(int i = 0; i < szRowSize; i++){
		    	// 차량스케줄 ID로 차량이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)        
	    		setRecord.setField("YD_CAR_SCH_ID",       	szYD_CAR_SCH_ID);
	    		setRecord.setField("STL_NO",              	ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
	    		
	    		//차량 이송재료 등록 (하차 )
	    		if(intGp == 0) {
		    		setRecord.setField("DEL_YN",       			"Y");
		    		intRtnVal = this.updCarftmvmtlCoil(setRecord, 0) ;
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
		    		setRecord.setField("YD_STK_LYR_NO",       	ydDaoUtils.stringPlusInt(ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER"), i)) ;
		    		intRtnVal = this.insYdCarftmvmtlCoil(setRecord) ;
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
	    	
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -1 ;
		}//end of try~catch
		
    	return 1 ;
    	
    }//end of setYdCarCoil()

    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 차량 스케줄 Select
     *  
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int getYdCarschCoil (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
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
        
    }//end of getYdCarschCoil
    
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 차량 이송재료 Update
     *  
     * @param inRecordSet, intGp(1:상차) 크레인스케줄을 조회한 레코드셋
     * @return int execution count(성공), 0:no data found, -1:dup_val_on_index, -2:parameter error, -3:execution failed -4:Exception error
     * @throws JDTOException
     */
    public int updCarftmvmtlCoil (JDTORecord inRecord, int intGp) throws JDTOException {
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
		
    }//end of updCarftmvmtlCoil
    

    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 차량이송재료 Insert
     *  
     * @param msgRecord, intGp(1:상하차)
     * @return int execution count, -2:parameter error, -3:Exception error
     * @throws 
     */
    public int insYdCarftmvmtlCoil(JDTORecord msgRecord){
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
    	
    }//end of insYdCarftmvmtlCoil


    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 차량스케줄 Update
     *  
     * @param msgRecord, intGp(0: 대차스케줄ID)
     * @return getRecSet
     * @throws 
     */
    public JDTORecordSet updYdCarschCoil (JDTORecord msgRecord, int intGp){
    	YdCarSchDao ydCarschDao = new YdCarSchDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
        String szMsg            = "";
        String szMethodName     = "updYdCarsch";
        
        try{
        	
        	intRtnVal = ydCarschDao.updYdCarsch(msgRecord, intGp);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	        }
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
        }//end of try~catch
        
        
        return getRecSet ;
    	
    }//end of updYdCarschCoil


    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 저장품 Update
     *  
     * @param msgRecord, intGp(0: 대차스케줄ID)
     * @return int execution count(성공), 0:no data found, -1:dup_val_on_index, -2:parameter error, -3:execution failed
     * @throws 
     */
    public int updYdStockCoil (JDTORecord msgRecord, int intGp){
    	YdStockDao ydStockDao = new YdStockDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
        String szMsg            = "";
        String szMethodName     = "craneLdHd";
        
        try{
        	
        	intRtnVal = ydStockDao.updPtComm_LOC(msgRecord, intGp);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	        }
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
        }//end of try~catch
        
        
        return  intRtnVal;
    	
    }//end of updYdStockCoil()
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 저장품 Select
     *  
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int getYdStockCoil (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
    	YdStockDao ydStockDao = new YdStockDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	String szMsg			= "" ;
    	String szMethodName		= "getYdStock" ;
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	
        	intRtnVal = ydStockDao.getYdStock(msgRecord, getRecSet, intGp);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	        }
	        outRecset.addAll(getRecSet)  ; 
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of getYdStockCoil()
    
    


    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 작업계획 Simulation 삭제 Setting  
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int setYdWrkplnsimulationCoil (JDTORecord msgRecord, int intGp){
    	YdWrkPlnSimulationDao ydWrkplnsimulationDao = new YdWrkPlnSimulationDao();
    	//작업계획 Sim 조회 한 값
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	//getRecSet의 첫번째 레코드 값을 저장
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	
    	String szMsg			= "" ;
    	String szMethodName		= "getYdWrkplnsimulation" ;
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	//작업계획 Simulation Select				msgRecord에는 스케줄코드와 재료번호가 있음
        	intRtnVal = this.getYdWrkplnsimulationCoil(msgRecord, getRecSet, intGp);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	            return intRtnVal ;
	        }
	        getRecSet.first();
	        getRecord = getRecSet.getRecord();
	        getRecord.setField("DEL_YN", "Y") ;
	        getRecord.setField("MODIFIER", "SYSTEM") ;
	        
	        intRtnVal = this.updYdWrkplnsimulationCoil(getRecord) ;
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	            return intRtnVal ;
	        }
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of setYdWrkplnsimulationCoil()
    
    


    
    
   
    
    
    /**
     * 오퍼레이션명 : 작업계획 Simulation Select
     *  
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int getYdWrkplnsimulationCoil (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
    	YdWrkPlnSimulationDao ydWrkplnsimulationDao = new YdWrkPlnSimulationDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	String szMsg			= "" ;
    	String szMethodName		= "getYdWrkplnsimulation" ;
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	
        	intRtnVal = ydWrkplnsimulationDao.getYdWrkplnsimulation(msgRecord, getRecSet, intGp);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	            return intRtnVal ;
	        }
	        outRecset.addAll(getRecSet)  ; 
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of getYdWrkplnsimulationCoil()
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 작업계획 Simulation Update
     *  
     * @param msgRecord, int intGp 구분(0:STL_NO,YD_SCH_CD)
     * @return int execution count(성공), -2:parameter error, -3:execution failed
     * @throws 
     */
    public int updYdWrkplnsimulationCoil (JDTORecord msgRecord){
    	YdWrkPlnSimulationDao ydWrkplnsimulationDao = new YdWrkPlnSimulationDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	String szMsg			= "" ;
    	String szMethodName		= "updYdWrkplnsimulation" ;
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	
        	intRtnVal = ydWrkplnsimulationDao.updYdWrkplnsimulationPlnIdAndLess(msgRecord);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == -2) {
	                szMsg = "parameter error	Error code:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -3) {
	                szMsg = "execution failed	Error code:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	            return intRtnVal ;
	        }
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of getYdWrkplnsimulationCoil()
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 진도코드갱신 Setting 
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int setProgCodeCoil (JDTORecord msgRecord){
    	
    	
    	//작업계획 Sim 조회 한 값
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	//공통테이블 정보를 담기위한 값
    	JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	//getRecSet의 첫번째 레코드 값을 저장
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	//Update할 레코드 셋팅
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	String szCurrProgCd					= "" ;
    	String szBefoProgCd					= "" ;
    	
    	String szMsg						= "" ;
    	String szMethodName					= "getYdWrkplnsimulation" ;
    	//재료품목 정의
    	String szYdMtlItem					= "" ;
    	//재료종류별 번호
    	String szStlNo						= "" ;
    	int intRtnVal 						= 0 ;
        
        try{
        	
        	//재료품목으로 비교하고 품목에따라 맞는 공통테이블을 조회한다.
        	szYdMtlItem = msgRecord.getFieldString("YD_SCH_CD").substring(2, 4) ;
        	szStlNo 	= msgRecord.getFieldString("STL_NO") ;
        	
        	if(szYdMtlItem.equals("BM")){
        		//주편 공통
        		msgRecord.setField("MSLAB_NO", szStlNo) ;
        		intRtnVal = this.getYdStockCoil(msgRecord, getRecSet, 6);
        		//에러 메시지
        		//
        		if(intRtnVal<0) return intRtnVal ;
        	}else if (szYdMtlItem.equals("SM") || szYdMtlItem.equals("SG") || szYdMtlItem.equals("SZ")) {
        		//슬라브 공통
        		msgRecord.setField("SLAB_NO", szStlNo) ;
        		intRtnVal = this.getYdStockCoil(msgRecord, getRecSet, 2);
        		//에러 메시지
        		//
        		if(intRtnVal<0) return intRtnVal ;
        	}
        	
        	getRecSet.first();
        	getRecord = getRecSet.getRecord() ;
        	//읽어온 값의 항목을 저장
        	szCurrProgCd = getRecord.getFieldString("CURR_PROG_CD") ;
        	szBefoProgCd = getRecord.getFieldString("BEFO_PROG_CD") ;
        	
        	//현재진도코드 = 이송지시대기'D'
        	if(getRecord.getFieldString("CURR_PROG_CD").equals("D")) {
        		if(getRecord.getFieldInt("HRSHR_WO_ORDRMN_GP")== 1){
        			if(getRecord.getFieldString("SCARFING_YN").equals("Y")) {
        				//정정작업대기(작업대기)
        				setRecord.setField("CURR_PROG_CD", "C") ;
        			}else{
        				//압연지시대(지시대기)
        				setRecord.setField("CURR_PROG_CD", "B") ;
        			}
        		}else{
        			//충당대기
        			setRecord.setField("CURR_PROG_CD", "Z") ;
        		}
        	}
        	//전 진도코드 = 현재 진도코드,   전전진도코드 = 전진도코드
			setRecord.setField("BEFO_PROG_CD", 					szCurrProgCd) ;
			setRecord.setField("BEFOBEFO_PROG_CD",  			szBefoProgCd) ; 
			//현재시간
			setRecord.setField("CURR_PROG_REG_DDTT", 			ydUtils.getCurDate("yyyyMMddHHmmss")) ;
			setRecord.setField("BEFO_PROG_REG_DDTT",  			ydUtils.getCurDate("yyyyMMddHHmmss")) ; 
			setRecord.setField("BEFOBEFO_PROG_REG_DDTT", 		ydUtils.getCurDate("yyyyMMddHHmmss")) ;

			

        	//업데이트 항목 추가해야함 ///////////////////////////////////////////////////////
        	if(szYdMtlItem.equals("BM")){
        		//주편 공통
        		//구분자 설정 다시해야함!
        		intRtnVal = this.updYdStockCoil(msgRecord,  6);
        		//에러 메시지
        		//
        		if(intRtnVal<0) return intRtnVal ;
        	}else if (szYdMtlItem.equals("SM") || szYdMtlItem.equals("SG") || szYdMtlItem.equals("SZ")) {
        		//슬라브 공통
        		intRtnVal = this.updYdStockCoil(msgRecord,  2);
        		//에러 메시지
        		//
        		if(intRtnVal<0) return intRtnVal ;
        	}
        	
        	
        	
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of setProgCodeCoil()

    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 비상조업
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void craneEmerOper(JDTORecord msgRecord)throws JDTOException  {
        //////////////////////
    	//TCCODE :	Y1YD012//
    	//////////////////////
    	
    	//업데이트 할 크레인 스케줄 Data 항목 set
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();
        //적치단클리어시 업데이트 항목
        JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        //파라미터 null check 후 받아온 Data
        JDTORecord getParamRecord = JDTORecordFactory.getInstance().create();


        //스케줄Table의 컬럼을 저장하기위해 생성
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
        //레코드 셋의 레코드값을 받음
        JDTORecord getRecord = JDTORecordFactory.getInstance().create();

        int intRtnVal 					= 0 ;
        int intRowsize					= 0 ;
        
        String szMsg              		= "";
        String szMethodName       		= "craneEmerOper";
        
        
        
        
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



	        //파라미터 check
        	//새로 만들어야 하는 부분
	        intRtnVal = this.emerOperParamCheck(msgRecord, getParamRecord) ;
	        if(intRtnVal == -1) {
                szMsg = "파라미터 Check중 Error	: " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
	        }
	        

	        setCrnschRecord.setField("YD_CRN_SCH_ID",         getParamRecord.getFieldString("YD_CRN_SCH_ID"));
	        setCrnschRecord.setField("YD_SCH_CD",             getParamRecord.getFieldString("YD_SCH_CD")) ;
	        setCrnschRecord.setField("MODIFIER",              "SYSTEM") ;
	        setCrnschRecord.setField("YD_EQP_ID",             getParamRecord.getFieldString("YD_EQP_ID")) ;
	        setCrnschRecord.setField("YD_UP_WR_LOC",          getParamRecord.getFieldString("YD_UP_WR_LOC")) ;
	        setCrnschRecord.setField("YD_UP_WR_LAYER",        getParamRecord.getFieldString("YD_UP_WR_LAYER")) ;
	        setCrnschRecord.setField("YD_DN_WR_LOC",          getParamRecord.getFieldString("YD_DN_WR_LOC")) ;
	        setCrnschRecord.setField("YD_DN_WR_LAYER",        getParamRecord.getFieldString("YD_DN_WR_LAYER")) ;
	        
	        
	        //크레인 스케줄의 Insert하기위해 스케줄의 항목의 값을 Setting하고 업데이트한다.
	        intRtnVal = this.insYdCrnsch(setCrnschRecord) ;
	        if (intRtnVal == 2) {
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        }

        
	        //크레인작업재료 Insert
			setRecord.setField("YD_CRN_SCH_ID", getParamRecord.getFieldString("YD_CRN_SCH_ID")) ;
			setRecord.setField("STL_NO", 		getParamRecord.getFieldString("STL_NO")) ;
			
	        intRtnVal = this.insYdCrnWrkMtl(setRecord) ;
	        if (intRtnVal == 2) {
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        }



	        //from위치정리
	        if(getParamRecord.getFieldString("STL_NO").equals("") || getParamRecord.getFieldString("STL_NO") == null) {
                szMsg = "'STL_NO' Data Error	: 재료번호가 없습니다." ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
	        }	        


	        //대상 데이터 SELECT			재료번호로 적치단 조회
	        intRtnVal = this.getYdStklyr(getParamRecord, getRecSet,3);
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "no data found!!!";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	        	case -2	:
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        }

	        
	        intRowsize = getRecSet.size() ;
	        
	        getRecSet.first();
	        //레코드셋의 사이즈값으로 ErrorCheck
	        if(intRowsize == 0){
	            szMsg = "적치단에 등록된 재료번호가 없습니다." ;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	        }
	        getRecord = getRecSet.getRecord();
		        
	        if(intRowsize > 0){

		        for(int i=0; i<intRowsize; i++) {
		        //클리어셋팅
		        	setRecord = JDTORecordFactory.getInstance().create();
		        	setRecord.setField("YD_STK_COL_GP", 		getRecord.getFieldString("YD_STK_COL_GP")) ;
		        	setRecord.setField("YD_STK_BED_NO", 		getRecord.getFieldString("YD_STK_BED_NO")) ;
		        	setRecord.setField("YD_STK_LYR_NO", 		getRecord.getFieldString("YD_STK_LYR_NO")) ;
		        	setRecord.setField("STL_NO",      		    "") ;
		        	setRecord.setField("MODIFIER",      		"SYSTEM") ;
		        	setRecord.setField("YD_STK_LYR_ACT_STAT", 	"E") ;
		        	setRecord.setField("YD_STK_LYR_MTL_STAT", 	"E") ;

//==========================================================================================                    
//                  기준이기 때문에 클리어 되면 안됨
//                  2009.09.25 권오창
//                    
//		        	setRecord.setField("YD_STK_LYR_XAXIS", 		"") ;
//		        	setRecord.setField("YD_STK_LYR_YAXIS", 		"") ;
//		        	setRecord.setField("YD_STK_LYR_ZAXIS", 		"") ;
//==========================================================================================                    

		        	//적치단 업데이트
		        	intRtnVal = this.updYdStklyr(setRecord, 0) ;
			        switch (intRtnVal) {
			        	case 0	:
			                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
			        	case -1	:
			                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return ;
			        	case -2	:
			                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
			                return ;
			        	case -3	:
			                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return ;
			        }	        
		        
			        getRecSet.next();
			        getRecord = getRecSet.getRecord();
		        }//end of for
	        }//end of if
	        
	        
	        
	        //적치단에 재료의 실적위치에 실적정보를 등록한다.
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_STK_COL_GP", 		getParamRecord.getFieldString("YD_DN_WR_LOC").substring(0,6)) ;
	        setRecord.setField("YD_STK_BED_NO", 		getParamRecord.getFieldString("YD_DN_WR_LOC").substring(6,8)) ;
	        setRecord.setField("YD_STK_LYR_NO", 		getParamRecord.getFieldString("YD_DN_WR_LAYER")) ;
	        setRecord.setField("MODIFIER", 				"SYSTEM") ;
	        setRecord.setField("STL_NO", 				getParamRecord.getFieldString("STL_NO")) ;
	        setRecord.setField("YD_STK_LYR_MTL_STAT", 	"C") ;
	        intRtnVal = this.updYdStklyr(setRecord, 0) ;
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	        	case -1	:
	                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        	case -2	:
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        	case -3	:
	                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        }
	        
		        
		        
	            
	            
	        szMsg="C연주 크레인 비상조업  실적 등록 완료";
	        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	    }catch(Exception e) {
	    	System.out.println("Error :  "+ e.getLocalizedMessage());
	    }//end of try~catch
	    
    }// end of craneEmerOper()
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 비상조업실적등록 파라미터 체크
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int emerOperParamCheck (JDTORecord msgRecord, JDTORecord outRecord) throws JDTOException {
    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        String szMsg                        = "" ;
        String szMethodName                 = "craneUdHd";
        int intRtnVal = 0 ;
        
    	try{
            
    		setRecord.setField("MSG_ID"          		, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_ID")) ;
			setRecord.setField("YD_CRN_SCH_ID"			, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID")) ;
			setRecord.setField("YD_SCH_CD"        		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD")) ;
			setRecord.setField("STL_NO"          		, ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO")) ;
			setRecord.setField("YD_EQP_ID"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID")) ;
			setRecord.setField("YD_UP_WR_LOC"       	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LOC")) ;
			setRecord.setField("YD_UP_WR_LAYER"         , ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LAYER")) ;
			setRecord.setField("YD_DN_WR_LOC"         	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LOC")) ;
			setRecord.setField("YD_DN_WR_LAYER"         , ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LAYER")) ;

    		
    		outRecord.addRecord(setRecord) ;
            
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -1 ;
			return intRtnVal;
        }//end of try~catch
        
        intRtnVal = 1 ;
        return intRtnVal;
        
    }//end of ParamCheck()
    
    
    
    
    
    
    /**
	 * 오퍼레이션명 : C연주크레인스케줄Main
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void CrnSchMain(JDTORecord msgRecord)throws JDTOException  {
	
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		YdDBAssist ydDBAssist = new YdDBAssist();
		
		JDTORecordSet outRecset    = JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsWrkbookmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
		//임시용
		JDTORecord recImsi = null;
		JDTORecordSet rsOut = null;
		JDTORecord recSeq = null;
		
		
		
		//Vector 선언
		Vector vecResult      = new Vector();
		Vector vecReResult     = new Vector();
		
		String szFieldName  = null;
		String szMsg        = "";
		String szMethodName = "CrnSchMain";
		//설비Id
		String szEqpId      = "";
		//스케줄코드
		String szSchCd      = "";
		
		String szQuery      = "";
		
		//true false 체크
		boolean bRtnCheck   = true;

		int intRtnVal       = 0;

		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode==null){
			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			return;
		}
		if(bDebugFlag){
			szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try{
			
			//전문의 설비Id
			szEqpId = ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID");
			//전문의 스케줄코드
			szSchCd = ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD");



			//C연주크레인스케줄수행조건판단
			intRtnVal = this.chkCrnSchEffectCondition(szEqpId, szSchCd, rsWrkbookmtl);
			if(intRtnVal == -1) return;
			
			//그룹핑 파라미터 셋팅
			intRtnVal = this.CrnSchSort(rsWrkbookmtl, outRecset);
			if(intRtnVal == -1) return;
System.out.println("outRecset =" + outRecset.size());
			
			//Handling Lot 편성
			intRtnVal = this.CrnSchDataHandling(outRecset, vecResult);
			if(intRtnVal == -1) return;

			//크레인사양 비교Check
			intRtnVal = this.chkHandledDataCrnSpec(szEqpId, vecResult, vecReResult);
			if(intRtnVal == -1) return;
			
			//크레인 스케줄 및 크레인 작업재료 등록
			intRtnVal = this.CrnSchIns(vecReResult, msgRecord);
			if(intRtnVal == -1) return;
			
			
		} catch (Exception e) {
			
			szMsg="[ERROR] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		
		}	// end try catch문
		
		szMsg="C연주크레인스케줄수신("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of CrnSchMain()
	
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : C연주크레인스케줄수행조건판단
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szEqpId, szSchCd, rsResultRt
	 * @return 1: 성공, -1: 실패
	 * @throws JDTOException
	 */
	public int chkCrnSchEffectCondition(String szEqpId, String szSchCd, JDTORecordSet rsResultRt) throws JDTOException  {
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		YdWrkbookMtlDao ydWrkbookmtlDao = new YdWrkbookMtlDao();
		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();
		
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		
		JDTORecord recSetWrkbookmtl = null;
		JDTORecordSet rsWrkbookmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		
		JDTORecord recParaSch  = null;
		JDTORecord recSch      = null;
		
		JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		JDTORecord recSchCd   = null;
		JDTORecordSet rsSchCd = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		String szFieldName  = null;
		String szMsg        = "";
		String szMethodName = "chkCrnSchEffectCondition";
		String szQuery      = "";
		//true false 체크
		boolean bRtnCheck   = true;

		int intRtnVal       = 0;
		
		
		JDTORecord recPara      = null;
		

		try{
			//작업예약Table조회 파라미터 Data Setting
			recSetWrkbookmtl = JDTORecordFactory.getInstance().create();
			recSetWrkbookmtl.setField("ROWNUM", "2");
			recSetWrkbookmtl.setField("YD_EQP_ID", szEqpId);
			recSetWrkbookmtl.setField("YD_SCH_CD", szSchCd);
			
			// 야드설비상태 Check		수신받은  야드설비Id로 설비Table를 조회하여 야드설비상태를 Check하고 고장이면 return
			bRtnCheck = this.eqpStatCheck(szEqpId);
			if(!bRtnCheck) return intRtnVal = -1;
			
			
			if(szSchCd.equals("")){
			
				
				/* 쿼리수정_요청
				szQuery  = "	SELECT YD_WBOOK_ID                                ";
				szQuery += "	      ,YD_SCH_CD                                  ";
				szQuery += "	  FROM (SELECT YD_WBOOK_ID                        ";
				szQuery += "	              ,YD_SCH_CD                          ";
				szQuery += "	          FROM TB_YD_WRKBOOK                      ";
				szQuery += "	         WHERE (DEL_YN <> 'Y' OR DEL_YN IS NULL)  ";
				szQuery += "			     ORDER BY YD_SCH_PRIOR, YD_WBOOK_ID)      ";
				szQuery += "	 WHERE ROWNUM < '2'                               ";
				intRtnVal = ydDBAssist.getData(szQuery, rsSchCd, null) ;				
				
				*/
				
				//com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.chkCrnSchEffectCondition01 
				recPara = JDTORecordFactory.getInstance().create();				
				intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, rsSchCd, 22);
				
				rsSchCd.absolute(1);
				recSchCd = rsSchCd.getRecord();
				szSchCd = recSchCd.getFieldString("YD_SCH_CD");
		
				//조회항목 record 생성
				recParaSch = JDTORecordFactory.getInstance().create();
				
				//스케줄코드
				recParaSch.setField("YD_SCH_CD", szSchCd);
				
				//스케줄코드로 스케줄기준 Table 조회
				intRtnVal = ydSchRuleDao.getYdSchrule(recParaSch, rsResult, 0);
				if(intRtnVal<=0){
	    			if(intRtnVal == 0) {
	    				szMsg = "data not found";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -2) {
	    				szMsg = "parameter error";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
					return intRtnVal = -1;
				}
				for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
					rsResult.absolute(Loop_i);
					recSch = rsResult.getRecord();
					
					if(recSch.getFieldString("YD_SCH_PROH_EXN").equals("Y")) {
						System.out.println("스케줄 금지 상태입니다.");
						return intRtnVal = -1;
					}
				}
				
				
				
				/*
			
				// 작업예약 Table조회		스케줄코드가 없을 경우 작업예약Table를 야드스케줄순위와 야드작업예약Id가 빠른순서로 조회하여 첫번째 야드작업예약Id를 읽어온다.
				szQuery  = "	SELECT YD_WBOOK_ID   AS YD_WBOOK_ID                                     ";
				szQuery += "	      ,YD_STK_COL_GP      AS YD_STK_COL_GP                              ";
				szQuery += "	      ,YD_STK_BED_NO      AS YD_STK_BED_NO                              ";
				szQuery += "	      ,MIN(YD_STK_LYR_NO) AS YD_STK_LYR_NO                              ";
				szQuery += "	  FROM TB_YD_WRKBOOKMTL                                                 ";
				szQuery += "	 WHERE YD_WBOOK_ID = (SELECT YD_WBOOK_ID                                ";
				szQuery += "	                        FROM (SELECT YD_WBOOK_ID                        ";
				szQuery += "	                                FROM TB_YD_WRKBOOK                      ";
				szQuery += "	                               WHERE (DEL_YN <> 'Y' OR DEL_YN IS NULL)  ";
				szQuery += "	                               ORDER BY YD_SCH_PRIOR, YD_WBOOK_ID)      ";
				szQuery += "	                       WHERE ROWNUM < 2 )                               ";
				szQuery += "	   AND (DEL_YN <> 'Y' OR DEL_YN IS NULL)                                ";
				szQuery += "	 GROUP BY YD_WBOOK_ID, YD_STK_COL_GP, YD_STK_BED_NO                     ";
				szQuery += "  ORDER BY YD_STK_COL_GP, YD_STK_BED_NO                                     ";
				intRtnVal = ydDBAssist.getData(szQuery, rsWrkbookmtl, null) ;
				*/
				
				
				// com.inisteel.cim.yd.dao.ydWrkbookMtlDao.chkCrnSchEffectCondition02 
				intRtnVal = ydWrkbookmtlDao.getYdWrkbookmtl(recSetWrkbookmtl, rsWrkbookmtl, 30);
				
				
//				
//				intRtnVal = ydWrkbookmtlDao.getYdWrkbookmtl(recSetWrkbookmtl, rsWrkbookmtl, 4);
//				if(intRtnVal<=0){
//	    			if(intRtnVal == 0) {
//	    				szMsg = "data not found";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	    			}else if(intRtnVal == -2) {
//	    				szMsg = "parameter error";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	    			}
//					return intRtnVal = -1;
//				}
				
			} else {
				
				//조회항목 record 생성
				recParaSch = JDTORecordFactory.getInstance().create();
				
				//스케줄코드
				recParaSch.setField("YD_SCH_CD", szSchCd);

				//스케줄코드로 스케줄기준 Table 조회
				intRtnVal = ydSchRuleDao.getYdSchrule(recParaSch, rsResult, 0);
				if(intRtnVal<=0){
	    			if(intRtnVal == 0) {
	    				szMsg = "data not found";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -2) {
	    				szMsg = "parameter error";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
					return intRtnVal = -1;
				}
				
System.out.println("rsResult 사이즈 : " + rsResult.size());				
				for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
					rsResult.absolute(Loop_i);
					recSch = rsResult.getRecord();
					
					if(recSch.getFieldString("YD_SCH_PROH_EXN").equals("Y")) {
						System.out.println("스케줄 금지 상태입니다.");
						return intRtnVal = -1;
					}
				}
				
				rsWrkbookmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
				// 작업예약 Table조회		수신받은 스케줄 코드로 작업예약Table를 조회하여 첫번째 야드작업예약Id를 읽어온다.
				//작업예약재료 조회를 미리 해놓는다.(최하단정보만)
			
				
				/*쿼리수정_요청
				szQuery  = "	SELECT YD_WBOOK_ID        AS YD_WBOOK_ID                               ";
				szQuery += "	      ,YD_STK_COL_GP      AS YD_STK_COL_GP                             ";
				szQuery += "	      ,YD_STK_BED_NO      AS YD_STK_BED_NO                             ";
				szQuery += "	      ,MIN(YD_STK_LYR_NO) AS YD_STK_LYR_NO                             ";
				szQuery += "	  FROM TB_YD_WRKBOOKMTL                                                ";
				szQuery += "	 WHERE YD_WBOOK_ID = (SELECT YD_WBOOK_ID                               ";
				szQuery += "	                        FROM (SELECT YD_WBOOK_ID                       ";
				szQuery += "	                                FROM TB_YD_WRKBOOK                     ";
				szQuery += "	                               WHERE YD_SCH_CD = '" + szSchCd +     "' ";
				szQuery += "	                                 AND (DEL_YN <> 'Y' OR DEL_YN IS NULL) ";
				szQuery += "	                               ORDER BY YD_WBOOK_ID)                   ";
				szQuery += "	                       WHERE ROWNUM < '2')                             ";
				szQuery += "	   AND (DEL_YN <> 'Y' OR DEL_YN IS NULL)                               ";
				szQuery += "	 GROUP BY YD_WBOOK_ID, YD_STK_COL_GP, YD_STK_BED_NO                    ";
				szQuery += "	 ORDER BY YD_STK_COL_GP, YD_STK_BED_NO DESC                            ";
				intRtnVal = ydDBAssist.getData(szQuery, rsWrkbookmtl, null) ;
				*/
				recSetWrkbookmtl = JDTORecordFactory.getInstance().create();
				recSetWrkbookmtl.setField("YD_SCH_CD", szSchCd);
				recSetWrkbookmtl.setField("ROWNUM",    new Integer(2));				
				intRtnVal = ydWrkbookmtlDao.getYdWrkbookmtl(recSetWrkbookmtl, rsWrkbookmtl, 3); 
				
//				if(intRtnVal<=0){
//	    			if(intRtnVal == 0) {
//	    				szMsg = "data not found";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	    			}else if(intRtnVal == 2) {
//	    				szMsg = "parameter error";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	    			}
//					return intRtnVal = -1;
//				}
			
			}//end of if
	
			
			rsResultRt.addAll(rsWrkbookmtl); 
			
			return intRtnVal = 1;
			
		} catch (Exception e) {
			
			szMsg="Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return intRtnVal = -1;
		
		}	// end try catch문


	} // end of chkCrnSchEffectCondition()
	
	
	
	
	
	
	
	
	/**
     * 오퍼레이션명 : C연주스케줄링 Handling Data Check
     *  
     * @param  ● msgRecSet, msgRec, vResult
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int CrnSchDataHandling (JDTORecordSet msgRecSet, Vector vResult) throws JDTOException {
		
		JDTORecord recPara       = null;
		JDTORecordSet rsHandling = null;

		String szMsg="";
		String szMethodName="CrnSchDataHandling";
		String szFieldName = null;
		
		//이전 재료의 To위치 결정방법
		String szBefoToLocDcsnMtd = "";

		int intYdUpCollSeq = 0;
		int intCurrBedCnt = 0;
		int intBefoBedCnt = 0;
		
		int intBefoCollSeq = 0;		//이전작업의 권상 모음 순서
		int intRtnVal = 0;

		try{
    		

	    		for (int Loop_i = 1; Loop_i <= msgRecSet.size(); Loop_i++) {
	    			msgRecSet.absolute(Loop_i);
	    			recPara = msgRecSet.getRecord();
	    			//권상모음순서
	    			intYdUpCollSeq = ydDaoUtils.paraRecChkNullInt(recPara, "YD_UP_COLL_SEQ");
	    			intCurrBedCnt = ydDaoUtils.paraRecChkNullInt(recPara, "BED_CNT");
	    			
	    			
	    			//새그룹 생성
	    			if (Loop_i == 1) {
	    				
	    				rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    				rsHandling.addRecord(recPara);
	    				vResult.add(rsHandling) ;
	    				intBefoCollSeq = intYdUpCollSeq;
	    				intBefoBedCnt  = intCurrBedCnt;
	    				szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
	    				
	    			} else {
	    				//새그룹 생성
	    				if (intCurrBedCnt != intBefoBedCnt) {
		    				rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    				rsHandling.addRecord(recPara);
		    				vResult.add(rsHandling) ;
		    				intBefoCollSeq = intYdUpCollSeq;
		    				intBefoBedCnt  = intCurrBedCnt;
		    				szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
		    				continue;
	    				
		    			//새그룹 생성	
	    				}else if (intYdUpCollSeq > 0 && intBefoCollSeq == 0) {
	    					rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    				rsHandling.addRecord(recPara);
		    				vResult.add(rsHandling);
		    				intBefoCollSeq = intYdUpCollSeq;
		    				intBefoBedCnt  = intCurrBedCnt;
		    				szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
		    				continue;
		    			//새그룹 생성
	    				} else if (intYdUpCollSeq == 0 && intBefoCollSeq > 0) {
	    					
	    					rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    				rsHandling.addRecord(recPara);
		    				vResult.add(rsHandling);
		    				intBefoCollSeq = intYdUpCollSeq;
		    				intBefoBedCnt  = intCurrBedCnt;
		    				szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
		    				continue;
		    			//기존 그룹에 추가
	    				} else if (intYdUpCollSeq == 0 && intBefoCollSeq == 0) {
	    					
	    					rsHandling.addRecord(recPara);
	    					intBefoCollSeq = intYdUpCollSeq;
		    				intBefoBedCnt  = intCurrBedCnt;
	    					szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
	    					continue;
	    				//기존 그룹에 추가
	    				} else if (intYdUpCollSeq > 0 && intBefoCollSeq > 0) {
	    					
	    					//권상모음순서비교
	    					if(intYdUpCollSeq == intBefoCollSeq+1) {
	    						//권상모음순서가 연속되더라도 To위치결정방법이 B,R이라면 다른 그룹으로 형성한다.
	    						if(recPara.getFieldString("YD_TO_LOC_DCSN_MTD").equals("R") 
	    								|| recPara.getFieldString("YD_TO_LOC_DCSN_MTD").equals("T")) {
	    							
		    						rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
				    				rsHandling.addRecord(recPara);
				    				vResult.add(rsHandling);
				    				intBefoCollSeq = intYdUpCollSeq;
				    				intBefoBedCnt  = intCurrBedCnt;
				    				szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
				    				continue;
	    						//권상모음순서가 연속되고 To위치결정방법이 'A'라면 같은 그룹으로 형성한다.
	    						}else if(recPara.getFieldString("YD_TO_LOC_DCSN_MTD").equals("A") ) {
	    							rsHandling.addRecord(recPara);
	    							intBefoCollSeq = intYdUpCollSeq;
				    				intBefoBedCnt  = intCurrBedCnt;
	    							szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
	    							continue;
	    						}
	    						
	    						//권상모음순서가 S일때..
	    						if( recPara.getFieldString("YD_TO_LOC_DCSN_MTD").equals("S")) {
	    							//이전 권상모음 순서가 S인경우
	    							if(szBefoToLocDcsnMtd.equals("A")) {
	    								//같은 그룹
	    								rsHandling.addRecord(recPara);
	    								intBefoCollSeq = intYdUpCollSeq;
					    				intBefoBedCnt  = intCurrBedCnt;
	    								szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
	    								continue;
	    							//이전 권상모음 순서가 S가 아닌경우	
	    							}else{
	    								//다른그룹
			    						rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
					    				rsHandling.addRecord(recPara);
					    				vResult.add(rsHandling);
					    				intBefoCollSeq = intYdUpCollSeq;
					    				intBefoBedCnt  = intCurrBedCnt;
					    				szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
					    				continue;
	    								
	    							}
	    						}
	    						
	    						
	    						
	    						
	    						//권상모음순서가 B일때
	    						if( recPara.getFieldString("YD_TO_LOC_DCSN_MTD").equals("B")) {
	    							//이전 권상모음 순서가 A인경우
	    							if(szBefoToLocDcsnMtd.equals("A")) {
	    								//같은 그룹
	    								rsHandling.addRecord(recPara);
	    								intBefoCollSeq = intYdUpCollSeq;
					    				intBefoBedCnt  = intCurrBedCnt;
					    				szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
					    				continue;
	    							//이전 권상모음 순서가 S가 아닌경우	
	    							}else{
	    								//다른그룹
			    						rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
					    				rsHandling.addRecord(recPara);
					    				vResult.add(rsHandling);
					    				intBefoCollSeq = intYdUpCollSeq;
					    				intBefoBedCnt  = intCurrBedCnt;
					    				szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
					    				continue;
	    							}
	    						}
	    					
	    						
	    						//권상모음순서가 M일때..
	    						if( recPara.getFieldString("YD_TO_LOC_DCSN_MTD").equals("M")) {
	    							//이전 권상모음 순서가 S인경우
	    							if(szBefoToLocDcsnMtd.equals("A")) {
	    								//같은 그룹
	    								rsHandling.addRecord(recPara);
	    								intBefoCollSeq = intYdUpCollSeq;
					    				intBefoBedCnt  = intCurrBedCnt;
	    								szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
	    								continue;
	    							//이전 권상모음 순서가 S가 아닌경우	
	    							}else{
	    								//다른그룹
			    						rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
					    				rsHandling.addRecord(recPara);
					    				vResult.add(rsHandling);
					    				intBefoCollSeq = intYdUpCollSeq;
					    				intBefoBedCnt  = intCurrBedCnt;
					    				szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
					    				continue;
	    								
	    							}
	    						}
	    						
	    						//권상모음순서가 A일때..
	    						if( recPara.getFieldString("YD_TO_LOC_DCSN_MTD").equals("A")) {
	    							//이전 권상모음 순서가 S인경우
	    							if(szBefoToLocDcsnMtd.equals("A")) {
	    								//같은 그룹
	    								rsHandling.addRecord(recPara);
	    								intBefoCollSeq = intYdUpCollSeq;
					    				intBefoBedCnt  = intCurrBedCnt;
	    								szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
	    								continue;
	    							//이전 권상모음 순서가 S가 아닌경우	
	    							}
	    						}
	    						
	    					}else{
		    					//새 그룹
	    						rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    				rsHandling.addRecord(recPara);
			    				vResult.add(rsHandling);
			    				intBefoCollSeq = intYdUpCollSeq;
			    				intBefoBedCnt  = intCurrBedCnt;
		    					szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
		    					continue;
	    					}

	    				//에러	
	    				} else {
	    					System.out.println("주작업 보조작업 판단 중 - Error ");
	    					return intRtnVal = -1;
	    				}	    					
	    			}
	    		}//end of for

			System.out.println("주작업 보조작업 판단 끝  ");
    		return intRtnVal = 1;


	
        }catch(Exception e){
			System.out.println("주작업 보조작업 판단 중 - Exception Error : "+ e.getLocalizedMessage());
			return intRtnVal = -1 ;
		
        }//end of try~catch
        
    }//end of CrnSchDataHandling()
	

    
    
    
    
    
    
    /**
     * 오퍼레이션명 : C연주스케줄링 Handling Data 크레인사양Check
     *  
     * @param  ● msgRecSet, msgRec
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int chkHandledDataCrnSpec (String szEqpId, Vector vecHandledData, Vector vecResult) throws JDTOException {
    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
    	YdDaoUtils ydDaoUtils = new YdDaoUtils();
    	
		JDTORecord    recPara    = null;
		JDTORecordSet rsPara 	 = null;
		JDTORecordSet rsMain 	 = null;
		JDTORecordSet rsSub 	 = null;
		JDTORecordSet rsHandling = null;
		
		String szMsg="";
		String szMethodName="CrnSchDataHandling";
		String szFieldName = null;
		

		int intRtnVal = 0;
		
		boolean blnRtnVal = false;
		//최대 폭			
		long lngMaxWidth  = 0;
		//현재 폭
		long lngCurrWidth = 0;	
		//중량의 합
		long lngSumWt     = 0;		
		//현재 중량
		long lngCurrWt    = 0;			
		//재료매수
		int intMtlSh      = 0;	
		
    	
		try{	
			System.out.println("C연주스케줄링 Handling Data 크레인사양Check 시작 ");
			//크레인 사양과 비교 Check
			System.out.println("vecHandledData.size(): " + vecHandledData.size());
    		for(int Loop_i = 0; Loop_i < vecHandledData.size(); Loop_i++) {
    			//폭,중량,매수 초기화
    			lngCurrWidth = 0;
    			lngMaxWidth = 0;
    			lngCurrWt = 0;
    			lngSumWt = 0;
    			intMtlSh = 0;
    			
    			rsPara = (JDTORecordSet)vecHandledData.get(Loop_i) ;
    			rsPara.first();
    			rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");
    			//새그룹 생성
    			vecResult.add(rsMain);
    			
    			for(int Loop_j = 0; Loop_j < rsPara.size(); Loop_j++) {
    				
    				//rsParac의 레코드를 읽어온다.
    				recPara = rsPara.getRecord();
    				//재료의 현재 폭
    				lngCurrWidth = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_W");
    				//재료의 현재 중량
    				lngCurrWt = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");
    				//누적중량
    				lngSumWt = lngSumWt + lngCurrWt;
    				//현재 재료 매수
    				intMtlSh++;

    				
    				intRtnVal = this.chkGetCrnspec(lngCurrWidth, lngMaxWidth, lngSumWt, intMtlSh, szEqpId);
    				//기존그룹 추가
    				if (intRtnVal == 1) {
    					recPara = rsPara.getRecord();
        				rsMain.addRecord(recPara);
        				//최대 폭
        				if(lngCurrWidth > lngMaxWidth) lngMaxWidth = lngCurrWidth;
    				
    				//새그룹 생성
    				} else if (intRtnVal == -1){
    					recPara = rsPara.getRecord();
    					rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");
        				rsMain.addRecord(recPara);
    					vecResult.add(rsMain);
    					//누적중량에 현재중량 대입
    					lngSumWt = lngCurrWt;
    					//최대폭에 = 현재 폭 대입
    					lngMaxWidth = lngCurrWidth;
    					intMtlSh = 1;
    					
    				} else {
    					//Error 처리
    					System.out.println("C연주스케줄링 Handling Data 크레인사양Check중 - Error");
    					return intRtnVal = -1;
    				}
    				
    				
	    			rsPara.next();

    			}//end of infor
    			
    		}//end of outfor
    		
			return intRtnVal = 1;
	
		}catch (Exception e) {
			System.out.println("C연주스케줄링 Handling Data 크레인사양Check중 - Exception Error : "+ e.getLocalizedMessage());
			return intRtnVal = -1 ;
		}
    }//end of chkHandledDataCrnSpec()
    
    
    
    
    
    
    
    
	/**
	 * 오퍼레이션명 : 크레인사양과 비교 체크
	 *  
	 * @param  String     현재폭, 최대폭, 누적중량, 재료매수, 설비Id
	 * @return boolean    1: 기존그룹처리  -1: 새그룹처리 -3: Exception Error 
	 * @throws JDTOException
	 */
	public int chkGetCrnspec(long  lngCurrWidth, long  lngMaxWidth, long lngSumWt, int intMtlSh, String szEqpId)throws JDTOException  {

		JDTORecord recCrnSpec = null;
		JDTORecordSet rsResult = null;
		
		String szMsg              = null;
		String szMethodName       = "chkGetCrnspec";

		int intRtnVal             = 0;
		
		//크레인 집게폭 오차
		int intCrnTongWTol        = 0;
		//크레인 허용 중량
		long lngWrkAbleWt         = 0;
		//크레인 허용 매수
		int intWrkAbleSh          = 0;

		boolean blnRtnVal         = false;
		JDTORecord recPara        = null;
		
		try {
			//레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			//크레인 사양 Select
			blnRtnVal = this.chkGetCrnSpec(szEqpId, rsResult);
			if(!blnRtnVal) return intRtnVal = -1;
			
			//레코드 추출
			rsResult.first() ;
			recCrnSpec = rsResult.getRecord();
			
			//크레인 집게허용 오차
			intCrnTongWTol = ydDaoUtils.paraRecChkNullInt(recCrnSpec,  "YD_CRN_TONG_W_TOL");
			//크레인 작업가능 중량
			lngWrkAbleWt   = ydDaoUtils.paraRecChkNullLong(recCrnSpec, "YD_WRK_ABLE_WT");
			//크레인 작업가능 매수
			intWrkAbleSh   = ydDaoUtils.paraRecChkNullInt(recCrnSpec,  "YD_WRK_ABLE_SH");
			
			
			//크레인사양의 집게허용 오차 Check
			if(lngMaxWidth > lngCurrWidth + intCrnTongWTol) return intRtnVal = -1;

			//크레인 작업가능 중량 Check
			if(lngWrkAbleWt < lngSumWt) return intRtnVal = -1;
							
			//크레인 작업가능 매수 Check
			if (intWrkAbleSh < intMtlSh) return intRtnVal = -1;
			
			return intRtnVal = 1;
			
		} catch(Exception e) {
			szMsg = "크레인사양과 비교 체크 중 Error :	" + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return intRtnVal = -3;
		}
		
	} //end of chkGetCrnspec
    
	
	
	
	
	
	
    
	/**
	 * 오퍼레이션명 : 크레인 스케줄 GROUPING PARAMETER DATA SETTING
	 *  
	 * @param  String     szEqpId, szSchCd, rsMinWrkBookMtl, rsReturn
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public int CrnSchSort(JDTORecordSet rsMinWrkBookMtl, JDTORecordSet rsReturn)throws JDTOException  {
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao(); 
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		
		//적치Bed정보
		JDTORecord recPara       = null;
		//적치단정보
		JDTORecord recStkLyr     = null;
		//현재적치단의 다음 재료정보
		JDTORecord recNextStkLyr = null;
		//현재적치단의 다음 권상모음순서  재료정보
		JDTORecord recNextUpCollSeq = null;
		JDTORecordSet rsResult 	 = null;
		
		//스케줄코드가 없을때 조회시
		JDTORecord recSchCd = null;
		JDTORecordSet rsSchCd = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		//작업예약재료 
		JDTORecordSet rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecord recMinWrkBookMtl = null;
		
		//결과 레코드셋
		JDTORecordSet rsCrnSchResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		//레코드셋 정렬 시
		JDTORecord recTemp     = null;
		
		
		//Bed조회시 정렬
		JDTORecord rsMaxWrkBookMtl = null;
		JDTORecordSet rsSelBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		String szMsg="";
		String szMethodName="CrnSchSort";
		String szOperationName = "크레인 스케줄 GROUPING PARAMETER DATA SETTING";
		String szFieldName = "";
		String szQuery = "";
		
		//위치비교 최하단위치 비교시
		String szStkLoc = "";
		String szMinStkLoc = "";
		
		String szCollGp  = "";
		String szBedNo   = "";
		String szLyrNo   = "";
		String szMtlStat = "";
		
		String szMaxCollGp  = "";
		String szMaxBedNo   = "";
		
		String szWbookId = "";
		
		int intYdUpCollSeq = 0;
		int intRtnVal = 0;
		int intHandlingCnt = 1;

		
		//적치Bed정보
		JDTORecord recParaTemp       = null;
		
		
		
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		
		try {
			
			rsMinWrkBookMtl.absolute(1);
			recPara = rsMinWrkBookMtl.getRecord();
			szWbookId = recPara.getFieldString("YD_WBOOK_ID");

			//작업예약재료 조회를 미리 해놓는다.
			
			/* 쿼리수정_요청 
			szQuery  = " SELECT YD_WBOOK_ID    AS YD_WBOOK_ID                                               ";
			szQuery += "       ,YD_STK_COL_GP  AS YD_STK_COL_GP                                             ";
			szQuery += "       ,YD_STK_BED_NO  AS YD_STK_BED_NO                                             ";
			szQuery += "       ,YD_STK_LYR_NO  AS YD_STK_LYR_NO                                             ";
			szQuery += "       ,YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ                                            ";
			szQuery += "       ,STL_NO         AS STL_NO                                                    ";
			szQuery += "   FROM TB_YD_WRKBOOKMTL                                                            ";
			szQuery += "  WHERE YD_WBOOK_ID = '" + szWbookId + "'";
			szQuery += "    AND (DEL_YN IS NULL OR DEL_YN <> 'Y')                                           ";
			szQuery += "  GROUP BY YD_WBOOK_ID, YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO, YD_UP_COLL_SEQ, STL_NO ";
			szQuery += "  ORDER BY YD_UP_COLL_SEQ DESC                                                       ";
			intRtnVal = ydDBAssist.getData(szQuery, rsWrkBookMtl, null) ;
			*/
			recParaTemp = JDTORecordFactory.getInstance().create();
			recParaTemp.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(recPara, "YD_WBOOK_ID"));
			
			intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recParaTemp, rsWrkBookMtl, 31);
			
			
			//권상 모음 순서가 가장 큰 재료의 정보를 가져온다.
			rsWrkBookMtl.absolute(1);
			rsMaxWrkBookMtl = rsWrkBookMtl.getRecord();
			szMaxCollGp  = rsMaxWrkBookMtl.getFieldString("YD_STK_COL_GP");
			szMaxBedNo   = rsMaxWrkBookMtl.getFieldString("YD_STK_BED_NO");
			for(int Loop_i = 1; Loop_i <= rsMinWrkBookMtl.size(); Loop_i++) {
				rsMinWrkBookMtl.absolute(Loop_i);
				recPara = rsMinWrkBookMtl.getRecord();
				//권상모음 순서가 가장 큰 재료정보와 같은 BED를 조회한다.
				if(szMaxCollGp.equals(recPara.getFieldString("YD_STK_COL_GP")) 
						&& szMaxBedNo.equals(recPara.getFieldString("YD_STK_BED_NO")) ) {
					//BED가 같다면 rsSelBed의 첫번째에 등록한다.
					rsSelBed.addRecord(recPara);
				}
			}
			for(int Loop_i = 1; Loop_i <= rsMinWrkBookMtl.size(); Loop_i++) {
				rsMinWrkBookMtl.absolute(Loop_i);
				recPara = rsMinWrkBookMtl.getRecord();
				if(szMaxCollGp.equals(recPara.getFieldString("YD_STK_COL_GP")) 
						&& szMaxBedNo.equals(recPara.getFieldString("YD_STK_BED_NO")) ) {
					
				}else{
					//권상 모음 순서가 가장 큰 재료정보의  BED를 제외한 BED를 차례대로  등록한다.
					rsSelBed.addRecord(recPara);
				}
			}
			


			//작업예약재료를 조회해서 받는다.Bed별로
			for(int Loop_i = 1; Loop_i <= rsSelBed.size(); Loop_i++) {
				rsSelBed.absolute(Loop_i);
				//적치Bed를 조회한다.

				recPara = rsSelBed.getRecord();
				//현재 적치중인 것만 받는다.
				recPara.setField("YD_STK_LYR_MTL_STAT", "C");
				szCollGp  = recPara.getFieldString("YD_STK_COL_GP");
				szBedNo   = recPara.getFieldString("YD_STK_BED_NO");
				szLyrNo   = recPara.getFieldString("YD_STK_LYR_NO");
				szMtlStat = recPara.getFieldString("YD_STK_LYR_MTL_STAT");
				System.out.println("szCollGp = " + szCollGp + ", szBedNo = " + szBedNo + ", szLyrNo = " + szLyrNo + ", szMtlStat = " + szMtlStat);
ydUtils.displayRecord(szOperationName, recPara);

	    		rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    		//적치단  조회
	    
	    		
	    		/*쿼리수정_요청
	    		
	    		szQuery  = "	SELECT X.STL_NO              AS STL_NO                           ";
	    		szQuery += "	      ,X.YD_STK_COL_GP       AS YD_STK_COL_GP                    ";
	    		szQuery += "	      ,X.YD_STK_BED_NO       AS YD_STK_BED_NO                    ";
	    		szQuery += "	      ,X.YD_STK_LYR_NO       AS YD_STK_LYR_NO                    ";
	    		szQuery += "	      ,X.YD_UP_COLL_SEQ      AS YD_UP_COLL_SEQ                   ";
	    		szQuery += "	      ,Y.YD_MTL_T            AS YD_MTL_T                         ";
	    		szQuery += "	      ,Y.YD_MTL_W            AS YD_MTL_W                         ";
	    		szQuery += "	      ,Y.YD_MTL_L            AS YD_MTL_L                         ";
	    		szQuery += "	      ,Y.YD_MTL_WT           AS YD_MTL_WT                        ";
	    		szQuery += "		  ,Y.YD_STK_LOT_TP  AS YD_STK_LOT_TP                         ";
	    		szQuery += "		  ,Y.YD_STK_LOT_CD  AS YD_STK_LOT_CD                         ";
	    		szQuery += "		  ,Y.YD_MTL_ITEM    AS YD_MTL_ITEM                           ";
	    		szQuery += "	      ,X.YD_STK_LYR_MTL_STAT AS YD_STK_LYR_MTL_STAT              ";
	    		szQuery += "	  FROM (SELECT A.STL_NO              AS STL_NO                   ";
	    		szQuery += "	              ,A.YD_STK_COL_GP       AS YD_STK_COL_GP            ";
	    		szQuery += "	              ,A.YD_STK_LYR_MTL_STAT AS YD_STK_LYR_MTL_STAT      ";
	    		szQuery += "	              ,A.YD_STK_BED_NO       AS YD_STK_BED_NO            ";
	    		szQuery += "	              ,A.YD_STK_LYR_NO       AS YD_STK_LYR_NO            ";
	    		szQuery += "	              ,B.YD_UP_COLL_SEQ      AS YD_UP_COLL_SEQ           ";
	    		szQuery += "	          FROM TB_YD_STKLYR A                                    ";
	    		szQuery += "	              ,TB_YD_WRKBOOKMTL B                                ";
	    		szQuery += "	         WHERE A.STL_NO              = B.STL_NO(+)               ";
	    		szQuery += "	           AND A.YD_STK_COL_GP       = '" + szCollGp +"'         ";
	    		szQuery += "	           AND A.YD_STK_BED_NO       = '" + szBedNo  +"'         ";
	    		szQuery += "	           AND A.YD_STK_LYR_NO      >= '" + szLyrNo  +"'         ";
	    		szQuery += "	           AND A.YD_STK_LYR_MTL_STAT = '" + szMtlStat+"'         ";
	    		szQuery += "	           AND (A.DEL_YN <> 'Y' OR A.DEL_YN IS NULL)             ";
	    		szQuery += "	           AND (B.DEL_YN <> 'Y' OR B.DEL_YN IS NULL) ) X         ";
	    		szQuery += "	      ,TB_YD_STOCK Y                                             ";
	    		szQuery += "	 WHERE X.STL_NO = Y.STL_NO                                       ";
	    		szQuery += "	   AND (Y.DEL_YN <> 'Y' OR Y.DEL_YN IS NULL)                     ";
	    		szQuery += "	 ORDER BY X.YD_STK_COL_GP, X.YD_STK_BED_NO, X.YD_STK_LYR_NO DESC ";
	    		intRtnVal = ydDBAssist.getData(szQuery, rsResult, null) ;
	    		*/ 
	    		
	    		
	    		intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 74);
//
//	    		if(intRtnVal <= 0) {
//	    			if(intRtnVal == 0) {
//	    				szMsg = "data not found";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	    			}else if(intRtnVal == -2) {
//	    				szMsg = "parameter error";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	    			}
//	    			return intRtnVal = -1;
//	    		}
	    		
	    		for(int Loop_j = 1; Loop_j <= rsResult.size(); Loop_j++) {
	    			rsResult.absolute(Loop_j);
	    			recStkLyr = rsResult.getRecord();
	    			//Bed순서
	    			recStkLyr.setField("BED_CNT", ""+Loop_i);
	    			recStkLyr.setField("YD_WBOOK_ID", recPara.getFieldString("YD_WBOOK_ID"));
	    			//HandlingCount
	    			recStkLyr.setField("HANDLING_CNT", ""+intHandlingCnt);
		    		//핸들링 카운트 증가
		    		intHandlingCnt++;
	    			//주작업여부판단
	    			if(ydDaoUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ") != 0) {
	    				//주작업
	    				recStkLyr.setField("MAIN_WRK_YN", "Y");
	    				
	    				//권상모음순서
		    			intYdUpCollSeq = ydDaoUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ");
		    			//권상모음Base
		    			recStkLyr.setField("UP_COLL_BASE", "" + (rsWrkBookMtl.size() - intYdUpCollSeq + 1));
		    			
		    			//현재작업재료의 다음 작업재료
		    			rsResult.next();
		    			recNextStkLyr = rsResult.getRecord();
		    			
		    			//주작업이적
		    			//현재 재료의 위치
		    			szStkLoc = recStkLyr.getFieldString("YD_STK_COL_GP") + recStkLyr.getFieldString("YD_STK_BED_NO") + recStkLyr.getFieldString("YD_STK_LYR_NO");
		    			for(int Loop_k = 1; Loop_k <= rsSelBed.size(); Loop_k++) {
		    				rsSelBed.absolute(Loop_k);
		    				recMinWrkBookMtl = rsSelBed.getRecord();
	    				
		    				//최하단 재료의 위치
							szMinStkLoc = recMinWrkBookMtl.getFieldString("YD_STK_COL_GP") + recMinWrkBookMtl.getFieldString("YD_STK_BED_NO") + recMinWrkBookMtl.getFieldString("YD_STK_LYR_NO");
							//현재 재료와 최하단 재료의 위치가 같은지 비교
							if(szStkLoc.equals(szMinStkLoc)){
		    					recStkLyr.setField("YD_TO_LOC_DCSN_MTD", "B");
		    					//권상모음 순서를 권상모음 Base의 값으로 바꾼다. (그룹핑 시.. 같은 그룹이 되는 것을 방지하기 위해...)
		    					recStkLyr.setField("HANDLING_CNT", "" + (10 + Integer.parseInt(recStkLyr.getFieldString("YD_UP_COLL_SEQ"))));
		    					
		    					//다음작업쟤료번호
								rsWrkBookMtl.first();
								recNextUpCollSeq = rsWrkBookMtl.getRecord();
								//현재작업재료번호의 권상모음순서가 마지막인 경우 바로 T로 등록한다.
								if(recStkLyr.getFieldString("YD_UP_COLL_SEQ").equals(recNextUpCollSeq.getFieldString("YD_UP_COLL_SEQ"))) {
									recStkLyr.setField("YD_TO_LOC_DCSN_MTD", "S");
								}						
		    					
								rsWrkBookMtl.first();
								recNextStkLyr = rsWrkBookMtl.getRecord();
								if(ydDaoUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ") 
										< ydDaoUtils.paraRecChkNullInt(recNextStkLyr,"YD_UP_COLL_SEQ")) {
		    						//다음작업쟤료번호;

		    						for(int Loop_l = 1; Loop_l <= rsWrkBookMtl.size(); Loop_l++) {

			    						rsWrkBookMtl.absolute(Loop_l);
			    						recNextUpCollSeq = rsWrkBookMtl.getRecord();

			    						//현재 재료번호의 위치와 같은경우 다음 레코드를 조회하면 다음 권상모음 순서이다.
			    						if(recStkLyr.getFieldString("STL_NO").equals(recNextUpCollSeq.getFieldString("STL_NO"))){
			    							//현재료의 다음권상모음순서의 재료번호
			    							rsWrkBookMtl.absolute(Loop_l-1);
			    							recNextUpCollSeq = rsWrkBookMtl.getRecord();
			    							//다음권상모음순서의 재료번호를 현재 레코드에 등록한다.
			    							recStkLyr.setField("UP_COLL_STL_NO", recNextUpCollSeq.getFieldString("STL_NO"));
			    						}//end of if
			    					}//end of for
		    						//최하단 재료와 현 재료의 위치가 같다면 정보 저장 후 for문을 벗어난다.
		    						
		    					}//end of if
								break;
		    				}
		    			}//주작업이적 end of for
		    			
		    			//최하단 재료가 아닌경우
		    			if(!szStkLoc.equals(szMinStkLoc)){
	    					//다음재료가 보조작업인 경우
	    					if(ydDaoUtils.paraRecChkNullInt(recNextStkLyr,"YD_UP_COLL_SEQ") == 0){
	    						recStkLyr.setField("YD_TO_LOC_DCSN_MTD", "M");
	    						recStkLyr.setField("UP_COLL_STL_NO", "");
	    					}else{
	    						recStkLyr.setField("YD_TO_LOC_DCSN_MTD", "A");
	    						recStkLyr.setField("UP_COLL_STL_NO", "");
	    					}
	    				}
		    			
	    			}else{
	    				//보조작업
	    				recStkLyr.setField("MAIN_WRK_YN", "N");
	    				recStkLyr.setField("YD_TO_LOC_DCSN_MTD", "W");
	    				recStkLyr.setField("UP_COLL_STL_NO","");
	    				recStkLyr.setField("UP_COLL_BASE","");
	    			}
	    			
	    			rsCrnSchResult.addRecord(recStkLyr);
ydUtils.displayRecord(szOperationName, recStkLyr);

	    			//등록된 레코드의 주작업 이적이 "M" 이거나 "T"인 경우 한번 더 등록을 한다.
	    			if(recStkLyr.getFieldString("YD_TO_LOC_DCSN_MTD").equals("M") ) {
	    				
	    				recTemp = JDTORecordFactory.getInstance().create();
	    				recTemp.addRecord(recStkLyr);
	    				recTemp.setField("YD_TO_LOC_DCSN_MTD", "R");
	    				
						//다음작업쟤료번호
						rsWrkBookMtl.first();
						recNextUpCollSeq = rsWrkBookMtl.getRecord();
						//현재작업재료번호의 권상모음순서가 마지막인 경우
						if(recStkLyr.getFieldString("YD_UP_COLL_SEQ").equals(recNextUpCollSeq.getFieldString("YD_UP_COLL_SEQ"))) {
							recTemp.setField("YD_TO_LOC_DCSN_MTD", "T");
						}						
		    			
	    				//HandlingCount등록 10+권상모음순서
	    				recTemp.setField("HANDLING_CNT", "" + (10 + Integer.parseInt(recTemp.getFieldString("YD_UP_COLL_SEQ"))));
						rsWrkBookMtl.first();
						recNextStkLyr = rsWrkBookMtl.getRecord();
						if(ydDaoUtils.paraRecChkNullInt(recTemp,"YD_UP_COLL_SEQ") 
								< ydDaoUtils.paraRecChkNullInt(recNextStkLyr,"YD_UP_COLL_SEQ")) {
    						//다음작업쟤료번호;

    						for(int Loop_l = 1; Loop_l <= rsWrkBookMtl.size(); Loop_l++) {

	    						rsWrkBookMtl.absolute(Loop_l);
	    						recNextUpCollSeq = rsWrkBookMtl.getRecord();

	    						//현재 재료번호의 위치와 같은경우 다음 레코드를 조회하면 다음 권상모음 순서이다.
	    						if(recTemp.getFieldString("STL_NO").equals(recNextUpCollSeq.getFieldString("STL_NO"))){
	    							//현재료의 다음권상모음순서의 재료번호
	    							rsWrkBookMtl.absolute(Loop_l-1);
	    							recNextUpCollSeq = rsWrkBookMtl.getRecord();
	    							//다음권상모음순서의 재료번호를 현재 레코드에 등록한다.
	    							recTemp.setField("UP_COLL_STL_NO", recNextUpCollSeq.getFieldString("STL_NO"));
	    						}//end of if
	    					}//end of for
    						
    					}//end of if
    					rsCrnSchResult.addRecord(recTemp);
ydUtils.displayRecord(szOperationName, recTemp);

	    			}//end of if
	    			

	    		}//end of for
System.out.println("TEST SEQ = 04");	
			}//end of for
			
			//레코드셋 정렬
			JDTORecordSet rsCrnSchSorted = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recResult = null;
			JDTORecord recAfter = null;
			JDTORecord recCurrt = null;
			for(int Loop_i = 1; Loop_i < rsCrnSchResult.size(); Loop_i++) {
			
				for (int Loop_j = Loop_i + 1; Loop_j < rsCrnSchResult.size() + 1; Loop_j++) {
					
					rsCrnSchResult.absolute(Loop_i);
					recCurrt = rsCrnSchResult.getRecord();
					
					rsCrnSchResult.absolute(Loop_j);
					recAfter = rsCrnSchResult.getRecord();
					//
					if (recCurrt.getFieldInt("HANDLING_CNT") > recAfter.getFieldInt("HANDLING_CNT")) {
						rsCrnSchResult = this.rsSort(Loop_i, Loop_j, rsCrnSchResult);
						if (intRtnVal == -1) {
							System.out.println("<rsSort> 정렬 중 Error ");
							return intRtnVal;
						}
					}
				}//end of infor
			}//end of outfor
			
			System.out.println("==============================================================================================");
			for(int Loop_i = 1; Loop_i <= rsCrnSchResult.size(); Loop_i++) {
				rsCrnSchResult.absolute(Loop_i);
				System.out.println("HandlingCount = " + rsCrnSchResult.getRecord().getFieldString("HANDLING_CNT"));
				recCurrt = rsCrnSchResult.getRecord();
				rsReturn.addRecord(recCurrt);
			}
			System.out.println("==============================================================================================");
	
System.out.println("TEST SEQ = 06");
//			rsReturn.addAll(rsCrnSchResult);

			System.out.println("rsReturn: "+rsReturn.size());			
		} catch(Exception e) {
			szMsg = "CrnSchSort 중 Error :	" + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return intRtnVal = -1;
		}
		return intRtnVal = 1;
	} //end of CrnSchSort
    
	
	
	
	
	
	
	
	
	/**
     * 오퍼레이션명 : 레코드 치환
     *  
     * @param  ● recPara1, recPara2, recResult
     * @return ● intRtnVal '1': 성공   '-1': 실패
     * @throws ● JDTOException
     */
    public JDTORecordSet rsSort (int intLoop_i, int intLoop_j, JDTORecordSet rsCrnSchResult) {

    	JDTORecord recTemp = null;
    	
    	JDTORecordSet rsTemp = null; 
    	
		int intRtnVal = 0;
		
		String szName = "SYSTEM";
		String szMsg = "";
		String szMethodName = "rsSort";
		
		try{
			rsTemp = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			for(int Loop_i = 1;  Loop_i <= rsCrnSchResult.size(); Loop_i++) {
				if(Loop_i == intLoop_i) {
					rsCrnSchResult.absolute(intLoop_j);
					recTemp = rsCrnSchResult.getRecord();
				}else if(Loop_i == intLoop_j) {
					rsCrnSchResult.absolute(intLoop_i);
					recTemp = rsCrnSchResult.getRecord();
				}else{
					rsCrnSchResult.absolute(Loop_i);
					recTemp = rsCrnSchResult.getRecord();
				}
				rsTemp.addRecord(recTemp);
			}
			
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
        }//end of try~catch
		
		return rsTemp;
    }//end of rsSort()

	
	
	
    
    
    
	/**
     * 오퍼레이션명 : C연주스케줄링 크레인 스케줄 등록
     *  
     * @param  ● msgRecSet, msgRec
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int CrnSchIns (Vector vResult, JDTORecord msgRecord) throws JDTOException {

    	JDTORecord recGet      = null;
		JDTORecord recIn       = null;
		JDTORecord recSeq      = null;
		
		JDTORecordSet rsOut    = null;
		JDTORecordSet rsResult = null;
		JDTORecordSet rsMtlResult = null;
		
		JDTORecord recWrkBookMtl   = null;
		JDTORecordSet rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		JDTORecord recSchCd   = null;
		JDTORecordSet rsSchCd = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		int intCnt    = 1;
		int intRtnVal = 0;
		int rowcount  = 0;
		int vSize	  = 0;
		String szName = "SYSTEM";
		String szMsg = "";
		String szMethodName = "CrnSchIns";
		String szEqpId = "";
		String szSchCd = "";
		String szQuery = "";
		
		JDTORecord recPara      = null;
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		
		try{
			szEqpId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			szSchCd = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			
			if(szSchCd.equals("")){
				
				/* 쿼리수정_요청
				szQuery  = "	SELECT YD_WBOOK_ID                                ";
				szQuery += "	      ,YD_SCH_CD                                  ";
				szQuery += "	  FROM (SELECT YD_WBOOK_ID                        ";
				szQuery += "	              ,YD_SCH_CD                          ";
				szQuery += "	          FROM TB_YD_WRKBOOK                      ";
				szQuery += "	         WHERE (DEL_YN <> 'Y' OR DEL_YN IS NULL)  ";
				szQuery += "			     ORDER BY YD_SCH_PRIOR, YD_WBOOK_ID)  ";
				szQuery += "	 WHERE ROWNUM < '2'                               ";
				intRtnVal = ydDBAssist.getData(szQuery, rsSchCd, null) ;
				*/ 
				
				
				
				recPara = JDTORecordFactory.getInstance().create();				
				intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, rsSchCd, 22);
				
				
				for(int Loop_i = 1; Loop_i <= rsSchCd.size(); Loop_i++) {
					rsSchCd.absolute(Loop_i);
					recSchCd = rsSchCd.getRecord();
					szSchCd = recSchCd.getFieldString("YD_SCH_CD");
				}
			}
			
			
			
			
			//작업예약재료 조회를 미리 해놓는다.
			/* 쿼리 수정_요청 
			szQuery  = " SELECT B.STL_NO         AS STL_NO                                                                  ";
			szQuery += "       ,B.YD_WBOOK_ID    AS YD_WBOOK_ID                                                             ";
			szQuery += "       ,B.YD_STK_COL_GP  AS YD_STK_COL_GP                                                           ";
			szQuery += "       ,B.YD_STK_BED_NO  AS YD_STK_BED_NO                                                           ";
			szQuery += "       ,B.YD_STK_LYR_NO  AS YD_STK_LYR_NO                                                           ";
			szQuery += "       ,B.YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ                                                          ";
			szQuery += "       ,A.YD_STK_LOT_TP  AS YD_STK_LOT_TP                                                           ";
			szQuery += "       ,A.YD_STK_LOT_CD  AS YD_STK_LOT_CD                                                           ";
			szQuery += "       ,A.YD_MTL_ITEM    AS YD_MTL_ITEM                                                             ";
			szQuery += "   FROM TB_YD_STOCK A                                                                               ";
			szQuery += "       ,(SELECT YD_WBOOK_ID    AS YD_WBOOK_ID                                                       ";
			szQuery += "               ,YD_STK_COL_GP  AS YD_STK_COL_GP                                                     ";
			szQuery += "               ,YD_STK_BED_NO  AS YD_STK_BED_NO                                                     ";
			szQuery += "               ,YD_STK_LYR_NO  AS YD_STK_LYR_NO                                                     ";
			szQuery += "               ,YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ                                                    ";
			szQuery += "               ,STL_NO         AS STL_NO                                                            ";
			szQuery += "         FROM TB_YD_WRKBOOKMTL                                                                      ";
			szQuery += "         WHERE YD_WBOOK_ID = (SELECT YD_WBOOK_ID                                                    ";
			szQuery += "                              FROM (SELECT YD_WBOOK_ID                                              ";
			szQuery += "                                      FROM TB_YD_WRKBOOK                                            ";
			szQuery += "                                     WHERE YD_SCH_CD = '" + szSchCd + "'                            ";
			szQuery += "                                       AND (DEL_YN <> 'Y' OR DEL_YN IS NULL)                        ";
			szQuery += "                                     ORDER BY YD_WBOOK_ID)                                          ";
			szQuery += "                             WHERE ROWNUM < 2)                                                      ";
			szQuery += "         AND (DEL_YN IS NULL OR DEL_YN <> 'Y')                                                      ";
			szQuery += "         GROUP BY YD_WBOOK_ID, YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO, YD_UP_COLL_SEQ, STL_NO  ";
			szQuery += "         ORDER BY YD_UP_COLL_SEQ DESC) B                                                            ";
			szQuery += "  WHERE  B.STL_NO = A.STL_NO(+)                                                                     ";
			intRtnVal = ydDBAssist.getData(szQuery, rsWrkBookMtl, null) ;
			*/
			
			/* com.inisteel.cim.yd.dao.ydWrkbookMtlDao.CrnSchIns01 */
			recPara = JDTORecordFactory.getInstance().create();			
			recPara.setField("YD_SCH_CD", szSchCd);
			intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, rsWrkBookMtl, 32);
			
			         
			//크레인 스케줄에 Insert한다.
			vSize = vResult.size();
			for(int i = 0; i < vSize; i++) {
				
				//Vector 값을  가져온다.
				rsResult = (JDTORecordSet) vResult.get(i);
				rowcount = rsResult.size();
				
				
				//크레인 스케줄 등록 마지막이 대표 정보임
				rsResult.last();
				recIn = rsResult.getRecord();
				recSeq = JDTORecordFactory.getInstance().create();
				recSeq.setField("YD_CRN_SCH_ID", "1");


				//크레인스케줄ID를 할당받는다
				rsOut = JDTORecordFactory.getInstance().createRecordSet("Temp");
				intRtnVal = this.getYdCrnsch(recSeq, rsOut, 9);
	    		if(intRtnVal <= 0) {
    				szMsg = "크레인스케줄 Id를 생성하지 못했습니다.";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			return intRtnVal = -1;
	    		}
	    		
	    		
	    		//할당받은 크레인 스케줄 아이디로 Insert
	    		rsOut.first() ; 
				recSeq = rsOut.getRecord();
				recIn.setField("YD_CRN_SCH_ID",    recSeq.getFieldString("YD_CRN_SCH_ID"));
				recIn.setField("YD_EQP_ID",        szEqpId);
				recIn.setField("YD_GP",            recIn.getFieldString("YD_STK_COL_GP").substring(0,1));
				recIn.setField("YD_BAY_GP",        recIn.getFieldString("YD_STK_COL_GP").substring(1,2));
				recIn.setField("YD_SCH_CD",        szSchCd);
				if( recIn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("M") 
						|| recIn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("W") 
						|| recIn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("S")
						|| recIn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("B")) {
					recIn.setField("YD_UP_WO_LOC",     recIn.getFieldString("YD_STK_COL_GP") + recIn.getFieldString("YD_STK_BED_NO"));
					recIn.setField("YD_UP_WO_LAYER",   recIn.getFieldString("YD_STK_LYR_NO"));
				}

				recIn.setField("YD_WRK_PROG_STAT", "W");
				recIn.setField("REGISTER", szName);
				
				intRtnVal = this.insYdCrnsch(recIn);
				if(intRtnVal <= 0) {
					System.out.println("크레인 스케줄 등록 실패 ");
					return intRtnVal = -1;
				}
				//앞 재료들 포함
				if(recIn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("R") 
						|| recIn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("T")
						|| recIn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("B")) {
					//이전 권상모음순서 재료들을 모두 등록한다.
					
					for(int Loop_j = 1; Loop_j <= rsWrkBookMtl.size(); Loop_j++) {
						rsWrkBookMtl.absolute(Loop_j);
						recWrkBookMtl = rsWrkBookMtl.getRecord();
						if(recIn.getFieldInt("YD_UP_COLL_SEQ") >= recWrkBookMtl.getFieldInt("YD_UP_COLL_SEQ")) {
							
							recWrkBookMtl.setField("YD_CRN_SCH_ID", recSeq.getFieldString("YD_CRN_SCH_ID"));
							recWrkBookMtl.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt("000", intCnt));
							recWrkBookMtl.setField("REGISTER", szName);
							//크레인작업재료insert
							intRtnVal = this.insYdCrnWrkMtl(recWrkBookMtl);
							if(intRtnVal <= 0) {
								System.out.println("크레인 작업재료 등록 실패  : parameter error ");
								return intRtnVal = -1;
							}
							intCnt++;
						}
					}//end of for
				}else if(recIn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("S") ) { 
					for(int Loop_j = 1; Loop_j <= rsWrkBookMtl.size(); Loop_j++) {
						rsWrkBookMtl.absolute(Loop_j);
						recWrkBookMtl = rsWrkBookMtl.getRecord();
						if(recIn.getFieldInt("YD_UP_COLL_SEQ") >= recWrkBookMtl.getFieldInt("YD_UP_COLL_SEQ")) {
							//적치단의 재료상태를 권상대기로 변경
							recWrkBookMtl.setField("YD_STK_LYR_MTL_STAT", "U");
							intRtnVal = this.updYdStklyr(recWrkBookMtl, 0);
							if(intRtnVal <= 0) {
								System.out.println("적치단 등록 실패  : parameter error ");
								return intRtnVal = -1;
							}
							
							recWrkBookMtl.setField("YD_CRN_SCH_ID", recSeq.getFieldString("YD_CRN_SCH_ID"));
							recWrkBookMtl.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt("000", intCnt));
							recWrkBookMtl.setField("REGISTER", szName);
							//크레인작업재료insert
							intRtnVal = this.insYdCrnWrkMtl(recWrkBookMtl);
							if(intRtnVal <= 0) {
								System.out.println("크레인 작업재료 등록 실패  : parameter error ");
								return intRtnVal = -1;
							}
							intCnt++;
						}
					}//end of for					
				}else{
					for(int Loop_j = 1; Loop_j <= rowcount; Loop_j++) {
						rsResult.absolute(Loop_j);
						recIn = rsResult.getRecord();
						
						//적치단의 재료상태를 권상대기로 변경
						recIn.setField("YD_STK_LYR_MTL_STAT", "U");
						intRtnVal = this.updYdStklyr(recIn, 0);
						if(intRtnVal <= 0) {
							System.out.println("적치단 등록 실패  : parameter error ");
							return intRtnVal = -1;
						}
						
						recIn.setField("YD_CRN_SCH_ID", recSeq.getFieldString("YD_CRN_SCH_ID"));
						recIn.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt("000", Loop_j));
						recIn.setField("REGISTER", szName);
						//크레인작업재료insert
						intRtnVal = this.insYdCrnWrkMtl(recIn);
						if(intRtnVal <= 0) {
							System.out.println("크레인 스케줄 등록 실패  : parameter error ");
							return intRtnVal = -1;
						}
						
					}//end of in for
				}
				intCnt = 1;
			}//end of out for
			return intRtnVal = 1;
       
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return intRtnVal = -1;
		
        }//end of try~catch
        
    }//end of CrnSchIns()



    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 크레인스케줄 Insert
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '-2': parameter error
     * @throws ● JDTOException
     */
    public int insYdCrnsch(JDTORecord msgRecord) throws JDTOException {
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

    	String szMsg        = "";
    	String szMethodName = "";
    	
    	int intRtnVal = 0 ;

		try{
			
			intRtnVal = ydCrnschDao.insYdCrnsch(msgRecord);		       
			if(intRtnVal == -2) {
				szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of insYdCrnsch
	
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 크레인작업재료 Insert
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '-2': parameter error
     * @throws ● JDTOException
     */
    public int insYdCrnWrkMtl(JDTORecord msgRecord) throws JDTOException {
    	YdCrnWrkMtlDao ydCrnwrkmtlDao = new YdCrnWrkMtlDao();

    	String szMsg        = "";
    	String szMethodName = "";
    	int intRtnVal = 0 ;

		try{
			
			intRtnVal = ydCrnwrkmtlDao.insYdCrnwrkmtl(msgRecord);		        
			if(intRtnVal == -2) {
				szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of updYdCrnsch
    
    
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 작업예약 Select
     *  
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int getYdWrkbook (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
    	YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	String szMsg			= "" ;
    	String szMethodName		= "getYdWrkbook" ;
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	
        	intRtnVal = ydWrkbookDao.getYdWrkbook(msgRecord, getRecSet, intGp);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	            return intRtnVal ;
	        }
	        outRecset.addAll(getRecSet)  ; 
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of getYdWrkbook()
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 작업예약재료 Select
     *  
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int getYdWrkbookmtl (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
    	YdWrkbookMtlDao ydWrkbookmtlDao = new YdWrkbookMtlDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	
        	intRtnVal = ydWrkbookmtlDao.getYdWrkbookmtl(msgRecord, getRecSet, intGp);
	        if (intRtnVal <= 0) return -2;
	        
	        outRecset.addAll(getRecSet)  ; 
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -2 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of getYdWrkbookmtl
    
    

    
    
    
    
    
    
    
    /**
	 * C연주저장위치등록Main
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void DnLocInsMain(JDTORecord inRecord)throws JDTOException  {

		//
		// C연주저장위치등록Main
		// TC : YDYDJ501
		// 야드작업자로부터 저장위치등록수신
		//
		//┏━┓
		//┃ 야드작업자로부터 저장위치 등록을 수신하면 저장위치를 검색하여 등록한다.
		//┗━┛
		
		//크레인작업재료의 최하단 재료정보, 크레인작업재료의 총매수 중량 높이, 크레인스케줄의 야드구분 동구분 스케줄코드, 저장집합코드
		JDTORecord recGetCrnWrkMtl = null ;
		
		JDTORecordSet rsBed       = null;
		JDTORecordSet rsCrnWrkMtl = null;
		JDTORecordSet rsResultCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		String szMethodName = "DnLocInsMain";
		String szMsg = "";
		//작업예약Id
		String szWbookId = "";
		//설비Id
		String szEqpId = "";
		

		int intRtnVal = 0;

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);
        if(szRcvTcCode==null){
        	szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        	return;
        }
        
        
        if(bDebugFlag){
            szMsg="[DEBUG] 전문수신1 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

		try{
			
			//파라미터 Null Check
			szWbookId = ydDaoUtils.paraRecChkNull(inRecord, "YD_WBOOK_ID");
			szEqpId   = ydDaoUtils.paraRecChkNull(inRecord, "YD_EQP_ID");
			if(szWbookId.equals("") || szEqpId.equals("")) {
	        	szMsg="파라미터값이 잘못되었습니다.";
	        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	        	return;
			}
			
			
			//1. 위치검색범위 조회 Data Setting
			intRtnVal = this.LocSrcRngDataSet(inRecord, recGetCrnWrkMtl) ;
			if( intRtnVal == -1 ) return ;
			
		}catch(Exception e){

			szMsg="Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}


		szMsg="저장위치  등록("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} //end of DnLocInsMain()
    
    
	
	
	
	
	
	
	
	
    /**
     * 오퍼레이션명 : 위치검색범위 조회 DataSet
     *  
     * @param  inRecord, recGetCrnWrkMtl, rsResultCrnwrkmtl
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int LocSrcRngDataSet (JDTORecord inRecord, JDTORecord recGetCrnWrkMtl){
    	//상위 Method : DnLocInsMain
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
		//┗━┛
	
    	YdStkBedDao   ydStkBedDao = new YdStkBedDao();
    	
    	YdDBAssist ydDBAssist = new YdDBAssist();
    	YdUtils       ydUtils = new YdUtils();

    	//크레인스케줄
    	JDTORecordSet rsCrnsch    = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet rsCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet rsCrnwrkmtlCmp = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
    	//크레인작업재료
    	JDTORecord recCrnSch      = null;
    	JDTORecord recCrnwrkmtl   = null;
    	

    	String szMethodName = "LocSrcRngDataSet";
    	String szMsg        = "";     	  
    	String szOperationName = "위치검색범위 조회 DataSet";
    	//크레인스케줄id,스케줄코드,야드구분,동구분,To위치결정방법
    	String szCrnSchId = "";
    	String szSchCd    = "";
    	String szYdGp     = "";
    	String szYdBayGp  = "";
    	String szQuery    = "";
    	String szToLocDcsnMtd = "";
    	
		//작업예약Id
		String szWbookId  = "";
		//설비Id
		String szEqpId    = "";
    	
    	int intRtnVal = 0 ;
    	
    	YdEqpDao ydEqpDao = new YdEqpDao();
    	JDTORecord recTempPara       = null;
    	
        try{
System.out.println("TEST_1 ============ ");
			//파라미터 Null Check
			szWbookId = ydDaoUtils.paraRecChkNull(inRecord, "YD_WBOOK_ID");
			szEqpId   = ydDaoUtils.paraRecChkNull(inRecord, "YD_EQP_ID");
System.out.println("TEST_2 ============ ");        	
        	//크레인스케줄 조회
//        	intRtnVal = ydCrnSchDao.getYdCrnsch(inRecord, rsCrnsch, 10) ;
//        	if(intRtnVal <= 0){
//        		if(intRtnVal == 0) {
//        			szMsg = "크레인스케줄 data를 찾을 수 없습니다.";
//        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//        		}
//        		if(intRtnVal == -2) {
//        			szMsg = "파라미터가 잘못되었습니다.";
//        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//        		}
//        		return intRtnVal = -1;
//        	}
//        	
//        	rsCrnsch.beforeFirst();        	
System.out.println("TEST_3 ============ ");        	
    		//크레인스케줄 조회 DB어시스트

/*쿼리수정_요청
			szQuery = "		SELECT A.YD_EQP_ID               AS YD_EQP_ID                                   ";
			szQuery+= "		      ,A.YD_EQP_NAME             AS YD_EQP_NAME                                 ";
			szQuery+= "		      ,B.YD_CRN_SCH_ID           AS YD_CRN_SCH_ID                               ";
			szQuery+= "		      ,B.REGISTER                AS REGISTER                                    ";
			szQuery+= "		      ,B.REG_DDTT                AS REG_DDTT                                    ";
			szQuery+= "		      ,B.MODIFIER                AS MODIFIER                                    ";
			szQuery+= "		      ,B.MOD_DDTT                AS MOD_DDTT                                    ";
			szQuery+= "		      ,B.DEL_YN                  AS DEL_YN                                      ";
			szQuery+= "		      ,B.YD_WBOOK_ID             AS YD_WBOOK_ID                                 ";
			szQuery+= "		      ,B.YD_EQP_ID               AS YD_EQP_ID                                   ";
			szQuery+= "		      ,B.YD_GP                   AS YD_GP                                       ";
			szQuery+= "		      ,B.YD_BAY_GP               AS YD_BAY_GP                                   ";
			szQuery+= "		      ,B.YD_SCH_CD               AS YD_SCH_CD                                   ";
			szQuery+= "		      ,B.YD_SCH_ST_GP            AS YD_SCH_ST_GP                                ";
			szQuery+= "		      ,B.YD_SCH_REQ_GP           AS YD_SCH_REQ_GP                               ";
			szQuery+= "		      ,B.YD_SCH_PRIOR            AS YD_SCH_PRIOR                                ";
			szQuery+= "		      ,B.YD_EQP_WRK_STAT         AS YD_EQP_WRK_STAT                             ";
			szQuery+= "		      ,B.YD_WRK_PROG_STAT        AS YD_WRK_PROG_STAT                            ";
			szQuery+= "		      ,B.YD_WBOOK_DT             AS YD_WBOOK_DT                                 ";
			szQuery+= "		      ,B.YD_SCH_DT               AS YD_SCH_DT                                   ";
			szQuery+= "		      ,B.YD_WORD_DT              AS YD_WORD_DT                                  ";
			szQuery+= "		      ,B.YD_UP_CMPL_DT           AS YD_UP_CMPL_DT                               ";
			szQuery+= "		      ,B.YD_DN_CMPL_DT           AS YD_DN_CMPL_DT                               ";
			szQuery+= "		      ,B.YD_WRK_HDS_DD           AS YD_WRK_HDS_DD                               ";
			szQuery+= "		      ,B.YD_WRK_DUTY             AS YD_WRK_DUTY                                 ";
			szQuery+= "		      ,B.YD_WRK_PARTY            AS YD_WRK_PARTY                                ";
			szQuery+= "		      ,B.YD_MAIN_WRK_MTL_SH      AS YD_MAIN_WRK_MTL_SH                          ";
			szQuery+= "		      ,B.YD_AID_WRK_MTL_SH       AS YD_AID_WRK_MTL_SH                           ";
			szQuery+= "		      ,B.YD_AID_WRK_UPDN_GP      AS YD_AID_WRK_UPDN_GP                          ";
			szQuery+= "		      ,B.YD_TO_LOC_DCSN_MTD      AS YD_TO_LOC_DCSN_MTD                          ";
			szQuery+= "		      ,B.YD_TO_LOC_GUIDE         AS YD_TO_LOC_GUIDE                             ";
			szQuery+= "		      ,B.YD_EQP_WRK_SH           AS YD_EQP_WRK_SH                               ";
			szQuery+= "		      ,B.YD_EQP_WRK_WT           AS YD_EQP_WRK_WT                               ";
			szQuery+= "		      ,B.YD_EQP_WRK_T            AS YD_EQP_WRK_T                                ";
			szQuery+= "		      ,B.YD_EQP_WRK_MAX_W        AS YD_EQP_WRK_MAX_W                            ";
			szQuery+= "		      ,B.YD_EQP_WRK_MAX_L        AS YD_EQP_WRK_MAX_L                            ";
			szQuery+= "		      ,B.YD_CRN_SB_CTL_H         AS YD_CRN_SB_CTL_H                             ";
			szQuery+= "		      ,B.YD_CRN_GRAB_USE_RULE_ID AS YD_CRN_GRAB_USE_RULE_ID                     ";
			szQuery+= "		      ,B.YD_UP_WO_LOC            AS YD_UP_WO_LOC                                ";
			szQuery+= "		      ,B.YD_UP_WO_LAYER          AS YD_UP_WO_LAYER                              ";
			szQuery+= "		      ,B.YD_UP_WO_LOC_XAXIS      AS YD_UP_WO_LOC_XAXIS                          ";
			szQuery+= "		      ,B.YD_UP_WO_XAXIS_GAP_MAX  AS YD_UP_WO_XAXIS_GAP_MAX                      ";
			szQuery+= "		      ,B.YD_UP_WO_XAXIS_GAP_MIN  AS YD_UP_WO_XAXIS_GAP_MIN                      ";
			szQuery+= "		      ,B.YD_UP_WO_LOC_YAXIS      AS YD_UP_WO_LOC_YAXIS                          ";
			szQuery+= "		      ,B.YD_UP_WO_LOC_YAXIS1     AS YD_UP_WO_LOC_YAXIS1                         ";
			szQuery+= "		      ,B.YD_UP_WO_LOC_YAXIS2     AS YD_UP_WO_LOC_YAXIS2                         ";
			szQuery+= "		      ,B.YD_UP_WO_YAXIS_GAP_MAX  AS YD_UP_WO_YAXIS_GAP_MAX                      ";
			szQuery+= "		      ,B.YD_UP_WO_YAXIS_GAP_MIN  AS YD_UP_WO_YAXIS_GAP_MIN                      ";
			szQuery+= "		      ,B.YD_UP_WO_LOC_ZAXIS      AS YD_UP_WO_LOC_ZAXIS                          ";
			szQuery+= "		      ,B.YD_UP_WO_ZAXIS_GAP_MAX  AS YD_UP_WO_ZAXIS_GAP_MAX                      ";
			szQuery+= "		      ,B.YD_UP_WO_ZAXIS_GAP_MIN  AS YD_UP_WO_ZAXIS_GAP_MIN                      ";
			szQuery+= "		      ,B.YD_DN_WO_LOC            AS YD_DN_WO_LOC                                ";
			szQuery+= "		      ,B.YD_DN_WO_LAYER          AS YD_DN_WO_LAYER                              ";
			szQuery+= "		      ,B.YD_DN_WO_LOC_XAXIS      AS YD_DN_WO_LOC_XAXIS                          ";
			szQuery+= "		      ,B.YD_DN_WO_XAXIS_GAP_MAX  AS YD_DN_WO_XAXIS_GAP_MAX                      ";
			szQuery+= "		      ,B.YD_DN_WO_XAXIS_GAP_MIN  AS YD_DN_WO_XAXIS_GAP_MIN                      ";
			szQuery+= "		      ,B.YD_DN_WO_LOC_YAXIS      AS YD_DN_WO_LOC_YAXIS                          ";
			szQuery+= "		      ,B.YD_DN_WO_LOC_YAXIS1     AS YD_DN_WO_LOC_YAXIS1                         ";
			szQuery+= "		      ,B.YD_DN_WO_LOC_YAXIS2     AS YD_DN_WO_LOC_YAXIS2                         ";
			szQuery+= "		      ,B.YD_DN_WO_YAXIS_GAP_MAX  AS YD_DN_WO_YAXIS_GAP_MAX                      ";
			szQuery+= "		      ,B.YD_DN_WO_YAXIS_GAP_MIN  AS YD_DN_WO_YAXIS_GAP_MIN                      ";
			szQuery+= "		      ,B.YD_DN_WO_LOC_ZAXIS      AS YD_DN_WO_LOC_ZAXIS                          ";
			szQuery+= "		      ,B.YD_DN_WO_ZAXIS_GAP_MAX  AS YD_DN_WO_ZAXIS_GAP_MAX                      ";
			szQuery+= "		      ,B.YD_DN_WO_ZAXIS_GAP_MIN  AS YD_DN_WO_ZAXIS_GAP_MIN                      ";
			szQuery+= "		      ,B.YD_UP_WR_LOC            AS YD_UP_WR_LOC                                ";
			szQuery+= "		      ,B.YD_UP_WR_LAYER          AS YD_UP_WR_LAYER                              ";
			szQuery+= "		      ,B.YD_UP_WRK_ACT_GP        AS YD_UP_WRK_ACT_GP                            ";
			szQuery+= "		      ,B.YD_UP_WR_XAXIS          AS YD_UP_WR_XAXIS                              ";
			szQuery+= "		      ,B.YD_UP_WR_YAXIS          AS YD_UP_WR_YAXIS                              ";
			szQuery+= "		      ,B.YD_UP_WR_YAXIS1         AS YD_UP_WR_YAXIS1                             ";
			szQuery+= "		      ,B.YD_UP_WR_YAXIS2         AS YD_UP_WR_YAXIS2                             ";
			szQuery+= "		      ,B.YD_UP_WR_ZAXIS          AS YD_UP_WR_ZAXIS                              ";
			szQuery+= "		      ,B.YD_DN_WR_LOC            AS YD_DN_WR_LOC                                ";
			szQuery+= "		      ,B.YD_DN_WR_LAYER          AS YD_DN_WR_LAYER                              ";
			szQuery+= "		      ,B.YD_DN_WRK_ACT_GP        AS YD_DN_WRK_ACT_GP                            ";
			szQuery+= "		      ,B.YD_DN_WR_XAXIS          AS YD_DN_WR_XAXIS                              ";
			szQuery+= "		      ,B.YD_DN_WR_YAXIS          AS YD_DN_WR_YAXIS                              ";
			szQuery+= "		      ,B.YD_DN_WR_YAXIS1         AS YD_DN_WR_YAXIS1                             ";
			szQuery+= "		      ,B.YD_DN_WR_YAXIS2         AS YD_DN_WR_YAXIS2                             ";
			szQuery+= "		      ,B.YD_DN_WR_ZAXIS          AS YD_DN_WR_ZAXIS                              ";
			szQuery+= "		  FROM TB_YD_EQP    A                                                           ";
			szQuery+= "		      ,TB_YD_CRNSCH B                                                           ";
			szQuery+= "		 WHERE B.YD_EQP_ID   = A.YD_EQP_ID                                              ";
			szQuery+= "		   AND B.YD_WBOOK_ID = '" + szWbookId + "'";
			szQuery+= "		   AND B.YD_EQP_ID   = '" + szEqpId + "'";                                   
			szQuery+= "		   AND (B.DEL_YN IS NULL OR B.DEL_YN <> 'Y')                                    ";
			szQuery+= "		 ORDER BY B.YD_CRN_SCH_ID                                                       ";   

		    intRtnVal = ydDBAssist.getData(szQuery, rsCrnsch, null) ;
		    */
		    recTempPara = JDTORecordFactory.getInstance().create();	
		    recTempPara.setField("YD_WBOOK_ID", szWbookId);
		    recTempPara.setField("YD_EQP_ID", szEqpId);
		    
		    ydEqpDao.getYdEqp(recTempPara, rsCrnsch, 7);
		    
		    
		    
		    
		    
		    
		    
		    
		    
		    
		    
		    
System.out.println("TEST_4 ============" + rsCrnsch.size());  
		    rsCrnsch.beforeFirst();

        	for(int Loop_i = 0; Loop_i < rsCrnsch.size(); Loop_i++) {
        		
        		rsCrnsch.next();
        		recCrnSch  = rsCrnsch.getRecord();
        		ydUtils.displayRecord(szOperationName, recCrnSch);
        		
        		//크레인스케줄Data저장
        		szCrnSchId     = recCrnSch.getFieldString("YD_CRN_SCH_ID");
        		szSchCd        = recCrnSch.getFieldString("YD_SCH_CD");
        		szYdGp         = recCrnSch.getFieldString("YD_GP");
        		szYdBayGp  	   = recCrnSch.getFieldString("YD_BAY_GP");
        		szToLocDcsnMtd = recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD");

        		
        		/*
        		//크레인작업재료조회(쿼리등록 완료)
				szQuery = "		SELECT A.STL_NO                                              AS STL_NO          ";
				szQuery+= "		      ,A.YD_STK_LYR_NO                                       AS YD_STK_LYR_NO   ";
				szQuery+= "		      ,A.YD_CRN_SCH_ID                                       AS YD_CRN_SCH_ID   ";
				szQuery+= "		      ,B.YD_MTL_W                                            AS YD_MTL_W        ";
				szQuery+= "		      ,B.YD_MTL_WT                                           AS YD_MTL_WT       ";
				szQuery+= "		      ,B.YD_MTL_T                                            AS YD_MTL_T        ";
				szQuery+= "		      ,B.YD_MTL_ITEM                                         AS YD_MTL_ITEM     ";
				szQuery+= "		      ,B.YD_STK_LOT_TP                                       AS YD_STK_LOT_TP   ";
				szQuery+= "		      ,B.YD_STK_LOT_CD                                       AS YD_STK_LOT_CD   ";
				szQuery+= "		      ,B.YD_AIM_RT_GP                                        AS YD_AIM_RT_GP    ";
				szQuery+= "		      ,SUM(B.YD_MTL_WT) OVER (ORDER BY A.YD_STK_LYR_NO DESC) AS SUM_MTL_WT      ";
				szQuery+= "		      ,SUM(B.YD_MTL_T) OVER (ORDER BY A.YD_STK_LYR_NO DESC)  AS SUM_MTL_T       ";
				szQuery+= "		      ,COUNT(A.STL_NO) OVER (ORDER BY A.YD_STK_LYR_NO DESC)  AS SH_CNT          ";
				szQuery+= "		  FROM TB_YD_CRNWRKMTL A                                                        ";
				szQuery+= "		      ,TB_YD_STOCK     B                                                        ";
				szQuery+= "		 WHERE A.STL_NO = B.STL_NO                                                      ";
				szQuery+= "		   AND A.YD_CRN_SCH_ID = '" + szCrnSchId + "'";
				szQuery+= "		   AND (A.DEL_YN IS NULL OR A.DEL_YN <> 'Y')                                    ";
				szQuery+= "		   AND (B.DEL_YN IS NULL OR B.DEL_YN <> 'Y')                                    ";
				szQuery+= "		 ORDER BY YD_STK_LYR_NO                                                         ";
        		rsCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
        		intRtnVal = ydDBAssist.getData(szQuery, rsCrnwrkmtl, null) ;
        		
        		*/
        	
        		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
        		rsCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
        		       		
        		
        		recTempPara = JDTORecordFactory.getInstance().create();	
      		    recTempPara.setField("YD_CRN_SCH_ID", szCrnSchId);
      		    
      		    
        		intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recTempPara, rsCrnwrkmtl, 7);
        		
        		rsCrnwrkmtl.first();
        		//크레인작업재료의 최하단정보 및 전체의 누계정보
        		recCrnwrkmtl = rsCrnwrkmtl.getRecord();
        		
            	recCrnwrkmtl.setField("YD_SCH_CD"    , szSchCd);
System.out.println("************완성된 크레인 작업재료*************");
            	ydUtils.displayRecord(szOperationName, recCrnwrkmtl);
    			
            	
            	//보조작업인 경우
            	if(szToLocDcsnMtd.equals("W")) {
            		
            		//임의의 위치에 값을 저장한다.
            		intRtnVal = this.getAidWrkLoc(rsCrnwrkmtl, recCrnSch);
            		//에러메시지
            		//
            	}
   
            	//주작업이면서 보조작업처리를 할  경우
            	if(szToLocDcsnMtd.equals("M")) {
            		
            		//임의의 위치에 값을 저장한다.
            		intRtnVal = this.getMainWrkLoc(rsCrnwrkmtl, recCrnSch);
            		//에러메시지
            		//
            	}
            	
            	//주작업 순서 맞추는 작업인 경우
            	if(szToLocDcsnMtd.equals("R") || szToLocDcsnMtd.equals("B")) {
            		//크레인스케줄 지시위치에 적치단을 등록한다.
            		intRtnVal = this.getMainWrkColl(rsCrnwrkmtl, recCrnSch);
            		//에러메시지
            		//
            	}            	
     
            	
            	//완성된 주작업 인 경우
            	if(szToLocDcsnMtd.equals("T") || szToLocDcsnMtd.equals("S")) {
	            	//2. 위치검색범위및위치검색Bed조회
	    			intRtnVal = this.getLocSrchRng(rsCrnwrkmtl, recCrnSch);
	    			//에러메시지
	    			//
            	}        	
            	
        	}//end of for        	
        	
        	
        }catch(Exception e){
			System.out.println("<LocSrcRngDataSet> Exception Error :"+ e.getLocalizedMessage());
			return intRtnVal = -1;
        }//end of try~catch
        
        
        return intRtnVal = 1;
    	
    }//end of LocSrcRngDataSet()
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 보조작업위치(나선형검색)
     *  
     * @param  rsResultCrnwrkmtl, rsBed
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int getAidWrkLoc (JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch){
    	//상위 Method : 
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 나선형으로 베드를 검색하여 저장위치를 구한다.
		//┗━┛
    	YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

    	JDTORecord recMinCrnwrkmtl = null;
    	JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	String szColGp      = "";
    	String szBedNo      = "";
    	
    	String szMsg        = "";
    	String szMethodName = "";
    	
    	int intRtnVal = 0;
    	//증가 감소 구분자
    	int intGp = 0;
    	//열증가 및 감소
    	int intTemp = 0;
    	
        try{
        	//작업재료의 Data를 정리한다.
        	rsCrnwrkmtl.absolute(1);
        	recMinCrnwrkmtl = rsCrnwrkmtl.getRecord();
        	
        	//현재 작업재료들의 Map정보를 알기위해 적치단을 조회
        	recMinCrnwrkmtl.setField("YD_STK_LYR_MTL_STAT", "U");
        	intRtnVal = ydStkLyrDao.getYdStklyr(recMinCrnwrkmtl, outRecSet, 3);
        	if(intRtnVal <= 0){
	    		if(intRtnVal == 0) {
	    			szMsg = "data not found";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    		}
	    		if(intRtnVal == -2) {
	    			szMsg = "parameter error";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    		}
	    		return intRtnVal = -1;
	    	}
        	
        	//조회한 적치단의 정보 중 권상대기인 재료의 Map정보를 가져온다.
        	for(int Loop_i = 1; Loop_i <= outRecSet.size(); Loop_i++) {
        		outRecSet.absolute(Loop_i);
        		recMinCrnwrkmtl = outRecSet.getRecord();
        		//권상대기인것
        		if(recMinCrnwrkmtl.getFieldString("YD_STK_LYR_MTL_STAT").equals("U") ) {
        			szColGp = recMinCrnwrkmtl.getFieldString("YD_STK_COL_GP");
        			szBedNo = recMinCrnwrkmtl.getFieldString("YD_STK_BED_NO");
        			break;
        		}
        	}
        	
        	
        	for(int Loop_i = 1; Loop_i <= 3; Loop_i++) {
        		//베드와 열이 증가할 경우
        		if(intGp == 0) {
        			
        			for(int Loop_j = 1; Loop_j <= Loop_i; Loop_j++) {
        				//Bed증가
        				intTemp = Integer.parseInt(szBedNo) + 1;
        				if (intTemp < 10)
        					szBedNo = "0" + intTemp;
        				else if (intTemp > 9 && intTemp < 99)
        					szBedNo = "" + intTemp;
        				
        				//조건Check 적치베드상태에 작업재료 Data와 비교 메소드 호출
        				intRtnVal = this.chkAidStkBed(rsCrnwrkmtl, szColGp, szBedNo, recCrnSch);
        				if(intRtnVal == 1) {
        					//등록
        					return intRtnVal;
        				}
System.out.println("열구분 :"+ szColGp);
System.out.println("BED번호 :"+ szBedNo);
        			}
        			
        			for(int Loop_j = 1; Loop_j <= Loop_i; Loop_j++) {
        				//열증가
        				intTemp = Integer.parseInt(szColGp.substring(4)) + 1;
        				if (intTemp < 10)
        					szColGp = szColGp.substring(0,4) + "0" + intTemp;
        				else if (intTemp > 9 && intTemp < 99)
        					szColGp = szColGp.substring(0,4) + "" + intTemp;
        				
        				
        				
        				
        				//조건Check 적치베드상태에 작업재료 Data와 비교 메소드 호출
        				intRtnVal = this.chkAidStkBed(rsCrnwrkmtl, szColGp, szBedNo, recCrnSch);
        				if(intRtnVal == 1) {
        					//등록
        					return intRtnVal;
        				}
System.out.println("열구분 :"+ szColGp);
System.out.println("BED번호 :"+ szBedNo);        				
        			}
        			
        			//감소 구분자로 Set
        			intGp = 1;
        			
        		//베드와 열이 감소할 경우	
        		}else{
        			
        			for(int Loop_j = 1; Loop_j <= Loop_i; Loop_j++) {
        				//Bed감소
        				intTemp = Integer.parseInt(szBedNo) - 1;
        				if (intTemp < 10)
        					szBedNo = "0" + intTemp;
        				else if (intTemp > 9 && intTemp < 99)
        					szBedNo = "" + intTemp;
        				
        				//조건Check 적치베드상태에 작업재료 Data와 비교 메소드 호출
        				intRtnVal = this.chkAidStkBed(rsCrnwrkmtl, szColGp, szBedNo, recCrnSch);
        				if(intRtnVal == 1) {
        					//등록
        					return intRtnVal;
        				}
System.out.println("열구분 :"+ szColGp);
System.out.println("BED번호 :"+ szBedNo);
        			}
        			
        			for(int Loop_j = 1; Loop_j <= Loop_i; Loop_j++) {
        				//열감소
        				intTemp = Integer.parseInt(szColGp.substring(4)) - 1;
        				if (intTemp < 10)
        					szColGp = szColGp.substring(0,4) + "0" + intTemp;
        				else if (intTemp > 9 && intTemp < 99)
        					szColGp = szColGp.substring(0,4) + "" + intTemp;
        				
        				//조건Check 적치베드상태에 작업재료 Data와 비교 메소드 호출
        				intRtnVal = this.chkAidStkBed(rsCrnwrkmtl, szColGp, szBedNo, recCrnSch);
        				if(intRtnVal == 1) {
        					//등록
        					return intRtnVal;
        				}
System.out.println("열구분 :"+ szColGp);
System.out.println("BED번호 :"+ szBedNo);
        			}//end of for
        			//증가 구분자로 Set
        			intGp = 0;
        		}//end of if
        	}//end of for
        	
        	
        	System.out.println("적치가능한 BED가 없습니다.");
        	return intRtnVal = -1;
        	
        }catch(Exception e){
			System.out.println("<getAidWrkLoc> Exception Error :"+ e.getLocalizedMessage());
			return intRtnVal = -1;
        }//end of try~catch

    	
    }//end of getAidWrkLoc()
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 적치Bed와 작업재료사양 비교 Check
     *  
     * @param  rsCrnwrkmtl
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int chkAidStkBed (JDTORecordSet rsCrnwrkmtl, String szColGp, String szBedNo, JDTORecord recCrnSch){
    	//상위 Method : 
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
		//┗━┛
    	YdStkBedDao ydStkBedDao = new YdStkBedDao();
    	YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao(); 
    	YdDBAssist   ydDBAssist = new YdDBAssist();
    	YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
    	
    	//적치Bed를 조회한 정보
    	JDTORecordSet rsGetStkBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	//적치단을 조회한 정보
    	JDTORecord recStkLyr = null;
    	JDTORecordSet rsGetStkLyr = null;
    	
    	//크레인작업재료 정보
    	JDTORecord recGetCrnWrkMtl = null;
    	JDTORecordSet rsResultCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
    	//작업예약재료의 베드별 최하단 정보만 가져온다.
    	JDTORecord recWrkbookmtl = null;
    	JDTORecordSet rsWrkbookmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	//파라미터 rsBed를 조회
    	JDTORecord recStkBed      = null;
    	//적치Bed를 조회한 정보
    	JDTORecord recGetRsSet    = null;
    	JDTORecord recBedSet      = null;
    
    	
    	
    	int intRtnVal 		= 0 ;
        String szMsg        = "";
        String szMethodName = "craneLdHd";
        String szOperationName = "적치Bed와 작업재료사양 비교 Check";
        //적치베드의 Max단,중량,높이
        int intStkBedLyrMax        = 0;
        long lngStkBedWtMax        = 0;
        long lngStkBedHMax         = 0;
        
        //크레인작업재료의 총매수,중량,높이
        int intCrnWrkMtlSh         = 0;
        long lngCrnWrkMtlWt        = 0;
        long lngCrnWrkMtlT         = 0;
        
        //적치단의 적치중인 재료의 총매수,중량,높이
        int intStkLyrMax           = 0;
        long lngStkLyrWtMax 	   = 0;
        long lngStkLyrHMax         = 0;
        
        //크레인작업재료의 최하단 산적LotType,산적Lot코드,폭
        String szCrnWrkMtlStkLotTp = "";
        String szCrnWrkMtlStkLotCd = "";
        long lngCrnWrkMtlW		   = 0;
        
        //적치단의 최상단 산적LotType,산적Lot코드,폭
        String szStkLyrStkLotTp    = "";
        String szStkLyrStkLotCd    = "";
        long lngStkLyrW		       = 0;
        
        String szStkColGp          = "";
        String szStkBedNo          = ""; 
        String szSchCd             = "";
        
        String szWbookMtlCol       = "";
        String szWbookMtlBed       = "";

        
        String szQuery = "";
        
    	int intGp = 0;
    	JDTORecord recTempPara = null; 
    	
        try{
        	rsCrnwrkmtl.absolute(1);
        	recGetCrnWrkMtl = rsCrnwrkmtl.getRecord();
        	
        	szSchCd = recCrnSch.getFieldString("YD_SCH_CD");
        	
        	//크레인 작업예약 재료벌 최하단 정보만 가져온다. (나선형 검색중 작업예약재료가 있는 적치단은 적치하지않고 지나가기 위해....)
        	
        	/* 쿼리수정_요청
        	szQuery  = "	SELECT YD_WBOOK_ID        AS YD_WBOOK_ID                               ";
			szQuery += "	      ,YD_STK_COL_GP      AS YD_STK_COL_GP                             ";
			szQuery += "	      ,YD_STK_BED_NO      AS YD_STK_BED_NO                             ";
			szQuery += "	      ,MIN(YD_STK_LYR_NO) AS YD_STK_LYR_NO                             ";
			szQuery += "	  FROM TB_YD_WRKBOOKMTL                                                ";
			szQuery += "	 WHERE YD_WBOOK_ID = (SELECT YD_WBOOK_ID                               ";
			szQuery += "	                        FROM (SELECT YD_WBOOK_ID                       ";
			szQuery += "	                                FROM TB_YD_WRKBOOK                     ";
			szQuery += "	                               WHERE YD_SCH_CD = '" + szSchCd +     "' ";
			szQuery += "	                                 AND (DEL_YN <> 'Y' OR DEL_YN IS NULL) ";
			szQuery += "	                               ORDER BY YD_WBOOK_ID)                   ";
			szQuery += "	                       WHERE ROWNUM < '2')                             ";
			szQuery += "	   AND (DEL_YN <> 'Y' OR DEL_YN IS NULL)                               ";
			szQuery += "	 GROUP BY YD_WBOOK_ID, YD_STK_COL_GP, YD_STK_BED_NO                    ";
			szQuery += "	 ORDER BY YD_STK_COL_GP, YD_STK_BED_NO DESC                            ";
			intRtnVal = ydDBAssist.getData(szQuery, rsWrkbookmtl, null) ;
			*/
        	
        	
			YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
			
        	
			recTempPara = JDTORecordFactory.getInstance().create();	
			recTempPara.setField("YD_SCH_CD", szSchCd);
			ydWrkbookMtlDao.getYdWrkbookmtl(recTempPara, rsWrkbookmtl, 33);
			
			
			
        	
        	//적치Bed조회한다.
        	recStkBed = JDTORecordFactory.getInstance().create();
        	recStkBed.setField("YD_STK_COL_GP", szColGp);
        	recStkBed.setField("YD_STK_BED_NO", szBedNo);

        	
        	
    		rsGetStkBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    		intRtnVal = ydStkBedDao.getYdStkbed(recStkBed, rsGetStkBed, 0) ;
ydUtils.displayRecord(szOperationName, recStkBed);

    		if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    			}else if(intRtnVal == -2) {
    				szMsg="parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			System.out.println("적치Bed 조회가 되지 않았습니다");
    			return intRtnVal = -1;
    		}
    		
    		
System.out.println("적치베드의 맥스값가져오기전 ..... : "+rsGetStkBed.size());
    		//적치베드의 Max단,중량,높이
			rsGetStkBed.absolute(1);
    		recGetRsSet = rsGetStkBed.getRecord();
    		ydUtils.displayRecord(szOperationName, recGetRsSet);
    		
    		
    		//적치 베드 입출고 상태 조회  (입출고상태가 "F"완산 BED라면 더이상 적치가 되지 않고 출고는 가능하다.)
    		if (recGetRsSet.getFieldString("YD_STK_BED_WHIO_STAT").equals("F") ) {
    			System.out.println("적치베드 상태가 입고 금지 상태입니다.");
    			return intRtnVal = -1;
    		}
    		
    		//크레인 작업재료가있는 베드 조회
    		for(int Loop_i = 1; Loop_i <= rsWrkbookmtl.size(); Loop_i++) {
    			rsWrkbookmtl.absolute(Loop_i);
    			recWrkbookmtl = rsWrkbookmtl.getRecord();
    			szWbookMtlCol = recWrkbookmtl.getFieldString("YD_STK_COL_GP");
    			szWbookMtlBed = recWrkbookmtl.getFieldString("YD_STK_BED_NO");
    			
    			if(szWbookMtlCol.equals(szColGp) && szWbookMtlBed.equals(szBedNo)) {
    				System.out.println("현재 작업예약재료가 적치중인 Bed입니다.");
        			return intRtnVal = -1;
    			}
    		}
    		
    		intStkBedLyrMax = ydDaoUtils.paraRecChkNullInt (recGetRsSet,"YD_STK_BED_LYR_MAX");
    		lngStkBedWtMax  = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"YD_STK_BED_WT_MAX");       
    		lngStkBedHMax   = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"YD_STK_BED_H_MAX"); 
    		
    		System.out.println("맥스단 : " + intStkBedLyrMax);
    		System.out.println("맥스중량 : " + lngStkBedWtMax);
    		System.out.println("맥스높이 : " + lngStkBedHMax);
    		
System.out.println("적치베드의 맥스값 가져오고  크레인 작업재료 총매수 중량 높이 가져오기전..... : ");
			
    		//크레인작업재료의 총매수 총중량 총높이를 가져온다. 		
    		intCrnWrkMtlSh      = ydDaoUtils.paraRecChkNullInt (recGetCrnWrkMtl,"SH_CNT");
    		lngCrnWrkMtlWt      = ydDaoUtils.paraRecChkNullLong(recGetCrnWrkMtl,"SUM_MTL_WT");
    		lngCrnWrkMtlT       = ydDaoUtils.paraRecChkNullLong(recGetCrnWrkMtl,"SUM_MTL_T");
    		
System.out.println("크레인 작업재료 최하단 재료정보 가져오기전... : ");

    		//크레인작업재료의 최하단 재료정보
    		lngCrnWrkMtlW       = ydDaoUtils.paraRecChkNullLong(recGetCrnWrkMtl,"SUM_MTL_W");
    		szCrnWrkMtlStkLotTp = ydDaoUtils.paraRecChkNull    (recGetCrnWrkMtl,"YD_STK_LOT_TP");
    		szCrnWrkMtlStkLotCd = ydDaoUtils.paraRecChkNull    (recGetCrnWrkMtl,"YD_STK_LOT_CD");
System.out.println("크레인작업재료 정보 : " + intRtnVal);

ydUtils.displayRecord(szOperationName, recGetCrnWrkMtl);

    		//적치Bed의 적치중이거나 권하대기상태인 재료정보를 가져온다.
			//적치단의 재료의 합계정보
			recStkLyr = JDTORecordFactory.getInstance().create();
			recStkLyr.setField("YD_STK_COL_GP", szColGp);
			recStkLyr.setField("YD_STK_BED_NO", szBedNo);
			
			rsGetStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
			intRtnVal = ydStkLyrDao.getYdStklyr(recStkLyr, rsGetStkLyr, 6);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				System.out.println("적치단이 조회가 되지 않았습니다");
			}
			if(intRtnVal == 0) {
				System.out.println("공베드입니다.");
			}


    		
			if(rsGetStkLyr.size() > 0) {
				rsGetStkLyr.absolute(1);
				recGetRsSet = rsGetStkLyr.getRecord();
	    		ydUtils.displayRecord(szOperationName, recGetRsSet);
        		//적치단에적치중인 총매수 중량 높이
        		intStkLyrMax     = ydDaoUtils.paraRecChkNullInt (recGetRsSet,"SH_CNT");
       			lngStkLyrWtMax   = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"SUM_MTL_WT");
       			lngStkLyrHMax    = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"SUM_MTL_T");
       			
       			//적치단의최상단  재료정보
       			lngStkLyrW       = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"SUM_MTL_W");
       			szStkLyrStkLotTp = ydDaoUtils.paraRecChkNull    (recGetRsSet,"YD_STK_LOT_TP");
       			szStkLyrStkLotCd = ydDaoUtils.paraRecChkNull    (recGetRsSet,"YD_STK_LOT_CD");
    		}else{
    			intStkLyrMax = 0;
    			lngStkLyrWtMax = 0;
    			lngStkLyrHMax = 0;
    			lngStkLyrW = 0;
    			szStkLyrStkLotTp = "";
    			szStkLyrStkLotCd = "";
    			
    			recGetRsSet = JDTORecordFactory.getInstance().create();
    			//공베드인경우에 적치단에 등록하기위해 베드정보까진 저장...
    			recGetRsSet.setField("YD_STK_COL_GP", szColGp);
       			recGetRsSet.setField("YD_STK_BED_NO", szBedNo);
       			recGetRsSet.setField("YD_STK_LYR_NO", "");
    		}
   			
   			System.out.println(intStkBedLyrMax + " < " +  intCrnWrkMtlSh + " + " + intStkLyrMax);
   			System.out.println(lngStkBedWtMax + " < " +  lngCrnWrkMtlWt + " + " + lngStkLyrWtMax);
   			System.out.println(lngStkBedHMax + " < " +  lngCrnWrkMtlT + " + " + lngStkLyrHMax);
   			
   			if(intStkBedLyrMax < intCrnWrkMtlSh + intStkLyrMax)   return intRtnVal = -1;
   			if(lngStkBedWtMax  < lngCrnWrkMtlWt + lngStkLyrWtMax) return intRtnVal = -1;
   			if(lngStkBedHMax   < lngCrnWrkMtlT  + lngStkLyrHMax)  return intRtnVal = -1;
   			
   			//적치단에 크레인작업재료를 등록한다.
   			recBedSet = JDTORecordFactory.getInstance().create();
   			recBedSet.setField("YD_STK_COL_GP", szColGp);
   			recBedSet.setField("YD_STK_BED_NO", szBedNo);
   			if(rsGetStkLyr.size() > 0) {
   				recBedSet.setField("YD_STK_LYR_NO", recGetRsSet.getFieldString("YD_STK_LYR_NO"));
   			}else{
   				recBedSet.setField("YD_STK_LYR_NO", "");
   			}
   			
   			intRtnVal = this.updGradStkLyr(rsCrnwrkmtl, recBedSet);
   			

        }catch(Exception e){
			System.out.println("<chkStkBed> Exception Error :"+ e.getLocalizedMessage());
			return intRtnVal = -1;
        }//end of try~catch
        
        
        return intRtnVal = 1;
    	
    }//end of chkAidStkBed()
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 주작업이지만 보조작업처리위치(나선형검색)
     *  
     * @param  rsResultCrnwrkmtl, rsBed
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int getMainWrkLoc (JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch){
    	//상위 Method : 
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 나선형으로 베드를 검색하여 저장위치를 구한다.
		//┗━┛
    	YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

    	JDTORecord recMinCrnwrkmtl = null;
    	JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	String szColGp      = "";
    	String szBedNo      = "";
    	
    	String szMsg        = "";
    	String szMethodName = "";
    	
    	int intRtnVal = 0;
    	//증가 감소 구분자
    	int intGp = 0;
    	//열증가 및 감소
    	int intTemp = 0;
    	
        try{
        	//작업재료의 Data를 정리한다.
        	rsCrnwrkmtl.absolute(1);
        	recMinCrnwrkmtl = rsCrnwrkmtl.getRecord();
        	
        	//현재 작업재료들의 Map정보를 알기위해 적치단을 조회
        	recMinCrnwrkmtl.setField("YD_STK_LYR_MTL_STAT", "U");
        	intRtnVal = ydStkLyrDao.getYdStklyr(recMinCrnwrkmtl, outRecSet, 3);
        	if(intRtnVal <= 0){
	    		if(intRtnVal == 0) {
	    			szMsg = "data not found";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    		}
	    		if(intRtnVal == -2) {
	    			szMsg = "parameter error";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    		}
	    		return intRtnVal = -1;
	    	}
        	
        	//조회한 적치단의 정보 중 권상대기인 재료의 Map정보를 가져온다.
        	for(int Loop_i = 1; Loop_i <= outRecSet.size(); Loop_i++) {
        		outRecSet.absolute(Loop_i);
        		recMinCrnwrkmtl = outRecSet.getRecord();
        		//권상대기인것
        		if(recMinCrnwrkmtl.getFieldString("YD_STK_LYR_MTL_STAT").equals("U") ) {
        			szColGp = recMinCrnwrkmtl.getFieldString("YD_STK_COL_GP");
        			szBedNo = recMinCrnwrkmtl.getFieldString("YD_STK_BED_NO");
        			break;
        		}
        	}
        	
        	
        	for(int Loop_i = 1; Loop_i <= 3; Loop_i++) {
        		//베드와 열이 증가할 경우
        		if(intGp == 0) {
        			
        			for(int Loop_j = 1; Loop_j <= Loop_i; Loop_j++) {
        				//Bed증가
        				intTemp = Integer.parseInt(szBedNo) + 1;
        				if (intTemp < 10)
        					szBedNo = "0" + intTemp;
        				else if (intTemp > 9 && intTemp < 99)
        					szBedNo = "" + intTemp;
        				
        				//조건Check 적치베드상태에 작업재료 Data와 비교 메소드 호출
        				intRtnVal = this.chkMainStkBed(rsCrnwrkmtl, szColGp, szBedNo, recCrnSch);
        				if(intRtnVal == 1) {
        					//등록
        					System.out.println("열구분 : "+ szColGp + ", BED번호 :"+ szBedNo + "위치에 등록되었습니다.");
        					return intRtnVal;
        				}
System.out.println("열구분 :"+ szColGp);
System.out.println("BED번호 :"+ szBedNo);
        			}
        			
        			for(int Loop_j = 1; Loop_j <= Loop_i; Loop_j++) {
        				//열증가
        				intTemp = Integer.parseInt(szColGp.substring(4)) + 1;
        				if (intTemp < 10)
        					szColGp = szColGp.substring(0,4) + "0" + intTemp;
        				else if (intTemp > 9 && intTemp < 99)
        					szColGp = szColGp.substring(0,4) + "" + intTemp;
        				
        				
        				
        				
        				//조건Check 적치베드상태에 작업재료 Data와 비교 메소드 호출
        				intRtnVal = this.chkMainStkBed(rsCrnwrkmtl, szColGp, szBedNo, recCrnSch);
        				if(intRtnVal == 1) {
        					//등록
        					System.out.println("열구분 : "+ szColGp + ", BED번호 :"+ szBedNo + "위치에 등록되었습니다.");
        					return intRtnVal;
        				}
System.out.println("열구분 :"+ szColGp);
System.out.println("BED번호 :"+ szBedNo);        				
        			}
        			
        			//감소 구분자로 Set
        			intGp = 1;
        			
        		//베드와 열이 감소할 경우	
        		}else{
        			
        			for(int Loop_j = 1; Loop_j <= Loop_i; Loop_j++) {
        				//Bed감소
        				intTemp = Integer.parseInt(szBedNo) - 1;
        				if (intTemp < 10)
        					szBedNo = "0" + intTemp;
        				else if (intTemp > 9 && intTemp < 99)
        					szBedNo = "" + intTemp;
        				
        				//조건Check 적치베드상태에 작업재료 Data와 비교 메소드 호출
        				intRtnVal = this.chkMainStkBed(rsCrnwrkmtl, szColGp, szBedNo, recCrnSch);
        				if(intRtnVal == 1) {
        					//등록
        					System.out.println("열구분 : "+ szColGp + ", BED번호 :"+ szBedNo + "위치에 등록되었습니다.");
        					return intRtnVal;
        				}
System.out.println("열구분 :"+ szColGp);
System.out.println("BED번호 :"+ szBedNo);
        			}
        			
        			for(int Loop_j = 1; Loop_j <= Loop_i; Loop_j++) {
        				//열감소
        				intTemp = Integer.parseInt(szColGp.substring(4)) - 1;
        				if (intTemp < 10)
        					szColGp = szColGp.substring(0,4) + "0" + intTemp;
        				else if (intTemp > 9 && intTemp < 99)
        					szColGp = szColGp.substring(0,4) + "" + intTemp;
        				
        				//조건Check 적치베드상태에 작업재료 Data와 비교 메소드 호출
        				intRtnVal = this.chkMainStkBed(rsCrnwrkmtl, szColGp, szBedNo, recCrnSch);
        				if(intRtnVal == 1) {
        					//등록
        					System.out.println("열구분 : "+ szColGp + ", BED번호 :"+ szBedNo + "위치에 등록되었습니다.");
        					return intRtnVal;
        				}
System.out.println("열구분 :"+ szColGp);
System.out.println("BED번호 :"+ szBedNo);
        			}//end of for
        			//증가 구분자로 Set
        			intGp = 0;
        		}//end of if
        	}//end of for
        	
        	
        	System.out.println("적치가능한 BED가 없습니다.");
        	return intRtnVal = -1;
        	
        }catch(Exception e){
			System.out.println("<getMainWrkLoc> Exception Error :"+ e.getLocalizedMessage());
			return intRtnVal = -1;
        }//end of try~catch
    	
    }//end of getMainWrkLoc()
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 적치Bed와 작업재료사양 비교 Check
     *  
     * @param  rsCrnwrkmtl
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int chkMainStkBed (JDTORecordSet rsCrnwrkmtl, String szColGp, String szBedNo, JDTORecord recCrnSch){
    	//상위 Method : DnLocInsMain
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
		//┗━┛
    	YdStkBedDao ydStkBedDao = new YdStkBedDao();
    	YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao(); 
    	YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
    	YdDBAssist   ydDBAssist = new YdDBAssist();
    	
    	
    	
    	//적치Bed를 조회한 정보
    	JDTORecordSet rsGetStkBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	//적치단을 조회한 정보
    	
    	JDTORecord recStkLyr      = null;
    	JDTORecordSet rsGetStkLyr = null;
    	//크레인작업재료 정보
    	JDTORecord recGetCrnWrkMtl = null;
    	JDTORecordSet rsResultCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
    	//작업예약재료의 베드별 최하단 정보만 가져온다.
    	JDTORecord recWrkbookmtl = null;
    	JDTORecordSet rsWrkbookmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	//파라미터 rsBed를 조회
    	JDTORecord recStkBed      = null;
    	//적치Bed를 조회한 정보
    	JDTORecord recGetRsSet    = null;
    	JDTORecord recBedSet      = null;
    	
    	
    	int intRtnVal 		= 0 ;
        String szMsg        = "";
        String szMethodName = "craneLdHd";
        String szOperationName = "적치Bed와 작업재료사양 비교 Check";
        //적치베드의 Max단,중량,높이
        int intStkBedLyrMax        = 0;
        long lngStkBedWtMax        = 0;
        long lngStkBedHMax         = 0;
        
        //크레인작업재료의 총매수,중량,높이
        int intCrnWrkMtlSh         = 0;
        long lngCrnWrkMtlWt        = 0;
        long lngCrnWrkMtlT         = 0;
        
        //적치단의 적치중인 재료의 총매수,중량,높이
        int intStkLyrMax           = 0;
        long lngStkLyrWtMax 	   = 0;
        long lngStkLyrHMax         = 0;
        
        //크레인작업재료의 최하단 산적LotType,산적Lot코드,폭
        String szCrnWrkMtlStkLotTp = "";
        String szCrnWrkMtlStkLotCd = "";
        long lngCrnWrkMtlW		   = 0;
        
        //적치단의 최상단 산적LotType,산적Lot코드,폭
        String szStkLyrStkLotTp    = "";
        String szStkLyrStkLotCd    = "";
        long lngStkLyrW		       = 0;
        
        String szStkColGp          = "";
        String szStkBedNo          = ""; 
        String szSchCd             = "";
        
        String szWbookMtlCol       = "";
        String szWbookMtlBed       = "";

        
        String szQuery = "";
        
    	int intGp = 0;
    	JDTORecord recTempPara      = null;
    	
        try{
        	rsCrnwrkmtl.absolute(1);
        	recGetCrnWrkMtl = rsCrnwrkmtl.getRecord();
        	
        	szSchCd = recCrnSch.getFieldString("YD_SCH_CD");
        	
        	
        	/* 쿼리수정_요청
        	//작업예약재료의 재료의 각 BED의 최하단 정보만 가져온다. (나선형 검색중 작업예약재료가 있는 적치단은 적치하지않고 지나가기 위해....)
        	szQuery  = "	SELECT YD_WBOOK_ID        AS YD_WBOOK_ID                               ";
			szQuery += "	      ,YD_STK_COL_GP      AS YD_STK_COL_GP                             ";
			szQuery += "	      ,YD_STK_BED_NO      AS YD_STK_BED_NO                             ";
			szQuery += "	      ,MIN(YD_STK_LYR_NO) AS YD_STK_LYR_NO                             ";
			szQuery += "	  FROM TB_YD_WRKBOOKMTL                                                ";
			szQuery += "	 WHERE YD_WBOOK_ID = (SELECT YD_WBOOK_ID                               ";
			szQuery += "	                        FROM (SELECT YD_WBOOK_ID                       ";
			szQuery += "	                                FROM TB_YD_WRKBOOK                     ";
			szQuery += "	                               WHERE YD_SCH_CD = '" + szSchCd +     "' ";
			szQuery += "	                                 AND (DEL_YN <> 'Y' OR DEL_YN IS NULL) ";
			szQuery += "	                               ORDER BY YD_WBOOK_ID)                   ";
			szQuery += "	                       WHERE ROWNUM < '2')                             ";
			szQuery += "	   AND (DEL_YN <> 'Y' OR DEL_YN IS NULL)                               ";
			szQuery += "	 GROUP BY YD_WBOOK_ID, YD_STK_COL_GP, YD_STK_BED_NO                    ";
			szQuery += "	 ORDER BY YD_STK_COL_GP, YD_STK_BED_NO DESC                            ";
			intRtnVal = ydDBAssist.getData(szQuery, rsWrkbookmtl, null) ;
        	
			*/
			
			YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
			recTempPara = JDTORecordFactory.getInstance().create();	
			recTempPara.setField("YD_SCH_CD", szSchCd);
			ydWrkbookMtlDao.getYdWrkbookmtl(recTempPara, rsWrkbookmtl, 33);
			
			
			
			
			
			
        	//적치Bed조회한다.
        	recStkBed = JDTORecordFactory.getInstance().create();
        	recStkBed.setField("YD_STK_COL_GP", szColGp);
        	recStkBed.setField("YD_STK_BED_NO", szBedNo);

        	
        	
    		rsGetStkBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    		intRtnVal = ydStkBedDao.getYdStkbed(recStkBed, rsGetStkBed, 0) ;
ydUtils.displayRecord(szOperationName, recStkBed);
    		if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    			}else if(intRtnVal == -2) {
    				szMsg="parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			System.out.println("적치Bed 조회가 되지 않았습니다");
    			return intRtnVal = -1;
    		}
    		
    		
System.out.println("적치베드의 맥스값가져오기전 ..... : "+rsGetStkBed.size());
    		//적치베드의 Max단,중량,높이
			rsGetStkBed.absolute(1);
    		recGetRsSet = rsGetStkBed.getRecord();
    		ydUtils.displayRecord(szOperationName, recGetRsSet);
    		
    		//적치 베드 입출고 상태 조회  (입출고상태가 "F"완산 BED라면 더이상 적치가 되지 않고 출고는 가능하다.)
    		if (recGetRsSet.getFieldString("YD_STK_BED_WHIO_STAT").equals("F") ) {
    			System.out.println("적치베드 상태가 입고 금지 상태입니다.");
    			return intRtnVal = -1;
    		}
    		
    		//크레인 작업재료가있는 베드 조회
    		for(int Loop_i = 1; Loop_i <= rsWrkbookmtl.size(); Loop_i++) {
    			rsWrkbookmtl.absolute(Loop_i);
    			recWrkbookmtl = rsWrkbookmtl.getRecord();
    			szWbookMtlCol = recWrkbookmtl.getFieldString("YD_STK_COL_GP");
    			szWbookMtlBed = recWrkbookmtl.getFieldString("YD_STK_BED_NO");
    			
    			if(szWbookMtlCol.equals(szColGp) && szWbookMtlBed.equals(szBedNo)) {
    				System.out.println("현재 작업예약재료가 적치중인 Bed입니다.");
        			return intRtnVal = -1;
    			}
    		}
    		
    		intStkBedLyrMax = ydDaoUtils.paraRecChkNullInt (recGetRsSet,"YD_STK_BED_LYR_MAX");
    		lngStkBedWtMax  = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"YD_STK_BED_WT_MAX");       
    		lngStkBedHMax   = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"YD_STK_BED_H_MAX"); 
    		
    		System.out.println("맥스단 : " + intStkBedLyrMax);
    		System.out.println("맥스중량 : " + lngStkBedWtMax);
    		System.out.println("맥스높이 : " + lngStkBedHMax);
    		
System.out.println("적치베드의 맥스값 가져오고  크레인 작업재료 총매수 중량 높이 가져오기전..... : ");
    		
    		//크레인작업재료의 총매수 총중량 총높이를 가져온다. 		
    		intCrnWrkMtlSh      = ydDaoUtils.paraRecChkNullInt (recGetCrnWrkMtl,"SH_CNT");
    		lngCrnWrkMtlWt      = ydDaoUtils.paraRecChkNullLong(recGetCrnWrkMtl,"SUM_MTL_WT");
    		lngCrnWrkMtlT       = ydDaoUtils.paraRecChkNullLong(recGetCrnWrkMtl,"SUM_MTL_T");
    		
System.out.println("크레인 작업재료 최하단 재료정보 가져오기전... : ");

    		//크레인작업재료의 최하단 재료정보
    		lngCrnWrkMtlW       = ydDaoUtils.paraRecChkNullLong(recGetCrnWrkMtl,"SUM_MTL_W");
    		szCrnWrkMtlStkLotTp = ydDaoUtils.paraRecChkNull    (recGetCrnWrkMtl,"YD_STK_LOT_TP");
    		szCrnWrkMtlStkLotCd = ydDaoUtils.paraRecChkNull    (recGetCrnWrkMtl,"YD_STK_LOT_CD");
System.out.println("크레인작업재료 정보 : " + intRtnVal);

ydUtils.displayRecord(szOperationName, recGetCrnWrkMtl);
			//적치Bed의 적치중이거나 권하대기상태인 재료정보를 가져온다.
			//적치단의 재료의 합계정보
			recStkLyr = JDTORecordFactory.getInstance().create();
			recStkLyr.setField("YD_STK_COL_GP", szColGp);
			recStkLyr.setField("YD_STK_BED_NO", szBedNo);
			
			rsGetStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
			intRtnVal = ydStkLyrDao.getYdStklyr(recStkLyr, rsGetStkLyr, 6);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				System.out.println("적치단이 조회가 되지 않았습니다");
			}
			if(intRtnVal == 0) {
				System.out.println("공베드입니다.");
			}
			
System.out.println("적치베드의 총 갯수*************" + rsGetStkLyr.size());
			
			
			if(rsGetStkLyr.size() > 0) {
				rsGetStkLyr.absolute(1);
				recGetRsSet = rsGetStkLyr.getRecord();
        		ydUtils.displayRecord(szOperationName, recGetRsSet);
        		
        		//적치단에적치중인 총매수 중량 높이
        		intStkLyrMax     = ydDaoUtils.paraRecChkNullInt (recGetRsSet,"SH_CNT");
       			lngStkLyrWtMax   = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"SUM_MTL_WT");
       			lngStkLyrHMax    = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"SUM_MTL_T");
       			//적치단의최상단  재료정보
       			lngStkLyrW       = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"SUM_MTL_W");
       			szStkLyrStkLotTp = ydDaoUtils.paraRecChkNull    (recGetRsSet,"YD_STK_LOT_TP");
       			szStkLyrStkLotCd = ydDaoUtils.paraRecChkNull    (recGetRsSet,"YD_STK_LOT_CD");
    		}else{
    			intStkLyrMax = 0;
    			lngStkLyrWtMax = 0;
    			lngStkLyrHMax = 0;
    			lngStkLyrW = 0;
    			szStkLyrStkLotTp = "";
    			szStkLyrStkLotCd = "";
    			
    			recGetRsSet = JDTORecordFactory.getInstance().create();
    			//공베드인경우에 적치단에 등록하기위해 베드정보까진 저장...
    			recGetRsSet.setField("YD_STK_COL_GP", szColGp);
       			recGetRsSet.setField("YD_STK_BED_NO", szBedNo);
       			recGetRsSet.setField("YD_STK_LYR_NO", "");
    		}
   			
   			System.out.println(intStkBedLyrMax + " < " +  intCrnWrkMtlSh + " + " + intStkLyrMax);
   			System.out.println(lngStkBedWtMax + " < " +  lngCrnWrkMtlWt + " + " + lngStkLyrWtMax);
   			System.out.println(lngStkBedHMax + " < " +  lngCrnWrkMtlT + " + " + lngStkLyrHMax);
   			
   			if(intStkBedLyrMax < intCrnWrkMtlSh + intStkLyrMax)   return intRtnVal = -1;
   			if(lngStkBedWtMax  < lngCrnWrkMtlWt + lngStkLyrWtMax) return intRtnVal = -1;
   			if(lngStkBedHMax   < lngCrnWrkMtlT  + lngStkLyrHMax)  return intRtnVal = -1;
   			
   			
   			
   			//적치단에 크레인작업재료를 등록한다.
   			recBedSet = JDTORecordFactory.getInstance().create();
   			recBedSet.setField("YD_STK_COL_GP", szColGp);
   			recBedSet.setField("YD_STK_BED_NO", szBedNo);
   			if(rsGetStkLyr.size() > 0) {
   				recBedSet.setField("YD_STK_LYR_NO", recGetRsSet.getFieldString("YD_STK_LYR_NO"));
   			}else{
   				recBedSet.setField("YD_STK_LYR_NO", "");
   			}
   			
   			intRtnVal = this.updGradStkLyr(rsCrnwrkmtl, recGetRsSet);
   			if(intRtnVal == 1) {
   	   			//적치Bed에 입출고가능상태를 완산Bed(입고불가) 상태로 등록한다.
   	   			recStkBed.setField("YD_STK_BED_WHIO_STAT", "F");
   	   			intRtnVal = ydStkBedDao.updYdStkbed(recStkBed, 0);
   	   			if(intRtnVal <= 0) {
	   	   			szMsg="적치베드 입출고가능상태를 완산Bed로 등록하지 못했습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
   	   			}
   			}else if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				System.out.println("적치단이 조회가 되지 않았습니다");
			}

        }catch(Exception e){
			System.out.println("<chkStkBed> Exception Error :"+ e.getLocalizedMessage());
			return intRtnVal = -1;
        }//end of try~catch
        
        
        return intRtnVal = 1;
    	
    }//end of chkStkBed()
    
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 모음작업
     *  
     * @param  rsResultCrnwrkmtl, rsBed
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int getMainWrkColl (JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch){
    	//상위 Method : DnLocInsMain
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
		//┗━┛
	
    	YdDBAssist   ydDBAssist = new YdDBAssist();
		YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		//업데이트할 크레인 스케줄
    	JDTORecord recCrnSchWoLoc = JDTORecordFactory.getInstance().create();;
		
		//크레인 작업재료의 최하단 재료정보
    	JDTORecord recMinCrnWrkMtl = null;
    	
    	
    	JDTORecord recReturnData = null;
    	JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
    	
    	//작업예약 재료
    	JDTORecord recWrkbookmtl = null;
    	
    	//작업예약 재료 조회
    	JDTORecordSet rsWrkbookmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
    	JDTORecordSet rsWrkBookMtl = null;
    	
    	String szQuery = "";
    	String szWbookId = "";

    	//권상 지시위치, 권하 지시위치
    	String szUpWoLoc      = "";
    	String szUpWoLocLayer = "";
    	String szDnWoLoc      = "";
    	String szDnWoLocLayer = "";
    	
    	String szMsg        = "";
    	String szMethodName = "getWainWrkLoc";
    	String szOperationName = "모음작업";
    	int intRtnVal = 0;
    	
    	JDTORecord recTempPara = null;
    	
        try{
        	
        	//크레인 작업재료의 최하단 재료정보를 가져온다.
        	
        	//가져온 최하단 재료 정보의 적치단 위치중 권하 대기인 위치정보를 가져온다.
        	
        	//크레인 스케줄의 작업예약 id를 가져온다.
    
        	//가져온 작업예약 ID로 작업예약 재료 TABLE를 권상모음 순서로 정렬해서 조회한다.
        	
        	//크레인 작업재료의 최하단 재료와 작업예약 재료를 비교해서 같은 것을 찾는다.
        	
        	//같은 것을 찾았다면 그 재료의 다음 권상모음순서의 재료를 찾는다.
        	
        	//다음 권상모음 순서의 재료의 적치단 위치를 조회한다. 권하 대기로 있는 것만
        	
        	//크레인 최하단 재료정보의 권하대기인 위치정보를 현 크레인스케줄의 권상지시위치에 등록한다.
        	
        	//다음 권상모음 순서의 재료의 적치단 위치에서 적치단번호 +1단을 하여 현 크레인스케줄의 권하지시위치에 등록한다.
        	
        	
System.out.println("크레인 작업재료의 최하단 재료정보를 가져온다.");        	
        	//크레인 작업재료의 최하단 재료정보를 가져온다.
        	rsCrnwrkmtl.absolute(1);
        	recMinCrnWrkMtl = rsCrnwrkmtl.getRecord();
        	//가져온 최하단 재료 정보의 적치단 위치중 권하 대기인 위치정보를 가져온다.
        	recMinCrnWrkMtl.setField("YD_STK_LYR_MTL_STAT", "D");
        	
System.out.println("====최하단 재료 정보=====");
ydUtils.displayRecord(szOperationName, recMinCrnWrkMtl);


			outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp1");
			intRtnVal = ydStkLyrDao.getYdStklyr(recMinCrnWrkMtl, outRecSet, 3);
    		if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				//권하대기가 없다면 권상 대기위치를 찾는다.(권상 모음 순서가 B인 경우)
        			recMinCrnWrkMtl.setField("YD_STK_LYR_MTL_STAT", "U");
        			outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp1");
        			intRtnVal = ydStkLyrDao.getYdStklyr(recMinCrnWrkMtl, outRecSet, 3);
    			}else if(intRtnVal == -2) {
    				szMsg="parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    		}

        	outRecSet.absolute(1);
        	recReturnData = outRecSet.getRecord();
        	
        	
System.out.println("====최하단 재료 정보=====");
ydUtils.displayRecord(szOperationName, recReturnData);

System.out.println("최하단 재료의 적치단 위치"); 
        	
			//최하단 재료의 적치단 위치
        	szUpWoLoc = recReturnData.getFieldString("YD_STK_COL_GP") + recReturnData.getFieldString("YD_STK_BED_NO"); 
        	szUpWoLocLayer = recReturnData.getFieldString("YD_STK_LYR_NO");
        	//권상지시위치에 등록
        	recCrnSch.setField("YD_UP_WO_LOC", szUpWoLoc);
        	recCrnSch.setField("YD_UP_WO_LAYER", szUpWoLocLayer);
        	
System.out.println("크레인 스케줄의 작업예약 id를 가져온다.");
        	
			//크레인 스케줄의 작업예약 id를 가져온다.
        	szWbookId = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WBOOK_ID");
        	
        	
        	
        	/*쿼리수정_요청
        	//작업예약 ID로 작업예약 재료 TABLE을 권상모음 순서로 정렬해서 조회한다.
			szQuery  = "	SELECT YD_WBOOK_ID   AS YD_WBOOK_ID      ";
			szQuery += "	      ,STL_NO        AS STL_NO           ";
			szQuery += "	      ,YD_STK_COL_GP AS YD_STK_COL_GP    ";
			szQuery += "	      ,YD_STK_BED_NO AS YD_STK_BED_NO    ";
			szQuery += "	      ,YD_STK_LYR_NO AS YD_STK_LYR_NO    ";
			szQuery += "	      ,YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ  ";
			szQuery += "	  FROM TB_YD_WRKBOOKMTL                  ";
			szQuery += "	 WHERE YD_WBOOK_ID = '" + szWbookId +  "'";
			szQuery += "	   AND (DEL_YN <> 'Y' OR DEL_YN IS NULL) ";
			szQuery += "  ORDER BY YD_UP_COLL_SEQ                    ";
			
			
			intRtnVal = ydDBAssist.getData(szQuery, rsWrkbookmtl, null);
			*/
			
			
			YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
			recTempPara = JDTORecordFactory.getInstance().create();	
			recTempPara.setField("YD_WBOOK_ID", szWbookId);
			ydWrkbookMtlDao.getYdWrkbookmtl(recTempPara, rsWrkbookmtl, 34);
			
			
			
			
			//크레인 작업재료의 최하단 재료와 작업예약 재료를 비교해서 같은 것을 찾는다.
			for(int Loop_i = 1; Loop_i <= rsWrkbookmtl.size(); Loop_i++) {
				rsWrkbookmtl.absolute(Loop_i);
				recWrkbookmtl = rsWrkbookmtl.getRecord();
				
				//같은 것을 찾았다면 그 재료의 다음 권상모음순서의 재료를 찾는다.
				if(recMinCrnWrkMtl.getFieldString("STL_NO").equals(recWrkbookmtl.getFieldString("STL_NO") ) ) {
					rsWrkbookmtl.absolute(Loop_i+1);
					recWrkbookmtl = rsWrkbookmtl.getRecord();
					
System.out.println("====다음 권상모음순서의 재료=====");

					break;
				}
			}
ydUtils.displayRecord(szOperationName, recWrkbookmtl);

System.out.println("다음 권상모음 순서의 재료의 적치단 위치를 조회한다. 권하 대기로 있는 것만");
			
			//다음 권상모음 순서의 재료의 적치단 위치를 조회한다. 권하 대기로 있는 것만
			recWrkbookmtl.setField("YD_STK_LYR_MTL_STAT", "D");
			
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp2");
			intRtnVal = ydStkLyrDao.getYdStklyr(recWrkbookmtl, outRecSet, 3);
    		if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				//권하대기가 없다면 권상대기를 찾는다 (다음 권상모음 순서의 재료의 저장위치 결정방법이 바닥인 경우)
    				recWrkbookmtl.setField("YD_STK_LYR_MTL_STAT", "U");
    				outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp2");
    				intRtnVal = ydStkLyrDao.getYdStklyr(recWrkbookmtl, outRecSet, 3);
    			}else if(intRtnVal == -2) {
    				szMsg="parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    		}
    		
        	outRecSet.absolute(1);
        	recReturnData = outRecSet.getRecord();
ydUtils.displayRecord(szOperationName, recReturnData);	
System.out.println("다음 권상모음 순서의 재료의 적치단 위치에서 적치단번호 +1단을 하여 현 크레인스케줄의 권하지시위치에 등록한다.");
        	
			//다음 권상모음 순서의 재료의 적치단 위치에서 적치단번호 +1단을 하여 현 크레인스케줄의 권하지시위치에 등록한다.
        	szDnWoLoc      = recReturnData.getFieldString("YD_STK_COL_GP") + recReturnData.getFieldString("YD_STK_BED_NO"); 
        	szDnWoLocLayer = ydDaoUtils.stringPlusInt(recReturnData.getFieldString("YD_STK_LYR_NO"), 1);

System.out.println("szDnWoLoc : " + szDnWoLoc);
System.out.println("szDnWoLocLayer : " + szDnWoLocLayer);

        	//권하지시위치에 등록
        	recCrnSch.setField("YD_DN_WO_LOC", szDnWoLoc);
        	recCrnSch.setField("YD_DN_WO_LAYER", szDnWoLocLayer);
        	recCrnSch.setField("REG_DDTT", null);
System.out.println("====업데이트할 크레인 스케줄 정보=====");
ydUtils.displayRecord(szOperationName, recCrnSch);
        	
			ydCrnschDao.updYdCrnsch(recCrnSch, 0);
    		if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    			}else if(intRtnVal == -1) {
    				szMsg="duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			return intRtnVal = -1;
    		}
			
        }catch(Exception e){
			System.out.println("<getLocSrcRng> Exception Error :"+ e.getLocalizedMessage());
			return intRtnVal = -1;
        }//end of try~catch
        
        return intRtnVal = 1;
    	
    }//end of getMainWrkLoc()
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 위치검색범위및위치검색Bed조회
     *  
     * @param  rsResultCrnwrkmtl, rsBed
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int getLocSrchRng (JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch){
    	//상위 Method : DnLocInsMain
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
		//┗━┛
	
    	YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
    	YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
    	YdDBAssist   ydDBAssist = new YdDBAssist();
    	
    	JDTORecordSet rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
    	String szQuery = "";
    	
    	//스케줄코드
    	String szSchCd    = "";
    	//야드행선구분
    	String szRouteGp  = "";
    	//저장집합코드
    	String szStrGtrCd = "";
    	
    	//권상지시위치정보
    	String szDnWoLoc = "";
    	String szDnWoLocLayer = "";
    	
    	JDTORecord recResultCrnwrkmtl = null;
    	JDTORecord recReturnData      = null;
    	JDTORecordSet outRecSet       = null;
    	
    	int intRtnVal = 0;
    	String szMsg        = "";
    	String szMethodName = "getLocSrchRng";
    	String szOperationName = "위치검색범위및위치검색Bed조회";
    	
    	JDTORecord recTempPara = null;
    	
        try{
        	//크레인 작업재료의 최하단 재료정보를 가져온다.
        	rsCrnwrkmtl.absolute(1);
        	recResultCrnwrkmtl = rsCrnwrkmtl.getRecord();
        	//가져온 최하단 재료 정보의 적치단 위치중 권하 대기인 위치정보를 가져온다.
        	recResultCrnwrkmtl.setField("YD_STK_LYR_MTL_STAT", "D");
        	
System.out.println("====최하단 재료 정보=====");
ydUtils.displayRecord(szOperationName, recResultCrnwrkmtl);


			outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp1");
			intRtnVal = ydStkLyrDao.getYdStklyr(recResultCrnwrkmtl, outRecSet, 3);
    		if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				//권하대기가 없다면 권상 대기위치를 찾는다.(권상 모음 순서가 B인 경우)
    				recResultCrnwrkmtl.setField("YD_STK_LYR_MTL_STAT", "U");
        			outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp1");
        			intRtnVal = ydStkLyrDao.getYdStklyr(recResultCrnwrkmtl, outRecSet, 3);
    			}else if(intRtnVal == -2) {
    				szMsg="parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    		}

        	outRecSet.absolute(1);
        	recReturnData = outRecSet.getRecord();
        	
        	
System.out.println("====최하단 재료 정보=====");
ydUtils.displayRecord(szOperationName, recReturnData);
System.out.println("최하단 재료의 적치단 위치"); 
     
			//최하단 재료의 적치단 위치
        	szDnWoLoc = recReturnData.getFieldString("YD_STK_COL_GP") + recReturnData.getFieldString("YD_STK_BED_NO"); 
        	szDnWoLocLayer = recReturnData.getFieldString("YD_STK_LYR_NO");
        	
        	//권상지시위치에 등록
        	if(recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD").equals("T") ) {
	        	recCrnSch.setField("YD_UP_WO_LOC", szDnWoLoc);
	        	recCrnSch.setField("YD_UP_WO_LAYER", szDnWoLocLayer);
	        	
        	
	        	recCrnSch.setField("REG_DDTT", null);
	        	
	        	ydCrnSchDao.updYdCrnsch(recCrnSch, 0);
	    		if(intRtnVal <= 0) {
	    			if(intRtnVal == 0) {
	    				szMsg="data not found";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	    			}else if(intRtnVal == -1) {
	    				szMsg="duplicate data,";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
	    			}else if(intRtnVal == -2) {
	    				szMsg="parameter error";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -3){
	    				szMsg="execution failed";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
	    			return intRtnVal = -1;
	    		}
        	}
        	
        	
        	
        	recResultCrnwrkmtl.setField("YD_SCH_CD", recCrnSch.getFieldString("YD_SCH_CD"));
        	
    		szSchCd    = recCrnSch.getFieldString("YD_SCH_CD");
    		szRouteGp  = recResultCrnwrkmtl.getFieldString("YD_AIM_RT_GP");
        		
System.out.println("************위치검색범위*************");
    		ydUtils.displayRecord(szOperationName, recResultCrnwrkmtl);
    		
    		//recGetData의 값으로 저장위치 Check
    		//
    		
    		
    		// DB어시스트
    		// 
        	/* 쿼리수정_요청	
    		szQuery = "		SELECT Y.YD_STK_COL_GP        AS YD_STK_COL_GP                                  ";
    		szQuery+= "		      ,Y.YD_STK_BED_NO        AS YD_STK_BED_NO                                  ";
    		szQuery+= "		      ,Y.YD_STK_BED_WHIO_STAT AS YD_STK_BED_WHIO_STAT                           ";
    		szQuery+= "		      ,Y.YD_STK_BED_ACT_STAT  AS YD_STK_BED_ACT_STAT                            ";
    		szQuery+= "		  FROM (SELECT A.YD_SCH_CD                AS YD_SCH_CD                          ";
    		szQuery+= "		              ,A.YD_ROUTE_GP              AS YD_ROUTE_GP                        ";
    		szQuery+= "		              ,A.YD_LOC_SRCH_RNG_REG_SNO  AS YD_LOC_SRCH_RNG_REG_SNO            ";
    		szQuery+= "		              ,A.YD_STR_GTR_CD            AS YD_STR_GTR_CD                      ";
    		szQuery+= "		              ,A.YD_LOC_SRCH_RNG_SEQ      AS YD_LOC_SRCH_RNG_SEQ                ";
    		szQuery+= "		              ,A.YD_LOC_SRCH_RNG_ACT_STAT AS YD_LOC_SRCH_RNG_ACT_STAT           ";
    		szQuery+= "		              ,A.YD_STK_BED_SRCH_METHOD   AS YD_STK_BED_SRCH_METHOD             ";
    		szQuery+= "		              ,B.YD_LOC_SRCH_BED_REG_SNO  AS YD_LOC_SRCH_BED_REG_SNO            ";
    		szQuery+= "		              ,B.YD_STK_BED_SRCH_SEQ      AS YD_STK_BED_SRCH_SEQ                ";
    		szQuery+= "		              ,B.YD_STK_COL_GP            AS YD_STK_COL_GP                      ";
    		szQuery+= "		              ,B.YD_STK_BED_NO            AS YD_STK_BED_NO                      ";
    		szQuery+= "		          FROM TB_YD_LOCSRCHRNG A                                               ";
    		szQuery+= "		              ,TB_YD_LOCSRCHBED B                                               ";
    		szQuery+= "		         WHERE A.YD_SCH_CD               = B.YD_SCH_CD                          ";
    		szQuery+= "		           AND A.YD_ROUTE_GP             = B.YD_ROUTE_GP                        ";
    		szQuery+= "		           AND A.YD_LOC_SRCH_RNG_REG_SNO = B.YD_LOC_SRCH_RNG_REG_SNO            ";
    		szQuery+= "		           AND A.YD_SCH_CD               = '" + szSchCd    + "'";
    		szQuery+= "		           AND A.YD_ROUTE_GP             = '" + szRouteGp  + "'";
    		szQuery+= "		           AND (A.DEL_YN IS NULL OR A.DEL_YN <> 'Y')                            ";
    		szQuery+= "		           AND (B.DEL_YN IS NULL OR B.DEL_YN <> 'Y')                            ";
    		szQuery+= "		         ORDER BY A.YD_LOC_SRCH_RNG_SEQ, B.YD_STK_BED_SRCH_SEQ) X               ";
    		szQuery+= "		      ,TB_YD_STKBED Y                                                           ";
    		szQuery+= "		 WHERE X.YD_STK_COL_GP = Y.YD_STK_COL_GP                                        ";
    		szQuery+= "		   AND X.YD_STK_BED_NO = Y.YD_STK_BED_NO                                        ";
    		szQuery+= "		   AND Y.YD_STK_BED_WHIO_STAT = 'E'                                             ";
    		szQuery+= "		   AND Y.YD_STK_BED_ACT_STAT = 'L'                                              ";
    		szQuery+= "		   AND (DEL_YN IS NULL OR DEL_YN <> 'Y')                                        ";
    	    rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
        	intRtnVal = ydDBAssist.getData(szQuery, rsBed, null) ;
        	
        	*/
    		
        	YdLocSrchRngDao ydLocSrchRngDao = new YdLocSrchRngDao();
        
        	
    		recTempPara = JDTORecordFactory.getInstance().create();	
  		    recTempPara.setField("YD_SCH_CD", szSchCd);
  		    recTempPara.setField("YD_ROUTE_GP", szRouteGp);
  		    
  		    
    	
  			ydLocSrchRngDao.getYdLocsrchrng(recTempPara, rsBed, 6);

    		
			//3. 위치검색Bed와 크레인작업재료 비교 Check
  			
			intRtnVal = this.chkLocsrchbedCrnMtl(rsBed, recResultCrnwrkmtl) ;
			if( intRtnVal == -1 ) return intRtnVal = -1;

        }catch(Exception e){
			System.out.println("<getLocSrcRng> Exception Error :"+ e.getLocalizedMessage());
			return intRtnVal = -1;
        }//end of try~catch
        
        
        return intRtnVal = 1;
    	
    }//end of getLocSrcRng()
    
    
    
    
	
	
	
    /**
     * 오퍼레이션명 : 적치베드 Check Update
     *  
     * @param rsBed, rsCrnWrkMtl, recCrnWrkMtl, recCrnWrkMtl
     * @return int 1, -1
     * @throws 
     */
    public int chkLocsrchbedCrnMtl (JDTORecordSet rsBed, JDTORecord recGetCrnWrkMtl){
    	//상위 Method : DnLocInsMain
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
		//┗━┛
    	
    	YdStkBedDao ydStkBedDao = new YdStkBedDao();
    	YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao(); 
    	YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
    	YdDBAssist   ydDBAssist = new YdDBAssist();
    	
    	
    	//적치Bed를 조회한 정보
    	JDTORecordSet rsGetStkBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	//적치단을 조회한 정보
    	JDTORecord recStkLyr = null;
    	JDTORecordSet rsGetStkLyr = null;
    	//크레인작업재료 정보
    	JDTORecordSet rsResultCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
    	//파라미터 rsBed를 조회
    	JDTORecord recStkBed      = null;
    	//적치Bed를 조회한 정보
    	JDTORecord recGetRsSet    = null;
    
    	
    	
    	int intRtnVal 		= 0 ;
        String szMsg        = "";
        String szMethodName = "craneLdHd";
        String szOperationName = "적치베드 Check Update";
        //적치베드의 Max단,중량,높이
        int intStkBedLyrMax        = 0;
        long lngStkBedWtMax        = 0;
        long lngStkBedHMax         = 0;
        
        //크레인작업재료의 총매수,중량,높이
        int intCrnWrkMtlSh         = 0;
        long lngCrnWrkMtlWt        = 0;
        long lngCrnWrkMtlT         = 0;
        
        //적치단의 적치중인 재료의 총매수,중량,높이
        int intStkLyrMax           = 0;
        long lngStkLyrWtMax 	   = 0;
        long lngStkLyrHMax         = 0;
        
        //크레인작업재료의 최하단 산적LotType,산적Lot코드,폭
        String szCrnWrkMtlStkLotTp = "";
        String szCrnWrkMtlStkLotCd = "";
        long lngCrnWrkMtlW		   = 0;
        
        //적치단의 최상단 산적LotType,산적Lot코드,폭
        String szStkLyrStkLotTp    = "";
        String szStkLyrStkLotCd    = "";
        long lngStkLyrW		       = 0;
        
        String szStkColGp          = "";
        String szStkBedNo          = ""; 
        
        //등급
        int intGrade               = 0;
        //이전등급중 가장 좋은 등급
        int intGradeSave           = 100;
        //등급의 적치단정보
        String szGradeColGp          = "";
        String szGradeBedNo          = "";
        String szGradeLyrNo          = "";
        
        String szQuery = "";
        String szCrnSchId = "";
        
        JDTORecord recTempPara      = null;
        
        
        try{
//        	intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recGetCrnWrkMtl, rsResultCrnwrkmtl, 1);
//    		if(intRtnVal <= 0) {
//    			if(intRtnVal == 0) {
//    				szMsg="getYdCrnwrkmtl data not found";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//    			}else if(intRtnVal == -2) {
//    				szMsg="getYdCrnwrkmtl parameter error";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			}
//    		}
        	
        	szCrnSchId = recGetCrnWrkMtl.getFieldString("YD_CRN_SCH_ID");
        	
        	
        	
        	/* 쿼리수정_요청 
        	szQuery  = "	SELECT YD_CRN_SCH_ID  AS YD_CRN_SCH_ID       ";
        	szQuery += "	      ,STL_NO  AS STL_NO                     ";
        	szQuery += "	      ,REGISTER  AS REGISTER                 ";
        	szQuery += "	      ,REG_DDTT  AS REG_DDTT                 ";
        	szQuery += "	      ,MOD_DDTT  AS MOD_DDTT                 ";
        	szQuery += "	      ,MODIFIER  AS MODIFIER                 ";
        	szQuery += "	      ,DEL_YN  AS DEL_YN                     ";
        	szQuery += "	      ,YD_AID_WRK_YN  AS YD_AID_WRK_YN       ";
        	szQuery += "	      ,YD_STK_LYR_NO  AS YD_STK_LYR_NO       ";
        	szQuery += "	      ,YD_STK_LOT_TP  AS YD_STK_LOT_TP       ";
        	szQuery += "	      ,YD_STK_LOT_CD  AS YD_STK_LOT_CD       ";
        	szQuery += "	      ,HCR_GP  AS HCR_GP                     ";
        	szQuery += "	      ,STL_PROG_CD  AS STL_PROG_CD           ";
        	szQuery += "	      ,YD_MTL_ITEM  AS YD_MTL_ITEM           ";
        	szQuery += "	      ,YD_ROUTE_GP  AS YD_ROUTE_GP           ";
        	szQuery += "	  FROM TB_YD_CRNWRKMTL                       ";
        	szQuery += "	 WHERE YD_CRN_SCH_ID = '" + szCrnSchId + "'";
        	szQuery += "	   AND (DEL_YN <> 'Y' OR DEL_YN IS NULL)     ";
        	szQuery += "	 ORDER BY YD_STK_LYR_NO                      ";
        	intRtnVal = ydDBAssist.getData(szQuery, rsResultCrnwrkmtl, null) ;
        	
*/
        	
        	YdLocSrchRngDao ydLocSrchRngDao = new YdLocSrchRngDao();
        	
    		recTempPara = JDTORecordFactory.getInstance().create();	
  		    recTempPara.setField("YD_CRN_SCH_ID", szCrnSchId);  		    
  		     ydCrnWrkMtlDao.getYdCrnwrkmtl(recTempPara, rsResultCrnwrkmtl, 13);
  			
  			
  			
        	
        	 
        	
        	rsBed.beforeFirst();
        	
        	//적치베드단위로 Loop
        	for(int Loop_i = 1; Loop_i <= rsBed.size(); Loop_i++) {
        		rsBed.absolute(Loop_i);
        		recStkBed = rsBed.getRecord();
        		//열구분과 베드번호를 읽어온다.
        		szStkColGp = recStkBed.getFieldString("YD_STK_COL_GP");
        		szStkBedNo = recStkBed.getFieldString("YD_STK_BED_NO");
        		
        		//적치Bed조회한다.
        		rsGetStkBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
        		intRtnVal = ydStkBedDao.getYdStkbed(recStkBed, rsGetStkBed, 0) ;
        		if(intRtnVal <= 0) {
        			if(intRtnVal == 0) {
        				szMsg="data not found";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
        			}else if(intRtnVal == -2) {
        				szMsg="parameter error";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        			}
        			System.out.println("적치베드가 조회되지않았습니다. 다음 베드를 조회합니다. ");
        			//다음for문
        			continue;
        		}
        		
System.out.println("적치베드의 맥스값가져오기전 ..... : "+rsGetStkBed.size());
        		//적치베드의 Max단,중량,높이
        		rsGetStkBed.first();
        		recGetRsSet = rsGetStkBed.getRecord();
        		ydUtils.displayRecord(szOperationName, recGetRsSet);
        		
        		intStkBedLyrMax = ydDaoUtils.paraRecChkNullInt (recGetRsSet,"YD_STK_BED_LYR_MAX");
        		lngStkBedWtMax  = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"YD_STK_BED_WT_MAX");       
        		lngStkBedHMax   = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"YD_STK_BED_H_MAX"); 
System.out.println("맥스단 : " + intStkBedLyrMax);
System.out.println("맥스중량 : " + lngStkBedWtMax);
System.out.println("맥스높이 : " + lngStkBedHMax);
        		
System.out.println("적치베드의 맥스값 가져오고  크레인 작업재료 총매수 중량 높이 가져오기전..... : ");
        		
        		//크레인작업재료의 총매수 총중량 총높이를 가져온다. 		
        		intCrnWrkMtlSh      = ydDaoUtils.paraRecChkNullInt (recGetCrnWrkMtl,"SH_CNT");
        		lngCrnWrkMtlWt      = ydDaoUtils.paraRecChkNullLong(recGetCrnWrkMtl,"SUM_MTL_WT");
        		lngCrnWrkMtlT       = ydDaoUtils.paraRecChkNullLong(recGetCrnWrkMtl,"SUM_MTL_T");
        		
System.out.println("크레인 작업재료 최하단 재료정보 가져오기전... : ");
        		//크레인작업재료의 최하단 재료정보
        		lngCrnWrkMtlW       = ydDaoUtils.paraRecChkNullLong(recGetCrnWrkMtl,"SUM_MTL_W");
        		szCrnWrkMtlStkLotTp = ydDaoUtils.paraRecChkNull    (recGetCrnWrkMtl,"YD_STK_LOT_TP");
        		szCrnWrkMtlStkLotCd = ydDaoUtils.paraRecChkNull    (recGetCrnWrkMtl,"YD_STK_LOT_CD");
        		System.out.println("크레인작업재료 정보 : " + intRtnVal);
        		ydUtils.displayRecord(szOperationName, recGetCrnWrkMtl);
        		
System.out.println("적치단 저장품 열구분 :  szStkColGp :" + szStkColGp );
System.out.println("적치단 저장품 베드번호 :  szStkBedNo :" + szStkBedNo );
        		
				//적치Bed의 적치중이거나 권하대기상태인 재료정보를 가져온다.
				//적치단의 재료의 합계정보
				recStkLyr = JDTORecordFactory.getInstance().create();
				recStkLyr.setField("YD_STK_COL_GP", szStkColGp);
				recStkLyr.setField("YD_STK_BED_NO", szStkBedNo);
				
				rsGetStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
				intRtnVal = ydStkLyrDao.getYdStklyr(recStkLyr, rsGetStkLyr, 6);
				if(intRtnVal <= 0) {
					if(intRtnVal == 0) {
						szMsg="data not found";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
					}else if(intRtnVal == -2) {
						szMsg="parameter error";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					System.out.println("적치단이 조회가 되지 않았습니다");
				}
				if(intRtnVal == 0) {
					System.out.println("공베드입니다.");
				}
System.out.println("적치베드의 총 갯수*************" + rsGetStkLyr.size());
				
				
				if(rsGetStkLyr.size() > 0) {
					rsGetStkLyr.absolute(1);
	        		recGetRsSet = rsGetStkLyr.getRecord();
	        		ydUtils.displayRecord(szOperationName, recGetRsSet);
	        		//적치단에적치중인 총매수 중량 높이
	        		intStkLyrMax     = ydDaoUtils.paraRecChkNullInt (recGetRsSet,"SH_CNT");
	       			lngStkLyrWtMax   = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"SUM_MTL_WT");
	       			lngStkLyrHMax    = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"SUM_MTL_T");
	       			//적치단의최상단  재료정보
	       			lngStkLyrW       = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"SUM_MTL_W");
	       			szStkLyrStkLotTp = ydDaoUtils.paraRecChkNull    (recGetRsSet,"YD_STK_LOT_TP");
	       			szStkLyrStkLotCd = ydDaoUtils.paraRecChkNull    (recGetRsSet,"YD_STK_LOT_CD");
        		}else{
        			intStkLyrMax = 0;
        			lngStkLyrWtMax = 0;
        			lngStkLyrHMax = 0;
        			lngStkLyrW = 0;
        			szStkLyrStkLotTp = "";
        			szStkLyrStkLotCd = "";
        		}
       			
       			System.out.println(intStkBedLyrMax + " < " +  intCrnWrkMtlSh + " + " + intStkLyrMax);
       			System.out.println(lngStkBedWtMax + " < " +  lngCrnWrkMtlWt + " + " + lngStkLyrWtMax);
       			System.out.println(lngStkBedHMax + " < " +  lngCrnWrkMtlT + " + " + lngStkLyrHMax);
       			
       			if(intStkBedLyrMax < intCrnWrkMtlSh + intStkLyrMax)   continue;
       			if(lngStkBedWtMax  < lngCrnWrkMtlWt + lngStkLyrWtMax) continue;
       			if(lngStkBedHMax   < lngCrnWrkMtlT  + lngStkLyrHMax)  continue;
       			
       			intGrade = this.gradeTest(recGetCrnWrkMtl, recGetRsSet);
System.out.println("intGrade : " + intGrade);
       			
       			//최상의 등급인 경우 종료
       			if (intGrade == 1) {
       				//최상의자리를 찾았다는 메시지
					System.out.println("1등급 위치를 찾았습니다.");
       				//To위치 저장 후 리턴
       				
					//적치단 등록               크레인 작접재료                적치단 정보       등급
					intRtnVal = this.updGradStkLyr(rsResultCrnwrkmtl, recGetRsSet);	
					if(intGrade == -1) {
	       				System.out.println("최상급 적치단 등록  중 Error");
	       				continue;
					}
       				return intRtnVal = 1;
       			}else if(intGrade == -1) {
       				System.out.println("위치검색 중 Error");
       				continue;
       			}
       			
       		
       			if(Loop_i == 1) {
       				intGradeSave = intGrade ;
       				szGradeColGp = ydDaoUtils.paraRecChkNull(recStkLyr,"YD_STK_COL_GP");
       				szGradeBedNo = ydDaoUtils.paraRecChkNull(recStkLyr,"YD_STK_BED_NO");
       				szGradeLyrNo = ydDaoUtils.paraRecChkNull(recStkLyr,"YD_STK_LYR_NO");       				
       			}
       			
       			//저장등급보다 지금 등급이 좋다면 저장
       			if (intGradeSave > intGrade) {
       				intGradeSave = intGrade ;
       				szGradeColGp = recStkLyr.getFieldString("YD_STK_COL_GP");
       				szGradeBedNo = recStkLyr.getFieldString("YD_STK_BED_NO");
       				szGradeLyrNo = recStkLyr.getFieldString("YD_STK_LYR_NO");
       			}
       			
        	}//end of for
        	
        	//적치단정보 Setting
        	recGetRsSet = JDTORecordFactory.getInstance().create();
        	recGetRsSet.setField("YD_STK_COL_GP", szGradeColGp);
        	recGetRsSet.setField("YD_STK_BED_NO", szGradeBedNo);
        	recGetRsSet.setField("YD_STK_LYR_NO", szGradeLyrNo);
        	
        	//적치단 등록               크레인 작접재료                적치단 정보       등급
        	System.out.println("=========최종위치 적치Bed정보==========");
        	ydUtils.displayRecord(szOperationName, recGetRsSet);
        	
			intRtnVal = this.updGradStkLyr(rsResultCrnwrkmtl, recGetRsSet);	
			if(intGrade == -1) {
   				System.out.println("저장등급 적치단 등록  중 Error");
			}
			
        }catch(Exception e){
			System.out.println("<chkLocsrchbedCrnMtl> Exception Error :"+ e.getLocalizedMessage());
			return intRtnVal = -1;
        }//end of try~catch
        
        
        return intRtnVal = 1;
    	
    }//end of chkLocsrchbedCrnMtl()
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 위치검색범위및위치검색Bed조회
     *  
     * @param  rsResultCrnwrkmtl, rsBed
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int gradeTest (JDTORecord recGetCrnWrkMtl, JDTORecord recGetStkLyr){
    	//상위 Method : chkLocsrchbedCrnMtl
		//┏━┓
		//┃ 저장위치의 등급을 구한다.
		//┗━┛
    	
    	int intRtnVal = 0;
    	
        //크레인작업재료의 최하단 산적LotType,산적Lot코드,폭
        String szCrnWrkMtlStkLotTp = "";
        String szCrnWrkMtlStkLotCd = "";
        
        //적치단의 최상단 산적LotType,산적Lot코드,폭
        String szStkLyrStkLotTp    = "";
        String szStkLyrStkLotCd    = "";
        
    	
        try{
        	szCrnWrkMtlStkLotTp = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl,"YD_STK_LOT_TP");
        	szCrnWrkMtlStkLotCd = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl,"YD_STK_LOT_CD");
        	szStkLyrStkLotTp    = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_LOT_TP");
        	szStkLyrStkLotCd    = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_LOT_CD");
        	;
        	//To위치가 공베드 = 3등급
        	if(szStkLyrStkLotTp.equals("")) {
        		System.out.println("공 베드입니다...");
        		return intRtnVal = 3;
        	}
        	
        	//산적LotType가 같은 경우
        	if(szStkLyrStkLotTp == szCrnWrkMtlStkLotTp) {
        		
        		//산적LotType와 Code가 같은경우 1등급
        		if(szStkLyrStkLotCd == szCrnWrkMtlStkLotCd) {
        			intRtnVal = 1;
            		System.out.println("intGrade : " + intRtnVal);
        			return intRtnVal;
        		
        		//코드가 틀린 경우 2등급
        		}else{
        			intRtnVal = 2;
            		System.out.println("intGrade : " + intRtnVal);
        			return intRtnVal;
        		}		
        	
        	//산적Lot Type가 틀린경우
        	}else{
        		intRtnVal = 4;
        		System.out.println("intGrade : " + intRtnVal);
        		return intRtnVal;
        		
        	}

    
        }catch(Exception e){
			System.out.println("<gradeTest> Exception Error :"+ e.getLocalizedMessage());
			return intRtnVal = -1;
        }//end of try~catch
    	
    }//end of gradeTest()
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 등록
     *  
     * @param  rsResultCrnwrkmtl, recGetRsSet, intGrade
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int updGradStkLyr (JDTORecordSet rsResultCrnwrkmtl, JDTORecord recGetRsSet){
    	//상위 Method : chkLocsrchbedCrnMtl
		//┏━┓
		//┃ 저장위치의 등급에 따라 적치단에 재료를 등록한다.
		//┗━┛
    	
    	int intRtnVal = 0;
    	YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
    	
    	
    	JDTORecord recUpdStkLyrData = null;
    	JDTORecord recSetStkLyrData = null;
    	JDTORecord recUpdCrnSchData = null;
    	JDTORecordSet rsGetStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");

        
        String szMsg               = "";
        String szMethodName        = "updGradStkLyr";
        String szOperationName = "적치단 등록";
        String szQuery = "";
    	
        try{
        	
        	//크레인 작업재료의 수만큼  for문 반복
        	for(int Loop_i = 1; Loop_i <= rsResultCrnwrkmtl.size(); Loop_i++) {
        		
        		
        		rsResultCrnwrkmtl.absolute(Loop_i);
        		recSetStkLyrData = rsResultCrnwrkmtl.getRecord();
System.out.println("===========크레인 작업재료===========");
ydUtils.displayRecord(szOperationName, recSetStkLyrData);

        		
				recUpdStkLyrData = JDTORecordFactory.getInstance().create() ;
				recUpdStkLyrData.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_COL_GP"));
				recUpdStkLyrData.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_BED_NO"));
   				//저장할 재료
   				recUpdStkLyrData.setField("STL_NO",        ydDaoUtils.paraRecChkNull(recSetStkLyrData, "STL_NO")); 
   				//권하대기상태로 적치단재료활성상태 변경
   				recUpdStkLyrData.setField("YD_STK_LYR_MTL_STAT", "D");
   				//공베드 인 경우
   				if ( ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_LYR_NO").equals("")) {
   					recUpdStkLyrData.setField("YD_STK_LYR_NO",  ydDaoUtils.stringPlusInt("000",Loop_i) );
   				}else{
   				//공베드가 아닌 경우
   					recUpdStkLyrData.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_LYR_NO"), Loop_i));
   				}
System.out.println("===========업데이트할 정보===========");
ydUtils.displayRecord(szOperationName, recUpdStkLyrData);

       				//적치단에 Update
   				
   				intRtnVal = ydStkLyrDao.updYdStklyr(recUpdStkLyrData, 0);
   				System.out.println("intRtnVal : " + intRtnVal );
   				if(intRtnVal <= 0) {
        			if(intRtnVal == 0) {
        				szMsg="data not found";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
        			}else if(intRtnVal == -2) {
        				szMsg="parameter error";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        			}else{
        				szMsg="execution failed";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        			}
        			return intRtnVal = -1;
   				}
   				
   				if(Loop_i == 1 ) {
   					
System.out.println("===========YD_CRN_SCH_ID===========" + recSetStkLyrData.getFieldString("YD_CRN_SCH_ID"));
System.out.println("===========YD_DN_WO_LOC===========" + recUpdStkLyrData.getFieldString("YD_STK_COL_GP") + recUpdStkLyrData.getFieldString("YD_STK_BED_NO"));
System.out.println("===========YD_DN_WO_LAYER===========" + recUpdStkLyrData.getFieldString("YD_STK_LYR_NO"));

   					//크레인 스케줄  권하지시위치 업데이트
					recUpdCrnSchData = JDTORecordFactory.getInstance().create();
   					recUpdCrnSchData.setField("YD_CRN_SCH_ID",  recSetStkLyrData.getFieldString("YD_CRN_SCH_ID") ) ;
   					recUpdCrnSchData.setField("REG_DDTT", null) ;
   					recUpdCrnSchData.setField("YD_DN_WO_LOC",   recUpdStkLyrData.getFieldString("YD_STK_COL_GP") + recUpdStkLyrData.getFieldString("YD_STK_BED_NO")) ;
   					recUpdCrnSchData.setField("YD_DN_WO_LAYER", recUpdStkLyrData.getFieldString("YD_STK_LYR_NO") ) ;
   					intRtnVal = this.updYdCrnsch(recUpdCrnSchData, 0);
   					if(intRtnVal == -1) {
        				szMsg="크레인 스케줄 권하지시위치 등록 실패!";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
   					}
   				}
   				
        	}//END OF FOR

    
        }catch(Exception e){
			System.out.println("<updGradStkLyr> Exception Error :"+ e.getLocalizedMessage());
			return intRtnVal = -1;
        }//end of try~catch
    	return intRtnVal = 1;
    }//end of updGradStkLyr()
    //
    
    
    

    
//=================================================================================================
//	김진욱 END
//=================================================================================================	   
	
	
//=================================================================================================
//	연은정 BEGIN
//=================================================================================================	
    /**
	 * 연주전단지시 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void CcFsWo(JDTORecord inRecord)throws JDTOException  {

		String szMethodName = "CcFsWo";
		String szMsg = "";


		int intRtnVal = 0;
		int i = 0;

		int rowsize = 0;

		JDTORecord recPlntProcCd = JDTORecordFactory.getInstance().create();
		JDTORecord recFromTo = JDTORecordFactory.getInstance().create();
		JDTORecord recPlanHeatNo = JDTORecordFactory.getInstance().create();
		JDTORecord recMslabWo = JDTORecordFactory.getInstance().create();
		JDTORecord recMslabWoItm = JDTORecordFactory.getInstance().create();
		JDTORecord recEditRec = JDTORecordFactory.getInstance().create();

		JDTORecordSet rsGetStockFromTo = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsGetStockTpsWoHeat = JDTORecordFactory.getInstance().createRecordSet("");


		YdStockDao ydStockDao = new YdStockDao();

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}

		try{
			//공장공정코드 =CZ (C열연 통합)
			recPlntProcCd.setField("PLNT_PROC_CD", "CZ");

			//출강지시Index Table에서 FromPoint와 ToPoint를 구하는 Dao 호출.
			intRtnVal = ydStockDao.getYdStock(recPlntProcCd, rsGetStockFromTo, 10);
			//From-ToPoint가 존재 하지 않을경우 
			if (intRtnVal <= 0){
				szMsg="FromPoint와 ToPoint가 존재하지 않습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			szMsg="PLNT_PROC_CD" + recPlntProcCd.getFieldString("PLNT_PROC_CD");
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			//구해진 FromPoint와 ToPoint를 JDTORecord recFromTo에  담기 
			rsGetStockFromTo.first();
			recFromTo = rsGetStockFromTo.getRecord();
			//recFromTo.setField("CHG_WO_FR_PNT", "11");
			//recFromTo.setField("CHG_WO_TO_PNT", "14");

			//구한 FromPoint부터 ToPoint까지 범위의 출강지시Heat Table의 예정Heat번호를 조건으로 주편작업지시 Table항목 읽어오기.
			intRtnVal = ydStockDao.getYdStock(recFromTo, rsGetStockTpsWoHeat, 12);

			if (intRtnVal <= 0 ){
				szMsg="Error :no data!  ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			rowsize = rsGetStockTpsWoHeat.size();
			szMsg="size : " +rowsize;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			//레코드의 커서를 처음으로
			rsGetStockTpsWoHeat.first();

			for(i =0; i < rowsize; i++){


				recMslabWoItm = rsGetStockTpsWoHeat.getRecord();

				//주편작업지시 Table에서 받은 항목을 야드 저장품Table항목으로 편집하기
				intRtnVal = edtMslabWo(recMslabWoItm,recEditRec);
				if( intRtnVal == 1 ){
					szMsg = "주편작업지시Table의 항목을 저장품 항목으로  편집 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				}else {
					szMsg = "주편작업지시Table의 항목을 저장품 항목으로  편집 실패 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					//return;
					continue;
				}

				intRtnVal = getInsUpdYdStock(recEditRec);

				//getInsUpdYdStock()의 리턴 값에 대한 Message
				switch (intRtnVal){

				case 1 :

					szMsg = "편집한 항목을 저장품 Table에 등록 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					break;

				case -1 :

					szMsg = "STL_NO로 저장품 Table 조회  실패 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					break;

				case -2 :

					szMsg = "Error : 데이타  Update 실패 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					break;

				case -3 :

					szMsg = "Error : 데이타 Insert 실패 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					break;
				} // end of switch
				rsGetStockTpsWoHeat.next();

			} // end of for
		}catch(Exception e){

			szMsg="Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}


		szMsg="연주전단지시  등록("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} //end of CcFsWo();

	
	
	
	/**
	 *주편작업지시(TB_CT_F_MSLABWO)의 항목을 야드 저장품 Table 항목으로 편집
	 * return  1: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int edtMslabWo(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {


		String szMethodName	=	"edtMslabWo";
		String szMsg="";

		try{

			recEditRec.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"PLN_MSLAB_NO")); 			// 재료번호	- 예정주편번호
			recEditRec.setField("REGISTER", 			ydDaoUtils.paraRecChkNull(inRecord,"REGISTER"));				// 등록자  	- 등록자	
			recEditRec.setField("REG_DDTT", 			ydDaoUtils.paraRecChkNull(inRecord,"REG_DDTT"));				// 등록일시
			//recEditRec.setField("MODIFIER", 			ydDaoUtils.paraRecChkNull(inRecord,"MODIFIER"));				// 수정자
			//recEditRec.setField("MOD_DDTT", 			ydDaoUtils.paraRecChkNull(inRecord,"MOD_DDTT"));				// 수정일시
			//recEditRec.setField("YD_STK_LOT_CD", 		ydDaoUtils.paraRecChkNull(inRecord,"STACK_LOT_NO"));			// 야드 산적LOT코드 - 슬라브산적LOT코드	
			recEditRec.setField("YD_MTL_W", 			ydDaoUtils.paraRecChkNull(inRecord,"CT_WO_MSLAB_WO_W"));		// 폭
			recEditRec.setField("YD_MTL_L", 			ydDaoUtils.paraRecChkNull(inRecord,"CT_WO_MSLAB_WO_L"));		// 길이
			recEditRec.setField("YD_MTL_T", 			ydDaoUtils.paraRecChkNull(inRecord,"CT_WO_MSLAB_WO_T"));		// 두께
			recEditRec.setField("YD_MTL_WT", 			ydDaoUtils.paraRecChkNull(inRecord,"CT_WO_MSLAB_WO_WT"));		// 중량
			recEditRec.setField("ORD_YEOJAE_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));			// 주문여재구분
			recEditRec.setField("ORD_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"REPRESENT_ORD_NO"));		// 주문번호
			recEditRec.setField("ORD_DTL", 				ydDaoUtils.paraRecChkNull(inRecord,"REPRESENT_ORD_DTL"));		// 주문행번
			recEditRec.setField("ORD_HCR_GP", 			ydDaoUtils.paraRecChkNull(inRecord,"QT_DS_HCR_GP"));			// 설계HCR구분	
			recEditRec.setField("HCR_GP", 				ydDaoUtils.paraRecChkNull(inRecord,"CT_HCR_GP"));				// HCR구분
			recEditRec.setField("FRTOMOVE_PLANT_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"SLMK_PLNT_GP"));			// 이송공장구분

		} catch(Exception e){

			return -1;
		}
		return 1;

	} //end of edtMslabWo()
	
	/**
	 *  해당 재료번호(STL_NO)로 저장품 table 조회 후
	 *  데이타가 존재할 경우 (update)
	 *  데이타가 존재하지 않을 경우 (insert)
	 *  
	 *  return -1: 저장품 table 조회 실패
	 *         -2: Update 실패
	 *         -3: Insert 실패
	 *          1: 저장품 Table에 수신된 STL_NO 에 대한 데이타 등록 완료
	 */
	public int getInsUpdYdStock( JDTORecord inRecord) throws JDTOException{
	
		String szMethodName	= "getInsUpdYdStock";
		String szMsg		= "";
	
	
		YdStockDao ydStockDao = new YdStockDao();
	
		int nRstVal ;
		//리턴될 값을 저장할 RecordSet의 선언
	
		JDTORecordSet getStockRecSet = JDTORecordFactory.getInstance().createRecordSet("");
	
		try{
	
			szMsg="재료번호[" +inRecord.getFieldString("STL_NO") + "]로 저장품Table에 조회" ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
			//저장품 intGp= 0 :STL_NO 기준으로 조회한 후  return값을  nRtn에 			
			int nRtn = ydStockDao.getYdStock(inRecord, getStockRecSet, 0);
	
	
			// nRtn= 1: STL_NO(재료번호)에 대한 데이타가 존재 할 경우 
			if(nRtn > 0){
	
				szMsg="재료 번호 ["+ inRecord.getFieldString("STL_NO") + "]인 데이타가 존재함" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
				//수신 받은 재료번호에 대한 데이타가 이미 있으므로 UPDATE함.		
				nRstVal = ydStockDao.updYdStock(inRecord, 0);
	
				//UPDATE 성공 유무
				if(nRstVal > 0){
					szMsg="STL_NO [" + inRecord.getFieldString("STL_NO") + "] UPDATE 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else{
					szMsg="Update Error !,ErrorCode: " + nRstVal ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	
					//UPDATE error  -2 리턴
					return -2;
				}
				
			// nRtn= 0: STL_NO(재료번호)에 대한 데이타가 존재 하지 않았을 경우	
			}else if(nRtn == 0){
	
				// INSERT함
				szMsg="STL_NO ["+ inRecord.getFieldString("STL_NO") + "] 인 데이타가 저장품Table에 존재하지 않음" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
				nRstVal = ydStockDao.insYdStock(inRecord);
	
				szMsg="STL_NO ["+ inRecord.getFieldString("STL_NO") + "] INSERT 성공 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
				if(nRstVal == -2){
					szMsg="parameter error!!,ErrorCode: " + nRstVal ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	
					// Insert parameter error 
					return -3;
				}
				
			}else{ return -1; } // end of if-else
		
		}catch(Exception e){
	
			szMsg="저장품 Table 조회시  Exception Error"+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
	
		// 저장품 Table에 등록 완료 되었을 때 1을 리턴
		return 1;
	
	} // end of getInsUpdYdStock()
//=================================================================================================
//	연은정 END
//=================================================================================================	
	
	/**
	 * 오퍼레이션명 : 테스트						
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */			
	public void mTestAAAAA(JDTORecord msgRecord) throws JDTOException {
		//레코드 선언
		JDTORecord recPara = null;
		YdDelegate ydDelegate = new YdDelegate();

		String szMethodName = "mTestAAAAA";
		String szLogMsg = "";
		boolean blnRtnVal = false;
		int intRtnVal = 0;
		
		szLogMsg = "YdSimSeEJBBean::mTestAAAAA() IN";
		ydUtils.putLog(YdSimSeEJBBean.class.getName(), szMethodName, szLogMsg, YdConstant.DEBUG);
				
		try {
		
//			//레코드 생성
//			recPara = JDTORecordFactory.getInstance().create();
////			recPara.setField("JMS_TC_CD", ydDaoUtils.paraRecChkNull(msgRecord, "JMS_TC_CD"));
//			recPara.setField("JMS_TC_CD", "YDY4L004"); //
//			recPara.setField("YD_GP", "A");
//			recPara.setField("YD_SCH_CD", "ACTC02UM");
//			recPara.setField("YD_CRN_SCH_ID", "200905201105002680");
//			recPara.setField("YD_CAR_SCH_ID", "200905221923010380");
//			recPara.setField("YD_TCAR_SCH_ID", "200905081511000117");
//			recPara.setField("YD_STK_COL_GP", "JH0606");                          // 야드 적치열 구분
//			recPara.setField("YD_STK_BED_NO", "01");                              // 적치BED NO
//			recPara.setField("PNT_UNIT_CL_GP", "Y");                              // 포인트개폐구분
//			recPara.setField("YD_INFO_SYNC_CD", "B");                             // 야드정보동기화코드
//			recPara.setField("STL_NO", "YD700001");                       	      // 재료번호
//			recPara.setField("RCV_TCCODE", "RCVTCCODE");                          // 수신TC
//			recPara.setField("YD_L2_WR_GP", "U");                         		  // 야드L2실적구분
//			recPara.setField("YD_L3_HD_RS_CD", "0000");                      	  // 야드L3처리결과코드
//			recPara.setField("YD_L3_MSG", "권상(또는 권하)실적이 정상 처리 되었습니다");   // 야드L3MESSAGE
//			recPara.setField("YD_CAR_ARRSTRT_STAT", "A");                         // 야드차량착발상태 (A도착 S출발)
//			recPara.setField("YD_EQP_WRK_STAT", "L");                             // 야드설비작업상태 (L공차 U영차)
//			recPara.setField("YD_CAR_AIM_YD_GP", "D");                            // 야드차량목표야드구분	
//			recPara.setField("YD_WRK_PROG_STAT", "2");
//			
//			recPara.setField("YD_BAY_GP", "D");                                   // 동구분
//			recPara.setField("YD_WBOOK_ID", "200904051402000662");                // 작업예약ID
//			recPara.setField("CARD_NO", "1234");                                  // 카드번호
//			
//			recPara.setField("GOODS_NO", "ASLAB000001");
//			recPara.setField("RECEIPT_DATE", "20090316");
//			recPara.setField("RECEIPT_TIME", "121314");
//		    recPara.setField("STORE_LOC", "ABCDEFGHIJ");
//      	recPara.setField("PROD_ITEM_CODE", "ABCDEF");
//			ydUtils.displayRecord(szOperationName, recPara);
//			ydDelegate.sendMsg(recPara);
		
			   	              	 
			   	   
			ydUtils.putLog(YdSimSeEJBBean.class.getName(), szMethodName, "mTestAAAAA() IN", 3);

			//.....
			//전문 송신
			String strTemp = "";
			strTemp = ydDaoUtils.paraRecChkNull(msgRecord, "JMS_TC_CD");
			
			if(strTemp.equals("YDYDJ999"))
			{			
				msgRecord.setField("JMS_TC_CD", "YDC3L001"); //
			}
			else if(strTemp.equals("YDYDJ998"))
			{			
				msgRecord.setField("JMS_TC_CD", "YDC3L002"); //
			}
			else if(strTemp.equals("YDYDJ997"))
			{		
				msgRecord.setField("JMS_TC_CD", "YDC3L003"); //
			}
			else if(strTemp.equals("YDYDJ996"))
			{			
				msgRecord.setField("JMS_TC_CD", "YDC3L004"); //
			}
			else if(strTemp.equals("YDYDJ995"))
			{			
				msgRecord.setField("JMS_TC_CD", "YDC3L005"); //
			}
			else if(strTemp.equals("YDYDJ994"))
			{			
				msgRecord.setField("JMS_TC_CD", "YDC3L006"); //
			}
			else if(strTemp.equals("YDYDJ993"))
			{			
				msgRecord.setField("JMS_TC_CD", "YDC3L007"); //
			}
			else if(strTemp.equals("YDYDJ992"))
			{			
				msgRecord.setField("JMS_TC_CD", "YDC3L008"); //
			}
			else if(strTemp.equals("YDYDJ991"))
			{			
				msgRecord.setField("JMS_TC_CD", "YDY1L001"); //
			}
			else if(strTemp.equals("YDYDJ990"))
			{			
				msgRecord.setField("JMS_TC_CD", "YDY1L002"); //
			}
			else if(strTemp.equals("YDYDJ989"))
			{			
				msgRecord.setField("JMS_TC_CD", "YDY1L003"); //
			}
			else if(strTemp.equals("YDYDJ988"))
			{			
				msgRecord.setField("JMS_TC_CD", "YDY1L004"); //
			}
			else if(strTemp.equals("YDYDJ987"))
			{			
				msgRecord.setField("JMS_TC_CD", "YDY1L005"); //
			}
			else if(strTemp.equals("YDYDJ986"))
			{			
				msgRecord.setField("JMS_TC_CD", "YDY3L001"); //
			}
			else if(strTemp.equals("YDYDJ985"))
			{			
				msgRecord.setField("JMS_TC_CD", "YDY3L002"); //
			}
			else if(strTemp.equals("YDYDJ984"))
			{			
				msgRecord.setField("JMS_TC_CD", "YDY3L003"); //
			}
			else if(strTemp.equals("YDYDJ983"))
			{			
				msgRecord.setField("JMS_TC_CD", "YDY3L004"); //
			}
			else if(strTemp.equals("YDYDJ982"))
			{			
				msgRecord.setField("JMS_TC_CD", "YDY3L005"); //
			}
			
			ydDelegate.sendMsg(msgRecord);
			
			recPara = JDTORecordFactory.getInstance().create();
			//YDY4 TEST
			if(strTemp.equals("YDYDJ888"))
			{
				recPara.setField("JMS_TC_CD", "YDY4L00"	+ msgRecord.getFieldString("intGp"));
				recPara.setField("YD_GP", 			 msgRecord.getFieldString("YD_GP"));
				recPara.setField("YD_INFO_SYNC_CD",	 msgRecord.getFieldString("YD_INFO_SYNC_CD"));
				recPara.setField("YD_TCAR_SCH_ID",	 msgRecord.getFieldString("YD_TCAR_SCH_ID"));
				recPara.setField("YD_CRN_SCH_ID",	 msgRecord.getFieldString("YD_CRN_SCH_ID"));
				recPara.setField("YD_L2_WR_GP",		 msgRecord.getFieldString("YD_L2_WR_GP"));
				recPara.setField("YD_STK_BED_NO",	 msgRecord.getFieldString("YD_STK_BED_NO"));
				recPara.setField("YD_STK_COL_GP",	 msgRecord.getFieldString("YD_STK_COL_GP"));
				recPara.setField("STL_NO",			 msgRecord.getFieldString("STL_NO"));
				recPara.setField("YD_L3_HD_RS_CD",	 msgRecord.getFieldString("YD_L3_HD_RS_CD"));
				
				
			}
			
			//YDY5 TEST
			if(strTemp.equals("YDYDJ887"))
			{
				recPara.setField("JMS_TC_CD", "YDY5L00"+msgRecord.getFieldString("intGp"));
				recPara.setField("YD_GP", 			 msgRecord.getFieldString("YD_GP"));
				recPara.setField("YD_INFO_SYNC_CD",	 msgRecord.getFieldString("YD_INFO_SYNC_CD"));
				recPara.setField("YD_TCAR_SCH_ID",	 msgRecord.getFieldString("YD_TCAR_SCH_ID"));
				recPara.setField("YD_CRN_SCH_ID",	 msgRecord.getFieldString("YD_CRN_SCH_ID"));
				recPara.setField("YD_L2_WR_GP",		 msgRecord.getFieldString("YD_L2_WR_GP"));
				recPara.setField("YD_STK_BED_NO",	 msgRecord.getFieldString("YD_STK_BED_NO"));
				recPara.setField("YD_STK_COL_GP",	 msgRecord.getFieldString("YD_STK_COL_GP"));
				recPara.setField("STL_NO",			 msgRecord.getFieldString("STL_NO"));
				recPara.setField("YD_L3_HD_RS_CD",	 msgRecord.getFieldString("YD_L3_HD_RS_CD"));
				
				
			}
			ydDelegate.sendMsg(recPara);
		
//			YdStockDao ydStockDao = new YdStockDao();
//			YdCarSchDao ydCarSchDao = new YdCarSchDao();
//			YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
//			JDTORecordSet rsResult = null;
//			JDTORecord recPara = null;
//			JDTORecord recOut = null;
//			JDTORecord recTest = null;
//
//			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//			recPara = JDTORecordFactory.getInstance().create();
//			recTest = JDTORecordFactory.getInstance().create();
//			
//			recPara.setField("STL_NO", "Z00003 01");
//
////			ydStockDao.insYdStock(recPara);
//			ydStockDao.getYdStock(recPara, rsResult, 0);
//
//			if(rsResult.size() <= 0)
//				return ;
//		
//			recOut = rsResult.getRecord(0);
//			ydUtils.displayRecord(szOperationName, recOut);
//			
//			recTest.setField("STL_NO", recOut.getFieldString("STL_NO"));
//			// HFCRF2
//			recTest.setField("YD_EQP_ID", "ABCDEF");
//			ydStockDao.updYdStock(recTest, 0);

		} catch (Exception e) {
			szLogMsg = "Test 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		
		szLogMsg = "YdSimSeEJBBean::mTestAAAAA() OUT";
		ydUtils.putLog(YdSimSeEJBBean.class.getName(), szMethodName, szLogMsg, YdConstant.DEBUG);
		
	} //end of mTestAAAAA		
		
	
	
	
	/**
	 * 오퍼레이션명 : 테스트						
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */			
	public void mTestBBBBB(JDTORecord msgRecord) throws JDTOException {
		// 운영계로 안간다고 하여 테스트 해 봄
		JDTORecord recPara = null;
		YdDelegate ydDelegate = new YdDelegate();

		String szMethodName = "mTestBBBBB";
		String szLogMsg = "";
		String szTcCode = "";
		String szQueueName ="";
		String szWkGp="";
		String szYdJMSQName ="";
		String szYdEAIQName ="";
		String strTemp = "";
		String strStream = "";

		boolean blnRtnVal = false;
		int intRtnVal = 0;
		int nRtc=0;
		int nTcKind=0;

		TcConstMgr tcConstMgr = new TcConstMgr();
		JDTORecord tcRecord = null;	
		JDTORecordSet tcRecSet = null;
		YdDeleComm yddeleComm = new YdDeleComm();
		PropertyService propertyService = null;	
		
		szLogMsg = "YdSimSeEJBBean::mTestBBBBB() IN";
		ydUtils.putLog(YdSimSeEJBBean.class.getName(), szMethodName, szLogMsg, YdConstant.DEBUG);
				
		try {		
			szTcCode = ydUtils.getTcCode(msgRecord);
			szLogMsg = "Delegate 송신 요청 수신 (TC Code="+szTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

			if(szTcCode.equals("YDYDJ996")){
				msgRecord.setField("JMS_TC_CD", "YDPTJ001");
			}else if(szTcCode.equals("YDYDJ997")){
				msgRecord.setField("JMS_TC_CD", "YDPTJ002");				
			}else if(szTcCode.equals("YDYDJ998")){
				msgRecord.setField("JMS_TC_CD", "YDPTJ003");
			}else if(szTcCode.equals("YDYDJ999")){
				msgRecord.setField("JMS_TC_CD", "YDPTJ004");
			}

			szTcCode = ydUtils.getTcCode(msgRecord);
			
			// 1:JMS, 2:Remote EAI, 3:L2 EAI, 9:Facade
			nTcKind = ydTcConst.chkTcType(szTcCode);	
		
			// TC코드가 맞지 않을때
			if(nTcKind <=0) {
				szLogMsg = "Unknown TC Code("+szTcCode+") Error (ErrCode="+nTcKind+")";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				return ;
			}
			
			tcRecSet = JDTORecordFactory.getInstance().createRecordSet("YDDelegate");
			nRtc = tcConstMgr.makeTc(msgRecord, tcRecSet);
			if(nRtc<=0){
				szLogMsg=" TC("+szTcCode+") Data Make Error";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				return ;
			}

			propertyService = PropertyService.getInstance();
			szYdJMSQName =propertyService.getProperty("common.properties", "jms.queue.YD_MDB_QUEUE");
			szYdEAIQName =propertyService.getProperty("common.properties", "jms.queue.YD_EAI_QUEUE");
			
			switch(nTcKind){
			case 1:			
				szWkGp = szTcCode.substring(2, 4);
				szQueueName =propertyService.getProperty("common.properties", "jms.queue."+szWkGp+"_MDB_QUEUE");
				
				for(int i=0; i<nRtc;i++){
					tcRecord = null;
					tcRecord = tcRecSet.getRecord(i);
					yddeleComm.jmsQSnder(szQueueName, tcRecord);
				}
				break;	
			}
		} catch (Exception e) {
			szLogMsg = "Test 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		
		szLogMsg = "YdSimSeEJBBean::mTestBBBBB() OUT";
		ydUtils.putLog(YdSimSeEJBBean.class.getName(), szMethodName, szLogMsg, YdConstant.DEBUG);
		
	} //end of mTestBBBBB		
	
	
	/**
	 * 오퍼레이션명 : 테스트						
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */			
	public void mTestCCC(JDTORecord msgRecord) throws JDTOException {
		//레코드 선언
		JDTORecord recPara = null;
		YdDelegate ydDelegate = new YdDelegate();
		YdUtils ydUtils = new YdUtils();
		String szMethodName = "mTestCCC";
		String szLogMsg = "";
		String szOperationName = "테스트";
		boolean blnRtnVal = false;
		int intRtnVal = 0;
		
		szLogMsg = "YdSimSeEJBBean::mTestCCC() IN";
		ydUtils.putLog(YdSimSeEJBBean.class.getName(), szMethodName, szLogMsg, YdConstant.DEBUG);
				
		try {		
			ydUtils.putLog(YdSimSeEJBBean.class.getName(), szMethodName, "mTestCCC() IN", 3);


			//전문 송신
			String strTemp = "";
			String strStream = "";
			strTemp = ydDaoUtils.paraRecChkNull(msgRecord, "JMS_TC_CD");
			
			if(strTemp.equals("YDYDJ800")){
				recPara.setField("TC_CODE", msgRecord.getFieldString("TC_CODE"));
				recPara.setField("TC_CREATE_DDTT", 	ydUtils.getCurDate("yyyyMMdd24HHmmss")); 
				recPara.setField("GOODS_NO",	    msgRecord.getFieldString("GOODS_NO"));
				recPara.setField("RECEIPT_DATE",	msgRecord.getFieldString("RECEIPT_DATE"));
				recPara.setField("RECEIPT_TIME",	msgRecord.getFieldString("RECEIPT_TIME"));
				recPara.setField("YD_GP",		    msgRecord.getFieldString("YD_GP"));
				recPara.setField("STORE_LOC",	    msgRecord.getFieldString("STORE_LOC"));
				recPara.setField("PROD_ITEM_CODE",	msgRecord.getFieldString("PROD_ITEM_CODE"));
				ydUtils.displayRecord(szOperationName, recPara);
				//ydDelegate.lclSndMsg(recPara);
				//ydDelegate.tstSndMsg(recPara);
				//YdDeleComm ydDeleComm = new YdDeleComm();
				//ydDeleComm.remoteEaiSnder(recPara);
				ydDelegate.sendMsg(recPara);
			}
		} catch (Exception e) {
			szLogMsg = "Test 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		
		szLogMsg = "YdSimSeEJBBean::mTestCCC() OUT";
		ydUtils.putLog(YdSimSeEJBBean.class.getName(), szMethodName, szLogMsg, YdConstant.DEBUG);
		
	} //end of mTestCCC		
	
	/**
	 * 오퍼레이션명 : 테스트						
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */			
	public void mTestDDD(JDTORecord msgRecord) throws JDTOException {
		//레코드 선언
		JDTORecord recPara = null;
		YdDelegate ydDelegate = new YdDelegate();
		YdDeleComm deleComm = new YdDeleComm();
		TcConstMgr tcConstMgr = new TcConstMgr();
		YdUtils ydUtils = new YdUtils();
		String szMethodName = "mTestDDD";
		String szLogMsg = "";
		String szTcCode = "";
		String szCH = "";
		String szQueueName = "";
		boolean blnRtnVal = false;
		int intRtnVal = 0;
	    JDTORecordSet tcRecSet = null;
		JDTORecord tcRecord = null;	
		PropertyService propertyService = null;	
		tcRecSet =JDTORecordFactory.getInstance().createRecordSet("YDDelegate");
			
		try {		
			ydUtils.putLog(YdSimSeEJBBean.class.getName(), szMethodName, "mTestDDD() IN", 3);
			
			szTcCode = ydUtils.getTcCode(msgRecord);
			szLogMsg = "Delegate 송신 요청 수신 (TC Code="+szTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

			if(szTcCode.equals("YDYDJ995")){
				msgRecord.setField("JMS_TC_CD", "YDPRJ001");
			}else if(szTcCode.equals("YDYDJ994")){
				msgRecord.setField("JMS_TC_CD", "YDPRJ002");
			}else if(szTcCode.equals("YDYDJ800")){
			     msgRecord.setField("JMS_TC_CD", "YDY1L001");
			}else if(szTcCode.equals("YDYDJ801")){
			     msgRecord.setField("JMS_TC_CD", "YDY1L002");
			}else if(szTcCode.equals("YDYDJ802")){
			     msgRecord.setField("JMS_TC_CD", "YDY1L003");
			}else if(szTcCode.equals("YDYDJ803")){
			     msgRecord.setField("JMS_TC_CD", "YDY1L004");
			}else if(szTcCode.equals("YDYDJ804")){
			     msgRecord.setField("JMS_TC_CD", "YDY1L005");
			}else if(szTcCode.equals("YDYDJ805")){
			     msgRecord.setField("JMS_TC_CD", "YDY3L001");
			}else if(szTcCode.equals("YDYDJ806")){
			     msgRecord.setField("JMS_TC_CD", "YDY3L002");
			}else if(szTcCode.equals("YDYDJ807")){
			     msgRecord.setField("JMS_TC_CD", "YDY3L003");
			}else if(szTcCode.equals("YDYDJ808")){
			     msgRecord.setField("JMS_TC_CD", "YDY3L004");
			}else if(szTcCode.equals("YDYDJ809")){
			     msgRecord.setField("JMS_TC_CD", "YDY3L005");
			}else if(szTcCode.equals("YDYDJ810")){
			     msgRecord.setField("JMS_TC_CD", "YDY4L001");
			}else if(szTcCode.equals("YDYDJ811")){
			     msgRecord.setField("JMS_TC_CD", "YDY4L002");
			}else if(szTcCode.equals("YDYDJ812")){
			     msgRecord.setField("JMS_TC_CD", "YDY4L004");
			}else if(szTcCode.equals("YDYDJ813")){
			     msgRecord.setField("JMS_TC_CD", "YDY4L005");
			}else if(szTcCode.equals("YDYDJ814")){
			     msgRecord.setField("JMS_TC_CD", "YDY5L001");
			}else if(szTcCode.equals("YDYDJ815")){
			     msgRecord.setField("JMS_TC_CD", "YDY5L002");
			}else if(szTcCode.equals("YDYDJ816")){
			     msgRecord.setField("JMS_TC_CD", "YDY5L004");
			}else if(szTcCode.equals("YDYDJ817")){
			     msgRecord.setField("JMS_TC_CD", "YDY5L005");
			}else if(szTcCode.equals("YDYDJ818")){
			     msgRecord.setField("JMS_TC_CD", "YDY5L006");
			}else if(szTcCode.equals("YDYDJ819")){
			     msgRecord.setField("JMS_TC_CD", "YDTSJ013");
			}else if(szTcCode.equals("YDYDJ820")){
			    msgRecord.setField("JMS_TC_CD", msgRecord.getFieldString("YD_TC_CODE_R"));	
			}
				
			ydDelegate.sendMsg(msgRecord);
			
		} catch (Exception e) {
			szLogMsg = "Test 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		
		szLogMsg = "YdSimSeEJBBean::mTestDDD() OUT";
		ydUtils.putLog(YdSimSeEJBBean.class.getName(), szMethodName, szLogMsg, YdConstant.DEBUG);
		
	} //end of mTestCCC		
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : C연주 슬라브 작업예약 삭제
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord (YD_WBOOK_ID)
	 * @return
	 * @throws JDTOException
	 */
	public String delWBookC(JDTORecord msgRecord)throws JDTOException  {
		
		
		// 크레인스케줄 DAO
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
		// 작업예약 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();

		YdDelegate ydDelegate = new YdDelegate();

		// 대차 , 차량스케줄 DAO

		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();

		// 리턴레코드셋
		JDTORecordSet rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
		// 파라미터 레코드 생성
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecord recCheck = JDTORecordFactory.getInstance().create();
		JDTORecord inRec = JDTORecordFactory.getInstance().create();

		JDTORecord recTemp = JDTORecordFactory.getInstance().create();

		// 파라미터 스크링 변수
		String szV_YD_CRN_SCH_ID = null;
		String szV_DEL_YN = null;
		String szV_MODIFIER = null;
		String szV_YD_WBOOK_ID = null;
		String szOperationName = "C연주 슬라브 작업예약 삭제";

		String szSchCd = null;
		String szCarGp = null;
		String szULGp = null;

		// 리턴값
		int intRtnVal = 0;

		// 체크 값
		String szC_YD_WRK_PROG_STAT = null;

		String szMsg = "";
		String szMethodName = "wrkCncl";
		String szStlNo = "";

		// 크레인 작업 지시 EJB Call 시 필요한 변수
		String szEjbConName = "";
		String szLogMsg = "";
		String szJMS_TC_CD = "";
		EJBConnector ejbConn = null;
		String szYdGp = "";
		JDTORecord recDelPara = null;
		String szEqpId = "";
		String szV_YD_SCH_CD = "";
		String szYD_USER_ID = "";
 
		//들어온데이타  display 
		
		 szMsg="작업예약 삭제 처리 기능 시작";
	     ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.DEBUG);
		 
		 ydUtils.displayRecord("작업예약 삭제 처리 기능 IN-PARA", msgRecord);

		 
		szYD_USER_ID 	= ydDaoUtils.paraRecChkNull(msgRecord , "MODIFIER");
		szV_YD_WBOOK_ID = ydDaoUtils.paraRecChkNull(msgRecord , "YD_WBOOK_ID");
		szSchCd 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
 
		
		//------------------------------------------------------------------------------------------------
		//	차량 / 대차 작업과 관계있는 작업 Clear (2010.01.13 이현성 수정)
		//------------------------------------------------------------------------------------------------
		
		String szRtnMsg = "";
		// 차량 또는 대차 스케줄에 있는 작업예약 ID를 Clear
		inRec    = JDTORecordFactory.getInstance().create();
		inRec.setField("MODIFIER" , szYD_USER_ID);
		inRec.setField("YD_WBOOK_ID", szV_YD_WBOOK_ID);
		
		
		szRtnMsg = yddatautil.delWBookBefoCarOrTCar(inRec);
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szV_YD_WBOOK_ID+"] 대차/차량 스케줄 Clear성공 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		}else if(szRtnMsg.equals(YdConstant.RETN_CD_FAILURE)){
			szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szV_YD_WBOOK_ID+"] 대차/차량 스케줄 Clear 실패 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
		}else{
			szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szV_YD_WBOOK_ID+"]  " + szRtnMsg ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		

		//------------------------------------------------------------------------------------------------
		//	작업예약/재료 삭제
		//------------------------------------------------------------------------------------------------
		
		szRtnMsg = YdCommonUtils.delYdWrkbookNMtl(szV_YD_WBOOK_ID, szYD_USER_ID);
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szV_YD_WBOOK_ID+"]삭제 성공";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}else{
			szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szV_YD_WBOOK_ID+"]삭제 실패 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		//------------------------------------------------------------------------------------------------
		

 
		//------------------------------------------------------------------------------------------------
		//	준비스케줄 원복
		//------------------------------------------------------------------------------------------------
		String szYD_WBOOK_ID = szV_YD_WBOOK_ID;
		JDTORecordSet outRecSet = null;
		// 준비스케줄 원복
		/*
		 * 상차작업예약ID와 연관된 준비스케줄과 준비재료의 DEL_YN항목을 N으로 설정 수정자 : 임춘수 수정일 : 2009.10.26
		 */
		szMsg = "[JSP Session : " + szOperationName + "] 삭제된 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 조회 시작";
		ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);

		outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_WBOOK_ID" , szYD_WBOOK_ID);

		YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();

		intRtnVal = ydPrepSchDao.getYdPrepsch(recPara , outRecSet , 8);

		if (intRtnVal < 0) {
			szMsg = "[JSP Session : " + szOperationName + "] 삭제된 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 조회 시 오류발생 : 반환값 - " + intRtnVal;
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
		} else if (intRtnVal == 0) {
			szMsg = "[JSP Session : " + szOperationName + "] 삭제된 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 조회 시 존재하지 않음 ";
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
		} else {

			outRecSet.first();
			recPara = outRecSet.getRecord();

			String szYD_PREP_SCH_ID = ydDaoUtils.paraRecChkNull(recPara , "YD_PREP_SCH_ID");

			szMsg = "[JSP Session : " + szOperationName + "] 삭제된 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄[" + szYD_PREP_SCH_ID + "]이 존재하므로 DEL_YN => N으로 설정 시작";
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);

			YdPrepMtlDao ydPrepMtlDao = new YdPrepMtlDao();

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_PREP_SCH_ID" , szYD_PREP_SCH_ID);
			recPara.setField("DEL_YN" , "N");
			recPara.setField("MODIFIER" , szMethodName.length() > 10 ? szMethodName.substring(0 , 10) : szMethodName);
			// 준비재료
			intRtnVal = ydPrepMtlDao.uptDelYdPrepmtlByPrepSchId(recPara);

			// 준비스케줄
			recPara.setField("YD_WBOOK_ID" , "");
			intRtnVal = ydPrepSchDao.updDelYdPrepsch(recPara);

			szMsg = "[JSP Session : " + szOperationName + "] 삭제된 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄[" + szYD_PREP_SCH_ID + "]과 준비재료 DEL_YN => N으로 설정 성공";
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
		}
		//------------------------------------------------------------------------------------------------

		
		return YdConstant.RETN_CD_SUCCESS ;
	
	}// end of delWBookC()
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
  //---------------------------------------------------------------------------
} // end of class





