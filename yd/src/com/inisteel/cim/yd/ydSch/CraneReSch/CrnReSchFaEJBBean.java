package com.inisteel.cim.yd.ydSch.CraneReSch;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTOException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;


/**
 * 크레인리스케쥴 Facade Session EJB
 *
 * @ejb.bean name="CrnReSchFaEJB" jndi-name="CrnReSchFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CrnReSchFaEJBBean extends BaseSessionBean {
	
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
	 * 오퍼레이션명 : 통합야드크레인리스케줄
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvY0CrnReSch(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 통합야드크레인리스케줄
		// TC : YDYDJ502
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY0CrnReSch";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 		try {
			
			// 크레인 리스케줄 요청 
			ydEjbCon.trx("CrnReSchSeEJB", "procY0CrnReSch", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch


		
		szMsg="통합야드크레인리스케줄 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY0CrnReSch()
	
	
	
	
		
		
	
		
		

	/**
	 * 오퍼레이션명 : C연주크레인리스케줄
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 * @weblogic.transaction-descriptor trans-timeout-seconds="180"
	 */
	public void rcvY1CrnReSch(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주크레인리스케줄
		// TC : YDYDJ502
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY1CrnReSch";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 		try {
			
			// 크레인 리스케줄 요청 
			ydEjbCon.trx("CrnReSchSeEJB", "procY1CrnReSch", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch


		
		szMsg="C연주크레인리스케줄 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY1CrnReSch()
				
	


		
		
	
		
		

	/**
	 * 오퍼레이션명 : A후판크레인리스케줄
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvY3CrnReSch(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? A후판크레인리스케줄
		// TC : YDYDJ505
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY3CrnReSch";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
		try {
			
			// 크레인 리스케줄 요청 
			ydEjbCon.trx("CrnReSchSeEJB", "procY3CrnReSch", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch


		
		szMsg="A후판크레인리스케줄 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY3CrnReSch()
			
	


		
		
	
		
		

	/**
	 * 오퍼레이션명 : 제품창고크레인리스케줄
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvY4CrnReSch(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 제품창고크레인리스케줄
		// TC : YDYDJ508
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY4CrnReSch";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
		try {
			
			// 크레인 리스케줄 요청 
			ydEjbCon.trx("CrnReSchSeEJB", "procY4CrnReSch", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		


		
		szMsg="제품창고크레인리스케줄 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY4CrnReSch()
		
	


		
		
	
		
		

	/**
	 * 오퍼레이션명 : C열연크레인리스케줄
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvY5CrnReSch(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연크레인리스케줄
		// TC : YDYDJ511
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY5CrnReSch";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
		try {
			
			// 크레인 리스케줄 요청 
//sjh			ydEjbCon.trx("CrnReSchSeEJB", "procY5CrnReSch", inRecord);
			ydEjbCon.trx("CoilCrnReSchSeEJB", "procY5CrnReSch", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch


		
		szMsg="C열연크레인리스케줄 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY5CrnReSch()


	
  //---------------------------------------------------------------------------	
} // end of class CrnReSchFaEJBBean

