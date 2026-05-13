package com.inisteel.cim.yd.ydEquipStat.EquipTrack;

import xlib.cmc.GridData;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdUtils;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;


/**
 * 설비Tracking Facade Session EJB 
 * 
 * @ejb.bean name="EqpTrackingFaEJB" jndi-name="EqpTrackingFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class EqpTrackingFaEJBBean extends BaseSessionBean {

	// Session Name
	private String szSessionName  = getClass().getName();
	private YdUtils ydUtils       = new YdUtils();
	private EJBConnector ydEjbCon = new EJBConnector("default", this);





	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}


	/**
	 * 오퍼레이션명 : C연주정정L2 C3수불구용도변경요구(C3YDL001, C7YDL001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvC3TkovlocUsgMod(JDTORecord inRecord) throws JDTOException  {
		String szMsg        = "";
		String szMethodName = "rcvC3TkovlocUsgMod";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procC3TkovlocUsgMod", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "C3수불구용도변경요구 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvC3TkovlocUsgMod()


	/**
	 * 오퍼레이션명 : C연주정정L2 C3수불구재료적치정보(C3YDL002, C7YDL002)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvC3MatlStkInfo(JDTORecord inRecord) throws JDTOException  {
		String szMsg        = "";
		String szMethodName = "rcvC3MatlStkInfo";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procC3MatlStkInfo", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "C3수불구재료적치정보 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvC3MatlStkInfo()


	/**
	 * 오퍼레이션명 : C연주정정L2 C3ROT재료도착통과정보(C3YDL010, C7YDL010)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvC3RotMatlArrPassInfo(JDTORecord inRecord) throws JDTOException  {
		String szMsg        = "";
		String szMethodName = "rcvC3RotMatlArrPassInfo";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procC3RotMatlArrPassInfo", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "ROT재료도착통과정보 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvC3RotMatlArrPassInfo()


	/**
	 * 오퍼레이션명 : C연주정정L2 C3설비고장복구실적(C3YDL008, C7YDL008)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvC3EqpTrblRcvrWr(JDTORecord inRecord) throws JDTOException  {
		String szMsg        = "";
		String szMethodName = "rcvC3EqpTrblRcvrWr";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procC3EqpTrblRcvrWr", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "C3설비고장복구실적 처리(" + szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvC3EqpTrblRcvrWr()


	/**
	 * 오퍼레이션명 : C연주정정L2 C3설비모드변경실적(C3YDL009, C7YDL009)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvC3EqpMdModWr(JDTORecord inRecord) throws JDTOException  {
		String szMsg        = "";
		String szMethodName = "rcvC3EqpMdModWr";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procC3EqpMdModWr", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "C3설비모드변경실적 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvC3EqpMdModWr()


	/**
	 * 오퍼레이션명 : Y1설비운전모드전환
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvY1EqpDrvMdTurnov(JDTORecord inRecord) throws JDTOException  {
		// TKOVLOC
		// YD-UC-????
		// TC : Y1YDL003
		// C연주슬라브야드L2시스템으로부터 설비운전모드전환 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY1EqpDrvMdTurnov";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procY1EqpDrvMdTurnov", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "Y1설비운전모드전환 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvY1EqpDrvMdTurnov()


	/**
	 * 오퍼레이션명 : Y1설비고장복구실적
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvY1EqpTrblRcvrWr(JDTORecord inRecord) throws JDTOException  {
		// TKOVLOC
		// YD-UC-????
		// TC : Y1YDL004
		// C연주슬라브야드L2시스템으로부터 설비고장복구실적 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY1EqpTrblRcvrWr";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procY1EqpTrblRcvrWr", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "Y1설비고장복구실적 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvY1EqpTrblRcvrWr()


	/**
	 * 오퍼레이션명 : Y1크레인현재위치
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvY1CrnCurrLoc(JDTORecord inRecord) throws JDTOException  {
		// TKOVLOC
		// YD-UC-????
		// TC : Y1YDL005
		// C연주슬라브야드L2시스템으로부터 크레인현재위치 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY1CrnCurrLoc";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procY1CrnCurrLoc", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "Y1크레인현재위치 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvY1CrnCurrLoc()


	/**
	 * 오퍼레이션명 : Y3설비운전모드전환
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvY3EqpDrvMdTurnov(JDTORecord inRecord) throws JDTOException  {
		// TKOVLOC
		// YD-UC-????
		// TC : Y3YDL003
		// A후판슬라브야드L2시스템으로부터 설비운전모드전환 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY3EqpDrvMdTurnov";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procY3EqpDrvMdTurnov", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "Y3설비운전모드전환 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvY3EqpDrvMdTurnov()


	/**
	 * 오퍼레이션명 : Y3설비고장복구실적
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvY3EqpTrblRcvrWr(JDTORecord inRecord) throws JDTOException  {
		// TKOVLOC
		// YD-UC-????
		// TC : Y3YDL004
		// A후판슬라브야드L2시스템으로부터 설비고장복구실적 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY3EqpTrblRcvrWr";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procY3EqpTrblRcvrWr", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "Y3설비고장복구실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvY3EqpTrblRcvrWr()


	/**
	 * 오퍼레이션명 : Y3크레인현재위치
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvY3CrnCurrLoc(JDTORecord inRecord) throws JDTOException  {
		// TKOVLOC
		// YD-UC-????
		// TC : Y3YDL005
		// A후판슬라브야드L2시스템으로부터 크레인현재위치 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY3CrnCurrLoc";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procY3CrnCurrLoc", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "Y3크레인현재위치 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvY3CrnCurrLoc()


	/**
	 * 오퍼레이션명 : Y3수불구용도변경요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvY3TkovlocUsgMod(JDTORecord inRecord) throws JDTOException  {
		// TKOVLOC
		// YD-UC-????
		// TC : Y3YDL011
		// C연주정정L2시스템으로부터 수불구용도변경요구 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY3TkovlocUsgMod";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procY3TkovlocUsgMod", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "Y3수불구용도변경요구 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvY3TkovlocUsgMod()


	/**
	 * 오퍼레이션명 : Y4설비운전모드전환
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvY4EqpDrvMdTurnov(JDTORecord inRecord) throws JDTOException  {
		//
		// YD-UC-????
		// TC : Y4YDL003
		// 후판제품야드L2시스템으로부터 설비운전모드전환 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY4EqpDrvMdTurnov";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procY4EqpDrvMdTurnov", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "Y4설비운전모드전환 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvY4EqpDrvMdTurnov()


	/**
	 * 오퍼레이션명 : Y8설비운전모드전환
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvY8EqpDrvMdTurnov(JDTORecord inRecord) throws JDTOException  {
		//
		// TC : Y8YDL003
		// 2후판제품야드L2시스템으로부터 설비운전모드전환 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY8EqpDrvMdTurnov";


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
//String logId                    		= inRecord.getResultCode();				// 전문으로 부터 logid get
String logId                    		= ydUtils.getJDTOLogId(inRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 					// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8설비운전모드전환 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			return ;
		}

		try {
			
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 전문처리 procY4EqpDrvMdTurnov Method에 ([T] + 전문일련번호) 형식으로  logId 넘김
			inRecord.setField("LOG_ID", logId);                                         
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
			
			ydEjbCon.trx("EqpTrackingSeEJB", "procY4EqpDrvMdTurnov", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "Y8설비운전모드전환 처리(" + szMethodName + ") 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	} // end of rcvY8EqpDrvMdTurnov()


	/**
	 * 오퍼레이션명 : Y4설비고장복구실적
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvY4EqpTrblRcvrWr(JDTORecord inRecord) throws JDTOException  {
		//
		// YD-UC-????
		// TC : Y4YDL004
		// 후판제품야드L2시스템으로부터 설비고장복구실적 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY4EqpTrblRcvrWr";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procY4EqpTrblRcvrWr", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "Y4설비고장복구실적 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvY4EqpTrblRcvrWr()


	/**
	 * 오퍼레이션명 : Y8설비고장복구실적
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvY8EqpTrblRcvrWr(JDTORecord inRecord) throws JDTOException  {
		//
		// TC : Y8YDL004
		// 2후판제품야드L2시스템으로부터 설비고장복구실적 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY8EqpTrblRcvrWr";

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선 START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= inRecord.getResultCode();				// 전문으로 부터 logid get
String logId                    		= ydUtils.getJDTOLogId(inRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 					// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8설비고장복구실적 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName+"() 실행 실패";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			return ;
		}

		try {
			
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선 START
// 전문처리 procY4EqpTrblRcvrWr Method에 ([T] + 전문일련번호) 형식으로  logId 넘김
			inRecord.setField("LOG_ID", logId);                                         
// 2024.09.02 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////
			
			ydEjbCon.trx("EqpTrackingSeEJB", "procY4EqpTrblRcvrWr", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "Y8설비고장복구실적 처리(" + szMethodName + ") 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	} // end of rcvY8EqpTrblRcvrWr()


	/**
	 * 오퍼레이션명 : Y4크레인현재위치
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvY4CrnCurrLoc(JDTORecord inRecord) throws JDTOException  {
		//
		// YD-UC-????
		// TC : Y4YDL005
		// 후판제품야드L2시스템으로부터 크레인현재위치 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY4CrnCurrLoc";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procY4CrnCurrLoc", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "Y4크레인현재위치 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvY4CrnCurrLoc()


	/**
	 * 오퍼레이션명 : Y8크레인현재위치
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvY8CrnCurrLoc(JDTORecord inRecord) throws JDTOException  {
		//
		// TC : Y8YDL005
		// 2후판제품야드L2시스템으로부터 크레인현재위치 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY8CrnCurrLoc";


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= inRecord.getResultCode();				// 전문으로 부터 logid get
String logId                    		= ydUtils.getJDTOLogId(inRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 					// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8크레인현재위치 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
//2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			return ;
		}

		try {
			
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 전문처리 procY4CrnCurrLoc Method에 ([T] + 전문일련번호) 형식으로  logId 넘김
			inRecord.setField("LOG_ID", logId);                                         
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
			
			ydEjbCon.trx("EqpTrackingSeEJB", "procY4CrnCurrLoc", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
//2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "Y8크레인현재위치 처리(" + szMethodName + ") 완료";
//2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	} // end of rcvY8CrnCurrLoc()



	/**
	 * 오퍼레이션명 : Y8입고존재료정보
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws DAOException
	 */
	public void rcvY8RcptZoneMtlInfo(JDTORecord inRecord) throws DAOException  {
		//
		// TC : Y8YDL010
		// 2후판제품야드L2시스템으로부터 입고존재료정보 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY8RcptZoneMtlInfo";


////////////////////////////////////////////////////////////////////////////////////////
//2024.09.02 로그 개선  START
//기존 putLog -> putLogNew logId 출력 되게 개선 : [T] + 전문일련번호
//
// String logId                    		= inRecord.getResultCode();				// 전문으로 부터 logid get
String logId                    		= ydUtils.getJDTOLogId(inRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 					// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8입고존재료정보 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

//2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		

		if(inRecord.getFieldString("MSG_ID").equals("Y8YDL013")){  //전문체크시 전문명, 메소드 명 함께 체크하므로 전문에 따라 메소드명도 미리 분기처리
			szMethodName = "rcvY8SpanMtlInfo";
		}

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			return ;
		}

		try {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// procY8SpanMtlInfo, procY8RcptZoneMtlInfo Method에 ([T] + 전문일련번호) 형식으로  logId 넘김
			inRecord.setField("LOG_ID", logId);
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
			
			if(inRecord.getFieldString("MSG_ID").equals("Y8YDL013")){
				ydEjbCon.trx("RcptWrkDmdSeEJB", "procY8SpanMtlInfo", inRecord);//메소드 생성 필요
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, "Y8YDL013 스판별 재고현황 요청 수신", YdConstant.DEBUG);
				ydUtils.putLogNew(szSessionName, szMethodName, "Y8YDL013 스판별 재고현황 요청 수신", YdConstant.DEBUG, logId);
			}
			else{
				ydEjbCon.trx("RcptWrkDmdSeEJB", "procY8RcptZoneMtlInfo", inRecord);
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, "Y8YDL010 입고존재료정보 수신", YdConstant.DEBUG);
				ydUtils.putLogNew(szSessionName, szMethodName, "Y8YDL010 입고존재료정보 수신", YdConstant.DEBUG, logId);
			}
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			throw new DAOException(szMsg);
		} // end of try catch

		szMsg = "Y8입고존재료정보 처리(" + szMethodName + ") 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	} // end of rcvY8RcptZoneMtlInfo()



	/**
	 * 오퍼레이션명 : Y8TB트래킹정보
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws DAOException
	 */
	public void rcvY8TfTrckInfo(JDTORecord inRecord) throws DAOException  {
		//
		// TC : Y8YDL011
		// 2후판제품야드L2시스템으로부터 Trans BED 트래킹정보 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY8TfTrckInfo";


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= inRecord.getResultCode();				// 전문으로 부터 logid get
String logId                    		= ydUtils.getJDTOLogId(inRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 					// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8TF트래킹정보 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			return ;
		}

		try {
			
        	
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 전문처리 procY8TfTrckInfo Method에 ([T] + 전문일련번호) 형식으로  logId 넘김
			inRecord.setField("LOG_ID", logId);                                         
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
			
			ydEjbCon.trx("RcptWrkDmdSeEJB", "procY8TfTrckInfo", inRecord);
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, "Y8YDL011 TB트래킹정보 수신", YdConstant.DEBUG);
			ydUtils.putLogNew(szSessionName, szMethodName, "Y8YDL011 TB트래킹정보 수신", YdConstant.DEBUG, logId);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			throw new DAOException(szMsg);
		} // end of try catch

		szMsg = "Y8TF트래킹정보 처리(" + szMethodName + ") 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	} // end of rcvY8TfTrckInfo()



	/**
	 * 오퍼레이션명 : Y8 BOOK-OUT요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws DAOException
	 */
	public void rcvY8BookOutReq(JDTORecord inRecord) throws DAOException  {
		//
		// TC : Y8YDL012
		// 2후판제품야드L2시스템으로부터 BOOK-OUT요구 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY8BookOutReq";

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId	= inRecord.getResultCode();										// 전문으로 부터 logid get
String logId                    		= ydUtils.getJDTOLogId(inRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 					// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8 BOOK-OUT요구 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			return ;
		}

		try {

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 전문처리 procY8BookOutReq Method에 ([T] + 전문일련번호) 형식으로  logId 넘김
			inRecord.setField("LOG_ID", logId);                                         
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
			
			ydEjbCon.trx("RcptWrkDmdSeEJB", "procY8BookOutReq", inRecord); //2013.03.05 수정 (3기)
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, "Y8YDL012 BOOK-OUT요구 수신", YdConstant.DEBUG);
			ydUtils.putLogNew(szSessionName, szMethodName, "Y8YDL012 BOOK-OUT요구 수신", YdConstant.DEBUG, logId);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			throw new DAOException(szMsg);
		} // end of try catch

		szMsg = "Y8 BOOK-OUT요구 처리(" + szMethodName + ") 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	} // end of rcvY8BookOutReq()

	/**
	 * 오퍼레이션명 : Y8 스판별 재고현황 요청 Y8YDL013
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws DAOException
	 */
	public void rcvY8SpanMtlInfo(JDTORecord inRecord) throws DAOException  {
		//
		// TC : Y8YDL013
		// 2후판제품야드L2시스템으로부터 스판별 재고현황 요청 전문 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY8SpanMtlInfo";

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= inRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(inRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8 스판별 재고현황 요청 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////


		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			return ;
		}

		try {
			ydEjbCon.trx("RcptWrkDmdSeEJB", "procY8SpanMtlInfo", inRecord);//메소드 생성 필요
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, "Y8YDL013 스판별 재고현황 요청 수신", YdConstant.DEBUG);
			ydUtils.putLogNew(szSessionName, szMethodName, "Y8YDL013 스판별 재고현황 요청 수신", YdConstant.DEBUG, logId);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			throw new DAOException(szMsg);
		} // end of try catch

		szMsg = "Y8 스판별 재고현황 요청 처리(" + szMethodName + ") 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	} // end of rcvY8SpanMtlInfo()


	/**
	 * 오퍼레이션명 : S1입고존도착정보
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws DAOException
	 */
	public void rcvS1RcptZoneArrInfo(JDTORecord inRecord) throws DAOException  {
		//
		// TC : S1PPL014
		// 2후판전단정정L2시스템으로부터 입고존도착정보 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvS1RcptZoneArrInfo";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("RcptWrkDmdSeEJB", "procS1RcptZoneArrInfo", inRecord);
			ydUtils.putLog(szSessionName, szMethodName, "S1YDL014 입고존도착정보 수신", YdConstant.DEBUG);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		szMsg = "S1입고존도착정보 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvS1RcptZoneArrInfo()


	/**
	 * 오퍼레이션명 : S1파일링실적
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws DAOException
	 */
	public void rcvS1PilingWr(JDTORecord inRecord) throws DAOException  {
		//
		// TC : S1YDL016
		// 2후판압연전단L2시스템으로부터 파일링실적 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvS1PilingWr";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("RcptWrkDmdSeEJB", "procS1PilingWr", inRecord);
			ydUtils.putLog(szSessionName, szMethodName, "S1PPL016 파일링실적 수신", YdConstant.DEBUG);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		szMsg = "S1파일링실적 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvS1PilingWr()


	/**
	 * 오퍼레이션명 : Y5설비운전모드전환
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvY5EqpDrvMdTurnov(JDTORecord inRecord) throws JDTOException  {
		//
		// YD-UC-????
		// TC : Y5YDL003
		// C열연코일야드L2시스템으로부터 설비운전모드전환 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY5EqpDrvMdTurnov";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procY5EqpDrvMdTurnov", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg="Y5설비운전모드전환 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvY5EqpDrvMdTurnov()


	/**
	 * 오퍼레이션명 : Y5설비고장복구실적
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvY5EqpTrblRcvrWr(JDTORecord inRecord) throws JDTOException  {
		//
		// YD-UC-????
		// TC : Y5YDL004
		// C열연코일야드L2시스템으로부터 설비고장복구실적 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY5EqpTrblRcvrWr";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procY5EqpTrblRcvrWr", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "Y5설비고장복구실적 처리(" + szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvY5EqpTrblRcvrWr()


	/**
	 * 오퍼레이션명 : Y5크레인현재위치
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvY5CrnCurrLoc(JDTORecord inRecord) throws JDTOException  {
		// TKVLOC
		// YD-UC-????
		// TC : Y5YDL005
		// C열연코일야드L2시스템으로부터 크레인현재위치 수신
		//
		String szMsg        = "";
		String szMethodName = "rcvY5CrnCurrLoc";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procY5CrnCurrLoc", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try catch

		szMsg = "Y5크레인현재위치 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvY5CrnCurrLoc()


	/**
	 * 오퍼레이션명 : HandScarfing작업진행정보 (C3YDL011)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws JDTOException
	 */
	public void rcvHandScarfingWrkProgInfo(JDTORecord inRecord) throws JDTOException  {
		// TKOVLOC
		// YD-UC-????
		// TC : C3YDL011
		// HandScarfing작업진행정보 (C3YDL011)
		//
		String szMethodName = "rcvHandScarfingWrkProgInfo";
		String szMsg        = "";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try {
			ydEjbCon.trx("EqpTrackingSeEJB", "procHandScarfingWrkProgInfo", inRecord);
		} catch (Exception e) {
			szMsg = szMethodName + "() " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}

		szMsg = "HandScarfing작업진행정보 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of rcvHandScarfingWrkProgInfo()


	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 설비기준관리 조회 (화면:설비기준관리)
	 *
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException
	 * @작성자 : hun
	 * @작성일 : 2015.08.31
	 */
	public GridData getEqpMgtList(GridData inDto) throws JDTOException {


		String szMethodName     = "getEqpMgtList";
		String szLogMsg = "";

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{

			szLogMsg = "JSP-FACADE [설비 기준 조회 (화면:설비기준관리)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "EqpTrackingSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getEqpMgtList",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");

		szLogMsg = "JSP-FACADE [설비기준 조회 (화면:스케줄기준관리)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);


		return gdRes;
	}  // End Of getSchRuleMgtList


  //---------------------------------------------------------------------------
} // end of class

