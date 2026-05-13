package com.inisteel.cim.yd.jsp.common.session;

import java.util.List;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jsp.common.YDComUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;

import flex.messaging.MessageBroker;
import flex.messaging.io.ArrayList;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.util.UUIDUtils;

/**
 * [A] 클래스명 : 야드 JSP 화면 공통  
 * 
 * @ejb.bean name="YdJspCommonFaEJB" jndi-name="YdJspCommonFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class YdJspCommonFaEJBBean extends BaseSessionBean {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7287111018902067801L;
	private YdUtils ydUtils = new YdUtils();
	YDComUtil   ydComUtil = new YDComUtil();
	private String szSessionName = getClass().getName();

	
	private final static  int ERROR   = 1;
	private final static  int WARNING = 2;
	private final static  int INFO    = 3;
	private final static  int DEBUG   = 4;
	
	
	
	/**
	 * ejbCreate()
	 * 
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	/**
	 * [A] 오퍼레이션명 : 공통 코드 조회 (WISEGRID)
	 * @ejb.interface-method
	 */
	public GridData getListCode(GridData gdReq) throws JDTOException {
		EJBConnector ejbConn = null;		
		try {
			ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
			return (GridData)ejbConn.trx("getListCode", new Class[] { GridData.class }, new Object[] { gdReq });
		} catch (Exception e) {
			throw new JDTOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	
	
	/**
	 * 공통 코드 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getComboCodeList(GridData inDto) throws JDTOException {

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		
		String szMethodName="getComboCodeList";		
		String szLogMsg = "";
		
		try{
			szLogMsg = "JSP-FACADE [공통 코드 조회]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			
			recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getComboCodeList", inRecord);
			
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		
		szLogMsg = "JSP-FACADE [공통 코드 조회]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	/**
	 * 출하차량상차LOT조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData getDmCarLiftLotList(GridData inDto) throws JDTOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getDmCarLiftLotList";
		String szOperationName = "출하차량상차LOT조회";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szMsg = "[JSP Facede] " + szOperationName + " 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getDmCarLiftLotList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			szMsg = "[JSP Facede] " + szOperationName + " 완료 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			szMsg = "[JSP Facede] " + szOperationName + " 시 오류발생 - " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getDmCarLiftLotList
	
		/**
		 * 
		 * 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param desti
		 * @param pushData
		 */
		public void pushToFlexClient(String desti, ArrayList pushData) throws JDTOException {
			

			
			String szLogMsg = "";
			String szMethodName ="pushToFlexClient";
			
			try{
				
				
				
				
				MessageBroker msgBroker = MessageBroker.getMessageBroker(null);
				String cliendID = UUIDUtils.createUUID(false);
				
				AsyncMessage msg = new AsyncMessage();
					
				msg.setDestination(desti);
				msg.setClientId(cliendID);
				msg.setMessageId( UUIDUtils.createUUID(false));				
				msg.setTimestamp(System.currentTimeMillis());
				
				msg.setBody(pushData);
			
				msgBroker.routeMessageToService(msg, null);
				
				szLogMsg = "pushToFlexClient SEND COUNT>>JSP>>pushToFlexClient!!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
 
			
			} catch(Exception e) {
				// Exception발생시 JDTOException의 상속클래스로 throw합니다.
					System.out.println("pushToFlexClient exception!!");
					throw new JDTOException(getClass().getName() + e.getMessage());
				} finally {
					
				}	
				
			}
		
		
		
		
		/**
		 * 
		 * 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param desti
		 * @param pushData
		 */
		public void pushToFlexClient2(String desti,Object pushData)throws JDTOException  {
			
			try {
				String szLogMsg = "";
				String szMethodName ="pushToFlexClient2";
				
			
				MessageBroker msgBroker = MessageBroker.getMessageBroker(null);
				String cliendID = UUIDUtils.createUUID(false);
				AsyncMessage msg = new AsyncMessage();
				
				msg.setDestination(desti);
				msg.setClientId(cliendID);
				msg.setMessageId( UUIDUtils.createUUID(false));				
				msg.setTimestamp(System.currentTimeMillis());
				
			
				msg.setBody(pushData);
			
				msgBroker.routeMessageToService(msg, null);
				
				
				szLogMsg = "JSP-FACADE [FLEX TOPIC 정보 INPUT]";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
				szLogMsg = "pushToFlexClient SEND COUNT>>JSP>>pushToFlexClient2!!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
 
			} catch(Exception e) {
				// Exception발생시 JDTOException의 상속클래스로 throw합니다.
					System.out.println("pushToFlexClient exception!!");
					throw new JDTOException(getClass().getName() + e.getMessage());
				} finally {
					
				}	
				
		}
		
		
		
		
		
		
		

		/**
		 * 구내운송 IDLE 차량 LIST
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getLIdelCar(GridData inDto) throws JDTOException {

			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			
			String szLogMsg = "";
			String szMethodName ="getLIdelCar";
			
			try{
				
				
				szLogMsg = "JSP-FACADE [구내운송 IDLE 차량 LIST] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getLIdelCar", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [구내운송 IDLE 차량 LIST] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		
		
		
		
		/**
		 *  야드크레인 작업관리 POP_UP (권상실적 처리)
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		
		
		public GridData updCrnUpPrsBackUp(GridData gdReq) throws JDTOException {
			//LOG
			String szMethodName = "updCrnUpPrsBackUp";
			String szLogMsg = null;
			String szRtnMsg = null;
			GridData gdRes = null;
			EJBConnector ejbConn = null;

			try{
				
				szLogMsg = "JSP-FACADE [야드크레인 작업관리 POP_UP (권상실적 처리)] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx("updCrnUpPrsBackUp", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]야드크레인작업관리 권상실적 처리 에러발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE [야드크레인 작업관리 POP_UP (권상실적 처리)] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		/**
		 *  야드크레인 작업관리 POP_UP (권하실적 처리)
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		
		
		public GridData updCrnDnPrsBackUp(GridData gdReq) throws DAOException {
			//LOG
			String szMethodName = "updCrnDnPrsBackUp";
			String szLogMsg = null;
			String szRtnMsg = null;
			GridData gdRes = null;
			EJBConnector ejbConn = null;

			try{
				
				szLogMsg = "JSP-FACADE [야드크레인 작업관리 POP_UP (권하실적 처리)] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx("updCrnDnPrsBackUp", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]야드크레인작업관리 권하실적 처리 에러발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			}
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE [야드크레인 작업관리 POP_UP (권하실적 처리)] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		
		
		/**
		 *  야드크레인 작업관리 POP_UP (권상/권하 처리)
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		
		
		public GridData updCrnUpDnPrsBackUp(GridData gdReq) throws JDTOException {
			//LOG
			String szMethodName = "updCrnUpDnPrsBackUp";
			String szLogMsg = null;
			String szRtnMsg = null;
			GridData gdRes = null;
			EJBConnector ejbConn = null;

			try{
				
				
				szLogMsg = "JSP-FACADE [야드크레인 작업관리 POP_UP (권상/권하 처리)] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				
				
				
				//권상 실적 처리				
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx("updCrnUpPrsBackUp", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				
				
				
				//권상 실적처리가 참일 경우만 권하 처리를 실행한다.
				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
					//권하 실적 처리				
					ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
					szRtnMsg = (String)ejbConn.trx("updCrnDnPrsBackUp", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				
				}
				
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]야드크레인작업관리 권상 /권하실적 처리 에러발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE [야드크레인 작업관리 POP_UP (권상/권하 처리)] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			return gdRes;
		}
		
		
		
		
		/**
		 *  권하위치 변경(크레인 상태 설정 화면)
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		
		
		public GridData updCrnDnPrsFix(GridData gdReq) throws JDTOException {
			//LOG			
			String szMethodName = "updCrnDnPrsFix";
			String szLogMsg = null;
			String szRtnMsg = null;
			GridData gdRes = null;
			EJBConnector ejbConn = null;

			try{
				
				szLogMsg = "JSP-FACADE [권하위치 변경(크레인 상태 설정 화면)] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx("updCrnDnPrsFix", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]권하위치 변경 처리 에러발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}		
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE [권하위치 변경(크레인 상태 설정 화면)] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		
		
		/**
		 * 크레인 목록 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getCrnList(GridData inDto) throws JDTOException {
			
			
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			
			//LOG
			String szMethodName = "getCrnList";
			String szLogMsg = null;
			
			
			try{
				
				szLogMsg = "JSP-FACADE [ 크레인 목록 조회] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getCrnList", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [ 크레인 목록 조회] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			return gdRes;
		}
		
		/**
		 * 크레인 스케줄 목록 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getCrnSchList(GridData inDto) throws JDTOException {
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			//LOG
			String szMethodName = "getCrnList";
			String szLogMsg = null;
			
			try{
				
				szLogMsg = "JSP-FACADE [크레인 스케줄 목록 조회] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getCrnSchList", inRecord);
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [크레인 스케줄 목록 조회] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		/**
		 * 설비 목록 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getEqpList(GridData inDto) throws JDTOException {
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			
			//LOG
			String szMethodName = "getEqpList";
			String szLogMsg = null;
			
			
			try{
				
				szLogMsg = "JSP-FACADE [설비 목록 조회] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getEqpList", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
				
				szLogMsg = "JSP-FACADE [설비 목록 조회] 끝";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
		}

		/**
		 * 스케줄 목록 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getSchRuleList(GridData inDto) throws JDTOException {
				GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			
			//LOG
			String szMethodName = "getSchRuleList";
			String szLogMsg = null;
			
			try{
				
				szLogMsg = "JSP-FACADE [스케줄 목록 조회] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getSchRuleList", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			

			szLogMsg = "JSP-FACADE [스케줄 목록 조회] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			return gdRes;
		}
		
		/**
		 * 스케줄 목록 조회 (화면:스케줄기준관리)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getSchRuleList_New(GridData inDto) throws JDTOException {
				GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			
			//LOG
			String szMethodName = "getSchRuleList_New";
			String szLogMsg = null;
			
			try{
				
				szLogMsg = "JSP-FACADE [스케줄 목록 조회 - 화면:스케줄기준관리] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getSchRuleList_New", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [스케줄 목록 조회 - 화면:스케줄기준관리] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		/**
		 * 스케줄 목록 조회 - 크레인별
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getSchRuleList_Crane(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			
			//LOG
			String szMethodName = "getSchRuleList_Crane";
			String szLogMsg = null;
			
			try{
				
				szLogMsg = "JSP-FACADE [스케줄 목록 조회 - 크레인별] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getSchRuleList_Crane", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			

			szLogMsg = "JSP-FACADE [스케줄 목록 조회 - 크레인별] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			return gdRes;
		}
		
		
		/**
		 * 크레인 작업재료 조회 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getCrnWrkMtlRef(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			
			//LOG
			String szMethodName = "getCrnWrkMtlRef";
			String szLogMsg = null;
			
			try{
				szLogMsg = "JSP-FACADE [크레인 작업재료 조회] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getCrnWrkMtlRef", inRecord);				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE [크레인 작업재료 조회] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			gdRes.setMessage("Success");
			return gdRes;
		}
		
		
		
		/**
		 * 후판정정야드 Remark조회 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getpPlateRemarkDtl(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			
			//LOG
			String szMethodName = "getpPlateRemarkDtl";
			String szLogMsg = null;
			
			try{
				szLogMsg = "JSP-FACADE [후판정정야드 Remark조회 ] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getpPlateRemarkDtl", inRecord);				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE [후판정정야드 Remark조회] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			gdRes.setMessage("Success");
			return gdRes;
		}
		
		/**
		 * 후판정정야드 Bookout기준조회 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getpPlateBookoutDtl(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			
			//LOG
			String szMethodName = "getpPlateBookoutDtl";
			String szLogMsg = null;
			
			try{
				szLogMsg = "JSP-FACADE [후판정정야드 Remark조회 ] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getpPlateBookoutDtl", inRecord);				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE [후판정정야드 Remark조회] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			gdRes.setMessage("Success");
			return gdRes;
		}
		
		
		
		/**
		 * 크레인 작업재료 조회 (화면:크레인작업관리)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getCrnWrkMtlRefCoil(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			
			//LOG
			String szMethodName = "getCrnWrkMtlRefCoil";
			String szLogMsg = null;
			
			try{
				szLogMsg = "JSP-FACADE [크레인 작업재료 조회 - 화면:크레인작업관리] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getCrnWrkMtlRefCoil", inRecord);				
				//gdRes = CmUtil.genGridData(inDto , recordSet);
				gdRes     = CmUtil.genResGridData(inDto , recordSet);

			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE [크레인 작업재료 조회 - 화면:크레인작업관리] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			gdRes.setMessage("Success");
			return gdRes;
		}
		

		
		/**
		 *  크레인 보류 
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData updCrnDelaySet(GridData gdReq) throws JDTOException {
			//			
			GridData gdRes           = null;
			EJBConnector ejbConn      = null;
			boolean bool              = false;
			Boolean boolWrapper       = null;
			String szMethodName       = "";
			String szLogMsg           = "";
			szMethodName = "updCrnDelaySet";
			

			try{
				
				
				szLogMsg = "JSP-FACADE [크레인 보류] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				boolWrapper = (Boolean)ejbConn.trx(szMethodName, new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				
				bool = boolWrapper.booleanValue();
				
				if(bool){
					gdRes.setStatus("true");
					gdRes.setMessage("Success");

					szLogMsg = "크레인 보류  성공";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, INFO);
					
				}else{
					gdRes.setStatus("true");
					gdRes.setMessage("Failure");
					
					szLogMsg = "크레인 보류  실패";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, WARNING);
				}
				
			}catch(JDTOException de) {
				
				gdRes.setMessage("Failure");
				szLogMsg = "크레인 보류  실패";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, ERROR);
				
			} catch(Exception e){
				
				gdRes.setMessage("Failure");
				szLogMsg = "크레인 보류  실패";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}			
			
			szLogMsg = "JSP-FACADE [크레인 보류] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		} // end of updCrnDelaySet()
		
		
		
		/**
		 *  크레인 보류해제
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		
		
		public GridData updCrnDelayCancleSet(GridData gdReq) throws JDTOException {
			//			
			GridData gdRes       = null;
			EJBConnector ejbConn = null;
			
			boolean bool         = false;
			Boolean boolWrapper  = null;
			
			
			String szMethodName       = "updCrnDelayCancleSet";
			String szLogMsg           = "";

			try{
				
				szLogMsg = "JSP-FACADE [크레인 보류해제] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				boolWrapper = (Boolean)ejbConn.trx(szMethodName , new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				bool = boolWrapper.booleanValue();
				
				if(bool){
					gdRes.setStatus("true");
					gdRes.setMessage("Success");
					szLogMsg = "크레인 보류해제 성공";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, INFO);
					
				}else{
					gdRes.setStatus("true");
					gdRes.setMessage("Failure");
					szLogMsg = "크레인 보류해제 실패";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, WARNING);
				}
				
				 
			}catch(JDTOException de) {
				
				gdRes.setMessage("Failure");
				szLogMsg = "크레인 보류해제 실패 - DAO Exception ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, ERROR);
				
			} catch(Exception e){
				gdRes.setMessage("Failure");
				szLogMsg = "크레인 보류해제 실패- JDTOException ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, ERROR);
				
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			
			}			
			
			szLogMsg = "JSP-FACADE [크레인 보류해제] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		} // end of updCrnDelayCancleSet()
		
		
		/**
		 *  차량출발
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		
		
		public GridData updCarDefBackUp(GridData gdReq) throws JDTOException {
			//			
			GridData gdRes = null;
			EJBConnector ejbConn = null;

			

			String szMethodName       = "updCarDefBackUp";
			String szLogMsg           = "";
			
			
			try{
				
				szLogMsg = "JSP-FACADE [차량 출발] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				ejbConn.trx("updCarDefBackUp", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				 
				
				szLogMsg = "JSP-FACADE [차량 출발] 끝";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}			
			return gdRes;
		} // end of updCarDefBackUp()
		
		
		/**
		 *  차량 도착
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		
		
		public GridData updCarArrBackUp(GridData gdReq) throws JDTOException {
			//			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			

			String szMethodName       = "updCarArrBackUp";
			String szLogMsg           = "";
			

			try{
				szLogMsg = "JSP-FACADE [차량 도착] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				ejbConn.trx("updCarArrBackUp", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				 
				
				szLogMsg = "JSP-FACADE [차량 도착] 끝";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}			
			return gdRes;
		} // end of updCarArrBackUp()
		
		
		/**
		 *위치 검색 범위 조회 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getYdLocSrchRng(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName       = "getYdLocSrchRng";
			String szLogMsg           = "";
			
			
			try{
				szLogMsg = "JSP-FACADE [위치 검색 범위 조회 ] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getYdLocSrchRng", inRecord);				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [위치 검색 범위 조회 ] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		/**
		 *위치 검색 범위 조회 (화면:위치검색SPAN관리)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getYdLocSrchRngCoil(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName       = "getYdLocSrchRngCoil";
			String szLogMsg           = "";
			
			
			try{
				szLogMsg = "JSP-FACADE [위치 검색 범위 조회 - 화면:위치검색SPAN관리] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getYdLocSrchRngCoil", inRecord);				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [위치 검색 범위 조회 - 화면:위치검색SPAN관리] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		/**
		 *	저장집합코드조회  (화면:위치검색SPAN관리)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getStrGtrCodeNew(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName       = "getStrGtrCodeNew";
			String szLogMsg           = "";
			
			
			try{
				szLogMsg = "JSP-FACADE [저장집합코드조회 - 화면:위치검색SPAN관리] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getStrGtrCodeNew", inRecord);				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [저장집합코드조회 - 화면:위치검색SPAN관리] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		/**
		 * 적치베드 조회 (저장집합코드)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getYdStkBedByYdStrGtrCd(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName       = "getYdStkBedByYdStrGtrCd";
			String szLogMsg           = "";
			
			try{
				szLogMsg = "JSP-FACADE [적치베드 조회 (저장집합코드) ] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getYdStkBedByYdStrGtrCd", inRecord);				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [적치베드 조회 (저장집합코드) ] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		} // end of getYdStkBedByYdStrGtrCd
		
		/**
		 * 적치베드 조회 (저장집합코드) 화면:위치검색SPAN관리
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getYdStkBedByYdStrGtrCdCoil(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName       = "getYdStkBedByYdStrGtrCdCoil";
			String szLogMsg           = "";
			
			try{
				szLogMsg = "JSP-FACADE [적치베드 조회 (저장집합코드) - 화면:위치검색SPAN관리] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getYdStkBedByYdStrGtrCdCoil", inRecord);				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [적치베드 조회 (저장집합코드) - 화면:위치검색SPAN관리] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		} // end of getYdStkBedByYdStrGtrCd
		
		/**
		 * 위치검색 베드 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getYdLocSrchBed(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName       = "getYdLocSrchBed";
			String szLogMsg           = "";
			
			try{
				
				szLogMsg = "JSP-FACADE [위치검색 베드 조회] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getYdLocSrchBed", inRecord);				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [위치검색 베드 조회] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			return gdRes;
		} // end of getYdLocSrchBed
		
		/** 2010.04.05
		 * 위치검색 베드 조회 (화면:위치검색SPAN관리)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getYdLocSrchBedCoil(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName       = "getYdLocSrchBedCoil";
			String szLogMsg           = "";
			
			try{
				
				szLogMsg = "JSP-FACADE [위치검색 베드 조회 - 화면:위치검색SPAN관리] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getYdLocSrchBedCoil", inRecord);				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [위치검색 베드 조회 - 화면:위치검색SPAN관리] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			return gdRes;
		} // end of getYdLocSrchBedCoil
		
		/**
		 * 슬라브 공통 조회 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getPtSlabComm(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName       = "getPtSlabComm";
			String szLogMsg           = "";
			
			try{
				szLogMsg = "JSP-FACADE [슬라브 공통 조회] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getPtSlabComm", inRecord);				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [슬라브 공통 조회] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		} // end of getPtSlabComm
		
		
		/**
		 * 주편 공통 조회 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getPtMSlabComm(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName       = "getPtMSlabComm";
			String szLogMsg           = "";
			
			try{
				
				szLogMsg = "JSP-FACADE [주편 공통 조회 ] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getPtMSlabComm", inRecord);				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [주편 공통 조회 ] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		} // end of getPtMSlabComm
		

		/**
		 * 후판 공통 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getPtPlateComm(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName       = "getPtPlateComm";
			String szLogMsg           = "";
			
			try{
				szLogMsg = "JSP-FACADE [후판 공통 조회] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getPtPlateComm", inRecord);				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [후판 공통 조회] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		} // end of getPtPlateComm
		
		/**
		 * 코일 공통 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getPtCoilComm(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			
			String szMethodName       = "getPtCoilComm";
			String szLogMsg           = "";
			
			try{
				
				szLogMsg = "JSP-FACADE [코일 공통 조회] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getPtCoilComm", inRecord);				
				gdRes = CmUtil.genGridData(inDto , recordSet);
				
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [코일 공통 조회] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		} // end of getPtCoilComm
		
		/**
		 *   위치검색 테이블 UPDATE/INSERT 
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		
		
		public GridData updYdLocSrchBed(GridData gdReq) throws JDTOException {						
			GridData gdRes = null;
			EJBConnector ejbConn = null;

			String szMethodName       = "updYdLocSrchBed";
			String szLogMsg           = "";
			
			try{
				
				szLogMsg = "JSP-FACADE [위치검색 테이블 UPDATE/INSERT ] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				ejbConn.trx("updYdLocSrchBed", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				 
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}			
			
			szLogMsg = "JSP-FACADE [위치검색 테이블 UPDATE/INSERT ] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		} // end of updYdLocSrchBed()
		
		/** 2010.04.05 
		 *   위치검색 테이블 UPDATE/INSERT (화면:위치검색SPAN관리)
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData updYdLocSrchBedCoil(GridData gdReq) throws JDTOException {						
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			String szMethodName       = "updYdLocSrchBedCoil";
			String szLogMsg           = "";
			
			try{
				
				szLogMsg = "JSP-FACADE [위치검색 테이블 UPDATE/INSERT - 화면:위치검색SPAN관리] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				ejbConn.trx("updYdLocSrchBedCoil", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				 
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}			
			
			szLogMsg = "JSP-FACADE [위치검색 테이블 UPDATE/INSERT - 화면:위치검색SPAN관리] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		} // end of updYdLocSrchBedCoil()
		
		/**
		 *   위치검색 테이블 삭제
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData delYdLocSrchBed(GridData gdReq) throws JDTOException {						
			GridData gdRes       = null;
			EJBConnector ejbConn = null;

			String szMethodName       = "delYdLocSrchBed";
			String szLogMsg           = "";
			
			
			try{
				
				szLogMsg = "JSP-FACADE [위치검색 테이블 삭제 ] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				ejbConn.trx("delYdLocSrchBed", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				szLogMsg = "JSP-FACADE [위치검색 테이블 삭제 ] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}			
			return gdRes;
		} // end of delYdLocSrchBed()
		
		/**
		 *   위치검색 테이블 삭제 (화면:위치검색SPAN관리)
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData delYdLocSrchBedCoil(GridData gdReq) throws JDTOException {						
			GridData gdRes       = null;
			EJBConnector ejbConn = null;

			String szMethodName       = "delYdLocSrchBedCoil";
			String szLogMsg           = "";
			
			
			try{
				
				szLogMsg = "JSP-FACADE [위치검색 테이블 삭제 - 화면:위치검색SPAN관리] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				ejbConn.trx("delYdLocSrchBedCoil_CHECK", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				szLogMsg = "JSP-FACADE [위치검색 테이블 삭제 - 화면:위치검색SPAN관리] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}			
			return gdRes;
		} // end of delYdLocSrchBedColi()
		
		/**
		 *   위치검색 테이블 삭제 (화면:위치검색SPAN관리)
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData delYdLocSrchBedCoil2(GridData gdReq) throws JDTOException {						
			GridData gdRes       = null;
			EJBConnector ejbConn = null;

			String szMethodName       = "delYdLocSrchBedCoil2";
			String szLogMsg           = "";
			
			
			try{
				
				szLogMsg = "JSP-FACADE [위치검색 테이블 삭제 - 화면:위치검색SPAN관리] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				ejbConn.trx("delYdLocSrchBedCoil_CHECK2", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				szLogMsg = "JSP-FACADE [위치검색 테이블 삭제 - 화면:위치검색SPAN관리] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}			
			return gdRes;
		} // end of delYdLocSrchBedCoil2()
		
		
		/**
		 *  위치검색 범위 수정
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData updYdLocSrchRng(GridData gdReq) throws JDTOException {						
			GridData gdRes       = null;
			EJBConnector ejbConn = null;

			String szMethodName       = "updYdLocSrchRng";
			String szLogMsg           = "";
			
			try{
				szLogMsg = "JSP-FACADE [ 위치검색 범위 수정 ] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				ejbConn.trx("updYdLocSrchRng", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				szLogMsg = "JSP-FACADE [ 위치검색 범위 수정 ] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}			
			return gdRes;
		} // end of updYdLocSrchRng()
		
		/**
		 *  위치검색 범위 수정 (화면:위치검색SPAN관리)
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData updYdLocSrchRngCoil(GridData gdReq) throws JDTOException {						
			GridData gdRes       = null;
			EJBConnector ejbConn = null;

			String szMethodName       = "updYdLocSrchRng";
			String szLogMsg           = "";
			
			try{
				szLogMsg = "JSP-FACADE [ 위치검색 범위 수정 - 화면:위치검색SPAN관리] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				ejbConn.trx("updYdLocSrchRngCoil", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				szLogMsg = "JSP-FACADE [ 위치검색 범위 수정 - 화면:위치검색SPAN관리] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}			
			return gdRes;
		} // end of updYdLocSrchRngCoil()
		
		
		
		/**
		 * 저장품  조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getStock(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			
			String szMethodName       = "getStock";
			String szLogMsg           = "";
						
			
			try{
				
				szLogMsg = "JSP-FACADE [  저장품  조회 ] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getStock", inRecord);				
				gdRes = CmUtil.genGridData(inDto , recordSet);
				
				
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			
			szLogMsg = "JSP-FACADE [ 저장품  조회 ] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			return gdRes;
		} // end of getStock
		
		
		/**
		 * 저장품 수정
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		public GridData updYdStock(GridData gdReq) throws JDTOException {						
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			

			String szMethodName       = "updYdStock";
			String szLogMsg           = "";
						
			
			try{
				
				szLogMsg = "JSP-FACADE [저장품 수정 ] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
							
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				ejbConn.trx("updYdStock", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				 
				
				szLogMsg = "JSP-FACADE [ 저장품 수정 ] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
				
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}			
			return gdRes;
		} // end of updYdStock()
		
		
		/**
		 * 슬라브 공통  항목 수정
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		public GridData updPtSlabCommFix(GridData gdReq) throws JDTOException {						
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			
			String szMethodName       = "updPtSlabCommFix";
			String szLogMsg           = "";
			

			try{
				
				szLogMsg = "JSP-FACADE [ 슬라브 공통  항목 수정] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				ejbConn.trx("updPtSlabCommFix", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				
				szLogMsg = "JSP-FACADE [ 슬라브 공통  항목 수정] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}			
			return gdRes;
		} // end of updPtSlabCommFix()
		
		
		/**
		 * 주편 공통  항목 수정
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		public GridData updPtMSlabCommFix(GridData gdReq) throws JDTOException {						
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			
			String szMethodName       = "updPtMSlabCommFix";
			String szLogMsg           = "";
						
			

			try{
				
				szLogMsg = "JSP-FACADE [ 주편 공통  항목 수정] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				ejbConn.trx("updPtMSlabCommFix", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				szLogMsg = "JSP-FACADE [ 주편 공통  항목 수정 ] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}			
			return gdRes;
		} // end of updPtMSlabCommFix()

		/**
		 * 후판 공통  항목 수정
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		public GridData updPtPlateComm(GridData gdReq) throws JDTOException {						
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			

			String szMethodName       = "updPtPlateComm";
			String szLogMsg           = "";
						
			
			try{
				
				szLogMsg = "JSP-FACADE [후판 공통  항목 수정] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				ejbConn.trx("updPtPlateComm", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				szLogMsg = "JSP-FACADE [ 후판 공통  항목 수정 ] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}			
			return gdRes;
		} // end of updPtPlateComm()
		
		
		
		/**
		 * 코일 공통  항목 수정
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		public GridData updPtCoilComm(GridData gdReq) throws JDTOException {						
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			

			String szMethodName       = "updPtCoilComm";
			String szLogMsg           = "";
						
			

			try{
				
				szLogMsg = "JSP-FACADE [코일 공통  항목 수정 ] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				ejbConn.trx("updPtCoilComm", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				szLogMsg = "JSP-FACADE [ 코일 공통  항목 수정 ] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}			
			return gdRes;
		} // end of updPtCoilComm()
		
		
		
		

		/**
		 * 저장품 (코일) 항목 수정
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		public GridData updStockCoilComm(GridData gdReq) throws JDTOException {						
			GridData gdRes = null;
			EJBConnector ejbConn = null;


			String szMethodName       = "updStockCoilComm";
			String szLogMsg           = "";
			
			try{
				szLogMsg = "JSP-FACADE [ 저장품 (코일) 항목 수정 ] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				ejbConn.trx("updStockCoilComm", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				
				szLogMsg = "JSP-FACADE [ 저장품 (코일) 항목 수정 ] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}			
			return gdRes;
		} // end of updStockCoilComm()
		
		
		
		/**
		 * 저장품 (후판) 항목 수정
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		public GridData updStockPlateComm(GridData gdReq) throws JDTOException {						
			GridData gdRes = null;
			EJBConnector ejbConn = null;

			String szMethodName       = "updStockPlateComm";
			String szLogMsg           = "";
			
			try{
				szLogMsg = "JSP-FACADE [저장품 (후판) 항목 수정 ] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				ejbConn.trx("updStockPlateComm", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				
				szLogMsg = "JSP-FACADE [저장품 (후판) 항목 수정 ] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}			
			return gdRes;
		} // end of updStockPlateComm()
		
		

		
		/**
		 * 크레인상태관리 - 명령선택기동
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		
		public GridData updCmdSelStart(GridData gdReq) throws JDTOException {	
			String szLogMsg = null;
			String szRtnMsg = null;
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			String szMethodName = "updCmdSelStart";
			try{
				szLogMsg = "JSP-FACADE [ 크레인상태관리 - 명령선택기동] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx("updCmdSelStart", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]명령선택기동 에러발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}
			
			szLogMsg = "JSP-FACADE [크레인상태관리 - 명령선택기동] ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			gdRes.setStatus("true");
			return gdRes;
		} // end of updCmdSelStart()
		
		
		
		
		/**
		 * 크레인상태관리 - 우선순위 변경
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		
		public GridData crnChgSchPrior(GridData gdReq) throws JDTOException {						
			GridData gdRes            = null;
			EJBConnector ejbConn      = null;
			boolean bool              = false;
			Boolean boolWrapper       = null;
			String szMethodName       = "crnChgSchPrior";
			String szLogMsg           = "";

			try{
		
				szLogMsg = "JSP-FACADE  [ 크레인상태관리 - 우선순위 변경 ] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				boolWrapper = (Boolean)ejbConn.trx("crnChgSchPrior", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				
				bool = boolWrapper.booleanValue();
				
				if(bool){
					gdRes.setStatus("true");
					gdRes.setMessage("Success");
					
					szLogMsg = "우선순위 변경 성공";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, INFO);
					
				}else{
					gdRes.setStatus("true");
					gdRes.setMessage("Failure");
					szLogMsg = "우선순위  변경 실패";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, WARNING);
				}
				
				
			}catch(JDTOException de) {
				gdRes.setMessage("Failure");
				szLogMsg = "우선순위 변경 실패 - DAO Exception ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, ERROR);
			}catch(Exception e){
				gdRes.setMessage("Failure");
				szLogMsg = "우선순위  변경 실패 - JDTOException ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}			
			
			
			szLogMsg = "JSP-FACADE  [ 크레인상태관리 - 우선순위 변경 ] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			return gdRes;
		} // end of crnChgSchPrior()
		
		
		/**
		 * 크레인상태관리 - 크레인 변경
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		
		public GridData wrkCrnChg(GridData gdReq)throws JDTOException  {						
			GridData gdRes            = null;
			EJBConnector ejbConn      = null;
			boolean bool              = false;
			Boolean boolWrapper       = null;
						
			String szMethodName       = "wrkCrnChg";
			String szLogMsg           = "";

			try{
				
				szLogMsg = "JSP-FACADE  [ 크레인상태관리 - 크레인 변경 ]시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				boolWrapper = (Boolean)ejbConn.trx("wrkCrnChg", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				bool = boolWrapper.booleanValue();
				
				if(bool){
					gdRes.setStatus("true");
					gdRes.setMessage("Success");
					
					//Log 
					szLogMsg = "크레인 변경 성공";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, INFO);
					
				}else{
					gdRes.setStatus("true");
					gdRes.setMessage("Failure");

					//Log 
					szLogMsg = "크레인 변경 실패";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, WARNING);
				}
				
			}catch(JDTOException de) {
				
				gdRes.setMessage("Failure");
				
				//Log 
				szLogMsg = "크레인 변경 실패 - DAO Exception ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, ERROR);
				
			} catch(Exception e){
				//Log
				gdRes.setMessage("Failure");
				szLogMsg = "크레인 변경 실패 - JDTOException ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, ERROR);
				
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}			
			
			szLogMsg = "JSP-FACADE  [ 크레인상태관리 - 크레인 변경 ] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		} // end of wrkCrnChg()
		
		
		/**
		 * 크레인상태관리 - 크레인 변경[신규작성중]
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		
		public GridData wrkCrnChange(GridData gdReq) throws JDTOException {						
			GridData gdRes            = null;
			EJBConnector ejbConn      = null;
		
			
						
			String szMethodName       = "wrkCrnChange";
			String szLogMsg           = "";
			String szOperationName = " 크레인상태관리 - 크레인 변경";
			String szRtnValue =  YdConstant.RETN_CD_SUCCESS;

			try{
				
				szLogMsg = "JSP-FACADE  [ "+ szOperationName +" ]시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnValue = (String)ejbConn.trx("wrkCrnChange", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				
				gdRes.setMessage(szRtnValue);
		
				
			}catch(JDTOException de) {
				
				gdRes.setMessage(szRtnValue);
				
				//Log 
				szLogMsg = "크레인 변경 실패 - DAO Exception ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, ERROR);
				
			} catch(Exception e){
				//Log
				gdRes.setMessage(szRtnValue);
				szLogMsg = "크레인 변경 실패 - JDTOException ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, ERROR);
				
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}			
			
			szLogMsg = "JSP-FACADE  [ 크레인상태관리 - 크레인 변경 ] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		} // end of wrkCrnChange()
		
		
		

		/**
		 * 크레인 작업 구분 지정
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		
		public GridData crnWrkGPartSet(GridData gdReq) throws JDTOException {
			String szLogMsg = null;
			String szRtnMsg = null;
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			String szMethodName = "crnWrkGPartSet";
			try{
				
				szLogMsg = "JSP-FACADE  [ 크레인 작업 구분 지정] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx("crnWrkGPartSet", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]크레인 작업 구분 지정 에러발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}
			
			szLogMsg = "JSP-FACADE  [ 크레인 작업 구분 지정] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			gdRes.setStatus("true");
			return gdRes;
		} // end of crnWrkGPartSet()
		
		
		/**
		 * 크레인 작업 구분 해제
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		
		public GridData crnWrkGpCalcle(GridData gdReq) throws JDTOException {
			String szLogMsg = null;
			String szRtnMsg = null;
			String szMethodName = "crnWrkGpCalcle";
			GridData gdRes = null;
			EJBConnector ejbConn = null;

			try{
				
				szLogMsg = "JSP-FACADE  [ 크레인 작업 구분 해제] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx("crnWrkGpCalcle", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]크레인 작업 구분 해제 에러발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE  [ 크레인 작업 구분 해제] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		} // end of crnWrkGpCalcle()
		
		
		

		/**
		 * 권상 취소
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData crnUpCancle(GridData gdReq) throws JDTOException {						
			String szLogMsg = null;
			String szRtnMsg = null;
			String szMethodName = "crnUpCancle";
			GridData gdRes = null;
			EJBConnector ejbConn = null;

			try{
				
				szLogMsg = "JSP-FACADE [ 권상 취소] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx("crnUpCancle", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]크레인 작업 구분 해제 에러발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}	
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE [ 권상 취소] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		} // end of crnUpCancle()
		
		
		
		/**
		 *  슬라브야드 메뉴얼 작업지시 편성
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData ydManualReq(GridData gdReq) throws JDTOException {
			//LOG
			String szMsg = "";
			String szMethodName="slabYdManualReq";
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			try{

				szMsg = "JSP-FACADE [ 슬라브야드 메뉴얼 작업지시 편성] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				ejbConn.trx("ydManualReq", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				szMsg = "JSP-FACADE [ 슬라브야드 메뉴얼 작업지시 편성] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				
			}catch(Exception e){
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			return gdRes;
		}  //end of ydManualReq
		
		
		/**
		 * 저장집합 목록 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getYdStrGtrCd(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				
				String szMethodName       = "getYdStrGtrCd";
				String szLogMsg           = "";
							
				
				
				szLogMsg = "JSP-FACADE [ 저장집합 목록 조회] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getYdStrGtrCd", inRecord);				
				gdRes = CmUtil.genGridData(inDto , recordSet);
				
				szLogMsg = "JSP-FACADE [ 저장집합 목록 조회] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
		} // end of getStock
		
		
		/**
		 *  권하위치 변경 (크레인작업관리 화면) 코일 사용 안함
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData updToPosFix(GridData gdReq) throws JDTOException {
			
			String szLogMsg           = "";
			String szMethodName       = "";
			boolean bool              = false;
			Boolean boolWrapper       = null;

			GridData gdRes            = null;
			EJBConnector ejbConn      = null;
			
			try{
				
				szLogMsg = "JSP-FACADE [권하위치 변경 (크레인작업관리 화면)] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				gdRes = OperateGridData.cloneResponseGridData(gdReq);
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				boolWrapper = (Boolean)ejbConn.trx("updToPosFix", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				bool = boolWrapper.booleanValue();
				szMethodName = "updToPosFix";
				
				if(bool){
					gdRes.setStatus("true");
					gdRes.setMessage("Success");
					szLogMsg = "권하위치 변경 성공";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, INFO);
					
				}else{
					gdRes.setStatus("true");
					gdRes.setMessage("Failure");
					szLogMsg = "적치불가능 베드입니다.(현대3사제품 관련)";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, WARNING);
				}
				
			}catch(JDTOException de) {
				
				gdRes.setMessage("Failure");
				szLogMsg = "권하위치 변경 실패";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, ERROR);
				
			}catch(Exception e){
				gdRes.setMessage("Failure");
				szLogMsg = "권하위치 변경 실패";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			
			szLogMsg = "JSP-FACADE [권하위치 변경 (크레인작업관리 화면)] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}  //end of updToPosFix
		
		
		
		

		/**
		 *  스케줄 기동 (스케줄기동관리 화면)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData schStart(GridData gdReq) throws JDTOException {
			//LOG
			String szMsg = "";
			String szMethodName="updToPosFix";
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			try{
				szMsg = "JSP-FACADE [스케줄 기동 (스케줄기동관리 화면)] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				ejbConn.trx("schStart", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				szMsg = "JSP-FACADE [스케줄 기동 (스케줄기동관리 화면)] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				
			}catch(Exception e){
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			return gdRes;
		}  //end of schStart
		
		
		

		/**
		 *   차량 상차 정보 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getCarLiftInfo(GridData gdReq) throws JDTOException {
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName="getCarLiftInfo";
			String szLogMsg           = "";
			
			try{
				
				szLogMsg = "JSP-FACADE [차량 상차 정보 조회] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getCarLiftInfo", inRecord);				
				gdRes = CmUtil.genGridData(gdReq , recordSet);
			}catch(Exception e){
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [차량 상차 정보 조회] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
			
		}
		
	
		
		
		/**
		 *   차량 상차 정보 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getCarLiftInfo_plateGds(GridData gdReq) throws JDTOException {
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName="getCarLiftInfo_plateGds";
			String szLogMsg           = "";
			
			try{
				
				szLogMsg = "JSP-FACADE [차량 상차 정보 조회] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", szMethodName, inRecord);				
				gdRes = CmUtil.genGridData(gdReq , recordSet);
			}catch(Exception e){
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [차량 상차 정보 조회] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
			
		}
		
		
		/**
		 *   차량 상차 정보 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getCarLiftInfo_BCoil(GridData gdReq) throws JDTOException {
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName="getCarLiftInfo_BCoil";
			String szLogMsg           = "";
			
			try{
				
				szLogMsg = "JSP-FACADE [차량 상차 정보 조회] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getCarLiftInfo_BCoil", inRecord);				
				gdRes = CmUtil.genGridData(gdReq , recordSet);
				
				szLogMsg = "JSP-FACADE [차량 상차 정보 조회] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
			}catch(Exception e){
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
			
		}
		/**
		 *   차량 상차 정보 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getCarLiftInfo_BSlab(GridData gdReq) throws JDTOException {
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName="getCarLiftInfo_BSlab";
			String szLogMsg           = "";
			try{
				
				szLogMsg = "JSP-FACADE [차량 상차 정보 조회] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getCarLiftInfo_BSlab", inRecord);				
				gdRes = CmUtil.genGridData(gdReq , recordSet);
				
				szLogMsg = "JSP-FACADE [차량 상차 정보 조회] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
			}catch(Exception e){
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
			
		}
		/**
		 *   차량 상차 정보 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getCarLiftInfo_ACoil(GridData gdReq) throws JDTOException {
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName="getCarLiftInfo_ACoil";
			String szLogMsg           = "";
			
			try{
				
				szLogMsg = "JSP-FACADE [차량 상차 정보 조회] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getCarLiftInfo_ACoil", inRecord);				
				gdRes = CmUtil.genGridData(gdReq , recordSet);
				
				szLogMsg = "JSP-FACADE [차량 상차 정보 조회] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
			}catch(Exception e){
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
			
		}
		/**
		 *   차량 상차 정보 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getCarLiftInfo_ASlab(GridData gdReq) throws JDTOException {
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName="getCarLiftInfo_ASlab";
			String szLogMsg           = "";
			
			try{
				
				szLogMsg = "JSP-FACADE [차량 상차 정보 조회] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getCarLiftInfo_ASlab", inRecord);				
				gdRes = CmUtil.genGridData(gdReq , recordSet);
				
				szLogMsg = "JSP-FACADE [차량 상차 정보 조회] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
			}catch(Exception e){
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
			
		}
		
		/**
		 *   차량번호 LIST
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getCarNoList(GridData gdReq) throws JDTOException {
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName		= "getCarNoList";
			String szLogMsg         = "";
			
			try{
				
				szLogMsg = "JSP-FACADE [차량번호 LIST] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getCarNoList", inRecord);				
				gdRes = CmUtil.genGridData(gdReq , recordSet);
				
			}catch(Exception e){
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [차량번호 LIST] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		} // End of getCarNoList
		
		
		
		
		/**
		 *   적치단 - 열 구분으로 조회 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getStkLyrByStkColGp(GridData gdReq) throws JDTOException {
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName="getStkLyrByStkColGp";
			String szLogMsg           = "";
			
			try{
				szLogMsg = "JSP-FACADE [적치단 - 열 구분으로 조회  ] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getStkLyrByStkColGp", inRecord);				
				gdRes = CmUtil.genGridData(gdReq , recordSet);
				
				
				szLogMsg = "JSP-FACADE [적치단 - 열 구분으로 조회 ] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
			}catch(Exception e){
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
		}
		
		/**
		 * 메뉴얼 작업지시 편성
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData ydCoilManualReq(GridData gdReq) throws JDTOException {
			//LOG
			String szMsg = "";
			String szMethodName="ydCoilManualReq";
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			try{
				
				szMsg = "JSP-FACADE [ 메뉴얼 작업지시 편성  ] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				ejbConn.trx("ydCoilManualReq", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				szMsg = "JSP-FACADE [ 메뉴얼 작업지시 편성  ] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				
			}catch(Exception e){
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			return gdRes;
		}  //end of ydCoilManualReq
		
		
		
		/**
		 *  공대차 스케줄 호출 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		public GridData ydTcarA(GridData gdReq) throws JDTOException {
			//LOG
			String szLogMsg = null;
			String szRtnMsg = null;
			String szMethodName="ydTcarA";
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			try{

				szLogMsg = "JSP-FACADE [ 공대차 스케줄 호출  ] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				szRtnMsg = (String)ejbConn.trx("ydTcarA", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]공대차 스케줄 호출 처리 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}catch(Exception e){
				gdRes.setMessage(YdConstant.RETN_CD_FAILURE);
				szLogMsg = "[JSP Facade]공대차 스케줄 호출 처리 예외발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE [ 공대차 스케줄 호출  ] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}  //end of ydTcarA
		
		
		
		
		/**
		 *  출발 실적
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		public GridData ydTcarB(GridData gdReq) throws JDTOException {
			//LOG
			String szLogMsg = null;
			String szRtnMsg = null;
			String szMethodName="ydTcarB";
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			try{
				
				szLogMsg = "JSP-FACADE [  출발 실적 ] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				

				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				szRtnMsg = (String)ejbConn.trx("ydTcarB", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]대차 출발 실적 호출 처리 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}catch(Exception e){
				gdRes.setMessage(YdConstant.RETN_CD_FAILURE);
				szLogMsg = "[JSP Facade]대차 출발 실적 호출 처리 예외발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE [  출발 실적 ] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}  //end of ydTcarB
		
		
		/**
		 *  도착실적
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		public GridData ydTcarC(GridData gdReq) throws JDTOException {
			//LOG
			String szLogMsg = null;
			String szRtnMsg = null;
			String szMethodName="ydTcarC";
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			try{
				
				szLogMsg = "JSP-FACADE [  도착실적  ] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				szRtnMsg = (String)ejbConn.trx("ydTcarC", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]대차 도착실적 호출 처리 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}catch(Exception e){
				gdRes.setMessage(YdConstant.RETN_CD_FAILURE);
				szLogMsg = "[JSP Facade]대차 도착실적 호출 처리 예외발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE [  도착실적  ] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}  //end of ydTcarC
		
		
		/**
		 *  완료 실적
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		public GridData ydTcarD(GridData gdReq) throws JDTOException {
			//LOG
			String szLogMsg = null;
			String szRtnMsg = null;
			String szMethodName="ydTcarD";
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			try{
				
				szLogMsg = "JSP-FACADE [  완료 실적 ] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				

				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				szRtnMsg = (String)ejbConn.trx("ydTcarD", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]대차 완료 실적 호출 처리 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}catch(Exception e){
				gdRes.setMessage(YdConstant.RETN_CD_FAILURE);
				szLogMsg = "[JSP Facade]대차 완료 실적 호출 처리 예외발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE [  완료 실적 ] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}  //end of ydTcarD
		
		

		/**
		 *  현재동 변경
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		public GridData ydTcarE(GridData gdReq) throws JDTOException {
			//LOG
			String szLogMsg = null;
			String szRtnMsg = null;
			String szMethodName="ydTcarE";
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			try{
				
				szLogMsg = "JSP-FACADE [  현재동 변경 ] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				szRtnMsg = (String)ejbConn.trx("ydTcarE", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]현재동 변경 처리 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}catch(Exception e){
				gdRes.setMessage(YdConstant.RETN_CD_FAILURE);
				szLogMsg = "[JSP Facade]현재동 변경 처리 예외발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			szLogMsg = "JSP-FACADE [  현재동 변경 ] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}  //end of ydTcarE
		
		
		/**
		 * HOME 동 변경
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		public GridData ydTcarF(GridData gdReq) throws JDTOException {
			//LOG
			String szLogMsg = null;
			String szRtnMsg = null;
			String szMethodName="ydTcarF";
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			try{
				
				szLogMsg = "JSP-FACADE [ HOME 동 변경]시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				szRtnMsg = (String)ejbConn.trx("ydTcarF", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]HOME 동 변경 처리 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}catch(Exception e){
				gdRes.setMessage(YdConstant.RETN_CD_FAILURE);
				szLogMsg = "[JSP Facade]HOME 동 변경 처리 예외발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE [HOME 동 변경]끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}  //end of ydTcarF
		
		
		/**
		 * 컨베어 목록 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getYdConveyorCodeName(GridData inDto) throws JDTOException {
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName="getYdConveyorCodeName";		
			String szLogMsg = "";
			
			try{
				szLogMsg = "JSP-FACADE [컨베어 목록 조회]시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getYdConveyorCodeName", inRecord);
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [컨베어 목록 조회]끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}		
		

		/**
		 * 분기 컨베어 목록 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getYdDivConveyorCodeName(GridData inDto) throws JDTOException {
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName="getYdDivConveyorCodeName";		
			String szLogMsg = "";
			try{
				
				szLogMsg = "JSP-FACADE [분기 컨베어 목록 조회]시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getYdDivConveyorCodeName", inRecord);
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [분기 컨베어 목록 조회] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			return gdRes;
		}		
		
		
		
		
		
		/**
		 * 위치검색베범위 등록
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		public GridData insLocSrchRng(GridData gdReq) throws JDTOException {
			//LOG
			String szMsg = "";
			String szMethodName="insLocSrchRng";
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			try{
				
				szMsg = "JSP-FACADE [위치검색베범위 등록]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
				

				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				ejbConn.trx("insLocSrchRng", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
			}catch(Exception e){
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			
			
			szMsg = "JSP-FACADE [위치검색베범위 등록]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return gdRes;
		}  //end of insLocSrchRng
		
		
		
		
		
		/**
		 * 공차 배차 실행
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		public GridData setLIdelCar(GridData gdReq) throws JDTOException {
			//LOG
			String szLogMsg = null;
			String szRtnMsg = null;
			String szMethodName="setLIdelCar";
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			try{
				szLogMsg = "JSP-FACADE [공차 배차 실행]시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				

				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				szRtnMsg = (String)ejbConn.trx("setLIdelCar", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]공차 배차 실행 에러발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE [공차 배차 실행]끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}  //end of setLIdelCar
		
		
		/**
		 * 차량진행관리 - 상태 값 변경 Main
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		
		public GridData updCarProgMgtBk(GridData gdReq) throws JDTOException {
			//LOG
			String szLogMsg = null;
			String szRtnMsg = null;
			String szMethodName="updCarProgMgtBk";
			
			
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			try{
				szLogMsg = "JSP-FACADE [차량진행관리 - 상태 값 변경 Main]시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				szRtnMsg = (String)ejbConn.trx("updCarProgMgtBk", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				try {
					szRtnMsg = YdConstant.RETN_CD_FAILURE;
					gdRes = OperateGridData.cloneResponseGridData(gdReq);
					gdRes = CmUtil.copyGDParam(gdReq, gdRes);
					gdRes.setMessage(szRtnMsg);
					szLogMsg = "[JSP Facade]공차 배차 실행 에러발생 : " + e.getMessage();
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				}catch(Exception ex) {
					szLogMsg = "[JSP Facade]공차 배차 실행 - GridData 생성 시 에러발생 : " + e.getMessage();
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					throw new JDTOException(getClass().getName() + e.getMessage(), e);
				}
			}
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE [차량진행관리 - 상태 값 변경 Main]끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			return gdRes;
		}  //end of updCarProgMgtBk
		
		
		/**
		 * 상차LOT편성 - 차량진행관리
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param gdReq
		 * @return
		 * @throws JDTOException
		 */
		public GridData mkUpLoadCarLot(GridData gdReq) throws JDTOException {
			//LOG
			String szMsg = "";
			String szMethodName="mkUpLoadCarLot";
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			
			JDTORecordSet rsWbookMtl        = null;
			JDTORecord    recInTemp         = null;
			
			String szTRN_EQP_CD	= "";
			String szYD_CAR_USE_GP = "";
			String szYD_GP = null;
			
			YdDaoUtils ydDaoUtils = new YdDaoUtils();
			YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
			
			int intRtnVal =0;
			
			String szRtnMsg = "";
			
			try{
				szMsg = "JSP-FACADE [상차LOT편성 - 차량진행관리]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	

				JDTORecord inRecord =  CmUtil.genJDTORecord(gdReq);
				
				szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(inRecord, "TRN_EQP_CD");
				szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(inRecord, "YD_CAR_USE_GP");
				szYD_GP	= ydDaoUtils.paraRecChkNull(inRecord, "YD_GP");
				
				rsWbookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("TRN_EQP_CD",    szTRN_EQP_CD);
		    	recInTemp.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
		    	
		    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsWbookMtl, 13);
		    	
		    	if( intRtnVal == 0 ) {
		    		szMsg = "[JSP Facade]상차LOT편성 - 차량진행관리에서 EJB Call [YdJspCommonSeEJB.mkUpLoadCarLot], 야드구분[" + szYD_GP + "]";
		    		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ejbConn = new EJBConnector("default", this);
					szRtnMsg = (String)ejbConn.trx("YdJspCommonSeEJB", "mkUpLoadCarLot", inRecord);
					//szRtnMsg = "Success";
		    	}else if( intRtnVal > 0 ){
		    		//szRtnMsg = "이미 상차LOT편성이 되어 있는 차량스케줄입니다!!";
		    		szRtnMsg = YdConstant.RETN_CD_EXIST;
		    		szMsg = "[JSP Facade]상차LOT편성 - 차량진행관리 : 이미 상차LOT편성이 되어 있는 차량스케줄입니다!!";
		    		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	}
		    	gdRes = OperateGridData.cloneResponseGridData(gdReq);
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
				
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szMsg = "[JSP Facade]상차LOT편성 - 차량진행관리 오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			
			szMsg = "JSP-FACADE [상차LOT편성 - 차량진행관리]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		/**
		 * 준비작업스케줄 편성
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getReadySch(GridData inDto) throws JDTOException {

			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			
			String szMethodName="getReadySch";		
			String szLogMsg = "";
			
			try{
				
				szLogMsg = "JSP-FACADE [준비작업스케줄 편성]시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getReadySch", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");	
			
			szLogMsg = "JSP-FACADE [준비작업스케줄 편성]끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		
		/**
		 * 준비작업스케줄 편성
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getReadySchPage(GridData inDto) throws JDTOException {

			GridData      gdRes     		= null;
			EJBConnector  ejbConn   		= null;
			JDTORecordSet recordSet 		= null;
			
			String szMethodName				= "getReadySchPage";	
			String szOperationName			= "준비작업스케줄 편성";
			String szLogMsg 				= "";
			
			try{
				
				szLogMsg = "[Jsp Facade : " + szOperationName + "] ----------------- 메소드 시작 -----------------";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getReadySchPage", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				szLogMsg = "[Jsp Facade : " + szOperationName + "] 예외발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");	
			
			szLogMsg = "[Jsp Facade : " + szOperationName + "] ----------------- 메소드 끝 -----------------";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		
		/**
		 *  작업예약등록(이적)
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData insMvWBookId(GridData gdReq) throws JDTOException {
			//			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			String szLogMsg = null;
			String szRtnMsg = "";
			String szMethodName = "insMvWBookId";
			try{
				
				szLogMsg = "JSP-FACADE [작업예약등록(이적)]시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
				JDTORecord [] inRecord = ydComUtil.genGridToJDTORecordAll(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx("insMvWBookId", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setStatus("true");
//				if( szRtnMsg.equals(YdConstant.RETN_CD_EXIST) ) {
//					gdRes.setMessage(szRtnMsg);
//				}else{
//					gdRes.setMessage("Success");
//				}
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				
				try {
					szRtnMsg = e.getMessage();
					gdRes = OperateGridData.cloneResponseGridData(gdReq);		
					gdRes = CmUtil.copyGDParam(gdReq, gdRes);
					gdRes.setStatus("true");
					gdRes.setMessage(szRtnMsg);
				}catch(Exception ex) {
					szLogMsg = "에러메세지 : " + ex.getMessage();
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					throw new JDTOException(getClass().getName() + e.getMessage(), e);
				}
			}			
			
			szLogMsg = "JSP-FACADE [작업예약등록(이적)]끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		} // end of insMvWBookId()
		
		
		
		/**
		 *  준비스케줄화면에서 선택된 베드의 대상재들이 크레인의 작업가능한 매수인 지를 판단하는 함수
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param gdReq
		 * @return
		 * @throws JDTOException
		 */
		public GridData chkIfCrnWorkable(GridData gdReq) throws JDTOException {
			//반환할 그리드 객체
			GridData gdRes = null;
			//메세지 처리 객체
			String szRtnMsg = "Success";
			//로그메세지 처리 객체
			String szLogMsg = "";
			//메소드 명
			String szMethodName="chkIfCrnWorkable";
			//저장품 DAO 객체
			YdStockDao ydStockDao = new YdStockDao();
			//유틸리티 객체
			YdDaoUtils ydDaoUtils = new YdDaoUtils();
			//레코드 객체
			JDTORecord inRec = JDTORecordFactory.getInstance().create();
			JDTORecord recTemp = null;
			//레코드셋 객체
			JDTORecordSet outRecSet = null;
			//DAO처리 시 반환값을 저장할 변수 선언
			int intRtnVal = 0;
			boolean blnRtnVal = false;
			//저장품의 무게
			long lngStockWeight = 0;
			//저장품의 길이
			int intStockLength = 0;
			//저장품의 폭
			int intStockWidth = 0;
			//대상재의 무게 합을 저장할 변수
			long lngSumWeight = 0;
			//작업 총 매수
			int intMtlCount = 0;
			//적치열구분
			String szYD_STK_COL_GP = null;
			//적치베드
			String szYD_STK_BED_NO = null;
			//야드목표행선구분
			String szYD_AIM_RT_GP = null;
			//크레인ID(설비ID)
			String szYD_EQP_ID 	= null;
			//크레인 작업허용 중량
			long lngYD_WRK_ABLE_WT = 0;
			//크레인 집게허용 오차
			int intYD_CRN_TONG_W_TOL = 0;
			//크레인 작업가능 매수
			int intYD_WRK_ABLE_SH = 0;
			try{
				
				szLogMsg = "JSP-FACADE [준비스케줄화면에서 선택된 베드의 대상재들이 크레인의 작업가능한 매수인 지를 판단 ]시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
				
				
				//그리드를 레코드배열로 변환, 파라미터도 같이 레코드의 필드값을 설정
				JDTORecord [] inRecord = ydComUtil.genGridToJDTORecordAll(gdReq);
				//레코드배열을 반복 처리
				for(int Loop_i = 0; Loop_i < inRecord.length; Loop_i++ ) {
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(inRecord[Loop_i], "YD_STK_COL_GP");
					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRecord[Loop_i], "YD_STK_BED_NO");
					szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(inRecord[Loop_i], "YD_AIM_RT_GP");
					
					szLogMsg = "YD_STK_COL_GP = " + szYD_STK_COL_GP + ", YD_STK_BED_NO = " + szYD_STK_BED_NO + ", YD_AIM_RT_GP = " + szYD_AIM_RT_GP;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					//레코드셋 생성
					outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
					
					//적치열,베드에 적치된 해당하는 야드목표행선을 가진 대상재를 조회
					inRec.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
					inRec.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
					inRec.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);
					szLogMsg = "inRec : " + inRec + ", outRecSet : " + outRecSet;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					intRtnVal = ydStockDao.getYdStock(inRec, outRecSet, 93);
					szLogMsg = "intRtnVal : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					//조회결과를 체크
					if( intRtnVal == 0 ) {
						szLogMsg = "YD_STK_COL_GP = " + szYD_STK_COL_GP + ", YD_STK_BED_NO = " + szYD_STK_BED_NO + ", YD_AIM_RT_GP = " + szYD_AIM_RT_GP 
						+ "에 대상재가 존재하지 않습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						continue;
					}else if( intRtnVal < 0 ) {
						szLogMsg = "YD_STK_COL_GP = " + szYD_STK_COL_GP + ", YD_STK_BED_NO = " + szYD_STK_BED_NO + ", YD_AIM_RT_GP = " + szYD_AIM_RT_GP
						+ "에 대상재를 조회 시 에러가 발생했습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						continue;
					}
					
					szLogMsg = "YD_STK_COL_GP = " + szYD_STK_COL_GP + ", YD_STK_BED_NO = " + szYD_STK_BED_NO + ", YD_AIM_RT_GP = " + szYD_AIM_RT_GP
					+ "에 대상재가 존재합니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					//조회된 결과값을 가져와서 로직 처리
					for(int Loop_j = 1; Loop_j <= outRecSet.size(); Loop_j++ ) {
						outRecSet.absolute(Loop_j);
						recTemp = outRecSet.getRecord();
						//저장품의 무게
						lngStockWeight = ydDaoUtils.paraRecChkNullLong(recTemp, "YD_MTL_WT");
						//저장품의 길이
						intStockLength = ydDaoUtils.paraRecChkNullInt(recTemp, "YD_MTL_L");
						//저장품의 폭
						intStockWidth = ydDaoUtils.paraRecChkNullInt(recTemp, "YD_MTL_W");
						szLogMsg = "저장품[" + ydDaoUtils.paraRecChkNull(recTemp, "STL_NO") + "]의 무게 = " + lngStockWeight + ", 저장품의 길이 = " + intStockLength + ", 저장품의 폭 = " + intStockWidth;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						//총 무게
						lngSumWeight += lngStockWeight;
						//총매수
						intMtlCount++;
					}
				}
				
				szLogMsg = "저장품의 총 무게 = " + lngSumWeight;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				if( szYD_STK_COL_GP.startsWith("A") || szYD_STK_COL_GP.startsWith("D") ) {
					//C연주슬라브야드, A후판슬라브야드 - 01, 02스판은 1번 크레인 할당, 03,04스판은 2번 크레인 할당
					if( szYD_STK_COL_GP.substring(2, 4).equals("01") || szYD_STK_COL_GP.substring(2, 4).equals("02") ) {
						szYD_EQP_ID = szYD_STK_COL_GP.substring(0, 2) + "CR" + szYD_STK_COL_GP.substring(1, 2) + "1";
					}else{
						szYD_EQP_ID = szYD_STK_COL_GP.substring(0, 2) + "CR" + szYD_STK_COL_GP.substring(1, 2) + "2";
					}
					szLogMsg = "현재 작업크레인은 [" + szYD_EQP_ID + "]입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else{
					szLogMsg = "현재는 지원하지 않는 야드구분[" + szYD_STK_COL_GP.substring(0, 1) + "]입니다.";
					szRtnMsg = szLogMsg;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				}
				
				if( !szYD_EQP_ID.equals("") ) {
					outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
					blnRtnVal = YdCommonUtils.chkGetCrnSpec(szYD_EQP_ID, outRecSet);
					if( !blnRtnVal ) {
						szLogMsg = "크레인사양 조회 시 에러 발생";
						szRtnMsg = szLogMsg;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					}else{
						outRecSet.first();
						recTemp = outRecSet.getRecord();
						//크레인 작업허용 중량
						lngYD_WRK_ABLE_WT = ydDaoUtils.paraRecChkNullLong(recTemp,"YD_WRK_ABLE_WT");
						//크레인 집게허용 오차
						intYD_CRN_TONG_W_TOL = ydDaoUtils.paraRecChkNullInt(recTemp,"YD_CRN_TONG_W_TOL");
						//크레인 작업가능 매수
						intYD_WRK_ABLE_SH = ydDaoUtils.paraRecChkNullInt(recTemp,"YD_WRK_ABLE_SH");
						szLogMsg = "크레인 작업허용 중량 = " + lngYD_WRK_ABLE_WT + ", 크레인 집게허용 오차 = " + intYD_CRN_TONG_W_TOL + ", 크레인 작업가능 매수 = " + intYD_WRK_ABLE_SH;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						if( lngSumWeight > lngYD_WRK_ABLE_WT ) {
							szLogMsg = "주작업 대상재 총중량[" + lngSumWeight + "]이 크레인[" + szYD_EQP_ID + "]의 작업허용 중량[" + lngYD_WRK_ABLE_WT + "]을 초과했습니다.";
							szRtnMsg = szLogMsg;
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						}else if( intMtlCount > intYD_WRK_ABLE_SH ) {
							szLogMsg = "주작업 대상재 매수[" + intMtlCount + "]가 크레인[" + szYD_EQP_ID + "]의 작업가능매수[" + intYD_WRK_ABLE_SH + "]를 초과했습니다.";
							szRtnMsg = szLogMsg;
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						}else{
							szLogMsg = "주작업 대상재 매수[" + intMtlCount + "]에 대해서 크레인[" + szYD_EQP_ID + "]이 작업가능합니다 .";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							szRtnMsg = "Success";
						}
					}
				}
				
				gdRes = OperateGridData.cloneResponseGridData(gdReq);
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setStatus("true");
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				
				try {
					szRtnMsg = e.getMessage();
					gdRes = OperateGridData.cloneResponseGridData(gdReq);
					gdRes = CmUtil.copyGDParam(gdReq, gdRes);
					szLogMsg = "에러메세지 : " + e.getMessage();
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					gdRes.setStatus("true");
					gdRes.setMessage(szRtnMsg);
				}catch(Exception ex) {
					szLogMsg = "에러메세지 : " + ex.getMessage();
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					throw new JDTOException(getClass().getName() + e.getMessage(), e);
				}
			}			
			
			szLogMsg = "JSP-FACADE [준비스케줄화면에서 선택된 베드의 대상재들이 크레인의 작업가능한 매수인 지를 판단 ] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			return gdRes;
		} // end of chkIfCrnWorkable()
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/**
		 *  예약 베드 조회
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData getBedPlanPos(GridData gdReq) throws JDTOException {

			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			
			String szMethodName="getBedPlanPos";		
			String szLogMsg = "";
			
			try{
				
				szLogMsg = "JSP-FACADE [예약 베드 조회]시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	

				
				JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getBedPlanPos", inRecord);				
				gdRes = CmUtil.genGridData(gdReq , recordSet);
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [예약 베드 조회]끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
			
		
		} // end of getBedPlanPos()
		
		
		/**
		 *  야드 주편공통, 슬라브공통 조회
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData getPtCommStock(GridData gdReq) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			
			String szMethodName="getPtCommStock";		
			String szLogMsg = "";
			
			try{
				
				szLogMsg = "JSP-FACADE [야드 주편공통, 슬라브공통 조회]시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
				JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getPtCommStock", inRecord);				
				gdRes = CmUtil.genGridData(gdReq , recordSet);
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [야드 주편공통, 슬라브공통 조회]끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
			
		
		} // end of getBedPlanPos()
		
		
		
		/**
		 *  검수완료 기능
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData inspectionComplete(GridData gdReq) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;			
			String        szRcvMsg = "";
			
			String szMethodName="inspectionComplete";		
			String szLogMsg = "";
			
			try{
				szLogMsg = "JSP-FACADE [검수완료 기능]시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
				JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
				ejbConn = new EJBConnector("default", this);				
				szRcvMsg = (String) ejbConn.trx("YdJspCommonSeEJB", "inspectionComplete", inRecord);
				
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setStatus("true");
				gdRes.setMessage(szRcvMsg);	
				
				szLogMsg = "JSP-FACADE [검수완료 기능]끝";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				return gdRes;
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
		} // end of inspectionComplete()
		
		/**
		 * 한 건의 차량스케줄 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param gdReq
		 * @return
		 * @throws JDTOException
		 */
		public GridData getCarSchById(GridData gdReq) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szLogMsg = null;
			String szMethodName = "getCarSchById";
			String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
			try{
				
				szLogMsg = "JSP-FACADE [차량스케줄 조회]시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
				JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getCarSchById", inRecord);				
				gdRes = CmUtil.genGridData(gdReq , recordSet);
				gdRes.setStatus("true");
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){		
				try {
					szRtnMsg = e.getMessage();
					gdRes = OperateGridData.cloneResponseGridData(gdReq);
					gdRes = CmUtil.copyGDParam(gdReq, gdRes);
					szLogMsg = "[JSP Session] 한 건의 차량스케줄 조회 - 에러메세지 : " + e.getMessage();
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					gdRes.setStatus("true");
					gdRes.setMessage(szRtnMsg);
				}catch(Exception ex) {
					szLogMsg = "[JSP Session] 한 건의 차량스케줄 조회 - 에러메세지 : " + ex.getMessage();
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					throw new JDTOException(getClass().getName() + e.getMessage(), e);
				}
			}
			
			szLogMsg = "JSP-FACADE [차량스케줄 조회] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			
			return gdRes;
		}
		
		
		/**
		 * 차량스케줄/차량Point삭제
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData delCarSchNCarPoint(GridData inDto) throws JDTOException {
			//		LOG
			String szMsg 				= "";
			String szMethodName			= "delCarSchNCarPoint";
			String szOperationName 		= "차량스케줄/차량Point삭제";
				
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			String szRtnMsg = "";
			
			try{
				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
				
				
				szMsg = "[JSP Facade] " + szOperationName + " 시작  ==>";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
				
				
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				szRtnMsg = (String)ejbConn.trx("delCarSchNCarPoint", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(inDto);
				gdRes = CmUtil.copyGDParam(inDto, gdRes);
				
				
				szMsg = "[JSP Facade] " + szOperationName + "  끝";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
				
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				try {
				gdRes = OperateGridData.cloneResponseGridData(inDto);
				gdRes = CmUtil.copyGDParam(inDto, gdRes);
				gdRes.setMessage(YdConstant.RETN_CD_FAILURE);
				}catch(Exception ex) {
					szMsg = "[JSP Facade] " + szOperationName + "  오류발생2 : " + ex.getMessage();
					ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
					throw new JDTOException(getClass().getName() + e.getMessage(), e);
				}
				
			}
			return gdRes;
		}  //end of delCarSchNCarPoint
		
		/**
		 * 차량출발처리
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData procLeaveCar(GridData inDto) throws JDTOException {
			//		LOG
			String szMsg 				= "";
			String szMethodName			= "procLeaveCar";
			String szOperationName 		= "차량출발처리";
				
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			String szRtnMsg = "";
			
			try{
				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
				
				
				szMsg = "[JSP Facade] " + szOperationName + " 시작  ==>";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
				
				
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				szRtnMsg = (String)ejbConn.trx("procLeaveCar", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(inDto);
				gdRes = CmUtil.copyGDParam(inDto, gdRes);
				
				
				szMsg = "[JSP Facade] " + szOperationName + "  끝";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
				
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				try {
				gdRes = OperateGridData.cloneResponseGridData(inDto);
				gdRes = CmUtil.copyGDParam(inDto, gdRes);
				gdRes.setMessage(YdConstant.RETN_CD_FAILURE);
				}catch(Exception ex) {
					szMsg = "[JSP Facade] " + szOperationName + "  오류발생2 : " + ex.getMessage();
					ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
					throw new JDTOException(getClass().getName() + e.getMessage(), e);
				}
				
			}
			return gdRes;
		}  //end of procLeaveCar
		
		
		/**
		 * 입동지시
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData procBayInWo(GridData inDto) throws JDTOException {
			//		LOG
			String szMsg 				= "";
			String szMethodName			= "procBayInWo";
			String szOperationName 		= "입동지시";
				
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			String szRtnMsg = "";
			
			try{
				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
				
				
				szMsg = "[JSP Facade] " + szOperationName + " 시작  ==>";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
				
				
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				szRtnMsg = (String)ejbConn.trx("procBayInWo", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(inDto);
				gdRes = CmUtil.copyGDParam(inDto, gdRes);
				
				
				szMsg = "[JSP Facade] " + szOperationName + "  끝";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
				
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				try {
				gdRes = OperateGridData.cloneResponseGridData(inDto);
				gdRes = CmUtil.copyGDParam(inDto, gdRes);
				gdRes.setMessage(YdConstant.RETN_CD_FAILURE);
				}catch(Exception ex) {
					szMsg = "[JSP Facade] " + szOperationName + "  오류발생2 : " + ex.getMessage();
					ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
					throw new JDTOException(getClass().getName() + e.getMessage(), e);
				}
				
			}
			return gdRes;
		}  //end of procBayInWo
		
		
		/**
		 * 운송지시일자,순번,차량번호,카드번호 그룹핑
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData getDmCarTrnsOrdDatSeqNoGroup(GridData inDto) throws JDTOException {
			//LOG
			String szMsg        = "";
			String szMethodName = "getDmCarTrnsOrdDatSeqNoGroup";
			String szOperationName = "운송지시일자,순번,차량번호,카드번호 그룹핑";
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				szMsg = "[JSP Facede] " + szOperationName + " 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getDmCarTrnsOrdDatSeqNoGroup", inRecord);
				gdRes = CmUtil.genGridData(inDto , recordSet);
				szMsg = "[JSP Facede] " + szOperationName + " 완료 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}catch(Exception e){
				szMsg = "[JSP Facede] " + szOperationName + " 시 오류발생 - " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
		} //end of getDmCarTrnsOrdDatSeqNoGroup
		
		
		/**
		 * 운송지시일자,순번,차량번호,카드번호 그룹핑
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData getDmCarTrnsOrdDatSeqNoGroup2(GridData inDto) throws JDTOException {
			//LOG
			String szMsg        = "";
			String szMethodName = "getDmCarTrnsOrdDatSeqNoGroup2";
			String szOperationName = "운송지시일자,순번,차량번호,카드번호 그룹핑";
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				szMsg = "[JSP Facede] " + szOperationName + " 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getDmCarTrnsOrdDatSeqNoGroup2", inRecord);
				gdRes = CmUtil.genGridData(inDto , recordSet);
				szMsg = "[JSP Facede] " + szOperationName + " 완료 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}catch(Exception e){
				szMsg = "[JSP Facede] " + szOperationName + " 시 오류발생 - " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
		} //end of getDmCarTrnsOrdDatSeqNoGroup2
		
		/**
		 * 야드관리 > 코일제품창고 > 출하관리 > 출하차량상차LOT List  (목록조회)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData getDmCarLotList(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getDmCarLotList";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getDmCarLotList", new Class[] { GridData.class }, new Object[] { inDto });
				
			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}
			
			
			return gdRes;
			
		} //end of getDmCarLotList
		
		
		
		
		/**
		 * 출하차량도착처리
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData procDmCarArr(GridData inDto) throws JDTOException {
			//		LOG
			String szMsg = "";
			String szMethodName="procDmCarArr";
			String szOperationName = "출하차량도착처리";
			ymCommonDAO dao = ymCommonDAO.getInstance();	
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			String szRtnMsg = "";
			String szyd_gp  ="";
			try{
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				
				
				szMsg = "[JSP Facade] " + szOperationName + " 시작  ==>";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
				
				
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				
//				장애 발생시 이전 소스로 원복 하기 위한 조치
				String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.chklist";
			    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});

			    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
		    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
		    	if(CHK.equals("Y")){
		    		szyd_gp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");
					
		    		ydUtils.putLog(szSessionName, szMethodName , "YD_GP=>" + szyd_gp , YdConstant.DEBUG);
		    		
					if(szyd_gp.equals("S")){					
						szRtnMsg = (String)ejbConn.trx("YdJspCommonSeEJB", "procDmCarArrS", inRecord);
					}else {
						szRtnMsg = (String)ejbConn.trx("YdJspCommonSeEJB", "procDmCarArr", inRecord);
					}
		    	}else{
		    		szRtnMsg = (String)ejbConn.trx("YdJspCommonSeEJB", "procDmCarArr", inRecord);
		    	}
				
				
				gdRes = OperateGridData.cloneResponseGridData(inDto);
				gdRes = CmUtil.copyGDParam(inDto, gdRes);
				
				
				szMsg = "[JSP Facade] " + szOperationName + "  끝";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
				
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				try {
				gdRes = OperateGridData.cloneResponseGridData(inDto);
				gdRes = CmUtil.copyGDParam(inDto, gdRes);
				gdRes.setMessage(YdConstant.RETN_CD_FAILURE);
				}catch(Exception ex) {
					szMsg = "[JSP Facade] " + szOperationName + "  오류발생2 : " + ex.getMessage();
					ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
					throw new JDTOException(getClass().getName() + e.getMessage(), e);
				}
				
			}
			return gdRes;
		}  //end of procDmCarArr
		
		/**
		 * 재료이력정보 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getYdWrkHistDaoStlNo(GridData inDto) throws JDTOException {

			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			
			String szMethodName="getYdWrkHistDaoStlNo";		
			String szLogMsg = "";
			
			try{
				
				szLogMsg = "JSP-FACADE [재료이력정보 조회]시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getYdWrkHistDaoStlNo", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");	
			
			szLogMsg = "JSP-FACADE [재료이력정보 조회]끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		/**
		 * 적치열의 동정보 or 스판정보 or 열정보를 조회하는 메소드
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData getCodeForStkCol(GridData inDto) throws JDTOException {
			String szOperationName	= "동정보/스판정보/열조회";
			String szMethodName		= "getCodeForStkCol";
			String szMsg			= null;
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				
				szMsg = "JSP-FACADE ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getCodeForStkCol", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);		
			
			szMsg = "JSP-FACADE ["+szOperationName+"]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			
			return gdRes;
		}
		
		/**
		 * 적치열의 동정보 or 스판정보 or 열정보를 조회하는 메소드
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData getCodeForStkCol2(GridData inDto) throws JDTOException {
			String szOperationName	= "동정보/스판정보/열조회";
			String szMethodName		= "getCodeForStkCol2";
			String szMsg			= null;
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				
				szMsg = "JSP-FACADE ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getCodeForStkCol2", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);		
			
			szMsg = "JSP-FACADE ["+szOperationName+"]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			
			return gdRes;
		}
		
		
		/***
		 *  완료 처리(작업완료처리) 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto (작업예약 번호 (YD_WBOOK_ID] )
		 * @return
		 * @throws JDTOException
		 */
		 
		public GridData procAllWrkCmp(GridData inDto) throws JDTOException {
			//		LOG
			String szMsg = "";
			String szMethodName="procCarLdCmp";
			String szOperationName = "작업차완료처리";
				
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			String szRtnMsg = "";
			
			try{
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				
				
				szMsg = "[JSP Facade] " + szOperationName + " 시작  ==>";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
				
				
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				szRtnMsg = (String)ejbConn.trx("YdJspCommonSeEJB", "procAllWrkCmp", inRecord);
				gdRes = OperateGridData.cloneResponseGridData(inDto);
				gdRes = CmUtil.copyGDParam(inDto, gdRes);
				
				
				szMsg = "[JSP Facade] " + szOperationName + "  끝";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
				
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				try {
				gdRes = OperateGridData.cloneResponseGridData(inDto);
				gdRes = CmUtil.copyGDParam(inDto, gdRes);
				gdRes.setMessage(YdConstant.RETN_CD_FAILURE);
				}catch(Exception ex) {
					szMsg = "[JSP Facade] " + szOperationName + "  오류발생2 : " + ex.getMessage();
					ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
					throw new JDTOException(getClass().getName() + e.getMessage(), e);
				}
				
			}
			return gdRes;
		}  //end of procCarLdCmp
		
		
		

		/**
		 *  상차정보조회 - 상차위치 수정
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		
		
		public GridData carLiftPosSet(GridData gdReq) throws JDTOException {
			//LOG
			String szOperationName = "상차위치 수정";
			String szMethodName = "carLiftPosSet";
			String szLogMsg = null;
			String szRtnMsg = null;
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			

			try{
				
				
				szLogMsg = "[JSP Facade] " + szOperationName + " 시작  ==>";
				ydUtils.putLog(szSessionName, szMethodName , szLogMsg , YdConstant.DEBUG);
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx("carLiftPosSet", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
				

				szLogMsg = "[JSP Facade] " + szOperationName + "  끝";
				ydUtils.putLog(szSessionName, szMethodName , szLogMsg , YdConstant.DEBUG);
				
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]상차정보조회 - 상차위치 수정 에러발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}
			gdRes.setStatus("true");
			return gdRes;
		}
		
		
		

		/**
		 *  상차정보조회 - 재료정보삭제
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		
		
		public GridData carLiftStlDelete(GridData gdReq) throws JDTOException {
			//LOG
			String szOperationName = "재료정보삭제";
			String szMethodName = "carLiftStlDelete";
			String szLogMsg = null;
			String szRtnMsg = null;
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			

			try{
				
				
				szLogMsg = "[JSP Facade] " + szOperationName + " 시작  ==>";
				ydUtils.putLog(szSessionName, szMethodName , szLogMsg , YdConstant.DEBUG);
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx("carLiftStlDelete", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
				

				szLogMsg = "[JSP Facade] " + szOperationName + "  끝";
				ydUtils.putLog(szSessionName, szMethodName , szLogMsg , YdConstant.DEBUG);
				
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]상차정보조회 - 재료정보삭제 에러발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}
			gdRes.setStatus("true");
			return gdRes;
		}
		
		

		/**
		 *  상차정보조회 - 재료정보삭제
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		
		
		public GridData carLiftStlInsert(GridData gdReq) throws JDTOException {
			//LOG
			String szOperationName = "재료정보등록";
			String szMethodName = "carLiftStlInsert";
			String szLogMsg = null;
			String szRtnMsg = null;
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			

			try{
								
				szLogMsg = "[JSP Facade] " + szOperationName + " 시작  ==>";
				ydUtils.putLog(szSessionName, szMethodName , szLogMsg , YdConstant.DEBUG);
				
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx("carLiftStlInsert", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
				
				
				szLogMsg = "[JSP Facade] " + szOperationName + "  끝";
				ydUtils.putLog(szSessionName, szMethodName , szLogMsg , YdConstant.DEBUG);
				
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]상차정보조회 -재료정보등록 에러발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}
			gdRes.setStatus("true");
			return gdRes;
		}
		
		
		
		
		/**
		 *  상차정보조회 - 상차초기화
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		
		
		public GridData carLiftStatInit(GridData gdReq) throws JDTOException {
			//LOG
			String szOperationName = "상차초기화";
			String szMethodName = "carLiftStatInit";
			String szLogMsg = null;
			String szRtnMsg = null;
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			

			try{
								
				szLogMsg = "[JSP Facade] " + szOperationName + " 시작  ==>";
				ydUtils.putLog(szSessionName, szMethodName , szLogMsg , YdConstant.DEBUG);
				
				
				
				JDTORecord inRecord1 = CmUtil.genJDTORecord(gdReq);
				ejbConn = new EJBConnector("default", this);				
				ejbConn.trx("YdJspCommonSeEJB", "carLiftStatInit0", inRecord1);				
								
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx("carLiftStatInit", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
				
				
				szLogMsg = "[JSP Facade] " + szOperationName + "  끝";
				ydUtils.putLog(szSessionName, szMethodName , szLogMsg , YdConstant.DEBUG);
				
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]상차초기화 -에러발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}
			gdRes.setStatus("true");
			return gdRes;
		}
		
		
		
		
		
		
		/**
		 * 차량상차정보 조회 - 이송재료에 등록된 재료정보 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData getStlInfobyMoveStl(GridData inDto) throws JDTOException {
			String szOperationName	= "이송재료에 등록된 재료정보 조회";
			String szMethodName		= "getStlInfobyMoveStl";
			String szMsg			= null;
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				
				szMsg = "JSP-FACADE ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getStlInfobyMoveStl", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);		
			
			szMsg = "JSP-FACADE ["+szOperationName+"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		
		/**
		 * 적치열정보로 BED정보를 조회하는 메소드
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData getBedNoByStkCol(GridData inDto) throws JDTOException {
			String szOperationName	= "BED조회";
			String szMethodName		= "getBedNoByStkCol";
			String szMsg			= null;
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				
				szMsg = "JSP-FACADE ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getBedNoByStkCol", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);		
			
			szMsg = "JSP-FACADE ["+szOperationName+"]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		
		/**
		 * BED정보로 주작업구분들을 조회하는 메소드
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData getBedInfoByGrpNm(GridData inDto) throws JDTOException {
			String szOperationName	= "주작업구분BED조회";
			String szMethodName		= "getBedInfoByGrpNm";
			String szMsg			= null;
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				
				szMsg = "JSP-FACADE ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getBedInfoByGrpNm", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);		
			
			szMsg = "JSP-FACADE ["+szOperationName+"]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return gdRes;
		}


		
		/**
		 * 대차 작업 예약 정보를 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData getWorkBookMtlByTcarSchWBookID(GridData inDto) throws JDTOException {
			String szOperationName	= "대차 작업 예약 정보를 조회";
			String szMethodName		= "getWorkBookMtlByTcarSchWBookID";
			String szMsg			= null;
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				
				szMsg = "JSP-FACADE ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", szMethodName, inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);		
			
			szMsg = "JSP-FACADE ["+szOperationName+"]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		
		
		/**
		 * 대차 작업 예약 정보를 조회(COIL)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData getWorkBookMtlByTcarSchWBookID2(GridData inDto) throws JDTOException {
			String szOperationName	= "대차 작업 예약 정보를 조회";
			String szMethodName		= "getWorkBookMtlByTcarSchWBookID2";
			String szMsg			= null;
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				
				szMsg = "JSP-FACADE ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", szMethodName, inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);		
			
			szMsg = "JSP-FACADE ["+szOperationName+"]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return gdRes;
		}

		

		
		
		/**
		 * 야드구분(YD_GP1, YD_GP2)으로 설비ID와   설비명 조회
		 * 
		 * 권오창
		 * 2009.11.11
		 * 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */ 
		public GridData getEqpIDEqpNameList(GridData inDto) throws JDTOException {
			// 객체 선언
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			
			// 레코드 선언
			JDTORecordSet recordSet = null;
			
			// 변수 선언
			String szMethodName     = "getEqpIDEqpNameList";
			String szMsg            = "";
			
			
			try{
				szMsg = "JSP-FACADE [설비ID와   설비명  목록 조회] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				// 레코드 생성
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				
				// EJB Connector 생성 및 호출
				ejbConn = new EJBConnector("default", this);
				recordSet = (JDTORecordSet)ejbConn.trx("YdJspCommonSeEJB", "getEqpIDEqpNameList", inRecord);
				
				// 호출 결과를 GridData 타입으로 반환
				gdRes = CmUtil.genGridData(inDto, recordSet);
				
				szMsg = "JSP-FACADE [설비ID와   설비명  목록 조회] 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szMethodName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);	
				throw new JDTOException(getClass().getName() + e.getMessage(), e);			
			}
			
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
		}
		
		
		
		
		
		/**
		 * 야드구분(YD_GP1, YD_GP2)과 동구분(YD_BAY_GP)으로 설비ID와   설비명 조회
		 * 
		 * 권오창
		 * 2009.11.18
		 * 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */ 
		public GridData getEqpIDEqpNameListYdGpYdBayGp(GridData inDto) throws JDTOException {
			// 객체 선언
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			
			// 레코드 선언
			JDTORecordSet recordSet = null;
			
			// 변수 선언
			String szMethodName     = "getEqpIDEqpNameListYdGpYdBayGp";
			String szMsg            = "";
			
			
			try{
				szMsg = "JSP-FACADE [설비ID와   설비명  목록 조회] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				// 레코드 생성
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				
				// EJB Connector 생성 및 호출
				ejbConn = new EJBConnector("default", this);
				recordSet = (JDTORecordSet)ejbConn.trx("YdJspCommonSeEJB", "getEqpIDEqpNameListYdGpYdBayGp", inRecord);
				
				// 호출 결과를 GridData 타입으로 반환
				gdRes = CmUtil.genGridData(inDto, recordSet);
				
				szMsg = "JSP-FACADE [설비ID와   설비명  목록 조회] 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szMethodName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);	
				throw new JDTOException(getClass().getName() + e.getMessage(), e);			
			}
			
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
		}

		
		
		
		
		public GridData getBedNoByStkColNo(GridData inDto) throws JDTOException {
			String szOperationName	= "BED조회";
			String szMethodName		= "getBedNoByStkColNo";
			String szMsg			= null;
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				
				szMsg = "JSP-FACADE ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getBedNoByStkCol", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);		
			
			szMsg = "JSP-FACADE ["+szOperationName+"]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		

		/**
		 *  대차 작업 예약 순서 변경
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		
		
		public GridData tCarSchChangeSequence(GridData gdReq) throws JDTOException {
			//LOG
			String szOperationName = " 대차 작업 예약 순서 변경";
			String szMethodName = "tCarSchChangeSequence";
			String szLogMsg = null;
			String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			

			try{
				
				szLogMsg = "[JSP Facade] " + szOperationName + " 시작  ==>";
				ydUtils.putLog(szSessionName, szMethodName , szLogMsg , YdConstant.DEBUG);
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx("tCarSchChangeSequence", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
				

				szLogMsg = "[JSP Facade] " + szOperationName + "  끝";
				ydUtils.putLog(szSessionName, szMethodName , szLogMsg , YdConstant.DEBUG);
				
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade] "+ szOperationName + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}
			gdRes.setStatus("true");
			return gdRes;
		}
		
		
		
		
		/**
		 *   대차 작업 취소 
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData tCarCancleWBook(GridData gdReq) throws JDTOException {
			//LOG
			String szOperationName = " 대차 작업 취소 ";
			String szMethodName = "tCarCancleWBook";
			String szLogMsg = null;
			String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			

			try{
				
				szLogMsg = "[JSP Facade] " + szOperationName + " 시작  ==>";
				ydUtils.putLog(szSessionName, szMethodName , szLogMsg , YdConstant.DEBUG);
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx("tCarCancleWBook", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
				

				szLogMsg = "[JSP Facade] " + szOperationName + "  끝";
				ydUtils.putLog(szSessionName, szMethodName , szLogMsg , YdConstant.DEBUG);
				
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade] "+ szOperationName + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}
			gdRes.setStatus("true");
			return gdRes;
		}
		
		/**
		 * 대차스케줄삭제
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData delTcarSch(GridData gdReq) throws JDTOException {
			String szOperationName 			= "대차스케줄삭제";
			String szMethodName 			= "delTcarSch";
			String szLogMsg 				= null;
			String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
			GridData gdRes 					= null;
			EJBConnector ejbConn 			= null;
			

			try{
				
				szLogMsg = "[JSP Facade : " + szOperationName + "] -------------- 메소드 시작 --------------";
				ydUtils.putLog(szSessionName, szMethodName , szLogMsg , YdConstant.DEBUG);

				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx("delTcarSch", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				gdRes.setMessage(szRtnMsg);
				
				szLogMsg = "[JSP Facade : " + szOperationName + "] -------------- 메소드 끝 --------------";
				ydUtils.putLog(szSessionName, szMethodName , szLogMsg , YdConstant.DEBUG);
				
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade : " + szOperationName + "] 예외발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			return gdRes;
		}
		
		
		/**
		 * 수요가 코드 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getDemanderCdList(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMsg = "";
			String szMethodName="getDemanderCdList";
			try{
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				
				ejbConn = new EJBConnector("default", this);
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getDemanderCdList", inRecord);			                                                          
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				szMsg = e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
		}
		
		/**
		 * 목적지 코드 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOExceptiongetDestCdList
		 */
		public GridData getDestCdList(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMsg = "";
			String szMethodName="getDestCdList";
			try{
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				
				ejbConn = new EJBConnector("default", this);
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getDestCdList", inRecord);			                                                          
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				szMsg = e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
		}
		
		/**
		 * 고객사 코드 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getCustCdList(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMsg = "";
			String szMethodName="getCustCdList";
			try{
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				
				ejbConn = new EJBConnector("default", this);
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getCustCdList", inRecord);			                                                          
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				szMsg = e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
		}
		
		
		/**
		 * 선박 코드 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getShipCdList(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMsg = "";
			String szMethodName="getShipCdList";
			try{
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				
				ejbConn = new EJBConnector("default", this);
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getShipCdList", inRecord);			                                                          
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				szMsg = e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
		}		
		
		
		/**
		 * 가동율 분석
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getYdWrkAnalysis(GridData inDto) throws JDTOException {
			String szOperationName	= "가동율 분석";
			String szMethodName		= "getYdWrkAnalysis";
			String szMsg			= null;
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				
				szMsg = "JSP-FACADE ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getYdWrkAnalysis", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);		
			
			szMsg = "JSP-FACADE ["+szOperationName+"]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		/**
		 * 가동율 분석
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getYdWrkAnalysisForPlate(GridData inDto) throws JDTOException {
			String szOperationName	= "가동율 분석";
			String szMethodName		= "getYdWrkAnalysisForPlate";
			String szMsg			= null;
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				
				szMsg = "JSP-FACADE ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getYdWrkAnalysisForPlate", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);		
			
			szMsg = "JSP-FACADE ["+szOperationName+"]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		
		
		/**
		 * 스케줄 분석
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getYdWrkAnalysisBySchCd(GridData inDto) throws JDTOException {
			String szOperationName	= "스케줄 분석";
			String szMethodName		= "getYdWrkAnalysisBySchCd";
			String szMsg			= null;
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				
				szMsg = "JSP-FACADE ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getYdWrkAnalysisBySchCd", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
				
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);		
			
			szMsg = "JSP-FACADE ["+szOperationName+"]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		/**
		 * 후판슬라브 이상재 이력관리
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getYdWrkAnalysisByAbSlab(GridData inDto) throws JDTOException {
			String szOperationName	= "후판슬라브 이상재 이력관리";
			String szMethodName		= "getYdWrkAnalysisByAbSlab";
			String szMsg			= null;
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				
				szMsg = "JSP-FACADE ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getYdWrkAnalysisByAbSlab", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
				
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);		
			
			szMsg = "JSP-FACADE ["+szOperationName+"]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		/**
		 * 크레인작업실적현황(야드관리 > 코일소재야드 > 크레인실적관리 > 크레인 작업실적현황)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws DAOException
		 * @throws JDTOException
		 */
		public GridData getCrnWrkWrStat(GridData inDto) throws JDTOException {
			String szOperationName	= "크레인작업실적현황";
			String szMethodName		= "getCrnWrkWrStat";
			String szMsg			= null;
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				
				szMsg = "JSP-FACADE ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getCrnWrkWrStat", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
				
			}catch(Exception e){
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);		
			
			szMsg = "JSP-FACADE ["+szOperationName+"]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		/**
		 * 적치 가능 번지 리스트 조회 (select box용  소재,제품 공통 )
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @작성자 : 박지열
		 * @작성일 : 2010.07.14
		 */
		public GridData getUsableBedList(GridData inDto){
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getUsableBedList";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getUsableBedList", new Class[] { GridData.class }, new Object[] { inDto });

			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}


			return gdRes;
		} 
		
		/**
		 * 후판정정야드 북아웃 대상재조회 (select box용)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdBookoutSltList(GridData inDto){
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getpPlateYdBookoutSltList";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getpPlateYdBookoutSltList", new Class[] { GridData.class }, new Object[] { inDto });

			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}


			return gdRes;
		} 
		
		
		/**
		 * 후판정정야드 BED 조회 (select box용)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdBedList(GridData inDto){
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getpPlateYdBedList";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getpPlateYdBedList", new Class[] { GridData.class }, new Object[] { inDto });

			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}


			return gdRes;
		} 
		
		
		
		
		
		/**
		 * 후판정정야드 BED 조회 (select box용)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdBedList99(GridData inDto){
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getpPlateYdBedList99";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getpPlateYdBedList99", new Class[] { GridData.class }, new Object[] { inDto });

			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}


			return gdRes;
		} 
		
		
		/**
		 * 후판정정야드 BED 조회 (select box용)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdBedList2(GridData inDto){ 
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getpPlateYdBedList2";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getpPlateYdBedList2", new Class[] { GridData.class }, new Object[] { inDto });

			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}


			return gdRes;
		} 
		
		
		
		
		/**
		 * 후판정정야드 BED 조회 (select box용)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdBedList3(GridData inDto){ 
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getpPlateYdBedList3";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getpPlateYdBedList3", new Class[] { GridData.class }, new Object[] { inDto });

			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}


			return gdRes;
		} 
		
		
		
		/**
		 * 후판정정야드 Layer 조회 (select box용)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdLayerList(GridData inDto){
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getpPlateYdLayerList";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getpPlateYdLayerList", new Class[] { GridData.class }, new Object[] { inDto });

			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}


			return gdRes;
		}
		
		
		/**
		 * 후판정정야드 Layer 조회 (select box용)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdLayerList_L(GridData inDto){
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getpPlateYdLayerList_L";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getpPlateYdLayerList_L", new Class[] { GridData.class }, new Object[] { inDto });

			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}


			return gdRes;
		}
		
		
		
		/**
		 * 후판정정야드 위치조회 (select box용)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdLocationList(GridData inDto){
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getpPlateYdLocationList";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getpPlateYdLocationList", new Class[] { GridData.class }, new Object[] { inDto });

			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}


			return gdRes;
		}
		
		
		/**
		 * 후판정정야드 위치조회 (select box용)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdLocationList2(GridData inDto){
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getpPlateYdLocationList2";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getpPlateYdLocationList", new Class[] { GridData.class }, new Object[] { inDto });

			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}


			return gdRes;
		}
		
		/**
		 * 후판정정야드 Layer 조회 (select box용)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdLayerList2(GridData inDto){
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getpPlateYdLayerList2";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getpPlateYdLayerList2", new Class[] { GridData.class }, new Object[] { inDto });

			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}


			return gdRes;
		} 
		
		
		
		
		/**
		 * 후판정정야드 북아웃 대상재 상세조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdBookoutSltDtlList(GridData inDto){
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getpPlateYdBookoutSltDtlList";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getpPlateYdBookoutSltDtlList", new Class[] { GridData.class }, new Object[] { inDto });

			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}


			return gdRes;
		} 
		
		
		/**
		 * 후판정정야드 적치현황 상세조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdStlList(GridData inDto){
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getpPlateYdStlList";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getpPlateYdStlList", new Class[] { GridData.class }, new Object[] { inDto });

			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}


			return gdRes;
		} 
		
		
		
		
		/**
		 * 후판정정야드 북아웃 야드조회 (select box용)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdBookoutYdList(GridData inDto){
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getpPlateYdBookoutYdList";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getpPlateYdBookoutYdList", new Class[] { GridData.class }, new Object[] { inDto });

			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}


			return gdRes;
		} 



		
		/**
		 * 야드관리 > 코일소재야드 / 코일제품창고 > 저장관리 > 이적작업진행관리(목록리스트 조회->대상재 )
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param GridData
		 * @return GridData
		 * @작성자 : 박지열
		 * @작성일 : 2010.07.19
		 */
		public GridData getMvstkProgMgtList(GridData inDto){
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getMvstkProgMgtList";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getMvstkProgMgtList", new Class[] { GridData.class }, new Object[] { inDto });

			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}


			return gdRes;
		} 
		
		/**
		 * 야드관리 > 코일소재야드 / 코일제품창고 > 저장관리 > 이적작업진행관리(목록리스트 조회 -> 작업진행분 )
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param GridData
		 * @return GridData
		 * @작성자 : 박지열
		 * @작성일 : 2010.07.19
		 */
		public GridData getMvstkProgMgtWorkList(GridData inDto){
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getMvstkProgMgtWorkList";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getMvstkProgMgtWorkList", new Class[] { GridData.class }, new Object[] { inDto });
				
			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}
			
			
			return gdRes;
		} 
		
		/**
		 * 야드관리 > 코일소재야드 / 코일제품창고 > 저장관리 > 이적작업진행관리(동별 이적/이송건수 조회 -> 대상재 )
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param GridData
		 * @return GridData
		 * @작성자 : 박지열
		 * @작성일 : 2010.07.19
		 */
		public GridData getMvstkProgMgtBayCnt(GridData inDto){
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getMvstkProgMgtBayCnt";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getMvstkProgMgtBayCnt", new Class[] { GridData.class }, new Object[] { inDto });
				
			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}
			
			
			return gdRes;
		} 
		
		/**
		 * 야드관리 > 코일소재야드 / 코일제품창고 > 저장관리 > 이적작업진행관리(동별 이적/이송건수 조회 -> 작업진행분 )
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param GridData
		 * @return GridData
		 * @작성자 : 박지열
		 * @작성일 : 2010.07.19
		 */
		public GridData getMvstkProgMgtWorkBayCnt(GridData inDto){
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			String szMethodName="getMvstkProgMgtWorkBayCnt";
			try{
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				gdRes = (GridData)ejbConn.trx("getMvstkProgMgtWorkBayCnt", new Class[] { GridData.class }, new Object[] { inDto });
				
			}catch(Exception e){		
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}
			
			
			return gdRes;
		} 
		
		
		
		/**
		 * LOT ID 조회하는 메소드
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData getCodeForLot(GridData inDto) throws JDTOException {
			String szOperationName	= "LOT ID조회";
			String szMethodName		= "getCodeForLot";
			String szMsg			= null;
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				
				szMsg = "JSP-FACADE ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getCodeForLot", inRecord);
				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);		
			
			szMsg = "JSP-FACADE ["+szOperationName+"]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			
			return gdRes;
		}
		
		/**
		 * [A] 오퍼레이션명 : MachineScarfint실적조회
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @throws DAOException 모든 예외
		 */
		public GridData getMachineScarfingWr(GridData gdReq) throws DAOException {
			try {
				EJBConnector ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				if ("summary".equals(gdReq.getParam("action_type"))) {
					return (GridData)ejbConn.trx("getMachineScarfingSummary", new Class[] { GridData.class }, new Object[] { gdReq });
				} else {
					return (GridData)ejbConn.trx("getMachineScarfingWr", new Class[] { GridData.class }, new Object[] { gdReq });
				}
			}
			catch (DAOException daoe) {
				throw daoe;
			}
			catch (Exception ex) {
				throw new DAOException(ex);
			}
		}
		
		
		/**
		 * 수요가 그룹핑
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData getDemenderNoGroup(GridData inDto) throws JDTOException {
			//LOG
			String szMsg        = "";
			String szMethodName = "getDemenderNoGroup";
			String szOperationName = "수요가 그룹핑";
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				szMsg = "[JSP Facede] " + szOperationName + " 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getDemenderNoGroup", inRecord);
				gdRes = CmUtil.genGridData(inDto , recordSet);
				szMsg = "[JSP Facede] " + szOperationName + " 완료 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}catch(Exception e){
				szMsg = "[JSP Facede] " + szOperationName + " 시 오류발생 - " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
		} //end of getDemenderNoGroup
		
		
		/**
		 * 운송장비코드 그룹핑
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData getDemenderNoGroup2(GridData inDto) throws JDTOException {
			//LOG
			String szMsg        = "";
			String szMethodName = "getDemenderNoGroup2";
			String szOperationName = "운송장비코드 그룹핑";
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				szMsg = "[JSP Facede] " + szOperationName + " 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getDemenderNoGroup2", inRecord);
				gdRes = CmUtil.genGridData(inDto , recordSet);
				szMsg = "[JSP Facede] " + szOperationName + " 완료 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}catch(Exception e){
				szMsg = "[JSP Facede] " + szOperationName + " 시 오류발생 - " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
		} //end of getDemenderNoGroup2
		
		
		/**
		 *   차량 공통 정보 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getCarInfo(GridData gdReq) throws JDTOException {
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName="getCarInfo";
			String szLogMsg           = "";
			
			try{
				
				szLogMsg = "JSP-FACADE [차량 공통 정보 조회] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getCarInfo", inRecord);				
				gdRes = CmUtil.genGridData(gdReq , recordSet);
			}catch(Exception e){
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [차량 공통 정보 조회] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
			
		}//getCarInfo
		
		/**
		 *	스케쥴코드조회 (화면:크레인작업시지 작성)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData getSchCodeNew(GridData inDto) throws JDTOException {
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			String szMethodName       = "getStrGtrCodeNew";
			String szLogMsg           = "";
			
			
			try{
				szLogMsg = "JSP-FACADE [스케쥴코드조회 (화면:크레인작업시지 작성)] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);				
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getSchCodeNew", inRecord);				
				gdRes = CmUtil.genGridData(inDto , recordSet);
			}catch(Exception e){		
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
			szLogMsg = "JSP-FACADE [스케쥴코드조회 (화면:크레인작업시지 작성)] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		
		/**
		 * 마킹최종고객사코드
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData getMarkingDemenderNo(GridData inDto) throws JDTOException {
			//LOG
			String szMsg        = "";
			String szMethodName = "getMarkingDemenderNo";
			String szOperationName = "마킹최종고객사코드";
			
			GridData      gdRes     = null;
			EJBConnector  ejbConn   = null;
			JDTORecordSet recordSet = null;
			try{
				szMsg = "[JSP Facede] " + szOperationName + " 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
				ejbConn = new EJBConnector("default", this);
				recordSet = (JDTORecordSet) ejbConn.trx("YdJspCommonSeEJB", "getMarkingDemenderNo", inRecord);
				gdRes = CmUtil.genGridData(inDto , recordSet);
				szMsg = "[JSP Facede] " + szOperationName + " 완료 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}catch(Exception e){
				szMsg = "[JSP Facede] " + szOperationName + " 시 오류발생 - " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
		} //end of getMarkingDemenderNo
		
		
		
		///////////////////////////////////////////////////////////////////////////////
		///                          YdJspCoommonFaEJB                              ///
		///////////////////////////////////////////////////////////////////////////////
		
		
		
		///////////////////////////////////////////////////////////////////////////////
		///                          전사물류개선 프로젝트 2021.1.6                  ///
		///////////////////////////////////////////////////////////////////////////////
		/**
		 * 크레인상태관리 - 작업Type변경(유인,무인,리모컨..)
		 *  - 전사물류개선 2021.1.6 기존화면 분리(자동화크레인관련)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData updCrnJobType(GridData gdReq) throws JDTOException {	
			//LOG
			String[] szRtnMsg = null;
			String rtnMsg = YdConstant.RETN_CD_SUCCESS;
			String szMethodName="updCrnJobType";
			String szLogMsg = "";
			String szOperationName	= "설비 작업Type변경 유인/무인/리모컨";
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			try{
				
				szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				szRtnMsg = (String[])ejbConn.trx("updCrnJobType", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);
				
				for(int Loop_i = 0; Loop_i < szRtnMsg.length; Loop_i++ ) {
					if( !szRtnMsg[Loop_i].equals(YdConstant.RETN_CD_SUCCESS) ) {
						rtnMsg = szRtnMsg[Loop_i];
						break;
					}
				}
					
				gdRes.setMessage(rtnMsg);
			}catch(Exception e){
				rtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(rtnMsg);
				ydUtils.putLog(szSessionName, szMethodName, "[JSP-FACADE  - "+ szOperationName  + "]" + e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}		
			gdRes.setStatus("true");
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			return gdRes;	
		} // end of updCmdSelStart()		
		
		/**
		 * 차량스케줄/차량Point삭제
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData delCarSchNCarPoint4G(GridData inDto) throws JDTOException {
			//		LOG
			String szMsg 				= "";
			String szMethodName			= "delCarSchNCarPoint4G";
			String szOperationName 		= "차량스케줄/차량Point삭제";
				
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			String szRtnMsg = "";
			
			try{
				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
				
				
				szMsg = "[JSP Facade] " + szOperationName + " 시작  ==>";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
				
				
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				szRtnMsg = (String)ejbConn.trx("delCarSchNCarPoint4G", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(inDto);
				gdRes = CmUtil.copyGDParam(inDto, gdRes);
				
				
				szMsg = "[JSP Facade] " + szOperationName + "  끝";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
				
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				szMsg = "[JSP Facade] " + szOperationName + "  오류발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				try {
				gdRes = OperateGridData.cloneResponseGridData(inDto);
				gdRes = CmUtil.copyGDParam(inDto, gdRes);
				gdRes.setMessage(YdConstant.RETN_CD_FAILURE);
				}catch(Exception ex) {
					szMsg = "[JSP Facade] " + szOperationName + "  오류발생2 : " + ex.getMessage();
					ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
					throw new JDTOException(getClass().getName() + e.getMessage(), e);
				}
				
			}
			return gdRes;
		}  //end of delCarSchNCarPoint
		
		/**
		 *  코일제품창고 포인트개폐 처리 코일 외
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws DAOException
		 * @throws JDTOException
		 */
		public GridData procCoilYdGdsPntUnitCL4G(GridData inDto) throws DAOException {
			//LOG
			String szMsg = "";
			String szMethodName="procCoilYdGdsPntUnitCL4G";
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			
			try{
				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
				
				
				szMsg = "포인트개폐 전송처리 시작 ==>";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
				
				
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				ejbConn.trx("procCoilYdGdsPntUnitCL4G", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(inDto);
				gdRes = CmUtil.copyGDParam(inDto, gdRes);
				
				
				szMsg = " 포인트개폐 전송처리 ===> 끝";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
				
				
			}catch(Exception e){
				ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}
			return gdRes;
		}  //end of procCoilYdGdsPntUnitCL
		
		
		/**
		 *  권하위치 변경 (크레인작업관리 화면) 코일 사용 안함
		 *  - 전사물류개선 2021.1.6 기존화면 분리(자동화크레인관련)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return GridData
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData updToPosFix4G(GridData gdReq) throws DAOException {
			
			String szLogMsg           = "";
			String szMethodName       = "updToPosFix4G";
			boolean bool              = false;

			GridData gdRes            = null;
			EJBConnector ejbConn      = null;
			
			try{
				
				szLogMsg = "JSP-FACADE [권하위치 변경 (크레인작업관리 화면)] 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				gdRes = OperateGridData.cloneResponseGridData(gdReq);
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				JDTORecord [] inRecord =  ydComUtil.genJDTORecordSetByDataAllTypeCheck(gdReq);
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				JDTORecord rtnJto = (JDTORecord)ejbConn.trx("updToPosFix4G", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				szMethodName = "updToPosFix4G";
				
				
				if( rtnJto != null){
					if(bool){
						gdReq.setStatus(rtnJto.getFieldString("STATUS"));
						gdReq.setMessage("Success");
						szLogMsg = "권하위치 변경 성공";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, INFO);
						
					}else{
						gdReq.setStatus("Failure");
						gdReq.setMessage(rtnJto.getFieldString("MESSAGE"));
//						szLogMsg = "적치불가능 베드입니다.(현대3사제품 관련)";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, WARNING);
					}
				}

				
			}catch(DAOException de) {
				throw de;
			}catch(Exception e){
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, ERROR);
				throw new DAOException(e.getMessage());
			}
			
			szLogMsg = "JSP-FACADE [권하위치 변경 (크레인작업관리 화면)] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdReq;
		}  //end of updToPosFix4G
		
		
		/**
		 *  권하위치 변경(크레인 상태 설정 화면)
		 *  - 전사물류개선 2021.1.6 기존화면 분리(자동화크레인관련)
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 */
		public GridData updCrnDnPrsFix4G(GridData gdReq) throws JDTOException {
			//LOG			
			String szMethodName = "updCrnDnPrsFix4G";
			String szLogMsg = null;
			String szRtnMsg = null;
			GridData gdRes = null;
			EJBConnector ejbConn = null;

			try{
				
				szLogMsg = "JSP-FACADE [권하위치 변경(크레인 상태 설정 화면)] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx("updCrnDnPrsFix4G", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				gdRes.setMessage(szRtnMsg);
			}catch(Exception e){
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(szRtnMsg);
				szLogMsg = "[JSP Facade]권하위치 변경 처리 에러발생 : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(),e);
			}		
			gdRes.setStatus("true");
			
			szLogMsg = "JSP-FACADE [권하위치 변경(크레인 상태 설정 화면)] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		/**
		 *  설비 고장/정상 설정
		 *  - 전사물류개선 2021.1.6 기존화면 분리(자동화크레인관련)
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData updSlabYdCrnStsSetCrnStat4G(GridData inDto) throws JDTOException {
		 
			String szMethodName="updSlabYdCrnStsSetCrnStat4G";
			String[] szRtnMsg = null;
			String rtnMsg = YdConstant.RETN_CD_SUCCESS;
			String szLogMsg = "";
			String szOperationName	= "설비 고장/정상 설정";
			
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			try{
				
				szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				szRtnMsg = (String[])ejbConn.trx("updSlabYdCrnStsSetCrnStat4G", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(inDto);
				
				for(int Loop_i = 0; Loop_i < szRtnMsg.length; Loop_i++ ) {
					if( !szRtnMsg[Loop_i].equals(YdConstant.RETN_CD_SUCCESS) ) {
						rtnMsg = szRtnMsg[Loop_i];
						break;
					}
				}
					
				gdRes.setMessage(rtnMsg);
				
			}catch(Exception e){
				rtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(rtnMsg);
				ydUtils.putLog(szSessionName, szMethodName, "[JSP-FACADE  - "+ szOperationName  +"] 설비고장/정상 설정 에러발생 : " + e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}		
			gdRes.setStatus("true");
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			return gdRes;	
		}
		

		/**
		 *  설비 On-Line/Off-Line 설정
		 *  - 전사물류개선 2021.1.6 기존화면 분리(자동화크레인관련)
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData updSlabYdCrnStsSetCrnMode4G(GridData inDto) throws JDTOException {
			//LOG
			String[] szRtnMsg = null;
			String rtnMsg = YdConstant.RETN_CD_SUCCESS;
			String szMethodName="updSlabYdCrnStsSetCrnMode";
			String szLogMsg = "";
			String szOperationName	= "설비 On-Line/Off-Line 설정";
			
			GridData gdRes = null;
			EJBConnector ejbConn = null;
			try{
				
				szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				szRtnMsg = (String[])ejbConn.trx("updSlabYdCrnStsSetCrnMode4G", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(inDto);
				
				for(int Loop_i = 0; Loop_i < szRtnMsg.length; Loop_i++ ) {
					if( !szRtnMsg[Loop_i].equals(YdConstant.RETN_CD_SUCCESS) ) {
						rtnMsg = szRtnMsg[Loop_i];
						break;
					}
				}
					
				gdRes.setMessage(rtnMsg);
			}catch(Exception e){
				rtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(rtnMsg);
				ydUtils.putLog(szSessionName, szMethodName, "[JSP-FACADE  - "+ szOperationName  + "]" + e.getMessage(), YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}		
			gdRes.setStatus("true");
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			return gdRes;	
		}

		/**
		 *      응답백업처리 
		 *
		 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 *      @param  GridData gdReq
		 *      @return GridData
		 *      @throws DAOException
		*/
		public GridData updbtCrnStsSetPp(GridData gdReq) throws DAOException {
			String methodNm = "응답백업[YdJspCommonSeEJB.updbtCrnStsSetPp]";
			String szLogMsg = "";
			String rtnMsg = YdConstant.RETN_CD_SUCCESS;
			GridData gdRes = null;
			try {
				
				ydUtils.putLog(szSessionName, methodNm, szLogMsg, YdConstant.INFO);
				szLogMsg = "[JSP-FACADE  - "+ methodNm  + "] + 시작";
				ydUtils.putLog(szSessionName, methodNm, szLogMsg, YdConstant.INFO);
				
				EJBConnector ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				
				//크레인변경
				JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
				JDTORecord rtnJto = (JDTORecord)ejbConn.trx("updbtCrnStsSetPp", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				gdRes = OperateGridData.cloneResponseGridData(gdReq);

				gdRes.setStatus(rtnJto.getFieldString("STATUS"));
				gdRes.setMessage(rtnJto.getFieldString("MESSAGE"));
				szLogMsg = "응답백업처리 성공";
				ydUtils.putLog(szSessionName, methodNm, szLogMsg, INFO); 
				
			}catch(Exception e){
				rtnMsg = YdConstant.RETN_CD_FAILURE;
				gdRes.setMessage(rtnMsg);
				ydUtils.putLog(szSessionName, methodNm, "[JSP-FACADE  - 응답백업처리 ]" + e.getMessage(), YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}
			
			szLogMsg = "[JSP-FACADE  - 응답백업처리] + 끝";
			ydUtils.putLog(szSessionName, methodNm, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		}
		
		/**
		 * 크레인상태관리 - 크레인 변경[신규작성중]
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws JDTOException
		 * @throws JDTOException
		 */
		public GridData wrkCrnChange4G(GridData gdReq) throws JDTOException {						
			GridData gdRes            = null;
			EJBConnector ejbConn      = null;
		
			
						
			String szMethodName       = "wrkCrnChange4G";
			String szLogMsg           = "";
			String szOperationName = " 크레인상태관리 - 크레인 변경";
			String szRtnValue =  YdConstant.RETN_CD_SUCCESS;

			try{
				
				szLogMsg = "JSP-FACADE  [ "+ szOperationName +" ]시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				gdRes = OperateGridData.cloneResponseGridData(gdReq);		
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
				
				JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
				ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);			
				JDTORecord jdtoRtnValue = (JDTORecord)ejbConn.trx("wrkCrnChange4G", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				
				
				if( jdtoRtnValue != null){
					szRtnValue = jdtoRtnValue.getFieldString("ERROR_MSG");
					
					try{
						List aSendMsg = (List)jdtoRtnValue.getField("SEND_DATA");
						JDTORecord jtoMsg = null;
						YdDelegate ydDelegate = new YdDelegate();					
						
						if( aSendMsg != null){
							for(int i=0; i < aSendMsg.size(); i++){
								jtoMsg = (JDTORecord)aSendMsg.get(i);
								if("Y9YDL007".equals(jtoMsg.getFieldString("MSG_ID"))){
									ejbConn = new EJBConnector("default", "PlateYdRcvL2SeEJB", this);
									ejbConn.trx("rcvY9YDL007",   new Class[] { JDTORecord.class }, new Object[] { jtoMsg });
								}
								else{
									ydDelegate.sendMsg(jtoMsg);
								}
							}	
						}
					}catch (Exception e) {
					}
				}
				
				
				gdRes.setMessage(szRtnValue);
		
				
			}catch(JDTOException de) {
				
				gdRes.setMessage(szRtnValue);
				
				//Log 
				szLogMsg = "크레인 변경 실패 - DAO Exception ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, ERROR);
				
			} catch(Exception e){
				//Log
				gdRes.setMessage(szRtnValue);
				szLogMsg = "크레인 변경 실패 - JDTOException ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, ERROR);
				
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}			
			
			szLogMsg = "JSP-FACADE  [ 크레인상태관리 - 크레인 변경 ] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return gdRes;
		} // end of wrkCrnChange4G()
		///////////////////////////////////////////////////////////////////////////////
		///                          전사물류개선 프로젝트 2021.1.6                  ///
		///////////////////////////////////////////////////////////////////////////////
}