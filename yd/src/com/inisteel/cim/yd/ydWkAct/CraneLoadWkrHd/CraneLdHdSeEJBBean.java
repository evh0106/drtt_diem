package com.inisteel.cim.yd.ydWkAct.CraneLoadWkrHd;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydTcarFtmvMtlDao.YdTcarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkPlnSimulationDao.YdWrkPlnSimulationDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDBAssist;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.common.rule.GetBreRule6;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.jplateyd.delegate.JPlateYdDelegate;
import com.inisteel.cim.yd.message.MessageSenderTalk;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;


/**
 * 권상실적처리 Session EJB  
 * 
 * @ejb.bean name="CraneLdHdSeEJB" jndi-name="CraneLdHdSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CraneLdHdSeEJBBean extends BaseSessionBean {

    // Session Name
    private String szSessionName=getClass().getName();

    private YdUtils ydUtils =new YdUtils();

    private YdDaoUtils ydDaoUtils =new YdDaoUtils();

    private YdTcConst ydTcConst =new YdTcConst();

    private YdDelegate ydDelegate =new YdDelegate();

    private YdDBAssist ydDBAssist =new YdDBAssist();

    private YdPICommDAO   commPiDao   = new YdPICommDAO();

    // [DEBUG] message flag
    private boolean bDebugFlag=true;

    private EJBConnector ydEjbCon = new EJBConnector("default", this);
    
    private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();

    /**
     * ejbCrate()
     *
     * @throws javax.ejb.CreateException
     */
    public void ejbCreate() throws javax.ejb.CreateException {
    }




    /**
     *      [A] 오퍼레이션명 :
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procTest(JDTORecord msgRecord)throws JDTOException  {


        String szMsg="";
        String szMethodName="procTest";



        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null){
            szMsg=szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
        }
        if(bDebugFlag){
            szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }



        //
        //
        //
        //
        //  toDo Something...
        //
        //
        //
        //
        //


        szMsg="Test정보수신 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


    }// end of procTest()








    /**
     * 오퍼레이션명 : 통합야드 크레인 작업지시
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws JDTOException
     */
    public String procY0CrnWrkOrdReq(JDTORecord msgRecord)throws JDTOException  {

        YdDelegate   ydDelegate   = new YdDelegate();
        YdCrnSchDao  YdCrnSchDao  = new YdCrnSchDao();
        YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
        YdEqpDao     ydEqpDao     = new YdEqpDao();
        JDTORecord recCrnSch = JDTORecordFactory.getInstance().create();
        JDTORecord recInPara = null;
        JDTORecord recOutTemp = null;
        JDTORecord recIntTemp = null;

        JDTORecordSet rsCrnSch = JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet rsWrkBook = null;


        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "procY0CrnWrkOrdReq";
        String szOperationName          = "통합야드크레인작업지시";

        String szEqpId                  = "";
        String szWrkProgStat            = "";
        String szYD_GP                  = "";
        String szYD_BAY_GP              = "";
        String szYD_WRK_PLAN_TCAR       = "";
        String szTRN_EQP_CD             = "";
        String szCAR_NO                 = "";

        //스케쥴코드
        String szYD_SCH_CD              = "";

        boolean bRtnCheck               = true;
        boolean blnRtnVal               = true;

        String szRtnMsg                 = null;


        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null || szRcvTcCode.equals("")){
            szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return YdConstant.RETN_CD_TC_ERROR;
        }




        if(bDebugFlag){
            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }



        try{
            //======================================================================================================
            // LOG 출력 - 그냥 테스트용으로 출력 레코드값체크하는 코드는 제외하고 나중에 삭제
            szMsg = "[1] 야드설비ID : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[2] 야드설비작업Mode : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[3] 야드작업진행상태 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[4] 야드스케쥴코드 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[5] 야드크레인스케쥴ID : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[6] 야드크레인X축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[7] 야드크레인Y축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            //======================================================================================================

            szEqpId     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            szYD_GP     = szEqpId.substring(0,1);
            szYD_BAY_GP = szEqpId.substring(1,2);

            //------------------------------------------------------------------------------------------------------
            // 야드설비상태 Check       수신받은  야드설비Id로 설비Table를 조회하여 야드설비상태를 Check하고 고장이면 return
            //------------------------------------------------------------------------------------------------------

            szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


            JDTORecordSet rsCrnInfo = JDTORecordFactory.getInstance().createRecordSet("");
            JDTORecord recCrnInfo   = JDTORecordFactory.getInstance().create();

            szRtnMsg = this.eqpStatCheck(szEqpId, rsCrnInfo);

            if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
                szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 시 오류발생 - 메세지 : " + szRtnMsg;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return szRtnMsg;
            }

            szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 완료 - 메세지 : " + szRtnMsg;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //------------------------------------------------------------------------------------------------------

            // 스케줄 기준 체크
            szYD_SCH_CD =  ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
            if( !szYD_SCH_CD.equals("") ) {
                blnRtnVal = YdCommonUtils.chkGetSchRule(szYD_SCH_CD, rsResult);
                if( !blnRtnVal ) return YdConstant.RETN_CD_FAILURE;
                // 레코드 추출
                rsResult.first();
                JDTORecord recPara = rsResult.getRecord();
                // 스케줄 금지 유무
                String szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,"YD_SCH_PROH_EXN");

                // 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
                if (szYD_SCH_PROH_EXN.equals("Y")) {
                    szMsg = "크레인 작업지시 시 스케쥴코드(" + szYD_SCH_CD + ")에 대한 스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return YdConstant.RETN_CRN_SCH_PROH;
                }
            }

            //야드 작업 진행상태를 check한다.
            szWrkProgStat = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");

            /*
             * 2011.05.25 윤재광 현 크레인상태를 넘겨줌.
            //레코드 추출
            rsCrnInfo.first();
            recCrnInfo = rsCrnInfo.getRecord();

            //야드 작업 진행상태를 check한다.
            szWrkProgStat = ydDaoUtils.paraRecChkNull(recCrnInfo, "YD_WRK_PROG_STAT");
            */

            //야드 작업 진행상태가 1,3인 경우 (작업지시를 재요구 하는 경우 사용한다.)
            if (szWrkProgStat.equals("1") || szWrkProgStat.equals("2") || szWrkProgStat.equals("3")){
                szMsg="야드 작업 진행상태가 1,3인 경우";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                intRtnVal = this.Y0ChkWrkProgStat(msgRecord, rsCrnSch);

                if( intRtnVal == 0 ) {
                    //크레인 스케줄이 없다면 없다는 메시지를 L2에 전송해야한다.

                    return YdConstant.RETN_CRN_NO_SCH;
                }else if(intRtnVal == -1) {
                    return YdConstant.RETN_CD_FAILURE;
                }else{      //통합야드
                    szMsg = "[크레인 작업지시]현재크레인이 작업중인 스케줄을 재전송처리않고 로그만 기록함 - 통합야드";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    return YdConstant.RETN_CD_SUCCESS;
                }


            //야드 작업 진행 상태가 'W'인경우 (작업이 없을때 요구)
            }else if(szWrkProgStat.equals("W")){
                szMsg="야드 작업 진행 상태가 'W'인경우";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                intRtnVal = this.Y0ChkWrkProgStatW(msgRecord, rsCrnSch);
                if(intRtnVal == 0) {
                    szMsg="크레인 스케줄이 조회되지 않습니다 [intRtnVal = " + intRtnVal + "]";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return YdConstant.RETN_CRN_NO_SCH;
                }else if(intRtnVal < 0) {
                    szMsg="크레인스케줄 조회 중 에러 발생  [intRtnVal = " + intRtnVal + "]";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return YdConstant.RETN_CD_FAILURE;
                }

            //야드 작업 진행 상태가 '4'인 경우 현재 진행중인 작업이 있을 경우 (현재 진행중인 작업이 있을 경우 해당작업을 호출한다.스케줄 코드로 조회, 조회한 data가 없다면 스케줄우선순위가 빠르고 크레인스케줄id가 가장빠른 작업을 보내준다.)
            }else if(szWrkProgStat.equals("4")) {

                szMsg="야드 작업 진행 상태가 '4'인 경우 (권하실적처리에서 호출)";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                intRtnVal = this.Y0ChkWrkProgStat4(msgRecord, rsCrnSch);

                //더이상 크레인 스케줄을 찾지 못했을 경우 설비상태를 W <--idle로 변경하고 0으로 리턴, 에러인 경우  -1로 리턴.. 크레인 스케줄 호출부분이 아직 없기때문에 종료처리...추후 변경 0일경우는 크레인 스케줄 호출..
                if(intRtnVal <= 0) {
                    szMsg="더이상 크레인 스케줄을 찾지못했거나 Error가 발생했을 경우 작업예약을 조회한다. intRtnVal : " + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


                    //작업예약조회
                    recInPara = JDTORecordFactory.getInstance().create();
                    recInPara.setField("YD_EQP_ID", szEqpId);
                    rsWrkBook = JDTORecordFactory.getInstance().createRecordSet("");
                    intRtnVal = ydWrkbookDao.getYdWrkbook(recInPara, rsWrkBook, 4);
                    if(intRtnVal > 0) {
                        //검색된 작업예약을 크레인스케줄을 생성한다. 생성하면 설비상태값에 따라서 작업지시를 내려보내도록되어있다.
                        szMsg="현재 크레인(" + szEqpId + ")로 작업할 수 있는 작업예약을 조회하였습니다.  크레인스케줄Main호출";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        //크레인 스케줄 호출 (설비id,스케줄코드);
                        rsWrkBook.absolute(1);
                        recOutTemp = JDTORecordFactory.getInstance().create();
                        recOutTemp.setRecord(rsWrkBook.getRecord());

                        recInPara = JDTORecordFactory.getInstance().create();

                        recInPara.setField("MSG_ID", "YDYDJ512");
                        recInPara.setField("YD_EQP_ID", szEqpId);
                        recInPara.setField("YD_SCH_CD", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"));



                        //크레인 스케줄 호출 메세지 전송
                        ydDelegate.sendMsg(recInPara);
                        //다음 크레인스케줄이 생성되는 중이기때문에 현재상태에서는 작업지시를 내려보내지않고 리턴
                        return YdConstant.RETN_CD_SUCCESS;
                    }

                    szMsg="현재 크레인(" + szEqpId + ")로 작업할 수 있는 작업예약이 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    szMsg = "[크레인 작업지시]자동작업호출 없음 - 통합야드";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    //다음 크레인스케줄이 생성되는 중이기때문에 현재상태에서는 작업지시를 내려보내지않고 리턴
                    return YdConstant.RETN_CRN_NO_WRK;

                }else{
                    //다음크레인 작업을 찾았을경우 작업지시상태로 변경
                    recCrnSch = JDTORecordFactory.getInstance().create();
                    recCrnSch.setRecord(rsCrnSch.getRecord(0));

                    String sSchYdWrkProgStat    = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WRK_PROG_STAT");
                    String sCrnYdWrkProgStat    = "";

                    if( sSchYdWrkProgStat.equals("") || sSchYdWrkProgStat.equals("W") || sSchYdWrkProgStat.equals("1") ) {
                        sCrnYdWrkProgStat   = "1";
                    }else{
                        sCrnYdWrkProgStat   = sSchYdWrkProgStat;
                    }
                    szMsg=" 윤재광 윤재광 : data ==========================="+sCrnYdWrkProgStat;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                    recIntTemp = JDTORecordFactory.getInstance().create();
                    recIntTemp.setField("YD_EQP_ID", szEqpId);
                    recIntTemp.setField("YD_EQP_STAT", sCrnYdWrkProgStat);
                    intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
                    if(intRtnVal <= 0) {
                        if(intRtnVal == 0) {
                            szMsg=" Y0ChkWrkProgStatW updYdEqp : data not found";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                        }else if(intRtnVal == -1) {
                            szMsg=" Y0ChkWrkProgStatW updYdEqp : duplicate data,";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        }else if(intRtnVal == -2) {
                            szMsg=" Y0ChkWrkProgStatW updYdEqp : parameter error";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        }else if(intRtnVal == -3){
                            szMsg=" Y0ChkWrkProgStatW updYdEqp : execution failed";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        }
                    }
                }
            }

            szMsg="[크레인 작업지시] rsCrnSch 1 : " + rsCrnSch;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            if( rsCrnSch != null ) {
                szMsg="[크레인 작업지시] rsCrnSch.size() : " + rsCrnSch.size();
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            }
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //L2로 전송 ( 전송을 하진 않고 Consol창에 보여준다. )
            for(int Loop_i = 1; Loop_i <= rsCrnSch.size(); Loop_i++) {
                rsCrnSch.absolute(Loop_i);
                recCrnSch = JDTORecordFactory.getInstance().create();
                recCrnSch.setRecord(rsCrnSch.getRecord());
                System.out.print("YD_CRN_SCH_ID : " + recCrnSch.getFieldString("YD_CRN_SCH_ID") + ", ") ;
                System.out.print("YD_WBOOK_ID : "   + recCrnSch.getFieldString("YD_WBOOK_ID") + ", ") ;
                System.out.print("YD_EQP_ID : "     + recCrnSch.getFieldString("YD_EQP_ID") + ", ") ;
                System.out.print("YD_SCH_CD : "     + recCrnSch.getFieldString("YD_SCH_CD") + ", ") ;
                System.out.println("YD_SCH_PRIOR : "  + recCrnSch.getFieldString("YD_SCH_PRIOR") + ", ") ;

//              recCrnSch = JDTORecordFactory.getInstance().create();
//              recCrnSch.setRecord(rsCrnSch.getRecord(Loop_i-1));


                if(ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC").equals("")) {
                    szMsg="[크레인 작업지시] 크레인스케줄 보기 1 : " + szMsg;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    continue;
                }else{
                    szMsg="[크레인 작업지시] 크레인스케줄 보기 2 : " + szMsg;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    break;
                }


            }


            recCrnSch = JDTORecordFactory.getInstance().create();
            recCrnSch.setRecord(rsCrnSch.getRecord(0));

            String szYD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WRK_PROG_STAT");

            szMsg="[크레인 작업지시] rsCrnSch : " + rsCrnSch;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            recInPara = JDTORecordFactory.getInstance().create();
            //작업지시 전문 전송 data setup
//          recInPara.setField("MSG_ID", "YDY0L004");
            recInPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID"));

            if( szYD_WRK_PROG_STAT.equals("") || szYD_WRK_PROG_STAT.equals("W") ) {
                recInPara.setField("YD_WRK_PROG_STAT", "1");
            }else{
                recInPara.setField("YD_WRK_PROG_STAT", szYD_WRK_PROG_STAT);
            }

            recInPara.setField("YD_WORD_DT",      YdUtils.getCurDate("yyyyMMddHHmmss"));
            recInPara.setField("MODIFIER", "YDSYSTEM");

            szMsg="[크레인 작업지시] 작업지시 전문 전송 : " + recInPara;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //크레인스케줄의 작업진행 상태를 권상지시로 변경
//          intRtnVal = YdCrnSchDao.updYdCrnsch(recInPara, 0);
            /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnWrkProgStat*/
            intRtnVal = YdCrnSchDao.updYdCrnschDelay(recInPara, 302);
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="procY0CrnWrkOrdReq updYdCrnsch : data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                }else if(intRtnVal == -2) {
                    szMsg="procY0CrnWrkOrdReq updYdCrnsch : parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else{
                    szMsg="procY0CrnWrkOrdReq updYdCrnsch : execution failed";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return YdConstant.RETN_CD_FAILURE;
            }



             // Flex(실시간작업을위함)
                JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
                recFlex.setField("YD_GP",  YdConstant.YD_GP_INTGR_YARD);
                recFlex.setField("YD_EQP_ID", szEqpId);
                ydUtils.putYdFlexCrnWrk("", recFlex);


            recInPara.setField("YD_SCH_CD", ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD"));
            recInPara.setField("YD_GP", ydDaoUtils.paraRecChkNull(recCrnSch, "YD_GP"));

            //통합야드
            szMsg = "[크레인 작업지시]크레인 작업지시 메세지 전송하지 않음[L2가 존재하지 않음] - 통합야드";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


        }catch(Exception e){
            szMsg="Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return YdConstant.RETN_CD_FAILURE;
        }

        szMsg="크레인 작업지시("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return YdConstant.RETN_CD_SUCCESS;

    } //end of procY0CrnWrkOrdReq()







    /**
     * 오퍼레이션명 : 크레인 작업지시(작업지시를 재요구 하는 경우 사용한다.)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public int Y0ChkWrkProgStat(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws JDTOException  {
        YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
        YdEqpDao    ydEqpDao    = new YdEqpDao();

        JDTORecordSet rsResult          = null;
        JDTORecord recInTemp            = null;
        JDTORecord recOutTemp           = null;

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y0ChkWrkProgStat";
        String szCrnSchId               = "";

        String szQuery                  = "";

        String szYD_EQP_ID              = "";
        String szWrkProgStat            = "";
        String szYdEqpStat              = "";


        try{
//          //파라미터 중 스케줄ID가 있다 스케줄 ID로 크레인 스케줄을 조회하여 작업예약 ID를 조회한다.
//          szCrnSchId = ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID");
//
//          rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//          intRtnVal = YdCrnSchDao.getYdCrnsch(msgRecord, rsResult, 0);
//          if(intRtnVal <= 0) {
//              szMsg="크레인 스케줄 조회중 Error";
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//          }


            //작업진행상태
            szWrkProgStat = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");


            //설비Table를 조회하여 설비상태가 '1 : 권상지시'또는 '3 : 권하지시'이라면 크레인 스케줄 Table에서 야드작업진행 상태가 1,2,3인 것만 찾는다. 1건만 나와야 정상이고 1건이 아니라면 Error처리
            rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
            intRtnVal = ydEqpDao.getYdEqp(msgRecord, rsResult, 0);
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="Y0ChkWrkProgStat getYdEqp : data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                }else if(intRtnVal == -2) {
                    szMsg="Y0ChkWrkProgStat getYdEqp : parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return intRtnVal = -1;
            }

            recOutTemp = JDTORecordFactory.getInstance().create();
            recOutTemp.setRecord(rsResult.getRecord(0));
            szYdEqpStat = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_STAT");
            //설비상태가 1또는 3인경우
            if(szYdEqpStat.equals("1") || szYdEqpStat.equals("2") || szYdEqpStat.equals("3")){
                //설비id로 크레인 스케줄을 조회한다. 현재 작업진행상태가 1또는 3인 경우를 조회...
                rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
                intRtnVal = ydCrnSchDao.getYdCrnsch(msgRecord, rsResult, 16);
                if(intRtnVal <= 0) {
                    //에러처리
                    szMsg="현재 작업진행상태가 1또는 3인 크레인 스케줄을 조회 중 Error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    //return intRtnVal = -1;
                    return intRtnVal;
                }
            }

            rsCrnSch.addAll(rsResult);

        }catch(Exception e){

            szMsg="Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }


        szMsg="chkWrkProgStat("+szMethodName+") 처리 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y0ChkWrkProgStat()







    /**
     * 오퍼레이션명 : 크레인 작업지시(작업이 없을 경우 요구한다.)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public int Y0ChkWrkProgStatW(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws JDTOException  {
        YdEqpDao ydEqpDao = new YdEqpDao();
        YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
        JDTORecordSet rsResult = null;
        JDTORecord recIntTemp  = null;
        JDTORecord para  = null;

        int intRtnVal          = 0 ;


        String szMsg           = "";
        String szMethodName    = "Y0ChkWrkProgStatW";
        String szQuery         = "";

        String szYD_EQP_ID     = "";
        try{
            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");

            //크레인 스케줄 전체에서 우선순위가 가장 빠른 작업을 조회한다. 크레인 스케줄을 조회한다.
            rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
            para = JDTORecordFactory.getInstance().create();
            para.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"));

            intRtnVal = ydCrnSchDao.getYdCrnsch(para, rsResult, 15);
            if(intRtnVal == 0) {
                //더이상 크레인 스케줄에 작업이 없을 경우 설비Table에 설비상태를 'W' 명령선택대기(idle)로 변경한다.
                recIntTemp = JDTORecordFactory.getInstance().create();
                recIntTemp.setField("YD_EQP_ID", szYD_EQP_ID);
                recIntTemp.setField("YD_EQP_STAT", "W");
                intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
                if(intRtnVal <= 0) {
                    if(intRtnVal == 0) {
                        szMsg=" Y0ChkWrkProgStatW updYdEqp : data not found";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                    }else if(intRtnVal == -1) {
                        szMsg=" Y0ChkWrkProgStatW updYdEqp : duplicate data,";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }else if(intRtnVal == -2) {
                        szMsg=" Y0ChkWrkProgStatW updYdEqp : parameter error";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }else if(intRtnVal == -3){
                        szMsg=" Y0ChkWrkProgStatW updYdEqp : execution failed";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }
                }
                szMsg="더이상의 크레인스케줄이 조회되지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);

                return intRtnVal = 0;

            }else if(intRtnVal < 0) {
                szMsg="크레인 스케줄 조회중 Error";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return intRtnVal = -1;
            }




            //다음 스케줄을 찾았을 경우
            recIntTemp = JDTORecordFactory.getInstance().create();
            recIntTemp.setField("YD_EQP_ID", szYD_EQP_ID);
            recIntTemp.setField("YD_EQP_STAT", "1");
            recIntTemp.setField("YD_WORD_DT",      ydUtils.getCurDate("yyyyMMddHHmmss"));
            intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg=" Y0ChkWrkProgStatW updYdEqp : data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                }else if(intRtnVal == -1) {
                    szMsg=" Y0ChkWrkProgStatW updYdEqp : duplicate data,";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -2) {
                    szMsg=" Y0ChkWrkProgStatW updYdEqp : parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -3){
                    szMsg=" Y0ChkWrkProgStatW updYdEqp : execution failed";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return intRtnVal = -1;
            }
            rsCrnSch.addAll(rsResult);

        }catch(Exception e){

            szMsg="Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }


        szMsg="chkWrkProgStatW("+szMethodName+") 처리완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;

    } //end of Y0ChkWrkProgStatW()



    /**
     * 오퍼레이션명 : 크레인 작업지시(현재 진행중인 작업이 있을경우 해당작업)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public int Y0ChkWrkProgStat4(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws JDTOException  {
         YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();

        JDTORecordSet rsResult          = null;
        JDTORecord    recInPara         = null;

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y0ChkWrkProgStat4";

        String szSchCd                  = "";
        String szQuery                  = "";

        try{
            //스케줄 코드 Check
            szSchCd = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
            if(szSchCd.equals("")) {
                szMsg="스케줄코드가 없습니다. : parameter error";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return intRtnVal = -1;
            }

            rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
            intRtnVal = ydCrnSchDao.getYdCrnsch(msgRecord, rsResult, 6);
            //조회된 크레인 스케줄이 없다면  전체에서 빠른 스케줄을 호출한다.
            if(intRtnVal <= 0) {
                rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
                recInPara = JDTORecordFactory.getInstance().create();
                recInPara.setRecord(msgRecord);
                intRtnVal = this.Y0ChkWrkProgStatW(recInPara, rsResult);
                if(intRtnVal == -1) {
                    szMsg="크레인 작업 조회 중 Error!!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return intRtnVal = -1;
                }else if(intRtnVal == 0) {
                    szMsg="같은 스케줄 코드의 크레인 작업이 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return intRtnVal = 0;
                }
            }






            rsCrnSch.addAll(rsResult);

        }catch(Exception e){

            szMsg="Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }


        szMsg="크레인 작업지시("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y0ChkWrkProgStat4()








    /**
     * 오퍼레이션명 : 통합야드크레인권상실적등록
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
     * @ejb.transaction type="RequiresNew"
     */
    public String procY0CrnLdWr(JDTORecord msgRecord)throws DAOException  {
        YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
        YdDelegate ydDelegate = new YdDelegate();
        YdEqpDao   ydEqpDao   = new YdEqpDao();
        YdCarSchDao ydCarSchDao = new YdCarSchDao();
        YdStkColDao ydStkColDao = new YdStkColDao();

        //업데이트 할 크레인 스케줄 Data 항목 set
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();

        //파라미터 null check 후 받아온 Data
        JDTORecord getParamRecord = JDTORecordFactory.getInstance().create();


        //스케줄Table의 컬럼을 저장하기위해 생성
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet  rsResult = null;
        //레코드 셋의 레코드값을 받음
        JDTORecord getRecord = JDTORecordFactory.getInstance().create();
        JDTORecord recInTemp = null;

        JDTORecord recSendMsg = null;

        int intRtnVal                   = 0 ;

        String szWbookId                = "";

        String szMsg="";
        String szMethodName="procY0CrnLdWr";

        String szTcarEqpId = "";
        //크레인스케줄ID
        String szYD_CRN_SCH_ID = "";
        //야드스케줄코드
        String szYD_SCH_CD = null;
        //권상실적위치
        String szYD_UP_WR_LOC = null;
        //설비ID(크레인설비ID)
        String szYD_EQP_ID = null;
        //야드To위치결정방법
        String szYD_TO_LOC_DCSN_MTD = null;
        //권하지시위치
        String szYD_DN_WO_LOC = null;
        //야드목표야드구분
        String szYD_AIM_YD_GP = null;

        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null){
            szMsg=szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return YdConstant.RETN_CD_TC_ERROR;
        }
        if(bDebugFlag){
            szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        try{

//          String sApplyYnPI = commPiDao.ApplyYnPI("", "통합야드크레인권상실적등록", "APPPI0", "S", "*");

            //파라미터 check
            intRtnVal = this.Y0ParamCheck(msgRecord, getParamRecord, 0) ;

            //크레인스케줄ID
            szYD_CRN_SCH_ID = getParamRecord.getFieldString("YD_CRN_SCH_ID");
            //야드스케줄코드
            szYD_SCH_CD = getParamRecord.getFieldString("YD_SCH_CD");
            //권상실적위치
            szYD_UP_WR_LOC = getParamRecord.getFieldString("YD_UP_WR_LOC");
            //설비ID(크레인설비ID)
            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(getParamRecord, "YD_EQP_ID");

            setCrnschRecord.setField("YD_CRN_SCH_ID",         getParamRecord.getFieldString("YD_CRN_SCH_ID"));
            setCrnschRecord.setField("YD_SCH_CD",             getParamRecord.getFieldString("YD_SCH_CD")) ;
            setCrnschRecord.setField("YD_UP_WR_LOC",          getParamRecord.getFieldString("YD_UP_WR_LOC")) ;
            setCrnschRecord.setField("YD_UP_WR_LAYER",        getParamRecord.getFieldString("YD_UP_WR_LAYER")) ;

            //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
            intRtnVal = this.Y0UpdYdCrnsch(setCrnschRecord, 0) ;
            if(intRtnVal == -1) return YdConstant.RETN_CD_FAILURE;

            //Key Data Check!           키값은 Null이나 ""가 되어서는 안됨.
            if(setCrnschRecord.getFieldString("YD_CRN_SCH_ID").equals("") ||
               setCrnschRecord.getFieldString("YD_CRN_SCH_ID") == null) {
                szMsg = "'YD_CRN_SCH_ID' Data Error : 크레인스케줄ID가 없습니다." ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
                throw new DAOException(szMsg);
            }

            //대상 데이터 SELECT
            intRtnVal = this.Y0GetYdCrnsch(setCrnschRecord, getRecSet,3);

            if( intRtnVal <= 0 ) {
                szMsg = "스케쥴 Data가 존재하지 않습니다. ="+ intRtnVal;
                m_ctx.setRollbackOnly();
                throw new DAOException(szMsg);
            }


            //레코드셋의 사이즈값으로 ErrorCheck
            if(getRecSet.size() == 0){
                szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
                throw new DAOException(szMsg);
            }

            getRecSet.first();
            getRecord = getRecSet.getRecord();

            //작업예약ID
            szWbookId           = ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID");
            //야드To위치결정방법
            szYD_TO_LOC_DCSN_MTD= ydDaoUtils.paraRecChkNull(getRecord, "YD_TO_LOC_DCSN_MTD");
            //권하지시위치
            szYD_DN_WO_LOC      = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC");
            //야드목표야드구분
            szYD_AIM_YD_GP      = ydDaoUtils.paraRecChkNull(getRecord, "YD_AIM_YD_GP");

            //조회한 크레인 스케줄의 작업활성상태가 check를 한다.
            if (getRecord.getFieldString("YD_WRK_PROG_STAT").equals("1")||
                getRecord.getFieldString("YD_WRK_PROG_STAT").equals("W")) {

                // 적치단 정보 Clear(1개의 크레인스케줄에 잡혀있는 크레인작업재료의 정보를 모두 Check!)
                intRtnVal = this.Y0LdClearYdStklyr(getRecSet, 0);

                //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
                setCrnschRecord = JDTORecordFactory.getInstance().create();
                setCrnschRecord.setField("YD_CRN_SCH_ID",      getParamRecord.getFieldString("YD_CRN_SCH_ID"));
                setCrnschRecord.setField("YD_WRK_PROG_STAT",   getParamRecord.getFieldString("YD_WRK_PROG_STAT"));
                setCrnschRecord.setField("YD_EQP_ID",          getParamRecord.getFieldString("YD_EQP_ID"));
                setCrnschRecord.setField("YD_UP_WR_LOC",       getParamRecord.getFieldString("YD_UP_WR_LOC"));
                setCrnschRecord.setField("YD_UP_WR_LAYER",     getParamRecord.getFieldString("YD_UP_WR_LAYER"));
                setCrnschRecord.setField("YD_UP_WRK_ACT_GP",   getParamRecord.getFieldString("YD_UP_WRK_ACT_GP"));
                setCrnschRecord.setField("YD_UP_WR_XAXIS",     getParamRecord.getFieldString("YD_CRN_XAXIS"));
                setCrnschRecord.setField("YD_UP_WR_YAXIS",     getParamRecord.getFieldString("YD_CRN_YAXIS"));
                setCrnschRecord.setField("YD_UP_WR_ZAXIS",     getParamRecord.getFieldString("YD_CRN_ZAXIS"));
                //권상완료일시
                setCrnschRecord.setField("YD_UP_CMPL_DT",      YdUtils.getCurDate("yyyyMMddHHmmss"));

                intRtnVal = this.Y0UpdYdCrnsch(setCrnschRecord, 0);

                //설비Table의 상태 변경 (권하상태로 변경)
                setCrnschRecord = JDTORecordFactory.getInstance().create();
                setCrnschRecord.setField("YD_EQP_ID",          getParamRecord.getFieldString("YD_EQP_ID"));
                setCrnschRecord.setField("YD_EQP_STAT",        getParamRecord.getFieldString("YD_WRK_PROG_STAT"));

                intRtnVal = ydEqpDao.updYdEqp(setCrnschRecord, 0);
                if(intRtnVal <= 0) {
                     szMsg="<procY0CrnLdWr> updYdEqp 설비상태 UPDATE 처리시 오류 발생.";
                     ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    m_ctx.setRollbackOnly();
                    throw new DAOException(szMsg);
                }

               if(ydDaoUtils.paraRecChkNull(getParamRecord,"YD_UP_WR_LOC").substring(2, 4).equals("PT")||
                  ydDaoUtils.paraRecChkNull(getParamRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TR")){

                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                     *           하차개시 전문 송신 처리 - 구내운송
                     * 업무기준 Desc : 권상실적위치로 적치열에서 운송장비코드를 조회하고 그 코드로 차량스케줄을 조회하여 차량진행상태가
                     *              하차검수 or 하차도착이면 하차개시로 차량스케줄을 업데이트하고 전문을 전송한다.
                     * 기능 추가 : 임춘수
                     * 일자 : 2009.07.15
                     +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                    //권상실적위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
                    recInTemp = JDTORecordFactory.getInstance().create();
                    recInTemp.setField("YD_STK_COL_GP", szYD_UP_WR_LOC.substring(0, 6));
                    rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                    intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsResult, 0);
                    if( intRtnVal <= 0 ) {
                        szMsg = "[권상실적처리 - 하차개시]차량정지위치[" + szYD_UP_WR_LOC.substring(0, 6) + "] 정보가 존재하지 않습니다." ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }else{
                        //조회된 차량정지위치에서 운송장비코드를 가져온다.
                        rsResult.first();
                        recInTemp = rsResult.getRecord();
                        //운송장비코드
                        String  szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(recInTemp, "TRN_EQP_CD");
                        String szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");
                        String szCAR_NO = ydDaoUtils.paraRecChkNull(recInTemp, "CAR_NO");
                        String szCARD_NO = ydDaoUtils.paraRecChkNull(recInTemp, "CARD_NO");
                        //운송장비코드로 차량스케줄조회
                        recInTemp = JDTORecordFactory.getInstance().create();
                        recInTemp.setField("TRN_EQP_CD", szTRN_EQP_CD);
                        recInTemp.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
                        recInTemp.setField("CAR_NO", szCAR_NO);
                        recInTemp.setField("CARD_NO", szCARD_NO);
                        rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//PIDEV_S :병행가동용:PI_YD
                        recInTemp.setField("PI_YD",     "S");
                        intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 27);
                        if( intRtnVal <= 0 ) {
                            szMsg = "[권상실적처리 - 하차개시]차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]에 해당하는 차량스케줄이 존재하지 않습니다." ;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        }else{
                            //차량진행상태를 파악하여 하차검수이거나 하차도착일 때만 하차개시 전문을 송신한다.
                            rsResult.absolute(1);
                            recInTemp = rsResult.getRecord();
                            //차량스케줄ID
                            String szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_SCH_ID");
                            //야드차량진행상태
                            String szYD_CAR_PROG_STAT = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_PROG_STAT");
                            //차량사용구분
                            szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");
                            //하차검수이거나 하차도착일 때 하차개시 전문 송신
                            if( szYD_CAR_PROG_STAT.equals("C") || szYD_CAR_PROG_STAT.equals("B") ) {
                                String szYD_CARUD_ST_DT = YdUtils.getCurDate("yyyyMMddHHmmss");
                                recInTemp = JDTORecordFactory.getInstance().create();
                                recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);                               //차량스케줄ID
                                recInTemp.setField("YD_CAR_PROG_STAT", "D");                                        //차량진행상태
                                recInTemp.setField("YD_CARUD_ST_DT", szYD_CARUD_ST_DT);                             //하차개시일시
                                intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
                                if(intRtnVal <= 0) {
                                    szMsg="[권상실적처리 - 하차개시]차량스케줄에 하차 개시 등록시 Error!! Code : " + intRtnVal;
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    m_ctx.setRollbackOnly();
                                    throw new DAOException(szMsg);
                                }

                                String szYD_GP          = szYD_SCH_CD.substring(0,1);
                                if( szYD_CAR_USE_GP.equals("L") ) {
                                    //하차작업개시 송신 YDTSJ009
                                    recInTemp = JDTORecordFactory.getInstance().create();
                                    recInTemp.setField("MSG_ID",        "YDTSJ009");
                                    recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
                                    recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
                                    recInTemp.setField("YD_GP",         szYD_GP);
                                    recInTemp.setField("YD_CARUD_ST_DT",         szYD_CARUD_ST_DT);
                                    ydDelegate.sendMsg(recInTemp);

                                    szMsg="[권상실적처리 - 하차개시]하차작업개시[YDTSJ009] 송신 완료";
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                }
                            }
                        }
                    }
                    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

                    intRtnVal = this.Y0SetYdCar(getRecSet, 0) ;
                    szMsg = "<procY0CrnLdWr> Y0SetYdCar권상시 차량이송재료 삭제 완료" ;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                }

                szMsg = "[권상실적처리] 상차개시 진입 전 - 권하지시위치[" + szYD_DN_WO_LOC + "], " + "설비구분[" + szYD_DN_WO_LOC.substring(2, 4) + "]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                 *           상차개시 전문 송신 처리 - 구내운송, 출하관리
                 * 업무기준 Desc : 권하지시위치로 적치열에서 운송장비코드를 조회하고 그 코드로 차량스케줄을 조회하여 차량진행상태가
                 *              상차검수 or 상차도착이면 상차개시로 차량스케줄을 업데이트하고 전문을 전송한다.
                 * 기능 추가 : 임춘수
                 * 일자 : 2009.07.15
                 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                //권하지시위치가 차상인 경우 상차개시 전문 송신 처리
                if( szYD_DN_WO_LOC.substring(2, 4).equals("PT") || szYD_DN_WO_LOC.substring(2, 4).equals("TR") ) {
                    //권하지시위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
                    recInTemp = JDTORecordFactory.getInstance().create();
                    recInTemp.setField("YD_STK_COL_GP", szYD_DN_WO_LOC.substring(0, 6));
                    rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                    intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsResult, 0);
                    if( intRtnVal <= 0 ) {
                        szMsg = "[권상실적처리 - 상차개시]차량정지위치[" + szYD_DN_WO_LOC.substring(0, 6) + "] 정보가 존재하지 않습니다." ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }else{
                        //조회된 차량정지위치에서 운송장비코드를 가져온다.
                        rsResult.first();
                        recInTemp = rsResult.getRecord();
                        //운송장비코드
                        String  szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(recInTemp, "TRN_EQP_CD");
                        String szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");
                        String szCAR_NO = ydDaoUtils.paraRecChkNull(recInTemp, "CAR_NO");
                        String szCARD_NO = ydDaoUtils.paraRecChkNull(recInTemp, "CARD_NO");
                        szMsg = "[권상실적처리 - 상차개시]차량정지위치[" + szYD_DN_WO_LOC.substring(0, 6) + "] 정보가 존재하므로 차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]로 차량스케줄 조회" ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        //운송장비코드로 차량스케줄조회
                        recInTemp = JDTORecordFactory.getInstance().create();
                        recInTemp.setField("TRN_EQP_CD", szTRN_EQP_CD);
                        recInTemp.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
                        recInTemp.setField("CAR_NO", szCAR_NO);
                        recInTemp.setField("CARD_NO", szCARD_NO);
                        rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//PIDEV_S :병행가동용:PI_YD
                        recInTemp.setField("PI_YD",     "S");
                        intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 27);
                        if( intRtnVal <= 0 ) {
                            szMsg = "[권상실적처리 - 상차개시]차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]에 해당하는 차량스케줄이 존재하지 않습니다." ;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        }else{
                            //차량진행상태를 파악하여 상차검수이거나 상차도착일 때만 상차개시 전문을 송신한다.
                            rsResult.absolute(1);
                            recInTemp = rsResult.getRecord();
                            //차량스케줄ID
                            String szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_SCH_ID");
                            //야드차량진행상태
                            String szYD_CAR_PROG_STAT = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_PROG_STAT");
                            //차량사용구분
                            szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");

                            szMsg = "[권상실적처리 - 상차개시]차량스케줄 조회 후 차량스케줄ID[" + szYD_CAR_SCH_ID + "], 야드차량진행상태[" + szYD_CAR_PROG_STAT + "], 차량사용구분[" + szYD_CAR_USE_GP + "]" ;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                            //상차검수이거나 상차도착일 때 상차개시 전문 송신
                            if( szYD_CAR_PROG_STAT.equals("3") || szYD_CAR_PROG_STAT.equals("2") ) {
                                /*
                                 * 야드목표야드구분은 작업예약에 등록된 목표야드를 사용한다.
                                 * 수정자 : 임춘수
                                 * 수정일 : 2009.11.03
                                 */
                                YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
                                JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
                                recInTemp = JDTORecordFactory.getInstance().create();
                                recInTemp.setField("YD_WBOOK_ID", szWbookId);
                                intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, outRecSet, 0);

                                if( intRtnVal > 0 ) {
                                    outRecSet.first();
                                    recInTemp = outRecSet.getRecord();
                                    szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recInTemp, "YD_AIM_YD_GP");
                                }
                                //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

                                //야드구분에 따른 개소코드 반환
//                              String szARR_WLOC_CD = YdCommonUtils.getWlocCd(szYD_AIM_YD_GP);
                                //장비코드로 구내운송에서 개소코드 가져 오기
                                String szARR_WLOC_CD = YdCommonUtils.getWlocCd2(szTRN_EQP_CD);

                                //차량스케줄 업데이트 - 상차개시
                                recInTemp = JDTORecordFactory.getInstance().create();
                                recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);                               //차량스케줄ID
                                recInTemp.setField("YD_CAR_PROG_STAT", "4");                                        //차량진행상태
                                recInTemp.setField("YD_EQP_WRK_STAT", "U");                                         //작업상태
                                recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szWbookId);                              //작업예약ID
                                recInTemp.setField("YD_CARLD_ST_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));         //상차개시일시
                                recInTemp.setField("MODIFIER",  "YDSYSTEM");                                        //수정자
                                if( szYD_CAR_USE_GP.equals("L") ) {                 //구내운송
                                    if(!szYD_DN_WO_LOC.substring(0,1).equals(szYD_AIM_YD_GP)){
                                        recInTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD);                               //착지개소코드
                                    }
                                }
                                intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);

                                if(intRtnVal <= 0) {
                                    szMsg="[권상실적처리]차량스케줄에 상차개시일시 등록시 Error!! Code : " + intRtnVal;
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    m_ctx.setRollbackOnly();
                                    throw new DAOException(szMsg);
                                }

                                recInTemp = JDTORecordFactory.getInstance().create();
                                if( szYD_CAR_USE_GP.equals("L") ) {                 //구내운송

                                    //상차작업개시 송신 YDTSJ007 (구내운송 상차개시)
                                    recInTemp.setField("MSG_ID",        "YDTSJ007");
                                    //착지개소코드
                                    recInTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD);

                                    szMsg="[권상실적처리]상차작업개시 송신 YDTSJ007 (구내운송 상차개시) 송신 시작";
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                                }else if( szYD_CAR_USE_GP.equals("G") ){            //출하차량

                                    //상차작업개시 송신 YDDMR009 (외판슬라브출하상차개시)
                                    //PIDEV
//                                  if("Y".equals(sApplyYnPI)) {
                                        recInTemp.setField("MQ_TC_CD",      "M10YDLMJ1073");
                                        szMsg="[권상실적처리]상차작업개시 송신 M10YDLMJ1073 (외판슬라브출하상차개시) 송신 시작";
//                                  } else {
//                                      recInTemp.setField("MSG_ID",        "YDDMR009");
//                                      szMsg="[권상실적처리]상차작업개시 송신 YDDMR009 (외판슬라브출하상차개시) 송신 시작";
//                                  }

                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                }

                                String szYD_GP          = szYD_SCH_CD.substring(0,1);

                                recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
                                recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
                                recInTemp.setField("YD_GP",         szYD_GP);

                                ydDelegate.sendMsg(recInTemp);

                                szMsg="[권상실적처리]상차작업개시 송신 완료";
                                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            }
                        }
                    }
                }
                /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

                //------------------------------------------------------------------
                // 권상 실적시 Flex 실시간 처리
                //------------------------------------------------------------------
                JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
                recFlex.setField("YD_GP",  YdConstant.YD_GP_INTGR_YARD);
                recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
                recFlex.setField("YD_UP_WR_LOC", szYD_UP_WR_LOC);
                 szMsg="Flex 권상 완료 실적 전송";
                 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putYdFlexCrnWrk("", recFlex);
                //------------------------------------------------------------------
            }

            szMsg="권상 완료 실적 처리 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //테스트 임시 방편용 (권하실적처리호출)
            /*
            JDTORecord recInPara = JDTORecordFactory.getInstance().create();
            JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
            recInPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
            intRtnVal = ydCrnschDao.getYdCrnsch(recInPara, outRecSet, 0);
            if(intRtnVal < 0) {
                szMsg="권하실적처리를 위해 크레인스케줄 조회중 Error!!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                szMsg = YdConstant.RETN_CD_FAILURE;
                throw new DAOException("<procY0CrnLdWr> getYdCrnsch " + szMsg);
            }
            outRecSet.absolute(1);
            JDTORecord recOutPara = JDTORecordFactory.getInstance().create();
            recOutPara.setRecord(outRecSet.getRecord());
            */
        }catch(Exception e) {
            szMsg="Error :  "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            m_ctx.setRollbackOnly();
            throw new DAOException(szMsg);
        }//end of try~catch
        return YdConstant.RETN_CD_SUCCESS;
    }// end of procY0CrnLdWr()

    /**
     * 오퍼레이션명 : 권상 파라미터 체크
     *
     * @param  ● msgRecord, outRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y0ParamCheck (JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
        JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        String szMsg                        = "" ;
        String szMethodName                 = "Y0ParamCheck";
        int intRtnVal = 0 ;

        try{

            //======================================================================================================
            // LOG 출력 - 그냥 테스트용으로 출력 레코드값체크하는 코드는 제외하고 나중에 삭제
            szMsg = "[1] 야드설비ID : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[2] 야드설비작업Mode : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[3] 야드작업진행상태 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[4] 야드스케쥴코드 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[5] 야드크레인스케쥴ID : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[6] 야드권상실적위치 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LOC");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[7] 야드권상실적단 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LAYER");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[8] 야드크레인X축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[9] 야드크레인Y축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[10] 야드크레인Z축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_ZAXIS");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            //======================================================================================================

            setRecord.setField("YD_CRN_SCH_ID"          , ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID")) ;
            setRecord.setField("YD_SCH_CD"              , ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD")) ;
            setRecord.setField("MSG_ID"                 , ydDaoUtils.paraRecChkNull(msgRecord,"MSG_ID")) ;
            setRecord.setField("DATE"                   , ydDaoUtils.paraRecChkNull(msgRecord,"DATE")) ;
            setRecord.setField("TIME"                   , ydDaoUtils.paraRecChkNull(msgRecord,"TIME")) ;
            setRecord.setField("MSG_GP"                 , ydDaoUtils.paraRecChkNull(msgRecord,"MSG_GP")) ;
            setRecord.setField("YD_EQP_ID"              , ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID")) ;
            setRecord.setField("YD_EQP_WRK_MODE"        , ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE")) ;
            setRecord.setField("YD_WRK_PROG_STAT"       , ydDaoUtils.paraRecChkNull(msgRecord,"YD_WRK_PROG_STAT")) ;
            setRecord.setField("YD_CRN_XAXIS"           , ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_XAXIS")) ;
            setRecord.setField("YD_CRN_YAXIS"           , ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_YAXIS")) ;
            setRecord.setField("YD_CRN_ZAXIS"           , ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_ZAXIS")) ;
            if(intGp == 0){
                setRecord.setField("YD_UP_WR_LOC"               , ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LOC")) ;
                setRecord.setField("YD_UP_WR_LAYER"             , ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LAYER")) ;

                //전문 송신지 위치 Check                :AUTO MANUAL BACKUP구분
                if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("1")) {
                    setRecord.setField("YD_UP_WRK_ACT_GP", "A") ;
                }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("9")) {
                    setRecord.setField("YD_UP_WRK_ACT_GP", "B") ;
                }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("0")) {
                    setRecord.setField("YD_UP_WRK_ACT_GP", "M") ;
                }
            }

            if(intGp == 1){
                setRecord.setField("YD_DN_WR_LOC"               , ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LOC")) ;
                setRecord.setField("YD_DN_WR_LAYER"             , ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LAYER")) ;

                //전문 송신지 위치 Check                :AUTO MANUAL BACKUP구분
                if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("1")) {
                    setRecord.setField("YD_DN_WRK_ACT_GP", "A") ;
                }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("9")) {
                    setRecord.setField("YD_DN_WRK_ACT_GP", "B") ;
                }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("0")) {
                    setRecord.setField("YD_DN_WRK_ACT_GP", "M") ;
                }
            }

            outRecord.addRecord(setRecord) ;

        }catch(Exception e){
            System.out.println("Error : "+ e.getLocalizedMessage());
            throw new JDTOException("<Y0ParamCheck> " + szMsg);
        }//end of try~catch

        intRtnVal = 1 ;
        return intRtnVal;

    }//end of Y0ParamCheck()

    /**
     * 오퍼레이션명 : 크레인스케줄 Update
     *
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y0UpdYdCrnsch(JDTORecord msgRecord, int intGp) throws JDTOException {
        YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

        int intRtnVal = 0 ;

        String szMethodName = "Y0UpdYdCrnsch";
        String szMsg        = "";

        try{
            intRtnVal = ydCrnschDao.updYdCrnsch(msgRecord, intGp);
            switch (intRtnVal) {
                case 0  :
                    szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    throw new DAOException("<Y0UpdYdCrnsch> updYdCrnsch " + szMsg);
                case -1 :
                    szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    throw new DAOException("<Y0UpdYdCrnsch> updYdCrnsch " + szMsg);
                case -2 :
                    szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    throw new DAOException("<Y0UpdYdCrnsch> updYdCrnsch " + szMsg);
                case -3 :
                    szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    throw new DAOException("<Y0UpdYdCrnsch> updYdCrnsch " + szMsg);
            }
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException("<Y0UpdYdCrnsch> " + szMsg);
        }//end of try~catch

        return intRtnVal ;

    }// end of Y0UpdYdCrnsch

    /**
     * 오퍼레이션명 : 크레인스케줄 Select
     *
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y0GetYdCrnsch (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {

        YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

        int intRtnVal           = 0 ;

        String szMethodName = "Y0GetYdCrnsch";
        String szMsg        = "";

        try{

            intRtnVal = ydCrnschDao.getYdCrnsch(msgRecord, getRecSet, intGp);
            switch (intRtnVal) {
            case 0  :
                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                //return intRtnVal = -1;
                throw new JDTOException("<Y0GetYdCrnsch> getYdCrnsch " + szMsg);
            case -2 :
                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                //return intRtnVal = -1;
                throw new JDTOException("<Y0GetYdCrnsch> getYdCrnsch " + szMsg);
        }
            outRecSet.addAll(getRecSet);

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException("<Y0GetYdCrnsch> " + szMsg);
        }//end of try~catch

        return intRtnVal ;

    }//end of Y0GetYdCrnsch()

    /**
     * 오퍼레이션명 : 적치단 Clear
     *
     * @param  ● getRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y0LdClearYdStklyr (JDTORecordSet getRecSet, int intGp) throws JDTOException {
        YdStkBedDao ydStkBedDao = new YdStkBedDao();

        JDTORecord getRecord = JDTORecordFactory.getInstance().create();
        JDTORecord setRecord = JDTORecordFactory.getInstance().create();

        int intRtnVal       = 0;
        String szMsg        = "";
        String szMethodName = "Y0LdClearYdStklyr";
        String szYD_TO_LOC_DCSN_MTD = null;
        String szYD_STK_COL_GP = null;
        String szYD_STK_BED_NO = null;
        String szYD_UP_WR_LOC  = null;
        String szYD_UP_WR_LAYER = null;

        try{
            int rowsize = getRecSet.size();
            getRecSet.first();
            getRecord = getRecSet.getRecord();

            szYD_STK_COL_GP      = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(0,6);
            szYD_STK_BED_NO      = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(6,8);
            szYD_UP_WR_LOC       = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC");
            szYD_UP_WR_LAYER     = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LAYER");
            szYD_TO_LOC_DCSN_MTD = ydDaoUtils.paraRecChkNull(getRecord,"YD_TO_LOC_DCSN_MTD");

            for(int i=0; i<rowsize ; i++){
                //권상 지시위치 Clear

                setRecord = JDTORecordFactory.getInstance().create();
                setRecord.setField("YD_STK_COL_GP",       szYD_STK_COL_GP);
                setRecord.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);

                //적치단 설정
                String szStkLyr = ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, i);
                setRecord.setField("YD_STK_LYR_NO",       szStkLyr) ;
                setRecord.setField("YD_STK_LYR_MTL_STAT", "E");
                setRecord.setField("STL_NO",              "");

                intRtnVal = this.Y0UpdYdStklyr(setRecord, 0);  //적치단의 재료정보 Clear

                /*
                 * 2010.05.04 윤재광 수정
                 * 아래의 소스 의미가 없슴. 주석처리
                 */
                //getRecSet.next();
                //getRecord = getRecSet.getRecord();
            } //end of for

            /*  2009.10.05 김진욱 수정
             *  크레인 작업 편성시 주작업재료이면서 보조작업재료일때 해당 ToBed를 완산상태로 바꾸는 것을 순서모음 작업할때 완산상태를 풀어주도록한다.
             *  적치Bed의 입출고상태를 완산("F")에서 입출고가능("E")로 변경한다.
             */
            if(szYD_TO_LOC_DCSN_MTD.equals("R")){
                setRecord = JDTORecordFactory.getInstance().create();
                setRecord.setField("YD_STK_COL_GP",       szYD_STK_COL_GP);
                setRecord.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);
                setRecord.setField("YD_STK_BED_WHIO_STAT", "E");
                intRtnVal = ydStkBedDao.updYdStkbed(setRecord, 0);
                if(intRtnVal <= 0) {
                    szMsg = "[통합야드 크레인 권상실적등록 중] 권상위치 입출고상태 변경 Error!!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        intRtnVal = 1 ;
        return  intRtnVal;
    }//end of Y0LdClearYdStklyr()

    /**
     * 오퍼레이션명 : 적치단 Update
     *
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y0UpdYdStklyr (JDTORecord msgRecord, int intGp) throws JDTOException {
        YdStkLyrDao ydStklyrDao = new YdStkLyrDao();

        int intRtnVal = 0 ;

        String szMsg        = "";
        String szMethodName = "Y0UpdYdStklyr";

        try{

            intRtnVal = ydStklyrDao.updYdStklyr(msgRecord, intGp);  //적치단의 재료정보 Clear
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                    /*
                     * 모음작업시에는 단이 존재하지 않을 수 있으므로 적치단이 존재하지 않더라도
                     * 업무는 진행이 되도록 아래 부분을 수정
                     * 수정자 : 임춘수
                     * 수정일 : 2009.09.21
                     */
                    szMsg="적치단이 존재하지 않습니다. - 모음작업 시 오류발생 가능 ";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    intRtnVal = 1;
                }else if(intRtnVal == -1) {
                    szMsg="duplicate data,";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -2) {
                    szMsg="parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -3){
                    szMsg="execution failed";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal ;

    }//end of Y0UpdYdStklyr


    /**
     * 오퍼레이션명 : 차량 Setting
     *
     * @param inRecordSet, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y0SetYdCar (JDTORecordSet inRecordSet, int intGp) throws JDTOException{
        YdCarSchDao ydCarschDao = new YdCarSchDao();

        //Data Setting
        JDTORecord    setRecord             = JDTORecordFactory.getInstance().create();

        //data를 받음
        JDTORecord    getRecord             = JDTORecordFactory.getInstance().create();

        //차량 스케줄 레코드셋의 레코드값을 받음
        JDTORecord    getTcarRecord         = JDTORecordFactory.getInstance().create();

        //차량 스케줄의 레코드셋
        JDTORecordSet outRecSet             = JDTORecordFactory.getInstance().createRecordSet("temp");

        String szMethodName                 = "Y0SetYdCar" ;
        String szMsg                        = "" ;
        long lngYD_MTL_WT                  = 0;
        int  intYD_MTL_SH                  = 0;
        long lngYD_EQP_WRK_WT              = 0;
        int  intYD_EQP_WRK_SH              = 0;

        int intRtnVal = 0 ;

        //차량 스케줄 ID
        String szYD_CAR_SCH_ID = "" ;

        try{

            // 크레인스케줄 Data
            inRecordSet.first();
            getRecord = inRecordSet.getRecord();


            //하차 작업 예약 ID Setting
            if(intGp == 0) {
                setRecord.setField("YD_CARUD_WRK_BOOK_ID", getRecord.getFieldString("YD_WBOOK_ID")) ;
                setRecord.setField("YD_CARLD_WRK_BOOK_ID", "") ;

            //상차 작업 예약 ID Setting
            }else if(intGp == 1) {
                setRecord.setField("YD_CARLD_WRK_BOOK_ID", getRecord.getFieldString("YD_WBOOK_ID")) ;
                setRecord.setField("YD_CARUD_WRK_BOOK_ID", "") ;
            }


            // 상하차 작업예약 ID로 차량스케줄 조회
//PIDEV_S :병행가동용:PI_YD
            setRecord.setField("PI_YD",     "S");
            intRtnVal = this.Y0GetYdCarsch(setRecord, outRecSet, 3) ;
            if (intRtnVal <= 0){
                szMsg = "차량에서 권상작업 처리시 차량스케쥴 정보 오류발생.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                throw new  JDTOException(szMsg);
            }

            // 차량스케줄 Data
            outRecSet.first() ;
            getTcarRecord = outRecSet.getRecord() ;
            // 차량스케줄 ID를 추출한다
            szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_CAR_SCH_ID");
            // 차량스케줄의 작업재료 중량 및 매수를 추출한다.
            lngYD_EQP_WRK_WT = ydDaoUtils.paraRecChkNullLong(getTcarRecord, "YD_EQP_WRK_WT");
            intYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNullInt(getTcarRecord, "YD_EQP_WRK_SH");

            //setRecord 초기화
            setRecord           = JDTORecordFactory.getInstance().create();
            int szRowSize = inRecordSet.size();

            // 권상한 재료만큼 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)
            for(int i = 0; i < szRowSize; i++){

                //재료매수
                intYD_MTL_SH = i + 1;

                // 차량스케줄 ID로 차량이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)
                setRecord.setField("YD_CAR_SCH_ID",         szYD_CAR_SCH_ID);
                setRecord.setField("STL_NO",                ydDaoUtils.paraRecChkNull(getRecord, "STL_NO"));

                //차량 이송재료 등록 (하차 )
                if(intGp == 0) {
                    setRecord.setField("DEL_YN",                "Y");
                    intRtnVal = this.Y0UpdCarftmvmtl(setRecord, 0) ;

                //차량 이송재료 등록 (상차 )
                }else if(intGp == 1) {
                    setRecord.setField("DEL_YN",                "N");
                    setRecord.setField("YD_STK_BED_NO",         ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(6,8)) ;
                    setRecord.setField("YD_STK_LYR_NO",         ydDaoUtils.stringPlusInt(ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER"), i)) ;
                    intRtnVal = this.Y0InsYdCarftmvmtl(setRecord) ;
                }
                //재료중량
                lngYD_MTL_WT = lngYD_MTL_WT + ydDaoUtils.paraRecChkNullLong(getRecord, "YD_MTL_WT");
                inRecordSet.next() ;
                getRecord = inRecordSet.getRecord();
            }
            if(intGp == 0) {
                //차량스케줄에 등록한다.
                lngYD_EQP_WRK_WT = lngYD_EQP_WRK_WT - lngYD_MTL_WT;
                intYD_EQP_WRK_SH = intYD_EQP_WRK_SH - intYD_MTL_SH;
                //setRecord 초기화
                setRecord           = JDTORecordFactory.getInstance().create();
                setRecord.setField("YD_CAR_SCH_ID",         szYD_CAR_SCH_ID);
                setRecord.setField("YD_EQP_WRK_WT",         "" + lngYD_EQP_WRK_WT);
                setRecord.setField("YD_EQP_WRK_SH",         "" + intYD_EQP_WRK_SH);

                intRtnVal = ydCarschDao.updYdCarsch(setRecord, 0);
            }


        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new  JDTOException(szMsg);
        }//end of try~catch

        return 1 ;

    }//end of Y0SetYdCar()

    /**
     * 오퍼레이션명 : 차량 스케줄 Select
     *
     * @param msgRecord, outRecset, intGp(1:상하차)
     * @return intRtnVal
     * @throws
     */
    public int Y0GetYdCarsch (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
        YdCarSchDao ydCarschDao = new YdCarSchDao();
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

        int intRtnVal           = 0 ;

        String szMethodName = "Y0GetYdCarsch";
        String szMsg        = "";

        try{

            intRtnVal = ydCarschDao.getYdCarsch(msgRecord, getRecSet, intGp);

            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                }else if(intRtnVal == -2) {
                    szMsg="parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                if (intRtnVal <= 0) return intRtnVal = -2;
            }
            outRecset.addAll(getRecSet)  ;

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -2;
        }//end of try~catch

        return intRtnVal ;

    }//end of Y0GetYdCarsch

    /**
     * 오퍼레이션명 : 차량 이송재료 Update
     *
     * @param inRecordSet, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y0UpdCarftmvmtl (JDTORecord inRecord, int intGp) throws JDTOException {
        YdCarFtmvMtlDao ydCarftmvmtlDao   = new YdCarFtmvMtlDao();

        int intRtnVal = 0 ;

        String szMethodName = "Y0UpdCarftmvmtl";
        String szMsg        = "";

        try{

            intRtnVal = ydCarftmvmtlDao.updYdCarftmvmtl(inRecord, intGp) ;
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="data not found!!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                }else if(intRtnVal == -1) {
                    szMsg="duplicate data,";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -2) {
                    szMsg="parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -3){
                    szMsg="execution failed";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return intRtnVal = -1;
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal ;

    }//end of Y0UpdCarftmvmtl

    /**
     * 오퍼레이션명 : 차량이송재료 Insert
     *
     * @param msgRecord
     * @return intRtnVal
     * @throws
     */
    public int Y0InsYdCarftmvmtl(JDTORecord msgRecord)throws JDTOException{
        YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();

        int intRtnVal = 0 ;

        String szMethodName = "Y0InsYdCarftmvmtl";
        String szMsg        = "";

        try{
            intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(msgRecord);
            if(intRtnVal == -2) {
                szMsg="parameter error";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return intRtnVal = -1;
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal = 1;
    }//end of Y0InsYdCarftmvmtl

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 오퍼레이션명 : C연주 크레인 작업지시 (Y1YDL007, YDYDJ640)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws JDTOException
     */
    public String procY1CrnWrkOrdReq(JDTORecord msgRecord)throws JDTOException  {

        YdEqpDao     ydEqpDao     = new YdEqpDao();
        /*
         * 동일크레인 명령선택시 최초 Status 관리방안.
         * - 설비ID의 수정일자 UPDATE - DB ROW LOCK
         * - 작업지시는 큐방식으로 호출하기 때문에 필요없어짐.(막음)
         */
        //int SeqNo = ydEqpDao.updYdEqpDirect(msgRecord, 4);

        YdDelegate   ydDelegate   = new YdDelegate();
        YdCrnSchDao  YdCrnSchDao  = new YdCrnSchDao();
        YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();

        JDTORecord recCrnSch = JDTORecordFactory.getInstance().create();
        JDTORecord recInPara = null;
        JDTORecord recOutTemp = null;
        JDTORecord recIntTemp = null;

        JDTORecordSet rsCrnSch = JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet rsWrkBook = null;

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "procY1CrnWrkOrdReq";
        String szOperationName          = "C연주크레인작업지시";

        String szEqpId                  = "";
        String szWrkProgStat            = "";
        String szYD_GP                  = "";

        //스케쥴코드
        String szYD_SCH_CD              = "";

        boolean blnRtnVal               = true;

        String szRtnMsg                 = null;

        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null || szRcvTcCode.equals("")){
            szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return YdConstant.RETN_CD_TC_ERROR;
        }

        try{
            //=============================================================
            // Log 테이블 등록
            //=============================================================
            szMsg = "[C연주정정] 크레인작업지시 수신";
            ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            //======================================================================================================
            // LOG 출력 - 그냥 테스트용으로 출력 레코드값체크하는 코드는 제외하고 나중에 삭제
            szMsg = "[1] 야드설비ID : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[2] 야드설비작업Mode : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[3] 야드작업진행상태 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[4] 야드스케쥴코드 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[5] 야드크레인스케쥴ID : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[6] 야드크레인X축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[7] 야드크레인Y축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            //======================================================================================================

            szEqpId     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            szYD_GP     = szEqpId.substring(0,1);

            //------------------------------------------------------------------------------------------------------
            // 야드설비상태 Check       수신받은  야드설비Id로 설비Table를 조회하여 야드설비상태를 Check하고 고장이면 return
            //------------------------------------------------------------------------------------------------------

            szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            JDTORecordSet rsCrnInfo = JDTORecordFactory.getInstance().createRecordSet("");
            JDTORecord recCrnInfo   = JDTORecordFactory.getInstance().create();

            szRtnMsg = this.eqpStatCheck(szEqpId, rsCrnInfo);

            if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
                szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 시 오류발생 - 메세지 : " + szRtnMsg;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return szRtnMsg;
            }

            szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 완료 - 메세지 : " + szRtnMsg;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //------------------------------------------------------------------------------------------------------

            // 스케줄 기준 체크
            szYD_SCH_CD =  ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
            if( !szYD_SCH_CD.equals("") ) {
                blnRtnVal = YdCommonUtils.chkGetSchRule(szYD_SCH_CD, rsResult);
                if( !blnRtnVal ) return YdConstant.RETN_CD_FAILURE;
                // 레코드 추출
                rsResult.first();
                JDTORecord recPara = rsResult.getRecord();
                // 스케줄 금지 유무
                String szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,"YD_SCH_PROH_EXN");

                // 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
                if (szYD_SCH_PROH_EXN.equals("Y")) {
                    szMsg = "크레인 작업지시 시 스케쥴코드(" + szYD_SCH_CD + ")에 대한 스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return YdConstant.RETN_CRN_SCH_PROH;
                }
            }

            //야드 작업 진행상태를 check한다.
            szWrkProgStat = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");

            /*
             * 2011.05.25 윤재광 현 크레인상태를 넘겨줌.
            //레코드 추출
            rsCrnInfo.first();
            recCrnInfo = rsCrnInfo.getRecord();

            //야드 작업 진행상태를 check한다.
            szWrkProgStat = ydDaoUtils.paraRecChkNull(recCrnInfo, "YD_EQP_STAT");
            */

            //야드 작업 진행상태가 1,3인 경우 (작업지시를 재요구 하는 경우 사용한다.)
            if (szWrkProgStat.equals("1") ||
                szWrkProgStat.equals("2") ||
                szWrkProgStat.equals("3")){

                szMsg="야드 작업 진행상태가 1,3인 경우";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                intRtnVal = this.Y1ChkWrkProgStat(msgRecord, rsCrnSch);

                if( intRtnVal == 0 ) {
                    //크레인 스케줄이 없다면 없다는 메시지를 L2에 전송해야한다.

                    return YdConstant.RETN_CRN_NO_SCH;
                }else if(intRtnVal == -1) {
                    return YdConstant.RETN_CD_FAILURE;
                }else{
                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                     * 업무기준 : C연주슬라브야드와 통합야드가 같은 로직을 사용하므로 C연주슬라브야드와 관련된 L2로만 전송 필요
                     * 수정자 : 임춘수
                     * 일자 : 2009.06.29
                     ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                    if( szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD)  ||        //C연주슬라브야드
                        szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD) ) {     //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
                        //현재크레인이 작업중인 스케줄의 값을 다시 재전송한다.
                        rsCrnSch.absolute(1);
                        recOutTemp = JDTORecordFactory.getInstance().create();
                        recOutTemp.setRecord(rsCrnSch.getRecord());

                        recInPara = JDTORecordFactory.getInstance().create();
                        //작업지시 전문 전송 data setup
                        recInPara.setField("MSG_ID", "YDY1L004");
                        //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
                        if (szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD)) {
                            recInPara.setField("MSG_ID", "YDE7L004");
                        }
                        else {
                            recInPara.setField("MSG_ID", "YDY1L004");
                        }
                        recInPara.setField("YD_CRN_SCH_ID",    ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CRN_SCH_ID"));
                        recInPara.setField("YD_WRK_PROG_STAT", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PROG_STAT"));
                        recInPara.setField("YD_SCH_CD",        ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"));
                        recInPara.setField("YD_GP",            ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP"));
                        recInPara.setField("MODIFIER", "YDSYSTEM");

                        //20090925 김진욱 추가 : 작업진행중인 작업을 재전송하는 경우는  MSG_GP 값을 'U' UPDATE로 설정해서 보낸다.
                        recInPara.setField("MSG_GP", "U");
                        ydDelegate.sendMsg(recInPara);
                        szMsg = "[크레인 작업지시]현재크레인이 작업중인 스케줄을 재전송 - C연주슬라브야드";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }else{      //통합야드
                        szMsg = "[크레인 작업지시]현재크레인이 작업중인 스케줄을 재전송처리않고 로그만 기록함 - 통합야드";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }
                    return YdConstant.RETN_CD_SUCCESS;
                }


            //야드 작업 진행 상태가 'W'인경우 (작업이 없을때 요구)
            }else if(szWrkProgStat.equals("W")){

                szMsg="야드 작업 진행 상태가 'W'인경우";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                intRtnVal = this.Y1ChkWrkProgStatW(msgRecord, rsCrnSch);
                if(intRtnVal == 0) {
                    szMsg="크레인 스케줄이 조회되지 않습니다 [intRtnVal = " + intRtnVal + "]";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return YdConstant.RETN_CRN_NO_SCH;
                }else if(intRtnVal < 0) {
                    szMsg="크레인스케줄 조회 중 에러 발생  [intRtnVal = " + intRtnVal + "]";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return YdConstant.RETN_CD_FAILURE;
                }

            //야드 작업 진행 상태가 '4'인 경우 현재 진행중인 작업이 있을 경우 (현재 진행중인 작업이 있을 경우 해당작업을 호출한다.스케줄 코드로 조회, 조회한 data가 없다면 스케줄우선순위가 빠르고 크레인스케줄id가 가장빠른 작업을 보내준다.)
            }else if(szWrkProgStat.equals("4")) {

                szMsg="야드 작업 진행 상태가 '4'인 경우 (권하실적처리에서 호출)";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                intRtnVal = this.Y1ChkWrkProgStat4(msgRecord, rsCrnSch);

                //더이상 크레인 스케줄을 찾지 못했을 경우 설비상태를 W <--idle로 변경하고 0으로 리턴, 에러인 경우  -1로 리턴.. 크레인 스케줄 호출부분이 아직 없기때문에 종료처리...추후 변경 0일경우는 크레인 스케줄 호출..
                if(intRtnVal <= 0) {
                    szMsg="더이상 크레인 스케줄을 찾지못했거나 Error가 발생했을 경우 작업예약을 조회한다. intRtnVal : " + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    //작업예약조회
                    recInPara = JDTORecordFactory.getInstance().create();
                    recInPara.setField("YD_EQP_ID", szEqpId);
                    rsWrkBook = JDTORecordFactory.getInstance().createRecordSet("");
                    intRtnVal = ydWrkbookDao.getYdWrkbook(recInPara, rsWrkBook, 4);
                    if(intRtnVal > 0) {
                        //검색된 작업예약을 크레인스케줄을 생성한다. 생성하면 설비상태값에 따라서 작업지시를 내려보내도록되어있다.
                        szMsg="현재 크레인(" + szEqpId + ")로 작업할 수 있는 작업예약을 조회하였습니다.  크레인스케줄Main호출";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        //크레인 스케줄 호출 (설비id,스케줄코드);
                        rsWrkBook.absolute(1);
                        recOutTemp = JDTORecordFactory.getInstance().create();
                        recOutTemp.setRecord(rsWrkBook.getRecord());

                        recInPara = JDTORecordFactory.getInstance().create();

                        recInPara.setField("MSG_ID", "YDYDJ500");
                        recInPara.setField("YD_EQP_ID", szEqpId);
                        recInPara.setField("YD_SCH_CD", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"));
                        recInPara.setField("YD_WBOOK_ID_YJK", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID")); //CHITO 2011.03.30 추가

                        //크레인 스케줄 호출 메세지 전송
                        ydDelegate.sendMsg(recInPara);
                        //다음 크레인스케줄이 생성되는 중이기때문에 현재상태에서는 작업지시를 내려보내지않고 리턴
                        return YdConstant.RETN_CD_SUCCESS;
                    }

                    szMsg="현재 크레인(" + szEqpId + ")로 작업할 수 있는 작업예약이 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                     * 업무기준 : 자동준비작업 기능 호출
                     * 수정자 : 윤재광
                     * 일자 : 2010.09.29
                     ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                    if( szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD)  ||
                        szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD) ) {  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY

                        recInPara = JDTORecordFactory.getInstance().create();
                        recInPara.setField("MSG_ID", "YDYDJ294");
                        recInPara.setField("YD_EQP_ID", szEqpId);

                        //크레인 스케줄 호출 메세지 전송
                        ydDelegate.sendMsg(recInPara);

                        //EJBConnector ydEjbCon = new EJBConnector("default", this);
                        //ydEjbCon.trx("IssueWrkDmdFaEJB", "rcvAutoWorkLotComp", recInPara);

                        szMsg = "[크레인 작업지시]C연주 자동준비작업 기능 호출";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }else{
                        szMsg = "[크레인 작업지시]자동작업호출 없음 - 통합야드";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }
                    //다음 크레인스케줄이 생성되는 중이기때문에 현재상태에서는 작업지시를 내려보내지않고 리턴
                    return YdConstant.RETN_CRN_NO_WRK;

                }else{
                    //다음크레인 작업을 찾았을경우 작업지시상태로 변경
                    recCrnSch = JDTORecordFactory.getInstance().create();
                    recCrnSch.setRecord(rsCrnSch.getRecord(0));

                    String sSchYdWrkProgStat    = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WRK_PROG_STAT");
                    String sCrnYdWrkProgStat    = "";

                    if( sSchYdWrkProgStat.equals("") || sSchYdWrkProgStat.equals("W") || sSchYdWrkProgStat.equals("1") ) {
                        sCrnYdWrkProgStat   = "1";
                    }else{
                        sCrnYdWrkProgStat   = sSchYdWrkProgStat;
                    }
                    szMsg=" 윤재광 윤재광 : data ==========================="+sCrnYdWrkProgStat;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);

                    recIntTemp = JDTORecordFactory.getInstance().create();
                    recIntTemp.setField("YD_EQP_ID", szEqpId);
                    recIntTemp.setField("YD_EQP_STAT", sCrnYdWrkProgStat);
                    intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);

                    if(intRtnVal <= 0) {
                        if(intRtnVal == 0) {
                            szMsg=" Y1ChkWrkProgStatW updYdEqp : data not found";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                        }else if(intRtnVal == -1) {
                            szMsg=" Y1ChkWrkProgStatW updYdEqp : duplicate data,";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        }else if(intRtnVal == -2) {
                            szMsg=" Y1ChkWrkProgStatW updYdEqp : parameter error";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        }else if(intRtnVal == -3){
                            szMsg=" Y1ChkWrkProgStatW updYdEqp : execution failed";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        }
                    }
                }
            }

            szMsg="[크레인 작업지시] rsCrnSch 1 : " + rsCrnSch;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            if( rsCrnSch != null ) {
                szMsg="[크레인 작업지시] rsCrnSch.size() : " + rsCrnSch.size();
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            }
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //L2로 전송 ( 전송을 하진 않고 Consol창에 보여준다. )
            for(int Loop_i = 1; Loop_i <= rsCrnSch.size(); Loop_i++) {
                rsCrnSch.absolute(Loop_i);
                recCrnSch = JDTORecordFactory.getInstance().create();
                recCrnSch.setRecord(rsCrnSch.getRecord());

                if(ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC").equals("")) {
                    szMsg="[크레인 작업지시] 크레인스케줄 보기 1 : " + szMsg;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    continue;
                }else{
                    szMsg="[크레인 작업지시] 크레인스케줄 보기 2 : " + szMsg;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    break;
                }
            }

            recCrnSch = JDTORecordFactory.getInstance().create();
            recCrnSch.setRecord(rsCrnSch.getRecord(0));

            String szYD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WRK_PROG_STAT");

            szMsg="[크레인 작업지시] rsCrnSch : " + rsCrnSch;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            recInPara = JDTORecordFactory.getInstance().create();
            //작업지시 전문 전송 data setup
            //recInPara.setField("MSG_ID", "YDY1L004");
            //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
            if (szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD)) {
                recInPara.setField("MSG_ID", "YDE7L004");
            }
            else {
                recInPara.setField("MSG_ID", "YDY1L004");
            }
            recInPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID"));

            if( szYD_WRK_PROG_STAT.equals("") || szYD_WRK_PROG_STAT.equals("W") || szYD_WRK_PROG_STAT.equals("1")) {
                recInPara.setField("YD_WRK_PROG_STAT", "1");
            }else{
                recInPara.setField("YD_WRK_PROG_STAT", szYD_WRK_PROG_STAT);
            }

            recInPara.setField("YD_WORD_DT",      YdUtils.getCurDate("yyyyMMddHHmmss"));
            recInPara.setField("MODIFIER", "YDSYSTEM");

            if (szWrkProgStat.equals("1") || szWrkProgStat.equals("2") || szWrkProgStat.equals("3")){
                recInPara.setField("MSG_GP", "U");
            }

            szMsg="[크레인 작업지시] 작업지시 전문 전송 : " + recInPara;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //크레인스케줄의 작업진행 상태를 권상지시로 변경
//          intRtnVal = YdCrnSchDao.updYdCrnsch(recInPara, 0);
            /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnWrkProgStat*/
            intRtnVal = YdCrnSchDao.updYdCrnschDelay(recInPara, 302);
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="procY1CrnWrkOrdReq updYdCrnsch : data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                }else if(intRtnVal == -2) {
                    szMsg="procY1CrnWrkOrdReq updYdCrnsch : parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else{
                    szMsg="procY1CrnWrkOrdReq updYdCrnsch : execution failed";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return YdConstant.RETN_CD_FAILURE;
            }

             // Flex(실시간작업을위함)
            JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
            //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
            if( szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD)) {
                recFlex.setField("YD_GP",  YdConstant.YD_GP_PORT_SLAB_YARD);
            }
            else {
                recFlex.setField("YD_GP",  YdConstant.YD_GP_C_SLAB_YARD);
            }
            recFlex.setField("YD_EQP_ID", szEqpId);
            ydUtils.putYdFlexCrnWrk("", recFlex);

            recInPara.setField("YD_SCH_CD", ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD"));
            recInPara.setField("YD_GP", ydDaoUtils.paraRecChkNull(recCrnSch, "YD_GP"));

            /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
             * 업무기준 : C연주슬라브야드와 통합야드가 같은 로직을 사용하므로 C연주슬라브야드와 관련된 L2로만 전송 필요
             * 수정자 : 임춘수
             * 일자 : 2009.06.29
             ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            if( szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD) ||     //C연주슬라브야드
                szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD) ) { //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
                //작업지시 메세지 전송
                ydDelegate.sendMsg(recInPara);
                szMsg = "[크레인 작업지시]크레인 작업지시 메세지 전송 완료 - C연주슬라브야드";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            }else{                                                      //통합야드
                szMsg = "[크레인 작업지시]크레인 작업지시 메세지 전송하지 않음[L2가 존재하지 않음] - 통합야드";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            }

        }catch(Exception e){
            szMsg="Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return YdConstant.RETN_CD_FAILURE;
        }

        szMsg="크레인 작업지시("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return YdConstant.RETN_CD_SUCCESS;

    } //end of procY1CrnWrkOrdReq()

    /**
     * 오퍼레이션명 : 크레인 작업지시(작업지시를 재요구 하는 경우 사용한다.)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public int Y1ChkWrkProgStat(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws JDTOException  {
        YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
        YdEqpDao    ydEqpDao    = new YdEqpDao();

        JDTORecordSet rsResult          = null;
        JDTORecord recInTemp            = null;
        JDTORecord recOutTemp           = null;

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y1ChkWrkProgStat";
        String szCrnSchId               = "";

        String szQuery                  = "";

        String szYD_EQP_ID              = "";
        String szWrkProgStat            = "";
        String szYdEqpStat              = "";


        try{
//          //파라미터 중 스케줄ID가 있다 스케줄 ID로 크레인 스케줄을 조회하여 작업예약 ID를 조회한다.
//          szCrnSchId = ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID");
//
//          rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//          intRtnVal = YdCrnSchDao.getYdCrnsch(msgRecord, rsResult, 0);
//          if(intRtnVal <= 0) {
//              szMsg="크레인 스케줄 조회중 Error";
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//          }


            //작업진행상태
            szWrkProgStat = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");


            //설비Table를 조회하여 설비상태가 '1 : 권상지시'또는 '3 : 권하지시'이라면 크레인 스케줄 Table에서 야드작업진행 상태가 1,2,3인 것만 찾는다. 1건만 나와야 정상이고 1건이 아니라면 Error처리
            rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
            intRtnVal = ydEqpDao.getYdEqp(msgRecord, rsResult, 0);
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="Y1ChkWrkProgStat getYdEqp : data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                }else if(intRtnVal == -2) {
                    szMsg="Y1ChkWrkProgStat getYdEqp : parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return intRtnVal = -1;
            }

            recOutTemp = JDTORecordFactory.getInstance().create();
            recOutTemp.setRecord(rsResult.getRecord(0));
            szYdEqpStat = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_STAT");
            //설비상태가 1또는 3인경우
            if(szYdEqpStat.equals("1") || szYdEqpStat.equals("2") || szYdEqpStat.equals("3")){
                //설비id로 크레인 스케줄을 조회한다. 현재 작업진행상태가 1또는 3인 경우를 조회...
                rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
                intRtnVal = ydCrnSchDao.getYdCrnsch(msgRecord, rsResult, 16);
                if(intRtnVal <= 0) {
                    //에러처리
                    szMsg="현재 작업진행상태가 1또는 3인 크레인 스케줄을 조회 중 Error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    //return intRtnVal = -1;
                    return intRtnVal;
                }
            }

            rsCrnSch.addAll(rsResult);

        }catch(Exception e){

            szMsg="Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }


        szMsg="chkWrkProgStat("+szMethodName+") 처리 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y1ChkWrkProgStat()







    /**
     * 오퍼레이션명 : 크레인 작업지시(작업이 없을 경우 요구한다.)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public int Y1ChkWrkProgStatW(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws JDTOException  {
        YdEqpDao ydEqpDao = new YdEqpDao();
        YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
        JDTORecordSet rsResult = null;
        JDTORecord recIntTemp  = null;
        JDTORecord para  = null;

        int intRtnVal          = 0 ;


        String szMsg           = "";
        String szMethodName    = "Y1ChkWrkProgStatW";
        String szQuery         = "";

        String szYD_EQP_ID     = "";
        try{
            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");

            //크레인 스케줄 전체에서 우선순위가 가장 빠른 작업을 조회한다. 크레인 스케줄을 조회한다.
            rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
            para = JDTORecordFactory.getInstance().create();
            para.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"));

            intRtnVal = ydCrnSchDao.getYdCrnsch(para, rsResult, 15);
            if(intRtnVal == 0) {
                //더이상 크레인 스케줄에 작업이 없을 경우 설비Table에 설비상태를 'W' 명령선택대기(idle)로 변경한다.
                recIntTemp = JDTORecordFactory.getInstance().create();
                recIntTemp.setField("YD_EQP_ID", szYD_EQP_ID);
                recIntTemp.setField("YD_EQP_STAT", "W");
                intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
                if(intRtnVal <= 0) {
                    if(intRtnVal == 0) {
                        szMsg=" Y1ChkWrkProgStatW updYdEqp : data not found";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                    }else if(intRtnVal == -1) {
                        szMsg=" Y1ChkWrkProgStatW updYdEqp : duplicate data,";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }else if(intRtnVal == -2) {
                        szMsg=" Y1ChkWrkProgStatW updYdEqp : parameter error";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }else if(intRtnVal == -3){
                        szMsg=" Y1ChkWrkProgStatW updYdEqp : execution failed";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }
                }
                //C연주슬라브야드와 통합야드가 같이 사용하므로 C연주슬라브야드인 경우에만 전송 처리
                if( szYD_EQP_ID.startsWith(YdConstant.YD_GP_C_SLAB_YARD) ||     //C연주슬라브야드
                    szYD_EQP_ID.startsWith(YdConstant.YD_GP_PORT_SLAB_YARD)) {  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                     *  C연주슬라브야드L2 크레인작업실적응답 전송  - YDY1L005 (항만슬라브야드L2 : YDE7L005)
                     * 업무기준 Desc : C연주슬라브야드L2에서 크레인 작업지시 요구 시 크레인 작업이 더 이상 존재하지 않을 경우 크레인작업실적응답 전송
                     * 기능 추가 : 임춘수
                     * 일자 : 2009.06.19
                     +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                    recIntTemp = JDTORecordFactory.getInstance().create();
                    //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
                    recIntTemp.setField("MSG_ID"        , "YDY1L005");
                    if (szYD_EQP_ID.startsWith(YdConstant.YD_GP_PORT_SLAB_YARD)) {
                        recIntTemp.setField("MSG_ID"        , "YDE7L005");
                    }
                    else {
                        recIntTemp.setField("MSG_ID"        , "YDY1L005");
                    }
                    recIntTemp.setField("YD_EQP_ID"     , szYD_EQP_ID);                                 //야드설비ID
                    recIntTemp.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_WO_DMD);                //야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
                    recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK);             //야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
                    ydDelegate.sendMsg(recIntTemp);
                    szMsg = "[C연주 크레인 작업지시 요구]크레인 작업이 더 이상 존재하지 않을 경우 C연주슬라브야드L2 크레인작업실적응답[YDY1L005] 전송 완료" ;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                }
                szMsg="더이상의 크레인스케줄이 조회되지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);

                return intRtnVal = 0;

            }else if(intRtnVal < 0) {
                szMsg="크레인 스케줄 조회중 Error";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return intRtnVal = -1;
            }

            //-------------------------------------------------------------------------------------------------------------
            //  다음스케줄의 작업상태를 체크하여 W이면 설비의 작업상태를 1로 변경, 아니면 변경하지 않음
            //  수정자 : 임춘수
            //  수정일 : 2009.12.21
            //-------------------------------------------------------------------------------------------------------------

            //-------------------------------------------------------------------------------------------------------------
            //  먼저 복사해서 아래부분에서 사라지는 문제를 해결
            //-------------------------------------------------------------------------------------------------------------
            rsCrnSch.addAll(rsResult);
            //-------------------------------------------------------------------------------------------------------------

            rsResult.first();

            recIntTemp  = rsResult.getRecord();

            String szYD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recIntTemp, "YD_WRK_PROG_STAT");

            //-------------------------------------------------------------------------------------------------------------


            //다음 스케줄을 찾았을 경우
            if( szYD_WRK_PROG_STAT.equals("") || szYD_WRK_PROG_STAT.equals(YdConstant.YD_EQP_STAT_IDLE))    {
                recIntTemp = JDTORecordFactory.getInstance().create();
                recIntTemp.setField("YD_EQP_ID", szYD_EQP_ID);
                recIntTemp.setField("YD_EQP_STAT", "1");
                recIntTemp.setField("YD_WORD_DT",      YdUtils.getCurDate("yyyyMMddHHmmss"));
                intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
                if(intRtnVal <= 0) {
                    if(intRtnVal == 0) {
                        szMsg=" Y1ChkWrkProgStatW updYdEqp : data not found";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                    }else if(intRtnVal == -1) {
                        szMsg=" Y1ChkWrkProgStatW updYdEqp : duplicate data,";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }else if(intRtnVal == -2) {
                        szMsg=" Y1ChkWrkProgStatW updYdEqp : parameter error";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }else if(intRtnVal == -3){
                        szMsg=" Y1ChkWrkProgStatW updYdEqp : execution failed";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }
                    return intRtnVal = -1;
                }
            }else{
                szMsg="크레인의 스케줄의 야드작업진행상태["+szYD_WRK_PROG_STAT+"]가 W가 아니므로 크레인설비의 상태를 변경하지 않음";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            }


        }catch(Exception e){

            szMsg="Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }


        szMsg="chkWrkProgStatW("+szMethodName+") 처리완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;

    } //end of Y1ChkWrkProgStatW()



    /**
     * 오퍼레이션명 : 크레인 작업지시(현재 진행중인 작업이 있을경우 해당작업)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public int Y1ChkWrkProgStat4(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws JDTOException  {
         YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();

        JDTORecordSet rsResult          = null;
        JDTORecord    recInPara         = null;

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y1ChkWrkProgStat4";

        String szSchCd                  = "";
        String szQuery                  = "";

        try{
            //스케줄 코드 Check
            szSchCd = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
            if(szSchCd.equals("")) {
                szMsg="스케줄코드가 없습니다. : parameter error";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return intRtnVal = -1;
            }

            rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
            intRtnVal = ydCrnSchDao.getYdCrnsch(msgRecord, rsResult, 55);
            //조회된 크레인 스케줄이 없다면  전체에서 빠른 스케줄을 호출한다.
            if(intRtnVal <= 0) {
                rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
                recInPara = JDTORecordFactory.getInstance().create();
                recInPara.setRecord(msgRecord);
                intRtnVal = this.Y1ChkWrkProgStatW(recInPara, rsResult);
                if(intRtnVal == -1) {
                    szMsg="크레인 작업 조회 중 Error!!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return intRtnVal = -1;
                }else if(intRtnVal == 0) {
                    szMsg="같은 스케줄 코드의 크레인 작업이 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return intRtnVal = 0;
                }
            }

            rsCrnSch.addAll(rsResult);

        }catch(Exception e){

            szMsg="Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }


        szMsg="크레인 작업지시("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y1ChkWrkProgStat4()










    /**
     * 오퍼레이션명 : C연주 크레인권상실적등록 (Y1YDL008)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
     * @ejb.transaction type="RequiresNew"
     */
    public String procY1CrnLdWr(JDTORecord msgRecord)throws DAOException  {
        YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
        YdDelegate ydDelegate = new YdDelegate();
        YdEqpDao   ydEqpDao   = new YdEqpDao();
        YdCarSchDao ydCarSchDao = new YdCarSchDao();
        YdStkColDao ydStkColDao = new YdStkColDao();

        //업데이트 할 크레인 스케줄 Data 항목 set
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();

        //파라미터 null check 후 받아온 Data
        JDTORecord getParamRecord = JDTORecordFactory.getInstance().create();


        //스케줄Table의 컬럼을 저장하기위해 생성
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet  rsResult = null;
        //레코드 셋의 레코드값을 받음
        JDTORecord getRecord = JDTORecordFactory.getInstance().create();
        JDTORecord recInTemp = null;

        JDTORecord recSendMsg = null;

        int intRtnVal                   = 0 ;

        String szWbookId                = "";

        String szMsg="";
        String szMethodName="procY1CrnLdWr";

        String szTcarEqpId = "";
        //크레인스케줄ID
        String szYD_CRN_SCH_ID = "";
        //야드스케줄코드
        String szYD_SCH_CD = null;
        //권상실적위치
        String szYD_UP_WR_LOC = null;
        //설비ID(크레인설비ID)
        String szYD_EQP_ID = null;
        //야드To위치결정방법
        String szYD_TO_LOC_DCSN_MTD = null;
        //권하지시위치
        String szYD_DN_WO_LOC = null;
        //야드목표야드구분
        String szYD_AIM_YD_GP = null;

        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null){
            szMsg=szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return YdConstant.RETN_CD_TC_ERROR;
        }

        try{
            //=============================================================
            // Log 테이블 등록
            //=============================================================
            szMsg = "[C연주정정] 크레인권상실적등록 수신";
            ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            //파라미터 check
            intRtnVal = this.Y1ParamCheck(msgRecord, getParamRecord, 0) ;

            //크레인스케줄ID
            szYD_CRN_SCH_ID = getParamRecord.getFieldString("YD_CRN_SCH_ID");
            //야드스케줄코드
            szYD_SCH_CD     = getParamRecord.getFieldString("YD_SCH_CD");
            //권상실적위치
            szYD_UP_WR_LOC  = getParamRecord.getFieldString("YD_UP_WR_LOC");
            //설비ID(크레인설비ID)
            szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(getParamRecord, "YD_EQP_ID");

            setCrnschRecord.setField("YD_CRN_SCH_ID",         getParamRecord.getFieldString("YD_CRN_SCH_ID"));
            setCrnschRecord.setField("YD_SCH_CD",             getParamRecord.getFieldString("YD_SCH_CD")) ;
            setCrnschRecord.setField("YD_UP_WR_LOC",          getParamRecord.getFieldString("YD_UP_WR_LOC")) ;
            setCrnschRecord.setField("YD_UP_WR_LAYER",        getParamRecord.getFieldString("YD_UP_WR_LAYER")) ;

            //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
            intRtnVal = this.Y1UpdYdCrnsch(setCrnschRecord, 0) ;
            if(intRtnVal == -1) return YdConstant.RETN_CD_FAILURE;

            //Key Data Check!           키값은 Null이나 ""가 되어서는 안됨.
            if(setCrnschRecord.getFieldString("YD_CRN_SCH_ID").equals("") ||
               setCrnschRecord.getFieldString("YD_CRN_SCH_ID") == null) {
                szMsg = "'YD_CRN_SCH_ID' Data Error : 크레인스케줄ID가 없습니다." ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
                throw new DAOException(szMsg);
            }

            //대상 데이터 SELECT
            intRtnVal = this.Y1GetYdCrnsch(setCrnschRecord, getRecSet,3);

            if( intRtnVal <= 0 ) {
                szMsg = "스케쥴 Data가 존재하지 않습니다. ="+ intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
                throw new DAOException(szMsg);
            }

            //레코드셋의 사이즈값으로 ErrorCheck
            if(getRecSet.size() == 0){
                szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
                throw new DAOException(szMsg);
            }

            getRecSet.first();
            getRecord = getRecSet.getRecord();

            //작업예약ID
            szWbookId           = ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID");
            //야드To위치결정방법
            szYD_TO_LOC_DCSN_MTD= ydDaoUtils.paraRecChkNull(getRecord, "YD_TO_LOC_DCSN_MTD");
            //권하지시위치
            szYD_DN_WO_LOC      = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC");
            //야드목표야드구분
            szYD_AIM_YD_GP      = ydDaoUtils.paraRecChkNull(getRecord, "YD_AIM_YD_GP");

            //조회한 크레인 스케줄의 작업활성상태가 check를 한다.
            if (getRecord.getFieldString("YD_WRK_PROG_STAT").equals("1")||
                getRecord.getFieldString("YD_WRK_PROG_STAT").equals("W")) {

                // 적치단 정보 Clear (1개의 크레인스케줄에 잡혀있는 크레인작업재료의 정보를 모두 Check!)
                intRtnVal = this.Y1LdClearYdStklyr(getRecSet, 0);

                //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
                setCrnschRecord = JDTORecordFactory.getInstance().create();
                setCrnschRecord.setField("YD_CRN_SCH_ID",      getParamRecord.getFieldString("YD_CRN_SCH_ID"));
                setCrnschRecord.setField("YD_WRK_PROG_STAT",   getParamRecord.getFieldString("YD_WRK_PROG_STAT"));
                setCrnschRecord.setField("YD_EQP_ID",          getParamRecord.getFieldString("YD_EQP_ID"));
                setCrnschRecord.setField("YD_UP_WR_LOC",       getParamRecord.getFieldString("YD_UP_WR_LOC"));
                setCrnschRecord.setField("YD_UP_WR_LAYER",     getParamRecord.getFieldString("YD_UP_WR_LAYER"));
                setCrnschRecord.setField("YD_UP_WRK_ACT_GP",   getParamRecord.getFieldString("YD_UP_WRK_ACT_GP"));
                setCrnschRecord.setField("YD_UP_WR_XAXIS",     getParamRecord.getFieldString("YD_CRN_XAXIS"));
                setCrnschRecord.setField("YD_UP_WR_YAXIS",     getParamRecord.getFieldString("YD_CRN_YAXIS"));
                setCrnschRecord.setField("YD_UP_WR_ZAXIS",     getParamRecord.getFieldString("YD_CRN_ZAXIS"));
                //권상완료일시
                setCrnschRecord.setField("YD_UP_CMPL_DT",      YdUtils.getCurDate("yyyyMMddHHmmss"));

                intRtnVal = this.Y1UpdYdCrnsch(setCrnschRecord, 0);

                //설비Table의 상태 변경 (권하상태로 변경)
                setCrnschRecord = JDTORecordFactory.getInstance().create();
                setCrnschRecord.setField("YD_EQP_ID",          getParamRecord.getFieldString("YD_EQP_ID"));
                setCrnschRecord.setField("YD_EQP_STAT",        getParamRecord.getFieldString("YD_WRK_PROG_STAT"));

                intRtnVal = ydEqpDao.updYdEqp(setCrnschRecord, 0);
                if(intRtnVal <= 0) {
                     szMsg="설비상태 UPDATE 처리시 오류 발생.";
                     ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    m_ctx.setRollbackOnly();
                    throw new DAOException(szMsg);
                }

                //------------------------------------------------------------------
                // 권상 실적시 Flex 실시간 처리
                //------------------------------------------------------------------
                JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
                //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
                if(szYD_EQP_ID.startsWith(YdConstant.YD_GP_PORT_SLAB_YARD)) {
                    recFlex.setField("YD_GP",  YdConstant.YD_GP_PORT_SLAB_YARD);
                }
                else {
                    recFlex.setField("YD_GP",  YdConstant.YD_GP_C_SLAB_YARD);
                }
                recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
                recFlex.setField("YD_UP_WR_LOC", szYD_UP_WR_LOC);

                szMsg="Flex 권상 완료 실적 전송";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                ydUtils.putYdFlexCrnWrk("", recFlex);
                //------------------------------------------------------------------

                //대차 및 차량 스케줄 이송재료 Handling
                if(ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TC")){

                    //대차스케쥴 이송재료 정보 셋팅.
                     this.Y1SetYdTcar(getRecSet) ;

                    szMsg = "권상시 대차이송재료 삭제 완료" ;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    szTcarEqpId = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(0,1) + "X" +
                                  ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2,6);

                    //대차스케줄 호출
                    recSendMsg = JDTORecordFactory.getInstance().create();
                    recSendMsg.setField("MSG_ID", "YDYDJ520");
                    recSendMsg.setField("YD_LD_UD_GP", "U");
                    recSendMsg.setField("YD_EQP_ID", szTcarEqpId);
                    recSendMsg.setField("YD_WBOOK_ID", szWbookId);
                    //ydDelegate.sendMsg(recSendMsg);
                    ydEjbCon.trx("TransEqpSchSeEJB", "procY1TcarSch", recSendMsg);

                }else if(ydDaoUtils.paraRecChkNull(getParamRecord,"YD_UP_WR_LOC").substring(2, 4).equals("PT")||
                         ydDaoUtils.paraRecChkNull(getParamRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TR")){

                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                     *           하차개시 전문 송신 처리 - 구내운송
                     * 업무기준 Desc : 권상실적위치로 적치열에서 운송장비코드를 조회하고 그 코드로 차량스케줄을 조회하여 차량진행상태가
                     *              하차검수 or 하차도착이면 하차개시로 차량스케줄을 업데이트하고 전문을 전송한다.
                     * 기능 추가 : 임춘수
                     * 일자 : 2009.07.15
                     +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                    //권상실적위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
                    recInTemp = JDTORecordFactory.getInstance().create();
                    recInTemp.setField("YD_STK_COL_GP", szYD_UP_WR_LOC.substring(0, 6));
                    rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                    intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsResult, 0);
                    if( intRtnVal <= 0 ) {
                        szMsg = "[권상실적처리 - 하차개시]차량정지위치[" + szYD_UP_WR_LOC.substring(0, 6) + "] 정보가 존재하지 않습니다." ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }else{
                        //조회된 차량정지위치에서 운송장비코드를 가져온다.
                        rsResult.first();
                        recInTemp = rsResult.getRecord();

                        //운송장비코드
                        String  szTRN_EQP_CD    = ydDaoUtils.paraRecChkNull(recInTemp, "TRN_EQP_CD");
                        String szYD_CAR_USE_GP  = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");
                        String szCAR_NO         = ydDaoUtils.paraRecChkNull(recInTemp, "CAR_NO");
                        String szCARD_NO        = ydDaoUtils.paraRecChkNull(recInTemp, "CARD_NO");
                        szMsg = "[권상실적처리 - 하차개시]차량정지위치[" + szYD_DN_WO_LOC.substring(0, 6) + "] 정보가 존재하므로 차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]로 차량스케줄 조회" ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        //운송장비코드로 차량스케줄조회
                        recInTemp = JDTORecordFactory.getInstance().create();
                        recInTemp.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
                        recInTemp.setField("TRN_EQP_CD", szTRN_EQP_CD);
                        recInTemp.setField("CAR_NO", szCAR_NO);
                        recInTemp.setField("CARD_NO", szCARD_NO);
                        rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                        intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 27);
                        if( intRtnVal <= 0 ) {
                            szMsg = "[권상실적처리 - 하차개시]차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]에 해당하는 차량스케줄이 존재하지 않습니다." ;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        }else{
                            //차량진행상태를 파악하여 하차검수이거나 하차도착일 때만 하차개시 전문을 송신한다.
                            rsResult.absolute(1);
                            recInTemp = rsResult.getRecord();
                            //차량스케줄ID
                            String szYD_CAR_SCH_ID      = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_SCH_ID");
                            //야드차량진행상태
                            String szYD_CAR_PROG_STAT   = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_PROG_STAT");
                            //차량사용구분
                            szYD_CAR_USE_GP             = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");
                            //하차검수이거나 하차도착일 때 하차개시 전문 송신
                            if( szYD_CAR_PROG_STAT.equals("C") || szYD_CAR_PROG_STAT.equals("B") ) {
                                String szYD_CARUD_ST_DT = YdUtils.getCurDate("yyyyMMddHHmmss");
                                recInTemp = JDTORecordFactory.getInstance().create();
                                recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);                               //차량스케줄ID
                                recInTemp.setField("YD_CAR_PROG_STAT", "D");                                        //차량진행상태
                                recInTemp.setField("YD_CARUD_ST_DT", szYD_CARUD_ST_DT);                             //하차개시일시
                                intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
                                if(intRtnVal <= 0) {
                                    szMsg="[권상실적처리 - 하차개시]차량스케줄에 하차 개시 등록시 Error!! Code : " + intRtnVal;
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    m_ctx.setRollbackOnly();
                                    throw new DAOException(szMsg);
                                }

                                String szYD_GP = szYD_SCH_CD.substring(0,1);
                                if( szYD_CAR_USE_GP.equals("L") ) {
                                    //하차작업개시 송신 YDTSJ009
                                    recInTemp = JDTORecordFactory.getInstance().create();
                                    recInTemp.setField("MSG_ID",        "YDTSJ009");
                                    recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
                                    recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
                                    recInTemp.setField("YD_GP",         szYD_GP);
                                    recInTemp.setField("YD_CARUD_ST_DT",         szYD_CARUD_ST_DT);
                                    ydDelegate.sendMsg(recInTemp);

                                    szMsg="[권상실적처리 - 하차개시]하차작업개시[YDTSJ009] 송신 완료";
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                }
                            }
                        }
                    }
                    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

                    intRtnVal = this.YSCSetYdCar(getRecSet, 0) ;
                    szMsg = "권상시 차량이송재료 삭제 완료" ;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                }

                szMsg = "[권상실적처리] 상차개시 진입 전 - 권하지시위치[" + szYD_DN_WO_LOC + "], " + "설비구분[" + szYD_DN_WO_LOC.substring(2, 4) + "]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                 *           상차개시 전문 송신 처리 - 구내운송, 출하관리
                 * 업무기준 Desc : 권하지시위치로 적치열에서 운송장비코드를 조회하고 그 코드로 차량스케줄을 조회하여 차량진행상태가
                 *              상차검수 or 상차도착이면 상차개시로 차량스케줄을 업데이트하고 전문을 전송한다.
                 * 기능 추가 : 임춘수
                 * 일자 : 2009.07.15
                 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                //권하지시위치가 차상인 경우 상차개시 전문 송신 처리
                if( szYD_DN_WO_LOC.substring(2, 4).equals("PT") ||
                    szYD_DN_WO_LOC.substring(2, 4).equals("TR") ) {
                    //권하지시위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
                    recInTemp = JDTORecordFactory.getInstance().create();
                    recInTemp.setField("YD_STK_COL_GP", szYD_DN_WO_LOC.substring(0, 6));
                    rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                    intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsResult, 0);
                    if( intRtnVal <= 0 ) {
                        szMsg = "[권상실적처리 - 상차개시]차량정지위치[" + szYD_DN_WO_LOC.substring(0, 6) + "] 정보가 존재하지 않습니다." ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }else{
                        //조회된 차량정지위치에서 운송장비코드를 가져온다.
                        rsResult.first();
                        recInTemp = rsResult.getRecord();
                        //운송장비코드
                        String  szTRN_EQP_CD    = ydDaoUtils.paraRecChkNull(recInTemp, "TRN_EQP_CD");
                        String szYD_CAR_USE_GP  = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");
                        String szCAR_NO         = ydDaoUtils.paraRecChkNull(recInTemp, "CAR_NO");
                        String szCARD_NO        = ydDaoUtils.paraRecChkNull(recInTemp, "CARD_NO");
                        szMsg = "[권상실적처리 - 상차개시]차량정지위치[" + szYD_DN_WO_LOC.substring(0, 6) + "] 정보가 존재하므로 차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]로 차량스케줄 조회" ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        //운송장비코드로 차량스케줄조회
                        recInTemp = JDTORecordFactory.getInstance().create();
                        recInTemp.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
                        recInTemp.setField("TRN_EQP_CD", szTRN_EQP_CD);
                        recInTemp.setField("CAR_NO", szCAR_NO);
                        recInTemp.setField("CARD_NO", szCARD_NO);
                        rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                        intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 27);
                        if( intRtnVal <= 0 ) {
                            szMsg = "[권상실적처리 - 상차개시]차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]에 해당하는 차량스케줄이 존재하지 않습니다." ;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        }else{
                            //차량진행상태를 파악하여 상차검수이거나 상차도착일 때만 상차개시 전문을 송신한다.
                            rsResult.absolute(1);
                            recInTemp = rsResult.getRecord();
                            //차량스케줄ID
                            String szYD_CAR_SCH_ID      = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_SCH_ID");
                            //야드차량진행상태
                            String szYD_CAR_PROG_STAT   = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_PROG_STAT");
                            //차량사용구분
                            szYD_CAR_USE_GP             = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");

                            szMsg = "[권상실적처리 - 상차개시]차량스케줄 조회 후 차량스케줄ID[" + szYD_CAR_SCH_ID + "], 야드차량진행상태[" + szYD_CAR_PROG_STAT + "], 차량사용구분[" + szYD_CAR_USE_GP + "]" ;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                            //상차검수이거나 상차도착일 때 상차개시 전문 송신
                            if( szYD_CAR_PROG_STAT.equals("3") || szYD_CAR_PROG_STAT.equals("2") ) {
                                /*
                                 * 야드목표야드구분은 작업예약에 등록된 목표야드를 사용한다.
                                 * 수정자 : 임춘수
                                 * 수정일 : 2009.11.03
                                 */
                                YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
                                JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
                                recInTemp = JDTORecordFactory.getInstance().create();
                                recInTemp.setField("YD_WBOOK_ID", szWbookId);
                                intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, outRecSet, 0);

                                if( intRtnVal > 0 ) {
                                    outRecSet.first();
                                    recInTemp = outRecSet.getRecord();
                                    szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recInTemp, "YD_AIM_YD_GP");
                                }
                                //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

                                //야드구분에 따른 개소코드 반환
//                              String szARR_WLOC_CD = YdCommonUtils.getWlocCd(szYD_AIM_YD_GP);
                                //장비코드로 구내운송에서 개소코드 가져 오기
                                String szARR_WLOC_CD = YdCommonUtils.getWlocCd2(szTRN_EQP_CD);

                                //차량스케줄 업데이트 - 상차개시
                                recInTemp = JDTORecordFactory.getInstance().create();
                                recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);                               //차량스케줄ID
                                recInTemp.setField("YD_CAR_PROG_STAT", "4");                                        //차량진행상태
                                recInTemp.setField("YD_EQP_WRK_STAT", "U");                                         //작업상태
                                recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szWbookId);                              //작업예약ID
                                recInTemp.setField("YD_CARLD_ST_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));         //상차개시일시
                                recInTemp.setField("MODIFIER",  "YDSYSTEM");                                        //수정자
                                if( szYD_CAR_USE_GP.equals("L") ) {                 //구내운송
                                    if(!szYD_DN_WO_LOC.substring(0,1).equals(szYD_AIM_YD_GP)){
                                        recInTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD);                               //착지개소코드
                                    }
                                }
                                intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);

                                if(intRtnVal <= 0) {
                                    szMsg="[권상실적처리]차량스케줄에 상차개시일시 등록시 Error!! Code : " + intRtnVal;
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    m_ctx.setRollbackOnly();
                                    throw new DAOException(szMsg);
                                }

                                recInTemp = JDTORecordFactory.getInstance().create();
                                if( szYD_CAR_USE_GP.equals("L") ) {                 //구내운송

                                    //상차작업개시 송신 YDTSJ007 (구내운송 상차개시)
                                    recInTemp.setField("MSG_ID",        "YDTSJ007");
                                    //착지개소코드
                                    recInTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD);

                                    szMsg="[권상실적처리]상차작업개시 송신 YDTSJ007 (구내운송 상차개시) 송신 시작";
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                                }else if( szYD_CAR_USE_GP.equals("G") ){            //출하차량

                                    //상차작업개시 송신 YDDMR009 (외판슬라브출하상차개시)
                                    recInTemp.setField("MSG_ID",        "YDDMR009");
                                    szMsg="[권상실적처리]상차작업개시 송신 YDDMR009 (외판슬라브출하상차개시) 송신 시작";
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                }

                                String szYD_GP          = szYD_SCH_CD.substring(0,1);

                                recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
                                recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
                                recInTemp.setField("YD_GP",         szYD_GP);

                                ydDelegate.sendMsg(recInTemp);

                                szMsg="[권상실적처리]상차작업개시 송신 완료";
                                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            }
                        }
                    }
                }
                /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

                if( szYD_EQP_ID.startsWith(YdConstant.YD_GP_C_SLAB_YARD) ||      //C연주슬라브야드
                    szYD_EQP_ID.startsWith(YdConstant.YD_GP_PORT_SLAB_YARD)) {   //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                     *          생산통제 C열연장입진행실적 전송  - YDCTJ033
                     * 업무기준 Desc : 1. C열연가열로 장입보급Carry-In
                     *                2. 보급베드 - AAPUP4,ABPUP6,ACPUP2
                     *                3. 대상재의 야드목표행선 : C2[작업대기(C열연압연)]
                     * 스케줄코드 :  1. C열연가열로 장입보급Carry-In 스케줄
                     * 장입보급진행상태  : 20 - 크레인 보급권상(보급스케줄시행)
                     * 기능 추가 : 임춘수
                     * 일자 : 2009.06.19
                     +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                    if( ( szYD_TO_LOC_DCSN_MTD.equals("S") ||
                          szYD_TO_LOC_DCSN_MTD.equals("T") ||
                          szYD_TO_LOC_DCSN_MTD.equals("F") ||
                          szYD_TO_LOC_DCSN_MTD.equals("A") ) /* 야드To위치결정방법 - 최종위치 결정인 경우*/
                        &&
                        ( szYD_DN_WO_LOC.startsWith(YdConstant.EQP_A_PU6) ||
                          szYD_DN_WO_LOC.startsWith(YdConstant.EQP_A_PU4) ||
                          szYD_DN_WO_LOC.startsWith(YdConstant.EQP_A_PU2) ) /* 권하지시위치 보급베드(AAPUP4,ABPUP6,ACPUP2) */
                        &&
                        ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_REFUR_SUP6_LEFT) ||
                          szYD_SCH_CD.equals(YdConstant.SCH_CD_A_REFUR_SUP6_RIGHT)||    /* 보급CARRY-IN스케줄(ABPUP6) - 크레인 보급권상 */
                          szYD_SCH_CD.equals(YdConstant.SCH_CD_A_REFUR_SUP4_LEFT) ||
                          szYD_SCH_CD.equals(YdConstant.SCH_CD_A_REFUR_SUP4_RIGHT)||    /* 보급CARRY-IN스케줄(AAPUP4) - 크레인 보급권상 */
                          szYD_SCH_CD.equals(YdConstant.SCH_CD_A_REFUR_SUP2_LEFT) ||
                          szYD_SCH_CD.equals(YdConstant.SCH_CD_A_REFUR_SUP2_RIGHT) )    /* 보급CARRY-IN스케줄(ACPUP2) - 크레인 보급권상 */
                    ) {
                        recInTemp = JDTORecordFactory.getInstance().create();
                        recInTemp.setField("MSG_ID",        "YDCTJ033");
                        recInTemp.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
                        recInTemp.setField("CHG_SUP_PROG_STAT", "20");
                        ydDelegate.sendMsg(recInTemp);
                        szMsg = "[권상실적처리]생산통제 C열연장입진행실적[YDCTJ033] 전송 완료 - 크레인 보급권상(보급스케줄시행)" ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }

                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                     *          연주정정Level2 OHC Take-Out 완료 전송  - YDC3L002
                     * 업무기준 Desc : 1. 연주설비의 RT에서 TAKE-OUT 시
                     *                2. Roller Table #1 Machine - ADRT01, #2 Machine - ADRT02, #3 Machine - ADRT03
                     * 스케줄코드 :  1. OHC Take-Out 스케줄
                     *              #1 Machine - ADRT01LM, #2 Machine - ADRT02LM, #3 Machine - ADRT03LM
                     * 기능 추가 : 임춘수
                     * 일자 : 2009.06.16
                     +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                    if( ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_OUT1) &&
                          szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_RT_01) )       /*#1 Machine OHC TAKE-OUT스케줄 */
                        ||
                        ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_OUT2) &&
                          szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_RT_02) )       /*#2 Machine OHC TAKE-OUT스케줄 */
                        ||
                        ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_OUT3) &&
                          szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_RT_03) )       /*#3 Machine OHC TAKE-OUT스케줄 */
                        ||
                        ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_OUT4) &&
                          szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_RT_04) )
                        ||
                        ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_OUT5) &&
                          szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_RT_05) )
                        ||
                        ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_OUT6) &&
                          szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_RT_06) )
                       ) {
                        recInTemp = JDTORecordFactory.getInstance().create();
                        recInTemp.setField("MSG_ID",        "YDC3L002");
                        recInTemp.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
                        ydDelegate.sendMsg(recInTemp);
                        szMsg = "[권상실적처리]연주정정Level2 OHC Take-Out 완료[YDC3L002] 전송 완료" ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }

                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                     *          C연주2정정Level2 OHC Take-Out 완료 전송  - YDC7L002
                     * 업무기준 Desc : 1. 연주설비의 RT에서 TAKE-OUT 시
                     *                2. Roller Table #4 Machine - ADRT04, #5 Machine - ADRT05
                     * 스케줄코드 :  1. OHC Take-Out 스케줄
                     *              #4 Machine - ADRT04LM, #5 Machine - ADRT05LM
                     * 일자 : 2012.08.08
                     +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                    if( ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_OUT7) &&
                          szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_RT_07) )       //#4 M/C OHC TAKE-OUT스케줄
                        ||
                        ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_OUT8) &&
                          szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_RT_08) )       //#5 M/C OHC TAKE-OUT스케줄
                       ) {
                        recInTemp = JDTORecordFactory.getInstance().create();
                        recInTemp.setField("MSG_ID",        "YDC7L002");
                        recInTemp.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
                        ydDelegate.sendMsg(recInTemp);
                        szMsg = "[권상실적처리]연주2정정Level2 OHC Take-Out 완료[YDC7L002] 전송 완료" ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }
                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                     *          연주정정Level2 Carry-Out완료 전송  - YDC3L003
                     * 업무기준 Desc : 1. 설비입고(Pickup Bed, Piler Bed) 시
                     *                2. Pickup Bed - ADPUP1, ACPUP2, ADPUP3, AAPUP4, ABPUP5
                     *                   Piler Bed - AAPS01, ACPI01, ACPI03
                     * 스케줄코드 :  1. Carry-Out(Pickup Bed, Piler Bed) 스케줄
                     *              AAPS01LM[A동 재열재인출], ACPI01LM[C동 Piler01 입고], ACPI03LM[C동 Piler03 입고],
                     *              AAPU04LM[A동 PU04 수입], ACPU02LM[C동 PU02 수입], ADPU01LM[D동 PU01 입고], ADPU03LM[D동 PU03 입고]
                     * 기능 추가 : 임춘수
                     * 추가 : 김진욱 추가내용 : 핸드스카핑 설비 및 핸드스카핑 입고 스케줄코드 추가 ABSB01, ABSB01LM
                     * 일자 : 2009.06.16
                     +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                    if( szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_PS_01)/*A동 재열재인출 */
                        ||
                        szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_PI_01)/*C동 Piler01 입고 */
                        ||
                        szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_PI_03)/*C동 Piler03 입고 */
                        ||
                        szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_PU6)/* B동 PU06 수입 */
                        ||
                        szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_PU4)/* A동 PU04 수입 */
                        ||
                        szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_PU2)/*C동 PU02 수입 */
                        ||
                        szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_PU1)/*D동 PU01 입고 */
                        ||
                        szYD_UP_WR_LOC.startsWith(YdConstant.EQP_AD_PU2)/*D동 PU02 입고 */
                        ||
                        szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_PU3)/*D동 PU03 입고 */
                        ||
                        szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_SB1)/*B동 SB01 입고 */
                        ||
                        szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_PU5)/*B동 MS BED 추출 */
                        ||
                        szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_PUB)     //C연주 A동 #2 Scarfer Pickup11
                        ||
                        szYD_UP_WR_LOC.startsWith(YdConstant.EQP_P_PU1)     //항만 B동 PickUp : 항만슬라브야드 기능추가 - 2015.12.30 LeeJY
                        ) {
                        recInTemp = JDTORecordFactory.getInstance().create();
                        //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
                        if (szYD_UP_WR_LOC.startsWith(YdConstant.EQP_P_PU1)) {
                            recInTemp.setField("MSG_ID",        "YDE9L003");
                        }
                        else {
                            recInTemp.setField("MSG_ID",        "YDC3L003");
                        }
                        recInTemp.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
                        ydDelegate.sendMsg(recInTemp);
                        szMsg = "[권상실적처리]연주정정Level2 Carry-Out완료[YDC3(E9)L003] 전송 완료" ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }
                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                     *          C연주2정정Level2 Carry-Out완료 전송  - YDC7L003
                     * 업무기준 Desc : 1. 설비입고(Pickup Bed, Piler Bed) 시
                     *                2. Pickup Bed - ACPUP7, ADPUP8, AAPUP9, AAPUPA, AAPUPB
                     *                   Piler Bed - ADPI04, ADPI05
                     * 스케줄코드 :  1. Carry-Out(Pickup Bed, Piler Bed) 스케줄
                     *              AAPS01LM[A동 재열재인출], ACPI01LM[C동 Piler01 입고], ACPI03LM[C동 Piler03 입고],
                     *              AAPU04LM[A동 PU04 수입], ACPU02LM[C동 PU02 수입], ADPU01LM[D동 PU01 입고], ADPU03LM[D동 PU03 입고]
                     * 기능 추가 : 임춘수
                     * 추가 : 김진욱 추가내용 : 핸드스카핑 설비 및 핸드스카핑 입고 스케줄코드 추가 ABSB01, ABSB01LM
                     * 일자 : 2009.06.16
                     +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                    if( szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_PI_04)   //C연주 D동 #4 M/C Piler04
                     || szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_PI_05)   //C연주 C동 #5 M/C Piler05
                     || szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_PU7)     //C연주 C동 #4 M/C Pickup07 (불출 Bed)
                     || szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_PU8)     //C연주 D동 #5 M/C Pickup08 (불출 Bed)
                      ) {
                        recInTemp = JDTORecordFactory.getInstance().create();
                        recInTemp.setField("MSG_ID",        "YDC7L003");
                        recInTemp.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
                        ydDelegate.sendMsg(recInTemp);
                        szMsg = "[권상실적처리]연주2정정Level2 Carry-Out완료[YDC7L003] 전송 완료" ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }
                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                     *          C연주슬라브야드L2 크레인작업실적응답 전송  - YDY1L005
                     * 업무기준 Desc : 크레인 권상실적처리 성공 후 크레인작업실적응답 전송
                     * 기능 추가 : 임춘수
                     * 일자 : 2009.06.17
                     +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

                    recInTemp = JDTORecordFactory.getInstance().create();
                    //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
                    //recInTemp.setField("MSG_ID"        , "YDY1L005");
                    if (szYD_UP_WR_LOC.startsWith(YdConstant.EQP_P_PU1)) {
                        recInTemp.setField("MSG_ID",        "YDE9L005");
                    }
                    else {
                        recInTemp.setField("MSG_ID",        "YDY1L005");
                    }
                    recInTemp.setField("YD_EQP_ID"     , szYD_EQP_ID);                          //야드설비ID
                    recInTemp.setField("YD_WRK_PROG_STAT"   , YdConstant.YD_EQP_STAT_UP_CMPL);  //야드작업진행상태
                    recInTemp.setField("YD_SCH_CD"   , szYD_SCH_CD);                            //야드스케줄코드
                    recInTemp.setField("YD_CRN_SCH_ID"   , szYD_CRN_SCH_ID);                    //야드크레인스케줄ID
                    recInTemp.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_LD_WR);          //야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
                    recInTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NORMAL_HD);   //야드L3처리결과코드
                    ydDelegate.sendMsg(recInTemp);
                    szMsg = "[권상실적처리]C연주슬라브야드L2 크레인작업실적응답[YDY1(E7)L005] 전송 완료" ;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                }
            }else{
                szMsg = "YD_WRK_PROG_STAT data : '1' or 'w' not" ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                 *          C연주슬라브야드L2 크레인작업실적응답 전송  - YDY1L005
                 * 업무기준 Desc : 크레인 권상실적처리 실패와 에러발생 시 크레인작업실적응답 전송
                 * 기능 추가 : 임춘수
                 * 일자 : 2009.06.17
                 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                //에러코드와 에러메세지에 대한 업무정의 후 전송 처리 필요.

                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                m_ctx.setRollbackOnly();
                throw new DAOException(szMsg);
            }

            szMsg="권상 완료 실적 처리 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        }catch(Exception e) {
            szMsg="Error :  "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            m_ctx.setRollbackOnly();
            throw new DAOException(szMsg);
        }

        return YdConstant.RETN_CD_SUCCESS;
    }// end of procY1CrnLdWr()

    /**
     * 오퍼레이션명 : 권상 파라미터 체크
     *
     * @param  ● msgRecord, outRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y1ParamCheck (JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
        JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        String szMsg                        = "" ;
        String szMethodName                 = "Y1ParamCheck";
        int intRtnVal = 0 ;
        
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= msgRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8크레인권상실적 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        try{

            //======================================================================================================
            // LOG 출력 - 그냥 테스트용으로 출력 레코드값체크하는 코드는 제외하고 나중에 삭제
            szMsg = "[1] 야드설비ID : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[2] 야드설비작업Mode : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE");
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[3] 야드작업진행상태 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[4] 야드스케쥴코드 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[5] 야드크레인스케쥴ID : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[6] 야드권상실적위치 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LOC");
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[7] 야드권상실적단 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LAYER");
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[8] 야드크레인X축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS");
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[9] 야드크레인Y축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS");
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[10] 야드크레인Z축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_ZAXIS");
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            //======================================================================================================

            setRecord.setField("YD_CRN_SCH_ID"          , ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID")) ;
            setRecord.setField("YD_SCH_CD"              , ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD")) ;
            setRecord.setField("MSG_ID"                 , ydDaoUtils.paraRecChkNull(msgRecord,"MSG_ID")) ;
            setRecord.setField("DATE"                   , ydDaoUtils.paraRecChkNull(msgRecord,"DATE")) ;
            setRecord.setField("TIME"                   , ydDaoUtils.paraRecChkNull(msgRecord,"TIME")) ;
            setRecord.setField("MSG_GP"                 , ydDaoUtils.paraRecChkNull(msgRecord,"MSG_GP")) ;
            setRecord.setField("YD_EQP_ID"              , ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID")) ;
            setRecord.setField("YD_EQP_WRK_MODE"        , ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE")) ;
            setRecord.setField("YD_WRK_PROG_STAT"       , ydDaoUtils.paraRecChkNull(msgRecord,"YD_WRK_PROG_STAT")) ;
            setRecord.setField("YD_CRN_XAXIS"           , ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_XAXIS")) ;
            setRecord.setField("YD_CRN_YAXIS"           , ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_YAXIS")) ;
            setRecord.setField("YD_CRN_ZAXIS"           , ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_ZAXIS")) ;
            if(intGp == 0){
                setRecord.setField("YD_UP_WR_LOC"               , ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LOC")) ;
                setRecord.setField("YD_UP_WR_LAYER"             , ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LAYER")) ;

                //전문 송신지 위치 Check                :AUTO MANUAL BACKUP구분
                if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("1")) {
                    setRecord.setField("YD_UP_WRK_ACT_GP", "A") ;
                }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("9")) {
                    setRecord.setField("YD_UP_WRK_ACT_GP", "B") ;
                }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("0")) {
                    setRecord.setField("YD_UP_WRK_ACT_GP", "M") ;
                }
            }

            if(intGp == 1){
                setRecord.setField("YD_DN_WR_LOC"               , ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LOC")) ;
                setRecord.setField("YD_DN_WR_LAYER"             , ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LAYER")) ;

                //전문 송신지 위치 Check                :AUTO MANUAL BACKUP구분
                if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("1")) {
                    setRecord.setField("YD_DN_WRK_ACT_GP", "A") ;
                }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("9")) {
                    setRecord.setField("YD_DN_WRK_ACT_GP", "B") ;
                }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("0")) {
                    setRecord.setField("YD_DN_WRK_ACT_GP", "M") ;
                }
            }

            outRecord.addRecord(setRecord) ;

        }catch(Exception e){
            System.out.println("Error : "+ e.getLocalizedMessage());
            throw new JDTOException(e.getMessage());
        }//end of try~catch

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START	
szMsg = "Y8크레인권상실적 처리(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        intRtnVal = 1 ;
        return intRtnVal;

    }//end of Y1ParamCheck()

    /**
     * 오퍼레이션명 : 크레인스케줄 Update
     *
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y1UpdYdCrnsch(JDTORecord msgRecord, int intGp) throws JDTOException {
        YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

        int intRtnVal = 0 ;

        String szMethodName = "Y1UpdYdCrnsch";
        String szMsg        = "";

        try{

            intRtnVal = ydCrnschDao.updYdCrnsch(msgRecord, intGp);
            switch (intRtnVal) {
                case 0  :
                    szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return intRtnVal = -1;
                case -1 :
                    szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return intRtnVal = -1;
                case -2 :
                    szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return intRtnVal = -1;
                case -3 :
                    szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return intRtnVal = -1;
            }
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal ;

    }// end of Y1UpdYdCrnsch

    /**
     * 오퍼레이션명 : 크레인스케줄 Select
     *
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y1GetYdCrnsch (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {

        YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

        int intRtnVal           = 0 ;

        String szMethodName = "Y1GetYdCrnsch";
        String szMsg        = "";

        try{

            intRtnVal = ydCrnschDao.getYdCrnsch(msgRecord, getRecSet, intGp);
            switch (intRtnVal) {
            case 0  :
                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return intRtnVal;
            case -2 :
                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return intRtnVal;
            }
            outRecSet.addAll(getRecSet);

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal ;

    }//end of Y1GetYdCrnsch()

    /**
     * 오퍼레이션명 : 적치단 Clear
     *
     * @param  ● getRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y1LdClearYdStklyr (JDTORecordSet getRecSet, int intGp) throws JDTOException {
        YdStkBedDao ydStkBedDao = new YdStkBedDao();

        JDTORecord getRecord = JDTORecordFactory.getInstance().create();
        JDTORecord setRecord = null;
        JDTORecord crnRecord = null;

        int intRtnVal       = 0;
        String szMsg        = "";
        String szMethodName             = "Y1LdClearYdStklyr";
        String szYD_TO_LOC_DCSN_MTD     = null;
        String szYD_STK_COL_GP          = null;
        String szYD_STK_BED_NO          = null;
        String szYD_UP_WR_LOC           = null;
        String szYD_UP_WR_LAYER         = null;

        String szStkLyr = "";
        String szCrnId  = "";
        String szStlNo  = "";

        try{
            int rowsize = getRecSet.size();
            getRecSet.first();
            getRecord = getRecSet.getRecord();

            szYD_STK_COL_GP      = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(0,6);
            szYD_STK_BED_NO      = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(6,8);
            szYD_UP_WR_LOC       = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC");
            szYD_UP_WR_LAYER     = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LAYER");
            szYD_TO_LOC_DCSN_MTD = ydDaoUtils.paraRecChkNull(getRecord,"YD_TO_LOC_DCSN_MTD");

            for(int i = 0; i < rowsize ; i++){

                getRecSet.absolute(i+1);
                getRecord = getRecSet.getRecord();
                szCrnId     = ydDaoUtils.paraRecChkNull(getRecord,"YD_EQP_ID");
                szStlNo     = ydDaoUtils.paraRecChkNull(getRecord,"STL_NO");

                //크레인에 UPDATE
                crnRecord = JDTORecordFactory.getInstance().create();
                crnRecord.setField("YD_STK_COL_GP",       szCrnId);
                crnRecord.setField("YD_STK_BED_NO",       "01");
                crnRecord.setField("YD_STK_LYR_NO",       "00"+(i+1)) ;
                crnRecord.setField("YD_STK_LYR_MTL_STAT", "C");
                crnRecord.setField("STL_NO",              szStlNo);

                intRtnVal = this.Y1UpdYdStklyr(crnRecord, 0);  //크레인 적치단의 재료정보 UPDATE

                //권상 지시위치 Clear
                setRecord = JDTORecordFactory.getInstance().create();
                setRecord.setField("YD_STK_COL_GP",       szYD_STK_COL_GP);
                setRecord.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);

                //적치단 설정
                szStkLyr = ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, i);
                setRecord.setField("YD_STK_LYR_NO",       szStkLyr) ;
                setRecord.setField("YD_STK_LYR_MTL_STAT", "E");
                setRecord.setField("STL_NO",              "");

                intRtnVal = this.Y1UpdYdStklyr(setRecord, 0);  //적치단의 재료정보 Clear

                /*
                 * 2010.05.04 윤재광 수정
                 * 아래의 소스 의미가 없슴. 주석처리
                 */
                //getRecSet.next();
                //getRecord = getRecSet.getRecord();
            } //end of for

            /*  2009.10.05 김진욱 수정
             *  크레인 작업 편성시 주작업재료이면서 보조작업재료일때 해당 ToBed를 완산상태로 바꾸는 것을 순서모음 작업할때 완산상태를 풀어주도록한다.
             *  적치Bed의 입출고상태를 완산("F")에서 입출고가능("E")로 변경한다.
             */
            if(szYD_TO_LOC_DCSN_MTD.equals("R")){
                setRecord = JDTORecordFactory.getInstance().create();
                setRecord.setField("YD_STK_COL_GP",       szYD_STK_COL_GP);
                setRecord.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);
                setRecord.setField("YD_STK_BED_WHIO_STAT", "E");
                intRtnVal = ydStkBedDao.updYdStkbed(setRecord, 0);
                if(intRtnVal <= 0) {
                    szMsg = "[C연주 크레인 권상실적등록 중] 권상위치 입출고상태 변경 Error!!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        intRtnVal = 1 ;
        return  intRtnVal;
    }//end of Y1LdClearYdStklyr()

    /**
     * 오퍼레이션명 : 적치단 Clear
     *
     * @param  ● getRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y4LdClearYdStklyr (JDTORecordSet getRecSet, int intGp, String logId) throws JDTOException {
        YdStkBedDao ydStkBedDao = new YdStkBedDao();
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// Y4LdClearYdStklyr argument 에 logId 항목 추가 개선
// public int Y4LdClearYdStklyr (JDTORecordSet getRecSet, int intGp) throws JDTOException {
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        JDTORecord getRecord = JDTORecordFactory.getInstance().create();
        JDTORecord setRecord = null;
        JDTORecord crnRecord = null;

        int intRtnVal       = 0;
        String szMsg        = "";
        String szMethodName             = "Y4LdClearYdStklyr";
        String szYD_TO_LOC_DCSN_MTD     = null;
        String szYD_STK_COL_GP          = null;
        String szYD_STK_BED_NO          = null;
        String szYD_UP_WR_LOC           = null;
        String szYD_UP_WR_LAYER         = null;
        String szYD_SCH_ST_GP           = "";
        String szRT_STK_COL_GP          = null;

        String szStkLyr = "";
        String szCrnId  = "";
        String szStlNo  = "";
        String szStkBedNo = "01"; //--2013.04.01 추가 (3기) : Crane Piling 기능


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// logId Empty 이면 logId 신규 생성 개선
if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "적치단 Clear(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////


        try{
            int rowsize = getRecSet.size();
            getRecSet.first();
            getRecord = getRecSet.getRecord();

            szYD_STK_COL_GP      = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(0,6);
            szYD_STK_BED_NO      = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(6,8);
            szYD_UP_WR_LOC       = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC");
            szYD_UP_WR_LAYER     = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LAYER");
            szYD_TO_LOC_DCSN_MTD = ydDaoUtils.paraRecChkNull(getRecord,"YD_TO_LOC_DCSN_MTD");
            szYD_SCH_ST_GP       = ydDaoUtils.paraRecChkNull(getRecord,"YD_SCH_ST_GP");

            for(int i = 0; i < rowsize ; i++){

                getRecSet.absolute(i+1);
                getRecord = getRecSet.getRecord();
                szCrnId     = ydDaoUtils.paraRecChkNull(getRecord,"YD_EQP_ID");
                szStlNo     = ydDaoUtils.paraRecChkNull(getRecord,"STL_NO");

                /*
                 * 2012.06.25 윤재광
                 * 후판제품창고 크레인 파일링지시 구분이 S/T이면
                 * 크레인 야드맵이 아닌 파일러 야드맵에 Update한다.
                 */
                if("S".equals(szYD_SCH_ST_GP)||"T".equals(szYD_SCH_ST_GP)){
                    szCrnId = szCrnId.substring(0, 2) + "AP02";

                    szRT_STK_COL_GP = szYD_STK_COL_GP;

                    //--2013.04.10 szYD_STK_COL_GP 가 Transfer 일 수 있다.
                    //-- 예를 들어 'KBTF02' 이면 'KBRTRA' 로 변경하여 아래 조건을 체크해야 된다.
                    if("TF".equals(szYD_STK_COL_GP.substring(2,4))){
                        szRT_STK_COL_GP = YdCommonUtils.getTf2RtStkLoc(szYD_STK_COL_GP);
                    }

                    //--2013.04.01 추가 (3기) : Crane Piling 기능
                    if(szRT_STK_COL_GP.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD)) {
                        //2후판일경우 RT 구분 A,B,C
                        if(szRT_STK_COL_GP.endsWith("RA")) {
                            szStkBedNo = "01";
                        } else if(szRT_STK_COL_GP.endsWith("RB")){
                            szStkBedNo = "02";
                        } else if(szRT_STK_COL_GP.endsWith("RC")){
                            szStkBedNo = "03";
                        } else if(szRT_STK_COL_GP.endsWith("RD")){
                            szStkBedNo = "04";
                        } else if(szRT_STK_COL_GP.endsWith("RE")){
                            szStkBedNo = "05";
                        } else if(szRT_STK_COL_GP.endsWith("RF")){
                            szStkBedNo = "06";
                        }
                    } else {
                        //1후판일경우 통합 전 A,B,C 인데 B가 01로 이전 부터 사용하고 있어서 B를 01로 함
                        //통합 후는 D,E,F 로 됨
                        if(szRT_STK_COL_GP.endsWith("RB")) {
                            szStkBedNo = "01";
                        } else if(szRT_STK_COL_GP.endsWith("RA")){
                            szStkBedNo = "02";
                        } else if(szRT_STK_COL_GP.endsWith("RC")){
                            szStkBedNo = "03";
                        } else if(szRT_STK_COL_GP.endsWith("RD")){
                            szStkBedNo = "04";
                        } else if(szRT_STK_COL_GP.endsWith("RE")){
                            szStkBedNo = "05";
                        } else if(szRT_STK_COL_GP.endsWith("RF")){
                            szStkBedNo = "06";
                        }
                    }
                }

                //크레인에 UPDATE
                crnRecord = JDTORecordFactory.getInstance().create();
                crnRecord.setField("YD_STK_COL_GP",       szCrnId);
                crnRecord.setField("YD_STK_BED_NO",       szStkBedNo);   //--2013.04.01 추가 (3기) : Crane Piling 기능
                crnRecord.setField("YD_STK_LYR_NO",       "00"+(i+1)) ;
                crnRecord.setField("YD_STK_LYR_MTL_STAT", "C");
                crnRecord.setField("STL_NO",              szStlNo);

                intRtnVal = this.Y1UpdYdStklyr(crnRecord, 0);  //크레인 적치단의 재료정보 UPDATE

                //권상 지시위치 Clear
                setRecord = JDTORecordFactory.getInstance().create();
                setRecord.setField("YD_STK_COL_GP",       szYD_STK_COL_GP);
                setRecord.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);

                //적치단 설정
                szStkLyr = ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, i);
                setRecord.setField("YD_STK_LYR_NO",       szStkLyr) ;
                setRecord.setField("YD_STK_LYR_MTL_STAT", "E");
                setRecord.setField("STL_NO",              "");

                intRtnVal = this.Y1UpdYdStklyr(setRecord, 0);  //적치단의 재료정보 Clear

                /*
                 * 2010.05.04 윤재광 수정
                 * 아래의 소스 의미가 없슴. 주석처리
                 */
                //getRecSet.next();
                //getRecord = getRecSet.getRecord();
            } //end of for

            /*  2009.10.05 김진욱 수정
             *  크레인 작업 편성시 주작업재료이면서 보조작업재료일때 해당 ToBed를 완산상태로 바꾸는 것을 순서모음 작업할때 완산상태를 풀어주도록한다.
             *  적치Bed의 입출고상태를 완산("F")에서 입출고가능("E")로 변경한다.
             */
            if(szYD_TO_LOC_DCSN_MTD.equals("R")){
                setRecord = JDTORecordFactory.getInstance().create();
                setRecord.setField("YD_STK_COL_GP",       szYD_STK_COL_GP);
                setRecord.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);
                setRecord.setField("YD_STK_BED_WHIO_STAT", "E");
                intRtnVal = ydStkBedDao.updYdStkbed(setRecord, 0);
                if(intRtnVal <= 0) {
                    szMsg = "[C연주 크레인 권상실적등록 중] 권상위치 입출고상태 변경 Error!!";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                }
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            throw new JDTOException(szMsg);
        }//end of try~catch

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
szMsg = "적치단 Clear(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        intRtnVal = 1 ;
        return  intRtnVal;
    }//end of Y4LdClearYdStklyr()

    /**
     * 오퍼레이션명 : 적치단 Update
     *
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y1UpdYdStklyr (JDTORecord msgRecord, int intGp) throws JDTOException {
        YdStkLyrDao ydStklyrDao = new YdStkLyrDao();

        int intRtnVal = 0 ;

        String szMsg        = "";
        String szMethodName = "Y1UpdYdStklyr";

        try{

            intRtnVal = ydStklyrDao.updYdStklyr(msgRecord, intGp);  //적치단의 재료정보 Clear
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                    /*
                     * 모음작업시에는 단이 존재하지 않을 수 있으므로 적치단이 존재하지 않더라도
                     * 업무는 진행이 되도록 아래 부분을 수정
                     * 수정자 : 임춘수
                     * 수정일 : 2009.09.21
                     */
                    szMsg="적치단이 존재하지 않습니다. - 모음작업 시 오류발생 가능 ";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    intRtnVal = 1;
                }else if(intRtnVal == -1) {
                    szMsg="duplicate data,";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -2) {
                    szMsg="parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -3){
                    szMsg="execution failed";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                //return intRtnVal = -1;
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal ;

    }//end of Y1UpdYdStklyr

    /**
     * 오퍼레이션명 : 대차 Setting
     *
     * @param  ● inRecordSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public void Y1SetYdTcar (JDTORecordSet inRecordSet) throws JDTOException{
        //Data Setting
        JDTORecord    setRecord             = JDTORecordFactory.getInstance().create();
        //data를 받음
        JDTORecord    getRecord             = JDTORecordFactory.getInstance().create();
        //대차 스케줄 레코드셋의 레코드값을 받음
        JDTORecord    getTcarRecord         = JDTORecordFactory.getInstance().create();
        //대차 스케줄의 레코드셋
        JDTORecordSet outRecSet             = JDTORecordFactory.getInstance().createRecordSet("temp");
        int intRtnVal                       = 0 ;

        String szMethodName                 = "Y1SetYdTcar" ;
        String szMsg                        = "" ;

        //대차 스케줄 ID
        String szYD_TCAR_SCH_ID             = "" ;

        try{
            // 크레인스케줄 Data
            inRecordSet.first();
            getRecord = inRecordSet.getRecord();

            setRecord.setField("YD_CARUD_WRK_BOOK_ID", getRecord.getFieldString("YD_WBOOK_ID")) ;
            setRecord.setField("YD_CARLD_WRK_BOOK_ID", "") ;

            // 상하차 작업예약 ID로 대차스케줄 조회
            intRtnVal = this.Y1GetYdTcarsch(setRecord, outRecSet, 1) ;
            if (intRtnVal <= 0){
                szMsg = "대차에서 권상작업 처리시 대차스케쥴 정보 오류발생.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                throw new  JDTOException(szMsg);
            }

            // 대차스케줄 Data
            outRecSet.first() ;
            getTcarRecord = outRecSet.getRecord() ;
            // 대차스케줄 ID를 추출한다
            szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_TCAR_SCH_ID");

            //setRecord 초기화
            setRecord       = JDTORecordFactory.getInstance().create();
            int szRowSize   = inRecordSet.size();

            // 권상한 재료만큼 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)
            for(int i = 0; i < szRowSize; i++){

                // 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)
                setRecord.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
                setRecord.setField("STL_NO",         ydDaoUtils.paraRecChkNull(getRecord, "STL_NO"));
                setRecord.setField("DEL_YN",         "Y");
                intRtnVal = this.Y1UpdTcarftmvmtl(setRecord, 0) ;

                inRecordSet.next() ;
                getRecord = inRecordSet.getRecord();
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new  JDTOException(szMsg);
        }//end of try~catch

    }//end of Y1SetYdTcar()

    /**
     * 오퍼레이션명 : 대차 스케줄 Select
     *
     * @param  ● msgRecord, outRecset, intGp
     * @return ● intRtnVal
     * @throws ●
     */
    public int Y1GetYdTcarsch (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp)throws JDTOException{
        YdTcarSchDao ydTcarschDao = new YdTcarSchDao();
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

        int intRtnVal           = 0 ;

        String szMethodName                 = "Y1GetYdTcarsch" ;
        String szMsg                        = "" ;

        try{

            intRtnVal = ydTcarschDao.getYdTcarsch(msgRecord, getRecSet, intGp);
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                }else if(intRtnVal == -2) {
                    szMsg="parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return intRtnVal = -1;
            }

            outRecset.addAll(getRecSet)  ;

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new  JDTOException(szMsg);
        }
        return intRtnVal ;
    }//end of Y1GetYdTcarsch

    /**
     * 오퍼레이션명 : 대차이송재료 Update
     *
     * @param inRecord, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y1UpdTcarftmvmtl (JDTORecord inRecord, int intGp) throws JDTOException {
        YdTcarFtmvMtlDao ydTcarftmvmtlDao   = new YdTcarFtmvMtlDao();

        int intRtnVal = 0 ;

        String szMethodName                 = "Y1UpdTcarftmvmtl" ;
        String szMsg                        = "" ;

        try{

            intRtnVal = ydTcarftmvmtlDao.updYdTcarftmvmtl(inRecord, intGp) ;
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                }else if(intRtnVal == -1) {
                    szMsg="duplicate data,";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -2) {
                    szMsg="parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -3){
                    szMsg="execution failed";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return intRtnVal = -1;
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }

        return intRtnVal ;
    }//end of Y1UpdTcarftmvmtl

    /**
     * 오퍼레이션명 : 대차이송재료 Insert
     *
     * @param msgRecord
     * @return intRtnVal
     * @throws
     */
    public int Y1InsYdTcarftmvmtl(JDTORecord msgRecord)throws JDTOException{
        YdTcarFtmvMtlDao ydTcarftmvmtlDao = new YdTcarFtmvMtlDao();

        int intRtnVal           = 0 ;

        String szMethodName                 = "Y1InsYdTcarftmvmtl" ;
        String szMsg                        = "" ;

        try{

            intRtnVal = ydTcarftmvmtlDao.insYdTcarftmvmtl(msgRecord);
            if(intRtnVal == -2) {
                szMsg="parameter error";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return intRtnVal = -1;
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }
        return intRtnVal ;

    }//end of Y1InsYdTcarftmvmtl

    /**
     * 오퍼레이션명 : 대차스케줄 Update
     *
     * @param msgRecord, intGp(0: 대차스케줄ID)
     * @return intRtnVal
     * @throws
     */
    public int Y1UpdYdTcarsch (JDTORecord msgRecord, int intGp)throws JDTOException{
        YdTcarSchDao ydTcarschDao = new YdTcarSchDao();

        int intRtnVal           = 0 ;

        String szMethodName                 = "Y1UpdYdTcarsch" ;
        String szMsg                        = "" ;

        try{

            intRtnVal = ydTcarschDao.updYdTcarsch(msgRecord, intGp);
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                }else if(intRtnVal == -1) {
                    szMsg="duplicate data,";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -2) {
                    szMsg="parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -3){
                    szMsg="execution failed";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return intRtnVal = -1;
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return 1 ;

    }//end of Y1UpdYdTcarsch

    /**
     * 오퍼레이션명 : 대차 Setting
     *
     * @param  ● inRecordSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public void Y3SetYdTcar (JDTORecordSet inRecordSet) throws JDTOException{
        //Data Setting
        JDTORecord    setRecord             = JDTORecordFactory.getInstance().create();
        //data를 받음
        JDTORecord    getRecord             = JDTORecordFactory.getInstance().create();
        //대차 스케줄 레코드셋의 레코드값을 받음
        JDTORecord    getTcarRecord         = JDTORecordFactory.getInstance().create();
        //대차 스케줄의 레코드셋
        JDTORecordSet outRecSet             = JDTORecordFactory.getInstance().createRecordSet("temp");
        int intRtnVal                       = 0 ;

        String szMethodName                 = "Y3SetYdTcar" ;
        String szMsg                        = "" ;

        //대차 스케줄 ID
        String szYD_TCAR_SCH_ID             = "" ;

        try{
            // 크레인스케줄 Data
            inRecordSet.first();
            getRecord = inRecordSet.getRecord();

            setRecord.setField("YD_CARUD_WRK_BOOK_ID", getRecord.getFieldString("YD_WBOOK_ID")) ;
            setRecord.setField("YD_CARLD_WRK_BOOK_ID", "") ;

            // 상하차 작업예약 ID로 대차스케줄 조회
            intRtnVal = this.Y3GetYdTcarsch(setRecord, outRecSet, 1) ;
            if (intRtnVal <= 0){
                szMsg = "대차에서 권상작업 처리시 대차스케쥴 정보 오류발생.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                throw new  JDTOException(szMsg);
            }

            // 대차스케줄 Data
            outRecSet.first() ;
            getTcarRecord = outRecSet.getRecord() ;
            // 대차스케줄 ID를 추출한다
            szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_TCAR_SCH_ID");

            //setRecord 초기화
            setRecord       = JDTORecordFactory.getInstance().create();
            int szRowSize   = inRecordSet.size();

            // 권상한 재료만큼 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)
            for(int i = 0; i < szRowSize; i++){

                // 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)
                setRecord.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
                setRecord.setField("STL_NO",         ydDaoUtils.paraRecChkNull(getRecord, "STL_NO"));
                setRecord.setField("DEL_YN",         "Y");
                intRtnVal = this.Y3UpdTcarftmvmtl(setRecord, 0) ;

                inRecordSet.next() ;
                getRecord = inRecordSet.getRecord();
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new  JDTOException(szMsg);
        }//end of try~catch

    }//end of Y3SetYdTcar()

    /**
     * 오퍼레이션명 : 대차 스케줄 Select
     *
     * @param  ● msgRecord, outRecset, intGp
     * @return ● intRtnVal
     * @throws ●
     */
    public int Y3GetYdTcarsch (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp)throws JDTOException{
        YdTcarSchDao ydTcarschDao = new YdTcarSchDao();
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

        int intRtnVal           = 0 ;

        String szMethodName                 = "Y3GetYdTcarsch" ;
        String szMsg                        = "" ;

        try{

            intRtnVal = ydTcarschDao.getYdTcarsch(msgRecord, getRecSet, intGp);
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                }else if(intRtnVal == -2) {
                    szMsg="parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return intRtnVal = -1;
            }

            outRecset.addAll(getRecSet)  ;

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new  JDTOException(szMsg);
        }
        return intRtnVal ;
    }//end of Y3GetYdTcarsch

    /**
     * 오퍼레이션명 : 대차이송재료 Update
     *
     * @param inRecord, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y3UpdTcarftmvmtl (JDTORecord inRecord, int intGp) throws JDTOException {
        YdTcarFtmvMtlDao ydTcarftmvmtlDao   = new YdTcarFtmvMtlDao();

        int intRtnVal = 0 ;

        String szMethodName                 = "Y3UpdTcarftmvmtl" ;
        String szMsg                        = "" ;

        try{

            intRtnVal = ydTcarftmvmtlDao.updYdTcarftmvmtl(inRecord, intGp) ;
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                }else if(intRtnVal == -1) {
                    szMsg="duplicate data,";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -2) {
                    szMsg="parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -3){
                    szMsg="execution failed";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return intRtnVal = -1;
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }

        return intRtnVal ;
    }//end of Y3UpdTcarftmvmtl

    /**
     * 오퍼레이션명 : 대차이송재료 Insert
     *
     * @param msgRecord
     * @return intRtnVal
     * @throws
     */
    public int Y3InsYdTcarftmvmtl(JDTORecord msgRecord)throws JDTOException{
        YdTcarFtmvMtlDao ydTcarftmvmtlDao = new YdTcarFtmvMtlDao();

        int intRtnVal           = 0 ;

        String szMethodName                 = "Y3InsYdTcarftmvmtl" ;
        String szMsg                        = "" ;

        try{

            intRtnVal = ydTcarftmvmtlDao.insYdTcarftmvmtl(msgRecord);
            if(intRtnVal == -2) {
                szMsg="parameter error";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return intRtnVal = -1;
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }
        return intRtnVal ;

    }//end of Y3InsYdTcarftmvmtl

    /**
     * 오퍼레이션명 : 대차스케줄 Update
     *
     * @param msgRecord, intGp(0: 대차스케줄ID)
     * @return intRtnVal
     * @throws
     */
    public int Y3UpdYdTcarsch (JDTORecord msgRecord, int intGp)throws JDTOException{
        YdTcarSchDao ydTcarschDao = new YdTcarSchDao();

        int intRtnVal           = 0 ;

        String szMethodName                 = "Y3UpdYdTcarsch" ;
        String szMsg                        = "" ;

        try{

            intRtnVal = ydTcarschDao.updYdTcarsch(msgRecord, intGp);
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                }else if(intRtnVal == -1) {
                    szMsg="duplicate data,";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -2) {
                    szMsg="parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -3){
                    szMsg="execution failed";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return intRtnVal = -1;
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return 1 ;

    }//end of Y3UpdYdTcarsch

    /**
     * 오퍼레이션명 : 차량 Setting
     *
     * @param inRecordSet, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int YSCSetYdCar (JDTORecordSet inRecordSet, int intGp) throws JDTOException{
        YdCarSchDao ydCarschDao = new YdCarSchDao();

        //Data Setting
        JDTORecord    setRecord             = JDTORecordFactory.getInstance().create();

        //data를 받음
        JDTORecord    getRecord             = JDTORecordFactory.getInstance().create();

        //차량 스케줄 레코드셋의 레코드값을 받음
        JDTORecord    getTcarRecord         = JDTORecordFactory.getInstance().create();

        //차량 스케줄의 레코드셋
        JDTORecordSet outRecSet             = JDTORecordFactory.getInstance().createRecordSet("temp");

        String szMethodName                 = "YSCSetYdCar" ;
        String szMsg                        = "" ;
        long lngYD_MTL_WT                  = 0;
        int  intYD_MTL_SH                  = 0;
        long lngYD_EQP_WRK_WT              = 0;
        int  intYD_EQP_WRK_SH              = 0;

        int intRtnVal = 0 ;

        //차량 스케줄 ID
        String szYD_CAR_SCH_ID = "" ;

        try{

            // 크레인스케줄 Data
            inRecordSet.first();
            getRecord = inRecordSet.getRecord();


            //하차 작업 예약 ID Setting
            if(intGp == 0) {
                setRecord.setField("YD_CARUD_WRK_BOOK_ID", getRecord.getFieldString("YD_WBOOK_ID")) ;
                setRecord.setField("YD_CARLD_WRK_BOOK_ID", "") ;

            //상차 작업 예약 ID Setting
            }else if(intGp == 1) {
                setRecord.setField("YD_CARLD_WRK_BOOK_ID", getRecord.getFieldString("YD_WBOOK_ID")) ;
                setRecord.setField("YD_CARUD_WRK_BOOK_ID", "") ;
            }

            // 상하차 작업예약 ID로 차량스케줄 조회
            intRtnVal = this.YSCGetYdCarsch(setRecord, outRecSet, 3) ;
            if (intRtnVal <= 0){
                szMsg = "차량에서 권상작업 처리시 차량스케쥴 정보 오류발생.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                /*
                 * 2010.12.25 윤재광 - 차량입고기능을 위해 아래 에러처리 막음.
                 * 차량입고기능은 차량스케쥴 정보 없슴
                 */
                //throw new  JDTOException(szMsg);
                return 1;
            }

            // 차량스케줄 Data
            outRecSet.first() ;
            getTcarRecord = outRecSet.getRecord() ;
            // 차량스케줄 ID를 추출한다
            szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_CAR_SCH_ID");
            // 차량스케줄의 작업재료 중량 및 매수를 추출한다.
            lngYD_EQP_WRK_WT = ydDaoUtils.paraRecChkNullLong(getTcarRecord, "YD_EQP_WRK_WT");
            intYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNullInt(getTcarRecord, "YD_EQP_WRK_SH");

            //setRecord 초기화
            setRecord       = JDTORecordFactory.getInstance().create();
            int szRowSize   = inRecordSet.size();

            // 권상한 재료만큼 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)
            for(int i = 0; i < szRowSize; i++){

                //재료매수
                intYD_MTL_SH = i + 1;

                // 차량스케줄 ID로 차량이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)
                setRecord.setField("YD_CAR_SCH_ID",         szYD_CAR_SCH_ID);
                setRecord.setField("STL_NO",                ydDaoUtils.paraRecChkNull(getRecord, "STL_NO"));

                //차량 이송재료 등록 (하차 )
                if(intGp == 0) {
                    setRecord.setField("DEL_YN",                "Y");
                    intRtnVal = this.YSCUpdCarftmvmtl(setRecord, 0) ;

                //차량 이송재료 등록 (상차 )
                }else if(intGp == 1) {
                    setRecord.setField("DEL_YN",                "N");
                    setRecord.setField("YD_STK_BED_NO",         ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(6,8)) ;
                    setRecord.setField("YD_STK_LYR_NO",         ydDaoUtils.stringPlusInt(ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER"), i)) ;
                    intRtnVal = this.YSCInsYdCarftmvmtl(setRecord) ;
                }
                //재료중량
                lngYD_MTL_WT = lngYD_MTL_WT + ydDaoUtils.paraRecChkNullLong(getRecord, "YD_MTL_WT");

                inRecordSet.next() ;
                getRecord = inRecordSet.getRecord();
            }
            if(intGp == 0) {
                //차량스케줄에 등록한다.
                lngYD_EQP_WRK_WT = lngYD_EQP_WRK_WT - lngYD_MTL_WT;
                intYD_EQP_WRK_SH = intYD_EQP_WRK_SH - intYD_MTL_SH;
                //setRecord 초기화
                setRecord           = JDTORecordFactory.getInstance().create();
                setRecord.setField("YD_CAR_SCH_ID",         szYD_CAR_SCH_ID);
                setRecord.setField("YD_EQP_WRK_WT",         "" + lngYD_EQP_WRK_WT);
                setRecord.setField("YD_EQP_WRK_SH",         "" + intYD_EQP_WRK_SH);

                intRtnVal = ydCarschDao.updYdCarsch(setRecord, 0);
            }
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new  JDTOException(szMsg);
        }//end of try~catch

        return 1 ;

    }//end of YSCSetYdCar()

    /**
     * 오퍼레이션명 : 차량 스케줄 Select
     *
     * @param msgRecord, outRecset, intGp(1:상하차)
     * @return intRtnVal
     * @throws
     */
    public int YSCGetYdCarsch (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp)throws JDTOException{
        YdCarSchDao ydCarschDao = new YdCarSchDao();
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

        int intRtnVal           = 0 ;

        String szMethodName = "YSCGetYdCarsch";
        String szMsg        = "";

        try{

            intRtnVal = ydCarschDao.getYdCarsch(msgRecord, getRecSet, intGp);

            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                }else if(intRtnVal == -2) {
                    szMsg="parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                if (intRtnVal <= 0) return intRtnVal = -2;
            }
            outRecset.addAll(getRecSet)  ;

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal ;

    }//end of YSCGetYdCarsch


    /**
     * 오퍼레이션명 : 차량 이송재료 Update
     *
     * @param inRecordSet, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int YSCUpdCarftmvmtl (JDTORecord inRecord, int intGp) throws JDTOException {
        YdCarFtmvMtlDao ydCarftmvmtlDao   = new YdCarFtmvMtlDao();

        int intRtnVal = 0 ;

        String szMethodName = "YSCUpdCarftmvmtl";
        String szMsg        = "";

        try{

            intRtnVal = ydCarftmvmtlDao.updYdCarftmvmtl(inRecord, intGp) ;
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="data not found!!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                }else if(intRtnVal == -1) {
                    szMsg="duplicate data,";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -2) {
                    szMsg="parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -3){
                    szMsg="execution failed";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return intRtnVal = -1;
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal ;

    }//end of YSCUpdCarftmvmtl

    /**
     * 오퍼레이션명 : 차량이송재료 Insert
     *
     * @param msgRecord
     * @return intRtnVal
     * @throws
     */
    public int YSCInsYdCarftmvmtl(JDTORecord msgRecord)throws JDTOException{
        YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();

        int intRtnVal = 0 ;

        String szMethodName = "YSCInsYdCarftmvmtl";
        String szMsg        = "";

        try{
            intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(msgRecord);
            if(intRtnVal == -2) {
                szMsg="parameter error";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return intRtnVal = -1;
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal = 1;
    }//end of YSCInsYdCarftmvmtl

    /**
     * 오퍼레이션명 : 작업계획 Simulation 삭제 Setting
     * @param msgRecord, intGp
     * @return intRtnVal
     * @throws
     */
    public int Y1SetYdWrkplnsimulation (JDTORecord msgRecord, int intGp)throws JDTOException{
        YdWrkPlnSimulationDao ydWrkplnsimulationDao = new YdWrkPlnSimulationDao();
        //작업계획 Sim 조회 한 값
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
        //getRecSet의 첫번째 레코드 값을 저장
        JDTORecord    getRecord             = JDTORecordFactory.getInstance().create();

        String szMsg            = "" ;
        String szMethodName     = "Y1SetYdWrkplnsimulation" ;

        int intRtnVal           = 0 ;

        try{
            //작업계획 Simulation Select                msgRecord에는 스케줄코드와 재료번호가 있음
            intRtnVal = this.Y1GetYdWrkplnsimulation(msgRecord, getRecSet, intGp);
            if (intRtnVal <= 0) {
                if (intRtnVal == 0) {
                    szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                } else if (intRtnVal == -2) {
                    szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return intRtnVal ;
            }
            getRecSet.first();
            getRecord = getRecSet.getRecord();
            getRecord.setField("DEL_YN", "Y") ;
            getRecord.setField("MODIFIER", "YDSYSTEM") ;

            intRtnVal = this.Y1UpdYdWrkplnsimulation(getRecord) ;
            if (intRtnVal <= 0) {
                if (intRtnVal == 0) {
                    szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                } else if (intRtnVal == -2) {
                    szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return intRtnVal ;
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal ;

    }//end of Y1SetYdWrkplnsimulation()

    /**
     * 오퍼레이션명 : 작업계획 Simulation Select
     *
     * @param msgRecord, outRecset, intGp
     * @return intRtnVal
     * @throws
     */
    public int Y1GetYdWrkplnsimulation (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp)throws JDTOException{
        YdWrkPlnSimulationDao ydWrkplnsimulationDao = new YdWrkPlnSimulationDao();
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

        String szMsg            = "" ;
        String szMethodName     = "Y1GetYdWrkplnsimulation" ;

        int intRtnVal           = 0 ;

        try{

            intRtnVal = ydWrkplnsimulationDao.getYdWrkplnsimulation(msgRecord, getRecSet, intGp);
            if (intRtnVal <= 0) {
                if (intRtnVal == 0) {
                    szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                } else if (intRtnVal == -2) {
                    szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return intRtnVal ;
            }
            outRecset.addAll(getRecSet)  ;

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal ;

    }//end of Y1GetYdWrkplnsimulation()

    /**
     * 오퍼레이션명 : 작업계획 Simulation Update
     *
     * @param msgRecord
     * @return intRtnVal
     * @throws
     */
    public int Y1UpdYdWrkplnsimulation (JDTORecord msgRecord)throws JDTOException{
        YdWrkPlnSimulationDao ydWrkplnsimulationDao = new YdWrkPlnSimulationDao();
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

        String szMsg            = "" ;
        String szMethodName     = "Y1UpdYdWrkplnsimulation" ;

        int intRtnVal           = 0 ;

        try{

            intRtnVal = ydWrkplnsimulationDao.updYdWrkplnsimulationPlnIdAndLess(msgRecord);
            if (intRtnVal <= 0) {
                if (intRtnVal == -2) {
                    szMsg = "parameter error    Error code:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                } else if (intRtnVal == -3) {
                    szMsg = "execution failed   Error code:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return intRtnVal ;
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal ;

    }//end of Y1UpdYdWrkplnsimulation()

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////







    /**
     * 오퍼레이션명 : A후판SLAB 크레인작업지시 (Y3YDL007, YDYDJ641)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws JDTOException
     */
    public String procY3CrnWrkOrdReq(JDTORecord msgRecord)throws JDTOException  {

        YdEqpDao     ydEqpDao     = new YdEqpDao();
        /*
         * 동일크레인 명령선택시 최초 Status 관리방안.
         * - 설비ID의 수정일자 UPDATE - DB ROW LOCK
         * - 작업지시는 큐방식으로 호출하기 때문에 필요없어짐.(막음)
         */
        //int SeqNo = ydEqpDao.updYdEqpDirect(msgRecord, 4);

        YdDelegate   ydDelegate   = new YdDelegate();
        YdCrnSchDao  YdCrnSchDao  = new YdCrnSchDao();
        YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();

        JDTORecord recCrnSch = JDTORecordFactory.getInstance().create();
        JDTORecord recInPara = null;
        JDTORecord recOutTemp = null;
        JDTORecord recIntTemp = null;

        JDTORecordSet rsCrnSch = JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet rsWrkBook = null;

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "procY3CrnWrkOrdReq";
        String szOperationName          = "A후판크레인작업지시";

        String szEqpId                  = "";
        String szWrkProgStat            = "";

        //스케쥴코드
        String szYD_SCH_CD              = "";

        boolean blnRtnVal               = true;

        String szRtnMsg                 = null;

        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null || szRcvTcCode.equals("")){
            szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return YdConstant.RETN_CD_TC_ERROR;
        }

        try{
            //=============================================================
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판슬라브야드] 크레인작업지시 수신";
            ydUtils.putLogMsg("D", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);


            //======================================================================================================
            // LOG 출력 - 그냥 테스트용으로 출력 레코드값체크하는 코드는 제외하고 나중에 삭제
            szMsg = "[1] 야드설비ID : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[2] 야드설비작업Mode : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[3] 야드작업진행상태 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[4] 야드스케쥴코드 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[5] 야드크레인스케쥴ID : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[6] 야드크레인X축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[7] 야드크레인Y축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            //======================================================================================================

            szEqpId     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");

            //------------------------------------------------------------------------------------------------------
            // 야드설비상태 Check       수신받은  야드설비Id로 설비Table를 조회하여 야드설비상태를 Check하고 고장이면 return
            //------------------------------------------------------------------------------------------------------

            szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            JDTORecordSet rsCrnInfo = JDTORecordFactory.getInstance().createRecordSet("");
            JDTORecord recCrnInfo   = JDTORecordFactory.getInstance().create();

            szRtnMsg = this.eqpStatCheck(szEqpId, rsCrnInfo);

            if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
                szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 시 오류발생 - 메세지 : " + szRtnMsg;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return szRtnMsg;
            }

            szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 완료 - 메세지 : " + szRtnMsg;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //------------------------------------------------------------------------------------------------------

            // 스케줄 기준 체크
            szYD_SCH_CD =  ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
            if( !szYD_SCH_CD.equals("") ) {
                blnRtnVal = YdCommonUtils.chkGetSchRule(szYD_SCH_CD, rsResult);
                if( !blnRtnVal ) return YdConstant.RETN_CD_FAILURE;
                // 레코드 추출
                rsResult.first();
                JDTORecord recPara = rsResult.getRecord();
                // 스케줄 금지 유무
                String szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,"YD_SCH_PROH_EXN");

                // 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
                if (szYD_SCH_PROH_EXN.equals("Y")) {
                    szMsg = "크레인 작업지시 시 스케쥴코드(" + szYD_SCH_CD + ")에 대한 스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return YdConstant.RETN_CRN_SCH_PROH;
                }
            }

            /*
             * 2011.05.25 윤재광 현 크레인상태를 넘겨줌.
            //레코드 추출
            rsCrnInfo.first();
            recCrnInfo = rsCrnInfo.getRecord();

            //야드 작업 진행상태를 check한다.
            szWrkProgStat = ydDaoUtils.paraRecChkNull(recCrnInfo, "YD_EQP_STAT");
            */

            szWrkProgStat = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");

            //야드 작업 진행상태가 1,3인 경우 (작업지시를 재요구 하는 경우 사용한다.)
            if (szWrkProgStat.equals("1") ||
                szWrkProgStat.equals("2") ||
                szWrkProgStat.equals("3")){

                szMsg="야드 작업 진행상태가 1,3인 경우";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                intRtnVal = this.Y3ChkWrkProgStat(msgRecord, rsCrnSch);

                if( intRtnVal == 0 ) {
                    //크레인 스케줄이 없다면 없다는 메시지를 L2에 전송해야한다.

                    return YdConstant.RETN_CRN_NO_SCH;
                }else if(intRtnVal == -1) {
                    return YdConstant.RETN_CD_FAILURE;
                }else{

                    //현재크레인이 작업중인 스케줄의 값을 다시 재전송한다.
                    rsCrnSch.absolute(1);
                    recOutTemp = JDTORecordFactory.getInstance().create();
                    recOutTemp.setRecord(rsCrnSch.getRecord());

                    recInPara = JDTORecordFactory.getInstance().create();
                    //작업지시 전문 전송 data setup
                    recInPara.setField("MSG_ID", "YDY3L004");
                    recInPara.setField("YD_CRN_SCH_ID",    ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CRN_SCH_ID"));
                    recInPara.setField("YD_WRK_PROG_STAT", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PROG_STAT"));
                    recInPara.setField("YD_SCH_CD",        ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"));
                    recInPara.setField("YD_GP",            ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP"));
                    recInPara.setField("MODIFIER", "YDSYSTEM");

                    //20090925 김진욱 추가 : 작업진행중인 작업을 재전송하는 경우는  MSG_GP 값을 'U' UPDATE로 설정해서 보낸다.
                    recInPara.setField("MSG_GP", "U");
                    ydDelegate.sendMsg(recInPara);
                    szMsg = "[A후판SLAB 크레인 작업지시]현재크레인이 작업중인 스케줄을 재전송";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    return YdConstant.RETN_CD_SUCCESS;
                }

            //야드 작업 진행 상태가 'W'인경우 (작업이 없을때 요구)
            }else if(szWrkProgStat.equals("W")){
                szMsg="야드 작업 진행 상태가 'W'인경우";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                intRtnVal = this.Y3ChkWrkProgStatW(msgRecord, rsCrnSch);
                if(intRtnVal == 0) {
                    szMsg="[A후판SLAB 크레인 작업지시]크레인 스케줄이 조회되지 않습니다 [intRtnVal = " + intRtnVal + "]";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return YdConstant.RETN_CRN_NO_SCH;
                }else if(intRtnVal < 0) {
                    szMsg="[A후판SLAB 크레인 작업지시]크레인스케줄 조회 중 에러 발생  [intRtnVal = " + intRtnVal + "]";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return YdConstant.RETN_CD_FAILURE;
                }
                szMsg="rsCrnSch의 사이즈 : " + rsCrnSch.size();
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //야드 작업 진행 상태가 '4'인 경우 현재 진행중인 작업이 있을 경우 (현재 진행중인 작업이 있을 경우 해당작업을 호출한다.스케줄 코드로 조회, 조회한 data가 없다면 스케줄우선순위가 빠르고 크레인스케줄id가 가장빠른 작업을 보내준다.)
            }else if(szWrkProgStat.equals("4")) {

                szMsg="야드 작업 진행 상태가 '4'인 경우 (권하실적처리에서 호출)";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                intRtnVal = this.Y3ChkWrkProgStat4(msgRecord, rsCrnSch);

                //더이상 크레인 스케줄을 찾지 못했을 경우 설비상태를 W <--idle로 변경하고 0으로 리턴, 에러인 경우  -1로 리턴.. 크레인 스케줄 호출부분이 아직 없기때문에 종료처리...추후 변경 0일경우는 크레인 스케줄 호출..
                if(intRtnVal <= 0) {
                    szMsg="더이상 크레인 스케줄을 찾지못했거나 Error가 발생했을 경우 작업예약을 조회한다. intRtnVal : " + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    //작업예약조회
                    recInPara = JDTORecordFactory.getInstance().create();
                    recInPara.setField("YD_EQP_ID", szEqpId);
                    rsWrkBook = JDTORecordFactory.getInstance().createRecordSet("");
                    intRtnVal = ydWrkbookDao.getYdWrkbook(recInPara, rsWrkBook, 4);
                    if(intRtnVal > 0) {
                        //검색된 작업예약을 크레인스케줄을 생성한다. 생성하면 설비상태값에 따라서 작업지시를 내려보내도록되어있다.
                        szMsg="현재 크레인(" + szEqpId + ")로 작업할 수 있는 작업예약을 조회하였습니다.  크레인스케줄Main호출";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        //크레인 스케줄 호출 (설비id,스케줄코드);
                        rsWrkBook.absolute(1);
                        recOutTemp = JDTORecordFactory.getInstance().create();
                        recOutTemp.setRecord(rsWrkBook.getRecord());

                        recInPara = JDTORecordFactory.getInstance().create();

                        recInPara.setField("MSG_ID", "YDYDJ503");
                        recInPara.setField("YD_EQP_ID", szEqpId);
                        recInPara.setField("YD_SCH_CD", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"));
                        recInPara.setField("YD_WBOOK_ID_YJK", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID")); //CHITO 2011.03.30 추가

                        //크레인 스케줄 호출 메세지 전송
                        ydDelegate.sendMsg(recInPara);
                        //다음 크레인스케줄이 생성되는 중이기때문에 현재상태에서는 작업지시를 내려보내지않고 리턴
                        return YdConstant.RETN_CD_SUCCESS;
                    }

                    szMsg="현재 크레인(" + szEqpId + ")로 작업할 수 있는 작업예약이 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                     * 업무기준 : 자동준비작업 기능 호출
                     * 수정자 : 윤재광
                     * 일자 : 2010.09.29
                     ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

                    recInPara = JDTORecordFactory.getInstance().create();
                    recInPara.setField("MSG_ID", "YDYDJ294");
                    recInPara.setField("YD_EQP_ID", szEqpId);

                    //크레인 스케줄 호출 메세지 전송
                    ydDelegate.sendMsg(recInPara);

                    //EJBConnector ydEjbCon = new EJBConnector("default", this);
                    //ydEjbCon.trx("IssueWrkDmdFaEJB", "rcvAutoWorkLotComp", recInPara);

                    szMsg = "[크레인 작업지시]A후판 자동준비작업 기능 호출";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    return YdConstant.RETN_CRN_NO_WRK;

                }else{
                    //다음크레인 작업을 찾았을경우 작업지시상태로 변경
                    recCrnSch = JDTORecordFactory.getInstance().create();
                    recCrnSch.setRecord(rsCrnSch.getRecord(0));

                    String sSchYdWrkProgStat    = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WRK_PROG_STAT");
                    String sCrnYdWrkProgStat    = "";

                    if( sSchYdWrkProgStat.equals("") || sSchYdWrkProgStat.equals("W") || sSchYdWrkProgStat.equals("1") ) {
                        sCrnYdWrkProgStat   = "1";
                    }else{
                        sCrnYdWrkProgStat   = sSchYdWrkProgStat;
                    }

                    szMsg=" 윤재광 윤재광 : data ==========================="+sCrnYdWrkProgStat;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                    recIntTemp = JDTORecordFactory.getInstance().create();
                    recIntTemp.setField("YD_EQP_ID", szEqpId);
                    recIntTemp.setField("YD_EQP_STAT", sCrnYdWrkProgStat);

                    intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
                    if(intRtnVal <= 0) {
                        if(intRtnVal == 0) {
                            szMsg=" Y3ChkWrkProgStatW updYdEqp : data not found";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                        }else if(intRtnVal == -1) {
                            szMsg=" Y3ChkWrkProgStatW updYdEqp : duplicate data,";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        }else if(intRtnVal == -2) {
                            szMsg=" Y3ChkWrkProgStatW updYdEqp : parameter error";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        }else if(intRtnVal == -3){
                            szMsg=" Y3ChkWrkProgStatW updYdEqp : execution failed";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        }
                    }
                }
            }

            recCrnSch = JDTORecordFactory.getInstance().create();
            rsCrnSch.absolute(1);
            recCrnSch.setRecord(rsCrnSch.getRecord());

            String szYD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WRK_PROG_STAT");

            recInPara = JDTORecordFactory.getInstance().create();
            //작업지시 전문 전송 data setup
            recInPara.setField("MSG_ID", "YDY3L004");
            recInPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID"));

            if( szYD_WRK_PROG_STAT.equals("") || szYD_WRK_PROG_STAT.equals("W") || szYD_WRK_PROG_STAT.equals("1")) {
                recInPara.setField("YD_WRK_PROG_STAT", "1");
            }else{
                recInPara.setField("YD_WRK_PROG_STAT", szYD_WRK_PROG_STAT);
            }

            recInPara.setField("YD_WORD_DT",      YdUtils.getCurDate("yyyyMMddHHmmss"));
            recInPara.setField("MODIFIER", "YDSYSTEM");

            //크레인스케줄의 작업진행 상태를 권상지시로 변경
//          intRtnVal = YdCrnSchDao.updYdCrnsch(recInPara, 0);

            /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnWrkProgStat*/
            intRtnVal = YdCrnSchDao.updYdCrnschDelay(recInPara, 302);
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="procY3CrnWrkOrdReq updYdCrnsch : data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                }else if(intRtnVal == -2) {
                    szMsg="procY3CrnWrkOrdReq updYdCrnsch : parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else{
                    szMsg="procY3CrnWrkOrdReq updYdCrnsch : execution failed";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return YdConstant.RETN_CD_FAILURE;
            }
            ydDelegate.sendMsg(recInPara);

            // Flex(실시간작업을위함)
            JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
            recFlex.setField("YD_GP",  YdConstant.YD_GP_A_PLATE_SLAB_YARD);
            recFlex.setField("YD_EQP_ID", szEqpId);
            ydUtils.putYdFlexCrnWrk("", recFlex);

        }catch(Exception e){
            szMsg="Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return YdConstant.RETN_CD_FAILURE;
        }

        szMsg="크레인 작업지시("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return YdConstant.RETN_CD_SUCCESS;

    } //end of procY3CrnWrkOrdReq()

    /**
     * 오퍼레이션명 : 크레인 작업지시(작업지시를 재요구 하는 경우 사용한다.)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public int Y3ChkWrkProgStat(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws JDTOException  {
        YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
        YdEqpDao    ydEqpDao    = new YdEqpDao();

        JDTORecordSet rsResult          = null;
        JDTORecord recInTemp            = null;
        JDTORecord recOutTemp           = null;

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y3ChkWrkProgStat";
        String szCrnSchId               = "";

        String szQuery                  = "";

        String szYD_EQP_ID              = "";
        String szWrkProgStat            = "";
        String szYdEqpStat              = "";


        try{
//          //파라미터 중 스케줄ID가 있다 스케줄 ID로 크레인 스케줄을 조회하여 작업예약 ID를 조회한다.
//          szCrnSchId = ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID");
//
//          rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//          intRtnVal = YdCrnSchDao.getYdCrnsch(msgRecord, rsResult, 0);
//          if(intRtnVal <= 0) {
//              szMsg="크레인 스케줄 조회중 Error";
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//          }


            //작업진행상태
            szWrkProgStat = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");


            //설비Table를 조회하여 설비상태가 '1 : 권상지시'또는 '3 : 권하지시'이라면 크레인 스케줄 Table에서 야드작업진행 상태가 1,2,3인 것만 찾는다. 1건만 나와야 정상이고 1건이 아니라면 Error처리
            rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
            intRtnVal = ydEqpDao.getYdEqp(msgRecord, rsResult, 0);
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="Y3ChkWrkProgStat getYdEqp : data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                }else if(intRtnVal == -2) {
                    szMsg="Y3ChkWrkProgStat getYdEqp : parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return intRtnVal = -1;
            }

            recOutTemp = JDTORecordFactory.getInstance().create();
            recOutTemp.setRecord(rsResult.getRecord(0));
            szYdEqpStat = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_STAT");
            //설비상태가 1또는 3인경우
            if(szYdEqpStat.equals("1") || szYdEqpStat.equals("2") || szYdEqpStat.equals("3")){
                //설비id로 크레인 스케줄을 조회한다. 현재 작업진행상태가 1또는 3인 경우를 조회...
                rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
                intRtnVal = ydCrnSchDao.getYdCrnsch(msgRecord, rsResult, 16);
                if(intRtnVal <= 0) {
                    //에러처리
                    szMsg="현재 작업진행상태가 1또는 3인 크레인 스케줄을 조회 중 Error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    //return intRtnVal = -1;
                    return intRtnVal;
                }
            }

            rsCrnSch.addAll(rsResult);

        }catch(Exception e){

            szMsg="Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }


        szMsg="chkWrkProgStat("+szMethodName+") 처리 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y3ChkWrkProgStat()







    /**
     * 오퍼레이션명 : 크레인 작업지시(작업이 없을 경우 요구한다.)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public int Y3ChkWrkProgStatW(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws JDTOException  {
        YdEqpDao ydEqpDao = new YdEqpDao();
        YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
        JDTORecordSet rsResult = null;
        JDTORecord recIntTemp  = null;
        JDTORecord para  = null;

        int intRtnVal          = 0 ;


        String szMsg           = "";
        String szMethodName    = "Y3ChkWrkProgStatW";
        String szQuery         = "";

        String szYD_EQP_ID     = "";
        try{
            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");

            //크레인 스케줄 전체에서 우선순위가 가장 빠른 작업을 조회한다. 크레인 스케줄을 조회한다.
            rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
            para = JDTORecordFactory.getInstance().create();
            para.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"));

            intRtnVal = ydCrnSchDao.getYdCrnsch(para, rsResult, 15);
            if(intRtnVal == 0) {
                //더이상 크레인 스케줄에 작업이 없을 경우 설비Table에 설비상태를 'W' 명령선택대기(idle)로 변경한다.
                recIntTemp = JDTORecordFactory.getInstance().create();
                recIntTemp.setField("YD_EQP_ID", szYD_EQP_ID);
                recIntTemp.setField("YD_EQP_STAT", "W");
                intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
                if(intRtnVal <= 0) {
                    if(intRtnVal == 0) {
                        szMsg=" Y3ChkWrkProgStatW updYdEqp : data not found";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                    }else if(intRtnVal == -1) {
                        szMsg=" Y3ChkWrkProgStatW updYdEqp : duplicate data,";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }else if(intRtnVal == -2) {
                        szMsg=" Y3ChkWrkProgStatW updYdEqp : parameter error";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }else if(intRtnVal == -3){
                        szMsg=" Y3ChkWrkProgStatW updYdEqp : execution failed";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }
                }

                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                 *          A후판슬라브야드L2 크레인작업실적응답 전송  - YDY3L005
                 * 업무기준 Desc : A후판슬라브야드L2에서 크레인 작업지시 요구 시 크레인 작업이 더 이상 존재하지 않을 경우 크레인작업실적응답 전송
                 * 기능 추가 : 임춘수
                 * 일자 : 2009.06.19
                 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                recIntTemp = JDTORecordFactory.getInstance().create();
                recIntTemp.setField("MSG_ID"        , "YDY3L005");
                recIntTemp.setField("YD_EQP_ID"     , szYD_EQP_ID);                     //야드설비ID
                recIntTemp.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_WO_DMD);    //야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
                recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK); //야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
                ydDelegate.sendMsg(recIntTemp);
                szMsg = "[A후판 크레인 작업지시 요구]크레인 작업이 더 이상 존재하지 않을 경우 A후판슬라브야드L2 크레인작업실적응답[YDY3L005] 전송 완료" ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

                szMsg="더이상의 크레인스케줄이 조회되지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);

                return intRtnVal = 0;

            }else if(intRtnVal < 0) {
                szMsg="크레인 스케줄 조회중 Error";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return intRtnVal = -1;
            }

            //-------------------------------------------------------------------------------------------------------------
            //  다음스케줄의 작업상태를 체크하여 W이면 설비의 작업상태를 1로 변경, 아니면 변경하지 않음
            //  수정자 : 임춘수
            //  수정일 : 2009.12.21
            //-------------------------------------------------------------------------------------------------------------

            //-------------------------------------------------------------------------------------------------------------
            //  먼저 복사해서 아래부분에서 사라지는 문제를 해결
            //-------------------------------------------------------------------------------------------------------------
            rsCrnSch.addAll(rsResult);
            //-------------------------------------------------------------------------------------------------------------

            rsResult.first();

            recIntTemp  = rsResult.getRecord();

            String szYD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recIntTemp, "YD_WRK_PROG_STAT");

            //-------------------------------------------------------------------------------------------------------------


            //다음 스케줄을 찾았을 경우
            if( szYD_WRK_PROG_STAT.equals("") || szYD_WRK_PROG_STAT.equals(YdConstant.YD_EQP_STAT_IDLE))    {
                recIntTemp = JDTORecordFactory.getInstance().create();
                recIntTemp.setField("YD_EQP_ID",        szYD_EQP_ID);
                recIntTemp.setField("YD_EQP_STAT",      "1");
                recIntTemp.setField("YD_WORD_DT",       YdUtils.getCurDate("yyyyMMddHHmmss"));
                intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
                if(intRtnVal <= 0) {
                    if(intRtnVal == 0) {
                        szMsg=" Y3ChkWrkProgStatW updYdEqp : data not found";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                    }else if(intRtnVal == -1) {
                        szMsg=" Y3ChkWrkProgStatW updYdEqp : duplicate data,";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }else if(intRtnVal == -2) {
                        szMsg=" Y3ChkWrkProgStatW updYdEqp : parameter error";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }else if(intRtnVal == -3){
                        szMsg=" Y3ChkWrkProgStatW updYdEqp : execution failed";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }
                    return intRtnVal = -1;
                }
            }else{
                szMsg="크레인의 스케줄의 야드작업진행상태["+szYD_WRK_PROG_STAT+"]가 W가 아니므로 크레인설비의 상태를 변경하지 않음";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            }


        }catch(Exception e){

            szMsg="Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }


        szMsg="chkWrkProgStatW("+szMethodName+") 처리완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;

    } //end of Y3ChkWrkProgStatW()



    /**
     * 오퍼레이션명 : 크레인 작업지시(현재 진행중인 작업이 있을경우 해당작업)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public int Y3ChkWrkProgStat4(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws JDTOException  {
         YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();

        JDTORecordSet rsResult          = null;
        JDTORecord    recInPara         = null;

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y3ChkWrkProgStat4";

        String szSchCd                  = "";
        String szQuery                  = "";

        try{
            //스케줄 코드 Check
            szSchCd = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
            if(szSchCd.equals("")) {
                szMsg="스케줄코드가 없습니다. : parameter error";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return intRtnVal = -1;
            }

            rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
            intRtnVal = ydCrnSchDao.getYdCrnsch(msgRecord, rsResult, 55);
            //조회된 크레인 스케줄이 없다면  전체에서 빠른 스케줄을 호출한다.
            if(intRtnVal <= 0) {
                rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
                recInPara = JDTORecordFactory.getInstance().create();
                recInPara.setRecord(msgRecord);
                intRtnVal = this.Y3ChkWrkProgStatW(recInPara, rsResult);
                if(intRtnVal == -1) {
                    szMsg="크레인 작업 조회 중 Error!!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return intRtnVal = -1;
                }else if(intRtnVal == 0) {
                    szMsg="같은 스케줄 코드의 크레인 작업이 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return intRtnVal = 0;
                }
            }

            rsCrnSch.addAll(rsResult);

        }catch(Exception e){

            szMsg="Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }


        szMsg="크레인 작업지시("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y3ChkWrkProgStat4()







    /**
     * 오퍼레이션명 : A후판SLAB 크레인권상실적등록 (Y3YDL008)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
     * @ejb.transaction type="RequiresNew"
     */
    public String procY3CrnLdWr(JDTORecord msgRecord)throws DAOException  {
        YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
        YdDelegate ydDelegate = new YdDelegate();
        YdEqpDao   ydEqpDao   = new YdEqpDao();
        YdCarSchDao ydCarSchDao = new YdCarSchDao();
        YdStkColDao ydStkColDao = new YdStkColDao();

        //업데이트 할 크레인 스케줄 Data 항목 set
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();

        //파라미터 null check 후 받아온 Data
        JDTORecord getParamRecord = JDTORecordFactory.getInstance().create();


        //스케줄Table의 컬럼을 저장하기위해 생성
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet  rsResult = null;
        //레코드 셋의 레코드값을 받음
        JDTORecord getRecord = JDTORecordFactory.getInstance().create();
        JDTORecord recInTemp = null;

        JDTORecord recSendMsg = null;

        int intRtnVal                   = 0 ;

        String szWbookId                = "";

        String szMsg="";
        String szMethodName="procY3CrnLdWr";

        String szTcarEqpId = "";
        //크레인스케줄ID
        String szYD_CRN_SCH_ID = "";
        //야드스케줄코드
        String szYD_SCH_CD = null;
        //권상실적위치
        String szYD_UP_WR_LOC = null;
        //설비ID(크레인설비ID)
        String szYD_EQP_ID = null;
        //야드To위치결정방법
        String szYD_TO_LOC_DCSN_MTD = null;
        //권하지시위치
        String szYD_DN_WO_LOC = null;
        //야드목표야드구분
        String szYD_AIM_YD_GP = null;


        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null){
            szMsg=szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return YdConstant.RETN_CD_TC_ERROR;
        }
        if(bDebugFlag){
            szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }


        try{
            //=============================================================
            // 권오창
            // 2009.11.05
            //
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판슬라브야드] 크레인권상실적등록 수신";
            ydUtils.putLogMsg("D", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            //파라미터 check
            intRtnVal = this.Y3ParamCheck(msgRecord, getParamRecord, 0) ;

            //크레인스케줄ID
            szYD_CRN_SCH_ID = getParamRecord.getFieldString("YD_CRN_SCH_ID");
            //야드스케줄코드
            szYD_SCH_CD = getParamRecord.getFieldString("YD_SCH_CD");
            //권상실적위치
            szYD_UP_WR_LOC = getParamRecord.getFieldString("YD_UP_WR_LOC");
            //설비ID(크레인설비ID)
            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(getParamRecord, "YD_EQP_ID");

            setCrnschRecord.setField("YD_CRN_SCH_ID",         getParamRecord.getFieldString("YD_CRN_SCH_ID"));
            setCrnschRecord.setField("YD_SCH_CD",             getParamRecord.getFieldString("YD_SCH_CD")) ;
            setCrnschRecord.setField("YD_UP_WR_LOC",          getParamRecord.getFieldString("YD_UP_WR_LOC")) ;
            setCrnschRecord.setField("YD_UP_WR_LAYER",        getParamRecord.getFieldString("YD_UP_WR_LAYER")) ;

            //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
            intRtnVal = this.Y3UpdYdCrnsch(setCrnschRecord, 0) ;
            if(intRtnVal == -1) return YdConstant.RETN_CD_FAILURE;

            //Key Data Check!           키값은 Null이나 ""가 되어서는 안됨.
            if(setCrnschRecord.getFieldString("YD_CRN_SCH_ID").equals("") ||
               setCrnschRecord.getFieldString("YD_CRN_SCH_ID") == null) {
                szMsg = "'YD_CRN_SCH_ID' Data Error : 크레인스케줄ID가 없습니다." ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
                throw new DAOException(szMsg);
            }

            //대상 데이터 SELECT
            intRtnVal = this.Y3GetYdCrnsch(setCrnschRecord, getRecSet,3);

            if( intRtnVal <= 0 ) {
                szMsg = "스케쥴 Data가 존재하지 않습니다. ="+ intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
                throw new DAOException(szMsg);
            }

            //레코드셋의 사이즈값으로 ErrorCheck
            if(getRecSet.size() == 0){
                szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
                throw new DAOException(szMsg);
            }

            getRecSet.first();
            getRecord = getRecSet.getRecord();

            //작업예약ID
            szWbookId = ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID");
            //야드To위치결정방법
            szYD_TO_LOC_DCSN_MTD = ydDaoUtils.paraRecChkNull(getRecord, "YD_TO_LOC_DCSN_MTD");
            //권하지시위치
            szYD_DN_WO_LOC = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC");
            //야드목표야드구분
            szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(getRecord, "YD_AIM_YD_GP");

            //조회한 크레인 스케줄의 작업활성상태가 check를 한다.
            if (getRecord.getFieldString("YD_WRK_PROG_STAT").equals("1")||
                getRecord.getFieldString("YD_WRK_PROG_STAT").equals("W")) {

                // 적치단 정보 Clear            (1개의 크레인스케줄에 잡혀있는 크레인작업재료의 정보를 모두 Check!)
                intRtnVal = this.Y3LdClearYdStklyr(getRecSet, 0);

                //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
                setCrnschRecord = JDTORecordFactory.getInstance().create();
                setCrnschRecord.setField("YD_CRN_SCH_ID",      getParamRecord.getFieldString("YD_CRN_SCH_ID"));
                setCrnschRecord.setField("YD_WRK_PROG_STAT",   getParamRecord.getFieldString("YD_WRK_PROG_STAT"));
                setCrnschRecord.setField("YD_EQP_ID",          getParamRecord.getFieldString("YD_EQP_ID"));
                setCrnschRecord.setField("YD_UP_WR_LOC",       getParamRecord.getFieldString("YD_UP_WR_LOC"));
                setCrnschRecord.setField("YD_UP_WR_LAYER",     getParamRecord.getFieldString("YD_UP_WR_LAYER"));
                setCrnschRecord.setField("YD_UP_WRK_ACT_GP",   getParamRecord.getFieldString("YD_UP_WRK_ACT_GP"));
                setCrnschRecord.setField("YD_UP_WR_XAXIS",     getParamRecord.getFieldString("YD_CRN_XAXIS"));
                setCrnschRecord.setField("YD_UP_WR_YAXIS",     getParamRecord.getFieldString("YD_CRN_YAXIS"));
                setCrnschRecord.setField("YD_UP_WR_ZAXIS",     getParamRecord.getFieldString("YD_CRN_ZAXIS"));
                //권상완료일시
                setCrnschRecord.setField("YD_UP_CMPL_DT",      YdUtils.getCurDate("yyyyMMddHHmmss"));

                intRtnVal = this.Y3UpdYdCrnsch(setCrnschRecord, 0);

                //설비Table의 상태 변경 (권하상태로 변경)
                setCrnschRecord = JDTORecordFactory.getInstance().create();
                setCrnschRecord.setField("YD_EQP_ID",          getParamRecord.getFieldString("YD_EQP_ID"));
                setCrnschRecord.setField("YD_EQP_STAT",        getParamRecord.getFieldString("YD_WRK_PROG_STAT"));

                intRtnVal = ydEqpDao.updYdEqp(setCrnschRecord, 0);
                if(intRtnVal <= 0) {
                     szMsg="설비상태 UPDATE 처리시 오류 발생.";
                     ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    m_ctx.setRollbackOnly();
                    throw new DAOException(szMsg);
                }

                //대차 및 차량 스케줄 이송재료 Handling
                if (ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TC")){

                    //대차스케쥴 이송재료 정보 셋팅.
                     this.Y3SetYdTcar(getRecSet) ;

                    szMsg = "권상시 대차이송재료 삭제 완료" ;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    szTcarEqpId = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(0,1) + "X" +
                                  ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2,6);

                    //대차스케줄 호출
                    recSendMsg = JDTORecordFactory.getInstance().create();
                    recSendMsg.setField("MSG_ID", "YDYDJ522");
                    recSendMsg.setField("YD_LD_UD_GP", "U");
                    recSendMsg.setField("YD_EQP_ID", szTcarEqpId);
                    recSendMsg.setField("YD_WBOOK_ID", szWbookId);
                    //ydDelegate.sendMsg(recSendMsg);
                    ydEjbCon.trx("TransEqpSchSeEJB", "procY3TcarSch", recSendMsg);

                } else if (ydDaoUtils.paraRecChkNull(getParamRecord,"YD_UP_WR_LOC").substring(2, 4).equals("PT")||
                  ydDaoUtils.paraRecChkNull(getParamRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TR")){

                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                     *           하차개시 전문 송신 처리 - 구내운송
                     * 업무기준 Desc : 권상실적위치로 적치열에서 운송장비코드를 조회하고 그 코드로 차량스케줄을 조회하여 차량진행상태가
                     *              하차검수 or 하차도착이면 하차개시로 차량스케줄을 업데이트하고 전문을 전송한다.
                     * 기능 추가 : 임춘수
                     * 일자 : 2009.07.15
                     +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                    //권상실적위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
                    recInTemp = JDTORecordFactory.getInstance().create();
                    recInTemp.setField("YD_STK_COL_GP", szYD_UP_WR_LOC.substring(0, 6));
                    rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                    intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsResult, 0);
                    if( intRtnVal <= 0 ) {
                        szMsg = "[권상실적처리 - 하차개시]차량정지위치[" + szYD_UP_WR_LOC.substring(0, 6) + "] 정보가 존재하지 않습니다." ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }else{
                        //조회된 차량정지위치에서 운송장비코드를 가져온다.
                        rsResult.first();
                        recInTemp = rsResult.getRecord();
                        //운송장비코드
                        String  szTRN_EQP_CD    = ydDaoUtils.paraRecChkNull(recInTemp, "TRN_EQP_CD");
                        String szYD_CAR_USE_GP  = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");
                        String szCAR_NO         = ydDaoUtils.paraRecChkNull(recInTemp, "CAR_NO");
                        String szCARD_NO        = ydDaoUtils.paraRecChkNull(recInTemp, "CARD_NO");
                        //운송장비코드로 차량스케줄조회
                        recInTemp = JDTORecordFactory.getInstance().create();
                        recInTemp.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
                        recInTemp.setField("TRN_EQP_CD", szTRN_EQP_CD);
                        recInTemp.setField("CAR_NO", szCAR_NO);
                        recInTemp.setField("CARD_NO", szCARD_NO);
                        rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                        intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 27);
                        if( intRtnVal <= 0 ) {
                            szMsg = "[권상실적처리 - 하차개시]차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]에 해당하는 차량스케줄이 존재하지 않습니다." ;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        }else{
                            //차량진행상태를 파악하여 하차검수이거나 하차도착일 때만 하차개시 전문을 송신한다.
                            rsResult.absolute(1);
                            recInTemp = rsResult.getRecord();
                            //차량스케줄ID
                            String szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_SCH_ID");
                            //야드차량진행상태
                            String szYD_CAR_PROG_STAT = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_PROG_STAT");
                            //차량사용구분
                            szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");
                            //하차검수이거나 하차도착일 때 하차개시 전문 송신
                            if( szYD_CAR_PROG_STAT.equals("C") || szYD_CAR_PROG_STAT.equals("B") ) {
                                String szYD_CARUD_ST_DT = YdUtils.getCurDate("yyyyMMddHHmmss");
                                recInTemp = JDTORecordFactory.getInstance().create();
                                recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);                               //차량스케줄ID
                                recInTemp.setField("YD_CAR_PROG_STAT", "D");                                        //차량진행상태
                                recInTemp.setField("YD_CARUD_ST_DT", szYD_CARUD_ST_DT);                             //하차개시일시
                                intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
                                if(intRtnVal <= 0) {
                                    szMsg="[권상실적처리 - 하차개시]차량스케줄에 하차 개시 등록시 Error!! Code : " + intRtnVal;
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    m_ctx.setRollbackOnly();
                                    throw new DAOException(szMsg);
                                }

                                String szYD_GP          = szYD_SCH_CD.substring(0,1);
                                if( szYD_CAR_USE_GP.equals("L") ) {
                                    //하차작업개시 송신 YDTSJ009
                                    recInTemp = JDTORecordFactory.getInstance().create();
                                    recInTemp.setField("MSG_ID",        "YDTSJ009");
                                    recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
                                    recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
                                    recInTemp.setField("YD_GP",         szYD_GP);
                                    recInTemp.setField("YD_CARUD_ST_DT",         szYD_CARUD_ST_DT);
                                    ydDelegate.sendMsg(recInTemp);

                                    szMsg="[권상실적처리 - 하차개시]하차작업개시[YDTSJ009] 송신 완료";
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                }
                            }
                        }
                    }
                    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

                    intRtnVal = this.YSCSetYdCar(getRecSet, 0) ;
                    szMsg = "권상시 차량이송재료 삭제 완료" ;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                }

                szMsg = "[권상실적처리] 상차개시 진입 전 - 권하지시위치[" + szYD_DN_WO_LOC + "], " + "설비구분[" + szYD_DN_WO_LOC.substring(2, 4) + "]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                 *           상차개시 전문 송신 처리 - 구내운송, 출하관리
                 * 업무기준 Desc : 권하지시위치로 적치열에서 운송장비코드를 조회하고 그 코드로 차량스케줄을 조회하여 차량진행상태가
                 *              상차검수 or 상차도착이면 상차개시로 차량스케줄을 업데이트하고 전문을 전송한다.
                 * 기능 추가 : 임춘수
                 * 일자 : 2009.07.15
                 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                //권하지시위치가 차상인 경우 상차개시 전문 송신 처리
                if( szYD_DN_WO_LOC.substring(2, 4).equals("PT") ||
                    szYD_DN_WO_LOC.substring(2, 4).equals("TR") ) {
                    //권하지시위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
                    recInTemp = JDTORecordFactory.getInstance().create();
                    recInTemp.setField("YD_STK_COL_GP", szYD_DN_WO_LOC.substring(0, 6));
                    rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                    intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsResult, 0);
                    if( intRtnVal <= 0 ) {
                        szMsg = "[권상실적처리 - 상차개시]차량정지위치[" + szYD_DN_WO_LOC.substring(0, 6) + "] 정보가 존재하지 않습니다." ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }else{
                        //조회된 차량정지위치에서 운송장비코드를 가져온다.
                        rsResult.first();
                        recInTemp = rsResult.getRecord();
                        //운송장비코드
                        String  szTRN_EQP_CD    = ydDaoUtils.paraRecChkNull(recInTemp, "TRN_EQP_CD");
                        String szYD_CAR_USE_GP  = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");
                        String szCAR_NO         = ydDaoUtils.paraRecChkNull(recInTemp, "CAR_NO");
                        String szCARD_NO        = ydDaoUtils.paraRecChkNull(recInTemp, "CARD_NO");
                        szMsg = "[권상실적처리 - 상차개시]차량정지위치[" + szYD_DN_WO_LOC.substring(0, 6) + "] 정보가 존재하므로 차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]로 차량스케줄 조회" ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        //운송장비코드로 차량스케줄조회
                        recInTemp = JDTORecordFactory.getInstance().create();
                        recInTemp.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
                        recInTemp.setField("TRN_EQP_CD", szTRN_EQP_CD);
                        recInTemp.setField("CAR_NO", szCAR_NO);
                        recInTemp.setField("CARD_NO", szCARD_NO);
                        rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                        intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 27);
                        if( intRtnVal <= 0 ) {
                            szMsg = "[권상실적처리 - 상차개시]차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]에 해당하는 차량스케줄이 존재하지 않습니다." ;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        }else{
                            //차량진행상태를 파악하여 상차검수이거나 상차도착일 때만 상차개시 전문을 송신한다.
                            rsResult.absolute(1);
                            recInTemp = rsResult.getRecord();
                            //차량스케줄ID
                            String szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_SCH_ID");
                            //야드차량진행상태
                            String szYD_CAR_PROG_STAT = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_PROG_STAT");
                            //차량사용구분
                            szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");

                            szMsg = "[권상실적처리 - 상차개시]차량스케줄 조회 후 차량스케줄ID[" + szYD_CAR_SCH_ID + "], 야드차량진행상태[" + szYD_CAR_PROG_STAT + "], 차량사용구분[" + szYD_CAR_USE_GP + "]" ;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                            //상차검수이거나 상차도착일 때 상차개시 전문 송신
                            if( szYD_CAR_PROG_STAT.equals("3") || szYD_CAR_PROG_STAT.equals("2") ) {
                                /*
                                 * 야드목표야드구분은 작업예약에 등록된 목표야드를 사용한다.
                                 * 수정자 : 임춘수
                                 * 수정일 : 2009.11.03
                                 */
                                YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
                                JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
                                recInTemp = JDTORecordFactory.getInstance().create();
                                recInTemp.setField("YD_WBOOK_ID", szWbookId);
                                intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, outRecSet, 0);

                                if( intRtnVal > 0 ) {
                                    outRecSet.first();
                                    recInTemp = outRecSet.getRecord();
                                    szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recInTemp, "YD_AIM_YD_GP");
                                }
                                //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

                                //야드구분에 따른 개소코드 반환
//                              String szARR_WLOC_CD = YdCommonUtils.getWlocCd(szYD_AIM_YD_GP);
                                //장비코드로 구내운송에서 개소코드 가져 오기
                                String szARR_WLOC_CD = YdCommonUtils.getWlocCd2(szTRN_EQP_CD);
                                //차량스케줄 업데이트 - 상차개시
                                recInTemp = JDTORecordFactory.getInstance().create();
                                recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);                               //차량스케줄ID
                                recInTemp.setField("YD_CAR_PROG_STAT", "4");                                        //차량진행상태
                                recInTemp.setField("YD_EQP_WRK_STAT", "U");                                         //작업상태
                                recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szWbookId);                              //작업예약ID
                                recInTemp.setField("YD_CARLD_ST_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));         //상차개시일시
                                recInTemp.setField("MODIFIER",  "YDSYSTEM");                                        //수정자
                                if( szYD_CAR_USE_GP.equals("L") ) {                 //구내운송
                                    if(!szYD_DN_WO_LOC.substring(0,1).equals(szYD_AIM_YD_GP)){
                                        recInTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD);                               //착지개소코드
                                    }
                                }
                                intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);

                                if(intRtnVal <= 0) {
                                    szMsg="[권상실적처리]차량스케줄에 상차개시일시 등록시 Error!! Code : " + intRtnVal;
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    m_ctx.setRollbackOnly();
                                    throw new DAOException(szMsg);
                                }

                                recInTemp = JDTORecordFactory.getInstance().create();
                                if( szYD_CAR_USE_GP.equals("L") ) {                 //구내운송

                                    //상차작업개시 송신 YDTSJ007 (구내운송 상차개시)
                                    recInTemp.setField("MSG_ID",        "YDTSJ007");
                                    //착지개소코드
                                    recInTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD);

                                    szMsg="[권상실적처리]상차작업개시 송신 YDTSJ007 (구내운송 상차개시) 송신 시작";
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                                }else if( szYD_CAR_USE_GP.equals("G") ){            //출하차량

                                    //상차작업개시 송신 YDDMR009 (외판슬라브출하상차개시)
                                    recInTemp.setField("MSG_ID",        "YDDMR009");
                                    szMsg="[권상실적처리]상차작업개시 송신 YDDMR009 (외판슬라브출하상차개시) 송신 시작";
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                }


                                String szYD_GP          = szYD_SCH_CD.substring(0,1);

                                recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
                                recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
                                recInTemp.setField("YD_GP",         szYD_GP);

                                ydDelegate.sendMsg(recInTemp);

                                szMsg="[권상실적처리]상차작업개시 송신 완료";
                                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            }
                        }
                    }
                }

                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                 *          생산통제 A후판장입진행실적 전송  - YDCTJ031
                 * 업무기준 Desc : 1. A후판가열로 장입보급Carry-In
                 *                2. 보급베드 - DAPU01
                 *                3. 대상재의 야드목표행선 : C3[작업대기(A후판압연)]
                 * 스케줄코드 :  1. A후판가열로 장입보급Carry-In 스케줄
                 * 장입보급진행상태  : 20 - 크레인 보급권상(보급스케줄시행)
                 * 기능 추가 : 임춘수
                 * 일자 : 2009.06.19
                 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                if( ( szYD_TO_LOC_DCSN_MTD.equals("S") ||
                      szYD_TO_LOC_DCSN_MTD.equals("T") ||
                      szYD_TO_LOC_DCSN_MTD.equals("F") ||
                      szYD_TO_LOC_DCSN_MTD.equals("A") )                    /* 야드To위치결정방법 - 최종위치 결정인 경우*/
                    &&
                    ( szYD_DN_WO_LOC.startsWith(YdConstant.EQP_D_PU1) )     /* 권하지시위치가 보급베드(DAPU01) */
                    &&
                    ( szYD_SCH_CD.equals(YdConstant.SCH_CD_D_REFUR_SUP1) )  /* 보급CARRY-IN스케줄 - 크레인 보급권상 */
                ) {
                    recInTemp = JDTORecordFactory.getInstance().create();
                    recInTemp.setField("MSG_ID",        "YDCTJ031");
                    recInTemp.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
                    recInTemp.setField("CHG_SUP_PROG_STAT", "20");
                    ydDelegate.sendMsg(recInTemp);
                    szMsg = "[권상실적처리]생산통제 A후판장입진행실적[YDCTJ031] 전송 완료 - 크레인 보급권상(보급스케줄시행)" ;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                }

                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                 *          A후판슬라브야드L2 크레인작업실적응답 전송  - YDY3L005
                 * 업무기준 Desc : 크레인 권상실적처리 성공 후 크레인작업실적응답 전송
                 * 기능 추가 : 임춘수
                 * 일자 : 2009.06.19
                 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                szYD_EQP_ID = ydDaoUtils.paraRecChkNull(getParamRecord, "YD_EQP_ID");
                recInTemp = JDTORecordFactory.getInstance().create();
                recInTemp.setField("MSG_ID"        , "YDY3L005");
                recInTemp.setField("YD_EQP_ID"     , szYD_EQP_ID);              //야드설비ID
                recInTemp.setField("YD_WRK_PROG_STAT"   , YdConstant.YD_EQP_STAT_UP_CMPL);                  //야드작업진행상태
                recInTemp.setField("YD_SCH_CD"   , szYD_SCH_CD);                //야드스케줄코드
                recInTemp.setField("YD_CRN_SCH_ID"   , szYD_CRN_SCH_ID);        //야드크레인스케줄ID
                recInTemp.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_LD_WR);                      //야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
                recInTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NORMAL_HD);               //야드L3처리결과코드
                ydDelegate.sendMsg(recInTemp);
                szMsg = "[권상실적처리]A후판슬라브야드L2 크레인작업실적응답[YDY3L005] 전송 완료" ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

            }else{
                szMsg = "YD_WRK_PROG_STAT data : '1' or 'w' not" ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                 *          C연주슬라브야드L2 크레인작업실적응답 전송  - YDY3L005
                 * 업무기준 Desc : 크레인 권상실적처리 실패와 에러발생 시 크레인작업실적응답 전송
                 * 기능 추가 : 임춘수
                 * 일자 : 2009.06.17
                 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                //에러코드와 에러메세지에 대한 업무정의 후 전송 처리 필요.

                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                m_ctx.setRollbackOnly();
                throw new DAOException(szMsg);
            }

            szMsg="권상 완료 실적 처리 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //테스트 임시 방편용 (권하실적처리호출)
            /*
            JDTORecord recInPara = JDTORecordFactory.getInstance().create();
            JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
            recInPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
            intRtnVal = ydCrnschDao.getYdCrnsch(recInPara, outRecSet, 0);
            if(intRtnVal < 0) {
                szMsg="권하실적처리를 위해 크레인스케줄 조회중 Error!!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                return YdConstant.RETN_CD_FAILURE;
            }
            outRecSet.absolute(1);
            JDTORecord recOutPara = JDTORecordFactory.getInstance().create();
            recOutPara.setRecord(outRecSet.getRecord());
            */
            //------------------------------------------------------------------
            // 권상 실적시 Flex 실시간 처리
            //------------------------------------------------------------------
            JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
            recFlex.setField("YD_GP",  YdConstant.YD_GP_A_PLATE_SLAB_YARD);
            recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
            recFlex.setField("YD_UP_WR_LOC", szYD_UP_WR_LOC);
            szMsg="Flex 권상 완료 실적 전송";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putYdFlexCrnWrk("", recFlex);
            //------------------------------------------------------------------
        }catch(Exception e) {
            szMsg="Error :  "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            m_ctx.setRollbackOnly();
            throw new DAOException(szMsg);
        }//end of try~catch

        return YdConstant.RETN_CD_SUCCESS;
    }// end of procY3CrnLdWr()

    /**
     * 오퍼레이션명 : 권상 파라미터 체크
     *
     * @param  ● msgRecord, outRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y3ParamCheck (JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
        JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        String szMsg                        = "" ;
        String szMethodName                 = "Y3ParamCheck";
        int intRtnVal = 0 ;

        try{
            //======================================================================================================
            // LOG 출력 - 그냥 테스트용으로 출력 레코드값체크하는 코드는 제외하고 나중에 삭제
            szMsg = "[1] 야드설비ID : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[2] 야드설비작업Mode : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[3] 야드작업진행상태 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[4] 야드스케쥴코드 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[5] 야드크레인스케쥴ID : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[6] 야드권상실적위치 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LOC");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[7] 야드권상실적단 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LAYER");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[8] 야드크레인X축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[9] 야드크레인Y축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[10] 야드크레인Z축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_ZAXIS");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            //======================================================================================================

            setRecord.setField("YD_CRN_SCH_ID"          , ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID")) ;
            setRecord.setField("YD_SCH_CD"              , ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD")) ;
            setRecord.setField("MSG_ID"                 , ydDaoUtils.paraRecChkNull(msgRecord,"MSG_ID")) ;
            setRecord.setField("DATE"                   , ydDaoUtils.paraRecChkNull(msgRecord,"DATE")) ;
            setRecord.setField("TIME"                   , ydDaoUtils.paraRecChkNull(msgRecord,"TIME")) ;
            setRecord.setField("MSG_GP"                 , ydDaoUtils.paraRecChkNull(msgRecord,"MSG_GP")) ;
            setRecord.setField("YD_EQP_ID"              , ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID")) ;
            setRecord.setField("YD_EQP_WRK_MODE"        , ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE")) ;
            setRecord.setField("YD_WRK_PROG_STAT"       , ydDaoUtils.paraRecChkNull(msgRecord,"YD_WRK_PROG_STAT")) ;
            setRecord.setField("YD_CRN_XAXIS"           , ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_XAXIS")) ;
            setRecord.setField("YD_CRN_YAXIS"           , ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_YAXIS")) ;
            setRecord.setField("YD_CRN_ZAXIS"           , ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_ZAXIS")) ;
            if(intGp == 0){
                setRecord.setField("YD_UP_WR_LOC"               , ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LOC")) ;
                setRecord.setField("YD_UP_WR_LAYER"             , ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LAYER")) ;

                //전문 송신지 위치 Check                :AUTO MANUAL BACKUP구분
                if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("1")) {
                    setRecord.setField("YD_UP_WRK_ACT_GP", "A") ;
                }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("9")) {
                    setRecord.setField("YD_UP_WRK_ACT_GP", "B") ;
                }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("0")) {
                    setRecord.setField("YD_UP_WRK_ACT_GP", "M") ;
                }
            }

            if(intGp == 1){
                setRecord.setField("YD_DN_WR_LOC"               , ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LOC")) ;
                setRecord.setField("YD_DN_WR_LAYER"             , ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LAYER")) ;

                //전문 송신지 위치 Check                :AUTO MANUAL BACKUP구분
                if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("1")) {
                    setRecord.setField("YD_DN_WRK_ACT_GP", "A") ;
                }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("9")) {
                    setRecord.setField("YD_DN_WRK_ACT_GP", "B") ;
                }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("0")) {
                    setRecord.setField("YD_DN_WRK_ACT_GP", "M") ;
                }
            }

            outRecord.addRecord(setRecord) ;

        }catch(Exception e){
            System.out.println("Error : "+ e.getLocalizedMessage());
            throw new JDTOException(e.getMessage());
        }//end of try~catch

        intRtnVal = 1 ;
        return intRtnVal;

    }//end of Y3ParamCheck()

    /**
     * 오퍼레이션명 : 크레인스케줄 Update
     *
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y3UpdYdCrnsch(JDTORecord msgRecord, int intGp) throws JDTOException {
        YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

        int intRtnVal = 0 ;

        String szMethodName = "Y3UpdYdCrnsch";
        String szMsg        = "";

        try{

            intRtnVal = ydCrnschDao.updYdCrnsch(msgRecord, intGp);
            switch (intRtnVal) {
                case 0  :
                    szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return intRtnVal = -1;
                case -1 :
                    szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return intRtnVal = -1;
                case -2 :
                    szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return intRtnVal = -1;
                case -3 :
                    szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return intRtnVal = -1;
            }
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal ;

    }// end of Y3UpdYdCrnsch

    /**
     * 오퍼레이션명 : 크레인스케줄 Select
     *
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y3GetYdCrnsch (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {

        YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

        int intRtnVal           = 0 ;

        String szMethodName = "Y3GetYdCrnsch";
        String szMsg        = "";

        try{

            intRtnVal = ydCrnschDao.getYdCrnsch(msgRecord, getRecSet, intGp);
            switch (intRtnVal) {
            case 0  :
                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                //return intRtnVal = -1;
                return intRtnVal;
            case -2 :
                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                //return intRtnVal = -1;
                return intRtnVal;
        }
            outRecSet.addAll(getRecSet);

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal ;

    }//end of Y3GetYdCrnsch()

    /**
     * 오퍼레이션명 : 적치단 Clear
     *
     * @param  ● getRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y3LdClearYdStklyr (JDTORecordSet getRecSet, int intGp) throws JDTOException {
        YdStkBedDao ydStkBedDao = new YdStkBedDao();

        JDTORecord getRecord = JDTORecordFactory.getInstance().create();
        JDTORecord setRecord = null;
        JDTORecord crnRecord = null;

        int intRtnVal       = 0;
        String szMsg        = "";
        String szMethodName = "Y3LdClearYdStklyr";
        String szYD_TO_LOC_DCSN_MTD = null;
        String szYD_STK_COL_GP = null;
        String szYD_STK_BED_NO = null;
        String szYD_UP_WR_LOC  = null;
        String szYD_UP_WR_LAYER = null;

        String szStkLyr = "";
        String szCrnId  = "";
        String szStlNo  = "";

        try{
            int rowsize = getRecSet.size();
            getRecSet.first();
            getRecord = getRecSet.getRecord();

            szYD_STK_COL_GP      = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(0,6);
            szYD_STK_BED_NO      = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(6,8);
            szYD_UP_WR_LOC       = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC");
            szYD_UP_WR_LAYER     = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LAYER");
            szYD_TO_LOC_DCSN_MTD = ydDaoUtils.paraRecChkNull(getRecord,"YD_TO_LOC_DCSN_MTD");

            for(int i = 0; i < rowsize; i++){

                getRecSet.absolute(i+1);
                getRecord = getRecSet.getRecord();
                szCrnId     = ydDaoUtils.paraRecChkNull(getRecord,"YD_EQP_ID");
                szStlNo     = ydDaoUtils.paraRecChkNull(getRecord,"STL_NO");

                //크레인에 UPDATE
                crnRecord = JDTORecordFactory.getInstance().create();
                crnRecord.setField("YD_STK_COL_GP",       szCrnId);
                crnRecord.setField("YD_STK_BED_NO",       "01");
                crnRecord.setField("YD_STK_LYR_NO",       "00"+(i+1)) ;
                crnRecord.setField("YD_STK_LYR_MTL_STAT", "C");
                crnRecord.setField("STL_NO",              szStlNo);

                intRtnVal = this.Y3UpdYdStklyr(crnRecord, 0);  //크레인 적치단의 재료정보 UPDATE

                //권상 지시위치 Clear
                setRecord = JDTORecordFactory.getInstance().create();
                setRecord.setField("YD_STK_COL_GP",       szYD_STK_COL_GP);
                setRecord.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);
                //적치단 설정
                szStkLyr = ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, i);
                setRecord.setField("YD_STK_LYR_NO",       szStkLyr) ;
                setRecord.setField("YD_STK_LYR_MTL_STAT", "E");
                setRecord.setField("STL_NO",              "");

                intRtnVal = this.Y3UpdYdStklyr(setRecord, 0);  //적치단의 재료정보 Clear
               /*
                * 2010.05.04 윤재광 수정
                * 아래의 소스 의미가 없슴. 주석처리
                */
                //getRecSet.next();
                //getRecord = getRecSet.getRecord();
            } //end of for

            /*  2009.10.05 김진욱 수정
             *  크레인 작업 편성시 주작업재료이면서 보조작업재료일때 해당 ToBed를 완산상태로 바꾸는 것을 순서모음 작업할때 완산상태를 풀어주도록한다.
             *  적치Bed의 입출고상태를 완산("F")에서 입출고가능("E")로 변경한다.
             */
            if(szYD_TO_LOC_DCSN_MTD.equals("R")){
                setRecord = JDTORecordFactory.getInstance().create();
                setRecord.setField("YD_STK_COL_GP",       szYD_STK_COL_GP);
                setRecord.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);
                setRecord.setField("YD_STK_BED_WHIO_STAT", "E");
                intRtnVal = ydStkBedDao.updYdStkbed(setRecord, 0);
                if(intRtnVal <= 0) {
                    szMsg = "[A후판SLAB 크레인 권상실적등록 중] 권상위치 입출고상태 변경 Error!!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
            }
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        intRtnVal = 1 ;
        return  intRtnVal;
    }//end of Y3LdClearYdStklyr()

    /**
     * 오퍼레이션명 : 적치단 Update
     *
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y3UpdYdStklyr (JDTORecord msgRecord, int intGp) throws JDTOException {
        YdStkLyrDao ydStklyrDao = new YdStkLyrDao();

        int intRtnVal = 0 ;

        String szMsg        = "";
        String szMethodName = "Y3UpdYdStklyr";

        try{

            intRtnVal = ydStklyrDao.updYdStklyr(msgRecord, intGp);  //적치단의 재료정보 Clear
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                    /*
                     * 모음작업시에는 단이 존재하지 않을 수 있으므로 적치단이 존재하지 않더라도
                     * 업무는 진행이 되도록 아래 부분을 수정
                     * 수정자 : 임춘수
                     * 수정일 : 2009.09.21
                     */
                    szMsg="적치단이 존재하지 않습니다. - 모음작업 시 오류발생 가능 ";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    intRtnVal = 1;
                }else if(intRtnVal == -1) {
                    szMsg="duplicate data,";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -2) {
                    szMsg="parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -3){
                    szMsg="execution failed";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return intRtnVal = -1;
            }

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal ;

    }//end of Y3UpdYdStklyr

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 오퍼레이션명 : 제품창고 크레인작업지시 (Y4YDL007),YDYDJ642
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
     */
    public String procY4CrnWrkOrdReq(JDTORecord msgRecord)throws DAOException  {

        YdDelegate   ydDelegate   = new YdDelegate();
        YdCrnSchDao  YdCrnSchDao  = new YdCrnSchDao();
        YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
        YdEqpDao     ydEqpDao     = new YdEqpDao();

        JDTORecord recCrnSch = JDTORecordFactory.getInstance().create();
        JDTORecord recInPara = null;
        JDTORecord recOutTemp = null;
        JDTORecord recIntTemp = null;

        JDTORecordSet rsCrnSch = JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet rsWrkBook = null;

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "procY4CrnWrkOrdReq";
        String szOperationName          = "후판제품크레인작업지시";

        String szYdGp                   = ""; //--2013.02.07 추가 (3기)
        String szEqpId                  = "";
        String szWrkProgStat            = "";

        //레코드 선언
        JDTORecord recPara              = null;
        //설비상태
        String szYD_EQP_STAT            = null;
        //야드설비작업Mode
        String szYD_EQP_WRK_MODE        = null;

        //스케쥴코드
        String szYD_SCH_CD              = "";

        boolean bRtnCheck               = true;
        boolean blnRtnVal               = true;
        //  2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
        boolean isSendToEaiY9           = false;
        // 2021.01.06 전사물류개선 자동화크레인 유인무인 모드추가
        String szYD_EQP_WRK_MODE2 = "";
        YdPlateCommDAO  commDao       = new YdPlateCommDAO();

        String szRtnMsg                 = null;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= msgRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "후판제품크레인작업지시(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////


        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null || szRcvTcCode.equals("")){
            szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return YdConstant.RETN_CD_TC_ERROR;
        }

        try{
        	
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// try 내에 하지 않으면 Unhandled exception type JDTOException 발생        	
        	msgRecord.setField("LOG_ID", logId);                                         
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        	szMsg="["+szOperationName+"] ------------------------ 메소드 시작 ------------------------";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            //=============================================================
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판제품야드] 크레인작업지시 수신";
            ydUtils.putLogMsg("K", YdConstant.YD_MONITORING_CHANNEL_K, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            szWrkProgStat   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");

            //입고작업요구일 경우 처리
            if("X".equals(szWrkProgStat)){
                /* 조정스케쥴 기능으로 인해 차상국입고요구 기능은 막음
                this.procCrnSpcWrkReq(msgRecord);

                return YdConstant.RETN_CD_SUCCESS;
                */
            }

            szYdGp  = YdConstant.YD_GP_PLATE2_GDS_YARD; //2후판제품창고 'T'
            szEqpId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            // 2021. 1. 6 추가(Y9시스템 전송여부)
            isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szEqpId);
            //-------------------------------------------------------------------------------------------------------------
            //스케쥴 생성후 호출 .따라서 아래 로직 막음
            //크레인 입고작업 스케쥴코드 재편
            //2020.12.07 윤재광 막음(입고가적베드 운영으로 통합 -> 입고작업 스케쥴코드 재편기능은 추후 다시 검토)
            //-------------------------------------------------------------------------------------------------------------
            //this.Y4CrnLWorkReScheduleNewMain(msgRecord);
            //-------------------------------------------------------------------------------------------------------------
            //스케쥴 생성후 호출 .따라서 아래 로직 막음
            //크레인 입고작업외 기송신된 작업지시가 5분이상 선택상태로 경과시 스케쥴작업 취소처리
            //2020.12.07 윤재광
            //-------------------------------------------------------------------------------------------------------------
            //this.Y4CrnLWorkReScheduleNewSub(msgRecord);
            //------------------------------------------------------------------------------------------
            //  설비상태 확인
            //------------------------------------------------------------------------------------------
            // 야드설비상태 Check       수신받은  야드설비Id로 설비Table를 조회하여 야드설비상태를 Check하고 고장이면 return
            szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 시작";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            //--------------------------------------------------------------------------start---
            //레코드 생성
            recPara = JDTORecordFactory.getInstance().create();
            //설비ID를 작업크레인으로 설정
            recPara.setField("YD_EQP_ID", szEqpId);

            //설비 체크 및 데이터 조회
            szRtnMsg        = DaoManager.getYdEqp(recPara, rsResult, 0);

            if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
                if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
                    return YdConstant.YD_EQP_NOTEXIST;
                }
            }

            //레코드 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //설비상태
            szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
            //야드설비작업Mode
            szYD_EQP_WRK_MODE = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_MODE");
            szYD_EQP_WRK_MODE2 = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_MODE2");

            szMsg = "설비ID(" + szEqpId + ")의 (" + szYD_EQP_STAT + ") 입니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            if (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {

                szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT + ") 입니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

                szRtnMsg = YdConstant.YD_EQP_STAT_BREAK;

                // 전사물류개선 2021. 1.6
                // 자동크레인이면서 권하위치변경 요청일때는 설비상태를 체크하지 말자
                if(isSendToEaiY9){

                    // 자동크레인모드이면서 파라메터로 전달받은 야드작업진행상태가 5일 경우에만 장비상태체크하지 않는다.
                    if(("A".equals(szYD_EQP_WRK_MODE2)) && "5".equals(ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT")) ){
                        szRtnMsg = YdConstant.RETN_CD_SUCCESS;
                    }
                }

            } else if (szYD_EQP_WRK_MODE.equals(YdConstant.YD_EQP_WRK_MODE_OFF_LINE)) {

                szMsg = "설비ID(" + szEqpId + ")의 상태가  OFF LINE(" + szYD_EQP_WRK_MODE + ")상태 입니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

                szRtnMsg = YdConstant.YD_EQP_WRK_MODE_OFF_LINE;
            } else {
                szRtnMsg = YdConstant.RETN_CD_SUCCESS;
            }

            //--------------------------------------------------------------------------end-----

            if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
                szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 시 오류발생 - 메세지 : " + szRtnMsg;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return szRtnMsg;
            }

            szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 완료 - 메세지 : " + szRtnMsg;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            //------------------------------------------------------------------------------------------


            //------------------------------------------------------------------------------------------
            // 스케줄 기준 체크
            //------------------------------------------------------------------------------------------
            szYD_SCH_CD =  ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD").trim();
            if( !szYD_SCH_CD.equals("") ) {

                szMsg="["+szOperationName+"] 스케줄기준[" + szYD_SCH_CD + "] 조회 시작 ";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                blnRtnVal = YdCommonUtils.chkGetSchRule(szYD_SCH_CD, rsResult);

                szMsg="["+szOperationName+"] 스케줄기준[" + szYD_SCH_CD + "] 조회 완료 - 메세지 : " + blnRtnVal;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                if( !blnRtnVal ) return YdConstant.RETN_CD_FAILURE;
                // 레코드 추출
                rsResult.first();
                recPara = rsResult.getRecord();
                // 스케줄 금지 유무
                String szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,"YD_SCH_PROH_EXN");

                // 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
                if (szYD_SCH_PROH_EXN.equals("Y")) {
                    szMsg = "["+szOperationName+"] 크레인 작업지시 시 스케쥴코드(" + szYD_SCH_CD + ")에 대한 스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                	ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    return YdConstant.RETN_CRN_SCH_PROH;
                }
            }

            //------------------------------------------------------------------------------------------

            //------------------------------------------------------------------------------------------
            //  파라미터로 넘겨진 야드 작업 진행상태별 로직 분기
            //------------------------------------------------------------------------------------------
            szWrkProgStat = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");

            if("W".equals(szWrkProgStat)) {
                //YD_WRK_PROG_STAT 가 'W'로 수신되더라도 크래인 스케줄에 '1','2','3' 작업이 있다면 YD_WRK_PROG_STAT 를 '1','2','3' 으로 수정한다.
                JDTORecordSet rsCrnSch2 = JDTORecordFactory.getInstance().createRecordSet("temp");
                intRtnVal = this.Y4ChkWrkProgStat(msgRecord, rsCrnSch2);

                if(intRtnVal > 0) {
                    if("1".equals(szYD_EQP_STAT)||"2".equals(szYD_EQP_STAT)||"3".equals(szYD_EQP_STAT)||"4".equals(szYD_EQP_STAT)) {
                        szWrkProgStat = szYD_EQP_STAT;
                    }
                }
            }

            /*
             * 2011.05.25 윤재광 현 크레인상태를 넘겨줌.
            //레코드 추출
            rsCrnInfo.first();
            recCrnInfo = rsCrnInfo.getRecord();

            //야드 작업 진행상태를 check한다.
            szWrkProgStat = ydDaoUtils.paraRecChkNull(recCrnInfo, "YD_EQP_STAT");
            */
            //------------------------------------------------------------------------------------------
            //야드 작업 진행상태가 1,3인 경우 (작업지시를 재요구 하는 경우 사용한다.)
            //------------------------------------------------------------------------------------------
            if (szWrkProgStat.equals("1") || szWrkProgStat.equals("2") || szWrkProgStat.equals("3")){

                szMsg="["+szOperationName+"] 야드작업진행상태[" + szWrkProgStat + "]가 1,3인 경우 Y4ChkWrkProgStat 호출";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                intRtnVal = this.Y4ChkWrkProgStat(msgRecord, rsCrnSch);
                //크레인 스케줄이 없다면 없다는 메시지를 L2에 전송해야한다. 현재는 종료처리중..수정할것

                szMsg="["+szOperationName+"] 야드작업진행상태[" + szWrkProgStat + "]가 1,3인 경우 Y4ChkWrkProgStat 호출완료 - 반환값 : "+intRtnVal+" , 대상재건수 : " + rsCrnSch.size();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                if( intRtnVal == 0 ) {
                    //크레인 스케줄이 없다면 없다는 메시지를 L2에 전송해야한다.
                    return YdConstant.RETN_CRN_NO_SCH;
                }else if(intRtnVal == -1) {
                    return YdConstant.RETN_CD_FAILURE;
                }else{

                    //현재크레인이 작업중인 스케줄의 값을 다시 재전송한다.
                    rsCrnSch.absolute(1);
                    recOutTemp = JDTORecordFactory.getInstance().create();
                    recOutTemp.setRecord(rsCrnSch.getRecord());

                    String szYD_CRN_SCH_ID          = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CRN_SCH_ID");
                    String szYD_WRK_PROG_STAT       = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PROG_STAT");

                    recInPara = JDTORecordFactory.getInstance().create();
                    //작업지시 전문 전송 data setup
                    if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) { //--2013.02.07 추가 (3기)
                        recInPara.setField("MSG_ID", "YDY8L004");
                        // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
                        if (isSendToEaiY9){
                            recInPara.setField("MSG_ID"        , "YDY9L004");
                        }
                    } else {
                        recInPara.setField("MSG_ID", "YDY4L004");
                    }
                    recInPara.setField("YD_CRN_SCH_ID",     szYD_CRN_SCH_ID);
                    recInPara.setField("YD_WRK_PROG_STAT",  szYD_WRK_PROG_STAT);
                    recInPara.setField("YD_SCH_CD",         ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"));
                    recInPara.setField("YD_GP",             ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP"));
                    recInPara.setField("MODIFIER",          "YDSYSTEM");

                    //20090925 김진욱 추가 : 작업진행중인 작업을 재전송하는 경우는  MSG_GP 값을 'U' UPDATE로 설정해서 보낸다.
                    recInPara.setField("MSG_GP",            "U");
                    

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// recInPara 에 logId 추가
                    recInPara.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
                    
                    ydDelegate.sendMsg(recInPara);
                    szMsg = "["+szOperationName+"] 현재 작업중["+szYD_WRK_PROG_STAT+"]인 스케줄["+szYD_CRN_SCH_ID+"]을 재전송";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    return YdConstant.RETN_CD_SUCCESS;
                }

            //------------------------------------------------------------------------------------------
            // 야드 작업 진행 상태가 'W'인경우 (작업이 없을때 요구)
            // 2012.07.09 윤재광----------
            // 야드 작업 진행 상태가 'P'인경우
            // : 크레인 파일링 권상상태를 표시한다.
            //   - 'W'로 초기화시 비상입고스케쥴에서 다른 작업지시를 받지 못하도록 하기위해 처리
            //------------------------------------------------------------------------------------------
            }else if(szWrkProgStat.equals("W")||szWrkProgStat.equals("P")){

                szMsg="["+szOperationName+"] 야드작업진행상태[" + szWrkProgStat + "]가 'W'인경우 Y4ChkWrkProgStatW 호출";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                intRtnVal = this.Y4ChkWrkProgStatW(msgRecord, rsCrnSch);

                szMsg="["+szOperationName+"] 야드작업진행상태[" + szWrkProgStat + "]가 'W'인경우 Y4ChkWrkProgStatW 호출 완료 - 반환값 : " + intRtnVal + ", 대상재 개수 : " + rsCrnSch.size();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                if(intRtnVal == 0) {
                    szMsg="크레인 스케줄이 조회되지 않습니다 [intRtnVal = " + intRtnVal + "]";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                    return YdConstant.RETN_CRN_NO_SCH;
                }else if(intRtnVal < 0) {
                    szMsg="크레인스케줄 조회 중 에러 발생  [intRtnVal = " + intRtnVal + "]";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                    return YdConstant.RETN_CD_FAILURE;
                }

            //------------------------------------------------------------------------------------------
            //야드 작업 진행 상태가 '4'인 경우 현재 진행중인 작업이 있을 경우 (현재 진행중인 작업이 있을 경우 해당작업을 호출한다.스케줄 코드로 조회, 조회한 data가 없다면 스케줄우선순위가 빠르고 크레인스케줄id가 가장빠른 작업을 보내준다.)
            //------------------------------------------------------------------------------------------
            }else if(szWrkProgStat.equals("4")) {

                szMsg="["+szOperationName+"] 야드작업진행상태[" + szWrkProgStat + "]가 '4'인 경우 (권하실적처리에서 호출) Y4ChkWrkProgStatW 호출";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                //--------------------------------------------------------------------------------
                //  해당 설비와 관련된 우선순위가 빠른 순으로 크레인스케줄 조회
                //  수정자 : 임춘수
                //  수정일 : 2010.01.27
                //--------------------------------------------------------------------------------
                //intRtnVal = this.Y4ChkWrkProgStat4(msgRecord, rsCrnSch);
                intRtnVal = this.Y4ChkWrkProgStatW(msgRecord, rsCrnSch);
                //--------------------------------------------------------------------------------

                szMsg="["+szOperationName+"] 야드작업진행상태[" + szWrkProgStat + "]가 '4'인 경우 (권하실적처리에서 호출) - Y4ChkWrkProgStatW 호출 완료 - 반환값 : " + intRtnVal + ", 대상재 개수 : " + rsCrnSch.size();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                //더이상 크레인 스케줄을 찾지 못했을 경우 설비상태를 W <--idle로 변경하고 0으로 리턴, 에러인 경우  -1로 리턴.. 크레인 스케줄 호출부분이 아직 없기때문에 종료처리...추후 변경 0일경우는 크레인 스케줄 호출..
                if(intRtnVal <= 0) {
                    szMsg="["+szOperationName+"] 더이상 크레인 스케줄을 찾지못했거나 Error가 발생했을 경우 작업예약을 조회한다. intRtnVal : " + intRtnVal;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    //작업예약조회
                    recInPara = JDTORecordFactory.getInstance().create();
                    recInPara.setField("YD_EQP_ID", szEqpId);
                    rsWrkBook = JDTORecordFactory.getInstance().createRecordSet("");
                    //intRtnVal = ydWrkbookDao.getYdWrkbook(recInPara, rsWrkBook, 4);
                    intRtnVal = commDao.select(recInPara, rsWrkBook, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0111");
                    if(intRtnVal > 0) {
                        //검색된 작업예약을 크레인스케줄을 생성한다. 생성하면 설비상태값에 따라서 작업지시를 내려보내도록되어있다.
                        szMsg="["+szOperationName+"] 현재 크레인(" + szEqpId + ")로 작업할 수 있는 작업예약을 조회하였습니다.  크레인스케줄Main호출";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                        //크레인 스케줄 호출 (설비id,스케줄코드);
                        rsWrkBook.absolute(1);
                        recOutTemp = JDTORecordFactory.getInstance().create();
                        recOutTemp.setRecord(rsWrkBook.getRecord());

                        recInPara = JDTORecordFactory.getInstance().create();

                        recInPara.setField("MSG_ID", "YDYDJ506");
                        recInPara.setField("YD_EQP_ID", szEqpId);
                        recInPara.setField("YD_SCH_CD", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"));
                        recInPara.setField("YD_WBOOK_ID_YJK", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID")); //CHITO 2011.03.30 추가

                        //크레인 스케줄 호출 메세지 전송
                        ydDelegate.sendMsg(recInPara);
                        //다음 크레인스케줄이 생성되는 중이기때문에 현재상태에서는 작업지시를 내려보내지않고 리턴
                        return YdConstant.RETN_CD_SUCCESS;
                    }

                    szMsg="["+szOperationName+"] 현재 크레인(" + szEqpId + ")로 작업할 수 있는 작업예약이 없습니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
//                  //자동작업호출
//                  recInPara = JDTORecordFactory.getInstance().create();
//                  recInPara.setField("MSG_ID",    "YDYDJ284");
//                  recInPara.setField("YD_EQP_ID", szEqpId);
//
//                  //제품창고 동내동간이적 LOT편성 호출
//                  ydDelegate.sendMsg(recInPara);
//                  //다음 크레인스케줄이 생성되는 중이기때문에 현재상태에서는 작업지시를 내려보내지않고 리턴
                    return YdConstant.RETN_CRN_NO_WRK;



                }else{
                    //다음크레인 스케줄을 찾았을경우 작업지시상태로 변경
                    szMsg="["+szOperationName+"] 다음 크레인 스케줄이 존재하므로 크레인설비["+szEqpId+"]의 설비상태를 1로 수정 시작";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    recCrnSch = JDTORecordFactory.getInstance().create();
                    recCrnSch.setRecord(rsCrnSch.getRecord(0));

                    String sSchYdWrkProgStat    = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WRK_PROG_STAT");
                    String sCrnYdWrkProgStat    = "";

                    if( sSchYdWrkProgStat.equals("") || sSchYdWrkProgStat.equals("W") || sSchYdWrkProgStat.equals("1") ) {
                        sCrnYdWrkProgStat   = "1";
                    }else{
                        sCrnYdWrkProgStat   = sSchYdWrkProgStat;
                    }
                    szMsg=" 윤재광 윤재광 : data ==========================="+sCrnYdWrkProgStat;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.WARNING, logId);
                    recIntTemp = JDTORecordFactory.getInstance().create();
                    recIntTemp.setField("YD_EQP_ID", szEqpId);
                    recIntTemp.setField("YD_EQP_STAT", sCrnYdWrkProgStat);
                    intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
                    if(intRtnVal <= 0) {
                        if(intRtnVal == 0) {
                            szMsg=" Y4ChkWrkProgStatW updYdEqp : data not found";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.WARNING, logId);
                        }else if(intRtnVal == -1) {
                            szMsg=" Y4ChkWrkProgStatW updYdEqp : duplicate data,";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                        }else if(intRtnVal == -2) {
                            szMsg=" Y4ChkWrkProgStatW updYdEqp : parameter error";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                        }else if(intRtnVal == -3){
                            szMsg=" Y4ChkWrkProgStatW updYdEqp : execution failed";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                        }
                    }

                    szMsg="["+szOperationName+"] 다음 크레인 스케줄이 존재하므로 크레인설비["+szEqpId+"]의 설비상태를 1로 수정 완료 - 반환값 : " + intRtnVal;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                }
            }
            // 전사물류개선 5 : 권하위치변경요청일 경우
            else if("5".equals(szWrkProgStat)){
                szMsg="["+szOperationName+"] 야드작업진행상태[" + szWrkProgStat + "]가 1,3인 경우 Y4ChkWrkProgStat 호출";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
                intRtnVal = ydCrnSchDao.getYdCrnsch(msgRecord, rsCrnSch, 16);

                //크레인 스케줄이 없다면 없다는 메시지를 L2에 전송해야한다. 현재는 종료처리중..수정할것

                szMsg="["+szOperationName+"] 야드작업진행상태[" + szWrkProgStat + "]가 1,3인 경우 Y4ChkWrkProgStat 호출완료 - 반환값 : "+intRtnVal+" , 대상재건수 : " + rsCrnSch.size();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                if( intRtnVal == 0 ) {
                    //크레인 스케줄이 없다면 없다는 메시지를 L2에 전송해야한다.
                    return YdConstant.RETN_CRN_NO_SCH;
                }else if(intRtnVal == -1) {
                    return YdConstant.RETN_CD_FAILURE;
                }else{

                    //현재크레인이 작업중인 스케줄의 값을 다시 재전송한다.
                    rsCrnSch.absolute(1);
                    recOutTemp = JDTORecordFactory.getInstance().create();
                    recOutTemp.setRecord(rsCrnSch.getRecord());

                    String szYD_CRN_SCH_ID          = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CRN_SCH_ID");
                    String szYD_WRK_PROG_STAT       = szWrkProgStat;

                    recInPara = JDTORecordFactory.getInstance().create();
                    //작업지시 전문 전송 data setup
                    if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) { //--2013.02.07 추가 (3기)
                        recInPara.setField("MSG_ID", "YDY8L004");
                        // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
                        if (isSendToEaiY9){
                            recInPara.setField("MSG_ID"        , "YDY9L004");
                        }
                    } else {
                        recInPara.setField("MSG_ID", "YDY4L004");
                    }
                    recInPara.setField("YD_CRN_SCH_ID",     szYD_CRN_SCH_ID);
                    recInPara.setField("YD_WRK_PROG_STAT",  szYD_WRK_PROG_STAT);
                    recInPara.setField("YD_SCH_CD",         ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"));
                    recInPara.setField("YD_GP",             ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP"));
                    recInPara.setField("MODIFIER",          "YDSYSTEM");

                    //20090925 김진욱 추가 : 작업진행중인 작업을 재전송하는 경우는  MSG_GP 값을 'U' UPDATE로 설정해서 보낸다.
                    recInPara.setField("MSG_GP",            "U");

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// recInPara 에 logId 추가
                    recInPara.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
                    
                    ydDelegate.sendMsg(recInPara);
                    szMsg = "["+szOperationName+"] 현재 작업중["+szYD_WRK_PROG_STAT+"]인 스케줄["+szYD_CRN_SCH_ID+"]을 재전송";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    return YdConstant.RETN_CD_SUCCESS;
                }
            }

            szMsg="["+szOperationName+"] 조회된 크레인스케줄 건수 " + rsCrnSch.size();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            //L2로 전송 ( 전송을 하진 않고 Consol창에 보여준다. )
            for(int Loop_i = 1; Loop_i <= rsCrnSch.size(); Loop_i++) {
                rsCrnSch.absolute(Loop_i);
                recCrnSch = JDTORecordFactory.getInstance().create();
                recCrnSch.setRecord(rsCrnSch.getRecord());
//              System.out.print("YD_CRN_SCH_ID : " + recCrnSch.getFieldString("YD_CRN_SCH_ID") + ", ") ;
//              System.out.print("YD_WBOOK_ID : "   + recCrnSch.getFieldString("YD_WBOOK_ID") + ", ") ;
//              System.out.print("YD_EQP_ID : "     + recCrnSch.getFieldString("YD_EQP_ID") + ", ") ;
//              System.out.print("YD_SCH_CD : "     + recCrnSch.getFieldString("YD_SCH_CD") + ", ") ;
//              System.out.println("YD_SCH_PRIOR : "  + recCrnSch.getFieldString("YD_SCH_PRIOR") + ", ") ;
                szMsg="["+szOperationName+"] ["+Loop_i+"] 크레인설비["+ydDaoUtils.paraRecChkNull(recCrnSch, "YD_EQP_ID")+"]의 크레인스케줄["+ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID")+"] , 작업예약["+ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WBOOK_ID")+"]";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            }

            szMsg="["+szOperationName+"] 로그 보기 완료 - 빠른 크레인스케줄을 크레인작업지시로 보냄";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            recCrnSch = JDTORecordFactory.getInstance().create();
            recCrnSch.setRecord(rsCrnSch.getRecord(0));

            String szYD_WRK_PROG_STAT   = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WRK_PROG_STAT");

            String szYD_CRN_SCH_ID      = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID");

            recInPara = JDTORecordFactory.getInstance().create();
            //작업지시 전문 전송 data setup
            if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) { //--2013.02.07 추가 (3기)
                recInPara.setField("MSG_ID", "YDY8L004");
                // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
                if (isSendToEaiY9){
                    recInPara.setField("MSG_ID"        , "YDY9L004");
                }
            } else {
                recInPara.setField("MSG_ID", "YDY4L004");
            }
            recInPara.setField("YD_CRN_SCH_ID"      , szYD_CRN_SCH_ID);

            szMsg="["+szOperationName+"] 크레인 스케줄["+szYD_CRN_SCH_ID+"]의 szYD_WRK_PROG_STAT["+szYD_WRK_PROG_STAT+"]을 판단해서 크레인 스케줄의 야드작업진행상태를 1로 변경 처리";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            if( szYD_WRK_PROG_STAT.equals("") || szYD_WRK_PROG_STAT.equals("W") || szYD_WRK_PROG_STAT.equals("1") ) {

                recInPara.setField("YD_WRK_PROG_STAT", "1");

                // 2021. 1.6 전사물류개선 프로젝트
                // Y9YDL015 전문을 받아 상태값을 변경을 위한 추가로직
                // YD_L2_REQUEST_STAT 값에 'S'를 셋팅후
                // Y9YDL015전문을 수신후 결과에 따라 초기화한다.
                if(isSendToEaiY9){
                    if(!"1".equals(szYD_WRK_PROG_STAT)){

//                       리모컨 or 자동화
//                      if( "A".equals(szYD_EQP_WRK_MODE2) || "R".equals(szYD_EQP_WRK_MODE2) ) {
                        JDTORecord param = JDTORecordFactory.getInstance().create();
                        param = JDTORecordFactory.getInstance().create();
                        param.setField("YD_DN_WO_LOC_TO"    , "" );
                        param.setField("STL_NO_TEMP"        , "");
                        param.setField("STK_LYR_NO_TEMP"    , "");
                        param.setField("YD_L2_REQUEST_STAT" , "1"); // 지시에 대한 응답대기상태를 저장처리하며, Y9YDL015전문을 수신후 결과에 따라 초기화한다.
                        param.setField("YD_CRN_SCH_ID"      , szYD_CRN_SCH_ID);

                        intRtnVal = commDao.update(param,"com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.upYdCrnSchLocStat1");
//                      }

                        // 응답을 받기 전까지는 대기상태를 유지한다.
                        recInPara.setField("YD_WRK_PROG_STAT", "W");
                    }
                }

            }else{
                recInPara.setField("YD_WRK_PROG_STAT", szYD_WRK_PROG_STAT);
            }

            recInPara.setField("YD_WORD_DT"         , YdUtils.getCurDate("yyyyMMddHHmmss"));
            recInPara.setField("MODIFIER"           , "YDSYSTEM");

            //야드 작업 진행상태가 1,3인 경우 (작업지시를 재요구 하는 경우 사용한다.)
            if (szWrkProgStat.equals("1") || szWrkProgStat.equals("2") || szWrkProgStat.equals("3")){
                //20090925 김진욱 추가 : 작업진행중인 작업을 재전송하는 경우는  MSG_GP 값을 'U' UPDATE로 설정해서 보낸다.
                recInPara.setField("MSG_GP", "U");
            }

            szMsg="["+szOperationName+"] 크레인 스케줄["+szYD_CRN_SCH_ID+"]의 야드작업진행상태를 수정 시작";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            //크레인스케줄의 작업진행 상태를 권상지시로 변경
//// 확인
//
//          intRtnVal = YdCrnSchDao.updYdCrnsch(recInPara, 0);
            /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnWrkProgStat*/
            intRtnVal = YdCrnSchDao.updYdCrnschDelay(recInPara, 302);
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="procY4CrnWrkOrdReq updYdCrnsch : data not found";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.WARNING, logId);
                    return YdConstant.RETN_CD_NOTEXIST;
                }else if(intRtnVal == -2) {
                    szMsg="procY4CrnWrkOrdReq updYdCrnsch : parameter error";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    return YdConstant.RETN_CD_NO_PARAM;
                }else{
                    szMsg="procY4CrnWrkOrdReq updYdCrnsch : execution failed";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    return YdConstant.RETN_CD_FAILURE;
                }
            }

            szMsg="["+szOperationName+"] 크레인 스케줄["+szYD_CRN_SCH_ID+"]의 야드작업진행상태를 수정 완료 - 반환값 : " + intRtnVal;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);



             // Flex(실시간작업을위함)
            JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
            if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) { //--2013.02.07 추가 (3기)
                recFlex.setField("YD_GP",  YdConstant.YD_GP_PLATE2_GDS_YARD);
            } else {
                recFlex.setField("YD_GP",  YdConstant.YD_GP_PLATE_GDS_YARD);
            }
            recFlex.setField("YD_EQP_ID", szEqpId);
            ydUtils.putYdFlexCrnWrk("", recFlex);



            recInPara.setField("YD_SCH_CD", ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD"));
            recInPara.setField("YD_GP", ydDaoUtils.paraRecChkNull(recCrnSch, "YD_GP"));

            szMsg="["+szOperationName+"] 크레인 스케줄["+szYD_CRN_SCH_ID+"]의 크레인작업지시[YDY4L004] 전송 시작";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// recInPara 에 logId 추가
            recInPara.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

            //작업지시 메세지 전송
            ydDelegate.sendMsg(recInPara);

            szMsg="["+szOperationName+"] 크레인 스케줄["+szYD_CRN_SCH_ID+"]의 크레인작업지시[YDY4L004] 전송 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
            szMsg = "후판제품크레인작업지시(" + szMethodName + ") 완료";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

            
        }catch(Exception e){
            szMsg="Error:" +e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            m_ctx.setRollbackOnly();
            throw new DAOException(szMsg);
        }

        szMsg="["+szOperationName+"] ------------------------ 메소드 끝 ------------------------";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
szMsg = "후판제품크레인작업지시(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        
        return YdConstant.RETN_CD_SUCCESS;

    } //end of procY4CrnWrkOrdReq()

    /**
     * 후판제품창고 입고작업 요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return Boolean
     * @throws DAOException
     */
    public void  procCrnSpcWrkReq(JDTORecord recMsg) throws DAOException {

        JDTORecord recPara          = null;
        JDTORecord recTemp          = null;
        JDTORecord recTemp2         = null;
        JDTORecord recEqpInfo       = null;

        JDTORecordSet rsDataSch     = null;
        JDTORecordSet rsEqpInfo     = null;

        YdCrnSchDao  ydCrnSchDao    = new YdCrnSchDao ();
        YdWrkbookDao ydWrkbookDao   = new YdWrkbookDao();
        YdEqpDao ydEqpDao           = new YdEqpDao();
        YdPlateCommDAO commDao      = new YdPlateCommDAO();

        String szEqpId              = null;
        String szWbookId            = null;
        String szYdCrnSchId         = null;

        int intGp           = 0;
        int intCrnPrior     = 0;

        String szMethodName     = "procCrnSpcWrkReq";
        String szOperationName  = "후판제품창고 입고작업 요구 ";
        String szLogMsg         = "";
        // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
        boolean isSendToEaiY9           = false;

        try{

            szLogMsg = "JSP-SESSION [" + szOperationName + " ]시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            //설비 ID
            szEqpId = recMsg.getFieldString("YD_EQP_ID");
            // 2021. 1. 6 추가(Y9시스템 전송여부)
            isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szEqpId);

            /*
             * 1 크레인 정보 가져오기
             */
            rsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
            recEqpInfo = JDTORecordFactory.getInstance().create();
            recEqpInfo.setField("YD_EQP_ID", szEqpId);

            //해당 설비  szChgCrn 로 설비 정보 조회
            intGp = ydEqpDao.getYdEqp(recEqpInfo, rsEqpInfo, 0);

            if(intGp > 0 ){

                rsEqpInfo.first();
                recEqpInfo = rsEqpInfo.getRecord();

                if(ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_BREAK)){
                    // 설비 상태가 고장일 경우
                    szLogMsg = "변경 설비["+ szEqpId+"]가 고장 상태여서 상태를 변경 할 수 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
                    return  ;
                }

                if(ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_WRK_MODE").equals(YdConstant.YD_EQP_WRK_MODE_OFF_LINE)){
                    // 설비 상태가 OFF_LINE 일 경우
                    szLogMsg = "변경 설비["+ szEqpId+"]가 OFF_LINE 이기때문에 상태를 변경 할 수 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
                    return  ;
                }

                if( ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_IDLE)){
                }else{
                    szLogMsg = "크레인 설비["+ szEqpId+"]가 작업지시기 내려가 있기때문에 상태를 변경 할 수 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
                    return  ;
                }

            }else{
                //해당 설비가 존재 하지 않습니다.
                szLogMsg = "해당 설비["+ szEqpId+"]가 존재 하지 않습니다";
                ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
                return ;
            }

            /*
             * 2. 해당설비에 해당하는 스케쥴 정보를 가져온다.
             */
            rsDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");
            recTemp = JDTORecordFactory.getInstance().create();
            recPara = JDTORecordFactory.getInstance().create();

            recPara.setField("YD_EQP_ID", szEqpId);

            intGp = commDao.select(recPara, rsDataSch, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0122");

            if(intGp < 1 ){
                // 스케줄 정보가 존재 하지 않을 경우
                szLogMsg = "해당크레인에 할당할  [ " +szEqpId  + "] 작업정보가 존재 하지 않습니다.";

                recPara = JDTORecordFactory.getInstance().create();
                // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
                if (isSendToEaiY9){
                    recPara.setField("MSG_ID"        , "YDY9L005");
                }
                else
                {
                    recPara.setField("MSG_ID"        , "YDY8L005");
                }
                recPara.setField("YD_EQP_ID"     , szEqpId);                        //야드설비ID
                recPara.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_WO_DMD);   //야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
                recPara.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK);//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.

                ydDelegate.sendMsg(recPara);

                return ;
            }

            rsDataSch.first();
            recTemp = rsDataSch.getRecord();

            szYdCrnSchId = ydDaoUtils.paraRecChkNull(recTemp, "YD_CRN_SCH_ID");
            szWbookId    = ydDaoUtils.paraRecChkNull(recTemp, "YD_WBOOK_ID");

            /*
             * 3. 선택된 크레인 스케줄 작업예약 ID에  대체 설비 ID 와 우선순위 를 UPDATE 한다.
             */
            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("YD_WBOOK_ID",szWbookId);
            recPara.setField("YD_SCH_PRIOR", new Integer("0")); // 우선순위는 무조건 0순위로 셋팅
            recPara.setField("MODIFIER","CRANE");
            intGp = ydWrkbookDao.updYdWrkbook(recPara, 0);
            if (intGp <1){
                throw new DAOException("선택된 크레인 스케줄 작업예약 ID : "+szWbookId+"에   우선순위 ("+intCrnPrior+") 를 UPDATE 중 ERROR");
            }

            /*
             * 4. 작업예약 ID 로 현재 편성된 스케줄 ID [] 를 구한다.
             */
            rsDataSch = JDTORecordFactory.getInstance().createRecordSet("YD");
            recPara   = JDTORecordFactory.getInstance().create();

            recPara.setField("YD_WBOOK_ID",szWbookId);
            recPara.setField("YD_WRK_PROG_STAT","W");

            // 기존쿼리는 W 이상태만 체크하였으나 지금은 1,W 상태를 조회한다.
            intGp = ydCrnSchDao.getYdCrnsch(recPara, rsDataSch, 23);

            if (intGp <1 ){
                // 해당 작업 ID 에 편성된 스케줄 정보가 없을경우
                throw new DAOException("해당작업 ID 에 편성된 스케줄 정보가 존재하지 않음");
            }

            //크레인 스케줄 정보 변경
            rsDataSch.first();

            do
            {
                /*
                 * 5. 편성된 스케줄 ID [] 에  대체 크레인 설비 ID와 우선순위를 편성
                 */
                recTemp = JDTORecordFactory.getInstance().create();
                recPara = JDTORecordFactory.getInstance().create();

                recTemp = rsDataSch.getRecord();

                recPara.setField("YD_CRN_SCH_ID"    , recTemp.getField("YD_CRN_SCH_ID"));
                recPara.setField("YD_EQP_ID"        , szEqpId);
                recPara.setField("YD_SCH_PRIOR"     , new Integer("0"));
                recPara.setField("MODIFIER"         , "CRANEs");

                // 5. 스케줄 테이블에 UPDATE
                intGp = ydCrnSchDao.updYdCrnsch(recPara, 0);
                if (intGp <1){
                    throw new DAOException("스케줄 테이블에 UPDATE 중 ERROR");
                }

                //----------------------------------------------------------------------------------
                //  크레인 허용 오차 및 크레인 X, Y좌표 계산
                //----------------------------------------------------------------------------------
                recTemp2 = JDTORecordFactory.getInstance().create();
                recTemp2.setField("YD_CRN_SCH_ID", recTemp.getField("YD_CRN_SCH_ID"));
                YdUtils.updYdCrnschBedData(recTemp2);
                //----------------------------------------------------------------------------------

            }while(rsDataSch.next());

            /*
             * 6. 작업지시 정보를 호출하여준다.
             */

            szLogMsg = "[JSP Session] "+szOperationName +": 크레인 작업지시   : 야드구분[T]";  // 야드구분은 스케줄 코드앞자리에서 발생되었다.
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

            //JMS => EJB CALL 형식으로 수정요청
            recPara   = JDTORecordFactory.getInstance().create();

            recPara.setField("MSG_ID"           , "Y4YDL007"        );
            recPara.setField("YD_EQP_ID"        , szEqpId            );
            recPara.setField("YD_WRK_PROG_STAT" , YdConstant.YD_EQP_STAT_IDLE );

            this.procY4CrnWrkOrdReq(recPara);

        }catch(DAOException e){
            ydUtils.putLog("CraneLdHdSeEJB", "procCrnSpcWrkReq", e.getMessage(), YdConstant.ERROR);
            throw e;

        }catch(Exception e){
            throw new DAOException(getClass().getName() + e.getMessage(),e);
        }

        szLogMsg = "JSP-SESSION [후판제품창고 입고작업 요구  ]끝";
        ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

        return ;
    }

    /**
     * 오퍼레이션명 : 크레인 입고작업 스케쥴코드 재편
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public void Y4CrnLWorkReSchedule(JDTORecord msgRecord)throws JDTOException{

        //설비 DAO
        YdEqpDao        ydEqpDao        = new YdEqpDao();
        YdCrnSchDao     ydCrnSchDao     = new YdCrnSchDao();
        YdUtils       ydUtils           = new YdUtils();

        JDTORecordSet rsCrnsch          = null;
        JDTORecordSet rsEquip           = null;

        JDTORecord recEquip             = null;
        JDTORecord recCrnSch            = null;

        String szMethodName             = "Y4CrnLWorkReSchedule";
        String szOperationName          = "크레인 입고작업 스케쥴코드 재편";
        String szMsg                    = "";
        String sBayGp                   = "";

        int iYDEqpId    = 0;    // 설비가동대수
        int iEqpCnt     = 0;    // 설비설치대수
        int iSchCnt     = 0;    // 입고작업갯수

        String sYdEqpStat1 = "";
        String sYdEqpStat2 = "";
        String sYdEqpStat3 = "";
        String sMEqpId     = "";
        String szYdGp      = "";

        try{

            sMEqpId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");

            if(sMEqpId.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD)) { //--2013.02.07 추가 (3기)
                szYdGp  = YdConstant.YD_GP_PLATE2_GDS_YARD; //2후판제품창고 'T'
            } else {
                szYdGp  = YdConstant.YD_GP_PLATE_GDS_YARD;  //1후판제품창고 'K'
            }

            sBayGp = sMEqpId.substring(1, 2);

            rsCrnsch    = JDTORecordFactory.getInstance().createRecordSet("");
            rsEquip     = JDTORecordFactory.getInstance().createRecordSet("");

            recEquip    = JDTORecordFactory.getInstance().create();
            recEquip.setField("YD_GP",      szYdGp);
            recEquip.setField("YD_BAY_GP",  sBayGp);
            recEquip.setField("YD_EQP_GP",  "CR");

            iEqpCnt = ydEqpDao.getYdEqp(recEquip,rsEquip,2);
            iSchCnt = ydCrnSchDao.getYdCrnsch(recEquip,rsCrnsch,54);

            String sWorkCaseId  = "";
            String sTmpEqpId    = "";
            String sTmpEqpStat  = "";
            String sTmpDnWoLoc  = "";

            {// 설비상태 정보 가져오기.
                for(int index = 1; index <= rsEquip.size(); index++) {
                    rsEquip.absolute(index);
                    recEquip = JDTORecordFactory.getInstance().create();
                    recEquip.setRecord(rsEquip.getRecord());

                    sTmpEqpId   = ydDaoUtils.paraRecChkNull(recEquip, "YD_EQP_ID");
                    sTmpEqpStat = ydDaoUtils.paraRecChkNull(recEquip, "YD_EQP_STAT");
                    if((szYdGp+sBayGp+"CR"+sBayGp+"1").equals(sTmpEqpId)){
                        sYdEqpStat1 = sTmpEqpStat;
                    }else if((szYdGp+sBayGp+"CR"+sBayGp+"2").equals(sTmpEqpId)){
                        sYdEqpStat2 = sTmpEqpStat;
                    }else if((szYdGp+sBayGp+"CR"+sBayGp+"3").equals(sTmpEqpId)){
                        sYdEqpStat3 = sTmpEqpStat;
                    }

                    if("B".equals(sTmpEqpStat)){
                    }else{
                        iYDEqpId++;
                    }
                }
            }

            szMsg = "크레인 입고작업 스케쥴코드 재편 INPUT값 sYdEqpStat1["+sYdEqpStat1+"]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "크레인 입고작업 스케쥴코드 재편 INPUT값 sYdEqpStat2["+sYdEqpStat2+"]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "크레인 입고작업 스케쥴코드 재편 INPUT값 sYdEqpStat3["+sYdEqpStat3+"]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "크레인 입고작업 스케쥴코드 재편 INPUT값 iYDEqpId["+iYDEqpId+"]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "크레인 입고작업 스케쥴코드 재편 INPUT값 iSchCnt["+iSchCnt+"]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //if("B".equals(sBayGp)||"D".equals(sBayGp)||"E".equals(sBayGp)){
            if("D".equals(sBayGp)||"E".equals(sBayGp)){ //--2013.04.08 B동 크래인 3개 --> 2개로 변경

                // 가동대수가 2일때.
                if(iYDEqpId == 2){
                    // 입고대기건수가 없을경우
                    if(iSchCnt == 1){
                        // 1호기 고장상태
                        if("B".equals(sYdEqpStat1)){
                            if(isSchCdInfo(rsCrnsch,"L")){
                                if("1".equals(sYdEqpStat2)||
                                   "2".equals(sYdEqpStat2)){

                                    if("4".equals(sYdEqpStat3)||
                                       "W".equals(sYdEqpStat3)){

                                        if(isSchLocCheck(rsCrnsch,"B",1)){
                                            if(this.getCrnDmCntInfo(szYdGp+sBayGp+"CR"+sBayGp+"3") == 0){
                                                // Case5 셋팅.
                                                sWorkCaseId = "5";
                                            }
                                        }// if(isSchLocCheck(rsCrnsch,"B",1)){
                                    }// if("4".equals(sYdEqpStat3)||
                                }// if("1".equals(sYdEqpStat2)||
                            }else{
                                if("1".equals(sYdEqpStat3)||
                                   "2".equals(sYdEqpStat3)){

                                    if("4".equals(sYdEqpStat2)||
                                       "W".equals(sYdEqpStat2)){
                                        if(this.getCrnDmCntInfo(szYdGp+sBayGp+"CR"+sBayGp+"2") == 0){
                                            // Case6 셋팅.
                                            sWorkCaseId = "6";
                                        }
                                    }// if("4".equals(sYdEqpStat2)||
                                }// if("1".equals(sYdEqpStat3)||
                            }
                        // 3호기 고장상태
                        }else if("B".equals(sYdEqpStat3)){
                            if(isSchCdInfo(rsCrnsch,"L")){
                                if("1".equals(sYdEqpStat1)||
                                   "2".equals(sYdEqpStat1)){

                                    if("4".equals(sYdEqpStat2)||
                                       "W".equals(sYdEqpStat2)){

                                        if(this.getCrnDmCntInfo(szYdGp+sBayGp+"CR"+sBayGp+"2") == 0){
                                            // Case7 셋팅.
                                            sWorkCaseId = "7";
                                        }
                                    }// if("4".equals(sYdEqpStat2)||
                                }// if("1".equals(sYdEqpStat1)||
                            }else{
                                if("1".equals(sYdEqpStat2)||
                                   "2".equals(sYdEqpStat2)){

                                    if("4".equals(sYdEqpStat1)||
                                       "W".equals(sYdEqpStat1)){

                                        if(isSchLocCheck(rsCrnsch,"S",4)){
                                            if(this.getCrnDmCntInfo(szYdGp+sBayGp+"CR"+sBayGp+"1") == 0){
                                                // Case8 셋팅.
                                                sWorkCaseId = "8";
                                            }
                                        }// if(isSchLocCheck(rsCrnsch,"S",4)){
                                    }// if("4".equals(sYdEqpStat1)||
                                }// if("1".equals(sYdEqpStat2)||
                            }
                        }else{
                            if(isSchCdInfo(rsCrnsch,"L")){
                                if("1".equals(sYdEqpStat1)||
                                   "2".equals(sYdEqpStat1)){

                                    if("4".equals(sYdEqpStat3)||
                                       "W".equals(sYdEqpStat3)){

                                        if(isSchLocCheck(rsCrnsch,"B",1)){
                                            if(this.getCrnDmCntInfo(szYdGp+sBayGp+"CR"+sBayGp+"3") == 0){
                                                // Case9 셋팅.
                                                sWorkCaseId = "9";
                                            }
                                        }// if(isSchLocCheck(rsCrnsch,"B",1)){
                                    }// if("4".equals(sYdEqpStat3)||
                                }// if("1".equals(sYdEqpStat1)||
                            }else{
                                if("1".equals(sYdEqpStat3)||
                                   "2".equals(sYdEqpStat3)){

                                    if("4".equals(sYdEqpStat1)||
                                       "W".equals(sYdEqpStat1)){

                                        if(isSchLocCheck(rsCrnsch,"S",4)){
                                            if(this.getCrnDmCntInfo(szYdGp+sBayGp+"CR"+sBayGp+"1") == 0){
                                                // Case10 셋팅.
                                                sWorkCaseId = "10";
                                            }
                                        }// if(isSchLocCheck(rsCrnsch,"S",4)){
                                    }// if("4".equals(sYdEqpStat1)||
                                }// if("1".equals(sYdEqpStat3)||
                            }
                        }// if("B".equals(sYdEqpStat1)){
                    }else if(iSchCnt >= 2){
                        // 1호기 고장상태
                        if("B".equals(sYdEqpStat1)){
                            if(isSchCdInfo(rsCrnsch,"L")){
                                if("1".equals(sYdEqpStat2)||
                                   "2".equals(sYdEqpStat2)||
                                   "3".equals(sYdEqpStat2)||
                                   "4".equals(sYdEqpStat2)){

                                    if("3".equals(sYdEqpStat3)||
                                       "4".equals(sYdEqpStat3)||
                                       "W".equals(sYdEqpStat3)){

                                        // Case11 셋팅.
                                        sWorkCaseId = "11";

                                    }// if("3".equals(sYdEqpStat3)||
                                }// if("1".equals(sYdEqpStat2)||
                            }else if(isSchCdInfo(rsCrnsch,"R")){
                                if("1".equals(sYdEqpStat3)||
                                   "2".equals(sYdEqpStat3)||
                                   "3".equals(sYdEqpStat3)||
                                   "4".equals(sYdEqpStat3)){

                                    if("3".equals(sYdEqpStat2)||
                                       "4".equals(sYdEqpStat2)||
                                       "W".equals(sYdEqpStat2)){

                                        // Case12 셋팅.
                                        sWorkCaseId = "12";

                                    }// if("3".equals(sYdEqpStat2)||
                                }// if("1".equals(sYdEqpStat3)||
                            }// if(isSchInfo(rsCrnsch,"L")){
                        // 3호기 고장상태
                        }else if("B".equals(sYdEqpStat3)){
                            if(isSchCdInfo(rsCrnsch,"L")){
                                if("1".equals(sYdEqpStat1)||
                                   "2".equals(sYdEqpStat1)||
                                   "3".equals(sYdEqpStat1)||
                                   "4".equals(sYdEqpStat1)){

                                    if("3".equals(sYdEqpStat2)||
                                       "4".equals(sYdEqpStat2)||
                                       "W".equals(sYdEqpStat2)){

                                        // Case13 셋팅.
                                        sWorkCaseId = "13";

                                    }// if("3".equals(sYdEqpStat2)||
                                }// if("1".equals(sYdEqpStat1)||
                            }else if(isSchCdInfo(rsCrnsch,"R")){
                                if("1".equals(sYdEqpStat2)||
                                   "2".equals(sYdEqpStat2)||
                                   "3".equals(sYdEqpStat2)||
                                   "4".equals(sYdEqpStat2)){

                                    if("3".equals(sYdEqpStat1)||
                                       "4".equals(sYdEqpStat1)||
                                       "W".equals(sYdEqpStat1)){

                                        // Case14 셋팅.
                                        sWorkCaseId = "14";

                                    }// if("3".equals(sYdEqpStat1)||
                                }// if("1".equals(sYdEqpStat2)||
                            }// if(isSchInfo(rsCrnsch,"L")){
                        }else{
                            if(isSchCdInfo(rsCrnsch,"L")){
                                if("1".equals(sYdEqpStat1)||
                                   "2".equals(sYdEqpStat1)||
                                   "3".equals(sYdEqpStat1)||
                                   "4".equals(sYdEqpStat1)){

                                    if("3".equals(sYdEqpStat3)||
                                       "4".equals(sYdEqpStat3)||
                                       "W".equals(sYdEqpStat3)){

                                        // Case15 셋팅.
                                        sWorkCaseId = "15";

                                    }// if("3".equals(sYdEqpStat3)||
                                }// if("1".equals(sYdEqpStat1)||
                            }else if(isSchCdInfo(rsCrnsch,"R")){
                                if("1".equals(sYdEqpStat3)||
                                   "2".equals(sYdEqpStat3)||
                                   "3".equals(sYdEqpStat3)||
                                   "4".equals(sYdEqpStat3)){

                                    if("3".equals(sYdEqpStat1)||
                                       "4".equals(sYdEqpStat1)||
                                       "W".equals(sYdEqpStat1)){

                                        // Case16 셋팅.
                                        sWorkCaseId = "16";

                                    }// if("3".equals(sYdEqpStat1)||
                                }// if("1".equals(sYdEqpStat3)||
                            }// if(isSchInfo(rsCrnsch,"L")){
                        }// if("B".equals(sYdEqpStat1)){
                    }// if(iSchCnt == 1){
                }else if(iYDEqpId == 3){

                    sTmpDnWoLoc = this.getCrnDnWoLocInfo(szYdGp+sBayGp+"CR"+sBayGp+"2");

                    if(!"".equals(sTmpDnWoLoc)) {

                        // 입고대기건수가 없을경우
                        if(iSchCnt == 1){
                            if("1".equals(sYdEqpStat2)||
                               "2".equals(sYdEqpStat2)){

                                if(isSchLocInfo(rsCrnsch,"L")){

                                    if(YdConstant.SPAN_ORDER_NEW_02.equals(sTmpDnWoLoc.substring(2, 4))||
                                       YdConstant.SPAN_ORDER_NEW_03.equals(sTmpDnWoLoc.substring(2, 4))||
                                       YdConstant.SPAN_ORDER_NEW_07.equals(sTmpDnWoLoc.substring(2, 4))||
                                       YdConstant.SPAN_ORDER_NEW_TP.equals(sTmpDnWoLoc.substring(2, 4))||
                                       "PTB".equals(sTmpDnWoLoc.substring(2, 5))){

                                       if("4".equals(sYdEqpStat1)||
                                           "W".equals(sYdEqpStat1)){

                                           if(isSchLocCheck(rsCrnsch,"S",4)){
                                               if(this.getCrnDmCntInfo(szYdGp+sBayGp+"CR"+sBayGp+"1") == 0){
                                                    // CASE 17 셋팅.
                                                    sWorkCaseId = "17";
                                               }
                                           }// if(isSchLocCheck(rsCrnsch,"S",4)){
                                        }// if("4".equals(sYdEqpStat1)||
                                    }else{
                                        if(isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"B")){
                                            if("4".equals(sYdEqpStat3)||
                                               "W".equals(sYdEqpStat3)){

                                                if(isSchLocCheck(rsCrnsch,"B",1)){
                                                    if(this.getCrnDmCntInfo(szYdGp+sBayGp+"CR"+sBayGp+"3") == 0){
                                                        // CASE 18 셋팅.
                                                        sWorkCaseId = "18";
                                                    }
                                                }// if(isSchLocCheck(rsCrnsch,"B",1)){
                                            }// if("4".equals(sYdEqpStat3)||
                                        }else{
                                            if("4".equals(sYdEqpStat1)||
                                               "W".equals(sYdEqpStat1)){

                                                if(isSchLocCheck(rsCrnsch,"S",4)){
                                                    if(this.getCrnDmCntInfo(szYdGp+sBayGp+"CR"+sBayGp+"1") == 0){
                                                        // CASE 17 셋팅(NEW).
                                                        sWorkCaseId = "17";
                                                    }
                                                }// if(isSchLocCheck(rsCrnsch,"B",1)){
                                            }// if("4".equals(sYdEqpStat3)||
                                        }//if(isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"B")){
                                    }// if(YdConstant.SPAN_ORDER_NEW_07.equals(sTmpDnWoLoc.substring(2, 4))||

                                }else{

                                    if(YdConstant.SPAN_ORDER_NEW_01.equals(sTmpDnWoLoc.substring(2, 4))||
                                       YdConstant.SPAN_ORDER_NEW_04.equals(sTmpDnWoLoc.substring(2, 4))||
                                       YdConstant.SPAN_ORDER_NEW_05.equals(sTmpDnWoLoc.substring(2, 4))||
                                       YdConstant.SPAN_ORDER_NEW_06.equals(sTmpDnWoLoc.substring(2, 4))||
                                       "PTA".equals(sTmpDnWoLoc.substring(2, 5))){
                                        if("4".equals(sYdEqpStat3)||
                                           "W".equals(sYdEqpStat3)){

                                            if(isSchLocCheck(rsCrnsch,"B",1)){
                                                if(this.getCrnDmCntInfo(szYdGp+sBayGp+"CR"+sBayGp+"1") == 0){
                                                    // CASE 19 셋팅.
                                                    sWorkCaseId = "19";
                                                }
                                            }// if(isSchLocCheck(rsCrnsch,"B",1)){
                                        }// if("4".equals(sYdEqpStat1)||
                                    }else{
                                        if(isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"S")){
                                            if("4".equals(sYdEqpStat1)||
                                               "W".equals(sYdEqpStat1)){

                                                if(isSchLocCheck(rsCrnsch,"S",4)){
                                                    if(this.getCrnDmCntInfo(szYdGp+sBayGp+"CR"+sBayGp+"1") == 0){
                                                        // CASE 20 셋팅.
                                                        sWorkCaseId = "20";
                                                    }
                                                }// if(isSchLocCheck(rsCrnsch,"S",4)){
                                            }// if("4".equals(sYdEqpStat1)||
                                        }else{
                                            if("4".equals(sYdEqpStat3)||
                                               "W".equals(sYdEqpStat3)){

                                                if(isSchLocCheck(rsCrnsch,"B",1)){
                                                    if(this.getCrnDmCntInfo(szYdGp+sBayGp+"CR"+sBayGp+"3") == 0){
                                                        // CASE 19 셋팅(NEW).
                                                        sWorkCaseId = "19";
                                                    }
                                                }// if(isSchLocCheck(rsCrnsch,"S",4)){
                                            }// if("4".equals(sYdEqpStat1)||
                                        }// if(isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"B")){
                                    }// if(YdConstant.SPAN_ORDER_NEW_04.equals(sTmpDnWoLoc.substring(2, 4))||
                                }// if(YdConstant.SPAN_ORDER_NEW_04.equals(sMDnWoLoc.substring(2, 4))||
                            }// if("1".equals(sYdEqpStat2)||
                        }else if(iSchCnt >= 2){

                            if(isSchLocInfo(rsCrnsch,"L")){

                                if(YdConstant.SPAN_ORDER_NEW_02.equals(sTmpDnWoLoc.substring(2, 4))||
                                   YdConstant.SPAN_ORDER_NEW_03.equals(sTmpDnWoLoc.substring(2, 4))||
                                   YdConstant.SPAN_ORDER_NEW_07.equals(sTmpDnWoLoc.substring(2, 4))||
                                   YdConstant.SPAN_ORDER_NEW_TP.equals(sTmpDnWoLoc.substring(2, 4))||
                                   "PTB".equals(sTmpDnWoLoc.substring(2, 5))){
                                    if("1".equals(sYdEqpStat2)||
                                       "2".equals(sYdEqpStat2)||
                                       "3".equals(sYdEqpStat2)||
                                       "4".equals(sYdEqpStat2)){

                                        if("W".equals(sYdEqpStat1)||
                                           "3".equals(sYdEqpStat1)||
                                           "4".equals(sYdEqpStat1)){

                                            // CASE 21 셋팅.
                                            sWorkCaseId = "21";

                                        }// if("W".equals(sYdEqpStat1)||
                                    }// if("1".equals(sYdEqpStat2)||
                                }else{
                                    if(isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"S")){
                                        if("1".equals(sYdEqpStat2)||
                                           "2".equals(sYdEqpStat2)||
                                           "3".equals(sYdEqpStat2)){

                                            if("W".equals(sYdEqpStat3)||
                                               "4".equals(sYdEqpStat3)){

                                                // CASE 22 셋팅.
                                                sWorkCaseId = "22";

                                            }// if("W".equals(sYdEqpStat3)||
                                        }// if("1".equals(sYdEqpStat2)||
                                    }else{
                                        if("2".equals(sYdEqpStat2)||
                                           "3".equals(sYdEqpStat2)||
                                           "4".equals(sYdEqpStat2)){

                                            if("W".equals(sYdEqpStat1)||
                                               "4".equals(sYdEqpStat1)){

                                                // CASE 25 셋팅.
                                                sWorkCaseId = "25";

                                            }// if("W".equals(sYdEqpStat1)||
                                        }// if("2".equals(sYdEqpStat2)||
                                    }// if(isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"S")){
                                }// if(YdConstant.SPAN_ORDER_NEW_07.equals(sTmpDnWoLoc.substring(2, 4))||

                            }else if(isSchLocInfo(rsCrnsch,"R")){

                                if(YdConstant.SPAN_ORDER_NEW_01.equals(sTmpDnWoLoc.substring(2, 4))||
                                   YdConstant.SPAN_ORDER_NEW_04.equals(sTmpDnWoLoc.substring(2, 4))||
                                   YdConstant.SPAN_ORDER_NEW_05.equals(sTmpDnWoLoc.substring(2, 4))||
                                   YdConstant.SPAN_ORDER_NEW_06.equals(sTmpDnWoLoc.substring(2, 4))||
                                   "PTA".equals(sTmpDnWoLoc.substring(2, 5))){
                                    if("1".equals(sYdEqpStat2)||
                                       "2".equals(sYdEqpStat2)||
                                       "3".equals(sYdEqpStat2)||
                                       "4".equals(sYdEqpStat2)){

                                        if("W".equals(sYdEqpStat3)||
                                           "3".equals(sYdEqpStat3)||
                                           "4".equals(sYdEqpStat3)){

                                            // CASE 22 셋팅.
                                            sWorkCaseId = "22";

                                        }// if("W".equals(sYdEqpStat1)||
                                    }// if("1".equals(sYdEqpStat2)||
                                }else{
                                    if(isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"S")){
                                        if("1".equals(sYdEqpStat2)||
                                           "2".equals(sYdEqpStat2)||
                                           "3".equals(sYdEqpStat2)){

                                            if("4".equals(sYdEqpStat1)||
                                               "W".equals(sYdEqpStat1)){

                                                // CASE 23 셋팅.
                                                sWorkCaseId = "23";

                                            }// if("4".equals(sYdEqpStat1)||
                                        }// if("1".equals(sYdEqpStat2)||
                                    }else{
                                        if("2".equals(sYdEqpStat2)||
                                           "3".equals(sYdEqpStat2)||
                                           "4".equals(sYdEqpStat2)){

                                            if("W".equals(sYdEqpStat3)||
                                               "4".equals(sYdEqpStat3)){

                                                // CASE 26 셋팅.
                                                sWorkCaseId = "26";

                                            }// if("W".equals(sYdEqpStat1)||
                                        }// if("2".equals(sYdEqpStat2)||
                                    }// if(isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"S")){
                                }// if(YdConstant.SPAN_ORDER_NEW_04.equals(sTmpDnWoLoc.substring(2, 4))||

                            }else{

                                if(YdConstant.SPAN_ORDER_NEW_02.equals(sTmpDnWoLoc.substring(2, 4))||
                                   YdConstant.SPAN_ORDER_NEW_03.equals(sTmpDnWoLoc.substring(2, 4))||
                                   YdConstant.SPAN_ORDER_NEW_07.equals(sTmpDnWoLoc.substring(2, 4))||
                                   YdConstant.SPAN_ORDER_NEW_TP.equals(sTmpDnWoLoc.substring(2, 4))||
                                   "PTB".equals(sTmpDnWoLoc.substring(2, 5))){
                                    if("1".equals(sYdEqpStat2)||
                                       "2".equals(sYdEqpStat2)||
                                       "3".equals(sYdEqpStat2)||
                                       "4".equals(sYdEqpStat2)){

                                        if("W".equals(sYdEqpStat1)||
                                           "3".equals(sYdEqpStat1)||
                                           "4".equals(sYdEqpStat1)){

                                            // CASE 23 셋팅.
                                            sWorkCaseId = "23";

                                        }// if("W".equals(sYdEqpStat1)||
                                    }// if("1".equals(sYdEqpStat2)||
                                }else{
                                    if(isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"S")){
                                        if("1".equals(sYdEqpStat2)||
                                           "2".equals(sYdEqpStat2)||
                                           "3".equals(sYdEqpStat2)){

                                            if("4".equals(sYdEqpStat3)||
                                               "W".equals(sYdEqpStat3)){

                                                // CASE 24 셋팅.
                                                sWorkCaseId = "24";

                                            }// if("4".equals(sYdEqpStat3)||
                                        }// if("1".equals(sYdEqpStat2)||
                                    }else{
                                        if("2".equals(sYdEqpStat2)||
                                           "3".equals(sYdEqpStat2)||
                                           "4".equals(sYdEqpStat2)){

                                            if("W".equals(sYdEqpStat1)||
                                               "4".equals(sYdEqpStat1)){

                                                // CASE 27 셋팅.
                                                sWorkCaseId = "27";

                                            }// if("W".equals(sYdEqpStat1)||
                                        }// if("2".equals(sYdEqpStat2)||
                                    }// if(isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"S")){
                                }// if(YdConstant.SPAN_ORDER_NEW_07.equals(sTmpDnWoLoc.substring(2, 4))||
                            }// if(YdConstant.SPAN_ORDER_NEW_04.equals(sMDnWoLoc.substring(2, 4))||
                        }// if(iSchCnt == 1){
                    }// if(jkrsCrnsch.size() > 0) {
                }// if(iYDEqpId == 3){
            }else{
                // 가동대수가 2일때.
                if(iYDEqpId == 2){
                    // 입고대기건수가 없을경우
                    if(iSchCnt == 1){

                        if(isSchCdInfo(rsCrnsch,"L")){
                            if("1".equals(sYdEqpStat1)||
                               "2".equals(sYdEqpStat1)){

                                if("W".equals(sYdEqpStat2)){

                                    sTmpDnWoLoc = this.getCrnDnWoLocInfo(szYdGp+sBayGp+"CR"+sBayGp+"1");
                                    if(!"".equals(sTmpDnWoLoc)&&isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"S")){
                                        if(this.getCrnDmCntInfo("K"+sBayGp+"CR"+sBayGp+"1") == 0){
                                            // Case1 셋팅.
                                            sWorkCaseId = "1";
                                        }
                                    }// if(isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"B")){
                                }// if("4".equals(sYdEqpStat2)||
                            }// if("1".equals(sYdEqpStat1)||
                        }else{

                            if("1".equals(sYdEqpStat2)||
                               "2".equals(sYdEqpStat2)){

                                if("W".equals(sYdEqpStat1)){

                                    sTmpDnWoLoc = this.getCrnDnWoLocInfo(szYdGp+sBayGp+"CR"+sBayGp+"2");

                                    if(!"".equals(sTmpDnWoLoc)&&isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"B")){
                                        if(this.getCrnDmCntInfo(szYdGp+sBayGp+"CR"+sBayGp+"2") == 0){
                                            // Case2 셋팅.
                                            sWorkCaseId = "2";
                                        }
                                    }// if(isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"S")){
                                }// if("4".equals(sYdEqpStat1)||
                            }// if("1".equals(sYdEqpStat2)||
                        }// if("L".equals(sMSchCd.substring(7))){

                    // 입고대기건수가 있을경우
                    }else if(iSchCnt >= 2){

                        if(isSchCdInfo(rsCrnsch,"L")){
                            if("1".equals(sYdEqpStat1)||
                               "2".equals(sYdEqpStat1)||
                               "3".equals(sYdEqpStat1)||
                               "4".equals(sYdEqpStat1)){

                                if("2".equals(sYdEqpStat2)||
                                   "3".equals(sYdEqpStat2)||
                                   "4".equals(sYdEqpStat2)||
                                   "W".equals(sYdEqpStat2)){

                                    // Case3 셋팅.
                                    sWorkCaseId = "3";

                                }// if("3".equals(sYdEqpStat2)||
                            }// if("1".equals(sYdEqpStat1)||
                        }else if(isSchCdInfo(rsCrnsch,"R")){
                            if("1".equals(sYdEqpStat2)||
                               "2".equals(sYdEqpStat2)||
                               "3".equals(sYdEqpStat2)||
                               "4".equals(sYdEqpStat2)){

                                if("2".equals(sYdEqpStat1)||
                                   "3".equals(sYdEqpStat1)||
                                   "4".equals(sYdEqpStat1)||
                                   "W".equals(sYdEqpStat1)){

                                    // Case4 셋팅.
                                    sWorkCaseId = "4";

                                }// if("3".equals(sYdEqpStat1)||
                            }// if("1".equals(sYdEqpStat2)||
                        }else{

                            // NEW CASE START
                            if("1".equals(sYdEqpStat1)||
                               "2".equals(sYdEqpStat1)){

                                if("4".equals(sYdEqpStat2)||
                                   "W".equals(sYdEqpStat2)){

                                    sTmpDnWoLoc = this.getCrnDnWoLocInfo(szYdGp+sBayGp+"CR"+sBayGp+"1");

                                    if(!"".equals(sTmpDnWoLoc)&&isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"S")){
                                        // Case3 셋팅.
                                        sWorkCaseId = "3";
                                    }// if(isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"S")){
                                }
                            }else if("4".equals(sYdEqpStat1)||
                                     "W".equals(sYdEqpStat1)){

                                if("1".equals(sYdEqpStat2)||
                                   "2".equals(sYdEqpStat2)){

                                    sTmpDnWoLoc = this.getCrnDnWoLocInfo(szYdGp+sBayGp+"CR"+sBayGp+"2");

                                    if(!"".equals(sTmpDnWoLoc)&&isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"B")){
                                        // Case4 셋팅.
                                        sWorkCaseId = "4";
                                    }// if(isSchLocCheck(sTmpDnWoLoc,rsCrnsch,"S")){
                                }// if("3".equals(sYdEqpStat1)||
                            }// if("1".equals(sYdEqpStat2)||
                            // NEW CASE END
                        }// if(isSchInfo(rsCrnsch,"L")){
                    }// if(iSchCnt == 1){
                }// if(iYDEqpId == 2){
            }// if("B".equals(sBayGp)||

            szMsg="[입고작업 크레인스케쥴 변경작업] 결과 CASE : "+sWorkCaseId;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            /*
             * CASE 에 따른 처리 로직 분리

             1,2,5,6,7,8,9,10,17,18,19,20 >
                현재 작업예약/스케쥴 스케쥴코드변경
                변경된 스케쥴코드에 해당하는 설비ID가져오기
                리턴 : 설비ID

             3,4,11,12,13,14,15,16,21,22,23,24,25,26,27>
                스케쥴등록 대상중 To위치가 큰/적은 제품을 선택
                위 대상 작업예약/스케쥴 스케쥴코드변경
                변경된 스케쥴코드에 해당하는 설비ID가져오기
                대상이 현작업이면
                    리턴 : 설비ID
                대상이 기작업이면
                    해당 설비ID가 IDLE시 작업지시
                    리턴 : -

              1,5,7, 9,18,19 >          현작업      , 'R'로 변경
              2,6,8,10,17,20 >          현작업      , 'L'로 변경
              3,11,13,15,22,24 >        큰제품선택  , 'R'로 변경
              4,12,14,16,21,23,25,27 >  적은제품선택    , 'L'로 변경
              26                >       큰제품선택  , 'L'로 변경
             */
            String sNewCase     = "";
            String sNewSchGbn   = "";
            String sNewStlGbn   = "";

                  if("1".equals(sWorkCaseId)||"5".equals(sWorkCaseId)||"7".equals(sWorkCaseId)||
                     "9".equals(sWorkCaseId)||"18".equals(sWorkCaseId)||"19".equals(sWorkCaseId)){
                      sNewCase      = "1";
                      sNewSchGbn    = "R";
                      sNewStlGbn    = "";
            }else if("2".equals(sWorkCaseId)||"6".equals(sWorkCaseId)||"8".equals(sWorkCaseId)||
                     "10".equals(sWorkCaseId)||"17".equals(sWorkCaseId)||"20".equals(sWorkCaseId)){
                    sNewCase        = "1";
                    sNewSchGbn      = "L";
                    sNewStlGbn      = "";
            }else if("3".equals(sWorkCaseId)||"11".equals(sWorkCaseId)||"13".equals(sWorkCaseId)||
                     "15".equals(sWorkCaseId)||"22".equals(sWorkCaseId)||"24".equals(sWorkCaseId)){
                    sNewCase        = "2";
                    sNewSchGbn      = "R";
                    sNewStlGbn      = "B";
            }else if("4".equals(sWorkCaseId)||"12".equals(sWorkCaseId)||"14".equals(sWorkCaseId)||
                     "16".equals(sWorkCaseId)||"21".equals(sWorkCaseId)||"23".equals(sWorkCaseId)||
                     "25".equals(sWorkCaseId)||"27".equals(sWorkCaseId)){
                    sNewCase        = "2";
                    sNewSchGbn      = "L";
                    sNewStlGbn      = "S";
            }else if("26".equals(sWorkCaseId)){
                    sNewCase        = "2";
                    sNewSchGbn      = "L";
                    sNewStlGbn      = "B";
            }

            String sNewSchCd = "";
            String sNewEqpId = "";
            String sNewSchId = "";

            if("1".equals(sNewCase)){
                rsCrnsch.absolute(1);
                recCrnSch = JDTORecordFactory.getInstance().create();
                recCrnSch.setRecord(rsCrnsch.getRecord());

                String sTmpYdSchId = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID");
                String sTmpYdSchCd = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD");
                String sTmpYdCrSts = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WRK_PROG_STAT");

                sNewSchCd = sTmpYdSchCd.substring(0, 7)+sNewSchGbn;
                sNewEqpId = this.getEqpInfo(sNewSchCd);
                sNewSchId = sTmpYdSchId;

                if(!"".equals(sNewEqpId)&&
                    "W".equals(sTmpYdCrSts)){
                    //스케쥴 설비ID UPDATE
                    JDTORecord recTmp = JDTORecordFactory.getInstance().create();
                    recTmp.setField("YD_SCH_CD"     ,sNewSchCd);
                    recTmp.setField("YD_EQP_ID"     ,sNewEqpId);
                    recTmp.setField("YD_CRN_SCH_ID" ,sNewSchId);

                    ydCrnSchDao.updYdCrnschReSch(recTmp,7);

                    szMsg="[입고작업 크레인스케쥴 변경작업] 이전 설비 : "+sMEqpId+"/ 결과 설비 : "+sNewEqpId;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                }
            }else if("2".equals(sNewCase)){

                String sTmpYdSchId1 = "";
                String sTmpYdSchCd1 = "";
                String sTmpYdCrSts1 = "";
                String sTmpDnWoLoc1 = "";
                String sTmpYdSchId2 = "";
                String sTmpYdSchCd2 = "";
                String sTmpYdCrSts2 = "";
                String sTmpDnWoLoc2 = "";
                int iSeq            = 0;

                for(int index = 1; index <= rsCrnsch.size(); index++) {
                    rsCrnsch.absolute(index);
                    recCrnSch = JDTORecordFactory.getInstance().create();
                    recCrnSch.setRecord(rsCrnsch.getRecord());

                    sTmpYdSchId1 = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID");
                    sTmpYdSchCd1 = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD");
                    sTmpDnWoLoc1 = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC");
                    sTmpYdCrSts1 = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WRK_PROG_STAT");

                    if("W".equals(sTmpYdCrSts1)){

                        iSeq++;

                        if(iSeq == 1){
                            sTmpYdSchCd2 = sTmpYdSchCd1;
                            sTmpYdSchId2 = sTmpYdSchId1;
                            continue;
                        }

                        if(sNewStlGbn.equals("B")){
                            if(sTmpDnWoLoc2.compareTo(sTmpDnWoLoc1) <= 0){
                                sTmpYdSchCd2 = sTmpYdSchCd1;
                                sTmpYdSchId2 = sTmpYdSchId1;
                            }
                        }else if(sNewStlGbn.equals("S")){
                            if(sTmpDnWoLoc2.compareTo(sTmpDnWoLoc1) >= 0){
                                sTmpYdSchCd2 = sTmpYdSchCd1;
                                sTmpYdSchId2 = sTmpYdSchId1;
                            }
                        }
                    }
                }

                sNewSchCd = sTmpYdSchCd2.substring(0, 7)+sNewSchGbn;
                sNewEqpId = this.getEqpInfo(sNewSchCd);
                if(!"".equals(sNewEqpId)&&
                   !"".equals(sTmpYdSchId2)){

                    //스케쥴 설비ID UPDATE
                    JDTORecord recTmp = JDTORecordFactory.getInstance().create();
                    recTmp.setField("YD_SCH_CD"     ,sNewSchCd);
                    recTmp.setField("YD_EQP_ID"     ,sNewEqpId);
                    recTmp.setField("YD_CRN_SCH_ID" ,sTmpYdSchId2);

                    ydCrnSchDao.updYdCrnschReSch(recTmp,7);
                }
            }

            if("1".equals(sNewCase)||
               "2".equals(sNewCase)){
                if(sMEqpId.equals(sNewEqpId)){

                    szMsg="[입고작업 크레인스케쥴 변경작업] 해당작업 이전 설비 : "+sMEqpId+"/ 결과 설비 : "+sNewEqpId;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                }else{
                    szMsg="[입고작업 크레인스케쥴 변경작업] 작업지시 요구  설비  : "+sNewEqpId;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    recEquip    = JDTORecordFactory.getInstance().create();
                    recEquip.setField("YD_EQP_ID",  sNewEqpId);

                    int intRtnVal = this.chkY4CrnWrkOrdReq(recEquip);
                }
            }
        }catch(Exception e){
            szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

    }//end of Y4CrnLWorkReSchedule()


    /**
     * 오퍼레이션명 : 크레인 입고작업 스케쥴코드 재편 Main
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public void Y4CrnLWorkReScheduleNewMain(JDTORecord msgRecord)throws JDTOException{

        YdCrnSchDao  YdCrnSchDao  = new YdCrnSchDao();

        JDTORecord recIntTemp = null;

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y4CrnLWorkReScheduleNewMain";
        String szOperationName          = "후판제품크레인 조정스케쥴메인";

        String szEqpId                  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");

        szMsg = "크레인 입고작업 스케쥴코드 재편 설비ID["+szEqpId+"]";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        /*
         * 중요 : 설비Id가 변경될 경우 리턴값 처리.
         *
         * 2012.07.09 윤재광
         * 파일링관련 작업지시인 경우
         * T : 파일링가능, F : 파일링 SKIP   -- 파일링정보가 없을 경우.
         * S : 파일링가능, E : 파일링 SKIP   -- 파일링정보가 있는 경우.
         * 스케쥴에 YD_SCH_ST_GP 항목이 위의 Case인 경우는 아래 메소드는 SKIP한다.
         */
        JDTORecordSet rsPiling = JDTORecordFactory.getInstance().createRecordSet("Temp");
        JDTORecord recPiling = JDTORecordFactory.getInstance().create();
        recPiling.setField("YD_EQP_ID", szEqpId);

        intRtnVal = YdCrnSchDao.getYdCrnsch(recPiling, rsPiling, 15);

        String reYD_SCH_ST_GP = "";
        String reYD_SCH_CD = "";

        if(intRtnVal > 0) {

            rsPiling.first();
            recIntTemp      = rsPiling.getRecord();
            reYD_SCH_ST_GP  = ydDaoUtils.paraRecChkNull(recIntTemp, "YD_SCH_ST_GP");
            reYD_SCH_CD     = ydDaoUtils.paraRecChkNull(recIntTemp, "YD_SCH_CD");
        }
        if("T".equals(reYD_SCH_ST_GP)||"S".equals(reYD_SCH_ST_GP)||"E".equals(reYD_SCH_ST_GP)){
            //SKIP
        }else if("F".equals(reYD_SCH_ST_GP)){

            /*
             * 0. 입고작업이고 파일링작업이 아닐경우에만 수행
             *
             * 1. 입고작업 조정스케쥴 편성조건 체크
             *  - 후판창고 분리운영여부 체크
             *  - B,D,E,F동인지 체크
             *  - R/T(RD,RE,RF)가 1후판인 경우만 체크
             *  - 크레인가동대수 체크(>=2)
             *  - 크레인 입고대기매수 체크(>=2)
             */
            String szYesNo  = "";

            rsPiling    = JDTORecordFactory.getInstance().createRecordSet("Temp");
            recPiling   = JDTORecordFactory.getInstance().create();
            recPiling.setField("YD_EQP_ID", szEqpId);
            recPiling.setField("YD_SCH_CD", reYD_SCH_CD);

            intRtnVal = YdCrnSchDao.getYdCrnResch(recPiling, rsPiling, 1);

            if(intRtnVal > 0) {

                rsPiling.first();
                recIntTemp  = rsPiling.getRecord();
                szYesNo     = ydDaoUtils.paraRecChkNull(recIntTemp, "RETURN_YN");
            }

            szMsg="["+szOperationName+"] 크레인조정스케쥴["+szEqpId+"]["+reYD_SCH_CD+"] 리턴값="+szYesNo;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            if("Y".equals(szYesNo)){

                boolean isOk = this.Y4CrnLWorkReScheduleNew(msgRecord);

                if(isOk){

                    szMsg="["+szOperationName+"] 크레인조정스케쥴 성공으로 작업지시요구 SKIP["+szEqpId+"]["+reYD_SCH_CD+"]";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    /*
                     * 2015.01.12 윤재광
                     * 일단 막음 - 에러예) 4호기 권하완료 후 작업지시 요구일때, 이 로직을 수행은 리턴되면 권하완료상태로 남음..
                     */
                    //return YdConstant.RETN_CD_SUCCESS;
                }
            }
        }

    }

    /**
     * 오퍼레이션명 : 크레인 입고작업외 기송신된 작업지시가 5분이상 선택상태로 경과시 스케쥴작업 취소처리
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public void Y4CrnLWorkReScheduleNewSub(JDTORecord msgRecord)throws JDTOException{

        YdCrnSchDao  YdCrnSchDao  = new YdCrnSchDao();
        YdEqpDao     ydEqpDao     = new YdEqpDao();

        JDTORecord recIntTemp = null;

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y4CrnLWorkReScheduleNewSub";
        String szOperationName          = "후판제품크레인 조정스케쥴서브";

        String szEqpId                  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(msgRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "후판제품크레인 조정스케쥴서브(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        
        szMsg = "크레인 입고작업 조정스케쥴서브 설비ID["+szEqpId+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

        /*
         * 현재 선택된 스케쥴정보가 작업지시 송신 후 5분 이상 경과된 스케쥴인지 체크한다.
         * 5분이상 경과된 스케쥴일경우 해당 스케쥴정보를 취소한다.
         */
        JDTORecordSet rsPiling = JDTORecordFactory.getInstance().createRecordSet("Temp");
        JDTORecord recPiling = JDTORecordFactory.getInstance().create();
        recPiling.setField("YD_EQP_ID", szEqpId);

        intRtnVal = YdCrnSchDao.getYdCrnsch(recPiling, rsPiling, 56);

        String reYD_CRN_SCH_ID  = "";
        String reIS_OK          = "N";

        if(intRtnVal > 0) {

            rsPiling.first();
            recIntTemp          = rsPiling.getRecord();
            reYD_CRN_SCH_ID     = ydDaoUtils.paraRecChkNull(recIntTemp, "YD_CRN_SCH_ID");
            reIS_OK             = ydDaoUtils.paraRecChkNull(recIntTemp, "IS_OK");
        }

        if("Y".equals(reIS_OK)){

            // 전사물류개선 2021. 4. 3
            // 5분이 경과가 되었더라도. 자동화크레인의 경우는 스케쥴 취소를 보내지 않는다.
            boolean bCancel = true;
            if(PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szEqpId)){
                // 무인크레인인지 여부를 확인하자
                JDTORecordSet jsEqpInfo =  JDTORecordFactory.getInstance().createRecordSet("temp");
                intRtnVal = YdCommonUtils.getYdEqp(szEqpId, jsEqpInfo);
                if(intRtnVal>0){
                    String szEqpAutoCrnMode= jsEqpInfo.getRecord(0).getFieldString("YD_EQP_AUTO_CRN_MODE"); // AutoCrn 상태
                    String szEqpAutoCrnYN   = jsEqpInfo.getRecord(0).getFieldString("YD_EQP_WRK_MODE2");   // AutoCrn 여부
                    if ("A".equals(szEqpAutoCrnYN)) {
//                      if (!("4".equals(szEqpAutoCrnMode) || "5".equals(szEqpAutoCrnMode))) {
                            bCancel = false;
//                      }
                    }
                }
            }

            if(bCancel){

                JDTORecord recPara = JDTORecordFactory.getInstance().create();

                recPara.setField("YD_CRN_SCH_ID"    , reYD_CRN_SCH_ID);
                recPara.setField("YD_WRK_PROG_STAT" , "W");

                // 5. 스케줄 테이블에 UPDATE
                intRtnVal = YdCrnSchDao.updYdCrnsch(recPara, 0);

                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("YD_EQP_ID", szEqpId);
                recPara.setField("YD_EQP_STAT", YdConstant.YD_EQP_STAT_IDLE);

                intRtnVal = ydEqpDao.updYdEqp(recPara, 0);

                JDTORecord recDelPara   = JDTORecordFactory.getInstance().create();
                recDelPara.setField("MSG_ID",               YdConstant.YDYDJ701);
                recDelPara.setField(YdConstant.BUFFER_TC_CD,"YDY8L004");


                recDelPara.setField("YD_CRN_SCH_ID",    reYD_CRN_SCH_ID    );
                recDelPara.setField("YD_WRK_PROG_STAT", "1"                );   // 이모듈을 탈려면 항상 '1'의값이 들어옴
                recDelPara.setField("MSG_GP",           "D"                );


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// sendMsg call 시  recDelPara 에 logId SET 추가 개선
                recDelPara.setField("LOG_ID", logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
                
                YdDelegate ydDelegate = new YdDelegate();
                ydDelegate.sendMsg(recDelPara);

                szMsg = "[Jsp Session : "+szOperationName+"] 작업지시취소전문 송신] 취소전문";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                /* 스케쥴 메인에서 작업지시 요구 호출함
                {//작업지시요구 처리
                    recPara     = JDTORecordFactory.getInstance().create();
                    recPara.setField("YD_EQP_ID",  szEqpId);

                    this.chkY4CrnWrkOrdReq(recPara);
                }
                 */
            }

        }

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
        szMsg = "후판제품크레인 조정스케쥴서브(" + szMethodName + ") 완료";
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        
    }

    /**
     * 오퍼레이션명 : 크레인 입고작업 스케쥴코드 재편
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public boolean Y4CrnLWorkReScheduleNew(JDTORecord msgRecord)throws JDTOException{

        boolean isOk = false;

        //설비 DAO
        YdEqpDao        ydEqpDao        = new YdEqpDao();
        YdCrnSchDao     ydCrnSchDao     = new YdCrnSchDao();
        YdUtils       ydUtils           = new YdUtils();

        JDTORecordSet rsCrnsch          = null;
        JDTORecordSet rsEquip           = null;

        JDTORecord recEquip             = null;
        JDTORecord recCrnSch            = null;

        String szMethodName             = "Y4CrnLWorkReScheduleNew";
        String szOperationName          = "크레인 입고작업 스케쥴코드 재편";
        String szMsg                    = "";
        String sBayGp                   = "";

        int iYDEqpId    = 0;    // 설비가동대수
        int iEqpCnt     = 0;    // 설비설치대수
        int iSchCnt     = 0;    // 입고작업갯수

        String sYdEqpStat3 = "";
        String sYdEqpStat4 = "";
        String sYdEqpStat5 = "";
        String sMEqpId     = "";
        String szYdGp      = "";

        int iCR3        = 0;
        int iCR4        = 0;
        int iCR5        = 0;
        int iRDL_C      = 0;
        String sRDL_S   = "";
        int iRDR_C      = 0;
        String sRDR_S   = "";
        int iREL_C      = 0;
        String sREL_S   = "";
        int iRER_C      = 0;
        String sRER_S   = "";
        int iRFL_C      = 0;
        String sRFL_S   = "";
        int iRFR_C      = 0;
        String sRFR_S   = "";

        try{

            sMEqpId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");

            szYdGp  = YdConstant.YD_GP_PLATE2_GDS_YARD;
            sBayGp  = sMEqpId.substring(1, 2);

            rsCrnsch    = JDTORecordFactory.getInstance().createRecordSet("");
            rsEquip     = JDTORecordFactory.getInstance().createRecordSet("");

            recEquip    = JDTORecordFactory.getInstance().create();
            recEquip.setField("YD_GP",      szYdGp);
            recEquip.setField("YD_BAY_GP",  sBayGp);
            recEquip.setField("YD_EQP_GP",  "CR");

            iEqpCnt = ydEqpDao.getYdEqp(recEquip,rsEquip,2);
            iSchCnt = ydCrnSchDao.getYdCrnResch(recEquip,rsCrnsch,2);

            String sTmpEqpId    = "";
            String sTmpEqpStat  = "";

            {// 설비상태 정보 가져오기.
                for(int index = 1; index <= rsEquip.size(); index++) {
                    rsEquip.absolute(index);
                    recEquip = JDTORecordFactory.getInstance().create();
                    recEquip.setRecord(rsEquip.getRecord());

                    sTmpEqpId   = ydDaoUtils.paraRecChkNull(recEquip, "YD_EQP_ID");
                    sTmpEqpStat = ydDaoUtils.paraRecChkNull(recEquip, "YD_EQP_STAT");
                    if((szYdGp+sBayGp+"CR"+sBayGp+"3").equals(sTmpEqpId)){
                        sYdEqpStat3 = sTmpEqpStat;
                        if("B".equals(sTmpEqpStat)){
                        }else{
                            iYDEqpId++;
                        }
                    }else if((szYdGp+sBayGp+"CR"+sBayGp+"4").equals(sTmpEqpId)){
                        sYdEqpStat4 = sTmpEqpStat;
                        if("B".equals(sTmpEqpStat)){
                        }else{
                            iYDEqpId++;
                        }
                    }else if((szYdGp+sBayGp+"CR"+sBayGp+"5").equals(sTmpEqpId)){
                        sYdEqpStat5 = sTmpEqpStat;
                        if("B".equals(sTmpEqpStat)){
                        }else{
                            iYDEqpId++;
                        }
                    }
                }
            }

            szMsg = "크레인 입고작업 스케쥴코드 재편 INPUT값 sYdEqpStat3["+sYdEqpStat3+"]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "크레인 입고작업 스케쥴코드 재편 INPUT값 sYdEqpStat4["+sYdEqpStat4+"]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "크레인 입고작업 스케쥴코드 재편 INPUT값 sYdEqpStat5["+sYdEqpStat5+"]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "크레인 입고작업 스케쥴코드 재편 INPUT값 iYDEqpId["+iYDEqpId+"]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "크레인 입고작업 스케쥴코드 재편 INPUT값 iSchCnt["+iSchCnt+"]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            for(int index = 1; index <= rsCrnsch.size(); index++) {

                rsCrnsch.absolute(index);
                recCrnSch = JDTORecordFactory.getInstance().create();
                recCrnSch.setRecord(rsCrnsch.getRecord());

                iCR3   = ydDaoUtils.paraRecChkNullInt(recCrnSch, "CR3");
                iCR4   = ydDaoUtils.paraRecChkNullInt(recCrnSch, "CR4");
                iCR5   = ydDaoUtils.paraRecChkNullInt(recCrnSch, "CR5");
                iRDL_C = ydDaoUtils.paraRecChkNullInt(recCrnSch, "RDL_C");
                sRDL_S = ydDaoUtils.paraRecChkNull(recCrnSch   , "RDL_S");
                iRDR_C = ydDaoUtils.paraRecChkNullInt(recCrnSch, "RDR_C");
                sRDR_S = ydDaoUtils.paraRecChkNull(recCrnSch   , "RDR_S");
                iREL_C = ydDaoUtils.paraRecChkNullInt(recCrnSch, "REL_C");
                sREL_S = ydDaoUtils.paraRecChkNull(recCrnSch   , "REL_S");
                iRER_C = ydDaoUtils.paraRecChkNullInt(recCrnSch, "RER_C");
                sRER_S = ydDaoUtils.paraRecChkNull(recCrnSch   , "RER_S");
                iRFL_C = ydDaoUtils.paraRecChkNullInt(recCrnSch, "RFL_C");
                sRFL_S = ydDaoUtils.paraRecChkNull(recCrnSch   , "RFL_S");
                iRFR_C = ydDaoUtils.paraRecChkNullInt(recCrnSch, "RFR_C");
                sRFR_S = ydDaoUtils.paraRecChkNull(recCrnSch   , "RFR_S");

            }

            String sSetCrnId = "";
            String sSetCrnGp = "";

            if("D".equals(sBayGp)||"E".equals(sBayGp)){
                //3대 정상가동시
                if(iYDEqpId == 3){

                    if(iCR3 >= 2){
                        if((iCR3 > iCR4)&&(iCR3-iCR4>=2)&&iREL_C>=1){
                            sSetCrnId = sREL_S;
                            sSetCrnGp = "4";
                            isOk = true;
                        }
                    }else if(iCR4 >= 2){
                        if((iCR4 > iCR3)&&(iCR4-iCR3>=2)&&iRDR_C>=1){
                            sSetCrnId = sRDR_S;
                            sSetCrnGp = "3";
                            isOk = true;
                        }else if((iCR4 > iCR5)&&(iCR4-iCR5>=2)&&iRER_C>=1){
                            sSetCrnId = sRER_S;
                            sSetCrnGp = "5";
                            isOk = true;
                        }
                    }else if(iCR5 >= 2){
                        if((iCR5 > iCR4)&&(iCR5-iCR4>=2)&&iRFR_C>=1){
                            sSetCrnId = sRFR_S;
                            sSetCrnGp = "4";
                            isOk = true;
                        }
                    }

                    if(isOk == false){
                        if(iCR3 >= 2){
                            if((iCR3 > iCR4)&&(iCR3-iCR4>=2)&&iRDL_C>=1){
                                sSetCrnId = sRDL_S;
                                sSetCrnGp = "4";
                                isOk = true;
                            }
                        }else if(iCR4 >= 2){
                            if((iCR4 > iCR3)&&(iCR4-iCR3>=2)&&iRER_C>=1){
                                sSetCrnId = sRER_S;
                                sSetCrnGp = "3";
                                isOk = true;
                            }else if((iCR4 > iCR5)&&(iCR4-iCR5>=2)&&iRFL_C>=1){
                                sSetCrnId = sRFL_S;
                                sSetCrnGp = "5";
                                isOk = true;
                            }
                        }else if(iCR5 >= 2){
                            if((iCR5 > iCR4)&&(iCR5-iCR4>=2)&&iRFL_C>=1){
                                sSetCrnId = sRFL_S;
                                sSetCrnGp = "4";
                                isOk = true;
                            }
                        }
                    }

                //3호기 고장시
                }else if("B".equals(sYdEqpStat3)){

                    if(iCR4 >= 2){
                        if((iCR4 > iCR5)&&(iCR4-iCR5>=2)&&iREL_C>=1){
                            sSetCrnId = sREL_S;
                            sSetCrnGp = "5";
                            isOk = true;
                        }
                    }else if(iCR5 >= 2){
                        if((iCR5 > iCR4)&&(iCR5-iCR4>=2)&&iRER_C>=1){
                            sSetCrnId = sRER_S;
                            sSetCrnGp = "4";
                            isOk = true;
                        }
                    }

                    if(isOk == false){
                        if(iCR4 >= 2){
                            if((iCR4 > iCR5)&&(iCR4-iCR5>=2)&&iRDR_C>=1){
                                sSetCrnId = sRDR_S;
                                sSetCrnGp = "5";
                                isOk = true;
                            }
                        }else if(iCR5 >= 2){
                            if((iCR5 > iCR4)&&(iCR5-iCR4>=2)&&iRFL_C>=1){
                                sSetCrnId = sRFL_S;
                                sSetCrnGp = "4";
                                isOk = true;
                            }
                        }
                    }

                    if(isOk == false){
                        if(iCR4 >= 2){
                            if((iCR4 > iCR5)&&(iCR4-iCR5>=2)&&iRDL_C>=1){
                                sSetCrnId = sRDL_S;
                                sSetCrnGp = "5";
                                isOk = true;
                            }
                        }else if(iCR5 >= 2){
                            if((iCR5 > iCR4)&&(iCR5-iCR4>=2)&&iRFR_C>=1){
                                sSetCrnId = sRFR_S;
                                sSetCrnGp = "4";
                                isOk = true;
                            }
                        }
                    }
                //4호기 고장시
                }else if("B".equals(sYdEqpStat4)){

                    if(iCR3 >= 2){
                        if((iCR3 > iCR5)&&(iCR3-iCR5>=2)&&iREL_C>=1){
                            sSetCrnId = sREL_S;
                            sSetCrnGp = "5";
                            isOk = true;
                        }
                    }else if(iCR5 >= 2){
                        if((iCR5 > iCR3)&&(iCR5-iCR3>=2)&&iRER_C>=1){
                            sSetCrnId = sRER_S;
                            sSetCrnGp = "3";
                            isOk = true;
                        }
                    }

                    if(isOk == false){
                        if(iCR3 >= 2){
                            if((iCR3 > iCR5)&&(iCR3-iCR5>=2)&&iRDR_C>=1){
                                sSetCrnId = sRDR_S;
                                sSetCrnGp = "5";
                                isOk = true;
                            }
                        }else if(iCR5 >= 2){
                            if((iCR5 > iCR3)&&(iCR5-iCR3>=2)&&iRFL_C>=1){
                                sSetCrnId = sRFL_S;
                                sSetCrnGp = "3";
                                isOk = true;
                            }
                        }
                    }

                    if(isOk == false){
                        if(iCR3 >= 2){
                            if((iCR3 > iCR5)&&(iCR3-iCR5>=2)&&iRDL_C>=1){
                                sSetCrnId = sRDL_S;
                                sSetCrnGp = "5";
                                isOk = true;
                            }
                        }else if(iCR5 >= 2){
                            if((iCR5 > iCR3)&&(iCR5-iCR3>=2)&&iRFR_C>=1){
                                sSetCrnId = sRFR_S;
                                sSetCrnGp = "3";
                                isOk = true;
                            }
                        }
                    }
                //5호기 고장시
                }else if("B".equals(sYdEqpStat5)){
                    if(iCR3 >= 2){
                        if((iCR3 > iCR4)&&(iCR3-iCR4>=2)&&iREL_C>=1){
                            sSetCrnId = sREL_S;
                            sSetCrnGp = "4";
                            isOk = true;
                        }
                    }else if(iCR4 >= 2){
                        if((iCR4 > iCR3)&&(iCR4-iCR3>=2)&&iRER_C>=1){
                            sSetCrnId = sRER_S;
                            sSetCrnGp = "3";
                            isOk = true;
                        }
                    }

                    if(isOk == false){
                        if(iCR3 >= 2){
                            if((iCR3 > iCR4)&&(iCR3-iCR4>=2)&&iRDR_C>=1){
                                sSetCrnId = sRDR_S;
                                sSetCrnGp = "4";
                                isOk = true;
                            }
                        }else if(iCR4 >= 2){
                            if((iCR4 > iCR3)&&(iCR4-iCR3>=2)&&iRFL_C>=1){
                                sSetCrnId = sRFL_S;
                                sSetCrnGp = "3";
                                isOk = true;
                            }
                        }
                    }

                    if(isOk == false){
                        if(iCR3 >= 2){
                            if((iCR3 > iCR4)&&(iCR3-iCR4>=2)&&iRDL_C>=1){
                                sSetCrnId = sRDL_S;
                                sSetCrnGp = "4";
                                isOk = true;
                            }
                        }else if(iCR4 >= 2){
                            if((iCR4 > iCR3)&&(iCR4-iCR3>=2)&&iRFR_C>=1){
                                sSetCrnId = sRFR_S;
                                sSetCrnGp = "3";
                                isOk = true;
                            }
                        }
                    }
                }
            }else if("B".equals(sBayGp)||"F".equals(sBayGp)){
                //2대 정상가동시
                if(iYDEqpId == 2){
                    if(iCR3 >= 2){
                        if((iCR3 > iCR4)&&(iCR3-iCR4>=2)&&iREL_C>=1){
                            sSetCrnId = sREL_S;
                            sSetCrnGp = "4";
                            isOk = true;
                        }
                    }else if(iCR4 >= 2){
                        if((iCR4 > iCR3)&&(iCR4-iCR3>=2)&&iRER_C>=1){
                            sSetCrnId = sRER_S;
                            sSetCrnGp = "3";
                            isOk = true;
                        }
                    }

                    if(isOk == false){
                        if(iCR3 >= 2){
                            if((iCR3 > iCR4)&&(iCR3-iCR4>=2)&&iRDR_C>=1){
                                sSetCrnId = sRDR_S;
                                sSetCrnGp = "4";
                                isOk = true;
                            }
                        }else if(iCR4 >= 2){
                            if((iCR4 > iCR3)&&(iCR4-iCR3>=2)&&iRFL_C>=1){
                                sSetCrnId = sRFL_S;
                                sSetCrnGp = "3";
                                isOk = true;
                            }
                        }
                    }

                    if(isOk == false){
                        if(iCR3 >= 2){
                            if((iCR3 > iCR4)&&(iCR3-iCR4>=2)&&iRDL_C>=1){
                                sSetCrnId = sRDL_S;
                                sSetCrnGp = "4";
                                isOk = true;
                            }
                        }else if(iCR4 >= 2){
                            if((iCR4 > iCR3)&&(iCR4-iCR3>=2)&&iRFR_C>=1){
                                sSetCrnId = sRFR_S;
                                sSetCrnGp = "3";
                                isOk = true;
                            }
                        }
                    }

                }
            }

            //2014.12.23 5호기고장과 B/F동 2대 가동로직은 동일함.

            szMsg="[입고작업 크레인스케쥴 변경작업] 결과 CASE : "+sSetCrnId;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg="[입고작업 크레인스케쥴 변경작업] 결과 CASE : "+sSetCrnGp;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            String sChgEqpId = szYdGp+sBayGp+"CR"+sBayGp+sSetCrnGp;

            if(!sMEqpId.equals(sChgEqpId)&&isOk){

                {//스케쥴정보 설비ID UPDATE
                    JDTORecord recTmp = JDTORecordFactory.getInstance().create();
                    recTmp.setField("YD_EQP_ID"     ,sChgEqpId);
                    recTmp.setField("YD_CRN_SCH_ID" ,sSetCrnId);

                    ydCrnSchDao.updYdCrnschReSch(recTmp,8);
                }

                {//작업지시요구 처리
                    recEquip    = JDTORecordFactory.getInstance().create();
                    recEquip.setField("YD_EQP_ID",  sChgEqpId);

                    this.chkY4CrnWrkOrdReq(recEquip);
                }

                isOk = true;
            }else{
                isOk = false;
            }

            return isOk;

        }catch(Exception e){
            szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

    }//end of Y4CrnLWorkReScheduleNew()

    private String getCrnDnWoLocInfo(String sEqpId) throws JDTOException{

        YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();

        String sTmpDnWoLoc = "";

        try{
            JDTORecordSet jkrsCrnsch    = JDTORecordFactory.getInstance().createRecordSet("");
            JDTORecord jkrecEquip       = JDTORecordFactory.getInstance().create();
            JDTORecord recCrnSch        = null;

            jkrecEquip.setField("YD_EQP_ID",sEqpId);

            ydCrnSchDao.getYdCrnsch(jkrecEquip,jkrsCrnsch,15);

            if(jkrsCrnsch.size() > 0) {

                jkrsCrnsch.absolute(1);
                recCrnSch = JDTORecordFactory.getInstance().create();
                recCrnSch.setRecord(jkrsCrnsch.getRecord());

                sTmpDnWoLoc = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC");
            }
            return sTmpDnWoLoc;
        }catch(Exception e){
            String szMsg=" Exception발생 : " + e.getMessage();
            throw new JDTOException(szMsg);
        }//end of try~catch
    }

    private int getCrnDmCntInfo(String sEqpId) throws JDTOException{

        int iCnt    = 0;

        YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();

        String sYdSchCd = "";

        try{
            JDTORecordSet jkrsCrnsch    = JDTORecordFactory.getInstance().createRecordSet("");
            JDTORecord jkrecEquip       = JDTORecordFactory.getInstance().create();
            JDTORecord recCrnSch        = null;

            jkrecEquip.setField("YD_EQP_ID",sEqpId);

            ydCrnSchDao.getYdCrnsch(jkrecEquip,jkrsCrnsch,15);

            if(jkrsCrnsch.size() > 0) {

                for(int index = 1; index <= jkrsCrnsch.size(); index++) {
                    jkrsCrnsch.absolute(index);

                    recCrnSch = JDTORecordFactory.getInstance().create();
                    recCrnSch.setRecord(jkrsCrnsch.getRecord());

                    sYdSchCd = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD");

                    ydUtils.putLog(szSessionName, "", "출하작업="+sEqpId.substring(0,2), YdConstant.DEBUG);
                    ydUtils.putLog(szSessionName, "", "출하작업="+sYdSchCd.substring(6,7), YdConstant.DEBUG);

                    if(sYdSchCd.startsWith(sEqpId.substring(0,2)+"PT") &&
                       "L".equals(sYdSchCd.substring(6,7))){
                        iCnt = 1;
                        break;
                    }
                }
            }
            return iCnt;
        }catch(Exception e){
            String szMsg=" Exception발생 : " + e.getMessage();
            throw new JDTOException(szMsg);
        }//end of try~catch
    }

    /**
     * 오퍼레이션명 : 크레인작업지시요구 판단
     *
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    private int chkY4CrnWrkOrdReq(JDTORecord msgRecord) throws JDTOException {
        YdEqpDao    ydEqpDao    = new YdEqpDao();

        JDTORecordSet rsResult = null;
        JDTORecord recInTemp   = null;
        JDTORecord recOutTemp  = null;

        int intRtnVal = 0 ;

        String szMsg            = "";
        String szMethodName     = "chkY4CrnWrkOrdReq";
        String szOperationName  = "크레인작업지시요구 판단";

        String szYD_EQP_STAT    = "";

        try{
            //설비Table조회
            rsResult    = JDTORecordFactory.getInstance().createRecordSet("Temp");
            intRtnVal   = ydEqpDao.getYdEqp(msgRecord, rsResult, 0);

            recOutTemp  = JDTORecordFactory.getInstance().create();
            recOutTemp.setRecord(rsResult.getRecord(0));

            szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_STAT");

            if(szYD_EQP_STAT.equals("W")) {

                recInTemp = JDTORecordFactory.getInstance().create();
//SJH03004
                recInTemp.setField("MSG_ID",           "YDYDJ642");
                recInTemp.setField("YD_EQP_ID",        ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_ID"));
                recInTemp.setField("YD_EQP_WRK_MODE",  ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_WRK_MODE"));
                recInTemp.setField("YD_WRK_PROG_STAT", "W");
                recInTemp.setField("YD_SCH_CD",        "");
                recInTemp.setField("YD_CRN_SCH_ID",    "");
                recInTemp.setField("YD_CRN_XAXIS",     "");
                recInTemp.setField("YD_CRN_YAXIS",     "");

                //크레인작업지시 송신
                ydDelegate.sendMsg(recInTemp);
            }else{
                szMsg="크레인설비의 상태가 Idle가 아닙니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            }

        }catch(Exception e){
            szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal = 1;

    }// end of chkY4CrnWrkOrdReq


    private String getEqpInfo(String sSchCd)throws JDTOException{

        YdUtils ydUtils         = new YdUtils();

        JDTORecordSet rsResult  = null;
        JDTORecord recPara      = null;
        boolean blnRtnVal       = true;

        String szMsg        = "";
        String szMethodName = "";

        String sReturnEqpId = "";

        try{
            //리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //스케줄 기준 체크
            blnRtnVal = this.chkGetSchRule(sSchCd, rsResult);
            if(!blnRtnVal) return sSchCd;

            //스케줄 기준 데이터 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //스케줄기준 체크
            //스케줄 금지 유무
            String szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
            //작업크레인
            String szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
            //대체크레인유무
            String szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
            //대체크레인
            String szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");

            //스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
            if(szYD_SCH_PROH_EXN.equals("Y")) {

                szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return sReturnEqpId;
            }

            JDTORecordSet rsCrnInfo = JDTORecordFactory.getInstance().createRecordSet("");
            //작업크레인 설비 상태 체크
            String szRtnMsg = this.eqpStatCheck(szYD_WRK_CRN,rsCrnInfo);

            //작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
            if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {

                szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                //대체크레인의 유무를 체크한다.
                //대체크레인이 없으면 에러 리턴
                if(!szYD_ALT_CRN_YN.equals("Y")) {

                    szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return sReturnEqpId;

                }

                //대체크레인이 있으면 대체크레인 설비 상태 체크
                rsCrnInfo = JDTORecordFactory.getInstance().createRecordSet("");

                szRtnMsg = this.eqpStatCheck(szYD_ALT_CRN , rsCrnInfo);
                //대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
                if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {

                    szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return sReturnEqpId;

                } else {
                    //대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
                    sReturnEqpId = szYD_ALT_CRN;
                }
            } else {
                //작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
                sReturnEqpId = szYD_WRK_CRN;
            }
            return sReturnEqpId;
        }catch(Exception e){
            throw new JDTOException("에러");
        }//end of try~catch
    }

    /**
     * 오퍼레이션명 : 스케줄기준 체크 및 데이터 반환
     *
     * @param  String     szSchCd 스케줄CD
     * @return boolean    true(성공), false(실패)
     * @throws JDTOException
     */
    private boolean chkGetSchRule(String szSchCd, JDTORecordSet rsResult)throws JDTOException  {

        //스케줄기준 DAO
        YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

        String szMsg              = null;
        String szMethodName       = "chkGetSchRule";
        String szOperationName          = "스케줄기준 체크 및 데이터 반환";
        int intRtnVal             = 0;
        boolean blnRtnVal         = false;
        JDTORecord recPara        = null;

        try {

            //조회항목 record 생성
            recPara = JDTORecordFactory.getInstance().create();

            //스케줄코드
            recPara.setField("YD_SCH_CD", szSchCd);

            //스케줄코드로 스케줄기준 Table 조회
            intRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsResult, 0);

            //리턴값 메세지처리
            if (intRtnVal > 1) {

                szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 중복되었습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if (intRtnVal == 1) {

                blnRtnVal = true;

            } else if (intRtnVal == 0) {

                szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if (intRtnVal == -2) {

                szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 parameter error 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else {

                szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 오류 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            }
        } catch(Exception e) {
            szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }
        return blnRtnVal = true;

    } //end of chkGetSchRule

    private boolean isSchCdInfo(JDTORecordSet rsCrnsch, String sGbn)throws JDTOException{

        YdUtils ydUtils         = new YdUtils();

        JDTORecord recCrnSch    = null;
        String sSchCd           = "";

        try{
            for(int index = 1; index <= rsCrnsch.size(); index++) {
                rsCrnsch.absolute(index);
                recCrnSch = JDTORecordFactory.getInstance().create();
                recCrnSch.setRecord(rsCrnsch.getRecord());

                sSchCd = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD");

                if(sGbn.equals(sSchCd.substring(7))){
                }else{
                    return false;
                }
            }
            return true;
        }catch(Exception e){
            throw new JDTOException("에러");
        }//end of try~catch

    }

    private boolean isSchLocInfo(JDTORecordSet rsCrnsch, String sGbn)throws JDTOException{

        YdUtils ydUtils         = new YdUtils();

        JDTORecord recCrnSch    = null;
        String sDnWoLoc         = "";

        try{
            for(int index = 1; index <= rsCrnsch.size(); index++) {
                rsCrnsch.absolute(index);
                recCrnSch = JDTORecordFactory.getInstance().create();
                recCrnSch.setRecord(rsCrnsch.getRecord());

                sDnWoLoc = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC");

                if(sGbn.equals("L")){
                    if(YdConstant.SPAN_ORDER_NEW_04.equals(sDnWoLoc.substring(2, 4))||
                       YdConstant.SPAN_ORDER_NEW_05.equals(sDnWoLoc.substring(2, 4))||
                       YdConstant.SPAN_ORDER_NEW_06.equals(sDnWoLoc.substring(2, 4))||
                       "PTA".equals(sDnWoLoc.substring(2, 5))){

                    }else{
                        return false;
                    }
                }else if(sGbn.equals("R")){
                    if(YdConstant.SPAN_ORDER_NEW_04.equals(sDnWoLoc.substring(2, 4))||
                       YdConstant.SPAN_ORDER_NEW_05.equals(sDnWoLoc.substring(2, 4))||
                       YdConstant.SPAN_ORDER_NEW_06.equals(sDnWoLoc.substring(2, 4))||
                       "PTA".equals(sDnWoLoc.substring(2, 5))){
                        return false;
                    }else{

                    }
                }
            }
            return true;
        }catch(Exception e){
            throw new JDTOException("에러");
        }//end of try~catch

    }

    private boolean isSchLocCheck(String s2DnWoLoc, JDTORecordSet rsCrnsch, String sGbn)throws JDTOException{

        YdUtils ydUtils         = new YdUtils();

        JDTORecord recCrnSch    = null;
        String sDnWoLoc         = "";

        try{
            for(int index = 1; index <= rsCrnsch.size(); index++) {
                rsCrnsch.absolute(index);
                recCrnSch = JDTORecordFactory.getInstance().create();
                recCrnSch.setRecord(rsCrnsch.getRecord());

                sDnWoLoc = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC");

                if(sGbn.equals("B")){
                    if(s2DnWoLoc.compareTo(sDnWoLoc) >= 0){

                    }else{
                        return false;
                    }
                }else if(sGbn.equals("S")){
                    if(s2DnWoLoc.compareTo(sDnWoLoc) <= 0){

                    }else{
                        return false;
                    }
                }
            }
            return true;
        }catch(Exception e){
            throw new JDTOException("에러");
        }//end of try~catch

    }

    private boolean isSchLocCheck(JDTORecordSet rsCrnsch, String sGbn, int iGbn)throws JDTOException{

        YdUtils ydUtils         = new YdUtils();

        JDTORecord recCrnSch    = null;
        String sDnWoLoc         = "";
        int    iDnWoLoc         = 0;

        try{
            for(int index = 1; index <= rsCrnsch.size(); index++) {
                rsCrnsch.absolute(index);
                recCrnSch = JDTORecordFactory.getInstance().create();
                recCrnSch.setRecord(rsCrnsch.getRecord());

                sDnWoLoc = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC");

                try{
                    iDnWoLoc = Integer.parseInt(sDnWoLoc.substring(2, 4));
                }catch(Exception x){
                    return false;
                }

                if(sGbn.equals("B")){
                    if(iDnWoLoc > iGbn){

                    }else{
                        return false;
                    }
                }else if(sGbn.equals("S")){
                    if(iDnWoLoc < iGbn){

                    }else{
                        return false;
                    }
                }
            }
            return true;
        }catch(Exception e){
            throw new JDTOException("에러");
        }//end of try~catch

    }

    /**
     * 오퍼레이션명 : 크레인 작업지시(작업지시를 재요구 하는 경우 사용한다.)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public int Y4ChkWrkProgStat(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws JDTOException  {
        YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
        YdEqpDao    ydEqpDao    = new YdEqpDao();

        JDTORecordSet rsResult          = null;
        JDTORecord recInTemp            = null;
        JDTORecord recOutTemp           = null;

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y4ChkWrkProgStat";
        String szCrnSchId               = "";

        String szQuery                  = "";

        String szYD_EQP_ID              = "";
        String szWrkProgStat            = "";
        String szYdEqpStat              = "";

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= msgRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg="chkWrkProgStat("+szMethodName+") 처리 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        try{
//          //파라미터 중 스케줄ID가 있다 스케줄 ID로 크레인 스케줄을 조회하여 작업예약 ID를 조회한다.
//          szCrnSchId = ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID");
//
//          rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//          intRtnVal = YdCrnSchDao.getYdCrnsch(msgRecord, rsResult, 0);
//          if(intRtnVal <= 0) {
//              szMsg="크레인 스케줄 조회중 Error";
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//          }


            //작업진행상태
            szWrkProgStat = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");


            //설비Table를 조회하여 설비상태가 '1 : 권상지시'또는 '3 : 권하지시'이라면 크레인 스케줄 Table에서 야드작업진행 상태가 1또는 3인 것만 찾는다. 1건만 나와야 정상이고 1건이 아니라면 Error처리
            rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
            intRtnVal = ydEqpDao.getYdEqp(msgRecord, rsResult, 0);
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="Y4ChkWrkProgStat getYdEqp : data not found";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.WARNING, logId);
                }else if(intRtnVal == -2) {
                    szMsg="Y4ChkWrkProgStat getYdEqp : parameter error";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                }
                return intRtnVal = -1;
            }

            recOutTemp = JDTORecordFactory.getInstance().create();
            recOutTemp.setRecord(rsResult.getRecord(0));
            szYdEqpStat = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_STAT");
            //설비상태가 1또는 3인경우
            if(szYdEqpStat.equals("1") || szYdEqpStat.equals("2") || szYdEqpStat.equals("3")){
                //설비id로 크레인 스케줄을 조회한다. 현재 작업진행상태가 1또는 3인 경우를 조회...
                rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
                intRtnVal = ydCrnSchDao.getYdCrnsch(msgRecord, rsResult, 16);
                if(intRtnVal != 1) {
                    //에러처리
                    szMsg="현재 작업진행상태가 1또는 3인 크레인 스케줄을 조회 중 Error";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    return intRtnVal = -1;
                }
            }

            rsCrnSch.addAll(rsResult);

        }catch(Exception e){

            szMsg="Error:" +e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return intRtnVal = -1;
        }


        szMsg="chkWrkProgStat("+szMethodName+") 처리 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
        return intRtnVal = 1;
    } //end of Y4ChkWrkProgStat()







    /**
     * 오퍼레이션명 : 크레인 작업지시(작업이 없을 경우 요구한다.)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public int Y4ChkWrkProgStatW(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws JDTOException  {
        YdEqpDao ydEqpDao = new YdEqpDao();
        YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
        JDTORecordSet rsResult = null;
        JDTORecord recIntTemp  = null;
        JDTORecord para  = null;

        int intRtnVal          = 0 ;


        String szMsg           = "";
        String szMethodName    = "Y4ChkWrkProgStatW";
        String szOperationName = "크레인작업지시조회(W인 경우)";
        String szQuery         = "";

        String szYdGp          = ""; //--2013.02.07 추가 (3기)
        String szYD_EQP_ID     = "";
        String szYD_CRN_SCH_ID  = "";
     // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
        boolean isSendToEaiY9           = false;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= msgRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg="크레인작업지시조회(W인 경우)("+szMethodName+") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

//2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        try{

            szMsg="["+szOperationName+"] ------------------------ 메소드 시작 ------------------------";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");

            // 2021. 1. 6 추가(Y9시스템 전송여부)
            isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szYD_EQP_ID);
            if(szYD_EQP_ID.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD)) { //--2013.02.07 추가 (3기)
                szYdGp  = YdConstant.YD_GP_PLATE2_GDS_YARD; //2후판제품창고 'T'
            } else {
                szYdGp  = YdConstant.YD_GP_PLATE_GDS_YARD;  //1후판제품창고 'K'
            }


            //-----------------------------------------------------------------------------------------------
            //  크레인 설비ID로 크레인 스케줄 전체에서 우선순위가 가장 빠른 스케줄을 조회한다. 크레인 스케줄을 조회한다.
            //-----------------------------------------------------------------------------------------------

            szMsg="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]로 크레인 스케줄 전체에서 우선순위가 가장 빠른 스케줄을 조회 시작";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
            para = JDTORecordFactory.getInstance().create();
            para.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"));

            intRtnVal = ydCrnSchDao.getYdCrnsch(para, rsResult, 15);

            szMsg="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]로 크레인 스케줄 전체에서 우선순위가 가장 빠른 스케줄을 조회 완료 - 반환값 : " + intRtnVal;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            //-----------------------------------------------------------------------------------------------

            if(intRtnVal == 0) {

                szMsg="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]로 크레인 스케줄 전체에서 우선순위가 가장 빠른 스케줄을 조회 결과 대상재가 없는 경우";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                //-----------------------------------------------------------------------------------------------
                //  크레인 설비ID로 더이상 크레인 스케줄이 없을 경우 설비Table에 설비상태를 'W' 명령선택대기(idle)로 변경한다.
                //-----------------------------------------------------------------------------------------------

                szMsg="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]의 설비상태[YD_EQP_STAT]를 W로 변경 시작";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                recIntTemp = JDTORecordFactory.getInstance().create();
                recIntTemp.setField("YD_EQP_ID", szYD_EQP_ID);
                recIntTemp.setField("YD_EQP_STAT", "W");
                intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
                if(intRtnVal <= 0) {
                    if(intRtnVal == 0) {
                        szMsg="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]의 설비상태[YD_EQP_STAT]를 W로 변경 시 존재하지 않습니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    }else if(intRtnVal == -1) {
                        szMsg="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]의 설비상태[YD_EQP_STAT]를 W로 변경 시 중복됩니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    }else if(intRtnVal == -2) {
                        szMsg="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]의 설비상태[YD_EQP_STAT]를 W로 변경 시 파라미터 오류";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    }else if(intRtnVal == -3){
                        szMsg="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]의 설비상태[YD_EQP_STAT]를 W로 변경 시 오류발생";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    }
                }

                szMsg="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]의 설비상태[YD_EQP_STAT]를 W로 변경 완료 - 반환값 : " + intRtnVal;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                 *          후판제품L2 크레인작업실적응답 전송  - YDY4L005
                 * 업무기준 Desc : 후판제품L2에서 크레인 작업지시 요구 시 크레인 작업이 더 이상 존재하지 않을 경우 크레인작업실적응답 전송
                 * 일자 : 2009.07.01
                 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                recIntTemp = JDTORecordFactory.getInstance().create();
                if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) { //--2013.02.07 추가 (3기)
                    recIntTemp.setField("MSG_ID"        , "YDY8L005");
                    // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
                    if (isSendToEaiY9){
                        recIntTemp.setField("MSG_ID"        , "YDY9L005");
                    }
                } else {
                    recIntTemp.setField("MSG_ID"        , "YDY4L005");
                }
                recIntTemp.setField("YD_EQP_ID"     , szYD_EQP_ID);                     //야드설비ID
                recIntTemp.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_WO_DMD);    //야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
                recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK); //야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// recInPara 에 logId 추가
                recIntTemp.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

                ydDelegate.sendMsg(recIntTemp);
                szMsg = "["+szOperationName+"] 크레인 작업이 더 이상 존재하지 않을 경우 후판제품L2 크레인작업실적응답[YDY4L005] 전송 완료" ;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

                szMsg="["+szOperationName+"] 더이상의 크레인스케줄이 조회되지 않습니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                return intRtnVal = 0;
            }else if(intRtnVal < 0) {
                szMsg="["+szOperationName+"] 크레인 스케줄 조회중 Error";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return intRtnVal = -1;
            }

            //-------------------------------------------------------------------------------------------------------------
            //  다음스케줄의 작업상태를 체크하여 W이면 설비의 작업상태를 1로 변경, 아니면 변경하지 않음
            //  수정자 : 임춘수
            //  수정일 : 2009.12.21
            //-------------------------------------------------------------------------------------------------------------

            //-------------------------------------------------------------------------------------------------------------
            //  먼저 복사해서 아래부분에서 사라지는 문제 해결
            //-------------------------------------------------------------------------------------------------------------

            rsCrnSch.addAll(rsResult);

            //-------------------------------------------------------------------------------------------------------------

            rsResult.first();

            recIntTemp  = rsResult.getRecord();

            String szYD_WRK_PROG_STAT   = ydDaoUtils.paraRecChkNull(recIntTemp, "YD_WRK_PROG_STAT");

            szYD_CRN_SCH_ID             = ydDaoUtils.paraRecChkNull(recIntTemp, "YD_CRN_SCH_ID");

            szMsg="["+szOperationName+"] ------------- 조회된 크레인스케줄["+szYD_CRN_SCH_ID+"]의 야드작업진행상태["+szYD_WRK_PROG_STAT+"]에 따른 설비테이블 수정판단 ----------------";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            //-------------------------------------------------------------------------------------------------------------

            //다음 스케줄을 찾았을 경우

            if( szYD_WRK_PROG_STAT.equals("")
                    || szYD_WRK_PROG_STAT.equals(YdConstant.YD_EQP_STAT_IDLE)
                    || szYD_WRK_PROG_STAT.equals(YdConstant.YD_EQP_STAT_UP_WO) ) {

                szMsg="["+szOperationName+"] ------------- 조회된 크레인스케줄["+szYD_CRN_SCH_ID+"]의 야드작업진행상태["+szYD_WRK_PROG_STAT+"]에 따른 설비ID["+szYD_EQP_ID+"]의 설비상태를 1로 수정 시작";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                recIntTemp = JDTORecordFactory.getInstance().create();
                recIntTemp.setField("YD_EQP_ID",        szYD_EQP_ID);
                recIntTemp.setField("YD_EQP_STAT",      "1");
                recIntTemp.setField("YD_WORD_DT",       YdUtils.getCurDate("yyyyMMddHHmmss"));
                intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
                if(intRtnVal <= 0) {
                    if(intRtnVal == 0) {
                        szMsg=" Y4ChkWrkProgStatW updYdEqp : data not found";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.WARNING, logId);
                    }else if(intRtnVal == -1) {
                        szMsg=" Y4ChkWrkProgStatW updYdEqp : duplicate data,";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    }else if(intRtnVal == -2) {
                        szMsg=" Y4ChkWrkProgStatW updYdEqp : parameter error";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    }else if(intRtnVal == -3){
                        szMsg=" Y4ChkWrkProgStatW updYdEqp : execution failed";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    }
                    return intRtnVal = -1;
                }
            }else{
                szMsg="크레인의 스케줄["+szYD_CRN_SCH_ID+"]의 야드작업진행상태["+szYD_WRK_PROG_STAT+"]가 W가 아니므로 크레인설비의 상태를 변경하지 않음";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            }


            szMsg="["+szOperationName+"] ------------------------ 메소드 끝 ------------------------";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

        }catch(Exception e){

            szMsg="Error:" +e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return intRtnVal = -1;
        }


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
szMsg="크레인작업지시조회(W인 경우)("+szMethodName+") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        
        return intRtnVal = 1;
    } //end of Y4ChkWrkProgStatW()



    /**
     * 오퍼레이션명 : 크레인 작업지시(현재 진행중인 작업이 있을경우 해당작업)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public int Y4ChkWrkProgStat4(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws JDTOException  {
         YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();

        JDTORecordSet rsResult          = null;

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y4ChkWrkProgStat4";

        String szSchCd                  = "";
        String szQuery                  = "";


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= msgRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg="크레인 작업지시("+szMethodName+") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        try{
            //스케줄 코드 Check
            szSchCd = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
            if(szSchCd.equals("")) {
                szMsg="스케줄코드가 없습니다. : parameter error";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return intRtnVal = -1;
            }

            rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
            intRtnVal = ydCrnSchDao.getYdCrnsch(msgRecord, rsResult, 6);
            //조회된 크레인 스케줄이 없다면  전체에서 빠른 스케줄을 호출한다.
            if(intRtnVal <= 0) {
                rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
                intRtnVal = this.Y4ChkWrkProgStatW(msgRecord, rsResult);
                if(intRtnVal == -1) {
                    szMsg="크레인 작업 조회 중 Error!!";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    return intRtnVal = -1;
                }else if(intRtnVal == 0) {
                    szMsg="같은 스케줄 코드의 크레인 작업이 없습니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    return intRtnVal = 0;
                }

            }






            rsCrnSch.addAll(rsResult);

        }catch(Exception e){

            szMsg="Error:" +e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return intRtnVal = -1;
        }


        szMsg="크레인 작업지시("+szMethodName+") 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
        return intRtnVal = 1;
    } //end of Y4ChkWrkProgStat4()


    /**
     * 오퍼레이션명 : A후판 크레인권상실적등록 (Y4YDL008)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
     * @ejb.transaction type="RequiresNew"
     */
    public String procY4CrnLdWr(JDTORecord msgRecord)throws DAOException  {
        YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
        YdDelegate ydDelegate = new YdDelegate();
        YdEqpDao   ydEqpDao   = new YdEqpDao();
        YdCarSchDao ydCarSchDao = new YdCarSchDao();
        YdStkColDao ydStkColDao = new YdStkColDao();

        //업데이트 할 크레인 스케줄 Data 항목 set
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();

        //파라미터 null check 후 받아온 Data
        JDTORecord getParamRecord = JDTORecordFactory.getInstance().create();


        //스케줄Table의 컬럼을 저장하기위해 생성
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet  rsResult = null;
        //레코드 셋의 레코드값을 받음
        JDTORecord getRecord = JDTORecordFactory.getInstance().create();
        JDTORecord recInTemp = null;

        JDTORecord recSendMsg = null;

        int intRtnVal                   = 0 ;
        int intRtnVal1                  = 0 ;

        String szWbookId                = "";

        String szMsg="";
        String szMethodName="procY4CrnLdWr";

        String szTcarEqpId = "";
        //크레인스케줄ID
        String szYD_CRN_SCH_ID = "";
        //야드스케줄코드
        String szYD_SCH_CD = null;
        //권상실적위치
        String szYD_UP_WR_LOC = null;
        //설비ID(크레인설비ID)
        String szYD_EQP_ID = null;
        //야드To위치결정방법
        String szYD_TO_LOC_DCSN_MTD = null;
        //권하지시위치
        String szYD_DN_WO_LOC = null;
        //야드목표야드구분
        String szYD_AIM_YD_GP = null;

        //야드구분
        String szYdGp                   = ""; //--2013.02.07 추가 (3기)

        //상차포인트
        String szCARLD_PNT_CD   = ""; //--2014.06.30 추가(해송)
        // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
        boolean isSendToEaiY9 = false;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= msgRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8크레인권상실적 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null){
            szMsg=szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

            return YdConstant.RETN_CD_TC_ERROR;
        }
        if(bDebugFlag){
            szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
        }


        try{
            //=============================================================
            // 권오창
            // 2009.11.05
            //
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판제품야드] 크레인권상실적등록 수신";
            ydUtils.putLogMsg("K", YdConstant.YD_MONITORING_CHANNEL_K, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            //PIDEV
//          String sApplyYnPI = commPiDao.ApplyYnPI("", "크레인권상실적등록", "APPPI0", "T", "*");

            //파라미터 check
            intRtnVal = this.Y1ParamCheck(msgRecord, getParamRecord, 0) ;

            //크레인스케줄ID
            szYD_CRN_SCH_ID = getParamRecord.getFieldString("YD_CRN_SCH_ID");
            //야드스케줄코드
            szYD_SCH_CD = getParamRecord.getFieldString("YD_SCH_CD");
            //권상실적위치
            szYD_UP_WR_LOC = getParamRecord.getFieldString("YD_UP_WR_LOC");
            //설비ID(크레인설비ID)
            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(getParamRecord, "YD_EQP_ID");

            //야드구분을 설비ID 첫자리로 구분한다.
            if(szYD_EQP_ID.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD)) { //--2013.02.07 추가 (3기)
                szYdGp  = YdConstant.YD_GP_PLATE2_GDS_YARD; //2후판제품창고 'T'
            } else {
                szYdGp  = YdConstant.YD_GP_PLATE_GDS_YARD;  //1후판제품창고 'K'
            }


            setCrnschRecord.setField("YD_CRN_SCH_ID",         getParamRecord.getFieldString("YD_CRN_SCH_ID"));
            setCrnschRecord.setField("YD_SCH_CD",             getParamRecord.getFieldString("YD_SCH_CD")) ;
            setCrnschRecord.setField("YD_UP_WR_LOC",          getParamRecord.getFieldString("YD_UP_WR_LOC")) ;
            setCrnschRecord.setField("YD_UP_WR_LAYER",        getParamRecord.getFieldString("YD_UP_WR_LAYER")) ;

            //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
            intRtnVal = this.Y1UpdYdCrnsch(setCrnschRecord, 0) ;
            if(intRtnVal == -1) return YdConstant.RETN_CD_FAILURE;

            //Key Data Check!           키값은 Null이나 ""가 되어서는 안됨.
            if(setCrnschRecord.getFieldString("YD_CRN_SCH_ID").equals("") ||
               setCrnschRecord.getFieldString("YD_CRN_SCH_ID") == null) {
                szMsg = "'YD_CRN_SCH_ID' Data Error : 크레인스케줄ID가 없습니다." ;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                m_ctx.setRollbackOnly();
                throw new DAOException(szMsg);
            }

            //대상 데이터 SELECT
            intRtnVal = this.Y1GetYdCrnsch(setCrnschRecord, getRecSet,3);

            if( intRtnVal <= 0 ) {
                szMsg = "스케쥴 Data가 존재하지 않습니다. ="+ intRtnVal;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                m_ctx.setRollbackOnly();
                throw new DAOException(szMsg);
            }

            //레코드셋의 사이즈값으로 ErrorCheck
            if(getRecSet.size() == 0){
                szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                m_ctx.setRollbackOnly();
                throw new DAOException(szMsg);
            }

            getRecSet.first();
            getRecord = getRecSet.getRecord();

            //작업예약ID
            szWbookId           = ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID");
            //야드To위치결정방법
            szYD_TO_LOC_DCSN_MTD= ydDaoUtils.paraRecChkNull(getRecord, "YD_TO_LOC_DCSN_MTD");
            //권하지시위치
            szYD_DN_WO_LOC      = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC");
            //야드목표야드구분
            szYD_AIM_YD_GP      = ydDaoUtils.paraRecChkNull(getRecord, "YD_AIM_YD_GP");
            //#UT 북아웃시 재료정보 때문에 추가
            String jStlNo       = ydDaoUtils.paraRecChkNull(getRecord, "STL_NO");

            //조회한 크레인 스케줄의 작업활성상태가 check를 한다.
            if (getRecord.getFieldString("YD_WRK_PROG_STAT").equals("1")||
                getRecord.getFieldString("YD_WRK_PROG_STAT").equals("W")) {

                // 적치단 정보 Clear            (1개의 크레인스케줄에 잡혀있는 크레인작업재료의 정보를 모두 Check!)
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// Y4LdClearYdStklyr call 시  logId 항목 추가 개선
//              intRtnVal = this.Y4LdClearYdStklyr(getRecSet, 0);
                intRtnVal = this.Y4LdClearYdStklyr(getRecSet, 0, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

                //---------------------------------------------------------------------------
                //  일반야드의 권상베드에 선별출하송신완료와 운송지시번호가 존재하는 재료가 하나라도 존재하지 않으면
                //  베드의 야드적치Bed입출고상태[YD_STK_BED_WHIO_STAT]를 E로 입출고가능 상태로 변경
                //  수정일 : 2010.03.10 - 임춘수
                //---------------------------------------------------------------------------
                if( szYD_UP_WR_LOC.substring(2, 4).matches("\\d\\d") ) {

                    //-------------------------------------------------------------------------------------------------------
                    // 2010.04.22 윤재광 추가.
                    // 권상발생시 From위치에 (운송지시대기, 운송대기)인 제품이 없으면 Bed정보를 완산에서 입출고가능으로 변경
                    //-------------------------------------------------------------------------------------------------------

                    //------------------------------------------------------------------------------------------------------------
                    //  선별출하
                    //------------------------------------------------------------------------------------------------------------
                    JDTORecordSet   outResult   = JDTORecordFactory.getInstance().createRecordSet("");
                    JDTORecord      inRecord1   = JDTORecordFactory.getInstance().create();
                    JDTORecord      outRecord1      = JDTORecordFactory.getInstance().create();
                    String szAPPLY_YN           = "N";

                    if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) { //--2013.02.07 추가 (3기)
                        inRecord1.setField("REPR_CD_GP", "T00150");    //선별출하 적용
                    } else {
                        inRecord1.setField("REPR_CD_GP", "K00150");    //선별출하 적용
                    }

                    /*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
                    intRtnVal = ydEqpDao.getYdEqp(inRecord1, outResult, 999);
                    if(intRtnVal > 0) {
                        outResult.first();
                        outRecord1  = outResult.getRecord();
                        szAPPLY_YN = outRecord1.getFieldString("ITEM1");
                    }
                    szMsg="신선별출하 적용 " + szAPPLY_YN ;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    if(szAPPLY_YN.equals("Y")) {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// procChangeBedTypeForPlateGdsSL call 시  logId 항목 추가 개선
//                      this.procChangeBedTypeForPlateGdsSL(szYD_UP_WR_LOC,szMethodName);
                        this.procChangeBedTypeForPlateGdsSL(szYD_UP_WR_LOC,szMethodName, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

                    } else {
                        YdCommonUtils.procChangeBedTypeForPlateGds(szYD_UP_WR_LOC,szMethodName);
                    }

                    //-------------------------------------------------------------------------------------------------------
                    /*
                    szMsg = "일반야드의 권상실적베드["+szYD_UP_WR_LOC+"]에 선별출하송신완료와 운송지시번호가 존재하는 재료가 하나라도 존재하는 지 조회 시작";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                    recInTemp = JDTORecordFactory.getInstance().create();
                    recInTemp.setField("YD_STK_COL_GP", szYD_UP_WR_LOC.substring(0, 6));
                    recInTemp.setField("YD_STK_BED_NO", szYD_UP_WR_LOC.substring(6));

                    //com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSelCompTransOrd
                    String szRtnMsg         = DaoManager.getYdStklyr(recInTemp, rsResult, 96);

                    if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
                        szMsg = "권상베드["+szYD_UP_WR_LOC+"]에 선별출하송신완료와 운송지시번호가 존재하는 재료가 하나라도 존재하므로  베드의 야드적치Bed입출고상태를 수정하지 않음 - 대상재건수["+rsResult.size()+"]";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }else if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
                        szMsg = "선별출하송신완료와 운송지시번호가 존재하는 재료가 하나라도 존재하는 지 권상베드["+szYD_UP_WR_LOC+"] 조회 성공 - 대상재가 존재하지 않으므로 베드의 야드적치Bed입출고상태를 수정";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                        szMsg = "권상베드["+szYD_UP_WR_LOC+"]야드적치Bed입출고상태를 E로수정 시작";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                        recInTemp = JDTORecordFactory.getInstance().create();
                        recInTemp.setField("YD_STK_COL_GP",             szYD_UP_WR_LOC.substring(0, 6));
                        recInTemp.setField("YD_STK_BED_NO",             szYD_UP_WR_LOC.substring(6));
                        recInTemp.setField("YD_STK_BED_WHIO_STAT",      "E");
                        recInTemp.setField("MODIFIER",                  szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);

                        szRtnMsg                = DaoManager.updYdStkbed(recInTemp, 0);

                        szMsg = "권상베드["+szYD_UP_WR_LOC+"]야드적치Bed입출고상태를 E로수정 완료 - 메세지 : " + szRtnMsg;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                        //L2 로 적치열 수정된 정보를 내려보내준다.
                        recInTemp =  JDTORecordFactory.getInstance().create();
                        recInTemp.setField("MSG_ID",            "YDY4L001");
                        recInTemp.setField("YD_INFO_SYNC_CD",  "4");
                        recInTemp.setField("YD_GP",             YdConstant.YD_GP_PLATE_GDS_YARD);
                        recInTemp.setField("YD_STK_COL_GP",     szYD_UP_WR_LOC.substring(0, 6));
                        recInTemp.setField("YD_STK_BED_NO",     szYD_UP_WR_LOC.substring(6));

                        szMsg = "[Jsp-Session "+szSessionName+" ] L2 로 적치열 수정된 정보 송신 시작" ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                        ydDelegate.sendMsg(recInTemp);

                        szMsg = "[Jsp-Session "+szSessionName+" ] L2 로 적치열 수정된 정보 송신 완료" ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }
                    */
                }else{
                    szMsg = "권상실적베드["+szYD_UP_WR_LOC+"]가 일반야드가 아니므로 선별출하송신완료와 운송지시번호가 존재하는 재료가 하나라도 존재하는 지 조회하지 않음";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                }
                //---------------------------------------------------------------------------

                //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
                setCrnschRecord = JDTORecordFactory.getInstance().create();
                setCrnschRecord.setField("YD_CRN_SCH_ID",      getParamRecord.getFieldString("YD_CRN_SCH_ID"));
                setCrnschRecord.setField("YD_WRK_PROG_STAT",   getParamRecord.getFieldString("YD_WRK_PROG_STAT"));
                setCrnschRecord.setField("YD_EQP_ID",          getParamRecord.getFieldString("YD_EQP_ID"));
                setCrnschRecord.setField("YD_UP_WR_LOC",       getParamRecord.getFieldString("YD_UP_WR_LOC"));
                setCrnschRecord.setField("YD_UP_WR_LAYER",     getParamRecord.getFieldString("YD_UP_WR_LAYER"));
                setCrnschRecord.setField("YD_UP_WRK_ACT_GP",   getParamRecord.getFieldString("YD_UP_WRK_ACT_GP"));
                setCrnschRecord.setField("YD_UP_WR_XAXIS",     getParamRecord.getFieldString("YD_CRN_XAXIS"));
                setCrnschRecord.setField("YD_UP_WR_YAXIS",     getParamRecord.getFieldString("YD_CRN_YAXIS"));
                setCrnschRecord.setField("YD_UP_WR_ZAXIS",     getParamRecord.getFieldString("YD_CRN_ZAXIS"));
                //권상완료일시
                setCrnschRecord.setField("YD_UP_CMPL_DT",      YdUtils.getCurDate("yyyyMMddHHmmss"));

                intRtnVal = this.Y1UpdYdCrnsch(setCrnschRecord, 0);

                //설비Table의 상태 변경 (권하상태로 변경)
                setCrnschRecord = JDTORecordFactory.getInstance().create();
                setCrnschRecord.setField("YD_EQP_ID",          getParamRecord.getFieldString("YD_EQP_ID"));
                setCrnschRecord.setField("YD_EQP_STAT",        getParamRecord.getFieldString("YD_WRK_PROG_STAT"));

                intRtnVal = ydEqpDao.updYdEqp(setCrnschRecord, 0);
                if(intRtnVal <= 0) {
                    szMsg="설비상태 UPDATE 처리시 오류 발생.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                    m_ctx.setRollbackOnly();
                    throw new DAOException(szMsg);
                }

                //대차 및 차량 스케줄 이송재료 Handling
                if(ydDaoUtils.paraRecChkNull(getParamRecord,"YD_UP_WR_LOC").substring(2, 4).equals("PT")||
                   ydDaoUtils.paraRecChkNull(getParamRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TR")){

                    //--------------------------------------------------------------------
                    // 전사물류개선
                    //----------------------------------------------------------------------
                    String szYD_CAR_SCH_ID = "";
                    String szYD_CAR_USE_GP = "";
                    String szCARD_NO = "";
                    String szCAR_NO = "";

                    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                     *           하차개시 전문 송신 처리 - 구내운송
                     * 업무기준 Desc : 권상실적위치로 적치열에서 운송장비코드를 조회하고 그 코드로 차량스케줄을 조회하여 차량진행상태가
                     *              하차검수 or 하차도착이면 하차개시로 차량스케줄을 업데이트하고 전문을 전송한다.
                     * 기능 추가 : 임춘수
                     * 일자 : 2009.07.15
                     +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                    //권상실적위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
                    recInTemp = JDTORecordFactory.getInstance().create();
                    recInTemp.setField("YD_STK_COL_GP", szYD_UP_WR_LOC.substring(0, 6));
                    rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                    intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsResult, 0);
                    if( intRtnVal <= 0 ) {
                        szMsg = "[권상실적처리 - 하차개시]차량정지위치[" + szYD_UP_WR_LOC.substring(0, 6) + "] 정보가 존재하지 않습니다." ;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                    }else{
                        //조회된 차량정지위치에서 운송장비코드를 가져온다.
                        rsResult.first();
                        recInTemp = rsResult.getRecord();
                        //운송장비코드
                        String  szTRN_EQP_CD    = ydDaoUtils.paraRecChkNull(recInTemp, "TRN_EQP_CD");
                        szYD_CAR_USE_GP     = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");
                        szCAR_NO        = ydDaoUtils.paraRecChkNull(recInTemp, "CAR_NO");
                        szCARD_NO       = ydDaoUtils.paraRecChkNull(recInTemp, "CARD_NO");
                        //운송장비코드로 차량스케줄조회
                        recInTemp = JDTORecordFactory.getInstance().create();
                        recInTemp.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
                        recInTemp.setField("TRN_EQP_CD", szTRN_EQP_CD);
                        recInTemp.setField("CAR_NO", szCAR_NO);

                        //PIDEV
//                      if("N".equals(sApplyYnPI)) {
//                          recInTemp.setField("CARD_NO", szCARD_NO);
//                      }

                        rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                        /*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByTrnEqpCdCarNoCardNo_PIDEV*/
//PIDEV_S :병행가동용:PI_YD
                        recInTemp.setField("PI_YD",     szYdGp);
                        intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 27);
                        if( intRtnVal <= 0 ) {
                            szMsg = "[권상실적처리 - 하차개시]차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]에 해당하는 차량스케줄이 존재하지 않습니다." ;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                        }else{
                            //차량진행상태를 파악하여 하차검수이거나 하차도착일 때만 하차개시 전문을 송신한다.
                            rsResult.absolute(1);
                            recInTemp = rsResult.getRecord();
                            //차량스케줄ID
                            szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_SCH_ID");
                            //야드차량진행상태
                            String szYD_CAR_PROG_STAT = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_PROG_STAT");
                            //차량사용구분
                            szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");
                            //하차검수이거나 하차도착일 때 하차개시 전문 송신
                            if( szYD_CAR_PROG_STAT.equals("C") || szYD_CAR_PROG_STAT.equals("B") ) {
                                String szYD_CARUD_ST_DT = YdUtils.getCurDate("yyyyMMddHHmmss");
                                recInTemp = JDTORecordFactory.getInstance().create();
                                recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);                               //차량스케줄ID
                                recInTemp.setField("YD_CAR_PROG_STAT", "D");                                        //차량진행상태
                                recInTemp.setField("YD_CARUD_ST_DT", szYD_CARUD_ST_DT);                             //하차개시일시
                                intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
                                if(intRtnVal <= 0) {
                                    szMsg="[권상실적처리 - 하차개시]차량스케줄에 하차 개시 등록시 Error!! Code : " + intRtnVal;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                    m_ctx.setRollbackOnly();
                                    throw new DAOException(szMsg);
                                }

                                String szYD_GP          = szYD_SCH_CD.substring(0,1);
                                if( szYD_CAR_USE_GP.equals("L") ) {
                                    //하차작업개시 송신 YDTSJ009
                                    recInTemp = JDTORecordFactory.getInstance().create();
                                    recInTemp.setField("MSG_ID",        "YDTSJ009");
                                    recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
                                    recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
                                    recInTemp.setField("YD_GP",         szYD_GP);
                                    recInTemp.setField("YD_CARUD_ST_DT",         szYD_CARUD_ST_DT);
                                    ydDelegate.sendMsg(recInTemp);

                                    szMsg="[권상실적처리 - 하차개시]하차작업개시[YDTSJ009] 송신 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                }
                            }
                        }
                    }

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// WbookIdEndCheck call 시  logId 항목 추가 개선
//                  intRtnVal1 = this.WbookIdEndCheck(szWbookId,szYD_CRN_SCH_ID,ydDaoUtils.paraRecChkNull(getParamRecord,"YD_UP_WR_LOC").substring(0, 6));
                    intRtnVal1 = this.WbookIdEndCheck(szWbookId,szYD_CRN_SCH_ID,ydDaoUtils.paraRecChkNull(getParamRecord,"YD_UP_WR_LOC").substring(0, 6),logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

                    // 전사물류개선 출하신규로직일 경우
                    // 권상시 차량초기화 모듈을 호출한다.
                    if(PlateGdsYdUtil.isPlateNewMoudleApply("DM")){
                        //--------------------------------------------------------------------
                        // 전사물류개선 2021. 1. 6
                        //  - 권하시 차량진행관리를 이용한 차량초기화를 권상으로 이동조치함
                        //  - 현재 작업이 마지막 크레인스케쥴이면 차량진행관리 모듈 호출
                        //----------------------------------------------------------------------
                        JDTORecord params = JDTORecordFactory.getInstance().create();
                        JDTORecordSet rstLastCrnInfo = JDTORecordFactory.getInstance().createRecordSet("");
                        params.setField("YD_WBOOK_ID", szWbookId);
                        params.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
                        YdPlateCommDAO commDao      = new YdPlateCommDAO();
                        if(commDao.select(params, rstLastCrnInfo, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getWbookIdEndCheck") > 0){

                            szMsg="[권상실적처리] 차량의 마지막재료 차량스케쥴ID["+szYD_CRN_SCH_ID+"]";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                            JDTORecord callCarWrkStatCtr = JDTORecordFactory.getInstance().create();
                            callCarWrkStatCtr.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
                            callCarWrkStatCtr.setField("YD_WBOOK_ID",   szWbookId);
                            callCarWrkStatCtr.setField("CAR_LDUD_GP",   "L"); // 하차(야드에 Load)
                            callCarWrkStatCtr.setField("YD_UP_WR_LOC",  szYD_UP_WR_LOC);
            				
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// CraneUdHdSeEJBBean.java procY4CarWrkStatCtr Method call 시 callCarWrkStatCtr 에 logId 추가
                            callCarWrkStatCtr.setField("LOG_ID", logId);
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

                            // 차량진행관리 호출
                            EJBConnector ejbConn = new EJBConnector("default", "CraneUdHdSeEJB", this);
                            ejbConn.trx("procY4CarWrkStatCtr", new Class[] { JDTORecord.class }, new Object[] { callCarWrkStatCtr });

                            szMsg="["+ szMethodName +"] 하차작업완료 송신 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                        }

                    }
                    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

                    intRtnVal = this.YSCSetYdCar(getRecSet, 0) ;
                    szMsg = "권상시 차량이송재료 삭제 완료" ;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                }

                szMsg = "[권상실적처리] 상차개시 진입 전 - 권하지시위치[" + szYD_DN_WO_LOC + "], " + "설비구분[" + szYD_DN_WO_LOC.substring(2, 4) + "]";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                 *           상차개시 전문 송신 처리 - 구내운송, 출하관리
                 * 업무기준 Desc : 권하지시위치로 적치열에서 운송장비코드를 조회하고 그 코드로 차량스케줄을 조회하여 차량진행상태가
                 *              상차검수 or 상차도착이면 상차개시로 차량스케줄을 업데이트하고 전문을 전송한다.
                 * 기능 추가 : 임춘수
                 * 일자 : 2009.07.15
                 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

                String sCAR_KIND = "";
                //권하지시위치가 차상인 경우 상차개시 전문 송신 처리
                if( szYD_DN_WO_LOC.substring(2, 4).equals("PT") ||
                    szYD_DN_WO_LOC.substring(2, 4).equals("TR") ) {
                    //권하지시위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
                    recInTemp = JDTORecordFactory.getInstance().create();
                    recInTemp.setField("YD_STK_COL_GP", szYD_DN_WO_LOC.substring(0, 6));
                    rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                    intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsResult, 0);
                    if( intRtnVal <= 0 ) {
                        szMsg = "[권상실적처리 - 상차개시]차량정지위치[" + szYD_DN_WO_LOC.substring(0, 6) + "] 정보가 존재하지 않습니다." ;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    }else{
                        //조회된 차량정지위치에서 운송장비코드를 가져온다.
                        rsResult.first();
                        recInTemp = rsResult.getRecord();
                        //운송장비코드
                        String  szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(recInTemp, "TRN_EQP_CD");
                        String szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");
                        String szCAR_NO = ydDaoUtils.paraRecChkNull(recInTemp, "CAR_NO");
                        String szCARD_NO = ydDaoUtils.paraRecChkNull(recInTemp, "CARD_NO");
                        szMsg = "[권상실적처리 - 상차개시]차량정지위치[" + szYD_DN_WO_LOC.substring(0, 6) + "] 정보가 존재하므로 차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]로 차량스케줄 조회" ;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                        //운송장비코드로 차량스케줄조회
                        recInTemp = JDTORecordFactory.getInstance().create();
                        recInTemp.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
                        recInTemp.setField("TRN_EQP_CD", szTRN_EQP_CD);
                        recInTemp.setField("CAR_NO", szCAR_NO);
                        recInTemp.setField("CARD_NO", szCARD_NO);
                        rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//PIDEV_S :병행가동용:PI_YD
                        recInTemp.setField("PI_YD",     szYdGp);
                        intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 27);
                        if( intRtnVal <= 0 ) {
                            szMsg = "[권상실적처리 - 상차개시]차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]에 해당하는 차량스케줄이 존재하지 않습니다." ;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                        }else{
                            //차량진행상태를 파악하여 상차검수이거나 상차도착일 때만 상차개시 전문을 송신한다.
                            rsResult.absolute(1);
                            recInTemp = rsResult.getRecord();
                            //차량스케줄ID
                            String szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_SCH_ID");
                            //야드차량진행상태
                            String szYD_CAR_PROG_STAT = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_PROG_STAT");
                            //차량사용구분
                            szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");

                            szMsg = "[권상실적처리 - 상차개시]차량스케줄 조회 후 차량스케줄ID[" + szYD_CAR_SCH_ID + "], 야드차량진행상태[" + szYD_CAR_PROG_STAT + "], 차량사용구분[" + szYD_CAR_USE_GP + "]" ;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

//PI0003
                            sCAR_KIND = ydDaoUtils.paraRecChkNull(recInTemp, "CAR_KIND");
                            //상차검수이거나 상차도착일 때 상차개시 전문 송신
                            if( szYD_CAR_PROG_STAT.equals("3") || szYD_CAR_PROG_STAT.equals("2") ) {
                                /*
                                 * 야드목표야드구분은 작업예약에 등록된 목표야드를 사용한다.
                                 * 수정자 : 임춘수
                                 * 수정일 : 2009.11.03
                                 */
                                YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
                                JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
                                recInTemp = JDTORecordFactory.getInstance().create();
                                recInTemp.setField("YD_WBOOK_ID", szWbookId);
                                intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, outRecSet, 0);

                                if( intRtnVal > 0 ) {
                                    outRecSet.first();
                                    recInTemp = outRecSet.getRecord();
                                    szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recInTemp, "YD_AIM_YD_GP");
                                }
                                //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                                szMsg="[권상실적처리] 작업예약["+szWbookId+"]의 목표야드["+szYD_AIM_YD_GP+"]";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                                String szARR_WLOC_CD = "";

                                if( szYD_AIM_YD_GP.equals("")) {
                                    /*
                                     * 2010.05.07 윤재광
                                     * 아래의 소스가 필요한지 체크요망.
                                     */
                                    //준비스케줄이 존재하는 지 확인해서 준비스케줄의 착지개소코드를 가져와서 설정한다.
                                    szMsg="[권상실적처리] 작업예약["+szWbookId+"]에 목표야드["+szYD_AIM_YD_GP+"]가 존재하지 않으므로 준비스케줄을 조회 시작";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                                    outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
                                    String szRtnMsg = YdCommonUtils.getPreSch(recInTemp, outRecSet, 8);

                                    szMsg="[권상실적처리] 작업예약["+szWbookId+"]에 목표야드["+szYD_AIM_YD_GP+"]가 존재하지 않으므로 준비스케줄을 조회 완료 - 반환값 : " + szRtnMsg;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                                    if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
                                        outRecSet.first();
                                        recInTemp = outRecSet.getRecord();
                                        szARR_WLOC_CD = ydDaoUtils.paraRecChkNull(recInTemp, "ARR_WLOC_CD");
                                    }

                                }else{
                                    //야드구분에 따른 개소코드 반환
//                                  szARR_WLOC_CD = YdCommonUtils.getWlocCd(szYD_AIM_YD_GP);
                                    //장비코드로 구내운송에서 개소코드 가져 오기
                                    szARR_WLOC_CD = YdCommonUtils.getWlocCd2(szTRN_EQP_CD);
                                }
                                //차량스케줄 업데이트 - 상차개시
                                recInTemp = JDTORecordFactory.getInstance().create();
                                recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);                               //차량스케줄ID
                                recInTemp.setField("YD_CAR_PROG_STAT", "4");                                        //차량진행상태
                                recInTemp.setField("YD_EQP_WRK_STAT", "U");                                         //작업상태
                                recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szWbookId);                              //작업예약ID
                                recInTemp.setField("YD_CARLD_ST_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));         //상차개시일시
                                recInTemp.setField("MODIFIER",  szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName); //수정자
                                if( szYD_CAR_USE_GP.equals("L") ) {                 //구내운송
                                    if(!szYD_DN_WO_LOC.substring(0,1).equals(szYD_AIM_YD_GP)){
                                        recInTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD);                               //착지개소코드
                                    }
                                }
                                intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);

                                if(intRtnVal <= 0) {
                                    szMsg="[권상실적처리]차량스케줄에 상차개시일시 등록시 Error!! Code : " + intRtnVal;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                                    m_ctx.setRollbackOnly();
                                    throw new DAOException(szMsg);
                                }

                                recInTemp = JDTORecordFactory.getInstance().create();
                                if( szYD_CAR_USE_GP.equals("L") ) {                 //구내운송

                                    //상차작업개시 송신 YDTSJ007 (구내운송 상차개시)
                                    recInTemp.setField("MSG_ID",        "YDTSJ007");
                                    //착지개소코드
                                    recInTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD);

                                    szMsg="[권상실적처리]상차작업개시 송신 YDTSJ007 (구내운송 상차개시) 송신 시작";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                                }else if( szYD_CAR_USE_GP.equals("G") ){            //출하차량

                                    //상차작업개시 송신 YDDMR008 (후판출하상차개시)
                                    //PIDEV
//                                  if("Y".equals(sApplyYnPI)) {
                                        recInTemp.setField("MQ_TC_CD",      "M10YDLMJ1072");
                                        szMsg="[권상실적처리]상차작업개시 송신 M10YDLMJ1072 (후판출하상차개시) 송신 시작";
//                                  } else {
//                                      recInTemp.setField("MSG_ID",        "YDDMR008");
//                                      szMsg="[권상실적처리]상차작업개시 송신 YDDMR009 (후판출하상차개시) 송신 시작";
//                                  }

// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                                    //해송출하 이후 파라메터로 받은 권하실적위치를 가지고 상차포인트를 편집하여 사용한다.
                                    if(!"".equals(szYD_DN_WO_LOC)) {
                                        if(szYD_DN_WO_LOC.length()>=6) {
                                            szCARLD_PNT_CD = szYD_DN_WO_LOC.substring(0,1) + szYD_DN_WO_LOC.substring(4,5) + szYD_DN_WO_LOC.substring(1,2) + szYD_DN_WO_LOC.substring(5,6);
                                        }
                                    }
                                }

                                String szYD_GP          = szYD_SCH_CD.substring(0,1);

                                recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
                                recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
                                recInTemp.setField("YD_GP",         szYD_GP);
                                recInTemp.setField("CARLD_PNT_CD",  szCARLD_PNT_CD);

                                ydDelegate.sendMsg(recInTemp);

                                szMsg="[권상실적처리]상차작업개시 송신 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                            }
                        }
                    }

//                  intRtnVal1 = this.WbookIdEndCheck(szWbookId,szYD_CRN_SCH_ID,szYD_DN_WO_LOC.substring(0, 6));
//                  if( intRtnVal1 == 1 ) {
//                      szMsg="[권상실적처리] 입동지시 송신 완료 ";
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//                  }

                    String sPI0003 = commPiDao.ApplyYn(szSessionName, szMethodName, "PI0003","T","*"); //
                    if("Y".equals(sPI0003)) {
                        String sPI0011 = commPiDao.ApplyYn(szSessionName, szMethodName, "PI0011","T","*"); //
                        if("Y".equals(sPI0011)) {
                            //기존 로직
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// WbookIdEndCheck call 시  logId 항목 추가 개선
//                          intRtnVal1 = this.WbookIdEndCheck(szWbookId,szYD_CRN_SCH_ID,szYD_DN_WO_LOC.substring(0, 6));
                            intRtnVal1 = this.WbookIdEndCheck(szWbookId,szYD_CRN_SCH_ID,szYD_DN_WO_LOC.substring(0, 6),logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

                            if( intRtnVal1 == 1 ) {
                                szMsg="[권상실적처리] 입동지시 송신 완료.. ";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                            }
                        } else {
                            if(sCAR_KIND.startsWith("P")){
                                szMsg="[권상실적처리]  PALLET 처리 완료 CAR_KIND = " + sCAR_KIND;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                                intRtnVal1 = this.WbookIdEndCheckPT(sCAR_KIND, szWbookId,szYD_CRN_SCH_ID,szYD_DN_WO_LOC.substring(0, 6));
                                if( intRtnVal1 == 1 ) {

                                }
                            } else {
                                //기존 로직
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// WbookIdEndCheck call 시  logId 항목 추가 개선
//                              intRtnVal1 = this.WbookIdEndCheck(szWbookId,szYD_CRN_SCH_ID,szYD_DN_WO_LOC.substring(0, 6));
                                intRtnVal1 = this.WbookIdEndCheck(szWbookId,szYD_CRN_SCH_ID,szYD_DN_WO_LOC.substring(0, 6),logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

                                if( intRtnVal1 == 1 ) {
                                    szMsg="[권상실적처리] 입동지시 송신 완료. ";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                }
                            }
                        }
                    } else {
                        //기존 로직
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// WbookIdEndCheck call 시  logId 항목 추가 개선
//                      intRtnVal1 = this.WbookIdEndCheck(szWbookId,szYD_CRN_SCH_ID,szYD_DN_WO_LOC.substring(0, 6));
                        intRtnVal1 = this.WbookIdEndCheck(szWbookId,szYD_CRN_SCH_ID,szYD_DN_WO_LOC.substring(0, 6),logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

                        if( intRtnVal1 == 1 ) {
                            szMsg="[권상실적처리] 입동지시 송신 완료.. ";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                        }
                    }
                }

                /*
                 * 후판제품L2 크레인작업실적응답 전송  - YDY4L005 권상완료
                 * 2009.07.01
                 */
                szYD_EQP_ID = ydDaoUtils.paraRecChkNull(getParamRecord, "YD_EQP_ID");
                recInTemp = JDTORecordFactory.getInstance().create();
                // 2021. 1. 6 추가(Y9시스템 전송여부)
                isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szYD_EQP_ID);
                if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) { //--2013.02.07 추가 (3기)
                    recInTemp.setField("MSG_ID"          , "YDY8L005");
                     // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
                    if (isSendToEaiY9){
                        recInTemp.setField("MSG_ID"        , "YDY9L005");
                    }
                } else {
                    recInTemp.setField("MSG_ID"          , "YDY4L005");
                }

                recInTemp.setField("YD_EQP_ID"       , szYD_EQP_ID);                        //야드설비ID
                recInTemp.setField("YD_WRK_PROG_STAT", YdConstant.YD_EQP_STAT_UP_CMPL);     //야드작업진행상태
                recInTemp.setField("YD_SCH_CD"       , szYD_SCH_CD);                        //야드스케줄코드
                recInTemp.setField("YD_CRN_SCH_ID"   , szYD_CRN_SCH_ID);                    //야드크레인스케줄ID
                recInTemp.setField("YD_L2_WR_GP"     , YdConstant.CRN_WRK_RE_LD_WR);        //야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
                recInTemp.setField("YD_L3_HD_RS_CD"  , YdConstant.CRN_WRK_RE_CD_NORMAL_HD); //야드L3처리결과코드

////////////////////////////////////////////////////////////////////////////////////////
//2024.09.02 로그 개선  START
//recInPara 에 logId 추가
                recInTemp.setField("LOG_ID", logId);  // 전문에 있는 logId
//2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
                
                ydDelegate.sendMsg(recInTemp);
                szMsg = "[권상실적처리]후판제품L2 크레인작업실적응답[YDY4L005] 전송 완료" ;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                //입고작업 권상완료시, 입고작업 완료 전송  REQ202306467258
                    if(szYD_SCH_CD.substring(2,5).equals("RTR") && szYD_SCH_CD.substring(6,7).equals("L")){
                        JDTORecord recSchPara = null;
                        recSchPara=JDTORecordFactory.getInstance().create();
                        recSchPara.setField("MSG_ID", "YDY8L009");
                        recSchPara.setField("EQP_CD", szYD_EQP_ID);
                        recSchPara.setField("YD_BAY", szYD_SCH_CD.substring(1,2));
                        recSchPara.setField("RT", szYD_SCH_CD.substring(5,6));
                        recSchPara.setField("FLAG_YN", "N");  //Y:설정(입고작성 생성시)  N:해제(권상완료시)

////////////////////////////////////////////////////////////////////////////////////////
//2024.09.02 로그 개선  START
//recInPara 에 logId 추가
                        recSchPara.setField("LOG_ID", logId);  // 전문에 있는 logId
//2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
                        ydDelegate.sendMsg(recSchPara);
                    }

                /*
                 * 2015.09.09 윤재광
                 * #UT설비 북아웃시 조업L2 송신
                 */
                if("TCRTUT01".equals(szYD_UP_WR_LOC)||
                   "TCRTUT02".equals(szYD_UP_WR_LOC)){

                    JPlateYdDelegate jDelegate = new JPlateYdDelegate();

                    szMsg = "["+ szMethodName +"] RT BOOK-OUT 실적 전송 .. 시작";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    JDTORecord jrecPara = JDTORecordFactory.getInstance().create();
                    jrecPara.setField("MSG_ID",             "YDS1L005");                        // BOOK-IN 실적 전송
                    jrecPara.setField("STL_NO",             jStlNo);                            // 재료번호
                    jrecPara.setField("OPERATION_TYPE",     "2");                               // 1:Book In, 2:Book Out
                    jrecPara.setField("YD_STK_COL_GP",      szYD_UP_WR_LOC.substring(0, 6));    // FROM위치
                    jrecPara.setField("YD_STK_BED_NO",      szYD_UP_WR_LOC.substring(6));       // 야드적치BED번호

                    jDelegate.sendMsg(jrecPara);

                    szMsg = "["+ szMethodName +"] RT BOOK-OUT 실적 전송 .. 완료>>>>";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                }

            }else{
                szMsg =  "["+ szMethodName +"] YD_WRK_PROG_STAT data : '1' or 'w' not" ;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                m_ctx.setRollbackOnly();
                //throw new DAOException(szMsg);
                return YdConstant.RETN_CD_SUCCESS;
            }

            szMsg="권상 완료 실적 처리 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            //-- 해송 출하 가동 이후 권상실적 발생시 SP_YD_SHIPSEL_001 프로시져 호출 -------------------------------------
            if( szYD_UP_WR_LOC.substring(2, 4).matches("\\d\\d") ) {
                //후판해송출하적용여부가 'Y'이고 권상위치가 야드일경우 프로시져 호출

                YdPlateCommDAO  commDao       = new YdPlateCommDAO();

                Object[] inParam = {
                         "*"
                        ,szYD_UP_WR_LOC.substring(0,6)
                        ,szYD_UP_WR_LOC.substring(6,8)
                       };

                int[] inParamIndex = {1,2,3};

                JDTORecord record = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.callSpQueryId_0004");

                if(record == null || record.size() <= 0){
                    szMsg="[권상실적처리] SP_YD_SHIPSEL_001 프로시져 호출시  Error!! " ;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                }
            }
            //------------------------------------------------------------------------------------------------

            //------------------------------------------------------------------
            // 권상 실적시 Flex 실시간 처리
            //------------------------------------------------------------------
            JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
            if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) { //--2013.02.07 추가 (3기)
                recFlex.setField("YD_GP",  YdConstant.YD_GP_PLATE2_GDS_YARD);
            } else {
                recFlex.setField("YD_GP",  YdConstant.YD_GP_PLATE_GDS_YARD);
            }
            recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
            recFlex.setField("YD_UP_WR_LOC", szYD_UP_WR_LOC);
            szMsg="Flex 권상 완료 실적 전송";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            ydUtils.putYdFlexCrnWrk("", recFlex);
            //------------------------------------------------------------------



          // 전사물류개선 2021. 4. 7
          // L2 : 장치영이사, 조민주부장
          // 차량입고시 권상실적을 L2요청
            try{
                if( szYD_UP_WR_LOC.substring(2, 4).equals("PT")) {
                    JDTORecord sMsg = JDTORecordFactory.getInstance().create();
                    sMsg.setField("MSG_ID"        , "YDY9L010");
                    sMsg.setField("YD_CRN_SCH_ID"     , szYD_CRN_SCH_ID);
                    sMsg.setField("CRN_JOB_RST_TYPE"   , "U");          //U:권상실적,P:권하실적
                    ydDelegate.sendMsg(sMsg);
                }
            }catch(Exception ex){}

        }catch(Exception e) {
            try{
                isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szYD_EQP_ID);
                if(isSendToEaiY9){

                        JDTORecord sMsg = JDTORecordFactory.getInstance().create();
                        sMsg.setField("MSG_ID"        , "YDY9L005");
                        sMsg.setField("YD_EQP_ID"     , szYD_EQP_ID);
                        sMsg.setField("YD_L2_WR_GP"   , "M");           //U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경
                        sMsg.setField("YD_L3_HD_RS_CD", "7777");            //야드L3처리결과코드
                        sMsg.setField("YD_L3_MSG", "Error : 권상실적처리오류");         //야드L3처리결과코드
                        ydDelegate.sendMsg(sMsg);

                }
            }catch(Exception ex){}
            //System.out.println("Error :  "+ e.getLocalizedMessage());
            szMsg="Error :  "+ e.getLocalizedMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            m_ctx.setRollbackOnly();
            throw new DAOException(szMsg);
        }//end of try~catch


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
szMsg = "Y8크레인권상실적 처리(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        
        return YdConstant.RETN_CD_SUCCESS;
    }// end of procY4CrnLdWr()

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 오퍼레이션명 : 설비상태 체크
     *
     * @param   String szEqpId 설비ID
     * @return boolean true(설비사용가능), false(설비사용불가)
     * @throws JDTOException
     */
    public String eqpStatCheck(String szEqpId,JDTORecordSet rsResult)throws JDTOException  {
        //메세지
        String szMsg           = null;
        //메소드명
        String szMethodName    = "eqpStatCheck";
        //설비상태
        String szYD_EQP_STAT   = null;
        //야드설비작업Mode
        String szYD_EQP_WRK_MODE    = null;
        //레코드 선언
        JDTORecord recPara     = null;

        String szRtnMsg         = null;

        try {
            //레코드 생성
            recPara = JDTORecordFactory.getInstance().create();

            //설비ID를 작업크레인으로 설정
            recPara.setField("YD_EQP_ID", szEqpId);

            //설비 체크 및 데이터 조회
//          blnRtnVal = this.chkGetEqp(szEqpId, rsResult);
//          if (!blnRtnVal) return blnRtnVal;

            szRtnMsg        = DaoManager.getYdEqp(recPara, rsResult, 0);

            if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
                if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
                    return YdConstant.YD_EQP_NOTEXIST;
                }
            }

            //레코드 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //설비상태
            szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
            //야드설비작업Mode
            szYD_EQP_WRK_MODE = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_MODE");

            //야드설비작업Mode
            String szYD_EQP_ID = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID");

            szMsg = "설비ID(" + szYD_EQP_ID + ")의 (" + szYD_EQP_STAT + ") 입니다.";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);


            //크레인의 상태가 'T'이면 false 리턴.
            if (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {

                szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT + ") 입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                //blnRtnVal = false;

                return YdConstant.YD_EQP_STAT_BREAK;
            }else if (szYD_EQP_WRK_MODE.equals(YdConstant.YD_EQP_WRK_MODE_OFF_LINE)) {

                szMsg = "설비ID(" + szEqpId + ")의 상태가  OFF LINE(" + szYD_EQP_WRK_MODE + ")상태 입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                //blnRtnVal = false;

                return YdConstant.YD_EQP_WRK_MODE_OFF_LINE;
            } else {

                //blnRtnVal = true;
                return YdConstant.RETN_CD_SUCCESS;
            }
        } catch(Exception e) {
            szMsg = "설비상태 체크 중 예외발생! 예외메세지: " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return YdConstant.RETN_CD_FAILURE;
        }
        //return blnRtnVal;

    } //end of eqpStatCheck

    /**
     * 오퍼레이션명 : 설비 유무체크 및 조회결과 데이터 반환
     *
     * @param  String        szEqpId  설비ID
     *         JDTORecordSet rsResult 결과레코드셋
     * @return boolean       true(성공), false(실패)
     * @throws JDTOException
     */
    public boolean chkGetEqp(String szEqpId, JDTORecordSet rsResult)throws JDTOException  {

        //설비 DAO
        YdEqpDao ydEqpDao     = new YdEqpDao();
        //리턴값(boolean)
        boolean blnRtnVal     = false;
        //리턴값(int)
        int intRtnVal         = 0;
        //메소드명
        String szMethodName   = "chkGetEqp";
        String szMsg          = null;

        //레코드 선언
        JDTORecord recPara        = null;

        try {

            //레코드 생성
            recPara  = JDTORecordFactory.getInstance().create();

            //설비ID
            recPara.setField("YD_EQP_ID", szEqpId);

            //설비 테이블 조회
            intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult, 0);

            //리턴값 메세지처리
            if (intRtnVal > 1) {

                szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 중복되었습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if (intRtnVal == 1) {

                blnRtnVal = true;

            } else if (intRtnVal == 0) {

                szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if (intRtnVal == -2) {

                szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 parameter error 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else {

                szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 오류 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            }
        } catch(Exception e) {
            szMsg = "설비 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return blnRtnVal = false;
        }
        return blnRtnVal;
    } //end of chkGetEqp
    /**
     * 오퍼레이션명 : 권상작업 마지막 CHECK
     *
     * @param   String szEqpId 설비ID
     * @return boolean true(설비사용가능), false(설비사용불가)
     * @throws JDTOException
     */
    public int WbookIdEndCheck(String szYD_WBOOK_ID,String szYD_CRN_SCH_ID,String szYD_CAR_STOP_LOC, String logId)throws JDTOException  {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// WbookIdEndCheck argument 에 logId 항목 추가 개선
// public int WbookIdEndCheck(String szYD_WBOOK_ID,String szYD_CRN_SCH_ID,String szYD_CAR_STOP_LOC)throws JDTOException  {
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        YdCrnSchDao     ydCrnSchDao     = new YdCrnSchDao();
        YdCarSchDao     ydCarSchDao     = new YdCarSchDao();
        YdEqpDao    ydEqpDao    = new YdEqpDao();
        String szMsg            = null;
        String szMethodName     = "WbookEndCheck";
        JDTORecordSet rsResult  = null;
        JDTORecordSet outRecSet = null;

        JDTORecord recInTemp    = null;
        JDTORecord recLast      = null;
        JDTORecord inRecord     = null;
        JDTORecord outRecord    = null;

        JDTORecordSet   rsResultYN  = null; //FlagYn
        JDTORecord      recInParaYN = null; //FlagYn
        JDTORecord      recParaYN   = null; //FlagYn
        int             intRtnValYN = 0;    //FlagYn
        String          FlagYn      = "N";  //FlagYn
        YdPlateCommDAO  commDao         = new YdPlateCommDAO(); //FlagYn

        String szLST_CRN_SCH_ID = "";
        String szYD_CAR_USE_GP  = "";
        String szCAR_NO         = "";
        String szCARD_NO        = "";
        String szTRANS_ORD_DATE = "";
        String szTRANS_ORD_SEQNO= "";
        String szWLOC_CD        = "";
        String szYD_PNT_CD      = "";

        JDTORecord outRecTarget         = null;
        JDTORecordSet outRecSetTargetUp = null;
        JDTORecordSet outRecSetTarget   = null;
        JDTORecord inRecTargetUp        = null;
        JDTORecord outRecTargetUp       = null;
        JDTORecord inRecTarget          = null;
        JDTORecord inRec                = null;

        String szCAR_LOTID_TARGET       = "";
        String szYD_CAR_SCH_ID_TARGET   = "";
        String szTRANS_ORD_DATE_TARGET  = "";
        String szTRANS_ORD_SEQNO_TARGET = "";
        String szCAR_LOTID_UP           = "";
        String szCARSCH_UP              = "";
        String szYD_CAR_SCH_ID_FIX      = "N";
        String szYD_CARPNT_CD2          = "";
        String szYD_GP                  = null;
        String szYD_CAR_PROG_STAT       = null;
        String szCAR_KIND               = null;//입동지시 대상 차량 구분
        String szCURR_CAR_KIND          = null;//현재 작업중인 차량 구분 2023.11.07 추가

        boolean isSend                  = false;
        boolean isFirst                 = true;

        int intRtnVal           = 0;
        
        
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// logId Empty 이면 logId 신규 생성 개선
if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "권상작업 마지막 CHECK(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////


        try {



            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            recInTemp = JDTORecordFactory.getInstance().create();
            recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);

            /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getWbookIdEndCheck*/
            intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 505);
            if(intRtnVal <= 0) {
                szMsg="["+ szMethodName +"] 작업예약ID["+szYD_WBOOK_ID+"]로 크레인 스케줄을 조회 중 Error";

            } else {

                rsResult.last();
                recLast = JDTORecordFactory.getInstance().create();
                recLast.setRecord(rsResult.getRecord());
                szLST_CRN_SCH_ID    = ydDaoUtils.paraRecChkNull(recLast, "YD_CRN_SCH_ID");
                szYD_CAR_USE_GP     = ydDaoUtils.paraRecChkNull(recLast, "YD_CAR_USE_GP");
                szCURR_CAR_KIND     = ydDaoUtils.paraRecChkNull(recLast, "YD_CURR_CAR_KIND");

// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, "szLST_CRN_SCH_ID: "+szLST_CRN_SCH_ID+" szYD_CAR_USE_GP: "+szYD_CAR_USE_GP+" szCURR_CAR_KIND:"+szCURR_CAR_KIND, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, "szLST_CRN_SCH_ID: "+szLST_CRN_SCH_ID+" szYD_CAR_USE_GP: "+szYD_CAR_USE_GP+" szCURR_CAR_KIND:"+szCURR_CAR_KIND, YdConstant.DEBUG, logId);

                //야드구분
                szYD_GP = szYD_CAR_STOP_LOC.substring(0, 1);
                // PIDEV
//              String sApplyYnPI = commPiDao.ApplyYnPI("", "CraneLdHdSeEJBBean => WbookIdEndCheck", "APPPI0", szYD_GP, "*");
                if(szLST_CRN_SCH_ID.equals(szYD_CRN_SCH_ID)) {

                        //차량입동지시요구(YDYDJ633) 로직중 우선작업대상 검색로직  동일함
                        //-----------------------------------------------------------------------------------------------------------------------
                        //------------------------------------------------------------------------------------------------------------
                        //  신 상차처리 적용여부
                        //  권상시 입동 지시로
                        //------------------------------------------------------------------------------------------------------------
                        JDTORecordSet   outResult9  = JDTORecordFactory.getInstance().createRecordSet("");
                        JDTORecord      inRecord9   = JDTORecordFactory.getInstance().create();
                        JDTORecord      outRecord8  = JDTORecordFactory.getInstance().create();

                        String szAPPLY_YN9  = "";
                        inRecord9.setField("REPR_CD_GP", "T00171"); //2후판 제품창고야드 기준
                        inRecord9.setField("CD_GP", szYD_CAR_STOP_LOC.substring(4,5));  // 통로
                        inRecord9.setField("ITEM", szYD_CAR_STOP_LOC.substring(1,2));   // 동

                        /*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
                        intRtnVal = ydEqpDao.getYdEqp(inRecord9, outResult9, 999);
                        if(intRtnVal > 0) {
                            outResult9.first();
                            outRecord8  = outResult9.getRecord();
                            szAPPLY_YN9 = outRecord8.getFieldString("ITEM1");
                        }
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, "신 입동지시 처리 적용여부 " + szAPPLY_YN9, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, "신 입동지시 처리 적용여부 " + szAPPLY_YN9, YdConstant.DEBUG, logId);

                        outRecSetTarget = JDTORecordFactory.getInstance().createRecordSet("");
                        inRecord = JDTORecordFactory.getInstance().create();
                        inRecord.setField("YD_CAR_STOP_LOC", szYD_CAR_STOP_LOC);

                        if( szAPPLY_YN9.equals("S")) { //선별기준

                            /*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByBayInSeqPlate*/ //입동대기중인 차량작업 리스트
                            intRtnVal = ydCarSchDao.getYdCarsch(inRecord, outRecSetTarget, 406);

                            if( intRtnVal == 0 ) {
                                szMsg= "["+ szMethodName +"] 입동지시할 차량스케줄이 존재하지 않습니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                return 1;
                            }else if( intRtnVal < 0 ) {
                                szMsg= "["+ szMethodName +"] 입동지시순서 목록 조회 시 오류발생 : " + intRtnVal;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                return 1;
                            }

                            String szCAR_LOTID_FIRST = "";
                            String szMAX_CAR_LOTID   = "";

                            outRecTarget = JDTORecordFactory.getInstance().create();
                            inRecTargetUp = JDTORecordFactory.getInstance().create();

                            for(int i = 1; i <= outRecSetTarget.size(); i++) {

                                outRecSetTarget.absolute(i);
                                outRecTarget = outRecSetTarget.getRecord();

                                szCAR_LOTID_TARGET      = StringHelper.evl(outRecTarget.getFieldString("CAR_LOTID"), "");
                                szYD_CAR_SCH_ID_TARGET  = StringHelper.evl(outRecTarget.getFieldString("YD_CAR_SCH_ID"), "");
                                szTRANS_ORD_DATE_TARGET = StringHelper.evl(outRecTarget.getFieldString("TRANS_ORD_DATE"), "");
                                szTRANS_ORD_SEQNO_TARGET= StringHelper.evl(outRecTarget.getFieldString("TRANS_ORD_SEQNO"), "");
                                szYD_CAR_PROG_STAT      = StringHelper.evl(outRecTarget.getFieldString("YD_CAR_PROG_STAT"), "");
                                szCAR_KIND              = StringHelper.evl(outRecTarget.getFieldString("CAR_KIND"), "");
                                szCARD_NO               = StringHelper.evl(outRecTarget.getFieldString("CARD_NO"), "");

                                if(!"".equals(szCAR_LOTID_TARGET)&&
                                   szCAR_LOTID_FIRST.equals(szCAR_LOTID_TARGET)){
                                    /*
                                     * 다음 입동지시 대상(A)을 검색한 후 ,
                                     * 다음/다음 입동지시 대상(B)을 검색할때 다시 처음부터 for문을 돌린다.
                                     * 그때 다음입동지시 대상(A)은 제외한다.
                                     */
                                    szMsg= "["+ szMethodName +"] 윤재광후판입고테스트 중 : 현재차량[" + szCAR_NO + "]의 카드["+szCARD_NO+"]에 정보";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                                    continue;
                                }

                                // PIDEV
//                              if("Y".equals(sApplyYnPI)) {
                                    if(("A".equals(szYD_CAR_PROG_STAT) || szCAR_KIND.startsWith("P"))) {
                                        //1,2 후판이면서 입고차량인 경우는최상단 CAR LOTID 구하는 로직체크없이 입동대기 순서대로 입동시킨다. + 해송 PT 인 경우도 체크없이 입동 시킨다.

                                        szMsg= "["+ szMethodName +"] 입동대기순에 입고챠량스케줄 : ["+ szYD_CAR_SCH_ID_TARGET + "] 처리";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                                        szYD_CAR_SCH_ID_FIX = szYD_CAR_SCH_ID_TARGET;

                                    } else {
                                        outRecSetTargetUp = JDTORecordFactory.getInstance().createRecordSet("");

                                        inRecTargetUp.setField("YD_STOP_LOC"        , szYD_CAR_STOP_LOC);
                                        inRecTargetUp.setField("CAR_LOTID"          , szCAR_LOTID_TARGET);
                                        inRecTargetUp.setField("YD_CAR_SCH_ID"      , szYD_CAR_SCH_ID_TARGET);
                                        inRecTargetUp.setField("TRANS_ORD_DATE"     , szTRANS_ORD_DATE_TARGET);
                                        inRecTargetUp.setField("TRANS_ORD_SEQNO"    , szTRANS_ORD_SEQNO_TARGET);
                                        inRecTargetUp.setField("FIRST_CAR_LOT_ID"   , szCAR_LOTID_FIRST);

                                        // 최상단 CAR LOTID 구하는 로직
                                        /*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByBayInSeqPlateMax*/
                                        intRtnVal = ydCarSchDao.getYdCarsch(inRecTargetUp, outRecSetTargetUp, 410);

                                        if(intRtnVal <= 0){

                                            szMsg= "["+ szMethodName +"] 최상단 CAR LOT 이상 intRtnVal = 0.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                                            szYD_CAR_SCH_ID_FIX = szYD_CAR_SCH_ID_TARGET;

                                        }else{

                                            outRecSetTargetUp.first();
                                            outRecTargetUp  = outRecSetTargetUp.getRecord();
                                            szMAX_CAR_LOTID = StringHelper.evl(outRecTargetUp.getFieldString("MAX_CAR_LOTID"), "");

                                            szMsg = "["+szMethodName+"] szMAX_CAR_LOTID = " + szMAX_CAR_LOTID;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);


                                            if ("".equals(szMAX_CAR_LOTID)||                 // 최상단대상(현 LOT보다 SEQ가 작은거 또는 LOT정보 없슴)존재안함.
                                                szCAR_LOTID_TARGET.equals(szMAX_CAR_LOTID)){ // 최상단 == 대상 이면   대상을 선택

                                                szMsg= "["+ szMethodName +"] 대상이 최상단 LOTID 와 동일 .";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                                                szYD_CAR_SCH_ID_FIX = szYD_CAR_SCH_ID_TARGET;
                                            } else {
                                                szMsg= "["+ szMethodName +"] 대상이 최상단 LOTID 와 틀림 .";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                            }
                                        }
                                    }

//                              } else {    // !"Y".equals(sApplyYnPI)
//                                  if(("A".equals(szYD_CAR_PROG_STAT)||
//                                      szCARD_NO.startsWith("P"))) {
//                                      //1,2 후판이면서 입고차량인 경우는최상단 CAR LOTID 구하는 로직체크없이 입동대기 순서대로 입동시킨다. + 해송 PT 인 경우도 체크없이 입동 시킨다.
//
//                                      szMsg= "["+ szMethodName +"] 입동대기순에 입고챠량스케줄 : ["+ szYD_CAR_SCH_ID_TARGET + "] 처리";
//                                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//                                      szYD_CAR_SCH_ID_FIX = szYD_CAR_SCH_ID_TARGET;
//
//                                  } else {
//
//                                      outRecSetTargetUp = JDTORecordFactory.getInstance().createRecordSet("");
//
//                                      inRecTargetUp.setField("YD_STOP_LOC"        , szYD_CAR_STOP_LOC);
//                                      inRecTargetUp.setField("CAR_LOTID"          , szCAR_LOTID_TARGET);
//                                      inRecTargetUp.setField("YD_CAR_SCH_ID"      , szYD_CAR_SCH_ID_TARGET);
//                                      inRecTargetUp.setField("TRANS_ORD_DATE"     , szTRANS_ORD_DATE_TARGET);
//                                      inRecTargetUp.setField("TRANS_ORD_SEQNO"    , szTRANS_ORD_SEQNO_TARGET);
//                                      inRecTargetUp.setField("FIRST_CAR_LOT_ID"   , szCAR_LOTID_FIRST);
//
//                                      // 최상단 CAR LOTID 구하는 로직
//                                      /*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByBayInSeqPlateMax*/
//                                      intRtnVal = ydCarSchDao.getYdCarsch(inRecTargetUp, outRecSetTargetUp, 410);
//
//                                      if(intRtnVal <= 0){
//
//                                          szMsg= "["+ szMethodName +"] 최상단 CAR LOT 이상 intRtnVal = 0.";
//                                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//                                          szYD_CAR_SCH_ID_FIX = szYD_CAR_SCH_ID_TARGET;
//
//                                      }else{
//
//                                          outRecSetTargetUp.first();
//                                          outRecTargetUp  = outRecSetTargetUp.getRecord();
//                                          szMAX_CAR_LOTID = StringHelper.evl(outRecTargetUp.getFieldString("MAX_CAR_LOTID"), "");
//
//                                          szMsg = "["+szMethodName+"] szMAX_CAR_LOTID = " + szMAX_CAR_LOTID;
//                                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//
//                                          if ("".equals(szMAX_CAR_LOTID)||                 // 최상단대상(현 LOT보다 SEQ가 작은거 또는 LOT정보 없슴)존재안함.
//                                              szCAR_LOTID_TARGET.equals(szMAX_CAR_LOTID)){ // 최상단 == 대상 이면   대상을 선택
//
//                                              szMsg= "["+ szMethodName +"] 대상이 최상단 LOTID 와 동일 .";
//                                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//                                              szYD_CAR_SCH_ID_FIX = szYD_CAR_SCH_ID_TARGET;
//                                          } else {
//                                              szMsg= "["+ szMethodName +"] 대상이 최상단 LOTID 와 틀림 .";
//                                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//                                          }
//                                      }
//                                  }
//                              }

                                if (!szYD_CAR_SCH_ID_FIX.equals("N")){

                                    outRecSet   = JDTORecordFactory.getInstance().createRecordSet("");
                                    inRecTarget = JDTORecordFactory.getInstance().create();
                                    inRecTarget.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID_FIX);

                                    /*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByBayInSeqPlateTarget*/
                                    intRtnVal = ydCarSchDao.getYdCarsch(inRecTarget, outRecSet, 408);

                                    if( intRtnVal == 0 ) {
                                        szMsg= "["+ szMethodName +"] 입동지시할 차량스케줄이 존재하지 않습니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                                        return 1;
                                    }else if( intRtnVal < 0 ) {
                                        szMsg= "["+ szMethodName +"] 입동지시순서 목록 조회 시 오류발생 : " + intRtnVal;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                                        return 1;
                                    }

                                    szMsg= "["+ szMethodName +"] 기존입동지시순서 목록 조회 성공 - 대상재 건수 : " + intRtnVal;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                                    outRecSet.first();
                                    inRec = outRecSet.getRecord();

                                    if(isFirst){
                                        /*
                                         *  현재 다음차례로 들어와야될 차량인 경우
                                         */
                                        szYD_CAR_USE_GP     = StringHelper.evl(inRec.getFieldString("YD_CAR_USE_GP"), "");
                                        szCAR_NO            = StringHelper.evl(inRec.getFieldString("CAR_NO"), "");
                                        szCARD_NO           = StringHelper.evl(inRec.getFieldString("CARD_NO"), "");
                                        szTRANS_ORD_DATE    = StringHelper.evl(inRec.getFieldString("TRANS_ORD_DATE"), "");
                                        szTRANS_ORD_SEQNO   = StringHelper.evl(inRec.getFieldString("TRANS_ORD_SEQNO"), "");
                                        szWLOC_CD           = StringHelper.evl(inRec.getFieldString("WLOC_CD"), "");
                                        szYD_PNT_CD         = StringHelper.evl(inRec.getFieldString("YD_PNT_CD"), "");
                                        szYD_CAR_PROG_STAT  = StringHelper.evl(inRec.getFieldString("YD_CAR_PROG_STAT"), "");
                                        szCAR_KIND          = StringHelper.evl(inRec.getFieldString("CAR_KIND"), "");

                                        // 항목값 초기화
                                        isSend  = true;
                                        isFirst = false;
                                        szYD_CAR_SCH_ID_FIX = "N";

                                        // 다음/다음차량 입동대상 체크시 다음차량대상은 제외한다.
                                        szCAR_LOTID_FIRST   = szCAR_LOTID_TARGET;

                                        i = 1;

                                    }else{
                                        /*
                                         *  현재 다음/다음차례로 들어와야될 차량인 경우
                                         */
                                        //--2013.04.15 SMS메세지 통로구분을 A,B 에서 숫자로 변경 (현업요청)

                                        String sSmsMsg      = szYD_CAR_STOP_LOC.substring(1,2)+"동 "+szYD_CAR_STOP_LOC.substring(4,5)+"통로 입동대기차량입니다.\n 입동대기장소로 이동하세요.";
                                        String sFromTelNo   = "";

                                        szMsg= "["+ szMethodName +"] sSmsMsg="+sSmsMsg;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                                        String sTelNo = StringHelper.evl(inRec.getFieldString("TEL_NO"), "");
                                        String sCarNo = StringHelper.evl(inRec.getFieldString("CAR_NO"), "");

                                        // 알림톡 전환 FLAG
                                        rsResultYN = JDTORecordFactory.getInstance().createRecordSet("");
                                        recInParaYN = JDTORecordFactory.getInstance().create();
                                        intRtnValYN = commDao.select(recInParaYN, rsResultYN, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.TalkFlagYN");
                                        if(intRtnValYN <= 0) {
                                            szMsg = "["+szMethodName+"] 알림톡 전환 FLAG 조회 실패";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                        } else{
                                            szMsg = "["+szMethodName+"] 알림톡 전환 FLAG 조회 성공";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                        }
                                        //레코드 추출
                                        rsResultYN.first();
                                        recParaYN = rsResultYN.getRecord();
                                        FlagYn    = ydDaoUtils.paraRecChkNull(recParaYN, "FLAG_YN");

                                        if("Y".equals(FlagYn)){
                                            MessageSenderTalk    sender = new MessageSenderTalk();

                                            JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
//                                  System.out.println(1);
                                            recPara1.setField("PHONE_NUM", new String(sTelNo));
//                                  System.out.println(2);
                                            recPara1.setField("TMPL_CD", new String("CM1"));
//                                  System.out.println(3);
                                            recPara1.setField("SND_MSG", new String("[현대제철 공지사항]\n" + sSmsMsg));
//                                  System.out.println(4);
                                            recPara1.setField("SUBJECT", new String("입동지시 알림"));
//                                  System.out.println(5);
                                            recPara1.setField("SMS_SND_NUM", new String("0416801592"));
//                                  System.out.println(6);
                                            recPara1.setField("RECV_ID","1522110");
//                                  System.out.println(7);
                                            recPara1.setField("GROUP_ID","KaKao");
//                                  System.out.println(8);
                                            recPara1.setField("PROGRAM_ID","udttalk");
//                                  System.out.println(9);

                                            if(sTelNo.length() > 8 ) {
//                                  System.out.println(10);
                                                sender.sendTalk(recPara1);  // 알림톡 송신
//                                  System.out.println(21);
                                                szMsg = "["+szMethodName+"] 신규 알림톡 송신";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                            } else{
                                                szMsg = "["+szMethodName+"] 전화번호 자릿수 8자리 미만["+ sTelNo.length() + "] 신규 알림톡 송신 안함";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                            }
                                        } else{

                                            JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
                                            recPara1.setField("FROM_PHONE_NO", sFromTelNo);
                                            recPara1.setField("TO_PHONE_NO"  , sTelNo);
                                            recPara1.setField("TO_CONTENT"   , sSmsMsg);

                                            if(sTelNo.length() > 8 ) {
                                                PlateGdsYdUtil.updSmsMsgSend(recPara1); // SMS 송신
                                                szMsg = "["+szMethodName+"] 기존 SMS 송신";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                            } else{
                                                szMsg = "["+szMethodName+"] 전화번호 자릿수 8자리 미만["+ sTelNo.length() + "] 기존 알림톡 송신 안함";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                            }
                                        }

                                            szMsg= "["+ szMethodName +"] 입동지시차량[" + szCAR_NO + "]의 다음차량["+sCarNo+"/"+sTelNo+"]에 대한 입동대기지시";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                                            i = outRecSetTarget.size() + 1;
                                    }
                                }
                            } //FOR
                        }else{
                            outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
                            /* com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdCarSchByInSeqCheck */
                            intRtnVal = ydCarSchDao.getYdCarsch(inRecord, outRecSet, 310);

                            if( intRtnVal == 0 ) {
                                szMsg= "["+ szMethodName +"] 입동지시할 차량스케줄이 존재하지 않습니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                return 1;
                            }else if( intRtnVal < 0 ) {
                                szMsg= "["+ szMethodName +"] 입동지시순서 목록 조회 시 오류발생 : " + intRtnVal;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                return 1;
                            }

                            outRecSet.first();
                            inRecord = outRecSet.getRecord();

                            szYD_CAR_USE_GP     = StringHelper.evl(inRecord.getFieldString("YD_CAR_USE_GP"), "");
                            szCAR_NO            = StringHelper.evl(inRecord.getFieldString("CAR_NO"), "");
                            szCARD_NO           = StringHelper.evl(inRecord.getFieldString("CARD_NO"), "");
                            szTRANS_ORD_DATE    = StringHelper.evl(inRecord.getFieldString("TRANS_ORD_DATE"), "");
                            szTRANS_ORD_SEQNO   = StringHelper.evl(inRecord.getFieldString("TRANS_ORD_SEQNO"), "");
                            szCAR_KIND          = StringHelper.evl(inRecord.getFieldString("CAR_KIND"), "");

                            szWLOC_CD           = StringHelper.evl(inRecord.getFieldString("WLOC_CD"), "");
                            szYD_PNT_CD         = StringHelper.evl(inRecord.getFieldString("YD_PNT_CD"), "");
                            szYD_CARPNT_CD2     = StringHelper.evl(inRecord.getFieldString("YD_CARPNT_CD"), "");
                            isSend = true;
                        }

                        if( szYD_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM)&&isSend){

                            outRecord = JDTORecordFactory.getInstance().create();

                            // PIDEV
//                          if ("Y".equals(sApplyYnPI)) {
                                String sPI0011 = commPiDao.ApplyYn(szSessionName, szMethodName, "PI0011","T","*"); //

                                if("Y".equals(sPI0011)) {

                                    szMsg="PI 입동지시 송신 안함" ;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);


                                    //마지막 재료 권상완료시, 선입동지시(신규전문) 전송  2023.05.15 박종호. 임진후 사원 요청
                                        if("A".equals(szYD_CAR_PROG_STAT) || szCAR_KIND.startsWith("P") ) {  //들어와야할 차량이 PT이면 선입동지시 송신X
                                            //1,2후판 이면서 입고차량인 경우 출하로 입동지시전문(YDDMR028)을 전송하지 않는다. +
                                            szMsg="입고차량 입동지시 송신 안함" ;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                                        } else {
                                            boolean isSend1162=true;

                                            if(szCURR_CAR_KIND.equals("PT")){  //현차량이 PT일경우 선입동지시 송신X(PT는 장착작업까지 해야하므로) 2023.11.07
                                                isSend1162=false;
                                                szMsg="현차량이 PT이므로 입동선지시 송신안함" ;
                                                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                            }

                                            if(isSend1162)
                                            {
                                                String sCurrDate = YdUtils.getCurDate("yyyyMMddHHmmss");

                                                outRecord.setField("MQ_TC_CD"             , "M10YDLMJ1162"      );
                                                outRecord.setField("MQ_TC_CREATE_DDTT"    , sCurrDate           ); //JMSTC생성일시
                                                outRecord.setField("TRANS_WORD_DATE"      , szTRANS_ORD_DATE    ); // 운송지시일자
                                                outRecord.setField("TRANS_WORD_SEQNO"     , szTRANS_ORD_SEQNO   ); // 운송의뢰순번

                                                outRecord.setField("CAR_NO"               , szCAR_NO            ); // 차량번호
                                                outRecord.setField("YD_GP"                , "T"                 ); // 야드구분
                                                outRecord.setField("DIST_GOODS_GP"        , "P"                 ); // 출하제품구분
                                                outRecord.setField("SCH_YN"               , "N"                 ); // 스케줄 여부
                                                outRecord.setField("BAYIN_DDTT"           , sCurrDate           ); // 입동일시
                                                outRecord.setField("WLOC_CD"              , szWLOC_CD           ); // 개소코드
                                                outRecord.setField("YD_PNT_CD"            , szYD_PNT_CD         ); // 야드포인트코드
                                                outRecord.setField("LOAN_PULLOUT_ABLE_YN" , "Y"                 ); // 차입인출가능여부
                                                outRecord.setField("YD_CARPNT_CD"         , szYD_CARPNT_CD2     );

                                                
                                                //2025.07.31 임진후기사 요청 RITM1277126
                                                //선입동차량은 2순위로 순위 조정
                                                String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szMethodName, "APP060", "T", "020");//후판 개발 적용여부
                                                
                                                if("Y".equals(sApplyYnPI)){
                                                	outRecord.setField("YD_BAYIN_WO_SEQ", "2");
                                                	outRecord.setField("TRANS_ORD_DATE", szTRANS_ORD_DATE);
                                                	outRecord.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
                                                	intRtnVal = commDao.update(outRecord,"com.inisteel.cim.yd.common.dao.YdPlateCommDAO.upYdCrnSchBayInSeq");
                                                    if(intRtnVal >0){
                                                    	szMsg="차량번호 ["+szCAR_NO+"] 지시일자 ["+szTRANS_ORD_DATE+"] 지시순번 ["+szTRANS_ORD_SEQNO+"] 입동순서 변경완료" ;
                                                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                                    }
                                                }
                   
                                                ydDelegate.sendMsg(outRecord);

                                            }
                                        }
                                } else {
                                    if("A".equals(szYD_CAR_PROG_STAT) || szCAR_KIND.startsWith("P") ) {
                                        //1,2후판 이면서 입고차량인 경우 출하로 입동지시전문(YDDMR028)을 전송하지 않는다. +
                                        szMsg="입고차량 입동지시 송신 안함" ;
                                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                                    } else {
                                        String sCurrDate = YdUtils.getCurDate("yyyyMMddHHmmss");

                                        outRecord.setField("MQ_TC_CD"             , "M10YDLMJ1062"      );
                                        outRecord.setField("MQ_TC_CREATE_DDTT"    , sCurrDate           ); //JMSTC생성일시
                                        outRecord.setField("TRANS_WORD_DATE"      , szTRANS_ORD_DATE    ); // 운송지시일자
                                        outRecord.setField("TRANS_WORD_SEQNO"     , szTRANS_ORD_SEQNO   ); // 운송의뢰순번

                                        outRecord.setField("CAR_NO"               , szCAR_NO            ); // 차량번호
                                        outRecord.setField("YD_GP"                , "T"                 ); // 야드구분
                                        outRecord.setField("DIST_GOODS_GP"        , "P"                 ); // 출하제품구분
                                        outRecord.setField("SCH_YN"               , "N"                 ); // 스케줄 여부
                                        outRecord.setField("BAYIN_DDTT"           , sCurrDate           ); // 입동일시
                                        outRecord.setField("WLOC_CD"              , szWLOC_CD           ); // 개소코드
                                        outRecord.setField("YD_PNT_CD"            , szYD_PNT_CD         ); // 야드포인트코드
                                        outRecord.setField("LOAN_PULLOUT_ABLE_YN" , "Y"                 ); // 차입인출가능여부
                                        outRecord.setField("YD_CARPNT_CD"         , szYD_CARPNT_CD2     );

                                        /*
                                         * 입동지시 대상차량에 입동지시 송신여부 셋팅
                                         */
                                        outRecord.setField("YD_CAR_RCPT_CHK_YN"   ,"Y");
                                        /* com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdCarschYdCarRcptChkYn_PIDEV
                                        UPDATE TB_YD_CARSCH
                                           SET YD_CAR_RCPT_CHK_YN = :YD_CAR_RCPT_CHK_YN
                                               , YD_BAYIN_WO_SEQ  = '1'
                                         WHERE CAR_NO             = :CAR_NO
                                           AND TRANS_ORD_DATE     = :TRANS_ORD_DATE
                                           AND TRANS_ORD_SEQNO    = :TRANS_ORD_SEQNO
                                        */
                                        intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId_PIDEV(outRecord, 305);
                                        if(intRtnVal <= 0) {
                                            szMsg="차량스케줄에 입동지시 송신여부 셋팅 Error!! Code : " + intRtnVal;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                        }

                                        ydDelegate.sendMsg(outRecord);
                                    }
                                }
//                          } else {    //!"Y".equals(sApplyYnPI)
//                              if("A".equals(szYD_CAR_PROG_STAT) || szCARD_NO.startsWith("P") ) {
//                                  //1,2후판 이면서 입고차량인 경우 출하로 입동지시전문(YDDMR028)을 전송하지 않는다. +
//                                  szMsg="입고차량 입동지시 송신 안함" ;
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//                              } else {
//                                  szMsg= "["+ szMethodName +"] 입동순서가 가장빠른 차량이 출하차량[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + ", 운송지시일자:" + szTRANS_ORD_DATE + ", 운송지시순번:" + szTRANS_ORD_SEQNO + "]이므로 입동지시 전문을 전송한다.";
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//                                  outRecord.setField("TC_CODE"                ,"YDDMR028");
//                                  outRecord.setField("TRANS_WORD_DATE"        ,szTRANS_ORD_DATE);
//                                  outRecord.setField("TRANS_WORD_SEQNO"       ,szTRANS_ORD_SEQNO);
//                                  outRecord.setField("CARD_NO"                ,szCARD_NO);
//                                  outRecord.setField("CAR_NO"                 ,szCAR_NO);
//                                  outRecord.setField("WLOC_CD"                ,szWLOC_CD);
//                                  outRecord.setField("YD_PNT_CD"              ,szYD_PNT_CD);
//                                  outRecord.setField("YD_CARPNT_CD"           ,szYD_CARPNT_CD2);
//                                  outRecord.setField("LOAN_PULLOUT_ABLE_YN"   ,"Y");
//
//                                  /*
//                                   * 입동지시 대상차량에 입동지시 송신여부 셋팅
//                                   * 2015.04.09 윤재광 - 차량도착처리 시점에 입동지시송신이 안된 차량이면 아래전문 재송신
//                                   */
//                                  outRecord.setField("YD_CAR_RCPT_CHK_YN"   ,"Y");
//
//                                  intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(outRecord, 305);
//                                  if(intRtnVal <= 0) {
//                                      szMsg="차량스케줄에 입동지시 송신여부 셋팅 Error!! Code : " + intRtnVal;
//                                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//                                  }
//
//                                  ydDelegate.sendMsg(outRecord);
//                              }
//                          }
                        }
                }
            }
        } catch(Exception e) {
            szMsg = "권상작업 마지막 CHECK 중 예외발생! 예외메세지: " + e.getMessage();
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return 0;
        }
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
szMsg = "권상작업 마지막 CHECK(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        return 1;

    } //end of WbookEndCheck

    /**
     * 후판창고 BED 타입변경 처리 : 후판제품용선별
     * @param sYdLocation
     * @return
     * @throws DAOException
     */
    public String procChangeBedTypeForPlateGdsSL(String sYdLocation,
                                                        String szMethodName, String logId) throws DAOException {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// procChangeBedTypeForPlateGdsSL argument 에 logId 항목 추가 개선
// public String procChangeBedTypeForPlateGdsSL(String sYdLocation,
//                                                      String szMethodName) throws DAOException {
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        int intRtnVal           = 0;
        int intMvCnt            = 0;
        //메세지
        String szMsg            = "";
        String szSessionName    = "BED TYPE 변경";

        JDTORecord recInTemp    = null;
        JDTORecord recOutTemp   = null;
        JDTORecord recSndTemp   = null;

        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

        YdStkBedDao    ydStkBedDao    = new YdStkBedDao();
        YdStkLyrDao    ydStkLyrDao    = new YdStkLyrDao();
        YdEqpDao       ydEqpDao       = new YdEqpDao();

        JDTORecord recInPara = null;
        JDTORecordSet rsChkBed = null;
        YdPlateCommDAO  commDao       = new YdPlateCommDAO();

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// logId Empty 이면 logId 신규 생성 개선
if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "후판창고 BED 타입변경 처리 : 후판제품용선별(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        try{
            szMsg = "[" + szSessionName + "] 저장위치 : " + sYdLocation;

            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            if(sYdLocation.length() != 8){
                return YdConstant.RETN_CD_FAILURE;
            }
            String szYD_STK_COL_GP = sYdLocation.substring(0, 6);
            String szYD_STK_BED_NO = sYdLocation.substring(6, 8);

            recInTemp = JDTORecordFactory.getInstance().create();

            recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
            recInTemp.setField("YD_STK_BED_NO", szYD_STK_BED_NO);

            getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
            /*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBedAll*/
            intRtnVal = ydStkBedDao.getYdStkbed(recInTemp, getRecSet, 313);

            if (intRtnVal <= 0) {
                szMsg = "[" + szSessionName + "] BED정보 조회중 Error!! Code : " + intRtnVal;
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                throw new JDTOException("<procY4CrnUdWr> getYdStkbed :" + szMsg);
            }

            getRecSet.first();
            recOutTemp = JDTORecordFactory.getInstance().create();
            recOutTemp.setRecord(getRecSet.getRecord());

            String szYD_STK_BED_WHIO_STAT   = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_BED_WHIO_STAT");
            String szYD_STK_BED_SEL_GP      = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_BED_SEL_GP");

            szMsg = "[szYD_STK_BED_WHIO_STAT]:"+szYD_STK_BED_WHIO_STAT;
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[szYD_STK_BED_SEL_GP]:"+szYD_STK_BED_SEL_GP;
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            //25.08.27 임진후기사 요청. 출하제품 없을때 완산베드 -> 일반베드 바꾸는것이 굳이 선별상태 출하송신에 한정지을 필요가 없다
            //출하송신 시에만 확인하던 로직 수정 
            //if(szYD_STK_BED_SEL_GP.equals("S")) {    // 출하송신

                getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
                // 출하LOTID COUNT
                /* com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getStlMoveSlCnt */
                intRtnVal = ydStkLyrDao.getYdStklyr(recInTemp, getRecSet, 618);

                if (intRtnVal < 0) {
                    szMsg = "[" + szSessionName + "] 선별LOT 편성 재료수 조회중 Error!! Code : " + intRtnVal;
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                    throw new JDTOException("[" + szSessionName + "] getYdStklyr :" + szMsg);
                }

                getRecSet.first();
                recOutTemp = JDTORecordFactory.getInstance().create();
                recOutTemp.setRecord(getRecSet.getRecord());

                intMvCnt = ydDaoUtils.paraRecChkNullInt(recOutTemp, "MV_CNT");
                szMsg = "[intMvCnt]:"+intMvCnt;
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                if(intMvCnt == 0) {  //MV_CNT: 해당 베드의 CAR_LOT ID가 있는 재료의 개수

                    recInTemp.setField("YD_STK_BED_SEL_GP",       "S");

                    if(szYD_STK_BED_WHIO_STAT.equals(YdConstant.YD_STK_BED_WHIO_FULL)){  //현재 완산베드이면 일반베드로 변경. NEXT_YD_STK_BED_SEL_GP이건 뭔지 확인필요.

                        szMsg = "szYD_STK_COL_GP:"+szYD_STK_COL_GP+" szYD_STK_BED_NO:"+szYD_STK_BED_NO+" 베드의 재료 정보 조회(운송지시대기 존재 여부 확인)";
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                        getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
                        //intRtnVal = ydStkBedDao.getYdStkbed(recInTemp, getRecSet, 313);
                        recInPara = JDTORecordFactory.getInstance().create();
                        recInPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
                        recInPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
                        recInPara.setField("PROG_CD", "N");  //운송지시대기
                        rsChkBed = JDTORecordFactory.getInstance().createRecordSet("temp"); //2022.01.24 제품출하팀 길선배주임 요청사항: 해당 베드의 잔여 재료중 운송지시대기인 재료가 1건이라도 있으면 완산베드를 일반베드가 아닌 입출고불가베드로 변경
                        intRtnVal = commDao.select(recInPara, rsChkBed, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0141");
                         /*com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0141*/
                         // SELECT PLATE_NO
                        //  FROM TB_PT_PLATECOMM A
                        //  WHERE PLATE_NO IN
                        //  (
                        //  SELECT STL_NO
                        //  FROM TB_YD_STKLYR tys
                        //  WHERE YD_STK_COL_GP =:V_YD_STK_COL_GP
                        //  AND YD_STK_BED_NO =:V_YD_STK_BED_NO
                        //  AND STL_NO IS NOT NULL
                        //  )
                        //  AND CURR_PROG_CD =:V_PROG_CD
                        szMsg = "[intRtnVal]:"+intRtnVal;
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                        if(intRtnVal > 0) {  //운송지시대기인 재료가 1건이상 있으므로, 베드상태를 일반베드가 아닌 입출고불가베드로 셋팅.
                            recInTemp.setField("YD_STK_BED_WHIO_STAT",  "X");
                        }
                        else if(intRtnVal == 0){  //운송지시대기인 재료가 없으므로 일반베드로 셋팅.
                            recInTemp.setField("YD_STK_BED_WHIO_STAT",  "E");
                        }
                        else{  //intRtnVal<0
                            szMsg = "[" + szSessionName + "] 베드내 잔여 재료의 진도코드 조회 중 Error!! Code : " + intRtnVal;
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                        }
                        //recInTemp.setField("YD_STK_BED_WHIO_STAT",  "E");  // 이부분을 E와 X로 분기되도록 처리 필요.(해당 베드의 잔여 재료중, 진도코드가 운송지시대기가 하나라도 있다면 입출고불가베드)
                        recInTemp.setField("NEXT_YD_STK_BED_SEL_GP","E");  //베드 선별상태구분?값인것같은데, 기존 입출고불가베드도 전부 E인것으로 봐서 별도 변경 필요없는듯.
                    } else {
                        /*
                         * 출하가적베드인지 아닌지를 체크한다.
                         * 2012.04.27 윤재광
                         */
                        boolean         isGajuk         = false;
                        JDTORecord      recSubPara      = null;
                        JDTORecord      inRecord        = JDTORecordFactory.getInstance().create();
                        JDTORecordSet   outResultSet    = JDTORecordFactory.getInstance().createRecordSet("");

                        /*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB601*/
                        intRtnVal = ydEqpDao.getYdEqp(inRecord, outResultSet, 601);
                        if(intRtnVal > 0) {
                            for(int i = 1; i <= outResultSet.size(); i++ ) {
                                recSubPara = JDTORecordFactory.getInstance().create();

                                outResultSet.absolute(i);
                                recSubPara = outResultSet.getRecord();

                                if(szYD_STK_COL_GP.equals(recSubPara.getFieldString("YD_STK_COL_GP"))){

                                    isGajuk = true;
                                    break;
                                }
                            }
                        }
                        /*
                         *  /yd/plateGdsYd/plateYdSelList.jsp에도 아래로직 존재
                         *  변경시에 JSP도 같이 변경요.
                         */
                        if(isGajuk){
                            recInTemp.setField("YD_STK_BED_WHIO_STAT",  "G");
                            recInTemp.setField("NEXT_YD_STK_BED_SEL_GP","F");
                        }else{
                            recInTemp.setField("YD_STK_BED_WHIO_STAT",  "E");
                            recInTemp.setField("NEXT_YD_STK_BED_SEL_GP","E");
                        }
                    }

                    if(szYD_STK_COL_GP.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD)) {
                        recInTemp.setField("YD_USER_ID",              "Y8YDL008");
                    } else {
                        recInTemp.setField("YD_USER_ID",              "Y4YDL008");
                    }


                    /*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updPlateYdSelList*/
                    intRtnVal = ydStkBedDao.updYdStkbedYdStkColGp(recInTemp, 400);
                    if (intRtnVal < 0) {
                        szMsg = "[Jsp Session : "+szSessionName+"] 적치열구분["+szYD_STK_COL_GP+"] 적치BED번호[" + szYD_STK_BED_NO + "]를 입출고가능[E]로 수정 시 오류발생 : 루프 계속처리 - 반환값 : " + intRtnVal;
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                        throw new JDTOException("<procY4CrnUdWr> updYdStkbed :" + szMsg);
                    }

                    szMsg = "[Jsp Session : "+szSessionName+"] 적치열구분["+szYD_STK_COL_GP+"] 적치BED번호[" + szYD_STK_BED_NO + "]를 입출고가능[E]로 수정 완료 ";
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    //L2 로 적치열 수정된 정보를 내려보내준다.
                    recSndTemp =  JDTORecordFactory.getInstance().create();
                    if(szYD_STK_COL_GP.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD)) {
                        recSndTemp.setField("MSG_ID",           "YDY8L001");
                        if(PlateGdsYdUtil.isSendToEaiY9_ydStkColGp(szYD_STK_COL_GP)){
                            recSndTemp.setField("MSG_ID",           "YDY9L001");
                        }

                        recSndTemp.setField("YD_GP",            YdConstant.YD_GP_PLATE2_GDS_YARD);
                    } else {
                        recSndTemp.setField("MSG_ID",           "YDY4L001");
                        recSndTemp.setField("YD_GP",            YdConstant.YD_GP_PLATE_GDS_YARD);
                    }
                    recSndTemp.setField("YD_INFO_SYNC_CD",  "4");
                    recSndTemp.setField("YD_STK_COL_GP",    szYD_STK_COL_GP);
                    recSndTemp.setField("YD_STK_BED_NO",    szYD_STK_BED_NO);

                    szMsg = "[Jsp-Session "+szSessionName+" ] L2 로 적치열 수정된 정보 송신 시작" ;
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    recSndTemp.setField("LOG_ID", logId);  // 전문에 있는 logId


                    ydDelegate.sendMsg(recSndTemp);

                    szMsg = "[Jsp-Session "+szSessionName+" ] L2 로 적치열 수정된 정보 송신 완료" ;
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                }
            //}
        }catch(Exception e) {
            szMsg = "["+szMethodName+"] 후판창고 선별 BED 타입변경  처리시 예외메세지: " + e.getMessage();
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
        }
        

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START

szMsg = "후판창고 BED 타입변경 처리 : 후판제품용선별(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        
        return YdConstant.RETN_CD_SUCCESS;
    }

    /**
     * 오퍼레이션명 : pallet 권상작업 마지막 CHECK
     *
     * @param   String szEqpId 설비ID
     * @return boolean true(설비사용가능), false(설비사용불가)
     * @throws JDTOException
     */
    public int WbookIdEndCheckPT(String sCarKind, String szYD_WBOOK_ID,String szYD_CRN_SCH_ID,String szYD_CAR_STOP_LOC)throws JDTOException  {

        YdCrnSchDao     ydCrnSchDao     = new YdCrnSchDao();
        YdCarSchDao     ydCarSchDao     = new YdCarSchDao();
        YdEqpDao    ydEqpDao    = new YdEqpDao();
        String szMsg            = null;
        String szMethodName     = "WbookIdEndCheckPT";
        JDTORecordSet rsResult  = null;
        JDTORecordSet outRecSet = null;

        JDTORecord recInTemp    = null;
        JDTORecord recLast      = null;
        JDTORecord inRecord     = null;
        JDTORecord outRecord    = null;

        JDTORecordSet   rsResultYN  = null; //FlagYn
        JDTORecord      recInParaYN = null; //FlagYn
        JDTORecord      recParaYN   = null; //FlagYn
        int             intRtnValYN = 0;    //FlagYn
        String          FlagYn      = "N";  //FlagYn
        YdPlateCommDAO  commDao         = new YdPlateCommDAO(); //FlagYn

        String szLST_CRN_SCH_ID = "";
        String szYD_CAR_USE_GP  = "";
        String szCAR_NO         = "";
        String szCARD_NO        = "";
        String szTRANS_ORD_DATE = "";
        String szTRANS_ORD_SEQNO= "";
        String szWLOC_CD        = "";
        String szYD_PNT_CD      = "";

        JDTORecord outRecTarget         = null;
        JDTORecordSet outRecSetTargetUp = null;
        JDTORecordSet outRecSetTarget   = null;
        JDTORecord inRecTargetUp        = null;
        JDTORecord outRecTargetUp       = null;
        JDTORecord inRecTarget          = null;
        JDTORecord inRec                = null;

        String szCAR_LOTID_TARGET       = "";
        String szYD_CAR_SCH_ID_TARGET   = "";
        String szTRANS_ORD_DATE_TARGET  = "";
        String szTRANS_ORD_SEQNO_TARGET = "";
        String szCAR_LOTID_UP           = "";
        String szCARSCH_UP              = "";
        String szYD_CAR_SCH_ID_FIX      = "N";
        String szYD_CARPNT_CD2          = "";
        String szYD_GP                  = null;
        String szYD_CAR_PROG_STAT       = null;
        String szCAR_KIND               = null;

        boolean isSend                  = false;
        boolean isFirst                 = true;

        int intRtnVal           = 0;


        try {



            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            recInTemp = JDTORecordFactory.getInstance().create();
            recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);

            /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getWbookIdEndCheck*/
            intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 505);
            if(intRtnVal <= 0) {
                szMsg="["+ szMethodName +"] 작업예약ID["+szYD_WBOOK_ID+"]로 크레인 스케줄을 조회 중 Error";

            } else {

                rsResult.last();
                recLast = JDTORecordFactory.getInstance().create();
                recLast.setRecord(rsResult.getRecord());
                szLST_CRN_SCH_ID    = ydDaoUtils.paraRecChkNull(recLast, "YD_CRN_SCH_ID");
                szYD_CAR_USE_GP     = ydDaoUtils.paraRecChkNull(recLast, "YD_CAR_USE_GP");

                //야드구분
                szYD_GP = szYD_CAR_STOP_LOC.substring(0, 1);

                if(szLST_CRN_SCH_ID.equals(szYD_CRN_SCH_ID)) {

                    //차량입동지시요구(YDYDJ633) 로직중 우선작업대상 검색로직  동일함
                    //-----------------------------------------------------------------------------------------------------------------------
                    //------------------------------------------------------------------------------------------------------------
                    //  신 상차처리 적용여부
                    //  권상시 입동 지시로
                    //------------------------------------------------------------------------------------------------------------
                    JDTORecordSet   outResult9  = JDTORecordFactory.getInstance().createRecordSet("");
                    JDTORecord      inRecord9   = JDTORecordFactory.getInstance().create();
                    JDTORecord      outRecord8  = JDTORecordFactory.getInstance().create();

                    String szAPPLY_YN9  = "";
                    inRecord9.setField("REPR_CD_GP", "T00171"); //2후판 제품창고야드 기준
                    inRecord9.setField("CD_GP", szYD_CAR_STOP_LOC.substring(4,5));  // 통로
                    inRecord9.setField("ITEM", szYD_CAR_STOP_LOC.substring(1,2));   // 동

                    /*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
                    intRtnVal = ydEqpDao.getYdEqp(inRecord9, outResult9, 999);
                    if(intRtnVal > 0) {
                        outResult9.first();
                        outRecord8  = outResult9.getRecord();
                        szAPPLY_YN9 = outRecord8.getFieldString("ITEM1");
                    }
                    ydUtils.putLog(szSessionName, szMethodName, "신 입동지시 처리 적용여부 " + szAPPLY_YN9, YdConstant.DEBUG);

                    outRecSetTarget = JDTORecordFactory.getInstance().createRecordSet("");
                    inRecord = JDTORecordFactory.getInstance().create();
                    inRecord.setField("YD_CAR_STOP_LOC", szYD_CAR_STOP_LOC);

                    if( szAPPLY_YN9.equals("S")) { //선별기준

                        /*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByBayInSeqPlate*/
                        intRtnVal = ydCarSchDao.getYdCarsch(inRecord, outRecSetTarget, 406);

                        if( intRtnVal == 0 ) {
                            szMsg= "["+ szMethodName +"] 입동지시할 차량스케줄이 존재하지 않습니다.";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            return 1;
                        }else if( intRtnVal < 0 ) {
                            szMsg= "["+ szMethodName +"] 입동지시순서 목록 조회 시 오류발생 : " + intRtnVal;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            return 1;
                        }

                        String szCAR_LOTID_FIRST = "";
                        String szMAX_CAR_LOTID   = "";

                        outRecTarget = JDTORecordFactory.getInstance().create();
                        inRecTargetUp = JDTORecordFactory.getInstance().create();

                        for(int i = 1; i <= outRecSetTarget.size(); i++) {

                            outRecSetTarget.absolute(i);
                            outRecTarget = outRecSetTarget.getRecord();

                            szCAR_LOTID_TARGET      = StringHelper.evl(outRecTarget.getFieldString("CAR_LOTID"), "");
                            szYD_CAR_SCH_ID_TARGET  = StringHelper.evl(outRecTarget.getFieldString("YD_CAR_SCH_ID"), "");
                            szTRANS_ORD_DATE_TARGET = StringHelper.evl(outRecTarget.getFieldString("TRANS_ORD_DATE"), "");
                            szTRANS_ORD_SEQNO_TARGET= StringHelper.evl(outRecTarget.getFieldString("TRANS_ORD_SEQNO"), "");
                            szYD_CAR_PROG_STAT      = StringHelper.evl(outRecTarget.getFieldString("YD_CAR_PROG_STAT"), "");
                            szCAR_KIND              = StringHelper.evl(outRecTarget.getFieldString("CAR_KIND"), "");
                            szCARD_NO               = StringHelper.evl(outRecTarget.getFieldString("CARD_NO"), "");

                            if(!"".equals(szCAR_LOTID_TARGET)&&
                               szCAR_LOTID_FIRST.equals(szCAR_LOTID_TARGET)){
                                /*
                                 * 다음 입동지시 대상(A)을 검색한 후 ,
                                 * 다음/다음 입동지시 대상(B)을 검색할때 다시 처음부터 for문을 돌린다.
                                 * 그때 다음입동지시 대상(A)은 제외한다.
                                 */
                                szMsg= "["+ szMethodName +"] 윤재광후판입고테스트 중 : 현재차량[" + szCAR_NO + "]의 카드["+szCARD_NO+"]에 정보";
                                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                                continue;
                            }

                            // PIDEV

                            if(("A".equals(szYD_CAR_PROG_STAT) || szCAR_KIND.startsWith("P"))) {
                                //1,2 후판이면서 입고차량인 경우는최상단 CAR LOTID 구하는 로직체크없이 입동대기 순서대로 입동시킨다. + 해송 PT 인 경우도 체크없이 입동 시킨다.

                                szMsg= "["+ szMethodName +"] 입동대기순에 입고챠량스케줄 : ["+ szYD_CAR_SCH_ID_TARGET + "] 처리";
                                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                                szYD_CAR_SCH_ID_FIX = szYD_CAR_SCH_ID_TARGET;

                            } else {
                                outRecSetTargetUp = JDTORecordFactory.getInstance().createRecordSet("");

                                inRecTargetUp.setField("YD_STOP_LOC"        , szYD_CAR_STOP_LOC);
                                inRecTargetUp.setField("CAR_LOTID"          , szCAR_LOTID_TARGET);
                                inRecTargetUp.setField("YD_CAR_SCH_ID"      , szYD_CAR_SCH_ID_TARGET);
                                inRecTargetUp.setField("TRANS_ORD_DATE"     , szTRANS_ORD_DATE_TARGET);
                                inRecTargetUp.setField("TRANS_ORD_SEQNO"    , szTRANS_ORD_SEQNO_TARGET);
                                inRecTargetUp.setField("FIRST_CAR_LOT_ID"   , szCAR_LOTID_FIRST);

                                // 최상단 CAR LOTID 구하는 로직
                                /*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByBayInSeqPlateMax*/
                                intRtnVal = ydCarSchDao.getYdCarsch(inRecTargetUp, outRecSetTargetUp, 410);

                                if(intRtnVal <= 0){

                                    szMsg= "["+ szMethodName +"] 최상단 CAR LOT 이상 intRtnVal = 0.";
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                                    szYD_CAR_SCH_ID_FIX = szYD_CAR_SCH_ID_TARGET;

                                }else{

                                    outRecSetTargetUp.first();
                                    outRecTargetUp  = outRecSetTargetUp.getRecord();
                                    szMAX_CAR_LOTID = StringHelper.evl(outRecTargetUp.getFieldString("MAX_CAR_LOTID"), "");

                                    szMsg = "["+szMethodName+"] szMAX_CAR_LOTID = " + szMAX_CAR_LOTID;
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


                                    if ("".equals(szMAX_CAR_LOTID)||                 // 최상단대상(현 LOT보다 SEQ가 작은거 또는 LOT정보 없슴)존재안함.
                                        szCAR_LOTID_TARGET.equals(szMAX_CAR_LOTID)){ // 최상단 == 대상 이면   대상을 선택

                                        szMsg= "["+ szMethodName +"] 대상이 최상단 LOTID 와 동일 .";
                                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                                        szYD_CAR_SCH_ID_FIX = szYD_CAR_SCH_ID_TARGET;
                                    } else {
                                        szMsg= "["+ szMethodName +"] 대상이 최상단 LOTID 와 틀림 .";
                                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    }
                                }
                            }

                            if (!szYD_CAR_SCH_ID_FIX.equals("N")){

                                outRecSet   = JDTORecordFactory.getInstance().createRecordSet("");
                                inRecTarget = JDTORecordFactory.getInstance().create();
                                inRecTarget.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID_FIX);

                                /*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByBayInSeqPlateTarget*/
                                intRtnVal = ydCarSchDao.getYdCarsch(inRecTarget, outRecSet, 408);

                                if( intRtnVal == 0 ) {
                                    szMsg= "["+ szMethodName +"] 입동지시할 차량스케줄이 존재하지 않습니다.";
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                                    return 1;
                                }else if( intRtnVal < 0 ) {
                                    szMsg= "["+ szMethodName +"] 입동지시순서 목록 조회 시 오류발생 : " + intRtnVal;
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                                    return 1;
                                }

                                szMsg= "["+ szMethodName +"] 기존입동지시순서 목록 조회 성공 - 대상재 건수 : " + intRtnVal;
                                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                                outRecSet.first();
                                inRec = outRecSet.getRecord();

                                if(isFirst){
                                    /*
                                     *  현재 다음차례로 들어와야될 차량인 경우
                                     */
                                    szYD_CAR_USE_GP     = StringHelper.evl(inRec.getFieldString("YD_CAR_USE_GP"), "");
                                    szCAR_NO            = StringHelper.evl(inRec.getFieldString("CAR_NO"), "");
                                    szCARD_NO           = StringHelper.evl(inRec.getFieldString("CARD_NO"), "");
                                    szTRANS_ORD_DATE    = StringHelper.evl(inRec.getFieldString("TRANS_ORD_DATE"), "");
                                    szTRANS_ORD_SEQNO   = StringHelper.evl(inRec.getFieldString("TRANS_ORD_SEQNO"), "");
                                    szWLOC_CD           = StringHelper.evl(inRec.getFieldString("WLOC_CD"), "");
                                    szYD_PNT_CD         = StringHelper.evl(inRec.getFieldString("YD_PNT_CD"), "");
                                    szYD_CAR_PROG_STAT  = StringHelper.evl(inRec.getFieldString("YD_CAR_PROG_STAT"), "");
                                    szCAR_KIND          = StringHelper.evl(inRec.getFieldString("CAR_KIND"), "");

                                    // 항목값 초기화
                                    isSend  = true;
                                    isFirst = false;
                                    szYD_CAR_SCH_ID_FIX = "N";

                                    // 다음/다음차량 입동대상 체크시 다음차량대상은 제외한다.
                                    szCAR_LOTID_FIRST   = szCAR_LOTID_TARGET;

                                    i = 1;

                                }else{
                                    /*
                                     *  현재 다음/다음차례로 들어와야될 차량인 경우
                                     */
                                    //--2013.04.15 SMS메세지 통로구분을 A,B 에서 숫자로 변경 (현업요청)

                                    String sSmsMsg      = szYD_CAR_STOP_LOC.substring(1,2)+"동 "+szYD_CAR_STOP_LOC.substring(4,5)+"통로 입동대기차량입니다.\n 입동대기장소로 이동하세요.";
                                    String sFromTelNo   = "";

                                    szMsg= "["+ szMethodName +"] sSmsMsg="+sSmsMsg;
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                                    String sTelNo = StringHelper.evl(inRec.getFieldString("TEL_NO"), "");
                                    String sCarNo = StringHelper.evl(inRec.getFieldString("CAR_NO"), "");

                                    // 알림톡 전환 FLAG
                                    rsResultYN = JDTORecordFactory.getInstance().createRecordSet("");
                                    recInParaYN = JDTORecordFactory.getInstance().create();
                                    intRtnValYN = commDao.select(recInParaYN, rsResultYN, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.TalkFlagYN");
                                    if(intRtnValYN <= 0) {
                                        szMsg = "["+szMethodName+"] 알림톡 전환 FLAG 조회 실패";
                                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    } else{
                                        szMsg = "["+szMethodName+"] 알림톡 전환 FLAG 조회 성공";
                                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    }
                                    //레코드 추출
                                    rsResultYN.first();
                                    recParaYN = rsResultYN.getRecord();
                                    FlagYn    = ydDaoUtils.paraRecChkNull(recParaYN, "FLAG_YN");

                                    if("Y".equals(FlagYn)){
                                        MessageSenderTalk    sender = new MessageSenderTalk();

                                        JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
                                        recPara1.setField("PHONE_NUM", new String(sTelNo));
                                        recPara1.setField("TMPL_CD", new String("CM1"));
                                        recPara1.setField("SND_MSG", new String("[현대제철 공지사항]\n" + sSmsMsg));
                                        recPara1.setField("SUBJECT", new String("입동지시 알림"));
                                        recPara1.setField("SMS_SND_NUM", new String("0416801616"));
                                        recPara1.setField("RECV_ID","1522110");
                                        recPara1.setField("GROUP_ID","KaKao");
                                        recPara1.setField("PROGRAM_ID","udttalk");

                                        if(sTelNo.length() > 8 ) {
                                            sender.sendTalk(recPara1);  // 알림톡 송신
                                            szMsg = "["+szMethodName+"] 신규 알림톡 송신";
                                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                        } else{
                                            szMsg = "["+szMethodName+"] 전화번호 자릿수 8자리 미만["+ sTelNo.length() + "] 신규 알림톡 송신 안함";
                                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                        }
                                    } else {

                                        JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
                                        recPara1.setField("FROM_PHONE_NO", sFromTelNo);
                                        recPara1.setField("TO_PHONE_NO"  , sTelNo);
                                        recPara1.setField("TO_CONTENT"   , sSmsMsg);

                                        if(sTelNo.length() > 8 ) {
                                            PlateGdsYdUtil.updSmsMsgSend(recPara1); // SMS 송신
                                            szMsg = "["+szMethodName+"] 기존 SMS 송신";
                                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                        } else{
                                            szMsg = "["+szMethodName+"] 전화번호 자릿수 8자리 미만["+ sTelNo.length() + "] 기존 알림톡 송신 안함";
                                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                        }
                                    }

                                    szMsg= "["+ szMethodName +"] 입동지시차량[" + szCAR_NO + "]의 다음차량["+sCarNo+"/"+sTelNo+"]에 대한 입동대기지시";
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                                    i = outRecSetTarget.size() + 1;
                                }
                            }
                        } //FOR
                    }else{
                        outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
                        /* com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdCarSchByInSeqCheck */
                        intRtnVal = ydCarSchDao.getYdCarsch(inRecord, outRecSet, 310);

                        if( intRtnVal == 0 ) {
                            szMsg= "["+ szMethodName +"] 입동지시할 차량스케줄이 존재하지 않습니다.";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            return 1;
                        }else if( intRtnVal < 0 ) {
                            szMsg= "["+ szMethodName +"] 입동지시순서 목록 조회 시 오류발생 : " + intRtnVal;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            return 1;
                        }

                        outRecSet.first();
                        inRecord = outRecSet.getRecord();

                        szYD_CAR_USE_GP     = StringHelper.evl(inRecord.getFieldString("YD_CAR_USE_GP"), "");
                        szCAR_NO            = StringHelper.evl(inRecord.getFieldString("CAR_NO"), "");
                        szCARD_NO           = StringHelper.evl(inRecord.getFieldString("CARD_NO"), "");
                        szTRANS_ORD_DATE    = StringHelper.evl(inRecord.getFieldString("TRANS_ORD_DATE"), "");
                        szTRANS_ORD_SEQNO   = StringHelper.evl(inRecord.getFieldString("TRANS_ORD_SEQNO"), "");
                        szCAR_KIND          = StringHelper.evl(inRecord.getFieldString("CAR_KIND"), "");

                        szWLOC_CD           = StringHelper.evl(inRecord.getFieldString("WLOC_CD"), "");
                        szYD_PNT_CD         = StringHelper.evl(inRecord.getFieldString("YD_PNT_CD"), "");
                        szYD_CARPNT_CD2     = StringHelper.evl(inRecord.getFieldString("YD_CARPNT_CD"), "");
                        isSend = true;
                    }

                    if( szYD_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM)&&isSend){

                        outRecord = JDTORecordFactory.getInstance().create();

                        //sCarKind = 현재작업중인 차량 종류 -> 현재작업이 PALLET인 경우 처리 안함

                        szMsg="[권상실적처리]  PALLET 처리 완료 sCarKind = " + sCarKind;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                        if("A".equals(szYD_CAR_PROG_STAT) || szCAR_KIND.startsWith("P") || sCarKind.startsWith("P") ) {
                            //1,2후판 이면서 입고차량인 경우 출하로 입동지시전문(YDDMR028)을 전송하지 않는다. + //
                            szMsg="입고차량 입동지시 송신 안함" ;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                        } else {
                            String sCurrDate = YdUtils.getCurDate("yyyyMMddHHmmss");

                            outRecord.setField("MQ_TC_CD"             , "M10YDLMJ1062"      );
                            outRecord.setField("MQ_TC_CREATE_DDTT"    , sCurrDate           ); //JMSTC생성일시
                            outRecord.setField("TRANS_WORD_DATE"      , szTRANS_ORD_DATE    ); // 운송지시일자
                            outRecord.setField("TRANS_WORD_SEQNO"     , szTRANS_ORD_SEQNO   ); // 운송의뢰순번

                            outRecord.setField("CAR_NO"               , szCAR_NO            ); // 차량번호
                            outRecord.setField("YD_GP"                , "T"                 ); // 야드구분
                            outRecord.setField("DIST_GOODS_GP"        , "P"                 ); // 출하제품구분
                            outRecord.setField("SCH_YN"               , "N"                 ); // 스케줄 여부
                            outRecord.setField("BAYIN_DDTT"           , sCurrDate           ); // 입동일시
                            outRecord.setField("WLOC_CD"              , szWLOC_CD           ); // 개소코드
                            outRecord.setField("YD_PNT_CD"            , szYD_PNT_CD         ); // 야드포인트코드
                            outRecord.setField("LOAN_PULLOUT_ABLE_YN" , "Y"                 ); // 차입인출가능여부
                            outRecord.setField("YD_CARPNT_CD"         , szYD_CARPNT_CD2     );

                            /*
                             * 입동지시 대상차량에 입동지시 송신여부 셋팅
                             */
                            outRecord.setField("YD_CAR_RCPT_CHK_YN"   ,"Y");
                            /* com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdCarschYdCarRcptChkYn_PIDEV
                            UPDATE TB_YD_CARSCH
                               SET YD_CAR_RCPT_CHK_YN = :YD_CAR_RCPT_CHK_YN
                                   , YD_BAYIN_WO_SEQ  = '1'
                             WHERE CAR_NO             = :CAR_NO
                               AND TRANS_ORD_DATE     = :TRANS_ORD_DATE
                               AND TRANS_ORD_SEQNO    = :TRANS_ORD_SEQNO
                            */
                            intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId_PIDEV(outRecord, 305);
                            if(intRtnVal <= 0) {
                                szMsg="차량스케줄에 입동지시 송신여부 셋팅 Error!! Code : " + intRtnVal;
                                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            }

                            ydDelegate.sendMsg(outRecord);
                        }


                    }
                }
            }
        } catch(Exception e) {
            szMsg = "권상작업 마지막 CHECK 중 예외발생! 예외메세지: " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return 0;
        }

        return 1;

    } //end of WbookEndCheck
  //---------------------------------------------------------------------------
} // end of class CraneLdHdSeEJBBean
