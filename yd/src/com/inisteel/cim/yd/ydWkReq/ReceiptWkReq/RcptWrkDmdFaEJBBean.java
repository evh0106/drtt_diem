package com.inisteel.cim.yd.ydWkReq.ReceiptWkReq;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTOException;

import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.common.exception.DAOException;

/**
 * 입고작업요구 Facade Session EJB
 *
 * @ejb.bean name="RcptWrkDmdFaEJB" jndi-name="RcptWrkDmdFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100" 
 * @ejb.transaction type="Required"
 */
public class RcptWrkDmdFaEJBBean extends BaseSessionBean {
	
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
	 * 오퍼레이션명 : C3 OHC Take-Out요구 (C3YDL003, C7YDL003)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvC3OhcTakeOutReq(JDTORecord inRecord) throws JDTOException {
		String szMsg="";
		String szMethodName="rcvC3OhcTakeOutReq";
		
		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
        try {
            ydEjbCon.trx("RcptWrkDmdSeEJB", "procC3OhcTakeOutReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() 결과 : " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);
        } // end of try catch
		
		szMsg="C3 OHC Take-Out요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvC3OhcTakeOutReq()

	
	/**
	 * 오퍼레이션명 : C3 Take-Out완료 (C3YDL004, C7YDL004)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvC3TakeOutCmpl(JDTORecord inRecord) throws JDTOException {
		String szMsg="";
		String szMethodName="rcvC3TakeOutCmpl";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
        try {
            // C연주Take-Out 완료수신
            ydEjbCon.trx("RcptWrkDmdSeEJB", "procC3TakeOutCmpl", inRecord);
        } catch (Exception e) {         
            szMsg =szMethodName + "() 결과 : " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);
        } // end of try catch
		
		szMsg="C3 Take-Out완료 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvC3TakeOutCmpl()

	
	/**
	 * 오퍼레이션명 : Y3 Take-Out완료 (Y3YDL012)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvY3TakeOutCmpl(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? Y3 Take-Out완료
		// TC : Y3YDL012
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY3TakeOutCmpl";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		

		
		
        try {
            
            ydEjbCon.trx("RcptWrkDmdSeEJB", "procY3TakeOutCmpl", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() 결과 : " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="Y3 Take-Out완료 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvY3TakeOutCmpl()
	
	
	
	
	

	/**
	 * 오퍼레이션명 : H1 압연분기Line-Off요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvR2MillBrLineOffReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? R2 압연분기Line-Off요구
		// TC : H1YDL001
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvR2MillBrLineOffReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		

		
		
        try {
            
            //C열연 압연분기 Line-Off 작업요구
//sjhkim            ydEjbCon.trx("RcptWrkDmdSeEJB", "procR2MillBrLineOffReq", inRecord);
            ydEjbCon.trx("CoilRcptWrkDmdSeEJB", "procR2MillBrLineOffReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="H1 압연분기Line-Off요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvR2MillBrLineOffReq()
	
	
	
	/**
	 * 오퍼레이션명 : H1 재열재 Take-Out 요구
	 * 2009.08.27    권오창
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvR2ReHeatTakeOutReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? H1 재열재 Take-Out 요구
		// TC : H1YDL002
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg = "";
		String szMethodName = "rcvR2ReHeatTakeOutReq";
		
		
		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {			
			// H1 재열재 Take-Out 요구
			ydEjbCon.trx("RcptWrkDmdSeEJB", "procR2ReHeatTakeOutReq", inRecord);
		} catch (Exception e) {         
			szMsg = szMethodName + "() " + e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch
		
		szMsg = "재열재 Take-Out 요구 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvR2ReHeatTakeOutReq()
	
	
	
	
	

	/**
	 * 오퍼레이션명 : H2 정정출측Line-Off요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException
	 */
	public void rcvR3ShearOutLineOffReq(JDTORecord inRecord) throws DAOException {
		//
		// YD-UC-???? R3 정정출측Line-Off요구
		// TC : H2YDL003
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvR3ShearOutLineOffReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		

		
		
        try {
            
            //C열연 정정출측 Line-Off 작업요구
//sjhkim            ydEjbCon.trx("RcptWrkDmdSeEJB", "procR3ShearOutLineOffReq", inRecord);
            ydEjbCon.trx("CoilRcptWrkDmdSeEJB", "procR3ShearOutLineOffReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);

        } // end of try catch

		
		szMsg="H2 정정출측Line-Off요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvR3ShearOutLineOffReq()
	
	
	
	
	

	/**
	 * 오퍼레이션명 : H2 수소탱크Line-Off요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvR3WtclTnkLineOffReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? H2 수소탱크Line-Off요구
		// TC : H2YDL004
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvR3WtclTnkLineOffReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		

		
		
        try {
            
            //C열연 수냉탱크 Line-Off 요구
//sjhkim            ydEjbCon.trx("RcptWrkDmdSeEJB", "procR3WtclTnkLineOffReq", inRecord);
            ydEjbCon.trx("CoilRcptWrkDmdSeEJB", "procR3WtclTnkLineOffReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="H2 수소탱크Line-Off요구처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvR3WtclTnkLineOffReq()
	
	
	
	
	

	/**
	 * 오퍼레이션명 : P2 Pilling실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvP2PillingWr(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? P2 Pilling실적
		// TC : P2YDL001
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvP2PillingWr";
		/*
		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		*/
        try {
            ydEjbCon.trx("RcptWrkDmdSeEJB", "procP2PillingWr", inRecord);
        } catch (Exception e) {         
            szMsg =szMethodName + "() 결과 : " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

		
		szMsg="P2 Pilling실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvP2PillingWr()
	
	
	
	
	

	/**
	 * 오퍼레이션명 : P2 BookOut실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvP2BookOutReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? P2 BookOut실적
		// TC : 
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvP2BookOutReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}

		
        try {
            
            ydEjbCon.trx("RcptWrkDmdSeEJB", "procP2BookOutReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() 결과 : " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="P2 Book-Out 실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvP2BookOutReq()


	/**
	 * 오퍼레이션명 : P2 Book-In 실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvP2BookInReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? P2 Book-In 실적
		// TC : P2YDL003
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvP2BookInReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}

		
        try {
            
            ydEjbCon.trx("RcptWrkDmdSeEJB", "procP2BookInReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() 결과 : " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="P2 Book-In실적 처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvP2BookInReq()
	

	
	/**
	 * 오퍼레이션명 : C연주불출구CarryOut요구 (YDYDJ201)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsExtSectCarryOutReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주불출구CarryOut요구
		// TC : YDYDJ201
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCCsExtSectCarryOutReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		

		
        try {
            
            // C연주불출구Carry-Out요구
            ydEjbCon.trx("RcptWrkDmdSeEJB", "procCCsExtSectCarryOutReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주불출구CarryOut요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		

	} // end of rcvCCsExtSectCarryOutReq()
	
	
	
	
	
	/**
	 * 오퍼레이션명 : A후판슬라브야드CARRY-OUT요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvY3CarryOutReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? A후판슬라브야드CARRY-OUT요구
		// TC : YDYDJ202
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY3CarryOutReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		

		
		
        try {
            
            // A후판 CARRY-OUT 요구
            ydEjbCon.trx("RcptWrkDmdSeEJB", "procY3CarryOutReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="A후판슬라브야드CARRY-OUT요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvY3CarryOutReq()
	
	
	
	
	
	/**
	 * 오퍼레이션명 : A후판창고야드Carry-Out요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvY4CarryOutReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? A후판창고야드Carry-Out요구
		// TC : YDYDJ203
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY4CarryOutReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		

		
		
        try {
            
            // A후판 CARRY-OUT 요구
            ydEjbCon.trx("RcptWrkDmdSeEJB", "procY4CarryOutReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="A후판창고야드Carry-Out요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvY4CarryOutReq()
	
	/**
	 * 오퍼레이션명 : 연주/후판 슬라브 이상재 등록/해제 -공정관리 호출 (YDYDJ298)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvAbmtlOccurSend(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 연주/후판 슬라브 이상재 등록/해제 -공정관리 호출
		// TC : YDYDJ298
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvAbmtlOccurSend";
		
        try {
            
            ydEjbCon.trx("RcptWrkDmdSeEJB", "procAbmtlOccurSend", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch
	} // end of rcvAbmtlOccurSend()
	
	/**
	 * 오퍼레이션명 : 후판제품창고 오버롤 체크 (YDYDJ297)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvPlateOverRollCheck(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 후판제품창고 오버롤 체크
		// TC : YDYDJ297
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvPlateOverRollCheck";
		
        try {
            
            ydEjbCon.trx("RcptWrkDmdSeEJB", "procPlateOverRollCheck", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch
	} // end of rcvPlateOverRollCheck()
	
	/**
	 * 오퍼레이션명 : C연주OHCCarry-Out요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsOhcCarryOutReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주OHCCarry-Out요구
		// TC : YDYDJ204 
		// 
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCCsOhcCarryOutReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}

 
		
		
        try {
            
            // C연주 OHC CARRY-OUT 요구
            ydEjbCon.trx("RcptWrkDmdSeEJB", "procCCsOhcCarryOutReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주OHCCarry-Out요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCCsOhcCarryOutReq()
	
	
	
	
	/**
	 * 오퍼레이션명 : A후판차량하차작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvAplCarUdWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? A후판차량하차작업요구
		// TC : YDYDJ205
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvAplCarUdWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}

 
		
		
        try {
            
            //차량하차작업 요구
            ydEjbCon.trx("RcptWrkDmdSeEJB", "procAplCarUdWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="A후판차량하차작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvAplCarUdWrkReq()
	
	
	
	
	

	/**
	 * 오퍼레이션명 : C연주차량하차작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsCarUdWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주차량하차작업요구
		// TC : YDYDJ206
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCCsCarUdWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	

		
		
        try {
            
            //차량하차작업 요구
            ydEjbCon.trx("RcptWrkDmdSeEJB", "procCCsCarUdWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주차량하차작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		
	} // end of rcvCCsCarUdWrkReq()
	
	
	
	
	

	/**
	 * 오퍼레이션명 : C열연차량하차작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrCarUdWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연차량하차작업요구
		// TC : YDYDJ207
		//  
		//
		//┏━┓
		//┃ 
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrCarUdWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		
		
        try {
            
            //차량하차작업 요구
//sjhkim            ydEjbCon.trx("RcptWrkDmdSeEJB", "procCHrCarUdWrkReq", inRecord);
            ydEjbCon.trx("CoilRcptWrkDmdSeEJB", "procCHrCarUdWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C열연차량하차작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCHrCarUdWrkReq()
	

				

	
	/**
	 * 오퍼레이션명 : 후판창고차량하차작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvY4CarUdWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 후판창고차량하차작업요구
		// TC : YDYDJ208
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY4CarUdWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		

		
		
        try {
            
            //차량하차작업 요구
            ydEjbCon.trx("RcptWrkDmdSeEJB", "procY4CarUdWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="후판창고차량하차작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY4CarUdWrkReq()
	

				

			

	/**
	 * 오퍼레이션명 : C연주대차하차작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCCsTcarUdWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C연주대차하차작업요구
		// TC : YDYDJ209
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCCsTcarUdWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		

		
		
        try {
            
            //차량하차작업 요구
            ydEjbCon.trx("RcptWrkDmdSeEJB", "procCCsTcarUdWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C연주대차하차작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCCsTcarUdWrkReq()
	

				

			
				

	/**
	 * 오퍼레이션명 : C열연대차하차작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrTcarUdWrkReq(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? C열연대차하차작업요구
		// TC : YDYDJ210
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrTcarUdWrkReq";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		

		
		
        try {
            
            //대차하차작업 요구
//sjhkim            ydEjbCon.trx("RcptWrkDmdSeEJB", "procCHrTcarUdWrkReq", inRecord);
            ydEjbCon.trx("CoilRcptWrkDmdSeEJB", "procCHrTcarUdWrkReq", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

		
		szMsg="C열연대차하차작업요구 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCHrTcarUdWrkReq()
	

	
	

	/**
	 * 오퍼레이션명 : A후판 Book-Out실적 (PRYDJ006) 2009.12.10    권오창
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvAPlBookOutWr(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? A후판 Book-Out실적
		// TC : PRYDJ006
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMethodName = "rcvAPlBookOutWr";
		String szMsg        = "";
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(inRecord, "T");  // JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "A후판 Book-Out실적 (" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg = szMethodName + "() 실행 실패";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			return ;
		}
		
		
        try {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// procAPlBookOutWr call 시  inRecord 에 logId SET 추가 개선
inRecord.setField("LOG_ID", logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
			
            // A후판 Book-Out실적 처리
            ydEjbCon.trx("RcptWrkDmdSeEJB", "procAPlBookOutWr", inRecord);
        } catch (Exception e) {         
            szMsg = szMethodName + "() " + e.getMessage(); 
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            throw new JDTOException(szMsg);
        } 
		
		szMsg = "A후판 Book-Out실적 (" + szMethodName + ") 완료";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	}
	
	/**
	 * 오퍼레이션명 : 후판제품창고 바코드 인식 수신실적 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvAPlBarCodeInfo(JDTORecord inRecord) throws JDTOException {
		
		String szMethodName = "rcvAPlBarCodeInfo";
		String szMsg        = "";
		
        try {
			ydEjbCon.trx("RcptWrkDmdSeEJB", "procAPlBarCodeInfo", inRecord);
        } catch (Exception e) {         
            szMsg = szMethodName + "() " + e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } 
		
		szMsg = "A후판 바코드실적 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} 


	
	
	
	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//                                                
	//                     일관제철소정보관리시스템-야드관리
	//              작업요구관리-입고작업요구 Facade Session Bean
	//                          2008.09.30 YHWHman
	//                                                      
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
	
	
  //---------------------------------------------------------------------------
} // end of class

