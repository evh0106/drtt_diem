package com.inisteel.cim.yd.ydWkAct.TcarMoveHd;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;


/**
* 대차이동처리 Facade Session EJB
*                 
* @ejb.bean name="TcarMvHdFaEJB" jndi-name="TcarMvHdFaEJB" type="Stateless"
*           view-type="remote" display-name="" description=""
* @weblogic.enable-call-by-reference True
* @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
* @ejb.transaction type="Required"
*/
public class TcarMvHdFaEJBBean extends BaseSessionBean {
	
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
	 * 오퍼레이션명 : C3대차이동실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */         
	public void rcvC3TcarMvWr(JDTORecord inRecord) throws JDTOException  {
	// 
	// YD-UC-???? C3대차이동실적
	// TC : C3YDL007, YDYDJ620
	// C연주정정L2시스템으로부터 C3대차도착실적 수신
	//
		
		String szMsg="";
		String szMethodName="rcvC3TcarMvWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR );

			return;
			
		}
		
		
		try {
			
			// 권하처리 요청 
			ydEjbCon.trx("TcarMvHdSeEJB", "procC3TcarMvWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="C3대차이동실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvC3TcarMvWr()
	
	

	/**
	 * 오퍼레이션명 : Y3대차이동실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */         
	public void rcvY3TcarMvWr(JDTORecord inRecord) throws JDTOException  {
	// 
	// YD-UC-???? Y3대차이동실적
	// TC : Y3YDL014, YDYDJ622
	// 후판슬라브야드L2시스템으로부터 Y3대차도착실적 수신
	//
		
		String szMsg="";
		String szMethodName="rcvY3TcarMvWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR );

			return;
			
		}
		
		
		try {
			
			// 권하처리 요청 
			ydEjbCon.trx("TcarMvHdSeEJB", "procY3TcarMvWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="Y3대차이동실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvY3TcarMvWr()
	
	
	

//	/**
//	 * 
//	 * 오퍼레이션명 : C열연코일야드L2 대차이동실적 (Y5YDL011)
//	 * 
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param inRecord
//	 * @return: 
//	 * @throws JDTOException
//	 */ 
//	public void rcvY5TcarMvWr(JDTORecord inRecord) throws JDTOException  {
//	// 
//	// YD-UC-???? Y5대차이동실적
//	// TC : Y5YDL011, YDYDJ621
//	// C열연코일야드L2시스템으로부터 Y5대차이동실적 수신
//	//
//	//┏━┓
//	//┃
//	//┗━┛
//		
//		String szMsg="";
//		String szMethodName="rcvY5TcarMvWr";
//
//		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
//			
//			szMsg= szMethodName+"() 실행 실패";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR );
//
//			return;
//			
//		}
//
//		try {
//			
//			// 권하처리 요청 
////sjhkim			ydEjbCon.trx("TcarMvHdSeEJB", "procY5TcarMvWr", inRecord);
//			ydEjbCon.trx("CoilTcarMvHdSeEJB", "procY5TcarMvWr", inRecord); 
//
//		} catch (Exception e) {			
//			szMsg =szMethodName + "() " +e.getMessage(); 
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//
//			throw new JDTOException(szMsg);
//
//		} // end of try catch
//
//		
//		szMsg="Y5대차이동실적 처리("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		
//		
//	 } // end of rcvY5TcarMvWr()	
	
	

//	/**
//	 * 
//	 * 오퍼레이션명 : C열연코일야드L2 대차이동실적 (Y5YDL018)
//	 * 
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param inRecord
//	 * @return: 
//	 * @throws JDTOException
//	 */ 
//	public void rcvY5TcarArriveMvWr(JDTORecord inRecord) throws JDTOException  {
//	// 
//	// YD-UC-???? Y5대차도착실적
//	// TC : Y5YDL018
//	// C열연코일야드L2시스템으로부터 Y5대차도착실적 수신
//	//
//	//┏━┓
//	//┃
//	//┗━┛
//		
//		String szMsg="";
//		String szMethodName="rcvY5TcarArriveMvWr";
//
//		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
//			
//			szMsg= szMethodName+"() 실행 실패";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR );
//
//			return;
//			
//		}
//
//		try {
//			
//			ydEjbCon.trx("CoilTcarMvHdSeEJB", "procY5TcarArriveMvWr", inRecord);
//
//		} catch (Exception e) {			
//			szMsg =szMethodName + "() " +e.getMessage(); 
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//
//			throw new JDTOException(szMsg);
//
//		} // end of try catch
//
//		
//		szMsg="Y5대차도착실적 처리("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		
//		
//	 } // end of rcvY5TcarArriveMvWr()	
	

	

	
	


	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//                                                
	//                     일관제철소정보관리시스템-야드관리
	//              작업실행관리-대차이동처리 Facade Session Bean
	//                          2008.09.30 YHWHman
	//                                                      
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
	
	
	
	
	
	
  //---------------------------------------------------------------------------
} // end of class

