package com.inisteel.cim.yd.ydWkReq.ReceiptWkReq;

import javax.ejb.EJBException;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSpecDao.YdCrnSpecDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydTcarFtmvMtlDao.YdTcarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdCodeMapping;
import com.inisteel.cim.yd.ydStock.RouteModReg.RtModRegSeEJBBean;
import com.inisteel.cim.yd.ydStock.StockSpecReg.StockSpecRegSeEJBBean;
import com.inisteel.cim.yd.common.dao.ydPrepSchDao.YdPrepSchDao;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;


/**
 * 입고작업요구 Session EJB
 *
 * @ejb.bean name="CoilRcptWrkDmdSeEJB" jndi-name="CoilRcptWrkDmdSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CoilRcptWrkDmdSeEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName=getClass().getName();
	
	private YdUtils ydUtils =new YdUtils();
	
	private YdTcConst ydTcConst =new YdTcConst();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	private StockSpecRegSeEJBBean stock  = new StockSpecRegSeEJBBean();
	
	private YmCommDAO commDao = new YmCommDAO();
// PIDEV
	private YdPICommDAO	ydPICommDAO = new YdPICommDAO();
	
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
	 * 오퍼레이션명 : C열연정정출측LINE-OFF요구/C열연정정추출요구/C열연정정 Take-Out요구 (H2YDL003, HRYDJ009, H2YDL005)
	 * SJH:H2YDL003   (H2YDL003             , HRYDJ009    , H2YDL005)
	 * C열연정정추출요구 :HFL2,HFL3 만 옴
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord procR3ShearOutLineOffReq(JDTORecord msgRecord)throws DAOException  {
		
		YdStkLyrDao ydStkLyrDao    		= new YdStkLyrDao();
		YdDaoUtils ydDaoUtils      		= new YdDaoUtils();
		YdStockDao ydStockDao 			= new YdStockDao();
		YdEqpDao ydEqpDao 				= new YdEqpDao();
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();//작업예약 DAO
		
		boolean blnRtnVal      	= false;
		int intRtnVal          	= 0;
		String szMsg           	= "";
		String szMethodName    	= "procR3ShearOutLineOffReq";
		String szUser          	= "";
		
		//레코드 선언
		JDTORecord recPara  	= null;
		JDTORecord recPara1  	= null;
		JDTORecord inRecord  	= null;
		JDTORecord outRecord  	= JDTORecordFactory.getInstance().create(); 
		JDTORecord outRecord1  	= JDTORecordFactory.getInstance().create(); 
		JDTORecord outRecord2  	= JDTORecordFactory.getInstance().create(); 
		JDTORecord recGetVal	= null;
		JDTORecord recGetVal1	= null;
		//레코드셋 선언
		JDTORecordSet rsResult 	= null;
		JDTORecordSet rsResult1 	= null;
		JDTORecordSet rsResult2 	= null;
		
		
		String szYD_EQP_ID      = null;
		String szSTL_NO         = null;
		String szYD_STK_BED_NO  = null;
		String szYD_SCH_CD      = null;
		String szEQP_GP			= null;
		String szTREAT_GP		= null;
		String szCURR_PROG_CD   = "";
		String szGPACK_FLAG   = "";
		String[] rVal           = new String[1];
		String sYD_WBOOK_ID		= null;
		String sRTN_CD			= null;
		String sRTN_MSG			= null;
		
		String szISPTOR			= null;
		String szTAKE_OUT_DT	= null;
		String szTAKE_OUT_CD	= null;
		
		String sRCPT_YD_EQP_ID	= null;
		String sRCPT_TCAR_AIM_BAY_GP = null;
		String sYD_WRK_CRN      = "";
		String szSCRAP_CAUSE_CD = "";
		String szTO_YD_STK_BED_NO =  "";
			
		EJBConnector ejbConn 	= null;		
		JDTORecord[] inRecordarr   	= null;
	
		JDTORecord    recInTemp  		 = null;
		YdDelegate ydDelegate = new YdDelegate();
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode == null) {
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			outRecord.setField("RTN_CD" , "0");	
//			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;
		}
		//TC CODE DISPLAY
		if(bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[C열연정정L2/열연조업] C열연정정출측Line-Off요구/C열연정정추출요구 수신 TCCODE(" + szRcvTcCode + ")";
			ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			szUser = szRcvTcCode;
			/*
			 * TC_CODE      "HRYDJ009"
			 * TREAT_GP	        처리구분	"1:보급, 2:보급취소, 3:추출, 4:Take-Out, 5:Take-In"				
			 * STL_NO	        재료번호						
			 * EQP_GP	        설비구분					
             */
//C증설			
// C열연정정출측LINE-OFF요구(L2)
			if(szRcvTcCode.equals("H2YDL003")
			 ||szRcvTcCode.equals("H2YDL013")
			 ||szRcvTcCode.equals("H2YDL023")
			 ||szRcvTcCode.equals("H2YDL033")
			 ||szRcvTcCode.equals("H2YDL043")
			 ||szRcvTcCode.equals("H2YDL053")
			 ||szRcvTcCode.equals("H2YDL063")
			 ||szRcvTcCode.equals("H2YDL073")
				  
			) {
				//설비ID
				szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID");
				if(szYD_EQP_ID.equals("")) {
					szMsg = "[전문 이상] 설비ID가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
				}		
				
				//재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO");
				if(szSTL_NO.equals("")) {
					szMsg = "[전문 이상] 재료번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);		
					return outRecord;
				}
				
//C열연정정 Take-Out요구 (L2)	
			} else if(szRcvTcCode.equals("H2YDL005")
					||szRcvTcCode.equals("H2YDL015")
					||szRcvTcCode.equals("H2YDL025")
					||szRcvTcCode.equals("H2YDL035")
					||szRcvTcCode.equals("H2YDL045")
					||szRcvTcCode.equals("H2YDL055")
					||szRcvTcCode.equals("H2YDL065")
					||szRcvTcCode.equals("H2YDL075")
					
			) {
				//============================================================
				// C열연정정 Take-Out 요구(H2YDL005)수신시 처리 (Line-Out 요구와 동일)
				//============================================================				
				
				// 설비ID
				szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
				if(szYD_EQP_ID.equals("")) {
					szMsg = "[전문 이상] 설비ID가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
				}			
				
				
				// 적치Bed번호
				szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
				if(szYD_STK_BED_NO.equals("")) {
					szMsg = "[전문 이상] 적치Bed번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
				}
				
				
				// 재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO");
				if(szSTL_NO.equals("")) {
					szMsg = "[전문 이상] 재료번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);		
					return outRecord;
				}

				//사번
				szISPTOR = ydDaoUtils.paraRecChkNull(msgRecord, "ISPTOR");
				//TakeOut시간
				szTAKE_OUT_DT = ydDaoUtils.paraRecChkNull(msgRecord, "TAKE_OUT_DT");
				//TakeOut원인명
				szTAKE_OUT_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TAKE_OUT_CD");
				
//	C열연정정추출요구(L3)HRYDJ009
			} else if(szRcvTcCode.equals("HRYDJ009")) {
				//재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO");
				if(szSTL_NO.equals("")) {
					szMsg = "[전문 이상] 재료번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
				}
				//설비구분
				szEQP_GP = ydDaoUtils.paraRecChkNull(msgRecord,"EQP_GP");
				if(szEQP_GP.equals("")) {
					szMsg = "[전문 이상] 설비구분이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
				}
				//처리구분
				szTREAT_GP = ydDaoUtils.paraRecChkNull(msgRecord,"TREAT_GP");
				if(szTREAT_GP.equals("")) {
					szMsg = "[전문 이상] 처리구분이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
				}                                        
				String szEQP_LOC = (String)YdCommonUtils.h_hRvsstEqpGpMatch.get(szEQP_GP); //HFFE0201

				if(szEQP_LOC == null || szEQP_LOC.equals("")) {
					szMsg = "[전문 이상] 해당하는 설비구분으로 맵핑된 야드설비위치정보가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
				}
				
				szYD_EQP_ID     = szEQP_LOC.substring(0,6);
				szYD_STK_BED_NO = szEQP_LOC.substring(6,8);

			}
			
			//저장품 테이블에 해당 재료번호의 데이터가 있는지 체크한다.
			blnRtnVal = this.chkStock(szSTL_NO);
			if(!blnRtnVal) {
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
			}
			

			// 코일공통에서 현재진도코드를 조회한다.
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("COIL_NO",       szSTL_NO);
			//com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCOILCOMM
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 8);
			if(intRtnVal <= 0){
				szMsg = "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
			}
			
			rsResult.first();
			recGetVal = rsResult.getRecord();
			szCURR_PROG_CD = ydDaoUtils.paraRecChkNull(recGetVal, "CURR_PROG_CD");
			szSCRAP_CAUSE_CD = ydDaoUtils.paraRecChkNull(recGetVal, "SCRAP_CAUSE_CD");
			szGPACK_FLAG = ydDaoUtils.paraRecChkNull(recGetVal, "GPACK_FLAG");
			
			// TCCODE, 재료번호, 현재진도코드를 가지고 목표행선을 가져온다.
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO"      	, szSTL_NO);
			recPara.setField("STL_PROG_CD"	, szCURR_PROG_CD);
			recPara.setField("CURR_PROG_CD"	, szCURR_PROG_CD);
			
			rVal = YdCommonUtils.getYdAimRtGp("C", recPara);		
			// 업데이트 항목 편성
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO"      	, szSTL_NO);
			recPara.setField("YD_AIM_RT_GP"	, rVal[0]);
			recPara.setField("STL_PROG_CD"	, szCURR_PROG_CD);
			if ("D".equals(rVal[1]) ||	//임가공 이송재 추가 20110321 CHITO
				"F".equals(rVal[1]) || //판정보류재
				"G".equals(rVal[1]) || //종합판정재
				"H".equals(rVal[1])	){ //입고대기재
				recPara.setField("YD_AIM_YD_GP", "J");
			} else {
				recPara.setField("YD_AIM_YD_GP", "H");						
			}
			
			if(("F4".equals(rVal[0])) || ("F5".equals(rVal[0])) ){
				recPara.setField("YD_AIM_YD_GP", "H");
			}
			if("F3".equals(rVal[0])){
				recPara.setField("YD_AIM_YD_GP", "J");
			}
//sjh 9.28 재작업재
//			if( (rVal[0].equals("B4")) && (szRcvTcCode.equals("H2YDL013")) ){
			if( "B4".equals(rVal[0]) || "B3".equals(rVal[0])|| "BC".equals(rVal[0]) 
				||"CE".equals(rVal[0]) || "CF".equals(rVal[0])|| "CG".equals(rVal[0]) 	
				||"EA".equals(rVal[0])
			){
				recPara.setField("YD_AIM_YD_GP", "H");
			}
			
			if("EA".equals(rVal[0])){
				recPara.setField("YD_AIM_RT_GP", "EA");
			}
			
//			151209 hun B,C,E,H동 지포장개발 소재야드로 세팅
			szMsg = "hun 지포장개발 소재야드로 세팅 GPACK_FLAG=" +szGPACK_FLAG +"재작업구분:"+rVal[0]+" 설비구분:"+szEQP_GP;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
			
			
				if( "HRYDJ009".equals(szRcvTcCode) && szEQP_GP.startsWith("G") && szEQP_GP!= null){
					// 지포장에서 제품야드로 추출
					recPara.setField("YD_AIM_YD_GP", "J");
					//recPara.setField("YD_AIM_RT_GP", "G0");
				}else if("Y".equals(szGPACK_FLAG)){
					
					//재작업 지시가 아닌 경우 2016.12.21
					if(!"F4".equals(rVal[0]) && !"F5".equals(rVal[0])){
						// 지포장 대상재 보급
						recPara.setField("YD_AIM_YD_GP", "H");
						recPara.setField("YD_AIM_RT_GP", "G0");
					}
				}
			
			intRtnVal = ydStockDao.updYdStock(recPara, 0);
			if(intRtnVal <= 0){
				szMsg = "저장품Table에 목표행선 Update 실패!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
			}
//C증설
//C열연정정출측Line-Off요구(L2)
			if(szRcvTcCode.equals("H2YDL003")
			 ||szRcvTcCode.equals("H2YDL013")
			 ||szRcvTcCode.equals("H2YDL023")
			 ||szRcvTcCode.equals("H2YDL033")
			 ||szRcvTcCode.equals("H2YDL043")
			 ||szRcvTcCode.equals("H2YDL053")
			 ||szRcvTcCode.equals("H2YDL063")
			 ||szRcvTcCode.equals("H2YDL073")
			 ) {
				if(rVal[0].equals("YC") && !szSCRAP_CAUSE_CD.equals("")){  //스크랩코일
					if("E".equals(szYD_EQP_ID.substring(1,2))){
						szYD_SCH_CD = "H" + szYD_EQP_ID.substring(1,2) + "DD05LM";  //SPM2만 예외처리
					}else{
						szYD_SCH_CD = "H" + szYD_EQP_ID.substring(1,2) + "KD05LM";
					}
				} else 
				if(rVal[0].equals("F4") || rVal[0].equals("EA")){      //재작업C , 2PASS 재작업
					if( !rVal[1].equals("D")){      //보급존으로 재작업 지시
						szYD_SCH_CD = "H" + szYD_EQP_ID.substring(1,4) + "02LM";
					}else{//소재 야드로 재작업 지시
						szYD_SCH_CD = "H" + szYD_EQP_ID.substring(1,4) + "01LM";
					}
				} else if(rVal[0].equals("F5")){      //재작업C
					szYD_SCH_CD = "H" + szYD_EQP_ID.substring(1,4) + "01LM";
//sjh 9.28 재작업재
				} else if(((rVal[0].equals("B4")) || rVal[0].equals("B3")|| rVal[0].equals("BC")
						  ||rVal[0].equals("CE") || rVal[0].equals("CF")|| rVal[0].equals("CG") 
				         ) ){
						szYD_SCH_CD = "H" + szYD_EQP_ID.substring(1,4) + "04LM";
				} else {
					if("Y".equals(szGPACK_FLAG)){
						szYD_SCH_CD = "H" + szYD_EQP_ID.substring(1,4) + "06LM";
						
						szMsg = "hun 지포장개발 소재야드로 세팅 szYD_SCH_CD=" +szYD_SCH_CD ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
					}else if("G".equals(szCURR_PROG_CD) || "H".equals(szCURR_PROG_CD) || rVal[0].equals("F3") || rVal[1].equals("D") ){ //임가공 이송재 추가 20110321 CHITO
						//대차작업 입고동 확인
						rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
						recPara1 = JDTORecordFactory.getInstance().create();
						recPara1.setField("RCPT_TCAR_BAY",       szYD_EQP_ID.substring(1,2));
						recPara1.setField("STL_NO",       szSTL_NO);
						intRtnVal = ydEqpDao.getYdEqp(recPara1, rsResult1, 304);
						if(intRtnVal <= 0){
							//입고대차가 없을 경우
							
							szYD_SCH_CD = "J" + szYD_EQP_ID.substring(1,3) + "D01LM";
						} else {
							rsResult1.first();
							recGetVal1 = rsResult1.getRecord();
					
							sRCPT_YD_EQP_ID		  = ydDaoUtils.paraRecChkNull(recGetVal1, "YD_EQP_ID");
							sRCPT_TCAR_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recGetVal1, "RCPT_TCAR_AIM_BAY_GP");
							
							if(sRCPT_YD_EQP_ID.equals("JXTC01")) {
								szYD_SCH_CD = "J" + szYD_EQP_ID.substring(1,2) + "TC01MM";
							} else if(sRCPT_YD_EQP_ID.equals("JXTC02")) {
								szYD_SCH_CD = "J" + szYD_EQP_ID.substring(1,2) + "TC02MM";
							}else if(sRCPT_YD_EQP_ID.equals("JXTC05")) {
								szYD_SCH_CD = "J" + szYD_EQP_ID.substring(1,2) + "TC05MM";
							}	 
						}
						
					} else {
						szMsg = "입고 대상재가 아닙니다.!!";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" , "0");	
						outRecord.setField("RTN_MSG", szMsg);
						return outRecord;
					}
					
				}
//C열연정정 Take-Out요구 (L2)	
			} else if(szRcvTcCode.equals("H2YDL005")
					||szRcvTcCode.equals("H2YDL015")
					||szRcvTcCode.equals("H2YDL025")
					||szRcvTcCode.equals("H2YDL035")
					||szRcvTcCode.equals("H2YDL045")
					||szRcvTcCode.equals("H2YDL055")
					||szRcvTcCode.equals("H2YDL065")
					||szRcvTcCode.equals("H2YDL075")
					) {
				szYD_SCH_CD = "H" + szYD_EQP_ID.substring(1,4) + "03LM";
//C열연정정추출요구(L3)
			} else if(szRcvTcCode.equals("HRYDJ009")){
//				151217 hun 지포장 스케줄 추가
				if( (szEQP_GP.startsWith("G1-") && "3".equals(szTREAT_GP)) 
					|| (szEQP_GP.startsWith("G2-") && "3".equals(szTREAT_GP))
					|| (szEQP_GP.startsWith("G3-") && "3".equals(szTREAT_GP))
					|| (szEQP_GP.startsWith("G4-") && "3".equals(szTREAT_GP))
				){
					szYD_SCH_CD = "J" + szYD_EQP_ID.substring(1,2) + "GF01LM";
				}else if(rVal[0].equals("EA")){      //재작업C , 2PASS 재작업
					szYD_SCH_CD = "H" + szYD_EQP_ID.substring(1,4) + "02LM";
				} else
				if(rVal[0].equals("YC") && !szSCRAP_CAUSE_CD.equals("")){  //스크랩코일
					szYD_SCH_CD = "H" + szYD_EQP_ID.substring(1,2) + "KD05LM";
				} else 
				if(rVal[0].equals("F5")){
					szYD_SCH_CD = "H" + szYD_EQP_ID.substring(1,4) + "01LM";
//추가				
				} else if(rVal[0].equals("B4") || rVal[0].equals("B3")|| rVal[0].equals("BC") 
						||rVal[0].equals("CE") || rVal[0].equals("CF")|| rVal[0].equals("CG") 
				        ){
					szYD_SCH_CD = "H" + szYD_EQP_ID.substring(1,4) + "04LM";
				} else {	
					if("G".equals(szCURR_PROG_CD) || "H".equals(szCURR_PROG_CD)|| rVal[0].equals("F3")|| rVal[1].equals("D") ){ //임가공 이송재 추가 20110321 CHITO
						
						//대차작업 입고동 확인
						rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
						recPara1 = JDTORecordFactory.getInstance().create();
						recPara1.setField("RCPT_TCAR_BAY",       szYD_EQP_ID.substring(1,2));
						recPara1.setField("STL_NO",       szSTL_NO);
						intRtnVal = ydEqpDao.getYdEqp(recPara1, rsResult1, 304);
						if(intRtnVal <= 0){
							//입고대차가 없을 경우
							
							if("Y".equals(szGPACK_FLAG)){
								
								szYD_SCH_CD = "H" + szYD_EQP_ID.substring(1,4) + "06LM";  //지포장 추출
							}else{
								szYD_SCH_CD = "J" + szYD_EQP_ID.substring(1,3) + "D01LM";  //입고
							}
						} else {
							rsResult1.first();
							recGetVal1 = rsResult1.getRecord();
					
							sRCPT_YD_EQP_ID		  = ydDaoUtils.paraRecChkNull(recGetVal1, "YD_EQP_ID");
							sRCPT_TCAR_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recGetVal1, "RCPT_TCAR_AIM_BAY_GP");
							
							if(sRCPT_YD_EQP_ID.equals("JXTC01")) {
								szYD_SCH_CD = "J" + szYD_EQP_ID.substring(1,2) + "TC01MM";
							} else if(sRCPT_YD_EQP_ID.equals("JXTC02")) {
								szYD_SCH_CD = "J" + szYD_EQP_ID.substring(1,2) + "TC02MM";
							}else if(sRCPT_YD_EQP_ID.equals("JXTC05")) {
								szYD_SCH_CD = "J" + szYD_EQP_ID.substring(1,2) + "TC05MM";
							}	 
						}
					} else {
						szMsg = "입고 대상재가 아닙니다.!!";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" , "0");	
						outRecord.setField("RTN_MSG", szMsg);
						return outRecord;
					}
				}
			}
			
			if((szRcvTcCode.equals("HRYDJ009"))
			 ||(szRcvTcCode.equals("H2YDL005"))
			 ||(szRcvTcCode.equals("H2YDL015"))
			 ||(szRcvTcCode.equals("H2YDL025"))
			 ||(szRcvTcCode.equals("H2YDL035"))
			 ||(szRcvTcCode.equals("H2YDL045"))
			 ||(szRcvTcCode.equals("H2YDL055"))
			 ||(szRcvTcCode.equals("H2YDL065"))
			 ||(szRcvTcCode.equals("H2YDL075"))
			 ) {
				//C열연정정 Take-Out요구,C열연정정추출요구
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				//적치단정보 조회 
				blnRtnVal = this.chkGetStkLyr(szYD_EQP_ID, szYD_STK_BED_NO, "001", rsResult);
				if(!blnRtnVal) {
					//return;
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", "적치단정보 조회 에러");	
					m_ctx.setRollbackOnly();
					return outRecord;
				}
				
				//적치단정보 레코드 추출
				rsResult.first();
				recPara = rsResult.getRecord();
				
				//적치단 재료상태
				String szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT");
				String sSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				//적치단 재료상태가 적치가능('E')가 아니면 에러 리턴
				if(!((szYD_STK_LYR_MTL_STAT.equals("E"))||(szYD_STK_LYR_MTL_STAT.equals("C")))) {
					
					if(!szSTL_NO.equals(sSTL_NO)){
						szMsg = "적치단 재료상태(" + szYD_STK_LYR_MTL_STAT + ") 적치가능 상태가 아닙니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" , "0");	
						outRecord.setField("RTN_MSG", szMsg);	
						m_ctx.setRollbackOnly();
						return outRecord;
					}
				}
				
				JDTORecordSet   outRecSet   = JDTORecordFactory.getInstance().createRecordSet("YD");
				inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("STL_NO",   szSTL_NO);
	//getYdStklyr24

//PIDEV_S :병행가동용:PI_YD
				inRecord.setField("PI_YD",    	"J");				
				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdStkPosInfo_PIDEV*/
				intRtnVal = ydStkLyrDao.getYdStklyr(inRecord, outRecSet, 24);
				if (intRtnVal > 0) {
					//적치되어 있는 정보 삭제처리
					outRecSet.first();
					outRecord1 = outRecSet.getRecord();
					recPara = JDTORecordFactory.getInstance().create();
					
					//적치단 재료상태가 적치 가능이면 재료 등록
					//적치단 테이블 업데이트
					//적치열구분 = 설비ID
					recPara.setField("YD_STK_COL_GP", 	    ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_COL_GP"));
					recPara.setField("YD_STK_BED_NO", 	    ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_BED_NO"));
					recPara.setField("YD_STK_LYR_NO", 	    ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_LYR_NO"));
					recPara.setField("MODIFIER", 		    szUser);
					recPara.setField("YD_STK_LYR_MTL_STAT", "E");
					recPara.setField("STL_NO", 			    "");
					
					//업데이트 실행
					//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo
					blnRtnVal = this.setStkLyr(recPara, 303);
					if(!blnRtnVal) {
						//return;
						outRecord.setField("RTN_CD" , "0");	
						outRecord.setField("RTN_MSG", "적치단 업데이트 실행 에러");	
						m_ctx.setRollbackOnly();
						return outRecord;
					}
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
				recPara.setField("DEL_YN", 			    "N");
				
				//업데이트 실행
	//			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo*/
				blnRtnVal = this.setStkLyr(recPara, 303);
				if(!blnRtnVal) {
					//return;
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", "적치단 업데이트 실행 에러");	
					m_ctx.setRollbackOnly();
					return outRecord;
				}
				
				
				if((szRcvTcCode.equals("H2YDL005"))
					 ||(szRcvTcCode.equals("H2YDL015"))
					 ||(szRcvTcCode.equals("H2YDL025"))
					 ||(szRcvTcCode.equals("H2YDL035"))
					 ||(szRcvTcCode.equals("H2YDL045"))
					 ||(szRcvTcCode.equals("H2YDL055"))
					 ||(szRcvTcCode.equals("H2YDL065"))
					 ||(szRcvTcCode.equals("H2YDL075"))
					 ) {
				
						/**********************************************************
		     	        * 정정작업메세지이력등록 시작
		     	        **********************************************************/
		     	        JDTORecord rcvMsgArgs = JDTORecordFactory.getInstance().create();
		     	        rcvMsgArgs.setField("COIL_NO"         , szSTL_NO);
		     	        rcvMsgArgs.setField("SHEAR_WRK_MSG_GP", "T");
		     	        rcvMsgArgs.setField("MSG_CONTENTS"    , szTAKE_OUT_CD);
		     	        rcvMsgArgs.setField("userid"          , szISPTOR);
		//    	        EJBConnector ejbConn2 = new EJBConnector("hsteelApp", "HrCommHdSeEJB", this);
		    	        EJBConnector ejbConn2 = new EJBConnector("hsteelApp", "HrCommMgtFaEJB", this);
		     	        ejbConn2.trx("insHrShrMsgLog", new Class[] { JDTORecord.class }, new Object[] { rcvMsgArgs });
				}
			}	
			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if(!blnRtnVal) {
				//return;
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", "스케줄 기준 체크 에러");	
				m_ctx.setRollbackOnly();
				return outRecord;
			}
			
			
			//결로재 대상여부 판단.
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setField("BAY_GP"	, szYD_EQP_ID.substring(1,2));
			jrParam.setField("STL_NO"	, szSTL_NO);

// PIDEV
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szMethodName, "APPPI0", "J", "*");
			
			JDTORecordSet loadYdStkcol = null;
//			if ("Y".equals(sApplyYnPI)) {
				loadYdStkcol = commDao.select(jrParam, "com.inisteel.cim.yd.ydWkReq.ReceiptWkReq.selectHotcoilAuto_PIDEV", szRcvTcCode, szMethodName, "결로재대상검핵");				
//			} else {
//				loadYdStkcol = commDao.select(jrParam, "com.inisteel.cim.yd.ydWkReq.ReceiptWkReq.selectHotcoilAuto", szRcvTcCode, szMethodName, "결로재대상검핵");
//			}
			
			if(loadYdStkcol.size() > 0 ){
				szTO_YD_STK_BED_NO   	= StringHelper.evl(loadYdStkcol.getRecord(0).getFieldString("TO_YD_STK_BED_NO"), "");
			}

			//재료번호
			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("YD_SCH_CD",          	szYD_SCH_CD);//스케줄코드
			inRecord.setField("STL_SH",      			"1");  //LINE_IN 재료매수
			inRecord.setField("STL_NO1", 	  			szSTL_NO);
			
			
			//결로재 대상 해당위치 지정
			if("".equals(szTO_YD_STK_BED_NO)){
				inRecord.setField("YD_TO_LOC_DCSN_MTD",		"S");
			}else{
				inRecord.setField("YD_TO_LOC_DCSN_MTD",		"F");
				inRecord.setField("TO_YD_STK_BED_NO", 		szTO_YD_STK_BED_NO); 
			}
			
			inRecord.setField("YD_AIM_YD_GP", 	  		szYD_SCH_CD.substring(0,1));
			inRecord.setField("YD_AIM_BAY_GP", 	  		sRCPT_TCAR_AIM_BAY_GP);
			inRecord.setField("YD_WRK_PLAN_TCAR", 	  	sRCPT_YD_EQP_ID);
			inRecord.setField("ISPTOR", 	  			szISPTOR);
			inRecord.setField("TAKE_OUT_DT", 	  		szTAKE_OUT_DT);
			inRecord.setField("TAKE_OUT_CD", 	  		szTAKE_OUT_CD); 
			
			
			
			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			sRTN_CD			= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			sYD_WBOOK_ID	= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
			sRTN_MSG		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			sYD_WRK_CRN		= StringHelper.evl(outRecord.getFieldString("YD_WRK_CRN"), "");
			if ("0".equals(sRTN_CD)) {
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				m_ctx.setRollbackOnly();
				return outRecord;
			}	
			szMsg = "C열연정정출측Line-Off요구/C열연정정추출요구/C열연정정 Take-Out요구 작업예약  완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					

////		스케줄 기동처리 
			// 열연 조업 추출, 대차 작업요구는 스케쥴 기동 안함	
//			if((szRcvTcCode.equals("HRYDJ009")) && 
//				((szYD_EQP_ID.equals("HEDD01")) ||(szYD_EQP_ID.equals("HGFD01")) ||(szYD_EQP_ID.equals("HHKD01")))
//			  ) {
	
			if(szRcvTcCode.equals("HRYDJ009")) {
				//skip HFL2,3 
				
				//재료번호
				rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
				inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("YD_SCH_CD",          	szYD_SCH_CD);//스케줄코드
				inRecord.setField("YD_WBOOK_ID",          	sYD_WBOOK_ID);
				
				/*com.inisteel.cim.yd.dao.ydWrkbookDao.getWorkTbRefYN*/
				intRtnVal = ydWrkbookDao.getYdWrkbook(inRecord, rsResult2, 503);
		    	if(intRtnVal <= 0) {
//		    		inRecordarr = new JDTORecord[1];
//					
//					inRecordarr[0] = JDTORecordFactory.getInstance().create();
//					inRecordarr[0].setField("YD_SCH_CD"		, szYD_SCH_CD); 
//					inRecordarr[0].setField("YD_WBOOK_ID"	, sYD_WBOOK_ID); 
//					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
//					outRecord2 = (JDTORecord)ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
//					sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
//					sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
//					if ("0".equals(sRTN_CD)) {
//						outRecord.setField("RTN_CD" , "0");	
//						outRecord.setField("RTN_MSG", sRTN_MSG);	
//						m_ctx.setRollbackOnly();
//						return outRecord;
//					}	
//
//		    		ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 완료"+sRTN_MSG, YdConstant.INFO);		    		
		    		
		    		recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("MSG_ID"		, "YDYDJ509");
	    			recInTemp.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
	    			recInTemp.setField("YD_SCH_CD"  , szYD_SCH_CD);
	    			recInTemp.setField("YD_EQP_ID"  , sYD_WRK_CRN);
	    			ydDelegate.sendMsg(recInTemp);			    		
	    			ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리:CRN_NO:"+sYD_WRK_CRN, YdConstant.INFO);
		    	}
		    	
		    	
			} else {
			
//				inRecordarr = new JDTORecord[1];
//				
//				inRecordarr[0] = JDTORecordFactory.getInstance().create();
//				inRecordarr[0].setField("YD_SCH_CD"		, szYD_SCH_CD); 
//				inRecordarr[0].setField("YD_WBOOK_ID"	, sYD_WBOOK_ID); 
//				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
//				outRecord2 = (JDTORecord)ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
//				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
//				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
//				if ("0".equals(sRTN_CD)) {
//					outRecord.setField("RTN_CD" , "0");	
//					outRecord.setField("RTN_MSG", sRTN_MSG);	
//					m_ctx.setRollbackOnly();
//					return outRecord;
//				}	
				recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("MSG_ID"		, "YDYDJ509");
    			recInTemp.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
    			recInTemp.setField("YD_SCH_CD"  , szYD_SCH_CD);
    			recInTemp.setField("YD_EQP_ID"  , sYD_WRK_CRN);
    			ydDelegate.sendMsg(recInTemp);	
				ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리:CRN_NO:"+sYD_WRK_CRN, YdConstant.INFO);

			}
			outRecord.setField("RTN_CD" , "1");	
			outRecord.setField("RTN_MSG", sRTN_MSG);	
			return outRecord;
		
			
		} catch (Exception e) {
		
			szMsg = "C열연 정정출측 요구 처리중 ERROR : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			//return;
			throw new DAOException(e);
		
		}	// end try catch
			
	
	} //end of procR3ShearOutLineOffReq
	
	
	
	
	/**
	 * 오퍼레이션명 : C열연압연분기Line-Off작업요구 (H1YDL001)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procR2MillBrLineOffReq(JDTORecord msgRecord)throws JDTOException  {
		
		YdStkLyrDao ydStkLyrDao    		= new YdStkLyrDao();//적치단 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();//작업예약 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();  //작업예약재료 DAO
		YdDaoUtils ydDaoUtils     	 	= new YdDaoUtils();//공용 DAO METHOD
		YdDelegate ydDelegate 			= new YdDelegate();//DELEGATE
		YdUtils ydutils            		= new YdUtils();//공용 METHOD
		
		boolean blnRtnVal      = false;
		int intRtnVal          = 0;
		String szMsg           = "";
		String szMethodName    = "procR2MillBrLineOffReq";
		String szUser          = "SYSTEM";
		
		JDTORecord    recPara  = null;
		JDTORecordSet rsResult = null;
		
		
		String szYD_EQP_ID         	= null;	//설비ID(열구분과 동일)
		//재료번호
		String szSTL_NO            	= null;//재료번호
		String szYD_STK_BED_NO 		= null;//적치BED번호
		String szYD_SCH_CD          = null;//스케줄코드
		String szYD_DSTR_GP 		= null;//야드분기구분
		String szNEXT_PROC		= null; // 공정코드
		
		JDTORecord    inRecord  = null;
		JDTORecord    outRecord  =JDTORecordFactory.getInstance().create(); 
		JDTORecord    outRecord2  =JDTORecordFactory.getInstance().create(); 
		EJBConnector ejbConn = null;		
		JDTORecord[] inRecordarr   	= null;
		String sYD_WBOOK_ID			   = null;
		String sRTN_CD		= null;
		String sRTN_MSG		= null;		
		
		YdStockDao ydStockDao = new YdStockDao();
		String[] rVal = new String[1];
		
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode == null || szRcvTcCode.equals("") ) {
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			szMsg = "[전문 이상] 설비ID가 없습니다.";
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;

		}
		//TC CODE DISPLAY
		if(bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try{
			//=============================================================
			// 권오창
			// 2009.11.05
			//
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[C열연압연L2] C열연압연분기Line-Off요구 수신";
			ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID");
			ydUtils.putLog(szSessionName, szMethodName, szYD_EQP_ID, YdConstant.DEBUG);
			if(szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;

				
			}					
			//재료번호
			szSTL_NO   = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO");
			if(szSTL_NO.equals("")) {
				
				szMsg = "[전문 이상] 재료번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;

				
			}
			szYD_DSTR_GP = ydDaoUtils.paraRecChkNull(msgRecord,"YD_DSTR_GP");
			if(szYD_DSTR_GP.equals("")) {
				
				szMsg = "[전문 이상] 야드분기구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;

				
			}
			
			//파라메터 JDTORecord 생성 
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setField("COIL_NO"     , szSTL_NO);
			
			/*com.inisteel.cim.yd.ydWkReq.ReceiptWkReq.CoilRcptWrkDmdSeEJBBean.getSelectNextProc
			SELECT COIL_NO , NEXT_PROC
			  FROM TB_PT_COILCOMM A
			WHERE COIL_NO =:V_COIL_NO
			*/
			
			JDTORecordSet jsReturn = commDao.select(jrParam, "com.inisteel.cim.yd.ydWkReq.ReceiptWkReq.CoilRcptWrkDmdSeEJBBean.getSelectNextProc", szUser, szMethodName, "공정코드 조회");
 
			if (jsReturn.size() > 0) {
				szNEXT_PROC = jsReturn.getRecord(0).getFieldString("NEXT_PROC");	 
			}
			
			//=================================================================================
			// 크레인 스케쥴 생성
			//C열연코일소재야드(1)+D동(1)+코일소재(2)+컨베어(2)+입고(1)+좌우구분(1)
			//szYD_SCH_CD  = "HDCV01LM";
		//	szYD_EQP_ID ="H"+szYD_EQP_ID.substring(1, 2)+szYD_EQP_ID.substring(0, 2)+"01";
		//	szYD_EQP_ID ="H"+szYD_EQP_ID.substring(1, 2)+szYD_EQP_ID.substring(0, 2)+"01";
			
			
			if(!"".equals(szNEXT_PROC)){
				if("A".equals(szNEXT_PROC.substring(1 , 2))){
					// 공냉재 수입
					szYD_SCH_CD = szYD_EQP_ID.subSequence(0 ,5) + "2LM";  //HDCV02LM
				}else{
					// 일반 수입
					szYD_SCH_CD = szYD_EQP_ID + "LM";  //HDCV01LM
				}				
			}else {
				// 일반 수입
				szYD_SCH_CD = szYD_EQP_ID + "LM";  //HDCV01LM
			}
			//=================================================================================
			//BED번호는 01
			szYD_STK_BED_NO = "00";
			
			//저장품 테이블에 해당 재료번호의 데이터가 있는지 체크한다.
			blnRtnVal = this.chkStock(szSTL_NO);
			if(!blnRtnVal) {
				szMsg = "저장품 테이블에 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
			
			}
			
			
			//차공정에 따른 목표행선 변경 작업
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO" , szSTL_NO);

			rVal = YdCommonUtils.getYdAimRtGp("C" , recPara);

			recPara.setField("YD_AIM_RT_GP" , rVal[0]);
			recPara.setField("MODIFIER" , "H1YDL001");

			intRtnVal = ydStockDao.updYdStock(recPara , 0);
			if (intRtnVal <= 0) {
				szMsg = "저장품 업데이트 실행 에러";
				outRecord.setField("RTN_CD" , "0");
				outRecord.setField("RTN_MSG" , szMsg);
				m_ctx.setRollbackOnly();
				return outRecord;
			}
			szMsg = szSTL_NO + " :: YD_STOCK[목표행선변경 LINE OFF]UPDATE Success  ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			
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
			if(!blnRtnVal) {
				//return;
				szMsg = "적치단 업데이트 실행 에러";
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				m_ctx.setRollbackOnly();
				return outRecord;				
			}
	
			
			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if(!blnRtnVal) {
				szMsg = "스케줄 기준 체크 에러";
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				m_ctx.setRollbackOnly();
				return outRecord;				
			}
			//레코드 생성
			inRecord = JDTORecordFactory.getInstance().create();

			inRecord.setField("YD_SCH_CD",          		szYD_SCH_CD);//스케줄코드
			inRecord.setField("STL_SH",      				"1");  //LINE_IN 재료매수
			inRecord.setField("STL_NO1", 	  				szSTL_NO);
			inRecord.setField("YD_TO_LOC_DCSN_MTD",			"S");

			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			sRTN_CD			= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			sYD_WBOOK_ID	= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
			sRTN_MSG		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				m_ctx.setRollbackOnly();
				return outRecord;
			}	
			szMsg = "C열연압연분기Line-Off요구 수신 작업예약  완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
			
			inRecordarr = new JDTORecord[1];
			
			inRecordarr[0] = JDTORecordFactory.getInstance().create();
			inRecordarr[0].setField("YD_SCH_CD"		, szYD_SCH_CD); 
			inRecordarr[0].setField("YD_WBOOK_ID"	, sYD_WBOOK_ID); 
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			outRecord2 = (JDTORecord)ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
			sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				m_ctx.setRollbackOnly();
				return outRecord;
			}	
			ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 완료"+sRTN_MSG, YdConstant.INFO);
			
			
			
		}catch(DAOException e) {
			szMsg = "C열연 압연분기 Line-Off 작업요구 처리중 EJBException ERROR[1] : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			throw e;
			//return e.getMessage();
		} catch (Exception e) {
		
			szMsg = "C열연 압연분기 Line-Off 작업요구 처리중 Exception ERROR[2] : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			throw new DAOException(e.getMessage());
		}	// end try catch
		outRecord.setField("RTN_CD" , "1");	
		return outRecord;
	} //end of procR2MillBrLineOffReq
	
	
	
	/**
	 * 오퍼레이션명 : C열연수냉탱크Line-Off요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procR3WtclTnkLineOffReq(JDTORecord msgRecord)throws JDTOException  {
		
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
		String szMethodName    = "procR3WtclTnkLineOffReq";
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
		if(szRcvTcCode == null) {
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		//TC CODE DISPLAY
		if(bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try{
			
			
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID");
			if(szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}			
//			//적치Bed번호
//			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_BED_NO");
//			if(szYD_STK_BED_NO.equals("")) {
//				
//				szMsg = "[전문 이상] 적치Bed번호가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
//			//재료번호
//			szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO");
//			if(szSTL_NO.equals("")) {
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
			if(!blnRtnVal) return;
			
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
				if(!ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT").equals("C")) {
					rsResult.next();
					continue;
				}
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				//적치BED번호
				szYD_STK_BED_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				
				//저장품 테이블에 해당 재료번호의 데이터가 있는지 체크한다.
				blnRtnVal = this.chkStock(szSTL_NO[Loop_i]);
				if(!blnRtnVal) return;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if(!blnRtnVal) return;
				
				//다음 레코드로
				rsResult.next();

			}
			
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if(!blnRtnVal) return;
			
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
			if(szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal) {
				
				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")) {
					
					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal) {
					
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
			if(!blnRtnVal) return;
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
			if(intRtnVal < 1) {
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
				
				if(intRtnVal < 1) {
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
	
	} //end of procR3WtclTnkLineOffReq
	
	
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
			szMsg = "설비ID(" + szEqpId + ")입니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//설비ID
			recPara.setField("YD_EQP_ID", szEqpId);
			
			//설비 테이블 조회
			intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult, 0);

			//리턴값 메세지처리
			if(intRtnVal > 1) {
				
				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if(intRtnVal == 0) {
				
				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2) {
				
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

	
//	
//	/**
//	 * 오퍼레이션명 : 컨베어 BED SHIFT
//	 *  
//	 * @param  String        szYD_EQP_ID     설비ID
//	 *         String        szYD_STK_BED_NO 적치BED번호
//	 *         JDTORecordSet rsResult        결과레코드셋
//	 * @return boolean       true(성공), false(실패)
//	 * @throws JDTOException
//	 */
//	public boolean setShiftStkBed(String szYD_EQP_ID, String szYD_STK_BED_NO)throws JDTOException  {
//
//		String szMsg        = null;
//		String szMethodName = "setShiftStkBed";
//		boolean blnRtnVal   = false;
//		
//		JDTORecord recPara     = null;
//		JDTORecordSet rsResult = null;
//		int intYD_STK_BED_NO = 0;
//		int intYD_STK_BED_NO_OVER = 0;
//		try {
//			intYD_STK_BED_NO = Integer.parseInt(szYD_STK_BED_NO);
//			//결과레코드셋 생성
//			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//			
//			//해당 적치열의 모든 BED 조회
//			blnRtnVal = this.chkGetStkLyr(szYD_EQP_ID, rsResult);
//			if(!blnRtnVal) return blnRtnVal;
//			
//			//rsResult.first();
//			//rsResult.next();
//			//적치열의 BED수 만큼 루프를 돌아 적치 데이터를 쉬프트한다.
//			for (int Loop_i = 2; Loop_i <= rsResult.size(); Loop_i++) {
//				rsResult.absolute(Loop_i);
//				recPara = rsResult.getRecord();
//				intYD_STK_BED_NO_OVER = ydDaoUtils.paraRecChkNullInt(recPara, "YD_STK_BED_NO");
//				if( intYD_STK_BED_NO_OVER >= intYD_STK_BED_NO ) {
//					recPara.setField("YD_STK_BED_NO", YdUtils.fillSpZr("" + ( intYD_STK_BED_NO_OVER + 1), 2, 0));
//					blnRtnVal = this.setStkLyr(recPara, 0);
//					if(!blnRtnVal) return blnRtnVal;
//				}
//				//rsResult.next();
//			}
//			recPara = JDTORecordFactory.getInstance().create();
//			//해당 적치열의 01 BED 초기화
//			//적치열
//			recPara.setField("YD_STK_COL_GP",       szYD_EQP_ID);
//			//적치BED
//			//recPara.setField("YD_STK_BED_NO",       "01");
//			recPara.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);
//			//적치단
//			recPara.setField("YD_STK_LYR_NO",       "001");
//			//재료번호
//			recPara.setField("STL_NO",              "");
//			//적치단 재료상태
//			recPara.setField("YD_STK_LYR_MTL_STAT", "E");
//			
////==========================================================================================                    
////          기준이기 때문에 클리어 되면 안됨
////          2009.09.25 권오창
////            
////			//야드적치단X축
////			recPara.setField("YD_STK_LYR_XAXIS",    "0");
////			//야드적치단Y축
////			recPara.setField("YD_STK_LYR_YAXIS",    "0");
////			//야드적치단Z축
////			recPara.setField("YD_STK_LYR_ZAXIS",    "0");
////==========================================================================================                    
//			
//			blnRtnVal = this.setStkLyr(recPara, 0);
//			if(!blnRtnVal) return blnRtnVal;
//			
//		} catch(JDTOException e) {
//			szMsg = "적치단 업데이트 중 Error : " + e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			blnRtnVal = false;
//			throw new DAOException(e);
//		}
//		return blnRtnVal;
//	} //end of setShiftStkBed
//	
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
			//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyr
			intRtnVal = ydStkLyrDao.updYdStklyr(recPara, intGp);

			//리턴값 메세지처리
			if(intRtnVal >= 1) {
				
				blnRtnVal = true;
				
			} else if(intRtnVal == 0) {
				szMsg = "적치열구분("  + recPara.getFieldString("YD_STK_COL_GP") + ")," +
				        "적치BED번호(" + recPara.getFieldString("YD_STK_BED_NO") + ")," +
				        "적치단번호("  + recPara.getFieldString("YD_STK_LYR_NO") + ")" +
						" 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -1) {
				szMsg = "적치열구분("  + recPara.getFieldString("YD_STK_COL_GP") + ")," +
				        "적치BED번호(" + recPara.getFieldString("YD_STK_BED_NO") + ")," +
				        "적치단번호("  + recPara.getFieldString("YD_STK_LYR_NO") + ")" +
				        "로 적치단 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2) {
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
			if(intRtnVal >= 1) {
				
				blnRtnVal = true;
				
			} else if(intRtnVal == 0) {
				szMsg = "적치열구분("  + szStkColGp + ")" +
						" 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2) {
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
			if(intRtnVal > 1) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 1) {
				
				blnRtnVal = true;
				
			} else if(intRtnVal == 0) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2) {
				
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
	 * 오퍼레이션명 : 저장품유무체크
	 *  
	 * @param  String  szStlNo 재료번호
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkStock(String szStlNo, JDTORecordSet rsResult)throws JDTOException  {
		
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
		//JDTORecordSet rsResult    = null;		
		
		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			//레코드셋 생성
			//rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//재료번호
			recPara.setField("STL_NO", szStlNo);
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 0);
			
			//리턴값 메세지처리
			if(intRtnVal > 1) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 1) {
				
				blnRtnVal = true;
				
			} else if(intRtnVal == 0) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2) {
				
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
			if(intRtnVal > 1) {
				
				szMsg = "적치열구분("  + szStkColGp + ")," +
				        "적치BED번호(" + szStkBedNo + ")," +
				        "적치단번호("  + szStkLyrNo + ")" +
				        " 에 대한 적치단 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				blnRtnVal = false;
				
			} else if(intRtnVal == 1) {
				
				blnRtnVal = true;
				
			} else if(intRtnVal == 0) {
				szMsg = "적치열구분("  + szStkColGp + ")," +
				        "적치BED번호(" + szStkBedNo + ")," +
				        "적치단번호("  + szStkLyrNo + ")" +
						" 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2) {
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

		if(intRtnVal > 0) {
			szMsg="BED 금지/해제 처리("+szMethodName+") 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} else {
			szMsg="BED 금지/해제 처리("+szMethodName+") 실패" + " intRtnVal: " +intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
	
	}// end of bedProhRel()

	
	
	/**
	 * 오퍼레이션명 : C열연차량하차작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCHrCarUdWrkReq(JDTORecord msgRecord)throws JDTOException  {
		
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
		String szMethodName    = "procCHrCarUdWrkReq";
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
		if(szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//차량스케줄ID
			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_ID");
			if(szYD_CAR_SCH_ID.equals("")) {
				
				szMsg = "[전문 이상] 차량스케줄ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분(하차위치)
			szYD_STK_COL_GP     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if(szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 저장위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//차량스케줄 데이터 체크
			blnRtnVal = this.chkGetCarSch(szYD_CAR_SCH_ID, rsResult);
			if(!blnRtnVal) return;
			
			//차량스케줄 데이터 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			//차량 정보 정합성 체크
			blnRtnVal = this.chkCarInfo(recPara, msgRecord);
			if(!blnRtnVal) return;

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
			if(!blnRtnVal) return;
			
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
			if(szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal) {
				
				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")) {
					
					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal) {
					
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
			if(!blnRtnVal) return;
			
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
				if(!blnRtnVal) return;
				
				//다음 레코드로 이동
				rsResult.next();
			}
			
//			//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다
//			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
//				
//				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
//				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
//				if(!blnRtnVal) return;
//				
//			}			

			//작업예약 테이블 INSERT할 항목 레코드 생성
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = this.getYdWbookId(rsResult);
			if(!blnRtnVal) return;
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
			if(intRtnVal < 1) {
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

	
	} // end of procCHrCarUdWrkReq()
	
	
	
	/**
	 * 오퍼레이션명 : C열연대차하차작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCHrTcarUdWrkReq(JDTORecord msgRecord) throws JDTOException  {

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
		String szMethodName    = "procCHrTcarUdWrkReq";
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
		if(szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//차량스케줄ID
			szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_SCH_ID");
			if(szYD_TCAR_SCH_ID.equals("")) {
				
				szMsg = "[전문 이상] 대차스케줄ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//적치열구분(하차위치)
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if(szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[전문 이상] 저장위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}

			//대차스케줄 데이터 유무 체크
			blnRtnVal = this.chkTcarSch(szYD_TCAR_SCH_ID);
			if(!blnRtnVal) return;
			
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
			if(!blnRtnVal) return;
			
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
			if(szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal) {
				
				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")) {
					
					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal) {
					
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
			if(!blnRtnVal) return;
			
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
				if(!blnRtnVal) return;
				
				//다음 레코드로 이동
				rsResult.next();
			}
			
//			//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다
//			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
//				
//				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
//				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
//				if(!blnRtnVal) return;
//				
//			}			

			//작업예약 테이블 INSERT할 항목 레코드 생성
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = this.getYdWbookId(rsResult);
			if(!blnRtnVal) return;
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
			if(intRtnVal < 1) {
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
				if(!blnRtnVal) {
					
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
		
	} // end of procCHrTcarUdWrkReq
	
	
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
			if(intRtnVal > 0) {

				blnRtnVal = true;
				
			} else if(intRtnVal == 0) {
				
				szMsg = "대차스케줄ID(" + szTcarSchId + ")에 대한 대차이송재료 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2) {
				
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
			if(intRtnVal > 1) {
				
				szMsg = "대차스케줄ID(" + szTcarSchID + ")에 대한 대차스케줄 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if(intRtnVal == 0) {
				
				szMsg = "대차스케줄ID(" + szTcarSchID + ")에 대한 대차스케줄 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2) {
				
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
			if(intRtnVal > 0) {

				blnRtnVal = true;
				
			} else if(intRtnVal == 0) {
				
				szMsg = "차량스케줄ID(" + szCarSchId + ")에 대한 차량이송재료 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2) {
				
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
			if(szYD_CAR_USE_GP.equals("G")) {
				
				//차량번호 비교 후 다르면 에러 처리후 리턴
				if(!szCAR_NO.equals(ydDaoUtils.paraRecChkNull(recCarSch, "CAR_NO"))) {
					
					szMsg = "차량스케줄ID("      + ydDaoUtils.paraRecChkNull(recCarSch, "YD_CAR_SCH_ID") + ")에 해당하는" +
					        "전문 차량번호("      + szCAR_NO                                              + ")와 "        +
					        "차량스케줄 차량번호(" + ydDaoUtils.paraRecChkNull(recCarSch, "CAR_NO")        + ")가 다릅니다.!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return blnRtnVal = false;
					
				}
				// PIDEV
//				String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "", "APPPI0", "J", "*");			
				
//				if("N".equals(sApplyYnPI)) {				
//				
//					//카드번호 비교 후 다르면 에러 처리후 리턴
//					if(!szCARD_NO.equals(ydDaoUtils.paraRecChkNull(recCarSch, "CARD_NO"))) {
//						
//						szMsg = "차량스케줄ID("      + ydDaoUtils.paraRecChkNull(recCarSch, "YD_CAR_SCH_ID") + ")에 해당하는" +
//						        "전문 카드번호("      + szCARD_NO                                             + ")와 "        +
//						        "차량스케줄 카드번호(" + ydDaoUtils.paraRecChkNull(recCarSch, "CARD_NO") + ")가 다릅니다.!";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//						return blnRtnVal = false;
//						
//					}
//				
//				}
				
				//차량번호 카드번호 비교후 같으면 true 리턴
				blnRtnVal = true;
			//구내 운송("L")이면 운송장비코드 체크
			} else if(szYD_CAR_USE_GP.equals("L")) {
				//운송장비코드 비교 후 다르면 에러 처리후 리턴
				if(!szTRN_EQP_CD.equals(ydDaoUtils.paraRecChkNull(recCarSch, "TRN_EQP_CD"))) {
					
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
			if(intRtnVal > 1) {
				
				szMsg = "차량스케줄ID(" + szCarSchID + ")에 대한 차량스케줄 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 1) {
				
				blnRtnVal = true;
				
			} else if(intRtnVal == 0) {
				
				szMsg = "차량스케줄ID(" + szCarSchID + ")에 대한 차량스케줄 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2) {
				
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
			if(!blnRtnVal) return blnRtnVal;
			
			//결과 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 폭
			double lngMtlW     = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_MTL_W");
			// 길이
			long lngMtlL     = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_L");
			// 중량			
			long lngMtlWt    = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");

			//레코드셋 재생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//크레인사양 체크 및 조회
			blnRtnVal = this.chkGetCrnSpec(szEqpId, rsResult);
			if(!blnRtnVal) return blnRtnVal;
			
			//크레인사양 추출
			rsResult.first();
			recPara = rsResult.getRecord();
	
			// 크레인 작업 능력
			// 작업가능길이
			long lngAbleL  = ydDaoUtils.paraRecChkNullLong(recPara, "YD_WRK_ABLE_L");
			// 작업가능폭
			double lngAbleW  = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_WRK_ABLE_W");
			// 작업가능중량
			long lngAbleWt = ydDaoUtils.paraRecChkNullLong(recPara, "YD_WRK_ABLE_WT");
			
			//크레인 작업가능 길이와 재료의 길이 비교
			if(lngAbleL < lngMtlL) {
				szMsg = "크레인 작업가능 길이(" + lngAbleL + ") 보다 재료의 길이(" + lngMtlL + ")가 더 큽니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;
			}
	
			//크레인 작업가능 폭과 재료의 폭 비교
			if(lngAbleW < lngMtlW) {
				szMsg = "크레인 작업가능 폭(" + lngAbleW + ")보다 재료의 폭(" + lngMtlW + ")이 더 큽니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;
			}
			
			//크레인 작업가능 중량과 재료의 중량  비교
			if(lngAbleWt < lngMtlWt) {
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
			if(intRtnVal > 1) {
				
				szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if(intRtnVal == 0) {
				
				szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2) {
				
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
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 110);
			
			//리턴값 메세지처리
			if(intRtnVal > 1) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if(intRtnVal == 0) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 없거나 적치단에 재료가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2) {
				
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
			szMsg = "설비ID(" + szEqpId + ")입니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//설비 체크 및 데이터 조회
			blnRtnVal = this.chkGetEqp(szEqpId, rsResult);
			if(!blnRtnVal) return blnRtnVal;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//설비상태
			szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
			
			//크레인의 상태가 'T'이면 false 리턴.
			//상수 수정 [2009.12.03] 이현성 
			if(szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {
				
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
			if(intRtnVal > 1) {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if(intRtnVal == 0) {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2) {
				
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
			if(intRtnVal > 0) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 이미 등록되어 있습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 0) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 등록 가능합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				blnRtnVal = true;
				
			} else if(intRtnVal == -2) {
				
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
			if(intRtnVal > 1) {
				
				szMsg = "작업예약ID 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if(intRtnVal == 0) {
				
				szMsg = "작업예약ID를 구하지 못했습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2) {
				
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
	 * 오퍼레이션명 : 후판압연전단L2 재열재Take-Out 요구 (H1YDL002) [권오창 2009.08.27] 
	 * 사용안함
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procR2ReHeatTakeOutReq(JDTORecord msgRecord)throws JDTOException  {
		// 레코드 선언
		JDTORecord recPara        = null;
		JDTORecordSet rsResult    = null;

		// 객체생성
		YdDelegate ydDelegate     = new YdDelegate();
		
		// 메소드명
		String szMethodName       = "procR2ReHeatTakeOutReq";		

		// 메세지
		String szMsg              = "";
		
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szSTL_NO           = "";

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode == null) {
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}

		
		try {
			//=============================================================
			// 권오창
			// 2009.11.05
			//
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[C열연압연L2] 재열재Take-Out 요구 수신";
			ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
				
			
			
			
			
			// 레코드 및 레코드셋 생성
			recPara  = JDTORecordFactory.getInstance().create();
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			// 수신전문 항목 추출
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			szYD_STK_BED_NO	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			szSTL_NO =  ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO");
			
	        // 델리게이트 호출을 위한 레코드 편집 
			recPara.setField("JMS_TC_CD", "YDH1L002");  // TC-CODE
			recPara.setField("YD_GP"    , szSTL_NO);    // 재료번호

			// 델리게이트 호출
	        ydDelegate.sendMsg(recPara);			
		} catch(Exception e) {
			szMsg = "재열재 Take-Out 요구 처리 중 예외메세지 : " + e.getMessage();			
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		return ;
	} //end of procR2ReHeatTakeOutReq

	
	/**
	 * 오퍼레이션명 : 동간입고 여부 check
	 *  
	 * @param  String     szSchCd 스케줄CD
	 * @return boolean    true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetRecp(String szSchCd, JDTORecordSet rsResult)throws JDTOException  {
		
		//스케줄기준 DAO
		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

		String szMsg              = null;
		String szMethodName       = "chkGetRecp";
		int intRtnVal             = 0;
		boolean blnRtnVal         = false;
		JDTORecord recPara        = null;
		
		try {
			
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_SCH_CD", szSchCd); //스케줄코드
			intRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsResult, 0);

			//리턴값 메세지처리
			if(intRtnVal > 1) {
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
			} else if(intRtnVal == 1) {
				blnRtnVal = true;
			} else if(intRtnVal == 0) {
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
			} else if(intRtnVal == -2) {
				szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
			} else {
				szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
			}
		} catch(Exception e) {
			szMsg = "동간입고 체크 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal = true;
		
	} //end of chkGetRecp

	
	/**
	 * 오퍼레이션명 : 포장 line off
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord procYardLineOffReq(JDTORecord msgRecord)throws DAOException  {
		
		YdDaoUtils ydDaoUtils   = new YdDaoUtils();
		YdDelegate ydDelegate 	= new YdDelegate();
		String szMsg           	= "";
		String szMethodName    	= "procYardLineOffReq";
		
		//레코드 선언
		JDTORecord inRecord  	= null;
		JDTORecord outRecord  	= JDTORecordFactory.getInstance().create(); 
		JDTORecord recInTemp  	= JDTORecordFactory.getInstance().create(); 
		
		String szSTL_NO         = null;
		String szYD_SCH_CD      = null;
		String sRTN_CD			= null;
		String sRTN_MSG			= null;
		String sYD_WBOOK_ID		= null;
		String sYD_WRK_CRN		= null;
		
		String szYD_BAY_GP      = "";
		
		EJBConnector ejbConn 	= null;		
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "포장재 LINE OFF";

			//설비ID
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord,"YD_BAY_GP");
			if(szYD_BAY_GP.equals("")) {
				szMsg = "[전문 이상] 동이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
			}		
			
			//재료번호
			szSTL_NO 	= ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO");
			if(szSTL_NO.equals("")) {
				szMsg = "[전문 이상] 재료번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);		
				return outRecord;
			}
			if (szYD_BAY_GP.equals("B")||szYD_BAY_GP.equals("C")) {
				szYD_SCH_CD = "J"+ szYD_BAY_GP+ "YD51MM";
			} else {
				szYD_SCH_CD = "J"+ szYD_BAY_GP+ "YD01MM";
			}	
			//재료번호
			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("YD_SCH_CD",          	szYD_SCH_CD);//스케줄코드
			inRecord.setField("STL_SH",      			"1");  //LINE_IN 재료매수
			inRecord.setField("STL_NO1", 	  			szSTL_NO);
			inRecord.setField("YD_TO_LOC_DCSN_MTD",		"F");
			inRecord.setField("YD_AIM_YD_GP", 	  		"J");
			inRecord.setField("YD_AIM_BAY_GP", 	  		szYD_BAY_GP);
			inRecord.setField("YD_WRK_PLAN_TCAR", 	  	"");
			inRecord.setField("YD_USER_ID", 	  	    "procYard");
			

			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			sRTN_CD			= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			sYD_WBOOK_ID	= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
			sRTN_MSG		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			sYD_WRK_CRN		= StringHelper.evl(outRecord.getFieldString("YD_WRK_CRN"), "");
			if ("0".equals(sRTN_CD)) {
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				m_ctx.setRollbackOnly();
				return outRecord;
			}	
			szMsg = "포장재 LINE OFF요구 작업예약  완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
			
			//L2저장품재원 정보 송신
			//======================================================
			// 저장품제원 : 코일야드L2로 송신(YDY5L002)
			//======================================================
			JDTORecord recResult = null;
			recResult = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY5L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , szSTL_NO);
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			
			ydDelegate.sendMsg(recResult);

			szMsg = "코일야드L2로 응답전문 [YDY5L002] 전송완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			if(!sYD_WBOOK_ID.equals("")){
				
	    		recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MSG_ID"		, "YDYDJ509");
				recInTemp.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
				recInTemp.setField("YD_SCH_CD"  , szYD_SCH_CD);
				recInTemp.setField("YD_EQP_ID"  , sYD_WRK_CRN);
				ydDelegate.sendMsg(recInTemp);			    		
				ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리:CRN_NO:"+sYD_WRK_CRN, YdConstant.INFO);
			}		
			outRecord.setField("RTN_CD" , "1");	
			outRecord.setField("RTN_MSG", sRTN_MSG);	
			return outRecord;
		
			
		} catch (Exception e) {
		
			szMsg = "C포장재 LINE OFF 요구 처리중 ERROR : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			//return;
			throw new DAOException(e);
		
		}	// end try catch
			
	
	} //end of procYardLineOffReq
	

	
//---------------------------------------------------------------------------	
} // end of class RcptWrkDmdSeEJBBean

