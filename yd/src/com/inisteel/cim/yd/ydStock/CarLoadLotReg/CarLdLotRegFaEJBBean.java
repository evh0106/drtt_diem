package com.inisteel.cim.yd.ydStock.CarLoadLotReg;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTOException;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;


/**
 * 상차LOT등록 Facade Session EJB
 *
 * @ejb.bean name="CarLdLotRegFaEJB" jndi-name="CarLdLotRegFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
 public class CarLdLotRegFaEJBBean extends BaseSessionBean  {
		
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
		 * 오퍼레이션명 : 코일제품상차지시등록
		 * 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inRecord
		 * @return: 
		 * @throws JDTOException
		 */ 
		public void rcvCoilGdsCarLdOrd(JDTORecord inRecord) throws JDTOException  {
			// 
			// YD-UC-1402 코일제품상차지시등록
			// TC : DMYDR023
			// 출하관리시스템으로부터 코일제품상차지시실적 수신
			//
			//┏━┓
			//┃출하관리에서 코일제품 출하차량이 대기장에 도착한 실적을 수신하여 
			//┃출하에서 편성한 상차 Lot를 저장품에 Match하는 기능 
			//┗━┛
			
			String szMsg="";
			String szMethodName="rcvCoilGdsCarLdOrd";
			String szOperationName = "코일제품상차지시등록";

			if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
				
				szMsg=szMethodName+"() 실행 실패";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				return;
				
			}
			
			ydUtils.displayRecord(szOperationName, inRecord);
			
			
			try {
				String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");	
				String sCancelChk = StringHelper.evl(inRecord.getFieldString("CANCEL_YN"), ""); //Y: 취소 , N: 지시 ,S: skip
				if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
					
					if(sCancelChk.equals("Y")){
						// AB열연 (취소)
						ydEjbCon.trx("JNDIInternal", "receiveCancel", inRecord);
					}else if(sCancelChk.equals("N")){
						// AB열연 (지시)
						ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
					}
				}else{
					if(sCancelChk.equals("Y")){
						// (취소)
						ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);					
					}else if(sCancelChk.equals("N")){
						// 코일제품상차지시실적 수신
						ydEjbCon.trx("CarLdLotRegSeEJB", "procCoilGdsCarLdOrd", inRecord);
					}
				}
			} catch (Exception e) {			
				szMsg =szMethodName + "() " +e.getMessage(); 
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				throw new JDTOException(szMsg);

			} // end of try catch
			szMsg="코일제품상차지시등록 처리("+szMethodName+") 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} // end of rcvCoilGdsCarLdOrd()
		
		

		
		/**
		 * 오퍼레이션명 : 후판제품상차지시등록
		 * 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inRecord
		 * @return: 
		 * @throws JDTOException
		 */ 
		public void rcvPlGdsCarLdOrd(JDTORecord inRecord) throws JDTOException  {
			// 
			// YD-UC-1403 후판제품상차지시등록
			// TC : DMYDR024
			// 생산통제시스템으로부터 후판제품상차지시실적 수신
			//
			//┏━┓
			//┃ 출하관리에서 후판제품 출하차량이 대기장에 도착한 실적을 수신하여 
			//┃ 출하에서 편성한 상차 Lot를 저장품에 Match하는 기능 
			//┗━┛
			
			String szMsg="";
			String szMethodName="rcvPlGdsCarLdOrd";
			String szOperationName = "후판제품상차지시등록";

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
						// 후판제품상차지시실적 수신
						ydEjbCon.trx("CarLdLotRegSeEJB", "procPlGdsCarLdOrd", inRecord);
					}
				}
			} catch (Exception e) {			
				szMsg =szMethodName + "() " +e.getMessage(); 
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				throw new JDTOException(szMsg);

			} // end of try catch
			szMsg="후판제품상차지시등록 처리("+szMethodName+") 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} // end of rcvPlGdsCarLdOrd()
		

		
		/**
		 * 오퍼레이션명 : 코일이송상차도착PDA(DMYDR071)
		 * 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inRecord
		 * @return: 
		 * @throws JDTOException
		 */ 
		public void rcvStandByYdArriveLdPDA(JDTORecord inRecord) throws JDTOException
		{
			// 
			// YD-UC-1402 코일제품상차지시등록
			// TC : DMYDR023
			// 출하관리시스템으로부터 코일제품상차지시실적 수신
			//
			//┏━┓
			//┃출하관리에서 코일제품 출하차량이 대기장에 도착한 실적을 수신하여 
			//┃출하에서 편성한 상차 Lot를 저장품에 Match하는 기능 
			//┗━┛
			
			String szMsg="";
			String szMethodName="rcvStandByYdArriveLdPDA";
			String szOperationName = "코일이송상차도착PDA";

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
	        			// 코일제품상차지시실적 수신
						ydEjbCon.trx("CarLdLotRegSeEJB", "procStandByYdArriveLdPDA", inRecord);
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
			
			szMsg="코일제품상차지시등록 처리("+szMethodName+") 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} // end of rcvStandByYdArriveLdPDA()
		
		
		/**
		 * 오퍼레이션명 : 코일이송하차도착PDA(DMYDR074)
		 * 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inRecord
		 * @return: 
		 * @throws JDTOException
		 */ 
		public void rcvStandByYdArriveUdPDA(JDTORecord inRecord) throws JDTOException
		{
			// 
			// YD-UC-1402 코일제품상차지시등록
			// TC : DMYDR023
			// 출하관리시스템으로부터 코일제품상차지시실적 수신
			//
			//┏━┓
			//┃출하관리에서 코일제품 출하차량이 대기장에 도착한 실적을 수신하여 
			//┃출하에서 편성한 상차 Lot를 저장품에 Match하는 기능 
			//┗━┛
			
			String szMsg="";
			String szMethodName="rcvStandByYdArriveUdPDA";
			String szOperationName = "코일이송하차도착PDA";

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
	        			// 코일제품상차지시실적 수신
						ydEjbCon.trx("CarLdLotRegSeEJB", "procStandByYdArriveUdPDA", inRecord);
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
			
			szMsg="코일이송하차도착PDA 처리("+szMethodName+") 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} // end of rcvStandByYdArriveUdPDA()
		
		

		/**
		 * 오퍼레이션명 : Y5 차량작업 예정정보 요구 Y5YDL016
		 * 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inRecord
		 * @return: 
		 * @throws JDTOException
		 */ 
		public void rcvY5DrvCarPlan(JDTORecord inRecord) throws JDTOException  {
			// 
			// YD-UC-????
			// TC : Y5YDL016
			// Y5 차량작업 예정정보 요구 수신
			//
			String szMsg        = "";
			String szMethodName = "rcvY5DrvCarPlan";

			if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
				szMsg=szMethodName+"() 실행 실패";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;	
			}
			
			try {
				ydEjbCon.trx("CarLdLotRegSeEJB", "rcvY5DrvCarPlan", inRecord);
			} catch (Exception e) {			
				szMsg = szMethodName + "() " + e.getMessage(); 
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new JDTOException(szMsg);
			} // end of try catch
			
			szMsg="Y5 차량작업 예정정보 요구("+szMethodName+") 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} // end of rcvY5EqpDrvCarPlan()
		


		/**
		 * 오퍼레이션명 : Y5 TC : Y5YDL017 상차도 작업불가 수신
		 * 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inRecord
		 * @return: 
		 * @throws JDTOException
		 */ 
		public void rcvY5CarNotWrk(JDTORecord inRecord) throws JDTOException  {
			// 
			// YD-UC-????
			// TC : Y5YDL017
			// Y5 상차도 작업불가 수신
			//
			String szMsg        = "";
			String szMethodName = "rcvY5CarNotWrk";

			if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
				szMsg=szMethodName+"() 실행 실패";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;	
			}
			
			try {
				ydEjbCon.trx("CarLdLotRegSeEJB", "rcvY5CarNotWrk", inRecord);
			} catch (Exception e) {			
				szMsg = szMethodName + "() " + e.getMessage(); 
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new JDTOException(szMsg);
			} // end of try catch
			
			szMsg="Y5 상차도 작업불가 수신("+szMethodName+") 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} // end of  rcvY5CarNotWrk()

		
		/**
		 * 오퍼레이션명 : Y5 TC : Y5YDL018 차량동간이적(도착) 수신
		 * 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inRecord
		 * @return: 
		 * @throws JDTOException
		 */ 
		public void rcvY5CarArrWrk(JDTORecord inRecord) throws JDTOException  {
			// 
			// YD-UC-????
			// TC : Y5YDL017
			// Y5 상차도 작업불가 수신
			//
			String szMsg        = "";
			String szMethodName = "rcvY5CarArrWrk";

			if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
				szMsg=szMethodName+"() 실행 실패";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;	
			}
			
			try {
				ydEjbCon.trx("CarLdLotRegSeEJB", "procY5CarArrWrk", inRecord);
			} catch (Exception e) {			
				szMsg = szMethodName + "() " + e.getMessage(); 
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new JDTOException(szMsg);
			} // end of try catch
			
			szMsg="Y5 차량동간이적(도착) 수신("+szMethodName+") 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} // end of  rcvY5CarArrWrk()

		//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
		//                                                
		//                     일관제철소정보관리시스템-야드관리
		//              저장품관리-상차LOT등록 Facade Session Bean
		//                          2008.09.30 YHWHman
		//                                                      
		//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
		
		

  //---------------------------------------------------------------------------
} // end of class

