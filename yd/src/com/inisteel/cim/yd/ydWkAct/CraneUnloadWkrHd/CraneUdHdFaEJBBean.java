package com.inisteel.cim.yd.ydWkAct.CraneUnloadWkrHd;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import com.inisteel.cim.common.exception.DAOException;
import jspeed.base.record.JDTORecord;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;



/**
* 권하실적처리 Facade Session EJB 
*                 
* @ejb.bean name="CraneUdHdFaEJB" jndi-name="CraneUdHdFaEJB" type="Stateless"
*           view-type="remote" display-name="" description=""
* @weblogic.enable-call-by-reference True
* @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
* @ejb.transaction type="Required"
*/
public class CraneUdHdFaEJBBean extends BaseSessionBean {
	
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
	 * 오퍼레이션명 : Y0크레인권하실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 * @weblogic.transaction-descriptor trans-timeout-seconds="180"
	 */         
	public void rcvY0CrnUdWr(JDTORecord inRecord) throws JDTOException  {
	// 
	// YD-UC-5201 Y0크레인권하실적
	// TC : Y0YDL009, YDYDJ601
	// C연주슬라브야드L2시스템으로부터 크레인권하실적 수신
	//
	//┏━┓
	//┃ C연주슬라브야드 L2에서 크레인 권하처리 결과를 수신하여 
	//┃ 권상실적을 등록
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY0CrnUdWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
		
		try {
			
			// 권하처리 요청 
			ydEjbCon.trx("CraneUdHdSeEJB", "procY0CrnUdWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="Y0크레인권하실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvY0CrnUdWr()
	
	

	
	/**
	 * 오퍼레이션명 : Y0크레인비상조업실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvY0CrnEmgPtopWr(JDTORecord inRecord) throws JDTOException  {
	// 
	// YD-UC-5205 Y0크레인비상조업실적
	// TC : Y0YDL010
	// C연주슬라브야드L2시스템으로부터 크레인비상조업실적 수신
	//
	//┏━┓
	//┃ C연주슬라브야드 L2에서 비상조업한 결과를 수신하여 야드의 저장위치를 정리
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY0CrnEmgPtopWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		try {
			
			// 비상조업실적등록 요청 
			ydEjbCon.trx("CraneUdHdSeEJB", "procY0CrnEmgPtopWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		

		
		szMsg="Y0크레인비상조업실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvY0CrnEmgPtopWr()
	
	
	
	
	

		
	

	
	/**
	 * 오퍼레이션명 : Y1크레인권하실적 (Y1YDL009)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 * @weblogic.transaction-descriptor trans-timeout-seconds="180"
	 */         
	public void rcvY1CrnUdWr(JDTORecord inRecord) throws JDTOException  {
	// 
	// YD-UC-5201 Y1크레인권하실적
	// TC : Y1YDL009, YDYDJ601
	// C연주슬라브야드L2시스템으로부터 크레인권하실적 수신
	//
	//┏━┓
	//┃ C연주슬라브야드 L2에서 크레인 권하처리 결과를 수신하여 
	//┃ 권상실적을 등록
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY1CrnUdWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
		try {
			
			// 권하처리 요청 
			ydEjbCon.trx("CraneUdHdSeEJB", "procY1CrnUdWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="Y1크레인권하실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvY1CrnUdWr()
	
	

	
	/**
	 * 오퍼레이션명 : Y1크레인비상조업실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvY1CrnEmgPtopWr(JDTORecord inRecord) throws JDTOException  {
	// 
	// YD-UC-5205 Y1크레인비상조업실적
	// TC : Y1YDL010
	// C연주슬라브야드L2시스템으로부터 크레인비상조업실적 수신
	//
	//┏━┓
	//┃ C연주슬라브야드 L2에서 비상조업한 결과를 수신하여 야드의 저장위치를 정리
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY1CrnEmgPtopWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		try {
			
			// 비상조업실적등록 요청 
			ydEjbCon.trx("CraneUdHdSeEJB", "procY1CrnEmgPtopWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		

		
		szMsg="Y1크레인비상조업실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvY1CrnEmgPtopWr()
	
	

	
	/**
	 * 오퍼레이션명 : Y3크레인권하실적 (Y3YDL009)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 * @weblogic.transaction-descriptor trans-timeout-seconds="180"
	 */ 
	public void rcvY3CrnUdWr(JDTORecord inRecord) throws JDTOException  {
	// 
	// YD-UC-5202 Y3크레인권하실적
	// TC : Y3YDL009, YDYDJ603
	// A후판슬라브야드L2시스템으로부터 크레인권하실적 수신
	//
	//┏━┓
	//┃ A후판슬라브야드 L2에서 크레인 권하처리 결과를 수신하여 권상실적을 등록
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY3CrnUdWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}

			
		
		try {
			
			// 권하처리 요청 
			ydEjbCon.trx("CraneUdHdSeEJB", "procY3CrnUdWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="Y3크레인권하실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvY3CrnUdWr()
	
	

	
	/**
	 * 오퍼레이션명 : Y3크레인비상조업실적 (Y3YDL010)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvY3CrnEmgPtopWr(JDTORecord inRecord) throws JDTOException  {
	// 
	// YD-UC-5206 Y3크레인비상조업실적
	// TC : Y3YDL010
	// A후판슬라브야드L2시스템으로부터 크레인비상조업실적 수신
	//
	//┏━┓
	//┃ A후판슬라브야드 L2에서 비상조업한 결과를 수신하여 야드의 저장위치를 정리
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY3CrnEmgPtopWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		
		try {
			
			// 비상조업실적등록 요청 
			ydEjbCon.trx("CraneUdHdSeEJB", "procY3CrnEmgPtopWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		
		
		szMsg="Y3크레인비상조업실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvY3CrnEmgPtopWr()
	
	

	
	/**
	 * 오퍼레이션명 : Y4 크레인권하실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 * @weblogic.transaction-descriptor trans-timeout-seconds="180"
	 */ 
	public void rcvY4CrnUdWr(JDTORecord inRecord) throws JDTOException  {
	// 
	// YD-UC-5203 Y4크레인권하실적
	// TC : Y4YDL009, YDYDJ605
	// 후판제품야드L2시스템으로부터 크레인권하실적 수신
	//
	//┏━┓
	//┃ 후판제품야드 L2에서 크레인 권하처리 결과를 수신하여 권상실적을 등록
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY4CrnUdWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
		
		try {
			
			// 권하처리 요청 
			ydEjbCon.trx("CraneUdHdSeEJB", "procY4CrnUdWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);
		} // end of try catch
		
		szMsg="Y4크레인권하실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvY4CrnUdWr()
	
	

	/**
	 * 오퍼레이션명 : Y8 크레인권하실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 * @weblogic.transaction-descriptor trans-timeout-seconds="180"
	 */ 
	public void rcvY8CrnUdWr(JDTORecord inRecord) throws JDTOException  {
	// 
	// TC : Y8YDL009, YDYDJ605
	// 2후판제품야드L2시스템으로부터 크레인권하실적 수신
	//
	//┏━┓
	//┃ 2후판제품야드 L2에서 크레인 권하처리 결과를 수신하여 권상실적을 등록
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY8CrnUdWr";

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= inRecord.getResultCode();				// 전문으로 부터 logid get
String logId                    		= ydUtils.getJDTOLogId(inRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 					// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8크레인권하실적 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

			return;
		}
		
		
		try {
////////////////////////////////////////////////////////////////////////////////////////
//2024.09.09 로그 개선  START
//전문처리 procY4CrnUdWr Method에 ([T] + 전문일련번호) 형식으로  logId 넘김
			inRecord.setField("LOG_ID", logId);
//2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
			
			// 권하처리 요청 
			ydEjbCon.trx("CraneUdHdSeEJB", "procY4CrnUdWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

			throw new JDTOException(szMsg);
		} // end of try catch
		
		szMsg="Y8크레인권하실적 처리("+szMethodName+") 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
		
		
	 } // end of rcvY8CrnUdWr()

	
	
	
	/**
	 * 오퍼레이션명 : C열연코일야드L2 크레인권하실적 (Y5YDL009)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 * @weblogic.transaction-descriptor trans-timeout-seconds="180"
	 */ 
	public void rcvY5CrnUdWr(JDTORecord inRecord) throws DAOException  {
	// 
	// YD-UC-5204 Y5크레인권하실적
	// TC : Y5YDL009, YDYDJ607
	// C연주코일야드L2시스템으로부터 크레인권하실적 수신
	//
	//┏━┓
	//┃ C연주코일야드L2에서 크레인 권하처리 결과를 수신하여 권상실적을 등록
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY5CrnUdWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
			
		
		try {
			
			// 권하처리 요청 
//SJH			ydEjbCon.trx("CraneUdHdSeEJB", "procY5CrnUdWr", inRecord);
			ydEjbCon.trx("CoilCraneUdHdSeEJB", "procY5CrnUdWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);

		} // end of try catch

		
		szMsg="Y5크레인권하실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvY5CrnUdWr()
	
	/**
	 * 오퍼레이션명 : C열연코일야드L2 크레인비상조업실적 (Y5YDL010)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvY5CrnEmgPtopWr(JDTORecord inRecord) throws JDTOException  {
	// 
	// YD-UC-5206 Y5크레인비상조업실적
	// TC : Y5YDL010
	// C열연코일야드 L2시스템으로부터 크레인비상조업실적 수신
	//
	//┏━┓
	//┃C열연코일야드 L2에서 비상조업한 결과를 수신하여 야드의 저장위치를 정리
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY5CrnEmgPtopWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		
		try {
			
			// 비상조업실적등록 요청 
//sjh			ydEjbCon.trx("CraneUdHdSeEJB", "procY5CrnEmgPtopWr", inRecord);
			ydEjbCon.trx("CoilCraneUdHdSeEJB", "procY5CrnEmgPtopWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		
		
		szMsg="Y5크레인비상조업실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvY5CrnEmgPtopWr()
	
	
//	/**
//	 * 오퍼레이션명 : 권하위치 변경가능유무 응답
//	 * 
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param inRecord
//	 * @return: 
//	 * @throws JDTOException
//	 */ 
//	public void rcvY5CrnUdWrAns(JDTORecord inRecord) throws JDTOException  {
//		// 
//		// YD-UC-????
//		// TC : Y5YDL015
//		// C열연코일야드L2시스템으로부터 권하위치 변경가능유무 응답
//		// 
//		String szMsg        = "";
//		String szMethodName = "rcvY5CrnUdWrAns";
//
//		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
//			szMsg=szMethodName+"() 실행 실패";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return ;	
//		}
//		
//		try {
//			ydEjbCon.trx("CoilCraneUdHdSeEJB", "rcvY5CrnUdWrAns", inRecord);
//		} catch (Exception e) {			
//			szMsg = szMethodName + "() " + e.getMessage(); 
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			throw new JDTOException(szMsg);
//		} // end of try catch
//		
//		szMsg="권하위치 변경가능유무 응답("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	} // end of rcvY5EqpDrvMdTurnov()
	


	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//                                                
	//                     일관제철소정보관리시스템-야드관리
	//              작업실행관리-권하실적처리 Facade Session Bean
	//                          2008.09.30 YHWHman
	//                                                      
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

  //---------------------------------------------------------------------------
} // end of class

