/**
 * @(#)SlabJspSeEJBBean.java
 *
 * @version         1.0
 * @author          현대제철
 * @date            2012/11/14
 *
 * @description     이클래스는업무 야드맵을 관리하기 위한 Session Session EJB클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.01  2013/04/03   허철호      허철호      신규설비 추가
 * V1.02  2015/12/14   이준영      이준영      항만 신규설비 추가 
 */
package com.inisteel.cim.yd.ydEquipStat.MapSync;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;

import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;





/**
 * Map동기화 Session EJB
 * 
 * @ejb.bean name="MapSyncSeEJB" jndi-name="MapSyncSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class MapSyncSeEJBBean extends BaseSessionBean {

    // Session Name
    private String szSessionName  = this.getClass().getName();

    private YdDaoUtils ydDaoUtils = new YdDaoUtils();
    private YdUtils ydUtils       = new YdUtils();

    // [DEBUG] message flag
    private boolean bDebugFlag    = true;





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
        String szMsg        = "";
        String szMethodName = "procTest";
        String szRcvTcCode  = ydUtils.getTcCode(msgRecord);

        if(szRcvTcCode == null){
            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }
        if(bDebugFlag){
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
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

        szMsg = "Test정보수신 처리(" + szMethodName + ") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    }// end of procTest()





    /**
     * 오퍼레이션명 : C연주 저장위치제원요구 (Y1YDL001) [권오창 2009.09.09]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY1StrLocSpecReq(JDTORecord msgRecord) throws JDTOException {
        // 파라미터 NULL 체크 후 레코드 데이터
        JDTORecord getParamRecord  = JDTORecordFactory.getInstance().create();

        // 델리게이트 호출을 위한 편집 레코드 데이터
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();

        YdDelegate ydDelegate      = new YdDelegate();

        String szMethodName        = "procY1StrLocSpecReq";
        String szMsg               = "";
        String szOperationName     = "C연주슬라브L2 저장위치제원요구";
        String szTemp              = "";
        String szRcvTcCode         = ydUtils.getTcCode(msgRecord);

        int nRtnVal                = 0;

        ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
        ydUtils.displayRecord(szOperationName, msgRecord);
        ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);

        if(szRcvTcCode == null){
            szMsg = szSessionName + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

        if(bDebugFlag){
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        try{
            //=============================================================
            // 권오창
            // 2009.11.04
            //
            // Log 테이블 등록
            //=============================================================
            szMsg = "[C연주정정] 저장위치제원요구 수신";
            ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);





            // 파라미터 Check
            nRtnVal = this.paramY1YDL001Check(msgRecord, getParamRecord, 0);
            if(nRtnVal == -1) {
                szMsg = "파라미터 Check중 Error : " + nRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            // 적치열구분 생성
            szTemp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP") + ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP")
                + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP") + ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO");

            // 델리게이트 호출을 위한 레코드 편집  -- 수정 : 2015.12.14 By LeeJY
            String ydGp         = getParamRecord.getFieldString("YD_GP"); //야드구분
            szMsg = szMsg + " --> 야드구분 : "+ydGp;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            setCrnschRecord.setField("JMS_TC_CD"      , "YDY1L001");                                        // TC-CODE
            if (ydGp.equals("M")) {
                setCrnschRecord.setField("JMS_TC_CD"      , "YDE7L001");                                    // TC-CODE: 항만야드
            }
            setCrnschRecord.setField("YD_GP"          , getParamRecord.getFieldString("YD_GP"));            // 야드구분
            setCrnschRecord.setField("YD_STK_COL_GP"  , szTemp);                                            // 야드적치열구분
            setCrnschRecord.setField("YD_STK_BED_NO"  , getParamRecord.getFieldString("YD_STK_BED_NO"));    // 야드적치BED번호
            setCrnschRecord.setField("YD_INFO_SYNC_CD", getParamRecord.getFieldString("YD_INFO_SYNC_CD"));  // 야드정보동기화코드

            // 델리게이트 호출
            ydDelegate.sendMsg(setCrnschRecord);
        }catch(JDTOException e) {
            szMsg = "JDTOError : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }catch(Exception e) {
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        } // end of try~catch
    } // end of procY1StrLocSpecReq





    /**
     * 오퍼레이션명 : C연주 저장위치제원요구 파라미터 체크  [권오창 2009.09.09]
     *
     * @param  ● msgRecord, outRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int paramY1YDL001Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
        // 파라미터 체크 결과 레코드 생성
        JDTORecord setRecord = JDTORecordFactory.getInstance().create();

        // 변수
        String szMethodName  = "paramY1YDL001Check";
        String szMsg         = "" ;

        int nRtnVal          = 0;

        try{
            // 레코드 값 체크
            setRecord.setField("YD_INFO_SYNC_CD", ydDaoUtils.paraRecChkNull(msgRecord, "YD_INFO_SYNC_CD"));
            setRecord.setField("YD_GP"          , ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP"));
            setRecord.setField("YD_BAY_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP"));
            setRecord.setField("YD_EQP_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP"));
            setRecord.setField("YD_STK_COL_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO"));
            setRecord.setField("YD_STK_BED_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO"));

            // 레퍼런스 레코드인자에 설정
            outRecord.setRecord(setRecord);

            //======================================================================================================
            // LOG 출력 - 그냥 테스트용으로 출력 레코드값체크하는 코드는 제외하고 나중에 삭제
            szMsg = "[1] 야드동기화코드 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_INFO_SYNC_CD");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[2] 야드구분 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_GP");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[3] 야드 동구분 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_BAY_GP");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[4] 야드 설비구분 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_EQP_GP");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[5] 야드 적치열번호 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_COL_NO");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[6] 야드 적치BED번호 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_BED_NO");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            //======================================================================================================

        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return nRtnVal = -1;
        } // end of try~catch

        return nRtnVal = 1;
    } // end of ParamY1YDL001Check





    /**
     * 오퍼레이션명 : 저장품제원요구 (Y1YDL002) [권오창 2009.09.09]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY1StockSpecReq(JDTORecord msgRecord) throws JDTOException {
        // 파라미터 NULL 체크 후 레코드 데이터
        JDTORecord getParamRecord  = JDTORecordFactory.getInstance().create();

        // 델리게이트 호출을 위한 편집 레코드 데이터
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();

        YdDelegate ydDelegate      = new YdDelegate();

        String szMethodName        = "procY1StockSpecReq";
        String szMsg               = "";
        String szOperationName     = "C연주슬라브L2 저장품제원요구";
        String szTemp              = "";
        String szRcvTcCode         = ydUtils.getTcCode(msgRecord);

        int nRtnVal                = 0;

        ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
        ydUtils.displayRecord(szOperationName, msgRecord);
        ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);

        if(szRcvTcCode == null){
            szMsg = szSessionName + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

        if(bDebugFlag){
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        try{
            //=============================================================
            // 권오창
            // 2009.11.04
            //
            // Log 테이블 등록
            //=============================================================
            szMsg = "[C연주정정] 저장품제원요구 수신";
            ydUtils.putLogMsg("A",YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);





            // 파라미터 Check
            nRtnVal = this.paramY1YDL002Check(msgRecord, getParamRecord, 0);
            if(nRtnVal == -1) {
                szMsg = "파라미터 Check중 Error : " + nRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            // 적치열구분 생성
            szTemp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP") + ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP")
                     + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP") + ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO");

            // 델리게이트 호출을 위한 레코드 편집  -- 수정 : 2015.12.14 By LeeJY
            String ydGp         = getParamRecord.getFieldString("YD_GP"); //야드구분
            szMsg = szMsg + " --> 야드구분 : "+ydGp;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            setCrnschRecord.setField("JMS_TC_CD"      , "YDY1L002");                                        // TC-CODE
            if (ydGp.equals("M")) {
                setCrnschRecord.setField("JMS_TC_CD"      , "YDE7L002");                                    // TC-CODE: 항만야드
            }
            setCrnschRecord.setField("YD_GP"          , getParamRecord.getFieldString("YD_GP"));            // 야드구분
            setCrnschRecord.setField("YD_STK_COL_GP"  , szTemp);                                            // 야드적치열구분
            setCrnschRecord.setField("YD_STK_BED_NO"  , getParamRecord.getFieldString("YD_STK_BED_NO"));    // 야드적치BED번호
            setCrnschRecord.setField("YD_INFO_SYNC_CD", getParamRecord.getFieldString("YD_INFO_SYNC_CD"));  // 야드정보동기화코드
            setCrnschRecord.setField("STL_NO"         , getParamRecord.getFieldString("STL_NO"));           // 재료번호

            // 델리게이트 호출
            ydDelegate.sendMsg(setCrnschRecord);
        }catch(JDTOException e) {
            szMsg = "JDTOError : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }catch(Exception e) {
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        } // end of try~catch
    } // end of procY1StockSpecReq





    /**
     * 오퍼레이션명 : C연주 저장품제원요구 파라미터 체크 [권오창 2009.09.09]
     *
     * @param  ● msgRecord, outRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int paramY1YDL002Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
        // 파라미터 체크 결과 레코드 생성
        JDTORecord setRecord = JDTORecordFactory.getInstance().create();

        // 변수
        String szMethodName  = "paramY1YDL002Check";
        String szMsg         = "" ;

        int nRtnVal          = 0;

        try{
            // 레코드 값 체크
            setRecord.setField("YD_INFO_SYNC_CD", ydDaoUtils.paraRecChkNull(msgRecord, "YD_INFO_SYNC_CD"));
            setRecord.setField("YD_GP"          , ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP"));
            setRecord.setField("YD_BAY_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP"));
            setRecord.setField("YD_EQP_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP"));
            setRecord.setField("YD_STK_COL_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO"));
            setRecord.setField("YD_STK_BED_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO"));
            setRecord.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"));

            // 레퍼런스 레코드인자에 설정
            outRecord.setRecord(setRecord);

            //======================================================================================================
            // LOG 출력  - 그냥 테스트용으로 출력 나중에 삭제
            szMsg = "[1] 정보동기화코드  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_INFO_SYNC_CD");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[2] 야드구분  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_GP");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[3] 야드동구분  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_BAY_GP");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[4] 야드설비구분  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_EQP_GP");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[5] 야드적치열번호  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_COL_NO");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[6] 야드적치Bed번호  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_BED_NO");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            //======================================================================================================

        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return nRtnVal = -1;
        } // end of try~catch

        return nRtnVal = 1;
    } // end of ParamY1YDL002Check





    /**
     * 오퍼레이션명 : C연주 크레인작업계획요구 (Y1YDL006) [권오창 2009.09.09]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY1CrnWrkPlnReq(JDTORecord msgRecord) throws JDTOException {
        // 파라미터 NULL 체크 후 레코드 데이터
        JDTORecord getParamRecord  = JDTORecordFactory.getInstance().create();

        // 델리게이트 호출을 위한 편집 레코드 데이터
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();

        YdDelegate ydDelegate      = new YdDelegate();

        String szMethodName        = "procY1CrnWrkPlnReq";
        String szMsg               = "";
        String szOperationName     = "C연주슬라브L2 크레인작업계획요구";
        String szRcvTcCode         = ydUtils.getTcCode(msgRecord);

        int nRtnVal                = 0;

        ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
        ydUtils.displayRecord(szOperationName, msgRecord);
        ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);

        if(szRcvTcCode == null){
            szMsg = szSessionName + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

        if(bDebugFlag){
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        try{
            //=============================================================
            // 권오창
            // 2009.11.04
            //
            // Log 테이블 등록
            //=============================================================
            szMsg = "[C연주정정] 크레인작업계획요구 수신";
            ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);





            // 파라미터 Check
            nRtnVal = this.paramY1YDL006Check(msgRecord, getParamRecord, 0);
            if(nRtnVal == -1) {
                szMsg = "파라미터 Check중 Error : " + nRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            // 델리게이트 호출을 위한 레코드 편집
            setCrnschRecord.setField("JMS_TC_CD"      , "YDY1L003");                                        // TC-CODE
            setCrnschRecord.setField("YD_GP"          , "A");                                               // 야드구분
            setCrnschRecord.setField("YD_INFO_SYNC_CD", getParamRecord.getFieldString("YD_INFO_SYNC_CD"));  // 야드동기화 코드

            // 델리게이트 호출
            ydDelegate.sendMsg(setCrnschRecord);
        }catch(JDTOException e) {
            szMsg = "JDTOError : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }catch(Exception e) {
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        } // end of try~catch
    } // end of procY1CrnWrkPlnReq





    /**
     * 오퍼레이션명 : C연주 크레인작업계획요구 파라미터 체크 [권오창 2009.09.09]
     *
     * @param  ● msgRecord, outRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int paramY1YDL006Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
        // 파라미터 체크 결과 레코드 생성
        JDTORecord setRecord = JDTORecordFactory.getInstance().create();

        // 변수
        String szMethodName  = "paramY1YDL006Check";
        String szMsg         = "" ;

        int nRtnVal          = 0;

        try{
            // 레코드 값 체크
            setRecord.setField("YD_INFO_SYNC_CD", ydDaoUtils.paraRecChkNull(msgRecord, "YD_INFO_SYNC_CD"));

            // 레퍼런스 레코드인자에 설정
            outRecord.setRecord(setRecord);

            //======================================================================================================
            // LOG 출력 - 그냥 테스트용으로 출력 레코드값체크하는 코드는 제외하고 나중에 삭제
            szMsg = "[1] 정보 동기화 코드 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_INFO_SYNC_CD");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            //======================================================================================================

        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return nRtnVal = -1;
        } // end of try~catch

        return nRtnVal = 1;
    } // end of ParamY1YDL006Check





    /**
     * 오퍼레이션명 : A후판 저장위치제원요구 (Y3YDL001) [권오창 2009.09.09]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY3StrLocSpecReq(JDTORecord msgRecord) throws JDTOException {
        // 파라미터 NULL 체크 후 레코드 데이터
        JDTORecord getParamRecord  = JDTORecordFactory.getInstance().create();

        // 델리게이트 호출을 위한 편집 레코드 데이터
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();

        YdDelegate ydDelegate      = new YdDelegate();

        String szMethodName        = "procY3StrLocSpecReq";
        String szMsg               = "";
        String szOperationName     = "A후판슬라브L2 저장위치제원요구";
        String szTemp              = "";
        String szRcvTcCode         = ydUtils.getTcCode(msgRecord);

        int nRtnVal                = 0;

        ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
        ydUtils.displayRecord(szOperationName, msgRecord);
        ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);

        if(szRcvTcCode == null){
            szMsg = szSessionName + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

        if(bDebugFlag){
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        try{
            //=============================================================
            // 권오창
            // 2009.11.04
            //
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판슬라브야드] 저장위치제원요구 수신";
            ydUtils.putLogMsg("D",YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);





            // 파라미터 Check
            nRtnVal = this.paramY3YDL001Check(msgRecord, getParamRecord, 0);
            if(nRtnVal == -1) {
                szMsg = "파라미터 Check중 Error : " + nRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            // 적치열구분 생성
            szTemp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP") + ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP")
                + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP") + ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO");

            // 델리게이트 호출을 위한 레코드 편집
            setCrnschRecord.setField("JMS_TC_CD"      , "YDY3L001");                                        // TC-CODE
            setCrnschRecord.setField("YD_GP"          , getParamRecord.getFieldString("YD_GP"));            // 야드구분
            setCrnschRecord.setField("YD_STK_COL_GP"  , szTemp);                                            // 야드적치열구분
            setCrnschRecord.setField("YD_STK_BED_NO"  , getParamRecord.getFieldString("YD_STK_BED_NO"));    // 야드적치BED번호
            setCrnschRecord.setField("YD_INFO_SYNC_CD", getParamRecord.getFieldString("YD_INFO_SYNC_CD"));  // 야드정보동기화코드

            // 델리게이트 호출
            ydDelegate.sendMsg(setCrnschRecord);
        }catch(JDTOException e) {
            szMsg = "JDTOError : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }catch(Exception e) {
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        } // end of try~catch
    } // end of procY3StrLocSpecReq





    /**
     * 오퍼레이션명 : A후판 저장위치제원요구 파라미터 체크 [권오창 2009.09.09]
     *
     * @param  ● msgRecord, outRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int paramY3YDL001Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
        // 파라미터 체크 결과 레코드 생성
        JDTORecord setRecord = JDTORecordFactory.getInstance().create();

        // 변수
        String szMethodName  = "paramY3YDL001Check";
        String szMsg         = "" ;

        int nRtnVal          = 0;

        try{
            // 레코드 값 체크
            setRecord.setField("YD_INFO_SYNC_CD", ydDaoUtils.paraRecChkNull(msgRecord, "YD_INFO_SYNC_CD"));
            setRecord.setField("YD_GP"          , ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP"));
            setRecord.setField("YD_BAY_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP"));
            setRecord.setField("YD_EQP_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP"));
            setRecord.setField("YD_STK_COL_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO"));
            setRecord.setField("YD_STK_BED_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO"));

            // 레퍼런스 레코드인자에 설정
            outRecord.setRecord(setRecord);

            //======================================================================================================
            // LOG 출력 - 그냥 테스트용으로 출력 레코드값체크하는 코드는 제외하고 나중에 삭제
            szMsg = "[1] 정보동기화코드 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_INFO_SYNC_CD");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[2] 야드구분 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_GP");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[3] 야드동구분 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_BAY_GP");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[4] 야드설비구분 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_EQP_GP");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[5] 야드적치열번호 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_COL_NO");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[6] 야드적치BED번호 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_BED_NO");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            //======================================================================================================

        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return nRtnVal = -1;
        } // end of try~catch

        return nRtnVal = 1;
    } // end of paramY3YDL001Check





    /**
     * 오퍼레이션명 : 저장품제원요구 (Y3YDL002) [권오창 2009.09.09]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY3StockSpecReq(JDTORecord msgRecord) throws JDTOException {
        // 파라미터 NULL 체크 후 레코드 데이터
        JDTORecord getparamRecord  = JDTORecordFactory.getInstance().create();

        // 델리게이트 호출을 위한 편집 레코드 데이터
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();

        YdDelegate ydDelegate      = new YdDelegate();

        String szMethodName        = "procY3StockSpecReq";
        String szMsg               = "";
        String szOperationName     = "A후판슬라브L2 저장품제원요구";
        String szTemp              = "";
        String szRcvTcCode         = ydUtils.getTcCode(msgRecord);

        int nRtnVal                = 0;

        ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
        ydUtils.displayRecord(szOperationName, msgRecord);
        ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);

        if(szRcvTcCode == null){
            szMsg = szSessionName + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

        if(bDebugFlag){
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        try{
            //=============================================================
            // 권오창
            // 2009.11.05
            //
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판슬라브야드] 저장품제원요구 수신";
            ydUtils.putLogMsg("D", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);





            // 파라미터 Check
            nRtnVal = this.paramY3YDL002Check(msgRecord, getparamRecord, 0);
            if(nRtnVal == -1) {
                szMsg = "파라미터 Check중 Error : " + nRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            // 적치열구분 생성
            szTemp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP") + ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP")
                     + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP") + ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO");

            // 델리게이트 호출을 위한 레코드 편집
            setCrnschRecord.setField("JMS_TC_CD"      , "YDY3L002");                                        // TC-CODE
            setCrnschRecord.setField("YD_GP"          , getparamRecord.getFieldString("YD_GP"));            // 야드구분                "D"
            setCrnschRecord.setField("YD_STK_COL_GP"  , szTemp);                                            // 야드적치열구분
            setCrnschRecord.setField("YD_STK_BED_NO"  , getparamRecord.getFieldString("YD_STK_BED_NO"));    // 야드적치BED번호
            setCrnschRecord.setField("YD_INFO_SYNC_CD", getparamRecord.getFieldString("YD_INFO_SYNC_CD"));  // 야드정보동기화코드
            setCrnschRecord.setField("STL_NO"         , getparamRecord.getFieldString("STL_NO"));           // 재료번호

            // 델리게이트 호출
            ydDelegate.sendMsg(setCrnschRecord);
        }catch(JDTOException e) {
            szMsg = "JDTOError : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }catch(Exception e) {
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        } // end of try~catch
    } // end of procY3StockSpecReq





    /**
     * 오퍼레이션명 : A후판 저장품제원요구 파라미터 체크 [권오창 2009.09.09]
     *
     * @param  ● msgRecord, outRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int paramY3YDL002Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
        // 파라미터 체크 결과 레코드 생성
        JDTORecord setRecord = JDTORecordFactory.getInstance().create();

        // 변수
        String szMethodName  = "paramY3YDL002Check";
        String szMsg         = "" ;

        int nRtnVal          = 0;

        try{
            // 레코드 값 체크
            setRecord.setField("YD_INFO_SYNC_CD", ydDaoUtils.paraRecChkNull(msgRecord, "YD_INFO_SYNC_CD"));
            setRecord.setField("YD_GP"          , ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP"));
            setRecord.setField("YD_BAY_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP"));
            setRecord.setField("YD_EQP_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP"));
            setRecord.setField("YD_STK_COL_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO"));
            setRecord.setField("YD_STK_BED_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO"));
            setRecord.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"));

            // 레퍼런스 레코드인자에 설정
            outRecord.setRecord(setRecord);

            //======================================================================================================
            // LOG 출력 - 그냥 테스트용으로 출력 레코드값체크하는 코드는 제외하고 나중에 삭제
            szMsg = "[1] 야드정보동기화코드 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_INFO_SYNC_CD");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[2] 야드구분 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_GP");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[3] 야드동구분 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_BAY_GP");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[4] 야드걸비구분 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_EQP_GP");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[5] 야드적치열번호 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_COL_NO");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[6] 야드적치BED번호: " + ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_BED_NO");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[7] 재료번호 : " + ydDaoUtils.paraRecChkNull(outRecord, "STL_NO");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            //======================================================================================================

        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return nRtnVal = -1;
        } // end of try~catch

        return nRtnVal = 1;
    } // end of paramY3YDL002Check





    /**
     * 오퍼레이션명 : A후판 크레인작업계획요구 (Y3YDL006) [권오창 2009.09.09]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY3CrnWrkPlnReq(JDTORecord msgRecord) throws JDTOException {
        // 파라미터 NULL 체크 후 레코드 데이터
        JDTORecord getparamRecord  = JDTORecordFactory.getInstance().create();

        // 델리게이트 호출을 위한 편집 레코드 데이터
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();

        YdDelegate ydDelegate      = new YdDelegate();

        String szMethodName        = "procY3CrnWrkPlnReq";
        String szMsg               = "";
        String szOperationName     = "A후판슬라브L2 크레인작업계획요구";
        String szRcvTcCode         = ydUtils.getTcCode(msgRecord);

        int nRtnVal                = 0;

        ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
        ydUtils.displayRecord(szOperationName, msgRecord);
        ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);

        if(szRcvTcCode == null){
            szMsg = szSessionName + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

        if(bDebugFlag){
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        try{
            //=============================================================
            // 권오창
            // 2009.11.05
            //
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판슬라브야드] 크레인작업계획요구 수신";
            ydUtils.putLogMsg("D", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);





            // 파라미터 Check
            nRtnVal = this.paramY3YDL006Check(msgRecord, getparamRecord, 0);
            if(nRtnVal == -1) {
                szMsg = "파라미터 Check중 Error : " + nRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            // 델리게이트 호출을 위한 레코드 편집
            setCrnschRecord.setField("JMS_TC_CD"      , "YDY3L003");                                        // TC-CODE
            setCrnschRecord.setField("YD_GP"          , YdConstant.YD_GP_A_PLATE_SLAB_YARD);                // 야드구분 - 후판슬라브야드
            setCrnschRecord.setField("YD_INFO_SYNC_CD", getparamRecord.getFieldString("YD_INFO_SYNC_CD"));  // 야드동기화 코드

            // 델리게이트 호출
            ydDelegate.sendMsg(setCrnschRecord);
        }catch(JDTOException e) {
            szMsg = "JDTOError : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }catch(Exception e) {
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        } // end of try~catch
    } // end of procY3CrnWrkPlnReq





    /**
     * 오퍼레이션명 : A후판 크레인작업계획요구 파라미터 체크 [권오창 2009.09.09]
     *
     * @param  ● msgRecord, outRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int paramY3YDL006Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
        // 파라미터 체크 결과 레코드 생성
        JDTORecord setRecord = JDTORecordFactory.getInstance().create();

        // 변수
        String szMethodName  = "paramY3YDL006Check";
        String szMsg         = "" ;

        int nRtnVal          = 0;

        try{
            // 레코드 값 체크
            setRecord.setField("YD_INFO_SYNC_CD", ydDaoUtils.paraRecChkNull(msgRecord, "YD_INFO_SYNC_CD"));

            // 레퍼런스 레코드인자에 설정
            outRecord.setRecord(setRecord);

            //======================================================================================================
            // LOG 출력 - 그냥 테스트용으로 출력 레코드값체크하는 코드는 제외하고 나중에 삭제
            szMsg = "[1] 야드 정보동기화코드 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_INFO_SYNC_CD");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            //======================================================================================================

        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return nRtnVal = -1;
        } // end of try~catch

        return nRtnVal = 1;
    } // end of paramY3YDL006Check





    /**
     * 오퍼레이션명 : 후판제품야드 저장위치제원요구 (Y4YDL001) [권오창 2009.09.09]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY4StrLocSpecReq(JDTORecord msgRecord) throws JDTOException {
        // 파라미터 NULL 체크 후 레코드 데이터
        JDTORecord getparamRecord  = JDTORecordFactory.getInstance().create();

        // 델리게이트 호출을 위한 편집 레코드 데이터
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();

        YdDelegate ydDelegate      = new YdDelegate();

        String szMethodName        = "procY4StrLocSpecReq";
        String szMsg               = "";
        String szOperationName     = "후판제품야드L2 저장위치제원요구";
        String szTemp              = "";
        String szRcvTcCode         = ydUtils.getTcCode(msgRecord);

        int nRtnVal                = 0;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= msgRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8저장위치제원요구 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//      ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
//      ydUtils.putLogNew(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG, logId);

        ydUtils.displayRecord(szOperationName, msgRecord);
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//      ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
//      ydUtils.putLogNew(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG, logId);

        if(szRcvTcCode == null){
            szMsg = szSessionName + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return ;
        }

        if(bDebugFlag){
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
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
            szMsg = "[후판제품야드] 저장위치제원요구 수신";
            ydUtils.putLogMsg("K", YdConstant.YD_MONITORING_CHANNEL_K, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);





            // 파라미터 Check
            nRtnVal = this.paramY4YDL001Check(msgRecord, getparamRecord, 0);
            if(nRtnVal == -1) {
                szMsg = "파라미터 Check중 Error : " + nRtnVal;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            }

            // 적치열구분 생성
            szTemp = ydDaoUtils.paraRecChkNull(msgRecord,"YD_GP") + ydDaoUtils.paraRecChkNull(msgRecord,"YD_BAY_GP")
                     + ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_GP") + ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_COL_NO");

            // 델리게이트 호출을 위한 레코드 편집
            if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(ydDaoUtils.paraRecChkNull(msgRecord,"YD_GP"))) { //--2013.01.22 수정(3기)
                setCrnschRecord.setField("JMS_TC_CD"      , "YDY8L001");                                        // TC-CODE

                //2021. 1. 6 추가(Y9시스템 전송여부)
                boolean isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_ydStkColGp(szTemp);
                if(isSendToEaiY9){
                    setCrnschRecord.setField("JMS_TC_CD"      , "YDY9L001");
                }

            } else {
                setCrnschRecord.setField("JMS_TC_CD"      , "YDY4L001");                                        // TC-CODE
            }
            setCrnschRecord.setField("YD_GP"          , getparamRecord.getFieldString("YD_GP"));            // 야드구분
            setCrnschRecord.setField("YD_STK_COL_GP"  , szTemp);                                            // 야드적치열구분
            setCrnschRecord.setField("YD_STK_BED_NO"  , getparamRecord.getFieldString("YD_STK_BED_NO"));    // 야드적치BED번호
            setCrnschRecord.setField("YD_INFO_SYNC_CD", getparamRecord.getFieldString("YD_INFO_SYNC_CD"));  // 야드정보동기화코드

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// setCrnschRecord 에 logId 추가
            setCrnschRecord.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

            // 델리게이트 호출
            ydDelegate.sendMsg(setCrnschRecord);
            
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
            szMsg = "Y8저장위치제원요구 처리(" + szMethodName + ") 완료";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
            
        }catch(JDTOException e) {
            szMsg = "JDTOError : " + e.getLocalizedMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return ;
        }catch(Exception e) {
            szMsg = "Error : " + e.getLocalizedMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return ;
        } // end of try~catch
    } // end of procY4StrLocSpecReq





    /**
     * 오퍼레이션명 : 후판제품야드 저장위치제원요구 파라미터 체크 [권오창 2009.09.09]
     *
     * @param  ● msgRecord, outRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int paramY4YDL001Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
        // 파라미터 체크 결과 레코드 생성
        JDTORecord setRecord = JDTORecordFactory.getInstance().create();

        // 변수
        String szMethodName  = "paramY4YDL001Check";
        String szMsg         = "" ;

        int nRtnVal          = 0;

        try{
            // 레코드 값 체크
            setRecord.setField("YD_INFO_SYNC_CD", ydDaoUtils.paraRecChkNull(msgRecord,"YD_INFO_SYNC_CD"));
            setRecord.setField("YD_GP"          , ydDaoUtils.paraRecChkNull(msgRecord,"YD_GP"));
            setRecord.setField("YD_BAY_GP"      , ydDaoUtils.paraRecChkNull(msgRecord,"YD_BAY_GP"));
            setRecord.setField("YD_EQP_GP"      , ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_GP"));
            setRecord.setField("YD_STK_COL_NO"  , ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_COL_NO"));
            setRecord.setField("YD_STK_BED_NO"  , ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_BED_NO"));

            // 레퍼런스 레코드인자에 설정
            outRecord.setRecord(setRecord);
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return nRtnVal = -1;
        } // end of try~catch

        return nRtnVal = 1;
    } // end of paramY4YDL001Check





    /**
     * 오퍼레이션명 : 저장품제원요구 (Y4YDL002) [권오창 2009.09.09]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY4StockSpecReq(JDTORecord msgRecord) throws JDTOException {
        // 파라미터 NULL 체크 후 레코드 데이터
        JDTORecord getparamRecord  = JDTORecordFactory.getInstance().create();

        // 델리게이트 호출을 위한 편집 레코드 데이터
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();

        YdDelegate ydDelegate      = new YdDelegate();

        String szMethodName        = "procY4StockSpecReq";
        String szMsg               = "";
        String szOperationName     = "후판제품야드L2 저장품제원요구";
        String szTemp              = "";
        String szRcvTcCode         = ydUtils.getTcCode(msgRecord);

        int nRtnVal                = 0;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= msgRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8저장품제원요구 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////



// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//      ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
//      ydUtils.putLogNew(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG, logId);
        ydUtils.displayRecord(szOperationName, msgRecord);
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//      ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
//      ydUtils.putLogNew(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG, logId);

        if(szRcvTcCode == null){
            szMsg = szSessionName + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return ;
        }

        if(bDebugFlag){
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
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
            szMsg = "[후판제품야드] 저장품제원요구 수신";
            ydUtils.putLogMsg("K", YdConstant.YD_MONITORING_CHANNEL_K, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            // 파라미터 Check
            nRtnVal = this.paramY4YDL002Check(msgRecord, getparamRecord, 0);
            if(nRtnVal == -1) {
                szMsg = "파라미터 Check중 Error : " + nRtnVal;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            }

            // 적치열구분 생성
            szTemp = ydDaoUtils.paraRecChkNull(msgRecord,"YD_GP") + ydDaoUtils.paraRecChkNull(msgRecord,"YD_BAY_GP")
                     + ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_GP") + ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_COL_NO");

            // 델리게이트 호출을 위한 레코드 편집
            if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(ydDaoUtils.paraRecChkNull(msgRecord,"YD_GP"))) { //--2013.01.22 수정(3기)
                setCrnschRecord.setField("JMS_TC_CD"      , "YDY8L002");                                    // TC-CODE

                //2021. 1. 6 추가(Y9시스템 전송여부)
                boolean isSendToEaiY9 = false;
                if(!"".equals(PlateGdsYdUtil.trim(getparamRecord.getFieldString("STL_NO")))){
                    isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_stlNo( getparamRecord.getFieldString("STL_NO") );
                }else{
                    isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_ydStkColGp(szTemp);
                }

                if(isSendToEaiY9){
                    setCrnschRecord.setField("JMS_TC_CD"      , "YDY9L002");
                }

            } else {
                setCrnschRecord.setField("JMS_TC_CD"      , "YDY4L002");                                    // TC-CODE
            }
            setCrnschRecord.setField("YD_GP"          , getparamRecord.getFieldString("YD_GP"));            // 야드구분
            setCrnschRecord.setField("YD_STK_COL_GP"  , szTemp);                                            // 야드적치열구분
            setCrnschRecord.setField("YD_STK_BED_NO"  , getparamRecord.getFieldString("YD_STK_BED_NO"));    // 야드적치BED번호
            setCrnschRecord.setField("YD_INFO_SYNC_CD", getparamRecord.getFieldString("YD_INFO_SYNC_CD"));  // 야드정보동기화코드
            setCrnschRecord.setField("STL_NO"         , getparamRecord.getFieldString("STL_NO"));           // 재료번호

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// setCrnschRecord 에 logId 추가
            setCrnschRecord.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

            // 델리게이트 호출
            ydDelegate.sendMsg(setCrnschRecord);
            
////////////////////////////////////////////////////////////////////////////////////////
//2024.09.02 로그 개선  START
szMsg = "Y8저장품제원요구 처리(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
//2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
            
        }catch(JDTOException e) {
            szMsg = "JDTOError : " + e.getLocalizedMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return ;
        }catch(Exception e) {
            szMsg = "Error : " + e.getLocalizedMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return ;
        } // end of try~catch
    } // end of procY4StockSpecReq





    /**
     * 오퍼레이션명 : 후판제품야드 저장품제원요구 파라미터 체크 [권오창 2009.09.09]
     *
     * @param  ● msgRecord, outRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int paramY4YDL002Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
        // 파라미터 체크 결과 레코드 생성
        JDTORecord setRecord = JDTORecordFactory.getInstance().create();

        // 변수
        String szMethodName  = "paramY4YDL002Check";
        String szMsg         = "" ;

        int nRtnVal          = 0;

        try{
            // 레코드 값 체크
            setRecord.setField("YD_INFO_SYNC_CD", ydDaoUtils.paraRecChkNull(msgRecord,"YD_INFO_SYNC_CD"));
            setRecord.setField("YD_GP"          , ydDaoUtils.paraRecChkNull(msgRecord,"YD_GP"));
            setRecord.setField("YD_BAY_GP"      , ydDaoUtils.paraRecChkNull(msgRecord,"YD_BAY_GP"));
            setRecord.setField("YD_EQP_GP"      , ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_GP"));
            setRecord.setField("YD_STK_COL_NO"  , ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_COL_NO"));
            setRecord.setField("YD_STK_BED_NO"  , ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_BED_NO"));
            setRecord.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO"));

            // 레퍼런스 레코드인자에 설정
            outRecord.setRecord(setRecord);
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return nRtnVal = -1;
        } // end of try~catch

        return nRtnVal = 1;
    } // end of paramY4YDL002Check




// 소스정리 20210701 lhj
//  /**
//   * 오퍼레이션명 : C열연코일야드L2 저장위치제원요구 (Y5YDL001) [권오창 2009.09.09]
//   *
//   * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//   * @param msgRecord
//   * @return
//   * @throws JDTOException
//   */
//  public void procY5StrLocSpecReq(JDTORecord msgRecord) throws JDTOException {
//      // 파라미터 NULL 체크 후 레코드 데이터
//        JDTORecord getParamRecord  = JDTORecordFactory.getInstance().create();
//
//        // 델리게이트 호출을 위한 편집 레코드 데이터
//        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();
//
//      YdDelegate ydDelegate      = new YdDelegate();
//
//
//      // 변수 선언
//      String szMethodName        = "procY5StrLocSpecReq";
//      String szMsg               = "";
//      String szOperationName     = "C열연코일야드L2 저장위치제원요구";
//      String szTemp              = "";
//      String szRcvTcCode         = ydUtils.getTcCode(msgRecord);
//      int nRtnVal                = 0;
//
//
//      ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
//      ydUtils.displayRecord(szOperationName, msgRecord);
//      ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
//
//      if(szRcvTcCode == null){
//          szMsg = szSessionName + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//          return ;
//      }
//
//      if(bDebugFlag){
//          szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//      }
//
//      try{
//          //=============================================================
//          // 권오창
//          // 2009.11.05
//          //
//          // Log 테이블 등록
//          //=============================================================
//          szMsg = "[열연 코일야드L2] 저장위치제원요구 수신";
//          ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
//
//
//
//
//
//          // 파라미터 Check
//          nRtnVal = this.paramY5YDL001Check(msgRecord, getParamRecord, 0);
//          if(nRtnVal == -1) {
//                szMsg = "파라미터 Check중 Error   : " + nRtnVal;
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                return ;
//          }
//
//          // 적치열구분 생성
//          szTemp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP") + ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP")
//                   + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP") + ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO");
//
//          // 델리게이트 호출을 위한 레코드 편집
//          setCrnschRecord.setField("JMS_TC_CD"      , "YDY5L001");                                        // TC-CODE
//          setCrnschRecord.setField("YD_GP"          , getParamRecord.getFieldString("YD_GP"));            // 야드구분
//          setCrnschRecord.setField("YD_STK_COL_GP"  , szTemp);                                            // 야드적치열구분
//          setCrnschRecord.setField("YD_STK_BED_NO"  , getParamRecord.getFieldString("YD_STK_BED_NO"));    // 야드적치BED번호
//          setCrnschRecord.setField("YD_INFO_SYNC_CD", getParamRecord.getFieldString("YD_INFO_SYNC_CD"));  // 야드정보동기화코드
//
//          // 델리게이트 호출
//          ydDelegate.sendMsg(setCrnschRecord);
//      }catch(JDTOException e) {
//            szMsg = "JDTOError : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//          return ;
//      }catch(Exception e) {
//            szMsg = "Error : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//            return ;
//      } // end of try~catch
//  } // end of procY5StrLocSpecReq





//    /**
//     * 오퍼레이션명 : C열연코일야드L2 저장위치제원요구 파라미터 체크 [권오창 2009.09.09]
//     *
//     * @param  ● msgRecord, outRecord, intGp
//     * @return ● nRtnVal
//     * @throws ● JDTOException
//     */
//  public int paramY5YDL001Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
//      // 파라미터 체크 결과 레코드 생성
//      JDTORecord setRecord = JDTORecordFactory.getInstance().create();
//
//      // 변수
//        String szMethodName  = "paramY5YDL001Check";
//      String szMsg         = "" ;
//
//        int nRtnVal          = 0;
//
//        try{
//          // 레코드 값 체크
//          setRecord.setField("YD_INFO_SYNC_CD", ydDaoUtils.paraRecChkNull(msgRecord, "YD_INFO_SYNC_CD"));
//          setRecord.setField("YD_GP"          , ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP"));
//          setRecord.setField("YD_BAY_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP"));
//          setRecord.setField("YD_EQP_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP"));
//          setRecord.setField("YD_STK_COL_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO"));
//          setRecord.setField("YD_STK_BED_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO"));
//
//          // 레퍼런스 레코드인자에 설정
//          outRecord.setRecord(setRecord);
//        }catch(Exception e){
//            szMsg = "Error : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//          return nRtnVal = -1;
//        } // end of try~catch
//
//      return nRtnVal = 1;
//  } // end of ParamY5YDL001Check





//  /**
//   * 오퍼레이션명 : C열연코일야드L2 저장품제원요구 (Y5YDL002) [권오창 2009.09.09]
//   *
//   * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//   * @param msgRecord
//   * @return
//   * @throws JDTOException
//   */
//  public void procY5StockSpecReq(JDTORecord msgRecord) throws JDTOException {
//      // 파라미터 NULL 체크 후 레코드 데이터
//      JDTORecord getparamRecord  = JDTORecordFactory.getInstance().create();
//
//      // 델리게이트 호출을 위한 편집 레코드 데이터
//      JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();
//
//      YdDelegate ydDelegate      = new YdDelegate();
//
//      String szMethodName        = "procY5StockSpecReq";
//      String szMsg               = "";
//      String szOperationName     = "C열연코일야드L2 저장품제원요구";
//      String szTemp              = "";
//      String szRcvTcCode         = ydUtils.getTcCode(msgRecord);
//
//      int nRtnVal                = 0;
//
//      ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
//      ydUtils.displayRecord(szOperationName, msgRecord);
//      ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
//
//      if(szRcvTcCode == null){
//          szMsg = szSessionName + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//          return ;
//      }
//
//      if(bDebugFlag){
//          szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//      }
//
//      try{
//          //=============================================================
//          // 권오창
//          // 2009.11.05
//          //
//          // Log 테이블 등록
//          //=============================================================
//          szMsg = "[열연 코일야드L2] 저장품제원요구 수신";
//          ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
//
//
//
//
//
//          // 파라미터 Check
//          nRtnVal = this.paramY5YDL002Check(msgRecord, getparamRecord, 0);
//          if(nRtnVal == -1) {
//              szMsg = "파라미터 Check중 Error : " + nRtnVal;
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//              return ;
//          }
//
//          // 적치열구분 생성
//          szTemp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP") + ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP")
//                   + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP") + ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO");
//
//          // 델리게이트 호출을 위한 레코드 편집
//          setCrnschRecord.setField("JMS_TC_CD"      , "YDY5L002");                                        // TC-CODE
//          setCrnschRecord.setField("YD_GP"          , getparamRecord.getFieldString("YD_GP"));            // 야드구분
//          setCrnschRecord.setField("YD_STK_COL_GP"  , szTemp);                                            // 야드적치열구분
//          setCrnschRecord.setField("YD_STK_BED_NO"  , getparamRecord.getFieldString("YD_STK_BED_NO"));    // 야드적치BED번호
//          setCrnschRecord.setField("YD_INFO_SYNC_CD", getparamRecord.getFieldString("YD_INFO_SYNC_CD"));  // 야드정보동기화코드
//          setCrnschRecord.setField("STL_NO"         , getparamRecord.getFieldString("STL_NO"));           // 재료번호
//
//          // 델리게이트 호출
//          ydDelegate.sendMsg(setCrnschRecord);
//      }catch(JDTOException e) {
//            szMsg = "JDTOError : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//            return ;
//      }catch(Exception e) {
//            szMsg = "Error : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//            return ;
//      } // end of try~catch
//  } // end of procY5StockSpecReq





//  /**
//   * 오퍼레이션명 : C열연 저장품제원요구 파라미터 체크 [권오창 2009.09.09]
//   *
//   * @param  ● msgRecord, outRecord, intGp
//   * @return ● nRtnVal
//   * @throws ● JDTOException
//   */
//  public int paramY5YDL002Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
//      // 파라미터 체크 결과 레코드 생성
//      JDTORecord setRecord = JDTORecordFactory.getInstance().create();
//
//      // 변수
//      String szMethodName  = "paramY5YDL002Check";
//      String szMsg         = "" ;
//
//      int nRtnVal          = 0;
//
//      try{
//          // 레코드 값 체크
//          setRecord.setField("YD_INFO_SYNC_CD", ydDaoUtils.paraRecChkNull(msgRecord, "YD_INFO_SYNC_CD"));
//          setRecord.setField("YD_GP"          , ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP"));
//          setRecord.setField("YD_BAY_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP"));
//          setRecord.setField("YD_EQP_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP"));
//          setRecord.setField("YD_STK_COL_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO"));
//          setRecord.setField("YD_STK_BED_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO"));
//          setRecord.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"));
//
//          // 레퍼런스 레코드인자에 설정
//          outRecord.setRecord(setRecord);
//      }catch(Exception e){
//            szMsg = "Error : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//          return nRtnVal = -1;
//      } // end of try~catch
//
//      return nRtnVal = 1;
//  } // end of paramY5YDL002Check


  //---------------------------------------------------------------------------
} // end of class MapSyncSeEJBBean



