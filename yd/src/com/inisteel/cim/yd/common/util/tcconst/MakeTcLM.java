package com.inisteel.cim.yd.common.util.tcconst;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;

import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDBAssist;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.ym.common.YmCommonUtil;


public class MakeTcLM {

	
	//클래스명
	private static final String szSessionName  = MakeTcLM.class.getName();
	private static YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	

	/**
	 * YDDMR002 : 후판입고작업실적(M10YDLMJ1012)
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeM10YDLMJ1012(JDTORecord inRec, JDTORecordSet outRecSet){
		// 크레인스케줄Dao 객체 생성
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
		YdDaoUtils ydDaoUtils   = new YdDaoUtils();
		YdUtils ydUtils         = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		= null;
		JDTORecord outRec 		= null;

		// 조회 결과를 담을 RecordSet생성
		JDTORecordSet rsGetYdCrnsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg            = "";
		String szMethodName     = "makeM10YDLMJ1012";
		String szOperationName  = "출하 후판입고작업실적";
		
		// 제품번호
		String szSTL_NO 		= "";

		// 입고 일자 및 시각(크레인권하완료일시)
		String szYD_DN_CMPL_DT	= "";
		String szRECEIPT_DATE	= "";
		String szRECEIPT_TIME	= "";

		// 야드구분
		String szYD_GP			= "";

		// 저장위치(권하실적위치(7) + 권하실적단단(3))
		String szYD_DN_WR_LOC	= "";
		String szYD_DN_WR_LAYER = "";
		String szSTORE_LOC      = "";
		String szTemp           = "";
		String szYD_STK_COL_GP  = "";
		String szYD_STK_BED_NO  = "";
		String szYD_STK_LYR_NO  = "";
		String szCRN_WRK_MTL_LYR_NO = "";
		int intSTK_LYR          = 0;
		
		//재료진도코드 - 임춘수 추가 2009.06.15
		String szSTL_PROG_CD	= "";

		int intRtnVal = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 크레인스케줄 조회
			/* com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCrnwrkmtlPlate */
			intRtnVal = ydCrnSchDao.getYdCrnsch(inRec, rsGetYdCrnsch, 51);
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 크레인스케줄조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 크레인스케줄 조회결과 추출
			for(int i=0; i<intRtnVal; i++){
				outRec  = JDTORecordFactory.getInstance().create();
				recPara = rsGetYdCrnsch.getRecord(i);
				szSTL_PROG_CD = ydDaoUtils.paraRecChkNull(recPara, "CURR_PROG_CD");
				if( !szSTL_PROG_CD.equals("2") && 
					!szSTL_PROG_CD.equals(YdConstant.PROG_CD_RCPT_WAIT) &&	
					!szSTL_PROG_CD.equals(YdConstant.PROG_CD_OVALL_STMP_WAIT) ) {
					szMsg = "재료진도코드[" + szSTL_PROG_CD + "]가 입고대기[" + YdConstant.PROG_CD_RCPT_WAIT + "]/종합판정대기[" + YdConstant.PROG_CD_OVALL_STMP_WAIT + "]가 아니므로 출하관리로 후판입고작업실적 전송불가";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					continue;
				}
				szMsg = "재료진도코드[" + szSTL_PROG_CD + "]가 입고대기[" + YdConstant.PROG_CD_RCPT_WAIT + "]/종합판정대기[" + YdConstant.PROG_CD_OVALL_STMP_WAIT + "]이므로 출하관리로 후판입고작업실적 전송가능";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

				// 제품번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

				// 입고 일자 및 시각(크레인권하완료일시)
				szYD_DN_CMPL_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_DN_CMPL_DT").trim();
				if(szYD_DN_CMPL_DT != "" && szYD_DN_CMPL_DT.length() == 14){
					szRECEIPT_DATE = szYD_DN_CMPL_DT.substring(0, 8);
					szRECEIPT_TIME = szYD_DN_CMPL_DT.substring(8, 14);					
				}

				// 야드구분
				szYD_GP = ydDaoUtils.paraRecChkNull(inRec, "YD_GP");

				szYD_DN_WR_LOC = ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WR_LOC").trim();
				if(szYD_DN_WR_LOC.equals("")){
					szMsg = "권하실적위치 항목이 유효하지 않습니다. STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					continue ;
				}
				
				szYD_DN_WR_LAYER = ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WR_LAYER");
				if(szYD_DN_WR_LAYER.equals("")){
					szMsg = "권하실적단 항목이 유효하지 않습니다. STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					continue ;
				}
				
				szYD_STK_COL_GP = szYD_DN_WR_LOC.substring(0, 6);
				szYD_STK_BED_NO	= szYD_DN_WR_LOC.substring(6, 8);

				//크레인작업재료의 단정보
				szCRN_WRK_MTL_LYR_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				//크레인작업재료의 단 - 1
				intSTK_LYR			 = Integer.parseInt(szCRN_WRK_MTL_LYR_NO) - 1;
				//각 크레인작업재료에 대해서 권하실적위치단에서 1단씩 증가시킴
				szYD_STK_LYR_NO = ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, intSTK_LYR);				
				szSTORE_LOC = ydUtils.ParsingStkColGpBedLyr(szYD_STK_COL_GP, szYD_STK_BED_NO, szYD_STK_LYR_NO);

				outRec.setField("MQ_TC_CD"          , new String("M10YDLMJ1012"));
				outRec.setField("MQ_TC_CREATE_DDTT" , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				outRec.setField("GOODS_NO"          , szSTL_NO);
				outRec.setField("RECEIPT_DATE"      , szRECEIPT_DATE);
				outRec.setField("RECEIPT_TIME"      , szRECEIPT_TIME);
				outRec.setField("YD_GP"             , szYD_GP);
				outRec.setField("STORE_LOC"         , szSTORE_LOC);
				outRec.setField("PROD_ITEM_CODE"    , "");
				outRec.setField("CURR_PROG_CD"      , szSTL_PROG_CD);
				outRec.setField("DIST_GOODS_GP"     , "P");
				outRec.setField("YARD_GP"           , "");
				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	

			} // end of for
			
		}catch(Exception e){
			szMsg = "[후판입고작업실적] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR002()


	/**
	 * YDDMR005 : 후판제품이적작업실적
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeM10YDLMJ1032(JDTORecord inRec, JDTORecordSet outRecSet){
		// 크레인스케줄Dao 객체 생성
		YdCrnSchDao ydCrnSchDao  = new YdCrnSchDao();
		YdDaoUtils ydDaoUtils    = new YdDaoUtils();
		YdUtils ydUtils          = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		 = null;
		JDTORecord outRec 		 = null;

		// 조회 결과를 담을 RecordSet생성
		JDTORecordSet rsGetYdCrnsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg             = "";
		String szMethodName      = "makeM10YDLMJ1032";
		String szOperationName  = "출하 후판제품이적작업실적";
		
		// 제품번호
		String szSTL_NO 		 = "";

		// FROM저장위치(권상실적위치 + 권상실적단)
		String szBEFO_STORE_LOC	 = "";
		String szYD_UP_WR_LOC	 = "";
		String szYD_UP_WR_LAYER	 = "";
		int intSTK_LYR				= 0;

		// TO 저장위치(권하실적위치 + 권하실적단)
		String szTO_STORE_LOC	 = "";
		String szYD_DN_WR_LOC	 = "";
		String szYD_DN_WR_LAYER	 = "";

		// 이적 일자및 시각(권하완료일시)
		String szYD_DN_CMPL_DT	 = "";
		String szMOVENSTACK_DATE = "";
		String szMOVENSTACK_TIME = "";
		
		String szUPTemp = "";
		String szDNTemp = "";
		
		String szYD_STK_COL_GP  = "";
		String szYD_STK_BED_NO  = "";
		String szYD_STK_LYR_NO  = "";
		String szCRN_WRK_MTL_LYR_NO		= null;
		
		//재료진도코드
		String szSTL_PROG_CD = "";
		int nTemp                = 0;
		int intRtnVal            = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 크레인스케줄 조회
			/* com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCrnwrkmtlPlate */
			intRtnVal = ydCrnSchDao.getYdCrnsch(inRec, rsGetYdCrnsch, 51);			
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 크레인스케줄조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}

			//크레인스케줄 조회결과 추출
			for(int i=0; i<intRtnVal; i++){
				outRec  = JDTORecordFactory.getInstance().create();
				recPara = rsGetYdCrnsch.getRecord(i);
				szSTL_PROG_CD 				= ydDaoUtils.paraRecChkNull(recPara, "CURR_PROG_CD");
				if( szSTL_PROG_CD.equals(YdConstant.PROG_CD_RCPT_WAIT) ||
					szSTL_PROG_CD.equals(YdConstant.PROG_CD_STMP_HOLD) || 
					szSTL_PROG_CD.equals(YdConstant.PROG_CD_OVALL_STMP_WAIT)) {
					szMsg = "재료진도코드[" + szSTL_PROG_CD + "]가 입고대기[" + YdConstant.PROG_CD_RCPT_WAIT + "]/판정보류[" + YdConstant.PROG_CD_OVALL_STMP_WAIT + "]/종합판정대기[" + YdConstant.PROG_CD_OVALL_STMP_WAIT + "]이므로 출하관리로 후판제품이적작업실적 전송불가";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					continue;
				}
				
				
				szMsg = "재료진도코드[" + szSTL_PROG_CD + "]가 입고대기[" + YdConstant.PROG_CD_RCPT_WAIT + "]/판정보류[" + YdConstant.PROG_CD_OVALL_STMP_WAIT + "]/종합판정대기[" + YdConstant.PROG_CD_OVALL_STMP_WAIT + "]가 아니므로 출하관리로 후판제품이적작업실적 전송가능";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

				// 제품번호
				szSTL_NO 		 			= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				//크레인작업재료의 단정보
				szCRN_WRK_MTL_LYR_NO		= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				
				//크레인작업재료의 단 - 1
				intSTK_LYR			= Integer.parseInt(szCRN_WRK_MTL_LYR_NO) - 1;
				
				// FROM 저장위치
				//==============================================================
				// 권오창
				// 2009.12.16
				//
				// 코일 (8+2) : 권하실적위치(8)+ 권하실적단(3->2)
				//==============================================================				
				szYD_UP_WR_LOC = ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WR_LOC").trim();
				if(szYD_UP_WR_LOC.equals("")){
					szMsg = "권상실적위치 항목이 유효하지 않습니다. STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					continue ;
				}
				
				szYD_UP_WR_LAYER = ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WR_LAYER");
				if(szYD_UP_WR_LAYER.equals("")){
					szMsg = "권상실적단 항목이 유효하지 않습니다. STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					continue ;
				}
				
				szYD_STK_COL_GP = szYD_UP_WR_LOC.substring(0, 6);
				szYD_STK_BED_NO	= szYD_UP_WR_LOC.substring(6, 8);
				
				//각 크레인작업재료에 대해서 권상실적위치단에서 1단씩 증가시킴
				szYD_STK_LYR_NO = ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, intSTK_LYR);
				
				szBEFO_STORE_LOC = ydUtils.ParsingStkColGpBedLyr(szYD_STK_COL_GP, szYD_STK_BED_NO, szYD_STK_LYR_NO);
												
				// TO 저장위치	
				//==============================================================
				// 권오창
				// 2009.12.16
				//
				// 코일 (8+2) : 권하실적위치(8)+ 권하실적단(3->2)
				//==============================================================				
				szYD_DN_WR_LOC = ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WR_LOC").trim();
				if(szYD_DN_WR_LOC.equals("")){
					szMsg = "권하실적위치 항목이 유효하지 않습니다. STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					continue ;
				}
				
				szYD_DN_WR_LAYER = ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WR_LAYER");
				if(szYD_DN_WR_LAYER.equals("")){
					szMsg = "권하실적단 항목이 유효하지 않습니다. STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					continue ;
				}
								
				szYD_STK_COL_GP = szYD_DN_WR_LOC.substring(0, 6);
				szYD_STK_BED_NO	= szYD_DN_WR_LOC.substring(6, 8);
				
				//각 크레인작업재료에 대해서 권하실적위치단에서 1단씩 증가시킴
				szYD_STK_LYR_NO = ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, intSTK_LYR);
				
				szTO_STORE_LOC = ydUtils.ParsingStkColGpBedLyr(szYD_STK_COL_GP, szYD_STK_BED_NO, szYD_STK_LYR_NO);
			
				
				
				
				
				// 야드권하완료일시 (이적일자/이적시각)
				szYD_DN_CMPL_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_DN_CMPL_DT").trim();
				if(szYD_DN_CMPL_DT != "" && szYD_DN_CMPL_DT.length() == 14){
					szMOVENSTACK_DATE = szYD_DN_CMPL_DT.substring(0, 8);
					szMOVENSTACK_TIME = szYD_DN_CMPL_DT.substring(8, 14);
				}
				
					

				outRec.setField("MQ_TC_CD"          , new String("M10YDLMJ1032"));
				outRec.setField("MQ_TC_CREATE_DDTT" , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				outRec.setField("GOODS_NO"          , szSTL_NO);
				outRec.setField("STORE_LOC_CD_FROM" , szBEFO_STORE_LOC);
				outRec.setField("STORE_LOC_CD_TO"   , szTO_STORE_LOC);
				outRec.setField("MOVENSTACK_DATE"   , szMOVENSTACK_DATE);
				outRec.setField("MOVENSTACK_TIME"   , szMOVENSTACK_TIME);
				outRec.setField("DIST_GOODS_GP"     , "P");
				outRec.setField("YD_GP"             , "T");
				outRec.setField("YARD_GP"           , "");
				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			} // end of for
		}catch(Exception e){
			szMsg = "[후판제품이적작업실적] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR005()


//	*********************************************************************************************************************************** //	



	/**
	 * YDDMR008 : 후판출하상차개시
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeM10YDLMJ1072(JDTORecord inRec, JDTORecordSet outRecSet){
		//	1.	인터페이스ID					  TC_CODE					VARCHAR2(8)		YDDMR008
		//	2.	전송일시						  TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
		//	3.	카드 번호						  CARD_NO					VARCHAR2(4)
		//	4.	차량 번호						  CAR_NO					VARCHAR2(15)
		//	5.  상차 개시 일자					  CARLOAD_START_DATE		VARCHAR2(8)
		//	6.	상차 개시 시각					  CARLOAD_START_TIME		VARCHAR2(6)
		//	7.	이송지시일자					  TRANS_WORD_DATE			VARCHAR2(8)
		//	8.	이송지시순번					  TRANS_WORD_SEQNO			VARCHAR2(4)
		//  9.  상차포인트				      
		
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao     = new YdCarSchDao();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		YdUtils ydUtils             = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		    = null;
		JDTORecord outRec 		    = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg                = "";
		String szMethodName         = "makeM10YDLMJ1072";
		String szOperationName      = "출하 후판출하상차개시";
		
		// 카드번호
		String szCARD_NO 		    = "";

		// 차량번호
		String szCAR_NO			    = "";

		// 운송지시일자
		String szTRANS_ORD_DATE 	= "";

		// 운송지시순번
		String szTRANS_ORD_SEQNO	= "";

		// 상차개시일자및시각 (야드상차개시일시)
		String szYD_CARLD_ST_DT  	= "";
		String szCARLOAD_START_DATE = "";
		String szCARLOAD_START_TIME = "";
		String szCARLD_PNT_CD = null;

		int intRtnVal               = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			//해송스케줄 이후 상차 포인트 
			szCARLD_PNT_CD = ydDaoUtils.paraRecChkNull(inRec, "CARLD_PNT_CD ");
			
			// 차량스케줄 조회
			// 기본쿼리에도 항목있는것 확인 , 상차개시때는 차량이송재료에 없기에 JOIN은 안됨
			/* com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarsch */
			intRtnVal = ydCarSchDao.getYdCarsch(inRec, rsGetYdCarsch, 0);
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 차량스케줄조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 차량스케줄 조회결과 추출
			//for(int i=0; i<intRtnVal; i++){		
				outRec = JDTORecordFactory.getInstance().create();
				rsGetYdCarsch.first();
				recPara = rsGetYdCarsch.getRecord();

				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, recPara);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

				// 카드번호
				szCAR_NO          = ydDaoUtils.paraRecChkNull(recPara, "CAR_NO");
				szTRANS_ORD_DATE  = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE");
				szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO");

					
				szYD_CARLD_ST_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_ST_DT").trim(); // 상차 개시일자 , 상차개시 시각
				szCARLOAD_START_DATE = (YdUtils.getCurDate("yyyyMMddHHmmss")).substring(0, 8);
				szCARLOAD_START_TIME = (YdUtils.getCurDate("yyyyMMddHHmmss")).substring(8, 14);	

				outRec.setField("MQ_TC_CD"          , new String("M10YDLMJ1072"));
				outRec.setField("MQ_TC_CREATE_DDTT" , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				outRec.setField("CAR_NO"            , szCAR_NO);
				outRec.setField("CARLOAD_START_DATE", szCARLOAD_START_DATE);
				outRec.setField("CARLOAD_START_TIME", szCARLOAD_START_TIME);
				outRec.setField("TRN_REQ_DATE"      , szTRANS_ORD_DATE);
				outRec.setField("TRN_REQ_SEQ"       , szTRANS_ORD_SEQNO);
	            outRec.setField("DIST_GOODS_GP"     , "P");
	            outRec.setField("YD_GP"             , "T");
	            outRec.setField("SCH_YN"            , "N");
	            
				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
			//} // end of for
			
		}catch(Exception e){
			szMsg = "후판출하상차개시  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR008()






	/**
	 * YDDMR009 : 외판슬라브출하상차개시
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeM10YDLMJ1073(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao     = new YdCarSchDao();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		YdUtils ydUtils             = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		    = null;
		JDTORecord outRec 		    = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg                = "";
		String szMethodName         = "makeM10YDLMJ1073";
		String szOperationName      = "출하 외판슬라브출하상차개시";
		
		// 카드번호
		String szCARD_NO 		    = "";

		// 차량번호
		String szCAR_NO			    = "";
 
		// 야드구분
		String szYD_GP			    = "";

		// 운송지시일자
		String szTRANS_ORD_DATE 	= "";

		// 운송지시순번
		String szTRANS_ORD_SEQNO	= "";

		// 상차개시일자및시각 (야드상차개시일시)
		String szYD_CARLD_ST_DT     = "";
		String szCARLOAD_START_DATE = "";
		String szCARLOAD_START_TIME = "";

		int intRtnVal = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 차량스케줄 조회
			// 상차개시는 재료와 JOIN걸면 안됨
			intRtnVal= ydCarSchDao.getYdCarsch(inRec, rsGetYdCarsch, 0);
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 차량스케줄조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 차량스케줄 조회결과 추출
			//for(int i=0; i<intRtnVal; i++){	
				outRec = JDTORecordFactory.getInstance().create();
				rsGetYdCarsch.first();
				recPara = rsGetYdCarsch.getRecord();

				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, recPara);				
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

				// 카드번호
				szCARD_NO = ydDaoUtils.paraRecChkNull(recPara, "CARD_NO");

				// 차량번호
				szCAR_NO = ydDaoUtils.paraRecChkNull(recPara, "CAR_NO");

				// 야드구분
				szYD_GP	= ydDaoUtils.paraRecChkNull(inRec, "YD_GP");

				// 운송지시일자
				szTRANS_ORD_DATE 	= ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE");

				// 운송지시순번
				szTRANS_ORD_SEQNO 	= ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO");

				// 상차 개시일자 , 상차개시 시각	
				szYD_CARLD_ST_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_ST_DT").trim();				
				if(szYD_CARLD_ST_DT != "" && szYD_CARLD_ST_DT.length() == 14){
					szCARLOAD_START_DATE = szYD_CARLD_ST_DT.substring(0, 8);
					szCARLOAD_START_TIME = szYD_CARLD_ST_DT.substring(8, 14);					
				}

				//		1.	인터페이스ID				  TC_CODE					VARCHAR2(8)		YDDMR009
				//		2.	전송일시					  TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
				//		3.	카드 번호					  CARD_NO					VARCHAR2(4)
				//		4.	차량 번호					  CAR_NO					VARCHAR2(15)
				//		5.  야드구분					  YD_GP						VARCHAR2(1)
				//		6.  상차 개시 일자				  CARLOAD_START_DATE		VARCHAR2(8)
				//		7.	상차 개시 시각				  CARLOAD_START_TIME		VARCHAR2(6)
				//		8.	이송지시일자				  TRANS_WORD_DATE			VARCHAR2(8)
				//		9.	이송지시순번				  TRANS_WORD_SEQNO			VARCHAR2(4)
				outRec.setField("MQ_TC_CD"          , new String("M10YDLMJ1073"));
				outRec.setField("MQ_TC_CREATE_DDTT" , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
//				outRec.setField("CARD_NO"           , szCARD_NO);
				outRec.setField("CAR_NO"            , szCAR_NO);
				outRec.setField("YD_GP"          	, szYD_GP);
				outRec.setField("CARLOAD_START_DATE", szCARLOAD_START_DATE);
				outRec.setField("CARLOAD_START_TIME", szCARLOAD_START_TIME);
				outRec.setField("TRN_REQ_DATE"      , szTRANS_ORD_DATE);
				outRec.setField("TRN_REQ_SEQ"       , szTRANS_ORD_SEQNO);
	            outRec.setField("DIST_GOODS_GP"     , "P");
	            outRec.setField("SCH_YN"            , "N");
				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
			//} //end of for
			
		}catch(Exception e){
			szMsg = "외판슬라브출하상차개시  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR009()




	/**
	 * YDDMR012 : 후판일품출하상차실적	
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeM10YDLMJ1082(JDTORecord inRec, JDTORecordSet outRecSet){

		YdDaoUtils ydDaoUtils    = new YdDaoUtils();
		YdUtils ydUtils          = new YdUtils();

		JDTORecord outRec 		 = null;
		String szMsg             = "";
		String szMethodName      = "makeM10YDLMJ1082";
		String szOperationName   = "출하 후판일품출하상차실적";
	
		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			outRec = JDTORecordFactory.getInstance().create();
            outRec.setField("MQ_TC_CD"           , new String("M10YDLMJ1082"));
            outRec.setField("MQ_TC_CREATE_DDTT"  , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
			outRec.setField("CAR_NO"             , ydDaoUtils.paraRecChkNull(inRec, "CAR_NO"));
			outRec.setField("YD_GP"              , ydDaoUtils.paraRecChkNull(inRec, "YD_GP"));
			outRec.setField("GOODS_EA"           , ydDaoUtils.paraRecChkNull(inRec, "GOODS_EA"));
			outRec.setField("GOODS_NO"           , ydDaoUtils.paraRecChkNull(inRec, "GOODS_NO"));		
			outRec.setField("TRN_REQ_DATE"       , ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_DATE"));
			outRec.setField("TRN_REQ_SEQ"        , ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_SEQNO"));			
            outRec.setField("DIST_GOODS_GP"      , "P");
            outRec.setField("SCH_YN"             , "N");
			outRecSet.addRecord(outRec);
			
			// Debug MSG
			ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);					
			
		}catch(Exception e){
			szMsg = "후판일품출하상차실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR012()


	/**
	 * YDDMR013 : 외판슬라브일품출하상차실적
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeM10YDLMJ1083(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao  = new YdCarSchDao();
		YdDaoUtils ydDaoUtils    = new YdDaoUtils();
		YdUtils ydUtils          = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		 = null;
		JDTORecord outRec 		 = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg             = "";
		String szMethodName      = "makeM10YDLMJ1083";
		String szOperationName   = "출하 외판슬라브일품출하상차실적";
		
		// 카드번호
		String szCARD_NO 		 = "";

		// 차량번호
		String szCAR_NO			 = "";

		// 야드구분
		String szYD_GP			 = "";

		// 운송지시일자
		String szTRANS_ORD_DATE  = "";

		// 운송지시순번
		String szTRANS_ORD_SEQNO = "";

		// 제품 개수
		String szGOODS_EA        = "";

		// 제품 번호
		String szGOODS_NO		 = "";

		int intRtnVal            = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 차량스케줄 조회
			intRtnVal= ydCarSchDao.getYdCarsch(inRec, rsGetYdCarsch, 4);			
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 차량스케줄조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}

			for(int i=0; i<intRtnVal; i++){
				recPara = rsGetYdCarsch.getRecord(i);

				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, recPara);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

				// 카드번호
				szCARD_NO = ydDaoUtils.paraRecChkNull(recPara, "CARD_NO");

				// 차량번호
				szCAR_NO = ydDaoUtils.paraRecChkNull(recPara, "CAR_NO");

				// 야드구분
				szYD_GP = ydDaoUtils.paraRecChkNull(inRec, "YD_GP");

				// 운송지시일자
				szTRANS_ORD_DATE  = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE");

				// 운송지시순번
				szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO");

				// 제품 개수
				szGOODS_EA = Integer.toString(intRtnVal);
				
				// 제품 번호
				szGOODS_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

				//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR013
				//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setField("MQ_TC_CD"        , new String("M10YDLMJ1083"));
				outRec.setField("MQ_TC_CREATE_DDTT" , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));

				//		3.	카드 번호				CARD_NO					VARCHAR2(4)
				//		4.	차량 번호				CAR_NO					VARCHAR2(15)
				//		5.	YARD 구분			YD_GP					VARCHAR2(1)
				//      6.  제품 개수				GOODS_EA				NUMBER(4)
				//		7.	제품 번호				GOODS_NO				VARCHAR2(11)
				//		8.	운송지시일자			TRANS_WORD_DATE			VARCHAR2(8)
				//		9.	운송지시순번			TRANS_WORD_SEQNO		NUMBER(4)
//				outRec.setField("CARD_NO"         , szCARD_NO);
				outRec.setField("CAR_NO"          , szCAR_NO);
				outRec.setField("YD_GP"           , szYD_GP);
				outRec.setField("GOODS_EA"        , szGOODS_EA);
				outRec.setField("GOODS_NO"        , szGOODS_NO);			
				outRec.setField("TRN_REQ_DATE" , szTRANS_ORD_DATE);
				outRec.setField("TRN_REQ_SEQ", szTRANS_ORD_SEQNO);
	            outRec.setField("DIST_GOODS_GP"   , "C");
	            outRec.setField("SCH_YN"          , "N");
	            
				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);					
			}

		}catch(Exception e){
			szMsg = "외판슬라브일품출하상차실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR013()



	/**
	 * YDDMR016 : 후판출하상차완료
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeM10YDLMJ1092(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		YdUtils ydUtils           = new YdUtils();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();

		// 레코드 선언
		JDTORecord recPara 		  = null;
		JDTORecord outRec 		  = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg              = "";
		String szMethodName       = "makeM10YDLMJ1092";
		String szOperationName    = "출하 후판출하상차완료";
		int intRtnVal             = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);
			
			//해송스케줄 이후 상차 포인트 
			String szCARLD_PNT_CD = ydDaoUtils.paraRecChkNull(inRec, "CARLD_PNT_CD");

			// 차량스케줄 조회
			intRtnVal= ydCarSchDao.getYdCarsch(inRec, rsGetYdCarsch, 0);
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 차량스케줄조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}

			// 차량스케줄 조회결과 추출
			//for(int i=0; i<intRtnVal ; i++){
			outRec = JDTORecordFactory.getInstance().create();
			rsGetYdCarsch.first();
			recPara = rsGetYdCarsch.getRecord();

			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, recPara);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);
			
			// 차량번호
			String szCAR_NO           = ydDaoUtils.paraRecChkNull(recPara, "CAR_NO");
			String szTRANS_ORD_DATE   = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE");
			String szTRANS_ORD_SEQNO  = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO");
			String szYD_CARLD_CMPL_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_CMPL_DT").trim();
			String szCARLOAD_END_DATE = (YdUtils.getCurDate("yyyyMMddHHmmss")).substring(0, 8);
			String szCARLOAD_END_TIME = (YdUtils.getCurDate("yyyyMMddHHmmss")).substring(8, 14);	

			outRec.setField("MQ_TC_CD"           , new String("M10YDLMJ1092"));
			outRec.setField("MQ_TC_CREATE_DDTT"  , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
			outRec.setField("CAR_NO"             , szCAR_NO);
			outRec.setField("CARLOAD_END_DATE"   , szCARLOAD_END_DATE);
			outRec.setField("CARLOAD_END_TIME"   , szCARLOAD_END_TIME);
			outRec.setField("TRN_REQ_DATE"       , szTRANS_ORD_DATE);
			outRec.setField("TRN_REQ_SEQ"        , szTRANS_ORD_SEQNO);
            outRec.setField("DIST_GOODS_GP"      , "P");
            outRec.setField("YARD_GP"            , "");
            outRec.setField("SCH_YN"             , "N");
            
			// RecordSet으로 반환
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	

		}catch(Exception e){
			szMsg = "후판출하상차완료  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR016()


	/**
	 * YDDMR017 : 외판슬라브출하상차완료
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeM10YDLMJ1093(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();
		YdUtils ydUtils           = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		  = null;
		JDTORecord outRec 		  = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg              = "";
		String szMethodName       = "makeM10YDLMJ1093";
		String szOperationName    = "출하 외판슬라브출하상차완료";
		
		// 카드번호
		String szCARD_NO 		  = "";

		// 차량번호
		String szCAR_NO			  = "";

		// 야드구분
		String szYD_GP			  = "";

		// 운송지시일자
		String szTRANS_ORD_DATE   = "";

		// 운송지시순번
		String szTRANS_ORD_SEQNO  = "";

		// 상차완료일자및시각 (야드상차완료일시)
		String szYD_CARLD_CMPL_DT = "";
		String szCARLOAD_END_DATE = "";
		String szCARLOAD_END_TIME = "";

		int intRtnVal             = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 차량스케줄 조회
			intRtnVal= ydCarSchDao.getYdCarsch(inRec, rsGetYdCarsch, 4);			
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 차량스케줄조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}

			// 차량스케줄 조회결과 추출
			//for(int i=0; i<intRtnVal; i++){
				outRec = JDTORecordFactory.getInstance().create();
				rsGetYdCarsch.first();
				recPara = rsGetYdCarsch.getRecord();

				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, recPara);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

				// 카드번호
				szCARD_NO = ydDaoUtils.paraRecChkNull(recPara, "CARD_NO");

				// 차량번호
				szCAR_NO = ydDaoUtils.paraRecChkNull(recPara, "CAR_NO");

				// 야드구분
				szYD_GP	= ydDaoUtils.paraRecChkNull(inRec, "YD_GP");

				// 운송지시일자
				szTRANS_ORD_DATE  = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE");

				// 운송지시순번
				szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO");

				// 상차 완료 일자 , 상차 완료 시각	
				szYD_CARLD_CMPL_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_CMPL_DT").trim();				
				if(szYD_CARLD_CMPL_DT != "" && szYD_CARLD_CMPL_DT.length() == 14){
					szCARLOAD_END_DATE = szYD_CARLD_CMPL_DT.substring(0, 8);
					szCARLOAD_END_TIME = szYD_CARLD_CMPL_DT.substring(8, 14);
				}

				//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR017
				//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
				outRec.setField("MQ_TC_CD"         , new String("M10YDLMJ1093"));
				outRec.setField("MQ_TC_CREATE_DDTT"  , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));

				//		3.	카드 번호				CARD_NO					VARCHAR2(4)
				//		4.	차량 번호				CAR_NO					VARCHAR2(15)
				//		5.	YARD 구분			YD_GP					VARCHAR2(1)
				//      6.  상차완료 일자			CARLOAD_END_DATE		VARCHAR2(8)
				//		7.	상차완료  시각			CARLOAD_END_TIME		VARCHAR2(6)
				//		8.	운송지시일자			TRANS_WORD_DATE			VARCHAR2(8)
				//		9.	운송지시순번			TRANS_WORD_SEQNO		NUMBER(4)
//				outRec.setField("CARD_NO"         , szCARD_NO);
				outRec.setField("CAR_NO"          , szCAR_NO);
				outRec.setField("YD_GP"           , szYD_GP);
				outRec.setField("CARLD_CMPL_DATE" , szCARLOAD_END_DATE);
				outRec.setField("CARLD_CMPL_TIME" , szCARLOAD_END_TIME);
				outRec.setField("TRN_REQ_DATE"    , szTRANS_ORD_DATE);
				outRec.setField("TRN_REQ_SEQ"     , szTRANS_ORD_SEQNO);
				outRec.setField("DIST_GOODS_GP"   , "C");
				outRec.setField("SCH_YN"          , "N");
		            
				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
			//}
		}catch(Exception e){
			szMsg = "외판슬라브출하상차완료  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR017()





	/**
	 * YDDMR020 : 임가공이송상하차개시
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR020(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao     = new YdCarSchDao();
		YdUtils ydUtils             = new YdUtils();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();

		// 레코드 선언
		JDTORecord recPara 		    = null;
		JDTORecord outRec 		    = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg                = "";
		String szMethodName         = "makeDMR020";
		String szOperationName      = "출하 임가공이송상하차개시";
		
		// 상하차구분
		String szUPCARUNLOAD_GP     = "";

		// 카드번호
		String szCARD_NO 		    = "";

		// 차량번호
		String szCAR_NO			    = "";

		// 야드구분
		String szYD_GP		     	= "";

		// 운송지시일자
		String szTRANS_ORD_DATE 	= "";

		// 운송지시순번
		String szTRANS_ORD_SEQNO	= "";

		// 상차개시일자및시각 (야드상차개시일시)
		String szYD_CARLD_ST_DT	    = "";

		// 하차개시일자및시각 (야드하차개시일시)
		String szYD_CARUD_ST_DT	    = "";
		String szCARLOAD_START_DATE = "";
		String szCARLOAD_START_TIME = "";
		String szYD_CAR_PROG_STAT	="";
		int intRtnVal               = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 차량스케줄 조회
			intRtnVal= ydCarSchDao.getYdCarsch(inRec, rsGetYdCarsch, 0);
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 차량스케줄조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}

//***********************************************************************************************************************************************//
			// 2009.07.24 수정 한건만 내려가도록 KOC
			
//			for(int i=0; i<intRtnVal; i++){				
				outRec = JDTORecordFactory.getInstance().create();
				rsGetYdCarsch.first();
				recPara = rsGetYdCarsch.getRecord();

				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, recPara);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

				// 카드번호
				szCARD_NO = ydDaoUtils.paraRecChkNull(recPara, "CARD_NO");

				// 차량번호
				szCAR_NO = ydDaoUtils.paraRecChkNull(recPara, "CAR_NO");

				// 야드구분
				szYD_GP	= ydDaoUtils.paraRecChkNull(inRec, "YD_GP");

				// 운송지시일자
				szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE");

				// 운송지시순번
				szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO");

				// 하차 개시일자
				szYD_CARUD_ST_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_ST_DT").trim();

				// 상차 개시일자
				szYD_CARLD_ST_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_ST_DT").trim();
				
				//야드차량진행상태		
				szYD_CAR_PROG_STAT	= ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_PROG_STAT");
				
				if(szYD_CAR_PROG_STAT.equals("3") ||szYD_CAR_PROG_STAT.equals("4") ||szYD_CAR_PROG_STAT.equals("5")){
//					 상하차 구분  :상차(U), 하차(D)
					szUPCARUNLOAD_GP = "U";
				}else{
//					 상하차 구분  :상차(U), 하차(D)
					szUPCARUNLOAD_GP = "D";
				}

				// 상하차 구분이 'U': 상차일 때
				if(szUPCARUNLOAD_GP.equals("U")){

					// 상차 개시일자 , 상차개시 시각	
					if(szYD_CARLD_ST_DT != "" && szYD_CARLD_ST_DT.length() == 14){
						szCARLOAD_START_DATE = szYD_CARLD_ST_DT.substring(0, 8);
						szCARLOAD_START_TIME = szYD_CARLD_ST_DT.substring(8, 14);
					}
				}

				// 상하차 구분이 'D': 하차일 때
				else if(szUPCARUNLOAD_GP.equals("D")){

					// 하차 개시 일자, 하차 개시 시각
					if(szYD_CARUD_ST_DT != "" && szYD_CARUD_ST_DT.length() == 14){
						szCARLOAD_START_DATE = szYD_CARUD_ST_DT.substring(0, 8);
						szCARLOAD_START_TIME = szYD_CARUD_ST_DT.substring(8, 14);						
					}
				}

				//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR020
				//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
				outRec.setField("TC_CODE"           , new String("YDDMR020"));
				outRec.setField("TC_CREATE_DDTT"    , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));

				//		7.	상하차 구분			UPCARUNLOAD_GP			VARCHAR2(1)
				//		8.	카드 번호				CARD_NO					VARCHAR2(4)
				//		9.	차량 번호				CAR_NO					VARCHAR2(15)
				//		10. YARD 구분			YD_GP					VARCHAR2(1)
				//		11. 상차 개시 일자			CARLOAD_START_DATE		VARCHAR2(8)
				//		12.	상차 개시 시각			CARLOAD_START_TIME		VARCHAR2(6)
				//		13.	이송지시일자			TRANS_WORD_DATE			VARCHAR2(8)
				//		14.	이송지시순번			TRANS_WORD_SEQNO		NUMBER(4)
				outRec.setField("UPCARUNLOAD_GP"    , szUPCARUNLOAD_GP);
				outRec.setField("CARD_NO"           , szCARD_NO);
				outRec.setField("CAR_NO"            , szCAR_NO);
				outRec.setField("YD_GP"             , szYD_GP);
				outRec.setField("CARLOAD_START_DATE", szCARLOAD_START_DATE);
				outRec.setField("CARLOAD_START_TIME", szCARLOAD_START_TIME);
				outRec.setField("TRANS_WORD_DATE"   , szTRANS_ORD_DATE);
				outRec.setField("TRANS_WORD_SEQNO"  , szTRANS_ORD_SEQNO);

				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
//			}
//***********************************************************************************************************************************************//
				
		}catch(Exception e){
			szMsg = "임가공이송상하차개시  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR020()




	/**
	 * YDDMR022 : 임가공이송상하차완료
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR022(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();
		YdUtils ydUtils           = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		  = null;
		JDTORecord outRec 		  = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg              = "";
		String szMethodName       = "makeDMR022";
		String szOperationName    = "출하 임가공이송상하차완료";
		
		// 상하차구분
		String szUPCARUNLOAD_GP   = "";

		// 카드번호
		String szCARD_NO 		  = "";

		// 차량번호
		String szCAR_NO			  = "";

		// 야드구분
		String szYD_GP			  = "";

		// 운송지시일자
		String szTRANS_ORD_DATE   = "";

		// 운송지시순번
		String szTRANS_ORD_SEQNO  = "";

		// 상차완료일자및시각 (야드상차완료일시)
		String szYD_CARLD_CMPL_DT = "";

		// 하차완료일자및시각 (야드하차완료일시)
		String szYD_CARUD_CMPL_DT = "";
		String szCARLOAD_DONE_DATE = "";
		String szCARLOAD_DONE_TIME = "";
		String szYD_CAR_PROG_STAT = "";
		int intRtnVal             = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 차량스케줄 조회
			intRtnVal= ydCarSchDao.getYdCarsch(inRec, rsGetYdCarsch, 0);			
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 차량스케줄조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}

			// 차량스케줄 조회결과 추출
//			for(int i=0; i<intRtnVal; i++){
			
				outRec = JDTORecordFactory.getInstance().create();
				rsGetYdCarsch.first();
				recPara = rsGetYdCarsch.getRecord();

				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, recPara);	
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

				// 카드번호
				szCARD_NO = ydDaoUtils.paraRecChkNull(recPara, "CARD_NO");

				// 차량번호
				szCAR_NO = ydDaoUtils.paraRecChkNull(recPara, "CAR_NO");

				// 야드구분
				szYD_GP	= ydDaoUtils.paraRecChkNull(inRec, "YD_GP");

				// 운송지시일자
				szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE");

				// 운송지시순번
				szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO");

				// 하차 완료일자
				szYD_CARUD_CMPL_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_CMPL_DT").trim();

				// 상차 완료일자
				szYD_CARLD_CMPL_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_CMPL_DT").trim();

				//야드차량진행상태		
				szYD_CAR_PROG_STAT	= ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_PROG_STAT");
				
				if(szYD_CAR_PROG_STAT.equals("3") ||szYD_CAR_PROG_STAT.equals("4") ||szYD_CAR_PROG_STAT.equals("5")){
//					 상하차 구분  :상차(U), 하차(D)
					szUPCARUNLOAD_GP = "U";
				}else{
//					 상하차 구분  :상차(U), 하차(D)
					szUPCARUNLOAD_GP = "D";
				}
				
				// 상하차 구분이 'U': 상차일 때
				if(szUPCARUNLOAD_GP.equals("U")){
					// 상차 완료 일자 , 상차 완료 시각	
					if(szYD_CARLD_CMPL_DT != "" && szYD_CARLD_CMPL_DT.length() == 14){
						szCARLOAD_DONE_DATE = szYD_CARLD_CMPL_DT.substring(0, 8);
						szCARLOAD_DONE_TIME = szYD_CARLD_CMPL_DT.substring(8, 14);
					}
				}

				// 상하차 구분이 'D': 하차일 때
				else if(szUPCARUNLOAD_GP.equals("D")){
					// 하차 완료 일자, 하차 완료 시각
					if(szYD_CARUD_CMPL_DT != "" && szYD_CARUD_CMPL_DT.length() == 14){
						szCARLOAD_DONE_DATE = szYD_CARUD_CMPL_DT.substring(0, 8);
						szCARLOAD_DONE_TIME = szYD_CARUD_CMPL_DT.substring(8, 14);						
					}
				}

				//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR022
				//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
				outRec.setField("TC_CODE"         , new String("YDDMR022"));
				outRec.setField("TC_CREATE_DDTT"  , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));

				//		7.	상하차 구분			UPCARUNLOAD_GP			VARCHAR2(1)
				//		8.	카드 번호				CARD_NO					VARCHAR2(4)
				//		9.	차량 번호				CAR_NO					VARCHAR2(15)
				//		12.	YARD 구분			YD_GP					VARCHAR2(1)
				//		13.	상차완료 일자			CARLOAD_DONE_DATE		VARCHAR2(8)
				//		14.	상차완료  시각			CARLOAD_DONE_TIME		VARCHAR2(6)
				//		10.	이송지시일자			TRANS_WORD_DATE			VARCHAR2(8)
				//		11.	이송지시순번			TRANS_WORD_SEQNO		NUMBER(4)
				outRec.setField("UPCARUNLOAD_GP"    , szUPCARUNLOAD_GP);
				outRec.setField("CARD_NO"           , szCARD_NO);
				outRec.setField("CAR_NO"            , szCAR_NO);
				outRec.setField("YD_GP"             , szYD_GP);
				outRec.setField("CARLOAD_DONE_DATE" , szCARLOAD_DONE_DATE);
				outRec.setField("CARLOAD_DONE_TIME" , szCARLOAD_DONE_TIME);
				outRec.setField("TRANS_WORD_DATE"   , szTRANS_ORD_DATE);
				outRec.setField("TRANS_WORD_SEQNO"  , szTRANS_ORD_SEQNO);
				
				//RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
//			}
		}catch(Exception e){
			szMsg = "임가공이송상하차완료  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR022()
	
	
	

	
	/**
	 * YDDMR026 : 포인트사용실적(20090714추가)
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR026(JDTORecord inRec, JDTORecordSet outRecSet){
		YdUtils ydUtils        = new YdUtils();
		YdStkColDao YdStkColDao = new YdStkColDao();
		YdDaoUtils ydDaoUtils   = new YdDaoUtils();
		JDTORecord 	outRec      = null;
		JDTORecord	recPara		= null;
		String szMsg           = "";
		String szMethodName    = "makeDMR026";
		String szOperationName = "출하 포인트사용실적";
		String szYD_STK_COL_ACT_STAT ="";
		String szYD_PNT_UNIT_CL_GP ="";
		String szCARD_NO ="";
		JDTORecordSet rsGetYdCrnsch = JDTORecordFactory.getInstance().createRecordSet("");
		int intRtnVal=0;
		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);
			
//			 크레인스케줄 조회
			intRtnVal =YdStkColDao.getYdStkcol(inRec, rsGetYdCrnsch, 0) ;
			if(intRtnVal <= 0){				
				ydUtils.putLog(szSessionName, szMethodName, "출하 포인트사용실적 조회  오류 :: ["+intRtnVal+"]", YdConstant.ERROR);
				return 0;
			}
			
			recPara = rsGetYdCrnsch.getRecord(0);
			
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, recPara);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);
			
			
			szYD_STK_COL_ACT_STAT =ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_ACT_STAT");

			if(szYD_STK_COL_ACT_STAT.equals("C")){
				szYD_PNT_UNIT_CL_GP ="O";
			} else {
				szYD_PNT_UNIT_CL_GP ="C";
			}
			
			szCARD_NO = ydDaoUtils.paraRecChkNull(recPara, "CARD_NO");
			if(szCARD_NO.equals("")|| szCARD_NO == null){
				szCARD_NO ="0000"; //출하에서 값이 없을 경우 예외 처리 함 따라서 0000으로 넣어 줌
			}
			
			//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR026
			//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("TC_CODE", 				new String("YDDMR026"));
			outRec.setField("TC_CREATE_DDTT", 		new String(YdUtils.getCurDate("yyyyMMddHHmmss")));

			//		7.	개소코드				WLOC_CD					VARCHAR2(5)
			//		8.	야드포인트코드			YD_PNT_CD				VARCHAR2(4)
			//		9.	야드포인트점유구분		YD_PNT_OCPY_GP			VARCHAR2(1) 	A:구내포인트,B: 출하포인트
			//		10.	야드포인트개폐구분		YD_PNT_UNIT_CL_GP		VARCHAR2(1) 	O: Open, C: Close
			//		11.	차입인출가능여부		LOAN_PULLOUT_ABLE_YN	VARCHAR2(1)		Y,N
			//		12.	점유운송장비코드		OCPY_TRN_EQP_CD			VARCHAR2(8)
			outRec.setField("WLOC_CD"    			, ydDaoUtils.paraRecChkNull(recPara, "WLOC_CD"));
			outRec.setField("YD_PNT_CD"         	, ydDaoUtils.paraRecChkNull(recPara, "YD_PNT_CD"));
			outRec.setField("YD_PNT_OCPY_GP"    	, "B");
			outRec.setField("YD_PNT_UNIT_CL_GP" 	, szYD_PNT_UNIT_CL_GP);
			outRec.setField("LOAN_PULLOUT_ABLE_YN" 	, "Y");
			outRec.setField("OCPY_TRN_EQP_CD" 		, szCARD_NO);
			
			//RecordSet으로 반환
			outRecSet.addRecord(outRec);
			
			ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
			
		}catch(Exception e){
			
			szMsg = "포인트사용실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR026()

	
	
	/**
	 * YDDMR028 : 후판차량입동지시
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeM10YDLMJ1062(JDTORecord inRec, JDTORecordSet outRecSet){

		YdUtils ydUtils        = new YdUtils();
		YdDaoUtils ydDaoUtils  = new YdDaoUtils();	
		YdCarSchDao ydCarSchDao 	 = new YdCarSchDao();
		JDTORecordSet rsResult = null; 
		
		JDTORecord outRec      = null;
		JDTORecord recPara     = null;
		JDTORecord recTemp     = null;
		
		String szMsg           = "";
		String szMethodName    = "makeM10YDLMJ1062";
		String szOperationName = "출하 차량입동지시";
		String szYD_CARPNT_CD  = null;
		String szYD_PNT_CD	   = null;
		String szWLOC_CD	   = null;
		String szTRANS_EQUIPMENT_TYPE	= "";
		String szCR_FRTOMOVE_GP= "";
		String szSPOS_WLOC_CD= "";
		
		int nRet               = 0;
		
		YdPlateCommDAO commDao = new YdPlateCommDAO(); 
		YdStockDao ydStockDao  = new YdStockDao();
		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);
			
			
			szYD_CARPNT_CD = ydDaoUtils.paraRecChkNull(inRec, "YD_CARPNT_CD");
			szYD_PNT_CD	   = ydDaoUtils.paraRecChkNull(inRec, "YD_PNT_CD");
			szWLOC_CD	   = ydDaoUtils.paraRecChkNull(inRec, "WLOC_CD");
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("CAR_NO"  , ydDaoUtils.paraRecChkNull(inRec, "CAR_NO"));
//			recPara.setField("CARD_NO" , ydDaoUtils.paraRecChkNull(inRec, "CARD_NO"));
			
			/* com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarNoCardNo_PIDEV */
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	"T");					
			nRet = ydCarSchDao.getYdCarsch(recPara, rsResult, 11);
			if(nRet > 0){
				rsResult.first();
				recPara = rsResult.getRecord();
				szTRANS_EQUIPMENT_TYPE = ydDaoUtils.paraRecChkNull(recPara,"TRANS_EQUIPMENT_TYPE");
				szSPOS_WLOC_CD         = ydDaoUtils.paraRecChkNull(recPara,"SPOS_WLOC_CD");
			}	
			
			if(YdConstant.WLOC_CD_PLATE_GDS_YARD.equals(szWLOC_CD)||YdConstant.WLOC_CD_PLATE2_GDS_YARD.equals(szWLOC_CD) || YdConstant.WLOC_CD_A_PLATE_PLANT.equals(szWLOC_CD)) {
				//1,2 후판 개소코드인 경우..
				
				if("".equals(szYD_CARPNT_CD) && !"".equals(szYD_PNT_CD)) {
					//---------------------------------------------------------------------------------
					//	 YD_CARPNT_CD 가 ""이면 YD_PNT_CD 로 TB_YD_CARPOINT 테이블에서 조회하여 셋팅한다.
					//---------------------------------------------------------------------------------
					JDTORecordSet rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
					recPara 	= JDTORecordFactory.getInstance().create();
					recPara.setField("YD_PNT_CD" , szYD_PNT_CD);
					recPara.setField("WLOC_CD"   , szWLOC_CD);
					
					int intRtnVal = commDao.select(recPara, rsResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0088");
					if(intRtnVal > 0){
						
						rsResult1.first();
						recPara = rsResult1.getRecord();
						szYD_CARPNT_CD = ydDaoUtils.paraRecChkNull(recPara,"YD_CARPNT_CD");
					}	
				}
			}
			
			 
			
			// 레코드생성-----------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DATE"  , ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_DATE"));
			recPara.setField("TRANS_ORD_SEQNO" , ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_SEQNO"));

			// (운송일자, 운송순번)로 저장품 조회(운송일자, 운송순번)
			if("D2Y44".equals(szSPOS_WLOC_CD)||"D2Y45".equals(szSPOS_WLOC_CD)||"D3Y41".equals(szSPOS_WLOC_CD)||"D3Y42".equals(szSPOS_WLOC_CD) ){
				/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDateAB*/
				nRet = ydStockDao.getYdStock(recPara, rsResult, 731);
			}else{
				/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDate*/
				nRet = ydStockDao.getYdStock(recPara, rsResult, 730);
			}
			if(nRet > 0){
				rsResult.first();
				recTemp				= rsResult.getRecord();			
				szCR_FRTOMOVE_GP	= ydDaoUtils.paraRecChkNull(recTemp, "CR_FRTOMOVE_GP");
			}
			
			// DB조회 
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MQ_TC_CD"             , new String("M10YDLMJ1062"));
			outRec.setField("MQ_TC_CREATE_DDTT"    , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
			outRec.setField("CAR_NO"               , ydDaoUtils.paraRecChkNull(inRec, "CAR_NO"));
			
			//25.07.24 니켈강 출하 추가 허동수 책임 요청 -- RITM1291473
			if("DKY23".equals(szWLOC_CD)){
				outRec.setField("YD_GP"                , "P");
			}
			else{
				outRec.setField("YD_GP"                , "T");
			}
			outRec.setField("TRN_REQ_DATE"         , ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_DATE"));
			outRec.setField("TRN_REQ_SEQ"          , ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_SEQNO"));
			outRec.setField("BAYIN_DDTT"           , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
			outRec.setField("LOAN_PULLOUT_ABLE_YN" , ydDaoUtils.paraRecChkNull(inRec, "LOAN_PULLOUT_ABLE_YN"));
			outRec.setField("WLOC_CD"              , szWLOC_CD);
			outRec.setField("YD_PNT_CD"            , szYD_PNT_CD);
            outRec.setField("DIST_GOODS_GP"        , "P");
            outRec.setField("SCH_YN"               , "N");
            outRec.setField("YD_CARPNT_CD"         , szYD_CARPNT_CD);
            
			
			
			//RecordSet으로 반환
			outRecSet.addRecord(outRec);
			
			ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
		}catch(Exception e){
			szMsg = "차량입동지시 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR028()
	
	/**
	 * M10YDLMJ1162 : 후판차량 선입동지시
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeM10YDLMJ1162(JDTORecord inRec, JDTORecordSet outRecSet){

		YdUtils ydUtils        = new YdUtils();
		YdDaoUtils ydDaoUtils  = new YdDaoUtils();	
		YdCarSchDao ydCarSchDao 	 = new YdCarSchDao();
		JDTORecordSet rsResult = null; 
		
		JDTORecord outRec      = null;
		JDTORecord recPara     = null;
		JDTORecord recTemp     = null;
		
		String szMsg           = "";
		String szMethodName    = "makeM10YDLMJ1162";
		String szOperationName = "출하 차량 선입동지시";
		String szYD_CARPNT_CD  = null;
		String szYD_PNT_CD	   = null;
		String szWLOC_CD	   = null;
		String szTRANS_EQUIPMENT_TYPE	= "";
		String szCR_FRTOMOVE_GP= "";
		String szSPOS_WLOC_CD= "";
		
		int nRet               = 0;
		
		YdPlateCommDAO commDao = new YdPlateCommDAO(); 
		YdStockDao ydStockDao  = new YdStockDao();
		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);
			
			
			szYD_CARPNT_CD = ydDaoUtils.paraRecChkNull(inRec, "YD_CARPNT_CD");
			szYD_PNT_CD	   = ydDaoUtils.paraRecChkNull(inRec, "YD_PNT_CD");
			szWLOC_CD	   = ydDaoUtils.paraRecChkNull(inRec, "WLOC_CD");
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("CAR_NO"  , ydDaoUtils.paraRecChkNull(inRec, "CAR_NO"));
//			recPara.setField("CARD_NO" , ydDaoUtils.paraRecChkNull(inRec, "CARD_NO"));
			
			/* com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarNoCardNo_PIDEV */
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	"T");					
			nRet = ydCarSchDao.getYdCarsch(recPara, rsResult, 11);
			if(nRet > 0){
				rsResult.first();
				recPara = rsResult.getRecord();
				szTRANS_EQUIPMENT_TYPE = ydDaoUtils.paraRecChkNull(recPara,"TRANS_EQUIPMENT_TYPE");
				szSPOS_WLOC_CD         = ydDaoUtils.paraRecChkNull(recPara,"SPOS_WLOC_CD");
			}	
			
			if(YdConstant.WLOC_CD_PLATE_GDS_YARD.equals(szWLOC_CD)||YdConstant.WLOC_CD_PLATE2_GDS_YARD.equals(szWLOC_CD)) {
				//1,2 후판 개소코드인 경우..
				
				if("".equals(szYD_CARPNT_CD) && !"".equals(szYD_PNT_CD)) {
					//---------------------------------------------------------------------------------
					//	 YD_CARPNT_CD 가 ""이면 YD_PNT_CD 로 TB_YD_CARPOINT 테이블에서 조회하여 셋팅한다.
					//---------------------------------------------------------------------------------
					JDTORecordSet rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
					recPara 	= JDTORecordFactory.getInstance().create();
					recPara.setField("YD_PNT_CD" , szYD_PNT_CD);
					recPara.setField("WLOC_CD"   , szWLOC_CD);
					
					int intRtnVal = commDao.select(recPara, rsResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0088");
					if(intRtnVal > 0){
						
						rsResult1.first();
						recPara = rsResult1.getRecord();
						szYD_CARPNT_CD = ydDaoUtils.paraRecChkNull(recPara,"YD_CARPNT_CD");
					}	
				}
			}
			
			 
			
			// 레코드생성-----------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DATE"  , ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_DATE"));
			recPara.setField("TRANS_ORD_SEQNO" , ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_SEQNO"));

			// (운송일자, 운송순번)로 저장품 조회(운송일자, 운송순번)
			if("D2Y44".equals(szSPOS_WLOC_CD)||"D2Y45".equals(szSPOS_WLOC_CD)||"D3Y41".equals(szSPOS_WLOC_CD)||"D3Y42".equals(szSPOS_WLOC_CD) ){
				/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDateAB*/
				nRet = ydStockDao.getYdStock(recPara, rsResult, 731);
			}else{
				/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDate*/
				nRet = ydStockDao.getYdStock(recPara, rsResult, 730);
			}
			if(nRet > 0){
				rsResult.first();
				recTemp				= rsResult.getRecord();			
				szCR_FRTOMOVE_GP	= ydDaoUtils.paraRecChkNull(recTemp, "CR_FRTOMOVE_GP");
			}
			
			// DB조회 
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MQ_TC_CD"             , new String("M10YDLMJ1162"));
			outRec.setField("MQ_TC_CREATE_DDTT"    , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
			outRec.setField("CAR_NO"               , ydDaoUtils.paraRecChkNull(inRec, "CAR_NO"));
			outRec.setField("YD_GP"                , "T");
			outRec.setField("TRN_REQ_DATE"         , ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_DATE"));
			outRec.setField("TRN_REQ_SEQ"          , ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_SEQNO"));
			outRec.setField("BAYIN_DDTT"           , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
			outRec.setField("LOAN_PULLOUT_ABLE_YN" , ydDaoUtils.paraRecChkNull(inRec, "LOAN_PULLOUT_ABLE_YN"));
			outRec.setField("WLOC_CD"              , szWLOC_CD);
			outRec.setField("YD_PNT_CD"            , szYD_PNT_CD);
            outRec.setField("DIST_GOODS_GP"        , "P");
            outRec.setField("SCH_YN"               , "N");
            outRec.setField("YD_CARPNT_CD"         , szYD_CARPNT_CD);
            
			
			
			//RecordSet으로 반환
			outRecSet.addRecord(outRec);
			
			ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
		}catch(Exception e){
			szMsg = "차량입동지시 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of M10YDLMJ1162


	/**
	 * YDDMR014 : 임가공일품출하상차실적	
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR014(JDTORecord inRec, JDTORecordSet outRecSet){
		YdUtils ydUtils        = new YdUtils();

		JDTORecord outRec      = null;

		String szMsg           = "";
		String szMethodName    = "makeDMR014";
		String szOperationName = "출하 임가공일품출하상차실적";
		int intRtnVal          = 0;

		try{
			//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR014
			//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
			outRec.setField("TC_CODE", 				new String("YDDMR014"));
			outRec.setField("TC_CREATE_DDTT", 		new String(YdUtils.getCurDate("yyyyMMddHHmmss")));

			//		7.	상하차 구분			UPCARUNLOAD_GP			VARCHAR2(1)
			//		8.	카드 번호				CARD_NO					VARCHAR2(4)
			//		9.	차량 번호				CAR_NO					VARCHAR2(15)
			//		10.	운송지시일자			TRANS_WORD_DATE			VARCHAR2(8)
			//		11.	운송지시순번			TRANS_WORD_SEQNO		VARCHAR2(4)
			//		12.	YARD 구분			YARD_GP					VARCHAR2(1)
			//		13.	제품 개수				GOODS_EA				NUMBER(4,0)
		}catch(Exception e){
			szMsg = "임가공일품출하상차실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return intRtnVal;
	} // end of makeDMR014()


	/**
	 * YDDMR018 : 임가공출하상차완료
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR018(JDTORecord inRec, JDTORecordSet outRecSet){
		//차량스케줄Dao 객체 생성
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		YdUtils ydUtils             = new YdUtils();
		YdDBAssist ydDBAssist       = new YdDBAssist();

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		//레코드 선언
		JDTORecord recPara 		    = null;
		JDTORecord outRec 		    = null;

		String szQuery              = "";
		String szMsg                = "";
		String szMethodName         = "makeDMR018";
		String szOperationName      = "출하 임가공출하상차완료";
		
		//조회할 차량스케줄id ->추후삭제
		String szYD_CAR_SCH_ID      = "";

		//카드번호
		String szCARD_NO 	     	= "";

		//차량번호
		String szCAR_NO			    = "";

		//야드구분
		String szYD_GP			    = "";

		//운송지시일자
		String szTRANS_ORD_DATE 	= "";

		//운송지시순번
		String szTRANS_ORD_SEQNO	= "";

		//상차완료일자및시각 (야드상차완료일시)
		String szYD_CARLD_CMPL_DT	= "";

		String szCARLOAD_END_DATE   = "";
		String szCARLOAD_END_TIME   = "";

		int intRtnVal               = 0;
		
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		JDTORecord recTempPara 		    = null;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(inRec, "YD_CAR_SCH_ID");

			
			/* 쿼리수정_요청
			szQuery ="SELECT 	D.* ";
			szQuery+="   		,C.FRTOMOVE_ORD_DATE ";
			szQuery+="   		,C.FRTOMOVE_PLANT_GP ";
			szQuery+="   		,TO_CHAR(C.TRANS_ORD_DATE, 'YYYYMMDDHH24MISS') AS TRANS_ORD_DATE";
			szQuery+="   		,C.TRANS_ORD_SEQNO ";
			szQuery+="   FROM 	TB_YD_STOCK C ";
			szQuery+="    	,( SELECT   B.YD_CAR_SCH_ID AS YD_CAR_SCH_ID ";
			szQuery+="                	,B.STL_NO AS STL_NO ";
			szQuery+="                	,B.YD_CAR_UPP_LOC_CD AS YD_CAR_UPP_LOC_CD ";
			szQuery+="                	,B.YD_STK_BED_NO AS YD_STK_BED_NO ";
			szQuery+="                	,B.YD_STK_LYR_NO AS YD_STK_LYR_NO ";
			szQuery+="                  ,A.CAR_NO AS CAR_NO ";
			szQuery+="                  ,A.YD_EQP_ID AS YD_EQP_ID ";
			szQuery+="                	,TO_CHAR(A.YD_CARLD_ST_DT , 'YYYYMMDDHH24MISS')AS YD_CARLD_ST_DT ";
			szQuery+="                	,TO_CHAR(A.YD_CARLD_CMPL_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_CMPL_DT ";
			szQuery+="               	,TO_CHAR(A.YD_CARUD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT ";
			szQuery+="                	,TO_CHAR(A.YD_CARUD_CMPL_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_CMPL_DT ";
			szQuery+="                	,A.CARD_NO AS CARD_NO ";
			szQuery+="                	,A.YD_CARLD_STOP_LOC AS YD_CARLD_STOP_LOC ";
			szQuery+="                	,A.YD_CARUD_STOP_LOC AS YD_CARUD_STOP_LOC ";
			szQuery+="         	 FROM    TB_YD_CARSCH A ";
			szQuery+="                	,TB_YD_CARFTMVMTL B ";
			szQuery+="            WHERE  	A.YD_CAR_SCH_ID ='"+szYD_CAR_SCH_ID+"'";
			szQuery+="              AND 	A.YD_CAR_SCH_ID =B.YD_CAR_SCH_ID   ) D ";   
			szQuery+=" WHERE 	C.STL_NO = D.STL_NO  ";

			ydDBAssist.getData(szQuery, rsGetYdCarsch, null) ;
			*/
			
			recTempPara =  JDTORecordFactory.getInstance().create();
			recTempPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);

			ydCarFtmvMtlDao.getYdCarftmvmtl(recTempPara, rsGetYdCarsch, 7);

//			조회 결과를 담을 RecordSet생성
//			JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");
//			차량스케줄 조회
//			ydCarSchDao.getYdCarsch(inRec, rsGetYdCarsch, 1);
			//차량스케줄 조회결과 추출

			for(int i=0; i<intRtnVal; i++){
				outRec = JDTORecordFactory.getInstance().create();
				recPara = rsGetYdCarsch.getRecord(i);

				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, recPara);	
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

				//카드번호
				szCARD_NO = ydDaoUtils.paraRecChkNull(recPara, "CARD_NO");

				//차량번호
				szCAR_NO = ydDaoUtils.paraRecChkNull(recPara, "CAR_NO");

				//야드구분
				szYD_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_GP");

				//운송지시일자
				szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE");

				//운송지시순번
				szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO");

				//상차 완료 일자 , 상차 완료 시각	
				szYD_CARLD_CMPL_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_CMPL_DT").trim();				
				if(szYD_CARLD_CMPL_DT != "" && szYD_CARLD_CMPL_DT.length() == 14){
					szCARLOAD_END_DATE = szYD_CARLD_CMPL_DT.substring(0, 8);
					szCARLOAD_END_TIME = szYD_CARLD_CMPL_DT.substring(8, 14);
				}

				//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR022
				//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
				outRec.setField("TC_CODE"         , new String("YDDMR018"));
				outRec.setField("TC_CREATE_DDTT"  , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));

				//		3.	카드 번호				CARD_NO					VARCHAR2(4)
				//		4.	차량 번호				CAR_NO					VARCHAR2(15)
				//		5.	YARD 구분			YARD_GP					VARCHAR2(1)
				//      6.  상차완료 일자			CARLOAD_END_DATE		VARCHAR2(8)
				//		7.	상차완료  시각			CARLOAD_END_TIME		VARCHAR2(6)
				//		8.	운송지시일자			TRANS_WORD_DATE			VARCHAR2(8)
				//		9.	운송지시순번			TRANS_WORD_SEQNO		VARCHAR2(4)			
				outRec.setField("CARD_NO"         , szCARD_NO);
				outRec.setField("CAR_NO"          , szCAR_NO);
				outRec.setField("YD_GP"           , szYD_GP);
				outRec.setField("CARLOAD_END_DATE", szCARLOAD_END_DATE);
				outRec.setField("CARLOAD_END_TIME", szCARLOAD_END_TIME);
				outRec.setField("TRANS_WORD_DATE" , szTRANS_ORD_DATE);
				outRec.setField("TRANS_WORD_SEQNO", szTRANS_ORD_SEQNO);

				//RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
			}
		}catch(Exception e){
			szMsg = "임가공출하상차완료  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR018()

	
	
	
	
	/**
	 * YDDMR030 : 후판제품고간이송상차개시
	 * 2009.09.23
	 * 권오창 
	 *
	 * 일단 껍데기만 생성 코드는 코일것으로 일단 갖다 놓았음. 나중에 전문이 정해지면 수정예정
     *
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 * 
	 */
	public static int makeDMR030(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR030
		//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
		//		3.	상하차 구분			UPCARUNLOAD_GP			VARCHAR2(1)
		//		4.	카드 번호				CARD_NO					VARCHAR2(4)
		//		5.	차량 번호				CAR_NO					VARCHAR2(15)
		//		6.  YARD 구분			YD_GP					VARCHAR2(1)
		//		7.  상차 개시 일자			CARLOAD_START_DATE		VARCHAR2(8)
		//		8.	상차 개시 시각			CARLOAD_START_TIME		VARCHAR2(6)
		//		9.	이송지시일자			TRANS_WORD_DATE			VARCHAR2(8)
		//		10.	이송지시순번			TRANS_WORD_SEQNO		NUMBER(4)
		
		// 차량스케줄DAO 객체 생성
		YdCarSchDao ydCarSchDao     = new YdCarSchDao();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		YdUtils ydUtils             = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		    = null;
		JDTORecord outRec 		    = null;
		JDTORecordSet rsGetYdCarsch = null;

		// 변수 선언
		String szMethodName         = "makeDMR030";
		String szMsg                = "";
		String szOperationName      = "출하 후판제품고간이송상차개시";
		String szUPCARUNLOAD_GP     = "";
		String szCARD_NO 		    = "";
		String szCAR_NO			    = "";
		String szYD_GP			    = "";
		String szSTL_NO			    = "";
		String szTRANS_ORD_DATE 	= "";
		String szTRANS_ORD_SEQNO	= "";
		String szYD_CARLD_ST_DT  	= "";
		String szYD_CARUD_ST_DT	    = "";
		String szCARLOAD_START_DATE = "";
		String szCARLOAD_START_TIME = "";
		int nRet                    = 0;
		int intRtnVal               = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 레코드 생성
			rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");
			outRec        = JDTORecordFactory.getInstance().create();
			
			//=========================================================================================================
			// 차량스케줄 조회
			//=========================================================================================================
			nRet = ydCarSchDao.getYdCarsch(inRec, rsGetYdCarsch, 4);
			if(nRet < 0){
				szMsg ="[오류발생]: 차량스케줄조회 중 오류 [" + nRet + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			} else if(nRet == 0){
				szMsg ="[오류발생]: 차량스케줄조회 중 건수가 없음 [" + nRet + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}


			rsGetYdCarsch.first();
			recPara = rsGetYdCarsch.getRecord();

			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, recPara);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 상하차 구분  :상차(U), 하차(D)
			szUPCARUNLOAD_GP = "D";

			// 카드번호
			szCARD_NO = ydDaoUtils.paraRecChkNull(recPara, "CARD_NO");

			// 차량번호
			szCAR_NO = ydDaoUtils.paraRecChkNull(recPara, "CAR_NO");

			// 야드구분
			szYD_GP = ydDaoUtils.paraRecChkNull(inRec, "YD_GP");

			// 운송지시일자
			szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE");

			// 운송지시순번
			szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO");

			// 하차 개시일자
			szYD_CARUD_ST_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_ST_DT");

			// 상차 개시일자
			szYD_CARLD_ST_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_ST_DT").trim();

			// 상하차 구분이 'U': 상차일 때
			if(szUPCARUNLOAD_GP.equals("U")){

				// 상차 개시일자 , 상차개시 시각	
				if(szYD_CARLD_ST_DT != "" && szYD_CARLD_ST_DT.length() == 14){
					szCARLOAD_START_DATE = szYD_CARLD_ST_DT.substring(0, 8);
					szCARLOAD_START_TIME = szYD_CARLD_ST_DT.substring(8, 14);
				}
			}

			// 상하차 구분이 'D': 하차일 때
			else if(szUPCARUNLOAD_GP.equals("D")){
				
				// 하차 개시 일자, 하차 개시 시각
				if(szYD_CARUD_ST_DT != "" && szYD_CARUD_ST_DT.length() == 14){
					szCARLOAD_START_DATE = szYD_CARUD_ST_DT.substring(0, 8);
					szCARLOAD_START_TIME = szYD_CARUD_ST_DT.substring(8, 14);		
				}
			}

			outRec.setField("TC_CODE"           , "YDDMR030");
			outRec.setField("TC_CREATE_DDTT"    , YdUtils.getCurDate("yyyyMMddHHmmss"));
			outRec.setField("UPCARUNLOAD_GP"    , szUPCARUNLOAD_GP);
			outRec.setField("CARD_NO"           , szCARD_NO);
			outRec.setField("CAR_NO"            , szCAR_NO);
			outRec.setField("YD_GP"             , szYD_GP);
			outRec.setField("CARLOAD_START_DATE", szCARLOAD_START_DATE);
			outRec.setField("CARLOAD_START_TIME", szCARLOAD_START_TIME);

			
			//----------------------------------------------------------------
			// 2009.11.11 이영근 출하팀에서 Lay-Out 변경
			//----------------------------------------------------------------
			for(int i=0; i<intRtnVal; i++){
				recPara = rsGetYdCarsch.getRecord(i);
				
			    // 재료번호 
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				
				// 운송지시일자
				szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE");

				// 운송지시순번
				szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO");
				
				outRec.setField("GOODS_NO" + (i+1)   		, szSTL_NO);
				outRec.setField("TRANS_WORD_DATE" + (i+1)   , szTRANS_ORD_DATE);
				outRec.setField("TRANS_WORD_SEQNO" + (i+1)  , szTRANS_ORD_SEQNO);
			}

			
			
			// RecordSet으로 반환
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
				
		}catch(Exception e){
			szMsg = "후판제품고간이송상차개시  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR030()
	
	
	
	

	/**
	 * YDDMR031 : 후판제품고간이송상차완료
	 * 2009.09.23
	 * 권오창 
	 * 
	 * 일단 껍데기만 생성 코드는 코일것으로 일단 갖다 놓았음. 나중에 전문이 정해지면 수정예정
	 *
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR031(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR031
		//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
		//		3.	상하차 구분			UPCARUNLOAD_GP			VARCHAR2(1)
		//		4.	카드 번호				CARD_NO					VARCHAR2(4)
		//		5.	차량 번호				CAR_NO					VARCHAR2(15)
		//		6.	착지야드포인트코드		ARR_YD_PNT_CD  			VARCHAR2(4)
		//		7.	발생일시				ISSUE_DDTT				VARCHAR2(14)
		//		8.	처리개수				TREAT_EA				NUMBER(9)
		//		9.	재료번호				GOODS_NO1 ~ GOODS_NO20	VARCHAR2(11)  --20회 반복-----
		//		10.	이송지시일자			TRANS_WORD_DATE1 ~ 20	VARCHAR2(8)					|
		//		11.	이송지시순번			TRANS_WORD_SEQNO1 ~ 20	NUMBER(4)					|
		//		12.	저장위치코드TO			STORE_LOC_CD1 ~ 20		VARCHAR2(10)				|
		//		13.	야드구분				YD_GP1 ~ 20				VARCHAR2(1)					|
		//	##	14.	동구분				BAY_GP1 ~ 20			VARCHAR2(1)					|
		//	##	15.	SPAN				SPAN1 ~ 20				VARCHAR2(2)					|	
		//	##	16.	적치단				STK_LYR1 ~ 20			VARCHAR2(3)   --------------
		

		// DAO 객체 생성
		YdCarSchDao ydCarSchDao      = new YdCarSchDao();
		YdUtils ydUtils              = new YdUtils();
		YdDaoUtils ydDaoUtils        = new YdDaoUtils();

		// 레코드 선언
		JDTORecord recPara 		     = null;
		JDTORecord outRec 		     = null;
		JDTORecordSet rsGetYdCarsch  = null;

		// 변수 선언
		String szMethodName          = "makeDMR031";
		String szMsg                 = "";
		String szOperationName       = "출하 후판제품고간이송상차완료";		
		String szUPCARUNLOAD_GP      = "";
		String szCARD_NO 		     = "";
		String szCAR_NO		         = "";
		String szARR_YD_PNT_CD       = "";
		String szISSUE_DDTT          = "";
		String szTREAT_EA            = "";
		String szSTORE_LOC_CD        = "";
		String szYD_CARUD_STOP_LOC   = "";
		String szYD_STK_BED_NO       = "";
		String szYD_STK_LYR_NO       = "";
		int nRet                     = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 레코드 생성
			rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

			// 차량스케줄 조회
			nRet= ydCarSchDao.getYdCarsch(inRec, rsGetYdCarsch, 4);
			if(nRet < 0){
				szMsg ="[오류발생]: 차량스케줄조회 중 오류 [" + nRet + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			} else if(nRet ==0){
				szMsg ="[오류발생]: 차량스케줄조회 중 조회건수 없음 [" + nRet + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}

			for(int i=0; i<nRet; i++){
				outRec = JDTORecordFactory.getInstance().create();
				recPara = rsGetYdCarsch.getRecord(i);

				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, recPara);	
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

				// 상하차 구분  :상차(U), 하차(D)
				szUPCARUNLOAD_GP = "D";
				//szUPCARUNLOAD_GP = "U";

				// 카드번호
				szCARD_NO = ydDaoUtils.paraRecChkNull(recPara, "CARD_NO");

				// 차량번호
				szCAR_NO  = ydDaoUtils.paraRecChkNull(recPara, "CAR_NO");

				// 착지야드포인트코드	
				szARR_YD_PNT_CD  = "";

				// 발생일지
				szISSUE_DDTT = "";

				// 처리개수
				szTREAT_EA = Integer.toString(nRet);

				
				szYD_CARUD_STOP_LOC = ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_STOP_LOC");
				szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
					
				if(!szYD_CARUD_STOP_LOC.trim().equals("")){
					// 후판 (7 + 3)
					szSTORE_LOC_CD = szYD_CARUD_STOP_LOC ;
					if(!szYD_STK_BED_NO.trim().equals("")){
						szSTORE_LOC_CD = szSTORE_LOC_CD + szYD_STK_BED_NO.substring(1, 2);
					}else {
						szSTORE_LOC_CD = szSTORE_LOC_CD + YdUtils.fillSpZr(szSTORE_LOC_CD, 4, 1);
					}
					
					if(!szYD_STK_LYR_NO.trim().equals("")){
						szSTORE_LOC_CD = szSTORE_LOC_CD + szYD_STK_LYR_NO;
					}else {
						szSTORE_LOC_CD = szSTORE_LOC_CD + YdUtils.fillSpZr(szSTORE_LOC_CD, 3, 1);						
					}
				}else {
					szSTORE_LOC_CD = YdUtils.fillSpZr(szSTORE_LOC_CD, 10, 1);
				}

				
				if(i==0){
					outRec.setField("TC_CODE"       , "YDDMR031" );
					outRec.setField("TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));
					outRec.setField("UPCARUNLOAD_GP", szUPCARUNLOAD_GP);
					outRec.setField("CARD_NO"       , szCARD_NO);
					outRec.setField("CAR_NO"        , szCAR_NO);
					outRec.setField("ARR_YD_PNT_CD" , szARR_YD_PNT_CD);
					outRec.setField("ISSUE_DDTT"    , szISSUE_DDTT);
					outRec.setField("TREAT_EA"		, szTREAT_EA);
				}
				
				outRec.setField("GOODS_NO" + (1+i)        , ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				outRec.setField("TRANS_WORD_DATE" + (1+i) , ydDaoUtils.paraRecChkNull(recPara, "FRTOMOVE_ORD_DATE"));
				outRec.setField("TRANS_WORD_SEQNO" + (1+i), ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO"));
//				outRec.setField("STORE_LOC_CD" + (1+i)    , ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_STOP_LOC"));
				outRec.setField("STORE_LOC_CD" + (1+i)    , szSTORE_LOC_CD);
				outRec.setField("YD_GP" + (1+i)           , ydDaoUtils.paraRecChkNull(recPara, "YD_GP"));
				outRec.setField("BAY_GP" + (1+i)          , ydDaoUtils.paraRecChkNull(recPara, "BAY_GP"));
				outRec.setField("SPAN" + (1+i)            , ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO"));
				outRec.setField("STK_LYR" + (1+i)         , ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO"));
				
				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
			}
		}catch(Exception e){
			szMsg = "후판제품고간이송상차완료 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR031()
	
	
	/**
	 * YDDMR034 : 반납확인정보
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR034(JDTORecord inRec, JDTORecordSet outRecSet){
//		GOODS_NO	제품 번호
//		YD_GP	YARD 구분
//		NEXT_PROC	다음 공정
//		RETURN_CONFIRM_GP	반납 확인 구분
//		RETURN_TREAT_DATE	반납 처리 일자
//		RETURN_TREAT_TIME	반납 처리 시각
//		RETURN_ETC_ERR	반납 기타 ERROR
//		RETURN_REAL_SPEC	반납 실 규격
//		RETURN_REAL_T	반납 실 두께
//		RETURN_REAL_W	반납 실 폭
//		RETURN_REAL_LEN	반납 실 길이
//		RETURN_REAL_GRADE	반납 실 등급
//		RETURN_REAL_WT	반납 실 중량
//		RETURN_USAGE_CD	반납 용도 CODE
//		RETURN_REAL_USAGE_CD	반납 실 용도 CODE
//		RETURN_REAL_YEOJAE_GP	반납 실 여재 구분
//		DIST_GOODS_GP	출하제품구분


		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao     = new YdCarSchDao();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		YdUtils ydUtils             = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 	    	= null;
		JDTORecord outRec    		= null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg                = "";		
		String szMethodName         = "makeDMR034";
		String szOperationName      = "반납확인정보";
		
		String 		szGOODS_NO			="";
		String 		szYD_GP				="";
		String 		szNEXT_PROC			="";
		String 		szRETURN_TREAT_DATE	="";
		String 		szRETURN_TREAT_TIME	="";
		String 		szRETURN_REAL_T		="";
		String 		szRETURN_REAL_W		="";
		String 		szRETURN_REAL_WT	="";

		int intRtnVal               = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 차량스케줄 조회
			
			// 2009.07.24 KOC
			// 한건 내려가는것으로 수정
//PIDEV_S :병행가동용:PI_YD			
			inRec.setField("PI_YD",    	ydDaoUtils.paraRecChkNull(inRec, "YD_GP"));				
//***********************************************************************************************************************************************//
			///*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdStockYdRetnWoCmtStat_PIDEV*/
			intRtnVal= ydCarSchDao.getYdCarsch(inRec, rsGetYdCarsch, 303);
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 코일공통조회  중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
//			for(int i=0; i<intRtnVal; i++){		

			rsGetYdCarsch.first();
			recPara = rsGetYdCarsch.getRecord();

			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, recPara);			
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 제품번호
			szGOODS_NO = ydDaoUtils.paraRecChkNull(recPara, "GOODS_NO");
			// 야드구분
			szYD_GP  = ydDaoUtils.paraRecChkNull(recPara, "YD_GP");
			// 다음 공정
			szNEXT_PROC  = ydDaoUtils.paraRecChkNull(recPara, "NEXT_PROC");
			// 반납 처리 일자
			szRETURN_TREAT_DATE  = ydDaoUtils.paraRecChkNull(recPara, "RETURN_TREAT_DATE");
			// 반납 처리 시간
			szRETURN_TREAT_TIME  = ydDaoUtils.paraRecChkNull(recPara, "RETURN_TREAT_TIME");
			// 반납 실 두께
			szRETURN_REAL_T  = ydDaoUtils.paraRecChkNull(recPara, "RETURN_REAL_T");
			// 반납 실 폭
			szRETURN_REAL_W  = ydDaoUtils.paraRecChkNull(recPara, "RETURN_REAL_W");
			// 반납 실 중량
			szRETURN_REAL_WT  = ydDaoUtils.paraRecChkNull(recPara, "RETURN_REAL_WT");

			outRec 	= JDTORecordFactory.getInstance().create();

			String mthdNm = "MakeTcDM.makeDMR034";
//PIDEV
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", ydDaoUtils.paraRecChkNull(inRec, "YD_GP"), "*");
			
//			if("Y".equals(sApplyYnPI)) {

				outRec.setField("MQ_TC_CD"           		, "M10YDLMJ1021");
				outRec.setField("MQ_TC_CREATE_DDTT"    		, new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				outRec.setField("YD_GP"           	 		, szYD_GP );
				outRec.setField("DIST_GOODS_GP"           	, "H" );
				outRec.setField("YARD_GP"           	 	, "" );				
				outRec.setField("GOODS_NO"           		, szGOODS_NO );				
				outRec.setField("NEXT_PROC"           		, szNEXT_PROC );
				outRec.setField("RETURN_CONFIRM_GP"         , "*");
				outRec.setField("RETURN_TREAT_DATE"         , szRETURN_TREAT_DATE );
				outRec.setField("RETURN_TREAT_TIME"         , szRETURN_TREAT_TIME );
				outRec.setField("RETURN_ETC_ERR"           	, "" );
				outRec.setField("RETURN_REAL_SPEC"          , "" );
				outRec.setField("RETURN_REAL_T"           	, szRETURN_REAL_T );
				outRec.setField("RETURN_REAL_W"           	, szRETURN_REAL_W );
				outRec.setField("RETURN_REAL_LEN"           , "" );
				outRec.setField("RETURN_REAL_GRADE"         , "" );
				outRec.setField("RETURN_REAL_WT"           	, szRETURN_REAL_WT );
				outRec.setField("RETURN_USAGE_CD"           , "" );
				outRec.setField("RETURN_REAL_USAGE_CD"      , "" );
				outRec.setField("RETURN_REAL_YEOJAE_GP"     , "" );
				outRec.setField("JMS_TC_CD"           		, "M10YDLMJ1021");		
				
//			} else {
//				outRec.setField("TC_CODE"           		, "YDDMR034");
//				outRec.setField("TC_CREATE_DDTT"    		, new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
//				outRec.setField("GOODS_NO"           		, szGOODS_NO );
//				outRec.setField("YD_GP"           	 		, szYD_GP );
//				outRec.setField("NEXT_PROC"           		, szNEXT_PROC );
//				outRec.setField("RETURN_CONFIRM_GP"         , "*");
//				outRec.setField("RETURN_TREAT_DATE"         , szRETURN_TREAT_DATE );
//				outRec.setField("RETURN_TREAT_TIME"         , szRETURN_TREAT_TIME );
//				outRec.setField("RETURN_ETC_ERR"           	, "" );
//				outRec.setField("RETURN_REAL_SPEC"          , "" );
//				outRec.setField("RETURN_REAL_T"           	, szRETURN_REAL_T );
//				outRec.setField("RETURN_REAL_W"           	, szRETURN_REAL_W );
//				outRec.setField("RETURN_REAL_LEN"           , "" );
//				outRec.setField("RETURN_REAL_GRADE"         , "" );
//				outRec.setField("RETURN_REAL_WT"           	, szRETURN_REAL_WT );
//				outRec.setField("RETURN_USAGE_CD"           , "" );
//				outRec.setField("RETURN_REAL_USAGE_CD"      , "" );
//				outRec.setField("RETURN_REAL_YEOJAE_GP"     , "" );
//				outRec.setField("JMS_TC_CD"           		, "YDDMR034");			
//			}			
			
			// RecordSet으로 반환
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);					
//		} // end of for  
//***********************************************************************************************************************************************//
			
		}catch(Exception e){
			szMsg = "코일출하상차개시  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}
		return intRtnVal;
	} // end of makeDMR034()
	
	
	/**
	 * YDDMR050 : 후핀야드핸들링정보(M10YDLMJ1052)
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeM10YDLMJ1052 (JDTORecord inRec, JDTORecordSet outRecSet){

		// 차량스케줄Dao 객체 생성
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		YdUtils ydUtils             = new YdUtils();

		// 레코드 선언
		JDTORecord outRec    		= null;

		String szMsg                = "";		
		String szMethodName         = "makeM10YDLMJ1052";
		String szOperationName      = "후핀야드핸들링정보";

		int intRtnVal               = 1;

		try{

			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);			
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);
 
			// 야드구분
			String szYD_GP                 = ydDaoUtils.paraRecChkNull(inRec, "YD_GP");  		    
			String szTRANS_ORD_DATE        = ydDaoUtils.paraRecChkNull(inRec, "TRANS_ORD_DATE");  	    // 운송지시일자
			String szTRANS_ORD_SEQNO       = ydDaoUtils.paraRecChkNull(inRec, "TRANS_ORD_SEQNO");      // 운송지시순번 
			String szCMBN_CARLD_YN         = ydDaoUtils.paraRecChkNull(inRec, "CMBN_CARLD_YN");		
			String szCARLD_PNT_CD          = ydDaoUtils.paraRecChkNull(inRec, "CARLD_PNT_CD");		    // 상차포인트코드 
			String szCAR_NO                = ydDaoUtils.paraRecChkNull(inRec, "CAR_NO");			
			String szHANDLING_CNT          = ydDaoUtils.paraRecChkNull(inRec, "HANDLING_CNT");	        // 핸들링횟수 
			String szYD_STK_BED_WHIO_STAT  = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_WHIO_STAT"); // 야드적치BED입출고상태
			
			outRec 	= JDTORecordFactory.getInstance().create();
			outRec.setField("MQ_TC_CD"           		, "M10YDLMJ1052");
			outRec.setField("MQ_TC_CREATE_DDTT"    		, new String(YdUtils.getCurDate("yyyyMMddHHmmss"))); 
			outRec.setField("YD_GP"           	 		, szYD_GP );
			outRec.setField("TRN_REQ_DATE"           	, szTRANS_ORD_DATE);
			outRec.setField("TRN_REQ_SEQ"         	    , szTRANS_ORD_SEQNO);
			outRec.setField("CMBN_CARLD_YN"         	, szCMBN_CARLD_YN );
			outRec.setField("CARLD_PNT_CD"         		, szCARLD_PNT_CD );
			outRec.setField("CAR_NO"           			, szCAR_NO );
			outRec.setField("HANDLING_CNT"          	, szHANDLING_CNT ); 
			outRec.setField("YD_STK_BED_WHIO_STAT"      , szYD_STK_BED_WHIO_STAT ); 
			outRec.setField("DIST_GOODS_GP"           	, "P");
//			outRec.setField("YARD_GP"           	    , "");


			// RecordSet으로 반환
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);					
		
		}catch(Exception e){
			szMsg = "야드핸들링정보  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}
		return intRtnVal;
	} // end of makeDMR050()
	

	
	/**
	 * YDDMR036 : 후핀검수완료실적
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeM10YDLMJ1102(JDTORecord inRec, JDTORecordSet outRecSet){
		
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		YdUtils ydUtils             = new YdUtils();
		
		String szMsg                = "";		
		String szMethodName         = "makeM10YDLMJ1102";
		String szOperationName      = "후핀검수완료실적";	
		JDTORecord outRec    		= null;
		
		int intRtnVal               = 1;
		
		JDTORecordSet rsResult = null;
		JDTORecord recTemp = null;
		JDTORecord recPara = null;
		YdStockDao ydStockDao = new YdStockDao();
		int nRet = 0;
		String szCR_FRTOMOVE_GP	= "";

		// 2021. 5. 27 
		// 후판제품출하지시060 수신시 추가된 컬럼에 의하여 
		// 036으로 전문 전송되어야 하는데 074로 나가는 문제 패치
		String szYD_GP_PLATE_GDS_YN = "";
		try{

			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);			
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			
			String szTRANS_WORD_DATE  = ydDaoUtils.paraRecChkNull   (inRec, "TRANS_WORD_DATE");
			String szTRANS_WORD_SEQNO = ydDaoUtils.paraRecChkNull   (inRec, "TRANS_WORD_SEQNO");
			String szCAR_NO  		  = ydDaoUtils.paraRecChkNull   (inRec, "CAR_NO");		
			int iGoodsNoCnt			  = ydDaoUtils.paraRecChkNullInt(inRec, "GOODS_NO_CNT"); // 제품수량
			
			// 레코드생성-----------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DATE",  szTRANS_WORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO", szTRANS_WORD_SEQNO);

			// (운송일자, 운송순번)로 저장품 조회(운송일자, 운송순번)
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDate*/
			nRet = ydStockDao.getYdStock(recPara, rsResult, 730);
			if(nRet > 0){
				rsResult.first();
				recTemp				 = rsResult.getRecord();			
				szCR_FRTOMOVE_GP	 = ydDaoUtils.paraRecChkNull(recTemp, "CR_FRTOMOVE_GP");
				szYD_GP_PLATE_GDS_YN = ydDaoUtils.paraRecChkNull(recTemp, "YD_GP_PLATE_GDS_YN"); // 후판제품여부(Y,N)
			}
			
			outRec 	= JDTORecordFactory.getInstance().create();
			
			outRec.setField("MQ_TC_CD"            , "M10YDLMJ1102");
			outRec.setField("MQ_TC_CREATE_DDTT"   , new String(YdUtils.getCurDate("yyyyMMddHHmmss"))); 			
			outRec.setField("TRN_REQ_DATE"        , szTRANS_WORD_DATE);
			outRec.setField("TRN_REQ_SEQ"         , szTRANS_WORD_SEQNO);
			outRec.setField("CARLD_CHK_DONE_DATE" , YmCommonUtil.getCurDate("yyyyMMdd"));
			outRec.setField("CARLD_CHK_DONE_TIME" , YmCommonUtil.getCurDate("HHmmss"));
			outRec.setField("CAR_NO"       		  , szCAR_NO);
			outRec.setField("GOODS_NO_CNT"        , Integer.toString(iGoodsNoCnt));
			
			for (int i = 0; i < iGoodsNoCnt; i++) {
				
				outRec.setField("GOODS_NO"+(i+1)   		, ydDaoUtils.paraRecChkNull(inRec, "GOODS_NO"+(i+1)));
				outRec.setField("GOODS_CHK_AB_CD"+(i+1) , ydDaoUtils.paraRecChkNull(inRec, "GOODS_CHK_AB_CD"+(i+1)));
				outRec.setField("LABEL_REISSUE_YN"+(i+1), ydDaoUtils.paraRecChkNull(inRec, "LABEL_REISSUE_YN"+(i+1)));
				outRec.setField("GDS_CARLD_LOC"+(i+1)   , ydDaoUtils.paraRecChkNull(inRec, "GDS_CARLD_LOC"+(i+1)));
				
			}
			
			if(!szCR_FRTOMOVE_GP.equals("")){
				outRec.setField("CR_FRTOMOVE_GP",			szCR_FRTOMOVE_GP);
			}
            outRec.setField("DIST_GOODS_GP"      , "P");
            outRec.setField("YD_GP"              , "T");          
            outRec.setField("SCH_YN"             , "N");
			// RecordSet으로 반환
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);					
		
		}catch(Exception e){
			szMsg = "검수완료실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}
		return intRtnVal;
	} 

	
} // end of class MakeTcDM

