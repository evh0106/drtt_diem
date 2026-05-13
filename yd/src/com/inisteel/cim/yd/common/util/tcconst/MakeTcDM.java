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
import com.inisteel.cim.ym.common.YmCommonUtil;


public class MakeTcDM {

	
	//클래스명
	private static final String szSessionName  = MakeTcDM.class.getName();
	
	/**
	 * YDDMR001 : 코일입고작업실적
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR001(JDTORecord inRec, JDTORecordSet outRecSet){
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
		String szMethodName     = "makeDMR001";
		String szOperationName  = "출하 코일입고작업실적";
		
		// 제품번호
		String szSTL_NO 		= "";

		// 입고 일자 및 시각(크레인권하완료일시)
		String szYD_DN_CMPL_DT	= "";
		String szRECEIPT_DATE	= "";
		String szRECEIPT_TIME	= "";

		// 야드구분
		String szYD_GP			= "";

		// 저장위치(권하실적위치(8) + 권하실적단(2))
		String szYD_DN_WR_LOC	= "";
		String szYD_DN_WR_LAYER = "";
		String szSTORE_LOC 		= "";
		String szTemp           = "";
		String szYD_STK_COL_GP  = "";
		String szYD_STK_BED_NO  = "";
		String szYD_STK_LYR_NO  = "";
		String szCRN_WRK_MTL_LYR_NO = "";
		int intSTK_LYR				= 0;

		
		
		//재료진도코드 - 임춘수 추가 2009.06.15
		String szSTL_PROG_CD	= "";

		int intRtnVal           = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);


			// 크레인스케줄 조회
			intRtnVal = ydCrnSchDao.getYdCrnsch(inRec, rsGetYdCrnsch, 3);
			if(intRtnVal <= 0){				
				ydUtils.putLog(szSessionName, szMethodName, "크레인스케줄 조회 중 오류 :: ["+intRtnVal+"]", YdConstant.ERROR);
				return 0;
			}
			
			// 크레인스케줄 조회결과 추출
			for(int i=0; i<intRtnVal; i++){
				outRec =  JDTORecordFactory.getInstance().create();
				recPara = rsGetYdCrnsch.getRecord(i);
				
				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, recPara);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);
				
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				 * 추가업무 : 재료진도코드가 입고대기(H)/종합판정대기[G]인 경우에만 출하관리로 전송 처리 강관입고대기(2) 추가
				 * 임춘수 추가 2009.06.15
				 */
				szSTL_PROG_CD = ydDaoUtils.paraRecChkNull(recPara, "STL_PROG_CD");
				if( !szSTL_PROG_CD.equals("2") && !szSTL_PROG_CD.equals(YdConstant.PROG_CD_RCPT_WAIT) && !szSTL_PROG_CD.equals(YdConstant.PROG_CD_OVALL_STMP_WAIT) ) {
					szMsg = "재료진도코드[" + szSTL_PROG_CD + "]가 입고대기[" + YdConstant.PROG_CD_RCPT_WAIT + "]/종합판정대기[" + YdConstant.PROG_CD_OVALL_STMP_WAIT + "]가 아니므로 출하관리로 코일입고작업실적 전송불가";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					continue;
				}
				szMsg = "재료진도코드[" + szSTL_PROG_CD + "]가 입고대기[" + YdConstant.PROG_CD_RCPT_WAIT + "]/종합판정대기[" + YdConstant.PROG_CD_OVALL_STMP_WAIT + "]이므로 출하관리로 코일입고작업실적 전송가능";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/


				// 제품번호 
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

				// 입고 일자 및 시각(크레인권하완료일시) ""
				szYD_DN_CMPL_DT 	= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_CMPL_DT").trim();
				if(szYD_DN_CMPL_DT != "" && szYD_DN_CMPL_DT.length() == 14){
					szRECEIPT_DATE 		= szYD_DN_CMPL_DT.substring(0, 8);
					szRECEIPT_TIME 		= szYD_DN_CMPL_DT.substring(8, 14);
				}

				// 야드구분
				szYD_GP = ydDaoUtils.paraRecChkNull(inRec, "YD_GP");

				
				
				
				
				//==============================================================
				// 권오창
				// 2009.12.16
				//
				// 권하실적위치(8)+ 권하실적단(3->2)
				//==============================================================
				szYD_DN_WR_LOC = ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WR_LOC");
				if(szYD_DN_WR_LOC.equals("")){
					szMsg = "권하실적위치 항목이 유효하지 않습니다. STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					continue ;
				}
				
				szYD_DN_WR_LAYER = ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WR_LAYER").trim();
				if(szYD_DN_WR_LAYER.equals("")){
					szMsg = "권하실적단 항목이 유효하지 않습니다. STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					continue ;
				}
				
				
				
				
				
				//=================================================================================
				// 2010.01.07
				// 권오창
				//
				// getYdCrnsch()에서 Gp 3번으로 읽는 쿼리에 크레인작업재료의 단정보 항목을 읽는 부분이 존재
				// 권하위치단에서 증가 처리
				//=================================================================================
				szYD_STK_COL_GP      = szYD_DN_WR_LOC.substring(0, 6);
				szYD_STK_BED_NO   	 = szYD_DN_WR_LOC.substring(6, 8);

				//크레인작업재료의 단정보
				szCRN_WRK_MTL_LYR_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");

				//크레인작업재료의 단 - 1
				intSTK_LYR			 = Integer.parseInt(szCRN_WRK_MTL_LYR_NO) - 1;
				
				//각 크레인작업재료에 대해서 권하실적위치단에서 1단씩 증가시킴
				szYD_STK_LYR_NO      = ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, intSTK_LYR);
				szSTORE_LOC          = ydUtils.ParsingStkColGpBedLyr(szYD_STK_COL_GP, szYD_STK_BED_NO, szYD_STK_LYR_NO);

				
				
				
				
				//	1.	인터페이스ID			   TC_CODE				VARCHAR2(8)		YDDMR001
				//	2.	전송일시				   TC_CREATE_DDTT		VARCHAR2(14)	YYYYMMDDHHMMSS
				//	3.	제품 번호				   GOODS_NO				VARCHAR2(11)	STL_NO
				//	4.	입고 일자				   RECEIPT_DATE			VARCHAR2(8)		YD_DN_RSLT_DT(1:8)
				//	5.	입고 시각				   RECEIPT_TIME			VARCHAR2(6)		YD_DN_RSLT_DT(9:6)
				//	6.	YARD 구분			   YD_GP				VARCHAR2(1)	
				//	7.	야드저장위치			   STORE_LOC			VARCHAR2(10)
				//	8.  ITEMCODE			   PROD_ITEM_CODE		VARCHAR2(25)
				//	9.  CURR_PROG_CD		      현재진도코드		    VARCHAR2(1)
				outRec.setField("TC_CODE"        , new String("YDDMR001") );
				outRec.setField("TC_CREATE_DDTT" , new String(YdUtils.getCurDate("yyyyMMddHHmmss")) );
				outRec.setField("GOODS_NO"       , szSTL_NO);
				outRec.setField("RECEIPT_DATE"   , szRECEIPT_DATE.trim());
				outRec.setField("RECEIPT_TIME"   , szRECEIPT_TIME.trim());
				outRec.setField("YD_GP"          , szYD_GP); 
				outRec.setField("STORE_LOC"      , szSTORE_LOC);
				outRec.setField("PROD_ITEM_CODE" , "");
				outRec.setField("CURR_PROG_CD"   , szSTL_PROG_CD);
				
				outRecSet.addRecord(outRec);
				
				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	

			} // end of for

			
		}catch(Exception e){
			szMsg = "[코일입고작업실적]  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();

	} // end of makeDMR001()







	/**
	 * YDDMR002 : 후판입고작업실적
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR002(JDTORecord inRec, JDTORecordSet outRecSet){
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
		String szMethodName     = "makeDMR002";
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

				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, recPara);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);
				
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				 * 추가업무 : 재료진도코드가 소재입고대기(2)추가
				 * 윤재광 추가 2015.09.24
				 */
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

				
				
				
				
				//==============================================================
				// 권오창
				// 2009.12.16
				//
				// 권하실적위치(8->7)+ 권하실적단(3)
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
				
				
				
				
				
				//=================================================================================
				// 2010.01.07
				// 권오창
				//
				// getYdCrnsch()에서 Gp 51번으로 읽는 쿼리에 크레인작업재료의 단정보 항목을 읽는 부분이 존재
				// 권하위치단에서 증가 처리
				//=================================================================================
				szYD_STK_COL_GP = szYD_DN_WR_LOC.substring(0, 6);
				szYD_STK_BED_NO	= szYD_DN_WR_LOC.substring(6, 8);

				//크레인작업재료의 단정보
				szCRN_WRK_MTL_LYR_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				//크레인작업재료의 단 - 1
				intSTK_LYR			 = Integer.parseInt(szCRN_WRK_MTL_LYR_NO) - 1;
				//각 크레인작업재료에 대해서 권하실적위치단에서 1단씩 증가시킴
				szYD_STK_LYR_NO = ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, intSTK_LYR);				
				szSTORE_LOC = ydUtils.ParsingStkColGpBedLyr(szYD_STK_COL_GP, szYD_STK_BED_NO, szYD_STK_LYR_NO);


				
				
				
				//		1.	인터페이스ID			   TC_CODE				VARCHAR2(8)		YDDMR002
				//		2.	전송일시				   TC_CREATE_DDTT		VARCHAR2(14)	YYYYMMDDHHMMSS
				outRec.setField("TC_CODE"        , new String("YDDMR002"));
				outRec.setField("TC_CREATE_DDTT" , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));

				//		3.	제품 번호				   GOODS_NO				VARCHAR2(11)	STL_NO
				//		4.	입고 일자				   RECEIPT_DATE			VARCHAR2(8)		YD_DN_RSLT_DT(1:8)
				//		5.	입고 시각				   RECEIPT_TIME			VARCHAR2(6)		YD_DN_RSLT_DT(9:6)
				//		6.	YARD 구분			   YD_GP				VARCHAR2(1)	
				//		7.	야드저장위치			   STORE_LOC			VARCHAR2(10)
				//		8.  ITEMCODE			   PROD_ITEM_CODE		VARCHAR2(25)
				outRec.setField("GOODS_NO"       , szSTL_NO);
				outRec.setField("RECEIPT_DATE"   , szRECEIPT_DATE);
				outRec.setField("RECEIPT_TIME"   , szRECEIPT_TIME);
				outRec.setField("YD_GP"          , szYD_GP);
				outRec.setField("STORE_LOC"      , szSTORE_LOC);
				outRec.setField("PROD_ITEM_CODE" , "");
				outRec.setField("CURR_PROG_CD"   , szSTL_PROG_CD);
				
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
	 * YDDMR002 : 후판종합판정요구
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeQMJ502(JDTORecord inRec, JDTORecordSet outRecSet){
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
		String szMethodName     = "makeQMJ502";
		String szOperationName  = "품질 후판종합판정요구";
		
		// 제품번호
		String szSTL_NO 		= "";
		
		int intRtnVal = 0;

		try{
			// 크레인스케줄 조회
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

				// 제품번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

				outRec.setField("JMS_TC_CD"        		, new String("PRQMJ502"));
				outRec.setField("JMS_TC_CREATE_DDTT" 	, new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				outRec.setField("PL_PLATE_NO"       	, szSTL_NO);
				outRec.setField("OPERATION_GP"   		, "1");
				
				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

			} // end of for
			
		}catch(Exception e){
			szMsg = "[후판종합판정요구] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeQMJ502()
	
	/**
	 * YDDMR003 : 코일 임가공 입고작업실적 --AB열연
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR003(JDTORecord inRec, JDTORecordSet outRecSet){
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
		String szMethodName     = "makeDMR003";
		String szOperationName  = "출하 코일 임가공 입고작업실적 --AB열연";
		
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

		int intRtnVal = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);


			// 크레인스케줄 조회
			intRtnVal = ydCrnSchDao.getYdCrnsch(inRec, rsGetYdCrnsch, 3);
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 크레인스케줄조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}

			// 크레인스케줄 조회결과 추출
			for(int i=0; i<intRtnVal; i++){
				outRec = JDTORecordFactory.getInstance().create();
				recPara = rsGetYdCrnsch.getRecord(i);

				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, 
						"\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, recPara);
				ydUtils.putLog(szSessionName, szMethodName, 
						"\n===================================================\n", YdConstant.DEBUG);

				// 제품번호
				szSTL_NO 			= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

				// 입고 일자 및 시각(크레인권하완료일시)
				szYD_DN_CMPL_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_DN_CMPL_DT").trim();
				if(szYD_DN_CMPL_DT != "" && szYD_DN_CMPL_DT.length() == 14){
					szRECEIPT_DATE 		= szYD_DN_CMPL_DT.substring(0, 8);
					szRECEIPT_TIME 		= szYD_DN_CMPL_DT.substring(8, 14);					
				}

				// 야드구분
				szYD_GP 			= ydDaoUtils.paraRecChkNull(inRec, "YD_GP");

				
				
				
				
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
				
				
				
				
				
				//=================================================================================
				// 2010.01.07
				// 권오창
				//
				// getYdCrnsch()에서 Gp 3번으로 읽는 쿼리에 크레인작업재료의 단정보 항목을 읽는 부분이 존재
				// 권하위치단에서 증가 처리
				//=================================================================================
				szYD_STK_COL_GP      = szYD_DN_WR_LOC.substring(0, 6);
				szYD_STK_BED_NO	     = szYD_DN_WR_LOC.substring(6, 8);

				//크레인작업재료의 단정보
				szCRN_WRK_MTL_LYR_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				//크레인작업재료의 단 - 1
				intSTK_LYR			 = Integer.parseInt(szCRN_WRK_MTL_LYR_NO) - 1;

				//각 크레인작업재료에 대해서 권하실적위치단에서 1단씩 증가시킴
				szYD_STK_LYR_NO      = ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, intSTK_LYR);				
				szSTORE_LOC          = ydUtils.ParsingStkColGpBedLyr(szYD_STK_COL_GP, szYD_STK_BED_NO, szYD_STK_LYR_NO);
				
				
				
				
				
				//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR003
				//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
				outRec.setField("TC_CODE", 				new String("YDDMR003"));
				outRec.setField("TC_CREATE_DDTT", 		new String(YdUtils.getCurDate("yyyyMMddHHmmss")));

				//		3.	제품 번호				GOODS_NO				VARCHAR2(11)	STL_NO
				//		4.	입고 일자				RECEIPT_DATE			VARCHAR2(8)		YD_DN_RSLT_DT(1:8)
				//		5.	입고 시각				RECEIPT_TIME			VARCHAR2(6)		YD_DN_RSLT_DT(9:6)
				//		6.	YARD 구분			YD_GP					VARCHAR2(1)		아세아스틸,삼우스틸,동양에스텍,성동철강
				//		7.	야드저장위치			STR_LOC				    VARCHAR2(10)
				//		8.  ITEMCODE			PROD_ITEM_CODE			VARCHAR2(25)
				outRec.setField("GOODS_NO"     , 	szSTL_NO);
				outRec.setField("RECEIPT_DATE" , 	szRECEIPT_DATE);
				outRec.setField("RECEIPT_TIME" , 	szRECEIPT_TIME);
				outRec.setField("YD_GP"        , 	szYD_GP);
				outRec.setField("STORE_LOC"    , 	szSTORE_LOC);
				outRec.setField("PROD_ITEM_CODE" , 	"");

				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
			}

		}catch(Exception e){
			szMsg = "[임가공입고작업실적] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR003()




	/**
	 * YDDMR004 : 코일제품이적작업실적
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR004(JDTORecord inRec, JDTORecordSet outRecSet){
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
		String szMethodName      = "makeDMR004";
		String szOperationName  = "출하 코일제품이적작업실적";
		// 제품번호
		String szSTL_NO 		 = "";

		// FROM저장위치(권상실적위치 + 권상실적단)
		String szBEFO_STORE_LOC	 = "";
		String szYD_UP_WR_LOC	 = "";
		String szYD_UP_WR_LAYER	 = "";

		// TO 저장위치(권하실적위치 + 권하실적단)
		String szTO_STORE_LOC	 = "";
		String szYD_DN_WR_LOC	 = "";
		String szYD_DN_WR_LAYER	 = "";

		// 이적 일자및 시각(권하완료일시)
		String szYD_DN_CMPL_DT	 = "";
		String szMOVENSTACK_DATE = "";
		String szMOVENSTACK_TIME = "";
		
		//재료진도코드
		String szSTL_PROG_CD = "";
		String szYD_STK_COL_GP  = "";
		String szYD_STK_BED_NO  = "";
		String szYD_STK_LYR_NO  = "";	
		int nTemp     = 0;
		int intRtnVal = 0;
		
		String szCRN_WRK_MTL_LYR_NO = "";
		int intSTK_LYR          = 0;
		

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 크레인스케줄 조회
			intRtnVal = ydCrnSchDao.getYdCrnsch(inRec, rsGetYdCrnsch, 3);
			if(intRtnVal <= 0){
				szMsg ="[오류발생]: 크레인스케줄조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 크레인스케줄 조회결과 추출
			for(int i=0; i<intRtnVal; i++){				
				outRec = JDTORecordFactory.getInstance().create();
				recPara = rsGetYdCrnsch.getRecord(i);

				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, recPara);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				 * 추가업무 : 재료진도코드가 입고대기(H)가 아닌 경우에만 출하관리로 전송 처리
				 * 임춘수 추가 2009.06.15
				 */
				szSTL_PROG_CD = ydDaoUtils.paraRecChkNull(recPara, "STL_PROG_CD");
				if( szSTL_PROG_CD.equals(YdConstant.PROG_CD_RCPT_WAIT)  || szSTL_PROG_CD.equals(YdConstant.PROG_CD_OVALL_STMP_WAIT) ) {
					szMsg = "재료진도코드[" + szSTL_PROG_CD + "]가 입고대기[" + YdConstant.PROG_CD_RCPT_WAIT + "]/종합판정대기[" + YdConstant.PROG_CD_OVALL_STMP_WAIT + "]이므로 출하관리로 코일제품이적작업실적 전송불가";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					continue;
				}
				szMsg = "재료진도코드[" + szSTL_PROG_CD + "]가 입고대기[" + YdConstant.PROG_CD_RCPT_WAIT + "]/종합판정대기[" + YdConstant.PROG_CD_OVALL_STMP_WAIT + "]가 아니므로 출하관리로 코일제품이적작업실적 전송가능";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				
				// 제품번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

				
				
				
				
				// 크레인작업재료의 단정보
				szCRN_WRK_MTL_LYR_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				// 크레인작업재료의 단 - 1
				intSTK_LYR			 = Integer.parseInt(szCRN_WRK_MTL_LYR_NO) - 1;


				
				
				
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
				
				
				
				

				//=================================================================================
				// 2010.01.07
				// 권오창
				//
				// getYdCrnsch()에서 Gp 3번으로 읽는 쿼리에 크레인작업재료의 단정보 항목을 읽는 부분이 존재
				// 권상위치단에서 증가 처리
				//=================================================================================				
				szYD_STK_COL_GP  = szYD_UP_WR_LOC.substring(0, 6);
				szYD_STK_BED_NO	 = szYD_UP_WR_LOC.substring(6, 8);

				// 각 크레인작업재료에 대해서 권상실적위치단에서 1단씩 증가시킴
				szYD_STK_LYR_NO  = ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, intSTK_LYR);
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

				
				
				
				
				//=================================================================================
				// 2010.01.07
				// 권오창
				//
				// getYdCrnsch()에서 Gp 3번으로 읽는 쿼리에 크레인작업재료의 단정보 항목을 읽는 부분이 존재
				// 권하위치단에서 증가 처리
				//=================================================================================						
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

				//		1.	인터페이스ID			   TC_CODE					VARCHAR2(8)		YDDMR004
				//		2.	전송일시				   TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
				outRec.setField("TC_CODE"        , new String("YDDMR004"));
				outRec.setField("TC_CREATE_DDTT" , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));

				//		3.	제품 번호				   GOODS_NO					VARCHAR2(11)	이적 주작업, 타 작업의 보조작업으로 권하실적처리 시점
				//		4.	FROM 저장위치			   BEFO_STORE_LOC			VARCHAR2(10)	권상(From)위치
				//		5.	TO 저장위치			   TO_STORE_LOC				VARCHAR2(10)	권하(To)위치
				//		6.	이적 일자				   MOVENSTACK_DATE			VARCHAR2(8)		YD_DN_RSLT_DT(1:8)
				//		7.	이적 시각				   MOVENSTACK_TIME			VARCHAR2(6)		YD_DN_RSLT_DT(9:6)
				outRec.setField("GOODS_NO"       , szSTL_NO);
				outRec.setField("BEFO_STORE_LOC" , szBEFO_STORE_LOC);
				outRec.setField("TO_STORE_LOC"   , szTO_STORE_LOC);
				outRec.setField("MOVENSTACK_DATE", szMOVENSTACK_DATE);
				outRec.setField("MOVENSTACK_TIME", szMOVENSTACK_TIME);

				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	

			} // end of for
		}catch(Exception e){
			szMsg = "[코일제품이적작업실적] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR004()


	/**
	 * YDDMR005 : 후판제품이적작업실적
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR005(JDTORecord inRec, JDTORecordSet outRecSet){
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
		String szMethodName      = "makeDMR005";
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

				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, recPara);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);
				
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				 * 추가업무 : 재료진도코드가 입고대기(H)가 아닌 경우에만 출하관리로 전송 처리
				 * 임춘수 추가 2009.06.15
				 */
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
				
					

				//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR005
				//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
				outRec.setField("TC_CODE"        , new String("YDDMR005"));
				outRec.setField("TC_CREATE_DDTT" , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));

				//		3.	제품 번호				GOODS_NO				VARCHAR2(11)	이적 주작업, 타 작업의 보조작업으로 권하실적처리 시점
				//		4.	FROM 저장위치			BEFO_STORE_LOC			VARCHAR2(10)	권상(From)위치
				//		5.	TO 저장위치			TO_STORE_LOC			VARCHAR2(10)	권하(To)위치
				//		6.	이적 일자				MOVENSTACK_DATE			VARCHAR2(8)		YD_DN_RSLT_DT(1:8)
				//		7.	이적 시각				MOVENSTACK_TIME			VARCHAR2(6)		YD_DN_RSLT_DT(9:6)
				outRec.setField("GOODS_NO"       , szSTL_NO);
				outRec.setField("BEFO_STORE_LOC" , szBEFO_STORE_LOC);
				outRec.setField("TO_STORE_LOC"   , szTO_STORE_LOC);
				outRec.setField("MOVENSTACK_DATE", szMOVENSTACK_DATE);
				outRec.setField("MOVENSTACK_TIME", szMOVENSTACK_TIME);

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
	 * YDDMR006 : 후판제품선별작업실적
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR006(JDTORecord inRec, JDTORecordSet outRecSet){

/*
 * 크레인스케줄을 조회하여 처리하는 것이 아닌
 * 화면에서 재료번호를 입력받으면 처리하는 것으로 수정해야함. 
 */ 	
		// 크레인스케줄Dao 객체 생성
		//YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
		YdDaoUtils ydDaoUtils 	= new YdDaoUtils();
		YdUtils ydUtils 		= new YdUtils();

		// 레코드 선언
		//JDTORecord recPara 		= null;
		JDTORecord outRec 		= null;

		// 조회 결과를 담을 RecordSet생성
		//JDTORecordSet rsGetYdCrnsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg            = "";
		String szMethodName     = "makeDMR006";
		String szOperationName  = "출하 후판제품선별작업실적";
		
		// 제품번호
		String szSTL_NO 		= "";

		// 선별일자및 시각
		//String szYD_DN_CMPL_DT	= "";
		String szCurDate		= "";
		String szSORTING_DATE   = "";
		String szSORTING_TIME   = "";
		String szSORTING_CONFIRM= "";
		String szCAR_LOTID = "";
		//int intRtnVal           = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	

//			// 크레인스케줄 조회
//			intRtnVal = ydCrnSchDao.getYdCrnsch(inRec, rsGetYdCrnsch, 3);
//			if(intRtnVal <=0){
//			szMsg ="[오류발생]: 크레인스케줄조회 중 오류 ["+intRtnVal+"]" ;
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return 0;
//			}

//			// 크레인스케줄 조회결과 추출

//			for(int i=0; i<intRtnVal; i++){
			outRec = JDTORecordFactory.getInstance().create();


//			// Debug Msg
//			ydUtils.putLog(szSessionName, szMethodName, 
//			"\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
//			ydUtils.displayRecord(szOperationName, recPara);

			// 제품번호
			szSTL_NO = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");
			
			//선별 취소 유무
			szSORTING_CONFIRM = ydDaoUtils.paraRecChkNull(inRec, "SORTING_CONFIRM");

			szCAR_LOTID = ydDaoUtils.paraRecChkNull(inRec, "LOT_NO");
			
			// ?(선별일자/선별시각)
			szCurDate = YdUtils.getCurDate("yyyyMMddHHmmss").trim();
			if(szCurDate != "" && szCurDate.length() == 14){
				szSORTING_DATE = szCurDate.substring(0, 8);
				szSORTING_TIME = szCurDate.substring(8, 14);
			}

			//	1.	인터페이스ID			   		TC_CODE					VARCHAR2(8)		YDDMR006
			//	2.	전송일시				   		TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
			//	3.	제품번호				   		GOODS_NO				VARCHAR2(11)	선별작업으로 권하실적처리 시점	
			//	4.	선별일자				   		SORTING_DATE			VARCHAR2(8)		YD_DN_CMPL_DT(0, 8)	
			//	5.	선별시각				  		SORTING_TIME			VARCHAR2(6)		YD_DN_CMPL_DT(8, 14)	
			//	6.	선별 취소 유무			  	SORTING_CONFIRM			VARCHAR2(1)		S':선별,'C' : 취소	
			//	7.	베드상태					  	YD_STK_BED_WHIO_STAT	VARCHAR2(1)		F:완산베드,'G' : 가적베드
			
			outRec.setField("TC_CODE"         , new String("YDDMR006"));
			outRec.setField("TC_CREATE_DDTT"  , new String(szCurDate));
			outRec.setField("GOODS_NO"        		, szSTL_NO);
			outRec.setField("SORTING_DATE"    		, szSORTING_DATE);
			outRec.setField("SORTING_TIME"    		, szSORTING_TIME);
			outRec.setField("SORTING_CONFIRM" 		, szSORTING_CONFIRM);
			outRec.setField("YD_STK_BED_WHIO_STAT"  , ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_WHIO_STAT"));
			
			outRec.setField("LOT_NO" 	      , szCAR_LOTID);

			// RecordSet으로 반환
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	

//			} // end of for

		}catch(Exception e){
			szMsg = "[후판제품선별작업실적] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}
		return outRecSet.size();
	} // end of makeDMR006()


	/**
	 * YDDMR007 : 코일출하상차개시
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR007(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	인터페이스ID				TC_CODE					VARCHAR2(8)		YDDMR007
		//		2.	전송일시					TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
		//		3.	카드 번호					CARD_NO					VARCHAR2(4)
		//		4.	차량 번호					CAR_NO					VARCHAR2(15)
		//		5.  야드구분					YD_GP					VARCHAR2(1)
		//		6.  상차 개시 일자				CARLOAD_START_DATE		VARCHAR2(8)
		//		7.	상차 개시 시각				CARLOAD_START_TIME		VARCHAR2(6)
		//		8.	이송지시일자				TRANS_WORD_DATE			VARCHAR2(8)
		//		9.	이송지시순번				TRANS_WORD_SEQNO		VARCHAR2(4)

		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao     = new YdCarSchDao();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		YdUtils ydUtils             = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 	    	= null;
		JDTORecord outRec    		= null;
		JDTORecord recTemp    		= null;
		
		YdStockDao ydStockDao  = new YdStockDao();
		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsResult = null;
		String szMsg                = "";		
		String szMethodName         = "makeDMR007";
		String szOperationName      = "출하 코일출하상차개시";
		
		// 카드번호
		String szCARD_NO 	    	= "";

		// 차량번호
		String szCAR_NO		    	= "";

		// 야드구분
		String szYD_GP			    = "";

		// 운송지시일자
		String szTRANS_ORD_DATE 	= "";

		// 운송지시순번
		String szTRANS_ORD_SEQNO	= "";

		// 상차개시일자및시각 (야드상차개시일시)
		String szYD_CARLD_ST_DT	    = "";
		String szCARLOAD_START_DATE = "";
		String szCARLOAD_START_TIME = "";
		int intRtnVal               = 0;
		int nRet= 0;
		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 차량스케줄 조회
			
			// 2009.07.24 KOC
			// 한건 내려가는것으로 수정
//***********************************************************************************************************************************************//
			intRtnVal= ydCarSchDao.getYdCarsch(inRec, rsGetYdCarsch, 0);
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 차량스케줄조회 중 오류 ["+intRtnVal+"]" ;
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

			// 카드번호
			szCARD_NO = ydDaoUtils.paraRecChkNull(recPara, "CARD_NO");

			// 차량번호
			szCAR_NO  = ydDaoUtils.paraRecChkNull(recPara, "CAR_NO");

			// 야드구분
			szYD_GP	  = (ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_STOP_LOC")).substring(0,1);

			// 운송지시일자
			szTRANS_ORD_DATE  = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE");

			// 운송지시순번
			szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO");

			// 상차 개시일자 , 상차개시 시각	
			szYD_CARLD_ST_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_ST_DT").trim();	
			if(szYD_CARLD_ST_DT != "" && szYD_CARLD_ST_DT.length() == 14){
				szCARLOAD_START_DATE = szYD_CARLD_ST_DT.substring(0, 8);
				szCARLOAD_START_TIME = szYD_CARLD_ST_DT.substring(8, 14);				
			}
			
 
			outRec 	= JDTORecordFactory.getInstance().create(); 
			outRec.setField("TC_CODE"           , new String("YDDMR007"));
			outRec.setField("TC_CREATE_DDTT"    , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
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
//		} // end of for  
//***********************************************************************************************************************************************//
			
		}catch(Exception e){
			szMsg = "코일출하상차개시  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}
		return intRtnVal;
	} // end of makeDMR007()


	/**
	 * YDDMR008 : 후판출하상차개시
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR008(JDTORecord inRec, JDTORecordSet outRecSet){
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
		String szMethodName         = "makeDMR008";
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
				szCARD_NO = ydDaoUtils.paraRecChkNull(recPara, "CARD_NO");

				// 차량번호
				szCAR_NO = ydDaoUtils.paraRecChkNull(recPara, "CAR_NO");

				// 운송지시일자
				szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE");

				// 운송지시순번
				szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO");

				// 상차 개시일자 , 상차개시 시각	
				szYD_CARLD_ST_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_ST_DT").trim();
				szCARLOAD_START_DATE = (YdUtils.getCurDate("yyyyMMddHHmmss")).substring(0, 8);
				szCARLOAD_START_TIME = (YdUtils.getCurDate("yyyyMMddHHmmss")).substring(8, 14);	

				outRec.setField("TC_CODE"           , new String("YDDMR008"));
				outRec.setField("TC_CREATE_DDTT"    , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				outRec.setField("CARD_NO"           , szCARD_NO);
				outRec.setField("CAR_NO"            , szCAR_NO);
				outRec.setField("CARLOAD_START_DATE", szCARLOAD_START_DATE);
				outRec.setField("CARLOAD_START_TIME", szCARLOAD_START_TIME);
				outRec.setField("TRANS_WORD_DATE"   , szTRANS_ORD_DATE);
				outRec.setField("TRANS_WORD_SEQNO"  , szTRANS_ORD_SEQNO);
				outRec.setField("CARLD_PNT_CD"      , szCARLD_PNT_CD);
 
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
	public static int makeDMR009(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao     = new YdCarSchDao();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		YdUtils ydUtils             = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		    = null;
		JDTORecord outRec 		    = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg                = "";
		String szMethodName         = "makeDMR009";
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
				outRec.setField("TC_CODE"           , new String("YDDMR009"));
				outRec.setField("TC_CREATE_DDTT"    , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				outRec.setField("CARD_NO"           , szCARD_NO);
				outRec.setField("CAR_NO"            , szCAR_NO);
				outRec.setField("YD_GP"          	, szYD_GP);
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
			//} //end of for
			
		}catch(Exception e){
			szMsg = "외판슬라브출하상차개시  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR009()



	/**
	 * YDDMR010 :  SLAB운송lOT편성정보
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR010(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR010
		//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
		//		3.	운송LOT편성일자		TRANS_LOT_DATE			VARCHAR2(8)
		//		4.	운송LOT번호  			TRANS_LOT_SEQ			VARCHAR2(10)
		//		5.	고객코드				CUST_CD					VARCHAR2(6)	
		//		6.	목적지코드			DEST_CD					VARCHAR2(5)	
		//		7.	목적지전화번호			DEST_TEL_NO				VARCHAR2(15)	
		//		8.	출하배선지시구분		DIST_SHIPASSIGIN_GP		VARCHAR2(1)	
		//		9.	카드 번호				CARD_NO					VARCHAR2(4)	
		//		10.	출하제품구분			DIST_GOODS_GP			VARCHAR2(1)		'C'	
		//		11.	YARD 구분			YD_GP					VARCHAR2(1)		
		//		12.	제품 개수				GOODS_EA				NUMBER(2)	
		//		13.	제품 번호				GOODS_NO				VARCHAR2(11)	

		
		// 작업예약DAO 객체 생성
		YdWrkbookDao ydWrkbookDao    = new YdWrkbookDao();
		YdUtils ydUtils              = new YdUtils();
		YdDaoUtils ydDaoUtils        = new YdDaoUtils();					

		// 레코드 선언
		JDTORecord recPara 	         = null;
		JDTORecord outRec 	         = null;
 
		// 작업예약DAO 조회 결과 담을 RecordSet
		JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg                 = "";
		String szMethodName          = "makeDMR010";
		String szOperationName       = "출하 SLAB운송lOT편성정보";
		
		// 운송LOT편성일자
		String szTRANS_LOT_DATE      = "";

		// 운송LOT번호
		String szTRANS_LOT_NO        = "";

		// 고객코드
		String szCUST_CD             = "";

		// 목적지코드
		String szDEST_CD             = "";

		// 목적지전화번호
		String szDEST_TEL_NO         = "";

		// 출하배선지시구분
		String szDIST_SHIPASSIGIN_GP = "";

		// 카드 번호
		String szCARD_NO             = "";

		// 출하제품구분
		String szDIST_GOODS_GP       = "";

		// YARD 구분
		String szYD_GP               = "";

		// 제품 개수
		String szGOODS_EA            = "";

		// 제품 번호
		String szGOODS_NO            = "";
		String szTemp                = "";
		

		int intRtnVal                = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			intRtnVal = ydWrkbookDao.getYdWrkbook(inRec, rsResult, 3);			
			if(intRtnVal <= 0){
				szMsg ="[오류발생]: 작업예약조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 0;
			}

			
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

			for(int i=0; i<intRtnVal; i++){
				recPara = rsResult.getRecord(i);
				
				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, recPara);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

				// 운송LOT편성일자
				szTemp = ydDaoUtils.paraRecChkNull(recPara, "REG_DDTT").trim();
				if(!szTemp.equals("") && szTemp.length() == 14)
				{
					szTRANS_LOT_DATE = szTemp.substring(0, 8);
				}
				// 운송LOT번호
				szTRANS_LOT_NO = "";

				// 고객코드
				szCUST_CD = ydDaoUtils.paraRecChkNull(recPara, "CUST_CD");

				// 목적지코드
				szDEST_CD = ydDaoUtils.paraRecChkNull(recPara, "DEST_CD");

				// 목적지전화번호
				szDEST_TEL_NO = ydDaoUtils.paraRecChkNull(recPara, "DEST_TEL_NO");

				// 출하배선지구분
				szDIST_SHIPASSIGIN_GP = ydDaoUtils.paraRecChkNull(recPara, "DIST_SHIPASSIGIN_GP");

				// 카드번호
				szCARD_NO = ydDaoUtils.paraRecChkNull(recPara, "CARD_NO");

				// 출하제품구분
				szDIST_GOODS_GP = "C";

				// 야드구분
				szYD_GP = ydDaoUtils.paraRecChkNull(inRec, "YD_GP");

				// 제품개수
				szGOODS_EA = Integer.toString(intRtnVal);

				// 제품번호
				szGOODS_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

				outRec 	= JDTORecordFactory.getInstance().create();
				
				if(i==0){
					outRec.setField("TC_CODE"			 , new String("YDDMR010"));
					outRec.setField("TC_CREATE_DDTT"	 , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
					outRec.setField("TRANS_LOT_DATE"	 , szTRANS_LOT_DATE);
					outRec.setField("TRANS_LOT_SEQ"		 , szTRANS_LOT_NO);
					outRec.setField("CUST_CD"			 , szCUST_CD);
					outRec.setField("DEST_CD"			 , szDEST_CD);
					outRec.setField("DEST_TEL_NO"		 , szDEST_TEL_NO);
					outRec.setField("DIST_SHIPASSIGIN_GP", szDIST_SHIPASSIGIN_GP);
					outRec.setField("CARD_NO"            , szCARD_NO);
					outRec.setField("DIST_GOODS_GP"		 , szDIST_GOODS_GP);
					outRec.setField("YD_GP"				 , szYD_GP);
					outRec.setField("GOODS_EA"			 , szGOODS_EA);
				}
				
				outRec.setField("GOODS_NO"+(i+1)          , szGOODS_NO);

				// RecordSet으로 반환
				outRecSet.addRecord(outRec);				

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
			}
			
		}catch(Exception e){
			szMsg = "SLAB운송lOT편성정보 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR010()







	/**
	 * YDDMR011 : 코일일품출하상차실적
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR011(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao  = new YdCarSchDao();
		YdDaoUtils ydDaoUtils    = new YdDaoUtils();
		YdUtils ydUtils          = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		 = null;
		JDTORecord outRec 		 = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg             = "";
		String szMethodName      = "makeDMR011";
		String szOperationName   = "출하 코일일품출하상차실적";
		
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
	
		// 재료번호
		String szCarSchId 	     = "";

		// 재료번호
		String szSTL_NO		     = "";

		int intRtnVal            = 0;
		
		
		JDTORecordSet rsResult = null;
		JDTORecord recTemp = null;

		YdStockDao ydStockDao = new YdStockDao();
		int nRet = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 차량스케줄 조회
			szCarSchId = ydDaoUtils.paraRecChkNull(inRec, "YD_CAR_SCH_ID");
			szSTL_NO   = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");
			szGOODS_EA   = ydDaoUtils.paraRecChkNull(inRec, "GOODS_EA");
			
			if(szGOODS_EA.equals("")){
				szGOODS_EA ="1";
			}
			
			recPara = JDTORecordFactory.getInstance().create();			
			recPara.setField("STL_NO", szSTL_NO);
			recPara.setField("YD_CAR_SCH_ID", szCarSchId);
			intRtnVal= ydCarSchDao.getYdCarsch(recPara, rsGetYdCarsch, 28);			
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 차량스케줄조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			// 차량스케줄 조회결과 추출
			
			rsGetYdCarsch.first();
			recPara = rsGetYdCarsch.getRecord();		



			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, recPara);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);
 

			//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR011
			//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
			//		3.	카드 번호				CARD_NO					VARCHAR2(4)
			//		4.	차량 번호				CAR_NO					VARCHAR2(15)
			//		5.	YARD 구분			YD_GP					VARCHAR2(1)
			//      6.  제품 개수				GOODS_EA				NUMBER(4)
			//		7.	제품 번호				GOODS_NO       			VARCHAR2(11)          ARRAY[20]
			//		8.	운송지시일자			TRANS_WORD_DATE			VARCHAR2(8)
			//		9.	운송지시순번			TRANS_WORD_SEQNO		NUMBER(4)
			
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

			// 제품 개수
			//szGOODS_EA = Integer.toString(intRtnVal);

			// 제품 번호
			szGOODS_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
			
		 
			
			outRec = JDTORecordFactory.getInstance().create();
 		 
			outRec.setField("TC_CODE"             , new String("YDDMR011"));
			outRec.setField("TC_CREATE_DDTT"  , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));		 
			outRec.setField("CARD_NO"         , szCARD_NO);
			outRec.setField("CAR_NO"          , szCAR_NO);
			outRec.setField("YD_GP"           , szYD_GP);
			outRec.setField("GOODS_EA"        , szGOODS_EA);
			outRec.setField("GOODS_NO"        , szGOODS_NO);			
			outRec.setField("TRANS_WORD_DATE" , szTRANS_ORD_DATE);
			outRec.setField("TRANS_WORD_SEQNO", szTRANS_ORD_SEQNO);
 
			// RecordSet으로 반환
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	

		}catch(Exception e){
			szMsg = "코일일품출하상차실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR011()


	/**
	 * YDDMR012 : 후판일품출하상차실적	
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR012(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		//YdCarSchDao ydCarSchDao  = new YdCarSchDao();
		YdDaoUtils ydDaoUtils    = new YdDaoUtils();
		YdUtils ydUtils          = new YdUtils();

		// 레코드 선언
		//JDTORecord recPara 		 = null;
		JDTORecord outRec 		 = null;

		//JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg             = "";
		String szMethodName      = "makeDMR012";
		String szOperationName   = "출하 후판일품출하상차실적";
		
		// 카드번호
		//String szCARD_NO 		 = "";

		// 차량번호
		//String szCAR_NO			 = "";

		// 운송지시일자
		//String szTRANS_ORD_DATE  = "";

		// 운송지시순번
		//String szTRANS_ORD_SEQNO = "";

		// 제품 개수
		//String szGOODS_EA        = "";

		// 제품 번호
		//String szGOODS_NO		 = "";

		//int intRtnVal            = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			/* -- 기존 방시 -------------------
			// 차량스케줄 조회
			intRtnVal= ydCarSchDao.getYdCarsch(inRec, rsGetYdCarsch, 4);			
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 차량스케줄조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}

			// 차량스케줄 조회결과 추출
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

				// 운송지시일자
				szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE");

				// 운송지시순번
				szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO");

				// 제품 개수
				szGOODS_EA = Integer.toString(rsGetYdCarsch.size());
				
				// 제품 번호
				szGOODS_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				
				//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR012
				//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setField("TC_CODE"         , new String("YDDMR012"));
				outRec.setField("TC_CREATE_DDTT"  , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));

				//		3.	카드 번호				CARD_NO					VARCHAR2(4)
				//		4.	차량 번호				CAR_NO					VARCHAR2(15)
				//      5.  제품 개수				GOODS_EA				NUMBER(4)
				//		6.	제품 번호				GOODS_NO				VARCHAR2(11)
				//		7.	운송지시일자			TRANS_WORD_DATE			VARCHAR2(8)
				//		8.	운송지시순번			TRANS_WORD_SEQNO		NUMBER(4)
				outRec.setField("CARD_NO"         , szCARD_NO);
				outRec.setField("CAR_NO"          , szCAR_NO);
				outRec.setField("GOODS_EA"        , szGOODS_EA);
				outRec.setField("GOODS_NO"        , szGOODS_NO);		
				outRec.setField("TRANS_WORD_DATE" , szTRANS_ORD_DATE);
				outRec.setField("TRANS_WORD_SEQNO", szTRANS_ORD_SEQNO);

				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);					
			}
			*/
			
			//--출하고도화 이후 방식 
			
			//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR012
			//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
			//		3.	카드 번호				CARD_NO					VARCHAR2(4)
			//		4.	차량 번호				CAR_NO					VARCHAR2(15)
			//		5.	YARD 구분			YD_GP					VARCHAR2(1)
			//      6.  제품 개수				GOODS_EA				NUMBER(4)
			//		7.	제품 번호				GOODS_NO       			VARCHAR2(11)          ARRAY[20]
			//		8.	운송지시일자			TRANS_WORD_DATE			VARCHAR2(8)
			//		9.	운송지시순번			TRANS_WORD_SEQNO		NUMBER(4)	
			//     10.  상차포인트			CARLD_PNT_CD			VARCHAR2(4)
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("TC_CODE"         , new String("YDDMR012"));
			outRec.setField("TC_CREATE_DDTT"  , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
			outRec.setField("CARD_NO"         , ydDaoUtils.paraRecChkNull(inRec, "CARD_NO"));
			outRec.setField("CAR_NO"          , ydDaoUtils.paraRecChkNull(inRec, "CAR_NO"));
			outRec.setField("YD_GP"           , ydDaoUtils.paraRecChkNull(inRec, "YD_GP"));
			outRec.setField("GOODS_EA"        , ydDaoUtils.paraRecChkNull(inRec, "GOODS_EA"));
			outRec.setField("GOODS_NO"        , ydDaoUtils.paraRecChkNull(inRec, "GOODS_NO"));		
			outRec.setField("TRANS_WORD_DATE" , ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_DATE"));
			outRec.setField("TRANS_WORD_SEQNO", ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_SEQNO"));			
			outRec.setField("CARLD_PNT_CD"    , ydDaoUtils.paraRecChkNull(inRec, "CARLD_PNT_CD"));
			
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
	public static int makeDMR013(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao  = new YdCarSchDao();
		YdDaoUtils ydDaoUtils    = new YdDaoUtils();
		YdUtils ydUtils          = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		 = null;
		JDTORecord outRec 		 = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg             = "";
		String szMethodName      = "makeDMR013";
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
				outRec.setField("TC_CODE"        , new String("YDDMR013"));
				outRec.setField("TC_CREATE_DDTT" , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));

				//		3.	카드 번호				CARD_NO					VARCHAR2(4)
				//		4.	차량 번호				CAR_NO					VARCHAR2(15)
				//		5.	YARD 구분			YD_GP					VARCHAR2(1)
				//      6.  제품 개수				GOODS_EA				NUMBER(4)
				//		7.	제품 번호				GOODS_NO				VARCHAR2(11)
				//		8.	운송지시일자			TRANS_WORD_DATE			VARCHAR2(8)
				//		9.	운송지시순번			TRANS_WORD_SEQNO		NUMBER(4)
				outRec.setField("CARD_NO"         , szCARD_NO);
				outRec.setField("CAR_NO"          , szCAR_NO);
				outRec.setField("YD_GP"           , szYD_GP);
				outRec.setField("GOODS_EA"        , szGOODS_EA);
				outRec.setField("GOODS_NO"        , szGOODS_NO);			
				outRec.setField("TRANS_WORD_DATE" , szTRANS_ORD_DATE);
				outRec.setField("TRANS_WORD_SEQNO", szTRANS_ORD_SEQNO);
	
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
	 * YDDMR015 : 코일출하상차완료 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR015(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();
		YdUtils ydUtils           = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		  = null;
		JDTORecord outRec 		  = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg              = "";
		String szMethodName       = "makeDMR015";
		String szOperationName    = "출하 코일출하상차완료";
		
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

				// 상차 완료 일자 , 상차 완료 시각	
				szYD_CARLD_CMPL_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_CMPL_DT").trim();
				if(szYD_CARLD_CMPL_DT != "" && szYD_CARLD_CMPL_DT.length() == 14){
					szCARLOAD_END_DATE = szYD_CARLD_CMPL_DT.substring(0, 8);
					szCARLOAD_END_TIME = szYD_CARLD_CMPL_DT.substring(8, 14);
				}

				//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR015
				//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
				//		3.	카드 번호				CARD_NO					VARCHAR2(4)
				//		4.	차량 번호				CAR_NO					VARCHAR2(15)
				//		5.	YARD 구분			YD_GP					VARCHAR2(1)
				//      6.  상차완료 일자			CARLOAD_END_DATE		VARCHAR2(8)
				//		7.	상차완료  시각			CARLOAD_END_TIME		VARCHAR2(6)
				//		8.	운송지시일자			TRANS_WORD_DATE			VARCHAR2(8)
				//		9.	운송지시순번			TRANS_WORD_SEQNO		NUMBER(4)
				
			
				
				outRec = JDTORecordFactory.getInstance().create();	 
				
				outRec.setField("TC_CODE"         , new String("YDDMR015"));
				outRec.setField("TC_CREATE_DDTT"  , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				outRec.setField("CARD_NO"         , szCARD_NO);
				outRec.setField("CAR_NO"          , szCAR_NO);
				outRec.setField("YD_GP"           , szYD_GP);
				outRec.setField("CARLOAD_END_DATE", szCARLOAD_END_DATE);
				outRec.setField("CARLOAD_END_TIME", szCARLOAD_END_TIME);
				outRec.setField("TRANS_WORD_DATE" , szTRANS_ORD_DATE);
				outRec.setField("TRANS_WORD_SEQNO", szTRANS_ORD_SEQNO);

				
				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
			//}
		}catch(Exception e){
			szMsg = "코일출하상차완료  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR015()


	/**
	 * YDDMR016 : 후판출하상차완료
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR016(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		YdUtils ydUtils           = new YdUtils();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();

		// 레코드 선언
		JDTORecord recPara 		  = null;
		JDTORecord outRec 		  = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg              = "";
		String szMethodName       = "makeDMR016";
		String szOperationName    = "출하 후판출하상차완료";
		
		// 카드번호
		String szCARD_NO 		  = "";

		// 차량번호
		String szCAR_NO			  = "";

		// 운송지시일자
		String szTRANS_ORD_DATE   = "";

		// 운송지시순번
		String szTRANS_ORD_SEQNO  = "";

		// 상차완료일자및시각 (야드상차완료일시)
		String szYD_CARLD_CMPL_DT = "";		
		String szCARLOAD_END_DATE = "";
		String szCARLOAD_END_TIME = "";
		
		String szCARLD_PNT_CD = null;

		int intRtnVal             = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);
			
			//해송스케줄 이후 상차 포인트 
			szCARLD_PNT_CD = ydDaoUtils.paraRecChkNull(inRec, "CARLD_PNT_CD");

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

				// 카드번호
				szCARD_NO = ydDaoUtils.paraRecChkNull(recPara, "CARD_NO");

				// 차량번호
				szCAR_NO = ydDaoUtils.paraRecChkNull(recPara, "CAR_NO");

				// 운송지시일자
				szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE");

				// 운송지시순번
				szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO");

				// 상차 완료 일자 , 상차 완료 시각	
				szYD_CARLD_CMPL_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_CMPL_DT").trim();
				szCARLOAD_END_DATE = (YdUtils.getCurDate("yyyyMMddHHmmss")).substring(0, 8);
				szCARLOAD_END_TIME = (YdUtils.getCurDate("yyyyMMddHHmmss")).substring(8, 14);	

				//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR016
				//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS

				outRec.setField("TC_CODE"         , new String("YDDMR016"));
				outRec.setField("TC_CREATE_DDTT"  , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));

				//		3.	카드 번호				CARD_NO					VARCHAR2(4)
				//		4.	차량 번호				CAR_NO					VARCHAR2(15)
				//      5.  상차완료 일자			CARLOAD_END_DATE		VARCHAR2(8)
				//		6.	상차완료  시각			CARLOAD_END_TIME		VARCHAR2(6)
				//		7.	운송지시일자			TRANS_WORD_DATE			VARCHAR2(8)
				//		8.	운송지시순번			TRANS_WORD_SEQNO		NUMBER(4)
				//      9.  상차포인트			CARLD_PNT_CD			VARCHAR2(4)
				outRec.setField("CARD_NO"         , szCARD_NO);
				outRec.setField("CAR_NO"          , szCAR_NO);
				outRec.setField("CARLOAD_END_DATE", szCARLOAD_END_DATE);
				outRec.setField("CARLOAD_END_TIME", szCARLOAD_END_TIME);
				outRec.setField("TRANS_WORD_DATE" , szTRANS_ORD_DATE);
				outRec.setField("TRANS_WORD_SEQNO", szTRANS_ORD_SEQNO);
				outRec.setField("CARLD_PNT_CD"    , szCARLD_PNT_CD);
				
				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
			//}
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
	public static int makeDMR017(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();
		YdUtils ydUtils           = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		  = null;
		JDTORecord outRec 		  = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg              = "";
		String szMethodName       = "makeDMR017";
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
				outRec.setField("TC_CODE"         , new String("YDDMR017"));
				outRec.setField("TC_CREATE_DDTT"  , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));

				//		3.	카드 번호				CARD_NO					VARCHAR2(4)
				//		4.	차량 번호				CAR_NO					VARCHAR2(15)
				//		5.	YARD 구분			YD_GP					VARCHAR2(1)
				//      6.  상차완료 일자			CARLOAD_END_DATE		VARCHAR2(8)
				//		7.	상차완료  시각			CARLOAD_END_TIME		VARCHAR2(6)
				//		8.	운송지시일자			TRANS_WORD_DATE			VARCHAR2(8)
				//		9.	운송지시순번			TRANS_WORD_SEQNO		NUMBER(4)
				outRec.setField("CARD_NO"         , szCARD_NO);
				outRec.setField("CAR_NO"          , szCAR_NO);
				outRec.setField("YD_GP"           , szYD_GP);
				outRec.setField("CARLOAD_END_DATE", szCARLOAD_END_DATE);
				outRec.setField("CARLOAD_END_TIME", szCARLOAD_END_TIME);
				outRec.setField("TRANS_WORD_DATE" , szTRANS_ORD_DATE);
				outRec.setField("TRANS_WORD_SEQNO", szTRANS_ORD_SEQNO);

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
	 * YDDMR019 : 코일제품고간이송상하차개시
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 * 
	 */
	public static int makeDMR019(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR019
		//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
		//		3.	상하차 구분			UPCARUNLOAD_GP			VARCHAR2(1)
		//		4.	카드 번호				CARD_NO					VARCHAR2(4)
		//		5.	차량 번호				CAR_NO					VARCHAR2(15)
		//		6.  YARD 구분			YD_GP					VARCHAR2(1)
		//		7.  상차 개시 일자			CARLOAD_START_DATE		VARCHAR2(8)
		//		8.	상차 개시 시각			CARLOAD_START_TIME		VARCHAR2(6)
		//  총 50건 Array
		//      9.  재료번호                           STL_NO                  VARCHAR2(11)
		//		9.	이송지시일자			TRANS_WORD_DATE			VARCHAR2(8)
		//		10.	이송지시순번			TRANS_WORD_SEQNO		NUMBER(4)
		
		
		
		
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao     = new YdCarSchDao();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		YdUtils ydUtils             = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		    = null;
		JDTORecord outRec 		    = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg                = "";
		String szMethodName         = "makeDMR019";
		String szOperationName      = "출하 코일제품고간이송상하차개시";
		
		// 상하차구분
		String szUPCARUNLOAD_GP     = "";

		// 카드번호
		String szCARD_NO 		    = "";

		// 차량번호
		String szCAR_NO			    = "";

		// 야드구분
		String szYD_GP			    = "";

		// 운송지시일자
		String szTRANS_ORD_DATE 	= "";
 
		// 재료번호 
		String szSTL_NO             = "";
		
		// 운송지시순번
		String szTRANS_ORD_SEQNO	= "";

		// 상차개시일자및시각 (야드상차개시일시)
		String szYD_CARLD_ST_DT  	= "";

		// 하차개시일자및시각 (야드하차개시일시)
		String szYD_CARUD_ST_DT	    = "";
		String szCARLOAD_START_DATE = "";
		String szCARLOAD_START_TIME = "";
		String szYD_EQP_WRK_STAT = "";
		

		int intRtnVal               = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 차량스케줄 조회
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdStockYdCarFtmvMtl2_PIDEV*/
			intRtnVal= ydCarSchDao.getYdCarsch(inRec, rsGetYdCarsch, 302);
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 차량스케줄조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}

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
				szYD_GP = ydDaoUtils.paraRecChkNull(inRec, "YD_GP");

				// 하차 개시일자
				szYD_CARUD_ST_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_ST_DT");

				// 상차 개시일자
				szYD_CARLD_ST_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_ST_DT").trim();
				
				
				szYD_EQP_WRK_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_STAT");
				
				//U:상차 , L:하차
				if(szYD_EQP_WRK_STAT.equals("L")){
					// 상하차 구분  :상차(U), 하차(D)
					szUPCARUNLOAD_GP = "D";
				}else{
					szUPCARUNLOAD_GP = "U";
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

				outRec.setField("TC_CODE"           , new String("YDDMR019"));
				outRec.setField("TC_CREATE_DDTT"    , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
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
			szMsg = "코일제품고간이송상하차개시  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR019()






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
	 * YDDMR021 : 코일제품고간이송상하차완료
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR021(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao      = new YdCarSchDao();
		YdUtils ydUtils              = new YdUtils();
		YdDaoUtils ydDaoUtils        = new YdDaoUtils();

		// 레코드 선언
		JDTORecord recPara 		     = null;
		JDTORecord outRec 		     = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg                 = "";
		String szMethodName          = "makeDMR021";
		String szOperationName       = "출하 코일제품고간이송상하차완료";
		
		// 상하차구분
		String szUPCARUNLOAD_GP      = "";

		// 카드번호
		String szCARD_NO 		     = "";
 
		// 차량번호
		String szCAR_NO		         = "";

		// 착지야드포인트코드	
		String szARR_YD_PNT_CD       = "";

		// 발생일지
		String szISSUE_DDTT          = "";

		// 처리개수
		String szTREAT_EA            = "";
		
		String szSTORE_LOC_CD        = "";
		String szYD_CARUD_STOP_LOC   = "";
		String szYD_STK_BED_NO       = "";
		String szYD_STK_LYR_NO       = "";
		String szYD_EQP_WRK_STAT 	 = "";

		int intRtnVal                = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 차량스케줄 조회
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdStockYdCarFtmvMtl2_PIDEV*/
			intRtnVal= ydCarSchDao.getYdCarsch(inRec, rsGetYdCarsch, 302);
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 차량스케줄조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}

			outRec = JDTORecordFactory.getInstance().create();
			
			for(int i=0; i<intRtnVal; i++){
				
				recPara = rsGetYdCarsch.getRecord(i);

				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, recPara);	
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);



				// 카드번호
				szCARD_NO = ydDaoUtils.paraRecChkNull(recPara, "CARD_NO");

				// 차량번호
				szCAR_NO  = ydDaoUtils.paraRecChkNull(recPara, "CAR_NO");

				// 착지야드포인트코드	
				szARR_YD_PNT_CD  = "";

				// 발생일지
				szISSUE_DDTT = "";

				// 처리개수
				szTREAT_EA = Integer.toString(intRtnVal);

				szYD_CARUD_STOP_LOC = ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_STOP_LOC");
				szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				szYD_EQP_WRK_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_STAT");
				
				//U:상차 , L:하차
				if(szYD_EQP_WRK_STAT.equals("L")){
					// 상하차 구분  :상차(U), 하차(D)
					szUPCARUNLOAD_GP = "D";
				}else{
					szUPCARUNLOAD_GP = "U";
				}			
				
					
				if(!szYD_CARUD_STOP_LOC.trim().equals("")){
					// 코일 (8 + 2)
					szSTORE_LOC_CD = szYD_CARUD_STOP_LOC ;
					if(!szYD_STK_BED_NO.trim().equals("")){
						szSTORE_LOC_CD = szSTORE_LOC_CD + szYD_STK_BED_NO;
					}else {
						szSTORE_LOC_CD = szSTORE_LOC_CD + YdUtils.fillSpZr(szSTORE_LOC_CD, 4, 1);
					}
					
					if(!szYD_STK_BED_NO.trim().equals("")){
						szSTORE_LOC_CD = szSTORE_LOC_CD + szYD_STK_LYR_NO.substring(1, 3);
					}else {
						szSTORE_LOC_CD = szSTORE_LOC_CD + YdUtils.fillSpZr(szSTORE_LOC_CD, 2, 1);						
					}
				}else {
					szSTORE_LOC_CD = YdUtils.fillSpZr(szSTORE_LOC_CD, 10, 1);
				}
				

				//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR021
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
				
				if(i==0){
					outRec.setField("TC_CODE", 				new String("YDDMR021") );
					outRec.setField("TC_CREATE_DDTT", 		new String(YdUtils.getCurDate("yyyyMMddHHmmss")) );
					outRec.setField("UPCARUNLOAD_GP"          , szUPCARUNLOAD_GP);
					outRec.setField("CARD_NO"                 , szCARD_NO);
					outRec.setField("CAR_NO"                  , szCAR_NO);
					outRec.setField("ARR_YD_PNT_CD"           , ydDaoUtils.paraRecChkNull(recPara, "YD_PNT_CD"));
					outRec.setField("ISSUE_DDTT"              , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
					outRec.setField("TREAT_EA"				  , szTREAT_EA);
				}
				
				outRec.setField("GOODS_NO" + (1+i)        , ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				outRec.setField("TRANS_WORD_DATE" + (1+i) , ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE"));
				outRec.setField("TRANS_WORD_SEQNO" + (1+i), ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO"));
				outRec.setField("STORE_LOC_CD" + (1+i)    , ydDaoUtils.paraRecChkNull(recPara, "STORE_LOC_CD"));
				outRec.setField("YD_GP" + (1+i)           , ydDaoUtils.paraRecChkNull(recPara, "YD_GP"));
				outRec.setField("BAY_GP" + (1+i)          , ydDaoUtils.paraRecChkNull(recPara, "BAY_GP"));
				outRec.setField("SPAN" + (1+i)            , ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO"));
				outRec.setField("STK_LYR" + (1+i)         , ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO"));
			}
			
				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
			
		}catch(Exception e){
			szMsg = "코일제품고간이송상하차완료 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR021()


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
	
	
	
	
	
//===========================================================================================================	
	/**
	 * YDDMR024 : HYSCO대차이송실적
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR024(JDTORecord inRec, JDTORecordSet outRecSet){
		YdUtils ydUtils           = new YdUtils();
		
		String szMsg              = "";
		String szMethodName       = "makeDMR024";
		String szOperationName    = "출하 HYSCO대차이송실적";
		
		try{

			// . . . 미구현 . . .
			
		}catch(Exception e){
			szMsg = "HYSCO대차이송실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR024()
//===========================================================================================================	


	
	
	
//===========================================================================================================	
	/**
	 * YDDMR025 : HYSCO수냉실적
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR025(JDTORecord inRec, JDTORecordSet outRecSet){
		YdUtils ydUtils           = new YdUtils();
		
		String szMsg              = "";
		String szMethodName       = "makeDMR025";
		String szOperationName    = "출하 HYSCO수냉실적";
		
		try{

			// . . . 미구현 . . .
			
		}catch(Exception e){
			szMsg = "HYSCO수냉실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR025()
//===========================================================================================================	

	
	
	
	
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
	 * YDDMR027 : 검수완료 실적
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR027(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR027
		//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
		//		3.	카드번호				CARD_NO					VARCHAR2(4)     
		//		4.	차량번호			    CAR_NO				    VARCHAR2(15)
		//		5.	야드출고검수 완료일시	YD_ISSUE_CHK_DT		  	VARCHAR2(14)    현재날짜시간
		//		6.	출고검수 작업자		    ISSUE_CHK_WORKER		VARCHAR2(10)
		//		7.	운송 지시일자		    TRANS_WORD_DATE	        VARCHAR2(8)
		//		8.	운송지시순번     		TRANS_WORD_SEQNO		NUMBER(4)
		YdUtils ydUtils        = new YdUtils();
		YdDaoUtils ydDaoUtils  = new YdDaoUtils();
		JDTORecord outRec 	   = null;
		String szMsg           = "";
		String szMethodName    = "makeDMR027";
		String szOperationName = "출하 검수완료 실적";
		
		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("TC_CODE"         , new String("YDDMR027"));
			outRec.setField("TC_CREATE_DDTT"  , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
			outRec.setField("CARD_NO"    	  , ydDaoUtils.paraRecChkNull(inRec, "CARD_NO"));
			outRec.setField("CAR_NO"          , ydDaoUtils.paraRecChkNull(inRec, "CAR_NO"));
			outRec.setField("YD_ISSUE_CHK_DT" , YdUtils.getCurDate("yyyyMMddHHmmss"));
			outRec.setField("ISSUE_CHK_WORKER", ydDaoUtils.paraRecChkNull(inRec, "ISSUE_CHK_WORKER"));
			outRec.setField("TRANS_WORD_DATE" , ydDaoUtils.paraRecChkNull(inRec, "TRANS_ORD_DATE"));
			outRec.setField("TRANS_WORD_SEQNO", ydDaoUtils.paraRecChkNull(inRec, "TRANS_ORD_SEQNO"));

			//RecordSet으로 반환
			outRecSet.addRecord(outRec);
			
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
		}catch(Exception e){
			
			szMsg = "검수완료실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR027()
	


	
	
	/**
	 * YDDMR028 : 차량입동지시
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR028(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	인터페이스ID			TC_CODE					VARCHAR2(4)		YDDMR028
		//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
		//		7.	카드번호				CARD_NO					VARCHAR2(4)
		//		8.	차량번호			    CAR_NO				    VARCHAR2(15)
		//		9.	입동일시                       	BAYIN_DDTT     		  	VARCHAR2(14)
		//		10.	개소코드                           WLOC_CD          		VARCHAR2(5)
		//		11. 야드포인트코드		    YD_PNT_CD      	        VARCHAR2(4)
		//		12.	차입인출가능여부    		LOAN_PULLOUT_ABLE_YN 	VARCHAR2(1)     Y,N,H

		/*
		 * CARD_NO	카드 번호
		CAR_NO	차량 번호
		TRANS_WORD_DATE	운송지시일자
		TRANS_WORD_SEQNO	운송지시순번
		BAYIN_DDTT	입동일시
		WLOC_CD	개소코드
		YD_PNT_CD	야드포인트코드
		LOAN_PULLOUT_ABLE_YN	차입인출가능여부

		 */
		YdUtils ydUtils        = new YdUtils();
		YdDaoUtils ydDaoUtils  = new YdDaoUtils();	
		YdCarSchDao ydCarSchDao 	 = new YdCarSchDao();
		JDTORecordSet rsResult = null; 
		
		JDTORecord outRec      = null;
		JDTORecord recPara     = null;
		JDTORecord recTemp     = null;
		
		String szMsg           = "";
		String szMethodName    = "makeDMR028";
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
			recPara.setField("CAR_NO",  ydDaoUtils.paraRecChkNull(inRec, "CAR_NO"));
			recPara.setField("CARD_NO",  ydDaoUtils.paraRecChkNull(inRec, "CARD_NO"));
			nRet = ydCarSchDao.getYdCarsch(recPara, rsResult, 11);
			if(nRet > 0){

				rsResult.first();
				recPara = rsResult.getRecord();
				szTRANS_EQUIPMENT_TYPE = ydDaoUtils.paraRecChkNull(recPara,"TRANS_EQUIPMENT_TYPE");
				szSPOS_WLOC_CD= ydDaoUtils.paraRecChkNull(recPara,"SPOS_WLOC_CD");
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
			recPara.setField("TRANS_ORD_DATE",  ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_DATE"));
			recPara.setField("TRANS_ORD_SEQNO", ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_SEQNO"));

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
			if(szTRANS_EQUIPMENT_TYPE.equals("P")){
				outRec.setField("TC_CODE"             , new String("YDDMR070"));
			}else{
				outRec.setField("TC_CODE"             , new String("YDDMR028"));
			}
			outRec.setField("TC_CREATE_DDTT"      , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
			outRec.setField("CARD_NO"    	      , ydDaoUtils.paraRecChkNull(inRec, "CARD_NO"));
			outRec.setField("CAR_NO"              , ydDaoUtils.paraRecChkNull(inRec, "CAR_NO"));
			outRec.setField("TRANS_WORD_DATE"     , ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_DATE"));
			outRec.setField("TRANS_WORD_SEQNO"    , ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_SEQNO"));
			outRec.setField("BAYIN_DDTT"          , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
			outRec.setField("LOAN_PULLOUT_ABLE_YN", ydDaoUtils.paraRecChkNull(inRec, "LOAN_PULLOUT_ABLE_YN"));
			outRec.setField("YD_CARPNT_CD"        , szYD_CARPNT_CD);
			
			if(szTRANS_EQUIPMENT_TYPE.equals("P")){
				outRec.setField("CR_FRTOMOVE_GP"           , szCR_FRTOMOVE_GP);				
			}else{
				outRec.setField("WLOC_CD"             , szWLOC_CD);
				outRec.setField("YD_PNT_CD"           , szYD_PNT_CD);
			}
			
			
			
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
			outRec.setField("TC_CODE"           		, "YDDMR034");
			outRec.setField("TC_CREATE_DDTT"    		, new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
			outRec.setField("GOODS_NO"           		,szGOODS_NO );
			outRec.setField("YD_GP"           	 		,szYD_GP );
			outRec.setField("NEXT_PROC"           		,szNEXT_PROC );
			outRec.setField("RETURN_CONFIRM_GP"         , "*");
			outRec.setField("RETURN_TREAT_DATE"         ,szRETURN_TREAT_DATE );
			outRec.setField("RETURN_TREAT_TIME"         ,szRETURN_TREAT_TIME );
			outRec.setField("RETURN_ETC_ERR"           	,"" );
			outRec.setField("RETURN_REAL_SPEC"          ,"" );
			outRec.setField("RETURN_REAL_T"           	,szRETURN_REAL_T );
			outRec.setField("RETURN_REAL_W"           	,szRETURN_REAL_W );
			outRec.setField("RETURN_REAL_LEN"           ,"" );
			outRec.setField("RETURN_REAL_GRADE"         ,"" );
			outRec.setField("RETURN_REAL_WT"           	,szRETURN_REAL_WT );
			outRec.setField("RETURN_USAGE_CD"           ,"" );
			outRec.setField("RETURN_REAL_USAGE_CD"      ,"" );
			outRec.setField("RETURN_REAL_YEOJAE_GP"     ,"" );
			outRec.setField("JMS_TC_CD"           		, "YDDMR034");


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
	 * YDDMR050 : 야드핸들링정보
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR050(JDTORecord inRec, JDTORecordSet outRecSet){
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
		String szMethodName         = "makeDMR050";
		String szOperationName      = "야드핸들링정보";
		
		String 		szTRANS_ORD_DT			="";
		String 		szYD_GP					="";
		String 		szTRANS_ORD_SEQNO		="";
		String 		szCMBN_CARLD_YN			="";
		String 		szCARLD_PNT_CD			="";
		String 		szCAR_NO				="";
		String 		szHANDLING_CNT			="";
		String 		szYD_STK_BED_WHIO_STAT	="";

		int intRtnVal               = 1;

		try{

			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);			
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);
 
			// 야드구분
			szYD_GP  = ydDaoUtils.paraRecChkNull(inRec, "YD_GP");
			// 운송지시일자
			szTRANS_ORD_DT  = ydDaoUtils.paraRecChkNull(inRec, "TRANS_ORD_DT");
			// 운송지시순번
			szTRANS_ORD_SEQNO  = ydDaoUtils.paraRecChkNull(inRec, "TRANS_ORD_SEQNO");
			// 조합상차유무
			szCMBN_CARLD_YN  = ydDaoUtils.paraRecChkNull(inRec, "CMBN_CARLD_YN");
			// 상차포인트코드
			szCARLD_PNT_CD  = ydDaoUtils.paraRecChkNull(inRec, "CARLD_PNT_CD");
			// 차량번호
			szCAR_NO  = ydDaoUtils.paraRecChkNull(inRec, "CAR_NO");
			// 핸들링횟수
			szHANDLING_CNT  = ydDaoUtils.paraRecChkNull(inRec, "HANDLING_CNT");
			// 야드적치BED입출고상태
			szYD_STK_BED_WHIO_STAT  = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_WHIO_STAT");
			
			
			outRec 	= JDTORecordFactory.getInstance().create();
			outRec.setField("TC_CODE"           		, "YDDMR050");
			outRec.setField("TC_CREATE_DDTT"    		, new String(YdUtils.getCurDate("yyyyMMddHHmmss"))); 
			outRec.setField("YD_GP"           	 		,szYD_GP );
			outRec.setField("TRANS_ORD_DT"           	,szTRANS_ORD_DT);
			outRec.setField("TRANS_ORD_SEQNO"         	,szTRANS_ORD_SEQNO);
			outRec.setField("CMBN_CARLD_YN"         	,szCMBN_CARLD_YN );
			outRec.setField("CARLD_PNT_CD"         		,szCARLD_PNT_CD );
			outRec.setField("CAR_NO"           			,szCAR_NO );
			outRec.setField("HANDLING_CNT"          	,szHANDLING_CNT ); 
			outRec.setField("YD_STK_BED_WHIO_STAT"      ,szYD_STK_BED_WHIO_STAT ); 
			outRec.setField("JMS_TC_CD"           		, "YDDMR050");


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
	 * YDDMR036 : 검수완료실적
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR036(JDTORecord inRec, JDTORecordSet outRecSet){
		
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		YdUtils ydUtils             = new YdUtils();
		
		String szMsg                = "";		
		String szMethodName         = "makeDMR036";
		String szOperationName      = "검수완료실적";	
		
		String szTRANS_WORD_DATE	= null;
		String szTRANS_WORD_SEQNO	= null;
		String szCAR_NO				= null;
		
		int	   iGoodsNoCnt			= 0;
		
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

			
			// 운송지시일자
			szTRANS_WORD_DATE  	= ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_DATE");
			// 운송지시순번
			szTRANS_WORD_SEQNO  = ydDaoUtils.paraRecChkNull(inRec, "TRANS_WORD_SEQNO");
			// 차량번호
			szCAR_NO  			= ydDaoUtils.paraRecChkNull(inRec, "CAR_NO");		
			// 제품수량
			iGoodsNoCnt			= ydDaoUtils.paraRecChkNullInt(inRec, "GOODS_NO_CNT");
			
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
				recTemp				= rsResult.getRecord();			
				szCR_FRTOMOVE_GP	= ydDaoUtils.paraRecChkNull(recTemp, "CR_FRTOMOVE_GP");
				
				// 후판제품여부(Y,N)
				szYD_GP_PLATE_GDS_YN = ydDaoUtils.paraRecChkNull(recTemp, "YD_GP_PLATE_GDS_YN");
			}
			
			outRec 	= JDTORecordFactory.getInstance().create();
			
			if("Y".equals(szYD_GP_PLATE_GDS_YN)){
				outRec.setField("TC_CODE"           		, "YDDMR036");
				outRec.setField("JMS_TC_CD"           		, "YDDMR036");	
			}
			else{

//				if(szCR_FRTOMOVE_GP.equals("")){
//					outRec.setField("TC_CODE"           		, "YDDMR036");
//					outRec.setField("JMS_TC_CD"           		, "YDDMR036");	
//				}else{
					outRec.setField("TC_CODE"           		, "YDDMR074");
					outRec.setField("JMS_TC_CD"           		, "YDDMR074");	
//				}
			}
			outRec.setField("TC_CREATE_DDTT"    		, new String(YdUtils.getCurDate("yyyyMMddHHmmss"))); 			
			outRec.setField("TRANS_WORD_DATE"           , szTRANS_WORD_DATE);
			outRec.setField("TRANS_WORD_SEQNO"          , szTRANS_WORD_SEQNO);
			outRec.setField("CARLD_CHK_DONE_DATE"       , YmCommonUtil.getCurDate("yyyyMMdd"));
			outRec.setField("CARLD_CHK_DONE_TIME"       , YmCommonUtil.getCurDate("HHmmss"));
			outRec.setField("CAR_NO"       				, szCAR_NO);
			outRec.setField("GOODS_NO_CNT"       		, Integer.toString(iGoodsNoCnt));
			
			for (int i = 0; i < iGoodsNoCnt; i++) {
				
				outRec.setField("GOODS_NO"+(i+1)   		, ydDaoUtils.paraRecChkNull(inRec, "GOODS_NO"+(i+1)));
				outRec.setField("GOODS_CHK_AB_CD"+(i+1) , ydDaoUtils.paraRecChkNull(inRec, "GOODS_CHK_AB_CD"+(i+1)));
				outRec.setField("LABEL_REISSUE_YN"+(i+1), ydDaoUtils.paraRecChkNull(inRec, "LABEL_REISSUE_YN"+(i+1)));
				outRec.setField("GDS_CARLD_LOC"+(i+1)   , ydDaoUtils.paraRecChkNull(inRec, "GDS_CARLD_LOC"+(i+1)));
				
			}
			
			if(!szCR_FRTOMOVE_GP.equals("")){
				outRec.setField("CR_FRTOMOVE_GP",			szCR_FRTOMOVE_GP);
			}
            
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
	} // end of makeDMR036()

	/**
	 * YDDMR071 : 코일이송상차개시PDA
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR071(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();
		YdUtils ydUtils           = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		  = null;
		JDTORecord outRec 		  = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg              = "";
		String szMethodName       = "makeDMR071";
		String szOperationName    = "코일이송상차개시PDA";
		
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
		String szYD_CARLD_ST_DT = "";
		String szCARLOAD_START_DATE = "";
		String szCARLOAD_START_TIME = "";

		int intRtnVal             = 0;
 
		String szCR_FRTOMOVE_GP		= "";

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
				
				//냉연이송구분
				szCR_FRTOMOVE_GP = ydDaoUtils.paraRecChkNull(recPara, "CR_FRTOMOVE_GP");

				// 상차 개시 일자 , 상차 개시 시각	
				szYD_CARLD_ST_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_ST_DT").trim();
				if(szYD_CARLD_ST_DT != "" && szYD_CARLD_ST_DT.length() == 14){
					szCARLOAD_START_DATE = szYD_CARLD_ST_DT.substring(0, 8);
					szCARLOAD_START_TIME = szYD_CARLD_ST_DT.substring(8, 14);
				}
 
				outRec = JDTORecordFactory.getInstance().create();	 
				
				outRec.setField("TC_CODE"         , new String("YDDMR071"));
				outRec.setField("TC_CREATE_DDTT"  , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				outRec.setField("CARD_NO"         , szCARD_NO);
				outRec.setField("CAR_NO"          , szCAR_NO);
				outRec.setField("YD_GP"           , szYD_GP);
				outRec.setField("CARLOAD_START_DATE", szCARLOAD_START_DATE);
				outRec.setField("CARLOAD_START_TIME", szCARLOAD_START_TIME);
				outRec.setField("TRANS_WORD_DATE" , szTRANS_ORD_DATE);
				outRec.setField("TRANS_WORD_SEQNO", szTRANS_ORD_SEQNO);
				outRec.setField("CR_FRTOMOVE_GP"	, szCR_FRTOMOVE_GP);

				
				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
			//}
		}catch(Exception e){
			szMsg = "코일출하상차완료  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR071()
	
	
	/**
	 * YDDMR072 : 코일이송일품실적PDA
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR072(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao  = new YdCarSchDao();
		YdDaoUtils ydDaoUtils    = new YdDaoUtils();
		YdUtils ydUtils          = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		 = null;
		JDTORecord outRec 		 = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg             = "";
		String szMethodName      = "makeDMR072";
		String szOperationName   = "코일이송일품실적PDA";
		
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
	
		// 재료번호
		String szCarSchId 	     = "";

		// 재료번호
		String szSTL_NO		     = "";

		int intRtnVal            = 0;
		
 
		String szCR_FRTOMOVE_GP		= "";

		try{
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 차량스케줄 조회
			szCarSchId = ydDaoUtils.paraRecChkNull(inRec, "YD_CAR_SCH_ID");
			szSTL_NO   = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");
			szGOODS_EA   = ydDaoUtils.paraRecChkNull(inRec, "GOODS_EA");
			
			if(szGOODS_EA.equals("")){
				szGOODS_EA ="1";
			}
			
			recPara = JDTORecordFactory.getInstance().create();			
			recPara.setField("STL_NO", szSTL_NO);
			recPara.setField("YD_CAR_SCH_ID", szCarSchId);
			intRtnVal= ydCarSchDao.getYdCarsch(recPara, rsGetYdCarsch, 28);			
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 차량스케줄조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			// 차량스케줄 조회결과 추출
			
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
			szYD_GP = ydDaoUtils.paraRecChkNull(inRec, "YD_GP");

			// 운송지시일자
			szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE");

			// 운송지시순번
			szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO");

			// 제품 개수
			//szGOODS_EA = Integer.toString(intRtnVal);

			// 제품 번호
			szGOODS_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
			
			//냉연이송구분
			szCR_FRTOMOVE_GP = ydDaoUtils.paraRecChkNull(recPara, "CR_FRTOMOVE_GP");
			
			outRec = JDTORecordFactory.getInstance().create();
 		 
			outRec.setField("TC_CODE"             , new String("YDDMR072"));
			outRec.setField("TC_CREATE_DDTT"  , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));		 
			outRec.setField("CARD_NO"         , szCARD_NO);
			outRec.setField("CAR_NO"          , szCAR_NO);
			outRec.setField("YD_GP"           , szYD_GP);
			//outRec.setField("GOODS_EA"        , szGOODS_EA);
			outRec.setField("GOODS_NO"        , szGOODS_NO);			
			outRec.setField("TRANS_WORD_DATE" , szTRANS_ORD_DATE);
			outRec.setField("TRANS_WORD_SEQNO", szTRANS_ORD_SEQNO);
			outRec.setField("CR_FRTOMOVE_GP"	, szCR_FRTOMOVE_GP);
			
			// RecordSet으로 반환
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	

		}catch(Exception e){
			szMsg = "코일일품출하상차실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR072()
	
	
	
	/**
	 * YDDMR073 : 코일이송상차완료PDA
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR073(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();
		YdUtils ydUtils           = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		  = null;
		JDTORecord outRec 		  = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg              = "";
		String szMethodName       = "makeDMR073";
		String szOperationName    = "코일이송상차완료PDA";
		
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
		
 
		String szCR_FRTOMOVE_GP		= "";

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
				
				//냉연이송구분
				szCR_FRTOMOVE_GP = ydDaoUtils.paraRecChkNull(recPara, "CR_FRTOMOVE_GP");

				// 상차 완료 일자 , 상차 완료 시각	
				szYD_CARLD_CMPL_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_CMPL_DT").trim();
				if(szYD_CARLD_CMPL_DT != "" && szYD_CARLD_CMPL_DT.length() == 14){
					szCARLOAD_END_DATE = szYD_CARLD_CMPL_DT.substring(0, 8);
					szCARLOAD_END_TIME = szYD_CARLD_CMPL_DT.substring(8, 14);
				}
 
				outRec = JDTORecordFactory.getInstance().create();	 
				
				outRec.setField("TC_CODE"         , new String("YDDMR073"));
				outRec.setField("TC_CREATE_DDTT"  , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				outRec.setField("CARD_NO"         , szCARD_NO);
				outRec.setField("CAR_NO"          , szCAR_NO);
				outRec.setField("YD_GP"           , szYD_GP);
				outRec.setField("CARLOAD_END_DATE", szCARLOAD_END_DATE);
				outRec.setField("CARLOAD_END_TIME", szCARLOAD_END_TIME);
				outRec.setField("TRANS_WORD_DATE" , szTRANS_ORD_DATE);
				outRec.setField("TRANS_WORD_SEQNO", szTRANS_ORD_SEQNO);
				outRec.setField("CR_FRTOMOVE_GP"	, szCR_FRTOMOVE_GP);

				
				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
			//}
		}catch(Exception e){
			szMsg = "코일출하상차완료  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR073()
	
	
	/**
	 * YDDMR075 : 코일이송하차개시PDA 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR075(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();
		YdUtils ydUtils           = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		  = null;
		JDTORecord outRec 		  = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg              = "";
		String szMethodName       = "makeDMR075";
		String szOperationName    = "코일이송하차개시PDA";
		
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

		// 하차개시일자및시각 (야드상차완료일시)
		String szYD_CARUD_ST_DT = "";
		String szCARUD_START_DATE = "";
		String szCARUD_START_TIME = "";

		int intRtnVal             = 0;
		
		JDTORecordSet rsResult = null;
		JDTORecord recTemp = null;

		YdStockDao ydStockDao = new YdStockDao();
		int nRet = 0;
		String szCR_FRTOMOVE_GP		= "";

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
				
				//냉연이송구분
				szCR_FRTOMOVE_GP = ydDaoUtils.paraRecChkNull(recPara, "CR_FRTOMOVE_GP");

				// 하차 개시 일자 , 하차 개시 시각	
				szYD_CARUD_ST_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_ST_DT").trim();
				if(szYD_CARUD_ST_DT != "" && szYD_CARUD_ST_DT.length() == 14){
					szCARUD_START_DATE = szYD_CARUD_ST_DT.substring(0, 8);
					szCARUD_START_TIME = szYD_CARUD_ST_DT.substring(8, 14);
				}
 
				outRec = JDTORecordFactory.getInstance().create();	 
				
				outRec.setField("TC_CODE"         , new String("YDDMR075"));
				outRec.setField("TC_CREATE_DDTT"  , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				outRec.setField("CARD_NO"         , szCARD_NO);
				outRec.setField("CAR_NO"          , szCAR_NO);
				outRec.setField("YD_GP"           , szYD_GP);
				outRec.setField("CARUD_START_DATE", szCARUD_START_DATE);
				outRec.setField("CARUD_START_TIME", szCARUD_START_TIME);
				outRec.setField("TRANS_WORD_DATE" , szTRANS_ORD_DATE);
				outRec.setField("TRANS_WORD_SEQNO", szTRANS_ORD_SEQNO);
				outRec.setField("CR_FRTOMOVE_GP"	, szCR_FRTOMOVE_GP);

				
				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
			//}
		}catch(Exception e){
			szMsg = "코일출하상차완료  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR075()
	
	
	/**
	 * YDDMR076 : 코일이송하차완료PDA 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeDMR076(JDTORecord inRec, JDTORecordSet outRecSet){
		// 차량스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();
		YdUtils ydUtils           = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		  = null;
		JDTORecord outRec 		  = null;

		JDTORecordSet rsGetYdCarsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg              = "";
		String szMethodName       = "makeDMR076";
		String szOperationName    = "코일이송하차완료PDA";
		
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

		// 하차완료일자및시각 (야드상차완료일시)
		String szYD_CARUD_CMPL_DT = "";
		String szCARUD_END_DATE = "";
		String szCARUD_END_TIME = "";

		int intRtnVal             = 0;
 
		String szCR_FRTOMOVE_GP		= "";

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
				
				//냉연이송구분
				szCR_FRTOMOVE_GP = ydDaoUtils.paraRecChkNull(recPara, "CR_FRTOMOVE_GP");

				// 하차 완료 일자 , 하차 완료 시각	
				szYD_CARUD_CMPL_DT = ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_CMPL_DT").trim();
				if(szYD_CARUD_CMPL_DT != "" && szYD_CARUD_CMPL_DT.length() == 14){
					szCARUD_END_DATE = szYD_CARUD_CMPL_DT.substring(0, 8);
					szCARUD_END_TIME = szYD_CARUD_CMPL_DT.substring(8, 14);
				}
 
				outRec = JDTORecordFactory.getInstance().create();	 
				
				outRec.setField("TC_CODE"         , new String("YDDMR076"));
				outRec.setField("TC_CREATE_DDTT"  , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				outRec.setField("CARD_NO"         , szCARD_NO);
				outRec.setField("CAR_NO"          , szCAR_NO);
				outRec.setField("YD_GP"           , szYD_GP);
				outRec.setField("CARUD_END_DATE", szCARUD_END_DATE);
				outRec.setField("CARUD_END_TIME", szCARUD_END_TIME);
				outRec.setField("TRANS_WORD_DATE" , szTRANS_ORD_DATE);
				outRec.setField("TRANS_WORD_SEQNO", szTRANS_ORD_SEQNO);
				outRec.setField("CR_FRTOMOVE_GP"	, szCR_FRTOMOVE_GP);

				
				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szSessionName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szSessionName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
			//}
		}catch(Exception e){
			szMsg = "코일출하상차완료  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeDMR076()
	//---------------------------------------------------------------------------	
} // end of class MakeTcDM

