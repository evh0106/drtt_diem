package com.inisteel.cim.yd.ydSch.CraneReSch;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.rule.GetBreRule6;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdDBAssist;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.crn.CrnSchUtil;
import com.inisteel.cim.yd.common.util.loc.YdToLocDcsnUtil;
import com.inisteel.cim.ydPI.dao.YdPICommDAO; 


/**
 * 크레인리스케쥴요청 Session EJB 
 *
 * @ejb.bean name="CrnReSchSeEJB" jndi-name="CrnReSchSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CrnReSchSeEJBBean extends BaseSessionBean {

    // Session Name
    private String szSessionName=getClass().getName();

    private YdUtils ydUtils =new YdUtils();

    private YdDaoUtils ydDaoUtils =new YdDaoUtils();

    private YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

    private YdPICommDAO   ydPICommDAO   = new YdPICommDAO();
    // [DEBUG] message flag
    private boolean bDebugFlag=true;

    /**
     * ejbCrate()
     *
     * @throws javax.ejb.CreateException
     */
    public void ejbCreate() throws javax.ejb.CreateException {
    }


    /**
     * 오퍼레이션명 : 통합야드 크레인 리스케줄
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY0CrnReSch(JDTORecord msgRecord)throws JDTOException  {

        YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

        JDTORecordSet rsSchRule     = null;
        JDTORecordSet outRecSet     = null;
        JDTORecordSet rsReSchResult = JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecord recTemp          = null;

        int intRtnVal                   = 0 ;
        int intGp                       = 0 ;
        String szMsg                    = "";
        String szMethodName             = "procY0CrnReSch";

        String szEqpId                  = "";
        String szYdGp                   = "";
        String szYdBayGp                = "";

        boolean bRtnCheck               = true;

        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null){
            szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }

        if(bDebugFlag){
            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        try{

            //파라미터로 넘어온 설비 ID로 설비 상태를 Check
            szEqpId   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");

            bRtnCheck = this.Y0EqpStatCheck(szEqpId);

            //설비아이디의 야드구분 동구분을 빼서 스케줄기준Table조회
            szYdGp    = szEqpId.substring(0,1);
            szYdBayGp = szEqpId.substring(1,2);
            recTemp = JDTORecordFactory.getInstance().create();
            recTemp.setField("YD_GP",     szYdGp);
            recTemp.setField("YD_BAY_GP", szYdBayGp);
            intRtnVal = ydSchRuleDao.getYdSchrule(recTemp, outRecSet, intGp);

            if(intRtnVal <= 0) {
                szMsg="스케줄기준이 조회가 되지않았습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            if(bRtnCheck == false) {
                //1. 고장시 고장 리스케줄 메소드 호출
                intRtnVal = this.Y0DisableReSch(szEqpId, rsSchRule, rsReSchResult);
                if(intRtnVal == -1) {
                    szMsg="고장 리스케줄 처리 Error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }

            }else{
                //2. 정상시 복구 리스케줄 메소드 호출
                intRtnVal = this.Y0ResPairReSch(szEqpId, rsSchRule, rsReSchResult);
                if(intRtnVal == -1) {
                    szMsg="복구 리스케줄 처리 Error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }
            }

            //3. 호출한 메소드의 결과값으로 작업예약 Table및 크레인 스케줄 Table업데이트 메소드 호출
            intRtnVal = this.Y0UpdWbookCrnsch(rsReSchResult);
            if(intRtnVal == -1) {
                szMsg="작업예약 및 크레인 스케줄 등록 중 Error";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

        }catch(Exception e){

            szMsg="Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }

        szMsg="크레인 리스케줄("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return;

    } //end of procY0CrnReSch()

    /**
     * 오퍼레이션명 : 고장 리스케줄
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param szEqpId, rsSchRule, rsReSchResult
     * @return
     * @throws JDTOException
     */
    public int Y0DisableReSch(String szEqpId, JDTORecordSet rsSchRule, JDTORecordSet rsReSchResult)throws JDTOException  {

        JDTORecord recTemp              = null;
        JDTORecord recResult            = null;
        JDTORecordSet rsResult          = JDTORecordFactory.getInstance().createRecordSet("Temp");

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y0DisableReSch";

        String szWrkCrn                 = "";
        String szAltCrn                 = "";


        try{

            for(int Loop_i = 1; Loop_i <= rsSchRule.size(); Loop_i++) {
                rsSchRule.absolute(Loop_i);
                recTemp = JDTORecordFactory.getInstance().create();
                recTemp.setRecord(rsSchRule.getRecord());
                szWrkCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_CRN");
                szAltCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_ALT_CRN");

                recResult = JDTORecordFactory.getInstance().create();
                //설비아이디가 스케줄 기준의 주크레인과 같거나 대체크레인과 같을 경우
                if(szEqpId.equals(szWrkCrn)) {
                    //새로운 레코드 셋에 등록한다.(스케줄 코드, 설비ID, 스케줄우선순위)
                    recResult.setField("YD_SCH_CD", recTemp.getFieldString("YD_SCH_CD"));
                    recResult.setField("YD_EQP_ID", recTemp.getFieldString("YD_ALT_CRN"));
                    recResult.setField("YD_SCH_PRIOR", recTemp.getFieldString("YD_ALT_CRN_PRIOR"));
                    recResult.setField("WRK_CRN_YN", "Y");
                    rsResult.addRecord(recResult);

                }else if (szEqpId.equals(szAltCrn)) {
                    //새로운 레코드 셋에 등록한다.
                    recResult.setField("YD_SCH_CD", recTemp.getFieldString("YD_SCH_CD"));
                    recResult.setField("YD_SCH_PRIOR", recTemp.getFieldString("YD_ALT_CRN_PRIOR"));
                    recResult.setField("WRK_CRN_YN", "N");
                    rsResult.addRecord(recResult);
                }

            }//end of for
            rsReSchResult.addAll(rsResult);


        }catch(Exception e){
            szMsg="고장 리스케줄 처리 Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }


        szMsg="고장 리스케줄 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y0DisableReSch()

    /**
     * 오퍼레이션명 : 복구리스케줄
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param szEqpId, rsSchRule, rsReSchResult
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y0ResPairReSch(String szEqpId, JDTORecordSet rsSchRule, JDTORecordSet rsReSchResult)throws JDTOException  {

        JDTORecord recTemp              = null;
        JDTORecord recResult            = null;
        JDTORecordSet rsResult          = JDTORecordFactory.getInstance().createRecordSet("Temp");

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y0ResPairReSch";

        String szWrkCrn                 = "";
        String szAltCrn                 = "";

        try{

            for(int Loop_i = 1; Loop_i <= rsSchRule.size(); Loop_i++) {
                rsSchRule.absolute(Loop_i);
                recTemp = JDTORecordFactory.getInstance().create();
                recTemp.setRecord(rsSchRule.getRecord());
                szWrkCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_CRN");
                szAltCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_ALT_CRN");

                recResult = JDTORecordFactory.getInstance().create();
                //설비아이디가 스케줄 기준의 주크레인과 같거나 대체크레인과 같을 경우
                if(szEqpId.equals(szWrkCrn)) {
                    //새로운 레코드 셋에 등록한다.(스케줄 코드, 설비ID, 스케줄우선순위)
                    recResult.setField("YD_SCH_CD", recTemp.getFieldString("YD_SCH_CD"));
                    recResult.setField("YD_EQP_ID", recTemp.getFieldString("YD_WRK_CRN"));
                    recResult.setField("YD_SCH_PRIOR", recTemp.getFieldString("YD_WRK_CRN_PRIOR"));
                    recResult.setField("WRK_CRN_YN", "Y");
                    rsResult.addRecord(recResult);

                }else if (szEqpId.equals(szAltCrn)) {
                    //새로운 레코드 셋에 등록한다.
                    recResult.setField("YD_SCH_CD", recTemp.getFieldString("YD_SCH_CD"));
                    recResult.setField("YD_SCH_PRIOR", recTemp.getFieldString("YD_WRK_CRN_PRIOR"));
                    recResult.setField("WRK_CRN_YN", "N");
                    rsResult.addRecord(recResult);
                }

            }//end of for
            rsReSchResult.addAll(rsResult);

        }catch(Exception e){

            szMsg="복구 리스케줄 처리 Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }

        szMsg="복구 리스케줄 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y0ResPairReSch()

    /**
     * 오퍼레이션명 : 통합야드 크레인 리스케줄 작업예약 및 크레인스케줄 등록
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param rsCrnSch
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y0UpdWbookCrnsch(JDTORecordSet rsCrnSch)throws JDTOException  {

        JDTORecord    recTemp           = null;

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y0UpdWbookCrnsch";

        String szSchCd                  = "";
        String szSchPrior               = "";
        String szEqpId                  = "";

        try{

            for(int Loop_i = 1; Loop_i <= rsCrnSch.size(); Loop_i++) {
                rsCrnSch.absolute(Loop_i);
                recTemp = JDTORecordFactory.getInstance().create();
                recTemp.setRecord(rsCrnSch.getRecord());

                szSchCd    = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");
                szSchPrior = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_PRIOR");

                //크레인 스케줄 Table 업데이트
                if(recTemp.getFieldString("WRK_CRN_YN").equals("Y")){

                }else{

                }
            }

        }catch(Exception e){

            szMsg="작업예약 및 크레인 스케줄 업데이트 Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }

        szMsg="작업예약 및 크레인 스케줄 업데이트("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y0UpdWbookCrnsch()

    /**
     * 오퍼레이션명 : 설비상태 체크
     *
     * @param   String szEqpId 설비ID
     * @return boolean true(설비사용가능), false(설비사용불가)
     * @throws JDTOException
     */
    public boolean Y0EqpStatCheck(String szEqpId)throws JDTOException  {

        //리턴값(boolean)
        boolean blnRtnVal      = false;
        //메세지
        String szMsg           = null;
        //메소드명
        String szMethodName    = "Y0EqpStatCheck";
        //설비상태
        String szYD_EQP_STAT   = null;
        //야드설비작업상태
        String szYD_EQP_WRK_MODE = null;
        //레코드 선언
        JDTORecord recPara     = null;
        //레코드셋 선언
        JDTORecordSet rsResult = null;

        try {
            //레코드 생성
            recPara = JDTORecordFactory.getInstance().create();
            //레코드셋 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //설비ID를 작업크레인으로 설정
            recPara.setField("YD_EQP_ID", szEqpId);

            //설비 체크 및 데이터 조회
            blnRtnVal = this.Y0ChkGetEqp(szEqpId, rsResult);
            if (!blnRtnVal) return blnRtnVal;

            //레코드 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //설비상태
            szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
            //야드설비작업상태
            szYD_EQP_WRK_MODE = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_MODE");

            //크레인의 상태가 'T'이면 false 리턴.
            //고장 플레그 변경으로 수정 [2009.12.03 - 이현성]
            if (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {

                szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT + ") 입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;
            } else if (szYD_EQP_WRK_MODE.equals("2")) {
                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                 * OFF-LINE 체크기능 추가 - 임춘수 추가 2009.06.17
                 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                szMsg = "설비ID(" + szEqpId + ")의 야드설비작업상태(" + szYD_EQP_STAT + " : OFF-LINE ) 입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;
            } else {

                blnRtnVal = true;

            }
        } catch(Exception e) {
            szMsg = "설비상태 체크 중 예외발생! 예외메세지: " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            blnRtnVal = false;
        }
        return blnRtnVal;

    } //end of Y0EqpStatCheck

    /**
     * 오퍼레이션명 : 설비 유무체크 및 조회결과 데이터 반환
     *
     * @param  String        szEqpId  설비ID
     *         JDTORecordSet rsResult 결과레코드셋
     * @return boolean       true(성공), false(실패)
     * @throws JDTOException
     */
    public boolean Y0ChkGetEqp(String szEqpId, JDTORecordSet rsResult)throws JDTOException  {

        //설비 DAO
        YdEqpDao ydEqpDao     = new YdEqpDao();
        //리턴값(boolean)
        boolean blnRtnVal     = false;
        //리턴값(int)
        int intRtnVal         = 0;
        //메소드명
        String szMethodName   = "Y0ChkGetEqp";
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
    } //end of Y0ChkGetEqp

    /**
     * 오퍼레이션명 : C연주 크레인 리스케줄
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY1CrnReSch(JDTORecord msgRecord)throws JDTOException  {

        JDTORecordSet outRecSet = null;
        JDTORecordSet rsReSchResult = JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecord recTemp = null;

        int intRtnVal                   = 0 ;
        String szMsg                    = "";
        String szMethodName             = "procY1CrnReSch";

        String szEqpId                  = "";
        String szYdGp                   = "";
        String szYdBayGp                = "";

        boolean bRtnCheck               = true;


        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null){
            szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }

        if(bDebugFlag){
            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        try{

            //파라미터로 넘어온 설비 ID로 설비 상태를 Check
            szEqpId   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");

            bRtnCheck = this.Y0EqpStatCheck(szEqpId);

            //설비아이디의 야드구분 동구분을 빼서 스케줄기준Table조회
            szYdGp    = szEqpId.substring(0,1);
            szYdBayGp = szEqpId.substring(1,2);
            recTemp = JDTORecordFactory.getInstance().create();
            recTemp.setField("YD_GP",     szYdGp);
            recTemp.setField("YD_BAY_GP", szYdBayGp);
            outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
            intRtnVal = ydSchRuleDao.getYdSchrule(recTemp, outRecSet, 7);
            if(intRtnVal <= 0) {
                szMsg="스케줄기준이 조회가 되지않았습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            if(bRtnCheck == false) {
                //------------------------------------------------------------------------------------------------
                // 고장  리스케줄 호출 LOG 추가 (이현성 2010.01.14)
                //------------------------------------------------------------------------------------------------
                szMsg="procY1CrnReSch - 고장 리스케줄 호출";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                intRtnVal = this.Y1DisableReSch(szEqpId, outRecSet, rsReSchResult);
                if(intRtnVal == -1) {
                    szMsg="고장 리스케줄 처리 Error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }

            }else{

                //------------------------------------------------------------------------------------------------
                // 복구 리스케줄 호출 LOG 추가 (이현성 2010.01.14)
                //------------------------------------------------------------------------------------------------
                szMsg="procY1CrnReSch - 복구 리스케줄 호출";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                intRtnVal = this.Y1ResPairReSch(szEqpId, outRecSet, rsReSchResult);
                if(intRtnVal == -1) {
                    szMsg="복구 리스케줄 처리 Error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }
            }

            //3. 호출한 메소드의 결과값으로 작업예약 Table및 크레인 스케줄 Table업데이트 메소드 호출
            intRtnVal = this.Y1UpdWbookCrnsch(rsReSchResult);
            if(intRtnVal == -1) {
                szMsg="작업예약 및 크레인 스케줄 등록 중 Error";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

        }catch(Exception e){

            szMsg="Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }

        szMsg="크레인 리스케줄("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return;

    } //end of procY1CrnReSch()

    /**
     * 오퍼레이션명 : 고장 리스케줄
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param szEqpId, rsSchRule, rsReSchResult
     * @return
     * @throws JDTOException
     */
    public int Y1DisableReSch(String szEqpId, JDTORecordSet rsSchRule, JDTORecordSet rsReSchResult)throws JDTOException  {

        JDTORecord recTemp              = null;
        JDTORecord recResult            = null;
        JDTORecordSet rsResult          = JDTORecordFactory.getInstance().createRecordSet("Temp");

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y1DisableReSch";

        String szWrkCrn                 = "";
        String szAltCrn                 = "";

        try{

            for(int Loop_i = 1; Loop_i <= rsSchRule.size(); Loop_i++) {
                rsSchRule.absolute(Loop_i);
                recTemp = JDTORecordFactory.getInstance().create();
                recTemp.setRecord(rsSchRule.getRecord());
                szWrkCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_CRN");
                szAltCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_ALT_CRN");

                recResult = JDTORecordFactory.getInstance().create();
                //설비아이디가 스케줄 기준의 주크레인과 같거나 대체크레인과 같을 경우
                if(szEqpId.equals(szWrkCrn)) {
                    //새로운 레코드 셋에 등록한다.(스케줄 코드, 설비ID, 스케줄우선순위)
                    recResult.setField("YD_SCH_CD", recTemp.getFieldString("YD_SCH_CD"));
                    recResult.setField("YD_EQP_ID", recTemp.getFieldString("YD_ALT_CRN"));
                    recResult.setField("YD_SCH_PRIOR", recTemp.getFieldString("YD_ALT_CRN_PRIOR"));
                    recResult.setField("WRK_CRN_YN", "Y");
                    rsResult.addRecord(recResult);

                }else if (szEqpId.equals(szAltCrn)) {
                    //새로운 레코드 셋에 등록한다.
                    recResult.setField("YD_SCH_CD", recTemp.getFieldString("YD_SCH_CD"));
                    recResult.setField("YD_SCH_PRIOR", recTemp.getFieldString("YD_ALT_CRN_PRIOR"));
                    recResult.setField("WRK_CRN_YN", "N");
                    rsResult.addRecord(recResult);
                }

            }//end of for
            rsReSchResult.addAll(rsResult);

        }catch(Exception e){
            szMsg="고장 리스케줄 처리 Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }

        szMsg="고장 리스케줄 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y1DisableReSch()

    /**
     * 오퍼레이션명 : 복구리스케줄
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param szEqpId, rsSchRule, rsReSchResult
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y1ResPairReSch(String szEqpId, JDTORecordSet rsSchRule, JDTORecordSet rsReSchResult)throws JDTOException  {

        JDTORecord recTemp              = null;
        JDTORecord recResult            = null;
        JDTORecordSet rsResult          = JDTORecordFactory.getInstance().createRecordSet("Temp");

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y1ResPairReSch";

        String szWrkCrn                 = "";
        String szAltCrn                 = "";

        try{

            for(int Loop_i = 1; Loop_i <= rsSchRule.size(); Loop_i++) {
                rsSchRule.absolute(Loop_i);
                recTemp = JDTORecordFactory.getInstance().create();
                recTemp.setRecord(rsSchRule.getRecord());
                szWrkCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_CRN");
                szAltCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_ALT_CRN");

                recResult = JDTORecordFactory.getInstance().create();
                //설비아이디가 스케줄 기준의 주크레인과 같거나 대체크레인과 같을 경우
                if(szEqpId.equals(szWrkCrn)) {
                    //새로운 레코드 셋에 등록한다.(스케줄 코드, 설비ID, 스케줄우선순위)
                    recResult.setField("YD_SCH_CD", recTemp.getFieldString("YD_SCH_CD"));
                    recResult.setField("YD_EQP_ID", recTemp.getFieldString("YD_WRK_CRN"));
                    recResult.setField("YD_SCH_PRIOR", recTemp.getFieldString("YD_WRK_CRN_PRIOR"));
                    recResult.setField("WRK_CRN_YN", "Y");
                    rsResult.addRecord(recResult);

                }else if (szEqpId.equals(szAltCrn)) {
                    //새로운 레코드 셋에 등록한다.
                    recResult.setField("YD_SCH_CD", recTemp.getFieldString("YD_SCH_CD"));
                    recResult.setField("YD_SCH_PRIOR", recTemp.getFieldString("YD_WRK_CRN_PRIOR"));
                    recResult.setField("WRK_CRN_YN", "N");
                    rsResult.addRecord(recResult);
                }

            }//end of for
            rsReSchResult.addAll(rsResult);

        }catch(Exception e){

            szMsg="복구 리스케줄 처리 Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }

        szMsg="복구 리스케줄 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y1ResPairReSch()

    /**
     * 오퍼레이션명 : C연주 크레인 리스케줄 작업예약 및 크레인스케줄 등록
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param rsCrnSch
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y1UpdWbookCrnsch(JDTORecordSet rsCrnSch)throws JDTOException  {

        YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
        YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
        JDTORecord    recTemp           = null;
        JDTORecord    inRec             = null;

        JDTORecordSet outRecSet         = null;
        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y1UpdWbookCrnsch";

        String szSchCd                  = "";
        String szSchPrior               = "";

        String[] szArrEqpId             = new String[5];
        String szEqpId                  = "";
        int szEqpCount                  = 0;
        boolean bIsBe                   = false;
        EJBConnector ejbConn            = null;


        try{

            for(int Loop_i = 1; Loop_i <= rsCrnSch.size(); Loop_i++) {
                rsCrnSch.absolute(Loop_i);
                recTemp = JDTORecordFactory.getInstance().create();
                recTemp.setRecord(rsCrnSch.getRecord());

                szSchCd    = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");
                szSchPrior = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_PRIOR");

                // 설비ID를 가져옴
                szEqpId = ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_ID");

                if(!szEqpId.equals("")){

                    for(int i=0; i<szArrEqpId.length; i++){
                        if(szEqpId.equals(szArrEqpId[i])){
                            bIsBe = true;
                            break;
                        }else{
                            bIsBe = false;
                        }
                    }

                    if(bIsBe == false){
                        szArrEqpId[szEqpCount] = ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_ID");
                        szEqpCount++;
                    }
                }

                //작업예약TABLE 스케줄우선순위 정보 UPDATE
                inRec = JDTORecordFactory.getInstance().create();
                inRec.setField("YD_SCH_CD", szSchCd);
                inRec.setField("MODIFIER", "SYSTEM");
                inRec.setField("YD_SCH_PRIOR", szSchPrior);
                intRtnVal = ydWrkbookDao.updYdWrkbook_YD_SCH_PRIOR(inRec);

                //크레인 스케줄 Table 업데이트
                //리스케줄 대상크레인이 주작업 크레인인 경우
                if(recTemp.getFieldString("WRK_CRN_YN").equals("Y")){
                    intRtnVal = ydCrnSchDao.updYdCrnschReSch(recTemp, 0);

                }else{
                    //리스케줄 대상크레인이 보조작업 크레인인 경우
                    intRtnVal = ydCrnSchDao.updYdCrnschReSch(recTemp, 1);
                }
            }//end of for


            for(int j=0; j<szArrEqpId.length; j++){
                szMsg = "설비ID : " + szArrEqpId[j];
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                //대체된 설비 ID의 작업상태가 'W'일경우 작업지시 전문 전송
                if(szArrEqpId[j]== null || szArrEqpId[j].equals("")){

                } else {
                    outRecSet = JDTORecordFactory.getInstance().createRecordSet("");

                    if(this.Y1ChkGetEqp(szArrEqpId[j],outRecSet)){
                        inRec = JDTORecordFactory.getInstance().create();
                        outRecSet.first();
                        inRec = outRecSet.getRecord();


                        if(ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_IDLE)){

                            //------------------------------------------------------------------------------------------------
                            // C연주 크레인 작업지시 호출
                            //------------------------------------------------------------------------------------------------
                            inRec = JDTORecordFactory.getInstance().create();
//SJH03004
                            inRec.setField("MSG_ID",           "YDYDJ640");
                            inRec.setField("YD_EQP_ID", szArrEqpId[j]);
                            inRec.setField("YD_WRK_PROG_STAT", YdConstant.YD_EQP_STAT_IDLE);

                            szMsg = "C연주 크레인 작업지시 호출 크레인  " + szArrEqpId[j]  ;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                            ejbConn = new EJBConnector("default", this);
                            ejbConn.trx("CraneLdHdSeEJB", "procY1CrnWrkOrdReq", inRec);
//                          ydDelegate.sendMsg(inRec);
                            //------------------------------------------------------------------------------------------------

                            // Flex(실시간작업을위함)
                            JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
                            recFlex.setField("YD_GP",  YdConstant.YD_GP_C_SLAB_YARD);
                            recFlex.setField("YD_EQP_ID",  szArrEqpId[j]);
                            ydUtils.putYdFlexCrnWrk("", recFlex);

                        }
                    }
                }
            }


        }catch(Exception e){

            szMsg="작업예약 및 크레인 스케줄 업데이트 Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }


        szMsg="작업예약 및 크레인 스케줄 업데이트("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y1UpdWbookCrnsch()

    /**
     * 오퍼레이션명 : 설비상태 체크
     *
     * @param   String szEqpId 설비ID
     * @return boolean true(설비사용가능), false(설비사용불가)
     * @throws JDTOException
     */
    public boolean Y1EqpStatCheck(String szEqpId)throws JDTOException  {

        //리턴값(boolean)
        boolean blnRtnVal      = false;
        //메세지
        String szMsg           = null;
        //메소드명
        String szMethodName    = "Y1EqpStatCheck";
        //설비상태
        String szYD_EQP_STAT   = null;
        //야드설비작업상태
        String szYD_EQP_WRK_MODE = null;
        //레코드 선언
        JDTORecord recPara     = null;
        //레코드셋 선언
        JDTORecordSet rsResult = null;

        try {
            //레코드 생성
            recPara = JDTORecordFactory.getInstance().create();
            //레코드셋 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //설비ID를 작업크레인으로 설정
            recPara.setField("YD_EQP_ID", szEqpId);

            //설비 체크 및 데이터 조회
            blnRtnVal = this.Y1ChkGetEqp(szEqpId, rsResult);
            if (!blnRtnVal) return blnRtnVal;

            //레코드 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //설비상태
            szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
            //야드설비작업상태
            szYD_EQP_WRK_MODE = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_MODE");

            //크레인의 상태가 'T'이면 false 리턴.
            //상수 수정 [2009.12.03 - 이현성]
            if (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {

                szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT + ") 입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;
            } else if (szYD_EQP_WRK_MODE.equals("2")) {
                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                 * OFF-LINE 체크기능 추가 - 임춘수 추가 2009.06.17
                 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                szMsg = "설비ID(" + szEqpId + ")의 야드설비작업상태(" + szYD_EQP_STAT + " : OFF-LINE ) 입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;
            } else {

                blnRtnVal = true;

            }
        } catch(Exception e) {
            szMsg = "설비상태 체크 중 예외발생! 예외메세지: " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            blnRtnVal = false;
        }
        return blnRtnVal;

    } //end of Y1EqpStatCheck

    /**
     * 오퍼레이션명 : 설비 유무체크 및 조회결과 데이터 반환
     *
     * @param  String        szEqpId  설비ID
     *         JDTORecordSet rsResult 결과레코드셋
     * @return boolean       true(성공), false(실패)
     * @throws JDTOException
     */
    public boolean Y1ChkGetEqp(String szEqpId, JDTORecordSet rsResult)throws JDTOException  {

        //설비 DAO
        YdEqpDao ydEqpDao     = new YdEqpDao();
        //리턴값(boolean)
        boolean blnRtnVal     = false;
        //리턴값(int)
        int intRtnVal         = 0;
        //메소드명
        String szMethodName   = "Y1ChkGetEqp";
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
    } //end of Y1ChkGetEqp

    /**
     * 오퍼레이션명 : A후판 크레인 리스케줄
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY3CrnReSch(JDTORecord msgRecord)throws JDTOException  {

        JDTORecordSet outRecSet     = null;
        JDTORecordSet rsReSchResult = JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecord recTemp          = null;

        int intRtnVal                   = 0 ;
        String szMsg                    = "";
        String szMethodName             = "procY3CrnReSch";

        String szEqpId                  = "";
        String szYdGp                   = "";
        String szYdBayGp                = "";

        boolean bRtnCheck               = true;

        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null){
            szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }

        if(bDebugFlag){
            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        try{

            //파라미터로 넘어온 설비 ID로 설비 상태를 Check
            szEqpId   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");

            bRtnCheck = this.Y3EqpStatCheck(szEqpId);

            //설비아이디의 야드구분 동구분을 빼서 스케줄기준Table조회
            szYdGp    = szEqpId.substring(0,1);
            szYdBayGp = szEqpId.substring(1,2);
            recTemp = JDTORecordFactory.getInstance().create();
            recTemp.setField("YD_GP",     szYdGp);
            recTemp.setField("YD_BAY_GP", szYdBayGp);
            outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
            intRtnVal = ydSchRuleDao.getYdSchrule(recTemp, outRecSet, 7);
            if(intRtnVal <= 0) {
                szMsg="스케줄기준이 조회가 되지않았습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            if(bRtnCheck == false) {
                //1. 고장시 고장 리스케줄 메소드 호출
                intRtnVal = this.Y3DisableReSch(szEqpId, outRecSet, rsReSchResult);
                if(intRtnVal == -1) {
                    szMsg="고장 리스케줄 처리 Error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }

            }else{
                //2. 정상시 복구 리스케줄 메소드 호출
                intRtnVal = this.Y3ResPairReSch(szEqpId, outRecSet, rsReSchResult);
                if(intRtnVal == -1) {
                    szMsg="복구 리스케줄 처리 Error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }
            }

            //3. 호출한 메소드의 결과값으로 작업예약 Table및 크레인 스케줄 Table업데이트 메소드 호출
            intRtnVal = this.Y3UpdWbookCrnsch(rsReSchResult);
            if(intRtnVal == -1) {
                szMsg="작업예약 및 크레인 스케줄 등록 중 Error";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

        }catch(Exception e){

            szMsg="Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }

        szMsg="크레인 리스케줄("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return;

    } //end of procY3CrnReSch()

    /**
     * 오퍼레이션명 : 고장 리스케줄
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param szEqpId, rsSchRule, rsReSchResult
     * @return
     * @throws JDTOException
     */
    public int Y3DisableReSch(String szEqpId, JDTORecordSet rsSchRule, JDTORecordSet rsReSchResult)throws JDTOException  {

        JDTORecord recTemp              = null;
        JDTORecord recResult            = null;
        JDTORecordSet rsResult          = JDTORecordFactory.getInstance().createRecordSet("Temp");

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y3DisableReSch";

        String szWrkCrn                 = "";
        String szAltCrn                 = "";

        try{

            for(int Loop_i = 1; Loop_i <= rsSchRule.size(); Loop_i++) {
                rsSchRule.absolute(Loop_i);
                recTemp = JDTORecordFactory.getInstance().create();
                recTemp.setRecord(rsSchRule.getRecord());
                szWrkCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_CRN");
                szAltCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_ALT_CRN");

                recResult = JDTORecordFactory.getInstance().create();
                //설비아이디가 스케줄 기준의 주크레인과 같거나 대체크레인과 같을 경우
                if(szEqpId.equals(szWrkCrn)) {
                    //새로운 레코드 셋에 등록한다.(스케줄 코드, 설비ID, 스케줄우선순위)
                    recResult.setField("YD_SCH_CD", recTemp.getFieldString("YD_SCH_CD"));
                    recResult.setField("YD_EQP_ID", recTemp.getFieldString("YD_ALT_CRN"));
                    recResult.setField("YD_SCH_PRIOR", recTemp.getFieldString("YD_ALT_CRN_PRIOR"));
                    recResult.setField("WRK_CRN_YN", "Y");
                    rsResult.addRecord(recResult);

                }else if (szEqpId.equals(szAltCrn)) {
                    //새로운 레코드 셋에 등록한다.
                    recResult.setField("YD_SCH_CD", recTemp.getFieldString("YD_SCH_CD"));
                    recResult.setField("YD_SCH_PRIOR", recTemp.getFieldString("YD_ALT_CRN_PRIOR"));
                    recResult.setField("WRK_CRN_YN", "N");
                    rsResult.addRecord(recResult);
                }

            }//end of for
            rsReSchResult.addAll(rsResult);

        }catch(Exception e){
            szMsg="고장 리스케줄 처리 Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }

        szMsg="고장 리스케줄 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y3DisableReSch()

    /**
     * 오퍼레이션명 : 복구리스케줄
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param szEqpId, rsSchRule, rsReSchResult
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y3ResPairReSch(String szEqpId, JDTORecordSet rsSchRule, JDTORecordSet rsReSchResult)throws JDTOException  {

        JDTORecord recTemp              = null;
        JDTORecord recResult            = null;
        JDTORecordSet rsResult          = JDTORecordFactory.getInstance().createRecordSet("Temp");

        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y3ResPairReSch";

        String szWrkCrn                 = "";
        String szAltCrn                 = "";

        try{

            for(int Loop_i = 1; Loop_i <= rsSchRule.size(); Loop_i++) {
                rsSchRule.absolute(Loop_i);
                recTemp = JDTORecordFactory.getInstance().create();
                recTemp.setRecord(rsSchRule.getRecord());
                szWrkCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_CRN");
                szAltCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_ALT_CRN");

                recResult = JDTORecordFactory.getInstance().create();
                //설비아이디가 스케줄 기준의 주크레인과 같거나 대체크레인과 같을 경우
                if(szEqpId.equals(szWrkCrn)) {
                    //새로운 레코드 셋에 등록한다.(스케줄 코드, 설비ID, 스케줄우선순위)
                    recResult.setField("YD_SCH_CD", recTemp.getFieldString("YD_SCH_CD"));
                    recResult.setField("YD_EQP_ID", recTemp.getFieldString("YD_WRK_CRN"));
                    recResult.setField("YD_SCH_PRIOR", recTemp.getFieldString("YD_WRK_CRN_PRIOR"));
                    recResult.setField("WRK_CRN_YN", "Y");
                    rsResult.addRecord(recResult);

                }else if (szEqpId.equals(szAltCrn)) {
                    //새로운 레코드 셋에 등록한다.
                    recResult.setField("YD_SCH_CD", recTemp.getFieldString("YD_SCH_CD"));
                    recResult.setField("YD_SCH_PRIOR", recTemp.getFieldString("YD_WRK_CRN_PRIOR"));
                    recResult.setField("WRK_CRN_YN", "N");
                    rsResult.addRecord(recResult);
                }

            }//end of for
            rsReSchResult.addAll(rsResult);

        }catch(Exception e){

            szMsg="복구 리스케줄 처리 Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }

        szMsg="복구 리스케줄 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y3ResPairReSch()

    /**
     * 오퍼레이션명 : A후판 크레인 리스케줄 작업예약 및 크레인스케줄 등록
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param rsCrnSch
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y3UpdWbookCrnsch(JDTORecordSet rsCrnSch)throws JDTOException  {

        YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
        YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
        JDTORecord    recTemp           = null;
        JDTORecord    inRec             = null;

        JDTORecordSet outRecSet         = null;
        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y3UpdWbookCrnsch";

        String szSchCd                  = "";
        String szSchPrior               = "";

        String[] szArrEqpId             = new String[5];
        String szEqpId                  = "";
        int szEqpCount                  = 0;
        boolean bIsBe                   = false;
        EJBConnector ejbConn = null;


        try{

            for(int Loop_i = 1; Loop_i <= rsCrnSch.size(); Loop_i++) {
                rsCrnSch.absolute(Loop_i);
                recTemp = JDTORecordFactory.getInstance().create();
                recTemp.setRecord(rsCrnSch.getRecord());

                szSchCd    = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");
                szSchPrior = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_PRIOR");

                // 설비ID를 가져옴
                szEqpId = ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_ID");

                if(!szEqpId.equals("")){

                    for(int i=0; i<szArrEqpId.length; i++){
                        if(szEqpId.equals(szArrEqpId[i])){
                            bIsBe = true;
                            break;
                        }else{
                            bIsBe = false;
                        }
                    }

                    if(bIsBe == false){
                        szArrEqpId[szEqpCount] = ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_ID");
                        szEqpCount++;
                    }
                }


                //작업예약TABLE 스케줄우선순위 정보 UPDATE
                inRec = JDTORecordFactory.getInstance().create();
                inRec.setField("YD_SCH_CD", szSchCd);
                inRec.setField("MODIFIER", "SYSTEM");
                inRec.setField("YD_SCH_PRIOR", szSchPrior);
                intRtnVal = ydWrkbookDao.updYdWrkbook_YD_SCH_PRIOR(inRec);

                //크레인 스케줄 Table 업데이트
                //리스케줄 대상크레인이 주작업 크레인인 경우
                if(recTemp.getFieldString("WRK_CRN_YN").equals("Y")){
                    intRtnVal = ydCrnSchDao.updYdCrnschReSch(recTemp, 0);

                }else{
                    //리스케줄 대상크레인이 보조작업 크레인인 경우
                    intRtnVal = ydCrnSchDao.updYdCrnschReSch(recTemp, 1);
                }
            }//end of for


            for(int j=0; j<szArrEqpId.length; j++){
                szMsg = "설비ID : " + szArrEqpId[j];
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                //대체된 설비 ID의 작업상태가 'W'일경우 작업지시 전문 전송

                if(szArrEqpId[j]== null || szArrEqpId[j].equals("")){

                } else {
                    outRecSet = JDTORecordFactory.getInstance().createRecordSet("");

                    if(this.Y3ChkGetEqp(szArrEqpId[j],outRecSet)){
                        inRec = JDTORecordFactory.getInstance().create();
                        outRecSet.first();
                        inRec = outRecSet.getRecord();

                        if(ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_IDLE)){

                            //------------------------------------------------------------------------------------------------
                            // A후판  크레인 작업지시 호출
                            //------------------------------------------------------------------------------------------------
                            inRec = JDTORecordFactory.getInstance().create();
//SJH03004

                            inRec.setField("MSG_ID",           "YDYDJ641");
                            inRec.setField("YD_EQP_ID", szArrEqpId[j]);
                            inRec.setField("YD_WRK_PROG_STAT", YdConstant.YD_EQP_STAT_IDLE);

                            szMsg = "A후판  크레인 작업지시 호출 크레인  " + szArrEqpId[j]  ;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                            ejbConn = new EJBConnector("default", this);
                            ejbConn.trx("CraneLdHdSeEJB", "procY3CrnWrkOrdReq", inRec);

//                          ydDelegate.sendMsg(inRec);
                            //------------------------------------------------------------------------------------------------

                            // Flex(실시간작업을위함)
                            JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
                            recFlex.setField("YD_GP",  YdConstant.YD_GP_A_PLATE_SLAB_YARD);
                            recFlex.setField("YD_EQP_ID",  szArrEqpId[j]);
                            ydUtils.putYdFlexCrnWrk("", recFlex);
                        }
                    }
                }
            }

        }catch(Exception e){

            szMsg="작업예약 및 크레인 스케줄 업데이트 Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }

        szMsg="작업예약 및 크레인 스케줄 업데이트("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y3UpdWbookCrnsch()

    /**
     * 오퍼레이션명 : 설비상태 체크
     *
     * @param   String szEqpId 설비ID
     * @return boolean true(설비사용가능), false(설비사용불가)
     * @throws JDTOException
     */
    public boolean Y3EqpStatCheck(String szEqpId)throws JDTOException  {

        //리턴값(boolean)
        boolean blnRtnVal      = false;
        //메세지
        String szMsg           = null;
        //메소드명
        String szMethodName    = "Y3EqpStatCheck";
        //설비상태
        String szYD_EQP_STAT   = null;
        //야드설비작업상태
        String szYD_EQP_WRK_MODE = null;
        //레코드 선언
        JDTORecord recPara     = null;
        //레코드셋 선언
        JDTORecordSet rsResult = null;

        try {
            //레코드 생성
            recPara = JDTORecordFactory.getInstance().create();
            //레코드셋 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //설비ID를 작업크레인으로 설정
            recPara.setField("YD_EQP_ID", szEqpId);

            //설비 체크 및 데이터 조회
            blnRtnVal = this.Y3ChkGetEqp(szEqpId, rsResult);
            if (!blnRtnVal) return blnRtnVal;

            //레코드 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //설비상태
            szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
            //야드설비작업상태
            szYD_EQP_WRK_MODE = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_MODE");

            //크레인의 상태가 'T'이면 false 리턴.
            //상수값으로 변경 - [2009.12.03 - 이현성]
            if (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {

                szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT + ") 입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if (szYD_EQP_WRK_MODE.equals("2")) {
                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                 * OFF-LINE 체크기능 추가 - 임춘수 추가 2009.06.18
                 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                szMsg = "설비ID(" + szEqpId + ")의 야드설비작업상태(" + szYD_EQP_STAT + " : OFF-LINE ) 입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;
            } else {

                blnRtnVal = true;

            }
        } catch(Exception e) {
            szMsg = "설비상태 체크 중 예외발생! 예외메세지: " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            blnRtnVal = false;
        }
        return blnRtnVal;

    } //end of Y3EqpStatCheck

    /**
     * 오퍼레이션명 : 설비 유무체크 및 조회결과 데이터 반환
     *
     * @param  String        szEqpId  설비ID
     *         JDTORecordSet rsResult 결과레코드셋
     * @return boolean       true(성공), false(실패)
     * @throws JDTOException
     */
    public boolean Y3ChkGetEqp(String szEqpId, JDTORecordSet rsResult)throws JDTOException  {

        //설비 DAO
        YdEqpDao ydEqpDao     = new YdEqpDao();
        //리턴값(boolean)
        boolean blnRtnVal     = false;
        //리턴값(int)
        int intRtnVal         = 0;
        //메소드명
        String szMethodName   = "Y3ChkGetEqp";
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
    } //end of Y3ChkGetEqp

    /**
     * 오퍼레이션명 : 제품창고 크레인 리스케줄
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY4CrnReSch(JDTORecord msgRecord)throws JDTOException  {

        YdPlateCommDAO commDao = new YdPlateCommDAO();

        JDTORecordSet outRecSet = null;
        JDTORecordSet rsReSchResult = JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecord recTemp = null;

        int intRtnVal                   = 0 ;
        String szMsg                    = "";
        String szMethodName             = "procY4CrnReSch";

        String szEqpId                  = "";
        String szYdGp                   = "";
        String szYdBayGp                = "";

        boolean bRtnCheck               = true;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// Y4UpdWbookCrnsch call 시  logId 항목 추가 개선
// String logId 						= msgRecord.getFieldString("LOG_ID");		// logid get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        try{

            szMsg="============> 제품창고 크레인 리스케줄 <===================== " ; //TEST
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);   //TEST
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);   //TEST

            //파라미터로 넘어온 설비 ID로 설비 상태를 Check
            szEqpId   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");  //입력받은 설비
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// Y4EqpStatCheck call 시  logId 항목 추가 개선
//          bRtnCheck = this.Y4EqpStatCheck(szEqpId); //bRtnCheck 가 false 이면 고장 , true 면 정상
            bRtnCheck = this.Y4EqpStatCheck(szEqpId, logId); //bRtnCheck 가 false 이면 고장 , true 면 정상
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

            //설비아이디의 야드구분 동구분을 빼서 스케줄기준Table조회
            szYdGp      = szEqpId.substring(0,1);
            szYdBayGp   = szEqpId.substring(1,2);

            JDTORecord recParaTemp = JDTORecordFactory.getInstance().create();

            //-- 통합 크레인 스케줄 기준 조회 -----------------------------------------------------------------start--
            outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
            recTemp = JDTORecordFactory.getInstance().create();

            recTemp.setField("YD_SCH_CD", szYdGp + szYdBayGp );
            recTemp.setField("YD_EQP_ID", szEqpId );

            intRtnVal = commDao.select(recTemp, outRecSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0099");

            if(intRtnVal == 0) {
                szMsg="스케줄기준이 조회가 되지않았습니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return;
            }

            for(int Loop_i = 1; Loop_i <= outRecSet.size(); Loop_i++) {
                outRecSet.absolute(Loop_i);
                recParaTemp.setRecord(outRecSet.getRecord());

                // 2021. 4. 09
                // 현업요청 차량 출하/입고 스케쥴의 경우 리스케쥴에서 제외시킬것
                if( !"Y".equals(ydDaoUtils.paraRecChkNull(recParaTemp, "RE_SCH_SKIP_YN")) ){
                    recTemp = JDTORecordFactory.getInstance().create();

                    recTemp.setField("YD_SCH_CD"        , ydDaoUtils.paraRecChkNull(recParaTemp, "YD_SCH_CD") );
                    recTemp.setField("YD_EQP_ID"        , ydDaoUtils.paraRecChkNull(recParaTemp, "WRK_CRN") );//변경될 크레인
                    recTemp.setField("YD_SCH_PRIOR"     , ydDaoUtils.paraRecChkNull(recParaTemp, "CRN_PRIOR") );
                    recTemp.setField("WRK_CRN_YN"       , "Y");
                    recTemp.setField("YD_EQP_ID_OLD"        , szEqpId );//변경전 크레인  2024.01.15 고장크레인 대체시 TO위치 변경 로직 개선건.

                    rsReSchResult.addRecord(recTemp);
                }

            }
            //-- 통합 크레인 스케줄 기준 조회 ------------------------------------------------------------------end---

            //3. 호출한 메소드의 결과값으로 작업예약 Table및 크레인 스케줄 Table업데이트 메소드 호출
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// Y4UpdWbookCrnsch call 시  logId 항목 추가 개선
//          intRtnVal = this.Y4UpdWbookCrnsch(rsReSchResult);
            intRtnVal = this.Y4UpdWbookCrnsch(rsReSchResult,logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        }catch(Exception e){

            szMsg="Error:" +e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            throw new JDTOException(szMsg);
        }
        szMsg="크레인 리스케줄("+szMethodName+") 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
        return;

    } //end of procY4CrnReSch()

    /**
     * 오퍼레이션명 : 고장 리스케줄
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param szEqpId, rsSchRule, rsReSchResult
     * @return
     * @throws JDTOException
     */
    public int Y4DisableReSch(String szEqpId, JDTORecordSet rsSchRule, JDTORecordSet rsReSchResult)throws JDTOException  {

        JDTORecord recTemp              = null;
        JDTORecord recResult            = null;
        JDTORecordSet rsResult          = JDTORecordFactory.getInstance().createRecordSet("Temp");
        String szMsg                    = "";
        String szMethodName             = "Y4DisableReSch";
        String szWrkCrn                 = "";
        String szAltCrn                 = "";

        int intRtnVal                   = 0 ;
        try{

            for(int Loop_i = 1; Loop_i <= rsSchRule.size(); Loop_i++) {
                rsSchRule.absolute(Loop_i);
                recTemp = JDTORecordFactory.getInstance().create();
                recTemp.setRecord(rsSchRule.getRecord());
                szWrkCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_CRN");
                szAltCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_ALT_CRN");

                recResult = JDTORecordFactory.getInstance().create();
                //설비아이디가 스케줄 기준의 주크레인과 같거나 대체크레인과 같을 경우
                if(szEqpId.equals(szWrkCrn)) {
                    //새로운 레코드 셋에 등록한다.(스케줄 코드, 설비ID, 스케줄우선순위)
                    recResult.setField("YD_SCH_CD",     recTemp.getFieldString("YD_SCH_CD"));
                    recResult.setField("YD_EQP_ID",     recTemp.getFieldString("YD_ALT_CRN"));
                    recResult.setField("YD_SCH_PRIOR",  recTemp.getFieldString("YD_ALT_CRN_PRIOR"));
                    recResult.setField("WRK_CRN_YN", "Y");
                    rsResult.addRecord(recResult);

                }else if (szEqpId.equals(szAltCrn)) {
                    //새로운 레코드 셋에 등록한다.
                    recResult.setField("YD_SCH_CD",     recTemp.getFieldString("YD_SCH_CD"));
                    recResult.setField("YD_SCH_PRIOR",  recTemp.getFieldString("YD_ALT_CRN_PRIOR"));
                    recResult.setField("WRK_CRN_YN", "N");
                    rsResult.addRecord(recResult);
                }
            }//end of for
            rsReSchResult.addAll(rsResult);
        }catch(Exception e){
            szMsg="고장 리스케줄 처리 Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }

        szMsg="고장 리스케줄 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y4DisableReSch()

    /**
     * 오퍼레이션명 : 복구리스케줄
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param szEqpId, rsSchRule, rsReSchResult
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y4ResPairReSch(String szEqpId, JDTORecordSet rsSchRule, JDTORecordSet rsReSchResult)throws JDTOException  {

        JDTORecord recTemp              = null;
        JDTORecord recResult            = null;
        JDTORecordSet rsResult          = JDTORecordFactory.getInstance().createRecordSet("Temp");
        String szMsg                    = "";
        String szMethodName             = "Y4ResPairReSch";
        String szWrkCrn                 = "";
        String szAltCrn                 = "";
        int intRtnVal                   = 0 ;

        try{

            for(int Loop_i = 1; Loop_i <= rsSchRule.size(); Loop_i++) {
                rsSchRule.absolute(Loop_i);
                recTemp = JDTORecordFactory.getInstance().create();
                recTemp.setRecord(rsSchRule.getRecord());
                szWrkCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_CRN");
                szAltCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_ALT_CRN");

                recResult = JDTORecordFactory.getInstance().create();
                //설비아이디가 스케줄 기준의 주크레인과 같거나 대체크레인과 같을 경우
                if(szEqpId.equals(szWrkCrn)) {
                    //새로운 레코드 셋에 등록한다.(스케줄 코드, 설비ID, 스케줄우선순위)
                    recResult.setField("YD_SCH_CD",     recTemp.getFieldString("YD_SCH_CD"));
                    recResult.setField("YD_EQP_ID",     recTemp.getFieldString("YD_WRK_CRN"));
                    recResult.setField("YD_SCH_PRIOR",  recTemp.getFieldString("YD_WRK_CRN_PRIOR"));
                    recResult.setField("WRK_CRN_YN", "Y");
                    rsResult.addRecord(recResult);

                }else if (szEqpId.equals(szAltCrn)) {
                    //새로운 레코드 셋에 등록한다.
                    recResult.setField("YD_SCH_CD",     recTemp.getFieldString("YD_SCH_CD"));
                    recResult.setField("YD_SCH_PRIOR",  recTemp.getFieldString("YD_WRK_CRN_PRIOR"));
                    recResult.setField("WRK_CRN_YN", "N");
                    rsResult.addRecord(recResult);
                }

            }//end of for
            rsReSchResult.addAll(rsResult);
        }catch(Exception e){
            szMsg="복구 리스케줄 처리 Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }
        szMsg="복구 리스케줄 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y4ResPairReSch()

    /**
     * 오퍼레이션명 :후판제품 크레인 리스케줄 작업예약 및 크레인스케줄 등록 // 2024.09.02 로그 개선
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param rsCrnSch
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y4UpdWbookCrnsch(JDTORecordSet rsCrnSch, String logId)throws JDTOException  {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// Y4UpdWbookCrnsch argument 에 logId 항목 추가 개선
// public int Y4UpdWbookCrnsch(JDTORecordSet rsCrnSch)throws JDTOException  {
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
        YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
        YdPlateCommDAO commDao = new YdPlateCommDAO();

        boolean isReSearch =false; // 크레인 고장범위  to위치면 재탐색여부 flag

        JDTORecord    recTemp           = null;
        JDTORecord    recTemp2           = null;
        JDTORecord    inRec             = null;
        JDTORecord    inRec2             = null;
        JDTORecordSet outRecSet         = null;
        JDTORecordSet outRecSet2         = null;
        int intRtnVal                   = 0 ;
        int intRtnVal2                  = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y4UpdWbookCrnsch";
        String szRtnMsg                 = "";

        String szYD_UP_WO_LOC           = "";
        String szYD_DN_WO_LOC           = "";

        String szSchCd                  = "";
        String szSchPrior               = "";
        String[] szArrEqpId             = new String[5];
        String szEqpId                  = "";
        String szEqpId_old              = "";
        int szEqpCount                  = 0;
        boolean bIsBe                   = false;
        EJBConnector ejbConn = null;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// logId Empty 이면 logId 신규 생성 개선
if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg="작업예약 및 크레인 스케줄 업데이트("+szMethodName+") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        try{

            for(int Loop_i = 1; Loop_i <= rsCrnSch.size(); Loop_i++) {

                rsCrnSch.absolute(Loop_i);
                recTemp = JDTORecordFactory.getInstance().create();
                recTemp.setRecord(rsCrnSch.getRecord());

                szSchCd    = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");
                szSchPrior = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_PRIOR");

                // 설비ID를 가져옴
                szEqpId = ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_ID");//변경될 크레인
                szEqpId_old = ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_ID_OLD");//변경전 크레인  2024.01.15 크레인대체시, TO위치 재탐색 로직 개선관련 항목 추가.

                if(!szEqpId.equals("")){

                    for(int i=0; i<szArrEqpId.length; i++){
                        if(szEqpId.equals(szArrEqpId[i])){
                            bIsBe = true;
                            break;
                        }else{
                            bIsBe = false;
                        }
                    }

                    if(bIsBe == false){
                        szArrEqpId[szEqpCount] = ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_ID");
                        szEqpCount++;
                    }
                }

                //작업예약TABLE 스케줄우선순위 정보 UPDATE
                inRec = JDTORecordFactory.getInstance().create();
                inRec.setField("YD_SCH_CD", szSchCd);
                inRec.setField("MODIFIER", "SYSTEM");
                inRec.setField("YD_SCH_PRIOR", szSchPrior);
                intRtnVal = ydWrkbookDao.updYdWrkbook_YD_SCH_PRIOR(inRec);

                //크레인 스케줄 Table 업데이트
                //리스케줄 대상크레인이 주작업 크레인인 경우
                if(recTemp.getFieldString("WRK_CRN_YN").equals("Y")){
                    intRtnVal = ydCrnSchDao.updYdCrnschReSch(recTemp, 0);//여기서 대상크레인 변경

                }else{
                    //리스케줄 대상크레인이 보조작업 크레인인 경우
                    intRtnVal = ydCrnSchDao.updYdCrnschReSch(recTemp, 1); //보조작업은 대상크레인 미변경하는듯.
                }


                //-----------------------------------------------------------------
                // 후판제품 X,Y POSITION UPDATE
                //
                // 1. 스케줄 코드로 편성된 스케줄 ID 조회(작업대기인 정보만 조회한다.)
                // 2. 스케줄 ID 별 정보 조회 후 UPDATE 로직 호출함
                //-----------------------------------------------------------------
                outRecSet   = JDTORecordFactory.getInstance().createRecordSet("");

                inRec       = JDTORecordFactory.getInstance().create();

                inRec.setField("YD_SCH_CD", szSchCd);
                inRec.setField("YD_WRK_PROG_STAT", YdConstant.YD_EQP_STAT_IDLE);
                /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnSchBySchCd*/
                intRtnVal = ydCrnSchDao.getYdCrnsch(inRec, outRecSet, 24);

                if( intRtnVal > 0 ){  //대상 스케줄이 있으면
                    outRecSet.first();
                    inRec = JDTORecordFactory.getInstance().create();

                    do{
                        inRec = outRecSet.getRecord();

                        szYD_UP_WO_LOC = inRec.getFieldString("YD_UP_WO_LOC");
                        szYD_DN_WO_LOC = inRec.getFieldString("YD_DN_WO_LOC");

                        //-----------------------------------------------------------------
                        // 후판제품 스케줄 TO위치 재계산
                        //
                        // 1.크레인 고장 등록 후, 대체크레인 할당 후, TO위치를 기존 고장등록 크레인의 정지 범위와 겹치는지 체크 후, 겹치면 신규 범위로 TO위치 재할당해준다.
                        //-----------------------------------------------------------------
                        if(recTemp.getFieldString("WRK_CRN_YN").equals("Y")){  //스케줄내 크레인 변경시에만 수행되도록(주작업시에만 변경하기때문에 주작업 조건 체크)
                            if(szEqpId!=""){
                                boolean  isNewToAddr=false; //TO위치 재탐색할지 여부 선택.
                                String szYD_DN_WO_LOC_OLD=""; //원래 권하위치.
                                String szYD_DN_WO_LOC_NEW=""; //변경할 권하위치.

                                szYD_DN_WO_LOC_OLD=inRec.getFieldString("YD_DN_WO_LOC");
                                szYD_DN_WO_LOC_NEW=inRec.getFieldString("YD_DN_WO_LOC");  //초기에는 AS-IS, TO-BE 권하위치 동일하게 셋팅.
                              //권하위치 재계산 후 스케줄 권하지시위치 수정.

                                outRecSet2 = JDTORecordFactory.getInstance().createRecordSet("");
                                recTemp2 = JDTORecordFactory.getInstance().create();

                                recTemp2.setField("YD_CRN_SCH_ID", inRec.getFieldString("YD_CRN_SCH_ID"));
                                recTemp2.setField("YD_EQP_ID", szEqpId_old);  //스케줄에 이미 설비 변경되있으므로, 변경전 고장설비 기준 범위 체크
                                //설비는 스케줄에 등록된 설비 기준으로 조회
                                //recTemp2.setField("YD_EQP_ID", szEqpId );


                              //1/2후판 판단(설비로)
                                String szYdGp="";
                                String eqpNum=szEqpId.substring(5,6);//대체설비


                                //권하위치가 고장정지 범위인지 체크후 해당 범위면, TO위치 재조정로직 수행.
                                if(eqpNum.equals("1")|| eqpNum.equals("2")){
                                    szYdGp="2"; //2후판
                                    //T00051
                                    //고장 FROM/TO위치, 대체 FROM/TO위치, TO위치 재탐색 여부 등 조회
                                    //인자로 변경 이전 설비 넣어줘야함. 스케줄에이미 변경설비로 업데이트된 상태라서
                                    intRtnVal2 = commDao.select(recTemp2, outRecSet2, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getAltToLocFor2");
                                }
                                else if(eqpNum.equals("3")|| eqpNum.equals("4")|| eqpNum.equals("5")){
                                    szYdGp="1"; //1후판
                                    //T00052
                                    //인자로 변경 이전 설비 넣어줘야함. 스케줄에이미 변경설비로 업데이트된 상태라서
                                    intRtnVal2 = commDao.select(recTemp2, outRecSet2, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getAltToLocFor1");
                                }
                                else{
                                    szMsg="설비명 이상으로 TO위치 재계산 불가:"+szEqpId;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                }

                                //읽어온 데이터값 확인 후 처리   ALT_FLAG  AL_FROM, AL_TO  YD_CRN_SCH_ID
                                if(intRtnVal2>0){
                                    outRecSet2.first();
                                    inRec2=outRecSet2.getRecord();

                                    if(inRec2.getFieldString("ALT_FLAG").equals("Y")){
                                        isNewToAddr=true;
                                    }
                                }

                                if(isNewToAddr==true){  //to위치 재탐색 로직 수행.
                                    String szAL_FROM ="";
                                    String szAL_TO ="";
                                    szAL_FROM=inRec2.getFieldString("AL_FROM");  //대체 FROM
                                    szAL_TO=inRec2.getFieldString("AL_TO");  //대체 TO

                                    JDTORecordSet rsCrnwrkmtl= JDTORecordFactory.getInstance().createRecordSet("Temp");
                                    YdCrnWrkMtlDao ydCrnWrkMtlDao   = new YdCrnWrkMtlDao();
                                    //recTemp2  인자: YD_CRN_SCH_ID
                                    intRtnVal2=ydCrnWrkMtlDao.getYdCrnwrkmtl(recTemp2, rsCrnwrkmtl, 6);

                                    JDTORecordSet rsCrnSch2=JDTORecordFactory.getInstance().createRecordSet("");
                                    JDTORecord  recCrnSch=null;
                                    //szEqpId:변경설비, szEqpId_old:이전설비

                                    //com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschByEqpIdandWBookId
                                    //가져온스케줄중, sch_id가 동일한것만 추가 필터되도록 변경 필요.
                                    //com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschByEqpIdandSchId 로 변경 21번->57번 신규
                                    //intRtnVal2=ydCrnSchDao.getYdCrnsch(recTemp2, rsCrnSch2, 21);
                                    intRtnVal2=ydCrnSchDao.getYdCrnsch(recTemp2, rsCrnSch2, 57);

                                    for(int i=1; i<=rsCrnSch2.size();i++){
                                        rsCrnSch2.absolute(i);
                                        recCrnSch=rsCrnSch2.getRecord();
                                    }

                                    //호출해서 권하위치 재계산 및 스케줄 내 권하위치 업데이트 수행.
                                    //TO위치 못찾으면 권하위치 변경X
                                    //ex:szAL_FROM:TC081501
                                    szRtnMsg=YdToLocDcsnUtil.procMainWrkToLocForPlateYdForChgCrn(rsCrnwrkmtl,recCrnSch,szAL_FROM,szAL_TO);


                                    if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
                                        szMsg="크레인스케줄 권하위치 변경 성공";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                                    }
                                    else{
                                        szMsg="크레인스케줄 권하위치 변경 실패"+szRtnMsg;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                                    }

                                }
                            }
                        }
                        //여기서 대체크레인의 경우 TO위치 재계산 로직 추가하면될듯.


                        if(szYD_UP_WO_LOC.length() != 8 || szYD_DN_WO_LOC.length() !=8){
                            szMsg="권상/권하 지시위치 정보가 올바르지않습니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                            continue;
                        }

                        inRec.setField("YD_UP_STK_COL_GP", szYD_UP_WO_LOC.substring(0, 6));
                        inRec.setField("YD_UP_STK_BED_NO", szYD_UP_WO_LOC.substring(6, 8));
                        inRec.setField("YD_DN_STK_COL_GP", szYD_DN_WO_LOC.substring(0, 6));
                        inRec.setField("YD_DN_STK_BED_NO", szYD_DN_WO_LOC.substring(6, 8));


                        szRtnMsg = CrnSchUtil.uptCrnSchXYCord(inRec);

                        if(szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
                            szMsg="후판제품 X,Y POSITION UPDATE 성공";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                        }else{
                            szMsg="후판제품 X,Y POSITION UPDATE 실패";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                        }

                    }while(outRecSet.next());
                }
            }//end of for

            for(int j=0; j<szArrEqpId.length; j++){
                szMsg = "설비ID : " + szArrEqpId[j];
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                //대체된 설비 ID의 작업상태가 'W'일경우 작업지시 전문 전송
                if(szArrEqpId[j]== null || szArrEqpId[j].equals("")){
					
                } else {
					
                    outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
                    
// 2024.09.02 로그 개선 관련 Y4ChkGetEqp Method call 시 argument 에 logId 항목 추가 개선
//                  if(this.Y4ChkGetEqp(szArrEqpId[j],outRecSet)){
                    if(this.Y4ChkGetEqp(szArrEqpId[j],outRecSet, logId)){
                        inRec = JDTORecordFactory.getInstance().create();
                        outRecSet.first();
                        inRec = outRecSet.getRecord();


                        if(ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_IDLE)){

                            //------------------------------------------------------------------------------------------------
                            // 후판제품 크레인 작업지시 호출
                            //------------------------------------------------------------------------------------------------
                            inRec = JDTORecordFactory.getInstance().create();
//SJH03004
                            inRec.setField("MSG_ID",           "YDYDJ642");

                            inRec.setField("YD_EQP_ID", szArrEqpId[j]);
                            inRec.setField("YD_WRK_PROG_STAT", YdConstant.YD_EQP_STAT_IDLE);

                            szMsg = "후판제품 크레인 작업지시  호출 크레인  " + szArrEqpId[j]  ;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// procY4CrnWrkOrdReq call 시  inRec 에 logId SET 추가 개선
                            inRec.setField("LOG_ID", logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

                            ejbConn = new EJBConnector("default", this);
                            ejbConn.trx("CraneLdHdSeEJB", "procY4CrnWrkOrdReq", inRec);

                            //------------------------------------------------------------------------------------------------
                            JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
                            //recFlex.setField("YD_GP",  YdConstant.YD_GP_PLATE_GDS_YARD);
                            recFlex.setField("YD_GP",  szArrEqpId[j].substring(0,1)); //--2013.02.14 추가 (3기)
                            recFlex.setField("YD_EQP_ID",  szArrEqpId[j]);
                            ydUtils.putYdFlexCrnWrk("", recFlex);
                            //------------------------------------------------------------------------------------------------

                        }
                    }
                }
            }
        }catch(Exception e){

            szMsg="작업예약 및 크레인 스케줄 업데이트 Error:" +e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            throw new JDTOException(szMsg);
        }
        szMsg="작업예약 및 크레인 스케줄 업데이트("+szMethodName+") 완료";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
        return intRtnVal = 1;
    } //end of Y4UpdWbookCrnsch()

    /**
     * 오퍼레이션명 : 설비상태 체크
     *
     * @param   String szEqpId 설비ID
     * @return boolean true(설비사용가능), false(설비사용불가)
     * @throws JDTOException
     */
    public boolean Y4EqpStatCheck(String szEqpId, String logId)throws JDTOException  {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// Y4EqpStatCheck argument 에 logId 항목 추가 개선
// public boolean Y4EqpStatCheck(String szEqpId)throws JDTOException  {
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        //리턴값(boolean)
        boolean blnRtnVal      = false;
        //메세지
        String szMsg           = null;
        //메소드명
        String szMethodName    = "Y4EqpStatCheck";
        //설비상태
        String szYD_EQP_STAT   = null;
        //레코드 선언
        JDTORecord recPara     = null;
        //레코드셋 선언
        JDTORecordSet rsResult = null;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// logId Empty 이면 logId 신규 생성 개선
if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "설비상태 체크(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        try {
            //레코드 생성
            recPara = JDTORecordFactory.getInstance().create();
            //레코드셋 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //설비ID를 작업크레인으로 설정
            recPara.setField("YD_EQP_ID", szEqpId);

            //설비 체크 및 데이터 조회
// 2024.09.02 로그 개선 관련 Y4ChkGetEqp Method call 시 argument 에 logId 항목 추가 개선
//          blnRtnVal = this.Y4ChkGetEqp(szEqpId, rsResult);
            blnRtnVal = this.Y4ChkGetEqp(szEqpId, rsResult, logId);
            if (!blnRtnVal) return blnRtnVal;

            //레코드 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //설비상태
            szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");

            //크레인의 상태가 'T'이면 false 리턴.
            //상수값으로 변경 - [2009.12.03 이현성]
            if (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {

                szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT + ") 입니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
                blnRtnVal = false;

            } else {

                blnRtnVal = true;

            }
        } catch(Exception e) {
            szMsg = "설비상태 체크 중 예외발생! 예외메세지: " + e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            blnRtnVal = false;
        }
        

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
szMsg = "설비상태 체크(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        
        return blnRtnVal;

    } //end of Y4EqpStatCheck

    /**
     * 오퍼레이션명 : 설비 유무체크 및 조회결과 데이터 반환
     *           2024.09.02 logId argument 추가
     *
     * @param  String        szEqpId  설비ID
     *         JDTORecordSet rsResult 결과레코드셋
     * @return boolean       true(성공), false(실패)
     * @throws JDTOException
     */

//    public boolean Y4ChkGetEqp(String szEqpId, JDTORecordSet rsResult)throws JDTOException  {
      public boolean Y4ChkGetEqp(String szEqpId, JDTORecordSet rsResult, String logId)throws JDTOException  {

        //설비 DAO
        YdEqpDao ydEqpDao     = new YdEqpDao();
        //리턴값(boolean)
        boolean blnRtnVal     = false;
        //리턴값(int)
        int intRtnVal         = 0;
        //메소드명
        String szMethodName   = "Y4ChkGetEqp";
        String szMsg          = null;

        //레코드 선언
        JDTORecord recPara        = null;
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선 START
// 기존 putLog -> putLogNew logId 출력 되게 개선
if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "설비 유무체크 및 조회결과 데이터 반환(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

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
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                blnRtnVal = false;

            } else if (intRtnVal == 1) {

                blnRtnVal = true;

            } else if (intRtnVal == 0) {

                szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 없습니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                blnRtnVal = false;

            } else if (intRtnVal == -2) {

                szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 parameter error 발생!";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                blnRtnVal = false;

            } else {

                szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 오류 발생!";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                blnRtnVal = false;

            }
        } catch(Exception e) {
            szMsg = "설비 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return blnRtnVal = false;
        }
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선 START
// 기존 putLog -> putLogNew logId 출력 되게 개선
szMsg = "설비 유무체크 및 조회결과 데이터 반환(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

        return blnRtnVal;
    } //end of Y4ChkGetEqp


  //---------------------------------------------------------------------------
} // end of class CrnReSchSeEJBBean
