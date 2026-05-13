package com.inisteel.cim.yd.ydWkRlt.LocFitnessMgt;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTOException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;


/**
 * 위치정합성관리 Facade Session EJB
 *
 * @ejb.bean name="LocFitnessMgtFaEJB" jndi-name="LocFitnessMgtFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class LocFitnessMgtFaEJBBean extends BaseSessionBean {
	
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

	

	
  //---------------------------------------------------------------------------	
} // end of class LocFitnessMgtFaEJBBean

