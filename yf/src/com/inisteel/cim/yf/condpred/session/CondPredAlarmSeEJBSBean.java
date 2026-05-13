/**
 * @(#)CondPredAlarmSeEJBSBean
 *
 * @version		V1.00
 * @author		현대제철
 * @date		2025/04/17
 *
 * @description	열연 결로 예측(Condensation Prediction) 시스템 알람 session EJB
 * 
 * -------------------------------------------------------------------------------
 * Ver.		수정일자	요청자	수정자	내용
 * =======	==========	======	======	==========================================
 * V1.00	2025/04/17	정종균	양태호	최초 등록
 * 
 */
package com.inisteel.cim.yf.condpred.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yf.message.MessageSenderTalk;
import com.inisteel.cim.yf.common.YfCommUtils;
import com.inisteel.cim.yf.common.YfConstant;
import com.inisteel.cim.yf.common.dao.YfCommDAO;
import com.inisteel.cim.yf.condpred.CondPredQueryIF;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;


/**
 * [A] 클래스명 : 열연 결로 예측(Condensation Prediction) 시스템 알람 session EJB
 *
 * @ejb.bean name="CondPredAlarmSeEJB" jndi-name="CondPredAlarmSeEJB" type="Stateless" view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
 */
public class CondPredAlarmSeEJBSBean extends BaseSessionBean implements CondPredQueryIF {
	private YfCommDAO commDao = new YfCommDAO();
	private YfCommUtils commUtils = new YfCommUtils();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * [A] 오퍼레이션명 : 열연 결로 예측 시스템 알람 메시지(알림톡) 발송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 *            gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord sendAlarmMsg(JDTORecord gdReq) throws DAOException {
		String methodNm = "열연 결로 알람 메시지(알림톡) 발송 [CondPredAlarmSeEJB.sendAlarmMsg] < " + gdReq.getResultMsg();
		gdReq.setResultCode(commUtils.getLogId(YfConstant.YD_GP_1));
		String logId = gdReq.getResultCode();

		JDTORecordSet smsListSet;

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getFieldString("userid")));
			/**********************************************************
			 * 1. SMS 전송 목록 조회
			 **********************************************************/
			jrParam.setResultCode(logId); 		// Log ID
			jrParam.setResultMsg(methodNm); 	// Log Method Name
			jrParam.setField("SND_STS", "S");
			smsListSet = commDao.select(jrParam, selAlarmMsgList, logId, methodNm, "열연 결로 알람 메시지 목록 조회");

			/**********************************************************
			 * 2. SMS 전송
			 **********************************************************/
			if (smsListSet != null && smsListSet.size() > 0) {
				for (int i = 0; i < smsListSet.size(); i++) {
					JDTORecord recPara1 = JDTORecordFactory.getInstance().create();

					MessageSenderTalk sender = new MessageSenderTalk();
					
					commUtils.printParam("열연 결로 알람 전송-" + i, smsListSet.getRecord(i));
					
					String seq = commUtils.nvl(smsListSet.getRecord(i).getFieldString("SEQ"), "");
					String yd_gp = commUtils.nvl(smsListSet.getRecord(i).getFieldString("YD_GP"), "");
					String yd_gp_nm = (yd_gp.equals("1")) ? "박판" : (yd_gp.equals("3")) ? "1열연" : (yd_gp.equals("J")) ? "2열연" : "열연";
					String send_subject = yd_gp_nm;
					String send_content = commUtils.nvl(smsListSet.getRecord(i).getFieldString("SEND_CONTENT"), "");
					String recv_id = commUtils.nvl(smsListSet.getRecord(i).getFieldString("RECV_ID"), "");
					String phone_num = commUtils.nvl(smsListSet.getRecord(i).getFieldString("PHONE_NUM"), "");

					recPara1 = JDTORecordFactory.getInstance().create();
					recPara1.setField("PHONE_NUM", phone_num);
					recPara1.setField("TMPL_CD", new String("CM1"));
					recPara1.setField("SND_MSG", new String("[현대제철 공지사항]-[" + send_subject + "]" + '\n' + send_content));
					recPara1.setField("SUBJECT", send_subject);
					recPara1.setField("SMS_SND_NUM", new String(phone_num));
					recPara1.setField("RECV_ID", recv_id);
					recPara1.setField("GROUP_ID", "CondPredAlarmSeEJB");
					recPara1.setField("PROGRAM_ID", "sendAlarmMsg");
					sender.sendTalk(recPara1);

					JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
					recPara2.setResultCode(logId); 		// Log ID
					recPara2.setResultMsg(methodNm); 	// Log Method Name
					recPara2.setField("SND_STS", "C");
					recPara2.setField("MODIFIER", "usryfa");
					recPara2.setField("SEQ", seq);
					commDao.update(recPara2, udtAlarmMsgList, logId, methodNm, "열연 결로 알람 메시지 상태 변경");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

}
