package com.inisteel.cim.yd.ydWkReq.IssueWkReq;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordImplMap;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdUtils;


/**
 * 출고작업요구 Facade Session EJB
 *
 * @ejb.bean name="IssueWrkDmdFaEJB" jndi-name="IssueWrkDmdFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class IssueWrkDmdFaEJBBean extends BaseSessionBean {
	private Logger logger = new Logger("yd");
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
	 * 오퍼레이션명 : Y3 Take-In완료 (Y3YDL013)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvY3TakeInCmpl(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? Y3 Take-In완료
		// TC : Y3YDL013
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY3TakeInCmpl";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		

		
		
        try {
            
            //A후판Take-In재료등록
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procY3TakeInCmpl", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="Y3 Take-In완료 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvY3TakeInCmpl()
	
	
	
	
	

	/**
	 * 오퍼레이션명 : C연주정정L2 C3 Take-In완료 (C3YDL005, C7YDL005)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvC3TakeInCmpl(JDTORecord inRecord) throws JDTOException {
		
		logger.println(LogLevel.DEBUG, "rcvC3TakeInCmpl() 메소드가 호출되었습니다.");
		
		Object[] keyArr = ((JDTORecordImplMap)inRecord).getFieldNames();
		for(int ii=0;ii<keyArr.length;ii++){
			String key = (String)keyArr[ii];
			logger.println(LogLevel.DEBUG, key + " : " +(String)inRecord.getField(key));
		
		}
		
		String szMsg="";
		String szMethodName="rcvC3TakeInCmpl";
		
		logger.println(LogLevel.DEBUG, "rcvC3TakeInCmpl() 1 ");


		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		logger.println(LogLevel.DEBUG, "rcvC3TakeInCmpl() 2 ");

        try {
            
            //C연주Take-In재료등록
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procC3TakeInCmpl", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

           throw new JDTOException(szMsg);

        } // end of try catch

        logger.println(LogLevel.DEBUG, "rcvC3TakeInCmpl() 3 ");
		
		szMsg="C3 Take-In완료 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvC3TakeInCmpl()

	
	/**
	 * 오퍼레이션명 : C열연  정정보급요구  (HRYDJ008)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException
	 */
	public void rcvCHrShearInSupLotCompFromHr(JDTORecord inRecord) throws DAOException {
		//
		// YD-UC-???? C열연  정정보급요구
		// TC : YDYDJ252
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrShearInSupLotCompFromHr";
		JDTORecord   outRecord   	= JDTORecordFactory.getInstance().create();

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
        try {
            //C열연 정정입측 보급Lot 편성
//sjhkim        	outRecord = (JDTORecord)ydEjbCon.trx("IssueWrkDmdSeEJB", "procCHrShearInSupLotComp", inRecord);
        	outRecord = (JDTORecord)ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCHrShearInSupLotComp", inRecord);
            String sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
            String sRTN_MSG		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "0");
 
        	if (!("1".equals(sRTN_CD))) {
        	
        		throw new DAOException(sRTN_MSG);
			}
		
        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);

        } // end of try catch

		
		szMsg="C열연  정정보급요구 - HRYDJ008 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvCHrShearInSupLotCompFromHr()
	
	
	/**
	 * 오퍼레이션명 : R3 정정입측보급Lot편성(H2YDL001)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrShearInSupLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? R3 정정입측보급Lot편성
		// TC : YDYDJ252
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrShearInSupLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		

		
		
        try {
            
            //C열연 정정입측 보급Lot 편성
             ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCHrShearInSupLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="R3 정정입측보급Lot편성 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvCHrShearInSupLotComp()
	
	
	

	/**
	 * 오퍼레이션명 : R3 정정입측Line-In요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvR3ShearInLineInReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? R3 정정입측Line-In요구
		// TC : R3YDL001
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvR3ShearInLineInReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
        try {
            //C열연 정정입측 Line-In 요구
//sjhkim            ydEjbCon.trx("IssueWrkDmdSeEJB", "procR3ShearInLineInReq", inRecord);
//            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procR3ShearInLineInReq", inRecord);
            
            //C열연 정정입측 보급Lot 편성
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCHrShearInSupLotComp", inRecord);
            

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

           throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="R3 정정입측Line-In요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvR3ShearInLineInReq()

	
	
	/**
	 * 오퍼레이션명 : R3 수냉탱크보급Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrWtclTnkSupLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? R3 수냉탱크보급Lot편성
		// TC : YDYDJ251
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrWtclTnkSupLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		

		
		
        try {
            
            //C열연 수냉탱크 보급 Lot 편성
//sjhkim            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCHrWtclTnkSupLotComp", inRecord);
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCHrWtclTnkSupLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="R3 수냉탱크보급Lot편성("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvCHrWtclTnkSupLotComp()
	
	

	/**
	 * 오퍼레이션명 : R3 수냉탱크Line-In요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvR3WtclTnkLineInReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? R3 수냉탱크Line-In요구
		// TC : R3YDL002
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvR3WtclTnkLineInReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		

		
		
        try {
            
            //C열연 수냉탱크 Line-In 요구
//sjhkim            ydEjbCon.trx("IssueWrkDmdSeEJB", "procR3WtclTnkLineInReq", inRecord);
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procR3WtclTnkLineInReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="R3 수냉탱크Line-In요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvR3WtclTnkLineInReq()
	
	/**
	 * 오퍼레이션명 : C연주C열연보급Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsCHrSupLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주C열연보급Lot편성
		// TC : YDYDJ231
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCCsCHrSupLotComp";
		
		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
        try {
            //C연주 C열연 보급 Lot 편성
        	ydEjbCon.trx("IssueWrkDmdSeEJB", "procCCsCHrSupLotComp", inRecord);
            
        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch
		
		szMsg="C연주C열연보급Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvCCsCHrSupLotComp()

	/**
	 * 오퍼레이션명 : C연주M-Scarfing보급Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsMScarfingSupLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주M-Scarfing보급Lot편성
		// TC : YDYDJ232
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCCsMScarfingSupLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	

		
		
        try {
        	
            //C연주 M-Scarfing 보급 Lot 편성
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCCsMScarfingSupLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주M-Scarfing보급Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvCCsMScarfingSupLotComp()
	


		
	
	/**
	 * 오퍼레이션명 : C연주정정보급Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsShearSupLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주정정보급Lot편성
		// TC : YDYDJ233
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCCsShearSupLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            //C연주 정정 보급 Lot 편성
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCCsShearSupLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주정정보급Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
	} // end of rcvCCsShearSupLotComp()
	


		

	/**
	 * 오퍼레이션명 : C연주C열연보급Carry-In작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsCHrSupCarryInWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주C열연보급Carry-In작업요구
		// TC : YDYDJ241
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCCsCHrSupCarryInWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //C연주C열연보급Carry-In작업요구
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCCsCHrSupCarryInWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주C열연보급Carry-In작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCCsCHrSupCarryInWrkReq()
	


		
		

	/**
	 * 오퍼레이션명 : C연주Scarfing보급Carry-In작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsScarfingSupCarryInWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주Scarfing보급Carry-In작업요구
		// TC : YDYDJ242
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCCsScarfingSupCarryInWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //C연주 M-Scarfing 보급Carry-In작업요구
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCCsScarfingSupCarryInWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주Scarfing보급Carry-In작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCCsScarfingSupCarryInWrkReq()
	


		
		
	

	/**
	 * 오퍼레이션명 : C연주정정보급Carry-In작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCSShearSupCarryInWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주정정보급Carry-In작업요구
		// TC : YDYDJ243
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCCSShearSupCarryInWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //C연주 정정 보급Carry-In작업요구
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCCSShearSupCarryInWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주정정보급Carry-In작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCCSShearSupCarryInWrkReq()
	


		
		
	
		

	/**
	 * 오퍼레이션명 : C연주소재이송상차LOT편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsMatlFtmvCarLdLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주소재이송상차LOT편성
		// TC : YDYDJ234
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCCsMatlFtmvCarLdLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //C연주 소재이송 상차 LOT 편성
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCCsMatlFtmvCarLdLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주소재이송상차LOT편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCCsMatlFtmvCarLdLotComp()
	
	
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 통합야드소재이송상차LOT편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvSUnMatlFtmvCarLdLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 통합야드소재이송상차LOT편성
		// TC : YDYDJ288
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvSUnMatlFtmvCarLdLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //통합야드 소재이송 상차 LOT 편성
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procSUnMatlFtmvCarLdLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="통합야드소재이송상차LOT편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvSUnMatlFtmvCarLdLotComp()
	


		
	/**
	 * 오퍼레이션명 : 통합야드소재이송상차LOT편성(크레인별)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvSUnMatlFtmvCarLdLotCompCrn(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 통합야드소재이송상차LOT편성
		// TC : YDYDJ295
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvSUnMatlFtmvCarLdLotCompCrn";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //통합야드 소재이송 상차 LOT 편성
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procSUnMatlFtmvCarLdLotCompCrn", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="통합야드소재이송상차LOT편성 처리(크레인별)("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvSUnMatlFtmvCarLdLotCompCrn()
	
		
		

	/**
	 * 오퍼레이션명 : C연주외판출하상차LOT편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsOutplDistCarLdlotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 
		// TC : YDYDJ235
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg = "";
		String szMethodName = "rcvCCsOutplDistCarLdlotComp";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
            //C연주 외판출하 상차 LOT 편성
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCCsOutplDistCarLdlotComp", inRecord);
        } catch (Exception e) {         
            szMsg = szMethodName + "() " + e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

		szMsg="C연주외판출하상차LOT편성처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvCCsOutplDistCarLdlotComp()
	


		
		
	
		
		
	

	/**
	 * 오퍼레이션명 : C연주대차상차LOT편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsTcarLdLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주대차상차LOT편성
		// TC : YDYDJ236
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCCsTcarLdLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //C연주 대차 상차 LOT 편성
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCCsTcarLdLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주대차상차LOT편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCCsTcarLdLotComp()
	


		
		
	
		
		
		

	/**
	 * 오퍼레이션명 : C연주차량상차작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsCarLdWrkReq(JDTORecord inRecord) throws Exception {
		//
		// YD-UC-???? C연주차량상차작업요구
		// TC : YDYDJ244
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCCsCarLdWrkReq";
		String szRtnMsg = null;
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //C연주 차량상차 작업요구
        	szRtnMsg = (String)ydEjbCon.trx("IssueWrkDmdSeEJB", "procCCsCarLdWrkReq", inRecord);
        	//리턴값에 따른 예외처리 필요 - 롤백처리가 되도록...
        	szMsg ="[C연주차량상차작업요구 - Facade] 리턴값 = " + szRtnMsg; 
            ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.DEBUG);

        } catch (Exception e) {         
            szMsg ="[C연주차량상차작업요구 - Facade] 예외발생 : " + e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            //예외가 발생하는 경우에는 롤백처리를 진행한다.
           throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주차량상차작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCCsCarLdWrkReq()
	


		
	/**
	 * 오퍼레이션명 : 통합야드차량상차작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvSlabTotCarLdWrkReq(JDTORecord inRecord) throws Exception {
		//
		// YD-UC-???? 통합야드차량상차작업요구
		// TC : YDYDJ244
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvSlabTotCarLdWrkReq";
		String szOperationName = "통합야드차량상차작업요구";
		String szRtnMsg = null;
		
		szMsg="["+szOperationName+" - Facade] 메소드 시작";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
        try {
            
            //통합야드차량상차작업요구
        	szRtnMsg = (String)ydEjbCon.trx("IssueWrkDmdSeEJB", "procSlabTotCarLdWrkReq", inRecord);
        	//리턴값에 따른 예외처리 필요 - 롤백처리가 되도록...
        	szMsg ="["+szOperationName+" - Facade] 리턴값 = " + szRtnMsg; 
            ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.DEBUG);

        } catch (Exception e) {         
            szMsg ="["+szOperationName+" - Facade] 예외발생 : " + e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            //예외가 발생하는 경우에는 롤백처리를 진행한다.
            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="["+szOperationName+" - Facade] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvSlabTotCarLdWrkReq()
	
		
		
	

	/**
	 * 오퍼레이션명 : C연주대차상차작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsTcarLdWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주대차상차작업요구
		// TC : YDYDJ249
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCCsTcarLdWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //C연주 대차상차 작업요구
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCCsTcarLdWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

           throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주대차상차작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCCsTcarLdWrkReq()
	


		
		
	
		
		
	

	/**
	 * 오퍼레이션명 : A후판가열로보급Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvAPlRefurSupLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? A후판가열로보급Lot편성
		// TC : YDYDJ237
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvAPlRefurSupLotComp";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
        try {
            //A후판 가열로 보급 Lot 편성
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procAPlRefurSupLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch
		
		szMsg="A후판가열로보급Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvAPlRefurSupLotComp()
	


	/**
	 * 오퍼레이션명 : A후판Carry-In작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvAPlCarryInWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? A후판Carry-In작업요구
		// TC : YDYDJ245
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvAPlCarryInWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            //A후판 Carry-In 작업요구
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procAPlCarryInWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="A후판Carry-In작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvAPlCarryInWrkReq()
	


		
		

	/**
	 * 오퍼레이션명 : 2후판가열로보급Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvBPlRefurSupLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 2후판가열로보급Lot편성
		// TC : YDYDJ497
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvBPlRefurSupLotComp";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
        try {
            //2후판 가열로 보급 Lot 편성
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procBPlRefurSupLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch
		
		szMsg="A후판가열로보급Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvAPlRefurSupLotComp()
	


	/**
	 * 오퍼레이션명 : 2후판Carry-In작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvBPlCarryInWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 2후판Carry-In작업요구
		// TC : YDYDJ495
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvBPlCarryInWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
        try {
            //2후판 Carry-In 작업요구
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procBPlCarryInWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="2후판Carry-In작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvAPlCarryInWrkReq()
	
	
		
		
	

	/**
	 * 오퍼레이션명 : A후판소재이송상차Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvAPlMatlFtmvCarLdLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? A후판소재이송상차Lot편성
		// TC : YDYDJ238
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvAPlMatlFtmvCarLdLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            //A후판 소재이송 상차 Lot 편성
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procAPlMatlFtmvCarLdLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="A후판소재이송상차Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvAPlMatlFtmvCarLdLotComp()
	


		
		
	
		
		
		

	/**
	 * 오퍼레이션명 : A후판차량상차작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvAPlCarldWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? A후판차량상차작업요구
		// TC : YDYDJ246
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvAPlCarldWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            //A후판 차량상차 작업요구
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procAPlCarldWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="A후판차량상차작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvAPlCarldWrkReq()
	


		
		
	
		
		

	/**
	 * 오퍼레이션명 : 후판창고차량상차작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */	
	public void rcvY4CarLdWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 후판창고차량상차작업요구
		// TC : YDYDJ247
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY4CarLdWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            //후판 창고 차량상차 작업요구
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procY4CarLdWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="후판창고차량상차작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY4CarLdWrkReq()
	


		
		
	
		
		

	/**
	 * 오퍼레이션명 : C열연차량상차작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrCarLdWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연차량상차작업요구

		// TC : YDYDJ248
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrCarLdWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
//sjhkim            //C열연 차량상차 작업요구
//sjhkim            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCHrCarLdWrkReq", inRecord);
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCHrCarLdWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C열연차량상차작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCHrCarLdWrkReq()
	


		
		
	
		
		
	

	/**
	 * 오퍼레이션명 : C열연소재임가공LOT편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrMatlRentProcLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연소재임가공LOT편성
		// TC : YDYDJ239
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrMatlRentProcLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //C열연 소재 임가공 LOT 편성
//sjhkim            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCHrMatlRentProcLotComp", inRecord);
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCHrMatlRentProcLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C열연소재임가공LOT편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCHrMatlRentProcLotComp()
	


		
		
	
		
		
		

	/**
	 * 오퍼레이션명 : A후판장입LotNo적용보급Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvAPlChgLotNoEffSupLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? A후판장입LotNo적용보급Lot편성
		// TC : YDYDJ264
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvAPlChgLotNoEffSupLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //A후판 장입LotNo적용 보급 Lot 편성
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procAPlChgLotNoEffSupLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="A후판장입LotNo적용보급Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvAPlChgLotNoEffSupLotComp()
	
	

		
		
		

	/**
	 * 오퍼레이션명 : C열연대차상차작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrTcarLdWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연대차상차작업요구
		// TC : YDYDJ250
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrTcarLdWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //C열연 대차상차 작업요구
//sjhkim            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCHrTcarLdWrkReq", inRecord);
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCHrTcarLdWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C열연대차상차작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCHrTcarLdWrkReq()
	


		
		
	
		
		
		

	/**
	 * 오퍼레이션명 : C열연소재이송Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrMatlFtmvLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연소재이송Lot편성
		// TC : YDYDJ253
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrMatlFtmvLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //C열연소재이송Lot편성
//sjhkim            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCHrMatlFtmvLotComp", inRecord);
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCHrMatlFtmvLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C열연소재이송Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCHrMatlFtmvLotComp()
	


		
		
	
		
		
		

	/**
	 * 오퍼레이션명 : C열연제품고간이송Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrGdsWhFtmvLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연제품고간이송Lot편성
		// TC : YDYDJ254
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrGdsWhFtmvLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //C열연제품고간이송Lot편성
//sjhkim            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCHrGdsWhFtmvLotComp", inRecord);
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCHrGdsWhFtmvLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C열연제품고간이송Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCHrGdsWhFtmvLotComp()
	


		
		
	
		
		
		

	/**
	 * 오퍼레이션명 : C열연소재대차상차Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrMatlTcarLdLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연소재대차상차Lot편성
		// TC : YDYDJ255
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrMatlTcarLdLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //C열연소재대차상차Lot편성
//sjhkim            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCHrMatlTcarLdLotComp", inRecord);
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCHrMatlTcarLdLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C열연소재대차상차Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCHrMatlTcarLdLotComp()
	


		
		
	
		
		
		

	/**
	 * 오퍼레이션명 : C열연제품대차상차Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrGdsTcarLdLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연제품대차상차Lot편성
		// TC : YDYDJ256
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrGdsTcarLdLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //C열연제품대차상차Lot편성
//sjhkim            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCHrGdsTcarLdLotComp", inRecord);
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCHrGdsTcarLdLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C열연제품대차상차Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCHrGdsTcarLdLotComp()
	


		
		
	
		
		
		

	/**
	 * 오퍼레이션명 : 후판제품이송상차Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvPlGdsFtmvCarLdLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 후판제품이송상차Lot편성
		// TC : YDYDJ257
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvPlGdsFtmvCarLdLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //후판제품이송상차Lot편성
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procPlGdsFtmvCarLdLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch
		
		szMsg="후판제품이송상차Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvPlGdsFtmvCarLdLotComp()
	


		
		
	
		
		
		

	/**
	 * 오퍼레이션명 : C연주장입LotNo적용보급Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsChgLotNoEffSupLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주장입LotNo적용보급Lot편성
		// TC : YDYDJ258
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCCsChgLotNoEffSupLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //C연주 장입LotNo적용 보급 Lot 편성
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCCsChgLotNoEffSupLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주장입LotNo적용보급Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCCsChgLotNoEffSupLotComp()
	

	
	
	/**
	 * 오퍼레이션명 : 후판제품출하차량상차작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvPlGdsDistCarLdWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 후판제품출하차량상차작업요구
		// TC : YDYDJ244
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvPlGdsDistCarLdWrkReq";
		
		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
        try {
            // 출하차량상차작업요구
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procPlGdsDistCarLdWrkReq", inRecord);
        } catch (Exception e) {         
            szMsg = szMethodName + "() " + e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

		
		szMsg = "후판제품출하차량상차작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvPlGdsDistCarLdWrkReq()
	
	/**
	 * 오퍼레이션명 : 출하차량스케줄 수정
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvDistCarSch(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 출하차량스케줄 수정
		// TC : YDYDJ293
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvDistCarSch";
		
		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
        try {
            // 출하차량상차작업요구
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procDistCarSch", inRecord);
        } catch (Exception e) {         
            szMsg = szMethodName + "() " + e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

		
		szMsg = "출하차량스케줄 수정("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvDistCarSch()
	
	
	/**
	 * 오퍼레이션명 : 코일제품출하상차Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCoilGdsDistCarLdLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 코일제품출하상차Lot편성
		// TC : YDYDJ282
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCoilGdsDistCarLdLotComp";
		
		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
        try {
            // 코일제품출하상차Lot편성
//sjhkim            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCoilGdsDistCarLdComp", inRecord);
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCoilGdsDistCarLdComp", inRecord);
        } catch (Exception e) {         
            szMsg = szMethodName + "() " + e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

		
		szMsg = "코일제품출하상차Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvCoilGdsDistCarLdLotComp()
	
	/**
	 * 오퍼레이션명 : 코일제품출하차량상차작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCoilGdsDistCarLdWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 코일제품출하차량상차작업요구
		// TC : YDYDJ244
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCoilGdsDistCarLdWrkReq";
		
		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
        try {
//sjhkim            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCoilGdsDistCarLdWrkReq", inRecord);
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCoilGdsDistCarLdWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg = szMethodName + "() " + e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

		
		szMsg = "코일제품출하차량상차작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvCoilGdsDistCarLdWrkReq()
	
	
	/**
	 * 오퍼레이션명 : 코일임가공출하상차Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCoilOutplDistCarLdLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 코일임가공출하상차Lot편성
		// TC : YDYDJ283
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCoilOutplDistCarLdLotComp";
		
		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
        try {
            // 코일임가공출하상차Lot편성
//sjhkim            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCoilOutplDistCarLdLotComp", inRecord);
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCoilOutplDistCarLdLotComp", inRecord);
        } catch (Exception e) {         
            szMsg = szMethodName + "() " + e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

		
		szMsg = "코일임가공출하상차Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvCoilOutplDistCarLdLotComp()
	

	
	
	/**
	 * 오퍼레이션명 : 후판제품출하상차Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvPlGdsDistCarLdLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 후판제품출하상차Lot편성
		// TC : YDYDJ284
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvPlGdsDistCarLdLotComp";
		
		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
        try {
            // 후판제품출하상차Lot편성
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procPlGdsDistCarLdLotComp", inRecord);
        } catch (Exception e) {         
            szMsg = szMethodName + "() " + e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

		
		szMsg = "후판제품출하상차Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvPlGdsDistCarLdLotComp()
	
	
	/**
	 * 오퍼레이션명 :   메뉴얼 작업지시 요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void ydManualReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? R3 정정입측Line-In요구
		// TC : R3YDL001
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="slabYdManualReq";
		

//		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
//			szMsg= szMethodName+"() 실행 실패";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//
//			return;
//			
//		}
		

		
		
        try {
            
            // 슬라브야드 메뉴얼 작업지시 요구
            ydEjbCon.trx("IssueWrkDmdSeEJB", "ydManualReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주 슬라브야드 메뉴얼 작업지시 요구처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of ydManualReq()

	/**
	 * 오퍼레이션명 : 후판제품 반납 대상재 Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvPlGdsRetnLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 후판제품 반납 대상재 Lot편성
		// TC : 
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvPlGdsRetnLotComp";
		
		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
        try {
            // 후판제품 반납 대상재 Lot편성
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procPlGdsRetnLotComp", inRecord);
        } catch (Exception e) {         
            szMsg = szMethodName + "() " + e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

		
		szMsg = "후판제품 반납 대상재 Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvPlGdsDistCarLdLotComp()

	
	/**
	 * 오퍼레이션명 : C연주정정L2 OHC Take-In 요구 (C3YDL006, C7YDL006)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvOHCTakeInCmpl(JDTORecord inRecord) throws JDTOException {
		String szMsg = "";
		String szMethodName = "rcvOHCTakeInCmpl";
		
		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
        try {
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procOHCTakeInCmpl", inRecord);
        } catch (Exception e) {         
            szMsg = szMethodName + "() " + e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

		
		szMsg = "OHC Take-In 요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvOHCTakeInCmpl()

	
	
	
	
	/**
	 * 오퍼레이션명 : C열연정정 Take-In 요구 (H2YDL004) - 권오창 2010.01.13
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrShearTakeInReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연정정 Take-In 요구
		// TC : H2YDL004
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMethodName = "rcvCHrShearTakeInReq";
		String szMsg        = "";
		
		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
        try {
//sjhkim            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCHrShearTakeInReq", inRecord);
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCHrShearTakeInReq", inRecord);
        } catch (Exception e) {         
            szMsg = szMethodName + "() " + e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } 
		
		szMsg = "C열연정정 Take-In 요구(H2YDL004) 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	}
	
	
	
	
	
	/**
	 * 오퍼레이션명 : C열연정정 Take-Out 요구 (H2YDL005) - 권오창 2010.01.13
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrShearTakeOutReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연정정 Take-Out 요구
		// TC : H2YDL005
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMethodName = "rcvCHrShearTakeOutReq";
		String szMsg        = "";
		
		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
        try {
//sjhkim            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCHrShearTakeOutReq", inRecord);
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCHrShearTakeOutReq", inRecord);
        } catch (Exception e) {         
            szMsg = szMethodName + "() " + e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } 
		
		szMsg = "C열연정정 Take-Out 요구(H2YDL005) 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	}

	
//!AT	
	/**
	 * 오퍼레이션명 :   메뉴얼 작업지시 요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void ydManualReqCoil(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? R3 정정입측Line-In요구
		// TC : R3YDL001
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="YdManualReqCoil";
		

//		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
//			szMsg= szMethodName+"() 실행 실패";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//
//			return;
//			
//		}
		

		
		
        try {
            
            // 코일야드 메뉴얼 작업지시 요구
//sjhkim            ydEjbCon.trx("IssueWrkDmdSeEJB", "ydManualReqCoil", inRecord);
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "ydManualReqCoil", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C열연 코일  메뉴얼 작업지시 요구처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of ydManualReq()
	
	
	/**
	 * 오퍼레이션명 :   C연주/후판 자동준비작업 LOT편성 기능
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvAutoWorkLotComp(JDTORecord inRecord) throws JDTOException {
		
		String szMsg="";
		String szMethodName="procAutoWorkLotComp";
		
		
        try {
            
            ydEjbCon.trx("IssueWrkDmdSeEJB", "procAutoWorkLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주/후판 자동준비작업 LOT편성 기능("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvAutoWorkLotComp()
	
	/**
	 * 오퍼레이션명 : 코일HYSCO출하상차Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCoilHYSCOCarLdLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 코일HYSCO출하상차Lot편성
		// TC : YDYDJ282
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCoilHYSCOCarLdLotComp";
		
		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
        try {
            // 코일제품출하상차Lot편성
//sjhkim            ydEjbCon.trx("IssueWrkDmdSeEJB", "procCoilGdsDistCarLdComp", inRecord);
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCoilHYSCOCarLdComp", inRecord);
        } catch (Exception e) {         
            szMsg = szMethodName + "() " + e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

		
		szMsg = "코일제품출하상차Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procCoilHYSCOCarLdComp()
	
	/**
	 * 오퍼레이션명 : 코일HYSCO출하차량상차작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCoilHYSCOCarLdWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 코일HYSCO출하차량상차작업요구
		// TC : YDYDJ244
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCoilHYSCOCarLdWrkReq";
		
		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
        try {
            ydEjbCon.trx("CoilIssueWrkDmdSeEJB", "procCoilHYSCOCarLdWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg = szMethodName + "() " + e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

		
		szMsg = "코일HYSCO출하차량상차작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvCoilHYSCOCarLdWrkReq()
	

	
	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//                                                
	//                     일관제철소정보관리시스템-야드관리
	//              작업요구관리-출고작업요구 Facade Session Bean
	//                          2008.09.30 YHWHman
	//                                                      
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
	
	
	
  //---------------------------------------------------------------------------
} // end of class

