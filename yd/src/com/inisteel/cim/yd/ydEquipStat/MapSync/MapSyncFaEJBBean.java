package com.inisteel.cim.yd.ydEquipStat.MapSync;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;

import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdUtils;





/**
 * Map동기화 Facade Session EJB 
 * 
 * @ejb.bean name="MapSyncFaEJB" jndi-name="MapSyncFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class MapSyncFaEJBBean extends BaseSessionBean {
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
     * 오퍼레이션명 : Y1저장위치제원요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws JDTOException
     */
    public void rcvY1StrLocSpecReq(JDTORecord inRecord) throws JDTOException  {
        //
        // YD-UC-????
        // TC : Y1YDL001
        // C연주슬라브야드L2시스템으로부터 저장위치제원요구 수신
        //
        String szMsg        = "";
        String szMethodName = "rcvY1StrLocSpecReq";

        if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
            szMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("MapSyncSeEJB", "procY1StrLocSpecReq", inRecord);
        } catch (Exception e) {
            szMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

        szMsg = "Y1저장위치제원요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    } // end of rcvY1StrLocSpecReq()





    /**
     * 오퍼레이션명 : Y1저장품제원요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws JDTOException
     */
    public void rcvY1StockSpecReq(JDTORecord inRecord) throws JDTOException  {
        //
        // YD-UC-????
        // TC : Y1YDL002
        // C연주슬라브야드L2시스템으로부터 저장품제원요구 수신
        //
        String szMsg        = "";
        String szMethodName = "rcvY1StockSpecReq";

        if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
            szMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("MapSyncSeEJB", "procY1StockSpecReq", inRecord);
        } catch (Exception e) {
            szMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

        szMsg = "Y1저장품제원요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    } // end of rcvY1StockSpecReq()





    /**
     * 오퍼레이션명 : Y1크레인작업계획요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws JDTOException
     */
    public void rcvY1CrnWrkPlnReq(JDTORecord inRecord) throws JDTOException  {
        // TKOVLOC
        // YD-UC-????
        // TC : Y1YDL006
        // C연주슬라브야드L2시스템으로부터 크레인작업계획요구 수신
        //
        String szMsg        = "";
        String szMethodName = "rcvY1CrnWrkPlnReq";

        if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
            szMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("MapSyncSeEJB", "procY1CrnWrkPlnReq", inRecord);
        } catch (Exception e) {
            szMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

        szMsg = "Y1크레인작업계획요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    } // end of rcvY1CrnWrkPlnReq()





    /**
     * 오퍼레이션명 : Y3저장위치제원요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws JDTOException
     */
    public void rcvY3StrLocSpecReq(JDTORecord inRecord) throws JDTOException  {
        // TKOVLOC
        // YD-UC-????
        // TC : Y3YDL001
        // A후판슬라브야드L2시스템으로부터 저장위치제원요구 수신
        //
        String szMsg        = "";
        String szMethodName = "rcvY3StrLocSpecReq";

        if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
            szMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("MapSyncSeEJB", "procY3StrLocSpecReq", inRecord);
        } catch (Exception e) {
            szMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

        szMsg = "Y3저장위치제원요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    } // end of rcvY3StrLocSpecReq()





    /**
     * 오퍼레이션명 : Y3저장품제원요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws JDTOException
     */
    public void rcvY3StockSpecReq(JDTORecord inRecord) throws JDTOException  {
        // TKOVLOC
        // YD-UC-????
        // TC : Y3YDL002
        // A후판슬라브야드L2시스템으로부터 저장품제원요구 수신
        //
        String szMsg        = "";
        String szMethodName = "rcvY3StockSpecReq";

        if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
            szMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("MapSyncSeEJB", "procY3StockSpecReq", inRecord);
        } catch (Exception e) {
            szMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

        szMsg = "Y3저장품제원요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    } // end of rcvY3StockSpecReq()





    /**
     * 오퍼레이션명 : Y3크레인작업계획요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws JDTOException
     */
    public void rcvY3CrnWrkPlnReq(JDTORecord inRecord) throws JDTOException  {
        // TKOVLOC
        // YD-UC-????
        // TC : Y3YDL006
        // A후판슬라브야드L2시스템으로부터 크레인작업계획요구 수신
        //
        String szMsg        = "";
        String szMethodName = "rcvY3CrnWrkPlnReq";

        if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
            szMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("MapSyncSeEJB", "procY3CrnWrkPlnReq", inRecord);
        } catch (Exception e) {
            szMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

        szMsg = "Y3크레인작업계획요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    } // end of rcvY3CrnWrkPlnReq()





    /**
     * 오퍼레이션명 : Y4저장위치제원요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws JDTOException
     */
    public void rcvY4StrLocSpecReq(JDTORecord inRecord) throws JDTOException  {
        // TKOVLOC
        // YD-UC-????
        // TC : Y4YDL001
        // 후판제품야드L2시스템으로부터 저장위치제원요구 수신
        //
        String szMsg        = "";
        String szMethodName = "rcvY4StrLocSpecReq";

        if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
            szMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("MapSyncSeEJB", "procY4StrLocSpecReq", inRecord);
        } catch (Exception e) {
            szMsg =szMethodName + "() " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

        szMsg = "Y4저장위치제원요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    } // end of rcvY4StrLocSpecReq()


    /**
     * 오퍼레이션명 : Y8저장위치제원요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws JDTOException
     */
    public void rcvY8StrLocSpecReq(JDTORecord inRecord) throws JDTOException  {
        // TC : Y8YDL001
        // 2후판제품야드L2시스템으로부터 저장위치제원요구 수신
        //
        String szMsg        = "";
        String szMethodName = "rcvY8StrLocSpecReq";

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선 : [T] + 전문일련번호
// String logId                    		= inRecord.getResultCode();				// 전문으로 부터 logid get
String logId                    		= ydUtils.getJDTOLogId(inRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 					// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번


szMsg = "Y8저장위치제원요구 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
            szMsg = szMethodName + "() 실행 실패";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return ;
        }

        try {
        	
////////////////////////////////////////////////////////////////////////////////////////
//2024.09.02 로그 개선  START
//전문처리 procY4StrLocSpecReq Method에 ([T] + 전문일련번호) 형식으로  logId 넘김
        	inRecord.setField("LOG_ID", logId);                                         
//2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        	
            //2후판제품야드L2시스템으로부터 저장위치제원요구 수신하였지만
            //처리는 1후판제품야드L2의 저장위치제원요구 수신 프로세스를 호출 한다.
            ydEjbCon.trx("MapSyncSeEJB", "procY4StrLocSpecReq", inRecord);

        } catch (Exception e) {
            szMsg =szMethodName + "() " + e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            throw new JDTOException(szMsg);
        } // end of try catch

        szMsg = "Y8저장위치제원요구 처리(" + szMethodName + ") 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

    } // end of rcvY8StrLocSpecReq()



    /**
     * 오퍼레이션명 : Y4저장품제원요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws JDTOException
     */
    public void rcvY4StockSpecReq(JDTORecord inRecord) throws JDTOException  {
        // TKOVLOC
        // YD-UC-????
        // TC : Y4YDL002
        // 후판제품야드L2시스템으로부터 저장품제원요구 수신
        //
        String szMsg        = "";
        String szMethodName = "rcvY4StockSpecReq";

        if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
            szMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("MapSyncSeEJB", "procY4StockSpecReq", inRecord);
        } catch (Exception e) {
            szMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

        szMsg = "Y4저장품제원요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    } // end of rcvY4StockSpecReq()

    /**
     * 오퍼레이션명 : Y8저장품제원요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws JDTOException
     */
    public void rcvY8StockSpecReq(JDTORecord inRecord) throws JDTOException  {
        // TC : Y8YDL002
        // 2후판제품야드L2시스템으로부터 저장품제원요구 수신
        //
        String szMsg        = "";
        String szMethodName = "rcvY8StockSpecReq";


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= inRecord.getResultCode();				// 전문으로 부터 logid get
String logId                    		= ydUtils.getJDTOLogId(inRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 					// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8저장품제원요구 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
            szMsg = szMethodName + "() 실행 실패";
//2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return ;
        }

        try {
        	
////////////////////////////////////////////////////////////////////////////////////////
//2024.09.02 로그 개선  START
//전문처리 procY4StockSpecReq Method에 ([T] + 전문일련번호) 형식으로  logId 넘김
        	inRecord.setField("LOG_ID", logId);
//2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        	
            //2후판제품야드L2시스템으로부터 저장품제원요구 수신하였지만
            //처리는 1후판제품야드L2의 저장품제원요구 수신 프로세스를 호출 한다.
            ydEjbCon.trx("MapSyncSeEJB", "procY4StockSpecReq", inRecord);

        } catch (Exception e) {
            szMsg = szMethodName + "() " + e.getMessage();
//2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            throw new JDTOException(szMsg);
        } // end of try catch

        szMsg = "Y8저장품제원요구 처리(" + szMethodName + ") 완료";
//2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
    } // end of rcvY8StockSpecReq()




    /**
     * 오퍼레이션명 : Y5저장위치제원요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws JDTOException
     */
    public void rcvY5StrLocSpecReq(JDTORecord inRecord) throws JDTOException  {
        // TKOVLOC
        // YD-UC-????
        // TC : Y5YDL001
        // C열연코일야드L2시스템으로부터 저장위치제원요구 수신
        //
        String szMsg        = "";
        String szMethodName = "rcvY5StrLocSpecReq";

        if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
            szMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("MapSyncSeEJB", "procY5StrLocSpecReq", inRecord);
        } catch (Exception e) {
            szMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

        szMsg = "Y5저장위치제원요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    } // end of rcvY5StrLocSpecReq()





    /**
     * 오퍼레이션명 : Y5저장품제원요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws JDTOException
     */
    public void rcvY5StockSpecReq(JDTORecord inRecord) throws JDTOException  {
        // TKOVLOC
        // YD-UC-????
        // TC : Y5YDL002
        // C열연코일야드L2시스템으로부터 저장품제원요구 수신
        //
        String szMsg        = "";
        String szMethodName = "rcvY5StockSpecReq";

        if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
            szMsg = szMethodName + "() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

        try {
            ydEjbCon.trx("MapSyncSeEJB", "procY5StockSpecReq", inRecord);
        } catch (Exception e) {
            szMsg = szMethodName + "() " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } // end of try catch

        szMsg = "Y5저장품제원요구 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    } // end of rcvY5StockSpecReq()




    //┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
    //
    //                     일관제철소정보관리시스템-야드관리
    //              설비상태관리-맵동기화 Facade Session Bean
    //                          2008.09.30 YHWHman
    //
    //┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

    public static void main(String[] args){
        MapSyncFaEJBBean im =new MapSyncFaEJBBean();
        JDTORecord testRec =JDTORecordFactory.getInstance().create();

        try {
            testRec.setField("JMS_TC_CD","Y5YDL001");
            im.rcvY5StrLocSpecReq(testRec);
        } catch (JDTOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    } // end of testMain()




  //---------------------------------------------------------------------------
} // end of class

