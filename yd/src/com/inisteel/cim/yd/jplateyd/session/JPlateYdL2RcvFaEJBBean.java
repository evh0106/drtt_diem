/*
 * @(#) 2후판정정야드 L2수신 처리 Facade Session EJB클래스
 *
 * @version         V1.00
 * @author          김현우
 * @date            2012/11/14
 *
 * @description     2후판정정야드 L2수신 처리 Facade Session EJB클래스
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/14   김현우      김현우       최초작성
 * V1.01  2024.11.15               1후판 정정 2열처리 Book-In/Book-Out Request(P8YDL501) 처리 
 *                                 rcvP8BookInOutReq Method 추가   
 */                                

package com.inisteel.cim.yd.jplateyd.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.jplateyd.delegate.JPlateYdDelegate;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;

import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.15 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 * 2후판정정야드 L2수신 처리 Facade Session EJB
 *
 * @ejb.bean name="JPlateYdL2RcvFaEJB" jndi-name="JPlateYdL2RcvFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class JPlateYdL2RcvFaEJBBean extends BaseSessionBean {

    private static final long serialVersionUID = 1L;

    // Session Name
    private static final String SZ_SESSION_NAME = JPlateYdL2RcvFaEJBBean.class.getName();

    private JPlateYdUtils       ydUtils     = new JPlateYdUtils();
    private EJBConnector        ydEjbCon    = new EJBConnector("default", this);
    private JPlateYdDelegate    ydDelegate  = new JPlateYdDelegate();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.15 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
    private YdUtils 			ydLogUtils  = new YdUtils();
    
    /**
     * ejbCrate()
     *
     * @throws javax.ejb.CreateException
     */
    public void ejbCreate() throws javax.ejb.CreateException {
    }

    /**
     * 오퍼레이션명 : Y7 저장위치제원요구 [Y7YDL001]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY7StrLocSpecReq(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y7YDL001
        // 후판정정야드 L2시스템으로부터 저장위치제원요구 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY7StrLocSpecReq";

        szLogMsg = "L2RcvFaEJB - Y7 저장위치제원요구 처리(" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY7StrLocSpecReq", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y7 저장위치제원요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvY7StrLocSpecReq()


    /**
     * 오퍼레이션명 : Y7 저장품제원요구 [Y7YDL002]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY7StockSpecReq(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y7YDL002
        // 후판정정야드 L2시스템으로부터 저장품제원요구 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY7StockSpecReq";

        szLogMsg = "L2RcvFaEJB - Y7 저장품제원요구 처리(" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY7StockSpecReq", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y7 저장품제원요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvY7StockSpecReq()

    /**
     * 오퍼레이션명 : Y7 설비운전모드전환 [Y7YDL003]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY7EqpDrvModeChg(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y7YDL003
        // 후판정정야드 L2시스템으로부터 설비운전모드전환 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY7EqpDrvModeChg";

        szLogMsg = "L2RcvFaEJB - Y7 설비운전모드전환 처리(" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY7EqpDrvModeChg", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y7 설비운전모드전환 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvY7EqpDrvModeChg()

    /**
     * 오퍼레이션명 : Y7 설비고장복구실적 [Y7YDL004]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY7EqpTrblRcvrWr(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y7YDL004
        // 후판정정야드 L2시스템으로부터 설비고장복구실적 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY7EqpTrblRcvrWr";

        szLogMsg = "L2RcvFaEJB - Y7 설비고장복구실적 처리(" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY7EqpTrblRcvrWr", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y7 설비고장복구실적 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvY7EqpTrblRcvrWr()

    /**
     * 오퍼레이션명 : Y7 크레인작업지시요구 [Y7YDL007]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY7CrnWrkOrdReq(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y7YDL007
        // 후판정정야드 L2시스템으로부터 크레인작업지시요구 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY7CrnWrkOrdReq";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.12 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(inRecord, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
               
        szLogMsg = "L2RcvFaEJB - Y7 크레인작업지시요구 처리(" + szMethodName + ") 시작";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY7CrnWrkOrdReq", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y7 크레인작업지시요구 처리(" + szMethodName + ") 완료";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
    } // end of rcvY7CrnWrkOrdReq()

    /**
     * 오퍼레이션명 : Y7 ON-LINE권상실적 [Y7YDL008]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY7CrnUpWr(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y7YDL008
        // 후판정정야드 L2시스템으로부터 ON-LINE권상실적 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY7CrnUpWr";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.13 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(inRecord, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
                           
        szLogMsg = "L2RcvFaEJB - Y7 ON-LINE권상실적 처리(" + szMethodName + ") 시작";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY7CrnUpWr", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y7 ON-LINE권상실적 처리(" + szMethodName + ") 완료";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
    } // end of rcvY7CrnUpWr()

    /**
     * 오퍼레이션명 : Y7 ON-LINE권하실적 [Y7YDL009]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY7CrnDownWr(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y7YDL009
        // 후판정정야드 L2시스템으로부터 ON-LINE권하실적 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY7CrnDownWr";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.13 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(inRecord, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
                                 
        szLogMsg = "L2RcvFaEJB - Y7 ON-LINE권하실적 처리(" + szMethodName + ") 시작";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY7CrnDownWr", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y7 ON-LINE권하실적 처리(" + szMethodName + ") 완료";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
    } // end of rcvY7CrnDownWr()

    /**
     * 오퍼레이션명 : Y7 강제권상요구 [Y7YDL010]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY7OffCrnUpWr(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y7YDL010
        // 후판정정야드 L2시스템으로부터 강제권상요구 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY7OffCrnUpWr";

        szLogMsg = "L2RcvFaEJB - Y7 강제권상요구 처리(" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY7OffCrnUpWr", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y7 강제권상요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvY7OffCrnUpWr()

    /**
     * 오퍼레이션명 : Y7 강제권하요구 [Y7YDL011]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY7OffCrnDownWr(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y7YDL011
        // 후판정정야드 L2시스템으로부터 강제권하요구 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY7OffCrnDownWr";

        szLogMsg = "L2RcvFaEJB - Y7 강제권하요구 처리(" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY7OffCrnDnWr", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y7 강제권하요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvY7OffCrnDownWr()

    /**
     * 오퍼레이션명 : Y7 크레인명령선택 [Y7YDL012]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY7CrnOrderSel(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y7YDL011
        // 후판정정야드 L2시스템으로부터 크레인명령선택 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY7CrnOrderSel";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.13 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(inRecord, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
                     
        szLogMsg = "L2RcvFaEJB - Y7 크레인명령선택 처리(" + szMethodName + ") 시작";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY7CrnOrderSel", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y7 크레인명령선택 처리(" + szMethodName + ") 완료";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
    } // end of rcvY7CrnOrderSel()

    /**
     * 오퍼레이션명 : Y7 2후판정정야드 파일링실적 [Y7YDL013]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY7PilingRslt(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y7YDL011
        // 후판정정야드 L2시스템으로부터 파일링실적 수신
        //
        String  szLogMsg        = "";
        String  szSendMsg       = "";
        String  szRtnMsg        = "";
        String  szMethodName    = "rcvY7PilingRslt";
        String  szOperationName = "2후판정정야드 파일링실적 수신";

        JDTORecord recInTemp    = null;

        szLogMsg = "L2RcvFaEJB - Y7 파일링실적 처리(" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {
            szRtnMsg = (String)ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY7PilingRslt", inRecord);

            // 파일링실적 비정상 완료시 메시지 전송
            if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {

                szLogMsg = "[" + szOperationName + "] 파일링실적 비정상 완료 >>>>"+szRtnMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

                /*
                YD_EQP_ID           야드설비ID          CHAR    6
                YD_PILING_SH        파일링매수          NUMBER  2
                YD_PILING_GP        파일링구분          CHAR    1
                YD_CRN_SCH_ID1      야드크레인스케쥴ID1 CHAR    18
                STL_NO1             재료번호1               CHAR    11
                YD_UP_WO_LAYER1     야드권상지시단1     CHAR    3       파일링단
                YD_CRN_SCH_ID2      야드크레인스케쥴ID2 CHAR    18
                                    :
                STL_NO15            재료번호15          CHAR    11
                YD_UP_WO_LAYER15    야드권상지시단15        CHAR    3
                */

                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                 * 2후판정정 야드L2 크레인작업실적응답 전송  - YDY7L005
                 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                recInTemp = JDTORecordFactory.getInstance().create();
                recInTemp.setField("MSG_ID",            "YDY7L005");
                recInTemp.setField("YD_EQP_ID",         inRecord.getFieldString("YD_EQP_ID"));          //야드설비ID
                recInTemp.setField("YD_WRK_PROG_STAT",  JPlateYdConst.YD_EQP_STAT_UP_WO);               //야드작업진행상태
                recInTemp.setField("YD_SCH_CD",         "");                                            //야드스케줄코드
                recInTemp.setField("YD_CRN_SCH_ID",     "");                                            //야드크레인스케줄ID
                recInTemp.setField("YD_L2_WR_GP",       JPlateYdConst.CRN_WRK_RE_LD_WR);                //야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
                recInTemp.setField("YD_L3_HD_RS_CD",    JPlateYdConst.CRN_WRK_RE_CD_ERROR);             //야드L3처리결과코드
                recInTemp.setField("YD_L3_MSG",         ydUtils.substr(szRtnMsg, 0, 40));               //야드L3처리결과메시지

                szSendMsg = ydDelegate.sendMsg(recInTemp);

                szLogMsg = "[" + szOperationName + "] 2후판정정야드L2 크레인작업실적응답[YDY7L005] 전송 완료>>>>"+szSendMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

                m_ctx.setRollbackOnly();

            } else {

                szLogMsg = "[" + szOperationName + "] 파일링실적 정상 완료 >>>>"+szRtnMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

            }

        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y7 파일링실적 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvY7PilingRslt()

    /**
     * 오퍼레이션명 : S1 2후판전단L2 Book-In/Book-Out요구 수신 [S1YDL013]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvS1BookInOutReq(JDTORecord inRecord) throws DAOException  {
        //
        // TC : S1YDL013
        // 2후판전단 L2시스템으로부터 Book-In/Book-Out요구 수신
        //
        String  szLogMsg        = "";
        String  szRtnMsg        = JPlateYdConst.RETN_CD_SUCCESS;
        String  szMethodName    = "rcvS1BookInOutReq";
        String  szOperationName = "2후판전단L2 Book-In/Book-Out요구 수신";

        szLogMsg = "L2RcvFaEJB - S1 Book-In/Book-Out요구 수신 처리 (" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {
            szRtnMsg = (String)ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procS1BookInOutReq", inRecord);

            if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
                szLogMsg = szOperationName + "[" + szMethodName + "] S1YDL013 수신처리 .. 실행 실패 >>>> " + szRtnMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);

                m_ctx.setRollbackOnly();

                throw new DAOException(szRtnMsg);
            }

        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - S1 Book-In/Book-Out요구 수신 처리 (" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvS1BookInOutReq()

    //---------------------------------------------------------------------------

    /**********************************************************
    * 1후판정정추가 SJH16
    **********************************************************/



    /**
     * 오퍼레이션명 : Y2 1후판정정 저장위치제원요구 [Y2YDL001]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY2StrLocSpecReq(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y2YDL001
        // 후판정정야드 L2시스템으로부터 저장위치제원요구 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY2StrLocSpecReq";

        szLogMsg = "L2RcvFaEJB - Y2 저장위치제원요구 처리(" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2StrLocSpecReq", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y2 저장위치제원요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvY2StrLocSpecReq()


    /**
     * 오퍼레이션명 : Y2 1후판정정 저장품제원요구 [Y2YDL002]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY2StockSpecReq(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y2YDL002
        // 후판정정야드 L2시스템으로부터 저장품제원요구 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY2StockSpecReq";

        szLogMsg = "L2RcvFaEJB - Y2 저장품제원요구 처리(" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2StockSpecReq", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y2 저장품제원요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvY2StockSpecReq()

    /**
     * 오퍼레이션명 : Y2 1후판정정 설비운전모드전환 [Y2YDL003]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY2EqpDrvModeChg(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y2YDL003
        // 후판정정야드 L2시스템으로부터 설비운전모드전환 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY2EqpDrvModeChg";

        szLogMsg = "L2RcvFaEJB - Y2 설비운전모드전환 처리(" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2EqpDrvModeChg", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y2 설비운전모드전환 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvY2EqpDrvModeChg()

    /**
     * 오퍼레이션명 : Y2 1후판정정 설비고장복구실적 [Y2YDL004]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY2EqpTrblRcvrWr(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y2YDL004
        // 후판정정야드 L2시스템으로부터 설비고장복구실적 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY2EqpTrblRcvrWr";

        szLogMsg = "L2RcvFaEJB - Y2 설비고장복구실적 처리(" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2EqpTrblRcvrWr", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y2 설비고장복구실적 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvY2EqpTrblRcvrWr()

    /**
     * 오퍼레이션명 : Y2 1후판정정 크레인작업지시요구 [Y2YDL007]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY2CrnWrkOrdReq(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y2YDL007
        // 후판정정야드 L2시스템으로부터 크레인작업지시요구 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY2CrnWrkOrdReq";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.12 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(inRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
         
        szLogMsg = "L2RcvFaEJB - Y2 크레인작업지시요구 처리(" + szMethodName + ") 시작";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            return ;
        }

        try {


            String sNEW_MODULE_EFF_YN = "N";

            JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
            sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A010"); // 1후판정정야드  작업지시요구 (Y2YDL007) 시 YDY2L004V2 전송여부

            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(Y2YDL007)---[[[ 1후판정정야드신규적용 : " + sNEW_MODULE_EFF_YN + " ]]]---", JPlateYdConst.DEBUG, logId);

            if(sNEW_MODULE_EFF_YN.equals("Y")) {
                // 신규 메소드 호출
				ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2CrnWrkOrdReq2", inRecord);
            } else {
                // 기존 메소드 호출
                ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2CrnWrkOrdReq", inRecord);
            }

        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y2 크레인작업지시요구 처리(" + szMethodName + ") 완료";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
    } // end of rcvY2CrnWrkOrdReq()

    /**
     * 오퍼레이션명 : Y2 1후판정정 ON-LINE권상실적 [Y2YDL008]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY2CrnUpWr(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y2YDL008
        // 후판정정야드 L2시스템으로부터 ON-LINE권상실적 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY2CrnUpWr";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.05 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(inRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
        
        szLogMsg = "L2RcvFaEJB - Y2 ON-LINE권상실적 처리(" + szMethodName + ") 시작";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            return ;
        }

        try {
        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.05 inRecord에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
        	inRecord.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
        	
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2CrnUpWr", inRecord);
            
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y2 ON-LINE권상실적 처리(" + szMethodName + ") 완료";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
    } // end of rcvY2CrnUpWr()

    /**
     * 오퍼레이션명 : Y2 1후판정정 ON-LINE권하실적 [Y2YDL009]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY2CrnDownWr(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y2YDL009
        // 후판정정야드 L2시스템으로부터 ON-LINE권하실적 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY2CrnDownWr";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.05 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(inRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
              
        szLogMsg = "L2RcvFaEJB - Y2 ON-LINE권하실적 처리(" + szMethodName + ") 시작";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            return ;
        }

        try {

            //권하실적 처리 호출
            String  szRtnMsg = (String)ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2CrnDownWr", inRecord);

            if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
                //권하실적 처리가 정상적으로 종료 되었을 경우..

                String szYdDnWrLoc = ydUtils.substr(inRecord.getFieldString("YD_DN_WR_LOC"), 0, 6);

                if("TR".equals(ydUtils.substr(szYdDnWrLoc,2,2))) {
                    //차량위치에 권하 한 경우만..

                    String sNEW_MODULE_EFF_YN = "N";

                    JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
                    sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A031"); //1후판정정야드 빈차량포인트에 권하시 사외이송재 삭제 여부

                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(A031)---[[[ 1후판정정야드 빈차량포인트에 권하시 사외이송재 삭제 여부 : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG, logId);

                    if(sNEW_MODULE_EFF_YN.equals("Y")) {
                        //1후판정정야드 빈차량포인트에 권하시 사외이송재 삭제 여부 기준이 Y 일 경우..

                        JPlateYdCommDAO  commDao    = new JPlateYdCommDAO();
                        
// 2024.12.06 신규 logId 사용                         
//                      String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
                        JDTORecord jrParam      = JDTORecordFactory.getInstance().create();
                        JDTORecordSet rsResult  = null;

                        jrParam.setField("YD_STK_COL_GP", szYdDnWrLoc);
                        rsResult    = commDao.select(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdStatStkColInfo", logId, szMethodName, " 적치열 정보 조회 - 차량 존재 여부");

                        if(rsResult.size() > 0) {

                            if( "".equals(rsResult.getRecord(0).getFieldString("TC_TR_INFO")) ) {
                                //권하한 위치에 차량이 없는 경우 는 사외 이송재로 간주하고 저장위치삭제를 실행 한다.

                                String szSTL_NO = null;

                                for(int ii=0; ii<15; ii++) {

                                    szSTL_NO = inRecord.getFieldString("STL_NO" + (ii+1));

                                    if(szSTL_NO == null || "".equals(szSTL_NO)) {
                                        break;
                                    }

                                    jrParam.setField("MODIFIER"             , "Y2YDL009");
                                    jrParam.setField("STL_NO"               , szSTL_NO );

                                    szRtnMsg = (String)ydEjbCon.trx("JPlateYdYdPJspSeEJB", "delYdLocInfo2", jrParam);
                                }

                                szLogMsg = szMethodName + " : 권하실적 사외이송재 삭제처리 완료";
                                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);

                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y2 ON-LINE권하실적 처리(" + szMethodName + ") 완료";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
    } // end of rcvY2CrnDownWr()

    /**
     * 오퍼레이션명 : Y2 1후판정정 강제권상요구 [Y2YDL010]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY2OffCrnUpWr(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y2YDL010
        // 후판정정야드 L2시스템으로부터 강제권상요구 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY2OffCrnUpWr";

        szLogMsg = "L2RcvFaEJB - Y2 강제권상요구 처리(" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2OffCrnUpWr", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y2 강제권상요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvY2OffCrnUpWr()

    /**
     * 오퍼레이션명 : Y2 1후판정정 강제권하요구 [Y2YDL011]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY2OffCrnDownWr(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y2YDL011
        // 후판정정야드 L2시스템으로부터 강제권하요구 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY2OffCrnDownWr";

        szLogMsg = "L2RcvFaEJB - Y2 강제권하요구 처리(" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2OffCrnDnWr", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y2 강제권하요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvY2OffCrnDownWr()

    /**
     * 오퍼레이션명 : Y2 1후판정정 크레인명령선택 [Y2YDL012]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY2CrnOrderSel(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y2YDL012
        // 후판정정야드 L2시스템으로부터 크레인명령선택 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY2CrnOrderSel";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(inRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
              
        szLogMsg = "L2RcvFaEJB - Y2 크레인명령선택 처리(" + szMethodName + ") 시작";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            return ;
        }

        try {
        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 inRecord에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
        	inRecord.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
        	
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2CrnOrderSel", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y2 크레인명령선택 처리(" + szMethodName + ") 완료";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
    } // end of rcvY2CrnOrderSel()

    /**
     * 오퍼레이션명 : Y2 1후판정정 파일링실적 [Y2YDL013]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY2PilingRslt(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y2YDL011
        // 후판정정야드 L2시스템으로부터 파일링실적 수신
        //
        String  szLogMsg        = "";
        String  szSendMsg       = "";
        String  szRtnMsg        = "";
        String  szMethodName    = "rcvY2PilingRslt";
        String  szOperationName = "1후판정정 파일링실적 수신";

        JDTORecord recInTemp    = null;

        szLogMsg = "L2RcvFaEJB - Y2 파일링실적 처리(" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {

            String szYdPilingGp = inRecord.getFieldString("YD_PILING_GP");  // 파일링구분 (P:파일링, H:횡행작업, M:멀티작업, R : RT상에서 파일링)

            if("R".equals(szYdPilingGp)) {
                //RT 상에서 파일링(2매) 후  권상처리
                szRtnMsg = (String)ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2PilingRsltR", inRecord);

            } else {

                String sNEW_MODULE_EFF_YN = "N";

                JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
                sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A002"); //1후판정정야드 파일링실적(Y2YDL013) 신규모듈적용여부

                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(Y2YDL013)---[[[ 1후판정정야드신규적용 : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);

                if(sNEW_MODULE_EFF_YN.equals("Y")) {
                    //신규 메소드 호출
                    szRtnMsg = (String)ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2PilingRslt2", inRecord);

                } else {
                    //기존 메소드 호출
                    szRtnMsg = (String)ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2PilingRslt", inRecord);
                }
            }

            // 파일링실적 비정상 완료시 메시지 전송
            if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {

                szLogMsg = "[" + szOperationName + "] 파일링실적 비정상 완료 >>>>"+szRtnMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

                /*
                YD_EQP_ID           야드설비ID          CHAR    6
                YD_PILING_SH        파일링매수          NUMBER  2
                YD_PILING_GP        파일링구분          CHAR    1
                YD_CRN_SCH_ID1      야드크레인스케쥴ID1 CHAR    18
                STL_NO1             재료번호1               CHAR    11
                YD_UP_WO_LAYER1     야드권상지시단1     CHAR    3       파일링단
                YD_CRN_SCH_ID2      야드크레인스케쥴ID2 CHAR    18
                                    :
                STL_NO15            재료번호15          CHAR    11
                YD_UP_WO_LAYER15    야드권상지시단15        CHAR    3
                */

                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                 * 1후판정정 야드L2 크레인작업실적응답 전송  - YDY2L005
                 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                recInTemp = JDTORecordFactory.getInstance().create();
                recInTemp.setField("MSG_ID",            "YDY2L005");
                recInTemp.setField("YD_EQP_ID",         inRecord.getFieldString("YD_EQP_ID"));          //야드설비ID
                recInTemp.setField("YD_WRK_PROG_STAT",  JPlateYdConst.YD_EQP_STAT_UP_WO);               //야드작업진행상태
                recInTemp.setField("YD_SCH_CD",         "");                                            //야드스케줄코드
                recInTemp.setField("YD_CRN_SCH_ID",     "");                                            //야드크레인스케줄ID
                recInTemp.setField("YD_L2_WR_GP",       JPlateYdConst.CRN_WRK_RE_LD_WR);                //야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
                recInTemp.setField("YD_L3_HD_RS_CD",    JPlateYdConst.CRN_WRK_RE_CD_ERROR);             //야드L3처리결과코드
                recInTemp.setField("YD_L3_MSG",         ydUtils.substr(szRtnMsg, 0, 40));               //야드L3처리결과메시지

                szSendMsg = ydDelegate.sendMsg(recInTemp);

                szLogMsg = "[" + szOperationName + "] 1후판정정야드L2 크레인작업실적응답[YDY2L005] 전송 완료>>>>"+szSendMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

                m_ctx.setRollbackOnly();

            } else {

                szLogMsg = "[" + szOperationName + "] 파일링실적 정상 완료 >>>>"+szRtnMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

            }

        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y2 파일링실적 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvY2PilingRslt()


    /**
     * 오퍼레이션명 : Y2 1후판정정  Book-In/Book-Out 실적-[Y2YDL014]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY2BookInOutRslt(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y2YDL014
        // 1후판정정  Book-In/Book-Out 실적 기존 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY2BookInOutRslt";

        szLogMsg = "L2RcvFaEJB - Y2 Book-In/Book-Out 실적 처리(" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {

            String sNEW_MODULE_EFF_YN = "N";

            JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
            sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A003"); //1후판정정야드  Y2YDL014 수신 시 크레인스케줄 작업취소 여부

            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(Y2YDL014)---[[[ 1후판정정야드  Y2YDL014 수신 시 크레인스케줄 작업취소 여부 : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);

            if(sNEW_MODULE_EFF_YN.equals("Y")) {
                //크레인스케줄 삭제처리 (기존 rcvPRYDJ016 호출)
                inRecord.setField("EQP_GP"  , "02");
                inRecord.setField("MODIFIER", "Y2YDJ014");
                ydEjbCon.trx("JPlateYdL3RcvFaEJB", "rcvPRYDJ016", inRecord);
            }

            //신규 메소드 호출
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2BookInOutRslt2", inRecord);

            //기존 메소드 호출
            //ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2BookInOutRslt", inRecord);

        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y2 Book-In/Book-Out 실적  처리(" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvY2BookInOutRslt()



    /**
     * 오퍼레이션명 : P2 1후판전단L2 Book-In/Book-Out요구 수신 [P2YDL501] //TY3ABR
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvP2BookInOutReq(JDTORecord inRecord) throws DAOException  {
        //
        // TC : P2YDL501
        // 1후판전단 L2시스템으로부터 Book-In/Book-Out요구 수신
        //
        String  szLogMsg        = "";
        String  szRtnMsg        = JPlateYdConst.RETN_CD_SUCCESS;
        String  szMethodName    = "rcvP2BookInOutReq";
        String  szOperationName = "1후판 전단L2 Book-In/Book-Out요구 수신";

        szLogMsg = "L2RcvFaEJB - P2 Book-In/Book-Out요구 수신 처리 (" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        try {

            //SMS '24752' ->  'P2YDL501' 강제로 변환처리 함
            inRecord.setField("MSG_ID",         "P2YDL501");

            if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
                szLogMsg = szMethodName + "() 실행 실패";
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
                return ;
            }


            String sNEW_MODULE_EFF_YN = "N";

            JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
            sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A004"); //1후판정정야드 전단L2 Book-In/Book-Out요구 (P2YDL501) 신규 모듈 적용여부

            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(P2YDL501)---[[[ 1후판정정야드신규적용 : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);

            if(sNEW_MODULE_EFF_YN.equals("Y")) {
                //신규 메소드 호출
                szRtnMsg = (String)ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procP2P3BookInOutReq2", inRecord);
            } else {
                //기존 메소드 호출
                szRtnMsg = (String)ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procP2P3BookInOutReq", inRecord);
            }

            if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
                szLogMsg = szOperationName + "[" + szMethodName + "] P2YDL501 수신처리 .. 실행 실패 >>>> " + szRtnMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);

                m_ctx.setRollbackOnly();

                throw new DAOException(szRtnMsg);
            }

        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - P2 Book-In/Book-Out요구 수신 처리 (" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvP2BookInOutReq()

    /**
     * 오퍼레이션명 : P3 1후판열처리L2 Book-In/Book-Out요구 수신 [P3YDL501]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvP3BookInOutReq(JDTORecord inRecord) throws DAOException  {
        //
        // TC : P3YDL501
        // 1후판열처리 L2시스템으로부터 Book-In/Book-Out요구 수신
        //
        String  szLogMsg        = "";
        String  szRtnMsg        = JPlateYdConst.RETN_CD_SUCCESS;
        String  szMethodName    = "rcvP3BookInOutReq";
        String  szOperationName = "1후판 열처리 L2 Book-In/Book-Out요구 수신";

        szLogMsg = "L2RcvFaEJB - P3 Book-In/Book-Out요구 수신 처리 (" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {

            String sNEW_MODULE_EFF_YN = "N";

            JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
            sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A005"); //1후판정정야드 열처리L2 Book-In/Book-Out요구 (P3YDL501) 신규 모듈 적용여부

            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(P3YDL501)---[[[ 1후판정정야드신규적용 : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);

            if(sNEW_MODULE_EFF_YN.equals("Y")) {
                //신규 메소드 호출
                szRtnMsg = (String)ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procP2P3BookInOutReq2", inRecord);
            } else {
                //기존 메소드 호출
                szRtnMsg = (String)ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procP2P3BookInOutReq", inRecord);
            }

            if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
                szLogMsg = szOperationName + "[" + szMethodName + "] P3YDL501 수신처리 .. 실행 실패 >>>> " + szRtnMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);

                m_ctx.setRollbackOnly();

                throw new DAOException(szRtnMsg);
            }

        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - P3 Book-In/Book-Out요구 수신 처리 (" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvP3BookInOutReq()

    //---------------------------------------------------------------------------

    /**
     * 오퍼레이션명 : Y2 후판L2제품번호요구 수신 [Y2YDL015]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY2YDL015(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y2YDL015
        // 후판정정야드 L2시스템으로부터 후판L2제품번호요구 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY2YDL015";

        szLogMsg = "L2RcvFaEJB - Y2후판L2제품번호요구 수신 처리(" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2YDL015", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y2후판L2제품번호요구 수신 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvY2YDL015

    /**
     * 오퍼레이션명 : Y2 저장위치제원정보 수신 [Y2YDL016]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY2YDL016(JDTORecord inRecord) throws DAOException  {
        //
        // TC : Y2YDL016
        // 후판정정야드 L2시스템으로부터 저장위치제원정보 수신
        //
        String szLogMsg     = "";
        String szMethodName = "rcvY2YDL016";

        szLogMsg = "L2RcvFaEJB - Y2저장위치제원정보 수신 처리(" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procY2YDL016", inRecord);
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - Y2저장위치제원정보 수신 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvY2YDL016

    /**
     * 오퍼레이션명 : P2 1후판압연 L2 Book-In/Book-Out요구 수신 [P2YDL601]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvP2YDL601(JDTORecord inRecord) throws DAOException  {
        //
        // TC : P2YDL601
        // 1후판압연 L2시스템으로부터 Book-In/Book-Out요구 수신
        //
        String  szLogMsg        = "";
        String  szRtnMsg        = JPlateYdConst.RETN_CD_SUCCESS;
        String  szMethodName    = "rcvP2YDL601";
        String  szOperationName = "1후판 압연L2 Book-In/Book-Out요구 수신";

        szLogMsg = "L2RcvFaEJB - P2 압연L2 Book-In/Book-Out요구 수신 처리 (" + szMethodName + ") 시작";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        try {

            //SMS '29782' ->  'P2YDL601' 강제로 변환처리 함
            inRecord.setField("MSG_ID",         "P2YDL601");

            if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
                szLogMsg = szMethodName + "() 실행 실패";
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
                return ;
            }


            String sNEW_MODULE_EFF_YN = "N";

            JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
            sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A004"); //1후판정정야드 전단L2 Book-In/Book-Out요구 (P2YDL501) 신규 모듈 적용여부

            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(P2YDL501)---[[[ 1후판정정야드신규적용 : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);

            if(sNEW_MODULE_EFF_YN.equals("Y")) {
                //신규 메소드 호출
                szRtnMsg = (String)ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procP2P3BookInOutReq2", inRecord);
            } else {
                //기존 메소드 호출
                szRtnMsg = (String)ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procP2P3BookInOutReq", inRecord);
            }

            if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
                szLogMsg = szOperationName + "[" + szMethodName + "] P2YDL601 수신처리 .. 실행 실패 >>>> " + szRtnMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);

                m_ctx.setRollbackOnly();

                throw new DAOException(szRtnMsg);
            }

        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - P2 압연L2 Book-In/Book-Out요구 수신 처리 (" + szMethodName + ") 완료";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    } // end of rcvP2YDL601()


//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.15 1후판 정정 2열처리 Book-In/Book-Out 요구 전문 추가
//-------------------------------------------------------------------------------------------------------------------------

    /**
     * 오퍼레이션명 : P8 1후판 2열처리 L2 Book-In/Book-Out요구 수신 [P8YDL501]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param JDTORecord inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvP8BookInOutReq(JDTORecord inRecord) throws DAOException  {
        //
        // TC : P8YDL501
        // 1후판 2열처리 L2시스템으로부터 Book-In/Book-Out 요구 수신
        //
        String  szLogMsg        = "";
        String  szRtnMsg        = JPlateYdConst.RETN_CD_SUCCESS;
        String  szMethodName    = "rcvP8BookInOutReq";
        String  szOperationName = "1후판 #2 열처리 L2 Book-In/Book-Out요구 수신";

        String logId            = ydLogUtils.getJDTOLogId(inRecord, "P");      // JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");     // log id 가 비어있는경우 새로 1후판 정정 log id 새로 발번


        szLogMsg = "L2RcvFaEJB - P8 Book-In/Book-Out요구 수신 처리 (" + szMethodName + ") 시작";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

        if(!ydUtils.rcvMsgChk(inRecord, SZ_SESSION_NAME, szMethodName)){
            szLogMsg = szMethodName + "() 실행 실패";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            return ;
        }

        try {
        	
            szRtnMsg = (String)ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procP8BookInOutReq", inRecord);

            if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
                szLogMsg = szOperationName + "[" + szMethodName + "] P8YDL501 수신처리 .. 실행 실패 >>>> " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);

                m_ctx.setRollbackOnly();

                throw new DAOException(szRtnMsg);
            }
            
        } catch (Exception e) {
            szLogMsg = szMethodName + "() " + e.getMessage();
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
            throw new DAOException(szLogMsg);
        } // end of try catch

        szLogMsg = "L2RcvFaEJB - P8 Book-In/Book-Out요구 수신 처리 (" + szMethodName + ") 완료";
        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
    } // end of rcvP8BookInOutReq()


}
