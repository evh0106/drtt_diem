package com.inisteel.cim.yd.ydStock.StockSpecEnd;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.rule.GetBreRule6;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;


/**
 * 저장품제원종료 Facade Session EJB
 *
 * @ejb.bean name="StockSpecEndFaEJB" jndi-name="StockSpecEndFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class StockSpecEndFaEJBBean extends BaseSessionBean {
	
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
	 * 오퍼레이션명 : A후판가열로추출실적수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvAPlRefurExtWr(JDTORecord inRecord) throws JDTOException  {
		// 
		// YD-UC-1507 A후판가열로추출실적수신
		// TC : PRYDJ002
		// 후판조업시스템으로부터 A후판가열로추출실적 수신
		//
		//┏━┓
		//┃ 후판조업에서 후판가열로추출실적을 수신하여
		//┃ 저장품 Table의 상태를 종료
		//┗━┛
			
		String szMsg="";
		String szMethodName="rcvAPlRefurExtWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}

		try {

			// A후판가열로추출실적을 수신
			ydEjbCon.trx("StockSpecEndSeEJB", "procAPlRefurExtWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
	
		
		szMsg="A후판가열로추출실적수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvAPlRefurExtWr()
	
	


	

	/**
	 * 오퍼레이션명 : C열연가열로추출실적수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvCHrRefurExtWr(JDTORecord inRecord) throws JDTOException  {
		// 
		// YD-UC-1508 C열연가열로추출실적수신
		// TC : HRYDJ002
		// 열연조업시스템으로부터 C열연가열로추출실적수신 수신
		//
		//┏━┓
		//┃열연조업에서 C열연가열로추출실적수신 수신하여
		//┃저장품 Table의 상태를 종료
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrRefurExtWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
		try {

			// C열연가열로추출실적을 수신
			ydEjbCon.trx("StockSpecEndSeEJB", "procCHrRefurExtWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		
		
		szMsg="C열연가열로추출실적수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
		
	} // end of rcvCHrRefurExtWr()
	
	

	


	/**
	 * 오퍼레이션명 : 외판슬라브출하완료
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvOutplSlabDistCmpl(JDTORecord inRecord) throws JDTOException  {
		// 
		// YD-UC-1510 외판슬라브출하완료
		// TC : DMYDR029
		// 출하관리시스템으로부터 외판슬라브출하완료 수신
		//
		//┏━┓
		//┃ 출하관리에서 외판슬라브 출하완료실적을 수신하여
		//┃ 저장품 Table의 상태를 종료
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvOutplSlabDistCmpl";
		String szOperationName = "외판슬라브출하완료";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}

		
		ydUtils.displayRecord(szOperationName, inRecord);
		
		
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				
				/***************************************************************
				 * B열연 신규모듈 적용 여부 
				 **************************************************************/
				String sBSLAB_EFF_YN = "N";
				String sBCOIL_EFF_YN = "N";

				YdPlateCommDAO commDao = new YdPlateCommDAO();
				JDTORecord jrResult = commDao.getNewModuleEffYn();
				
				sBSLAB_EFF_YN = StringHelper.evl(jrResult.getFieldString("BSLAB_EFF_YN"),"N");
				sBCOIL_EFF_YN = StringHelper.evl(jrResult.getFieldString("BCOIL_EFF_YN"),"N");
				
				szMsg = "YdPlateCommDAO.getNewModuleEffYn()---[[[ B열연SLAB야드신규적용:" + sBSLAB_EFF_YN + " ,B열연COIL야드신규적용:" + sBCOIL_EFF_YN + " ]]]---"; 
				ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
				
				if(sYdGp.equals("2")&&sBSLAB_EFF_YN.equals("Y")) {
					//B열연  신규모듈 적용
					ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
				} else  {
					//기존모듈 호출
					ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
				}
			}else{
				// 외판슬라브출하완료'를  수신
				ydEjbCon.trx("StockSpecEndSeEJB", "procOutplSlabDistCmpl", inRecord);
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		
		szMsg="외판슬라브출하완료 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
		
	} // end of rcvOutplSlabDistCmpl()
	
	


			

	/**
	 * 오퍼레이션명 : 코일제품출하완료
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvCoilGdsDistCmpl(JDTORecord inRecord) throws JDTOException
	{
		// 
		// YD-UC-1511 코일제품출하완료
		// TC : DMYDR030
		// 출하관리시스템으로부터 코일제품출하완료 수신
		//
		//┏━┓
		//┃ 출하관리에서 코일제품 출하완료실적을 수신하여
		//┃ 저장품 Table의 상태를 종료
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCoilGdsDistCmpl";
		String szOperationName = "코일제품출하완료";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName))
		{	
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;	
		}
		
		ydUtils.displayRecord(szOperationName, inRecord);
		
		try
		{
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");
			
			if( "0".equals(sYdGp) || "1".equals(sYdGp) || "2".equals(sYdGp) || "3".equals(sYdGp) || "6".equals(sYdGp) || "8".equals(sYdGp) || "Z".equals(sYdGp) )
			{
				//AB열연 수신
				//박판열연 신규모듈 적용여부 조회
                YdPlateCommDAO commDao	= new YdPlateCommDAO();
                JDTORecord jrResult		= commDao.getYfNewModuleEffYn();

                String sACOIL_EFF_YN	= StringHelper.evl(jrResult.getFieldString("ACOIL_EFF_YN"),	"N");

                szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
				
				if( "3".equals(sYdGp) )
				{
					//B열연 신규모듈 적용
					ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
				}
				else if( "1".equals(sYdGp) && "Y".equals(sACOIL_EFF_YN))
				{
					//박판열연 COIL야드 신규모듈 적용
					ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
				}
				else
				{
					//기존모듈 호출
					ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
				}
			}
			else
			{
				/**********************************
            	 * 2열연코일야드 신규모듈 적용여부 
            	 **********************************/
            	YdPlateCommDAO ydCommDao = new YdPlateCommDAO();
            	JDTORecord jrAppYn = ydCommDao.get2HrAppYn();
            	
            	String s2HrAppYn = StringHelper.evl(jrAppYn.getFieldString("CCOIL_EFF_YN"), "N");; //2열연 코일야드 적용여부
        		
        		ydUtils.putLog(szSessionName, szMethodName,"2열연코일야드 신규모듈 적용여부 : " + s2HrAppYn, YdConstant.DEBUG);
        		
        		if ("J".equals(sYdGp) && "Y".equals(s2HrAppYn)) {
                
        			ydEjbCon.trx("CCommSeEJB", "rcvInterface", inRecord); //2열연 신규모듈		
                    
        		} else {
                    /* 기존모듈**********************************************/
    				// 코일제품출하완료  수신
    				ydEjbCon.trx("StockSpecEndSeEJB", "procCoilGdsDistCmpl", inRecord);
                    /* ******************************************************/
                }

			}
		}
		catch (Exception e)
		{			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);
		} // end of try catch
		
		szMsg="코일제품출하완료 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvCoilGdsDistCmpl()
	
	

	
				

	/**
	 * 오퍼레이션명 : 후판제품출하완료
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvPlGdsDistCmpl(JDTORecord inRecord) throws JDTOException  {
		// 
		// YD-UC-1512 후판제품출하완료
		// TC : DMYDR031
		// 출하관리시스템으로부터 후판제품출하완료 수신
		//
		//┏━┓
		//┃ 출하관리에서 후판제품 출하완료실적을 수신하여
		//┃ 저장품 Table의 상태를 종료
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvPlGdsDistCmpl";
		String szOperationName = "후판제품출하완료";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
				
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				
				// AB열연 (지시)
				ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
			}else{
				// 후판제품출하완료  수신
				ydEjbCon.trx("StockSpecEndSeEJB", "procPlGdsDistCmpl", inRecord);
    	    	//------------------------------------------------------------------------------------------------		    			
	    		String szCARLD_PNT_CD = StringHelper.evl(inRecord.getFieldString("CARLD_PNT_CD"), "");
				String szSTL_APPEAR_GP = StringHelper.evl(inRecord.getFieldString("STL_APPEAR_GP"), "");
				String szCARD_NO = StringHelper.evl(inRecord.getFieldString("CARD_NO"), "");
				
				if("*".equals(szSTL_APPEAR_GP) && "T".equals(sYdGp) && szCARLD_PNT_CD.endsWith("2") && szCARD_NO.startsWith("P")) {
					YdPlateCommDAO	commDao 		= new YdPlateCommDAO();			
					
					//TB_YD_CARPOINT 상태 변경
					commDao.update(inRecord, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0051");
					
					//TB_YD_STKCOL 상태변경
					commDao.update(inRecord, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0050");
				}	
    	    	//------------------------------------------------------------------------------------------------		    			
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		
		szMsg="후판제품출하완료 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvPlGdsDistCmpl()

	/**
	 * 오퍼레이션명 : C연주주편생산예정종료
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvCCsMslabPrdPlnEnd(JDTORecord inRecord) throws JDTOException  {
		// 
		// YD-UC-???? C연주주편생산예정종료
		// TC : YDYDJ101
		// 
		// C연주주편생산실적제원등록 기능에서 생산예정종료를 수신
		//┏━┓
		//┃ C연주주편생산실적제원등록 기능에서 생산예정종료를
		//┃ 수신하여 저장품의 상태를 종료
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCCsMslabPrdPlnEnd";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		


		try {

			// 생산예정종료  수신
			ydEjbCon.trx("StockSpecEndSeEJB", "procCCsMslabPrdPlnEnd", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="C연주주편생산예정종료 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
		
	} // end of rcvCCsMslabPrdPlnEnd()
	
	
	/**
	 * 오퍼레이션명 : A후판모슬라브제원종료
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvAPlSlabSpecEnd(JDTORecord inRecord) throws JDTOException  {
		// 
		// YD-UC-???? A후판모슬라브제원종료
		// TC : YDYDJ105
		// 
		// A후판슬라브분할제원등록 기능에서 제원종료를 수신
		//┏━┓
		//┃A후판슬라브분할제원등록 기능에서 제원종료를 
		//┃수신하여 저장품의 상태를 종료
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvAPlSlabSpecEnd";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}

		try {

			// 제원종료  수신
			ydEjbCon.trx("StockSpecEndSeEJB", "procAPlSlabSpecEnd", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="A후판모슬라브제원종료 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
		
	} // end of rcvAPlSlabSpecEnd()
	
	
	/**
	 * 오퍼레이션명 : 코일이송상차완료PDA(DMYDR072)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvCoilGdsDistCmplLdPDA(JDTORecord inRecord) throws JDTOException
	{
		String szMsg="";
		String szMethodName="rcvCoilGdsDistCmplLdPDA";
		String szOperationName = "코일이송상차완료PDA";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName))
		{
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;	
		}
 
		ydUtils.displayRecord(szOperationName, inRecord);
		
		try
		{
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");
			
 			if( "0".equals(sYdGp) || "1".equals(sYdGp) || "2".equals(sYdGp) || "3".equals(sYdGp) )
 			{
 				//AB열연 수신
				//박판열연 신규모듈 적용여부 조회
                YdPlateCommDAO commDao	= new YdPlateCommDAO();
                JDTORecord jrResult		= commDao.getYfNewModuleEffYn();

                String sACOIL_EFF_YN	= StringHelper.evl(jrResult.getFieldString("ACOIL_EFF_YN"),	"N");

                szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
				
				if( "3".equals(sYdGp) )
				{
					//B열연 신규모듈 적용
					ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
				}
				else if( "1".equals(sYdGp) && "Y".equals(sACOIL_EFF_YN) )
				{
					//박판열연 COIL야드 신규모듈 적용
					ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
				}
				else
				{
					//기존모듈 호출
					ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
				}
			}
 			else
 			{
				/**********************************
            	 * 2열연코일야드 신규모듈 적용여부 
            	 **********************************/
            	YdPlateCommDAO ydCommDao = new YdPlateCommDAO();
            	JDTORecord jrAppYn = ydCommDao.get2HrAppYn();
            	
            	String s2HrAppYn = StringHelper.evl(jrAppYn.getFieldString("CCOIL_EFF_YN"), "N");; //2열연 코일야드 적용여부
        		
        		ydUtils.putLog(szSessionName, szMethodName,"2열연코일야드 신규모듈 적용여부 : " + s2HrAppYn, YdConstant.DEBUG);
        		
        		if ("J".equals(sYdGp) && "Y".equals(s2HrAppYn)) {
                
        			ydEjbCon.trx("CCommSeEJB", "rcvInterface", inRecord); //2열연 신규모듈		
                    
        		} else {
                    /* 기존모듈**********************************************/
        			// 코일제품출하완료  수신
					ydEjbCon.trx("StockSpecEndSeEJB", "procCoilGdsDistCmplLdPDA", inRecord);
                    /* ******************************************************/
                }
			}
		}
		catch (Exception e)
		{			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg="코일이송상차완료PDA 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvCoilGdsDistCmplLdPDA()
	
	
	
	/**
	 * 오퍼레이션명 : 코일이송하차차완료PDA(DMYDR075)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvCoilGdsDistCmplUdPDA(JDTORecord inRecord) throws JDTOException
	{
		String szMsg="";
		String szMethodName="rcvCoilGdsDistCmplUdPDA";
		String szOperationName = "코일이송상차완료PDA";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName))
		{	
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;	
		}
 
		ydUtils.displayRecord(szOperationName, inRecord);
		
		try
		{
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");
			
 			if( "0".equals(sYdGp) || "1".equals(sYdGp) || "2".equals(sYdGp) || "3".equals(sYdGp) )
 			{
 				//AB열연 수신
				//박판열연 신규모듈 적용여부 조회
                YdPlateCommDAO commDao	= new YdPlateCommDAO();
                JDTORecord jrResult		= commDao.getYfNewModuleEffYn();

                String sACOIL_EFF_YN	= StringHelper.evl(jrResult.getFieldString("ACOIL_EFF_YN"),	"N");

                szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
				
				if( "3".equals(sYdGp) )
				{
					//B열연 신규모듈 적용
					ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
				}
				else if( "1".equals(sYdGp) && "Y".equals(sACOIL_EFF_YN) )
				{
					//박판열연 COIL야드 신규모듈 적용
					ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
				}
				else
				{
					ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
				}
			}
 			else
 			{
				/**********************************
            	 * 2열연코일야드 신규모듈 적용여부 
            	 **********************************/
            	YdPlateCommDAO ydCommDao = new YdPlateCommDAO();
            	JDTORecord jrAppYn = ydCommDao.get2HrAppYn();
            	
            	String s2HrAppYn = StringHelper.evl(jrAppYn.getFieldString("CCOIL_EFF_YN"), "N");; //2열연 코일야드 적용여부
        		
        		ydUtils.putLog(szSessionName, szMethodName,"2열연코일야드 신규모듈 적용여부 : " + s2HrAppYn, YdConstant.DEBUG);
        		
        		if ("J".equals(sYdGp) && "Y".equals(s2HrAppYn)) {
                
        			ydEjbCon.trx("CCommSeEJB", "rcvInterface", inRecord); //2열연 신규모듈		
                    
        		} else {
                    /* 기존모듈**********************************************/
        			// 코일제품출하완료  수신
     				ydEjbCon.trx("StockSpecEndSeEJB", "procCoilGdsDistCmplUdPDA", inRecord);
                    /* ******************************************************/
                }
			}
		}
		catch (Exception e)
		{			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg="코일이송상차완료PDA 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvCoilGdsDistCmplUdPDA()


	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//                                                
	//                     일관제철소정보관리시스템-야드관리
	//              저장품관리-저장품제원종료 Facade Session Bean
	//                          2008.09.30 YHWHman
	//                                                      
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
	
  //---------------------------------------------------------------------------
} // end of class

