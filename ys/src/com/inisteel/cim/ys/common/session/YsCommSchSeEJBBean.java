/**
 * @(#)YsCommSchSeEJBBean
 *
 * @version          V1.00
 * @author           송정현
 * @date             2015/12/22
 *
 * @description      공통  관리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.common.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.util.YsCommUtils;
/**
 *      [A] 클래스명 : 공통관리 Session EJB 
 *
 * @ejb.bean name="YsCommSchSeEJB" jndi-name="YsCommSchSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class YsCommSchSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	

	/**
	 *      [A] 오퍼레이션명 : 크레인작업관리 작업취소 --> 차량도착전문이 수정인 경우만 사용함
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procCraneWrkCancel(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업관리 작업취소[YsCommSchSeEJB.procCraneWrkCancel] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = null;

		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			
			commUtils.printParam(logId + "크레인작업관리 작업취소 기동 ", rcvMsg);

			if(commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )).equals("")) {
				throw new Exception("크레인 스케줄ID 없음");
			}
		
			jrRtn = JDTORecordFactory.getInstance().create();
			//운송장비코드, 개소코드, 상하차구분코드, 포인트요구일시
			jrRtn.setField("YD_WBOOK_ID"  , commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"     )));
			jrRtn.setField("YD_CRN_SCH_ID", commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )));
			jrRtn.setField("YD_EQP_ID"    , commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )));
			jrRtn.setField("YD_SCH_CD"    , commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )));	    	

			/**********************************************************
			* 1. 크레인스케줄 취소
			**********************************************************/
		    this.trtCrnSchCncl(rcvMsg); 

			/**********************************************************
			* 2. 작업예약 취소
			**********************************************************/
		    this.trtWrkBookCncl(rcvMsg);

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 취소처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnSchCncl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인스케줄 취소처리[YsCommSchSeEJB.trtCrnSchCncl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydCrnSchId = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); //야드크레인스케쥴ID
			String ydWbookId  = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"  )); //야드작업예약ID
			
			if ("".equals(ydCrnSchId)) {
				throw new Exception("크레인스케쥴ID가 없습니다.");
			} else if ("".equals(ydWbookId)) {
				throw new Exception("작업예약ID가 없습니다.");
			}

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
			jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
			
			/**********************************************************
			* 1. 크레인스케쥴 정보 Check
			**********************************************************/
//			com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWrkMgtSCSch
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWrkMgtSCSch", logId, methodNm, "크레인작업지시read");
			if (jsCrnSch == null || jsCrnSch.size() <= 0) {
				throw new Exception("크레인스케쥴ID[" + ydCrnSchId + "]의 크레인스케줄 정보가 존재하지 않습니다.");
		    }
			
			JDTORecord jrCrnSch = jsCrnSch.getRecord(0);
			
		    String ydWrkProgStat = commUtils.trim(jrCrnSch.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
		    String eqpUpdYn      = commUtils.trim(jrCrnSch.getFieldString("EQP_UPD_YN"      )); //설비상태수정여부
		    String ydEqpId       = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"       )); //야드설비ID
		    String ydEqpStat     = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_STAT"     )); //야드설비상태

			if ("2".equals(ydWrkProgStat)) {
				throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [2:권상완료]이므로 취소하실 수 없습니다.");
			} else if ("3".equals(ydWrkProgStat)) {
				throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [3:권하지시]이므로 취소하실 수 없습니다.");
			} else if ("4".equals(ydWrkProgStat)) {
				throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [4:권하완료]이므로 취소하실 수 없습니다.");
			}
			
			/**********************************************************
			* 2. 작업진행상태가 [1:권상지시] 이면 작업지시취소 전문 전송
			**********************************************************/
			if ("1".equals(ydWrkProgStat)) {
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
				jrParam.setField("MSG_GP"       , "D"       ); //전문구분(취소)

				//크레인작업지시(YDY1L004, YDY3L004) 전문 조회
				String szJMS_TC_CD = "";
//				String szYdGpBay = ydEqpId.substring(0,2);

		    	if(ydEqpId.startsWith("B") ){	    		
		    		szJMS_TC_CD = "YSN1L003";
		    	}else if(ydEqpId.startsWith("C")){
		    		szJMS_TC_CD = "YSN2L003";
		    	}else if(ydEqpId.startsWith("KACRA2")){
		    		szJMS_TC_CD = "YSN4L003";
		    	}else if(ydEqpId.startsWith("KATC")){
		    		szJMS_TC_CD = "YSN4L003";
				}else if(ydEqpId.startsWith("KA")){
		    		szJMS_TC_CD = "YSN6L003";
		    	}else if(ydEqpId.startsWith("KB")){
		    		szJMS_TC_CD = "YSN4L003";
		    	}else if(ydEqpId.startsWith("KD")){
		    		szJMS_TC_CD = "YSN5L003";
		    	}else if(ydEqpId.startsWith("KE")){
		    		szJMS_TC_CD = "YSN3L003";
		    	}			

				
				jrRtn = commUtils.addSndData(commDao.getMsgL2(szJMS_TC_CD, jrParam));
			}

			/**********************************************************
			* 3. 권상, 권하위치 원복 - 적치단, 적치Bed
			**********************************************************/
			//적치단 수정 - 권상위치(U -> C), 권하위치(D -> E)
			commDao.update(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.updCrnWrkMgtSCStkLyr", logId, methodNm, "TB_YS_STKLYR");				
			
			//적치Bed 수정 - 완산Bed 해제
//			commDao.update(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.updCrnWrkMgtSCStkBed", logId, methodNm, "TB_YS_STKBED");				
			
			/**********************************************************
			* 4. 크레인스케줄 삭제
			**********************************************************/
			//크레인작업재료 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtSCCrnMtl", logId, methodNm, "TB_YS_CRNWRKMTL");				
			
			//크레인스케줄 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtSCCrnSch", logId, methodNm, "TB_YS_CRNSCH");				

			/**********************************************************
			* 5. 설비상태 수정 - 크레인이 고장 또는 Off-Line이 아니고 상태가 다르면
			**********************************************************/
			if ("Y".equals(eqpUpdYn)) {
				jrParam.setField("YD_EQP_ID"  , ydEqpId  ); //야드설비ID
				jrParam.setField("YD_EQP_STAT", ydEqpStat); //야드설비상태

				commDao.update(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.updStatEqp", logId, methodNm, "TB_YD_EQP");				
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
	 *      [A] 오퍼레이션명 : 작업예약 취소처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtWrkBookCncl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "작업예약 취소처리[YsCommSchSeEJB.trtWrkBookCncl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydCrnSchId= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"  )); //야드설비ID
			String ydWbookId = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID")); //야드작업예약ID
		    String ydEqpId   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"  )); //야드설비ID
		    String ydSchCd   = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER" )); //수정자
			
			if ("".equals(ydWbookId)) {
				throw new Exception("작업예약ID가 없습니다.");
			}

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);

			jrParam.setField("YD_WBOOK_ID", ydWbookId);
			
			/**********************************************************
			* 1. 크레인스케줄 존재여부 Check
			**********************************************************/

			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCommWbCrnSch 
			--작업예약 크레인스케줄조회 - 
			SELECT YD_CRN_SCH_ID
			  FROM TB_YS_CRNSCH
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND DEL_YN      = 'N'
			*/	   
				   
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCommWbCrnSch", logId, methodNm, "크레인작업지시read");
			if (jsCrnSch != null && jsCrnSch.size() > 0) {				
				throw new Exception("작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 " + jsCrnSch.size() + " 건 존재합니다.");
		    }
			
			/**********************************************************
			* 2. 준비스케줄 복원
			**********************************************************/
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepMtlRcvr 
			--준비재료 복원 - 
			UPDATE TB_YS_PREPMTL
			   SET MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,DEL_YN   = 'N'
			 WHERE YD_PREP_SCH_ID IN
			      (SELECT YD_PREP_SCH_ID
			         FROM TB_YS_PREPSCH
			        WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID)
			*/
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepMtlRcvr", logId, methodNm, "TB_YS_PREPMTL");	

			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepSchRcvr 
			--준비스케줄 복원 - 
			UPDATE TB_YS_PREPSCH
			   SET MODIFIER    = :V_MODIFIER
			      ,MOD_DDTT    = SYSDATE
			      ,DEL_YN      = 'N'
			      ,YD_WBOOK_ID = NULL
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			*/ 
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepSchRcvr", logId, methodNm, "TB_YS_PREPSCH");	
//			//준비스케줄 복원
//			//준비재료 복원
//			//com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCommPrepMtlRcvr
//		    jspDao.updComm("PrepMtlRcvr", jrParam);
//			
//			//준비스케줄 복원
//		    //com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCommPrepSchRcvr
//		    jspDao.updComm("PrepSchRcvr", jrParam);

			/**********************************************************
			* 3. 차량/대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			**********************************************************/
			//차량스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommCarSchWbDel", logId, methodNm, "TB_YS_CARSCH");				
		
			//대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommTcarSchWbDel", logId, methodNm, "TB_YS_TCARSCH");				

		    /**********************************************************
			* 4. 작업예약/재료 삭제
			**********************************************************/
			//작업예약재료 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");				

			//작업예약 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBook", logId, methodNm, "TB_YS_WRKBOOK");				
			
			/**********************************************************
			* 5. 크레인작업지시요구 전문 조회
			**********************************************************/
			//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			
			String szYdGp = ydSchCd.substring(0,2);	// 스케줄 코드에서 야드동구분을 가져옴
			commUtils.printLog(logId, methodNm+ "야드구분[" + szYdGp + "]", "SL");
			
			jrYdMsg.setField("JMS_TC_CD"       , "YSYSJ001");	//크레인작업지시요구
			jrYdMsg.setField("YD_EQP_ID"       , ydEqpId   );	//야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       );	//야드작업진행상태(권하완료)
			jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   );	//야드스케쥴코드
			jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID
			
			JDTORecord jrRtn1 = commUtils.addSndData(jrYdMsg);
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn1;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

}	