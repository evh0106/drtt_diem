package com.inisteel.cim.yd.ydWkPlan.IssueWkPlan;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;


/**
 * 출고작업계획 Session EJB
 *
 * @ejb.bean name="IssueWrkPlnSeEJB" jndi-name="IssueWrkPlnSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class IssueWrkPlnSeEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName=getClass().getName();
	
	private YdUtils ydUtils =new YdUtils();
	
	private YdTcConst ydTcConst =new YdTcConst();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	// [DEBUG] message flag
	private boolean bDebugFlag=true;
	
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	

	/**
	 *      [A] 오퍼레이션명 : 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procTest(JDTORecord msgRecord)throws JDTOException  {
		
		
		String szMsg="";
		String szMethodName="procTest";
		
		

		String szRcvTcCode=ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode==null){
			szMsg=szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			return;
		}
		if(bDebugFlag){
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} 
		
		
		//
		//
		//
		//
		//	toDo Something...
		//
		//
		//
		//
		//

		
		szMsg="Test정보수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	}// end of procTest()
	
	
  //---------------------------------------------------------------------------	
} // end of class IssueWrkPlnSeEJBBean
