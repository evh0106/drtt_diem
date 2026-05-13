/**
 * @(#)CCoilWrkBookSeEJBBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2019/05/02
 *
 * @description      2열연 COIL 야드 작업예약 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자     요청자  수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2019/05/02   정종균  이현진      최초 등록
 * 
 */
package com.inisteel.cim.yd.ccoil.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.yd.ccommon.util.CConstant;
import com.inisteel.cim.yd.ccommon.dao.CCommDAO;
import com.inisteel.cim.yd.ccoil.dao.CCoilDAO;

/**
 *      [A] 클래스명 : 2열연 COIL 야드 작업예약 처리
 *
 * @ejb.bean name="CCoilWrkBookSeEJB" jndi-name="CCoilWrkBookSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/
 
public class CCoilWrkBookSeEJBBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private CCommUtils commUtils = new CCommUtils();
	private CCommDAO commDao = new CCommDAO();
	private CCoilDAO coilDao = new CCoilDAO();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * 오퍼레이션명 : C열연 작업예약 생성 모든 소스는 여기로 호출하도록 WrkBookInsertProc
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @p aram JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * 
	 */
	public JDTORecord insWrkBook(JDTORecord rcvMsg) throws DAOException  {
		String mthdNm = "작업예약생성[CCoilWrkBookSeEJB.insWrkBook] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		JDTORecord	  jrRtn	   = JDTORecordFactory.getInstance().create(); //
		JDTORecordSet jsResult = JDTORecordFactory.getInstance().createRecordSet("");

		String sMsg	= "";

		try	{
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId,	rcvMsg);

			String ydSchCd			 	= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"		   )); //스케줄	코드
			String sStlSh				= commUtils.nvl	(rcvMsg.getFieldString("STL_SH"			   ), "0");	//재료매수
			int	iStlSh					= Integer.parseInt(sStlSh);

			String sToYdStkBedNo	 	= commUtils.trim(rcvMsg.getFieldString("TO_YD_STK_BED_NO"  ));
			String ydToLocDcsnMtd 		= commUtils.trim(rcvMsg.getFieldString("YD_TO_LOC_DCSN_MTD"));
			String ydAimGp 				= commUtils.trim(rcvMsg.getFieldString("YD_AIM_YD_GP"	   ));
			String ydAimBayGp	 		= commUtils.trim(rcvMsg.getFieldString("YD_AIM_BAY_GP"	   ));
			String ydWrkPlanTcar	 	= commUtils.trim(rcvMsg.getFieldString("YD_WRK_PLAN_TCAR"  ));
			String sCardNo				= commUtils.trim(rcvMsg.getFieldString("CARD_NO"		   ));
			String ydCarUseGp			= commUtils.trim(rcvMsg.getFieldString("YD_CAR_USE_GP"	   ));
			String sDistShipassignGp	= commUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP"));
			String sCarNo				= commUtils.trim(rcvMsg.getFieldString("CAR_NO"			   ));
			String sTrnEqpCd			= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"		   ));
			String sIsptor				= commUtils.trim(rcvMsg.getFieldString("ISPTOR"			   ));
			String sTakeOutDt			= commUtils.trim(rcvMsg.getFieldString("TAKE_OUT_DT"	   ));
			String stakeOutCd			= commUtils.trim(rcvMsg.getFieldString("TAKE_OUT_CD"	   ));
			String sydEqpId  			= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"		   ));
			
			// 21.07.06 KJS 생성할 코일번호가 미리만들어진 작업예약에 있는지 체크
			String sWrkMtlChk			= commUtils.nvl(rcvMsg.getFieldString("WRKMTL_CHK"		   ), "Y");
			
			String sModifier   			= commUtils.trim(rcvMsg.getFieldString("MODIFIER" )); //수정자(Backup Only)
			String supZoneDummy			= commUtils.nvl(rcvMsg.getFieldString("SUPZONE_DUMMY"		   ), "N");
			if ("".equals(sModifier)) {	sModifier =	"";	}

			String[] sStlNo			   	= new String[100]; //재료번호
			String[] ydUpCollSeq		= new String[100]; //권상모음순서

			String sWrkCrn	   	= "";//야드작업크레인
			String ydSchPrior	= "";//야드작업크레인우선순위
			String ydWbookId 	= "";//작업예약ID

//			DAO	Parameter -	Log	ID,	Method,	수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, commUtils.trim(sModifier));

			/**********************************************************
			* 0. 항목 값 Check
			**********************************************************/
			//스케줄 코드
			if ("".equals(ydSchCd))	{
				sMsg = "스케줄코드가 없습니다.";
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sMsg);
				return jrRtn;
			}
			//재료매수
			if (iStlSh == 0) {
				sMsg = "재료매수가 없습니다.";
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sMsg);
				return jrRtn;
			}

			/**********************************************************
			* 1. 스케줄코드	Check
			**********************************************************/
			//스케줄코드
			jrParam.setField("YD_SCH_CD", ydSchCd);
			/*
			SELECT YD_SCH_CD
				 , DEL_YN
				 , YD_GP
				 , YD_BAY_GP
				 , YD_SCH_RNG_CD
				 , YD_SCH_WHIO_GP
				 , YD_SCH_DIV_GP
				 , YD_SCH_RULE_ACT_STAT
				 , YD_WRK_CRN
				 , YD_WRK_CRN_PRIOR
				 , YD_ALT_CRN_YN
				 , YD_ALT_CRN
				 , YD_ALT_CRN_PRIOR
				 , CD_CONTENTS
				 , YD_SCH_PROH_EXN
			  FROM TB_YD_SCHRULE
			 WHERE 1 = 1
			   AND YD_GP IN	('H','J')
			   AND YD_SCH_CD = :V_YD_SCH_CD
			   AND DEL_YN	 = 'N'
			 */
			jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdSchRule", logId, mthdNm, "스케줄기준 조회");

			//리턴값 메세지처리
			if (jsResult.size()	<= 0) {
				sMsg = "스케줄코드(" + ydSchCd + ")에 대한 스케줄기준 데이터가 이상합니다.";
				commUtils.printLog(logId, sMsg,	"SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sMsg);
				return jrRtn;
			}

			//레코드 추출
			jsResult.absolute(1);

			String ydGp			   	= commUtils.trim(jsResult.getRecord().getFieldString("YD_GP"		   )); //야드구분
			String ydBayGp		  	= commUtils.trim(jsResult.getRecord().getFieldString("YD_BAY_GP"	   )); //동구분
			String ydSchProhExn	  	= commUtils.trim(jsResult.getRecord().getFieldString("YD_SCH_PROH_EXN" )); //스케줄	금지 유무
			String ydWrkCrn		  	= commUtils.trim(jsResult.getRecord().getFieldString("YD_WRK_CRN"	   )); //작업크레인
			String ydWrkCrnPrior 	= commUtils.trim(jsResult.getRecord().getFieldString("YD_WRK_CRN_PRIOR")); //작업크레인우선순위
			String ydAltCrnYn	 	= commUtils.trim(jsResult.getRecord().getFieldString("YD_ALT_CRN_YN"   )); //대체크레인유무
			String ydAltCrn		  	= commUtils.trim(jsResult.getRecord().getFieldString("YD_ALT_CRN"	   )); //대체크레인
			String ydAltCrnPrior	= commUtils.trim(jsResult.getRecord().getFieldString("YD_ALT_CRN_PRIOR")); //대체크레인우선순위

			//스케줄 금지 유무가 "Y"이면 처리를	중지
			if ("Y".equals(ydSchProhExn)) {
				sMsg = "스케줄 금지 유무가 [Y] 입니다";
				commUtils.printLog(logId, sMsg,	"SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sMsg);
				return jrRtn;
			}

			//야드 목적동
			if ("".equals(ydAimBayGp)) {
				if (!"".equals(sToYdStkBedNo)) {
					ydAimBayGp = sToYdStkBedNo.substring(1,	2);
				}
			}

			//대차
			if (ydBayGp.equals(ydAimBayGp))	{
				ydWrkPlanTcar =	"";
			}

			/**********************************************************
			* 2. 작업크레인	체크
			**********************************************************/
			//작업크레인 설비 상태 체크
			boolean	blnRtnVal =	coilDao.chkEqpStat(logId, mthdNm, ydWrkCrn);

			commUtils.printLog(logId, "blnRtnVal:"+ blnRtnVal, "SL");

			// 스케쥴 작업크레인 Default
			sWrkCrn	   = ydWrkCrn;
			ydSchPrior = ydWrkCrnPrior;

			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal)	{

				sMsg = "작업크레인("+ ydWrkCrn +")이 사용 불가 상태입니다.";
				commUtils.printLog(logId, sMsg,	"SL");

				//대체크레인의 유무	체크
				if (!"Y".equals(ydAltCrnYn)) {
					sMsg = "대체크레인유무("+ ydAltCrnYn +"), 대체크레인이 없습니다.";
					commUtils.printLog(logId, sMsg,	"SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", sMsg);
					return jrRtn;
				}

				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal =	coilDao.chkEqpStat(logId, mthdNm, ydAltCrn);

				//대체크레인 사용여부
				if (!blnRtnVal)	{
					sMsg = "대체크레인("+ ydAltCrn +")이 사용 불가 상태입니다.";
					commUtils.printLog(logId, sMsg,	"SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", sMsg);
					return jrRtn;
				} else {
					//대체크레인이 사용가능하면	설비사양 파라미터에	대체크레인의 설비ID를 세팅한다.
					sWrkCrn	   = ydAltCrn;
					ydSchPrior = ydAltCrnPrior;
				}
			}


			
			/**********************************************************
			* 3. 작업재료 check
			**********************************************************/
			if( "Y".equals(sWrkMtlChk) ) {
					
				//다른 작업예약에 재료가 등록되어있는지	체크한다.
				for	(int ii	= 1; ii	<= iStlSh; ii++) {
	
					sStlNo[ii] 			= commUtils.trim(rcvMsg.getFieldString("STL_NO"		   +ii));
					ydUpCollSeq[ii]		= commUtils.trim(rcvMsg.getFieldString("YD_UP_COLL_SEQ"+ii));
	
					/*
					SELECT A.YD_WBOOK_ID		 AS	YD_WBOOK_ID
						  ,A.DEL_YN				 AS	DEL_YN
						  ,A.YD_GP				 AS	YD_GP
						  ,A.YD_BAY_GP			 AS	YD_BAY_GP
						  ,A.YD_SCH_CD			 AS	YD_SCH_CD
						  ,A.YD_SCH_PRIOR		 AS	YD_SCH_PRIOR
						  ,A.YD_SCH_PROG_STAT	 AS	YD_SCH_PROG_STAT
						  ,A.YD_SCH_ST_GP		 AS	YD_SCH_ST_GP
						  ,A.YD_SCH_REQ_GP		 AS	YD_SCH_REQ_GP
						  ,A.YD_AIM_YD_GP		 AS	YD_AIM_YD_GP
						  ,A.YD_AIM_BAY_GP		 AS	YD_AIM_BAY_GP
						  ,A.YD_CTS_RELAY_YN	 AS	YD_CTS_RELAY_YN
						  ,A.YD_CTS_RELAY_BAY_GP AS	YD_CTS_RELAY_BAY_GP
						  ,A.YD_TO_LOC_DCSN_MTD	 AS	YD_TO_LOC_DCSN_MTD
						  ,A.YD_TO_LOC_GUIDE	 AS	YD_TO_LOC_GUIDE
						  ,B.STL_NO				 AS	STL_NO
						  ,B.YD_STK_COL_GP		 AS	YD_STK_COL_GP
						  ,B.YD_STK_BED_NO		 AS	YD_STK_BED_NO
						  ,B.YD_STK_LYR_NO		 AS	YD_STK_LYR_NO
						  ,B.YD_UP_COLL_SEQ		 AS	YD_UP_COLL_SEQ
					  FROM TB_YD_WRKBOOK	A
						  ,TB_YD_WRKBOOKMTL	B
					 WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
					   AND A.DEL_YN	= 'N'
					   AND B.DEL_YN	= 'N'
					   AND B.STL_NO	= :V_STL_NO
					 */
					jrParam.setField("STL_NO", sStlNo[ii]);
					jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdWrkbookmtlSTLNO", logId, mthdNm, "작업예약조회");
	
					//리턴값 메세지처리
					if (jsResult.size()	> 0) {
						sMsg = "재료번호("+ sStlNo[ii] +")로 작업예약재료 등록되어 있습니다.!";
						commUtils.printLog(logId, sMsg,	"SL");
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", sMsg);
						return jrRtn;
					}
				} //END	FOR
			}


			/**********************************************************
			* 4. 작업예약 생성
			**********************************************************/
			//작업예약 Id생성
			ydWbookId =	coilDao.getSeqId(logId,	mthdNm,	"WrkBook");

			if ("".equals(ydWbookId)) {
				throw new Exception("작업예약ID 생성 실패");
			}


			//저장품 조회 (목표동 및 목표야드 조회)
			jrParam.setField("STL_NO", sStlNo[1]); // 일단첫코일 기준으로
			/**********************************************************
			 * JHCRH2 크레인이 작업할수 없는 공간 로직 추가 YYS 20230403
			 * ('JH32','JH33','JH34','JH35','JH36','JH37')
			 * 
			 **********************************************************/
//			
//			String sAPP013_YN = coilDao.ApplyYn(logId, mthdNm, "APP001","J","000"); 
//			commUtils.printLog(logId, "JHCRH2 크레인이 작업할수 없는 공간 : " + sAPP013_YN, "SL");
//			if ("Y".equals(sAPP013_YN)) {
//				
//				jrParam.setField("YD_WRK_PLAN_CRN_CHECK", sWrkCrn);
//				/*
//				 * SELECT A.YD_STK_COL_GP          AS YD_STK_COL_GP
//				     , A.YD_STK_BED_NO          AS YD_STK_BED_NO
//				     , A.YD_STR_GTR_CD          AS YD_STR_GTR_CD
//				     , B.YD_STK_LYR_NO          AS YD_STK_LYR_NO
//				     , CASE WHEN  
//				          :V_YD_WRK_PLAN_CRN_CHECK = 'JHCRH2' -- JHCRH2 크레인이 작업할수 없는 공간    
//				          AND SUBSTR(A.YD_STK_COL_GP,0,4) IN ('JH32','JH33','JH34','JH35','JH36','JH37') 
//				          AND A.YD_STK_BED_NO IN ('01') 
//				          AND B.YD_STK_LYR_NO = '001' THEN 'N'                   
//				       ELSE 'Y' END CHECK_YN
//				  FROM TB_YD_STKBED A
//				     , TB_YD_STKLYR B 
//				 WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP 
//				   AND A.YD_STK_BED_NO = B.YD_STK_BED_NO 
//				   AND B.STL_NO = :V_STL_NO
//				   AND A.DEL_YN = 'N'
//				   AND B.DEL_YN = 'N'
//				   AND B.YD_STK_LYR_ACT_STAT = 'E'
//                   AND B.YD_STK_LYR_MTL_STAT = 'C'
//				 * 
//				 */
//				jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilWrkBookSeEJB.getStklyrCheckCrn",	logId, mthdNm, "작업가능여부 판단 JHCRH2 크레인 조회");
//				String checkYn = "Y";
//				if (jsResult.size()	> 0) {
//	
//					checkYn		= commUtils.trim(jsResult.getRecord(0).getFieldString("CHECK_YN"));
//				}
//				
//				if ("N".equals(checkYn)) { // JHCRH2 크레인이 작업할수 없는 공간 대체크레인으로 변경 
//					sWrkCrn	   = ydAltCrn;
//				}
//			}
           /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdStock", logId, mthdNm, "저장품 조회");

			if (rsResult.size()	<= 0) {
				sMsg="getYdStock : data	not	found";
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sMsg);
				return jrRtn;
			}

			if ("".equals(ydAimGp))	{
				ydAimGp		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_AIM_YD_GP"));
			}
			if ("".equals(ydAimBayGp)) {
				ydAimBayGp 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_AIM_BAY_GP"));
			}

			//INSERT 항목 record 생성
			jrParam	= commUtils.getParam(logId,	mthdNm,	sModifier);

			if("Y".equals(supZoneDummy)){
				ydSchPrior = "0";
			}
			
			//이적화면에서 지정한 크레인이 있을경우 해당 호기로 스케쥴 편성
			if(!"".equals(sydEqpId)){
				sWrkCrn = sydEqpId ;
			}
			
			//INSERT할 항목	SET
			jrParam.setField("YD_WBOOK_ID"			,ydWbookId);
			jrParam.setField("YD_GP"				,ydGp);
			jrParam.setField("YD_BAY_GP"			,ydBayGp);
			jrParam.setField("YD_SCH_CD"			,ydSchCd);
			jrParam.setField("YD_SCH_PRIOR"			,ydSchPrior);
			jrParam.setField("YD_AIM_YD_GP"			,ydAimGp);
			jrParam.setField("YD_AIM_BAY_GP"		,ydAimBayGp);
			jrParam.setField("YD_TO_LOC_DCSN_MTD"	,ydToLocDcsnMtd);
			jrParam.setField("YD_TO_LOC_GUIDE"		,sToYdStkBedNo);	//야드To위치Guide
			jrParam.setField("YD_WRK_PLAN_TCAR"		,ydWrkPlanTcar);
			jrParam.setField("YD_CAR_USE_GP"		,ydCarUseGp);
			jrParam.setField("DIST_SHIPASSIGN_GP"	,sDistShipassignGp); //차량동간이적	방향 1:1통로로
			jrParam.setField("CARD_NO"				,sCardNo);
			jrParam.setField("TRN_EQP_CD"			,sTrnEqpCd);
			jrParam.setField("CAR_NO"				,sCarNo);
			jrParam.setField("YD_WRK_PLAN_CRN"		,sWrkCrn);

			//작업예약 INSERT
			commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilWrkBookSeEJB.insYdWrkbook",	logId, mthdNm, "작업예약 생성");

			commUtils.printLog(logId, "작업예약 생성 완료", "SL");

			/*****************************************
			 * 5. 작업예약 재료(TB_YD_WRKBOOKMTL) 생성
			 *****************************************/
			commUtils.printLog(logId, "작업예약 재료 생성", "SL");
			//조회항목 record 생성
//			jrParam	=commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_WBOOK_ID",	  ydWbookId);

			for	(int ii	= 1; ii	<= iStlSh; ii++) {

				/*
				-- 작업예약	재료조회
				SELECT SL.STL_NO
					 , SL.YD_STK_COL_GP
					 , SL.YD_STK_BED_NO
					 , SL.YD_STK_LYR_NO
					 , PT.COIL_WT
					 , PT.COIL_T
					 , PT.COIL_W
					 , PT.CURR_COIL_LEN
					 , TO_CHAR(PT.COIL_T)||' X '||TO_CHAR(PT.COIL_W,'FM9,999') AS MTL_SIZE
					 , TO_CHAR(PT.COIL_OUTDIA,'FM99,999') AS COIL_OUTDIA
					 , SL.YD_STK_COL_GP||SL.YD_STK_BED_NO||'-'||SL.YD_STK_LYR_NO AS	YD_STR_LOC
					 , PT.MILL_INI_DATE
				  FROM TB_YD_STKLYR		SL
					 , TB_YD_STOCK		ST
					 , USRPTA.TB_PT_COILCOMM   PT
				 WHERE SL.STL_NO = ST.STL_NO
				   AND SL.STL_NO = PT.COIL_NO
				   AND SL.STL_NO = :V_STL_NO
				   AND SL.YD_STK_LYR_MTL_STAT =	'C'
				   AND SL.YD_STK_COL_GP	LIKE 'J%'
				   AND NOT EXISTS (SELECT  1
									 FROM TB_YD_WRKBOOK	   WB
										, TB_YD_WRKBOOKMTL WM
									WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
									  AND WB.DEL_YN	= 'N'
									  AND WM.DEL_YN	= 'N'
									  AND WM.STL_NO	= PT.COIL_NO
								  )
				*/
				jrParam.setField("STL_NO" ,	sStlNo[ii]);
				jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilWrkBookSeEJB.getWrkBookMtl",	logId, mthdNm, "작업예약재료 조회");
				if (jsResult.size()	<= 0) {

					sMsg = "재료번호에 해당하는	적치 확인 중 에러";
					commUtils.printLog(logId, sMsg,	"SL");

					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "재료번호에 해당하는 적치 확인 중 에러");
					return jrRtn;
				}

	 			//레코드추출
				jrParam.setField("YD_STK_COL_GP",  jsResult.getRecord(0).getFieldString("YD_STK_COL_GP")); //적치열구분
				jrParam.setField("YD_STK_BED_NO",  jsResult.getRecord(0).getFieldString("YD_STK_BED_NO")); //적치BED번호
				jrParam.setField("YD_STK_LYR_NO",  jsResult.getRecord(0).getFieldString("YD_STK_LYR_NO")); //적치단번호
				jrParam.setField("YD_UP_COLL_SEQ", ydUpCollSeq[ii]);  //권상모음순서

				jrParam.setField("YD_ISPTOR"	 , sIsptor);
				jrParam.setField("YD_TAKE_OUT_DT", sTakeOutDt);
				jrParam.setField("YD_TAKE_OUT_CD", stakeOutCd);

				// 작업예약재료	테이블에 등록한다.
				commDao.insert(jrParam,	"com.inisteel.cim.yd.ccoil.dao.CCoilWrkBookSeEJB.insYdWrkbookMtl", logId, mthdNm, "작업예약재료 등록");
			}

			jrRtn.setField("RTN_CD"		, "1");
			jrRtn.setField("RTN_MSG"	, "작업예약 등록 완료");

			jrRtn.setField("YD_WBOOK_ID", ydWbookId);
			jrRtn.setField("YD_SCH_CD"	, ydSchCd);
			jrRtn.setField("YD_WRK_CRN"	, sWrkCrn);

			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 


	/**
	 *      [A] 오퍼레이션명 : 작업예약 생성 (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public JDTORecord insWrkBookTx(JDTORecord rcvMsg) throws DAOException {
		String mthdNm  = "작업예약생성Tx분리[CCoilWrkBookSeEJB.insWrkBookTx] < " + rcvMsg.getResultMsg();
		String logId   = rcvMsg.getResultCode();
		
		JDTORecord jrRtn  = null;
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilWrkBookSeEJB", this);
			jrRtn = (JDTORecord)ejbConn.trx("insWrkBook", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			
			String rtnCd	= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			if ("0".equals(rtnCd)) {
				commUtils.printLog(logId, "작업예약 등록시 ERROR", "S-");
				
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", "작업예약 등록시 ERROR");	
				return jrRtn;
			
			}
			
			commUtils.printLog(logId, mthdNm, "S-");

			jrRtn.setField("RTN_MSG", "작업예약 등록 완료");
			
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 	
	
}