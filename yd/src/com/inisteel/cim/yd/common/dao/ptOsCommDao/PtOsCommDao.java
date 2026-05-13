/**
 * 클래스명 : 진행관리 OS 공통 테이블
 */
package com.inisteel.cim.yd.common.dao.ptOsCommDao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

/**
 * @author Administrator
 *
 */
public class PtOsCommDao {
	// Dao Name
	private String szDaoName = getClass().getName();

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	private CCommUtils	   commUtils   = new CCommUtils();
	
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommDaoE";
	private String szQueryIdGet2 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommDaoD";
	private String szQueryIdGet3 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommDaoY";
	private String szQueryIdGet4 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForStkChar";
	
	// 김종건(2009. 8. 12) 입고 예정 - Off-Line 입고 대상
	private String szQueryIdGet5 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPlateYdOffLine_PIDEV";
	// 김종건(2009. 8. 17) 입고 예정 - 지시 확정
	private String szQueryIdGet6 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPlateYdRefurWo_PIDEV";
	// 김종건(2009. 9. 7) 저장 속성 미확정 DATA 
	private String szQueryIdGet7 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPlateYdCharMapping";
	// 김종건(2009. 9. 9) 주문 진행 현황 (PAGEING) 
	private String szQueryIdGet8 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForOrderProc_PIDEV";
	// 김종건(2009. 9. 10) 입고 예정 - KARTPA
	private String szQueryIdGet9 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPlateYdRollNm_PIDEV";
	// 김종건(2009. 9. 17) 주문 번호 및 행번의 폭 및 길이 값 조회
	private String szQueryIdGet10 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPlateYdWLByOrd";
	// 임춘수 - 2009.09.22 : OS공통테이블 PILING코드  조회 - Paging처리
	private String szQueryIdGet11 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPilingCd_PIDEV";
	// 임춘수 - 2009.09.22 : 기존속성과 맵핑이 되고 Piling Code가 존재하지 않는 OS 조회
	private String szQueryIdGet12 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPilingCdNull";
	// 임춘수 - 2009.09.22 : 고객코드, 목적지코드, 수요가코드로  OS 조회
	private String szQueryIdGet13 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommByCustDestDemander";
	
	// 석창화 - 2009.11.05 : 1차저장계획등록 -주문상태(ALL)
	private String szQueryIdGet14 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForOrderRcptPlnAll_PIDEV";
	// 석창화 - 2009.12.04 : 저장그룹재고현황
	private String szQueryIdGet15 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForRcptGrpPdList_PAGE_PIDEV";
	// 석창화 - 2009.12.08 : 수요가코드조회
	private String szQueryIdGet16 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getDemanderList_PIDEV";
	// 석창화 - 2009.12.09 : 목적지코드조회
	private String szQueryIdGet17 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getDestCdList_PIDEV";
	// 석창화 - 2009.12.10 : 목적지코드로만 저장속성그룹 조회
	private String szQueryIdGet18 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPilingCdCommon";
	// 석창화 - 2009.12.21 : 투입주문중 Pilig_cd 미생성 주문 조회
	private String szQueryIdGet19 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPTYDJ004_PIDEV";
	// 석창화 - 2010.03.08 : 코일공통의 재료진도코드와 야드구분 조회
	private String szQueryIdGet20 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtCoilCommByCoilNo";
	// 박종호 - 2024.06.13 : 선박코드정보 조회
	private String szQueryIdGet21 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getShipCdList_PIDEV";	
	

	
	// 석창화 - 2009.12.21 : 투입주문중 Pilig_cd 미생성 주문 조회
	private String szQueryIdGet100 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPTYDJ004_NEW_PIDEV";	
	private String szQueryIdGet101 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommByDetatilArrCdE_PIDEV";	
	private String szQueryIdGet102 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommByDetatilArrCd_PIDEV";	
	private String szQueryIdGet103 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPilingCd_NEW_PIDEV";	

	private String szQueryIdGet110 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForStrCharGrpCd"; 
	private String szQueryIdGet111 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForStrCharGrpCd1_PIDEV"; //대표	
	private String szQueryIdGet112 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForStrCharGrpCd2_PIDEV"; //일반	
	private String szQueryIdGet113 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForStrCharGrpCd3_PIDEV"; //고객	
	private String szQueryIdGet114 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPilingNullD_PIDEV"; 	
	private String szQueryIdGet200 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPilingNull_PIDEV";	
	private String szQueryIdGet201 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForSameDestCustPigCustDtlArr_PIDEV";	
	private String szQueryIdGet202 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForYardStkNO";	
	private String szQueryIdGet203 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForSameDestCustDtlArr_PIDEV";	
	private String szQueryIdGet204 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForSameDestDtlArr_PIDEV";	
	private String szQueryIdGet205 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPilingNullCnt";	
	private String szQueryIdGet206 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsComm_PIDEV";
	
	//저장계획 
	private String szQueryIdGet300 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommLocPlanCd_PIDEV";	
	private String szQueryIdGet301 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommPriorLocPlanAcc";	
	
	//update query id - 
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.updPtOsCommDaoForPilingCd";
	//update query id - 
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.updPtOsCommDao";
	// 김종건(2009. 9. 8) 저장 속성 Matching(Piling Code, Book-Out Code Set!)
	private String szQueryIdUpd3 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.updPtOsCommDaoForYdPlatePilingBook";
	// 김종건(2009. 9. 16) 저장 속성 취소 by Order No
	private String szQueryIdUpd4 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.updPtOsCommDaoForPilingCdByOrderNo";
	// 김종건(2009. 10. 06) 입고 예정 주문 TABLE Piling Code / Book-Out 위치 update 
	private String szQueryIdUpd5 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.updPtOsCommDaoForPCdBOutByOrderNo";	
	//석창화(2010. 02. 03) PTYDJ004 update 
	private String szQueryIdUpd6 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.updPtOsCommDaoForPTYDJ004";
	//윤재광(2010. 04. 13) 파일링코드 UPDATE 
	private String szQueryIdUpd7 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.updPtOsCommPilingCd";

	private String szQueryIdUpd8 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.updPtOsCommYdRecpStrLoc";
	
	private String szQueryIdUpd9 = "com.inisteel.cim.yd.common.dao.ptOsCommDao.updPtOsCommDaoForPTYDJ005";
	/**
	 *      [A] 오퍼레이션명 : OS공통테이블 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_WTCL_TNK_SCH_ID,STL_NO, 
	 *         								1:YD_WTCL_TNK_SCH_ID
	 *         								4:V_PAGE_CNT1, V_ROW_CNT1, V_PAGE_CNT2, V_ROW_CNT1
	 *         								6:ORD_NO
	 *         								7:7:V_SEARCH_GBN, V_CONFIRM_DELIVER_END_DATE, V_YD_STR_CHAR_GP, V_DEST_CD, V_DEMANDER_CD, V_ORD_NO, V_ORD_DTL
	 *         								8:V_ROLL_UNIT_NAME
	 *         								9:ORD_NO, ORD_DTL
	 *         								)
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getPtOsComm(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdWtclmtl";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkParameterForSelect(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
			else if (intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet2);
			else if (intGp == 2)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet3);
			else if (intGp == 3)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet4);
			else if (intGp == 4)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet5);
			else if (intGp == 5)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet6);
			else if (intGp == 6)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet7);
			else if (intGp == 7)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet8);
			else if (intGp == 8)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet9);
			else if (intGp == 9)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet10);
			else if (intGp == 10)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet11);
			else if (intGp == 11)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet12);
			else if (intGp == 12)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet13);
			else if (intGp == 13)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet14);
			else if (intGp == 14)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet15);
			else if (intGp == 15)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet16);
			else if (intGp == 16)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet17);
			else if (intGp == 17)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet18);
			else if (intGp == 18)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet19);
			else if (intGp == 19)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet20);
			else if (intGp == 20)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet21);			
			else if (intGp == 100)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet100);
			else if (intGp == 101)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet101);
			else if (intGp == 102)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet102);
			else if (intGp == 103)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet103);
			else if (intGp == 110)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet110);
			else if (intGp == 111)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet111);
			else if (intGp == 112)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet112);
			else if (intGp == 113)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet113);
			else if (intGp == 114)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet114);
			else if (intGp == 200)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet200);
			else if (intGp == 201)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet201);
			else if (intGp == 202)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet202);
			else if (intGp == 203)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet203);
			else if (intGp == 204)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet204);
			else if (intGp == 205)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet205);
			else if (intGp == 206)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet206);			
			else if (intGp == 300)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet300);
			else if (intGp == 301)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet301);
			
//PIDEV
			//PIDEV_S :병행가동용:PI_YD
//			String sPI_YD     = commUtils.nvl(recPara.getFieldString("V_PI_YD"), "*");			
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", szMethodName, "YD0001", commUtils.trim(recPara.getFieldString("JSPEED_QUERY_ID")), "APPPI0", sPI_YD, "*" );
//			recPara.setField("JSPEED_QUERY_ID", commUtils.trim(toQuery_ID));
			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0)
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
			else {
				//data not found
	//			szMsg = "data not found!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 3);
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal = rsTemp.size();
	} //end of getPtOsComm
	
	/**
	 *      [A] 오퍼레이션명 : 야드 저장속성 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(
	 *                              0: V_YD_STRCHAR_ID
	 *                              1:YD_STRCHAR_GRP_CD
	 *                              3:SEARCH_GBN, YD_STRCHAR_ID, DEST_CD, DEMANDER_CD, ORD_NO, ORD_DTL
	 *                              4:V_PAGE_CNT1, V_ROW_CNT1, V_PAGE_CNT2, V_ROW_CNT1
	 *                              6:ORD_NO
	 *                              7:V_SEARCH_GBN, V_CONFIRM_DELIVER_END_DATE, V_YD_STR_CHAR_GP, V_DEST_CD, V_DEMANDER_CD, V_ORD_NO, V_ORD_DTL
	 *                              8:V_ROLL_UNIT_NAME
	 *                              9:ORD_NO, ORD_DTL
	 *                              )
	 * @return boolean          true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameterForSelect(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		try {
			if (intGp == 3) {
				szFieldName = "V_SEARCH_GBN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_DTL";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CONFIRM_DELIVER_END_DATE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
//0116				
//				szFieldName = "V_DEST_CD";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
//				if (!blnErr) return blnErr;
				
				szFieldName = "V_DEMANDER_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STR_CHAR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 4){
				szFieldName = "V_YD_STR_LOC";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				
				szFieldName = "V_YD_PILING_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			}else if(intGp == 5){
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				
			}else if(intGp == 6){
//				szFieldName = "V_ORD_NO";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				
			}else if(intGp == 7){
				szFieldName = "V_SEARCH_GBN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CONFIRM_DELIVER_END_DATE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STR_CHAR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
//0116				
//				szFieldName = "V_DEST_CD";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
//				if (!blnErr) return blnErr;
				
				szFieldName = "V_DEMANDER_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_DTL";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				
			}else if(intGp == 8){
				szFieldName = "V_ROLL_UNIT_NAME";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'S', 0, 0);
				
				szFieldName = "V_YD_PILING_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			}else if(intGp == 9){
				szFieldName = "V_ORD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_DTL";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			}else if(intGp == 10){
				szFieldName = "V_SEARCH_GBN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STRCHAR_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
//0116				
//				szFieldName = "V_DEST_CD";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
//				if (!blnErr) return blnErr;
				
				szFieldName = "V_DEMANDER_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_DTL";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				
			}else if(intGp == 11){
//0116				
//				szFieldName = "V_DEST_CD";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
//				if (!blnErr) return blnErr;
				
				szFieldName = "V_DEMANDER_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CUST_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				//szFieldName = "V_ORD_DTL";
				//blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
				//if (!blnErr) return blnErr;
				
			}else if(intGp == 12){
//0116				
//				szFieldName = "V_DEST_CD";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, YdDaoUtils.STRING_TYPE, 0, 0);
//				if (!blnErr) return blnErr;
				
				szFieldName = "V_DEMANDER_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CUST_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 13){
				szFieldName = "V_ORD_PROG_STAT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
//0116												
//				szFieldName = "V_DEST_CD";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
//				if (!blnErr) return blnErr;
				
				szFieldName = "V_DEMANDER_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_DTL";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PILING_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
					
			}else if(intGp == 14){
								
				szFieldName = "V_ORD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STRCHAR_GRP_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_MTL_W_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_MTL_L_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				
				szFieldName = "V_ORD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_DTL";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
					
			} else if(intGp == 15){
				szFieldName = "V_CUST_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CUST_KO_NAME";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 40, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_COMREGNO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 14, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				
			} else if(intGp == 16){
//0116				
//				szFieldName = "V_DEST_CD";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
//				if (!blnErr) return blnErr;
				
				szFieldName = "V_DEST_NAME";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 40, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DEST_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DEST_AREA_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			}else if(intGp == 17){
//0116				
//				szFieldName = "V_DEST_CD";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
//				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_DTL";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 18){
//0116
//				szFieldName = "V_DEST_CD";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
//				if (!blnErr) return blnErr;
			}else if(intGp == 19){
				szFieldName = "V_COIL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}	
				
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkParameterForSelect
	
	
	/**
	 * OS공통테이블 업데이트 처리
	 * @param inRec
	 * @param intGp
	 * @return
	 * @throws DAOException
	 */
	public int updPtOsComm(JDTORecord inRec, int intGp) throws DAOException {
		String szMethodName = "updPtOsComm";
		String szMsg = null;
		int intRtnVal = 0;
		JDTORecord recOutPara = null;
		boolean blnChk_Field = true;
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0); 
			
			//parameter check
			blnChk_Field = this.chkParameterForUpdate(recOutPara, intGp);
			//parameter error return
			if (!blnChk_Field) return intRtnVal = -1;
			
			if (intGp == 1)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd1);
			else if (intGp == 2)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd3);
			else if (intGp == 3)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd4);
			else if (intGp == 4)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd5);			
			else if (intGp == 5)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd6);
			else if (intGp == 6)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd7);
			else if (intGp == 7)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd8);
			else if (intGp == 8)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd9);
			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			if (intRtnVal <= 0) intRtnVal = -3;
		}catch(JDTOException e) {	
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	}
	
	/**
	 * 파라미터 체크
	 * @param inRec
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public boolean chkParameterForUpdate(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		if( intGp == 1 ) {
			szFieldName = "V_YD_PILING_CD";						//Piling 코드
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_BOOK_OUT_LOC";					//Book-Out 위치
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_RCPT_STR_LOC";					//입고예정위치
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_NO";						//주문번호
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_DTL";						//주문행번
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
		} else if(intGp == 4) {
			szFieldName = "V_YD_PILING_CD";						//Piling 코드
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_BOOK_OUT_LOC";					//Book-Out 위치
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_RCPT_STR_LOC";					//입고예정위치
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_NO";						//주문번호
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;

		}  else if(intGp == 5) {
			szFieldName = "V_YD_PILING_CD";						//Piling 코드
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_BOOK_OUT_LOC";					//Book-Out 위치
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_RCPT_STR_LOC";					//입고예정위치
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_NO";						//주문번호
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_DTL";						//주문행번
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
		}  else if(intGp == 6) {
			szFieldName = "V_ORG_YD_PILING_CD";					//Piling 코드
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CHG_YD_PILING_CD";					//Piling 위치
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_NO";						//주문번호
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
		}

		return blnErr;
	}
}
