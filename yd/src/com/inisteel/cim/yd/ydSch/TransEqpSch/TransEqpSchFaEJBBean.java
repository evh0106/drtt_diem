package com.inisteel.cim.yd.ydSch.TransEqpSch;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTOException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;


/**
 * 이동설비스케줄 Facade Session EJB
 *                 
 * @ejb.bean name="TransEqpSchFaEJB" jndi-name="TransEqpSchFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */        
public class TransEqpSchFaEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName=getClass().getName();
	
	private YdUtils ydUtils =new YdUtils();
	
	private EJBConnector ydEjbCon = new EJBConnector("default", this);

	
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	
	
	
	
	/**
	 * 오퍼레이션명 : Test1
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvTest1(JDTORecord inRecord) throws JDTOException  {
		// TKOVLOC
		// YD-UC-????
		// TC : ????????
		// C연주정정L2시스템으로부터 수불구용도변경요구 수신
		//
		
		String szMsg="";
		String szMethodName="rcvTest1";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		
//		try {
//		
//		// Slab Spec 처리 EJB.지시정보 등록 요청 
//		ejbCon.trx("SlabSpecRegSeEJB", "woInfoRegReq", inRecord);
//		
//		// Slab Spec 처리 EJB.저장품제원등록요청
//		ejbCon.trx( "SlabSpecRegSeEJB", "reqStockSpecReg", inRecord);
//		
//		} catch (Exception e) {	
//			szMsg =szMethodName + "() " +e.getMessage(); 
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//		
//		} // end of try catch

		
		szMsg="Test1요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvTest1()
	
	
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : C연주 대차스케줄
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */         
	public void rcvY1TcarSch(JDTORecord inRecord) throws JDTOException  {
		// TKOVLOC
		// YD-UC-????
		// TC : YDYDJ620
		// C연주정정L2시스템으로부터 수불구용도변경요구 수신
		//
		
		String szMsg="";
		String szMethodName="rcvY1TcarSch";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		
		try {
			
			// 저장위치등록Main 요청 
			ydEjbCon.trx("TransEqpSchSeEJB", "procY1TcarSch", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="C연주 대차스케줄 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvY1TcarSch()
	
	
	
	/**
	 * 오퍼레이션명 : 후판 대차스케줄
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */         
	public void rcvY3TcarSch(JDTORecord inRecord) throws JDTOException  {
		String szMsg="";
		String szMethodName="rcvY3TcarSch";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		
		try {
			
			// 저장위치등록Main 요청 
			ydEjbCon.trx("TransEqpSchSeEJB", "procY3TcarSch", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="C연주 대차스케줄 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvY1TcarSch()
	
	
	
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : C열연 대차스케줄
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */         
	public void rcvY5TcarSch(JDTORecord inRecord) throws JDTOException  {
		// TKOVLOC
		// YD-UC-????
		// TC : YDYDJ621
		// C열연L2시스템으로부터 수불구용도변경요구 수신
		//
		
		String szMsg="";
		String szMethodName="rcvY5TcarSch";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		
		try {
			
			// 저장위치등록Main 요청 
//sjhkim			ydEjbCon.trx("TransEqpSchSeEJB", "procY5TcarSch", inRecord);
			ydEjbCon.trx("CoilTransEqpSchSeEJB", "procY5TcarSch", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="C열연 대차스케줄 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvY5TcarSch()



	
  //---------------------------------------------------------------------------	
} // end of class TransEqpSchFaEJBBean
