package com.inisteel.cim.yd.ydSch.CraneSch;

import java.util.Iterator;
import java.util.List;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSpecDao.YdCrnSpecDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydLocSrchRngDao.YdLocSrchRngDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydWrkHistDao.YdWrkHistDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDBAssist;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.loc.CoilYdToLocDcsnUtil;
import com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
/**
 * 크레인스케줄 Session EJB
 *
 * @ejb.bean name="CoilCrnSchSeEJB" jndi-name="CoilCrnSchSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CoilCrnSchSeEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName=getClass().getName();
	private YdUtils ydUtils =new YdUtils();
	private YdDaoUtils ydDaoUtils =new YdDaoUtils();
	private YdTcConst ydTcConst =new YdTcConst();
	private YdDBAssist ydDBAssist =new YdDBAssist();
	private YdDelegate ydDelegate = new YdDelegate();
	private YmComm YmComm = new YmComm();
	private boolean bDebugFlag=true;
	private YmCommDAO commDao = new YmCommDAO();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	 //SCH MAIN
    
    /**
	 * 오퍼레이션명 : C열연 크레인스케줄Main (YDYDJ509)(H/J)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * @수정자 : 박지열
	 * @수정내용 : 등록자/수정자 로그인ID 적용
	 */
	public JDTORecord procY5CrnSchMain(JDTORecord msgRecord)throws JDTOException  {
	
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		ymCommonDAO dao 			= ymCommonDAO.getInstance();
		JDTORecordSet outRecset    	= JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsWrkbookmtl 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecord outRecord  		= JDTORecordFactory.getInstance().create(); 
		JDTORecord outRecord1  		= JDTORecordFactory.getInstance().create(); 
		JDTORecord outRecord2  		= JDTORecordFactory.getInstance().create(); 
		JDTORecord recPara  		= JDTORecordFactory.getInstance().create(); 
		JDTORecord recCheck  		= JDTORecordFactory.getInstance().create();
		JDTORecordSet rsRtnVal 		= JDTORecordFactory.getInstance().createRecordSet("temRs"); 
		
		YdCrnSchDao  ydCrnSchDao        = new  YdCrnSchDao();
		List dmList					= null;
		JDTORecord inRecord 		= null;
		String szMsg        		= "";
		String szMethodName 		= "procY5CrnSchMain";
		String szOperationName 		= "C열연 크레인스케줄Main";
		//설비Id
		String szEqpId      	= "";
		//스케줄코드
		String szSchCd      	= "";
		String szYD_WBOOK_ID 	= null;
		String sYD_WRK_PROG_STAT_CD= "";
		String sHDONG_YN	 	= null;
		String sRTN_CD			= null;
		String sRTN_MSG			= null;
		String sYD_SCH_CD		= null;
		String sYD_CRN_SCH_ID	= null;
		String sYD_EQP_ID		= null;
		int 	intRtnVal		=0;
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode.equals("")){
			szMsg="[" + szOperationName + "] TC Code Error";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			//m_ctx.setRollbackOnly();
			return outRecord;
		}
		if(bDebugFlag){
			szMsg="[" + szOperationName + "] [DEBUG] 전문수신 : TCCODE=" +szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try{

			szMsg="[스케쥴메인시작]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//차량동간이적
			String szYD_SCH_CD	= StringHelper.evl(msgRecord.getFieldString("YD_SCH_CD"), "0");
//			if(szYD_SCH_CD.substring(2, 8).equals("TR05MM")
//				|| szYD_SCH_CD.substring(2, 8).equals("TR06MM")
//				|| szYD_SCH_CD.substring(2, 8).equals("TR07MM")
//				|| szYD_SCH_CD.substring(2, 8).equals("TR08MM")
//				|| szYD_SCH_CD.substring(2, 8).equals("TR57MM")
//				|| szYD_SCH_CD.substring(2, 8).equals("TR58MM")
//			
//			){
			//차량동간이적
			if("TR".equals(szYD_SCH_CD.substring(2 , 4)) && "MM".equals(szYD_SCH_CD.substring(6 , 8))){
				
				//해당 차량으로 하차지 스케줄 존재 유무 체크(존재하면 스케줄 호출 SKIP)
				String sQueryId = "yd.common.dao.ydStockDao.getYdschchk";
				dmList = dao.getCommonList(sQueryId, new Object[] { szYD_SCH_CD,szYD_SCH_CD});
				
				if(dmList.size()>0){
					outRecord.setField("RTN_CD", "1");						
					outRecord.setField("RTN_MSG", "해당 차량으로 하차지 스케줄 존재 유무 체크(존재하면 스케줄 호출 SKIP)");						
					return outRecord;
				}
			}
			//락 문제로 트렌젝션 분리 제거(2012.09.10)
			EJBConnector ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);			
			outRecord1 = (JDTORecord)ejbConn.trx("procY5CrnSchMainNEW", new Class[]  { JDTORecord.class }
											  , new Object[] { msgRecord  });
			
			sRTN_CD			= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			sYD_CRN_SCH_ID 	= StringHelper.evl(outRecord1.getFieldString("YD_CRN_SCH_ID"), "");
			sYD_EQP_ID		= StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
			if(sRTN_CD.equals("0")){ 
				//throw new DAOException(szMsg);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", sRTN_MSG);
				//m_ctx.setRollbackOnly();
				return outRecord;
			} else if (sRTN_CD.equals("1")){ 	

//크레인작업지시요구 판단
//L2 작업지시 송신			
				if (!(sYD_CRN_SCH_ID.equals(""))){ 
					
					//파라미터 레코드 setting
					recPara.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID);
					rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
					intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 0);
					
					if (intRtnVal < 1 ){
						szMsg="해당크레인 스케줄이 존재하지않습니다";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" , "0");	
						outRecord.setField("RTN_MSG", szMsg);
						return outRecord;
					}
					
					rsRtnVal.first();
					recCheck = rsRtnVal.getRecord();
					sHDONG_YN = ydDaoUtils.paraRecChkNull(recCheck, "HDONG_YN");
					sYD_WRK_PROG_STAT_CD = ydDaoUtils.paraRecChkNull(recCheck, "YD_WRK_PROG_STAT_CD");
					
					if("Y".equals(sHDONG_YN)){
						szMsg="[" + szOperationName + "]크레인 변경 작업 H1호기2: " + sHDONG_YN;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						recPara 	= JDTORecordFactory.getInstance().create();
						recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
						recPara.setField("YD_EQP_ID", 		sYD_EQP_ID);
						recPara.setField("YD_CRN_SCH_ID", 	sYD_CRN_SCH_ID);
						recPara.setField("YD_WRK_PROG_STAT_CD", 	sYD_WRK_PROG_STAT_CD);
	
						JDTORecord [] inRecord2 =  new JDTORecord[1];
						inRecord2[0]	= recPara;
	
						ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
						outRecord = (JDTORecord)ejbConn.trx("wrkCrnChange", new Class[] { JDTORecord[].class }, new Object[] { inRecord2 });
						
						sRTN_CD	    = StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
						
						ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.DEBUG);
						if(sRTN_CD.equals("0")){ 
							sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
							//throw new DAOException(szMsg);
							outRecord.setField("RTN_CD" , "0");	
							outRecord.setField("RTN_MSG", sRTN_MSG);	
							//m_ctx.setRollbackOnly();
							return outRecord;
						}	
					}else{				
					
						szMsg="[" + szOperationName + "]L2 작업지시 송신  호출: " + sRTN_CD;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						outRecord2  = (JDTORecord)this.chkY5CrnWrkOrdReq(outRecord1);	
						sRTN_CD	    = StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
						
						ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.DEBUG);
						if(sRTN_CD.equals("0")){ 
							sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
							//throw new DAOException(szMsg);
							outRecord.setField("RTN_CD" , "0");	
							outRecord.setField("RTN_MSG", sRTN_MSG);	
							//m_ctx.setRollbackOnly();
							return outRecord;
						}	
					}
					
				}	
					
			}	
			
			
		} catch (Exception e) {
			
			szMsg="[ERROR] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		
		}	// end try catch문
		
		szMsg="C열연크레인스케줄 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		outRecord.setField("RTN_MSG"	, sRTN_MSG);	
		outRecord.setField("RTN_CD" 	, "1");	
		return outRecord;

	} // end of procY5CrnSchMain()
   
	/**
	 * 오퍼레이션명 : C열연 크레인스케줄Main (YDYDJ509)(H/J)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procY5CrnSchMainNEW(JDTORecord msgRecord)throws JDTOException  {
	
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		
		JDTORecordSet outRecset    	= JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsWrkbookmtl 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecord outRecord  		= JDTORecordFactory.getInstance().create(); 
		JDTORecord outRecord1  		= JDTORecordFactory.getInstance().create(); 
		JDTORecord outRecord2  		= JDTORecordFactory.getInstance().create(); 
		
		JDTORecord inRecord 		= null;
		String szMsg        		= "";
		String szMethodName 		= "procY5CrnSchMainNEW";
		String szOperationName 		= "C열연 크레인스케줄MainNEW";
		//설비Id
		String szEqpId      	= "";
		//스케줄코드
		String szSchCd      	= "";
		String szYD_WBOOK_ID 	= null;
		

		String sRTN_CD			= null;
		String sRTN_MSG			= null;
		String sYD_SCH_CD		= null;
		String sYD_CRN_SCH_ID	= null;

		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode.equals("")){
			szMsg="[" + szOperationName + "] TC Code Error";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			//m_ctx.setRollbackOnly();
			return outRecord;
		}
		if(bDebugFlag){
			szMsg="[" + szOperationName + "] [DEBUG] 전문수신 : TCCODE=" +szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try{

			szMsg="[스케쥴메인시작]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			szEqpId 		= ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID");   //크레인설비ID 
			szSchCd 		= ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD");   //크레인스케줄코드
			szYD_WBOOK_ID 	= ydDaoUtils.paraRecChkNull(msgRecord,"YD_WBOOK_ID");

			szMsg="[" + szOperationName + "] 메소드 시작 파라미터 확인 : YD_EQP_ID : " + szEqpId + " , YD_SCH_CD : " + szSchCd + ", 작업예약ID["+szYD_WBOOK_ID+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


//C열연크레인스케줄수행조건판단
//작업예약,대상 의 현위치 정보,권상모음  UPDATE 처리 함
			szMsg="[" + szOperationName + "] 크레인스케줄수행조건판단 및 스케줄생성 가능한 작업예약의 재료들이 존재하는 BED정보를 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			outRecord1 	= JDTORecordFactory.getInstance().create();
			
			outRecord1 	= (JDTORecord)this.Y5ChkCrnSchEffectConditionCoil(msgRecord, rsWrkbookmtl);

			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			sYD_SCH_CD  = StringHelper.evl(outRecord1.getFieldString("YD_SCH_CD"), "");

			szMsg="[" + szOperationName + "]C열연 크레인스케줄수행조건판단 완료: " + sRTN_CD;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			if(sRTN_CD.equals("0")){ 
				//throw new DAOException(szMsg);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				//m_ctx.setRollbackOnly();
				return outRecord;
			} else { 
				szSchCd = sYD_SCH_CD;
			}	
			
//그룹핑 파라미터 셋팅
//적치단의 재료상태를 권상대기로 변경처리 
//주작업 및 보조 작업 셋팅 
//			intRtnVal = this.Y5CrnSchSortCoil(rsWrkbookmtl, szSchCd, outRecset);
			outRecord1 = JDTORecordFactory.getInstance().create();
			
			outRecord1 = (JDTORecord)this.Y5CrnSchSortCoil(rsWrkbookmtl, szSchCd, outRecset);
			
			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			
			szMsg="[" + szOperationName + "]그룹핑 파라미터 셋팅 완료: " + sRTN_CD;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.DEBUG);
			if(sRTN_CD.equals("0")){ 
				//throw new DAOException(szMsg);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				///m_ctx.setRollbackOnly();
				return outRecord;
			}	
	
//크레인 스케줄 등록
			outRecord1 = JDTORecordFactory.getInstance().create();
			
			outRecord1 = (JDTORecord)this.Y5CrnSchInsCoil(outRecset, msgRecord);
			
			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			
			String sMSG_ID		= StringHelper.evl(outRecord1.getFieldString("MSG_ID"), "");
			String sYD_EQP_ID	= StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
			String sYD_WBOOK_ID	= StringHelper.evl(outRecord1.getFieldString("YD_WBOOK_ID"), "");
			
			ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.DEBUG);
			if(sRTN_CD.equals("0")){ 
				//throw new DAOException(szMsg);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				//m_ctx.setRollbackOnly();
				return outRecord;
			}	

			szMsg="[" + szOperationName + "] 크레인 스케줄 등록 처리 완료: " + sRTN_CD;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			
//c8			
//저장위치결정MAIN호출  : YDYDJ510, 설비ID, 작업예약ID
//크레인스케줄의 권상지시위치 등록한다.
//to위치 적치단에 저장
//크레인 스케줄 권하지시위치 등록		
//정정설비 별도 TO 위치 결정
			szMsg="[" + szOperationName + "] TO위치 결정시작: " + sRTN_CD;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("MSG_ID",      sMSG_ID);
			inRecord.setField("YD_EQP_ID",   sYD_EQP_ID);
			inRecord.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
			inRecord.setField("YD_USER_ID",  ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID"));/*2010.07.29 - 박지열 (수정자 추가)*/
			
			outRecord1 = JDTORecordFactory.getInstance().create();
//C증설	 
			if((szSchCd.equals("HBKD01LM"))||(szSchCd.equals("HBKD02LM"))||(szSchCd.equals("HBKD03LM"))||(szSchCd.equals("HBKD04LM"))||(szSchCd.equals("HBKD05LM"))||(szSchCd.equals("HBKE01UM"))||
			   (szSchCd.equals("HAKD01LM"))||(szSchCd.equals("HAKD02LM"))||(szSchCd.equals("HAKD03LM"))||(szSchCd.equals("HAKD04LM"))||(szSchCd.equals("HAKD05LM"))||(szSchCd.equals("HAKE01UM"))||
			   (szSchCd.equals("HBFD01LM"))||(szSchCd.equals("HBFD02LM"))||(szSchCd.equals("HBFD04LM"))||(szSchCd.equals("HAKD01UM"))||(szSchCd.equals("HBKD01UM"))||
	  	       (szSchCd.equals("JBKD01LM"))||//(szSchCd.equals("JBFD01LM"))||
	  	       (szSchCd.equals("JAKD01LM"))||
  		  	   //(szSchCd.equals("JBTC01MM"))||(szSchCd.equals("JBTC02MM"))||
  		  	   (szSchCd.equals("JBTC05MM"))||
  		  	   (szSchCd.equals("JATC05MM"))||
  		  	   (szSchCd.equals("HBKE03UM"))||(szSchCd.equals("HBKD03UM"))||
  		  	   (szSchCd.equals("HAKE03UM"))||(szSchCd.equals("HAKD03UM"))||
  		  	   (szSchCd.equals("HBFE03UM"))||(szSchCd.equals("HBFD03UM"))||

  		  	   (szSchCd.equals("HCKD01LM"))||(szSchCd.equals("HCKD02LM"))||(szSchCd.equals("HCKD03LM"))||(szSchCd.equals("HCKD04LM"))||(szSchCd.equals("HCKD05LM"))||(szSchCd.equals("HCFE01UM"))||(szSchCd.equals("HCKD01UM"))||
	  	       (szSchCd.equals("HCFD01LM"))||(szSchCd.equals("HCFD02LM"))||(szSchCd.equals("HCFD04LM"))||
  		  	   (szSchCd.equals("JCKD01LM"))||(szSchCd.equals("JCFD01LM"))||(szSchCd.equals("HCKE01UM"))||
  		  	   (szSchCd.equals("JCTC01MM"))||(szSchCd.equals("JCTC02MM"))||(szSchCd.equals("JCTC05MM"))||
  		  	   (szSchCd.equals("HCKE03UM"))||(szSchCd.equals("HCKD03UM"))||
  		  	   (szSchCd.equals("HCFE03UM"))||(szSchCd.equals("HCFD03UM"))||
  		  	
	  	 	   (szSchCd.equals("HEDD01LM"))||(szSchCd.equals("HEDD02LM"))||(szSchCd.equals("HEDD03LM"))||(szSchCd.equals("HEDD04LM"))||(szSchCd.equals("HEDD05LM"))||(szSchCd.equals("HEDE01UM"))||
	  	 	   (szSchCd.equals("JEDD01LM"))||(szSchCd.equals("JETC01MM"))||(szSchCd.equals("JETC02MM"))||(szSchCd.equals("HEDD01UM"))||
	  	 	   (szSchCd.equals("HEDE03UM"))||(szSchCd.equals("HEDD03UM"))||

	  	 	   (szSchCd.equals("HGFD01LM"))||(szSchCd.equals("HGFD02LM"))||(szSchCd.equals("HGFD04LM"))||(szSchCd.equals("HGFE01UM"))||
			   (szSchCd.equals("JGFD01LM"))||(szSchCd.equals("JGTC01MM"))||(szSchCd.equals("JGTC02MM"))||
			   (szSchCd.equals("HGFE03UM"))||(szSchCd.equals("HGFD03UM"))||
			   
	  	 	   (szSchCd.equals("HHKD01LM"))||(szSchCd.equals("HHKD02LM"))||(szSchCd.equals("HHKD03LM"))||(szSchCd.equals("HHKD04LM"))||(szSchCd.equals("HHKD05LM"))||(szSchCd.equals("HHKE01UM"))||
	  	 	   (szSchCd.equals("JHKD01LM"))||(szSchCd.equals("JHTC01MM"))||(szSchCd.equals("JHTC02MM"))||(szSchCd.equals("HHKD01UM"))||
	  	 	   (szSchCd.equals("HHKE03UM"))||(szSchCd.equals("HHKD03UM"))||
			   
//	  	 	   151224 hun 지포장추출 스케줄코드 추가
	  	 	    (szSchCd.equals("HAKD06LM"))||
			    (szSchCd.equals("HBKD06LM"))||
			    (szSchCd.equals("HBFD06LM"))||
			    (szSchCd.equals("HCKD06LM"))||
			    (szSchCd.equals("HCFD06LM"))||
			    (szSchCd.equals("HDFD06LM"))||
			    (szSchCd.equals("HEDD06LM"))||
			    (szSchCd.equals("HFFD06LM"))||
			    (szSchCd.equals("HGFD06LM"))||
			    (szSchCd.equals("HHKD06LM"))||
	  	 	   
			   (szSchCd.substring(2, 4).equals("CV"))  ){ // CONV 입고
		
				szMsg="[" + szOperationName + "] TO위치 설비위치 검색:procY5CrnEqpLocDeciMain ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord1 = (JDTORecord)this.procY5CrnEqpLocDeciMain(inRecord);
			} else {	
				szMsg="[" + szOperationName + "] TO위치 저장위치,대차 위치 검색: procY5CrnStrLocDeciMain ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord1 = (JDTORecord)this.procY5CrnStrLocDeciMain(inRecord);
			}
			
			outRecord1.setField("YD_EQP_ID" , sYD_EQP_ID);
			
			szMsg="[스케쥴메인종료]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			
			szMsg="[ERROR] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		
		}	// end try catch문
		
		return outRecord1;

	} // end of procY5CrnSchMainNEW()
	
	
	
	/**
	 * 오퍼레이션명 : C열연 크레인스케줄MainB (YDYDJ599)(H/J)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procY5CrnSchMainB(JDTORecord msgRecord)throws JDTOException  {
	
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		YdCrnSchDao  ydCrnSchDao        = new  YdCrnSchDao();
		JDTORecordSet outRecset    	= JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsWrkbookmtl 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecord outRecord  		= JDTORecordFactory.getInstance().create(); 
		JDTORecord outRecord1  		= JDTORecordFactory.getInstance().create(); 
		JDTORecord outRecord2  		= JDTORecordFactory.getInstance().create(); 
		JDTORecord recPara  		= JDTORecordFactory.getInstance().create(); 
		JDTORecord recCheck  		= JDTORecordFactory.getInstance().create();
		JDTORecordSet rsRtnVal 		= JDTORecordFactory.getInstance().createRecordSet("temRs"); 
		JDTORecord inRecord 		= null;
		String szMsg        		= "";
		String szMethodName 		= "procY5CrnSchMainB";
		String szOperationName 		= "C열연 크레인스케줄MainB";
		//설비Id
		String szEqpId      	= "";
		//스케줄코드
		String szSchCd      	= "";
		String szYD_WBOOK_ID 	= null;
		String sYD_WRK_PROG_STAT_CD= "";
		String sHDONG_YN		= null;
		String sRTN_CD			= null;
		String sRTN_MSG			= null;
		String sYD_SCH_CD		= null;
		String sYD_CRN_SCH_ID	= null;
		int 	intRtnVal		=0;
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode.equals("")){
			szMsg="[" + szOperationName + "] TC Code Error";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			m_ctx.setRollbackOnly();
			return outRecord;
		}
		if(bDebugFlag){
			szMsg="[" + szOperationName + "] [DEBUG] 전문수신 : TCCODE=" +szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try{
	
			szMsg="[스케쥴메인시작]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			szEqpId 		= ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID");   //크레인설비ID 
			szSchCd 		= ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD");   //크레인스케줄코드
			szYD_WBOOK_ID 	= ydDaoUtils.paraRecChkNull(msgRecord,"YD_WBOOK_ID");

			szMsg="[" + szOperationName + "] 메소드 시작 파라미터 확인 : YD_EQP_ID : " + szEqpId + " , YD_SCH_CD : " + szSchCd + ", 작업예약ID["+szYD_WBOOK_ID+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


//C열연크레인스케줄수행조건판단
//작업예약,대상 의 현위치 정보,권상모음  UPDATE 처리 함
			szMsg="[" + szOperationName + "] 크레인스케줄수행조건판단 및 스케줄생성 가능한 작업예약의 재료들이 존재하는 BED정보를 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			outRecord1 	= JDTORecordFactory.getInstance().create();
			
			outRecord1 	= (JDTORecord)this.Y5ChkCrnSchEffectConditionCoil(msgRecord, rsWrkbookmtl);

			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			sYD_SCH_CD  = StringHelper.evl(outRecord1.getFieldString("YD_SCH_CD"), "");

			szMsg="[" + szOperationName + "]C열연 크레인스케줄수행조건판단 완료: " + sRTN_CD;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			if(sRTN_CD.equals("0")){ 
				//throw new DAOException(szMsg);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				m_ctx.setRollbackOnly();
				return outRecord;
			} else { 
				szSchCd = sYD_SCH_CD;
			}	
			
//그룹핑 파라미터 셋팅
//적치단의 재료상태를 권상대기로 변경처리 
//주작업 및 보조 작업 셋팅 
//			intRtnVal = this.Y5CrnSchSortCoil(rsWrkbookmtl, szSchCd, outRecset);
			outRecord1 = JDTORecordFactory.getInstance().create();
			
			outRecord1 = (JDTORecord)this.Y5CrnSchSortCoil(rsWrkbookmtl, szSchCd, outRecset);
			
			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			
			szMsg="[" + szOperationName + "]그룹핑 파라미터 셋팅 완료: " + sRTN_CD;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.DEBUG);
			if(sRTN_CD.equals("0")){ 
				//throw new DAOException(szMsg);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				m_ctx.setRollbackOnly();
				return outRecord;
			}	
	
//크레인 스케줄 등록
			outRecord1 = JDTORecordFactory.getInstance().create();
			
			outRecord1 = (JDTORecord)this.Y5CrnSchInsCoil(outRecset, msgRecord);
			
			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			
			String sMSG_ID		= StringHelper.evl(outRecord1.getFieldString("MSG_ID"), "");
			String sYD_EQP_ID	= StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
			String sYD_WBOOK_ID	= StringHelper.evl(outRecord1.getFieldString("YD_WBOOK_ID"), "");
			
			ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.DEBUG);
			if(sRTN_CD.equals("0")){ 
				//throw new DAOException(szMsg);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				m_ctx.setRollbackOnly();
				return outRecord;
			}	

			szMsg="[" + szOperationName + "] 크레인 스케줄 등록 처리 완료: " + sRTN_CD;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			
//c8			
//저장위치결정MAIN호출  : YDYDJ510, 설비ID, 작업예약ID
//크레인스케줄의 권상지시위치 등록한다.
//to위치 적치단에 저장
//크레인 스케줄 권하지시위치 등록		
//정정설비 별도 TO 위치 결정
			szMsg="[" + szOperationName + "] TO위치 결정시작: " + sRTN_CD;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("MSG_ID",      sMSG_ID);
			inRecord.setField("YD_EQP_ID",   sYD_EQP_ID);
			inRecord.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
			inRecord.setField("YD_USER_ID",  ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID"));/*2010.07.29 - 박지열 (수정자 추가)*/
			
			outRecord1 = JDTORecordFactory.getInstance().create();
//C증설		 
 			   
 			   
			if((szSchCd.equals("HBKD01LM"))||(szSchCd.equals("HBKD02LM"))||(szSchCd.equals("HBKD03LM"))||(szSchCd.equals("HBKD04LM"))||(szSchCd.equals("HBKD05LM"))||(szSchCd.equals("HBKE01UM"))||
			   (szSchCd.equals("HAKD01LM"))||(szSchCd.equals("HAKD02LM"))||(szSchCd.equals("HAKD03LM"))||(szSchCd.equals("HAKD04LM"))||(szSchCd.equals("HAKD05LM"))||(szSchCd.equals("HAKE01UM"))||(szSchCd.equals("HAKD01UM"))||
			   //(szSchCd.equals("HBFD01LM"))||(szSchCd.equals("HBFD02LM"))||(szSchCd.equals("HBFD04LM"))||
	  	       (szSchCd.equals("JBKD01LM"))||//(szSchCd.equals("JBFD01LM"))||
	  	       (szSchCd.equals("JAKD01LM"))||
  		  	   //(szSchCd.equals("JBTC01MM"))||(szSchCd.equals("JBTC02MM"))||
  		  	   (szSchCd.equals("JBTC05MM"))||
  		  	   (szSchCd.equals("JATC05MM"))||
  		  	   (szSchCd.equals("HBKE03UM"))||(szSchCd.equals("HBKD03UM"))||
  		  	   (szSchCd.equals("HAKE03UM"))||(szSchCd.equals("HAKD03UM"))||(szSchCd.equals("HBKD01UM"))||
  		  	   //(szSchCd.equals("HBFE03UM"))||(szSchCd.equals("HBFD03UM"))||

  		  	   (szSchCd.equals("HCKD01LM"))||(szSchCd.equals("HCKD02LM"))||(szSchCd.equals("HCKD03LM"))||(szSchCd.equals("HCKD04LM"))||(szSchCd.equals("HCKD05LM"))||(szSchCd.equals("HCFE01UM"))||(szSchCd.equals("HCKD01UM"))||
	  	       (szSchCd.equals("HCFD01LM"))||(szSchCd.equals("HCFD02LM"))||(szSchCd.equals("HCFD04LM"))||
  		  	   (szSchCd.equals("JCKD01LM"))||(szSchCd.equals("JCFD01LM"))||(szSchCd.equals("HCKE01UM"))||
  		  	   (szSchCd.equals("JCTC01MM"))||(szSchCd.equals("JCTC02MM"))||(szSchCd.equals("JCTC05MM"))||
  		  	   (szSchCd.equals("HCKE03UM"))||(szSchCd.equals("HCKD03UM"))||
  		  	   (szSchCd.equals("HCFE03UM"))||(szSchCd.equals("HCFD03UM"))||
  		  	
	  	 	   (szSchCd.equals("HEDD01LM"))||(szSchCd.equals("HEDD02LM"))||(szSchCd.equals("HEDD03LM"))||(szSchCd.equals("HEDD04LM"))||(szSchCd.equals("HEDD05LM"))||(szSchCd.equals("HEDE01UM"))||(szSchCd.equals("HEDD01UM"))||
	  	 	   (szSchCd.equals("JEDD01LM"))||(szSchCd.equals("JETC01MM"))||(szSchCd.equals("JETC02MM"))||
	  	 	   (szSchCd.equals("HEDE03UM"))||(szSchCd.equals("HEDD03UM"))||

	  	 	   (szSchCd.equals("HGFD01LM"))||(szSchCd.equals("HGFD02LM"))||(szSchCd.equals("HGFD04LM"))||(szSchCd.equals("HGFE01UM"))||
			   (szSchCd.equals("JGFD01LM"))||(szSchCd.equals("JGTC01MM"))||(szSchCd.equals("JGTC02MM"))||
			   (szSchCd.equals("HGFE03UM"))||(szSchCd.equals("HGFD03UM"))||
			   
	  	 	   (szSchCd.equals("HHKD01LM"))||(szSchCd.equals("HHKD02LM"))||(szSchCd.equals("HHKD03LM"))||(szSchCd.equals("HHKD04LM"))||(szSchCd.equals("HHKD05LM"))||(szSchCd.equals("HHKE01UM"))||
	  	 	   (szSchCd.equals("JHKD01LM"))||(szSchCd.equals("JHTC01MM"))||(szSchCd.equals("JHTC02MM"))||(szSchCd.equals("HHKD01UM"))||
	  	 	   (szSchCd.equals("HHKE03UM"))||(szSchCd.equals("HHKD03UM"))||
//			   151209 hun 지포장 입고 가작업지시 추가	  	 	    
		  	 	(szSchCd.equals("HAKD06LM"))||
			    (szSchCd.equals("HBKD06LM"))||
			    (szSchCd.equals("HBFD06LM"))||
			    (szSchCd.equals("HCKD06LM"))||
			    (szSchCd.equals("HCFD06LM"))||
			    (szSchCd.equals("HDFD06LM"))||
			    (szSchCd.equals("HEDD06LM"))||
			    (szSchCd.equals("HFFD06LM"))||
			    (szSchCd.equals("HGFD06LM"))||
			    (szSchCd.equals("HHKD06LM"))||
 			   (szSchCd.substring(2, 4).equals("CV"))  ){ // CONV 입고
		
				outRecord1 = (JDTORecord)this.procY5CrnEqpLocDeciMain(inRecord);
			} else {	

				outRecord1 = (JDTORecord)this.procY5CrnStrLocDeciMain(inRecord);
			}
//SJH04001
			
			sRTN_CD			= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			sYD_CRN_SCH_ID 	= StringHelper.evl(outRecord1.getFieldString("YD_CRN_SCH_ID"), "");
			ydUtils.putLog(szSessionName, szMethodName, sRTN_CD + "/////" + sRTN_MSG, YdConstant.DEBUG);
	
			szMsg="[" + szOperationName + "] 크레인 TO 위치 결정 처리 완료: " + sRTN_CD +"/"+ sYD_CRN_SCH_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if(sRTN_CD.equals("0")){ 
				//throw new DAOException(szMsg);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", sRTN_MSG);
				m_ctx.setRollbackOnly();
				return outRecord;
			} else if (sRTN_CD.equals("1")){ 	

//크레인작업지시요구 판단
//L2 작업지시 송신			
				if (!(sYD_CRN_SCH_ID.equals(""))){ 
					//파라미터 레코드 setting
					recPara.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID);
					rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
					intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 0);
					
					if (intRtnVal < 1 ){
						szMsg="해당크레인 스케줄이 존재하지않습니다";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" , "0");	
						outRecord.setField("RTN_MSG", szMsg);
						return outRecord;
					}
					
					rsRtnVal.first();
					recCheck = rsRtnVal.getRecord();
					sHDONG_YN = ydDaoUtils.paraRecChkNull(recCheck, "HDONG_YN");
					sYD_WRK_PROG_STAT_CD = ydDaoUtils.paraRecChkNull(recCheck, "YD_WRK_PROG_STAT_CD");
					
					if("Y".equals(sHDONG_YN)){
						szMsg="[" + szOperationName + "]크레인 변경 작업 H1호기: " + sHDONG_YN;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						recPara 	= JDTORecordFactory.getInstance().create();
						recPara.setField("YD_SCH_CD", 		szSchCd);
						recPara.setField("YD_EQP_ID", 		sYD_EQP_ID);
						recPara.setField("YD_CRN_SCH_ID", 	sYD_CRN_SCH_ID);
						recPara.setField("YD_WRK_PROG_STAT_CD", 	sYD_WRK_PROG_STAT_CD);
	
						JDTORecord [] inRecord2 =  new JDTORecord[1];
						inRecord2[0]	= recPara;
	
						EJBConnector ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
						outRecord = (JDTORecord)ejbConn.trx("wrkCrnChange", new Class[] { JDTORecord[].class }, new Object[] { inRecord2 });
						
						sRTN_CD	    = StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
						
						ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.DEBUG);
						if(sRTN_CD.equals("0")){ 
							sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
							//throw new DAOException(szMsg);
							outRecord.setField("RTN_CD" , "0");	
							outRecord.setField("RTN_MSG", sRTN_MSG);	
							//m_ctx.setRollbackOnly();
							return outRecord;
						}	
					}else{
					
						szMsg="[" + szOperationName + "]L2 작업지시 송신  호출: " + sRTN_CD;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						outRecord2  = (JDTORecord)this.chkY5CrnWrkOrdReq(outRecord1);	
						sRTN_CD	    = StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
						
						ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.DEBUG);
						if(sRTN_CD.equals("0")){ 
							sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
							//throw new DAOException(szMsg);
							outRecord.setField("RTN_CD" , "0");	
							outRecord.setField("RTN_MSG", sRTN_MSG);	
							m_ctx.setRollbackOnly();
							return outRecord;
						}	
					}
					
				}	
				
			}	
			
			
		} catch (Exception e) {
			
			szMsg="[ERROR] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		
		}	// end try catch문
		
		return outRecord1;

	} // end of procY5CrnSchMainB()
	

    /**
	 * 오퍼레이션명 : C열연 크레인스케줄Main_Re (YDYDJ509)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * sYD_CRN_SCH_ID, 
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procY5CrnSchMainRe(JDTORecord msgRecord)throws JDTOException  {
	
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		YdEqpDao   ydEqpDao     = new YdEqpDao();
		
		JDTORecordSet outRecset    	= JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsWrkbookmtl 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecord outRecord  		= JDTORecordFactory.getInstance().create(); 
		JDTORecord outRecord1  		= JDTORecordFactory.getInstance().create(); 
		JDTORecord outRecord2  		= JDTORecordFactory.getInstance().create(); 
		 
		
		JDTORecord inRecord 	= null;
		String szMsg        	= "";
		String szMethodName 	= "procY5CrnSchMainRe";
		String szOperationName 	= "C열연 크레인스케줄MainRe";
		//설비Id
		String szEqpId      	= "";
		//스케줄코드
		String szSchCd      	= "";
		String szYD_WBOOK_ID 	= null;
		

		String sRTN_CD			= null;
		String sRTN_MSG			= null;
		String sYD_SCH_CD		= null;
		String sYD_CRN_SCH_ID	= null;
		String sRTN_SCH_ERR     = null;
		String sRTN_SCH_CD     	= null;
		

		sYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID");
		if(sYD_CRN_SCH_ID.equals("")){
			szMsg="[" + szOperationName + "] YD_CRN_SCH_ID Error";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			m_ctx.setRollbackOnly();
			return outRecord;
		}

		try{

			szMsg = szOperationName;
			ydUtils.putLog(szSessionName, szMethodName, "크레인리스케줄시작", YdConstant.DEBUG);
			
			szEqpId 		= ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID"); //크레인설비ID
			szSchCd 		= ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD"); //크레인스케줄코드
			szYD_WBOOK_ID 	= ydDaoUtils.paraRecChkNull(msgRecord,"YD_WBOOK_ID");

			szMsg="[" + szOperationName + "] 메소드 시작 파라미터 확인 : YD_EQP_ID : " + szEqpId + " , YD_SCH_CD : " + szSchCd + ", 작업예약ID["+szYD_WBOOK_ID+"], sYD_CRN_SCH_ID[" + sYD_CRN_SCH_ID+ "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
//크레인 스케줄 등록 ----------------->스케쥴 main 과 틀림
//적치단의 재료상태를 권상대기로 변경			
			outRecord1 = JDTORecordFactory.getInstance().create();
			
			outRecord1 = (JDTORecord)this.Y5CrnSchInsCoilRe(msgRecord);
			
			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			
			String sMSG_ID		= StringHelper.evl(outRecord1.getFieldString("MSG_ID"), "");
			String sYD_EQP_ID	= StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
			String sYD_WBOOK_ID	= StringHelper.evl(outRecord1.getFieldString("YD_WBOOK_ID"), "");
			ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.DEBUG);
			if(sRTN_CD.equals("0")){ 
				//throw new DAOException(szMsg);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				m_ctx.setRollbackOnly();
				return outRecord;
			}	

			szMsg="[" + szOperationName + "]TO저장위치 결정main 호출: " + sRTN_CD;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			
//c8			
//저장위치결정MAIN호출  : YDYDJ510, 설비ID, 작업예약ID
//크레인스케줄의 권상지시위치 등록한다.
//적치단에 저장
//크레인 스케줄 권하지시위치 등록		
			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("MSG_ID",      sMSG_ID);
			inRecord.setField("YD_EQP_ID",   sYD_EQP_ID);
			inRecord.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
			
			outRecord1 = JDTORecordFactory.getInstance().create();
			
			outRecord1 = (JDTORecord)this.procY5CrnStrLocDeciMain(inRecord);
			
			sRTN_CD			= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			sYD_CRN_SCH_ID 	= StringHelper.evl(outRecord1.getFieldString("YD_CRN_SCH_ID"), "");
			ydUtils.putLog(szSessionName, szMethodName, sRTN_CD + "/////" + sRTN_MSG, YdConstant.DEBUG);
			
			if(sRTN_CD.equals("0")){ 

				//throw new DAOException(szMsg);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", sRTN_MSG);
				m_ctx.setRollbackOnly();
				return outRecord;
			} else if (sRTN_CD.equals("1")){ 	

//크레인작업지시요구 판단
//L2 작업지시 송신
//설비를 명령선택 대기로 변경후 처리 함				
				szMsg="[" + szOperationName + "]sYD_CRN_SCH_ID: " + sYD_CRN_SCH_ID;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				if (!(sYD_CRN_SCH_ID.equals(""))){ 

					outRecord1 = JDTORecordFactory.getInstance().create();
					outRecord1.setField("YD_EQP_ID"		, szEqpId);
					outRecord1.setField("YD_EQP_STAT"	, "W");	//'1' 명령선택대기
					outRecord1.setField("YD_CRN_SCH_ID"	, sYD_CRN_SCH_ID);	 
            		int intRtnVal = ydEqpDao.updYdEqp(outRecord1, 0);
        			if(intRtnVal <= 0) {
        				outRecord.setField("RTN_CD" , "0");	
        				outRecord.setField("RTN_MSG", sRTN_MSG);
        				m_ctx.setRollbackOnly();
        				return outRecord;
    	    		}					
					szMsg="[" + szOperationName + "]크레인작업지시요구 Method 호출: " + sYD_CRN_SCH_ID+"-"+sRTN_CD;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					outRecord2 	= (JDTORecord)this.chkY5CrnWrkOrdReq(outRecord1);	
					sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
					sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
	
					ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.DEBUG);
					if(sRTN_CD.equals("0")){ 
						//throw new DAOException(szMsg);
						outRecord.setField("RTN_CD" , "0");	
						outRecord.setField("RTN_MSG", sRTN_MSG);	
						m_ctx.setRollbackOnly();
						return outRecord;
					}
					
				}
			}
//			} else {
//					
//				//to 위치 못잡더라도 강제로 정상처리 함
//				outRecord.setField("RTN_CD" , "1");	
//				outRecord.setField("RTN_MSG", sRTN_MSG);	
//				return outRecord;
//					
//			}	
//			
			
		} catch (Exception e) {
			
			szMsg="[ERROR] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		
		}	// end try catch문
		
		szMsg="C열연크레인 재스케줄수신("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		outRecord.setField("RTN_MSG"	, sRTN_MSG);	
		outRecord.setField("RTN_CD" 	, "1");	
		return outRecord;

	} // end of procY5CrnSchMainRe()
	

	
	
	
	
	
/*********************************************************************************************
* 야드 작업 이력 생성  
**********************************************************************************************/
	/**
	 * 오퍼레이션명 : 차량작업이력Update
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return void
	 * @throws JDTOException
	 */
	
	public void procCarWorkHistoryUpdate(JDTORecord msgRecord)throws JDTOException  {
		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();
		
		int intRtnVal;
		String szMsg        = "";
		String szMethodName = "procCarWorkHistoryUpdate";
		String szOperationName = "차량작업이력Update";
		
		String szCarSchId = "";     // 차량스케줄ID
		//String szStlNo = "";
		
		JDTORecordSet rsCarSch = null;
		JDTORecord    recCarSch = null;
		
		
		try {
			szMsg="차량작업이력생성("+szMethodName+") 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szCarSchId     = ydDaoUtils.paraRecChkNull(msgRecord,"YD_CAR_SCH_ID");
			
			rsCarSch  = JDTORecordFactory.getInstance().createRecordSet("Temp");
			JDTORecord recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID", szCarSchId);
			
			//차량스케줄 작업이력을 읽어온다
			intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, rsCarSch, 6);
			
			if(intRtnVal<=0){
				if(intRtnVal == 0) {
					szMsg = "차량스케줄ID(" + szCarSchId + ")에 대한 데이터가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(szMethodName + szMsg);
				}else if(intRtnVal == -2) {
					szMsg = "차량스케줄ID(" + szCarSchId + ")로 조회중 parameter error 발생!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(szMethodName + szMsg);
				}
			}
			
			rsCarSch.absolute(1);
			recCarSch = rsCarSch.getRecord();
			recCarSch.setField("UP_YD_CAR_SCH_ID", szCarSchId);
						
			ydUtils.displayRecord(szOperationName, recCarSch);
			
			//크레인스케줄ID와 재료번호를 키로 이력테이블에 UPDATE
			intRtnVal = ydWrkHistDao.updYdWrkHistByIdNo(recCarSch, 4);
			
			if(intRtnVal<=0) {
				szMsg = "차량스케줄(" + szCarSchId + ")에 대한 UPDATE가 실패하였습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMethodName + szMsg);
			}
			
			
			
		}  catch (Exception e) {
			
			szMsg="[ERROR] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMethodName + szMsg); 
			
		}	// end try catch문

			
		szMsg="차량작업이력생성("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return;
		
	}
	
	 /**
	 * 오퍼레이션명 : 야드작업이력생성Main
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return void
	 * @throws JDTOException
	 */

	public void procWorkHistoryCreate(JDTORecord msgRecord)throws JDTOException  {
		
		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		int intRtnVal;
		String szMsg        = "";
		String szMethodName = "procWorkHistoryCreate";
		String szOperationName = "야드작업이력생성Main";
		//String szOperationName = "야드작업이력생성Main";
		
		String szCrnSchId = "";     // 크레인스케줄ID
		String szCarSchId = "";     // 차량스케줄ID
		String szTcarSchId = "";    // 대차스케줄ID
		String szWtclTnkSchId = ""; // 수냉탱크스케줄ID
		//String szWbookId = "";      // 작업예약ID
		String szStlNo = "";
		String szYdMtlItem = "";
		
		String szYD_STK_LYR_NO		= null;
		int intLYR_NO				= 0;
		
		JDTORecordSet rsResult = null;
		
		JDTORecordSet rsCrnStock = null;
		JDTORecordSet rsCarSch   = null;
		JDTORecordSet rsTcarSch  = null;
		JDTORecordSet rsTnkSch   = null;
		
		JDTORecord recCrnStock = null;
		JDTORecord recCarSch   = null;
		JDTORecord recTcarSch  = null;
		JDTORecord recTnkSch   = null;
		
		JDTORecord    recMtl = null;
						
		/*
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode.equals("")){
			szMsg="[" + szOperationName + "] TC Code Error";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			return;
		}
		if(bDebugFlag){
			szMsg="[" + szOperationName + "] [DEBUG] 전문수신 : TCCODE=" +szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		*/
		
		try {
			szMsg="야드작업이력생성("+szMethodName+") 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, msgRecord);
			
			szCrnSchId     = ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID");
			szCarSchId     = ydDaoUtils.paraRecChkNull(msgRecord,"YD_CAR_SCH_ID");
			szTcarSchId    = ydDaoUtils.paraRecChkNull(msgRecord,"YD_TCAR_SCH_ID");
			szWtclTnkSchId = ydDaoUtils.paraRecChkNull(msgRecord,"YD_WTCL_TNK_SCH_ID");
			//szWbookId      = ydDaoUtils.paraRecChkNull(msgRecord,"YD_WBOOK_ID");
			
			
			//레코드 생성
			JDTORecord recPara  = JDTORecordFactory.getInstance().create();
			//크레인스케줄ID
			recPara.setField("YD_CRN_SCH_ID", szCrnSchId);
			
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			// 크레인스케줄의 작업재료들을 읽어온다.
			intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, rsResult, 1);
			//intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, rsResult, 4);
			
			if(intRtnVal<=0){
	  			if(intRtnVal == 0) {
	  				szMsg = "크레인스케줄ID(" + szCrnSchId + ")에 대한 재료데이터가 없습니다.";
	  				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	  				return;
	  			}else if(intRtnVal == -2) {
	  				szMsg = "크레인스케줄ID(" + szCrnSchId + ")로 조회중 parameter error 발생!";
	  				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	  				return;
	  			}
			}
			
			// 작업재료 수만큼 루프
			for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				rsResult.absolute(Loop_i);
				recMtl = rsResult.getRecord();
				
				//작업재료번호와 재료유형을 읽어온다
				//B:주편, C:코일, P:후판, S:슬라브 
				
				ydUtils.displayRecord(szOperationName, recMtl);
				
				szStlNo = ydDaoUtils.paraRecChkNull(recMtl,"STL_NO");
				szYdMtlItem = ydDaoUtils.paraRecChkNull(recMtl,"YD_MTL_ITEM");
				
				
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", szCrnSchId);
				recPara.setField("STL_NO", szStlNo);
				
				rsCrnStock  = JDTORecordFactory.getInstance().createRecordSet("Temp");
				
				// 재료유형에 따라 공통테이블, 저장품테이블, 크레인작업내역들을 읽어온다.
				if (szYdMtlItem.equals("B")) {
					// 주편일때...
					intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, rsCrnStock, 2);
				}else if (szYdMtlItem.equals("C")) {
					// 코일일때...
					intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, rsCrnStock, 3);
				}else if (szYdMtlItem.equals("P")) {
					// 후판일때...
					intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, rsCrnStock, 4);
				}else if (szYdMtlItem.equals("S")) {
					// 슬라브일때...
					intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, rsCrnStock, 5);
				}// end if
				
				if(intRtnVal<=0){
					if(intRtnVal == 0) {
						szMsg = "재료번호(" + szStlNo + ")에 대한 데이터가 없습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return;
					}else if(intRtnVal == -2) {
						szMsg = "재료번호(" + szStlNo + ")로 조회중 parameter error 발생!";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return;
					} //end if
				}//end if
				
				rsCrnStock.absolute(1);
				recCrnStock = rsCrnStock.getRecord();
				
				
				/*********************************************************************************/
				// 2010.01.25 석창화 - 대차스케줄이 존재하면 수불구분이 모두 이적(M)으로 설정한다.
				// 최종락 이사님 지시에 의해서 수정...
				if (!szTcarSchId.equals("") ) {
					recCrnStock.setField("YD_GNT_GP", "M");
				}
				/*********************************************************************************/
				
				szYD_STK_LYR_NO			= ydDaoUtils.paraRecChkNull(recCrnStock, "YD_STK_LYR_NO");
				
				intLYR_NO = Integer.parseInt(szYD_STK_LYR_NO) - 1;
				
				//------------------------------------------------------------------------------------
				//	권상지시단, 권상실적단, 권하지시단, 권하실적단 정보를 각 재료별로 증가시킴
				//	수정자 : 임춘수
				//	수정일 : 2010.01.05
				//------------------------------------------------------------------------------------
				recCrnStock.setField("YD_UP_WO_LAYER", ydDaoUtils.stringPlusInt(recCrnStock.getFieldString("YD_UP_WO_LAYER"), intLYR_NO));
				recCrnStock.setField("YD_UP_WR_LAYER", ydDaoUtils.stringPlusInt(recCrnStock.getFieldString("YD_UP_WR_LAYER"), intLYR_NO));
				recCrnStock.setField("YD_DN_WO_LAYER", ydDaoUtils.stringPlusInt(recCrnStock.getFieldString("YD_DN_WO_LAYER"), intLYR_NO));
				recCrnStock.setField("YD_DN_WR_LAYER", ydDaoUtils.stringPlusInt(recCrnStock.getFieldString("YD_DN_WR_LAYER"), intLYR_NO));
				//------------------------------------------------------------------------------------
				
				// 이력테이블에 INSERT
				intRtnVal = ydWrkHistDao.insYdWrkHistYD(recCrnStock);
				
				if(intRtnVal<=0) {
					szMsg = "재료번호(" + szStlNo + ")에 대한 INSERT가 실패하였습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
				
				//2010.03.19 이현성 - 실시간 모니터링 반영
				ydUtils.putYdFlexCrnWrk("", recCrnStock);
				
				//차량작업,대차작업,수냉탱크작업이력을 읽어와 이력테이블에 UPDATE한다
				if(!szCarSchId.equals("") ) {
					// 차량스케줄ID
					rsCarSch  = JDTORecordFactory.getInstance().createRecordSet("Temp");
					recPara  = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CAR_SCH_ID", szCarSchId);
					
					//차량스케줄 작업이력을 읽어온다
					intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, rsCarSch, 6);
					
					if(intRtnVal<=0){
						if(intRtnVal == 0) {
							szMsg = "차량스케줄ID(" + szCarSchId + ")에 대한 데이터가 없습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							return;
						}else if(intRtnVal == -2) {
							szMsg = "차량스케줄ID(" + szCarSchId + ")로 조회중 parameter error 발생!";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							return;
						}
					}
					
					rsCarSch.absolute(1);
					recCarSch = rsCarSch.getRecord();
					recCarSch.setField("YD_CRN_SCH_ID", szCrnSchId);
					recCarSch.setField("STL_NO", szStlNo);
					
					ydUtils.displayRecord(szOperationName, recCarSch);
					
					//크레인스케줄ID와 재료번호를 키로 이력테이블에 UPDATE
					intRtnVal = ydWrkHistDao.updYdWrkHistByIdNo(recCarSch, 1);
					
					if(intRtnVal<=0) {
						szMsg = "재료번호(" + szStlNo + ")에 대한 UPDATE가 실패하였습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return;
					}
						
					
				
				
				}else if (!szTcarSchId.equals("") ) {
					// 대차스케줄ID
					rsTcarSch  = JDTORecordFactory.getInstance().createRecordSet("Temp");
					recPara  = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_TCAR_SCH_ID", szTcarSchId);
					
					//대차스케줄 작업이력을 읽어온다
					intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, rsTcarSch, 7);
					
					if(intRtnVal<=0){
			  			if(intRtnVal == 0) {
			  				szMsg = "대차스케줄ID(" + szTcarSchId + ")에 대한 데이터가 없습니다.";
			  				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			  				return;
			  			}else if(intRtnVal == -2) {
			  				szMsg = "대차스케줄ID(" + szTcarSchId + ")로 조회중 parameter error 발생!";
			  				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			  				return;
			  			}
					}
					
					//szMsg = "대차스케줄ID(" + szTcarSchId + ")시작.";
	  				//ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					rsTcarSch.absolute(1);
					recTcarSch = rsTcarSch.getRecord();
					recTcarSch.setField("YD_CRN_SCH_ID", szCrnSchId);
					recTcarSch.setField("STL_NO", szStlNo);
					
					
					
					
					//szMsg = "대차스케줄ID(" + szTcarSchId + ")종료.";
	  				//ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	  				
	  				//ydUtils.displayRecord(szOperationName, recTcarSch);
					
	  			    //크레인스케줄ID와 재료번호를 키로 이력테이블에 UPDATE
					intRtnVal = ydWrkHistDao.updYdWrkHistByIdNo(recTcarSch, 2);
					
					//szMsg = "대차스케줄UPDATE(" + szTcarSchId + ")완료.";
	  				//ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					if(intRtnVal<=0) {
						szMsg = "재료번호(" + szStlNo + ")에 대한 UPDATE가 실패하였습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return;
					}
					
				}else if (!szWtclTnkSchId.equals("") ) {
					// 탱크스케줄ID
					rsTnkSch  = JDTORecordFactory.getInstance().createRecordSet("Temp");
					recPara  = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WTCL_TNK_SCH_ID", szWtclTnkSchId);
					
					//수냉탱크 작업이력을 읽어온다
					intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, rsTnkSch, 8);
					
					if(intRtnVal<=0){
			  			if(intRtnVal == 0) {
			  				szMsg = "수냉탱크스케줄ID(" + szWtclTnkSchId + ")에 대한 데이터가 없습니다.";
			  				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			  				return;
			  			}else if(intRtnVal == -2) {
			  				szMsg = "수냉탱크스케줄ID(" + szWtclTnkSchId + ")로 조회중 parameter error 발생!";
			  				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			  				return;
			  			}
					}
					
					rsTnkSch.absolute(1);
					recTnkSch = rsTnkSch.getRecord();
					recTnkSch.setField("YD_CRN_SCH_ID", szCrnSchId);
					recTnkSch.setField("STL_NO", szStlNo);
					
					//크레인스케줄ID와 재료번호를 키로 이력테이블에 UPDATE
					intRtnVal = ydWrkHistDao.updYdWrkHistByIdNo(recTnkSch, 3);
					
					if(intRtnVal<=0) {
						szMsg = "재료번호(" + szStlNo + ")에 대한 UPDATE가 실패하였습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return;
					}
				}	
				
				
				
			} // end of for
				
				
			
					
		}  catch (Exception e) {
				
			szMsg="[ERROR] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			
		}	// end try catch문

			
		szMsg="야드작업이력생성("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return;
		

	}

    
	   /**
	 * C열연 일반저장결정Main(H/J)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public JDTORecord procY5CrnStrLocDeciMain(JDTORecord inRecord)throws JDTOException  {

		//
		// C열연저장위치등록Main
		// TC : YDYDJ509
		// 야드작업자로부터 저장위치등록수신
		//
		//┏━┓
		//┃ 야드작업자로부터 저장위치 등록을 수신하면 저장위치를 검색하여 등록한다.
		//┗━┛
		
		//크레인작업재료의 최하단 재료정보, 크레인작업재료의 총매수 중량 높이, 크레인스케줄의 야드구분 동구분 스케줄코드, 저장집합코드
		JDTORecord outRecord 		= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1 		= JDTORecordFactory.getInstance().create();
		
		String szMethodName = "procY5CrnStrLocDeciMain";
		String szMsg 		= "";
		//작업예약Id
		String szWbookId 	= "";
		//설비Id
		String szEqpId 		= "";
		String sRTN_MSG		= "";
		String sRTN_CD		= "";
		int intRtnVal 		= 0;

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode = ydUtils.getTcCode(inRecord);
        if(szRcvTcCode == null){
        	szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;
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
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
			}
			
			//1. 위치검색범위 조회 Data Setting
			//스케쥴 기동처리 하기 위해 RETURN 된 값을 다시 하위로 보냄 (중요)
			outRecord1 = (JDTORecord)this.Y5LocSrcRngDataSetCoil(inRecord) ;
			
			sRTN_CD	 	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			ydUtils.putLog(szSessionName, szMethodName, "1" +sRTN_CD + sRTN_MSG, YdConstant.DEBUG);
			if ("0".equals(sRTN_CD)) {
				outRecord1.setField("RTN_CD" , "0");	
				outRecord1.setField("RTN_MSG", sRTN_MSG);	
				return outRecord1;
			}
			
		}catch(Exception e){

			szMsg="Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;
//			return;
		}

		szMsg="저장위치  등록("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		outRecord1.setField("RTN_CD" , sRTN_CD);
		outRecord1.setField("RTN_MSG", sRTN_MSG);	
		return outRecord1;


	} //end of procY5CrnStrLocDeciMain()
    

/**
	 * C열연 설비저장결정Main(H/J)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public JDTORecord procY5CrnEqpLocDeciMain(JDTORecord inRecord)throws JDTOException  {

		//
		// C열연저장위치등록Main
		// TC : YDYDJ509
		// 야드작업자로부터 저장위치등록수신
		// 야드작업자로부터 저장위치 등록을 수신하면 저장위치를 검색하여 등록한다.
		// 크레인작업재료의 최하단 재료정보, 크레인작업재료의 총매수 중량 높이, 크레인스케줄의 야드구분 동구분 스케줄코드, 저장집합코드
		
		JDTORecord outRecord 	= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1 	= JDTORecordFactory.getInstance().create();
		
		String szMethodName 	= "procY5CrnEqpLocDeciMain";
		String szMsg 			= "";
		//작업예약Id
		String szWbookId 		= "";
		//설비Id
		String szEqpId 			= "";
		String sRTN_MSG			= "";
		String sRTN_CD			= "";
		int intRtnVal 			= 0;

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode = ydUtils.getTcCode(inRecord);
        if(szRcvTcCode == null){
        	szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;
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
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
			}
			
			//1. 위치검색범위 조회 Data Setting
			//스케쥴 기동처리 하기 위해 RETURN 된 값을 다시 하위로 보냄 (중요)
			outRecord1 = (JDTORecord)this.Y5EqpLocSrcRngDataSetCoil(inRecord) ;
			
			sRTN_CD	 	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			ydUtils.putLog(szSessionName, szMethodName, "1" +sRTN_CD + sRTN_MSG, YdConstant.DEBUG);
			if ("0".equals(sRTN_CD)) {
				outRecord1.setField("RTN_CD" , "0");	
				outRecord1.setField("RTN_MSG", sRTN_MSG);	
				return outRecord1;
			}
			
		}catch(Exception e){

			szMsg="Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;
		}

		szMsg="저장위치  등록("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		outRecord1.setField("RTN_CD" , sRTN_CD);
		outRecord1.setField("RTN_MSG", sRTN_MSG);	
		return outRecord1;


	} //end of procY5CrnStrLocDeciMain()
    

	
	
	
	
	
    /**
     * 오퍼레이션명 : 위치검색범위 조회 DataSet(TO위치 결정)(H/J)
     *  
     * @param  inRecord
     * @return intRtnVal
     * @throws 
     */
    public JDTORecord Y5LocSrcRngDataSetCoil (JDTORecord inRecord) throws JDTOException {
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
		//┗━┛
	
    	JDTORecord 		outRecord 	= JDTORecordFactory.getInstance().create();
    	JDTORecord 		outRecord1 	= JDTORecordFactory.getInstance().create();
    	YdCrnSchDao   	ydCrnSchDao 	= new YdCrnSchDao();
    	YdCrnWrkMtlDao 	ydCrnWrkMtlDao 	= new YdCrnWrkMtlDao();
    	YdWrkbookDao 	ydWrkbookDao	= new YdWrkbookDao();
    	YdUtils       	ydUtils 		= new YdUtils();

    	JDTORecordSet rsCrnsch    		= JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet rsCrnwrkmtl 		= JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet rsTemp         	= null;
    	
    	JDTORecord recCrnSch      = null;
    	JDTORecord recInTemp      = null;
 
    	String szMethodName = "Y5LocSrcRngDataSetCoil";
    	String szMsg        = "";     	  
    	String szOperationName = "위치검색범위 조회 DataSet";
    	String szCrnSchId = "";
    	String szSchCd    = "";
    	String szYdGp     = "";
    	String szYdBayGp  = "";
    	String szToLocDcsnMtd = "";
    	String szWbookToLocDcsnMtd = "";
    	
		//작업예약Id
		String szWbookId  = "";
		//설비Id
		String szEqpId    = "";
		String sRTN_MSG	  = "";
		String sRTN_CD    = "";
		String sYD_TO_LOC_GUIDE = "";
		
		
    	int intRtnVal = 0 ;
    	
        try{

			//파라미터 Null Check
			szWbookId = ydDaoUtils.paraRecChkNull(inRecord, "YD_WBOOK_ID");
			szEqpId   = ydDaoUtils.paraRecChkNull(inRecord, "YD_EQP_ID");

			//작업예약을 조회한다. To위치 결정방법이  사용자 지정인지 알기위해서...
			rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydWrkbookDao.getYdWrkbook(inRecord, rsTemp, 0);
			if(intRtnVal <= 0) {
				szMsg = "Y5LocSrcRngDataSet 작업예약 조회 중 Error : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			rsTemp.absolute(1);
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setRecord(rsTemp.getRecord());
			szWbookToLocDcsnMtd = ydDaoUtils.paraRecChkNull(recInTemp, "YD_TO_LOC_DCSN_MTD");
			
			
			ydUtils.putLog(szSessionName, szMethodName, "◆◆◆◆◆ TO위치 검색 대상 리스트 조회  START  ◆◆◆◆◆", YdConstant.INFO);
			
			rsCrnsch = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setField("YD_WBOOK_ID", szWbookId);
			recInPara.setField("YD_EQP_ID",   szEqpId);
			
			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschByEqpIdandWBookId*/
			intRtnVal = ydCrnSchDao.getYdCrnsch(recInPara, rsCrnsch, 21);		
			
			
			szMsg = "작업예약 ID로 크레인스케줄 조회 스케줄의 횟수 : " + rsCrnsch.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.putLog(szSessionName, szMethodName, "◆◆◆◆◆ TO위치 검색 대상 리스트 조회  END  ◆◆◆◆◆", YdConstant.INFO);
					
			
			for(int Loop_i = 1; Loop_i <= rsCrnsch.size(); Loop_i++) {
        		
    			szMsg = "◆◆◆◆◆ "+Loop_i+"번째 크레인 스케줄 ◆◆◆◆◆";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		
        		rsCrnsch.absolute(Loop_i);
        		recCrnSch  = rsCrnsch.getRecord();
        		ydUtils.displayRecord(szOperationName, recCrnSch);
        		
        		//크레인스케줄Data저장
        		szCrnSchId     = recCrnSch.getFieldString("YD_CRN_SCH_ID");
        		szSchCd        = recCrnSch.getFieldString("YD_SCH_CD");
        		szYdGp         = recCrnSch.getFieldString("YD_GP");
        		szYdBayGp  	   = recCrnSch.getFieldString("YD_BAY_GP");
        		szToLocDcsnMtd = recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD");
        		
        		//----------------------------------------------------------------------------------------------------------
        		//	대차상차스케줄이면 작업예약에 야드작업계획대차가 등록이 되어 있고
        		//	야드To위치결정방법이 F로 설정이 되어 있더라도
        		//	야드To위치Guide를 사용하지 않는다.
        		//----------------------------------------------------------------------------------------------------------
        		
        		if( !( szSchCd.substring(2, 4).equals("TC") && szSchCd.substring(6, 7).equals("U") ) ) {
	        		//20090917 김진욱 : 크레인 스케줄의 TO위치 결정방법이 S(최종위치)가는 작업이고  작업예약에 권상모음순서가 F라면...업데이트한다.
	        		if(Loop_i == rsCrnsch.size() && szWbookToLocDcsnMtd.equals("F")) {
	        			szToLocDcsnMtd = "F";
	        		}
        		}
        		//----------------------------------------------------------------------------------------------------------
        		//크레인스케줄의야드To위치결정방법 S 이면서 작업예약의 야드To위치결정방법이 F라면 야드To위치결정방법을 F로 처리한다
        		//----------------------------------------------------------------------------------------------------------
        		
        		if(szToLocDcsnMtd.equals("S") && szWbookToLocDcsnMtd.equals("F")) {
        			szToLocDcsnMtd = "F";
        		}
        		
        		//크레인작업재료조회(쿼리등록 완료 : 수정요청 항목이 추가됨)
				rsCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
        		JDTORecord recInData = JDTORecordFactory.getInstance().create();
        		
        		
        		
        		recInData.setField("YD_CRN_SCH_ID", szCrnSchId);
        		/*com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlBySchId*/
        		intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recInData, rsCrnwrkmtl, 6);
            	
        		//보조작업인 경우
            	if(szToLocDcsnMtd.equals("W")) {
            		
//////////////////////////////////////////////////            		
            		outRecord1 = (JDTORecord)this.Y5GetAidWrkLocCoil(rsCrnwrkmtl, recCrnSch);
            		
            		sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
    				if(sRTN_MSG.equals("")){
    					sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
    				}
    				if ("0".equals(sRTN_CD)) {

    					szMsg = "Y5GetAidWrkLocCoil : 보조작업 스케줄 실패!!";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        				outRecord.setField("RTN_CD" , "0");	
    					outRecord.setField("RTN_MSG", sRTN_MSG);	
    					return outRecord;
    				}
            	}
   
            	//주작업이면서 보조작업처리를 할  경우
            	if(szToLocDcsnMtd.equals("M")) {
          		
        			szMsg = "주작업이면서 보조작업처리 스케줄입니다.";
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            		//임의의 위치에 값을 저장한다.
//////////////////////////////////////////////   
        			outRecord1 = (JDTORecord)this.Y5GetMainWrkLocCoil(rsCrnwrkmtl, recCrnSch);
        			
              	 	sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
    			
              	 	if(sRTN_MSG.equals("")){
    					sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
    				}
    				if ("0".equals(sRTN_CD)) {
    					szMsg = "Y5GetAidWrkLocCoil : 주작업이면서 보조작업처리 스케줄 실패!!";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        				outRecord.setField("RTN_CD" , "0");	
    					outRecord.setField("RTN_MSG", sRTN_MSG);	
    					return outRecord;
    				
    				}
            	}

            	//완성된 주작업 인 경우
            	if(szToLocDcsnMtd.equals("R") || szToLocDcsnMtd.equals("B") || szToLocDcsnMtd.equals("S")) {
            		
        			//차량상차 To위치 결정
        			if((szSchCd.substring(2,4).equals("PT") ||szSchCd.substring(2,4).equals("TT") || szSchCd.substring(2,4).equals("TR")) && szSchCd.substring(6,7).equals("U")
//        			    || ((szSchCd.substring(2, 8).equals("TR07MM")||  szSchCd.substring(2, 8).equals("TR08MM")
//        			       || szSchCd.substring(2, 8).equals("TR57MM")|| szSchCd.substring(2, 8).equals("TR58MM")) && "J".equals(szSchCd.substring(0, 1)) ) 
        					|| ((("TR0".equals(szSchCd.substring(2 , 5)) || ("TR5".equals(szSchCd.substring(2 , 5)))) && "MM".equals(szSchCd.substring(6 , 8)) ) && "J".equals(szSchCd.substring(0, 1)) ) 
        			){
////////////////////////////////////////////////           				
        				outRecord1 = (JDTORecord)this.Y5GetCarLdBedToLocToLoc(rsCrnwrkmtl, recCrnSch);
        				
                 	 	sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
        				if(sRTN_MSG.equals("")){
        					sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
        				}
        				if ("0".equals(sRTN_CD)) {

        					szMsg = "[Y5GetCarLdBedToLocToLoc] : 차량상차 Bed To위치 결정하는 스케줄 실패!!";
    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            				outRecord.setField("RTN_CD" , "0");	
        					outRecord.setField("RTN_MSG", sRTN_MSG);	
        					return outRecord;
        				
        				}
  	        			szMsg = "차량상차 To위치 결정 모듈 호출 성공 - 스케줄코드["+szSchCd+"]";
            			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        			}
        			
//위치검색범위및위치검색Bed조회
        			else {
	            		
	    				szMsg = "일반야드 TO위치 결정 모듈 호출 시작 - 스케줄코드["+szSchCd+"]";
	        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        			
	        			if( Loop_i == rsCrnsch.size() ) {
	        				recCrnSch.setField("LAST_CRN_SCH_YN", "Y");
	        			}else{
	        				recCrnSch.setField("LAST_CRN_SCH_YN", "N");
	        			}
	        			//------------------------------------------------------------------------------
///sjh//////////////////////////////////////////     	        			
        				outRecord1 = (JDTORecord)this.Y5GetLocSrchRngCoil(rsCrnwrkmtl, recCrnSch, recInTemp);
        				
        				sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
        				
        				if ("-1".equals(sRTN_CD) && "CV02LM".equals(szSchCd.substring(2 , 8))) {
        					//공냉재 수입 인 경우 공냉장 to위치가 없으면 일반 수입야드로 적치함.
        					
        					szMsg = "공냉재 수입 인 경우 공냉장 to위치가 없으면 일반 수입야드로 적치함 - 스케줄코드["+szSchCd+"]";
                			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                			
        					recCrnSch.setField("YD_SCH_CD", szSchCd.substring(0 , 5)+"1LM");
        					outRecord1 = (JDTORecord)this.Y5GetLocSrchRngCoil(rsCrnwrkmtl, recCrnSch, recInTemp);
            				
            				sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
        				}
        				
        				
        				if(sRTN_MSG.equals("")){
        					sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
        				}
        				ydUtils.putLog(szSessionName, szMethodName, "2" +sRTN_CD +sRTN_MSG, YdConstant.DEBUG);
                	 	if ("0".equals(sRTN_CD)) {
              	 		
                	 		szMsg = "[Y5GetLocSrchRngCoil] : 최종위치를 결정하는 스케줄 실패!!";
    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            				outRecord.setField("RTN_CD" , "0");	
        					outRecord.setField("RTN_MSG", sRTN_MSG);	
        					return outRecord;
        				
        				}
 
    					szMsg = "일반야드 TO위치 결정 모듈 호출 성공 - 스케줄코드["+szSchCd+"]";
            			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        		}

            	}        	        	
            	
            	//사용자지정위치 인 경우(열까지 지정된것은 따로처리함:송)
            	if(szToLocDcsnMtd.equals("F")){
            		sYD_TO_LOC_GUIDE = ydDaoUtils.paraRecChkNull(recInTemp, "YD_TO_LOC_GUIDE"); 
           			
       				szMsg = "지정된 일반야드 TO위치 결정 모듈 호출 시작 - 스케줄코드["+szSchCd+"]";
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        			if( Loop_i == rsCrnsch.size() ) {
        				recCrnSch.setField("LAST_CRN_SCH_YN", "Y");
        			}else{
        				recCrnSch.setField("LAST_CRN_SCH_YN", "N");
        			}
        			//------------------------------------------------------------------------------
        			
        			outRecord1 = (JDTORecord)this.Y5GetLocSrchRngCoilUser(rsCrnwrkmtl, recCrnSch, recInTemp);

        			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//KKK    			
               	 	ydUtils.putLog(szSessionName, szMethodName, "2" +sRTN_CD +sRTN_MSG, YdConstant.DEBUG);	
               	 	if(sRTN_MSG.equals("")){
    					sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
    				}
    				if ("0".equals(sRTN_CD)) {
    					
    					szMsg = "[Y5GetLocSrchRngCoil] : 최종위치를 결정하는 스케줄 실패!!";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        				outRecord.setField("RTN_CD" , "0");	
    					outRecord.setField("RTN_MSG", sRTN_MSG);	
    					return outRecord;
    				
    				}
        			szMsg = "지정된일반야드 TO위치 결정 모듈 호출 성공 - 스케줄코드["+szSchCd+"]";
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		}

            	
            }//end of for   
 	
			
			
////////////////////////////////////////////
//	스케줄 좌표값 셋팅 작업 
////////////////////////////////////////////	
			JDTORecordSet rsResultCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recStkBed    = null;
			JDTORecord recGetRsSet	= null;
		
               //권하위치
        	recInTemp = JDTORecordFactory.getInstance().create();
        	recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
        	/*com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlByCrnSchIDOrdStkLyrNoUp*/
        	intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recInTemp, rsResultCrnwrkmtl, 306);
  				
        	//rsBed.beforeFirst();
        	szMsg = "스케줄 좌표값 셋팅 작업"+rsResultCrnwrkmtl.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
        	//적치베드단위로 Loop
        	for(int Loop_i = 1; Loop_i <= rsResultCrnwrkmtl.size(); Loop_i++) {
        		rsResultCrnwrkmtl.absolute(Loop_i);
        		recStkBed = JDTORecordFactory.getInstance().create();
        		recStkBed.setRecord(rsResultCrnwrkmtl.getRecord());
        		
        		
        		//적치단정보 Setting
            	recGetRsSet = JDTORecordFactory.getInstance().create();
            	recGetRsSet.setField("YD_STK_COL_GP", recStkBed.getFieldString("YD_STK_COL_GP"));
            	recGetRsSet.setField("YD_STK_BED_NO", recStkBed.getFieldString("YD_STK_BED_NO"));
            	recGetRsSet.setField("YD_STK_LYR_NO", recStkBed.getFieldString("YD_STK_LYR_NO"));
            	
	        	//크레인 작업 지시 좌표값 설정 작업      	        	
	        	outRecord1 = (JDTORecord)this.Y5UpdGradStkLyr(recStkBed, recGetRsSet);
				sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
         	 	if(sRTN_MSG.equals("")){
					sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				}

				if ("0".equals(sRTN_CD)) {
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", sRTN_MSG);	
					return outRecord;
				}
        	}
        	
        	
        	
////////////////////////////////////////////            	
//to위치 결정 실패시에 default값으로 xx010101을 설정
////////////////////////////////////////////
            	
			rsCrnsch = JDTORecordFactory.getInstance().createRecordSet("");
			recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setField("YD_WBOOK_ID", szWbookId);
			recInPara.setField("YD_EQP_ID",   szEqpId);
			//com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschByEqpIdandWBookId
			intRtnVal = ydCrnSchDao.getYdCrnsch(recInPara, rsCrnsch, 21);
			if(intRtnVal <= 0) {
				szMsg = "해당 작업예약 크레인스케줄 조회 실패!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
    		}
			
    		//20090821 김진욱 to위치 결정 실패시에 default값으로 xx010101을 설정
			for(int Loop = 1; Loop <= rsCrnsch.size(); Loop++) {
				rsCrnsch.absolute(Loop);
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setRecord(rsCrnsch.getRecord());
				if(ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_WO_LOC").equals("")) {
					recInPara.setField("YD_DN_WO_LOC", "XX010101");
					intRtnVal = ydCrnSchDao.updYdCrnsch(recInPara, 0);
					if(intRtnVal <= 0){
	    				szMsg = "Y5LocSrcRngDataSetCoil 크레인스케줄 To위치 Default값 등록 실패!!";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    				outRecord.setField("RTN_CD" , "0");	
	    				outRecord.setField("RTN_MSG", szMsg);	
	    				return outRecord;
					}
				}
			}
			
			
			
			rsCrnsch.absolute(1);
			recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setRecord(rsCrnsch.getRecord());
			
			//첫번째 크레인작업이 TO위치가 결정이 되었다면...크레인작업지시 송신
			inRecord.setField("YD_WRK_PROG_STAT", "W");
			inRecord.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recInPara, "YD_CRN_SCH_ID"));
			inRecord.setField("RTN_CD" , sRTN_CD);	
			inRecord.setField("RTN_MSG", sRTN_MSG);	
			ydUtils.putLog(szSessionName, szMethodName, "첫번째 크레인작업이 TO위치가 결정", YdConstant.DEBUG);
			return inRecord;

        	
        	
        }catch(Exception e){
			System.out.println("<Y5LocSrcRngDataSetCoil> Exception Error :"+ e.getLocalizedMessage());
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", "<Y5LocSrcRngDataSetCoil> Exception Error");	
			return outRecord;
        }//end of try~catch
    	
    }//end of Y5LocSrcRngDataSetCoil()
    
     
	
    /**
     * 오퍼레이션명 : 설비위치검색범위 조회 DataSet(TO위치 결정)(H/J)
     *  
     * @param  inRecord
     * @return intRtnVal
     * @throws 
     */
    public JDTORecord Y5EqpLocSrcRngDataSetCoil (JDTORecord inRecord) throws JDTOException {
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
		//┗━┛
	
    	JDTORecord 		outRecord 	= JDTORecordFactory.getInstance().create();
    	JDTORecord 		outRecord1 	= JDTORecordFactory.getInstance().create();
    	YdCrnSchDao   	ydCrnSchDao 	= new YdCrnSchDao();
    	YdCrnWrkMtlDao 	ydCrnWrkMtlDao 	= new YdCrnWrkMtlDao();
    	YdWrkbookDao 	ydWrkbookDao	= new YdWrkbookDao();
    	YdUtils       	ydUtils 		= new YdUtils();

    	//크레인스케줄
    	JDTORecordSet rsCrnsch    = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet rsCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet rsTemp      = null;
    	
    	//크레인작업재료
    	JDTORecord recCrnSch      = null;
    	JDTORecord recInTemp      = null;
 
    	String szMethodName = "Y5EqpLocSrcRngDataSetCoil";
    	String szMsg        = "";     	  
    	String szOperationName = "설비위치검색범위 조회 DataSet";
    	//크레인스케줄id,스케줄코드,야드구분,동구분,To위치결정방법
    	String szCrnSchId = "";
    	String szSchCd    = "";
    	String szYdGp     = "";
    	String szYdBayGp  = "";
    	String szToLocDcsnMtd = "";
    	String szWbookToLocDcsnMtd = "";
    	
		//작업예약Id
		String szWbookId  = "";
		//설비Id
		String szEqpId    = "";
		String sRTN_MSG	  = "";
		String sRTN_CD    = "";
		String sYD_TO_LOC_GUIDE = "";
		
		
    	int intRtnVal = 0 ;
    	
        try{

			//파라미터 Null Check
			szWbookId = ydDaoUtils.paraRecChkNull(inRecord, "YD_WBOOK_ID");
			szEqpId   = ydDaoUtils.paraRecChkNull(inRecord, "YD_EQP_ID");

			//작업예약을 조회한다. To위치 결정방법이  사용자 지정인지 알기위해서...
			rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydWrkbookDao.getYdWrkbook(inRecord, rsTemp, 0);
			if(intRtnVal <= 0) {
				szMsg = "Y5LocSrcRngDataSet 작업예약 조회 중 Error : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			rsTemp.absolute(1);
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setRecord(rsTemp.getRecord());
			szWbookToLocDcsnMtd = ydDaoUtils.paraRecChkNull(recInTemp, "YD_TO_LOC_DCSN_MTD");
			
			rsCrnsch = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setField("YD_WBOOK_ID", szWbookId);
			recInPara.setField("YD_EQP_ID",   szEqpId);
			
			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschByEqpIdandWBookId*/
			intRtnVal = ydCrnSchDao.getYdCrnsch(recInPara, rsCrnsch, 21);		
			
			
			szMsg = "작업예약 ID로 크레인스케줄 조회 스케줄의 횟수 : " + rsCrnsch.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
			
			for(int Loop_i = 1; Loop_i <= rsCrnsch.size(); Loop_i++) {
        		
    			szMsg = "◆◆◆◆◆ "+Loop_i+"번째 크레인 스케줄 ◆◆◆◆◆";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		
        		rsCrnsch.absolute(Loop_i);
        		recCrnSch  = rsCrnsch.getRecord();
        		ydUtils.displayRecord(szOperationName, recCrnSch);
        		
        		//크레인스케줄Data저장
        		szCrnSchId     = recCrnSch.getFieldString("YD_CRN_SCH_ID");
        		szSchCd        = recCrnSch.getFieldString("YD_SCH_CD");
        		szYdGp         = recCrnSch.getFieldString("YD_GP");
        		szYdBayGp  	   = recCrnSch.getFieldString("YD_BAY_GP");
        		szToLocDcsnMtd = recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD");
        		
        		//----------------------------------------------------------------------------------------------------------
        		//	대차상차스케줄이면 작업예약에 야드작업계획대차가 등록이 되어 있고
        		//	야드To위치결정방법이 F로 설정이 되어 있더라도
        		//	야드To위치Guide를 사용하지 않는다.
        		//----------------------------------------------------------------------------------------------------------
        		
        		if( !( szSchCd.substring(2, 4).equals("TC") && szSchCd.substring(6, 7).equals("U") ) ) {
	        		//20090917 김진욱 : 크레인 스케줄의 TO위치 결정방법이 S(최종위치)가는 작업이고  작업예약에 권상모음순서가 F라면...업데이트한다.
	        		if(Loop_i == rsCrnsch.size() && szWbookToLocDcsnMtd.equals("F")) {
	        			szToLocDcsnMtd = "F";
	        		}
        		}
        		//----------------------------------------------------------------------------------------------------------
        		//크레인스케줄의야드To위치결정방법 S 이면서 작업예약의 야드To위치결정방법이 F라면 야드To위치결정방법을 F로 처리한다
        		//----------------------------------------------------------------------------------------------------------
        		
        		if(szToLocDcsnMtd.equals("S") && szWbookToLocDcsnMtd.equals("F")) {
        			szToLocDcsnMtd = "F";
        		}
        		
        		//크레인작업재료조회(쿼리등록 완료 : 수정요청 항목이 추가됨)
				rsCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
        		JDTORecord recInData = JDTORecordFactory.getInstance().create();
        		
        		
        		
        		recInData.setField("YD_CRN_SCH_ID", szCrnSchId);
        		/*com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlBySchId*/
        		intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recInData, rsCrnwrkmtl, 6);
            	
        		//보조작업인 경우(나선형검색)
            	if(szToLocDcsnMtd.equals("W")) {
//////////////////////////////////////////////////            		
            		outRecord1 = (JDTORecord)this.Y5GetAidWrkLocCoil(rsCrnwrkmtl, recCrnSch);
            		sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
    				if(sRTN_MSG.equals("")){
    					sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
    				}
    				if ("0".equals(sRTN_CD)) {
    					szMsg = "Y5GetAidWrkLocCoil : 보조작업 스케줄 실패!!";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        				outRecord.setField("RTN_CD" , "0");	
    					outRecord.setField("RTN_MSG", sRTN_MSG);	
    					return outRecord;
    				}
            	}
 
            	//완성된 주작업 인 경우
            	if(szToLocDcsnMtd.equals("R") || szToLocDcsnMtd.equals("B") || szToLocDcsnMtd.equals("S")) {
            		
            		szMsg = "일반야드 TO위치 결정 모듈 호출 시작 - 스케줄코드["+szSchCd+"]";
        			
            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        			
         			if( Loop_i == rsCrnsch.size() ) {
        				recCrnSch.setField("LAST_CRN_SCH_YN", "Y");
        			}else{
        				recCrnSch.setField("LAST_CRN_SCH_YN", "N");
        			}
        			//------------------------------------------------------------------------------
///sjh1//////////////////////////////////////////     	        			
    				outRecord1 = (JDTORecord)this.Y5GetEqpLocSrchRngCoil(rsCrnwrkmtl, recCrnSch, recInTemp);
     				sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
    				if(sRTN_MSG.equals("")){
    					sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
    				}
    				ydUtils.putLog(szSessionName, szMethodName, "2" +sRTN_CD +sRTN_MSG, YdConstant.DEBUG);
            	 	if ("0".equals(sRTN_CD)) {
    					szMsg = "[Y5GetLocSrchRngCoil] : 최종위치를 결정하는 스케줄 실패!!";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        				outRecord.setField("RTN_CD" , "0");	
    					outRecord.setField("RTN_MSG", sRTN_MSG);	
        				return outRecord;
        				
        			}
 
    				szMsg = "일반야드 TO위치 결정 모듈 호출 성공 - 스케줄코드["+szSchCd+"]";
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


            	}        	        	
            	
            	//사용자지정위치 인 경우(열까지 지정된것은 따로처리함:송)
            	if(szToLocDcsnMtd.equals("F")){
            		sYD_TO_LOC_GUIDE = ydDaoUtils.paraRecChkNull(recInTemp, "YD_TO_LOC_GUIDE"); 
           			
       				szMsg = "지정된 일반야드 TO위치 결정 모듈 호출 시작 - 스케줄코드["+szSchCd+"]";
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        			if( Loop_i == rsCrnsch.size() ) {
        				recCrnSch.setField("LAST_CRN_SCH_YN", "Y");
        			}else{
        				recCrnSch.setField("LAST_CRN_SCH_YN", "N");
        			}
        			//------------------------------------------------------------------------------
        			
        			outRecord1 = (JDTORecord)this.Y5GetEqpLocSrchRngCoil(rsCrnwrkmtl, recCrnSch, recInTemp);
               	 	sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
    				if(sRTN_MSG.equals("")){
    					sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
    				}
    				if ("0".equals(sRTN_CD)) {
    					szMsg = "[Y5GetLocSrchRngCoil] : 최종위치를 결정하는 스케줄 실패!!";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        				outRecord.setField("RTN_CD" , "0");	
    					outRecord.setField("RTN_MSG", sRTN_MSG);	
    					return outRecord;
    				
    				}
        			szMsg = "지정된일반야드 TO위치 결정 모듈 호출 성공 - 스케줄코드["+szSchCd+"]";
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		}

            	
            }//end of for   
 	
			
			
////////////////////////////////////////////
//	스케줄 좌표값 셋팅 작업 
////////////////////////////////////////////	
			JDTORecordSet rsResultCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recStkBed      = null;
			JDTORecord recGetRsSet      = null;

			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
        	intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recInTemp, rsResultCrnwrkmtl, 306);

        	//rsBed.beforeFirst();
        	szMsg = "스케줄 좌표값 셋팅 작업"+rsResultCrnwrkmtl.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
        	//적치베드단위로 Loop
        	for(int Loop_i = 1; Loop_i <= rsResultCrnwrkmtl.size(); Loop_i++) {
        		rsResultCrnwrkmtl.absolute(Loop_i);
        		recStkBed = JDTORecordFactory.getInstance().create();
        		recStkBed.setRecord(rsResultCrnwrkmtl.getRecord());
        		
        		
        		//적치단정보 Setting
            	recGetRsSet = JDTORecordFactory.getInstance().create();
            	recGetRsSet.setField("YD_STK_COL_GP", recStkBed.getFieldString("YD_STK_COL_GP"));
            	recGetRsSet.setField("YD_STK_BED_NO", recStkBed.getFieldString("YD_STK_BED_NO"));
            	recGetRsSet.setField("YD_STK_LYR_NO", recStkBed.getFieldString("YD_STK_LYR_NO"));
            	
	        	//크레인 작업 지시 좌표값 설정 작업      	        	
	        	outRecord1 = (JDTORecord)this.Y5UpdGradStkLyr(recStkBed, recGetRsSet);
				
	        	
	        	sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				if ("0".equals(sRTN_CD)) {
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", sRTN_MSG);	
					return outRecord;
				}
        	}
        	
        	
        	
////////////////////////////////////////////            	
//to위치 결정 실패시에 default값으로 xx010101을 설정
////////////////////////////////////////////
            	
			rsCrnsch = JDTORecordFactory.getInstance().createRecordSet("");
			recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setField("YD_WBOOK_ID", szWbookId);
			recInPara.setField("YD_EQP_ID",   szEqpId);
			//com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschByEqpIdandWBookId
			intRtnVal = ydCrnSchDao.getYdCrnsch(recInPara, rsCrnsch, 21);
			if(intRtnVal <= 0) {
				szMsg = "해당 작업예약 크레인스케줄 조회 실패!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
	    
//				throw new DAOException("<Y1LocSrcRngDataSet> " + szMsg);
    		}
			
			
    		//20090821 김진욱 to위치 결정 실패시에 default값으로 xx010101을 설정
			for(int Loop = 1; Loop <= rsCrnsch.size(); Loop++) {
				rsCrnsch.absolute(Loop);
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setRecord(rsCrnsch.getRecord());
				if(ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_WO_LOC").equals("")) {
					recInPara.setField("YD_DN_WO_LOC", "XX010101");
					intRtnVal = ydCrnSchDao.updYdCrnsch(recInPara, 0);
					if(intRtnVal <= 0){
	    				szMsg = "Y5LocSrcRngDataSetCoil 크레인스케줄 To위치 Default값 등록 실패!!";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    				outRecord.setField("RTN_CD" , "0");	
	    				outRecord.setField("RTN_MSG", szMsg);	
	    				return outRecord;
					}
				}
			}
			
			
			
			rsCrnsch.absolute(1);
			recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setRecord(rsCrnsch.getRecord());
			
			//첫번째 크레인작업이 TO위치가 결정이 되었다면...크레인작업지시 송신
//			if( !ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_WO_LOC").equals("") ) {
				inRecord.setField("YD_WRK_PROG_STAT", "W");
//			inRecord.setField("YD_CRN_SCH_ID", szCrnSchId);
				inRecord.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recInPara, "YD_CRN_SCH_ID"));
				inRecord.setField("RTN_CD" , sRTN_CD);	
				inRecord.setField("RTN_MSG", sRTN_MSG);	
				ydUtils.putLog(szSessionName, szMethodName, "첫번째 크레인작업이 TO위치가 결정", YdConstant.DEBUG);
				return inRecord;

//			}
        	
        	
        }catch(Exception e){
			System.out.println("<Y5LocSrcRngDataSetCoil> Exception Error :"+ e.getLocalizedMessage());
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", "<Y5LocSrcRngDataSetCoil> Exception Error");	
			return outRecord;
        }//end of try~catch
        
        
//		inRecord.setField("RTN_CD" , sRTN_CD);	
//		inRecord.setField("RTN_MSG", sRTN_MSG);	
//		return inRecord;
//       return intRtnVal = 1;
    	
    }//end of Y5LocSrcRngDataSetCoil()
    
     
    
    /**
     * 오퍼레이션명 : 보조작업위치(나선형검색)(H/J)
     *  
     * @param  rsResultCrnwrkmtl, rsBed
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    	//상위 Method : 
    public JDTORecord Y5GetAidWrkLocCoil (JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch) throws JDTOException {
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 나선형으로 베드를 검색하여 저장위치를 구한다.
		//┗━┛
    	YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

    	JDTORecord recMinCrnwrkmtl = null;
    	JDTORecord 		outRecord 	= JDTORecordFactory.getInstance().create();
    	JDTORecord 		outRecord1 	= JDTORecordFactory.getInstance().create();
    	
    	JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	String szColGp      = "";
    	String szBedNo      = "";
    	String szLyrNo      = "";
    	String szStlNo      = "";
    	
    	String szMsg        = "";
    	String szMethodName = "Y5GetAidWrkLocCoil";
    	
    	String sRTN_MSG     = "";
    	int intRtnVal = 0;
    	
        try{
        	        	
        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일야드 TO위치결정 보조작업위치(나선형검색) START★★★★★", YdConstant.INFO);
        	
        	//작업재료의 Data를 정리한다.
        	rsCrnwrkmtl.absolute(1);
        	recMinCrnwrkmtl = rsCrnwrkmtl.getRecord();
        	
        	//현재 작업재료들의 Map정보를 알기위해 적치단을 조회 
        	recMinCrnwrkmtl.setField("YD_STK_LYR_MTL_STAT", "U");
        	/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO*/
        	intRtnVal = ydStkLyrDao.getYdStklyr(recMinCrnwrkmtl, outRecSet, 3);
        	if(intRtnVal <= 0){
	    		if(intRtnVal == 0) {
	    			szMsg = "<Y5GetAidWrkLocCoil> getYdStklyr data not found";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    		}
	    		if(intRtnVal == -2) {
	    			szMsg = "<Y5GetAidWrkLocCoil> getYdStklyr parameter error";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    		}
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
	    	}
        	
        	//조회한 적치단의 정보 중 권상대기인 재료의 Map정보를 가져온다.
        	outRecSet.absolute(1);
    		recMinCrnwrkmtl = outRecSet.getRecord();
    		szStlNo 		= recMinCrnwrkmtl.getFieldString("STL_NO");
    		szColGp 		= recMinCrnwrkmtl.getFieldString("YD_STK_COL_GP");
    		recMinCrnwrkmtl = JDTORecordFactory.getInstance().create();
    		recMinCrnwrkmtl.setField("YD_STK_COL_GP", szColGp);
    		recMinCrnwrkmtl.setField("STL_NO"		, szStlNo);
    		
    		//★★★★★★★★★★★	보조작업 To위치 나선형 위치조회 쿼리	★★★★★★★★★★★
    		outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp");
    		
    		/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdToSearchList*/
    		intRtnVal = ydStkLyrDao.getYdStklyr(recMinCrnwrkmtl, outRecSet, 304);
        	if(intRtnVal <= 0){
	    		if(intRtnVal == 0) {
	    			szMsg = "<Y5GetAidWrkLocCoil> getYdStklyr data not found";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    		}
	    		if(intRtnVal == -2) {
	    			szMsg = "<Y5GetAidWrkLocCoil> getYdStklyr parameter error";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    		}
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
	    	}
        	
        	for(int Loop_i = 1; Loop_i <= outRecSet.size(); Loop_i++) {
        		outRecSet.absolute(Loop_i);
        		recMinCrnwrkmtl = outRecSet.getRecord();
    			szColGp = recMinCrnwrkmtl.getFieldString("YD_STK_COL_GP");
    			szBedNo = recMinCrnwrkmtl.getFieldString("YD_STK_BED_NO");
    			szLyrNo = recMinCrnwrkmtl.getFieldString("YD_STK_LYR_NO");
    			
    			ydUtils.putLog(szSessionName, szMethodName, "["+Loop_i+"] C열연코일야드 TO위치결정 보조작업위치(나선형검색):"+szColGp+szBedNo+szLyrNo, YdConstant.INFO);
//sjh2-------------------------------------------------------------------			
    			outRecord1 = (JDTORecord)this.Y5ChkAidStkBedCoil(rsCrnwrkmtl, szColGp, szBedNo, szLyrNo, recCrnSch);
//---------------------------------------------------------------------
    			String sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
    			sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
    			if(sRTN_CD.equals("0")){ 
    				outRecord.setField("RTN_CD" , "0");	
    				outRecord.setField("RTN_MSG", sRTN_MSG);	
    				return outRecord;
    			}
    			
    			if(sRTN_CD.equals("1")){ 
    				//등록
    				outRecord.setField("RTN_CD" , "1");	
    				outRecord.setField("RTN_MSG", "[ C열연코일야드 TO위치결정 보조작업위치(나선형검색):"+szColGp);	
    				return outRecord;
    			}
        	}
        	

        	
        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일야드 TO위치결정 보조작업위치(나선형검색) END★★★★★", YdConstant.INFO);
        	System.out.println("적치가능한 BED가 없습니다.");
			outRecord.setField("RTN_CD" , "-1");	
			outRecord.setField("RTN_MSG", sRTN_MSG);	
			return outRecord;

        }catch(Exception e){
			szMsg = "<Y5GetAidWrkLocCoil> Exception Error :"+ e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", "처리중 Exception 발생");	
			return outRecord;

        }//end of try~catch

    	
    }//end of Y5GetAidWrkLocCoil()


    
    
    
    
    /**
     * 오퍼레이션명 : 주작업이지만 보조작업처리위치(나선형검색)(H/J)
     *  
     * @param  rsResultCrnwrkmtl, rsBed
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public JDTORecord Y5GetMainWrkLocCoil (JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch) throws JDTOException {
    	//상위 Method : 
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 나선형으로 베드를 검색하여 저장위치를 구한다.
		//┗━┛
    	YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
    	

    	JDTORecord recMinCrnwrkmtl = null;
    	JDTORecord 	  outRecord = JDTORecordFactory.getInstance().create();
    	JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	String szColGp      = "";
    	String szBedNo      = "";
    	String szLyrNo      = "";
    	String szMsg        = "";
    	String szMethodName = "Y5GetMainWrkLocCoil";

		JDTORecord outRecord1  		= JDTORecordFactory.getInstance().create(); 
    	
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
	    			szMsg = "<Y5GetMainWrkLocCoil> getYdStklyr data not found";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    		}
	    		if(intRtnVal == -2) {
	    			szMsg = "<Y5GetMainWrkLocCoil> getYdStklyr parameter error";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    		}
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
	    	}
        	
    		outRecSet.absolute(1);
    		recMinCrnwrkmtl = JDTORecordFactory.getInstance().create();
    		recMinCrnwrkmtl.setRecord(outRecSet.getRecord());
    		//권상대기인것
    		if(recMinCrnwrkmtl.getFieldString("YD_STK_LYR_MTL_STAT").equals("U") ) {
    			szColGp = recMinCrnwrkmtl.getFieldString("YD_STK_COL_GP");
    			szBedNo = recMinCrnwrkmtl.getFieldString("YD_STK_BED_NO");
    			szLyrNo = recMinCrnwrkmtl.getFieldString("YD_STK_LYR_NO");
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
//////////////////////////////////////////////////////        				
        				outRecord1 = (JDTORecord)this.Y5ChkMainStkBedCoil(rsCrnwrkmtl, szColGp, szBedNo,szLyrNo, recCrnSch);
        				String sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
        				String sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
        				
          				if(sRTN_CD.equals("0")){ 
        					//throw new DAOException(szMsg);
        					outRecord.setField("RTN_CD" , "0");	
        					outRecord.setField("RTN_MSG", sRTN_MSG);	
        					return outRecord;
        				}	

         				if(sRTN_CD.equals("1")){ 
          					System.out.println("열구분 : "+ szColGp + ", BED번호 :"+ szBedNo + "위치에 등록되었습니다.");
        					outRecord.setField("RTN_CD" , "1");	
        					return outRecord;
        				}	
       				
        			}
        			
        			for(int Loop_j = 1; Loop_j <= Loop_i; Loop_j++) {
        				//열증가
        				intTemp = Integer.parseInt(szColGp.substring(4)) + 1;
        				if (intTemp < 10)
        					szColGp = szColGp.substring(0,4) + "0" + intTemp;
        				else if (intTemp > 9 && intTemp < 99)
        					szColGp = szColGp.substring(0,4) + "" + intTemp;

        				//조건Check 적치베드상태에 작업재료 Data와 비교 메소드 호출
        				outRecord1 = (JDTORecord)this.Y5ChkMainStkBedCoil(rsCrnwrkmtl, szColGp, szBedNo,szLyrNo, recCrnSch);
           				String sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
        				String sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
        				
          				if(sRTN_CD.equals("0")){ 
        					//throw new DAOException(szMsg);
        					outRecord.setField("RTN_CD" , "0");	
        					outRecord.setField("RTN_MSG", sRTN_MSG);	
        					return outRecord;
        				}	

         				if(sRTN_CD.equals("1")){ 
          					System.out.println("열구분 : "+ szColGp + ", BED번호 :"+ szBedNo + "위치에 등록되었습니다.");
        					outRecord.setField("RTN_CD" , "1");	
        					return outRecord;
        				}	
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
        				outRecord1 = (JDTORecord)this.Y5ChkMainStkBedCoil(rsCrnwrkmtl, szColGp, szBedNo,szLyrNo, recCrnSch);
           				String sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
        				String sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
        				
          				if(sRTN_CD.equals("0")){ 
        					//throw new DAOException(szMsg);
        					outRecord.setField("RTN_CD" , "0");	
        					outRecord.setField("RTN_MSG", sRTN_MSG);	
        					return outRecord;
        				}	

         				if(sRTN_CD.equals("1")){ 
          					System.out.println("열구분 : "+ szColGp + ", BED번호 :"+ szBedNo + "위치에 등록되었습니다.");
        					outRecord.setField("RTN_CD" , "1");	
        					return outRecord;
        				}	
         			}
        			
        			for(int Loop_j = 1; Loop_j <= Loop_i; Loop_j++) {
        				//열감소
        				intTemp = Integer.parseInt(szColGp.substring(4)) - 1;
        				if (intTemp < 10)
        					szColGp = szColGp.substring(0,4) + "0" + intTemp;
        				else if (intTemp > 9 && intTemp < 99)
        					szColGp = szColGp.substring(0,4) + "" + intTemp;
        				
        				//조건Check 적치베드상태에 작업재료 Data와 비교 메소드 호출
        				outRecord1 = (JDTORecord)this.Y5ChkMainStkBedCoil(rsCrnwrkmtl, szColGp, szBedNo,szLyrNo, recCrnSch);
           				String sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
        				String sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
        				
          				if(sRTN_CD.equals("0")){ 
        					//throw new DAOException(szMsg);
        					outRecord.setField("RTN_CD" , "0");	
        					outRecord.setField("RTN_MSG", sRTN_MSG);	
        					return outRecord;
        				}	

         				if(sRTN_CD.equals("1")){ 
          					System.out.println("열구분 : "+ szColGp + ", BED번호 :"+ szBedNo + "위치에 등록되었습니다.");
        					outRecord.setField("RTN_CD" , "1");	
        					return outRecord;
        				}	
         			}//end of for
        			//증가 구분자로 Set
        			intGp = 0;
        		}//end of if
        	}//end of for
        	
        	
        	System.out.println("적치가능한 BED가 없습니다.");
			outRecord.setField("RTN_CD" , "-1");	
			outRecord.setField("RTN_MSG", "열구분 : "+ szColGp + ", BED번호 :"+ szBedNo + "위치에 등록되었습니다.");	
			return outRecord;

        	
        }catch(Exception e){
			System.out.println("<Y5GetMainWrkLocCoil> Exception Error :"+ e.getLocalizedMessage());
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", "Exception 발생되었습니다.");	
			return outRecord;
        }//end of try~catch
    	
    }//end of Y5GetMainWrkLocCoil()
    

	/**
	  * 오퍼레이션명 : C열연코일 차량상차To위치검색
	  *  
	  * @param  rsResultCrnwrkmtl, rsBed
	  * @return int 성공:1, 실패:-1
	  * @throws 
	  */
	 public JDTORecord Y5GetCarLdBedToLocToLoc (JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch) throws JDTOException{
	 	//상위 Method : DnLocInsMain
			//┏━┓
			//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
			//┗━┛
		
	 	YdStkLyrDao ydStkLyrDao 	= new YdStkLyrDao();
	 	YdCrnSchDao ydCrnSchDao 	= new YdCrnSchDao();
	 	YdWrkbookDao ydWrkbookDao 	= new YdWrkbookDao();
	 	YdStkBedDao ydStkBedDao 	= new YdStkBedDao();
	 	
	 	JDTORecordSet rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
	 	
	 	JDTORecord inRecord 	= JDTORecordFactory.getInstance().create();
	 	JDTORecord outRecord 	= JDTORecordFactory.getInstance().create();
	 	JDTORecord outRecord1 	= JDTORecordFactory.getInstance().create();
	 	JDTORecord recOutTemp         = null;
	 	JDTORecord recResultCrnwrkmtl = null;
	 	JDTORecord recReturnData      = null;
	 	JDTORecordSet outRecSet       = null;
	 	//스케줄코드
	 	String szSchCd    		= "";
	 	//야드행선구분
	 	String szRouteGp  		= "";
	 	
	 	//권상지시위치정보
	 	String szDnWoLoc 		= "";
	 	String szDnWoLocLayer 	= "";
	 	
	 	//작업예약
	 	String szWbookId 		= "";
	 	String szTRN_EQP_CD 	= "";
	 	String szYD_CAR_USE_GP 	= "";
	 	String szCAR_NO 		= "";
	 	String szCARD_NO 		= "";
	 	String sRTN_BED  		= "";
	 	
	 	int intRtnVal 			= 0;
	 	String szMsg        	= "";
	 	String szMethodName 	= "Y5GetCarLdBedToLocToLoc";
	 	String szOperationName	= "C열연 차량상차To위치검색";
	 	
     	String sRTN_CD		= "";
		String sRTN_MSG		= "";
		String szSTL_NO		= "";
		
	 	
	     try{
	     	//크레인 작업재료의 최하단 재료정보를 가져온다.
	     	rsCrnwrkmtl.absolute(1);
	     	recResultCrnwrkmtl = JDTORecordFactory.getInstance().create();
	     	
	     	recResultCrnwrkmtl.setRecord(rsCrnwrkmtl.getRecord());
	     	
	     	szSTL_NO = recResultCrnwrkmtl.getFieldString("STL_NO");
	     	
	     	//가져온 최하단 재료 정보의 적치단 위치중 권하 대기인 위치정보를 가져온다.
	     	recResultCrnwrkmtl.setField("YD_STK_LYR_MTL_STAT", "D");
	
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp1");
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO*/
			intRtnVal = ydStkLyrDao.getYdStklyr(recResultCrnwrkmtl, outRecSet, 3);
			if(intRtnVal <= 0) {
	 			if(intRtnVal == 0) {
	 				//권하대기가 없다면 권상 대기위치를 찾는다.(권상 모음 순서가 B인 경우)
	 				recResultCrnwrkmtl.setField("YD_STK_LYR_MTL_STAT", "U");
	     			outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp1");
	     			intRtnVal = ydStkLyrDao.getYdStklyr(recResultCrnwrkmtl, outRecSet, 3);
	     			
	 			}else if(intRtnVal == -2) {
	 				szMsg="["+szOperationName+"] 권하 대기인 위치정보 조회 시 parameter error";
	 				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
	 				
	 			}

			}
	
	     	outRecSet.absolute(1);
	     	recReturnData = JDTORecordFactory.getInstance().create();
	     	recReturnData.setRecord(outRecSet.getRecord());
	  
				//최하단 재료의 적치단 위치
	     	szDnWoLoc = recReturnData.getFieldString("YD_STK_COL_GP") + recReturnData.getFieldString("YD_STK_BED_NO"); 
	     	szDnWoLocLayer = recReturnData.getFieldString("YD_STK_LYR_NO");
	     	
	     	//권상지시위치에 등록
	     	if(recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD").equals("T")  ||
//	     	        || "TR07MM".equals(recCrnSch.getFieldString("YD_SCH_CD").substring(2, 8)) 
//	     	        || "TR08MM".equals(recCrnSch.getFieldString("YD_SCH_CD").substring(2, 8)) 
//	     	        || "TR57MM".equals(recCrnSch.getFieldString("YD_SCH_CD").substring(2, 8)) 
//	     	        || "TR58MM".equals(recCrnSch.getFieldString("YD_SCH_CD").substring(2, 8)) 
	     			(("TR0".equals(recCrnSch.getFieldString("YD_SCH_CD").substring(2 , 5)) || "TR5".equals(recCrnSch.getFieldString("YD_SCH_CD").substring(2 , 5))) 
	     			&& "MM".equals(recCrnSch.getFieldString("YD_SCH_CD").substring(6 , 8)))
	     	 ) {
	        	recCrnSch.setField("YD_UP_WO_LOC", szDnWoLoc);
	        	recCrnSch.setField("YD_UP_WO_LAYER", szDnWoLocLayer);
	        	
	        	intRtnVal = ydCrnSchDao.updYdCrnsch(recCrnSch, 0);
	    		if(intRtnVal <= 0) {
	    			if(intRtnVal == 0) {
	    				szMsg="["+szOperationName+"] 크레인스케줄 수정 - data not found";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
	    			}else if(intRtnVal == -1) {
	    				szMsg="["+szOperationName+"] 크레인스케줄 수정 - duplicate data";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
	    			}else if(intRtnVal == -2) {
	    				szMsg="["+szOperationName+"] 크레인스케줄 수정 - parameter error";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -3){
	    				szMsg="["+szOperationName+"] 크레인스케줄 수정 - execution failed";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
	    		}
		    		
	     	}
	     	
	     	
	     	
	     	recResultCrnwrkmtl.setField("YD_SCH_CD", recCrnSch.getFieldString("YD_SCH_CD"));
	     	
	 		szSchCd    = recCrnSch.getFieldString("YD_SCH_CD");
	 		szWbookId  = recCrnSch.getFieldString("YD_WBOOK_ID");
	 		szRouteGp  = recResultCrnwrkmtl.getFieldString("YD_AIM_RT_GP");
	 		
				szMsg="["+szOperationName+"] 스케줄코드 :" + szSchCd;
				szMsg+=", 작업예약id :" + szWbookId;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				szMsg="["+szOperationName+"] 목표행선 :" + szRouteGp;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				szMsg="["+szOperationName+"] 스케줄코드가 출고인 경우 현재 상태 : " + szSchCd.substring(4,8);
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	     		
	 		//작업예약id로 작업예약을 조회한다.
	 		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
	 		intRtnVal = ydWrkbookDao.getYdWrkbook(recCrnSch, outRecSet, 0);
	 		outRecSet.absolute(1);
	 		recOutTemp = JDTORecordFactory.getInstance().create();
	 		recOutTemp.setRecord(outRecSet.getRecord());
	 		
	 		//조회된 data의 항목 중 운송장비코드와 차량사용구분의 값을 가져온다.
	     	szTRN_EQP_CD    = ydDaoUtils.paraRecChkNull(recOutTemp, "TRN_EQP_CD");
	     	szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_USE_GP");
	     	szCAR_NO        = ydDaoUtils.paraRecChkNull(recOutTemp, "CAR_NO");
	     	szCARD_NO       = ydDaoUtils.paraRecChkNull(recOutTemp, "CARD_NO");
	         	
			szMsg="["+szOperationName+"] 저장위치결정 중 운송장비코드와 차량사용구분 " + szTRN_EQP_CD + " ,  " + szYD_CAR_USE_GP;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	     	if(szYD_CAR_USE_GP.equals("L")) {
	         	//운송장비코드와 차량사용구분으로 적치열을 조회한다.
					JDTORecord recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("TRN_EQP_CD", szTRN_EQP_CD);
					recInPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
					rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
		        	intRtnVal = ydStkBedDao.getYdStkbed(recInPara, rsBed, 9);
		        	//에러처리
		        	//
	     	}else if(szYD_CAR_USE_GP.equals("G")){
	         	//차량사용구분과 카드번호 차량번호으로 적치열을 조회한다.
					JDTORecord recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
					recInPara.setField("CAR_NO", szCAR_NO);
					recInPara.setField("CARD_NO", szCARD_NO);
					recInPara.setField("STL_NO", szSTL_NO);
					
					rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
//PIDEV_S :병행가동용:PI_YD
					recInPara.setField("PI_YD",    	szSchCd.substring(0,1));						
		        	intRtnVal = ydStkBedDao.getYdStkbed(recInPara, rsBed, 20);
		        	//에러처리
		        	//
	     	}else{
	     		szMsg="["+szOperationName+"] 작업예약에 차량사용구분값이 잘못되었습니다. 차량사용구분 : " + szYD_CAR_USE_GP;
	     		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;

	     	}
	     	

			szMsg="["+szOperationName+"] 차량 저장위치결정 일때 검색된 차량베드의 수 " + rsBed.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		        	
	     		
	     	
	     	
	     	if( rsBed.size() <= 0) {
	     		szMsg="["+szOperationName+"] 저장위치 Bed가 검색되지않았습니다.";
	     		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;

	     	}
	 		
//차량상차 bed와 검색kkkkk
	     	outRecord1 = (JDTORecord)this.Y5ChkCarLdbedCrnMtl(rsBed, recResultCrnwrkmtl) ;
	     
	     	sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			sRTN_BED	= StringHelper.evl(outRecord1.getFieldString("RTN_BED"), "");
			if ("0".equals(sRTN_CD)) {
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				return outRecord;
			}	

			if ("".equals(sRTN_BED)) {
    			szMsg =  szMethodName + "코일제품  To위치검색 중 ERROR 발생";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

    			outRecord.setField("RTN_CD" , "-1");	
   		    	outRecord.setField("RTN_MSG", sRTN_MSG);	
				ydUtils.putLog(szSessionName, szMethodName, "4" +sRTN_CD +sRTN_MSG, YdConstant.DEBUG);
				return outRecord;
			
			}        		

        	//크레인 스케줄  권하지시위치 업데이트
			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("YD_CRN_SCH_ID",  recCrnSch.getFieldString("YD_CRN_SCH_ID") ) ;

			inRecord.setField("YD_WRK_PROG_STAT",  "W" ) ;
			inRecord.setField("YD_DN_WO_LOC",   sRTN_BED.substring(0,8)) ;
			inRecord.setField("YD_DN_WO_LAYER", sRTN_BED.substring(8,11)) ;
        	intRtnVal = this.Y5UpdYdCrnsch(inRecord, 0);
        	if(intRtnVal == -1) {
        		szMsg="크레인 스케줄 권하지시위치 등록 실패!";
        		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;

        	}
			
			
	     }catch(Exception e){
	 		szMsg="["+szOperationName+"] Exception Error :" + e.getLocalizedMessage();
	 		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;
	     }//end of try~catch
	     
			outRecord.setField("RTN_CD" , sRTN_CD);	
			outRecord.setField("RTN_MSG", sRTN_MSG);	
			return outRecord;
	     
	 }//end of Y5GetCarLdBedToLoc()
	 
	   
	    
    /**
     * 오퍼레이션명 : 위치검색범위및위치검색Bed조회(H/J)
     *  
     * @param  rsResultCrnwrkmtl, rsBed
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public JDTORecord Y5GetLocSrchRngCoil (JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch, JDTORecord recInTemp)  throws JDTOException {
    	//상위 Method : DnLocInsMainCoil
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
		//┗━┛
	
    	YdStkLyrDao     ydStkLyrDao     = new YdStkLyrDao();
    	YdCrnSchDao     ydCrnSchDao     = new YdCrnSchDao();
    	YdWrkbookDao    ydWrkbookDao    = new YdWrkbookDao();
    	YdEqpDao		ydEqpDao 		= new YdEqpDao();
    	
    	CoilYdToLocDcsnUtil ydToLocDcsnUtil = new CoilYdToLocDcsnUtil();

    	JDTORecord recMinCrnwrkmtl = null;
    	JDTORecord recGetCrnWrkMtl = null;
    	JDTORecord recUpdCrnSchData = null;
	 	JDTORecord 		outRecord 	= JDTORecordFactory.getInstance().create();
	 	JDTORecord 		outRecord1 	= JDTORecordFactory.getInstance().create();
   	
    	String szStlNo      = "";
    	String szYdRouteGp  = "";
    	String szYdSchCd    = "";
    	String sRTN_BED     = "";   
    	
    	JDTORecordSet rsWbook = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
    	
    	
    	//권상지시위치정보
    	String szDnWoLoc 		= "";
    	String szDnWoLocLayer 	= "";
    	
    	String szYD_WBOOK_ID 	= "";
    	String szYD_CAR_USE_GP	= "";
    	String szTRN_EQP_CD 	= "";
    	String szCAR_NO 		= "";
    	String szCARD_NO 		= "";
    	String sYD_TO_LOC_GUIDE    = "";
    	String sYD_TO_LOC_DCSN_MTD = "";   	
    	JDTORecord recResultCrnwrkmtl 	= null;
    	JDTORecord recReturnData      	= null;
    	JDTORecord recWbook           	= null;
    	JDTORecordSet outRecSet       	= null;
    	
    	int intRtnVal = 0;
    	String szMsg        = "";
    	String szMethodName = "Y5GetLocSrchRngCoil";
       	String sRTN_CD		= "";
		String sRTN_MSG		= "";
		String sYD_CRN_SCH_ID = "";
		String szEqpId        = "";
    	
        try{
        	
        	
        	for(int Loop_i = 1; Loop_i <= rsCrnwrkmtl.size(); Loop_i++) {
        		rsCrnwrkmtl.absolute(Loop_i);
        		recMinCrnwrkmtl = rsCrnwrkmtl.getRecord();
        		
        		        		            	
        		szStlNo     		= ydDaoUtils.paraRecChkNull(recMinCrnwrkmtl, "STL_NO");
            	szYdSchCd   		= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD");
            	szYdRouteGp			= ydDaoUtils.paraRecChkNull(recMinCrnwrkmtl, "YD_AIM_RT_GP");
               	sYD_TO_LOC_GUIDE    = ydDaoUtils.paraRecChkNull(recInTemp, "YD_TO_LOC_GUIDE");
            	sYD_TO_LOC_DCSN_MTD = ydDaoUtils.paraRecChkNull(recInTemp, "YD_TO_LOC_DCSN_MTD");           	
            	sYD_CRN_SCH_ID		= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID");
            	szEqpId 			= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_EQP_ID");
            	
            	if(ydDaoUtils.paraRecChkNull(recCrnSch,"YD_TO_LOC_DCSN_MTD").equals("R") ) {
            		//가져온 최하단 재료 정보의 적치단 위치중 권하 대기인 위치정보를 가져온다.
                	recResultCrnwrkmtl.setField("YD_STK_LYR_MTL_STAT", "D");
        			outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp1");
        			intRtnVal = ydStkLyrDao.getYdStklyr(recResultCrnwrkmtl, outRecSet, 3);
            		
            		outRecSet.absolute(1);
            		recReturnData = JDTORecordFactory.getInstance().create();
                	recReturnData.setRecord(outRecSet.getRecord());
             
        			//재료의 적치단의 권하대기 위치
                	szDnWoLoc = recReturnData.getFieldString("YD_STK_COL_GP") + recReturnData.getFieldString("YD_STK_BED_NO"); 
                	szDnWoLocLayer = recReturnData.getFieldString("YD_STK_LYR_NO");
                	//크레인스케줄의 권상지시위치에 등록한다. (스케줄 편성 시에 주작업이면서 보조작업으로 처리 후 권상 지시위치를 알 수 없었던 경우)
    	        	recCrnSch.setField("YD_UP_WO_LOC", szDnWoLoc);
    	        	recCrnSch.setField("YD_UP_WO_LAYER", szDnWoLocLayer);
    	        	//recCrnSch.setField("REG_DDTT", null);
    	        	
    	        	intRtnVal = ydCrnSchDao.updYdCrnsch(recCrnSch, 0);
    	    		if(intRtnVal <= 0) {
    	    			if(intRtnVal == 0) {
    	    				szMsg= szMethodName + "updYdCrnsch data not found";
    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
    	    			}else if(intRtnVal == -1) {
    	    				szMsg= szMethodName + "updYdCrnsch duplicate data,";
    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    	    			}else if(intRtnVal == -2) {
    	    				szMsg= szMethodName + "updYdCrnsch parameter error";
    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    	    			}else if(intRtnVal == -3){
    	    				szMsg= szMethodName + "updYdCrnsch execution failed";
    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    	    			}
    					outRecord.setField("RTN_CD" , "0");	
    					outRecord.setField("RTN_MSG", szMsg);	
    					return outRecord;
    	    		}
            	}
            	
            	intRtnVal = ydWrkbookDao.getYdWrkbook(recCrnSch, rsWbook, 0);
            	if(intRtnVal <= 0) {
        			if(intRtnVal == 0) {
        				szMsg= szMethodName + "getYdWrkbook data not found";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
        			}else if(intRtnVal == -1) {
        				szMsg= szMethodName + "getYdWrkbook duplicate data,";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
        			}else if(intRtnVal == -2) {
        				szMsg= szMethodName + "getYdWrkbook parameter error";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        			}else if(intRtnVal == -3){
        				szMsg= szMethodName + "getYdWrkbook execution failed";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        			}
    				outRecord.setField("RTN_CD" , "-1");	
    				outRecord.setField("RTN_MSG", szMsg);	
    				return outRecord;

        		}
            	rsWbook.absolute(1);
            	recWbook = JDTORecordFactory.getInstance().create();
            	recWbook.setRecord(rsWbook.getRecord());
            	szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recWbook, "YD_CAR_USE_GP");
            	szTRN_EQP_CD    = ydDaoUtils.paraRecChkNull(recWbook, "TRN_EQP_CD");
            	szCAR_NO        = ydDaoUtils.paraRecChkNull(recWbook, "CAR_NO");
            	szCARD_NO       = ydDaoUtils.paraRecChkNull(recWbook, "CARD_NO");
////////////////////////////////////////            	            	
				szMsg = "일반 크레인 체크 시작:" ;
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord1 = (JDTORecord)ydToLocDcsnUtil.procCoilYdToPosDecision(sYD_CRN_SCH_ID,szStlNo, szYdSchCd, szYdRouteGp, szYD_CAR_USE_GP, szTRN_EQP_CD, szCAR_NO, szCARD_NO,sYD_TO_LOC_DCSN_MTD,sYD_TO_LOC_GUIDE,szEqpId);
////////////////////////////////////////           	 	
            	
            	sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            	sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				sRTN_BED	= StringHelper.evl(outRecord1.getFieldString("RTN_BED"), "");
				
				ydUtils.putLog(szSessionName, szMethodName, "3" +sRTN_CD +sRTN_MSG, YdConstant.DEBUG);
        	 	
				if ("0".equals(sRTN_CD)) {
        			szMsg = szMethodName + "To위치평점실패";
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				outRecord.setField("RTN_CD" , "0");	
    				outRecord.setField("RTN_MSG", sRTN_MSG);	
    				return outRecord;
				
				}
				if (!("1".equals(sRTN_CD))) {
        			szMsg =  szMethodName + " To위치평점실패";
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				outRecord.setField("RTN_CD" , sRTN_CD);	
    				outRecord.setField("RTN_MSG", sRTN_MSG);	
    				ydUtils.putLog(szSessionName, szMethodName, "4" +sRTN_CD +sRTN_MSG, YdConstant.DEBUG);
    				return outRecord;
				
				}

				if ("".equals(sRTN_BED)) {
        			szMsg =  szMethodName + "코일소재야드 To위치검색 중 ERROR 발생";
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
       				outRecord.setField("RTN_CD" , "-1");	
       		    	outRecord.setField("RTN_MSG", sRTN_MSG);	
    				ydUtils.putLog(szSessionName, szMethodName, "5" +sRTN_CD +sRTN_MSG, YdConstant.DEBUG);
    				return outRecord;
				
				}        		
        		recGetCrnWrkMtl = JDTORecordFactory.getInstance().create();
        		recGetCrnWrkMtl.setField("STL_NO",       		szStlNo);
        		recGetCrnWrkMtl.setField("YD_STK_COL_GP",       sRTN_BED.substring(0,6));
        		recGetCrnWrkMtl.setField("YD_STK_BED_NO",       sRTN_BED.substring(6,8));
        		recGetCrnWrkMtl.setField("YD_STK_LYR_NO",       sRTN_BED.substring(8,11));
        		recGetCrnWrkMtl.setField("YD_STK_LYR_MTL_STAT", "D");
	        	intRtnVal = ydStkLyrDao.updYdStklyrNEW(recGetCrnWrkMtl, 0);
	        	if(intRtnVal <= 0) {
	        		if(intRtnVal == 0) {
	        			szMsg=  szMethodName + "updYdStklyr data not found";
	        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

	        		}else if(intRtnVal == -2) {
	        			szMsg=  szMethodName + "updYdStklyr parameter error";
	        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

	        		}
	        		System.out.println("적치단에 저장하지 못했습니다.");
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;

	        	}
	
	
	        	//크레인 스케줄  권하지시위치 업데이트
	        	recUpdCrnSchData = JDTORecordFactory.getInstance().create();
	        	recUpdCrnSchData.setField("YD_CRN_SCH_ID",  recCrnSch.getFieldString("YD_CRN_SCH_ID") ) ;

	        	recUpdCrnSchData.setField("YD_WRK_PROG_STAT",  "W" ) ;
	        	recUpdCrnSchData.setField("YD_DN_WO_LOC",   sRTN_BED.substring(0,8)) ;
	        	recUpdCrnSchData.setField("YD_DN_WO_LAYER", sRTN_BED.substring(8,11)) ;
	        	intRtnVal = this.Y5UpdYdCrnsch(recUpdCrnSchData, 0);
	        	if(intRtnVal == -1) {
	        		szMsg="크레인 스케줄 권하지시위치 등록 실패!";
	        		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;

	        	}
	
        	}
        	
 
        }catch(Exception e){
			System.out.println(szMethodName + "Exception Error :"+ e.getLocalizedMessage());
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;

        }//end of try~catch
        
        
		outRecord.setField("RTN_CD" , sRTN_CD);	
		outRecord.setField("RTN_MSG", sRTN_MSG);	
		return outRecord;

    	
    }//end of Y5GetLocSrchRngCoil()
    
    /**
     * 오퍼레이션명 : 설비위치검색범위및위치검색Bed조회(H/J)
     *  
     * @param  rsResultCrnwrkmtl, rsBed
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public JDTORecord Y5GetEqpLocSrchRngCoil (JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch, JDTORecord recInTemp)  throws JDTOException {
    	//상위 Method : DnLocInsMainCoil                                                                                                     
		//┏━┓                                                                                                                               
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.                                                                  
		//┗━┛                                                                                                                               
	                                                                                                                                         
    	YdStkLyrDao     ydStkLyrDao     = new YdStkLyrDao();                                                                                 
                                                                                                                                           
    	JDTORecord recMinCrnwrkmtl 	= null;                                                                                                   
    	JDTORecord recUpdCrnSchData = null;                                                                                                  
    	JDTORecord outRecord 		= JDTORecordFactory.getInstance().create();                                                                
    	JDTORecord inRecord 		= JDTORecordFactory.getInstance().create();                                                                  
    	JDTORecord outRecord1  		= JDTORecordFactory.getInstance().create();                                                                 
    	                                                                                                                                     
    	String szStlNo      = "";                                                                                                            
    	String szSchCd    	= "";                                                                                                            
    	String sRTN_BED     = "";                                                                                                            
                                                                                                                                           
    	                                                                                                                                     
    	int intRtnVal = 0;                                                                                                                   
    	String szMsg        = "";                                                                                                            
    	String szMethodName = "Y5GetEqpLocSrchRngCoil";                                                                                      
       	String sRTN_CD		= "";                                                                                                            
		String sRTN_MSG		= "";                                                                                                                
   	                                                                                                                                       
    	                                                                                                                                     
        try{                                                                                                                               
        	                                                                                                                                 
        	szMsg =  szMethodName + "설비위치검색범위및위치검색Bed조회 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
        	for(int Loop_i = 1; Loop_i <= rsCrnwrkmtl.size(); Loop_i++) {                                                                    
        		rsCrnwrkmtl.absolute(Loop_i);                                                                                                  
        		recMinCrnwrkmtl = rsCrnwrkmtl.getRecord();                                                                                     
        		                                                                                                                               
        		        		            	                                                                                                     
        		szStlNo = ydDaoUtils.paraRecChkNull(recMinCrnwrkmtl, "STL_NO");                                                                
        		szSchCd	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD");                                                                   
//C증설                                                                                                                                           
//권하 위치 update 처리        		                                                                                                         
    			if((szSchCd.equals("HBKE01UM"))||(szSchCd.equals("HBKE03UM"))||(szSchCd.equals("HBKE03LM"))||
    			   (szSchCd.equals("HBKD01UM"))||(szSchCd.equals("HBFE01UM"))||(szSchCd.equals("HBFE03UM"))||
    			   (szSchCd.equals("HBFE03LM"))||(szSchCd.equals("HBKD02LM"))||(szSchCd.equals("HBKD03LM"))||(szSchCd.equals("HBKD04LM"))||
    			   
    			   (szSchCd.equals("HAKE01UM"))||(szSchCd.equals("HAKE03UM"))||(szSchCd.equals("HAKE03LM"))||
    			   (szSchCd.equals("HAKD01UM"))||(szSchCd.equals("HAKD02LM"))||(szSchCd.equals("HAKD03LM"))||(szSchCd.equals("HAKD04LM"))||
    			   
    			   (szSchCd.equals("HCKE01UM"))||(szSchCd.equals("HCKE03UM"))||(szSchCd.equals("HCKE03LM"))||
				   (szSchCd.equals("HCKD01UM"))||(szSchCd.equals("HCFE01UM"))||(szSchCd.equals("HCFE03UM"))||
				   (szSchCd.equals("HCFE03LM"))||(szSchCd.equals("HCKD02LM"))||(szSchCd.equals("HCKD03LM"))||(szSchCd.equals("HCKD04LM"))||
    			   
    			   (szSchCd.equals("HEDE01UM"))||(szSchCd.equals("HEDE03UM"))||(szSchCd.equals("HEDE03LM"))||
				   (szSchCd.equals("HEDD01UM"))||(szSchCd.equals("HEDD02LM"))||(szSchCd.equals("HEDD03LM"))|| (szSchCd.equals("HEDD04LM"))||                             

				   (szSchCd.equals("HGFE01UM"))||(szSchCd.equals("HGFE03UM"))||(szSchCd.equals("HGFE03LM"))||
	  			   
				   (szSchCd.equals("HHKE01UM"))||(szSchCd.equals("HHKD01UM"))||(szSchCd.equals("HHKE03LM"))||                     
	  			   (szSchCd.equals("HHKE03UM")||(szSchCd.equals("HHKD02LM"))||(szSchCd.equals("HHKD03LM"))|| (szSchCd.equals("HHKD04LM")))                      
	  			    ){   
    				
    				if( (szSchCd.equals("HHKD02LM"))||
						(szSchCd.equals("HEDD02LM"))||
						(szSchCd.equals("HCKD02LM"))||
						(szSchCd.equals("HAKD02LM"))||
						(szSchCd.equals("HBKD02LM"))){
    					
    					sRTN_BED	= "H" + szSchCd.substring(1,3)+"E" + "01" + "00001"; 
    				}else{
    					sRTN_BED	= "H" + szSchCd.substring(1,4) + "01" + "00001";  
    				}
    	            	                      
//C증설				
					     
					if((szSchCd.substring(1,4).equals("CKE"))||(szSchCd.substring(1,4).equals("CKD"))){
    					sRTN_BED	= "H" + szSchCd.substring(1,4) + "03" + "00001";                                                                     
    				} else if((szSchCd.substring(1,4).equals("BKE"))||(szSchCd.substring(1,4).equals("BKD"))){
    					sRTN_BED	= "H" + szSchCd.substring(1,4) + "04" + "00001";                                                                     
//        				151021 HUN SPM5 추가 권하위치 AKE 로 나가는게 맞는지 확인후 수정할것... 
    				} else if((szSchCd.substring(1,4).equals("AKE"))||(szSchCd.substring(1,4).equals("AKD")) ){
    					sRTN_BED	= "H" + szSchCd.substring(1,4) + "05" + "00001";                                                                     
    				} else if((szSchCd.substring(1,4).equals("CFE"))||(szSchCd.substring(1,4).equals("CFD"))){
    					sRTN_BED	= "H" + szSchCd.substring(1,4) + "04" + "00001";                                                                     
    				} else if((szSchCd.substring(1,4).equals("BFE"))||(szSchCd.substring(1,4).equals("BFD"))){
    					sRTN_BED	= "H" + szSchCd.substring(1,4) + "05" + "00001";       
    				}                                                                                                                         
    	        	//크레인 스케줄  권하지시위치 업데이트                                                                                     
    	        	recUpdCrnSchData = JDTORecordFactory.getInstance().create();                                                               
    	        	recUpdCrnSchData.setField("YD_CRN_SCH_ID",  recCrnSch.getFieldString("YD_CRN_SCH_ID") ) ;                                  
                                                                                                                                           
    	        	recUpdCrnSchData.setField("YD_WRK_PROG_STAT",  "W" ) ;                                                                     
    	        	recUpdCrnSchData.setField("YD_DN_WO_LOC",   sRTN_BED.substring(0,8)) ;                                                     
    	        	recUpdCrnSchData.setField("YD_DN_WO_LAYER", sRTN_BED.substring(8,11)) ;                                                    
    	        	intRtnVal = this.Y5UpdYdCrnsch(recUpdCrnSchData, 0);                                                                       
    	        	if(intRtnVal == -1) {                                                                                                      
    	        		szMsg="크레인 스케줄 권하지시위치 등록 실패!";                                                                           
    	        		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);                                                    
    					outRecord.setField("RTN_CD" , "0");	                                                                                         
    					outRecord.setField("RTN_MSG", szMsg);	                                                                                       
    					return outRecord;                                                                                                            
                                                                                                                                           
    	        	}                                                                                                                          
    			} else {                                                                                                                         
    				                                                                                                                               
    				JDTORecordSet   outRecSet   = JDTORecordFactory.getInstance().createRecordSet("YD");                                           
    				inRecord = JDTORecordFactory.getInstance().create();                                                                           
    				inRecord.setField("STL_NO",   szStlNo);                                                                                        
//    	getYdStklyr24                                                                                                                      
    				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdStkPosInfo_PIDEV*/                                                        
    				intRtnVal = ydStkLyrDao.getYdStklyr(inRecord, outRecSet, 24);                                                                  
    				if (intRtnVal > 0) {                                                                                                           
    					//적치되어 있는 정보 삭제처리                                                                                                
    					outRecSet.first();                                                                                                           
    					outRecord1 = outRecSet.getRecord();                                                                                          
    					//UPDATE 항목 record  생성                                                                                                   
    					inRecord = JDTORecordFactory.getInstance().create();                                                                         
    					                                                                                                                             
    					//적치단 재료상태가 적치 가능이면 재료 등록                                                                                  
    					//적치단 테이블 업데이트                                                                                                     
    					//적치열구분 = 설비ID                                                                                                        
    					inRecord.setField("YD_STK_COL_GP", 	    ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_COL_GP"));                             
    					inRecord.setField("YD_STK_BED_NO", 	    ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_BED_NO"));                             
    					inRecord.setField("YD_STK_LYR_NO", 	    ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_LYR_NO"));                             
    					inRecord.setField("YD_STK_LYR_MTL_STAT", "E");                                                                               
    					inRecord.setField("STL_NO", 		   "");                                                                                   
    					                                                                                                                             
    					//업데이트 실행                                                                                                              
    					//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo                                                         
    	   	        	intRtnVal = ydStkLyrDao.updYdStklyr(inRecord, 303);                                                                    
        	        	if(intRtnVal <= 0) {                                                                                                   
        	        		if(intRtnVal == 0) {                                                                                                 
        	        			szMsg = szMethodName + "updYdStklyr data not found";                                                               
        	        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);                                               
                                                                                                                                           
        	        		}else if(intRtnVal == -2) {                                                                                          
        	        			szMsg = szMethodName + "updYdStklyr parameter error";                                                              
        	        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);                                              
                                                                                                                                           
        	        		}                                                                                                                    
        	        		System.out.println("적치단에 저장하지 못했습니다.");                                                                 
        					outRecord.setField("RTN_CD" , "0");	                                                                                     
        					outRecord.setField("RTN_MSG", szMsg);	                                                                                   
        					return outRecord;                                                                                                        
                                                                                                                                           
        	        	}                                                                                                                      
    				}                                                                                                                              
    				                                                                                                                               
//권상 위치 update 처리   
//C증설    				
    				if(//(szSchCd.equals("JBTC01MM"))||(szSchCd.equals("JBTC02MM"))||
    				   (szSchCd.equals("JBTC05MM"))||
    				   (szSchCd.equals("JATC05MM"))||
    	    		   (szSchCd.equals("JCTC01MM"))||(szSchCd.equals("JCTC02MM"))||(szSchCd.equals("JCTC05MM"))||
    	    		   (szSchCd.equals("JETC01MM"))||(szSchCd.equals("JETC02MM"))|| 
    		   		   (szSchCd.equals("JGTC01MM"))||(szSchCd.equals("JGTC02MM"))||
    	    		   (szSchCd.equals("JHTC01MM"))||(szSchCd.equals("JHTC02MM"))
 		   		     ){ 
    					if (szSchCd.equals("JBTC01MM")||szSchCd.equals("JBTC02MM")){		
    						sRTN_BED	= "HBFE05" + "00001";
    					} else if ((szSchCd.equals("JCTC01MM"))||(szSchCd.equals("JCTC02MM"))){		
    						sRTN_BED	= "HCFD04" + "00001";
    					} else if ((szSchCd.equals("JETC01MM"))||(szSchCd.equals("JETC02MM"))){		
    						sRTN_BED	= "HEDD01" + "00001";
    					} else if ((szSchCd.equals("JGTC01MM"))||(szSchCd.equals("JGTC02MM"))){		
    						sRTN_BED	= "HGFD01" + "00001";
    					} else if ((szSchCd.equals("JHTC01MM"))||(szSchCd.equals("JHTC02MM"))){
    						sRTN_BED	= "HHKD01" + "00001";
    					} else if ((szSchCd.equals("JBTC05MM")))	{
    						sRTN_BED	= "HBKD04" + "00001";
    					} else if ((szSchCd.equals("JATC05MM")))	{
    						sRTN_BED	= "HAKD05" + "00001";
    					} else if ((szSchCd.equals("JCTC05MM")))	{
    						sRTN_BED	= "HCKD03" + "00001";
    					}
    				} else {
    					sRTN_BED	= "H" + szSchCd.substring(1,4) + "01" + "00001";                                                                     

    					if((szSchCd.substring(1,4).equals("EKE"))||(szSchCd.substring(1,4).equals("EKD"))){
        					sRTN_BED	= "H" + "ED"+szSchCd.substring(3,4) + "01" + "00001";                                                                     
        				} else if((szSchCd.substring(1,4).equals("CKE"))||(szSchCd.substring(1,4).equals("CKD"))){
        					sRTN_BED	= "H" + szSchCd.substring(1,4) + "03" + "00001";                                                                     
        				} else if((szSchCd.substring(1,4).equals("BKE"))||(szSchCd.substring(1,4).equals("BKD"))){
        					sRTN_BED	= "H" + szSchCd.substring(1,4) + "04" + "00001";       
//        				151007 HUN SPM5 추가
        				} else if((szSchCd.substring(1,4).equals("AKE"))||(szSchCd.substring(1,4).equals("AKD"))){
        					sRTN_BED	= "H" + szSchCd.substring(1,4) + "05" + "00001";                                                                     
        				} else if((szSchCd.substring(1,4).equals("CFE"))||(szSchCd.substring(1,4).equals("CFD"))){
        					sRTN_BED	= "H" + szSchCd.substring(1,4) + "04" + "00001";                                                                     
        				} else if((szSchCd.substring(1,4).equals("BFE"))||(szSchCd.substring(1,4).equals("BFD"))){
        					sRTN_BED	= "H" + szSchCd.substring(1,3)+"E" + "05" + "00001";       
        				}                                                                                                                         

    				
    				
    				}                                                                                                                               
    	        	//크레인 스케줄  권상지시위치 업데이트                                                                                     
    	        	recUpdCrnSchData = JDTORecordFactory.getInstance().create();                                                               
    	        	recUpdCrnSchData.setField("YD_CRN_SCH_ID",  recCrnSch.getFieldString("YD_CRN_SCH_ID") ) ;                                  
                                                                                                                                           
    	        	recUpdCrnSchData.setField("YD_WRK_PROG_STAT",  "W" ) ;                                                                     
    	        	recUpdCrnSchData.setField("YD_UP_WO_LOC",   sRTN_BED.substring(0,8)) ;                                                     
    	        	recUpdCrnSchData.setField("YD_UP_WO_LAYER", sRTN_BED.substring(8,11)) ;                                                    
    	        	intRtnVal = this.Y5UpdYdCrnsch(recUpdCrnSchData, 0);                                                                       
    	        	if(intRtnVal == -1) {                                                                                                      
    	        		szMsg="크레인 스케줄 권상지시위치 등록 실패!";                                                                           
    	        		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);                                                    
    					outRecord.setField("RTN_CD" , "0");	                                                                                         
    					outRecord.setField("RTN_MSG", szMsg);	                                                                                       
    					return outRecord;                                                                                                            
                                                                                                                                           
    	        	}                                                                                                                          
   				                                                                                                                                 
    			}                                                                                                                                
        	}                                                                                                                                
        	                                                                                                                                 
        }catch(Exception e){                                                                                                               
			System.out.println(szMethodName + "Exception Error :"+ e.getLocalizedMessage());                                                     
			outRecord.setField("RTN_CD" , "0");	                                                                                                 
			outRecord.setField("RTN_MSG", szMsg);	                                                                                               
			return outRecord;                                                                                                                    
                                                                                                                                           
        }//end of try~catch                                                                                                                
                                                                                                                                           
                                                                                                                                           
		outRecord.setField("RTN_CD" , "1");	                                                                                                   
		outRecord.setField("RTN_MSG", sRTN_MSG);	                                                                                             
		return outRecord;                                                                                                                      
                                                                                                                                           
    	                                                                                                                                     
    }     
    
    /**
     * 오퍼레이션명 : 사용자지정 위치검색범위및위치검색Bed조회
     *  
     * @param  rsResultCrnwrkmtl, rsBed
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public JDTORecord Y5GetLocSrchRngCoilUser (JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch, JDTORecord recInTemp)  throws JDTOException {
    	//상위 Method : DnLocInsMainCoil
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
		//┗━┛
	
    	YdStkLyrDao     ydStkLyrDao     = new YdStkLyrDao();
    	YdCrnSchDao     ydCrnSchDao     = new YdCrnSchDao();
    	YdDBAssist      ydDBAssist      = new YdDBAssist();
    	YdStkBedDao     ydStkBedDao     = new YdStkBedDao();
    	YdWrkbookDao    ydWrkbookDao    = new YdWrkbookDao();
    	YdLocSrchRngDao ydLocSrchRngDao = new YdLocSrchRngDao();
    	YdEqpDao		ydEqpDao		= new YdEqpDao();
    	
    	CoilYdToLocDcsnUtil ydToLocDcsnUtil = new CoilYdToLocDcsnUtil();

    	JDTORecord recMinCrnwrkmtl = null;
    	JDTORecord recGetCrnWrkMtl = null;
    	JDTORecord recUpdCrnSchData = null;
	 	JDTORecord 		outRecord 	= JDTORecordFactory.getInstance().create();
	 	JDTORecord 		outRecord1 	= JDTORecordFactory.getInstance().create();
   	
    	String szStlNo      = "";
    	String szYdRouteGp  = "";
    	String szYdSchCd    = "";
    	String sRTN_BED     = "";   
    	
    	JDTORecordSet rsWbook = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
    	
    	
    	//권상지시위치정보
     	
    	String szYD_CAR_USE_GP = "";
    	String szTRN_EQP_CD = "";
    	String szCAR_NO = "";
    	String szCARD_NO = "";
    	String sYD_TO_LOC_GUIDE    = "";
    	String sYD_TO_LOC_DCSN_MTD = "";
    	String sYD_CRN_SCH_ID      = "";
    	String szEqpId = "";
    	JDTORecord recWbook           = null;
    	
    	int intRtnVal = 0;
    	String szMsg        = "";
    	String szMethodName = "Y5GetLocSrchRngCoilUser";
       	String sRTN_CD		= "";
		String sRTN_MSG		= "";
   	
    	
        try{
        	
        	//------------------------------------------------------------------------------
        	// 새로운 루틴 적용
        	
        	for(int Loop_i = 1; Loop_i <= rsCrnwrkmtl.size(); Loop_i++) {
        		rsCrnwrkmtl.absolute(Loop_i);
        		recMinCrnwrkmtl = rsCrnwrkmtl.getRecord();
        		
        		        		            	
        		szStlNo     		= ydDaoUtils.paraRecChkNull(recMinCrnwrkmtl, "STL_NO");
            	szYdSchCd   		= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD");
            	szYdRouteGp			= ydDaoUtils.paraRecChkNull(recMinCrnwrkmtl, "YD_AIM_RT_GP");
            	sYD_TO_LOC_GUIDE    = ydDaoUtils.paraRecChkNull(recInTemp, "YD_TO_LOC_GUIDE");
            	sYD_TO_LOC_DCSN_MTD = ydDaoUtils.paraRecChkNull(recInTemp, "YD_TO_LOC_DCSN_MTD");           	
            	sYD_CRN_SCH_ID		= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_CRN_SCH_ID");
            	szEqpId				= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_EQP_ID");
            	 
            	
            	intRtnVal = ydWrkbookDao.getYdWrkbook(recCrnSch, rsWbook, 0);
            	if(intRtnVal <= 0) {
        			if(intRtnVal == 0) {
        				szMsg="<Y5GetLocSrchRngCoilUser> getYdWrkbook data not found";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
        			}else if(intRtnVal == -1) {
        				szMsg="<Y5GetLocSrchRngCoilUser> getYdWrkbook duplicate data,";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
        			}else if(intRtnVal == -2) {
        				szMsg="<Y5GetLocSrchRngCoilUser> getYdWrkbook parameter error";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        			}else if(intRtnVal == -3){
        				szMsg="<Y5GetLocSrchRngCoilUser> getYdWrkbook execution failed";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        			}
    				outRecord.setField("RTN_CD" , "-1");	
    				outRecord.setField("RTN_MSG", szMsg);	
    				return outRecord;

        		}
            	rsWbook.absolute(1);
            	recWbook = JDTORecordFactory.getInstance().create();
            	recWbook.setRecord(rsWbook.getRecord());
            	szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recWbook, "YD_CAR_USE_GP");
            	szTRN_EQP_CD    = ydDaoUtils.paraRecChkNull(recWbook, "TRN_EQP_CD");
            	szCAR_NO        = ydDaoUtils.paraRecChkNull(recWbook, "CAR_NO");
            	szCARD_NO       = ydDaoUtils.paraRecChkNull(recWbook, "CARD_NO");
////////////////////////////////////////            	            	
//            	if(ydEqpDao.chkAutoCrn(szEqpId)){
//    				szMsg = "Auto 크레인 체크 시작:" ;
//        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        			outRecord1 = (JDTORecord)ydToLocDcsnUtil.procCoilYdToPosDecisionAuto(sYD_CRN_SCH_ID,szStlNo, szYdSchCd, szYdRouteGp, szYD_CAR_USE_GP, szTRN_EQP_CD, szCAR_NO, szCARD_NO,sYD_TO_LOC_DCSN_MTD,sYD_TO_LOC_GUIDE);
//    			}else{
    				szMsg = "일반 크레인 체크 시작:" ;
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				outRecord1 = (JDTORecord)ydToLocDcsnUtil.procCoilYdToPosDecision(sYD_CRN_SCH_ID,szStlNo, szYdSchCd, szYdRouteGp, szYD_CAR_USE_GP, szTRN_EQP_CD, szCAR_NO, szCARD_NO,sYD_TO_LOC_DCSN_MTD,sYD_TO_LOC_GUIDE,szEqpId);
//    			}
////////////////////////////////////////           	 	
            	
            	sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            	sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				sRTN_BED	= StringHelper.evl(outRecord1.getFieldString("RTN_BED"), "");
				
				ydUtils.putLog(szSessionName, szMethodName, "3" +sRTN_CD +sRTN_MSG +sRTN_BED, YdConstant.DEBUG);
        	 	
				if ("0".equals(sRTN_CD)) {
        			szMsg = "<Y5GetLocSrchRngCoilUser> To위치평점실패";
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				outRecord.setField("RTN_CD" , "0");	
    				outRecord.setField("RTN_MSG", sRTN_MSG);	
    				return outRecord;
				
				}
				if (!("1".equals(sRTN_CD))) {
        			szMsg = "<Y5GetLocSrchRngCoilUser> To위치평점실패";
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				outRecord.setField("RTN_CD" , sRTN_CD);	
    				outRecord.setField("RTN_MSG", sRTN_MSG);	
    				ydUtils.putLog(szSessionName, szMethodName, "4" +sRTN_CD +sRTN_MSG, YdConstant.DEBUG);
    				return outRecord;
				
				}
				if ("".equals(sRTN_BED)) {
        			szMsg = "<Y5GetLocSrchRngCoilUser> 사용자 정의  To위치검색 중 ERROR 발생";
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
 //sjh   				outRecord.setField("RTN_CD" , "0");	
       				outRecord.setField("RTN_CD" , "-1");	
       		    	outRecord.setField("RTN_MSG", sRTN_MSG);	
    				ydUtils.putLog(szSessionName, szMethodName, "5" +sRTN_CD +sRTN_MSG, YdConstant.DEBUG);
    				return outRecord;
				
				}

        		
        		recGetCrnWrkMtl = JDTORecordFactory.getInstance().create();
        		recGetCrnWrkMtl.setField("STL_NO",       		szStlNo);
        		recGetCrnWrkMtl.setField("YD_STK_COL_GP",       sRTN_BED.substring(0,6));
        		recGetCrnWrkMtl.setField("YD_STK_BED_NO",       sRTN_BED.substring(6,8));
        		recGetCrnWrkMtl.setField("YD_STK_LYR_NO",       sRTN_BED.substring(8,11));
        		recGetCrnWrkMtl.setField("YD_STK_LYR_MTL_STAT", "D");
	        	intRtnVal = ydStkLyrDao.updYdStklyr(recGetCrnWrkMtl, 0);
	        	if(intRtnVal <= 0) {
	        		if(intRtnVal == 0) {
	        			szMsg="<Y5GetLocSrchRngCoilUser> updYdStklyr data not found";
	        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

	        		}else if(intRtnVal == -2) {
	        			szMsg="<Y5GetLocSrchRngCoilUser> updYdStklyr parameter error";
	        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

	        		}
	        		System.out.println("적치단에 저장하지 못했습니다.");
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;

	        	}
	
	
	        	//크레인 스케줄  권하지시위치 업데이트
	        	recUpdCrnSchData = JDTORecordFactory.getInstance().create();
	        	recUpdCrnSchData.setField("YD_CRN_SCH_ID",  recCrnSch.getFieldString("YD_CRN_SCH_ID") ) ;

	        	recUpdCrnSchData.setField("YD_WRK_PROG_STAT",  "W" ) ;
	        	recUpdCrnSchData.setField("YD_DN_WO_LOC",   sRTN_BED.substring(0,8)) ;
	        	recUpdCrnSchData.setField("YD_DN_WO_LAYER", sRTN_BED.substring(8,11)) ;
	        	intRtnVal = this.Y5UpdYdCrnsch(recUpdCrnSchData, 0);
	        	if(intRtnVal == -1) {
	        		szMsg="크레인 스케줄 권하지시위치 등록 실패!";
	        		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;

	        	}
	
        	}
        	
 
        }catch(Exception e){
			System.out.println("<Y5GetLocSrchRngCoilUser> Exception Error :"+ e.getLocalizedMessage());
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;

        }//end of try~catch
        
        
		outRecord.setField("RTN_CD" , sRTN_CD);	
		outRecord.setField("RTN_MSG", sRTN_MSG);	
		return outRecord;

    	
    }//end of Y5GetLocSrchRngCoil()

    /**
     * 오퍼레이션명 : 사용자 지정위치
     * 열단위 이적요구시에도 사용함 
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
   
     public JDTORecord Y5GetUsrAppLoc(JDTORecord inRecord, JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch, JDTORecord recInTemp) throws JDTOException {
    	//상위 Method : DnLocInsMain
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
		//┗━┛
    	
    	YdStkBedDao    ydStkBedDao    = new YdStkBedDao();
    	YdCrnSchDao    ydCrnSchDao    = new YdCrnSchDao(); 
    	YdStkLyrDao    ydStkLyrDao    = new YdStkLyrDao();
    	
	 	JDTORecord 		outRecord 	= JDTORecordFactory.getInstance().create();
   	
    	//적치Bed를 조회한 정보
    	JDTORecordSet rsGetStkBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	//적치단을 조회한 정보
    	JDTORecord recStkLyr = null;
    	JDTORecordSet rsGetStkLyr = null;
    	//크레인작업재료 정보
    	JDTORecordSet rsResultCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
    	JDTORecordSet outRecSet = null;
    	JDTORecord recResultCrnwrkmtl = null;
    	JDTORecord recReturnData = null;
    	
    	JDTORecord recGetCrnWrkMtl = null;
    	
    	//파라미터 rsBed를 조회
    	JDTORecord recStkBed      = null;
    	//적치Bed를 조회한 정보
    	JDTORecord recGetRsSet    = null;
    
    	JDTORecordSet rsResult = null;
    	
    	
    	int intRtnVal 		= 0 ;
        String szMsg        = "";
        String szMethodName = "Y5GetUsrAppLoc";
        String szOperationName = "사용자 지정위치";
        //적치베드의 Max단,중량,높이
        int intStkBedLyrMax        = 0;
        long lngStkBedWtMax        = 0;
        double lngStkBedHMax         = 0;
        
        //크레인작업재료의 총매수,중량,높이
        int intCrnWrkMtlSh         = 0;
        long lngCrnWrkMtlWt        = 0;
        double lngCrnWrkMtlT         = 0;
        
        //적치단의 적치중인 재료의 총매수,중량,높이
        int intStkLyrMax           = 0;
        long lngStkLyrWtMax 	   = 0;
        double lngStkLyrHMax         = 0;
        
        //크레인작업재료의 최하단 산적LotType,산적Lot코드,폭
        String szCrnWrkMtlStkLotTp = "";
        String szCrnWrkMtlStkLotCd = "";
        long lngCrnWrkMtlW		   = 0;
        
        //적치단의 최상단 산적LotType,산적Lot코드,폭
        String szStkLyrStkLotTp    = "";
        String szStkLyrStkLotCd    = "";
        double lngStkLyrW		       = 0;
        
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
        
        String szQuery     = "";
        String szCrnSchId  = "";
        String szYD_EQP_ID = "";
        String szYD_SCH_CD = "";
        
        String szUpWoLoc = null;
        String szUpWoLocLayer = null;
        
        
        try{
        	
        	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        	 * 업무기준 : 권상지시위치정보가 없을 경우는 이전 크레인 스케줄의 작업재료들 중
        	 * 			권상대기, 권하대기인 재료의 위치정보를 가져와서 권상지시위치로 사용한다.
        	 * 수정자 : 임춘수
        	 * 수정일 : 2009.07.30
        	 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        	szUpWoLoc = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");
        	
	        if( szUpWoLoc.equals("") ) {
	        	szMsg="[사용자 지정위치]권상지시위치 존재하지 않으므로 위치정보 조회";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        	//크레인 작업재료의 최하단 재료정보를 가져온다.
	        	rsCrnwrkmtl.absolute(1);
	        	recResultCrnwrkmtl = JDTORecordFactory.getInstance().create();
	        	
	        	recResultCrnwrkmtl.setRecord(rsCrnwrkmtl.getRecord());
	        	
	        	//가져온 최하단 재료 정보의 적치단 위치중 권하 대기인 위치정보를 가져온다.
	        	recResultCrnwrkmtl.setField("YD_STK_LYR_MTL_STAT", "D");
	
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
	        	recReturnData = JDTORecordFactory.getInstance().create();
	        	recReturnData.setRecord(outRecSet.getRecord());
	     
				//최하단 재료의 적치단 위치
	        	szUpWoLoc = recReturnData.getFieldString("YD_STK_COL_GP") + recReturnData.getFieldString("YD_STK_BED_NO"); 
	        	szUpWoLocLayer = recReturnData.getFieldString("YD_STK_LYR_NO");
	        	
	        	szMsg="[사용자 지정위치]새로 조회된 권상지시위치 - " + szUpWoLoc + ", 단 - " + szUpWoLocLayer;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        	
	        	recCrnSch.setField("YD_UP_WO_LOC", szUpWoLoc);
	        	recCrnSch.setField("YD_UP_WO_LAYER", szUpWoLocLayer);
	        	////recCrnSch.setField("REG_DDTT", null);
	        	
	        	intRtnVal = ydCrnSchDao.updYdCrnsch(recCrnSch, 0);
	    		if(intRtnVal <= 0) {
	    			if(intRtnVal == 0) {
	    				szMsg="[사용자 지정위치]updYdCrnsch : 권상지시위치업데이트 - data not found";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
	    			}else if(intRtnVal == -1) {
	    				szMsg="[사용자 지정위치]updYdCrnsch : 권상지시위치업데이트- duplicate data,";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
	    			}else if(intRtnVal == -2) {
	    				szMsg="[사용자 지정위치]updYdCrnsch : 권상지시위치업데이트- parameter error";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -3){
	    				szMsg="[사용자 지정위치]updYdCrnsch : 권상지시위치업데이트- execution failed";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;

	    		}
	        }else{
	        	szMsg="[사용자 지정위치]권상지시위치[" + szUpWoLoc + "]가 존재함";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        }
        	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        	
        	//크레인 작업재료 조회
        	szCrnSchId = recCrnSch.getFieldString("YD_CRN_SCH_ID");
        	szYD_SCH_CD = recCrnSch.getFieldString("YD_SCH_CD");

        	
        	rsResultCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("");
        	intRtnVal = ydCrnSchDao.getYdCrnsch(recCrnSch, rsResultCrnwrkmtl, 29);
    		if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="Y5GetUsrAppLoc getYdCrnsch : data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
    			}else if(intRtnVal == -2) {
    				szMsg="Y5GetUsrAppLoc getYdCrnsch : parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;

     		}
        	
        	
        	
    		//열구분과 베드번호를 읽어온다.
			ydUtils.putLog(szSessionName, szMethodName, "YD_TO_LOC_GUIDE:"+ydDaoUtils.paraRecChkNull(recInTemp, "YD_TO_LOC_GUIDE"), YdConstant.INFO);

			szStkColGp = ydDaoUtils.paraRecChkNull(recInTemp, "YD_TO_LOC_GUIDE").substring(0,6);
 
    		szStkBedNo = ydDaoUtils.paraRecChkNull(recInTemp, "YD_TO_LOC_GUIDE").substring(6,8);
    		
    		recStkBed = JDTORecordFactory.getInstance().create();
    		recStkBed.setField("YD_STK_COL_GP", szStkColGp);
    		recStkBed.setField("YD_STK_BED_NO", szStkBedNo);
    		
    		//적치Bed조회한다.
    		rsGetStkBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    		intRtnVal = ydStkBedDao.getYdStkbed(recStkBed, rsGetStkBed, 0) ;
    		if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="<Y5GetUsrAppLoc> getYdStkbed data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
    			}else if(intRtnVal == -2) {
    				szMsg="<Y5GetUsrAppLoc> getYdStkbed parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
				szMsg="다음 적치BED를 조회합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;

    		}
    		
    		//적치베드의 Max단,중량,높이
    		rsGetStkBed.first();
    		recGetRsSet = rsGetStkBed.getRecord();
        		
        		

ydUtils.displayRecord(szOperationName, recGetRsSet);
        		
        	intStkBedLyrMax = ydDaoUtils.paraRecChkNullInt (recGetRsSet,"YD_STK_BED_LYR_MAX");
    		lngStkBedWtMax  = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"YD_STK_BED_WT_MAX");       
    		lngStkBedHMax   = ydDaoUtils.paraRecChkNullDouble(recGetRsSet,"YD_STK_BED_H_MAX"); 
    		if(intStkBedLyrMax <= 0) {
				szMsg="적치Bed정보에 최대적치단의 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    		}else if(lngStkBedWtMax <= 0){
				szMsg="적치Bed정보에 최대중량의 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    		}else if(lngStkBedHMax <= 0){
				szMsg="적치Bed정보에 최대높이의 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    		}
    		
szMsg="TEST MESSAGE 1";
ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		rsCrnwrkmtl.absolute(1);
    		recGetCrnWrkMtl = JDTORecordFactory.getInstance().create();
    		recGetCrnWrkMtl.setRecord(rsCrnwrkmtl.getRecord());
    		
szMsg="TEST MESSAGE 2";
ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
ydUtils.displayRecord(szOperationName, recGetCrnWrkMtl);

    		//크레인작업재료의 총매수 총중량 총높이를 가져온다. 		
    		intCrnWrkMtlSh      = ydDaoUtils.paraRecChkNullInt (recGetCrnWrkMtl,"SH_CNT");
    		lngCrnWrkMtlWt      = ydDaoUtils.paraRecChkNullLong(recGetCrnWrkMtl,"SUM_MTL_WT");
    		lngCrnWrkMtlT       = ydDaoUtils.paraRecChkNullDouble(recGetCrnWrkMtl,"SUM_MTL_T");
    		

    		//크레인작업재료의 최하단 재료정보
    		//lngCrnWrkMtlW       = ydDaoUtils.paraRecChkNullLong(recGetCrnWrkMtl,"SUM_MTL_W");
    		szCrnWrkMtlStkLotTp = ydDaoUtils.paraRecChkNull    (recGetCrnWrkMtl,"YD_STK_LOT_TP");
    		szCrnWrkMtlStkLotCd = ydDaoUtils.paraRecChkNull    (recGetCrnWrkMtl,"YD_STK_LOT_CD");

    		
			//적치Bed의 적치중이거나 권하대기상태인 재료정보를 가져온다.
			//적치단의 재료의 합계정보
			recStkLyr = JDTORecordFactory.getInstance().create();
			recStkLyr.setField("YD_STK_COL_GP", szStkColGp);
			recStkLyr.setField("YD_STK_BED_NO", szStkBedNo);
			
			rsGetStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
			intRtnVal = ydStkLyrDao.getYdStklyr(recStkLyr, rsGetStkLyr, 6);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<Y5GetUsrAppLoc> getYdStklyr data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					outRecord.setField("RTN_CD" , "-1");	
					outRecord.setField("RTN_MSG", "적치단이 조회가 되지 않았습니다");	
					return outRecord;
				}else if(intRtnVal == -2) {
					szMsg="<Y5GetUsrAppLoc> getYdStklyr parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "-1");	
					outRecord.setField("RTN_MSG", "적치단이 조회가 되지 않았습니다");	
					return outRecord;
				}
				System.out.println("적치단이 조회가 되지 않았습니다");
			}
			
			
			if(intRtnVal == 0) {
				System.out.println("공베드입니다.");
			}
			ydUtils.putLog(szSessionName, szMethodName, szStkColGp+szStkBedNo, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, ""+ rsGetStkLyr.size(), YdConstant.INFO);
//C증설			
//보급존 check 안함			
			if(rsGetStkLyr.size() > 0) {
				if(		(szStkColGp+szStkBedNo).equals("HBKE0402") ||
						(szStkColGp+szStkBedNo).equals("HAKE0502") ||
						(szStkColGp+szStkBedNo).equals("HBFE0502") ||   
						(szStkColGp+szStkBedNo).equals("HCKE0302") ||
						(szStkColGp+szStkBedNo).equals("HCFE0402") ||
						(szStkColGp+szStkBedNo).equals("HDFE0202") ||
						(szStkColGp+szStkBedNo).equals("HEDE0102") ||
						(szStkColGp+szStkBedNo).equals("HFFE0202") ||
						(szStkColGp+szStkBedNo).equals("HGFE0102") ||
						(szStkColGp+szStkBedNo).equals("HHKE0102") 
						){
				
	    			recGetRsSet = JDTORecordFactory.getInstance().create();
	    			recGetRsSet.setField("YD_STK_LOT_TP", "");
	    			recGetRsSet.setField("YD_STK_LOT_CD", "");
	    			recGetRsSet.setField("YD_STK_COL_GP", szStkColGp);
	    			recGetRsSet.setField("YD_STK_BED_NO", szStkBedNo);
	    			
	    			intStkLyrMax = 0;
	    			lngStkLyrWtMax = 0;
	    			lngStkLyrHMax = 0;
	    			lngStkLyrW = 0;
	    			szStkLyrStkLotTp = "";
	    			szStkLyrStkLotCd = "";
					
				
				} else {
					
					rsGetStkLyr.absolute(1);
					recGetRsSet = JDTORecordFactory.getInstance().create();
	        		recGetRsSet.setRecord(rsGetStkLyr.getRecord());
	        		
	        		//적치단에적치중인 총매수 중량 높이
	        		intStkLyrMax     = ydDaoUtils.paraRecChkNullInt (recGetRsSet,"SH_CNT");
	       			lngStkLyrWtMax   = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"SUM_MTL_WT");
	       			lngStkLyrHMax    = ydDaoUtils.paraRecChkNullDouble(recGetRsSet,"SUM_MTL_T");
	       			
	       			//적치단의최상단  재료정보
	       			lngStkLyrW       = ydDaoUtils.paraRecChkNullDouble(recGetRsSet,"SUM_MTL_W");
	       			szStkLyrStkLotTp = ydDaoUtils.paraRecChkNull    (recGetRsSet,"YD_STK_LOT_TP");
	       			szStkLyrStkLotCd = ydDaoUtils.paraRecChkNull    (recGetRsSet,"YD_STK_LOT_CD");
				} 	

    		}else{
    			recGetRsSet = JDTORecordFactory.getInstance().create();
    			recGetRsSet.setField("YD_STK_LOT_TP", "");
    			recGetRsSet.setField("YD_STK_LOT_CD", "");
    			recGetRsSet.setField("YD_STK_COL_GP", szStkColGp);
    			recGetRsSet.setField("YD_STK_BED_NO", szStkBedNo);
    			
    			intStkLyrMax = 0;
    			lngStkLyrWtMax = 0;
    			lngStkLyrHMax = 0;
    			lngStkLyrW = 0;
    			szStkLyrStkLotTp = "";
    			szStkLyrStkLotCd = "";
    			
    			
//        			//공베드인경우에 적치단에 등록하기위해 베드정보까진 저장...
//        			szGradeColGp = szStkColGp;
//        			szGradeBedNo = szStkBedNo;
//        			szGradeLyrNo = "";
    			
    			System.out.println("공베드일경우 DATA저장.");
    			System.out.println("szGradeColGp :" + szGradeColGp);
    			System.out.println("szGradeBedNo :" + szGradeBedNo);
    			System.out.println("szGradeLyrNo :" + szGradeLyrNo);
    		}
   			
			
			
   			System.out.println(intStkBedLyrMax + " < " +  intCrnWrkMtlSh + " + " + intStkLyrMax);
   			System.out.println(lngStkBedWtMax + " < " +  lngCrnWrkMtlWt + " + " + lngStkLyrWtMax);
   			System.out.println(lngStkBedHMax + " < " +  lngCrnWrkMtlT + " + " + lngStkLyrHMax);
   			
   			
   			
   			if(intStkBedLyrMax < intCrnWrkMtlSh + intStkLyrMax) {
   				szMsg="MAX단 초과";
   				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
   			}else if(lngStkBedWtMax  < lngCrnWrkMtlWt + lngStkLyrWtMax) { 
   				szMsg="MAX중량 초과";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
    		}else if(lngStkBedHMax   < lngCrnWrkMtlT  + lngStkLyrHMax) {
   				szMsg="MAX높이 초과";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
    		}
   		
       			
   			rsResultCrnwrkmtl.absolute(1);
   			recResultCrnwrkmtl = JDTORecordFactory.getInstance().create();
   			recResultCrnwrkmtl.setRecord(rsResultCrnwrkmtl.getRecord());
        	//적치단 등록               크레인 작업재료                적치단 정보       등급
			intRtnVal = this.Y5UpdGradStkLyrCoil(recResultCrnwrkmtl, recGetRsSet);	
			if(intGrade == -1) {
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
			}
			
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			szMsg="<Y5GetUsrAppLoc> Error : "+ e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;
       }//end of try~catch
		
		outRecord.setField("RTN_CD" , "1");	
		outRecord.setField("RTN_MSG", szMsg);	
		return outRecord;
		
    }// end of Y5GetUsrAppLoc 
    
	
	
 	/**
 	 * 오퍼레이션명 : C열연 크레인스케줄수행조건판단(H/J)
 	 *  
 	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
 	 * @param szEqpId, szSchCd, rsResultRt
 	 * @return 1: 성공, -1: 실패
 	 * @throws JDTOException
 	 */
 	public JDTORecord Y5ChkCrnSchEffectConditionCoil(JDTORecord msgRecord, JDTORecordSet rsResultRt) throws JDTOException  {
 		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();
 		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
 		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
 		YdStkLyrDao  ydStkLyrDao  = new YdStkLyrDao();
 		//JDTORecord recSetWrkbookmtl = null;
 		JDTORecord recInTemp        = null;
 		JDTORecord recInTemp1        = null;
 		JDTORecordSet rsWrkbookmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
 		
 		
 		
 		JDTORecord recParaSch  = null;
 		JDTORecord recSch      = null;
 		JDTORecord recWrkkbook      = null;
 		JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
 		
 		JDTORecord recSchCd   = null;
 		JDTORecordSet rsSchCd = JDTORecordFactory.getInstance().createRecordSet("Temp");
 		
 		JDTORecord recInPara = null;
 		
 		String szMsg        = "";
 		String szMethodName = "Y5ChkCrnSchEffectConditionCoil";
 		String szOperationName = "C열연크레인스케줄수행조건판단";
 		String szYD_WBOOK_ID      = "";
 		
 		String szYdGp       = "";
 		String szYdBayGp    = "";
 		
 		String szEqpId = null;
 		String szSchCd = null;
 		
 		JDTORecord outRecord  = JDTORecordFactory.getInstance().create();
 		
 		//true false 체크
 		boolean bRtnCheck   = true;

 		int intRtnVal       = 0;
 		String szRtnVal     = "";

 		try{
 			
 			/*
 			 * 업무기준 : 
 			 * 		1. 크레인스케줄코드, 작업예약ID없이 크레인설비ID만 넘어오는 경우
 			 * 			-> 해당크레인설비ID로 만들어진 크레인스케줄금지가 되지 않고 크레인우선순위가 가장빠른 작업예약들 중에서
 			 * 			가장빠른 작업예약을 하나 조회해서 작업 진행.
 			 * 		2. 작업예약ID없이 크레인스케줄코드, 크레인설비ID만 넘어오는 경우
 			 * 			-> 크레인스케줄코드로 크레인스케줄이 생성되지 않은 작업예약들 중에서 가장빠른 작업예약을 하나 조회해서 작업 진행.
 			 * 		3. 크레인스케줄, 크레인설비ID, 작업예약ID가 모두 넘어오는 경우
 			 * 			-> 해당작업예약ID로 직접 조회를 해서 작업진행 - 차량도착인 경우
 			 */
 			szEqpId = ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID");
 			szSchCd = ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD");
 			szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(msgRecord,"YD_WBOOK_ID");
 			
 			szMsg = "[" + szOperationName + "] 메소드시작 파라미터확인 : 크레인설비ID["+szEqpId+"], 크레인스케줄코드["+szSchCd+"], 작업예약ID["+szYD_WBOOK_ID+"]";
 			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

 			if(szSchCd.equals("")){
 				szMsg = "[" + szOperationName + "] 크레인설비ID["+szEqpId+"]만 넘어온 경우에 작업예약 조회 시작";
 				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
 				//해당크레인설비ID로 만들어진 크레인스케줄금지가 되지 않고 크레인우선순위가 가장빠른 작업예약들 중에서
 				//가장빠른 작업예약을 하나 조회
 				rsSchCd = JDTORecordFactory.getInstance().createRecordSet("");
 				recInPara = JDTORecordFactory.getInstance().create();
 				recInPara.setField("YD_EQP_ID",     szEqpId);
 				
 				//com.inisteel.cim.yd.dao.ydWrkbookDao.getCrnSchNONE
 				intRtnVal = ydWrkbookDao.getYdWrkbook(recInPara, rsSchCd, 4);
 				if(intRtnVal<=0){
 	    			if(intRtnVal == 0) {
 	    				szMsg = "[" + szOperationName + "] 크레인설비ID["+szEqpId+"]만 넘어온 경우에 작업예약 조회 시 data not found";
 	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 	    			}else if(intRtnVal == -2) {
 	    				szMsg = "[" + szOperationName + "] 크레인설비ID["+szEqpId+"]만 넘어온 경우에 작업예약 조회 시 parameter error";
 	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 	    			}
 					szMsg = "[" + szOperationName + "] 크레인설비ID["+szEqpId+"]만 넘어온 경우에 작업예약 조회 시 오류발생 - 반환값 : " + intRtnVal;
 					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

 					outRecord.setField("RTN_CD", "0");						
 					outRecord.setField("RTN_MSG", szMsg);						
 					return outRecord;
 				}
 				
 				rsSchCd.absolute(1);
 				recSchCd = rsSchCd.getRecord();
 				szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(recSchCd, "YD_WBOOK_ID");
 				
 				//지정된 작업예약ID로 작업예약재료들이 존재하는 BED정보와 대상재가 존재하는 해당BED의 최하단정보를 가져온다.
 				szMsg = "[" + szOperationName + "] 크레인설비ID["+szEqpId+"]만 넘어온 경우에 작업예약ID["+szYD_WBOOK_ID+"]로 작업예약재료가 존재하는 BED정보를 조회";
 				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
 				rsWrkbookmtl = JDTORecordFactory.getInstance().createRecordSet("");
 				recInTemp = JDTORecordFactory.getInstance().create();
 				recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
 				
 				//com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookNONE
 				intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsWrkbookmtl, 401);
 				
 				if(intRtnVal<=0){
 	    			if(intRtnVal == 0) {
 	    				szMsg = "[" + szOperationName + "] 크레인설비ID["+szEqpId+"]만 넘어온 경우에 작업예약ID["+szYD_WBOOK_ID+"]로 작업예약재료가 존재하는 BED정보를 조회 시 data not found";
 	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 	    			}else if(intRtnVal == -2) {
 	    				szMsg = "[" + szOperationName + "] 크레인설비ID["+szEqpId+"]만 넘어온 경우에 작업예약ID["+szYD_WBOOK_ID+"]로 작업예약재료가 존재하는 BED정보를 조회 시 parameter error";
 	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 	    			}
 					szMsg = "[" + szOperationName + "] 크레인설비ID["+szEqpId+"]만 넘어온 경우에 작업예약ID["+szYD_WBOOK_ID+"]로 작업예약재료가 존재하는 BED정보를 조회 시 오류발생 - 반환값 : " + intRtnVal;
 					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 				
 					outRecord.setField("RTN_CD", "0");						
 					outRecord.setField("RTN_MSG", szMsg);						
 					return outRecord;
 				}
 				
 			} else {
 				szMsg = "[" + szOperationName + "] 크레인설비ID["+szEqpId+"], 크레인스케줄코드["+szSchCd+"], 작업예약["+szYD_WBOOK_ID+"] 넘어온 경우에 작업예약 조회 시작";
 				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
 				
 				szMsg = "[" + szOperationName + "] 크레인스케줄코드["+szSchCd+"]로 스케줄기준 조회 시작";
 				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
 				//조회항목 record 생성
 				recParaSch = JDTORecordFactory.getInstance().create();
 				
 				//스케줄코드
 				recParaSch.setField("YD_SCH_CD", szSchCd);

 				//스케줄코드로 스케줄기준 Table 조회
 				intRtnVal = ydSchRuleDao.getYdSchrule(recParaSch, rsResult, 0);
 				if(intRtnVal<=0){
 	    			if(intRtnVal == 0) {
 	    				szMsg = "[" + szOperationName + "] 크레인스케줄코드["+szSchCd+"]로 스케줄기준 조회 시 data not found";
 	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 	    			}else if(intRtnVal == -2) {
 	    				szMsg = "[" + szOperationName + "] 크레인스케줄코드["+szSchCd+"]로 스케줄기준 조회 시 parameter error";
 	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 	    			}
 					szMsg = "[" + szOperationName + "] 크레인스케줄코드["+szSchCd+"]로 스케줄기준 조회 시 오류발생 - 반환값 : " + intRtnVal;
 					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 					outRecord.setField("RTN_CD", "0");						
 					outRecord.setField("RTN_MSG", szMsg);						
 					return outRecord;
 				}
 			
 				rsResult.absolute(1);
 				recSch = rsResult.getRecord();
 				
 				if(recSch.getFieldString("YD_SCH_PROH_EXN").equals("Y")) {
 					szMsg = "[" + szOperationName + "] 크레인스케줄코드["+szSchCd+"]는 스케줄 금지 상태입니다.";
 					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 					//throw new DAOException(szMsg);
 					outRecord.setField("RTN_CD", "0");						
 					outRecord.setField("RTN_MSG", szMsg);						
 					return outRecord;
 				}

//SJH 추가 
//대차 작업일 경우 대차 이동가능 위치를 CHECK하여 리턴 처리 함
 				if((szSchCd.substring(2, 4).equals("TC")) && (szSchCd.substring(6, 7).equals("U"))){
 					recInPara = JDTORecordFactory.getInstance().create();
 	 				recInPara.setField("YD_WBOOK_ID",     szYD_WBOOK_ID);
 	 				
 	 				//com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookTCarArrCoil
 	 				intRtnVal = ydWrkbookDao.getYdWrkbook(recInPara, rsSchCd, 303);
 	 				if(intRtnVal<=0){
 	 					szMsg = "[" + szOperationName + "] 크레인스케줄코드["+szSchCd+"]는 대차 이동 범위에 포함되지 않습니다..";
 	 					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 	 					//throw new DAOException(szMsg);
 	 					outRecord.setField("RTN_CD", "0");						
 	 					outRecord.setField("RTN_MSG", szMsg);						
 	 					return outRecord;
 	 				}
 				}
 				rsWrkbookmtl = JDTORecordFactory.getInstance().createRecordSet("");
 				recInTemp = JDTORecordFactory.getInstance().create();
 				if(szYD_WBOOK_ID.equals("")) {
 					szMsg = "[" + szOperationName + "] 작업예약ID없이 스케줄코드["+szSchCd+"]로 작업예약의 작업예약재료가 존재하는 BED정보를 조회";
     				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

     				recInTemp.setField("YD_SCH_CD", szSchCd);
 					//작업예약ID없이 스케줄코드로 작업예약의 작업예약재료가 존재하는 BED정보를 조회"
     				/*com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookSCHCDCoil1Row*/
     				intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsWrkbookmtl, 404);
 				}else{
 					////작업예약ID로 스케줄코드로 작업예약의 작업예약재료가 존재하는 BED정보를 조회"
 					szMsg = "[" + szOperationName + "] 작업예약ID["+szYD_WBOOK_ID+"]으로  작업예약재료가 존재하는 BED정보를 조회 ";
     				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
 					recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
 					intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsWrkbookmtl, 401);
 				}

 				if(intRtnVal<=0){
 	    			if(intRtnVal == 0) {
 	    				szMsg = "[" + szOperationName + "] 작업예약재료가 존재하는 BED정보를 조회 시 data not found";
 	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 	    			}else if(intRtnVal == -2) {
 	    				szMsg = "[" + szOperationName + "] 작업예약재료가 존재하는 BED정보를 조회 시 parameter error";
 	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 	    			}
 					szMsg = "[" + szOperationName + "] 작업예약재료가 존재하는 BED정보를 조회 시 오류발생 - 반환값 : " + intRtnVal;
 					
 					outRecord.setField("RTN_CD", "0");						
 					outRecord.setField("RTN_MSG", szMsg);						
 					return outRecord;
 				}
 			}//end of if
 			
 			szMsg = "[" + szOperationName + "] 크레인설비ID["+szEqpId+"], 크레인스케줄코드["+szSchCd+"], 작업예약ID["+szYD_WBOOK_ID+"]로 작업예약재료 BED정보 조회 완료 - 건수 : " + rsWrkbookmtl.size();
 			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
 			
 			rsWrkbookmtl.absolute(1);
 			//===============================================================================================================
 			/*
 			 * 2009.10.08 김진욱추가
 			 * 추가내용 : 스케줄 기동시 작업예약재료의 적치열구분, 적치베드, 적치단정보를 현재 재료의 위치를 재조회하여 다시 UPDATE한다.
 			 * 만약 적치중인 재료가 아닌 권상대기 및 권하대기인 재료들은 검색을 하지않는다.
 			 * 타 크레인 스케줄에 잡혀있는 경우에는 적치중인 재료로 조회되지 않기때문에 종료처리를 하고 스케줄 기동을 하지않도록한다. 
 			 */
 			//작업예약재료의 현재 위치를 재조회해서 업데이트한다.
 			recInTemp = JDTORecordFactory.getInstance().create();
 			recInTemp.setRecord(rsWrkbookmtl.getRecord());
 			JDTORecordSet rsWrkbookmtl2 = JDTORecordFactory.getInstance().createRecordSet("");
 			
 			intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsWrkbookmtl2, 19);

//SJH 추가
//C증설 			
 			if((szSchCd.equals("HBKD01LM"))||(szSchCd.equals("HBKD02LM"))||(szSchCd.equals("HBKD05LM"))||
 			   (szSchCd.equals("HAKD01LM"))||(szSchCd.equals("HAKD02LM"))||(szSchCd.equals("HAKD05LM"))||
 			   (szSchCd.equals("HBFD01LM"))||(szSchCd.equals("HBFD02LM"))||(szSchCd.equals("HBFD04LM"))||
 		 	   
 		 	   (szSchCd.equals("JBKD01LM"))||//(szSchCd.equals("JBFD01LM"))||
 		 	   (szSchCd.equals("JAKD01LM"))||
 		 	   //(szSchCd.equals("JBTC01MM"))||(szSchCd.equals("JBTC02MM"))||
 		 	   (szSchCd.equals("JBTC05MM"))||
 		 	   (szSchCd.equals("JATC05MM"))||
	 		   (szSchCd.equals("HCKD01LM"))||(szSchCd.equals("HCKD02LM"))||(szSchCd.equals("HCFD01LM"))||
	 		   (szSchCd.equals("HCFD02LM"))||(szSchCd.equals("HCFD04LM"))||(szSchCd.equals("HCKD05LM"))||

	 		   (szSchCd.equals("JCKD01LM"))||(szSchCd.equals("JCFD01LM"))||
	 		   (szSchCd.equals("JCTC01MM"))||(szSchCd.equals("JCTC02MM"))||(szSchCd.equals("JCTC05MM"))||
	 		   
	 		   (szSchCd.equals("HEDD02LM"))||(szSchCd.equals("HEDD01LM"))||(szSchCd.equals("JEDD01LM"))||
 			   (szSchCd.equals("JETC01MM"))||(szSchCd.equals("JETC02MM"))||(szSchCd.equals("HEDD05LM"))||
			   
 			   (szSchCd.equals("HGFD01LM"))||(szSchCd.equals("HGFD02LM"))||(szSchCd.equals("HGFD04LM"))||
			   (szSchCd.equals("JGFD01LM"))||(szSchCd.equals("JGTC01MM"))||(szSchCd.equals("JGTC02MM"))||

 			   (szSchCd.equals("HHKD01LM"))||(szSchCd.equals("HHKD02LM"))||(szSchCd.equals("JHKD01LM"))|| 
	 		   (szSchCd.equals("JHTC01MM"))||(szSchCd.equals("JHTC02MM"))||(szSchCd.equals("HHKD05LM")) 
	 		   ){ 

 				rsWrkbookmtl = JDTORecordFactory.getInstance().createRecordSet("");
 	 			recInTemp = JDTORecordFactory.getInstance().create();
 	 			recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
 	 			/*com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookNONECOIL*/
				intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsWrkbookmtl, 401);
				if(intRtnVal <= 0) {
 					szMsg="작업예약에 해당 정보가 없습니다..";
 					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
  					outRecord.setField("RTN_CD", "0");						
 					outRecord.setField("RTN_MSG", szMsg);						
 					return outRecord;
 				}
 	 			rsResultRt.addAll(rsWrkbookmtl); 
 	 			outRecord.setField("RTN_CD", "1");						
 	 			outRecord.setField("YD_SCH_CD", szSchCd);						
 	 			outRecord.setField("RTN_MSG", szMsg);						
 	 			return outRecord;
 				
 			} 	
 			
 			//작업예약재료에 현 재료의 위치정보를 업데이트한다.
 			szMsg="<Y5ChkCrnSchEffectConditionCoil> 작업예약재료에 현 재료의 위치정보를 UPDATE!!!";
 			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
 			for(int Loop_i=1; Loop_i <= rsWrkbookmtl2.size(); Loop_i++){
 				rsWrkbookmtl2.absolute(Loop_i);
 				JDTORecord recOutTemp = JDTORecordFactory.getInstance().create();
 				recOutTemp.setRecord(rsWrkbookmtl2.getRecord());
//C증설				
  				if((szSchCd.equals("HBKD01LM"))||(szSchCd.equals("HBKD02LM"))||(szSchCd.equals("HBKD03LM"))||(szSchCd.equals("HBKD04LM"))||(szSchCd.equals("HBKD05LM"))||
  				   (szSchCd.equals("HAKD01LM"))||(szSchCd.equals("HAKD02LM"))||(szSchCd.equals("HAKD03LM"))||(szSchCd.equals("HAKD04LM"))||(szSchCd.equals("HAKD05LM"))||
  				   (szSchCd.equals("HBFD01LM"))||(szSchCd.equals("HBFD02LM"))||(szSchCd.equals("HBFD04LM"))||
  		  	       (szSchCd.equals("JBKD01LM"))||//(szSchCd.equals("JBFD01LM"))||
  		  	       (szSchCd.equals("JAKD01LM"))||//(szSchCd.equals("JBTC01MM"))||(szSchCd.equals("JBTC02MM"))||
  	  		  	   (szSchCd.equals("JBTC05MM"))||
  	  		  	   (szSchCd.equals("JATC05MM"))||
  	  		  	   (szSchCd.equals("HBKE03UM"))||(szSchCd.equals("HBKD03UM"))||
  	  		  	   (szSchCd.equals("HAKE03UM"))||(szSchCd.equals("HAKD03UM"))||
	  		  	   (szSchCd.equals("HBFE03UM"))||(szSchCd.equals("HBFD03UM"))||

  	  		  	   (szSchCd.equals("HCKD01LM"))||(szSchCd.equals("HCKD02LM"))||(szSchCd.equals("HCKD03LM"))||(szSchCd.equals("HCKD04LM"))||(szSchCd.equals("HCKD05LM"))||
  		  	       (szSchCd.equals("HCFD01LM"))||(szSchCd.equals("HCFD02LM"))||(szSchCd.equals("HCFD04LM"))||
	  		  	   (szSchCd.equals("JCKD01LM"))||(szSchCd.equals("JCFD01LM"))||
  	  		  	   (szSchCd.equals("JCTC01MM"))||(szSchCd.equals("JCTC02MM"))||(szSchCd.equals("JCTC05MM"))||
  	  		  	   (szSchCd.equals("HCKE03UM"))||(szSchCd.equals("HCKD03UM"))||
  	  		  	   (szSchCd.equals("HCFE03UM"))||(szSchCd.equals("HCFD03UM"))||
  	  		  	
  		  	 	   (szSchCd.equals("HEDD01LM"))||(szSchCd.equals("HEDD02LM"))||(szSchCd.equals("HEDD03LM"))||(szSchCd.equals("HEDD04LM"))||(szSchCd.equals("HEDD05LM"))||
  		  	 	   (szSchCd.equals("JEDD01LM"))||(szSchCd.equals("JETC01MM"))||(szSchCd.equals("JETC02MM"))||
  		  	 	   (szSchCd.equals("HEDE03UM"))||(szSchCd.equals("HEDD03UM"))||

  		  	 	   (szSchCd.equals("HGFD01LM"))||(szSchCd.equals("HGFD02LM"))||(szSchCd.equals("HGFD04LM"))||
  				   (szSchCd.equals("JGFD01LM"))||(szSchCd.equals("JGTC01MM"))||(szSchCd.equals("JGTC02MM"))||
  				   (szSchCd.equals("HGFE03UM"))||(szSchCd.equals("HGFD03UM"))||
  				   
  		  	 	   (szSchCd.equals("HHKD01LM"))||(szSchCd.equals("HHKD02LM"))||(szSchCd.equals("HHKD03LM"))||(szSchCd.equals("HHKD04LM"))||(szSchCd.equals("HHKD05LM"))||
  		  	 	   (szSchCd.equals("JHKD01LM"))||(szSchCd.equals("JHTC01MM"))||(szSchCd.equals("JHTC02MM"))||
  		  	 	   (szSchCd.equals("HHKE03UM"))||(szSchCd.equals("HHKD03UM"))||
//  		  	 151222 hun 지포장 스케줄4개 추가
  		  	 	    (szSchCd.equals("HAKD06LM"))||
	 			    (szSchCd.equals("HBKD06LM"))||
	 			    (szSchCd.equals("HBFD06LM"))||
	 			    (szSchCd.equals("HCKD06LM"))||
	 			    (szSchCd.equals("HCFD06LM"))||
	 			    (szSchCd.equals("HDFD06LM"))||
	 			    (szSchCd.equals("HEDD06LM"))||
	 			    (szSchCd.equals("HFFD06LM"))||
	 			    (szSchCd.equals("HGFD06LM"))||
	 			    (szSchCd.equals("HHKD06LM"))||
			       (szSchCd.substring(2,4).equals("CV"))){ //COVER 
  					
  					// LINE OFF 및   CV 는 권상위치 업데이트 안함
  					szMsg="<Y5ChkCrnSchEffectConditionCoil> 재료상태가 설비위치에서는 적치가 안되어 있어도 됨";	//2020.08.27 추가 작업
 					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
 				
 				} else {	
 					
 					JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
	 				recOutTemp.setField("YD_STK_LYR_MTL_STAT", "C");
	 				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNONEW*/
 	 				intRtnVal = ydStkLyrDao.getYdStklyr(recOutTemp, outRecSet, 632); //2020.01.29 쿼리 분리 작업
 					
	 				//재료번호로 적치단 조회
	 				if(intRtnVal <= 0) {
	 					szMsg="<Y5ChkCrnSchEffectConditionCoil> 재료상태가 적치중인 재료가 아닙니다.";
	 					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	 					outRecord.setField("RTN_MSG", szMsg);						
	 					return outRecord;
	
	
	
	 				}else if(intRtnVal >= 2){
	 					szMsg="<Y5ChkCrnSchEffectConditionCoil> 같은 재료번호로 2매이상 적치단에 등록되어있습니다.";
	 					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	 					outRecord.setField("RTN_MSG", szMsg);						
	 					return outRecord;
	
	 				}
	 				outRecSet.absolute(1);
	 				JDTORecord recOutTemp2 = JDTORecordFactory.getInstance().create();
	 				recOutTemp2.setRecord(outRecSet.getRecord());
	 				
	 				JDTORecord recInTemp2 = JDTORecordFactory.getInstance().create();
	 				recInTemp2.setField("YD_WBOOK_ID",   ydDaoUtils.paraRecChkNull(recOutTemp,  "YD_WBOOK_ID"));
	 				recInTemp2.setField("STL_NO",        ydDaoUtils.paraRecChkNull(recOutTemp,  "STL_NO"));
	 				recInTemp2.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recOutTemp2, "YD_STK_COL_GP"));
	 				recInTemp2.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(recOutTemp2, "YD_STK_BED_NO"));
	 				recInTemp2.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(recOutTemp2, "YD_STK_LYR_NO"));
	 				recInTemp2.setField("MODIFIER", 	 ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID"));/*2010.07.29 - 박지열 (수정자 추가)*/

	 				/*com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.updYdWrkbookmtl*/
	 				intRtnVal = ydWrkbookMtlDao.updYdWrkbookmtl(recInTemp2, 0);
	 				if(intRtnVal <= 0) {
	 					szMsg="<Y5ChkCrnSchEffectConditionCoil> 작업예약재료 위치정보 UPDATE 실패!!";
	 					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	 
	 					outRecord.setField("RTN_CD", "0");						
	 					outRecord.setField("RTN_MSG", szMsg);						
	 					return outRecord;
	
	
	 				}
 				}
 			}
 			
 			//현위치정보 업데이트 후 작업예약재료 권상모음순서를 재편성한다. 작업예약재료 조회 시 적치열구분, 베드번호, 
 			//단번호 DESC순서로 정렬해서 조회할 것!!
 			szMsg="<Y5ChkCrnSchEffectConditionCoil> 작업예약재료에 권상모음순서 재정렬 UPDATE!!!";
 			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
 			rsWrkbookmtl2 = JDTORecordFactory.getInstance().createRecordSet("");
 			
 			//적치열 ASC 적치베드 ASC 적치단 DESC로 정렬하여 권상모음순서를 재정렬한다.
 			intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsWrkbookmtl2, 37);
 			if(intRtnVal <= 0) {
 				szMsg="<Y5ChkCrnSchEffectConditionCoil> 작업예약재료 권상모음순서 재정렬 중 작업재료 조회실패!!";
 				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 				
 				outRecord.setField("RTN_CD", "0");						
 				outRecord.setField("RTN_MSG", szMsg);						
 				return outRecord;


 			}
// kk	
 			for(int Loop_i = 1; Loop_i <= rsWrkbookmtl2.size(); Loop_i++){
 				rsWrkbookmtl2.absolute(Loop_i);
 				recInTemp = JDTORecordFactory.getInstance().create();
 				recInTemp.setRecord(rsWrkbookmtl2.getRecord());
 				recInTemp.setField("YD_UP_COLL_SEQ", "" + Loop_i);
 				recInTemp.setField("MODIFIER", 	 ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID"));/*2010.07.29 - 박지열 (수정자 추가)*/
 				
 				/*com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.updYdWrkbookmtl*/
 				intRtnVal = ydWrkbookMtlDao.updYdWrkbookmtl(recInTemp, 0);
 				if(intRtnVal <= 0) {
 					szMsg="<Y5ChkCrnSchEffectConditionCoil> 작업예약재료 권상모음순서 재등록 실패!!";
 					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 					
 					outRecord.setField("RTN_CD", "0");						
 					outRecord.setField("RTN_MSG", szMsg);						
 					return outRecord;


 				}
 			}

 			//재료정보중 최하단 정보만 다시 조회하여 다음작업수행!!
 			rsWrkbookmtl = JDTORecordFactory.getInstance().createRecordSet("");
 			recInTemp = JDTORecordFactory.getInstance().create();
 			if(szYD_WBOOK_ID.equals("")) {
 				szMsg = "[" + szOperationName + "] 작업예약ID없이 스케줄코드["+szSchCd+"]로 작업예약의 작업예약재료가 존재하는 BED정보를 조회";
 				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
 				recInTemp.setField("YD_SCH_CD", szSchCd);
 				intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsWrkbookmtl, 402);
 			}else{

 				JDTORecordSet rsWrkbook22 = JDTORecordFactory.getInstance().createRecordSet("");
 				recInTemp1 = JDTORecordFactory.getInstance().create();
 				
 				ydUtils.putLog(szSessionName, " 열단위 이적 처리 CHECK", szMsg, YdConstant.DEBUG);
 				recInTemp1.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
 				intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp1, rsWrkbook22, 0);
 				rsWrkbook22.absolute(1);
 				recWrkkbook = rsWrkbook22.getRecord();
 				
 				///열단위 이적시 이적 첫코일만 스케쥴 생성 처리
 				if((recWrkkbook.getFieldString("YD_TO_LOC_GUIDE").trim().length() == 6 ) && 
 				   ((recWrkkbook.getFieldString("YD_GP").equals("J")) ||
 					(recWrkkbook.getFieldString("YD_GP").equals("H")) )){
 				
 					szMsg = "[" + szOperationName + "] 작업예약ID["+szYD_WBOOK_ID+"]으로 작업예약의 작업예약재료가 존재하는 BED정보를 조회 - 열별 이적작업인 경우";
 					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
 					recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
 					intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsWrkbookmtl, 301);

 				} else {
 					szMsg = "[" + szOperationName + "] 작업예약ID["+szYD_WBOOK_ID+"]으로 작업예약의 작업예약재료가 존재하는 BED정보를 조회";
 					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
 					recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
 					intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsWrkbookmtl, 401);
 			
 				}
 			}
 			if(intRtnVal<=0){
     			if(intRtnVal == 0) {
     				szMsg = "<Y5ChkCrnSchEffectConditionCoil> getYdWrkbookmtl data not found";
     				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
     			}else if(intRtnVal == -2) {
     				szMsg = "<Y5ChkCrnSchEffectConditionCoil> getYdWrkbookmtl parameter error";
     				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
     			}
 				szMsg = "작업예약재료 조회 중 Error!!";
 				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 				
 				outRecord.setField("RTN_CD", "0");						
 				outRecord.setField("RTN_MSG", szMsg);						
 				return outRecord;

 			}
 			//===============================================================================================================
 			
 			rsResultRt.addAll(rsWrkbookmtl); 
 			outRecord.setField("RTN_CD", "1");						
 			outRecord.setField("YD_SCH_CD", szSchCd);						
 			outRecord.setField("RTN_MSG", szMsg);						
 			return outRecord;
 			
 		} catch (Exception e) {
 			
 			szMsg="[" + szOperationName + "] Exception발생 : " + e.getMessage();
 			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 			outRecord.setField("RTN_CD", "0");						
 			outRecord.setField("RTN_MSG", szMsg);						
 			return outRecord;

 		
 		}	// end try catch문


 	} // end of Y5ChkCrnSchEffectConditionCoil()
 	
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : C열연 크레인 스케줄 GROUPING PARAMETER DATA SETTING(H/J)
	 *  
	 * @param  String     szEqpId, szSchCd, rsMinWrkBookMtl, rsReturn
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public JDTORecord Y5CrnSchSortCoil(JDTORecordSet rsWrkBookMtl, String szSchCd, JDTORecordSet rsReturn)throws JDTOException  {
		
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
		YdWrkbookDao    ydWrkbookDao    = new YdWrkbookDao();
		YdEqpDao	ydEqpDao 	= new YdEqpDao();
		
		//적치Bed정보
		JDTORecord recPara       = null;
		JDTORecord recTempPara   = null;
		//적치단정보
		JDTORecord recStkLyr     = null;

		JDTORecordSet rsResult 	 = null;

		//결과 레코드셋
		JDTORecordSet rsCrnSchResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsSchResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		//레코드셋 정렬 시
		JDTORecord recTemp     = null;
		String szMtlStat       = "";
		String szMsg="";
		String szMethodName="Y5CrnSchSortCoil";
		String szOperationName = "C열연 크레인 스케줄 GROUPING PARAMETER DATA SETTING";
		String szQuery = "";
		
		String szColGp  = "";
		String szBedNo   = "";
		String szLyrNo   = "";
		
		String szWbookId = "";
		
		JDTORecord outRecord  = JDTORecordFactory.getInstance().create();
		
		//현재료의 베드+1
		String szBedNoLeft = "";
		String szLyrUp   = "";

		int intRtnVal = 0;
		int intHandlingCnt = 1;

		int intTemp        = 0;
		int intCompBedNo   = 1;
		int iYD_UP_COLL_SEQ = 0;
		
		try {
			
			rsWrkBookMtl.absolute(1);
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setRecord( rsWrkBookMtl.getRecord() );
			szWbookId = ydDaoUtils.paraRecChkNull(recPara,"YD_WBOOK_ID");
			
//C증설
 			if(
 			   (szSchCd.equals("HAKD01LM"))||(szSchCd.equals("HAKD02LM"))||(szSchCd.equals("HAKD03LM"))||(szSchCd.equals("HAKD04LM"))||(szSchCd.equals("HAKD05LM"))||
	  	       (szSchCd.equals("HBFD01LM"))||(szSchCd.equals("HBFD02LM"))||(szSchCd.equals("HBFD04LM"))||
		       (szSchCd.equals("HBKD01LM"))||(szSchCd.equals("HBKD02LM"))||(szSchCd.equals("HBKD03LM"))||(szSchCd.equals("HBKD04LM"))||(szSchCd.equals("HBKD05LM"))||
		       
	  	       
		       
		       (szSchCd.equals("JBKD01LM"))|| 
		       (szSchCd.equals("JAKD01LM"))|| 
		       (szSchCd.equals("JBTC05MM"))||
		       (szSchCd.equals("JATC05MM"))||
		       (szSchCd.equals("HCFD01LM"))||(szSchCd.equals("HCFD02LM"))||(szSchCd.equals("HCFD04LM"))||
		       (szSchCd.equals("HCKD01LM"))||(szSchCd.equals("HCKD02LM"))||(szSchCd.equals("HCKD03LM"))||(szSchCd.equals("HCKD04LM"))||(szSchCd.equals("HCKD05LM"))||
		       
		       (szSchCd.equals("JCFD01LM"))||(szSchCd.equals("JCKD01LM"))||
               (szSchCd.equals("JCTC01MM"))||(szSchCd.equals("JCTC02MM"))||(szSchCd.equals("JCTC05MM"))||

        	   (szSchCd.equals("HEDD01LM"))||(szSchCd.equals("HEDD02LM"))||(szSchCd.equals("HEDD03LM"))||(szSchCd.equals("HEDD04LM"))||(szSchCd.equals("HEDD05LM"))||
	  	       (szSchCd.equals("JEDD01LM"))||(szSchCd.equals("JETC01MM"))||(szSchCd.equals("JETC02MM"))||

        	   (szSchCd.equals("HGFD01LM"))||(szSchCd.equals("HGFD02LM"))||(szSchCd.equals("HGFD04LM"))||
	  	       (szSchCd.equals("JGFD01LM"))||(szSchCd.equals("JGTC01MM"))||(szSchCd.equals("JGTC02MM"))||

        	   (szSchCd.equals("HHKD01LM"))||(szSchCd.equals("HHKD02LM"))||(szSchCd.equals("HHKD03LM"))||(szSchCd.equals("HHKD04LM"))||(szSchCd.equals("HHKD05LM"))||
	  	       (szSchCd.equals("JHKD01LM"))||(szSchCd.equals("JHTC01MM"))||(szSchCd.equals("JHTC02MM"))||
//	  	       151222 hun 지포장 스케줄 추가	  	        
	  	        (szSchCd.equals("HAKD06LM"))||
			    (szSchCd.equals("HBKD06LM"))||
			    (szSchCd.equals("HBFD06LM"))||
			    (szSchCd.equals("HCKD06LM"))||
			    (szSchCd.equals("HCFD06LM"))||
			    (szSchCd.equals("HDFD06LM"))||
			    (szSchCd.equals("HEDD06LM"))||
			    (szSchCd.equals("HFFD06LM"))||
			    (szSchCd.equals("HGFD06LM"))||
			    (szSchCd.equals("HHKD06LM"))||
	  	       (szSchCd.substring(2,4).equals("CV"))){ //COVER 

 				//작업예약재료를 조회해서 받는다. 코일은 재료별로 처리하도록...
 				for(int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
 					rsWrkBookMtl.absolute(Loop_i);
 					recPara = JDTORecordFactory.getInstance().create();
 					recPara.setRecord( rsWrkBookMtl.getRecord() );
 					
 					ydUtils.displayRecord(szOperationName, recPara);

 					szColGp   = recPara.getFieldString("YD_STK_COL_GP");
 					
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID",     szWbookId);
					recPara.setField("YD_SCH_CD",     szSchCd);
 					//recPara.setField("YD_STK_COL_GP",   szColGp);
 					
 					ydUtils.putLog(szSessionName, szMethodName, szWbookId +"////////////"+szColGp, YdConstant.DEBUG);
 					
 					rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
 					
 					/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWrkBookIDColGpBedNoLyrNoLineOff*/
 		    		intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 401);
 		    		if(intRtnVal <= 0) {
 		    			if(intRtnVal == 0) {
 		    				szMsg = "<Y5CrnSchSortCoil> getYdStklyr data not found";
 		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 		    			}else if(intRtnVal == -2) {
 		    				szMsg = "<Y5CrnSchSortCoil> getYdStklyr parameter error";
 		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 		    			}
 						outRecord.setField("RTN_VAL", "-1");						
 						outRecord.setField("RTN_MSG", szMsg);						
 						return outRecord;

 					}
 		    		szMsg = Loop_i + "번째 재료 !의 각 베드별 조회 수 : " + rsResult.size();
 					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	    			rsResult.absolute(rsResult.size());
	    			recStkLyr = JDTORecordFactory.getInstance().create();
	    			recStkLyr.setRecord(rsResult.getRecord());
	    			//HandlingCount
    				recStkLyr.setField("HANDLING_CNT", "" + intHandlingCnt);
	    			intHandlingCnt++;;
    				rsCrnSchResult.addRecord(recStkLyr);
    				
	    		}//end of for
 			} else {	
				
				//작업예약재료를 조회해서 받는다. 코일은 재료별로 처리하도록...
				for(int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
					rsWrkBookMtl.absolute(Loop_i);
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setRecord( rsWrkBookMtl.getRecord() );
					
					ydUtils.displayRecord(szOperationName, recPara);
	
					szColGp   = recPara.getFieldString("YD_STK_COL_GP");
					szBedNo   = recPara.getFieldString("YD_STK_BED_NO");
					szLyrNo   = recPara.getFieldString("YD_STK_LYR_NO");
					String szYD_COIL_OUTDIA_GRP_GP   = StringHelper.evl(recPara.getFieldString("YD_COIL_OUTDIA_GRP_GP"),"");
					
					if(szColGp.substring(0, 1).equals("J")) {
//ABC					
//						if(szColGp.substring(1,2).equals("A")||
//						   szColGp.substring(1,2).equals("B")||
//						   szColGp.substring(1,2).equals("C")){
							
//				        150908 hun 기존 ABC체크 로직 고정스키드 분류로 변경
				        if(ydEqpDao.chkFixedSkid(szColGp)){	
							intCompBedNo = 1;	
						} else {
							if(szYD_COIL_OUTDIA_GRP_GP.equals("A")) {
								intCompBedNo = 2;	
							} else if(szYD_COIL_OUTDIA_GRP_GP.equals("B")) {
								intCompBedNo = 3;	
							} else if(szYD_COIL_OUTDIA_GRP_GP.equals("C")) {
								intCompBedNo = 4;	
							}
						}	
					} else {
						intCompBedNo = 1;	
					}	
							
					//Bed번호 - 1 (현재재료의 좌측의 베드)
					intTemp = Integer.parseInt(szBedNo) - intCompBedNo;
					if (intTemp > 0 && intTemp < 10) 		szBedNoLeft = "0" + intTemp;
					else if (intTemp > 9 && intTemp < 100)  szBedNoLeft = "" + intTemp;
					else szBedNoLeft = "00";
					
					//적치단번호  + 1(현재 재료의 상단)
					intTemp = Integer.parseInt(szLyrNo) + 1;
					if 		(intTemp < 10) szLyrUp = "00" + intTemp;
					else if (intTemp > 9 && intTemp < 100) szLyrUp = "0" + intTemp;
					else if (intTemp > 99 && intTemp < 1000) szLyrUp = "" + intTemp;
					
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID",     szWbookId);
					recPara.setField("YD_STK_COL_GP",   szColGp);
					recPara.setField("YD_STK_BED_NO1",  szBedNo);
					recPara.setField("YD_STK_BED_NO2",  szBedNo);
					recPara.setField("YD_STK_BED_NO_L", szBedNoLeft);
					recPara.setField("YD_STK_LYR_NO1",  szLyrNo);
					recPara.setField("YD_STK_LYR_NO2",  szLyrUp);
					
					rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
					
					/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWrkBookIDColGpBedNoLyrNoIn*/
		    		intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 30);
		    		if(intRtnVal < 0) {
		    			if(intRtnVal == -2) {
		    				szMsg = "<Y5CrnSchSortCoil> getYdStklyr parameter error";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			}
						outRecord.setField("RTN_VAL", "-1");						
						outRecord.setField("RTN_MSG", szMsg);						
						return outRecord;

					}
		    		szMsg = Loop_i + "번째 재료 !의 각 베드별 조회 수 : " + rsResult.size();
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
	
					if(rsResult.size() != 0){
		    		//현재료가 1단인 경우
		    		if(szLyrNo.equals("001")) {
		    			/////////////////
		    			//상위 좌측 베드 단//
		    			/////////////////
		    			if(rsResult.size() == 3 || (rsResult.size() == 2 && !szBedNoLeft.equals("00")) ) {
		    				rsResult.absolute(1);
		    			
		    			
			    			recStkLyr = JDTORecordFactory.getInstance().create();
			    			recStkLyr.setRecord(rsResult.getRecord());
		 			
			    			//적치단 재료상태를 확인한다.
			    			//적치중이고 주작업이면 주작업이면서 보조작업처리(M)
			    			if(ydDaoUtils.paraRecChkNull(recStkLyr,"YD_STK_LYR_MTL_STAT").equals("C") 
			    					&& !ydDaoUtils.paraRecChkNull(recStkLyr,"STL_NO").equals("")
			    					&& (ydDaoUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ") != 0) ) {
			    				//To위치 결정방법
			    				recStkLyr.setField("YD_WBOOK_ID", szWbookId);

			    				recStkLyr.setField("YD_TO_LOC_DCSN_MTD","S");
			    				//HandlingCount
			    				recStkLyr.setField("HANDLING_CNT", ""+intHandlingCnt);
				    			intHandlingCnt++;
			    				rsCrnSchResult.addRecord(recStkLyr);
			    				
			    				//적치단의 재료상태를 권상대기로 변경
			    				recStkLyr.setField("YD_STK_LYR_MTL_STAT", "U");
			    				intRtnVal = this.Y5UpdYdStklyr(recStkLyr, 0);
			    				if(intRtnVal <= 0) {
			    					System.out.println("적치단 등록 실패  : parameter error ");
	
			    					outRecord.setField("RTN_VAL", "-1");						
			    					outRecord.setField("RTN_MSG", "적치단 등록 실패  : parameter error ");						
			    					return outRecord;

			    				}
		 
		    				//적치중이고 보조작업이면 보조작업 처리(W)	
			    			}else if(ydDaoUtils.paraRecChkNull(recStkLyr,"YD_STK_LYR_MTL_STAT").equals("C") 
			    					&& !ydDaoUtils.paraRecChkNull(recStkLyr,"STL_NO").equals("")
			    					&& (ydDaoUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ") == 0) ) {
			    				//To위치 결정방법
			    				recStkLyr.setField("YD_WBOOK_ID", szWbookId);
			    				recStkLyr.setField("YD_TO_LOC_DCSN_MTD", "W");
				    			//HandlingCount
				    			recStkLyr.setField("HANDLING_CNT", ""+intHandlingCnt);
				    			intHandlingCnt++;
				    			rsCrnSchResult.addRecord(recStkLyr);
				    			
			    				//적치단의 재료상태를 권상대기로 변경
			    				recStkLyr.setField("YD_STK_LYR_MTL_STAT", "U");
			    				
			    				intRtnVal = this.Y5UpdYdStklyr(recStkLyr, 0);
			    				if(intRtnVal <= 0) {
			    					System.out.println("적치단 등록 실패  : parameter error ");
		
			    					outRecord.setField("RTN_VAL", "-1");						
			    					outRecord.setField("RTN_MSG", "적치단 등록 실패  : parameter error ");						
			    					return outRecord;
			    					
	//
	//		    					return intRtnVal = -1;
			    				}
		
		    				//권상대기라면 해당 재료의 스케줄 코드를 확인한다.
			    			}else if(ydDaoUtils.paraRecChkNull(recStkLyr,"YD_STK_LYR_MTL_STAT").equals("U") ) {
			    				//현재 스케줄 코드와 상위좌측베드단 재료의 스케줄코드를 비교한다.
			    				if(!ydDaoUtils.paraRecChkNull(recStkLyr,"YD_WBOOK_ID").equals(szWbookId)) {
			    					//틀리면 프로세스 종료
			    					szMsg = "이미 다른 크레인 작업에 속해있는  있는 재료라서 스케줄 생성을 할 수 없습니다.";
			    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			    					outRecord.setField("RTN_CD", "0");						
//			    					outRecord.setField("RTN_MSG", szMsg);						
//			    					return outRecord;
			    					
	//		    					return intRtnVal = -1; 

//			    					150911 hun 미리 물려있는 크레인 작업이 현재 W이면 1순위 조정 start
//			    					1. 해당 코일번호로 스케쥴 select
			    					recPara = JDTORecordFactory.getInstance().create();
			    					recPara.setField("STL_NO",  ydDaoUtils.paraRecChkNull(recStkLyr,"STL_NO"));
			    					rsSchResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    					
			    			        /* com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCrnSchByStlNo */
			    		    		intRtnVal = ydCrnschDao.getYdCrnsch(recPara, rsSchResult, 52); 
			    		    		if(intRtnVal > 0) {
			    		    			rsSchResult.absolute(1);
			    		    			
			    		    			recTempPara = JDTORecordFactory.getInstance().create();
						    			recTempPara.setRecord(rsSchResult.getRecord());
						    			
				    		    		if("W".equals(recTempPara.getField("YD_WRK_PROG_STAT"))){
				    		    			
//					    					2. 해당 스케쥴 update				    		    			
				    		    			recPara = JDTORecordFactory.getInstance().create();
				    		    		    recPara.setField("YD_CRN_SCH_ID", recTempPara.getField("YD_CRN_SCH_ID"));
				    		    		    recPara.setField("YD_SCH_PRIOR", "1"); 
					    		    		     
					    		    		ydUtils.displayRecord(szOperationName, recPara); 
					    		    		
					    		    		/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnsch*/
					    		    		intRtnVal =  ydCrnschDao.updYdCrnsch(recPara, 0);
					    		    		
					    		    		if(intRtnVal <= 0 ){
						    		    		szMsg = "[Jsp Session : "+szOperationName+"] 크레인스케쥴 UPDATE 할 항목이 없습니다!!";
						    		    		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					    		    		}else{
						    		    		szMsg = "[Jsp Session : "+szOperationName+"] 크레인스케쥴 우선순위 UPDATE 성공!!!";
						    		    		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					    		    		      
					    		    		}
//					    					3. 해당 작업예약 update				    		    			
				    		    			recPara = JDTORecordFactory.getInstance().create();
				    		    		    recPara.setField("YD_SCH_CD", recTempPara.getField("YD_SCH_CD"));
				    		    		    recPara.setField("YD_WBOOK_ID", recTempPara.getField("YD_WBOOK_ID"));
				    		    		    recPara.setField("YD_SCH_PRIOR", "1"); 
					    		    		     
					    		    		ydUtils.displayRecord(szOperationName, recPara); 
					    		    		     
					    		    		/*com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.updYdWrkbook*/
					    		    		intRtnVal =  ydWrkbookDao.updYdWrkbook(recPara, 0);
					    		    		
					    		    		if(intRtnVal <= 0 ){
						    		    		szMsg = "[Jsp Session : "+szOperationName+"] 작업예약 UPDATE 할 항목이 없습니다!!";
						    		    		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					    		    		}else{
						    		    		szMsg = "[Jsp Session : "+szOperationName+"] 작업예약 우선순위 UPDATE 성공!!!";
						    		    		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					    		    		      
					    		    		}
//					    					150911 hun 미리 물려있는 크레인 작업이 현재 W이면 1순위 조정 end
				    		    		}
				    		    		
			    		    		} 

			    				}
			    				//같다면 앞에서 이미 등록 되었기때문에 따로 등록하지않는다.
			    				szMsg = "그룹작업이 끝난 재료입니다.";
		    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    					
			    			}
		    			}
		    			
		    			/////////////////
		    			//상위 우측 베드 단//
		    			/////////////////
		    			//적치단 재료상태를 확인한다.
		    			if(rsResult.size() == 3 || (rsResult.size() == 2 && szBedNoLeft.equals("00")) ) {
		    				if(rsResult.size() == 3) {
		    					rsResult.absolute(2);
		    				}else{
		    					rsResult.absolute(1);
		    				}
		    				
		    				
			    			recStkLyr = JDTORecordFactory.getInstance().create();
			    			recStkLyr.setRecord(rsResult.getRecord());
		
			    			//적치단 재료상태를 확인한다.
			    			//적치중이고 주작업이면 주작업이면서 보조작업처리(M)
			    			if(ydDaoUtils.paraRecChkNull(recStkLyr,"YD_STK_LYR_MTL_STAT").equals("C") 
			    					&& !ydDaoUtils.paraRecChkNull(recStkLyr,"STL_NO").equals("")
			    					&& (ydDaoUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ") != 0) ) {
			    				//To위치 결정방법
			    				recStkLyr.setField("YD_WBOOK_ID", szWbookId);
			    				recStkLyr.setField("YD_TO_LOC_DCSN_MTD","S");
			    				//HandlingCount
			    				recStkLyr.setField("HANDLING_CNT", ""+intHandlingCnt);
				    			intHandlingCnt++;
			    				rsCrnSchResult.addRecord(recStkLyr);
			    				
			    				//적치단의 재료상태를 권상대기로 변경
			    				recStkLyr.setField("YD_STK_LYR_MTL_STAT", "U");
			    				intRtnVal = this.Y5UpdYdStklyr(recStkLyr, 0);
			    				if(intRtnVal <= 0) {
			    					System.out.println("적치단 등록 실패  : parameter error ");
			    					outRecord.setField("RTN_CD", "0");						
			    					outRecord.setField("RTN_MSG", "적치단 등록 실패  : parameter error ");						
			    					return outRecord;
		    					
	//		    					
			    				}
			    				
		    				//적치중이고 보조작업이면 보조작업 처리(W)	
			    			}else if(ydDaoUtils.paraRecChkNull(recStkLyr,"YD_STK_LYR_MTL_STAT").equals("C")
			    					&& !ydDaoUtils.paraRecChkNull(recStkLyr,"STL_NO").equals("")
			    					&& (ydDaoUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ") == 0) ) {
			    				//To위치 결정방법
			    				recStkLyr.setField("YD_WBOOK_ID", szWbookId);
			    				recStkLyr.setField("YD_TO_LOC_DCSN_MTD","W");
				    			//HandlingCount
				    			recStkLyr.setField("HANDLING_CNT", ""+intHandlingCnt);
				    			intHandlingCnt++;
				    			rsCrnSchResult.addRecord(recStkLyr);
				    			
			    				//적치단의 재료상태를 권상대기로 변경
			    				recStkLyr.setField("YD_STK_LYR_MTL_STAT", "U");
			    				intRtnVal = this.Y5UpdYdStklyr(recStkLyr, 0);
			    				if(intRtnVal <= 0) {
			    					System.out.println("적치단 등록 실패  : parameter error ");
	
			    					outRecord.setField("RTN_CD", "0");						
			    					outRecord.setField("RTN_MSG", "적치단 등록 실패  : parameter error ");						
			    					return outRecord;
			    				}
			    				
		    				//권상대기라면 해당 재료의 스케줄 코드를 확인한다.
			    			}else if(ydDaoUtils.paraRecChkNull(recStkLyr,"YD_STK_LYR_MTL_STAT").equals("U") ) {
			    				//현재 스케줄 코드와 상위좌측베드단 재료의 스케줄코드를 비교한다.
			    				if(!ydDaoUtils.paraRecChkNull(recStkLyr,"YD_WBOOK_ID").equals(szWbookId)) {
			    					//틀리면 프로세스 종료
			    					szMsg = "이미 다른 크레인 작업에 속해있는  있는 재료라서 스케줄 생성을 할 수 없습니다.";
			    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			    					
//			    					150911 hun 미리 물려있는 크레인 작업이 현재 W이면 1순위 조정 start
//			    					1. 해당 코일번호로 스케쥴 select
			    					recPara = JDTORecordFactory.getInstance().create();
			    					recPara.setField("STL_NO",  ydDaoUtils.paraRecChkNull(recStkLyr,"STL_NO"));
			    					rsSchResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    					
			    			        /* com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCrnSchByStlNo */
			    		    		intRtnVal = ydCrnschDao.getYdCrnsch(recPara, rsSchResult, 52); 
			    		    		if(intRtnVal > 0) {
			    		    			rsSchResult.absolute(1);
			    		    			
			    		    			recTempPara = JDTORecordFactory.getInstance().create();
						    			recTempPara.setRecord(rsSchResult.getRecord());
						    			
				    		    		if("W".equals(recTempPara.getField("YD_WRK_PROG_STAT"))){
				    		    			
//					    					2. 해당 스케쥴 update				    		    			
				    		    			recPara = JDTORecordFactory.getInstance().create();
				    		    		    recPara.setField("YD_CRN_SCH_ID", recTempPara.getField("YD_CRN_SCH_ID"));
				    		    		    recPara.setField("YD_SCH_PRIOR", "1"); 
					    		    		     
					    		    		ydUtils.displayRecord(szOperationName, recPara); 
					    		    		
					    		    		/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnsch*/
					    		    		intRtnVal =  ydCrnschDao.updYdCrnsch(recPara, 0);
					    		    		
					    		    		if(intRtnVal <= 0 ){
						    		    		szMsg = "[Jsp Session : "+szOperationName+"] 크레인스케쥴 UPDATE 할 항목이 없습니다!!";
						    		    		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					    		    		}else{
						    		    		szMsg = "[Jsp Session : "+szOperationName+"] 크레인스케쥴 우선순위 UPDATE 성공!!!";
						    		    		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					    		    		      
					    		    		}
//					    					3. 해당 작업예약 update				    		    			
				    		    			recPara = JDTORecordFactory.getInstance().create();
				    		    		    recPara.setField("YD_SCH_CD", recTempPara.getField("YD_SCH_CD"));
				    		    		    recPara.setField("YD_WBOOK_ID", recTempPara.getField("YD_WBOOK_ID"));
				    		    		    recPara.setField("YD_SCH_PRIOR", "1"); 
					    		    		     
					    		    		ydUtils.displayRecord(szOperationName, recPara); 
					    		    		     
					    		    		/*com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.updYdWrkbook*/
					    		    		intRtnVal =  ydWrkbookDao.updYdWrkbook(recPara, 0);
					    		    		
					    		    		if(intRtnVal <= 0 ){
						    		    		szMsg = "[Jsp Session : "+szOperationName+"] 작업예약 UPDATE 할 항목이 없습니다!!";
						    		    		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					    		    		}else{
						    		    		szMsg = "[Jsp Session : "+szOperationName+"] 작업예약 우선순위 UPDATE 성공!!!";
						    		    		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					    		    		      
					    		    		}
//					    					150911 hun 미리 물려있는 크레인 작업이 현재 W이면 1순위 조정 end
				    		    		}
				    		    		
			    		    		} 
			    				}
			    				szMsg = "그룹작업이 끝난 재료입니다.";
		    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    				//같다면 앞에서 이미 등록 되었기때문에 따로 등록하지않는다.
			    			}
	
		    			}
		    			
		    			
		    			/////////////////
		    			//   현재위치       //
		    			/////////////////
		    			//현재료 주작업등록 (B)		    			
		    			rsResult.absolute(rsResult.size());
		    			recStkLyr = JDTORecordFactory.getInstance().create();
		    			recStkLyr.setRecord(rsResult.getRecord());
		    			
		    			if(ydDaoUtils.paraRecChkNull(recStkLyr,"YD_STK_LYR_MTL_STAT").equals("C")){
			    			//To위치 결정방법
			    			recStkLyr.setField("YD_WBOOK_ID", szWbookId);
			    			recStkLyr.setField("YD_TO_LOC_DCSN_MTD","S");
			    			//HandlingCount
			    			iYD_UP_COLL_SEQ =  Integer.parseInt(StringHelper.evl(recStkLyr.getFieldString("YD_UP_COLL_SEQ"), "0"));
		    				recStkLyr.setField("HANDLING_CNT", "" + (100 + iYD_UP_COLL_SEQ));
		    				rsCrnSchResult.addRecord(recStkLyr);
		    				
		    				//적치단의 재료상태를 권상대기로 변경
		    				recStkLyr.setField("YD_STK_LYR_MTL_STAT", "U");
		    				intRtnVal = this.Y5UpdYdStklyr(recStkLyr, 0);
		    				if(intRtnVal <= 0) {
		    					System.out.println("적치단 등록 실패  : parameter error ");
		    					outRecord.setField("RTN_CD", "0");						
		    					outRecord.setField("RTN_MSG", "적치단 등록 실패  : parameter error ");						
		    					return outRecord;	
		    				}
		    			}
		    		
	    				
	    			//현재료가 2단인 경우	
		    		}else{
		    			//현재료 주작업등록 (B)
		    			rsResult.absolute(1);
		    			recStkLyr = JDTORecordFactory.getInstance().create();
		    			recStkLyr.setRecord(rsResult.getRecord());
		    			
		    			if(ydDaoUtils.paraRecChkNull(recStkLyr,"YD_STK_LYR_MTL_STAT").equals("C")){
			    			//To위치 결정방법
			    			recStkLyr.setField("YD_WBOOK_ID", szWbookId);
			    			recStkLyr.setField("YD_TO_LOC_DCSN_MTD","B");
			    			//HandlingCount
			    			iYD_UP_COLL_SEQ =  Integer.parseInt(StringHelper.evl(recStkLyr.getFieldString("YD_UP_COLL_SEQ"), "0"));
			    			recStkLyr.setField("HANDLING_CNT", "" + (100 + iYD_UP_COLL_SEQ));
		    				rsCrnSchResult.addRecord(recStkLyr);
		    				
		    				//적치단의 재료상태를 권상대기로 변경
		    				recStkLyr.setField("YD_STK_LYR_MTL_STAT", "U");
		    				intRtnVal = this.Y5UpdYdStklyr(recStkLyr, 0);
		    				if(intRtnVal <= 0) {
		    					System.out.println("적치단 등록 실패  : parameter error ");
		    					outRecord.setField("RTN_CD", "0");						
		    					outRecord.setField("RTN_MSG", "적치단 등록 실패  : parameter error ");						
		    					return outRecord;	
		    				}
		    			}
		    		}
					}
				}//end of for
 			}
 			
 			//레코드셋 정렬
			JDTORecord recAfter = null;
			JDTORecord recCurrt = null;
			YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
			String  sCAR_KIND ="TT";
			
			JDTORecordSet rsWrkbookmtl = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
 			recInTemp.setField("YD_WBOOK_ID", szWbookId);
 			/*com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookNONECOIL*/
			intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsWrkbookmtl, 401);
			if(intRtnVal > 0) {
				rsWrkbookmtl.absolute(1);
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setRecord(rsWrkbookmtl.getRecord());
				sCAR_KIND = ydDaoUtils.paraRecChkNull(recInTemp, "CAR_KIND");
				}
		 
			//(trailer인경우)
			if("T".equals(sCAR_KIND)||"TR".equals(sCAR_KIND)){
	 			//차량작업이 아닌경우 정렬(trailer인경우)
	 			szSchCd=szSchCd.substring(2, 4);
			}
 			if(!szSchCd.equals("TR")&& !szSchCd.equals("PT")&& !szSchCd.equals("TT")){
				
				for(int Loop_i = 1; Loop_i < rsCrnSchResult.size(); Loop_i++) {
				
					for (int Loop_j = Loop_i + 1; Loop_j < rsCrnSchResult.size() + 1; Loop_j++) {
						
						rsCrnSchResult.absolute(Loop_i);
						recCurrt = rsCrnSchResult.getRecord();
						
						rsCrnSchResult.absolute(Loop_j);
						recAfter = rsCrnSchResult.getRecord();
						  //
						if (recCurrt.getFieldInt("HANDLING_CNT") > recAfter.getFieldInt("HANDLING_CNT")) {
							rsCrnSchResult = this.Y5RsSortCoil(Loop_i, Loop_j, rsCrnSchResult);
							if (intRtnVal == -1) {
								System.out.println("<rsSort> 정렬 중 Error ");
		    					outRecord.setField("RTN_CD", "-1");						
		    					outRecord.setField("RTN_MSG", "정렬 중 Error");						
		    					return outRecord;
	
							}
						}
					}//end of infor
				}//end of outfor
			
 			}
 			String sSTL_NO ="";
			for(int Loop_i = 1; Loop_i <= rsCrnSchResult.size(); Loop_i++) {
				rsCrnSchResult.absolute(Loop_i);
				System.out.println("HandlingCount = " + rsCrnSchResult.getRecord().getFieldString("HANDLING_CNT") + 
														rsCrnSchResult.getRecord().getFieldString("STL_NO")+
														rsCrnSchResult.getRecord().getFieldString("YD_TO_LOC_DCSN_MTD"));
				recCurrt = rsCrnSchResult.getRecord();
				
				
				
				if(!rsCrnSchResult.getRecord().getFieldString("STL_NO").equals("")
					&& !rsCrnSchResult.getRecord().getFieldString("STL_NO").equals(sSTL_NO)){
					sSTL_NO =rsCrnSchResult.getRecord().getFieldString("STL_NO");
					rsReturn.addRecord(recCurrt);
				}
			}

		} catch(Exception e) {
			e.printStackTrace();
			szMsg = "CrnSchSort 중 Error :	" + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			System.out.println("<rsSort> 정렬 중 Error ");

			outRecord.setField("RTN_CD", "0");						
			outRecord.setField("RTN_MSG", "<rsSort>정렬 중 Error");						
			return outRecord;

		}
		outRecord.setField("RTN_CD", "1");						
		return outRecord;

	} //end of Y5CrnSchSortCoil()
	
	
	
	
	
	
	
	
	
	
	
	
	/**
     * 오퍼레이션명 : C열연 스케줄링 크레인 스케줄 등록(H/J)
     *  
     * @param  ● msgRecSet, msgRec
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public JDTORecord Y5CrnSchInsCoil (JDTORecordSet rsResult, JDTORecord msgRecord) throws JDTOException {

    	YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
    	//YdWrkbookDao    ydWrkbookDao    = new YdWrkbookDao();
    	
		JDTORecord recIn       = null;
		JDTORecord recSeq      = null;
		JDTORecord recInPara = null;
		JDTORecord recInTemp = null;
		JDTORecordSet rsWrkBookMtl = null;
		JDTORecordSet rsOut    = null;
		
		JDTORecordSet rsSchRuleChk = null;
		JDTORecord recPara = null;
		JDTORecord outRecord  = JDTORecordFactory.getInstance().create();
		JDTORecord setRecord = JDTORecordFactory.getInstance().create();
		
		int intRtnVal = 0;
		String szName = "SYSTEM";
		String szMsg = "";
		String szMethodName = "Y5CrnSchInsCoil";
		String szOperationName			= "C열연크레인스케줄등록";
		//현재재료의 값
		String szCurrStlNo  = "";
		String szCurrToLocDcsnMtd = "";
	
		//이전 재료의 값
		String szBefoStlNo  = "";
		String szBefoToLocDcsnMtd = "";
		
		String szEqpId = "";
		String szSchCd = "";
		String szWbookId = "";
		String szREG_DDTT = "";
		String sYD_TO_LOC_GUIDE = "";
		String szYD_SCH_PROH_EXN  = null;
		String szYD_WRK_CRN       = null;
		String szYD_WRK_CRN_PRIOR = null;
		String szYD_ALT_CRN_YN    = null; 
		String szYD_ALT_CRN       = null;
		String szYD_ALT_CRN_PRIOR = null;
		String szCrn              = null;
		String szYD_SCH_PRIOR     = null;
		String szYD_WBOOK_ID     = null;
		String sPROG_GP			="";
		String sHDONG_YN			="N";
		String sADONG_YN			="N";
		String sEDONG_YN			="N";
		boolean blnRtnVal = false;
		
		
		try{
			rsResult.absolute(1);
			recIn = JDTORecordFactory.getInstance().create();
			recIn.setRecord(rsResult.getRecord());
			
			szEqpId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			szSchCd = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			szWbookId = ydDaoUtils.paraRecChkNull(recIn,   "YD_WBOOK_ID");
			
			//작업예약재료조회
			rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("");
			recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setField("YD_WBOOK_ID", szWbookId);
			/*com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookbyMtlSCHCD*/
			intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInPara, rsWrkBookMtl, 8);
			if(intRtnVal<=0){
    			if(intRtnVal == 0) {
    				szMsg = "<Y5CrnSchInsCoil> recInPara data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    				outRecord.setField("RTN_CD", "0");						
    				outRecord.setField("RTN_MSG", szMsg);						
    				return outRecord;
   			}else if(intRtnVal == -2) {
    				szMsg = "<Y5CrnSchInsCoil> recInPara parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
				outRecord.setField("RTN_CD", "0");						
				outRecord.setField("RTN_MSG", szMsg);						
				return outRecord;
			}
			
			rsWrkBookMtl.absolute(1);
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setRecord(rsWrkBookMtl.getRecord());
			szWbookId 			= ydDaoUtils.paraRecChkNull(recInTemp, "YD_WBOOK_ID");
			szREG_DDTT 			= ydDaoUtils.paraRecChkNull(recInTemp, "REG_DDTT");
			sYD_TO_LOC_GUIDE 	= ydDaoUtils.paraRecChkNull(recInTemp, "YD_TO_LOC_GUIDE");
			sPROG_GP			= ydDaoUtils.paraRecChkNull(recInTemp, "PROG_GP");
			sHDONG_YN			= ydDaoUtils.paraRecChkNull(recInTemp, "HDONG_YN");
			sEDONG_YN			= ydDaoUtils.paraRecChkNull(recInTemp, "EDONG_YN");
			sADONG_YN			= ydDaoUtils.paraRecChkNull(recInTemp, "ADONG_YN");
			
			//스케줄 기준 체크 
			rsSchRuleChk = JDTORecordFactory.getInstance().createRecordSet("");
			blnRtnVal = this.chkGetSchRule(szSchCd, rsSchRuleChk);
			if (!blnRtnVal){ 
				outRecord.setField("RTN_CD", "0");						
				outRecord.setField("RTN_MSG", "스케줄 기준이 이상합니다.");						
				return outRecord;
 
			}
			
			//레코드 추출
			rsSchRuleChk.first();
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setRecord(rsSchRuleChk.getRecord());
			
//C동 반입시 크레인 지정조건 해제 요청 2015.05.13 진기양 계장님			
//			//C동 반입/이송 인경우
//			if(szSchCd.equals("HCPT01LM")||szSchCd.equals("HCPT01UM")){
//				//SPM재 반입/이송
//				if(sPROG_GP.equals("K")){
//					//스케줄CD 체크
//					szYD_SCH_PROH_EXN 	= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN"); //스케줄 금지 유무
//					szYD_WRK_CRN      	= "HCCRC2";      //작업크레인
//					szYD_WRK_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");   // 작업크레인우선순위
//					szYD_ALT_CRN_YN  	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");   //대체크레인유무
//					szYD_ALT_CRN      	= "HCCRC3";		//대체크레인
//					szYD_ALT_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");	// 대체크레인우선순위
//				}else {
//					//스케줄CD 체크
//					szYD_SCH_PROH_EXN 	= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN"); //스케줄 금지 유무
//					szYD_WRK_CRN      	= "HCCRC3";      //작업크레인
//					szYD_WRK_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");   // 작업크레인우선순위
//					szYD_ALT_CRN_YN  	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");   //대체크레인유무
//					szYD_ALT_CRN      	= "HCCRC2";		//대체크레인
//					szYD_ALT_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");	// 대체크레인우선순위
//				}
//			}else 
			if("Y".equals(sHDONG_YN)){
				//보급작업  2,3스판  , H동 1번지 1단 대상이 존재 하는 경우 처리 (H2-->H1)
				//스케줄CD 체크
				szYD_SCH_PROH_EXN 	= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN"); //스케줄 금지 유무
				szYD_WRK_CRN      	= "HHCRH1";      //작업크레인
				szYD_WRK_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");   // 작업크레인우선순위
				szYD_ALT_CRN_YN  	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");   //대체크레인유무
				szYD_ALT_CRN      	= "HHCRH2";		//대체크레인
				szYD_ALT_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");	// 대체크레인우선순위
			}else if("Y".equals(sEDONG_YN)){
				//E동 보급작업 2,3스판 대상이 존재 하는 경우 처리 (E2-->E1)
				//스케줄CD 체크
				szYD_SCH_PROH_EXN 	= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN"); //스케줄 금지 유무
				szYD_WRK_CRN      	= "HECRE1";      //작업크레인
				szYD_WRK_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");   // 작업크레인우선순위
				szYD_ALT_CRN_YN  	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");   //대체크레인유무
				szYD_ALT_CRN      	= "HECRE2";		//대체크레인
				szYD_ALT_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");	// 대체크레인우선순위
			}else if("Y".equals(sADONG_YN)){
				//A동 보급작업 31,32스판 대상이 존재 하는 경우 처리 (A2-->A1)
				szYD_SCH_PROH_EXN 	= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN"); //스케줄 금지 유무
				szYD_WRK_CRN      	= "HACRA1";      //작업크레인
				szYD_WRK_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");   // 작업크레인우선순위
				szYD_ALT_CRN_YN  	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");   //대체크레인유무
				szYD_ALT_CRN      	= "HACRA2";		//대체크레인
				szYD_ALT_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");	// 대체크레인우선순위
				
			}else {
				//스케줄CD 체크
				szYD_SCH_PROH_EXN 	= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN"); //스케줄 금지 유무
				szYD_WRK_CRN      	= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");      //작업크레인
				szYD_WRK_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");   // 작업크레인우선순위
				szYD_ALT_CRN_YN  	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");   //대체크레인유무
				szYD_ALT_CRN      	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");		//대체크레인
				szYD_ALT_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");	// 대체크레인우선순위
			
			}
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD", "0");						
				outRecord.setField("RTN_MSG", szMsg);						
				return outRecord;
//				return intRtnVal = -1;
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
					outRecord.setField("RTN_CD", "0");										
					outRecord.setField("RTN_MSG", szMsg);						
					return outRecord;
//					return intRtnVal = -1;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) { 
					
					//저장위치 적치상태로 원복 U -> C
					setRecord.setField("YD_WBOOK_ID"          , szWbookId);
					commDao.update(setRecord, "com.inisteel.cim.yd.ydSch.CraneSch.CoilCrnSchSeEJBBean.updateStkLyrMtlStat", "Y5CrnSchInsCoil", szOperationName, "저장위치 적치상태로 원복");					
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD", "0");									
					outRecord.setField("RTN_MSG", szMsg);						
					return outRecord;
					
				} else {
					
					
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;

					// 추가 대체 크레인 CALL 하기 위해					
					szEqpId	= szYD_ALT_CRN;
					
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
			}
			
			for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				rsResult.absolute(Loop_i);
				recIn = JDTORecordFactory.getInstance().create();
				recIn.setRecord(rsResult.getRecord());
				
				szWbookId = ydDaoUtils.paraRecChkNull(recIn, "YD_WBOOK_ID");
				
				//현재료의 handlingCnt와 to위치 결정방법
				szCurrStlNo        = ydDaoUtils.paraRecChkNull(recIn, "STL_NO");
				szCurrToLocDcsnMtd = ydDaoUtils.paraRecChkNull(recIn, "YD_TO_LOC_DCSN_MTD"); 
				
				szMsg = "스케줄 등록 현재료와to위치 결정방법: "+szCurrStlNo+","+szCurrToLocDcsnMtd;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				//크레인스케줄ID를 할당받는다
				recSeq = JDTORecordFactory.getInstance().create();
				recSeq.setField("YD_CRN_SCH_ID", "1");

				rsOut = JDTORecordFactory.getInstance().createRecordSet("Temp");
				intRtnVal = this.Y5GetYdCrnsch(recSeq, rsOut, 9);
	    		if(intRtnVal <= 0) {
    				szMsg = "크레인스케줄 Id를 생성하지 못했습니다.";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    				outRecord.setField("RTN_CD", "0");								
					outRecord.setField("RTN_MSG", szMsg);						
					return outRecord;
//	    			return intRtnVal = -1;
	    		}
	    		
	    		
	    		//할당받은 크레인 스케줄 아이디로 Insert
	    		rsOut.first() ; 
	    		recSeq = JDTORecordFactory.getInstance().create();
				recSeq.setRecord(rsOut.getRecord());
				recIn.setField("YD_CRN_SCH_ID",    	recSeq.getFieldString("YD_CRN_SCH_ID"));
				recIn.setField("YD_EQP_ID",        	szCrn);

				recIn.setField("YD_GP",            	szSchCd.substring(0,1));
				recIn.setField("YD_BAY_GP",        	szSchCd.substring(1,2));
				recIn.setField("YD_SCH_CD",        	szSchCd);

				recIn.setField("YD_SCH_PRIOR",     	szYD_SCH_PRIOR);
				recIn.setField("YD_WBOOK_DT",     	szREG_DDTT);
				recIn.setField("YD_TO_LOC_GUIDE",   sYD_TO_LOC_GUIDE);
				recIn.setField("REGISTER", 	 ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID"));/*2010.07.29 - 박지열 (수정자 추가)*/
				recIn.setField("MODIFIER", 	 ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID"));/*2010.07.29 - 박지열 (수정자 추가)*/

				if(!szCurrToLocDcsnMtd.equals("R") ){
					
					if(
         			   (szSchCd.equals("HBFE03LM"))|| 
        			   (szSchCd.equals("HBKE03LM"))||
        			   (szSchCd.equals("HAKE03LM"))||
        			   (szSchCd.equals("HCFE03LM"))||
        			   (szSchCd.equals("HCKE03LM"))||
        			   (szSchCd.equals("HEDE03LM"))||
        			   (szSchCd.equals("HGFE03LM"))||
        			   (szSchCd.equals("HHKE03LM"))||
        			   //추출 
        			   (szSchCd.equals("HBKD03LM"))||
        			   (szSchCd.equals("HAKD03LM"))|| 
        			   (szSchCd.equals("HCKD03LM"))||
        			   (szSchCd.equals("HEDD03LM"))|| 
        			   (szSchCd.equals("HHKD03LM"))
        			   
        			   ){ // TAKEOUT존
					
						recIn.setField("YD_UP_WO_LOC",     recIn.getFieldString("YD_STK_COL_GP") + "00");
					}else{
						recIn.setField("YD_UP_WO_LOC",     recIn.getFieldString("YD_STK_COL_GP") + recIn.getFieldString("YD_STK_BED_NO"));
					}
					
					
					recIn.setField("YD_UP_WO_LAYER",   recIn.getFieldString("YD_STK_LYR_NO"));
					if(recIn.getFieldString("YD_UP_WO_LOC").trim().equals("")){
						szMsg = "권상지시위치가 없습니다.";
						outRecord.setField("RTN_CD", "0");										
						outRecord.setField("RTN_MSG", szMsg);						
						return outRecord;
//						throw new DAOException("<Y5CrnSchInsCoil> " + szMsg);
					}
				}
				
				//한개의 재료가 보조작업으로 중복인 경우
				if( szCurrStlNo.equals(szBefoStlNo) && szCurrToLocDcsnMtd.equals(szBefoToLocDcsnMtd) ){
					//현재료정보를 이전재료 정보로 변경
					szBefoStlNo        = szCurrStlNo;
					szBefoToLocDcsnMtd = szCurrToLocDcsnMtd;
					continue;
				}

				if(szCurrToLocDcsnMtd.equals("B") && szBefoToLocDcsnMtd.equals("R") && szCurrStlNo.equals(szBefoStlNo) ) {
					//현재료정보를 이전재료 정보로 변경
					szBefoStlNo        = szCurrStlNo;
					szBefoToLocDcsnMtd = szCurrToLocDcsnMtd;
					continue;
				}
				
				 
				intRtnVal = this.Y5InsYdCrnsch(recIn);
				if(intRtnVal <= 0) {
					System.out.println("크레인 스케줄 등록 실패 ");
					outRecord.setField("RTN_CD", "0");											
					outRecord.setField("RTN_MSG", "크레인 스케줄 등록 실패 ");						
					return outRecord;
//					return intRtnVal = -1;
				}
				//################################################################################
				

				//보조작업 유무 등록
				if(recIn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("W")){
					recIn.setField("YD_AID_WRK_YN", "Y");
				}else{
					recIn.setField("YD_AID_WRK_YN", "N");
				}
				 
				//크레인 작업재료 등록
				recIn.setField("YD_STK_LYR_NO", "001");
				//recIn.setField("REGISTER", szName);/*2010.07.29 - 박지열 삭제 (등록자를 "SYSTEM"으로 넣으면 안됨)*/
				intRtnVal = this.Y5InsYdCrnWrkMtl(recIn);
				if(intRtnVal <= 0) {
					System.out.println("크레인 스케줄 등록 실패  : parameter error ");
					outRecord.setField("RTN_CD", "0");										
					outRecord.setField("RTN_MSG", "크레인 스케줄 등록 실패  : parameter error");						
					return outRecord;
					
//					return intRtnVal = -1;
				}
				//################################################################################
				
				//현재료정보를 이전재료 정보로 변경
				szBefoStlNo        = szCurrStlNo;
				szBefoToLocDcsnMtd = szCurrToLocDcsnMtd;
				
			}//end of out for
			
			
	    	// 저장위치 결정 MAIN 호출하기 위해
			outRecord.setField("RTN_CD"	, "1");						
			outRecord.setField("MSG_ID"		, "YDYDJ510");						
			outRecord.setField("YD_EQP_ID"	, szCrn);						
			outRecord.setField("YD_WBOOK_ID", szWbookId);						
			outRecord.setField("RTN_MSG", "크레인 스케줄 등록 성공 ");						
			return outRecord;
	    	
       
		}catch(Exception e){
			szMsg = "Error : " + e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD", "0");										
			outRecord.setField("RTN_MSG", "크레인 스케줄 등록 실패  : parameter error");						
			return outRecord;
//			return intRtnVal = -1;
		
        }//end of try~catch
        
    }//end of Y5CrnSchInsCoil()

	
	
	/**
     * 오퍼레이션명 : C열연 스케줄링 크레인 스케줄 재등록
     *  
     * @param  ● msgRecSet, msgRec
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public JDTORecord Y5CrnSchInsCoilRe (JDTORecord msgRecord) throws JDTOException {

    	YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
    	YdCrnSchDao ydCrnSchDao 		= new YdCrnSchDao();
    	YdStkLyrDao ydStkLyrDao    		= new YdStkLyrDao();
    	
		JDTORecord recIn       = null;
		JDTORecord recSeq      = null;
		JDTORecord recInPara = null;
		JDTORecord recInTemp = null;
		JDTORecordSet rsWrkBookMtl = null;
		JDTORecordSet rsOut    = null;
		
		JDTORecordSet rsSchRuleChk = null;
		JDTORecord recPara = null;
		JDTORecord outRecord  = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1  = JDTORecordFactory.getInstance().create();
		JDTORecord inRecord  = JDTORecordFactory.getInstance().create();
		
		
		int intRtnVal = 0;
		String szName = "SYSTEM";
		String szMsg = "";
		String szMethodName = "Y5CrnSchInsCoilRe";
		String szOperationName			= "C열연크레인스케줄재등록";
		//현재재료의 값
		String szCurrStlNo  = "";
		String szCurrToLocDcsnMtd = "";
	
		//이전 재료의 값
		String szBefoStlNo  = "";
		String szBefoToLocDcsnMtd = "";
		
		String szEqpId = "";
		String szSchCd = "";
		String szSchId = "";
		String szWbookId = "";
		String szREG_DDTT = "";
		String sYD_TO_LOC_GUIDE = "";
		String szYD_SCH_PROH_EXN  = null;
		String szYD_WRK_CRN       = null;
		String szYD_WRK_CRN_PRIOR = null;
		String szYD_ALT_CRN_YN    = null; 
		String szYD_ALT_CRN       = null;
		String szYD_ALT_CRN_PRIOR = null;
		String szCrn              = null;
		String szYD_SCH_PRIOR     = null;
		String szYD_WBOOK_ID     = null;
		
		boolean blnRtnVal = false;
		
		
		try{
			
			ydUtils.putLog(szSessionName, szMethodName, "1", YdConstant.INFO);
			szEqpId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			ydUtils.putLog(szSessionName, szMethodName, "2" + szEqpId, YdConstant.INFO);
			szSchCd 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			ydUtils.putLog(szSessionName, szMethodName, "3" + szSchCd, YdConstant.INFO);
			szSchId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
			ydUtils.putLog(szSessionName, szMethodName, "4" + szSchId, YdConstant.INFO);
			szWbookId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");

			ydUtils.putLog(szSessionName, szMethodName, szWbookId, YdConstant.INFO);

			
			JDTORecordSet   outRecSet   = JDTORecordFactory.getInstance().createRecordSet("YD");
			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("YD_CRN_SCH_ID",   szSchId);

			/* com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSchSCHID*/
			intRtnVal = ydStkLyrDao.getYdStklyr(inRecord, outRecSet, 400);
			outRecSet.first();
			outRecord1 = JDTORecordFactory.getInstance().create();
			outRecord1.setRecord(outRecSet.getRecord());

			//적치단의 재료상태를 권상대기로 변경
			outRecord1.setField("YD_STK_LYR_MTL_STAT", "U");
			intRtnVal = this.Y5UpdYdStklyr(outRecord1, 0);
			if(intRtnVal <= 0) {
				System.out.println("적치단 등록 실패  : parameter error ");

				outRecord.setField("RTN_VAL", "-1");						
				outRecord.setField("RTN_MSG", "적치단 등록 실패  : parameter error ");						
				return outRecord;

			}
			
			//스케줄 기준 체크
			rsSchRuleChk = JDTORecordFactory.getInstance().createRecordSet("");
			blnRtnVal = this.chkGetSchRule(szSchCd, rsSchRuleChk);
			if (!blnRtnVal){ 
				outRecord.setField("RTN_CD", "0");						
				outRecord.setField("RTN_MSG", "스케줄 기준이 이상합니다.");						
				return outRecord;

			}
			
			//레코드 추출
			rsSchRuleChk.first();
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setRecord(rsSchRuleChk.getRecord());
			
			//스케줄CD 체크
			szYD_SCH_PROH_EXN 	= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN"); //스케줄 금지 유무
			szYD_WRK_CRN      	= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");      //작업크레인
			szYD_WRK_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");   // 작업크레인우선순위
			szYD_ALT_CRN_YN  	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");   //대체크레인유무
			szYD_ALT_CRN      	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");		//대체크레인
			szYD_ALT_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");	// 대체크레인우선순위
			
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD", "0");						
				outRecord.setField("RTN_MSG", szMsg);						
				return outRecord;
//				return intRtnVal = -1;
			}
			
			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szEqpId);
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {
				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD", "0");						
				outRecord.setField("RTN_MSG", szMsg);						
				return outRecord;
				
				
			}

			
			
			inRecord.setField("YD_CRN_SCH_ID",    	szSchId);
			inRecord.setField("YD_EQP_ID",        	szEqpId);
			inRecord.setField("YD_GP",            	szSchCd.substring(0,1));
			inRecord.setField("YD_BAY_GP",        	szSchCd.substring(1,2));
			inRecord.setField("YD_SCH_CD",        	szSchCd);


			intRtnVal = ydCrnSchDao.updYdCrnsch(inRecord, 0);		        
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg=" updYdCrnsch data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
    			}else if(intRtnVal == -1) {
    				szMsg=" updYdCrnsch duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg=" updYdCrnsch parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg=" updYdCrnsch execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
				outRecord.setField("RTN_CD", "0");						
				outRecord.setField("RTN_MSG", szMsg);						
				return outRecord;
			}
				   
	    	// 저장위치 결정 MAIN 호출하기 위해
			outRecord.setField("RTN_CD"	, "1");						
			outRecord.setField("MSG_ID"		, "YDYDJ510");						
			outRecord.setField("YD_EQP_ID"	, szEqpId);						
			outRecord.setField("YD_WBOOK_ID", szWbookId);						
			outRecord.setField("RTN_MSG", "크레인 스케줄 재등록 성공 ");						
			return outRecord;
	    	
       
		}catch(Exception e){
			szMsg = "Error : " + e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD", "0");										
			outRecord.setField("RTN_MSG", "크레인 스케줄 재등록 실패  : parameter error");						
			return outRecord;
//			return intRtnVal = -1;
		
        }//end of try~catch
        
    }//end of Y5CrnSchInsCoil()

	
	
	
	
	
	
	
	
	/**
     * 오퍼레이션명 : 레코드 치환(H/J)
     *  
     * @param  ● recPara1, recPara2, recResult
     * @return ● intRtnVal '1': 성공   '-1': 실패
     * @throws ● JDTOException
     */
    public JDTORecordSet Y5RsSortCoil (int intLoop_i, int intLoop_j, JDTORecordSet rsCrnSchResult) {

    	JDTORecord recTemp = null;
    	
    	JDTORecordSet rsTemp = null; 
    	
		int intRtnVal = 0;
		
		String szName = "SYSTEM";
		String szMsg = "";
		String szMethodName = "Y5RsSortCoil";
		
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
			szMsg = "Error : " + e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        }//end of try~catch
		
		return rsTemp;
    }//end of Y5RsSortCoil()
    
    
    
    
    
    
	/**
	 * 오퍼레이션명 : 설비상태 체크
	 *  
	 * @param  String szEqpId 설비ID
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
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 크레인사양 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  szEqpId, rsResult
	 * @return blnRtnVal       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean Y5ChkGetCrnSpec(String szEqpId, JDTORecordSet rsResult)throws JDTOException  {
		
		//크레인사양 DAO
		YdCrnSpecDao ydCrnSpecDao = new YdCrnSpecDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "Y5ChkGetCrnSpec";
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
	} //end of Y5ChkGetCrnSpec
	
	
	
	
	
	
	
	

	
    /**
     * 오퍼레이션명 : 크레인스케줄 Select(H/J)
     *  
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal 
     * @throws ● JDTOException
     */
    public int Y5GetYdCrnsch (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
    	
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	
    	String szMsg            = "";
    	String szMethodName     = "Y5GetYdCrnsch";
    	
        try{
        	
	        intRtnVal = ydCrnschDao.getYdCrnsch(msgRecord, getRecSet, intGp);  
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<Y5GetYdCrnsch> getYdCrnsch data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				}else if(intRtnVal == -2) {
					szMsg="<Y5GetYdCrnsch> getYdCrnsch parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return intRtnVal = -1;
			}
			
	        outRecSet.addAll(getRecSet);

        }catch(Exception e){
			szMsg="Error :  : " + e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return intRtnVal = -1;
        }//end of try~catch

        return intRtnVal = 1;
    	
    }//end of Y5GetYdCrnsch()
	
	
    
    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 Update(H/J)
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y5UpdYdStklyr (JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
    	
    	int intRtnVal = 0 ;
        
    	String szMsg            = "";
    	String szMethodName     = "Y5UpdYdStklyr";
    	
        try{
        	
	    	intRtnVal = ydStklyrDao.updYdStklyr(msgRecord, intGp);  //적치단의 재료정보 Clear
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="<Y5UpdYdStklyr> updYdStklyr data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
    			}else if(intRtnVal == -1) {
    				szMsg="<Y5UpdYdStklyr> updYdStklyr duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="<Y5UpdYdStklyr> updYdStklyr parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="<Y5UpdYdStklyr> updYdStklyr execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			return intRtnVal = -1;
    		}

		}catch(Exception e){
			szMsg="<Y5UpdYdStklyr> Error :  : " + e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return intRtnVal = -3;
	    }//end of try~catch
		
		return intRtnVal ;
		
    }//end of Y5UpdYdStklyr
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 크레인스케줄 Insert(H/J)
     *  
     * @param  ● msgRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y5InsYdCrnsch(JDTORecord msgRecord) throws JDTOException {
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

    	String szMsg        = "";
    	String szMethodName = "Y5InsYdCrnsch";
    	
    	int intRtnVal = 0 ;

		try{
			
			intRtnVal = ydCrnschDao.insYdCrnsch(msgRecord);		
    		if(intRtnVal == -2) {
				szMsg="parameter error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal = -1;
    		}
    		
			if(intRtnVal == -2) {
				szMsg = "크레인 스케줄 등록중 Error!! ErrorCode: " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
		}catch(Exception e){
			szMsg="Error :  : " + e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return intRtnVal = -3;
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of Y5InsYdCrnsch
	
    
    
    
    
    

    
    /**
     * 오퍼레이션명 : 크레인작업재료 Insert(H/J)
     *  
     * @param  ● msgRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y5InsYdCrnWrkMtl(JDTORecord msgRecord) throws JDTOException {
    	YdCrnWrkMtlDao ydCrnwrkmtlDao = new YdCrnWrkMtlDao();

    	String szMsg        = "";
    	String szMethodName = "Y5InsYdCrnWrkMtl";
    	int intRtnVal = 0 ;

		try{
			
			intRtnVal = ydCrnwrkmtlDao.insYdCrnwrkmtl(msgRecord);		
    		if(intRtnVal == -2) {
				szMsg="parameter error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal = -1;
    		}
    		
			if(intRtnVal == -2) {
				szMsg = "크레인 작업재료 삽입 중 Error!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return intRtnVal = -1;
			}
		}catch(Exception e){
			szMsg="Error :  : " + e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return intRtnVal = -3;
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of Y5InsYdCrnWrkMtl
    
    
    
    
    

    
    
    
    
    /**
     * 오퍼레이션명 : 보조작업인 경우(나선형검색) 적치Bed와 작업재료사양 비교 Check(H/J)
     *  
     * @param  rsCrnwrkmtl
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public JDTORecord Y5ChkAidStkBedCoil (JDTORecordSet rsCrnwrkmtl, String szColGp, String szBedNo, String szLyrNo, JDTORecord recCrnSch) throws JDTOException {
    	//상위 Method :Y5GetAidWrkLocCoil 
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
		//┗━┛
    	YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

    	
    	
    	
    	//크레인작업재료 정보
    	JDTORecord recGetCrnWrkMtl = null;
    	
    	//작업예약재료의 베드별 최하단 정보만 가져온다.
    	//파라미터 rsBed를 조회
    	//적치Bed를 조회한 정보
     	JDTORecord outRecord    = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1	= JDTORecordFactory.getInstance().create();
		JDTORecord recGetRsSet    = JDTORecordFactory.getInstance().create(); 
    
    	//크레인 스케줄 권하 지시위치 
    	JDTORecord recUpdCrnSchData = null;
    	
    	
    	int intRtnVal 		= 0 ;
        String szMsg        = "";
        String szMethodName = "Y5ChkAidStkBedCoil";
        
		String sRTN_BED ="";     	
    	CoilYdToLocDcsnUtil ydToLocDcsnUtil = new CoilYdToLocDcsnUtil();
    	String szStlNo 		= "";
    	String szYdSchCd 	= "";
    	String szRtnVal 	= "";
    	String sYdCrnSchId 	= "";
    	String szYdEqpId 	= "";
        try{
        	
    		rsCrnwrkmtl.absolute(1);
    		recGetCrnWrkMtl = rsCrnwrkmtl.getRecord();
    		
    		
    		szStlNo 	= recGetCrnWrkMtl.getFieldString("STL_NO");
    		szYdSchCd 	= recGetCrnWrkMtl.getFieldString("YD_SCH_CD");
    		sYdCrnSchId = recCrnSch.getFieldString("YD_CRN_SCH_ID");
    		szYdEqpId = recCrnSch.getFieldString("YD_EQP_ID");
 //////재료단위로 정보 read///////////   		
    		outRecord1 = (JDTORecord)ydToLocDcsnUtil.procCoilYdAidWkToPosDecision(sYdCrnSchId, szStlNo, szColGp, szBedNo, szLyrNo, szYdSchCd, szYdEqpId);
 /////////////////////////////////////////////////////////////   	
    		
    		String sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			sRTN_BED		= StringHelper.evl(outRecord1.getFieldString("RTN_BED"), "");
//kkkkRTN_BED
			if ("0".equals(sRTN_CD)) {
    			szMsg = "<Y5ChkAidStkBedCoil> To위치평점실패";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				return outRecord;
			}

			
			if (!("1".equals(sRTN_CD))) {
    			szMsg = "<Y5ChkAidStkBedCoil> To위치평점실패";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" , sRTN_CD);	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				return outRecord;
			}
		
			if ("".equals(sRTN_BED)) {
    			szMsg = "<Y5ChkAidStkBedCoil> 보조작업 To위치검색 중 ERROR 발생";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				ydUtils.putLog(szSessionName, szMethodName, "4" +sRTN_CD +sRTN_MSG, YdConstant.DEBUG);
				return outRecord;
			
			}

   		
    		recGetCrnWrkMtl = JDTORecordFactory.getInstance().create();
    		recGetCrnWrkMtl.setField("YD_STK_COL_GP",       sRTN_BED.substring(0,6));
    		recGetCrnWrkMtl.setField("YD_STK_BED_NO",       sRTN_BED.substring(6,8));
    		recGetCrnWrkMtl.setField("YD_STK_LYR_NO",       sRTN_BED.substring(8,11));
    		recGetCrnWrkMtl.setField("YD_STK_LYR_MTL_STAT", "D");
    		recGetCrnWrkMtl.setField("STL_NO", szStlNo);
    		
        	intRtnVal = ydStkLyrDao.updYdStklyrNEW(recGetCrnWrkMtl, 0);
        	if(intRtnVal <= 0) {
        		if(intRtnVal == 0) {
        			szMsg="<Y5ChkAidStkBedCoil> updYdStklyr data not found";
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
    				outRecord.setField("RTN_CD" , "0");	
    				outRecord.setField("RTN_MSG", sRTN_MSG);	
    				return outRecord;

        		}else if(intRtnVal == -2) {
        			szMsg="<Y5ChkAidStkBedCoil> updYdStklyr parameter error";
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    				outRecord.setField("RTN_CD" , "0");	
    				outRecord.setField("RTN_MSG", sRTN_MSG);	
    				return outRecord;

        		}
        		System.out.println("적치단에 저장하지 못했습니다.");
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", "적치단에 저장하지 못했습니다.");	
				return outRecord;

        	}


        	//크레인 스케줄  권하지시위치 업데이트
        	recUpdCrnSchData = JDTORecordFactory.getInstance().create();
        	recUpdCrnSchData.setField("YD_CRN_SCH_ID",  recCrnSch.getFieldString("YD_CRN_SCH_ID") ) ;
        	//recUpdCrnSchData.setField("REG_DDTT", null) ;
        	recUpdCrnSchData.setField("YD_WRK_PROG_STAT",  "W" ) ;
        	recUpdCrnSchData.setField("YD_DN_WO_LOC",   sRTN_BED.substring(0,8)) ;
        	recUpdCrnSchData.setField("YD_DN_WO_LAYER", sRTN_BED.substring(8,11)) ;
        	intRtnVal = this.Y5UpdYdCrnsch(recUpdCrnSchData, 0);
        	if(intRtnVal == -1) {
        		szMsg="크레인 스케줄 권하지시위치 등록 실패!";
        		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;

        	}
		
        }catch(Exception e){
			szMsg="<Y5ChkAidStkBedCoil> Exception Error :"+ e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;

        }//end of try~catch
        
		outRecord.setField("RTN_CD" , "1");	

		return outRecord;

    	
    }//end of Y5ChkAidStkBedCoil()
    
    
    
   
    
    /**
     * 오퍼레이션명 : 적치Bed와 작업재료사양 비교 Check(주작업이면서 보조작업처리 스케줄입니다)(H/J)
     *  
     * @param  rsCrnwrkmtl
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public JDTORecord Y5ChkMainStkBedCoil (JDTORecordSet rsCrnwrkmtl, String szColGp, String szBedNo,String szLyrNo, JDTORecord recCrnSch)  throws JDTOException {
    	//상위 Method : DnLocInsMainCoil
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
		//┗━┛
    	YdStkBedDao ydStkBedDao 			= new YdStkBedDao();
    	YdCrnWrkMtlDao ydCrnWrkMtlDao 		= new YdCrnWrkMtlDao(); 
    	YdDBAssist   ydDBAssist 			= new YdDBAssist();
    	YdStkLyrDao ydStkLyrDao 			= new YdStkLyrDao();
    	YdWrkbookMtlDao ydWrkbookMtlDao 	= new YdWrkbookMtlDao();
    	CoilYdToLocDcsnUtil ydToLocDcsnUtil = new CoilYdToLocDcsnUtil();
    	
    	//적치Bed를 조회한 정보
    	JDTORecordSet rsGetStkBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	//적치단을 조회한 정보
    	JDTORecord recStkLyr 		= null;
    	JDTORecordSet rsGetStkLyr 	= null;
    	
    	//크레인작업재료 정보
    	JDTORecord recGetCrnWrkMtl 	= null;
    	JDTORecordSet rsResultCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
    	//작업예약재료의 베드별 최하단 정보만 가져온다.
    	JDTORecord recWrkbookmtl = null;
    	JDTORecordSet rsWrkbookmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	//파라미터 rsBed를 조회
    	JDTORecord recStkBed      = null;
    	//적치Bed를 조회한 정보
     
    	//크레인스케줄 권하지시위치 등록
    	JDTORecord recUpdCrnSchData = null;
    	JDTORecord outRecord  		= JDTORecordFactory.getInstance().create(); 
    	JDTORecord outRecord1  		= JDTORecordFactory.getInstance().create(); 
    	int intRtnVal 		= 0 ;
        String szMsg        = "";
        String szMethodName = "Y5ChkMainStkBedCoil";
        
    	String sRTN_BED 	= "";
    	String szStlNo 		= "";
    	String szYdSchCd 	= "";
    	String szYdCrnSchId 	= "";
    	String szYdEqpId 	= "";

    	
        try{
        	
    		rsCrnwrkmtl.absolute(1);
    		recGetCrnWrkMtl = rsCrnwrkmtl.getRecord();
    		
    		
    		szStlNo 	= recGetCrnWrkMtl.getFieldString("STL_NO");
    		szYdSchCd 	= recGetCrnWrkMtl.getFieldString("YD_SCH_CD");
    		szYdCrnSchId = recCrnSch.getFieldString("YD_CRN_SCH_ID");
    		szYdEqpId = recCrnSch.getFieldString("YD_EQP_ID");
    		
    		outRecord1 	= (JDTORecord)ydToLocDcsnUtil.procCoilYdAidWkToPosDecision(szYdCrnSchId,szStlNo, szColGp, szBedNo,szLyrNo,szYdSchCd,szYdEqpId);
    		
    		String sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			sRTN_BED		= StringHelper.evl(outRecord1.getFieldString("RTN_BED"), "");
			if ("0".equals(sRTN_CD)) {
    			szMsg = "<Y5ChkMainStkBedCoil> To위치평점실패";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				return outRecord;
			}
  		
			if(!(sRTN_CD.equals("1"))){ 
	  			szMsg = "<Y5ChkMainStkBedCoil> To위치평점실패";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
 				outRecord.setField("RTN_CD" , sRTN_CD);	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				return outRecord;
			}
			
			if ("".equals(sRTN_BED)) {
    			szMsg = "<Y5ChkMainStkBedCoil> 코일소재야드 To위치검색 중 ERROR 발생";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//sjh				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				ydUtils.putLog(szSessionName, szMethodName, "4" +sRTN_CD +sRTN_MSG, YdConstant.DEBUG);
				return outRecord;
			
			}			
    		recGetCrnWrkMtl = JDTORecordFactory.getInstance().create();
    		recGetCrnWrkMtl.setField("YD_STK_COL_GP",       sRTN_BED.substring(0,6));
    		recGetCrnWrkMtl.setField("YD_STK_BED_NO",       sRTN_BED.substring(6,8));
    		recGetCrnWrkMtl.setField("YD_STK_LYR_NO",       sRTN_BED.substring(8,11));
    		recGetCrnWrkMtl.setField("YD_STK_LYR_MTL_STAT", "D");
        	intRtnVal = ydStkLyrDao.updYdStklyrNEW(recGetCrnWrkMtl, 0);
        	if(intRtnVal <= 0) {
        		if(intRtnVal == 0) {
        			szMsg="<Y5ChkMainStkBedCoil> updYdStklyr data not found";
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
     				outRecord.setField("RTN_CD" , "0");	
    				outRecord.setField("RTN_MSG", szMsg);	
    				return outRecord;
        		}else if(intRtnVal == -2) {
        			szMsg="<Y5ChkMainStkBedCoil> updYdStklyr parameter error";
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
     				outRecord.setField("RTN_CD" , "0");	
    				outRecord.setField("RTN_MSG", szMsg);	
    				return outRecord;
        		}
        		System.out.println("적치단에 저장하지 못했습니다.");
 				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", "적치단에 저장하지 못했습니다.");	
				return outRecord;
        	}


        	//크레인 스케줄  권하지시위치 업데이트
        	recUpdCrnSchData = JDTORecordFactory.getInstance().create();
        	recUpdCrnSchData.setField("YD_CRN_SCH_ID",  recCrnSch.getFieldString("YD_CRN_SCH_ID") ) ;
        	//recUpdCrnSchData.setField("REG_DDTT", null) ;
        	recUpdCrnSchData.setField("YD_WRK_PROG_STAT",  "W" ) ;
        	recUpdCrnSchData.setField("YD_DN_WO_LOC",   sRTN_BED.substring(0,8)) ;
        	recUpdCrnSchData.setField("YD_DN_WO_LAYER", sRTN_BED.substring(8,11)) ;
        	intRtnVal = this.Y5UpdYdCrnsch(recUpdCrnSchData, 0);
        	if(intRtnVal == -1) {
        		szMsg="크레인 스케줄 권하지시위치 등록 실패!";
        		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
        	}
        	
//        	//적치Bed에 입출고가능상태를 완산Bed(입고불가) 상태로 등록한다.
//        	recStkBed = JDTORecordFactory.getInstance().create();
//   			recStkBed.setField("YD_STK_BED_WHIO_STAT", "F");
//   			recStkBed.setField("YD_STK_COL_GP",        szColGp);
//   			recStkBed.setField("YD_STK_BED_NO",        szBedNo);
//   			intRtnVal = ydStkBedDao.updYdStkbed(recStkBed, 0);
//   			if(intRtnVal <= 0) {
//	   			szMsg="적치베드 입출고가능상태를 완산Bed로 등록하지 못했습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
// 				outRecord.setField("RTN_CD" , "0");	
//				outRecord.setField("RTN_MSG", szMsg);	
//				return outRecord;
//   			}

        }catch(Exception e){
			System.out.println("<Y5ChkMainStkBedCoil> Exception Error :"+ e.getLocalizedMessage());
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", "Exception Error :"+ e.getLocalizedMessage());	
			return outRecord;

        }//end of try~catch
        
		outRecord.setField("RTN_BED" , sRTN_BED);	
		outRecord.setField("RTN_CD" , "1");	
		return outRecord;

    	
    }//end of Y5ChkMainStkBedCoil()
    
    
    
    
    
    
    
    
    
//     
//    
//	
//    /**
//     * 오퍼레이션명 : 적치베드 Check Update 사용안
//     *  
//     * @param rsBed, rsCrnWrkMtl, recCrnWrkMtl, recCrnWrkMtl
//     * @return int 1, -1
//     * @throws 
//     */
//    public int Y5ChkLocsrchbedCrnMtlCoil (JDTORecordSet rsBed, JDTORecord recGetCrnWrkMtl){
//    	//상위 Method : 
//		//┏━┓
//		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
//		//┗━┛
//    	
//    	YdStkBedDao ydStkBedDao = new YdStkBedDao();
//    	YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao(); 
//    	YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
//    	YdDBAssist   ydDBAssist = new YdDBAssist();
//    	
//    	
//    	//적치Bed를 조회한 정보
//    	JDTORecordSet rsGetStkBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
//    	//적치단을 조회한 정보
//    	JDTORecord recStkLyr = null;
//    	JDTORecordSet rsGetStkLyr = null;
//    	
//    	//파라미터 rsBed를 조회
//    	JDTORecord recStkBed      = null;
//    	//적치Bed를 조회한 정보
//    	JDTORecord recGetRsSet    = null;
//    
//    	
//    	//적치Bed조회용
//    	JDTORecord    recTemp  = null;
//    	JDTORecord    recInTemp  = null;
//    	JDTORecordSet rsTemp   = null;
//    	JDTORecordSet rsResult = null;
//    	JDTORecord recStkLyr1 = null;
//    	JDTORecord recStkLyr2 = null;
//    	JDTORecord recStkLyr3 = null;
//    	
//    	int intRtnVal 		= 0 ;
//        String szMsg        = "";
//        String szMethodName = "Y5ChkLocsrchbedCrnMtlCoil";
//        String szOperationName = "적치베드 Check Update";
//        
//        //크레인작업재료의 최하단 산적LotType,산적Lot코드,폭
//        String szCrnWrkMtlStkLotTp = "";
//        String szCrnWrkMtlStkLotCd = "";
//        long lngCrnWrkMtlW		   = 0;
//        
//        //적치단의 최상단 산적LotType,산적Lot코드,폭
//        String szStkLyrStkLotTp    = "";
//        String szStkLyrStkLotCd    = "";
//        long lngStkLyrW		       = 0;
//        
//        String szStkColGp          = "";
//        String szStkBedNo          = ""; 
//        String szStkBedNoR = "";
//        
//        //등급
//        int intGrade               = 0;
//        //이전등급중 가장 좋은 등급
//        int intGradeSave           = 100;
//        //등급의 적치단정보
//        String szGradeColGp        = "";
//        String szGradeBedNo        = "";
//        String szGradeLyrNo        = "";
//        
//        String szQuery             = "";
//        String szCrnSchId          = "";
//        String szStkLyrMtlStat     = "";
//        
//        //적치베드상태
//        String szStkBedWhioStat    = "";
//        
//        //적치베드의 x축
//        long lngXaxis              = 0;
//        long lngXaxisR             = 0;
//        
//        long lngCoilOutDia         = 0;
//        long lngCoilOutDiaR        = 0;
//        
//        long lngMtlWt              = 0;
//        long lngMtlWtR             = 0;
//        
//        long lngCrnWrkMtlWt        = 0;
//        long lngCrnWrkMtlWtR       = 0;
//        
//        long lngRtnVal             = 0;
//        long lngCrnWrkMtlOutDia    = 0;
//        
//        int intTemp                = 0;
//        
//        boolean blBED_DEL_YN       = false;
//        
//        try{
//        	
//        	//적치베드단위로 Loop
//        	for(int Loop_i = 1; Loop_i <= rsBed.size(); Loop_i++) {
//        		rsBed.absolute(Loop_i);
//        		recStkBed = JDTORecordFactory.getInstance().create();
//        		recStkBed.setRecord(rsBed.getRecord());
//        		//열구분과 베드번호를 읽어온다.
//        		szStkColGp = recStkBed.getFieldString("YD_STK_COL_GP");
//        		szStkBedNo = recStkBed.getFieldString("YD_STK_BED_NO");
//        		
//
//        		
//        		//적치Bed의 오른쪽 Bed번호
//    			intTemp = Integer.parseInt(szStkBedNo) + 1;
//    			if (intTemp < 10) szStkBedNoR = "0" + intTemp;
//    			else if (intTemp > 9 && intTemp < 100) szStkBedNoR = "" + intTemp;
//    			
//    			lngCrnWrkMtlOutDia = ydDaoUtils.paraRecChkNullLong(recGetCrnWrkMtl,"COIL_OUTDIA");
//
//            	//적치베드 조회
//    			recTemp = JDTORecordFactory.getInstance().create();
//    			rsTemp  = JDTORecordFactory.getInstance().createRecordSet("Temp");
//    			recTemp.setField("YD_STK_COL_GP", szStkColGp);
//    			recTemp.setField("YD_STK_BED_NO", szStkBedNo);
//    			intRtnVal = ydStkBedDao.getYdStkbed(recTemp, rsTemp, 0);
//        		if(intRtnVal <= 0) {
//        			if(intRtnVal == 0) {
//        				szMsg="<Y5ChkLocsrchbedCrnMtlCoil> getYdStkbed data not found";
//        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//        			}else if(intRtnVal == -2) {
//        				szMsg="<Y5ChkLocsrchbedCrnMtlCoil> getYdStkbed parameter error";
//        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//        			}
//        			System.out.println("적치Bed 조회가 되지 않았습니다");
//        			continue;
//        		}
//        		rsTemp.first();
//        		lngXaxis         = ydDaoUtils.paraRecChkNullLong(rsTemp.getRecord(), "YD_STK_BED_XAXIS");
//        		szStkBedWhioStat = ydDaoUtils.paraRecChkNull(rsTemp.getRecord(), "YD_STK_BED_WHIO_STAT");
//        		
//        		//적치베드 우측베드 조회
//    			recTemp = JDTORecordFactory.getInstance().create();
//    			rsTemp  = JDTORecordFactory.getInstance().createRecordSet("Temp");
//    			recTemp.setField("YD_STK_COL_GP", szStkColGp);
//    			recTemp.setField("YD_STK_BED_NO", szStkBedNoR);
//    			intRtnVal = ydStkBedDao.getYdStkbed(recTemp, rsTemp, 0);
//        		if(intRtnVal <= 0) {
//        			if(intRtnVal == 0) {
//        				szMsg="<Y5ChkLocsrchbedCrnMtlCoil> getYdStkbed data not found";
//        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//        			}else if(intRtnVal == -2) {
//        				szMsg="<Y5ChkLocsrchbedCrnMtlCoil> getYdStkbed parameter error";
//        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//        			}
//        			System.out.println("적치Bed 조회가 되지 않았습니다");
////        			continue;
//        			blBED_DEL_YN = true;
//        		}else{
//	        		rsTemp.first();
//	        		//X축 값 조회
//	        		lngXaxisR = ydDaoUtils.paraRecChkNullLong(rsTemp.getRecord(), "YD_STK_BED_XAXIS");
//        		}
//        		
//        		
//        		//적치 베드 입출고 상태 조회  (입출고상태가 "F"완산 BED라면 더이상 적치가 되지 않고 출고는 가능하다.)
//        		if (szStkBedWhioStat.equals("F") ) {
//        			System.out.println("적치베드 상태가 입고 금지태입니다.");
//        			continue;
//         		}
//    			
//        		//적치단 조회
//        		rsGetStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
//        		for(int Loop_j = 1; Loop_j <= 3; Loop_j++) {
//        			recTemp = JDTORecordFactory.getInstance().create();
//        			if(Loop_j == 1) {
//        				//조회된 베드의 2단
//        				recTemp.setField("YD_STK_COL_GP", szStkColGp);
//            			recTemp.setField("YD_STK_BED_NO", szStkBedNo);
//            			recTemp.setField("YD_STK_LYR_NO", "002");
//        			}else if(Loop_j == 2){
//        				//조회된 베드의 1단
//            			recTemp.setField("YD_STK_COL_GP", szStkColGp);
//            			recTemp.setField("YD_STK_BED_NO", szStkBedNo);
//            			recTemp.setField("YD_STK_LYR_NO", "001");
//        			}else if(Loop_j == 3){
//        				//조회된 베드우측 베드의 1단
//            			recTemp.setField("YD_STK_COL_GP", szStkColGp);
//            			recTemp.setField("YD_STK_BED_NO", szStkBedNoR);
//            			recTemp.setField("YD_STK_LYR_NO", "001");
//        			}
//        			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//        			intRtnVal = ydStkLyrDao.getYdStklyr(recTemp, rsResult, 39);
//        			if(intRtnVal <= 0) {
//        				if(intRtnVal == 0) {
//        					szMsg="조회된 적치단 정보가 없습니다.";
//        					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//        				}else{
//        					szMsg="적치단 정보 조회중 Error";
//        					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//        					break;
//        				}
//        				
//        				//적치단정보 셋팅 (2단 및 우측베드가 없거나 적치불가인 경우 적치불가능이라는 레코드의 정보를 추가해준다. 첫번째 레코드의 정보는 2단정보여야하기때문)
//        				recInTemp = JDTORecordFactory.getInstance().create();
//        				if(Loop_j == 1) {
//        					//2단 정보가 없을경우
//            				recInTemp.setField("YD_STK_LYR_MTL_STAT", "X");
//        				}else{
//        					//우측베드정보가 없을경우
//        					recInTemp.setField("YD_STK_LYR_MTL_STAT", "X");
//            				recInTemp.setField("COIL_OUTDIA", "0");
//            				recInTemp.setField("YD_MTL_WT",   "0");
//        				}
//        				rsGetStkLyr.addRecord(recInTemp);
//        			}else{
//	        			rsResult.absolute(1);
//	        			rsGetStkLyr.addRecord(rsResult.getRecord());
//        			}
//        		}//end of for
//    			
//    			
//
//    			
//    			//검색된 Bed의 1단의 적치단재료상태 Check
//    			rsGetStkLyr.absolute(2);
//    			recStkLyr1 = JDTORecordFactory.getInstance().create();
//    			recStkLyr1.setRecord(rsGetStkLyr.getRecord());
//    			szStkLyrMtlStat = ydDaoUtils.paraRecChkNull(recStkLyr1,"YD_STK_LYR_MTL_STAT");
//    			//중량과 외경을 가져온다.
//    			lngCoilOutDia = ydDaoUtils.paraRecChkNullLong(recStkLyr1, "COIL_OUTDIA");
//    			lngMtlWt      = ydDaoUtils.paraRecChkNullLong(recStkLyr1, "YD_MTL_WT");
//
//    			
//    			//공베드인경우 바로 적치
//    			if(szStkLyrMtlStat.equals("E")) {
//    				intGrade = 3;
//    				recStkLyr1.setField("YD_STK_LYR_NO","");
//    			//1단에 재료가 있는 경우 check
//    			}else if( (szStkLyrMtlStat.equals("C") || szStkLyrMtlStat.equals("D")) && !blBED_DEL_YN) {
//    				//검색된 Bed의 2단의 적치단재료상태 Check
//    				rsGetStkLyr.absolute(1);
//    				recStkLyr2 = JDTORecordFactory.getInstance().create();
//    				recStkLyr2.setRecord(rsGetStkLyr.getRecord());
//    				szStkLyrMtlStat = ydDaoUtils.paraRecChkNull(recStkLyr2,"YD_STK_LYR_MTL_STAT");
//    				
//    				
//    				//적치단재료상태가 적치가능인 경우 
//    				if(szStkLyrMtlStat.equals("E")) {
//    					//베드+1 의 1단 재료정보를 읽어와서 비교한다.
//    					rsGetStkLyr.absolute(3);
//    					recStkLyr3 = JDTORecordFactory.getInstance().create();
//    					recStkLyr3.setRecord(rsGetStkLyr.getRecord());
//    					//중량과 외경을 가져온다.
//    					lngCoilOutDiaR = ydDaoUtils.paraRecChkNullLong(recStkLyr3, "COIL_OUTDIA");
//    					lngMtlWtR      = ydDaoUtils.paraRecChkNullLong(recStkLyr3, "YD_MTL_WT");
//    					
//    					//1. 적치베드의 1단 재료 중량과 적치베드+1 의 1단 재료 중량의 비교 큰값을 찾는다.
//						szMsg="lngMtlWt : " + lngMtlWt + "  >= " + "lngMtlWtR : " + lngMtlWtR;
//    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//						szMsg="lngMtlWtR : " + lngMtlWtR + "  > " + "lngMtlWt : " + lngMtlWt;
//    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
//    					if(lngMtlWt >= lngMtlWtR) {
//    						//크레인 작업재료의 중량 > 적치단의 재료중량+2000 
//    						if(lngCrnWrkMtlWt > lngMtlWtR + 2000){
//    							szMsg="크레인 작업재료의 중량이 적치중인 재료의 중량+2Ton의 무게를 초과하였습니다.";
//    	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    	    					continue;
//    						}
//    						
//
//    					}else if(lngMtlWtR > lngMtlWt) {
//
//        					
//    						szMsg="lngCrnWrkMtlWt : " + lngCrnWrkMtlWt + "  > " + "lngMtlWt + 2000 : " + lngMtlWt + "2000";
//        					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    						if(lngCrnWrkMtlWt > lngMtlWt + 2000){
//
//            					
//    							szMsg="크레인 작업재료의 중량이 적치중인 재료의 중량+2Ton의 무게를 초과하였습니다.";
//    	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    	    					continue;
//    						}
//    					}
//    					
//    					//절대값(검새된 bed의 x축값 - 검색된bed의 우측bed의 x축갑) - ( (검색된 Bed의 재료외경 / 2) + (검색된bed의 우측bed의 제료외경 / 2) ) 
//    					lngRtnVal = Math.abs(lngXaxis - lngXaxisR) - ( (lngCoilOutDia / 2) + (lngCoilOutDiaR / 2) ) ;
//    					//2. 구한값이 500mm 이상이면 종료 (코일과 코일의 사이의 공간)
//    					if (lngRtnVal >= 500) {
//    						szMsg="코일과 코일의 사이 간격이 500mm 이상입니다.";
//	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	    					continue;
//    					}
//    					
//    					//3. 적치베드의 1단 재료 중량과 적치베드+1 의 1단 재료 외경을 비교, 큰값을 찾는다.
//    					if(lngCoilOutDia >= lngCoilOutDiaR) {
//    						//크레인 작업재료의 외경 > 적치단의 재료외경+200 
//    						if(lngCrnWrkMtlOutDia > lngCoilOutDiaR + 200){
//    							szMsg="크레인 작업재료의 외경이 적치중인 재료의 외경+200을 초과하였습니다.";
//    	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    	    					continue;
//    						}
//    					}else if(lngCoilOutDiaR > lngCoilOutDia) {
//    						if(lngCrnWrkMtlOutDia > lngCoilOutDia + 200){
//    							szMsg="크레인 작업재료의 외경이 적치중인 재료의 외경+200을 초과하였습니다.";
//    	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    	    					continue;
//    						}
//    					}
//
//
//    					intGrade = this.Y5GradeTestCoil(recGetCrnWrkMtl, recStkLyr1);
//    	       			//최상의 등급인 경우 종료
//    	       			if (intGrade == 1) {
//    	       				//최상의자리를 찾았다는 메시지
//    						System.out.println("1등급 위치를 찾았습니다.");
//    	       				//To위치 저장 후 리턴
//    	       				
//    						//적치단 등록               크레인 작접재료                적치단 정보       등급
//    						intRtnVal = this.Y5UpdGradStkLyrCoil(recGetCrnWrkMtl, recStkLyr2);	
//    						if(intGrade == -1) {
//    		       				System.out.println("최상급 적치단 등록  중 Error");
//    		       				continue;
//    						}
//    	       				return intRtnVal = 1;
//    	       			}else if(intGrade == -1) {
//    	       				System.out.println("등급 부여 중 Error");
//    	       				continue;
//    	       			}
//    	       			
//    				//적치단재료상태가 적치가능이 아닌 경우	
//    				}else{
//    					szMsg="적치단의 재료상태가 적치가능이 아닙니다.";
//    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    					continue;
//    				}
//    			
//    			//공베드도 아니고 2단에 적치할 수 없는 경우	
//    			}else{
//    				szMsg="검색위치의 2단에 현재 적치할 수 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    				continue;
//    			}
//
//
//       			if(Loop_i == 1) {
//       				intGradeSave = intGrade;
//       				szGradeColGp = ydDaoUtils.paraRecChkNull(recStkLyr1,"YD_STK_COL_GP");
//       				szGradeBedNo = ydDaoUtils.paraRecChkNull(recStkLyr1,"YD_STK_BED_NO");
//       				szGradeLyrNo = ydDaoUtils.paraRecChkNull(recStkLyr1,"YD_STK_LYR_NO");       				
//       			}
//       			
//       			//저장등급보다 지금 등급이 좋다면 저장
//       			if (intGradeSave > intGrade) {
//       				intGradeSave = intGrade;
//       				szGradeColGp = recStkLyr1.getFieldString("YD_STK_COL_GP");
//       				szGradeBedNo = recStkLyr1.getFieldString("YD_STK_BED_NO");
//       				szGradeLyrNo = recStkLyr1.getFieldString("YD_STK_LYR_NO");
//       			}
//       			
//        	}//end of for
//        	
//        	//적치단정보 Setting
//        	recGetRsSet = JDTORecordFactory.getInstance().create();
//        	recGetRsSet.setField("YD_STK_COL_GP", szGradeColGp);
//        	recGetRsSet.setField("YD_STK_BED_NO", szGradeBedNo);
//        	recGetRsSet.setField("YD_STK_LYR_NO", szGradeLyrNo);
//        	
//ydUtils.displayRecord(szOperationName, recGetRsSet);
//        	
//        	//적치단 등록               크레인 작접재료                적치단 정보       등급
//			intRtnVal = this.Y5UpdGradStkLyrCoil(recGetCrnWrkMtl, recGetRsSet);	
//			if(intGrade == -1) {
//				szMsg="저장위치 적치단 등록 중 Error : " + intGrade;
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			}
//			
//        }catch(Exception e){
//			System.out.println("<Y5ChkLocsrchbedCrnMtlCoil> Exception Error :"+ e.getLocalizedMessage());
//			return intRtnVal = -1;
//        }//end of try~catch
//        
//        
//        return intRtnVal = 1;
//    	
//    }//end of Y5ChkLocsrchbedCrnMtlCoil()
//    
//
//    
//    
//    
//    
//    
    
    
    /**
     * 오퍼레이션명 : 크레인작업지시요구 판단
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public JDTORecord chkY5CrnWrkOrdReq(JDTORecord msgRecord) throws JDTOException {
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
    	YdEqpDao    ydEqpDao    = new YdEqpDao();
    	
    	JDTORecordSet rsResult = null;
    	JDTORecord recInTemp   = null;
    	JDTORecord recOutTemp  = null;
    	JDTORecord outRecord = JDTORecordFactory.getInstance().create();

    	int intRtnVal = 0 ;
    	
    	String szMsg        = "";
    	String szMethodName = "chkY5CrnWrkOrdReq";
    	String szOperationName ="C열연 크레인작업지시요구";
    	
    	String szYD_EQP_ID = "";
    	String szYD_EQP_WRK_MODE = "";
    	String szYD_EQP_STAT = "";
    	String szYD_SCH_CD = "";
    	String szYD_CRN_SCH_ID = "";
    	String szYD_CRN_XAXIS = "";
    	String szYD_CRN_YAXIS = "";
    	String sRTN_MSG = "";
    	String sSndFlag = "";
		try{
			
			//크레인스케줄을 조회하여 설비ID를 찾는다 
			//설비ID로 설비Table를 조회하여 야드설비작업Mode, 야드설비상태(작업진행상태)의 값을 조회한다.
			//설비id, 야드설비작업Mode(ONLINE:1 OFFLINE:0), 야드작업진행상태('W'), 스케줄코드, 크레인스케줄id(공백), 크레인X축(공백), 크레인Y축(공백)
			
			
			//크레인 스케줄 조회
			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			intRtnVal = ydCrnschDao.getYdCrnsch(msgRecord, rsResult, 0);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<chkY5CrnWrkOrdReq> getYdCrnsch data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				}else if(intRtnVal == -2) {
					szMsg="<chkY5CrnWrkOrdReq> getYdCrnsch parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				outRecord.setField("RTN_CD", "0");						
				outRecord.setField("RTN_MSG", szMsg);						
				return outRecord;

				
//				return intRtnVal = -1;
			}

			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setRecord(rsResult.getRecord(0));
			
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recInTemp, "YD_SCH_CD");
			
			//설비Table조회
			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 0);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<chkY5CrnWrkOrdReq> getYdEqp data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				}else if(intRtnVal == -2) {
					szMsg="<chkY5CrnWrkOrdReq> getYdEqp parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				outRecord.setField("RTN_CD", "0");						
				outRecord.setField("RTN_MSG", szMsg);						
				return outRecord;
//				return intRtnVal = -1;
			}
			
			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult.getRecord(0));
			
			
			szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_STAT");
//			sSndFlag  	  = ydDaoUtils.paraRecChkNull(msgRecord,  "SND_CHECK");   //재스케쥴 기동시 처리 됨
//			if(sSndFlag.equals("Y")) {
//				szYD_EQP   _STAT = "W";
//			}
			
			if(szYD_EQP_STAT.equals("W")) {
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MSG_ID",           "YDYDJ643");
				recInTemp.setField("YD_EQP_ID",        ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_ID"));
				recInTemp.setField("YD_EQP_WRK_MODE",  ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_WRK_MODE"));
				recInTemp.setField("YD_WRK_PROG_STAT", "W");
				recInTemp.setField("YD_SCH_CD",        "");
				recInTemp.setField("YD_CRN_SCH_ID",    "");
				recInTemp.setField("YD_CRN_SCH_ID_RE",    ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID")); //re스케줄 값				
				recInTemp.setField("YD_CRN_XAXIS",     "");
				recInTemp.setField("YD_CRN_YAXIS",     "");
				
				//크레인작업지시 송신
				//ydDelegate.sendMsg(recInTemp);
				
				szMsg="[JMS > EJB 변환 ============= Y5CrnSchInsCoil ==============";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
//				EJBConnector ydEjbCon = new EJBConnector("default", this);
//				ydEjbCon.trx("CraneLdHdFaEJB", "rcvY5CrnWrkOrdReq", recInTemp);

				EJBConnector ydEjbCon = new EJBConnector("default", this);
				sRTN_MSG = (String)ydEjbCon.trx("CoilCraneLdHdSeEJB", "procY5CrnWrkOrdReq", recInTemp);
				
				if (sRTN_MSG.equals(YdConstant.RETN_CD_SUCCESS)){
			    	szMsg="["+szOperationName+"] ----------------------- 메소드 끝 -----------------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD", "1");						
//					outRecord.setField("RTN_MSG", szMsg);						
					return outRecord;
				} else {
					
					outRecord.setField("RTN_CD", "0");						
					outRecord.setField("RTN_MSG","procY5CrnWrkOrdReq 실패 크레인 상태(고장및 OFF-라인) 확인 바람...");						
					return outRecord;
				}
				
			}else{
				szMsg="크레인설비의 상태가 Idle가 아닙니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			szMsg="<chkY5CrnWrkOrdReq> Error : "+ e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD", "0");						
			outRecord.setField("RTN_MSG", szMsg);						
			return outRecord;
        }//end of try~catch
		
		outRecord.setField("RTN_CD", "1");						
//		outRecord.setField("RTN_MSG", szMsg);						
		return outRecord;
//		return intRtnVal = 1;
		
    }// end of chkY5CrnWrkOrdReq
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 위치검색범위및위치검색Bed조회
     *  
     * @param  rsResultCrnwrkmtl, rsBed
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int Y5GradeTestCoil (JDTORecord recGetCrnWrkMtl, JDTORecord recGetStkLyr){
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
        
        String szMsg = "";
        String szMethodName = "Y5GradeTestCoil";
    	
        try{
        	szCrnWrkMtlStkLotTp = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl,"YD_STK_LOT_TP");
        	szCrnWrkMtlStkLotCd = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl,"YD_STK_LOT_CD");
        	szStkLyrStkLotTp    = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_LOT_TP");
        	szStkLyrStkLotCd    = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_LOT_CD");

			szMsg="크레인 작업재료 szCrnWrkMtlStkLotTp : " + szCrnWrkMtlStkLotTp;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			szMsg="크레인 작업재료 szCrnWrkMtlStkLotCd : " + szCrnWrkMtlStkLotCd;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			szMsg="적치단 재료 szStkLyrStkLotTp : " + szStkLyrStkLotTp;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			szMsg="적치단 재료 szStkLyrStkLotCd : " + szStkLyrStkLotCd;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        	
        	//To위치가 공베드 = 3등급
        	if(szStkLyrStkLotTp.equals("") && szStkLyrStkLotCd.equals("")) {
        		intRtnVal = 3;
        		System.out.println("저장위치 등급 : " + intRtnVal);
        		return intRtnVal;
        	}
        	

        	
        	//산적LotType가 같은 경우
        	if(szStkLyrStkLotTp.equals(szCrnWrkMtlStkLotTp) ) {
        		
        		//산적LotType와 Code가 같은경우 1등급
        		if(szStkLyrStkLotCd.equals(szCrnWrkMtlStkLotCd) ) {
        			intRtnVal = 1;
            		System.out.println("저장위치 등급 : " + intRtnVal);
        			return intRtnVal;
        		
        		//코드가 틀린 경우 2등급
        		}else{
        			intRtnVal = 2;
            		System.out.println("저장위치 등급 : " + intRtnVal);
        			return intRtnVal;
        		}		
        	
        	//산적Lot Type가 틀린경우
        	}else{
        		intRtnVal = 4;
        		System.out.println("저장위치 등급 : " + intRtnVal);
        		return intRtnVal;
        		
        	}

    
        }catch(Exception e){
			System.out.println("<Y5GradeTestCoil> Exception Error :"+ e.getLocalizedMessage());
			return intRtnVal = -1;
        }//end of try~catch
    	
    }//end of Y5GradeTestCoil()
    

    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 등록
     *  
     * @param  rsResultCrnwrkmtl, recGetRsSet, intGrade
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    	//상위 Method : chkLocsrchbedCrnMtl
    public int Y5UpdGradStkLyrCoil (JDTORecord recResultCrnwrkmtl, JDTORecord recGetRsSet){
		//┏━┓
		//┃ 저장위치의 등급에 따라 적치단에 재료를 등록한다.
		//┗━┛
    	
    	int intRtnVal = 0;
    	YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
    	
    	
    	JDTORecord recUpdStkLyrData = null;
    	JDTORecord recSetStkLyrData = null;
    	JDTORecord recUpdCrnSchData = null;
    	JDTORecordSet rsGetStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");

    	JDTORecordSet outRecSet = null;
    	JDTORecord setRecord = null;
    	JDTORecord outRecord = null;
    	
        
        String szMsg               = "";
        String szMethodName        = "Y5UpdGradStkLyrCoil";
        
        String szQuery = "";
        String szStkLyr = "";
        String szYD_DN_WR_LOC = "";
        String szStkBedNo = "";
        
        try{

        	recUpdStkLyrData = JDTORecordFactory.getInstance().create();
			recUpdStkLyrData.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_COL_GP"));
			recUpdStkLyrData.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_BED_NO"));
			//저장할 재료
			recUpdStkLyrData.setField("STL_NO",        ydDaoUtils.paraRecChkNull(recResultCrnwrkmtl, "STL_NO")); 
			//권하대기상태로 적치단재료활성상태 변경
			recUpdStkLyrData.setField("YD_STK_LYR_MTL_STAT", "D");
			
			//공베드 인 경우
			if ( ydDaoUtils.paraRecChkNull(recGetRsSet,    "YD_STK_LYR_NO").equals("")) {
				recUpdStkLyrData.setField("YD_STK_LYR_NO", "001");
			}else{
			//공베드가 아닌 경우
				recUpdStkLyrData.setField("YD_STK_LYR_NO", "002");
			}
			
   			//적치단에 Update
			intRtnVal = ydStkLyrDao.updYdStklyr(recUpdStkLyrData, 0);
			System.out.println("intRtnVal : " + intRtnVal );
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="<Y5UpdGradStkLyrCoil> updYdStklyr data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
    			}else if(intRtnVal == -2) {
    				szMsg="<Y5UpdGradStkLyrCoil> updYdStklyr parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else{
    				szMsg="<Y5UpdGradStkLyrCoil> updYdStklyr execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			return intRtnVal = -1;
			}
			
			

			//크레인 스케줄  권하지시위치 업데이트
			recUpdCrnSchData = JDTORecordFactory.getInstance().create();
			recUpdCrnSchData.setField("YD_CRN_SCH_ID",  recResultCrnwrkmtl.getFieldString("YD_CRN_SCH_ID") );
			recUpdCrnSchData.setField("YD_WRK_PROG_STAT",  "W" );
			recUpdCrnSchData.setField("YD_DN_WO_LOC",   recUpdStkLyrData.getFieldString("YD_STK_COL_GP") + recUpdStkLyrData.getFieldString("YD_STK_BED_NO"));
			recUpdCrnSchData.setField("YD_DN_WO_LAYER", recUpdStkLyrData.getFieldString("YD_STK_LYR_NO") );
			intRtnVal = this.Y5UpdYdCrnsch(recUpdCrnSchData, 0);
			if(intRtnVal == -1) {
				szMsg="크레인 스케줄 권하지시위치 등록 실패!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
    
			
			szYD_DN_WR_LOC = ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_DN_WO_LOC");
			szStkLyr = ydDaoUtils.paraRecChkNull(recUpdStkLyrData, "YD_STK_LYR_NO");
			
			
			if(szStkLyr.equals("001") ) {
	        	
                //실적위치의 좌측 Bed 1단 Select
	        	outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
                setRecord = JDTORecordFactory.getInstance().create();
                szStkBedNo = "0" + ( (Integer.parseInt(szYD_DN_WR_LOC.substring(6,8)) - 1 ) );
                setRecord.setField("YD_STK_COL_GP",       szYD_DN_WR_LOC.substring(0,6));    
                setRecord.setField("YD_STK_BED_NO",       szStkBedNo);  
                setRecord.setField("YD_STK_LYR_NO",       "001") ;
                //적치단 상태조회		:적치불가인지 아닌지 알기위해서...
                intRtnVal = this.Y5GetYdStklyrCoil(setRecord, outRecSet, 0);
                //에러 메시지
                if(intRtnVal <= 0) {
        	        szMsg = "좌측Bed정보가 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
                }else{
                    outRecSet.first() ;
                    outRecord = outRecSet.getRecord() ;
                    
                    //적치불가라면 적치가능으로 업데이트
                    if(outRecord.getFieldString("YD_STK_LYR_MTL_STAT").equals("C") || outRecord.getFieldString("YD_STK_LYR_MTL_STAT").equals("D")) {

                    	//실적위치 좌측 Bed 2단 Select	
                    	outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
                        setRecord = JDTORecordFactory.getInstance().create();
                        szStkBedNo = "0" + ( (Integer.parseInt(szYD_DN_WR_LOC.substring(6,8)) - 1 ) );
                        setRecord.setField("YD_STK_COL_GP",       szYD_DN_WR_LOC.substring(0,6));    
                        setRecord.setField("YD_STK_BED_NO",       szStkBedNo);  
                        setRecord.setField("YD_STK_LYR_NO",       "002") ;
                        //적치단 상태조회		:적치불가인지 아닌지 알기위해서...
                        intRtnVal = this.Y5GetYdStklyrCoil(setRecord, outRecSet, 0);
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
                        	intRtnVal = this.Y5UpdYdStklyrCoil(setRecord, 1); 
                        	//에러 메시지
                        	//
                        }
                    	
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
                intRtnVal = this.Y5GetYdStklyrCoil(setRecord, outRecSet, 0);
                if(intRtnVal <= 0) {
        	        szMsg = "우좌측Bed정보가 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
                }else{
                    //에러 메시지
                    outRecSet.first() ;
                    outRecord = outRecSet.getRecord() ;
                    
                    //적치불가라면 적치가능으로 업데이트
                    if(outRecord.getFieldString("YD_STK_LYR_MTL_STAT").equals("C") || outRecord.getFieldString("YD_STK_LYR_MTL_STAT").equals("D")) {
                    
                    	//실적위치Bed 왼쪽 2단 Select
                    	outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
                    	setRecord = JDTORecordFactory.getInstance().create() ;
                    	//실적위치 Bed -1 = 실적위치Bed 왼쪽 2단
       
                        setRecord.setField("YD_STK_COL_GP",       szYD_DN_WR_LOC.substring(0,6));    
                        setRecord.setField("YD_STK_BED_NO",       szYD_DN_WR_LOC.substring(6,8)); 
                        setRecord.setField("YD_STK_LYR_NO",       "002") ;
                        
                        //적치단 상태조회		: 적치불가인지 아닌지 알기위해서...
                        intRtnVal = this.Y5GetYdStklyrCoil(setRecord, outRecSet, 0);
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
                        	intRtnVal = this.Y5UpdYdStklyrCoil(setRecord, 1); 
                        }
                    	
                    }
                }

	        }
			
        }catch(Exception e){
			System.out.println("<Y5UpdGradStkLyrCoil> Exception Error :"+ e.getLocalizedMessage());
			return intRtnVal = -1;
        }//end of try~catch
    	return intRtnVal = 1;
    }//end of Y5UpdGradStkLyrCoil()
    //
    
    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y5UpdYdStklyrCoil (JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
    	
    	int intRtnVal = 0 ;
        
        try{
        	
	    	intRtnVal = ydStklyrDao.updYdStklyr(msgRecord, intGp);  //적치단의 재료정보 Clear
	    	
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
	    }//end of try~catch
		
		return intRtnVal ;
		
    }//end of Y5UpdYdStklyrCoil
    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 Select
     *  
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y5GetYdStklyrCoil (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
    	
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
    	
    }//end of Y5GetYdStklyrCoil()
    
    
    
    
    /**
     * 오퍼레이션명 : 크레인스케줄 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int Y5UpdYdCrnsch(JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

    	int intRtnVal = 0 ;
    	
    	String szMsg        = "";
    	String szMethodName = "Y5UpdYdCrnsch";

		try{
			
			intRtnVal = ydCrnschDao.updYdCrnsch(msgRecord, intGp);		        
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="<Y5UpdYdCrnsch> updYdCrnsch data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
    			}else if(intRtnVal == -1) {
    				szMsg="<Y5UpdYdCrnsch> updYdCrnsch duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="<Y5UpdYdCrnsch> updYdCrnsch parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="<Y5UpdYdCrnsch> updYdCrnsch execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			return intRtnVal = -1;
    		}
			
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of Y5UpdYdCrnsch
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    









/**
	 * 오퍼레이션명 : 스케줄기준 체크 및 데이터 반환(H/J)
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
	 * 오퍼레이션명 : 설비상태 체크(H/J)
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
	 *      [A] 오퍼레이션명 : 전문전송버퍼
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procForwardTcRecord(JDTORecord msgRecord)throws JDTOException  {
		String szMsg			=	"";
		String szMethodName		=	"procForwardTcRecord";
		String szOperationName	=	"전문전송버퍼";
		
		JDTORecord recPara 		= null;
		
		String szFieldName		= null;
		String szFieldValue		= null;
		

		String szRcvTcCode=ydUtils.getTcCode(msgRecord);
		if( szRcvTcCode==null || szRcvTcCode.equals("") ){
			szMsg = "["+ szOperationName +"] TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			return;
		}
		if(bDebugFlag){
			szMsg="["+ szOperationName +"] 전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} 
		szMsg="["+ szOperationName +"] ----------------- 메소드 시작 - 전문확인 ----------------";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		ydUtils.displayRecord(szOperationName, msgRecord);
		
		//JDTORecord recPara = (JDTORecord)msgRecord.getField(YdConstant.TC_BODY);
		
		//ydUtils.displayRecord(szOperationName, recPara);
		
		szMsg="["+ szOperationName +"] ----------------- 전문변환 전 ----------------";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		recPara	= JDTORecordFactory.getInstance().create();
		
		Iterator iter = msgRecord.iterateName();
		
//		while( iter.hasNext() ) {
//			szFieldName = (String)iter.next();
//			if( szFieldName.equals(YdConstant.JMS_TC_CD) ||
//				szFieldName.equals(YdConstant.MSG_ID) ||
//				szFieldName.equals(YdConstant.TC_CODE)
//			) {
//				continue;
//			}
//		
//			szFieldValue = msgRecord.getFieldString(szFieldName);
//		
//			if( szFieldName.equals(YdConstant.BUFFER_TC_CD)) {
//				recPara.setField(YdConstant.JMS_TC_CD, szFieldValue);
//			}else{
//				recPara.setField(szFieldName, szFieldValue);
//			}
//		}			
		
		// PIDEV
		while( iter.hasNext() ) {
			szFieldName = (String)iter.next();
			if( szFieldName.equals(YdConstant.JMS_TC_CD) ||
					szFieldName.equals(YdConstant.MSG_ID) ||
					szFieldName.equals(YdConstant.TC_CODE)||
					szFieldName.equals("MQ_TC_CD")
			) {
				continue;
			}
			
			szFieldValue = msgRecord.getFieldString(szFieldName);
			
			if( szFieldName.equals(YdConstant.BUFFER_TC_CD)) {
				if (szFieldValue.startsWith("M10")) {
					recPara.setField("MQ_TC_CD", szFieldValue);
				} else {
					recPara.setField(YdConstant.JMS_TC_CD, szFieldValue);
				}
			}else{
				recPara.setField(szFieldName, szFieldValue);
			}
		}
		
		szMsg="["+ szOperationName +"] ----------------- 전문변환 후 파라미터 확인 시작 ----------------";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		ydUtils.displayRecord(szOperationName, recPara);
		
		szMsg="["+ szOperationName +"] ----------------- 전문변환 후 파라미터 확인 완료 후 전송 ----------------";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		ydDelegate.sendMsg(recPara);
		
		szMsg="["+ szOperationName +"] ----------------- 메소드 끝 ----------------";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	}// end of procForwardTcRecord()
  //---------------------------------------------------------------------------	
	
	   /**
     * 오퍼레이션명 : 차량상차 Bed 크레인작업재료 비교 Check
     *  
     * @param rsBed, rsCrnWrkMtl, recCrnWrkMtl, recCrnWrkMtl
     * @return int 1, -1
     * @throws 
     */
    public JDTORecord Y5ChkCarLdbedCrnMtl (JDTORecordSet rsBed, JDTORecord recGetCrnWrkMtl) throws JDTOException  {
    	//상위 Method : DnLocInsMain
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
		//┗━┛
    	
    	YdStkBedDao    ydStkBedDao    = new YdStkBedDao();
    	YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao(); 
    	YdCrnSchDao    ydCrnSchDao    = new YdCrnSchDao(); 
    	YdEqpDao       ydEqpDao       = new YdEqpDao(); 
    	YdStkLyrDao    ydStkLyrDao    = new YdStkLyrDao();
    	YdDBAssist     ydDBAssist     = new YdDBAssist();
    	
    	YdDaoUtils ydDaoUtils = new YdDaoUtils();
    	JDTORecord 		outRecord1 	= JDTORecordFactory.getInstance().create();
    	JDTORecord 		outRecord 	= JDTORecordFactory.getInstance().create();
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
    	
    	JDTORecord recInTemp    = null;
    
    	JDTORecordSet rsResult = null;
    	
    	
    	int intRtnVal 		= 0 ;
        String szMsg        = "";
        String szMethodName = "Y5ChkCarLdbedCrnMtl";
        String szOperationName		= "차량상차Bed크레인작업재료비교";
        
        //적치베드의 Max단,중량,높이
        int intStkBedLyrMax        = 0;
        long lngStkBedWtMax        = 0;
        double lngStkBedHMax         = 0;
        
        //크레인작업재료의 총매수,중량,높이
        int intCrnWrkMtlSh         = 0;
        long lngCrnWrkMtlWt        = 0;
        double lngCrnWrkMtlT         = 0;
        
        //적치단의 적치중인 재료의 총매수,중량,높이
        int intStkLyrMax           = 0;
        long lngStkLyrWtMax 	   = 0;
        double lngStkLyrHMax         = 0;
        
        //크레인작업재료의 최하단 산적LotType,산적Lot코드,폭
        String szCrnWrkMtlStkLotTp = "";
        String szCrnWrkMtlStkLotCd = "";
        long lngCrnWrkMtlW		   = 0;
        
        //적치단의 최상단 산적LotType,산적Lot코드,폭
        String szStkLyrStkLotTp    = "";
        String szStkLyrStkLotCd    = "";
        double lngStkLyrW		       = 0;
        
        String szStkColGp          = "";
        String szStkBedNo          = ""; 
        String szStkLyrNo			="";
        String szGradeStlNo  		="";
        String szStlNo				="";
        //등급
        int intGrade               = 101;
        //이전등급중 가장 좋은 등급
        int intGradeSave           = 100;
        //등급의 적치단정보
        String szGradeColGp          = "";
        String szGradeBedNo          = "";
        String szGradeLyrNo          = "";
        
        String szQuery     = "";
        String szCrnSchId  = "";
        String szYD_EQP_ID = "";
        String szYD_SCH_CD = "";
        
        //야드적치Bed용도구분
        String szYD_STK_BED_USG_GP = null;
        
        try{
        	
        	//크레인 작업재료 조회
        	szCrnSchId  = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl, "YD_CRN_SCH_ID");
        	szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl, "YD_SCH_CD");
        	
//        	recInTemp = JDTORecordFactory.getInstance().create();
//        	recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
//        	intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recInTemp, rsResultCrnwrkmtl, 8);
        	
        	rsBed.beforeFirst();
        	
//        	szMsg= "*********"+rsResultCrnwrkmtl.size()+"-"+rsBed.size();
//        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	
        	//적치베드단위로 Loop
        	for(int Loop_i = 1; Loop_i <= rsBed.size(); Loop_i++) {
        		rsBed.absolute(Loop_i);
        		recStkBed = JDTORecordFactory.getInstance().create();
        		recStkBed.setRecord(rsBed.getRecord());
        		
        		
        		//열구분과 베드번호를 읽어온다.
        		szStkColGp = recStkBed.getFieldString("YD_STK_COL_GP");
        		szStkBedNo = recStkBed.getFieldString("YD_STK_BED_NO");
        		szStkLyrNo = "001";
        		szStlNo	= recGetCrnWrkMtl.getFieldString("STL_NO");
        		
szMsg= "["+szOperationName+"] " + Loop_i + "번째 Bed : 적치열구분["+szStkColGp+"], 적치베드번호["+szStkBedNo+"], 코일번호 ["+szStlNo+"]로 조회 시작";
ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
ydUtils.displayRecord(szOperationName, recStkBed);
        		
        		
        		

        		//적치Bed조회한다.
        		rsGetStkBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
        		intRtnVal = ydStkBedDao.getYdStkbed(recStkBed, rsGetStkBed, 0) ;
        		if(intRtnVal <= 0) {
        			if(intRtnVal == 0) {
        				szMsg="["+szOperationName+"] 적치열구분["+szStkColGp+"], 적치베드번호["+szStkBedNo+"]로 적치Bed조회 data not found";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
        			}else if(intRtnVal == -2) {
        				szMsg="["+szOperationName+"] 적치열구분["+szStkColGp+"], 적치베드번호["+szStkBedNo+"]로 적치Bed조회 parameter error";
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        			}
    				szMsg="["+szOperationName+"] 다음 적치BED를 조회합니다.";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				continue;
        		}
        		
        		//적치베드의 Max단,중량,높이
        		rsGetStkBed.first();
        		recGetRsSet = rsGetStkBed.getRecord();
        		
        		

ydUtils.displayRecord(szOperationName, recGetRsSet);
        		
        		intStkBedLyrMax = ydDaoUtils.paraRecChkNullInt (recGetRsSet,"YD_STK_BED_LYR_MAX");
        		lngStkBedWtMax  = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"YD_STK_BED_WT_MAX");       
        		lngStkBedHMax   = ydDaoUtils.paraRecChkNullDouble(recGetRsSet,"YD_STK_BED_H_MAX"); 
        		if(intStkBedLyrMax <= 0) {
    				szMsg="["+szOperationName+"] 적치Bed정보에 최대적치단의 정보가 없습니다.";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        		}else if(lngStkBedWtMax <= 0){
    				szMsg="["+szOperationName+"] 적치Bed정보에 최대중량의 정보가 없습니다.";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        		}else if(lngStkBedHMax <= 0){
    				szMsg="["+szOperationName+"] 적치Bed정보에 최대높이의 정보가 없습니다.";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        		}
        		
        		
        		//크레인작업재료의 총매수 총중량 총높이를 가져온다. 		
				szMsg="["+szOperationName+"] 크레인작업재료의 총매수 총중량 총높이를 가져온다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
        		intCrnWrkMtlSh      = ydDaoUtils.paraRecChkNullInt (recGetCrnWrkMtl,"SH_CNT");
        		lngCrnWrkMtlWt      = ydDaoUtils.paraRecChkNullLong(recGetCrnWrkMtl,"SUM_MTL_WT");
        		lngCrnWrkMtlT       = ydDaoUtils.paraRecChkNullDouble(recGetCrnWrkMtl,"SUM_MTL_T");
        		
        		szMsg="크레인작업재료의 총매수 :" + intCrnWrkMtlSh;
				szMsg+=", 총중량 :" + lngCrnWrkMtlWt;
				szMsg+=", 총높이  :" + lngCrnWrkMtlT;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        		//크레인작업재료의 최하단 재료정보
				//lngCrnWrkMtlW       = ydDaoUtils.paraRecChkNullLong(recGetCrnWrkMtl,"SUM_MTL_W");
        		szCrnWrkMtlStkLotTp = ydDaoUtils.paraRecChkNull    (recGetCrnWrkMtl,"YD_STK_LOT_TP");
        		szCrnWrkMtlStkLotCd = ydDaoUtils.paraRecChkNull    (recGetCrnWrkMtl,"YD_STK_LOT_CD");
        		
        		szMsg="["+szOperationName+"] 크레인작업재료의 최하단 재료정보의 산적LOT TYPE["+szCrnWrkMtlStkLotTp+"], 산적LOT코드["+szCrnWrkMtlStkLotCd+"]";
        		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		
				//적치Bed의 적치중이거나 권하대기상태인 재료정보를 가져온다.
				recStkLyr = JDTORecordFactory.getInstance().create();
				recStkLyr.setField("YD_STK_COL_GP", szStkColGp);
				recStkLyr.setField("YD_STK_BED_NO", szStkBedNo);
				
				rsGetStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
				intRtnVal = ydStkBedDao.getYdStkbed(recStkLyr, rsGetStkLyr, 19);
				recGetRsSet = JDTORecordFactory.getInstance().create();
				if(intRtnVal <= 0) {
					if(intRtnVal == 0) {
						//베드가 존재 하지 않을경우
						szMsg="["+szOperationName+"] 베드가 존재 하지 않음!!!!!!";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
					}else if(intRtnVal == -2) {
						szMsg="["+szOperationName+"] getYdStklyr parameter error";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					continue;
				}
				rsGetStkLyr.absolute(1);
				recGetRsSet.setRecord(rsGetStkLyr.getRecord());
				
				
				ydUtils.putLog(szSessionName, szMethodName, "확인요망"+ydDaoUtils.paraRecChkNull(recGetRsSet, "STL_NO"), YdConstant.DEBUG);
				if (!ydDaoUtils.paraRecChkNull(recGetRsSet, "STL_NO").equals("")){
					rsGetStkLyr.absolute(1);
					recGetRsSet = JDTORecordFactory.getInstance().create();
	        		recGetRsSet.setRecord(rsGetStkLyr.getRecord());
	        		
	        		//적치단에적치중인 총매수 중량 높이
	        		intStkLyrMax     = ydDaoUtils.paraRecChkNullInt (recGetRsSet,"SH_CNT");
	       			lngStkLyrWtMax   = ydDaoUtils.paraRecChkNullLong(recGetRsSet,"SUM_MTL_WT");
	       			lngStkLyrHMax    = ydDaoUtils.paraRecChkNullDouble(recGetRsSet,"SUM_MTL_T");
	       			
	       			//적치단의최상단  재료정보
	       			lngStkLyrW       = ydDaoUtils.paraRecChkNullDouble(recGetRsSet,"SUM_MTL_W");
	       			szStkLyrStkLotTp = ydDaoUtils.paraRecChkNull    (recGetRsSet,"YD_STK_LOT_TP");
	       			szStkLyrStkLotCd = ydDaoUtils.paraRecChkNull    (recGetRsSet,"YD_STK_LOT_CD");
	       			
	       			//공베드인지 아닌지 구분자
	       			recGetRsSet.setField("YD_STK_BED_NULL_GP", "N");
	       			
	       			intGrade = 2;
	       			//intGrade = -1;
        		}else{
        			recGetRsSet = JDTORecordFactory.getInstance().create();

        			//공베드인지 아닌지 구분자
        			recGetRsSet.setField("YD_STK_BED_NULL_GP", "Y");
        			recGetRsSet.setField("YD_STK_COL_GP", szStkColGp);
        			recGetRsSet.setField("YD_STK_BED_NO", szStkBedNo);
        			
        			intStkLyrMax = 0;
        			lngStkLyrWtMax = 0;
        			lngStkLyrHMax = 0;
        			lngStkLyrW = 0;
        			szStkLyrStkLotTp = "";
        			szStkLyrStkLotCd = "";
        			
        			intGrade = 1;
        		}
				
       			
       			if(intStkBedLyrMax < intCrnWrkMtlSh + intStkLyrMax) {
       				szMsg="["+szOperationName+"] MAX단 초과";
       				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
       				continue;
       			}else if(lngStkBedWtMax  < lngCrnWrkMtlWt + lngStkLyrWtMax) { 
       				szMsg="["+szOperationName+"] MAX중량 초과";
   					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
       				continue;
        		}else if(lngStkBedHMax   < lngCrnWrkMtlT  + lngStkLyrHMax) {
       				szMsg="["+szOperationName+"] MAX높이 초과";
   					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
       				continue;
        		}
       			
       			
   				szMsg="["+szOperationName+"] ===============크레인 하단재료정보============";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
       			ydUtils.displayRecord(szOperationName, recGetCrnWrkMtl);
       			
   				szMsg="["+szOperationName+"] ===============적치베드 상단재료정보===============";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
       			ydUtils.displayRecord(szOperationName, recGetRsSet);
       			
       			
       			
       			//최상의 등급인 경우 종료
       			if (intGrade == 1) {
       				//최상의자리를 찾았다는 메시지
       				szMsg="["+szOperationName+"] 1등급의 위치를 찾았습니다.";
   					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
       				//To위치 저장 후 리턴
   					
   					
   	        		JDTORecord recUpdStkLyrData = JDTORecordFactory.getInstance().create() ;
   					recUpdStkLyrData.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recStkBed, "YD_STK_COL_GP"));
   					recUpdStkLyrData.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(recStkBed, "YD_STK_BED_NO"));
   					recUpdStkLyrData.setField("YD_STK_LYR_NO", "001");
   	   				//저장할 재료
   	   				recUpdStkLyrData.setField("STL_NO",        ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl, "STL_NO")); 
   	   				//권하대기상태로 적치단재료활성상태 변경
   	   				recUpdStkLyrData.setField("YD_STK_LYR_MTL_STAT", "D");
   	   				
   					//적치단에 Update
   	   				intRtnVal = ydStkLyrDao.updYdStklyr(recUpdStkLyrData, 0);
   	   				
	   	   			szMsg="["+szOperationName+"] 1등급의 위치를 찾았습니다.to위치 검색을 종료 합니다.";
	        		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	        		outRecord.setField("RTN_CD" , "1");	
	        		outRecord.setField("RTN_BED" , ydDaoUtils.paraRecChkNull(recStkBed, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recStkBed, "YD_STK_BED_NO")+ "001");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
   	   				
       			}else if(intGrade == -1) {
       				szMsg="["+szOperationName+"] 위치검색 중 Error";
   					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
       				continue;
       			}
       			
       		
       			if(Loop_i == 1) {
		szMsg="["+szOperationName+"] 첫번째 BED의 값을 저장한다.";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

       				intGradeSave = intGrade ;
       				szGradeColGp = szStkColGp;
       				szGradeBedNo = szStkBedNo;
       				//szGradeLyrNo = ydDaoUtils.paraRecChkNull(recGetRsSet,"YD_STK_LYR_NO");  
       				szGradeLyrNo = szStkLyrNo;
       			}
       			
szMsg="["+szOperationName+"] intGradeSave : " + intGradeSave + " = intGrade : " +  intGrade;
ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
szMsg="["+szOperationName+"] szGradeColGp : " + szGradeColGp + " = szStkColGp : " +  szStkColGp;
ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
szMsg="["+szOperationName+"] szGradeBedNo : " + szGradeBedNo + " = szStkBedNo : " +  szStkBedNo;
ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//szMsg="["+szOperationName+"] szGradeLyrNo : " + szGradeLyrNo + " = " + ydDaoUtils.paraRecChkNull(recGetRsSet,"YD_STK_LYR_NO");
szMsg="["+szOperationName+"] szGradeLyrNo : " + szGradeLyrNo + " = " + szStkLyrNo;
ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


       			//저장등급보다 지금 등급이 좋다면 저장
       			if (intGradeSave > intGrade) {
       				intGradeSave = intGrade ;
       				szGradeColGp = szStkColGp;
       				szGradeBedNo = szStkBedNo;
       				//szGradeLyrNo = recGetRsSet.getFieldString("YD_STK_LYR_NO");
       				szGradeLyrNo = szStkLyrNo;
       				szGradeStlNo = szStlNo;
       				
       				
       				szMsg="["+szOperationName+"] intGradeSave : " + intGradeSave + " = intGrade : " +  intGrade;
       				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
       				szMsg="["+szOperationName+"] szGradeColGp : " + szGradeColGp + " = szStkColGp : " +  szStkColGp;
       				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
       				szMsg="["+szOperationName+"] szGradeBedNo : " + szGradeBedNo + " = szStkBedNo : " +  szStkBedNo;
       				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
       				//szMsg="["+szOperationName+"] szGradeLyrNo : " + szGradeLyrNo + " = " + recGetRsSet.getFieldString("YD_STK_LYR_NO");
       				szMsg="["+szOperationName+"] szGradeLyrNo : " + szGradeLyrNo + " = " + szStkLyrNo;
       				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
       				
       			}
       			
        	}//end of for
        	//적치베드가 모두 Error일 경우 Error처리 후 종료한다.
        	if (intGradeSave == 100) {
        		szMsg="["+szOperationName+"] 적치베드가 모두 Error일 경우";
        		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
        	}
        	
        	
        	
        	szMsg="["+szOperationName+"] =================적치단에 등록 전 data============";
        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	szMsg="["+szOperationName+"] szGradeColGp : " + szGradeColGp ;
        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	szMsg="["+szOperationName+"] szGradeBedNo : " + szGradeBedNo;
        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	szMsg="["+szOperationName+"] szGradeLyrNo : " + szGradeLyrNo;
        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	
         	//적치단정보 Setting
        	recGetRsSet = JDTORecordFactory.getInstance().create();
        	recGetRsSet.setField("YD_STK_COL_GP", szGradeColGp);
        	recGetRsSet.setField("YD_STK_BED_NO", szGradeBedNo);
        	recGetRsSet.setField("YD_STK_LYR_NO", szGradeLyrNo);
			//저장할 재료
        	recGetRsSet.setField("STL_NO",        szGradeStlNo); 
			//권하대기상태로 적치단재료활성상태 변경
        	recGetRsSet.setField("YD_STK_LYR_MTL_STAT", "D");
	 
			//적치단에 Update
			intRtnVal = ydStkLyrDao.updYdStklyr(recGetRsSet, 0);
			
        }catch(Exception e){
			szMsg="["+szOperationName+"] Exception Error :"+ e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;
        }//end of try~catch
        
        
		outRecord.setField("RTN_CD" , "1");	
		outRecord.setField("RTN_BED" , szGradeColGp + szGradeBedNo + "001");	
		
		return outRecord;
    	
    }//end of Y5ChkCarLdbedCrnMtl()
    
    /**
     * 오퍼레이션명 : 적치단 좌표 값 등록
     *  
     * @param  rsResultCrnwrkmtl, recGetRsSet, intGrade
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public JDTORecord Y5UpdGradStkLyr (JDTORecord rsResultCrnwrkmtl, JDTORecord recGetRsSet) throws JDTOException  {
    	//상위 Method : chkLocsrchbedCrnMtl
		//┏━┓
		//┃ 저장위치의 등급에 따라 적치단에 재료를 등록한다.
		//┗━┛
    	
    	int intRtnVal = 0;
    	YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
    	YdStkBedDao ydStkBedDao = new YdStkBedDao();
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
    	YdEqpDao	ydEqpDao 	= new YdEqpDao();
    	JDTORecord recUpdStkLyrData = null;
    	JDTORecord recSetStkLyrData = null;
    	JDTORecord recUpdCrnSchData = null;
    	JDTORecord recGetStkBedData = null;
    	JDTORecord recUpStkBed      = null;
    	JDTORecord recDnStkBed     = null;
    	JDTORecord recInPara       = null;
    	
    	
    	JDTORecordSet rsGetStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet rsUpStkBed  = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet rsDnStkBed  = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet rsGetCrnSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet rsGetStkLyrT = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecord 		outRecord 	= JDTORecordFactory.getInstance().create();
        
        String szMsg               = "";
        String szMethodName        = "Y5UpdGradStkLyr";
        String szOperationName     = "적치단 좌표 값 등록";
        double dblSUM_MTL_T = 0;
        int intRtnVal1 = 0;
    	
        String szQuery = "";
		String sGRP_GP_CD       = "";
		String sUSE_YN          = "";
		String sBRE_CHK3        = "N";
        String szYD_UP_WO_LOC_ZAXIS = "";
        String szYD_DN_WO_LOC_ZAXIS = "";
        String szYD_SCH_CD			= null;
        String szFR_WO_LOC    = "";
        String szTO_WO_LOC    = "";

        String szFR_WO_LOC_LAY	= "";
        String szTO_WO_LOC_LAY	= "";
        
        String sUP_YD_STK_LYR_XAXIS 		= "";
        String sUP_YD_STK_BED_XAXIS_TOL 	= "";
        String sUP_YD_STK_LYR_YAXIS		= "";
        String sUP_YD_STK_BED_YAXIS_TOL 	= "";

        String sDN_YD_STK_LYR_XAXIS 		= "";
        String sDN_YD_STK_BED_XAXIS_TOL 	= "";
        String sDN_YD_STK_LYR_YAXIS 		= "";
        String sDN_YD_STK_BED_YAXIS_TOL 	= "";
		
        String sUP_YD_ROTATION_ANGLE 	= "";
        String sDN_YD_ROTATION_ANGLE 	= "";
        
        int Loop_i = 1;
        
        try{
        	
    		szMsg="recSetStkLyrData 정보조회!!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
    		ydUtils.displayRecord(szOperationName, rsResultCrnwrkmtl);
    		
    		dblSUM_MTL_T = ydDaoUtils.paraRecChkNullDouble(rsResultCrnwrkmtl, "SUM_MTL_T");
    		
//    		szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_COL_GP");
//    		szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_BED_NO");
//    		szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_LYR_NO");
			
    		szYD_DN_WO_LOC_ZAXIS = Double.toString(dblSUM_MTL_T);
				
			int idx		= szYD_DN_WO_LOC_ZAXIS.lastIndexOf(".");
			if( idx >= 0 ) {
				szYD_DN_WO_LOC_ZAXIS		= szYD_DN_WO_LOC_ZAXIS.substring(0, idx);
			}
			

			rsGetCrnSch = JDTORecordFactory.getInstance().createRecordSet("");
			recUpdCrnSchData = JDTORecordFactory.getInstance().create();
			recUpdCrnSchData.setField("YD_CRN_SCH_ID",  rsResultCrnwrkmtl.getFieldString("YD_CRN_SCH_ID") );
			
			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnsch*/
			intRtnVal = ydCrnschDao.getYdCrnsch(recUpdCrnSchData, rsGetCrnSch, 0);
			if(intRtnVal <= 0){
				szMsg="Y5UpdGradStkLyr getYdCrnsch : execution failed";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;

			} 
			rsGetCrnSch.absolute(1);
			recUpdCrnSchData = JDTORecordFactory.getInstance().create();
			recUpdCrnSchData.setRecord(rsGetCrnSch.getRecord());
			
			szYD_SCH_CD		= ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_SCH_CD");
			szFR_WO_LOC		= ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_UP_WO_LOC");
			szTO_WO_LOC		= ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_DN_WO_LOC");
			szFR_WO_LOC_LAY = ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_UP_WO_LAYER");
			szTO_WO_LOC_LAY = ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_DN_WO_LAYER");
			
			if(szFR_WO_LOC.equals("")){
				sUP_YD_STK_LYR_XAXIS 		= "0";
				sUP_YD_STK_BED_XAXIS_TOL 	= "0";
				sUP_YD_STK_LYR_YAXIS		= "0";
				sUP_YD_STK_BED_YAXIS_TOL 	= "0";
				sUP_YD_ROTATION_ANGLE       = "0";
				
			} else {
				szMsg="권상지시베드조회 전.. ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//권상지시베드조회
				recGetStkBedData = JDTORecordFactory.getInstance().create();
				rsUpStkBed = JDTORecordFactory.getInstance().createRecordSet("");
				recGetStkBedData.setField("YD_STK_COL_GP", szFR_WO_LOC.substring(0,6) );
				recGetStkBedData.setField("YD_STK_BED_NO", szFR_WO_LOC.substring(6,8) );
				recGetStkBedData.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_UP_WO_LAYER"));    
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedLyr*/
				intRtnVal = ydStkBedDao.getYdStkbed(recGetStkBedData, rsUpStkBed, 304);
				if(intRtnVal <= 0){
					szMsg="Y5UpdGradStkLyr getYdStkbed : execution failed";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
	
				}
				rsUpStkBed.absolute(1);
				recUpStkBed = JDTORecordFactory.getInstance().create();
				recUpStkBed.setRecord(rsUpStkBed.getRecord());
	
//				szMsg="권상지시베드조회 후.. ";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				
//				recInPara = JDTORecordFactory.getInstance().create();
//				rsGetStkLyrT = JDTORecordFactory.getInstance().createRecordSet("");
//				recInPara.setField("YD_STK_COL_GP", szFR_WO_LOC.substring(0,6));
//				recInPara.setField("YD_STK_BED_NO", szFR_WO_LOC.substring(6,8));
//				recInPara.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_UP_WO_LAYER"), -1));
//				intRtnVal = ydStkLyrDao.getYdStklyr(recInPara, rsGetStkLyrT, 71);
//				if( intRtnVal <= 0 ) {
//					szYD_UP_WO_LOC_ZAXIS = "0";
//				}else{
//					rsGetStkLyrT.absolute(1);
//					recInPara = JDTORecordFactory.getInstance().create();
//					recInPara.setRecord(rsGetStkLyrT.getRecord());
//					szYD_UP_WO_LOC_ZAXIS = String.valueOf(ydDaoUtils.paraRecChkNullDouble(recInPara, "SUM_MTL_T"));
//				}
				
				sUP_YD_STK_LYR_XAXIS 		= ydDaoUtils.paraRecChkNull(recUpStkBed, "YD_STK_LYR_XAXIS");
				sUP_YD_STK_BED_XAXIS_TOL 	= ydDaoUtils.paraRecChkNull(recUpStkBed, "YD_STK_BED_XAXIS_TOL");
				sUP_YD_STK_LYR_YAXIS		= ydDaoUtils.paraRecChkNull(recUpStkBed, "YD_STK_LYR_YAXIS");
				sUP_YD_STK_BED_YAXIS_TOL 	= ydDaoUtils.paraRecChkNull(recUpStkBed, "YD_STK_BED_YAXIS_TOL");
				
				sUP_YD_ROTATION_ANGLE       = ydDaoUtils.paraRecChkNull(recUpStkBed, "YD_ROTATION_ANGLE");
				
			}

			if(szTO_WO_LOC.equals("")){
				sDN_YD_STK_LYR_XAXIS 		= "0";
				sDN_YD_STK_BED_XAXIS_TOL 	= "0";
				sDN_YD_STK_LYR_YAXIS 		= "0";
				sDN_YD_STK_BED_YAXIS_TOL 	= "0";
				sDN_YD_ROTATION_ANGLE	 	= "0";
				
			} else {
			
				//권하지시베드조회
				rsDnStkBed = JDTORecordFactory.getInstance().createRecordSet("");
				recGetStkBedData = JDTORecordFactory.getInstance().create();
				recGetStkBedData.setField("YD_STK_COL_GP", szTO_WO_LOC.substring(0,6) );
				recGetStkBedData.setField("YD_STK_BED_NO", szTO_WO_LOC.substring(6,8) );
				recGetStkBedData.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_DN_WO_LAYER"));    
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedLyr*/
				intRtnVal = ydStkBedDao.getYdStkbed(recGetStkBedData, rsDnStkBed, 304);
				if(intRtnVal <= 0){
					szMsg="Y5UpdGradStkLyr getYdStkbed : execution failed";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
	
				}
				rsDnStkBed.absolute(1);
				recDnStkBed = JDTORecordFactory.getInstance().create();
				recDnStkBed.setRecord(rsDnStkBed.getRecord());
				
				sDN_YD_STK_LYR_XAXIS 		= ydDaoUtils.paraRecChkNull(recDnStkBed, "YD_STK_LYR_XAXIS");
				sDN_YD_STK_BED_XAXIS_TOL 	= ydDaoUtils.paraRecChkNull(recDnStkBed, "YD_STK_BED_XAXIS_TOL");
				sDN_YD_STK_LYR_YAXIS 		= ydDaoUtils.paraRecChkNull(recDnStkBed, "YD_STK_LYR_YAXIS");
				sDN_YD_STK_BED_YAXIS_TOL 	= ydDaoUtils.paraRecChkNull(recDnStkBed, "YD_STK_BED_YAXIS_TOL");
				sDN_YD_ROTATION_ANGLE	 	= ydDaoUtils.paraRecChkNull(recDnStkBed, "YD_ROTATION_ANGLE");
			
			}

        	//BRE 사용 조건 READ	
			JDTORecordSet rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
        	recInTemp.setField("TEMP" 	, "0");	
	    	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB701*/
	    	intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 701);
			if(intRtnVal <= 0) {
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
    		}
			
			for(int Loop1_i = 1; Loop1_i <= rsResult.size(); Loop1_i++) {
				JDTORecord recOutTemp = JDTORecordFactory.getInstance().create();
				rsResult.absolute(Loop1_i);
				recOutTemp.setRecord(rsResult.getRecord());

				sGRP_GP_CD	= ydDaoUtils.paraRecChkNull(recOutTemp, "GRP_GP_CD");	//CHECK_GPT		
				sUSE_YN		= ydDaoUtils.paraRecChkNull(recOutTemp, "USE_YN");	//사용가능 Y,N	
				
				if((sGRP_GP_CD.equals("3")) && (sUSE_YN.equals("Y"))) {
					sBRE_CHK3 = "Y";
				}
			}		

    		ydUtils.putLog(szSessionName, szMethodName, "sBRE_CHK3구분:" + sBRE_CHK3, YdConstant.INFO);
    		
			
			szMsg="크레인스케줄 권하지시위치 Update0";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			//크레인 스케줄  권하지시위치 업데이트
			recUpdCrnSchData = JDTORecordFactory.getInstance().create();
			recUpdCrnSchData.setField("YD_CRN_SCH_ID",  				rsResultCrnwrkmtl.getFieldString("YD_CRN_SCH_ID") );
			recUpdCrnSchData.setField("YD_WRK_PROG_STAT",  				"W" ) ;
			
			recUpdCrnSchData.setField("YD_EQP_WRK_SH",    				ydDaoUtils.paraRecChkNull(rsResultCrnwrkmtl, "SH_CNT"));
			recUpdCrnSchData.setField("YD_EQP_WRK_WT",    				ydDaoUtils.paraRecChkNull(rsResultCrnwrkmtl, "SUM_MTL_WT"));
			recUpdCrnSchData.setField("YD_EQP_WRK_T",     				ydDaoUtils.paraRecChkNull(rsResultCrnwrkmtl, "SUM_MTL_T"));
			recUpdCrnSchData.setField("YD_EQP_WRK_MAX_W", 				ydDaoUtils.paraRecChkNull(rsResultCrnwrkmtl, "MAX_MTL_W"));
			recUpdCrnSchData.setField("YD_EQP_WRK_MAX_L", 				ydDaoUtils.paraRecChkNull(rsResultCrnwrkmtl, "MAX_MTL_L"));
			
			recUpdCrnSchData.setField("YD_UP_WO_LOC_XAXIS",  			sUP_YD_STK_LYR_XAXIS ) ;
			if( szYD_SCH_CD.substring(2, 4).equals("TC") ) {
	        		//동간대차 상/하차스케줄이거나 스케줄인 경우에는 크레인작업이 원활하도록 주행오차를 1000mm로 설정
				recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MAX",  	sUP_YD_STK_BED_XAXIS_TOL ) ;
				recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MIN",  	sUP_YD_STK_BED_XAXIS_TOL ) ;
			}else{
				recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MAX",  	sUP_YD_STK_BED_XAXIS_TOL ) ;
				recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MIN",  	sUP_YD_STK_BED_XAXIS_TOL ) ;
			}
			recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS",  			sUP_YD_STK_LYR_YAXIS) ;
////Y좌표 계산
			if(sBRE_CHK3.equals("Y")){
				if((szYD_SCH_CD.substring(0, 1).equals("J")) && (szFR_WO_LOC_LAY.equals("002"))) {
	
					ydUtils.putLog(szSessionName, szMethodName, "권상Y좌표재계산", YdConstant.DEBUG);
	
					intRtnVal1 = searchCoilYdGdsClineY(szFR_WO_LOC.substring(0,6),szFR_WO_LOC.substring(6,8),szFR_WO_LOC_LAY,sUP_YD_STK_LYR_YAXIS);				
					if(intRtnVal1 > 0) {
						ydUtils.putLog(szSessionName, szMethodName, "권상Y좌표재계산값:"+ intRtnVal1, YdConstant.DEBUG);
						recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS",  String.valueOf(intRtnVal1)) ;	
					}
				}
			}	
			recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS1",  			"" ) ;
			recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS2",  			"" ) ;
			if( szYD_SCH_CD.substring(2, 4).equals("TC") || szYD_SCH_CD.substring(2, 4).equals("SB") ) {
        		//동간대차 상/하차스케줄이거나 스카핑인출스케줄인 경우에는 크레인작업이 원활하도록 횡행오차를 2000mm로 설정
				recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MAX",  	sUP_YD_STK_BED_YAXIS_TOL ) ;
				recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MIN",  	sUP_YD_STK_BED_YAXIS_TOL ) ;
			}else{
				recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MAX",  	sUP_YD_STK_BED_YAXIS_TOL ) ;
				recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MIN",  	sUP_YD_STK_BED_YAXIS_TOL ) ;
			}
			recUpdCrnSchData.setField("YD_UP_WO_LOC_ZAXIS",  			szYD_UP_WO_LOC_ZAXIS ) ;
			recUpdCrnSchData.setField("YD_UP_WO_ZAXIS_GAP_MAX",  		String.valueOf(YdConstant.C_COIL_CRANE_GAP_Z) ) ;
			recUpdCrnSchData.setField("YD_UP_WO_ZAXIS_GAP_MIN",  		String.valueOf(YdConstant.C_COIL_CRANE_GAP_Z) ) ;
			recUpdCrnSchData.setField("UP_ROTATION_ANGLE",  			sUP_YD_ROTATION_ANGLE ) ;
//권하정보   					
			
			recUpdCrnSchData.setField("YD_DN_WO_LOC_XAXIS",  			sDN_YD_STK_LYR_XAXIS ) ;
			if( szYD_SCH_CD.substring(2, 4).equals("TC") ) {
        		//동간대차 상/하차스케줄이거나 스케줄인 경우에는 크레인작업이 원활하도록 주행오차를 1000mm로 설정
				recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MAX",  	sDN_YD_STK_BED_XAXIS_TOL ) ;
				recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MIN",  	sDN_YD_STK_BED_XAXIS_TOL ) ;
			}else{
				
				if(( szYD_SCH_CD.substring(2, 6).equals("KE01") )|| (szYD_SCH_CD.substring(2, 6).equals("FE01") )||( szYD_SCH_CD.substring(2, 6).equals("DE01") )) {
	        		//02번지가 기준점임
//					recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MAX",  	ydDaoUtils.stringPlusInt(sDN_YD_STK_BED_XAXIS_TOL, + 2500)) ;
//					recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MIN",  	ydDaoUtils.stringPlusInt(sDN_YD_STK_BED_XAXIS_TOL, - 2500)) ;
					recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MAX",  	sDN_YD_STK_BED_XAXIS_TOL) ;
					recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MIN",  	sDN_YD_STK_BED_XAXIS_TOL) ;
				} else {	
				
					recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MAX",  	sDN_YD_STK_BED_XAXIS_TOL ) ;
					recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MIN",  	sDN_YD_STK_BED_XAXIS_TOL ) ;
				}
			}
//Y좌표 계산			
			recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS",  			sDN_YD_STK_LYR_YAXIS ) ;

			if(sBRE_CHK3.equals("Y")){			
				if((szYD_SCH_CD.substring(0, 1).equals("J")) && (szTO_WO_LOC_LAY.equals("002"))) {
					ydUtils.putLog(szSessionName, szMethodName, "권하Y좌표재계산", YdConstant.DEBUG);
	
					intRtnVal1 = searchCoilYdGdsClineY(szTO_WO_LOC.substring(0,6),szTO_WO_LOC.substring(6,8),szTO_WO_LOC_LAY,sDN_YD_STK_LYR_YAXIS);				
					if(intRtnVal1 > 0) {
						ydUtils.putLog(szSessionName, szMethodName, "권하Y좌표재계산값:"+ intRtnVal1, YdConstant.DEBUG);
						recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS",  String.valueOf(intRtnVal1)) ;	
					}
				}
			}	
			recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS1",  			"" ) ;
			recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS2",  			"" ) ;
			if( szYD_SCH_CD.substring(2, 4).equals("TC") ) {
        		//동간대차 상/하차스케줄이거나 스케줄인 경우에는 크레인작업이 원활하도록 횡행오차를 2000mm로 설정
				recUpdCrnSchData.setField("YD_DN_WO_YAXIS_GAP_MAX",  	sDN_YD_STK_BED_YAXIS_TOL ) ;
				recUpdCrnSchData.setField("YD_DN_WO_YAXIS_GAP_MIN",  	sDN_YD_STK_BED_YAXIS_TOL ) ;
			}else{
				recUpdCrnSchData.setField("YD_DN_WO_YAXIS_GAP_MAX",  	sDN_YD_STK_BED_YAXIS_TOL ) ;
				recUpdCrnSchData.setField("YD_DN_WO_YAXIS_GAP_MIN",  	sDN_YD_STK_BED_YAXIS_TOL ) ;
			}
			recUpdCrnSchData.setField("YD_DN_WO_LOC_ZAXIS",  			szYD_DN_WO_LOC_ZAXIS ) ;
			recUpdCrnSchData.setField("YD_DN_WO_ZAXIS_GAP_MAX",  		String.valueOf(YdConstant.C_COIL_CRANE_GAP_Z) ) ;
			recUpdCrnSchData.setField("YD_DN_WO_ZAXIS_GAP_MIN",  		String.valueOf(YdConstant.C_COIL_CRANE_GAP_Z) ) ;
			recUpdCrnSchData.setField("DOWN_ROTATION_ANGLE",  		sDN_YD_ROTATION_ANGLE ) ;

			 /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnsch*/ 
			intRtnVal = this.Y5UpdYdCrnsch(recUpdCrnSchData, 0);
			
			if(intRtnVal == -1) {
				szMsg="<Y5UpdGradStkLyr> 크레인 스케줄 권하지시위치 등록 실패!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
			
			ydUtils.displayRecord(szOperationName, recUpdCrnSchData);
   					
        }catch(Exception e){
			szMsg="<Y5UpdGradStkLyr> Exception Error :"+ e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;

        }//end of try~catch
		outRecord.setField("RTN_CD" , "1");	
		return outRecord;

    }//end of Y1UpdGradStkLyr()

    
   
    
    /**
	 * [A] 오퍼레이션명 : 작업보류/해제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord WrkBookInsertProcTX(JDTORecord rcvMsg) throws DAOException {

		String szMethodName         = "WrkBookInsertProcTX";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		String sRTN_CD              = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		JDTORecord outRecord     	= JDTORecordFactory.getInstance().create(); // 
		try {
			
				ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
				outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
				szMsg		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
				sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
				if ("0".equals(sRTN_CD)) {
					szMsg = "작업예약 등록시 ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
				
				}	
			
				outRecord.setField("RTN_MSG"	 , "작업예약 등록 완료");	
				return outRecord;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szMethodName + e.getMessage(), e);
		}
	} // end of WrkBookInsertProcTX
    
	/**
	 * 오퍼레이션명 : C열연 작업예약 생성 모든 소스는 여기로 호출하도록
	 * 송정현  (YD_SCH_CD:스케줄코드,
	 *         STL_SH: 재료매수,
	 *         YD_TO_LOC_DCSN_MTD(TO위치 결정 방법)
	 *         FR_YD_STK_BED_NO(적치배드)
	 *         TO_YD_STK_BED_NO
	 *         TO_YD_STK_BED_NO
	 *         STL_NO(재료번호1,2,3,....)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * 
	 */
	public JDTORecord WrkBookInsertProc(JDTORecord msgRecord)throws JDTOException  {
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();   	//스케줄기준 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao(); 	//작업예약 재료 DAO
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();    		//공용 DAO METHOD
		YdUtils ydutils                 = new YdUtils();			//공용 METHOD
		YdSchRuleDao ydSchRuleDao 		= new YdSchRuleDao();
//		설비 DAO
		YdEqpDao ydEqpDao     = new YdEqpDao();
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal          		= false;
		//리턴값(int)
		int intRtnVal              		= 0;
		//메세지
		String szMsg               		= "";
		//METHOD명
		String szMethodName        		= "WrkBookInsertProc ";
		//사용자
		String szUser              		= "SYSTEM";
		
		//레코드 선언
		JDTORecord recPara         		= null;
		JDTORecord recStkPara      		= null;
		JDTORecord inRecord        		= null; // 
		JDTORecord outRecord       		= JDTORecordFactory.getInstance().create(); // 
		JDTORecord recInTemp       		= JDTORecordFactory.getInstance().create(); // 

		JDTORecordSet rsResult1 		= JDTORecordFactory.getInstance().createRecordSet("");
		//레코드셋 선언
		JDTORecordSet rsResult     		= null;
		
		
		String szYD_STK_COL_GP     		= null;	//설비ID(열구분)
		String szFR_YD_STK_BED_NO     	= null;	//적치BED번호
		String szTO_YD_STK_BED_NO     	= null;	//적치BED번호
		int intMtlCnt              		= 0;  	//재료매수(int)
//		String szSTL_NO            		= null;	//재료번호
//		String szYD_UP_COLL_SEQ    		= null;	//권상모음순서
		String szYD_SCH_CD         		= null;	//스케줄코드
		String szYD_SCH_PRIOR	   		= null;	//스케줄우선순위
		String szYD_SCH_PROH_EXN   		= null;	//스케줄 금지 유무
		String szYD_WRK_CRN        		= null;	//작업크레인
		String szYD_WRK_CRN_PRIOR  		= null;	//작업크레인우선순위
		String szYD_ALT_CRN_YN     		= null;	//대체크레인유무
		String szYD_ALT_CRN        		= null;	//대체크레인
		String szYD_ALT_CRN_PRIOR  		= null;	//대체크레인우선순위
		String szWrkCrn               	= null;	//선택크레인
		String szYD_WBOOK_ID       		= null;	//작업예약ID
		String szYD_TO_LOC_DCSN_MTD		= null;	//위치결정방법
		String szYD_AIM_GP 		= null; 
		String szYD_AIM_BAY_GP 	= null; 			
		String sYD_WRK_PLAN_TCAR        = null;
		String szCARD_NO				= null;
		String szTRN_EQP_CD				= null;
		String szYD_CAR_USE_GP          = null;
		String szDIST_SHIPASSIGN_GP     = null;
		String szCAR_NO				= null;
		
		String szISPTOR			= null;
		String szTAKE_OUT_DT	= null;
		String szTAKE_OUT_CD	= null;
		
		
		//재료번호
		String [] szSTL_NO         		= new String[100];
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  		= new String[100];
		
		try {
		
			//받은 전문 편집
			//스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				szMsg = "스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);
				m_ctx.setRollbackOnly();
				return outRecord;

			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "STL_SH");
			if(intMtlCnt == 0){
				szMsg = "재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				m_ctx.setRollbackOnly();
				return outRecord;
				
			}
			
			szUser = ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");
			
			//적치Bed번호
			szFR_YD_STK_BED_NO 		= ydDaoUtils.paraRecChkNull(msgRecord, "FR_YD_STK_BED_NO");
			szTO_YD_STK_BED_NO 		= ydDaoUtils.paraRecChkNull(msgRecord, "TO_YD_STK_BED_NO");
			szYD_TO_LOC_DCSN_MTD	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_LOC_DCSN_MTD");
			szYD_AIM_GP 			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_YD_GP");
			szYD_AIM_BAY_GP 	    = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP");			
			sYD_WRK_PLAN_TCAR 	    = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PLAN_TCAR");		
			szCARD_NO				= ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");		
			szYD_CAR_USE_GP			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
			szDIST_SHIPASSIGN_GP	= ydDaoUtils.paraRecChkNull(msgRecord, "DIST_SHIPASSIGN_GP");	
			szCAR_NO				= ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
			szTRN_EQP_CD			= ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
			szISPTOR				= ydDaoUtils.paraRecChkNull(msgRecord, "ISPTOR");	
			szTAKE_OUT_DT			= ydDaoUtils.paraRecChkNull(msgRecord, "TAKE_OUT_DT");	
			szTAKE_OUT_CD			= ydDaoUtils.paraRecChkNull(msgRecord, "TAKE_OUT_CD");	
		
 
			ydUtils.putLog(szSessionName, szMethodName, "szYD_AIM_BAY_GP-->" + szYD_AIM_BAY_GP, YdConstant.DEBUG);
			
			
			if(szYD_AIM_BAY_GP.equals("")){
				if(!szTO_YD_STK_BED_NO.equals("")){
					if(szTO_YD_STK_BED_NO.length() ==  6){
						szYD_AIM_BAY_GP = szTO_YD_STK_BED_NO.substring(1, 2);
					} else if(szTO_YD_STK_BED_NO.length() ==  8){
						szYD_AIM_BAY_GP = szTO_YD_STK_BED_NO.substring(0, 1);
					}
				}
			}
			
			ydUtils.putLog(szSessionName, szMethodName, "szYD_AIM_BAY_GP-->" + szYD_AIM_BAY_GP, YdConstant.DEBUG);
			
			
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				szSTL_NO[Loop_i] 			= ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO" + Loop_i);
				szYD_UP_COLL_SEQ[Loop_i] 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_COLL_SEQ" + Loop_i);
//다른 작업예약에 재료가 등록되어있는지 체크한다.

				//재료번호
				inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("STL_NO", szSTL_NO[Loop_i] );
				ydUtils.putLog(szSessionName, szMethodName, "szSTL_NO[Loop_i]-->" + szSTL_NO[Loop_i], YdConstant.DEBUG);
				//재료번호로 작업예약재료 테이블을 읽어온다.
				intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(inRecord, rsResult1, 2);
				
				//리턴값 메세지처리
				if(intRtnVal == 0) {
					szMsg = "재료번호(" + szSTL_NO[Loop_i]  + ")에 대한 작업예약재료 데이터가 등록 가능합니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				} else {
					szMsg = "재료번호(" + szSTL_NO[Loop_i]  + ")로 작업예약재료 등록되어 있습니다.!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					m_ctx.setRollbackOnly();
					return outRecord;
				}
			}
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			inRecord = JDTORecordFactory.getInstance().create();
			
			//스케줄코드
			inRecord.setField("YD_SCH_CD", szYD_SCH_CD);

			//스케줄코드로 스케줄기준 Table 조회
			intRtnVal = ydSchRuleDao.getYdSchrule(inRecord, rsResult, 0);

			//리턴값 메세지처리
			if(intRtnVal != 1) {
				szMsg = "스케줄코드(" + szYD_SCH_CD + ")에 대한 스케줄기준 데이터가 이상합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				m_ctx.setRollbackOnly();
				return outRecord;
			}	
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			//스케줄CD 체크
			
			szYD_SCH_PROH_EXN  = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN"); //스케줄 금지 유무
			szYD_WRK_CRN       = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");   	//작업크레인
			szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");// 작업크레인우선순위
			szYD_ALT_CRN_YN    = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");  	//대체크레인유무
			szYD_ALT_CRN       = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN"); 		//대체크레인
			szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");// 대체크레인우선순위
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if(szYD_SCH_PROH_EXN.equals("Y")){
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);
				m_ctx.setRollbackOnly();
				return outRecord;
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
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);
					m_ctx.setRollbackOnly();
					return outRecord;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					m_ctx.setRollbackOnly();
					return outRecord;
					
					
				} else {
					
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szWrkCrn 		= szYD_ALT_CRN;
					szYD_SCH_PRIOR 	= szYD_ALT_CRN_PRIOR;
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szWrkCrn 		= szYD_WRK_CRN;
				szYD_SCH_PRIOR 	= szYD_WRK_CRN_PRIOR;
			}


			
//작업예약ID 생성
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			//레코드 생성
			inRecord  = JDTORecordFactory.getInstance().create();
			
			//================================================
			inRecord.setField("YD_WBOOK_ID", "1");
			intRtnVal = ydWrkbookDao.getYdWrkbook(inRecord, rsResult, 1);
			//리턴값 메세지처리
			if(intRtnVal != 1) {
				szMsg = "작업예약ID를 구하지 못했습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);
				m_ctx.setRollbackOnly();
				return outRecord;
			}
			
//작업예약ID 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			//작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			//저장품 조회 (목표동 및 목표야드 조회)
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", szSTL_NO[1]);                      //일단첫코일 기준으로
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 0);
			if(intRtnVal <= 0) {
				szMsg="getYdStock : data not found";
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				m_ctx.setRollbackOnly();
				return outRecord;
			}

			rsResult.absolute(1);
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setRecord(rsResult.getRecord());
			
			if (szYD_AIM_GP.equals("")) {
				szYD_AIM_GP 		= ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
			}
			if (szYD_AIM_BAY_GP.equals("")) {
				szYD_AIM_BAY_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");		
			}
			
			//INSERT 항목 record 생성
			inRecord = JDTORecordFactory.getInstance().create();
			String szYD_GP       = szYD_SCH_CD.substring(0, 1);  //야드구분
			String szYD_BAY_GP   = szYD_SCH_CD.substring(1, 2);  //동구분
			
			//INSERT할 항목 SET
			inRecord.setField("YD_WBOOK_ID"			,szYD_WBOOK_ID);
			inRecord.setField("YD_GP"				,szYD_GP);
			inRecord.setField("YD_BAY_GP"			,szYD_BAY_GP);
			inRecord.setField("YD_SCH_CD"			,szYD_SCH_CD);
			inRecord.setField("YD_SCH_PRIOR"		,szYD_SCH_PRIOR);
			inRecord.setField("YD_AIM_YD_GP"		,szYD_AIM_GP);
			inRecord.setField("YD_AIM_BAY_GP"		,szYD_AIM_BAY_GP);
			inRecord.setField("YD_TO_LOC_DCSN_MTD"	,szYD_TO_LOC_DCSN_MTD);
			inRecord.setField("YD_TO_LOC_GUIDE"		,szTO_YD_STK_BED_NO);	//야드To위치Guide
			inRecord.setField("YD_WRK_PLAN_TCAR"	,sYD_WRK_PLAN_TCAR);	//야드To위치Guide
			inRecord.setField("YD_CAR_USE_GP"		,szYD_CAR_USE_GP);	//야드To위치Guide
			inRecord.setField("DIST_SHIPASSIGN_GP"	,szDIST_SHIPASSIGN_GP);	
			inRecord.setField("REGISTER"			,szUser);
			inRecord.setField("MODIFIER"			,szUser);
			inRecord.setField("CARD_NO"			,szCARD_NO);
			inRecord.setField("TRN_EQP_CD"			,szTRN_EQP_CD);
			inRecord.setField("CAR_NO"			,szCAR_NO);
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(inRecord);
			if(intRtnVal < 1){
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);
				m_ctx.setRollbackOnly();
				return outRecord;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("REGISTER", 	  szUser);
			recPara.setField("MODIFIER", 	  szUser);
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
//SJH 추가
//C증설				
	 			if(
  		  	       (szYD_SCH_CD.equals("HBKD01LM"))||
 		  	       (szYD_SCH_CD.equals("HBKD02LM"))||
 		  	       (szYD_SCH_CD.equals("HBFD01LM"))||
 		  	       (szYD_SCH_CD.equals("HBFD02LM"))||
 		  	       (szYD_SCH_CD.equals("HBFD04LM"))||
 		  	       (szYD_SCH_CD.equals("HBKD04LM"))||
 		  	    
 		  	       (szYD_SCH_CD.equals("HBKD05LM"))||
 		  	       
 		  	       (szYD_SCH_CD.equals("HAKD01LM"))||
		  	       (szYD_SCH_CD.equals("HAKD02LM"))||
		  	       (szYD_SCH_CD.equals("HAKD04LM"))||
		  	       (szYD_SCH_CD.equals("HAKD05LM"))||

 		  	       //(szYD_SCH_CD.equals("JBFD01LM"))||
  		  	       (szYD_SCH_CD.equals("JBKD01LM"))||
  		  	       (szYD_SCH_CD.equals("JAKD01LM"))||
// 		  	       (szYD_SCH_CD.equals("JBTC01MM"))||
// 		  	       (szYD_SCH_CD.equals("JBTC02MM"))||
 		  	       (szYD_SCH_CD.equals("JBTC05MM"))||
 		  	       (szYD_SCH_CD.equals("JATC05MM"))||
 		  	       (szYD_SCH_CD.equals("HCKD01LM"))||
 		  	       (szYD_SCH_CD.equals("HCKD02LM"))||
 		  	       (szYD_SCH_CD.equals("HCKD04LM"))||
 		  	       (szYD_SCH_CD.equals("HCFD01LM"))||
 		  	       (szYD_SCH_CD.equals("HCFD02LM"))||
 		  	       (szYD_SCH_CD.equals("HCFD04LM"))||
 		  	       (szYD_SCH_CD.equals("HCKD05LM"))||

 		  	       (szYD_SCH_CD.equals("JCFD01LM"))||
		  	       (szYD_SCH_CD.equals("JCKD01LM"))||
		  	       (szYD_SCH_CD.equals("JCTC01MM"))||
		  	       (szYD_SCH_CD.equals("JCTC02MM"))||
		  	       (szYD_SCH_CD.equals("JCTC05MM"))||
 		  	       
	 			   (szYD_SCH_CD.equals("HEDD02LM"))||
	 			   (szYD_SCH_CD.equals("HEDD01LM"))||
 		  	       (szYD_SCH_CD.equals("JEDD01LM"))||
	 			   (szYD_SCH_CD.equals("JETC01MM"))||
	 			   (szYD_SCH_CD.equals("JETC02MM"))||
	 			   (szYD_SCH_CD.equals("HEDD05LM"))||

	 			   (szYD_SCH_CD.equals("HGFD01LM"))||
	 			   (szYD_SCH_CD.equals("HGFD02LM"))||
	 			   (szYD_SCH_CD.equals("HGFD04LM"))||
 		  	       (szYD_SCH_CD.equals("JGFD01LM"))||
	 			   (szYD_SCH_CD.equals("JGTC01MM"))||
	 			   (szYD_SCH_CD.equals("JGTC02MM"))||
	 			   (szYD_SCH_CD.equals("HHKD05LM"))||
//	 			   151222 hun 지포장추출 스케줄코드 추가
	 			   (szYD_SCH_CD.equals("HAKD06LM"))||
	 			   (szYD_SCH_CD.equals("HBKD06LM"))||
	 			   (szYD_SCH_CD.equals("HBFD06LM"))||
	 			   (szYD_SCH_CD.equals("HCKD06LM"))||
	 			   (szYD_SCH_CD.equals("HCFD06LM"))||
	 			   (szYD_SCH_CD.equals("HDFD06LM"))||
	 			   (szYD_SCH_CD.equals("HEDD06LM"))||
	 			   (szYD_SCH_CD.equals("HFFD06LM"))||
	 			   (szYD_SCH_CD.equals("HGFD06LM"))||
	 			   (szYD_SCH_CD.equals("HHKD06LM"))||
	 			   (szYD_SCH_CD.equals("HHKD04LM"))||
	 			   (szYD_SCH_CD.equals("HEDD04LM"))||
	 			  
	 			   
	 			   (szYD_SCH_CD.equals("HHKD01LM"))|| 
	 			   (szYD_SCH_CD.equals("HHKD02LM"))||
 		  	       (szYD_SCH_CD.equals("JHKD01LM"))|| 
	 			   (szYD_SCH_CD.equals("JHTC01MM"))|| 
	 			   (szYD_SCH_CD.equals("JHTC02MM"))
 		  	       ){ // 소재LINE OFF존

					recPara.setField("STL_NO", 		   szSTL_NO[Loop_i]);		//재료번호
					recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);  //권상모음순서
					// 작업예약재료 테이블에 등록한다.
					intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
					
					if(intRtnVal < 1){
						szMsg = "작업예약재료 데이터 등록 중 에러";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" , "0");	
						outRecord.setField("RTN_MSG", szMsg);	
						m_ctx.setRollbackOnly();
						return outRecord;
					}
	 			
	 			} else {
	 				
					blnRtnVal = YdCommonUtils.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
					if(!blnRtnVal){
						//return ;
						szMsg = "재료번호에 해당하는 적치 확인 중 에러";
						ydUtils.putLog(szSessionName, szMethodName, "" + blnRtnVal, YdConstant.ERROR);
	
						outRecord.setField("RTN_CD" , "0");	
						outRecord.setField("RTN_MSG", "재료번호에 해당하는 적치 확인 중 에러");
						m_ctx.setRollbackOnly();
						return outRecord;
					}
		 			//레코드추출
					rsResult.first();
					recStkPara = rsResult.getRecord();
					
					recPara.setField("STL_NO", 		   szSTL_NO[Loop_i]);		//재료번호
					recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP")); //적치열구분
					recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO")); //적치BED번호
					recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO")); //적치단번호
					recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);  //권상모음순서
					
					recPara.setField("YD_ISPTOR", szISPTOR);  // 
					recPara.setField("YD_TAKE_OUT_DT", szTAKE_OUT_DT);  // 
					recPara.setField("YD_TAKE_OUT_CD", szTAKE_OUT_CD);  // 
	 
					
					// 작업예약재료 테이블에 등록한다.
					intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
					
					if(intRtnVal < 1){
						szMsg = "작업예약재료 데이터 등록 중 에러";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" , "0");	
						outRecord.setField("RTN_MSG", szMsg);	
						m_ctx.setRollbackOnly();
						return outRecord;

					}
					if(!sYD_WRK_PLAN_TCAR.equals("")) {
						
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("STL_NO"			, szSTL_NO[Loop_i]);
						recInTemp.setField("YD_AIM_BAY_GP"	, szYD_AIM_BAY_GP);
						intRtnVal = ydStockDao.updYdStock(recInTemp, 0);
						
						if(intRtnVal < 1){
							szMsg = "stock목적동 등록중 에러";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							outRecord.setField("RTN_CD" , "0");	
							outRecord.setField("RTN_MSG", szMsg);	
							m_ctx.setRollbackOnly();
							return outRecord;

						}
						
					}
					
	 			}
			}
			outRecord.setField("RTN_CD" 	, "1");	
			outRecord.setField("YD_WBOOK_ID", szYD_WBOOK_ID);	
			outRecord.setField("YD_SCH_CD"	, szYD_SCH_CD);	
			outRecord.setField("YD_WRK_CRN"	, szWrkCrn);	// linein및lineoff에서 사 
			outRecord.setField("RTN_MSG"	 , "작업예약 등록 완료");	
			return outRecord;
			
		}catch(DAOException e){
			szMsg = "작업예약 등록  처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw e;
		} catch(Exception e){
			szMsg = "작업예약 등록 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(e);
		}

	
	} // end of 
	
	/**
	 *      [A] 오퍼레이션명 : Y좌표 공식 적용
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public int searchCoilYdGdsClineY( String szYdStkColGp, String  szYdStkBedNo,String  szYdStkLyrNo,String sUP_YD_STK_LYR_YAXIS) throws JDTOException  {
		
		int intRtnVal         = 0;
		YdStkLyrDao     ydStkLyrDao  = new YdStkLyrDao();
		String szMsg			=	"";
		String szMethodName		=	"searchCoilYdGdsCline";
		String szOperationName	=	"코일제품야드 2단 Y좌표 Check";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		JDTORecordSet rsStkLyr1  = null;
		JDTORecord 	recPara    = null;
		JDTORecord 	recStkLyr1    = null;
		
				
		long   lngsGRP_GP_LOC  		= 0;           
		long   lngsCOIL_OUTDIA_A  	= 0;           //코일외경
		long   lngsCOIL_OUTDIA_B   	= 0;           
		long   lngsCOIL_OUTDIA_C   	= 0;           
		long   lngsCOIL_OUTDIA_D   	= 0;           
		long   lngsCOIL_OUTDIA_E   	= 0;           
		long   lngsCOIL_OUTDIA_F   	= 0;           
		long   lngsCOIL_OUTDIA_G   	= 0;           
		int    iClineY   	= 0;           
		int    iY1 = 0; 
		int    iY2 = 0; 
		String sCOIL_NO_C 		= "";
		String sCOIL_OUTDIA_C 	= "";  
		String sCOIL_NO_D 		= "";
		String sCOIL_OUTDIA_D 	= "";  
		String sCOIL_NO_E 		= "";
		String sCOIL_OUTDIA_E 	= "";  
		String sGRP_GP_LOC      = "";
		try {
			
			
   			rsStkLyr1 = JDTORecordFactory.getInstance().createRecordSet("Temp");

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
			recPara.setField("YD_STK_LYR_NO", szYdStkLyrNo);
   	    
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilClineY*/			
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr1, 605);
 	    	if (intRtnVal <= 0) {
				szMsg = "적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkBedNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return -1;
			}
 	    	
	    	rsStkLyr1.first();
 	    	recStkLyr1 = JDTORecordFactory.getInstance().create();
 	    	recStkLyr1 = rsStkLyr1.getRecord();

 	    	for(int Loop_i = 1; Loop_i <= rsStkLyr1.size(); Loop_i++) {
				rsStkLyr1.absolute(Loop_i);
				recStkLyr1 = rsStkLyr1.getRecord();
				
				String sCOIL_GP = ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_GP");
				//좌측1단
				if (sCOIL_GP.equals("C")) {
					sCOIL_NO_C 		= ydDaoUtils.paraRecChkNull(recStkLyr1, "STL_NO"); 
					sCOIL_OUTDIA_C 	= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_OUTDIA"),"0");  
					sGRP_GP_LOC		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "GRP_GP_LOC"),"0");  
					lngsCOIL_OUTDIA_C	= Long.parseLong(sCOIL_OUTDIA_C);
				//대상2단 : 목적
				} else if (sCOIL_GP.equals("D")) {
					sCOIL_NO_D 		= ydDaoUtils.paraRecChkNull(recStkLyr1, "STL_NO"); 
					sCOIL_OUTDIA_D 	= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_OUTDIA"),"0");  
					lngsCOIL_OUTDIA_D	= Long.parseLong(sCOIL_OUTDIA_D);
				//우측1단
				} else if (sCOIL_GP.equals("E")) {
					sCOIL_NO_E 		= ydDaoUtils.paraRecChkNull(recStkLyr1, "STL_NO"); 
					sCOIL_OUTDIA_E 	= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_OUTDIA"),"0");  
					lngsCOIL_OUTDIA_E	= Long.parseLong(sCOIL_OUTDIA_E);
				}
			}
			
			
			
			lngsGRP_GP_LOC		= Long.parseLong(sGRP_GP_LOC);
			lngsCOIL_OUTDIA_C	= Long.parseLong(sCOIL_OUTDIA_C);
			lngsCOIL_OUTDIA_D	= Long.parseLong(sCOIL_OUTDIA_D);
			lngsCOIL_OUTDIA_E	= Long.parseLong(sCOIL_OUTDIA_E);

			
			if(lngsCOIL_OUTDIA_C <= lngsCOIL_OUTDIA_E){

    			rsStkLyr1 = JDTORecordFactory.getInstance().createRecordSet("Temp");

    			recPara = JDTORecordFactory.getInstance().create();
    			recPara.setField("GRP_GP_LOC"	, sGRP_GP_LOC);
    			recPara.setField("COIL_OUTDIA_C", sCOIL_OUTDIA_C);
    			recPara.setField("COIL_OUTDIA_D", sCOIL_OUTDIA_D);
    			recPara.setField("COIL_OUTDIA_E", sCOIL_OUTDIA_E);
    			
    			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilClineCheck1*/			
            	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr1, 601);
     	    	if (intRtnVal <= 0) {
     	    		return -1;
     	    	}
     	    	rsStkLyr1.first();
     	    	recStkLyr1 = JDTORecordFactory.getInstance().create();
     	    	recStkLyr1 = rsStkLyr1.getRecord();
     	    	iY1 = ydDaoUtils.paraRecChkNullInt(recStkLyr1, "Y1");
     			
     	    	szMsg = "대상재료번호(" + sCOIL_NO_D + ")=>>Y1:"+iY1 ;
     	    	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
     	    	
     	    	iClineY = (int)(lngsGRP_GP_LOC / 2 - iY1);
     
     	    	szMsg = "대상재료번호1(" + sCOIL_NO_D + ")=>>iClineY:"+iClineY + "sUP_YD_STK_LYR_YAXIS:"+ sUP_YD_STK_LYR_YAXIS ;
     	    	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
     
     	    	int iY_valie = Integer.parseInt(sUP_YD_STK_LYR_YAXIS) - iClineY ;

     	    	szMsg = "대상재료번호y값(" + sCOIL_NO_D + ")=>>iY_valie:"+iClineY ;
     	    	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
     	    	
     	    	return iY_valie;
     	    	
			} else {

    			rsStkLyr1 = JDTORecordFactory.getInstance().createRecordSet("Temp");

    			recPara = JDTORecordFactory.getInstance().create();
    			recPara.setField("GRP_GP_LOC"	, sGRP_GP_LOC);
    			recPara.setField("COIL_OUTDIA_B", sCOIL_OUTDIA_C);
    			recPara.setField("COIL_OUTDIA_A", sCOIL_OUTDIA_D);
    			recPara.setField("COIL_OUTDIA_C", sCOIL_OUTDIA_E);
    			
    			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilClineCheck2*/			
            	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr1, 602);
     	    	if (intRtnVal <= 0) {
     	    		return -1;
     	    	}
     	    	rsStkLyr1.first();
     	    	recStkLyr1 = JDTORecordFactory.getInstance().create();
     	    	recStkLyr1 = rsStkLyr1.getRecord();
     	    	iY2 = ydDaoUtils.paraRecChkNullInt(recStkLyr1, "Y2");
     			
     	    	szMsg = "대상재료번호(" + sCOIL_NO_D + ")=>>Y2:"+iY2 ;
     	    	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
     	    	
     	    	iClineY = (int)(lngsGRP_GP_LOC / 2 - iY2);
     
     	    	szMsg = "대상재료번호2(" + sCOIL_NO_D + ")=>>iClineY:"+iClineY + "sUP_YD_STK_LYR_YAXIS:"+ sUP_YD_STK_LYR_YAXIS;
     	    	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

     	    	int iY_valie = Integer.parseInt(sUP_YD_STK_LYR_YAXIS) + iClineY ;

     	    	szMsg = "대상재료번호y값(" + sCOIL_NO_D + ")=>>iY_valie:"+iClineY ;
     	    	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
     	    	
     	    	return iY_valie;
			}
 			
				
 		} catch(Exception e) {
			szMsg = "기울기 편차 CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}
		
	} 	
	
	/**
	 * 스케줄log
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord insSchLog(JDTORecord inDto) {
//		logtbl    					
//		EJBConnector ejbConn = null;
//		JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
//		recLog.setField("STL_NO"			, "");
//		recLog.setField("YD_CRN_SCH_ID"		, szCrnSchId);
//		recLog.setField("YD_GP"				, szYdGp);
//		recLog.setField("YD_SCH_CD"			, szSchCd);
//		recLog.setField("YD_USER_ID"		, "log");
//		recLog.setField("MSG"				, sRTN_MSG);
		
//		ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
//		ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });

		
		int       intRtnVal    	= 0;
		String    szMsg        	= "";
		String    szMethodName 	= "updSchLog";
		String szOperationName 	= "TO위치 LOG";
		JDTORecord outRecord	= JDTORecordFactory.getInstance().create(); 
		
		JDTORecord recPara  	= JDTORecordFactory.getInstance().create();	
		CoilGdsJspDao dao = new CoilGdsJspDao();
		
		
		szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		try {
			
			recPara  = JDTORecordFactory.getInstance().create();	
			recPara.setField("V_STL_NO"			, ydDaoUtils.paraRecChkNull(inDto, "STL_NO"));
			recPara.setField("V_YD_CRN_SCH_ID"	, ydDaoUtils.paraRecChkNull(inDto, "YD_CRN_SCH_ID"));
			recPara.setField("V_YD_GP"			, ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
			recPara.setField("V_YD_SCH_CD"		, ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_CD"));
			recPara.setField("V_YD_USER_ID"		, ydDaoUtils.paraRecChkNull(inDto, "YD_USER_ID"));
			recPara.setField("V_SCH_CONTENTS"	, ydDaoUtils.paraRecChkNull(inDto, "MSG"));
							
			String sAPP310_YN = YmComm.BCoilApplyYn("APP310","J","*");   
			szMsg = "[JSP Session : "+szOperationName+"] 실좌표적용여부: "+sAPP310_YN;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			if(sAPP310_YN.equals("Y") ) {
				intRtnVal = dao.insSchLog(recPara);
			}
	        
			if (intRtnVal < 1) {
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, "SCH LOG 등록시 ERROR 발생");	
				return outRecord;
			} // end of if				

			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

	}	// end of updCoilYdRetCrnReg    
 	
} // end of class CrnWrkGpSeEJBBean
