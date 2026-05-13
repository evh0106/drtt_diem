/**
 * @(#)StockSpecRegFaEJBBean.java
 * 
 * @version			1.0
 * @author 			현대제철
 * @date			2012/11/14
 * 
 * @description		이클래스는 저장품제원등록 Facade EJB클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.01  2013/04/03   허철호      허철호      신규설비 추가
*/

package com.inisteel.cim.yd.ydStock.StockSpecReg;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.yd.common.rule.GetBreRule6;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;


/**
 * 저장품제원등록 Facade Session EJB
 *
 * @ejb.bean name="StockSpecRegFaEJB" jndi-name="StockSpecRegFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class StockSpecRegFaEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName=getClass().getName();
	
	private YdUtils ydUtils =new YdUtils();
	private YdDaoUtils ydDaoUtils  = new YdDaoUtils();
	
	private EJBConnector ydEjbCon = new EJBConnector("default", this);

	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * 오퍼레이션명 : 후판압연사양확정등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvPlMillSpecCmmt(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-1102 후판압연사양확정등록
		// TC : CTYDJ021
		// 생산통제시스템으로부터 후판압연사양확정등록 수신
		//
		//┏━┓
		//┃ 생산통제에서 후판압연사양확정 Event를 수신하여 Plate사양Table의 생산예정 Plate를 저장품에 등록
		//┗━┛
		String szMsg="";
		String szMethodName="rcvPlMillSpecCmmt";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}

		try {

			// 후판압연사양확정등록 수신
			ydEjbCon.trx("PlateSpecRegSeEJB", "procPlMillSpecCmmt", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);
		} // end of try catch
		
		szMsg="후판압연사양확정등록 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvPlMillSpecCmmt()
	
	/**
	 * 오퍼레이션명 : 구입슬라브등록실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvBuySlabRegWr(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-1103 구입슬라브등록실적
		// TC : QMYDJ001
		// 품질관리시스템으로부터 구입슬라브등록실적 수신 
		//
		//┏━┓
		//┃ 품질관리에서 구입슬라브에 대해 슬라브공통Table에 등록한 실적을 수신하여 저장품에 관련 항목을 등록
		//┗━┛
	
	
		String szMsg="";
		String szMethodName="rcvBuySlabRegWr";


		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}

		try {

			// 구입슬라브등록실적 수신
			ydEjbCon.trx("SlabSpecRegSeEJB", "procBuySlabRegWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		
		
		szMsg="구입슬라브등록실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
		
	} // end of rcvBuySlabRegWr()
	
	
	
	
	/**
	 * 오퍼레이션명 : 연주전단실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCcFsWr(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-1104 연주전단실적
		// TC : CSYDJ001
		// 연주조업시스템으로부터 연주전단실적 수신
		//
		//┏━┓
		//┃연주조업에서 연주전단실적(CCM #1,2,3)을 수신하여 저장품에 관련 항목을 등록 
		//┗━┛

		String szMsg="";
		String szMethodName="rcvCcFsWr";
		String szOperationName = "연주전단실적";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
		try {

			// 연주전단실적 수신
			ydEjbCon.trx("SlabSpecRegSeEJB", "procCcFsWr", inRecord);
			
			// AB열연 호출 (슬라브지시행선 : HB)
			String sSlabWoRtCd = ydDaoUtils.paraRecChkNull(inRecord,"SLAB_WO_RT_CD");
			if("HB".equals(sSlabWoRtCd)||
			   "HC".equals(sSlabWoRtCd)||
			   "MS".equals(sSlabWoRtCd)||
			   "PA".equals(sSlabWoRtCd)){
				szMsg="== 연주전단실적 AB열연 송신 ==";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				ydEjbCon.trx("JNDISlabReg", "procCcFsWr", inRecord);
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch



		szMsg="연주전단실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


	} // end of rcvCcFsWr()
	
	
	
	
	/**
	 * 오퍼레이션명 : SCARFING실적 (CSYDJ002, QMYDJ004)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvScarfWr(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-1105 SCARFING실적

		// TC : CSYDJ002, QMYDJ004
		// 연주조업시스템 또는 품질시스템으로 부터 SCARFING실적 수신
		//
		//┏━┓
		//┃ 연주조업 또는 품질시스템에서 주편 SCARFING실적을 수신하여 저장품에 관련 항목을 등록
		//┗━┛


		String szMsg="";
		String szMethodName="rcvScarfWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}

		try {

			// 연주Scarfing실적 수신
			ydEjbCon.trx("SlabSpecRegSeEJB", "procScarfWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="SCARFING실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


	} // end of rcvScarfWr()

	/**
	 * 오퍼레이션명 : 후판제품 상세변경 (QMYDJ005) - 2010.02.17 이영근
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvQMPlateProgSync(JDTORecord inRecord) throws JDTOException  {
		//
		// 후판제품 상세변경
		// TC : QMYDJ005
		// 공정계획시스템으로부터 후판제품 상세변경 수신
		//

		String szMethodName = "rcvQMPlateProgSync"; 
		String szMsg        = "";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try {

			// 후판제품 상세변경 수신
			ydEjbCon.trx("RtModRegSeEJB", "procQMPlateProgSync", inRecord);
			
			/*
			 * AB열연 시스템으로 인터페이스 전달  
			 */
			ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);

		} catch (Exception e) {			
			szMsg = szMethodName + "() " + e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}

		szMsg = "[공정계획]후판제품 상세변경 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} 
	
	/**
	 * 오퍼레이션명 : 연주정정실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException, JDTOException
	 */
	public void rcvCsShearWr(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-1106 연주정정실적
		// TC : CSYDJ003
		// 연주조업시스템으로부터 연주정정실적 수신
		//
		//┏━┓
		//┃ 연주조업에서 연주정정실적을 수신하여 저장품에 관련된 항목을 등록
		//┗━┛
	
		
		String szMsg="";
		String szMethodName="rcvCsShearWr";


		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;

		}

		try {

			// 연주정정실적 수신
			ydEjbCon.trx("SlabSpecRegSeEJB", "procCsShearWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		
		szMsg="연주정정실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of rcvCsShearWr()
	
	/**
	 * 오퍼레이션명 : A후판슬라브분할실적수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException, JDTOException
	 */
	public void rcvAPlSlabDivWr(JDTORecord inRecord) throws JDTOException{
		//
		// YD-UC-1108 A후판슬라브분할실적수신
		// TC : PRYDJ003
		// 후판조업시스템으로부터 A후판슬라브분할실적 수신
		//
		//┏━┓
		//┃ 후판조업에서 A후판슬라브분할실적수신하여 저장품에 관련된 항목을 등록
		//┗━┛
	
		String szMsg="";
		String szMethodName="rcvAPlSlabDivWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
		try {

			// A후판슬라브분할실적 수신
			ydEjbCon.trx("PlateSpecRegSeEJB", "procAPlSlabDivWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="A후판슬라브분할실적수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of rcvAPlSlabDivWr()
		
	/**
	 * 오퍼레이션명 : C열연압연생산실적수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrMillPrdWr(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-1110 C열연압연생산실적수신
		// TC : HRYDJ003
		// 열연조업시스템으로부터 C열연압연생산실적 수신
		//
		//┏━┓
		//┃열연조업에서 Coil 압연생산실적(권취)을 수신하여 저장품에 관련된 항목을 등록 
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCHrMillPrdWr";

//		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
//			
//			szMsg=szMethodName+"() 실행 실패";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//
//			return;
//			
//		}
		
		try {

			// C열연압연생산실적수신 수신
			ydEjbCon.trx("CoilSpecRegSeEJB", "procCHrMillPrdWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="C열연압연생산실적수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of rcvCHrMillPrdWr()
	
	
	
	/**
	 * 오퍼레이션명 : C열연압연작업실적수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrMillWrkWr(JDTORecord inRecord) throws JDTOException {	
		// 신규
		// YD-UC-1111 C열연압연작업실적수신
		// TC : HRYDJ004
		// 열연조업시스템으로부터 C열연압연작업실적 수신
		//
		//┏━┓
		//┃열연조업에서 Coil 압연작업실적(평량)을 수신하여 저장품에 관련된 항목을 등록
		//┗━┛

		String szMsg="";
		String szMethodName="rcvCHrMillWrkWr";

//		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
//
//			szMsg=szMethodName+"() 실행 실패";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//
//			return;
//		}


		try {

			// C열연압연작업실적 수신
			ydEjbCon.trx("CoilSpecRegSeEJB", "procCHrMillWrkWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="C열연압연작업실적수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);




	} // end of rcvCHrMillWrkWr()
	
	
	
	
	
	/**
	 * 오퍼레이션명 : C열연정정작업실적수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCHrShearWrkWr(JDTORecord inRecord) throws JDTOException {		
		//
		// YD-UC-1112 C열연정정작업실적수신
		// TC : HRYDJ007
		// 열연조업시스템으로부터 C열연정정작업실적수신 수신
		//
		//┏━┓
		//┃ 열연조업에서 Coil 정정작업실적(SPM1, Dividing, HFL)을 수신하여 저장품에 관련된 항목을 등록
		//┗━┛
	
		
		String szMsg="";
		String szMethodName="rcvCHrShearWrkWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}

		try {

			// C열연정정작업실적수신
			ydEjbCon.trx("CoilSpecRegSeEJB", "procCHrShearWrkWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="C열연정정작업실적수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of rcvCHrShearWrkWr()
	
	/**
	 * 오퍼레이션명 : A후판제품생산실적수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvAPlGdsPrdWr(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-1114 A후판제품생산실적수신
		// TC : PRYDJ004
		// 후판조업시스템으로부터 A후판제품생산실적 수신
		//
		//┏━┓
		//┃ 후판조업에서 Plate 생산실적(전단, 정정, 열처리, 보수)을 수신하여 저장품에 관련된 항목을 등록
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvAPlGdsPrdWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
		try {
			String szRcvTcCode = ydUtils.getTcCode(inRecord);

			if("PPYDJ004".equals(szRcvTcCode)) {
				// 2후판제품생산실적
				ydEjbCon.trx("PlateSpecRegSeEJB", "procPl2GdsPrdWr", inRecord);				
			} else {
				
				String sStlNo = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO");
				/*
				 * 2014.02.17 윤재광
				 * 2후판 분할판이 1후판으로 이송해서 절단되는 경우 -> PRYDJ004 생산실적으로 넘어옴
				 * 다시 2후판으로 이송이 될 경우를 대비해서 2후판 정정야드 저장품에 정보만 등록한다.
				 */
				if(sStlNo.startsWith("FB") || sStlNo.startsWith("FD")){  //FD 추가
					// 2후판 정정야드 재료정보 생성모듈 호출
					JPlateYdStockDAO    ydStockDao   	= new JPlateYdStockDAO();
					
					JDTORecord recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO",	sStlNo);  // 재료번호
					
					ydStockDao.insYdStockBookOut(recPara);
				}  
				
				// A후판제품생산실적
				ydEjbCon.trx("PlateSpecRegSeEJB", "procAPlGdsPrdWr", inRecord);
				
			}
			
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		szMsg="A후판제품생산실적수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	
	} // end of rcvAPlGdsPrdWr()
	
	/**
	 * 오퍼레이션명 : 외판슬라브반품
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvOutplSlabRetngds(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-1118 외판슬라브반품
		// TC : DMYDR032
		// 출하관리시스템으로부터 외판슬라브반품 수신
		//
		//┏━┓
		//┃ 출하관리에서 Slab(외판) 반품실적을 수신하여 저장품에 관련된 항목을 등록
		//┗━┛
	
		
		String szMsg="";
		String szMethodName="rcvOutplSlabRetngds";
		String szOperationName = "외판슬라브반품";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
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
					//B열연 신규모듈 적용
					ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
				} else  {
					//기존모듈 호출				
					ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
				}
			}else{
				// 외판슬라브반품실적 수신
				ydEjbCon.trx("SlabSpecRegSeEJB", "procOutplSlabRetngds", inRecord);
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		
		
		szMsg="외판슬라브반품 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	} // end of rcvOutplSlabRetngds()
	
	/**
	 * 오퍼레이션명 : 코일제품반품
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvCoilGdsRetngds(JDTORecord inRecord) throws JDTOException
	{
		//
		// YD-UC-1119 코일제품반품
		// TC : DMYDR033
		// 출하관리시스템으로부터 코일제품반품 수신
		//
		//┏━┓
		//┃ 출하관리에서 Coil 제품 반품실적을 수신하여 저장품에 관련된 항목을 등록
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCoilGdsRetngds";
		String szOperationName = "코일제품반품";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName))
		{
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;	
		}
		
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
        			// 코일제품반품실적 수신
    				ydEjbCon.trx("CoilSpecRegSeEJB", "procCoilGdsRetngds", inRecord);
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
		
		szMsg="코일제품반품 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvCoilGdsRetngds()

	/**
	 * 오퍼레이션명 : 후판제품반품
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvPlGdsRetngds(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-1120 후판제품반품
		// TC : DMYDR034
		// 출하관리시스템으로부터 후판제품반품 수신
		//
		//┏━┓
		//┃ 출하관리에서 Plate 제품 반품실적을 수신하여 저장품에 관련된 항목을 등록
		//┗━┛
	
		
		String szMsg="";
		String szMethodName="rcvPlGdsRetngds";
		String szOperationName = "후판제품반품";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");			
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				// AB열연 수신
				ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
			}else{
				// 후판제품반품 수신
				ydEjbCon.trx("PlateSpecRegSeEJB", "procPlGdsRetngds", inRecord);
			}
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		
		
		szMsg="후판제품반품 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	
	}  // end of rcvPlGdsRetngds()
	
	/**
	 * 오퍼레이션명 : 후판제품목적지코드 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvPlGdsDestChgInfo(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-1120 후판제품목적지코드 변경
		// TC : DMYDR044
		// 출하관리시스템으로부터 후판제품목적지코드 변경
		//
		//┏━┓
		//┃ 출하관리에서 Plate 후판제품목적지코드 변경실적을 수신하여 저장품에 관련된 항목을 등록
		//┗━┛
	
		
		String szMsg="";
		String szMethodName="rcvPlGdsDestChgInfo";
		String szOperationName = "후판제품목적지코드 변경";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		try {
				//  후판제품목적지코드 변경 수신
				ydEjbCon.trx("PlateSpecRegSeEJB", "procPlGdsDestChgInfo", inRecord);
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch
		
		
		szMsg=" 후판제품목적지코드 변경 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	
	}  // end of rcvPlGdsDestChgInfo()
	
	
	
	
	/**
	 * 오퍼레이션명 : 슬라브보류실적 (QMYDJ002) 권오창
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvSlabHoldWr(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-1120 슬라브보류실적
		// TC : QMYDJ002
		// 품질관리시스템으로부터 슬라브보류실적 수신
		//	
		
		String szMsg        = "";
		String szMethodName = "rcvSlabHoldWr";
		String szOperationName = "슬라브보류실적";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");			
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
			}else{
				// 슬라브보류실적
				ydEjbCon.trx("SlabSpecRegSeEJB", "procSlabHoldWr", inRecord);
			}
		} catch (Exception e) {			
			szMsg = szMethodName + "() " + e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} 
		
		szMsg = "슬라브보류실적 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	}

	
	
	/**
	 * 오퍼레이션명 : Scarfing 대상재변경 (QMYDJ003) 이영근
	 * 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvSlabScarSTLNo(JDTORecord inRecord) throws JDTOException {
		//
		// Scarfing 대상재변경
		// TC : QMYDJ003
		// 품질시스템으로부터  Scarfing 대상재변경 수신
		//	
		
		String szMsg        = "";
		String szMethodName = "rcvSlabScarSTLNo";
		String szOperationName = "Scarfing 대상재변경";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");			
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
			}else{
				// Scarfing 대상재변경
				ydEjbCon.trx("SlabSpecRegSeEJB", "procSlabScarSTLNo", inRecord);
			}
		} catch (Exception e) {			
			szMsg = szMethodName + "() " + e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} 
		
		szMsg = "Scarfing 대상재변경 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	}
	
	/**
	 * 오퍼레이션명 : 후판압연지시확정시 후판제품창고야드 동별저장계획 적용
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvYdBayLocPln3G(JDTORecord inRecord) throws JDTOException {
		//
		// 후판압연지시확정시 후판제품창고야드 동별저장계획 적용
		// TC :  YDYDJ031
		// CTYDJ031 수신 처리시 내부 인터페이스(YDYDJ031)로 호출
		//
		//┏━┓
		//┃ 생산통제에서 후판압연지시확정 Event를 수신하여 YD저장품(STOCK)을 생성하고 입고 예정위치를 설정한다.
		//┗━┛
		String szMsg="";
		String szMethodName="rcvYdBayLocPln3G";
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START 
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= inRecord.getResultCode();				// 전문으로 부터 logid get
String logId                    		= ydUtils.getJDTOLogId(inRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 					// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "후판압연지시확정 JMS 수신 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

szMsg = "전문수신 : TCCODE = [" + ydUtils.getTcCode(inRecord) + "]";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

		try {


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// procYdBayLocPlnCncl3G, procYdBayLocPln3GNew call 시  inRecord 에 logId SET 추가 개선
			inRecord.setField("LOG_ID", logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
			
			// 수신항목[MOD_GP: 모드구분]
			String sModGp	   		= ydDaoUtils.paraRecChkNull(inRecord, "MOD_GP");
        	/*
        	 * I : 생산통제 압연지시
        	 * D : 생산통제 압연지시 취소
        	 */
        	if("D".equals(sModGp)){
    			// 후판압연지시취소 JMS 수신
    			ydEjbCon.trx("PlateSpecRegSeEJB", "procYdBayLocPlnCncl3G", inRecord);
        	} if("I".equals(sModGp)){
    			// 후판압연지시확정 JMS 수신
    			ydEjbCon.trx("PlateSpecRegSeEJB", "procYdBayLocPln3GNew", inRecord);
        	}
        		

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

			throw new JDTOException(szMsg);
		} // end of try catch
		
		szMsg="후판압연지시확정 JMS 수신 처리("+szMethodName+") 완료";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
		
	} // end of rcvYdBayLocPln3G()
	
	
	/**
	 * 오퍼레이션명 : 2후판 이상재실적수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvPl2AbmtWr(JDTORecord inRecord) throws JDTOException {
		//
		// 후판압연지시확정시 후판제품창고야드 동별저장계획 적용
		// TC :  PPYDJ008
		//
		//┏━┓
		//┃ 후판조업에서 이상재 발생시 야드로 전문을 전송한다.
		//┗━┛
		String szMsg="";
		String szMethodName="rcvPl2AbmtWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}

		try {
			
			ydEjbCon.trx("PlateSpecRegSeEJB", "procPl2AbmtWr", inRecord);
			
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);
		} // end of try catch
		
		szMsg="후판압연지시확정 JMS 수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvPl2AbmtWr()
	
	
	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//                                                
	//                     일관제철소정보관리시스템-야드관리
	//              저장품관리-저장품제원등록 Facade Session Bean
	//                          2008.09.30 YHWHman
	//                                                      
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
	
	public static void main(String[] args){
		StockSpecRegFaEJBBean im =new StockSpecRegFaEJBBean();
		JDTORecord testRec =JDTORecordFactory.getInstance().create();
		
		try {
			testRec.setField("JMS_TC_CD","QMYDJ001 ");
			//im.rcvBuySlabRegWr(testRec);
		} catch (JDTOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	} // end of testMain()

//-----------------------------------------------------------------------------	
} // end of class


