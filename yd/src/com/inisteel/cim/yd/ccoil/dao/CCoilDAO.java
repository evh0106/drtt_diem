/**
 * @(#)CCoilDAO
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2019/05/02
 *
 * @description      2열연 COIL 야드  DAO
 * ------------------------------------------------------------------------------
 * Ver.   수정일자     요청자  수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2019/05/02   정종균  이현진      최초 등록
 * 
 */
package com.inisteel.cim.yd.ccoil.dao;

import xlib.cmc.GridData;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.ccommon.dao.CCommDAO;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.yd.ccommon.util.CConstant;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

public class CCoilDAO extends DBAssistantDAO {
	
	private CCommUtils commUtils = new CCommUtils();
	private CCommDAO   commDao   = new CCommDAO();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();

	/**
	 *      [A] 오퍼레이션명 : 2열연크레인작업실적응답(YDY5L005) 전문 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYDY5L005(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "크레인작업실적응답 조회[CCoilDAO.getYDY5L005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try { 
			//수신 항목 값
			String msgId      = ""; //전문ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID
			//YD_WRK_PROG_STAT
			//YD_SCH_CD
			String ydL2WrGp   = commUtils.trim(rcvMsg.getFieldString("YD_L2_WR_GP"   )); //야드L2실적구분
			String ydL3HdRsCd = commUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD")); //야드L3처리결과코드
			String ydL3Msg    = commUtils.trim(rcvMsg.getFieldString("YD_L3_MSG"     )); //야드L3MESSAGE

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				return null;
			}

			if (ydEqpId.startsWith("J")) {
				msgId = "YDY5L005";
			} else {
				return null;
			}

			/**********************************************************
			* 2. 크레인작업실적응답 전문 생성
			**********************************************************/
			//야드L3Message가 없으면 생성
			if ("".equals(ydL3Msg)) {
				if ("U".equals(ydL2WrGp)) {
					ydL3Msg = "권상실적";
				} else if ("D".equals(ydL2WrGp)) {
					ydL3Msg = "권하실적";
				} else if ("E".equals(ydL2WrGp)) {
					ydL3Msg = "비상조업실적";
				} else if ("R".equals(ydL2WrGp)) {
					ydL3Msg = "고장복구실적";
				} else if ("M".equals(ydL2WrGp)) {
					ydL3Msg = "운전모드전환";
				} else if ("J".equals(ydL2WrGp)) {
					ydL3Msg = "지시요구";
				} else if ("F".equals(ydL2WrGp)) {
					ydL3Msg = "강제권하";
				} else if ("G".equals(ydL2WrGp)) {
					ydL3Msg = "강제권상요구";
				} else if ("T".equals(ydL2WrGp)) {
					ydL3Msg = "대차이동실적";
				} else {
					ydL3Msg = ydL2WrGp;
				}

				if ("0000".equals(ydL3HdRsCd)) {
					ydL3Msg = ydL3Msg + " 정상 처리";
				} else if ("9999".equals(ydL3HdRsCd)) {
					ydL3Msg = ydL3Msg + " 정보 없음";
				} else {
					ydL3Msg = ydL3Msg + " 오류 <" + logId + ">";
				}
			}

			StringBuffer sbMsg = new StringBuffer();

			sbMsg = sbMsg.append(msgId                                 ); //전문ID
			sbMsg = sbMsg.append(commUtils.getDateTime18()             ); //생성일,생성시간(yyyy-MM-ddHH:mm:ss)
			sbMsg = sbMsg.append("I"                                   ); //전문구분
			sbMsg = sbMsg.append("0078"                                ); //전문길이
			sbMsg = sbMsg.append(commUtils.getRPad(" "       , 29, " ")); //임시
			sbMsg = sbMsg.append(commUtils.getRPad(ydEqpId   ,  6, " ")); //야드설비ID
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")),  1, " ")); //야드작업진행상태
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )),  8, " ")); //야드스케쥴코드
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )), 18, " ")); //야드크레인스케쥴ID
			sbMsg = sbMsg.append(commUtils.getRPad(ydL2WrGp  ,  1, " ")); //야드L2실적구분
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3HdRsCd,  4, " ")); //야드L3처리결과코드
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3Msg   , 40, " ")); //야드L3Message

			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();

			sndL2Msg.setResultCode(logId);	//Log ID
			sndL2Msg.setResultMsg(mthdNm);	//Log Method Name
			sndL2Msg.addField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
			sndL2Msg.addField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
			sndL2Msg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage

			//전송 Data Return
			return commUtils.addSndData(sndL2Msg);
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return null;
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 공통야드 코드 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getYdCode(GridData gdReq) throws DAOException {
		String mthdNm = "코드조회[CCoilDAO.getYdCode] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			
			String itmGp = commUtils.trim(gdReq.getParam("V_ITM_GP")); //코드항목구분

			JDTORecord jrParam  = commUtils.getParam(logId, mthdNm, "getYdCode");
			jrParam.setField("YD_GP"   			, commUtils.trim(gdReq.getParam("V_YD_GP"))  );
			jrParam.setField("YD_STK_COL_GP"	, commUtils.trim(gdReq.getParam("V_YD_STK_COL_GP"))  );
			jrParam.setField("YD_STK_BED_NO"	, commUtils.trim(gdReq.getParam("V_YD_STK_BED_NO"))  );
			jrParam.setField("YD_BAY_GP"		, commUtils.trim(gdReq.getParam("V_YD_BAY_GP"))  );
			jrParam.setField("YD_LOC_GP"		, commUtils.trim(gdReq.getParam("V_YD_LOC_GP"))  );
			jrParam.setField("IN_GP"			, commUtils.trim(gdReq.getParam("V_IN_GP"))  );
			jrParam.setField("CD_GP"			, commUtils.trim(gdReq.getParam("V_CD_GP"))  );
			jrParam.setField("REPR_CD_GP"		, commUtils.trim(gdReq.getParam("V_REPR_CD_GP"))  );
			jrParam.setField("CD_EN_ID"			, commUtils.trim(gdReq.getParam("V_CD_EN_ID"))   );
			jrParam.setField("CD_CAT_ID"		, commUtils.trim(gdReq.getParam("V_CD_CAT_ID"))   );
			jrParam.setField("YD_BAY_EQP_GP"	, commUtils.trim(gdReq.getParam("V_YD_BAY_EQP_GP"))   );
			jrParam.setField("YD_EQP_GP"	    , commUtils.trim(gdReq.getParam("V_YD_EQP_GP"))   );
			//YYS 추가 수입존 저장위치 조회
			jrParam.setField("CV_SCH_CD"	    , commUtils.trim(gdReq.getParam("V_CV_SCH_CD"))   );
			jrParam.setField("CV_SEARCH"	    , commUtils.trim(gdReq.getParam("V_CV_SEARCH"))   );
			if ("YD_BAY_GP".equals(itmGp)) {
				trtNm = "동구분";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdBayGp 
				SELECT DISTINCT YD_BAY_GP AS CD_VAL
				     , YD_BAY_GP||'동'    AS CD_NM
				  FROM TB_YD_STKCOL
				 WHERE YD_GP IN ('H','J')
				   AND YD_GP = :V_YD_GP
				   AND DEL_YN = 'N'
				 ORDER BY YD_BAY_GP
				*/ 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdBayGp";  
			} else if ("YD_EQP_GP".equals(itmGp)) { //00~99
				trtNm = "스판구분";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdEqpGp";
			} else if ("YD_LOC_GP".equals(itmGp)) { //00~99, 설비
				trtNm = "위치구분";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdLocGp";
			} else if ("YD_STK_COL_NO".equals(itmGp)) {
				trtNm = "적치열번호";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdStkColNo";
			} else if ("YD_STK_BED_NO".equals(itmGp)) {
				trtNm = "적치Bed번호";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdStkBedNo";
			} else if ("STACK_LAYER_GP".equals(itmGp)) {
				trtNm = "적치단번호";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdStkLyrNo"; //2열연 적치단은 3자리 (etc. 001)
				
			} else if ("YD_SCH_CD".equals(itmGp)) {
				trtNm = "스케줄코드";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdSchCd";
				
			} else if ("YD_EQP_ID_CR".equals(itmGp)) {
				trtNm = "크레인설비ID";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdCrEqp";

			} else if ("YD_EQP_ID_TC_BY_BAY".equals(itmGp)) {
				trtNm = "동별대차설비ID";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdEqpTcByBay";
				
			} else if ("YD_LOC_GP2".equals(itmGp)) { //00~99, 설비
				trtNm = "위치구분(기존야드구분기준)";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdLocGp2";
			} else if ("YD_BAY".equals(itmGp) || "YD_EQP".equals(itmGp) || "YD_COL".equals(itmGp) ) {
				trtNm = "TB_YD_RULE 제품 가상저장위치 포함 조회";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdRule";  //YD_RULE 테이블 조회				 

			} else if ("YD_RULE".equals(itmGp)) {
				// REPR_CD_GP, CD_GP 항목으로 DTL_ITEM1, DTL_ITEM2 데이터 반환
				trtNm = "기준정보";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdRule";
				//sSJH				
			} else if ("YD_SCH_CD_YD_LOC_GP".equals(itmGp)) {
				trtNm = "스케줄코드";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdSchCdYdLocGp 
				SELECT YD_SCH_CD         AS CD_VAL
				     , CD_CONTENTS       AS CD_NM
				  FROM TB_YD_SCHRULE
				 WHERE YD_GP     = 'J'
				   AND 'Y' = CASE WHEN :V_YD_LOC_GP = 'H'  AND SUBSTR(YD_SCH_CD,8,1) = 'H' THEN 'Y'
				                  WHEN :V_YD_LOC_GP = 'J'  AND SUBSTR(YD_SCH_CD,8,1) <> 'H' THEN 'Y'
				                  ELSE 'N' END 
				   AND YD_BAY_GP LIKE :V_YD_BAY_GP||'%'
				   AND DEL_YN = 'N'
				 ORDER BY YD_BAY_GP, CD_CONTENTS
			*/	 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdSchCdYdLocGp";
				//sSJH				
			} else if ("YD_SCH_CD_RULL_YD_LOC_GP".equals(itmGp)) {
				trtNm = "RULL 검색조건스케줄코드";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getRule006ComboList 
				SELECT DTL_ITEM1 AS CD_VAL
				     , DTL_ITEM2 AS CD_NM
				  FROM TB_YD_RULE 
				 WHERE REPR_CD_GP = 'APP005'
				   AND SUBSTR(DTL_ITEM1,2,1) = :V_YD_BAY_GP
				  ORDER BY DTL_ITEM1  
			*/	 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getRule006ComboList";
			} else if ("YD_BAY_GP_YD_LOC_GP".equals(itmGp)) { 
				trtNm = "동구분";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdBayGpYdLocGp 
				SELECT DISTINCT YD_BAY_GP AS CD_VAL
				     , YD_BAY_GP||'동'    AS CD_NM
				  FROM TB_YD_STKCOL
				 WHERE YD_GP = 'J'
				   AND 'Y' = CASE WHEN :V_YD_LOC_GP = 'H'  AND YD_LOC_GP = 'H' THEN 'Y'
					              WHEN :V_YD_LOC_GP = 'J'  AND YD_LOC_GP = 'J' THEN 'Y'
					              ELSE 'N' END 
				   AND DEL_YN = 'N'
				 ORDER BY YD_BAY_GP
				*/ 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdBayGpYdLocGp";
			} else if ("YD_BAY_GP_YD_LOC_GP_LM".equals(itmGp)) { 
				trtNm = "동구분(LM동 추가)";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdBayGpYdLocGpLM 
				WITH BAY_TBL AS 
				(
				SELECT DISTINCT YD_BAY_GP AS CD_VAL
				     , YD_BAY_GP||'동'    AS CD_NM
				  FROM TB_YD_STKCOL
				 WHERE YD_GP = 'J'
				   AND 'Y' = CASE WHEN :V_YD_LOC_GP = 'H'  AND YD_LOC_GP = 'H' THEN 'Y'
					              WHEN :V_YD_LOC_GP = 'J'  AND YD_LOC_GP = 'J' THEN 'Y'
					              ELSE 'N' END 
				   AND DEL_YN = 'N'
				 UNION ALL
				SELECT 'L'   AS CD_VAL
				     , 'L동' AS CD_NM
				  FROM DUAL   
				 UNION ALL
				SELECT 'M'   AS CD_VAL
				     , 'M동' AS CD_NM
				  FROM DUAL   
				) 
				SELECT CD_VAL
				     , CD_NM
				  FROM BAY_TBL  
				 WHERE 'Y' = CASE WHEN :V_YD_LOC_GP = 'H'  AND CD_VAL NOT IN ('L','M') THEN 'Y'
					              WHEN :V_YD_LOC_GP = 'J' THEN 'Y'
					              ELSE 'N' END 
				 ORDER BY CD_VAL         
				*/ 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdBayGpYdLocGpLM";
				
			} else if ("YD_BAY_GP_EQP_YD_LOC_GP_LM".equals(itmGp)) { 
				trtNm = "동구분(LM동 추가)";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdScrpBayGpEqpYdLocGpLM 
				WITH BAY_TBL AS 
				(
				SELECT DISTINCT YD_BAY_GP AS CD_VAL
				     , YD_BAY_GP||'동'    AS CD_NM
				  FROM TB_YD_STKCOL
				 WHERE YD_GP = 'J'
				   AND 'Y' = CASE WHEN :V_YD_LOC_GP = 'H'  AND YD_LOC_GP = 'H' THEN 'Y'
					              WHEN :V_YD_LOC_GP = 'J'  AND YD_LOC_GP = 'J' THEN 'Y'
					              ELSE 'N' END 
				   AND DEL_YN = 'N'
				   AND YD_EQP_GP LIKE '%'||NVL(:V_YD_EQP_GP,'')||'%'
				 UNION ALL
				SELECT 'L'   AS CD_VAL
				     , 'L동' AS CD_NM
				  FROM DUAL   
				 UNION ALL
				SELECT 'M'   AS CD_VAL
				     , 'M동' AS CD_NM
				  FROM DUAL   
				) 
				SELECT CD_VAL
				     , CD_NM
				  FROM BAY_TBL  
				 WHERE 'Y' = CASE WHEN :V_YD_LOC_GP = 'H'  AND CD_VAL NOT IN ('L','M') THEN 'Y'
					              WHEN :V_YD_LOC_GP = 'J' THEN 'Y'
					              ELSE 'N' END 
				 ORDER BY CD_VAL         
				 */ 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdScrpBayGpEqpYdLocGpLM";

			} else if ("YD_SPAN_GP_YD_LOC_GP".equals(itmGp)) { 
				trtNm = "스판구분";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdSpanGpYdLocGp
				SELECT DISTINCT YD_EQP_GP   AS CD_VAL
				     , CASE WHEN YD_EQP_GP = 'TC' THEN '대차' 
				            WHEN YD_EQP_GP = 'PT' THEN 'Pallet' 
				            WHEN YD_EQP_GP = 'TT' THEN 'TT-Car' 
				            WHEN YD_EQP_GP = 'GF' THEN '지포장' 
				            WHEN YD_EQP_GP = 'FE' THEN 'HFL입측' 
				            WHEN YD_EQP_GP = 'KE' THEN 'SPM입측' 
				            WHEN YD_EQP_GP = 'KD' THEN 'SPM출측' 
				            ELSE YD_EQP_GP||'스판' END    AS CD_NM
				  FROM TB_YD_STKCOL
				 WHERE YD_GP = 'J'
				   AND YD_BAY_GP = :V_YD_BAY_GP
				   AND ((YD_EQP_GP BETWEEN '00' AND '99') OR (YD_EQP_GP NOT IN  ('CR','CV','SC' ) ))
				   AND 'Y' = CASE WHEN :V_YD_LOC_GP = 'H'  AND YD_LOC_GP = 'H' THEN 'Y'
					              WHEN :V_YD_LOC_GP = 'J'  AND YD_LOC_GP = 'J' THEN 'Y'
					              ELSE 'N' END 
				   AND DEL_YN = 'N'
				 ORDER BY YD_EQP_GP
				*/ 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdSpanGpYdLocGp";
				
			} else if ("YD_SPAN_GP_YD_LOC_GP_SPAN_TC".equals(itmGp)) { 
				trtNm = "스판구분(SPAN, 대차)";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdSpanGpYdLocGpSpanTc
				SELECT DISTINCT YD_EQP_GP   AS CD_VAL
				     , CASE WHEN YD_EQP_GP = 'TC' THEN '대차' 
				            ELSE YD_EQP_GP||'스판' END    AS CD_NM
				  FROM TB_YD_STKCOL A
				     , (SELECT * FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP002' AND CD_GP = 'J' ) B
				 WHERE YD_GP = 'J'
				   AND YD_BAY_GP = :V_YD_BAY_GP
				   AND ((YD_EQP_GP BETWEEN '00' AND '99') OR (YD_EQP_GP IN  ('스판','TC')))
				   AND 'Y' = CASE WHEN B.ITEM1 = 'Y'                           THEN 'Y'
				                  WHEN :V_YD_LOC_GP = 'H'  AND YD_LOC_GP = 'H' THEN 'Y'
					              WHEN :V_YD_LOC_GP = 'J'  AND YD_LOC_GP = 'J' THEN 'Y'
					              ELSE 'N' END 
				   AND A.DEL_YN = 'N'
				 ORDER BY YD_EQP_GP
				*/ 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdSpanGpYdLocGpSpanTc";

			} else if ("YD_SPAN_GP_YD_LOC_GP_GF".equals(itmGp)) { 
				trtNm = "스판구분";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdSpanGpYdLocGpGF
				SELECT DISTINCT YD_EQP_GP   AS CD_VAL
				     , CASE WHEN YD_EQP_GP = 'TC' THEN '대차'
				            WHEN YD_EQP_GP = 'GF' THEN '지포장'
				            ELSE YD_EQP_GP||'스판'
				       END AS CD_NM
				  FROM TB_YD_STKCOL A
				     , (SELECT * FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP002' AND CD_GP = 'J' ) B
				 WHERE YD_GP     = 'J'
				   AND YD_BAY_GP = :V_YD_BAY_GP
				   AND ((YD_EQP_GP BETWEEN '00' AND '99') OR (YD_EQP_GP  IN  ('TC', 'GF')))
				   AND 'Y' = CASE WHEN B.ITEM1 = 'Y'                           THEN 'Y'
				                  WHEN :V_YD_LOC_GP = 'H'  AND YD_LOC_GP = 'H' THEN 'Y'
				                  WHEN :V_YD_LOC_GP = 'J'  AND YD_LOC_GP = 'J' THEN 'Y'
				                  ELSE 'N' END
				   AND A.DEL_YN = 'N'
				 ORDER BY YD_EQP_GP
				*/ 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdSpanGpYdLocGpGF";

			} else if ("YD_STK_COL_NO_YD_LOC_GP".equals(itmGp)) { 
				trtNm = "열구분";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdStkColNoYdLocGp 
				SELECT DISTINCT YD_STK_COL_NO   AS CD_VAL
				     , YD_STK_COL_NO            AS CD_NM
				  FROM TB_YD_STKCOL
				 WHERE YD_GP = 'J'
				   AND YD_BAY_GP||YD_EQP_GP = :V_YD_BAY_EQP_GP
				   AND ((YD_EQP_GP BETWEEN '00' AND '99') OR (YD_EQP_GP NOT IN  ('CR','CV','SC' ) ))
				   AND 'Y' = CASE WHEN :V_YD_LOC_GP = 'H'  AND YD_LOC_GP = 'H' THEN 'Y'
					              WHEN :V_YD_LOC_GP = 'J'  AND YD_LOC_GP = 'J' THEN 'Y'
					              ELSE 'N' END 
				   AND DEL_YN = 'N'
				 ORDER BY YD_STK_COL_NO
				*/ 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdStkColNoYdLocGp";
				
			} else if ("YD_STK_COL_NO_EQP_YD_LOC_GP".equals(itmGp)) { 
				trtNm = "열구분";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdStkColNoEqpYdLocGp 
				SELECT DISTINCT YD_STK_COL_NO   AS CD_VAL
				     , YD_STK_COL_NO            AS CD_NM
				  FROM TB_YD_STKCOL
				 WHERE YD_GP = 'J'
				   AND YD_BAY_GP||YD_EQP_GP = :V_YD_BAY_EQP_GP
				   AND 'Y' = CASE WHEN :V_YD_LOC_GP = 'H'  AND YD_LOC_GP = 'H' THEN 'Y'
					              WHEN :V_YD_LOC_GP = 'J'  AND YD_LOC_GP = 'J' THEN 'Y'
					              ELSE 'N' END 
				   AND DEL_YN = 'N'
				 ORDER BY YD_STK_COL_NO
				 */ 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdStkColNoEqpYdLocGp";
				
			} else if ("YD_SPAN_GP_YD_LOC_GP_SPLIT".equals(itmGp)) { 
				trtNm = "스판구분";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdSpanGpYdLocGpSplit
				SELECT DISTINCT
				       YD_EQP_GP          AS CD_VAL
				     , YD_EQP_GP||'스판'  AS CD_NM
				  FROM TB_YD_STKCOL A
				 WHERE YD_GP     = 'J'
				   AND YD_BAY_GP = :V_YD_BAY_GP
				   AND YD_LOC_GP = :V_YD_LOC_GP
				   AND YD_EQP_GP BETWEEN '00' AND '99'
				   AND A.DEL_YN = 'N'
				 ORDER BY YD_EQP_GP
				*/
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdSpanGpYdLocGpSplit";
				
			} else if ("YD_STK_COL_NO_YD_LOC_GP_SPLIT".equals(itmGp)) { 
				trtNm = "열구분";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdStkColNoYdLocGpSplit
				SELECT DISTINCT
				       YD_STK_COL_NO AS CD_VAL
				     , YD_STK_COL_NO AS CD_NM
				  FROM TB_YD_STKCOL A
				 WHERE YD_GP                = 'J'
				   AND YD_BAY_GP||YD_EQP_GP = :V_YD_BAY_EQP_GP
				   AND YD_LOC_GP            = :V_YD_LOC_GP
				   AND YD_EQP_GP BETWEEN '00' AND '99'
				   AND A.DEL_YN = 'N'
				 ORDER BY YD_STK_COL_NO
				*/ 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdStkColNoYdLocGpSplit";
				
			} else if ("YD_EQP_ID_CR_NM".equals(itmGp)) { 
				trtNm = "CR구분";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getJspYdEqpList 
				SELECT YD_EQP_ID, YD_EQP_ID   AS CODE             
				     , YD_EQP_NAME || '(' || decode(YD_EQP_WRK_MODE2,'A','무','R','리','유') || ')'    AS NAME
				     , YD_BAY_GP AS YD_BAY_GP
				     , (CASE WHEN SUBSTR(YD_EQP_ID,5,2) IN('B1','B2','C1','C2') THEN YD_BAY_GP||'2' 
				             WHEN SUBSTR(YD_EQP_ID,5,2) IN('B3','B4','C3','C4') THEN YD_BAY_GP||'1' 
				             ELSE YD_BAY_GP END
				       ) AS YD_BAY_GP2
				     , YD_EQP_STAT    AS YD_EQP_STAT          
				  FROM TB_YD_EQP                          
				 WHERE SUBSTR(YD_EQP_ID,1,1) = 'J'
				   AND NVL(YD_BAY_GP, '*') LIKE  :V_YD_BAY_GP || '%' 
				   AND NVL(YD_EQP_GP, '*') LIKE  :V_YD_EQP_GP || '%' 
				 ORDER BY YD_EQP_GP desc ,YD_EQP_ID  
				*/ 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getJspYdEqpList";
				
			} else if ("MV_CAR_NO".equals(itmGp)) { 
				trtNm = "동간이적차량";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeMvCarNo
				 SELECT * 
				   FROM (
				        SELECT 'H' AS GP
				             , DTL_ITEM1
				             , DTL_ITEM2 
				          FROM TB_YD_RULE
				         WHERE REPR_CD_GP = 'APP011' 
				           AND DTL_ITEM1 IS NOT NULL
				           AND ITEM1 = 'Y'
				        UNION ALL
				        SELECT 'J' AS GP
				             , DTL_ITEM1
				             , DTL_ITEM2 
				          FROM TB_YD_RULE
				         WHERE REPR_CD_GP = 'APP012'  
				           AND DTL_ITEM1 IS NOT NULL
				           AND ITEM1 = 'Y'
				         )
				  WHERE GP = :V_YD_LOC_GP 
				*/ 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeMvCarNo";

			} else if ("YD_BAY_GP_CARWAY".equals(itmGp)) { 
				trtNm = "동구분(차량통로기준)";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdBayGpCarWay
				WITH BAY_TBL AS
				(
				SELECT DISTINCT YD_BAY_GP AS CD_VAL
				     , YD_BAY_GP||'동'    AS CD_NM
				  FROM TB_YD_STKCOL
				 WHERE YD_GP = 'J'
				   AND 'Y' = CASE WHEN :V_YD_LOC_GP = 'H'  AND YD_LOC_GP = 'H' THEN 'Y'
				                  WHEN :V_YD_LOC_GP = 'J'  AND YD_LOC_GP = 'J' THEN 'Y'
				                  ELSE 'N' END
				   AND DEL_YN = 'N'
				 UNION ALL SELECT 'B1' AS CD_VAL, 'B1동' AS CD_NM FROM DUAL
				 UNION ALL SELECT 'B2' AS CD_VAL, 'B2동' AS CD_NM FROM DUAL
				 UNION ALL SELECT 'C1' AS CD_VAL, 'C1동' AS CD_NM FROM DUAL
				 UNION ALL SELECT 'C2' AS CD_VAL, 'C2동' AS CD_NM FROM DUAL
				)
				SELECT CD_VAL
				     , CD_NM
				  FROM BAY_TBL
				 WHERE 'Y' = CASE WHEN :V_YD_LOC_GP = 'H' AND CD_VAL NOT IN ('B1','B2','C1','C2') THEN 'Y'
				                  WHEN :V_YD_LOC_GP = 'J' AND CD_VAL NOT IN ('B', 'C')            THEN 'Y'
				                  ELSE 'N' END
				 ORDER BY CD_VAL      
				*/ 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdBayGpCarWay";
				
				
			} else if ("YD_SPAN_GP_YD_EQP_GP_YD_LOC_GP".equals(itmGp)) { 
				trtNm = "스판/설비구분(소재기준스판설비)";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getSpanEqpGpForH
				SELECT YD_EQP_GP AS CD_VAL
				     , CASE WHEN YD_EQP_GP = 'TC' THEN '대차'
				            WHEN YD_EQP_GP = 'PT' THEN 'Pallet'
				            WHEN YD_EQP_GP = 'GF' THEN '지포장'
				            WHEN YD_EQP_GP = 'FE' THEN 'HFL입측'
				            WHEN YD_EQP_GP = 'FD' THEN 'HFL출측'
				            WHEN YD_EQP_GP = 'KE' THEN 'SPM입측'
				            WHEN YD_EQP_GP = 'KD' THEN 'SPM출측'
				            WHEN YD_EQP_GP = 'CV' THEN '컨베이어'
				            WHEN YD_EQP_GP = 'CD' THEN '크래들롤'
				            WHEN YD_EQP_GP = 'SC' THEN '스크랩'
				            ELSE YD_EQP_GP||'스판'
				       END AS CD_NM
				  FROM (SELECT DISTINCT(YD_EQP_GP) AS YD_EQP_GP
				          FROM TB_YD_STKCOL
				         WHERE DEL_YN    = 'N'
				           AND YD_LOC_GP = :V_YD_LOC_GP
				           AND YD_BAY_GP = :V_YD_BAY_GP
				       )
				 ORDER BY YD_EQP_GP
				 */ 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getSpanEqpGpForH";
				
				
			} else if ("YD_STK_COL_NO_YD_LOC_GP_YD_EQP_GP".equals(itmGp)) { 
				trtNm = "열구분(LOC_GP에따른모든열조회)";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getAllYdStkColNo
				SELECT YD_STK_COL_NO AS CD_VAL
				     , YD_STK_COL_NO AS CD_NM
				  FROM TB_YD_STKCOL
				 WHERE 1=1
				   AND DEL_YN                    = 'N'
				   AND YD_GP                     = 'J'
				   AND YD_LOC_GP                 = :V_YD_LOC_GP
				   AND SUBSTR(YD_STK_COL_GP,1,4) = 'J'||:V_YD_STK_COL_GP
				 ORDER BY CD_VAL
				*/
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getAllYdStkColNo";
				
			} else if ("YD_SPAN_GP_CARWAY".equals(itmGp)) { 
				trtNm = "스판구분(차량통로기준)";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdSpanGpCarWay
				SELECT DISTINCT
				       YD_EQP_GP         AS CD_VAL
				     , YD_EQP_GP||'스판'  AS CD_NM
				  FROM TB_YD_STKCOL A
				 WHERE YD_GP     = 'J'
				   AND YD_LOC_GP = :V_YD_LOC_GP
				   AND YD_BAY_GP = SUBSTR(:V_YD_BAY_GP, 0, 1)
				   AND A.DEL_YN = 'N'
				   AND 'Y' = CASE WHEN :V_YD_LOC_GP = 'H' AND YD_EQP_GP BETWEEN '00' AND '99' THEN 'Y'
				                  WHEN :V_YD_LOC_GP = 'J' AND SUBSTR(:V_YD_BAY_GP, 0, 1) IN ('B', 'C')
				                       THEN CASE WHEN SUBSTR(:V_YD_BAY_GP, 2, 1) = '2' AND SUBSTR(YD_STK_COL_GP, 3, 2) BETWEEN '00' AND '30' THEN 'Y'
				                                 WHEN SUBSTR(:V_YD_BAY_GP, 2, 1) = '1' AND SUBSTR(YD_STK_COL_GP, 3, 2) BETWEEN '31' AND '99' THEN 'Y'
				                                 ELSE 'N'
				                            END
				                  WHEN :V_YD_LOC_GP = 'J' AND YD_EQP_GP BETWEEN '00' AND '99' THEN 'Y'
				                  ELSE 'N'
				             END
				 ORDER BY YD_EQP_GP
				*/ 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeYdSpanGpCarWay";

			} else if ("YD_CMCODE".equals(itmGp)) { 
				trtNm = "공통코드조회2(CMCODE)";
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeCmCodes2 
					SELECT CD_VAL
					     , CD_MNNG AS CD_NM
					  FROM BRE.VW_CM_CODES
					 WHERE CD_EN_ID  = :V_CD_EN_ID
					   AND CD_CAT_ID = NVL(:V_CD_CAT_ID, 'HS0000')
					 ORDER BY SORT_SEQ, CD_VAL
				 */
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeCmCodes2";
				
			}else if ("YD_CV_SEARCHCODE".equals(itmGp)) { 
				trtNm = "스케줄코드별 권하위치조회 스판"; // yys 추가 202302
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeSchSearch 
					SELECT SUBSTR(BB.YD_STK_COL_GP,3,2) AS CD_VAL --,BB.YD_STK_BED_SRCH_SEQ
				       , SUBSTR(BB.YD_STK_COL_GP,3,2)||'스판' AS CD_NM
				           FROM (
				             SELECT  B.YD_STK_COL_GP                   
				               FROM TB_YD_LOCSRCHRNG A
				                  , TB_YD_LOCSRCHBED B
				              WHERE A.YD_LOC_SRCH_RNG_REG_SNO = B.YD_LOC_SRCH_RNG_REG_SNO
				                AND A.YD_SCH_CD               = :V_CV_SCH_CD
				                AND A.YD_ROUTE_GP             = :V_CV_SEARCH
				                AND A.DEL_YN = 'N'
				                AND B.DEL_YN = 'N'
				              ORDER BY B.YD_STK_BED_SRCH_SEQ
				              ) BB
				            WHERE 1=1             
				              GROUP BY SUBSTR(BB.YD_STK_COL_GP,3,2)
				 */
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeSchSearch";
				
			} else { //공통코드조회
				trtNm = "[" + itmGp + "]코드";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCodeCmCodes";
			}
			
			trtNm += " : ";
			commUtils.printLog(logId, "조회[jspSelect] 결과 건수: " + itmGp , "DB");
			return commDao.select(jrParam, jspeed_query_id, logId, mthdNm, trtNm);
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, trtNm + mthdNm, e));
		}
	}
	
	/***************************************************************************
	 * L2 송신 전문 조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : L2전문조회 - 개발중 LHJ
	 *      
	 *      @param String msgId
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMsgL2(String msgId, JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "L2전문생성[CCoilDAO.getMsgL2] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";

			/* 2열연 COIL야드 L2 송신 *************************************************************************************/			    	
			if("YDY5L001".equals(msgId)) {
			
				trtNm = "2열연 COIL 저장위치 제원";
		    	jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYDY5L001";
		    	
			} else if("YDY5L001_CarInfo".equals(msgId)) {

		    	trtNm = "2열연 코일 저장위치제원(차량정보Backup)";
		    	
		    	/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYDY5L001_CarInfo 
		    	SELECT JMS_TC_CD                                  --JMSTC코드
		    	      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
		    	      ,JMS_TC_CD                                  --전문ID
		    	     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')    --생성일시
		    	     ||'I'                                        --전문구분
		    	     ||'0088'                                     --전문길이
		    	     ||RPAD(' ',29,' ')                           --임시
		    	     ||RPAD(NVL(YD_INFO_SYNC_CD     ,' '), 1,' ') --야드정보동기화코드
		    	     ||RPAD(NVL(YD_GP               ,' '), 1,' ') --야드구분
		    	     ||RPAD(NVL(YD_BAY_GP           ,' '), 1,' ') --야드동구분
		    	     ||RPAD(NVL(YD_EQP_GP           ,' '), 2,' ') --야드설비구분
		    	     ||RPAD(NVL(YD_STK_COL_NO       ,' '), 2,' ') --야드적치열번호
		    	     ||RPAD(NVL(YD_STK_BED_NO       ,' '), 2,' ') --야드적치Bed번호
		    	     ||RPAD(NVL(YD_STK_LYR_NO       ,' '), 1,' ') --야드적치단번호
		    	     ||RPAD(NVL(YD_STK_BED_L_GP     ,' '), 1,' ') --야드적치Bed길이구분
		    	     ||RPAD(NVL(YD_STK_BED_W_GP     ,' '), 1,' ') --야드적치Bed폭구분
		    	     ||RPAD(NVL(YD_STK_BED_DIR_GP   ,' '), 1,' ') --야드적치Bed방향구분
		    	     ||RPAD(NVL(YD_STK_BED_ACT_STAT ,' '), 1,' ') --야드적치Bed활성상태
		    	     ||RPAD(NVL(YD_STK_BED_WHIO_STAT,' '), 1,' ') --야드적치Bed입출고상태
		    	     ||RPAD(NVL(YD_STK_BED_XAXIS    ,' '), 7,' ') --야드적치BedX축(주행)
		    	     ||RPAD(NVL(YD_STK_BED_YAXIS    ,' '), 5,' ') --야드적치BedY축(주행)
		    	--     ||RPAD(NVL(YD_STK_BED_ZAXIS_SYM,' '), 1,' ') --야드적치BedZ축부호
		    	     ||RPAD(NVL(YD_STK_BED_ZAXIS    ,' '), 5,' ') --야드적치BedZ축(주행)
		    	     ||RPAD(NVL(YD_STK_BED_LYR_MAX  ,' '), 3,' ') --야드적치Bed단Max
		    	     ||RPAD(NVL(YD_STK_BED_WT_MAX   ,' '), 7,' ') --야드적치Bed중량Max
		    	     ||RPAD(NVL(YD_STK_BED_H_MAX    ,' '), 5,' ') --야드적치Bed높이Max
		    	     ||RPAD(NVL(YD_STK_BED_L_MAX    ,' '), 5,' ') --야드적치Bed길이Max
		    	     ||RPAD(NVL(YD_STK_BED_W_MAX    ,' '), 5,' ') --야드적치Bed폭Max
		    	     ||RPAD(NVL(YD_CAR_ARRSTRT_STAT ,' '), 1,' ') --야드차량착발상태
		    	     ||RPAD(NVL(YD_CAR_USE_GP       ,' '), 1,' ') --야드차량사용구분
		    	     ||RPAD(NVL(YD_EQP_WRK_STAT     ,' '), 1,' ') --야드설비작업상태
		    	     ||RPAD(NVL(CAR_NO              ,' '),15,' ') --차량번호
		    	     ||RPAD(NVL(TRN_EQP_CD          ,' '), 8,' ') --운송장비코드
		    	     ||RPAD(NVL(CARD_NO             ,' '), 4,' ') --카드번호
		    	     ||RPAD(NVL(YD_CAR_AIM_YD_GP    ,' '), 1,' ') --야드차량목표야드구분
		    	       AS JMS_TC_MESSAGE --JMSTCMessage
		    	  FROM (
		    	            SELECT 'YDY5L001'                                        AS JMS_TC_CD
		    	                  ,:V_YD_INFO_SYNC_CD                                AS YD_INFO_SYNC_CD
		    	                  ,SC.YD_GP                                          AS YD_GP
		    	                  ,SC.YD_BAY_GP                                      AS YD_BAY_GP
		    	                  ,SC.YD_EQP_GP                                      AS YD_EQP_GP
		    	                  ,SC.YD_STK_COL_NO                                  AS YD_STK_COL_NO 
		    	                  ,SC.YD_STK_COL_GP                                  AS YD_STK_COL_GP
		    	                  ,SB.YD_STK_BED_NO                                  AS YD_STK_BED_NO
		    	                  ,SL.YD_STK_LYR_NO                                  AS YD_STK_LYR_NO
		    	                  ,''                                                AS YS_STK_BED_L_GP
		    	                  ,''                                                AS YS_STK_BED_W_GP
		    	                  ,''                                                AS YD_STK_BED_DIR_GP
		    	                  ,CASE WHEN SUBSTR(SC.YD_STK_COL_GP,3,2) = 'PT' THEN
		    	                          (
		    	                             SELECT DECODE(CP.YD_STK_COL_ACT_STAT,'N','C','L') FROM TB_YD_CARPOINT CP WHERE CP.YD_STK_COL_GP = SC.YD_STK_COL_GP
		    	                          )
		    	                        ELSE SB.YD_STK_BED_ACT_STAT
		    	                        END AS YD_STK_BED_ACT_STAT
		    	                  ,''                                                AS YD_STK_BED_WHIO_STAT
		    	                  ,TO_CHAR(NVL(SB.YD_STK_BED_XAXIS,0)  ,'FM0000000') AS YD_STK_BED_XAXIS
		    	                  ,TO_CHAR(NVL(SB.YD_STK_BED_YAXIS,0)  ,'FM00000'  ) AS YD_STK_BED_YAXIS
		    	                  ,CASE WHEN SB.YD_STK_BED_ZAXIS >= 0  THEN '+'
		    	                        ELSE '-' END                                 AS YD_STK_BED_ZAXIS_SYM
		    	                  ,TO_CHAR(NVL(SB.YD_STK_BED_ZAXIS,0)  ,'FM00000'  ) AS YD_STK_BED_ZAXIS
		    	                  ,TO_CHAR(SB.YD_STK_BED_LYR_MAX       ,'FM000'    ) AS YD_STK_BED_LYR_MAX
		    	                  ,TO_CHAR(SB.YD_STK_BED_WT_MAX        ,'FM0000000') AS YD_STK_BED_WT_MAX
		    	                  ,TO_CHAR(SB.YD_STK_BED_H_MAX         ,'FM00000'  ) AS YD_STK_BED_H_MAX
		    	                  ,TO_CHAR(SB.YD_STK_BED_L_MAX         ,'FM00000'  ) AS YD_STK_BED_L_MAX
		    	                  ,TO_CHAR(SB.YD_STK_BED_W_MAX         ,'FM00000' )  AS YD_STK_BED_W_MAX
		    	                  ,NVL(:V_YD_CAR_ARRSTRT_STAT,'')                    AS YD_CAR_ARRSTRT_STAT
		    	                  ,NVL(:V_YD_CAR_USE_GP,'')                          AS YD_CAR_USE_GP
		    	                  ,NVL(:V_YD_EQP_WRK_STAT,'')                        AS YD_EQP_WRK_STAT
		    	                  ,NVL(:V_CAR_NO,'')                                 AS CAR_NO
		    	                  ,NVL(:V_TRN_EQP_CD,'')                             AS TRN_EQP_CD
		    	                  ,NVL(:V_CARD_NO,'')                                AS CARD_NO
		    	                  ,'3'                                               AS YD_CAR_AIM_YD_GP 
		    	              FROM TB_YD_STKBED    SB
		    	                 , TB_YD_STKCOL    SC
		    	                 , (
		    	                    SELECT A.YD_STK_COL_GP
		    	                          ,A.YD_STK_BED_NO
		    	                          ,(SELECT NVL(MAX(B.YD_STK_LYR_NO),'00') 
		    	                              FROM TB_YD_STKLYR B 
		    	                             WHERE B.YD_STK_COL_GP = A.YD_STK_COL_GP 
		    	                               AND B.YD_STK_BED_NO = A.YD_STK_BED_NO 
		    	                               AND B.STL_NO IS NOT NULL 
		    	                               AND B.YD_STK_LYR_NO IN ('C','U')) AS YD_STK_LYR_NO 
		    	                          ,MAX(A.DEL_YN) AS DEL_YN
		    	                      FROM TB_YD_STKLYR A
		    	                     WHERE A.YD_STK_COL_GP LIKE 'J%'
		    	                       AND A.DEL_YN = 'N'
		    	                     GROUP BY A.YD_STK_COL_GP,A.YD_STK_BED_NO              
		    	                   ) SL

		    	             WHERE SC.YD_STK_COL_GP = SB.YD_STK_COL_GP
		    	               AND SB.YD_STK_COL_GP = SL.YD_STK_COL_GP
		    	               AND SB.YD_STK_BED_NO = SL.YD_STK_BED_NO
		    	               AND SB.YD_STK_COL_GP LIKE NVL(:V_YD_STK_COL_GP,'X')||'%'
		    	               AND SB.YD_STK_BED_NO LIKE     :YD_STK_BED_NO||'%'
		    	               AND SC.DEL_YN = 'N'
		    	               AND SB.DEL_YN = 'N'
		    	               AND SL.DEL_YN = 'N'
		    	            ORDER BY YD_BAY_GP, YD_EQP_GP, YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO    
		    	  
		    	       )
		    	*/
		    	
		    	jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYDY5L001_CarInfo";
		    	
			} else if("YDY5L002".equals(msgId)) {

		    	trtNm = "2열연 코일 저장품제원";
		    	
				//야드정보동기화코드 
				String ydInfoSyncCd = commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD"));
				
				if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
					//위치별 >> 1:동,2:SPAN,3:열,4:BED
			    	jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYDY5L002ByLoc_PIDEV";
				} else {
					//재료별 >> 5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제),H:C열연장입,P:1후판장입,Q:2후판장입
			    	jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYDY5L002_PIDEV";
				}
				
			} else if("YDY5L002DnWr".equals(msgId)) {
			    
				trtNm = "2열연 코일 저장품 제원";
		    	
		    	jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYDY5L002DnWr_PIDEV";
		    	
			} else if("YDY5L004".equals(msgId)) {
				
		    	trtNm = "2열연 COIL 작업지시";
		    	
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYDY5L004_PIDEV";
				
			} else if("YDY5L004WC".equals(msgId)) {
				
				trtNm = "2열연 분동COIL 작업지시";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYDY5L004WeightCoil_PIDEV";
				
			} else if("YDY5L006".equals(msgId)) {
				
		    	trtNm = "2열연 COIL 대차출발지시";
		    	                
		    	jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYDY5L006";
		    			    	
			} else if("YDY5L007".equals(msgId)) {
				
		    	trtNm = "작업 현황 응답";
		    	/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYDY5L007 
		    	SELECT 'YDY5L007'                          AS JMS_TC_CD --JMSTC코드
		    	     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
		    	     , 'YDY5L007'                                 --전문ID
		    	     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')    --생성일시
		    	     ||'I'                                        --전문구분
		    	     ||'0046' --전문길이
		    	     ||RPAD(' ',29,' ')                           --임시
		    	     ||RPAD(NVL(:V_YD_EQP_ID       ,' '), 6,' ')  --야드설비ID
		    	     ||RPAD(NVL(YD_SCH_FLAG1       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT1        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG2       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT2        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG3       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT3        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG4       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT4        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG5       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT5        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG6       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT6        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG7       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT7        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG8       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT8        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG9       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT9        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG10      ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT10       ,' '), 3,' ')  --요구스케쥴건수1
		    	     
		    	       AS JMS_TC_MESSAGE    --JMSTCMessage
		    	  FROM (

		    	        SELECT MAX(DECODE(CNT, 1,YD_SCH_FLAG,''))  AS YD_SCH_FLAG1
		    	             , MAX(DECODE(CNT, 1,YD_SCH_CNT,''))   AS YD_SCH_CNT1
		    	             , MAX(DECODE(CNT, 2,YD_SCH_FLAG,''))  AS YD_SCH_FLAG2
		    	             , MAX(DECODE(CNT, 2,YD_SCH_CNT,''))   AS YD_SCH_CNT2
		    	             , MAX(DECODE(CNT, 3,YD_SCH_FLAG,''))  AS YD_SCH_FLAG3
		    	             , MAX(DECODE(CNT, 3,YD_SCH_CNT,''))   AS YD_SCH_CNT3
		    	             , MAX(DECODE(CNT, 4,YD_SCH_FLAG,''))  AS YD_SCH_FLAG4
		    	             , MAX(DECODE(CNT, 4,YD_SCH_CNT,''))   AS YD_SCH_CNT4
		    	             , MAX(DECODE(CNT, 5,YD_SCH_FLAG,''))  AS YD_SCH_FLAG5
		    	             , MAX(DECODE(CNT, 5,YD_SCH_CNT,''))   AS YD_SCH_CNT5
		    	             , MAX(DECODE(CNT, 6,YD_SCH_FLAG,''))  AS YD_SCH_FLAG6
		    	             , MAX(DECODE(CNT, 6,YD_SCH_CNT,''))   AS YD_SCH_CNT6
		    	             , MAX(DECODE(CNT, 7,YD_SCH_FLAG,''))  AS YD_SCH_FLAG7
		    	             , MAX(DECODE(CNT, 7,YD_SCH_CNT,''))   AS YD_SCH_CNT7
		    	             , MAX(DECODE(CNT, 8,YD_SCH_FLAG,''))  AS YD_SCH_FLAG8
		    	             , MAX(DECODE(CNT, 8,YD_SCH_CNT,''))   AS YD_SCH_CNT8
		    	             , MAX(DECODE(CNT, 9,YD_SCH_FLAG,''))  AS YD_SCH_FLAG9
		    	             , MAX(DECODE(CNT, 9,YD_SCH_CNT,''))   AS YD_SCH_CNT9
		    	             , MAX(DECODE(CNT,10,YD_SCH_FLAG,''))  AS YD_SCH_FLAG10
		    	             , MAX(DECODE(CNT,10,YD_SCH_CNT,''))   AS YD_SCH_CNT10
		    	          FROM     
		    	        (     
		    	        SELECT YD_SCH_FLAG 
		    	             , LPAD(TO_CHAR(COUNT(*)),'3',0) AS YD_SCH_CNT
		    	             , ROW_NUMBER() OVER(ORDER BY YD_SCH_FLAG) AS CNT
		    	          FROM (
		    	                SELECT (CASE WHEN A.YD_SCH_CD    LIKE 'J_CV01LH'     THEN 'A'--수입
		    	                             WHEN (B.CD_CONTENTS LIKE '%보급%' OR B.CD_CONTENTS LIKE '%TakeIn%') AND (B.CD_CONTENTS NOT LIKE '%재작%' ) THEN 'B'--보급
		    	                             WHEN A.YD_SCH_CD    LIKE 'J_TC%'        THEN 'C'--대차
		    	                             WHEN B.CD_CONTENTS  LIKE '%반입%'        THEN 'D'--반입
		    	                             --WHEN A.YD_SCH_CD LIKE 'J_PT03UM' THEN 'E'--HYSCO 출하
		    	                             WHEN A.YD_SCH_CD    LIKE 'J_PT0_UH'     THEN 'F'--이송
		    	                             WHEN B.CD_CONTENTS  LIKE '%입측TakeOut%' THEN 'G'--입측추출
		    	                             WHEN B.CD_CONTENTS  LIKE '%출측TakeOut%' OR (B.CD_CONTENTS LIKE '%추출%' AND B.CD_CONTENTS NOT LIKE '%스크%') THEN 'H'--출측추출
		    	                             WHEN B.CD_CONTENTS  LIKE '%차공정%'      THEN 'I'--차공정
		    	                             WHEN B.CD_CONTENTS  LIKE '%차량이적%'     THEN 'J'--차량이적
		    	                             WHEN B.CD_CONTENTS  LIKE '%스크%'        THEN 'L' --출측SCRAP추출
		    	                             WHEN B.CD_CONTENTS  LIKE '%스크%'        THEN 'K' --입측SCRAP추출
		    	                             WHEN B.CD_CONTENTS  LIKE '%재작%'        THEN 'M'--재작보급
		    	                         END) AS YD_SCH_FLAG
		    	                     , B.YD_WRK_CRN
		    	                     , B.YD_ALT_CRN
		    	                     , A.YD_SCH_CD
		    	                  FROM TB_YD_CRNSCH A
		    	                     , TB_YD_SCHRULE B
		    	                     , TB_YD_EQP C
		    	                 WHERE A.YD_SCH_CD  = B.YD_SCH_CD
		    	                   AND B.YD_WRK_CRN = C.YD_EQP_ID
		    	                   AND A.DEL_YN = 'N'
		    	                   AND A.YD_GP  = 'J'
		    	                   AND SUBSTR(A.YD_SCH_CD,8,1) = 'H'
		    	                   AND (CASE C.YD_EQP_STAT WHEN 'B' THEN B.YD_ALT_CRN ELSE A.YD_EQP_ID END) = :V_YD_EQP_ID
		    	                ) A 
		    	         WHERE 1 = 1 --YD_SCH_FLAG IS NOT NULL
		    	         GROUP BY YD_SCH_FLAG
		    	        ) 
		    	)
		    	*/
		    	jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYDY5L007";
		    			    	
			} else if("YDY5L008BackUp".equals(msgId)) {
				
		    	trtNm = "2열연 COIL 차량예정정보 Backup";
		    	jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYDY5L008BackUp";

			} else if ("YDH2".equals(msgId)) {
				trtNm= "열연정정";
				/* 
				SELECT JMS_TC_CD                                                 --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , JMS_TC_CD                                                 --전문ID
				       || TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')   --발생일자
				       || RPAD(NVL(MSG_GP          , ' '), 1 , ' ') --전문구분
				       || RPAD(NVL(MSG_LEN         , ' '), 4 , ' ') --전문길이
				       || RPAD(NVL(TEMP            , ' '), 29, ' ') --임시
				       || RPAD(NVL(YD_EQP_ID       , ' '), 6 , ' ') --설비코드
				       || RPAD(NVL(YD_STK_BED_NO   , ' '), 2 , ' ') --이동구분
				       || RPAD(NVL(STL_NO          , ' '),11 , ' ') --이동구분
				       AS JMS_TC_MESSAGE --JMSTCMessage
				  FROM (
				        SELECT :V_JMS_TC_CD         AS JMS_TC_CD --전문ID
				             , 'I'                  AS MSG_GP    --전문구분
				             , '0019'               AS MSG_LEN
				             , ''                   AS TEMP
				--             , P_YD_EQP_ID          AS YD_EQP_ID
				             ,(SELECT EQP_GP
				                 FROM TB_YD_EQPTRACKING
				                WHERE DEL_YN = 'N'
				                  AND YD_STK_COL_GP = P_YD_EQP_ID
				                  AND YD_STK_BED_NO = P_YD_STK_BED_NO
				              ) AS YD_EQP_ID
				             , P_YD_STK_BED_NO      AS YD_STK_BED_NO
				             , P_STL_NO             AS STL_NO
				          FROM (
				                SELECT :V_YD_EQP_ID         AS P_YD_EQP_ID
				                     , :V_YD_STK_BED_NO     AS P_YD_STK_BED_NO
				                     , :V_STL_NO            AS P_STL_NO
				                  FROM DUAL
				               )
				       )
				 WHERE 1 = 1
				 */
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYDH2";
			} else if ("YDH1L001".equals(msgId)) {
				trtNm= "열연압연";
				/*
				SELECT JMS_TC_CD                                     --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				      ,JMS_TC_CD                                     --전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')       --생성일시
				     ||RPAD(NVL(MSG_GP                 ,'I'), 1,' ') --전문구분
				     ||'0019'                                        --전문길이
				     ||RPAD(' ',29,' ')                              --임시
				     
				     ||RPAD(NVL(YD_EQP_ID        ,' '), 6,' ') --야드설비ID
				     ||RPAD(NVL(STL_NO           ,' '),11,' ') --재료번호
				     ||RPAD(NVL(YD_STK_BED_NO    ,' '), 2,' ') --야드적치bed번호
				    
				       AS JMS_TC_MESSAGE --JMSTCMESSAGE
				  FROM (
				
				        SELECT 'YDH1L001'       AS JMS_TC_CD                --전문ID
				             , :V_MSG_GP        AS MSG_GP                   --전문구분
				             , :V_YD_EQP_ID     AS YD_EQP_ID
				             , :V_STL_NO        AS STL_NO
				             , :V_YD_STK_BED_NO AS YD_STK_BED_NO
				          FROM DUAL
				       )

				 */
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYDH1L001";
			}
			
			JDTORecordSet jsRst = null;
			
			if(!"".equals(jspeed_query_id)) {
				trtNm = trtNm + "(" + msgId + ") : ";
				
				jsRst = commDao.select(rcvMsg, jspeed_query_id);
					
				commUtils.printLog(logId, trtNm + jsRst.size(), "DB");
			}

			return jsRst;
			
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, trtNm + mthdNm, e));
		}
	}
	
	/***************************************************************************
	 * L3 송신 전문 조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : L3전문생성 - 개발중 LHJ
	 *      
	 *      @param String msgId
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMsgL3(String msgId, JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "내부전문생성[CCoilDAO.getMsgL3] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			
			rcvMsg.setField("JMS_TC_CD"       , msgId);
			
/* 출하관리  */	
			if ("YDDMR001".equals(msgId) ) {
				/* 
				SELECT 'YDDMR001'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDDMR001'                          AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				     , MT.STL_NO                           AS GOODS_NO
				     , TO_CHAR(SYSDATE,'YYYYMMDD') AS RECEIPT_DATE
				     , TO_CHAR(SYSDATE,'HH24MISS') AS RECEIPT_TIME
				     , SC.YD_GP
				     , (SELECT YD_STK_COL_GP||YD_STK_BED_NO||YD_STK_LYR_NO
				          FROM TB_YD_STKLYR 
				         WHERE STL_NO = MT.STL_NO AND ROWNUM = 1) AS STORE_LOC
				     , PT.PRD_ITM_CD AS PROD_ITEM_CODE
				     , PT.CURR_PROG_CD 
				  FROM TB_YD_WRKBOOK    SC
				     , TB_YD_WRKBOOKMTL MT
				     , TB_PT_COILCOMM   PT
				 WHERE SC.YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND SC.YD_WBOOK_ID = MT.YD_WBOOK_ID
				   AND MT.STL_NO = PT.COIL_NO
				   AND PT.CURR_PROG_CD IN ('H','2')
				 */       				
				trtNm = "입고실적";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDDMR001";
			} else if ("YDDMR004".equals(msgId)) {
				 /*  
					WITH PARA_TABLE AS
					(
					   SELECT 'YDDMR004'   AS JMS_TC_CD 
					        , :V_STL_NO    AS STL_NO
					     FROM DUAL
					)
					SELECT P.JMS_TC_CD                                              
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT
					     , P.JMS_TC_CD                         AS TC_CODE                                             
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT
					     , A.STL_NO                            AS GOODS_NO
					     , B.YD_STR_LOC                        AS BEFO_STORE_LOC 
					     , C.YD_STK_COL_GP||C.YD_STK_BED_NO||SUBSTR(C.YD_STK_LYR_NO, 2, 3) AS TO_STORE_LOC    --저장위치
					     , TO_CHAR(SYSDATE,'YYYYMMDD')         AS MOVENSTACK_DATE
					     , TO_CHAR(SYSDATE,'HH24MISS')         AS MOVENSTACK_TIME
					  FROM TB_YD_STOCK    A
					     , TB_PT_COILCOMM B
					     , TB_YD_STKLYR   C
					     , PARA_TABLE P
					 WHERE A.STL_NO  = B.COIL_NO
					   AND A.STL_NO  = P.STL_NO    
					   AND A.STL_NO  = C.STL_NO 
					   AND C.YD_STK_LYR_MTL_STAT IN('C','U') --적치중, 권상대기
				   */ 
					trtNm = "코일제품이적작업실적";
					jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDDMR004_PIDEV";				
			} else if ("YDDMR007".equals(msgId) ) {
					/* 
					SELECT 'YDDMR007'                          AS JMS_TC_CD          --JMSTC코드
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
					     , 'YDDMR007'                          AS TC_CODE            --IF구분코드
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT     --TC생성일시
					     , TS.CAR_NO                                                 --차량번호
					     , SUBSTR(TS.YD_CARLD_STOP_LOC,1,1)    AS YD_GP              --야드구분
					     , COUNT(*) OVER ()                    AS BD_EA           --제품개수
					     , TM.STL_NO                           AS BD_NO           --제품번호
					     , TS.TRANS_ORD_DATE                   AS TRANS_WORD_DATE                                        --운송지시일자
					     , TS.TRANS_ORD_SEQNO                  AS TRANS_WORD_SEQNO                               --운송지시순번
					     , '1'                                 AS SPST_FRTOMOVE_GP
					  FROM (
					        SELECT *
					          FROM TB_YD_CARSCH
					         WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					       ) TS
					     , (
					        SELECT STL_NO
					          FROM TB_YD_CRNWRKMTL
					         WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					       ) TM
					 WHERE 1 = 1 
					 */       				
					trtNm = "출하차량상차개시";
					jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDDMR007";
			} else if ("YDDMR011".equals(msgId) ) {
					/* 
					SELECT 'YDDMR011'                          AS JMS_TC_CD          --JMSTC코드
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
					     , 'YDDMR011'                          AS TC_CODE            --IF구분코드
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT     --TC생성일시
					     , TS.CARD_NO                                                 --카드번호   
					     , TS.CAR_NO                                                 --차량번호
					     , SUBSTR(TS.YD_CARLD_STOP_LOC,1,1)    AS YD_GP              --야드구분
					     , :V_GOODS_EA                         AS GOODS_EA           --제품개수완료시(*)
					     , :V_STOCK_ID                         AS GOODS_NO           --제품번호
					     , TS.TRANS_ORD_DATE                   AS TRANS_WORD_DATE                                        --운송지시일자
					     , TS.TRANS_ORD_SEQNO                  AS TRANS_WORD_SEQNO                               --운송지시순번
					  FROM USRYDA.TB_YD_CARSCH TS
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					*/ 
					
					trtNm = "코일일품출하상차실적";
					jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDDMR011";	
			} else if ("YDDMR015".equals(msgId) ) {
				/* 
				SELECT 'YDDMR015'                              AS JMS_TC_CD          --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')     AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , 'YDDMR015'                              AS TC_CODE            --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')     AS TC_CREATE_DDTT     --TC생성일시
				     , TS.CARD_NO 
				     , TS.CAR_NO                                                     --차량번호
				     , MIN(SUBSTR(TS.YD_CARLD_STOP_LOC,1,1))   AS YD_GP  
				     , TO_CHAR(TS.YD_CARLD_CMPL_DT,'YYYYMMDD') AS CARLOAD_END_DATE   --상차완료일자
				     , TO_CHAR(TS.YD_CARLD_CMPL_DT,'HH24MISS') AS CARLOAD_END_TIME   --상차완료시각
				     , MIN(TS.TRANS_ORD_DATE )                 AS TRANS_WORD_DATE     --운송지시일자
				     , MIN(TS.TRANS_ORD_SEQNO)                 AS TRANS_WORD_SEQNO    --운송지시순번
				  FROM TB_YD_CARSCH     TS
				 WHERE TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 GROUP BY TS.CARD_NO
				        , TS.CAR_NO
				        , TS.YD_CARLD_CMPL_DT
				 */       				
				trtNm = "출하차량상차완료";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDDMR015";
			} else if ("YDDMR019".equals(msgId) ) {
				/* 
				SELECT  'YDDMR019'                          AS JMS_TC_CD            --JMSTC코드
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				      , 'YDDMR019'                          AS TC_CODE              --IF구분코드
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				      , TM.UPCARUNLOAD_GP
				      , TM.CARD_NO
				      , TM.CAR_NO 
				      , TM.YD_GP
				      , TM.CARLOAD_START_DATE
				      , TM.CARLOAD_START_TIME
				      , MAX(DECODE(NO,1 ,TM.STL_NO,''))          AS GOODS_NO1 
					  , MAX(DECODE(NO,1 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE1 
					  , MAX(DECODE(NO,1 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO1   
				      , MAX(DECODE(NO,2 ,TM.STL_NO,''))          AS GOODS_NO2 
					  , MAX(DECODE(NO,2 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE2 
					  , MAX(DECODE(NO,2 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO2   
				      , MAX(DECODE(NO,3 ,TM.STL_NO,''))          AS GOODS_NO3 
					  , MAX(DECODE(NO,3 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE3 
					  , MAX(DECODE(NO,3 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO3   	  	  
				      , MAX(DECODE(NO,4 ,TM.STL_NO,''))          AS GOODS_NO4 
					  , MAX(DECODE(NO,4 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE4 
					  , MAX(DECODE(NO,4 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO4   
				      , MAX(DECODE(NO,5 ,TM.STL_NO,''))          AS GOODS_NO5 
					  , MAX(DECODE(NO,5 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE5 
					  , MAX(DECODE(NO,5 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO5   
				      , MAX(DECODE(NO,6 ,TM.STL_NO,''))          AS GOODS_NO6 
					  , MAX(DECODE(NO,6 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE6 
					  , MAX(DECODE(NO,6 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO6  
				      , MAX(DECODE(NO,7 ,TM.STL_NO,''))          AS GOODS_NO7 
					  , MAX(DECODE(NO,7 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE7 
					  , MAX(DECODE(NO,7 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO7   
				      , MAX(DECODE(NO,8 ,TM.STL_NO,''))          AS GOODS_NO8 
					  , MAX(DECODE(NO,8 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE8 
					  , MAX(DECODE(NO,8 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO8   
				      , MAX(DECODE(NO,9 ,TM.STL_NO,''))          AS GOODS_NO9 
					  , MAX(DECODE(NO,9 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE9 
					  , MAX(DECODE(NO,9 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO9   	  	  
				      , MAX(DECODE(NO,10,TM.STL_NO,''))          AS GOODS_NO10
					  , MAX(DECODE(NO,10,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE10
					  , MAX(DECODE(NO,10,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO10  
				      , MAX(DECODE(NO,11,TM.STL_NO,''))          AS GOODS_NO11
					  , MAX(DECODE(NO,11,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE11
					  , MAX(DECODE(NO,11,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO11  
				      , MAX(DECODE(NO,12,TM.STL_NO,''))          AS GOODS_NO12
					  , MAX(DECODE(NO,12,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE12
					  , MAX(DECODE(NO,12,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO12
				      , MAX(DECODE(NO,13,TM.STL_NO,''))          AS GOODS_NO13
					  , MAX(DECODE(NO,13,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE13
					  , MAX(DECODE(NO,13,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO13  
				      , MAX(DECODE(NO,14,TM.STL_NO,''))          AS GOODS_NO14
					  , MAX(DECODE(NO,14,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE14
					  , MAX(DECODE(NO,14,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO14  
				      , MAX(DECODE(NO,15,TM.STL_NO,''))          AS GOODS_NO15
					  , MAX(DECODE(NO,15,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE15
					  , MAX(DECODE(NO,15,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO15  	  	  
				
				  FROM         
				       (
				        SELECT ROWNUM NO 
				             , CASE WHEN TA.YD_EQP_WRK_STAT = 'L' THEN 'D'
				                    ELSE 'U' END  AS UPCARUNLOAD_GP
				             , TB.CARD_NO		AS CARD_NO                       
				             , (CASE YD_CAR_USE_GP WHEN 'G' THEN TB.CAR_NO ELSE TA.TRN_EQP_CD END) AS CAR_NO                 
				             , 'J' AS YD_GP     
				             , SUBSTR(TA.YD_CARLD_ST_DT,1,8)  AS CARLOAD_START_DATE
				             , SUBSTR(TA.YD_CARLD_ST_DT,9,6)  AS CARLOAD_START_TIME      
				             , TA.STL_NO AS STL_NO
				             , NVL(DM.FRTOMOVE_WORD_DATE , NVL(TB.TRANS_ORD_DATE , NVL(TD.TRANS_ORD_DATE , SUBSTR(TC.TRANS_WORD_NO,1,8)))) AS TRANS_ORD_DATE
				             , NVL(DM.FRTOMOVE_WORD_SEQNO, NVL(TB.TRANS_ORD_SEQNO, NVL(TD.TRANS_ORD_DATE , SUBSTR(TC.TRANS_WORD_NO,9))))   AS TRANS_ORD_SEQNO 
				          FROM (SELECT A.YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
				                     , A.TRN_EQP_CD     AS TRN_EQP_CD
				                     , (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END) AS YD_EQP_WRK_STAT              
				                     , A.CAR_NO         AS CAR_NO
				                     , TO_CHAR(A.YD_CARLD_ST_DT , 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT                              
				                     , A.CARD_NO        AS CARD_NO                 
				                     , C.STL_NO         AS STL_NO
				                     , A.YD_CAR_USE_GP
				                  FROM TB_YD_CARSCH  A
				                     , TB_YD_PREPSCH B
				                     , TB_YD_PREPMTL C
				                 WHERE A.YD_CARLD_WRK_BOOK_ID = B.YD_WBOOK_ID
				                   AND B.YD_PREP_SCH_ID = C.YD_PREP_SCH_ID
				                   AND (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END) = 'U' --상차
				                 UNION ALL
				                SELECT A.YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
				                     , A.TRN_EQP_CD     AS TRN_EQP_CD
				                     , (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END) AS YD_EQP_WRK_STAT              
				                     , A.CAR_NO         AS CAR_NO
				                     , TO_CHAR(A.YD_CARLD_ST_DT , 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT                              
				                     , A.CARD_NO        AS CARD_NO                 
				                     , C.STL_NO         AS STL_NO
				                     , A.YD_CAR_USE_GP
				                  FROM TB_YD_CARSCH     A
				                     , TB_YD_CARFTMVMTL C
				                 WHERE A.YD_CAR_SCH_ID = C.YD_CAR_SCH_ID
				                   AND (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END) = 'L' --하차
				                   AND A.DEL_YN='N'
				               ) TA
				             , TB_YD_STOCK TB
				             , TB_YM_STOCK TC
				             , TB_YF_STOCK TD
				             , TB_DM_GOODSFRTOMOVEWORD  @DL_SMDB DM
				         WHERE TA.STL_NO = TB.STL_NO
				           AND TA.STL_NO = TC.STOCK_ID(+)
				           AND TA.STL_NO = TD.STL_NO(+)
				           AND TA.STL_NO = DM.GOODS_NO(+)
				           AND DM.FRTOMOVE_WORD_DATE(+) >= TO_CHAR(SYSDATE-1,'YYYYMMDD')
				           AND TA.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				           AND DM.DEL_YN = 'N'
				       ) TM
				 WHERE 1 = 1             
				 GROUP BY   UPCARUNLOAD_GP,CARD_NO,CAR_NO ,YD_GP,CARLOAD_START_DATE, CARLOAD_START_TIME             
				*/
				trtNm = "코일제품고간이송상하차개시";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDDMR019";
			} else if ("YDDMR020".equals(msgId) ) {
				/*  
				SELECT 'YDDMR020'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDDMR020'                          AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				     , CASE WHEN YD_CAR_PROG_STAT IN ('2','3','4','5')       THEN 'U'   
				            ELSE 'D' END                   AS UPCARUNLOAD_GP
				     , CARD_NO                             AS CARD_NO         
				     , CAR_NO                              AS CAR_NO
				     , SUBSTR(YD_CARLD_STOP_LOC,1,1)       AS YD_GP
				     , CASE WHEN  YD_CAR_PROG_STAT IN ('2','3','4','5') 
				            THEN SUBSTR(TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS'),1,8) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS'),1,8) END  AS CARLOAD_START_DATE
				     , CASE WHEN  YD_CAR_PROG_STAT IN ('2','3','4','5')
				            THEN SUBSTR(TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS'),9,6) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS'),9,6) END  AS CARLOAD_START_TIME 
				     , TRANS_ORD_DATE                      AS TRANS_WORD_DATE
				     , TRANS_ORD_SEQNO                     AS TRANS_WORD_SEQNO
				  FROM TB_YD_CARSCH C
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/ 
				trtNm = "임가공이송상하차개시";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDDMR020"; 
				
			} else if ("YDDMR021".equals(msgId) ) {
				trtNm = "코일제품고간이송상하차완료";
				/*
				SELECT 'YDDMR021'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDDMR021'                          AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				     , UPCARUNLOAD_GP
				     , CARD_NO
				     , CAR_NO
				     , YD_PNT_CD                            AS ARR_YD_PNT_CD
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  AS ISSUE_DDTT
				     , TO_CHAR(COUNT(*) OVER ()) AS TREAT_EA
				     , MAX(DECODE(NO,1,DD.STL_NO,''))          AS GOODS_NO1
				     , MAX(DECODE(NO,1,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE1
				     , MAX(DECODE(NO,1,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO1
				     , MAX(DECODE(NO,1,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD1    
				     , MAX(DECODE(NO,1,DD.YD_GP,''))           AS YD_GP1    
				     , MAX(DECODE(NO,1,DD.BAY_GP,''))          AS BAY_GP1      
				     , MAX(DECODE(NO,1,DD.YD_STK_BED_NO,''))   AS SPAN1
				     , MAX(DECODE(NO,1,DD.YD_STK_LYR_NO,''))   AS STK_LYR1    
				     , MAX(DECODE(NO,2,DD.STL_NO,''))          AS GOODS_NO2
				     , MAX(DECODE(NO,2,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE2
				     , MAX(DECODE(NO,2,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO2
				     , MAX(DECODE(NO,2,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD2    
				     , MAX(DECODE(NO,2,DD.YD_GP,''))           AS YD_GP2    
				     , MAX(DECODE(NO,2,DD.BAY_GP,''))          AS BAY_GP2      
				     , MAX(DECODE(NO,2,DD.YD_STK_BED_NO,''))   AS SPAN2
				     , MAX(DECODE(NO,2,DD.YD_STK_LYR_NO,''))   AS STK_LYR2 
				     , MAX(DECODE(NO,3,DD.STL_NO,''))          AS GOODS_NO3
				     , MAX(DECODE(NO,3,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE3
				     , MAX(DECODE(NO,3,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO3
				     , MAX(DECODE(NO,3,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD3    
				     , MAX(DECODE(NO,3,DD.YD_GP,''))           AS YD_GP3    
				     , MAX(DECODE(NO,3,DD.BAY_GP,''))          AS BAY_GP3      
				     , MAX(DECODE(NO,3,DD.YD_STK_BED_NO,''))   AS SPAN3
				     , MAX(DECODE(NO,3,DD.YD_STK_LYR_NO,''))   AS STK_LYR3
				     , MAX(DECODE(NO,4,DD.STL_NO,''))          AS GOODS_NO4
				     , MAX(DECODE(NO,4,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE4
				     , MAX(DECODE(NO,4,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO4
				     , MAX(DECODE(NO,4,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD4    
				     , MAX(DECODE(NO,4,DD.YD_GP,''))           AS YD_GP4    
				     , MAX(DECODE(NO,4,DD.BAY_GP,''))          AS BAY_GP4      
				     , MAX(DECODE(NO,4,DD.YD_STK_BED_NO,''))   AS SPAN4
				     , MAX(DECODE(NO,4,DD.YD_STK_LYR_NO,''))   AS STK_LYR4
				     , MAX(DECODE(NO,5,DD.STL_NO,''))          AS GOODS_NO5
				     , MAX(DECODE(NO,5,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE5
				     , MAX(DECODE(NO,5,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO5
				     , MAX(DECODE(NO,5,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD5    
				     , MAX(DECODE(NO,5,DD.YD_GP,''))           AS YD_GP5    
				     , MAX(DECODE(NO,5,DD.BAY_GP,''))          AS BAY_GP5      
				     , MAX(DECODE(NO,5,DD.YD_STK_BED_NO,''))   AS SPAN5
				     , MAX(DECODE(NO,5,DD.YD_STK_LYR_NO,''))   AS STK_LYR5
				     , MAX(DECODE(NO,6,DD.STL_NO,''))          AS GOODS_NO6
				     , MAX(DECODE(NO,6,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE6
				     , MAX(DECODE(NO,6,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO6
				     , MAX(DECODE(NO,6,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD6    
				     , MAX(DECODE(NO,6,DD.YD_GP,''))           AS YD_GP6    
				     , MAX(DECODE(NO,6,DD.BAY_GP,''))          AS BAY_GP6      
				     , MAX(DECODE(NO,6,DD.YD_STK_BED_NO,''))   AS SPAN6
				     , MAX(DECODE(NO,6,DD.YD_STK_LYR_NO,''))   AS STK_LYR6   
				     , MAX(DECODE(NO,7,DD.STL_NO,''))          AS GOODS_NO7
				     , MAX(DECODE(NO,7,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE7
				     , MAX(DECODE(NO,7,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO7
				     , MAX(DECODE(NO,7,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD7    
				     , MAX(DECODE(NO,7,DD.YD_GP,''))           AS YD_GP7    
				     , MAX(DECODE(NO,7,DD.BAY_GP,''))          AS BAY_GP7      
				     , MAX(DECODE(NO,7,DD.YD_STK_BED_NO,''))   AS SPAN7
				     , MAX(DECODE(NO,7,DD.YD_STK_LYR_NO,''))   AS STK_LYR7   
				     , MAX(DECODE(NO,8,DD.STL_NO,''))          AS GOODS_NO8
				     , MAX(DECODE(NO,8,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE8
				     , MAX(DECODE(NO,8,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO8
				     , MAX(DECODE(NO,8,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD8    
				     , MAX(DECODE(NO,8,DD.YD_GP,''))           AS YD_GP8    
				     , MAX(DECODE(NO,8,DD.BAY_GP,''))          AS BAY_GP8      
				     , MAX(DECODE(NO,8,DD.YD_STK_BED_NO,''))   AS SPAN8
				     , MAX(DECODE(NO,8,DD.YD_STK_LYR_NO,''))   AS STK_LYR8   
				     , MAX(DECODE(NO,9,DD.STL_NO,''))          AS GOODS_NO9
				     , MAX(DECODE(NO,9,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE9
				     , MAX(DECODE(NO,9,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO9
				     , MAX(DECODE(NO,9,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD9    
				     , MAX(DECODE(NO,9,DD.YD_GP,''))           AS YD_GP9    
				     , MAX(DECODE(NO,9,DD.BAY_GP,''))          AS BAY_GP9      
				     , MAX(DECODE(NO,9,DD.YD_STK_BED_NO,''))   AS SPAN9
				     , MAX(DECODE(NO,9,DD.YD_STK_LYR_NO,''))   AS STK_LYR9   
				     , MAX(DECODE(NO,10,DD.STL_NO,''))          AS GOODS_NO10
				     , MAX(DECODE(NO,10,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE10
				     , MAX(DECODE(NO,10,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO10
				     , MAX(DECODE(NO,10,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD10    
				     , MAX(DECODE(NO,10,DD.YD_GP,''))           AS YD_GP10    
				     , MAX(DECODE(NO,10,DD.BAY_GP,''))          AS BAY_GP10      
				     , MAX(DECODE(NO,10,DD.YD_STK_BED_NO,''))   AS SPAN10
				     , MAX(DECODE(NO,10,DD.YD_STK_LYR_NO,''))   AS STK_LYR10   
				     , MAX(DECODE(NO,11,DD.STL_NO,''))          AS GOODS_NO11
				     , MAX(DECODE(NO,11,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE11
				     , MAX(DECODE(NO,11,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO11
				     , MAX(DECODE(NO,11,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD11    
				     , MAX(DECODE(NO,11,DD.YD_GP,''))           AS YD_GP11    
				     , MAX(DECODE(NO,11,DD.BAY_GP,''))          AS BAY_GP11      
				     , MAX(DECODE(NO,11,DD.YD_STK_BED_NO,''))   AS SPAN11
				     , MAX(DECODE(NO,11,DD.YD_STK_LYR_NO,''))   AS STK_LYR11   
				     , MAX(DECODE(NO,12,DD.STL_NO,''))          AS GOODS_NO12
				     , MAX(DECODE(NO,12,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE12
				     , MAX(DECODE(NO,12,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO12
				     , MAX(DECODE(NO,12,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD12    
				     , MAX(DECODE(NO,12,DD.YD_GP,''))           AS YD_GP12    
				     , MAX(DECODE(NO,12,DD.BAY_GP,''))          AS BAY_GP12      
				     , MAX(DECODE(NO,12,DD.YD_STK_BED_NO,''))   AS SPAN12
				     , MAX(DECODE(NO,12,DD.YD_STK_LYR_NO,''))   AS STK_LYR12   
				     , MAX(DECODE(NO,13,DD.STL_NO,''))          AS GOODS_NO13
				     , MAX(DECODE(NO,13,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE13
				     , MAX(DECODE(NO,13,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO13
				     , MAX(DECODE(NO,13,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD13    
				     , MAX(DECODE(NO,13,DD.YD_GP,''))           AS YD_GP13    
				     , MAX(DECODE(NO,13,DD.BAY_GP,''))          AS BAY_GP13      
				     , MAX(DECODE(NO,13,DD.YD_STK_BED_NO,''))   AS SPAN13
				     , MAX(DECODE(NO,13,DD.YD_STK_LYR_NO,''))   AS STK_LYR13   
				     , MAX(DECODE(NO,14,DD.STL_NO,''))          AS GOODS_NO14
				     , MAX(DECODE(NO,14,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE14
				     , MAX(DECODE(NO,14,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO14
				     , MAX(DECODE(NO,14,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD14    
				     , MAX(DECODE(NO,14,DD.YD_GP,''))           AS YD_GP14    
				     , MAX(DECODE(NO,14,DD.BAY_GP,''))          AS BAY_GP14      
				     , MAX(DECODE(NO,14,DD.YD_STK_BED_NO,''))   AS SPAN14
				     , MAX(DECODE(NO,14,DD.YD_STK_LYR_NO,''))   AS STK_LYR14   
				     , MAX(DECODE(NO,15,DD.STL_NO,''))          AS GOODS_NO15
				     , MAX(DECODE(NO,15,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE15
				     , MAX(DECODE(NO,15,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO15
				     , MAX(DECODE(NO,15,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD15    
				     , MAX(DECODE(NO,15,DD.YD_GP,''))           AS YD_GP15    
				     , MAX(DECODE(NO,15,DD.BAY_GP,''))          AS BAY_GP15      
				     , MAX(DECODE(NO,15,DD.YD_STK_BED_NO,''))   AS SPAN15
				     , MAX(DECODE(NO,15,DD.YD_STK_LYR_NO,''))   AS STK_LYR15
				     
				  FROM
				       ( SELECT ROWNUM NO
				              , CASE WHEN  TA.YD_EQP_WRK_STAT ='L' THEN 'D'
				                     ELSE  'U' END              AS UPCARUNLOAD_GP
				              , TX.CAR_CARD_NO    	            AS CARD_NO       
				              , (CASE YD_CAR_USE_GP WHEN 'G' THEN TX.CAR_NO2 ELSE TA.TRN_EQP_CD END)		
				                                                AS CAR_NO 
				              , TA.STL_NO                       AS STL_NO
				              , TA.YD_PNT_CD
				              , 'J'                             AS YD_GP
				              , NVL(DM.FRTOMOVE_WORD_DATE , NVL(TY.TRANS_ORD_DATE , NVL(TD.TRANS_ORD_DATE , SUBSTR(TX.TRANS_WORD_NO,1,8)))) AS TRANS_ORD_DATE
				              , NVL(DM.FRTOMOVE_WORD_SEQNO, NVL(TY.TRANS_ORD_SEQNO, NVL(TD.TRANS_ORD_DATE , SUBSTR(TX.TRANS_WORD_NO,9))))   AS TRANS_ORD_SEQNO              
				              , (SELECT YD_STK_COL_GP ||YD_STK_BED_NO ||YD_STK_LYR_NO 
				                   FROM TB_YD_STKLYR
				                  WHERE STL_NO = TA.STL_NO
				                    AND ROWNUM <= 1 )            AS STORE_LOC_CD
				              , SUBSTR(TA.YD_CARLD_STOP_LOC,2,1) AS BAY_GP   
				              , (SELECT SUBSTR(YD_STK_COL_GP, 3, 2) 
				                   FROM TB_YD_STKLYR
				                  WHERE STL_NO =TA.STL_NO
				                  AND ROWNUM <= 1 )              AS YD_STK_BED_NO  
				              , TA.YD_STK_LYR_NO	             AS YD_STK_LYR_NO 
				          FROM (SELECT A.YD_CAR_SCH_ID           AS YD_CAR_SCH_ID
				                     , A.TRN_EQP_CD              AS TRN_EQP_CD
				                     , (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END) AS YD_EQP_WRK_STAT
				                     , A.YD_CARLD_STOP_LOC       AS YD_CARLD_STOP_LOC 
				                     , A.CAR_NO AS CAR_NO
				                     , TO_CHAR(A.YD_CARLD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT                              
				                     , TO_CHAR(A.YD_CARUD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT
				                     , A.CARD_NO                 AS CARD_NO                 
				                     , C.STL_NO                  AS STL_NO
				                     , YD_CAR_USE_GP
				                     , C.YD_STK_LYR_NO           AS YD_STK_LYR_NO
				                     , NVL(A.YD_PNT_CD1,A.YD_PNT_CD2) AS YD_PNT_CD                              
				                  FROM TB_YD_CARSCH  A
				                     , TB_YD_PREPSCH B
				                     , TB_YD_PREPMTL C
				                 WHERE A.YD_CARLD_WRK_BOOK_ID = B.YD_WBOOK_ID
				                   AND B.YD_PREP_SCH_ID       = C.YD_PREP_SCH_ID
				                   AND (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END)='U' --상차
				                 UNION ALL
				                SELECT A.YD_CAR_SCH_ID           AS YD_CAR_SCH_ID
				                     , A.TRN_EQP_CD              AS TRN_EQP_CD
				                     , (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END) AS YD_EQP_WRK_STAT      
				                     , A.YD_CARLD_STOP_LOC       AS YD_CARLD_STOP_LOC    
				                     , A.CAR_NO                  AS CAR_NO
				                     , TO_CHAR(A.YD_CARLD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT                              
				                     , TO_CHAR(A.YD_CARUD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT
				                     , A.CARD_NO                 AS CARD_NO                 
				                     , C.STL_NO                  AS STL_NO
				                     , YD_CAR_USE_GP
				                     , C.YD_STK_LYR_NO           AS YD_STK_LYR_NO        
				                     , NVL(A.YD_PNT_CD1,A.YD_PNT_CD2) AS YD_PNT_CD                          
				                  FROM TB_YD_CARSCH     A
				                     , TB_YD_CARFTMVMTL C
				                 WHERE A.YD_CAR_SCH_ID = C.YD_CAR_SCH_ID
				                   AND (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END)='L' --하차
				                   AND A.DEL_YN = 'N'
				               ) TA
				             , TB_YM_STOCK  TX           
				             , TB_YD_STOCK  TY
				             , TB_YF_STOCK  TD
				             , TB_DM_GOODSFRTOMOVEWORD  @DL_SMDB DM
				         WHERE TA.STL_NO = TY.STL_NO 
				           AND TA.STL_NO = TX.STOCK_ID(+)
				           AND TA.STL_NO = TD.STL_NO(+)
				           AND TA.STL_NO = DM.GOODS_NO(+)
				           AND DM.FRTOMOVE_WORD_DATE(+) >= TO_CHAR(SYSDATE-1,'YYYYMMDD')
				           AND YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				           AND DM.DEL_YN='N'
				     ) DD
				 GROUP BY UPCARUNLOAD_GP
				        , CARD_NO
				        , CAR_NO
				        , YD_PNT_CD   
				 */
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDDMR021_PIDEV";
				
			} else if ("YDDMR022".equals(msgId) ) {
				/*  
				SELECT 'YDDMR022'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDDMR022'                          AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
					 , CASE WHEN YD_CAR_PROG_STAT IN ('3','4','5') THEN 'U'   
						    ELSE 'D' END                   AS UPCARUNLOAD_GP
				     , CARD_NO                             AS CARD_NO
				     , CAR_NO                              AS CAR_NO
				     , SUBSTR(YD_CARLD_STOP_LOC,1,1)       AS YD_GP
				     , CASE WHEN YD_CAR_PROG_STAT IN ('3','4','5') 
				            THEN SUBSTR(TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS'),1,8) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS'),1,8) END  AS CARLOAD_DONE_DATE
				     , CASE WHEN  YD_CAR_PROG_STAT IN ('3','4','5')
				            THEN SUBSTR(TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS'),9,6) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS'),9,6) END AS CARLOAD_DONE_TIME        

				     , TRANS_ORD_DATE                      AS TRANS_WORD_DATE
				     , TRANS_ORD_SEQNO                     AS TRANS_WORD_SEQNO
				  FROM TB_YD_CARSCH C
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 */
				trtNm = "임가공이송상차완료";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDDMR022";
				
			} else if("YDDMR028".equals(msgId)) {
				/* 
				SELECT 'YDDMR028'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDDMR028'                          AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				     , CARD_NO                                                     --카트번호
				     , CAR_NO                                                      --차량번호      
				     , TRANS_ORD_DATE                      AS TRANS_WORD_DATE      --운송작업지시일자
				     , TRANS_ORD_SEQNO                     AS TRANS_WORD_SEQNO     --운송작업지시순번
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS BAYIN_DDTT           --입동일시
				     , ARR_WLOC_CD                         AS WLOC_CD              --개소코드(착지) 
				     , YD_PNT_CD                                                   --야드차량트코드
				     , LOAN_PULLOUT_ABLE_YN                AS LOAN_PULLOUT_ABLE_YN --차입인출가능여부 Y,N,H
				  FROM (SELECT TS.CARD_NO
				             , TS.CAR_NO
				             , TS.TRANS_ORD_DATE
				             , TS.TRANS_ORD_SEQNO
				             , SC.WLOC_CD
				             , SC.YD_PNT_CD
				             , TS.YD_CAR_USE_GP
				             , NVL(TS.YD_BAYIN_WO_SEQ,9) AS YD_BAYIN_WO_SEQ
				             , TS.YD_CAR_SCH_ID
				             , (SELECT YD_CARPNT_CD 
				                  FROM TB_YD_CARPOINT 
				                 WHERE YD_STK_COL_GP = SC.YD_STK_COL_GP
				                   AND DEL_YN = 'N'
				                   AND ROWNUM = 1 
				               ) AS YD_CARPNT_CD 
				             , TS.ARR_WLOC_CD 
				             , :V_LOAN_PULLOUT_ABLE_YN AS LOAN_PULLOUT_ABLE_YN
				          FROM TB_YD_STKCOL SC
				             , TB_YD_CARSCH TS
				         WHERE SC.YD_STK_COL_GP       = :V_YD_STK_COL_GP
				           AND SC.DEL_YN              = 'N'
				           AND SC.YD_STK_COL_ACT_STAT = 'C' --비활성화
				           AND (SC.YD_STKBED_USG_CD IS NULL OR SC.YD_STKBED_USG_CD != 'GT') --출하
				           AND ((TS.YD_CAR_PROG_STAT = '1' AND TS.YD_CARLD_STOP_LOC = SC.YD_STK_COL_GP)
				             OR (TS.YD_CAR_PROG_STAT = 'A' AND TS.YD_CARUD_STOP_LOC = SC.YD_STK_COL_GP))
				           AND TS.DEL_YN             = 'N'
				         ORDER BY YD_BAYIN_WO_SEQ, YD_CAR_SCH_ID)
				 WHERE ROWNUM = 1          --첫번째가
				   AND YD_CAR_USE_GP = 'G' --출하차량   
				 */
				trtNm = "차량입동지시";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDDMR028";

			} else if("YDDMR034".equals(msgId)) {
				trtNm = "반납확인 정보";
				/*
				SELECT 'YDDMR001'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDDMR001'                          AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				     , COIL_NO AS GOODS_NO
				     , YD_GP
				     , NEXT_PROC                 --다음공정
				     , '*' AS RETURN_CONFIRM_GP  --반납확인구분
				     , TO_CHAR(SYSDATE,'YYYYMMDD') AS RETURN_TREAT_DATE --반납처리일자
				     , TO_CHAR(SYSDATE,'HH24MISS') AS RETURN_TREAT_TIME --반납처리시간
				     , '' AS RETURN_ETC_ERR        --반납 기타 ERROR
				     , '' AS RETURN_REAL_SPEC      --반납 실 규격
				     , COIL_T  AS RETURN_REAL_T    --반납실 두께
				     , COIL_W  AS RETURN_REAL_W    --반납실 폭
				     , '' AS RETURN_REAL_LEN       --반납 실 길이
				     , '' AS RETURN_REAL_GRADE     --반납 실 등급
				     , COIL_WT AS RETURN_REAL_WT   --반납실 중량
				     , '' AS RETURN_USAGE_CD       --반납 용도 CODE
				     , '' AS RETURN_REAL_USAGE_CD  --반납 실 용도 CODE
				     , '' AS RETURN_REAL_YEOJAE_GP --반납 실 여재 구분
				     , '' AS DIST_GOODS_GP         --출하제품구분
				  FROM TB_PT_COILCOMM
				 WHERE COIL_NO = :V_STL_NO 				
				*/
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDDMR034_PIDEV";

			} else if("YDDMR050".equals(msgId)) {
					trtNm = "상차완료(야드핸드링)";
					/* 
					SELECT 'YDDMR050'                          AS JMS_TC_CD          --JMSTC코드
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
					     , 'YDDMR050'                          AS TC_CODE            --IF구분코드
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT     --TC생성일시
					     , :V_YD_GP                            AS YD_GP              --야드구분
					     , :V_TRANS_ORD_DT                     AS TRANS_ORD_DT       --운송지시일자
					     , :V_TRANS_ORD_SEQNO                  AS TRANS_ORD_SEQNO    --운송지시시각
					     , :V_CMBN_CARLD_YN                    AS CMBN_CARLD_YN      -- 
					     , :V_CARLD_PNT_CD                     AS CARLD_PNT_CD       -- 
					     , :V_CAR_NO                           AS CAR_NO             --차량번호
					     , :V_HANDLING_CNT                     AS HANDLING_CNT       --핸드링수
					     , :V_YD_STK_BED_WHIO_STAT             AS YD_STK_BED_WHIO_STAT
					  FROM DUAL				
					*/
					jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDDMR050_PIDEV";

			} else if("YDDMR071".equals(msgId)) {
				/* 
				SELECT 'YDDMR071'                           AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDDMR071'                           AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  AS TC_CREATE_DDTT       --TC생성일시		
				     , CAR_NO		                    AS CAR_NO
				     , CARD_NO 		                    AS CARD_NO 
				     , SUBSTR(YD_CARLD_STOP_LOC,1,1)    AS YD_GP   
				     , CASE WHEN YD_CARLD_ST_DT IS NOT NULL AND LENGTH(TRIM(YD_CARLD_ST_DT)) = 14  
				            THEN SUBSTR(YD_CARLD_ST_DT,1,8)
				            ELSE '' END                     AS CARLOAD_START_DATE   
				     , CASE WHEN YD_CARLD_ST_DT IS NOT NULL AND LENGTH(TRIM(YD_CARLD_ST_DT)) = 14
				            THEN SUBSTR(YD_CARLD_ST_DT,9,6)
				            ELSE '' END                     AS CARLOAD_START_TIME  
				     , TRANS_ORD_DATE                   AS TRANS_WORD_DATE
				     , TRANS_ORD_SEQNO                  AS TRANS_WORD_SEQNO   
				     , (SELECT CR_FRTOMOVE_GP 
				          FROM TB_YD_STOCK
				         WHERE TRANS_ORD_DATE  = A.TRANS_ORD_DATE
				           AND TRANS_ORD_SEQNO = A.TRANS_ORD_SEQNO 
				           AND ROWNUM = 1)                  AS CR_FRTOMOVE_GP   
				  FROM (SELECT A.YD_CAR_SCH_ID              AS YD_CAR_SCH_ID
				             , A.TRN_EQP_CD                 AS TRN_EQP_CD
				             , A.SPOS_WLOC_CD               AS SPOS_WLOC_CD
				             , A.ARR_WLOC_CD                AS ARR_WLOC_CD
				             , A.YD_EQP_WRK_STAT            AS YD_EQP_WRK_STAT
				             , A.YD_CARLD_CMPL_DT           AS YD_CARLD_CMPL_DT
				             , A.YD_CARLD_STOP_LOC          AS YD_CARLD_STOP_LOC
				             , A.CAR_NO                     AS CAR_NO
				             , A.YD_EQP_ID                  AS YD_EQP_ID
				             , TO_CHAR(A.YD_CARLD_ST_DT     , 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT                              
				             , TO_CHAR(A.YD_CARUD_ST_DT     , 'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT
				             , TO_CHAR(A.YD_CARUD_CMPL_DT   , 'YYYYMMDDHH24MISS') AS YD_CARUD_CMPL_DT
				             , A.CARD_NO                    AS CARD_NO                 
				             , A.DEL_YN 
				             , A.TRANS_ORD_DATE
				             , A.TRANS_ORD_SEQNO
				          FROM TB_YD_CARSCH A
				         WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID 
				           AND A.DEL_YN = 'N'
				        )  A 
				 WHERE 1 = 1
				*/	   
				trtNm = "코일이송 상차개시";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDDMR071";

			} else if("YDDMR072".equals(msgId)) {
				/*
				SELECT 'YDDMR072'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDDMR072'                          AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시	
				     , TA.YD_CAR_SCH_ID                    AS YD_CAR_SCH_ID                         
				     , TA.CARD_NO		                   AS CARD_NO                                 
				     , TA.CAR_NO		                   AS CAR_NO                                  
				     , SUBSTR(TA.YD_CARLD_STOP_LOC,1,1)    AS YD_GP                 
				     , TB.STL_NO                           AS GOODS_NO                                        
				     , TB.TRANS_ORD_DATE                   AS TRANS_WORD_DATE                       
				     , TB.TRANS_ORD_SEQNO                  AS TRANS_WORD_SEQNO             
				  FROM TB_YD_CARSCH TA
					 , TB_YD_STOCK  TB
				 WHERE TA.TRANS_ORD_DATE  = TB.TRANS_ORD_DATE
				   AND TA.TRANS_ORD_SEQNO = TB.TRANS_ORD_SEQNO
				   AND TB.STL_NO          = :V_STL_NO
				   AND TA.YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID 		
				 */
				trtNm = "코일일품출하상차실적";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDDMR072";

			} else if("YDDMR073".equals(msgId)) {
				/* 
				SELECT 'YDDMR073'                           AS JMS_TC_CD            --JMSTC코드
					 , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
					 , 'YDDMR073'                           AS TC_CODE              --IF구분코드
					 , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  AS TC_CREATE_DDTT       --TC생성일시		
				     , TA.CAR_NO		                    AS CAR_NO
				     , TA.CARD_NO 		                    AS CARD_NO 
				     , substr(TA.YD_CARLD_STOP_LOC,1,1)     AS YD_GP   
				     , SUBSTR(TA.YD_CARLD_CMPL_DT,1,8)      AS CARLOAD_END_DATE   
				     , SUBSTR(TA.YD_CARLD_CMPL_DT,9,6)      AS CARLOAD_END_TIME  
				     , TB.TRANS_ORD_DATE                    AS TRANS_WORD_DATE
				     , TB.TRANS_ORD_SEQNO                   AS TRANS_WORD_SEQNO   
				     , TB.CR_FRTOMOVE_GP                    AS CR_FRTOMOVE_GP
				  FROM (SELECT A.YD_CAR_SCH_ID      AS YD_CAR_SCH_ID
				             , B.DEL_YN             AS DEL_YN
				             , A.TRN_EQP_CD         AS TRN_EQP_CD
				             , A.SPOS_WLOC_CD       AS SPOS_WLOC_CD
				             , A.ARR_WLOC_CD        AS ARR_WLOC_CD
				             , A.YD_EQP_WRK_STAT    AS YD_EQP_WRK_STAT
				             , B.STL_NO             AS STL_NO
				             , A.YD_CARLD_STOP_LOC  AS YD_CARLD_STOP_LOC
				             , B.YD_CAR_UPP_LOC_CD  AS YD_CAR_UPP_LOC_CD
				             , B.YD_STK_BED_NO      AS YD_STK_BED_NO
				             , B.YD_STK_LYR_NO      AS YD_STK_LYR_NO
				             , A.CAR_NO             AS CAR_NO
				             , A.YD_EQP_ID          AS YD_EQP_ID
				             , NVL(TO_CHAR(A.YD_CARLD_CMPL_DT, 'YYYYMMDDHH24MISS'),:V_WR_DT) AS YD_CARLD_CMPL_DT --상차완료                             
				             , A.CARD_NO            AS CARD_NO                 
				             , A.YD_CARUD_STOP_LOC  AS YD_CARUD_STOP_LOC
				             , A.YD_PNT_CD1
				          FROM TB_YD_CARSCH     A
				             , TB_YD_CARFTMVMTL B
				         WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
				           AND A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				           AND ROWNUM = 1
				        ) TA
				      , TB_YD_STOCK TB
				  WHERE TA.STL_NO = TB.STL_NO
				    AND TA.DEL_YN = 'N'
				*/		   
				trtNm = "코일이송 상차완료";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDDMR073";

			} else if("YDDMR075".equals(msgId)) {
				/*
				SELECT 'YDDMR075'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDDMR075'                          AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				     , TA.CARD_NO		                   AS CARD_NO     
				     , TA.CAR_NO	                       AS CAR_NO            
				     , substr(TA.YD_CARUD_STOP_LOC,1,1)    AS YD_GP
				     , substr(TA.YD_CARUD_ST_DT,1,8)	   AS CARUD_START_DATE
				     , substr(TA.YD_CARUD_ST_DT,9,6)	   AS CARUD_START_TIME     
				     , TB.TRANS_ORD_DATE                   AS TRANS_WORD_DATE
				     , TB.TRANS_ORD_SEQNO                  AS TRANS_WORD_SEQNO   
				     , TB.CR_FRTOMOVE_GP                   AS CR_FRTOMOVE_GP
				  FROM (SELECT A.YD_CAR_SCH_ID      AS YD_CAR_SCH_ID
				             , B.DEL_YN             AS DEL_YN
				             , A.TRN_EQP_CD         AS TRN_EQP_CD
				             , A.SPOS_WLOC_CD       AS SPOS_WLOC_CD
				             , A.ARR_WLOC_CD        AS ARR_WLOC_CD
				             , A.YD_EQP_WRK_STAT    AS YD_EQP_WRK_STAT
				             , A.YD_EQP_WRK_SH      AS YD_EQP_WRK_SH
				             , B.STL_NO             AS STL_NO
				             , A.YD_CARLD_CMPL_DT   AS YD_CARLD_CMPL_DT
				             , A.YD_CARLD_STOP_LOC  AS YD_CARLD_STOP_LOC
				             , B.YD_CAR_UPP_LOC_CD  AS YD_CAR_UPP_LOC_CD
				             , B.YD_STK_BED_NO      AS YD_STK_BED_NO
				             , B.YD_STK_LYR_NO      AS YD_STK_LYR_NO
				             , A.CAR_NO             AS CAR_NO
				             , A.YD_EQP_ID          AS YD_EQP_ID
				             , NVL(TO_CHAR(A.YD_CARUD_ST_DT ,'YYYYMMDDHH24MISS'),:V_WR_DT) AS YD_CARUD_ST_DT
				             , A.CARD_NO            AS CARD_NO                 
				             , A.YD_CARUD_STOP_LOC  AS YD_CARUD_STOP_LOC
				             , A.YD_PNT_CD1
				          FROM TB_YD_CARSCH A, TB_YD_CARFTMVMTL B
				         WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
				           AND A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				           AND ROWNUM = 1) TA
				      ,TB_YD_STOCK TB
				 WHERE TA.STL_NO = TB.STL_NO  
				 */
				trtNm = "코일이송하차개시전송 PDA";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDDMR075";
				
			} else if("YDDMR076".equals(msgId)) {
				/*
				SELECT 'YDDMR076'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDDMR076'                          AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				     , CARD_NO		                       AS CARD_NO     
				     , CAR_NO		                       AS CAR_NO            
				     , SUBSTR(YD_CARUD_STOP_LOC,1,1)       AS YD_GP
				     , SUBSTR(NVL(TO_CHAR(C.YD_CARUD_CMPL_DT ,'YYYYMMDDHH24MISS'),:V_WR_DT),1,8)	       AS CARUD_END_DATE
				     , SUBSTR(NVL(TO_CHAR(C.YD_CARUD_CMPL_DT ,'YYYYMMDDHH24MISS'),:V_WR_DT),9,6)    	   AS CARUD_END_TIME     
				     , TRANS_ORD_DATE                      AS TRANS_WORD_DATE
				     , TRANS_ORD_SEQNO                     AS TRANS_WORD_SEQNO   
				     , (SELECT MIN(B.CR_FRTOMOVE_GP) 
				          FROM TB_YD_CARFTMVMTL A
				             , TB_YD_STOCK      B
				         WHERE 1=1
				           AND A.STL_NO = B.STL_NO
				           AND A.YD_CAR_SCH_ID = C.YD_CAR_SCH_ID
				       ) AS CR_FRTOMOVE_GP
				  FROM TB_YD_CARSCH C
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/ 
				trtNm = "코일이송하차완료전송 PDA"; 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDDMR076";
			}
			
			/* 구내운송  */  			
			else if ("YDTSJ007".equals(msgId)) {
				trtNm = "소재차량상차개시";
				/*
				SELECT 'YDTSJ007'                          AS JMS_TC_CD          --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , CS.TRN_EQP_CD
				     , CS.SPOS_WLOC_CD
				     , SC.YD_PNT_CD    AS SPOS_YD_PNT_CD
				     , CS.ARR_WLOC_CD
				     , CS.YD_CARLD_ST_DT
				  FROM TB_YD_CARSCH   CS
				     , TB_YD_STKCOL   SC
				 WHERE CS.YD_CAR_SCH_ID     = :V_YD_CAR_SCH_ID
				   AND CS.YD_CARLD_STOP_LOC = SC.YD_STK_COL_GP
				   AND SC.YD_CAR_USE_GP = 'L'           --L:구내운송, G:출하차량
				   AND SC.DEL_YN        = 'N' 
				 */
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDTSJ007";

			} else if("YDTSJ008".equals(msgId)) {
				trtNm = "소재차량상차완료";
				/*
				SELECT 'YDTSJ008'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDTSJ008'                          AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				     , TRN_EQP_CD           --운송장비코드
				     , SPOS_WLOC_CD         --발지개소코드
				     , SPOS_YD_PNT_CD       --발지야드포인트코드
				     , ARR_WLOC_CD          --착지개소코드
				     , TRN_WRK_MTL_GP       --운송작업재료구분
				     , MTL_UGNT_GP          --재료긴급구분
				     , HCR_GP               --HCR구분
				     , CARLD_CMPL_DT        --상차완료일시
				     , CARLD_SH             --상차매수
				     , STL_NO1             --특수강재료번호1
				     , STL_WT1              --재료중량1
				     , STL_NO2             --특수강재료번호2
				     , STL_WT2              --재료중량2
				     , STL_NO3             --특수강재료번호3
				     , STL_WT3              --재료중량3
				     , STL_NO4             --특수강재료번호4
				     , STL_WT4              --재료중량4
				     , STL_NO5             --특수강재료번호5
				     , STL_WT5              --재료중량5
				     , STL_NO6             --특수강재료번호6
				     , STL_WT6              --재료중량6
				     , STL_NO7             --특수강재료번호7
				     , STL_WT7              --재료중량7
				     , STL_NO8             --특수강재료번호8
				     , STL_WT8              --재료중량8
				     , STL_NO9             --특수강재료번호9
				     , STL_WT9              --재료중량9
				     , STL_NO10            --특수강재료번호10
				     , STL_WT10             --재료중량10
				     , STL_NO11            --특수강재료번호11
				     , STL_WT11             --재료중량11
				     , STL_NO12            --특수강재료번호12
				     , STL_WT12             --재료중량12
				  FROM(
				       SELECT
				              DD.YD_CAR_SCH_ID
				            , MAX(DD.TRN_EQP_CD) AS TRN_EQP_CD
				            , MAX(DD.SPOS_WLOC_CD) AS SPOS_WLOC_CD
				            , MAX(DD.SPOS_YD_PNT_CD) AS SPOS_YD_PNT_CD
				            , MAX(DD.ARR_WLOC_CD) AS ARR_WLOC_CD
				            , MAX(DD.TRN_WRK_MTL_GP) AS TRN_WRK_MTL_GP
				            , MAX(DD.MTL_UGNT_GP) AS MTL_UGNT_GP
				            , MAX(DD.HCR_GP) AS HCR_GP
				            , MAX(DD.CARLD_CMPL_DT) AS CARLD_CMPL_DT
				            , COUNT(*) AS CARLD_SH
				            , MAX(DECODE(NO,1,DD.STL_NO,''))       AS STL_NO1
				            , MAX(DECODE(NO,1,DD.YD_MTL_WT,''))    AS STL_WT1
				            , MAX(DECODE(NO,2,DD.STL_NO,''))       AS STL_NO2
				            , MAX(DECODE(NO,2,DD.YD_MTL_WT,''))    AS STL_WT2
				            , MAX(DECODE(NO,3,DD.STL_NO,''))       AS STL_NO3
				            , MAX(DECODE(NO,3,DD.YD_MTL_WT,''))    AS STL_WT3
				            , MAX(DECODE(NO,4,DD.STL_NO,''))       AS STL_NO4
				            , MAX(DECODE(NO,4,DD.YD_MTL_WT,''))    AS STL_WT4
				            , MAX(DECODE(NO,5,DD.STL_NO,''))       AS STL_NO5
				            , MAX(DECODE(NO,5,DD.YD_MTL_WT,''))    AS STL_WT5
				            , MAX(DECODE(NO,6,DD.STL_NO,''))       AS STL_NO6
				            , MAX(DECODE(NO,6,DD.YD_MTL_WT,''))    AS STL_WT6
				            , MAX(DECODE(NO,7,DD.STL_NO,''))       AS STL_NO7
				            , MAX(DECODE(NO,7,DD.YD_MTL_WT,''))    AS STL_WT7
				            , MAX(DECODE(NO,8,DD.STL_NO,''))       AS STL_NO8
				            , MAX(DECODE(NO,8,DD.YD_MTL_WT,''))    AS STL_WT8
				            , MAX(DECODE(NO,9,DD.STL_NO,''))       AS STL_NO9
				            , MAX(DECODE(NO,9,DD.YD_MTL_WT,''))    AS STL_WT9
				            , MAX(DECODE(NO,10,DD.STL_NO,''))      AS STL_NO10
				            , MAX(DECODE(NO,10,DD.YD_MTL_WT,''))   AS STL_WT10
				            , MAX(DECODE(NO,11,DD.STL_NO,''))      AS STL_NO11
				            , MAX(DECODE(NO,11,DD.YD_MTL_WT,''))   AS STL_WT11
				            , MAX(DECODE(NO,12,DD.STL_NO,''))      AS STL_NO12
				            , MAX(DECODE(NO,12,DD.YD_MTL_WT,''))   AS STL_WT12
				         FROM(
				              SELECT A.YD_CAR_SCH_ID 
				                   , A.TRN_EQP_CD 
				                   , A.SPOS_WLOC_CD 
				                   , C.YD_PNT_CD      AS SPOS_YD_PNT_CD
				                   , A.ARR_WLOC_CD 
				                   , CASE E.STL_APPEAR_GP WHEN 'E' THEN 'CM' ELSE 'CG' END AS TRN_WRK_MTL_GP
				                   , D.URGENT_FRTOMOVE_WORD_GP AS MTL_UGNT_GP --'Y(긴급재),N(일반재)
				                   , NVL(B.HCR_GP,E.HCR_GP)    AS HCR_GP
				                   , TO_CHAR(A.YD_CARLD_CMPL_DT, 'YYYYMMDDHH24MISS') AS CARLD_CMPL_DT
				                   , B.STL_NO
				                   , ROWNUM NO
				                   , E.COIL_WT AS YD_MTL_WT 
				               FROM TB_YD_CARSCH     A
				                  , TB_YD_CARFTMVMTL B
				                  , TB_YD_STKCOL     C
				                  , TB_YD_STOCK      D
				                  , TB_PT_COILCOMM   E
				              WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				                AND A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
				                AND A.YD_CARLD_STOP_LOC = C.YD_STK_COL_GP 
				                AND B.STL_NO = D.STL_NO
				                AND B.STL_NO = E.COIL_NO
				              
				             ) DD
				        GROUP BY YD_CAR_SCH_ID      
				      )    
				 WHERE 1 = 1 
				 */
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDTSJ008";
				
			} else if("YDTSJ009".equals(msgId)) {
				trtNm = "소재차량하차개시";
				/* 
				SELECT 'YDTSJ009'                          AS JMS_TC_CD          --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				      ,TS.TRN_EQP_CD                                             --운송장비코드
				      ,TS.ARR_WLOC_CD                                            --착지개소코드
				      ,SC.YD_PNT_CD                        AS ARR_YD_PNT_CD      --착지야드포인트코드
				      ,NVL(:V_WR_DT,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')) AS TRN_WRK_ST_DT --운송작업시작일시
				  FROM TB_YD_STKCOL SC
				      ,TB_YD_CARSCH TS
				 WHERE SC.YD_CAR_USE_GP = TS.YD_CAR_USE_GP
				   AND SC.TRN_EQP_CD    = TS.TRN_EQP_CD
				   AND SC.YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND SC.YD_CAR_USE_GP = 'L'           --구내운송
				   AND SC.DEL_YN        = 'N'
				   AND TS.YD_CAR_PROG_STAT IN ('B','C') --하차도착,검수
				   AND TS.DEL_YN        = 'N'
				*/	   
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDTSJ009";

			} else if("YDTSJ010".equals(msgId)) {
				trtNm = "소재차량하차완료";
				/* 
				SELECT 'YDTSJ010'                          AS JMS_TC_CD          --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , TS.TRN_EQP_CD                                             --운송장비코드
				     , TS.ARR_WLOC_CD                                            --착지개소코드
				     , SC.YD_PNT_CD                        AS ARR_YD_PNT_CD      --착지야드포인트코드
				     , TO_CHAR(TS.YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS') AS CARUD_CMPL_DT --하차완료일시
				  FROM TB_YD_STKCOL SC
				     , TB_YD_CARSCH TS
				 WHERE SC.YD_STK_COL_GP = TS.YD_CARUD_STOP_LOC
				   AND TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/   
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDTSJ010";
				
			} else if("YDTSJ011".equals(msgId)) {
				trtNm = "소재차량 포인트 지시";
				/*
				WITH PARAM AS (
				SELECT :V_YD_STK_COL_GP AS P_YD_STK_COL_GP
				     , :V_YD_CAR_SCH_ID AS P_YD_CAR_SCH_ID
				  FROM DUAL     
				)
				SELECT 'YDTSJ011'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     
				     , TRN_EQP_CD
				     , WLOC_CD
				     , NVL(YD_PNT_CD, '0000') AS YD_PNT_CD
				     , 'A' AS PNT_WO_GP
				     , PNT_WO_DT
				     , CASE WHEN NVL(YD_PNT_CD, '0000') = '0000' THEN CASE WHEN SUBSTR(P_YD_STK_COL_GP, 5, 2) IN ('01','02') THEN '1' --1통로
				                                                           WHEN SUBSTR(P_YD_STK_COL_GP, 5, 2) IN ('04','05') THEN '2' --3통로
				                                                           ELSE '3' END
				            ELSE SUBSTR(P_YD_STK_COL_GP, 2, 1) END AS YD_BAY_GP
				     ,NVL((SELECT NVL(CAR_CNT, '99')
				             FROM USRYDA.VW_YD_CARPOINT   VC
				                , USRYDA.TB_YD_CARPOINT   TC
				            WHERE VC.YD_GP = 'J' 
				              AND VC.YD_CARPNT_CD = TC.YD_CARPNT_CD
				              AND VC.YD_PNT_CD    = A.YD_PNT_CD
				              AND TC.YD_CAR_USETYPE_GP IN ('TO','GT')
				              AND ROWNUM = 1
				          ), '99') AS ETR_HOU_WAI_SEQ            
				  FROM ( 
				        SELECT CS.TRN_EQP_CD    AS TRN_EQP_CD      -- 운송장비코드
				             , CS.SPOS_WLOC_CD  AS WLOC_CD         -- 발지개소코드
				             , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  AS PNT_WO_DT       -- 포인트지시일시
				             , YD_PNT_CD1 AS YD_PNT_CD          
				             , CS.YD_CAR_SCH_ID 
				          FROM TB_YD_CARSCH CS
				             , PARAM
				         WHERE YD_CAR_SCH_ID   = P_YD_CAR_SCH_ID
				           AND YD_EQP_WRK_STAT = 'U'
				         UNION ALL
				        SELECT CS.TRN_EQP_CD    AS TRN_EQP_CD      -- 운송장비코드
				             , CS.ARR_WLOC_CD   AS WLOC_CD         -- 착지개소코드
				             , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  AS PNT_WO_DT       -- 포인트지시일시
				             , YD_PNT_CD3 AS YD_PNT_CD          
				             , CS.YD_CAR_SCH_ID
				          FROM TB_YD_CARSCH CS
				             , PARAM
				         WHERE YD_CAR_SCH_ID   = P_YD_CAR_SCH_ID
				           AND YD_EQP_WRK_STAT = 'L'
				       ) A
				     , PARAM
				 WHERE 1 = 1 
				 */
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDTSJ011";
				
				/*
				UPDATE TB_YD_CARSCH
				   SET MODIFIER      = :V_MODIFIER
				     , MOD_DDTT      = SYSDATE   
				     , YD_MSG_NM = (
				                    WITH PARAM AS (
				                    SELECT SUBSTR(NVL(:V_YD_STK_COL_GP, 'J_PT'), 1, 2) AS P_YD_GP_BAY
				                         , :V_YD_STK_COL_GP AS P_YD_STK_COL_GP
				                         , (SELECT SPOS_WLOC_CD 
				                              FROM TB_YD_CARSCH 
				                             WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				                               AND YD_EQP_WRK_STAT = 'U'
				                             UNION ALL
				                            SELECT ARR_WLOC_CD
				                              FROM TB_YD_CARSCH
				                             WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				                               AND YD_EQP_WRK_STAT = 'L') AS P_WLOC_CD
				                      FROM DUAL     
				                    )
				                    SELECT CASE WHEN CNT1 = 0 THEN SUBSTR(P_YD_GP_BAY, 2, 1)||'동 지시개소코드가 야드와 틀림.'
				                                WHEN CNT2 = 0 THEN SUBSTR(P_YD_GP_BAY, 2, 1)||'동 개소지의 야드포인트가 사용불가.'
				                                WHEN CNT3 > 0 THEN SUBSTR(P_YD_GP_BAY, 2, 1)||'동 해당개소에 다른 차량 점유.'
				                                WHEN CNT4 = 0 THEN SUBSTR(P_YD_GP_BAY, 2, 1)||'동 구내운송 전용구분 확인 요망.'
				                                WHEN P_YD_STK_COL_GP IS NULL THEN '목적동OR이송대상이 존재 안함.'
				                                ELSE '시스템 담당자 확인 요망.' 
				                            END AS YD_MSG_NM
				                      FROM (SELECT COUNT(*) AS CNT1
				                              FROM TB_YD_STKCOL A, PARAM
				                             WHERE A.YD_STK_COL_GP LIKE P_YD_GP_BAY ||'%'
				                               AND A.WLOC_CD = P_WLOC_CD
				                           )
				                         , (SELECT COUNT(*) AS CNT2
				                              FROM TB_YD_STKCOL A, PARAM
				                             WHERE A.YD_STK_COL_GP LIKE P_YD_GP_BAY ||'%'
				                               AND A.WLOC_CD = P_WLOC_CD
				                               AND YD_STK_COL_ACT_STAT = 'C'
				                           )
				                         , (SELECT COUNT(*) AS CNT3
				                              FROM TB_YD_STKCOL A, PARAM
				                             WHERE A.YD_STK_COL_GP LIKE P_YD_GP_BAY ||'%'
				                               AND A.WLOC_CD = P_WLOC_CD
				                               AND (YD_STK_COL_ACT_STAT ='L' OR TRN_EQP_CD IS NOT NULL OR CARD_NO IS NOT NULL)
				                           )
				                         , (SELECT COUNT(*) AS CNT4
				                              FROM TB_YD_STKCOL A, PARAM
				                             WHERE A.YD_STK_COL_GP LIKE P_YD_GP_BAY ||'%'
				                               AND A.WLOC_CD = P_WLOC_CD
				                               AND YD_STK_COL_ACT_STAT ='C'    
				                           )
				                         , PARAM
				                     WHERE 1 = 1       
				                    )
				 WHERE 1 = 1
				   AND DEL_YN        = 'N'
				   AND YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 */
				commDao.update(rcvMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.updYdMsgYDTSJ011", logId, mthdNm, "차량스케줄 메시지수정");
				
			} else if("YDTSJ012".equals(msgId)) {
				trtNm = "소재차량 포인트 개폐";
				/*
				SELECT 'YDTSJ012'                          AS JMS_TC_CD          --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , WLOC_CD           AS PRSNT_LOC_WLOC_CD
				     , YD_PNT_CD
				     , :V_PNT_UNIT_CL_GP AS PNT_UNIT_CL_GP
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS YD_PNT_OP_CL_TT 
				  FROM TB_YD_STKCOL
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
				*/   
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDTSJ012";	
				
			}
/* 진행관리  */			
			 else if("YDPTJ002".equals(msgId)) {
				trtNm = "코일소재이송완료실적";
				/* 
				SELECT 'YDPTJ002'                          AS JMS_TC_CD          --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , 'YDPTJ002'                          AS TC_CODE            --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT     --TC생성일시

				     , A.COIL_NO                           AS STL_NO
				     , A.ORD_NO                            AS ORD_NO
				     , A.ORD_DTL                           AS ORD_DTL
				     , A.PLNT_PROC_CD                      AS PLNT_PROC_CD
				     , A.STL_APPEAR_GP                     AS STL_APPEAR_GP
				     , A.CURR_PROG_CD                      AS CURR_PROG_CD
				     , A.ORD_YEOJAE_GP                     AS ORD_YEOJAE_GP
				     , A.COIL_WT                           AS STL_WT
				     , ''                                  AS DS_MTL_WT
				     , A.RECORD_PROG_STAT                  AS MTL_STAT_GP
				     , A.RECORD_END_GP                     AS RECORD_END_GP
				     , ''                                  AS RECORD_END_GP1
				     , A.BEFO_PROG_CD                      AS BEFO_PROG_CD
				     , A.BEF_ORD_NO                        AS BEF_ORD_NO
				     , A.BEF_ORD_DTL                       AS BEF_ORD_DTL
				     , A.MMATL_FEE_NO                      AS MMATL_FEE_NO
				     , A.MATCH_ORDERTRANS_GP               AS ORDERTRANS_MATCH_GP
				  FROM TB_PT_COILCOMM A
				 WHERE A.COIL_NO = :V_COIL_NO				
				*/
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDPTJ002";
				
			} 
/* 품질  */ 
			 else if("YDQMJ002".equals(msgId)) {
				trtNm = "품질 송신:열연정정입측보급실적";
				/*  
				SELECT 'YDQMJ002'                          AS JMS_TC_CD          --JMSTC코드  
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시  
				     , SUBSTR(A.PTOP_PLNT_GP, 2, 1)        AS PTOP_PLNT_GP            --IF구분코드  
				     , A.STL_APPEAR_GP                     AS STL_APPEAR_GP     --TC생성일시  
				     , A.COIL_NO                           AS STL_NO  
				  FROM (SELECT B.PTOP_PLNT_GP
				             , B.STL_APPEAR_GP
				             , B.COIL_NO
				          FROM TB_YD_CRNWRKMTL A
				             , TB_PT_COILCOMM  B
				         WHERE A.STL_NO = B.COIL_NO
				           AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
				       ) A				
				*/
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYDQMJ002";

/* PIDEV  */
			} else if ("M10YDLMJ1125".equals(msgId) ) {
				/*  
					SELECT 
					       'M10YDLMJ1125'                         				AS MQ_TC_CODE              --IF구분코드
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 						AS MQ_TC_CREATE_DDTT       --TC생성일시
					     , TRANS_ORD_DATE                      						AS TRANS_REQ_DATE
					     , TRANS_ORD_SEQNO                     						AS TRANS_REQ_SEQNO     
					     , CAR_NO                              						AS CAR_NO
							 -- , CARD_NO                             				AS CARD_NO     
					     , SUBSTR(YD_CARLD_STOP_LOC,1,1)       AS YD_GP		 
					     , 'H'																						AS DIST_GOODS_GP
					     , ''																							AS YARD_GP     
					--	 	 , CASE WHEN YD_CAR_PROG_STAT IN ('3','4','5') THEN 'U'   
					--		   ELSE 'D' END                   AS UPCARUNLOAD_GP
					     , CASE WHEN YD_CAR_PROG_STAT IN ('3','4','5') 
					            THEN SUBSTR(TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS'),1,8) 
					            ELSE SUBSTR(TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS'),1,8) END  AS CARLOAD_DONE_DATE
					     , CASE WHEN  YD_CAR_PROG_STAT IN ('3','4','5')
					            THEN SUBSTR(TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS'),9,6) 
					            ELSE SUBSTR(TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS'),9,6) END AS CARLOAD_DONE_TIME        
					  FROM 
					  			TB_YD_CARSCH C
					 WHERE 
					 				YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 */
				trtNm = "임가공이송하차완료";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1125_PIDEV";
				
				
			 } else if ("M10YDLMJ1115".equals(msgId) ) {
				 /* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1115_PIDEV  
				SELECT 
				       'M10YDLMJ1115'                          AS MQ_TC_CD  --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 		 AS MQ_TC_CREATE_DDTT       --TC생성일시
				     , TRANS_ORD_DATE                      AS TRN_REQ_DATE
				     , TRANS_ORD_SEQNO                     AS TRN_REQ_SEQ     
				     , CAR_NO                              AS CAR_NO
				     , SUBSTR(YD_CARLD_STOP_LOC,1,1)       AS YD_GP
				     , 'H'			 AS DIST_GOODS_GP
				     , ''			 AS YARD_GP
				 --    , CARD_NO                           AS CARD_NO              
				--     , CASE WHEN YD_CAR_PROG_STAT IN ('2','3','4','5') THEN 'U'   
				--			 ELSE 'D' END                   AS UPCARUNLOAD_GP      
				     , CASE WHEN  YD_CAR_PROG_STAT IN ('2','3','4','5') 
				            THEN SUBSTR(TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS'),1,8) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS'),1,8) END  AS CARUD_ST_DATE
				     , CASE WHEN  YD_CAR_PROG_STAT IN ('2','3','4','5')
				            THEN SUBSTR(TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS'),9,6) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS'),9,6) END  AS CARUD_ST_TIME
				  FROM 
				  		 TB_YD_CARSCH C
				 WHERE 
				 			 YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/ 
				trtNm = "임가공이송하차개시";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1115_PIDEV"; 
				
			} else if ("M10YDLMJ1075".equals(msgId) ) {
				 /* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1075_PIDEV 
				SELECT 
				       'M10YDLMJ1075'                      AS MQ_TC_CD  --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT       --TC생성일시
				     , TRANS_ORD_DATE                      AS TRN_REQ_DATE
				     , TRANS_ORD_SEQNO                     AS TRN_REQ_SEQ     
				     , CAR_NO                              AS CAR_NO
				     , SUBSTR(YD_CARLD_STOP_LOC,1,1)       AS YD_GP
				     , 'H'								   AS DIST_GOODS_GP
				     , ''								   AS YARD_GP
				 --    , CARD_NO                           AS CARD_NO              
				--     , CASE WHEN YD_CAR_PROG_STAT IN ('2','3','4','5') THEN 'U'   
				--			 ELSE 'D' END                   AS UPCARUNLOAD_GP      
				     , CASE WHEN  YD_CAR_PROG_STAT IN ('2','3','4','5') 
				            THEN SUBSTR(TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS'),1,8) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS'),1,8) END  AS CARLOAD_START_DATE
				     , CASE WHEN  YD_CAR_PROG_STAT IN ('2','3','4','5')
				            THEN SUBSTR(TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS'),9,6) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS'),9,6) END  AS CARLOAD_START_TIME 
				  FROM 
				  	   TB_YD_CARSCH C
				 WHERE 
				 	   YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/ 
				trtNm = "임가공이송상차개시";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1075_PIDEV"; 
				
				
			} else if ("M10YDLMJ1011".equals(msgId) ) {
				/* 
				-- 입고실적적 전문조회 
				SELECT 
				       'M10YDLMJ1011'                      AS MQ_TC_CD            -- IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT   -- TC생성일시
				     , SC.YD_GP                                                   -- 야드구분
				     , 'H'                                 AS DIST_GOODS_GP       -- 출하제품구분
				     , ''                                  AS YARD_GP             -- 출하창고구분
				     , MT.STL_NO                           AS GOODS_NO            -- 제품 번호
				     , (SELECT YD_STK_COL_GP||YD_STK_BED_NO||SUBSTR(YD_STK_LYR_NO,2,2)
				          FROM TB_YD_STKLYR 
				         WHERE STL_NO = MT.STL_NO AND ROWNUM = 1) AS STORE_LOC_CD -- 저장위치코드
				     , TO_CHAR(SYSDATE,'YYYYMMDD') AS RECEIPT_DATE                -- 입고일자
				     , TO_CHAR(SYSDATE,'HH24MISS') AS RECEIPT_TIME                -- 입고시각
				     , PT.PRD_ITM_CD AS PROD_ITEM_CODE
				     , PT.CURR_PROG_CD 
				  FROM TB_YD_WRKBOOK    SC
				     , TB_YD_WRKBOOKMTL MT
				     , TB_PT_COILCOMM   PT
				 WHERE SC.YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND SC.YD_WBOOK_ID = MT.YD_WBOOK_ID
				   AND MT.STL_NO = PT.COIL_NO
				   AND PT.CURR_PROG_CD IN ('H','2')
				 */       				
				trtNm = "입고실적";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1011_PIDEV";
			
			} else if("M10YDLMJ1121B".equals(msgId)) {
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1121B_PIDEV 
				SELECT 'M10YDLMJ1121'                          AS MQ_TC_CD             -- IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')     AS MQ_TC_CREATE_DDTT    -- TC생성일시
				     , TRANS_ORD_DATE                          AS TRN_REQ_DATE
				     , TRANS_ORD_SEQNO                         AS TRN_REQ_SEQ
				     , CAR_NO		                           AS CAR_NO
				     , SUBSTR(YD_CARUD_STOP_LOC,1,1)           AS YD_GP
				     , 'H'                                     AS DIST_GOODS_GP
				     , ''                                      AS YARD_GP
				     , '0'                                     AS GOODS_CNT
				     , SUBSTR(NVL(TO_CHAR(C.YD_CARUD_CMPL_DT ,'YYYYMMDDHH24MISS'),:V_WR_DT),1,8)	       AS CARUD_CMPL_DATE
				     , SUBSTR(NVL(TO_CHAR(C.YD_CARUD_CMPL_DT ,'YYYYMMDDHH24MISS'),:V_WR_DT),9,6)    	   AS CARUD_CMPL_TIME
				     , (SELECT MIN(B.CR_FRTOMOVE_GP) 
				          FROM TB_YD_CARFTMVMTL A
				             , TB_YD_STOCK      B
				         WHERE 1=1
				           AND A.STL_NO = B.STL_NO
				           AND A.YD_CAR_SCH_ID = C.YD_CAR_SCH_ID
				       ) AS CR_FRTOMOVE_GP
				  FROM TB_YD_CARSCH C
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 */
				trtNm = "코일이송하차완료전송 PDA"; 
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1121B_PIDEV";				
			} else if ("M10YDLMJ1031".equals(msgId)) {
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1031_PIDEV  
				WITH PARA_TABLE AS
				(
				   SELECT 'M10YDLMJ1031'   AS JMS_TC_CD 
				        , :V_STL_NO    AS STL_NO
				     FROM DUAL
				)
				SELECT P.JMS_TC_CD                         AS MQ_TC_CD                                       -- IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT                              -- 전송일시
				     , B.YD_GP                                                                               -- 야드구분
				     , 'H' AS DIST_GOODS_GP                                                                  -- 출하제품구분
				     , '' AS YARD_GP                                                                         -- 출하창고구분
				     , A.STL_NO                            AS GOODS_NO                                       -- 제품 번호
				     , B.YD_STR_LOC_HIS1                   AS STORE_LOC_CD_FROM                              -- FROM 저장위치
				     , C.YD_STK_COL_GP||C.YD_STK_BED_NO||SUBSTR(C.YD_STK_LYR_NO, 2, 3) AS STORE_LOC_CD_TO    -- 저장위치코드
				     , TO_CHAR(SYSDATE,'YYYYMMDD')         AS MOVENSTACK_DATE                                -- 이적 일자
				     , TO_CHAR(SYSDATE,'HH24MISS')         AS MOVENSTACK_TIME                                -- 이적 시각
				  FROM TB_YD_STOCK    A
				     , TB_PT_COILCOMM B
				     , TB_YD_STKLYR   C
				     , PARA_TABLE P
				 WHERE A.STL_NO  = B.COIL_NO
				   AND A.STL_NO  = P.STL_NO    
				   AND A.STL_NO  = C.STL_NO 
				   AND C.YD_STK_LYR_MTL_STAT IN('C','U') --적치중, 권상대기
			   */ 
				trtNm = "코일제품이적작업실적";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1031_PIDEV";	
			} else if ("M10YDLMJ1081A".equals(msgId) ) {
				/* 
				SELECT 'M10YDLMJ1081'                         AS MQ_TC_CD           -- IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')    AS MQ_TC_CREATE_DDTT  -- TC생성일시
				     , TS.TRANS_ORD_DATE                      AS TRN_REQ_DATE       -- 운송지시일자
				     , TS.TRANS_ORD_SEQNO                     AS TRN_REQ_SEQ        -- 운송지시순번     
				     , TS.CARD_NO                                                   -- 카드번호   
				     , TS.CAR_NO                                                    -- 차량번호
				     , SUBSTR(TS.YD_CARLD_STOP_LOC,1,1)       AS YD_GP              -- 야드구분
				     , 'H'                                    AS DIST_GOODS_GP      -- 출하제품구분
				     , 'N'                                    AS SCH_YN             -- 스케쥴여부
				     , :V_GOODS_EA                            AS GOODS_EA           -- 제품개수완료시(*)
				     , :V_STL_NO                              AS GOODS_NO           -- 제품번호
				  FROM USRYDA.TB_YD_CARSCH TS
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/ 
				
				trtNm = "코일일품출하상차실적";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1081A_PIDEV";	
			} else if ("M10YDLMJ1071A".equals(msgId) ) {
				/* 
				-- YDDMR007
				SELECT 'M10YDLMJ1071'                          AS MQ_TC_CD            --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')     AS MQ_TC_CREATE_DDTT   --TC생성일시
				     , TS.TRANS_ORD_DATE                       AS TRN_REQ_DATE        --운송지시일자
				     , TS.TRANS_ORD_SEQNO                      AS TRN_REQ_SEQ         --운송지시순번
				     , TS.CAR_NO                                                      --차량번호
				     , SUBSTR(TS.YD_CARLD_STOP_LOC,1,1)        AS YD_GP               --야드구분
				     , 'H'                                     AS DIST_GOODS_GP       --출하제품구분
				     , 'N' 		                              AS SCH_YN              --스케쥴여부
				     , COUNT(*) OVER ()                    AS BD_EA                   --제품개수
				     , TM.STL_NO                           AS BD_NO                   --제품번호
				     , '1'                                 AS SPST_FRTOMOVE_GP
				  FROM (
				        SELECT *
				          FROM TB_YD_CARSCH
				         WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				       ) TS
				     , (
				        SELECT STL_NO
				          FROM TB_YD_CRNWRKMTL
				         WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				       ) TM
				 WHERE 1 = 1  
				 */       				
				trtNm = "출하차량상차개시";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1071A_PIDEV";
			
			}  else if ("M10YDLMJ1091A".equals(msgId) ) {
				/* 
				--출하상차완료 전문조회 
				SELECT 'M10YDLMJ1091'                           AS MQ_TC_CD            --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')      AS MQ_TC_CREATE_DDTT     --TC생성일시
				     , MIN(TS.TRANS_ORD_DATE )                  AS TRN_REQ_DATE     --운송지시일자
				     , MIN(TS.TRANS_ORD_SEQNO)                  AS TRN_REQ_SEQ    --운송지시순번     
				     , 'H'                                      AS DIST_GOODS_GP
				     , 'N'                                      AS SCH_YN
				     , TS.CARD_NO 
				     , TS.CAR_NO                                                     --차량번호
				     , MIN(SUBSTR(TS.YD_CARLD_STOP_LOC,1,1))   AS YD_GP  
				     , TO_CHAR(TS.YD_CARLD_CMPL_DT,'YYYYMMDD') AS CARLD_CMPL_DATE   --상차완료일자
				     , TO_CHAR(TS.YD_CARLD_CMPL_DT,'HH24MISS') AS CARLD_CMPL_TIME   --상차완료시각
				  FROM TB_YD_CARSCH     TS
				 WHERE TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 GROUP BY TS.CARD_NO
				        , TS.CAR_NO
				        , TS.YD_CARLD_CMPL_DT
				 */       				
				trtNm = "출하차량상차완료";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1091A_PIDEV";
			} else if ("M10YDLMJ1095A".equals(msgId) ) {
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1095A_PIDEV 
				SELECT 'M10YDLMJ1095'                        AS MQ_TC_CD              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 	 AS MQ_TC_CREATE_DDTT       --TC생성일시
				     , TRANS_ORD_DATE             			 AS TRN_REQ_DATE
				     , TRANS_ORD_SEQNO                     	 AS TRN_REQ_SEQ
				     , CAR_NO                              	 AS CAR_NO
				-- 	 , CARD_NO                             	 AS CARD_NO               
				     , SUBSTR(YD_CARLD_STOP_LOC,1,1)       	 AS YD_GP
				     , 'H'									 AS DIST_GOODS_GP
				     , ''									 AS YARD_GP     
				     , CASE WHEN YD_CAR_PROG_STAT IN ('3','4','5') 
				            THEN SUBSTR(TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS'),1,8) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS'),1,8) END AS CARLD_CMPL_DATE
				     , CASE WHEN  YD_CAR_PROG_STAT IN ('3','4','5')
				            THEN SUBSTR(TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS'),9,6) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS'),9,6) END AS CARLD_CMPL_TIME        
				  FROM TB_YD_CARSCH C
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 */
				trtNm = "임가공이송상차완료";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1095A_PIDEV";
			} else if("M10YDLMJ1061A".equals(msgId)) {
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1061A_PIDEV
				SELECT 'M10YDLMJ1061'                          AS MQ_TC_CD             --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')     AS MQ_TC_CREATE_DDTT    --TC생성일시
				     , CARD_NO                                                         -- 카트번호
				     , CAR_NO                                                          -- 차량번호      
				     , TRANS_ORD_DATE                          AS TRN_REQ_DATE         -- 운송작업지시일자
				     , TRANS_ORD_SEQNO                         AS TRN_REQ_SEQ          -- 운송작업지시순번
				     , 'J'                                     AS YD_GP
				     , 'H'                                     AS DIST_GOODS_GP
				     , 'N'                                     AS SCH_YN
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')     AS BAYIN_DDTT           -- 입동일시
				     , ARR_WLOC_CD                             AS WLOC_CD              -- 개소코드(착지) 
				     , YD_PNT_CD                                                       -- 야드차량트코드
				     , LOAN_PULLOUT_ABLE_YN                    AS LOAN_PULLOUT_ABLE_YN -- 차입인출가능여부 Y,N,H
				  FROM (SELECT TS.CARD_NO
				             , TS.CAR_NO
				             , TS.TRANS_ORD_DATE
				             , TS.TRANS_ORD_SEQNO
				             , SC.WLOC_CD
				             , SC.YD_PNT_CD
				             , TS.YD_CAR_USE_GP
				             , NVL(TS.YD_BAYIN_WO_SEQ,9) AS YD_BAYIN_WO_SEQ
				             , TS.YD_CAR_SCH_ID
				             , (SELECT YD_CARPNT_CD 
				                  FROM TB_YD_CARPOINT 
				                 WHERE YD_STK_COL_GP = SC.YD_STK_COL_GP
				                   AND DEL_YN = 'N'
				                   AND ROWNUM = 1 
				               ) AS YD_CARPNT_CD 
				             , TS.ARR_WLOC_CD 
				             , :V_LOAN_PULLOUT_ABLE_YN AS LOAN_PULLOUT_ABLE_YN
				          FROM TB_YD_STKCOL SC
				             , TB_YD_CARSCH TS
				         WHERE SC.YD_STK_COL_GP       = :V_YD_STK_COL_GP
				           AND SC.DEL_YN              = 'N'
				           AND SC.YD_STK_COL_ACT_STAT = 'C' --비활성화
				           AND (SC.YD_STKBED_USG_CD IS NULL OR SC.YD_STKBED_USG_CD != 'GT') --출하
				           AND ((TS.YD_CAR_PROG_STAT = '1' AND TS.YD_CARLD_STOP_LOC = SC.YD_STK_COL_GP)
				             OR (TS.YD_CAR_PROG_STAT = 'A' AND TS.YD_CARUD_STOP_LOC = SC.YD_STK_COL_GP))
				           AND TS.DEL_YN             = 'N'
				         ORDER BY YD_BAYIN_WO_SEQ, YD_CAR_SCH_ID)
				 WHERE ROWNUM = 1          --첫번째가
				   AND YD_CAR_USE_GP = 'G' --출하차량   
				*/
				trtNm = "차량입동지시";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1061A_PIDEV";

			} else if("M10YDLMJ1021".equals(msgId)) {
				trtNm = "반납확인 정보";
				/*
                    SELECT  'M10YDLMJ1021'                       AS MQ_TC_CD              -- IF구분코드
                           , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT       -- 전송일시
                           , YD_GP                                                          -- 야드구분
                           , 'H' AS DIST_GOODS_GP                                           -- 출하제품구분
                           , '' AS YARD_GP                                                  -- 출하창고구분
                           , COIL_NO AS GOODS_NO                                            -- 제품번호
                           , NEXT_PROC                                                      -- 다음공정
                           , '*' AS RETURN_CONFIRM_GP                                       -- 반납확인구분
                           , TO_CHAR(SYSDATE,'YYYYMMDD') AS RETURN_TREAT_DATE               -- 반납처리일자
                           , TO_CHAR(SYSDATE,'HH24MISS') AS RETURN_TREAT_TIME               -- 반납처리시간
                           , '' AS RETURN_ETC_ERR                                           -- 반납 기타 ERROR
                           , '' AS RETURN_REAL_SPEC                                         -- 반납 실 규격
                           , COIL_T  AS RETURN_REAL_T                                       -- 반납실 두께
                           , COIL_W  AS RETURN_REAL_W                                       -- 반납실 폭
                           , '' AS RETURN_REAL_LEN                                          -- 반납 실 길이
                           , '' AS RETURN_REAL_GRADE                                        -- 반납 실 등급
                           , COIL_WT AS RETURN_REAL_WT                                      -- 반납실 중량
                           , '' AS RETURN_USAGE_CD                                          -- 반납 용도 CODE
                           , '' AS RETURN_REAL_USAGE_CD                                     -- 반납 실 용도 CODE
                           , '' AS RETURN_REAL_YEOJAE_GP                                    -- 반납 실 여재 구분
                    FROM 
                          TB_PT_COILCOMM
                    WHERE 
                          COIL_NO = :V_STL_NO
				*/
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1021_PIDEV";
			}  else if("M10YDLMJ1051".equals(msgId)) {
				trtNm = "상차완료(야드핸드링)";
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1051_PIDEV 
				SELECT 'M10YDLMJ1051'                      AS MQ_TC_CD              -- IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT     -- TC생성일시
				     , :V_YD_GP                            AS YD_GP                 -- 야드구분     
				     , 'H'                                 AS DIST_GOODS_GP         -- 출하제품구분
				     , :V_CAR_NO                           AS CAR_NO                -- 차량번호     
				     , :V_TRANS_ORD_DT                     AS TRN_REQ_DATE          -- 운송지시일자
				     , :V_TRANS_ORD_SEQNO                  AS TRN_REQ_SEQ           -- 운송지시시각
				     , :V_CMBN_CARLD_YN                    AS CMBN_CARLD_YN         -- 조합상차유무
				     , :V_CARLD_PNT_CD                     AS CARLD_PNT_CD          -- 상차포인트코드
				     , :V_HANDLING_CNT                     AS HANDLING_CNT         -- 핸들링횟수
				     , :V_YD_STK_BED_WHIO_STAT             AS YD_STK_BED_WHIO_STAT  -- 야드적치BED입출고상태
				  FROM DUAL 		
				*/
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1051_PIDEV";

			} else if("M10YDLMJ1071B".equals(msgId)) {
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1071B_PIDEV 
				-- 코일이송 상차개시
				SELECT 
				         'M10YDLMJ1071'                           AS MQ_TC_CD              --IF구분코드
				       , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')      AS MQ_TC_CREATE_DDTT       --TC생성일시		
				       , TRANS_ORD_DATE                           AS TRN_REQ_DATE
				       , TRANS_ORD_SEQNO                          AS TRN_REQ_SEQ
				       , CAR_NO		                                AS CAR_NO
				       , CARD_NO 		                              AS CARD_NO 
				       , SUBSTR(YD_CARLD_STOP_LOC,1,1)            AS YD_GP   
				       , 'H'                                      AS DIST_GOODS_GP
				       , 'Y'                                      AS SCH_YN
				       , CASE WHEN YD_CARLD_ST_DT IS NOT NULL AND LENGTH(TRIM(YD_CARLD_ST_DT)) = 14  
				              THEN SUBSTR(YD_CARLD_ST_DT,1,8)
				              ELSE '' END                     AS CARLOAD_START_DATE   
				       , CASE WHEN YD_CARLD_ST_DT IS NOT NULL AND LENGTH(TRIM(YD_CARLD_ST_DT)) = 14
				              THEN SUBSTR(YD_CARLD_ST_DT,9,6)
				              ELSE '' END                     AS CARLOAD_START_TIME  
				       , (SELECT CR_FRTOMOVE_GP 
				            FROM TB_YD_STOCK
				           WHERE TRANS_ORD_DATE  = A.TRANS_ORD_DATE
				             AND TRANS_ORD_SEQNO = A.TRANS_ORD_SEQNO 
				             AND ROWNUM = 1)                  AS CR_FRTOMOVE_GP   
				  FROM (SELECT A.YD_CAR_SCH_ID              AS YD_CAR_SCH_ID
				             , A.TRN_EQP_CD                 AS TRN_EQP_CD
				             , A.SPOS_WLOC_CD               AS SPOS_WLOC_CD
				             , A.ARR_WLOC_CD                AS ARR_WLOC_CD
				             , A.YD_EQP_WRK_STAT            AS YD_EQP_WRK_STAT
				             , A.YD_CARLD_CMPL_DT           AS YD_CARLD_CMPL_DT
				             , A.YD_CARLD_STOP_LOC          AS YD_CARLD_STOP_LOC
				             , A.CAR_NO                     AS CAR_NO
				             , A.YD_EQP_ID                  AS YD_EQP_ID
				             , TO_CHAR(A.YD_CARLD_ST_DT     , 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT                              
				             , TO_CHAR(A.YD_CARUD_ST_DT     , 'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT
				             , TO_CHAR(A.YD_CARUD_CMPL_DT   , 'YYYYMMDDHH24MISS') AS YD_CARUD_CMPL_DT
				             , A.CARD_NO                    AS CARD_NO                 
				             , A.DEL_YN 
				             , A.TRANS_ORD_DATE
				             , A.TRANS_ORD_SEQNO
				          FROM TB_YD_CARSCH A
				         WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID 
				           AND A.DEL_YN = 'N'
				        )  A 
				 WHERE 1 = 1
				 */
				trtNm = "코일이송 상차개시";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1071B_PIDEV";

			} else if("M10YDLMJ1091B".equals(msgId)) {
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1091B_PIDEV 
				SELECT 'M10YDLMJ1091'                       AS MQ_TC_CD              --IF구분코드
					 , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  AS MQ_TC_CREATE_DDTT       --TC생성일시		
				     , TB.TRANS_ORD_DATE                    AS TRN_REQ_DATE
				     , TB.TRANS_ORD_SEQNO                   AS TRN_REQ_SEQ   	   
				     , TA.CAR_NO		                    AS CAR_NO
				--     , TA.CARD_NO 		                      AS CARD_NO 
				     , substr(TA.YD_CARLD_STOP_LOC,1,1)     AS YD_GP   
				     , 'H'                                  AS DIST_GOODS_GP
				     , 'Y'                                  AS SCH_YN
				     , SUBSTR(TA.YD_CARLD_CMPL_DT,1,8)      AS CARLD_CMPL_DATE   
				     , SUBSTR(TA.YD_CARLD_CMPL_DT,9,6)      AS CARLD_CMPL_TIME  
				     , TB.CR_FRTOMOVE_GP                    AS CR_FRTOMOVE_GP
				  FROM (SELECT A.YD_CAR_SCH_ID      AS YD_CAR_SCH_ID
				             , B.DEL_YN             AS DEL_YN
				             , A.TRN_EQP_CD         AS TRN_EQP_CD
				             , A.SPOS_WLOC_CD       AS SPOS_WLOC_CD
				             , A.ARR_WLOC_CD        AS ARR_WLOC_CD
				             , A.YD_EQP_WRK_STAT    AS YD_EQP_WRK_STAT
				             , B.STL_NO             AS STL_NO
				             , A.YD_CARLD_STOP_LOC  AS YD_CARLD_STOP_LOC
				             , B.YD_CAR_UPP_LOC_CD  AS YD_CAR_UPP_LOC_CD
				             , B.YD_STK_BED_NO      AS YD_STK_BED_NO
				             , B.YD_STK_LYR_NO      AS YD_STK_LYR_NO
				             , A.CAR_NO             AS CAR_NO
				             , A.YD_EQP_ID          AS YD_EQP_ID
				             , NVL(TO_CHAR(A.YD_CARLD_CMPL_DT, 'YYYYMMDDHH24MISS'),:V_WR_DT) AS YD_CARLD_CMPL_DT --상차완료                             
				             , A.CARD_NO            AS CARD_NO                 
				             , A.YD_CARUD_STOP_LOC  AS YD_CARUD_STOP_LOC
				             , A.YD_PNT_CD1
				          FROM TB_YD_CARSCH     A
				             , TB_YD_CARFTMVMTL B
				         WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
				           AND A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				--           AND A.TRANS_ORD_SEQNO < '700000' --20201103 임가공대상재는 YDDMR022 보냈으므로 YDDMR073 보내지 않는다 박주한 책임 요청
				           AND ROWNUM = 1
				        ) TA
				      , TB_YD_STOCK TB
				  WHERE TA.STL_NO = TB.STL_NO
				    AND TA.DEL_YN = 'N'
				    AND TB.CR_FRTOMOVE_GP != '63' --20201103 임가공대상재는 YDDMR022 보냈으므로 YDDMR073 보내지 않는다 박주한 책임 요청/		
				*/
				trtNm = "코일이송 상차완료";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1091B_PIDEV";				
			} else if("M10YDLMJ1081B".equals(msgId)) {
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1081B_PIDEV 
				SELECT 'M10YDLMJ1081'                      AS MQ_TC_CD             -- IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT    -- TC생성일시
				     , TB.TRANS_ORD_DATE                   AS TRN_REQ_DATE
				     , TB.TRANS_ORD_SEQNO                  AS TRN_REQ_SEQ     
				--     , TA.CARD_NO		                   AS CARD_NO
				     , TA.CAR_NO		                   AS CAR_NO
				     , 'J'                                 AS YD_GP
				     , 'H'                                 AS DIST_GOODS_GP
				     , 'Y' AS SCH_YN
				     , TB.STL_NO                           AS GOODS_NO
				     , TA.YD_CAR_SCH_ID                    AS YD_CAR_SCH_ID
				     , TB.CR_FRTOMOVE_GP
				  FROM TB_YD_CARSCH TA
					 , TB_YD_STOCK  TB
				 WHERE TA.TRANS_ORD_DATE  = TB.TRANS_ORD_DATE
				   AND TA.TRANS_ORD_SEQNO = TB.TRANS_ORD_SEQNO
				   AND TB.STL_NO          = :V_STL_NO
				   AND TA.YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
				 */
				trtNm = "코일일품출하상차실적";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1081B_PIDEV";

			} else if("M10YDLMJ1111B".equals(msgId)) {
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1111B_PIDEV
				SELECT 'M10YDLMJ1111'                       AS MQ_TC_CD             --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  AS MQ_TC_CREATE_DDTT    --TC생성일시
				--     , TA.CARD_NO		                    AS CARD_NO
				     , TA.CAR_NO	                        AS CAR_NO
				     , substr(TA.YD_CARUD_STOP_LOC,1,1)     AS YD_GP
				     , 'H'                                  AS DIST_GOODS_GP
				     , ''                                   AS YARD_GP
				     , substr(TA.YD_CARUD_ST_DT,1,8)	    AS CARUD_ST_DATE
				     , substr(TA.YD_CARUD_ST_DT,9,6)	    AS CARUD_ST_TIME
				     , '0'                                  AS GOODS_CNT
				     , TB.TRANS_ORD_DATE                    AS TRN_REQ_DATE
				     , TB.TRANS_ORD_SEQNO                   AS TRN_REQ_SEQ
				     , TB.CR_FRTOMOVE_GP                    AS CR_FRTOMOVE_GP     
				  FROM (SELECT A.YD_CAR_SCH_ID      AS YD_CAR_SCH_ID
				             , B.DEL_YN             AS DEL_YN
				             , A.TRN_EQP_CD         AS TRN_EQP_CD
				             , A.SPOS_WLOC_CD       AS SPOS_WLOC_CD
				             , A.ARR_WLOC_CD        AS ARR_WLOC_CD
				             , A.YD_EQP_WRK_STAT    AS YD_EQP_WRK_STAT
				             , A.YD_EQP_WRK_SH      AS YD_EQP_WRK_SH
				             , B.STL_NO             AS STL_NO
				             , A.YD_CARLD_CMPL_DT   AS YD_CARLD_CMPL_DT
				             , A.YD_CARLD_STOP_LOC  AS YD_CARLD_STOP_LOC
				             , B.YD_CAR_UPP_LOC_CD  AS YD_CAR_UPP_LOC_CD
				             , B.YD_STK_BED_NO      AS YD_STK_BED_NO
				             , B.YD_STK_LYR_NO      AS YD_STK_LYR_NO
				             , A.CAR_NO             AS CAR_NO
				             , A.YD_EQP_ID          AS YD_EQP_ID
				             , NVL(TO_CHAR(A.YD_CARUD_ST_DT ,'YYYYMMDDHH24MISS'),:V_WR_DT) AS YD_CARUD_ST_DT
				             , A.CARD_NO            AS CARD_NO                 
				             , A.YD_CARUD_STOP_LOC  AS YD_CARUD_STOP_LOC
				             , A.YD_PNT_CD1
				          FROM TB_YD_CARSCH A, TB_YD_CARFTMVMTL B
				         WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
				           AND A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				           AND ROWNUM = 1) TA
				      ,TB_YD_STOCK TB
				 WHERE TA.STL_NO = TB.STL_NO
				*/
				trtNm = "코일이송하차개시전송 PDA";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcM10YDLMJ1111B_PIDEV";

							
			
			}
			
			JDTORecordSet jsRst = null;
			
			if(!"".equals(jspeed_query_id)) {
				trtNm = trtNm + "(" + msgId + ") : ";
				
				jsRst = commDao.select(rcvMsg, jspeed_query_id);
					
				commUtils.printLog(logId, trtNm + jsRst.size(), "DB");
				
				//---[JMS IF 로그 조회 시 순서바뀜 현상 수정 추가 시작]-------------------------------------------------------
				JDTORecordSet addData = JDTORecordFactory.getInstance().createRecordSet("");
				String sITM_ID;
				String sITM_VALUE;
				
				if (jsRst.size() > 0) {
					
					JDTORecord jrAdd = JDTORecordFactory.getInstance().create();
//PIDEV				
					JDTORecordSet jsLayOut = null;
								
					if("M10".equals(msgId.substring(0,3))) {
						
						msgId = msgId.substring(0,12);
						
						rcvMsg.setField("IF_ID",msgId);								
						
						jsLayOut = commDao.select(rcvMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getIfTestLayout_PIDEV");
						
					} else {
						
						rcvMsg.setField("IF_ID",msgId);		
						
						jsLayOut = commDao.select(rcvMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getIfTestLayout");	
						
					}
					
					for (int ii = 0; ii < jsRst.size(); ii++) {
						
						for (int jj = 0; jj < jsLayOut.size(); jj++ ) {
							
							sITM_ID = jsLayOut.getRecord(jj).getFieldString("ITM_ID");
							sITM_VALUE = jsRst.getRecord(ii).getFieldString(sITM_ID);
							
							jrAdd.setField(sITM_ID , sITM_VALUE);
						}
						addData.addRecord(jrAdd);
					}
					
					jsRst = JDTORecordFactory.getInstance().createRecordSet("");
					jsRst.addAll(addData);
				}
				//---[JMS IF 로그 조회 시 순서바뀜 현상 수정 추가 종료]-------------------------------------------------------
			}
			
			return jsRst;			
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, trtNm + mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : Sequence ID 조회 
	 *      
	 *      @param String logId
	 *      @param String mthdNm
	 *      @param String trtGp
	 *      @return String
	 *      @throws DAOException
	 *      이현진
	*/
	public String getSeqId(String logId, String mthdNms, String trtGp) throws DAOException {
		String mthdNm = "SeqID조회[CCoilDAO.getSeqId] < " + mthdNms;
		String trtNm = "";

		try {
			
//			commUtils.printLog(logId, mthdNm, "S+");
			
			String jspeed_query_id = "";
			String seqId = ""; //반환할 Sequence ID
  
			if ("CrnSch".equals(trtGp)) {
				trtNm = "야드크레인스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getSeqIdCrnSch";
			} else if ("WrkBook".equals(trtGp)) {
				trtNm = "야드작업예약ID";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getSeqIdWrkBook";
			} else if ("TcarSch".equals(trtGp)) {
				trtNm = "야드대차스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getSeqIdTcarSch";
			} else if ("CarSch".equals(trtGp)) {
				trtNm = "야드차량스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getSeqIdCarSch";
			}  else if ("PrepSch".equals(trtGp)) {
				trtNm = "준비스케쥴번호";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getSeqIdPrepSch";
			}  else if ("ydCarRegSeq".equals(trtGp)) {
				trtNm = "준비스케쥴번호";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getSeqIdydCarRegSeq";
			}  else if ("FtMvWo".equals(trtGp)) {
				trtNm = "이송작업지시번호";
				jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getFrToMoveWordNo";				
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			JDTORecordSet jsRst = getRecordSet(jspeed_query_id, null);

			if (jsRst.size() > 0) {
				seqId = commUtils.trim(jsRst.getRecord(0).getFieldString("SEQ_ID")); //Sequence ID
			}
			
//			commUtils.printLog(logId, mthdNm, "S-");
			return seqId;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, trtNm + mthdNm, e));
		}
	}	
	
	/***************************************************************************
	 * 공통 Check
	 **************************************************************************/
	
	/**
	 *      [A] 오퍼레이션명 :  TB_YD_RULE 조회 여부
	 *      -- 
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public boolean getYdRule(String logId, String mthdNms, String sReprCdGp, String sCdGp,String sItem, JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "getYdRule 조회[CCoilDao.getYdRule] < " + mthdNms;

		boolean bRst    = false;

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			/**********************************************************
			* 1. TB_YD_RULE 조회
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam("", mthdNm, "");
			jrParam.setField("REPR_CD_GP", sReprCdGp  ); //작업구분
			jrParam.setField("CD_GP"     , sCdGp      ); //구분
			jrParam.setField("ITEM"      , sItem      ); //ITEM

			/*
			SELECT CD_GP
			     , ITEM
			     , DEL_YN
			     , REPR_CD_CONTENTS
			     , ITEM1
			     , ITEM_VALUE1
			     , ITEM2
			     , ITEM_VALUE2
			     , DTL_ITEM1
			     , DTL_ITEM2
			     , DTL_ITEM3
			     , DTL_ITEM4
			     , DTL_ITEM5
			     , DTL_ITEM6
			     , DTL_ITEM7
			     , DTL_ITEM8
			     , DTL_ITEM9
			     , DTL_ITEM10
			  FROM TB_YD_RULE
			 WHERE REPR_CD_GP = :V_REPR_CD_GP
			   AND CD_GP   LIKE :V_CD_GP||'%'
			   AND ITEM    LIKE :V_ITEM ||'%'
			   AND DEL_YN     = 'N'
			*/  
			JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdRule", logId, mthdNm, "TB_YD_RULE 조회");
			if (jsRst.size() == 1) {
				rcvMsg.addRecord(jsRst.getRecord(0));
				bRst = true;
			}
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return bRst;

		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return bRst;
		} catch (Exception e) {
			return bRst;
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 :  TB_YD_RULE 조회 여부
	 *      -- 
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public boolean getYdRule(String logId, String mthdNms,String sReprCdGp, String sCdGp,String sItem, JDTORecordSet jsMsg) throws DAOException {
		String mthdNm = "getYdRule 조회[CCommDao.getYdRule] < " + mthdNms;
		boolean bRst    = false;

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			/**********************************************************
			* 1. TB_YD_RULE 조회
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam("", mthdNm, "");
			jrParam.setField("REPR_CD_GP", sReprCdGp  ); //작업구분
			jrParam.setField("CD_GP"     , sCdGp      ); //구분
			jrParam.setField("ITEM"      , sItem      ); //ITEM

			/*
			SELECT CD_GP
			     , ITEM
			     , DEL_YN
			     , REPR_CD_CONTENTS
			     , ITEM1
			     , ITEM_VALUE1
			     , ITEM2
			     , ITEM_VALUE2
			     , DTL_ITEM1
			     , DTL_ITEM2
			     , DTL_ITEM3
			     , DTL_ITEM4
			     , DTL_ITEM5
			     , DTL_ITEM6
			     , DTL_ITEM7
			     , DTL_ITEM8
			     , DTL_ITEM9
			     , DTL_ITEM10
			  FROM TB_YD_RULE
			 WHERE REPR_CD_GP = :V_REPR_CD_GP
			   AND CD_GP   LIKE :V_CD_GP||'%'
			   AND ITEM    LIKE :V_ITEM ||'%'
			   AND DEL_YN     = 'N'
			*/ 
			JDTORecordSet jstmp = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdRule", logId, mthdNm, "TB_YD_RULE 조회");
			if (jstmp.size() > 0) { 
				jsMsg.addAll(jstmp);
				bRst = true;
			}
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return bRst;

		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return bRst;
		} catch (Exception e) {
			return bRst;
		}
	}		
	
	/**
	 *      [A] 오퍼레이션명 :  신규시스템 적용 여부
	 *      -- 
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String ApplyYn(String logId, String mthdNms,String sReprCdGp, String sCdGp,String sItem) throws DAOException {

		String mthdNm = "신규시스템 적용여부[CCoilDao.ApplyYn] < " + mthdNms;
		String szAPPLY_YN = "N";

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			//수신 항목 값
			/**********************************************************
			* 2. 열정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("REPR_CD_GP", sReprCdGp  ); //작업구분
			jrParam.setField("CD_GP"     , sCdGp      ); //구분
			jrParam.setField("ITEM"      , sItem      ); //ITEM

			/* 
			SELECT NVL(MAX(ITEM1),'N') AS APPLY_YN 
			  FROM USRYDA.TB_YD_RULE
			 WHERE REPR_CD_GP = :V_REPR_CD_GP  -- APP001
			   AND CD_GP  = :V_CD_GP            -- CD_GP
			   AND ITEM   = :V_ITEM
			   AND DEL_YN = 'N'
			*/  
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCCoilApplyYn", logId, mthdNm, "열정보 Read"); 

			if (jsChk.size() > 0) {
				szAPPLY_YN    = commUtils.trim(jsChk.getRecord(0).getFieldString("APPLY_YN"));
			}
            
			commUtils.printLog(logId, mthdNm, "S-");

			return szAPPLY_YN;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return szAPPLY_YN;
		} catch (Exception e) {
			return szAPPLY_YN;
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 :  자동화 크레인 CHECK 여부
	 *      -- 
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public boolean chkAutoCrn(String logId, String mthdNms, String ydEqpId) throws DAOException {
		String mthdNm = "자동화 크레인 CHECK [CCoilDao.chkAutoCrn] < " + mthdNms;
		String ydEqpIdGet = "";

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			//수신 항목 값
			/**********************************************************
			* 2. 설비정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("YD_EQP_ID", ydEqpId); //공장구분 2,3

			/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getChkCrnMode2
			SELECT YD_EQP_WRK_MODE2
			  FROM TB_YD_EQP
			 WHERE DEL_YN = 'N'
			   AND YD_EQP_ID = :V_YD_EQP_ID
			*/  
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getChkCrnMode2", logId, mthdNm, "설비정보 조회"); 

			if (jsChk.size() > 0) {
				ydEqpIdGet    = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE2"));
			}

			if ("A".equals(ydEqpIdGet)){ //리모컨은 유인
				commUtils.printLog(logId, mthdNm, "S-");
				return true;
			} else {
				commUtils.printLog(logId, mthdNm, "S-");
				return false;
			}
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return false;
		} catch (Exception e) {
			return false;
		}
	}		
	
	/**
	 *      [A] 오퍼레이션명 : 스케줄코드 Check
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord chkSchCd(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "스케줄코드Check[CCoilDAO.chkSchCd] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			jrRtn.setField("YD_L3_HD_RS_CD", "SC99"); //야드L3처리결과코드(Error)
			jrRtn.setField("YD_L3_MSG"     , "오류:스케줄코드Check 예상치 못한 오류"); //야드L3MESSAGE(40Byte)
			
			//수신 항목 값
			String ydSchCd = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD")); //야드스케쥴코드
			String ydL3HdRsCd = ""; //야드L3처리결과코드
			String ydL3Msg    = ""; //야드L3MESSAGE
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydSchCd)) {
				ydL3HdRsCd = "SC01";
				ydL3Msg = "오류:스케줄코드 없음";
			} else if (ydSchCd.length() < 8) {
				ydL3HdRsCd = "SC02";
				ydL3Msg = "오류:스케줄코드[" + ydSchCd + "] 이상";
			}

			if (!"".equals(ydL3Msg)) {
				jrRtn.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			/**********************************************************
			* 2. 크레인스케줄 상태 Check
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("YD_SCH_CD", ydSchCd); //야드스케쥴코드
 
			//야드스케쥴금지유무 조회
			/* com.inisteel.cim.yd.ccommon.dao.CCommDAO.getYdSchRule 
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
			   AND YD_GP    IN ('H','J')
			   AND YD_SCH_CD = :V_YD_SCH_CD
			   AND DEL_YN    = 'N'
			 
			*/	   
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdSchRule", logId, mthdNm, "야드스케쥴금지유무 조회"); 
			

			String ydSchProhExn = "";  //야드스케쥴금지유무

			if (jsChk.size() > 0) {
				ydSchProhExn  = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PROH_EXN"));
			}

			if ("".equals(ydSchProhExn)) {
				//스케줄기준 Table 정보 Check
				ydL3HdRsCd = "SC03";
				ydL3Msg = "오류:스케쥴코드[" + ydSchCd + "] 정보 없음";
			} else if ("Y".equals(ydSchProhExn)) {
				//스케줄 금지여부 Check
				ydL3HdRsCd = "SC04";
				ydL3Msg = "오류:스케쥴코드[" + ydSchCd + "] 기동금지";
			}
			
			if (!"".equals(ydL3Msg)) {
				jrRtn.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			jrRtn.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드
			jrRtn.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE

			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return jrRtn;
		} catch (Exception e) {
			return jrRtn;
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 설비상태 Check
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord chkEqpStat(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "설비상태Check[CCoilDAO.chkEqpStat] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			jrRtn.setField("YD_L3_HD_RS_CD", "EQ99"); //야드L3처리결과코드(Error)
			jrRtn.setField("YD_L3_MSG"     , "오류:설비상태Check 예상치 못한 오류"); //야드L3MESSAGE(40Byte)

			//수신 항목 값
			String ydEqpId	  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //야드설비ID
			String ydL3HdRsCd = ""; //야드L3처리결과코드
			String ydL3Msg    = ""; //야드L3MESSAGE
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "EQ01";
				ydL3Msg = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "EQ02";
				ydL3Msg = "오류:설비ID[" + ydEqpId + "] 이상";
			}

			if (!"".equals(ydL3Msg)) {
				jrRtn.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			/**********************************************************
			* 2. 설비상태 Check
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("YD_EQP_ID", ydEqpId); //야드설비ID

			/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getStatEqp 
			--설비상태조회 
			SELECT YD_EQP_STAT     AS YD_EQP_STAT
			     , YD_EQP_WRK_MODE AS YD_EQP_WRK_MODE
			     , YD_CURR_BAY_GP
			  FROM TB_YD_EQP EQ
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			   AND DEL_YN    = 'N' 
			*/	   
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getStatEqp", logId, mthdNm, "설비상태 Check"); 

			String ydEqpStat     = ""; //야드설비상태
			String ydEqpWrkMode  = ""; //야드설비작업Mode

			if (jsChk.size() > 0) {
				ydEqpStat    = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT"    ));
				ydEqpWrkMode = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE"));
			}

			if ("".equals(ydEqpStat)) {
				//설비 Table 정보 Check
				ydL3HdRsCd = "EQ03";
				ydL3Msg = "오류:크레인[" + ydEqpId + "] 정보 없음";
			} else if ("B".equals(ydEqpStat)) {
				//설비 Table 설비상태 Check
				ydL3HdRsCd = "EQ04";
				ydL3Msg = "오류:크레인[" + ydEqpId + "] 고장";
			} else if (!"1".equals(ydEqpWrkMode)) {
				//설비 Table 설비작업Mode Check
				ydL3HdRsCd = "EQ05";
				ydL3Msg = "오류:크레인[" + ydEqpId + "] Off-Line";
			}
			
			if (!"".equals(ydL3Msg)) {
				jrRtn.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			jrRtn.setField("YD_L3_HD_RS_CD"	, "0000"); //야드L3처리결과코드
			jrRtn.setField("YD_L3_MSG"     	, ""    ); //야드L3MESSAGE
			jrRtn.setField("YD_EQP_STAT"   	, ydEqpStat);
			jrRtn.setField("YD_EQP_WRK_MODE", ydEqpWrkMode);
			
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
//			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return jrRtn;
		} catch (Exception e) {
			return jrRtn;
		}
	}	

	
	/**
	 * 오퍼레이션명 : 설비상태 체크(H/J)eqpStatCheck
	 *  
	 * @param   String szEqpId 설비ID
	 * @return boolean true(설비사용가능), false(설비사용불가)
	 * @throws JDTOException
	 */
	public boolean chkEqpStat(String logId, String mthdNms, String ydEqpId) throws JDTOException  {
		String mthdNm = "설비상태 체크[CCoilWrkBookSeEJB.chkEqpStat] < " + mthdNms;
		//리턴값(boolean)
		boolean blnRtnVal = false;
		
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");
			//설비상태
			JDTORecordSet jsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//설비 체크 및 데이터 조회
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("YD_EQP_ID", ydEqpId);
			
			//설비 테이블 조회
			jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdEqp", logId, mthdNm, "설비정보 조회");

			//리턴값 메세지처리
			if (jsResult.size() > 1) {
//				sMsg = "설비ID(" + ydEqpId + ")에 대한 설비 데이터가 중복되었습니다.";
				blnRtnVal = false;
			} else if (jsResult.size() == 1) {
				blnRtnVal = true;
			} else if (jsResult.size() == 0) {
//				sMsg = "설비ID(" + ydEqpId + ")에 대한 설비 데이터가 없습니다.";
				blnRtnVal = false;
			} else {
//				sMsg = "설비ID(" + ydEqpId + ")로 설비 조회중 오류 발생!";
				blnRtnVal = false;
			}
			
			//레코드 추출
			jsResult.first();
			JDTORecord jrResult = jsResult.getRecord();
			commUtils.printLog(logId, mthdNm+"blnRtnVal4:" + commUtils.trim(jrResult.getFieldString("YD_EQP_STAT")), "SL");

			//설비상태
			String ydEqpStat = commUtils.trim(jrResult.getFieldString("YD_EQP_STAT"));
			
			commUtils.printLog(logId, "ydEqpStat:" + ydEqpStat, "SL");
			
			//크레인의 상태가 'T'이면 false 리턴.
			if (ydEqpStat.equals(CConstant.YD_EQP_STAT_BREAK)) {
//				sMsg = "설비ID(" + ydEqpId + ")의 상태가 고장(" + ydEqpStat + ") 입니다.";
				blnRtnVal = false;
			} else {
				blnRtnVal = true;
			}
			
			commUtils.printLog(logId, mthdNm, "S-");
		} catch(Exception e) {
//			sMsg = "설비상태 체크 중 예외발생! 예외메세지: " + e.getMessage();
			blnRtnVal = false;
		}
		return blnRtnVal;
	} 	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 기동 조회
	 *
	 *      @param String JDTORecord rcvMsg
	 *      @return String JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getCrnSchMsg(JDTORecord rcvMsg) throws DAOException {
 		/***************************************************************************
		 * 스케줄 기동시 사용: procCrnWrkBookMgtStart
		 **************************************************************************/
		
		String mthdNm = "크레인스케줄전문조회[CCoilDAO.getCrnSchMsg] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");

			//Return Value
			String currDate   = commUtils.getDateTime14();									//현재시각
			String ydGp       = commUtils.trim(rcvMsg.getFieldString("YD_GP"        ));	//야드구분
			String ydWbookId  = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"  ));	//야드작업예약ID
			String ydSchCd    = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"    ));	//야드스케쥴코드
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    ));	//야드설비ID
			String ydSchStGp  = commUtils.trim(rcvMsg.getFieldString("YD_SCH_ST_GP" ));	//야드스케쥴기동구분
			String ydSchReqGp = commUtils.trim(rcvMsg.getFieldString("YD_SCH_REQ_GP"));	//야드스케쥴요청구분
			String sModifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     ));	//수정자
			String ejbCallYn  = commUtils.trim(rcvMsg.getFieldString("EJB_CALL_YN"  ));	//EJBCall여부(신 크레인스케줄)

			if ("".equals(ydWbookId) && "".equals(ydSchCd) && "".equals(ydEqpId)) {
				if ("Y".equals(ejbCallYn)) {
					throw new Exception("크레인스케줄 기동을 위한 정보가 없습니다.");
				} else {
					commUtils.printLog(logId, "크레인스케줄 기동을 위한 정보가 없습니다.", "SL");
					return null;
				}
			}

			//크레인스케줄기동구분 조회
			if (!"".equals(ydWbookId) && ("".equals(ydSchCd) || "".equals(ydEqpId))) {
				rcvMsg.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCrnSchStartGp 
				--크레인스케줄기동구분조회 
				SELECT WB.YD_GP
				      ,WB.YD_SCH_CD
				      ,(SELECT SR.YD_WRK_CRN
				         FROM TB_YD_SCHRULE SR
				         WHERE SR.YD_SCH_CD = WB.YD_SCH_CD) AS YD_EQP_ID
				  FROM TB_YD_WRKBOOK WB
				 WHERE WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND WB.DEL_YN      = 'N'
				*/	   
				JDTORecordSet jsChk = commDao.select(rcvMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCrnSchStartGp", logId, mthdNm, "크레인스케줄기동구분 조회");

				if (jsChk.size() > 0) {
					ydGp       = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_GP"        ));	//야드구분
					ydSchCd    = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_CD"    ));	//야드스케쥴코드
					ydEqpId    = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"    ));	//야드설비ID
				} else {
					if ("Y".equals(ejbCallYn)) {
						throw new Exception("작업예약ID[" + ydWbookId + "]의 정보가 없어 크레인스케줄을 기동할 수 없습니다.");
					} else {
						commUtils.printLog(logId, "작업예약ID[" + ydWbookId + "]의 정보가 없어 크레인스케줄을 기동할 수 없습니다.", "SL");
						return null;
					}
				}
			} else {
				if ("".equals(ydGp)) {
					if (!"".equals(ydSchCd)) {
						ydGp = ydSchCd.substring(0, 1);
					} else if (!"".equals(ydEqpId)) {
						ydGp = ydEqpId.substring(0, 1);
					}
				}

				rcvMsg.setField("YD_GP", ydGp); //야드구분
			}


			commUtils.printLog(logId, "[작업예약ID:" + ydWbookId + ", 스케쥴코드:" + ydSchCd + ", 설비ID:" + ydEqpId + "]", "SL");

			//크레인스케줄 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, rcvMsg.getResultMsg(), sModifier);
			
			jrYdMsg.setField("JMS_TC_CD"		 , "YDYDJ551"); //
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate  ); //JMSTC생성일시
			jrYdMsg.setField("YD_WBOOK_ID"       , ydWbookId ); //야드작업예약ID
			jrYdMsg.setField("YD_SCH_CD"         , ydSchCd   ); //야드스케쥴코드
			jrYdMsg.setField("YD_EQP_ID"         , ydEqpId   ); //야드설비ID
			jrYdMsg.setField("YD_SCH_ST_GP"      , ydSchStGp ); //야드스케쥴기동구분
			jrYdMsg.setField("YD_SCH_REQ_GP"     , ydSchReqGp); //야드스케쥴요청구분

			jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 진도코드 get
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getCoilCurrProgCd(JDTORecord rcvMsg) throws DAOException {	
		String mthdNm = "진도코드Check[CCoilDAO.getCoilCurrProgCd] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
//PIDEV
			String sPI_YD     = commUtils.nvl(rcvMsg.getFieldString("PI_YD"),"*");
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "getYdAimRtGp => 진도코드 get 지정", "APPPI0", sPI_YD, "*");
			
			if("PIDEV".equals("PIDEV")) {
				jrRtn = this.getCoilCurrProgCd_PIDEV(rcvMsg);
				return jrRtn;
				
			}			
			
			//수신 항목 값
			String msgId     = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String TcCode 	 = commUtils.trim(rcvMsg.getFieldString("TC_CD"));	//TC_CD
			String sStlNo 	 = commUtils.trim(rcvMsg.getFieldString("STOCK_ID"));//재료
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }

			/**********************************************************
			* 2. COILCOMM READ
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);

			jrParam.setField("COIL_NO"	, sStlNo); //충당재료
			jrParam.setField("MODIFIER" , sModifier); //수정자

			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilComByCurrProgCd 
			SELECT DECODE(CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',CURR_PROG_CD) AS CURR_PROG_CD
		     	 , RETURN_GP  --반납구분
		      FROM USRPTA.TB_PT_COILCOMM 
		     WHERE COIL_NO = :V_COIL_NO   -- 재료번호
		 	*/
			JDTORecordSet jsStl = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilComByCurrProgCd", logId, mthdNm, "CoilComm 조회");
			
			String ydStocMv = "";
			
			if (jsStl != null && jsStl.size() > 0) {
				String CurrProgCd 	= commUtils.trim(jsStl.getRecord(0).getFieldString("CURR_PROG_CD"));//진도코드
				String ReturnGp 	= commUtils.trim(jsStl.getRecord(0).getFieldString("RETURN_GP")); 	//반납구분
			   	
		    	if(CConstant.DMYDR008.equals(TcCode)){			//코일제품반납대기
		    		if(CConstant.RETURN_GP_1.equals(ReturnGp)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_JR;
		    		}else{
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_JG;
		    		}
		    	}else if(CConstant.DMYDR005.equals(TcCode)||			//코일제품출하지시대기 
		    			 CConstant.DMYDR004.equals(TcCode)|| 			//외판슬라브출하지시대기
		    			 CConstant.DMYDR033.equals(TcCode)){			//코일제품반품
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_KG;
		    		
		    	}else if(CConstant.DMYDR027.equals(TcCode)||			//코일제품보관지시 
		    			 CConstant.DMYDR030.equals(TcCode)){			//코일제품출하완료
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_MG;
		    		
		    	}else if(CConstant.DMYDR016.equals(TcCode)){			//외판슬라브운송지시대기
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_NG;
		    		
		    	}else if(CConstant.DMYDR060.equals(TcCode)||			//코일제품운송지시
		    			 CConstant.DMYDR022.equals(TcCode) ){			//외판슬라브운송상차지시
		    		
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_LG;
		    	}else if(CConstant.CURR_PROG_CD_COIL_A.equals(CurrProgCd)||
		    			 CConstant.CURR_PROG_CD_COIL_R.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_AC;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_B.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_BC;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_C.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_CC;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_D.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_DC;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_E.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_CS;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_F.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_FC;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_K.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_KG;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_G.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_GC;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_H.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_HG;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_J.equals(CurrProgCd)){
		    		if(CConstant.RETURN_GP_1.equals(ReturnGp)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_JR;
		    		}else{
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_JG;
		    		}
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_L.equals(CurrProgCd)){//코일제품상차지시 
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_LG;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_N.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_NG;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_M.equals(CurrProgCd)||
		    			 CConstant.CURR_PROG_CD_COIL_P.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_MG;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_X.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_XG;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_Y.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_YG;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_Z.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_ZG;
		    	}															
		    	
		    	jrRtn.setResultCode(logId);	//Log ID
		    	jrRtn.setResultMsg(mthdNm);	//Log Method Name
//		    	jrRtn.setField("STOCK_ID"		, StlNo); //충당재료
//		    	jrRtn.setField("MODIFIER" 		, modifier); //수정자
		    	jrRtn.setField("CURR_PROG_CD"  	, CurrProgCd); 	//진도코드
		    	jrRtn.setField("STOCK_MOVE_TERM", ydStocMv  );	//저장품 이동 조건
			} 

			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return jrRtn;
		} catch (Exception e) {
			return jrRtn;
		}
	}	
	

	/**
	 *      [A] 오퍼레이션명 : 야드적치구분(검색조건 행선)
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String getCoilYdRouteGp(String logId , String mthdNms, String ydSchCd, String sStlNo) throws DAOException {
		String mthdNm = "야드적치구분[CCoilDAO.getCoilYdRouteGp] < " + mthdNms;
		String ydRouteGp = "";
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
			//수신 항목 값
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("YD_SCH_CD", ydSchCd); 
			jrParam.setField("STL_NO"   , sStlNo); 

			/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getCoilYdRouteGp_PIDEV
			WITH DATA_TBL AS
			(
			SELECT A.ORD_YEOJAE_GP
			     , A.ITEMNAME_CD
			     -- 인도조건
			     , CASE WHEN A.ORD_YEOJAE_GP = '2' THEN ''
			       --PIDEV
			       --ELSE SUBSTR(C.DELIVER_TERM_CD,1,1) END AS DELIVER_TERM_CD
			         WHEN (SUBSTR(C.DELIVER_TERM_CD,1,1) = '1') OR (SUBSTR(C.DELIVER_TERM_CD,1,1) = '2') THEN
			           SUBSTR(C.DELIVER_TERM_CD,1,1)
			         ELSE
			           C.DELIVER_TERM_CD
			       END AS DELIVER_TERM_CD
			     --포장방법
			     , DECODE(A.CURR_PROG_CD,'F','XX',C.WRAP_METHOD_CD) AS WRAP_METHOD_CD
			     -- 용도
			     , (CASE WHEN A.ORD_NO LIKE 'G%' AND A.CURR_PROG_CD='H' THEN 'F'
			             WHEN A.ITEMNAME_CD IN ('HAP','HBP','HCP','HAS','HBS','HCS','HAT','HBT','HCT','HCJ') THEN 'F' ELSE 'Y' END) AS USAGE_CD
			     , A.ORD_NO
			     , A.ORD_DTL
			  FROM USRPTA.TB_PT_COILCOMM A
			     , TB_YD_STOCK    B 
			     , USRPTA.TB_PT_OSCOMM   C             
			 WHERE A.COIL_NO = B.STL_NO
			   AND A.COIL_NO = :V_STL_NO
			   AND A.ORD_NO  = C.ORD_NO(+)
			   AND A.ORD_DTL = C.ORD_DTL(+)
			)
			
			--YD_ROUTE_GP:'*'-수입및일반입고
			SELECT     --소재장
			       CASE WHEN SUBSTR(TP.YD_SCH_CD,8,1) = 'H' AND APP_YN = 'N' THEN
			                 ( 
			                   SELECT CASE --지포장 보급
			                               WHEN TP.YD_SCH_CD IN ('JBGF01UH','JCGF01UH','JEGF01UH','JFGF01UH','JHGF01UH')    THEN 'G0'
			                               --C동반입 HFL재(B3), SPM재(B4)
			                               WHEN TP.YD_SCH_CD IN ('JCPT01LH','JCPT02LH') THEN DECODE(CC.NEXT_PROC,'CH','B3','B4') 
			                               ELSE (SELECT YD_AIM_RT_GP FROM TB_YD_STOCK WHERE STL_NO = CC.COIL_NO) 
			
			                          END
			                     FROM TB_PT_COILCOMM CC
			                    WHERE CC.COIL_NO = :V_STL_NO 
			                 ) 
			             -- 소재 신 검색조건     
			            WHEN SUBSTR(TP.YD_SCH_CD,8,1) = 'H' AND APP_YN = 'Y' THEN
			                 ( 
			                   SELECT CASE WHEN TP.YD_SCH_CD IN ('JBGF01UH','JCGF01UH','JEGF01UH','JFGF01UH','JHGF01UH')  THEN 'G0'
			                               --C동반입 HFL재(B3), SPM재(B4)
			                               WHEN TP.YD_SCH_CD LIKE 'J_TR13LH' THEN SUBSTR(TP.YD_SCH_CD,2,1)||SUBSTR(CC.NEXT_PROC,2,1) 
			                               WHEN TP.YD_SCH_CD IN ('JCPT01LH','JCPT02LH') THEN DECODE(CC.NEXT_PROC,'CH','B3','B4') 
			--                               WHEN SUBSTR(CC.NEXT_PROC,1,1) = SUBSTR(TP.YD_SCH_CD,2,1) 
			--                                     AND SUBSTR(CC.NEXT_PROC,2,1) IN ('A','H','K','R') THEN CC.NEXT_PROC
			                               -- RULE 에 포함 안된것은 '_Z'처리
			                               WHEN TP.YD_SCH_CD LIKE ('J_CV0_LH') THEN '*'
			                               WHEN SUBSTR(CC.NEXT_PROC,1,1) = SUBSTR(TP.YD_SCH_CD,2,1) 
			                                     AND SUBSTR(CC.NEXT_PROC,2,1) IN ('H','K','R') THEN 
			                                              CASE WHEN CC.NEXT_PROC IN ( SELECT NVL(DTL_ITEM2,'*') FROM TB_YD_RULE
			                                                                           WHERE REPR_CD_GP  = 'APP007'
			                                                                             AND DEL_YN ='N'
			                                                                            ) THEN CC.NEXT_PROC
			                                                   ELSE SUBSTR(TP.YD_SCH_CD,2,1) ||'Z' 
			                                                   END                             
			                               -- 공냉재
			                               WHEN SUBSTR(CC.NEXT_PROC,2,1) = 'A'
			                                 OR EXISTS (SELECT 1
			                                              FROM TB_HR_C_SHEARWOWR SR
			                                             WHERE SR.COIL_NO      = CC.COIL_NO
			                                               AND SR.HR_PLNT_GP   = 'C'
			                                               AND SR.WORK_STAT    = '*'
			                                               AND SR.WORD_PROC LIKE '%A'
			                                               AND SR.RECEIPT_HOLD_SCRAP_CAUSE_GP IN ('I', 'B')
			                                               AND SR.STEP_NO = (SELECT MAX(STEP_NO) FROM TB_HR_C_SHEARWOWR WHERE COIL_NO = SR.COIL_NO)
			                                           ) THEN SUBSTR(TP.YD_SCH_CD,2,1) ||'A'
			                               ELSE SUBSTR(TP.YD_SCH_CD,2,1) ||'Z'
			                               END  NEXT_PROC
			                      FROM TB_PT_COILCOMM CC
			                     WHERE CC.COIL_NO = :V_STL_NO 
			                 )      
			           --제품장           
			            ELSE 
			              NVL(( 
			                   SELECT CASE WHEN TP.YD_SCH_CD IN ('JAKD01LM','JBKD01LM','JCKD01LM','JHKD01LM','JEKD01LM'
			                                                    ,'JCFD01LM','JGFD01LM') THEN '*'
			                               WHEN ST.YD_AIM_RT_GP  = 'F3'                 THEN 'F0'
			                               WHEN TP.YD_SCH_CD IN ( 'JBGF01LM','JCGF01LM','JEGF01LM','JFGF01LM','JHGF01LM') THEN 'G0'
			                               WHEN TP.YD_SCH_CD IN ( 'JAKD01LM'                                 ,'JATC05MM'
			                                                     ,'JBKD01LM'           ,'JBTC01MM','JBTC02MM','JBTC05MM'
			                                                     ,'JCKD01LM','JCFD01LM','JCTC01MM','JCTC02MM','JCTC05MM'
			                                                     ,'JDFD01LM','JDTC01MM','JDTC02MM'
			                                                     ,'JEKD01LM','JETC01MM','JETC02MM'
			                                                     ,'JFFD01LM','JFTC01MM','JFTC02MM'
			                                                     ,'JGFD01LM','JGTC01MM','JGTC02MM'
			                                                     ,'JHKD01LM'           ,'JHTC01MM','JHTC02MM') 
			                                                 AND WRAP_METHOD_CD = 'EB' AND USAGE_CD NOT IN ('F') THEN 'G0'
			                               ELSE 
			                                    (SELECT A.CD_GP AS CODE
			                                       FROM VW_YD_YDB700 A
			                                      WHERE A.ORD_YEOJAE_GP LIKE NVL(BT.ORD_YEOJAE_GP,'') ||'%' 
			                                        AND A.ITM_NM        LIKE NVL(BT.DELIVER_TERM_CD ,'') ||'%' 
			                                        AND ((BT.ORD_YEOJAE_GP IS NOT NULL AND (A.CD_GP <> 'F0') AND (A.CD_GP <> 'G0'))
			                                             OR ((BT.ORD_YEOJAE_GP IS NULL) )
			                                            )
			                                        AND ROWNUM = 1    
			                                    )        
			                          END
			                     FROM TB_YD_STOCK  ST  
			                        , DATA_TBL     BT
			                    WHERE ST.STL_NO = :V_STL_NO   
			                 ),'A1') 
			            
			       END 
			       AS YD_ROUTE_GP
			  FROM 
			       ( SELECT :V_YD_SCH_CD AS YD_SCH_CD 
			              , NVL((SELECT ITEM1 
			                       FROM TB_YD_RULE 
			                      WHERE REPR_CD_GP = 'APP005'
			                        AND CD_GP = 'J' AND ITEM = '*' AND DEL_YN = 'N'),'N') AS APP_YN 
			           FROM DUAL)  TP  
			*/
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getCoilYdRouteGp_PIDEV", logId, mthdNm, "적치구분(행선)조회"); 

			if (jsChk.size() > 0) {
				ydRouteGp 	= commUtils.trim(jsChk.getRecord(0).getFieldString("YD_ROUTE_GP"));
			}
			
			commUtils.printLog(logId, mthdNm, "S-");

			return ydRouteGp;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), commUtils, e);
			return ydRouteGp;
		} catch (Exception e) {
			return ydRouteGp;
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 고정스키드 CHECK(chkFixedSkid)
	 * 
	 * @param String szYdStkColGp
	 * @return boolean     			// 고정스키드(F)일때 true
	 */	
	public boolean chkFixedSkid(String logId, String mthdNms, String ydStkColGp) throws DAOException {
		String mthdNm = "고정스키드 CHECK [CCoilDao.chkFixedSkid] < " + mthdNms;
		
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");
			
			JDTORecord	jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("YD_STK_COL_GP"	, ydStkColGp);
			
			if(!"".equals(ydStkColGp) && !(ydStkColGp == null)){  
				/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.ChkStkLyrCD
				SELECT YD_STK_SKID_GP
				  FROM TB_YD_STKCOL
				 WHERE DEL_YN = 'N'
				   AND YD_STK_COL_GP = :V_YD_STK_COL_GP
				*/   
				JDTORecordSet jsInTemp = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.ChkStkLyrCD", logId, mthdNm, "작업예약 조회");
				jsInTemp.absolute(1);
 				JDTORecord jrInTemp = jsInTemp.getRecord();
				
				String ydStkColGpGet    = commUtils.trim(jrInTemp.getFieldString("YD_STK_SKID_GP"));
				
				if("F".equals(ydStkColGpGet)){
					commUtils.printLog(logId, mthdNm, "S-");
					return true;
				}else{
					commUtils.printLog(logId, mthdNm, "S-");
					return false;
				}
			}else{
				commUtils.printLog(logId, mthdNm, "S-");
				return false;
			}
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}



	/**
	 * 오퍼레이션명 : 크레인설비상태체크(checkCrnStat)
	 * YYS 2019-08-30
	 * @param szEqpId
	 * @param recOut
	 * @return
	 * @throws JDTOException
	 */
	public String chkCrnStat(String logId, String mthdNms, String ydEqpId) throws JDTOException  {
		String mthdNm = "크레인설비상태체크 [CCoilDao.chkCrnStat] < " + mthdNms;
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");	
			String  rtnMsg = "";
			//설비ID
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("YD_EQP_ID", ydEqpId);
			
			//설비 테이블 조회
			JDTORecordSet jsYdEpq = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdEqp", logId, mthdNm, "설비정보 조회");

			if( jsYdEpq.size() == 0 ) {
				rtnMsg = CConstant.RETN_CD_NOTEXIST;
			} else if( jsYdEpq.size() == -2 ) {
				rtnMsg = CConstant.RETN_CD_NO_PARAM;
			} else if( jsYdEpq.size() < 0 ) {
				rtnMsg = CConstant.RETN_CD_FAILURE;
			} else {
				rtnMsg = CConstant.RETN_CD_SUCCESS;
			}
			
			if( !rtnMsg.equals(CConstant.RETN_CD_SUCCESS) ) {
				return rtnMsg;
			} else if(  jsYdEpq.size() > 1 ) {
				return CConstant.RETN_CD_DUPLICATE;
			} else {
				jsYdEpq.first();
				JDTORecord jrYdEpq = jsYdEpq.getRecord();
				
				String ydEqpStat	= commUtils.trim(jrYdEpq.getFieldString("YD_EQP_STAT")); 
				String ydEqpWrkMode	= commUtils.trim(jrYdEpq.getFieldString("YD_EQP_WRK_MODE"));
				
				if( ydEqpStat.equals(CConstant.YD_EQP_STAT_BREAK) )	{
					return CConstant.YD_EQP_STAT_BREAK;
				} else if( ydEqpWrkMode.equals(CConstant.YD_EQP_WRK_MODE_OFF_LINE) )	{
					return CConstant.YD_EQP_WRK_MODE_OFF_LINE;
				}
				
			}
			commUtils.printLog(logId, mthdNm, "S-");
			return CConstant.RETN_CD_SUCCESS;
			
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
	} 

	
	/**
	 * 오퍼레이션명 : 재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환
	 *  YYS 2019-08-30
	 * @param  String        szStlNo   재료번호
	 *         String        szMtlStat 적치단재료상태
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetStlStkLyr(String logId, String mthdNms, String sStlNo, String sMtlStat, JDTORecordSet jsResult) throws JDTOException  {
		String mthdNm = "크레인설비상태체크 [CCoilDao.chkGetStlStkLyr] < " + mthdNms;
		//적치단
		boolean blnRtnVal   = false;
		String sMsg = "";
		try {
			commUtils.printLog(logId, mthdNm, "S+");	
			
			
			//조회 항목  record 생성
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");

			//조회 파라미터 레코드 set
			jrParam.setField("STL_NO",              sStlNo);
			jrParam.setField("YD_STK_LYR_MTL_STAT", sMtlStat);
			
			//적치단정보 조회
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO*/
			//intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 3);
			/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdStklyrSTLNO
			WITH TEMP_TABLE AS (
			SELECT  :V_STL_NO  AS STL_NO
			      , :V_YD_STK_LYR_MTL_STAT AS YD_STK_LYR_MTL_STAT
			  FROM DUAL
			)
			SELECT *
			 FROM (
			        SELECT YD_STK_COL_GP            AS YD_STK_COL_GP
			              ,YD_STK_BED_NO            AS YD_STK_BED_NO
			              ,YD_STK_LYR_NO            AS YD_STK_LYR_NO
			              ,REGISTER                 AS REGISTER
			              ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
			              ,MODIFIER                 AS MODIFIER
			              ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
			              ,DEL_YN                   AS DEL_YN
			              ,STL_NO                   AS STL_NO
			              ,YD_STK_LYR_ACT_STAT      AS YD_STK_LYR_ACT_STAT
			              ,YD_STK_LYR_MTL_STAT      AS YD_STK_LYR_MTL_STAT
			              ,YD_STK_LYR_XAXIS         AS YD_STK_LYR_XAXIS
			              ,YD_STK_LYR_YAXIS         AS YD_STK_LYR_YAXIS
			              ,YD_STK_LYR_ZAXIS         AS YD_STK_LYR_ZAXIS
			          FROM TB_YD_STKLYR
			         WHERE STL_NO = (SELECT STL_NO FROM TEMP_TABLE)
			           AND NVL(YD_STK_LYR_MTL_STAT, '*') LIKE (SELECT YD_STK_LYR_MTL_STAT FROM TEMP_TABLE) || '%'
			           AND DEL_YN='N'
			         UNION ALL
			         SELECT STACK_COL_GP            AS YD_STK_COL_GP
			              ,STACK_BED_GP            AS YD_STK_BED_NO
			              ,STACK_LAYER_GP            AS YD_STK_LYR_NO
			              ,REGISTER                 AS REGISTER
			              ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
			              ,MODIFIER                 AS MODIFIER
			              ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
			              ,DEL_YN                   AS DEL_YN
			              ,STOCK_ID                   AS STL_NO
			              ,STACK_LAYER_ACTIVE_STAT      AS YD_STK_LYR_ACT_STAT
			              ,STACK_LAYER_STAT      AS YD_STK_LYR_MTL_STAT
			              ,STACK_LAYER_X_AXIS         AS YD_STK_LYR_XAXIS
			              ,STACK_LAYER_Y_AXIS         AS YD_STK_LYR_YAXIS
			              ,STACK_LAYER_Z_AXIS         AS YD_STK_LYR_ZAXIS
			          FROM TB_YM_STACKLAYER
			         WHERE STOCK_ID = (SELECT STL_NO FROM TEMP_TABLE)
			--           AND NVL(STACK_LAYER_STAT, '*') LIKE (SELECT DECODE(YD_STK_LYR_MTL_STAT,'C','L',YD_STK_LYR_MTL_STAT) FROM TEMP_TABLE) || '%'
			           AND NVL(STACK_LAYER_STAT, '*') LIKE (SELECT YD_STK_LYR_MTL_STAT FROM TEMP_TABLE) || '%'
			           AND DEL_YN='N'
			           AND SUBSTR(STACK_COL_GP,1,1) IN ('0','1','2','3')
			           AND SUBSTR(STACK_COL_GP,3,2) BETWEEN '00' AND '99'
			         ) A
			 ORDER BY YD_STK_COL_GP
			 */
			jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdStklyrSTLNO", logId, mthdNm, "작업예약 조회");
			
			//리턴값 메세지처리
			if (jsResult.size() > 1) {
				
				sMsg = "재료번호("      + sStlNo   + ")," + "적치단재료상태(" + sMtlStat + ")," + " 에 대한 적치단 데이터가 중복되었습니다.";
				commUtils.printLog(logId, mthdNm, "S-");
				blnRtnVal = false;
				
			} else if (jsResult.size() == 1) {
				blnRtnVal = true;
				
			} else if (jsResult.size() == 0) {
				sMsg = "재료번호(" + sStlNo + ")," + "적치단재료상태(" + sMtlStat + ")," + " 에 대한 적치단 데이터가 없습니다.";
				commUtils.printLog(logId, mthdNm, "S-");
				blnRtnVal = false;
				
			} else if (jsResult.size() == -2) {
				sMsg = "재료번호(" + sStlNo + ")," + "적치단재료상태(" + sMtlStat + ")," + " 로 적치단 조회중 parameter error 발생!";
				commUtils.printLog(logId, mthdNm, "S-");
				blnRtnVal = false;
				
			} else {
				sMsg = "재료번호(" + sStlNo + ")," + "적치단재료상태(" + sMtlStat + ")," + " 로 적치단 조회중 오류 발생!";
				commUtils.printLog(logId, mthdNm, "S-");
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			sMsg = "재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환 중 Error : " + e.getMessage();
			commUtils.printLog(logId, mthdNm, "S-");
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetStlStkLyr

	
	
	/**
	 * 대차작업지정기준조회1
	 * YYS 2018-08-30
	 * @param szYD_EQP_ID			대차호기
	 * @return String[]
	 */
	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr) {
		return setWiseGridCombo(obj, hTitle, comboStrArr, 1, "N");
	}
	
	/**
 	 * 대차작업지정기준조회_코일1
	 * @param szYD_EQP_ID			대차호기
	 * @return String[]
    */
	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr, int cdVal, String headTextYn) {
		String comboStr = "";
		
		if(comboStrArr != null) {
			
			if("Y".equals(headTextYn)) {
				comboStr = obj + ".AddComboListValue('" + hTitle + "', '', '');";
			}
			
			if(cdVal == 0 || cdVal == 1) {
				for(int ii=0; ii < comboStrArr[0].length; ii++) {
					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" + comboStrArr[cdVal][ii] + "', '" + comboStrArr[0][ii] + "');";
				}
			}else if(cdVal == 2) { //YD에 쓸수 있게 코드/코드명 형식으로 출력				
				for(int ii=0; ii < comboStrArr[0].length; ii++) {
					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" + 
									"[" +comboStrArr[0][ii] + "] " + comboStrArr[1][ii] + "', '" + comboStrArr[0][ii] + "');";
				} 
			}else {
				for(int ii=0; ii < comboStrArr[0].length; ii++) {
					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" + 
										comboStrArr[0][ii] + " (" + comboStrArr[1][ii] + ")', '" + comboStrArr[0][ii] + "');";
				}
			}
		}
		
		return comboStr;
	}
	
	public boolean isNumeric(String str){  

		try  {  

			double d = Double.parseDouble(str);  

	 	}catch(NumberFormatException nfe){  
	 		return false;  
	 	}  
	 	return true;  
	}
	
	
	/**
     * 
	 * 야드목표행선지구분를 지정한다.
     *
     * @param  String	sItemGp :	제품구분(S:SLAB, C:COIL ,P: 후판 ) ,JDTORecord inRecord
     *
     * @return String
     * @throws  
     */		
	public String[] getYdAimRtGp(String sItemGp, JDTORecord jrRecord) throws DAOException {
		String mthdNm = "야드목표행선구분 지정[coilDao.getYdAimRtGp] < " + jrRecord.getResultMsg();
		String logId = jrRecord.getResultCode();
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");

			JDTORecordSet jsRst = JDTORecordFactory.getInstance().createRecordSet("");
			String[] rVal = new String[2];

			String sMsg       	= "";
			String ydAimRtGp  	= "";
			String ydAimRtGp2	= "";
			String sSkinPassYn  = "";
			String sCurrProgCd	= "";
			String sWorkProc   	= "";
			String sNextProc  	= ""; // 다음공정
			String sPlanProc1	= ""; // 열연계획작업코드1
			String sRcvTcCode 	= commUtils.getTcCode(jrRecord);
			String sStlNo    	= commUtils.trim(jrRecord.getFieldString("STL_NO"));

//PIDEV     
			//PIDEV_S :병행가동용:PI_YD
			String sPI_YD     = commUtils.nvl(jrRecord.getFieldString("PI_YD"),"*");
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "getYdAimRtGp => 야드목표행선구분 지정", "APPPI0", sPI_YD, "*");
			
			if("PIDEV".equals("PIDEV")) {
				rVal = this.getYdAimRtGp_PIDEV("C", jrRecord);
				return rVal;
				
			}
			 if ("C".equals(sItemGp)) {
				// 수신한 재료번호로 코일공통 읽기***************************************************************************************************
				if (!"".equals(sStlNo)) {
					
					JDTORecord jrParam  = commUtils.getParam(logId, mthdNm, "");
					jrParam.setField("COIL_NO" , sStlNo);
					jsRst = commDao.select(jrParam,"com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getPtCoilComm",logId, mthdNm, "코일공통 조회");

					if (jsRst.size() <= 0) {
						sMsg = "코일공통 SELECT Error :: [" + sStlNo + "]"+ "DO NOT EXIST";
						commUtils.printLog(logId, sMsg, "SL");
						return rVal;
					} else {
						ydAimRtGp2 	= commUtils.trim(jsRst.getRecord(0).getFieldString("YD_AIM_RT_GP2"));
						sSkinPassYn = commUtils.trim(jsRst.getRecord(0).getFieldString("SKINPASS_YN"  ));
						sCurrProgCd = commUtils.trim(jsRst.getRecord(0).getFieldString("CURR_PROG_CD" ));
						
						commUtils.printLog(logId, ydAimRtGp2 + " " + sSkinPassYn + " " + sCurrProgCd, "SL");
						
						// 진도코드 존제여부 체크
						if ("".equals(sCurrProgCd)) {
							sMsg = "진도코드가  존재  안 함";
							commUtils.printLog(logId, sMsg, "SL");
							return rVal;
						}
						
						sNextProc  = commUtils.trim(jsRst.getRecord(0).getFieldString("NEXT_PROC"));
						sPlanProc1 = commUtils.trim(jsRst.getRecord(0).getFieldString("PLAN_PROC1"));
					}
										
				} else {
					// 진도코드
					sCurrProgCd = commUtils.trim(jrRecord.getFieldString("CURR_PROG_CD"));
				}


				if ("DMYDR005".equals(sRcvTcCode)) {
					ydAimRtGp = "K2"; // 출하지시대기
					sCurrProgCd = "K";
				} else if ("DMYDR020".equals(sRcvTcCode)) {
					ydAimRtGp = "L2"; // 운송지시
					sCurrProgCd = "L";
				} else if ("DMYDR023".equals(sRcvTcCode)
						|| "DMYDR060".equals(sRcvTcCode)) {
					ydAimRtGp = "L5"; // 상차지시
					sCurrProgCd = "L";
				} else if ("DMYDR030".equals(sRcvTcCode)) {
					ydAimRtGp = "M2"; // 출하완료
					sCurrProgCd = "M";
				} else if ("G".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 종합판정대기
				} else if ("I".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 반송대기
				} else if ("H".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 입고대기
				} else if ("Y".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "C"; // 재공충당대기(C열연정정)
				} else if ("B".equals(sCurrProgCd)) { // 지시대기

					sNextProc  = commUtils.trim(jsRst.getRecord(0).getFieldString("NEXT_PROC"));
					
					// C 동 수입인 경우 에만 spm재와 hfl재를 분리해서 적치 한다.
					if ("H".equals(sNextProc.substring(1, 2))) {
						ydAimRtGp = sCurrProgCd + "3"; // 지시대기
					} else {
						ydAimRtGp = sCurrProgCd + "4"; // 지시대기
					}
 
				} else if ("J".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 반납대기
				} else if ("Z".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 제품충당대기
				} else if ("X".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 경매대상선정
				} else if ("E".equals(sCurrProgCd) || "D".equals(sCurrProgCd)) {
					// 재공이송작업대기
					sNextProc  = commUtils.nvl(commUtils.trim(jsRst.getRecord(0).getFieldString("NEXT_PROC" )), "");
					sPlanProc1 = commUtils.nvl(commUtils.trim(jsRst.getRecord(0).getFieldString("PLAN_PROC1")), "");

					if (!"".equals(sNextProc)) {
						sWorkProc = sNextProc;
					} else {
						sWorkProc = sPlanProc1;
					}
					// 계획공정정보를 가지고 야드행선을 셋팅
					if (sWorkProc.startsWith("1")) {
						ydAimRtGp = "EA";
					} else if (sWorkProc.startsWith("5")
							|| sWorkProc.startsWith("6")) {
						ydAimRtGp = "EB";
					} else if (sWorkProc.startsWith("9S")) {
						ydAimRtGp = "ED";
					} else {
						ydAimRtGp = "EC";
					}
				} else if ("C".equals(sCurrProgCd)) {
					
					// 정정작업지시대기
					sNextProc  = commUtils.nvl(commUtils.trim(jsRst.getRecord(0).getFieldString("NEXT_PROC" )), "");
					sPlanProc1 = commUtils.nvl(commUtils.trim(jsRst.getRecord(0).getFieldString("PLAN_PROC1")), "");

					if (!"".equals(sNextProc)) {
						sWorkProc = sNextProc;
					} else {
						sWorkProc = sPlanProc1;
					}

					/*
					계획공정코드
						DH C열연 D Line No3HFL C열연 D Line No3HFL(정정LINE구분 : No3HFL) 11 
						DA C열연 D Line 공냉 C열연 D Line 공냉(Hysco向) 12 
						EH C열연 E Line Hot Final C열연 E Line Hot Final(정정LINE구분:SPM2) 13 
						EK C열연 E Line Skin Pass C열연 E Line Skin Pass(정정LINE구분:SPM2) 14 
						ER C열연 E Line Recoiling C열연 E Line Recoiling(정정LINE구분:SPM2) 15 
						EA C열연 E Line 공냉 C열연 E Line 공냉(Hysco向) 16 
						FH C열연 F Line No2HFL C열연 F Line No2HFL(정정LINE구분:No2HFL) 17 
						FA C열연 F Line 공냉 C열연 F Line 공냉(Hysco向) 18 
						GA C열연 G Line 공냉 C열연 G Line 공냉(정정LINE구분:No1HFL) 19 
						GH C열연 G Line No1HFL C열연 G Line No1HFL(정정LINE구분:No1HFL) 20 
						GT C열연 G Line 수냉 C열연 G Line 수냉(정정LINE구분:No1HFL) 21 
						HH C열연 H Line Hot Final C열연 H Line Hot Final(정정LINE구분:SPM1) 22 
						HK C열연 H Line Skin Pass C열연 H Line Skin Pass(정정LINE구분:SPM1) 23 
						HR C열연 H Line Recoiling C열연 H Line Recoiling(정정LINE구분:SPM1) 24 
						HA C열연 H Line 공냉 C열연 H Line 공냉(Hysco向) 25
					야드행선구분 
						CE 작업대기(C열연 HFL)
						CF 작업대기(C열연 SPM1)
						CG 작업대기(C열연 SPM2)
						CH 작업대기(C열연#1결속대)
						CI 작업대기(C열연#2결속대) 
					*/

					// 계획공정정보를 가지고 야드행선을 셋팅 _ 추후 다시 셋팅 (C열연만 셋팅 )
					if("DH".equals(sWorkProc)||
					   "FH".equals(sWorkProc)||
					   "GA".equals(sWorkProc)||
					   "GH".equals(sWorkProc)||
					   "CA".equals(sWorkProc)||
					   "CH".equals(sWorkProc)||
					   "AA".equals(sWorkProc)||
					   "BH".equals(sWorkProc)||
					   "GT".equals(sWorkProc)){
						ydAimRtGp	= "CE";
					}else if("HH".equals(sWorkProc)||
							 "HK".equals(sWorkProc)||
							 "HR".equals(sWorkProc)){
						ydAimRtGp	= "CF";
					}else if("EH".equals(sWorkProc)||
							 "EK".equals(sWorkProc)||
							 "ER".equals(sWorkProc)){
						ydAimRtGp	= "CG";
					}else if("CK".equals(sWorkProc)||
							 "CR".equals(sWorkProc)){
						ydAimRtGp	= "CF";
					}else if("BK".equals(sWorkProc)||
							 "BR".equals(sWorkProc)){
						ydAimRtGp	= "CF";	
					}else if("AK".equals(sWorkProc)||
							 "AR".equals(sWorkProc)){
						ydAimRtGp	= "CF";	
					}else {
						ydAimRtGp	= "XX";
					}	
					if("F4".equals(ydAimRtGp2) || "F5".equals(ydAimRtGp2)) {   		//재작업인 경우 
						ydAimRtGp = ydAimRtGp2;										   //재작업인(C열연정정)
					}

				} else if ("F".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "3"; // 판정보류 
				}

				//2pass재 작업 대상
				if ("Z".equals(sSkinPassYn) && ("C".equals(sCurrProgCd)||"D".equals(sCurrProgCd))) {
					ydAimRtGp	= "EA";
				}
			
			}
			 
			sMsg = "진도코드: " + sCurrProgCd+" 야드목표행선지구분: " + ydAimRtGp;
			commUtils.printLog(logId, sMsg, "S-");
	 
			rVal[0] = commUtils.trim(ydAimRtGp);
			rVal[1] = commUtils.trim(sCurrProgCd);

			commUtils.printLog(logId, mthdNm, "S-");

			return rVal;
			 
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

	}		
	
	
	
	/**
	 * 야드목표행선지구분를 지정한다.
     *
     * @param  String	:	제품구분(S:SLAB, C:COIL ,P: 후판 ) ,제품코드
     *
     * @return String
     * @throws  
     */		
	public String[] getYdAimRtGp2(String sRcvTcCode ,String sItemGp ,String sStlNo ,String sCurrProgCd) throws DAOException {
		String mthdNm = "야드목표행선구분 지정[coilDao.getYdAimRtGp2] < ";// + jrRecord.getResultMsg();
		String logId  = "";//jrRecord.getResultCode();
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");

			String[] rVal = new String[2];

			String ydAimRtGp = "";
			
			if ("P".equals(sItemGp)) {
				
				if ("H".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "3";	//입고대기
				} else if ("J".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "3";	//반납대기
				} else if ("K".equals(sCurrProgCd)) {		
					ydAimRtGp = sCurrProgCd + "3";	//출하지시대기
				} else if ("L".equals(sCurrProgCd)) {
					if ("DMYDR021".equals(sRcvTcCode)) { //후판제품운송상차지시
						ydAimRtGp = sCurrProgCd + "6";	//상차대기 
					} else {
						ydAimRtGp = sCurrProgCd + "3";	//운송대기
					}
				} else if ("M".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "3";	//출하완료
				} else if ("N".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "3";	//운송지시대기
				}
				
			} else if ("C".equals(sItemGp)) {
				
				if ("H".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2";	//입고대기
				} else if ("J".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2";	//반납대기
				} else if ("K".equals(sCurrProgCd)) {		
					ydAimRtGp = sCurrProgCd + "2";	//출하지시대기
				} else if ("L".equals(sCurrProgCd)) {
					if ("DMYDR023".equals(sRcvTcCode)) { //코일제품상차지시
						ydAimRtGp = sCurrProgCd + "5";	//상차대기 
					} else {
						ydAimRtGp = sCurrProgCd + "2";	//운송대기
					}
				} else if ("M".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2";	//출하완료				
				} else if ("N".equals(sCurrProgCd)) {
					ydAimRtGp ="K2";			//운송지시대기
				}

			} else if ("S".equals(sItemGp)) {
				
				if ("H".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "1";	//입고대기
				} else if ("J".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "1";	//반납대기
				} else if ("K".equals(sCurrProgCd)) {		
					ydAimRtGp = sCurrProgCd + "1";	//출하지시대기
				} else if ("L".equals(sCurrProgCd)) {
					if ("DMYDR022".equals(sRcvTcCode)) { //외판슬라브운송상차지시 
						ydAimRtGp = sCurrProgCd + "4";	//상차대기 
					} else {
						ydAimRtGp = sCurrProgCd + "1";	//운송대기
					}
				} else if ("M".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "1";	//출하완료
				} else if ("N".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "1";	//운송지시대기
				}
			}
			
			rVal[0] = ydAimRtGp ;
			return rVal;
			 
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

	}	
	
	

	/**
	 *      [A] 오퍼레이션명 : 스케쥴기준 적용여부?
	 * 
	 * @param String sSkidGp
	 * @return boolean     			
	 */	
/*	public boolean chkSchRuleYn(String logId, String mthdNms, String sRuleCd, String sSkidGp, JDTORecordSet jsSchRule) throws DAOException {
		String mthdNm = "스케줄기준  CHECK [CCoilDao.chkSchRuleYn] < " + mthdNms;
		try {
			JDTORecord jrSchRule =	JDTORecordFactory.getInstance().create();
			
			for (int Loop_i = 1; Loop_i <= jsSchRule.size(); Loop_i++) {
				jsSchRule.absolute(Loop_i);
				jrSchRule = jsSchRule.getRecord(); 
				if(sRuleCd.equals(jrSchRule.getFieldString("CD_GP"))) {
					if("H".equals(sSkidGp)    && "Y".equals("H_YN"))    {
						return true;
					}	
					if("J".equals(sSkidGp)    && "Y".equals("J_YN"))    {
						return true;
					}	
					if("ABC".equals(sSkidGp)  && "Y".equals("ABC_YN"))  {
						return true;
					}	
					if("WRAP".equals(sSkidGp) && "Y".equals("WRAP_YN")) {
						return true;
					}	
				}		
			}				
			return false;
			
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

	}	
	*/
	
	/**
	 * 오퍼레이션명 : 스케쥴기준을 조회하여 크레인 정보를 반환하는 메소드
	 * 강정선 19.10.04
	 * @param sYdSchCd, jrResult
	 * @param jrResult
	 * @return int
	 *			 1 : 메소드 호출 성공
	 * 			-1 : 스케쥴금지
	 *			-2 : 작업크레인고장이고 대체크레인정보가 없는 경우
	 *			-3 : 작업크레인고장이고 대체크레인 고장인 경우 작업 불가
	 *			-4 : 스케쥴기준 조회에러
	 *			-5 : 크레인설비 정보 조회시 에러 발생
	 * @throws JDTOException
	 */
	public int getCrnInfoByCrnSchRule(String ydSchCd, JDTORecord jrResult) throws JDTOException {
		String mthdNm	= "스케쥴기준조회 크레인정보 반환[CCoilDao.getCrnInfoByCrnSchRule] < " + jrResult.getResultMsg();
		String logId	= jrResult.getResultCode(); // 크레인정보 return 이지만 LogId, Method 명 셋팅하여 전달할 것
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
			String sMsg			= "";
			String ydCrn		= "";
			String ydSchPrior	= "9";
			// 리턴 recordSet 생성
			JDTORecordSet jsSchRule = JDTORecordFactory.getInstance().createRecordSet("Temp");
	
			// 레코드 추출
			jsSchRule.first();
			JDTORecord jrSchRule = jsSchRule.getRecord();
	
			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			String ydSchProhExn = commUtils.trim(jrSchRule.getFieldString("YD_SCH_PROH_EXN"));
			if ( "Y".equals(ydSchProhExn) ) {
				sMsg = "스케줄 금지 유무가 [" + ydSchProhExn + "] 입니다";
				commUtils.printLog(logId, sMsg, "SL");
				return -1;
			}
			
			// 작업크레인
			String ydWrkCrn = commUtils.trim(jrSchRule.getFieldString("YD_WRK_CRN"));
			
			// 작업크레인 설비 상태 체크
			String sRtnMsg = this.chkCrnStat(logId, mthdNm, ydWrkCrn);
	
			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if( !CConstant.RETN_CD_SUCCESS.equals(sRtnMsg) ) {
	
				commUtils.printLog(logId, "작업크레인(" + ydWrkCrn + ")이 사용 불가 상태입니다.", "SL");
	
				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				String sYdAltCrnYn = commUtils.trim(jrSchRule.getFieldString("YD_ALT_CRN_YN"));
				if (!"Y".equals(sYdAltCrnYn) ) {
	
					commUtils.printLog(logId, "대체크레인유무(" + sYdAltCrnYn + "), 대체크레인이 없습니다.", "SL");
					return -2;
	
				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				String sYdAltCrn = commUtils.trim(jrSchRule.getFieldString("YD_ALT_CRN"));
				
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if( !CConstant.RETN_CD_SUCCESS.equals(sRtnMsg) ) {
	
					commUtils.printLog(logId, "대체크레인(" + sYdAltCrn + ")이 사용 불가 상태입니다.", "SL");
					return -3;
	
				} else {
					// 대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					ydCrn = sYdAltCrn;
					ydSchPrior = commUtils.trim(jrSchRule.getFieldString("YD_ALT_CRN_PRIOR"));
				}
			} else {
				// 작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				ydCrn = ydWrkCrn;
				ydSchPrior = commUtils.trim(jrSchRule.getFieldString("YD_WRK_CRN_PRIOR"));
			}
			
			
			jrResult.setField("YD_WRK_CRN"			, ydCrn); //작업가능한 크레인
			jrResult.setField("YD_SCH_PRIOR"		, ydSchPrior);//스케쥴우선순위
			jrResult.setField("YD_WRK_ABLE_WT"		, commUtils.trim(jrSchRule.getFieldString("YD_WRK_ABLE_WT"))); //크레인 작업허용중량
			jrResult.setField("YD_CRN_TONG_W_TOL"	, commUtils.trim(jrSchRule.getFieldString("YD_CRN_TONG_W_TOL")));  //크레인 집게허용 오차
			jrResult.setField("YD_WRK_ABLE_SH"		, commUtils.trim(jrSchRule.getFieldString("YD_WRK_ABLE_SH")));  //크레인 작업가능 매수
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return 1;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * COIL 저장품 등록 수정 삭제 처리
	 * 강정선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param String logId
	 * @param String mthdNm
	 * @param String sStlNo
	 * @param String procFlag
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean stockProcCom (String logId, String mthdNms, String sStlNo, int procFlag) throws JDTOException {
		/*
		 * procFlag : 1.등록(Insert), 2.수정(Update), 3.삭제(Delete)
		 * --> 기존 procFlag 값 추가와 수정은 같은 쿼리로 처리하나 수정은 행선(YD_AIM_RT_GP) Method 에서 가져옴
		 */
		String mthdNm = "Coil저장품 등록/수정/삭제[CCoilDAO.stockProcCom] < " + mthdNms;
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
			int intRtnVal	= 0;
			String sMsg		= "";
			
			//--------------------------------------------------//
			// CHECK : 코일번호
			//--------------------------------------------------//
			if( "".equals(sStlNo) ) {
				sMsg = "파라미터 Error 재료번호가 없습니다"; 
				commUtils.printLog(logId, sMsg, "SL");
				return false;
			}
			
			commUtils.printLog(logId, "STL_NO : ["+ sStlNo +"], procFlag : ["+ procFlag +"]", "SL");
			//--------------------------------------------------//
			// procFlag : 1(추가), 2(수정)
			//--------------------------------------------------//
			if( procFlag == 1 || procFlag == 2) {
				//--------------------------------------------------//
				// 코일공통정보 조회 TB_PT_COILCOMM
				//--------------------------------------------------//
				JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "stockProc");
				jrParam.setField("COIL_NO", sStlNo);
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCoilComm */
				/*
				SELECT COIL_NO
				     , COIL_T
				     , COIL_W
				     , COIL_LEN
				     , COIL_WT
				     , COIL_INDIA
				     , COIL_OUTDIA
				     , PLNT_PROC_CD
				     , ORD_YEOJAE_GP
				     , ORD_NO
				     , ORD_DTL
				     , DEMANDER_CD
				     , HCR_GP
				     , HYSCO_TRANS_GP
				     , CUST_CD
				     , ITEMNAME_CD
				     , STL_APPEAR_GP
				     , NEXT_PROC
				     , NVL(SHEAR_W, 0)     AS SHEAR_W
				     , NVL(COIL_OUTDIA, 0) AS COIL_OUTDIA
				  FROM TB_PT_COILCOMM
				 WHERE COIL_NO = :V_COIL_NO
				*/
				JDTORecordSet jsCoilComm = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCoilComm", logId, mthdNm, "공통코일정보 조회");
				if( jsCoilComm.size() < 1 ) {
					sMsg = "TB_PT_COILCOMM에 코일정보가 없습니다. COIL_NO["+ sStlNo +"]"; 
					commUtils.printLog(logId, sMsg, "SL");
					return false;
				}
				jsCoilComm.first();
				JDTORecord jrCoilComm = jsCoilComm.getRecord();
				
				/*********************************
				 * 추가정보 편집
				 *********************************/
				// 야드목표행성구분 : EC(이송대기 2열연정정)
				String ydAimRtGp = "EC";
				
				// 목표야드구분 : J(2열연)
				String ydAimYdGp = "J";
				
				// 목표동 : 차공정 목표동으로 셋팅
				String sNextProc = commUtils.nvl(jrCoilComm.getFieldString("NEXT_PROC"), " ");
				String ydAimBayGp = sNextProc.substring(0, 1);
				
				// 재료외형구분(STL_APPEAR_GP) : E(열연코일), Y(제품)
				// 야드재료품목(YD_TML_ITEM) : CM(코일소재), CG(코일제품)
				String sStlAppearGp = commUtils.trim(jrCoilComm.getFieldString("STL_APPEAR_GP"));
				String ydMtlItem = "";
				if( "E".equals(sStlAppearGp) ){
					ydMtlItem = "CM";
				} else if ("Y".equals(sStlAppearGp) ) {
					ydMtlItem = "CG";
				}
				
				// 재료외형구분으로 목표야드를 셋팅함.
//				if( "CM".equals(ydMtlItem) ) {
//					ydAimYdGp = "H"; 
//				} else if("CG".equals(ydMtlItem) ) {
//					ydAimYdGp = "J";
//				}
				
				String ydMtlWGp	= "";
				String ydCoilOutDiaGrpGp = "";
				double  dMtlWGp	= Double.parseDouble(jrCoilComm.getFieldString("COIL_W"));
				double  dOutDia	= Double.parseDouble(jrCoilComm.getFieldString("COIL_OUTDIA"));
				
				// 야드재료폭구분 : M(중폭), L(광폭)
				if( dMtlWGp < 1601 ) {
					ydMtlWGp = "M";
				} else {
					ydMtlWGp = "L";
				}
				
				// 코일외경군 구분
				if( dOutDia < 1281 ) {
					ydCoilOutDiaGrpGp = "A";
				} else if( dOutDia < 1931 ) {
					ydCoilOutDiaGrpGp = "B";
				} else {
					ydCoilOutDiaGrpGp = "C";
				}
				
				/***************************************
				 * procFlag : 2(수정) 행선 별도 로직
				 ***************************************/
				if( procFlag == 2 ) {
	
					jrParam = commUtils.getParam(logId, mthdNm, "stockProc");
					jrParam.setResultCode(logId);
					jrParam.setField("STL_NO", sStlNo);
	
					// 행선정보 가져옴
					String[] sAimRtGp = new String[1];
					sAimRtGp = this.getYdAimRtGp("C", jrParam);
					ydAimRtGp = sAimRtGp[0];
				}
				
				
				// Parameter 설정
				JDTORecord jrStockData = commUtils.getParam(logId, mthdNm, "stockProc");
				jrStockData.setField("STL_NO"			, commUtils.trim(jrCoilComm.getFieldString("COIL_NO")));
				jrStockData.setField("YD_MTL_T"			, commUtils.trim(jrCoilComm.getFieldString("COIL_T")));
				jrStockData.setField("YD_MTL_W"			, commUtils.trim(jrCoilComm.getFieldString("COIL_W")));
				jrStockData.setField("YD_MTL_L"			, commUtils.trim(jrCoilComm.getFieldString("COIL_LEN")));
				jrStockData.setField("YD_MTL_WT"		, commUtils.trim(jrCoilComm.getFieldString("COIL_WT")));
				jrStockData.setField("COIL_INDIA"		, commUtils.trim(jrCoilComm.getFieldString("COIL_INDIA")));
				jrStockData.setField("COIL_OUTDIA"		, commUtils.trim(jrCoilComm.getFieldString("COIL_OUTDIA")));
				jrStockData.setField("PLNT_PROC_CD"		, commUtils.trim(jrCoilComm.getFieldString("PLNT_PROC_CD")));
				jrStockData.setField("ORD_YEOJAE_GP"	, commUtils.trim(jrCoilComm.getFieldString("ORD_YEOJAE_GP")));
				jrStockData.setField("ORD_NO"			, commUtils.trim(jrCoilComm.getFieldString("ORD_NO")));
				jrStockData.setField("ORD_DTL"			, commUtils.trim(jrCoilComm.getFieldString("ORD_DTL")));
				jrStockData.setField("DEMANDER_CD"		, commUtils.trim(jrCoilComm.getFieldString("DEMANDER_CD")));
				jrStockData.setField("HCR_GP"			, commUtils.trim(jrCoilComm.getFieldString("HCR_GP")));
				jrStockData.setField("HYSCO_TRANS_GP"	, commUtils.trim(jrCoilComm.getFieldString("HYSCO_TRANS_GP")));
				jrStockData.setField("CUST_CD"			, commUtils.trim(jrCoilComm.getFieldString("CUST_CD")));
				jrStockData.setField("ITEMNAME_CD"		, commUtils.trim(jrCoilComm.getFieldString("ITEMNAME_CD")));
				jrStockData.setField("STL_PROG_CD"		, commUtils.trim(jrCoilComm.getFieldString("CURR_PROG_CD"))); // 재료진도코드
				jrStockData.setField("STL_APPEAR_GP"	, sStlAppearGp);
				jrStockData.setField("YD_MTL_ITEM"		, ydMtlItem);
				jrStockData.setField("YD_AIM_RT_GP"		, ydAimRtGp);
				jrStockData.setField("YD_AIM_YD_GP"		, ydAimYdGp);
				jrStockData.setField("YD_AIM_BAY_GP"	, ydAimBayGp);
				jrStockData.setField("YD_MTL_W_GP"		, ydMtlWGp);
				jrStockData.setField("YD_COIL_OUTDIA_GRP_GP", ydCoilOutDiaGrpGp);
				jrStockData.setField("DEL_YN"			, "N");
				jrStockData.setField("PTOP_PLNT_GP"		, ""); // 조업공장구분

				/***************************************
				 * UPDATE 일때만 진도코드 STL_PROG_CD 적용
				 ***************************************/
				/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStockMetge */
				/*
				MERGE INTO TB_YD_STOCK A
				USING (
				    SELECT
				           :V_STL_NO                AS STL_NO
				         , :V_YD_MTL_T              AS YD_MTL_T
				         , :V_YD_MTL_W              AS YD_MTL_W
				         , :V_YD_MTL_L              AS YD_MTL_L
				         , :V_YD_MTL_WT             AS YD_MTL_WT
				         , :V_COIL_INDIA            AS COIL_INDIA
				         , :V_COIL_OUTDIA           AS COIL_OUTDIA
				         , :V_PLNT_PROC_CD          AS PLNT_PROC_CD
				         , :V_ORD_YEOJAE_GP         AS ORD_YEOJAE_GP
				         , :V_ORD_NO                AS ORD_NO
				         , :V_ORD_DTL               AS ORD_DTL
				         , :V_DEMANDER_CD           AS DEMANDER_CD
				         , :V_HCR_GP                AS HCR_GP
				         , :V_HYSCO_TRANS_GP        AS HYSCO_TRANS_GP
				         , :V_CUST_CD               AS CUST_CD
				         , :V_ITEMNAME_CD           AS ITEMNAME_CD
				         , :V_STL_PROG_CD           AS STL_PROG_CD
				         , :V_STL_APPEAR_GP         AS STL_APPEAR_GP
				         , :V_YD_MTL_ITEM           AS YD_MTL_ITEM
				         , :V_YD_AIM_RT_GP          AS YD_AIM_RT_GP
				         , :V_YD_AIM_YD_GP          AS YD_AIM_YD_GP
				         , :V_YD_AIM_BAY_GP         AS YD_AIM_BAY_GP
				         , :V_YD_MTL_W_GP           AS YD_MTL_W_GP
				         , :V_PTOP_PLNT_GP          AS PTOP_PLNT_GP
				         , :V_YD_COIL_OUTDIA_GRP_GP AS YD_COIL_OUTDIA_GRP_GP
				         , :V_DEL_YN                AS DEL_YN
				         , :V_MODIFIER              AS REGISTER
				         , :V_MODIFIER              AS MODIFIER
				         , SYSDATE                  AS REG_DDTT
				         , SYSDATE                  AS MOD_DDTT
				      FROM DUAL
				      ) B
				   ON ( A.STL_NO = B.STL_NO )
				 WHEN MATCHED
				      THEN UPDATE
				              SET
				                  A.YD_MTL_T              = B.YD_MTL_T
				                , A.YD_MTL_W              = B.YD_MTL_W
				                , A.YD_MTL_L              = B.YD_MTL_L
				                , A.YD_MTL_WT             = B.YD_MTL_WT
				                , A.COIL_INDIA            = B.COIL_INDIA
				                , A.COIL_OUTDIA           = B.COIL_OUTDIA
				                , A.PLNT_PROC_CD          = B.PLNT_PROC_CD
				                , A.ORD_YEOJAE_GP         = B.ORD_YEOJAE_GP
				                , A.ORD_NO                = B.ORD_NO
				                , A.ORD_DTL               = B.ORD_DTL
				                , A.DEMANDER_CD           = B.DEMANDER_CD
				                , A.HCR_GP                = B.HCR_GP
				                , A.HYSCO_TRANS_GP        = B.HYSCO_TRANS_GP
				                , A.CUST_CD               = B.CUST_CD
				                , A.ITEMNAME_CD           = B.ITEMNAME_CD
				                , A.STL_PROG_CD           = B.STL_PROG_CD
				                , A.STL_APPEAR_GP         = B.STL_APPEAR_GP
				                , A.YD_MTL_ITEM           = B.YD_MTL_ITEM
				                , A.YD_AIM_RT_GP          = B.YD_AIM_RT_GP
				                , A.YD_AIM_YD_GP          = B.YD_AIM_YD_GP
				                , A.YD_AIM_BAY_GP         = B.YD_AIM_BAY_GP
				                , A.YD_MTL_W_GP           = B.YD_MTL_W_GP
				                , A.PTOP_PLNT_GP          = B.PTOP_PLNT_GP
				                , A.YD_COIL_OUTDIA_GRP_GP = B.YD_COIL_OUTDIA_GRP_GP
				                , A.MODIFIER              = B.MODIFIER
				                , A.MOD_DDTT              = B.MOD_DDTT
				                , A.DEL_YN                = B.DEL_YN
				 WHEN NOT MATCHED
				      THEN INSERT
				           (
				                  STL_NO
				                , YD_MTL_T
				                , YD_MTL_W
				                , YD_MTL_L
				                , YD_MTL_WT
				                , COIL_INDIA
				                , COIL_OUTDIA
				                , PLNT_PROC_CD
				                , ORD_YEOJAE_GP
				                , ORD_NO
				                , ORD_DTL
				                , DEMANDER_CD
				                , HCR_GP
				                , HYSCO_TRANS_GP
				                , CUST_CD
				                , ITEMNAME_CD
				                , STL_APPEAR_GP
				                , YD_MTL_ITEM
				                , YD_AIM_RT_GP
				                , YD_AIM_YD_GP
				                , YD_AIM_BAY_GP
				                , YD_MTL_W_GP
				                , PTOP_PLNT_GP
				                , YD_COIL_OUTDIA_GRP_GP
				                , REGISTER
				                , REG_DDTT
				                , MODIFIER
				                , MOD_DDTT
				                , DEL_YN
				           )
				           VALUES
				           (
				                  B.STL_NO
				                , B.YD_MTL_T
				                , B.YD_MTL_W
				                , B.YD_MTL_L
				                , B.YD_MTL_WT
				                , B.COIL_INDIA
				                , B.COIL_OUTDIA
				                , B.PLNT_PROC_CD
				                , B.ORD_YEOJAE_GP
				                , B.ORD_NO
				                , B.ORD_DTL
				                , B.DEMANDER_CD
				                , B.HCR_GP
				                , B.HYSCO_TRANS_GP
				                , B.CUST_CD
				                , B.ITEMNAME_CD
				                , B.STL_APPEAR_GP
				                , B.YD_MTL_ITEM
				                , B.YD_AIM_RT_GP
				                , B.YD_AIM_YD_GP
				                , B.YD_AIM_BAY_GP
				                , B.YD_MTL_W_GP
				                , B.PTOP_PLNT_GP
				                , B.YD_COIL_OUTDIA_GRP_GP
				                , B.REGISTER
				                , B.REG_DDTT
				                , B.MODIFIER
				                , B.MOD_DDTT
				                , B.DEL_YN
				           )
				*/
				intRtnVal = commDao.update(jrStockData, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStockMetge", logId, mthdNm, "TB_YD_STOCK 저장");
				if( intRtnVal < 1 ) {
					sMsg = "TB_YD_STOCK 저장품 저장 실패. COIL_NO["+ sStlNo +"]"; 
					commUtils.printLog(logId, sMsg, "SL");
					return false;
				}
				
				commUtils.printLog(logId, "STL_NO       : ["+ sStlNo +"]", "SL");
				commUtils.printLog(logId, "YD_AIM_RT_GP : ["+ ydAimRtGp +"]", "SL");
				commUtils.printLog(logId, "=================== 저장 완료 ===================", "SL");
				
			} else if (procFlag == 3 ) {
				//--------------------------------------------------//
				// procFlag : 3(삭제)
				//--------------------------------------------------//
				JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "stockProc");
				jrParam.setField("STL_NO", sStlNo);
				jrParam.setField("DEL_YN", "Y");
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStockDel */
				/*
				UPDATE TB_YD_STOCK
				   SET DEL_YN   = :V_DEL_YN
				     , MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				 WHERE STL_NO   = :V_STL_NO
				*/
				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStockDel", logId, mthdNm, "TB_YD_STOCK 삭제");
				if( intRtnVal < 0 ) {
					sMsg = "TB_YD_STOCK 저장품 삭제 실패. COIL_NO["+ sStlNo +"]"; 
					commUtils.printLog(logId, sMsg, "SL");
					return false;
				}
				
				commUtils.printLog(logId, "STL_NO       : ["+ sStlNo +"]", "SL");
				commUtils.printLog(logId, "=================== 삭제 완료 ===================", "SL");
			}
			
			commUtils.printLog(logId, "저장품 등록 수정 삭제 처리 완료 되었습니다. stockProcCom", "SL");
			
			commUtils.printLog(logId, mthdNm, "S-");
			return true;

		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 오퍼레이션명 : 준비스케쥴 원복
	 * 강정선 19.11.08
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord restorePrepSch(JDTORecord rcvMsg) throws DAOException {
		String mthdNm	= "준비스케쥴 원복[CCoilDAO.restorePrepSch]  < " + rcvMsg.getResultMsg();
		String logId	= rcvMsg.getResultCode();

		try { 
			commUtils.printLog(logId, mthdNm, "S+");

			String sMsg			= "";
			String userid		= rcvMsg.getFieldString("MODIFIER");

			JDTORecord jrRtn	= JDTORecordFactory.getInstance().create();
			JDTORecord jrParam	= null;

			String ydWbookId = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"));
			
    		/**************************************************
    		 * 1. 준비스케쥴 검색
    		 **************************************************/
			jrParam = commUtils.getParam(logId, mthdNm, userid);
			jrParam.setField("YD_WBOOK_ID", ydWbookId);
			/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdPrepschByYdWbookId */
			/*
			SELECT YD_PREP_SCH_ID
			     , YD_SCH_CD
			     , DEL_YN
			     , YD_GP
			     , YD_PREP_WK_ST
			     , YD_TO_LOC_DCSN_MTD
			     , YD_TO_LOC_GUIDE
			     , ARR_WLOC_CD
			     , YD_AIM_BAY_GP
			     , YD_CARASGN_SEQ
			     , YD_EQP_WRK_SH
			     , YD_WRK_PLAN_CRN
			     , YD_WBOOK_ID
			  FROM TB_YD_PREPSCH
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			*/
			JDTORecordSet jsPrepSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdPrepschByYdWbookId", logId, mthdNm, "준비스케쥴검색");
		
			sMsg = "작업예약ID["+ ydWbookId +"] 준비스케쥴 검색 jsPrepSch.size() = "+ jsPrepSch.size();
			commUtils.printLog(logId, sMsg, "SL");
			
			if( jsPrepSch.size() < 0 ) {
				
				sMsg = "작업예약ID["+ ydWbookId +"] 준비스케쥴 검색 error. jsPrepSch.size() = "+ jsPrepSch.size();
				commUtils.printLog(logId, sMsg, "SL");
				
	    		jrRtn.setField("RTN_CD"	, "0");
	    		jrRtn.setField("RTN_MSG", sMsg);
	    		return jrRtn;
			} else if( jsPrepSch.size() == 0 ) {
				sMsg = "작업예약ID["+ ydWbookId +"] 준비스케쥴 없음";
				commUtils.printLog(logId, sMsg, "SL");
			} else {
	    		/**************************************************
	    		 * 2-1. 준비스케쥴 재료정보 복구
	    		 **************************************************/
				jsPrepSch.first();
				String ydPrepSchId = commUtils.trim(jsPrepSch.getRecord().getFieldString("YD_PREP_SCH_ID"));
				
				jrParam = commUtils.getParam(logId, mthdNm, userid);
				jrParam.setField("YD_PREP_SCH_ID"	, ydPrepSchId);
				jrParam.setField("DEL_YN"			, "N");
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.delYdPrepmtlByPrepSchId1 */
				/*
				UPDATE TB_YD_PREPMTL
				   SET MODIFIER       = :V_MODIFIER
				     , MOD_DDTT       = SYSDATE
				     , DEL_YN         = :V_DEL_YN
				 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
				*/
				int intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.delYdPrepmtlByPrepSchId1", logId, mthdNm, "준비스케쥴재료 복구");
				
	    		/**************************************************
	    		 * 2-2. 준비스케쥴 복구 (작업예약 null 처리)
	    		 **************************************************/
				/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.delYdPrepsch1 */
				/*
				UPDATE TB_YD_PREPSCH
				   SET DEL_YN         = :V_DEL_YN
				     , YD_WBOOK_ID    = :V_YD_WBOOK_ID
				     , MODIFIER       = :V_MODIFIER
				     , MOD_DDTT       = SYSDATE
				 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
				*/
				jrParam.setField("YD_WBOOK_ID", "");
				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.delYdPrepsch1", logId, mthdNm, "준비스케쥴 복구");
			}

			commUtils.printLog(logId, mthdNm, "S-");

			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "준비스케쥴원복을 정상적으로 처리하였습니다.");
			return jrRtn;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 차량작업예정정보요구 YDY5L008
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procCarPlanInfo(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "차량작업예정정보요구[CCoilDAO.procCarPlanInfo] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID

			String ydLoadLoc   	= commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC"  )); //상차도 위치
			
			String ydCarSchId  	= commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID")); //차량스케쥴 ID
			String carWrkGp    	= commUtils.trim(rcvMsg.getFieldString("CAR_WRK_GP"   )); //차량동간이적시 G으로 SET
			
			String sModifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); 		//수정자(Backup Only)
//PIDEV_S :병행가동용:PI_YD
//			String sPI_YD     = commUtils.nvl(rcvMsg.getFieldString("PI_YD"),"*");			
			if ("".equals(sModifier)) { sModifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			
			if ("".equals(ydLoadLoc) && "".equals(ydCarSchId)) {
				commUtils.printLog(logId, "상차도위치 또는 차량스케줄Id가 없음" , "SL");
			}

			JDTORecordSet jrCarInfo = JDTORecordFactory.getInstance().createRecordSet("");
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);	
			jrParam.setField("YD_CARUD_STOP_LOC", ydLoadLoc);
			
			
			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschInfo_PIDEV *
			SELECT A.CAR_NO,A.YD_CAR_WRK_GP ,A.YD_CAR_PROG_STAT
			  FROM TB_YD_CARSCH A
			     , TB_YD_CARPOINT B
			 WHERE B.YD_STK_COL_GP = :V_YD_CARUD_STOP_LOC
			   AND 'Y' = CASE WHEN A.CAR_NO = B.CAR_NO AND A.CARD_NO = B.CARD_NO THEN 'Y' 
			                  WHEN A.TRN_EQP_CD = B.TRN_EQP_CD THEN 'Y'
			                  ELSE 'N' END
			   AND A.DEL_YN = 'N'
			*/
/*			
			JDTORecordSet jrCarPointInfo = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschInfo_PIDEV", logId, mthdNm, "상차도 차량정보");
			
			if(jrCarPointInfo.size() == 0 ) {
				jrRtn.setField("RTN_CD"	, "0");
	    		jrRtn.setField("RTN_MSG", "상차도 정보이상");
	    		return jrRtn;
			} else {
				carWrkGp = jrCarPointInfo.getRecord(0).getFieldString("YD_CAR_WRK_GP");
			}
*/			
			
			jrParam.setField("YD_CAR_SCH_ID"    , ydCarSchId);
			
			/**********************************************************
			* 2. 차량예정정보 조회
			**********************************************************/
			//차량동간이적 (쿼리 분리 )
			if ("G".equals(carWrkGp)) {
				
				/**********************************************************
				* 2. 차량동간이적 시
				**********************************************************/
				if ("".equals(ydCarSchId)) {
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschCarGetInWorkByCarMove 
					WITH TEMP_PARAM AS (
					SELECT MAX(YD_CAR_SCH_ID) AS YD_CAR_SCH_ID
					  FROM USRYDA.TB_YD_CARSCH
					 WHERE (CASE WHEN YD_CAR_PROG_STAT IN ('2','3','4','5') THEN YD_CARLD_STOP_LOC
					             ELSE YD_CARUD_STOP_LOC END) = :V_YD_CARUD_STOP_LOC
					   AND  DEL_YN = 'N'
					   AND  YD_CAR_PROG_STAT NOT IN ('1','A')
					)  
					SELECT COUNT(1) OVER(PARTITION BY YD_CAR_NO ) AS YD_WORK_COIL_MAX_CNT
					     , A.* 
					  FROM (
					        SELECT A.YD_CAR_SCH_ID
					             , A.STL_NO    AS YD_STL_NO
					             , A.STOP_LOC  AS YD_PT_LOAD_LOC
					             , A.CAR_NO    AS YD_CAR_NO
					             , A.PT_CLS    AS YD_PT_CLS
					             , (CASE WHEN A.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN  '4'
					                     WHEN A.YD_CAR_PROG_STAT IN ('A','B','C','D','E') THEN  '3'
					                      END) YD_WORK_CLS
					             , '    '              AS YD_PT_WTH
					             , '    '              AS YD_PT_LEN
					             , '    '              AS YD_PT_HEIGHT
					             , '    '              AS YD_RAIN_CLS
					             , A.STOP_LOC          AS YD_CURR_BAY_GP -- 동정보(차량적재위치)
					             , A.YD_CAR_UPP_LOC_CD AS YD_LOAD_LOC_CD -- 상차위치코드
					             , C.COIL_WT           AS YD_COIL_WT -- 중량
					             , C.COIL_T            AS YD_COIL_T  -- 두께
					             , C.COIL_W            AS YD_COIL_W  -- 폭
					             , C.COIL_LEN          AS YD_COIL_LEN -- 길이
					             , C.COIL_OUTDIA       AS YD_COIL_OUTDIA -- 외경
					             , C.COIL_INDIA        AS YD_COIL_INDIA -- 내경
					             ,(CASE WHEN A.YD_CAR_PROG_STAT IN('5','E') THEN '*'
					                    WHEN A.YD_CAR_PROG_STAT IN('1','2','3','4')  AND C.YD_EQP_GP = 'PT' THEN '*'
					                    WHEN A.YD_CAR_PROG_STAT IN('A','B','C','D')  AND C.YD_GP IN ('0','1','2','3','H','J') AND C.YD_EQP_GP BETWEEN '00' AND '99'  THEN '*'
					                    ELSE '0'
					                END) AS YD_WORK_STATE
					             , YD_CAR_PROG_STAT
					             , YD_GP
					          FROM(
					                SELECT D.STL_NO 
					                     , A.CAR_NO 
					                     , (CASE WHEN YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN A.YD_CARLD_STOP_LOC ELSE A.YD_CARUD_STOP_LOC END) AS STOP_LOC 
					                     , (CASE WHEN YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN D.YD_CAR_UPP_LOC_CD  
					                             ELSE (SELECT YD_STK_BED_NO 
					                                     FROM TB_YD_CARFTMVMTL 
					                                    WHERE YD_CAR_SCH_ID = A.YD_CAR_SCH_ID
					                                      AND STL_NO = D.STL_NO )
					                                      END ) AS YD_CAR_UPP_LOC_CD
					                     , LPAD((CASE WHEN A.CAR_KIND IS NULL THEN SUBSTR(A.TRN_EQP_CD,2,2) ELSE A.CAR_KIND END),2,' ') AS PT_CLS
					                     , A.YD_CAR_WRK_GP
					                     , A.YD_CAR_PROG_STAT
					                     , A.YD_CAR_USE_GP
					                     , A.CAR_KIND
					                     , A.YD_CAR_SCH_ID
					                  FROM USRYDA.TB_YD_CARSCH A
					                     , TB_YD_STOCK D
					                     , TEMP_PARAM E
					                 WHERE 1=1
					                   AND A.TRANS_ORD_DATE||A.TRANS_ORD_SEQNO = D.CAR_FRTOMOVE_WORD_NO
					                   AND A.CAR_NO = D.COIL_CAR_NO
					                   AND A.YD_CAR_SCH_ID = E.YD_CAR_SCH_ID
					                   AND A.YD_CAR_USE_GP = 'G'
					                   AND A.DEL_YN ='N'
					                   --차량동간이적
					                   AND A.YD_CAR_WRK_GP = 'G'
					               ) A
					             , TB_PT_COILCOMM C
					         WHERE A.STL_NO = C.COIL_NO  
					      ) A  
					 ORDER BY YD_WORK_STATE,YD_LOAD_LOC_CD 
					*/
					jrCarInfo = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschCarGetInWorkByCarMove", logId, mthdNm, "차량동간이적 차량예정정보 조회");
				} else {
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschCarGetInWorkInfoCarMove
					WITH TEMP_PARAM AS (
					SELECT   :V_YD_CAR_SCH_ID AS YD_CAR_SCH_ID FROM DUAL
					)  
					SELECT COUNT(1) OVER(PARTITION BY YD_CAR_NO ) AS YD_WORK_COIL_MAX_CNT
					     , A.* 
					  FROM (
					        SELECT A.YD_CAR_SCH_ID
					             , A.STL_NO    AS YD_STL_NO
					             , A.STOP_LOC  AS YD_PT_LOAD_LOC
					             , A.CAR_NO    AS YD_CAR_NO
					             , A.PT_CLS    AS YD_PT_CLS
					             , (CASE WHEN A.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN  '4'
					                     WHEN A.YD_CAR_PROG_STAT IN ('A','B','C','D','E') THEN  '3'
					                      END) YD_WORK_CLS
					             , '    '              AS YD_PT_WTH
					             , '    '              AS YD_PT_LEN
					             , '    '              AS YD_PT_HEIGHT
					             , '    '              AS YD_RAIN_CLS
					             , A.STOP_LOC          AS YD_CURR_BAY_GP -- 동정보(차량적재위치)
					             , A.YD_CAR_UPP_LOC_CD AS YD_LOAD_LOC_CD -- 상차위치코드
					             , C.COIL_WT           AS YD_COIL_WT -- 중량
					             , C.COIL_T            AS YD_COIL_T  -- 두께
					             , C.COIL_W            AS YD_COIL_W  -- 폭
					             , C.COIL_LEN          AS YD_COIL_LEN -- 길이
					             , C.COIL_OUTDIA       AS YD_COIL_OUTDIA -- 외경
					             , C.COIL_INDIA        AS YD_COIL_INDIA -- 내경
					             ,(CASE WHEN YD_CAR_PROG_STAT IN('5','E') THEN '*'
					                    WHEN YD_CAR_PROG_STAT IN('1','2','3','4') AND YD_EQP_GP = 'PT' THEN '*'
					                    WHEN YD_CAR_PROG_STAT IN('A','B','C','D')  AND C.YD_GP IN ('0','1','2','3','H','J')  AND YD_EQP_GP BETWEEN '00' AND '99'  THEN '*'
					                    ELSE '0'
					                END) AS YD_WORK_STATE
					             , YD_CAR_PROG_STAT
					             , YD_GP
					          FROM(
					                SELECT D.STL_NO 
					                     , A.CAR_NO 
					                     , (CASE WHEN YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN YD_CARLD_STOP_LOC ELSE YD_CARUD_STOP_LOC END) AS STOP_LOC 
					                     , (CASE WHEN YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN D.YD_CAR_UPP_LOC_CD  
					                             ELSE (SELECT YD_STK_BED_NO 
					                                     FROM TB_YD_CARFTMVMTL 
					                                    WHERE YD_CAR_SCH_ID = A.YD_CAR_SCH_ID
					                                      AND STL_NO = D.STL_NO )
					                                      END ) AS YD_CAR_UPP_LOC_CD
					                     , LPAD((CASE WHEN A.CAR_KIND IS NULL THEN SUBSTR(A.TRN_EQP_CD,2,2) ELSE A.CAR_KIND END),2,' ') AS PT_CLS
					                     , A.YD_CAR_WRK_GP
					                     , A.YD_CAR_PROG_STAT
					                     , A.YD_CAR_USE_GP
					                     , A.CAR_KIND
					                     , A.YD_CAR_SCH_ID
					                  FROM USRYDA.TB_YD_CARSCH A
					                     , TB_YD_STOCK D
					                     , TEMP_PARAM E
					                 WHERE 1=1
					                   AND A.TRANS_ORD_DATE||A.TRANS_ORD_SEQNO = D.CAR_FRTOMOVE_WORD_NO
					                   AND A.CAR_NO = D.COIL_CAR_NO
					                   AND A.YD_CAR_SCH_ID = E.YD_CAR_SCH_ID
					                   AND A.YD_CAR_USE_GP = 'G'
					                   AND A.DEL_YN ='N'
					                   AND (A.SPOS_WLOC_CD IN ('DJY1E','DJY21','DJY22') OR A.ARR_WLOC_CD  IN ('DJY1E','DJY21','DJY22') )
					                   --차량동간이적
					                   AND A.YD_CAR_WRK_GP = 'G'
					               ) A
					             , TB_PT_COILCOMM C
					         WHERE A.STL_NO = C.COIL_NO  
					      ) A  
					 ORDER BY YD_WORK_STATE,YD_LOAD_LOC_CD 
					 */                                  
					jrCarInfo = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschCarGetInWorkInfoCarMove", logId, mthdNm, "차량동간이적 차량예정정보 조회");
					
				}
			} else if ("".equals(ydCarSchId)) {
			// 차량 동간이적 제외	
				//상차위치로 차량예정정보 조회
				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschCarGetInWorkByCarNo_PIDEV 
				WITH TEMP_PARRAM AS (
				SELECT MAX(YD_CAR_SCH_ID) AS YD_CAR_SCH_ID
				  FROM USRYDA.TB_YD_CARSCH
				 WHERE (CASE WHEN YD_CAR_PROG_STAT IN ('2','3','4','5') THEN YD_CARLD_STOP_LOC
				             ELSE YD_CARUD_STOP_LOC END) = :V_YD_CARUD_STOP_LOC
				   AND DEL_YN = 'N'
				   AND YD_CAR_PROG_STAT NOT IN ('1','A')
				)  
				, TEMP_TABLE AS (
				--//상차대상
				SELECT A.STL_NO 
				     , B.CAR_NO
				     , B.CAR_KIND
				     , B.TRN_EQP_CD
				     , YD_CARLD_STOP_LOC AS STOP_LOC                                -- 상차
				     , A.YD_CAR_UPP_LOC_CD
				     , B.YD_CAR_WRK_GP
				     , B.YD_CAR_PROG_STAT
				     , B.YD_CAR_USE_GP
				     , B.YD_CAR_SCH_ID
				     , C.CMBN_CARLD_NO
				     , C.TRANS_WORD_DATE
				     , C.TRANS_WORD_SEQNO 
				     , A.TRANS_ORD_DATE
				     , A.TRANS_ORD_SEQNO
				  FROM TB_YD_STOCK A
				     , TB_YD_CARSCH B
				     , TB_DM_TRANSWORDCOMM @DL_SMDB  C
				     , TB_PT_COILCOMM E
				     , TEMP_PARRAM D
				 WHERE A.TRANS_ORD_DATE = B.TRANS_ORD_DATE
				   AND A.TRANS_ORD_SEQNO= B.TRANS_ORD_SEQNO
				   AND B.YD_CAR_SCH_ID  = D.YD_CAR_SCH_ID
				   AND A.STL_NO         = E.COIL_NO
				   AND A.TRANS_ORD_DATE = C.TRANS_WORD_DATE(+)
				   AND A.TRANS_ORD_SEQNO= C.TRANS_WORD_SEQNO(+) 
				   AND E.YD_BAY_GP      = SUBSTR(YD_CARLD_STOP_LOC,2,1)
				    --차량동간이적 제외
				   AND B.YD_CAR_WRK_GP NOT IN ('G')
				   AND B.YD_CAR_USE_GP = 'G'
				   AND B.SPOS_WLOC_CD  IN('DJY21','DJY22','DJY1E')
				   AND YD_CAR_PROG_STAT IN ('1','2','3','4','5') 
				 UNION ALL
				   --//하차대상
				SELECT F.STL_NO AS STL_NO
				     , B.CAR_NO
				     , B.CAR_KIND
				     , B.TRN_EQP_CD
				     , YD_CARUD_STOP_LOC AS STOP_LOC                                -- 하차
				     , F.YD_STK_BED_NO AS    YD_CAR_UPP_LOC_CD    
				     , B.YD_CAR_WRK_GP
				     , B.YD_CAR_PROG_STAT
				     , B.YD_CAR_USE_GP
				     , B.YD_CAR_SCH_ID
				     , '' AS CMBN_CARLD_NO
				     , B.TRANS_ORD_DATE AS TRANS_WORD_DATE
				     , B.TRANS_ORD_SEQNO AS TRANS_WORD_SEQNO 
				     , B.TRANS_ORD_DATE
				     , B.TRANS_ORD_SEQNO
				  FROM TB_YD_CARSCH B
				     , TEMP_PARRAM D
				     , TB_YD_CARFTMVMTL F
				 WHERE B.YD_CAR_SCH_ID =D.YD_CAR_SCH_ID
				   AND B.YD_CAR_SCH_ID =F.YD_CAR_SCH_ID  
				   --차량동간이적 제외
				   AND NVL(B.YD_CAR_WRK_GP,' ') NOT IN ('G')
				   AND B.YD_CAR_USE_GP = 'G'
				   AND B.ARR_WLOC_CD   IN('DJY21','DJY22','DJY1E')
				   AND B.YD_CAR_PROG_STAT IN('A','B','C','D','E') 
				)
				, TEMP_TRANSWORDCOMM AS (
				 SELECT C.TRANS_WORD_DATE
				      , C.TRANS_WORD_SEQNO
				   FROM TB_DM_TRANSWORDCOMM @DL_SMDB C
				      , TEMP_TABLE D
				  WHERE C.CMBN_CARLD_NO=D.CMBN_CARLD_NO
				)  

				SELECT COUNT(1) OVER(PARTITION BY YD_CAR_NO ) AS YD_WORK_COIL_MAX_CNT
				     , A.* 
				  FROM
				       (
				          SELECT A.YD_CAR_SCH_ID
				               , A.STL_NO    AS YD_STL_NO
				               , (CASE WHEN CHK = 1 THEN (SELECT STOP_LOC FROM TEMP_TABLE WHERE ROWNUM <= 1) ELSE STOP_LOC END)  AS YD_PT_LOAD_LOC
				               , A.CAR_NO    AS YD_CAR_NO
				               , A.PT_CLS    AS YD_PT_CLS
				               , (CASE WHEN A.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN 
				                            CASE WHEN A.YD_CAR_WRK_GP IN ('G') THEN '4' --차량동간이적
				                                 WHEN A.YD_CAR_USE_GP = 'G' AND (A.CAR_KIND = 'TT' OR A.CAR_KIND = 'PT') THEN '4'
				                                 WHEN A.YD_CAR_USE_GP = 'G' THEN '2' -- 출하출고
				                                 ELSE '4'  
				                            END   -- 구내출고 
				                       WHEN A.YD_CAR_PROG_STAT IN ('A','B','C','D','E') THEN
				                            CASE WHEN A.YD_CAR_WRK_GP IN ('G') THEN '4'--차량동간이적
				                                 WHEN A.YD_CAR_USE_GP = 'G' AND (A.CAR_KIND = 'TT' OR A.CAR_KIND = 'PT') THEN '3'
				                                 WHEN A.YD_CAR_USE_GP = 'G' THEN '1' -- 출하입고
				                                 ELSE '3' 
				                            END -- 구내입고
				                  END) AS YD_WORK_CLS
				               , '    '             AS YD_PT_WTH
				               , '    '             AS YD_PT_LEN
				               , '    '             AS YD_PT_HEIGHT
				               , '    '             AS YD_RAIN_CLS
				               
				               , A.STOP_LOC          AS YD_CURR_BAY_GP -- 동정보(차량적재위치)
				               , NVL(A.YD_CAR_UPP_LOC_CD, LPAD(ROWNUM,2,'0')) AS YD_LOAD_LOC_CD-- 상차위치코드
				               , C.COIL_WT          AS YD_COIL_WT -- 중량
				               , C.COIL_T           AS YD_COIL_T  -- 두께
				               , C.COIL_W           AS YD_COIL_W  -- 폭
				               , C.COIL_LEN         AS YD_COIL_LEN -- 길이
				               , C.COIL_OUTDIA      AS YD_COIL_OUTDIA -- 외경
				               , C.COIL_INDIA       AS YD_COIL_INDIA -- 내경
				               , (CASE WHEN CHK2 = 2 THEN '*' 
				                       WHEN YD_CAR_PROG_STAT IN('5','E') THEN '*'
				                       WHEN YD_CAR_PROG_STAT IN('1','2','3','4') AND C.YD_EQP_GP = 'PT' THEN '*'
				                       WHEN YD_CAR_PROG_STAT IN('A','B','C','D') AND C.YD_GP IN ('0','1','2','3','H','J') AND C.YD_EQP_GP = 'PT'
				                        AND (SELECT CM.DEL_YN
				                               FROM TB_YD_CARFTMVMTL   CM
				                              WHERE CM.YD_CAR_SCH_ID = A.YD_CAR_SCH_ID
				                                AND CM.STL_NO = A.STL_NO
				                            ) = 'Y' THEN '*'
				                       WHEN YD_CAR_PROG_STAT IN('A','B','C','D') AND C.YD_GP IN ('0','1','2','3','H','J') AND C.YD_EQP_GP BETWEEN '00' AND '99' THEN '*'
				                       ELSE '0'
				                   END) AS YD_WORK_STATE
				               , CHK2 
				               , CHK
				               , A.YD_CAR_PROG_STAT
				               , C.YD_GP
				            FROM (
				                    --출하
				                    SELECT  1 AS CHK 
				                         , A.STL_NO
				                         , CAR_NO
				                         , STOP_LOC
				                         , YD_CAR_UPP_LOC_CD
				                         , LPAD((CASE WHEN CAR_KIND IS NULL THEN SUBSTR(TRN_EQP_CD,2,2) ELSE CAR_KIND END),2,' ') AS PT_CLS
				                         , YD_CAR_WRK_GP
				                         , YD_CAR_PROG_STAT
				                         , YD_CAR_USE_GP
				                         , CAR_KIND
				                         , YD_CAR_SCH_ID
				                         , CHK2
				                      FROM ( --//일반차량
				                            SELECT 1 AS CHK2
				                                 , STL_NO 
				                                 , CAR_NO
				                                 , CAR_KIND
				                                 , TRN_EQP_CD
				                                 , STOP_LOC                                -- 하차
				                                 , YD_CAR_UPP_LOC_CD    
				                                 , YD_CAR_WRK_GP
				                                 , YD_CAR_PROG_STAT
				                                 , YD_CAR_USE_GP
				                                 , YD_CAR_SCH_ID 
				                              FROM TEMP_TABLE
				                            UNION ALL
				                            --//복수상차완료 차량(조합상차번호)
				                            SELECT  
				                                   2 AS CHK2
				                                 , A.STL_NO 
				                                 , B.CAR_NO
				                                 , B.CAR_KIND
				                                 , B.TRN_EQP_CD
				                                 , (CASE WHEN YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN YD_CARLD_STOP_LOC  -- 상차
				                                         ELSE YD_CARUD_STOP_LOC END) AS STOP_LOC                                -- 하차
				--                                 , (CASE WHEN (B.CAR_KIND = 'TT' OR A.CR_FRTOMOVE_GP IN ('81','63')) THEN (SELECT YD_STK_BED_NO 
				--                                                                                                             FROM TB_YD_STKBED C
				--                                                                                                            WHERE C.YD_STK_COL_GP='JAPT01'
				--                                                                                                              AND C.YD_CAR_UPP_LOC_CD=A.YD_CAR_UPP_LOC_CD
				--                                                                                                          )
				--                                       ELSE A.YD_CAR_UPP_LOC_CD END
				--                                    )AS    YD_CAR_UPP_LOC_CD   
				                                 , A.YD_CAR_UPP_LOC_CD
				                                 , B.YD_CAR_WRK_GP
				                                 , YD_CAR_PROG_STAT
				                                 , YD_CAR_USE_GP
				                                 , B.YD_CAR_SCH_ID                    
				                              FROM TB_YD_STOCK        A
				                                 , TB_YD_CARSCH       B  
				                                 , TB_PT_COILCOMM     E
				                                 , TEMP_TRANSWORDCOMM F
				                             WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
				                               AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
				                               AND A.TRANS_ORD_DATE  = F.TRANS_WORD_DATE
				                               AND A.TRANS_ORD_SEQNO = F.TRANS_WORD_SEQNO
				                               AND A.STL_NO = E.COIL_NO
				                               AND B.YD_CAR_SCH_ID >= TO_CHAR(SYSDATE-2,'YYYYMMDD')||'%'
				                               AND E.YD_BAY_GP = SUBSTR((CASE WHEN B.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN B.YD_CARLD_STOP_LOC ELSE B.YD_CARUD_STOP_LOC END),2,1)
				                               AND B.DEL_YN = 'Y' 
				                               AND B.YD_CAR_PROG_STAT = '5'
				                               AND B.YD_CAR_USE_GP ='G' 
				                               --차량동간이적 제외
				                               AND NVL(B.YD_CAR_WRK_GP,' ') NOT IN ('G')
				                               AND A.STL_NO NOT IN (SELECT STL_NO FROM TEMP_TABLE)
				                               AND B.YD_CAR_SCH_ID  NOT IN (SELECT YD_CAR_SCH_ID FROM TEMP_TABLE)
				                             
				                             UNION ALL
				                             --//복수상차완료 차량(운송지시번호 동일한경우)
				                             SELECT 3 AS CHK2
				                                  , A.STL_NO 
				                                  , B.CAR_NO
				                                  , B.CAR_KIND
				                                  , B.TRN_EQP_CD
				                                  , (CASE WHEN YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN YD_CARLD_STOP_LOC  -- 상차
				                                          ELSE YD_CARUD_STOP_LOC END) AS STOP_LOC                                -- 하차
				--                                  , (CASE WHEN (B.CAR_KIND='TT' OR A.CR_FRTOMOVE_GP IN ('81','63')  ) THEN (SELECT YD_STK_BED_NO 
				--                                                                                              FROM TB_YD_STKBED C
				--                                                                                             WHERE C.YD_STK_COL_GP='JAPT01'
				--                                                                                               AND C.YD_CAR_UPP_LOC_CD=A.YD_CAR_UPP_LOC_CD
				--                                                                                              )
				--                                      ELSE A.YD_CAR_UPP_LOC_CD END
				--                                     )AS    YD_CAR_UPP_LOC_CD 
				                                  , A.YD_CAR_UPP_LOC_CD
				                                  , B.YD_CAR_WRK_GP
				                                  , YD_CAR_PROG_STAT
				                                  , YD_CAR_USE_GP
				                                  , B.YD_CAR_SCH_ID                    
				                              FROM TB_YD_STOCK A
				                                 , TB_YD_CARSCH B 
				                                 , TB_PT_COILCOMM E
				                             WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
				                               AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
				                               AND A.STL_NO = E.COIL_NO
				                               AND B.YD_CAR_SCH_ID NOT IN (SELECT YD_CAR_SCH_ID FROM TEMP_TABLE)
				                               AND A.STL_NO NOT IN (SELECT STL_NO FROM TEMP_TABLE)
				                               AND E.YD_BAY_GP = SUBSTR((CASE WHEN B.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN B.YD_CARLD_STOP_LOC ELSE B.YD_CARUD_STOP_LOC END),2,1)
				                               AND B.DEL_YN = 'Y'
				                               AND A.DEL_YN = 'Y'
				                               AND B.YD_CAR_PROG_STAT = '5'
				                               --차량동간이적 제외
				                               AND NVL(B.YD_CAR_WRK_GP,' ') NOT IN ('G')
				                               AND B.YD_CAR_USE_GP = 'G' 
				                               AND (A.TRANS_ORD_DATE, A.TRANS_ORD_SEQNO) IN ( SELECT TRANS_ORD_DATE ,TRANS_ORD_SEQNO
				                                                                               FROM TEMP_TABLE
				                                                                             )
				                                                                                
				                             UNION ALL
				                             --//복수상차완료 차량(복수창고(AB야드)운송지시번호 동일한경우)
				                             SELECT 4 AS CHK2
				                                  , A.STOCK_ID AS STL_NO 
				                                  , B.CAR_NO
				                                  , B.CAR_KIND
				                                  , B.TRN_EQP_CD
				                                  , (CASE WHEN YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN YD_CARLD_STOP_LOC  -- 상차
				                                          ELSE YD_CARUD_STOP_LOC END) AS STOP_LOC                                -- 하차
				--                                  , (CASE WHEN (B.CAR_KIND='TT' OR A.CR_FRTOMOVE_GP IN ('81','63')  ) THEN (SELECT YD_STK_BED_NO 
				--                                                                                               FROM TB_YD_STKBED C
				--                                                                                              WHERE C.YD_STK_COL_GP='JAPT01'
				--                                                                                                AND C.YD_CAR_UPP_LOC_CD=A.FRTOMOVE_EQUIP_BED_GP
				--                                                                                              )
				--                                      ELSE A.FRTOMOVE_EQUIP_BED_GP END
				--                                    )AS    YD_CAR_UPP_LOC_CD    
				                                  , A.FRTOMOVE_EQUIP_BED_GP AS YD_CAR_UPP_LOC_CD
				                                  , B.YD_CAR_WRK_GP
				                                  , YD_CAR_PROG_STAT
				                                  , YD_CAR_USE_GP
				                                  , B.YD_CAR_SCH_ID                    
				                               FROM TB_YM_STOCK A
				                                  , TB_YD_CARSCH B 
				                                  , TB_PT_COILCOMM E
				                              WHERE A.TRANS_ORD_DATE2  = B.TRANS_ORD_DATE
				                                AND A.TRANS_ORD_SEQNO2 = B.TRANS_ORD_SEQNO
				                                AND A.STOCK_ID=E.COIL_NO
				                                AND B.YD_CAR_SCH_ID NOT IN (SELECT YD_CAR_SCH_ID FROM TEMP_TABLE)
				                                AND E.YD_BAY_GP = SUBSTR((CASE WHEN B.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN B.YD_CARLD_STOP_LOC ELSE B.YD_CARUD_STOP_LOC END),2,1)
				                                AND B.DEL_YN = 'Y'
				                                AND A.DEL_YN = 'Y'
				                                AND B.YD_CAR_PROG_STAT ='5'
				                               --차량동간이적 제외
				                                AND NVL(B.YD_CAR_WRK_GP,' ') NOT IN ('G')
				                                AND B.YD_CAR_USE_GP='G' 
				                                AND A.STOCK_ID NOT IN (SELECT STL_NO FROM TEMP_TABLE)
				                                AND (A.TRANS_ORD_DATE2, A.TRANS_ORD_SEQNO2) IN ( SELECT TRANS_ORD_DATE ,TRANS_ORD_SEQNO
				                                                                               FROM TEMP_TABLE
				                                                                             )
				                ----------------------------------------------
				                             UNION ALL
				                             --//복수상차완료 차량(복수창고(AB야드)운송지시번호 동일한경우) - 박판
				                             SELECT 4 AS CHK2
				                                  , A.STL_NO AS STL_NO 
				                                  , B.CAR_NO
				                                  , B.CAR_KIND
				                                  , B.TRN_EQP_CD
				                                  , (CASE WHEN YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN YD_CARLD_STOP_LOC  -- 상차
				                                          ELSE YD_CARUD_STOP_LOC END) AS STOP_LOC                                -- 하차
				--                                  , (CASE WHEN (B.CAR_KIND = 'TT' OR A.CR_FRTOMOVE_GP = '81') THEN (SELECT ITEM FROM TB_YF_RULE CC WHERE CC.REPR_CD_GP='YM1001' AND CC.CD_GP = A.YD_CAR_UPP_LOC_CD)     --TTCar위치변환
				--                                          ELSE A.YD_CAR_UPP_LOC_CD END
				--                                    )  AS YD_CAR_UPP_LOC_CD
				                                  , A.YD_CAR_UPP_LOC_CD AS YD_CAR_UPP_LOC_CD
				                                  , B.YD_CAR_WRK_GP
				                                  , YD_CAR_PROG_STAT
				                                  , YD_CAR_USE_GP
				                                  , B.YD_CAR_SCH_ID                    
				                               FROM TB_YF_STOCK    A
				                                  , TB_YD_CARSCH   B 
				                                  , TB_PT_COILCOMM E
				                              WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
				                                AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
				                                AND A.STL_NO = E.COIL_NO
				                                AND B.YD_CAR_SCH_ID NOT IN (SELECT YD_CAR_SCH_ID FROM TEMP_TABLE)
				                                AND E.YD_BAY_GP=SUBSTR((CASE WHEN B.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN B.YD_CARLD_STOP_LOC ELSE B.YD_CARUD_STOP_LOC END),2,1)
				                                AND B.DEL_YN = 'Y'
				                                AND A.DEL_YN = 'Y'
				                                AND B.YD_CAR_PROG_STAT = '5'
				                               --차량동간이적 제외
				                                AND NVL(B.YD_CAR_WRK_GP,' ') NOT IN ('G')
				                                AND B.YD_CAR_USE_GP = 'G' 
				                                AND A.STL_NO NOT IN (SELECT STL_NO FROM TEMP_TABLE)
				                                AND (A.TRANS_ORD_DATE||A.TRANS_ORD_SEQNO) IN ( SELECT TRANS_ORD_DATE||TRANS_ORD_SEQNO
				                                                                               FROM TEMP_TABLE
				                                                                             )
				                               
				                          ) A
				                    UNION ALL
				                    --구내운송 하차
				                    SELECT 2 AS CHK
				                         , D.STL_NO 
				                         , A.TRN_EQP_CD
				                         , YD_CARUD_STOP_LOC  AS STOP_LOC 
				                         , LPAD(ROWNUM,2,'0') AS YD_CAR_UPP_LOC_CD
				                         , LPAD(SUBSTR(A.TRN_EQP_CD, 2, 2), 2, ' ') AS PT_CLS
				                         , A.YD_CAR_WRK_GP
				                         , YD_CAR_PROG_STAT
				                         , A.YD_CAR_USE_GP
				                         , A.CAR_KIND
				                         , A.YD_CAR_SCH_ID
				                         , 3 AS CHK2
				                      FROM TB_YD_CARSCH A
				                         , TB_YD_STOCK  D
				                         , TEMP_PARRAM  E
				                         , TB_YD_CARFTMVMTL CM
				                     WHERE A.YD_CAR_SCH_ID = E.YD_CAR_SCH_ID
				                       AND A.YD_CAR_USE_GP = 'L'
				                       AND A.YD_CAR_SCH_ID = CM.YD_CAR_SCH_ID
				                       AND D.STL_NO        = CM.STL_NO
				                       --차량동간이적 제외
				                       AND NVL(A.YD_CAR_WRK_GP,' ') NOT IN ('G')
				                       AND A.DEL_YN = 'N'
				--                       AND A.YD_CAR_SCH_ID NOT IN (SELECT YD_CAR_SCH_ID FROM TEMP_TABLE)
				                       AND (A.SPOS_WLOC_CD IN ('DJY21','DJY22', 'DJY1E') OR A.ARR_WLOC_CD IN ('DJY21','DJY22', 'DJY1E'))

				                     UNION ALL
				                    --구내운송 상차
				                    SELECT 2 AS CHK
				                         , D.STL_NO 
				                         , A.TRN_EQP_CD
				                         , YD_CARLD_STOP_LOC  AS STOP_LOC 
				                         , LPAD(ROWNUM,2,'0') AS YD_CAR_UPP_LOC_CD
				                         , LPAD(SUBSTR(A.TRN_EQP_CD, 2, 2), 2, ' ') AS PT_CLS
				                         , A.YD_CAR_WRK_GP
				                         , YD_CAR_PROG_STAT
				                         , A.YD_CAR_USE_GP
				                         , A.CAR_KIND
				                         , A.YD_CAR_SCH_ID
				                         , 3 AS CHK2
				                      FROM TB_YD_CARSCH A
				                         , TB_YD_STOCK  D
				                         , TEMP_PARRAM  E
				                     WHERE A.YD_CAR_SCH_ID = E.YD_CAR_SCH_ID
				                       AND A.YD_CAR_USE_GP = 'L'
				                       --차량동간이적 제외
				                       AND NVL(A.YD_CAR_WRK_GP,' ') NOT IN ('G')
				                       AND A.DEL_YN = 'N'
				--                       AND A.YD_CAR_SCH_ID NOT IN (SELECT YD_CAR_SCH_ID FROM TEMP_TABLE)
				                       AND (A.SPOS_WLOC_CD IN ('DJY21','DJY22', 'DJY1E') OR A.ARR_WLOC_CD IN ('DJY21','DJY22', 'DJY1E'))
				                       AND D.CAR_FRTOMOVE_WORD_NO = A.FRTOMOVE_WORD_NO
				                ) A
				            , TB_PT_COILCOMM C
				            WHERE A.STL_NO = C.COIL_NO  
				        ) A  
				 ORDER BY YD_WORK_STATE
				        , YD_LOAD_LOC_CD 

				 */		 
//PIDEV_S :병행가동용:PI_YD
				jrParam.setField("PI_YD",    	"J");						
				jrCarInfo = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschCarGetInWorkByCarNo_PIDEV", logId, mthdNm, "상차위치로 차량예정정보 조회");
			} else {
				//차량스케줄ID로 차량예정정보 조회
				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschCarGetInWorkInfo_PIDEV
				WITH TEMP_PARRAM AS (
				SELECT   :V_YD_CAR_SCH_ID AS YD_CAR_SCH_ID FROM DUAL
				)
				, TEMP_TABLE AS (
				--//상차대상
				SELECT A.STL_NO 
				     , B.CAR_NO
				     , B.CAR_KIND
				     , B.TRN_EQP_CD
				     , YD_CARLD_STOP_LOC AS STOP_LOC                                -- 상차
				     , (CASE WHEN (B.CAR_KIND='TT' OR A.CR_FRTOMOVE_GP IN ('81','63')  ) THEN (SELECT YD_STK_BED_NO 
				                                                                                 FROM TB_YD_STKBED CC
				                                                                                WHERE CC.YD_STK_COL_GP='JAPT01'
				                                                                                  AND CC.YD_CAR_UPP_LOC_CD=A.YD_CAR_UPP_LOC_CD
				                                                                              )
				             ELSE A.YD_CAR_UPP_LOC_CD END 
				       ) AS YD_CAR_UPP_LOC_CD    
				     , B.YD_CAR_WRK_GP
				     , B.YD_CAR_PROG_STAT
				     , B.YD_CAR_USE_GP
				     , B.YD_CAR_SCH_ID
				     , C.CMBN_CARLD_NO
				     , C.TRANS_WORD_DATE
				     , C.TRANS_WORD_SEQNO 
				     , A.TRANS_ORD_DATE
				     , A.TRANS_ORD_SEQNO
				  FROM TB_YD_STOCK A
				     , TB_YD_CARSCH B
				     , TB_DM_TRANSWORDCOMM @DL_SMDB  C
				     , TB_PT_COILCOMM E
				     , TEMP_PARRAM D
				 WHERE A.TRANS_ORD_DATE = B.TRANS_ORD_DATE
				   AND A.TRANS_ORD_SEQNO= B.TRANS_ORD_SEQNO
				   AND B.YD_CAR_SCH_ID  = D.YD_CAR_SCH_ID
				   AND A.STL_NO         = E.COIL_NO
				   AND A.TRANS_ORD_DATE = C.TRANS_WORD_DATE(+)
				   AND A.TRANS_ORD_SEQNO= C.TRANS_WORD_SEQNO(+) 
				   AND E.YD_BAY_GP      = SUBSTR(YD_CARLD_STOP_LOC,2,1)
				    --차량동간이적 제외
				   AND NVL(B.YD_CAR_WRK_GP,' ') NOT IN ('G')
				   AND B.YD_CAR_USE_GP='G'
				   AND B.SPOS_WLOC_CD IN  ('DJY21','DJY22','DJY1E')
				   AND YD_CAR_PROG_STAT IN ('1','2','3','4','5') 
				 UNION ALL
				   --//하차대상
				SELECT F.STL_NO AS STL_NO
				     , B.CAR_NO
				     , B.CAR_KIND
				     , B.TRN_EQP_CD
				     , YD_CARUD_STOP_LOC AS STOP_LOC                                -- 하차
				     , F.YD_STK_BED_NO AS    YD_CAR_UPP_LOC_CD    
				     , B.YD_CAR_WRK_GP
				     , B.YD_CAR_PROG_STAT
				     , B.YD_CAR_USE_GP
				     , B.YD_CAR_SCH_ID
				     , '' AS CMBN_CARLD_NO
				     , B.TRANS_ORD_DATE AS TRANS_WORD_DATE
				     , B.TRANS_ORD_SEQNO AS TRANS_WORD_SEQNO 
				     , B.TRANS_ORD_DATE
				     , B.TRANS_ORD_SEQNO
				  FROM TB_YD_CARSCH B
				     , TEMP_PARRAM D
				     , TB_YD_CARFTMVMTL F
				 WHERE B.YD_CAR_SCH_ID =D.YD_CAR_SCH_ID
				   AND B.YD_CAR_SCH_ID =F.YD_CAR_SCH_ID  
				   --차량동간이적 제외
				   AND NVL(B.YD_CAR_WRK_GP,' ') NOT IN ('G')
				   AND B.YD_CAR_USE_GP='G'
				   AND B.ARR_WLOC_CD  IN  ('DJY21','DJY22','DJY1E')
				   AND B.YD_CAR_PROG_STAT IN('A','B','C','D','E') 
				)
				, TEMP_TRANSWORDCOMM AS (
				 SELECT C.TRANS_WORD_DATE
				      , C.TRANS_WORD_SEQNO
				   FROM TB_DM_TRANSWORDCOMM @DL_SMDB C
				      , TEMP_TABLE D
				  WHERE C.CMBN_CARLD_NO=D.CMBN_CARLD_NO
				)  

				SELECT Count(1) Over(Partition By YD_CAR_NO ) AS YD_WORK_COIL_MAX_CNT
				     , A.* 
				  FROM 
				        (
				          SELECT A.YD_CAR_SCH_ID
				               , A.STL_NO  AS YD_STL_NO
				               , (CASE WHEN CHK=1 THEN (SELECT STOP_LOC FROM TEMP_TABLE WHERE ROWNUM<=1) ELSE STOP_LOC END)     AS YD_PT_LOAD_LOC
				               , A.CAR_NO    AS YD_CAR_NO
				               , A.PT_CLS    AS YD_PT_CLS
				               , (CASE WHEN A.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN 
				                            --차량동간이적
				                            CASE WHEN NVL(A.YD_CAR_WRK_GP,' ') IN ('G') THEN '4'
				                                 WHEN A.YD_CAR_USE_GP= 'G' AND ( A.CAR_KIND ='TT' OR A.CAR_KIND ='PT') THEN '4'
				                                 WHEN A.YD_CAR_USE_GP= 'G' THEN '2' -- 출하출고
				                                 ELSE '4'  
				                            END   -- 구내출고 
				                       WHEN A.YD_CAR_PROG_STAT IN ('A','B','C','D','E') THEN
				                            --차량동간이적
				                            CASE WHEN NVL(A.YD_CAR_WRK_GP,' ') IN ('G') THEN '4'
				                                 WHEN A.YD_CAR_USE_GP= 'G' AND ( A.CAR_KIND ='TT' OR A.CAR_KIND ='PT') THEN '3'
				                                 WHEN A.YD_CAR_USE_GP= 'G' THEN '1' -- 출하입고
				                                 ELSE '3' 
				                            END -- 구내입고
				                          
				                  END) YD_WORK_CLS
				               , '    '             AS YD_PT_WTH
				               , '    '             AS YD_PT_LEN
				               , '    '             AS YD_PT_HEIGHT
				               , '    '             AS YD_RAIN_CLS
				               
				               , A.STOP_LOC    AS YD_CURR_BAY_GP -- 동정보(차량적재위치)
				               , A.YD_CAR_UPP_LOC_CD    AS YD_CAR_UPP_LOC_CD -- 상차위치코드
				               , C.COIL_WT          AS YD_COIL_WT -- 중량
				               , C.COIL_T           AS YD_COIL_T  -- 두께
				               , C.COIL_W           AS YD_COIL_W  -- 폭
				               , C.COIL_LEN         AS YD_COIL_LEN -- 길이
				               , C.COIL_OUTDIA      AS YD_COIL_OUTDIA -- 외경
				               , C.COIL_INDIA       AS YD_COIL_INDIA -- 내경
				               , (CASE WHEN CHK2 =2 THEN '*' 
				                       WHEN YD_CAR_PROG_STAT IN('5','E') THEN '*'
				                       WHEN YD_CAR_PROG_STAT IN('1','2','3','4') AND YD_EQP_GP='PT' THEN '*'
				                       WHEN YD_CAR_PROG_STAT IN('A','B','C','D') AND C.YD_GP IN ('0','1','2','3','H','J') AND YD_EQP_GP BETWEEN '00' AND '99' THEN '*'
				                       ELSE '0'
				                   END) YD_WORK_STATE
				               , CHK2 
				               , CHK
				               , YD_CAR_PROG_STAT
				               , YD_GP
				            FROM (
				                    --출하
				                    SELECT  1 AS CHK ,  A.STL_NO
				                         , CAR_NO
				                         , STOP_LOC
				                         , YD_CAR_UPP_LOC_CD
				                         , LPAD((CASE WHEN CAR_KIND IS NULL THEN SUBSTR(TRN_EQP_CD,2,2) ELSE CAR_KIND END),2,' ') AS PT_CLS
				                         , YD_CAR_WRK_GP
				                         , YD_CAR_PROG_STAT
				                         , YD_CAR_USE_GP
				                         , CAR_KIND
				                         , YD_CAR_SCH_ID
				                         , CHK2
				                      FROM ( --//일반차량
				                            SELECT 1 AS CHK2
				                                 , STL_NO 
				                                 , CAR_NO
				                                 , CAR_KIND
				                                 , TRN_EQP_CD
				                                 , STOP_LOC                                -- 하차
				                                 , YD_CAR_UPP_LOC_CD    
				                                 , YD_CAR_WRK_GP
				                                 , YD_CAR_PROG_STAT
				                                 , YD_CAR_USE_GP
				                                 , YD_CAR_SCH_ID 
				                              FROM TEMP_TABLE
				                            UNION ALL
				                            --//복수상차완료 차량(조합상차번호)
				                            SELECT  
				                                   2 AS CHK2
				                                 , A.STL_NO 
				                                 , B.CAR_NO
				                                 , B.CAR_KIND
				                                 , B.TRN_EQP_CD
				                                 , (CASE WHEN YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN YD_CARLD_STOP_LOC  -- 상차
				                                         ELSE YD_CARUD_STOP_LOC END) AS STOP_LOC                                -- 하차
				                                 , (CASE WHEN (B.CAR_KIND='TT' OR A.CR_FRTOMOVE_GP IN ('81','63')  ) THEN (SELECT YD_STK_BED_NO 
				                                                                                               FROM TB_YD_STKBED C
				                                                                                              WHERE C.YD_STK_COL_GP='JAPT01'
				                                                                                                AND C.YD_CAR_UPP_LOC_CD=A.YD_CAR_UPP_LOC_CD
				                                                                                              )
				                                       ELSE A.YD_CAR_UPP_LOC_CD END
				                                    )AS    YD_CAR_UPP_LOC_CD    
				                                 , B.YD_CAR_WRK_GP
				                                 , YD_CAR_PROG_STAT
				                                 , YD_CAR_USE_GP
				                                 , B.YD_CAR_SCH_ID                    
				                              FROM TB_YD_STOCK A
				                                 , TB_YD_CARSCH B  
				                                 , TB_PT_COILCOMM E
				                                 , TEMP_TRANSWORDCOMM F
				                             WHERE A.TRANS_ORD_DATE=B.TRANS_ORD_DATE
				                               AND A.TRANS_ORD_SEQNO=B.TRANS_ORD_SEQNO
				                               AND A.TRANS_ORD_DATE=F.TRANS_WORD_DATE
				                               AND A.TRANS_ORD_SEQNO=F.TRANS_WORD_SEQNO
				                               AND A.STL_NO=E.COIL_NO
				                               AND B.YD_CAR_SCH_ID||'' >=TO_CHAR(SYSDATE-4,'YYYYMMDD')||'%'
				                               AND E.YD_BAY_GP=SUBSTR((CASE WHEN B.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN B.YD_CARLD_STOP_LOC ELSE B.YD_CARUD_STOP_LOC END),2,1)
				                               AND B.DEL_YN='Y' 
				                               AND B.YD_CAR_PROG_STAT ='5'
				                               AND B.YD_CAR_USE_GP='G' 
				                               --차량동간이적 제외
				                               AND NVL(B.YD_CAR_WRK_GP,' ') NOT IN ('G')
				                               AND A.STL_NO NOT IN (SELECT STL_NO FROM TEMP_TABLE)
				                               AND B.YD_CAR_SCH_ID||''  NOT IN (SELECT YD_CAR_SCH_ID FROM TEMP_TABLE)
				                             
				                             UNION ALL
				                             --//복수상차완료 차량(운송지시번호 동일한경우)
				                             SELECT 3 AS CHK2
				                                  , A.STL_NO 
				                                  , B.CAR_NO
				                                  , B.CAR_KIND
				                                  , B.TRN_EQP_CD
				                                  , (CASE WHEN YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN YD_CARLD_STOP_LOC  -- 상차
				                                          ELSE YD_CARUD_STOP_LOC END) AS STOP_LOC                                -- 하차
				                                  , (CASE WHEN (B.CAR_KIND='TT' OR A.CR_FRTOMOVE_GP IN ('81','63')  ) THEN (SELECT YD_STK_BED_NO 
				                                                                                              FROM TB_YD_STKBED C
				                                                                                             WHERE C.YD_STK_COL_GP='JAPT01'
				                                                                                               AND C.YD_CAR_UPP_LOC_CD=A.YD_CAR_UPP_LOC_CD
				                                                                                              )
				                                      ELSE A.YD_CAR_UPP_LOC_CD END
				                                     )AS    YD_CAR_UPP_LOC_CD    
				                                  , B.YD_CAR_WRK_GP
				                                  , YD_CAR_PROG_STAT
				                                  , YD_CAR_USE_GP
				                                  , B.YD_CAR_SCH_ID                    
				                              FROM TB_YD_STOCK A
				                                 , TB_YD_CARSCH B 
				                                 , TB_PT_COILCOMM E
				                             WHERE A.TRANS_ORD_DATE=B.TRANS_ORD_DATE
				                               AND A.TRANS_ORD_SEQNO=B.TRANS_ORD_SEQNO
				                               AND A.STL_NO=E.COIL_NO
				                               AND B.YD_CAR_SCH_ID NOT IN (SELECT YD_CAR_SCH_ID FROM TEMP_TABLE)
				                               AND A.STL_NO NOT IN (SELECT STL_NO FROM TEMP_TABLE)
				                               AND E.YD_BAY_GP=SUBSTR((CASE WHEN B.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN B.YD_CARLD_STOP_LOC ELSE B.YD_CARUD_STOP_LOC END),2,1)
				                               AND B.DEL_YN='Y'
				                               AND A.DEL_YN='Y'
				                               AND B.YD_CAR_PROG_STAT ='5'
				                               --차량동간이적 제외
				                               AND NVL(B.YD_CAR_WRK_GP,' ') NOT IN ('G')
				                               AND B.YD_CAR_USE_GP='G' 
				                               AND (A.TRANS_ORD_DATE, A.TRANS_ORD_SEQNO) IN ( SELECT TRANS_ORD_DATE ,TRANS_ORD_SEQNO
				                                                                               FROM TEMP_TABLE
				                                                                             )
				                                                                                
				                             UNION ALL
				                             --//복수상차완료 차량(복수창고(AB야드)운송지시번호 동일한경우)
				                             SELECT 4 AS CHK2
				                                  , A.STOCK_ID AS STL_NO 
				                                  , B.CAR_NO
				                                  , B.CAR_KIND
				                                  , B.TRN_EQP_CD
				                                  , (CASE WHEN YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN YD_CARLD_STOP_LOC  -- 상차
				                                          ELSE YD_CARUD_STOP_LOC END) AS STOP_LOC                                -- 하차
				                                  , (CASE WHEN (B.CAR_KIND='TT' OR A.CR_FRTOMOVE_GP IN ('81','63')  ) THEN (SELECT YD_STK_BED_NO 
				                                                                                               FROM TB_YD_STKBED C
				                                                                                              WHERE C.YD_STK_COL_GP='JAPT01'
				                                                                                                AND C.YD_CAR_UPP_LOC_CD=A.FRTOMOVE_EQUIP_BED_GP
				                                                                                              )
				                                      ELSE A.FRTOMOVE_EQUIP_BED_GP END
				                                    )AS    YD_CAR_UPP_LOC_CD    
				                                  , B.YD_CAR_WRK_GP
				                                  , YD_CAR_PROG_STAT
				                                  , YD_CAR_USE_GP
				                                  , B.YD_CAR_SCH_ID                    
				                               FROM TB_YM_STOCK A
				                                  , TB_YD_CARSCH B 
				                                  , TB_PT_COILCOMM E
				                              WHERE A.TRANS_ORD_DATE2=B.TRANS_ORD_DATE
				                                AND A.TRANS_ORD_SEQNO2=B.TRANS_ORD_SEQNO
				                                AND A.STOCK_ID=E.COIL_NO
				                                AND B.YD_CAR_SCH_ID NOT IN (SELECT YD_CAR_SCH_ID FROM TEMP_TABLE)
				                                AND E.YD_BAY_GP=SUBSTR((CASE WHEN B.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN B.YD_CARLD_STOP_LOC ELSE B.YD_CARUD_STOP_LOC END),2,1)
				                                AND B.DEL_YN='Y'
				                                AND A.DEL_YN='Y'
				                                AND B.YD_CAR_PROG_STAT ='5'
				                               --차량동간이적 제외
				                                AND NVL(B.YD_CAR_WRK_GP,' ') NOT IN ('G')
				                                AND B.YD_CAR_USE_GP='G' 
				                                AND A.STOCK_ID NOT IN (SELECT STL_NO FROM TEMP_TABLE)
				                                AND (A.TRANS_ORD_DATE2, A.TRANS_ORD_SEQNO2) IN ( SELECT TRANS_ORD_DATE ,TRANS_ORD_SEQNO
				                                                                               FROM TEMP_TABLE
				                                                                             )
				                               
				                          ) A
				                    UNION ALL
				                    --구내운송
				                    SELECT 2 AS CHK
				                         , D.STL_NO 
				                         , A.TRN_EQP_CD
				                         , (CASE WHEN YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN YD_CARLD_STOP_LOC ELSE YD_CARUD_STOP_LOC END) AS STOP_LOC 
				                         , LPAD(ROWNUM,2,'0') AS YD_CAR_UPP_LOC_CD
				                         , LPAD((CASE WHEN A.CAR_KIND IS NULL THEN SUBSTR(A.TRN_EQP_CD,2,2) ELSE A.CAR_KIND END),2,' ') AS PT_CLS
				                         , A.YD_CAR_WRK_GP
				                         , YD_CAR_PROG_STAT
				                         , A.YD_CAR_USE_GP
				                         , A.CAR_KIND
				                         , A.YD_CAR_SCH_ID
				                         , 3 AS CHK2
				                      FROM TB_YD_CARSCH A
				                         , TB_YD_STOCK  D
				                         , TEMP_PARRAM  E
				                     WHERE A.YD_CAR_SCH_ID = E.YD_CAR_SCH_ID
				                       AND A.YD_CAR_USE_GP = 'L'
				                       --차량동간이적 제외
				                       AND NVL(A.YD_CAR_WRK_GP,' ') NOT IN ('G')
				                       AND A.DEL_YN = 'N'
				                       AND A.YD_CAR_SCH_ID||'' NOT IN (SELECT YD_CAR_SCH_ID FROM TEMP_TABLE)
				                       AND D.CAR_FRTOMOVE_WORD_NO = A.FRTOMOVE_WORD_NO
				                    
				                ) A
				            , TB_PT_COILCOMM C
				            WHERE A.STL_NO = C.COIL_NO  
				        ) A  
				 ORDER BY YD_WORK_STATE,YD_CAR_UPP_LOC_CD 
				 */
//PIDEV_S :병행가동용:PI_YD
				jrParam.setField("PI_YD",    	"J");							
				jrCarInfo = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschCarGetInWorkInfo_PIDEV", logId, mthdNm, "차량스케줄ID로 차량예정정보 조회"); 
			}

			/**********************************************************
			* 2. 차량예정정보 송신
			**********************************************************/
			if (jrCarInfo.size() > 0) {
				
				jrCarInfo.first();
				
				JDTORecord jsCarInfo = JDTORecordFactory.getInstance().create();
				jsCarInfo.setRecord(jrCarInfo.getRecord());
				
				//차량작업 예정정보 전문 data setup
			    jrParam.setField("PT_LOAD_LOC"      	, commUtils.trim(jsCarInfo.getFieldString("YD_PT_LOAD_LOC")));   	// 상차도 위치				
			    jrParam.setField("CAR_NO"      			, commUtils.trim(jsCarInfo.getFieldString("YD_CAR_NO")));  			// 차량번호	
			    jrParam.setField("PT_CLS"       		, commUtils.trim(jsCarInfo.getFieldString("YD_PT_CLS")));   		// 차량구분				
			    jrParam.setField("WORK_CLS"      		, commUtils.trim(jsCarInfo.getFieldString("YD_WORK_CLS")));			// 작업구분  				
			    jrParam.setField("WORK_COIL_MAX_CNT"	, commUtils.trim(jsCarInfo.getFieldString("YD_WORK_COIL_MAX_CNT")));// 작업총 수량 				
	
			    for (int ii = 0; ii < jrCarInfo.size(); ii++) {
			    	
			    	jrParam.setField("STL_NO_"+ii       , commUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_STL_NO"))); 
			    	jrParam.setField("LOAD_LOC_CD_"+ii  , commUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_LOAD_LOC_CD")));
			    	jrParam.setField("WORK_STATE_"+ii   , commUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_WORK_STATE"))); //*:작업완료, 0:작업예정
				}
	
				//차량예정정보 백업 송신
			    jrRtn = commUtils.addSndData(jrRtn, this.getMsgL2("YDY5L008BackUp", jrParam));
			    
			} else {
				//빈 전문 생성
			    jrParam.setField("PT_LOAD_LOC"      	, ydLoadLoc);   // 상차도 위치				
			    jrParam.setField("CAR_NO"      			, "");  		// 차량번호	
			    jrParam.setField("PT_CLS"       		, "");   		// 차량구분				
			    jrParam.setField("WORK_CLS"      		, "");			// 작업구분  				
			    jrParam.setField("WORK_COIL_MAX_CNT"	, "0");			// 작업총 수량
			    
		    	jrParam.setField("STL_NO_0"	            , ""); 
		    	jrParam.setField("LOAD_LOC_CD_0"		, "");
		    	jrParam.setField("WORK_STATE_0"			, "");
				
		    	jrRtn = commUtils.addSndData(jrRtn, this.getMsgL2("YDY5L008BackUp", jrParam));
			}
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
    /**
	 * 오퍼레이션명 : 차량POINT 점유(procUpdYdTransOrdChangeNEW)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public String procUpdYdTransOrdChangeNEW(JDTORecord rcvMsg)throws DAOException  {
		String mthdNm = "차량POINT 점유[CCoilDAO.procUpdYdTransOrdChangeNEW] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {

			commUtils.printLog(logId, mthdNm, "S+");
			String sTrnEqpCd		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"));
			String sCarNo			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));
			String sCardNo			= commUtils.trim(rcvMsg.getFieldString("CARD_NO")); 
			String ydMakeCarPntCd	= commUtils.trim(rcvMsg.getFieldString("YD_MAKECARPNT_CD"));
			String sModifier		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
	 
		 	//------------------------------------------------------------------------------------------------------------
	    	//	차량포인트 도착상태 변경 처리
	    	//------------------------------------------------------------------------------------------------------------

			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		    jrParam.setField("YD_STK_COL_ACT_STAT", "L");
		    jrParam.setField("TRN_EQP_CD"		  , sTrnEqpCd);
		    jrParam.setField("CAR_NO"			  , sCarNo);
		    jrParam.setField("CARD_NO"			  , sCardNo);
		    jrParam.setField("YD_CARPNT_CD"		  , ydMakeCarPntCd);
			
		    /*
			UPDATE TB_YD_CARPOINT
			   SET YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
			     , TRN_EQP_CD          = :V_TRN_EQP_CD
			     , CAR_NO              = :V_CAR_NO
			     , CARD_NO             = :V_CARD_NO
			     , MOD_DDTT            = SYSDATE
			     , MODIFIER            = :V_MODIFIER
			 WHERE YD_CARPNT_CD        = :V_YD_CARPNT_CD
			   AND DEL_YN = 'N'
		    */
		    String sApp838 = this.ApplyYn(logId, mthdNm, "APP838", "J", "*"); //트랜잭션분리
		    if ("Y".equals(sApp838)) {
		    	commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarPointStat", logId, mthdNm, "TB_YD_CARPOINT 갱신New");
		    } else {
		    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarPointStat", logId, mthdNm, "TB_YD_CARPOINT 갱신");	
		    }
		    
		    commUtils.printLog(logId, mthdNm, "S-");
		    return CConstant.RETN_CD_SUCCESS;
		    
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
    /**
	 * 오퍼레이션명 : 차량스케줄 POINT 점유
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public String procUpdYdTransOrdChange(JDTORecord rcvMsg)throws DAOException  {
		String mthdNm = "차량스케줄POINT 점유[CCoilDAO.procUpdYdTransOrdChange] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {

			commUtils.printLog(logId, mthdNm, "S+");
	 
			String sNewYdCarSchId = commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"    ));
			String sNewWlocCd     = commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"     ));
			String sNewYdCarpntCd = commUtils.trim(rcvMsg.getFieldString("NEW_YD_CARPNT_CD" ));
			String sOldYdCarpntCd = commUtils.trim(rcvMsg.getFieldString("OLD_YD_CARPNT_CD" ));
			String sNewYdStkColGp = commUtils.trim(rcvMsg.getFieldString("YD_CARLD_STOP_LOC"));
			String sNewYdPntCd    = commUtils.trim(rcvMsg.getFieldString("NEW_YD_PNT_CD"    ));
			String ydCarWrkGp     = commUtils.trim(rcvMsg.getFieldString("YD_CAR_WRK_GP"    ));
			String ydEqpWrkStat   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_STAT"  ));//L:영차 U:공차

			String sModifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
			
		 	//------------------------------------------------------------------------------------------------------------
	    	//	차량스케줄 도착상태 변경 처리
	    	//------------------------------------------------------------------------------------------------------------

			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		    jrParam.setField("YD_CAR_SCH_ID", sNewYdCarSchId);

		    if ("L".equals(ydEqpWrkStat)) {
		    	jrParam.setField("YD_CARUD_ARR_DT"  , commUtils.getDateTime14());
		    	jrParam.setField("YD_CAR_PROG_STAT" , "B"); //하차도착상태
		    	jrParam.setField("ARR_WLOC_CD"      , sNewWlocCd    );												
				jrParam.setField("YD_PNT_CD3"       , sNewYdPntCd   ); 
				jrParam.setField("YD_CARUD_STOP_LOC", sNewYdStkColGp);
		    } else {
		    	jrParam.setField("YD_CARLD_ARR_DT"  , commUtils.getDateTime14());
		    	jrParam.setField("YD_CAR_PROG_STAT" , "2"); //상차도착상태
		    	jrParam.setField("SPOS_WLOC_CD"     , sNewWlocCd    ); 												
				jrParam.setField("YD_PNT_CD1"       , sNewYdPntCd   ); 
				jrParam.setField("YD_CARLD_STOP_LOC", sNewYdStkColGp);
		    }
		    
		    
		    /*
			UPDATE TB_YD_CARSCH
			   SET MODIFIER         = :V_MODIFIER
			     , MOD_DDTT         = SYSDATE
			     --하차
			     , YD_CAR_PROG_STAT     = NVL(:V_YD_CAR_PROG_STAT    , YD_CAR_PROG_STAT)
			     , YD_CARUD_WRK_BOOK_ID = NVL(:V_YD_CARUD_WRK_BOOK_ID, YD_CARUD_WRK_BOOK_ID)
			     , YD_CARUD_STOP_LOC    = NVL(:V_YD_CARUD_STOP_LOC   , YD_CARUD_STOP_LOC)
			     , YD_CARUD_ARR_DT      = DECODE(:V_YD_CARUD_ARR_DT,NULL,YD_CARUD_ARR_DT, TO_DATE(:V_YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS')) 
			     , YD_PNT_CD3           = NVL(:V_YD_PNT_CD3          , YD_PNT_CD3)
			     , ARR_WLOC_CD          = NVL(:V_ARR_WLOC_CD         , ARR_WLOC_CD)
			     --상차
			     , YD_CARLD_WRK_BOOK_ID = NVL(:V_YD_CARLD_WRK_BOOK_ID, YD_CARLD_WRK_BOOK_ID)
			     , YD_CARLD_STOP_LOC    = NVL(:V_YD_CARLD_STOP_LOC   , YD_CARLD_STOP_LOC)
			     , YD_CARLD_ARR_DT      = DECODE(:V_YD_CARLD_ARR_DT,NULL,YD_CARLD_ARR_DT, TO_DATE(:V_YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS'))
			     , YD_PNT_CD1           = NVL(:V_YD_PNT_CD1          , YD_PNT_CD1)
			     , SPOS_WLOC_CD         = NVL(:V_SPOS_WLOC_CD        , SPOS_WLOC_CD)
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  
		     */
		    commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarschArrStat", logId, mthdNm, "TB_YD_CARPOINT 갱신");
		    
		    commUtils.printLog(logId, mthdNm, "S-");
		    return CConstant.RETN_CD_SUCCESS;
		    
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	
	/**
	 * 오퍼레이션명 : 출하시 맵활성화
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public String procYdLayerOpen(JDTORecord rcvMsg)throws DAOException  {
		String mthdNm = "출하시 맵 활성화[CCoilDAO.procYdLayerOpen] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		int intRtnVal = 0;
		
		try{
			commUtils.printLog(logId, mthdNm, "S+");
		    	
			String ydStackColGp		= commUtils.trim(rcvMsg.getFieldString("YD_STK_COL_GP"   ));	 
			String ydCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"          ));	 
			String ydCardNo 		= commUtils.trim(rcvMsg.getFieldString("CARD_NO"         ));	
			String sTrnEqpCd 		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"      ));
			String ydGp             = commUtils.trim(rcvMsg.getFieldString("YD_GP"           ));
		 	String sTranOrdDate     = commUtils.trim(rcvMsg.getFieldString("TRANS_WORD_DATE" ));
		 	String sTransOrdSeqno   = commUtils.trim(rcvMsg.getFieldString("TRANS_WORD_SEQNO"));	
			String sModifier     	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"        ));
			
			String sApp838 = this.ApplyYn(logId, mthdNm, "APP838", "J", "*"); //트랜잭션분리
		    
			//------------------------------------------------------------------------------------------------------------
	    	//	적치열 테이블에 활성상태 처리 
	    	//------------------------------------------------------------------------------------------------------------
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_STK_COL_GP"		, ydStackColGp);
			jrParam.setField("YD_STK_COL_ACT_STAT"  , "L");
			jrParam.setField("YD_CAR_USE_GP"      	, "G");
			jrParam.setField("TRN_EQP_CD"         	, sTrnEqpCd);
			jrParam.setField("CAR_NO"             	, ydCarNo);
			jrParam.setField("CARD_NO"            	, ydCardNo);
			
	    	/*
			UPDATE TB_YD_STKCOL
			   SET MODIFIER      = :V_MODIFIER
			     , MOD_DDTT      = SYSDATE
			     , TRN_EQP_CD    = :V_TRN_EQP_CD        
			     , YD_CAR_USE_GP = :V_YD_CAR_USE_GP
			     , CAR_NO        = :V_CAR_NO
			     , CARD_NO       = :V_CARD_NO
			     , YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT     
			WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
	    	 */
			if ("Y".equals(sApp838)) {
				commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkColCarClear", logId, mthdNm, "TB_YD_STACKCOL 등록");
		    } else {
		    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkColCarClear", logId, mthdNm, "TB_YD_STACKCOL 등록");	
		    }
	    	
	    	
	    	/********************************
			 * 적치베드 상태 활성화등록
			 *******************************/
	    	jrParam.setField("YD_STK_BED_WT_MAX"   , CConstant.YD_STK_BED_WT_MAX_DEFAULT); 
	    	jrParam.setField("YD_STK_COL_GP"       , ydStackColGp);
			jrParam.setField("YD_STK_BED_ACT_STAT" , "L");
	    	
			/*
			UPDATE TB_YD_STKBED
			   SET MODIFIER  = :V_MODIFIER
			     , MOD_DDTT  = SYSDATE
			     , YD_STK_BED_WT_MAX   = :V_YD_STK_BED_WT_MAX
			     , YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			 */
			if ("Y".equals(sApp838)) {
				commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkBedInit", logId, mthdNm, "적치BED 활성상태수정");
			} else {
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkBedInit", logId, mthdNm, "적치BED 활성상태수정");	
			}
			
	    	
			/********************************
			 * 적치단 상태 활성화등록
			 *******************************/
			jrParam.setField("YD_STK_LYR_ACT_STAT", "E");
			jrParam.setField("STL_NO"             , "" );
			jrParam.setField("YD_STK_LYR_MTL_STAT", "E");
			/*
			UPDATE TB_YD_STKLYR
			   SET MODIFIER  = :V_MODIFIER
			     , MOD_DDTT  = SYSDATE
			     , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
			     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
			     , STL_NO              = :V_STL_NO
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			 */
			if ("Y".equals(sApp838)) {
				commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkLyrInit", logId, mthdNm, "적치단 활성상태수정");
			} else {
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkLyrInit", logId, mthdNm, "적치단 활성상태수정");	
			}
			
			

			commUtils.printLog(logId, mthdNm, "S-");
		
			return  ""+intRtnVal ;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 :  차량동간이적 여부
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public boolean chkCarMv(String logId, String mthdNms, String ydSchCd) throws DAOException {
		String mthdNm = "차량동간이적 여부[CCoilDao.chkCarMv] < " + mthdNms;

		try {
			commUtils.printLog(logId, mthdNm, "S+"); 

			if ("".equals(ydSchCd)) {
				commUtils.printLog(logId, "스케줄코드가 없습니다.", "S-");
				return false;
			}
			
			if ("TR1".equals(ydSchCd.substring(2, 5)) || "TR2".equals(ydSchCd.substring(2, 5)) || "TR3".equals(ydSchCd.substring(2, 5)) ) {
				return true ;
			}
			commUtils.printLog(logId, mthdNm, "S-");
			return false;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return false;
		} catch (Exception e) {
			return false;
		}
	}		
	
	
	/**
	 *      [A] 오퍼레이션명 :  반품회송 여부
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public boolean chkCarMvRetn(String logId, String mthdNms, String ydSchCd) throws DAOException {
		String mthdNm = "반품회송 여부[CCoilDao.chkCarMvRetn] < " + mthdNms;

		try {
			commUtils.printLog(logId, mthdNm, "S+"); 

			if ("".equals(ydSchCd)) {
				commUtils.printLog(logId, "스케줄코드가 없습니다.", "S-");
				return false;
			}
			
			if ("PT41L".equals(ydSchCd.substring(2, 7)) 
			||  "PT42L".equals(ydSchCd.substring(2, 7))
			||  "PT43L".equals(ydSchCd.substring(2, 7)) ) {
				return true ;
			}
			commUtils.printLog(logId, mthdNm, "S-");
			return false;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return false;
		} catch (Exception e) {
			return false;
		}
	}	

	
	/**
	 *      [A] 오퍼레이션명 : 제품대차 자동출발 여부 CHECK
	 * 
	 * @param String szYdStkColGp
	 * @return boolean     			
	 */	
	public boolean chkTcAutoStartAble(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "제품대차 자동출발 여부[CCoilDao.chkTcAutoStartAble] < " + rcvMsg.getResultMsg();
	    String logId  = rcvMsg.getResultCode();
	    
	    try {
	    	commUtils.printLog(logId, mthdNm, "S+");
	    	
	    	String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
	    	String ydWbookId = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"));
	    	String ydSchCd   = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));
	    	
	    	JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
	    	jrParam.setField("YD_SCH_CD"   , ydSchCd);
			jrParam.setField("YD_WBOOK_ID" , ydWbookId);	
			
			 /* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getStkColTCarUpChk1 
			SELECT SUM(CNT)     AS STL_CNT 
			     , SUM(COIL_WT) AS SUM_WGT
			     , MAX(STK_BED_MAX_QNTY)
			     , MAX(STK_BED_MAX_WT)
			     
			     , CASE WHEN SUM(CNT) <= MAX(STK_BED_MAX_QNTY) AND SUM(COIL_WT) < MAX(STK_BED_MAX_WT) THEN 'Y'
			            ELSE 'N' END LOC_YN
			  --   , 'Y'  AS LOC_YN
			  FROM
			        (   -- 현재 중량
			            SELECT COUNT(*)                   AS CNT
			                 , SUM (Z2.COIL_WT)           AS COIL_WT 
			                 , MAX (Z1.YD_WRK_PLAN_TCAR ) AS YD_WRK_PLAN_TCAR
			                 , MAX (Z1.STK_BED_MAX_QNTY ) AS STK_BED_MAX_QNTY
			                 , MAX (Z1.STK_BED_MAX_WT )   AS STK_BED_MAX_WT
			              FROM
			                   (
			                     SELECT B.STL_NO
			                          , A.YD_WRK_PLAN_TCAR
			                          , C.STK_BED_MAX_QNTY
			                          , C.STK_BED_MAX_WT
			                       FROM TB_YD_WRKBOOK A 
			                          , TB_YD_WRKBOOKMTL B
			                          , TB_YD_EQP        C
			                      WHERE A.YD_WBOOK_ID      = B.YD_WBOOK_ID
			                        AND A.YD_WRK_PLAN_TCAR = C.YD_EQP_ID
			                        AND B.YD_WBOOK_ID      = :V_YD_WBOOK_ID
			                        AND B.DEL_YN = 'N'
			                        
			                    ) Z1
			                 , USRPTA.TB_PT_COILCOMM Z2
			            WHERE Z1.STL_NO = Z2.COIL_NO
			            UNION ALL
			            -- 대차위 중량
			            SELECT COUNT(*)               AS CNT
			                 , NVL(SUM(Z2.COIL_WT),0) AS COIL_WT 
			                 , ''                     AS YD_WRK_PLAN_TCAR
			                 , 0                      AS STK_BED_MAX_QNTY
			                 , 0                      AS STK_BED_MAX_WT
			              FROM
			                   (
			                     SELECT A.STL_NO
			                       FROM TB_YD_STKLYR A
			                      WHERE A.YD_STK_COL_GP  = SUBSTR(:V_YD_SCH_CD,1,2)|| (SELECT SUBSTR(YD_WRK_PLAN_TCAR,3,4) FROM TB_YD_WRKBOOK 
			                                                                            WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID )
			                        AND A.YD_STK_LYR_MTL_STAT IN ('C')
			                        AND A.DEL_YN = 'N'
			                   ) Z1
			                 , USRPTA.TB_PT_COILCOMM Z2
			             WHERE Z1.STL_NO = Z2.COIL_NO
			        ) 	 
			*/
	    	//설비 테이블 조회
			JDTORecordSet jsTcLocChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getStkColTCarUpChk1", logId, mthdNm, "Tc적치가능여부");
			if (jsTcLocChk == null || jsTcLocChk.size() == 0) {
				commUtils.printLog(logId, "해당정보가 없습니다.", "S-");	
				return false;
				
			} 
			jsTcLocChk.first();
			JDTORecord jrTcLocChk = jsTcLocChk.getRecord();
		
			String sLocYn = commUtils.trim(jrTcLocChk.getFieldString("LOC_YN"));
			
			if ("Y".equals(sLocYn)) {
				commUtils.printLog(logId, mthdNm, "S-");
				return true;
			} else {	
				commUtils.printLog(logId, mthdNm, "S-");
				return false;
			}	
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 대차 적치가능 여부 CHECK
	 * 
	 * @param String szYdStkColGp
	 * @return boolean     			
	 */	
	public boolean chkTcLocAble(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "대차적치가능 여부[CCoilDao.chkTcLocAble] < " + rcvMsg.getResultMsg();
	    String logId  = rcvMsg.getResultCode();
	    
	    try {
	    	commUtils.printLog(logId, mthdNm, "S+");
	    	
	    	String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
	    	String ydWbookId = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"));
	    	String ydSchCd   = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));
	    	
	    	JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
	    	jrParam.setField("YD_SCH_CD"   , ydSchCd);
			jrParam.setField("YD_WBOOK_ID" , ydWbookId);	
			
			 /* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getStkColTCarUpChk2
			SELECT SUM(CNT)     AS STL_CNT 
			     , SUM(COIL_WT) AS SUM_WGT
			     , MAX(STK_BED_MAX_QNTY)
			     , MAX(STK_BED_MAX_WT)
			     
			     , CASE WHEN SUM(CNT) <= MAX(STK_BED_MAX_QNTY) AND SUM(COIL_WT) < MAX(STK_BED_MAX_WT) THEN 'Y'
			            ELSE 'N' END LOC_YN
			  --   , 'Y'  AS LOC_YN
			  FROM
			        (   -- 현재 중량
			            SELECT COUNT(*)                   AS CNT
			                 , SUM (Z2.COIL_WT)           AS COIL_WT 
			                 , MAX (Z1.YD_WRK_PLAN_TCAR ) AS YD_WRK_PLAN_TCAR
			                 , MAX (Z1.STK_BED_MAX_QNTY ) AS STK_BED_MAX_QNTY
			                 , MAX (Z1.STK_BED_MAX_WT )   AS STK_BED_MAX_WT
			              FROM
			                   (
			                     SELECT B.STL_NO
			                          , A.YD_WRK_PLAN_TCAR
			                          , C.STK_BED_MAX_QNTY
			                          , C.STK_BED_MAX_WT
			                       FROM TB_YD_WRKBOOK A 
			                          , TB_YD_WRKBOOKMTL B
			                          , TB_YD_EQP        C
			                      WHERE A.YD_WBOOK_ID      = B.YD_WBOOK_ID
			                        AND A.YD_WRK_PLAN_TCAR = C.YD_EQP_ID
			                        AND B.YD_WBOOK_ID      = :V_YD_WBOOK_ID
			                        AND B.DEL_YN = 'N'
			                        
			                    ) Z1
			                 , USRPTA.TB_PT_COILCOMM Z2
			            WHERE Z1.STL_NO = Z2.COIL_NO
			            UNION ALL
			            -- 대차위 중량
			            SELECT COUNT(*)               AS CNT
			                 , NVL(SUM(Z2.COIL_WT),0) AS COIL_WT 
			                 , ''                     AS YD_WRK_PLAN_TCAR
			                 , 0                      AS STK_BED_MAX_QNTY
			                 , 0                      AS STK_BED_MAX_WT
			              FROM
			                   (
			                     SELECT A.STL_NO
			                       FROM TB_YD_STKLYR A
			                      WHERE A.YD_STK_COL_GP  = SUBSTR(:V_YD_SCH_CD,1,2)|| (SELECT SUBSTR(YD_WRK_PLAN_TCAR,3,4) FROM TB_YD_WRKBOOK 
			                                                                            WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID )
			                        AND A.YD_STK_LYR_MTL_STAT IN ('C','D')
			                        AND A.DEL_YN = 'N'
			                   ) Z1
			                 , USRPTA.TB_PT_COILCOMM Z2
			             WHERE Z1.STL_NO = Z2.COIL_NO
			        ) 	 
			*/
	    	//설비 테이블 조회
			JDTORecordSet jsTcLocChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getStkColTCarUpChk2", logId, mthdNm, "Tc적치가능여부");
			if (jsTcLocChk == null || jsTcLocChk.size() == 0) {
				commUtils.printLog(logId, "해당정보가 없습니다.", "S-");	
				return false;
				
			} 
			jsTcLocChk.first();
			JDTORecord jrTcLocChk = jsTcLocChk.getRecord();
		
			String sLocYn = commUtils.trim(jrTcLocChk.getFieldString("LOC_YN"));
			
			if ("Y".equals(sLocYn)) {
				commUtils.printLog(logId, mthdNm, "S-");
				return true;
			} else {	
				commUtils.printLog(logId, mthdNm, "S-");
				return false;
			}	
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 야드적치구분(검색조건 행선 - 수입TO위치검색용)
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String getCoilYdRouteGpCV(String logId , String mthdNms, String ydSchCd, String sStlNo) throws DAOException {
		String mthdNm = "야드적치구분[CCoilDAO.getCoilYdRouteGpCV] < " + mthdNms;
		String ydRouteGp = "";
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
			//수신 항목 값
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("YD_SCH_CD", ydSchCd); 
			jrParam.setField("STL_NO"   , sStlNo); 

			/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getCoilYdRouteGpCV_PIDEV
			WITH DATA_TBL AS
			(
			SELECT A.ORD_YEOJAE_GP
			     , A.ITEMNAME_CD
			     -- 인도조건
			     , CASE WHEN A.ORD_YEOJAE_GP = '2' THEN ''
			            ELSE SUBSTR(C.DELIVER_TERM_CD,1,1) END AS DELIVER_TERM_CD
			     --포장방법       
			     , DECODE(A.CURR_PROG_CD,'F','XX',C.WRAP_METHOD_CD) AS WRAP_METHOD_CD       
			     -- 용도       
			     , (CASE WHEN A.ORD_NO LIKE 'G%' AND A.CURR_PROG_CD='H' THEN 'F'
			             WHEN A.ITEMNAME_CD IN ('HAP','HBP','HCP','HAS','HBS','HCS','HAT','HBT','HCT','HCJ') THEN 'F' ELSE 'Y' END) AS USAGE_CD
			     , A.ORD_NO
			     , A.ORD_DTL
			  FROM USRPTA.TB_PT_COILCOMM A
			     , TB_YD_STOCK    B 
			     , USRPTA.TB_PT_OSCOMM   C             
			 WHERE A.COIL_NO = B.STL_NO
			   AND A.COIL_NO = :V_STL_NO
			   AND A.ORD_NO  = C.ORD_NO(+)
			   AND A.ORD_DTL = C.ORD_DTL(+)
			)
			SELECT     --소재장
			       CASE WHEN SUBSTR(TP.YD_SCH_CD,8,1) = 'H' AND APP_YN = 'N' THEN
			                 ( 
			                   SELECT CASE --지포장 보급
			                               WHEN TP.YD_SCH_CD IN ('JBGF01UH','JCGF01UH','JEGF01UH','JHGF01UH')                   THEN 'G0'
			                               --C동반입 HFL재(B3), SPM재(B4)
			                               WHEN TP.YD_SCH_CD IN ('JCPT01LH','JCPT02LH') THEN DECODE(CC.NEXT_PROC,'CH','B3','B4') 
			                               ELSE (SELECT YD_AIM_RT_GP FROM TB_YD_STOCK WHERE STL_NO = CC.COIL_NO) 

			                          END
			                     FROM TB_PT_COILCOMM CC
			                    WHERE CC.COIL_NO = :V_STL_NO 
			                 ) 
			             -- 소재 신 검색조건     
			            WHEN SUBSTR(TP.YD_SCH_CD,8,1) = 'H' AND APP_YN = 'Y' THEN
			                 ( 
			                   SELECT CASE WHEN TP.YD_SCH_CD IN ('JBGF01UH','JCGF01UH','JEGF01UH','JHGF01UH')  THEN 'G0'
			                               --C동반입 HFL재(B3), SPM재(B4)
			                               WHEN TP.YD_SCH_CD IN ('JCPT01LH','JCPT02LH') THEN DECODE(CC.NEXT_PROC,'CH','B3','B4') 
			--                               WHEN SUBSTR(CC.NEXT_PROC,1,1) = SUBSTR(TP.YD_SCH_CD,2,1) 
			--                                     AND SUBSTR(CC.NEXT_PROC,2,1) IN ('A','H','K','R') THEN CC.NEXT_PROC
			                               -- RULE 에 포함 안된것은 '_Z'처리
			                               WHEN SUBSTR(CC.NEXT_PROC,1,1) = SUBSTR(TP.YD_SCH_CD,2,1) 
			                                     AND SUBSTR(CC.NEXT_PROC,2,1) IN ('H','K','R') THEN 
			                                              CASE WHEN CC.NEXT_PROC IN ( SELECT NVL(DTL_ITEM2,'*') FROM TB_YD_RULE
			                                                                           WHERE REPR_CD_GP  = 'APP007'
			                                                                             AND DEL_YN ='N'
			                                                                            ) THEN CC.NEXT_PROC
			                                                   ELSE SUBSTR(TP.YD_SCH_CD,2,1) ||'Z' 
			                                                   END                             
			                               -- 공냉재
			                               WHEN SUBSTR(CC.NEXT_PROC,2,1) = 'A'
			                                 OR EXISTS (SELECT 1
			                                              FROM TB_HR_C_SHEARWOWR SR
			                                             WHERE SR.COIL_NO      = CC.COIL_NO
			                                               AND SR.HR_PLNT_GP   = 'C'
			                                               AND SR.WORK_STAT    = '*'
			                                               AND SR.WORD_PROC LIKE '%A'
			                                               AND SR.RECEIPT_HOLD_SCRAP_CAUSE_GP IN ('I', 'B')
			                                               AND SR.STEP_NO = (SELECT MAX(STEP_NO) FROM TB_HR_C_SHEARWOWR WHERE COIL_NO = SR.COIL_NO)
			                                           ) THEN SUBSTR(TP.YD_SCH_CD,2,1) ||'A'
			                               ELSE SUBSTR(TP.YD_SCH_CD,2,1) ||'Z'
			                               END  NEXT_PROC
			                      FROM TB_PT_COILCOMM CC
			                     WHERE CC.COIL_NO = :V_STL_NO 
			                 )      
			           --제품장           
			            ELSE 
			              NVL(( 
			                   SELECT CASE 
			                               WHEN ST.YD_AIM_RT_GP  = 'F3'  THEN 'F0'
			                               WHEN TP.YD_SCH_CD IN ( 'JAKD01LM'                                 ,'JATC05MM'
			                                                     ,'JBKD01LM'           ,'JBTC01MM','JBTC02MM','JBTC05MM'
			                                                     ,'JCKD01LM','JCFD01LM','JCTC01MM','JCTC02MM','JCTC05MM'
			                                                     ,'JDFD01LM','JDTC01MM','JDTC02MM'
			                                                     ,'JEKD01LM','JETC01MM','JETC02MM'
			                                                     ,'JFFD01LM','JFTC01MM','JFTC02MM'
			                                                     ,'JGFD01LM','JGTC01MM','JGTC02MM'
			                                                     ,'JHKD01LM'           ,'JHTC01MM','JHTC02MM') 
			                                                 AND WRAP_METHOD_CD = 'EB' AND USAGE_CD NOT IN ('F') THEN 'G0'
			                               ELSE 
			                                    (SELECT A.CD_GP AS CODE
			                                       FROM VW_YD_YDB700 A
			                                      WHERE A.ORD_YEOJAE_GP LIKE NVL(BT.ORD_YEOJAE_GP,'') ||'%' 
			                                        AND A.ITM_NM        LIKE NVL(BT.DELIVER_TERM_CD ,'') ||'%' 
			                                        AND ((BT.ORD_YEOJAE_GP IS NOT NULL AND (A.CD_GP <> 'F0') AND (A.CD_GP <> 'G0'))
			                                             OR ((BT.ORD_YEOJAE_GP IS NULL) )
			                                            )
			                                        AND ROWNUM = 1    
			                                    )        
			                          END
			                     FROM TB_YD_STOCK  ST  
			                        , DATA_TBL     BT
			                    WHERE ST.STL_NO = :V_STL_NO   
			                 ),'A1') 
			            
			       END 
			       AS YD_ROUTE_GP
			  FROM 
			       ( SELECT :V_YD_SCH_CD AS YD_SCH_CD 
			              , NVL((SELECT ITEM1 
			                       FROM TB_YD_RULE 
			                      WHERE REPR_CD_GP = 'APP005'
			                        AND CD_GP = 'J' AND ITEM = '*' AND DEL_YN = 'N'),'N') AS APP_YN 
			           FROM DUAL)  TP
			 */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getCoilYdRouteGpCV_PIDEV", logId, mthdNm, "적치구분(행선)조회"); 

			if (jsChk.size() > 0) {
				ydRouteGp 	= commUtils.trim(jsChk.getRecord(0).getFieldString("YD_ROUTE_GP"));
			}
			
			commUtils.printLog(logId, mthdNm, "S-");

			return ydRouteGp;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), commUtils, e);
			return ydRouteGp;
		} catch (Exception e) {
			return ydRouteGp;
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 야드적치구분(검색조건 행선) - 제품 입고스케쥴 TO위치
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String getCoilYdRouteGpKd(String logId , String mthdNms, String ydSchCd, String sStlNo) throws DAOException {
		String mthdNm = "야드적치구분[CCoilDAO.getCoilYdRouteGpKd] < " + mthdNms;
		String ydRouteGp = "";
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
			//수신 항목 값
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("YD_SCH_CD", ydSchCd); 
			jrParam.setField("STL_NO"   , sStlNo); 

			/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getCoilYdRouteGpKd_PIDEV
			WITH DATA_TBL AS
			(
			SELECT A.ORD_YEOJAE_GP
			     , A.ITEMNAME_CD
			     -- 인도조건
			     , CASE WHEN A.ORD_YEOJAE_GP = '2' THEN ''
			            ELSE SUBSTR(C.DELIVER_TERM_CD,1,1) END AS DELIVER_TERM_CD
			     --포장방법       
			     , DECODE(A.CURR_PROG_CD,'F','XX',C.WRAP_METHOD_CD) AS WRAP_METHOD_CD       
			     -- 용도       
			     , (CASE WHEN A.ORD_NO LIKE 'G%' AND A.CURR_PROG_CD='H' THEN 'F'
			             WHEN A.ITEMNAME_CD IN ('HAP','HBP','HCP','HAS','HBS','HCS','HAT','HBT','HCT','HCJ') THEN 'F' ELSE 'Y' END) AS USAGE_CD
			     , A.ORD_NO
			     , A.ORD_DTL
			  FROM USRPTA.TB_PT_COILCOMM A
			     , TB_YD_STOCK    B 
			     , USRPTA.TB_PT_OSCOMM   C             
			 WHERE A.COIL_NO = B.STL_NO
			   AND A.COIL_NO = :V_STL_NO
			   AND A.ORD_NO  = C.ORD_NO(+)
			   AND A.ORD_DTL = C.ORD_DTL(+)
			)
			SELECT     --소재장
			       CASE WHEN SUBSTR(TP.YD_SCH_CD,8,1) = 'H' AND APP_YN = 'N' THEN
			                 ( 
			                   SELECT CASE --지포장 보급
			                               WHEN TP.YD_SCH_CD IN ('JBGF01UH','JCGF01UH','JEGF01UH','JHGF01UH')                   THEN 'G0'
			                               --C동반입 HFL재(B3), SPM재(B4)
			                               WHEN TP.YD_SCH_CD IN ('JCPT01LH','JCPT02LH') THEN DECODE(CC.NEXT_PROC,'CH','B3','B4') 
			                               ELSE (SELECT YD_AIM_RT_GP FROM TB_YD_STOCK WHERE STL_NO = CC.COIL_NO) 

			                          END
			                     FROM TB_PT_COILCOMM CC
			                    WHERE CC.COIL_NO = :V_STL_NO 
			                 ) 
			             -- 소재 신 검색조건     
			            WHEN SUBSTR(TP.YD_SCH_CD,8,1) = 'H' AND APP_YN = 'Y' THEN
			                 ( 
			                   SELECT CASE WHEN TP.YD_SCH_CD IN ('JBGF01UH','JCGF01UH','JEGF01UH','JHGF01UH')  THEN 'G0'
			                               --C동반입 HFL재(B3), SPM재(B4)
			                               WHEN TP.YD_SCH_CD IN ('JCPT01LH','JCPT02LH') THEN DECODE(CC.NEXT_PROC,'CH','B3','B4') 
			--                               WHEN SUBSTR(CC.NEXT_PROC,1,1) = SUBSTR(TP.YD_SCH_CD,2,1) 
			--                                     AND SUBSTR(CC.NEXT_PROC,2,1) IN ('A','H','K','R') THEN CC.NEXT_PROC
			                               -- RULE 에 포함 안된것은 '_Z'처리
			                               WHEN TP.YD_SCH_CD LIKE ('J_CV0_LH') THEN 'KK'
			                               WHEN SUBSTR(CC.NEXT_PROC,1,1) = SUBSTR(TP.YD_SCH_CD,2,1) 
			                                     AND SUBSTR(CC.NEXT_PROC,2,1) IN ('H','K','R') THEN 
			                                              CASE WHEN CC.NEXT_PROC IN ( SELECT NVL(DTL_ITEM2,'*') FROM TB_YD_RULE
			                                                                           WHERE REPR_CD_GP  = 'APP007'
			                                                                             AND DEL_YN ='N'
			                                                                            ) THEN CC.NEXT_PROC
			                                                   ELSE SUBSTR(TP.YD_SCH_CD,2,1) ||'Z' 
			                                                   END                             
			                               -- 공냉재
			                               WHEN SUBSTR(CC.NEXT_PROC,2,1) = 'A'
			                                 OR EXISTS (SELECT 1
			                                              FROM TB_HR_C_SHEARWOWR SR
			                                             WHERE SR.COIL_NO      = CC.COIL_NO
			                                               AND SR.HR_PLNT_GP   = 'C'
			                                               AND SR.WORK_STAT    = '*'
			                                               AND SR.WORD_PROC LIKE '%A'
			                                               AND SR.RECEIPT_HOLD_SCRAP_CAUSE_GP IN ('I', 'B')
			                                               AND SR.STEP_NO = (SELECT MAX(STEP_NO) FROM TB_HR_C_SHEARWOWR WHERE COIL_NO = SR.COIL_NO)
			                                           ) THEN SUBSTR(TP.YD_SCH_CD,2,1) ||'A'
			                               ELSE SUBSTR(TP.YD_SCH_CD,2,1) ||'Z'
			                               END  NEXT_PROC
			                      FROM TB_PT_COILCOMM CC
			                     WHERE CC.COIL_NO = :V_STL_NO 
			                 )      
			           --제품장           
			            ELSE 
			              NVL(( 
			                   SELECT CASE 
			                               WHEN ST.YD_AIM_RT_GP  = 'F3'  THEN 'F0'
			                               WHEN TP.YD_SCH_CD IN ( 'JAKD01LM'                                 ,'JATC05MM'
			                                                     ,'JBKD01LM'           ,'JBTC01MM','JBTC02MM','JBTC05MM'
			                                                     ,'JCKD01LM','JCFD01LM','JCTC01MM','JCTC02MM','JCTC05MM'
			                                                     ,'JDFD01LM','JDTC01MM','JDTC02MM'
			                                                     ,'JEKD01LM','JETC01MM','JETC02MM'
			                                                     ,'JFFD01LM','JFTC01MM','JFTC02MM'
			                                                     ,'JGFD01LM','JGTC01MM','JGTC02MM'
			                                                     ,'JHKD01LM'           ,'JHTC01MM','JHTC02MM') 
			                                                 AND WRAP_METHOD_CD = 'EB' AND USAGE_CD NOT IN ('F') THEN 'G0'
			                               ELSE 
			                                    (SELECT A.CD_GP AS CODE
			                                       FROM VW_YD_YDB700 A
			                                      WHERE A.ORD_YEOJAE_GP LIKE NVL(BT.ORD_YEOJAE_GP,'') ||'%' 
			                                        AND A.ITM_NM        LIKE NVL(BT.DELIVER_TERM_CD ,'') ||'%' 
			                                        AND ((BT.ORD_YEOJAE_GP IS NOT NULL AND (A.CD_GP <> 'F0') AND (A.CD_GP <> 'G0'))
			                                             OR ((BT.ORD_YEOJAE_GP IS NULL) )
			                                            )
			                                        AND ROWNUM = 1    
			                                    )        
			                          END
			                     FROM TB_YD_STOCK  ST  
			                        , DATA_TBL     BT
			                    WHERE ST.STL_NO = :V_STL_NO   
			                 ),'A1') 
			            
			       END 
			       AS YD_ROUTE_GP
			  FROM 
			       ( SELECT :V_YD_SCH_CD AS YD_SCH_CD 
			              , NVL((SELECT ITEM1 
			                       FROM TB_YD_RULE 
			                      WHERE REPR_CD_GP = 'APP005'
			                        AND CD_GP = 'J' AND ITEM = '*' AND DEL_YN = 'N'),'N') AS APP_YN 
			           FROM DUAL)  TP
			*/
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getCoilYdRouteGpKd_PIDEV", logId, mthdNm, "적치구분(행선)조회"); 

			if (jsChk.size() > 0) {
				ydRouteGp 	= commUtils.trim(jsChk.getRecord(0).getFieldString("YD_ROUTE_GP"));
			}
			
			commUtils.printLog(logId, mthdNm, "S-");

			return ydRouteGp;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), commUtils, e);
			return ydRouteGp;
		} catch (Exception e) {
			return ydRouteGp;
		}
	}

	
/***********************************
            PIDEV 개발
***********************************/
	
//PIDEV	


	/**
	 * 야드목표행선지구분를 지정한다. PIDEV
     * @param  String	sItemGp :	제품구분(S:SLAB, C:COIL ,P: 후판 ) ,JDTORecord inRecord
     * @return String
     * @throws  
     */		
	public String[] getYdAimRtGp_PIDEV(String sItemGp, JDTORecord jrRecord) throws DAOException {
		String mthdNm = "야드목표행선구분 지정[coilDao.getYdAimRtGp_PIDEV] < " + jrRecord.getResultMsg();
		String logId = jrRecord.getResultCode();
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");

			JDTORecordSet jsRst = JDTORecordFactory.getInstance().createRecordSet("");
			String[] rVal = new String[2];

			String sMsg       	= "";
			String ydAimRtGp  	= "";
			String ydAimRtGp2	= "";
			String sSkinPassYn  = "";
			String sCurrProgCd	= "";
			String sWorkProc   	= "";
			String sNextProc  	= ""; // 다음공정
			String sPlanProc1	= ""; // 열연계획작업코드1
			String sRcvTcCode 	= commUtils.getTcCode(jrRecord);
			String sStlNo    	= commUtils.trim(jrRecord.getFieldString("STL_NO"));
			String sINFO_GP    	= commUtils.trim(jrRecord.getFieldString("INFO_GP"));
			commUtils.printLog(logId, "sRcvTcCode:"+ sRcvTcCode + "  INFO_GP:"+sINFO_GP, "SL");
			
			if ("C".equals(sItemGp)) {
				// 수신한 재료번호로 코일공통 읽기***************************************************************************************************
				if (!"".equals(sStlNo)) {
					
					JDTORecord jrParam  = commUtils.getParam(logId, mthdNm, "");
					jrParam.setField("COIL_NO" , sStlNo);
					jsRst = commDao.select(jrParam,"com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getPtCoilComm",logId, mthdNm, "코일공통 조회");

					if (jsRst.size() <= 0) {
						sMsg = "코일공통 SELECT Error :: [" + sStlNo + "]"+ "DO NOT EXIST";
						commUtils.printLog(logId, sMsg, "SL");
						return rVal;
					} else {
						ydAimRtGp2 	= commUtils.trim(jsRst.getRecord(0).getFieldString("YD_AIM_RT_GP2"));
						sSkinPassYn = commUtils.trim(jsRst.getRecord(0).getFieldString("SKINPASS_YN"  ));
						sCurrProgCd = commUtils.trim(jsRst.getRecord(0).getFieldString("CURR_PROG_CD" ));
						
						commUtils.printLog(logId, ydAimRtGp2 + " " + sSkinPassYn + " " + sCurrProgCd, "SL");
						
						// 진도코드 존제여부 체크
						if ("".equals(sCurrProgCd)) {
							sMsg = "진도코드가  존재  안 함";
							commUtils.printLog(logId, sMsg, "SL");
							return rVal;
						}
						
						sNextProc  = commUtils.trim(jsRst.getRecord(0).getFieldString("NEXT_PROC"));
						sPlanProc1 = commUtils.trim(jsRst.getRecord(0).getFieldString("PLAN_PROC1"));
					}
										
				} else {
					// 진도코드
					sCurrProgCd = commUtils.trim(jrRecord.getFieldString("CURR_PROG_CD"));
				}


//				if ("DMYDR005".equals(sRcvTcCode)) {
//					ydAimRtGp = "K2"; // 출하지시대기
//					sCurrProgCd = "K";
//				} else if ("DMYDR020".equals(sRcvTcCode)) {
//					ydAimRtGp = "L2"; // 운송지시
//					sCurrProgCd = "L";
//				} else if ("DMYDR023".equals(sRcvTcCode)
//						|| "DMYDR060".equals(sRcvTcCode)) {
//					ydAimRtGp = "L5"; // 상차지시
//					sCurrProgCd = "L";
//				} else if ("DMYDR030".equals(sRcvTcCode)) {
//					ydAimRtGp = "M2"; // 출하완료
//					sCurrProgCd = "M";
				if ("M10LMYDJ1011".equals(sRcvTcCode) && "4".equals(sINFO_GP) ) {
					ydAimRtGp = "K2"; // 출하지시대기
					sCurrProgCd = "K";
//사용안함			} else if ("DMYDR020".equals(sRcvTcCode)) {   
//사용안함				ydAimRtGp = "L2"; // 운송지시                                     
//사용안함				sCurrProgCd = "L";                        
//23사용안함		} else if ("DMYDR023".equals(sRcvTcCode) || "DMYDR060".equals(sRcvTcCode)) {
				} else if ("M10LMYDJ1031".equals(sRcvTcCode)) {					
					ydAimRtGp = "L5"; // 상차지시
					sCurrProgCd = "L";
				} else if ("M10LMYDJ1071".equals(sRcvTcCode)) {
					ydAimRtGp = "M2"; // 출하완료
					sCurrProgCd = "M";				
				} else if ("G".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 종합판정대기
				} else if ("I".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 반송대기
				} else if ("H".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 입고대기
				} else if ("Y".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "C"; // 재공충당대기(C열연정정)
				} else if ("B".equals(sCurrProgCd)) { // 지시대기

					sNextProc  = commUtils.trim(jsRst.getRecord(0).getFieldString("NEXT_PROC"));
					
					// C 동 수입인 경우 에만 spm재와 hfl재를 분리해서 적치 한다.
					if ("H".equals(sNextProc.substring(1, 2))) {
						ydAimRtGp = sCurrProgCd + "3"; // 지시대기
					} else {
						ydAimRtGp = sCurrProgCd + "4"; // 지시대기
					}
 
				} else if ("J".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 반납대기
				} else if ("Z".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 제품충당대기
				} else if ("X".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 경매대상선정
				} else if ("E".equals(sCurrProgCd) || "D".equals(sCurrProgCd)) {
					// 재공이송작업대기
					sNextProc  = commUtils.nvl(commUtils.trim(jsRst.getRecord(0).getFieldString("NEXT_PROC" )), "");
					sPlanProc1 = commUtils.nvl(commUtils.trim(jsRst.getRecord(0).getFieldString("PLAN_PROC1")), "");

					if (!"".equals(sNextProc)) {
						sWorkProc = sNextProc;
					} else {
						sWorkProc = sPlanProc1;
					}
					// 계획공정정보를 가지고 야드행선을 셋팅
					if (sWorkProc.startsWith("1")) {
						ydAimRtGp = "EA";
					} else if (sWorkProc.startsWith("5")
							|| sWorkProc.startsWith("6")) {
						ydAimRtGp = "EB";
					} else if (sWorkProc.startsWith("9S")) {
						ydAimRtGp = "ED";
					} else {
						ydAimRtGp = "EC";
					}
				} else if ("C".equals(sCurrProgCd)) {
					
					// 정정작업지시대기
					sNextProc  = commUtils.nvl(commUtils.trim(jsRst.getRecord(0).getFieldString("NEXT_PROC" )), "");
					sPlanProc1 = commUtils.nvl(commUtils.trim(jsRst.getRecord(0).getFieldString("PLAN_PROC1")), "");

					if (!"".equals(sNextProc)) {
						sWorkProc = sNextProc;
					} else {
						sWorkProc = sPlanProc1;
					}

					/*
					계획공정코드
						DH C열연 D Line No3HFL C열연 D Line No3HFL(정정LINE구분 : No3HFL) 11 
						DA C열연 D Line 공냉 C열연 D Line 공냉(Hysco向) 12 
						EH C열연 E Line Hot Final C열연 E Line Hot Final(정정LINE구분:SPM2) 13 
						EK C열연 E Line Skin Pass C열연 E Line Skin Pass(정정LINE구분:SPM2) 14 
						ER C열연 E Line Recoiling C열연 E Line Recoiling(정정LINE구분:SPM2) 15 
						EA C열연 E Line 공냉 C열연 E Line 공냉(Hysco向) 16 
						FH C열연 F Line No2HFL C열연 F Line No2HFL(정정LINE구분:No2HFL) 17 
						FA C열연 F Line 공냉 C열연 F Line 공냉(Hysco向) 18 
						GA C열연 G Line 공냉 C열연 G Line 공냉(정정LINE구분:No1HFL) 19 
						GH C열연 G Line No1HFL C열연 G Line No1HFL(정정LINE구분:No1HFL) 20 
						GT C열연 G Line 수냉 C열연 G Line 수냉(정정LINE구분:No1HFL) 21 
						HH C열연 H Line Hot Final C열연 H Line Hot Final(정정LINE구분:SPM1) 22 
						HK C열연 H Line Skin Pass C열연 H Line Skin Pass(정정LINE구분:SPM1) 23 
						HR C열연 H Line Recoiling C열연 H Line Recoiling(정정LINE구분:SPM1) 24 
						HA C열연 H Line 공냉 C열연 H Line 공냉(Hysco向) 25
					야드행선구분 
						CE 작업대기(C열연 HFL)
						CF 작업대기(C열연 SPM1)
						CG 작업대기(C열연 SPM2)
						CH 작업대기(C열연#1결속대)
						CI 작업대기(C열연#2결속대) 
					*/

					// 계획공정정보를 가지고 야드행선을 셋팅 _ 추후 다시 셋팅 (C열연만 셋팅 )
					if("DH".equals(sWorkProc)||
					   "FH".equals(sWorkProc)||
					   "GA".equals(sWorkProc)||
					   "GH".equals(sWorkProc)||
					   "CA".equals(sWorkProc)||
					   "CH".equals(sWorkProc)||
					   "AA".equals(sWorkProc)||
					   "BH".equals(sWorkProc)||
					   "GT".equals(sWorkProc)){
						ydAimRtGp	= "CE";
					}else if("HH".equals(sWorkProc)||
							 "HK".equals(sWorkProc)||
							 "HR".equals(sWorkProc)){
						ydAimRtGp	= "CF";
					}else if("EH".equals(sWorkProc)||
							 "EK".equals(sWorkProc)||
							 "ER".equals(sWorkProc)){
						ydAimRtGp	= "CG";
					}else if("CK".equals(sWorkProc)||
							 "CR".equals(sWorkProc)){
						ydAimRtGp	= "CF";
					}else if("BK".equals(sWorkProc)||
							 "BR".equals(sWorkProc)){
						ydAimRtGp	= "CF";	
					}else if("AK".equals(sWorkProc)||
							 "AR".equals(sWorkProc)){
						ydAimRtGp	= "CF";	
					}else {
						ydAimRtGp	= "XX";
					}	
					if("F4".equals(ydAimRtGp2) || "F5".equals(ydAimRtGp2)) {   		//재작업인 경우 
						ydAimRtGp = ydAimRtGp2;										   //재작업인(C열연정정)
					}

				} else if ("F".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "3"; // 판정보류 
				}

				//2pass재 작업 대상
				if ("Z".equals(sSkinPassYn) && ("C".equals(sCurrProgCd)||"D".equals(sCurrProgCd))) {
					ydAimRtGp	= "EA";
				}
			
			}
			 
			sMsg = "진도코드: " + sCurrProgCd+" 야드목표행선지구분: " + ydAimRtGp;
			commUtils.printLog(logId, sMsg, "S-");
	 
			rVal[0] = commUtils.trim(ydAimRtGp);
			rVal[1] = commUtils.trim(sCurrProgCd);

			commUtils.printLog(logId, mthdNm, "S-");

			return rVal;
			 
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

	}		

	/**
	 *      [A] 오퍼레이션명 : 진도코드 get
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getCoilCurrProgCd_PIDEV(JDTORecord rcvMsg) throws DAOException {	
		String mthdNm = "진도코드Check[CCoilDAO.getCoilCurrProgCd] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
			//수신 항목 값
			String msgId     = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String TcCode 	 = commUtils.getMsgId(rcvMsg);	//TC_CD
			String sStlNo 	 = commUtils.trim(rcvMsg.getFieldString("STOCK_ID"));//재료
			String sInfoGp   = commUtils.trim(rcvMsg.getFieldString("INFO_GP") );// 정보구분 
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }

			/**********************************************************
			* 2. COILCOMM READ
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);

			jrParam.setField("COIL_NO"	, sStlNo); //충당재료
			jrParam.setField("MODIFIER" , sModifier); //수정자

			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilComByCurrProgCd 
			SELECT DECODE(CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',CURR_PROG_CD) AS CURR_PROG_CD
		     	 , RETURN_GP  --반납구분
		      FROM USRPTA.TB_PT_COILCOMM 
		     WHERE COIL_NO = :V_COIL_NO   -- 재료번호
		 	*/
			JDTORecordSet jsStl = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilComByCurrProgCd", logId, mthdNm, "CoilComm 조회");
			
			String ydStocMv = "";
			
			if (jsStl != null && jsStl.size() > 0) {
				String CurrProgCd 	= commUtils.trim(jsStl.getRecord(0).getFieldString("CURR_PROG_CD"));//진도코드
				String ReturnGp 	= commUtils.trim(jsStl.getRecord(0).getFieldString("RETURN_GP")); 	//반납구분
			   	
				// if(CConstant.DMYDR008.equals(TcCode)){			//코일제품반납대기
		    	if( "M10LMYDJ1021".equals(TcCode) ){			//코일제품반납대기
		    		if(CConstant.RETURN_GP_1.equals(ReturnGp)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_JR;
		    		}else{
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_JG;
		    		}
		    		
//		    	}else if(CConstant.DMYDR005.equals(TcCode)||			//코일제품출하지시대기 
//		    			 CConstant.DMYDR004.equals(TcCode)|| 			//외판슬라브출하지시대기
//		    			 CConstant.DMYDR033.equals(TcCode)){			//코일제품반품
		    		
		    	}else if(( "M10LMYDJ1011".equals(TcCode) && "4".equals(sInfoGp) ) ||			//코일제품출하지시대기 
		    			 ( "M10LMYDJ1013".equals(TcCode) && "4".equals(sInfoGp) ) || 			//외판슬라브출하지시대기
		    			 ( "M10LMYDJ1011".equals(TcCode) && "3".equals(sInfoGp) ) ){			//코일제품반품
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_KG;
		    		
//		    	}else if(CConstant.DMYDR027.equals(TcCode)||			//코일제품보관지시 
//		    			 CConstant.DMYDR030.equals(TcCode)){			//코일제품출하완료		    			
		    			
		    	}else if(( "M10LMYDJ1011".equals(TcCode) && "2".equals(sInfoGp) )||			//코일제품보관지시 
		    			   "M10LMYDJ1071".equals(TcCode) ){			//코일제품출하완료
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_MG;
		    		
		    			
//		    	}else if(CConstant.DMYDR016.equals(TcCode)){			//외판슬라브운송지시대기		    			
		    	}else if( "M10LMYDJ1013".equals(TcCode) && "5".equals(sInfoGp) ){			//외판슬라브운송지시대기
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_NG;
		    		
//		    	}else if(CConstant.DMYDR060.equals(TcCode)||			//코일제품운송지시		    			
		    	}else if( "M10LMYDJ1031".equals(TcCode) ||			//코일제품운송지시
		    			  "M10LMYDJ1032".equals(TcCode) ||			//후판운송상차지시
		    			  "M10LMYDJ1033".equals(TcCode) ||			//외판슬라브운송상차지시
		    			  "M10LMYDJ1035".equals(TcCode) 			//임가공운송상차지시
		    			 ){			
		    		
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_LG;
		    	}else if(CConstant.CURR_PROG_CD_COIL_A.equals(CurrProgCd)||
		    			 CConstant.CURR_PROG_CD_COIL_R.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_AC;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_B.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_BC;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_C.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_CC;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_D.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_DC;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_E.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_CS;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_F.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_FC;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_K.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_KG;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_G.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_GC;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_H.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_HG;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_J.equals(CurrProgCd)){
		    		if(CConstant.RETURN_GP_1.equals(ReturnGp)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_JR;
		    		}else{
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_JG;
		    		}
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_L.equals(CurrProgCd)){//코일제품상차지시 
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_LG;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_N.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_NG;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_M.equals(CurrProgCd)||
		    			 CConstant.CURR_PROG_CD_COIL_P.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_MG;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_X.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_XG;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_Y.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_YG;
		    		
		    	}else if(CConstant.CURR_PROG_CD_COIL_Z.equals(CurrProgCd)){
		    			ydStocMv = CConstant.NEW_STOCK_MOVE_TERM_ZG;
		    	}															
		    	
		    	jrRtn.setResultCode(logId);	//Log ID
		    	jrRtn.setResultMsg(mthdNm);	//Log Method Name
//		    	jrRtn.setField("STOCK_ID"		, StlNo); //충당재료
//		    	jrRtn.setField("MODIFIER" 		, modifier); //수정자
		    	jrRtn.setField("CURR_PROG_CD"  	, CurrProgCd); 	//진도코드
		    	jrRtn.setField("STOCK_MOVE_TERM", ydStocMv  );	//저장품 이동 조건
			} 

			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return jrRtn;
		} catch (Exception e) {
			return jrRtn;
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 :  신규시스템 적용 여부
	 *      -- 
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String ApplyYn_AT0000(String logId, String mthdNms,String sItem) throws DAOException {

		String mthdNm = "자동화 신규시스템 적용여부[CCoilDao.ApplyYn_AT0000] < " + mthdNms;
		String szAPPLY_YN = "N";

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			//수신 항목 값
			/**********************************************************
			* 2. 열정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			//jrParam.setField("ITEM"      , sItem      ); //ITEM

			/* 
			SELECT 
			     'N' AS APPLY_AT0000 -- 제품 HOT코일 결로존 권하위치 변경 하지 못하게 처리
			  FROM DUAL
			*/  
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCCoilApplyYn_AT0000", logId, mthdNm, "적용여부 Read"); 

			if (jsChk.size() > 0) {
				szAPPLY_YN    = commUtils.trim(jsChk.getRecord(0).getFieldString(sItem));
			}
			if(szAPPLY_YN == null || "".equals(szAPPLY_YN)){
				szAPPLY_YN = "N";
			}
			commUtils.printLog(logId, mthdNm, "S-");

			return szAPPLY_YN;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return szAPPLY_YN;
		} catch (Exception e) {
			return szAPPLY_YN;
		}
	}
		
}
