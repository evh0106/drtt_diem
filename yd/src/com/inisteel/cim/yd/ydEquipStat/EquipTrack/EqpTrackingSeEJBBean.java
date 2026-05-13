/**
 * @(#)SlabJspSeEJBBean.java
 *
 * @version         1.0
 * @author          현대제철
 * @date            2012/11/14
 * 
 * @description     이클래스는업무 설비트래킹를 관리하기 위한 Session Session EJB클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.01  2013/04/03   허철호      허철호      신규설비 추가
 * V1.02  2015/12/14   이준영      이준영      항만랴드 설비추가 
 */
package com.inisteel.cim.yd.ydEquipStat.EquipTrack;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydEqpPauseDao.YdEqpPauseDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.yd.ydSch.CraneReSch.CoilCrnReSchSeEJBBean;

/**
 * 설비Tracking Session EJB
 *
 * @ejb.bean name="EqpTrackingSeEJB" jndi-name="EqpTrackingSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class EqpTrackingSeEJBBean extends BaseSessionBean {
    // Session Name
    private String szSessionName  = this.getClass().getName();
    private YdUtils ydUtils       = new YdUtils();
    private YdDaoUtils ydDaoUtils = new YdDaoUtils();
    private CoilCrnReSchSeEJBBean CoilobjCrnResch = new CoilCrnReSchSeEJBBean();
    YDDataUtil  yddatautil = new YDDataUtil();

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
     * 오퍼레이션명 : C연주 설비운전모드전환 (Y1YDL003) [권오창 2009.09.09]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY1EqpDrvMdTurnov(JDTORecord msgRecord) throws JDTOException {

        // 레코드 선언
        JDTORecord recPara         = null;
        JDTORecord setCrnschRecord = null;

        // DAO 및 UTIL 객체 생성
        YdDelegate ydDelegate      = new YdDelegate();
        EJBConnector ejbConn       = null;

        // 변수 선언
        String szMethodName        = "procY1EqpDrvMdTurnov";
        String szMsg               = "";
        String szOperationName     = "C연주슬라브L2 설비운전모드전환";
        String szRcvTcCode         = null;
        String szYD_EQP_ID         = "";
        String szYD_EQP_WRK_MODE   = "";
        int nRet                   = 0;

        szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
            // Log 테이블 등록
            //=============================================================
            szMsg = "[C연주정정] 설비운전모드전환 수신";
            ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            setCrnschRecord = JDTORecordFactory.getInstance().create();

            // 설비테이블에 야드설비작업Mode 업데이트
            szYD_EQP_ID       = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            szYD_EQP_WRK_MODE = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE");

            setCrnschRecord.setField("YD_EQP_ID"      , szYD_EQP_ID);         // 설비ID
            setCrnschRecord.setField("YD_EQP_WRK_MODE", szYD_EQP_WRK_MODE);   // 1: On-Line, 2: Off-Line
            setCrnschRecord.setField("MODIFIER" , szRcvTcCode);

            nRet = this.Y1YDL003UpdYdEqp(setCrnschRecord, 0);

            if(nRet == -1){
                szMsg = "설비테이블 업데이트 중  Error : (YD_EQP_ID : " + szYD_EQP_ID + "), (YD_EQP_WRK_MODE : " + szYD_EQP_WRK_MODE + "), Ret : " + nRet;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
             * YD_EQP_WRK_MODE (1: On-Line, 2: Off-Line)에 따른 업무 정의
             * 1: On-Line - 크레인 리스케줄 호출[복구], C연주 크레인 작업지시 호출[YD_EQP_ID, YD_WRK_PROG_STAT(W)]
             * 2: Off-Line - 크레인 리스케줄 호출[고장]
             * 수정자 : 임춘수
             * 일자 : 2009.06.17
             +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            if( szYD_EQP_WRK_MODE.equals("1") ) {           // 1: On-Line

                recPara = JDTORecordFactory.getInstance().create();
                //서버이중화로 인한 JMS 호출 순서가 바뀔 수 있으므로 해결 필요.
                //크레인 리스케줄 호출[복구]
                recPara.setField("MSG_ID", "YDYDJ502");
                recPara.setField("YD_EQP_ID", szYD_EQP_ID);

                //------------------------------------------------------------------------------------------------
                // EJB 호출로 변경 (2010.01.14) : 이현성
                //------------------------------------------------------------------------------------------------
                // ydDelegate.sendMsg(recPara);
                ejbConn = new EJBConnector("default", this);
                ejbConn.trx("CrnReSchSeEJB", "procY1CrnReSch", recPara);
                //------------------------------------------------------------------------------------------------

                szMsg = "[C연주 설비운전모드전환]C연주 크레인 리스케줄 호출[복구]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            }else if( szYD_EQP_WRK_MODE.equals("2")) {      // 2: Off-Line

                //크레인 리스케줄 호출[고장]
                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("MSG_ID",   "YDYDJ502");
                recPara.setField("YD_EQP_ID", szYD_EQP_ID);

                //------------------------------------------------------------------------------------------------
                // EJB 호출로 변경 (2010.01.14) : 이현성
                //------------------------------------------------------------------------------------------------
                // ydDelegate.sendMsg(recPara);
                ejbConn = new EJBConnector("default", this);
                ejbConn.trx("CrnReSchSeEJB", "procY1CrnReSch", recPara);
                //------------------------------------------------------------------------------------------------

                szMsg = "[C연주 설비운전모드전환]C연주 크레인 리스케줄 호출[고장]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            }else{                                          // 정의되지 않은 값
                szMsg = "[C연주 설비운전모드전환]야드설비작업Mode[" + szYD_EQP_WRK_MODE + "]가 정의되지 않은 값입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }


            //------------------------------------------------------------------
            // C 연주 설비정보변경시
            //------------------------------------------------------------------
            JDTORecord    recFlex = JDTORecordFactory.getInstance().create();
            recFlex.setField("YD_GP",  YdConstant.YD_GP_C_SLAB_YARD);
            recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
            // 추가 (2015.12.14) : 항만야드  EJB호출  By LeeJY
            String  szYdGp       = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
            if( szYdGp.equals("M")){
                recFlex.setField("YD_GP",  szYdGp);
            }
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putYdFlexCrnWrk("", recFlex);

            /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

            /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
             * 업무 : 크레인작업실적응답 전문 전송(YDY1L005)
             +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("MSG_ID"        , "YDY1L005");
            recPara.setField("YD_EQP_ID"     , szYD_EQP_ID);
            recPara.setField("YD_L2_WR_GP"   , "M");            //U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경
            recPara.setField("YD_L3_HD_RS_CD", "0000");         //야드L3처리결과코드
            ydDelegate.sendMsg(recPara);
            /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

        }catch(JDTOException e) {
            szMsg = "JDTOError : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }catch(Exception e) {
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }
    }

    /**
     * 오퍼레이션명 : C연주 야드 설비테이블 야드설비작업Mode 업데이트 [권오창 2009.09.09]
     *
     * @param  ● msgRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int Y1YDL003UpdYdEqp(JDTORecord msgRecord, int intGp) throws JDTOException {
        // DAO 생성
        YdEqpDao ydEqpDao   = new YdEqpDao();

        // 변수 선언
        String szMethodName = "Y1YDL003UpdYdEqp";
        String szMsg        = "";
        int nRtnVal         = 0;

        try{
            nRtnVal = ydEqpDao.updYdEqp(msgRecord, intGp);

            switch(nRtnVal){
                case 0 :
                    szMsg = "No Data Found!!!, ErrorCode:" + nRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRtnVal = -1;
                case -1 :
                    szMsg = "Dup_val_on_index!!!, ErrorCode:" + nRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRtnVal = -1;
                case -2 :
                    szMsg = "Parameter Error!!!, ErrorCode:" + nRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRtnVal = -1;
                case -3 :
                    szMsg = "Execution Failed!!!, ErrorCode:" + nRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRtnVal = -1;
            }
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return nRtnVal = -1;
        }
        return nRtnVal;
    }

    /**
     * 오퍼레이션명 : C연주 설비고장복구실적 (Y1YDL004)[권오창 2009.09.09]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY1EqpTrblRcvrWr(JDTORecord msgRecord) throws DAOException {
        // 레코드 선언
        JDTORecord recPara             = null;
        JDTORecord setCrnschRecord     = null;

        // DAO 및 UTIL 객체 생성
        YdDelegate ydDelegate          = new YdDelegate();
        EJBConnector ejbConn           = null;

        // 변수선언
        String szMethodName            = "procY1EqpTrblRcvrWr";
        String szMsg                   = "";
        String szOperationName         = "C연주슬라브L2 설비고장복구실적";
        String szRcvTcCode             = null;
        String szYD_EQP_ID             = "";
        String szYD_EQP_STAT           = "";
        String szYD_EQP_PAUSE_CODE     = "";
        String szYD_EQP_TRBL_RCVR_DT   = "";
        String szYD_EQP_STAT_UPD       = "";
        int nRet                       = 0;

        szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
            // Log 테이블 등록
            //=============================================================
            szMsg = "[C연주정정] 설비고장복구실적 수신";
            ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            setCrnschRecord = JDTORecordFactory.getInstance().create();

            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            if(szYD_EQP_ID.equals("")){
                szMsg = "설비ID가 존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_STAT");
            if(szYD_EQP_STAT.equals("")){
                szMsg = "설비상태가  존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            szYD_EQP_PAUSE_CODE   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
            if(szYD_EQP_PAUSE_CODE.equals("")){
                szMsg = "휴지코드가  존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            szYD_EQP_TRBL_RCVR_DT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_TRBL_RCVR_DT");
            if(szYD_EQP_TRBL_RCVR_DT.equals("")){
                szMsg = "야드설비고장복구일시가  존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            //============================================================================
            // 변환...
            //============================================================================
            if(szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)){
                // 고장
                szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_BREAK;
                if(szYD_EQP_PAUSE_CODE.equals("0000") ||
                   szYD_EQP_PAUSE_CODE.equals("")){
                    szYD_EQP_PAUSE_CODE = "B000";
                }else{
                    szYD_EQP_PAUSE_CODE   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
                }

            }else if(szYD_EQP_STAT.equals("R") ||
                     szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_NORM)){
                // 복구
                szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_NORM;
                if(szYD_EQP_PAUSE_CODE.equals("0000") || szYD_EQP_PAUSE_CODE.equals("")){
                    szYD_EQP_PAUSE_CODE = "R000";
                }else{
                    szYD_EQP_PAUSE_CODE   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
                }
            }

            //============================================================================
            // 휴지테이블 업데이트
            // 고장이나 복구일 때 남김
            //============================================================================
            if(szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_BREAK) ||
               szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_NORM)){
                ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [2] 설비 휴지테이블에 야드 설비휴지코드 업데이트 처리", YdConstant.DEBUG);
                this.ProcEqpPause(szRcvTcCode, szYD_EQP_ID, szYD_EQP_STAT_UPD, szYD_EQP_PAUSE_CODE, szYD_EQP_TRBL_RCVR_DT);
            }

            //============================================================================
            // 크레인 설비인 경우 설비 작업 상태 및 스케줄 변경
            //============================================================================
            String lzRtnMsg = null;

            if (szYD_EQP_ID.substring(2,4).equals(YdConstant.YD_EQP_GP_CRANE)){

                // 고장 UPDATE 시
                if(szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_BREAK)){
                    // 해당 설비 스케줄이 권상지시(YD_EQP_STAT_UP_WO) 일경우 IDLE 상태로 변경 YD_EQP_STAT_IDLE

                    lzRtnMsg = this.updCrnWrkProgStatUpWoToIdle(szYD_EQP_ID);

                    if(lzRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
                        szMsg = "스케줄 변경 성공 하였습니다.";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }else if (lzRtnMsg.equals(YdConstant.RETN_CD_FAILURE)){
                        szMsg = "스케줄 변경 실패 하였습니다.";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }
                }

                // 정상 UPDATE 시
                else{
                    szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_IDLE;
                }
            }
            //============================================================================
            // 설비테이블에 야드설비상태 업데이트
            //============================================================================
            ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [3] 설비 테이블에 업데이트 처리", YdConstant.DEBUG);
            setCrnschRecord.setField("YD_EQP_ID"  , szYD_EQP_ID);       // 설비ID
            setCrnschRecord.setField("YD_EQP_STAT", szYD_EQP_STAT_UPD); // "B": 고장, "W": 대기
            setCrnschRecord.setField("MODIFIER"   , szRcvTcCode);
            nRet = this.Y1YDL004UpdYdEqp(setCrnschRecord, 0);
            if(nRet == -1){
                szMsg = "설비테이블 업데이트 중  Error : (YD_EQP_ID : " + szYD_EQP_ID + "),(YD_EQP_STAT : " + szYD_EQP_STAT + "), Ret : " + nRet;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                throw new JDTOException(szMsg);
            }

            //------------------------------------------------------------------
            // C 연주 설비정보변경시
            //------------------------------------------------------------------
            JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
            recFlex.setField("YD_GP",  YdConstant.YD_GP_C_SLAB_YARD);
            recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putYdFlexCrnWrk("", recFlex);

            //==================================================================
            // 아래에 JMS 호출하는것이 문제가 될 시 직접 호출하는 것으로 처리해야 됨
            // 2009.10.15
            // 권오창
            // 설비고장복구실적 전문 수신하여 설비 테이블에 업데이트 후
            // 김진욱 리스케줄 호출을 직접 호출
            //
            // 파라미터 : 설비ID
            // 연주 슬라브 야드  => procY1CrnReSch()
            //==================================================================
//          JDTORecord SndRecord       = null;
//          SndRecord = JDTORecordFactory.getInstance().create();
//          SndRecord.setField("YD_EQP_ID", getparamRecord.getFieldString("YD_EQP_ID"));
//          objCrnResch.procY1CrnReSch(SndRecord);

            /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
             * YD_EQP_STAT (R: 복구, B: 고장)에 따른 업무 정의
             * R: 복구 - 크레인 리스케줄링 호출[복구], C연주 크레인 작업지시 호출[YD_EQP_ID, YD_WRK_PROG_STAT(W)]
             * B: 고장 - 크레인 리스케줄링 호출[고장]
             * 수정자 : 임춘수
             * 일자 : 2009.06.17
             +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            if( szYD_EQP_STAT.equals("R") ||
                szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_NORM) ) {           // R: 복구
                recPara = JDTORecordFactory.getInstance().create();
                //서버이중화로 인한 JMS 호출 순서가 바뀔 수 있으므로 해결 필요.
                //크레인 리스케줄링 호출[복구]
                recPara.setField("MSG_ID",           "YDYDJ502");
                recPara.setField("YD_EQP_ID", szYD_EQP_ID);

                ejbConn = new EJBConnector("default", this);
                ejbConn.trx("CrnReSchSeEJB", "procY1CrnReSch", recPara);

                szMsg = "[C연주 설비고장복구실적]크레인 리스케줄링 호출[복구]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                szMsg = "[C연주 설비고장복구실적]C연주 크레인 작업지시 호출";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            }else if( szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {     //B: 고장
                //크레인 리스케줄링 호출[고장]
                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("MSG_ID",   "YDYDJ502");
                recPara.setField("YD_EQP_ID", szYD_EQP_ID);

                ejbConn = new EJBConnector("default", this);
                ejbConn.trx("CrnReSchSeEJB", "procY1CrnReSch", recPara);

                szMsg = "[C연주 설비고장복구실적]크레인 리스케줄링 호출[고장]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            }else{                                          // 정의되지 않은 값
                szMsg = "[C연주 설비고장복구실적]야드설비상태[" + szYD_EQP_STAT + "]가 정의되지 않은 값입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }
            /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/


            /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
             * 업무 : 크레인작업실적응답 전문 전송(YDY1L005)
             * 수정자 : 임춘수
             * 일자 : 2009.06.17
             +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("MSG_ID"        , "YDY1L005");
            recPara.setField("YD_EQP_ID"     , szYD_EQP_ID);
            recPara.setField("YD_L2_WR_GP"   , "R");            //U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경
            recPara.setField("YD_L3_HD_RS_CD", "0000");         //야드L3처리결과코드
            ydDelegate.sendMsg(recPara);
            /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

        }catch(Exception e) {
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new DAOException(szMsg);
        }
    }

    /**
     * 오퍼레이션명 : 해당 크레인 설비 스케줄 작업 상태를 IDLE 상태로 변경
     *
     * @param  ● Stirng pYdEqpId
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public String  updCrnWrkProgStatUpWoToIdle(String pYdEqpId) throws JDTOException {
        // 레코드선언
        JDTORecord setRecord = null;
        JDTORecord recRecord = null;

        // 변수 선언
        String szMethodName  = "updCrnWrkProgStatUpWoToIdle";
        String szMsg         = "" ;
        String szOperationName = "설비 스케줄 작업 상태 IDLE로 변경";

        //JDTORecordSet
        JDTORecordSet rsResult     = null;

        //DAO
        YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();

        int intRtnVal = 0;

        try{
            szMsg = "[ " +szOperationName + "] 메소드 시작 ";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //------------------------------------------------------------------------------------------------
            // 설비 ID 로 크레인 작업지시가 내려간 스케줄 조회
            //------------------------------------------------------------------------------------------------
            recRecord = JDTORecordFactory.getInstance().create();
            recRecord.setField("YD_EQP_ID"          , pYdEqpId);
            recRecord.setField("YD_WRK_PROG_STAT"   , YdConstant.YD_EQP_STAT_UP_WO);

            rsResult = JDTORecordFactory.getInstance().createRecordSet("rsResult");

            intRtnVal = ydCrnSchDao.getYdCrnsch(recRecord, rsResult, 46);

            if( intRtnVal < 0 ){
                szMsg = "[ " +szOperationName + "] 스케줄 조회 ERROR";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                return YdConstant.RETN_CD_FAILURE;

            } else if (intRtnVal == 0){
                szMsg = "[ " +szOperationName + "] 변경할 데이터가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                return YdConstant.RETN_CD_SUCCESS;
            }
            szMsg = "[ " +szOperationName + "] 크레인 스케줄 조회 성공!";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            //------------------------------------------------------------------------------------------------

            //------------------------------------------------------------------------------------------------
            // 크레인 스케줄 정보 IDLE 상태로 변경
            //------------------------------------------------------------------------------------------------
            recRecord = JDTORecordFactory.getInstance().create();
            setRecord = JDTORecordFactory.getInstance().create();
            rsResult.first();
            recRecord = rsResult.getRecord();

            setRecord.setField("YD_WRK_PROG_STAT", YdConstant.YD_EQP_STAT_IDLE);
            setRecord.setField("MODIFIER", "updCrnWrk");
            setRecord.setField("YD_CRN_SCH_ID", recRecord.getField("YD_CRN_SCH_ID"));

            intRtnVal = ydCrnSchDao.updYdCrnsch(setRecord, 0);

            if(intRtnVal < 0 ){

                szMsg = "[ " +szOperationName + "] UPDATE ERROR";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return YdConstant.RETN_CD_FAILURE;

            } else if (intRtnVal == 0 ){
                szMsg = "[ " +szOperationName + "] UPDATE 할 스케줄이 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                return YdConstant.RETN_CD_SUCCESS;
            }

            szMsg = "[ " +szOperationName + "] 스케줄 ID : " + recRecord.getFieldString("YD_CRN_SCH_ID") + "정보를 변경하였습니다.";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            //------------------------------------------------------------------------------------------------

            szMsg = "[ " +szOperationName + "] 메소드 끝 ";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return YdConstant.RETN_CD_FAILURE;
        }

        return YdConstant.RETN_CD_SUCCESS;
    }


    /**
     * 오퍼레이션명 : 해당 크레인 설비 스케줄 작업 상태를 IDLE 상태로 변경
     *
     * @param  ● Stirng pYdEqpId
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public String  updEqpWrkStatUpToNow(String pYdEqpId) throws JDTOException {
        // 레코드선언
        JDTORecord setRecord = null;
        JDTORecord recRecord = null;

        // 변수 선언
        String szMethodName  = "updEqpWrkStatUpToNow";
        String szMsg         = "" ;
        String szOperationName = "설비 상태 현재 스케줄상태로 변경";
        String sQueryId = "";
        String szSTAT_TO = "";

        //JDTORecordSet
        JDTORecordSet rsResult     = null;

        //DAO
        YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
        YdEqpDao ydEqpDao = new YdEqpDao();
        JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();

        int intRtnVal = 0;

        try{
            szMsg = "[ " +szOperationName + "] 메소드 시작 ";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //------------------------------------------------------------------------------------------------
            // 설비 ID 로 크레인 작업지시가 내려간 스케줄 조회
            //------------------------------------------------------------------------------------------------
            recRecord = JDTORecordFactory.getInstance().create();
            recRecord.setField("YD_EQP_ID"          , pYdEqpId);

            rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");

            sQueryId = "com.inisteel.cim.yd.dao.ydeqpdao.YdCrnSchDao.YdGetYdCrnSchProgbyEqpId";
            intRtnVal = ydCommDao.select(recRecord, rsResult, sQueryId);


            if( intRtnVal < 0 ){
                szMsg = "[ " +szOperationName + "] 스케줄의 상태 조회 ERROR";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                return YdConstant.RETN_CD_FAILURE;

            } else if (intRtnVal == 0){
                szMsg = "[ " +szOperationName + "] 현재 선택된 스케쥴이 없습니다. STAT=W update";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                szSTAT_TO = "W";
            }
            szMsg = "[ " +szOperationName + "] 크레인 스케줄의 설비 상태 조회 성공!";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            //------------------------------------------------------------------------------------------------

            //------------------------------------------------------------------------------------------------
            // 크레인 설비 상태 정보를 현재 크레인 상태로 변경
            //------------------------------------------------------------------------------------------------
            recRecord = JDTORecordFactory.getInstance().create();
            setRecord = JDTORecordFactory.getInstance().create();
            rsResult.first();
            recRecord = rsResult.getRecord();


            if(!"W".equals(szSTAT_TO)){
                szSTAT_TO = recRecord.getField("STAT_TO").toString();
            }

            szMsg = "[ " +szOperationName + "] 메소드 끝 ";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return szSTAT_TO;
        }

        return szSTAT_TO;
    }


    /**
     * 오퍼레이션명 : C연주 야드 설비테이블 설비상태 업데이트
     *
     * @param  ● msgRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int Y1YDL004UpdYdEqp(JDTORecord msgRecord, int intGp) throws JDTOException {
        // DAO 생성
        YdEqpDao ydEqpDao = new YdEqpDao();

        // 변수 선언
        String szMethodName = "Y1YDL004UpdYdEqp";
        String szMsg        = "";
        int nRet            = 0;

        try{
            nRet = ydEqpDao.updYdEqp(msgRecord, intGp);

            switch(nRet){
                case 0 :
                    szMsg = "No Data Found!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -1 :
                    szMsg = "Dup_val_on_index!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -2 :
                    szMsg = "Parameter Error!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -3 :
                    szMsg = "Execution Failed!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
            }
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return nRet = -1;
        }
        return nRet;
    }

    /**
     * 오퍼레이션명 : Y1크레인현재위치 (Y1YDL005)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY1CrnCurrLoc(JDTORecord msgRecord) throws JDTOException {

        // 레코드 선언
        JDTORecord setCrnschRecord = null;

        // 변수 선언
        String szMethodName        = "procY1CrnCurrLoc";
        String szMsg               = "";
        String szOperationName     = "C연주슬라브L2 크레인현재위치";
        String szRcvTcCode         = ydUtils.getTcCode(msgRecord);
        String szYD_EQP_ID         = "";
        String szYD_CRN_XAXIS      = "";
        String szYD_CRN_YAXIS      = "";
        int nRtnVal                = 0;

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
            // Log 테이블 등록
            //=============================================================
            szMsg = "[C연주정정] 크레인현재위치 수신";
            ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            setCrnschRecord = JDTORecordFactory.getInstance().create();

            //=================================================================
            // 수신받은 크레인 위치정보 화면으로 호출
            //=================================================================
//          for(int i=0; i<20; i++){
//              szYD_EQP_ID    = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID" + (i+1));
//              szYD_CRN_XAXIS = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS" + (i+1));
//              szYD_CRN_YAXIS = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS" + (i+1));
//
//              // 존재하는 설비ID에 대한 위치정보 Flex화면에서 보여지기 위한 호출
//              if(!szYD_EQP_ID.trim().equals("")){
//                  //ydUtils.putYdFlexCrnPos(YdConstant.YD_MONITORING_CHANNEL_A, YdConstant.YD_GP_C_SLAB_YARD, szYD_EQP_ID, Integer.parseInt(szYD_CRN_XAXIS), Integer.parseInt(szYD_CRN_YAXIS));
//              }
//          }

            ydUtils.putYdCrnPosMult(YdConstant.YD_MONITORING_CHANNEL_A, YdConstant.YD_GP_C_SLAB_YARD,msgRecord);

        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }
    }

    /**
     * 오퍼레이션명 : A후판 설비운전모드전환 (Y3YDL003)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY3EqpDrvMdTurnov(JDTORecord msgRecord) throws DAOException {

        // 레코드 선언
        JDTORecord recPara         = null;
        JDTORecord setCrnschRecord = null;

        // DAO 및 UTIL 객체 생성
        YdDelegate ydDelegate      = new YdDelegate();
        EJBConnector ejbConn       = null;

        // 변수 선언
        String szMethodName        = "procY3EqpDrvMdTurnov";
        String szMsg               = "";
        String szOperationName     = "A후판슬라브L2 설비운전모드전환";
        String szRcvTcCode         = null;
        String szYD_EQP_ID         = "";
        String szYD_EQP_WRK_MODE   = "";
        int nRet                   = 0;

        szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판슬라브야드] 설비운전모드전환 수신";
            ydUtils.putLogMsg("D", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            setCrnschRecord = JDTORecordFactory.getInstance().create();

            szYD_EQP_ID         = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            szYD_EQP_WRK_MODE   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE");

            setCrnschRecord.setField("YD_EQP_ID"      , szYD_EQP_ID);       // 설비ID
            setCrnschRecord.setField("YD_EQP_WRK_MODE", szYD_EQP_WRK_MODE); // 1: On-Line, "2": Off-Line
            setCrnschRecord.setField("MODIFIER" , szRcvTcCode);

            nRet = this.Y3YDL003UpdYdEqp(setCrnschRecord, 0);

            if(nRet == -1){
                szMsg = "설비테이블 업데이트 중  Error : (YD_EQP_ID : " + szYD_EQP_ID + "), (YD_EQP_WRK_MODE : " + szYD_EQP_WRK_MODE + "), Ret : " + nRet;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
             * YD_EQP_WRK_MODE (1: On-Line, 2: Off-Line)에 따른 업무 정의
             * 1: On-Line - 크레인 리스케줄 호출[복구], A후판 크레인 작업지시 호출[YD_EQP_ID, YD_WRK_PROG_STAT(W)]
             * 2: Off-Line - 크레인 리스케줄 호출[고장]
             * 수정자 : 임춘수
             * 일자 : 2009.06.17
             +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            if( szYD_EQP_WRK_MODE.equals("1") ) {           // 1: On-Line
                recPara = JDTORecordFactory.getInstance().create();
                //서버이중화로 인한 JMS 호출 순서가 바뀔 수 있으므로 해결 필요.
                //크레인 리스케줄 호출[복구]
                recPara.setField("MSG_ID",           "YDYDJ505");
                recPara.setField("YD_EQP_ID", szYD_EQP_ID);

                //------------------------------------------------------------------------------------------------
                // EJB 호출로 변경 (2010.01.14) : 이현성
                //------------------------------------------------------------------------------------------------
                // ydDelegate.sendMsg(recPara);
                ejbConn = new EJBConnector("default", this);
                ejbConn.trx("CrnReSchSeEJB", "procY3CrnReSch", recPara);
                //------------------------------------------------------------------------------------------------

                szMsg = "[A후판 설비운전모드전환]A후판 크레인 리스케줄 호출[복구]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            }else if( szYD_EQP_WRK_MODE.equals("2")) {      // 2: Off-Line
                //크레인 리스케줄 호출[고장]
                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("MSG_ID",           "YDYDJ505");
                recPara.setField("YD_EQP_ID", szYD_EQP_ID);

                //------------------------------------------------------------------------------------------------
                // EJB 호출로 변경 (2010.01.14) : 이현성
                //------------------------------------------------------------------------------------------------
                // ydDelegate.sendMsg(recPara);
                ejbConn = new EJBConnector("default", this);
                ejbConn.trx("CrnReSchSeEJB", "procY3CrnReSch", recPara);
                //------------------------------------------------------------------------------------------------

                szMsg = "[A후판 설비운전모드전환]A후판 크레인 리스케줄 호출[고장]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            }else{                                          // 정의되지 않은 값
                szMsg = "[A후판 설비운전모드전환]야드설비작업Mode[" + szYD_EQP_WRK_MODE + "]가 정의되지 않은 값입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                return;
            }


            //------------------------------------------------------------------
            //A 후판 슬라브야드 복구 스케줄 설비정보변경시
            //------------------------------------------------------------------
            JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
            recFlex.setField("YD_GP",  YdConstant.YD_GP_A_PLATE_SLAB_YARD);
            recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putYdFlexCrnWrk("", recFlex);
            /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

            /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
             * 업무 : 크레인작업실적응답 전문 전송(YDY3L005)
             * 수정자 : 임춘수
             * 일자 : 2009.06.17
             +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("MSG_ID"        , "YDY3L005");
            recPara.setField("YD_EQP_ID"     , szYD_EQP_ID);
            recPara.setField("YD_L2_WR_GP"   , "M");            //U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경
            recPara.setField("YD_L3_HD_RS_CD", "0000");         //야드L3처리결과코드
            ydDelegate.sendMsg(recPara);
            /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

        }catch(Exception e) {
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new DAOException(szMsg);
        }
    }

    /**
     * 오퍼레이션명 : A후판 야드 설비테이블 야드설비작업Mode 업데이트
     *
     * @param  ● msgRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int Y3YDL003UpdYdEqp(JDTORecord msgRecord, int intGp) throws JDTOException {
        // DAO 생성
        YdEqpDao ydEqpDao   = new YdEqpDao();

        // 변수 선언
        String szMethodName = "Y3YDL003UpdYdEqp";
        String szMsg        = "";
        int nRet            = 0;

        try{
            nRet = ydEqpDao.updYdEqp(msgRecord, intGp);

            switch(nRet){
                case 0 :
                    szMsg = "No Data Found!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -1 :
                    szMsg = "Dup_val_on_index!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -2 :
                    szMsg = "Parameter Error!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -3 :
                    szMsg = "Execution Failed!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
            }
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return nRet = -1;
        }

        return nRet;
    }

    /**
     * 오퍼레이션명 : A후판 설비고장복구실적
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY3EqpTrblRcvrWr(JDTORecord msgRecord) throws DAOException {
        // 레코드 선언
        JDTORecord recPara             = null;
        JDTORecord setCrnschRecord     = null;

        // DAO 및 UTIL 객체 생성
        YdDelegate ydDelegate          = new YdDelegate();
        EJBConnector ejbConn           = null;

        // 변수 선언
        String szMethodName            = "procY3EqpTrblRcvrWr";
        String szMsg                   = "";
        String szOperationName         = "A후판슬라브L2 설비고장복구실적";
        String szRcvTcCode             = null;
        String szYD_EQP_ID             = "";
        String szYD_EQP_STAT           = "";
        String szYD_EQP_PAUSE_CODE     = "";
        String szYD_EQP_TRBL_RCVR_DT   = "";
        String szYD_EQP_STAT_UPD       = "";
        int nRet                       = 0;

        szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판슬라브야드] 설비고장복구실적 수신";
            ydUtils.putLogMsg("D",YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            setCrnschRecord = JDTORecordFactory.getInstance().create();

            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            if(szYD_EQP_ID.equals("")){
                szMsg = "설비ID가 존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_STAT");
            if(szYD_EQP_STAT.equals("")){
                szMsg = "설비상태가  존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            szYD_EQP_PAUSE_CODE = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
            if(szYD_EQP_PAUSE_CODE.equals("")){
                szMsg = "휴지코드가  존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            szYD_EQP_TRBL_RCVR_DT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_TRBL_RCVR_DT");
            if(szYD_EQP_TRBL_RCVR_DT.equals("")){
                szMsg = "야드설비고장복구일시가  존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            //============================================================================
            // 변환...
            //============================================================================
            if(szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)){
                // 고장
                szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_BREAK;
                if(szYD_EQP_PAUSE_CODE.equals("0000") ||
                   szYD_EQP_PAUSE_CODE.equals("")){
                    szYD_EQP_PAUSE_CODE = "B000";
                }else{
                    szYD_EQP_PAUSE_CODE   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
                }
            }else if(szYD_EQP_STAT.equals("R") ||
                     szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_NORM)){
                // 복구
                szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_NORM;
                if(szYD_EQP_PAUSE_CODE.equals("0000") || szYD_EQP_PAUSE_CODE.equals("")){
                    szYD_EQP_PAUSE_CODE = "R000";
                }else{
                    szYD_EQP_PAUSE_CODE   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
                }
            }

            //============================================================================
            // 휴지테이블 업데이트
            // 고장이나 복구일 때 남김
            //============================================================================
            if(szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_BREAK) ||
               szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_NORM)){
                ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [2] 휴지 테이블 업데이트 처리", YdConstant.DEBUG);
                this.ProcEqpPause(szRcvTcCode, szYD_EQP_ID, szYD_EQP_STAT_UPD, szYD_EQP_PAUSE_CODE, szYD_EQP_TRBL_RCVR_DT);
            }

            //============================================================================
            // 크레인 설비인 경우 설비 작업 상태 및 스케줄 변경
            //============================================================================
            String lzRtnMsg = null;

            if (szYD_EQP_ID.substring(2,4).equals(YdConstant.YD_EQP_GP_CRANE)){

                // 고장 UPDATE 시
                if(szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_BREAK)){
                    // 해당 설비 스케줄이 권상지시(YD_EQP_STAT_UP_WO) 일경우 IDLE 상태로 변경 YD_EQP_STAT_IDLE

                    lzRtnMsg = this.updCrnWrkProgStatUpWoToIdle(szYD_EQP_ID);

                    if(lzRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
                        szMsg = "스케줄 변경 성공 하였습니다.";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }else if (lzRtnMsg.equals(YdConstant.RETN_CD_FAILURE)){
                        szMsg = "스케줄 변경 실패 하였습니다.";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }
                }

                // 정상 UPDATE 시
                else{
                    szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_IDLE;
                }
            }

            //============================================================================
            // 설비테이블에 야드설비상태 업데이트
            //============================================================================
            ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [3] 설비 테이블 업데이트 처리", YdConstant.DEBUG);
            setCrnschRecord.setField("YD_EQP_ID"  , szYD_EQP_ID);       // 설비ID
            setCrnschRecord.setField("YD_EQP_STAT", szYD_EQP_STAT_UPD); // "B": 고장, "W": 대기
            setCrnschRecord.setField("MODIFIER"   , szRcvTcCode);
            nRet = this.Y3YDL004UpdYdEqp(setCrnschRecord, 0);
            if(nRet == -1){
                szMsg = "설비테이블 업데이트 중  Error : (YD_EQP_ID : " + szYD_EQP_ID + "),(YD_EQP_STAT : " + szYD_EQP_STAT + "), Ret : " + nRet;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                throw new JDTOException(szMsg);
            }

            //------------------------------------------------------------------
            //A후판 설비정보변경시
            //------------------------------------------------------------------
            JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
            recFlex.setField("YD_GP",  YdConstant.YD_GP_A_PLATE_SLAB_YARD);
            recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putYdFlexCrnWrk("", recFlex);

            //==================================================================
            // 아래에 JMS 호출하는것이 문제가 될 시 직접 호출하는 것으로 처리해야 됨
            // 2009.10.15
            // 권오창
            // 설비고장복구실적 전문 수신하여 설비 테이블에 업데이트 후
            // 김진욱 리스케줄 호출을 직접 호출
            //
            // 파라미터 : 설비ID
            // 후판 슬라브 야드  => procY3CrnReSch()
            //==================================================================
//          JDTORecord SndRecord       = null;
//          SndRecord = JDTORecordFactory.getInstance().create();
//          SndRecord.setField("YD_EQP_ID", szYD_EQP_ID);
//          objCrnResch.procY3CrnReSch(SndRecord);

            /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
             * YD_EQP_STAT (R: 복구, B: 고장)에 따른 업무 정의
             * R: 복구 - 크레인 리스케줄 호출[복구], A후판 크레인 작업지시 호출[YD_EQP_ID, YD_WRK_PROG_STAT(W)]
             * B: 고장 - 크레인 리스케줄 호출[고장]
             * 수정자 : 임춘수
             * 일자 : 2009.06.18
             +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            if( szYD_EQP_STAT.equals("R") ||
                szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_NORM) ) {           // R: 복구
                recPara = JDTORecordFactory.getInstance().create();
                //서버이중화로 인한 JMS 호출 순서가 바뀔 수 있으므로 해결 필요.
                //크레인 리스케줄 호출[복구]
                recPara.setField("MSG_ID",    "YDYDJ505");
                recPara.setField("YD_EQP_ID", szYD_EQP_ID);

                ejbConn = new EJBConnector("default", this);
                ejbConn.trx("CrnReSchSeEJB", "procY3CrnReSch", recPara);

                szMsg = "[A후판 설비고장복구실적]A후판 크레인 리스케줄 호출[복구]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            }else if( szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {     //B: 고장
                //크레인 리스케줄 호출[고장]
                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("MSG_ID",    "YDYDJ505");
                recPara.setField("YD_EQP_ID", szYD_EQP_ID);

                ejbConn = new EJBConnector("default", this);
                ejbConn.trx("CrnReSchSeEJB", "procY3CrnReSch", recPara);

                szMsg = "[A후판 설비고장복구실적]A후판 크레인 리스케줄 호출[고장]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            }else{                                          // 정의되지 않은 값
                szMsg = "[A후판 설비고장복구실적]야드설비상태[" + szYD_EQP_STAT + "]가 정의되지 않은 값입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

            /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
             * 업무 : 크레인작업실적응답 전문 전송(YDY3L005)
             * 수정자 : 임춘수
             * 일자 : 2009.06.17
             +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("MSG_ID"        , "YDY3L005");
            recPara.setField("YD_EQP_ID"     , szYD_EQP_ID);
            recPara.setField("YD_L2_WR_GP"   , "R");            //U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경
            recPara.setField("YD_L3_HD_RS_CD", "0000");         //야드L3처리결과코드
            ydDelegate.sendMsg(recPara);
            /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        }catch(Exception e) {
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new DAOException(szMsg);
        }
    }

    /**
     * 오퍼레이션명 : A후판 야드 설비테이블 설비상태 업데이트 [권오창 2009.09.09]
     *
     * @param  ● msgRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int Y3YDL004UpdYdEqp(JDTORecord msgRecord, int intGp) throws JDTOException {
        // DAO 생성
        YdEqpDao ydEqpDao   = new YdEqpDao();

        // 변수 선언
        String szMethodName = "Y3YDL004UpdYdEqp";
        String szMsg        = "";
        int nRet            = 0;

        try{
            nRet = ydEqpDao.updYdEqp(msgRecord, intGp);

            switch(nRet){
                case 0 :
                    szMsg = "No Data Found!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -1 :
                    szMsg = "Dup_val_on_index!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -2 :
                    szMsg = "Parameter Error!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -3 :
                    szMsg = "Execution Failed!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
            }
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return nRet = -1;
        }
        return nRet;
    }

    /**
     * 오퍼레이션명 : Y3 크레인현재위치 (Y3YDL005)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY3CrnCurrLoc(JDTORecord msgRecord) throws DAOException {
        // 레코드 선언
        JDTORecord setCrnschRecord = null;

        // 변수 선언
        String szMethodName        = "procY3CrnCurrLoc";
        String szMsg               = "";
        String szOperationName     = "A후판슬라브L2 크레인현재위치";
        String szRcvTcCode         = null;
        String szYD_EQP_ID         = "";
        String szYD_CRN_XAXIS      = "";
        String szYD_CRN_YAXIS      = "";
        int nRtnVal                = 0;

        szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판슬라브야드] 크레인현재위치 수신";
            ydUtils.putLogMsg("D", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            setCrnschRecord = JDTORecordFactory.getInstance().create();

            //=================================================================
            // 수신받은 크레인 위치정보 화면으로 호출
            //=================================================================
//          for(int i=0; i<20; i++){
//              szYD_EQP_ID    = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID" + (i+1));
//              szYD_CRN_XAXIS = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS" + (i+1));
//              szYD_CRN_YAXIS = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS" + (i+1));
//
//              // 존재하는 설비ID에 대한 위치정보 Flex화면에서 보여지기 위한 호출
//              if(!szYD_EQP_ID.trim().equals("")){
//                  //ydUtils.putYdFlexCrnPos("yd_monitorD", YdConstant.YD_GP_A_PLATE_SLAB_YARD, szYD_EQP_ID, Integer.parseInt(szYD_CRN_XAXIS), Integer.parseInt(szYD_CRN_YAXIS));
//              }
//          }

            ydUtils.putYdCrnPosMult(YdConstant.YD_MONITORING_CHANNEL_D, YdConstant.YD_GP_A_PLATE_SLAB_YARD,msgRecord);

        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new DAOException(szMsg);
        }
    }

    /**
     * 오퍼레이션명 : Y3 수불구용도변경요구 (Y3YDL011)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY3TkovlocUsgMod(JDTORecord msgRecord) throws DAOException {
        // 레코드 선언
        JDTORecord recPara         = null;

        // DAO객체 선언
        YdStkBedDao ydStkBedDao    = new YdStkBedDao();

        // 변수선언
        String szMethodName        = "procY3TkovlocUsgMod";
        String szMsg               = "";
        String szOperationName     = "A후판슬라브L2 수불구용도변경요구";
        String szRcvTcCode         = null;
        int nRet                   = 0;

        szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판슬라브야드] 수불구용도변경요구 수신";
            ydUtils.putLogMsg("D", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            for(int i=0; i < 6; i++){
                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("YD_STK_COL_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"), 6, 1));
                recPara.setField("YD_STK_BED_NO", YdUtils.fillSpZr(Integer.toString(i+1), 2, 0));
                recPara.setField("YD_STK_BED_ACT_STAT", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_ACT_STAT" + (i+1)), 1, 1));
                nRet = ydStkBedDao.updYdStkbed(recPara, 0);
            }
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new DAOException(szMsg);
        }
    }

    /**
     * 오퍼레이션명 : 후판제품야드 설비운전모드전환 (Y4YDL003)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY4EqpDrvMdTurnov(JDTORecord msgRecord) throws DAOException {

        // 레코드 선언
        JDTORecord    recPara      = null;
        JDTORecord setCrnschRecord = null;

        // DAO 및 UTIL 객체 생성
        YdDelegate ydDelegate      = new YdDelegate();
        EJBConnector ejbConn       = null;

        // 변수선언
        String szMethodName        = "procY4EqpDrvMdTurnov";
        String szMsg               = "";
        String szOperationName     = "후판제품야드L2 설비운전모드전환";
        String szRcvTcCode         = null;
        String szYD_EQP_ID         = "";
        String szYD_EQP_WRK_MODE   = "";
        int nRet                   = 0;


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선 START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= msgRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8설비운전모드전환 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

        szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판제품야드] 설비운전모드전환 수신";
            ydUtils.putLogMsg("K", YdConstant.YD_MONITORING_CHANNEL_K, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            setCrnschRecord = JDTORecordFactory.getInstance().create();

            szYD_EQP_ID         = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            szYD_EQP_WRK_MODE   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE");

            setCrnschRecord.setField("YD_EQP_ID"        , szYD_EQP_ID);       // 설비ID
            setCrnschRecord.setField("YD_EQP_WRK_MODE"  , szYD_EQP_WRK_MODE); // 1: On-Line, 2: Off-Line
            setCrnschRecord.setField("MODIFIER"         , szRcvTcCode);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// Y4YDL003UpdYdEqp call 시  logId 항목 추가 개선
//          nRet = this.Y4YDL003UpdYdEqp(setCrnschRecord, 0);
            nRet = this.Y4YDL003UpdYdEqp(setCrnschRecord, 0, logId);
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

            if(nRet == -1){
                szMsg = "설비테이블 업데이트 중  Error : (YD_EQP_ID : " + szYD_EQP_ID + "), (YD_EQP_WRK_MODE : " + szYD_EQP_WRK_MODE + "), Ret : " + nRet;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            }

            /*
             * YD_EQP_WRK_MODE (1: On-Line, 2: Off-Line)에 따른 업무 정의
             * 1: On-Line - 크레인 리스케줄 호출[복구], C연주 크레인 작업지시 호출[YD_EQP_ID, YD_WRK_PROG_STAT(W)]
             * 2: Off-Line - 크레인 리스케줄 호출[고장]
             */
            if( szYD_EQP_WRK_MODE.equals("1") ) {           // 1: On-Line

                recPara = JDTORecordFactory.getInstance().create();
                //서버이중화로 인한 JMS 호출 순서가 바뀔 수 있으므로 해결 필요.
                //크레인 리스케줄 호출[복구]
                recPara.setField("MSG_ID",           "YDYDJ508");
                recPara.setField("YD_EQP_ID", szYD_EQP_ID);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선 START
// procY4CrnReSch call 시 recPara 에  logId 항목 추가 개선
////////////////////////////////////////////////////////////////////////////////////////
                recPara.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.02 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

                //------------------------------------------------------------------------------------------------
                // EJB 호출로 변경 (2010.01.14) : 이현성
                //------------------------------------------------------------------------------------------------
                // ydDelegate.sendMsg(recPara);
                ejbConn = new EJBConnector("default", this);
                ejbConn.trx("CrnReSchSeEJB", "procY4CrnReSch", recPara);
                //------------------------------------------------------------------------------------------------

                szMsg = "[제품창고 설비운전모드전환]제품창고 크레인 리스케줄 호출[복구]";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            }else if( szYD_EQP_WRK_MODE.equals("2")) {      // 2: Off-Line

                //크레인 리스케줄 호출[고장]
                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("MSG_ID",           "YDYDJ508");
                recPara.setField("YD_EQP_ID", szYD_EQP_ID);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선 START
// procY4CrnReSch call 시 recPara 에  logId 항목 추가 개선
////////////////////////////////////////////////////////////////////////////////////////
                recPara.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.02 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

                //------------------------------------------------------------------------------------------------
                // EJB 호출로 변경 (2010.01.14) : 이현성
                //------------------------------------------------------------------------------------------------
                // ydDelegate.sendMsg(recPara);
                ejbConn = new EJBConnector("default", this);
                ejbConn.trx("CrnReSchSeEJB", "procY4CrnReSch", recPara);
                //------------------------------------------------------------------------------------------------

                szMsg = "[제품창고 설비운전모드전환]제품창고 크레인 리스케줄 호출[고장]";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            }else{                                          // 정의되지 않은 값
                szMsg = "[제품창고 설비운전모드전환]야드설비작업Mode[" + szYD_EQP_WRK_MODE + "]가 정의되지 않은 값입니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return;
            }

            //------------------------------------------------------------------
            // 제품창고 스케줄 설비정보변경시
            //------------------------------------------------------------------
            JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
            //recFlex.setField("YD_GP",  YdConstant.YD_GP_PLATE_GDS_YARD);
            recFlex.setField("YD_GP",  szYD_EQP_ID.substring(0,1)); //--2013.02.15 수정 (3기)
            recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            ydUtils.putYdFlexCrnWrk("", recFlex);

            /*
             * 업무 : 크레인작업실적응답 전문 전송(YDY4L005/YDY8L008)
             */
            recPara = JDTORecordFactory.getInstance().create();
            if(szYD_EQP_ID.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD)) { //--2013.02.15 수정 (3기)
                recPara.setField("MSG_ID"        , "YDY8L005");
            } else {
                recPara.setField("MSG_ID"        , "YDY4L005");
            }
            recPara.setField("YD_EQP_ID"     , szYD_EQP_ID);
            recPara.setField("YD_L2_WR_GP"   , "M");            //U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경
            recPara.setField("YD_L3_HD_RS_CD", "0000");         //야드L3처리결과코드

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// recPara 에 logId 추가
            recPara.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

            ydDelegate.sendMsg(recPara);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
szMsg = "Y8설비운전모드전환 처리(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

        }catch(Exception e) {
            szMsg = "Error : " + e.getLocalizedMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new DAOException(szMsg);
        }
    }

    /**
     * 오퍼레이션명 : 후판제품야드 야드 설비테이블 야드설비작업Mode 업데이트
     *
     * @param  ● msgRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
      public int Y4YDL003UpdYdEqp(JDTORecord msgRecord, int intGp, String logId) throws JDTOException {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// Y4YDL003UpdYdEqp argument 에 logId 항목 추가 개선
// public int Y4YDL003UpdYdEqp(JDTORecord msgRecord, int intGp) throws JDTOException {
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        // DAO객체 선언
        YdEqpDao ydEqpDao   = new YdEqpDao();

        // 변수 선언
        String szMethodName = "Y4YDL003UpdYdEqp";
        String szMsg        = "";
        int nRet            = 0;
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
if(ydUtils.isEmpty(logId)) logId 		= ydUtils.getLogIdNew("T"); 			// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        try{
            nRet = ydEqpDao.updYdEqp(msgRecord, intGp);

            switch(nRet){
                case 0 :
                    szMsg = "No Data Found!!!, ErrorCode:" + nRet;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    return nRet = -1;
                case -1 :
                    szMsg = "Dup_val_on_index!!!, ErrorCode:" + nRet;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    return nRet = -1;
                case -2 :
                    szMsg = "Parameter Error!!!, ErrorCode:" + nRet;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    return nRet = -1;
                case -3 :
                    szMsg = "Execution Failed!!!, ErrorCode:" + nRet;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    return nRet = -1;
            }
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return nRet = -1;
        }
        return nRet;
    }

    /**
     * 오퍼레이션명 : 후판제품야드 설비고장복구실적 (Y4YDL004)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY4EqpTrblRcvrWr(JDTORecord msgRecord) throws DAOException {
        // 레코드 선언
        JDTORecord recPara             = null;
        JDTORecord setCrnschRecord     = null;

        // DAO 및 UTIL 객체 생성
        YdDelegate ydDelegate          = new YdDelegate();
        EJBConnector ejbConn           = null;

        // 변수 선언
        String szMethodName            = "procY4EqpTrblRcvrWr";
        String szMsg                   = "";
        String szOperationName         = "후판제품야드L2 설비고장복구실적";
        String szRcvTcCode             = null;
        String szYdGp                  = ""; //--2013.02.14 추가 (3기)
        String szYD_EQP_ID             = "";
        String szYD_EQP_STAT           = "";
        String szYD_EQP_PAUSE_CODE     = "";
        String szYD_EQP_TRBL_RCVR_DT   = "";
        String szYD_EQP_STAT_UPD       = "";
        int nRet                       = 0;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선 START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= msgRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8설비고장복구실적 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

        szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판제품야드] 설비고장복구실적 수신";
            ydUtils.putLogMsg("K", YdConstant.YD_MONITORING_CHANNEL_K , szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            setCrnschRecord = JDTORecordFactory.getInstance().create();

            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            if(szYD_EQP_ID.equals("")){
                szMsg = "설비ID가 존재하지 않습니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            }

            szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_STAT");
            if(szYD_EQP_STAT.equals("")){
                szMsg = "설비상태가  존재하지 않습니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            }

            szYD_EQP_PAUSE_CODE = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
            if(szYD_EQP_PAUSE_CODE.equals("")){
                szMsg = "휴지코드가  존재하지 않습니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            }

            szYD_EQP_TRBL_RCVR_DT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_TRBL_RCVR_DT");
            if(szYD_EQP_TRBL_RCVR_DT.equals("")){
                szMsg = "야드설비고장복구일시가  존재하지 않습니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            }

            //야드구분을 설비ID 첫 자리로 구분한다.
            if(szYD_EQP_ID.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD)) { //--2013.02.14 추가 (3기)
                szYdGp = YdConstant.YD_GP_PLATE2_GDS_YARD; //2후판제품창고
            } else {
                szYdGp = YdConstant.YD_GP_PLATE_GDS_YARD; //1후판제품창고
            }

            //============================================================================
            // 변환...
            //============================================================================
            if(szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)){
                // 고장
                szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_BREAK;
                if(szYD_EQP_PAUSE_CODE.equals("0000") || szYD_EQP_PAUSE_CODE.equals("")){
                    szYD_EQP_PAUSE_CODE = "B000";
                }else{
                    szYD_EQP_PAUSE_CODE   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
                }
            }else if(szYD_EQP_STAT.equals("R") ||
                     szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_NORM)){
                // 복구
                szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_NORM;
                if(szYD_EQP_PAUSE_CODE.equals("0000") || szYD_EQP_PAUSE_CODE.equals("")){
                    szYD_EQP_PAUSE_CODE = "R000";
                }else{
                    szYD_EQP_PAUSE_CODE   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
                }
            }

            //============================================================================
            // 휴지테이블 업데이트
            //
            // 고장이나 복구일 때 남김
            //============================================================================
            if(szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_BREAK) ||
               szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_NORM)){
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [2] 휴지 테이블에 업데이트 처리", YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMethodName + "::    [2] 휴지 테이블에 업데이트 처리", YdConstant.DEBUG, logId);
// 2024.09.02 로그 개선 관련 ProcEqpPause Method 를 다른 야드에서도 사용 하기 때문에
// 신규 ProcEqpPause 작성 : 기존 ProcEqpPause 처리 부분은 그대로 argument 에 logId 항목 추가 개선
//              this.ProcEqpPause(szRcvTcCode, szYD_EQP_ID, szYD_EQP_STAT_UPD, szYD_EQP_PAUSE_CODE, szYD_EQP_TRBL_RCVR_DT);
                this.ProcEqpPause(szRcvTcCode, szYD_EQP_ID, szYD_EQP_STAT_UPD, szYD_EQP_PAUSE_CODE, szYD_EQP_TRBL_RCVR_DT, logId);
            }

            //============================================================================
            // 크레인 설비인 경우 설비 작업 상태 및 스케줄 변경
            //============================================================================
            String lzRtnMsg = null;

            if (szYD_EQP_ID.substring(2,4).equals(YdConstant.YD_EQP_GP_CRANE)){

                // 고장 UPDATE 시
                if(szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_BREAK)){

                    // 해당 설비 스케줄이 권상지시(YD_EQP_STAT_UP_WO) 일경우 IDLE 상태로 변경 YD_EQP_STAT_IDLE
// 2024.09.02 로그 개선 관련 updCrnWrkProgStatUpWoToIdle Method 를 다른 야드에서도 사용 하기 때문에
// 신규 updCrnWrkProgStatUpWoToIdle 작성 : 기존 updCrnWrkProgStatUpWoToIdle 처리 부분은 그대로 argument 에 logId 항목 추가 개선
//                  lzRtnMsg = this.updCrnWrkProgStatUpWoToIdle(szYD_EQP_ID);
                    lzRtnMsg = this.updCrnWrkProgStatUpWoToIdle(szYD_EQP_ID, logId);

                    if(lzRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
                        szMsg = "스케줄 변경 성공 하였습니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                    }else if (lzRtnMsg.equals(YdConstant.RETN_CD_FAILURE)){
                        szMsg = "스케줄 변경 실패 하였습니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    }
                }

                // 정상 UPDATE 시
                else{
                    szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_IDLE;
                }
            }
            //============================================================================
            // 설비테이블에 야드설비상태 업데이트
            //============================================================================
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [3] 설비 테이블에 업데이트 처리", YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMethodName + "::    [3] 설비 테이블에 업데이트 처리", YdConstant.DEBUG, logId);
            setCrnschRecord.setField("YD_EQP_ID"  , szYD_EQP_ID);   // 설비ID
            setCrnschRecord.setField("YD_EQP_STAT", szYD_EQP_STAT_UPD); // "B": 고장, "W": 대기
            setCrnschRecord.setField("MODIFIER"   , szRcvTcCode);
            nRet = this.Y4YDL004UpdYdEqp(setCrnschRecord, 0);
            if(nRet == -1){
                szMsg = "설비테이블 업데이트 중  Error : (YD_EQP_ID : " + szYD_EQP_ID + "),(YD_EQP_STAT : " + szYD_EQP_STAT + "), Ret : " + nRet;
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                throw new JDTOException(szMsg);
            }

            //------------------------------------------------------------------
            //제품창고  설비정보변경시
            //------------------------------------------------------------------
            JDTORecord recFlex = JDTORecordFactory.getInstance().create();
            //recFlex.setField("YD_GP",  YdConstant.YD_GP_PLATE_GDS_YARD);
            recFlex.setField("YD_GP",  szYdGp); //--2013.02.14 수정 (3기)
            recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            ydUtils.putYdFlexCrnWrk("", recFlex);

            /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
             * YD_EQP_STAT (R: 복구, B: 고장)에 따른 업무 정의
             * R: 복구 - 크레인 리스케줄링 호출[복구], 제품창고 크레인 작업지시 호출[YD_EQP_ID, YD_WRK_PROG_STAT(W)]
             * B: 고장 - 크레인 리스케줄링 호출[고장]
             +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            if( szYD_EQP_STAT.equals("R") ||
                szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_NORM) ) {           // R: 복구
                recPara = JDTORecordFactory.getInstance().create();
                //서버이중화로 인한 JMS 호출 순서가 바뀔 수 있으므로 해결 필요.
                //크레인 리스케줄링 호출[복구]
                recPara.setField("MSG_ID",    "YDYDJ508");
                recPara.setField("YD_EQP_ID", szYD_EQP_ID);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선 START
// procY4CrnReSch call 시 recPara 에  logId 항목 추가 개선
////////////////////////////////////////////////////////////////////////////////////////
                recPara.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.02 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

                ejbConn = new EJBConnector("default", this);
                ejbConn.trx("CrnReSchSeEJB", "procY4CrnReSch", recPara);

                szMsg = "[제품창고 설비고장복구실적]크레인 리스케줄링 호출[복구]";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            }else if( szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {     //B: 고장
                //크레인 리스케줄링 호출[고장]
                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("MSG_ID",    "YDYDJ508");
                recPara.setField("YD_EQP_ID", szYD_EQP_ID);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선 START
// procY4CrnReSch call 시 recPara 에  logId 항목 추가 개선
////////////////////////////////////////////////////////////////////////////////////////
                recPara.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.02 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

                ejbConn = new EJBConnector("default", this);
                ejbConn.trx("CrnReSchSeEJB", "procY4CrnReSch", recPara);

                szMsg = "[제품창고 설비고장복구실적]크레인 리스케줄링 호출[고장]";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            }else{                                          // 정의되지 않은 값
                szMsg = "[제품창고 설비고장복구실적]야드설비상태[" + szYD_EQP_STAT + "]가 정의되지 않은 값입니다.";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return;
            }

            /*
             * 업무 : 크레인작업실적응답 전문 전송(YDY4L005/YDY8L005)
             */
            recPara = JDTORecordFactory.getInstance().create();
            if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) {   //--2013.02.14 수정 (3기)
                recPara.setField("MSG_ID"        , "YDY8L005");
            } else {
                recPara.setField("MSG_ID"        , "YDY4L005");
            }
            recPara.setField("YD_EQP_ID"     , szYD_EQP_ID);
            recPara.setField("YD_L2_WR_GP"   , szYD_EQP_STAT);          //B:고장,R:복구
            recPara.setField("YD_L3_HD_RS_CD", "0000");                 //야드L3처리결과코드

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// recPara 에 logId 추가
recPara.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

            ydDelegate.sendMsg(recPara);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
szMsg = "Y8설비고장복구실적 처리(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

        }catch(Exception e) {
            szMsg = "Error : " + e.getLocalizedMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            throw new DAOException(szMsg);
        }
    }

    /**
     * 오퍼레이션명 : 후판제품야드 야드 설비테이블 설비상태 업데이트
     *
     * @param  ● msgRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int Y4YDL004UpdYdEqp(JDTORecord msgRecord, int intGp) throws JDTOException {
        // DAO객체 생성
        YdEqpDao ydEqpDao   = new YdEqpDao();

        // 변수 선언
        String szMethodName = "Y4YDL004UpdYdEqp";
        String szMsg        = "";
        int nRet            = 0;

        try{
            nRet = ydEqpDao.updYdEqp(msgRecord, intGp);

            switch(nRet){
                case 0 :
                    szMsg = "No Data Found!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -1 :
                    szMsg = "Dup_val_on_index!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -2 :
                    szMsg = "Parameter Error!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -3 :
                    szMsg = "Execution Failed!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
            }
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return nRet = -1;
        }
        return nRet;
    }

    /**
     * 오퍼레이션명 : Y4크레인현재위치 (Y4YDL005)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY4CrnCurrLoc(JDTORecord msgRecord) throws DAOException {
        // 레코드 선언
        JDTORecord setCrnschRecord = null;

        // 변수 선언
        String szMethodName        = "procY4CrnCurrLoc";
        String szMsg               = "";
        String szOperationName     = "후판제품야드L2 크레인현재위치";
        String szRcvTcCode         = null;
        String szYD_EQP_ID         = "";
        String szYD_CRN_XAXIS      = "";
        String szYD_CRN_YAXIS      = "";
        int nRet                   = 0;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선 START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= msgRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "후판제품야드L2 크레인현재위치 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

        szRcvTcCode = ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode == null){
            szMsg = szSessionName + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
//2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return ;
        }

        if(bDebugFlag){
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
//2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
        }

        try{
            //=============================================================
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판제품야드] Y4크레인현재위치 수신";
            ydUtils.putLogMsg("K",YdConstant.YD_MONITORING_CHANNEL_K, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            setCrnschRecord = JDTORecordFactory.getInstance().create();

            //=================================================================
            // 수신받은 크레인 위치정보 화면으로 호출
            //=================================================================
//          for(int i=0; i<20; i++){
//              szYD_EQP_ID    = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID" + (i+1));
//              szYD_CRN_XAXIS = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS" + (i+1));
//              szYD_CRN_YAXIS = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS" + (i+1));
//
//              // 존재하는 설비ID에 대한 위치정보 Flex화면에서 보여지기 위한 호출
//              if(!szYD_EQP_ID.trim().equals("")){
//                  //ydUtils.putYdFlexCrnPos("yd_monitorK", YdConstant.YD_GP_PLATE_GDS_YARD, szYD_EQP_ID, Integer.parseInt(szYD_CRN_XAXIS), Integer.parseInt(szYD_CRN_YAXIS));
//              }
//          }

            ydUtils.putYdCrnPosMult(YdConstant.YD_MONITORING_CHANNEL_K, YdConstant.YD_GP_PLATE_GDS_YARD,msgRecord);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
szMsg = "후판제품야드L2 크레인현재위치 처리(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////


        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
//2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            throw new DAOException(szMsg);
        }
    }

//  /**
//   * 오퍼레이션명 : C열연코일야드L2 설비운전모드전환 (Y5YDL003)
//   *
//   * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//   * @param msgRecord
//   * @return
//   * @throws JDTOException
//   */
//  public void procY5EqpDrvMdTurnov(JDTORecord msgRecord) throws JDTOException {
//      // 레코드 선언
//      JDTORecord recPara         = null;
//      JDTORecordSet rsResult     = null;
//      JDTORecordSet rsGetYdCrnS  = null;
//      JDTORecord recGetVal       = null;
//        JDTORecord getparamRecord  = null;
//        JDTORecord setCrnschRecord = null;
//        JDTORecord SndRecord       = null;
//        JDTORecord recTempPara = null;
//
//        // DAO 객체 생성
//      YdCrnSchDao ydCrnSchDao    = new YdCrnSchDao();
//      YdDelegate ydDelegate      = new YdDelegate();
//      JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
//
//      // 변수 선언
//      String szMethodName        = "procY5EqpDrvMdTurnov";
//      String szMsg               = "";
//      String szOperationName     = "C열연코일야드L2 설비운전모드전환";
//      String szRcvTcCode         = null;
//      int nRet                   = 0;
//      int intRtnVal              = 0;
//      String szYD_EQP_ID         = "";
//      String sQueryId            = "";
//      String szMode1Temp         = "";
//
//      szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
//          // Log 테이블 등록
//          //=============================================================
//          szMsg = "[열연 코일야드L2] 설비운전모드전환 수신";
//          ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
//
//          rsResult     = JDTORecordFactory.getInstance().createRecordSet("");
//          getparamRecord  = JDTORecordFactory.getInstance().create();
//          setCrnschRecord = JDTORecordFactory.getInstance().create();
//          SndRecord = JDTORecordFactory.getInstance().create();
//
//          // 파라미터 Check
//          ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [1] 파라미터 검사", YdConstant.DEBUG);
//          nRet = this.paramY5YDL003Check(msgRecord, getparamRecord, 0);
//          if(nRet == -1) {
//                szMsg = "파라미터 Check중 Error   : " + nRet;
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                return ;
//          }
//
//          // 설비테이블에 야드설비작업Mode 업데이트
//          ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [2] 설비 테이블 업데이트 처리", YdConstant.DEBUG);
//          setCrnschRecord.setField("YD_EQP_ID"        , getparamRecord.getFieldString("YD_EQP_ID"));       // 설비ID
//
////            150918 hun 오토크레인 설비 모드 추가 update
//          if("1".equals(getparamRecord.getFieldString("YD_EQP_WRK_MODE")) ){
//              setCrnschRecord.setField("YD_EQP_WRK_MODE"  , getparamRecord.getFieldString("YD_EQP_WRK_MODE")); // 1: On-Line, "2": Off-Line
//              setCrnschRecord.setField("YD_EQP_AUTO_CRN_MODE"  , getparamRecord.getFieldString("YD_EQP_WRK_MODE")); // 1: 정상, 4: 일시정지, 5:비상정지
//          }else if("2".equals(getparamRecord.getFieldString("YD_EQP_WRK_MODE"))){
//              setCrnschRecord.setField("YD_EQP_WRK_MODE"  , getparamRecord.getFieldString("YD_EQP_WRK_MODE")); // 1: On-Line, "2": Off-Line
//          }else if("3".equals(getparamRecord.getFieldString("YD_EQP_WRK_MODE"))
//                  || "4".equals(getparamRecord.getFieldString("YD_EQP_WRK_MODE"))
//                  || "5".equals(getparamRecord.getFieldString("YD_EQP_WRK_MODE"))){
//              setCrnschRecord.setField("YD_EQP_AUTO_CRN_MODE"  , getparamRecord.getFieldString("YD_EQP_WRK_MODE")); // 1: 정상, 4: 일시정지, 5:비상정지
//          }
//          setCrnschRecord.setField("YD_EQP_WRK_MODE2"  , getparamRecord.getFieldString("YD_EQP_WRK_MODE2")); // 150626 hun A:무인, R:리모컨, E:정비, M:유인
//          nRet = this.Y5YDL003UpdYdEqp(setCrnschRecord, 0);
//          if(nRet == -1){
//                szMsg = "설비테이블 업데이트 중  Error : (" + getparamRecord.getFieldString("YD_EQP_ID") + ")(" + getparamRecord.getFieldString("YD_EQP_WRK_MODE") + ")" + nRet;
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                return ;
//          }
//
//
//
//
//
//          //==================================================================
//          // 2009.10.15
//          // 권오창
//          // 설비운전모드전환 전문 수신하여 설비 테이블에 업데이트 후
//          // 김진욱 리스케줄 호출을 직접 호출
//          //
//          // 파라미터 : 설비ID
//          // 열연코일 야드  => procY5CrnReSch()
//              //==================================================================
//          SndRecord.setField("YD_EQP_ID", getparamRecord.getFieldString("YD_EQP_ID"));
//          //코일 제품창고 크레인 리스케줄
//          CoilobjCrnResch.procY5CrnReSch(SndRecord);
//
//
//
//          //------------------------------------------------------------------
//          // 코일 복구 스케줄 설비정보변경시
//          //------------------------------------------------------------------
////            JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
////            recFlex.setField("YD_GP",  YdConstant.YD_GP_C_HR_COIL_MATL_YARD);
////            recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
////            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
////            ydUtils.putYdFlexCrnWrk("", recFlex);
//
//
//
//          /*
//           * 업무 : 크레인작업실적응답 전문 전송(YDY5L005)
//           */
//          recPara = JDTORecordFactory.getInstance().create();
//          recPara.setField("MSG_ID"        , "YDY5L005");
//          recPara.setField("YD_EQP_ID"     , getparamRecord.getFieldString("YD_EQP_ID"));
//          recPara.setField("YD_L2_WR_GP"   , "M");            //U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경
//          recPara.setField("YD_L3_HD_RS_CD", "0000");         //야드L3처리결과코드
//          ydDelegate.sendMsg(recPara);
//
//
//          //3. 무인설비->유인 전환시 CrnSch테이블 "S"상태값 "1" 업데이트 메소드 호출
//          if("M".equals(getparamRecord.getFieldString("YD_EQP_WRK_MODE2"))){
//
//              szYD_EQP_ID = getparamRecord.getFieldString("YD_EQP_ID");
//
//              // 설비ID 로 CrnSch상태가 S 인놈 check
//              rsGetYdCrnS = JDTORecordFactory.getInstance().createRecordSet("Temp");
//              sQueryId = "com.inisteel.cim.yd.dao.ydeqpdao.YdCrnSchDao.YdGetCrnSchSbyEqpId";
//              intRtnVal = ydCommDao.select(msgRecord, rsGetYdCrnS, sQueryId);
//
//              if(intRtnVal < 0 )
//              {
//                  szMsg="크레인 스케줄 S값 select Data없음";
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//              }else{
//
//                  rsGetYdCrnS.absolute(1);
//                  recTempPara = JDTORecordFactory.getInstance().create();
//                  // Temp Data inDto에 다시 세팅
//                  recTempPara.setRecord(rsGetYdCrnS.getRecord());
//                  recTempPara.setField("YD_WRK_PROG_STAT"   , recTempPara.getFieldString("PROG_STAT_TO"));   // 유인크레인 전환시 PROG_STAT=S값 있으면 안됨
//                  recTempPara.setField("MODIFIER" , "Y5YDL003" );
//                  recTempPara.setField("YD_WORD_DT",      YdUtils.getCurDate("yyyyMMddHHmmss"));
//
//                  /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnWrkProgStat*/
//                  nRet = this.Y5UpdCrnschMode(recTempPara);
//                  if(nRet == -1) {
//                      szMsg="크레인 스케줄 S값 1로 update 중 Error";
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                  }
//              }
//          }
//
//
//      }catch(JDTOException e) {
//            szMsg = "JDTOError : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//            return ;
//      }catch(Exception e) {
//            szMsg = "Error : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//            return ;
//      }
//  }
//


    /**
     * 오퍼레이션명 : C연주 크레인 리스케줄 작업예약 및 크레인스케줄 등록
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param rsCrnSch
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y5UpdCrnschMode(JDTORecord inRec)throws JDTOException  {
        YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
        JDTORecord    recTemp           = null;

        JDTORecordSet outRecSet         = null;
        int intRtnVal                   = 0 ;

        String szMsg                    = "";
        String szMethodName             = "Y5UpdCrnschMode";


        String szQuery                  = "";

        String szSchCd                  = "";
        String szSchPrior               = "";
        String szEqpId                  = "";

        try{
            //크레인 스케줄 Table 업데이트
            /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnWrkProgStat*/
            intRtnVal = ydCrnSchDao.updYdCrnschDelay(inRec, 302);


        }catch(Exception e){

            szMsg="유인 크레인 스케줄 S값 업데이트 Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return intRtnVal = -1;
        }


        szMsg="유인 크레인 스케줄 S값 업데이트("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return intRtnVal = 1;
    } //end of Y5UpdWbookCrnsch()


//    /**
//     * 오퍼레이션명 : C열연 설비운전모드전환 파라미터 체크 [권오창 2009.09.09]
//     *
//     * @param  ● msgRecord, outRecord, intGp
//     * @return ● nRtnVal
//     * @throws ● JDTOException
//     */
//  public int paramY5YDL003Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
//      // 레코드 선언
//      JDTORecord setRecord = null;
//
//      // 변수 선언
//        String szMethodName  = "paramY5YDL003Check";
//      String szMsg         = "" ;
//
//
//        try{
//          setRecord = JDTORecordFactory.getInstance().create();
//
//          // 레코드 값 체크
//          setRecord.setField("YD_EQP_ID"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"));
//          setRecord.setField("YD_EQP_WRK_MODE", ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE"));
//          setRecord.setField("YD_EQP_WRK_MODE2", ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE2"));  // 150626 hun 크레인자동화
//
//          // 레퍼런스 레코드인자에 설정
//          outRecord.setRecord(setRecord);
//        }catch(Exception e){
//            szMsg = "Error : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//          return -1;
//        }
//
//      return 1;
//  }
//


//    /**
//     * 오퍼레이션명 : C열연 야드 설비테이블 야드설비작업Mode 업데이트 [권오창 2009.09.09]
//     *
//     * @param  ● msgRecord, intGp
//     * @return ● nRtnVal
//     * @throws ● JDTOException
//     */
//    public int Y5YDL003UpdYdEqp(JDTORecord msgRecord, int intGp) throws JDTOException {
//      // DAO객체 생성
//      YdEqpDao ydEqpDao   = new YdEqpDao();
//
//      // 변수 선언
//      String szMethodName = "Y5YDL003UpdYdEqp";
//      String szMsg        = "";
//      int nRet            = 0;
//
//      try{
//          nRet = ydEqpDao.updYdEqp(msgRecord, intGp);
//
//          switch(nRet){
//              case 0 :
//                  szMsg = "No Data Found!!!, ErrorCode:" + nRet;
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                  return nRet = -1;
//              case -1 :
//                  szMsg = "Dup_val_on_index!!!, ErrorCode:" + nRet;
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                  return nRet = -1;
//              case -2 :
//                  szMsg = "Parameter Error!!!, ErrorCode:" + nRet;
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                  return nRet = -1;
//              case -3 :
//                  szMsg = "Execution Failed!!!, ErrorCode:" + nRet;
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                  return nRet = -1;
//          }
//      }catch(Exception e){
//            szMsg = "Error : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//            return nRet = -1;
//        }
//      return nRet;
//    }





//  /**
//   * 오퍼레이션명 : C열연코일야드L2 설비고장복구실적 (Y5YDL004) [권오창 2009.09.09]
//   *
//   * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//   * @param msgRecord
//   * @return
//   * @throws JDTOException
//   */
//  public void procY5EqpTrblRcvrWr(JDTORecord msgRecord) throws JDTOException {
//      JDTORecord    recPara          = null;
//      JDTORecordSet rsResult         = null;
//      JDTORecord    recGetVal        = null;
//        JDTORecord getparamRecord      = null;
//        JDTORecord setCrnschRecord     = null;
//        JDTORecord SndRecord           = null;
//
//        // DAO 객체 생성
//      YdCrnSchDao ydCrnSchDao        = new YdCrnSchDao();
//      YdDelegate ydDelegate          = new YdDelegate();
//      YdEqpDao     ydEqpDao     = new YdEqpDao();
//
//      // 변수 선언
//      String szMethodName            = "procY5EqpTrblRcvrWr";
//      String szMsg                   = "";
//      String szOperationName         = "C열연코일야드L2 설비고장복구실적";
//      String szRcvTcCode             = null;
//      String szYD_EQP_ID             = "";
//      String szYD_EQP_STAT           = "";
//      String szYD_EQP_PAUSE_CODE     = "";
//      String szYD_EQP_TRBL_RCVR_DT   = "";
//      String szYD_EQP_STAT_UPD       = "";
//      String szYD_EQP_CURR_STAT      = "";
//      String szYD_EQP_PAUSE_OCCR_SEQ = "";
//      String szCrnReturnStat = "";
//      int nRet                       = 0;
//
//      ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
//      ydUtils.displayRecord(szOperationName, msgRecord);
//      ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
//
//      szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
//          szMsg = "[열연 코일야드L2] 설비고장복구실적 수신";
//          ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
//
//
//
//
//
//          rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//          getparamRecord  = JDTORecordFactory.getInstance().create();
//          setCrnschRecord = JDTORecordFactory.getInstance().create();
//          SndRecord = JDTORecordFactory.getInstance().create();
//
//          // 파라미터 Check
//          ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [1] 파라미터 검사", YdConstant.DEBUG);
//          nRet = this.paramY5YDL004Check(msgRecord, getparamRecord, 0);
//          if(nRet == -1) {
//                szMsg = "파라미터 Check중 Error   : " + nRet;
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                return ;
//          }
//
//
//
//
//
//          szYD_EQP_ID = getparamRecord.getFieldString("YD_EQP_ID");
//          if(szYD_EQP_ID.equals("")){
//              szMsg = "설비ID가 존재하지 않습니다.";
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//              return ;
//          }
//
//          szYD_EQP_STAT = getparamRecord.getFieldString("YD_EQP_STAT");
//          if(szYD_EQP_STAT.equals("")){
//              szMsg = "설비상태가  존재하지 않습니다.";
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//              return ;
//          }
//
//          szYD_EQP_PAUSE_CODE = getparamRecord.getFieldString("YD_EQP_PAUSE_CODE");
//          if(szYD_EQP_PAUSE_CODE.equals("")){
//              szMsg = "휴지코드가  존재하지 않습니다.";
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//              return ;
//          }
//
//          szYD_EQP_TRBL_RCVR_DT = getparamRecord.getFieldString("YD_EQP_TRBL_RCVR_DT");
//          if(szYD_EQP_TRBL_RCVR_DT.equals("")){
//              szMsg = "야드설비고장복구일시가  존재하지 않습니다.";
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//              return ;
//          }
//
//
//
//
//
//          //============================================================================
//          // 변환...
//          //============================================================================
//          if(szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)){
//              // 고장
//              szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_BREAK;
//              if(szYD_EQP_PAUSE_CODE.equals("0000") || szYD_EQP_PAUSE_CODE.equals("")){
//                  szYD_EQP_PAUSE_CODE = "B000";
//              }else{
//                  szYD_EQP_PAUSE_CODE   = getparamRecord.getFieldString("YD_EQP_PAUSE_CODE");
//              }
//          }else if(szYD_EQP_STAT.equals("R") || szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_NORM)){
//              // 복구
//              szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_NORM;
//              if(szYD_EQP_PAUSE_CODE.equals("0000") || szYD_EQP_PAUSE_CODE.equals("")){
//                  szYD_EQP_PAUSE_CODE = "R000";
//              }else{
//                  szYD_EQP_PAUSE_CODE   = getparamRecord.getFieldString("YD_EQP_PAUSE_CODE");
//              }
//          }
//
//
//
//
//
//          //============================================================================
//          // 권오창
//          // 2009.11.06
//          // 휴지테이블 업데이트
//          //
//          // 고장이나 복구일 때 남김
//          //============================================================================
//          if(szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_BREAK) || szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_NORM)){
//              ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [2] 휴지 테이블 업데이트 처리", YdConstant.DEBUG);
//              this.ProcEqpPause(szRcvTcCode, szYD_EQP_ID, szYD_EQP_STAT_UPD, szYD_EQP_PAUSE_CODE, szYD_EQP_TRBL_RCVR_DT);
//          }
//
//
//          //============================================================================
//          // 이현성
//          // 2010.01.14
//          //
//          // 크레인 설비인 경우 설비 작업 상태 및 스케줄 변경
//          //============================================================================
//
//          String lzRtnMsg = null;
//
//          if (szYD_EQP_ID.substring(2,4).equals(YdConstant.YD_EQP_GP_CRANE)){
//
//              // 고장 UPDATE 시
//              if(szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_BREAK)){
//                  // 해당 설비 스케줄이 권상지시(YD_EQP_STAT_UP_WO) 일경우 IDLE 상태로 변경 YD_EQP_STAT_IDLE
////                    if(ydEqpDao.chkAutoCrn(szYD_EQP_ID )){
////                        // 자동 크레인일 경우 고장 수신시 스케쥴 변경 안함...
////                        ydUtils.putLog(szSessionName, szMethodName, szMethodName + ":: Auto크레인 경우 고장 수신시 스케쥴 변경X pass !!", YdConstant.DEBUG);
////                    }else{
////                        lzRtnMsg = this.updCrnWrkProgStatUpWoToIdle(szYD_EQP_ID);
////
////                        if(lzRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
////                            szMsg = "스케줄 변경 성공 하였습니다.";
////                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
////                        }else if (lzRtnMsg.equals(YdConstant.RETN_CD_FAILURE)){
////                            szMsg = "스케줄 변경 실패 하였습니다.";
////                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
////                        }
////                    }
//
//                  szMsg = "크레인 스케줄을 3자가 같이 가지고 있는다.";
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//              }
//
//              // 정상 UPDATE 시
//              else{
////                    150827 hun 설비 정상 update 방법 변경 => AsIs : W, ToBe : 현재크레인스케쥴 상태
//                  szYD_EQP_STAT_UPD = this.updEqpWrkStatUpToNow(szYD_EQP_ID);
//
//              }
//          }
//
//
//          //============================================================================
//          // 권오창
//          //
//          //
//          // 설비테이블에 야드설비상태 업데이트
//          //============================================================================
//          ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [3] 설비 테이블 업데이트 처리", YdConstant.DEBUG);
//          setCrnschRecord.setField("YD_EQP_ID"  , szYD_EQP_ID);       // 설비ID
//          setCrnschRecord.setField("YD_EQP_STAT", szYD_EQP_STAT_UPD); // "B": 고장, "R": 복구
//          setCrnschRecord.setField("MODIFIER"   , szRcvTcCode);
//          nRet = this.Y5YDL004UpdYdEqp(setCrnschRecord, 0);
//          if(nRet == -1){
//                szMsg = "설비테이블 업데이트 중  Error : (" + getparamRecord.getFieldString("YD_EQP_ID") + ")(" + getparamRecord.getFieldString("YD_EQP_STAT") + ") " + nRet;
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                return ;
//          }
//
//
//
//
//          //------------------------------------------------------------------
//          //C열연코일야드  설비정보변경시
//          //------------------------------------------------------------------
//          JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
//          recFlex.setField("YD_GP",  YdConstant.YD_GP_C_HR_COIL_MATL_YARD);
//          recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//          ydUtils.putYdFlexCrnWrk("", recFlex);
//
//
//
//
//          //==================================================================
//          // 2009.10.15
//          // 권오창
//          // 설비고장복구실적 전문 수신하여 설비 테이블에 업데이트 후
//          // 김진욱 리스케줄 호출을 직접 호출
//          //
//          // 파라미터 : 설비ID
//          // 열연코일 => procY5CrnReSch()
//              //==================================================================
//          SndRecord.setField("YD_EQP_ID", getparamRecord.getFieldString("YD_EQP_ID"));
////sjhkim
////SJH 일단 막음
////            CoilobjCrnResch.procY5CrnReSch(SndRecord);
//
//
//
//
//
//
//
//
//          /*
//           * 업무 : 크레인작업실적응답 전문 전송(YDY5L005)
//           */
//          ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [3] 전문 구성 후 델리게이트 호출", YdConstant.DEBUG);
//          recPara = JDTORecordFactory.getInstance().create();
//          recPara.setField("MSG_ID"        , "YDY5L005");
//          recPara.setField("YD_EQP_ID"     , getparamRecord.getFieldString("YD_EQP_ID"));
//          recPara.setField("YD_L2_WR_GP"   , szYD_EQP_STAT);                  //U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경
//          recPara.setField("YD_L3_HD_RS_CD", "0000");                 //야드L3처리결과코드
//          ydDelegate.sendMsg(recPara);
//      }catch(JDTOException e) {
//            szMsg = "JDTOError : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//            return ;
//      }catch(Exception e) {
//            szMsg = "Error : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//            return ;
//      }
//  }
//




//    /**
//     * 오퍼레이션명 : C열연 설비고장복구실적 파라미터 체크 [권오창 2009.09.09]
//     *
//     * @param  ● msgRecord, outRecord, intGp
//     * @return ● nRtnVal
//     * @throws ● JDTOException
//     */
//  public int paramY5YDL004Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
//      // 레코드 선언
//      JDTORecord setRecord = null;
//
//      // 변수 선언
//        String szMethodName  = "paramY5YDL004Check";
//      String szMsg         = "" ;
//
//        try{
//          setRecord = JDTORecordFactory.getInstance().create();
//
//          // 레코드 값 체크
//          setRecord.setField("YD_EQP_ID"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"));
//          setRecord.setField("YD_EQP_STAT", ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_STAT"));
//          setRecord.setField("YD_EQP_PAUSE_CODE", ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE"));
//          setRecord.setField("YD_EQP_TRBL_RCVR_DT", ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_TRBL_RCVR_DT"));
//
//          // 레퍼런스 레코드인자에 설정
//          outRecord.setRecord(setRecord);
//
//          //======================================================================================================
//          // LOG 출력 - 그냥 테스트용으로 출력 레코드값체크하는 코드는 제외하고 나중에 삭제
//          szMsg = "[1] 야드설비ID : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_EQP_ID");
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//          szMsg = "[2] 야드설비상태 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_EQP_STAT");
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//          szMsg = "[3] 야드설비휴지코드 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_EQP_PAUSE_CODE");
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//          szMsg = "[4] 야드설비고장복구일시 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_EQP_TRBL_RCVR_DT");
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//          //======================================================================================================
//
//        }catch(Exception e){
//            szMsg = "Error : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//          return -1;
//        }
//
//      return 1;
//  }





//    /**
//     * 오퍼레이션명 : C열연 야드 설비테이블 설비상태 업데이트 [권오창 2009.09.09]
//     *
//     * @param  ● msgRecord, intGp
//     * @return ● nRtnVal
//     * @throws ● JDTOException
//     */
//    public int Y5YDL004UpdYdEqp(JDTORecord msgRecord, int intGp) throws JDTOException {
//      // DAO객체 생성
//      YdEqpDao ydEqpDao   = new YdEqpDao();
//
//      // 변수 선언
//      String szMethodName = "Y5YDL004UpdYdEqp";
//      String szMsg        = "";
//      int nRet            = 0;
//
//      try{
//          nRet = ydEqpDao.updYdEqp(msgRecord, intGp);
//
//          switch(nRet){
//              case 0 :
//                  szMsg = "No Data Found!!!, ErrorCode:" + nRet;
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                  return nRet = -1;
//              case -1 :
//                  szMsg = "Dup_val_on_index!!!, ErrorCode:" + nRet;
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                  return nRet = -1;
//              case -2 :
//                  szMsg = "Parameter Error!!!, ErrorCode:" + nRet;
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                  return nRet = -1;
//              case -3 :
//                  szMsg = "Execution Failed!!!, ErrorCode:" + nRet;
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                  return nRet = -1;
//          }
//      }catch(Exception e){
//            szMsg = "Error : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//            return nRet = -1;
//        }
//      return nRet;
//    }





//  /**
//   * 오퍼레이션명 : C열연코일야드L2 크레인현재위치 (Y5YDL005) [권오창 2009.09.09]
//   *
//   * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//   * @param msgRecord
//   * @return
//   * @throws JDTOException
//   */
//  public void procY5CrnCurrLoc(JDTORecord msgRecord) throws JDTOException {
//      // 레코드 선언
//        JDTORecord getparamRecord  = null;
//        JDTORecord setCrnschRecord = null;
//
//        // 변수 선언
//      String szMethodName        = "procY5CrnCurrLoc";
//      String szMsg               = "";
//      String szOperationName     = "C열연코일야드L2 크레인현재위치";
//      String szRcvTcCode         = null;
//      String szYD_EQP_ID         = "";
//      String szYD_CRN_XAXIS      = "";
//      String szYD_CRN_YAXIS      = "";
//      int nRet                   = 0;
//
//      ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
//      ydUtils.displayRecord(szOperationName, msgRecord);
//      ydUtils.putLog(szSessionName, szMethodName, "\n*+*+*+*+*+*+*+*+*+*+*+*+*\n", YdConstant.DEBUG);
//
//      szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
//        try{
//          //=============================================================
//          // 권오창
//          // 2009.11.05
//          //
//          // Log 테이블 등록
//          //=============================================================
//
//
//
//
//
//            getparamRecord  = JDTORecordFactory.getInstance().create();
//            setCrnschRecord = JDTORecordFactory.getInstance().create();
//
//          // 파라미터 Check
//          ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [1] 파라미터 검사", YdConstant.DEBUG);
//          nRet = this.paramY5YDL005Check(msgRecord, getparamRecord, 0);
//          if(nRet == -1) {
//                szMsg = "파라미터 Check중 Error   : " + nRet;
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                return ;
//          }
//
//
//
//
//
//          //=================================================================
//          // 2010.01.05
//          // 권오창
//          //
//          // 수신받은 크레인 위치정보 화면으로 호출
//          //=================================================================
//          ydUtils.putLog(szSessionName, szMethodName, "Y5크레인현재위치 화면처리 위한 호출", YdConstant.DEBUG);
////            for(int i=0; i<20; i++){
////                szYD_EQP_ID    = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID" + (i+1));
////                szYD_CRN_XAXIS = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS" + (i+1));
////                szYD_CRN_YAXIS = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS" + (i+1));
////
////                // 존재하는 설비ID에 대한 위치정보 Flex화면에서 보여지기 위한 호출
////                if(!szYD_EQP_ID.trim().equals("")){
////                    //ydUtils.putYdFlexCrnPos(YdConstant.YD_MONITORING_CHANNEL_H, YdConstant.YD_GP_C_HR_COIL_MATL_YARD, szYD_EQP_ID, Integer.parseInt(szYD_CRN_XAXIS), Integer.parseInt(szYD_CRN_YAXIS));
////                    //ydUtils.putYdFlexCrnPos(YdConstant.YD_MONITORING_CHANNEL_J, YdConstant.YD_GP_C_HR_COIL_GDS_YARD, szYD_EQP_ID, Integer.parseInt(szYD_CRN_XAXIS), Integer.parseInt(szYD_CRN_YAXIS));
////                }
////            }
//
//          ydUtils.putYdCrnPosMult(YdConstant.YD_MONITORING_CHANNEL_H, YdConstant.YD_GP_C_HR_COIL_MATL_YARD,msgRecord);
//          ydUtils.putYdCrnPosMult(YdConstant.YD_MONITORING_CHANNEL_J, YdConstant.YD_GP_C_HR_COIL_GDS_YARD, msgRecord);
//
//        }catch(Exception e){
//            szMsg = "Error : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//          return ;
//        }
//  }
//




//    /**
//     * 오퍼레이션명 : Y5크레인현재위치 파라미터 체크 [권오창 2009.09.09]
//     *
//     * @param  ● msgRecord, outRecord, intGp
//     * @return ● nRtnVal
//     * @throws ● JDTOException
//     */
//  public int paramY5YDL005Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
//      // 레코드 선언
//      JDTORecord setRecord = null;
//
//      // 변수 선언
//        String szMethodName  = "paramY5YDL005Check";
//      String szMsg         = "" ;
//
//        try{
//          setRecord = JDTORecordFactory.getInstance().create();
//
//          // 레코드 값 체크
//          setRecord.setField("YD_EQP_QNTY"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_QNTY"));
//
//          for(int i=0; i<20; i++){
//              setRecord.setField("YD_EQP_ID" + (i+1), ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID" + (i+1)));
//              setRecord.setField("YD_CRN_XAXIS" + (i+1), ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS" + (i+1)));
//              setRecord.setField("YD_CRN_YAXIS" + (i+1), ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS" + (i+1)));
//          }
//
//          // 레퍼런스 레코드인자에 설정
//          outRecord.setRecord(setRecord);
//        }catch(Exception e){
//            szMsg = "Error : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//          return -1;
//        }
//
//      return 1;
//  }

    /**
     * 오퍼레이션명 : C연주정정L2 수불구용도변경요구 (C3YDL001, C7YDL001)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procC3TkovlocUsgMod(JDTORecord msgRecord) throws DAOException {
        // 레코드 선언
        JDTORecord recPara         = null;
        JDTORecord setCrnschRecord = null;

        // DAO객체 생성
        YdStkBedDao ydStkBedDao    = new YdStkBedDao();
        YdDelegate ydDelegate      = new YdDelegate();

        // 변수 선언
        String szMethodName        = "procC3TkovlocUsgMod";
        String szMsg               = "";
        String szOperationName     = "C연주정정L2 수불구용도변경요구";
        String szRcvTcCode         = null;
        String szTemp              = "";
        String msgID               = "";
        int nRet                   = 0;

        szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
            // Log 테이블 등록
            //=============================================================
            szMsg = "[C연주정정L2] 수불구용도변경요구 수신";
            ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            // BED테이블 업데이트
            for(int i=0; i < 7; i++){

                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("YD_STK_COL_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"), 6, 1));
                recPara.setField("YD_STK_BED_NO", YdUtils.fillSpZr(Integer.toString(i+1), 2, 0));
                recPara.setField("YD_STK_BED_USG_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_USG_GP" + (i+1)), 1, 1));

                nRet = ydStkBedDao.updYdStkbed(recPara, 0);
            }

            // 델리게이트 호출을 위한 레코드 편집
            ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [3] 전문 구성 후 델리게이트 호출 ", YdConstant.DEBUG);

            msgID = ydDaoUtils.paraRecChkNull(msgRecord, "MSG_ID");
            if("C7YDL001".equals(msgID)) {
                msgID = "YDC7L001";
            } else {
                msgID = "YDC3L001";
            }

            szTemp = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"), 6, 1).trim();

            setCrnschRecord = JDTORecordFactory.getInstance().create();
            setCrnschRecord.setField("MSG_ID"       , msgID);                                                            // TC-CODE
            setCrnschRecord.setField("YD_GP"        , (szTemp != "" && szTemp.length() == 6) ? szTemp.substring(0, 1) : "");  // 야드설비구분
            setCrnschRecord.setField("YD_STK_COL_GP", szTemp);                                                                // 야드적치열구분

            // 델리게이트 호출
            ydDelegate.sendMsg(setCrnschRecord);

        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new DAOException(szMsg);
        }
    }

    /**
     * 오퍼레이션명 : C연주정정L2 수불구재료적치정보 (C3YDL002, C7YDL002)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procC3MatlStkInfo(JDTORecord msgRecord) throws DAOException {

        // 변수 선언
        String szMethodName        = "procC3MatlStkInfo";
        String szMsg               = "";
        String szOperationName     = "C연주정정L2 수불구재료적치정보";
        String szRcvTcCode         = null;
        String szSTL_NO            = "";
        int nSTLSH                 = 0;
        int nRet                   = 0;

        szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
            // Log 테이블 등록
            //=============================================================
            szMsg = "[C연주정정L2] 수불구재료적치정보 수신";
            ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            nSTLSH = Integer.parseInt(msgRecord.getFieldString("YD_STK_BED_STL_SH"));

            //=================================================================
            // 매수만큼 돌지 말고 전문에 MAX만큼 돌면서 없는 재료에 대해서는 클리어 처리
            //=================================================================
            for(int i=0; i < 5; i++){
                JDTORecord setUpdRecord = JDTORecordFactory.getInstance().create();

                // 재료번호를 추출해 온다.
                szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO" + (i+1));

                // 재료번호가 존재하면 적치 중인 상태로 재료번호 설정, 재료번호가 존재하지 않을 시에는 적치단을 클리어시켜줌
                if(szSTL_NO.trim().equals("")){
                    setUpdRecord.setField("STL_NO"             , "");            // 재료번호
                    setUpdRecord.setField("YD_STK_LYR_MTL_STAT", "E");           // 재료 적치 상태
                }else{
                    setUpdRecord.setField("STL_NO"             , szSTL_NO);      // 재료번호
                    setUpdRecord.setField("YD_STK_LYR_MTL_STAT", "C");           // 재료 적치 상태
                }

                setUpdRecord.setField("YD_STK_COL_GP"      , msgRecord.getFieldString("YD_EQP_ID"));                               // 설비ID
                setUpdRecord.setField("YD_STK_BED_NO"      , YdUtils.fillSpZr(msgRecord.getFieldString("YD_STK_BED_NO"), 2, 0));  // 적치BED번호
                setUpdRecord.setField("YD_STK_LYR_NO"      , YdUtils.fillSpZr("" + (i+1), 3, 0));                                      // 적치단 번호

                nRet = this.C3YDL002UpdYdEqp(setUpdRecord, 0);
                if(nRet == -1){
                    szMsg = "적치단 테이블(" + i + ") 업데이트 중  Error : " + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return ;           // 한개라도 업데이트 실패하면... 실패
                }
            }

        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new DAOException(szMsg);
        }
    }

    /**
     * 오퍼레이션명 : C3수불구용도변경요구 적치단 테이블 업데이트
     *
     * @param  ● msgRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int C3YDL002UpdYdEqp(JDTORecord msgRecord, int intGp) throws JDTOException {
        // DAO객체 생성
        YdStkLyrDao ydStkLyrDao   = new YdStkLyrDao();

        // 변수 선언
        String szMethodName = "C3YDL002UpdYdEqp";
        String szMsg        = "";
        int nRet            = 0;

        try{
            nRet = ydStkLyrDao.updYdStklyr(msgRecord, intGp);

            switch(nRet){
                case 0 :
                    szMsg = "No Data Found!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -1 :
                    szMsg = "Dup_val_on_index!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -2 :
                    szMsg = "Parameter Error!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -3 :
                    szMsg = "Execution Failed!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
            }
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return nRet = -1;
        }
        return nRet;
    }

    /**
     * 오퍼레이션명 : C연주정정L2 설비고장복구실적 (C3YDL008, C7YDL008)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procC3EqpTrblRcvrWr(JDTORecord msgRecord) throws JDTOException {

        // 레코드 선언
        JDTORecord setCrnschRecord   = null;
        JDTORecord SndRecord         = null;

        // 변수 선언
        String szMethodName          = "procC3EqpTrblRcvrWr";
        String szMsg                 = "";
        String szOperationName       = "C연주정정L2 설비고장복구실적";
        String szRcvTcCode           = null;
        String szYD_EQP_ID           = "";
        String szYD_EQP_STAT         = "";
        String szYD_EQP_PAUSE_CODE   = "";
        String szYD_EQP_TRBL_RCVR_DT = "";
        String szYD_EQP_STAT_UPD     = "";
        String szYD_EQP_CURR_STAT    = "";
        String szYD_EQP_PAUSE_OCCR_SEQ = "";
        int nRet                   = 0;

        szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
            // Log 테이블 등록
            //=============================================================
            szMsg = "[C연주정정L2] 설비고장복구실적 수신";
            ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            setCrnschRecord = JDTORecordFactory.getInstance().create();
            SndRecord = JDTORecordFactory.getInstance().create();

            szYD_EQP_ID           = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            szYD_EQP_STAT         = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_STAT");
            szYD_EQP_PAUSE_CODE   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
            szYD_EQP_TRBL_RCVR_DT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_TRBL_RCVR_DT");

            //============================================================================
            // 변환...
            //============================================================================
            if(szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)){
                // 고장
                szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_BREAK;
                if(szYD_EQP_PAUSE_CODE.equals("0000") || szYD_EQP_PAUSE_CODE.equals("")){
                    szYD_EQP_PAUSE_CODE = "B000";
                }else{
                    szYD_EQP_PAUSE_CODE   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
                }
            }else if(szYD_EQP_STAT.equals("R") || szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_NORM)){
                // 복구
                szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_NORM;
                if(szYD_EQP_PAUSE_CODE.equals("0000") || szYD_EQP_PAUSE_CODE.equals("")){
                    szYD_EQP_PAUSE_CODE = "R000";
                }else{
                    szYD_EQP_PAUSE_CODE   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
                }
            }

            //============================================================================
            // 휴지테이블 업데이트
            //
            // 고장이나 복구일 때 남김
            //============================================================================
            if(szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_BREAK) || szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_NORM)){
                ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [2] 휴지테이블  업데이트 처리", YdConstant.DEBUG);
                this.ProcEqpPause(szRcvTcCode, szYD_EQP_ID, szYD_EQP_STAT_UPD, szYD_EQP_PAUSE_CODE, szYD_EQP_TRBL_RCVR_DT);
            }

            //============================================================================
            // 설비테이블에 야드설비상태 업데이트
            //============================================================================
            ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [3] 설비 테이블에 업데이트 처리", YdConstant.DEBUG);
            setCrnschRecord.setField("YD_EQP_ID"  , szYD_EQP_ID);       // 설비ID
            setCrnschRecord.setField("YD_EQP_STAT", szYD_EQP_STAT_UPD); // 고장(B)=>"B", 복구(R)=>"N"
            setCrnschRecord.setField("MODIFIER"   , szRcvTcCode);
            nRet = this.C3YDL008UpdYdEqp(setCrnschRecord, 0);
            if(nRet == -1){
                szMsg = "설비테이블 업데이트 중  Error : " + nRet;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            //==================================================================
            // 설비고장복구실적 전문 수신하여 설비 테이블에 업데이트 후
            // 김진욱 리스케줄 호출을 직접 호출
            //
            // 파라미터 : 설비ID
            // 연주 정정C3 => procY1CrnReSch()
            //==================================================================
            //SndRecord.setField("YD_EQP_ID", msgRecord.getFieldString("YD_EQP_ID"));
            //objCrnResch.procY1CrnReSch(SndRecord);

        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }
    }

    /**
     * 오퍼레이션명 : 설비고장복구실적 휴지테이블 처리
     *
     * @param  ● JDTORecord(MSG_ID, YD_EQP_ID, YD_EQP_STAT, YD_EQP_PAUSE_CODE, YD_EQP_TRBL_RCVR_DT)
     * @return ● void
     * @throws ● JDTOException
     */
    public void ProcEqpPause(JDTORecord inRec) throws JDTOException {
        // 변수 선언
        String szMethodName          = "ProcEqpPause";
        String szMsg                 = "";

        String szTcCode              = "";
        String szYD_EQP_ID           = "";
        String szYD_EQP_STAT_UPD     = "";
        String szYD_EQP_PAUSE_CODE   = "";
        String szYD_EQP_TRBL_RCVR_DT = "";


        try {
            // 레코드 항목 검사
            szTcCode = ydDaoUtils.paraRecChkNull(inRec, "MSG_ID");
            if(szTcCode.equals("")){
                szMsg = "TC CODE가 존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }


            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
            if(szYD_EQP_ID.equals("")){
                szMsg = "설비ID가 존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }


            szYD_EQP_STAT_UPD = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_STAT");
            if(szYD_EQP_STAT_UPD.equals("")){
                szMsg = "설비상태가 존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }


            szYD_EQP_PAUSE_CODE = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_PAUSE_CODE");
            if(szYD_EQP_PAUSE_CODE.equals("")){
                szMsg = "설비휴지코드가 존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }


            szYD_EQP_TRBL_RCVR_DT = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_TRBL_RCVR_DT");
            if(szYD_EQP_TRBL_RCVR_DT.equals("")){
                szMsg = "휴지일시가 존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            // 설비고장복구 실적 처리 호출
            this.ProcEqpPause(szTcCode, szYD_EQP_ID, szYD_EQP_STAT_UPD, szYD_EQP_PAUSE_CODE, szYD_EQP_TRBL_RCVR_DT);

        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

    }

    /**
     * 오퍼레이션명 : 설비고장복구실적 휴지테이블 처리 [권오창 2009.11.04]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● TC코드, 설비ID, 수신된 전문의 고장(B)/복구(N), 휴지코드, 발생일시
     * @return ● void
     * @throws ● JDTOException
     */
    public void ProcEqpPause(String strRcvTcCode,
                             String szYD_EQP_ID,
                             String szYD_EQP_STAT_UPD,
                             String szYD_EQP_PAUSE_CODE,
                             String szYD_EQP_TRBL_RCVR_DT) throws JDTOException {

        // DAO 및 UTIL 객체 생성
        YdEqpDao ydEqpDao               = new YdEqpDao();
        YdEqpPauseDao ydEqpPauseDao     = new YdEqpPauseDao();

        // 레코드 선언
        JDTORecord recPara              = null;
        JDTORecord setCrnschRecord      = null;
        JDTORecord recGetVal            = null;
        JDTORecordSet rsResult          = null;

        // 변수 선언
        String szMethodName             = "ProcEqpPause";
        String szMsg                    = "";
        String szYD_EQP_CURR_STAT       = "";
        String szYD_EQP_PAUSE_OCCR_SEQ  = "";
        String szYD_EQP_PAUSE_OCC_DT    = "";
        String szYD_EQP_PAUSE_RCVR_CNTS = "";
        int nRet                        = 0;


        try {
            // 데이터 항목 점검
            if(strRcvTcCode.equals("")){
                szMsg = "TC CODE가 존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            if(szYD_EQP_ID.equals("")){
                szMsg = "설비ID가 존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            if(szYD_EQP_STAT_UPD.equals("")){
                szMsg = "설비상태가 존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            if(szYD_EQP_PAUSE_CODE.equals("")){
                szMsg = "설비휴지코드가 존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            if(szYD_EQP_TRBL_RCVR_DT.equals("")){
                szMsg = "휴지일시가 존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            recPara         = JDTORecordFactory.getInstance().create();
            setCrnschRecord = JDTORecordFactory.getInstance().create();
            rsResult        = JDTORecordFactory.getInstance().createRecordSet("");

            // 설비테이블과 휴지테이블에서 해당 설비ID를 가지고 MAX차수의 상태값을 가져온다.
            // 읽어온 현재 설비의 상태값이 고장일 경우 수신받은 상태가 복구이면 휴지테이블에 MAX차수에 UPDATE 를 하고
            // 수신받은 상태가 고장이면 PASS
            // 읽어온 현재 설비의 상태값이 복구일 경우 수신받은 상태가 고장이면 휴지테이블에 MAX차수+1에 INSERT 를 하고
            // 수신받은 상태가 복구이면 PASS

            //=========================================================================================
            // 설비ID로 현재 설비의 상태와 휴지테이블에서 MAX차수의 값을 추출 [1건]  (GP : 12)
            // com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getEqpStatofMAX
            //
            // JSPEED 파라미터 : V_YD_EQP_ID
            //=========================================================================================
            recPara.setField("YD_EQP_ID", szYD_EQP_ID);
            nRet = ydEqpDao.getYdEqp(recPara, rsResult, 12);
            if(nRet < 0){
                szMsg = "설비 휴지 테이블 조회 오류 [" + nRet + "] YD_EQP_ID(" + szYD_EQP_ID + ")";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            } else if(nRet == 0){
                // 1차
                szYD_EQP_PAUSE_OCCR_SEQ = ydUtils.IncreaseStrToInt("0", 1, 18);

                // 레코드 편성
                setCrnschRecord = JDTORecordFactory.getInstance().create();
                setCrnschRecord.setField("REGISTER"             , strRcvTcCode);             // 등록자
                setCrnschRecord.setField("MODIFIER"             , strRcvTcCode);             // 수정자
                setCrnschRecord.setField("DEL_YN"               , "N");                      // 삭제유무
                setCrnschRecord.setField("YD_EQP_ID"            , szYD_EQP_ID);              // 설비ID
                setCrnschRecord.setField("YD_EQP_PAUSE_OCCR_SEQ", szYD_EQP_PAUSE_OCCR_SEQ);  // 1차
                setCrnschRecord.setField("YD_EQP_PAUSE_CODE"    , szYD_EQP_PAUSE_CODE);      // 설비휴지코드
                setCrnschRecord.setField("YD_EQP_PAUSE_OCC_DT"  , szYD_EQP_TRBL_RCVR_DT);    // 야드설비휴지발생일시

                // 설비휴지테이블 ISNERT
                nRet = ydEqpPauseDao.insYdEqppause(setCrnschRecord);
                if(nRet < 0){
                    szMsg = "설비 휴지테이블 INSERT 중  Error : " + nRet + " : YD_EQP_ID(" + szYD_EQP_ID + ") YD_EQP_PAUSE_OCCR_SEQ(" + szYD_EQP_PAUSE_OCCR_SEQ + ") YD_EQP_PAUSE_CODE(" + szYD_EQP_PAUSE_CODE + ") YD_EQP_TRBL_RCVR_DT(" + szYD_EQP_TRBL_RCVR_DT + ")";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return ;
                }

                szMsg = "설비 휴지테이블 INSERT 성공 : YD_EQP_ID(" + szYD_EQP_ID + ") YD_EQP_PAUSE_OCCR_SEQ(" + szYD_EQP_PAUSE_OCCR_SEQ + ") YD_EQP_PAUSE_CODE(" + szYD_EQP_PAUSE_CODE + ") YD_EQP_TRBL_RCVR_DT(" + szYD_EQP_TRBL_RCVR_DT + ")";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            } else {
                rsResult.first();
                recGetVal = rsResult.getRecord();

                // DB조회를 한 현재 설비의 상태와 차수를 가져온다
                szYD_EQP_CURR_STAT      = ydDaoUtils.paraRecChkNull(recGetVal, "YD_EQP_STAT");
                szYD_EQP_PAUSE_OCCR_SEQ = ydDaoUtils.paraRecChkNull(recGetVal, "YD_EQP_PAUSE_OCCR_SEQ");
                szYD_EQP_PAUSE_OCC_DT   = ydDaoUtils.paraRecChkNull(recGetVal, "YD_EQP_PAUSE_OCC_DT");

                if(szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_BREAK)){
                    // 수신전문의 설비상태가 고장일 경우 해당설비ID의 차수+1에 업데이트
                    ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [3] 설비 휴지테이블에 INSERT 처리", YdConstant.DEBUG);

                    // 해당차수를 1증가 처리
                    szYD_EQP_PAUSE_OCCR_SEQ = ydUtils.IncreaseStrToInt(szYD_EQP_PAUSE_OCCR_SEQ, 1, 18);

                    // 레코드 편성
                    setCrnschRecord = JDTORecordFactory.getInstance().create();
                    setCrnschRecord.setField("REGISTER"             , strRcvTcCode);             // 등록자
                    setCrnschRecord.setField("MODIFIER"             , strRcvTcCode);             // 수정자
                    setCrnschRecord.setField("DEL_YN"               , "N");                      // 삭제유무
                    setCrnschRecord.setField("YD_EQP_ID"            , szYD_EQP_ID);              // 설비ID
                    setCrnschRecord.setField("YD_EQP_PAUSE_OCCR_SEQ", szYD_EQP_PAUSE_OCCR_SEQ);  // 차수 + 1
                    setCrnschRecord.setField("YD_EQP_PAUSE_CODE"    , szYD_EQP_PAUSE_CODE);      // 설비휴지코드
                    setCrnschRecord.setField("YD_EQP_PAUSE_OCC_DT"  , szYD_EQP_TRBL_RCVR_DT);    // 야드설비휴지발생일시

                    // 설비휴지테이블 ISNERT
                    nRet = ydEqpPauseDao.insYdEqppause(setCrnschRecord);

                    szMsg = "설비 휴지테이블 INSERT 성공 : YD_EQP_ID(" + szYD_EQP_ID + ") YD_EQP_PAUSE_OCCR_SEQ(" + szYD_EQP_PAUSE_OCCR_SEQ + ") YD_EQP_PAUSE_CODE(" + szYD_EQP_PAUSE_CODE + ") YD_EQP_TRBL_RCVR_DT(" + szYD_EQP_TRBL_RCVR_DT + ")";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                }else{
                    // 수신전문의 설비상태가 복구일 경우 해당설비ID의 해당차수에 업데이트
                    ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [3] 설비 휴지테이블에 UPDATE 처리", YdConstant.DEBUG);

                    setCrnschRecord = JDTORecordFactory.getInstance().create();
                    setCrnschRecord.setField("YD_EQP_ID"              , szYD_EQP_ID);              // 설비ID
                    setCrnschRecord.setField("MODIFIER"               , strRcvTcCode);             // 수정자
                    setCrnschRecord.setField("YD_EQP_PAUSE_OCCR_SEQ"  , szYD_EQP_PAUSE_OCCR_SEQ);  // 해당 차수
                    setCrnschRecord.setField("YD_EQP_PAUSE_CODE"      , szYD_EQP_PAUSE_CODE);      // 설비휴지코드
                    setCrnschRecord.setField("YD_EQP_PAUSE_END_DT"    , szYD_EQP_TRBL_RCVR_DT);    // 야드설비휴지종료일시
                    setCrnschRecord.setField("YD_EQP_PAUSE_OCC_DT"    , szYD_EQP_PAUSE_OCC_DT);    // 야드설비휴지발생일시(차를 계산하기 위함)
                    setCrnschRecord.setField("YD_EQP_PAUSE_RCVR_CNTS" , szYD_EQP_PAUSE_RCVR_CNTS); // 야드설비휴지복구내용 (일단 항목이 없음)*************************************************

                    // 설비휴지테이블 UPDATE
                    nRet = ydEqpPauseDao.updYdEqpPauseRepair(setCrnschRecord);

                    szMsg = "설비 휴지테이블 UPDATE 성공 : YD_EQP_ID(" + szYD_EQP_ID + ") YD_EQP_PAUSE_OCCR_SEQ(" + szYD_EQP_PAUSE_OCCR_SEQ + ") YD_EQP_PAUSE_CODE(" + szYD_EQP_PAUSE_CODE + ") YD_EQP_TRBL_RCVR_DT(" + szYD_EQP_TRBL_RCVR_DT + ")";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                }
            }
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }

        return ;
    }

    /**
     * 오퍼레이션명 : C3설비고장복구실적 야드 설비테이블 설비상태 업데이트
     *
     * @param  ● msgRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int C3YDL008UpdYdEqp(JDTORecord msgRecord, int intGp) throws JDTOException {
        // DAO객체 생성
        YdEqpDao ydEqpDao   = new YdEqpDao();

        // 변수 선언
        String szMethodName = "C3YDL008UpdYdEqp";
        String szMsg        = "";
        int nRet            = 0;

        try{
            nRet = ydEqpDao.updYdEqp(msgRecord, intGp);

            switch(nRet){
                case 0 :
                    szMsg = "No Data Found!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -1 :
                    szMsg = "Dup_val_on_index!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -2 :
                    szMsg = "Parameter Error!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -3 :
                    szMsg = "Execution Failed!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
            }
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return nRet = -1;
        }
        return nRet;
    }

    /**
     * 오퍼레이션명 : C3설비고장복구실적 야드 설비휴지테이블설비상태 업데이트 (C3YDL008, C7YDL008)
     *
     * @param  ● msgRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int C3YDL008UpdYdEqpPause(JDTORecord msgRecord, int intGp) throws JDTOException {
        // DAO객체 생성
        YdEqpPauseDao ydEqpPauseDao   = new YdEqpPauseDao();

        // 변수 선언
        String szMethodName = "C3YDL008UpdYdEqpPause";
        String szMsg        = "";
        int nRet            = 0;

        try{
            nRet = ydEqpPauseDao.updYdEqppause(msgRecord, intGp);

            switch(nRet){
                case 0 :
                    szMsg = "No Data Found!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -1 :
                    szMsg = "Dup_val_on_index!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -2 :
                    szMsg = "Parameter Error!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -3 :
                    szMsg = "Execution Failed!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
            }
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return nRet = -1;
        }
        return nRet;
    }

    /**
     * 오퍼레이션명 : C연주정정L2 설비모드변경실적 (C3YDL009, C7YDL009)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procC3EqpMdModWr(JDTORecord msgRecord) throws DAOException {
        // 레코드 선언
        JDTORecord setCrnschRecord = null;
        JDTORecord SndRecord = null;

        // 변수 선언
        String szMethodName        = "procC3EqpMdModWr";
        String szMsg               = "";
        String szOperationName     = "C연주정정L2 설비모드변경실적";
        String szRcvTcCode         = null;
        int nRet                   = 0;

        szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
            // Log 테이블 등록
            //=============================================================
            szMsg = "[C연주정정L2] 설비고장복구실적 수신";
            ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            setCrnschRecord = JDTORecordFactory.getInstance().create();
            SndRecord = JDTORecordFactory.getInstance().create();

            String sYdEqpWrkMode = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE");  // 1: On-Line, "2": Off-Line

            if("2".equals(sYdEqpWrkMode)){
                sYdEqpWrkMode = "0"; // L3는 '0'이 Off-Line
            }

            setCrnschRecord.setField("YD_EQP_ID"        , ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"));       // 설비ID
            setCrnschRecord.setField("YD_EQP_WRK_MODE"  , sYdEqpWrkMode);
            nRet = this.C3YDL009UpdYdEqp(setCrnschRecord, 0);
            if(nRet == -1){
                szMsg = "설비테이블 업데이트 중  Error : " + nRet;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            //==================================================================
            // 설비모드변경실적 전문 수신하여 설비 테이블에 업데이트 후
            // 김진욱 리스케줄 호출을 직접 호출
            //
            // 파라미터 : 설비ID
            // 연주 정정C3 => procY1CrnReSch()
            //==================================================================
            //SndRecord.setField("YD_EQP_ID", msgRecord.getFieldString("YD_EQP_ID"));
            //objCrnResch.procY1CrnReSch(SndRecord);

        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new DAOException(szMsg);
        }
    }

    /**
     * 오퍼레이션명 : C3설비모드변경실적  설비테이블 야드설비작업Mode 업데이트
     *
     * @param  ● msgRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int C3YDL009UpdYdEqp(JDTORecord msgRecord, int intGp) throws JDTOException {
        // DAO객체 생성
        YdEqpDao ydEqpDao   = new YdEqpDao();

        // 변수 선언
        String szMethodName = "C3YDL009UpdYdEqp";
        String szMsg        = "";
        int nRet            = 0;

        try{
            nRet = ydEqpDao.updYdEqp(msgRecord, intGp);

            switch(nRet){
                case 0 :
                    szMsg = "No Data Found!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -1 :
                    szMsg = "Dup_val_on_index!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -2 :
                    szMsg = "Parameter Error!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
                case -3 :
                    szMsg = "Execution Failed!!!, ErrorCode:" + nRet;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return nRet = -1;
            }
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return nRet = -1;
        }
        return nRet;
    }

    /**
     * 오퍼레이션명 : C연주정정L2 ROT재료도착통과정보 (C3YDL010, C7YDL010) : 현재 처리되는 내용 없음
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procC3RotMatlArrPassInfo(JDTORecord msgRecord) throws DAOException {

        // 변수 선언
        String szMethodName        = "procC3RotMatlArrPassInfo";
        String szMsg               = "";
        String szOperationName     = "C연주정정L2 ROT재료도착통과정보";
        String szRcvTcCode         = null;
        String szCC_MC_CD          = "";

        String szEqpID_D           = "";
        String szEqpID_C           = "";
        String szEqpID_B           = "";
        String szEqpID_A           = "";
        int nBedCnt_D              = 0;
        int nBedCnt_C              = 0;
        int nBedCnt_B              = 0;
        int nBedCnt_A              = 0;
        int nIdx                   = 0;
        int nRet                   = 0;

        szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
            // Log 테이블 등록
            //=============================================================
            szMsg = "[C연주정정L2] ROT재료도착통과정보 수신";
            ydUtils.putLogMsg("A",YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            //======================================================================
            // 코드 매핑 : 조건비교가 많지 않아 해쉬테이블 안쓰고 비교처리
            // 1~5 : #1~5 M/C   ADRT01, ADRT02, ADRT03, ADRT06, ADRT07
            // S   : #1 Scarfer : ADRT04
            // T   : #1 2차전단   : ADRT05
            // U   : #2 Scarfer : ADRT08
            // V   : #2 2차전단   : ADRT09
            // W   : #3 2차전단   : ADRT10
            //======================================================================
            szCC_MC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "CC_MC_CD");
            if(szCC_MC_CD.equals("1")){
                nBedCnt_D = 6;
                szEqpID_D = "ADRT01";
                nBedCnt_C = 1;
                szEqpID_C = "ACRT01";
            } else if(szCC_MC_CD.equals("2")){
                nBedCnt_D = 6;
                szEqpID_D = "ADRT02";
                nBedCnt_C = 2;
                szEqpID_C = "ACRT02";
                nBedCnt_B = 2;
                szEqpID_B = "ABRT02";
                nBedCnt_A = 3;
                szEqpID_A = "AART02";
            } else if(szCC_MC_CD.equals("3")){
                nBedCnt_D = 6;
                szEqpID_D = "ADRT03";
                nBedCnt_C = 1;
                szEqpID_C = "ACRT03";
            } else if(szCC_MC_CD.equals("4")){
                //2012.08.06 추가
                nBedCnt_D = 6;
                szEqpID_D = "ADRT06";
                nBedCnt_C = 1;
                szEqpID_C = "ACRT06";
            } else if(szCC_MC_CD.equals("5")){
                //2012.08.06 추가
                nBedCnt_D = 6;
                szEqpID_D = "ADRT07";
                nBedCnt_C = 1;
                szEqpID_C = "ACRT07";
            } else if(szCC_MC_CD.equals("S")){
                nBedCnt_C = 1;
                szEqpID_C = "ACRT04";
                nBedCnt_B = 3;
                szEqpID_B = "ABRT04";
            } else if(szCC_MC_CD.equals("T")){
                szEqpID_A = "AART05";
                nBedCnt_A = 6;
            } else if(szCC_MC_CD.equals("U")){
                //2012.08.06 추가
                szEqpID_A = "AART08";
                nBedCnt_A = 6;
            } else if(szCC_MC_CD.equals("V")){
                //2012.08.06 추가
                szEqpID_A = "AART09";
                nBedCnt_A = 6;
            } else if(szCC_MC_CD.equals("W")){
                //2012.08.06 추가
                szEqpID_A = "AART10";
                nBedCnt_A = 6;
            } else {
                szMsg = "연주Machine코드(" + szCC_MC_CD + ")가 정의되어 있지 않은 값으로 수신되었음";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            szMsg = "C3ROT재료도착통과정보 (C3YDL010) 처리완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }catch(Exception e){
            throw new DAOException("Error : " + e.getLocalizedMessage());
        }
    }

    /**
     * 오퍼레이션명 : ROT재료도착정보 업데이트 처리
     *
     * @param  ● msgRecord, szEqpID, nLoopCnt, nIdx
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public int UpdateROTStlNo(JDTORecord msgRecord, String szEqpID, int nLoopCnt, int nIdx) throws JDTOException {

        // DAO객체 선언
        YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

        // 레코드 선언
        JDTORecord recPara         = null;

        // 변수 선언
        String szMethodName        = "UpdateROTStlNo";
        String szMsg               = "";
        String szSTL_NO            = "";
        String szBED_NO            = "";
        int i                      = 0;
        int nLastIdx               = 0;
        int nRet                   = 0;

        //======================================================================
        // 적치단 업데이트
        //======================================================================
        for(i=0; i<nLoopCnt; i++){

            // 레코드 생성
            recPara  = JDTORecordFactory.getInstance().create();

            // 00
            szBED_NO = YdUtils.fillSpZr("" + (i+1), 2, 0);

            // 재료번호 추출
            nLastIdx = (nIdx+i+1);
            szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO" + nLastIdx);

            // 업데이트 레코드 편집
            recPara.setField("STL_NO"             , szSTL_NO);
            recPara.setField("YD_STK_LYR_MTL_STAT", "C");       // FIX
            recPara.setField("YD_STK_COL_GP"      , szEqpID);
            recPara.setField("YD_STK_BED_NO"      , szBED_NO);
            recPara.setField("YD_STK_LYR_NO"      , "001");     // FIX

            // 적치단 업데이트 처리
            nRet = ydStkLyrDao.updYdStklyrStlNoOnRcvROT(recPara);
            if(nRet <= 0){
                szMsg = "C3ROT재료도착통과정보 수신 시 적치단 업데이트 실패 RetCode[" + nRet + "] STL_NO(" + szSTL_NO + ") YD_STK_COL_GP(" + szEqpID + ") YD_STK_BED_NO(" + szBED_NO + ")";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            } else {
                szMsg = "C3ROT재료도착통과정보 수신 시 적치단 업데이트 성공 RetCode[" + nRet + "] STL_NO(" + szSTL_NO + ") YD_STK_COL_GP(" + szEqpID + ") YD_STK_BED_NO(" + szBED_NO + ")";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            }
        }

        return nLastIdx;
    }

    /**
     * 오퍼레이션명 : C연주정정L2 HandScarfing작업진행정보 (C3YDL011)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procHandScarfingWrkProgInfo(JDTORecord msgRecord) throws DAOException {
        YdStkLyrDao ydStkLyrDao    = new YdStkLyrDao();

        // 레코드 선언
        JDTORecord recPara         = null;

        // 변수 선언
        String szMethodName        = "procHandScarfingWrkProgInfo";
        String szMsg               = "";
        String szOperationName     = "C연주정정L2 HandScarfing작업진행정보";
        String szRcvTcCode         = null;
        String szSTL_NO            = "";
        int nRet                   = 0;

        szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
            // Log 테이블 등록
            //=============================================================
            szMsg = "[C연주정정L2] HandScarfing작업진행정보 수신";
            ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);


            //============================================
            // 저장품제원(YDY1L002) 송신
            //
            // BED 9개를 조회하며 재료번호 있는 BED에 대해서 동별조회
            // 내려보냄
            //============================================
            for(int i=0; i < 9; i++){

                szSTL_NO =  ydDaoUtils.paraRecChkNull(msgRecord, "SLAB_NO"+(i+1)).trim();

                szMsg = "SLAB_NO"+(i+1) + "=" + szSTL_NO;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                if(!szSTL_NO.equals("")){
                    // 적치단 업데이트
                    recPara = JDTORecordFactory.getInstance().create();
                    recPara.setField("YD_STK_COL_GP"      , "ABSB01");
                    recPara.setField("YD_STK_LYR_NO"      , "001");
                    recPara.setField("YD_STK_BED_NO"      , YdUtils.fillSpZr(""+(i+1), 2, 0));
                    recPara.setField("STL_NO"             , szSTL_NO);
                    recPara.setField("YD_STK_LYR_MTL_STAT", "C");
                    nRet = ydStkLyrDao.updYdStklyr(recPara, 0);

                } else {
                    // 적치단 업데이트
                    recPara = JDTORecordFactory.getInstance().create();
                    recPara.setField("YD_STK_COL_GP"      , "ABSB01");
                    recPara.setField("YD_STK_LYR_NO"      , "001");
                    recPara.setField("YD_STK_BED_NO"      , YdUtils.fillSpZr(""+(i+1), 2, 0));
                    recPara.setField("STL_NO"             , "");
                    recPara.setField("YD_STK_LYR_MTL_STAT", "E");
                    nRet = ydStkLyrDao.updYdStklyr(recPara, 0);
                }
            }
            ydUtils.putLog(szSessionName, szMethodName, "HandScarfing작업진행정보처리 완료 ", YdConstant.DEBUG);
        }catch(Exception e){
            throw new DAOException("Error : " + e.getLocalizedMessage());
        }
    }

    /**
     * 야드관리 > 코일제품창고 > 기준관리 > 설비기준관리 조회 (화면:설비기준관리)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param JDTORecord
     * @return JDTORecordSet
     * @throws DAOException
     * @작성자 : hun
     * @작성일 : 2015.08.31
     */
    public JDTORecordSet getEqpMgtList(JDTORecord inDto) throws DAOException {

        int    intRtnVal    = 0;
        String szMsg        = "";
        CoilGdsJspDao dao   = new CoilGdsJspDao();
        String szMethodName = "getEqpMgtList";
        JDTORecordSet outRecSet = null;
        JDTORecord recPara = JDTORecordFactory.getInstance().create();
        JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();

        String sQueryId = "";
        try {
            szMsg = "JSP-SESSION [설비기준 조회 (화면:설비기준관리)] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            //recPara.setField("V_YD_GP",           inDto.getField("YD_GP"));
            recPara.setField("V_YD_BAY_GP",     inDto.getField("YD_BAY_GP"));
            /*
            recPara.setField("V_PAGE_CNT1",     inDto.getField("PAGE_NO"));
            recPara.setField("V_PAGE_CNT2",     inDto.getField("PAGE_NO"));
            recPara.setField("V_ROW_CNT1",      inDto.getField("PAGE_SIZE"));
            recPara.setField("V_ROW_CNT2",      inDto.getField("PAGE_SIZE"));
            */








            outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp");
            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("YD_BAY_GP",           inDto.getField("YD_BAY_GP"));//스케줄코드
            sQueryId = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getCoilYdCrnStsSetByYdgp";
            intRtnVal = ydCommDao.select(recPara, outRecSet, sQueryId);

            //outRecSet = dao.getEqpMgtList(recPara);

            if (outRecSet == null || outRecSet.size() < 1) {

                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                return outRecSet;

             } // end of if

        } catch (Exception e) {
            e.printStackTrace();
            // Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
            throw new DAOException(getClass().getName() + e.getMessage(),e);
        } finally {
        }
        szMsg = "JSP-SESSION [설비기준 조회 (화면:설비기준관리)] 끝";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

        return outRecSet;
    }


    /**
     *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  JDTORecord inRec parameter record
     * @return
     * @ejb.transaction type="RequiresNew"
     */
    public int updYdEqpTX(JDTORecord inRec) throws DAOException, JDTOException {

        int intRtnVal               = 0;
        YdEqpDao ydEqpDao = new YdEqpDao();
        try {

//          기존 방식 적용
            intRtnVal = ydEqpDao.updYdEqpTX(inRec,0);
            if(intRtnVal ==0){
                return intRtnVal = -1;
            }

            intRtnVal = 1;

        } catch (Exception e) {
            // Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
            throw new DAOException(e.getMessage(), e);
        }
        return intRtnVal;
    } // end of updYdPlateCommBookOutLocReTX





    //---------------------------------------------------------------------------

    ///////////////////////////////////////////////////////////////////////////////
    ///                          전사물류개선 프로젝트 2021.1.6                  ///
    ///////////////////////////////////////////////////////////////////////////////

    /**
     * 오퍼레이션명 : 후판제품야드 설비운전모드전환 (Y4YDL003)
     *  - 전사물류개선 2021.1.6 기존 기능 분리(자동화크레인관련)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY9EqpDrvMdTurnov(JDTORecord msgRecord) throws DAOException {

        // 레코드 선언
        JDTORecord    recPara      = null;
        JDTORecord setCrnschRecord = null;

        // DAO 및 UTIL 객체 생성
        YdDelegate ydDelegate      = new YdDelegate();
        EJBConnector ejbConn       = null;

        // 변수선언
        String szMethodName        = "procY9EqpDrvMdTurnov[후판제품야드L2 설비운전모드전환]";
        String szMsg               = "";
        String szRcvTcCode         = null;
        String szYD_EQP_ID         = "";
        String szYD_EQP_WRK_MODE   = "";
        int nRet                   = 0;

        // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
        boolean isSendToEaiY9 = false;
        String szYD_EQP_WRK_MODE2   = ""; // A:무인, R:리모컨, E:정비, M:유인

        szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판제품야드] 설비운전모드전환 수신";
            ydUtils.putLogMsg("K", YdConstant.YD_MONITORING_CHANNEL_K, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            setCrnschRecord = JDTORecordFactory.getInstance().create();

            szYD_EQP_ID         = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            szYD_EQP_WRK_MODE   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE");

            // 2020.01.06 설비운전모드 추가
            szYD_EQP_WRK_MODE2  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE2");
            setCrnschRecord.setField("YD_EQP_ID"        , szYD_EQP_ID);       // 설비ID
            setCrnschRecord.setField("YD_EQP_WRK_MODE"  , szYD_EQP_WRK_MODE); // 1: On-Line, 2: Off-Line
            setCrnschRecord.setField("YD_EQP_WRK_MODE2"  , szYD_EQP_WRK_MODE2); // A:무인, R:리모컨, E:정비, M:유인
            setCrnschRecord.setField("MODIFIER"         , szRcvTcCode);

            // 자동화크레인의 상태를 별로로 관리(2열연기준으로 따라함)
            // 일시정지 및 비상정지 , 정상 일 경우에 관련 컬럼을 업데이트한다.
            if("4".equals(szYD_EQP_WRK_MODE) || "5".equals(szYD_EQP_WRK_MODE) ){
                setCrnschRecord.setField("YD_EQP_AUTO_CRN_MODE" , szYD_EQP_WRK_MODE);
            }
            else if("1".equals(szYD_EQP_WRK_MODE)){
                setCrnschRecord.setField("YD_EQP_AUTO_CRN_MODE" , szYD_EQP_WRK_MODE);
            }

            JDTORecord param = JDTORecordFactory.getInstance().create();
            JDTORecordSet rstEqp = JDTORecordFactory.getInstance().createRecordSet("ydPlate");
            YdEqpDao ydEqpDao     = new YdEqpDao();

            String sBefore_YD_EQP_WRK_MODE = "";
            int intRtnVal = ydEqpDao.getYdEqp(param, rstEqp, 0);
            if(intRtnVal>0){
                sBefore_YD_EQP_WRK_MODE = rstEqp.getRecord(0).getFieldString("YD_EQP_WRK_MODE");
            }

            // ydDelegate.sendMsg(recPara);
            ejbConn = new EJBConnector("default", "PlateYdRcvL2SeEJB", this);
            nRet = ((Integer)ejbConn.trx("updateTx", new Class[] { JDTORecord.class, String.class }, new Object[] { setCrnschRecord, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updEqpByStatus" })).intValue();

            if(nRet == -1){
                szMsg = "설비테이블 업데이트 중  Error : (YD_EQP_ID : " + szYD_EQP_ID + "), (YD_EQP_WRK_MODE : " + szYD_EQP_WRK_MODE + "), Ret : " + nRet;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            /*
             * YD_EQP_WRK_MODE (1: On-Line, 2: Off-Line)에 따른 업무 정의
             * 1: On-Line - 크레인 리스케줄 호출[복구], C연주 크레인 작업지시 호출[YD_EQP_ID, YD_WRK_PROG_STAT(W)]
             * 2: Off-Line - 크레인 리스케줄 호출[고장]
             *
             * 2021.01.06 전사물류개선 유인무인관련 로직 추가
             * YD_EQP_WRK_MODE2 : A:무인, R:리모컨, E:정비, M:유인
             *  - TB_YD_EQP_ID의 해당 컬럼만 Update처리한다.
             */
            // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
            if( szYD_EQP_WRK_MODE.equals("1") ) {           // 1: On-Line

                // 모드변경 이전 신규 값을 확인하여
                // 두 값이 같지 않을 경우에만 로직을 처리한다.
                if( !szYD_EQP_WRK_MODE.equals(sBefore_YD_EQP_WRK_MODE)){

                    // OFF-LINE에서 ONLINE케이스만 리스케쥴링 처리한다.
                    if("2".equals(sBefore_YD_EQP_WRK_MODE)){
                        recPara = JDTORecordFactory.getInstance().create();

                        //서버이중화로 인한 JMS 호출 순서가 바뀔 수 있으므로 해결 필요.
                        //크레인 리스케줄 호출[복구]
                        recPara.setField("MSG_ID",           "YDYDJ508");
                        recPara.setField("YD_EQP_ID", szYD_EQP_ID);

                        //------------------------------------------------------------------------------------------------
                        // EJB 호출로 변경 (2010.01.14) : 이현성
                        //------------------------------------------------------------------------------------------------
                        // ydDelegate.sendMsg(recPara);
                        ejbConn = new EJBConnector("default", this);
                        ejbConn.trx("CrnReSchSeEJB", "procY4CrnReSch", recPara);
                        //------------------------------------------------------------------------------------------------

                        szMsg = "[제품창고 설비운전모드전환]제품창고 크레인 리스케줄 호출[복구]";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }

                }

            }else if( szYD_EQP_WRK_MODE.equals("2")) {      // 2: Off-Line

                // 모드변경 이전 신규 값을 확인하여
                // 두 값이 같지 않을 경우에만 로직을 처리한다.
                if( !szYD_EQP_WRK_MODE.equals(sBefore_YD_EQP_WRK_MODE)){

                    // OFF-LINE에서 ONLINE케이스만 리스케쥴링 처리한다.
                    if("1".equals(sBefore_YD_EQP_WRK_MODE)){

                        //크레인 리스케줄 호출[고장]
                        recPara = JDTORecordFactory.getInstance().create();
                        recPara.setField("MSG_ID",           "YDYDJ508");
                        recPara.setField("YD_EQP_ID", szYD_EQP_ID);

                        //------------------------------------------------------------------------------------------------
                        // EJB 호출로 변경 (2010.01.14) : 이현성
                        //------------------------------------------------------------------------------------------------
                        // ydDelegate.sendMsg(recPara);
                        ejbConn = new EJBConnector("default", this);
                        ejbConn.trx("CrnReSchSeEJB", "procY4CrnReSch", recPara);
                        //------------------------------------------------------------------------------------------------

                        szMsg = "[제품창고 설비운전모드전환]제품창고 크레인 리스케줄 호출[고장]";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }
                }
            }else if("4".equals(szYD_EQP_WRK_MODE) || "5".equals(szYD_EQP_WRK_MODE) ){
                // 4: 일시정지, 5:비상정지

            }else{                                          // 정의되지 않은 값
                szMsg = "[제품창고 설비운전모드전환]야드설비작업Mode[" + szYD_EQP_WRK_MODE + "]가 정의되지 않은 값입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            //------------------------------------------------------------------
            // 제품창고 스케줄 설비정보변경시
            //------------------------------------------------------------------
            JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
            //recFlex.setField("YD_GP",  YdConstant.YD_GP_PLATE_GDS_YARD);
            recFlex.setField("YD_GP",  szYD_EQP_ID.substring(0,1)); //--2013.02.15 수정 (3기)
            recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putYdFlexCrnWrk("", recFlex);

            recPara = JDTORecordFactory.getInstance().create();
            if(szYD_EQP_ID.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD)) { //--2013.02.15 수정 (3기)
                recPara.setField("MSG_ID"        , "YDY8L005");

                // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
                isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szYD_EQP_ID);
                if (isSendToEaiY9){
                    recPara.setField("MSG_ID"        , "YDY9L005");
                }
            } else {
                recPara.setField("MSG_ID"        , "YDY4L005");
            }
            recPara.setField("YD_EQP_ID"     , szYD_EQP_ID);
            recPara.setField("YD_L2_WR_GP"   , "M");            //U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경
            recPara.setField("YD_L3_HD_RS_CD", "0000");         //야드L3처리결과코드
            ydDelegate.sendMsg(recPara);

        }catch(Exception e) {
            try{
                if(!"".equals(szYD_EQP_ID)){
                    if(PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szYD_EQP_ID)){
                        JDTORecord sMsg = JDTORecordFactory.getInstance().create();
                        sMsg.setField("MSG_ID"        , "YDY9L005");
                        sMsg.setField("YD_EQP_ID"     , szYD_EQP_ID);
                        sMsg.setField("YD_L2_WR_GP"   , "M");           //U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경
                        sMsg.setField("YD_L3_HD_RS_CD", "7777");            //야드L3처리결과코드
                        sMsg.setField("YD_L3_MSG", "Error : " + e.getLocalizedMessage());           //야드L3처리결과코드
                        ydDelegate.sendMsg(sMsg);
                    }
                }
            }catch(Exception ex){}
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new DAOException(szMsg);
        }
    }


    /**
     * 오퍼레이션명 : 후판제품야드 설비고장복구실적 (Y9YDL004)
     *  - 전사물류개선 2021.1.6 기존 기능 분리(자동화크레인관련)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY9EqpTrblRcvrWr(JDTORecord msgRecord) throws DAOException {
        // 레코드 선언
        JDTORecord recPara             = null;
        JDTORecord setCrnschRecord     = null;

        // DAO 및 UTIL 객체 생성
        YdDelegate ydDelegate          = new YdDelegate();
        EJBConnector ejbConn           = null;

        // 변수 선언
        String szMethodName            = "procY9EqpTrblRcvrWr[후판제품야드L2 설비고장복구실적]";
        String szMsg                   = "";
        String szRcvTcCode             = null;
        String szYdGp                  = ""; //--2013.02.14 추가 (3기)
        String szYD_EQP_ID             = "";
        String szYD_EQP_STAT           = "";
        String szYD_EQP_PAUSE_CODE     = "";
        String szYD_EQP_TRBL_RCVR_DT   = "";
        String szYD_EQP_STAT_UPD       = "";
        int nRet                       = 0;
        boolean isSendToEaiY9 = false;  // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부

        szRcvTcCode = ydUtils.getTcCode(msgRecord);
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
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판제품야드] 설비고장복구실적 수신";
            ydUtils.putLogMsg("K", YdConstant.YD_MONITORING_CHANNEL_K , szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            setCrnschRecord = JDTORecordFactory.getInstance().create();

            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");

             // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
            JDTORecord params = JDTORecordFactory.getInstance().create();
            params.setField("YD_EQP_ID", szYD_EQP_ID);

            if(szYD_EQP_ID.equals("")){
                szMsg = "설비ID가 존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_STAT");
            if(szYD_EQP_STAT.equals("")){
                szMsg = "설비상태가  존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            szYD_EQP_PAUSE_CODE = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
            if(szYD_EQP_PAUSE_CODE.equals("")){
                szMsg = "휴지코드가  존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            szYD_EQP_TRBL_RCVR_DT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_TRBL_RCVR_DT");
            if(szYD_EQP_TRBL_RCVR_DT.equals("")){
                szMsg = "야드설비고장복구일시가  존재하지 않습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            //야드구분을 설비ID 첫 자리로 구분한다.
            if(szYD_EQP_ID.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD)) { //--2013.02.14 추가 (3기)
                szYdGp = YdConstant.YD_GP_PLATE2_GDS_YARD; //2후판제품창고
            } else {
                szYdGp = YdConstant.YD_GP_PLATE_GDS_YARD; //1후판제품창고
            }

            //============================================================================
            // 변환...
            //============================================================================
            if(szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)){
                // 고장
                szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_BREAK;
                if(szYD_EQP_PAUSE_CODE.equals("0000") || szYD_EQP_PAUSE_CODE.equals("")){
                    szYD_EQP_PAUSE_CODE = "B000";
                }else{
                    szYD_EQP_PAUSE_CODE   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
                }
            }else if(szYD_EQP_STAT.equals("R") ||
                     szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_NORM)){
                // 복구
                szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_NORM;
                if(szYD_EQP_PAUSE_CODE.equals("0000") || szYD_EQP_PAUSE_CODE.equals("")){
                    szYD_EQP_PAUSE_CODE = "R000";
                }else{
                    szYD_EQP_PAUSE_CODE   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
                }
            }

            //============================================================================
            // 휴지테이블 업데이트
            //
            // 고장이나 복구일 때 남김
            //============================================================================
            if(szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_BREAK) ||
               szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_NORM)){
                ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [2] 휴지 테이블에 업데이트 처리", YdConstant.DEBUG);
                this.ProcEqpPause(szRcvTcCode, szYD_EQP_ID, szYD_EQP_STAT_UPD, szYD_EQP_PAUSE_CODE, szYD_EQP_TRBL_RCVR_DT);
            }

            // 전사물류개선 2021. 1. 6
            JDTORecord param = JDTORecordFactory.getInstance().create();
            JDTORecordSet rstEqp = JDTORecordFactory.getInstance().createRecordSet("ydPlate");
            YdEqpDao ydEqpDao     = new YdEqpDao();

            String sBefore_YD_EQP_STAT = "";
            int intRtnVal = ydEqpDao.getYdEqp(param, rstEqp, 0);
            if(intRtnVal>0){
                sBefore_YD_EQP_STAT = rstEqp.getRecord(0).getFieldString("YD_EQP_STAT");
            }

            //============================================================================
            // 크레인 설비인 경우 설비 작업 상태 및 스케줄 변경
            //============================================================================
            String lzRtnMsg = null;

            if (szYD_EQP_ID.substring(2,4).equals(YdConstant.YD_EQP_GP_CRANE)){

                // 고장 UPDATE 시
                if(szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_BREAK)){

                    // 해당 설비 스케줄이 권상지시(YD_EQP_STAT_UP_WO) 일경우 IDLE 상태로 변경 YD_EQP_STAT_IDLE
                    lzRtnMsg = this.updCrnWrkProgStatUpWoToIdle(szYD_EQP_ID);

                    if(lzRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
                        szMsg = "스케줄 변경 성공 하였습니다.";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }else if (lzRtnMsg.equals(YdConstant.RETN_CD_FAILURE)){
                        szMsg = "스케줄 변경 실패 하였습니다.";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    }
                }

                // 정상 UPDATE 시
                else{
                    szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_IDLE;
                }
            }
            //============================================================================
            // 설비테이블에 야드설비상태 업데이트
            //============================================================================
            ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [3] 설비 테이블에 업데이트 처리", YdConstant.DEBUG);
            setCrnschRecord.setField("YD_EQP_ID"  , szYD_EQP_ID);   // 설비ID
            setCrnschRecord.setField("YD_EQP_STAT", szYD_EQP_STAT_UPD); // "B": 고장, "W": 대기
            setCrnschRecord.setField("MODIFIER"   , szRcvTcCode);

            ejbConn = new EJBConnector("default", "PlateYdRcvL2SeEJB", this);
            nRet = ((Integer)ejbConn.trx("updateTx", new Class[] { JDTORecord.class, String.class }, new Object[] { setCrnschRecord, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updEqpByStatus" })).intValue();

            if(nRet == -1){
                szMsg = "설비테이블 업데이트 중  Error : (YD_EQP_ID : " + szYD_EQP_ID + "),(YD_EQP_STAT : " + szYD_EQP_STAT + "), Ret : " + nRet;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                throw new JDTOException(szMsg);
            }

            //------------------------------------------------------------------
            //제품창고  설비정보변경시
            //------------------------------------------------------------------
            JDTORecord recFlex = JDTORecordFactory.getInstance().create();
            //recFlex.setField("YD_GP",  YdConstant.YD_GP_PLATE_GDS_YARD);
            recFlex.setField("YD_GP",  szYdGp); //--2013.02.14 수정 (3기)
            recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putYdFlexCrnWrk("", recFlex);

            /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
             * YD_EQP_STAT (R: 복구, B: 고장)에 따른 업무 정의
             * R: 복구 - 크레인 리스케줄링 호출[복구], 제품창고 크레인 작업지시 호출[YD_EQP_ID, YD_WRK_PROG_STAT(W)]
             * B: 고장 - 크레인 리스케줄링 호출[고장]
             +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            if( szYD_EQP_STAT.equals("R") ||
                szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_NORM) ) {           // R: 복구

                // 전사물류개선 2021. 1.7 이전상태와 동일하면 pass
                if(!sBefore_YD_EQP_STAT.equals(szYD_EQP_STAT)){
                    recPara = JDTORecordFactory.getInstance().create();
                    //서버이중화로 인한 JMS 호출 순서가 바뀔 수 있으므로 해결 필요.
                    //크레인 리스케줄링 호출[복구]
                    recPara.setField("MSG_ID",    "YDYDJ508");
                    recPara.setField("YD_EQP_ID", szYD_EQP_ID);
                    ejbConn = new EJBConnector("default", this);
                    ejbConn.trx("CrnReSchSeEJB", "procY4CrnReSch", recPara);

                    szMsg = "[제품창고 설비고장복구실적]크레인 리스케줄링 호출[복구]";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                }

            }else if( szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {     //B: 고장

                // 전사물류개선 2021. 1.7 이전상태와 동일하면 pass
                if(!sBefore_YD_EQP_STAT.equals(szYD_EQP_STAT)){
                    //크레인 리스케줄링 호출[고장]
                    recPara = JDTORecordFactory.getInstance().create();
                    recPara.setField("MSG_ID",    "YDYDJ508");
                    recPara.setField("YD_EQP_ID", szYD_EQP_ID);
                    ejbConn = new EJBConnector("default", this);
                    ejbConn.trx("CrnReSchSeEJB", "procY4CrnReSch", recPara);

                    szMsg = "[제품창고 설비고장복구실적]크레인 리스케줄링 호출[고장]";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                }

            }else{                                          // 정의되지 않은 값
                szMsg = "[제품창고 설비고장복구실적]야드설비상태[" + szYD_EQP_STAT + "]가 정의되지 않은 값입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            /*
             * 업무 : 크레인작업실적응답 전문 전송(YDY4L005/YDY8L005)
             */
            recPara = JDTORecordFactory.getInstance().create();
            if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) {   //--2013.02.14 수정 (3기)
                recPara.setField("MSG_ID"        , "YDY8L005");

                // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
                isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szYD_EQP_ID);
                if (isSendToEaiY9){
                    recPara.setField("MSG_ID"        , "YDY9L005");
                }
            } else {
                recPara.setField("MSG_ID"        , "YDY4L005");
            }
            recPara.setField("YD_EQP_ID"     , szYD_EQP_ID);
            recPara.setField("YD_L2_WR_GP"   , szYD_EQP_STAT);          //B:고장,R:복구
            recPara.setField("YD_L3_HD_RS_CD", "0000");                 //야드L3처리결과코드
            ydDelegate.sendMsg(recPara);

        }catch(Exception e) {
            try{
                if(!"".equals(szYD_EQP_ID)){
                    if(PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szYD_EQP_ID)){
                        JDTORecord sMsg = JDTORecordFactory.getInstance().create();
                        sMsg.setField("MSG_ID"        , "YDY9L005");
                        sMsg.setField("YD_EQP_ID"     , szYD_EQP_ID);
                        sMsg.setField("YD_L2_WR_GP"   , "R");           //U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경
                        sMsg.setField("YD_L3_HD_RS_CD", "7777");            //야드L3처리결과코드
                        sMsg.setField("YD_L3_MSG", "Error : " + e.getLocalizedMessage());           //야드L3처리결과코드
                        ydDelegate.sendMsg(sMsg);
                    }
                }
            }catch(Exception ex){}
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new DAOException(szMsg);
        }
    }



    /**
     * 오퍼레이션명 : 설비고장복구실적 휴지테이블 처리 [후판 제품 logId 추가 하기 위해 신규 작성]
     *           2024.09.02 기존 ProcEqpPause 다른 야드도 사용 하기 때문에 후판 제품 설비고장복구 부분만
     *           logId argument 추가
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● TC코드, 설비ID, 수신된 전문의 고장(B)/복구(N), 휴지코드, 발생일시, logId
     * @return ● void
     * @throws ● JDTOException
     */
    public void ProcEqpPause(String strRcvTcCode,
                             String szYD_EQP_ID,
                             String szYD_EQP_STAT_UPD,
                             String szYD_EQP_PAUSE_CODE,
                             String szYD_EQP_TRBL_RCVR_DT,
                             String logId ) throws JDTOException {

        // DAO 및 UTIL 객체 생성
        YdEqpDao ydEqpDao               = new YdEqpDao();
        YdEqpPauseDao ydEqpPauseDao     = new YdEqpPauseDao();

        // 레코드 선언
        JDTORecord recPara              = null;
        JDTORecord setCrnschRecord      = null;
        JDTORecord recGetVal            = null;
        JDTORecordSet rsResult          = null;

        // 변수 선언
        String szMethodName             = "ProcEqpPause";
        String szMsg                    = "";
        String szYD_EQP_CURR_STAT       = "";
        String szYD_EQP_PAUSE_OCCR_SEQ  = "";
        String szYD_EQP_PAUSE_OCC_DT    = "";
        String szYD_EQP_PAUSE_RCVR_CNTS = "";
        int nRet                        = 0;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선 START
if(ydUtils.isEmpty(logId)) logId 		= ydUtils.getLogIdNew("T"); 			// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8설비고장복구실적 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

        try {
            // 데이터 항목 점검
            if(strRcvTcCode.equals("")){
                szMsg = "TC CODE가 존재하지 않습니다.";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            }

            if(szYD_EQP_ID.equals("")){
                szMsg = "설비ID가 존재하지 않습니다.";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            }

            if(szYD_EQP_STAT_UPD.equals("")){
                szMsg = "설비상태가 존재하지 않습니다.";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            }

            if(szYD_EQP_PAUSE_CODE.equals("")){
                szMsg = "설비휴지코드가 존재하지 않습니다.";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            }

            if(szYD_EQP_TRBL_RCVR_DT.equals("")){
                szMsg = "휴지일시가 존재하지 않습니다.";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            }

            recPara         = JDTORecordFactory.getInstance().create();
            setCrnschRecord = JDTORecordFactory.getInstance().create();
            rsResult        = JDTORecordFactory.getInstance().createRecordSet("");

            // 설비테이블과 휴지테이블에서 해당 설비ID를 가지고 MAX차수의 상태값을 가져온다.
            // 읽어온 현재 설비의 상태값이 고장일 경우 수신받은 상태가 복구이면 휴지테이블에 MAX차수에 UPDATE 를 하고
            // 수신받은 상태가 고장이면 PASS
            // 읽어온 현재 설비의 상태값이 복구일 경우 수신받은 상태가 고장이면 휴지테이블에 MAX차수+1에 INSERT 를 하고
            // 수신받은 상태가 복구이면 PASS

            //=========================================================================================
            // 설비ID로 현재 설비의 상태와 휴지테이블에서 MAX차수의 값을 추출 [1건]  (GP : 12)
            // com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getEqpStatofMAX
            //
            // JSPEED 파라미터 : V_YD_EQP_ID
            //=========================================================================================
            recPara.setField("YD_EQP_ID", szYD_EQP_ID);
            nRet = ydEqpDao.getYdEqp(recPara, rsResult, 12);
            if(nRet < 0){
                szMsg = "설비 휴지 테이블 조회 오류 [" + nRet + "] YD_EQP_ID(" + szYD_EQP_ID + ")";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            } else if(nRet == 0){
                // 1차
                szYD_EQP_PAUSE_OCCR_SEQ = ydUtils.IncreaseStrToInt("0", 1, 18);

                // 레코드 편성
                setCrnschRecord = JDTORecordFactory.getInstance().create();
                setCrnschRecord.setField("REGISTER"             , strRcvTcCode);             // 등록자
                setCrnschRecord.setField("MODIFIER"             , strRcvTcCode);             // 수정자
                setCrnschRecord.setField("DEL_YN"               , "N");                      // 삭제유무
                setCrnschRecord.setField("YD_EQP_ID"            , szYD_EQP_ID);              // 설비ID
                setCrnschRecord.setField("YD_EQP_PAUSE_OCCR_SEQ", szYD_EQP_PAUSE_OCCR_SEQ);  // 1차
                setCrnschRecord.setField("YD_EQP_PAUSE_CODE"    , szYD_EQP_PAUSE_CODE);      // 설비휴지코드
                setCrnschRecord.setField("YD_EQP_PAUSE_OCC_DT"  , szYD_EQP_TRBL_RCVR_DT);    // 야드설비휴지발생일시

                // 설비휴지테이블 INSERT
                nRet = ydEqpPauseDao.insYdEqppause(setCrnschRecord);
                if(nRet < 0){
                    szMsg = "설비 휴지테이블 INSERT 중  Error : " + nRet + " : YD_EQP_ID(" + szYD_EQP_ID + ") YD_EQP_PAUSE_OCCR_SEQ(" + szYD_EQP_PAUSE_OCCR_SEQ + ") YD_EQP_PAUSE_CODE(" + szYD_EQP_PAUSE_CODE + ") YD_EQP_TRBL_RCVR_DT(" + szYD_EQP_TRBL_RCVR_DT + ")";
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    return ;
                }

                szMsg = "설비 휴지테이블 INSERT 성공 : YD_EQP_ID(" + szYD_EQP_ID + ") YD_EQP_PAUSE_OCCR_SEQ(" + szYD_EQP_PAUSE_OCCR_SEQ + ") YD_EQP_PAUSE_CODE(" + szYD_EQP_PAUSE_CODE + ") YD_EQP_TRBL_RCVR_DT(" + szYD_EQP_TRBL_RCVR_DT + ")";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            } else {
                rsResult.first();
                recGetVal = rsResult.getRecord();

                // DB조회를 한 현재 설비의 상태와 차수를 가져온다
                szYD_EQP_CURR_STAT      = ydDaoUtils.paraRecChkNull(recGetVal, "YD_EQP_STAT");
                szYD_EQP_PAUSE_OCCR_SEQ = ydDaoUtils.paraRecChkNull(recGetVal, "YD_EQP_PAUSE_OCCR_SEQ");
                szYD_EQP_PAUSE_OCC_DT   = ydDaoUtils.paraRecChkNull(recGetVal, "YD_EQP_PAUSE_OCC_DT");

                if(szYD_EQP_STAT_UPD.equals(YdConstant.YD_EQP_STAT_BREAK)){
                    // 수신전문의 설비상태가 고장일 경우 해당설비ID의 차수+1에 업데이트
                    ydUtils.putLogNew(szSessionName, szMethodName, szMethodName + "::    [3] 설비 휴지테이블에 INSERT 처리", YdConstant.DEBUG, logId);

                    // 해당차수를 1증가 처리
                    szYD_EQP_PAUSE_OCCR_SEQ = ydUtils.IncreaseStrToInt(szYD_EQP_PAUSE_OCCR_SEQ, 1, 18);

                    // 레코드 편성
                    setCrnschRecord = JDTORecordFactory.getInstance().create();
                    setCrnschRecord.setField("REGISTER"             , strRcvTcCode);             // 등록자
                    setCrnschRecord.setField("MODIFIER"             , strRcvTcCode);             // 수정자
                    setCrnschRecord.setField("DEL_YN"               , "N");                      // 삭제유무
                    setCrnschRecord.setField("YD_EQP_ID"            , szYD_EQP_ID);              // 설비ID
                    setCrnschRecord.setField("YD_EQP_PAUSE_OCCR_SEQ", szYD_EQP_PAUSE_OCCR_SEQ);  // 차수 + 1
                    setCrnschRecord.setField("YD_EQP_PAUSE_CODE"    , szYD_EQP_PAUSE_CODE);      // 설비휴지코드
                    setCrnschRecord.setField("YD_EQP_PAUSE_OCC_DT"  , szYD_EQP_TRBL_RCVR_DT);    // 야드설비휴지발생일시

                    // 설비휴지테이블 INSERT
                    nRet = ydEqpPauseDao.insYdEqppause(setCrnschRecord);

                    szMsg = "설비 휴지테이블 INSERT 성공 : YD_EQP_ID(" + szYD_EQP_ID + ") YD_EQP_PAUSE_OCCR_SEQ(" + szYD_EQP_PAUSE_OCCR_SEQ + ") YD_EQP_PAUSE_CODE(" + szYD_EQP_PAUSE_CODE + ") YD_EQP_TRBL_RCVR_DT(" + szYD_EQP_TRBL_RCVR_DT + ")";
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                }else{
                    // 수신전문의 설비상태가 복구일 경우 해당설비ID의 해당차수에 업데이트
                    ydUtils.putLogNew(szSessionName, szMethodName, szMethodName + "::    [3] 설비 휴지테이블에 UPDATE 처리", YdConstant.DEBUG, logId);

                    setCrnschRecord = JDTORecordFactory.getInstance().create();
                    setCrnschRecord.setField("YD_EQP_ID"              , szYD_EQP_ID);              // 설비ID
                    setCrnschRecord.setField("MODIFIER"               , strRcvTcCode);             // 수정자
                    setCrnschRecord.setField("YD_EQP_PAUSE_OCCR_SEQ"  , szYD_EQP_PAUSE_OCCR_SEQ);  // 해당 차수
                    setCrnschRecord.setField("YD_EQP_PAUSE_CODE"      , szYD_EQP_PAUSE_CODE);      // 설비휴지코드
                    setCrnschRecord.setField("YD_EQP_PAUSE_END_DT"    , szYD_EQP_TRBL_RCVR_DT);    // 야드설비휴지종료일시
                    setCrnschRecord.setField("YD_EQP_PAUSE_OCC_DT"    , szYD_EQP_PAUSE_OCC_DT);    // 야드설비휴지발생일시(차를 계산하기 위함)
                    setCrnschRecord.setField("YD_EQP_PAUSE_RCVR_CNTS" , szYD_EQP_PAUSE_RCVR_CNTS); // 야드설비휴지복구내용 (일단 항목이 없음)*************************************************

                    // 설비휴지테이블 UPDATE
                    nRet = ydEqpPauseDao.updYdEqpPauseRepair(setCrnschRecord);

                    szMsg = "설비 휴지테이블 UPDATE 성공 : YD_EQP_ID(" + szYD_EQP_ID + ") YD_EQP_PAUSE_OCCR_SEQ(" + szYD_EQP_PAUSE_OCCR_SEQ + ") YD_EQP_PAUSE_CODE(" + szYD_EQP_PAUSE_CODE + ") YD_EQP_TRBL_RCVR_DT(" + szYD_EQP_TRBL_RCVR_DT + ")";
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                }
            }
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            throw new JDTOException(szMsg);
        }

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선 START
szMsg = "Y8설비고장복구실적 처리(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.02 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

        return ;
    }


    /**
     * 오퍼레이션명 : 해당 크레인 설비 스케줄 작업 상태를 IDLE 상태로 변경 [후판 제품 logId 추가 하기 위해 신규 작성]
     *           2024.09.02 기존 updCrnWrkProgStatUpWoToIdle 다른 야드도 사용 하기 때문에 후판 제품 설비고장복구 부분만
     *           logId argument 추가
     * @param  ● Stirng pYdEqpId, String logId
     * @return ● nRtnVal
     * @throws ● JDTOException
     */
    public String  updCrnWrkProgStatUpWoToIdle(String pYdEqpId, String logId ) throws JDTOException {
        // 레코드선언
        JDTORecord setRecord = null;
        JDTORecord recRecord = null;

        // 변수 선언
        String szMethodName  = "updCrnWrkProgStatUpWoToIdle";
        String szMsg         = "" ;
        String szOperationName = "설비 스케줄 작업 상태 IDLE로 변경";

        //JDTORecordSet
        JDTORecordSet rsResult     = null;

        //DAO
        YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();

        int intRtnVal = 0;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
if(ydUtils.isEmpty(logId)) logId 		= ydUtils.getLogIdNew("T"); 			// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "설비 스케줄 작업 상태 IDLE로 변경(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        try{
            szMsg = "[ " +szOperationName + "] 메소드 시작 ";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            //------------------------------------------------------------------------------------------------
            // 설비 ID 로 크레인 작업지시가 내려간 스케줄 조회
            //------------------------------------------------------------------------------------------------
            recRecord = JDTORecordFactory.getInstance().create();
            recRecord.setField("YD_EQP_ID"          , pYdEqpId);
            recRecord.setField("YD_WRK_PROG_STAT"   , YdConstant.YD_EQP_STAT_UP_WO);

            rsResult = JDTORecordFactory.getInstance().createRecordSet("rsResult");

            intRtnVal = ydCrnSchDao.getYdCrnsch(recRecord, rsResult, 46);

            if( intRtnVal < 0 ){
                szMsg = "[ " +szOperationName + "] 스케줄 조회 ERROR";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

                return YdConstant.RETN_CD_FAILURE;

            } else if (intRtnVal == 0){
                szMsg = "[ " +szOperationName + "] 변경할 데이터가 없습니다.";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                return YdConstant.RETN_CD_SUCCESS;
            }
            szMsg = "[ " +szOperationName + "] 크레인 스케줄 조회 성공!";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            //------------------------------------------------------------------------------------------------

            //------------------------------------------------------------------------------------------------
            // 크레인 스케줄 정보 IDLE 상태로 변경
            //------------------------------------------------------------------------------------------------
            recRecord = JDTORecordFactory.getInstance().create();
            setRecord = JDTORecordFactory.getInstance().create();
            rsResult.first();
            recRecord = rsResult.getRecord();

            setRecord.setField("YD_WRK_PROG_STAT", YdConstant.YD_EQP_STAT_IDLE);
            setRecord.setField("MODIFIER", "updCrnWrk");
            setRecord.setField("YD_CRN_SCH_ID", recRecord.getField("YD_CRN_SCH_ID"));

            intRtnVal = ydCrnSchDao.updYdCrnsch(setRecord, 0);

            if(intRtnVal < 0 ){

                szMsg = "[ " +szOperationName + "] UPDATE ERROR";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return YdConstant.RETN_CD_FAILURE;

            } else if (intRtnVal == 0 ){
                szMsg = "[ " +szOperationName + "] UPDATE 할 스케줄이 없습니다.";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                return YdConstant.RETN_CD_SUCCESS;
            }

            szMsg = "[ " +szOperationName + "] 스케줄 ID : " + recRecord.getFieldString("YD_CRN_SCH_ID") + "정보를 변경하였습니다.";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            //------------------------------------------------------------------------------------------------

            szMsg = "[ " +szOperationName + "] 메소드 끝 ";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return YdConstant.RETN_CD_FAILURE;
        }

////////////////////////////////////////////////////////////////////////////////////////
//2024.09.02 로그 개선  START
szMsg = "설비 스케줄 작업 상태 IDLE로 변경(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
//2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        
        return YdConstant.RETN_CD_SUCCESS;
    }

} // end of class EqpTrackingSeEJBBean
