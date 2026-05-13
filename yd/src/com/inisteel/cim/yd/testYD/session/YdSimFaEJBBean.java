package com.inisteel.cim.yd.testYD.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecordFactory;

import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdConstant;
import jspeed.base.property.PropertyService;
import com.inisteel.cim.common.jms.JmsQueueSender;

/**
 * 저장품제원등록 Facade Session EJB
 *
 * @ejb.bean name="YdSimFaEJB" jndi-name="YdSimFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class YdSimFaEJBBean extends BaseSessionBean {

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
	 * 오퍼레이션명 : DAO TEST
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void testDao(JDTORecord inRecord) throws JDTOException{

		
		String szMsg="";
		String szMethodName="testDao";
		

//		if( !rcvMsgChk(inRecord, szMethodName)){			
//			szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//
//			return;
//			
//		}
		
		
		


		try {
			
			// DAO TEST
			ydEjbCon.trx("YdSimSeEJB", "testDao", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="DAO TEST("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of testDao()
	
	
	

	/**
	 *      [A] 오퍼레이션명 : 메시지 수신 및 분석 처리
	 * 	
	 * @param inRecord
	 * @return: true/false
	 * @throws JDTOException
	 */
	private boolean rcvMsgChk(JDTORecord inRecord, String szMethodName)
									throws JDTOException {
		

		String szMsg="";
		YdUtils ydUtils =new YdUtils();
		YdTcConst ydTcConst =new YdTcConst();
		
		//
		// 수신메시지의 TC 유효성 검사
		//
		String szRcvTcCode=ydUtils.getTcCode(inRecord);
		if(szRcvTcCode==null){
			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			return false;
		}
		
		
		
		//
		// 수신 메시지 로깅
		//
		szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode;
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
		int nRtc=0;
		
		// 수신 Tc Check
		nRtc=ydTcConst.chkTcType(szRcvTcCode);
		if(nRtc==1){
			
			// 내부 인터페이스 TC 수신
			szMsg="[DEBUG] 내부인터페이스 TC 수신 : " + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		}else if(nRtc==2){
			
			// 외부 인터페이스 TC 수신
			szMsg="[DEBUG] 외부인터페이스 TC 수신 : " + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
		}else{
			// Unknown TC 수신
			szMsg="[ERROR] Unknown TC Error : " + szRcvTcCode + " ErrCode="+nRtc;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			return false;
			
		} // end of if()
		
		
		
		
		// 
		// TC Code vs Method Check
		//
		if( !(ydTcConst.chkTcMethod(szRcvTcCode, szMethodName)) ){
			szMsg="[ERROR] Unknown TC Method TCCode="+szRcvTcCode+" MethodName="+szMethodName;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
			return false;
		
		} // end of if()
		
		
		
		return true;
	
	} // end of rcvMsgChk()
	
	
	
	
	
	
	// ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//   EJBConnector.trx( java.lang.String methodName)) 
    //  
	//   EJBConnector.trx( java.lang.String methodName, 
	//                                     java.lang.Class[] argTypes, 
	//                                     java.lang.Object[] argValues) 
	// ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
	
	
	
	
	/**
	 * 오퍼레이션명 : BED 금지/해제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void reqBedProhRel(JDTORecord inRecord) throws JDTOException{
		//
		// YD-UC-9001 BED 금지 / 해제
		// TC : YDYD9001
		// 야드관리시스템으로부터 해당 BED 금지/해제 수신
		//
		//┏━┓
		//┃ 야드관리시스템으로부터 BED에 대한 금지 해제 설정
		//┗━┛
		
		String szMsg="";
		String szMethodName="reqBedProhRel";
		

		if( !rcvMsgChk(inRecord, szMethodName)){			
			szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		
		


		try {
			
			// BED 금지 / 해제 요청
			ydEjbCon.trx("YdSimSeEJB", "bedProhRel", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="BED 금지/해제 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of reqBedProhRel()
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 스케줄 금지/해제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void reqSchProhRel(JDTORecord inRecord) throws JDTOException{
		//
		// YD-UC-9002 스케줄 금지 / 해제
		// TC : YDYD9002
		// 야드관리시스템으로부터 스케줄 금지/해제 수신
		//
		//┏━┓
		//┃ 야드관리시스템으로부터 해당 스케줄에 대한 금지 해제 설정
		//┗━┛
		
		String szMsg="";
		String szMethodName="reqSchProhRel";
		

		if( !rcvMsgChk(inRecord, szMethodName)){			
			szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		
		


		try {
			
			// 스케줄 금지 / 해제 요청
			ydEjbCon.trx("YdSimSeEJB", "schProhRel", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="스케줄 금지/해제 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of reqSchProhRel()
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 스케줄 취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void reqSchCncl(JDTORecord inRecord) throws JDTOException{
		//
		// YD-UC-9003 스케줄 취소
		// TC : YDYD9003
		// 야드관리시스템으로부터 스케줄 취소 수신
		//
		//┏━┓
		//┃ 야드관리시스템으로부터 해당 스케줄에 대한 취소 설정
		//┗━┛
		
		String szMsg="";
		String szMethodName="reqSchCncl";
		

		if( !rcvMsgChk(inRecord, szMethodName)){			
			szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		
		


		try {
			
			// 스케줄 취소 요청
			ydEjbCon.trx("YdSimSeEJB", "schCncl", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="스케줄 취소 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of reqSchCncl()
	
	
	
	/**
	 * 오퍼레이션명 : 작업 취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void reqWrkCncl(JDTORecord inRecord) throws JDTOException{
		//
		// YD-UC-9004 작업 취소
		// TC : YDYD9003
		// 야드관리시스템으로부터 작업 취소 수신
		//
		//┏━┓
		//┃ 야드관리시스템으로부터 해당 작업에 대한 취소 설정
		//┗━┛
		
		String szMsg="";
		String szMethodName="reqWrkCncl";
		
		if( !rcvMsgChk(inRecord, szMethodName)){			
			szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
		try {
			// 작업 취소 요청 
			ydEjbCon.trx("YdSimSeEJB", "wrkCncl", inRecord);
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);
		} // end of try catch
		
		szMsg="작업 취소 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of reqWrkCncl()
	
    /**
     *      [A] 오퍼레이션명 : C연주Take-Out 완료수신
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return
     * @throws JDTOException
     */
    public void rcvCCTakeOutCmpl(JDTORecord inRecord) throws JDTOException{
        
        String szMsg="";
        String szMethodName="rcvCCTakeOutCmpl";
        

        if( !rcvMsgChk(inRecord, szMethodName)){            
            szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
            
        }   


        try {
            
            // C연주Take-Out 완료수신
            ydEjbCon.trx("YdSimSeEJB", "ccTakeOutCmpl", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() 결과 : " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

        
        szMsg="C연주Take-Out 완료수신("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
        
        
    } //end of rcvCCTakeOutCmpl
    
    /**
     *      [A] 오퍼레이션명 : C연주불출구Carry-Out요구
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return
     * @throws JDTOException
     */
    public void reqCCExtSectCarryOutDmd(JDTORecord inRecord) throws JDTOException{
        
        String szMsg="";
        String szMethodName="reqCCExtSectCarryOutDmd";
        

        if( !rcvMsgChk(inRecord, szMethodName)){            
            szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
            
        }
        

        try {
            
            // C연주불출구Carry-Out요구
            ydEjbCon.trx("YdSimSeEJB", "ccExtSectCarryOutDmd", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

        
        szMsg="C연주불출구Carry-Out요구("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
        
    } //end of reqCCExtSectCarryOutDmd
    
    
    /**
     *      [A] 오퍼레이션명 : C연주 OHC Take-Out 완료수신
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return
     * @throws JDTOException
     */
    public void rcvCCOHCTakeOutCmpl(JDTORecord inRecord) throws JDTOException{
        
        String szMsg="";
        String szMethodName="rcvCCOHCTakeOutCmpl";
        

        if( !rcvMsgChk(inRecord, szMethodName)){            
            szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
            
        }   


        try {
            
            ydEjbCon.trx("YdSimSeEJB", "ccOHCTakeOutCmpl", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() 결과 : " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

        
        szMsg="C연주 OHC TAKE_OUT 완료수신("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
        
        
    } //end of rcvCCOHCTakeOutCmpl 
    
    
    /**
     *      [A] 오퍼레이션명 : C연주 OHC Carry-Out 요구
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return
     * @throws JDTOException
     */
    public void reqCCOHCCarryOutDmd(JDTORecord inRecord) throws JDTOException{
        
        String szMsg="";
        String szMethodName="reqCCOHCCarryOutDmd";
        

        if( !rcvMsgChk(inRecord, szMethodName)){            
            szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
            
        }
        

        try {
            
            // C연주 OHC CARRY-OUT 요구
            ydEjbCon.trx("YdSimSeEJB", "ccOHCCarryOutDmd", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

        
        szMsg="C연주 OHC Carry-Out 요구("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
        
    } //end of reqCCOHCCarryOutDmd
    

    /**
     *      [A] 오퍼레이션명 : A후판 슬라브 야드 TAKE-OUT 완료수신
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return
     * @throws JDTOException
     */
    public void rcvPLSlabYdTakeOutCmpl(JDTORecord inRecord) throws JDTOException{
        
        String szMsg="";
        String szMethodName="rcvPLSlabYdTakeOutCmpl";
        

        if( !rcvMsgChk(inRecord, szMethodName)){            
            szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
            
        }   


        try {
            
            ydEjbCon.trx("YdSimSeEJB", "plSlabYdTakeOutCmpl", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() 결과 : " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

        
        szMsg="A후판 TAKE-OUT 완료수신("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
        
        
    } //end of rcvPLSlabYdTakeOutCmpl   
  
    
    /**
     *      [A] 오퍼레이션명 : A후판 슬라브 야드 CARRY-OUT 요구
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return
     * @throws JDTOException
     */
    public void reqPLSlabYdCarryOutDmd(JDTORecord inRecord) throws JDTOException{
        
        String szMsg="";
        String szMethodName="reqPLSlabYdCarryOutDmd";
        

        if( !rcvMsgChk(inRecord, szMethodName)){            
            szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
            
        }
        

        try {
            
            // A후판 CARRY-OUT 요구
            ydEjbCon.trx("YdSimSeEJB", "plSlabYdCarryOutDmd", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

        
        szMsg="A후판 CARRY-OUT 요구("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
        
    } //end of reqPLSlabYdCarryOutDmd
    

    /**
     *      [A] 오퍼레이션명 : A후판 창고 야드 BOOK_OUT 완료수신
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return
     * @throws JDTOException
     */
    public void rcvPLWhYdBookOutCmpl(JDTORecord inRecord) throws JDTOException{
        
        String szMsg="";
        String szMethodName="rcvPLWhYdBookOutCmpl";
        

        if( !rcvMsgChk(inRecord, szMethodName)){            
            szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
            
        }   


        try {
            
            ydEjbCon.trx("YdSimSeEJB", "plWhYdBookOutCmpl", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() 결과 : " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

        
        szMsg="A후판 창고 야드 BOOK_OUT 완료수신("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
        
        
    } //end of rcvPLWhYdBookOutCmpl 
    
    
    /**
     *      [A] 오퍼레이션명 : A후판창고야드 Carry-Out요구
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return
     * @throws JDTOException
     */
    public void reqPLWhYdCarryOutDmd(JDTORecord inRecord) throws JDTOException{
        
        String szMsg="";
        String szMethodName="reqPLWhYdCarryOutDmd";
        

        if( !rcvMsgChk(inRecord, szMethodName)){            
            szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
            
        }
        

        try {
            
            // A후판 CARRY-OUT 요구
            ydEjbCon.trx("YdSimSeEJB", "plWhYdCarryOutDmd", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

        
        szMsg="A후판창고야드 Carry-Out요구("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
        
    } //end of reqPLWhYdCarryOutDmd
     
    /**
     *      [A] 오퍼레이션명 : A후판 차량하차작업 요구
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return
     * @throws JDTOException
     */
    public void reqAPLCarCarudWrkDmd(JDTORecord inRecord) throws JDTOException{
        
        String szMsg="";
        String szMethodName="reqAPLCarCarudWrkDmd";
        

        if( !rcvMsgChk(inRecord, szMethodName)){            
            szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
            
        }
        

        try {
            
            //차량하차작업 요구
            ydEjbCon.trx("YdSimSeEJB", "mAPLCarCarudWrkDmd", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

        
        szMsg="A후판 차량하차작업 요구("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
        
    } //end of reqAPLCarCarudWrkDmd
    
    
    /**
     *      [A] 오퍼레이션명 : C연주 차량하차작업 요구
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return
     * @throws JDTOException
     */
    public void reqCCCCarCarudWrkDmd(JDTORecord inRecord) throws JDTOException{
        
        String szMsg="";
        String szMethodName="reqCCCCarCarudWrkDmd";
        

        if( !rcvMsgChk(inRecord, szMethodName)){            
            szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
            
        }
        

        try {
            
            //차량하차작업 요구
            ydEjbCon.trx("YdSimSeEJB", "mCCCCarCarudWrkDmd", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

        
        szMsg="C연주 차량하차작업 요구("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
        
    } //end of reqCCCarCarudWrkDmd
    
    
    
    /**
     *      [A] 오퍼레이션명 : C연주 대차하차작업 요구
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return
     * @throws JDTOException
     */
    public void reqCCCTcarCarudWrkDmd(JDTORecord inRecord) throws JDTOException{
        
        String szMsg="";
        String szMethodName="reqCCCTcarCarudWrkDmd";
        

        if( !rcvMsgChk(inRecord, szMethodName)){            
            szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
            
        }
        

        try {
            
            //차량하차작업 요구
            ydEjbCon.trx("YdSimSeEJB", "mCCCTcarCarudWrkDmd", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

        
        szMsg="C연주 대차하차작업 요구("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
        
    } //end of reqCCCTcarCarudWrkDmd

    
    /**
     *      [A] 오퍼레이션명 : C열연 대차하차작업 요구
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return
     * @throws JDTOException
     */
    public void reqCHRTcarCarudWrkDmd(JDTORecord inRecord) throws JDTOException{
        
        String szMsg="";
        String szMethodName="reqCHRTcarCarudWrkDmd";
        

        if( !rcvMsgChk(inRecord, szMethodName)){            
            szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
            
        }
        

        try {
            
            //대차하차작업 요구
            ydEjbCon.trx("YdSimSeEJB", "mCHRTcarCarudWrkDmd", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

        
        szMsg="C열연 대차하차작업 요구("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
        
    } //end of reqCHRTcarCarudWrkDmd
    
    /**
     *      [A] 오퍼레이션명 : C열연 차량하차작업 요구
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return
     * @throws JDTOException
     */
    public void reqCHRCarCarudWrkDmd(JDTORecord inRecord) throws JDTOException{
        
        String szMsg="";
        String szMethodName="reqCHRCarCarudWrkDmd";
        

        if( !rcvMsgChk(inRecord, szMethodName)){            
            szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
            
        }
        

        try {
            
            //차량하차작업 요구
            ydEjbCon.trx("YdSimSeEJB", "mCHRCarCarudWrkDmd", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

        
        szMsg="C열연 차량하차작업 요구("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
        
    } //end of reqCHRCarCarudWrkDmd
    
    /**
     *      [A] 오퍼레이션명 : 후판창고 차량하차작업 요구
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return
     * @throws JDTOException
     */
    public void reqPLWHCarCarudWrkDmd(JDTORecord inRecord) throws JDTOException{
        
        String szMsg="";
        String szMethodName="reqPLWHCarCarudWrkDmd";
        

        if( !rcvMsgChk(inRecord, szMethodName)){            
            szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
            
        }
        

        try {
            
            //차량하차작업 요구
            ydEjbCon.trx("YdSimSeEJB", "mPLWHCarCarudWrkDmd", inRecord);

        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(szMsg);

        } // end of try catch

        
        szMsg="후판창고 차량하차작업 요구("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
        
    } //end of reqPLWHCarCarudWrkDmd
    
//=================================================================================================
//	김진욱 BEGIN
//=================================================================================================
    /**
	 * 오퍼레이션명 : 권상처리Backup
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void reqCraneLdHdBkup(JDTORecord inRecord) throws JDTOException{
		//
		// YD-UC-9005 권상처리Backup
		// TC : YDYD9005
		// 야드관리시스템으로부터 권상처리Backup 수신
		//
		//┏━┓
		//┃ 야드관리시스템으로부터 권상처리Backup처리 수신
		//┗━┛
		
		String szMsg="";
		String szMethodName="reqCraneLdHdBkup";
		

		if( !rcvMsgChk(inRecord, szMethodName)){			
			szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		
		


		try {
			
			// 권상처리 요청
			ydEjbCon.trx("YdSimSeEJB", "craneLdHd", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="권상처리Backup 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of reqCraneLdHdBkup()
	
	
		
	
	
	/**
	 * 오퍼레이션명 : 권하처리Backup
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void reqCraneUdHdBkup(JDTORecord inRecord) throws JDTOException{
		//
		// YD-UC-9006 권하처리Backup
		// TC : YDYD9006
		// 야드관리시스템으로부터 권하처리Backup 수신
		//
		//┏━┓
		//┃ 야드관리시스템으로부터 권하처리Backup 수신
		//┗━┛
		
		String szMsg="";
		String szMethodName="reqCraneUdHdBkup";
		

		if( !rcvMsgChk(inRecord, szMethodName)){			
			szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		
		


		try {
			
			// 권하처리 요청 
			ydEjbCon.trx("YdSimSeEJB", "craneUdHd", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="권하처리Backup 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of reqCraneUdHdBkup()
   
    
//=================================================================================================
//	김진욱 END
//=================================================================================================	   
	
	
//=================================================================================================
//	연은정 BEGIN
//=================================================================================================	
    
    /**
	 *      [A] 오퍼레이션명 : 연주전단실적 수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCcFsWr(JDTORecord inRecord) throws JDTOException{
	
		String szMsg="";
		String szMethodName="rcvCcFsWr";
		

		if( !rcvMsgChk(inRecord, szMethodName)){			
			szMsg="[ERROR] "+ szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		

		try {
			
			// 연주전단실적 수신
			ydEjbCon.trx("YdSimSeEJB", "CcFsWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="연주전단실적("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	}
	
	/**
	 *      [A] 오퍼레이션명 : 연주전단지시 수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCcFsWo(JDTORecord inRecord) throws JDTOException{

		String szMsg="";
		String szMethodName="rcvCcFsWo";


		try {

			// 연주전단지시  수신
		ydEjbCon.trx("YdSimSeEJB", "CcFsWo", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="연주전단지시 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	}
//=================================================================================================
//	연은정 END
//=================================================================================================	

	
	
	
	/**
	 * 오퍼레이션명 : Test Delegate
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 * @author YHWHman 2009.01.20
	 */
	public void testDelegate(JDTORecord inRecord) throws JDTOException{

		
		String szMsg="";
		String szMethodName="testDelegate";
		String szTcCode="";
		
		YdDelegate ydDelegate =new YdDelegate();
		YdUtils ydUtils = new YdUtils();
		YdTcConst ydTcConst =new YdTcConst();
		
		int nTcCnt =ydTcConst.regTcMap.size();
			
		JDTORecord dataRecord =JDTORecordFactory.getInstance().create();
		

		
		
		JmsQueueSender jmsQSnder =new JmsQueueSender();
		PropertyService propertyService = null;	
		String szQName=null;
		
		try{
		
			propertyService = PropertyService.getInstance();
			szQName= propertyService.getProperty("common.properties", "jms.queue.YD_MDB_QUEUE");
			jmsQSnder.initQueueService(szQName);
				
				//for(int i=0;i<nTcCnt;i++){
			for(int i=0;i<1;i++){

				szTcCode=(String)ydTcConst.regTcMap.get(""+i);
				System.out.println("regTcMap["+i+"]="+szTcCode);


				
				dataRecord.setField("JMS_TC_CD", szTcCode );
				dataRecord.setField("JMS_TC_CREATE_DDTT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
				
				dataRecord.setField("MSG_GP",  new String("A") );
				dataRecord.setField("STL_APPEAR_GP", new String("A") );
				dataRecord.setField("STL_NO",  new String("ABCDEFGHIJKLM") );
				dataRecord.setField("PLNT_PROC_CD", new String("AB") );
				dataRecord.setField("STL_PROG_CD",  new String("A"));
				dataRecord.setField("ORD_YEOJAE_GP",  new String("A") );
				dataRecord.setField("ORD_NO",  new String("ABCDEFGHIJ") );
				dataRecord.setField("ORD_DTL",  new String("ABC") );
				dataRecord.setField("BUY_SLAB_NO",   new String("ABCDEFGHIJABCDEFGHIJABCDEFGHIJ") );
				dataRecord.setField("REPR_MATL_RT_GP",  new String("AB") );
				dataRecord.setField("CCM_NO",   new String("A") );
				dataRecord.setField("ORD_HCR_GP",   new String("A") );
				dataRecord.setField("HCR_GP",   new String("A") );
				dataRecord.setField("SCARFING_YN",   new String("Y") );
				dataRecord.setField("SCARFING_DONE_YN",   new String("Y") );
				dataRecord.setField("SCARFING_PATTERN",   new String("ABCDE") );
				dataRecord.setField("SCARFING_DEPTH",   new String("99") );
				dataRecord.setField("REHEAT_SLAB_GP",   new String("Y") );
				dataRecord.setField("SLAB_FORM_GP",   new String("A") );
				dataRecord.setField("QNAME",   new String("jms/YD_EAI_QUEUE") );
				dataRecord.setField("JMS_TC_MESSAGE",   new String("여호와는 나의 목자시니 내게 부족함이 없으리로다.") );
				//dataRecord.setField("JMS_TC_MESSAGE",   new String(dataRecord.toString()) );


				jmsQSnder.send(dataRecord);

				//Thread.sleep(250);

			} // end of for()



		}catch(Exception e){
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			throw new JDTOException(szMsg);
		}
		
		szMsg="Test Delegate("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of testDelegate()
	
	/**
	 * 오퍼레이션명 : reqTestAAAAA
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 * @author YHWHman 2009.01.20
	 */
	public void reqTestAAAAA(JDTORecord inRecord) throws JDTOException{
		String szLogMsg="";
		String szMethodName="reqTestAAAAA";

		szLogMsg = "YdSimFaEJBBean::reqTestAAAAA() IN";
		ydUtils.putLog(YdSimFaEJBBean.class.getName(), szMethodName, szLogMsg, YdConstant.INFO);

		if( !rcvMsgChk(inRecord, szMethodName)){            
			szLogMsg="[ERROR] "+ szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg,1);
			return;
		}
	
		try {
			ydEjbCon.trx("YdSimSeEJB", "mTestAAAAA", inRecord);
		} catch (Exception e) {         
			szLogMsg = szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg,1);

			throw new JDTOException(szLogMsg);

		} // end of try catch


		szLogMsg="reqTest ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg,4);

		szLogMsg = "YdSimFaEJBBean::reqTestAAAAA() OUT";
		ydUtils.putLog(YdSimFaEJBBean.class.getName(), szMethodName, szLogMsg, YdConstant.INFO);
	} //end of reqTest    	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : reqTestBBBBB
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 * @author YHWHman 2009.01.20
	 */
	public void reqTestBBBBB(JDTORecord inRecord) throws JDTOException{
		String szLogMsg="";
		String szMethodName="reqTestBBBBB";

		szLogMsg = "YdSimFaEJBBean::reqTestBBBBB() IN";
		ydUtils.putLog(YdSimFaEJBBean.class.getName(), szMethodName, szLogMsg, YdConstant.INFO);

		if( !rcvMsgChk(inRecord, szMethodName)){            
			szLogMsg="[ERROR] "+ szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg,1);
			return;
		}
	
		try {
			ydEjbCon.trx("YdSimSeEJB", "mTestBBBBB", inRecord);
		} catch (Exception e) {         
			szLogMsg = szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg,1);

			throw new JDTOException(szLogMsg);

		} // end of try catch


		szLogMsg="reqTest ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg,4);

		szLogMsg = "YdSimFaEJBBean::reqTestBBBBB() OUT";
		ydUtils.putLog(YdSimFaEJBBean.class.getName(), szMethodName, szLogMsg, YdConstant.INFO);
	} //end of reqTest 
	
	/**
	 * 오퍼레이션명 : reqTestBBBBB
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 * @author YHWHman 2009.01.20
	 */
	public void reqTestCCC(JDTORecord inRecord) throws JDTOException{
		String szLogMsg="";
		String szMethodName="reqTestCCC";

		szLogMsg = "YdSimFaEJBBean::reqTestCCC() IN";
		ydUtils.putLog(YdSimFaEJBBean.class.getName(), szMethodName, szLogMsg, YdConstant.INFO);

		if( !rcvMsgChk(inRecord, szMethodName)){            
			szLogMsg="[ERROR] "+ szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg,1);
			return;
		}
	
		try {
			ydEjbCon.trx("YdSimSeEJB", "mTestCCC", inRecord);
		} catch (Exception e) {         
			szLogMsg = szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg,1);

			throw new JDTOException(szLogMsg);

		} // end of try catch


		szLogMsg="reqTestCCC ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg,4);

		szLogMsg = "YdSimFaEJBBean::reqTestCCC() OUT";
		ydUtils.putLog(YdSimFaEJBBean.class.getName(), szMethodName, szLogMsg, YdConstant.INFO);
	} //end of reqTest   
	
	
	
	/**
	 * 오퍼레이션명 : reqTestDDD
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 * @author YEON
	 */
	public void reqTestDDD(JDTORecord inRecord) throws JDTOException{
		String szLogMsg="";
		String szMethodName="reqTestDDD";

		szLogMsg = "YdSimFaEJBBean::reqTestDDD() IN";
		ydUtils.putLog(YdSimFaEJBBean.class.getName(), szMethodName, szLogMsg, YdConstant.INFO);

		if( !rcvMsgChk(inRecord, szMethodName)){            
			szLogMsg="[ERROR] "+ szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg,1);
			return;
		}
	
		try {
			ydEjbCon.trx("YdSimSeEJB", "mTestDDD", inRecord);
		} catch (Exception e) {         
			szLogMsg = szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg,1);

			throw new JDTOException(szLogMsg);

		} // end of try catch


		szLogMsg="reqTestDDD ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg,4);

		szLogMsg = "YdSimFaEJBBean::reqTestDDD() OUT";
		ydUtils.putLog(YdSimFaEJBBean.class.getName(), szMethodName, szLogMsg, YdConstant.INFO);
	} //end of reqTest    	
  //---------------------------------------------------------------------------
} // end of class
