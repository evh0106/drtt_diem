/**
 * @(#)YsComm
 *
 * @version          V1.00
 * @author           조병기
 * @date             2014/12/22
 *
 * @description      BILLET 야드 공통 처리 EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.common.session;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ys.bl.session.BlYsComm;
import com.inisteel.cim.ys.bt.session.BtYsComm;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.gds.session.GdsYsComm;
import com.inisteel.cim.ys.sbr.session.SbrYsComm;


/**
 *      [A] 클래스명 : 특수강 공통 야드 공통 처리
 *
*/

public class YsComm {
	
	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();	
	private BlYsComm blYsComm = new BlYsComm();
	private GdsYsComm gdsYsComm = new GdsYsComm();	
	private BtYsComm btYsComm = new BtYsComm();	
	private SbrYsComm sbrYsComm = new SbrYsComm();
	
	/***************************************************************************
	 * 공통 Check
	 **************************************************************************/
	
	/**
	 *      [A] 오퍼레이션명 : 설비상태 Check
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord chkEqpStat(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "설비상태Check[YsComm.chkEqpStat] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try {
			commUtils.printLog(logId, methodNm, "S+");

			jrRtn.setField("YD_L3_HD_RS_CD", "EQ99"); //야드L3처리결과코드(Error)
			jrRtn.setField("YD_L3_MSG"     , "오류:설비상태Check 예상치 못한 오류"); //야드L3MESSAGE(40Byte)

			//수신 항목 값
			String ydEqpId = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //야드설비ID
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
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, "");

			jrParam.setField("YD_EQP_ID", ydEqpId); //야드설비ID

			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatEqp", logId, methodNm, "설비상태 Check"); 

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
			
			jrRtn.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드
			jrRtn.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return jrRtn;
		} catch (Exception e) {
			return jrRtn;
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 스케줄코드 Check
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord chkSchCd(JDTORecord rcvMsg) {
		String methodNm = "스케줄코드Check[YsComm.chkSchCd] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try {
			commUtils.printLog(logId, methodNm, "S+");

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
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, "");

			jrParam.setField("YD_SCH_CD", ydSchCd); //야드스케쥴코드

			//야드스케쥴금지유무 조회
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatSchCd", logId, methodNm, "야드스케쥴금지유무 조회"); 
			

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

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return jrRtn;
		} catch (Exception e) {
			return jrRtn;
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 스케줄코드 및 크레인 Check
	 *
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord jrRtn
	 *      @throws DAOException
	*/
	public JDTORecord chkSchCdEqp(JDTORecord jrParam) {
		String methodNm = "스케줄코드 및 크레인 Check[YdComm.chkSchCdEqp] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydSchCd = commUtils.trim(jrParam.getFieldString("YD_SCH_CD")); //야드스케쥴코드

			if ("".equals(ydSchCd)) {
				throw new Exception("스케쥴코드 없음");
			} else if (ydSchCd.length() < 8) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 이상");
			}
			
			//야드스케쥴금지유무 조회
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatSchCd", logId, methodNm, "야드스케쥴금지유무 조회"); 
			

			String ydSchProhExn = "";  //야드스케쥴금지유무
			String ydEqpId      = "";  //야드설비ID(작업크레인)

			if (jsChk.size() > 0) {
				ydSchProhExn = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PROH_EXN"));
				ydEqpId      = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"      ));
			}

			if ("".equals(ydSchProhExn)) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 정보 없음");
			} else if ("Y".equals(ydSchProhExn)) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 기동금지");
			} else if ("".equals(ydEqpId)) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 작업가능 크레인 없음");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jsChk.getRecord(0);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 대차스케줄 상차완료 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTcarSchLdCmpl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차스케줄 상차완료 처리[YdComm.trtTcarSchLdCmpl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydEqpId = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID" )); //야드설비ID

			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			}

			//전문 Return
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID", ydEqpId);	//야드설비ID

			/**********************************************************
			* 1. 대차 상차 정보 Check
			**********************************************************/
			//대차상차스케쥴 정보 조회
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getTcarSchLdCmpl 
			--대차스케줄 대차상차완료 조회 
			SELECT YD_TCAR_SCH_ID
			      ,YD_CARLD_WRK_BOOK_ID
			      ,CASE WHEN TC_MTL_SH > 0 THEN
			            CASE WHEN TC_SCH_YN = 'N' OR
			                     (TC_SCH_YN = 'Y' AND TC_MTL_SH = WB_MTL_SH) THEN 'Y'
			            ELSE 'N' END
			       ELSE 'N' END AS TCAR_LD_CMPL_YN --대차상차완료여부
			      ,'대차스케쥴ID : '||YD_TCAR_SCH_ID||', 상차작업예약ID : '||YD_CARLD_WRK_BOOK_ID||', 스케쥴코드 : '||YD_SCH_CD||
			       ', 대차이송재료매수 : '||TO_CHAR(TC_MTL_SH)||', 작업예약재료매수 : '||TO_CHAR(WB_MTL_SH) AS TCAR_SCH_ST_MSG
			  FROM (SELECT TS.YD_TCAR_SCH_ID
			              ,TS.YD_CARLD_WRK_BOOK_ID
			              ,WB.YD_SCH_CD
			              ,DECODE(SUBSTR(WB.YD_SCH_CD,3,2),'TC','Y','N') AS TC_SCH_YN --대차스케줄여부
			              ,(SELECT COUNT(*)
			                  FROM TB_YS_TCARFTMVMTL TM
			                 WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
			                   AND TM.DEL_YN = 'N') AS TC_MTL_SH --대차이송재료매수
			              ,(SELECT COUNT(*)
			                  FROM TB_YS_WRKBOOKMTL WM
			                 WHERE WM.YD_WBOOK_ID = WB.YD_WBOOK_ID) AS WB_MTL_SH --작업예약재료매수
			          FROM TB_YS_TCARSCH TS
			              ,TB_YS_WRKBOOK WB
			         WHERE TS.YD_CARLD_WRK_BOOK_ID = WB.YD_WBOOK_ID(+)
			           AND TS.YD_EQP_ID = :V_YD_EQP_ID
			           AND TS.DEL_YN    = 'N')

			 */     
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getTcarSchLdCmpl", logId, methodNm, "대차상차완료 조회");

			if (jsChk == null || jsChk.size() <= 0) {
				throw new Exception("대차 상차스케쥴 정보가 없습니다.");
		    }

			JDTORecord jrChk = jsChk.getRecord(0);

			String ydTcarSchId      = commUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID"      )); //야드대차스케쥴ID
			String ydCarldWrkBookId = commUtils.trim(jrChk.getFieldString("YD_CARLD_WRK_BOOK_ID")); //야드상차작업예약ID
			String tcarLdCmplYn     = commUtils.trim(jrChk.getFieldString("TCAR_LD_CMPL_YN"     )); //대차상차완료여부

			commUtils.printLog(logId, commUtils.trim(jrChk.getFieldString("TCAR_SCH_ST_MSG")) + " >> 대차상차완료여부 : [" + tcarLdCmplYn + "]", "SL");
			
			jrParam.setField("YD_TCAR_SCH_ID"      , ydTcarSchId     ); //야드대차스케쥴ID
			jrParam.setField("YD_CARLD_WRK_BOOK_ID", ydCarldWrkBookId); //야드상차작업예약ID

			/**********************************************************
			* 2. 상차완료이면 하차작업예약 생성
			**********************************************************/
			if ("Y".equals(tcarLdCmplYn)) {
				//상차완료이면 하차작업예약 생성
				//야드하차작업예약ID 생성
				String ydCarudWrkBookId = commDao.getSeqId(logId, methodNm, "WrkBook");
	
				if ("".equals(ydCarudWrkBookId)) {
					throw new Exception("대차 하차작업예약ID 생성 중 오류가 발생하였습니다.");
				}

				//작업예약 등록
				jrParam.setField("YD_CARUD_WRK_BOOK_ID", ydCarudWrkBookId); //야드하차작업예약ID

				//하차 작업예약 등록
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updTcarSchInsUdWb 
				--대차스케줄 하차작업예약 등록 
				MERGE INTO TB_YS_WRKBOOK WB USING (
				SELECT :V_YD_CARUD_WRK_BOOK_ID AS YD_WBOOK_ID      --야드작업예약ID
				      ,:V_MODIFIER             AS MODIFIER         --수정자
				      ,SYSDATE                 AS MOD_DDTT         --수정일시
				      ,'N'                     AS DEL_YN           --삭제유무
				      ,WB.YD_GP                                    --야드구분
				      ,WB.YD_BAY_GP                                --야드동구분
				      ,WB.YD_SCH_CD                                --야드스케쥴코드
				      ,(SELECT CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN_PRIOR1
				                    WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN_PRIOR2
				                END 
				            FROM TB_YS_SCHRULE A
				                ,(
				                    SELECT YD_GP
				                          ,YD_BAY_GP
				                          ,YD_SCH_GP
				                          ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 1, YD_EQP_STAT, '') ELSE 'O' END), 'X') AS STAT1 
				                          ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 2, YD_EQP_STAT, '') ELSE 'X' END), 'X') AS STAT2 
				                    FROM   (
				                                SELECT YD_EQP_ID
				                                      ,YD_GP
				                                      ,YD_BAY_GP
				                                      ,DECODE(YD_EQP_STAT,'B','C','O') AS YD_EQP_STAT
				                                      ,SUBSTR(YD_EQP_NO,-1) AS CRN_NO 
				                                      ,DECODE(YD_EQP_GP,'CR','CR','S'||SUBSTR(YD_EQP_ID,-1)) AS YD_SCH_GP
				                                FROM   TB_YS_EQP
				                                WHERE  YD_EQP_GP IN ('CR','SC')
				                           )
				                    GROUP BY YD_GP, YD_BAY_GP , YD_SCH_GP             
				                 ) B
				            WHERE 1=1
				            AND A.YD_SCH_CD    = WB.YD_SCH_CD
				            AND   A.YD_DATA_GP = 'M'
				            AND   A.YD_SCH_GP  = B.YD_SCH_GP
				            AND   A.YD_GP      = B.YD_GP
				            AND   A.YD_BAY_GP  = B.YD_BAY_GP
				            AND   A.YD_CRN_STAT1 = B.STAT1
				            AND   A.YD_CRN_STAT2 = B.STAT2
				      ) AS YD_SCH_PRIOR                --야드스케쥴우선순위  
				      ,'W'                     AS YD_SCH_PROG_STAT --야드스케쥴진행상태(스케줄수행대기)
				      ,'M'                     AS YD_SCH_ST_GP     --야드스케쥴기동구분(Manual)
				      ,'1'                     AS YD_SCH_REQ_GP    --야드스케쥴요청구분(대차상차완료)
				      ,WB.YD_TO_LOC_DCSN_MTD                       --야드To위치결정방법
				      ,WB.YD_TO_LOC_GUIDE                          --야드To위치Guide
				      ,WB.YD_WRK_PLAN_TCAR                         --야드작업계획대차
				  FROM (SELECT WB.YD_GP
				              ,WB.YD_AIM_BAY_GP AS YD_BAY_GP
				              ,WB.YD_GP||WB.YD_AIM_BAY_GP||SUBSTR(WB.YD_WRK_PLAN_TCAR,3)||'LM' AS YD_SCH_CD
				              ,CASE WHEN WB.YD_TO_LOC_DCSN_MTD = 'F' AND WB.YD_AIM_BAY_GP != SUBSTR(WB.YD_TO_LOC_GUIDE,2,1)
				                    THEN 'S' ELSE WB.YD_TO_LOC_DCSN_MTD END AS YD_TO_LOC_DCSN_MTD
				              ,CASE WHEN WB.YD_TO_LOC_DCSN_MTD = 'F' AND WB.YD_AIM_BAY_GP != SUBSTR(WB.YD_TO_LOC_GUIDE,2,1)
				                    THEN ''  ELSE WB.YD_TO_LOC_GUIDE    END AS YD_TO_LOC_GUIDE
				              ,WB.YD_WRK_PLAN_TCAR
				          FROM TB_YS_WRKBOOK WB
				         WHERE WB.YD_WBOOK_ID = :V_YD_CARLD_WRK_BOOK_ID) WB
				) DD ON (WB.YD_WBOOK_ID = DD.YD_WBOOK_ID)
				WHEN NOT MATCHED THEN
				INSERT (WB.YD_WBOOK_ID       , WB.REGISTER       , WB.REG_DDTT        , WB.MODIFIER    , WB.MOD_DDTT     ,
				        WB.DEL_YN            , WB.YD_GP          , WB.YD_BAY_GP       , WB.YD_SCH_CD   , WB.YD_SCH_PRIOR ,
				        WB.YD_SCH_PROG_STAT  , WB.YD_SCH_ST_GP   , WB.YD_SCH_REQ_GP   , WB.YD_AIM_YD_GP, WB.YD_AIM_BAY_GP,
				        WB.YD_TO_LOC_DCSN_MTD, WB.YD_TO_LOC_GUIDE, WB.YD_WRK_PLAN_TCAR)
				VALUES (DD.YD_WBOOK_ID       , DD.MODIFIER       , DD.MOD_DDTT        , DD.MODIFIER    , DD.MOD_DDTT     ,
				        DD.DEL_YN            , DD.YD_GP          , DD.YD_BAY_GP       , DD.YD_SCH_CD   , DD.YD_SCH_PRIOR ,
				        DD.YD_SCH_PROG_STAT  , DD.YD_SCH_ST_GP   , DD.YD_SCH_REQ_GP   , DD.YD_GP       , DD.YD_BAY_GP    ,
				        DD.YD_TO_LOC_DCSN_MTD, DD.YD_TO_LOC_GUIDE, DD.YD_WRK_PLAN_TCAR)
				*/        
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updTcarSchInsUdWb", logId, methodNm, "하차 작업예약 등록");
				
				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updTcarSchInsUdWbMtl 
				--대차스케줄 하차작업예약재료 등록 
				MERGE INTO TB_YS_WRKBOOKMTL WM USING (
				SELECT :V_YD_CARUD_WRK_BOOK_ID AS YD_WBOOK_ID
				      ,TM.SSTL_NO
				      ,:V_MODIFIER             AS MODIFIER
				      ,SYSDATE                 AS MOD_DDTT
				      ,'N'                     AS DEL_YN
				      ,WB.YS_STK_COL_GP
				      ,'01'                    AS YS_STK_BED_NO
				      ,TM.YS_STK_LYR_NO
				      ,COUNT(*) OVER () - ROW_NUMBER() OVER (ORDER BY TM.YS_STK_LYR_NO) + 1 AS YD_UP_COLL_SEQ
				  FROM TB_YS_TCARFTMVMTL TM
				      ,(SELECT WB.YD_GP||WB.YD_AIM_BAY_GP||SUBSTR(WB.YD_WRK_PLAN_TCAR,3) AS YS_STK_COL_GP
				          FROM TB_YS_WRKBOOK WB
				         WHERE WB.YD_WBOOK_ID = :V_YD_CARLD_WRK_BOOK_ID) WB
				 WHERE TM.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
				   AND TM.DEL_YN         = 'N'
				) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.SSTL_NO = DD.SSTL_NO)
				WHEN NOT MATCHED THEN
				INSERT (WM.YD_WBOOK_ID  , WM.SSTL_NO       , WM.REGISTER      , WM.REG_DDTT     ,
				        WM.MODIFIER     , WM.MOD_DDTT      , WM.DEL_YN        , WM.YS_STK_COL_GP,
				        WM.YS_STK_BED_NO, WM.YS_STK_LYR_NO , WM.YD_UP_COLL_SEQ)
				VALUES (DD.YD_WBOOK_ID  , DD.SSTL_NO       , DD.MODIFIER      , DD.MOD_DDTT     ,
				        DD.MODIFIER     , DD.MOD_DDTT      , DD.DEL_YN        , DD.YS_STK_COL_GP,
				        DD.YS_STK_BED_NO, DD.YS_STK_LYR_NO , DD.YD_UP_COLL_SEQ)
				*/        
				
				
				
				//하차 작업예약재료 등록
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updTcarSchInsUdWbMtl", logId, methodNm, "하차 작업예약재료 등록");

				/**********************************************************
				* 3. 상차 대차스케줄 수정
				*  - 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등
				**********************************************************/
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updTcarSchUpdLdSch 
				--대차스케줄 상차스케줄 수정 
				MERGE INTO TB_YS_TCARSCH TS USING (
				SELECT TM.YD_TCAR_SCH_ID
				      ,TM.YD_EQP_WRK_SH
				      ,TM.YD_EQP_WRK_WT
				      ,WB.YD_WBOOK_ID           AS YD_CARUD_WRK_BOOK_ID
				      ,SUBSTR(WB.YD_SCH_CD,1,6) AS YD_CARUD_STOP_LOC
				  FROM TB_YS_WRKBOOK WB
				      ,(SELECT TM.YD_TCAR_SCH_ID
				              ,COUNT(*)                AS YD_EQP_WRK_SH
				              ,SUM(ST.YD_MTL_WT)       AS YD_EQP_WRK_WT
				              ,:V_YD_CARUD_WRK_BOOK_ID AS YD_CARUD_WRK_BOOK_ID
				          FROM TB_YS_TCARFTMVMTL TM
				              ,TB_YS_STOCK       ST
				         WHERE TM.SSTL_NO         = ST.SSTL_NO
				           AND TM.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
				           AND TM.DEL_YN         = 'N'
				         GROUP BY TM.YD_TCAR_SCH_ID) TM
				  WHERE TM.YD_CARUD_WRK_BOOK_ID = WB.YD_WBOOK_ID(+)
				) DD ON (TS.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID)
				WHEN MATCHED THEN UPDATE SET
					 TS.MODIFIER             = :V_MODIFIER
				    ,TS.MOD_DDTT             = SYSDATE
				    ,TS.YD_EQP_WRK_STAT      = 'L' --영차
				    ,TS.YD_CAR_PROG_STAT     = '5' --상차완료
				    ,TS.YD_EQP_WRK_SH        = DD.YD_EQP_WRK_SH
				    ,TS.YD_EQP_WRK_WT        = DD.YD_EQP_WRK_WT
				    ,TS.YD_CARLD_ST_DT       = NVL(TS.YD_CARLD_ST_DT  ,SYSDATE)
				    ,TS.YD_CARLD_CMPL_DT     = NVL(TS.YD_CARLD_CMPL_DT,SYSDATE)
				    ,TS.YD_CARUD_WRK_BOOK_ID = DD.YD_CARUD_WRK_BOOK_ID
				    ,TS.YD_CARUD_STOP_LOC    = DD.YD_CARUD_STOP_LOC
				 */   

				    
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updTcarSchUpdLdSch", logId, methodNm, "상차 대차스케줄 수정");
				
				/**********************************************************
				* 4. 대차작업실적 및 대차출발지시 전송
				**********************************************************/
				//블름 대차 출발
				if ("A".equals(ydEqpId.substring(0, 1))) {
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN1L005", jrParam));
				} else if ("K".equals(ydEqpId.substring(0, 1))) {
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN4L005", jrParam));
				}
			} else {
				//상차완료가 아니면
				throw new Exception("대차 상차완료처리할 수 있는 상태가 아닙니다.");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 *      [A] 오퍼레이션명 : 대차스케줄 하차완료 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTcarSchUdCmpl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차스케줄 하차완료 처리[YsComm.trtTcarSchUdCmpl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			//수신 항목 값
			String ydEqpId     = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID(대차)
			String ydTcarSchId = commUtils.trim(rcvMsg.getFieldString("YD_TCAR_SCH_ID")); //야드대차스케쥴ID
			String ydL2Id	   = commUtils.trim(rcvMsg.getFieldString("YD_L2_ID")); //L2 ID (N1,N2...N6);
			String ydBayGpTo = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP" )); //야드동구분(상차동)
			String ydWrkStat = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_STAT"));
			String ydCurrBayGp1 = commUtils.trim(rcvMsg.getFieldString("YD_CURR_BAY_GP"));

			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			}

			//전문 Return
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID", ydEqpId);	//야드설비ID

			/**********************************************************
			* 1. 대차 하차스케쥴 정보 조회
			**********************************************************/
			if ("".equals(ydTcarSchId)) {
				//대차하차스케쥴 조회
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getTcarSchUdCmpl", logId, methodNm, "대차하차스케쥴 조회");
				
				if (jsChk != null && jsChk.size() > 0) {
					JDTORecord jrChk = jsChk.getRecord(0);

					ydTcarSchId = commUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID")); //야드대차스케쥴ID
				} else {
					throw new Exception("대차 하차스케쥴 정보가 없습니다.");
			    }
			}
			
			jrParam.setField("YD_TCAR_SCH_ID", ydTcarSchId);	//야드대차스케쥴ID

			/**********************************************************
			* 2. 대차스케줄 삭제
			**********************************************************/
			//대차이송재료 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updTcarSchDelMtl", logId, methodNm, "대차이송재료 삭제");

			//대차스케줄 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updTcarSchDelSch", logId, methodNm, "대차스케줄 삭제");
			
			/**********************************************************
			* 3. L2 대차작업실적(YSN1L006) 전송
			**********************************************************/
			if("N1".equals(ydL2Id)) {
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN1L006", jrParam));
			} else if("N4".equals(ydL2Id)) {
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN4L006", jrParam));
			} else if("N5".equals(ydL2Id)) { // 소형야드 대차이동요구
				
				jrParam.setField("EQP_WRK_STAT" , ydWrkStat);	//상하차
				jrParam.setField("TCAR_LOC"   	, ydBayGpTo);	//목표동
				jrParam.setField("ST_LOC"   	, ydCurrBayGp1);//출발동
				jrParam.setField("END_LOC"   	, ydBayGpTo);	//도착동
				
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L312", jrParam));
			}

			/**********************************************************
			* 4. 공대차출발지시 처리
			**********************************************************/
			jrRtn = commUtils.addSndData(jrRtn, this.trtTcarSchLevWo(rcvMsg));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 *      [A] 오퍼레이션명 : 대차스케줄 공대차출발지시 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTcarSchLevWo(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차스케줄 공대차출발지시 처리[YsComm.trtTcarSchLevWo] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydEqpId   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID" )); //야드설비ID
			String ydBayGpTo = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP" )); //야드동구분(상차동)
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자
			String ydL2Id	 = commUtils.trim(rcvMsg.getFieldString("YD_L2_ID")); //L2 ID (N1,N2...N6);
			String ydWrkStat = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_STAT"));
			String ydCurrBayGp1 = commUtils.trim(rcvMsg.getFieldString("YD_CURR_BAY_GP"));

			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			}

			//전문 Return
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID", ydEqpId);	//야드설비ID

			/**********************************************************
			* 1. 대차스케줄 정보 조회
			**********************************************************/
			String ydCurrBayGp      = ""; //야드현재동구분
			String ydHomeBayGp      = ""; //야드Home동구분
			String autoTcarSchYn    = ""; //자동대차스케줄여부
			String ydTcarSchId      = ""; //야드대차스케쥴ID
			String ydWbookIdCurr    = ""; //야드작업예약ID(현재 대차스케줄 상차작업예약ID)
			String ydBayGpCurr      = ""; //야드동구분(현재 대차스케줄 상차동)
			String ydAimBayGpCurr   = ""; //야드목표동구분(현재 대차스케줄 하차동)
			String ydWbookIdNext    = ""; //야드작업예약ID(다음 상차작업예약ID)
			String ydBayGpNext      = ""; //야드동구분(다음 작업예약 상차동)
			String ydAimBayGpNext   = ""; //야드목표동구분(다음 작업예약 하차동)
			String sBloomBBayWork   = ""; //블룸대차 역이송상차작업용 항목
		 			
			//대차스케쥴정보(공대차출발지시) 조회
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getTcarSchLevWo", logId, methodNm, "대차스케쥴정보(공대차출발지시) 조회");

			if (jsChk != null && jsChk.size() > 0) {
				jsChk.first();
				JDTORecord jrChk = jsChk.getRecord();
//				JDTORecord jrChk = jsChk.getRecord(0);

				ydCurrBayGp    = commUtils.trim(jrChk.getFieldString("YD_CURR_BAY_GP"    ));
				ydHomeBayGp    = commUtils.trim(jrChk.getFieldString("YD_HOME_BAY_GP"    ));
				autoTcarSchYn  = commUtils.trim(jrChk.getFieldString("AUTO_TCAR_SCH_YN"  ));
				ydTcarSchId    = commUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID"    ));
				ydWbookIdCurr  = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID_CURR"  ));
				ydBayGpCurr    = commUtils.trim(jrChk.getFieldString("YD_BAY_GP_CURR"    ));
				ydAimBayGpCurr = commUtils.trim(jrChk.getFieldString("YD_AIM_BAY_GP_CURR"));
				ydWbookIdNext  = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID_NEXT"  ));
				ydBayGpNext    = commUtils.trim(jrChk.getFieldString("YD_BAY_GP_NEXT"    ));
				ydAimBayGpNext = commUtils.trim(jrChk.getFieldString("YD_AIM_BAY_GP_NEXT"));
				sBloomBBayWork = commUtils.trim(jrChk.getFieldString("BLOOM_B_BAY_WORK"));

				//현재동이 없으면 Home동을 현재동으로
				if ("".equals(ydCurrBayGp)) {
					ydCurrBayGp = ydHomeBayGp;
				}
				
				if ("B".equals(commUtils.trim(jrChk.getFieldString("YD_EQP_STAT")))) {
					throw new Exception("대차[" + ydEqpId + "]는 고장 상태입니다.");
				} else if (!"1".equals(commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE")))) {
					throw new Exception("대차[" + ydEqpId + "]는 Off-Line 상태입니다.");
				} else if ("Y".equals(commUtils.trim(jrChk.getFieldString("TC_MTL_YN")))) {
					throw new Exception("대차스케줄[" + ydEqpId + " : " + ydTcarSchId + "]의 이송재료가 존재하여 공대차출발지시를 할 수 없습니다.");
				}
			} else {
				throw new Exception("대차 정보가 없습니다.");
		    }

			/**********************************************************
			* 2. 상차작업예약ID 및 상차도착위치, 하차도착위치 결정
			* 2.1 대차스케줄의 야드상차작업예약ID가 있으면 그대로
			* 2.2 대차스케줄의 야드상차작업예약ID가 없으면
			*   - 야드작업계획대차의 작업예약 정보로 작업예약 조회
			* 2.3 야드작업계획대차의 작업예약이 없고 자동대차스케줄 기준이 'Y'이면
			*   - 자동 스케줄 기준에 해당하는 작업예약 생성
			**********************************************************/
			String ydCarldWrkBookId = ""; //야드상차작업예약ID
			String ydCarldLevLoc    = ""; //야드상차출발위치
			String ydCarldStopLoc   = ""; //야드상차정지위치
			String ydCarudStopLoc   = ""; //야드하차정지위치
			String ydCarProgStat    = "0"; //야드차량진행상태(상차대기)
			String ydBayGp          = ""; //야드동구분(상차동)
			String ydAimBayGp       = ""; //야드동구분(하차동)

			if (!"".equals(ydWbookIdCurr)) {
				//대차스케줄 및 상차작업예약이 있는 경우
				//상차동으로 출발지시
				ydCarldWrkBookId = ydWbookIdCurr;	//야드상차작업예약ID
				ydBayGp          = ydBayGpCurr;		//야드상차동
				ydAimBayGp       = ydAimBayGpCurr;	//야드하차동
			} else if (!"".equals(ydWbookIdNext)) {
				//대차의 다음 상차작업예약이 있는 경우
				ydCarldWrkBookId = ydWbookIdNext;	//야드상차작업예약ID
				ydBayGp          = ydBayGpNext;		//야드상차동
				ydAimBayGp       = ydAimBayGpNext;	//야드하차동
			} else if ("Y".equals(autoTcarSchYn)) {
				//대차 상차작업예약이 없으면 상차작업예약을 생성
				//자동 대차스케줄 처리(상차작업예약 생성)
				/*
				JDTORecordSet jsWbMtl = commDao.getTcarSch("WbMtl", jrParam);
				int wbMtlSh = jsWbMtl.size();
				JDTORecord jrRow = null;

				if (wbMtlSh > 0) {
					jrRow = jsWbMtl.getRecord(0);

					String ydStkColGp = commUtils.trim(jrRow.getFieldString("YD_STK_COL_GP"));	//야드적치열구분
					String ydGp       = ydStkColGp.substring(0, 1);								//야드구분
					       ydBayGp    = ydStkColGp.substring(1, 2);								//야드동구분(상차동)
					       ydAimBayGp = commUtils.trim(jrRow.getFieldString("YD_AIM_BAY_GP"));	//야드목표동구분(하차동)
					String ydSchCd    = commUtils.trim(jrRow.getFieldString("YD_SCH_CD"    ));	//야드스케쥴코드
					String ydSchPrior = commUtils.trim(jrRow.getFieldString("YD_SCH_PRIOR" ));	//야드스케쥴우선순위
					String ydSchStGp  = "M";													//야드스케쥴기동구분
					if (modifier.length() == 8 && "YD".equals(modifier.substring(2, 4))) {
						ydSchStGp = "A";	//Auto
					}
					
					//작업예약ID 생성
					ydCarldWrkBookId = commDao.getSeqId(logId, methodNm, "WrkBook");

					if ("".equals(ydCarldWrkBookId)) {
						throw new Exception("대차상차 작업예약ID 생성중 오류가 발생하였습니다.");
					}

					//작업예약 등록
					jrParam.setField("V_YD_WBOOK_ID"       , ydCarldWrkBookId); //야드작업예약ID
					jrParam.setField("V_YD_GP"             , ydGp            ); //야드구분
					jrParam.setField("V_YD_BAY_GP"         , ydBayGp         ); //야드동구분
					jrParam.setField("V_YD_SCH_CD"         , ydSchCd         ); //야드스케쥴코드
					jrParam.setField("V_YD_SCH_PRIOR"      , ydSchPrior      ); //야드스케쥴우선순위
					jrParam.setField("V_YD_SCH_PROG_STAT"  , "W"             ); //야드스케쥴진행상태(스케줄수행대기)
					jrParam.setField("V_YD_SCH_ST_GP"      , ydSchStGp       ); //야드스케쥴기동구분
					jrParam.setField("V_YD_SCH_REQ_GP"     , "5"             ); //야드스케쥴요청구분(공대차출발)
					jrParam.setField("V_YD_AIM_YD_GP"      , ydGp            ); //야드목표야드구분
					jrParam.setField("V_YD_AIM_BAY_GP"     , ydAimBayGp      ); //야드목표동구분
					jrParam.setField("V_YD_TO_LOC_DCSN_MTD", "S"             ); //야드TO위치결정방법
					jrParam.setField("V_YD_WRK_PLAN_TCAR"  , ydEqpId         ); //야드작업계획대차

					commDao.insSlabYd("WrkBook", jrParam);

					//작업예약재료 등록
					String[][] wmParam = new String[wbMtlSh][8];
					
					for (int ii = 0; ii < wbMtlSh; ii++) {
						jrRow = jsWbMtl.getRecord(ii);
						
						wmParam[ii][0] = ydCarldWrkBookId;											//야드작업예약ID
						wmParam[ii][1] = commUtils.trim(jrRow.getFieldString("STL_NO"        ));	//재료번호
						wmParam[ii][2] = modifier;													//등록자
						wmParam[ii][3] = modifier;													//수정자
						wmParam[ii][4] = commUtils.trim(jrRow.getFieldString("YD_STK_COL_GP" ));	//야드적치열구분
						wmParam[ii][5] = commUtils.trim(jrRow.getFieldString("YD_STK_BED_NO" ));	//야드적치Bed번호
						wmParam[ii][6] = commUtils.trim(jrRow.getFieldString("YD_STK_LYR_NO" ));	//야드적치단번호
						wmParam[ii][7] = commUtils.trim(jrRow.getFieldString("YD_UP_COLL_SEQ"));	//야드권상모음순서
					}
					
					commDao.upsBatch("WrkBookMtl", wmParam, logId, methodNm);
				}
				*/
			}
			
			//블룸대차 B동 하차작업이후 역이송상차작업이 있으면 B동에 상차도착상태로 만들기 위함.
			if("Y".equals(sBloomBBayWork)){
				ydCarldWrkBookId = "";
				ydBayGpTo		 = "";
				ydHomeBayGp		 = ydCurrBayGp;
			}
			commUtils.printLog(logId, "윤재광 sBloomBBayWork  : [" + sBloomBBayWork + "- ydHomeBayGp => " + ydHomeBayGp , "SL");
			
			//상차출발위치, 상차도착위치, 하차도착위치 Set
			ydCarldLevLoc = ydEqpId.substring(0, 1) + ydCurrBayGp + ydEqpId.substring(2, 6);
			if ("".equals(ydCarldWrkBookId)) {
				//대차 상차작업예약이 없으면
				if ("".equals(ydBayGpTo)) {
					ydCarldStopLoc = ydEqpId.substring(0, 1) + ydHomeBayGp + ydEqpId.substring(2, 6);
				} else {
					ydCarldStopLoc = ydEqpId.substring(0, 1) + ydBayGpTo + ydEqpId.substring(2, 6);
				}
			} else {
				//대차 상차작업예약이 있으면
				ydCarldStopLoc = ydEqpId.substring(0, 1) + ydBayGp     + ydEqpId.substring(2, 6);
				ydCarudStopLoc = ydEqpId.substring(0, 1) + ydAimBayGp  + ydEqpId.substring(2, 6);
			}

			//상차출발위치와 상차도착위치가 같으면 상차도착 상태
			if (ydCarldLevLoc.equals(ydCarldStopLoc)) {
				ydCarProgStat = "2"; //야드차량진행상태(상차도착)
			}
			
			/**********************************************************
			* 3. 대차스케줄 생성 또는 수정
			* 3.1  대차스케줄 없으면 야드대차스케쥴ID 생성하여 대차스케줄 생성
			* 3.1  대차스케줄 있으면 대차스케줄 수정
			**********************************************************/
			if ("".equals(ydTcarSchId)) {
				ydTcarSchId = commDao.getSeqId(logId, methodNm, "TcarSch");

				if ("".equals(ydTcarSchId)) {
					throw new Exception( "대차스케줄ID 생성 중 오류가 발생하였습니다.");
				}
			}
			
			jrParam.setField("YD_TCAR_SCH_ID"      , ydTcarSchId     );	//야드대차스케쥴ID
			jrParam.setField("YD_CAR_PROG_STAT"    , ydCarProgStat   );	//야드차량진행상태
			jrParam.setField("YD_CARLD_WRK_BOOK_ID", ydCarldWrkBookId);	//야드상차작업예약ID
			jrParam.setField("YD_CARLD_LEV_LOC"    , ydCarldLevLoc   );	//야드상차출발위치
			jrParam.setField("YD_CARLD_STOP_LOC"   , ydCarldStopLoc  );	//야드상차정지위치
			jrParam.setField("YD_CARUD_STOP_LOC"   , ydCarudStopLoc  );	//야드하차정지위치
			
			//대차스케줄 수정 또는 생성
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updTcarSchInsSch", logId, methodNm, "대차스케줄 수정 또는 생성");			

			if ("0".equals(ydCarProgStat)) {
				/**********************************************************
				* 4. 상차출발위치와 상차도착위치가 다르면 공대차출발지시 전송
				**********************************************************/
				if("N1".equals(ydL2Id)) {
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN1L005", jrParam));
				} else if("N4".equals(ydL2Id)) {
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN4L005", jrParam));
				} else if("N5".equals(ydL2Id)) { // 소형야드 대차이동요구 송신
					
					jrParam.setField("YD_EQP_WRK_STAT" 	, "U"			);	//상하차
					jrParam.setField("TCAR_LOC"   		, ydBayGpTo		);	//목표동
					jrParam.setField("ST_LOC"   		, ydCurrBayGp1	);	//출발동
					jrParam.setField("END_LOC"   		, ydBayGpTo		);	//도착동
					
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L312", jrParam));
				}	
				
			} else if (!"".equals(ydCarldWrkBookId) && "".equals(ydWbookIdCurr)) {
				/**********************************************************
				* 5. 상차출발위치와 상차도착위치가 같고 신규 작업예약ID이면 크레인스케줄 호출
				**********************************************************/
				//크레인스케줄 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);

				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
				jrYdMsg.setField("YD_WBOOK_ID"  , ydCarldWrkBookId); //야드작업예약ID
				jrYdMsg.setField("YD_SCH_ST_GP" , "A"             ); //야드스케쥴기동구분(Auto)
				jrYdMsg.setField("YD_SCH_REQ_GP", "6"             ); //야드스케쥴요청구분(공대차도착)

				if(ydEqpId.startsWith("B")) { //Bloom 야드
					jrRtn = commUtils.addSndData(jrRtn, blYsComm.getCrnSchMsg(jrYdMsg));
				} else if(ydEqpId.startsWith("C")) { //Billet 야드
					jrRtn = commUtils.addSndData(jrRtn, btYsComm.getCrnSchMsg(jrYdMsg));
				} else if(ydEqpId.startsWith("K")) { //제품창고 야드
					jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getCrnSchMsg(jrYdMsg));
				} else if(ydEqpId.startsWith("D")) { // 소형야드
					jrRtn = commUtils.addSndData(jrRtn, sbrYsComm.getCrnSchMsg(jrYdMsg));
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * 야드 기준 항목 조회
	 * 
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public String getYsRuleItem(String logId, String mthdNms, String sReprCdGp, String sCdGp) throws DAOException {
		String mthdNm = "야드 기준 항목 조회[YsComm.getYsRuleItem] < " + mthdNms;
		String ruleItem = "";

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			// 수신 항목 값
			/**********************************************************
			 * 2. 열정보 read
			 **********************************************************/
			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("REPR_CD_GP", sReprCdGp); // 작업구분
			jrParam.setField("CD_GP", sCdGp); // 구분
			// jrParam.setField("ITEM" , sItem ); //ITEM

			// 필드명 변환 (필드명 -> V_필드명)
			jrParam = commDao.conversionFieldname(jrParam, 0);
			// query id setting
			jrParam.setField("JSPEED_QUERY_ID", "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsRuleList");
			// query execute
			JDTORecordSet jsChk = commDao.getRecordSet(jrParam);

			if (jsChk.size() > 0) {
				ruleItem = commUtils.trim(jsChk.getRecord(0).getFieldString("ITEM"));
				commUtils.printLog(logId, mthdNm, "ITEM : " + ruleItem);
			}

			commUtils.printLog(logId, mthdNm, "S-");

			return ruleItem;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return ruleItem;
		} catch (Exception e) {
			return ruleItem;
		}
	}
	
}
