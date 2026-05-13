/**
 * @(#)WrkPlnRegFaEJBBean.java
 * 
 * @version			1.0
 * @author 			현대제철
 * @date			2012/11/14
 * 
 * @description		이클래스는작업예정등록 Facade EJB클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.01  2013/04/03   허철호      허철호      신규설비 추가
 */

package com.inisteel.cim.yd.ydStock.WkPlanReg;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;

import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import jspeed.base.util.StringHelper;


/**
 * 작업예정등록 Facade Session EJB
 *
 * @ejb.bean name="WrkPlnRegFaEJB" jndi-name="WrkPlnRegFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class WrkPlnRegFaEJBBean  extends BaseSessionBean {
	
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
	 * 오퍼레이션명 : 연주전단지시확정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCcFsOrdCmmt(JDTORecord inRecord) throws JDTOException{
		//
		// YD-UC-1211 연주전단지시확정
		// TC : CTYDJ011
		// 생산통제 시스템으로부터 연주전단지시확정 수신
		//
		//┏━┓
		//┃생산통제에서 연주전단지시를 확정한 Event를 수신하여 
		//┃연주전단지시 Table From/To Point에 해당하는 지시를 야드작업지시이력Table에 등록
		//┗━┛

		String szMsg="";
		String szMethodName="rcvCcFsOrdCmmt";


		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}

		try {

			// 연주전단지시확정  수신
			//ydEjbCon.trx("WrkPlnRegSeEJB", "procCcFsOrdCmmt", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch


		szMsg="연주전단지시확정 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvCcFsOrdCmmt()
	
	/**
	 * 오퍼레이션명 : 후판압연지시확정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvPlMillOrdCmmt(JDTORecord inRecord) throws JDTOException{
		//
		// YD-UC-1219 후판압연지시등록
		// TC : CTYDJ031
		// 생산통제시스템으로부터 후판압연지시확정 수신
		//
		//┏━┓
		//┃생산통제에서 후판압연지시확정 Event를 수신하여 
		//┃후판압연지시 Table From/To Point에 해당하는 지시를 야드작업지시이력Table에 등록하고
		//┃저장품 Table에 가열로 보급 장입대Lot-No, 장입소Lot-No를 등록
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvPlMillOrdCmmt";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
		try {

			// 후판압연지시확정  수신
			ydEjbCon.trx("WrkPlnRegSeEJB", "procPlMillOrdCmmt", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		
		szMsg="후판압연지시확정 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvPlMillOrdCmmt()	
	
	/**
	 * 오퍼레이션명 : A후판압연지시결번실적수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvAPlMillOrdMissnoWr(JDTORecord inRecord) throws JDTOException{
		//
		// YD-UC-1220 A후판압연지시결번등록
		// TC : PRYDJ001
		// 후판조업시스템으로부터 A후판압연결번실적 수신
		//
		//┏━┓
		//┃후판조업에서 A후판압연결번실적(장입전)을 송신하면 
		//┃	저장품 Table에 가열로 보급 장입대Lot-No, 장입소Lot-No를 삭제
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvAPlMillOrdMissnoWr";
		

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		try {

			// A후판압연결번실적  수신
			ydEjbCon.trx("WrkPlnRegSeEJB", "procAPlMillOrdMissnoWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="A후판압연지시결번실적수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of rcvAPlMillOrdMissnoWr()
	




	/**
	 * 오퍼레이션명 : C열연압연지시확정 (CTYDJ033)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrMillOrdCmmt(JDTORecord inRecord) throws JDTOException{
		//
		// YD-UC-1221 C열연압연지시확정
		// TC : CTYDJ033
		// 생산통제시스템으로부터 C열연압연지시확정 수신
		//
		//┏━┓
		//┃ 생산통제에서 열연압연지시확정 Event를 수신하여 
		//┃	열연압연지시 Table From/To Point에 해당하는 지시를 야드작업지시이력Table에 등록하고 
		//┃	저장품 Table에 가열로 보급 장입대Lot-No를 등록
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrMillOrdCmmt";
		
		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	
			return;
		}
		
		try {

			// C열연압연지시확정  수신
			ydEjbCon.trx("WrkPlnRegSeEJB", "procCHrMillOrdCmmt", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
	
		
		szMsg="C열연압연지시확정 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of rcvCHrMillOrdCmmt()
	



	
	/**
	 * 오퍼레이션명 : C열연압연지시결번실적수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrMillOrdMissnoWr(JDTORecord inRecord) throws JDTOException{
		//
		// YD-UC-1222 C열연압연지시결번실적수신
		// TC : HRYDJ001
		// 열연조업시스템으로부터 C열연압연지시결번실적 수신
		//
		//┏━┓
		//┃열연조업시스템이 C열연압연지시결번실적(장입전)을 송신하면 
	 	//┃	저장품 Table에 가열로 보급 장입대Lot-No, 장입소Lot-No를 삭제
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrMillOrdMissnoWr";
		
	
		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	
			return;
			
		}
		
		try {

			// C열연압연지시결번실적 수신
			ydEjbCon.trx("WrkPlnRegSeEJB", "procCHrMillOrdMissnoWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		
		szMsg="C열연압연지시결번실적수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of rcvCHrMillOrdMissnoWr()
	
	
	
	
	
	/**
	 * 오퍼레이션명 : C열연정정작업지시수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrShearOrd(JDTORecord inRecord) throws JDTOException{
		//
		// YD-UC-1223 C열연정정작업지시수신
		// TC : HRYDJ005
		// 열연조업시스템으로부터 C열연정정작업지시 수신
		//
		//┏━┓
		//┃열연조업에서 C열연정정지시Event를 수신하여 
		//┃열연정정지시 Table From/To Point에 해당하는 지시를 야드작업지시이력Table에 등록하고 
		//┃저장품 Table에 정정보급순서를 등록
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrShearOrd";
		
	
		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	
			return;
			
		}
		
		try {

			// C열연정정작업지시  수신
			ydEjbCon.trx("WrkPlnRegSeEJB", "procCHrShearOrd", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		
		szMsg="C열연정정작업지시수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of rcvCHrShearOrd()
	

	
	
	
	/**
	 * 오퍼레이션명 : C열연정정지시결번실적수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrShearOrdMissnoWr(JDTORecord inRecord) throws JDTOException{
		//
		// YD-UC-1224 C열연정정지시결번실적수신
		// TC : HRYDJ006
		// 열영조업시스템으로부터 C열연정정지시결번실적 수신
		//
		//┏━┓
		//┃열연조업에서 열연정정결번실적을 수신하여 저장품 Table에 정정보급순서를 삭제
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrShearOrdMissnoWr";
		
	
		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	
			return;
		}
		
		try {

			// C열연정정지시결번실적  수신
			ydEjbCon.trx("WrkPlnRegSeEJB", "procCHrShearOrdMissnoWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		
		szMsg="C열연정정지시결번실적수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of rcvCHrShearOrdMissnoWr()	
	




	/**
	 * 오퍼레이션명 : B열연압연지시확정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvBHrMillOrdCmmt(JDTORecord inRecord) throws JDTOException{
		//
		// YD-UC-???? B열연압연지시확정
		// TC : CTYDJ032
		// 생산통제시스템으로부터 B열연압연지시확정 수신
		//
		//┏━┓
		//┃ 생산통제에서 열연압연지시확정 Event를 수신하여 
		//┃	열연압연지시 Table From/To Point에 해당하는 지시를 야드작업지시이력Table에 등록하고 
		//┃	저장품 Table에 가열로 보급 장입대Lot-No를 등록
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvBHrMillOrdCmmt";
		
	
		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	
			return;
			
		}
		
		try {

			// B열연압연지시확정  수신
			//ydEjbCon.trx("WrkPlnRegSeEJB", "procBHrMillOrdCmmt", inRecord);
			
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
			
			if(sBSLAB_EFF_YN.equals("Y")) {
				//B열연  신규모듈 적용
				ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
			} else  {
				//기존모듈 호출
				ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
			}
			
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		
		szMsg="B열연압연지시확정 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of rcvBHrMillOrdCmmt()
	

	
	


	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//                                                
	//                     일관제철소정보관리시스템-야드관리
	//              저장품관리-작업예정등록 Facade Session Bean
	//                          2008.09.30 YHWHman
	//                                                      
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
	
		
		
		
  //---------------------------------------------------------------------------	
} // end of class


