/**
 * @(#)RtModRegFaEJBBean.java
 * 
 * @version			1.0
 * @author 			현대제철
 * @date			2012/11/14
 * 
 * @description		이클래스는 행선변경등록 Facade EJB클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.01  2013/04/03   허철호      허철호      신규설비 추가 
 */

package com.inisteel.cim.yd.ydStock.RouteModReg;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;

/**
 * 행선변경등록 Facade Session EJB
 *
 * @ejb.bean name="RtModRegFaEJB" jndi-name="RtModRegFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class RtModRegFaEJBBean extends BaseSessionBean {

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
	 * 오퍼레이션명 : 주편재설계확정지시
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException, JDTOException
	 */ 
	public void rcvMslabDsCmmtOrd(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1301 주편재설계확정지시
		// TC : CTYDJ012
		// 생산통제시스템으로부터 주편재설계확정지시 수신
		//
		//┏━┓
		//┃ 생산통제에서 주편재설계확정지시를 수신하여 저장품 Table에 주편/Slab/Plate 관련 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvMslabDsCmmtOrd";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}

		try {

			// 주편재설계확정지시등록을 수신
			ydEjbCon.trx("RtModRegSeEJB", "procMslabDsCmmtWo", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="주편재설계확정지시 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



	} // end of rcvMslabDsCmmtOrd()





	/**
	 * 오퍼레이션명 : 외판행선변경확정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvOutplRtChng(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1303 외판행선변경확정
		// TC : CTYDJ013
		// 생산통제시스템으로부터 외판행선변경확정 수신
		//
		//┏━┓
		//┃ 생산통제에서 외판(슬라브)행선변경 실적을 수신하여 저장품 Table에 야드슬라브보급행선 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvOutplRtChng";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}



		try {

			// 외판슬라브행선변경확정등록 수신
			ydEjbCon.trx("RtModRegSeEJB", "procOutplRtChng", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		szMsg="외판행선변경확정 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



	} // end of rcvOutplRtChng()






	/**
	 * 오퍼레이션명 : 코일제품보류확정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */   
	public void rcvCoilGdsHoldCommt(JDTORecord inRecord) throws JDTOException  {
		//
		// YD-UC-1306 코일제품보류확정
		// TC : DMYDR002
		// 출하관리시스템으로부터 코일제품보류확정 수신
		//
		//┏━┓
		//┃출하관리에서 창고에 적치된 코일제품 보류(이상재)에 대해 조치를 확정한 실적을 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvCoilGdsHoldCommt"; 
		String szOperationName = "코일제품보류확정";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}
		
		ydUtils.displayRecord(szOperationName, inRecord);
		

		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");			
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				// AB열연 수신
				ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
			}else{
				// 코일제품보류확정 수신
				ydEjbCon.trx("RtModRegSeEJB", "procCoilGdsHoldCommt", inRecord);
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="코일제품보류확정 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



	} // end of rcvCoilGdsHoldCommt()





	/**
	 * 오퍼레이션명 : 후판제품보류확정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */  
	public void rcvPlGdsHoldCommt(JDTORecord inRecord) throws JDTOException  {
		//
		// YD-UC-1307 후판제품보류확정
		// TC : DMYDR003
		// 출하관리시스템으로부터 후판제품보류확정 수신
		//
		//┏━┓
		//┃출하관리에서 창고에 적치된 후판제품 보류(이상재)에 대해 조치를 확정한 실적을 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvPlGdsHoldCommt"; 
		String szOperationName = "후판제품보류확정";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}
		ydUtils.displayRecord(szOperationName, inRecord);

		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				
				if(sCancelChk.equals("Y")){
					// AB열연 (취소)
					ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
				}else {
					// AB열연 (지시)
					ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
				}
			}else{
//				if(sCancelChk.equals("Y")){
//					 //(취소)
//					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
//				}else {
					// 후판제품보류확정 수신
					ydEjbCon.trx("RtModRegSeEJB", "procPlGdsHoldCommt", inRecord);
//				}
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="후판제품보류확정 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


	} // end of rcvPlGdsHoldCommt()

	/**
	 * 오퍼레이션명 : 슬라브충당실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvSlabMatchWr(JDTORecord inRecord) throws JDTOException  {
		//
		// YD-UC-1308 슬라브충당실적
		// TC : PMYDJ001
		// 공정계획시스템으로부터 슬라브충당실적 수신
		//
		//┏━┓
		//┃ 공정계획에서 슬라브충당실적을 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvSlabMatchWr"; 

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		try {

			// 슬라브충당실적 수신
			ydEjbCon.trx("RtModRegSeEJB", "procSlabMatchWr", inRecord);
			
			/*
			 * AB열연 시스템으로 인터페이스 전달  
			 */
			//ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="슬라브충당실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


	} // end of rcvSlabMatchWr()

	/**
	 * 오퍼레이션명 : 코일충당실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvCoilMatchWr(JDTORecord inRecord) throws JDTOException
	{
		//
		// YD-UC-1309 코일충당실적
		// TC : PTYDJ001 
		// 진행관리시스템으로부터 코일충당실적 수신
		//
		//┏━┓
		//┃ 진행관리에서 Coil충당실적을 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛
		String szMsg="";
		String szMethodName="rcvCoilMatchWr"; 

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName))
		{
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}

		try
		{	
			// 코일충당실적 수신
			//ydEjbCon.trx("RtModRegSeEJB", "procCoilMatchWr", inRecord);
			
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
			
			//박판열연 신규모듈 적용여부 조회
            YdPlateCommDAO commDaoYf	= new YdPlateCommDAO();
            JDTORecord jrResultYf		= commDaoYf.getYfNewModuleEffYn();

            String sACOIL_EFF_YN	= StringHelper.evl(jrResultYf.getFieldString("ACOIL_EFF_YN"),	"N");

            szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
            ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
			
			if("Y".equals(sBCOIL_EFF_YN))
			{
				//B열연 COIL야드 신규모듈 적용
				ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
			}
			else
			{
				//기존모듈 호출
				ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
			}
			
			if("Y".equals(sACOIL_EFF_YN))
            {
            	//박판열연 COIL야드 신규모듈 적용
				ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
			}
		}
		catch (Exception e)
		{			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="코일충당실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCoilMatchWr()

	/**
	 * 오퍼레이션명 : A후판제품행선변경실적수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvAPlGdsRtChngWr(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1311 A후판제품행선변경실적수신
		// TC : 
		// 후판조업시스템으로부터 A후판제품행선변경실적 수신
		//
		//┏━┓
		//┃후판조업에서 정정 출측 이상재 발생 실적을 송신하면저장품에 관련항목을 등록
		//┗━┛

		String szMsg="";
		String szMethodName="rcvAPlGdsRtChngWr"; 

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}

		try {

			// A후판제품행선변경실적수신 수신
			ydEjbCon.trx("RtModRegSeEJB", "procAPlGdsRtChngWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="A후판제품행선변경실적수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvAPlGdsRtChngWr()


	/**
	 * 오퍼레이션명 : 코일제품반납대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvCoilGdsRetnWait(JDTORecord inRecord) throws JDTOException
	{
		// 신규
		// YD-UC-1312 코일제품반납대기등록
		// TC : DMYDR008
		// 출하관리시스템으로부터 코일제품반납대기실적 수신
		//
		//┏━┓
		//┃출하관리에서 코일제품 반납대기등록실적을 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvCoilGdsRetnWait"; 
		String szOperationName = "코일제품반납대기";

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
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
			
			if( "0".equals(sYdGp) || "1".equals(sYdGp) || "2".equals(sYdGp) || "3".equals(sYdGp) )
			{
				// AB열연 수신				
				//박판열연 신규모듈 적용여부 조회
                YdPlateCommDAO commDao	= new YdPlateCommDAO();
                JDTORecord jrResult		= commDao.getYfNewModuleEffYn();

                String sACOIL_EFF_YN	= StringHelper.evl(jrResult.getFieldString("ACOIL_EFF_YN"),	"N");

                szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
				
				if("Y".equals(sCancelChk))
				{
					// AB열연 (취소)
					if( "2".equals(sYdGp) || "3".equals(sYdGp) )
					{
						//B열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("BCoilL3RcvSeEJB", "receiveCancel", inRecord);
					}
					else if( "1".equals(sYdGp) && "Y".equals(sACOIL_EFF_YN))
					{
						//박판열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
					}
					else 
					{
						//기존모듈 호출
						ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
					}
				}
				else
				{
					// AB열연 (지시)
					if( "2".equals(sYdGp) || "3".equals(sYdGp) )
					{
						//B열연 COIL야드 신규모듈 적용
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
        			/* 기존모듈**************************************************/
        			if(sCancelChk.equals("Y")) {
    					// (취소)
    					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
    				} else {
    					// 코일제품반납대기등록 수신
    					ydEjbCon.trx("RtModRegSeEJB", "procCoilGdsRetnWait", inRecord);
    				}
        			/* ***********************************************************/
                }
			}
		}
		catch (Exception e)
		{			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg="코일제품반납대기 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCoilGdsRetnWait()





	/**
	 * 오퍼레이션명 : 후판제품반납대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:  
	 * @throws JDTOException
	 */ 
	public void rcvPlGdsRetnWait(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1313 후판제품반납대기등록
		// TC : DMYDR009
		// 출하관리시스템으로부터 후판제품반납대기실적 수신
		//
		//┏━┓
		//┃출하관리에서 후판제품 반납대기등록실적을 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvPlGdsRetnWait"; 
		String szOperationName = "후판제품반납대기";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}

		ydUtils.displayRecord(szOperationName, inRecord);

		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				
				if(sCancelChk.equals("Y")){
					// AB열연 (취소)
					ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
				}else {
					// AB열연 (지시)
					ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
				}
			}else{
//				if(sCancelChk.equals("Y")){
//					//(취소)
//					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
//				}else {
					// 후판제품반납대기실적 수신
					ydEjbCon.trx("RtModRegSeEJB", "procPlGdsRetnWait", inRecord);
//				}
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="후판제품반납대기 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



	} // end of rcvPlGdsRetnWait()





	/**
	 * 오퍼레이션명 : 외판슬라브목전
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:  
	 * @throws JDTOException
	 */ 
	public void rcvOutplSlabOrdtrn(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1314 외판슬라브목전
		// TC : DMYDR013
		// 출하관리시스템으로부터 외판슬라브목전실적 수신
		//
		//┏━┓
		//┃출하관리에서 외판슬라브 목전실적을 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvOutplSlabOrdtrn"; 
		String szOperationName = "외판슬라브목전";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}
		ydUtils.displayRecord(szOperationName, inRecord);

		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
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
				
				if(sCancelChk.equals("Y")){
					// AB열연 (취소)
					if((sYdGp.equals("2")&&sBSLAB_EFF_YN.equals("Y")) || (sYdGp.equals("3")&&sBCOIL_EFF_YN.equals("Y"))	) {
						//B열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("BCoilL3RcvSeEJB", "receiveCancel", inRecord);
					} else {
						//기존모듈 호출
						ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
					}
				}else {
					// AB열연 (지시)
					if((sYdGp.equals("2")&&sBSLAB_EFF_YN.equals("Y")) || (sYdGp.equals("3")&&sBCOIL_EFF_YN.equals("Y"))	) {
						//B열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
					} else  {
						//기존모듈 호출
						ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
					}
				}
			}else{
				if(sCancelChk.equals("Y")){
					// (취소)
					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
				}else {
					// 외판슬라브목전실적 수신
					ydEjbCon.trx("RtModRegSeEJB", "procOutplSlabOrdtrn", inRecord);
				}				
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="외판슬라브목전 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



	} // end of rcvOutplSlabOrdtrn()





	/**
	 * 오퍼레이션명 : 코일제품목전
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvCoilGdsOrdtrn(JDTORecord inRecord) throws JDTOException
	{
		// 신규
		// YD-UC-1315 코일제품목전실적등록
		// TC : DMYDR014
		// 출하관리시스템으로부터 코일제품목전실적 수신
		//
		//┏━┓
		//┃출하관리에서 코일제품 목전실적을 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvCoilGdsOrdtrn"; 
		String szOperationName = "코일제품목전";

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
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
			
			if( "0".equals(sYdGp) || "1".equals(sYdGp) || "2".equals(sYdGp) || "3".equals(sYdGp) )
			{
				
				//AB열연 수신
				//박판열연 신규모듈 적용여부 조회
                YdPlateCommDAO commDao	= new YdPlateCommDAO();
                JDTORecord jrResult		= commDao.getYfNewModuleEffYn();

                String sACOIL_EFF_YN	= StringHelper.evl(jrResult.getFieldString("ACOIL_EFF_YN"),	"N");

                szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
				
				if(sCancelChk.equals("Y"))
				{
					// AB열연 (취소)
					if( "2".equals(sYdGp) || "3".equals(sYdGp))
					{
						//B열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("BCoilL3RcvSeEJB", "receiveCancel", inRecord);
					}
					else if( "1".equals(sYdGp) && "Y".equals(sACOIL_EFF_YN))
					{
						//박판열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
					}
					else
					{
						//기존모듈 호출
						ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
					}
				}
				else
				{
					// AB열연 (지시)
					if( "2".equals(sYdGp) || "3".equals(sYdGp))
					{
						//B열연 COIL야드 신규모듈 적용
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
        			if("Y".equals(sCancelChk)) {
						// (취소)
						ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
        			} else {
						// 코일제품목전실적 수신
						ydEjbCon.trx("RtModRegSeEJB", "procCoilGdsOrdtrn", inRecord);
        			}
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

		szMsg="코일제품목전 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvCoilGdsOrdtrn()





	/**
	 * 오퍼레이션명 : 후판제품목전
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:  
	 * @throws JDTOException
	 */ 
	public void rcvPlageGdsOrdtrn(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1316 후판제품목전실적등록
		// TC : DMYDR015
		// 출하관리시스템으로부터 후판제품목전실적 수신
		//
		//┏━┓
		//┃출하관리에서 후판제품 목전실적을 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvPlageGdsOrdtrn"; 
		String szOperationName = "후판제품목전";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}

		ydUtils.displayRecord(szOperationName, inRecord);
		
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				
				if(sCancelChk.equals("Y")){
					// AB열연 (취소)
					ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
				}else {
					// AB열연 (지시)
					ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
				}
			}else{
//				if(sCancelChk.equals("Y")){
//					//(취소)
//					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
//				}else {
//					// 후판제품목전실적 수신
					ydEjbCon.trx("RtModRegSeEJB", "procPlageGdsOrdtrn", inRecord);
//				}
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="후판제품목전 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



	} // end of rcvPlageGdsOrdtrn()





	/**
	 * 오퍼레이션명 : 외판슬라브출하지시대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:  
	 * @throws JDTOException
	 */ 
	public void rcvOutplSlabDistOrdWait(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1317 외판슬라브출하지시대기
		// TC : DMYDR004
		// 출하관리시스템으로부터 외판슬라브출하지시대기 수신
		//
		//┏━┓
		//┃출하관리에서 외판슬라브 출하지시대기등록실적을 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvOutplSlabDistOrdWait"; 
		String szOperationName = "외판슬라브출하지시대기";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}
		ydUtils.displayRecord(szOperationName, inRecord);


		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");			
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				// AB열연 수신
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
				
				if((sYdGp.equals("2")&&sBSLAB_EFF_YN.equals("Y")) || (sYdGp.equals("3")&&sBCOIL_EFF_YN.equals("Y"))	) {
					//B열연 COIL야드 신규모듈 적용
					ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
				} else  {
					//기존모듈 호출
					ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
				}
			}else{
				// 외판슬라브출하지시대기 수신
				ydEjbCon.trx("RtModRegSeEJB", "procOutplSlabDistOrdWait", inRecord);
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="외판슬라브출하지시대기 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



	} // end of rcvOutplSlabDistOrdWait()





	/**
	 * 오퍼레이션명 : 외판슬라브운송지시대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:  
	 * @throws JDTOException
	 */ 
	public void rcvOutplSlabTrnOrdWait(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1316 외판슬라브운송지시대기
		// TC : DMYDR016
		// 출하관리시스템으로부터 외판슬라브운송지시 수신
		//
		//┏━┓
		//┃
		//┗━┛

		String szMsg="";
		String szMethodName="rcvOutplSlabTrnOrdWait"; 
		String szOperationName = "외판슬라브운송지시대기";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}
		ydUtils.displayRecord(szOperationName, inRecord);
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
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
				
				if(sCancelChk.equals("Y")){
					// AB열연 (취소)
					if((sYdGp.equals("2")&&sBSLAB_EFF_YN.equals("Y")) || (sYdGp.equals("3")&&sBCOIL_EFF_YN.equals("Y"))	) {
						//B열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("BCoilL3RcvSeEJB", "receiveCancel", inRecord);
					} else {
						//기존모듈 호출
						ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
					}
				}else {
					// AB열연 (지시)
					if((sYdGp.equals("2")&&sBSLAB_EFF_YN.equals("Y")) || (sYdGp.equals("3")&&sBCOIL_EFF_YN.equals("Y"))	) {
						//B열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
					} else  {
						//기존모듈 호출
						ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
					}
				}
			}else{
				if(sCancelChk.equals("Y")){
					// (취소)
					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
				}else {
					// 외판슬라브운송지시대기 수신
					ydEjbCon.trx("RtModRegSeEJB", "procOutplSlabTrnOrdWait", inRecord);
				}
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch


		szMsg="외판슬라브운송지시대기 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



	} // end of rcvOutplSlabTrnOrdWait()





	/**
	 * 오퍼레이션명 : 코일제품출하지시대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvCoilGdsDistOrdWait(JDTORecord inRecord) throws JDTOException
	{
		// 신규
		// YD-UC-1319 코일제품출하지시대기등록
		// TC : DMYDR005
		// 출하관리시스템으로부터 코일제품출하지시대기 수신
		//
		//┏━┓
		//┃출하관리에서 코일제품 출하지시대기등록실적을 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCoilGdsDistOrdWait"; 
		String szOperationName = "코일제품출하지시대기";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName))
		{
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			return;
		}

		try 
		{
			ydUtils.displayRecord(szOperationName, inRecord);
			
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");		
			ydUtils.putLog(szSessionName, szMethodName, "야드구분:>>>>>>>>"+sYdGp, YdConstant.DEBUG);
			
			if( "0".equals(sYdGp) || "1".equals(sYdGp) || "2".equals(sYdGp) || "3".equals(sYdGp) )
			{
				// AB열연 수신				
				//박판열연 신규모듈 적용여부 조회
                YdPlateCommDAO commDao	= new YdPlateCommDAO();
                JDTORecord jrResult		= commDao.getYfNewModuleEffYn();

                String sACOIL_EFF_YN	= StringHelper.evl(jrResult.getFieldString("ACOIL_EFF_YN"),	"N");

                szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
				
				if( "2".equals(sYdGp) || "3".equals(sYdGp) )
				{
					//B열연 COIL야드 신규모듈 적용
					ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
				}
				else if ( "1".equals(sYdGp) && "Y".equals(sACOIL_EFF_YN) )
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
    				// 코일제품출하지시대기 수신
    				ydEjbCon.trx("RtModRegSeEJB", "procCoilGdsDistOrdWait", inRecord);   //기존모듈    			
        		}
			}
		}
		catch (Exception e)
		{			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg="코일제품출하지시대기 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvCoilGdsDistOrdWait()





	/**
	 * 오퍼레이션명 : 코일제품운송지시,제품운송상차지시
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:  
	 * @throws JDTOException
	 */ 
	public void rcvCoilGdsTrnOrd(JDTORecord inRecord) throws JDTOException
	{
		// 신규
		// YD-UC-1320 코일제품운송지시등록,제품운송상차지시등록
		// TC : DMYDR020,DMYDR060
		// 출하관리시스템으로부터 코일제품운송지시실적 수신
		//
		//┏━┓
		//┃출하관리에서 코일제품 운송지시를 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvCoilGdsTrnOrd"; 
		String szOperationName = "코일제품운송지시";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName))
		{
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			return;
		}
		
		ydUtils.displayRecord(szOperationName, inRecord);

		try
		{
			String sTcCd = StringHelper.evl(inRecord.getFieldString("TC_CODE"), "");	
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
			
			//--------------------------------------------------------------------------------------------
			// 1,2 후판 육송출하 고도화 분기 모듈 
			//------------------------------------------------------------------------------------[시작]---
			if(sTcCd.equals("DMYDR060"))
			{ 
				//TC코드가 DMYDR060 이고
				String szYdGp1 = StringHelper.evl(inRecord.getFieldString("YD_GP1"), "");
				
				if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp1))
				{
					//2020.1.6 신규모듈관련 분기 추가
					//후판제품 신규모듈 적용여부 조회
	                if(PlateGdsYdUtil.isPlateNewMoudleApply("DM")){
	                	ydEjbCon.trx("PlateYdRcvFaEJB", "rcvInterface", inRecord);
	                }else{
						//후판제품운송지시등록(출하고도화) 호출
						ydEjbCon.trx("RtModRegSeEJB", "procPlateGdsTrnOrd3GUpGrade", inRecord);
	                }
			    	return;
				}
			}
			//------------------------------------------------------------------------------------[종료]---
			
			//육송출하고도화
//			ymCommonDAO dao = ymCommonDAO.getInstance();
//			 List chkList = null;
//			String QueryId 	= "com.inisteel.cim.yd.dao.chklist";
//			chkList = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)chkList.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");	    	
//	    	ydUtils.putLog(szSessionName, szMethodName, "◑◑◑◑◑ TC_CODE:"+sTcCd+" , CHK:"+CHK, YdConstant.INFO);
//	    	
//	    	if(CHK.equals("Y") && sTcCd.equals("DMYDR060")){
	    		sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP1"), "");
	    		inRecord.setField("YD_GP", 		sYdGp);
	    		
	    		if( "0".equals(sYdGp) || "1".equals(sYdGp) || "2".equals(sYdGp) || "3".equals(sYdGp) )
	    		{	
	    			//AB열연 수신
					//박판열연 신규모듈 적용여부 조회
	                YdPlateCommDAO commDao	= new YdPlateCommDAO();
	                JDTORecord jrResult		= commDao.getYfNewModuleEffYn();

	                String sACOIL_EFF_YN	= StringHelper.evl(jrResult.getFieldString("ACOIL_EFF_YN"),	"N");

	                szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
	                ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
					
					if(sCancelChk.equals("Y"))
					{
						// AB열연 (취소)
						if( "2".equals(sYdGp) || "3".equals(sYdGp) ) 
						{
							//B열연 COIL야드 신규모듈 적용
							ydEjbCon.trx("BCoilL3RcvSeEJB", "receiveCancel", inRecord);
						}
						else if( "1".equals(sYdGp) && "Y".equals(sACOIL_EFF_YN))
						{
							//박판열연 COIL야드 신규모듈 적용
							ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
						}
						else
						{
							//기존모듈 호출
							ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
						}
					}
					else
					{
						// AB열연 (지시)
						if( "2".equals(sYdGp) || "3".equals(sYdGp) )
						{
							//B열연 COIL야드 신규모듈 적용 --> 바라보는 View Table 이 다르다.
							ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
						}
						else if( "1".equals(sYdGp) && "Y".equals(sACOIL_EFF_YN))
						{
							//박판열연 COIL야드 신규모듈 적용
							ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
						}
						else
						{
							//기존모듈 호출 **
							ydEjbCon.trx("RtModRegSeEJB", "procCoilGdsTrnOrdNEW", inRecord);
						}
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
	        			if ("Y".equals(sCancelChk)) {
		    				//C열연/후판(취소)
							ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);	
						} else {
			    			// 제품운송상차지시 수신
							ydEjbCon.trx("RtModRegSeEJB", "procCoilGdsTrnOrdNEW", inRecord);
						}
	                    /* ******************************************************/
	                }
	    		}
	    		
//	    		//-------------------------------------------------------------------------------
//				//신규 방식 적용
//	    		if(sCancelChk.equals("Y")){
//	    			//취소 처리######################################################################
//	    			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
//	    				// AB열연 (취소)
//						ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
//	    			}else {
//	    				//C열연/후판(취소)
//						ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);	
//	    			}
//	    			//#############################################################################
//	    		}else{
//	    			//운송상차지시처리################################################################
//
//	    			// 제품운송상차지시 수신
//					ydEjbCon.trx("RtModRegSeEJB", "procCoilGdsTrnOrdNEW", inRecord);
//					
//	    			//#############################################################################
//	    		}
//	    		//-------------------------------------------------------------------------------
//	    	}else{
//	    		//-------------------------------------------------------------------------------
//	    		//기존 방식 적용 
//				if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
//					
//					if(sCancelChk.equals("Y")){
//						// AB열연 (취소)
//						ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
//					}else {
//						// AB열연 (지시)
//						ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
//					}
//				}else{
//					if(sCancelChk.equals("Y")){
//						// (취소)
//						ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
//					}else {
//						// 코일제품운송지시 수신
//						ydEjbCon.trx("RtModRegSeEJB", "procCoilGdsTrnOrd", inRecord);
//					}
//				}
//				//-------------------------------------------------------------------------------
//	    	}
		}
		catch (Exception e)
		{			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg="코일제품운송지시 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCoilGdsTrnOrd()





	/**
	 * 오퍼레이션명 : 후판제품출하지시대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvPlateGdsDistOrdWait(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1321 후판제품출하지시대기등록
		// TC : DMYDR006
		// 출하관리시스템으로부터 후판제품출하지시대기 수신
		//
		//┏━┓
		//┃출하관리에서 후판제품 출하지시대기등록실적을 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛


		String szMsg="";
		String szMethodName="rcvPlateGdsDistOrdWait"; 
		String szOperationName = "후판제품출하지시대기";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}
		ydUtils.displayRecord(szOperationName, inRecord);

	
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");			
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				// AB열연 수신
				ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
			}else{
				// 후판제품출하지시대기 수신
				ydEjbCon.trx("RtModRegSeEJB", "procPlateGdsDistOrdWait", inRecord);
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch


		szMsg="후판제품출하지시대기 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


	} // end of rcvPlateGdsDistOrdWait()





	/**
	 * 오퍼레이션명 : 후판제품운송지시대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:  
	 * @throws JDTOException
	 */ 
	public void rcvPlateGdsTrnOrdWait(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1322 후판제품운송지시대기등록
		// TC : DMYDR018
		// 출하관리시스템으로부터 후판제품운송지시대기실적 수신
		//
		//┏━┓
		//┃출하관리에서 후판제품 운송지시대기등록실적을 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvPlateGdsTrnOrdWait"; 
		String szOperationName = "후판제품운송지시대기";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}
		ydUtils.displayRecord(szOperationName, inRecord);
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				
				if(sCancelChk.equals("Y")){
					// AB열연 (취소)
					ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
				}else {
					// AB열연 (지시)
					ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
				}
			}else{
				if(sCancelChk.equals("Y")){
					// (취소)
					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
				}else {
					// 후판제품운송지시대기 수신
					ydEjbCon.trx("RtModRegSeEJB", "procPlateGdsTrnOrdWait", inRecord);
				}
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch


		szMsg="후판제품운송지시대기 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



	} // end of rcvPlateGdsTrnOrdWait()





	/**
	 * 오퍼레이션명 : 후판제품운송상차지시
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvPlateGdsTrnOrd(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1323 후판제품운송지시등록(상차지시와 통합)
		// TC : DMYDR021
		// 출하관리시스템으로부터 후판제품운송지시실적 수신
		//
		//┏━┓
		//┃출하관리에서 후판제품 운송지시를 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg					= "";
		String szMethodName				= "rcvPlateGdsTrnOrd"; 
		String szOperationName			= "후판제품운송상차지시(DMYDR021)";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}
		
		szMsg = "["+szOperationName+"] 메소드 시작";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		ydUtils.displayRecord(szOperationName, inRecord);


		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				
				if(sCancelChk.equals("Y")){
					// AB열연 (취소)
					ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
				}else {
					// AB열연 (지시)
					ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
				}
			}else{
				if(sCancelChk.equals("Y")){
					// (취소)
					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
				}else {
					
					ydEjbCon.trx("RtModRegSeEJB", "procPlateGdsTrnOrd3G", inRecord);
				}
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch


		szMsg = "["+szOperationName+"]  완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



	} // end of rcvPlateGdsTrnOrd()

	/**
	 * 오퍼레이션명 : 후판제품선별LOT편성정보
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvPlateGdsTrnOrdLot(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1323 후판제품선별LOT편성정보
		// TC : DMYDR046
		// 출하관리시스템으로부터 후판제품선별LOT편성정보 수신
		
		String szMsg					= "";
		String szMethodName				= "rcvPlateGdsTrnOrdLot"; 
		String szOperationName			= "후판제품선별LOT편성정보(DMYDR046)";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
		szMsg = "["+szOperationName+"] 메소드 시작";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		ydUtils.displayRecord(szOperationName, inRecord);

		try {
				// 후판제품선별LOT편성정보 수신
				ydEjbCon.trx("RtModRegSeEJB", "procPlateGdsTrnOrdLot", inRecord);
			
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg = "["+szOperationName+"]  완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of procPlateGdsTrnOrdLot()
 
	/**
	 * 오퍼레이션명 : 후판제품해송선별LOT편성정보 (DMYDR048)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvPlateGdsShptrTrnOrdLot(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1323 후판제품선별LOT편성정보
		// TC : DMYDR048
		// 출하관리시스템으로부터 후판제품선별LOT편성정보 수신
		
		String szMsg					= "";
		String szMethodName				= "rcvPlateGdsShptrTrnOrdLot"; 
		String szOperationName			= "후판제품해송선별LOT편성정보(DMYDR048)";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
		szMsg = "["+szOperationName+"] 메소드 시작";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		ydUtils.displayRecord(szOperationName, inRecord);

		try {
				// 후판제품해송선별LOT편성정보 수신
				ydEjbCon.trx("RtModRegSeEJB", "procPlateGdsShptrTrnOrdLot", inRecord);
				
				String szCARLD_PNT_CD = StringHelper.evl(inRecord.getFieldString("CARLD_PNT_CD"), "");
				
				if(!"".equals(szCARLD_PNT_CD)) {
					YdPlateCommDAO	commDao 		= new YdPlateCommDAO();					
					commDao.update(inRecord, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0050");
				}
				
			
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg = "["+szOperationName+"]  완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvPlateGdsShptrTrnOrdLot()	

	/**
	 * 오퍼레이션명 : 후판제품해송적하그룹편성정보 (DMYDR049)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvPlateGdsShpudGrpGpInfo(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1323 후판제품선별LOT편성정보
		// TC : DMYDR048
		// 출하관리시스템으로부터 후판제품선별LOT편성정보 수신
		
		String szMsg					= "";
		String szMethodName				= "rcvPlateGdsShpudGrpGpInfo"; 
		String szOperationName			= "후판제품해송적하그룹편성정보(DMYDR049)";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
		szMsg = "["+szOperationName+"] 메소드 시작";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		ydUtils.displayRecord(szOperationName, inRecord);

		try {
				// 후판제품해송적하그룹편성정보 수신
				ydEjbCon.trx("RtModRegSeEJB", "procPlateGdsShpudGrpGpInfo", inRecord);
			
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg = "["+szOperationName+"]  완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvPlateGdsShpudGrpGpInfo()	
	
	/**
	 * 오퍼레이션명 : 외판슬라브운송상차지시
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvSlabGdsTrnOrd(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1323 외판슬라브운송상차지시(상차지시와 통합)
		// TC : DMYDR022
		// 출하관리시스템으로부터 외판슬라브운송상차실적 수신
		//
		//┏━┓
		//┃출하관리에서 외판슬라브 운송지시를 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvSlabGdsTrnOrd"; 
		String szOperationName = "외판슬라브운송상차지시";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}
		ydUtils.displayRecord(szOperationName, inRecord);


		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
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
				
				if(sCancelChk.equals("Y")){
					// AB열연 (취소)
					if((sYdGp.equals("2")&&sBSLAB_EFF_YN.equals("Y")) || (sYdGp.equals("3")&&sBCOIL_EFF_YN.equals("Y"))	) {
						//B열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("BCoilL3RcvSeEJB", "receiveCancel", inRecord);
					} else {
						//기존모듈 호출
						ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
					}
				}else {
					// AB열연 (지시)
					if((sYdGp.equals("2")&&sBSLAB_EFF_YN.equals("Y")) || (sYdGp.equals("3")&&sBCOIL_EFF_YN.equals("Y"))	) {
						//B열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
					} else  {
						//기존모듈 호출
						ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
					}
				}
			}else{
				if(sCancelChk.equals("Y")){
					// (취소)
					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
				}else {
					// 외판슬라브제품운송상차지시등록 수신
					ydEjbCon.trx("RtModRegSeEJB", "procSlabGdsTrnOrd", inRecord);
				}
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch


		szMsg="외판슬라브운송상차지시 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



	} // end of rcvPlateGdsTrnOrd()

	/**
	 * 오퍼레이션명 : 슬라브이송지시
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:  
	 * @throws JDTOException
	 */ 
	public void rcvSlavFtmvOrd(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1324 슬라브이송지시
		// TC : PMYDJ002
		// 공정계획시스템으로부터 슬라브이송지시 수신
		//
		//┏━┓
		//┃공정계획에서 슬라브이송지시를 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvSlavFtmvOrd"; 

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}
		try {

			// 슬라브이송지시 수신
			ydEjbCon.trx("RtModRegSeEJB", "procSlavFtmvOrd", inRecord);
			/*
			 * AB열연 시스템으로 인터페이스 전달  
			 */
			//ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch


		szMsg="슬라브이송지시 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



	} // end of rcvSlavFtmvOrd()





	/**
	 * 오퍼레이션명 : 코일소재이송지시
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:  
	 * @throws JDTOException
	 */ 
	public void rcvCoilMatlFtmvOrd(JDTORecord inRecord) throws JDTOException
	{
		// 신규
		// YD-UC-1325 코일소재이송지시
		// TC : PTYDJ002
		// 진행관리시스템으로부터 코일소재이송지시 수신
		//
		//┏━┓
		//┃진행관리에서 코일소재이송지시를 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvCoilMatlFtmvOrd"; 

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName))
		{
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}

		try 
		{
			/**********************************
        	 * 2열연코일야드 신규모듈 적용여부 
        	 **********************************/
        	YdPlateCommDAO ydCommDao = new YdPlateCommDAO();
        	JDTORecord jrAppYn = ydCommDao.get2HrAppYn();
        	
        	String s2HrAppYn = StringHelper.evl(jrAppYn.getFieldString("CCOIL_EFF_YN"), "N");; //2열연 코일야드 적용여부
    		
    		ydUtils.putLog(szSessionName, szMethodName,"2열연코일야드 신규모듈 적용여부 : " + s2HrAppYn, YdConstant.DEBUG);

			if ("Y".equals(s2HrAppYn)) {
				ydEjbCon.trx("CCommSeEJB", "rcvInterface", inRecord); //2열연 신규모듈
			} else {
				// 코일소재이송지시 수신
				ydEjbCon.trx("RtModRegSeEJB", "procCoilMatlFtmvOrd", inRecord);
			}
			
			/*
			 * AB열연 시스템으로 인터페이스 전달  
			 */
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
			
			//박판열연 신규모듈 적용여부 조회
            YdPlateCommDAO commDaoYf	= new YdPlateCommDAO();
            JDTORecord jrResultYf		= commDaoYf.getYfNewModuleEffYn();

            String sACOIL_EFF_YN	= StringHelper.evl(jrResultYf.getFieldString("ACOIL_EFF_YN"),	"N");

            szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
            ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);

			if("Y".equals(sBCOIL_EFF_YN))
			{
				//B열연 COIL야드 신규모듈 적용
				ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
			}
			else
			{
				//기존모듈 호출
				ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
			}
			
			if( "Y".equals(sACOIL_EFF_YN) )
			{
				//박판열연 COIL야드 신규모듈 적용
				ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
			}
		}
		catch (Exception e) 
		{			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="코일소재이송지시 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvCoilMatlFtmvOrd()


	/**
	 * 오퍼레이션명 : 코일소재임가공이송지시
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:  
	 * @throws JDTOException
	 */ 
	public void rcvCoilMatlRentprocFtmvOrd(JDTORecord inRecord) throws JDTOException
	{
		// 신규
		// YD-UC-1326 코일소재임가공이송지시
		// TC : PTYDJ003
		// 진행관리시스템으로부터 코일소재임가공이송지시 수신
		//
		//┏━┓
		//┃진행관리에서 코일임가공이송지시를 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvCoilMatlRentprocFtmvOrd"; 

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName))
		{
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}

		try
		{
			/**********************************
        	 * 2열연코일야드 신규모듈 적용여부 
        	 **********************************/
        	YdPlateCommDAO ydCommDao = new YdPlateCommDAO();
        	JDTORecord jrAppYn = ydCommDao.get2HrAppYn();
        	
        	String s2HrAppYn = StringHelper.evl(jrAppYn.getFieldString("CCOIL_EFF_YN"), "N");; //2열연 코일야드 적용여부
    		
    		ydUtils.putLog(szSessionName, szMethodName,"2열연코일야드 신규모듈 적용여부 : " + s2HrAppYn, YdConstant.DEBUG);

    		if ("Y".equals(s2HrAppYn)) {
    			ydEjbCon.trx("CCommSeEJB", "rcvInterface", inRecord); //2열연 신규모듈
    		} else {
    			// 코일소재임가공이송지시 수신
    			ydEjbCon.trx("RtModRegSeEJB", "procCoilMatlRentprocFtmvOrd", inRecord);
    		}
			
			/*
			 * AB열연 시스템으로 인터페이스 전달  
			 */
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
			
			//박판열연 신규모듈 적용여부 조회
            YdPlateCommDAO commDaoYf	= new YdPlateCommDAO();
            JDTORecord jrResultYf		= commDaoYf.getYfNewModuleEffYn();

            String sACOIL_EFF_YN	= StringHelper.evl(jrResultYf.getFieldString("ACOIL_EFF_YN"),	"N");

            szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
            ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);

			if("Y".equals(sBCOIL_EFF_YN))
			{
				//B열연 COIL야드 신규모듈 적용
				ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
			}
			else
			{
				//기존모듈 호출
				ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
			}
			
			if( "Y".equals(sACOIL_EFF_YN) )
			{
				//박판열연 COIL야드 신규모듈 적용
				ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
			}
		}
		catch (Exception e)
		{			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);
			
		} // end of try catch

		szMsg="코일임가공이송지시 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCoilMatlRentprocFtmvOrd()

	
	
	
	
	
	/**
	 * 오퍼레이션명 : OS주문투입실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:  
	 * @throws JDTOException
	 */ 
	public void rcvOrdInputHis(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1326 OS주문투입실적
		// TC : PTYDJ004
		// 진행관리시스템으로부터 OS주문투입실적 수신
		//
		//┏━┓
		//┃진행관리에서 OS주문투입실적 수신하여 Log Table에 등록
		//┗━┛

		String szMsg="";
		String szMethodName="rcvOrdInputHis"; 


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.10 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= inRecord.getResultCode();				// 전문으로 부터 logid get
String logId                    		= ydUtils.getJDTOLogId(inRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 					// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "OS주문투입실적 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.10 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

			return;
		}

		try {

			// OS주문투입실적 수신
			ydEjbCon.trx("RtModRegSeEJB", "procOrdInputHis", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="OS주문투입실적 처리("+szMethodName+") 완료";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

	} // end of OrdInputHis()
	
	
	/**
	 * 오퍼레이션명 : OS주문변경정보
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:  
	 * @throws JDTOException
	 */ 
	public void rcvOrdInputChg(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1326 OS주문변경정보
		// TC : PTYDJ005
		// 진행관리시스템으로부터 OS주문투입실적 수신
		//
		//┏━┓
		//┃진행관리에서 OS주문투입실적 수신하여 Log Table에 등록
		//┗━┛

		String szMsg="";
		String szMethodName="rcvOrdInputChg"; 

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}

		try {

			// OS주문변경정보 수신
			ydEjbCon.trx("RtModRegSeEJB", "procOrdInputChg", inRecord);
			
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="OS주문변경정보 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



	} // end of rcvOrdInputChg()
	
	




	/**
	 * 오퍼레이션명 : 코일제품고간이송지시
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvCoilGdsWhFtmvOrd(JDTORecord inRecord) throws JDTOException
	{
		// 신규
		// YD-UC-1327 코일제품고간이송지시등록
		// TC : DMYDR011
		// 출하관리시스템으로부터 코일제품고간이송지시 수신
		//
		//┏━┓
		//┃출하관리에서 코일제품고간이송지시를 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvCoilGdsWhFtmvOrd"; 
		String szOperationName = "코일제품고간이송지시";

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
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
			
			if( "0".equals(sYdGp) || "1".equals(sYdGp) || "2".equals(sYdGp) || "3".equals(sYdGp))
			{
                //AB열연 수신
				//박판열연 신규모듈 적용여부 조회
                YdPlateCommDAO commDao	= new YdPlateCommDAO();
                JDTORecord jrResult		= commDao.getYfNewModuleEffYn();

                String sACOIL_EFF_YN	= StringHelper.evl(jrResult.getFieldString("ACOIL_EFF_YN"),	"N");

                szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
				
				if(sCancelChk.equals("Y"))
				{
					// AB열연 (취소)
					if( "2".equals(sYdGp) || "3".equals(sYdGp) )
					{
						//B열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("BCoilL3RcvSeEJB", "receiveCancel", inRecord);
					}
					else if( "1".equals(sYdGp) && "Y".equals(sACOIL_EFF_YN) )
					{
						//박판열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
					}
					else
					{
						//기존모듈 호출
						ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
					}
				}
				else
				{
					// AB열연 (지시)
					if( "2".equals(sYdGp) || "3".equals(sYdGp) )
					{
						//B열연 COIL야드 신규모듈 적용
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
        			/*기존모듈****************************************************/
        			if("Y".equals(sCancelChk)) {
    					// (취소)
    					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
    				} else {
    					// 코일제품고간이송지시 수신
    					ydEjbCon.trx("RtModRegSeEJB", "procCoilGdsWhFtmvOrd", inRecord);
    				}
        			/* ************************************************************/
                }
        		
				
			}
		}
		catch (Exception e)
		{			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg="코일제품고간이송지시 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvCoilGdsWhFtmvOrd()





	/**
	 * 오퍼레이션명 : 후판제품고간이송지시
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvPlGdsWhFtmvOrd(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-1328 후판제품고간이송지시등록
		// TC : DMYDR012
		// 출하관리시스템으로부터 후판제품고간이송지시 수신
		//
		//┏━┓
		//┃출하관리에서 후판제품고간이송지시를 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvPlGdsWhFtmvOrd"; 
		String szOperationName = "후판제품고간이송지시";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}
		ydUtils.displayRecord(szOperationName, inRecord);

		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				
				if(sCancelChk.equals("Y")){
					// AB열연 (취소)
					ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
				}else {
					// AB열연 (지시)
					ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
				}
			}else{
				if(sCancelChk.equals("Y")){
					// (취소)
					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
				}else {
					// 후판제품고간이송지시 수신
					ydEjbCon.trx("RtModRegSeEJB", "procPlGdsWhFtmvOrd", inRecord);
				}
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="후판제품고간이송지시 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



	} // end of rcvPlGdsWhFtmvOrd()





	/**
	 * 오퍼레이션명 : 외판슬라브보관지시
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvOutplSlabKeepOrd(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-???? 외판슬라브보관지시
		// TC : DMYDR026
		// 출하관리시스템으로부터 외판슬라브보관지시 수신
		//
		//┏━┓
		//┃출하관리에서 외판슬라브보관지시를 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvOutplSlabKeepOrd"; 
		String szOperationName = "외판슬라브보관지시";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}
		ydUtils.displayRecord(szOperationName, inRecord);
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
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
				
				if(sCancelChk.equals("Y")){
					// AB열연 (취소)
					if((sYdGp.equals("2")&&sBSLAB_EFF_YN.equals("Y")) || (sYdGp.equals("3")&&sBCOIL_EFF_YN.equals("Y"))	) {
						//B열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("BCoilL3RcvSeEJB", "receiveCancel", inRecord);
					} else {
						//기존모듈 호출
						ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
					}
				}else {
					// AB열연 (지시)
					if((sYdGp.equals("2")&&sBSLAB_EFF_YN.equals("Y")) || (sYdGp.equals("3")&&sBCOIL_EFF_YN.equals("Y"))	) {
						//B열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
					} else  {
						//기존모듈 호출
						ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
					}
				}
			}else{
				if(sCancelChk.equals("Y")){
					// (취소)
					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
				}else {
					// 외판슬라브보관지시 수신
					ydEjbCon.trx("RtModRegSeEJB", "procOutplSlabKeepOrd", inRecord);
				}
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch


		szMsg="외판슬라브보관지시 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



	} // end of rcvOutplSlabKeepOrd()






	/**
	 * 오퍼레이션명 : 코일제품보관지시
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvCoilGdsKeepOrd(JDTORecord inRecord) throws JDTOException
	{
		// 신규
		// YD-UC-???? 코일제품보관지시
		// TC : DMYDR027
		// 출하관리시스템으로부터 코일제품보관지시 수신
		//
		//┏━┓
		//┃출하관리에서 코일제품보관지시 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvCoilGdsKeepOrd"; 
		String szOperationName = "코일제품보관지시";

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
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
			
			if( "0".equals(sYdGp) || "1".equals(sYdGp) || "2".equals(sYdGp) || "3".equals(sYdGp) ) 
			{
				//AB열연 수신
				//박판열연 신규모듈 적용여부 조회
                YdPlateCommDAO commDao	= new YdPlateCommDAO();
                JDTORecord jrResult		= commDao.getYfNewModuleEffYn();

                String sACOIL_EFF_YN	= StringHelper.evl(jrResult.getFieldString("ACOIL_EFF_YN"),	"N");

                szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
				
				if(sCancelChk.equals("Y"))
				{
					// AB열연 (취소)
					if( "2".equals(sYdGp) || "3".equals(sYdGp) ) 
					{
						//B열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("BCoilL3RcvSeEJB", "receiveCancel", inRecord);
					}
					else if( "1".equals(sYdGp) && "Y".equals(sACOIL_EFF_YN))
					{
						//박판열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
					}
					else
					{
						//기존모듈 호출
						ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
					}
				}
				else
				{
					// AB열연 (지시)
					if( "2".equals(sYdGp) || "3".equals(sYdGp) ) 
					{
						//B열연 COIL야드 신규모듈 적용
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
        			if("Y".equals(sCancelChk)) {
    					// (취소)
    					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
    				} else {
    					// 코일제품보관지시 수신
    					ydEjbCon.trx("RtModRegSeEJB", "procCoilGdsKeepOrd", inRecord);
    				}
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

		szMsg="코일제품보관지시 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCoilGdsKeepOrd()






	/**
	 * 오퍼레이션명 : 후판제품보관지시
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvPlGdsKeepOrd(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-???? 후판제품보관지시
		// TC : DMYDR028
		// 출하관리시스템으로부터 후판제품보관지시 수신
		//
		//┏━┓
		//┃출하관리에서 후판제품보관지시 수신하여 저장품 Table에 야드산적Lot 항목을 수정
		//┗━┛

		String szMsg="";
		String szMethodName="rcvPlGdsKeepOrd"; 
		String szOperationName = "후판제품보관지시";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}
		ydUtils.displayRecord(szOperationName, inRecord);
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				
				if(sCancelChk.equals("Y")){
					// AB열연 (취소)
					ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
				}else {
					// AB열연 (지시)
					ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
				}
			}else{
				if(sCancelChk.equals("Y")){
					// (취소)
					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
				}else {
					// 후판제품보관지시 수신
					ydEjbCon.trx("RtModRegSeEJB", "procPlGdsKeepOrd", inRecord);
				}
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch


		szMsg="후판제품보관지시 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



	} // end of rcvPlGdsKeepOrd()

	
	/**
	 * 오퍼레이션명 : 임가공이송상차지시
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvSlabGdsWhFtmvOrd(JDTORecord inRecord) throws JDTOException  {
		// 신규
		// YD-UC-???? 임가공이송상차지시
		// TC : DMYDR025

		String szMsg = "";
		String szMethodName = "rcvSlabGdsWhFtmvOrd"; 
		String szOperationName = "임가공이송상차지시";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg = szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}
		
		ydUtils.displayRecord(szOperationName, inRecord);
		
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				if(sCancelChk.equals("Y")){
					ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
				}else {
					ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
				}
			}else{
				if(sCancelChk.equals("Y")){
					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
				}else {
					ydEjbCon.trx("CarLdLotRegSeEJB", "procCoilRentGdsCarLdOrd", inRecord);
				}
			}
		} catch (Exception e) {			
			szMsg = szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "임가공이송상차지시 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvSlabGdsWhFtmvOrd()	

	
	
	

	/**
	 * 오퍼레이션명 : 슬라브진행변경 (PMYDJ003) - 2010.01.25 권오창
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvSlabProgSync(JDTORecord inRecord) throws JDTOException  {
		//
		// YD-UC-1308 슬라브진행변경
		// TC : PMYDJ003
		// 공정계획시스템으로부터 슬라브진행변경 수신
		//

		String szMethodName = "rcvSlabProgSync"; 
		String szMsg        = "";

		
		
		
		
		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		
		
		
		
		try {

			// 슬라브충당실적 수신
			ydEjbCon.trx("RtModRegSeEJB", "procSlabProgSync", inRecord);
			
			/*
			 * AB열연 시스템으로 인터페이스 전달  
			 */
			ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);

		} catch (Exception e) {			
			szMsg = szMethodName + "() " + e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}

		szMsg = "슬라브진행변경 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} 

	
	/**
	 * 오퍼레이션명 : 후판제품출하 변경정보수신 (DMYDR047)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvStlFrtMoveCancel(JDTORecord inRecord) throws JDTOException  {
		//
		// YD-UC-1309 후판제품출하 변경정보수신
		// TC : DMYDR047
		// 출하시스템으로부터 후판제품출하 변경정보수신 수신
		//

		String szMethodName = "rcvStlFrtMoveCancel"; 
		String szMsg        = "";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {

			// 후판제품출하 변경정보수신 수신
			ydEjbCon.trx("RtModRegSeEJB", "procStlFrtMoveCancel", inRecord);

		} catch (Exception e) {			
			szMsg = szMethodName + "() " + e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}

		szMsg = "후판제품출하 변경정보수신 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} 
	
	
	
	/**
	 * 오퍼레이션명 : 대기장도착실적(DMYDR061)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvStandByYdArrive(JDTORecord inRecord) throws JDTOException
	{
		// 신규
		// YD-UC-1321 대기장도착실적
		// TC : DMYDR061
		// 출하관리시스템으로부터 대기장도착실적 수신

		String szMsg="";
		String szMethodName="rcvStandByYdArrive"; 
		String szOperationName = "대기장도착실적";

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
			
			if("T".equals(sYdGp) )
			{
				//2020.1.6 신규모듈관련 분기 추가
				//후판제품 신규모듈 적용여부 조회
                if(PlateGdsYdUtil.isPlateNewMoudleApply("DM")){
    				// 후판 수신
                	ydEjbCon.trx("PlateYdRcvFaEJB", "rcvInterface", inRecord);
                }else{
    				// 후판 수신
    				ydEjbCon.trx("RtModRegSeEJB", "procStandByYdArrivePlate", inRecord);
                }
			}
			else
			{	
				if( "0".equals(sYdGp) || "1".equals(sYdGp) || "2".equals(sYdGp) || "3".equals(sYdGp) )
				{	
					//AB열연 수신
					//박판열연 신규모듈 적용여부 조회
	                YdPlateCommDAO commDao	= new YdPlateCommDAO();
	                JDTORecord jrResult		= commDao.getYfNewModuleEffYn();

	                String sACOIL_EFF_YN	= StringHelper.evl(jrResult.getFieldString("ACOIL_EFF_YN"),	"N");

	                szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
	                ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
					
					if( "2".equals(sYdGp) || "3".equals(sYdGp) )
					{
						//B열연 COIL야드 신규모듈 적용
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
						ydEjbCon.trx("RtModRegSeEJB", "procStandByYdArrive", inRecord);
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
	        			// 열연 수신
						ydEjbCon.trx("RtModRegSeEJB", "procStandByYdArrive", inRecord);
	                    /* ******************************************************/
	                }
				}
			}
		}
		catch (Exception e)
		{			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg=szOperationName+" 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvStandByYdArrive()
	
	
	/**
	 * 오퍼레이션명 : 코일이송상차대기장도착PDA(DMYDR070)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:  
	 * @throws JDTOException
	 */ 
	public void rcvCoilGdsTrnOrdLdPDA(JDTORecord inRecord) throws JDTOException
	{
		String szMsg="";
		String szMethodName="rcvCoilGdsTrnOrdLdPDA"; 
		String szOperationName = "코일이송상차대기장도착PDA";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName))
		{
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			return;
		}
		
		ydUtils.displayRecord(szOperationName, inRecord);

		try
		{
			String sTcCd = StringHelper.evl(inRecord.getFieldString("TC_CODE"), "");	
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
			 
    		//-------------------------------------------------------------------------------
    		//기존 방식 적용 
			if( "0".equals(sYdGp) || "1".equals(sYdGp) || "2".equals(sYdGp) || "3".equals(sYdGp) )
			{	
				//AB열연 수신
				//박판열연 신규모듈 적용여부 조회
                YdPlateCommDAO commDao	= new YdPlateCommDAO();
                JDTORecord jrResult		= commDao.getYfNewModuleEffYn();

                String sACOIL_EFF_YN	= StringHelper.evl(jrResult.getFieldString("ACOIL_EFF_YN"),	"N");

                szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
				
				if(sCancelChk.equals("Y"))
				{
					// AB열연 (취소)
					if( "2".equals(sYdGp) || "3".equals(sYdGp) )
					{
						//B열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("BCoilL3RcvSeEJB", "receiveCancel", inRecord);
					}
					else if( "1".equals(sYdGp) && "Y".equals(sACOIL_EFF_YN) )
					{
						//박판열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
					}
					else
					{
						//기존모듈 호출
						ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
					}
				}
				else
				{
					// AB열연 (지시)
					if( "2".equals(sYdGp) || "3".equals(sYdGp) )
					{
						//B열연 COIL야드 신규모듈 적용
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
        			if ("Y".equals(sCancelChk)) {
    					// (취소)
    					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
    				} else{
    					// 코일제품운송지시 수신
    					ydEjbCon.trx("RtModRegSeEJB", "procCoilGdsTrnOrdLdPDA", inRecord);
    				}
                    /* ******************************************************/
                }
			}
			//-------------------------------------------------------------------------------
		}
		catch (Exception e)
		{			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg="코일이송상차대기장도착PDA 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvCoilGdsTrnOrdLdPDA()
	
	
	
	/**
	 * 오퍼레이션명 : 코일이송하차대기장도착PDA(DMYDR073)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:  
	 * @throws JDTOException
	 */ 
	public void rcvCoilGdsTrnOrdUdPDA(JDTORecord inRecord) throws JDTOException
	{
		String szMsg="";
		String szMethodName="rcvCoilGdsTrnOrdUdPDA"; 
		String szOperationName = "코일이송하차대기장도착PDA";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName))
		{
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		ydUtils.displayRecord(szOperationName, inRecord);

		try
		{
			String sTcCd = StringHelper.evl(inRecord.getFieldString("TC_CODE"), "");	
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
			String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시
			 
    		//-------------------------------------------------------------------------------
    		//기존 방식 적용 
			if( "0".equals(sYdGp) || "1".equals(sYdGp) || "2".equals(sYdGp) || "3".equals(sYdGp) )
			{
				//AB열연 수신
				//박판열연 신규모듈 적용여부 조회
                YdPlateCommDAO commDao	= new YdPlateCommDAO();
                JDTORecord jrResult		= commDao.getYfNewModuleEffYn();

                String sACOIL_EFF_YN	= StringHelper.evl(jrResult.getFieldString("ACOIL_EFF_YN"),	"N");

                szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
				
				if(sCancelChk.equals("Y"))
				{
					// AB열연 (취소)
					if( "2".equals(sYdGp) || "3".equals(sYdGp) )
					{
						//B열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("BCoilL3RcvSeEJB", "receiveCancel", inRecord);
					}
					else if( "1".equals(sYdGp) && "Y".equals(sACOIL_EFF_YN) )
					{
						//박판열연 COIL야드 신규모듈 적용
						ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
					}
					else
					{
						//기존모듈 호출
						ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
					}
				}
				else
				{
					// AB열연 (지시)
					if( "2".equals(sYdGp) || "3".equals(sYdGp) )
					{
						//B열연 COIL야드 신규모듈 적용
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
        			if ("Y".equals(sCancelChk)) {
    					// (취소)
    					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
    				} else {
    					// 코일제품운송지시 수신
    					ydEjbCon.trx("RtModRegSeEJB", "procCoilGdsTrnOrdUdPDA", inRecord);
    				}
                    /* ******************************************************/
                }
				
			}
			//------------------------------------------------------------------------------- 
		}
		catch (Exception e)
		{			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg="코일이송하차대기장도착PDA 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCoilGdsTrnOrdUdPDA()
	
	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//                                                
	//                     일관제철소정보관리시스템-야드관리
	//              저장품관리-행선변경등록 Facade Session Bean
	//                          2008.09.30 YHWHman
	//                                                      
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

	public static void main(String[] args){
		RtModRegFaEJBBean im =new RtModRegFaEJBBean();
		JDTORecord testRec =JDTORecordFactory.getInstance().create();

		try {
			testRec.setField("JMS_TC_CD","DMYDR004");
			im.rcvOutplSlabDistOrdWait(testRec);
		} catch (JDTOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	} // end of testMain()


//	-----------------------------------------------------------------------------	
} // end of class

