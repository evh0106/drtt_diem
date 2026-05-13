package com.inisteel.cim.yd.ydSch.CraneSch;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecordSet;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jsp.common.YDComUtil;


/**
 * 크레인스케쥴 Facade Session EJB
 *
 * @ejb.bean name="CrnSchFaEJB" jndi-name="CrnSchFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CrnSchFaEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName=getClass().getName();
	
	private YdUtils ydUtils =new YdUtils();
	YDComUtil   ydComUtil = new YDComUtil();
	
	private EJBConnector ydEjbCon = new EJBConnector("default", this);

	
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	/**
	 * 오퍼레이션명 : 야드작업내역 생성Main
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException
	 */
	public void rcvWrkHistMain(JDTORecord inRecord) throws DAOException {
				
		String szMsg="";
		String szMethodName="rcvWrkHistMain";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		try {
			
			// 야드작업내역등록Main 요청 
			ydEjbCon.trx("CrnSchSeEJB", "procWorkHistoryCreate", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);

		} // end of try catch


		
		szMsg="야드작업내역생성Main 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY0CrnSchMain()

	
	
	/**
	 * 오퍼레이션명 : 크레인스케줄 점검Main 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws DAOException
	 */
	public GridData rcvCrnSchCheckMain(GridData inDto) throws DAOException {
		
		String szMsg="";
		String szMethodName="rcvCrnSchCheckMain";
					
		GridData      gdRes     = null;
		JDTORecordSet recordSet = null;
		String        szMessage = "";
		EJBConnector ejbConn;
		
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			JDTORecord inTempRecord[] =  ydComUtil.genJDTORecordSet(inDto);
			
			JDTORecord sndRecord;
			
			ejbConn = new EJBConnector("default", "CrnSchSeEJB", this);

			szMessage = (String)ejbConn.trx("procCrnSchCheckMain",
						new Class[] { JDTORecord[].class }, new Object[] { inTempRecord });
			
						
			//for (int Loop_i = 1; Loop_i <= inTempRecord.length; Loop_i++) {
				
			//	sndRecord = inTempRecord[Loop_i];
				//ydUtils.displayRecord(szOperationName, recPara);
			//	szMessage = (String) ydEjbCon.trx("CrnSchSeEJB", "procCrnSchCheckMain", sndRecord);
			//}
			
						
			//szMessage = (String) ydEjbCon.trx("CrnSchSeEJB", "procCrnSchCheckMain", inTempRecord);
			
			                                                          
			gdRes = OperateGridData.cloneResponseGridData(inDto);

		}catch(Exception e){
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);
			
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szMessage);
		return gdRes;
	} //end of rcvCrnSchCheckMain
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 통합야드크레인스케줄Main
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException
	 * @weblogic.transaction-descriptor trans-timeout-seconds="180"
	 */
	public void rcvY0CrnSchMain(JDTORecord inRecord) throws DAOException {
		//
		// YD-UC-???? 통합야드크레인스케줄Main
		// TC : YDYDJ512
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY0CrnSchMain";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		try {
			
			// 저장위치등록Main 요청 
			ydEjbCon.trx("CrnSchSeEJB", "procY0CrnSchMain", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);

		} // end of try catch


		
		szMsg="통합야드크레인스케줄Main 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY0CrnSchMain()
	


		
		
	
		
		
		

	/**
	 * 오퍼레이션명 : 통합야드크레인저장위치결정Main
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException
	 */
	public void rcvY0CrnStrLocDeciMain(JDTORecord inRecord) throws DAOException {
		//
		// YD-UC-???? 통합야드크레인저장위치결정Main
		// TC : YDYDJ501
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY0CrnStrLocDeciMain";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
		try {
			
			// 저장위치등록Main 요청 
			ydEjbCon.trx("CrnSchSeEJB", "procY0CrnStrLocDeciMain", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);

		} // end of try catch


		
		szMsg="통합야드크레인저장위치결정Main 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY0CrnStrLocDeciMain()
	
	
	

		
		
	
		
		
		

	/**
	 * 오퍼레이션명 : C연주크레인스케줄Main
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException
	 * @weblogic.transaction-descriptor trans-timeout-seconds="180"
	 */
	public void rcvY1CrnSchMain(JDTORecord inRecord) throws DAOException {
		//
		// YD-UC-???? C연주크레인스케줄Main
		// TC : YDYDJ500
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY1CrnSchMain";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		try {
			
			// 저장위치등록Main 요청 
			ydEjbCon.trx("CrnSchSeEJB", "procY1CrnSchMain", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);

		} // end of try catch


		
		szMsg="C연주크레인스케줄Main 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY1CrnSchMain()
	


		
		
	
	/**
	 * 후판정정야드 크레인스케줄Main
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @작성자 : 
	 * @작성일 : 
	 */
	public GridData pPlateCrnSchBookout(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;

		try{
			ejbConn = new EJBConnector("default", "CrnSchSeEJB", this);
			gdRes = (GridData)ejbConn.trx("pPlateCrnSchBookout", new Class[] { GridData.class }, new Object[] { inDto });

		}catch(Exception e){		
			e.printStackTrace();
		}


		return gdRes;
	} 
	
		
		
		

	/**
	 * 오퍼레이션명 : C연주크레인저장위치결정Main
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException
	 */
	public void rcvY1CrnStrLocDeciMain(JDTORecord inRecord) throws DAOException {
		//
		// YD-UC-???? C연주크레인저장위치결정Main
		// TC : YDYDJ501
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY1CrnStrLocDeciMain";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
		try {
			
			// 저장위치등록Main 요청 
			ydEjbCon.trx("CrnSchSeEJB", "procY1CrnStrLocDeciMain", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);

		} // end of try catch


		
		szMsg="C연주크레인저장위치결정Main 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY1CrnStrLocDeciMain()
	


		
		
	
		
		
		

	/**
	 * 오퍼레이션명 : A후판크레인스케줄Main
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException
	 * @weblogic.transaction-descriptor trans-timeout-seconds="180"
	 */
	public void rcvY3CrnSchMain(JDTORecord inRecord) throws DAOException {
		//
		// YD-UC-???? A후판크레인스케줄Main
		// TC : YDYDJ503
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY3CrnSchMain";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		try {
			
			// 크레인스케줄Main 요청 
			ydEjbCon.trx("CrnSchSeEJB", "procY3CrnSchMain", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);

		} // end of try catch


		
		szMsg="A후판크레인스케줄Main 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY3CrnSchMain()
	


		
		
	
		
		

	/**
	 * 오퍼레이션명 : A후판크레인저장위치결정Main
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException
	 */
	public void rcvY3CrnStrLocDeciMain(JDTORecord inRecord) throws DAOException {
		//
		// YD-UC-???? A후판크레인저장위치결정Main
		// TC : YDYDJ504
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY3CrnStrLocDeciMain";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
		try {
			
			// 저장위치등록 요청 
			ydEjbCon.trx("CrnSchSeEJB", "procY3CrnStrLocDeciMain", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);

		} // end of try catch
		

		
		szMsg="A후판크레인저장위치결정Main 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY3CrnStrLocDeciMain()
	


		
		
	
		
		

	/**
	 * 오퍼레이션명 : 제품창고크레인스케줄Main
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException
	 * @weblogic.transaction-descriptor trans-timeout-seconds="180"
	 */
	public void rcvY4CrnSchMain(JDTORecord inRecord) throws DAOException {
		//
		// YD-UC-???? 제품창고크레인스케줄Main
		// TC : YDYDJ506
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY4CrnSchMain";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		try {
			
			// 크레인스케줄 Main요청 
			ydEjbCon.trx("CrnSchSeEJB", "procY4CrnSchMain", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);

		} // end of try catch


		
		szMsg="제품창고크레인스케줄Main 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY4CrnSchMain()
	


		
		
	
		
		

	
		
	/**
	 * 오퍼레이션명 : 제품창고크레인저장위치결정Main
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException
	 */
	public void rcvY4CrnStrLocDeciMain(JDTORecord inRecord) throws DAOException {
		//
		// YD-UC-???? 제품창고크레인저장위치결정Main
		// TC : YDYDJ507
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY4CrnStrLocDeciMain";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
		try {
			
			// 저장위치등록Main 요청 
			ydEjbCon.trx("CrnSchSeEJB", "procY4CrnStrLocDeciMain", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);

		} // end of try catch

		
		szMsg="제품창고크레인저장위치결정Main 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY4CrnStrLocDeciMain()
			
	


		
		
	
		
		

	/**
	 * 오퍼레이션명 : C열연크레인스케줄Main
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException
	 * @weblogic.transaction-descriptor trans-timeout-seconds="180"
	 */
	public void rcvY5CrnSchMain(JDTORecord inRecord) throws DAOException {
		//
		// YD-UC-???? C열연크레인스케줄Main
		// TC : YDYDJ509
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY5CrnSchMain";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		try {
			
			// 크레인 스케줄 Main요청
//sjh			ydEjbCon.trx("CrnSchSeEJB", "procY5CrnSchMain", inRecord);
			ydEjbCon.trx("CoilCrnSchSeEJB", "procY5CrnSchMain", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);

		} // end of try catch


		
		szMsg="C열연크레인스케줄Main 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY5CrnSchMain()		
	


		
		
	
		
		

	/**
	 * 오퍼레이션명 : C열연크레인저장위치결정Main
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException
	 */
	public void rcvY5CrnStrLocDeciMain(JDTORecord inRecord) throws DAOException {
		//
		// YD-UC-???? C열연크레인저장위치결정Main
		// TC : YDYDJ510
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY5CrnStrLocDeciMain";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		try {
			
			// 저장위치등록 요청 
//sjh			ydEjbCon.trx("CrnSchSeEJB", "procY5CrnStrLocDeciMain", inRecord);
			ydEjbCon.trx("CoilCrnSchSeEJB", "procY5CrnStrLocDeciMain", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);

		} // end of try catch


		
		szMsg="C열연크레인저장위치결정Main 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY5CrnStrLocDeciMain()


	/**
	 * 오퍼레이션명 : 전문전송버퍼
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException
	 */
	public void rcvForwardTcRecord(JDTORecord inRecord) throws DAOException {
		//
		// YD-UC-???? 전문전송버퍼
		// TC : YDYDJ701
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvForwardTcRecord";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		try {
			
			//전문전송버퍼
			ydEjbCon.trx("CrnSchSeEJB", "procForwardTcRecord", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);

		} // end of try catch


		
		szMsg="전문전송버퍼 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvForwardTcRecord()
	
  //---------------------------------------------------------------------------	
} // end of class CrnSchFaEJBBean

