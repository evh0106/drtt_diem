/*
 * @(#) 2후판정정야드 L3수신 처리 Session EJB클래스
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/12/03
 *
 * @description		2후판정정야드 L3수신 처리 Session EJB클래스
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/12/03   김현우      김현우       최초작성 
 */

package com.inisteel.cim.yd.jplateyd.session;

//UTIL IMPORT
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.rule.GetBreRule8;

import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO;
import com.inisteel.cim.yd.jplateyd.delegate.JPlateYdDelegate;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdCommonUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;

import com.inisteel.cim.yd.pSlabCommon.util.PSlabYdUtils;
import com.inisteel.cim.yd.pSlabYd.session.PSlabYdComm;
import com.inisteel.cim.yd.pSlabYd.session.PSlabYdL3RcvSeEJBBean;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

//-------------------------------------------------------------------------------------------------------------------------
//2024.12.17 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 * 2후판정정야드 L3수신 처리
 *
 * @ejb.bean name="JPlateYdL3RcvSeEJB" jndi-name="JPlateYdL3RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */

public class JPlateYdL3RcvSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;

	// Session Name
	private final String SZ_SESSION_NAME = this.getClass().getName();

	private JPlateYdDaoUtils 	ydDaoUtils = new JPlateYdDaoUtils();
	private JPlateYdUtils		ydUtils    = new JPlateYdUtils();
	private	JPlateYdDelegate 	ydDelegate = new JPlateYdDelegate();

	private PSlabYdComm         slabComm   = new PSlabYdComm();
	private PSlabYdUtils        slabUtils  = new PSlabYdUtils();

	
	private YdPICommDAO ydPICommDAO   = new YdPICommDAO();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
    private YdUtils 			ydLogUtils  = new YdUtils();
		    
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * 오퍼레이션명 : 후판조업 GAS장 절단실적수신 (PPYDJ014)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public String procPPGasCutResult(JDTORecord inRecord)throws JDTOException  {
		// DAO 및 UTIL 객체 생성
		JPlateYdStockDAO  ydStockDao 	= new JPlateYdStockDAO();
		JPlateYdStkLyrDAO ydStkLyrDao	= new JPlateYdStkLyrDAO();

		// 레코드 선언
		JDTORecordSet rsGetCutStl   	= null;
		JDTORecordSet rsGetRgntPkCnts   = null;		// 시편채취내용 그릇
		JDTORecordSet rsResult   		= null;
		JDTORecord recPara 		    	= null;
		JDTORecord recMtl 				= null;
		JDTORecord recCnc 		    	= null;
		JDTORecord recL2Para   			= null;

		// 변수선언
		String 	sMethodName 			= "procPPGasCutResult";
		String 	szOperationName 		= "후판조업 GAS장 절단실적수신";
		String 	sMsg 					= "";
		String 	sRcvTcCode				= ydUtils.getTcCode(inRecord);

		String 	sPlMplNo    			= "";		// 후판모재료번호
		String 	sStlNo					= "";		// 후판재료번호
		String 	sYdGp               	= "";
		String	sStlAppearGp			= "";		// 재료외형구분
		String 	sYdStkBedNo	        	= "";		// 야드적치Bed번호
		int 	iFromBedNo	        	= 1;		// 야드적치Bed번호 (변경전 재료의 적재위치)
		String 	sYdStkColGp	        	= "";		// 야드적치열구분
		String 	sYdCurrStrLoc	    	= "";		// 야드현저장위치
		String 	sTemp					= "";
	    String 	sYdMtlWGp          	 	= "";       // 야드재료폭구분		< 4500:S1-소폭 , >=4500:L1-광폭
	    String 	sYdMtlTGp          	 	= "";       // 야드재료두께구분
	    String 	sYdMtlLGp           	= "";       // 야드재료길이구분	< 15000:S1-단척, >=1500:L1-장척
	    String	sYdStkLyrMtlStat		= "";

		float  	fYdMtlW					= 0.0f;		// 야드재료폭
		int    	iYdMtlL					= 0;		// 야드재료길이
		String	szMODIFIER				= "";
		String	sDelYn					= "";
		String  sRgntPkCnts             = "";       // 시편채취내용

		String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
		int    	intRtnVal 				= 0;
		int    	iCutCnt					= 0;
		String	arrStlNo[]				= null;
		String	szYdStockExist			= "Y";			// 야드에 재료 미존재시 가스장 절단실적 발생시 처리할수 있도록 보완
		String	szYdSchCall				= "";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(inRecord, "F");  		// JDTORecord 에서 logid get(1 : JDTORecord.getResultCode(), Field명 - 2 : UNIQUE_ID, 3 : LOG_ID, 4 : 새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
				      		                     
		if (sRcvTcCode == null) {
			sMsg = "[ERROR] " + SZ_SESSION_NAME + "::" + sMethodName + "() TC Code Error (" + sRcvTcCode + ")";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);

		} else {

			try {
			/*
				YD_GP				야드구분
				STL_APPEAR_GP		재료외형구분			(2013.06.10 항목추가)
				PL_MTL_NO    		후판재료번호
				PL_WR_GDS_TOT_SH	후판실적제품총매수
				STL_NO1				재료번호1
				YD_MTL_T1		6.3	야드재료두께1
				YD_MTL_W1		5.1	야드재료폭1
				YD_MTL_L1		7	야드재료길이1
				YD_MTL_WT1		5	야드재료중량1
			*/
			//  재료번호1, 2, 3 --> 3, 2, 1 Bed

				sYdGp 	 		= JPlateYdConst.YD_GP_F_PLATE_YARD;
			//	sStlAppearGp	= ydDaoUtils.paraRecChkNull(inRecord, 		"STL_APPEAR_GP");
				sStlAppearGp	= ydDaoUtils.paraRecChkNull(inRecord, 		"STL_APPEAR_GP", "P");
				sPlMplNo 		= ydDaoUtils.paraRecChkNull(inRecord, 		"PL_MPL_NO");
				iCutCnt  		= ydDaoUtils.paraRecChkNullInt(inRecord, 	"PL_WR_GDS_TOT_SH");
				szMODIFIER		= ydDaoUtils.paraRecModifier(inRecord);
				szYdSchCall		= ydDaoUtils.paraRecChkNull(inRecord, 		"YD_SCH_CALL", "N");
				
				//----------------------------------------------------------------------
				// 0. 전문 체크
				//----------------------------------------------------------------------
				if ("".equals(sPlMplNo)) {
					szRtnMsg = "모재료정보 데이타 오류 : " + sPlMplNo;
					sMsg = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
					
					return szRtnMsg;
				}
				if (iCutCnt < 1 || iCutCnt > 20) {
					szRtnMsg = "절단매수 데이타 오류 : " + Integer.toString(iCutCnt);
					sMsg = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
					
					return szRtnMsg;
				}
				if ("".equals(sStlAppearGp)) {
					szRtnMsg = "재료외형구분 데이타 오류 : " + sStlAppearGp;
					sMsg = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
					
					return szRtnMsg;
				}
                
				/*
				 * 2014.03.26 윤재광
				 * 창고 사내절단장에서 절단실적 발생시 처리하고 리턴
				 */
				{
					YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
					JPlateYdStockDAO ydDao 	= new JPlateYdStockDAO();
					
					JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("yd");
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", sPlMplNo);
					
					intRtnVal = ydStklyrDao.getYdStklyr(recPara, outRecSet, 622);
					
					if (intRtnVal > 0) {
						
						recMtl	= outRecSet.getRecord(0);
						
						String sYdStrLoc = ydDaoUtils.paraRecChkNull(recMtl, "YD_STR_LOC");
						
						if("FE010101".equals(sYdStrLoc)){
							
			                // 적치단에 존재하는 모재료정보를 모두 CLEAR한다.
			                intRtnVal = ydStklyrDao.updYdStklyrWithStock(recPara);
			            	
			                for(int inx = 0; inx < iCutCnt; inx++) {
			                	sStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO" + Integer.toString(iCutCnt-inx));
			                	
			                	recPara = JDTORecordFactory.getInstance().create();
				                recPara.setField("YD_STK_COL_TO", 	"FE0101");		// TO적치열
				                recPara.setField("YD_STK_BED_TO", 	"01");			// TO적치BED 
				                recPara.setField("ARR_STL_NO", 		sStlNo);		// 재료번호
				                recPara.setField("STL_NO",			sStlNo);		// 재료번호

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 recPara에 logId 추가 
				                recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
				              	        	
				                //후판 정정야드 야드맵에 Data셋팅
				                intRtnVal = ydStklyrDao.updYdStklyrJplateStlNo(recPara); 
				                
				                // 2후판 정정야드 재료정보 생성모듈 호출
								intRtnVal = ydDao.getYdStockWithLoc(recPara, outRecSet);
			
								// 재료 정보 존재여부 체크
								if (intRtnVal < 1) {
									intRtnVal = ydDao.insYdStockBookOut(recPara);
								}
								
				            }
			               
							sMsg = "[" + szOperationName + "] 창고 사내절단장 절단실적 처리 성공";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);
							
							return JPlateYdConst.RETN_CD_SUCCESS;
						}
						
					}
				}
				
				//----------------------------------------------------------------------
				// 0. PLATE정보 체크 :: 전문 전송하기전에 재료정보를 조회하여 3M 체크함 ...
				//----------------------------------------------------------------------
				sMsg = "[" + szOperationName + "] 0. PLATE정보 조회 >>>> " + Integer.toString(iCutCnt);
				ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);

				for(int ii=0; ii<iCutCnt; ii++) {

					// sStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO" + Integer.toString(ii+1));
					// cut :: 3 , ii :: 0 --> 3 , ii :: 1 --> 2 , ii :: 2 --> 1
					//
					sStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO" + Integer.toString(iCutCnt-ii));

					rsGetCutStl = JDTORecordFactory.getInstance().createRecordSet("yd");
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", sStlNo);				// PLATE NO

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 recPara에 logId 추가 
					recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
									              	        	
					//----------------------------------------------------------------------
					// 0.1. PLATE 정보 조회
					//----------------------------------------------------------------------
					intRtnVal = ydStockDao.getGasCutResult(recPara, rsGetCutStl);
					if (intRtnVal != 1) {
						szRtnMsg = "PLATE 정보 미존재 .. 재료번호 : " + sStlNo;
						sMsg = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
						
						return szRtnMsg;
					}

					recMtl  = rsGetCutStl.getRecord(0);
					iYdMtlL = ydDaoUtils.paraRecChkNullInt(recMtl, 	"YD_MTL_L");

					// 길이가 3미터 미만 재료는 에러처리 (2013.05.07 김광률계장님 요청)
					if (iYdMtlL < 3000) {
						szRtnMsg = "PLATE 재료 길이가 3M 미만 .. 길이 : " + Integer.toString(iYdMtlL);
						sMsg = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
						
						return szRtnMsg;
					}

					//----------------------------------------------------------------------
					// 0.2. PLATE 저장위치 정보를 조회하여 현위치가 (NULL, 가스장)이 아니면 오류로 처리
					//----------------------------------------------------------------------
					rsGetCutStl = JDTORecordFactory.getInstance().createRecordSet("yd");
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", 	sStlNo);												// PLATE번호
					recPara.setField("YD_GP", 	sYdGp);													// YARD구분

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 recPara에 logId 추가 
					recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
														              	        	
					intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsGetCutStl);

					if (intRtnVal > 0) {
						recMtl	= rsGetCutStl.getRecord(0);
						sYdStkColGp 	 = ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_COL_GP");			// 야드적치열구분
						sYdStkLyrMtlStat = ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_LYR_MTL_STAT");	// 재료적치상태

						// 저장위치 체크
						if (!"".equals(sYdStkColGp) && !"CN".equals(ydUtils.substr(sYdStkColGp, 2, 2))) {
							szRtnMsg = "PLATE 위치가 가스장이 아님 : " + sYdStkColGp;
							sMsg = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
							
							return szRtnMsg;
						}

						// 적치상태 체크
						if (!"".equals(sYdStkLyrMtlStat) && !"C".equals(sYdStkLyrMtlStat)) {
							szRtnMsg = "PLATE 적치상태 오류 : " + sYdStkLyrMtlStat;
							sMsg = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
							
							return szRtnMsg;
						}
					}
				}

				//----------------------------------------------------------------------
				// 1. 날판 저장위치 조회 - 야드저장품에서 변경전 저장위치 정보를 조회
				//----------------------------------------------------------------------
				sMsg = "[" + szOperationName + "] 1. 날판 저장위치 조회 ";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);

				rsGetCutStl = JDTORecordFactory.getInstance().createRecordSet("yd");
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", sPlMplNo);				// 날판번호

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 recPara에 logId 추가 
				recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
																		              	        	
				intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsGetCutStl);
				if (intRtnVal != 1) {
					szRtnMsg = "날판정보 정보 미존재 .. 날판번호 : " + sPlMplNo;
					sMsg = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
				//	return szRtnMsg;
					szYdStockExist = "N";
				}

				if ("Y".equals(szYdStockExist)) {
					recMtl       	= rsGetCutStl.getRecord(0);
					sYdStkBedNo   	= ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_BED_NO");		// 야드적치Bed번호
					iFromBedNo 		= ydDaoUtils.paraRecChkNullInt(recMtl, "YD_STK_BED_NO");	// 야드적치Bed번호 (변경전 Bed Index)
					sYdStkColGp   	= ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_COL_GP");		// 야드적치열구분
					sYdCurrStrLoc 	= ydDaoUtils.paraRecChkNull(recMtl, "YD_CURR_STR_LOC");		// 야드현저장위치
					sRgntPkCnts		= ydDaoUtils.paraRecChkNull(recMtl, "RGNT_PK_CNTS"); 		// 시편채취내용

					// 2013.05.07 윤재광과장님 요청 .. 날판이 가스장위치가 아니면 재료만 등록하고 스케쥴 기동 안하도록 요청
					if (!"CN".equals(ydUtils.substr(sYdStkColGp, 2, 2))) {
						szYdStockExist = "N";
					}
				}

				if ("Y".equals(szYdStockExist)) {
					//----------------------------------------------------------------------
					// 2. 날판 정보 CLEAR
					//----------------------------------------------------------------------

					//----------------------------------------------------------------------
					// 2.1. 야드L2 전문송신 (저장품제원 :: YDY7L002 전송)
					//----------------------------------------------------------------------
					sMsg = "[ " + szOperationName + "] 2.1. 야드L2 저장품제원 전문송신 (날판삭제정보) START";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);

		        	recL2Para = JDTORecordFactory.getInstance().create();
		        	recL2Para.setField("JMS_TC_CD", 		"YDY7L002");                            // TC-CODE
		        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);		// 야드구분
		        	recL2Para.setField("YD_STK_COL_GP", 	sYdStkColGp);                          	// 야드적치열구분
		        	recL2Para.setField("YD_STK_BED_NO", 	sYdStkBedNo);    						// 야드적치BED번호
		        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드  [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
		        	recL2Para.setField("STL_NO", 			sPlMplNo);	        					// 재료번호
		        	recL2Para.setField("MSG_GP", 			"D");	        						// 전문구분 - D:삭제
		        	recL2Para.setField("DEL_YN_CHECK",		"N");									// 삭제된 데이타도 조회하도록 처리

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 recL2Para에 logId 추가
		        	recL2Para.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
		        																			              	        	
		        	szRtnMsg = ydDelegate.sendMsg(recL2Para);

					sMsg = "[ " + szOperationName + "] >>>> 야드L2 저장품제원 전문송신(날판삭제정보) END >>>> " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);

					//----------------------------------------------------------------------
					// 2.2. 날판 재료정보 삭제
					//----------------------------------------------------------------------
					sMsg = "[" + szOperationName  + "] 2.2. 날판 재료정보 삭제";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("MODIFIER",		szMODIFIER);
					recPara.setField("STL_NO",			sPlMplNo);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 recPara에 logId 추가
					recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
							        																			              	        	
					intRtnVal = ydStockDao.delYdStock(recPara);
					if (intRtnVal != 1) {
						szRtnMsg = "날판 재료정보 삭제시 오류발생 : " + Integer.toString(intRtnVal);
						sMsg = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
						
						return szRtnMsg;
					}

					//----------------------------------------------------------------------
					// 2.3. 날판 저장위치 CLEAR
					//----------------------------------------------------------------------
					sMsg = "[" + szOperationName  + "] 2.3. 날판 저장위치 CLEAR";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("MODIFIER",			szMODIFIER);
					recPara.setField("YD_STK_COL_GP",		sYdStkColGp);
					recPara.setField("YD_STK_BED_NO",		sYdStkBedNo);
					recPara.setField("STL_NO",				sPlMplNo);
					recPara.setField("YD_GP",				ydUtils.substr(sYdStkColGp,0,1));

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 recPara에 logId 추가
					recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
												        																			              	        	
					intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
					if (intRtnVal < 1) {
						szRtnMsg = "날판 저장위치 CLEAR 오류발생 : " + Integer.toString(intRtnVal);
						sMsg = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
						
						return szRtnMsg;
					}
				}

				arrStlNo = new String[iCutCnt];

				//----------------------------------------------------------------------
				// 3. PLATE정보 생성
				//----------------------------------------------------------------------
				sMsg = "[" + szOperationName + "] 3. PLATE정보 생성 >>>> " + Integer.toString(iCutCnt);
				ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);

				for(int ii=0; ii<iCutCnt; ii++) {

					// sStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO" + Integer.toString(ii+1));
					// cut :: 3 , ii :: 0 --> 3 , ii :: 1 --> 2 , ii :: 2 --> 1
					//
					sStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO" + Integer.toString(iCutCnt-ii));
					arrStlNo[ii] = sStlNo;

					rsGetCutStl = JDTORecordFactory.getInstance().createRecordSet("yd");
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", sStlNo);				// PLATE NO

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 recPara에 logId 추가
					recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
																	        																			              	        	
					//----------------------------------------------------------------------
					// 3.1. PLATE 정보 조회
					//----------------------------------------------------------------------
					intRtnVal = ydStockDao.getGasCutResult(recPara, rsGetCutStl);
					if (intRtnVal != 1) {
						szRtnMsg = "PLATE 정보 미존재 .. 재료번호 : " + sStlNo;
						sMsg = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
						
						return szRtnMsg;
					}

					recMtl      	= rsGetCutStl.getRecord(0);
					sTemp  			= ydDaoUtils.paraRecChkNull(recMtl,		"YD_MTL_W");
					fYdMtlW 		= ydUtils.strToFloat(sTemp, 5, 1);
					iYdMtlL  		= ydDaoUtils.paraRecChkNullInt(recMtl, 	"YD_MTL_L");
					sDelYn			= ydDaoUtils.paraRecChkNull(recMtl, 	"DEL_YN");

					sMsg = "[" + szOperationName + "] GAS절단실적 수신후 .... 조업테이블 조회 결과 >>>> " + "두께 ::"  + ydDaoUtils.paraRecChkNull(recMtl, "YD_MTL_T")
					     + ", 폭::"  + ydDaoUtils.paraRecChkNull(recMtl,	"YD_MTL_W") + ", 길이::"	+ ydDaoUtils.paraRecChkNull(recMtl,	"YD_MTL_L");
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);

					// BRE RULE 적용
					JDTORecord jdtoRcd = JDTORecordFactory.getInstance().create();
			    	boolean bRtnVal = GetBreRule8.getYDB801(JPlateYdConst.YD_GP_F_PLATE_YARD, fYdMtlW, jdtoRcd);
			    	if (bRtnVal) {
			    		sYdMtlWGp = ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_MTL_W_GP");
			    	} else {
			    		sYdMtlWGp = "";
			    	}
					jdtoRcd = JDTORecordFactory.getInstance().create();
			    	bRtnVal = GetBreRule8.getYDB802(JPlateYdConst.YD_GP_F_PLATE_YARD, iYdMtlL, jdtoRcd);
			    	if (bRtnVal) {
			    		sYdMtlLGp = ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_MTL_L_GP");
			    	} else {
			    		sYdMtlLGp = "";
			    	}

					if ("Y".equals(szYdStockExist)) {
				//		sYdStkBedNo   = ydUtils.addLeftStr(Integer.toString(ii+1), 2, '0');				// 야드적치Bed번호
						sYdStkBedNo   = ydUtils.addLeftStr(Integer.toString(ii+iFromBedNo), 2, '0');	// 야드적치Bed번호
						sYdCurrStrLoc = sYdStkColGp + sYdStkBedNo;										// 야드현저장위치
					} else {
						sYdStkColGp   = "";
						sYdStkBedNo   = "";
						sYdCurrStrLoc = "";
					}

					// 가스장에 이미 재료가 존재시 다음 적치위치 조회
					rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
					recCnc   = JDTORecordFactory.getInstance().create();
					recPara  = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_STK_COL_GP", 	sYdStkColGp);
					recPara.setField("YD_STK_BED_NO", 	sYdStkBedNo);
					recPara.setField("YD_GP", 			sYdGp);
					
					sMsg = "[" + szOperationName + "] 가스장 적치 가능 위치 BEFORE >>>> " + sYdStkColGp + sYdStkBedNo;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 recPara에 logId 추가
					recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
																						        																			              	        	
					intRtnVal = ydStkLyrDao.getEmptyToCnc(recPara, rsResult);
					if (intRtnVal <= 0) {
						szRtnMsg = "가스장 저장위치 조회 오류 : " + Integer.toString(intRtnVal);
						sMsg = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
						
						return szRtnMsg;
					}
					rsResult.first();
					recCnc = rsResult.getRecord();
					sYdStkColGp = ydDaoUtils.paraRecChkNull(recCnc, "YD_STK_COL_GP");
					sYdStkBedNo = ydDaoUtils.paraRecChkNull(recCnc, "YD_STK_BED_NO");

					sMsg = "[" + szOperationName + "] 가스장 적치 가능 위치 AFTER >>>> " + sYdStkColGp + sYdStkBedNo;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);
					
					//----------------------------------------------------------------------
					// 3.1.1  파라미터 셋팅
					//----------------------------------------------------------------------	
					recMtl.setField("YD_GP", 				sYdGp);
					recMtl.setField("STL_NO", 		 		sStlNo);
					recMtl.setField("YD_STK_COL_GP",		sYdStkColGp);							// 야드적치열구분
					recMtl.setField("YD_STK_BED_NO",		sYdStkBedNo);							// 야드적치Bed번호
					recMtl.setField("YD_CURR_STR_LOC", 		sYdCurrStrLoc);							// 야드현저장위치
					recMtl.setField("YD_MTL_W_GP", 	 		sYdMtlWGp);       						// 야드재료폭구분
					recMtl.setField("YD_MTL_T_GP", 	 		sYdMtlTGp);       						// 야드재료두께구분
					recMtl.setField("YD_MTL_L_GP", 	 		sYdMtlLGp);       						// 야드재료길이구분
					recMtl.setField("REGISTER",				szMODIFIER);
					recMtl.setField("MODIFIER",				szMODIFIER);
					recMtl.setField("DEL_YN",				"N");
					recMtl.setField("YD_STK_LYR_NO",		"001");									// 적치단
					recMtl.setField("YD_STK_LYR_MTL_STAT", 	"C");									// 재료적치상태
					recMtl.setField("RGNT_PK_CNTS", 		sRgntPkCnts);							// 시편채취내용

					sMsg = "[" + szOperationName + "] 가스절단실적 등록데이터 :: " + sStlNo;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 recMtl에 logId 추가
					recMtl.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
																											        																			              	        	
					//----------------------------------------------------------------------
					// 3.2. 정정야드 저장품 정보 등록
					//----------------------------------------------------------------------
					if ("".equals(sDelYn)) {
						// 정정야드저장품 Insert
						intRtnVal = ydStockDao.insYdStockCutResult(recMtl);
					} else {
						// 정정야드저장품 Update
						intRtnVal = ydStockDao.updYdStockCutResult(recMtl);
					}
					if (intRtnVal != 1) {
						szRtnMsg = "가스절단실적 등록 오류 .. 재료번호 : " + sStlNo;
						sMsg = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
					}

					if ("Y".equals(szYdStockExist)) {
						//----------------------------------------------------------------------
						// 3.3. 적치단 정보 등록
						//----------------------------------------------------------------------
						// 야드맵 업데이트 : Null일때는 Skip
						if (!"".equals(sYdStkBedNo) && !"".equals(sYdStkColGp)) {
																																		        																			              	        	
							intRtnVal = ydStkLyrDao.updYdStklyrStat(recMtl);
							if (intRtnVal != 1) {
								szRtnMsg = "가스절단실적 적치단 등록 오류 .. 재료번호 : " + sStlNo;
								sMsg = "[" + szOperationName + "] " + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
								
								return szRtnMsg;
							}
						}
					} else {
						// 날판 저장위치 미존재시는 저장위치를 등록하지 않고 추후 수작업으로 저장위치를 등록하여야함 (윤재광과장님 요청사항)
						sMsg =  "[" + szOperationName + "] 날판 저장위치 미존재하여 절단 재료 적치단 등록 SKIP";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);
					}

					//----------------------------------------------------------------------
					// 3.4. 야드L2 전문송신 (저장품제원 :: YDY7L002 전송)
					//----------------------------------------------------------------------
					sMsg = "[ " +szOperationName + "] 야드L2 저장품제원 전문송신 START";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);

		        	recL2Para = JDTORecordFactory.getInstance().create();
		        	recL2Para.setField("JMS_TC_CD", 		"YDY7L002"							);		// TC-CODE
		        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD	);		// 야드구분
		        	recL2Para.setField("YD_STK_COL_GP", 	sYdStkColGp							);		// 야드적치열구분
		        	recL2Para.setField("YD_STK_BED_NO", 	sYdStkBedNo							);		// 야드적치BED번호
		        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5"									);		// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
		        	recL2Para.setField("STL_NO", 			sStlNo								);		// 재료번호

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 recL2Para에 logId 추가
		        	recL2Para.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
		        			        																			              	        	
		        	szRtnMsg = ydDelegate.sendMsg(recL2Para);

					sMsg = "[ " + szOperationName + "] 야드L2 저장품제원 전문송신 END >>>> " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);
				}

				// 가스장 추출 스케쥴을 검사실적 : 차행선결정정보 수신 (PPYDJ015) 수신후 처리하도록 보완
				// 2013.09.03 화면에서 북아웃요청시 스케쥴 기동하도록 보완

				if ("Y".equals(szYdStockExist) && "Y".equals(szYdSchCall)) {
					//----------------------------------------------------------------------
					// 4. GAS장 추출 스케줄 기동
					//----------------------------------------------------------------------
					sMsg = "[" + szOperationName + "] 4. GAS장 추출 스케줄 기동";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);

					EJBConnector ejbConn 	= new EJBConnector("default", this);

					String sYD_BAY_GP 		= sYdStkColGp.substring(1, 2);
					String sYD_SPAN_GP 		= sYdStkColGp.substring(2, 4);
					String sYD_AIM_YD_GP	= JPlateYdConst.YD_GP_F_PLATE_YARD;		// 목표야드구분
					String sYD_AIM_BAY_GP	= sYD_BAY_GP;								// 목표동구분
					String sYD_AIM_SPAN_GP	= "";										// 목표스판구분
					String sYD_AIM_COL_GP	= "";										// 목표적치열구분
					String sYD_AIM_BED_NO	= "";										// 목표적치BED구분

					for(int ii=0; ii<iCutCnt; ii++) {

						recPara = JDTORecordFactory.getInstance().create();

						recPara.setField("YD_GP",				JPlateYdConst.YD_GP_F_PLATE_YARD);
						recPara.setField("YD_BAY_GP", 	      	sYD_BAY_GP);
						recPara.setField("YD_SPAN_GP", 	      	sYD_SPAN_GP);
						// 주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차
						recPara.setField("YD_MAIN_WRK_GP", 	  	JPlateYdConst.YD_MAIN_WRK_GP_GAS_OUT);
						recPara.setField("YD_TO_LOC_GUIDE_GP",	"Y");
						recPara.setField("YD_AIM_YD_GP", 	  	sYD_AIM_YD_GP);			// 목표야드구분
						recPara.setField("YD_AIM_BAY_GP", 	  	sYD_AIM_BAY_GP);		// 목표동구분
						recPara.setField("YD_AIM_SPAN_GP", 	  	sYD_AIM_SPAN_GP);		// 목표스판구분
						recPara.setField("YD_AIM_COL_GP", 	  	sYD_AIM_COL_GP);		// 목표적치열구분
						recPara.setField("YD_AIM_BED_NO", 	  	sYD_AIM_BED_NO);		// 목표적치BED구분

						//---------------------------------------------------------------------------------------------
						//	후판정정의 준비스케줄 등록 시 지정한 작업매수의 값을 전달하여 해당하는 매수만큼 작업예약에 등록
						//---------------------------------------------------------------------------------------------
						recPara.setField("YD_EQP_WRK_SH", 	  	"1");
						recPara.setField("ARR_WLOC_CD", 	    "");

						//---------------------------------------------------------------------------------------------
						//	작업재료리스트와 JMS_TC_CD
						//---------------------------------------------------------------------------------------------
						recPara.setField("STL_LIST", 	        arrStlNo[ii]);
						recPara.setField("JMS_TC_CD", 	        sRcvTcCode);
						recPara.setField("YD_USER_ID", 	        szMODIFIER);

						sMsg = "[" + szOperationName + "] GAS장 추출 스케줄 기동 .. START";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);
						
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 recPara에 logId 추가
						recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
								        			        																			              	        	
						//내부 Process 연결
						szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapa", recPara);

						sMsg =  "[" + szOperationName + "] GAS장 추출 스케줄 기동 .. END";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);

						if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
							sMsg =  "[" + szOperationName + "] GAS장 추출 스케줄 기동 .. 결과 :: " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
							
							return szRtnMsg;
						}
					}
				} else {
					sMsg =  "[" + szOperationName + "] GAS장 추출 스케쥴 SKIP >>>> 재료존재 :: " + szYdStockExist + ", 스케쥴기동 :: " + szYdSchCall;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);
				}


			} catch(Exception e) {
				sMsg = "[후판정정야드가스절단실적] Exception Error 발생 ";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
				throw new DAOException("[후판정정야드가스절단실적]" + sMsg);
			} // end of try-catch
		}

		ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, "[후판정정야드가스절단실적] 처리(" + sMethodName + ") 완료", JPlateYdConst.DEBUG, logId);
		return JPlateYdConst.RETN_CD_SUCCESS;

	} // end of procPPGasCutResult


	/**
	 * 오퍼레이션명 : 후판조업 차행선결정정보 수신 (PPYDJ015)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public String procPPNextDeciInfo(JDTORecord inRecord)throws JDTOException  {
		// DAO 및 UTIL 객체 생성
		JPlateYdStockDAO 	ydStockDao	= new JPlateYdStockDAO();
		JPlateYdStkLyrDAO	ydStkLyrDAO	= new JPlateYdStkLyrDAO();

		// 레코드 선언
		JDTORecordSet rsYdStock     = null;
		JDTORecord recPara 		    = null;
		JDTORecord recMtl 			= null;

		// 변수선언
		String	szMethodName 		= "procPPNextDeciInfo";
		String	szOperationName 	= "후판조업 차행선결정정보 수신";
		String	szRtnMsg			= "";
		String 	szMsg 				= "";
		String 	szRcvTcCode			= ydUtils.getTcCode(inRecord);

		String 	szStlNo				= "";		// 재료번호
		String 	szUsMaintmatl		= "";		// 상면보수재
		String 	szLsMaintmatl		= "";		// 하면보수재
		String 	szCplWrkMtl			= "";		// 냉간교정재
		String 	szHttrtHplMtl		= "";		// 열처리교정재
		String 	szGasWrkMtl			= "";		// GAS작업재
		String 	szShotBlstWrkMtl	= "";		// ShortBlast작업재
		String 	szPressWrkMtl		= "";		// 프레스교정재
		String 	szPlWrPrsntProcCd	= "";		// 후판실적현공정코드
		String	szModifier			= "";

		String	szYdStkColGp		= "";
		String	szYdStkBedNo 		= "";
		String	szYdStkLyrNo 		= "";
		String  szYdBayGp 			= "";
		String	szYdSpanGp 			= "";
		String	szYdAimYdGp			= "";			// 목표야드구분
		String	szYdAimBayGp		= "";			// 목표동구분
		String	szYdAimSpanGp		= "";			// 목표스판구분
		String	szYdAimColGp		= "";			// 목표적치열구분
		String	szYdAimBedNo		= "";			// 목표적치BED구분
		String	szYdMtlL			= "";
		String	szPlMeaGdsL			= "";
		String	szYdStkLyrMtlStat	= "";
		String	szGdsMainGrd		= "";

		String	szMtlStatCd			= "";			// 재료진행상태 : 종료된 재료는 스케줄 기동 안하도록 체크

		int    	intRtnVal 			= 0;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.19 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(inRecord, "F");  		// JDTORecord 에서 logid get(1 : JDTORecord.getResultCode(), Field명 - 2 : UNIQUE_ID, 3 : LOG_ID, 4 : 새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
						      		                     
		try {
			if (szRcvTcCode == null) {
				szMsg ="[ERROR] " + SZ_SESSION_NAME + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

			} else {

			/*
				STL_NO				재료번호			CHAR	11	Y	후판Plate번호 또는 후판날판번호
				US_MAINTMATL		상면보수재		CHAR	1	0	해당작업일경우 "Y"
				LS_MAINTMATL		하면보수재		CHAR	1	0	해당작업일경우 "Y"
				CPL_WRK_MTL			냉간교정재		CHAR	1	0	해당작업일경우 "Y"
				HTTRT_HPL_MTL		열처리교정재		CHAR	1	0	해당작업일경우 "Y"
				GAS_WRK_MTL			GAS작업재			CHAR	1	0	해당작업일경우 "Y"
				SHOT_BLST_WRK_MTL	ShortBlast작업재	CHAR	1	0	해당작업일경우 "Y"
				PRESS_WRK_MTL		프레스교정재		CHAR	1	0	해당작업일경우 "Y"
				PL_WR_PRSNT_PROC_CD	후판실적현공정코드	CHAR	2	0	2O:ON-LINE 입고,2N:OFF-LINE입고
				GDS_MAIN_GRD		제품주등급		CHAR	1	0	6:스크랩, 7:충당대기
			*/

				szStlNo				= ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"				);		// 재료번호
				szUsMaintmatl		= ydDaoUtils.paraRecChkNull(inRecord, "US_MAINTMATL"		);		// 상면보수재
				szLsMaintmatl		= ydDaoUtils.paraRecChkNull(inRecord, "LS_MAINTMATL"		);		// 하면보수재
				szCplWrkMtl			= ydDaoUtils.paraRecChkNull(inRecord, "CPL_WRK_MTL"			);		// 냉간교정재
				szHttrtHplMtl		= ydDaoUtils.paraRecChkNull(inRecord, "HTTRT_HPL_MTL"		);		// 열처리교정재
				szGasWrkMtl			= ydDaoUtils.paraRecChkNull(inRecord, "GAS_WRK_MTL"			);		// GAS작업재
				szShotBlstWrkMtl	= ydDaoUtils.paraRecChkNull(inRecord, "SHOT_BLST_WRK_MTL"	);		// ShortBlast작업재
				szPressWrkMtl		= ydDaoUtils.paraRecChkNull(inRecord, "PRESS_WRK_MTL"		);		// 프레스교정재
				szPlWrPrsntProcCd	= ydDaoUtils.paraRecChkNull(inRecord, "PL_WR_PRSNT_PROC_CD"	);		// 후판실적현공정코드
				szGdsMainGrd		= ydDaoUtils.paraRecChkNull(inRecord, "GDS_MAIN_GRD"		);		// 제품주등급
				szModifier			= ydDaoUtils.paraRecModifier(inRecord						);		// 수정자

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 	szStlNo);						// 재료번호

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.19 recPara에 logId 추가 
				recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
								              	        	
				rsYdStock = JDTORecordFactory.getInstance().createRecordSet("yd");
				intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsYdStock);

				if (intRtnVal != 1) {
					// 재료 미존재시는 Insert
					// ydStockDao.insYdStockCutResult(recPara2);

					szRtnMsg = "재료정보 미존재 :: " + szStlNo;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					//throw new DAOException(szRtnMsg);
					
					return szRtnMsg;

				} else {

					recMtl = rsYdStock.getRecord(0);

					szYdStkColGp 		= ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_COL_GP"			);
					szYdStkBedNo 		= ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_BED_NO"			);
					szYdStkLyrNo 		= ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_LYR_NO"			);
					szMtlStatCd			= ydDaoUtils.paraRecChkNull(recMtl, "MTL_STAT_CD"			);
					szYdMtlL			= ydDaoUtils.paraRecChkNull(recMtl, "YD_MTL_L"				);
					szYdStkLyrMtlStat 	= ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_LYR_MTL_STAT"	);
					szPlMeaGdsL			= ydDaoUtils.paraRecChkNull(recMtl, "PL_MEA_GDS_L"			);

					// 재료 존재시는 Update
					recMtl.setField("DEL_YN", 					"N"					);
					recMtl.setField("STL_NO", 					szStlNo				);
					recMtl.setField("MODIFIER", 				szModifier			);

					recMtl.setField("US_MAINTMATL", 			"N"					);		// 상면보수재
					recMtl.setField("US_MAINT_SCH_MAKE_YN", 	"N"					);		// 상면보수스케줄작성여부
					recMtl.setField("US_MAINT_WRK_CMPL_YN", 	"N"					);		// 상면보수작업완료여부
					recMtl.setField("LS_MAINTMATL", 			"N"					);		// 하면보수재
					recMtl.setField("LS_MAINT_SCH_MAKE_YN", 	"N"					);		// 하면보수스케줄작성여부
					recMtl.setField("LS_MAINT_WRK_CMPL_YN",		"N"					);		// 하면보수작업완료여부
					recMtl.setField("CPL_WRK_MTL", 				"N"					);		// 냉간교정재
					recMtl.setField("CR_CORR_SCH_MAKE_YN", 		"N"					);		// 냉간교정스케줄작성여부
					recMtl.setField("CR_CORR_WRK_CMPL_YN", 		"N"					);		// 냉간교정작업완료여부
					recMtl.setField("HTTRT_HPL_MTL", 			"N"					);		// 열처리교정재
					recMtl.setField("HTTRT_CORR_SCH_MAKE_YN", 	"N"					);		// 열처리교정스케줄작성여부
					recMtl.setField("HTTRT_CORR_WRK_CMPL_YN", 	"N"					);		// 열처리교정작업완료여부
					recMtl.setField("GAS_WRK_MTL", 				"N"					);		// GAS작업재
					recMtl.setField("GAS_WRK_SCH_MAKE_YN", 		"N"					);		// Gas작업스케줄작성여부
					recMtl.setField("GAS_WRK_WRK_CMPL_YN", 		"N"					);		// Gas작업작업완료여부
					recMtl.setField("SHOT_BLST_WRK_MTL", 		"N"					);		// ShortBlast작업재
					recMtl.setField("S_BLST_WRK_SCH_MAKE_YN", 	"N"					);		// ShortBlast작업스케줄작성여부
					recMtl.setField("S_BLST_WRK_WRK_CMPL_YN", 	"N"					);		// ShortBlast작업작업완료여부
					recMtl.setField("PRESS_WRK_MTL", 			"N"					);		// 프레스교정재
					recMtl.setField("PRS_CORR_SCH_MAKE_YN", 	"N"					);		// Press교정스케줄작성여부
					recMtl.setField("PRS_CORR_WRK_CMPL_YN", 	"N"					);		// Press교정작업완료여부
					recMtl.setField("GDS_MAIN_GRD",				szGdsMainGrd		);		// 충당대상재

					if ("Y".equals(szUsMaintmatl)) {
						recMtl.setField("US_MAINTMATL", 		szUsMaintmatl		);		// 상면보수재
					}
					if ("Y".equals(szLsMaintmatl)) {
						recMtl.setField("LS_MAINTMATL", 		szLsMaintmatl		);		// 하면보수재
					}
					if ("Y".equals(szCplWrkMtl)) {
						recMtl.setField("CPL_WRK_MTL", 			szCplWrkMtl			);		// 냉간교정재
					}
					if ("Y".equals(szHttrtHplMtl)) {
						recMtl.setField("HTTRT_HPL_MTL", 		szHttrtHplMtl		);		// 열처리교정재
					}
					if ("Y".equals(szGasWrkMtl)) {
						recMtl.setField("GAS_WRK_MTL", 			szGasWrkMtl			);		// GAS작업재
					}
					if ("Y".equals(szShotBlstWrkMtl)) {
						recMtl.setField("SHOT_BLST_WRK_MTL", 	szShotBlstWrkMtl	);		// ShortBlast작업재
					}
					if ("Y".equals(szPressWrkMtl)) {
						recMtl.setField("PRESS_WRK_MTL", 		szPressWrkMtl		);		// 프레스교정재
					}
					if ("Y".equals(szPlWrPrsntProcCd)) {
						recMtl.setField("PL_WR_PRSNT_PROC_CD", 	szPlWrPrsntProcCd	);		// 후판실적현공정코드
					}

					if (!szYdMtlL.equals(szPlMeaGdsL) && !"".equals(szYdStkColGp)) {
						// 점유상태 CLEAR
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("MODIFIER",			szModifier);
						recPara.setField("STL_NO", 				szStlNo);			// 재료번호
						recPara.setField("YD_STK_LYR_MTL_STAT",	"E");
						recPara.setField("YD_STK_COL_GP",		szYdStkColGp);
						recPara.setField("YD_STK_BED_NO",		szYdStkBedNo);
						recPara.setField("YD_STK_LYR_NO",		szYdStkLyrNo);

						intRtnVal = ydStkLyrDAO.updYdStklyrStat(recMtl);

						szMsg ="[" + SZ_SESSION_NAME + "::" + szMethodName + "] 점유상태 CLEAR >>> 결과 :: " + intRtnVal;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					}

					// 두께,폭,길이,중량 UPDATE 추가
					recMtl.setField("YD_MTL_T", 	ydDaoUtils.paraRecChkNull(recMtl, "PL_MEA_GDS_T")	);		// 제촌두께
					recMtl.setField("YD_MTL_W", 	ydDaoUtils.paraRecChkNull(recMtl, "PL_MEA_GDS_W")	);		// 제촌폭
					recMtl.setField("YD_MTL_L", 	ydDaoUtils.paraRecChkNull(recMtl, "PL_MEA_GDS_L")	);		// 제촌길이
					recMtl.setField("YD_MTL_WT", 	ydDaoUtils.paraRecChkNull(recMtl, "PL_MEA_GDS_WT")	);		// 제촌중량

					intRtnVal = ydStockDao.updNextDeciInfo(recMtl);

					szMsg ="[" + SZ_SESSION_NAME + "::" + szMethodName + "] 야드재료정보 UPDATE >>> 결과 :: " + intRtnVal;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					if (!szYdMtlL.equals(szPlMeaGdsL) && !"".equals(szYdStkColGp)) {
						// 점유상태 SET
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("MODIFIER",			szModifier			);
						recPara.setField("STL_NO", 				szStlNo				);		// 재료번호
						recPara.setField("YD_STK_LYR_MTL_STAT",	szYdStkLyrMtlStat	);
						recPara.setField("YD_STK_COL_GP",		szYdStkColGp		);
						recPara.setField("YD_STK_BED_NO",		szYdStkBedNo		);
						recPara.setField("YD_STK_LYR_NO",		szYdStkLyrNo		);

						intRtnVal = ydStkLyrDAO.updYdStklyrStat(recPara);

						szMsg ="[" + SZ_SESSION_NAME + "::" + szMethodName + "] 점유상태 SET >>> 결과 :: " + intRtnVal;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					}
				}
			}

			szYdSpanGp = ydUtils.substr(szYdStkColGp, 2, 2);

			// 2013.07.05 :: 종료된 재료는 추출 스케줄 기동 안하도록 보완
			if ("3".equals(szMtlStatCd)) {

				szRtnMsg = "해당 재료가 종료되어 추출 스케줄 기동 SKIP함!!!! 재료번호 :: " + szStlNo + ", 저장위치 :: " + szYdStkColGp;
				szMsg 	 = "["+szOperationName+"] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
				return szRtnMsg;

			} else {

				// 저장위치가 가스장/TOD 일때 추출 스케쥴 기동
				// 저장위치가 이면검사일때 이적 스케쥴 기동
				if ("CN".equals(szYdSpanGp) || "TD".equals(szYdSpanGp) || "FA0204".equals(szYdStkColGp) || "FB0403".equals(szYdStkColGp)) {

					//----------------------------------------------------------------------
					// GAS/TOD/이면검사 추출 스케줄 기동
					//----------------------------------------------------------------------
					szMsg = "["+szOperationName+"] GAS/TOD/이면검사 추출 스케줄 기동";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					EJBConnector ejbConn 	= new EJBConnector("default", this);

					szYdBayGp 		= ydUtils.substr(szYdStkColGp, 1, 1);
					szYdSpanGp 		= ydUtils.substr(szYdStkColGp, 2, 2);
					szYdAimYdGp		= JPlateYdConst.YD_GP_F_PLATE_YARD;		// 목표야드구분
					szYdAimBayGp	= szYdBayGp;								// 목표동구분
					szYdAimSpanGp	= "";										// 목표스판구분
					szYdAimColGp	= "";										// 목표적치열구분
					szYdAimBedNo	= "";										// 목표적치BED구분

					recPara = JDTORecordFactory.getInstance().create();

					recPara.setField("YD_GP",				JPlateYdConst.YD_GP_F_PLATE_YARD		);
					recPara.setField("YD_BAY_GP", 	      	szYdBayGp								);
					recPara.setField("YD_SPAN_GP", 	      	szYdSpanGp								);

					// 주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차, M:보수장이적
					if ("CN".equals(szYdSpanGp)) {
						recPara.setField("YD_MAIN_WRK_GP", 	JPlateYdConst.YD_MAIN_WRK_GP_GAS_OUT	);
					} else if ("TD".equals(szYdSpanGp)) {
						recPara.setField("YD_MAIN_WRK_GP", 	JPlateYdConst.YD_MAIN_WRK_GP_TOD_OUT	);
					} else {
						recPara.setField("YD_MAIN_WRK_GP", 	JPlateYdConst.YD_MAIN_WRK_GP_MV		);
					}
					recPara.setField("YD_TO_LOC_GUIDE_GP",	"Y"										);
					recPara.setField("YD_AIM_YD_GP", 	  	szYdAimYdGp								);		// 목표야드구분
					recPara.setField("YD_AIM_BAY_GP", 	  	szYdAimBayGp							);		// 목표동구분
					recPara.setField("YD_AIM_SPAN_GP", 	  	szYdAimSpanGp							);		// 목표스판구분
					recPara.setField("YD_AIM_COL_GP", 	  	szYdAimColGp							);		// 목표적치열구분
					recPara.setField("YD_AIM_BED_NO", 	  	szYdAimBedNo							);		// 목표적치BED구분

					//---------------------------------------------------------------------------------------------
					//	후판정정의 준비스케줄 등록 시 지정한 작업매수의 값을 전달하여 해당하는 매수만큼 작업예약에 등록
					//---------------------------------------------------------------------------------------------
					recPara.setField("YD_EQP_WRK_SH", 	  	"1"										);
					recPara.setField("ARR_WLOC_CD", 	    ""										);

					//---------------------------------------------------------------------------------------------
					//	작업재료리스트와 JMS_TC_CD
					//---------------------------------------------------------------------------------------------
					recPara.setField("STL_LIST", 	        szStlNo									);
					recPara.setField("JMS_TC_CD", 	        szRcvTcCode								);
					recPara.setField("YD_USER_ID", 	        szModifier								);

					szMsg = "["+szOperationName+"] GAS/TOD/이면검사 추출 스케줄 기동 .. START";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.19 recPara에 logId 추가 
					recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

					//내부 Process 연결
					szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapa", recPara);

					szMsg = "["+szOperationName+"] GAS/TOD/이면검사 추출 스케줄 기동 .. END";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						szMsg =  "["+szOperationName+"] GAS/TOD/이면검사 추출 스케줄 기동 .. 결과 :: "+ szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
					}

				// 저장위치가 보수장일때 추출 스케쥴 기동
				} else if ("BS".equals(szYdSpanGp)) {

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("JMS_TC_CD",		szRcvTcCode);
					recPara.setField("ARR_STL_NO", 		szStlNo);
					recPara.setField("MODIFIER", 		szModifier);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.19 recPara에 logId 추가 
					recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

					szRtnMsg = this.procBsOut(recPara);

					if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						szMsg =  "["+szOperationName+"] 보수장 추출 스케줄 기동 .. 결과 :: "+ szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return szRtnMsg;
					}
				}
			}

		} catch(Exception e) {
			szRtnMsg = "후판정정차행선결정정보 수신오류 >>>> " +e.getMessage();
			szMsg    = "[후판정정차행선결정정보] Exception Error :: " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			throw new DAOException(szRtnMsg);
		} // end of try-catch

		szMsg = "[후판정정차행선결정정보] 처리("+szMethodName+") 완료";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		return JPlateYdConst.RETN_CD_SUCCESS;

	} // end of procPPNextDeciInfo


	/**
	 * 오퍼레이션명 : 후판조업 극후물냉각대 BOOK-OUT 실적 수신 (PPYDJ016)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public String procPPCBRTBookOut(JDTORecord inRecord)throws JDTOException  {
		// DAO 및 UTIL 객체 생성
		JPlateYdStockDAO  ydStockDao	= new JPlateYdStockDAO();
		JPlateYdStkLyrDAO ydStkLyrDao	= new JPlateYdStkLyrDAO();

		// 레코드 선언
		JDTORecordSet 	rsResult  	= null;
		JDTORecord 		inRec 		= null;
		JDTORecord 		recOut		= null;
		JDTORecord 		recL2Para	= null;
		JDTORecord 		recL3Para	= null;
		JDTORecord 		recPara 	= null;

		// 변수선언
		String	szMethodName 		= "procPPCBRTBookOut";
		String	szOperationName 	= "극후물냉각대 BOOK-OUT 실적 수신";
		String	szRtnMsg			= "";
		String 	szMsg 				= "";
		String 	szRcvTcCode			= ydUtils.getTcCode(inRecord);

		String 	szStlNo				= "";		// 재료번호
		String	szModifier			= "";
		String	szPlTrckZoneNo		= "";
		String	szFrYdStkColGp		= "";
        String	szToYdStkColGp  	= "";		// 냉각대 야드적치열구분
        String	szToYdStkBedNo  	= "";    	// 냉각대 야드적치BED번호
        String	szToYdStkLyrNo  	= "";    	// 냉각대 적치단
		String	szYdStkColGp		= "";

		int    	intRtnVal 			= 0;

		try {
			if (szRcvTcCode == null) {
				szRtnMsg = "TC Code Error (" + szRcvTcCode + ")";
				szMsg    = "[" + szMethodName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			/*
			  	YD_GP				야드구분			CHAR	1	Y
				STL_APPEAR_GP		재료외형구분		CHAR	1	Y
				PL_MTL_NO			후판재료번호		CHAR	11	Y	후판날판번호 또는 SLAB번호
				PL_TRCK_ZONE_NO		후판트래킹존번호	NUMBER	5	Y
			*/

			szStlNo			= ydDaoUtils.paraRecChkNull(inRecord, "PL_MTL_NO");				// 후판재료번호
			szPlTrckZoneNo	= ydDaoUtils.paraRecChkNull(inRecord, "PL_TRCK_ZONE_NO");		// 후판트래킹존번호
			szModifier		= ydDaoUtils.paraRecChkNull(inRecord, "YD_USER_ID");			// 수정자
			if ("".equals(szModifier)) {
				szModifier	= ydDaoUtils.paraRecChkNull(inRecord, "MODIFIER", szRcvTcCode);
			}

			//서버 적용여부 : 후판슬라브 재열재 자동이적 로직 적용여부
			String methodNm = "후판조업 극후물냉각대 BOOK-OUT 실적 수신 (PPYDJ016)[JPlateYdL3RcvSeEJBBean.procPPCBRTBookOut] < " + inRecord.getResultMsg();
			String logId = inRecord.getResultCode();
			slabUtils.printLog(logId, methodNm, "S+");
			
			
			slabUtils.printLog(logId, "(PPYDJ016) 후판트래킹존번호번호 [" + szPlTrckZoneNo + "]", "SL");
			
			//"후판트래킹존번호번호"가 '12500'이면 후판슬라브 처리 
			if ("12500".equals(szPlTrckZoneNo)) {
				slabUtils.printLog(logId, "(PPYDJ016) 후판슬라브 재열재 작업 시작...", "SL");
				PSlabYdL3RcvSeEJBBean pSlabYdL3RcvSeEJBBean = new PSlabYdL3RcvSeEJBBean();
				pSlabYdL3RcvSeEJBBean.rcvPPYDJ016(inRecord);
				
				slabUtils.printLog(logId, methodNm, "S-");
				return JPlateYdConst.RETN_CD_SUCCESS;
			}

			
			// 후판트래킹존번호ZONE NO를 야드저장위치 코드로 변경
			if (!"".equals(szPlTrckZoneNo)) {
				// ZONE NO를 야드저장위치로 변경 :: 33000 , 33005 >>>> FDRT02
				szFrYdStkColGp = JPlateYdCommonUtils.getY7RtZoneToLoc(szPlTrckZoneNo);
			}

			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
			inRec = JDTORecordFactory.getInstance().create();
			inRec.setField("STL_NO", 		szStlNo);             	// 재료번호

			intRtnVal = ydStockDao.getYdStockWithLoc(inRec, rsResult);
			if (intRtnVal > 0) {

				recOut = JDTORecordFactory.getInstance().create();

    			rsResult.first();
    			recOut = rsResult.getRecord();
    			szYdStkColGp = ydDaoUtils.paraRecChkNull(recOut, "YD_STK_COL_GP");

    			if ("".equals(szYdStkColGp) || "RT".equals(ydUtils.substr(szYdStkColGp, 2, 2))) {

					szMsg    = "["+szOperationName+"] 해당 재료의 저장위치 :: " + szYdStkColGp + " 계속 진행 ";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

    			} else {
					szRtnMsg = "야드재료가 이미 존재합니다 .";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
    			}
			}

			//------------------------------------------------------------------------------------------------
			// 2.1. 야드재료 등록
			//------------------------------------------------------------------------------------------------
			szMsg    = "["+szOperationName+"] ----------- 야드재료 등록 START ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			// 레코드 편성
			inRec = JDTORecordFactory.getInstance().create();
			inRec.setField("REGISTER", 		szModifier);				// 등록자
			inRec.setField("STL_NO", 		szStlNo);             		// 재료번호

			intRtnVal = ydStockDao.insYdStockBookOut(inRec);
			if (intRtnVal <= 0) {
				szRtnMsg = "야드재료 등록 ERROR .. " + Integer.toString(intRtnVal);
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			//------------------------------------------------------------------------------------------------
			// 2.2. 극후물 냉각대 적치 가능 베드 조회
			//------------------------------------------------------------------------------------------------
			szToYdStkColGp = "FDCB01";		// 극후물 냉각대 저장위치 구분

			rsResult = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP",  szToYdStkColGp);

			intRtnVal = ydStkLyrDao.getEmptyToLoc(recPara, rsResult);
			if (intRtnVal <= 0) {
				szRtnMsg = "해당 냉각대에 적치가능한 저장위치가 없습니다!" + szToYdStkColGp;
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}
			rsResult.first();
			recOut = rsResult.getRecord();
	        szToYdStkColGp  = ydDaoUtils.paraRecChkNull(recOut, "YD_STK_COL_GP");     			// 냉각대 야드적치열구분
	        szToYdStkBedNo  = ydDaoUtils.paraRecChkNull(recOut, "YD_STK_BED_NO");    			// 냉각대 야드적치BED번호
	        szToYdStkLyrNo  = ydDaoUtils.paraRecChkNull(recOut, "YD_STK_LYR_NO");    			// 냉각대 적치단

			//------------------------------------------------------------------------------------------------
			// 2.3. 저장위치 수정
			//------------------------------------------------------------------------------------------------
			szMsg    = "["+szOperationName+"] ----------- 적치단 야드적치단재료상태 수정 START";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//적치단 야드적치단재료상태 수정
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", 		szToYdStkColGp);            // 야드적치열구분
			recPara.setField("YD_STK_BED_NO", 		szToYdStkBedNo);    		// 야드적치BED번호
			recPara.setField("YD_STK_LYR_NO", 		szToYdStkLyrNo);    		// 야드적치단
			recPara.setField("STL_NO", 				szStlNo);             		// 재료번호
			recPara.setField("YD_STK_LYR_MTL_STAT", "C"); 						// 적치완료
			recPara.setField("MODIFIER", 			szModifier);				// 등록자

			intRtnVal = ydStkLyrDao.updYdStklyrStat(recPara);
			if (intRtnVal <= 0) {
				szRtnMsg = "야드적치단재료상태 수정 ERROR .. " + Integer.toString(intRtnVal);
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			//------------------------------------------------------------------------------------------------
			// 2.4. 저장위치 변경정보 송신 (야드L2)
			//------------------------------------------------------------------------------------------------
			szMsg = "[ " +szOperationName + "] 야드L2 저장품제원 전문송신 START";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			recL2Para = JDTORecordFactory.getInstance().create();
			recL2Para.setField("JMS_TC_CD", 		"YDY7L002");                            // TC-CODE
			recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);		// 야드구분
			recL2Para.setField("YD_STK_COL_GP", 	szToYdStkColGp);                        // 야드적치열구분
			recL2Para.setField("YD_STK_BED_NO", 	"01");    								// 야드적치BED번호
			recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드  [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
			recL2Para.setField("STL_NO", 			szStlNo);	        					// 재료번호
			szRtnMsg = ydDelegate.sendMsg(recL2Para);

			szMsg = "[ " +szOperationName + "] 야드L2 저장품제원 전문송신 END >>>> " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//------------------------------------------------------------------------------------------------
			// 2.5. 저장위치 변경정보 송신 (후판조업)
			//------------------------------------------------------------------------------------------------
			szMsg = "[ " +szOperationName + "] 후판조업 저장위치변경정보 전문송신 START";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			recL3Para = JDTORecordFactory.getInstance().create();
			recL3Para.setField("MSG_ID", 			"YDPPJ011");
			recL3Para.setField("YD_STK_COL_FR", 	szFrYdStkColGp);			// From적치열
			recL3Para.setField("YD_STK_BED_FR", 	"01");						// From적치BED
			recL3Para.setField("YD_STK_COL_TO", 	szToYdStkColGp);			// TO적치열
			recL3Para.setField("YD_STK_BED_TO", 	szToYdStkBedNo);			// TO적치BED
			recL3Para.setField("YD_EQP_WRK_SH", 	"1");						// 야드설비작업매수
            recL3Para.setField("ARR_STL_NO", 		szStlNo);

			szRtnMsg = ydDelegate.sendMsg(recL3Para);

			szMsg = "[ " +szOperationName + "] 후판조업 저장위치변경정보 전문송신 END >>>> " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);


		} catch(Exception e) {
			szRtnMsg = "극후물냉각대 BOOK-OUT 실적 수신 오류 >>>> " +e.getMessage();
			szMsg    = "["+szOperationName+"] Exception Error :: " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw new DAOException(szRtnMsg);
		} // end of try-catch

		szMsg = "["+szOperationName+"] 처리("+szMethodName+") 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		return JPlateYdConst.RETN_CD_SUCCESS;

	} // end of procPPCBRTBookOut


	/**
	 * 오퍼레이션명 : 보수장 추출 스케줄 기동
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public String procBsOut(JDTORecord inRecord)throws JDTOException  {
		// DAO 및 UTIL 객체 생성
		JPlateYdStockDAO  ydStockDao 	= new JPlateYdStockDAO();

		// 레코드 선언
		JDTORecordSet rsRsltStl   		= null;
		JDTORecord recPara 		    	= null;
		JDTORecord recMtl 				= null;

		// 변수선언
		String 	sMethodName 			= "procBsOut";
		String 	szOperationName 		= "보수장 추출스케줄기동";
		String 	sMsg 					= "";
		String 	sRcvTcCode				= ydUtils.getTcCode(inRecord);

		String	szMODIFIER				= "";

		String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
		int    	intRtnVal 				= 0;
		int    	iStlCnt					= 0;
		String	arrStlNo[]				= null;
		String	szARR_STL_NO			= "";		// 재료번호 List
		String 	szSTL_NO				= "";		// 재료번호
		String 	szYD_GP               	= "";		// 야드구분
		String 	szYD_STK_COL_GP	        = "";		// 야드적치열구분
		String  szYD_BAY_GP 			= "";		// 야드동구분
		String	szYD_SPAN_GP 			= "";		// 야드스판구분
		String	szYD_AIM_YD_GP			= "";		// 목표야드구분
		String	szYD_AIM_BAY_GP			= "";		// 목표동구분
		String	szYD_AIM_SPAN_GP		= "";		// 목표스판구분
		String	szYD_AIM_COL_GP			= "";		// 목표적치열구분
		String	szYD_AIM_BED_NO			= "";		// 목표적치BED구분

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.19 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(inRecord, "F");  		// JDTORecord 에서 logid get(1 : JDTORecord.getResultCode(), Field명 - 2 : UNIQUE_ID, 3 : LOG_ID, 4 : 새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
						      		                     
		try {

			//----------------------------------------------------------------------
			// 1. 보수장 추출 스케줄 기동
			//----------------------------------------------------------------------
			sMsg = "[" + szOperationName + "] 1. 보수장 추출 스케줄 기동";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);

			if (sRcvTcCode == null) {
				sMsg ="[ERROR] " + SZ_SESSION_NAME + "::" + sMethodName + "() TC Code Error (" + sRcvTcCode + ")";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);

			} else {

				szYD_GP  	  = JPlateYdConst.YD_GP_F_PLATE_YARD;
				szARR_STL_NO  = ydDaoUtils.paraRecChkNull(inRecord, "ARR_STL_NO");

				szMODIFIER	 = ydDaoUtils.paraRecChkNull(inRecord, 	"YD_USER_ID");
				if ("".equals(szMODIFIER)) {
					szMODIFIER = ydDaoUtils.paraRecChkNull(inRecord, "MODIFIER", sRcvTcCode);
				}
				arrStlNo 	= szARR_STL_NO.split(";");
				iStlCnt 	= arrStlNo.length;

				EJBConnector ejbConn = new EJBConnector("default", this);

				rsRsltStl = JDTORecordFactory.getInstance().createRecordSet("");

				for(int ii=0; ii<iStlCnt; ii++) {

					recPara  = JDTORecordFactory.getInstance().create();

					szSTL_NO = arrStlNo[ii];
					//----------------------------------------------------------------------
					// 2. 재료 저장위치 조회 - 야드저장품에서 변경전 저장위치 정보를 조회
					//----------------------------------------------------------------------
					sMsg = "["+szOperationName+"] 2.재료 저장위치 조회 ";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", 	szSTL_NO);				// 재료번호

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.19 recPara에 logId 추가 
					recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
									              	        	
					intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsRsltStl);
					if (intRtnVal != 1) {
						szRtnMsg = "재료정보 정보 미존재 .. 재료번호 : " + szSTL_NO;
						sMsg = "["+szOperationName+"] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
						
						return szRtnMsg;
					}
					
					rsRsltStl.first();
					recMtl = rsRsltStl.getRecord();

					szYD_STK_COL_GP  = ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_COL_GP");
					szYD_BAY_GP 	 = ydUtils.substr(szYD_STK_COL_GP, 1, 1);
					szYD_SPAN_GP 	 = ydUtils.substr(szYD_STK_COL_GP, 2, 2);
					szYD_AIM_YD_GP	 = szYD_GP;											// 목표야드구분
					szYD_AIM_BAY_GP	 = szYD_BAY_GP;										// 목표동구분
					szYD_AIM_SPAN_GP = "";												// 목표스판구분
					szYD_AIM_COL_GP	 = "";												// 목표적치열구분
					szYD_AIM_BED_NO	 = "";												// 목표적치BED구분

					recPara.setField("YD_GP",				szYD_GP				);
					recPara.setField("YD_BAY_GP", 	      	szYD_BAY_GP			);
					recPara.setField("YD_SPAN_GP", 	      	szYD_SPAN_GP		);
					// 주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차
					recPara.setField("YD_MAIN_WRK_GP", 	  	JPlateYdConst.YD_MAIN_WRK_GP_BS_OUT);
					recPara.setField("YD_TO_LOC_GUIDE_GP",	"Y");
					recPara.setField("YD_AIM_YD_GP", 	  	szYD_AIM_YD_GP		);		// 목표야드구분
					recPara.setField("YD_AIM_BAY_GP", 	  	szYD_AIM_BAY_GP		);		// 목표동구분
					recPara.setField("YD_AIM_SPAN_GP", 	  	szYD_AIM_SPAN_GP	);		// 목표스판구분
					recPara.setField("YD_AIM_COL_GP", 	  	szYD_AIM_COL_GP		);		// 목표적치열구분
					recPara.setField("YD_AIM_BED_NO", 	  	szYD_AIM_BED_NO		);		// 목표적치BED구분

					//---------------------------------------------------------------------------------------------
					//	후판정정의 준비스케줄 등록 시 지정한 작업매수의 값을 전달하여 해당하는 매수만큼 작업예약에 등록
					//---------------------------------------------------------------------------------------------
					recPara.setField("YD_EQP_WRK_SH", 	  	"1"					);
					recPara.setField("ARR_WLOC_CD", 	    ""					);

					//---------------------------------------------------------------------------------------------
					//	작업재료리스트와 JMS_TC_CD
					//---------------------------------------------------------------------------------------------
					recPara.setField("STL_LIST", 	        arrStlNo[ii]		);
					recPara.setField("JMS_TC_CD", 	        sRcvTcCode			);
					recPara.setField("YD_USER_ID", 	        szMODIFIER			);

					sMsg = "["+szOperationName+"] 3.보수장 추출 스케줄 기동 .. START";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.19 recPara에 logId 추가 
					recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
									              	        	
					//내부 Process 연결
					szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapa", recPara);

					sMsg =  "["+szOperationName+"] 3.보수장 추출 스케줄 기동 .. END";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);

					if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						sMsg =  "["+szOperationName+"] 3.보수장 추출 스케줄 기동 .. 결과 :: "+ szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
						
						return szRtnMsg;
					}
				}
			}

		} catch(Exception e) {
			sMsg = "["+szOperationName+"] Exception Error 발생 ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
			throw new DAOException(sMsg);
		} // end of try-catch

		sMsg = "["+szOperationName+"] 9.보수장 추출 스케줄 기동 완료";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);
		
		return JPlateYdConst.RETN_CD_SUCCESS;

	} // end of procBsOut


	/**
	 * 오퍼레이션명 : 이송 상차완료 실적 처리 [YDYDJ770]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public String procYdPcarWrkEnd(JDTORecord inRecord)throws JDTOException  {

		// DAO 및 UTIL 객체 생성
		JPlateYdStockDAO  	ydStockDao 	= new JPlateYdStockDAO();
		JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();

		// 레코드 선언
		JDTORecordSet outRecSet   		= null;
		JDTORecord recPara 		    	= null;
		JDTORecord recTemp 				= null;
		JDTORecord recL2Para			= null;
		JDTORecord recL3Para			= null;

		// 변수선언
		String 	szMethodName 			= "procYdPcarWrkEnd";
		String 	szOperationName 		= "이송 상차완료 실적 처리";
		String 	szMsg 					= "";
		String 	szRcvTcCode				= ydUtils.getTcCode(inRecord);
		String	szYdGp					= "";
		String	szStlNo					= "";
		String	szModifier				= "";
		String	szYdStkColGp			= "";
		String	szArrStlNo				= "";

		String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
		int    	intRtnVal 				= 0;
		int		iTotCh					= 0;

		try {

			//----------------------------------------------------------------------
			// 이송 상차완료 실적 처리
			//----------------------------------------------------------------------
			// JMS_TC_CD			JMS TC코드
			// PL_WR_GDS_TOT_SH		후판실적제품총매수
			// STL_NO1				재료번호1
			//----------------------------------------------------------------------
			szMsg = "["+szOperationName+"] 1. 이송 상차완료 실적 처리";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if (szRcvTcCode == null) {
				szMsg ="[ERROR] " + SZ_SESSION_NAME + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

			} else {

				szYdGp		= JPlateYdConst.YD_GP_F_PLATE_YARD;
				iTotCh  	= ydDaoUtils.paraRecChkNullInt(inRecord, 	"PL_WR_GDS_TOT_SH");
				szModifier	= ydDaoUtils.paraRecChkNull(inRecord, 		"YD_USER_ID");
				if ("".equals(szModifier)) {
					szModifier = ydDaoUtils.paraRecChkNull(inRecord, 	"MODIFIER", szRcvTcCode);
				}

				for(int ii=0; ii<iTotCh; ii++) {

					szStlNo  = ydDaoUtils.paraRecChkNull(inRecord, 	"STL_NO"+Integer.toString(ii+1));

					if (ii>0) {
						szArrStlNo += ";";
					}
					szArrStlNo += szStlNo;		// 조업 L3 저장위치 변경정보CLEAR 송신용 데이타

					recPara  = JDTORecordFactory.getInstance().create();
					recTemp  = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", 		szStlNo);
					recPara.setField("MODIFIER", 	szModifier);
					recPara.setField("YD_GP",		szYdGp);

					//-------------------------------
					// 저장위치정보 조회
					//-------------------------------
					outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
					intRtnVal = ydStkLyrDao.getYdStklyrByStlNo(recPara, outRecSet);
					// 재료 적치상태가 1건 이상일때 오류로 Set
					if (intRtnVal > 1 || intRtnVal <= 0) {
						szRtnMsg = "저장위치정보 조회시 오류 발생 .... 재료번호 :: " + szStlNo;
						szMsg    = "["+szOperationName+"] "+ szRtnMsg + " .... 결과 :: " + Integer.toString(intRtnVal);
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
					outRecSet.first();
					recTemp = outRecSet.getRecord();
					szYdStkColGp = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");

					//-------------------------------
					// 야드저장위치 체크
					//-------------------------------
					if (!"PT".equals(ydUtils.substr(szYdStkColGp, 2, 2))) {
						szRtnMsg = "현위치가 차량포인트가 아님으로 오류발생 .... 저장위치 :: " + szYdStkColGp;
						szMsg    = "["+szOperationName+"] "+ szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					//-------------------------------
					// 재료정보 삭제 처리
					//-------------------------------
					intRtnVal = ydStockDao.delYdStock(recPara);
					if (intRtnVal <= 0) {
						szRtnMsg = "재료정보 삭제 처리시 오류 발생 .... 재료번호 :: " + szStlNo;
						szMsg    = "["+szOperationName+"] "+ szRtnMsg + " .... 결과 :: " + Integer.toString(intRtnVal);
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					//-------------------------------
					// 저장위치 삭제 처리
					//-------------------------------
					intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
					if (intRtnVal <= 0) {
						szRtnMsg = "저장위치 삭제 처리시 오류 발생 .... 재료번호 :: " + szStlNo;
						szMsg    = "["+szOperationName+"] "+ szRtnMsg + " .... 결과 :: " + Integer.toString(intRtnVal);
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					//-------------------------------
					// 저장위치 변경 이력 등록
					//-------------------------------


					//-------------------------------
					// L2에 저장품 제원정보 전송 [YDY7L002]
					//-------------------------------
					szMsg = "[ " +szOperationName + "] 야드L2 전문송신 START";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		        	recL2Para = JDTORecordFactory.getInstance().create();
		        	recL2Para.setField("JMS_TC_CD", 		"YDY7L002");                            // TC-CODE
		        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);		// 야드구분
		        	recL2Para.setField("YD_STK_COL_GP", 	szYdStkColGp);                          // 야드적치열구분
		        	recL2Para.setField("YD_STK_BED_NO", 	"01");    								// 야드적치BED번호
		        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
		        	recL2Para.setField("STL_NO", 			szStlNo);	        					// 재료번호
		        	recL2Para.setField("MSG_GP", 			"D");	        						// 전문구분
		        	recL2Para.setField("DEL_YN_CHECK",		"N");									// 삭제된 전문도 조회

		        	szRtnMsg = ydDelegate.sendMsg(recL2Para);

					szMsg = "[ " +szOperationName + "] 야드L2 전문송신 END >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				}

				//-------------------------------
				// 조업 L3에 저장위치변경정보 전송
				//-------------------------------
				szMsg = "[ " +szOperationName + "] 조업L3 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	recL3Para = JDTORecordFactory.getInstance().create();

	        	recL3Para.setField("MSG_ID", 			"YDPPJ011");
	        	recL3Para.setField("YD_STK_COL_FR", 	szYdStkColGp);							// From적치열
	        	recL3Para.setField("YD_STK_BED_FR", 	"01");									// From적치BED
	        	recL3Para.setField("YD_STK_COL_TO", 	"");									// TO적치열
		        recL3Para.setField("YD_STK_BED_TO", 	"");									// TO적치BED
		        recL3Para.setField("YD_EQP_WRK_SH", 	Integer.toString(iTotCh));				// 야드설비작업매수
		        recL3Para.setField("ARR_STL_NO",		szArrStlNo);							// 재료번호 Array - CLEAR시 사용

	        //	szRtnMsg = ydDelegate.sendMsg(recL3Para);
	            szRtnMsg = JPlateYdCommonUtils.sendL3YDPPJ011(recL3Para);

				szMsg = "[ " +szOperationName + "] 조업L3 전문송신 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}

		} catch(Exception e) {
			szMsg = "["+szOperationName+"] Exception Error 발생 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw new DAOException(szMsg);
		} // end of try-catch

		szMsg = "["+szOperationName+"] 이송상차완료 실적처리 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of procYdPcarWrkEnd
	
	
	/**********************************************************
	* 1후판정정추가 SJH16 
	**********************************************************/
	
	
	/** 오퍼레이션명 : 1후판조업 임가공절단장 절단실적수신 (PRYDJ014)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public String procPRRentCutResult(JDTORecord inRecord)throws JDTOException  {
		// DAO 및 UTIL 객체 생성
		JPlateYdStockDAO  ydStockDao 	= new JPlateYdStockDAO();
		JPlateYdStkLyrDAO ydStkLyrDao	= new JPlateYdStkLyrDAO();

		// 레코드 선언
		JDTORecordSet rsGetCutStl   	= null;
		JDTORecordSet rsGetRgntPkCnts	= null;		// 시편채취내용 그릇
		JDTORecordSet rsResult   		= null;
		JDTORecord recPara 		    	= null;
		JDTORecord recMtl 				= null;
		JDTORecord recCnc 		    	= null;
		JDTORecord recL2Para   			= null;

		// 변수선언
		String 	sMethodName 			= "procPRRentCutResult";
		String 	szOperationName 		= "1후판조업 임가공절단장 절단실적수신";
		String 	sMsg 					= "";
		String 	sRcvTcCode				= ydUtils.getTcCode(inRecord);

		String 	sPlMplNo    			= "";		// 후판모재료번호
		String 	sStlNo					= "";		// 후판재료번호
		String 	sYdGp               	= "";
		String	sStlAppearGp			= "";		// 재료외형구분
		String 	sYdStkBedNo	        	= "";		// 야드적치Bed번호
		int 	iFromBedNo	        	= 1;		// 야드적치Bed번호 (변경전 재료의 적재위치)
		String 	sYdStkColGp	        	= "";		// 야드적치열구분
		String 	sYdCurrStrLoc	    	= "";		// 야드현저장위치
		String 	sTemp					= "";
	    String 	sYdMtlWGp          	 	= "";       // 야드재료폭구분		< 4500:S1-소폭 , >=4500:L1-광폭
	    String 	sYdMtlTGp          	 	= "";       // 야드재료두께구분
	    String 	sYdMtlLGp           	= "";       // 야드재료길이구분	< 15000:S1-단척, >=1500:L1-장척
	    String	sYdStkLyrMtlStat		= "";

		float  	fYdMtlW					= 0.0f;		// 야드재료폭
		int    	iYdMtlL					= 0;		// 야드재료길이
		String	szMODIFIER				= "";
		String	sDelYn					= "";
		String  sRgntPkCnts             = "";       // 시편채취내용
		
		String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
		int    	intRtnVal 				= 0;
		int    	iCutCnt					= 0;
		String	arrStlNo[]				= null;
		String	szYdStockExist			= "Y";			// 야드에 재료 미존재시 가스장 절단실적 발생시 처리할수 있도록 보완
		String	szYdSchCall				= "";
		
		String  sBOOK_OUT_SPAN			= "";
		String  sBOOK_OUT_COL			= "";
		
		String  szCARD_NO				= ""; //L3화면을 통해 만들어진 작업예약, 작업지시 설정 **** 

		if (sRcvTcCode == null) {
			sMsg ="[ERROR] " + SZ_SESSION_NAME + "::" + sMethodName + "() TC Code Error (" + sRcvTcCode + ")";
			ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);

		} else {

			try {
			/*
				YD_GP				야드구분
				STL_APPEAR_GP		재료외형구분			(2013.06.10 항목추가)
				PL_MTL_NO    		후판재료번호
				PL_WR_GDS_TOT_SH	후판실적제품총매수
				STL_NO1				재료번호1
				YD_MTL_T1		6.3	야드재료두께1
				YD_MTL_W1		5.1	야드재료폭1
				YD_MTL_L1		7	야드재료길이1
				YD_MTL_WT1		5	야드재료중량1
			*/
			//  재료번호1, 2, 3 --> 3, 2, 1 Bed

				sYdGp 	 		= JPlateYdConst.YD_GP_P_PLATE_YARD;
				sStlAppearGp	= ydDaoUtils.paraRecChkNull(inRecord, 		"STL_APPEAR_GP", "P");
				sPlMplNo 		= ydDaoUtils.paraRecChkNull(inRecord, 		"PL_MPL_NO");
				iCutCnt  		= ydDaoUtils.paraRecChkNullInt(inRecord, 	"PL_WR_GDS_TOT_SH");
				szMODIFIER		= ydDaoUtils.paraRecModifier(inRecord);
				szYdSchCall		= ydDaoUtils.paraRecChkNull(inRecord, 		"YD_SCH_CALL", "N");

				sBOOK_OUT_SPAN	= ydDaoUtils.paraRecChkNull(inRecord, 		"BOOK_OUT_SPAN");
				sBOOK_OUT_COL	= ydDaoUtils.paraRecChkNull(inRecord, 		"BOOK_OUT_COL");
				
				szCARD_NO		= ydDaoUtils.paraRecChkNull(inRecord, 		"CARD_NO");		//L3화면을 통해 만들어진 작업예약, 작업지시 설정 (값이 "L3"이면 야드L3 화면에서 만들어진 지시임)**
		
				//----------------------------------------------------------------------
				// 0. 전문 체크
				//----------------------------------------------------------------------
				if ("".equals(sPlMplNo)) {
					szRtnMsg = "모재료정보 데이타 오류 : " + sPlMplNo;
					sMsg = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
				if (iCutCnt < 1 || iCutCnt > 20) {
					szRtnMsg = "절단매수 데이타 오류 : " + Integer.toString(iCutCnt);
					sMsg = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
				if ("".equals(sStlAppearGp)) {
					szRtnMsg = "재료외형구분 데이타 오류 : " + sStlAppearGp;
					sMsg = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
               
				//----------------------------------------------------------------------
				// 0. PLATE정보 체크 :: 전문 전송하기전에 재료정보를 조회하여 3M 체크함 ...
				//----------------------------------------------------------------------
				sMsg = "["+szOperationName+"] 0. PLATE정보 조회 >>>> " + Integer.toString(iCutCnt);
				ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

				for(int ii=0; ii<iCutCnt; ii++) {

					// sStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"+Integer.toString(ii+1));
					// cut :: 3 , ii :: 0 --> 3 , ii :: 1 --> 2 , ii :: 2 --> 1
					//
					sStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"+Integer.toString(iCutCnt-ii));

					rsGetCutStl = JDTORecordFactory.getInstance().createRecordSet("yd");
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", sStlNo);				// PLATE NO

					//----------------------------------------------------------------------
					// 0.1. PLATE 정보 조회
					//----------------------------------------------------------------------
					intRtnVal = ydStockDao.getGasCutResult(recPara, rsGetCutStl);
					if (intRtnVal != 1) {
						szRtnMsg = "PLATE 정보 미존재 .. 재료번호 : " + sStlNo;
						sMsg = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					recMtl  = rsGetCutStl.getRecord(0);
					iYdMtlL = ydDaoUtils.paraRecChkNullInt(recMtl, 	"YD_MTL_L");

					// 길이가 3미터 미만 재료는 에러처리 
					if (iYdMtlL < 3000) {
						szRtnMsg = "PLATE 재료 길이가 3M 미만 .. 길이 : " + Integer.toString(iYdMtlL);
						sMsg = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					//----------------------------------------------------------------------
					// 0.2. PLATE 저장위치 정보를 조회하여 현위치가 (NULL, 가스장)이 아니면 오류로 처리
					//----------------------------------------------------------------------
					rsGetCutStl = JDTORecordFactory.getInstance().createRecordSet("yd");
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", 	sStlNo);												// PLATE번호
					recPara.setField("YD_GP", 	sYdGp);													// YARD구분
					intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsGetCutStl);
					if (intRtnVal > 0) {
						recMtl	= rsGetCutStl.getRecord(0);
						sYdStkColGp 	 = ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_COL_GP");			// 야드적치열구분
						sYdStkLyrMtlStat = ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_LYR_MTL_STAT");	// 재료적치상태

						// 저장위치 체크
						if (!"".equals(sYdStkColGp) && 
							!"CN".equals(ydUtils.substr(sYdStkColGp, 2, 2))&& 
							!"BC".equals(ydUtils.substr(sYdStkColGp, 2, 2))) {
							szRtnMsg = "PLATE 위치가 임가공절단장(BC)/가스절단장(CN) 이 아님 : " + sYdStkColGp;
							sMsg = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}

						// 적치상태 체크
						if (!"".equals(sYdStkLyrMtlStat) && !"C".equals(sYdStkLyrMtlStat)) {
							szRtnMsg = "PLATE 적치상태 오류 : " + sYdStkLyrMtlStat;
							sMsg = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}
					}
				}

				//----------------------------------------------------------------------
				// 1. 날판 저장위치 조회 - 야드저장품에서 변경전 저장위치 정보를 조회
				//----------------------------------------------------------------------
				sMsg = "["+szOperationName+"] 1. 날판 저장위치 조회 ";
				ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

				rsGetCutStl = JDTORecordFactory.getInstance().createRecordSet("yd");
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO",  sPlMplNo);				// 날판번호
				recPara.setField("YD_GP", 	sYdGp);			
				intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsGetCutStl);
				if (intRtnVal != 1) {
					szRtnMsg = "날판정보 정보 미존재 .. 날판번호 : " + sPlMplNo;
					sMsg = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
					szYdStockExist = "N";
				}

				if ("Y".equals(szYdStockExist)) {
					recMtl       	= rsGetCutStl.getRecord(0);
					sYdStkBedNo   	= ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_BED_NO");		// 야드적치Bed번호
					iFromBedNo 		= ydDaoUtils.paraRecChkNullInt(recMtl, "YD_STK_BED_NO");	// 야드적치Bed번호 (변경전 Bed Index)
					sYdStkColGp   	= ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_COL_GP");		// 야드적치열구분
					sYdCurrStrLoc 	= ydDaoUtils.paraRecChkNull(recMtl, "YD_CURR_STR_LOC");		// 야드현저장위치
					sRgntPkCnts		= ydDaoUtils.paraRecChkNull(recMtl, "RGNT_PK_CNTS"); 		// 시편채취내용

					// 날판이 가스장위치가 아니면 재료만 등록하고 스케쥴 기동 안하도록 요청
					if (!"CN".equals(ydUtils.substr(sYdStkColGp, 2, 2))&&
						!"BC".equals(ydUtils.substr(sYdStkColGp, 2, 2))) {
						szYdStockExist = "N";
						
						//=================================================================================================================
						String sNEW_MODULE_EFF_YN = "N";
						
						JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
						
						sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A033"); //1후판정정야드 전단실적 수신시 가스장외 날판정보 삭제처리 여부
						
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 전단실적 수신시 가스장외 날판정보 삭제처리 여부  : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
						
						if(sNEW_MODULE_EFF_YN.equals("Y")) {
						
							EJBConnector ydEjbCon 	= new EJBConnector("default", this);
							
							if(sPlMplNo.length() > 8) {

								//날판 삭제 
								sMsg = "[ " +szOperationName + "] 가스장 이외에 적치되어 있는 날판 정보 삭제(스케줄포함) : " + ydUtils.substr(sPlMplNo,0,8);
								ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);
								
								inRecord.setField("STL_NO"			, ydUtils.substr(sPlMplNo,0,8)); 
								ydEjbCon.trx("JPlateYdL3RcvFaEJB"	, "rcvPRYDJ017"	, inRecord);

							}
							
							//분할판 삭제
							sMsg = "[ " +szOperationName + "] 가스장 이외에 적치되어 있는 분할판 정보 삭제(스케줄포함) : " + sPlMplNo;
							ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);
							
							inRecord.setField("STL_NO"			, sPlMplNo); 
							ydEjbCon.trx("JPlateYdL3RcvFaEJB"	, "rcvPRYDJ017"	, inRecord);
							
						}
						//=================================================================================================================
					}
				}

				if ("Y".equals(szYdStockExist)) {
					//----------------------------------------------------------------------
					// 2. 날판 정보 CLEAR
					//----------------------------------------------------------------------

					//----------------------------------------------------------------------
					// 2.1. 야드L2 전문송신 (저장품제원 :: YDY2L002 전송)
					//----------------------------------------------------------------------
					sMsg = "[ " +szOperationName + "] 2.1. 야드L2 저장품제원 전문송신 (날판삭제정보) START";
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

		        	recL2Para = JDTORecordFactory.getInstance().create();
		        	recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
		        	recL2Para.setField("YD_GP", 			sYdGp);									// 야드구분
		        	recL2Para.setField("YD_STK_COL_GP", 	sYdStkColGp);                          	// 야드적치열구분
		        	recL2Para.setField("YD_STK_BED_NO", 	sYdStkBedNo);    						// 야드적치BED번호
		        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드  [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
		        	recL2Para.setField("STL_NO", 			sPlMplNo);	        					// 재료번호
		        	recL2Para.setField("MSG_GP", 			"D");	        						// 전문구분 - D:삭제
		        	recL2Para.setField("DEL_YN_CHECK",		"N");									// 삭제된 데이타도 조회하도록 처리

		        	szRtnMsg = ydDelegate.sendMsg(recL2Para);

					sMsg = "[ " +szOperationName + "] >>>> 야드L2 저장품제원 전문송신(날판삭제정보) END >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

					//----------------------------------------------------------------------
					// 2.2. 날판 재료정보 삭제
					//----------------------------------------------------------------------
					sMsg = "["+szOperationName+"] 2.2. 날판 재료정보 삭제";
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("MODIFIER",		szMODIFIER);
					recPara.setField("STL_NO",			sPlMplNo);
					intRtnVal = ydStockDao.delYdStock(recPara);
					if (intRtnVal != 1) {
						szRtnMsg = "날판 재료정보 삭제시 오류발생 : " + Integer.toString(intRtnVal);
						sMsg = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					//----------------------------------------------------------------------
					// 2.3. 날판 저장위치 CLEAR
					//----------------------------------------------------------------------
					sMsg = "["+szOperationName+"] 2.3. 날판 저장위치 CLEAR";
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("MODIFIER",			szMODIFIER);
					recPara.setField("YD_STK_COL_GP",		sYdStkColGp);
					recPara.setField("YD_STK_BED_NO",		sYdStkBedNo);
					recPara.setField("STL_NO",				sPlMplNo);
					recPara.setField("YD_GP",				sYdGp);

					intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
					if (intRtnVal < 1) {
						szRtnMsg = "날판 저장위치 CLEAR 오류발생 : " + Integer.toString(intRtnVal);
						sMsg = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}

				arrStlNo = new String[iCutCnt];

				//----------------------------------------------------------------------
				// 3. PLATE정보 생성
				//----------------------------------------------------------------------
				sMsg = "["+szOperationName+"] 3. PLATE정보 생성 >>>> " + Integer.toString(iCutCnt);
				ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

				for(int ii=0; ii<iCutCnt; ii++) {

					// sStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"+Integer.toString(ii+1));
					// cut :: 3 , ii :: 0 --> 3 , ii :: 1 --> 2 , ii :: 2 --> 1
					//
					sStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"+Integer.toString(iCutCnt-ii));
					arrStlNo[ii] = sStlNo;

					rsGetCutStl = JDTORecordFactory.getInstance().createRecordSet("yd");
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", sStlNo);				// PLATE NO

					//----------------------------------------------------------------------
					// 3.1. PLATE 정보 조회
					//----------------------------------------------------------------------
					intRtnVal = ydStockDao.getGasCutResult(recPara, rsGetCutStl);
					if (intRtnVal != 1) {
						szRtnMsg = "PLATE 정보 미존재 .. 재료번호 : " + sStlNo;
						sMsg = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					recMtl      	= rsGetCutStl.getRecord(0);
					sTemp  			= ydDaoUtils.paraRecChkNull(recMtl,		"YD_MTL_W");
					fYdMtlW 		= ydUtils.strToFloat(sTemp, 5, 1);
					iYdMtlL  		= ydDaoUtils.paraRecChkNullInt(recMtl, 	"YD_MTL_L");
					sDelYn			= ydDaoUtils.paraRecChkNull(recMtl, 	"DEL_YN");

					sMsg = "["+szOperationName+"] 임가공절단장 절단실적 수신후 .... 조업테이블 조회 결과 >>>> " + "두께 ::"  + ydDaoUtils.paraRecChkNull(recMtl, "YD_MTL_T")
					     + ", 폭::"  + ydDaoUtils.paraRecChkNull(recMtl,	"YD_MTL_W") + ", 길이::"	+ ydDaoUtils.paraRecChkNull(recMtl,	"YD_MTL_L");
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);

					// BRE RULE 적용
					JDTORecord jdtoRcd = JDTORecordFactory.getInstance().create();
			    	boolean bRtnVal = GetBreRule8.getYDB801(sYdGp, fYdMtlW, jdtoRcd);
			    	if (bRtnVal) {
			    		sYdMtlWGp = ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_MTL_W_GP");
			    	} else {
			    		sYdMtlWGp = "";
			    	}
					jdtoRcd = JDTORecordFactory.getInstance().create();
			    	bRtnVal = GetBreRule8.getYDB802(sYdGp, iYdMtlL, jdtoRcd);
			    	if (bRtnVal) {
			    		sYdMtlLGp = ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_MTL_L_GP");
			    	} else {
			    		sYdMtlLGp = "";
			    	}

					if ("Y".equals(szYdStockExist)) {
						sYdStkBedNo   = ydUtils.addLeftStr(Integer.toString(ii+iFromBedNo), 2, '0');	// 야드적치Bed번호
						sYdCurrStrLoc = sYdStkColGp + sYdStkBedNo;										// 야드현저장위치
					} else {
						sYdStkColGp   = "";
						sYdStkBedNo   = "";
						sYdCurrStrLoc = "";
					}

					// 가스장에 이미 재료가 존재시 다음 적치위치 조회
					rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
					recCnc   = JDTORecordFactory.getInstance().create();
					recPara  = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_STK_COL_GP", 	sYdStkColGp);
					recPara.setField("YD_STK_BED_NO", 	sYdStkBedNo);
					recPara.setField("YD_GP", 			sYdGp);

					sMsg = "["+szOperationName+"] 임가공절단장 적치 가능 위치 BEFORE >>>> " + sYdStkColGp + sYdStkBedNo;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);
					
					if ("CN".equals(ydUtils.substr(sYdStkColGp, 2, 2))){
						
						intRtnVal = ydStkLyrDao.getEmptyToCnc(recPara, rsResult);
						
					}else{

						// "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getEmptyToBc 
						intRtnVal = ydStkLyrDao.getEmptyToBc(recPara, rsResult);
					}
					
					if (intRtnVal <= 0) {
						szRtnMsg = "임가공절단장 저장위치 조회 오류 : " + Integer.toString(intRtnVal);
						sMsg = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
					rsResult.first();
					recCnc = rsResult.getRecord();
					sYdStkColGp = ydDaoUtils.paraRecChkNull(recCnc, "YD_STK_COL_GP");
					sYdStkBedNo = ydDaoUtils.paraRecChkNull(recCnc, "YD_STK_BED_NO");

					sMsg = "["+szOperationName+"] 임가공절단장 적치 가능 위치 AFTER >>>> " + sYdStkColGp + sYdStkBedNo;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

					//----------------------------------------------------------------------
					// 3.1.1  파라미터 셋팅
					//----------------------------------------------------------------------						
					recMtl.setField("YD_GP", 				sYdGp);
					recMtl.setField("STL_NO", 		 		sStlNo);
					recMtl.setField("YD_STK_COL_GP",		sYdStkColGp);							// 야드적치열구분
					recMtl.setField("YD_STK_BED_NO",		sYdStkBedNo);							// 야드적치Bed번호
					recMtl.setField("YD_CURR_STR_LOC", 		sYdCurrStrLoc);							// 야드현저장위치
					recMtl.setField("YD_MTL_W_GP", 	 		sYdMtlWGp);       						// 야드재료폭구분
					recMtl.setField("YD_MTL_T_GP", 	 		sYdMtlTGp);       						// 야드재료두께구분
					recMtl.setField("YD_MTL_L_GP", 	 		sYdMtlLGp);       						// 야드재료길이구분
					recMtl.setField("REGISTER",				szMODIFIER);
					recMtl.setField("MODIFIER",				szMODIFIER);
					recMtl.setField("DEL_YN",				"N");
					recMtl.setField("YD_STK_LYR_NO",		"001");									// 적치단
					recMtl.setField("YD_STK_LYR_MTL_STAT", 	"C");									// 재료적치상태
					recMtl.setField("RGNT_PK_CNTS", 		sRgntPkCnts);							// 시편채취내용

					sMsg = "["+szOperationName+"] 임가공절단장 절단실적 등록데이터 :: "+ sStlNo;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

					//----------------------------------------------------------------------
					// 3.2. 정정야드 저장품 정보 등록
					//----------------------------------------------------------------------
					if ("".equals(sDelYn)) {
						// 정정야드저장품 Insert
						intRtnVal = ydStockDao.insYdStockCutResult(recMtl);
					} else {
						// 정정야드저장품 Update
						intRtnVal = ydStockDao.updYdStockCutResult(recMtl);
					}
					if (intRtnVal != 1) {
						szRtnMsg = "임가공절단장 절단실적 등록 오류 .. 재료번호 : " + sStlNo;
						sMsg = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					if ("Y".equals(szYdStockExist)) {
						//----------------------------------------------------------------------
						// 3.3. 적치단 정보 등록
						//----------------------------------------------------------------------
						// 야드맵 업데이트 : Null일때는 Skip
						if (!"".equals(sYdStkBedNo) && !"".equals(sYdStkColGp)) {

							intRtnVal = ydStkLyrDao.updYdStklyrStat(recMtl);
							if (intRtnVal != 1) {
								szRtnMsg = "임가공절단장 절단실적 적치단 등록 오류 .. 재료번호 : " + sStlNo;
								sMsg = "["+szOperationName+"] " + szRtnMsg;
								ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
								return szRtnMsg;
							}
						}
					} else {
						// 날판 저장위치 미존재시는 저장위치를 등록하지 않고 추후 수작업으로 저장위치를 등록하여야함 (윤재광과장님 요청사항)
						sMsg =  "["+szOperationName+"] 날판 저장위치 미존재하여 절단 재료 적치단 등록 SKIP";
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);
					}

					//----------------------------------------------------------------------
					// 3.4. 야드L2 전문송신 (저장품제원 :: YDY2L002 전송)
					//----------------------------------------------------------------------
					sMsg = "[ " +szOperationName + "] 야드L2 저장품제원 전문송신 START";
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

		        	recL2Para = JDTORecordFactory.getInstance().create();
		        	recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
		        	recL2Para.setField("YD_GP", 			sYdGp);		// 야드구분
		        	recL2Para.setField("YD_STK_COL_GP", 	sYdStkColGp);                           // 야드적치열구분
		        	recL2Para.setField("YD_STK_BED_NO", 	sYdStkBedNo);    						// 야드적치BED번호
		        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
		        	recL2Para.setField("STL_NO", 			sStlNo);	        					// 재료번호
		        	szRtnMsg = ydDelegate.sendMsg(recL2Para);

					sMsg = "[ " +szOperationName + "] 야드L2 저장품제원 전문송신 END >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);
				}

				// 가스장 추출 스케쥴을 검사실적 : 차행선결정정보 수신 (PPYDJ015) 수신후 처리하도록 보완
				// 2013.09.03 화면에서 북아웃요청시 스케쥴 기동하도록 보완
				if ("Y".equals(szYdStockExist) && "Y".equals(szYdSchCall)) {
					//----------------------------------------------------------------------
					// 4. GAS장 추출 스케줄 기동
					//----------------------------------------------------------------------
					sMsg = "["+szOperationName+"] 4. 임가공절단장 장 추출 스케줄 기동";
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

					EJBConnector ejbConn 	= new EJBConnector("default", this);

					String sYD_BAY_GP 		= sYdStkColGp.substring(1, 2);
					String sYD_SPAN_GP 		= sYdStkColGp.substring(2, 4);
					String sYD_AIM_YD_GP	= sYdGp;			// 목표야드구분
					String sYD_AIM_BAY_GP	= sYD_BAY_GP;								// 목표동구분
					String sYD_AIM_SPAN_GP	= sBOOK_OUT_SPAN;										// 목표스판구분
					String sYD_AIM_COL_GP	= sBOOK_OUT_COL;										// 목표적치열구분
					String sYD_AIM_BED_NO	= "";										// 목표적치BED구분

					for(int ii=0; ii<iCutCnt; ii++) {

						recPara = JDTORecordFactory.getInstance().create();

						recPara.setField("YD_GP",				sYdGp);
						recPara.setField("YD_BAY_GP", 	      	sYD_BAY_GP);
						recPara.setField("YD_SPAN_GP", 	      	sYD_SPAN_GP);
						// 주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차, E:임가공보급 ,F: 임가공추출
						//recPara.setField("YD_MAIN_WRK_GP", 	  	JPlateYdConst.YD_MAIN_WRK_GP_BC_OUT);
						recPara.setField("YD_MAIN_WRK_GP", 	  	JPlateYdConst.YD_MAIN_WRK_GP_GAS_OUT);
						recPara.setField("YD_TO_LOC_GUIDE_GP",	"Y");
						recPara.setField("YD_AIM_YD_GP", 	  	sYD_AIM_YD_GP);			// 목표야드구분
						recPara.setField("YD_AIM_BAY_GP", 	  	sYD_AIM_BAY_GP);		// 목표동구분
						recPara.setField("YD_AIM_SPAN_GP", 	  	sYD_AIM_SPAN_GP);		// 목표스판구분
						recPara.setField("YD_AIM_COL_GP", 	  	sYD_AIM_COL_GP);		// 목표적치열구분
						recPara.setField("YD_AIM_BED_NO", 	  	sYD_AIM_BED_NO);		// 목표적치BED구분

						//---------------------------------------------------------------------------------------------
						//	후판정정의 준비스케줄 등록 시 지정한 작업매수의 값을 전달하여 해당하는 매수만큼 작업예약에 등록
						//---------------------------------------------------------------------------------------------
						recPara.setField("YD_EQP_WRK_SH", 	  	"1");
						recPara.setField("ARR_WLOC_CD", 	    "");

						//---------------------------------------------------------------------------------------------
						//	작업재료리스트와 JMS_TC_CD
						//---------------------------------------------------------------------------------------------
						recPara.setField("STL_LIST", 	        arrStlNo[ii]);
						recPara.setField("JMS_TC_CD", 	        sRcvTcCode);
						recPara.setField("YD_USER_ID", 	        szMODIFIER);
						
						//---------------------------------------------------------------------------------------------
						//	L3화면을 통해 만들어진 작업예약, 작업지시 설정
						//---------------------------------------------------------------------------------------------
						recPara.setField("CARD_NO",	szCARD_NO);		
						

						sMsg = "["+szOperationName+"] 임가공절단장 추출 스케줄 기동 .. START";
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

						//내부 Process 연결
						szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapaYdP", recPara);

						sMsg =  "["+szOperationName+"] 임가공절단장 추출 스케줄 기동 .. END";
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

						if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
							sMsg =  "["+szOperationName+"] 임가공절단장 추출 스케줄 기동 .. 결과 :: "+ szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}
					}
				} else {
					sMsg =  "["+szOperationName+"] 임가공절단장 추출 스케쥴 SKIP >>>> 재료존재 :: " + szYdStockExist + ", 스케쥴기동 :: " + szYdSchCall;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);
				}


			} catch(Exception e) {
				sMsg = "[1후판정정야드 임가공절단장 절단실적] Exception Error 발생 ";
				ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
				throw new DAOException("[1후판정정야드 임가공절단장절단실적]" + sMsg);
			} // end of try-catch
		}

		ydUtils.putLog(SZ_SESSION_NAME, sMethodName, "[1후판정정야드 임가공절단장절단실적] 처리("+sMethodName+") 완료", JPlateYdConst.DEBUG);
		return JPlateYdConst.RETN_CD_SUCCESS;

	} // end of procPRRentCutResult


	/**
	 * 오퍼레이션명 : 1후판조업 차행선결정정보 수신 (PRYDJ015)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public String procPRNextDeciInfo(JDTORecord inRecord)throws JDTOException  {
		// DAO 및 UTIL 객체 생성
		JPlateYdStockDAO 	ydStockDao	= new JPlateYdStockDAO();
		JPlateYdStkLyrDAO	ydStkLyrDAO	= new JPlateYdStkLyrDAO();

		// 레코드 선언
		JDTORecordSet rsYdStock     = null;
		JDTORecord recPara 		    = null;
		JDTORecord recMtl 			= null;

		// 변수선언
		String	szMethodName 		= "procPRNextDeciInfo";
		String	szOperationName 	= "1후판조업 차행선결정정보 수신";
		String	szRtnMsg			= "";
		String 	szMsg 				= "";
		String 	szRcvTcCode			= ydUtils.getTcCode(inRecord);

		String 	szStlNo				= "";		// 재료번호
		String 	szUsMaintmatl		= "";		// 상면보수재
		String 	szLsMaintmatl		= "";		// 하면보수재
		String 	szCplWrkMtl			= "";		// 냉간교정재
		String  szScplWrkMtl		= "";		// 강력교정재**
		String 	szHttrtHplMtl		= "";		// 열처리교정재
		String 	szGasWrkMtl			= "";		// GAS작업재
		String 	szShotBlstWrkMtl	= "";		// ShortBlast작업재
		String 	szPressWrkMtl		= "";		// 프레스교정재
		String 	szPlWrPrsntProcCd	= "";		// 후판실적현공정코드
		String	szModifier			= "";
		
		String  szQaAsgnMtl			= "";		// QA지정재 ***
		String  szUtWaitMtl			= "";		// UT대기재 ***

		String	szYdStkColGp		= "";
		String	szYdStkBedNo 		= "";
		String	szYdStkLyrNo 		= "";
		String  szYdBayGp 			= "";
		String	szYdSpanGp 			= "";
		String	szYdAimYdGp			= "";			// 목표야드구분
		String	szYdAimBayGp		= "";			// 목표동구분
		String	szYdAimSpanGp		= "";			// 목표스판구분
		String	szYdAimColGp		= "";			// 목표적치열구분
		String	szYdAimBedNo		= "";			// 목표적치BED구분
		String	szYdMtlL			= "";
		String	szPlMeaGdsL			= "";
		String	szYdStkLyrMtlStat	= "";
		String	szGdsMainGrd		= "";

		String	szMtlStatCd			= "";			// 재료진행상태 : 종료된 재료는 스케줄 기동 안하도록 체크

		int    	intRtnVal 			= 0;

		String  sBOOK_OUT_SPAN		= ""; //화면에서 입력한 TO위치 가이드 Span
		String  sBOOK_OUT_COL		= ""; //화면에서 입력한 TO위치 가이드 열 
		
		String  sWR_ELE_PROC_CD     = ""; //후판실적전공정코드 (1P:냉간교정) ***
		
		String  szCARD_NO			= ""; //L3화면을 통해 만들어진 작업예약, 작업지시 설정 **** 
		
		String  szNoCrnSchYN		= ""; //Y:크레인 스케줄 생성 안함 ***
		
		try {
			if (szRcvTcCode == null) {
				szMsg ="[ERROR] " + SZ_SESSION_NAME + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

			} else {

			/*
				STL_NO				재료번호			CHAR	11	Y	후판Plate번호 또는 후판날판번호
				US_MAINTMATL		상면보수재		CHAR	1	0	해당작업일경우 "Y"
				LS_MAINTMATL		하면보수재		CHAR	1	0	해당작업일경우 "Y"
				CPL_WRK_MTL			냉간교정재		CHAR	1	0	해당작업일경우 "Y"
				SCPL_WRK_MTL		강력교정재		CHAR	1	0	해당작업일경우 "Y"
				HTTRT_HPL_MTL		열처리교정재		CHAR	1	0	해당작업일경우 "Y"
				GAS_WRK_MTL			GAS작업재			CHAR	1	0	해당작업일경우 "Y"
				SHOT_BLST_WRK_MTL	ShortBlast작업재	CHAR	1	0	해당작업일경우 "Y"
				PRESS_WRK_MTL		프레스교정재		CHAR	1	0	해당작업일경우 "Y"
				PL_WR_PRSNT_PROC_CD	후판실적현공정코드	CHAR	2	0	2O:ON-LINE 입고,2N:OFF-LINE입고
				GDS_MAIN_GRD		제품주등급		CHAR	1	0	6:스크랩, 7:충당대기
			*/

				szStlNo				= ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");				// 재료번호
				szUsMaintmatl		= ydDaoUtils.paraRecChkNull(inRecord, "US_MAINTMATL");			// 상면보수재
				szLsMaintmatl		= ydDaoUtils.paraRecChkNull(inRecord, "LS_MAINTMATL");			// 하면보수재
				szCplWrkMtl			= ydDaoUtils.paraRecChkNull(inRecord, "CPL_WRK_MTL");			// 냉간교정재
				szScplWrkMtl		= ydDaoUtils.paraRecChkNull(inRecord, "SCPL_WRK_MTL");			// 강력교정재 **
				szHttrtHplMtl		= ydDaoUtils.paraRecChkNull(inRecord, "HTTRT_HPL_MTL");			// 열처리교정재
				szGasWrkMtl			= ydDaoUtils.paraRecChkNull(inRecord, "GAS_WRK_MTL");			// GAS작업재
				szShotBlstWrkMtl	= ydDaoUtils.paraRecChkNull(inRecord, "SHOT_BLST_WRK_MTL");		// ShortBlast작업재
				szPressWrkMtl		= ydDaoUtils.paraRecChkNull(inRecord, "PRESS_WRK_MTL");			// 프레스교정재
				szPlWrPrsntProcCd	= ydDaoUtils.paraRecChkNull(inRecord, "PL_WR_PRSNT_PROC_CD");	// 후판실적현공정코드
				szGdsMainGrd		= ydDaoUtils.paraRecChkNull(inRecord, "GDS_MAIN_GRD");			// 제품주등급
				szModifier			= ydDaoUtils.paraRecModifier(inRecord);							// 수정자
				
				sBOOK_OUT_SPAN		= ydDaoUtils.paraRecChkNull(inRecord, "BOOK_OUT_SPAN");			//화면에서 입력한 TO위치 가이드 Span
				sBOOK_OUT_COL		= ydDaoUtils.paraRecChkNull(inRecord, "BOOK_OUT_COL");			//화면에서 입력한 TO위치 가이드 열 
				
				sWR_ELE_PROC_CD		= ydDaoUtils.paraRecChkNull(inRecord, "PL_WR_ELE_PROC_CD");		//후판실적전공정코드**
				
				szCARD_NO			= ydDaoUtils.paraRecChkNull(inRecord, "CARD_NO");		//L3화면을 통해 만들어진 작업예약, 작업지시 설정 (값이 "L3"이면 야드L3 화면에서 만들어진 지시임)**

				szQaAsgnMtl			= ydDaoUtils.paraRecChkNull(inRecord, "QA_ASGN_MTL");			// QA지정재 ***
				szUtWaitMtl			= ydDaoUtils.paraRecChkNull(inRecord, "UT_WAIT_MTL");			// UT대기재 ***
				
				szNoCrnSchYN		= ydDaoUtils.paraRecChkNull(inRecord, "NO_CRN_SCH_YN","N");		// Y:크레인 스케줄 생성 안함 *** 
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 	szStlNo);						// 재료번호
				recPara.setField("YD_GP", 		JPlateYdConst.YD_GP_P_PLATE_YARD);

				rsYdStock = JDTORecordFactory.getInstance().createRecordSet("yd");
				/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStockWithLoc 
				-- 2후판정정야드재료 정보 조회

				SELECT /*+ opt_param('_disable_function_based_index','true') 
				       A.STL_NO                         AS STL_NO                   -- 재료번호
				     , A.REGISTER                       AS REGISTER                 -- 등록자
				     , TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT          -- 등록일시
				     , A.MODIFIER                       AS MODIFIER                 -- 수정자
				     , TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT          -- 수정일시
				     , A.DEL_YN                         AS DEL_YN                   -- 삭제유무
				     , A.YD_WBOOK_ID                    AS YD_WBOOK_ID              -- 야드작업예약ID
				     , A.YD_SCH_CD                      AS YD_SCH_CD                -- 야드스케쥴코드
				     , A.PTOP_PLNT_GP                   AS PTOP_PLNT_GP             -- 조업공장구분
				     , A.YD_MTL_ITEM                    AS YD_MTL_ITEM              -- 야드재료품목
				     , A.ITEMNAME_CD                    AS ITEMNAME_CD              -- 품명코드
				     , A.YD_MTL_STAT                    AS YD_MTL_STAT              -- 야드재료상태
				     , A.STL_PROG_CD                    AS STL_PROG_CD              -- 재료진도코드
				     , A.ORD_YEOJAE_GP                  AS ORD_YEOJAE_GP            -- 주문여재구분
				     , A.FRTOMOVE_ORD_DATE              AS FRTOMOVE_ORD_DATE        -- 이송지시일자
				     , A.FRTOMOVE_PLANT_GP              AS FRTOMOVE_PLANT_GP        -- 이송공장구분
				     , A.STL_APPEAR_GP                  AS STL_APPEAR_GP            -- 재료외형구분
				     , A.PLNT_PROC_CD                   AS PLNT_PROC_CD             -- 공장공정코드
				     , A.YD_MTL_T                       AS YD_MTL_T                 -- 야드재료두께
				     , A.YD_MTL_W                       AS YD_MTL_W                 -- 야드재료폭
				     , A.YD_MTL_L                       AS YD_MTL_L                 -- 야드재료길이
				     , A.YD_MTL_WT                      AS YD_MTL_WT                -- 야드재료중량
				     , A.YD_MTL_W_GP                    AS YD_MTL_W_GP              -- 야드재료폭구분
				     , A.YD_MTL_T_GP                    AS YD_MTL_T_GP              -- 야드재료두께구분
				     , A.YD_MTL_L_GP                    AS YD_MTL_L_GP              -- 야드재료길이구분
				     , A.COOL_DONE_GP                   AS COOL_DONE_GP             -- 냉각완료구분
				     , A.REHEAT_SLAB_GP                 AS REHEAT_SLAB_GP           -- 재열재구분
				     , A.TRANS_ORD_DATE                 AS TRANS_ORD_DATE           -- 운송지시일자
				     , A.TRANS_ORD_SEQNO                AS TRANS_ORD_SEQNO          -- 운송지시순번
				     , A.CAR_NO                         AS CAR_NO                   -- 차량번호
				     , A.CARD_NO                        AS CARD_NO                  -- 카드번호
				     , A.ARR_WLOC_CD                    AS ARR_WLOC_CD              -- 착지개소코드
				     , A.YD_FRTOMOVE_YD_GP              AS YD_FRTOMOVE_YD_GP        -- 야드이송야드구분
				     , A.YD_FRTOMOVE_BAY_GP             AS YD_FRTOMOVE_BAY_GP       -- 야드이송동구분
				     , A.URGENT_FRTOMOVE_WORD_GP        AS URGENT_FRTOMOVE_WORD_GP  -- 긴급이송작업지시구분
				     , A.YD_FTMV_MEANS_GP               AS YD_FTMV_MEANS_GP         -- 야드이송수단구분
				     , A.MMATL_FEE_NO                   AS MMATL_FEE_NO             -- 모재료번호
				     , A.YD_WRK_PLAN_CRN                AS YD_WRK_PLAN_CRN          -- 야드작업계획크레인
				     , A.YD_WRK_PLAN_TCAR               AS YD_WRK_PLAN_TCAR         -- 야드작업계획대차
				     , A.YD_CAR_UPP_LOC_CD              AS YD_CAR_UPP_LOC_CD        -- 야드차상위치코드
				     , A.YD_CURR_STR_LOC                AS YD_CURR_STR_LOC          -- 야드현저장위치
				     , A.YD_RCPT_DATE                   AS YD_RCPT_DATE             -- 야드입고일자
				     , A.SNDBK_RSN_CD                   AS SNDBK_RSN_CD             -- 반송원인코드
				     , A.SNDBK_GP                       AS SNDBK_GP                 -- 반송요청구분
				     , A.SNDBK_REGISTER                 AS SNDBK_REGISTER           -- 반송요청자
				     , TO_CHAR(A.SNDBK_REG_DDTT, 'YYYYMMDDHH24MISS') AS SNDBK_REG_DDTT              -- 반송요청일자
				     , A.CAR_LOTID                      AS CAR_LOTID                                -- 차량LotID
				     , TO_CHAR(A.CAR_LOTID_REG_DDTT, 'YYYYMMDDHH24MISS') AS CAR_LOTID_REG_DDTT      -- 차량LotID등록일자
				     , A.DETAIL_ARR_CD                  AS DETAIL_ARR_CD                            -- 상세착지코드
				     , A.YD_FTMV_WRK_CMPL_GP            AS YD_FTMV_WRK_CMPL_GP                      -- 야드이송작업완료구분
				     , TO_CHAR(A.YD_FTMV_WRK_CMPL_DD, 'YYYYMMDDHH24MISS') AS YD_FTMV_WRK_CMPL_DD    -- 야드이송작업완료일자
				     , A.BOOK_OUT_RESN                  AS BOOK_OUT_RESN            -- Book-Out원인
				     , A.BOOK_OUT_DATE                  AS BOOK_OUT_DATE            -- Book-Out일자
				     , A.BOOK_OUT_PROG                  AS BOOK_OUT_PROG            -- Book-Out공정
				     , A.US_MAINTMATL                   AS US_MAINTMATL             -- 상면보수재
				     , A.US_MAINT_SCH_MAKE_YN           AS US_MAINT_SCH_MAKE_YN     -- 상면보수스케줄작성여부
				     , A.US_MAINT_WRK_CMPL_YN           AS US_MAINT_WRK_CMPL_YN     -- 상면보수작업완료여부
				     , A.LS_MAINTMATL                   AS LS_MAINTMATL             -- 하면보수재
				     , A.LS_MAINT_SCH_MAKE_YN           AS LS_MAINT_SCH_MAKE_YN     -- 하면보수스케줄작성여부
				     , A.LS_MAINT_WRK_CMPL_YN           AS LS_MAINT_WRK_CMPL_YN     -- 하면보수작업완료여부
				     , A.CPL_WRK_MTL                    AS CPL_WRK_MTL              -- 냉간교정재
				     , A.CR_CORR_SCH_MAKE_YN            AS CR_CORR_SCH_MAKE_YN      -- 냉간교정스케줄작성여부
				     , A.CR_CORR_WRK_CMPL_YN            AS CR_CORR_WRK_CMPL_YN      -- 냉간교정작업완료여부
				     , A.HTTRT_HPL_MTL                  AS HTTRT_HPL_MTL            -- 열처리교정재
				     , A.HTTRT_CORR_SCH_MAKE_YN         AS HTTRT_CORR_SCH_MAKE_YN   -- 열처리교정스케줄작성여부
				     , A.HTTRT_CORR_WRK_CMPL_YN         AS HTTRT_CORR_WRK_CMPL_YN   -- 열처리교정작업완료여부
				     , A.GAS_WRK_MTL                    AS GAS_WRK_MTL              -- GAS작업재
				     , A.GAS_WRK_SCH_MAKE_YN            AS GAS_WRK_SCH_MAKE_YN      -- Gas작업스케줄작성여부
				     , A.GAS_WRK_WRK_CMPL_YN            AS GAS_WRK_WRK_CMPL_YN      -- Gas작업작업완료여부
				     , A.SHOT_BLST_WRK_MTL              AS SHOT_BLST_WRK_MTL        -- ShortBlast작업재
				     , A.S_BLST_WRK_SCH_MAKE_YN         AS S_BLST_WRK_SCH_MAKE_YN   -- ShortBlast작업스케줄작성여부
				     , A.S_BLST_WRK_WRK_CMPL_YN         AS S_BLST_WRK_WRK_CMPL_YN   -- ShortBlast작업작업완료여부
				     , A.PRESS_WRK_MTL                  AS PRESS_WRK_MTL            -- 프레스교정재
				     , A.PRS_CORR_SCH_MAKE_YN           AS PRS_CORR_SCH_MAKE_YN     -- Press교정스케줄작성여부
				     , A.PRS_CORR_WRK_CMPL_YN           AS PRS_CORR_WRK_CMPL_YN     -- Press교정작업완료여부
				     , A.PL_WR_PRSNT_PROC_CD            AS PL_WR_PRSNT_PROC_CD      -- 후판실적현공정코드
				     -- 저장위치(야드적치단) 정보
				     , B.YD_STK_COL_GP                  AS YD_STK_COL_GP            -- 야드적치열구분
				     , B.YD_STK_BED_NO                  AS YD_STK_BED_NO            -- 야드적치Bed번호
				     , B.YD_STK_LYR_NO                  AS YD_STK_LYR_NO            -- 야드적치단번호
				     , B.YD_STK_LYR_ACT_STAT            AS YD_STK_LYR_ACT_STAT      -- 야드적치단활성상태
				     , B.YD_STK_LYR_MTL_STAT            AS YD_STK_LYR_MTL_STAT      -- 야드적치단재료상태
				     , B.YD_OCPY_BED_GP                 AS YD_OCPY_BED_GP           -- 야드점유구분
				     , B.YD_OCPY_STK_BED_NO             AS YD_OCPY_STK_BED_NO       -- 야드점유적치Bed번호
				     , B.YD_OCPY_STK_LYR_NO             AS YD_OCPY_STK_LYR_NO       -- 야드점유적치단번호
				     -- 보수장 적치필요 베드 정보
				     , NVL(CEIL(A.YD_MTL_W/1000), 1)    AS BS_COL_CNT               -- 재료폭별 적치필요 열수
				     , NVL(CEIL(A.YD_MTL_L/2000), 1)    AS BS_BED_CNT               -- 재료길이별 적치필요 베드수

				     , C.CURR_PROG_CD                   AS CURR_PROG_CD             -- 현재진도코드
				     , C.MTL_STAT_CD                    AS MTL_STAT_CD              -- 재료상태코드
				     , C.PL_MEA_GDS_T                   AS PL_MEA_GDS_T             -- 제촌두께
				     , C.PL_MEA_GDS_W                   AS PL_MEA_GDS_W             -- 제촌폭
				     , C.PL_MEA_GDS_L                   AS PL_MEA_GDS_L             -- 제촌길이
				     , C.PL_MEA_GDS_WT                  AS PL_MEA_GDS_WT            -- 제촌중량

				  FROM TB_YD_SHRSTOCK  A
				     , (SELECT
				               X.YD_STK_COL_GP
				             , X.YD_STK_BED_NO
				             , X.YD_STK_LYR_NO
				             , X.STL_NO
				             , X.YD_STK_LYR_ACT_STAT
				             , X.YD_STK_LYR_MTL_STAT
				             , X.YD_OCPY_BED_GP
				             , X.YD_OCPY_STK_BED_NO
				             , X.YD_OCPY_STK_LYR_NO
				             , DECODE(X.YD_STK_LYR_MTL_STAT, 'U','C', X.YD_STK_LYR_MTL_STAT) AS MTL_STAT
				          FROM TB_YD_STKLYR X
				         WHERE X.STL_NO = :V_STL_NO
				           AND X.YD_STK_COL_GP LIKE NVL(:V_YD_GP, 'F') || '%'
				           AND X.DEL_YN = 'N'
				           AND X.YD_STK_LYR_MTL_STAT IN ('C','U')
				       ) B
				     , VW_YD_SHRSTOCK C
				 WHERE A.STL_NO      = :V_STL_NO
				   AND A.STL_NO      = B.STL_NO(+)
				   AND A.STL_NO      = C.STL_NO(+)
				   AND A.DEL_YN      = 'N'
				   AND B.MTL_STAT(+) = 'C'
				   AND ROWNUM        =  1
				  */ 
				intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsYdStock);

				if (intRtnVal != 1) {
					// 재료 미존재시는 Insert
					// ydStockDao.insYdStockCutResult(recPara2);

					szRtnMsg = "재료정보 미존재 :: " + szStlNo;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					//throw new DAOException(szRtnMsg);
					return szRtnMsg;

				} else {

					recMtl = rsYdStock.getRecord(0);

					szYdStkColGp 		= ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_COL_GP");//NULL 박종호
					szYdStkBedNo 		= ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_BED_NO"); //NULL 박종호
					szYdStkLyrNo 		= ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_LYR_NO"); //NULL 박종호
					szMtlStatCd			= ydDaoUtils.paraRecChkNull(recMtl, "MTL_STAT_CD"); //2 박종호
					szYdMtlL			= ydDaoUtils.paraRecChkNull(recMtl, "YD_MTL_L");  //11005 박종호
					szYdStkLyrMtlStat 	= ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_LYR_MTL_STAT");  //NULL 박종호
					szPlMeaGdsL			= ydDaoUtils.paraRecChkNull(recMtl, "PL_MEA_GDS_L");      // 제촌길이  11005 박종호

					// 재료 존재시는 Update
					recMtl.setField("DEL_YN", 					"N");
					recMtl.setField("STL_NO", 					szStlNo);  //FC71251601 박종호
					recMtl.setField("MODIFIER", 				szModifier);

					recMtl.setField("US_MAINTMATL", 			"N");			// 상면보수재
					recMtl.setField("US_MAINT_SCH_MAKE_YN", 	"N");			// 상면보수스케줄작성여부
					recMtl.setField("US_MAINT_WRK_CMPL_YN", 	"N");			// 상면보수작업완료여부
					recMtl.setField("LS_MAINTMATL", 			"N");			// 하면보수재
					recMtl.setField("LS_MAINT_SCH_MAKE_YN", 	"N");			// 하면보수스케줄작성여부
					recMtl.setField("LS_MAINT_WRK_CMPL_YN",		"N");			// 하면보수작업완료여부
					recMtl.setField("CPL_WRK_MTL", 				"N");			// 냉간교정재
					recMtl.setField("CR_CORR_SCH_MAKE_YN", 		"N");			// 냉간교정스케줄작성여부
					recMtl.setField("CR_CORR_WRK_CMPL_YN", 		"N");			// 냉간교정작업완료여부
					recMtl.setField("SCPL_WRK_MTL",				"N");			// 강력교정재**
					recMtl.setField("SCPL_WRK_SCH_MAKE_YN",		"N");			// 강력교정스케줄작성여부**
					recMtl.setField("SCPL_WRK_CMPL_YN",			"N");			// 강력교정작업완료여부**
					recMtl.setField("HTTRT_HPL_MTL", 			"N");			// 열처리교정재
					recMtl.setField("HTTRT_CORR_SCH_MAKE_YN", 	"N");			// 열처리교정스케줄작성여부
					recMtl.setField("HTTRT_CORR_WRK_CMPL_YN", 	"N");			// 열처리교정작업완료여부
					recMtl.setField("GAS_WRK_MTL", 				"N");			// GAS작업재
					recMtl.setField("GAS_WRK_SCH_MAKE_YN", 		"N");			// Gas작업스케줄작성여부
					recMtl.setField("GAS_WRK_WRK_CMPL_YN", 		"N");			// Gas작업작업완료여부
					recMtl.setField("SHOT_BLST_WRK_MTL", 		"N");			// ShortBlast작업재
					recMtl.setField("S_BLST_WRK_SCH_MAKE_YN", 	"N");			// ShortBlast작업스케줄작성여부
					recMtl.setField("S_BLST_WRK_WRK_CMPL_YN", 	"N");			// ShortBlast작업작업완료여부
					recMtl.setField("PRESS_WRK_MTL", 			"N");			// 프레스교정재
					recMtl.setField("PRS_CORR_SCH_MAKE_YN", 	"N");			// Press교정스케줄작성여부
					recMtl.setField("PRS_CORR_WRK_CMPL_YN", 	"N");			// Press교정작업완료여부
					recMtl.setField("GDS_MAIN_GRD",				szGdsMainGrd);	// 충당대상재

					if ("Y".equals(szUsMaintmatl)) {
						recMtl.setField("US_MAINTMATL", 		szUsMaintmatl);		// 상면보수재
					}
					if ("Y".equals(szLsMaintmatl)) {
						recMtl.setField("LS_MAINTMATL", 		szLsMaintmatl);		// 하면보수재
					}
					if ("Y".equals(szCplWrkMtl)) {
						recMtl.setField("CPL_WRK_MTL", 			szCplWrkMtl);		// 냉간교정재
					}
					if ("Y".equals(szScplWrkMtl)) {
						recMtl.setField("SCPL_WRK_MTL", 		szScplWrkMtl);		// 강력교정재**
					}
					if ("Y".equals(szHttrtHplMtl)) {
						recMtl.setField("HTTRT_HPL_MTL", 		szHttrtHplMtl);		// 열처리교정재
					}
					if ("Y".equals(szGasWrkMtl)) {
						recMtl.setField("GAS_WRK_MTL", 			szGasWrkMtl);		// GAS작업재
					}
					if ("Y".equals(szShotBlstWrkMtl)) {
						recMtl.setField("SHOT_BLST_WRK_MTL", 	szShotBlstWrkMtl);	// ShortBlast작업재
					}
					if ("Y".equals(szPressWrkMtl)) {
						recMtl.setField("PRESS_WRK_MTL", 		szPressWrkMtl);		// 프레스교정재
					}
					if ("Y".equals(szPlWrPrsntProcCd)) {
						recMtl.setField("PL_WR_PRSNT_PROC_CD", 	szPlWrPrsntProcCd);	// 후판실적현공정코드
					}
					if ("Y".equals(szQaAsgnMtl)) {
						recMtl.setField("QA_ASGN_MTL", 			szQaAsgnMtl);		/// QA지정재 ***
					}						
					if ("Y".equals(szUtWaitMtl)) {
						recMtl.setField("UT_WAIT_MTL", 			szUtWaitMtl);		/// UT대기재 ***
					}						
						
//
//					if (!szYdMtlL.equals(szPlMeaGdsL) && !"".equals(szYdStkColGp)) {
//						// 점유상태 CLEAR
//						recPara = JDTORecordFactory.getInstance().create();
//						recPara.setField("MODIFIER",			szModifier);
//						recPara.setField("STL_NO", 				szStlNo);			// 재료번호
//						recPara.setField("YD_STK_LYR_MTL_STAT",	"E");
//						recPara.setField("YD_STK_COL_GP",		szYdStkColGp);
//						recPara.setField("YD_STK_BED_NO",		szYdStkBedNo);
//						recPara.setField("YD_STK_LYR_NO",		szYdStkLyrNo);
//						/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat 
//
//						UPDATE TB_YD_STKLYR
//						   SET MODIFIER            = :V_MODIFIER
//						     , MOD_DDTT            = SYSDATE
//						     , STL_NO              = :V_STL_NO
//						     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
//						     , YD_OCPY_BED_GP      = ''
//						     , YD_OCPY_STK_BED_NO  = ''
//						     , YD_OCPY_STK_LYR_NO  = ''
//						 WHERE YD_STK_COL_GP       = SUBSTR(:V_YD_STK_COL_GP,1,6)
//						   AND YD_STK_BED_NO       = :V_YD_STK_BED_NO
//						   AND YD_STK_LYR_NO       = :V_YD_STK_LYR_NO
//						*/   
//						intRtnVal = ydStkLyrDAO.updYdStklyrStat(recMtl);
//
//						szMsg ="[" + SZ_SESSION_NAME + "::" + szMethodName + "] 점유상태 CLEAR >>> 결과 :: " + intRtnVal;
//						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
//
//					}

					// 두께,폭,길이,중량 UPDATE 추가
					recMtl.setField("YD_MTL_T", 	ydDaoUtils.paraRecChkNull(recMtl, "PL_MEA_GDS_T"));		// 제촌두께
					recMtl.setField("YD_MTL_W", 	ydDaoUtils.paraRecChkNull(recMtl, "PL_MEA_GDS_W"));		// 제촌폭
					recMtl.setField("YD_MTL_L", 	ydDaoUtils.paraRecChkNull(recMtl, "PL_MEA_GDS_L"));		// 제촌길이
					recMtl.setField("YD_MTL_WT", 	ydDaoUtils.paraRecChkNull(recMtl, "PL_MEA_GDS_WT"));	// 제촌중량
					/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updNextDeciInfo2 

					UPDATE TB_YD_SHRSTOCK A
					   SET
					       MODIFIER                 = :V_MODIFIER                   -- 수정자
					     , MOD_DDTT                 = SYSDATE                       -- 수정일시
					     , DEL_YN                   = :V_DEL_YN                     -- 삭제유무
					     , US_MAINTMATL             = :V_US_MAINTMATL               -- 상면보수재
					     , US_MAINT_SCH_MAKE_YN     = :V_US_MAINT_SCH_MAKE_YN       -- 상면보수스케줄작성여부
					     , US_MAINT_WRK_CMPL_YN     = :V_US_MAINT_WRK_CMPL_YN       -- 상면보수작업완료여부
					     , LS_MAINTMATL             = :V_LS_MAINTMATL               -- 하면보수재
					     , LS_MAINT_SCH_MAKE_YN     = :V_LS_MAINT_SCH_MAKE_YN       -- 하면보수스케줄작성여부
					     , LS_MAINT_WRK_CMPL_YN     = :V_LS_MAINT_WRK_CMPL_YN       -- 하면보수작업완료여부
					     , CPL_WRK_MTL              = :V_CPL_WRK_MTL                -- 냉간교정재
					     , CR_CORR_SCH_MAKE_YN      = :V_CR_CORR_SCH_MAKE_YN        -- 냉간교정스케줄작성여부
					     , CR_CORR_WRK_CMPL_YN      = :V_CR_CORR_WRK_CMPL_YN        -- 냉간교정작업완료여부
					     , HTTRT_HPL_MTL            = :V_HTTRT_HPL_MTL              -- 열처리교정재
					     , HTTRT_CORR_SCH_MAKE_YN   = :V_HTTRT_CORR_SCH_MAKE_YN     -- 열처리교정스케줄작성여부
					     , HTTRT_CORR_WRK_CMPL_YN   = :V_HTTRT_CORR_WRK_CMPL_YN     -- 열처리교정작업완료여부
					     , GAS_WRK_MTL              = :V_GAS_WRK_MTL                -- GAS작업재
					     , GAS_WRK_SCH_MAKE_YN      = :V_GAS_WRK_SCH_MAKE_YN        -- Gas작업스케줄작성여부
					     , GAS_WRK_WRK_CMPL_YN      = :V_GAS_WRK_WRK_CMPL_YN        -- Gas작업작업완료여부
					     , SHOT_BLST_WRK_MTL        = :V_SHOT_BLST_WRK_MTL          -- ShortBlast작업재
					     , S_BLST_WRK_SCH_MAKE_YN   = :V_S_BLST_WRK_SCH_MAKE_YN     -- ShortBlast작업스케줄작성여부
					     , S_BLST_WRK_WRK_CMPL_YN   = :V_S_BLST_WRK_WRK_CMPL_YN     -- ShortBlast작업작업완료여부
					     , PRESS_WRK_MTL            = :V_PRESS_WRK_MTL              -- 프레스교정재
					     , PRS_CORR_SCH_MAKE_YN     = :V_PRS_CORR_SCH_MAKE_YN       -- Press교정스케줄작성여부
					     , PRS_CORR_WRK_CMPL_YN     = :V_PRS_CORR_WRK_CMPL_YN       -- Press교정작업완료여부
					     , PL_WR_PRSNT_PROC_CD      = :V_PL_WR_PRSNT_PROC_CD        -- 후판실적현공정코드
					     
					     , YD_MTL_T                 = NVL(:V_YD_MTL_T,  YD_MTL_T)   -- 제촌두께
					     , YD_MTL_W                 = NVL(:V_YD_MTL_W,  YD_MTL_W)   -- 제촌폭
					     , YD_MTL_L                 = NVL(:V_YD_MTL_L,  YD_MTL_L)   -- 제촌길이
					     , YD_MTL_WT                = NVL(:V_YD_MTL_WT, YD_MTL_WT)  -- 제촌중량
					     
					     , SCPL_WRK_MTL             = :V_SCPL_WRK_MTL               -- 강력교정재
					     , SCPL_WRK_SCH_MAKE_YN     = :V_SCPL_WRK_SCH_MAKE_YN       -- 강력교정스케줄작성여부
					     , SCPL_WRK_CMPL_YN         = :V_SCPL_WRK_CMPL_YN           -- 강력교정작업완료여부

					 WHERE STL_NO = :V_STL_NO  
					 */
					intRtnVal = ydStockDao.updNextDeciInfoYdP(recMtl);

					szMsg ="[" + SZ_SESSION_NAME + "::" + szMethodName + "] 야드재료정보 UPDATE >>> 결과 :: " + intRtnVal;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					if (!szYdMtlL.equals(szPlMeaGdsL) && !"".equals(szYdStkColGp)) {
						// 점유상태 SET
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("MODIFIER",			szModifier);
						recPara.setField("STL_NO", 				szStlNo);			// 재료번호
						recPara.setField("YD_STK_LYR_MTL_STAT",	szYdStkLyrMtlStat);
						recPara.setField("YD_STK_COL_GP",		szYdStkColGp);
						recPara.setField("YD_STK_BED_NO",		szYdStkBedNo);
						recPara.setField("YD_STK_LYR_NO",		szYdStkLyrNo);

						intRtnVal = ydStkLyrDAO.updYdStklyrStat(recPara);

						szMsg ="[" + SZ_SESSION_NAME + "::" + szMethodName + "] 점유상태 SET >>> 결과 :: " + intRtnVal;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					}
				}
			}
			
			//==================================================================================================
			if("1P".equals(sWR_ELE_PROC_CD)) {
				
				inRecord.setField("MODIFIER"		, "PRYDJ015"); 
				
				
				
				
				//1후판정정야드 삭제처리
				EJBConnector ejbConn 	= new EJBConnector("default", this);
				
				//if(sAPPPI5.equals("Y")){
				//	szRtnMsg = (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "delYdLocInfo2TX", inRecord);//트랜잭션 분리 처리 2024.02.06 데드락 현상 발생 방지 차원
                    //간혹 PRYDJ015 전문내 해당 로직과 바로 이어서 오는 PRYDJ017 전문이 STK_LYR 중복 점유 현상 발생함.	
				//}
				//else{
				String sAPPPI5 =ydPICommDAO.ApplyYnPI("",szOperationName,"APPPI5","T","*");
				if(sAPPPI5.equals("Y")){
					szRtnMsg = (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "delYdLocInfo3", inRecord);
				}
				else{
					szRtnMsg = (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "delYdLocInfo2", inRecord);
				}
				  
				//delYdLocInfo2 쓰이는곳이 많으므로 새로 만들자. PRYDJ015에 1P로 올때 대부분은 위치 없는 상태로와서 사실 대부분은 호출 안됨.
				//호출되는게 특수케이스. 따라서 호출되는 특수케이스만 별도 로직 타도록 delYdLocInfo3 새로 만들자(클리어 트랜잭션 분리)
				
				//1후판정정야드 크레인 작업 메세지 전송
				JDTORecord recSndPara = JDTORecordFactory.getInstance().create();
				
				//18호기
				recSndPara.setField("MSG_ID", 			"YDY2L007");			// YDY2L007 : 크레인작업메세지
				recSndPara.setField("YD_MSG_GP", 		"3"); // 2:해당동, 3:해당설비
				recSndPara.setField("YD_BAY_GP", 		"C"); 
				recSndPara.setField("YD_EQP_ID", 		"PCCRC2");
				recSndPara.setField("YD_WRK_MSG",		"냉간교정 입측 R/T로 Book-In 바랍니다.");
				ydDelegate.sendMsg(recSndPara);

				//19호기
				recSndPara.setField("MSG_ID", 			"YDY2L007");			// YDY2L007 : 크레인작업메세지
				recSndPara.setField("YD_MSG_GP", 		"3"); // 2:해당동, 3:해당설비
				recSndPara.setField("YD_BAY_GP", 		"C"); 
				recSndPara.setField("YD_EQP_ID", 		"PCCRC3");
				recSndPara.setField("YD_WRK_MSG",		"냉간교정 입측 R/T로 Book-In 바랍니다.");
				ydDelegate.sendMsg(recSndPara);
				
				szMsg = "[1후판정정차행선결정정보] 처리("+szMethodName+") 냉간교정(CPL) 실적 완료";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return JPlateYdConst.RETN_CD_SUCCESS;				
			}
			//==================================================================================================
			
			szYdSpanGp = ydUtils.substr(szYdStkColGp, 2, 2);

			//:: 종료된 재료는 추출 스케줄 기동 안하도록 보완
			if ("3".equals(szMtlStatCd)) {

				szRtnMsg = "해당 재료가 종료되어 추출 스케줄 기동 SKIP함!!!! 재료번호 :: " + szStlNo + ", 저장위치 :: " + szYdStkColGp;
				szMsg 	 = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return szRtnMsg;

			} else {

				// 저장위치가 가스장/TOD 일때 추출 스케쥴 기동
				// 저장위치(PF0108,PF0219 -> PF0150,PF0250) 가 이면검사일때 이적 스케쥴 기동
//SJH16 확인				
//				if ("CN".equals(szYdSpanGp) || "TD".equals(szYdSpanGp) || "FA0204".equals(szYdStkColGp) || "FB0403".equals(szYdStkColGp)) {
				//if ( "CN".equals(szYdSpanGp) || "TD".equals(szYdSpanGp)|| "BC".equals(szYdSpanGp)|| "PF0150".equals(szYdStkColGp) || "PF0250".equals(szYdStkColGp)) {
				if ( "CN".equals(szYdSpanGp) || "TD".equals(szYdSpanGp)|| "BC".equals(szYdSpanGp)
						|| "PB0150".equals(szYdStkColGp)
						|| "PC0150".equals(szYdStkColGp) || "PC0250".equals(szYdStkColGp) 
						|| "PD0150".equals(szYdStkColGp)
						|| "PE0150".equals(szYdStkColGp)
						|| "PF0150".equals(szYdStkColGp) || "PF0250".equals(szYdStkColGp)) {

					//----------------------------------------------------------------------
					// GAS/TOD/이면검사 추출 스케줄 기동
					//----------------------------------------------------------------------
					szMsg = "["+szOperationName+"] GAS/TOD/이면검사 추출 스케줄 기동";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					EJBConnector ejbConn 	= new EJBConnector("default", this);

					szYdBayGp 		= ydUtils.substr(szYdStkColGp, 1, 1);
					szYdSpanGp 		= ydUtils.substr(szYdStkColGp, 2, 2);
					szYdAimYdGp		= JPlateYdConst.YD_GP_P_PLATE_YARD;			// 목표야드구분
					szYdAimBayGp	= szYdBayGp;								// 목표동구분
					szYdAimSpanGp	= "";										// 목표스판구분
					szYdAimColGp	= "";										// 목표적치열구분
					szYdAimBedNo	= "";										// 목표적치BED구분

					recPara = JDTORecordFactory.getInstance().create();

					recPara.setField("YD_GP",				JPlateYdConst.YD_GP_P_PLATE_YARD);
					recPara.setField("YD_BAY_GP", 	      	szYdBayGp);
					recPara.setField("YD_SPAN_GP", 	      	szYdSpanGp);

					
					// 주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차, M:보수장이적
					if ("BC".equals(szYdSpanGp)) {
						recPara.setField("YD_MAIN_WRK_GP", 	JPlateYdConst.YD_MAIN_WRK_GP_BC_OUT);
					} else if ("CN".equals(szYdSpanGp)) {
						recPara.setField("YD_MAIN_WRK_GP", 	JPlateYdConst.YD_MAIN_WRK_GP_GAS_OUT);
					} else if ("TD".equals(szYdSpanGp)) {
						recPara.setField("YD_MAIN_WRK_GP", 	JPlateYdConst.YD_MAIN_WRK_GP_TOD_OUT);
					} else {
						recPara.setField("YD_MAIN_WRK_GP", 	JPlateYdConst.YD_MAIN_WRK_GP_MV);
					}
					recPara.setField("YD_TO_LOC_GUIDE_GP",	"Y");
					recPara.setField("YD_AIM_YD_GP", 	  	szYdAimYdGp);			// 목표야드구분
					recPara.setField("YD_AIM_BAY_GP", 	  	szYdAimBayGp);			// 목표동구분
					recPara.setField("YD_AIM_SPAN_GP", 	  	szYdAimSpanGp);			// 목표스판구분
					recPara.setField("YD_AIM_COL_GP", 	  	szYdAimColGp);			// 목표적치열구분
					recPara.setField("YD_AIM_BED_NO", 	  	szYdAimBedNo);			// 목표적치BED구분

					//---------------------------------------------------------------------------------------------
					//	후판정정의 준비스케줄 등록 시 지정한 작업매수의 값을 전달하여 해당하는 매수만큼 작업예약에 등록
					//---------------------------------------------------------------------------------------------
					recPara.setField("YD_EQP_WRK_SH", 	  	"1");
					recPara.setField("ARR_WLOC_CD", 	    "");

					//---------------------------------------------------------------------------------------------
					//	작업재료리스트와 JMS_TC_CD
					//---------------------------------------------------------------------------------------------
					recPara.setField("STL_LIST", 	        szStlNo);
					recPara.setField("JMS_TC_CD", 	        szRcvTcCode);
					recPara.setField("YD_USER_ID", 	        szModifier);
					
					//---------------------------------------------------------------------------------------------
					//	L3화면을 통해 만들어진 작업예약, 작업지시 설정
					//---------------------------------------------------------------------------------------------
					recPara.setField("CARD_NO",	szCARD_NO);		

					szMsg = "["+szOperationName+"] GAS/TOD/이면검사 추출 스케줄 기동 .. START";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					//내부 Process 연결
					szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapaYdP", recPara);

					szMsg = "["+szOperationName+"] GAS/TOD/이면검사 추출 스케줄 기동 .. END";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						szMsg =  "["+szOperationName+"] GAS/TOD/이면검사 추출 스케줄 기동 .. 결과 :: "+ szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

				// 저장위치가 보수장일때 추출 스케쥴 기동
				} else if ("BS".equals(szYdSpanGp) && !"1V".equals(szPlWrPrsntProcCd)) {

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("JMS_TC_CD",		szRcvTcCode);
					recPara.setField("ARR_STL_NO", 		szStlNo);
					recPara.setField("MODIFIER", 		szModifier);
					
					recPara.setField("BOOK_OUT_SPAN", sBOOK_OUT_SPAN);
					recPara.setField("BOOK_OUT_COL", sBOOK_OUT_COL);
					
					//---------------------------------------------------------------------------------------------
					// 차행선(후판실적현공정코드)가 1M:1후판 NO1 정정분기 인경우는 54020(PCRT70)으로 TO위치 가이드를 준다.
					//---------------------------------------------------------------------------------------------
					if("1M".equals(szPlWrPrsntProcCd)){
						if("".equals(sBOOK_OUT_SPAN)) {
							
							String sNEW_MODULE_EFF_YN = "N";
							
							JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
							
							sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A021"); //1후판정정야드 차행선 정단정정일때 RT 54020으로 북인 지정여부 
							
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 차행선 정단정정일때 RT 54020으로 북인 지정여부  : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
							
							if(sNEW_MODULE_EFF_YN.equals("Y")) {
								//54040 -> PCRT70
								recPara.setField("BOOK_OUT_SPAN", "RT");
								recPara.setField("BOOK_OUT_COL", "70");
							
							}
						}
					}

					//---------------------------------------------------------------------------------------------
					// 차행선(후판실적현공정코드)가 AJ:1후판 NO1 QA검사장 인경우는 54020(PCRT70)으로 TO위치 가이드를 준다.
					//---------------------------------------------------------------------------------------------
					if("AJ".equals(szPlWrPrsntProcCd)){
						if("".equals(sBOOK_OUT_SPAN)) {
							
							String sNEW_MODULE_EFF_YN = "N";
							
							JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
							
							sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A023"); //1후판정정야드 차행선 QA검사장일때 RT 54020으로 북인 지정여부 
							
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 차행선 QA검사장일때 RT 54020으로 북인 지정여부  : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
							
							if(sNEW_MODULE_EFF_YN.equals("Y")) {
								//54040 -> PCRT70
								recPara.setField("BOOK_OUT_SPAN", "RT");
								recPara.setField("BOOK_OUT_COL", "70");
							
							}
						}
					}
					
					//---------------------------------------------------------------------------------------------
					//	L3화면을 통해 만들어진 작업예약, 작업지시 설정
					//---------------------------------------------------------------------------------------------
					recPara.setField("CARD_NO",	szCARD_NO);		
					
					
					//*********************************************************************************************
					// 무정보 작업을 지정하면 보수장 BOOK_OUT 스케줄을 만들지 않고 재료를 임시 입고  BED로 이적 시키고 종료한다.
					//*********************************************************************************************
					if("Y".equals(szNoCrnSchYN)) {
						
						EJBConnector ejbConn 	= new EJBConnector("default", this);
						
						inRecord.setField("STL_NO"			, szStlNo); 
						inRecord.setField("EQP_GP"			, "02"); 
						inRecord.setField("MODIFIER"		, "PRYDJ015"); 
						
						//크레인 스케줄 취소 처리 추가
						szRtnMsg = (String)ejbConn.trx("JPlateYdL3RcvFaEJB", "rcvPRYDJ016", inRecord);
						
						JPlateYdCommDAO 		commDao 		= new JPlateYdCommDAO();
						
						String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
						
						JDTORecord setRecord = JDTORecordFactory.getInstance().create();
				        setRecord.setField("STL_NO"				,	szStlNo);
				        setRecord.setField("YD_TO_STK_COL_GP"	,	"PCTY0");
				        setRecord.setField("YD_GP"				,	JPlateYdConst.YD_GP_P_PLATE_YARD);
				        setRecord.setField("YD_BAY_GP"			,	"C");
		
				        JDTORecordSet  rsResult = commDao.select(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getSameLWGpBookInYdP", logId, szMethodName, "보수장일때 적치위치를 다시 조회 (보수장은 권하위치 틀림)");
				        if(rsResult.size() > 0){
				        	
				        	inRecord.setField("YD_STK_COL_GP", rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
				        	inRecord.setField("YD_STK_BED_NO", rsResult.getRecord(0).getFieldString("YD_STK_BED_NO"));
				        	inRecord.setField("YD_STK_LYR_NO", rsResult.getRecord(0).getFieldString("YD_STK_LYR_NO"));
				        	
							//저장위치 수정 호출
							szRtnMsg = (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "updYdLocInfo2", inRecord);
				        }
						
				        szRtnMsg = JPlateYdConst.RETN_CD_SUCCESS;
				        
					} else {
					
						//보수장 BOOK-OUT 크레인스케줄 생성
						szRtnMsg = this.procBsOutYdP(recPara);
						
					}

					if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						szMsg =  "["+szOperationName+"] 보수장 추출 스케줄 기동 .. 결과 :: "+ szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}
			}

		} catch(Exception e) {
			szRtnMsg = "1후판정정차행선결정정보 수신오류 >>>> " +e.getMessage();
			szMsg    = "[1후판정정차행선결정정보] Exception Error :: " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw new DAOException(szRtnMsg);
		} // end of try-catch

		szMsg = "[1후판정정차행선결정정보] 처리("+szMethodName+") 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		return JPlateYdConst.RETN_CD_SUCCESS;

	} // end of procPRNextDeciInfo

	/**
	 * 오퍼레이션명 : 보수장 추출 스케줄 기동
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public String procBsOutYdP(JDTORecord inRecord)throws JDTOException  {
		// DAO 및 UTIL 객체 생성
		JPlateYdStockDAO  ydStockDao 	= new JPlateYdStockDAO();

		// 레코드 선언
		JDTORecordSet rsRsltStl   		= null;
		JDTORecord recPara 		    	= null;
		JDTORecord recMtl 				= null;

		// 변수선언
		String 	sMethodName 			= "procBsOutYdP";
		String 	szOperationName 		= "1후판 정정 보수장 추출스케줄기동";
		String 	sMsg 					= "";
		String 	sRcvTcCode				= ydUtils.getTcCode(inRecord);

		String	szMODIFIER				= "";

		String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
		int    	intRtnVal 				= 0;
		int    	iStlCnt					= 0;
		String	arrStlNo[]				= null;
		String	szARR_STL_NO			= "";		// 재료번호 List
		String 	szSTL_NO				= "";		// 재료번호
		String 	szYD_GP               	= "";		// 야드구분
		String 	szYD_STK_COL_GP	        = "";		// 야드적치열구분
		String  szYD_BAY_GP 			= "";		// 야드동구분
		String	szYD_SPAN_GP 			= "";		// 야드스판구분
		String	szYD_AIM_YD_GP			= "";		// 목표야드구분
		String	szYD_AIM_BAY_GP			= "";		// 목표동구분
		String	szYD_AIM_SPAN_GP		= "";		// 목표스판구분
		String	szYD_AIM_COL_GP			= "";		// 목표적치열구분
		String	szYD_AIM_BED_NO			= "";		// 목표적치BED구분
		
		String  sBOOK_OUT_SPAN		= ""; //화면에서 입력한 TO위치 가이드 Span
		String  sBOOK_OUT_COL		= ""; //화면에서 입력한 TO위치 가이드 열 
		
		String  szCARD_NO			= ""; //L3화면을 통해 만들어진 작업예약, 작업지시 설정 **** 

		try {

			//----------------------------------------------------------------------
			// 1. 보수장 추출 스케줄 기동
			//----------------------------------------------------------------------
			sMsg = "["+szOperationName+"] 1. 보수장 추출 스케줄 기동";
			ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

			if (sRcvTcCode == null) {
				sMsg ="[ERROR] " + SZ_SESSION_NAME + "::" + sMethodName + "() TC Code Error (" + sRcvTcCode + ")";
				ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);

			} else {

				szYD_GP  	  = JPlateYdConst.YD_GP_P_PLATE_YARD;
				szARR_STL_NO  = ydDaoUtils.paraRecChkNull(inRecord, "ARR_STL_NO");

				szMODIFIER	 = ydDaoUtils.paraRecChkNull(inRecord, 	"YD_USER_ID");
				if ("".equals(szMODIFIER)) {
					szMODIFIER = ydDaoUtils.paraRecChkNull(inRecord, "MODIFIER", sRcvTcCode);
				}
				
				sBOOK_OUT_SPAN = ydDaoUtils.paraRecChkNull(inRecord, "BOOK_OUT_SPAN");
				sBOOK_OUT_COL  = ydDaoUtils.paraRecChkNull(inRecord, "BOOK_OUT_COL");
				
				szCARD_NO			= ydDaoUtils.paraRecChkNull(inRecord, "CARD_NO");		//L3화면을 통해 만들어진 작업예약, 작업지시 설정 (값이 "L3"이면 야드L3 화면에서 만들어진 지시임)**
				
				arrStlNo 	= szARR_STL_NO.split(";");
				iStlCnt 	= arrStlNo.length;

				EJBConnector ejbConn = new EJBConnector("default", this);

				rsRsltStl = JDTORecordFactory.getInstance().createRecordSet("");

				for(int ii=0; ii<iStlCnt; ii++) {

					recPara  = JDTORecordFactory.getInstance().create();

					szSTL_NO = arrStlNo[ii];
					//----------------------------------------------------------------------
					// 2. 재료 저장위치 조회 - 야드저장품에서 변경전 저장위치 정보를 조회
					//----------------------------------------------------------------------
					sMsg = "["+szOperationName+"] 2.재료 저장위치 조회 ";
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", 	szSTL_NO);
					recPara.setField("YD_GP", 	JPlateYdConst.YD_GP_P_PLATE_YARD);             	//야드구분
					// 재료번호
					intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsRsltStl);
					if (intRtnVal != 1) {
						szRtnMsg = "재료정보 정보 미존재 .. 재료번호 : " + szSTL_NO;
						sMsg = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
					rsRsltStl.first();
					recMtl = rsRsltStl.getRecord();

					szYD_STK_COL_GP  = ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_COL_GP");
					szYD_BAY_GP 	 = ydUtils.substr(szYD_STK_COL_GP, 1, 1);
					szYD_SPAN_GP 	 = ydUtils.substr(szYD_STK_COL_GP, 2, 2);
					szYD_AIM_YD_GP	 = szYD_GP;											// 목표야드구분
					szYD_AIM_BAY_GP	 = szYD_BAY_GP;										// 목표동구분
					szYD_AIM_SPAN_GP = sBOOK_OUT_SPAN;												// 목표스판구분
					szYD_AIM_COL_GP	 = sBOOK_OUT_COL;												// 목표적치열구분
					szYD_AIM_BED_NO	 = "";												// 목표적치BED구분

					recPara.setField("YD_GP",				szYD_GP);
					recPara.setField("YD_BAY_GP", 	      	szYD_BAY_GP);
					recPara.setField("YD_SPAN_GP", 	      	szYD_SPAN_GP);
					// 주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차
					recPara.setField("YD_MAIN_WRK_GP", 	  	JPlateYdConst.YD_MAIN_WRK_GP_BS_OUT);
					recPara.setField("YD_TO_LOC_GUIDE_GP",	"Y");
					recPara.setField("YD_AIM_YD_GP", 	  	szYD_AIM_YD_GP);			// 목표야드구분
					recPara.setField("YD_AIM_BAY_GP", 	  	szYD_AIM_BAY_GP);			// 목표동구분
					recPara.setField("YD_AIM_SPAN_GP", 	  	szYD_AIM_SPAN_GP);			// 목표스판구분
					recPara.setField("YD_AIM_COL_GP", 	  	szYD_AIM_COL_GP);			// 목표적치열구분
					recPara.setField("YD_AIM_BED_NO", 	  	szYD_AIM_BED_NO);			// 목표적치BED구분

					//---------------------------------------------------------------------------------------------
					//	후판정정의 준비스케줄 등록 시 지정한 작업매수의 값을 전달하여 해당하는 매수만큼 작업예약에 등록
					//---------------------------------------------------------------------------------------------
					recPara.setField("YD_EQP_WRK_SH", 	  	"1");
					recPara.setField("ARR_WLOC_CD", 	    "");

					//---------------------------------------------------------------------------------------------
					//	작업재료리스트와 JMS_TC_CD
					//---------------------------------------------------------------------------------------------
					recPara.setField("STL_LIST", 	        arrStlNo[ii]);
					recPara.setField("JMS_TC_CD", 	        sRcvTcCode);
					recPara.setField("YD_USER_ID", 	        szMODIFIER);

					//---------------------------------------------------------------------------------------------
					//	L3화면을 통해 만들어진 작업예약, 작업지시 설정
					//---------------------------------------------------------------------------------------------
					recPara.setField("CARD_NO",	szCARD_NO);		
					
					
					sMsg = "["+szOperationName+"] 3.보수장 추출 스케줄 기동 .. START";
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

					//내부 Process 연결
					szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapaYdP", recPara);

					sMsg =  "["+szOperationName+"] 3.보수장 추출 스케줄 기동 .. END";
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

					if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						sMsg =  "["+szOperationName+"] 3.보수장 추출 스케줄 기동 .. 결과 :: "+ szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}
			}

		} catch(Exception e) {
			sMsg = "["+szOperationName+"] Exception Error 발생 ";
			ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
			throw new DAOException(sMsg);
		} // end of try-catch

		sMsg = "["+szOperationName+"] 9.보수장 추출 스케줄 기동 완료";
		ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);
		return JPlateYdConst.RETN_CD_SUCCESS;

	} // end of procBsOut
	/**
	 * 오퍼레이션명 : 1후판조업 GAS장 절단실적수신 (PPYDJ014)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public String procPRGasCutResult(JDTORecord inRecord)throws JDTOException  {
		// DAO 및 UTIL 객체 생성
		JPlateYdStockDAO  ydStockDao 	= new JPlateYdStockDAO();
		JPlateYdStkLyrDAO ydStkLyrDao	= new JPlateYdStkLyrDAO();

		// 레코드 선언
		JDTORecordSet rsGetCutStl   	= null;
		JDTORecordSet rsGetRgntPkCnts	= null;		// 시편채취내용 그릇
		JDTORecordSet rsResult   		= null;
		JDTORecord recPara 		    	= null;
		JDTORecord recMtl 				= null;
		JDTORecord recCnc 		    	= null;
		JDTORecord recL2Para   			= null;

		// 변수선언
		String 	sMethodName 			= "procPRGasCutResult";
		String 	szOperationName 		= "1후판조업 GAS장 절단실적수신";
		String 	sMsg 					= "";
		String 	sRcvTcCode				= ydUtils.getTcCode(inRecord);

		String 	sPlMplNo    			= "";		// 후판모재료번호
		String 	sStlNo					= "";		// 후판재료번호
		String 	sYdGp               	= "";
		String	sStlAppearGp			= "";		// 재료외형구분
		String 	sYdStkBedNo	        	= "";		// 야드적치Bed번호
		int 	iFromBedNo	        	= 1;		// 야드적치Bed번호 (변경전 재료의 적재위치)
		String 	sYdStkColGp	        	= "";		// 야드적치열구분
		String 	sYdCurrStrLoc	    	= "";		// 야드현저장위치
		String 	sTemp					= "";
	    String 	sYdMtlWGp          	 	= "";       // 야드재료폭구분		< 4500:S1-소폭 , >=4500:L1-광폭
	    String 	sYdMtlTGp          	 	= "";       // 야드재료두께구분
	    String 	sYdMtlLGp           	= "";       // 야드재료길이구분	< 15000:S1-단척, >=1500:L1-장척
	    String	sYdStkLyrMtlStat		= "";

		float  	fYdMtlW					= 0.0f;		// 야드재료폭
		int    	iYdMtlL					= 0;		// 야드재료길이
		String	szMODIFIER				= "";
		String	sDelYn					= "";
		String  sRgntPkCnts             = "";       // 시편채취내용
		
		String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
		int    	intRtnVal 				= 0;
		int    	iCutCnt					= 0;
		String	arrStlNo[]				= null;
		String	szYdStockExist			= "Y";			// 야드에 재료 미존재시 가스장 절단실적 발생시 처리할수 있도록 보완
		String	szYdSchCall				= "";

		if (sRcvTcCode == null) {
			sMsg ="[ERROR] " + SZ_SESSION_NAME + "::" + sMethodName + "() TC Code Error (" + sRcvTcCode + ")";
			ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);

		} else {

			try {
			/*
				YD_GP				야드구분
				STL_APPEAR_GP		재료외형구분			(2013.06.10 항목추가)
				PL_MTL_NO    		후판재료번호
				PL_WR_GDS_TOT_SH	후판실적제품총매수
				STL_NO1				재료번호1
				YD_MTL_T1		6.3	야드재료두께1
				YD_MTL_W1		5.1	야드재료폭1
				YD_MTL_L1		7	야드재료길이1
				YD_MTL_WT1		5	야드재료중량1
			*/
			//  재료번호1, 2, 3 --> 3, 2, 1 Bed

				sYdGp 	 		= JPlateYdConst.YD_GP_P_PLATE_YARD;
			//	sStlAppearGp	= ydDaoUtils.paraRecChkNull(inRecord, 		"STL_APPEAR_GP");
				sStlAppearGp	= ydDaoUtils.paraRecChkNull(inRecord, 		"STL_APPEAR_GP", "P");
				sPlMplNo 		= ydDaoUtils.paraRecChkNull(inRecord, 		"PL_MPL_NO");
				iCutCnt  		= ydDaoUtils.paraRecChkNullInt(inRecord, 	"PL_WR_GDS_TOT_SH");
				szMODIFIER		= ydDaoUtils.paraRecModifier(inRecord);
				szYdSchCall		= ydDaoUtils.paraRecChkNull(inRecord, 		"YD_SCH_CALL", "N");

				//----------------------------------------------------------------------
				// 0. 전문 체크
				//----------------------------------------------------------------------
				if ("".equals(sPlMplNo)) {
					szRtnMsg = "모재료정보 데이타 오류 : " + sPlMplNo;
					sMsg = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
				if (iCutCnt < 1 || iCutCnt > 20) {
					szRtnMsg = "절단매수 데이타 오류 : " + Integer.toString(iCutCnt);
					sMsg = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
				if ("".equals(sStlAppearGp)) {
					szRtnMsg = "재료외형구분 데이타 오류 : " + sStlAppearGp;
					sMsg = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
                
//				/*
//				 * 2014.03.26 윤재광
//				 * 창고 사내절단장에서 절단실적 발생시 처리하고 리턴
//				 */
//				{
//					YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
//					JPlateYdStockDAO ydDao 	= new JPlateYdStockDAO();
//					
//					JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("yd");
//					recPara = JDTORecordFactory.getInstance().create();
//					recPara.setField("STL_NO", sPlMplNo);
//					
//					intRtnVal = ydStklyrDao.getYdStklyr(recPara, outRecSet, 622);
//					
//					if (intRtnVal > 0) {
//						
//						recMtl	= outRecSet.getRecord(0);
//						
//						String sYdStrLoc = ydDaoUtils.paraRecChkNull(recMtl, "YD_STR_LOC");
//						
//						if("FE010101".equals(sYdStrLoc)){
//							
//			                // 적치단에 존재하는 모재료정보를 모두 CLEAR한다.
//			                intRtnVal = ydStklyrDao.updYdStklyrWithStock(recPara);
//			            	
//			                for(int inx = 0; inx < iCutCnt; inx++) {
//			                	sStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"+Integer.toString(iCutCnt-inx));
//			                	
//			                	recPara = JDTORecordFactory.getInstance().create();
//				                recPara.setField("YD_STK_COL_TO", 	"FE0101");		// TO적치열
//				                recPara.setField("YD_STK_BED_TO", 	"01");			// TO적치BED 
//				                recPara.setField("ARR_STL_NO", 		sStlNo);		// 재료번호
//				                recPara.setField("STL_NO",			sStlNo);		// 재료번호
//				                
//				                //후판 정정야드 야드맵에 Data셋팅
//				                intRtnVal = ydStklyrDao.updYdStklyrJplateStlNo(recPara); 
//				                
//				                // 2후판 정정야드 재료정보 생성모듈 호출
//								intRtnVal = ydDao.getYdStockWithLoc(recPara, outRecSet);
//			
//								// 재료 정보 존재여부 체크
//								if (intRtnVal < 1) {
//									intRtnVal = ydDao.insYdStockBookOut(recPara);
//								}
//								
//				            }
//			               
//							sMsg = "["+szOperationName+"] 창고 사내절단장 절단실적 처리 성공";
//							ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);
//							return JPlateYdConst.RETN_CD_SUCCESS;
//						}
//						
//					}
//				}
				//----------------------------------------------------------------------
				// 0. PLATE정보 체크 :: 전문 전송하기전에 재료정보를 조회하여 3M 체크함 ...
				//----------------------------------------------------------------------
				sMsg = "["+szOperationName+"] 0. PLATE정보 조회 >>>> " + Integer.toString(iCutCnt);
				ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

				for(int ii=0; ii<iCutCnt; ii++) {

					// sStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"+Integer.toString(ii+1));
					// cut :: 3 , ii :: 0 --> 3 , ii :: 1 --> 2 , ii :: 2 --> 1
					//
					sStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"+Integer.toString(iCutCnt-ii));

					rsGetCutStl = JDTORecordFactory.getInstance().createRecordSet("yd");
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", sStlNo);				// PLATE NO

					//----------------------------------------------------------------------
					// 0.1. PLATE 정보 조회
					//----------------------------------------------------------------------
					intRtnVal = ydStockDao.getGasCutResult(recPara, rsGetCutStl);
					if (intRtnVal != 1) {
						szRtnMsg = "PLATE 정보 미존재 .. 재료번호 : " + sStlNo;
						sMsg = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					recMtl  = rsGetCutStl.getRecord(0);
					iYdMtlL = ydDaoUtils.paraRecChkNullInt(recMtl, 	"YD_MTL_L");

					// 길이가 3미터 미만 재료는 에러처리 (2013.05.07 김광률계장님 요청)
					if (iYdMtlL < 3000) {
						szRtnMsg = "PLATE 재료 길이가 3M 미만 .. 길이 : " + Integer.toString(iYdMtlL);
						sMsg = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					//----------------------------------------------------------------------
					// 0.2. PLATE 저장위치 정보를 조회하여 현위치가 (NULL, 가스장)이 아니면 오류로 처리
					//----------------------------------------------------------------------
					rsGetCutStl = JDTORecordFactory.getInstance().createRecordSet("yd");
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", 	sStlNo);												// PLATE번호
					recPara.setField("YD_GP", 	sYdGp);													// YARD구분
					intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsGetCutStl);
					if (intRtnVal > 0) {
						recMtl	= rsGetCutStl.getRecord(0);
						sYdStkColGp 	 = ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_COL_GP");			// 야드적치열구분
						sYdStkLyrMtlStat = ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_LYR_MTL_STAT");	// 재료적치상태

						// 저장위치 체크
						if (!"".equals(sYdStkColGp) && !"CN".equals(ydUtils.substr(sYdStkColGp, 2, 2))) {
							szRtnMsg = "PLATE 위치가 가스장이 아님 : " + sYdStkColGp;
							sMsg = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}

						// 적치상태 체크
						if (!"".equals(sYdStkLyrMtlStat) && !"C".equals(sYdStkLyrMtlStat)) {
							szRtnMsg = "PLATE 적치상태 오류 : " + sYdStkLyrMtlStat;
							sMsg = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}
					}
				}

				//----------------------------------------------------------------------
				// 1. 날판 저장위치 조회 - 야드저장품에서 변경전 저장위치 정보를 조회
				//----------------------------------------------------------------------
				sMsg = "["+szOperationName+"] 1. 날판 저장위치 조회 ";
				ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

				rsGetCutStl = JDTORecordFactory.getInstance().createRecordSet("yd");
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", sPlMplNo);				// 날판번호
				recPara.setField("YD_GP", 	sYdGp);			
				intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsGetCutStl);
				if (intRtnVal != 1) {
					szRtnMsg = "날판정보 정보 미존재 .. 날판번호 : " + sPlMplNo;
					sMsg = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
				//	return szRtnMsg;
					szYdStockExist = "N";
				}

				if ("Y".equals(szYdStockExist)) {
					recMtl       	= rsGetCutStl.getRecord(0);
					sYdStkBedNo   	= ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_BED_NO");		// 야드적치Bed번호
					iFromBedNo 		= ydDaoUtils.paraRecChkNullInt(recMtl, "YD_STK_BED_NO");	// 야드적치Bed번호 (변경전 Bed Index)
					sYdStkColGp   	= ydDaoUtils.paraRecChkNull(recMtl, "YD_STK_COL_GP");		// 야드적치열구분
					sYdCurrStrLoc 	= ydDaoUtils.paraRecChkNull(recMtl, "YD_CURR_STR_LOC");		// 야드현저장위치
					sRgntPkCnts		= ydDaoUtils.paraRecChkNull(recMtl, "RGNT_PK_CNTS"); 		// 시편채취내용

					// 2013.05.07 윤재광과장님 요청 .. 날판이 가스장위치가 아니면 재료만 등록하고 스케쥴 기동 안하도록 요청
					if (!"CN".equals(ydUtils.substr(sYdStkColGp, 2, 2))) {
						szYdStockExist = "N";
					}
				}

				if ("Y".equals(szYdStockExist)) {
					//----------------------------------------------------------------------
					// 2. 날판 정보 CLEAR
					//----------------------------------------------------------------------

					//----------------------------------------------------------------------
					// 2.1. 야드L2 전문송신 (저장품제원 :: YDY7L002 전송)
					//----------------------------------------------------------------------
					sMsg = "[ " +szOperationName + "] 2.1. 야드L2 저장품제원 전문송신 (날판삭제정보) START";
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

		        	recL2Para = JDTORecordFactory.getInstance().create();
		        	recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
		        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
		        	recL2Para.setField("YD_STK_COL_GP", 	sYdStkColGp);                          	// 야드적치열구분
		        	recL2Para.setField("YD_STK_BED_NO", 	sYdStkBedNo);    						// 야드적치BED번호
		        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드  [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
		        	recL2Para.setField("STL_NO", 			sPlMplNo);	        					// 재료번호
		        	recL2Para.setField("MSG_GP", 			"D");	        						// 전문구분 - D:삭제
		        	recL2Para.setField("DEL_YN_CHECK",		"N");									// 삭제된 데이타도 조회하도록 처리

		        	szRtnMsg = ydDelegate.sendMsg(recL2Para);

					sMsg = "[ " +szOperationName + "] >>>> 야드L2 저장품제원 전문송신(날판삭제정보) END >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

					//----------------------------------------------------------------------
					// 2.2. 날판 재료정보 삭제
					//----------------------------------------------------------------------
					sMsg = "["+szOperationName+"] 2.2. 날판 재료정보 삭제";
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("MODIFIER",		szMODIFIER);
					recPara.setField("STL_NO",			sPlMplNo);
					intRtnVal = ydStockDao.delYdStock(recPara);
					if (intRtnVal != 1) {
						szRtnMsg = "날판 재료정보 삭제시 오류발생 : " + Integer.toString(intRtnVal);
						sMsg = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					//----------------------------------------------------------------------
					// 2.3. 날판 저장위치 CLEAR
					//----------------------------------------------------------------------
					sMsg = "["+szOperationName+"] 2.3. 날판 저장위치 CLEAR";
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("MODIFIER",			szMODIFIER);
					recPara.setField("YD_STK_COL_GP",		sYdStkColGp);
					recPara.setField("YD_STK_BED_NO",		sYdStkBedNo);
					recPara.setField("STL_NO",				sPlMplNo);
					recPara.setField("YD_GP",				ydUtils.substr(sYdStkColGp,0,1));

					intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
					if (intRtnVal < 1) {
						szRtnMsg = "날판 저장위치 CLEAR 오류발생 : " + Integer.toString(intRtnVal);
						sMsg = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}

				arrStlNo = new String[iCutCnt];

				//----------------------------------------------------------------------
				// 3. PLATE정보 생성
				//----------------------------------------------------------------------
				sMsg = "["+szOperationName+"] 3. PLATE정보 생성 >>>> " + Integer.toString(iCutCnt);
				ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

				for(int ii=0; ii<iCutCnt; ii++) {

					// sStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"+Integer.toString(ii+1));
					// cut :: 3 , ii :: 0 --> 3 , ii :: 1 --> 2 , ii :: 2 --> 1
					//
					sStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"+Integer.toString(iCutCnt-ii));
					arrStlNo[ii] = sStlNo;

					rsGetCutStl = JDTORecordFactory.getInstance().createRecordSet("yd");
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", sStlNo);				// PLATE NO

					//----------------------------------------------------------------------
					// 3.1. PLATE 정보 조회
					//----------------------------------------------------------------------
					intRtnVal = ydStockDao.getGasCutResult(recPara, rsGetCutStl);
					if (intRtnVal != 1) {
						szRtnMsg = "PLATE 정보 미존재 .. 재료번호 : " + sStlNo;
						sMsg = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					recMtl      	= rsGetCutStl.getRecord(0);
					sTemp  			= ydDaoUtils.paraRecChkNull(recMtl,		"YD_MTL_W");
					fYdMtlW 		= ydUtils.strToFloat(sTemp, 5, 1);
					iYdMtlL  		= ydDaoUtils.paraRecChkNullInt(recMtl, 	"YD_MTL_L");
					sDelYn			= ydDaoUtils.paraRecChkNull(recMtl, 	"DEL_YN");

					sMsg = "["+szOperationName+"] GAS절단실적 수신후 .... 조업테이블 조회 결과 >>>> " + "두께 ::"  + ydDaoUtils.paraRecChkNull(recMtl, "YD_MTL_T")
					     + ", 폭::"  + ydDaoUtils.paraRecChkNull(recMtl,	"YD_MTL_W") + ", 길이::"	+ ydDaoUtils.paraRecChkNull(recMtl,	"YD_MTL_L");
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);

					// BRE RULE 적용
					JDTORecord jdtoRcd = JDTORecordFactory.getInstance().create();
			    	boolean bRtnVal = GetBreRule8.getYDB801(JPlateYdConst.YD_GP_P_PLATE_YARD, fYdMtlW, jdtoRcd);
			    	if (bRtnVal) {
			    		sYdMtlWGp = ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_MTL_W_GP");
			    	} else {
			    		sYdMtlWGp = "";
			    	}
					jdtoRcd = JDTORecordFactory.getInstance().create();
			    	bRtnVal = GetBreRule8.getYDB802(JPlateYdConst.YD_GP_P_PLATE_YARD, iYdMtlL, jdtoRcd);
			    	if (bRtnVal) {
			    		sYdMtlLGp = ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_MTL_L_GP");
			    	} else {
			    		sYdMtlLGp = "";
			    	}

					if ("Y".equals(szYdStockExist)) {
				//		sYdStkBedNo   = ydUtils.addLeftStr(Integer.toString(ii+1), 2, '0');				// 야드적치Bed번호
						sYdStkBedNo   = ydUtils.addLeftStr(Integer.toString(ii+iFromBedNo), 2, '0');	// 야드적치Bed번호
						sYdCurrStrLoc = sYdStkColGp + sYdStkBedNo;										// 야드현저장위치
					} else {
						sYdStkColGp   = "";
						sYdStkBedNo   = "";
						sYdCurrStrLoc = "";
					}

					// 가스장에 이미 재료가 존재시 다음 적치위치 조회
					rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
					recCnc   = JDTORecordFactory.getInstance().create();
					recPara  = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_STK_COL_GP", 	sYdStkColGp);
					recPara.setField("YD_STK_BED_NO", 	sYdStkBedNo);
					recPara.setField("YD_GP", 			sYdGp);

					sMsg = "["+szOperationName+"] 가스장 적치 가능 위치 BEFORE >>>> " + sYdStkColGp + sYdStkBedNo;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

					intRtnVal = ydStkLyrDao.getEmptyToCnc(recPara, rsResult);
					if (intRtnVal <= 0) {
						szRtnMsg = "가스장 저장위치 조회 오류 : " + Integer.toString(intRtnVal);
						sMsg = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
					rsResult.first();
					recCnc = rsResult.getRecord();
					sYdStkColGp = ydDaoUtils.paraRecChkNull(recCnc, "YD_STK_COL_GP");
					sYdStkBedNo = ydDaoUtils.paraRecChkNull(recCnc, "YD_STK_BED_NO");

					sMsg = "["+szOperationName+"] 가스장 적치 가능 위치 AFTER >>>> " + sYdStkColGp + sYdStkBedNo;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

					//----------------------------------------------------------------------
					// 3.1.1  파라미터 셋팅
					//----------------------------------------------------------------------	
					recMtl.setField("YD_GP", 				sYdGp);
					recMtl.setField("STL_NO", 		 		sStlNo);
					recMtl.setField("YD_STK_COL_GP",		sYdStkColGp);							// 야드적치열구분
					recMtl.setField("YD_STK_BED_NO",		sYdStkBedNo);							// 야드적치Bed번호
					recMtl.setField("YD_CURR_STR_LOC", 		sYdCurrStrLoc);							// 야드현저장위치
					recMtl.setField("YD_MTL_W_GP", 	 		sYdMtlWGp);       						// 야드재료폭구분
					recMtl.setField("YD_MTL_T_GP", 	 		sYdMtlTGp);       						// 야드재료두께구분
					recMtl.setField("YD_MTL_L_GP", 	 		sYdMtlLGp);       						// 야드재료길이구분
					recMtl.setField("REGISTER",				szMODIFIER);
					recMtl.setField("MODIFIER",				szMODIFIER);
					recMtl.setField("DEL_YN",				"N");
					recMtl.setField("YD_STK_LYR_NO",		"001");									// 적치단
					recMtl.setField("YD_STK_LYR_MTL_STAT", 	"C");									// 재료적치상태
					recMtl.setField("RGNT_PK_CNTS", 		sRgntPkCnts);							// 시편채취내용

					sMsg = "["+szOperationName+"] 가스절단실적 등록데이터 :: "+ sStlNo;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

					//----------------------------------------------------------------------
					// 3.2. 정정야드 저장품 정보 등록
					//----------------------------------------------------------------------
					if ("".equals(sDelYn)) {
						// 정정야드저장품 Insert
						intRtnVal = ydStockDao.insYdStockCutResult(recMtl);
					} else {
						// 정정야드저장품 Update
						intRtnVal = ydStockDao.updYdStockCutResult(recMtl);
					}
					if (intRtnVal != 1) {
						szRtnMsg = "가스절단실적 등록 오류 .. 재료번호 : " + sStlNo;
						sMsg = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					if ("Y".equals(szYdStockExist)) {
						//----------------------------------------------------------------------
						// 3.3. 적치단 정보 등록
						//----------------------------------------------------------------------
						// 야드맵 업데이트 : Null일때는 Skip
						if (!"".equals(sYdStkBedNo) && !"".equals(sYdStkColGp)) {

							intRtnVal = ydStkLyrDao.updYdStklyrStat(recMtl);
							if (intRtnVal != 1) {
								szRtnMsg = "가스절단실적 적치단 등록 오류 .. 재료번호 : " + sStlNo;
								sMsg = "["+szOperationName+"] " + szRtnMsg;
								ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
								return szRtnMsg;
							}
						}
					} else {
						// 날판 저장위치 미존재시는 저장위치를 등록하지 않고 추후 수작업으로 저장위치를 등록하여야함 (윤재광과장님 요청사항)
						sMsg =  "["+szOperationName+"] 날판 저장위치 미존재하여 절단 재료 적치단 등록 SKIP";
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);
					}

					//----------------------------------------------------------------------
					// 3.4. 야드L2 전문송신 (저장품제원 :: YDY7L002 전송)
					//----------------------------------------------------------------------
					sMsg = "[ " +szOperationName + "] 야드L2 저장품제원 전문송신 START";
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

		        	recL2Para = JDTORecordFactory.getInstance().create();
		        	recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
		        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
		        	recL2Para.setField("YD_STK_COL_GP", 	sYdStkColGp);                          // 야드적치열구분
		        	recL2Para.setField("YD_STK_BED_NO", 	sYdStkBedNo);    						// 야드적치BED번호
		        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
		        	recL2Para.setField("STL_NO", 			sStlNo);	        					// 재료번호
		        	szRtnMsg = ydDelegate.sendMsg(recL2Para);

					sMsg = "[ " +szOperationName + "] 야드L2 저장품제원 전문송신 END >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);
				}

				// 가스장 추출 스케쥴을 검사실적 : 차행선결정정보 수신 (PPYDJ015) 수신후 처리하도록 보완
				// 2013.09.03 화면에서 북아웃요청시 스케쥴 기동하도록 보완

				if ("Y".equals(szYdStockExist) && "Y".equals(szYdSchCall)) {
					//----------------------------------------------------------------------
					// 4. GAS장 추출 스케줄 기동
					//----------------------------------------------------------------------
					sMsg = "["+szOperationName+"] 4. GAS장 추출 스케줄 기동";
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

					EJBConnector ejbConn 	= new EJBConnector("default", this);

					String sYD_BAY_GP 		= sYdStkColGp.substring(1, 2);
					String sYD_SPAN_GP 	= sYdStkColGp.substring(2, 4);
					String sYD_AIM_YD_GP	= JPlateYdConst.YD_GP_P_PLATE_YARD;			// 목표야드구분
					String sYD_AIM_BAY_GP	= sYD_BAY_GP;								// 목표동구분
					String sYD_AIM_SPAN_GP	= "";										// 목표스판구분
					String sYD_AIM_COL_GP	= "";										// 목표적치열구분
					String sYD_AIM_BED_NO	= "";										// 목표적치BED구분

					for(int ii=0; ii<iCutCnt; ii++) {

						recPara = JDTORecordFactory.getInstance().create();

						recPara.setField("YD_GP",				JPlateYdConst.YD_GP_P_PLATE_YARD);
						recPara.setField("YD_BAY_GP", 	      	sYD_BAY_GP);
						recPara.setField("YD_SPAN_GP", 	      	sYD_SPAN_GP);
						// 주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차
						recPara.setField("YD_MAIN_WRK_GP", 	  	JPlateYdConst.YD_MAIN_WRK_GP_GAS_OUT);
						recPara.setField("YD_TO_LOC_GUIDE_GP",	"Y");
						recPara.setField("YD_AIM_YD_GP", 	  	sYD_AIM_YD_GP);			// 목표야드구분
						recPara.setField("YD_AIM_BAY_GP", 	  	sYD_AIM_BAY_GP);		// 목표동구분
						recPara.setField("YD_AIM_SPAN_GP", 	  	sYD_AIM_SPAN_GP);		// 목표스판구분
						recPara.setField("YD_AIM_COL_GP", 	  	sYD_AIM_COL_GP);		// 목표적치열구분
						recPara.setField("YD_AIM_BED_NO", 	  	sYD_AIM_BED_NO);		// 목표적치BED구분

						//---------------------------------------------------------------------------------------------
						//	후판정정의 준비스케줄 등록 시 지정한 작업매수의 값을 전달하여 해당하는 매수만큼 작업예약에 등록
						//---------------------------------------------------------------------------------------------
						recPara.setField("YD_EQP_WRK_SH", 	  	"1");
						recPara.setField("ARR_WLOC_CD", 	    "");

						//---------------------------------------------------------------------------------------------
						//	작업재료리스트와 JMS_TC_CD
						//---------------------------------------------------------------------------------------------
						recPara.setField("STL_LIST", 	        arrStlNo[ii]);
						recPara.setField("JMS_TC_CD", 	        sRcvTcCode);
						recPara.setField("YD_USER_ID", 	        szMODIFIER);

						sMsg = "["+szOperationName+"] GAS장 추출 스케줄 기동 .. START";
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

						//내부 Process 연결
						szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapaYdP", recPara);

						sMsg =  "["+szOperationName+"] GAS장 추출 스케줄 기동 .. END";
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

						if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
							sMsg =  "["+szOperationName+"] GAS장 추출 스케줄 기동 .. 결과 :: "+ szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}
					}
				} else {
					sMsg =  "["+szOperationName+"] GAS장 추출 스케쥴 SKIP >>>> 재료존재 :: " + szYdStockExist + ", 스케쥴기동 :: " + szYdSchCall;
					ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);
				}


			} catch(Exception e) {
				sMsg = "[1후판정정야드가스절단실적] Exception Error 발생 ";
				ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
				throw new DAOException("[후판정정야드가스절단실적]" + sMsg);
			} // end of try-catch
		}

		ydUtils.putLog(SZ_SESSION_NAME, sMethodName, "[1후판정정야드가스절단실적] 처리("+sMethodName+") 완료", JPlateYdConst.DEBUG);
		return JPlateYdConst.RETN_CD_SUCCESS;

	} // end of procPRGasCutResult

}
