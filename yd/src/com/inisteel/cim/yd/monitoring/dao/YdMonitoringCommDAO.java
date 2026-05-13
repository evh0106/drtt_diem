/**
 * @(#)MonitoringCommDAO
 *
 * @version          V1.00
 * @author           신지은
 * @date             2018/07/31
 * 
 * @description      야드 모니터링 공통 DAO
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2018/07/31    신지은      신지은      최초 등록
 */
package com.inisteel.cim.yd.monitoring.dao;

import java.util.Iterator;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;


/**
 * [A] 클래스명 : 야드 모니터링 공통 DAO
 *
 */


public class YdMonitoringCommDAO extends DBAssistantDAO {

	private YdSlabUtils slabUtils = new YdSlabUtils();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	
	
	/**
	 *      [A] 오퍼레이션명 : 유형 별 JMS Log Insert
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int insJMSLog(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "유형 별 JMS Log Insert[MonitoringCommDAO.insJMSLog] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("JMSLog1".equals(trtGp)) {
				trtNm = "EnQ 성공 후 3분 이상 DeQ가 되지 않는 전문 내역 Insert";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.monitoring.dao.MonitoringCommDAO.insJMSLog1";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_DOMAIN"))
					};
			} else if ("JMSLog2".equals(trtGp)) {
				trtNm = "DeQ 실패 전문 내역 (2분전~현재)Insert";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.monitoring.dao.MonitoringCommDAO.insJMSLog2";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_DOMAIN"))
					};
			} else if ("JMSLog3".equals(trtGp)) {
				trtNm = "DeQ 성공 전문 중 10분(EJB수행시간 기준) 이상 소요된 전문 내역 (2분전~현재)Insert";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.monitoring.dao.MonitoringCommDAO.insJMSLog3";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_DOMAIN"))
					}; 
			}  else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";
			
			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt, "DB");

			return trtCnt;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 유형 별 EAI Log Insert
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int insEAILog(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "유형 별 EAI Log Insert[MonitoringCommDAO.insEAILog] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("EAILog1".equals(trtGp)) {
				trtNm = "EAI 실패 전문 내역 Insert";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.monitoring.dao.MonitoringCommDAO.insEAILog1";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_GROUP_NAME"))
					   ,slabUtils.trim(jrParam.getFieldString("V_IF_ID"))
					   ,slabUtils.trim(jrParam.getFieldString("V_IF_NAME"))
					   ,slabUtils.trim(jrParam.getFieldString("V_IF_OCCUR_TIME"))
					   ,slabUtils.trim(jrParam.getFieldString("V_ELAPSED"))
					   ,slabUtils.trim(jrParam.getFieldString("V_ERROR_CONTENT"))
					};
			} else if ("EAILog2".equals(trtGp)) {
				trtNm = "EAI 성공했지만 수행시간 긴 전문 내역 Insert";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.monitoring.dao.MonitoringCommDAO.insEAILog2";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_GROUP_NAME"))
					   ,slabUtils.trim(jrParam.getFieldString("V_IF_ID"))
					   ,slabUtils.trim(jrParam.getFieldString("V_IF_NAME"))
					   ,slabUtils.trim(jrParam.getFieldString("V_IF_OCCUR_TIME"))
					   ,slabUtils.trim(jrParam.getFieldString("V_ELAPSED"))
					   ,slabUtils.trim(jrParam.getFieldString("V_ERROR_CONTENT"))
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";
			
			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt, "DB");

			return trtCnt;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : I/F LOG Update
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updIFLog(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "I/F LOG Update[MonitoringCommDAO.updIFLog] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("updSMSList".equals(trtGp)) {
				trtNm = "TB_YD_IFLOG SMS 전송 대상 표시";
				jspeed_query_id = "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.updSMSSendList";
				param = new Object[] {
					   slabUtils.trim(jrParam.getFieldString("V_DOMAIN"))
					};
			} else if("updSMSLogId".equals(trtGp)) {
				trtNm = "TB_YD_IFLOG SMS LOG ID 저장";
				jspeed_query_id = "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.updSMSLogId";
				param = new Object[] {
					   slabUtils.trim(jrParam.getFieldString("V_DOMAIN"))
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";
			
			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt, "DB");

			return trtCnt;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : SMS LOG Insert/Update
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int trtSMSLog(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "SMS LOG Insert/Update[MonitoringCommDAO.trtSMSLog] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("insSMSLog".equals(trtGp)) {
				trtNm = "SMS LOG Insert";
				jspeed_query_id = "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.insSMSLog";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_PROGRAM"))
					   ,slabUtils.trim(jrParam.getFieldString("V_DOMAIN"))
					};
			} else if("updSMSLog".equals(trtGp)){
				trtNm = "SMS LOG Update";
				jspeed_query_id = "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.updSMSLog";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_MESSAGE_LOG_ID"))
					   ,slabUtils.trim(jrParam.getFieldString("V_DOMAIN_GP"))
					   ,slabUtils.trim(jrParam.getFieldString("V_MESSAGE_LOG_ID"))
					   ,slabUtils.trim(jrParam.getFieldString("V_DOMAIN_GP"))
					};
			} else if("updDelYn".equals(trtGp)){
				trtNm = "SMS LOG DEL_YN 수정";
				jspeed_query_id = "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.updSMSLogDelYn";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_MESSAGE_LOG_ID"))
					   ,slabUtils.trim(jrParam.getFieldString("DOMAIN"))
					};
			} else if("updSMSLogCnt".equals(trtGp)){
				trtNm = "SMS LOG Count 증가";
				jspeed_query_id = "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.updSMSLogCnt";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_MESSAGE_LOG_ID"))
					   ,slabUtils.trim(jrParam.getFieldString("V_DOMAIN_GP"))
					   ,slabUtils.trim(jrParam.getFieldString("V_MESSAGE_LOG_ID"))
					   ,slabUtils.trim(jrParam.getFieldString("V_DOMAIN_GP"))
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";
			
			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt, "DB");

			return trtCnt;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : SELECT 메소드
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         String        queryId    QueryId 
	 *         String     	 logId   	 
	 *         String     	 mthdNm   	 
	 *         String     	 trtNm   	 
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public JDTORecordSet selectEAI(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
		
		String methodNm = trtNm + "[YmCommDAO.select] < " + mthdNm;
		
		JDTORecord recPara = null;
		JDTORecordSet rsTemp =  null;
		
		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			rsTemp = getRecordSet(recPara);
			
			slabUtils.printLog(logId, trtNm + "[YmCommDAO.select] 결과 건수: " + rsTemp.size() , "DB");
			
			return rsTemp;
			
		} catch (Exception e) {
			return rsTemp;
		}
		
	}	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : conversionFieldname 
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         int intGp             // 구분(0:"V_" 추가, 1:"V_" 제거
	 * @return JDTORecord			 // 필드명을 변환한 결과레코드
	 * @throws JDTOException 
	 */
	public JDTORecord conversionFieldname(JDTORecord recPara, int intGp) throws JDTOException {
		JDTORecord recRtnVal = JDTORecordFactory.getInstance().create();
		String szFieldName = null;
		Iterator itrFieldName = null;
		
		//필드명을 가져온다.
		itrFieldName = recPara.iterateName();
		
		//필드명 갯수만큼 루프를 돈다.
		while(itrFieldName.hasNext()) {
			
			szFieldName = (String)itrFieldName.next();
			//"V_" 추가
			if (intGp == 0) {
				recRtnVal.setField("V_" + szFieldName, recPara.getField(szFieldName));
			//"V_" 제거
			} else {
				recRtnVal.setField(szFieldName.substring(2), recPara.getField(szFieldName));
			}
		}
		
		return recRtnVal ;
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : Jsp 화면용 SELECT 메소드 
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         String        queryId    QueryId 
	 *         String        logId   	 
	 *         String        mthdNm   	 
	 * @return int           result size
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int jspSelect(JDTORecord inRec, JDTORecordSet outRecSet, String queryId, String logId, String mthdNm) throws DAOException, JDTOException {
		
		String methodNm = "조회[YmCommDAO.jspSelect] < " + mthdNm;
		
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
		
		try {

//PIDEV
//			queryId = ydPICommDAO.getYdRulePI("", mthdNm, "YD0001", queryId, "APPPI0", "*", "*" );

			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			rsTemp = getRecordSet(recPara);
			
			slabUtils.printLog(logId, "조회[YmCommDAO.jspSelect] 결과 건수: " + rsTemp.size() , "DB");
			
			if (rsTemp.size() > 0) {
				outRecSet.addAll(rsTemp);
			} else {
				return 0;
			}
			
		} catch (Exception e) {
			
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
		return outRecSet.size();
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : SMS USER Update
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updSMSUserInfo(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "SMS USER Update[MonitoringCommDAO.updSMSUserInfo] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("smsUser".equals(trtGp)) {
				trtNm = "SMS User update";
				jspeed_query_id = "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.updSMSUser";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_HANDPHONE_NO"))
					   ,slabUtils.trim(jrParam.getFieldString("V_RECV_YN"))
					   ,slabUtils.trim(jrParam.getFieldString("V_MAX_RECV_COUNT"))
					   ,slabUtils.trim(jrParam.getFieldString("V_USER_ID"))
					   ,slabUtils.trim(jrParam.getFieldString("V_DOMAIN_GP"))
					   ,slabUtils.trim(jrParam.getFieldString("V_USER_ID"))
					};
			} else if ("smsUserDel".equals(trtGp)) {
				trtNm = "SMS User update";
				jspeed_query_id = "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.delSMSUser";
				param = new Object[] {
					   slabUtils.trim(jrParam.getFieldString("V_USER_ID"))
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";
			
			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt, "DB");

			return trtCnt;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	/**
	 *      [A] 오퍼레이션명 : SMS USER Update
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int insSMSUserInfo(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "SMS USER Insert[MonitoringCommDAO.updSMSUserInfo] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("smsUser".equals(trtGp)) {
				trtNm = "SMS User update";
				jspeed_query_id = "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.insSMSUser";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_USER_ID"))
						,slabUtils.trim(jrParam.getFieldString("V_USER_NAME"))
						,slabUtils.trim(jrParam.getFieldString("V_HANDPHONE_NO"))
					    ,slabUtils.trim(jrParam.getFieldString("V_REGISTER"))
					    ,slabUtils.trim(jrParam.getFieldString("V_REGISTER"))
					   ,slabUtils.trim(jrParam.getFieldString("V_RECV_YN"))
					   ,slabUtils.trim(jrParam.getFieldString("V_MAX_RECV_COUNT"))
					   ,slabUtils.trim(jrParam.getFieldString("V_DOMAIN_GP"))

					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";
			
			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt, "DB");

			return trtCnt;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 에러 기준 Update
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updErrorControl(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "에러 기준 Update[MonitoringCommDAO.updErrorControl] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("control".equals(trtGp)) {
				trtNm = "에러 기준 Update";
				jspeed_query_id = "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.updErrorControl";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_ITEM1"))
					   ,slabUtils.trim(jrParam.getFieldString("V_USER_ID"))
					   ,slabUtils.trim(jrParam.getFieldString("V_DOMAIN_GP"))
					   ,slabUtils.trim(jrParam.getFieldString("V_ITEM"))
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";
			
			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt, "DB");

			return trtCnt;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
}
