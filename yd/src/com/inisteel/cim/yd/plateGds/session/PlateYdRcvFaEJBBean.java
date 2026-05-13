/**
 * @(#)PlateYdRcvFaEJBBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2021/01/06
 * 
 * @description      후판제품야드 자동화 크레인 L2 수신 처리 Facade EJB클래스
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2021/01/06   			윤재광       김광철      최초 등록
 * 
 */
package com.inisteel.cim.yd.plateGds.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.StringUtils;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 *      [A] 클래스명 :  후판제품야드 자동화 크레인 L2 IF 수신 분기처리 Class
 *
 * @ejb.bean name="PlateYdRcvFaEJB" jndi-name="PlateYdRcvFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
*/

public class PlateYdRcvFaEJBBean extends BaseSessionBean 
{
	private static final long serialVersionUID = 1L;
	private Logger logger = new Logger("yd");
	
	// Session Name 
	private String szSessionName = getClass().getName(); 
	
	private YdUtils        ydUtils        	= new YdUtils();
	private YdSlabUtils        ydSlabUtils        	= new YdSlabUtils();
	
	private YdDelegate   ydDelegate =new YdDelegate();
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException 
	{
		
	}
	
    
	/**
	 * @(#)클래스 이름
	 *                   rcvInterface
	 *
	 * @description      클래스 설명
	 *                   L2전문을 수신하여 해당 전문의 처리 메서드를 호출하는 메인
	 *
	 * @ejb.interface-method EJB FA 메소드 생성하는 태그입니다.
	 * @param  JDTORecord    indo
	 * @return void
	 * @throws DAOException
	 */	
	public void rcvInterface(JDTORecord into) throws DAOException 
	{
		String szMsg="";
		String sMSG_ID = "";
		String sUniqueId = "";
		String logId = ydSlabUtils.getLogId();
		String szMethodName = "수신[PlateYdRcvFaEJB.rcvInterface]";
		try
		{
			sUniqueId = StringUtils.trim(into.getFieldString("UNIQUE_ID"));
			if(sUniqueId == null || "".equals(sUniqueId)){
				sUniqueId = logId;
			}else{
				logId = sUniqueId;
			}

			ydSlabUtils.printLog(logId, "I/F" + szMethodName, "I+");
			
			//1. TC ID 얻어오기 
			sMSG_ID =  ydUtils.getTcCode(into);
			
			ydSlabUtils.printLog(logId, szMethodName, "S+"); 
			ydUtils.displayRecord("수신전문ID :: " + sMSG_ID, into);
			ydSlabUtils.printLog(logId, szMethodName, "S+"); 
			
			//2. 전문ID가 없을경우 에러처리
			if ("".equals(sMSG_ID)) {
				szMsg = sUniqueId + "[" + szMethodName + "] " + "수신된 전문의 IF ID가 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new Exception(szMsg);
			}
			
			into.setResultCode(sUniqueId);
			into.setResultMsg(szMethodName);
			
			//3. 메소드 호출
			EJBConnector ejbConn = null;
			if(sMSG_ID.startsWith("DM") || sMSG_ID.startsWith("YDYDJ")){
				ejbConn = new EJBConnector("default", "PlateYdRcvFaEJB", this); 
				ejbConn.trx("rcv" + sMSG_ID,   new Class[] { JDTORecord.class }, new Object[] { into });
			}else{
				ejbConn = new EJBConnector("default", "PlateYdRcvL2SeEJB", this); 
				JDTORecord sndData = (JDTORecord) ejbConn.trx("rcv" + sMSG_ID,   new Class[] { JDTORecord.class }, new Object[] { into });
				
				//4. 전문송신
				// 후판제품 경우 수신측 java에서 전문을 전송처리한다.
				//5. 전문 전송
				if (sndData != null) 
				{
					sndData.setResultCode(logId);
					sndData.setResultMsg(szMethodName);
					
					
					JDTORecordSet sndMsgSet = (JDTORecordSet)sndData.getField("SEND_DATA");
					if (sndMsgSet == null || sndMsgSet.size() <= 0) 
					{
						ydSlabUtils.printLog("", ydSlabUtils.makeErrorLog(logId, szMethodName, "전송할 Data가 존재하지 않습니다 ."), "IS");
						return;
					}
					
					int sndCnt = sndMsgSet.size(); //전송Data 건수
					String msgId = "";
					JDTORecord jtoSendMsg = null;
					
					//같은 IF ID 끼리 정리
					for (int i = 0; i < sndCnt; i++)
					{
						jtoSendMsg = sndMsgSet.getRecord(i);
						//EAI, JMS, HTTP(출하관리 등) 송신 전문 IF ID
						msgId = ydSlabUtils.getMsgId(jtoSendMsg);
						if (!"".equals(msgId) && ((msgId.length() == 8)||(msgId.length() == 7))){
							ydSlabUtils.printLog("", ydSlabUtils.makeErrorLog(logId, szMethodName, "Start Send Msg :: " + msgId), "IS");
							ydDelegate.sendMsg(jtoSendMsg);
							ydSlabUtils.printLog("", ydSlabUtils.makeErrorLog(logId, szMethodName, "END Send Msg :: " + msgId), "IS");
						}
					}
				}
			}
			
		}
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{ 
			throw new DAOException(ydSlabUtils.makeErrorLog(logId, szMethodName, e));
		}
	} 
	
	/**
	 * 오퍼레이션명 : 후판용 제품운송상차지시(DMYDR060) - (출하고도화)
	 *  -  원본소스 :  com.inisteel.cim.yd.ydStock.RouteModReg.RtModRegFaEJBBean.rcvCoilGdsTrnOrd(JDTORecord)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord inRecord
	 *      @throws DAOException   
	 */ 
	public void rcvDMYDR060(JDTORecord inRecord) throws DAOException  {
		// 신규
		// YD-UC-1320 코일제품운송지시등록,제품운송상차지시등록
		// TC : DMYDR020,DMYDR060
		// 출하관리시스템으로부터 코일제품운송지시실적 수신
		//
		//┏━┓
		//┃출하관리에서 코일제품 운송지시를 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛
		
		String methodNm = "후판용 제품운송상차지시(DMYDR060)[PlateYdRcvFaEJB.rcvDMYDR060] < " + inRecord.getResultMsg();
		String logId = inRecord.getResultCode();
		ydSlabUtils.printLog(logId, methodNm, "F+");
		try {
			
			// 통합후판제품운송상차지시등록
//			ydEjbCon.trx("RtModRegSeEJB", "procPlateGdsTrnOrd3GUpGrade", inRecord);
			EJBConnector ejbConn = new EJBConnector("default", "RtModRegSeEJB", this);  
			ejbConn.trx("procPlateGdsTrnOrd4GUpGrade",   new Class[] { JDTORecord.class }, new Object[] { inRecord });
		 
			ydSlabUtils.printLog(logId, methodNm, "F-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
		}	
	 } // end of rcvDMYDR060()
	
	
	/**
	 * 오퍼레이션명 : 후판용 대기장도착실적(DMYDR061) - (출하고도화)
	 *  -  원본소스 : com.inisteel.cim.yd.ydStock.RouteModReg.RtModRegFaEJBBean.rcvStandByYdArrive(JDTORecord)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord inRecord
	 *      @throws DAOException   
	 */ 
	public void rcvDMYDR061(JDTORecord inRecord) throws DAOException  {
		// 신규
		// YD-UC-1321 대기장도착실적
		// TC : DMYDR061
		// 출하관리시스템으로부터 대기장도착실적 수신
		
		String logId = inRecord.getResultCode();
		String methodNm = "후판용 대기장도착실적(DMYDR061)[PlateYdRcvFaEJB.rcvDMYDR061] < " + inRecord.getResultMsg();
		ydSlabUtils.printLog(logId, methodNm, "F+");

		try {
			
			// 대기장도착실적
//			ydEjbCon.trx("RtModRegSeEJB", "procStandByYdArrivePlate", inRecord);
			EJBConnector ejbConn = new EJBConnector("default", "RtModRegSeEJB", this);  
			ejbConn.trx("procStandByYdArrivePlate4G",   new Class[] { JDTORecord.class }, new Object[] { inRecord });
	 
			ydSlabUtils.printLog(logId, methodNm, "F-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
		}		
		
	 } // end of rcvDMYDR061()

	
	/**
	 * 오퍼레이션명 : 후판제품입고차량도착실적(YDYDJ661) - (출하고도화) 
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord inRecord
	 *      @throws DAOException  
	 *       
	 */ 
	public void rcvYDYDJ661(JDTORecord inRecord) throws DAOException  {
		// 신규
		// YD-UC-1321 대기장도착실적
		// TC : DMYDR061
		// 출하관리시스템으로부터 대기장도착실적 수신
		
		String logId = inRecord.getResultCode();
		String methodNm = "후판제품입고차량도착실적(YDYDJ661)[PlateYdRcvFaEJB.rcvYDYDJ661] < " + inRecord.getResultMsg();
		ydSlabUtils.printLog(logId, methodNm, "F+");

		try {
			// 후판제품입고차량도착실적
			EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);  
			ejbConn.trx("procPlGdsRcptCarArrWr4G",   new Class[] { JDTORecord.class }, new Object[] { inRecord });
	 
			ydSlabUtils.printLog(logId, methodNm, "F-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
		}		
		
	 } // end of rcvDMYDR061()	
	/**
	 * 오퍼레이션명 : 입고가적베드이적작업(YDYDJ557) - (출하고도화) 
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord inRecord
	 *      @throws DAOException  
	 *       
	 */ 
	public void rcvYDYDJ557(JDTORecord inRecord) throws DAOException  {
		// 신규
		// 입고가적베드이적작업(YDYDJ557)
		// TC : YDYDJ557
		// 입고가적베드 이적작업 스케쥴 생성 및 기동
		// MESSAGE PARAM : YD_EQP_ID
		String logId = inRecord.getResultCode();
		String methodNm = "입고가적베드이적작업(YDYDJ557)[PlateYdRcvFaEJB.rcvYDYDJ557] < " + inRecord.getResultMsg();
		ydSlabUtils.printLog(logId, methodNm, "F+");

		try {
			// 후판제품입고차량도착실적
			EJBConnector ejbConn = new EJBConnector("default", "RcptWrkDmdSeEJB", this);  
			JDTORecord sndData = (JDTORecord)ejbConn.trx("procRcptTempToPlanStrMove",   new Class[] { JDTORecord.class }, new Object[] { inRecord });
 
			if (sndData != null) 
			{
				sndData.setResultCode(logId);
				sndData.setResultMsg(methodNm);
				
				JDTORecordSet sndMsgSet = (JDTORecordSet)sndData.getField("SEND_DATA");
				if (sndMsgSet == null || sndMsgSet.size() <= 0) 
				{
					ydSlabUtils.printLog("", ydSlabUtils.makeErrorLog(logId, methodNm, "전송할 Data가 존재하지 않습니다 ."), "IS");
					return;
				}
				
				int sndCnt = sndMsgSet.size(); //전송Data 건수
				String msgId = "";
				JDTORecord jtoSendMsg = null;
				
				//같은 IF ID 끼리 정리
				for (int i = 0; i < sndCnt; i++)
				{
					jtoSendMsg = sndMsgSet.getRecord(i);
					//EAI, JMS, HTTP(출하관리 등) 송신 전문 IF ID
					msgId = ydSlabUtils.getMsgId(jtoSendMsg);
					if (!"".equals(msgId) && ((msgId.length() == 8)||(msgId.length() == 7))){
						ydSlabUtils.printLog("", ydSlabUtils.makeErrorLog(logId, methodNm, "Start Send Msg :: " + msgId), "IS");
						ydDelegate.sendMsg(jtoSendMsg);
						ydSlabUtils.printLog("", ydSlabUtils.makeErrorLog(logId, methodNm, "END Send Msg :: " + msgId), "IS");
					}
				}
			}
			
			ydSlabUtils.printLog(logId, methodNm, "F-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
		}		
		
	 } // end of rcvYDYDJ557()	
} 
