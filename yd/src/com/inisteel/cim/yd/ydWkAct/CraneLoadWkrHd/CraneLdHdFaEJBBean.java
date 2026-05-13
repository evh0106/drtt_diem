package com.inisteel.cim.yd.ydWkAct.CraneLoadWkrHd;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import com.inisteel.cim.common.exception.DAOException;
import jspeed.base.record.JDTORecord;

import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdConstant;



/**
 * 권상실적처리 Facade Session EJB 
 * 
 * @ejb.bean name="CraneLdHdFaEJB" jndi-name="CraneLdHdFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CraneLdHdFaEJBBean extends BaseSessionBean {

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
     * 오퍼레이션명 : Y0크레인작업지시요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     * @weblogic.transaction-descriptor trans-timeout-seconds="180"
     */
    public void rcvY0CrnWrkOrdReq(JDTORecord inRecord) throws DAOException  {
    //
    // YD-UC-5101 C연주크레인작업지시요구수신
    // TC : Y0YDL007,
    // C연주슬라브야드L2시스템으로부터 크레인작업지시요구 수신
    //
    //┏━┓
    //┃ C연주슬라브야드 L2에서 크레인작업지시를 요구를 수신하여
    //┃    크레인의 현재 스케줄을 작업지시시하는 기능
    //┗━┛

        String szMsg="";
        String szMethodName="rcvY0CrnWrkOrdReq";

        if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

            szMsg= szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;

        }



        try {


            ydEjbCon.trx("CraneLdHdSeEJB", "procY0CrnWrkOrdReq", inRecord);

        } catch (Exception e) {
            szMsg =szMethodName + "() " +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);

        } // end of try catch



        szMsg="Y0크레인작업지시요구 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


    } // end of rcvY0CrnWrkOrdReq()








    /**
     * 오퍼레이션명 : Y0크레인권상실적
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     * @weblogic.transaction-descriptor trans-timeout-seconds="180"
     */
    public void rcvY0CrnLdWr(JDTORecord inRecord) throws DAOException  {
    //
    // YD-UC-5102 Y0크레인권상실적
    // TC : Y0YDL008, YDYDJ600
    // C연주슬라브야드L2시스템으로부터 크레인권상실적 수신
    //
    //┏━┓
    //┃ C연주슬라브야드 L2에서 크레인 권상처리 결과를 수신하여
    //┃ 권상실적을 등록
    //┗━┛



        String szMsg="";
        String szMethodName="rcvY0CrnLdWr";

        if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

            szMsg= szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;

        }


        try {

            // 권상실적처리 요청
            ydEjbCon.trx("CraneLdHdSeEJB", "procY0CrnLdWr", inRecord);

        } catch (Exception e) {
            szMsg =szMethodName + "() " +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);

        } // end of try catch


        szMsg="Y0크레인권상실적 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

    } // end of rcvY0CrnLdWr()









    /**
     * 오퍼레이션명 : Y1크레인작업지시요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     * @weblogic.transaction-descriptor trans-timeout-seconds="180"
     */
    public void rcvY1CrnWrkOrdReq(JDTORecord inRecord) throws DAOException  {
    //
    // YD-UC-5101 C연주크레인작업지시요구수신
    // TC : Y1YDL007,
    // C연주슬라브야드L2시스템으로부터 크레인작업지시요구 수신
    //
    //┏━┓
    //┃ C연주슬라브야드 L2에서 크레인작업지시를 요구를 수신하여
    //┃    크레인의 현재 스케줄을 작업지시시하는 기능
    //┗━┛

        String szMethodName = "rcvY1CrnWrkOrdReq";
        String szMsg        = "";

        if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

            szMsg= szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;

        }



        try {


            ydEjbCon.trx("CraneLdHdSeEJB", "procY1CrnWrkOrdReq", inRecord);

        } catch (Exception e) {
            szMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);

        } // end of try catch



        szMsg = "Y1크레인작업지시요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


    } // end of rcvY1CrnWrkOrdReq()




    /**
     * 오퍼레이션명 : Y1크레인권상실적 (Y1YDL008)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     * @weblogic.transaction-descriptor trans-timeout-seconds="180"
     */
    public void rcvY1CrnLdWr(JDTORecord inRecord) throws DAOException  {
    //
    // YD-UC-5102 Y1크레인권상실적
    // TC : Y1YDL008, YDYDJ600
    // C연주슬라브야드L2시스템으로부터 크레인권상실적 수신
    //
    //┏━┓
    //┃ C연주슬라브야드 L2에서 크레인 권상처리 결과를 수신하여
    //┃ 권상실적을 등록
    //┗━┛



        String szMsg="";
        String szMethodName="rcvY1CrnLdWr";

        if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

            szMsg= szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;

        }


        try {

            // 권상실적처리 요청
            ydEjbCon.trx("CraneLdHdSeEJB", "procY1CrnLdWr", inRecord);

        } catch (Exception e) {
            szMsg =szMethodName + "() " +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);

        } // end of try catch


        szMsg="Y1크레인권상실적 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

    } // end of rcvY1CrnLdWr()




    /**
     * 오퍼레이션명 : Y3크레인작업지시요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     * @weblogic.transaction-descriptor trans-timeout-seconds="180"
     */
    public void rcvY3CrnWrkOrdReq(JDTORecord inRecord) throws DAOException  {
    //
    // YD-UC-5103 Y3크레인작업지시요구
    // TC : Y3YDL007
    // A후판슬라브야드 L2시스템으로부터 크레인작업지시요구 수신
    //
    //┏━┓
    //┃ A후판슬라브야드 L2에서 크레인작업지시를 요구를 수신하여 크레인의 현재 스케줄을 작업지시시
    //┗━┛

        String szMsg="";
        String szMethodName="rcvY3CrnWrkOrdReq";

        if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

            szMsg= szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;

        }


        try {


            ydEjbCon.trx("CraneLdHdSeEJB", "procY3CrnWrkOrdReq", inRecord);

        } catch (Exception e) {
            szMsg =szMethodName + "() " +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);

        } // end of try catch

        szMsg="Y3크레인작업지시요구 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


     } // end of rcvY3CrnWrkOrdReq()




    /**
     * 오퍼레이션명 : Y3크레인권상실적 (Y3YDL008)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     * @weblogic.transaction-descriptor trans-timeout-seconds="180"
     */
    public void rcvY3CrnLdWr(JDTORecord inRecord) throws DAOException  {
    //
    // YD-UC-5104 Y3크레인권상실적
    // TC : Y3YDL008, YDYDJ602
    // A후판슬라브야드 L2시스템으로부터 크레인권상실적 수신
    //
    //┏━┓
    //┃ A후판슬라브야드 L2에서 크레인 권상처리 결과를 수신하여 권상실적을 등록
    //┗━┛

        String szMsg="";
        String szMethodName="rcvY3CrnLdWr";

        if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

            szMsg= szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;

        }


        try {

            // 권상실적처리 요청
            ydEjbCon.trx("CraneLdHdSeEJB", "procY3CrnLdWr", inRecord);

        } catch (Exception e) {
            szMsg =szMethodName + "() " +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);

        } // end of try catch




        szMsg="Y3크레인권상실적 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


     } // end of rcvY3CrnLdWr()




    /**
     * 오퍼레이션명 : Y4 크레인작업지시요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     * @weblogic.transaction-descriptor trans-timeout-seconds="180"
     */
    public void rcvY4CrnWrkOrdReq(JDTORecord inRecord) throws DAOException  {
    //
    // YD-UC-5105 Y4크레인작업지시요구
    // TC : Y4YDL007
    // 후판제품야드L2시스템으로부터 크레인작업지시요구 수신
    //
    //┏━┓
    //┃ 후판제품야드 L2에서 크레인작업지시를 요구를 수신하여 크레인의 현재 스케줄을 작업지시시
    //┗━┛

        String szMsg="";
        String szMethodName="rcvY4CrnWrkOrdReq";

        if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

            szMsg= szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;

        }


        try {


            ydEjbCon.trx("CraneLdHdSeEJB", "procY4CrnWrkOrdReq", inRecord);

        } catch (Exception e) {
            szMsg =szMethodName + "() " +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);

        } // end of try catch


        szMsg="Y4크레인작업지시요구 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


     } // end of rcvY4CrnWrkOrdReq()



    /**
     * 오퍼레이션명 : Y8 크레인작업지시요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     * @weblogic.transaction-descriptor trans-timeout-seconds="180"
     */
    public void rcvY8CrnWrkOrdReq(JDTORecord inRecord) throws DAOException  {
    //
    // TC : Y8YDL007
    // 2후판제품야드L2시스템으로부터 크레인작업지시요구 수신
    //
    //┏━┓
    //┃ 2후판제품야드 L2에서 크레인작업지시를 요구를 수신하여 크레인의 현재 스케줄을 작업지시시
    //┗━┛

        String szMsg="";
        String szMethodName="rcvY8CrnWrkOrdReq";

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= inRecord.getResultCode();				// 전문으로 부터 logid get
String logId                    		= ydUtils.getJDTOLogId(inRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 					// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번


szMsg = "Y8크레인작업지시요구 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

            szMsg= szMethodName+"() 실행 실패";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

            return;

        }


        try {


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// procY4CrnWrkOrdReq call 시  inRecord 에 logId SET 추가 개선
        	inRecord.setField("LOG_ID", logId);
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

            ydEjbCon.trx("CraneLdHdSeEJB", "procY4CrnWrkOrdReq", inRecord);

        } catch (Exception e) {
            szMsg =szMethodName + "() " +e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

            throw new DAOException(szMsg);

        } // end of try catch


        szMsg="Y8크레인작업지시요구 처리("+szMethodName+") 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);


     } // end of rcvY8CrnWrkOrdReq()





    /**
     * 오퍼레이션명 : Y4크레인권상실적
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     * @weblogic.transaction-descriptor trans-timeout-seconds="180"
     */
    public void rcvY4CrnLdWr(JDTORecord inRecord) throws DAOException  {
    //
    // YD-UC-5106 Y4크레인권상실적
    // TC : Y4YDL008, YDYDJ604
    // 후판제품야드L2시스템으로부터 크레인권상실적 수신
    //
    //┏━┓
    //┃ 후판제품야드 L2에서 크레인 권상처리 결과를 수신하여 권상실적을 등록
    //┗━┛

        String szMsg="";
        String szMethodName="rcvY4CrnLdWr";

        if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

            szMsg= szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;

        }


        try {

            // 권상실적처리 요청
            ydEjbCon.trx("CraneLdHdSeEJB", "procY4CrnLdWr", inRecord);

        } catch (Exception e) {
            szMsg =szMethodName + "() " +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);

        } // end of try catch


        szMsg="Y4크레인권상실적 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


     } // end of rcvY4CrnLdWr()



    /**
     * 오퍼레이션명 : Y8크레인권상실적
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     * @weblogic.transaction-descriptor trans-timeout-seconds="180"
     */
    public void rcvY8CrnLdWr(JDTORecord inRecord) throws DAOException  {
    //
    // TC : Y8YDL008, YDYDJ604
    // 2후판제품야드L2시스템으로부터 크레인권상실적 수신
    //
    //┏━┓
    //┃ 2후판제품야드 L2에서 크레인 권상처리 결과를 수신하여 권상실적을 등록
    //┗━┛

        String szMsg="";
        String szMethodName="rcvY8CrnLdWr";

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= inRecord.getResultCode();				// 전문으로 부터 logid get
String logId                    		= ydUtils.getJDTOLogId(inRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 					// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8크레인권상실적 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

            szMsg= szMethodName+"() 실행 실패";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

            return;

        }


        try {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 전문처리 procY4CrnLdWr Method에 ([T] + 전문일련번호) 형식으로  logId 넘김
        	inRecord.setField("LOG_ID", logId);                                         
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

            // 권상실적처리 요청
            ydEjbCon.trx("CraneLdHdSeEJB", "procY4CrnLdWr", inRecord);

        } catch (Exception e) {
            szMsg =szMethodName + "() " +e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

            throw new DAOException(szMsg);

        } // end of try catch


        szMsg="Y8크레인권상실적 처리("+szMethodName+") 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);


     } // end of rcvY8CrnLdWr()





    /**
     * 오퍼레이션명 : C열연코일야드L2 크레인작업지시 요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     * @weblogic.transaction-descriptor trans-timeout-seconds="180"
     */
    public void rcvY5CrnWrkOrdReq(JDTORecord inRecord) throws DAOException  {
    //
    // YD-UC-5107 Y5크레인작업지시요구
    // TC : Y5YDL007
    // C열연코일야드L2시스템으로부터 크레인작업지시요구 수신
    //
    //┏━┓
    //┃ C열연코일야드L2에서 크레인작업지시를 요구를 수신하여 크레인의 현재 스케줄을 작업지시시
    //┗━┛

        String szMsg="";
        String szMethodName="rcvY5CrnWrkOrdReq";

        if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

            szMsg= szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;

        }


        try {


//sjh코일 분리          ydEjbCon.trx("CraneLdHdSeEJB", "procY5CrnWrkOrdReq", inRecord);

            ydEjbCon.trx("CoilCraneLdHdSeEJB", "procY5CrnWrkOrdReq", inRecord);

        } catch (Exception e) {
            szMsg =szMethodName + "() " +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);

        } // end of try catch

        szMsg="Y5크레인작업지시요구 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


     } // end of rcvY5CrnWrkOrdReq()




    /**
     * 오퍼레이션명 : C열연코일야드L2 크레인권상실적 (Y5YDL008)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     * @weblogic.transaction-descriptor trans-timeout-seconds="180"
     */
    public void rcvY5CrnLdWr(JDTORecord inRecord) throws DAOException  {
    //
    // YD-UC-5108 Y5크레인권상실적
    // TC : Y5YDL008, YDYDJ606
    // C열연코일야드L2시스템으로부터 크레인권상실적 수신
    //
    //┏━┓
    //┃ C열연코일야드 L2에서 크레인 권상처리 결과를 수신하여 권상실적을 등록
    //┗━┛

        String szMsg="";
        String szMethodName="rcvY5CrnLdWr";

        if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

            szMsg= szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;

        }



        try {

            // 권상실적처리 요청
//          sjh코일 분리            ydEjbCon.trx("CraneLdHdSeEJB", "procY5CrnLdWr", inRecord);
            ydEjbCon.trx("CoilCraneLdHdSeEJB", "procY5CrnLdWr", inRecord);

        } catch (Exception e) {
            szMsg =szMethodName + "() " +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);

        } // end of try catch


        szMsg="Y5크레인권상실적 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


     } // end of rcvY5CrnLdWr()



    /**
     * 오퍼레이션명 : C열연코일야드L2 크레인강제이적 요구 (Y5YDL012)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY5CrnMvstk(JDTORecord inRecord) throws DAOException  {
    //
    // TC : Y5YDL012
    // C열연코일야드L2시스템으로부터 크레인이적요구 수신 수신
    //
    //┏━┓
    //┃ C열연코일야드 L2에서 크레인 이적요구 를 수신하여 이적 작업지시 생성
    //┗━┛

        String szMsg="";
        String szMethodName="rcvY5CrnMvstk";

        if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

            szMsg= szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }

        try {

            //이적 요구  요청
            ydEjbCon.trx("CoilCraneLdHdSeEJB", "procY5CrnMvstk", inRecord);

        } catch (Exception e) {
            szMsg =szMethodName + "() " +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);

        } // end of try catch


        szMsg="Y5크레인권상실적 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


     } // end of rcvY5CrnLdWr()




    /**
     * 오퍼레이션명 : C열연코일야드L2 작업현황요구 (Y5YDL013)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvY5CrnMvwbk(JDTORecord inRecord) throws DAOException  {
    //
    // TC : Y5YDL013
    // C열연코일야드L2시스템으로부터 작업현황요구 수신 수신
    //
    //┏━┓
    //┃ C열연코일야드 L2에서 크레인 작업현황요구 를 수신하여 작업현황유구응답 처리
    //┗━┛

        String szMsg="";
        String szMethodName="rcvY5CrnMvwbk";

        if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

            szMsg= szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }

        try {

            //이적 요구  요청
            ydEjbCon.trx("CoilCraneLdHdSeEJB", "procY5CrnMvwbk", inRecord);

        } catch (Exception e) {
            szMsg =szMethodName + "() " +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);

        } // end of try catch


        szMsg="Y5크레인작업현황요구 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


     } // end of rcvY5CrnMvwbk()


    //┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
    //
    //                     일관제철소정보관리시스템-야드관리
    //              작업실행관리-권상실적처리 Facade Session Bean
    //                          2008.09.30 YHWHman
    //
    //┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛




  //---------------------------------------------------------------------------
} // end of class

