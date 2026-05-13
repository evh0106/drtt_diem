/**
 * @(#)YmComm
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      B열연 COIL 야드 공통 처리 EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.ym.bcommon.session;

import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bcommon.util.YmConstant;
/**
 *      [A] 클래스명 : B열연 COIL 야드 공통 처리
 *
*/

public class YmComm {
 
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();	

	/***************************************************************************
	 * 공통 Check
	 **************************************************************************/
	/**
	 *      [A] 오퍼레이션명 :  신규시스템 적용 여부
	 *      -- 
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String BCoilApplyYn(String szREPR_CD_GP,String szCD_GP,String szITEM) throws DAOException {
		String methodNm = "신규시스템 적용여부[YmComm.BCoilApplyYn]" ;
		String logId = "";
		String szAPPLY_YN = "N";

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			/**********************************************************
			* 2. 열정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam("", methodNm, "");
			jrParam.setField("REPR_CD_GP", szREPR_CD_GP  ); //작업구분
			jrParam.setField("CD_GP"     , szCD_GP       ); //구분
			jrParam.setField("ITEM"      , szITEM        ); //ITEM

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getBCoilApplyYn
			SELECT NVL(MAX(DTL_ITM1),'N') AS APPLY_YN
			  FROM USRYMA.TB_YM_RULE
			 WHERE REPR_CD_GP = :V_REPR_CD_GP  -- APP001
			   AND CD_GP = :V_CD_GP            -- CD_GP
			   AND ITEM  = :V_ITEM
			   AND DEL_YN = 'N'
			*/  
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getBCoilApplyYn", logId, methodNm, "열정보 Read"); 

			if (jsChk.size() > 0) {
				szAPPLY_YN    = commUtils.trim(jsChk.getRecord(0).getFieldString("APPLY_YN"));
			}
            
			commUtils.printLog(logId, methodNm, "S-");

			return szAPPLY_YN;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
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
	public boolean chkAutoCrn(String szYD_EQP_ID) throws DAOException {
		String methodNm = "자동화 크레인 CHECK [YmComm.chkAutoCrn]" ;
		String logId = "";
		String szYD_EQP_ID_GET = "";

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			/**********************************************************
			* 2. 설비정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam("", methodNm, "");
			jrParam.setField("YD_EQP_ID", szYD_EQP_ID); //공장구분 2,3

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.ChkCrnMode2 
			SELECT YD_EQP_WRK_MODE2
			  FROM TB_YM_EQUIP
			 WHERE DEL_YN = 'N'
			   AND EQUIP_GP = :V_YD_EQP_ID
			*/  
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.ChkCrnMode2", logId, methodNm, "설비정보 조회"); 

			if (jsChk.size() > 0) {
				szYD_EQP_ID_GET    = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE2"));
			}
			//if ("A".equals(szYD_EQP_ID_GET) ||"R".equals(szYD_EQP_ID_GET)){
			if ("A".equals(szYD_EQP_ID_GET)){ //리모컨은 유인
				commUtils.printLog(logId, methodNm, "S-");
				return true;
			} else {
				commUtils.printLog(logId, methodNm, "S-");
				return false;
			}
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
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
	public JDTORecord chkSchCd(JDTORecord rcvMsg) {
		String methodNm = "스케줄코드Check[YmComm.chkSchCd] < " + rcvMsg.getResultMsg();
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
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStatSchCd", logId, methodNm, "야드스케쥴금지유무 조회"); 
			

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
	 *      [A] 오퍼레이션명 : 설비상태 Check
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord chkEqpStat(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "설비상태Check[YmComm.chkEqpStat] < " + rcvMsg.getResultMsg();
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

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStatEqp 
			--설비상태조회 
			SELECT WPROG_STAT     AS YD_EQP_STAT
			     , WORK_MODE      AS YD_EQP_WRK_MODE
				 , STACK_MAX_QNTY	                  --적재 최대 수량
				 , STACK_MAX_WT		                  --적재 최대 중량
			  FROM TB_YM_EQUIP EQ
			 WHERE EQUIP_GP = :V_YD_EQP_ID
			   AND DEL_YN    = 'N' 
			*/	   
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStatEqp", logId, methodNm, "설비상태 Check"); 

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
	 *      [A] 오퍼레이션명 : 설비정보 READ
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord readTcEquipInfo(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "설비정보 READ[YmComm.readEquipInfo] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtnResult = JDTORecordFactory.getInstance().create(); //결과

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String StackColGp = commUtils.trim(rcvMsg.getFieldString("TAG_STACK_COL_GP")); //야드 열 
			/**********************************************************
			* 2. 설비상태 Check
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, "");
			jrParam.setField("STACK_COL_GP", StackColGp); //열

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getToEquipState 
			SELECT
			       EQUIP_STAT              -- 설비상태(O,C)
			     , STACK_MAX_QNTY          -- 적재MAX수량
			     , STACK_STAT              -- 적재상태
			     , WPROG_STAT              -- 작업진행상태
			     , WAIT_STOP_LOC           -- 대기위치
			     , CURR_STOP_LOC           -- 현재위치
			     , CARLOAD_STOP_LOC        -- 상차위치
			     , CARUNLOAD_STOP_LOC      -- 하차위치
			     , CARLOAD_ASSIGN_YN       -- 상차스케쥴지정
			     , CARLOAD_SCH_WORK_KIND   -- 상차스케쥴
			     , CARUNLOAD_ASSIGN_YN     -- 하차스케쥴지정
			     , CARUNLOAD_SCH_WORK_KIND -- 하차스케쥴
			     , WORK_MODE               -- 작업모드
			     , REGISTER  AS  AUTO_YN   -- BACKUP으로 사용됨
			     , EQP_DIR_UP_GP           -- 설비직상차구분('0':직상차없음,1:SPM1 추출,2:SPM2 추출,3:HFL추출,4:HFL결속대)
			     , EQP_DIR_TO_LOC          -- 목적동
			  FROM TB_YM_EQUIP
			 WHERE EQUIP_GP = SUBSTR(:V_STACK_COL_GP,0,1)||'X'||SUBSTR(:V_STACK_COL_GP,3)
			*/
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getToEquipState", logId, methodNm, "설비정보 READ"); 

			if (jsChk.size() > 0) {
				jsChk.absolute(1);
				jrRtnResult  = jsChk.getRecord();
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtnResult;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return jrRtnResult;
		} catch (Exception e) {
			return jrRtnResult;
		}
	}	
	

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 기동 조회
	 *
	 *      @param String JDTORecord rcvMsg
	 *      @return String JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getCrnSchMsg(JDTORecord jrParam) {
 		/***************************************************************************
		 * 스케줄 기동시 사용: procCrnWrkBookMgtStart
		 **************************************************************************/
		
		String methodNm = "크레인스케줄전문조회[YmComm.getCrnSchMsg] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			String currDate   = commUtils.getDateTime14();									//현재시각
			String ydGp       = commUtils.trim(jrParam.getFieldString("YD_GP"        ));	//야드구분
			String ydWbookId  = commUtils.trim(jrParam.getFieldString("YD_WBOOK_ID"  ));	//야드작업예약ID
			String ydSchCd    = commUtils.trim(jrParam.getFieldString("YD_SCH_CD"    ));	//야드스케쥴코드
			String ydEqpId    = commUtils.trim(jrParam.getFieldString("YD_EQP_ID"    ));	//야드설비ID
			String ydSchStGp  = commUtils.trim(jrParam.getFieldString("YD_SCH_ST_GP" ));	//야드스케쥴기동구분
			String ydSchReqGp = commUtils.trim(jrParam.getFieldString("YD_SCH_REQ_GP"));	//야드스케쥴요청구분
			String modifier   = commUtils.trim(jrParam.getFieldString("MODIFIER"     ));	//수정자
			String ejbCallYn  = commUtils.trim(jrParam.getFieldString("EJB_CALL_YN"  ));	//EJBCall여부(신 크레인스케줄)

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
				jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchStartGp 
				--크레인스케줄기동구분조회 
				SELECT WB.YD_GP
				      ,WB.YD_SCH_CD
				      ,(SELECT SR.YD_WRK_CRN
				         FROM TB_YM_SCHEDULERULE SR
				         WHERE SR.YD_SCH_CD = WB.YD_SCH_CD) AS YD_EQP_ID
				  FROM TB_YM_WRKBOOK WB
				 WHERE WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND WB.DEL_YN      = 'N'
				*/	   
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchStartGp", logId, methodNm, "크레인스케줄기동구분 조회");

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

				jrParam.setField("YD_GP", ydGp); //야드구분
			}


			commUtils.printLog(logId, "[작업예약ID:" + ydWbookId + ", 스케쥴코드:" + ydSchCd + ", 설비ID:" + ydEqpId + "]", "SL");

			//크레인스케줄 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, jrParam.getResultMsg(), modifier);
			
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name			
			// 크레인스케줄 기동
			if(ydGp.equals("2")) {
				jrYdMsg.setField("JMS_TC_CD", "YMYMJ202"); //
				
			} else {
				jrYdMsg.setField("JMS_TC_CD", "YMYMJ302"); //
				
			}
			
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate  ); //JMSTC생성일시
			jrYdMsg.setField("YD_WBOOK_ID"       , ydWbookId ); //야드작업예약ID
			jrYdMsg.setField("YD_SCH_CD"         , ydSchCd   ); //야드스케쥴코드
			jrYdMsg.setField("YD_EQP_ID"         , ydEqpId   ); //야드설비ID
			jrYdMsg.setField("YD_SCH_ST_GP"      , ydSchStGp ); //야드스케쥴기동구분
			jrYdMsg.setField("YD_SCH_REQ_GP"     , ydSchReqGp); //야드스케쥴요청구분

			jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
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
		String methodNm = "대차스케줄 하차완료 처리[YmComm.trtTcarSchUdCmpl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			//수신 항목 값
			String ydEqpId     		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); 		//야드설비ID(대차)
			String ydTcarSchId 		= commUtils.trim(rcvMsg.getFieldString("YD_TCAR_SCH_ID")); 		//야드대차스케쥴ID
			String CraneId	   		= commUtils.trim(rcvMsg.getFieldString("CRANE_ID"));       		//크레인 id
			String ydCarUdStopLoc	= commUtils.trim(rcvMsg.getFieldString("YD_CARUD_STOP_LOC"));	//하차완료위치
			
			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			}

			//전문 Return
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

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
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchUdCmpl 
				-- 대차스케줄 대차하차완료 조회  
				SELECT TS.YD_TCAR_SCH_ID
				  FROM TB_YM_TCARSCH TS
				 WHERE TS.YD_EQP_ID = :V_YD_EQP_ID
				   AND TS.DEL_YN    = 'N'
				*/	   
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchUdCmpl", logId, methodNm, "대차하차스케쥴 조회");
				
				if (jsChk != null && jsChk.size() > 0) {
					JDTORecord jrChk = jsChk.getRecord(0);
					ydTcarSchId = commUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID")); //야드대차스케쥴ID
				} else {
					return jrRtn;
			    }
			}
			
			jrParam.setField("YD_TCAR_SCH_ID", ydTcarSchId);	//야드대차스케쥴ID

			/**********************************************************
			* 2. 대차스케줄 삭제
			**********************************************************/
			//대차이송재료 삭제
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchDelMtl 
			-- 대차스케줄재료 삭제 
			UPDATE TB_YM_TCARFTMVMTL
			   SET MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,DEL_YN   = 'Y'
			 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
			   AND DEL_YN   = 'N'

			*/	   
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchDelMtl", logId, methodNm, "대차이송재료 삭제");

	
			jrParam.setField("CRANE_ID"			, CraneId);			//크레인 id
			jrParam.setField("YD_CARUD_STOP_LOC", ydCarUdStopLoc);	//하차완료위치

			//대차스케줄 삭제
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchDelSch
			UPDATE TB_YM_TCARSCH 
			   SET MODIFIER          = :V_MODIFIER
			     , MOD_DDTT          = SYSDATE
			     , DEL_YN            = 'Y'
			     , YD_EQP_WRK_STAT   = 'U'                         --공차
			     , YD_CAR_PROG_STAT  = 'E'                         --하차완료
			     , YD_CARUD_ST_DT    = NVL(YD_CARUD_ST_DT,SYSDATE) --하차개시시간
			     , YD_CARUD_CMPL_DT  = SYSDATE                     --하차완료시간
			     , YD_CARUD_WRK_CRN  = :V_CRANE_ID                 --작업크레인
			     , YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC
			 WHERE YD_TCAR_SCH_ID    = :V_YD_TCAR_SCH_ID
			 */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchDelSch", logId, methodNm, "대차스케줄 삭제");
			
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
		String methodNm = "대차스케줄 공대차출발지시 처리[YmComm.trtTcarSchLevWo] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String sOPRN     = commUtils.nvl(commUtils.trim(rcvMsg.getFieldString("OPRN" )), "N");   //영대차여부
			String ydEqpId   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID" )); //야드설비ID
			String ydBayGpTo = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP" )); //야드동구분(상차동)
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자

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
		 			
			//대차스케쥴정보(공대차출발지시) 조회
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchLevWo 
			-- 대차스케줄 공대차출발지시 조회 - 
			SELECT TS.YD_TCAR_SCH_ID
			      ,EQ.EQUIP_STAT                AS YD_EQP_STAT
			      ,EQ.WORK_MODE                 AS YD_EQP_WRK_MODE
			      ,NVL(SUBSTR(CURR_STOP_LOC,2,1),WB.YD_BAY_GP) 
			                                    AS YD_CURR_BAY_GP --이동중이면 상차동을 현재동으로
			      ,EQ.WAIT_STOP_LOC             AS YD_HOME_BAY_GP
			      ,WB.YD_WBOOK_ID               AS YD_WBOOK_ID_CURR   --현재 대차스케줄 상차작업예약ID
			      ,WB.YD_BAY_GP                 AS YD_BAY_GP_CURR     --현재 대차스케줄 상차동
			      ,WB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_CURR --현재 대차스케줄 하차동
			      ,XB.YD_WBOOK_ID               AS YD_WBOOK_ID_NEXT   --다음 상차작업예약ID
			      ,XB.YD_BAY_GP                 AS YD_BAY_GP_NEXT     --다음 상차동
			      ,XB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_NEXT --다음 하차동
			      ,(SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END
			          FROM TB_YM_TCARFTMVMTL TM
			         WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
			           AND TM.DEL_YN = 'N')     AS TC_MTL_YN
			--      ,NVL(EQ.AUTO_TCAR_SCH_YN,'N') AS AUTO_TCAR_SCH_YN   --자동대차스케줄여부
			  FROM TB_YM_EQUIP   EQ
			      ,TB_YM_TCARSCH TS
			      ,TB_YM_WRKBOOK WB
			      ,(SELECT MIN(YD_WBOOK_ID  ) AS YD_WBOOK_ID
			              ,MIN(YD_BAY_GP    ) AS YD_BAY_GP
			              ,MIN(YD_AIM_BAY_GP) AS YD_AIM_BAY_GP
			          FROM (SELECT YD_WBOOK_ID
			                      ,YD_BAY_GP
			                      ,YD_AIM_BAY_GP
			                  FROM TB_YM_WRKBOOK
			                 WHERE YD_WRK_PLAN_TCAR = :V_YD_EQP_ID
			                   AND YD_WBOOK_ID NOT IN
			                      (SELECT NVL(YD_CARLD_WRK_BOOK_ID,YD_CARUD_WRK_BOOK_ID) AS YD_WBOOK_ID
			                         FROM TB_YM_TCARSCH
			                        WHERE DEL_YN = 'N'
			                          AND (YD_CARLD_WRK_BOOK_ID IS NOT NULL	OR YD_CARUD_WRK_BOOK_ID IS NOT NULL))
			--                   AND YD_SCH_CD LIKE '__TC__U%'
			                   AND ((SUBSTR(YD_SCH_CD,1,2) <> (NVL(SUBSTR(YD_TO_LOC_GUIDE,1,2),SUBSTR(YD_SCH_CD,1,2))))
			                         OR 
			                        (YD_SCH_CD LIKE SUBSTR(YD_SCH_CD,1,2)|| 'TC__U%')
			                       )
			                   AND DEL_YN = 'N'
			                 ORDER BY YD_SCH_PRIOR, YD_WBOOK_ID)
			         WHERE ROWNUM = 1) XB
			 WHERE EQ.EQUIP_GP             = TS.YD_EQP_ID(+)
			   AND 'N'                     = TS.DEL_YN(+)
			   AND TS.YD_CARLD_WRK_BOOK_ID = WB.YD_WBOOK_ID(+)
			   AND 'N'                     = WB.DEL_YN(+)
			   AND EQ.EQUIP_GP             = :V_YD_EQP_ID
			*/   
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchLevWo", logId, methodNm, "대차스케쥴정보(공대차출발지시) 조회");

			if (jsChk != null && jsChk.size() > 0) {
				JDTORecord jrChk = jsChk.getRecord(0);

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

				//현재동이 없으면 Home동을 현재동으로
				if ("".equals(ydCurrBayGp)) {
					ydCurrBayGp = ydHomeBayGp;
				}
				if ("B".equals(commUtils.trim(jrChk.getFieldString("YD_EQP_STAT")))) {
					throw new Exception("대차[" + ydEqpId + "]는 고장 상태입니다.");
				} else if (!"1".equals(commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE")))) {
					throw new Exception("대차[" + ydEqpId + "]는 Off-Line 상태입니다.");
				} else if ("Y".equals(commUtils.trim(jrChk.getFieldString("TC_MTL_YN")))) { 
					
					//화면에서 영대차 체크한 경우 
					if ("Y".equals(sOPRN)) {

						/*
						SELECT YD_WBOOK_ID
						     , YD_BAY_GP
						  FROM TB_YM_WRKBOOK
						 WHERE DEL_YN = 'N'
						   AND YD_WBOOK_ID IN(  SELECT YD_WBOOK_ID
						                          FROM TB_YM_WRKBOOKMTL
						                         WHERE DEL_YN   = 'N'
						                           AND STOCK_ID IN( SELECT STOCK_ID
						                                              FROM TB_YM_TCARFTMVMTL
						                                             WHERE DEL_YN = 'N'
						                                               AND YD_TCAR_SCH_ID IN(SELECT YD_TCAR_SCH_ID
						                                                                       FROM TB_YM_TCARSCH
						                                                                      WHERE DEL_YN = 'N' 
						                                                                        AND YD_EQP_ID = :V_YD_EQP_ID
						                                                                     )
						                                          )
						                      )
						 */
						JDTORecordSet rst = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarWbook");
			
						String sYD_WBOOK_ID  = "";
						if (rst.size() > 0) {
							for (int i = 0; i < rst.size(); ++i) {
								sYD_WBOOK_ID = rst.getRecord(i).getFieldString("YD_WBOOK_ID");
								
								jrParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
								/*
								UPDATE TB_YM_WRKBOOK
								   SET MODIFIER    = :V_MODIFIER
								      ,MOD_DDTT    = SYSDATE
								      ,DEL_YN      = 'Y'
								 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
								   AND DEL_YN      = 'N'
								 */
								commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBook");
								/*
								--작업예약재료 삭제 
								UPDATE TB_YM_WRKBOOKMTL
								   SET MODIFIER    = :V_MODIFIER
								      ,MOD_DDTT    = SYSDATE
								      ,DEL_YN      = 'Y'
								 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
								   AND DEL_YN      = 'N'
								 */
								commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBookMtl");
							}
						}
						
						//이송재료가 존재하는 경우 영대차 출발지시
						//대차스케줄 update
						/*
						UPDATE TB_YM_TCARSCH
						   SET YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC --하차위치
						     , YD_CARLD_STOP_LOC = :V_YD_CARLD_STOP_LOC --상차위치
						     , YD_CAR_PROG_STAT  = 'A'
						     , MODIFIER          = :V_MODIFIER
						     , MOD_DDTT          = SYSDATE
						 WHERE YD_TCAR_SCH_ID =(SELECT YD_TCAR_SCH_ID
						                          FROM TB_YM_EQUIP    EQ
						                             , TB_YM_TCARSCH  TS
						                         WHERE EQ.EQUIP_GP  = TS.YD_EQP_ID(+)
						                           AND EQ.EQUIP_GP  = :V_YD_EQP_ID      
						                           AND 'N'          = TS.DEL_YN(+)
						                       )
						 */
						jrParam.setField("YD_TCAR_SCH_ID"    , jrChk.getFieldString("YD_TCAR_SCH_ID"));
						jrParam.setField("HOME_BAY"          , "N");	
						jrParam.setField("YD_CARUD_STOP_LOC" , ydEqpId.substring(0, 1) + ydBayGpTo   + ydEqpId.substring(2, 6) );//하차위치
						jrParam.setField("YD_CARLD_STOP_LOC" , ydEqpId.substring(0, 1) + ydCurrBayGp + ydEqpId.substring(2, 6) );//상차위치
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchLoc");
						
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L006", jrParam));
						
						return jrRtn;	
					}
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
			String ydHomeBayGpYn    = "N";  //home동 여부
			
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

			}

			//상차출발위치, 상차도착위치, 하차도착위치 Set
			ydCarldLevLoc = ydEqpId.substring(0, 1) + ydCurrBayGp + ydEqpId.substring(2, 6);
			if ("".equals(ydCarldWrkBookId)) {
				//대차 상차작업예약이 없으면
				if ("".equals(ydBayGpTo)) {
					ydCarldStopLoc = ydEqpId.substring(0, 1) + ydHomeBayGp + ydEqpId.substring(2, 6);
					ydHomeBayGpYn = "Y";
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
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchInsSch 
			-- 대차스케줄 등록
			MERGE INTO TB_YM_TCARSCH TS USING (
			SELECT :V_YD_TCAR_SCH_ID       AS YD_TCAR_SCH_ID
			      ,:V_MODIFIER             AS MODIFIER
			      ,SYSDATE                 AS MOD_DDTT
			      ,'N'                     AS DEL_YN
			      ,:V_YD_EQP_ID            AS YD_EQP_ID
			      ,'U'                     AS YD_EQP_WRK_STAT     --공차
			      ,:V_YD_CAR_PROG_STAT     AS YD_CAR_PROG_STAT
			      ,:V_YD_CARLD_WRK_BOOK_ID AS YD_CARLD_WRK_BOOK_ID
			      ,:V_YD_CARLD_LEV_LOC     AS YD_CARLD_LEV_LOC
			      ,:V_YD_CARLD_STOP_LOC    AS YD_CARLD_STOP_LOC
			      ,:V_YD_CARUD_STOP_LOC    AS YD_CARUD_STOP_LOC
			      ,'6'                     AS YD_CARLD_SCH_REQ_GP --공대차도착
			      ,'3'                     AS YD_CARUD_SCH_REQ_GP --영대차도착
			  FROM DUAL
			) DD ON (TS.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID)
			WHEN MATCHED THEN UPDATE SET
				 TS.MODIFIER             = DD.MODIFIER
			    ,TS.MOD_DDTT             = DD.MOD_DDTT
			    ,TS.YD_EQP_WRK_STAT      = DD.YD_EQP_WRK_STAT
			    ,TS.YD_CAR_PROG_STAT     = DD.YD_CAR_PROG_STAT
			    ,TS.YD_CARLD_WRK_BOOK_ID = DD.YD_CARLD_WRK_BOOK_ID
			    ,TS.YD_CARLD_LEV_LOC     = DD.YD_CARLD_LEV_LOC
			    ,TS.YD_CARLD_STOP_LOC    = DD.YD_CARLD_STOP_LOC
			    ,TS.YD_CARUD_STOP_LOC    = DD.YD_CARUD_STOP_LOC
			    ,TS.YD_CARLD_SCH_REQ_GP  = DD.YD_CARLD_SCH_REQ_GP
			    ,TS.YD_CARUD_SCH_REQ_GP  = DD.YD_CARUD_SCH_REQ_GP
			WHEN NOT MATCHED THEN
			INSERT (TS.YD_TCAR_SCH_ID   , TS.REGISTER            , TS.REG_DDTT           , TS.MODIFIER         ,
			        TS.MOD_DDTT         , TS.DEL_YN              , TS.YD_EQP_ID          , TS.YD_EQP_WRK_STAT  ,
			        TS.YD_CAR_PROG_STAT , TS.YD_CARLD_WRK_BOOK_ID, TS.YD_CARLD_LEV_LOC   , TS.YD_CARLD_STOP_LOC,
			        TS.YD_CARUD_STOP_LOC, TS.YD_CARLD_SCH_REQ_GP , TS.YD_CARUD_SCH_REQ_GP)
			VALUES (DD.YD_TCAR_SCH_ID   , DD.MODIFIER            , DD.MOD_DDTT           , DD.MODIFIER         ,
			        DD.MOD_DDTT         , DD.DEL_YN              , DD.YD_EQP_ID          , DD.YD_EQP_WRK_STAT  ,
			        DD.YD_CAR_PROG_STAT , DD.YD_CARLD_WRK_BOOK_ID, DD.YD_CARLD_LEV_LOC   , DD.YD_CARLD_STOP_LOC,
			        DD.YD_CARUD_STOP_LOC, DD.YD_CARLD_SCH_REQ_GP , DD.YD_CARUD_SCH_REQ_GP)
			*/        
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchInsSch", logId, methodNm, "대차스케줄 수정 또는 생성");
			
			if ("0".equals(ydCarProgStat)) {
				/**********************************************************
				* 4. 상차출발위치와 상차도착위치가 다르면 공대차출발지시 전송
				**********************************************************/
				if("2".equals(ydEqpId.substring(0, 1))) {   // SLAB
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L006", jrParam));
				} else if("3".equals(ydEqpId.substring(0, 1))) {             // COIL
				}
				if(ydHomeBayGpYn.equals("Y")) {
					jrParam.setField("HOME_BAY"      , "Y"     );	
					jrParam.setField("YD_CARLD_STOP_LOC" , ydEqpId.substring(0, 1) + ydCurrBayGp + ydEqpId.substring(2, 6) );	
					jrParam.setField("YD_CARUD_STOP_LOC" , ydEqpId.substring(0, 1) + ydHomeBayGp + ydEqpId.substring(2, 6)  );	
					
				}
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L006", jrParam));
				
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

				jrRtn = commUtils.addSndData(jrRtn, this.getCrnSchMsg(jrYdMsg));
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
	 *      [A] 오퍼레이션명 : 대차스케줄 하차완료 처리(Slab)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTcarSchUdCmpl_Slab(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차스케줄 하차완료 처리(Slab)[YmComm.trtTcarSchUdCmpl_Slab] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			//수신 항목 값
			String ydEqpId     		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); 		//야드설비ID(대차)
			String ydTcarSchId 		= commUtils.trim(rcvMsg.getFieldString("YD_TCAR_SCH_ID")); 		//야드대차스케쥴ID
			String CraneId	   		= commUtils.trim(rcvMsg.getFieldString("CRANE_ID"));       		//크레인 id
			String ydCarUdStopLoc	= commUtils.trim(rcvMsg.getFieldString("YD_CARUD_STOP_LOC"));	//하차완료위치
			
			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			}

			//전문 Return
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

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
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchUdCmpl 
				-- 대차스케줄 대차하차완료 조회  
				SELECT TS.YD_TCAR_SCH_ID
				  FROM TB_YM_TCARSCH TS
				 WHERE TS.YD_EQP_ID = :V_YD_EQP_ID
				   AND TS.DEL_YN    = 'N'
				*/	   
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchUdCmpl", logId, methodNm, "대차하차스케쥴 조회");
				
				if (jsChk != null && jsChk.size() > 0) {
					JDTORecord jrChk = jsChk.getRecord(0);
					ydTcarSchId = commUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID")); //야드대차스케쥴ID
				} else {
					return jrRtn;
			    }
			}
			
			jrParam.setField("YD_TCAR_SCH_ID", ydTcarSchId);	//야드대차스케쥴ID

			/**********************************************************
			* 2. 대차스케줄 삭제
			**********************************************************/
			//대차이송재료 삭제
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchDelMtl 
			-- 대차스케줄재료 삭제 
			UPDATE TB_YM_TCARFTMVMTL
			   SET MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,DEL_YN   = 'Y'
			 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
			   AND DEL_YN   = 'N'

			*/	   
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchDelMtl", logId, methodNm, "대차이송재료 삭제");

	
			jrParam.setField("CRANE_ID"			, CraneId);			//크레인 id
			jrParam.setField("YD_CARUD_STOP_LOC", ydCarUdStopLoc);	//하차완료위치

			//대차스케줄 삭제
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchDelSch
			UPDATE TB_YM_TCARSCH 
			   SET MODIFIER          = :V_MODIFIER
			     , MOD_DDTT          = SYSDATE
			     , DEL_YN            = 'Y'
			     , YD_EQP_WRK_STAT   = 'U'                         --공차
			     , YD_CAR_PROG_STAT  = 'E'                         --하차완료
			     , YD_CARUD_ST_DT    = NVL(YD_CARUD_ST_DT,SYSDATE) --하차개시시간
			     , YD_CARUD_CMPL_DT  = SYSDATE                     --하차완료시간
			     , YD_CARUD_WRK_CRN  = :V_CRANE_ID                 --작업크레인
			     , YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC
			 WHERE YD_TCAR_SCH_ID    = :V_YD_TCAR_SCH_ID
			 */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchDelSch", logId, methodNm, "대차스케줄 삭제");
			
			/**********************************************************
			* 4. 공대차출발지시 처리
			**********************************************************/
			jrRtn = commUtils.addSndData(jrRtn, this.trtTcarSchLevWo_Slab(rcvMsg));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 *      [A] 오퍼레이션명 : 대차스케줄 공대차출발지시 처리(Slab)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTcarSchLevWo_Slab(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차스케줄 공대차출발지시 처리(Slab)[YmComm.trtTcarSchLevWo_Slab] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String sOPRN     = commUtils.nvl(commUtils.trim(rcvMsg.getFieldString("OPRN" )), "N");   //영대차여부
			String ydEqpId   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID" )); //야드설비ID
			String ydBayGpTo = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP" )); //야드동구분(상차동)
			
			String ydColGpTo = commUtils.trim(rcvMsg.getFieldString("YD_COL_GP" )); //야드동열구분(상차동) 
			
			String ydCarUdStopLoc = commUtils.trim(rcvMsg.getFieldString("YD_CARUD_STOP_LOC" )); //하차완료위치  (마지막 하차 권상 인지)
			String sOprYn    = commUtils.nvl(rcvMsg.getFieldString("OPR_YN" ),"N"); 
			
			if (ydCarUdStopLoc.length()!= 6) {
				ydCarUdStopLoc = "" ;
			}
			
			if ("".equals(ydColGpTo)) { 
				ydColGpTo = ydBayGpTo ;
			}

			
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자

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
			String ydHomeColGp      = ""; //야드Home동열구분
			String autoTcarSchYn    = ""; //자동대차스케줄여부
			String ydTcarSchId      = ""; //야드대차스케쥴ID
			String ydWbookIdCurr    = ""; //야드작업예약ID(현재 대차스케줄 상차작업예약ID)
			String ydBayGpCurr      = ""; //야드동구분(현재 대차스케줄 상차동)
			String ydAimBayGpCurr   = ""; //야드목표동구분(현재 대차스케줄 하차동)
			String ydWbookIdNext    = ""; //야드작업예약ID(다음 상차작업예약ID)
			String ydBayGpNext      = ""; //야드동구분(다음 작업예약 상차동)
			String ydAimBayGpNext   = ""; //야드목표동구분(다음 작업예약 하차동)
		 	
			String ydCarldLevLoc    = ""; //야드상차출발위치
			String ydCarloadStopLoc    = ""; //장비상차출발위치
			String ydCarunloadStopLoc   = ""; //장비상차출발위치
			String PreWrkWo            = ""; //선작업지시 1:상차지 2:대기 3:하차지 
			String YdTcarWrkAbleBay6   = ""; //선작업지시 상차지 이지만 하차지에 실행할 경우 TAG		
			
			String sEQUIP_CARLD_SCH_CD = ""; //동간작업기준 - 상차스케줄코드
			String sEQUIP_CARUD_SCH_CD = ""; //동간작업기준 - 하차스케줄코드
			String sEQUIP_CARUNLOAD_STOP_LOC = ""; //동간작업기준 - 하차동
			
			//대차스케쥴정보(공대차출발지시) 조회
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchLevWoSlab  
			-- 대차스케줄 공대차출발지시 조회 - 
			SELECT TS.YD_TCAR_SCH_ID
			      ,EQ.WPROG_STAT                AS YD_EQP_STAT
			      ,EQ.WORK_MODE                 AS YD_EQP_WRK_MODE
			      ,NVL(SUBSTR(CURR_STOP_LOC,2,1),WB.YD_BAY_GP) 
			                                    AS YD_CURR_BAY_GP --이동중이면 상차동을 현재동으로
			      ,SUBSTR(EQ.WAIT_STOP_LOC,2,1) AS YD_HOME_BAY_GP
			      ,WB.YD_WBOOK_ID               AS YD_WBOOK_ID_CURR   --현재 대차스케줄 상차작업예약ID
			      ,WB.YD_BAY_GP                 AS YD_BAY_GP_CURR     --현재 대차스케줄 상차동
			      ,WB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_CURR --현재 대차스케줄 하차동
			      ,XB.YD_WBOOK_ID               AS YD_WBOOK_ID_NEXT   --다음 상차작업예약ID
			      ,XB.YD_BAY_GP                 AS YD_BAY_GP_NEXT     --다음 상차동
			      ,XB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_NEXT --다음 하차동
			      ,(SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END
			          FROM TB_YM_TCARFTMVMTL TM
			         WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
			           AND TM.DEL_YN = 'N')     AS TC_MTL_YN
			--      ,NVL(EQ.AUTO_TCAR_SCH_YN,'N') AS AUTO_TCAR_SCH_YN   --자동대차스케줄여부
			        ,CURR_STOP_LOC
			        ,EQ.WAIT_STOP_LOC
			        --,EQ.CARLOAD_STOP_LOC
			        --,EQ.CARUNLOAD_STOP_LOC
			        ,EQ.PALLET_NO
			        ,EQ.YD_TCAR_WRK_ABLE_BAY6
			        ,NVL(( SELECT ITEM
			             FROM USRYMA.TB_YM_RULE RL 
			            WHERE RL.REPR_CD_GP = 'TCAR01'
			              AND RL.CD_GP    = '2'
			              AND RL.DEL_YN   = 'N'
			              AND RL.DTL_ITM1 = :V_YD_EQP_ID
			              AND RL.DTL_ITM2 = WB.YD_BAY_GP ),EQ.CARLOAD_STOP_LOC) AS CARLOAD_STOP_LOC --작업예약 기준 상차지 정지 위치 
			        ,NVL(( SELECT ITEM
			             FROM USRYMA.TB_YM_RULE RL 
			            WHERE RL.REPR_CD_GP = 'TCAR01'
			                  AND RL.CD_GP    = '2'
			                  AND RL.DEL_YN   = 'N'
			                  AND RL.DTL_ITM1 = :V_YD_EQP_ID
			                  AND RL.DTL_ITM2 = WB.YD_AIM_BAY_GP ),CARUNLOAD_STOP_LOC) AS CARUNLOAD_STOP_LOC --작업예약 기준 상차지 정지 위치             
			  FROM TB_YM_EQUIP   EQ
			      ,TB_YM_TCARSCH TS
			      ,TB_YM_WRKBOOK WB
			      ,(SELECT MIN(YD_WBOOK_ID  ) AS YD_WBOOK_ID
			              ,MIN(YD_BAY_GP    ) AS YD_BAY_GP
			              ,MIN(YD_AIM_BAY_GP) AS YD_AIM_BAY_GP
			          FROM (SELECT YD_WBOOK_ID
			                      ,YD_BAY_GP
			                      ,YD_AIM_BAY_GP
			                  FROM TB_YM_WRKBOOK
			                 WHERE YD_WRK_PLAN_TCAR = :V_YD_EQP_ID
			                   AND YD_WBOOK_ID NOT IN
			                      (SELECT NVL(YD_CARLD_WRK_BOOK_ID,YD_CARUD_WRK_BOOK_ID) AS YD_WBOOK_ID
			                         FROM TB_YM_TCARSCH
			                        WHERE DEL_YN = 'N'
			                          AND (YD_CARLD_WRK_BOOK_ID IS NOT NULL	OR YD_CARUD_WRK_BOOK_ID IS NOT NULL))
			--                   AND YD_SCH_CD LIKE '__TC__U%'
			                   AND ((SUBSTR(YD_SCH_CD,1,2) <> (NVL(SUBSTR(YD_TO_LOC_GUIDE,1,2),SUBSTR(YD_SCH_CD,1,2))))
			                         OR 
			                        (YD_SCH_CD LIKE SUBSTR(YD_SCH_CD,1,2)|| 'TC__U%')
			                       )
			                   AND DEL_YN = 'N'
			                 ORDER BY  YD_WBOOK_ID)
			         WHERE ROWNUM = 1) XB
			 WHERE EQ.EQUIP_GP             = TS.YD_EQP_ID(+)
			   AND 'N'                     = TS.DEL_YN(+)
			   AND TS.YD_CARLD_WRK_BOOK_ID = WB.YD_WBOOK_ID(+)
			   AND 'N'                     = WB.DEL_YN(+)
			   AND EQ.EQUIP_GP             = :V_YD_EQP_ID
			*/   
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchLevWoSlab", logId, methodNm, "대차스케쥴정보(공대차출발지시) 조회");

			if (jsChk != null && jsChk.size() > 0) {
				JDTORecord jrChk = jsChk.getRecord(0);

				ydCurrBayGp    = commUtils.trim(jrChk.getFieldString("YD_CURR_BAY_GP"    ));
				ydHomeBayGp    = commUtils.trim(jrChk.getFieldString("YD_HOME_BAY_GP"    ));
				ydHomeColGp    = commUtils.trim(jrChk.getFieldString("WAIT_STOP_LOC"    ));
				autoTcarSchYn  = commUtils.trim(jrChk.getFieldString("AUTO_TCAR_SCH_YN"  ));
				ydTcarSchId    = commUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID"    ));
				ydWbookIdCurr  = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID_CURR"  ));
				ydBayGpCurr    = commUtils.trim(jrChk.getFieldString("YD_BAY_GP_CURR"    ));
				ydAimBayGpCurr = commUtils.trim(jrChk.getFieldString("YD_AIM_BAY_GP_CURR"));
				ydWbookIdNext  = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID_NEXT"  ));
				ydBayGpNext    = commUtils.trim(jrChk.getFieldString("YD_BAY_GP_NEXT"    ));
				ydAimBayGpNext = commUtils.trim(jrChk.getFieldString("YD_AIM_BAY_GP_NEXT"));
				
				 
				PreWrkWo       = commUtils.trim(jrChk.getFieldString("PALLET_NO"));     //선작업지시 1:실행 2:대기

				ydCarldLevLoc  = commUtils.trim(jrChk.getFieldString("CURR_STOP_LOC")) ;
				
				ydCarloadStopLoc     = commUtils.trim(jrChk.getFieldString("CARLOAD_STOP_LOC")) ;   //장비상차정지위치
				ydCarunloadStopLoc   = commUtils.trim(jrChk.getFieldString("CARUNLOAD_STOP_LOC")) ; //장비하차정지위치
				YdTcarWrkAbleBay6    = commUtils.trim(jrChk.getFieldString("YD_TCAR_WRK_ABLE_BAY6")) ; //선작업지시 상차지 이지만 하차지에 실행할 경우 TAG
				
				
				sEQUIP_CARLD_SCH_CD = commUtils.trim(jrChk.getFieldString("CARLD_SCH_CD"    )); //동간작업기준 - 상차스케줄코드
				sEQUIP_CARUD_SCH_CD = commUtils.trim(jrChk.getFieldString("CARUD_SCH_CD"    )); //동간작업기준 - 하차스케줄코드
				sEQUIP_CARUNLOAD_STOP_LOC = commUtils.trim(jrChk.getFieldString("EQ_CARUNLOAD_STOP_LOC"    )); //동간작업기준 - 하차동
				
				
				//이적 화면에서 상차지 동구분만  넘어온다면 상차정지위치를 세팅
				if(ydColGpTo.length()==1) ydColGpTo = ydCarloadStopLoc;
				if(ydBayGpTo.length()==1) ydBayGpTo	= ydCarloadStopLoc; 
								
				//현재동열이 없으면 Home동열을 현재동으로
				if ("".equals(ydCarldLevLoc)) {
					ydCarldLevLoc = ydHomeColGp;
				}
				if ("B".equals(commUtils.trim(jrChk.getFieldString("YD_EQP_STAT")))) {
					throw new Exception("대차[" + ydEqpId + "]는 고장 상태입니다.");
				} else if (!"1".equals(commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE")))) {
					throw new Exception("대차[" + ydEqpId + "]는 Off-Line 상태입니다.");
				} else if ("Y".equals(commUtils.trim(jrChk.getFieldString("TC_MTL_YN")))) { 
					
					//화면에서 영대차 체크한 경우 
					if ("Y".equals(sOPRN)) {

						/*
						SELECT YD_WBOOK_ID
						     , YD_BAY_GP
						  FROM TB_YM_WRKBOOK
						 WHERE DEL_YN = 'N'
						   AND YD_WBOOK_ID IN(  SELECT YD_WBOOK_ID
						                          FROM TB_YM_WRKBOOKMTL
						                         WHERE DEL_YN   = 'N'
						                           AND STOCK_ID IN( SELECT STOCK_ID
						                                              FROM TB_YM_TCARFTMVMTL
						                                             WHERE DEL_YN = 'N'
						                                               AND YD_TCAR_SCH_ID IN(SELECT YD_TCAR_SCH_ID
						                                                                       FROM TB_YM_TCARSCH
						                                                                      WHERE DEL_YN = 'N' 
						                                                                        AND YD_EQP_ID = :V_YD_EQP_ID
						                                                                     )
						                                          )
						                      )
						 */
						JDTORecordSet rst = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarWbook");
			
						String sYD_WBOOK_ID  = "";
						if (rst.size() > 0) {
							for (int i = 0; i < rst.size(); ++i) {
								sYD_WBOOK_ID = rst.getRecord(i).getFieldString("YD_WBOOK_ID");
								
								jrParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
								/*
								UPDATE TB_YM_WRKBOOK
								   SET MODIFIER    = :V_MODIFIER
								      ,MOD_DDTT    = SYSDATE
								      ,DEL_YN      = 'Y'
								 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
								   AND DEL_YN      = 'N'
								 */
								commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBook");
								/*
								--작업예약재료 삭제 
								UPDATE TB_YM_WRKBOOKMTL
								   SET MODIFIER    = :V_MODIFIER
								      ,MOD_DDTT    = SYSDATE
								      ,DEL_YN      = 'Y'
								 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
								   AND DEL_YN      = 'N'
								 */
								commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBookMtl");
							}
						}
						
						//이송재료가 존재하는 경우 영대차 출발지시
						//대차스케줄 update
						/*
						UPDATE TB_YM_TCARSCH
						   SET YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC --하차위치
						     , YD_CARLD_STOP_LOC = :V_YD_CARLD_STOP_LOC --상차위치
						     , YD_CAR_PROG_STAT  = 'A'
						     , MODIFIER          = :V_MODIFIER
						     , MOD_DDTT          = SYSDATE
						 WHERE YD_TCAR_SCH_ID =(SELECT YD_TCAR_SCH_ID
						                          FROM TB_YM_EQUIP    EQ
						                             , TB_YM_TCARSCH  TS
						                         WHERE EQ.EQUIP_GP  = TS.YD_EQP_ID(+)
						                           AND EQ.EQUIP_GP  = :V_YD_EQP_ID      
						                           AND 'N'          = TS.DEL_YN(+)
						                       )
						 */
					
						jrParam.setField("YD_TCAR_SCH_ID"    , jrChk.getFieldString("YD_TCAR_SCH_ID"));
						jrParam.setField("HOME_BAY"          , "N");	
						jrParam.setField("YD_CARUD_STOP_LOC" , ydColGpTo );//하차위치
						jrParam.setField("YD_CARLD_STOP_LOC" , ydCarldLevLoc );//상차위치
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchLoc");
						
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L006", jrParam));
						
						return jrRtn;	
					}
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
			//String ydCarldLevLoc    = ""; //야드상차출발위치
			String ydCarldStopLoc   = ""; //야드상차정지위치
			String ydCarudStopLoc   = ""; //야드하차정지위치
			String ydCarProgStat    = "0"; //야드차량진행상태(상차대기)
			String ydBayGp          = ""; //야드동구분(상차동)
			String ydAimBayGp       = ""; //야드동구분(하차동)
			String ydHomeBayGpYn    = "N";  //home동 여부
			
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

			}

			//상차출발위치, 상차도착위치, 하차도착위치 Set
			//ydCarldLevLoc = ydEqpId.substring(0, 1) + ydCurrBayGp + ydEqpId.substring(2, 6);
			if ("".equals(ydCarldWrkBookId)) {
				//대차 상차작업예약이 없으면
				if ("".equals(ydBayGpTo)) {
					ydCarldStopLoc = ydHomeColGp;
					ydHomeBayGpYn = "Y";
				} else {
					ydCarldStopLoc = ydColGpTo;
				}
			} else {
				//대차 상차작업예약이 있으면
				ydCarldStopLoc = ydCarloadStopLoc;
				ydCarudStopLoc = ydCarunloadStopLoc;
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
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchInsSch 
			-- 대차스케줄 등록
			MERGE INTO TB_YM_TCARSCH TS USING (
			SELECT :V_YD_TCAR_SCH_ID       AS YD_TCAR_SCH_ID
			      ,:V_MODIFIER             AS MODIFIER
			      ,SYSDATE                 AS MOD_DDTT
			      ,'N'                     AS DEL_YN
			      ,:V_YD_EQP_ID            AS YD_EQP_ID
			      ,'U'                     AS YD_EQP_WRK_STAT     --공차
			      ,:V_YD_CAR_PROG_STAT     AS YD_CAR_PROG_STAT
			      ,:V_YD_CARLD_WRK_BOOK_ID AS YD_CARLD_WRK_BOOK_ID
			      ,:V_YD_CARLD_LEV_LOC     AS YD_CARLD_LEV_LOC
			      ,:V_YD_CARLD_STOP_LOC    AS YD_CARLD_STOP_LOC
			      ,:V_YD_CARUD_STOP_LOC    AS YD_CARUD_STOP_LOC
			      ,'6'                     AS YD_CARLD_SCH_REQ_GP --공대차도착
			      ,'3'                     AS YD_CARUD_SCH_REQ_GP --영대차도착
			  FROM DUAL
			) DD ON (TS.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID)
			WHEN MATCHED THEN UPDATE SET
				 TS.MODIFIER             = DD.MODIFIER
			    ,TS.MOD_DDTT             = DD.MOD_DDTT
			    ,TS.YD_EQP_WRK_STAT      = DD.YD_EQP_WRK_STAT
			    ,TS.YD_CAR_PROG_STAT     = DD.YD_CAR_PROG_STAT
			    ,TS.YD_CARLD_WRK_BOOK_ID = DD.YD_CARLD_WRK_BOOK_ID
			    ,TS.YD_CARLD_LEV_LOC     = DD.YD_CARLD_LEV_LOC
			    ,TS.YD_CARLD_STOP_LOC    = DD.YD_CARLD_STOP_LOC
			    ,TS.YD_CARUD_STOP_LOC    = DD.YD_CARUD_STOP_LOC
			    ,TS.YD_CARLD_SCH_REQ_GP  = DD.YD_CARLD_SCH_REQ_GP
			    ,TS.YD_CARUD_SCH_REQ_GP  = DD.YD_CARUD_SCH_REQ_GP
			WHEN NOT MATCHED THEN
			INSERT (TS.YD_TCAR_SCH_ID   , TS.REGISTER            , TS.REG_DDTT           , TS.MODIFIER         ,
			        TS.MOD_DDTT         , TS.DEL_YN              , TS.YD_EQP_ID          , TS.YD_EQP_WRK_STAT  ,
			        TS.YD_CAR_PROG_STAT , TS.YD_CARLD_WRK_BOOK_ID, TS.YD_CARLD_LEV_LOC   , TS.YD_CARLD_STOP_LOC,
			        TS.YD_CARUD_STOP_LOC, TS.YD_CARLD_SCH_REQ_GP , TS.YD_CARUD_SCH_REQ_GP)
			VALUES (DD.YD_TCAR_SCH_ID   , DD.MODIFIER            , DD.MOD_DDTT           , DD.MODIFIER         ,
			        DD.MOD_DDTT         , DD.DEL_YN              , DD.YD_EQP_ID          , DD.YD_EQP_WRK_STAT  ,
			        DD.YD_CAR_PROG_STAT , DD.YD_CARLD_WRK_BOOK_ID, DD.YD_CARLD_LEV_LOC   , DD.YD_CARLD_STOP_LOC,
			        DD.YD_CARUD_STOP_LOC, DD.YD_CARLD_SCH_REQ_GP , DD.YD_CARUD_SCH_REQ_GP)
			*/        
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchInsSch", logId, methodNm, "대차스케줄 수정 또는 생성");
			
			if ("".equals(ydCarUdStopLoc)){  //마지막 하차 권상 태크		
				// 상차출발위치와 상차도착위치가 다르면 공대차출발지시 전송
						if ("0".equals(ydCarProgStat)) {
				

							if(ydHomeBayGpYn.equals("Y")) {
								
								jrParam.setField("HOME_BAY"      , "Y"     );	
								jrParam.setField("YD_CARLD_STOP_LOC" , ydCarldLevLoc );	
								jrParam.setField("YD_CARUD_STOP_LOC" , ydHomeColGp );	
								
								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L006", jrParam));
								
							}
							
							//상차출발위치(현위치)와 상차도착위치가 틀리면  상차지 대차출발지시???확인요
							if (!ydCarldLevLoc.equals(ydCarldStopLoc)) {
								jrParam.setField("HOME_BAY"      , "N"     );	
								jrParam.setField("YD_CARLD_STOP_LOC" , ydCarldLevLoc );	
								jrParam.setField("YD_CARUD_STOP_LOC" , ydCarudStopLoc );	
								
								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L006", jrParam));
							}
							
							
						} 						
							if(sOprYn.equals("N")) {
								if (!"".equals(ydCarldWrkBookId) && "".equals(ydWbookIdCurr)) {   //작업대상이 있는지
	
									//크레인스케줄 전문 - Log ID, Method, 수정자 Set
									JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
									
									jrYdMsg.setResultCode(logId);	//Log ID
									jrYdMsg.setResultMsg(methodNm);	//Log Method Name
									jrYdMsg.setField("YD_WBOOK_ID"  , ydCarldWrkBookId); //야드작업예약ID
									jrYdMsg.setField("YD_SCH_ST_GP" , "A"             ); //야드스케쥴기동구분(Auto)
									jrYdMsg.setField("YD_SCH_REQ_GP", "6"             ); //야드스케쥴요청구분(공대차도착)
									
									jrRtn = commUtils.addSndData(jrRtn, this.getCrnSchMsg(jrYdMsg));
								}
							}	
						//}
						      
			}else{

				
				if (!"".equals(ydCarldWrkBookId) && "".equals(ydWbookIdCurr)) {   //작업대상이 있는지
	 				if ( "3".equals(PreWrkWo)|| "X".equals(YdTcarWrkAbleBay6)){  
	 					    
	 					    //하차지선작업 구분이 실행시  스케줄을 호출한다.
							//크레인스케줄 전문 - Log ID, Method, 수정자 Set
							JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
			
							jrYdMsg.setResultCode(logId);	//Log ID
							jrYdMsg.setResultMsg(methodNm);	//Log Method Name
							jrYdMsg.setField("YD_WBOOK_ID"  , ydCarldWrkBookId); //야드작업예약ID
							jrYdMsg.setField("YD_SCH_ST_GP" , "A"             ); //야드스케쥴기동구분(Auto)
							jrYdMsg.setField("YD_SCH_REQ_GP", "6"             ); //야드스케쥴요청구분(공대차도착)
			
							jrRtn = commUtils.addSndData(jrRtn, this.getCrnSchMsg(jrYdMsg));
						} 
					
	 				jrParam.setField("YD_WBOOK_ID"  , ydCarldWrkBookId); //야드작업예약ID	
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchForEqpid1 
 
					SELECT
					     
					   ( SELECT ITEM
					       FROM USRYMA.TB_YM_RULE RL 
					          WHERE RL.REPR_CD_GP = 'TCAR01'
					            AND RL.CD_GP    = '2'
					            AND RL.DEL_YN   = 'N'
					            AND RL.DTL_ITM1 = :V_YD_EQP_ID
					            AND RL.DTL_ITM2 = A.YD_BAY_GP ) BO_CARLD_STOP_LOC --작업예약 기준 상차지 정지 위치 
					  ,( SELECT ITEM
					       FROM USRYMA.TB_YM_RULE RL 
					          WHERE RL.REPR_CD_GP = 'TCAR01'
					            AND RL.CD_GP    = '2'
					            AND RL.DEL_YN   = 'N'
					            AND RL.DTL_ITM1 = :V_YD_EQP_ID
					            AND RL.DTL_ITM2 = A.YD_AIM_BAY_GP ) BO_CARUD_STOP_LOC --작업예약 기준 상차지 정지 위치             
					               
					 FROM  TB_YM_WRKBOOK A 
					 WHERE 1=1
					   AND A.YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND A.DEL_YN    = 'N' 
					*/	   
					JDTORecordSet jsChk7 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchForEqpid1", logId, methodNm, "대차스케줄코드검색");	
					
					JDTORecord jrTcar7 = jsChk7.getRecord(0);
					
					ydCarloadStopLoc   = commUtils.trim(jrTcar7.getFieldString("BO_CARLD_STOP_LOC"));
					ydCarunloadStopLoc = commUtils.trim(jrTcar7.getFieldString("BO_CARUD_STOP_LOC"));
					jrParam.setField("NEXT_STOP_LOC_L"   , ydCarloadStopLoc);     //상차지 위치
					jrParam.setField("NEXT_STOP_LOC_U"   , ydCarunloadStopLoc);   //하차지 위치
					
					if(sEQUIP_CARLD_SCH_CD.equals(sEQUIP_CARUD_SCH_CD)) {
						//동간작업기준 상차스케줄코드와 하차스케줄코드가 같으면 작업예약의 목적동으로 동간작업기준의 하차지를 변경하지 않고
						//기존 값을 그대로 유지 한다.
						jrParam.setField("NEXT_STOP_LOC_U"   , sEQUIP_CARUNLOAD_STOP_LOC);   //하차지 위치
					}

					//대차대기동및 상차지 수정 FOR 라스트권상 후 대차출발지시용
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdEqpCurrBay77 
						UPDATE TB_YM_EQUIP
						   SET MODIFIER           = :V_MODIFIER
						      ,MOD_DDTT           = SYSDATE
						      ,CARLOAD_STOP_LOC   = :V_NEXT_STOP_LOC_L
						      ,CARUNLOAD_STOP_LOC = :V_NEXT_STOP_LOC_U
						      ,WAIT_STOP_LOC      = :V_NEXT_STOP_LOC_L
						WHERE EQUIP_GP            = :V_YD_EQP_ID 
					*/ 
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdEqpCurrBay77", logId, methodNm, "대차대기동및 상사지 수정 FOR 라스트권상 후 대차출발지시용");
					
					
					//대차대기동및 상사지 수정 FOR next to 위치 기준 설정
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchCurrBay77 
						UPDATE TB_YM_TCARSCH
						   SET MODIFIER          = :V_MODIFIER
						      ,MOD_DDTT          = SYSDATE
						      ,YD_CARLD_LEV_LOC  = :V_NEXT_STOP_LOC_L
						      ,YD_CARLD_STOP_LOC = :V_NEXT_STOP_LOC_L
						      ,YD_CARUD_STOP_LOC = :V_NEXT_STOP_LOC_U      
						WHERE YD_EQP_ID          = :V_YD_EQP_ID 
						  AND DEL_YN             = 'N'
					*/ 
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchCurrBay77", logId, methodNm, "To위치기준으로 활용하기위한 Next상차지정보업데이트");			 				
	 				
	 				
	 				
	 				//상차지 공대차출발지시전송은 하차지 상차작업이 없는경우만 
				  if("".equals(YdTcarWrkAbleBay6)){
						jrParam.setField("HOME_BAY"      , "N"     );	
						jrParam.setField("YD_CARLD_STOP_LOC" , ydCarloadStopLoc );	
						jrParam.setField("YD_CARUD_STOP_LOC" , ydCarunloadStopLoc );	
						
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L006", jrParam));
				  }else{

						 
			 
							jrParam.setField("YD_TCAR_WRK_ABLE_BAY6", ""   ); //
							// 영대차 하차출발 일경우 하차지 정보와  다음 작업대상의 상차지 정보가 같으면  다음스케줄 구동하지 않고 마지막 하차지권상 후 사용하기 위함 대체필드 사용
							// 마지막 하차지 권하후 크리어 하여야함
							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdEqpCurrBay79 
								UPDATE TB_YM_EQUIP
								   SET MODIFIER                = :V_MODIFIER
								      ,MOD_DDTT                = SYSDATE
								      ,YD_TCAR_WRK_ABLE_BAY6   = :V_YD_TCAR_WRK_ABLE_BAY6
								WHERE EQUIP_GP                 = :V_YD_EQP_ID 
							*/ 
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdEqpCurrBay79", logId, methodNm, "하차지 라스트권상 후 스케줄기동을위함");	
						 
						
				  }
			  }else{
				  //현재동위치와 대기동위치 다르면
				  if (!(ydCarldLevLoc.equals(ydHomeColGp))) { 

					  
						jrParam.setField("HOME_BAY"      , "Y"     );	
						jrParam.setField("YD_CARLD_STOP_LOC" , ydCarldLevLoc );	
						jrParam.setField("YD_CARUD_STOP_LOC" , ydHomeColGp );	
						
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L006", jrParam));
						
					}
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
	 *      [A] 오퍼레이션명 : 공대차출발지시 처리동간이적외(Slab)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTcarStartUnload_Slab(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "공대차출발지시 처리 처리동간이팝업화면외(Slab)[YmComm.trtTcarStartUnload_Slab] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			//수신 항목 값
			String ydWrkPlanTcar     		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); 		//야드설비ID(대차)
			String ydBayGp 		           = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP")); 		//상차동

			
			if ("".equals(ydWrkPlanTcar)) {
				throw new Exception("설비ID가 없습니다.");
			}

			//전문 Return
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID", ydWrkPlanTcar);	//야드설비ID

			/**********************************************************
			* 1. 대차작업이 있으면 공대차출발지시 처리
			**********************************************************/
			if (!"".equals(ydWrkPlanTcar)) {
				
				//공대차출발지시 처리시 Exception을 발생시키지 않기위해 미리 Check
				String msgTcar = ""; //공대차출발지시 처리 메세지
			 			
				//대차스케쥴정보(공대차출발지시) 조회
				jrParam.setField("YD_EQP_ID", ydWrkPlanTcar);	//야드설비ID(대차)
				
				/*
				SELECT TS.YD_TCAR_SCH_ID
				      ,EQ.WPROG_STAT                AS YD_EQP_STAT
				      ,EQ.WORK_MODE                 AS YD_EQP_WRK_MODE
				      ,NVL(SUBSTR(CURR_STOP_LOC,2,1),WB.YD_BAY_GP) 
				                                    AS YD_CURR_BAY_GP --이동중이면 상차동을 현재동으로
				      ,SUBSTR(EQ.WAIT_STOP_LOC,2,1) AS YD_HOME_BAY_GP
				      ,WB.YD_WBOOK_ID               AS YD_WBOOK_ID_CURR   --현재 대차스케줄 상차작업예약ID
				      ,WB.YD_BAY_GP                 AS YD_BAY_GP_CURR     --현재 대차스케줄 상차동
				      ,WB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_CURR --현재 대차스케줄 하차동
				      ,XB.YD_WBOOK_ID               AS YD_WBOOK_ID_NEXT   --다음 상차작업예약ID
				      ,XB.YD_BAY_GP                 AS YD_BAY_GP_NEXT     --다음 상차동
				      ,XB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_NEXT --다음 하차동
				      ,(SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END
				          FROM TB_YM_TCARFTMVMTL TM
				         WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
				           AND TM.DEL_YN = 'N')     AS TC_MTL_YN
				--      ,NVL(EQ.AUTO_TCAR_SCH_YN,'N') AS AUTO_TCAR_SCH_YN   --자동대차스케줄여부
				  FROM TB_YM_EQUIP   EQ
				      ,TB_YM_TCARSCH TS
				      ,TB_YM_WRKBOOK WB
				      ,(SELECT MIN(YD_WBOOK_ID  ) AS YD_WBOOK_ID
				              ,MIN(YD_BAY_GP    ) AS YD_BAY_GP
				              ,MIN(YD_AIM_BAY_GP) AS YD_AIM_BAY_GP
				          FROM (SELECT YD_WBOOK_ID
				                      ,YD_BAY_GP
				                      ,YD_AIM_BAY_GP
				                  FROM TB_YM_WRKBOOK
				                 WHERE YD_WRK_PLAN_TCAR = :V_YD_EQP_ID
				                   AND YD_WBOOK_ID NOT IN
				                      (SELECT NVL(YD_CARLD_WRK_BOOK_ID,YD_CARUD_WRK_BOOK_ID) AS YD_WBOOK_ID
				                         FROM TB_YM_TCARSCH
				                        WHERE DEL_YN = 'N'
				                          AND (YD_CARLD_WRK_BOOK_ID IS NOT NULL	OR YD_CARUD_WRK_BOOK_ID IS NOT NULL))
				--                   AND YD_SCH_CD LIKE '__TC__U%'
				                   AND ((SUBSTR(YD_SCH_CD,1,2) <> (NVL(SUBSTR(YD_TO_LOC_GUIDE,1,2),SUBSTR(YD_SCH_CD,1,2))))
				                         OR 
				                        (YD_SCH_CD LIKE SUBSTR(YD_SCH_CD,1,2)|| 'TC__U%')
				                       )
				                   AND DEL_YN = 'N'
				                 ORDER BY YD_SCH_PRIOR, YD_WBOOK_ID)
				         WHERE ROWNUM = 1) XB
				 WHERE EQ.EQUIP_GP             = TS.YD_EQP_ID(+)
				   AND 'N'                     = TS.DEL_YN(+)
				   AND TS.YD_CARLD_WRK_BOOK_ID = WB.YD_WBOOK_ID(+)
				   AND 'N'                     = WB.DEL_YN(+)
				   AND EQ.EQUIP_GP             = :V_YD_EQP_ID
				 */
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchLevWoSlab", logId, methodNm, "공대차출발지시 조회");
				
				if (jsChk != null && jsChk.size() > 0) {
					JDTORecord jrChk = jsChk.getRecord(0);

					String ydTcarSchId   = commUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID"  ));
					String ydWbookIdCurr = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID_CURR"));

					if ("B".equals(commUtils.trim(jrChk.getFieldString("YD_EQP_STAT")))) {
						msgTcar = "고장";
					} else if (!"1".equals(commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE")))) {
						msgTcar = "Off-Line";
					} else if ("Y".equals(commUtils.trim(jrChk.getFieldString("TC_MTL_YN")))) {
						msgTcar = "대차스케줄[" + ydTcarSchId + "] 이송재료 존재";
					} else if (!"".equals(ydWbookIdCurr)) {
						msgTcar = "대차스케줄[" + ydTcarSchId + "] 상차작업예약[" + ydWbookIdCurr + "] 존재";
					}
				} else {
					msgTcar = "정보 없음";
			    }
				
				//공대차출발지시 처리
				if ("".equals(msgTcar)) {
					jrParam.setField("YD_EQP_ID", ydWrkPlanTcar); //야드설비ID(대차)
					jrParam.setField("YD_BAY_GP", ydBayGp      ); //야드동구분(상차동)
					jrParam.setField("OPR_YN"   , "Y"          ); //화면에서 작업예약 생성
					
					jrRtn = this.trtTcarSchLevWo_Slab(jrParam);
				} else {
					commUtils.printLog(logId, "대차[" + ydWrkPlanTcar + "] 공대차출발지시 불가 : " + msgTcar, "SL");
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
	 *      [A] 오퍼레이션명 : 진도코드 get
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getCoilCurrProgCd(JDTORecord rcvMsg) throws DAOException {	
		String methodNm = "진도코드Check[YmComm.getCoilCurrProgCd] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			// PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", "YmComm => getCoilCurrProgCd", "APPPI0", "*", "*");
			
			if("PIDEV".equals("PIDEV")) {
				jrRtn = this.getCoilCurrProgCd_PIDEV(rcvMsg);
				return jrRtn;
			}
			
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String TcCode 	= commUtils.trim(rcvMsg.getFieldString("TC_CD"));	//TC_CD
			String StlNo 	= commUtils.trim(rcvMsg.getFieldString("STOCK_ID"));//재료
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 2. COILCOMM READ
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("COIL_NO"	, StlNo); //충당재료
			jrParam.setField("MODIFIER" , modifier); //수정자

			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilComByCurrProgCd 
			SELECT DECODE(CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',CURR_PROG_CD) AS CURR_PROG_CD
		     	 , RETURN_GP  --반납구분
		      FROM USRPTA.TB_PT_COILCOMM 
		     WHERE COIL_NO = :V_COIL_NO   -- 재료번호
		 	*/
			JDTORecordSet jsStl = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilComByCurrProgCd", logId, methodNm, "CoilComm 조회");
			
			String ydStocMv = "";
			
			if (jsStl != null && jsStl.size() > 0) {
				String CurrProgCd 	= commUtils.trim(jsStl.getRecord(0).getFieldString("CURR_PROG_CD"));//진도코드
				String ReturnGp 	= commUtils.trim(jsStl.getRecord(0).getFieldString("RETURN_GP")); 	//반납구분
			   	
		    	if(YmConstant.DMYDR008.equals(TcCode)){			//코일제품반납대기
		    		if(YmConstant.RETURN_GP_1.equals(ReturnGp)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_JR;
		    		}else{
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_JG;
		    		}
		    	}else if(YmConstant.DMYDR005.equals(TcCode)||			//코일제품출하지시대기 
		    			 YmConstant.DMYDR004.equals(TcCode)|| 			//외판슬라브출하지시대기
		    			 YmConstant.DMYDR033.equals(TcCode)){			//코일제품반품
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_KG;
		    		
		    	}else if(YmConstant.DMYDR027.equals(TcCode)||			//코일제품보관지시 
		    			 YmConstant.DMYDR030.equals(TcCode)){			//코일제품출하완료
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_MG;
		    		
		    	}else if(YmConstant.DMYDR016.equals(TcCode)){			//외판슬라브운송지시대기
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_NG;
		    		
		    	}else if(YmConstant.DMYDR060.equals(TcCode)||			//코일제품운송지시
		    			 YmConstant.DMYDR022.equals(TcCode) ){			//외판슬라브운송상차지시
		    		
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_LG;
		    	}else if(YmConstant.CURR_PROG_CD_COIL_A.equals(CurrProgCd)||
		    			 YmConstant.CURR_PROG_CD_COIL_R.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_AC;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_B.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_BC;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_C.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_CC;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_D.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_DC;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_E.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_CS;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_F.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_FC;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_K.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_KG;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_G.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_GC;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_H.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_HG;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_J.equals(CurrProgCd)){
		    		if(YmConstant.RETURN_GP_1.equals(ReturnGp)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_JR;
		    		}else{
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_JG;
		    		}
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_L.equals(CurrProgCd)){//코일제품상차지시 
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_LG;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_N.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_NG;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_M.equals(CurrProgCd)||
		    			 YmConstant.CURR_PROG_CD_COIL_P.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_MG;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_X.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_XG;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_Y.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_YG;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_Z.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_ZG;
		    	}															
		    	
		    	jrRtn.setResultCode(logId);	//Log ID
		    	jrRtn.setResultMsg(methodNm);	//Log Method Name
//		    	jrRtn.setField("STOCK_ID"		, StlNo); //충당재료
//		    	jrRtn.setField("MODIFIER" 		, modifier); //수정자
		    	jrRtn.setField("CURR_PROG_CD"  	, CurrProgCd); 	//진도코드
		    	jrRtn.setField("STOCK_MOVE_TERM", ydStocMv  );	//저장품 이동 조건
			} 

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
	 *      [A] 오퍼레이션명 : 진도코드 get
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getCoilCurrProgCd2(JDTORecord rcvMsg) throws DAOException {	
		String methodNm = "진도코드Check2[YmComm.getCoilCurrProgCd2] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String TcCode 		= commUtils.trim(rcvMsg.getFieldString("TC_CD"));	//TC_CD
			String StlNo 		= commUtils.trim(rcvMsg.getFieldString("STOCK_ID"));//재료
			String CurrProgCd	= commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"));//진도코드
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 2. COILCOMM READ
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("COIL_NO"	, StlNo); //충당재료
			jrParam.setField("MODIFIER" , modifier); //수정자

			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilComByCurrProgCd 
			SELECT DECODE(CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',CURR_PROG_CD) AS CURR_PROG_CD
		     	 , RETURN_GP  --반납구분
		      FROM USRPTA.TB_PT_COILCOMM 
		     WHERE COIL_NO = :V_COIL_NO   -- 재료번호
		 	*/
			JDTORecordSet jsStl = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilComByCurrProgCd", logId, methodNm, "CoilComm 조회");
			
			String ydStocMv = "";
			
			if (jsStl != null && jsStl.size() > 0) {
				
				String ReturnGp 	= commUtils.trim(jsStl.getRecord(0).getFieldString("RETURN_GP")); 	//반납구분
			   	

		    	if(YmConstant.CURR_PROG_CD_COIL_B.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_BC;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_A.equals(CurrProgCd)||
		    			 YmConstant.CURR_PROG_CD_COIL_R.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_AC;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_C.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_CC;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_D.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_DC;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_E.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_CS;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_F.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_FC;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_K.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_KG;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_G.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_GC;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_H.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_HG;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_J.equals(CurrProgCd)){
		    		if(YmConstant.RETURN_GP_1.equals(ReturnGp)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_JR;
		    		}else{
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_JG;
		    		}
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_L.equals(CurrProgCd)){//코일제품상차지시 
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_LG;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_M.equals(CurrProgCd)||
		    			 YmConstant.CURR_PROG_CD_COIL_P.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_MG;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_N.equals(CurrProgCd)){
	    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_NG;
	    			
		    	}else if(YmConstant.CURR_PROG_CD_COIL_X.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_XG;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_Y.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_YG;
		    		
		    	}else if(YmConstant.CURR_PROG_CD_COIL_Z.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_ZG;
		    	}															
		    	
		    	jrRtn.setResultCode(logId);	//Log ID
		    	jrRtn.setResultMsg(methodNm);	//Log Method Name
//		    	jrRtn.setField("STOCK_ID"		, StlNo); //충당재료
//		    	jrRtn.setField("MODIFIER" 		, modifier); //수정자
		    	jrRtn.setField("CURR_PROG_CD"  	, CurrProgCd); 	//진도코드
		    	jrRtn.setField("STOCK_MOVE_TERM", ydStocMv  );	//저장품 이동 조건
			} 

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
	 *      [A] 오퍼레이션명 : 진도코드 get(slab)
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getSlabCurrProgCd(JDTORecord rcvMsg) throws DAOException {	
		String methodNm = "진도코드Check[YmComm.getSlabCurrProgCd] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId     = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String TcCode 	 = commUtils.trim(rcvMsg.getFieldString("TC_CD"));	//TC_CD
			String sSTOCK_ID = commUtils.trim(rcvMsg.getFieldString("STOCK_ID"));//재료
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 2. COILCOMM READ
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("SLAB_NO"	, sSTOCK_ID); 
			jrParam.setField("MODIFIER" , modifier); //수정자


			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectSlabMatirialInfo", logId, methodNm, "SlabComm 조회");
			
			String sSTOCK_MOVE_TERM = "";
			
			if (rsResult != null && rsResult.size() > 0) {
				String sCURR_PROG_CD     = commUtils.trim(rsResult.getRecord(0).getFieldString("CURR_PROG_CD"));
				String sWO_MSLAB_RPR_MTD = commUtils.trim(rsResult.getRecord(0).getFieldString("WO_MSLAB_RPR_MTD"));
			   	
		    	/* 일관제철 진도코드 */
		    	if(YmConstant.DMYDR016.equals(TcCode) ){				//외판슬라브운송지시대기
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_NS;
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_0.equals(sCURR_PROG_CD)){    		
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_11; 
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_1.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_12;	
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_A.equals(sCURR_PROG_CD)){
		    		if("Q".equals(sWO_MSLAB_RPR_MTD)){
		        		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_D3;	//핸드스카핑작업대기
		    		}else{
		    			sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_DS;
		    		}
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_B.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ES;
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_C.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_FS;
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_D.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_BS;
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_E.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_CS;
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_F.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_YS;
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_G.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_GS; // 종합판정대기
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_H.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_HS; // 입고대기
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_J.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_JS; // 반납대기
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_K.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_KS;
				}else if(YmConstant.CURR_PROG_CD_SLAB_L.equals(sCURR_PROG_CD)){
			    	sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_LS;
				}else if(YmConstant.CURR_PROG_CD_SLAB_M.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_MS;    		
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_N.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_NS;    	
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_Y.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ZS;    	
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_Z.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ZS;
		    	}															
		    	
		    	jrRtn.setResultCode(logId);	//Log ID
		    	jrRtn.setResultMsg(methodNm);	//Log Method Name
		    	jrRtn.setField("CURR_PROG_CD"  	, sCURR_PROG_CD); 	//진도코드
		    	jrRtn.setField("STOCK_MOVE_TERM", sSTOCK_MOVE_TERM  );	//저장품 이동 조건
			} 

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
	 *      [A] 오퍼레이션명 : 저장품 이동 조건
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String getStockMv(String logId , String methodNm, String StockId) throws DAOException {
		YmCommDAO commDao = new YmCommDAO();
		YmCommUtils commUtil = new YmCommUtils();

		String sProgCd 		= "";
		String sNextProc 	= "";
		String sPlanProc 	= "";
		String sCoilProc    = "";
		String sStockMv     = "";   //return sStockMv

		try {
			commUtil.printLog(logId, methodNm, "S+");

			//수신 항목 값
			JDTORecord jrParam = commUtil.getParam(logId, methodNm, "");
			jrParam.setField("COIL_NO", StockId); 

			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilComByCurrProgCd 
			SELECT DECODE(CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',CURR_PROG_CD) AS CURR_PROG_CD
			     , RETURN_GP  --반납구분
			     , NEXT_PROC  --차공정
			     , PLAN_PROC1 -- 계획공정
			  FROM USRPTA.TB_PT_COILCOMM 
			 WHERE COIL_NO = :V_COIL_NO   -- 재료번호
			*/ 
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilComByCurrProgCd", logId, methodNm, "열정보 Read"); 

			if (jsChk.size() > 0) {
				sProgCd 	= commUtil.trim(jsChk.getRecord(0).getFieldString("CURR_PROG_CD"));
				sNextProc 	= commUtil.trim(jsChk.getRecord(0).getFieldString("NEXT_PROC"));
				sPlanProc 	= commUtil.trim(jsChk.getRecord(0).getFieldString("PLAN_PROC1"));
			}
			
			if("".equals(sNextProc)){
				sCoilProc = sPlanProc;
			}else{
				sCoilProc = sNextProc;
			}
			
			if(YmConstant.CURR_PROG_CD_COIL_1.equals(sProgCd)){
				sStockMv   = YmConstant.NEW_STOCK_MOVE_TERM_1C;
	    	}else{
	    		if(YmConstant.SHEAR_SUPPLY_GP_5K.equals(sCoilProc)){		 	//B열연 SPM
					sStockMv = YmConstant.NEW_STOCK_MOVE_TERM_A2;				//SPM 추출
				}else if(YmConstant.SHEAR_SUPPLY_GP_5H.equals(sCoilProc)){		//B열연 HFL
					sStockMv = YmConstant.NEW_STOCK_MOVE_TERM_A1;				//HFL 추출
				}else if(YmConstant.SHEAR_SUPPLY_GP_5T.equals(sCoilProc)){		//B열연 수냉재
					sStockMv = YmConstant.NEW_STOCK_MOVE_TERM_A3;				//수냉재 추출
				}else if(YmConstant.SHEAR_SUPPLY_GP_5A.equals(sCoilProc)){		//B열연 공냉재
					sStockMv = YmConstant.NEW_STOCK_MOVE_TERM_A4;				//공냉재 추출
				}else if(YmConstant.SHEAR_SUPPLY_GP_6H.equals(sCoilProc)){		//B열연 HFL결속대
					sStockMv = YmConstant.NEW_STOCK_MOVE_TERM_A7;				//B열연 HFL결속대
				}else if(YmConstant.SHEAR_SUPPLY_GP_6K.equals(sCoilProc)){		//B열연2SPM
					sStockMv = YmConstant.NEW_STOCK_MOVE_TERM_A6;				//B열연2SPM
				}else{
					sStockMv =  YmConstant.NEW_STOCK_MOVE_TERM_A2;
				}
			}

			commUtil.printLog(logId, methodNm, "S-");

			return sStockMv;
		} catch (DAOException e) {
			commUtil.printErrorLog(commUtil.makeErrorLog(logId, methodNm, e), commUtil, e);
			return sStockMv;
		} catch (Exception e) {
			return sStockMv;
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 열정보 read
	 *      -- 용도  기존 getStackColInfoWithPk
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String readStackColInfo(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "열용도Check[YmComm.readStackColInfo] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		YmCommDAO commDao = new YmCommDAO();
		YmCommUtils commUtil = new YmCommUtils();
		String ydSTACK_COL_USAGE_CD = "";

		try {
			commUtil.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String StackColGp = commUtil.trim(rcvMsg.getFieldString("TAG_STACK_COL_GP")); //야드 열
			
			/**********************************************************
			* 2. 열정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtil.getParam(logId, methodNm, "");
			jrParam.setField("STACK_COL_GP", StackColGp); //열

			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStackColInfoWithPk", logId, methodNm, "열정보 Read"); 

			if (jsChk.size() > 0) {
				ydSTACK_COL_USAGE_CD = commUtil.trim(jsChk.getRecord(0).getFieldString("STACK_COL_USAGE_CD"));
			}

			commUtil.printLog(logId, methodNm, "S-");

			return ydSTACK_COL_USAGE_CD;
		} catch (DAOException e) {
			commUtil.printErrorLog(commUtil.makeErrorLog(logId, methodNm, e), commUtil, e);
			return ydSTACK_COL_USAGE_CD;
		} catch (Exception e) {
			return ydSTACK_COL_USAGE_CD;
		}
	}	
		
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업실적응답(YMA7L005) 전문 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYMA7L005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업실적응답 조회[YmComm.getYMA7L005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//수신 항목 값
			String msgId      = ""; //전문ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID
			String ydL2WrGp   = commUtils.trim(rcvMsg.getFieldString("YD_L2_WR_GP"   )); //야드L2실적구분
			String ydL3HdRsCd = commUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD")); //야드L3처리결과코드
			String ydL3Msg    = commUtils.trim(rcvMsg.getFieldString("YD_L3_MSG"     )); //야드L3MESSAGE

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				return null;
			}

			if (ydEqpId.startsWith("3")) {
				//B열연코일
				msgId = "YMA7L005";
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

			JDTORecord sndMsg = JDTORecordFactory.getInstance().create();

			sndMsg.setResultCode(logId);	//Log ID
			sndMsg.setResultMsg(methodNm);	//Log Method Name
			sndMsg.addField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
			sndMsg.addField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
			sndMsg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage

			//전송 Data Return
			return commUtils.addSndData(sndMsg);
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 일반작업예약 생성
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String  procWkBookInsert(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "작업예약생성[YmComm.procWkBookInsert] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		String ydSchPrior = "";
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String ydStackColGp = commUtils.trim(rcvMsg.getFieldString("STACK_COL_GP"));   //적치 열 구분
			String ydStackBedGp = commUtils.trim(rcvMsg.getFieldString("STACK_BED_GP"));   //적치 BED구분
			String ydStackLayer = commUtils.trim(rcvMsg.getFieldString("STACK_LAYER_GP")); //적치 단 구분
			String ydSchCd 		= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));      //야드스케쥴코드
			String toLocGuide	= commUtils.trim(rcvMsg.getFieldString("YD_TO_LOC_GUIDE"));//TO위치 가이드
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));       //야드L3MESSAGE
			String sYD_EQP_ID   = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")), "");  //설비ID
			
			String sSTOCK_ID    = commUtils.trim(rcvMsg.getFieldString("STOCK_ID"));      //저장품(SPM2 커팅후보급 일 때만 사용)
			
			JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
			recInTemp1.setField("YD_SCH_CD", ydSchCd);
	    	/*  
			SELECT YD_SCH_CD
			     , YD_WRK_CRN
			     , YD_WRK_CRN_PRIOR
			  FROM TB_YM_SCHEDULERULE
			 WHERE DEL_YN = 'N'
			   AND YD_SCH_CD = :V_YD_SCH_CD
			*/   
			JDTORecordSet jsResult = commDao.select(recInTemp1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
	    	
			if (jsResult != null && jsResult.size() > 0) {
				ydSchPrior = jsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
			} else {
				return YmConstant.RETN_CD_FAILURE;
			}

			
			if ("".equals(sYD_EQP_ID)) {
				sYD_EQP_ID = jsResult.getRecord(0).getFieldString("YD_WRK_CRN");
			}
			
			
			//작업예약ID 조회
			String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
			if ("".equals(ydWbookId)) {
				return YmConstant.RETN_CD_FAILURE;
			}
			
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			//작업예약 등록
			recInTemp.setField("YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
			recInTemp.setField("MODIFIER"          , modifier      ); //수정자
			recInTemp.setField("YD_GP"             , ydStackColGp.substring(0,1)          ); //야드구분
			recInTemp.setField("YD_BAY_GP"         , ydStackColGp.substring(1,2)       ); //야드동구분
			recInTemp.setField("YD_SCH_CD"         , ydSchCd       ); //야드스케쥴코드
			recInTemp.setField("YD_SCH_PRIOR"      , ydSchPrior    ); //야드스케쥴우선순위
			recInTemp.setField("YD_SCH_PROG_STAT"  , "W"           ); //야드스케쥴진행상태(스케줄수행대기)
			recInTemp.setField("YD_SCH_ST_GP"      , "O"           ); //야드스케쥴기동구분(Manual)
			recInTemp.setField("YD_SCH_REQ_GP"     , "M"           ); //야드스케쥴요청구분(이적)
			recInTemp.setField("YD_TO_LOC_GUIDE"   , toLocGuide    ); //TO위치가이드
			recInTemp.setField("YD_WRK_PLAN_CRN"   , sYD_EQP_ID    ); //작업예약 크레인
			

			int ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "TB_YM_WRKBOOK");
			if (ins_cnt <= 0) {
				//throw new JDTOException("작업예약 등록실패");
				return YmConstant.RETN_CD_FAILURE;

			}
			        
			recInTemp = JDTORecordFactory.getInstance().create();
			//작업예약 재료 등록
			recInTemp.setField("YD_WBOOK_ID"      , ydWbookId     ); //야드작업예약ID
			recInTemp.setField("MODIFIER"         , modifier      ); //수정자
			recInTemp.setField("STACK_COL_GP"     , ydStackColGp  ); //적치열구분
			recInTemp.setField("STACK_BED_GP"     , ydStackBedGp  ); //적치BED구분
			recInTemp.setField("STACK_LAYER_GP"   , ydStackLayer  ); //적치단구분
			recInTemp.setField("STOCK_ID"         , sSTOCK_ID     ); //저장품(SPM2 커팅후보급 일 때만 사용)

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBookMtlByStkLyr
			--적재위치대상재 작업예약재료 등록 
			MERGE INTO TB_YM_WRKBOOKMTL WM USING (
			SELECT :V_YD_WBOOK_ID   AS YD_WBOOK_ID --야드작업예약ID
			     , SL.STOCK_ID                     --재료번호
			     , :V_MODIFIER      AS MODIFIER    --수정자
			     , SYSDATE          AS MOD_DDTT    --수정일시
			     , 'N'              AS DEL_YN      --삭제유무
			     , SL.STACK_COL_GP                 --야드적치열구분
			     , SL.STACK_BED_GP                 --야드적치Bed번호
			     , SL.STACK_LAYER_GP               --야드적치단번호
			  FROM TB_YM_STACKLAYER SL
			 WHERE SL.STACK_COL_GP   = :V_STACK_COL_GP
			   AND SL.STACK_BED_GP   = :V_STACK_BED_GP
			   AND SL.STACK_LAYER_GP = :V_STACK_LAYER_GP
			   AND SL.STOCK_ID IS NOT NULL
			) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.STOCK_ID = DD.STOCK_ID)
			WHEN NOT MATCHED THEN
			INSERT (WM.YD_WBOOK_ID   , WM.STOCK_ID        , WM.REGISTER      , WM.REG_DDTT    ,
			        WM.MODIFIER      , WM.MOD_DDTT        , WM.DEL_YN        , WM.STACK_COL_GP,
			        WM.STACK_BED_GP  , WM.STACK_LAYER_GP )
			VALUES (DD.YD_WBOOK_ID   , DD.STOCK_ID        , DD.MODIFIER      , DD.MOD_DDTT    ,
			        DD.MODIFIER      , DD.MOD_DDTT        , DD.DEL_YN        , DD.STACK_COL_GP,
			        DD.STACK_BED_GP  , DD.STACK_LAYER_GP )
			*/        

			if ("3EKE02MM".equals(ydSchCd)) {
				//SPM2 커팅후보급 은  파라메터로 전달 된 sSTOCK_ID 값으로 작업예약재료 등록 한다.
				ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBookMtlByStkLyrSPM2", logId, methodNm, "TB_YM_WRKBOOKMTL SPM2");
			} else {
				//그 외 작업은 파라메터로 전달 된 적치 열, BED, 단 에 위치한 재료번호로 작업예약 재료 등록 한다.
				ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBookMtlByStkLyr", logId, methodNm, "TB_YM_WRKBOOKMTL");
			}
			
			if (ins_cnt <= 0) {
				//throw new JDTOException("작업예약 재료 등록실패");
				return YmConstant.RETN_CD_FAILURE;
			}
			
			commUtils.printLog(logId, methodNm, "S-");
			
			return ydWbookId;
			
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return YmConstant.RETN_CD_FAILURE;
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 작업예약 생성-차량
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String  procCarWkBookInsert(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "작업예약 생성-차량[YmComm.procCarWkBookInsert] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		String ydSchPrior = "";
		try {
			//수신 항목 값
			String ydStackColGp = commUtils.trim(rcvMsg.getFieldString("STACK_COL_GP"));   //적재위치
			String ydCarSchId   = commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"));  //차량스케줄
			String ydSchCd 		= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));      //야드스케쥴코드
			String ydCarNo 		= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));         //차량번호
			String YdCardNo 	= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));        //차량번호
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));       //야드L3MESSAGE

			
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_SCH_CD"	, ydSchCd);
			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
	    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
			SELECT YD_SCH_CD
			     , YD_WRK_CRN
			     , YD_WRK_CRN_PRIOR
			  FROM TB_YM_SCHEDULERULE
			 WHERE DEL_YN = 'N'
			   AND YD_SCH_CD = :V_YD_SCH_CD
			*/   
			JDTORecordSet jsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
	    	
			if (jsResult != null && jsResult.size() > 0) {
				ydSchPrior = jsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
			} else {
				return YmConstant.RETN_CD_FAILURE;
			}

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStockTransOrdDT70Wbook 
			SELECT A.STOCK_ID  
			     , SUBSTR(B.STACK_COL_GP,2,1) AS YD_BAY_GP
			     , B.STACK_COL_GP 
			     , B.STACK_BED_GP 
			     , B.STACK_LAYER_GP
			  FROM USRYMA.TB_YM_STOCK A
			     , USRYMA.TB_YM_STACKLAYER B
			     , USRYDA.TB_YD_CARPOINT C
			     , USRYDA.TB_YD_CARSCH D
			 WHERE A.STOCK_ID = B.STOCK_ID
			   AND A.TRANS_ORD_DATE2 = D.TRANS_ORD_DATE
			   AND A.TRANS_ORD_SEQNO2 =D.TRANS_ORD_SEQNO
			   AND A.CAR_NO2          =D.CAR_NO
			   AND B.STACK_COL_GP LIKE :V_YD_GP ||:V_YD_BAY_GP ||'%'
			   AND C.YD_STK_COL_GP=:V_YD_STK_COL_GP
			    AND 1 = CASE WHEN YD_EQP_WRK_STAT = 'L' THEN 1
			                WHEN YD_EQP_WRK_STAT = 'U' 
			                     AND SUBSTR(B.STACK_COL_GP,3,2) BETWEEN '00' AND '99' 
			                     AND SUBSTR(B.STACK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO 
			                                           THEN 1
			                ELSE 2 END 
			   AND B.STACK_LAYER_STAT IN ('C')
			   AND D.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			   AND D.DEL_YN = 'N'
			  ORDER BY B.STACK_LAYER_GP DESC
			   
			*/
			jrParam.setField("YD_GP"		 , ydStackColGp.substring(0, 1));
			jrParam.setField("YD_BAY_GP"	 , ydStackColGp.substring(1, 2));
			jrParam.setField("YD_STK_COL_GP" , ydStackColGp);
			jrParam.setField("YD_CAR_SCH_ID" , ydCarSchId);
			JDTORecordSet jsStock = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStockTransOrdDT70Wbook", logId, methodNm, "스케줄 기준 조회");			
			
			if ( jsStock.size() < 1) {
				return YmConstant.RETN_CD_FAILURE;
			}
			
			String first_wbook_ID = "";
			
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			JDTORecord jrInTemp = JDTORecordFactory.getInstance().create();
	    	for(int Loop_i = 0; Loop_i < jsStock.size(); Loop_i++) {
	    		
				//작업예약 등록
				String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
				if("".equals(first_wbook_ID)) {
					first_wbook_ID = ydWbookId; //첫번째 작업예약 ID 
				}				
				
				recInTemp.setField("YD_WBOOK_ID"       	, ydWbookId     ); //야드작업예약ID
				recInTemp.setField("MODIFIER"          	, modifier      ); //수정자
				recInTemp.setField("YD_GP"              , ydStackColGp.substring(0,1) ); //야드구분
				recInTemp.setField("YD_BAY_GP"          , ydStackColGp.substring(1,2) ); //야드동구분
				recInTemp.setField("YD_SCH_CD"         	, ydSchCd       ); //야드스케쥴코드
				recInTemp.setField("YD_SCH_PRIOR"      	, ydSchPrior    ); //야드스케쥴우선순위
				recInTemp.setField("YD_SCH_PROG_STAT"  	, "W"           ); //야드스케쥴진행상태(스케줄수행대기)
				recInTemp.setField("YD_SCH_ST_GP"      	, "O"           ); //야드스케쥴기동구분(Manual)
				recInTemp.setField("YD_SCH_REQ_GP"     	, "M"           ); //야드스케쥴요청구분(이적)
				recInTemp.setField("YD_CAR_USE_GP"		, "G");
				recInTemp.setField("CAR_NO"				, ydCarNo);
				recInTemp.setField("CARD_NO"			, YdCardNo);
				recInTemp.setField("YD_AIM_YD_GP"		, ydStackColGp.substring(0,1) ); //야드구분);
				recInTemp.setField("YD_AIM_BAY_GP"		, ydStackColGp.substring(1,2) ); //야드동구분);
				commDao.insert(recInTemp, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "TB_YM_WRKBOOK");
	    		
	    		//작업예약재료 등록
	    		jrInTemp.setField("YD_WBOOK_ID"		, ydWbookId);
	    		jrInTemp.setField("MODIFIER"		, modifier);
	    		jrInTemp.setField("STACK_COL_GP"	, ydStackColGp);
	    		jrInTemp.setField("STACK_BED_GP"	, commUtils.trim(jsStock.getRecord(Loop_i).getFieldString("STACK_BED_GP"       )));
	    		jrInTemp.setField("STACK_LAYER_GP"	, commUtils.trim(jsStock.getRecord(Loop_i).getFieldString("STACK_LAYER_GP"       )));
	    		jrInTemp.setField("STOCK_ID"		, commUtils.trim(jsStock.getRecord(Loop_i).getFieldString("STOCK_ID"       )));
	    		jrInTemp.setField("YD_UP_COLL_SEQ"	, "" + Loop_i);
	    		commDao.insert(jrInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insWrkBookMtl", logId, methodNm, "TB_YM_WRKBOOKMTL");
	    	}		
	    	
			return first_wbook_ID;
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return YmConstant.RETN_CD_FAILURE;
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 차량작업예정정보요구(procCarPlanInfo)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procCarPlanInfo(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "차량작업예정정보요구[YmComm.procCarPlanInfo] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String SearchFlag	= commUtils.trim(rcvMsg.getFieldString("SEARCH_FLAG"));   	//1:상차도, 2:차량스케쥴 ID
			String ydLoadLoc   	= commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC"));    	//상차도 위치
			String ydCarSchId  	= commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"));  	//차량스케쥴 ID
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); 		//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (SearchFlag.length() < 0) {
				commUtils.printLog(logId, methodNm + " 검색조건 없음 [" + SearchFlag + "]" , "SL");
				//throw new Exception("검색조건 없음 [" + SearchFlag + "]");
			} else if (SearchFlag.equals("1")) {
				if (ydLoadLoc.length() < 6) {
					commUtils.printLog(logId, methodNm + " 상차도 위치 Error [" + ydLoadLoc + "]" , "SL");
					//throw new Exception("상차도 위치 Error [" + ydLoadLoc + "]");
				}
			} else if (SearchFlag.equals("2")) {
				if (ydCarSchId.equals("")) {
					commUtils.printLog(logId, methodNm + " 차량스케쥴 ID Error [" + ydCarSchId + "]" , "SL");
					//throw new Exception("차량스케쥴 ID Error [" + ydCarSchId + "]");
				}
			} 
			JDTORecordSet jrCarInfo = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"                 , modifier);	
			
			/**********************************************************
			* 2. 차량예정정보 조회
			**********************************************************/			
			if(SearchFlag.equals("1")) {
				//상차위치로 차량예정정보 조회
				jrParam.setField("YD_CARUD_STOP_LOC", ydLoadLoc);  	
//PIDEV_S :병행가동용:PI_YD
				jrParam.setField("PI_YD",    	"3");						
				jrCarInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarschCarGetInWorkByCarNo_PIDEV", logId, methodNm, "상차위치로 차량예정정보 조회");
			} else {
				//차량스케줄ID로 차량예정정보 조회
				jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);  					
				//PIDEV_S :병행가동용:PI_YD
				jrParam.setField("PI_YD",    	"3");						
				jrCarInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarschCarGetInWorkInfo_PIDEV", logId, methodNm, "차량스케줄ID로 차량예정정보 조회"); 
			}

			/**********************************************************
			* 2. 차량예정정보 송신
			**********************************************************/
			if(jrCarInfo.size() > 0) {
				
				jrCarInfo.first();
				
				JDTORecord jsCarInfo = JDTORecordFactory.getInstance().create();
				JDTORecord jsCarInfo1 = JDTORecordFactory.getInstance().create();
				jsCarInfo.setRecord(jrCarInfo.getRecord());
				
				//차량작업 예정정보 전문 data setup
			    jrParam.setField("PT_LOAD_LOC"      	, commUtils.trim(jsCarInfo.getFieldString("YD_PT_LOAD_LOC")));   	// 상차도 위치				
			    jrParam.setField("CAR_NO"      			, commUtils.trim(jsCarInfo.getFieldString("YD_CAR_NO")));  			// 차량번호	
			    jrParam.setField("CARD_NO"      		, commUtils.trim(jsCarInfo.getFieldString("YD_CARD_NO"))); 			// 차량번호	
			    jrParam.setField("PT_CLS"       		, commUtils.trim(jsCarInfo.getFieldString("YD_PT_CLS")));   		// 차량구분				
			    jrParam.setField("WORK_CLS"      		, commUtils.trim(jsCarInfo.getFieldString("YD_WORK_CLS")));			// 작업구분  				
			    jrParam.setField("WORK_COIL_MAX_CNT"	, commUtils.trim(jsCarInfo.getFieldString("YD_WORK_COIL_MAX_CNT")));// 작업총 수량 				
	
			    for (int ii = 0; ii < jrCarInfo.size(); ii++) {
			    	//jrCarInfo.absolute(ii);
			    	//jsCarInfo1.setRecord(jrCarInfo.getRecord());
	        		
	//				jrParam.setField("STOCK_ID_"+ii		 , commUtils.trim(jsCarInfo1.getFieldString("STOCK_ID")));		//재료번호
	//				jrParam.setField("LOAD_LOC_CD_"+ii	 , commUtils.trim(jsCarInfo1.getFieldString("YD_LOAD_LOC_CD")));//차량적재위치
	//				jrParam.setField("MAT_WGT_"+ii	     , commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_WT"))); 	//재료중량
	//				jrParam.setField("MAT_THK_"+ii	     , commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_T"))); 	//재료두께
	//				jrParam.setField("MAT_WTH_"+ii	     , commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_W"))); 	//재료폭
	//				jrParam.setField("MAT_LEN_"+ii	     , commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_LEN"))); 	//재료길이
	//				jrParam.setField("MAT_ODIA_"+ii	     , commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_OUTDIA")));//재료외경
	//				jrParam.setField("MAT_IDIA_"+ii	     , commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_INDIA"))); //재료내경
	//				jrParam.setField("YD_WORK_STATE_"+ii , commUtils.trim(jsCarInfo1.getFieldString("YD_WORK_STATE"))); //작업상태
	//				jrParam.setField("YD_CURR_BAY_GP_"+ii, commUtils.trim(jsCarInfo1.getFieldString("YD_CURR_BAY_GP")));//동정보
/*								
			    	// 송신 쿼리에서 자리수 재조정함
					String stockInfo = 
					( commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_STL_NO"))		, 11, " ")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_LOAD_LOC_CD"))	,  2, " ")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_WT"))		, 10, "0")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_T"))		, 10, "0")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_W"))		, 10, "0")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_LEN"))	, 10, "0")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_OUTDIA"))	, 10, "0")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_INDIA"))	, 10, "0")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_WORK_STATE"))	,  1, " ")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_CURR_BAY_GP"))	,  6, " "));
					
					commUtils.printLog(logId, methodNm + "재료정보 :" +stockInfo , "SL");
					
					jrParam.setField("STOCK_INFO_"+ii, stockInfo); //재료그룹
					
					//주석처리된 정보는 코일공통 테이블에서 가져오도록 쿼리 작성함..
*/
			    	
			    	jrParam.setField("STOCK_ID_"+ii		, commUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_STL_NO"))); 
			    	jrParam.setField("LOAD_LOC_CD_"+ii	, commUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_LOAD_LOC_CD")));
			    	jrParam.setField("WORK_STATE_"+ii	, commUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_WORK_STATE")));
			    	
				}
	
				//차량예정정보 백업 송신
				//jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L008", jrParam));
			    jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L008BackUp", jrParam));
			    
			} else {
				//빈 전문 생성
			    jrParam.setField("PT_LOAD_LOC"      	, ydLoadLoc);   // 상차도 위치				
			    jrParam.setField("CAR_NO"      			, "");  		// 차량번호	
			    jrParam.setField("CARD_NO"      		, ""); 			// 차량번호	
			    jrParam.setField("PT_CLS"       		, "");   		// 차량구분				
			    jrParam.setField("WORK_CLS"      		, "");			// 작업구분  				
			    jrParam.setField("WORK_COIL_MAX_CNT"	, "0");			// 작업총 수량
			    
		    	jrParam.setField("STOCK_ID_0"			, ""); 
		    	jrParam.setField("LOAD_LOC_CD_0"		, "");
		    	jrParam.setField("WORK_STATE_0"			, "");
				
		    	jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L008BackUp", jrParam));
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 차량작업예정정보요구(procCarPlanInfo)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procCarPlanInfo_Slab(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "차량작업예정정보요구[YmComm.procCarPlanInfo] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String SearchFlag	= commUtils.trim(rcvMsg.getFieldString("SEARCH_FLAG"));   	//1:상차도, 2:차량스케쥴 ID
			String ydLoadLoc   	= commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC"));    	//상차도 위치
			String ydCarSchId  	= commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"));  	//차량스케쥴 ID
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); 		//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (SearchFlag.length() < 0) {
				commUtils.printLog(logId, methodNm + " 검색조건 없음 [" + SearchFlag + "]" , "SL");
				//throw new Exception("검색조건 없음 [" + SearchFlag + "]");
			} else if (SearchFlag.equals("1")) {
				if (ydLoadLoc.length() < 6) {
					commUtils.printLog(logId, methodNm + " 상차도 위치 Error [" + ydLoadLoc + "]" , "SL");
					//throw new Exception("상차도 위치 Error [" + ydLoadLoc + "]");
				}
			} else if (SearchFlag.equals("2")) {
				if (ydCarSchId.equals("")) {
					commUtils.printLog(logId, methodNm + " 차량스케쥴 ID Error [" + ydCarSchId + "]" , "SL");
					//throw new Exception("차량스케쥴 ID Error [" + ydCarSchId + "]");
				}
			} 
			JDTORecordSet jrCarInfo = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"                 , modifier);	
			
			/**********************************************************
			* 2. 차량예정정보 조회
			**********************************************************/			
			if(SearchFlag.equals("1")) {
				//상차위치로 차량예정정보 조회
				jrParam.setField("YD_CARUD_STOP_LOC", ydLoadLoc);  	
				jrCarInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdCarschCarGetInWorkByCarNo", logId, methodNm, "상차위치로 차량예정정보 조회");
			} else if(SearchFlag.equals("2")) {
				//차량스케줄ID로 차량예정정보 조회
				jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);  					
				jrCarInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdCarschCarGetInWorkInfo", logId, methodNm, "차량스케줄ID로 차량예정정보 조회"); 
			} else if(SearchFlag.equals("3")) {
				jrParam.setField("YD_CARUD_STOP_LOC", ydLoadLoc);
				jrParam.setField("YD_WBOOK_ID"		, commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID")));
				jrCarInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdCarschCarGetInWorkByWbookId", logId, methodNm, "상차 차량예정정보 조회");
			} else if(SearchFlag.equals("4")) {
				jrParam.setField("YD_CARUD_STOP_LOC", ydLoadLoc);
				jrCarInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdCarschCarGetInWork", logId, methodNm, "상차 차량예정정보 조회");
			} else if(SearchFlag.equals("5")) {
				jrParam.setField("LOC", ydLoadLoc);
				jrCarInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdCarschCarGetInWorkByTcLoc", logId, methodNm, "대차 차량예정정보 조회");
			}

			/**********************************************************
			* 2. 차량예정정보 송신
			**********************************************************/
			if(jrCarInfo.size() > 0) {
				
				jrCarInfo.first();
				
				JDTORecord jsCarInfo = JDTORecordFactory.getInstance().create();
				jsCarInfo.setRecord(jrCarInfo.getRecord());
				
				//차량작업 예정정보 전문 data setup
			    jrParam.setField("PT_LOAD_LOC"      	, commUtils.trim(jsCarInfo.getFieldString("YD_PT_LOAD_LOC")));   	// 상차도 위치				
			    jrParam.setField("CAR_NO"      			, commUtils.trim(jsCarInfo.getFieldString("YD_CAR_NO")));  			// 차량번호	
			    jrParam.setField("CARD_NO"      		, commUtils.trim(jsCarInfo.getFieldString("YD_CARD_NO"))); 			// 차량번호	
			    jrParam.setField("PT_CLS"       		, commUtils.trim(jsCarInfo.getFieldString("YD_PT_CLS")));   		// 차량구분				
			    jrParam.setField("WORK_CLS"      		, commUtils.trim(jsCarInfo.getFieldString("YD_WORK_CLS")));			// 작업구분  				
			    jrParam.setField("WORK_COIL_MAX_CNT"	, commUtils.trim(jsCarInfo.getFieldString("YD_WORK_COIL_MAX_CNT")));// 작업총 수량 				
	
			    for (int ii = 0; ii < jrCarInfo.size(); ii++) {
			    	//jrCarInfo.absolute(ii);
			    	//jsCarInfo1.setRecord(jrCarInfo.getRecord());
	        		
	//				jrParam.setField("STOCK_ID_"+ii		 , commUtils.trim(jsCarInfo1.getFieldString("STOCK_ID")));		//재료번호
	//				jrParam.setField("LOAD_LOC_CD_"+ii	 , commUtils.trim(jsCarInfo1.getFieldString("YD_LOAD_LOC_CD")));//차량적재위치
	//				jrParam.setField("MAT_WGT_"+ii	     , commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_WT"))); 	//재료중량
	//				jrParam.setField("MAT_THK_"+ii	     , commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_T"))); 	//재료두께
	//				jrParam.setField("MAT_WTH_"+ii	     , commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_W"))); 	//재료폭
	//				jrParam.setField("MAT_LEN_"+ii	     , commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_LEN"))); 	//재료길이
	//				jrParam.setField("MAT_ODIA_"+ii	     , commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_OUTDIA")));//재료외경
	//				jrParam.setField("MAT_IDIA_"+ii	     , commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_INDIA"))); //재료내경
	//				jrParam.setField("YD_WORK_STATE_"+ii , commUtils.trim(jsCarInfo1.getFieldString("YD_WORK_STATE"))); //작업상태
	//				jrParam.setField("YD_CURR_BAY_GP_"+ii, commUtils.trim(jsCarInfo1.getFieldString("YD_CURR_BAY_GP")));//동정보
/*								
			    	// 송신 쿼리에서 자리수 재조정함
					String stockInfo = 
					( commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_STL_NO"))		, 11, " ")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_LOAD_LOC_CD"))	,  2, " ")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_WT"))		, 10, "0")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_T"))		, 10, "0")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_W"))		, 10, "0")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_LEN"))	, 10, "0")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_OUTDIA"))	, 10, "0")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_COIL_INDIA"))	, 10, "0")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_WORK_STATE"))	,  1, " ")
					+ commUtils.getRPad(commUtils.trim(jsCarInfo1.getFieldString("YD_CURR_BAY_GP"))	,  6, " "));
					
					commUtils.printLog(logId, methodNm + "재료정보 :" +stockInfo , "SL");
					
					jrParam.setField("STOCK_INFO_"+ii, stockInfo); //재료그룹
					
					//주석처리된 정보는 코일공통 테이블에서 가져오도록 쿼리 작성함..
*/
			    	
			    	jrParam.setField("STOCK_ID_"+ii		, commUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_STL_NO"))); 
			    	jrParam.setField("LOAD_LOC_CD_"+ii	, commUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_LOAD_LOC_CD")));
			    	jrParam.setField("WORK_STATE_"+ii	, commUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_WORK_STATE")));
			    	
				}
	
				//차량예정정보 백업 송신
				//jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L008", jrParam));
			    jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L008BackUp", jrParam));
			    
			} else {
				//빈 전문 생성
			    jrParam.setField("PT_LOAD_LOC"      	, ydLoadLoc);   // 상차도 위치				
			    jrParam.setField("CAR_NO"      			, "");  		// 차량번호	
			    jrParam.setField("CARD_NO"      		, ""); 			// 차량번호	
			    jrParam.setField("PT_CLS"       		, "");   		// 차량구분				
			    jrParam.setField("WORK_CLS"      		, "");			// 작업구분  				
			    jrParam.setField("WORK_COIL_MAX_CNT"	, "0");			// 작업총 수량
			    
		    	jrParam.setField("STOCK_ID_0"			, ""); 
		    	jrParam.setField("LOAD_LOC_CD_0"		, "");
		    	jrParam.setField("WORK_STATE_0"			, "");
				
		    	jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L008BackUp", jrParam));
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}		
	/**
	 *      [A] 오퍼레이션명 : B열연 신규모듈 적용여부 리턴 메소드
	 *      
	 * @param  void 
	 * @return JDTORecord
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public JDTORecord getNewModuleEffYn_YM() throws DAOException, JDTOException {
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();	
		JDTORecordSet rsTemp = null;
		
		try {
			//query id setting
			recPara.setField("PARM", "1");
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getNewModuleEffYn 
			/* B열연 신규모듈적용여부

			SELECT 'YM00' AS REPR_CD_GP
			      ,'N'    AS BSLAB_EFF_YN --B열연 SLAB야드 신규모듈 적용여부(Y:적용, N:적용안함)
			      ,'Y'    AS BCOIL_EFF_YN --B열연 COIL야드 신규모듈 적용여부(Y:적용, N:적용안함)
			      ,'Y'    AS PO_EFF_YN    --PO MODEL      신규모듈 적용여부(Y:적용, N:적용안함)
			  FROM DUAL      
			  
			--SELECT REPR_CD_GP
			--      ,MAX(DECODE(CD_GP,'2',ITEM)) AS BSLAB_EFF_YN --B열연 SLAB야드 신규모듈 적용여부(Y:적용, N:적용안함)
			--      ,MAX(DECODE(CD_GP,'3',ITEM)) AS BCOIL_EFF_YN --B열연 COIL야드 신규모듈 적용여부(Y:적용, N:적용안함)
			--  FROM USRYMA.TB_YM_RULE
			-- WHERE REPR_CD_GP = 'YM00'
			-- GROUP BY REPR_CD_GP
			*/
			//query execute
			
			rsTemp = commDao.select(recPara, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getNewModuleEffYn", "A", "COMMON", "적용여부");
			if(rsTemp.size() <= 0) {
				recPara.setField("PO_EFF_YN", "N");
				recPara.setField("BSLAB_EFF_YN", "N");
				recPara.setField("BCOIL_EFF_YN", "N");
			} else {
				recPara.setField("PO_EFF_YN"   , commUtils.nvl(rsTemp.getRecord(0).getFieldString("PO_EFF_YN"   ),"N")); 
				recPara.setField("BSLAB_EFF_YN", commUtils.nvl(rsTemp.getRecord(0).getFieldString("BSLAB_EFF_YN"),"N"));
				recPara.setField("BCOIL_EFF_YN", commUtils.nvl(rsTemp.getRecord(0).getFieldString("BCOIL_EFF_YN"),"N"));
			}
			
		} catch (Exception e) {
			
			recPara.setField("PO_EFF_YN"   , "N");
			recPara.setField("BSLAB_EFF_YN", "N");
			recPara.setField("BCOIL_EFF_YN", "N");
		}
		return recPara;
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 행선코드 get
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getSlabYdAimRtGp(JDTORecord rcvMsg) throws DAOException {	
		String methodNm = "행선코드[YmComm.getSlabYdAimRtGp] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId     = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String TcCode 	 = commUtils.trim(rcvMsg.getFieldString("TC_CD"));	//TC_CD
			String sSTOCK_ID = commUtils.trim(rcvMsg.getFieldString("STOCK_ID"));//재료
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 2. COILCOMM READ
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("SLAB_NO"	, sSTOCK_ID); 
			jrParam.setField("MODIFIER" , modifier); //수정자


			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectSlabMatirialInfo", logId, methodNm, "SlabComm 조회");
			
			String sSTOCK_MOVE_TERM = "";
			
			if (rsResult != null && rsResult.size() > 0) {
				String sCURR_PROG_CD     = commUtils.trim(rsResult.getRecord(0).getFieldString("CURR_PROG_CD"));
				String sWO_MSLAB_RPR_MTD = commUtils.trim(rsResult.getRecord(0).getFieldString("WO_MSLAB_RPR_MTD"));
			   	
		    	/* 일관제철 진도코드 */
		    	if(YmConstant.DMYDR016.equals(TcCode) ){				//외판슬라브운송지시대기
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_NS;
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_0.equals(sCURR_PROG_CD)){    		
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_11; 
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_1.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_12;	
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_A.equals(sCURR_PROG_CD)){
		    		if("Q".equals(sWO_MSLAB_RPR_MTD)){
		        		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_D3;	//핸드스카핑작업대기
		    		}else{
		    			sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_DS;
		    		}
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_B.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ES;
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_C.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_FS;
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_D.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_BS;
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_E.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_CS;
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_F.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_YS;
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_G.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_GS; // 종합판정대기
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_H.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_HS; // 입고대기
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_J.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_JS; // 반납대기
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_K.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_KS;
				}else if(YmConstant.CURR_PROG_CD_SLAB_L.equals(sCURR_PROG_CD)){
			    	sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_LS;
				}else if(YmConstant.CURR_PROG_CD_SLAB_M.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_MS;    		
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_N.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_NS;    	
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_Y.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ZS;    	
		    	}else if(YmConstant.CURR_PROG_CD_SLAB_Z.equals(sCURR_PROG_CD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ZS;
		    	}															
		    	
		    	jrRtn.setResultCode(logId);	//Log ID
		    	jrRtn.setResultMsg(methodNm);	//Log Method Name
		    	jrRtn.setField("CURR_PROG_CD"  	, sCURR_PROG_CD); 	//진도코드
		    	jrRtn.setField("YD_AIM_RT_GP"	, sSTOCK_MOVE_TERM  );	//저장품 이동 조건
			} 

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
	 *      [A] 오퍼레이션명 :  설비작업  여부
	 *      -- 
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public boolean chkEqpIdGp(String szYD_SCH_CD, String szTO_LOC) throws DAOException {
		String methodNm = "설비작업여부 CHECK [YmComm.chkEqpIdGp]" ;
		String logId = "";
		String ydEqpIdYn = "";

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			/**********************************************************
			* 2. 설비정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam("", methodNm, "");
			jrParam.setField("YD_SCH_CD", szYD_SCH_CD); 
			jrParam.setField("TO_LOC"   , szTO_LOC); 

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.geteEquipGpYn 
			WITH PARM_TBL AS 
			(
			SELECT :V_YD_SCH_CD AS YD_SCH_CD 
			     , SUBSTR(:V_TO_LOC,3,2)    AS TO_LOC
			  FROM DUAL 
			)
			SELECT CASE WHEN SUBSTR(YD_SCH_CD,3,6) IN  ('PT02UM')                           THEN 'Y'  --이송상차
			            WHEN SUBSTR(YD_SCH_CD,3,6) IN  ('WB01UM') AND TO_LOC   = 'WB'       THEN 'Y'  --WB보급
			            WHEN SUBSTR(YD_SCH_CD,3,6) IN  ('CT01UM') AND TO_LOC   = 'CT'       THEN 'Y'  --CT보급
			            WHEN SUBSTR(YD_SCH_CD,3,6) IN  ('PT02LM') AND TO_LOC IN ('BK','TC') THEN 'Y'  --이송하차
			            WHEN SUBSTR(YD_SCH_CD,3,6) IN  ('TC01LM','TC02LM','TC03LM') 
			                 AND TO_LOC IN ('BK')                                           THEN 'Y'  --대차하차
			            WHEN SUBSTR(YD_SCH_CD,3,3) IN  ('TC1')                              THEN 'Y'  --동간보급상차
			            WHEN SUBSTR(YD_SCH_CD,3,3) IN  ('TC01UM','TC02UM','TC03UM')         THEN 'Y'  --대차상차
			            ELSE 'N' END AS EQUIP_GP
			  FROM PARM_TBL 
			*/  
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.geteEquipGpYn", logId, methodNm, "설비작업여부"); 

			if (jsChk.size() > 0) {
				ydEqpIdYn    = commUtils.trim(jsChk.getRecord(0).getFieldString("EQUIP_GP"));
			}
			if ("Y".equals(ydEqpIdYn)){
				commUtils.printLog(logId, methodNm, "S-");
				return true;
			} else {
				commUtils.printLog(logId, methodNm, "S-");
				return false;
			}
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return false;
		} catch (Exception e) {
			return false;
		}
	}		
	/**
	 *      [A] 오퍼레이션명 :  고도화 적용 SCH CD
	 *      -- 
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public boolean chkRule(String ydSchCd) throws DAOException {
		String methodNm = "RULE CHECK [YmComm.chkRule]" ;
		String logId = "";

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			/**********************************************************
			* 2. 설비정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam("", methodNm, "");
			jrParam.setField("YD_SCH_CD", ydSchCd); 

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.ChkRule
			SELECT *
			  FROM USRYMA.TB_YM_RULE
			 WHERE REPR_CD_GP = 'APP013'
			   AND CD_GP = '3'
			   AND DTL_ITM1 = SUBSTR(:V_YD_SCH_CD,3,2)
			   AND DEL_YN = 'N'
			*/  
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.ChkRule", logId, methodNm, "대상 SCH 여부"); 

			if (jsChk.size() > 0) {
				commUtils.printLog(logId, methodNm, "S-");
				return true;
			} else {
				commUtils.printLog(logId, methodNm, "S-");
				return false;
			}
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return false;
		} catch (Exception e) {
			return false;
		}
	}		
	
	
	/**
	 *      [A] 오퍼레이션명 : INSERT,UPDATE Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public boolean execQueryId(JDTORecord rcvMsg,String queryId) throws DAOException {
		String methodNm = "INSERT,UPDATE Transaction 분리메소드 호출[BSlabComm.execQueryId] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
	
		try {
			commUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbConn1 = new EJBConnector("default", "YmCommSeEJB", this);
			ejbConn1.trx("execQueryIdTx", new Class[] { JDTORecord.class, String.class }, new Object[] { rcvMsg, queryId });
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	}

	/***********************************
	    PIDEV 개발
	***********************************/
	/**
	 *      [A] 오퍼레이션명 : 진도코드 get PI
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getCoilCurrProgCd_PIDEV(JDTORecord rcvMsg) throws DAOException {	
		String methodNm = "진도코드Check[YmComm.getCoilCurrProgCd_PIDEV] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String TcCode 	= commUtils.trim(rcvMsg.getFieldString("TC_CD"));	//TC_CD
			String infoGp   = commUtils.trim(rcvMsg.getFieldString("INFO_GP"));//정보구분
			String StlNo 	= commUtils.trim(rcvMsg.getFieldString("STOCK_ID"));//재료
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			
			if ("".equals(modifier)) { modifier = msgId; }

			commUtils.printLog(logId, "TcCode:"+ TcCode, "SL");
			/**********************************************************
			* 2. COILCOMM READ
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("COIL_NO"	, StlNo); //충당재료
			jrParam.setField("MODIFIER" , modifier); //수정자

			JDTORecordSet jsStl = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilComByCurrProgCd", logId, methodNm, "CoilComm 조회");
			
			String ydStocMv = "";
			
			if (jsStl != null && jsStl.size() > 0) {
				String CurrProgCd 	= commUtils.trim(jsStl.getRecord(0).getFieldString("CURR_PROG_CD"));//진도코드
				String ReturnGp 	= commUtils.trim(jsStl.getRecord(0).getFieldString("RETURN_GP")); 	//반납구분
			   	
		    	if("M10LMYDJ1021".equals(TcCode)) {			//코일제품반납대기
		    		if(YmConstant.RETURN_GP_1.equals(ReturnGp)) {
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_JR;
		    		}else{
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_JG;
		    		}
		    	} else if(
			    		"M10LMYDJ1011".equals(TcCode) && ("4".equals(infoGp) ||	//코일제품출하지시대기 
			    		"3".equals(infoGp))		//코일제품반품
		    			) {
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_KG;
		    		
		    	} else if(
			    		("M10LMYDJ1011".equals(TcCode) && "2".equals(infoGp))||	//코일제품보관지시 
			    		"M10LMYDJ1071".equals(TcCode)		//코일제품출하완료
		    			) {
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_MG;
		    		
		    	} else if("M10LMYDJ1031".equals(TcCode) ){			//코일제품운송지시
		    		
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_LG;
		    	} else if(YmConstant.CURR_PROG_CD_COIL_A.equals(CurrProgCd)||
		    			 YmConstant.CURR_PROG_CD_COIL_R.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_AC;
		    		
		    	} else if(YmConstant.CURR_PROG_CD_COIL_B.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_BC;
		    		
		    	} else if(YmConstant.CURR_PROG_CD_COIL_C.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_CC;
		    		
		    	} else if(YmConstant.CURR_PROG_CD_COIL_D.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_DC;
		    		
		    	} else if(YmConstant.CURR_PROG_CD_COIL_E.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_CS;
		    		
		    	} else if(YmConstant.CURR_PROG_CD_COIL_F.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_FC;
		    		
		    	} else if(YmConstant.CURR_PROG_CD_COIL_K.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_KG;
		    		
		    	} else if(YmConstant.CURR_PROG_CD_COIL_G.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_GC;
		    		
		    	} else if(YmConstant.CURR_PROG_CD_COIL_H.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_HG;
		    		
		    	} else if(YmConstant.CURR_PROG_CD_COIL_J.equals(CurrProgCd)){
		    		if(YmConstant.RETURN_GP_1.equals(ReturnGp)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_JR;
		    		}else{
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_JG;
		    		}
		    		
		    	} else if(YmConstant.CURR_PROG_CD_COIL_L.equals(CurrProgCd)){//코일제품상차지시 
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_LG;
		    		
		    	} else if(YmConstant.CURR_PROG_CD_COIL_N.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_NG;
		    		
		    	} else if(YmConstant.CURR_PROG_CD_COIL_M.equals(CurrProgCd)||
		    			 YmConstant.CURR_PROG_CD_COIL_P.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_MG;
		    		
		    	} else if(YmConstant.CURR_PROG_CD_COIL_X.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_XG;
		    		
		    	} else if(YmConstant.CURR_PROG_CD_COIL_Y.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_YG;
		    		
		    	} else if(YmConstant.CURR_PROG_CD_COIL_Z.equals(CurrProgCd)){
		    			ydStocMv = YmConstant.NEW_STOCK_MOVE_TERM_ZG;
		    	}															
		    	
		    	jrRtn.setResultCode(logId);	//Log ID
		    	jrRtn.setResultMsg(methodNm);	//Log Method Name
		    	jrRtn.setField("CURR_PROG_CD"  	, CurrProgCd); 	//진도코드
		    	jrRtn.setField("STOCK_MOVE_TERM", ydStocMv  );	//저장품 이동 조건
			} 

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return jrRtn;
		} catch (Exception e) {
			return jrRtn;
		}
	}
}
