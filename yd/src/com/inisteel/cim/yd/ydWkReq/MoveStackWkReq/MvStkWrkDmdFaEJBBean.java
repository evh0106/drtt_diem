package com.inisteel.cim.yd.ydWkReq.MoveStackWkReq;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTOException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;


/**
 * 이적작업요구 Facade Session EJB
 *
 * @ejb.bean name="MvStkWrkDmdFaEJB" jndi-name="MvStkWrkDmdFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class MvStkWrkDmdFaEJBBean extends BaseSessionBean {
	
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
	 * 오퍼레이션명 : A후판장입준비작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException, JDTOException
	 */
	public void rcvAPlChgPrepWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? A후판장입준비작업요구
		// TC : YDYDJ265
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvAPlChgPrepWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		
        try {
            
            //A후판 장입 준비작업 요구
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procAPlChgPrepWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="A후판장입준비작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvAPlChgPrepWrkReq()
	
	
	
	
	
	/**
	 * 오퍼레이션명 : A후판공Bed확보Lot편성

	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvAPlEmptyBedSecurLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? A후판공Bed확보Lot편성
		// TC : YDYDJ266
		//  
		//
		//┏━┓
		//┃ 
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvAPlEmptyBedSecurLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		} 
		
		
        try {
            
            //A후판 공Bed확보 Lot 편성
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procAPlEmptyBedSecurLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="A후판공Bed확보Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvAPlEmptyBedSecurLotComp()
	
	
	
		

	
	/**
	 * 오퍼레이션명 :  A후판공Bed확보작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void  rcvAPlEmptyBedSecurWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-????  A후판공Bed확보작업요구
		// TC : YDYDJ267
		//  
		//
		//┏━┓
		//┃ 
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvAPlEmptyBedSecurWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		} 
		
		
        try {
            
            //A후판 공Bed확보 작업요구
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procAPlEmptyBedSecurWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="A후판공Bed확보작업요구  처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // endo of  rcvAPlEmptyBedSecurWrkReq()
	
	
	
	
		

	/**
	 * 오퍼레이션명 :  A후판정리Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void  rcvAPlReadjLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-????  A후판정리Lot편성
		// TC : YDYDJ268
		//  
		//
		//┏━┓
		//┃ 
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvAPlReadjLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		} 
		
		
        try {
            
            //A후판 정리 Lot 편성
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procAPlReadjLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch


		
		szMsg="A후판정리Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of  rcvAPlReadjLotComp()
	
	
	
	
		

	/**
	 * 오퍼레이션명 : A후판정리작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvAPlReadjWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? A후판정리작업요구
		// TC : YDYDJ269
		//  
		//
		//┏━┓
		//┃ 
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvAPlReadjWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			 
		}
		
        try {
            
            //A후판 정리 작업요구
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procAPlReadjWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="A후판정리작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvAPlReadjWrkReq()
	
	
	
	
		

	/**
	 * 오퍼레이션명 : 후판창고선별작업Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvY4SelWrkLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 후판창고선별작업Lot편성
		// TC : YDYDJ270
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY4SelWrkLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		} 
		
		
        try {
            
            //후판창고 선별작업 편성
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procY4SelWrkLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="후판창고선별작업Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvY4SelWrkLotComp()
	
	
	
		

	
	/**
	 * 오퍼레이션명 : 후판창고선별작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvY4SelWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 후판창고선별작업요구
		// TC : YDYDJ271
		//  
		//
		//┏━┓
		//┃ 
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY4SelWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		} 
		
		
        try {
            
            //후판창고 선별작업 요구
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procY4SelWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="후판창고선별작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY4SelWrkReq()
	


		
		
	
		
		
		

	/**
	 * 오퍼레이션명 : 후판창고공Bed확보Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvY4EmptyBedSecurLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 후판창고공Bed확보Lot편성
		// TC : YDYDJ272
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY4EmptyBedSecurLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //후판창고 공Bed확보 Lot 편성
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procY4EmptyBedSecurLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="후판창고공Bed확보Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY4EmptyBedSecurLotComp()
	


		
		
	
		
		

	/**
	 * 오퍼레이션명 : 후판창고공Bed확보작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvY4EmptyBedSecurWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 후판창고공Bed확보작업요구
		// TC : YDYDJ273
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY4EmptyBedSecurWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //후판창고 공Bed확보 작업요구
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procY4EmptyBedSecurWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="후판창고공Bed확보작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY4EmptyBedSecurWrkReq()
	


		
		
	
		
		

	/**
	 * 오퍼레이션명 : 후판창고정리Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvY4ReadjLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 후판창고정리Lot편성
		// TC : YDYDJ274
		//  
		//
		//┏━┓
		//┃
		//┗━┛
			
	
		String szMsg="";
		String szMethodName="rcvY4ReadjLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //후판창고 정리 Lot 편성
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procY4ReadjLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="후판창고정리Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY4ReadjLotComp()
	


		
		
	
		
		

	/**
	 * 오퍼레이션명 : 후판창고정리작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvY4ReadjWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 후판창고정리작업요구
		// TC : YDYDJ275
		//  
		//
		//┏━┓
		//┃
		//┗━┛
			

		String szMsg="";
		String szMethodName="rcvY4ReadjWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //후판창고 정리 작업요구
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procY4ReadjWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="후판창고정리작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY4ReadjWrkReq()
	


	/**
	 * 오퍼레이션명 : C열연정정보급준비Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrShearSupPrepLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연정정보급준비Lot편성
		// TC : YDYDJ276
		//  
		//
		//┏━┓
		//┃
		//┗━┛
			

		String szMsg="";
		String szMethodName="rcvCHrShearSupPrepLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //C열연정정보급준비Lot편성
//sjhkim            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procCHrShearSupPrepLotComp", inRecord);
            ydEjbCon.trx("CoilMvStkWrkDmdSeEJB", "procCHrShearSupPrepLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C열연정정보급준비Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCHrShearSupPrepLotComp()	
		
	
	
	/**
	 * 오퍼레이션명 : C열연정정보급준비작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrShearSupPrepWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연정정보급준비작업요구
		// TC : YDYDJ277
		//  
		//
		//┏━┓
		//┃
		//┗━┛
			

		String szMsg="";
		String szMethodName="rcvCHrShearSupPrepWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
try {
            
            //C열연정정보급준비작업요구
//sjhkim    ydEjbCon.trx("MvStkWrkDmdSeEJB", "procCHrShearSupPrepWrkReq", inRecord);
    	ydEjbCon.trx("CoilMvStkWrkDmdSeEJB", "procCHrShearSupPrepWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C열연정정보급준비작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCHrShearSupPrepWrkReq()	
	
		
	
	/**
	 * 오퍼레이션명 : C열연소재정리Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrMatlAdjLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연소재정리Lot편성
		// TC : YDYDJ278
		//  
		//
		//┏━┓
		//┃
		//┗━┛
			

		String szMsg="";
		String szMethodName="rcvCHrMatlAdjLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
		try {
            
            //C열연소재정리Lot편성
//sjhkim            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procCHrMatlAdjLotComp", inRecord);
            ydEjbCon.trx("CoilMvStkWrkDmdSeEJB", "procCHrMatlAdjLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C열연소재정리Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCHrMatlAdjLotComp()
	
	
	
	/**
	 * 오퍼레이션명 : C열연소재정리작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrMatlAdjWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연소재정리작업요구
		// TC : YDYDJ279
		//  
		//
		//┏━┓
		//┃
		//┗━┛
			

		String szMsg="";
		String szMethodName="rcvCHrMatlAdjWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
		try {
            
            //C열연소재정리작업요구
//sjhkim            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procCHrMatlAdjWrkReq", inRecord);
            ydEjbCon.trx("CoilMvStkWrkDmdSeEJB", "procCHrMatlAdjWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C열연소재정리작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCHrMatlAdjWrkReq()
	
	
	
	/**
	 * 오퍼레이션명 : C열연제품정리Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrGdsAdjLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연제품정리Lot편성
		// TC : YDYDJ280
		//  
		//
		//┏━┓
		//┃
		//┗━┛
			

		String szMsg="";
		String szMethodName="rcvCHrGdsAdjLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
		try {
            
            //C열연제품정리Lot편성
//sjhkim            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procCHrGdsAdjLotComp", inRecord);
            ydEjbCon.trx("CoilMvStkWrkDmdSeEJB", "procCHrGdsAdjLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C열연제품정리Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCHrGdsAdjLotComp()
	
	
	
	/**
	 * 오퍼레이션명 : C열연제품정리작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrGdsAdjWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연제품정리작업요구
		// TC : YDYDJ281
		//  
		//
		//┏━┓
		//┃
		//┗━┛
			

		String szMsg="";
		String szMethodName="rcvCHrGdsAdjWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
		try {
            
            //C열연제품정리작업요구
//sjhkim            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procCHrGdsAdjWrkReq", inRecord);
            ydEjbCon.trx("CoilMvStkWrkDmdSeEJB", "procCHrGdsAdjWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C열연제품정리작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCHrGdsAdjWrkReq()
	
	
	////

	/**
	 * 오퍼레이션명 : C연주장입준비작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsChgPrepWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주장입준비작업요구
		// TC : YDYDJ259
		//  
		//
		//┏━┓
		//┃
		//┗━┛
			

		String szMsg="";
		String szMethodName="rcvCCsChgPrepWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
		try {
            
            //C연주장입준비작업요구
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procCCsChgPrepWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch


		
		szMsg="C연주장입준비작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCCsChgPrepWrkReq()
	
	
	/**
	 * 오퍼레이션명 : C연주 이적(동내,동간) LOT편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCMvLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주 이적(동내,동간) LOT편성
		// TC : YDYDJ260
		//  
		//
		//┏━┓
		//┃
		//┗━┛
			

		String szMsg="";
		String szMethodName="rcvCCMvLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
		try {
            
            //C연주 이적(동내,동간) LOT편성
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procCCMvLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주 이적(동내,동간) LOT편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCCsMatlFtmvOrdLotComp()
		
		

	/**
	 * 오퍼레이션명 : C연주공Bed확보Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsEmptyBedSecurLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주공Bed확보Lot편성
		// TC : YDYDJ260
		//  
		//
		//┏━┓
		//┃
		//┗━┛
			

		String szMsg="";
		String szMethodName="rcvCCsEmptyBedSecurLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
		try {
            
            //C연주공Bed확보Lot편성
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procCCsEmptyBedSecurLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주공Bed확보Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCCsEmptyBedSecurLotComp()
	


		
		
	
		
		

	/**
	 * 오퍼레이션명 : C연주공Bed확보작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsEmptyBedSecurWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주공Bed확보작업요구
		// TC : YDYDJ261
		//  
		//
		//┏━┓
		//┃
		//┗━┛
			

		String szMsg="";
		String szMethodName="rcvCCsEmptyBedSecurWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
		try {
            
            //C연주공Bed확보작업요구
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procCCsEmptyBedSecurWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주공Bed확보작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCCsEmptyBedSecurWrkReq()
	


		
		
	
		
		

	/**
	 * 오퍼레이션명 : C연주정리Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsReadjLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주정리Lot편성
		// TC : YDYDJ262
		//  
		//
		//┏━┓
		//┃
		//┗━┛
			

		String szMsg="";
		String szMethodName="rcvCCsReadjLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
		try {
            
            //C연주정리Lot편성
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procCCsReadjLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주정리Lot편성 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCCsReadjLotComp()
	


		
		
	
		
		

	/**
	 * 오퍼레이션명 : C연주정리작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsReadjWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주정리작업요구
		// TC : YDYDJ263
		//  
		//
		//┏━┓
		//┃
		//┗━┛
			

		String szMsg="";
		String szMethodName="rcvCCsReadjWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
		try {
            
            //C연주정리작업요구
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procCCsReadjWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주정리작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCCsReadjWrkReq()
	

	/**
	 * 오퍼레이션명 : 대상재 상단 더미재 이적Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvMvDummyMtlAboveTgMtlLotComp(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 대상재 상단 더미재 이적Lot편성
		// TC : 
		//  
		//
		//┏━┓
		//┃
		//┗━┛
			

		String szMsg="";
		String szMethodName="rcvMvDummyMtlAboveTgMtlLotComp";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //대상재 상단 더미재 이적Lot편성
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procMvDummyMtlAboveTgMtlLotComp", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="대상재 상단 더미재 이적Lot편성 처리("+szMethodName+") Session 호출 성공";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvMvDummyMtlAboveTgMtlLotComp()	
	
	
	/**
	 * 오퍼레이션명 : 준비스케줄 LOT편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCPrepLotCompByCapa(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 준비스케줄 LOT편성
		// TC : YDYDJ290
		//  
		//
		//┏━┓
		//┃
		//┗━┛
			

		String szMsg="";
		String szMethodName="rcvCCPrepLotCompByCapa";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //준비스케줄 LOT편성
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procCCPrepLotCompByCapa", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="준비스케줄 LOT편성 처리("+szMethodName+") Session 호출 성공";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCCPrepLotCompByCapa()
	
	/**
	 * 오퍼레이션명 : 준비스케줄 LOT편성 ---> 이적 작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvFtmvOrdLotReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 준비스케줄 LOT편성
		// TC : YDYDJ290
		//  
		//
		//┏━┓
		//┃
		//┗━┛
			

		String szMsg="";
		String szMethodName="rcvFtmvOrdLotReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //이적 작업요구
            ydEjbCon.trx("MvStkWrkDmdSeEJB", "procFtmvOrdLotReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="이적 작업요구 처리("+szMethodName+") Session 호출 성공";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvFtmvOrdLotReq()
	

	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//                                                
	//                     일관제철소정보관리시스템-야드관리
	//              작업요구관리-이적작업요구 Facade Session Bean
	//                          2008.09.30 YHWHman
	//                                                      
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
	
  //---------------------------------------------------------------------------
} // end of class

