/**
 * @(#)RcptWrkDmdSeEJBBean.java
 *
 * @version         1.0
 * @author          현대제철
 * @date            2011/07/13
 *
 * @description     이클래스는 입고작업요구 Session EJB 클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2011/07/13                    최초 등록
 * V1.01  2013/03/28   조병기       조병기       procP2BookOutReq 수정
 *                                      : for 문안에 update 문 for 문 없이 멀티 update 문으로 수정
 *                                      procY8BookOutReq 추가
 * V1.02  2013/04/02      조병기       조병기   procY8RcptZoneMtlInfo 추가 
 *
 */

package com.inisteel.cim.yd.ydWkReq.ReceiptWkReq;

import java.util.ArrayList;

import javax.ejb.EJBException;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import websvc.web.CommonUtils;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ptOsCommDao.PtOsCommDao;
import com.inisteel.cim.yd.common.dao.ptPlateCommDao.PtPlateCommDao;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSpecDao.YdCrnSpecDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydTcarFtmvMtlDao.YdTcarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.rule.GetBreRule6;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdCodeMapping;
import com.inisteel.cim.yd.ydStock.StockSpecReg.StockSpecRegSeEJBBean;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yd.common.dao.ydPrepSchDao.YdPrepSchDao;
import com.inisteel.cim.yd.common.dao.ymEtcDao.YmEtcDao;
import com.inisteel.cim.yd.common.util.loc.YdStkLocComparator;
import com.inisteel.cim.yd.common.util.loc.YdStkLocVO;
import com.inisteel.cim.yd.common.util.loc.YdToLocDcsnUtil;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.common.util.tcconst.MakeTcY8;
import com.inisteel.cim.yd.common.util.tcconst.MakeTcY9;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO;

import edu.emory.mathcs.backport.java.util.Collections;


/**
 * 입고작업요구 Session EJB
 *
 * @ejb.bean name="RcptWrkDmdSeEJB" jndi-name="RcptWrkDmdSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class RcptWrkDmdSeEJBBean extends BaseSessionBean {

    // Session Name
    private String szSessionName=getClass().getName();

    private YdUtils ydUtils =new YdUtils();

    private YdTcConst ydTcConst =new YdTcConst();

    private YdDaoUtils ydDaoUtils = new YdDaoUtils();

    private YdPICommDAO   ydPICommDAO   = new YdPICommDAO();

    private StockSpecRegSeEJBBean stock  = new StockSpecRegSeEJBBean();

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
     * 오퍼레이션명 : C연주정정L2 OHC Take-Out요구 (C3YDL003, C7YDL003)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  msgRecord
     * @return
     * @throws JDTOException
     */
    public void procC3OhcTakeOutReq(JDTORecord msgRecord)throws JDTOException {
        //적치단 DAO
        YdStkLyrDao     ydStkLyrDao     = new YdStkLyrDao();
        //저장품 DAO
        YdStockDao      ydStockDao      = new YdStockDao();
        //작업예약
        YdWrkbookDao    ydWrkbookDao    = new YdWrkbookDao();
        //작업예약재료
        YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
        //공용 DAO METHOD
        YdDaoUtils      ydDaoUtils      = new YdDaoUtils();
        //DELEGATE
        YdDelegate      ydDelegate      = new YdDelegate();
        //공용 METHOD
        YdUtils         ydutils         = new YdUtils();

        //리턴값(int)
        int intRtnVal          = 0;
        //리턴값(boolean)
        boolean blnRtnVal      = false;
        //메세지
        String szMsg           = "";
        //METHOD명
        String szMethodName    = "procC3OhcTakeOutReq";
        //사용자
        String szUser          = "SYSTEM";
        String szOperationName = "C연주정정L2 OHC Take-Out요구";

        //레코드 선언
        JDTORecord    recPara  = null;
        //레코드셋 선언
        JDTORecordSet rsResult = null;

        //설비ID(열구분과 동일)
        String szYD_EQP_ID           = "";
        //적치BED번호
        String szYD_STK_BED_NO       = "";
        //TAKE_OUT 재료번호
        String szSTL_NO              = "";
        //비상적재위치
        String szYD_EMG_STK_LOC      = "";
        //적치열구분
        String szYD_STK_COL_GP       = "";
        //적치단번호
        String szYD_STK_LYR_NO       = "";
        //스케줄코드
        String szYD_SCH_CD           = "";
        //스케줄금지유무
        String szYD_SCH_PROH_EXN     = "";
        //작업크레인
        String szYD_WRK_CRN          = "";
        //작업크레인우선순위
        String szYD_WRK_CRN_PRIOR    = "";
        //대체크레인유무
        String szYD_ALT_CRN_YN       = "";
        //대체크레인
        String szYD_ALT_CRN          = "";
        //대체크레인우선순위
        String szYD_ALT_CRN_PRIOR    = "";
        //작업예약ID
        String szYD_WBOOK_ID         = "";
        //크레인
        String szCrn                 = "";
        //스케줄우선순위
        String szYD_SCH_PRIOR        = "";
        //야드구분
        String szYD_GP               = "";
        //동구분
        String szYD_BAY_GP           = "";
        //목표야드
        String szYD_AIM_YD_GP        = "";
        //목표동
        String szYD_AIM_BAY_GP       = "";

        ydutils.displayRecord(szOperationName, msgRecord);

        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(msgRecord);

        //에러 리턴
        if (szRcvTcCode == null) {
            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }
        //TC CODE DISPLAY
        if (bDebugFlag) {
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        try{
            //=============================================================
            // Log 테이블 등록
            //=============================================================
            szMsg = "[C연주정정L2] OHC Take-Out요구 수신";
            ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            //받은 전문 편집
            //설비ID
            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            if (szYD_EQP_ID.equals("")) {

                szMsg = "[전문 이상] 설비ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            szMsg = "[1] 설비ID : " + szYD_EQP_ID;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, 3);

            //적치Bed번호
            szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
            if (szYD_STK_BED_NO.equals("")) {
                szMsg = "[전문 이상] 적치Bed번호가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            szMsg = "[2] 적치Bed번호 : " + szYD_STK_BED_NO;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, 3);

            //TAKE_OUT 재료번호
            szSTL_NO = (ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO")).trim();
            if (szSTL_NO.equals("")) {
                szMsg = "[전문 이상] TAKE_OUT 재료번호가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }
            szMsg = "[3] TAKE_OUT 재료번호 : " + szSTL_NO;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, 3);

            // 비상적재위치
            szYD_EMG_STK_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EMG_STK_LOC");
            if(szYD_EMG_STK_LOC.equals("")) {
                szMsg = "[전문 이상] 비상적재위치가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }
            szMsg = "[4] 비상적재위치 : " + szYD_EMG_STK_LOC;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, 3);

            //저장품 테이블에 해당 재료번호의 데이터가 있는지 체크한다.
            blnRtnVal = this.chkStock(szSTL_NO);
            if (!blnRtnVal) return;

            //저장품을 조회하여 목표동 및 목표야드값을 가져온다.
            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("STL_NO",        szSTL_NO);
            //결과레코드셋
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 0);
            if(intRtnVal <= 0) {
                szMsg = "<procC3OhcTakeOutReq> Error!! 해당 저장품에 대한 정보가 없습니다." + szSTL_NO;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }
            rsResult.absolute(1);
            recPara = JDTORecordFactory.getInstance().create();
            recPara.setRecord(rsResult.getRecord());
            szYD_AIM_YD_GP  = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
            szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");

            szYD_STK_COL_GP = szYD_EQP_ID;
            szYD_STK_LYR_NO = "001";

            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
            recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
            recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
            recPara.setField("STL_NO",        szSTL_NO);
            recPara.setField("YD_STK_LYR_MTL_STAT", "C");

            intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);
            if(intRtnVal <= 0){
                szMsg = "C3 OHC Take-Out요구중 R/T에 재료 등록시 ERROR!!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            //작업예약및 예약재료 등록!!
            szYD_SCH_CD = szYD_STK_COL_GP + "LM";

            //스케줄 기준 체크
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
            if (!blnRtnVal) {
                //return;
                throw new EJBException("스케줄 기준 체크 에러");
            }

            //레코드 추출
            rsResult.first();
            recPara = JDTORecordFactory.getInstance().create();
            recPara.setRecord(rsResult.getRecord());

            //스케줄 금지 유무
            szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
            //작업크레인
            szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
            //작업크레인우선순위
            szYD_WRK_CRN_PRIOR     = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
            //대체크레인유무
            szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
            //대체크레인
            szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
            //대체크레인우선순위
            szYD_ALT_CRN_PRIOR     = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");

            szMsg = "스케줄기준항목";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
            if (szYD_SCH_PROH_EXN.equals("Y")) {
                szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                //return;
                throw new EJBException(szMsg);
            }

            //작업크레인 설비 상태 체크
            blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

            //작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
            if (!blnRtnVal) {
                szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                //대체크레인의 유무를 체크한다.
                //대체크레인이 없으면 에러 리턴
                if (!szYD_ALT_CRN_YN.equals("Y")) {

                    szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    //return;
                    throw new EJBException(szMsg);
                }
                //대체크레인이 있으면 대체크레인 설비 상태 체크
                blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
                //대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
                if (!blnRtnVal) {

                    szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    //return;
                    throw new EJBException(szMsg);
                } else {
                    //대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
                    szCrn = szYD_ALT_CRN;
                    szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
                }
            } else {
                //작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
                szCrn = szYD_WRK_CRN;
                szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
            }

            //다른 작업예약에 재료가 등록되어있는지 체크한다.
            blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO);
            if (!blnRtnVal) {
                //return;
                throw new EJBException("다른 작업예약에 재료가 등록되어 있음");
            }

            //리턴 recordSet 생성
            rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
            //작업예약ID 생성
            blnRtnVal = getYdWbookId(rsResult);
            if (!blnRtnVal) {
                //return;
                throw new EJBException("작업예약ID 생성 에러");
            }
            //레코드추출
            rsResult.first();
            recPara = rsResult.getRecord();
            //작업예약ID
            szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

            //야드구분
            szYD_GP       = szYD_SCH_CD.substring(0, 1);
            //동구분
            szYD_BAY_GP   = szYD_SCH_CD.substring(1, 2);

            //INSERT 항목 RECORD 생성
            recPara = JDTORecordFactory.getInstance().create();

            //INSERT할 항목 SET
            recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
            recPara.setField("YD_GP",       szYD_GP);
            recPara.setField("YD_BAY_GP",   szYD_BAY_GP);
            recPara.setField("YD_SCH_PRIOR",    szYD_SCH_PRIOR);
            recPara.setField("YD_SCH_CD",   szYD_SCH_CD);
            recPara.setField("YD_AIM_YD_GP",    szYD_AIM_YD_GP);
            recPara.setField("YD_AIM_BAY_GP",   szYD_AIM_BAY_GP);
            recPara.setField("YD_TO_LOC_DCSN_MTD",  "F");
            recPara.setField("YD_TO_LOC_GUIDE",     szYD_EMG_STK_LOC);
            recPara.setField("REGISTER",    szUser);

            //작업예약 INSERT
            intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
            if (intRtnVal < 1) {
                szMsg = "작업예약 데이터 등록 중 에러";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                //return;
                throw new EJBException(szMsg);
            }

            //INSERT 항목 record 생성
            recPara = JDTORecordFactory.getInstance().create();

            //작업예약재료 정보 SET
            recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
            recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
            recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
            recPara.setField("REGISTER",      szUser);
            recPara.setField("STL_NO",        szSTL_NO);
            recPara.setField("YD_STK_LYR_NO", "001");
            recPara.setField("YD_UP_COLL_SEQ", "1");

            //작업예약재료 테이블에 등록한다.
            intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

            if (intRtnVal < 1) {
                szMsg = "작업예약재료 데이터 등록 중 에러";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                //return;
                throw new EJBException(szMsg);
            }

            //======================================================
            // 저장품제원 : 연주슬라브야드L2 로 송신(YDY1L002)
            //======================================================
            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("MSG_ID"         , "YDY1L002");
            recPara.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
            recPara.setField("STL_NO"         , szSTL_NO);
            recPara.setField("YD_STK_COL_GP"  , szYD_STK_COL_GP);
            recPara.setField("YD_STK_BED_NO"  , szYD_STK_BED_NO);
            ydDelegate.sendMsg(recPara);

            //C연주크레인스케줄Main 호출
            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("JMS_TC_CD", "YDYDJ500");
            recPara.setField("YD_SCH_CD", szYD_SCH_CD);
            recPara.setField("YD_EQP_ID", szCrn);
            ydDelegate.sendMsg(recPara);

        } catch (Exception e) {

            szMsg = "C3 OHC Take-Out요구중 ERROR : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        } //end try catch
    } //end of procC3OhcTakeOutReq()


    /**
     * 오퍼레이션명 : C연주정정L2 TAKE_OUT완료 (C3YDL004, C7YDL004)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  msgRecord
     * @return
     * @throws JDTOException
     */
    public void procC3TakeOutCmpl(JDTORecord msgRecord)throws JDTOException {
        // DAO객체 및 UTIL 생성
        YdStkLyrDao ydStkLyrDao      = new YdStkLyrDao();
        YdDaoUtils ydDaoUtils        = new YdDaoUtils();
        YdDelegate ydDelegate        = new YdDelegate();
        YdStockDao ydStockDao        = new YdStockDao();
        YdCodeMapping ydCodeMapping  = new YdCodeMapping();

        // 레코드 선언
        JDTORecord recPara           = null;
        JDTORecordSet rsResult       = null;
        JDTORecord recGetVal         = null;
        JDTORecord outRecTemp        = null;

        // 변수 선언
        String szMethodName          = "procC3TakeOutCmpl";
        String szMsg                 = "";
        String szUser                = "SYSTEM";
        String szYD_EQP_ID           = null;
        String szYD_STK_BED_NO       = null;
        String szCARRY_OUT_REQ_GP    = null;
        String szTAKE_OUT_STL_NO     = null;
        String szYD_STK_BED_STL_SH   = null;
        String szYD_CARRY_OUT_SH     = "";
        String [] szSTL_NO           = new String[6];
        String szYD_STK_LYR_MTL_STAT = null;
        String szDate                = null;
        String szUPD_YD_STK_COL_GP   = "";
        String szUPD_YD_STK_BED_NO   = "";
        String szYD_AIM_RT_GP        = "";

        int intMtlCnt                = 0;
        int intRtnVal                = 0;
        boolean blnRtnVal            = false;

        // TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode == null) {
            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }
        //TC CODE DISPLAY
        if(bDebugFlag) {
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        try{
            //=============================================================
            // Log 테이블 등록
            //=============================================================
            szMsg = "[C연주정정L2] TAKE_OUT완료 수신";
            ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            // [1] 설비ID
            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            if(szYD_EQP_ID.equals("")) {
                szMsg = "[전문 이상] 설비ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//              return;
            }

            szMsg = "[1] 설비ID : " + szYD_EQP_ID;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            // [2] CARRY_OUT 요구 구분
            szCARRY_OUT_REQ_GP = ydDaoUtils.paraRecChkNull(msgRecord, "CARRY_OUT_REQ_GP");
            if(szCARRY_OUT_REQ_GP.equals("")) {
                szMsg = "[전문 이상] Carry-Out요구구분가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            szMsg = "[2] Carry-Out요구구분 : " + szCARRY_OUT_REQ_GP;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            // [3] 적치Bed번호
            szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
            if(szYD_STK_BED_NO.equals("")) {

                szMsg = "[전문 이상] 적치Bed번호가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            szMsg = "[3] 적치Bed번호 : " + szYD_STK_BED_NO;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //적치Bed번호
            szYD_CARRY_OUT_SH = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARRY_OUT_SH");
            if(szYD_CARRY_OUT_SH.equals("")) {

                szMsg = "[전문 이상] Carry-Out매수가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                if(szCARRY_OUT_REQ_GP.equals("Y")){
                    szMsg = "Carry-Out요구를 하였으나 Carry-Out매수가 없습니다. Error!! 종료처리!!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }
            }

            szMsg = "[4] Carry-Out매수 : " + szYD_CARRY_OUT_SH;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            // [4] TAKE_OUT 재료번호
            szTAKE_OUT_STL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO");

            // [5] 재료 매수
            szYD_STK_BED_STL_SH = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_STL_SH");
            szMsg = "[5] 재료매수 : " + szYD_STK_BED_STL_SH;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            intMtlCnt = Integer.parseInt(szYD_STK_BED_STL_SH);

            // [6] Carry-Out매수
            szYD_CARRY_OUT_SH = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARRY_OUT_SH");
            szMsg = "[6] Carry-Out매수 : " + szYD_CARRY_OUT_SH;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            // [7] 재료번호 (1..5)
            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
                szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO" + Loop_i);

                // LOG
                szMsg = "[" + (Loop_i+6)+ "] 재료번호" + Loop_i + " : " + szSTL_NO[Loop_i];
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                //20090924 김진욱 수정 : TAKE-OUT할때 L2에서 보내는 트래킹정보로 적치단에 모두 재등록한다.
                //중간에 전문이 한번 새고 들어오거나 했을 경우를 대비 ex) 1번 take-out(o), 2번 take-out(o), 3번 take-out(x), 4번 take-out(o)
                //                                            일 경우 3번에 대해 전문이 안와서 등록을 못하더라도 4번째에 정보등록하면서 3번도 재등록한다.
                // 적치단 테이블 업데이트
                // 적치단 재료상태가 적치 가능이면 재료 등록
                // 적치열구분 = 설비ID
                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("YD_STK_COL_GP",       szYD_EQP_ID);
                recPara.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);
                recPara.setField("YD_STK_LYR_NO",       "00" + Loop_i);
                recPara.setField("MODIFIER",            szUser);
                recPara.setField("YD_STK_LYR_MTL_STAT", "C");
                recPara.setField("STL_NO",              szSTL_NO[Loop_i]);
                intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);
                if(intRtnVal == 1) {
                    szMsg = "적치단 Update 성공!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                } else {
                    szMsg = "적치단 Update 실패!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }

                szMsg = "[맵핑 처리1] 저장품 조회 Index(" + Loop_i + ") STL_NO(" + szSTL_NO[Loop_i] + ")";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                // 저장품 조회 후 존재 시  재료번호로  코드매핑 후 저장품 저장
                recPara = JDTORecordFactory.getInstance().create();
                rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                recPara.setField("STL_NO",  szSTL_NO[Loop_i]);
                intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 0);
                if(intRtnVal <=0 ){
                    if(intRtnVal == 0){
                        szMsg= "YDSTOCK[저장품] SELECT Error :: STL_NO(" + szSTL_NO[Loop_i] + ") DO NOT EXIST";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        continue ;
                    }else{
                        szMsg= "YDSTOCK[저장품] SELECT Error :: [" + intRtnVal + "] STL_NO(" + szSTL_NO[Loop_i] + ") PARAMETER ERROR" ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        continue ;
                    }
                }

                outRecTemp = JDTORecordFactory.getInstance().create();
                rsResult.absolute(1);
                outRecTemp = rsResult.getRecord();

                recPara.setField("REFUR_CHG_LOT_NO"   , ydDaoUtils.paraRecChkNull(outRecTemp, "REFUR_CHG_LOT_NO"));    // 가열로장입Lot번호
                recPara.setField("REFUR_CHG_PLN_SERNO", ydDaoUtils.paraRecChkNull(outRecTemp, "REFUR_CHG_PLN_SERNO")); // 가열로장입예정일련번호
                recPara.setField("SLAB_WO_RT_CD"      , ydDaoUtils.paraRecChkNull(outRecTemp, "SLAB_WO_RT_CD"));
                recPara.setField("YD_STK_LOT_CD"      , ydDaoUtils.paraRecChkNull(outRecTemp, "YD_STK_LOT_CD"));

                szMsg = "[맵핑 처리2] 코드맵핑";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                //=============================================================================================
                // 2009.09.15
                // 권오창
                // 코드 매핑값 호출
                //
                //     * 앞에서 코드매핑값에 대해 어떤처리를 하든지 다시 엎어침...
                //       나중에 앞에서 아래코드에 대해 처리하는 부분 삭제해야 됨
                //=============================================================================================
                outRecTemp = JDTORecordFactory.getInstance().create();
                intRtnVal = ydCodeMapping.MakeCodeMapping(szRcvTcCode, szSTL_NO[Loop_i], msgRecord, outRecTemp);
                if(intRtnVal <= 0){
                    String szTempSTL_APPEAR_GP =  ydDaoUtils.paraRecChkNull(outRecTemp, "STL_APPEAR_GP");
                    if(!szTempSTL_APPEAR_GP.trim().equals("")){
                        recPara.setField("STL_APPEAR_GP", szTempSTL_APPEAR_GP);
                    }

                    String szTempSCARFING_YN =  ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_YN");
                    if(!szTempSCARFING_YN.trim().equals("")){
                        recPara.setField("SCARFING_YN", szTempSCARFING_YN);
                    }

                    String szSCARFING_DONE_YN =  ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_DONE_YN");
                    if(!szSCARFING_DONE_YN.trim().equals("")){
                        recPara.setField("SCARFING_DONE_YN", szSCARFING_DONE_YN);
                    }

                    szMsg = "[nRet " + intRtnVal + "] 매핑되는 코드가 없습니다. 재료외형, 스카핑여부, 스카핑 완료여부는 업데이트를 위함 STL_APPEAR_GP(" + szTempSTL_APPEAR_GP + ") SCARFING_YN(" + szTempSCARFING_YN + ") SCARFING_DONE_YN(" + szSCARFING_DONE_YN + ")";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else {
                    szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_RT_GP");
                    if(!szYD_AIM_RT_GP.equals("")){
                        recPara.setField("YD_AIM_RT_GP"    , szYD_AIM_RT_GP);
                    }

                    String szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "STL_APPEAR_GP");
                    if(!szSTL_APPEAR_GP.equals("")){
                        recPara.setField("STL_APPEAR_GP"   , szSTL_APPEAR_GP);
                    }

                    // NULL이 아니고 또한 공백도 아니면 항목이 있는 값이기 때문에 목표행선에서 재료진도코드를 뽑아온다.
                    if(szYD_AIM_RT_GP != null && !szYD_AIM_RT_GP.equals("")){
                        recPara.setField("STL_PROG_CD", szYD_AIM_RT_GP.substring(0, 1));
                    }

                    String szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_YD_GP");
                    if(!szYD_AIM_YD_GP.equals("")){
                        recPara.setField("YD_AIM_YD_GP"    , szYD_AIM_YD_GP);
                    }

                    String szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_BAY_GP");
                    if(!szYD_AIM_BAY_GP.equals("")){
                        recPara.setField("YD_AIM_BAY_GP"   , szYD_AIM_BAY_GP);
                    }

                    String szSCARFING_YN = ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_YN");
                    if(!szSCARFING_YN.equals("")){
                        recPara.setField("SCARFING_YN"   , szSCARFING_YN);
                    }

                    String szSCARFING_DONE_YN = ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_DONE_YN");
                    if(!szSCARFING_DONE_YN.equals("")){
                        recPara.setField("SCARFING_DONE_YN"   , szSCARFING_DONE_YN);
                    }
                }

                stock.setYdStkLocTpCd(recPara);

                szMsg = "[맵핑 처리3] 저장품 업데이트";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                intRtnVal = ydStockDao.updYdStock(recPara, 0);
                if(intRtnVal <= 0){
                    szMsg= "YD_STOCK[Take-Out] UPDATE Error :: [" + intRtnVal + "] STL_NO(" + szSTL_NO[Loop_i] + ")";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    continue;
                }

                szMsg = "Take-Out 처리 시 코드 맵핑 후 저장품 업데이트 성공 STL_NO(" + szSTL_NO[Loop_i] + ")";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            }


            if(szTAKE_OUT_STL_NO.trim().equals("")) {

                szMsg = "[전문 이상] TAKE_OUT 재료번호가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//              return;

                szTAKE_OUT_STL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO" + intMtlCnt);

            }else{

                //저장품 테이블에 해당 재료번호의 데이터가 있는지 체크한다.
                blnRtnVal = this.chkStock(szTAKE_OUT_STL_NO);
                if(!blnRtnVal) return;

                //조회결과 recordSet 생성
                rsResult = JDTORecordFactory.getInstance().createRecordSet("");


                //적치단정보 조회
                blnRtnVal = this.chkGetStkLyr(szYD_EQP_ID, szYD_STK_BED_NO, "00" + intMtlCnt, rsResult);
                if(!blnRtnVal) return;

                //적치단정보 레코드 추출
                recPara = null;
                rsResult.first();
                recPara = rsResult.getRecord();

                //적치단 재료상태
                szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT");

//              //적치단 재료상태가 적치가능('E')가 아니면 에러 리턴
//              if(!szYD_STK_LYR_MTL_STAT.equals("E")) {
//                  szMsg = "적치단 재료상태(" + szYD_STK_LYR_MTL_STAT + ")!";
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
////                    return ;
//              }

                // 적치단 테이블 업데이트
                // 적치단 재료상태가 적치 가능이면 재료 등록
                // 적치열구분 = 설비ID
                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("YD_STK_COL_GP",       szYD_EQP_ID);
                recPara.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);
                recPara.setField("YD_STK_LYR_NO",       "00" + intMtlCnt);
                recPara.setField("MODIFIER",            szUser);
                recPara.setField("YD_STK_LYR_MTL_STAT", "C");
                recPara.setField("STL_NO",              szTAKE_OUT_STL_NO);
                intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);
                if(intRtnVal == 1) {
                    szMsg = "적치단 Update 성공!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, 3);
                } else {
                    szMsg = "적치단 Update 실패!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }
            }

            // CARRY_OUT 요구 구분항목이 "Y"이면 CARRY_OUT 요구 전문 송신
            // CARRY_OUT 요구 구분항목이 "Y"이고 설비ID가 "ABSB01"이고 "09"번 BED에 재료번호값이 있다면 요구전문 송신
            // 쉽게 따로따로 비교했음
            if(szYD_EQP_ID.trim().equals("ABSB01")){
                //if(szCARRY_OUT_REQ_GP.equals("Y") && szYD_STK_BED_NO.equals("09") && !szTAKE_OUT_STL_NO.trim().equals("")){
                if(szCARRY_OUT_REQ_GP.equals("Y") && !szTAKE_OUT_STL_NO.trim().equals("")){
                    //전문 발생 일시
                    szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
                    //큐전송 항목 저장 레코드 생성
                    recPara = JDTORecordFactory.getInstance().create();
                    //JMS TC CODE
                    recPara.setField("JMS_TC_CD",          "YDYDJ201");
                    //발생 일시
                    recPara.setField("JMS_TC_CREATE_DDTT", szDate);
                    //설비ID
                    recPara.setField("YD_EQP_ID",          szYD_EQP_ID);
                    //적치BED번호
                    recPara.setField("YD_STK_BED_NO",      szYD_STK_BED_NO);
                    //Take-Out 재료번호
                    recPara.setField("TAKE_OUT_STL_NO",    szTAKE_OUT_STL_NO);
                    //적치재료매수
                    recPara.setField("YD_STK_BED_STL_SH",  szYD_STK_BED_STL_SH);
                    //CARRY-OUT매수
                    recPara.setField("YD_CARRY_OUT_SH",    szYD_CARRY_OUT_SH);
                    //재료번호
                    for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
                        recPara.setField("STL_NO" + Loop_i, szSTL_NO[Loop_i]);
                    }

                    //전문 송신
                    ydDelegate.sendMsg(recPara);
                    szMsg = "C연주 TAKE_OUT 처리 완료후 CARRY_OUT 송신 완료!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                }
            } else if(szCARRY_OUT_REQ_GP.equals("Y")) {
                //전문 발생 일시
                szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
                //큐전송 항목 저장 레코드 생성
                recPara = JDTORecordFactory.getInstance().create();
                //JMS TC CODE
                recPara.setField("JMS_TC_CD",          "YDYDJ201");
                //발생 일시
                recPara.setField("JMS_TC_CREATE_DDTT", szDate);
                //설비ID
                recPara.setField("YD_EQP_ID",          szYD_EQP_ID);
                //적치BED번호
                recPara.setField("YD_STK_BED_NO",      szYD_STK_BED_NO);
                //Take-Out 재료번호
                recPara.setField("TAKE_OUT_STL_NO",    szTAKE_OUT_STL_NO);
                //적치재료매수
                recPara.setField("YD_STK_BED_STL_SH",  szYD_STK_BED_STL_SH);
                //CARRY-OUT매수
                recPara.setField("YD_CARRY_OUT_SH",    szYD_CARRY_OUT_SH);
                //재료번호
                for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
                    recPara.setField("STL_NO" + Loop_i, szSTL_NO[Loop_i]);
                }

                //전문 송신
                ydDelegate.sendMsg(recPara);
                szMsg = "C연주 TAKE_OUT 처리 완료후 CARRY_OUT 송신 완료!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            }

        } catch (Exception e) {

            szMsg = "C연주 TAKE_OUT 처리중 ERROR : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);

        } //end try catch
    } //end of procC3TakeOutCmpl()


    /**
     * 오퍼레이션명 : A후판슬라브야드 TAKE_OUT완료수신 (Y3YDL012)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY3TakeOutCmpl(JDTORecord msgRecord)throws JDTOException  {

        //적치단 DAO
        YdStkLyrDao ydStkLyrDao    = new YdStkLyrDao();
        //공용 DAO METHOD
        YdDaoUtils ydDaoUtils      = new YdDaoUtils();
        //DELEGATE
        YdDelegate ydDelegate = new YdDelegate();
        //공용 METHOD
        YdUtils ydutils            = new YdUtils();

        //리턴값(boolean)
        boolean blnRtnVal      = false;
        //리턴값(int)
        int intRtnVal          = 0;
        //메세지
        String szMsg           = "";
        //METHOD명
        String szMethodName    = "procY3TakeOutCmpl";
        //사용자
        String szUser          = "SYSTEM";

        //레코드 선언
        JDTORecord    recPara  = null;
        //레코드셋 선언
        JDTORecordSet rsResult = null;


        //설비ID(열구분과 동일)
        String szYD_EQP_ID         = null;
        //적치BED번호
        String szYD_STK_BED_NO     = null;
        //CARRY_OUT 요구 구분
        String szCARRY_OUT_REQ_GP  = null;
        //TAKE_OUT 재료번호
        String szTAKE_OUT_STL_NO   = null;
        //재료 매수(String)
        String szYD_STK_BED_STL_SH = null;
        //재료 매수(int)
        int intMtlCnt              = 0;
        //재료번호
        String [] szSTL_NO         = new String[4];
        //전문발생일시
        String szDate              = null;

        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode == null) {
            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }
        //TC CODE DISPLAY
        if(bDebugFlag) {
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        try{
            //=============================================================
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판슬라브야드] TAKE_OUT완료수신 수신";
            ydUtils.putLogMsg("D", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            //======================================================================================================
            szMsg = "[1] 야드설비ID : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, 3);
            szMsg = "[2] Carry-Out요구구분 : " + ydDaoUtils.paraRecChkNull(msgRecord, "CARRY_OUT_REQ_GP");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, 3);
            szMsg = "[3] 야드적치Bed번호 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, 3);
            szMsg = "[4] 재료번호 : " + ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, 3);
            szMsg = "[5] 야드적치Bed재료매수 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_STL_SH");
            ydUtils.putLog(szSessionName, szMethodName, szMsg, 3);
            for(int j=0; j<4; j++){
                szMsg = "[" + (j+6) + "] 재료번호" + (j+1) + " : " + ydDaoUtils.paraRecChkNull(msgRecord, ("STL_NO" + (j+1)));
                ydUtils.putLog(szSessionName, szMethodName, szMsg, 3);
            }
            //======================================================================================================

            //받은 전문 편집
            //설비ID(열구분)
            szYD_EQP_ID         = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            if(szYD_EQP_ID.equals("")) {
                szMsg = "[전문 이상] 설비ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            //적치Bed번호
            szYD_STK_BED_NO     = ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_BED_NO");
            if(szYD_STK_BED_NO.equals("")) {
                szMsg = "[전문 이상] 적치Bed번호가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            /*
            //TAKE_OUT 재료번호
            szTAKE_OUT_STL_NO   = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO");
            if(szTAKE_OUT_STL_NO.equals("")) {
                szMsg = "[전문 이상] TAKE_OUT 재료번호가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }
            */
            //CARRY_OUT 요구 구분
            szCARRY_OUT_REQ_GP  = ydDaoUtils.paraRecChkNull(msgRecord,"CARRY_OUT_REQ_GP");

            //재료매수(String)
            szYD_STK_BED_STL_SH = ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_BED_STL_SH");
            //재료매수(int)
            intMtlCnt = Integer.parseInt(szYD_STK_BED_STL_SH);

            //재료번호
            for (int Loop_i=0; Loop_i<intMtlCnt; Loop_i++) {
                szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord, ("STL_NO" + (Loop_i+1)));
                blnRtnVal = this.chkStock(szSTL_NO[Loop_i]);
                if(!blnRtnVal) return;

                if((Loop_i+1) == intMtlCnt){
                    //저장품 테이블에 해당 재료번호의 데이터가 있는지 체크한다.
                    szTAKE_OUT_STL_NO = szSTL_NO[Loop_i];
                }
            }
            /*
            //조회결과 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //적치단정보 조회
            blnRtnVal = this.chkGetStkLyr(szYD_EQP_ID, szYD_STK_BED_NO, "00" + intMtlCnt, rsResult);
            if(!blnRtnVal) return;

            //적치단정보 레코드 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //적치단 재료상태
            String szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT");
            //적치단 재료상태가 적치가능('E')가 아니면 에러 리턴
            if(!szYD_STK_LYR_MTL_STAT.equals("E")) {

                szMsg = "적치단 재료상태(" + szYD_STK_LYR_MTL_STAT + ") 적치가능 상태가 아닙니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }
            */
            //=================================================================
            // TAKE_OUT완료수신 전문에서 온 재료번호 매수만큼 돌면서 적치단을 1단부터 업데이트
            // 만약 업데이트 중 실패나면 에러처리(그렇지 않으면 공중부양 현상이 남)
            //=================================================================
            for(int i=0; i<intMtlCnt; i++){
                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("YD_STK_COL_GP"      , szYD_EQP_ID);
                recPara.setField("YD_STK_BED_NO"      , szYD_STK_BED_NO);
                recPara.setField("YD_STK_LYR_NO"      , "00" + (i+1));
                recPara.setField("MODIFIER"           , szUser);
                recPara.setField("YD_STK_LYR_MTL_STAT", "C");
                recPara.setField("STL_NO"             , szSTL_NO[i]);
                intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);
                if(intRtnVal == 1) {
                    szMsg = "[" + i + "] STL_NO(" + szSTL_NO[i] + "적치단 Update 성공!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                } else {
                    szMsg = "[" + i + "] STL_NO(" + szSTL_NO[i] + "적치단 Update 실패! intRtnVal[" + intRtnVal + "]";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    throw new DAOException(szMethodName + szMsg);
                }

            }

            // Carry-Out 요구 구분항목이 "Y"이면 Carry-Out 요구 전문 송신
            if(szCARRY_OUT_REQ_GP.equals("Y")) {

                //전문 발생 일시
                szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
                //큐전송 항목 레코드 생성
                recPara = JDTORecordFactory.getInstance().create();
                //JMS TC CODE
                recPara.setField("JMS_TC_CD",          "YDYDJ202");
                //발생 일시
                recPara.setField("JMS_TC_CREATE_DDTT", szDate);
                //설비ID
                recPara.setField("YD_EQP_ID",          szYD_EQP_ID);
                //적치Bed번호
                recPara.setField("YD_STK_BED_NO",      szYD_STK_BED_NO);
                //Take-Out 재료번호
                recPara.setField("TAKE_OUT_STL_NO",    szTAKE_OUT_STL_NO);
                //적치Bed재료매수
                recPara.setField("YD_STK_BED_STL_SH",  szYD_STK_BED_STL_SH);

                //재료번호
                for (int Loop_i=0; Loop_i<intMtlCnt; Loop_i++) {
                    recPara.setField("STL_NO" + (Loop_i+1), szSTL_NO[Loop_i]);
                }

                szMsg = "A후판 슬라브야드 TAKE_OUT 처리 완료후 CARRY_OUT 기능 호출!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                this.procY3CarryOutReq(recPara);

            }   // end if


        } catch (Exception e) {

            szMsg = "A후판 슬라브야드 TAKE_OUT 처리중 ERROR : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMethodName + szMsg);

        }   // end try catch


    } //end of procY3TakeOutCmpl()

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
            szMsg = "설비ID(" + szEqpId + ")입니다.";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            //설비ID
            recPara.setField("YD_EQP_ID", szEqpId);

            //설비 테이블 조회
            intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult, 0);

            //리턴값 메세지처리
            if(intRtnVal > 1) {

                szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 중복되었습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == 1) {

                blnRtnVal = true;

            } else if(intRtnVal == 0) {

                szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == -2) {

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
     * 오퍼레이션명 : 적치단 유무체크 및 조회결과 데이터 반환
     *
     * @param  String        szStkColGp 적치열구분
     *         JDTORecordSet rsResult   결과레코드셋
     * @return boolean       true(성공), false(실패)
     * @throws JDTOException
     */
    public boolean chkGetStkLyr(String szStkColGp, JDTORecordSet rsResult)throws JDTOException  {

        //적치단 DAO
        YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

        String szMsg        = null;
        String szMethodName = "chkGetStkLyr";
        int intRtnVal       = 0;
        boolean blnRtnVal   = false;
        JDTORecord recPara  = null;

        try {

            //조회 항목  record 생성
            recPara = JDTORecordFactory.getInstance().create();

            //조회 파라미터 레코드 set
            recPara.setField("YD_STK_COL_GP",   szStkColGp);

            //적치단정보 조회
            intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 5);

            //리턴값 메세지처리
            if(intRtnVal >= 1) {

                blnRtnVal = true;

            } else if(intRtnVal == 0) {
                szMsg = "적치열구분("  + szStkColGp + ")" +
                        " 에 대한 적치단 데이터가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == -2) {
                szMsg = "적치열구분("  + szStkColGp + ")" +
                        "로 적치단 조회중 parameter error 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else {
                szMsg = "적치열구분("  + szStkColGp + ")" +
                        " 로 적치단 조회중 오류 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            }
        } catch(Exception e) {
            szMsg = "적치단 유무체크 및 조회결과 데이터 반환 중 Error : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            blnRtnVal = false;
        }
        return blnRtnVal;
    } //end of chkGetStkLyr


    /**
     * 오퍼레이션명 : 저장품유무체크
     *
     * @param  String  szStlNo 재료번호
     * @return boolean true(성공), false(실패)
     * @throws JDTOException
     */
    public boolean chkStock(String szStlNo)throws JDTOException  {

        //저장품 DAO
        YdStockDao ydStockDao = new YdStockDao();
        //리턴값(boolean)
        boolean blnRtnVal     = false;
        //리턴값(int)
        int intRtnVal         = 0;
        //메소드명
        String szMethodName   = "chkStock";
        String szMsg          = null;

        //레코드 선언
        JDTORecord recPara        = null;
        //레코드셋 선언
        JDTORecordSet rsResult    = null;

        try {
            //레코드 생성
            recPara  = JDTORecordFactory.getInstance().create();
            //레코드셋 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //재료번호
            recPara.setField("STL_NO", szStlNo);

            //저장품 테이블 조회
            intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 0);

            //리턴값 메세지처리
            if(intRtnVal > 1) {

                szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 중복되었습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == 1) {

                blnRtnVal = true;

            } else if(intRtnVal == 0) {

                szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == -2) {

                szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 parameter error 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else {

                szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 오류 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            }
        } catch(Exception e) {
            szMsg = "저장품유무체크 중 예외발생! 예외메세지: " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            blnRtnVal = false;
        }
        return blnRtnVal;
    } //end of chkStock


    /**
     * 오퍼레이션명 : 저장품유무체크
     *
     * @param  String  szStlNo 재료번호
     * @return boolean true(성공), false(실패)
     * @throws JDTOException
     */
    public boolean chkStock(String szStlNo, JDTORecordSet rsResult)throws JDTOException  {

        //저장품 DAO
        YdStockDao ydStockDao = new YdStockDao();
        //리턴값(boolean)
        boolean blnRtnVal     = false;
        //리턴값(int)
        int intRtnVal         = 0;
        //메소드명
        String szMethodName   = "chkStock";
        String szMsg          = null;

        //레코드 선언
        JDTORecord recPara        = null;
        //레코드셋 선언
        //JDTORecordSet rsResult    = null;

        try {
            //레코드 생성
            recPara  = JDTORecordFactory.getInstance().create();
            //레코드셋 생성
            //rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //재료번호
            recPara.setField("STL_NO", szStlNo);

            //저장품 테이블 조회
            intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 0);

            //리턴값 메세지처리
            if(intRtnVal > 1) {

                szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 중복되었습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == 1) {

                blnRtnVal = true;

            } else if(intRtnVal == 0) {

                szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == -2) {

                szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 parameter error 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else {

                szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 오류 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            }
        } catch(Exception e) {
            szMsg = "저장품유무체크 중 예외발생! 예외메세지: " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            blnRtnVal = false;
        }
        return blnRtnVal;
    } //end of chkStock


    /**
     * 오퍼레이션명 : 적치단 유무체크 및 조회결과 데이터 반환
     *
     * @param  String        szStkColGp 적치열구분
     *         String        szStkBedNo 적치BED번호
     *         String        szStkLyrNo 적치단번호
     *         JDTORecordSet rsResult   결과레코드셋
     * @return boolean       true(성공), false(실패)
     * @throws JDTOException
     */

    public boolean chkGetStkLyr(String szStkColGp, String szStkBedNo, String szStkLyrNo, JDTORecordSet rsResult)throws JDTOException  {
        //적치단 DAO
        YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

        String szMsg        = null;
        String szMethodName = "chkGetStkLyr";
        int intRtnVal       = 0;
        boolean blnRtnVal   = false;
        JDTORecord recPara  = null;

        try {

            //조회 항목  record 생성
            recPara = JDTORecordFactory.getInstance().create();

            //조회 파라미터 레코드 set
            recPara.setField("YD_STK_COL_GP",   szStkColGp);
            recPara.setField("YD_STK_BED_NO",   szStkBedNo);
            recPara.setField("YD_STK_LYR_NO",   szStkLyrNo);

            //적치단정보 조회
            intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 0);

            //리턴값 메세지처리
            if(intRtnVal > 1) {

                szMsg = "적치열구분("  + szStkColGp + ")," +
                        "적치BED번호(" + szStkBedNo + ")," +
                        "적치단번호("  + szStkLyrNo + ")" +
                        " 에 대한 적치단 데이터가 중복되었습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//              blnRtnVal = false;

            } else if(intRtnVal == 1) {

                blnRtnVal = true;

            } else if(intRtnVal == 0) {
                szMsg = "적치열구분("  + szStkColGp + ")," +
                        "적치BED번호(" + szStkBedNo + ")," +
                        "적치단번호("  + szStkLyrNo + ")" +
                        " 에 대한 적치단 데이터가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == -2) {
                szMsg = "적치열구분("  + szStkColGp + ")," +
                        "적치BED번호(" + szStkBedNo + ")," +
                        "적치단번호("  + szStkLyrNo + ")" +
                        "로 적치단 조회중 parameter error 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else {
                szMsg = "적치열구분("  + szStkColGp + ")," +
                        "적치BED번호(" + szStkBedNo + ")," +
                        "적치단번호("  + szStkLyrNo + ")" +
                        " 로 적치단 조회중 오류 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            }
        } catch(Exception e) {
            szMsg = "적치단 유무체크 및 조회결과 데이터 반환 중 Error : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            blnRtnVal = false;
        }
        return blnRtnVal;
    } //end of chkGetStkLyr


    /**
     * 오퍼레이션명 : BED 금지/해제
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void bedProhRel(JDTORecord msgRecord)throws JDTOException  {
        //적치bed DAO
        YdStkBedDao ydStkBedDao = new  YdStkBedDao();

        //파라미터 레코드 생성
        JDTORecord recPara = JDTORecordFactory.getInstance().create();

        //파라미터 string
        String szV_YD_STK_COL_GP       = null;
        String szV_YD_STK_BED_NO       = null;
        String szV_YD_STK_BED_ACT_STAT = null;
        String szV_MODIFIER            = null;

        int intRtnVal = 0;

        String szMsg="";
        String szMethodName="bedProhRel";

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

        //파라미터 null 체크
        szV_YD_STK_COL_GP       = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
        szV_YD_STK_BED_NO       = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
        szV_YD_STK_BED_ACT_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_ACT_STAT");
        szV_MODIFIER            = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");


        //파라미터 레코드 편집
        recPara.setField("YD_STK_COL_GP",       szV_YD_STK_COL_GP);
        recPara.setField("YD_STK_BED_NO",       szV_YD_STK_BED_NO);
        recPara.setField("YD_STK_BED_ACT_STAT", szV_YD_STK_BED_ACT_STAT);
        recPara.setField("MODIFIER",            szV_MODIFIER);

        //적치bedDao 업데이트 실행
        intRtnVal = ydStkBedDao.updYdStkbed(recPara, 0);

        if(intRtnVal > 0) {
            szMsg="BED 금지/해제 처리("+szMethodName+") 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        } else {
            szMsg="BED 금지/해제 처리("+szMethodName+") 실패" + " intRtnVal: " +intRtnVal;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        }

    }// end of bedProhRel()

    /**
     * 오퍼레이션명 : C연주불출구CARRY_OUT요구 (YDYDJ201)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procCCsExtSectCarryOutReq(JDTORecord msgRecord)throws JDTOException  {

        //작업예약 DAO
        YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
        //작업예약재료 DAO
        YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
        //저장품DAO
        YdStockDao ydStockDao = new YdStockDao();
        //공용 DAO METHOD
        YdDaoUtils ydDaoUtils           = new YdDaoUtils();
        //공용 METHOD
        //YdUtils ydutils                 = new YdUtils();
        //메세지 전송 delegate
        YdDelegate ydDelegate           = new YdDelegate();

        //리턴값(boolean)
        boolean blnRtnVal               = false;
        //리턴값(int)
        int intRtnVal                   = 0;
        //메세지
        String szMsg                    = "";
        //METHOD명
        String szMethodName             = "procCCsExtSectCarryOutReq";
        //사용자
        String szUser                   = "SYSTEM";

        //레코드 선언
        JDTORecord recPara              = null;
        //레코드셋 선언
        JDTORecordSet rsResult          = null;

        //설비ID(열구분)
        String szYD_EQP_ID         = null;
        //적치BED번호
        String szYD_STK_BED_NO     = null;
        //TAKE_OUT 재료번호
        String szTAKE_OUT_STL_NO   = null;
        //재료매수(String)
        String szYD_STK_BED_STL_SH = null;
        //재료매수(int)
        int intMtlCnt              = 0;
        //CARRY-OUT매수(int)
        int intCARRY_OUT_SH        = 0;
        //재료번호
        String [] szSTL_NO         = new String[6];
        //재료번호
        String [] szYD_STK_LYR_NO  = new String[6];

        //스케줄코드
        String szYD_SCH_CD         = null;
        //스케줄우선순위
        String szYD_SCH_PRIOR       = "";
        //스케줄 금지 유무
        String szYD_SCH_PROH_EXN   = null;
        //작업크레인
        String szYD_WRK_CRN        = null;
        //작업크레인우선순위
        String szYD_WRK_CRN_PRIOR        = null;
        //대체크레인
        String szYD_ALT_CRN        = null;
        //대체크레인유무
        String szYD_ALT_CRN_YN     = null;
        //대체크레인우선순위
        String szYD_ALT_CRN_PRIOR        = null;
        //선택크레인
        String szCrn               = null;
        //야드구분
        String szYD_GP             = null;
        //동구분
        String szYD_BAY_GP         = null;
        //목표야드구분
        String szYD_AIM_YD_GP             = null;
        //목표동구분
        String szYD_AIM_BAY_GP         = null;
        //작업예약ID
        String szYD_WBOOK_ID       = null;
        //CARRY-OUT매수
        String szYD_CARRY_OUT_SH   = null;

        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(msgRecord);
        //에러 리턴
        if(szRcvTcCode == null) {

            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;

        }
        //TC CODE DISPLAY
        if(bDebugFlag) {

            szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        }

        try {
            //받은 전문 편집
            //설비ID(적치열)
            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID");
            if(szYD_EQP_ID.equals("")) {

                szMsg = "[전문 이상] 야드설비ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }
            //적치BED번호
            szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_BED_NO");
            if(szYD_STK_BED_NO.equals("")) {

                szMsg = "[전문 이상] 적치BED번호가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }

            //TAKE_OUT 재료번호
            szTAKE_OUT_STL_NO   = ydDaoUtils.paraRecChkNull(msgRecord,"TAKE_OUT_STL_NO");
            if(szTAKE_OUT_STL_NO.equals("")) {

                szMsg = "[전문 이상] TAKE_OUT 재료번호가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }

            //재료매수(String)
            szYD_STK_BED_STL_SH = ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_BED_STL_SH");
            if(szYD_STK_BED_STL_SH.equals("")) {

                szMsg = "[전문 이상] 재료매수가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }

            //CARRY-OUT매수(String)
            szYD_CARRY_OUT_SH = ydDaoUtils.paraRecChkNull(msgRecord,"YD_CARRY_OUT_SH");
            if(szYD_CARRY_OUT_SH.equals("")) {

                szMsg = "[전문 이상] CARRY-OUT매수가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }

            //재료매수(int)
            intMtlCnt = Integer.parseInt(szYD_STK_BED_STL_SH);
            //CARRY-OUT매수(int)
            intCARRY_OUT_SH = Integer.parseInt(szYD_CARRY_OUT_SH);

            //재료번호
            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

                szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);

                //대상재의 목표야드와 목표동 구분을 가져온다.
                rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                if( !this.chkGetStock1(szSTL_NO[Loop_i], rsResult) ) return;
                rsResult.first();
                recPara = rsResult.getRecord();

                if(!"".equals(szYD_AIM_YD_GP))
                //목표야드
                szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
                if(!"".equals(szYD_AIM_BAY_GP))
                //목표동
                szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
                //현재 단 위치
                szYD_STK_LYR_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");

            }

            //=================================================================================
            //수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
            //BRE 등록 안됨...테스트용 스케줄코드 생성
            //추후 구현..
            //szYD_SCH_CD  = "ADBHPULM";
//          szYD_SCH_CD  = szYD_EQP_ID.substring(0,4) + "0" + szYD_EQP_ID.substring(5,6) + "LM";
            //szYD_SCH_CD  = szYD_EQP_ID.trim().substring(0, 4) + "0" + szYD_EQP_ID.trim().substring(5, 6) + "LM";
            szYD_SCH_CD  = YdCommonUtils.getSchCd(szYD_EQP_ID, szYD_STK_BED_NO, "L");
            //=================================================================================

            szMsg = "[C연주불출구 CARRY_OUT 요구] 스케줄코드 : " + szYD_SCH_CD + ", 목표야드구분 : " + szYD_AIM_YD_GP + ", 목표동구분 : " + szYD_AIM_BAY_GP;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            // 리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            //스케줄CD 체크
            blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
            if(!blnRtnVal) return;
            //레코드 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //스케줄 금지 유무
            szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
            //작업크레인
            szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
            //작업크레인우선순위
            szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
            //대체크레인유무
            szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
            //대체크레인
            szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
            // 대체크레인우선순위
            szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");

            //스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
            if(szYD_SCH_PROH_EXN.equals("Y")) {

                szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }

            //작업크레인 설비 상태 체크
            blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

            //작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
            if(!blnRtnVal) {

                szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                //대체크레인의 유무를 체크한다.
                //대체크레인이 없으면 에러 리턴
                if(!szYD_ALT_CRN_YN.equals("Y")) {

                    szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;

                }
                //대체크레인이 있으면 대체크레인 설비 상태 체크
                blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
                //대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
                if(!blnRtnVal) {

                    szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;

                } else {
                    //대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
                    szCrn = szYD_ALT_CRN;
                    szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
                }
            } else {
                //작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
                szCrn = szYD_WRK_CRN;
                szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
            }

            //재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
            //작업예약재료의 등록 여부 체크
            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

//              //크레인사양과 저장품 사양을 체크(길이,폭,중량)
//              blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
//              if(!blnRtnVal) return;

                //다른 작업예약에 재료가 등록되어있는지 체크한다.
                blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
                if(!blnRtnVal) return;

            }

            // INSERT 항목 RECORD 생성
            //리턴 recordSet 생성
            rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
            //작업예약ID 생성
            blnRtnVal = getYdWbookId(rsResult);
            if(!blnRtnVal) return;
            //레코드추출
            rsResult.first();
            recPara = rsResult.getRecord();
            //작업예약ID
            szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
            //야드구분
            szYD_GP       = szYD_SCH_CD.substring(0, 1);
            //동구분
            szYD_BAY_GP   = szYD_SCH_CD.substring(1, 2);
            //레코드 재생성
            recPara       = JDTORecordFactory.getInstance().create();

            //INSERT할 항목 SET
            recPara.setField("YD_WBOOK_ID",     szYD_WBOOK_ID);
            recPara.setField("YD_GP",           szYD_GP);
            recPara.setField("YD_BAY_GP",       szYD_BAY_GP);
            recPara.setField("YD_SCH_CD",       szYD_SCH_CD);
            recPara.setField("YD_SCH_PRIOR",    szYD_SCH_PRIOR);
            recPara.setField("YD_AIM_YD_GP",    szYD_AIM_YD_GP);
            recPara.setField("YD_AIM_BAY_GP",   szYD_AIM_BAY_GP);
            recPara.setField("REGISTER",    szUser);
            this.addYdSchPrior(recPara);

            //작업예약 INSERT
            intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
            if(intRtnVal < 1) {
                szMsg = "작업예약 데이터 등록 중 에러";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }


            //INSERT 항목 record 생성
            recPara = JDTORecordFactory.getInstance().create();



            //작업예약재료 정보 SET
            recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
            recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
            recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
            recPara.setField("REGISTER",      szUser);

            //재료 매수만큼 작업예약재료 테이블에 저장한다.
            for (int Loop_i = 1; Loop_i <= intCARRY_OUT_SH; Loop_i++) {

                recPara.setField("STL_NO",         szSTL_NO[intMtlCnt - intCARRY_OUT_SH + Loop_i]);
                recPara.setField("YD_STK_LYR_NO",  szYD_STK_LYR_NO[intMtlCnt - intCARRY_OUT_SH + Loop_i]);
                recPara.setField("YD_UP_COLL_SEQ", "" + (intCARRY_OUT_SH - (Loop_i - 1)));

                //작업예약재료 테이블에 등록한다.
                intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

                if(intRtnVal < 1) {
                    szMsg = "작업예약재료 데이터 등록 중 에러";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }
            }

            //크레인 스케줄 호출
            //스케줄코드, 설비id
            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("JMS_TC_CD",   "YDYDJ500");
            recPara.setField("YD_SCH_CD",   szYD_SCH_CD);
            recPara.setField("YD_EQP_ID",   szCrn);
            recPara.setField("CRN_SCH_INS_TYPE",    "C");

            ydDelegate.sendMsg(recPara);

        } catch(Exception e) {
            szMsg = "C연주불출구 CARRY_OUT 요구 처리중 Error : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }
    } // end of procCCsExtSectCarryOutReq()

    /**
     * 오퍼레이션명 : A후판슬라브야드CARRY_OUT요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY3CarryOutReq(JDTORecord msgRecord)throws JDTOException  {

        //스케줄기준 DAO
        YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
        //스케줄기준 DAO
        YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
        //공용 DAO METHOD
        YdDaoUtils ydDaoUtils           = new YdDaoUtils();
        //공용 METHOD
        //YdUtils ydutils                 = new YdUtils();
        //Delegate
        YdDelegate ydDelegate           = new YdDelegate();

        //리턴값(boolean)
        boolean blnRtnVal      = false;
        //리턴값(int)
        int intRtnVal          = 0;
        //메세지
        String szMsg           = "";
        //METHOD명
        String szMethodName    = "procY3CarryOutReq";
        //사용자
        String szUser          = "SYSTEM";

        //레코드 선언
        JDTORecord recPara     = null;
        //레코드셋 선언
        JDTORecordSet rsResult = null;

        //설비ID(열구분)
        String szYD_EQP_ID         = null;
        //적치BED번호
        String szYD_STK_BED_NO     = null;
        //TAKE_OUT 재료번호
        String szTAKE_OUT_STL_NO   = null;
        //재료매수(String)
        String szYD_STK_BED_STL_SH = null;
        //재료매수(int)
        int intMtlCnt              = 0;
        //재료번호
        String[] szSTL_NO          = new String[5];
        //스케줄코드
        String szYD_SCH_CD         = null;
        //스케줄우선순위
        String szYD_SCH_PRIOR       = "";
        //스케줄 금지 유무
        String szYD_SCH_PROH_EXN   = null;
        //작업크레인
        String szYD_WRK_CRN        = null;
        //작업크레인우선순위
        String szYD_WRK_CRN_PRIOR        = null;
        //대체크레인유무
        String szYD_ALT_CRN_YN     = null;
        //대체크레인
        String szYD_ALT_CRN        = null;
        //대체크레인우선순위
        String szYD_ALT_CRN_PRIOR        = null;
        //선택크레인
        String szCrn               = null;
        //작업예약ID
        String szYD_WBOOK_ID       = null;


        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(msgRecord);
        //에러 리턴
        if(szRcvTcCode == null || szRcvTcCode.equals("") ){

            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;

        }
        //TC CODE DISPLAY
        if(bDebugFlag){

            szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        }

        try {

            //받은 전문 편집
            //설비ID(적치열)
            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            if(szYD_EQP_ID.equals("")) {

                szMsg = "[전문 이상] 설비ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }
            //적치BED번호
            szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
            if(szYD_STK_BED_NO.equals("")) {

                szMsg = "[전문 이상] 적치BED번호가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }
            //적치Bed재료매수(String)
            szYD_STK_BED_STL_SH = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_STL_SH");
            if(szYD_STK_BED_STL_SH.equals("")) {

                szMsg = "[전문 이상] 적치Bed재료매수가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }
            //적치Bed재료매수(int)
            intMtlCnt = Integer.parseInt(szYD_STK_BED_STL_SH);

            // Take-Out재료번호
            szTAKE_OUT_STL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "TAKE_OUT_STL_NO");

            // 재료번호
            for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

                szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);

            }

            //=================================================================================
            //수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
            //BRE 등록 안됨...테스트용 스케줄코드 생성
            //추후 구현..
            //szYD_SCH_CD  = szYD_EQP_ID + "I" + "A";
            //szYD_SCH_CD  = szYD_EQP_ID.trim().substring(0, 4) + "0" + szYD_EQP_ID.trim().substring(5, 6) + "LM";

              szYD_SCH_CD = szYD_EQP_ID + "LM";
            //=================================================================================

            // 리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //스케줄 기준 체크
            blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
            if(!blnRtnVal) return;

            //레코드 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //스케줄CD 체크
            //스케줄 금지 유무
            szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
            //작업크레인
            szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
            // 작업크레인우선순위
            szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
            //대체크레인유무
            szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
            //대체크레인
            szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
            // 대체크레인우선순위
            szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");


            //스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
            if(szYD_SCH_PROH_EXN.equals("Y")) {

                szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            //작업크레인 설비 상태 체크
            blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

            //작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
            if(!blnRtnVal) {

                szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                //대체크레인의 유무를 체크한다.
                //대체크레인이 없으면 에러 리턴
                if(!szYD_ALT_CRN_YN.equals("Y")) {

                    szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;

                }
                //대체크레인이 있으면 대체크레인 설비 상태 체크
                blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
                //대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
                if(!blnRtnVal) {

                    szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;

                } else {
                    //대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
                    szCrn = szYD_ALT_CRN;
                    szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
                }
            } else {
                //작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
                szCrn = szYD_WRK_CRN;
                szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
            }

            //재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
            //작업예약재료 등록 여부를 체크한다.
            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

                //크레인사양과 저장품 사양을 체크(길이,폭,중량)
                blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
                if(!blnRtnVal) return;

                //다른 작업예약에 재료가 등록되어있는지 체크한다.
                blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
                if(!blnRtnVal) return;

            }

            //리턴 recordSet 생성
            rsResult  = JDTORecordFactory.getInstance().createRecordSet("");

            //작업예약ID 생성
            blnRtnVal = getYdWbookId(rsResult);
            if(!blnRtnVal) return;
            //레코드추출
            rsResult.first();
            recPara = rsResult.getRecord();
            //작업예약ID
            szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

            //INSERT 항목 record 생성
            recPara = JDTORecordFactory.getInstance().create();
            //야드구분
            String szYD_GP       = szYD_SCH_CD.substring(0, 1);
            //동구분
            String szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);

            //INSERT할 항목 SET
            recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
            recPara.setField("YD_GP",       szYD_GP);
            recPara.setField("YD_BAY_GP",   szYD_BAY_GP);
            recPara.setField("YD_SCH_CD",   szYD_SCH_CD);
            recPara.setField("YD_SCH_PRIOR",    szYD_SCH_PRIOR);
            recPara.setField("YD_AIM_YD_GP",        szYD_GP);
            recPara.setField("YD_AIM_BAY_GP",   szYD_BAY_GP);
            recPara.setField("REGISTER",    szUser);

            //작업예약 INSERT
            intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
            if(intRtnVal < 1) {
                szMsg = "작업예약 데이터 등록 중 에러";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }


            //조회항목 record 생성
            recPara = JDTORecordFactory.getInstance().create();

            recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
            recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
            recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
            recPara.setField("REGISTER",      szUser);

            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

                recPara.setField("STL_NO",        szSTL_NO[Loop_i]);
                recPara.setField("YD_STK_LYR_NO", "00" + Loop_i);
                recPara.setField("YD_UP_COLL_SEQ", "" + (intMtlCnt - (Loop_i - 1)));

                // 작업예약재료 테이블에 등록한다.
                intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

                if(intRtnVal < 1) {
                    szMsg = "작업예약재료 데이터 등록 중 에러";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }
            }

            //크레인 스케줄 호출
            //스케줄코드, 설비id
            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("JMS_TC_CD",   "YDYDJ503");
            recPara.setField("YD_SCH_CD",   szYD_SCH_CD);
            recPara.setField("YD_EQP_ID",   szCrn);
            recPara.setField("CRN_SCH_INS_TYPE",    "C");

            ydDelegate.sendMsg(recPara);

        } catch(Exception e) {
            szMsg = "A후판 슬라브야드 CARRY_OUT 요구 처리중 Error : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMethodName + szMsg);
        }

    } // end of procY3CarryOutReq()



    /**
     * 오퍼레이션명 : A후판창고야드CARRY_OUT요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY4CarryOutReq(JDTORecord msgRecord)throws JDTOException  {

        //설비 DAO
        YdEqpDao ydEqpDao = new YdEqpDao();
        //작업예약 DAO
        YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
        //작업예약재료 DAO
        YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
        //공용 DAO METHOD
        YdDaoUtils ydDaoUtils           = new YdDaoUtils();
        //공용 METHOD
        //YdUtils ydutils                 = new YdUtils();
        //Delegate
        YdDelegate ydDelegate           = new YdDelegate();

        YdPlateCommDAO commDao = new YdPlateCommDAO();
        JDTORecord recInPara  = null;

        //레코드 선언
        JDTORecord recPara     = null;
        JDTORecord recStkPara  = null;
        //레코드셋 선언
        JDTORecordSet rsResult = null;

        //리턴값(boolean)
        boolean blnRtnVal      = false;
        //리턴값(int)
        int intRtnVal          = 0;
        //메세지
        String szMsg           = "";
        //메소드명
        String szMethodName    = "procY4CarryOutReq";
        String szOperationName  = "후판제품야드BOOK_OUT작업요구";
        //사용자
        String szUser          = "SYSTEM";

        //적치열구분
        String szYD_EQP_ID         = null;
        //적치BED번호
        String szYD_STK_BED_NO     = null;
        //적치재료매수(String)
        String szYD_STK_BED_STL_SH = null;
        //적치재료매수(int)
        int intMtlCnt              = 0;
        //선택크레인
        String szCrn               = null;
        //야드구분
        String szYD_GP             = null;
        //동구분
        String szYD_BAY_GP         = null;
        //작업예약ID
        String szYD_WBOOK_ID       = null;
        //스케줄코드
        String szYD_SCH_CD         = null;
        //스케줄우선순위
        String szYD_SCH_PRIOR       = "";
        //재료번호
        String [] szSTL_NO         = new String[6];
        //권상모음순서
        String[] szYD_UP_COLL_SEQ  = new String[6];
        //야드입고예정저장위치
        String szYD_RCPT_PLN_STR_LOC = null;
        //작업예약생성시 파일링 작업지시인지를 구분하는 항목(2012.06.04 윤재광 추가)
        String szYD_SCH_ST_GP       = null;

        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(msgRecord);
        //에러 리턴
        if( szRcvTcCode == null || szRcvTcCode.equals("") ) {
            szMsg = "["+szOperationName+"] TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }
        //TC CODE DISPLAY
        if(bDebugFlag) {
            szMsg="["+szOperationName+"] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        try {

            //받은 전문 편집
            //설비ID
            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            if(szYD_EQP_ID.equals("")) {
                szMsg = "["+szOperationName+"] [전문 이상] 설비ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            //야드구분은 설비ID의 첫 번째 한 자리를 사용한다. (3기)
            szYD_GP = szYD_EQP_ID.substring(0,1);

            //적치Bed번호
            szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
            if(szYD_STK_BED_NO.equals("")) {
                szMsg = "["+szOperationName+"] [전문 이상] 적치Bed번호가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }
            //재료매수
            szYD_STK_BED_STL_SH = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_STL_SH");
            if(szYD_STK_BED_STL_SH.equals("")) {
                szMsg = "["+szOperationName+"] [전문 이상] 재료매수가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            szYD_RCPT_PLN_STR_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_RCPT_PLN_STR_LOC");
            szYD_SCH_ST_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_ST_GP" );

            //재료매수(int)
            intMtlCnt = Integer.parseInt(szYD_STK_BED_STL_SH);
            //재료번호
            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

                szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO" + Loop_i);
                szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_COLL_SEQ" + Loop_i);

            }

            String szYD_SPAN_NO         = "";
            String szYD_SCH_CD_SUFFIX   = "";

            String sBayGp = szYD_EQP_ID.substring(1, 2);
            String szYD_DTL_EQP_LOC_GP = szYD_EQP_ID.substring(4, 6); //상세설비(위치구분)

            if("TF".equals(szYD_EQP_ID.substring(2,4))) {
                //Transfer 의 경우는 TCTF01 이 어느 RT 의 TRANFER 인지 알아내야 한다.
                szYD_DTL_EQP_LOC_GP = YdCommonUtils.getTf2RtStkLoc(szYD_EQP_ID).substring(4, 6); //상세설비(위치구분);
            }

            String sHmiStat         = "N";
            String sWorkMode        = "*";

            String sQuery1          = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
            JDTORecord wbJr         = (new com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO()).getData(sQuery1, new Object[]{ "2XRT05" });
            if (wbJr != null){
                sHmiStat    = StringHelper.evl(wbJr.getFieldString("HMI_STAT"), "");
                sWorkMode   = StringHelper.evl(wbJr.getFieldString("WORK_MODE"), "");
            }

            if("Y".equals(sHmiStat)){
                if(!"*".equals(sWorkMode)){
                    if(sBayGp.equals(sWorkMode)){
                        sHmiStat = "Y";
                    }else{
                        sHmiStat = "N";
                    }
                }
            }

            if("Y".equals(sHmiStat)){

                szMsg = "["+szOperationName+"] 신규입고 크레인할당기능 사용";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
                recInPara = JDTORecordFactory.getInstance().create();

                recInPara.setField("YD_BAY_GP"  , sBayGp);
                recInPara.setField("RT_GP"      , szYD_DTL_EQP_LOC_GP);
                recInPara.setField("YD_LOC"     , szYD_RCPT_PLN_STR_LOC);

                intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0134");

                if(intRtnVal == 0) {

                    if( szYD_RCPT_PLN_STR_LOC.length() >= 4) {
                        szYD_SPAN_NO = szYD_RCPT_PLN_STR_LOC.substring(2, 4);

                        if("RA".equals(szYD_DTL_EQP_LOC_GP) || "RB".equals(szYD_DTL_EQP_LOC_GP) || "RC".equals(szYD_DTL_EQP_LOC_GP)) {

                            if(YdConstant.SPAN_ORDER_NEW_01.equals(szYD_SPAN_NO) ) {
                                szYD_SCH_CD_SUFFIX = "LL";
                            } else if("TCRTUT45".equals(szYD_RCPT_PLN_STR_LOC)){
                                szYD_SCH_CD_SUFFIX = "LL";
                            } else if("AP".equals(szYD_SPAN_NO)) {//#2RT B동파일링
                                szYD_SCH_CD_SUFFIX = "AP";
                            } else {
                                szYD_SCH_CD_SUFFIX = "LR";
                            }

                        } else if("RD".equals(szYD_DTL_EQP_LOC_GP)) {
                            if("XX".equals(szYD_SPAN_NO)){szYD_SPAN_NO = "05";}
                            if(Integer.valueOf(szYD_SPAN_NO).compareTo(Integer.valueOf("05")) < 0) {
                                szYD_SCH_CD_SUFFIX = "LL";
                            } else {
                                szYD_SCH_CD_SUFFIX = "LR";
                            }

                        } else if("RE".equals(szYD_DTL_EQP_LOC_GP)) {
                            if("XX".equals(szYD_SPAN_NO)){szYD_SPAN_NO = "06";}
                            if(Integer.valueOf(szYD_SPAN_NO).compareTo(Integer.valueOf("06")) < 0) {
                                szYD_SCH_CD_SUFFIX = "LL";
                            } else {
                                szYD_SCH_CD_SUFFIX = "LR";
                            }

                        } else if("RF".equals(szYD_DTL_EQP_LOC_GP)) {
                            if("XX".equals(szYD_SPAN_NO)){szYD_SPAN_NO = "07";}
                            if(Integer.valueOf(szYD_SPAN_NO).compareTo(Integer.valueOf("07")) < 0) {
                                szYD_SCH_CD_SUFFIX = "LL";
                            } else {
                                szYD_SCH_CD_SUFFIX = "LR";
                            }
                          //20251023 - 추관식 : RG추가
                        } else if("RG".equals(szYD_DTL_EQP_LOC_GP)) {
                        	if("XX".equals(szYD_SPAN_NO)){szYD_SPAN_NO = "07";} //G RT의 경우 F동으로 쓰임
                            if(Integer.valueOf(szYD_SPAN_NO).compareTo(Integer.valueOf("07")) < 0) {
                                szYD_SCH_CD_SUFFIX = "LL";
                            } else {
                                szYD_SCH_CD_SUFFIX = "LR";
                            }
                        } else if("UT".equals(szYD_DTL_EQP_LOC_GP)) {
                            szYD_SCH_CD_SUFFIX = "LM";
                        }
                    }else{
                        if("UT".equals(szYD_DTL_EQP_LOC_GP)) {
                            szYD_SCH_CD_SUFFIX = "LM";
                        }else{
                            szYD_SCH_CD_SUFFIX = "LR";
                        }
                    }

                    szMsg = "["+szOperationName+"] 검색대상이 없어 ["+szYD_SCH_CD_SUFFIX+"]를 사용";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                }else{

                    //레코드 추출
                    rsResult.first();
                    recPara = rsResult.getRecord();

                    szYD_SCH_CD_SUFFIX  = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");

                    szMsg = "["+szOperationName+"] 최종 검색대상 ["+szYD_SCH_CD_SUFFIX+"]를 사용";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                }

            }else{

                szMsg = "["+szOperationName+"] 기존입고 크레인할당기능 사용";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                // 1,2 후판 통합 크래인 스케줄 생성 로직
                if( szYD_RCPT_PLN_STR_LOC.length() >= 4) {
                    szYD_SPAN_NO = szYD_RCPT_PLN_STR_LOC.substring(2, 4);

                    if("RA".equals(szYD_DTL_EQP_LOC_GP) || "RB".equals(szYD_DTL_EQP_LOC_GP) || "RC".equals(szYD_DTL_EQP_LOC_GP)) {

                        if(YdConstant.SPAN_ORDER_NEW_01.equals(szYD_SPAN_NO) ) {
                            szYD_SCH_CD_SUFFIX = "LL";
                        } else if("TCRTUT45".equals(szYD_RCPT_PLN_STR_LOC)){
                            szYD_SCH_CD_SUFFIX = "LL";
                        } else if("AP".equals(szYD_SPAN_NO)) {//#2RT B동파일링
                            szYD_SCH_CD_SUFFIX = "AP";
                        } else {
                            szYD_SCH_CD_SUFFIX = "LR";
                        }

                    } else if("RD".equals(szYD_DTL_EQP_LOC_GP)) {
                        if("XX".equals(szYD_SPAN_NO)){szYD_SPAN_NO = "05";}
                        if(Integer.valueOf(szYD_SPAN_NO).compareTo(Integer.valueOf("05")) < 0) {
                            szYD_SCH_CD_SUFFIX = "LL";
                        } else {
                            szYD_SCH_CD_SUFFIX = "LR";
                        }

                    } else if("RE".equals(szYD_DTL_EQP_LOC_GP)) {
                        if("XX".equals(szYD_SPAN_NO)){szYD_SPAN_NO = "06";}
                        if(Integer.valueOf(szYD_SPAN_NO).compareTo(Integer.valueOf("06")) < 0) {
                            szYD_SCH_CD_SUFFIX = "LL";
                        } else {
                            szYD_SCH_CD_SUFFIX = "LR";
                        }

                    } else if("RF".equals(szYD_DTL_EQP_LOC_GP)) {
                        if("XX".equals(szYD_SPAN_NO)){szYD_SPAN_NO = "07";}
                        if(Integer.valueOf(szYD_SPAN_NO).compareTo(Integer.valueOf("07")) < 0) {
                            szYD_SCH_CD_SUFFIX = "LL";
                        } else {
                            szYD_SCH_CD_SUFFIX = "LR";
                        }
                    //20251023 - 추관식 : RG추가
                    } else if("RG".equals(szYD_DTL_EQP_LOC_GP)) {
                        if("XX".equals(szYD_SPAN_NO)){szYD_SPAN_NO = "07";} //G RT의 경우 F동으로 쓰임
                        if(Integer.valueOf(szYD_SPAN_NO).compareTo(Integer.valueOf("07")) < 0) {
                            szYD_SCH_CD_SUFFIX = "LL";
                        } else {
                            szYD_SCH_CD_SUFFIX = "LR";
                        }
                    } else if("UT".equals(szYD_DTL_EQP_LOC_GP)) {
                        szYD_SCH_CD_SUFFIX = "LM";
                    }
                }else{
                    if("UT".equals(szYD_DTL_EQP_LOC_GP)) {
                        szYD_SCH_CD_SUFFIX = "LM";
                    }else{
                        szYD_SCH_CD_SUFFIX = "LR";
                    }
                }
            }

            szYD_SCH_CD = szYD_EQP_ID.substring(0,4) + szYD_DTL_EQP_LOC_GP + szYD_SCH_CD_SUFFIX;

            szMsg = "["+szOperationName+"] 입고예정위치["+szYD_RCPT_PLN_STR_LOC+"]를 사용하여 스케줄코드 생성 - ["+szYD_SCH_CD+"]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


            //-- 통합 크레인 스케줄 조회 -----------------------------------------------------------------start--
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            recInPara = JDTORecordFactory.getInstance().create();

            recInPara.setField("YD_SCH_CD", szYD_SCH_CD);

            intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0095");

            if(intRtnVal == 0) {

                szMsg = "["+szOperationName+"] 통합 크레인 스케줄 코드 조회 0건 - ["+szYD_SCH_CD+"]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                return;

            }else{

                //레코드 추출
                rsResult.first();
                recPara = rsResult.getRecord();

                szCrn           = ydDaoUtils.paraRecChkNull(recPara, "WRK_CRN");
                szYD_SCH_PRIOR  = ydDaoUtils.paraRecChkNull(recPara, "CRN_PRIOR");
                //-- 통합 크레인 스케줄 조회 ------------------------------------------------------------------end---
            }

            if("".equals(szCrn)&&"TBRTRAAP".equals(szYD_SCH_CD)){

                szMsg="[TBRTRAAP]진행중인 설비ID를  크레인  스케줄정보에서 검색합니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                //------------------------------------------------------------------------------------------------
                JDTORecordSet rsResult1     = JDTORecordFactory.getInstance().createRecordSet("");
                JDTORecord    recInTemp     = JDTORecordFactory.getInstance().create();

                recInTemp.setField("YD_SCH_CD", szYD_SCH_CD);
                intRtnVal = commDao.select(recInTemp, rsResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0137");

                if(intRtnVal > 0) {
                    rsResult1.first();
                    recInTemp = rsResult1.getRecord();

                    szCrn                   = ydDaoUtils.paraRecChkNull(recInTemp, "YD_EQP_ID");
                    szYD_SCH_PRIOR          = "1";

                }else{

                    szMsg = "["+szOperationName+"] 통합 크레인 스케줄 코드 조회 0건 - ["+szYD_SCH_CD+"]";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    return;
                }

                szMsg="[TBRTRAAP]진행중인 설비ID를  크레인  스케줄정보에서 검색합니다. szYD_WRKABLE_CRN ="+szCrn;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                //------------------------------------------------------------------------------------------------
            }

            //재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
            //작업예약재료 등록 여부를 체크한다.
            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

                //크레인사양과 저장품 사양을 체크(길이,폭,중량)
                blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
                if(!blnRtnVal) return;

                //다른 작업예약에 재료가 등록되어있는지 체크한다.
                blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
                if(!blnRtnVal) return;

            }

//          rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
//          blnRtnVal = this.chkGetStock(szSTL_NO[1], rsResult);
//          rsResult.first();
//          recPara = rsResult.getRecord();
//          szYD_RCPT_PLN_STR_LOC = ydDaoUtils.paraRecChkNull(recPara, "YD_RCPT_PLN_STR_LOC");


            //리턴 recordSet 생성
            rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
            //작업예약ID 생성
            blnRtnVal = getYdWbookId(rsResult);
            if(!blnRtnVal) return;
            //레코드추출
            rsResult.first();
            recPara = rsResult.getRecord();
            //작업예약ID
            szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

            //작업예약 테이블 INSERT할 항목 레코드 생성
            recPara       = JDTORecordFactory.getInstance().create();
            //야드구분
            szYD_GP       = szYD_SCH_CD.substring(0, 1);
            //동구분
            szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);

            /** 2025.10.27 추관식
             *  [입고분할]
             * □ 입고 분할 F R/T 
					if 4호기 작업중 & 3호기 대기  ==> 3호기가 대신함
					if 4호기가 출하작업중  ==> 3호기가 대신함
					if 입고권하위치가 3호기 금지구역이 아님
					  >  T00053으로 설정해 놓았음 ===> 3호기가 대신함
			    >> 입고로 인해서 스케줄이 자동으로 생성될때 이렇게 바꿔주시면됨. 
				
				□ 입고분할 D R/T 
					if 3호기 작업중 & 4호기 대기  ==> 4호기가 대신함
					if 3호기가 출하작업중   ==> 4호기가 대신함
					if 입고예정위치가 4호기 금지구역이 아님
					  >  T00053으로 설정해 놓았음 ===> 3호기가 대신함
			    >> 입고로 인해서 스케줄이 자동으로 생성될때 이렇게 바꿔주시면됨. 
             */
			String sApplyYnPIs = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "024"); //1후판 입고 이적/분할 적용 여부
			
			if("Y".equals(sApplyYnPIs)){
                szMsg = "입고분할 F, D R/T (" + szMethodName + ") 신규모듈 호출";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                

    			if("TBCRB3".equals(szCrn) || "TBCRB4".equals(szCrn)){ //B동 크레인 3호기, 4호기 일경우
    				//취소상태 확인
    				JDTORecord crnPara  = JDTORecordFactory.getInstance().create();
    				
    				JDTORecordSet  rsCrnResult  = JDTORecordFactory.getInstance().createRecordSet("");
    				crnPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
    				boolean isCancelled;
    				
    				intRtnVal = ydWrkbookDao.getYdWrkbook(crnPara, rsCrnResult, 0); //쿼리에 취소코드 [SCH_CNCL_YN] 추가
    				if (intRtnVal < 1) {
    					isCancelled = true; // 취소거나 없거나
                        szMsg = "[권하실적처리] B동 Crane - 기존 작업예약이 존재하지 않음";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
    				} else { 
    					rsCrnResult.first();
    					crnPara = rsCrnResult.getRecord();
    					String szSchCnclYn = ydDaoUtils.paraRecChkNull(crnPara, "SCH_CNCL_YN");
    					isCancelled = "Y".equalsIgnoreCase(szSchCnclYn); //취소됐을경우 true, 살아있는 경우 false
    				}
    				
    				if(!isCancelled) { //취소가 아닐 경우
    					JDTORecord crnStatusPara  = JDTORecordFactory.getInstance().create();
    					JDTORecordSet rsCrnStatusResult  = JDTORecordFactory.getInstance().createRecordSet("");
    					
    					//com.inisteel.cim.yd.common.dao.YdPlateCommDao.getCrnStatusConfirm
    					if(commDao.select(crnStatusPara, rsCrnStatusResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getCrnStatusConfirm") > 0 ){
    						//입고예정위치가 3호기 금지구역이 아닐경우
    						JDTORecord stopPara  = JDTORecordFactory.getInstance().create();
    						JDTORecordSet rsStopResult  = JDTORecordFactory.getInstance().createRecordSet("");
    						stopPara.setField("REPR_CD_GP", "T00053"); //금지구역코드
    						stopPara.setField("CD_GP", "B"); //동
    						stopPara.setField("YD_SPAN_NO", szYD_RCPT_PLN_STR_LOC.substring(2, 4)); //SPAN
    						stopPara.setField("YD_COL", szYD_RCPT_PLN_STR_LOC.substring(4, 6)); //COL
    						stopPara.setField("V_YD_DTL_EQP_LOC_GP", szYD_DTL_EQP_LOC_GP); //RT구분
    						
    						if("RF".equals(szYD_DTL_EQP_LOC_GP)){ //F R/T
    							String crn3Status = null;
    							String crn4Status = null;
    							String crn4OutYn = "N";

    							int nRows = rsCrnStatusResult.size();
    							for( int i=0; i < nRows; i++){
    								JDTORecord recMtl = rsCrnStatusResult.getRecord(i);
    								String ydEqpId = recMtl.getFieldString("YD_EQP_ID");
    								String status = recMtl.getFieldString("YD_WRK_PROG_STAT");

    								if("TBCRB3".equals(ydEqpId)){
    									crn3Status = status;
    								} else if("TBCRB4".equals(ydEqpId)){
    									crn4Status = status;
    									if("TBPT40UM".equals(recMtl.getFieldString("YD_SCH_CD"))){  //4호기 현재 출하작업 여부
    										crn4OutYn = "Y";
    									}
    								}
    							}
    							
    							//4호기 작업중(대기 상태가 아닌경우) & 3호기 대기 
    							if(crn3Status.equals("W") && !crn4Status.equals("W")){
    								szCrn = "TBCRB3";
    							} 
    							
    							//출하작업? 확인필요
    							if(crn4OutYn == "Y") {
    								szCrn = "TBCRB3";
    							}
    							
    							/** 쿼리
    							 * --com.inisteel.cim.yd.common.dao.YdPlateCommDao.getCheckRuleT00053
    							SELECT REPR_CD_GP,
    									CD_GP,-- AS DONG, 
    									ITEM,-- AS USE_YN,
    									ITEM1,-- AS CR1_BR_FROM, 
    									ITEM2,-- AS CR1_BR_TO, 
    									DTL_ITEM1, -- CR1/CR3_AL_FROM, 
    									DTL_ITEM2,--  CR1/CR3_AL_TO,
    									DTL_ITEM3,--  CR2/CR4_BR_FROM, 
    									DTL_ITEM4,--  CR2/CR4_BR_TO, 
    									DTL_ITEM5,--  CR2/CR4_AL_FROM, 
    									DTL_ITEM6,--  CR2/CR4_AL_TO,
    									DTL_ITEM7,--  CR3/CR5_BR_FROM, 
    									DTL_ITEM8,--  CR3/CR5_BR_TO, 
    									DTL_ITEM9,--  CR3/CR5_AL_FROM, 
    									DTL_ITEM10,-- CR3/CR5_AL_TO,
    									'Y' AS APP_YN
    							FROM TB_YD_RULE A 
    							WHERE REPR_CD_GP LIKE :V_REPR_CD_GP
    							AND CD_GP LIKE NVL(:V_CD_GP,'%')
    							AND ITEM = 'Y'
    							AND :V_YD_SPAN_NO||:V_YD_COL BETWEEN (CASE WHEN :V_YD_DTL_EQP_LOC_GP = 'RF' THEN ITEM1 
    																	   WHEN :V_YD_DTL_EQP_LOC_GP = 'RD' THEN DTL_ITEM3 END) AND 
    																 (CASE WHEN :V_YD_DTL_EQP_LOC_GP = 'RF' THEN ITEM2
    																	   WHEN :V_YD_DTL_EQP_LOC_GP = 'RD' THEN DTL_ITEM4 END)
    							 **/
    							if(commDao.select(stopPara, rsStopResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getCheckRuleT00053") == 0 ){ //금지구역이 아님
    								szCrn = "TBCRB3";
    							}
    						} else if("RD".equals(szYD_DTL_EQP_LOC_GP)){ //D R/T
    							String crn3Status = null;
    							String crn4Status = null;
    							String crn3OutYn = "N";
    							
    							int nRows = rsCrnStatusResult.size();
    							for( int i=0; i < nRows; i++){
    								JDTORecord recMtl = rsCrnStatusResult.getRecord(i);
    								String ydEqpId = recMtl.getFieldString("YD_EQP_ID");
    								String status = recMtl.getFieldString("YD_WRK_PROG_STAT");

    								if("TBCRB3".equals(ydEqpId)){
    									crn3Status = status;
    									if("TBPT30UM".equals(recMtl.getFieldString("YD_SCH_CD"))){  //3호기 현재 출하작업 여부
    										crn3OutYn = "Y";
    									}
    								} else if("TBCRB4".equals(ydEqpId)){
    									crn4Status = status;
    								}
    							}
    							
    							//4호기 작업중(대기 상태가 아닌경우) & 3호기 대기 
    							if(!crn3Status.equals("W") && crn4Status.equals("W")){
    								szCrn = "TBCRB4";
    							} 
    							
    							//출하작업? 확인필요
    							if(crn3OutYn == "Y") {
    								szCrn = "TBCRB4";
    							}
    							
    							/** 쿼리
    							 *  --com.inisteel.cim.yd.common.dao.YdPlateCommDao.getCheckRuleT00053
    								SELECT REPR_CD_GP,
    										CD_GP,-- AS DONG, 
    										ITEM,-- AS USE_YN,
    										ITEM1,-- AS CR1_BR_FROM, 
    										ITEM2,-- AS CR1_BR_TO, 
    										DTL_ITEM1, -- CR1/CR3_AL_FROM, 
    										DTL_ITEM2,--  CR1/CR3_AL_TO,
    										DTL_ITEM3,--  CR2/CR4_BR_FROM, 
    										DTL_ITEM4,--  CR2/CR4_BR_TO, 
    										DTL_ITEM5,--  CR2/CR4_AL_FROM, 
    										DTL_ITEM6,--  CR2/CR4_AL_TO,
    										DTL_ITEM7,--  CR3/CR5_BR_FROM, 
    										DTL_ITEM8,--  CR3/CR5_BR_TO, 
    										DTL_ITEM9,--  CR3/CR5_AL_FROM, 
    										DTL_ITEM10,-- CR3/CR5_AL_TO,
    										'Y' AS APP_YN
    								FROM TB_YD_RULE A 
    								WHERE REPR_CD_GP LIKE :V_REPR_CD_GP
    								AND CD_GP LIKE NVL(:V_CD_GP,'%')
    								AND ITEM = 'Y'
    								AND :V_YD_SPAN_NO||:V_YD_COL BETWEEN (CASE WHEN :V_YD_DTL_EQP_LOC_GP = 'RF' THEN ITEM1 
    																		   WHEN :V_YD_DTL_EQP_LOC_GP = 'RD' THEN DTL_ITEM3 END) AND 
    																	 (CASE WHEN :V_YD_DTL_EQP_LOC_GP = 'RF' THEN ITEM2
    																		   WHEN :V_YD_DTL_EQP_LOC_GP = 'RD' THEN DTL_ITEM4 END)
    							**/
    							if(commDao.select(stopPara, rsStopResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getCheckRuleT00053") == 0 ){ //금지구역이 아님
    								szCrn = "TBCRB4";
    							}
    						}
    					}
    				}
    			}
			}
			
            //INSERT할 항목 SET
            recPara.setField("YD_WBOOK_ID"          , szYD_WBOOK_ID);
            recPara.setField("YD_GP"                , szYD_GP);
            recPara.setField("YD_BAY_GP"            , szYD_BAY_GP);
            recPara.setField("YD_SCH_CD"            , szYD_SCH_CD);
            recPara.setField("YD_SCH_PRIOR"         , szYD_SCH_PRIOR);
            recPara.setField("YD_AIM_YD_GP"         , szYD_GP);
            recPara.setField("YD_AIM_BAY_GP"        , szYD_BAY_GP);
            recPara.setField("YD_TO_LOC_DCSN_MTD"   , "F");
            recPara.setField("YD_TO_LOC_GUIDE"      , szYD_RCPT_PLN_STR_LOC);
            recPara.setField("REGISTER"             , szUser);
            recPara.setField("YD_SCH_ST_GP"         , szYD_SCH_ST_GP);
            recPara.setField("YD_WRK_PLAN_TCAR"     , szCrn); //후판야드 작업크레인을 YD_WRK_PLAN_TCAR 에 저장 --2014.04.07 cbg


            String sApplyYnPI =ydPICommDAO.ApplyYnPI("",szMethodName,"APPPI222","T","*");

            if(sApplyYnPI.equals("Y")){
                EJBConnector ejbConn = new EJBConnector("default", "RcptWrkDmdSeEJB", this);
                intRtnVal=(int) ejbConn.trx("insWrkBookTx", new Class[] { JDTORecord.class }, new Object[] { recPara });
                szMsg = "intRtnVal:"+intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                if(intRtnVal < 1) {
                    szMsg = "작업예약 Table 등록 중 에러";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }	
            }
            else{
                //작업예약 INSERT
                intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);  //이부분 require_new로 분리.(별도 트랜잭션 처리 후 스케줄 기동위해).
                                                                 //현재구조에서 스케줄 기동이 require_new이므로, 작업예약 생성된거 인식못하고 스케줄 생성하러감.

                if(intRtnVal < 1) {
                    szMsg = "작업예약 Table 등록 중 에러";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }
            }




            //작업예약재료 테이블 조회 레코드 생성
            recPara = JDTORecordFactory.getInstance().create();

            recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
            //recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
            //recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
            recPara.setField("REGISTER",      szUser);

            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

                //리턴 recordSet 생성
                rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
                //재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
                blnRtnVal = YdCommonUtils.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
                if(!blnRtnVal) return;

                //레코드추출
                rsResult.first();
                recStkPara = rsResult.getRecord();

                //재료번호
                recPara.setField("STL_NO",         szSTL_NO[Loop_i]);
                //적치열구분
                recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
                //적치BED번호
                recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
                //적치단번호
                recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
                //권상모음순서
                recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

                if(sApplyYnPI.equals("Y")){
                    EJBConnector ejbConn = new EJBConnector("default", "RcptWrkDmdSeEJB", this);
                    intRtnVal=(int) ejbConn.trx("insWrkBookMtlTx", new Class[] { JDTORecord.class }, new Object[] { recPara });
                    szMsg = "intRtnVal:"+intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    if(intRtnVal < 1) {
                        szMsg = "작업예약 Table 등록 중 에러";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        return;
                    }
                }
                else{
                    // 작업예약재료 테이블에 등록한다.
                    intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara); //이부분 require_new로 분리.(별도 트랜잭션 처리 후 스케줄 기동위해).
                                                                          //현재구조에서 스케줄 기동이 require_new이므로, 작업예약 생성된거 인식못하고 스케줄 생성하러감.
                    if(intRtnVal < 1){
                        szMsg = "작업예약재료 데이터 등록 중 에러";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        return;
                    }
                }

            }

            //크레인 스케줄 호출
            //스케줄코드, 설비id

            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("JMS_TC_CD",   "YDYDJ506");
            recPara.setField("YD_SCH_CD",   szYD_SCH_CD);
            recPara.setField("YD_EQP_ID",   szCrn);
            recPara.setField("CRN_SCH_INS_TYPE",    "C");

            if(sApplyYnPI.equals("Y")){
                szMsg = "스케줄 생성 직접호출 방식 사용";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                EJBConnector ejbConn        = null;
                JDTORecord   outRecord  = JDTORecordFactory.getInstance().create();

                //ejbConn = new EJBConnector("default", "CrnSchSeEJB", this);
                //outRecord =(JDTORecord)ejbConn.trx("procY4CrnSchMain", new Class[] { JDTORecord.class}, new Object[] { recPara });
                EJBConnector ydEjbCon = new EJBConnector("default", this);
                ydEjbCon.trx("CrnSchSeEJB", "procY4CrnSchMain", recPara);
            }
            else{
            ydDelegate.sendMsg(recPara);
            }

        } catch(Exception e) {
            szMsg = "A후판 창고야드 CARRY_OUT 요구 처리중 Error : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }

    } // end of procY4CarryOutReq()


    /**
     * 오퍼레이션명 : 작업예약생성(Transaction 분리)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     * @ejb.transaction type="RequiresNew"
     */
    public int insWrkBookTx(JDTORecord rcvMsg) throws DAOException, JDTOException{
        String mthdNm  = "작업예약생성Tx분리[RcptWrkDmdSeEJB.insWrkBookTx] < " + rcvMsg.getResultMsg();
        String logId   = rcvMsg.getResultCode();

        String szMsg="";
        int intRtnVal=0;

        try{
            szMsg = szSessionName + "::" + mthdNm;
            ydUtils.putLog(szSessionName, mthdNm, szMsg, YdConstant.DEBUG);

            YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();

            intRtnVal = ydWrkbookDao.insYdWrkbook(rcvMsg);

        } catch(DAOException e){
            throw e;
        }

        return intRtnVal;
    }

    /**
     * 오퍼레이션명 : 작업예약재료생성(Transaction 분리)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     * @ejb.transaction type="RequiresNew"
     */
    public int insWrkBookMtlTx(JDTORecord rcvMsg) throws DAOException, JDTOException{
        String mthdNm  = "작업예약재료생성Tx분리[RcptWrkDmdSeEJB.insWrkBookTx] < " + rcvMsg.getResultMsg();
        String logId   = rcvMsg.getResultCode();

        String szMsg="";
        int intRtnVal=0;

        try{
            szMsg = szSessionName + "::" + mthdNm;
            ydUtils.putLog(szSessionName, mthdNm, szMsg, YdConstant.DEBUG);

            YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();

            intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(rcvMsg);

        } catch(DAOException e){
            throw e;
        }

        return intRtnVal;
    }

    /**
     * 오퍼레이션명 : C연주OHCCARRY_OUT요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procCCsOhcCarryOutReq(JDTORecord msgRecord)throws JDTOException  {
        YdDelegate ydDelegate = new YdDelegate();


        //작업예약 DAO
        YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
        //작업예약재료 DAO
        YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
        //공용 DAO METHOD
        YdDaoUtils ydDaoUtils           = new YdDaoUtils();
        //공용 METHOD
        //YdUtils ydutils                 = new YdUtils();

        //리턴값(boolean)
        boolean blnRtnVal               = false;
        //리턴값(int)
        int intRtnVal                   = 0;
        //메세지
        String szMsg                    = "";
        //METHOD명
        String szMethodName             = "procCCsOhcCarryOutReq";
        //사용자
        String szUser                   = "SYSTEM";

        //레코드 선언
        JDTORecord recPara              = null;
        //레코드셋 선언
        JDTORecordSet rsResult          = null;

        //설비ID(열구분)
        String szYD_EQP_ID            = null;
        //적치BED번호
        String szYD_STK_BED_NO        = null;
        //OHC TAKE_OUT 재료번호
        String szOHC_TAKE_OUT_STL_NO  = null;
        //스케줄코드
        String szYD_SCH_CD            = null;
        //스케줄우선순위
        String szYD_SCH_PRIOR       = "";
        //스케줄 금지 유무
        String szYD_SCH_PROH_EXN      = null;
        //작업크레인
        String szYD_WRK_CRN           = null;
        //작업크레인우선순위
        String szYD_WRK_CRN_PRIOR        = null;
        //대체크레인
        String szYD_ALT_CRN           = null;
        //대체크레인유무
        String szYD_ALT_CRN_YN        = null;
        //대체크레인우선순위
        String szYD_ALT_CRN_PRIOR        = null;
        //선택크레인
        String szCrn                  = null;
        //야드구분
        String szYD_GP                = null;
        //동구분
        String szYD_BAY_GP            = null;
        //작업예약ID
        String szYD_WBOOK_ID          = null;

        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(msgRecord);
        //에러 리턴
        if(szRcvTcCode == null) {

            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;

        }
        //TC CODE DISPLAY
        if(bDebugFlag) {

            szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        }

        try {

            //받은 전문 편집
            //설비ID(적치열)
            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            if(szYD_EQP_ID.equals("")) {

                szMsg = "[전문 이상] 설비ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }
            //적치BED번호
            szYD_STK_BED_NO     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
            if(szYD_STK_BED_NO.equals("")) {

                szMsg = "[전문 이상] 적치BED번호가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }
            //OHC TAKE_OUT 재료번호
            szOHC_TAKE_OUT_STL_NO   = ydDaoUtils.paraRecChkNull(msgRecord, "OHC_TAKE_OUT_STL_NO");
            if(szOHC_TAKE_OUT_STL_NO.equals("")) {

                szMsg = "[전문 이상] OHC TAKE_OUT 재료번호가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }

            //=================================================================================
            //수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
            //BRE 등록 안됨...테스트용 스케줄코드 생성
            //추후 구현..
            szYD_SCH_CD  = szYD_EQP_ID + "LM";
            //=================================================================================

            // 리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //스케줄 기준 체크
            blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
            if(!blnRtnVal) return;

            //레코드 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //스케줄 금지 유무
            szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
            //작업크레인
            szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
            // 작업크레인우선순위
            szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
            //대체크레인유무
            szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
            //대체크레인
            szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
            // 대체크레인우선순위
            szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");


            //스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
            if(szYD_SCH_PROH_EXN.equals("Y")) {

                szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }

            //작업크레인 설비 상태 체크
            blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

            //작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
            if(!blnRtnVal) {

                szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                //대체크레인의 유무를 체크한다.
                //대체크레인이 없으면 에러 리턴
                if(!szYD_ALT_CRN_YN.equals("Y")) {

                    szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;

                }
                //대체크레인이 있으면 대체크레인 설비 상태 체크
                blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
                //대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
                if(!blnRtnVal) {

                    szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;

                } else {
                    //대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
                    szCrn = szYD_ALT_CRN;
                    szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
                }
            } else {
                //작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
                szCrn = szYD_WRK_CRN;
                szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
            }

            //크레인사양과 저장품 사양을 체크(길이,폭,중량)
            blnRtnVal = chkCrnSpecMtlSpec(szOHC_TAKE_OUT_STL_NO, szCrn);
            if(!blnRtnVal) return;

            //다른 작업예약에 재료가 등록되어있는지 체크한다.
            blnRtnVal = this.chkYdWrkBookMtl(szOHC_TAKE_OUT_STL_NO);
            if(!blnRtnVal) return;

            //리턴 recordSet 생성
            rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
            //작업예약ID 생성
            blnRtnVal = getYdWbookId(rsResult);
            if(!blnRtnVal) return;
            //레코드추출
            rsResult.first();
            recPara = rsResult.getRecord();
            //작업예약ID
            szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

            //야드구분
            szYD_GP       = szYD_SCH_CD.substring(0, 1);
            //동구분
            szYD_BAY_GP   = szYD_SCH_CD.substring(1, 2);

            //INSERT 항목 RECORD 생성
            recPara = JDTORecordFactory.getInstance().create();

            //INSERT할 항목 SET
            recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
            recPara.setField("YD_GP",       szYD_GP);
            recPara.setField("YD_BAY_GP",   szYD_BAY_GP);
            recPara.setField("YD_SCH_CD",   szYD_SCH_CD);
            recPara.setField("YD_SCH_PRIOR",    szYD_SCH_PRIOR);
            recPara.setField("YD_AIM_YD_GP",        szYD_GP);
            recPara.setField("YD_AIM_BAY_GP",   szYD_BAY_GP);
            recPara.setField("REGISTER",    szUser);

            //작업예약 INSERT
            intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
            if(intRtnVal < 1) {
                szMsg = "작업예약 데이터 등록 중 에러";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }


            //INSERT 항목 record 생성
            recPara = JDTORecordFactory.getInstance().create();

            //작업예약재료 정보 SET
            recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
            recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
            recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
            recPara.setField("REGISTER",      szUser);
            recPara.setField("STL_NO",        szOHC_TAKE_OUT_STL_NO);
            recPara.setField("YD_STK_LYR_NO", "001");

            //작업예약재료 테이블에 등록한다.
            intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

            if(intRtnVal < 1) {
                szMsg = "작업예약재료 데이터 등록 중 에러";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            //스케줄코드, 설비id
            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("JMS_TC_CD",   "YDYDJ500");
            recPara.setField("YD_SCH_CD",   szYD_SCH_CD);
            recPara.setField("YD_EQP_ID",   szCrn);

            ydDelegate.sendMsg(recPara);

        } catch(Exception e) {
            szMsg = "C연주 OHC CARRY_OUT 요구 처리중 Error : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }

    } // end of procCCsOhcCarryOutReq()



    /**
     * 오퍼레이션명 : A후판차량하차작업요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procAplCarUdWrkReq(JDTORecord msgRecord)throws JDTOException  {

        //작업예약 DAO
        YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
        //작업예약재료 DAO
        YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
        //공용 DAO METHOD
        YdDaoUtils ydDaoUtils           = new YdDaoUtils();
        //공용 METHOD
        YdUtils ydutils                 = new YdUtils();

        //레코드 선언
        JDTORecord recPara     = null;
        //레코드셋 선언
        JDTORecordSet rsResult = null;

        //리턴값(boolean)
        boolean blnRtnVal      = false;
        //리턴값(int)
        int intRtnVal          = 0;
        //메세지
        String szMsg           = "";
        //메소드명
        String szMethodName    = "procAplCarUdWrkReq";
        //사용자
        String szUser          = "SYSTEM";


        //차량스케줄ID
        String szYD_CAR_SCH_ID    = null;
        //적치열구분
        String szYD_STK_COL_GP    = null;
        //적치재료매수(int)
        int intMtlCnt             = 0;
        //스케줄 금지 유무
        String szYD_SCH_PROH_EXN  = null;
        //작업크레인
        String szYD_WRK_CRN       = null;
        //작업크레인우선순위
        String szYD_WRK_CRN_PRIOR        = null;
        //대체크레인유무
        String szYD_ALT_CRN_YN    = null;
        //대체크레인
        String szYD_ALT_CRN       = null;
        //대체크레인우선순위
        String szYD_ALT_CRN_PRIOR        = null;
        //선택크레인
        String szCrn              = null;
        //야드구분
        String szYD_GP            = null;
        //동구분
        String szYD_BAY_GP        = null;
        //작업예약ID
        String szYD_WBOOK_ID      = null;
        //스케줄코드
        String szYD_SCH_CD        = null;
        //스케줄우선순위
        String szYD_SCH_PRIOR       = "";
        //재료번호
        String [] szSTL_NO        = null;
        //적치BED번호
        String [] szYD_STK_BED_NO = null;
        //적치단번호
        String [] szYD_STK_LYR_NO = null;


        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(msgRecord);
        //에러 리턴
        if(szRcvTcCode == null) {

            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;

        }
        //TC CODE DISPLAY
        if(bDebugFlag) {

            szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        }

        try {
            //받은 전문 편집
            //차량스케줄ID
            szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_ID");
            if(szYD_CAR_SCH_ID.equals("")) {

                szMsg = "[전문 이상] 차량스케줄ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }
            //적치열구분(하차위치)
            szYD_STK_COL_GP     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
            if(szYD_STK_COL_GP.equals("")) {

                szMsg = "[전문 이상] 저장위치가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }

            //리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //차량스케줄 데이터 체크
            blnRtnVal = this.chkGetCarSch(szYD_CAR_SCH_ID, rsResult);
            if(!blnRtnVal) return;

            //차량스케줄 데이터 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //차량 정보 정합성 체크
            blnRtnVal = this.chkCarInfo(recPara, msgRecord);
            if(!blnRtnVal) return;

            //=================================================================================
            //수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
            //BRE 등록 안됨...테스트용 스케줄코드 생성
            //추후 구현..
            szYD_SCH_CD = szYD_STK_COL_GP + "I" + "A";
            //=================================================================================

            //리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //스케줄 기준 체크
            blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
            if(!blnRtnVal) return;

            //스케줄 기준 데이터 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //스케줄기준 체크
            //스케줄 금지 유무
            szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
            //작업크레인
            szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
            // 작업크레인우선순위
            szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
            //대체크레인유무
            szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
            //대체크레인
            szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
            // 대체크레인우선순위
            szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");

            //스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
            if(szYD_SCH_PROH_EXN.equals("Y")) {

                szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }

            //작업크레인 설비 상태 체크
            blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

            //작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
            if(!blnRtnVal) {

                szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                //대체크레인의 유무를 체크한다.
                //대체크레인이 없으면 에러 리턴
                if(!szYD_ALT_CRN_YN.equals("Y")) {

                    szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;

                }
                //대체크레인이 있으면 대체크레인 설비 상태 체크
                blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
                //대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
                if(!blnRtnVal) {

                    szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;

                }
            }

            //리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //작업예약 재료 조회
            blnRtnVal = this.chkGetCarftmvmtl(szYD_CAR_SCH_ID, rsResult);
            if(!blnRtnVal) return;

            //차량이송재료 갯수
            intMtlCnt       = rsResult.size();
            //재료번호
            szSTL_NO        = new String[intMtlCnt + 1];
            //적치BED번호
            szYD_STK_BED_NO = new String[intMtlCnt + 1];
            //적치단번호
            szYD_STK_LYR_NO = new String[intMtlCnt + 1];
            //커서 첨으로 이동
            rsResult.first();
            //차량이송재료 갯수만큼 재료번호, 적치BED번호, 적치단번호 배열에 대입, 작업예약등록여부체크
            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
                //레코드 추출
                recPara                 = rsResult.getRecord();
                //재료번호
                szSTL_NO[Loop_i]        = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
                //적치BED번호
                szYD_STK_BED_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
                //적치단번호
                szYD_STK_LYR_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");

                //다른 작업예약에 재료가 등록되어있는지 체크한다.
                blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
                if(!blnRtnVal) return;

                //다음 레코드로 이동
                rsResult.next();
            }

//          //재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다
//          for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
//
//              //크레인사양과 저장품 사양을 체크(길이,폭,중량)
//              blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
//              if(!blnRtnVal) return;
//
//          }

            //작업예약 테이블 INSERT할 항목 레코드 생성

            //리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //작업예약ID 생성
            blnRtnVal = this.getYdWbookId(rsResult);
            if(!blnRtnVal) return;
            //레코드 추출
            rsResult.first();
            recPara = rsResult.getRecord();
            //작업예약ID
            szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(recPara, "YD_WBOOK_ID");

            recPara       = JDTORecordFactory.getInstance().create();
            //야드구분
            szYD_GP       = szYD_SCH_CD.substring(0, 1);
            //동구분
            szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);

            //INSERT할 항목 SET
            recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
            recPara.setField("YD_GP",       szYD_GP);
            recPara.setField("YD_BAY_GP",   szYD_BAY_GP);
            recPara.setField("YD_SCH_CD",   szYD_SCH_CD);
            recPara.setField("REGISTER",    szUser);

            //작업예약 INSERT
            intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
            if(intRtnVal < 1) {
                szMsg = "작업예약 TABLE 등록 중 에러";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            //작업예약재료 테이블 조회 레코드 생성
            recPara = JDTORecordFactory.getInstance().create();

            recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
            recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
            recPara.setField("REGISTER",      szUser);
            //재료매수만큼 루프를 돌아서 작업예약재료를 등록한다.
            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

                recPara.setField("STL_NO",        szSTL_NO[Loop_i]);
                recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO[Loop_i]);
                recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO[Loop_i]);

                // 작업예약재료 테이블에 등록한다.
                intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

                if(intRtnVal < 1){
                    szMsg = "작업예약재료 TABLE 등록 중 에러";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }
            }

        } catch(Exception e) {
            szMsg = "A후판 차량 하차작업 요구 처리중 Error : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }


    } // end of procAplCarUdWrkReq()



    /**
     * 오퍼레이션명 : C연주차량하차작업요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procCCsCarUdWrkReq(JDTORecord msgRecord)throws JDTOException  {

        //차량이송재료 DAO
        YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
        //스케줄기준 DAO
        YdSchRuleDao ydSchRuleDao       = new YdSchRuleDao();
        //작업예약 DAO
        YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
        //작업예약재료 DAO
        YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
        //공용 DAO METHOD
        YdDaoUtils ydDaoUtils           = new YdDaoUtils();
        //공용 METHOD
        YdUtils ydutils                 = new YdUtils();

        //레코드 선언
        JDTORecord recPara     = null;
        //레코드셋 선언
        JDTORecordSet rsResult = null;

        //리턴값(boolean)
        boolean blnRtnVal      = false;
        //리턴값(int)
        int intRtnVal          = 0;
        //메세지
        String szMsg           = "";
        //메소드명
        String szMethodName    = "procCCsCarUdWrkReq";
        //사용자
        String szUser          = "SYSTEM";

        //차량스케줄ID
        String szYD_CAR_SCH_ID    = null;
        //적치열구분
        String szYD_STK_COL_GP    = null;
        //적치재료매수(int)
        int intMtlCnt             = 0;
        //스케줄 금지 유무
        String szYD_SCH_PROH_EXN  = null;
        //작업크레인
        String szYD_WRK_CRN       = null;
        //대체크레인유무
        String szYD_ALT_CRN_YN    = null;
        //대체크레인
        String szYD_ALT_CRN       = null;
        //선택크레인
        String szCrn              = null;
        //야드구분
        String szYD_GP            = null;
        //동구분
        String szYD_BAY_GP        = null;
        //작업예약ID
        String szYD_WBOOK_ID      = null;
        //스케줄코드
        String szYD_SCH_CD        = null;
        //재료번호
        String [] szSTL_NO        = null;
        //적치BED번호
        String [] szYD_STK_BED_NO = null;
        //적치단번호
        String [] szYD_STK_LYR_NO = null;


        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(msgRecord);
        //에러 리턴
        if(szRcvTcCode == null) {

            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;

        }
        //TC CODE DISPLAY
        if(bDebugFlag) {

            szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        }

        try {
            //받은 전문 편집
            //차량스케줄ID
            szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_ID");
            if(szYD_CAR_SCH_ID.equals("")) {

                szMsg = "[전문 이상] 차량스케줄ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }
            //적치열구분(하차위치)
            szYD_STK_COL_GP     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
            if(szYD_STK_COL_GP.equals("")) {

                szMsg = "[전문 이상] 저장위치가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }

            //리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //차량스케줄 데이터 체크
            blnRtnVal = this.chkGetCarSch(szYD_CAR_SCH_ID, rsResult);
            if(!blnRtnVal) return;

            //차량스케줄 데이터 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //차량 정보 정합성 체크
            blnRtnVal = this.chkCarInfo(recPara, msgRecord);
            if(!blnRtnVal) return;

            //=================================================================================
            //수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
            //BRE 등록 안됨...테스트용 스케줄코드 생성
            //추후 구현..
            szYD_SCH_CD = szYD_STK_COL_GP + "I" + "A";
            //=================================================================================

            //리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //스케줄 기준 체크
            blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
            if(!blnRtnVal) return;

            //스케줄 기준 데이터 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //스케줄기준 체크
            //스케줄 금지 유무
            szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
            //작업크레인
            szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
            //대체크레인유무
            szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
            //대체크레인
            szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");


            //스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
            if(szYD_SCH_PROH_EXN.equals("Y")) {

                szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }

            //작업크레인 설비 상태 체크
            blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

            //작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
            if(!blnRtnVal) {

                szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                //대체크레인의 유무를 체크한다.
                //대체크레인이 없으면 에러 리턴
                if(!szYD_ALT_CRN_YN.equals("Y")) {

                    szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;

                }
                //대체크레인이 있으면 대체크레인 설비 상태 체크
                blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
                //대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
                if(!blnRtnVal) {

                    szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;

                } else {
                    //대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
                    szCrn = szYD_ALT_CRN;

                }
            } else {
                //작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
                szCrn = szYD_WRK_CRN;

            }

            //리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            blnRtnVal = this.chkGetCarftmvmtl(szYD_CAR_SCH_ID, rsResult);
            if(!blnRtnVal) return;

            //차량이송재료 갯수
            intMtlCnt       = rsResult.size();
            //재료번호
            szSTL_NO        = new String[intMtlCnt + 1];
            //적치BED번호
            szYD_STK_BED_NO = new String[intMtlCnt + 1];
            //적치단번호
            szYD_STK_LYR_NO = new String[intMtlCnt + 1];
            //커서 첨으로 이동
            rsResult.first();
            //차량이송재료 갯수만큼 재료번호, 적치BED번호, 적치단번호 배열에 대입, 작업예약등록여부체크
            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
                //레코드 추출
                recPara                 = rsResult.getRecord();
                szSTL_NO[Loop_i]        = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
                szYD_STK_BED_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
                szYD_STK_LYR_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");

                //다른 작업예약에 재료가 등록되어있는지 체크한다.
                blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
                if(!blnRtnVal) return;

                //다음 레코드로 이동
                rsResult.next();
            }

//          //재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다
//          for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
//
//              //크레인사양과 저장품 사양을 체크(길이,폭,중량)
//              blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
//              if(!blnRtnVal) return;
//
//          }

            //작업예약 테이블 INSERT할 항목 레코드 생성

            //리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //작업예약ID 생성
            blnRtnVal = this.getYdWbookId(rsResult);
            if(!blnRtnVal) return;
            rsResult.first();
            recPara = rsResult.getRecord();
            szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

            recPara       = JDTORecordFactory.getInstance().create();
            //야드구분
            szYD_GP       = szYD_SCH_CD.substring(0, 1);
            //동구분
            szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);

            //INSERT할 항목 SET
            recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
            recPara.setField("YD_GP",       szYD_GP);
            recPara.setField("YD_BAY_GP",   szYD_BAY_GP);
            recPara.setField("YD_SCH_CD",   szYD_SCH_CD);
            recPara.setField("REGISTER",    szUser);

            //작업예약 INSERT
            intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
            if(intRtnVal < 1) {
                szMsg = "작업예약 Table 등록 중 에러";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            //작업예약재료 테이블 조회 레코드 생성
            recPara = JDTORecordFactory.getInstance().create();

            recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
            recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
            recPara.setField("REGISTER",      szUser);
            //재료매수만큼 루프를 돌아서 작업예약재료를 등록한다.
            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

                recPara.setField("STL_NO",        szSTL_NO[Loop_i]);
                recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO[Loop_i]);
                recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO[Loop_i]);

                // 작업예약재료 테이블에 등록한다.
                intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

                if(intRtnVal < 1){
                    szMsg = "작업예약재료 데이터 등록 중 에러";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }
            }

        } catch(Exception e) {
            szMsg = "C연주 차량 하차작업 요구 처리중 Error : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }


    } // end of procCCsCarUdWrkReq()

    /**
     * 오퍼레이션명 : 후판창고차량하차작업요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY4CarUdWrkReq(JDTORecord msgRecord)throws JDTOException  {

        //차량이송재료 DAO
        YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
        //스케줄기준 DAO
        YdSchRuleDao ydSchRuleDao       = new YdSchRuleDao();
        //작업예약 DAO
        YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
        //작업예약재료 DAO
        YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
        //공용 DAO METHOD
        YdDaoUtils ydDaoUtils           = new YdDaoUtils();
        //공용 METHOD
        YdUtils ydutils                 = new YdUtils();

        //레코드 선언
        JDTORecord recPara     = null;
        //레코드셋 선언
        JDTORecordSet rsResult = null;

        //리턴값(boolean)
        boolean blnRtnVal      = false;
        //리턴값(int)
        int intRtnVal          = 0;
        //메세지
        String szMsg           = "";
        //메소드명
        String szMethodName    = "procY4CarUdWrkReq";
        //사용자
        String szUser          = "SYSTEM";


        //차량스케줄ID
        String szYD_CAR_SCH_ID    = null;
        //적치열구분
        String szYD_STK_COL_GP    = null;
        //적치재료매수(int)
        int intMtlCnt             = 0;
        //스케줄 금지 유무
        String szYD_SCH_PROH_EXN  = null;
        //작업크레인
        String szYD_WRK_CRN       = null;
        //대체크레인유무
        String szYD_ALT_CRN_YN    = null;
        //대체크레인
        String szYD_ALT_CRN       = null;
        //선택크레인
        String szCrn              = null;
        //야드구분
        String szYD_GP            = null;
        //동구분
        String szYD_BAY_GP        = null;
        //작업예약ID
        String szYD_WBOOK_ID      = null;
        //스케줄코드
        String szYD_SCH_CD        = null;
        //재료번호
        String [] szSTL_NO        = null;
        //적치BED번호
        String [] szYD_STK_BED_NO = null;
        //적치단번호
        String [] szYD_STK_LYR_NO = null;


        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(msgRecord);
        //에러 리턴
        if(szRcvTcCode == null) {

            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;

        }
        //TC CODE DISPLAY
        if(bDebugFlag) {

            szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        }

        try {
            //받은 전문 편집
            //차량스케줄ID
            szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_ID");
            if(szYD_CAR_SCH_ID.equals("")) {

                szMsg = "[전문 이상] 차량스케줄ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }
            //적치열구분(하차위치)
            szYD_STK_COL_GP     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
            if(szYD_STK_COL_GP.equals("")) {

                szMsg = "[전문 이상] 저장위치가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }

            //리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //차량스케줄 데이터 체크
            blnRtnVal = this.chkGetCarSch(szYD_CAR_SCH_ID, rsResult);
            if(!blnRtnVal) return;

            //차량스케줄 데이터 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //차량 정보 정합성 체크
            blnRtnVal = this.chkCarInfo(recPara, msgRecord);
            if(!blnRtnVal) return;

            //=================================================================================
            //수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
            //BRE 등록 안됨...테스트용 스케줄코드 생성
            //추후 구현..
            szYD_SCH_CD = szYD_STK_COL_GP + "I" + "A";
            //=================================================================================

            //리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //스케줄 기준 체크
            blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
            if(!blnRtnVal) return;

            //스케줄 기준 데이터 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //스케줄기준 체크
            //스케줄 금지 유무
            szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
            //작업크레인
            szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
            //대체크레인유무
            szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
            //대체크레인
            szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");


            //스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
            if(szYD_SCH_PROH_EXN.equals("Y")) {

                szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }

            //작업크레인 설비 상태 체크
            blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

            //작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
            if(!blnRtnVal) {

                szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                //대체크레인의 유무를 체크한다.
                //대체크레인이 없으면 에러 리턴
                if(!szYD_ALT_CRN_YN.equals("Y")) {

                    szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;

                }
                //대체크레인이 있으면 대체크레인 설비 상태 체크
                blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
                //대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
                if(!blnRtnVal) {

                    szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;

                } else {
                    //대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
                    szCrn = szYD_ALT_CRN;

                }
            } else {
                //작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
                szCrn = szYD_WRK_CRN;

            }

            //리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            blnRtnVal = this.chkGetCarftmvmtl(szYD_CAR_SCH_ID, rsResult);
            if(!blnRtnVal) return;

            //차량이송재료 갯수
            intMtlCnt       = rsResult.size();
            //재료번호
            szSTL_NO        = new String[intMtlCnt + 1];
            //적치BED번호
            szYD_STK_BED_NO = new String[intMtlCnt + 1];
            //적치단번호
            szYD_STK_LYR_NO = new String[intMtlCnt + 1];
            //커서 첨으로 이동
            rsResult.first();
            //차량이송재료 갯수만큼 재료번호, 적치BED번호, 적치단번호 배열에 대입, 작업예약등록여부체크
            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
                //레코드 추출
                recPara                 = rsResult.getRecord();
                szSTL_NO[Loop_i]        = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
                szYD_STK_BED_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
                szYD_STK_LYR_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");

                //다른 작업예약에 재료가 등록되어있는지 체크한다.
                blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
                if(!blnRtnVal) return;

                //다음 레코드로 이동
                rsResult.next();
            }

            //재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다
            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

                //크레인사양과 저장품 사양을 체크(길이,폭,중량)
                blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
                if(!blnRtnVal) return;

            }

            //작업예약 테이블 INSERT할 항목 레코드 생성

            //리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //작업예약ID 생성
            blnRtnVal = this.getYdWbookId(rsResult);
            if(!blnRtnVal) return;
            rsResult.first();
            recPara = rsResult.getRecord();
            szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

            recPara       = JDTORecordFactory.getInstance().create();
            //야드구분
            szYD_GP       = szYD_SCH_CD.substring(0, 1);
            //동구분
            szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);

            //INSERT할 항목 SET
            recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
            recPara.setField("YD_GP",       szYD_GP);
            recPara.setField("YD_BAY_GP",   szYD_BAY_GP);
            recPara.setField("YD_SCH_CD",   szYD_SCH_CD);
            recPara.setField("REGISTER",    szUser);

            //작업예약 INSERT
            intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
            if(intRtnVal < 1) {
                szMsg = "작업예약 Table 등록 중 에러";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            //작업예약재료 테이블 조회 레코드 생성
            recPara = JDTORecordFactory.getInstance().create();

            recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
            recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
            recPara.setField("REGISTER",      szUser);
            //재료매수만큼 루프를 돌아서 작업예약재료를 등록한다.
            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

                recPara.setField("STL_NO",        szSTL_NO[Loop_i]);
                recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO[Loop_i]);
                recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO[Loop_i]);

                // 작업예약재료 테이블에 등록한다.
                intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

                if(intRtnVal < 1){
                    szMsg = "작업예약재료 데이터 등록 중 에러";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }
            }

        } catch(Exception e) {
            szMsg = "후판창고 차량 하차작업 요구 처리중 Error : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }


    } // end of procY4CarUdWrkReq()

    /**
     * 오퍼레이션명 : C연주대차하차작업요구
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procCCsTcarUdWrkReq(JDTORecord msgRecord)throws JDTOException  {

        //작업예약 DAO
        YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
        //작업예약재료 DAO
        YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
        //공용 DAO METHOD
        YdDaoUtils ydDaoUtils           = new YdDaoUtils();
        //공용 METHOD
        YdUtils ydutils                 = new YdUtils();

        //레코드 선언
        JDTORecord recPara     = null;
        //레코드셋 선언
        JDTORecordSet rsResult = null;

        //리턴값(boolean)
        boolean blnRtnVal      = false;
        //리턴값(int)
        int intRtnVal          = 0;
        //메세지
        String szMsg           = "";
        //메소드명
        String szMethodName    = "procCCsTcarUdWrkReq";
        //사용자
        String szUser          = "SYSTEM";


        //차량스케줄ID
        String szYD_TCAR_SCH_ID   = null;
        //적치열구분
        String szYD_STK_COL_GP    = null;
        //적치재료매수(int)
        int intMtlCnt             = 0;
        //스케줄 금지 유무
        String szYD_SCH_PROH_EXN  = null;
        //작업크레인
        String szYD_WRK_CRN       = null;
        //대체크레인유무
        String szYD_ALT_CRN_YN    = null;
        //대체크레인
        String szYD_ALT_CRN       = null;
        //선택크레인
        String szCrn              = null;
        //야드구분
        String szYD_GP            = null;
        //동구분
        String szYD_BAY_GP        = null;
        //작업예약ID
        String szYD_WBOOK_ID      = null;
        //스케줄코드
        String szYD_SCH_CD        = null;
        //재료번호
        String [] szSTL_NO        = null;
        //적치BED번호
        String [] szYD_STK_BED_NO = null;
        //적치단번호
        String [] szYD_STK_LYR_NO = null;


        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(msgRecord);
        //에러 리턴
        if(szRcvTcCode == null) {

            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;

        }
        //TC CODE DISPLAY
        if(bDebugFlag) {

            szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        }

        try {
            //받은 전문 편집
            //차량스케줄ID
            szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_SCH_ID");
            if(szYD_TCAR_SCH_ID.equals("")) {

                szMsg = "[전문 이상] 대차스케줄ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }
            //적치열구분(하차위치)
            szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
            if(szYD_STK_COL_GP.equals("")) {

                szMsg = "[전문 이상] 저장위치가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }

            //대차스케줄 데이터 유무 체크
            blnRtnVal = this.chkTcarSch(szYD_TCAR_SCH_ID);
            if(!blnRtnVal) return;

            //=================================================================================
            //수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
            //BRE 등록 안됨...테스트용 스케줄코드 생성
            //추후 구현..
            szYD_SCH_CD = szYD_STK_COL_GP + "I" + "A";
            //=================================================================================

            //리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            //스케줄기준 체크
            blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
            if(!blnRtnVal) return;

            //레코드 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //스케줄 금지 유무
            szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
            //작업크레인
            szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
            //대체크레인유무
            szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
            //대체크레인
            szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");


            //스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
            if(szYD_SCH_PROH_EXN.equals("Y")) {

                szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;

            }

            //작업크레인 설비 상태 체크
            blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

            //작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
            if(!blnRtnVal) {

                szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                //대체크레인의 유무를 체크한다.
                //대체크레인이 없으면 에러 리턴
                if(!szYD_ALT_CRN_YN.equals("Y")) {

                    szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;

                }
                //대체크레인이 있으면 대체크레인 설비 상태 체크
                blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
                //대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
                if(!blnRtnVal) {

                    szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;

                } else {
                    //대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
                    szCrn = szYD_ALT_CRN;

                }
            } else {
                //작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
                szCrn = szYD_WRK_CRN;

            }

            //리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //대차이송재료 체크
            blnRtnVal = this.chkGetTcarftmvmtl(szYD_TCAR_SCH_ID, rsResult);
            if(!blnRtnVal) return;

            //레코드 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //대차이송재료 갯수
            intMtlCnt       = rsResult.size();
            //재료번호
            szSTL_NO        = new String[intMtlCnt + 1];
            //적치BED번호
            szYD_STK_BED_NO = new String[intMtlCnt + 1];
            //적치단번호
            szYD_STK_LYR_NO = new String[intMtlCnt + 1];
            //커서 첨으로 이동
            rsResult.first();
            //대차이송재료 갯수만큼 재료번호, 적치BED번호, 적치단번호 배열에 대입, 작업예약등록여부체크
            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
                //레코드 추출
                recPara                 = rsResult.getRecord();
                szSTL_NO[Loop_i]        = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
                szYD_STK_BED_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
                szYD_STK_LYR_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");

                //다른 작업예약에 재료가 등록되어있는지 체크한다.
                blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
                if(!blnRtnVal) return;

                //다음 레코드로 이동
                rsResult.next();
            }

//          //재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다
//          for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
//
//              //크레인사양과 저장품 사양을 체크(길이,폭,중량)
//              blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
//              if(!blnRtnVal) return;
//
//          }

            //작업예약 테이블 INSERT할 항목 레코드 생성

            //리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //작업예약ID 생성
            blnRtnVal = this.getYdWbookId(rsResult);
            if(!blnRtnVal) return;
            rsResult.first();
            recPara = rsResult.getRecord();
            szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

            recPara       = JDTORecordFactory.getInstance().create();
            //야드구분
            szYD_GP       = szYD_SCH_CD.substring(0, 1);
            //동구분
            szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);

            //INSERT할 항목 SET
            recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
            recPara.setField("YD_GP",       szYD_GP);
            recPara.setField("YD_BAY_GP",   szYD_BAY_GP);
            recPara.setField("YD_SCH_CD",   szYD_SCH_CD);
            recPara.setField("REGISTER",    szUser);

            //작업예약 INSERT
            intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
            if(intRtnVal < 1) {
                szMsg = "작업예약 Table 등록 중 에러";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            //작업예약재료 테이블 조회 레코드 생성
            recPara = JDTORecordFactory.getInstance().create();

            recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
            recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
            recPara.setField("REGISTER",      szUser);
            //재료매수만큼 루프를 돌아서 작업예약재료를 등록한다.
            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

                recPara.setField("STL_NO",        szSTL_NO[Loop_i]);
                recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO[Loop_i]);
                recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO[Loop_i]);

                // 작업예약재료 테이블에 등록한다.
                intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

                if(intRtnVal < 1){
                    szMsg = "작업예약재료 데이터 등록 중 에러";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }
            }

        } catch(Exception e) {
            szMsg = "C연주 대차 하차작업 요구 처리중 Error : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }

    } // end of procCCsTcarUdWrkReq

    /**
     * 오퍼레이션명 : 대차이송재료 체크 및 데이터 반환
     *
     * @param  String     szCarSchId 차량스케줄ID
     * @return boolean    true(성공), false(실패)
     * @throws JDTOException
     */
    public boolean chkGetTcarftmvmtl(String szTcarSchId, JDTORecordSet rsResult)throws JDTOException  {

        //대차이송재료 DAO
        YdTcarFtmvMtlDao ydTcarFtmvMtlDao = new YdTcarFtmvMtlDao();

        String szMsg              = null;
        String szMethodName       = "chkGetTcarftmvmtl";
        int intRtnVal             = 0;
        boolean blnRtnVal         = false;
        JDTORecord recPara        = null;

        try {

            //조회항목 record 생성
            recPara = JDTORecordFactory.getInstance().create();

            //대차스케줄ID
            recPara.setField("YD_TCAR_SCH_ID", szTcarSchId);

            //대차이송재료 조회
            intRtnVal = ydTcarFtmvMtlDao.getYdTcarftmvmtl(recPara, rsResult, 1);

            //리턴값 메세지처리
            if(intRtnVal > 0) {

                blnRtnVal = true;

            } else if(intRtnVal == 0) {

                szMsg = "대차스케줄ID(" + szTcarSchId + ")에 대한 대차이송재료 데이터가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == -2) {

                szMsg = "대차스케줄ID(" + szTcarSchId + ")로 대차이송재료 조회중 parameter error 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else {

                szMsg = "대차스케줄ID(" + szTcarSchId + ")로 대차이송재료 조회중 오류 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            }
        } catch(Exception e) {
            szMsg = "대차이송재료 체크 중 Error : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            blnRtnVal = false;
        }
        return blnRtnVal = true;

    } //end of chkGetTcarftmvmtl

    /**
     * 오퍼레이션명 : 대차스케줄 유무 체크
     *
     * @param  String     szTcarSchID 대차스케줄ID
     * @return boolean    true(성공), false(실패)
     * @throws JDTOException
     */
    public boolean chkTcarSch(String szTcarSchID)throws JDTOException  {

        //대차스케줄 DAO
        YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();

        String szMsg           = null;
        String szMethodName    = "chkTcarSch";
        int intRtnVal          = 0;
        boolean blnRtnVal      = false;

        JDTORecord recPara     = null;
        JDTORecordSet rsResult = null;

        try {

            //조회항목 record 생성
            recPara = JDTORecordFactory.getInstance().create();

            //리턴 recordSet 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //대차스케줄ID
            recPara.setField("YD_TCAR_SCH_ID", szTcarSchID);

            //대차스케줄ID로 대차 스케줄 테이블 조회
            intRtnVal = ydTcarSchDao.getYdTcarsch(recPara, rsResult, 0);

            //리턴값 메세지처리
            if(intRtnVal > 1) {

                szMsg = "대차스케줄ID(" + szTcarSchID + ")에 대한 대차스케줄 데이터가 중복되었습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == 1) {

                blnRtnVal = true;

            } else if(intRtnVal == 0) {

                szMsg = "대차스케줄ID(" + szTcarSchID + ")에 대한 대차스케줄 데이터가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == -2) {

                szMsg = "대차스케줄ID(" + szTcarSchID + ")로 대차스케줄 조회중 parameter error 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else {

                szMsg = "대차스케줄ID(" + szTcarSchID + ")로 대차스케줄 조회중 오류 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            }
        } catch(Exception e) {
            szMsg = "대차스케줄 유무 체크 중 Error : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return blnRtnVal = false;
        }
        return blnRtnVal = true;

    } //end of chkTcarSch


    /**
     * 오퍼레이션명 : 차량이송재료 체크 및 데이터 반환
     *
     * @param  String     szCarSchId 차량스케줄ID
     * @return boolean    true(성공), false(실패)
     * @throws JDTOException
     */
    public boolean chkGetCarftmvmtl(String szCarSchId, JDTORecordSet rsResult)throws JDTOException  {

        //차량이송재료 DAO
        YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();

        String szMsg              = null;
        String szMethodName       = "chkGetCarftmvmtl";
        int intRtnVal             = 0;
        boolean blnRtnVal         = false;
        JDTORecord recPara        = null;

        try {

            //조회항목 record 생성
            recPara = JDTORecordFactory.getInstance().create();

            //차량스케줄ID
            recPara.setField("YD_CAR_SCH_ID", szCarSchId);

            //차량이송재료 조회
            intRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recPara, rsResult, 1);

            //리턴값 메세지처리
            if(intRtnVal > 0) {

                blnRtnVal = true;

            } else if(intRtnVal == 0) {

                szMsg = "차량스케줄ID(" + szCarSchId + ")에 대한 차량이송재료 데이터가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == -2) {

                szMsg = "차량스케줄ID(" + szCarSchId + ")로 차량이송재료 조회중 parameter error 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else {

                szMsg = "차량스케줄ID(" + szCarSchId + ")로 차량이송재료 조회중 오류 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            }
        } catch(Exception e) {
            szMsg = "차량이송재료 체크 중 Error : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            blnRtnVal = false;
        }
        return blnRtnVal = true;

    } //end of chkGetCarftmvmtl


    /**
     * 오퍼레이션명 : 차량 정보 체크
     *
     * @param  JDTORecord recCarSch 차량스케줄 레코드
     *         JDTORecord recMsg    전문 레코드
     * @return boolean    true(성공), false(실패)
     * @throws JDTOException
     */
    public boolean chkCarInfo(JDTORecord recCarSch, JDTORecord recMsg)throws JDTOException  {

        String szMsg        = null;
        String szMethodName = "chkCarInfo";
        boolean blnRtnVal   = false;

        //차량 사용 구분
        String szYD_CAR_USE_GP = null;
        //운송장비코드
        String szTRN_EQP_CD    = null;
        //차량번호
        String szCAR_NO        = null;
        //카드번호
        String szCARD_NO       = null;


        try {
            //운송장비코드
            szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(recMsg, "TRN_EQP_CD");
            //차량번호
            szCAR_NO     = ydDaoUtils.paraRecChkNull(recMsg, "CAR_NO");
            //카드번호
            szCARD_NO    = ydDaoUtils.paraRecChkNull(recMsg, "CARD_NO");


            //차량 사용 구분
            szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recCarSch, "YD_CAR_USE_GP");

            //제품 출하("G")이면 차량번호와 카드번호 체크
            if(szYD_CAR_USE_GP.equals("G")) {

                //차량번호 비교 후 다르면 에러 처리후 리턴
                if(!szCAR_NO.equals(ydDaoUtils.paraRecChkNull(recCarSch, "CAR_NO"))) {

                    szMsg = "차량스케줄ID("      + ydDaoUtils.paraRecChkNull(recCarSch, "YD_CAR_SCH_ID") + ")에 해당하는" +
                            "전문 차량번호("      + szCAR_NO                                              + ")와 "        +
                            "차량스케줄 차량번호(" + ydDaoUtils.paraRecChkNull(recCarSch, "CAR_NO")        + ")가 다릅니다.!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return blnRtnVal = false;

                }

                // PIDEV
//              String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "", "APPPI0", "*", "*");

//              if("N".equals(sApplyYnPI)) {
//
//                  //카드번호 비교 후 다르면 에러 처리후 리턴
//                  if(!szCARD_NO.equals(ydDaoUtils.paraRecChkNull(recCarSch, "CARD_NO"))) {
//
//                      szMsg = "차량스케줄ID("      + ydDaoUtils.paraRecChkNull(recCarSch, "YD_CAR_SCH_ID") + ")에 해당하는" +
//                              "전문 카드번호("      + szCARD_NO                                             + ")와 "        +
//                              "차량스케줄 카드번호(" + ydDaoUtils.paraRecChkNull(recCarSch, "CARD_NO") + ")가 다릅니다.!";
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                      return blnRtnVal = false;
//
//                  }
//
//              }

                //차량번호 카드번호 비교후 같으면 true 리턴
                blnRtnVal = true;
            //구내 운송("L")이면 운송장비코드 체크
            } else if(szYD_CAR_USE_GP.equals("L")) {
                //운송장비코드 비교 후 다르면 에러 처리후 리턴
                if(!szTRN_EQP_CD.equals(ydDaoUtils.paraRecChkNull(recCarSch, "TRN_EQP_CD"))) {

                    szMsg = "차량스케줄ID("         + ydDaoUtils.paraRecChkNull(recCarSch, "YD_CAR_SCH_ID") + ")에 해당하는" +
                            "전문 운송장비코드("      + szTRN_EQP_CD                                          + ")와 "       +
                            "차량스케줄 운송장비코드(" + ydDaoUtils.paraRecChkNull(recCarSch, "TRN_EQP_CD") + ")가 다릅니다.!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return blnRtnVal = false;

                }
                //운송장비코드 비교후 같으면 true 리턴
                blnRtnVal = true;
            //차량 사용구분 error
            } else {

                szMsg = "차량스케줄ID(" + ydDaoUtils.paraRecChkNull(recCarSch, "YD_CAR_SCH_ID") + ")에 해당하는 " +
                        "차량사용구분(" + szYD_CAR_USE_GP                                        + ") 에러!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;
            }
        } catch(Exception e) {
            szMsg = "차량 정보 체크 중 예외발생! 예외메세지: " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return blnRtnVal = false;
        }
        return blnRtnVal;

    } //end of chkCarInfo



    /**
     * 오퍼레이션명 : 차량스케줄 유무체크 및 조회결과 데이터 반환
     *
     * @param  String        szCarSchID 차량스케줄ID
     *         JDTORecordSet rsResult   결과레코드셋
     * @return boolean       true(성공), false(실패)
     * @throws JDTOException
     */
    public boolean chkGetCarSch(String szCarSchID, JDTORecordSet rsResult)throws JDTOException  {

        //차량스케줄 DAO
        YdCarSchDao ydCarSchDao = new YdCarSchDao();

        String szMsg        = null;
        String szMethodName = "chkGetCarSch";
        int intRtnVal       = 0;
        boolean blnRtnVal   = false;
        JDTORecord recPara  = null;

        try {

            //조회항목 record 생성
            recPara = JDTORecordFactory.getInstance().create();

            //차량스케줄ID
            recPara.setField("YD_CAR_SCH_ID", szCarSchID);

            //차량스케줄ID로 차량스케줄 테이블 조회
            intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult, 0);

            //리턴값 메세지처리
            if(intRtnVal > 1) {

                szMsg = "차량스케줄ID(" + szCarSchID + ")에 대한 차량스케줄 데이터가 중복되었습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == 1) {

                blnRtnVal = true;

            } else if(intRtnVal == 0) {

                szMsg = "차량스케줄ID(" + szCarSchID + ")에 대한 차량스케줄 데이터가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == -2) {

                szMsg = "차량스케줄ID(" + szCarSchID + ")로 차량스케줄 조회중 parameter error 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else {

                szMsg = "차량스케줄ID(" + szCarSchID + ")로 차량스케줄 조회중 오류 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            }
        } catch(Exception e) {
            szMsg = "차량스케줄 유무체크 및 조회결과 데이터 반환 중 Error : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            blnRtnVal = false;
        }
        return blnRtnVal;
    } //end of chkGetCarSch



    /**
     * 오퍼레이션명 : 크레인작업가능사양과 재료사양을 체크
     *
     * @param   String szStlNo 재료번호
     *          String szEqpId 크레인 설비ID
     * @return boolean true(크레인재료이송가능), false(크레인재료이송불가)
     * @throws JDTOException
     */
    public boolean chkCrnSpecMtlSpec(String szStlNo, String szEqpId)throws JDTOException  {

        //리턴값(boolean)
        boolean blnRtnVal         = false;
        //메세지
        String szMsg              = null;
        //메소드명
        String szMethodName       = "chkCrnSpecMtlSpec";
        //레코드 선언
        JDTORecord recPara        = null;
        //레코드셋 선언
        JDTORecordSet rsResult    = null;

        try {
            //레코드셋 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //저장품 유무 체크
            blnRtnVal = this.chkGetStock(szStlNo, rsResult);
            if(!blnRtnVal) return blnRtnVal;

            //결과 레코드 추출
            rsResult.first();
            recPara = rsResult.getRecord();
            // 폭
            double lngMtlW     = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_MTL_W");
            // 길이
            long lngMtlL     = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_L");
            // 중량
            long lngMtlWt    = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");

            //레코드셋 재생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //크레인사양 체크 및 조회
            blnRtnVal = this.chkGetCrnSpec(szEqpId, rsResult);
            if(!blnRtnVal) return blnRtnVal;

            //크레인사양 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            // 크레인 작업 능력
            // 작업가능길이
            long lngAbleL  = ydDaoUtils.paraRecChkNullLong(recPara, "YD_WRK_ABLE_L");
            // 작업가능폭
            double lngAbleW  = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_WRK_ABLE_W");
            // 작업가능중량
            long lngAbleWt = ydDaoUtils.paraRecChkNullLong(recPara, "YD_WRK_ABLE_WT");

            //크레인 작업가능 길이와 재료의 길이 비교
            if(lngAbleL < lngMtlL) {
                szMsg = "크레인 작업가능 길이(" + lngAbleL + ") 보다 재료의 길이(" + lngMtlL + ")가 더 큽니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return blnRtnVal = false;
            }

            //크레인 작업가능 폭과 재료의 폭 비교
            if(lngAbleW < lngMtlW) {
                szMsg = "크레인 작업가능 폭(" + lngAbleW + ")보다 재료의 폭(" + lngMtlW + ")이 더 큽니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return blnRtnVal = false;
            }

            //크레인 작업가능 중량과 재료의 중량  비교
            if(lngAbleWt < lngMtlWt) {
                szMsg = "크레인 작업가능 중량(" + lngAbleWt + ")보다 재료의 중량(" + lngMtlWt + ")이 더 큽니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return blnRtnVal = false;
            }

        } catch(Exception e) {
            szMsg = "크레인작업가능사양과 재료사양을 체크 중 예외발생! 예외메세지: " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return blnRtnVal = false;
        }
        return blnRtnVal = true;
    } //end of chkCrnSpecMtlSpec



    /**
     * 오퍼레이션명 : 크레인사양 유무체크 및 조회결과 데이터 반환
     *
     * @param  String        szEqpId  설비ID
     *         JDTORecordSet rsResult 결과레코드셋
     * @return boolean       true(성공), false(실패)
     * @throws JDTOException
     */
    public boolean chkGetCrnSpec(String szEqpId, JDTORecordSet rsResult)throws JDTOException  {

        //크레인사양 DAO
        YdCrnSpecDao ydCrnSpecDao = new YdCrnSpecDao();
        //리턴값(boolean)
        boolean blnRtnVal     = false;
        //리턴값(int)
        int intRtnVal         = 0;
        //메소드명
        String szMethodName   = "chkGetCrnSpec";
        String szMsg          = null;

        //레코드 선언
        JDTORecord recPara        = null;

        try {
            //레코드 생성
            recPara  = JDTORecordFactory.getInstance().create();

            //크레인 설비ID
            recPara.setField("YD_EQP_ID", szEqpId);

            //크레인사양 조회
            intRtnVal = ydCrnSpecDao.getYdCrnspec(recPara, rsResult, 0);

            //리턴값 메세지처리
            if(intRtnVal > 1) {

                szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 중복되었습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == 1) {

                blnRtnVal = true;

            } else if(intRtnVal == 0) {

                szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == -2) {

                szMsg = "크레인설비ID(" + szEqpId + ")로 크레인사양 조회중 parameter error 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else {

                szMsg = "크레인설비ID(" + szEqpId + ")로 크레인사양 조회중 오류 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            }
        } catch(Exception e) {
            szMsg = "크레인사양 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            blnRtnVal = false;
        }
        return blnRtnVal;
    } //end of chkGetCrnSpec



    /**
     * 오퍼레이션명 : 저장품유무체크 및 조회결과 데이터 반환
     *
     * @param  String        szStlNo  재료번호
     *         JDTORecordSet rsResult 결과레코드셋
     * @return boolean       true(성공), false(실패)
     * @throws JDTOException
     */
    public boolean chkGetStock(String szStlNo, JDTORecordSet rsResult)throws JDTOException  {

        //저장품 DAO
        YdStockDao ydStockDao = new YdStockDao();
        //리턴값(boolean)
        boolean blnRtnVal     = false;
        //리턴값(int)
        int intRtnVal         = 0;
        //메소드명
        String szMethodName   = "chkGetStock";
        String szMsg          = null;

        //레코드 선언
        JDTORecord recPara        = null;

        try {
            //레코드 생성
            recPara  = JDTORecordFactory.getInstance().create();

            //재료번호
            recPara.setField("STL_NO", szStlNo);

            //저장품 테이블 조회
            intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 110);

            //리턴값 메세지처리
            if(intRtnVal > 1) {

                szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 중복되었습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == 1) {

                blnRtnVal = true;

            } else if(intRtnVal == 0) {

                szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 없거나 적치단에 재료가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == -2) {

                szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 parameter error 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else {

                szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 오류 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            }
        } catch(Exception e) {
            szMsg = "저장품유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            blnRtnVal = false;
        }
        return blnRtnVal;
    } //end of chkGetStock



    /**
     * 오퍼레이션명 : 저장품유무체크 및 조회결과 데이터 반환
     *
     * @param  String        szStlNo  재료번호
     *         JDTORecordSet rsResult 결과레코드셋
     * @return boolean       true(성공), false(실패)
     * @throws JDTOException
     */
    public boolean chkGetStock1(String szStlNo, JDTORecordSet rsResult)throws JDTOException  {

        //저장품 DAO
        YdStockDao ydStockDao = new YdStockDao();
        //리턴값(boolean)
        boolean blnRtnVal     = false;
        //리턴값(int)
        int intRtnVal         = 0;
        //메소드명
        String szMethodName   = "chkGetStock";
        String szMsg          = null;

        //레코드 선언
        JDTORecord recPara        = null;

        try {
            //레코드 생성
            recPara  = JDTORecordFactory.getInstance().create();

            //재료번호
            recPara.setField("STL_NO", szStlNo);

            //저장품 테이블 조회
            intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 110);

            //리턴값 메세지처리
            if(intRtnVal > 1) {

                szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 중복-PASS.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = true;

            } else if(intRtnVal == 1) {

                blnRtnVal = true;

            } else if(intRtnVal == 0) {

                szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 없거나 적치단에 재료가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == -2) {

                szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 parameter error 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else {

                szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 오류 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            }
        } catch(Exception e) {
            szMsg = "저장품유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            blnRtnVal = false;
        }
        return blnRtnVal;
    } //end of chkGetStock



    /**
     * 오퍼레이션명 : 설비상태 체크
     *
     * @param   String szEqpId 설비ID
     * @return boolean true(설비사용가능), false(설비사용불가)
     * @throws JDTOException
     */
    public boolean eqpStatCheck(String szEqpId)throws JDTOException  {

        //리턴값(boolean)
        boolean blnRtnVal      = false;
        //메세지
        String szMsg           = null;
        //메소드명
        String szMethodName    = "eqpStatCheck";
        //설비상태
        String szYD_EQP_STAT   = null;
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
            szMsg = "설비ID(" + szEqpId + ")입니다.";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //설비 체크 및 데이터 조회
            blnRtnVal = this.chkGetEqp(szEqpId, rsResult);
            if(!blnRtnVal) return blnRtnVal;

            //레코드 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            //설비상태
            szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");

            //크레인의 상태가 'T'이면 false 리턴.
            //상수 수정 [2009.12.03] 이현성
            if(szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {

                szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT + ") 입니다.";
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

    } //end of eqpStatCheck

    /**
     * 오퍼레이션명 : 스케줄기준 체크 및 데이터 반환
     *
     * @param  String     szSchCd 스케줄CD
     * @return boolean    true(성공), false(실패)
     * @throws JDTOException
     */
    public boolean chkGetSchRule(String szSchCd, JDTORecordSet rsResult)throws JDTOException  {

        //스케줄기준 DAO
        YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

        String szMsg              = null;
        String szMethodName       = "chkGetSchRule";
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
            if(intRtnVal > 1) {

                szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 중복되었습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == 1) {

                blnRtnVal = true;

            } else if(intRtnVal == 0) {

                szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == -2) {

                szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 parameter error 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else {

                szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 오류 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            }
        } catch(Exception e) {
            szMsg = "스케줄기준 체크 중 Error : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return blnRtnVal = false;
        }
        return blnRtnVal = true;

    } //end of chkGetSchRule


    /**
     * 오퍼레이션명 : 작업예약재료 등록여부 체크
     *
     * @param   String szStlNo 재료번호
     * @return boolean true(작업예약재료등록가능), false(작업예약재료등록불가)
     * @throws JDTOException
     */
    public boolean chkYdWrkBookMtl(String szStlNo)throws JDTOException  {

        //작업예약재료 DAO
        YdWrkbookMtlDao ydWrkbookMtlDao  = new YdWrkbookMtlDao();

        //메세지
        String szMsg              = null;
        //메소드명
        String szMethodName       = "chkYdWrkBookMtl";
        //리턴값(boolean)
        boolean blnRtnVal = false;
        //리턴값(int)
        int intRtnVal = 0;
        //레코드 선언
        JDTORecord recPara     = null;
        //레코드셋 선언
        JDTORecordSet rsResult = null;

        try {
            //레코드 생성
            recPara  = JDTORecordFactory.getInstance().create();
            //레코드셋 생성
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            //재료번호
            recPara.setField("STL_NO", szStlNo);

            //재료번호로 작업예약재료 테이블을 읽어온다.
            intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, rsResult, 2);

            //리턴값 메세지처리
            if(intRtnVal > 0) {

                szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 이미 등록되어 있습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == 0) {

                szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 등록 가능합니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                blnRtnVal = true;

            } else if(intRtnVal == -2) {

                szMsg = "재료번호(" + szStlNo + ")로 작업예약재료 조회중 parameter error 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else {

                szMsg = "재료번호(" + szStlNo + ")로 작업예약재료 조회중 오류 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            }
        } catch(Exception e) {
            szMsg = "작업예약재료 등록여부 체크 중 예외발생! 예외메세지: " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            blnRtnVal = false;
        }
        return blnRtnVal;

    } //end of chkYdWrkBookMtl

    /**
     * 오퍼레이션명 : 작업예약ID생성
     *
     * @param  JDTORecordSet rsResult 결과 레코드셋
     * @return boolean       true(성공), false(실패)
     * @throws JDTOException
     */
    public boolean getYdWbookId(JDTORecordSet rsResult)throws JDTOException  {

        //작업예약 DAO
        YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();

        //메세지
        String szMsg              = null;
        //메소드명
        String szMethodName       = "getYdWbookId";
        //리턴값(int)
        int intRtnVal             = 0;
        //리턴값(boolean)
        boolean blnRtnVal         = false;
        //레코드 선언
        JDTORecord recPara        = null;

        try {
            //레코드 생성
            recPara  = JDTORecordFactory.getInstance().create();

            //================================================
            //파라미터를 설정하지 않으면 JSPEED에서 에러발생. 추후 수정요
            recPara.setField("YD_WBOOK_ID", "1");
            //================================================

            //작업예약 테이블의 시퀀스를 이용해 작업예약ID를 구해온다.
            intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, rsResult, 1);
            //리턴값 메세지처리
            if(intRtnVal > 1) {

                szMsg = "작업예약ID 데이터가 중복되었습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == 1) {

                blnRtnVal = true;

            } else if(intRtnVal == 0) {

                szMsg = "작업예약ID를 구하지 못했습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else if(intRtnVal == -2) {

                szMsg = "작업예약ID 조회중 parameter error 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            } else {

                szMsg = "작업예약ID 조회중 오류 발생!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                blnRtnVal = false;

            }
        } catch(Exception e) {
            szMsg = "작업예약ID생성 중 예외발생! 예외메세지: " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            blnRtnVal = false;
        }
        return blnRtnVal;
    } //end of getYdWbookId


    /**
     * 오퍼레이션명 : P2 Pilling실적 (P2YDL001) - 1후판제품창고야드용
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procP2PillingWr(JDTORecord tcRecord) throws JDTOException  {
        /*
         * 업무기준 : 1. L2전문을 내부전문 형식으로 변환처리
         *           2. 재료의 파일링코드 조회 해서 파일링ZONE위치 판단
         *              2-1. 길이구분이 단척이면 #1 STOPPER에서 처리 - RT ZONE NO[56010]
         *              2-2. 길이구분이 단척이 아니면 #2 STOPPER에서 처리 - RT ZONE NO[56020]
         *           3. 해당 RT ZONE NO를 야드의 저장위치 로 변환
         *           4. 변환된 저장위치에 해당 재료들을 적치
         *           5. 재료의 BOOK-OUT위치를 야드의 저장위치로 변환 후 해당 저장위치의 가상베드를 조회
         *              5-1. 가상베드가 존재하지 않으면 오류처리
         *              5-2. 가상베드가 존재하면 해당 가상베드에 재료를 적치
         *                  5-2-1. A동이면
         *                      5-2-1-1. RT상의 BOOK-OUT위치[56010, 56020]이면 가적장으로 처리
         *                      5-2-1-2. BOOK-OUT위치[56106, 56116]이면 TRNASFER로 처리
         *                  5-2-2. B동이면 TRANSFER로 처리[입고예정위치가 01 BED - 56206, 02 BED - 56216]
         *                  5-2-3. 이외의 동이면 RT로 BOOK-OUT위치 처리
         *                  5-2-4. 해당 재료에 대한 Plate공통에 현재 BOOK-OUT위치의 야드저장위치를 업데이트 처리
         *              5-3. RT상에 적치된 재료 정보 삭제처리
         * 수정자 : 임춘수
         * 수정일 :
         *      1) 2009.11.06 - 전문LAYOUT 변경
         *      2) 2009.11.13 - Plate공통의 저장위치 업데이트 로직 추가
         *
         */
        // 수신전문과 프로그램에서 사용하는 수신항목이 일치하는지 확인
        //
        // Pre Piler 실적 TL3CPP(P2YDL001) 29043
        //
        //후판작업자사번            PL_WRKER_EMPNO          VARCHAR2    10
        //후판작업근조              PL_WD                   VARCHAR2    2
        //후판제품Piling작업일      PL_GDS_PILNG_WRK_DD     VARCHAR2    8
        //후판제품Piling단위번호    PL_GDS_PILNG_UNIT_NO    NUMBER      4
        //후판제품Piling매수        PL_GDS_PILNG_SH         NUMBER      2
        //후판L2제품번호1           PL_L2_TRK_NO1           VARCHAR2    16
        //후판Plate번호1            PL_PLATE_NO1            VARCHAR2    10
        //후판L2제품번호2           PL_L2_TRK_NO2           VARCHAR2    16
        //후판Plate번호2            PL_PLATE_NO2            VARCHAR2    10
        //후판L2제품번호3           PL_L2_TRK_NO3           VARCHAR2    16
        //후판Plate번호3            PL_PLATE_NO3            VARCHAR2    10
        //후판L2제품번호4           PL_L2_TRK_NO4           VARCHAR2    16
        //후판Plate번호4            PL_PLATE_NO4            VARCHAR2    10
        //후판L2제품번호5           PL_L2_TRK_NO5           VARCHAR2    16
        //후판Plate번호5            PL_PLATE_NO5            VARCHAR2    10

        //적치단 DAO
        YdStkLyrDao ydStkLyrDao    = new YdStkLyrDao();
        //공용 DAO METHOD
        YdDaoUtils ydDaoUtils      = new YdDaoUtils();
        //DELEGATE
        //YdDelegate ydDelegate = new YdDelegate();
        //공용 METHOD
        //YdUtils ydutils            = new YdUtils();
        //저장품 DAO
        YdStockDao ydStockDao      = new YdStockDao();

        //리턴값(int)
        int intRtnVal          = 0;
        //리턴값(boolean)
        boolean blnRtnVal      = false;
        //메세지
        String szMsg           = "";
        //METHOD명
        String szMethodName    = "procP2PillingWr";
        //사용자
        String szUser          = "SYSTEM";
        String szOperationName = "P2 Pilling실적";

        //레코드 선언
        JDTORecord    recPara  = null;
        //JDTORecord    recOutPara  = null;
        JDTORecord    recTemp  = null;
        JDTORecord    recApTmp = null;
        //레코드셋 선언
        JDTORecordSet rsResult = null;

        //파일링 위치
        String szPILING_LOC          = null;
        //설비ID(PILING 설비)
        String szYD_EQP_ID           = null;
        //적치BED번호
        String szYD_STK_BED_NO       = null;
        //CARRY_OUT 요구 구분
        String szPILING_CMPL_YN      = "Y";
        //재료번호
        String szPILING_STL_NO       = null;
        //FILING ZONE NO
        String szPILING_ZONE_NO      = null;
        //적치단재료상태
        String szYD_STK_LYR_MTL_STAT = null;
        //전문발생일시
        String szDate                = null;
        //BOOK-OUT위치
        String szYD_BOOK_OUT_LOC     = null;
        //야드Piling코드
        String szYD_PILING_CD        = null;
        //BOOK-OUT COL_GP
        String szYD_BOOK_OUT_COL_GP  = null;
        //BOOK-OUT COL_GP
        String szYD_BOOK_OUT_BED_NO  = null;
        // 저장위치 정보
        String[] arrRT_ZONE_NO = null;

        boolean isExistBed          = false;

        boolean isL2SendMod         = false;

        //재료 매수
        int intPILING_MTL_SH         = 0;
        int iTmpSeq                     = 0;
        JDTORecord msgRecord = JDTORecordFactory.getInstance().create();

        //Crane 파일링 관련 변수 및 dao --------------------------------------------
        JDTORecord      inRecord    = JDTORecordFactory.getInstance().create();
        YdPlateCommDAO  commDao     = new YdPlateCommDAO();
        String          szStlNo     = null;
        JDTORecordSet   outRecSet1      = JDTORecordFactory.getInstance().createRecordSet("");
        JDTORecordSet   outRecSet2      = JDTORecordFactory.getInstance().createRecordSet("");
        String          szYD_STK_COL_GP = null;
        int             intRtnVal2          = 0;
        //---------------------------------------------------------------------

        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(tcRecord);

        /*
         * R/T모니터링 및 1매 BOOK-OUT요구시 JMS_TC_CD 값에 P2YDL001 셋팅함.
         * L2에서 수신되는 I/F 는 JMS_TC_CD 값이 없다.
         * 이후 처리로직에 해당항목을 기준으로 체크한다.
         */
        if(szRcvTcCode == null || szRcvTcCode.equals("")) {
            isL2SendMod = true;
        }else{
            isL2SendMod = false;
        }


        try{
            //=============================================================
            // 권오창
            // 2009.11.05
            //
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판압연전단L2] P2 Pilling실적 수신";
            ydUtils.putLogMsg("D", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            //파라미터 Check

            //재료번호를 파일링 위치에 등록

            //마지막재료인지 확인 구분자 check

            //마지막재료인 경우 정지위치 롤러 Table에 적치

            //현재위치 재료삭제

            szMsg = "[P2 Pilling실적 - procP2PillingWr] 메소드 시작 ---------- 전문 내용 시작 --------------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            intPILING_MTL_SH = ydDaoUtils.paraRecChkNullInt(tcRecord, "PL_GDS_PILNG_SH");

            if(isL2SendMod){
                /*
                 * 2010.04.26 윤재광
                 * L2에서 I/F 되는 경우
                 * 1.   날판번호/분할번호로 넘어옴
                 * 2.   후판제품Piling작업일이 있으면 최종 파일링 실적, 없으면 각개 단위 파일링 실적
                 */

                String sPlGdsPilngWrkDd = ydDaoUtils.paraRecChkNull(tcRecord, "PL_GDS_PILNG_WRK_DD");

                if("".equals(sPlGdsPilngWrkDd)){
                    // A R/T Tracking Info로 활용
                    szMsg = "A후판 PILING실적 (P2YDL001) => PILING실적 SKIP 처리";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    szMsg = "후판제품창고 A R/T 파일러 트래킹 정보";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    String sMaxPlL2TrkNo    = "";
                    String sMaxPlPlateNo    = "";
                    for(int idx = 1; idx <= intPILING_MTL_SH; idx++ ) {

                        sMaxPlL2TrkNo   = ydDaoUtils.paraRecChkNull(tcRecord, "PL_L2_TRK_NO"    + (idx));
                        sMaxPlPlateNo   = ydDaoUtils.paraRecChkNull(tcRecord, "PL_PLATE_NO"     + (idx));

                        //INSERT항목 레코드 생성
                        recPara = JDTORecordFactory.getInstance().create();

                        recPara.setField("YD_STK_COL_GP",       YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BAP01");
                        recPara.setField("YD_STK_BED_NO",       "01");
                        recPara.setField("YD_STK_LYR_NO",       "00" + (idx));
                        recPara.setField("MODIFIER",            "");
                        recPara.setField("YD_STK_LYR_MTL_STAT", "C");
                        recPara.setField("STL_NO",              getL3PlateNo(sMaxPlL2TrkNo,sMaxPlPlateNo));

                        //업데이트 실행
                        intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);

                        szMsg = "[P2 Pilling실적 - procP2PillingWr] 각개 단위 PILING실적  적치단 Update 결과["+intRtnVal+"]";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.DEBUG);

                        /*
                         * 2010.08.02 YJK
                         * 파일링 실적시간 관리 항목
                         */
                        if(idx == 1){
                            //--------------------------------------------------------------
                            recPara = JDTORecordFactory.getInstance().create();
                            recPara.setField("STL_NO",          getL3PlateNo(sMaxPlL2TrkNo,sMaxPlPlateNo));
                            recPara.setField("COIL_CAR_NO",     "P"); //Piling 실적
                            //--------------------------------------------------------------

                            szMsg = "[P2 Pilling실적 - 재료정보 파일링실적 시간 등록]["+getL3PlateNo(sMaxPlL2TrkNo,sMaxPlPlateNo)+"]";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            intRtnVal = ydStockDao.update_Dm_Time(recPara,4);
                        }
                    }

                    //-------------------------------------------------------------------------------------------------start
                    //1후판 ON,OFF 라인 크래인 파일링 기능 적용시
                    //  AutoPiler가 파일링 중인 대상들이 1후판 R/T 가상버퍼에 낱장으로 존재한다면 Clear 하는 작업을 수행한다.
                    for(int idx = 1; idx <= intPILING_MTL_SH; idx++ ) {
                        sMaxPlL2TrkNo   = ydDaoUtils.paraRecChkNull(tcRecord, "PL_L2_TRK_NO"    + (idx));
                        sMaxPlPlateNo   = ydDaoUtils.paraRecChkNull(tcRecord, "PL_PLATE_NO"     + (idx));

                        //a. STL_NO 구하기
                        szStlNo = getL3PlateNo(sMaxPlL2TrkNo,sMaxPlPlateNo);

                        //b. STL_NO 로 R/T 가상버퍼 조회하기
                        recPara.setField("YD_STK_COL_GP", YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "_RT%");
                        recPara.setField("STL_NO", szStlNo);

                        /*com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0054*/
                        intRtnVal = commDao.select(recPara, outRecSet1, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0054");

                        for(int ii = 1; ii <= intRtnVal; ii++) {
                            //c. 가상버퍼에 낱장 존재한다면
                            outRecSet1.absolute(ii);
                            recPara = outRecSet1.getRecord();

                            //입고존 출발시 R/T 가상베드 예정위치에 낱장으로 입력된 제품의 물리주소
                            szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
                            szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");

                            //d. 해당 R/T 가상 BED Clear
                            //가상버퍼의 마지막  부분 처리
                            recPara.setField("FROM_STK_COL_GP",     szYD_STK_COL_GP);
                            recPara.setField("FROM_BED_NO",       szYD_STK_BED_NO); //마지막 가상버퍼 번지 (Clear 될 번지)

                            commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0007");

                            //e. 다음 가상버퍼에 데이터 존재한다면 가상버퍼 Shift 처리 하기
                            recPara.setField("YD_STK_COL_GP",     szYD_STK_COL_GP);
                            recPara.setField("YD_STK_BED_NO",     szYD_STK_BED_NO);
                            intRtnVal2 = commDao.select(recPara, outRecSet2, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0055");

                            if(intRtnVal2 > 0) {
                                YdCommonUtils.procShiftBedInfoForBookoutLocNew(szYD_STK_COL_GP + szYD_STK_BED_NO);
                            }
                        }
                    }
                    //-------------------------------------------------------------------------------------------------end

                    return;
                }else{

                    String sMaxPlL2TrkNo    = ydDaoUtils.paraRecChkNull(tcRecord, "PL_L2_TRK_NO"    + intPILING_MTL_SH);
                    String sMaxPlPlateNo    = ydDaoUtils.paraRecChkNull(tcRecord, "PL_PLATE_NO"     + intPILING_MTL_SH);

                    msgRecord.setField("PILING_STL_NO", getL3PlateNo(sMaxPlL2TrkNo,sMaxPlPlateNo));
                    msgRecord.setField("PILING_MTL_SH", "" + intPILING_MTL_SH);         //후판제품Piling매수

                    for(int i = 0; i < intPILING_MTL_SH; i++ ) {
                        // L2 후판번호 > L3 후판번호 변환작업.
                        //sMaxPlL2TrkNo = ydDaoUtils.paraRecChkNull(tcRecord, "PL_L2_TRK_NO"    + (intPILING_MTL_SH - i));
                        //sMaxPlPlateNo = ydDaoUtils.paraRecChkNull(tcRecord, "PL_PLATE_NO"     + (intPILING_MTL_SH - i));

                        sMaxPlL2TrkNo   = ydDaoUtils.paraRecChkNull(tcRecord, "PL_L2_TRK_NO"    + (i + 1));
                        sMaxPlPlateNo   = ydDaoUtils.paraRecChkNull(tcRecord, "PL_PLATE_NO"     + (i + 1));

                        msgRecord.setField("STL_NO" + (i + 1), getL3PlateNo(sMaxPlL2TrkNo,sMaxPlPlateNo));

                        /*
                         * 2010.08.02 YJK
                         * 파일링 실적시간 관리 항목
                         */
                        if(i == 0){
                            //--------------------------------------------------------------
                            recPara = JDTORecordFactory.getInstance().create();
                            recPara.setField("STL_NO",          getL3PlateNo(sMaxPlL2TrkNo,sMaxPlPlateNo));
                            recPara.setField("COIL_CAR_NO",     "P"); //Piling 실적
                            //--------------------------------------------------------------

                            szMsg = "[P2 Pilling실적 - 재료정보 파일링실적 시간 등록]["+getL3PlateNo(sMaxPlL2TrkNo,sMaxPlPlateNo)+"]";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            intRtnVal = ydStockDao.update_Dm_Time(recPara,4);
                        }
                    }
                }
            }else{
                /*
                 * L3화면에서 처리되는 경우 : L3 Plate No로 넘어옴
                 */
                String sMaxPlL2TrkNo    = ydDaoUtils.paraRecChkNull(tcRecord, "PL_L2_TRK_NO"    + intPILING_MTL_SH);
                String sMaxPlPlateNo    = ydDaoUtils.paraRecChkNull(tcRecord, "PL_PLATE_NO"     + intPILING_MTL_SH);

                msgRecord.setField("PILING_STL_NO", sMaxPlPlateNo);
                msgRecord.setField("PILING_MTL_SH", "" + intPILING_MTL_SH);         //후판제품Piling매수

                for(int i = 0; i < intPILING_MTL_SH; i++ ) {
                    /*
                     * L2 후판번호 > L3 후판번호 변환작업.
                     */
                    sMaxPlL2TrkNo   = ydDaoUtils.paraRecChkNull(tcRecord, "PL_L2_TRK_NO"    + (i + 1));
                    sMaxPlPlateNo   = ydDaoUtils.paraRecChkNull(tcRecord, "PL_PLATE_NO"     + (i + 1));

                    msgRecord.setField("STL_NO" + (i + 1), sMaxPlPlateNo);

                    /*
                     * 2010.08.02 YJK
                     * 파일링 실적시간 관리 항목
                     */
                    if(i == 0){
                        //--------------------------------------------------------------
                        recPara = JDTORecordFactory.getInstance().create();
                        recPara.setField("STL_NO",          sMaxPlPlateNo);
                        //--------------------------------------------------------------

                        szMsg = "[P2 Pilling실적 - 재료정보 파일링실적 시간 등록]["+sMaxPlPlateNo+"]";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        intRtnVal = ydStockDao.update_Dm_Time(recPara,4);
                    }
                }
            }

            msgRecord.setField("PILING_CMPL_YN", "Y");

            //PILING 재료번호
            szPILING_STL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "PILING_STL_NO");
            if(szPILING_STL_NO.equals("")) {

                szMsg = "[전문 이상] Piling 재료번호가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            //재료매수 CHECK
            intPILING_MTL_SH = ydDaoUtils.paraRecChkNullInt(msgRecord, "PILING_MTL_SH");
            if(intPILING_MTL_SH == 0) {

                szMsg = "[전문 이상] 재료매수가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            //파일링 완료 여부
            szPILING_CMPL_YN = ydDaoUtils.paraRecChkNull(msgRecord, "PILING_CMPL_YN");
            if(szPILING_CMPL_YN.equals("")) {

                szMsg = "[전문 이상] 파일링 완료 여부가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            //저장품 테이블에 해당 재료번호의 데이터가 있는지 체크한다.
            blnRtnVal = this.chkStock(szPILING_STL_NO, rsResult);
            if(!blnRtnVal) {
                return;
            }

            rsResult.first();
            recPara = rsResult.getRecord();

            szYD_PILING_CD      = ydDaoUtils.paraRecChkNull(recPara, "YD_PILING_CD");
            szYD_BOOK_OUT_LOC   = ydDaoUtils.paraRecChkNull(recPara, "YD_BOOK_OUT_LOC");

            /*
             * 2011.11.01 윤재광
             * F동 입고대상재 D동에 가 적치후 다시 F동에서 북아웃요구시 문제해결
             */
            if("77777".equals(szYD_BOOK_OUT_LOC)||"88888".equals(szYD_BOOK_OUT_LOC)){

                szMsg = "[P2 Pilling실적 - procP2PillingWr] 파일링처리시 이전    YD_BOOK_OUT_LOC[" + szYD_BOOK_OUT_LOC + "]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                szYD_BOOK_OUT_LOC   = ydDaoUtils.paraRecChkNull(tcRecord, "YD_BOOK_OUT_LOC");

                if("".equals(szYD_BOOK_OUT_LOC)) {
                    szYD_BOOK_OUT_LOC = "58090";
                }

                szMsg = "[P2 Pilling실적 - procP2PillingWr] 파일링처리시 이후    YD_BOOK_OUT_LOC[" + szYD_BOOK_OUT_LOC + "]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            }

            // 파일링완료 요구 구분항목이 "Y"이면 CARRY_OUT 요구 전문 송신
            if(szPILING_CMPL_YN.equals("Y")) {

                szMsg = "[P2 Pilling실적 - procP2PillingWr] 파일링완료 시 저장품 조회용 재료번호[" + szPILING_STL_NO + "] - YD_BOOK_OUT_LOC[" + szYD_BOOK_OUT_LOC + "], YD_PILING_CD[" + szYD_PILING_CD + "] ";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                /* +++++++++++++ 2009.04.24 임춘수 변경 +++++++++++++++++*/
                //파일링 ZONE NO ==> 저장위치 정보 변환
                arrRT_ZONE_NO = YdCommonUtils.getY4BookOutLoc(szYD_BOOK_OUT_LOC);
                //RT 적치열구분
                szYD_BOOK_OUT_COL_GP = arrRT_ZONE_NO[0];
                //적치베드번호
                szYD_BOOK_OUT_BED_NO = arrRT_ZONE_NO[1];

                szMsg = "[P2 Pilling실적 - procP2PillingWr] BOOK-OUT위치[" + szYD_BOOK_OUT_LOC + "]를 야드저장위치로 변환 후: 적치열[" + szYD_BOOK_OUT_COL_GP + "], 적치베드[" + szYD_BOOK_OUT_BED_NO + "] ";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                /* ++++++++++++++++++++++++++++++++++++++++++++++++++++*/


                //----------------------------------------------------------------------------------------------------

                YdEqpDao   ydEqpDao   = new YdEqpDao();
                JDTORecordSet   outResult   = JDTORecordFactory.getInstance().createRecordSet("");
                JDTORecord      inRecord1   = JDTORecordFactory.getInstance().create();
                JDTORecord      outRecord1      = JDTORecordFactory.getInstance().create();
                String szAPPLY_YN           = "N";

                inRecord1.setField("REPR_CD_GP", YdConstant.YD_GP_PLATE_GDS_YARD + "00100");    //RT 베드 문자 관리

                /*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
                intRtnVal = ydEqpDao.getYdEqp(inRecord1, outResult, 999);
                if(intRtnVal > 0) {
                    outResult.first();
                    outRecord1  = outResult.getRecord();
                    szAPPLY_YN = outRecord1.getFieldString("ITEM1");
                }
                szMsg="RT베드문자 관리 적용 " + szAPPLY_YN ;
                ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);

                if (szAPPLY_YN.equals("Y")) {
                    iTmpSeq = 6;

                    if(szYD_BOOK_OUT_COL_GP.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "DRTR")||
                       szYD_BOOK_OUT_COL_GP.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "ERTR")||
                       szYD_BOOK_OUT_COL_GP.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "FRTR")){
                        iTmpSeq = 14;
                    }else{
                        iTmpSeq = 6;
                    }

                    //대기베드순서를 차례로 조회한다.
                    for(int Loop_i = 1; Loop_i <= iTmpSeq; Loop_i++) {

                        recPara = JDTORecordFactory.getInstance().create();
//  DONG_INSERT:OK
                        szYD_BOOK_OUT_BED_NO = ydDaoUtils.stringPlusNext(szYD_BOOK_OUT_BED_NO, 1);

                        recPara.setField("YD_STK_COL_GP", szYD_BOOK_OUT_COL_GP);
                        recPara.setField("YD_STK_BED_NO", szYD_BOOK_OUT_BED_NO);

                        szMsg = "[P2 Pilling실적 - procP2PillingWr] 야드저장위치 : 가상베드 조회 적치열[" + szYD_BOOK_OUT_COL_GP + "], 적치베드[" + szYD_BOOK_OUT_BED_NO + "] ";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                        //적치열과 베드번호로 적치단을 조회해서 재료가 권상대기 권하대기 적치중인것이 있는 것만 찾는다.빈베드가 적치가능한 대기베드임
                        rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                        /*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrByColGpBedNo*/
                        intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 29);
                        if(intRtnVal == 0) {
                            szMsg = "[P2 Pilling실적 - procP2PillingWr] 적치가능한 대기Bed를 찾았습니다.  " + szYD_BOOK_OUT_COL_GP + "  " + szYD_BOOK_OUT_BED_NO;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            isExistBed = true;
                            break;
                        }
                    }

                } else {
                    iTmpSeq = 6;

                    if(szYD_BOOK_OUT_COL_GP.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "DRTR")||
                       szYD_BOOK_OUT_COL_GP.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "ERTR")||
                       szYD_BOOK_OUT_COL_GP.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "FRTR")){
                        iTmpSeq = 9;
                    }else{
                        iTmpSeq = 6;
                    }

                    //대기베드순서를 차례로 조회한다.
                    for(int Loop_i = 1; Loop_i <= iTmpSeq; Loop_i++) {

                        recPara = JDTORecordFactory.getInstance().create();
//  DONG_INSERT :OK
                        szYD_BOOK_OUT_BED_NO = ydDaoUtils.stringPlusInt2(szYD_BOOK_OUT_BED_NO, 1);

                        recPara.setField("YD_STK_COL_GP", szYD_BOOK_OUT_COL_GP);
                        recPara.setField("YD_STK_BED_NO", szYD_BOOK_OUT_BED_NO);

                        szMsg = "[P2 Pilling실적 - procP2PillingWr] 야드저장위치 : 가상베드 조회 적치열[" + szYD_BOOK_OUT_COL_GP + "], 적치베드[" + szYD_BOOK_OUT_BED_NO + "] ";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                        //적치열과 베드번호로 적치단을 조회해서 재료가 권상대기 권하대기 적치중인것이 있는 것만 찾는다.빈베드가 적치가능한 대기베드임
                        rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                        /*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrByColGpBedNo*/
                        intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 29);
                        if(intRtnVal == 0) {
                            szMsg = "[P2 Pilling실적 - procP2PillingWr] 적치가능한 대기Bed를 찾았습니다.  " + szYD_BOOK_OUT_COL_GP + "  " + szYD_BOOK_OUT_BED_NO;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            isExistBed = true;
                            break;
                        }
                    }
                }


                String szRtnMsg = null;

                if( isExistBed ) {
                    //INSERT항목 레코드 생성
                    recPara = JDTORecordFactory.getInstance().create();
                    recTemp = JDTORecordFactory.getInstance().create();
                    recApTmp= JDTORecordFactory.getInstance().create();
                    //대기베드에 적치한다.
                    for(int Loop_i =intPILING_MTL_SH; Loop_i > 0; Loop_i--) {

                        //적치단 재료상태가 적치 가능이면 재료 등록
                        //적치단 테이블 업데이트
                        //적치열구분 = 설비ID
                        recPara.setField("YD_STK_COL_GP",       szYD_BOOK_OUT_COL_GP);
                        recPara.setField("YD_STK_BED_NO",       szYD_BOOK_OUT_BED_NO);
                        recPara.setField("YD_STK_LYR_NO",       "00" + (Loop_i));
                        recPara.setField("MODIFIER",            szUser);
                        recPara.setField("YD_STK_LYR_MTL_STAT", "C");
                        recPara.setField("STL_NO",              msgRecord.getField("STL_NO" + Loop_i) );

                        //업데이트 실행
                        intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);

                        szMsg = "[P2 Pilling실적 - procP2PillingWr] 대기베드 적치단 Update 성공!";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


                        recApTmp.setField("STL_NO",             "");
                        recApTmp.setField("YD_STK_LYR_MTL_STAT","E");
                        recApTmp.setField("YD_STK_COL_GP",      YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BAP01");
                        recApTmp.setField("STL_NO1",            msgRecord.getField("STL_NO" + Loop_i) );
                        //업데이트 실행
                        intRtnVal = ydStkLyrDao.updYdStklyrWithColStock(recApTmp);

                        /*
                         * Plate공통테이블에 BOOK-OUT위치의 야드저장위치로 업데이트 처리함으로써 입고대상재에서 제거를 시킴
                         * 수정자 : 임춘수
                         * 수정일 : 2009.11.13
                         */
                        recTemp.setField("PLATE_NO",            msgRecord.getField("STL_NO" + Loop_i));
                        recTemp.setField("YD_MTL_ITEM",         "PG");
                        recTemp.setField("YD_STK_COL_GP",       szYD_BOOK_OUT_COL_GP);
                        recTemp.setField("YD_STK_BED_NO",       arrRT_ZONE_NO[1]);
                        recTemp.setField("YD_STK_LYR_NO",       "00" + (Loop_i));

                        szRtnMsg = YdCommonUtils.setYdStrLocToPtComm(recTemp, szMethodName);

                        szMsg = "[P2 Pilling실적 - procP2PillingWr] 재료공통 업데이트 처리 : " + szRtnMsg;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }

                }else{
                    szMsg = "[P2 Pilling실적 - procP2PillingWr] 적치가능한 대기Bed가 존재하지 않습니다.  ";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
            }

        } catch(Exception e) {
            szMsg = "P2 Pilling실적 처리중 Error : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
        szMsg = "[P2 Pilling실적 - procP2PillingWr] 파일링 실적처리 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        return;

    } // end of procP2PillingWr



    /**
     * 오퍼레이션명 : 후판압연전단L2 BOOK_OUT완료수신 (1후판 전용)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procP2BookOutReq(JDTORecord tcRecord)throws JDTOException  {
        /*
         * 업무기준 : 1. BOOK-OUT위치를 야드의 저장위치로 변환
         *           2. 해당 BOOK-OUT위치에 TRANSFER가 존재하는 지 조회
         *           3. 변환된 야드의 저장위치에 가상베드에 등록된 재료정보를 조회 후 변환된 야드의 저장위치에 등록
         *           4. 가상베드들의 정보를 SHIFT 처리
         *           5. 변환된 야드의 저장위치에 등록된 재료들을 작업요구
         * 수정자 : 임춘수
         * 수정일 : 2009.11.06 - 전문LAYOUT 변경
         *
         */
        // 수신전문과 프로그램에서 사용하는 수신항목이 일치하는지 확인
        //
        // Book-Out 실적 TL3COI 29050
        //
        //후판L2제품번호    PL_L2_TRK_NO        VARCHAR2    16
        //후판재료번호      PL_MTL_NO           VARCHAR2    10
        //후판트래킹존번호  PL_TRCK_ZONE_NO     NUMBER      5
        //후판날판위치조정  PL_MPL_LOC_ADJ1     NUMBER      5
        //후판날판위치조정  PL_MPL_LOC_ADJ2     NUMBER      5
        //후판날판위치조정  PL_MPL_LOC_ADJ3     NUMBER      5
        //후판제촌제품두께  PL_MEA_GDS_T        NUMBER      6   3
        //후판제촌제품폭    PL_MEA_GDS_W        NUMBER      5   1
        //후판제촌제품길이  PL_MEA_GDS_L        NUMBER      5
        //후판북아웃위치    PL_BOOK_OUT_LOC     NUMVER      1
        //후판압연패스수    PL_ROLLING_PASS_CNT NUMBER      2
        //후판북아웃모드    PL_BOOK_OUT_MOD     VARCHAR2    1
        //후판북아웃일시    PL_BOOK_OUT_DATE    DATE
        //후판북아웃사유    PL_BOOK_OUT_RSN     VARCHAR2    1

        YdStkLyrDao ydStkLyrDao    = new YdStkLyrDao();
        YdDaoUtils ydDaoUtils      = new YdDaoUtils();
        YdStockDao ydStockDao      = new YdStockDao();
        YdDelegate ydDelegate      = new YdDelegate();

        boolean blnRtnVal       = false;
        int intRtnVal           = 0;
        String szMsg            = "";
        String szMethodName     = "procP2BookOutReq";
        String szOperationName  = "후판압연전단L2 BOOK_OUT완료수신";

        //레코드 선언
        JDTORecord recPara      = null;
        JDTORecord recInPara    = null;
        JDTORecord recOutPara   = null;
        JDTORecordSet rsResult  = null;
        JDTORecord recEdit      = null;

        String szYD_BOOK_OUT_LOC    = null;
        int intMtlCnt               = 0;
        String szCARRY_OUT_REQ_GP   = null;
        String[] szSTL_NO           = null;
        String szDate               = null;
        String szYD_STK_COL_GP      = null;
        String szYD_STK_BED_NO      = null;
        String szYD_STK_COL_GP1     = null;
        String szYD_STK_BED_NO1     = null;
        String szYD_STK_LYR_NO1     = null;
        String[] arrRT_ZONE_NO      = null;
        String szPL_MTL_NO          = null;
        //후판트래킹존번호
        String szPL_TRCK_ZONE_NO    = null;
        //입고예정저장위치
        String szYD_RCPT_PLN_STR_LOC= null;
        //길이구분
        String szYD_MTL_L_GP        = null;
        int intYD_STK_BED_STL_SH    = 0;

        YdPlateCommDAO commDao = new YdPlateCommDAO();

        JDTORecord msgRecord = JDTORecordFactory.getInstance().create();
        JDTORecord inRecord = JDTORecordFactory.getInstance().create();


        // 2021. 4. 16 전사물류개선
        // 동일북아웃위치 & 동일파일링이 아닐 경우 생기는 문제 패치
        // 복구를 위한 크레인스케쥴 및 저장위치 정보를 미리 조회한다.
        JDTORecordSet orgPlingCompledStl = JDTORecordFactory.getInstance().createRecordSet("ydPlate");
        JDTORecordSet orgPlingCrnInfo = JDTORecordFactory.getInstance().createRecordSet("ydPlate");


        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(tcRecord);
        //에러 리턴
        if(szRcvTcCode == null || szRcvTcCode.equals("") ) {
            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }
        //TC CODE DISPLAY
        if(bDebugFlag) {
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        try{
            //=============================================================
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판압연전단L2] BOOK_OUT완료 수신";
            ydUtils.putLogMsg("D", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            szMsg = "[후판창고야드BOOK_OUT완료수신 - procP2BookOutReq]--------------------- 전문 내용 시작 --------------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //L3 후판번호이다  procP2BookOutReq 호출시 이미 getL3PlateNo(szPL_L2_TRK_NO,szPL_MTL_NO) 수행하여 변환된값이 들어 있다.
            szPL_MTL_NO = ydDaoUtils.paraRecChkNull(tcRecord,"PL_MTL_NO");

            szMsg = "[후판창고야드BOOK_OUT완료수신 - procP2BookOutReq]--------------------- 전문 내용 끝 --------------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            msgRecord.setField("YD_BOOK_OUT_LOC", ydDaoUtils.paraRecChkNull(tcRecord,"PL_TRCK_ZONE_NO"));

            szPL_TRCK_ZONE_NO = ydDaoUtils.paraRecChkNull(tcRecord,"PL_TRCK_ZONE_NO");

            //받은 전문 편집
            //BOOK_OUT 위치
            szYD_BOOK_OUT_LOC       = ydDaoUtils.paraRecChkNull(msgRecord,"YD_BOOK_OUT_LOC");

            if(szYD_BOOK_OUT_LOC.equals("")) {

                szMsg = "[전문 이상] BOOK_OUT 위치가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            szCARRY_OUT_REQ_GP = "Y";

            rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            //저장품 테이블에 해당 재료번호의 데이터가 있는지 체크한다.
            blnRtnVal = this.chkStock(szPL_MTL_NO, rsResult);
            if(!blnRtnVal) {
                return;
            }

            szMsg = "[저장품검색] 저장품 검색 대상 정보[rsResult]"+rsResult;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            rsResult.first();
            recInPara = rsResult.getRecord();

            szMsg = "[저장품검색] 저장품 검색 대상 정보[recInPara]"+recInPara;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szYD_MTL_L_GP           = ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");
            szYD_RCPT_PLN_STR_LOC   = ydDaoUtils.paraRecChkNull(recInPara, "YD_RCPT_PLN_STR_LOC");


            szMsg = "szYD_BOOK_OUT_LOC:"+szYD_BOOK_OUT_LOC;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


            /* +++++++++++++ 2009.04.24 임춘수 변경 +++++++++++++++++*/
            //파일링 ZONE NO ==> 저장위치 정보 변환
            arrRT_ZONE_NO       = YdCommonUtils.getY4PilingZoneNo2StrLoc(szYD_BOOK_OUT_LOC);
            //RT 적치열구분
            szYD_BOOK_OUT_LOC   = arrRT_ZONE_NO[0] + arrRT_ZONE_NO[1];
            szYD_STK_COL_GP     = arrRT_ZONE_NO[0];
            szYD_STK_BED_NO     = arrRT_ZONE_NO[1];

            szMsg = "[후판창고야드BOOK_OUT완료수신 - procP2BookOutReq] BOOK-OUT LOC[" + szYD_BOOK_OUT_LOC + "] ===> 야드저장위치 : 적치열[" + szYD_STK_COL_GP + "], 적치베드[" + szYD_STK_BED_NO + "]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            /* ++++++++++++++++++++++++++++++++++++++++++++++++++++*/

            /* ++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            recInPara = JDTORecordFactory.getInstance().create();
            recInPara.setField("STL_NO", szPL_MTL_NO);
            recInPara.setField("YD_STK_LYR_MTL_STAT", "");
            //AT000 2022.11.24 물류시스템 개선 D,E R/T에서 Book Out 처리 시 후판 정정 저장위치 검색 방지를 위해
            // com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO 변경
            //intRtnVal = ydStkLyrDao.getYdStklyr(recInPara, rsResult, 3);
            intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO4");

            if( intRtnVal <= 0 ) {

                //1후판에서는 낱장 단위로 R/T에 흐를 경우 BOOK-OUT될 가상버퍼에 미리 넣어 두지 못 함으로
                //여기서 PILING 실적 처리를 해서 가상버퍼에 넣는 작업을 한다.

                szMsg = "[후판창고야드BOOK_OUT완료수신 - procP2BookOutReq] 적치단에 재료정보["+szPL_MTL_NO+"]가 없습니다. Error Code : " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("JMS_TC_CD",       "P2YDL001");
                recPara.setField("PL_PLATE_NO1",    szPL_MTL_NO);
                recPara.setField("PL_GDS_PILNG_SH", "1");
                /*
                 * PILING 실적 처리 백업
                 * PILING 실적없이 바로 BOOK OUT 요구가 오는 경우에 처리.
                 */
                this.procP2PillingWr(recPara);

                //intRtnVal = ydStkLyrDao.getYdStklyr(recInPara, rsResult, 3);
                intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO4");

                if (intRtnVal <= 0) {
                    szMsg = "[후판창고야드BOOK_OUT완료수신 - procP2BookOutReq] Piling 처리후 적치단에 재료정보["+szPL_MTL_NO+"]가 없습니다. Error Code : " + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    return;
                }

            }

            szMsg = "[후판창고야드BOOK_OUT완료수신 - procP2BookOutReq] 적치단에 재료정보["+szPL_MTL_NO+"]가 존재합니다.";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            rsResult.first();
            recInPara = rsResult.getRecord();

            szYD_STK_COL_GP1 = ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");
            szYD_STK_BED_NO1 = ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");
            szYD_STK_LYR_NO1 = ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_LYR_NO");

            szMsg = "[후판창고야드BOOK_OUT완료수신 - procP2BookOutReq] 재료정보["+szPL_MTL_NO+"]가 존재하는 적치열["+szYD_STK_COL_GP1+"], 적치베드["+szYD_STK_BED_NO1+"]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            if(!"RT".equals(szYD_STK_COL_GP1.substring(2,4))&&
               !"TF".equals(szYD_STK_COL_GP1.substring(2,4))){

                szMsg = "[저장위치에러] BOOK OUT 대상 저장위치가 RT/TF가 아닙니다.[윤재광]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            if(!"001".equals(szYD_STK_LYR_NO1)){
                szMsg = "[저장위치에러] BOOK OUT 대상 저장위치가 SKIP 대상입니다 .[윤재광][SMS때문에..짜증..]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }
            /* ++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            String szYD_SCH_ST_GP = ""; // 작업예약생성시 파일링 작업지시인지를 구분하는 항목

            //NEW Version : 크래인 파일링 ON,OFF-LINE 모두 적용 적용, 낱장+여러장 가능
            //--2013.04.09 szYD_STK_COL_GP 값이 "KBTF02"와 같이 Transfer 일 수 도 있다.
            //  --> 이런경우 입력 파라메터 tcRecord 로 전달받은 PL_TRCK_ZONE_NO(ex:56216) 으로
            //  --> YdCommonUtils.getY4BookOutLoc 을 실행하여 BOOK-OUT 위치를 다시 구한다.
            //  -->  원래 R/T상의 위치는 그대로 나오고 TF 일경우는 TF와 붙어있는 R/T 의 위치가 나온다.
            //  -->  이 값을 가지고 아래 로직을 수행한다. (ex: "KBTF02" --> "KBRTRA" )
            arrRT_ZONE_NO   = YdCommonUtils.getY4BookOutLoc(szPL_TRCK_ZONE_NO);
            String szRT_STK_COL_GP = arrRT_ZONE_NO[0];

            if(szRT_STK_COL_GP.endsWith("RA")||szRT_STK_COL_GP.endsWith("RB")||szRT_STK_COL_GP.endsWith("RC")||
               szRT_STK_COL_GP.endsWith("RD")||szRT_STK_COL_GP.endsWith("RE")||szRT_STK_COL_GP.endsWith("RF")){

                //CRANE PILING 시 사용하는 각 동, 각 RT 라인의  가상 AutoPiler 버퍼을 구분하기 위한 Bed No를 구함
                // ex) D동  A-RT 는 KDAP02 의 02 BED
                String szTempBedNo = "01";

                if(szRT_STK_COL_GP.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD)) {
                    //2후판일경우 RT 구분 A,B,C
                    if(szRT_STK_COL_GP.endsWith("RA")) {
                        szTempBedNo = "01";
                    } else if(szRT_STK_COL_GP.endsWith("RB")){
                        szTempBedNo = "02";
                    } else if(szRT_STK_COL_GP.endsWith("RC")){
                        szTempBedNo = "03";
                    } else if(szRT_STK_COL_GP.endsWith("RD")){
                        szTempBedNo = "04";
                    } else if(szRT_STK_COL_GP.endsWith("RE")){
                        szTempBedNo = "05";
                    } else if(szRT_STK_COL_GP.endsWith("RF")){
                        szTempBedNo = "06";
                    }
                } else {
                    //1후판일경우 통합 전 A,B,C 인데 B가 01로 이전 부터 사용하고 있어서 B를 01로 함
                    //통합 후는 D,E,F 로 됨
                    if(szRT_STK_COL_GP.endsWith("RB")) {
                        szTempBedNo = "01";
                    } else if(szRT_STK_COL_GP.endsWith("RA")){
                        szTempBedNo = "02";
                    } else if(szRT_STK_COL_GP.endsWith("RC")){
                        szTempBedNo = "03";
                    } else if(szRT_STK_COL_GP.endsWith("RD")){
                        szTempBedNo = "04";
                    } else if(szRT_STK_COL_GP.endsWith("RE")){
                        szTempBedNo = "05";
                    } else if(szRT_STK_COL_GP.endsWith("RF")){
                        szTempBedNo = "06";
                    }
                }

                // 2021. 4. 16
                // 만약 파일링이 진행중이라면 삭제 전 이전 정보를 갖고 있자.
                JDTORecord jtoPilingCheckParam = JDTORecordFactory.getInstance().create();
                jtoPilingCheckParam.setField("REQ_YD_STK_COL_GP", szRT_STK_COL_GP);
                jtoPilingCheckParam.setField("REQ_PLATE_NO", szPL_MTL_NO);
                jtoPilingCheckParam.setField("REQ_YD_STK_BED_NO", szTempBedNo);
                commDao.select(jtoPilingCheckParam, orgPlingCompledStl, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getPilingInfoByDefault");

                // 현재 크레인스케쥴 정보를 담는다.(나중에 복구용)
                jtoPilingCheckParam = JDTORecordFactory.getInstance().create();
                jtoPilingCheckParam.setField("YD_STK_COL_GP",   szYD_STK_COL_GP);
                jtoPilingCheckParam.setField("YD_STK_BED_NO",       szTempBedNo);
                commDao.select(jtoPilingCheckParam, orgPlingCrnInfo, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getPilingEndCheckByCrnsch");


                szMsg = "[Jsp-Session "+szSessionName+" ] szPL_MTL_NO:" + szPL_MTL_NO + " , szRT_STK_COL_GP : " +  szRT_STK_COL_GP + " , szTempBedNo : " + szTempBedNo;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                Object[] inParam = {
                                     szPL_MTL_NO
                                    ,szRT_STK_COL_GP
                                    ,szTempBedNo
                                   };

                int[] inParamIndex = {1,2,3};
                JDTORecord record = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.callSpQueryId_0001");

//              220719 박성열 8월 가동 예정 --> 2022.11.16 신책임 요청 으로 막음
//              JDTORecord record = JDTORecordFactory.getInstance().create();
//              if ("RT".equals(szRT_STK_COL_GP.substring(2,4)))
//              {
//                  record = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.callSpQueryId_0008");
//              }
//              else
//              {
//                  record = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.callSpQueryId_0001");
//              }


                if(record.size() > 0){
                    /*
                     * T : 파일링가능, F : 파일링 SKIP   -- 파일링정보가 없을 경우.
                     * S : 파일링가능, E : 파일링 SKIP   -- 파일링정보가 있는 경우.
                     */
                    szYD_SCH_ST_GP = ydDaoUtils.paraRecChkNull(record, "OUT_RTN_CODE");
                }
                szMsg = "[Jsp-Session "+szSessionName+" ] OUT_RTN_CODE="+szYD_SCH_ST_GP ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            }
            //-------------------------------------------------------------------------------------------------end

            rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            recInPara = JDTORecordFactory.getInstance().create();
            recInPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP1);
            recInPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO1);

            /**
             * 2021. 04. 15 전사물류개선
             *  - 구분자가 파일링 종료되는 시점에 다음과 같이 한 번 더 판별한다.
             *    T : 파일링가능, F : 파일링 SKIP   -- 파일링정보가 없을 경우.
             *    S : 파일링가능, E : 파일링 SKIP   -- 파일링정보가 있는 경우.
             *  - Check Point
             *   1. 동일한 파일링 코드 및 동일한 파일링 위치인가?
             *   2. 변경전 크레인스케쥴이 파일중인가?
             *   3. 변경전 크레인스케쥴의 야드진행상태가 권상인가?
             *
             *
             *  ** 제품번호로 조회한 YD적치열테이블이 결과 값, 가상베드의 주소가 들어 있다
             *  szYD_STK_COL_GP1, szYD_STK_BED_NO1
             */
            if(PlateGdsYdUtil.isApplyYn("파일링오류패치적용여부")){

                if("F".equals(szYD_SCH_ST_GP) || "E".equals(szYD_SCH_ST_GP)){

                    szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 파일링중인 스케쥴에 대해서만 종료시점 Check";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                    JDTORecord jtoPilingCheckParam = JDTORecordFactory.getInstance().create();
                    JDTORecordSet jtoPilingCheckRst = JDTORecordFactory.getInstance().createRecordSet("ydPlate");

                    szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 파일링중인 스케쥴의 북아웃정보, 요청한 북아웃정보 비교시작";
                    if( orgPlingCompledStl != null && orgPlingCompledStl.size() > 0){


//                      String szREQ_PL_RCPT_TRK_NO      = jtoPilingCheckRst.getRecord(0).getFieldString("REQ_PL_RCPT_TRK_NO");
                        String szREQ_YD_PILING_CD        = StringHelper.nvl(orgPlingCompledStl.getRecord(0).getFieldString("REQ_YD_PILING_CD"),"REQ");
                        String szREQ_YD_BOOK_OUT_LOC     = StringHelper.nvl(orgPlingCompledStl.getRecord(0).getFieldString("REQ_YD_BOOK_OUT_LOC"),"REQ");
                        String szREQ_YD_RCPT_PLN_STR_LOC = StringHelper.nvl(orgPlingCompledStl.getRecord(0).getFieldString("REQ_YD_RCPT_PLN_STR_LOC"),"REQ");

//                      String szTO_PL_RCPT_TRK_NO          = jtoPilingCheckRst.getRecord(0).getFieldString("PL_RCPT_TRK_NO         ");
                        String szTO_YD_PILING_CD            = StringHelper.nvl(orgPlingCompledStl.getRecord(0).getFieldString("YD_PILING_CD"),"TO");
                        String szTO_YD_BOOK_OUT_LOC         = StringHelper.nvl(orgPlingCompledStl.getRecord(0).getFieldString("YD_BOOK_OUT_LOC"),"TO");
                        String szTO_YD_RCPT_PLN_STR_LOC     = StringHelper.nvl(orgPlingCompledStl.getRecord(0).getFieldString("YD_RCPT_PLN_STR_LOC"),"TO");

                        szMsg += "\n 북아웃 정보 비교 시작(요청vs파일링완료)";
                        szMsg += "\n 북아웃 정보 비교 동일파링 :: ["+szREQ_YD_PILING_CD+":VS:"+szTO_YD_PILING_CD+"]";
                        szMsg += "\n 북아웃 정보 비교 동일북아웃위치 :::: ["+szREQ_YD_BOOK_OUT_LOC+":VS:"+szTO_YD_BOOK_OUT_LOC+"]";
                        szMsg += "\n 북아웃 정보 비교 동일한 야드저장위치 :: :: ["+szREQ_YD_RCPT_PLN_STR_LOC+":VS:"+szTO_YD_RCPT_PLN_STR_LOC+"]";
                        szMsg += "\n 북아웃 정보 비교 종료";

                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

                        // 동일 파일링, 동일 북아웃위치, 동일 야드저장위치까지 다 확인하자!
                        if(
                                !(szREQ_YD_PILING_CD.equals(szTO_YD_PILING_CD)
                                && szREQ_YD_BOOK_OUT_LOC.equals(szTO_YD_BOOK_OUT_LOC)
//                              && szREQ_YD_RCPT_PLN_STR_LOC.equals(szTO_YD_RCPT_PLN_STR_LOC)) 예정위치는 바뀔 수 있다.
                                )
                        ){

                            szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 파일링이 되지 않은 재료가 북아웃되어왔음 관련 스케쥴 및 작업예약 체크";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

                            // 혹시 크레인스케쥴이 삭제되었는지 확인한다.
                            // 아래 To Bed의 위치에 적재된 재료가 크레인스케쥴 파일링이 취소되었다면
                            // 해당 파일링은 재 작업지시를 내린다.
                            // 삭제되었는지 체크
                            szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 파일링 스케쥴이 삭제되었는가 확인";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                            if(orgPlingCrnInfo != null && orgPlingCrnInfo.size() > 0){
                                String szYD_CRN_SCH_ID = orgPlingCrnInfo.getRecord(0).getFieldString("YD_CRN_SCH_ID");
                                jtoPilingCheckParam = JDTORecordFactory.getInstance().create();
                                jtoPilingCheckParam.setField("YD_CRN_SCH_ID",   szYD_CRN_SCH_ID);   //북아웃위치 적치열구분(TO) - RT,TF

                                if(commDao.select(jtoPilingCheckParam, jtoPilingCheckRst, "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnsch")>0){

                                    szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 스케쥴ID["+szYD_CRN_SCH_ID+"] 삭제되어 다시 원복 후 재작업지시(파일링종료) 처리";
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

                                    szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 크레인작업내역 복구 시작 ";
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                                    String szYD_WBOOK_ID = orgPlingCrnInfo.getRecord(0).getFieldString("YD_WBOOK_ID");
                                    jtoPilingCheckParam.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
                                    commDao.update(jtoPilingCheckParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdWrkBookMtlOnByPiling");
                                    commDao.update(jtoPilingCheckParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdWrkBookOnByPiling");

                                    jtoPilingCheckParam.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
                                    commDao.update(jtoPilingCheckParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCrnSchMtlOnByPiling");
                                    commDao.update(jtoPilingCheckParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCrnSchOnByPiling");
                                    szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 크레인작업내역 복구 종료 ";
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

                                    szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 크레인설비 상태  YD_EQP_STAT 복구 시작 ";
                                    jtoPilingCheckParam.setField("YD_EQP_STAT", orgPlingCrnInfo.getRecord(0).getFieldString("YD_EQP_STAT"));
                                    jtoPilingCheckParam.setField("YD_EQP_ID", orgPlingCrnInfo.getRecord(0).getFieldString("YD_EQP_ID"));
                                    commDao.update(jtoPilingCheckParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdEqpRepairePiling");
                                    szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 크레인설비 상태  YD_EQP_STAT 복구 종료 ";
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

                                    szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 적치단(tb_yd_stklyr) 복구 시작 ";
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                                    for( int i=0; i < orgPlingCrnInfo.size(); i++){
                                        jtoPilingCheckParam = JDTORecordFactory.getInstance().create();
                                        jtoPilingCheckParam.setField("STL_NO", orgPlingCrnInfo.getRecord(i).getFieldString("STL_NO"));

                                        szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 적치단(tb_yd_stklyr) Clear By STL_NO :: " +jtoPilingCheckParam.getFieldString("STL_NO") ;
                                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

                                        // 변경전 파일링 재료 초기화
                                        commDao.update(jtoPilingCheckParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdStkLyrClearByPiling");
                                    }

                                    for( int i=0; i < orgPlingCrnInfo.size(); i++){
                                        jtoPilingCheckParam = JDTORecordFactory.getInstance().create();

                                        jtoPilingCheckParam.setField("YD_STK_COL_GP", orgPlingCrnInfo.getRecord(i).getFieldString("YD_STK_COL_GP"));
                                        jtoPilingCheckParam.setField("YD_STK_BED_NO", orgPlingCrnInfo.getRecord(i).getFieldString("YD_STK_BED_NO"));
                                        jtoPilingCheckParam.setField("YD_STK_LYR_NO", orgPlingCrnInfo.getRecord(i).getFieldString("YD_STK_LYR_NO"));

                                        jtoPilingCheckParam.setField("STL_NO", orgPlingCrnInfo.getRecord(i).getFieldString("STL_NO"));
                                        jtoPilingCheckParam.setField("YD_STK_LYR_MTL_STAT", orgPlingCrnInfo.getRecord(i).getFieldString("YD_STK_LYR_MTL_STAT"));

                                        szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 복구처리"
                                            + "\n YD_STK_COL_GP :: " + jtoPilingCheckParam.getField("YD_STK_COL_GP")
                                            + "\n YD_STK_BED_NO :: " + jtoPilingCheckParam.getField("YD_STK_BED_NO")
                                            + "\n YD_STK_LYR_NO :: " + jtoPilingCheckParam.getField("YD_STK_LYR_NO")
                                            + "\n STL_NO :: " + jtoPilingCheckParam.getField("STL_NO")
                                            + "\n YD_STK_LYR_MTL_STAT :: " + jtoPilingCheckParam.getField("YD_STK_LYR_MTL_STAT")
                                            ;

                                        // 삭제된 파일링 재료 복구처리
                                        commDao.update(jtoPilingCheckParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdStkLyrByPiling");
                                    }
                                    szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq]  적치단(tb_yd_stklyr) 복구 종료 ";
                                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


                                    // 파일링 완료 작업지시 전문을 전송처리한다.
                                    jtoPilingCheckParam = JDTORecordFactory.getInstance().create();
                                    jtoPilingCheckRst = JDTORecordFactory.getInstance().createRecordSet("ydPlate");

                                    jtoPilingCheckParam.setField("MSG_ID", "YDY8L004");
                                    jtoPilingCheckParam.setField("YD_CRN_SCH_ID",       szYD_CRN_SCH_ID);
                                    jtoPilingCheckParam.setField("YD_WRK_PROG_STAT",    orgPlingCrnInfo.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
                                    jtoPilingCheckParam.setField("YD_SCH_CD",           orgPlingCrnInfo.getRecord(0).getFieldString("YD_SCH_CD"));
                                    jtoPilingCheckParam.setField("YD_GP",               YdConstant.YD_GP_PLATE2_GDS_YARD);

                                    // 자동화대상 전문 분리
                                    if(PlateGdsYdUtil.isSendToEaiY9_ydEqpId(orgPlingCrnInfo.getRecord(0).getFieldString("YD_EQP_ID"))){
                                        MakeTcY9.makeY9L004(jtoPilingCheckParam, jtoPilingCheckRst);
                                    }
                                    else{
                                        MakeTcY8.makeY8L004(jtoPilingCheckParam, jtoPilingCheckRst);
                                    }

                                    if(jtoPilingCheckRst != null ){

                                        JDTORecord sendMsgYDY8L004 = null;
                                        sendMsgYDY8L004 = (JDTORecord)jtoPilingCheckRst.getRecord(0);
                                        if("YDY9L004".equals(sendMsgYDY8L004.getFieldString("MSG_ID"))){
                                            sendMsgYDY8L004.setField("YD_WRK_PROG_STAT", "5"); // Y9강제종료는 5번으로 협의함(장치영이사, 조민주부장)
                                        }
                                        ydDelegate.sendMsg_NoMakeTc(sendMsgYDY8L004);

                                        szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 스케쥴ID["+szYD_CRN_SCH_ID+"] 재작업지시(파일링종료) 전문전송(No_Make_TC)";
                                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            /* com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrByColGpBedNoLike */
            intRtnVal = ydStkLyrDao.getYdStklyr(recInPara, rsResult, 75);
            if(intRtnVal <= 0) {
                szMsg = "[후판창고야드BOOK_OUT완료수신 - procP2BookOutReq] 대기베드[적치열:"+szYD_STK_COL_GP1+", 적치베드:"+szYD_STK_BED_NO1+"]에 재료정보가 없습니다. Error Code : " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            intMtlCnt = rsResult.size();

            szMsg = "[후판창고야드BOOK_OUT완료수신 - procP2BookOutReq] 대기베드[적치열:"+szYD_STK_COL_GP1+", 적치베드:"+szYD_STK_BED_NO1+"]에 재료가 존재합니다 - 대상재건수 : " + intRtnVal;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            szSTL_NO  = new String[intMtlCnt + 1];

            //New Version--------------------------------------------------------------------------
            for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
                rsResult.absolute(Loop_i);
                //RT가상버퍼에서 RT상의 실제 Book_Out 위치에 재료 등록 후 RT 가상 버퍼 Clear
                recOutPara = rsResult.getRecord();

                szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recOutPara, "STL_NO");

                if( !szSTL_NO[Loop_i].equals("") ) {
                    intYD_STK_BED_STL_SH++;
                }

                szMsg = "[후판창고야드BOOK_OUT완료수신 - procP2BookOutReq] intYD_STK_BED_STL_SH : " + intYD_STK_BED_STL_SH;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                if(Loop_i == 1) { //1회만 수행  쿼리참고바람

                    recPara = JDTORecordFactory.getInstance().create();
                    recPara.setField("FROM_STK_COL_GP", szYD_STK_COL_GP1);  //가상버퍼 적치열구분(FROM)
                    recPara.setField("TO_STK_COL_GP",   szYD_STK_COL_GP);   //북아웃위치 적치열구분(TO) - RT,TF
                    recPara.setField("FROM_BED_NO",     szYD_STK_BED_NO1);  //가상버퍼 적치열구분(FROM)
                    recPara.setField("TO_BED_NO",       szYD_STK_BED_NO);   //북아웃위치 적치열구분(TO) - RT,TF

                    intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0006");

                    if(intRtnVal < 1) {
                        szMsg = "[후판창고야드BOOK_OUT완료수신 - procP2BookOutReq] BOOK-OUT위치 Update ERROR 위치 - 반환값 : " + intRtnVal;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        return;
                    }

                    intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0007");

                    if(intRtnVal < 1) {
                        szMsg = "[후판창고야드BOOK_OUT완료수신 - procP2BookOutReq] 대기베드 재료정보 Clear Update ERROR 위치: " + intRtnVal;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        return;
                    }
                }
            }
            //-------------------------------------------------------------------------------------

            //------------------------------------------------------------------
            // book out 처리시 flex 데이터 전송
            //------------------------------------------------------------------
            JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
            recFlex.setField("YD_GP",  YdConstant.YD_GP_PLATE_GDS_YARD);
            recFlex.setField("YD_UP_WR_LOC", szYD_STK_COL_GP+szYD_STK_BED_NO );
            ydUtils.putYdFlexCrnWrk("", recFlex);

            //----------------------------------------------------------------------------------------------------
            //  후판제품야드L2로 저장품제원 정보 전송
            //----------------------------------------------------------------------------------------------------
            szMsg = "[후판창고야드BOOK_OUT완료수신 - procP2BookOutReq] 적치열[" + szYD_STK_COL_GP + "], 적치베드[" + szYD_STK_BED_NO + "] - 저장품제원 전송 시작 ";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
            recInTemp.setField("YD_INFO_SYNC_CD",   "4");                       //1:동,2:SPAN,3:열,4:BED
            recInTemp.setField("YD_GP",             szYD_STK_COL_GP.substring(0, 1));
            recInTemp.setField("YD_STK_COL_GP",     szYD_STK_COL_GP);
            recInTemp.setField("YD_STK_BED_NO",     szYD_STK_BED_NO);

            YdCommonUtils.sndStockSpecToL2(recInTemp);

            szMsg = "[후판창고야드BOOK_OUT완료수신 - procP2BookOutReq] 적치열[" + szYD_STK_COL_GP + "], 적치베드[" + szYD_STK_BED_NO + "] - 저장품제원 전송 완료 ";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //----------------------------------------------------------------------------------------------------

            YdCommonUtils.procShiftBedInfoForBookoutLocNew(szYD_STK_COL_GP1 + szYD_STK_BED_NO1);

            // BOOK-OUT완료 요구 구분항목이 "Y"이면 BOOK-OUT 요구 전문 송신
            if(szCARRY_OUT_REQ_GP.equals("Y")) {

                if(!szYD_STK_COL_GP.substring(2, 4).equals("RT") &&
                   !szYD_STK_COL_GP.substring(2, 4).equals("TF")) {
                    arrRT_ZONE_NO   = YdCommonUtils.getY4BookOutLoc(szPL_TRCK_ZONE_NO);
                    szYD_STK_COL_GP = arrRT_ZONE_NO[0];
                }

                //전문 발생 일시
                szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
                //큐전송 항목 저장 레코드 생성
                recPara = JDTORecordFactory.getInstance().create();
                //JMS TC CODE
                recPara.setField("JMS_TC_CD",          "YDYDJ203");
                //발생 일시
                recPara.setField("JMS_TC_CREATE_DDTT", szDate);
                //적치열구분
                recPara.setField("YD_EQP_ID",          szYD_STK_COL_GP);
                //적치BED번호
                recPara.setField("YD_STK_BED_NO",      szYD_STK_BED_NO);
                //입고예정저장위치
                recPara.setField("YD_RCPT_PLN_STR_LOC",szYD_RCPT_PLN_STR_LOC);
                //적치재료매수
                recPara.setField("YD_STK_BED_STL_SH",  "" + intYD_STK_BED_STL_SH);
                //대표재료의 길이구분
                recPara.setField("YD_MTL_L_GP",  szYD_MTL_L_GP);
                //작업예약생성시 파일링 작업지시인지를 구분하는 항목(2012.06.04 윤재광 추가)
                recPara.setField("YD_SCH_ST_GP",  szYD_SCH_ST_GP);

                JDTORecord recPtPara = JDTORecordFactory.getInstance().create();
                recPtPara.setField("JMS_TC_CD",          "YDYDJ297");
                recPtPara.setField("YD_STK_BED_STL_SH",  "" + intYD_STK_BED_STL_SH);

                //재료번호
                for (int Loop_i = 1; Loop_i <= intYD_STK_BED_STL_SH; Loop_i++) {
                    //재료번호
                    recPara.setField("STL_NO" + Loop_i, szSTL_NO[Loop_i]);
                    //재료번호
                    recPtPara.setField("STL_NO" + Loop_i, szSTL_NO[Loop_i]);
                    //권상모음순서
                    recPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + (intMtlCnt - Loop_i + 1));

                    //2010.12.24 윤재광 - 후판공통 이송일자/시간 업데이트
                    recEdit = JDTORecordFactory.getInstance().create();
                    recEdit.setField("PLATE_NO",    szSTL_NO[Loop_i]);
                    recEdit.setField("WH_FTMV_GP",  (ydDaoUtils.paraRecChkNull(msgRecord,"YD_BOOK_OUT_LOC").startsWith("56")?"A":"B"));
                    intRtnVal = ydStockDao.update_Dm_Time(recEdit,5);
                    /* ++++++++++++++++++++++++++++++++++++++++++++++++++++*/
                }

                // 진행관리 오버롤체크
                {
                    //전문 송신
                    ydDelegate.sendMsg(recPtPara);
                    szMsg = "A후판 창고 야드 BOOK_OUT 처리 완료후 오버롤 체크완료!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                }
                // 야드관리 북아웃요구
                {
                    //전문 송신
                    //ydDelegate.sendMsg(recPara);
                    this.procY4CarryOutReq(recPara);

                    szMsg = "A후판 창고 야드 BOOK_OUT 처리 완료후 CARRY_OUT 송신 완료!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                }
            }   // end if

        } catch (Exception e) {

            szMsg = "A후판 창고 야드 BOOK_OUT 처리중 ERROR : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(getClass().getName() + e.getMessage(), e);

        }   // end try catch

    } // end of procP2BookOutReq()


    /**
     * 오퍼레이션명 : 후판제품창고L2 BOOK_OUT 요청 수신 (2후판 전용)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws DAOException
     */
    public void procY8BookOutReq(JDTORecord tcRecord)throws DAOException  {

        // Book-Out 요청  Y8YDL012
        // CURR_LOC             현재위치            VARCHAR2(8)     RT상의 BOOK-OUT 할 현재 위치
        // MTL_SH               재료매수            VARCHAR2(2)     BOOK-OUT 위치의 재료 매수 (01~06)
        // STL_NO1              1단_재료번호        VARCHAR2(11)    최하단 재료번호
        // STL_NO2              2단_재료번호        VARCHAR2(11)    2단 재료번호
        // STL_NO3              3단_재료번호        VARCHAR2(11)    3단 재료번호
        // STL_NO4              4단_재료번호        VARCHAR2(11)    4단 재료번호
        // STL_NO5              5단_재료번호        VARCHAR2(11)    5단 재료번호
        // STL_NO6              6단_재료번호        VARCHAR2(11)    6단 재료번호
        // TRCK_MTL_SH          트래킹재료매수      VARCHAR2(2)     트래킹 정보 개수
        // TRCK_STL_NO1         트래킹재료번호1 VARCHAR2(11)    BOOK-OUT 위치에서 입고존 방향을 1번째 재료가 있는 위치의 최상단 재료번호
        // TRCK_MTL_SH1         트래킹파일링매수1  VARCHAR2(2)     재료매수
        //    :
        // TRCK_STL_NO6         트래킹재료번호6 VARCHAR2(11)    BOOK-OUT 위치에서 입고존 방향을 6번째 재료가 있는 위치의 최상단 재료번호
        // TRCK_MTL_SH6         트래킹파일링매수6  VARCHAR2(2)     재료매수


        YdStkLyrDao ydStkLyrDao    = new YdStkLyrDao();
        YdDaoUtils ydDaoUtils      = new YdDaoUtils();
        YdStockDao ydStockDao      = new YdStockDao();
        YdDelegate ydDelegate = new YdDelegate();

        boolean blnRtnVal   = false;
        int intRtnVal       = 0;
        String szMsg        = "";
        String szMethodName = "procY8BookOutReq";
        String szOperationName = "후판제품창고L2 BOOK_OUT요청수신";

        //레코드 선언
        JDTORecord recPara  = null;
        JDTORecord recInPara  = null;
        JDTORecord recOutPara  = null;
        JDTORecordSet rsResult = null;
        JDTORecord recEdit = null;

        String szYD_BOOK_OUT_LOC   = null;

        int intMtlCnt              = 0;
        String[] szSTL_NO          = null;
        String szDate              = null;
        String szYD_STK_COL_GP = null;
        String szYD_STK_BED_NO = null;
        String szYD_STK_COL_GP1 = null;
        String szYD_STK_BED_NO1 = null;
        String szYD_STK_LYR_NO1 = null;
        //String szYD_STRLOC_GRP_GP = null;
        String[] arrRT_ZONE_NO = null;
        String szPL_MTL_NO  = null;
        //후판트래킹존번호
        String szPL_TRCK_ZONE_NO        = null;
        //입고예정저장위치
        String szYD_RCPT_PLN_STR_LOC    = null;
        //길이구분
        String szYD_MTL_L_GP            = null;

        String szSTL_PROG_CD            = "";
        String is2UtReturn              = "";

        String szUST_ULTRASONIC_TESTING="";  //UT 대상재 체크 : 값이 있으면 대상재, NULL이면 미대상재
        String szPL_UST_STMP_RS_DTL_GP=""; // Off Line UST 대상재 여부 체크: NULL or 0일경우 Off Line 대상재

        int intYD_STK_BED_STL_SH    = 0;

        String szTF_PILING_LOC      = null;


        JDTORecordSet   outRecSet1  = null;

        //RT 가상버퍼 적치베드(TO위치)
        String szTO_LOC_BED_NO      = null;
        //재료매수
        int intMTL_SH           = 0;

        YdPlateCommDAO commDao = new YdPlateCommDAO();
        //String sz

        JDTORecord msgRecord = JDTORecordFactory.getInstance().create();

        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(tcRecord);


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= tcRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(tcRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8BOOK_OUT 요청 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////


        //에러 리턴
        if(szRcvTcCode == null || "".equals(szRcvTcCode) ) {
            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return;
        }
        //TC CODE DISPLAY
        if(bDebugFlag) {
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
        }


        // 2021. 4. 16 전사물류개선
        // 동일북아웃위치 & 동일파일링이 아닐 경우 생기는 문제 패치
        // 복구를 위한 크레인스케쥴 및 저장위치 정보를 미리 조회한다.
        JDTORecordSet orgPlingCompledStl = JDTORecordFactory.getInstance().createRecordSet("ydPlate");
        JDTORecordSet orgPlingCrnInfo = JDTORecordFactory.getInstance().createRecordSet("ydPlate");

        try{
            //=============================================================
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판제품창고L2] BOOK_OUT요구 수신";
            ydUtils.putLogMsg("D", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            szMsg = "[후판제품창고L2 BOOK_OUT요구 수신 - procY8BookOutReq]--------------------- 전문 내용 시작 --------------------------";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            szPL_MTL_NO = ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO1"); //최하단 재료번호
            intMTL_SH   = ydDaoUtils.paraRecChkNullInt(tcRecord,"MTL_SH");  //재료매수

            msgRecord.setField("YD_BOOK_OUT_LOC", ydDaoUtils.paraRecChkNull(tcRecord,"CURR_LOC"));

            szPL_TRCK_ZONE_NO = ydDaoUtils.paraRecChkNull(tcRecord,"PL_TRCK_ZONE_NO"); //R/T 모니터링 화면에서 BOOK-OUT 할 때만 값이 들어 있다..

            //받은 전문 편집
            //BOOK_OUT 위치
            szYD_BOOK_OUT_LOC       = ydDaoUtils.paraRecChkNull(msgRecord,"YD_BOOK_OUT_LOC");

            if("".equals(szYD_BOOK_OUT_LOC)) {

                szMsg = "[전문 이상] BOOK_OUT 위치가 없습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return;
            }

            if(szYD_BOOK_OUT_LOC.length() == 5) { //R/T 모니터링에서 BOOK-OUT 요청한 경우 66080 같이 5자리 숫자로 넘어 온다.

                //파일링 ZONE NO ==> 저장위치 정보 변환
                arrRT_ZONE_NO       = YdCommonUtils.getY4PilingZoneNo2StrLoc(szYD_BOOK_OUT_LOC); //66080 -> TERTRB80 변환 작업
                //RT 적치열구분
                szYD_BOOK_OUT_LOC   = arrRT_ZONE_NO[0] + arrRT_ZONE_NO[1];
                szYD_STK_COL_GP     = arrRT_ZONE_NO[0];
                szYD_STK_BED_NO     = arrRT_ZONE_NO[1];

            } else { // Level2 에서 Y8YDL012 전문으로 BOOK-OUT 요청한 경우는 TERTRB80 같이 8자리 야드위치로 넘어 온다.

                szYD_STK_COL_GP     = szYD_BOOK_OUT_LOC.substring(0,6);
                szYD_STK_BED_NO     = szYD_BOOK_OUT_LOC.substring(6,8);
            }

            szMsg = "[후판제품창고L2 BOOK_OUT요구 수신 - procY8BookOutReq] BOOK-OUT LOC[" + szYD_BOOK_OUT_LOC + "] ===> 야드저장위치 : 적치열[" + szYD_STK_COL_GP + "], 적치베드[" + szYD_STK_BED_NO + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            /*
             * 2018.03.30 윤재광
             * 2후판 #2 R/T 크레인파일링 처리
             */
            //2021.12.24 박종호 임가공재 재료는 파일링 대상 제외 (9번째 자리가 알파벳인 재료) 소영조차장 요청사항
            boolean isImgagong=false;
            if(szPL_MTL_NO.endsWith("AA")||
                szPL_MTL_NO.endsWith("BB")||
                szPL_MTL_NO.endsWith("CC")||
                szPL_MTL_NO.endsWith("DD"))
            {
                isImgagong=true;  //임가공재 판단 flag
            }

            if(szYD_BOOK_OUT_LOC.equals("TBRTRA15") && !isImgagong){  //임가공재는 라우팅위치가 TBRTRA15더라도 파일링대상에서 제외. 2021.12.24 소영조차장 요청사항

                this.procY8BookOutReqPiling(tcRecord);

                //----------------------------------------------------------------------------------------------------
                //  후판제품야드L2로 저장품제원 정보 전송
                //----------------------------------------------------------------------------------------------------
                szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 적치열[" + szYD_STK_COL_GP + "], 적치베드[" + szYD_STK_BED_NO + "] - 저장품제원 전송 시작 ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
                recInTemp.setField("YD_INFO_SYNC_CD",   "4");                       //1:동,2:SPAN,3:열,4:BED
                recInTemp.setField("YD_GP",             szYD_STK_COL_GP.substring(0, 1));
                recInTemp.setField("YD_STK_COL_GP",     szYD_STK_COL_GP);
                recInTemp.setField("YD_STK_BED_NO",     szYD_STK_BED_NO);


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// sndStockSpecToL2 call 시  recInTemp 에 logId SET 추가 개선
                recInTemp.setField("LOG_ID", logId);
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

                
                YdCommonUtils.sndStockSpecToL2(recInTemp);

                szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 적치열[" + szYD_STK_COL_GP + "], 적치베드[" + szYD_STK_BED_NO + "] - 저장품제원 전송 완료 ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                return;
            }

            /*
             * 2014.03.18 윤재광
             * 분할판을 북아웃 요청하는 경우
             * B동 수동절단장 대상으로 인식한다.
             */
            if(szPL_MTL_NO.endsWith("AA")||
               szPL_MTL_NO.endsWith("BB")||
               szPL_MTL_NO.endsWith("CC")||
               szPL_MTL_NO.endsWith("DD"))
            {
                this.procY8BookOutReqSub(tcRecord,"1");
                return;
            }

            /*
             * 2014.04.18 윤재광
             * QA검사대상재를 북아웃 요청하는 경우
             */
            if(szYD_BOOK_OUT_LOC.equals("TBTF0116")||szYD_BOOK_OUT_LOC.equals("TBRTRA20")||szYD_BOOK_OUT_LOC.equals("TBRTRB20")||szYD_BOOK_OUT_LOC.equals("TBRTRC20"))
            {

                JDTORecordSet rsOutRecSet   = JDTORecordFactory.getInstance().createRecordSet("");
                JDTORecord recIn            = JDTORecordFactory.getInstance().create();
                JDTORecord recOut           = JDTORecordFactory.getInstance().create();
                recIn.setField("PLATE_NO", szPL_MTL_NO);
                intRtnVal = ydStockDao.getYdStock(recIn, rsOutRecSet, 172);
                if(intRtnVal > 0){

                    rsOutRecSet.first();
                    recOut = rsOutRecSet.getRecord();

                    String sPlQaInspMtl = ydDaoUtils.paraRecChkNull(recOut,"PL_QA_INSP_MTL");
                    if("1".equals(sPlQaInspMtl)){
                        /*
                         * QA품질검사재이면 Default저장위치(TB033101)로 북아웃요청한다. //TB033101->TB032801로 변경 요청. 2022.11.30  후판품질팀 서승범 책임. 1673
                         */
                        this.procY8BookOutReqSub(tcRecord,"2");
                        return;
                    }
                }
            }

            rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            //저장품 테이블에 BOOK-OUT 대상 최하단 재료번호의 데이터가 있는지 체크한다.
            blnRtnVal = this.chkStock(szPL_MTL_NO, rsResult);
            if(!blnRtnVal) {
                return;
            }

            szMsg = "[저장품검색] 저장품 검색 대상 정보[rsResult]"+rsResult;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            rsResult.first();
            recInPara = rsResult.getRecord();

            szMsg = "[저장품검색] 저장품 검색 대상 정보[recInPara]"+recInPara;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            szYD_MTL_L_GP           = ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");
            szYD_RCPT_PLN_STR_LOC   = ydDaoUtils.paraRecChkNull(recInPara, "YD_RCPT_PLN_STR_LOC"); //저장품의 야드입고예정위치

            /*
             * 2015.09.09 윤재광
             * 2후판 제품창고내 UT탐상재 처리기준
             *  - 재료진도 = 'C' 인 대상재
             *  - 현재동    = 'C'
             *  2017.07.19 윤재광
             *  #2UT로 반납하는 기능 추가
             *  - 후판조업테이블 참조 - 차행선
             */
            szSTL_PROG_CD           = ydDaoUtils.paraRecChkNull(recInPara, "STL_PROG_CD");
            is2UtReturn             = ydDaoUtils.paraRecChkNull(recInPara, "IS_2UT_RETURN");

            szUST_ULTRASONIC_TESTING=ydDaoUtils.paraRecChkNull(recInPara, "UST_ULTRASONIC_TESTING");
            szPL_UST_STMP_RS_DTL_GP=ydDaoUtils.paraRecChkNull(recInPara, "PL_UST_STMP_RS_DTL_GP");
            // 입고RT > YD
            if(
               (
                   "C".equals(szSTL_PROG_CD)&&
                   "C".equals(szYD_BOOK_OUT_LOC.substring(1,2))&&
                   !szYD_BOOK_OUT_LOC.startsWith("TCRTUT") &&
                   !"".equals(szUST_ULTRASONIC_TESTING)&&  //UT 대상재 체크: 값이 있으면 대상재
                   ("".equals(szPL_UST_STMP_RS_DTL_GP) ||"0".equals(szPL_UST_STMP_RS_DTL_GP))  //Off Line UST 대상재 여부 체크: NULL or 0일경우 Off Line 대상재
               )
               ||
               (
                   "C".equals(szYD_BOOK_OUT_LOC.substring(1,2))&&
                   "Y".equals(is2UtReturn)&&
                   !szYD_BOOK_OUT_LOC.startsWith("TCRTUT") &&
                   !"".equals(szUST_ULTRASONIC_TESTING)&&  //UT 대상재 체크: 값이 있으면 대상재
                   ("".equals(szPL_UST_STMP_RS_DTL_GP) ||"0".equals(szPL_UST_STMP_RS_DTL_GP))  //Off Line UST 대상재 여부 체크: NULL or 0일경우 Off Line 대상재
               )
            ){

                this.procY8BookOutReqSub(tcRecord,"3");
                return;
            }else if(szYD_BOOK_OUT_LOC.startsWith("TCRTUT")){
            // UTRT > YD
                this.procY8BookOutReqSub(tcRecord,"4");
                return;
            }

            /* ++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            //BOOKOUT 위치가 C-RT(TBRTRC10, TBRTRC20) 이면서 입고예정치가 B동이 아닌경우는 TRANS BED 파일링 처리를 한다.
            if( ("TBRTRC10".equals(szYD_BOOK_OUT_LOC)||"TBRTRC20".equals(szYD_BOOK_OUT_LOC))
                    && !"B".equals(szYD_RCPT_PLN_STR_LOC.substring(1,2))) {

                // TransBed 파일링 지시 생성 여부가 'Y'이면

                //파일링 가능한 TRANS BED 주소를 검색한다.
                // ( 이미 입고존 출발시점에 파일링 가능한 TRANS BED가 존재하여 L2로 라우팅지시를 4:파일링 으로 전송해서
                //   BOOK-OUT 요청이 왔기 때문에 가능한 위치가 존재해야 현 시점에서 위치를 못 찾으면 ERROR 로그를
                //   남기고 종료한다.)
                //Trans Bed에서 입고존 출발하는 제품이 파일링 될 수 있는 위치가 존재하는지 쿼리로 찾는다.

                rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                recInPara = JDTORecordFactory.getInstance().create();
                recInPara.setField("STL_NO1", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO1"));
                recInPara.setField("STL_NO2", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO2"));
                recInPara.setField("STL_NO3", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO3"));
                recInPara.setField("STL_NO4", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO4"));
                recInPara.setField("STL_NO5", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO5"));
                recInPara.setField("STL_NO6", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO6"));

                intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0070");

                if(intRtnVal > 0 ) {

                    if("TBRTRC10".equals(szYD_BOOK_OUT_LOC)) {
                        //TBRTRC10 에서 BOOK-OUT 된 경우
                        szTF_PILING_LOC = "";

                        for(int Loop_i = 1; Loop_i <= intRtnVal; Loop_i++) {
                            rsResult.absolute(Loop_i);
                            recInPara = rsResult.getRecord();

                            szTF_PILING_LOC =    ydDaoUtils.paraRecChkNull(recInPara, "STL_LOC_8010");

                            if(!"".equals(szTF_PILING_LOC)) {
                                break;
                            }
                        }

                    } else {
                        //TBRTRC20 에서 BOOK-OUT 된 경우
                        szTF_PILING_LOC = "";

                        for(int Loop_i = 1; Loop_i <= intRtnVal; Loop_i++) {
                            rsResult.absolute(Loop_i);
                            recInPara = rsResult.getRecord();

                            szTF_PILING_LOC =    ydDaoUtils.paraRecChkNull(recInPara, "STL_LOC_8020");

                            if(!"".equals(szTF_PILING_LOC)) {
                                break;
                            }
                        }
                    }

                    if(!"".equals(szTF_PILING_LOC)) {
                        //BOO-OUT 된 존에서  TransBed 파일링 할 수 있는 위치를 찾음

                        szMsg = "[후판제품창고L2 BOOK_OUT요구 수신 - procY8BookOutReq] Trans Bed 파일링 지시하기전에 C-RT BOOK-OUT 존에 제품을 적치한다.------------------";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                        //INSERT항목 레코드 생성
                        recPara = JDTORecordFactory.getInstance().create();
                        //recTemp = JDTORecordFactory.getInstance().create();

                        //BOOK-OUT 된 RT 상에 적치한다.
                        for(int Loop_i = intMTL_SH; Loop_i > 0; Loop_i--) {

                            //적치단 재료상태가 적치 가능이면 재료 등록
                            //적치단 테이블 업데이트
                            //적치열구분 = 설비ID
                            recPara.setField("YD_STK_COL_GP",       szYD_STK_COL_GP);
                            recPara.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);
                            recPara.setField("YD_STK_LYR_NO",       "00" + (Loop_i));
                            recPara.setField("MODIFIER",            "Y8YDL012");
                            recPara.setField("YD_STK_LYR_MTL_STAT", "C");
                            recPara.setField("STL_NO",              ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO"+Loop_i) );

                            //업데이트 실행
                            intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);

                        }


                        szMsg = "[후판제품창고L2 BOOK_OUT요구 수신 - procY8BookOutReq] Trans Bed 파일링 지시 생성 시작-----------------------";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                        rsResult.first(); //권상위치에서 가장 가까운 Trans Bed
                        recInPara = rsResult.getRecord();

                        recPara  = JDTORecordFactory.getInstance().create();

                        //YD_SCH_CD
                        recPara.setField("YD_SCH_CD",       szYD_BOOK_OUT_LOC.substring(0,2)+"TF01MM");
                        //YD_GP
                        recPara.setField("YD_GP",           szYD_BOOK_OUT_LOC.substring(0,1));
                        //YD_BAY_GP
                        recPara.setField("YD_BAY_GP",       szYD_BOOK_OUT_LOC.substring(1,2));
                        //YD_AIM_YD_GP
                        recPara.setField("YD_AIM_YD_GP",    szYD_BOOK_OUT_LOC.substring(0,1));
                        //YD_AIM_BAY_GP
                        recPara.setField("YD_AIM_BAY_GP",   szYD_BOOK_OUT_LOC.substring(1,2));
                        //YD_LOT_GP_SH [매수]
                        recPara.setField("YD_LOT_GP_SH",    ydDaoUtils.paraRecChkNull(tcRecord,"MTL_SH"));
                        //YD_TO_LOC_GUIDE
                        //recPara.setField("YD_TO_LOC_GUIDE", ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP")
                        //                                  + ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO"));
                        recPara.setField("YD_TO_LOC_GUIDE", szTF_PILING_LOC);

                        //int intSh = tcRecord.getFieldInt("MTL_SH");
                        int intSh = ydDaoUtils.paraRecChkNullInt(tcRecord,"MTL_SH");  //재료매수

                        for(int Loopi=0 ; Loopi<intSh ;Loopi++){
                            //재료번호
                            recPara.setField("STL_NO"+(Loopi+1), ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO"+(Loopi+1)));
                            //권상 모음순서 - YD_UP_COLL_SEQ
                            recPara.setField("YD_UP_COLL_SEQ"+(Loopi+1),Integer.toString(Loopi+1));
                        }
                        recPara.setField("REGISTER", "Y8YDL012");

                        //내부 Process 연결 - 작업예약 생성 / 스케줄 MAIN 호출
                        EJBConnector ejbConn = null;
                        ejbConn = new EJBConnector("default", this);
                        ejbConn.trx("IssueWrkDmdSeEJB", "procPlGdsRetnWrkReq", recPara);
                        szMsg = "[후판제품창고L2 BOOK_OUT요구 수신 - procY8BookOutReq] Trans Bed 파일링 지시 생성 종료-----------------------";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                        return;

                    } else {
                        //BOO-OUT 된 존에서  TransBed 파일링 할 수 있는 위치를  못 찾음
                        szMsg = "[후판제품창고L2 BOOK_OUT요구 수신 - procY8BookOutReq] BOO-OUT 된 존에서  TransBed 파일링 할 수 있는 위치를  못 찾음!!!! : " + szYD_BOOK_OUT_LOC;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                    }

                } else {
                    //파일링 할 수 있는 위치를 찾지 못 함 (Query 결과가 없는 경우)
                    szMsg = "[후판제품창고L2 BOOK_OUT요구 수신 - procY8BookOutReq] Trans Bed 파일링 가능한 위치를 찾지못함!!!!";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                }


                //----------------------------------------------------------------------------------------------------------------------------

                szMsg = "[후판제품창고L2 BOOK_OUT요구 수신 - procY8BookOutReq] Trans Bed 파일링 지시하기전에 C-RT BOOK-OUT 존에 제품을 적치한다.------------------";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                //BOOK-OUT 된 RT 상에 적치한다.
                for(int Loop_i = intMTL_SH; Loop_i > 0; Loop_i--) {

                    //적치단 재료상태가 적치 가능이면 재료 등록
                    //적치단 테이블 업데이트
                    //적치열구분 = 설비ID
                    recPara = JDTORecordFactory.getInstance().create();

                    recPara.setField("YD_STK_COL_GP",       szYD_STK_COL_GP);
                    recPara.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);
                    recPara.setField("YD_STK_LYR_NO",       "00" + (Loop_i));
                    recPara.setField("MODIFIER",            "Y8YDL012");
                    recPara.setField("YD_STK_LYR_MTL_STAT", "C");
                    recPara.setField("STL_NO",              ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO"+Loop_i) );

                    //업데이트 실행
                    intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);

                }

                szMsg = "[후판제품창고L2 BOOK_OUT요구 수신 - procY8BookOutReq] A-RT 동간이적 지시 생성 시작-----------------------";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                recPara = JDTORecordFactory.getInstance().create();

                //YD_SCH_CD
                recPara.setField("YD_SCH_CD",       szYD_BOOK_OUT_LOC.substring(0,2)+"RTRA"+"DR"); //A-RT 를 이용한 동간이적 -- 통합크레인스케줄
                //YD_GP
                recPara.setField("YD_GP",           szYD_BOOK_OUT_LOC.substring(0,1));
                //YD_BAY_GP
                recPara.setField("YD_BAY_GP",       szYD_BOOK_OUT_LOC.substring(1,2));
                //YD_AIM_YD_GP
                recPara.setField("YD_AIM_YD_GP",    szYD_RCPT_PLN_STR_LOC.substring(0,1));
                //YD_AIM_BAY_GP
                recPara.setField("YD_AIM_BAY_GP",   szYD_RCPT_PLN_STR_LOC.substring(1,2));
                //YD_LOT_GP_SH [매수]
                recPara.setField("YD_LOT_GP_SH",    ydDaoUtils.paraRecChkNull(tcRecord,"MTL_SH"));
                //YD_TO_LOC_GUIDE
                recPara.setField("YD_TO_LOC_GUIDE", szYD_RCPT_PLN_STR_LOC);

                int intSh = ydDaoUtils.paraRecChkNullInt(tcRecord,"MTL_SH");  //재료매수

                for(int Loopi=0 ; Loopi<intSh ;Loopi++){
                    //재료번호
                    recPara.setField("STL_NO"+(Loopi+1), ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO"+(Loopi+1)));
                    //권상 모음순서 - YD_UP_COLL_SEQ
                    recPara.setField("YD_UP_COLL_SEQ"+(Loopi+1),Integer.toString(Loopi+1));
                }
                recPara.setField("REGISTER", "Y8YDL012");

                //내부 Process 연결 - 작업예약 생성 / 스케줄 MAIN 호출
                EJBConnector ejbConn = null;
                ejbConn = new EJBConnector("default", this);
                ejbConn.trx("IssueWrkDmdSeEJB", "procPlGdsRetnWrkReq", recPara);

                szMsg = "[후판제품창고L2 BOOK_OUT요구 수신 - procY8BookOutReq] A-RT 동간이적 지시 생성 종료-----------------------";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                return;
            }


            /* ++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            //아래는 RT 또는 TF 상에서 입고되는 (가상베드 처리를 포함한) BOOKOUT 처리 이다.

            /////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////2012.06.04 윤재광////////////////////////////////////////////
            /////////////////////////OFF LINE 북아웃 대상에 대한 크레인 파일링 지시///////////////////
            /////////////////////////////////////////////////////////////////////////////////////

            String szYD_SCH_ST_GP = ""; // 작업예약생성시 파일링 작업지시인지를 구분하는 항목

            //NEW Version : 크래인 파일링 ON,OFF-LINE 모두 적용 적용, 낱장+여러장 가능
            //--2013.04.09 szYD_STK_COL_GP 값이 "KBTF02"와 같이 Transfer 일 수 도 있다.
            //  --> 이런경우 입력 파라메터 tcRecord 로 전달받은 PL_TRCK_ZONE_NO(ex:56216) 으로
            //  --> YdCommonUtils.getY4BookOutLoc 을 실행하여 BOOK-OUT 위치를 다시 구한다.
            //  -->  원래 R/T상의 위치는 그대로 나오고 TF 일경우는 TF와 붙어있는 R/T 의 위치가 나온다.
            //  -->  이 값을 가지고 아래 로직을 수행한다. (ex: "KBTF02" --> "KBRTRA" )

            String szRT_STK_COL_GP = null;

            if(!"".equals(szPL_TRCK_ZONE_NO)) {
                //R/T 모니터링 화면에서 BOOK-OUT 할 때 ...
                arrRT_ZONE_NO   = YdCommonUtils.getY4BookOutLoc(szPL_TRCK_ZONE_NO);
                szRT_STK_COL_GP = arrRT_ZONE_NO[0];
            } else {
                //Level 2 에서 BOOK-OUT 할 때 ... ** TRANSFER 일 때  RT로 주소로 변경
                if("TF".equals(szYD_STK_COL_GP.substring(2,4))){
                    szRT_STK_COL_GP = YdCommonUtils.getTf2RtStkLoc(szYD_STK_COL_GP);
                } else {
                    szRT_STK_COL_GP = szYD_STK_COL_GP;
                }
            }

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////

            /////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////////////////////////

            rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            recInPara = JDTORecordFactory.getInstance().create();
            recInPara.setField("STL_NO", szPL_MTL_NO);
            recInPara.setField("YD_STK_LYR_MTL_STAT", "");
            recInPara.setField("YD_GP", szRT_STK_COL_GP.substring(0,1));
            //intRtnVal = ydStkLyrDao.getYdStklyr(recInPara, rsResult, 3);
            intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0080");

            if( intRtnVal > 0) {

                //1건 이상 존재시 CPL 트랜스 BED 에 제품이 존재할 경우 BOOKOUT 요청이 왔다는 건 트랜스 BED에서는 지나왔다는 것을 의미함으로
                //트랜스 BED의 해당 제품을 삭제(UPDATE)한다. -- 2013.08.29
                int isUpdated = 0;
                for(int Loop_i = 1; Loop_i <= intRtnVal; Loop_i++) {

                    rsResult.absolute(Loop_i);
                    recInPara = rsResult.getRecord();

                    szYD_STK_COL_GP1 = ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");
                    szYD_STK_BED_NO1 = ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");

                    if(szYD_STK_COL_GP1.startsWith("TBTF")) {
                        if(szYD_STK_COL_GP1.endsWith("03") || szYD_STK_COL_GP1.endsWith("04") ||
                           szYD_STK_COL_GP1.endsWith("05") || szYD_STK_COL_GP1.endsWith("06") ||
                           szYD_STK_COL_GP1.endsWith("07") ) {
                            //CPL 트랜스 BED에 위치할 경우...
                            recInPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP1);
                            recInPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO1);
                            commDao.update(recInPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0025");
                            isUpdated++;
                        }
                    }
                }
                if(isUpdated > 0) {
                    //Update 되었다면 다시 한 번 조회 한다.
                    rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                    recInPara.setField("STL_NO", szPL_MTL_NO);
                    recInPara.setField("YD_STK_LYR_MTL_STAT", "");
                    recInPara.setField("YD_GP", szRT_STK_COL_GP.substring(0,1));
                    intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0080");
                }
            }

            if( intRtnVal <= 0 ) {

                szMsg = "[후판제품창고L2 BOOK_OUT요구 수신  - procY8BookOutReq] 적치단에 재료정보["+szPL_MTL_NO+"]가 없습니다. Error Code : " + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                //return;

                //--------------------------------------------------------------------------start---
                // 입고존 출발 정보 없이 BOOK-OUT 요청이 오면 여기서 가상버퍼에 담는 작업을 한다.

                //BOOKOUT 위치로 입력가능한 가상베드이 적치열구분과, 적치Bed를 구한다.
                recPara = JDTORecordFactory.getInstance().create();
                outRecSet1 = JDTORecordFactory.getInstance().createRecordSet("");

                recPara.setField("YD_STK_COL_GP", szRT_STK_COL_GP);
                recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);


                /*com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0047*/
                intRtnVal = commDao.select(recPara, outRecSet1, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0047");

                if(intRtnVal < 1) {

                    //입력가능한 빈 가상베드를 찾지 못하면 에러 로그를 남긴다.
                    szMsg = "[후판제품창고L2] 입고존 재료정보 수신 : 입고존 출발시 빈 RT 가상베드 찾지 못함!!  YD_STK_COL_GP : "+szRT_STK_COL_GP + " ,YD_STK_BED_NO : " + szYD_STK_BED_NO;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                } else {

                    //입력가능한 빈 가상베드를 찾으면 그 가상베드에 입고존 출발 제품들을 적치시킨다.
                    outRecSet1.first();
                    recPara = outRecSet1.getRecord();
                    szTO_LOC_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");


                    if( !"".equals(szTO_LOC_BED_NO) ) {

                        //INSERT항목 레코드 생성
                        recPara = JDTORecordFactory.getInstance().create();

                        //대기베드에 적치한다.
                        for(int Loop_i = intMTL_SH; Loop_i > 0; Loop_i--) {

                            //적치단 재료상태가 적치 가능이면 재료 등록
                            //적치단 테이블 업데이트
                            //적치열구분 = 설비ID
                            recPara.setField("YD_STK_COL_GP",       szRT_STK_COL_GP);
                            recPara.setField("YD_STK_BED_NO",       szTO_LOC_BED_NO);
                            recPara.setField("YD_STK_LYR_NO",       "00" + (Loop_i));
                            recPara.setField("MODIFIER",            "Y8YDL012");
                            recPara.setField("YD_STK_LYR_MTL_STAT", "C");
                            recPara.setField("STL_NO",              ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO"+Loop_i) );

                            //업데이트 실행
                            intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);

                        }
                    }
                }
                //--------------------------------------------------------------------------end-----

                //intRtnVal = ydStkLyrDao.getYdStklyr(recInPara, rsResult, 3);
                intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0080");

                if (intRtnVal <= 0) {
                    szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] Piling 처리후 적치단에 재료정보["+szPL_MTL_NO+"]가 없습니다. Error Code : " + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    return;
                }
            }

            if( intRtnVal > 1 ) {

                szMsg = "[후판제품창고L2 BOOK_OUT요구 수신  - procY8BookOutReq] 적치단에 재료정보["+szPL_MTL_NO+"]가 1개이상 존재 합니다. intRtnVal : " + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return;
            }


            szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 적치단에 재료정보["+szPL_MTL_NO+"]가 존재합니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);


            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            if(szRT_STK_COL_GP.endsWith("RA")||szRT_STK_COL_GP.endsWith("RB")||szRT_STK_COL_GP.endsWith("RC")
                    ||szRT_STK_COL_GP.endsWith("RD")||szRT_STK_COL_GP.endsWith("RE")||szRT_STK_COL_GP.endsWith("RF")
                ){

                    //CRANE PILING 시 사용하는 각 동, 각 RT 라인의  가상 AutoPiler 버퍼을 구분하기 위한 Bed No를 구함
                    // ex) D동  A-RT 는 KDAP02 의 02 BED
                    String szTempBedNo = "01";

                    if(szRT_STK_COL_GP.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD)) {
                        //2후판일경우 RT 구분 A,B,C
                        if(szRT_STK_COL_GP.endsWith("RA")) {
                            szTempBedNo = "01";
                        } else if(szRT_STK_COL_GP.endsWith("RB")){
                            szTempBedNo = "02";
                        } else if(szRT_STK_COL_GP.endsWith("RC")){
                            szTempBedNo = "03";
                        } else if(szRT_STK_COL_GP.endsWith("RD")){
                            szTempBedNo = "04";
                        } else if(szRT_STK_COL_GP.endsWith("RE")){
                            szTempBedNo = "05";
                        } else if(szRT_STK_COL_GP.endsWith("RF")){
                            szTempBedNo = "06";
                        }
                    } else {
                        //1후판일경우 통합 전 A,B,C 인데 B가 01로 이전 부터 사용하고 있어서 B를 01로 함
                        //통합 후는 D,E,F 로 됨
                        if(szRT_STK_COL_GP.endsWith("RB")) {
                            szTempBedNo = "01";
                        } else if(szRT_STK_COL_GP.endsWith("RA")){
                            szTempBedNo = "02";
                        } else if(szRT_STK_COL_GP.endsWith("RC")){
                            szTempBedNo = "03";
                        } else if(szRT_STK_COL_GP.endsWith("RD")){
                            szTempBedNo = "04";
                        } else if(szRT_STK_COL_GP.endsWith("RE")){
                            szTempBedNo = "05";
                        } else if(szRT_STK_COL_GP.endsWith("RF")){
                            szTempBedNo = "06";
                        }
                    }

                    // 전사물류개선 2021. 4. 16
                    // 만약 파일링이 진행중이라면 삭제 전 이전 정보를 갖고 있자.
                    JDTORecord jtoPilingCheckParam = JDTORecordFactory.getInstance().create();
                    jtoPilingCheckParam.setField("REQ_YD_STK_COL_GP", szRT_STK_COL_GP);
                    jtoPilingCheckParam.setField("REQ_PLATE_NO", szPL_MTL_NO);
                    jtoPilingCheckParam.setField("REQ_YD_STK_BED_NO", szTempBedNo);
                    commDao.select(jtoPilingCheckParam, orgPlingCompledStl, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getPilingInfoByDefault");

                    // 현재 크레인스케쥴 정보를 담는다.(나중에 복구용)
                    jtoPilingCheckParam = JDTORecordFactory.getInstance().create();
                    jtoPilingCheckParam.setField("YD_STK_COL_GP",   szYD_STK_COL_GP);
                    jtoPilingCheckParam.setField("YD_STK_BED_NO",       szTempBedNo);
                    commDao.select(jtoPilingCheckParam, orgPlingCrnInfo, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getPilingEndCheckByCrnsch");


                    szMsg = "[Jsp-Session "+szSessionName+" ] szPL_MTL_NO:" + szPL_MTL_NO + " , szRT_STK_COL_GP : " +  szRT_STK_COL_GP + " , szTempBedNo : " + szTempBedNo;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    Object[] inParam = {
                                 szPL_MTL_NO
                                ,szRT_STK_COL_GP
                                ,szTempBedNo
                               };

                    int[] inParamIndex = {1,2,3};

                    JDTORecord record = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.callSpQueryId_0001");

                    if(record.size() > 0){
                        /*
                         * T : 파일링가능, F : 파일링 SKIP   -- 파일링정보가 없을 경우.
                         * S : 파일링가능, E : 파일링 SKIP   -- 파일링정보가 있는 경우.
                         */
                        szYD_SCH_ST_GP = ydDaoUtils.paraRecChkNull(record, "OUT_RTN_CODE");

                        szMsg = "[Jsp-Session "+szSessionName+" ] OUT_RTN_CODE="+szYD_SCH_ST_GP ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                    }
                }

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            rsResult.first();
            recInPara = rsResult.getRecord();

            //제품번호로 조회한 YD적치열테이블이 결과 값, 가상베드의 주소가 들어 있다 (ex: TERTRB-7N-001.)
            //  szYD_STK_COL_GP1 : TERTRB
            //  szYD_STK_BED_NO1 : 7N
            //  szYD_STK_LYR_NO1 : 001
            //  szYD_STRLOC_GRP_GP : 75
            szYD_STK_COL_GP1 = ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");
            szYD_STK_BED_NO1 = ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");
            szYD_STK_LYR_NO1 = ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_LYR_NO");
            //szYD_STRLOC_GRP_GP = ydDaoUtils.paraRecChkNull(recInPara, "YD_STRLOC_GRP_GP");

            szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 재료정보["+szPL_MTL_NO+"]가 존재하는 적치열["+szYD_STK_COL_GP1+"], 적치베드["+szYD_STK_BED_NO1+"]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            if(!"RT".equals(
            szYD_STK_COL_GP1.substring(2,4))&&
               !"TF".equals(szYD_STK_COL_GP1.substring(2,4))){

                szMsg = "[저장위치에러] BOOK OUT 대상 저장위치가 RT/TF가 아닙니다.[윤재광]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return;
            }

            if(!"001".equals(szYD_STK_LYR_NO1)){
                szMsg = "[저장위치에러] BOOK OUT 대상 저장위치가 SKIP 대상입니다 .[윤재광]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return;
            }

            rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            recInPara = JDTORecordFactory.getInstance().create();
            recInPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP1);
            recInPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO1);



            /**
             * 2021. 04. 15
             *  - 구분자가 파일링 종료되는 시점에 다음과 같이 한 번 더 판별한다.
             *    T : 파일링가능, F : 파일링 SKIP   -- 파일링정보가 없을 경우.
             *    S : 파일링가능, E : 파일링 SKIP   -- 파일링정보가 있는 경우.
             *  - Check Point
             *   1. 동일한 파일링 코드 및 동일한 파일링 위치인가?
             *   2. 변경전 크레인스케쥴이 파일중인가?
             *   3. 변경전 크레인스케쥴의 야드진행상태가 권상인가?
             *
             *
             *  ** 제품번호로 조회한 YD적치열테이블이 결과 값, 가상베드의 주소가 들어 있다
             *  szYD_STK_COL_GP1, szYD_STK_BED_NO1
             */
            if(PlateGdsYdUtil.isApplyYn("파일링오류패치적용여부")){

                if("F".equals(szYD_SCH_ST_GP) || "E".equals(szYD_SCH_ST_GP)){

                    szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 파일링중인 스케쥴에 대해서만 종료시점 Check";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
                    JDTORecord jtoPilingCheckParam = JDTORecordFactory.getInstance().create();
                    JDTORecordSet jtoPilingCheckRst = JDTORecordFactory.getInstance().createRecordSet("ydPlate");

                    szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 파일링중인 스케쥴의 북아웃정보, 요청한 북아웃정보 비교시작";
                    if( orgPlingCompledStl != null && orgPlingCompledStl.size() > 0){


//                      String szREQ_PL_RCPT_TRK_NO      = jtoPilingCheckRst.getRecord(0).getFieldString("REQ_PL_RCPT_TRK_NO");
                        String szREQ_YD_PILING_CD        = StringHelper.nvl(orgPlingCompledStl.getRecord(0).getFieldString("REQ_YD_PILING_CD"),"REQ");
                        String szREQ_YD_BOOK_OUT_LOC     = StringHelper.nvl(orgPlingCompledStl.getRecord(0).getFieldString("REQ_YD_BOOK_OUT_LOC"),"REQ");
                        String szREQ_YD_RCPT_PLN_STR_LOC = StringHelper.nvl(orgPlingCompledStl.getRecord(0).getFieldString("REQ_YD_RCPT_PLN_STR_LOC"),"REQ");

//                      String szTO_PL_RCPT_TRK_NO          = jtoPilingCheckRst.getRecord(0).getFieldString("PL_RCPT_TRK_NO         ");
                        String szTO_YD_PILING_CD            = StringHelper.nvl(orgPlingCompledStl.getRecord(0).getFieldString("YD_PILING_CD"),"TO");
                        String szTO_YD_BOOK_OUT_LOC         = StringHelper.nvl(orgPlingCompledStl.getRecord(0).getFieldString("YD_BOOK_OUT_LOC"),"TO");
                        String szTO_YD_RCPT_PLN_STR_LOC     = StringHelper.nvl(orgPlingCompledStl.getRecord(0).getFieldString("YD_RCPT_PLN_STR_LOC"),"TO");

                        szMsg += "\n 북아웃 정보 비교 시작(요청vs파일링완료)";
                        szMsg += "\n 북아웃 정보 비교 동일파링 :: ["+szREQ_YD_PILING_CD+":VS:"+szTO_YD_PILING_CD+"]";
                        szMsg += "\n 북아웃 정보 비교 동일북아웃위치 :::: ["+szREQ_YD_BOOK_OUT_LOC+":VS:"+szTO_YD_BOOK_OUT_LOC+"]";
                        szMsg += "\n 북아웃 정보 비교 동일한 야드저장위치 :: :: ["+szREQ_YD_RCPT_PLN_STR_LOC+":VS:"+szTO_YD_RCPT_PLN_STR_LOC+"]";
                        szMsg += "\n 북아웃 정보 비교 종료";

// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

                        // 동일 파일링, 동일 북아웃위치, 동일 야드저장위치까지 다 확인하자!
                        if(
                                !(szREQ_YD_PILING_CD.equals(szTO_YD_PILING_CD)
                                && szREQ_YD_BOOK_OUT_LOC.equals(szTO_YD_BOOK_OUT_LOC)
//                              && szREQ_YD_RCPT_PLN_STR_LOC.equals(szTO_YD_RCPT_PLN_STR_LOC)) 예정위치는 바뀔 수 있다.
                                )
                        ){

                            szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 파일링이 되지 않은 재료가 북아웃되어왔음 관련 스케쥴 및 작업예약 체크";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

                            // 혹시 크레인스케쥴이 삭제되었는지 확인한다.
                            // 아래 To Bed의 위치에 적재된 재료가 크레인스케쥴 파일링이 취소되었다면
                            // 해당 파일링은 재 작업지시를 내린다.
                            // 삭제되었는지 체크
                            szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 파일링 스케쥴이 삭제되었는가 확인";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
                            if(orgPlingCrnInfo != null && orgPlingCrnInfo.size() > 0){
                                String szYD_CRN_SCH_ID = orgPlingCrnInfo.getRecord(0).getFieldString("YD_CRN_SCH_ID");
                                jtoPilingCheckParam = JDTORecordFactory.getInstance().create();
                                jtoPilingCheckParam.setField("YD_CRN_SCH_ID",   szYD_CRN_SCH_ID);   //북아웃위치 적치열구분(TO) - RT,TF

                                if(commDao.select(jtoPilingCheckParam, jtoPilingCheckRst, "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnsch")>0){

                                    szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 스케쥴ID["+szYD_CRN_SCH_ID+"] 삭제되어 다시 원복 후 재작업지시(파일링종료) 처리";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

                                    szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 크레인작업내역 복구 시작 ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
                                    String szYD_WBOOK_ID = orgPlingCrnInfo.getRecord(0).getFieldString("YD_WBOOK_ID");
                                    jtoPilingCheckParam.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
                                    commDao.update(jtoPilingCheckParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdWrkBookMtlOnByPiling");
                                    commDao.update(jtoPilingCheckParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdWrkBookOnByPiling");

                                    jtoPilingCheckParam.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
                                    commDao.update(jtoPilingCheckParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCrnSchMtlOnByPiling");
                                    commDao.update(jtoPilingCheckParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCrnSchOnByPiling");
                                    szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 크레인작업내역 복구 종료 ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

                                    szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 크레인설비 상태  YD_EQP_STAT 복구 시작 ";
                                    jtoPilingCheckParam.setField("YD_EQP_STAT", orgPlingCrnInfo.getRecord(0).getFieldString("YD_EQP_STAT"));
                                    jtoPilingCheckParam.setField("YD_EQP_ID", orgPlingCrnInfo.getRecord(0).getFieldString("YD_EQP_ID"));
                                    commDao.update(jtoPilingCheckParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdEqpRepairePiling");
                                    szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 크레인설비 상태  YD_EQP_STAT 복구 종료 ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

                                    szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 적치단(tb_yd_stklyr) 복구 시작 ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
                                    for( int i=0; i < orgPlingCrnInfo.size(); i++){
                                        jtoPilingCheckParam = JDTORecordFactory.getInstance().create();
                                        jtoPilingCheckParam.setField("STL_NO", orgPlingCrnInfo.getRecord(i).getFieldString("STL_NO"));

                                        szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 적치단(tb_yd_stklyr) Clear By STL_NO :: " +jtoPilingCheckParam.getFieldString("STL_NO") ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

                                        // 변경전 파일링 재료 초기화
                                        commDao.update(jtoPilingCheckParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdStkLyrClearByPiling");
                                    }

                                    for( int i=0; i < orgPlingCrnInfo.size(); i++){
                                        jtoPilingCheckParam = JDTORecordFactory.getInstance().create();

                                        jtoPilingCheckParam.setField("YD_STK_COL_GP", orgPlingCrnInfo.getRecord(i).getFieldString("YD_STK_COL_GP"));
                                        jtoPilingCheckParam.setField("YD_STK_BED_NO", orgPlingCrnInfo.getRecord(i).getFieldString("YD_STK_BED_NO"));
                                        jtoPilingCheckParam.setField("YD_STK_LYR_NO", orgPlingCrnInfo.getRecord(i).getFieldString("YD_STK_LYR_NO"));

                                        jtoPilingCheckParam.setField("STL_NO", orgPlingCrnInfo.getRecord(i).getFieldString("STL_NO"));
                                        jtoPilingCheckParam.setField("YD_STK_LYR_MTL_STAT", orgPlingCrnInfo.getRecord(i).getFieldString("YD_STK_LYR_MTL_STAT"));

                                        szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 복구처리"
                                            + "\n YD_STK_COL_GP :: " + jtoPilingCheckParam.getField("YD_STK_COL_GP")
                                            + "\n YD_STK_BED_NO :: " + jtoPilingCheckParam.getField("YD_STK_BED_NO")
                                            + "\n YD_STK_LYR_NO :: " + jtoPilingCheckParam.getField("YD_STK_LYR_NO")
                                            + "\n STL_NO :: " + jtoPilingCheckParam.getField("STL_NO")
                                            + "\n YD_STK_LYR_MTL_STAT :: " + jtoPilingCheckParam.getField("YD_STK_LYR_MTL_STAT")
                                            ;

                                        // 삭제된 파일링 재료 복구처리
                                        commDao.update(jtoPilingCheckParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdStkLyrByPiling");
                                    }
                                    szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq]  적치단(tb_yd_stklyr) 복구 종료 ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);


                                    // 파일링 완료 작업지시 전문을 전송처리한다.
                                    jtoPilingCheckParam = JDTORecordFactory.getInstance().create();
                                    jtoPilingCheckRst = JDTORecordFactory.getInstance().createRecordSet("ydPlate");

                                    jtoPilingCheckParam.setField("MSG_ID", "YDY8L004");
                                    jtoPilingCheckParam.setField("YD_CRN_SCH_ID",       szYD_CRN_SCH_ID);
                                    jtoPilingCheckParam.setField("YD_WRK_PROG_STAT",    orgPlingCrnInfo.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
                                    jtoPilingCheckParam.setField("YD_SCH_CD",           orgPlingCrnInfo.getRecord(0).getFieldString("YD_SCH_CD"));
                                    jtoPilingCheckParam.setField("YD_GP",               YdConstant.YD_GP_PLATE2_GDS_YARD);

                                    MakeTcY8.makeY8L004(jtoPilingCheckParam, jtoPilingCheckRst);
                                    if(jtoPilingCheckRst != null ){

                                        szMsg =  "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 스케쥴ID["+szYD_CRN_SCH_ID+"] 재작업지시(파일링종료) 전문전송(No_Make_TC)";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

                                        JDTORecord sendMsgYDY8L004 = null;
                                        sendMsgYDY8L004 = (JDTORecord)jtoPilingCheckRst.getRecord(0);
                                        ydDelegate.sendMsg_NoMakeTc(sendMsgYDY8L004);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            /* com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrByColGpBedNoLike */
            intRtnVal = ydStkLyrDao.getYdStklyr(recInPara, rsResult, 75);
            if(intRtnVal <= 0) {
                szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 대기베드[적치열:"+szYD_STK_COL_GP1+", 적치베드:"+szYD_STK_BED_NO1+"]에 재료정보가 없습니다. Error Code : " + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return;
            }

            intMtlCnt = rsResult.size();

            szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 대기베드[적치열:"+szYD_STK_COL_GP1+", 적치베드:"+szYD_STK_BED_NO1+"]에 재료가 존재합니다 - 대상재건수 : " + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

            /**
             * 가상Bed -> 실제 파일링되는 Bed로 옮기기
             */
            szSTL_NO  = new String[intMtlCnt + 1];

            //New Version--------------------------------------------------------------------------
            for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
                rsResult.absolute(Loop_i);
                //RT가상버퍼에서 RT상의 실제 Book_Out 위치에 재료 등록 후 RT 가상 버퍼 Clear
                recOutPara = rsResult.getRecord();

                szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(recOutPara, "STL_NO");
                
                String ydStkColGp = ydDaoUtils.paraRecChkNull(recOutPara, "YD_STK_COL_GP");
                String ydStkBedNo = ydDaoUtils.paraRecChkNull(recOutPara, "YD_STK_BED_NO");
                String ydStkLyrNo = ydDaoUtils.paraRecChkNull(recOutPara, "YD_STK_LYR_NO");
                String modifier   = ydDaoUtils.paraRecChkNull(recOutPara, "MODIFIER");
                String modDdtt    = ydDaoUtils.paraRecChkNull(recOutPara, "MOD_DDTT");
                
                szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 적치열 ["+ydStkColGp+"] 적치베드 ["+ydStkBedNo+"] 트래킹";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                
                szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] "+ydStkLyrNo+"번째 적치단 적치재료 ["+szSTL_NO[Loop_i]+"] 수정자["+modifier+"] 수정일시["+modDdtt+"]";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                
                if( !"".equals(szSTL_NO[Loop_i]) ) {
                    intYD_STK_BED_STL_SH++;
                }

                szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] intYD_STK_BED_STL_SH 값 ["+intYD_STK_BED_STL_SH+"]";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                
                if(Loop_i == 1) { //1회만 수행  쿼리참고

                    if(szYD_STK_COL_GP1.equals(szYD_STK_COL_GP)&&szYD_STK_BED_NO1.equals(szYD_STK_BED_NO)) {
                        //from, to 위치가 같다면 아래 두 개의 쿼리 수행을 하지 않는다.
                        continue;
                    }

                    recPara = JDTORecordFactory.getInstance().create();
                    recPara.setField("FROM_STK_COL_GP", szYD_STK_COL_GP1);  //가상버퍼 적치열구분(FROM)
                    recPara.setField("TO_STK_COL_GP",   szYD_STK_COL_GP);   //북아웃위치 적치열구분(TO) - RT,TF
                    recPara.setField("FROM_BED_NO",     szYD_STK_BED_NO1);  //가상버퍼 적치열구분(FROM)
                    recPara.setField("TO_BED_NO",       szYD_STK_BED_NO);   //북아웃위치 적치열구분(TO) - RT,TF

                    intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0006");

                    if(intRtnVal < 1) {
                        szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] BOOK-OUT위치 Update ERROR 위치 - 반환값 : " + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                        return;
                    }

                    intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0007");

                    if(intRtnVal < 1) {
                        szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 대기베드 재료정보 Clear Update ERROR 위치: " + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                        return;
                    }
                }
            }

            //------------------------------------------------------------------
            // book out 처리시 flex 데이터 전송
            //------------------------------------------------------------------
            JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
            recFlex.setField("YD_GP",  YdConstant.YD_GP_PLATE2_GDS_YARD);
            recFlex.setField("YD_UP_WR_LOC", szYD_STK_COL_GP+szYD_STK_BED_NO );
            ydUtils.putYdFlexCrnWrk("", recFlex);


            //----------------------------------------------------------------------------------------------------
            //  후판제품야드L2로 저장품제원 정보 전송
            //----------------------------------------------------------------------------------------------------
            szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 적치열[" + szYD_STK_COL_GP + "], 적치베드[" + szYD_STK_BED_NO + "] - 저장품제원 전송 시작 ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
            recInTemp.setField("YD_INFO_SYNC_CD",   "4");                       //1:동,2:SPAN,3:열,4:BED
            recInTemp.setField("YD_GP",             szYD_STK_COL_GP.substring(0, 1));
            recInTemp.setField("YD_STK_COL_GP",     szYD_STK_COL_GP);
            recInTemp.setField("YD_STK_BED_NO",     szYD_STK_BED_NO);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// sndStockSpecToL2 call 시  recInTemp 에 logId SET 추가 개선
            recInTemp.setField("LOG_ID", logId);
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

            YdCommonUtils.sndStockSpecToL2(recInTemp);

            szMsg = "[후판창고야드BOOK_OUT완료수신 - procY8BookOutReq] 적치열[" + szYD_STK_COL_GP + "], 적치베드[" + szYD_STK_BED_NO + "] - 저장품제원 전송 완료 ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            //----------------------------------------------------------------------------------------------------


            //가상 Bed Shift 처리... TERTRB + 7N
            YdCommonUtils.procShiftBedInfoForBookoutLoc3G(szYD_STK_COL_GP1 + szYD_STK_BED_NO1); //BOOK-OUT 위치를 넘겨 준다.


            if(!szYD_STK_COL_GP.substring(2, 4).equals("RT") &&
               !szYD_STK_COL_GP.substring(2, 4).equals("TF")) {
                arrRT_ZONE_NO   = YdCommonUtils.getY4BookOutLoc(szPL_TRCK_ZONE_NO);
                szYD_STK_COL_GP = arrRT_ZONE_NO[0];
            }

            //전문 발생 일시
            szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
            //큐전송 항목 저장 레코드 생성
            recPara = JDTORecordFactory.getInstance().create();
            //JMS TC CODE
            recPara.setField("JMS_TC_CD",          "YDYDJ203");
            //발생 일시
            recPara.setField("JMS_TC_CREATE_DDTT", szDate);
            //적치열구분
            recPara.setField("YD_EQP_ID",          szYD_STK_COL_GP);
            //적치BED번호
            recPara.setField("YD_STK_BED_NO",      szYD_STK_BED_NO);

            if(!szYD_RCPT_PLN_STR_LOC.substring(1,2).equals(szYD_BOOK_OUT_LOC.substring(1,2))) {
                //입고예정위치와 북아웃 위치의 동이 틀리면 입고예정위치의 동을  북아웃 위치의 동으로 변경한다. -- 2013.08.22 윤재광(GJ) : 동이 틀린 곳에서 북아웃 되면 그 동에서 권하위치를 검색하도록 함.
                szYD_RCPT_PLN_STR_LOC = szYD_RCPT_PLN_STR_LOC.substring(0,1) + szYD_BOOK_OUT_LOC.substring(1,2) + szYD_RCPT_PLN_STR_LOC.substring(2);
            }
            //입고예정저장위치
            recPara.setField("YD_RCPT_PLN_STR_LOC",      szYD_RCPT_PLN_STR_LOC);
            //적치재료매수
            recPara.setField("YD_STK_BED_STL_SH",  "" + intYD_STK_BED_STL_SH);
            //대표재료의 길이구분
            recPara.setField("YD_MTL_L_GP",  szYD_MTL_L_GP);
            //작업예약생성시 파일링 작업지시인지를 구분하는 항목(2012.06.04 윤재광 추가)
            recPara.setField("YD_SCH_ST_GP",  szYD_SCH_ST_GP);

            JDTORecord recPtPara = JDTORecordFactory.getInstance().create();
            recPtPara.setField("JMS_TC_CD",          "YDYDJ297");
            recPtPara.setField("YD_STK_BED_STL_SH",  "" + intYD_STK_BED_STL_SH);

            //재료번호
            for (int Loop_i = 1; Loop_i <= intYD_STK_BED_STL_SH; Loop_i++) {
                //재료번호
                recPara.setField("STL_NO" + Loop_i, szSTL_NO[Loop_i]);
                //재료번호
                recPtPara.setField("STL_NO" + Loop_i, szSTL_NO[Loop_i]);
                //권상모음순서
                recPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + (intMtlCnt - Loop_i + 1));

                //2010.12.24 윤재광 - 후판공통 이송일자/시간 업데이트
                recEdit = JDTORecordFactory.getInstance().create();
                recEdit.setField("PLATE_NO",    szSTL_NO[Loop_i]);
                recEdit.setField("WH_FTMV_GP",  (ydDaoUtils.paraRecChkNull(msgRecord,"YD_BOOK_OUT_LOC").startsWith("56")?"A":"B"));
                intRtnVal = ydStockDao.update_Dm_Time(recEdit,5);
                /* ++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            }

            // 진행관리 오버롤체크
            {
                //전문 송신
                ydDelegate.sendMsg(recPtPara);
                szMsg = "A후판 창고 야드 BOOK_OUT 처리 완료후 오버롤 체크완료!";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            }
            // 야드관리 북아웃요구
            {
                //전문 송신
                //ydDelegate.sendMsg(recPara);
                this.procY4CarryOutReq(recPara);

                szMsg = "A후판 창고 야드 BOOK_OUT 처리 완료후 CARRY_OUT 송신 완료!";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            }


        } catch (Exception e) {

            szMsg = "A후판 창고 야드 BOOK_OUT 처리중 ERROR : " + e.getMessage();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

            throw new DAOException(getClass().getName() + e.getMessage(), e);

        }   // end try catch

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  END
        szMsg = "Y8BOOK_OUT 요청 처리(" + szMethodName + ") 완료";
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

    } // end of procY8BookOutReq()


    /**
     * 오퍼레이션명 : 후판제품창고 SPAN별 재고현황 요청 수신  Y8YDL013
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws DAOException
     */
    public void procY8SpanMtlInfo(JDTORecord tcRecord)throws DAOException  {
        String szMsg        = "";
        String szMethodName = "procY8SpanMtlInfo";
        String szOperationName = "후판제품창고 SPAN별 재고현황 요청 수신 ";
        JDTORecord      recEdit     = null;

        String szYD_INFO_SYNC_CD="";
        String szYD_STK_SPAN="";

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= tcRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(tcRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8SPAN별 재고현황 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        YdDelegate ydDelegate = new YdDelegate();

        try{
            szMsg= "["+ szOperationName +"] 수신 후 YDY8L010 송신 처리 시작 " ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);


            szYD_INFO_SYNC_CD = ydDaoUtils.paraRecChkNull(tcRecord,"YD_INFO_SYNC_CD"); //동기화 코드 1:전체SPAN대상 2:특정SPAN대상
            szYD_STK_SPAN   = ydDaoUtils.paraRecChkNull(tcRecord,"YD_STK_SPAN"); //SPAN 위치정보 4자리

            if(!(szYD_INFO_SYNC_CD.equals("1") || szYD_INFO_SYNC_CD.equals("2"))) {

                szMsg = "[전문 이상] 동기화코드가 잘못수신되었습니다.:"+szYD_INFO_SYNC_CD;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return;
            }

            if(szYD_INFO_SYNC_CD.equals("2")){
                if(szYD_STK_SPAN.equals("") || szYD_STK_SPAN.length()!=4){
                    szMsg = "SPAN정보가 잘못되었습니다.:"+szYD_STK_SPAN;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    return;
                }
            }

            recEdit = JDTORecordFactory.getInstance().create();
            recEdit.setField("MSG_ID"           , "YDY8L010");  // ROUTING 정보
            recEdit.setField("YD_INFO_SYNC_CD"          , szYD_INFO_SYNC_CD);   // 1:전체 SPAN 2:특정 SPAN
            recEdit.setField("YD_STK_SPAN"          , szYD_STK_SPAN); // SPAN위치 4자리

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// recEdit 에 logId 추가
            recEdit.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

            ydDelegate.sendMsg(recEdit);

            szMsg= "["+ szOperationName +"] [YDY8L010] 송신 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

        } catch(Exception e){
            szMsg = "후판 SPAN별 재고현황 송신 중 error : " + e.getMessage();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

            throw new DAOException(getClass().getName() + e.getMessage(), e);

        }

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
        szMsg = "Y8SPAN별 재고현황 처리(" + szMethodName + ") 완료";
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        
    }

    /**
     * 오퍼레이션명 : 2후판 제품창고 B동 수동절단장 L2 BOOK_OUT 요청 수신
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord, sGbn(1 = 사내절단장, 2 = 품질검사장, 3 = UT검사장)
     * @return
     * @throws DAOException
     */
    public void procY8BookOutReqSub(JDTORecord tcRecord,String sGbn)throws DAOException  {

        // Book-Out 요청  Y8YDL012
        // CURR_LOC             현재위치            VARCHAR2(8)     RT상의 BOOK-OUT 할 현재 위치
        // MTL_SH               재료매수            VARCHAR2(2)     BOOK-OUT 위치의 재료 매수 (01~06)
        // STL_NO1              1단_재료번호        VARCHAR2(11)    최하단 재료번호
        // STL_NO2              2단_재료번호        VARCHAR2(11)    2단 재료번호
        // STL_NO3              3단_재료번호        VARCHAR2(11)    3단 재료번호
        // STL_NO4              4단_재료번호        VARCHAR2(11)    4단 재료번호
        // STL_NO5              5단_재료번호        VARCHAR2(11)    5단 재료번호
        // STL_NO6              6단_재료번호        VARCHAR2(11)    6단 재료번호
        // TRCK_MTL_SH          트래킹재료매수      VARCHAR2(2)     트래킹 정보 개수
        // TRCK_STL_NO1         트래킹재료번호1 VARCHAR2(11)    BOOK-OUT 위치에서 입고존 방향을 1번째 재료가 있는 위치의 최상단 재료번호
        // TRCK_MTL_SH1         트래킹파일링매수1  VARCHAR2(2)     재료매수
        //    :
        // TRCK_STL_NO6         트래킹재료번호6 VARCHAR2(11)    BOOK-OUT 위치에서 입고존 방향을 6번째 재료가 있는 위치의 최상단 재료번호
        // TRCK_MTL_SH6         트래킹파일링매수6  VARCHAR2(2)     재료매수


        YdStkLyrDao ydStkLyrDao    = new YdStkLyrDao();
        YdDaoUtils ydDaoUtils      = new YdDaoUtils();
        YdStockDao ydStockDao      = new YdStockDao();
        JPlateYdStockDAO ydDao     = new JPlateYdStockDAO();

        int intRtnVal               = 0;
        String szMsg                = "";
        String szMethodName         = "procY8BookOutReqSub";
        String szSubName            = "";
        if("1".equals(sGbn)){
            szSubName           = "수동절단장";
        }else if("2".equals(sGbn)){
            szSubName           = "QA검사장";
        }else if("3".equals(sGbn)){
            szSubName           = "입고RT>대기야드";
        }else if("4".equals(sGbn)){
            szSubName           = "UTRT>완료야드";
        }
        //레코드 선언
        JDTORecord recPara          = null;
        JDTORecord recEdit          = null;

        JDTORecordSet rsResult      = null;


        String szYD_BOOK_OUT_LOC    = null;

        String szDate               = null;
        String szYD_STK_COL_GP      = null;
        String szYD_STK_BED_NO      = null;

        String[] arrRT_ZONE_NO      = null;
        String szPL_MTL_NO          = null;
        //입고예정저장위치
        String szYD_RCPT_PLN_STR_LOC    = null;
        //길이구분
        String szYD_MTL_L_GP            = null;


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= tcRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get

String logId                    		= ydUtils.getJDTOLogId(tcRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "2후판 제품창고 B동 수동절단장 L2 BOOK_OUT 요청 수신(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////
        
        try{

            szPL_MTL_NO         = ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO1"); //최하단 재료번호
            szYD_BOOK_OUT_LOC   = ydDaoUtils.paraRecChkNull(tcRecord,"CURR_LOC");

            if("".equals(szYD_BOOK_OUT_LOC)) {

                szMsg = "[전문 이상] BOOK_OUT 위치가 없습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return;
            }

            /*
             * 분할판 정보 TB_YD_STOCK 테이블에 INSERT 하기
             */
            if("1".equals(sGbn)){

                //레코드 생성
                recPara  = JDTORecordFactory.getInstance().create();
                //레코드셋 생성
                rsResult = JDTORecordFactory.getInstance().createRecordSet("");

                //재료번호
                recPara.setField("STL_NO", szPL_MTL_NO);

                //저장품 테이블 조회
                intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 0);

                //리턴값 메세지처리
                if(intRtnVal == 0) {

                    recEdit = JDTORecordFactory.getInstance().create();

                    recEdit.setField("STL_NO"               , szPL_MTL_NO);
                    recEdit.setField("YD_MTL_T"             , "30");
                    recEdit.setField("YD_MTL_W"             , "4500");
                    recEdit.setField("YD_MTL_L"             , "25000");
                    recEdit.setField("YD_MTL_WT"            , "20000");
                    recEdit.setField("YD_MTL_L_GP"          , "X");         //야드재료길이구분
                    recEdit.setField("YD_MTL_W_GP"          , "L");         //야드재료폭구분
                    recEdit.setField("YD_MTL_STAT"          , "2");
                    recEdit.setField("YD_BOOK_OUT_LOC"      , "67020");     //야드Book_out위치
                    recEdit.setField("YD_RCPT_PLN_STR_LOC"  , "TB010101");  //야드입고예정저장위치

                    intRtnVal = ydStockDao.insYdStock(recEdit);
                }

                {
                    recEdit = JDTORecordFactory.getInstance().create();
                    recEdit.setField("STL_NO"   , szPL_MTL_NO);
                    recEdit.setField("REGISTER",    "TB0101");          // 등록자
                    recEdit.setField("MODIFIER",    "FE0101");          // 수정자

                    intRtnVal = ydDao.insYdStockBookOut(recEdit);
                }
            }

            szYD_MTL_L_GP           = "X";

            if("1".equals(sGbn)){
                szYD_RCPT_PLN_STR_LOC   = "TB010101";
            }else if("2".equals(sGbn)){
                //szYD_RCPT_PLN_STR_LOC     = "TB033101"; //TB033101->TB032801로 변경 요청. 2022.11.30  후판품질팀 서승범 책임. 1673
                szYD_RCPT_PLN_STR_LOC   = "TB032801";
            }else if("3".equals(sGbn)){
                szYD_RCPT_PLN_STR_LOC   = "TCRTUT45";
            }else if("4".equals(sGbn)){

                String szToLoc = ydDaoUtils.paraRecChkNull(tcRecord,"TO_LOC");
                if(!"".equals(szToLoc)){
                    szYD_RCPT_PLN_STR_LOC   = szToLoc;
                }else{
                    szYD_RCPT_PLN_STR_LOC   = "TCRTUT13";
                }
            }

            if(szYD_BOOK_OUT_LOC.length() == 5) { //R/T 모니터링에서 BOOK-OUT 요청한 경우 66080 같이 5자리 숫자로 넘어 온다.

                //파일링 ZONE NO ==> 저장위치 정보 변환
                arrRT_ZONE_NO       = YdCommonUtils.getY4PilingZoneNo2StrLoc(szYD_BOOK_OUT_LOC); //66080 -> TERTRB80 변환 작업
                //RT 적치열구분
                szYD_BOOK_OUT_LOC   = arrRT_ZONE_NO[0] + arrRT_ZONE_NO[1];
                szYD_STK_COL_GP     = arrRT_ZONE_NO[0];
                szYD_STK_BED_NO     = arrRT_ZONE_NO[1];

            } else { // Level2 에서 Y8YDL012 전문으로 BOOK-OUT 요청한 경우는 TERTRB80 같이 8자리 야드위치로 넘어 온다.

                szYD_STK_COL_GP     = szYD_BOOK_OUT_LOC.substring(0,6);
                szYD_STK_BED_NO     = szYD_BOOK_OUT_LOC.substring(6,8);
            }

            szMsg = "[2후판제품창고 B동  "+szSubName+" L2 BOOK_OUT요구 수신 - procY8BookOutReqSub] BOOK-OUT LOC[" + szYD_BOOK_OUT_LOC + "] ===> 야드저장위치 : 적치열[" + szYD_STK_COL_GP + "], 적치베드[" + szYD_STK_BED_NO + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            //적치단 재료상태가 적치 가능이면 재료 등록
            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("YD_STK_COL_GP",       szYD_STK_COL_GP);
            recPara.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);
            recPara.setField("YD_STK_LYR_NO",       "001" );
            recPara.setField("MODIFIER",            "Y8YDL012");
            recPara.setField("YD_STK_LYR_MTL_STAT", "C");
            recPara.setField("STL_NO",              szPL_MTL_NO);

            //업데이트 실행
            intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);

            //전문 발생 일시
            szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
            //큐전송 항목 저장 레코드 생성
            recPara = JDTORecordFactory.getInstance().create();
            //JMS TC CODE
            recPara.setField("JMS_TC_CD",          "YDYDJ203");
            //발생 일시
            recPara.setField("JMS_TC_CREATE_DDTT", szDate);
            //적치열구분
            recPara.setField("YD_EQP_ID",          szYD_STK_COL_GP);
            //적치BED번호
            recPara.setField("YD_STK_BED_NO",      szYD_STK_BED_NO);
            //입고예정저장위치
            recPara.setField("YD_RCPT_PLN_STR_LOC",szYD_RCPT_PLN_STR_LOC);
            //적치재료매수
            recPara.setField("YD_STK_BED_STL_SH",  "1");
            //대표재료의 길이구분
            recPara.setField("YD_MTL_L_GP",  szYD_MTL_L_GP);
            //작업예약생성시 파일링 작업지시인지를 구분하는 항목
            recPara.setField("YD_SCH_ST_GP",  "F");
            //재료번호
            recPara.setField("STL_NO1", szPL_MTL_NO);
            //권상모음순서
            recPara.setField("YD_UP_COLL_SEQ1", "1");

            // 야드관리 북아웃요구
            {
                //전문 송신
                //ydDelegate.sendMsg(recPara);
                this.procY4CarryOutReq(recPara);

                szMsg = "2후판제품창고 B동  "+szSubName+" BOOK_OUT 처리 완료후 CARRY_OUT 송신 완료!";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            }


        } catch (Exception e) {

            szMsg = "2후판제품창고 B동  "+szSubName+" BOOK_OUT 처리중 ERROR : " + e.getMessage();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

            throw new DAOException(getClass().getName() + e.getMessage(), e);

        }   // end try catch

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 START
        szMsg = "2후판 제품창고 B동 수동절단장 L2 BOOK_OUT 요청 수신(" + szMethodName + ") 완료";
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

    } // end of procY8BookOutReqSub()

    /**
     * 오퍼레이션명 : 2후판 제품창고 #2 R/T PILING BOOK_OUT 요청 수신
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord, sGbn(1 = 사내절단장, 2 = 품질검사장, 3 = UT검사장)
     * @return
     * @throws DAOException
     */
    public void procY8BookOutReqPiling(JDTORecord tcRecord)throws DAOException  {

        YdStkLyrDao ydStkLyrDao    = new YdStkLyrDao();
        YdDaoUtils ydDaoUtils      = new YdDaoUtils();
        YdPlateCommDAO commDao     = new YdPlateCommDAO();

        String szMsg                = "";
        String szMethodName         = "procY8BookOutReqSub";
        String szSubName            = "#2R/T 크레인파일링";

        //레코드 선언
        JDTORecord recPara          = null;
        String szYD_BOOK_OUT_LOC    = null;

        String szDate               = null;
        String szYD_STK_COL_GP      = null;
        String szYD_STK_BED_NO      = null;

        String[] arrRT_ZONE_NO      = null;
        String szPL_MTL_NO          = null;
        //입고예정저장위치
        String szYD_RCPT_PLN_STR_LOC    = null;
        //길이구분
        String szYD_MTL_L_GP            = null;

        try{

            szPL_MTL_NO         = ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO1"); //최하단 재료번호
            szYD_BOOK_OUT_LOC   = ydDaoUtils.paraRecChkNull(tcRecord,"CURR_LOC");

            if("".equals(szYD_BOOK_OUT_LOC)) {

                szMsg = "[전문 이상] BOOK_OUT 위치가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return;
            }

            szYD_MTL_L_GP           = "X";
            szYD_RCPT_PLN_STR_LOC   = "TBAP0201";

            if(szYD_BOOK_OUT_LOC.length() == 5) { //R/T 모니터링에서 BOOK-OUT 요청한 경우 66080 같이 5자리 숫자로 넘어 온다.

                //파일링 ZONE NO ==> 저장위치 정보 변환
                arrRT_ZONE_NO       = YdCommonUtils.getY4PilingZoneNo2StrLoc(szYD_BOOK_OUT_LOC); //66080 -> TERTRB80 변환 작업
                //RT 적치열구분
                szYD_BOOK_OUT_LOC   = arrRT_ZONE_NO[0] + arrRT_ZONE_NO[1];
                szYD_STK_COL_GP     = arrRT_ZONE_NO[0];
                szYD_STK_BED_NO     = arrRT_ZONE_NO[1];

            } else { // Level2 에서 Y8YDL012 전문으로 BOOK-OUT 요청한 경우는 TERTRB80 같이 8자리 야드위치로 넘어 온다.

                szYD_STK_COL_GP     = szYD_BOOK_OUT_LOC.substring(0,6);
                szYD_STK_BED_NO     = szYD_BOOK_OUT_LOC.substring(6,8);
            }

            szMsg = "[2후판제품창고 B동  "+szSubName+" L2 BOOK_OUT요구 수신 - procY8BookOutReqSub] BOOK-OUT LOC[" + szYD_BOOK_OUT_LOC + "] ===> 야드저장위치 : 적치열[" + szYD_STK_COL_GP + "], 적치베드[" + szYD_STK_BED_NO + "]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            /*
             * #2UT 크레인파일링 작업생성
             * 1. 파일링작업스케쥴 존재시 해당작업예약 및 스케쥴 정보 삭제
             * 2. 파일링작업 대상재 등록(이미 파일링하고 있는 대상 포함)
             * 3. 스케쥴을 파일링하고 있어야 하는지, R/T에 권하를 해야하는지 판단
             * call SP_YD_PLATE_CRANE_PILER_2DS2(?,?,?,?)
             */
            Object[] inParam = {
                         szPL_MTL_NO
                        ,szYD_STK_COL_GP //TBRTRA
                        ,szYD_STK_BED_NO //15
                       };

            int[] inParamIndex = {1,2,3};

            JDTORecord record = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.callSpQueryId_0006");

            String szYD_SCH_ST_GP = "";

            if(record.size() > 0){
                /*
                 * T : 파일링가능, F : 파일링 SKIP   -- 파일링정보가 없을 경우.
                 * S : 파일링가능, E : 파일링 SKIP   -- 파일링정보가 있는 경우.
                 */
                szYD_SCH_ST_GP = ydDaoUtils.paraRecChkNull(record, "OUT_RTN_CODE");
            }

            szMsg = "[ "+szSessionName+" ] #2UT 크레인파일링 OUT_RTN_CODE="+szYD_SCH_ST_GP ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            JDTORecord recStkLyr = JDTORecordFactory.getInstance().create();
            recStkLyr.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
            recStkLyr.setField("YD_STK_BED_NO", szYD_STK_BED_NO);

            JDTORecordSet rsGetStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
            int intRtnVal = ydStkLyrDao.getYdStklyr(recStkLyr, rsGetStkLyr, 1);

            //전문 발생 일시
            szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
            //큐전송 항목 저장 레코드 생성
            recPara = JDTORecordFactory.getInstance().create();
            //JMS TC CODE
            recPara.setField("JMS_TC_CD",          "YDYDJ203");
            //발생 일시
            recPara.setField("JMS_TC_CREATE_DDTT", szDate);
            //적치열구분
            recPara.setField("YD_EQP_ID",          szYD_STK_COL_GP);
            //적치BED번호
            recPara.setField("YD_STK_BED_NO",      szYD_STK_BED_NO);
            //입고예정저장위치
            recPara.setField("YD_RCPT_PLN_STR_LOC",szYD_RCPT_PLN_STR_LOC);
            //대표재료의 길이구분
            recPara.setField("YD_MTL_L_GP",  szYD_MTL_L_GP);
            //작업예약생성시 파일링 작업지시인지를 구분하는 항목
            recPara.setField("YD_SCH_ST_GP",  szYD_SCH_ST_GP);

            int intMtlCnt = 0;

            for(int Loop_i = 1; Loop_i <= rsGetStkLyr.size(); Loop_i++) {
                rsGetStkLyr.absolute(Loop_i);
                JDTORecord recGetStkLyr = JDTORecordFactory.getInstance().create();
                recGetStkLyr = rsGetStkLyr.getRecord();

                if("".equals(recGetStkLyr.getFieldString("STL_NO") ) ) {
                    break;
                }else{
                    intMtlCnt++;

                    //재료번호
                    recPara.setField("STL_NO"        +Loop_i, recGetStkLyr.getFieldString("STL_NO"));
                    //권상모음순서
                    recPara.setField("YD_UP_COLL_SEQ"+Loop_i, intMtlCnt+"");

                    szMsg = "[ "+szSessionName+" ] #2UT 크레인파일링 STL_NO="+szPL_MTL_NO+" : YD_UP_COLL_SEQ="+intMtlCnt ;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                }
            }
            //적치재료매수
            recPara.setField("YD_STK_BED_STL_SH",  intMtlCnt+"");

            // 야드관리 북아웃요구
            {
                this.procY4CarryOutReq(recPara);

                szMsg = "2후판제품창고 B동  "+szSubName+" BOOK_OUT 처리 완료후 CARRY_OUT 송신 완료!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            }

        } catch (Exception e) {

            szMsg = "2후판제품창고 B동  "+szSubName+" BOOK_OUT 처리중 ERROR : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(getClass().getName() + e.getMessage(), e);

        }   // end try catch

    } // end of procY8BookOutReqPiling()

    /**
     * 오퍼레이션명 : 2후판제품창고L2 입고존 재료정보 수신 (2후판 전용:Y8YDL010)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws DAOException
     */
    public void procY8RcptZoneMtlInfo(JDTORecord tcRecord)throws DAOException  {

        // Y8YDL010 입고존 재료정보
        // RCPT_ZONE_GP         입고존 구분     VARCHAR2(1)     2후판 R/T 구분 (A,B,C)
        // ARR_LEV_GP           도착,출발구분       VARCHAR2(1)     도착(A), 출발(L)
        // MTL_SH               재료매수            VARCHAR2(2)     01~06
        // STL_NO1              1단_재료번호        VARCHAR2(11)    최하단 재료번호
        // STL_NO2              2단_재료번호        VARCHAR2(11)    2단 재료번호
        // STL_NO3              3단_재료번호        VARCHAR2(11)    3단 재료번호
        // STL_NO4              4단_재료번호        VARCHAR2(11)    4단 재료번호
        // STL_NO5              5단_재료번호        VARCHAR2(11)    5단 재료번호
        // STL_NO6              6단_재료번호        VARCHAR2(11)    6단 재료번호


        YdStkLyrDao ydStkLyrDao     = new YdStkLyrDao();
        YdDaoUtils  ydDaoUtils      = new YdDaoUtils();
        YdStockDao  ydStockDao      = new YdStockDao();


        //DELEGATE
        YdDelegate  ydDelegate      = new YdDelegate();

        boolean blnRtnVal           = false;
        int intRtnVal               = 0;
        String szMsg                = "";
        String szMethodName         = "procY8RcptZoneMtlInfo";
        String szOperationName      = "2후판제품창고L2 입고존 재료정보수신";

        //레코드 선언
        JDTORecord      recPara     = null;
        JDTORecord      recInPara   = null;
        JDTORecordSet   rsResult    = null;
        JDTORecordSet   outRecSet1  = null;
        JDTORecord      recTemp     = null;
        JDTORecord      recEdit     = null;
        JDTORecord      recEdit2    = null;
        JDTORecord      setRecord   = null;


        String[] arrRT_ZONE_NO = null;
        String szPL_MTL_NO  = null;

        //야드북아웃위치(zone code)
        String szYD_BOOK_OUT_LOC_CD     = null;


        //RT BOOKOUT 적치열
        String szYD_BOOK_OUT_COL_GP     = null;
        //RT BOOKOUT 적치베드
        String szYD_BOOK_OUT_BED_NO     = null;
        //RT 가상버퍼 적치베드(TO위치)
        String szTO_LOC_BED_NO          = null;
        //재료매수
        int intMTL_SH                   = 0;

        //입고존 도착 출발  구분
        String szARR_LEV_GP             = null;
        //입고존 구분 - 2후판 R/T 구분 (A,B,C)
        String szRCPT_ZONE_GP           = null;

        String szRtnMsg = null;
        String sTrackingGbn = null;
        String szPL_RCPT_TRK_NO = null;

        String szYD_RCPT_PLN_STR_LOC    = null;

        String szAIM_STL_NO             = null; //Routing 지시에 사용
        String szINFO_GP                = null; //Routing 지시에 사용

        double dYD_MTL_L                = 0;
        double dINZN_YD_MTL_L           = 0;

        String szSTL_LOC_8010           = null;
        String szYD_STK_LYR_NO_8010     = null;
        String szSTL_NO_8010            = null;
        String szSTL_LOC_8020           = null;
        String szYD_STK_LYR_NO_8020     = null;
        String szSTL_NO_8020            = null;

        String szUsageYn = "N";

        String szYD_PILING_CD           = null;
        String szORD_NO                 = null;
        String szORD_DTL                = null;
        double dYD_MTL_W                = 0;

        String szSTL_PROG_CD            = "";

        YdPlateCommDAO commDao = new YdPlateCommDAO();


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= tcRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get

String logId                    		= ydUtils.getJDTOLogId(tcRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8입고존 재료정보 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(tcRecord);

        //에러 리턴
        if(szRcvTcCode == null || "".equals(szRcvTcCode) ) {
            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return;
        }

        //TC CODE DISPLAY
        if(bDebugFlag) {
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
        }

        try{

            szMsg = "[후판제품창고L2] 입고존 재료정보 수신 --------------------- 처리 시작 --------------------------";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            //수신 전문에서 입고존 도착,출발구분/ 입고존 구분 / 재료 매수를 추출한다.
            szARR_LEV_GP    = ydDaoUtils.paraRecChkNull(tcRecord,"ARR_LEV_GP"); //입고존 도착(A), 출발(L) 구분
            szRCPT_ZONE_GP  = ydDaoUtils.paraRecChkNull(tcRecord,"RCPT_ZONE_GP"); //입고존 구분 - 2후판 R/T 구분 (A,B,C, T)
            intMTL_SH       = ydDaoUtils.paraRecChkNullInt(tcRecord,"MTL_SH");  //재료매수

            //수신전문의 입고존구분에 따라 sTrackingGbn 값을 설정한다. (1후판과 달리 2후판은 후판작업공정코들를 사용하지 않고 Zone 코드 앞 2자리를 사용한다.)
            if("C".equals(szRCPT_ZONE_GP)){
                sTrackingGbn = "68";    // C R/T  - OFF-LINE
            } else if("B".equals(szRCPT_ZONE_GP)) {
                sTrackingGbn = "66";    // B R/T  - ON-LINE
            } else if("A".equals(szRCPT_ZONE_GP)) {
                sTrackingGbn = "67";    // A R/T  - ON-LINE
            } else if("T".equals(szRCPT_ZONE_GP)) {
                sTrackingGbn = "66";    // B R/T  - ON-LINE (CPL-LINE --> TRANS BED--> NO1 DS RT로 들어온 경우 66020 존에서 입고존 재료정보를 보내주기로 함)
                szARR_LEV_GP = "L";     //출발 정보로 강제 설정
            } else {
                //RCPT_ZONE_GP 가 'A','B','C','T' 가 아니면 Error
                szMsg = "[후판제품창고L2] 입고존 재료정보 수신 : 입고존 구분 값이 A,B,C,T 가 아닙니다!! RCPT_ZONE_GP : "+szRCPT_ZONE_GP;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                return;
            }

            //--------------------------------------------------------------------------------------------------
            //최하단 재료로 YD저장품을 조회한다.
            szPL_MTL_NO = ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO1"); //최하단 1단 재료번호

            //----------------------------------------------------------------------------------------------------------------------
            //----------------------------------------------------------------------------------------------------------------------
            //----------------------------------------------------------------------------------------------------------------------
            //  입고존 도착시 2후판 #2DS B동 크레인 파일링 지시여부 셋팅 시작
            //----------------------------------------------------------------------------------------------------------------------
            //----------------------------------------------------------------------------------------------------------------------
            //----------------------------------------------------------------------------------------------------------------------
            if("A".equals(szRCPT_ZONE_GP)){     // A R/T일 경우

                //-- 통합 크레인 스케줄 조회 -----------------------------------------------------------------start--
                rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                recInPara = JDTORecordFactory.getInstance().create();

                recInPara.setField("YD_SCH_CD", "TBRTRAAP");

                intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0095");

                //레코드 추출
                rsResult.first();
                recPara = rsResult.getRecord();
                //-- 통합 크레인 스케줄 조회 ------------------------------------------------------------------end---

                szMsg = "[후판제품창고L2] 입고존 도착시 대상 파일링 설비 [WRK_CRN]: "+ydDaoUtils.paraRecChkNull(recPara, "WRK_CRN")+"[CRN_PRIOR]:"+ydDaoUtils.paraRecChkNull(recPara, "CRN_PRIOR");
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                //B동 크레인을 파일링설비로 셋팅을 했는지 여부 체크
                if(!"".equals(ydDaoUtils.paraRecChkNull(recPara, "WRK_CRN"))&&
                   !"".equals(ydDaoUtils.paraRecChkNull(recPara, "CRN_PRIOR"))){

                    if("A".equals(szARR_LEV_GP)) {  // 입고존 도착일 경우
                        boolean isTrue = this.procY8RcptZoneMtlInfo_Sub01(tcRecord);

                        if(isTrue){
                            szMsg = "[후판제품창고L2] 입고존 도착시 2후판 #2DS B동 크레인 파일링 지시 송신성공 STL_NO : "+szPL_MTL_NO;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                            return;
                        }
                    }else{                          // 입고존 출발일 경우
                        boolean isTrue = this.procY8RcptZoneMtlInfo_Sub02(tcRecord);

                        if(isTrue){
                            szMsg = "[후판제품창고L2] 입고존 출발시 2후판 #2DS B동 크레인 파일링 지시 송신대상 STL_NO : "+szPL_MTL_NO;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                            return;
                        }
                    }
                }
            }
            //----------------------------------------------------------------------------------------------------------------------
            //----------------------------------------------------------------------------------------------------------------------
            //----------------------------------------------------------------------------------------------------------------------
            //  입고존 도착시 2후판 #2DS B동 크레인 파일링 지시여부 셋팅 종료
            //----------------------------------------------------------------------------------------------------------------------
            //----------------------------------------------------------------------------------------------------------------------
            //----------------------------------------------------------------------------------------------------------------------

            //----------------------------------------------------------------------------------------------------------------------
            //  입고존 도착시 TO위치 결정로직 수행 시작
            //----------------------------------------------------------------------------------------------------------------------
            String sHmiStat         = "N";
            String sQuery1          = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
            JDTORecord wbJr         = (new com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO()).getData(sQuery1, new Object[]{ "2XRT02" });
            if (wbJr != null){
                sHmiStat    = StringHelper.evl(wbJr.getFieldString("HMI_STAT"), "");
            }
            if("Y".equals(sHmiStat)){

                if("A".equals(szARR_LEV_GP)) {

                    String sRtGp    = "";

                    if("A".equals(szRCPT_ZONE_GP)){
                        sRtGp = "A";    // A R/T  - ON-LINE
                    } else if("B".equals(szRCPT_ZONE_GP)) {
                        sRtGp = "B";    // B R/T  - ON-LINE
                    } else if("C".equals(szRCPT_ZONE_GP)) {
                        sRtGp = "B";    // C R/T  - OFF-LINE
                    }

                    EJBConnector ejbConn = new EJBConnector("default", "RcptWrkDmdSeEJB", this);
                  
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ejbConn.trx("procPreMainWrkToLocForPlateYd", new  Class[] { String.class, String.class},
//                          new Object[] { szPL_MTL_NO , sRtGp });
                    ejbConn.trx("procPreMainWrkToLocForPlateYd", new  Class[] { String.class, String.class, String.class, String.class},
                            new Object[] { szPL_MTL_NO , sRtGp, "", logId});
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
                }
            }
            //----------------------------------------------------------------------------------------------------------------------
            //  입고존 도착시 TO위치 결정로직 수행 종료
            //----------------------------------------------------------------------------------------------------------------------

            rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            blnRtnVal = this.chkStock(szPL_MTL_NO, rsResult);

            if(!blnRtnVal) {
                //저장품에 존재하지 않으면 여기서 종료
                szMsg = "[후판제품창고L2] 입고존 재료정보 수신 : 최하단재료 YD저장품  검색 실패!! STL_NO : "+szPL_MTL_NO;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                return;
            }

            rsResult.first();
            recInPara = rsResult.getRecord();

            //저장품의 Book-out 위치 코드를 YD저장품에서 읽어 온다. (여기서 읽은 BOOKOUT 위치는 압연지시수신시 1차적으로 만들어진 BOOK-OUT 위치이다.)
            szYD_BOOK_OUT_LOC_CD    = ydDaoUtils.paraRecChkNull(recInPara, "YD_BOOK_OUT_LOC");  //저장품의 야드BookOut위치(ex:66065)
            //입고예정위치
            szYD_RCPT_PLN_STR_LOC   = ydDaoUtils.paraRecChkNull(recInPara, "YD_RCPT_PLN_STR_LOC");
            //제품길이
            //dYD_MTL_L                 = ydDaoUtils.paraRecChkNullDouble(recInPara, "YD_MTL_L");
            //파일링코드
            szYD_PILING_CD          = ydDaoUtils.paraRecChkNull(recInPara, "YD_PILING_CD");
            //제품폭
            dYD_MTL_W               = ydDaoUtils.paraRecChkNullDouble(recInPara, "YD_MTL_W");
            //주문번호
            szORD_NO                = ydDaoUtils.paraRecChkNull(recInPara, "ORD_NO");
            //주문행번
            szORD_DTL               = ydDaoUtils.paraRecChkNull(recInPara, "ORD_DTL");

            szSTL_PROG_CD           = ydDaoUtils.paraRecChkNull(recInPara, "STL_PROG_CD");

            //--------------------------------------------------------------------------------------------------
            //입고Zone 도착, 출발에 따른 처리
            if("A".equals(szARR_LEV_GP)) {
                //=========================================================================================
                //입고 존 도착 처리

                //--------------------------------------------------------------------------------------------------
                // YD저장품에서 읽은 Book-out위치코드가 '77777', '88888' 과 같이 반난 후 재입고 되는 경우 (생산실적이 늦게 발생하여 book-out 위치 코드가 변경 안된 경우)
                // 여기서 book-out 위치 코드를 설정한다.
                if("".equals(szYD_BOOK_OUT_LOC_CD)||"".equals(szYD_RCPT_PLN_STR_LOC)
                        ||szYD_BOOK_OUT_LOC_CD.startsWith("5")||szYD_RCPT_PLN_STR_LOC.startsWith("K")
                        ||(!szYD_BOOK_OUT_LOC_CD.startsWith("66")&&!szYD_BOOK_OUT_LOC_CD.startsWith("67")&&!szYD_BOOK_OUT_LOC_CD.startsWith("68"))) {

                    PtOsCommDao ptOsCommDao     = new PtOsCommDao();

                    JDTORecordSet outRecSet9  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
                    outRecSet1  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
                    JDTORecord    outRec9     = JDTORecordFactory.getInstance().create();
                    String        szPLAN_DONG = null;

                    recPara = JDTORecordFactory.getInstance().create();


                    recPara.setField("ORD_NO",      szORD_NO);
                    recPara.setField("ORD_DTL",     szORD_DTL);

                    //저장계획 코드 Read
                    /*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommLocPlanCd_PIDEV*/
                    intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet1, 300);

                    if (intRtnVal <= 0) {
                        szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL + " 저장계획 코드 Read error!!!, ErrorCode:" + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                        //continue ;
                    }

                    outRecSet1.absolute(1);
                    JDTORecord outRec1 = JDTORecordFactory.getInstance().create();
                    outRec1 = outRecSet1.getRecord();

                    String szLOC_PLAN_CD        = ydDaoUtils.paraRecChkNull(outRec1,"LOC_PLAN_CD");
                    String szMAIN_TRANS_AREA    = ydDaoUtils.paraRecChkNull(outRec1,"MAIN_TRANS_AREA");
                    String szYD_PILING_CD2      = szYD_PILING_CD;

                    
                    /*
        			 * 2024.09.13 후판동별저장계획 화면 개선요청 임진후 기사 요청 --REQ202408611796
        			 * 수출재 신규고객사 추가. 고객사별 개별셋팅을 하기때문에 szLOC_PLAN_CD 는 버리고 파일링코드 앞 4자리 사용
        			 * 
        			 * */
        			YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
        			String szORD_GP = szORD_NO.substring(0,1);
        			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "002");
        			if ("Y".equals(sApplyYnPI) && ((szORD_GP.equals("E")) || (szORD_GP.equals("F")) ) ){
        				szMsg = "신규 동별저장계획기준 해당주문 :"+ szORD_NO + "-" +szORD_DTL +"권역구분["+szLOC_PLAN_CD+ "] 대신 ["+szYD_PILING_CD.substring(0,4)+"]사용";
        				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
        				szLOC_PLAN_CD = szYD_PILING_CD.substring(0,4);
        			}
        			
                    
                    
                    recPara.setField("YD_PILING_CD",    szYD_PILING_CD2);
                    recPara.setField("LOC_PLAN_CD",     szLOC_PLAN_CD);
                    recPara.setField("YD_GP",           YdConstant.YD_GP_PLATE2_GDS_YARD);
                    recPara.setField("MAIN_TRANS_AREA", szMAIN_TRANS_AREA);
                    recPara.setField("PTOP_PLNT_GP",    "PB");

                    /*com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0059*/
                    intRtnVal = commDao.select(recPara, outRecSet9, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0059");
                    if (intRtnVal <= 0) {
                        szMsg = "해당 Piling코드  :"+ szYD_PILING_CD +") Access저장동  Read error!!!:" + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                    } else {
                        outRecSet9.absolute(1);
                        outRec9 = JDTORecordFactory.getInstance().create();
                        outRec9 = outRecSet9.getRecord();

                        szPLAN_DONG = ydDaoUtils.paraRecChkNull(outRec9,"DONG");

                        if(!"".equals(szPLAN_DONG)) {

                            recTemp     = JDTORecordFactory.getInstance().create();

                            String sRTN_LOC         = null;
                            String sRTN_BOOKOUT_LOC = null;

                            String szPTOP_PLNT_GP   = "PB";
                            //-------------------------------------------------------
                            //동이 정해졌으면 그 동에서 적치가능한 LOC 를 구한다.
                            recTemp.setField("YD_GP",           YdConstant.YD_GP_PLATE2_GDS_YARD);
                            recTemp.setField("YD_BAY_GP",       szPLAN_DONG);
                            recTemp.setField("YD_PILING_CD",    szYD_PILING_CD);
                            recTemp.setField("PTOP_PLNT_GP",    szPTOP_PLNT_GP);

                            sRTN_LOC = YdToLocDcsnUtil.getYdBayLocPln3G(recTemp);

                            /*
                             * 2014.10.15 윤재광 - 이명운대리 요청
                             * G동 중척재이하는 무조건 2베드로 셋팅
                             */
                            if("G".equals(szPLAN_DONG) && ("M".equals(szYD_PILING_CD.substring(6,7))||
                                                           "S".equals(szYD_PILING_CD.substring(6,7))||
                                                           "U".equals(szYD_PILING_CD.substring(6,7)))){
                                recTemp.setField("YD_STK_BED_NO",   "02");
                            }else{
                                recTemp.setField("YD_STK_BED_NO",   sRTN_LOC.substring(6,8));
                            }

                            //업무기준 : YDB674 (후판제품창고 BOOKOUT_LOC 결정 기준) 야드구분값 셋팅
                            if("PA".equals(szPTOP_PLNT_GP)){
                                recTemp.setField("YD_GP",   "K");
                            }else{
                                recTemp.setField("YD_GP",   "T");
                            }

                            //업무기준 : YDB674 (후판제품창고 BOOKOUT_LOC 결정 기준)
                            if( GetBreRule6.getYDB674(recTemp) ) {
                                sRTN_BOOKOUT_LOC = StringHelper.evl(recTemp.getFieldString("YDB674_RV01_YD_BOOK_OUT_LOC"), "00000"); // 업무기준 YDB674 반환값#1 YD_BOOK_OUT_LOC
                            } else {
                                sRTN_BOOKOUT_LOC ="00000";
                            }

                            szYD_BOOK_OUT_LOC_CD    = sRTN_BOOKOUT_LOC;
                            szYD_RCPT_PLN_STR_LOC   = sRTN_LOC;
                        }

                    }

                }


                //--------------------------------------------------------------------------------------------------
                //YD저장품에서 읽은 Book-out위치코드 앞자리와  입고존 sTrackingGbn 값이 다르면 YD저장품의 Book-out위치코드값을 수정한다.
                if(!szYD_BOOK_OUT_LOC_CD.startsWith(sTrackingGbn)) {

                    if("68".equals(sTrackingGbn) && (szYD_BOOK_OUT_LOC_CD.startsWith("66")||szYD_BOOK_OUT_LOC_CD.startsWith("67") )) {
                        //입고존이 68000 인데 Bookout코드가 66,67로 시작하면 Bookout 코드를 66으로 수정
                        //단 입고예정동이 B동일경우는 68로 수정
                        if(szYD_RCPT_PLN_STR_LOC.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "B")) {
                            szYD_BOOK_OUT_LOC_CD = "68" + szYD_BOOK_OUT_LOC_CD.substring(2);
                        } else {
                            szYD_BOOK_OUT_LOC_CD = "66" + szYD_BOOK_OUT_LOC_CD.substring(2);
                        }
                    } else if("67".equals(sTrackingGbn) && (szYD_BOOK_OUT_LOC_CD.startsWith("66")||szYD_BOOK_OUT_LOC_CD.startsWith("68"))) {
                        //입고존이 67000 인데 Bookout코드가 66,68로 시작하면 Bookout 코드를 67으로 수정
                        szYD_BOOK_OUT_LOC_CD = "67" + szYD_BOOK_OUT_LOC_CD.substring(2);
                    } else if("66".equals(sTrackingGbn) && (szYD_BOOK_OUT_LOC_CD.startsWith("67")||szYD_BOOK_OUT_LOC_CD.startsWith("68"))) {
                        //입고존이 66000 인데 Bookout코드가 67,68로 시작하면 Bookout 코드를 66으로 수정
                        szYD_BOOK_OUT_LOC_CD = "66" + szYD_BOOK_OUT_LOC_CD.substring(2);
                    }
                }
                //-----------------------------------------------------------------------------------------------------------------------------

                //-- Step1 Start ---------------------------------------------------------------------------------------------
                szAIM_STL_NO            = ""; //Routing 지시에 사용
                szINFO_GP               = "1"; //Routing 지시에 사용

                //--------------------------------------------------------------------------------------------------
                //-- 이부분에서 ROUTING 조건 판단 로직(양안승 위원)이 들어 가야 한다. 결과적으로 최종 BOOK-OUT 위치가 만들어진다.
                if("C".equals(szRCPT_ZONE_GP) && szYD_RCPT_PLN_STR_LOC.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "B")) {
                    //CPL 입고대기존 + 입고예정위치 동구분 == "B" 이면

                    if(szYD_RCPT_PLN_STR_LOC.endsWith("01")) { //01베드이면
                        szYD_BOOK_OUT_LOC_CD = "68020";
                    } else {
                        szYD_BOOK_OUT_LOC_CD = "68010";
                    }

                } else {

                    //입고대기제품들에서 가장 긴 길이를 구한다.
                    recInPara = JDTORecordFactory.getInstance().create();
                    recInPara.setField("STL_NO1", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO1"));
                    recInPara.setField("STL_NO2", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO2"));
                    recInPara.setField("STL_NO3", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO3"));
                    recInPara.setField("STL_NO4", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO4"));
                    recInPara.setField("STL_NO5", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO5"));
                    recInPara.setField("STL_NO6", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO6"));

                    intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0078");

                    rsResult.first();
                    recInPara = rsResult.getRecord();
                    //제품최대길이
                    dYD_MTL_L   = ydDaoUtils.paraRecChkNullDouble(recInPara, "YD_MTL_L");

                    //********************************************
                    //**    2후판 입고 최종 BOOK OUT 위치를 찾는다.   **
                    //********************************************
                    if(szYD_RCPT_PLN_STR_LOC.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "B")) {
                        //B동은 A-RT일 경우 Transfer Zone 코드로 변경한다.- 그외는 압연지시수신시 결정된 BOOK-OUT 위치를 그대로 사용한다.
                   
                        szUsageYn = "N";
                        recPara = JDTORecordFactory.getInstance().create();
                        //업무기준 : YDB670 (3기 기능 적용여부 (테스트용))
                        recPara.setField("3G_FNC_ID", "TBTF01isEnableYn"); //TBTF01사용가능여부(Y:가능)
                        if( GetBreRule6.getYDB670(recPara) ) {
                            szUsageYn = StringHelper.evl(recPara.getFieldString("YDB670_RV01_USAGE_YN"), "N"); // 업무기준 YDB670 반환값#1 사용여부
                        } else {
                            szUsageYn ="Y";
                        }

                        if("Y".equals(szUsageYn)) { //TBTF01이 사용 가능 할 경우만 Transfer Zone 코드로 변경한다.
                            if( "67020".equals(szYD_BOOK_OUT_LOC_CD)) {
                                szYD_BOOK_OUT_LOC_CD = "67216";
                            } else if( "67010".equals(szYD_BOOK_OUT_LOC_CD)) {
                                szYD_BOOK_OUT_LOC_CD = "67206";
                            }
                        }

                    } else if (szYD_RCPT_PLN_STR_LOC.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "C")) {
                        //C동은 B-RT일 경우 Transfer Zone 코드로 변경한다. - 그외는 압연지시수신시 결정된 BOOK-OUT 위치를 그대로 사용한다.
                    	String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szMethodName, "APP060", "T", "023");
            			
                    	if("Y".equals(sApplyYnPI)){
                    		 if(szYD_RCPT_PLN_STR_LOC.endsWith("01") && dYD_MTL_L < 17950) {
 	                            szYD_BOOK_OUT_LOC_CD = szYD_BOOK_OUT_LOC_CD.substring(0,2) + "035";
 	                        }
                    	}
                    	else {
                    	
	                        //C동은 입고예정위 Bed 구분이 = '01' 이고 제품길이 < 18,601 이면 02BED -- 2013.11.26
	                        if(szYD_RCPT_PLN_STR_LOC.endsWith("01") && dYD_MTL_L < 18601) {
	                            szYD_BOOK_OUT_LOC_CD = szYD_BOOK_OUT_LOC_CD.substring(0,2) + "035";
	                        }
                    	}

                        szUsageYn = "N";
                        recPara = JDTORecordFactory.getInstance().create();
                        //업무기준 : YDB670 (3기 기능 적용여부 (테스트용))
                        recPara.setField("3G_FNC_ID", "TCTF01isEnableYn"); //TCTF01사용가능여부(Y:가능)
                        if( GetBreRule6.getYDB670(recPara) ) {
                            szUsageYn = StringHelper.evl(recPara.getFieldString("YDB670_RV01_USAGE_YN"), "N"); // 업무기준 YDB670 반환값#1 사용여부
                        } else {
                            szUsageYn ="Y";
                        }

                        if("Y".equals(szUsageYn)) { //TCTF01이 사용 가능 할 경우만 Transfer Zone 코드로 변경한다.
                            if("66040".equals(szYD_BOOK_OUT_LOC_CD)) {
                                szYD_BOOK_OUT_LOC_CD = "66226";   //2013.11.26 추가됨
                            } else if( "66035".equals(szYD_BOOK_OUT_LOC_CD)) {
                                szYD_BOOK_OUT_LOC_CD = "66216";
                            } else if( "66025".equals(szYD_BOOK_OUT_LOC_CD) || "66030".equals(szYD_BOOK_OUT_LOC_CD)) {
                                szYD_BOOK_OUT_LOC_CD = "66206";
                            }
                        }

                    } else if (szYD_RCPT_PLN_STR_LOC.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "D")) {
                        //D동은 입고예정위 Bed 구분이 = '01' 이고 제품길이 < Crane#1 Beam Min Size 이면
                        if(szYD_RCPT_PLN_STR_LOC.endsWith("01") && dYD_MTL_L < 9220) {
                            //szYD_BOOK_OUT_LOC_CD = sTrackingGbn + "055"; // A:67055, B:66055
                            szYD_BOOK_OUT_LOC_CD = szYD_BOOK_OUT_LOC_CD.substring(0,2) + "055"; // A:67055, B:66055
                        }
                    } else if (szYD_RCPT_PLN_STR_LOC.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "E")) {
                    	/*
						 * 24.09.10 임진후 기사 요청. E동 RT 에서  초단척재가 65, 80으로 들어가지않고 70,75 로만 들어감
						 * 이 기준을 단척재에도 적용하게끔 요청.
						 * --> 기준 찾아보니, 초단척 코드기준이 아닌 아래 조건문의 길이 기준으로 적용하고있음.
						 * 
						 * 24.09.20 임진후 기사 재요청. 이렇게 운용하다보니, 단척재가 75 zone, 초단척재가 70 으로 지시받은경우 겹침현상 발생
						 * RT zone 별로 크기는 7M. 단척채 9M 짜리가 들어올시, 센터 맞추면 위아래로 1M씩 초과 
						 * 단척재가 75 로 들어온경우 70 zone 에 1M 침범하여 정지하고 그상태에서 70 존에 재료가 들어오면 재료가 겹친다.
						 * 따라서, 단척재 (6801 ~ 9200) 는 70 zone 으로 가야한다. (어차피 65,80 은 안쓰니 70에 오는건 상관없음)
						 */	
						String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "001");
				
						if("Y".equals(sApplyYnPI)){
							//E동은 입고예정위 Bed 구분이 = '01' 이고 제품길이 < Crane#1 Beam Min Size 이면 
							if(szYD_RCPT_PLN_STR_LOC.endsWith("01") && dYD_MTL_L < 6801) { 
								szYD_BOOK_OUT_LOC_CD = szYD_BOOK_OUT_LOC_CD.substring(0,2) + "075"; // A:67075, B:66075
							} else if ( szYD_RCPT_PLN_STR_LOC.endsWith("01") && (dYD_MTL_L >= 6801 && dYD_MTL_L < 9200)){
								szYD_BOOK_OUT_LOC_CD = szYD_BOOK_OUT_LOC_CD.substring(0,2) + "070"; // A:67075, B:66075
							} else if(szYD_RCPT_PLN_STR_LOC.endsWith("04") && dYD_MTL_L < 9200) {
								//E동은 입고예정위 Bed 구분이 = '04' 이고 제품길이 < Crane#1 Beam Min Size - 700 이면  03 Bed
								szYD_BOOK_OUT_LOC_CD = szYD_BOOK_OUT_LOC_CD.substring(0,2) + "070"; // A:67070, B:66070
							} else if(szYD_RCPT_PLN_STR_LOC.endsWith("04") && dYD_MTL_L >= 6400) {
								//E동은 입고예정위 Bed 구분이 = '04' 이고 제품길이가 6400 이상일때   03 Bed
								szYD_BOOK_OUT_LOC_CD = szYD_BOOK_OUT_LOC_CD.substring(0,2) + "070"; // A:67070, B:66070
							}
						}
						else{
							//E동은 입고예정위 Bed 구분이 = '01' 이고 제품길이 < Crane#1 Beam Min Size 이면
	                        if(szYD_RCPT_PLN_STR_LOC.endsWith("01") && dYD_MTL_L < 6820) {
	                            //szYD_BOOK_OUT_LOC_CD = sTrackingGbn + "075"; // A:67075, B:66075
	                            szYD_BOOK_OUT_LOC_CD = szYD_BOOK_OUT_LOC_CD.substring(0,2) + "075"; // A:67075, B:66075
	                        } else if(szYD_RCPT_PLN_STR_LOC.endsWith("04") && dYD_MTL_L < (6820-700)) {
	                            //E동은 입고예정위 Bed 구분이 = '04' 이고 제품길이 < Crane#1 Beam Min Size - 700 이면  03 Bed
	                            szYD_BOOK_OUT_LOC_CD = szYD_BOOK_OUT_LOC_CD.substring(0,2) + "070"; // A:67070, B:66070
	                        } else if(szYD_RCPT_PLN_STR_LOC.endsWith("04") && dYD_MTL_L >= 6400) {
	                            //E동은 입고예정위 Bed 구분이 = '04' 이고 제품길이가 6400 이상일때   03 Bed
	                            szYD_BOOK_OUT_LOC_CD = szYD_BOOK_OUT_LOC_CD.substring(0,2) + "070"; // A:67070, B:66070
	                        }
						}
                    	
                        /*
                         * 2016.08.01 윤재광
                         * 노형준 요청 01베드 위치시 02으로 강제변경 요청
                         */
                        if("66065".equals(szYD_BOOK_OUT_LOC_CD)){
                            szYD_BOOK_OUT_LOC_CD = "66070";
                        }else if("67065".equals(szYD_BOOK_OUT_LOC_CD)){
                            szYD_BOOK_OUT_LOC_CD = "67070";
                        }

                    } else if (szYD_RCPT_PLN_STR_LOC.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "F")) {
                        //F동은 입고예정위 Bed 구분이 = '01' 이고 제품길이 < 11,340 이면
                        //if(szYD_RCPT_PLN_STR_LOC.endsWith("01") && dYD_MTL_L < 11340) {
                        //F동은 입고예정위 Bed 구분이 = '01' 이고 제품길이 < 12,670 이면
                        //if(szYD_RCPT_PLN_STR_LOC.endsWith("01") && dYD_MTL_L < 12670) {
                        //F동은 입고예정위 Bed 구분이 = '01' 이고 제품길이 < Crane#1 Beam Min Size - 800 이면  03 Bed
                        if(szYD_RCPT_PLN_STR_LOC.endsWith("01") && dYD_MTL_L < (12670-800)) {
                            //szYD_BOOK_OUT_LOC_CD = sTrackingGbn + "095"; // A:67095, B:66095
                            szYD_BOOK_OUT_LOC_CD = szYD_BOOK_OUT_LOC_CD.substring(0,2)+ "095"; // A:67095, B:66095 <--2013.09.09 66090이 66095 으로 변경
                            //szYD_BOOK_OUT_LOC_CD = szYD_BOOK_OUT_LOC_CD.substring(0,2)+ "090"; // A:67090, B:66090 <--2013.08.02 66095가 66090 으로 변경
                        }
                    } else if (szYD_RCPT_PLN_STR_LOC.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "G")) {
                        //G동은 입고예정위 Bed 구분이 = '01' 이고 제품길이 < 9,220 이면 + '03' 이고 제품길이 > 9,220 이면(2013.11.25 or 조건 추가)
                        //if((szYD_RCPT_PLN_STR_LOC.endsWith("01") && dYD_MTL_L < 9220) || (szYD_RCPT_PLN_STR_LOC.endsWith("03") && dYD_MTL_L > 9220)) {
                        if((szYD_RCPT_PLN_STR_LOC.endsWith("01") && dYD_MTL_L < 13820) || (szYD_RCPT_PLN_STR_LOC.endsWith("03") && dYD_MTL_L > 13820)) {
                            //szYD_BOOK_OUT_LOC_CD = sTrackingGbn + "110"; // A:67110, B:66110
                            //szYD_BOOK_OUT_LOC_CD = szYD_BOOK_OUT_LOC_CD.substring(0,2) + "110"; // A:67110, B:66110
                            szYD_BOOK_OUT_LOC_CD = szYD_BOOK_OUT_LOC_CD.substring(0,2) + "115"; // A:67115, B:66115 <--2013.09.11 66110 이 66115 로 변경
                        }

                        /*
                         * 2016.08.01 윤재광
                         * 노형준 요청 무조건 115로 셋팅
                         */
                        szYD_BOOK_OUT_LOC_CD = szYD_BOOK_OUT_LOC_CD.substring(0,2) + "115";
                    }
                }
                //-- Step1 end ---------------------------------------------------------------------------------------------
                if(!"C".equals(szSTL_PROG_CD)) {
                    //Y 이면 TransBed 파일링을 하기위한 처리를 한다.
                    /*
                     * 2016.01.14 UT검사재 파일링처리 안되게 막음
                     */

                    if("C".equals(szRCPT_ZONE_GP)
                        && !szYD_BOOK_OUT_LOC_CD.equals("68010")
                        && !szYD_BOOK_OUT_LOC_CD.equals("68020"))  {

                        szAIM_STL_NO            = "";
                        szINFO_GP               = "4"; //파일링

                        //---------------------------------------------------------------------------------------------
                        //입고 존 도착시 C-RT 이면서,  BOOKOUT 위치가 68010, 68020 아니면
                        //Trans Bed 파일링 대상임으로 입고 R/T 가상 버퍼에 등록하는 작업을 하지 않고
                        //Trans Bed 에서 파일링 가능한 제품이 있는 위치를 찾고 그 위치를 Routing 지시를 준다.

                        //Trans Bed에서 입고존 도착하는 제품이 파일링 될 수 있는 위치가 존재하는지 쿼리로 찾는다.
                        rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                        recInPara = JDTORecordFactory.getInstance().create();
                        recInPara.setField("STL_NO1", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO1"));
                        recInPara.setField("STL_NO2", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO2"));
                        recInPara.setField("STL_NO3", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO3"));
                        recInPara.setField("STL_NO4", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO4"));
                        recInPara.setField("STL_NO5", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO5"));
                        recInPara.setField("STL_NO6", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO6"));

                        intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0070");

                        if( intRtnVal <= 0 ) {
                            //파일링할 수 있는 위치를 찾지 못 함
                            szAIM_STL_NO  = "";
                            szINFO_GP   = "1"; //입고로 변경

                            //양위원님 문서에는 제품길이가 13000 보다 크고 작을 경우에 따라 처리하는 내용이 있지만
                            //아래 Routing 지시 전문으로는  8010, 8020으로 가라는 정보를 줄 수 없어 처리 하지 않고
                            //최종 위치로 Routing 지시를 내린다.

                        } else {
                            //파일링할 수 있는 위치를 찾았을 경우

                            //8010 Zone에 동일 파일링코드와 Book-out 위치가 같은 적치된 제품이 있는지 확인 (맨 처음 발견된 곳에서 stop)
                            szSTL_LOC_8010 = "";
                            szYD_STK_LYR_NO_8010 = "001";
                            szSTL_NO_8010 = "";

                            for(int Loop_i = 1; Loop_i <= intRtnVal; Loop_i++) {
                                rsResult.absolute(Loop_i);
                                recInPara = rsResult.getRecord();

                                szSTL_LOC_8010 =    ydDaoUtils.paraRecChkNull(recInPara, "STL_LOC_8010");

                                if(!"".equals(szSTL_LOC_8010)) {
                                    szSTL_NO_8010   = ydDaoUtils.paraRecChkNull(recInPara, "STL_NO_8010");
                                    dINZN_YD_MTL_L  = ydDaoUtils.paraRecChkNullDouble(recInPara, "INZN_YD_MTL_L");
                                    szYD_STK_LYR_NO_8010 = ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_LYR_NO_8010");

                                    break;
                                }
                            }

                            //8020 Zone에 동일 파일링코드와 Book-out 위치가 같은 적치된 제품이 있는지 확인 (맨 처음 발견된 곳에서 stop)
                            szSTL_LOC_8020 = "";
                            szYD_STK_LYR_NO_8020 = "001";
                            szSTL_NO_8020 = "";

                            for(int Loop_i = 1; Loop_i <= intRtnVal; Loop_i++) {
                                rsResult.absolute(Loop_i);
                                recInPara = rsResult.getRecord();

                                szSTL_LOC_8020 =    ydDaoUtils.paraRecChkNull(recInPara, "STL_LOC_8020");

                                if(!"".equals(szSTL_LOC_8020)) {
                                    szSTL_NO_8020 = ydDaoUtils.paraRecChkNull(recInPara, "STL_NO_8020");
                                    dINZN_YD_MTL_L  = ydDaoUtils.paraRecChkNullDouble(recInPara, "INZN_YD_MTL_L");
                                    szYD_STK_LYR_NO_8020 = ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_LYR_NO_8020");

                                    break;
                                }
                            }

                            if(!"".equals(szSTL_LOC_8010) && !"".equals(szSTL_LOC_8020)) {
                                //입고예정품과 동일한 파일링코드와 book-out 위치가 같은 제품이 8010존과 8020존 모두 존재할 경우
                                if(dINZN_YD_MTL_L > 13000) {
                                    //입고예정품 최대길이가 13000 보다 크면 8020존을 선택한다.
                                    szAIM_STL_NO = szSTL_NO_8020;
                                } else {
                                    //입고예정품 최대길이가 13000 보다 작거나 같을 경우 단이 낮은 존을 선택하고 단이 같을 경우 8020 존을 선택한다.

                                    if(Integer.parseInt(szYD_STK_LYR_NO_8010) < Integer.parseInt(szYD_STK_LYR_NO_8020)) {
                                        szAIM_STL_NO = szSTL_NO_8010;
                                    } else {
                                        szAIM_STL_NO = szSTL_NO_8020;
                                    }
                                }

                            } else if("".equals(szSTL_LOC_8010) && !"".equals(szSTL_LOC_8020)) {
                                //입고예정품과 동일한 파일링코드와 book-out 위치가 같은 제품이 8020존에만 존재할 경우
                                szAIM_STL_NO = szSTL_NO_8020;
                            } else {
                                //입고예정품과 동일한 파일링코드와 book-out 위치가 같은 제품이 8010존에만 존재할 경우
                                szAIM_STL_NO = szSTL_NO_8010;
                            }

                            szINFO_GP   = "4"; //파일링
                        }

                    }
                }

                //-----------------------------------------------------------------------------------------------------------------------------
                recEdit             = JDTORecordFactory.getInstance().create();
                setRecord           = JDTORecordFactory.getInstance().create();
                szPL_RCPT_TRK_NO    = sTrackingGbn + YdUtils.getCurDate("yyyyMMddHHmmss");

                for(int Loop_i = intMTL_SH; Loop_i > 0; Loop_i--) {

                    //YD저장품에 PL_RCPT_TRK_NO 설정
                    recEdit.setField("STL_NO",              ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO"+Loop_i));
                    recEdit.setField("PL_RCPT_TRK_NO",      szPL_RCPT_TRK_NO);
                    recEdit.setField("YD_BOOK_OUT_LOC",     szYD_BOOK_OUT_LOC_CD); //최종 BOOK-OUT 위치 설정 , Transfer 주소 일 수도 있다.
                    recEdit.setField("YD_RCPT_PLN_STR_LOC", szYD_RCPT_PLN_STR_LOC); //입고예정위치

                    intRtnVal = ydStockDao.updYdStock(recEdit,0);

                    intRtnVal = ydStockDao.update_Dm_Time(recEdit,2);

                    /*
                     * 입고존 도착시점에  PLATE공통 저장위치 강제 UPDATE
                     */
                    //-- NEW Version ---------------------------------------------------------
                    setRecord.setField("YD_GP",             YdConstant.YD_GP_PLATE2_GDS_YARD);
                    setRecord.setField("YD_BAY_GP",         "X");
                    setRecord.setField("YD_EQP_GP",         "RT");
                    setRecord.setField("YD_STK_COL_NO",     "R" + szRCPT_ZONE_GP);
                    setRecord.setField("YD_STK_BED_NO",     "01");
                    setRecord.setField("YD_STK_LYR_NO",     "00"+Loop_i);
                    setRecord.setField("FNL_REG_PGM",       "Y8YDL010");
                    setRecord.setField("MODIFIER",          "Y8YDL010");
                    setRecord.setField("YD_STR_LOC_HIS1",   "") ;
                    setRecord.setField("YD_STR_LOC_HIS2",   "");
                    setRecord.setField("PLATE_NO",          ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO" + Loop_i));
                    setRecord.setField("YD_STR_LOC",        YdConstant.YD_GP_PLATE2_GDS_YARD  + "XRTR" + szRCPT_ZONE_GP); //입고존 위치 : TXRTRA, TXRTRB, TXRTRC

                    /* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtPlatecommLOC */
                    intRtnVal = ydStockDao.updPtComm_LOC(setRecord, 1);

                    setRecord.setField("PLATE_NO",          ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO" + Loop_i));
                    setRecord.setField("YD_STR_LOC",        YdConstant.YD_GP_PLATE2_GDS_YARD  + "XRTR" + szRCPT_ZONE_GP); //입고존 위치 : TXRTRA, TXRTRB, TXRTRC
                    setRecord.setField("YD_BOOK_OUT_LOC",   szYD_BOOK_OUT_LOC_CD);

                    PtPlateCommDao      ptPlateCommDao      = new PtPlateCommDao();
                    intRtnVal = ptPlateCommDao.updPtPlateComm(setRecord, 1);
                }

                //------------------------------------------------------------------------------------------------
                if("A".equals(szRCPT_ZONE_GP)||"B".equals(szRCPT_ZONE_GP)||"C".equals(szRCPT_ZONE_GP))  {

                    JDTORecordSet rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");
                    JDTORecord recIn = JDTORecordFactory.getInstance().create();
                    JDTORecord recGetVal        = null;

                    //최하단 재료를 수신 전문에서 읽어온다.
                    recIn.setField("PLATE_NO", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO1"));

                    intRtnVal = ydStockDao.getYdStock(recIn, rsOutRecSet, 172);

                    if(intRtnVal > 0) {
                        rsOutRecSet.first();
                        recGetVal = rsOutRecSet.getRecord();

                        String sPlQaInspMtl = ydDaoUtils.paraRecChkNull(recGetVal,"PL_QA_INSP_MTL");
                        if("1".equals(sPlQaInspMtl)){
                            /*
                             * 품질검사재에 대해서는  무조건 68020 [TBRTRC20] 으로 Routing 지시를 내린다.
                             * 2014.04.09  윤재광
                             * 온라인(B)도 추가한다.[66020 [TBRTRB20] 으로 Routing 지시]
                             * 2015.12.21  윤재광
                             * 온라인(A)도 추가한다.[66020 [TBRTRB20] 으로 Routing 지시]
                             * 2015.12.21  윤재광
                             */
                            szYD_BOOK_OUT_LOC_CD = sTrackingGbn+"020";

                            /*
                             * A R/T B동입고시 T/F로 라우팅지시 함.
                             */
                            if( "67020".equals(szYD_BOOK_OUT_LOC_CD)) {
                                szYD_BOOK_OUT_LOC_CD = "67216";
                            } else if( "67010".equals(szYD_BOOK_OUT_LOC_CD)) {
                                szYD_BOOK_OUT_LOC_CD = "67206";
                            }
                        }

                        szMsg= "["+ szOperationName +"] sPlQaInspMtl  [" + sPlQaInspMtl + "]에 대한 L2 라우팅지시 [YDY8L006] 송신 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                    }
                }

                //--------------------------------------------------------------------------------------------------
                //라우팅지시 L2로 전송

                //최상단 재료를 수신 전문에서 읽어온다.
                szPL_MTL_NO = ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO" + intMTL_SH);

                arrRT_ZONE_NO       = YdCommonUtils.getY4PilingZoneNo2StrLoc(szYD_BOOK_OUT_LOC_CD);

                szMsg= "["+ szOperationName +"] 최상단제품  [" + szPL_MTL_NO + "]에 대한 L2 라우팅지시 [YDY8L006] 송신 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                recEdit2 = JDTORecordFactory.getInstance().create();
                recEdit2.setField("MSG_ID"          , "YDY8L006");  // ROUTING 정보
                recEdit2.setField("INFO_GP"         , szINFO_GP);   // 1:입고, 4:파일링
                recEdit2.setField("STL_NO"          , szPL_MTL_NO); // 최상단재료번호
                recEdit2.setField("CURR_LOC"        , YdConstant.YD_GP_PLATE2_GDS_YARD  + "XRTR" + szRCPT_ZONE_GP + "01");  //RT상의 현재위치 (FROM) : 여기선 입고존위치 (TXRTRA01, TXRTRB01, TXRTRC01)
                recEdit2.setField("AIM_LOC1"        , arrRT_ZONE_NO[0] + arrRT_ZONE_NO[1]);  //재료가 갈 최종 위치
                recEdit2.setField("AIM_STL_NO"      , szAIM_STL_NO);    //Trans bed 파일링 대상위치의 최상단 제품 번호
				
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// recEdit2 에 logId 추가
                recEdit2.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

                ydDelegate.sendMsg(recEdit2);

                szMsg= "["+ szOperationName +"] 최상단제품  [" + szPL_MTL_NO + "]에 대한 L2 라우팅지시 [YDY8L006] 송신 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);


            } else { //'L'
                //=========================================================================================
                //입고 존 출발 처리

                //-- Step1 Start ---------------------------------------------------------------------------------------------
                //-- Step1 end ---------------------------------------------------------------------------------------------


                recEdit             = JDTORecordFactory.getInstance().create();
                setRecord           = JDTORecordFactory.getInstance().create();
                szPL_RCPT_TRK_NO    = sTrackingGbn + YdUtils.getCurDate("yyyyMMddHHmmss");

                for(int Loop_i = intMTL_SH; Loop_i > 0; Loop_i--) {

                    //YD저장품에 PL_RCPT_TRK_NO 설정
                    recEdit.setField("STL_NO",          ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO"+Loop_i));
                    recEdit.setField("PL_RCPT_TRK_NO",  szPL_RCPT_TRK_NO);
                    //recEdit.setField("YD_BOOK_OUT_LOC", szYD_BOOK_OUT_LOC_CD); //최종 BOOK-OUT 위치 설정 , Transfer 주소 일 수도 있다.

                    intRtnVal = ydStockDao.updYdStock(recEdit,0);

                    /*
                     * 2016.09.02 윤재광 - CPL,ON-LINE 합류지점에서 올라오는 이벤트 시간관리 요청 - 양준석 과장
                     * 2후판에서는 YD_RCPT_LEV_DT(입고존 출발정보 수신일시 정보 관리) 이 항목으로 관리한다.
                     */
                    if("T".equals(szRCPT_ZONE_GP)||"A".equals(szRCPT_ZONE_GP)) {
                        intRtnVal = ydStockDao.update_Dm_Time(recEdit,6);
                    }
                }

                //------------------------------------------------------------------------------------------------
                if("A".equals(szRCPT_ZONE_GP)||"B".equals(szRCPT_ZONE_GP)||"C".equals(szRCPT_ZONE_GP))  {

                    JDTORecordSet rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");
                    JDTORecord recIn = JDTORecordFactory.getInstance().create();
                    JDTORecord recGetVal        = null;

                    //최하단 재료를 수신 전문에서 읽어온다.
                    recIn.setField("PLATE_NO", ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO1"));

                    intRtnVal = ydStockDao.getYdStock(recIn, rsOutRecSet, 172);

                    if(intRtnVal > 0) {
                        rsOutRecSet.first();
                        recGetVal = rsOutRecSet.getRecord();

                        String sPlQaInspMtl = ydDaoUtils.paraRecChkNull(recGetVal,"PL_QA_INSP_MTL");
                        if("1".equals(sPlQaInspMtl)){

                            if(("A".equals(szRCPT_ZONE_GP)
                                && !szYD_BOOK_OUT_LOC_CD.equals("67206")
                                && !szYD_BOOK_OUT_LOC_CD.equals("67216"))||
                               ("B".equals(szRCPT_ZONE_GP)
                                && !szYD_BOOK_OUT_LOC_CD.equals("66010")
                                && !szYD_BOOK_OUT_LOC_CD.equals("66020"))||
                               ("C".equals(szRCPT_ZONE_GP)
                                && !szYD_BOOK_OUT_LOC_CD.equals("68010")
                                && !szYD_BOOK_OUT_LOC_CD.equals("68020"))
                              )
                            {

                                recTemp = JDTORecordFactory.getInstance().create();

                                for(int Loop_i = intMTL_SH; Loop_i > 0; Loop_i--) {
                                    /*
                                     * Plate공통테이블에 BOOK-OUT위치의 야드저장위치로 업데이트 처리함으로써 입고대상재에서 제거를 시킴
                                     */
                                    recTemp.setField("PLATE_NO",            ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO"+Loop_i));
                                    recTemp.setField("YD_MTL_ITEM",         "PG");
                                    recTemp.setField("YD_STK_COL_GP",       "TBRTR"+szRCPT_ZONE_GP);
                                    recTemp.setField("YD_STK_BED_NO",       "20");
                                    recTemp.setField("YD_STK_LYR_NO",       "00" + (Loop_i));

                                    szRtnMsg = YdCommonUtils.setYdStrLocToPtComm(recTemp, szMethodName);
                                }

                            }

                            /*
                             * 품질검사재에 대해서는  R/T 가상버퍼에 등록 하지 않고 종료한다.
                             * 2014.04.09  윤재광
                             */

                            szMsg = "[후판제품창고L2] 입고존 재료정보 수신 --------------------- 정상 종료 --------------------------";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                            return;
                        }
                    }

                }

                //----------------------------------------------------------------------------------------------------------

                if(("B".equals(szRCPT_ZONE_GP)
                    && !szYD_BOOK_OUT_LOC_CD.equals("66010")
                    && !szYD_BOOK_OUT_LOC_CD.equals("66020"))||
                   ("C".equals(szRCPT_ZONE_GP)
                    && !szYD_BOOK_OUT_LOC_CD.equals("68010")
                    && !szYD_BOOK_OUT_LOC_CD.equals("68020"))
                  )
                {

                    recTemp = JDTORecordFactory.getInstance().create();

                    for(int Loop_i = intMTL_SH; Loop_i > 0; Loop_i--) {
                        /*
                         * Plate공통테이블에 BOOK-OUT위치의 야드저장위치로 업데이트 처리함으로써 입고대상재에서 제거를 시킴
                         */
                        recTemp.setField("PLATE_NO",            ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO"+Loop_i));
                        recTemp.setField("YD_MTL_ITEM",         "PG");
                        recTemp.setField("YD_STK_COL_GP",       "TBRTR"+szRCPT_ZONE_GP);
                        recTemp.setField("YD_STK_BED_NO",       "20");
                        recTemp.setField("YD_STK_LYR_NO",       "00" + (Loop_i));

                        szRtnMsg = YdCommonUtils.setYdStrLocToPtComm(recTemp, szMethodName);
                    }

                } else {

                    //---------------------------------------------------------------------------------------------
                    //입고 존에서 출발 할 때 R/T 가상 버퍼에 등록 한다.

                    //arrRT_ZONE_NO         = YdCommonUtils.getY4PilingZoneNo2StrLoc(szYD_BOOK_OUT_LOC_CD);
                    //56216 을  KBTF02-16 으로 해석하면 안되고 KBRTRA-40 으로 변환되어야 한다.
                    arrRT_ZONE_NO       = YdCommonUtils.getY4BookOutLoc(szYD_BOOK_OUT_LOC_CD);

                    //RT상의  BOOK-OUT 위치의 적치열구분
                    szYD_BOOK_OUT_COL_GP = arrRT_ZONE_NO[0];
                    //RT상의 BOOK-OUT 위치의 적치베드번호
                    szYD_BOOK_OUT_BED_NO = arrRT_ZONE_NO[1];


                    //최하단 재료번호로 RT 가상베드를 조회하여 존재하지 않음을 확인 할 것...
                    rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                    recInPara = JDTORecordFactory.getInstance().create();
                    recInPara.setField("STL_NO", szPL_MTL_NO);
                    recInPara.setField("YD_STK_LYR_MTL_STAT", "");
                    recInPara.setField("YD_GP", YdConstant.YD_GP_PLATE2_GDS_YARD + "_RT"); //RT와 RT가상버퍼에 이미 존재하는지 체크

                    intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0080");

                    if( intRtnVal > 0 ) {
                        //이미 RT 가상버퍼에 추가된 상태임으로 SKIP 처리 한다. (66000 에서 출발 처리하고, 66020 에서 다시 출발 처리되는 경우)
                        szMsg = "[후판제품창고L2] 입고존 재료정보 수신 : 최하단재료 YD저장품  RT, RT가상버퍼에서 검색됨!  STL_NO : "+szPL_MTL_NO;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                        return;
                    }

                    //BOOKOUT 위치로 입력가능한 가상베드이 적치열구분과, 적치Bed를 구한다.
                    recPara = JDTORecordFactory.getInstance().create();
                    outRecSet1 = JDTORecordFactory.getInstance().createRecordSet("");

                    recPara.setField("YD_STK_COL_GP", szYD_BOOK_OUT_COL_GP);
                    recPara.setField("YD_STK_BED_NO", szYD_BOOK_OUT_BED_NO);

                    /*com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0047*/
                    intRtnVal = commDao.select(recPara, outRecSet1, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0047");

                    if(intRtnVal < 1) {

                        //입력가능한 빈 가상베드를 찾지 못하면 에러 로그를 남긴다.
                        szMsg = "[후판제품창고L2] 입고존 재료정보 수신 : 입고존 출발시 빈 RT 가상베드 찾지 못함!!  YD_STK_COL_GP : "+szYD_BOOK_OUT_COL_GP + " ,YD_STK_BED_NO : " + szYD_BOOK_OUT_BED_NO;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    } else {

                        //입력가능한 빈 가상베드를 찾으면 그 가상베드에 입고존 출발 제품들을 적치시킨다.
                        outRecSet1.first();
                        recPara = outRecSet1.getRecord();
                        szTO_LOC_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");


                        if( !"".equals(szTO_LOC_BED_NO) ) {

                            //INSERT항목 레코드 생성
                            recPara = JDTORecordFactory.getInstance().create();
                            recTemp = JDTORecordFactory.getInstance().create();

                            //대기베드에 적치한다.
                            for(int Loop_i = intMTL_SH; Loop_i > 0; Loop_i--) {

                                //적치단 재료상태가 적치 가능이면 재료 등록
                                //적치단 테이블 업데이트
                                //적치열구분 = 설비ID
                                recPara.setField("YD_STK_COL_GP",       szYD_BOOK_OUT_COL_GP);
                                recPara.setField("YD_STK_BED_NO",       szTO_LOC_BED_NO);
                                recPara.setField("YD_STK_LYR_NO",       "00" + (Loop_i));
                                recPara.setField("MODIFIER",            "Y8YDL010");
                                recPara.setField("YD_STK_LYR_MTL_STAT", "C");
                                recPara.setField("STL_NO",              ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO"+Loop_i) );

                                //업데이트 실행
                                intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);

                                /*
                                 * Plate공통테이블에 BOOK-OUT위치의 야드저장위치로 업데이트 처리함으로써 입고대상재에서 제거를 시킴
                                 */
                                recTemp.setField("PLATE_NO",            ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO"+Loop_i));
                                recTemp.setField("YD_MTL_ITEM",         "PG");
                                recTemp.setField("YD_STK_COL_GP",       szYD_BOOK_OUT_COL_GP);
                                recTemp.setField("YD_STK_BED_NO",       szTO_LOC_BED_NO);
                                recTemp.setField("YD_STK_LYR_NO",       "00" + (Loop_i));

                                szRtnMsg = YdCommonUtils.setYdStrLocToPtComm(recTemp, szMethodName);

                            }
                        }
                    }

                }

                //--------------------------------------------------------------------------------------------------

            }

            szMsg = "[후판제품창고L2] 입고존 재료정보 수신 --------------------- 정상 종료 --------------------------";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

        } catch (Exception e) {

            szMsg = "[후판제품창고L2] 입고존 재료정보 수신 처리중 ERROR : " + e.getMessage();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

            throw new DAOException(getClass().getName() + e.getMessage(),e);

        }   // end try catch

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 START
szMsg = "Y8입고존 재료정보 처리(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

    } // end of procY8RcptZoneMtlInfo()

    /**
     * 오퍼레이션명 : 2후판제품창고 #2DS B동 크레인 파일링 지시_입고존도착시
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return 파일링지시여부(true:파일링지시 송신함,false:파일링지시 송신안함)
     * @throws DAOException
     */
    public boolean procY8RcptZoneMtlInfo_Sub01(JDTORecord tcRecord)throws DAOException  {
        boolean isTrue = false;

        YdDaoUtils  ydDaoUtils      = new YdDaoUtils();
        YdStockDao  ydStockDao      = new YdStockDao();
        YdPlateCommDAO commDao      = new YdPlateCommDAO();

        //DELEGATE
        YdDelegate  ydDelegate      = new YdDelegate();

        boolean blnRtnVal           = false;
        int intRtnVal               = 0;
        String szMsg                = "";
        String szMethodName         = "procY8RcptZoneMtlInfo_Sub01";
        String szOperationName      = "2후판제품창고 #2DS B동 크레인 파일링 지시_입고존도착시";

        //레코드 선언
        JDTORecord      recInPara   = null;
        JDTORecordSet   rsResult    = null;
        JDTORecord      recEdit     = null;
        JDTORecord      recEdit2    = null;
        JDTORecord      setRecord   = null;

        String[] arrRT_ZONE_NO      = null;
        String szPL_MTL_NO          = null;

        //야드북아웃위치(zone code)
        String szYD_BOOK_OUT_LOC_CD     = null;

        //재료매수
        int intMTL_SH                   = 0;

        //입고존 구분 - 2후판 R/T 구분 (A,B,C)
        String szRCPT_ZONE_GP           = null;
        String szPL_RCPT_TRK_NO         = null;
        String szYD_RCPT_PLN_STR_LOC    = null;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= tcRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(tcRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "2후판제품창고 #2DS B동 크레인 파일링 지시_입고존도착시(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

        try{

            szMsg = "[후판제품창고L2] 2후판제품창고 #2DS B동 크레인 파일링 지시_입고존도착시 --------------------- 처리 시작 --------------------------";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            szRCPT_ZONE_GP  = ydDaoUtils.paraRecChkNull(tcRecord,"RCPT_ZONE_GP");   //입고존 구분 - 2후판 R/T 구분 (A,B,C, T)
            szPL_MTL_NO     = ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO1");        //최하단 1단 재료번호

            //--------------------------------------------------------------------------------------------------
            //라우팅지시 L2로 전송
            String szRtnCd  = "S";
            /*
             * 1. 파일링 변경정보 체크 Procedure 호출
             */
            szMsg = "2후판 입고트래킹 파일링변경정보 처리   프로시져 호출";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            Object[] inParam = {
                                 szPL_MTL_NO
                                ,"BO"
                               };

            int[] inParamIndex = {1,2};

            //call SP_YD_PLATE_PILING_CRANE_PB(?,?,?)
            JDTORecord record = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.callSpQueryId_0007");

            if(record.size() > 0){
                /*
                 * S : Auto 파일링 SKIP
                 * 1 : 파일링 명령 (파일링하여 잡고 있어라)
                 * 0 : 해당 제품 위로 AP 가 가진 재료를 내려 놓아라
                 * 그외 : 에러 처리로 SKIP 처리
                 */
                szRtnCd = ydDaoUtils.paraRecChkNull(record, "OUT_RTN_CODE");

                szMsg = "파일링프로시져(SP_YD_PLATE_PILING_CRANE_PB) 호출 결과  :: [" + szRtnCd + "] ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            }

            if("S".equals(szRtnCd)){
                isTrue  = false;
            }else{
                /*
                intMTL_SH       = ydDaoUtils.paraRecChkNullInt(tcRecord,"MTL_SH");      //재료매수

                rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                blnRtnVal = this.chkStock(szPL_MTL_NO, rsResult);

                if(!blnRtnVal) {
                    szMsg = "[후판제품창고L2] 2후판제품창고 #2DS B동 크레인 파일링 지시_입고존도착시 : 최하단재료 YD저장품  검색 실패!! STL_NO : "+szPL_MTL_NO;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    return false;
                }

                rsResult.first();
                recInPara = rsResult.getRecord();

                //저장품의 Book-out 위치 코드를 YD저장품에서 읽어 온다. (여기서 읽은 BOOKOUT 위치는 압연지시수신시 1차적으로 만들어진 BOOK-OUT 위치이다.)
                szYD_BOOK_OUT_LOC_CD    = ydDaoUtils.paraRecChkNull(recInPara, "YD_BOOK_OUT_LOC");  //저장품의 야드BookOut위치(ex:66065)
                //입고예정위치
                szYD_RCPT_PLN_STR_LOC   = ydDaoUtils.paraRecChkNull(recInPara, "YD_RCPT_PLN_STR_LOC");

                recEdit             = JDTORecordFactory.getInstance().create();
                setRecord           = JDTORecordFactory.getInstance().create();
                szPL_RCPT_TRK_NO    = "67" + YdUtils.getCurDate("yyyyMMddHHmmss");

                for(int Loop_i = intMTL_SH; Loop_i > 0; Loop_i--) {

                    //YD저장품에 PL_RCPT_TRK_NO 설정
                    recEdit.setField("STL_NO",              ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO"+Loop_i));
                    recEdit.setField("PL_RCPT_TRK_NO",      szPL_RCPT_TRK_NO);
                    recEdit.setField("YD_BOOK_OUT_LOC",     szYD_BOOK_OUT_LOC_CD); //최종 BOOK-OUT 위치 설정 , Transfer 주소 일 수도 있다.
                    recEdit.setField("YD_RCPT_PLN_STR_LOC", szYD_RCPT_PLN_STR_LOC); //입고예정위치

                    intRtnVal = ydStockDao.updYdStock(recEdit,0);

                    intRtnVal = ydStockDao.update_Dm_Time(recEdit,2);

                    // 입고존 도착시점에  PLATE공통 저장위치 강제 UPDATE
                    setRecord.setField("YD_GP",             YdConstant.YD_GP_PLATE2_GDS_YARD);
                    setRecord.setField("YD_BAY_GP",         "X");
                    setRecord.setField("YD_EQP_GP",         "RT");
                    setRecord.setField("YD_STK_COL_NO",     "R" + szRCPT_ZONE_GP);
                    setRecord.setField("YD_STK_BED_NO",     "01");
                    setRecord.setField("YD_STK_LYR_NO",     "00"+Loop_i);
                    setRecord.setField("FNL_REG_PGM",       "Y8YDL010");
                    setRecord.setField("MODIFIER",          "Y8YDL010");
                    setRecord.setField("YD_STR_LOC_HIS1",   "") ;
                    setRecord.setField("YD_STR_LOC_HIS2",   "");
                    setRecord.setField("PLATE_NO",          ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO" + Loop_i));
                    setRecord.setField("YD_STR_LOC",        YdConstant.YD_GP_PLATE2_GDS_YARD  + "XRTR" + szRCPT_ZONE_GP); //입고존 위치 : TXRTRA, TXRTRB, TXRTRC

                    //com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtPlatecommLOC
                    intRtnVal = ydStockDao.updPtComm_LOC(setRecord, 1);

                    setRecord.setField("PLATE_NO",          ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO" + Loop_i));
                    setRecord.setField("YD_STR_LOC",        YdConstant.YD_GP_PLATE2_GDS_YARD  + "XRTR" + szRCPT_ZONE_GP); //입고존 위치 : TXRTRA, TXRTRB, TXRTRC
                    setRecord.setField("YD_BOOK_OUT_LOC",   szYD_BOOK_OUT_LOC_CD);

                    PtPlateCommDao      ptPlateCommDao      = new PtPlateCommDao();
                    intRtnVal = ptPlateCommDao.updPtPlateComm(setRecord, 1);
                }
                */
                arrRT_ZONE_NO   = YdCommonUtils.getY4PilingZoneNo2StrLoc("67015");

                szMsg= "["+ szOperationName +"] 최상단제품  [" + szPL_MTL_NO + "]에 대한 L2 라우팅지시 [YDY8L006] 송신 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                recEdit2 = JDTORecordFactory.getInstance().create();
                recEdit2.setField("MSG_ID"          , "YDY8L006");  // ROUTING 정보
                recEdit2.setField("INFO_GP"         , "1");         // 1:입고, 4:파일링
                recEdit2.setField("STL_NO"          , szPL_MTL_NO); // 최상단재료번호
                recEdit2.setField("CURR_LOC"        , YdConstant.YD_GP_PLATE2_GDS_YARD  + "XRTR" + szRCPT_ZONE_GP + "01");  //RT상의 현재위치 (FROM) : 여기선 입고존위치 (TXRTRA01, TXRTRB01, TXRTRC01)
                recEdit2.setField("AIM_LOC1"        , arrRT_ZONE_NO[0] + arrRT_ZONE_NO[1]);  //재료가 갈 최종 위치
                recEdit2.setField("AIM_STL_NO"      , "");          //Trans bed 파일링 대상위치의 최상단 제품 번호
				
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// recEdit2 에 logId 추가
                recEdit2.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

                ydDelegate.sendMsg(recEdit2);

                szMsg= "["+ szOperationName +"] 최상단제품  [" + szPL_MTL_NO + "]에 대한 L2 라우팅지시 [YDY8L006] 송신 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                isTrue  = true;
            }
        } catch (Exception e) {

            szMsg = "[후판제품창고L2] 2후판제품창고 #2DS B동 크레인 파일링 지시_입고존도착시  처리중 ERROR : " + e.getMessage();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

            throw new DAOException(getClass().getName() + e.getMessage(),e);

        }   // end try catch

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 END
szMsg = "2후판제품창고 #2DS B동 크레인 파일링 지시_입고존도착시(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

        return isTrue;
    }

    /**
     * 오퍼레이션명 : 2후판제품창고 #2DS B동 크레인 파일링 지시_입고존출발시
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return 파일링대상여부(true:파일링지시 대상,false:파일링지시 미대상)
     * @throws DAOException
     */
    public boolean procY8RcptZoneMtlInfo_Sub02(JDTORecord tcRecord)throws DAOException  {

        boolean isTrue = false;

        YdDaoUtils  ydDaoUtils      = new YdDaoUtils();
        YdStockDao  ydStockDao      = new YdStockDao();
        YdStkLyrDao ydStkLyrDao     = new YdStkLyrDao();

        int intRtnVal               = 0;
        String szMsg                = "";
        String szMethodName         = "procY8RcptZoneMtlInfo_Sub02";
        String szOperationName      = "2후판제품창고 #2DS B동 크레인 파일링 지시_입고존출발시";

        //레코드 선언
        JDTORecord      recInPara   = null;
        JDTORecordSet   rsResult    = null;
        JDTORecord      recEdit     = null;
        JDTORecord      recGetVal   = null;
        //재료매수
        int intMTL_SH               = 0;

        String szPL_RCPT_TRK_NO     = "";
        String szSTL_NO             = "";
        String szPL_MTL_NO          = "";


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 START
// 기존 putLog -> putLogNew logId 출력 되게 개선
//String logId                    		= tcRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(tcRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "후판제품창고 #2DS B동 크레인 파일링 지시_입고존출발시(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
//2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

        try{

            szMsg = "[후판제품창고L2] 2후판제품창고 #2DS B동 크레인 파일링 지시_입고존출발시 --------------------- 처리 시작 --------------------------";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            rsResult    = JDTORecordFactory.getInstance().createRecordSet("");
            recInPara   = JDTORecordFactory.getInstance().create();

            szPL_MTL_NO = ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO1"); //최하단 1단 재료번호

            this.chkStock(szPL_MTL_NO, rsResult);

            rsResult.first();
            recInPara = rsResult.getRecord();

            String sPilingYn = ydDaoUtils.paraRecChkNull(recInPara, "COIL_CAR_LOTID_YN");
            /*
             * P : PILING지시
             * R : RELEASE짓
             * 그외에는 기존 로직 수행
             */
            if("P".equals(sPilingYn)||"R".equals(sPilingYn)){
                isTrue  = true;
            }else{
                isTrue  = false;
            }

            szMsg = "[후판제품창고L2] 2후판제품창고 #2DS B동 크레인 파일링 지시_입고존출발시 : szSTL_NO     : "+szPL_MTL_NO;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[후판제품창고L2] 2후판제품창고 #2DS B동 크레인 파일링 지시_입고존출발시 : sPilingYn : "+sPilingYn;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[후판제품창고L2] 2후판제품창고 #2DS B동 크레인 파일링 지시_입고존출발시 : isTrue   : "+isTrue;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            //레코드 선언
            /*
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            recPara  = JDTORecordFactory.getInstance().create();
            recPara.setField("YD_STK_COL_GP", "TBAP02");
            recPara.setField("YD_STK_BED_NO", "BF");
            recPara.setField("YD_STK_LYR_NO", "001");

            intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 0);

            if(intRtnVal > 0){
                rsResult.first();
                recGetVal   = rsResult.getRecord();
                szSTL_NO    = ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO");
            }

            szPL_MTL_NO     = ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO1"); //최하단 1단 재료번호

            szMsg = "[후판제품창고L2] 2후판제품창고 #2DS B동 크레인 파일링 지시_입고존출발시 : szPL_MTL_NO  : "+szPL_MTL_NO;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[후판제품창고L2] 2후판제품창고 #2DS B동 크레인 파일링 지시_입고존출발시 : szSTL_NO         : "+szSTL_NO;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            if(szPL_MTL_NO.equals(szSTL_NO)){

                intMTL_SH   = ydDaoUtils.paraRecChkNullInt(tcRecord,"MTL_SH");  //재료매수

                //입고 존 출발 처리
                recEdit             = JDTORecordFactory.getInstance().create();
                szPL_RCPT_TRK_NO    = "67" + YdUtils.getCurDate("yyyyMMddHHmmss");

                for(int Loop_i = intMTL_SH; Loop_i > 0; Loop_i--) {

                    //YD저장품에 PL_RCPT_TRK_NO 설정
                    recEdit.setField("STL_NO",          ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO"+Loop_i));
                    recEdit.setField("PL_RCPT_TRK_NO",  szPL_RCPT_TRK_NO);

                    intRtnVal = ydStockDao.updYdStock(recEdit,0);
                }

                isTrue  = true;
            }else{
                isTrue  = false;
            }
            */

            szMsg = "[후판제품창고L2] 2후판제품창고 #2DS B동 크레인 파일링 지시_입고존출발시 --------------------- 정상 종료 --------------------------";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

        } catch (Exception e) {

            szMsg = "[후판제품창고L2] 2후판제품창고 #2DS B동 크레인 파일링 지시_입고존출발시 처리중 ERROR : " + e.getMessage();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

            throw new DAOException(getClass().getName() + e.getMessage(),e);

        }   // end try catch

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 END
szMsg = "후판제품창고 #2DS B동 크레인 파일링 지시_입고존출발시(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////
        
        return isTrue;
    }


/*  public void procY8TfTrckInfo(JDTORecord tcRecord)throws JDTOException  {

        // Y8YDL011 Trans Bed 트래킹정보
        // STR_LOC1         저장위치1       VARCHAR2(8)     TB 위치1 (TBTF0705)
        // STL_NO1          재료번호1       VARCHAR2(11)    TB 위치1의 최상단 재료번호
        // MTL_SH1          재료매수1       VARCHAR2(2)     TB 위치1의 적치매수
        // STR_LOC2         저장위치2       VARCHAR2(8)     TB 위치2 (TBTF0715)
        // STL_NO2          재료번호2       VARCHAR2(11)    TB 위치2의 최상단 재료번호
        // MTL_SH2          재료매수2       VARCHAR2(2)     TB 위치2의 적치매수
        //  :
        // STR_LOC10        저장위치10  VARCHAR2(8)     TB 위치2 (TBTF0319)
        // STL_NO10         재료번호10  VARCHAR2(11)    TB 위치2의 최상단 재료번호
        // MTL_SH10         재료매수10  VARCHAR2(2)     TB 위치2의 적치매수

        String szMsg                = "";
        String szMethodName         = "procY8TfTrckInfo";
        String szOperationName      = "2후판제품창고L2 TB Tracking정보 수신";
        YdPlateCommDAO commDao = new YdPlateCommDAO();

        JDTORecordSet   rsResult    = null;
        JDTORecordSet   rsResult2   = null;
        JDTORecordSet   outRecSet1  = null;
        JDTORecord      recInPara   = null;
        JDTORecord      recOutPara  = null;
        JDTORecord      recPara     = null;

        int intRtnVal               = 0;
        boolean blnRtnVal           = false;

        String  szL3_STR_LOC        = null;
        String  szL3_STL_NO         = null;
        int     iL3_MTL_SH          = 0;

        String  szL2_STR_LOC        = null;
        String  szL2_STL_NO         = null;
        int     iL2_MTL_SH          = 0;

        String  szFROM_YD_STK_COL_GP = null;
        String  szFROM_YD_STK_BED_NO = null;

        String  szTO_YD_STK_COL_GP  = null;
        String  szTO_YD_STK_BED_NO  = null;

        String szYD_BOOK_OUT_LOC_CD = null;
        String[] arrRT_ZONE_NO      = null;

        //RT 가상버퍼 적치베드(TO위치)
        String szTO_LOC_BED_NO      = null;


        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(tcRecord);

        //에러 리턴
        if(szRcvTcCode == null || "".equals(szRcvTcCode) ) {
            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }

        //TC CODE DISPLAY
        if(bDebugFlag) {
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        try{

            szMsg = "[후판제품창고L2] TB Tracking정보 수신 --------------------- 처리 시작 --------------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //야드 TransBed에 있는 현재 적치 정보를 읽어온다.
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            recInPara = JDTORecordFactory.getInstance().create();
            recInPara.setField("YD_GP", YdConstant.YD_GP_PLATE2_GDS_YARD);

            intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0071");

            if(intRtnVal != 10 ) {
                szMsg = "[후판제품창고L2] TB Tracking정보 수신 : Error!! Trans Bed 적치상태 조회 실패  intRtnVal : " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                return;
            }

            rsResult.first();
            recOutPara = JDTORecordFactory.getInstance().create();
            recPara = JDTORecordFactory.getInstance().create();

            //TBTF0319, TBTF0309, TBTF0418, TBTF0408 ... TBTF0715, TBTF0705 순으로 처리
            for(int Loop_i = 10 ; Loop_i > 1; Loop_i--) {

                //L3 정보 변수에 담기 (DB에서 읽은 값)
                recOutPara = rsResult.getRecord();
                szL3_STR_LOC        = ydDaoUtils.paraRecChkNull(recOutPara,"STR_LOC");
                szL3_STL_NO         = ydDaoUtils.paraRecChkNull(recOutPara,"STL_NO");
                iL3_MTL_SH          = ydDaoUtils.paraRecChkNullInt(recOutPara,"SH_CNT");

                //L2 정보 변수에 담기 (전문으로 받은 값)
                szL2_STR_LOC        = ydDaoUtils.paraRecChkNull(tcRecord,"STR_LOC" + Loop_i);
                szL2_STL_NO         = ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO" + Loop_i);
                iL2_MTL_SH          = ydDaoUtils.paraRecChkNullInt(tcRecord,"MTL_SH" + Loop_i);

                //위치 확인
                if(!szL3_STR_LOC.equals(szL2_STR_LOC)) {
                    szMsg = "[후판제품창고L2] TB Tracking정보 수신 : Error!! 수신전문의  " + Loop_i + " 번째 위치가  " + szL3_STR_LOC + " 가 아니라  " + szL2_STR_LOC + " 로 수신 되었습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    return;
                }

                //최상단 재료번호 비교
                if(!szL3_STR_LOC.equals(szL2_STL_NO)) {

                    if(Loop_i == 10 || Loop_i == 9) { // TBTF03-19 , TBTF03-09

                        //Shift 할 TO 위치가 B-RT 임으로 SHIFT 처리는 하진 않고 입고존 출발과 같은 처리를 여기서 한다.
                        //단 Routing 지시는 이미 L2 로 전송 했기 때문에 여기서는 Routing 지시를 주지 않는다.

                        //--------------------------------------------------------------------------------------------------
                        //L3 재료로 YD저장품을 조회한다.
                        rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
                        blnRtnVal = this.chkStock(szL3_STL_NO, rsResult2);

                        if(!blnRtnVal) {
                            //저장품에 존재하지 않으면 여기서 종료
                            szMsg = "[후판제품창고L2] TB Tracking정보 수신 : L3 최상단재료 YD저장품  검색 실패!! STL_NO : "+szL3_STL_NO;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            return;
                        }

                        rsResult2.first();
                        recInPara = rsResult2.getRecord();

                        //저장품의 Book-out 위치 코드를 YD저장품에서 읽어 온다. (여기서 읽은 BOOKOUT 위치는 입고존 출발시 Routing 지시를 주기 위해서 보정된  BOOK-OUT 위치이다.)
                        szYD_BOOK_OUT_LOC_CD    = ydDaoUtils.paraRecChkNull(recInPara, "YD_BOOK_OUT_LOC");  //저장품의 야드BookOut위치(ex:66065)

                        arrRT_ZONE_NO       = YdCommonUtils.getY4BookOutLoc(szYD_BOOK_OUT_LOC_CD);

                        //BOOKOUT 위치로 입력가능한 가상베드이 적치열구분과, 적치Bed를 구한다.
                        outRecSet1 = JDTORecordFactory.getInstance().createRecordSet("");

                        recPara.setField("YD_STK_COL_GP", arrRT_ZONE_NO[0]); //RT상의  BOOK-OUT 위치의 적치열구분
                        recPara.setField("YD_STK_BED_NO", arrRT_ZONE_NO[1]); //RT상의 BOOK-OUT 위치의 적치베드번호

                        com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0047
                        intRtnVal = commDao.select(recPara, outRecSet1, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0047");

                        if(intRtnVal < 1) {

                            //입력가능한 빈 가상베드를 찾지 못하면 에러 로그를 남긴다.
                            szMsg = "[후판제품창고L2] TB Tracking정보 수신  : 입고존 출발시 빈 RT 가상베드 찾지 못함!!  YD_STK_COL_GP : "+arrRT_ZONE_NO[0] + " ,YD_STK_BED_NO : " + arrRT_ZONE_NO[1];
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                        } else {

                            //입력가능한 빈 가상베드를 찾으면 그 가상베드에 입고존 출발 제품들을 적치시킨다.
                            outRecSet1.first();
                            recPara = outRecSet1.getRecord();
                            szTO_LOC_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");

                            szFROM_YD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recOutPara,"YD_STK_COL_GP");
                            szFROM_YD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recOutPara,"YD_STK_BED_NO");

                            szTO_YD_STK_COL_GP  = ydDaoUtils.paraRecChkNull(recPara,"YD_STK_COL_GP");
                            szTO_YD_STK_BED_NO  = ydDaoUtils.paraRecChkNull(recPara,"YD_STK_BED_NO");

                            szMsg = "[후판제품창고L2] TB Tracking정보 수신 :  " + szFROM_YD_STK_COL_GP + szFROM_YD_STK_BED_NO  + " 위치의 내용을   RT가상버퍼" + szTO_YD_STK_COL_GP + szTO_YD_STK_BED_NO + " 로 이동시킵니다.";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                            recPara.setField("FROM_STK_COL_GP", szFROM_YD_STK_COL_GP);  //적치열구분(FROM)
                            recPara.setField("FROM_BED_NO",     szFROM_YD_STK_BED_NO);  //적치Bed번호(FROM)
                            recPara.setField("TO_STK_COL_GP",   szTO_YD_STK_COL_GP);    //적치열구분(TO)
                            recPara.setField("TO_BED_NO",       szTO_YD_STK_BED_NO);    //적치Bed번호(TO)

                            recPara.setField("PL_RCPT_TRK_NO",  "66" + YdUtils.getCurDate("yyyyMMddHHmmss")); //B-RT 상으로 입고 됨으로 "66"
                            recPara.setField("MODIFIER",        "Y8YDL011");
                            recPara.setField("YD_STK_COL_GP",   szFROM_YD_STK_COL_GP);
                            recPara.setField("YD_STK_BED_NO",   szFROM_YD_STK_BED_NO);

                            //YD저장품에 PL_RCPT_TRK_NO 설정
                            intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0015");

                            //SHIFT 처리
                            intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0006");

                            //FROM 위치 Clear
                            intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0007");
                        }


                    } else {

                        szFROM_YD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recOutPara,"YD_STK_COL_GP");
                        szFROM_YD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recOutPara,"YD_STK_BED_NO");

                        szTO_YD_STK_COL_GP  = ydDaoUtils.paraRecChkNull(recOutPara,"TO_YD_STK_COL_GP");
                        szTO_YD_STK_BED_NO  = ydDaoUtils.paraRecChkNull(recOutPara,"TO_YD_STK_BED_NO");

                        szMsg = "[후판제품창고L2] TB Tracking정보 수신 :  " + szFROM_YD_STK_COL_GP + szFROM_YD_STK_BED_NO  + " 위치의 내용을   " + szTO_YD_STK_COL_GP + szTO_YD_STK_BED_NO + " 로 SHIFT 처리합니다.";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


                        recPara.setField("FROM_STK_COL_GP", szFROM_YD_STK_COL_GP);  //적치열구분(FROM)
                        recPara.setField("FROM_BED_NO",     szFROM_YD_STK_BED_NO);  //적치Bed번호(FROM)
                        recPara.setField("TO_STK_COL_GP",   szTO_YD_STK_COL_GP);    //적치열구분(TO)
                        recPara.setField("TO_BED_NO",       szTO_YD_STK_BED_NO);    //적치Bed번호(TO)

                        //SHIFT 처리
                        intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0006");

                        //FROM 위치 Clear
                        intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0007");

                        if(Loop_i == 2 || Loop_i == 1) {
                            //C-RT 에서 Trans Bed 로 들어 오는 부분으로 SHIFT 처리와 함께  TBTF07-15 , TBTF07-05 에 새로운 정보를 등록 한다.

                            recPara.setField("STL_NO",          szL2_STL_NO);           //L2 제품번호
                            recPara.setField("YD_STK_COL_GP",   szFROM_YD_STK_COL_GP);  //적치열구분
                            recPara.setField("YD_STK_BED_NO",   szFROM_YD_STK_BED_NO);  //적치BED번호
                            recPara.setField("YD_STK_LYR_NO",   "001");                 //적치단

                            intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0014");
                        }
                    }
                }


                rsResult.next();
            }


            szMsg = "[후판제품창고L2] TB Tracking정보 수신 --------------------- 처리 종료 --------------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        } catch (Exception e) {

            szMsg = "[후판제품창고L2] TB Tracking정보수신 처리중 ERROR : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new JDTOException(getClass().getName() + e.getMessage(), e);

        }   // end try catch

    } // end of procY8TfTrckInfo()
*/
    /**
     * 오퍼레이션명 : 2후판제품창고L2 Trans Bed 트래킹정보 수신 (2후판 전용:Y8YDL011)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws DAOException
     */
    public void procY8TfTrckInfo(JDTORecord tcRecord)throws DAOException  {

        // Y8YDL011 Trans Bed 트래킹정보
        // STR_LOC1         저장위치1               VARCHAR2(8)     TB 위치1 (TBTF0705)
        // MTL_SH1          재료매수1               VARCHAR2(2)     TB 위치1의 적치매수
        // STL_NO1_001      위치1의 1단 재료번호    VARCHAR2(11)    TB 위치1의 1단 재료번호
        // STL_NO1_002      위치1의 2단 재료번호    VARCHAR2(11)    TB 위치1의 2단 재료번호
        // STL_NO1_003      위치1의 3단 재료번호    VARCHAR2(11)    TB 위치1의 3단 재료번호
        // STL_NO1_004      위치1의 4단 재료번호    VARCHAR2(11)    TB 위치1의 4단 재료번호
        // STL_NO1_005      위치1의 5단 재료번호    VARCHAR2(11)    TB 위치1의 5단 재료번호
        // STL_NO1_006      위치1의 6단 재료번호    VARCHAR2(11)    TB 위치1의 6단 재료번호
        // STR_LOC2         저장위치2               VARCHAR2(8)     TB 위치2 (TBTF0715)
        // MTL_SH2          재료매수2               VARCHAR2(2)     TB 위치2의 적치매수
        // STL_NO2_001      위치1의 1단 재료번호    VARCHAR2(11)    TB 위치2의 1단 재료번호
        // STL_NO2_002      위치1의 2단 재료번호    VARCHAR2(11)    TB 위치2의 2단 재료번호
        // STL_NO2_003      위치1의 3단 재료번호    VARCHAR2(11)    TB 위치2의 3단 재료번호
        // STL_NO2_004      위치1의 4단 재료번호    VARCHAR2(11)    TB 위치2의 4단 재료번호
        // STL_NO2_005      위치1의 5단 재료번호    VARCHAR2(11)    TB 위치2의 5단 재료번호
        // STL_NO2_006      위치1의 6단 재료번호    VARCHAR2(11)    TB 위치2의 6단 재료번호
        //  :
        // STR_LOC10        저장위치10          VARCHAR2(8)     TB 위치10 (TBTF0319)
        // MTL_SH10         재료매수10          VARCHAR2(2)     TB 위치10의 적치매수
        // STL_NO10_001     위치10의 1단 재료번호   VARCHAR2(11)    TB 위치10의 1단 재료번호
        // STL_NO10_002     위치10의 2단 재료번호   VARCHAR2(11)    TB 위치10의 2단 재료번호
        // STL_NO10_003     위치10의 3단 재료번호   VARCHAR2(11)    TB 위치10의 3단 재료번호
        // STL_NO10_004     위치10의 4단 재료번호   VARCHAR2(11)    TB 위치10의 4단 재료번호
        // STL_NO10_005     위치10의 5단 재료번호   VARCHAR2(11)    TB 위치10의 5단 재료번호
        // STL_NO10_006     위치10의 6단 재료번호   VARCHAR2(11)    TB 위치10의 6단 재료번호

        String szMsg                = "";
        String szMethodName         = "procY8TfTrckInfo";
        String szOperationName      = "2후판제품창고L2 TB Tracking정보 수신";
        YdPlateCommDAO commDao = new YdPlateCommDAO();


        JDTORecord      recInPara   = null;


        int intRtnVal               = 0;

        String  szL2_STR_LOC        = null;
        int     iL2_MTL_SH          = 0;
        String  szL2_STL_NO_001     = null;
        String  szL2_STL_NO_002     = null;
        String  szL2_STL_NO_003     = null;
        String  szL2_STL_NO_004     = null;
        String  szL2_STL_NO_005     = null;
        String  szL2_STL_NO_006     = null;

        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(tcRecord);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= tcRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(tcRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8TF트래킹정보 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        //에러 리턴
        if(szRcvTcCode == null || "".equals(szRcvTcCode) ) {
            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return;
        }

        //TC CODE DISPLAY
        if(bDebugFlag) {
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
        }

        try{

            szMsg = "[후판제품창고L2] TB Tracking정보 수신 --------------------- 처리 시작 --------------------------";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            recInPara = JDTORecordFactory.getInstance().create();

            for(int Loop_i = 1; Loop_i <= 10; Loop_i++) {

                //L2 정보 변수에 담기 (전문으로 받은 값)
                szL2_STR_LOC        = ydDaoUtils.paraRecChkNull(tcRecord,"STR_LOC" + Loop_i);
                iL2_MTL_SH          = ydDaoUtils.paraRecChkNullInt(tcRecord,"MTL_SH" + Loop_i);
                szL2_STL_NO_001     = ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO" + Loop_i + "_001");
                szL2_STL_NO_002     = ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO" + Loop_i + "_002");
                szL2_STL_NO_003     = ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO" + Loop_i + "_003");
                szL2_STL_NO_004     = ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO" + Loop_i + "_004");
                szL2_STL_NO_005     = ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO" + Loop_i + "_005");
                szL2_STL_NO_006     = ydDaoUtils.paraRecChkNull(tcRecord,"STL_NO" + Loop_i + "_006");

                recInPara.setField("STL_NO_001"     , szL2_STL_NO_001);
                recInPara.setField("STL_NO_002"     , szL2_STL_NO_002);
                recInPara.setField("STL_NO_003"     , szL2_STL_NO_003);
                recInPara.setField("STL_NO_004"     , szL2_STL_NO_004);
                recInPara.setField("STL_NO_005"     , szL2_STL_NO_005);
                recInPara.setField("STL_NO_006"     , szL2_STL_NO_006);
                recInPara.setField("YD_STK_COL_GP"  , szL2_STR_LOC.substring(0,6));
                recInPara.setField("YD_STK_BED_NO"  , szL2_STR_LOC.substring(6,8));

                //YD적치단 UPDATE
                intRtnVal = commDao.update(recInPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0020");
            }

            szMsg = "[후판제품창고L2] TB Tracking정보 수신 --------------------- 처리 종료 --------------------------";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);


        } catch (Exception e) {

            szMsg = "[후판제품창고L2] TB Tracking정보수신 처리중 ERROR : " + e.getMessage();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

            throw new DAOException(getClass().getName() + e.getMessage(), e);

        }   // end try catch

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  END
szMsg = "Y8TF트래킹정보 처리(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

    } // end of procY8TfTrckInfo()

    /**
     * 오퍼레이션명 :  연주/후판 슬라브 이상재 등록/해제 -공정관리 호출 (YDYDJ298)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procAbmtlOccurSend(JDTORecord msgRecord)throws DAOException  {

        String szMethodName         = "procAbmtlOccurSend";
        String szMsg                = "";

        YmEtcDao    ymEtcDao            = new YmEtcDao();

        JDTORecord recPara     = null;

        int intRtnVal = 0;
        try{
            String sPROCESS_GP = ydDaoUtils.paraRecChkNull(msgRecord,"PROCESS_GP");

            if("1".equals(sPROCESS_GP)){ // 이상재 등록
                YdEqpDao        ydEqpDao        = new YdEqpDao();

                //공정관리 프로지셔 기동
                JDTORecord record = ydEqpDao.ProcedureAbmtlPmCall(msgRecord);

                szMsg = "[" + szMethodName + "] 연주/후판 슬라브 이상재 등록 -공정관리  프로시져 기동 완료";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                recPara     = JDTORecordFactory.getInstance().create();
                recPara.setField("MATCH_HOLD_GP",       "Y");
                recPara.setField("MATCH_HOLD_RSN_CD",   "YD");
                recPara.setField("SLAB_NO",             ydDaoUtils.paraRecChkNull(msgRecord,"SLAB_NO"));

                /* com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updTB_PT_SLABCOMM_MATCH_HOLD_GP */
                intRtnVal = ymEtcDao.uptYmEtcDao(recPara, 13);

            }else{
                szMsg = "[" + szMethodName + "] 연주/후판 슬라브 이상재 해제 -공정관리  프로시져 기동 안함";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                recPara     = JDTORecordFactory.getInstance().create();
                recPara.setField("MATCH_HOLD_GP",       "N");
                recPara.setField("MATCH_HOLD_RSN_CD",   "YM");
                recPara.setField("SLAB_NO",             ydDaoUtils.paraRecChkNull(msgRecord,"SLAB_NO"));

                /* com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updTB_PT_SLABCOMM_MATCH_HOLD_GP */
                intRtnVal = ymEtcDao.uptYmEtcDao(recPara, 13);
            }
        } catch (Exception e) {

            szMsg = "연주/후판 슬라브 이상재 등록/해제 -공정관리 호출  처리중 ERROR : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(getClass().getName() + e.getMessage(), e);

        }   // end try catch
    }


    /**
     * 오퍼레이션명 : 후판제품창고 오버롤 체크 (YDYDJ297)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procPlateOverRollCheck(JDTORecord msgRecord)throws DAOException  {

        //DELEGATE
        YdDelegate ydDelegate = new YdDelegate();

        JDTORecord    recPara       = null;
        JDTORecord recInPara        = null;
        EJBConnector ejbConn        = null;
        String szMethodName         = "procPlateOverRollCheck";
        String szMsg                = "";

        String szSTL_NO         = "";
        String szORD_NO         = "";
        String szORD_DTL        = "";
        String szPLNT_PROC_CD   = "";
        String szPL_MEA_GDS_WT  = "";
        String szRECORD_END_GP  = "";
        String szBEFO_PROG_CD   = "";
        String szBEF_ORD_NO     = "";
        String szBEF_ORD_DTL    = "";

        try{
            JDTORecordSet rsRtn = JDTORecordFactory.getInstance().createRecordSet("");

            ejbConn = new EJBConnector("default", "RcptWrkDmdSeEJB", this);
            rsRtn =(JDTORecordSet)ejbConn.trx("procPlateOverRollCheck_Sub", new Class[] { JDTORecord.class}, new Object[] { msgRecord });

            //재료번호
            for (int Loop_i = 1; Loop_i <= rsRtn.size(); Loop_i++) {
                rsRtn.first();
                recInPara = rsRtn.getRecord();

                szSTL_NO        = ydDaoUtils.paraRecChkNull(recInPara, "PLATE_NO");
                szORD_NO        = ydDaoUtils.paraRecChkNull(recInPara, "ORD_NO");
                szORD_DTL       = ydDaoUtils.paraRecChkNull(recInPara, "ORD_DTL");
                szPLNT_PROC_CD  = ydDaoUtils.paraRecChkNull(recInPara, "PLNT_PROC_CD");
                szPL_MEA_GDS_WT = ydDaoUtils.paraRecChkNull(recInPara, "PL_MEA_GDS_WT");
                szRECORD_END_GP = ydDaoUtils.paraRecChkNull(recInPara, "RECORD_END_GP");
                szBEFO_PROG_CD  = ydDaoUtils.paraRecChkNull(recInPara, "BEFO_PROG_CD");
                szBEF_ORD_NO    = ydDaoUtils.paraRecChkNull(recInPara, "BEF_ORD_NO");
                szBEF_ORD_DTL   = ydDaoUtils.paraRecChkNull(recInPara, "BEF_ORD_DTL");

                /*
                 * 2. 진행관리 송신
                 */
                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("JMS_TC_CD",           "YDPTJ005");
                recPara.setField("JMS_TC_CODE",         "YDPTJ005");
                recPara.setField("JMS_TC_CREATE_DDTT",  YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss"));
                recPara.setField("STL_NO",              szSTL_NO);
                recPara.setField("ORD_NO",              szORD_NO);
                recPara.setField("ORD_DTL",             szORD_DTL);
                recPara.setField("PLNT_PROC_CD",        szPLNT_PROC_CD);
                recPara.setField("STL_APPEAR_GP",       "G");
                recPara.setField("CURR_PROG_CD",        "H");
                recPara.setField("ORD_YEOJAE_GP",       "2");
                recPara.setField("STL_WT",              szPL_MEA_GDS_WT);
                recPara.setField("DS_MTL_WT",           szPL_MEA_GDS_WT);
                recPara.setField("MTL_STAT_GP",         "2");
                recPara.setField("RECORD_END_GP",       szRECORD_END_GP);
                recPara.setField("RECORD_END_GP1",      "");
                recPara.setField("BEFO_PROG_CD",        szBEFO_PROG_CD);
                recPara.setField("BEF_ORD_NO",          szBEF_ORD_NO);
                recPara.setField("BEF_ORD_DTL",         szBEF_ORD_DTL);
                recPara.setField("MMATL_FEE_NO",        "");
                recPara.setField("ORDERTRANS_MATCH_GP", "C");

                //전문 송신
                ydDelegate.sendMsg(recPara);
                szMsg = "후판제품창고 오버롤체크 진행관리 송신 완료!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            }
        } catch (Exception e) {

            szMsg = "후판제품창고 오버롤 체크 처리중 ERROR : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(getClass().getName() + e.getMessage(), e);

        }   // end try catch
    }

    /**
     * 오퍼레이션명 : 입고존 도착시 TO위치 결정 스케쥴링 처리
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     * @ejb.transaction type="RequiresNew"
     */
    public String procPreMainWrkToLocForPlateYd(String sStlNo,String sRtGp)throws DAOException  {

        String szMethodName         = "procPreMainWrkToLocForPlateYd";
        String szMsg                = "";

        try {

          return YdToLocDcsnUtil.procPreMainWrkToLocForPlateYd(sStlNo, sRtGp, "");

        } catch (Exception e) {

            szMsg = "입고존 도착시 TO위치 결정 스케쥴링 처리중 ERROR : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(getClass().getName() + e.getMessage(), e);

        }   // end try catch
    }

    /**
     * 오퍼레이션명 : 입고존 도착시 TO위치 결정 스케쥴링 처리
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     * @ejb.transaction type="RequiresNew"
     */
  public String procPreMainWrkToLocForPlateYd(String sStlNo,String sRtGp,String sBayGp)throws DAOException  {

        String szMethodName         = "procPreMainWrkToLocForPlateYd";
        String szMsg                = "";

        try {

          return YdToLocDcsnUtil.procPreMainWrkToLocForPlateYd(sStlNo, sRtGp, sBayGp);

        } catch (Exception e) {

            szMsg = "입고존 도착시 TO위치 결정 스케쥴링 처리중 ERROR : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(getClass().getName() + e.getMessage(), e);

        }   // end try catch
    }

  /**
   * 오퍼레이션명 : 입고존 도착시 TO위치 결정 스케쥴링 처리
   *
   * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
   * @param msgRecord
   * @return
   * @throws JDTOException
   * @ejb.transaction type="RequiresNew"
   */
  public String procPreMainWrkToLocForPlateYd(String sStlNo,String sRtGp,String sBayGp,String logId)throws DAOException  {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 START
// procPreMainWrkToLocForPlateYd 사용하는곳이 여러곳이라 argument 에 logId 항목 추가 하지 않고
// 기존  procPreMainWrkToLocForPlateYd(String, String, String) 에서	
// 신규  procPreMainWrkToLocForPlateYd(String, String, String, String) 작성		
// 기존 putLog -> putLogNew logId 출력 되게 개선

      String szMethodName         = "procPreMainWrkToLocForPlateYd";
      String szMsg                = "";

      if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
      
//      szMsg = "입고존 도착시 사전 TO위치결정(후판제품)(RcptWrkDmdSeEJBBean." + szMethodName + ") 시작";
//      ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
      
      try {

        return YdToLocDcsnUtil.procPreMainWrkToLocForPlateYd(sStlNo, sRtGp, sBayGp, logId);

      } catch (Exception e) {

          szMsg = "입고존 도착시 TO위치 결정 스케쥴링 처리중 ERROR : " + e.getMessage();
          ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

          throw new DAOException(getClass().getName() + e.getMessage(), e);

      }   // end try catch
// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////
  }

	
   /**
     * 오퍼레이션명 : 입고존 도착시 동별 분산 로직 추가
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     * @ejb.transaction type="RequiresNew"
     */
	public String procPreMainWrkBookOutLocForPlateYd(String sStlNo,String sRtGp,String logId)throws DAOException  {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// procPreMainWrkBookOutLocForPlateYd argument 에 logId 항목 추가 개선
// public String procPreMainWrkBookOutLocForPlateYd(String sStlNo,String sRtGp)throws DAOException  {
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        String szMethodName         = "procPreMainWrkBookOutLocForPlateYd";
        String szMsg                = "";
        
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// logId Empty 이면 logId 신규 생성 개선
if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "입고존 도착시 동별 분산(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        try {

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// YdToLocDcsnUtil.procPreMainWrkBookOutLocForPlateYd call 시  logId 항목 추가 개선
//          return YdToLocDcsnUtil.procPreMainWrkBookOutLocForPlateYd(sStlNo, sRtGp);
            return YdToLocDcsnUtil.procPreMainWrkBookOutLocForPlateYd(sStlNo, sRtGp, logId);
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        } catch (Exception e) {

            szMsg = "입고존 도착시 TO위치 결정 스케쥴링 처리중 ERROR : " + e.getMessage();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

            throw new DAOException(getClass().getName() + e.getMessage(), e);

        }   // end try catch

    }
	
    /**
     * 오퍼레이션명 : 후판제품창고 오버롤 체크 (YDYDJ297)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     * @ejb.transaction type="RequiresNew"
     */
    public JDTORecordSet procPlateOverRollCheck_Sub(JDTORecord msgRecord)throws DAOException  {

        // 저장품 DAO
        YdStockDao ydStockDao      = new YdStockDao();

        //메세지
        String szMsg            = "";
        //METHOD명
        String szMethodName     = "procPlateOverRollCheck_Sub";
        String szOperationName  = "후판제품창고 오버롤 체크수신";

        //적치재료매수(String)
        String szYD_STK_BED_STL_SH = null;
        //적치재료매수(int)
        int intMtlCnt              = 0;
        //재료번호
        String szSTL_NO         = "";
        //오버롤 체크여부
        String szSTAMP_RESULT   = "";

        JDTORecord    recPara       = null;
        JDTORecord    recInPara     = null;
        JDTORecordSet rsResult      = null;

        String szCURR_PROG_CD   = "";
        String szORD_NO         = "";
        String szORD_DTL        = "";

        JDTORecordSet rsRtn = JDTORecordFactory.getInstance().createRecordSet("");
        try {

            //재료매수
            szYD_STK_BED_STL_SH = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_STL_SH");
            if(szYD_STK_BED_STL_SH.equals("")) {

                szMsg = "["+szOperationName+"] [전문 이상] 재료매수가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return rsRtn;

            }
            //재료매수(int)
            intMtlCnt = Integer.parseInt(szYD_STK_BED_STL_SH);
            //재료번호
            for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

                szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO" + Loop_i);

                rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
                //레코드 생성
                recPara  = JDTORecordFactory.getInstance().create();

                //재료번호
                recPara.setField("PLATE_NO", szSTL_NO);

                //저장품 테이블 조회
                ydStockDao.getYdStock(recPara, rsResult, 4);

                rsResult.first();
                recInPara = rsResult.getRecord();

                szCURR_PROG_CD  = ydDaoUtils.paraRecChkNull(recInPara, "CURR_PROG_CD");
                szORD_NO        = ydDaoUtils.paraRecChkNull(recInPara, "ORD_NO");
                szORD_DTL       = ydDaoUtils.paraRecChkNull(recInPara, "ORD_DTL");

                if("H".equals(szCURR_PROG_CD)){
                    //큐전송 항목 저장 레코드 생성
                    recPara = JDTORecordFactory.getInstance().create();
                    //JMS TC CODE
                    recPara.setField("TC_CODE",     "YDPTJ004");
                    //적치열구분
                    recPara.setField("MTL_NO",      szSTL_NO);
                    //적치BED번호
                    recPara.setField("ORD_NO",      szORD_NO);
                    //적치BED번호
                    recPara.setField("ORD_DTL",     szORD_DTL);

                    EJBConnector ydEjbCon = new EJBConnector("default", this);
                    recPara = (JDTORecord)ydEjbCon.trx("CommRcvHdFaEJB", "rcvQtMgtSysInfo501", recPara);

                    szSTAMP_RESULT = ydDaoUtils.paraRecChkNull(recPara, "STAMP_RESULT");
                }else{
                    szSTAMP_RESULT = "";
                }

                if("Y".equals(szSTAMP_RESULT)){

                    /*
                     * 1. Plate공통 주여구분 변경
                     */
                    recPara = JDTORecordFactory.getInstance().create();
                    recPara.setField("PLATE_NO",        szSTL_NO);
                    recPara.setField("ORD_YEOJAE_GP",   "2");

                    //EJBConnector ydEjbCon = new EJBConnector("default", this);
                    //recPara = (JDTORecord)ydEjbCon.trx("RcptWrkDmdSeEJB", "procPlateOverRollCheck_Sub", recPara);
                    //this.procPlateOverRollCheck_Sub(recPara);

                    //ydStockDao.update_PlateYeajaeGp(recPara);
                    /*
                     * 2011.05.11 윤재광
                     * 진행관리 모듈에서 Plate공통을 업데이트하기때문에
                     * 트랜잭션분리하지 않음 - DB Lock 현상발생.
                     */
                    ydStockDao.update_PlateYeajaeGpTX(recPara);

                    szMsg = "후판제품창고 오버롤체크 주문여재구분 항목 변경!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    rsRtn.addRecord(recInPara);
                }
            }

            return rsRtn;

        } catch (Exception e) {

            szMsg = "후판제품창고 오버롤 체크 처리중 ERROR : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(getClass().getName() + e.getMessage(), e);

        }   // end try catch
    }

    /**
     * 오퍼레이션명 : 후판압연전단L2 BOOK_IN완료수신 (TL3CII : P2YDL003: 사용안함)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procP2BookInReq(JDTORecord msgRecord)throws JDTOException  {
        //후판L2제품번호        PL_L2_TRK_NO            VARCHAR2    16
        //후판재료번호          PL_MTL_NO               VARCHAR2    10
        //후판트래킹존번호      PL_TRCK_ZONE_NO         NUMBER      5
        //후판날판위치조정1 PL_MPL_LOC_ADJ1         NUMBER      5
        //후판날판위치조정2 PL_MPL_LOC_ADJ2         NUMBER      5
        //후판날판위치조정3 PL_MPL_LOC_ADJ3         NUMBER      5
        //후판제품두께          PL_MEA_GDS_T            NUMBER      6   3
        //후판제품폭            PL_MEA_GDS_W            NUMBER      5   1
        //후판제품길이          PL_MEA_GDS_L            NUMBER      5
        //후판북인위치          PL_BOOK_IN_LOC          VARCHAR2    1
        //후판압연패스수        PL_ROLLING_PASS_CNT     NUMBER      2
        //후판북인일시          PL_BOOK_IN_DATE         DATE


        // DAO 및 UTIL 객체 생성
        YdUtils ydutils              = new YdUtils();
        YdPrepSchDao ydPrepSchDao    = new YdPrepSchDao();
        YdStockDao ydStockDao        = new YdStockDao();
        YdStkLyrDao ydStkLyrDao      = new YdStkLyrDao();


        //레코드 선언
        JDTORecord recPara           = null;
        JDTORecord recEdit           = null;
        JDTORecord recTemp           = null;
        JDTORecord recGetVal         = null;
        JDTORecordSet rsResult       = null;


        // 변수 선언
        String szMethodName          = "procP2BookInReq";
        String szMsg                 = "";
        String szUser                = "SYSTEM";
        String szPL_L2_TRK_NO        = "";
        String szPL_MTL_NO           = "";
        String szPL_TRCK_ZONE_NO     = "";
        String szPL_MPL_LOC_ADJ1     = "";
        String szPL_MPL_LOC_ADJ2     = "";
        String szPL_MPL_LOC_ADJ3     = "";
        String szPL_MEA_GDS_T        = "";
        String szPL_MEA_GDS_W        = "";
        String szPL_MEA_GDS_L        = "";
        String szPL_BOOK_IN_LOC      = "";
        String szPL_ROLLING_PASS_CNT = "";
        String szPL_BOOK_IN_DATE     = "";
        String[] arrRT_ZONE_NO       = null;
        String szYD_V_COL_GP         = "";
        String szYD_V_BED_NO         = "";
        int nRet                     = 0;
        int nMtlSH                   = 0;
        boolean isExistBed           = false;
        boolean blnRtnVal            = false;


        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode == null) {
            //에러 리턴
            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }


        //TC CODE DISPLAY
        if(bDebugFlag) {
            szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }


        try{
            recEdit = JDTORecordFactory.getInstance().create();


            //=============================================================
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판압연전단L2] BOOK_IN완료 수신";
            ydUtils.putLogMsg("D", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);





            // 수신전문 항목 추출
            szPL_L2_TRK_NO = ydDaoUtils.paraRecChkNull(msgRecord, "PL_L2_TRK_NO");
            if(szPL_L2_TRK_NO.trim().equals("")){
                szMsg = "후판L2제품번호(PL_L2_TRK_NO) 값이 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            }


            szPL_MTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO");
            if(szPL_MTL_NO.trim().equals("")){
                szMsg = "후판재료번호(PL_MTL_NO) 값이 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            }

            szPL_TRCK_ZONE_NO = ydDaoUtils.paraRecChkNull(msgRecord, "PL_TRCK_ZONE_NO");
            if(szPL_TRCK_ZONE_NO.trim().equals("")){
                szMsg = "후판트래킹존번호(PL_TRCK_ZONE_NO) 값이 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            }

            szPL_MPL_LOC_ADJ1 = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MPL_LOC_ADJ1");
            if(szPL_MPL_LOC_ADJ1.trim().equals("")){
                szMsg = "후판날판위치조정(PL_MPL_LOC_ADJ1) 값이 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            }

            szPL_MPL_LOC_ADJ2 = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MPL_LOC_ADJ2");
            if(szPL_MPL_LOC_ADJ2.trim().equals("")){
                szMsg = "후판날판위치조정(PL_MPL_LOC_ADJ2) 값이 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            }

            szPL_MPL_LOC_ADJ3 = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MPL_LOC_ADJ3");
            if(szPL_MPL_LOC_ADJ3.trim().equals("")){
                szMsg = "후판날판위치조정(PL_MPL_LOC_ADJ3) 값이 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            }

            szPL_MEA_GDS_T = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MEA_GDS_T");
            if(szPL_MEA_GDS_T.trim().equals("")){
                szMsg = "후판제촌제품두께(PL_MEA_GDS_T) 값이 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            }

            szPL_MEA_GDS_W = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MEA_GDS_W");
            if(szPL_MEA_GDS_W.trim().equals("")){
                szMsg = "후판제촌제품폭(PL_MEA_GDS_W) 값이 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            }

            szPL_MEA_GDS_L = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MEA_GDS_L");
            if(szPL_MEA_GDS_L.trim().equals("")){
                szMsg = "후판제촌제품길이(PL_MEA_GDS_L) 값이 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            }

            szPL_BOOK_IN_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "PL_BOOK_IN_LOC");
            if(szPL_BOOK_IN_LOC.trim().equals("")){
                szMsg = "후판북인위치(PL_BOOK_IN_LOC) 값이 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            }

            szPL_ROLLING_PASS_CNT = ydDaoUtils.paraRecChkNull(msgRecord, "PL_ROLLING_PASS_CNT");
            if(szPL_ROLLING_PASS_CNT.trim().equals("")){
                szMsg = "후판압연패스수(PL_ROLLING_PASS_CNT) 값이 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            }

            szPL_ROLLING_PASS_CNT = ydDaoUtils.paraRecChkNull(msgRecord, "PL_ROLLING_PASS_CNT");
            if(szPL_ROLLING_PASS_CNT.trim().equals("")){
                szMsg = "후판압연패스수(PL_ROLLING_PASS_CNT) 값이 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            }

            szPL_BOOK_IN_DATE = ydDaoUtils.paraRecChkNull(msgRecord, "PL_BOOK_IN_DATE");
            if(szPL_BOOK_IN_DATE.trim().equals("")){
                szMsg = "후판북인일시(PL_BOOK_IN_DATE) 값이 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            }


            // 준비스케줄 JOIN 준비재료 조회
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("STL_NO", szPL_MTL_NO);
            nRet = ydPrepSchDao.getYdPrepsch(recPara, rsResult, 21);
            if(nRet < 0){
                szMsg = "준비스케줄 + 준비재료 조회 중 PARAMETER ERROR nRet[" + nRet + "]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            } else if(nRet == 0){
                szMsg = "준비스케줄 + 준비재료 조회 중 조회건수가 없음 STL_NO[" + szPL_MTL_NO + "] nRet[" + nRet + "]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


            } else {
                szMsg = "준비스케줄 + 준비재료 조회 성공 STL_NO[" + szPL_MTL_NO + "] nRet[" + nRet + "]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                nMtlSH = nRet;

                // 조회된 매수만큼 반복하면서 레코드를 편집
                for(int i=0; i<nMtlSH; i++){
                    recGetVal = rsResult.getRecord(i);
                    recEdit.setField("STL_NO" + (i+1), ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO" + (i+1)));
                }


                //==================================================================
                // 저장위치 매핑
                //==================================================================
                arrRT_ZONE_NO       = YdCommonUtils.getY4BookOutLoc(szPL_TRCK_ZONE_NO);
                String szYdStkColGp = arrRT_ZONE_NO[0];    // 적치열구분
                String szYdStkBedNo = arrRT_ZONE_NO[1];    // 적치베드번호

                szMsg = "적치열[" + szYdStkColGp + "], 적치베드[" + szYdStkBedNo + "]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


                //==================================================================
                // 적치단에 업데이트
                //==================================================================
                for(int j=nMtlSH; j>0; j--) {
                    //INSERT항목 레코드 생성
                    recPara = JDTORecordFactory.getInstance().create();

                    //적치단 재료상태가 적치 가능이면 재료 등록
                    //적치단 테이블 업데이트
                    //적치열구분 = 설비ID
                    recPara.setField("YD_STK_COL_GP"      , szYdStkColGp);
                    recPara.setField("YD_STK_BED_NO"      , szYdStkBedNo);
                    recPara.setField("YD_STK_LYR_NO"      , "00" + (j));
                    recPara.setField("MODIFIER"           , szUser);
                    recPara.setField("YD_STK_LYR_MTL_STAT", "C");
                    recPara.setField("STL_NO"             , recEdit.getField("STL_NO" + j) );

                    //업데이트 실행
                    nRet = ydStkLyrDao.updYdStklyr(recPara, 0);
                    if(nRet == 1) {
                        szMsg = "적치열[" + szYdStkColGp + "], 적치베드[" + szYdStkBedNo + "] 적치단 Update 성공!1";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    } else {
                        szMsg = "적치열[" + szYdStkColGp + "], 적치베드[" + szYdStkBedNo + "] 적치단 Update 성공!0";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        return;
                    }
                }


                //==================================================================
                // 저장위치 매핑
                //==================================================================
                szMsg = "변환전 BOOKIN[" + szPL_BOOK_IN_LOC ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                arrRT_ZONE_NO = YdCommonUtils.getY4BookOutLoc(szPL_BOOK_IN_LOC);

                szMsg = "변환전 BOOKIN[" + szPL_BOOK_IN_LOC + "변환 적치열[" + arrRT_ZONE_NO[0] + "], 변환 적치베드[" + arrRT_ZONE_NO[1] + "] ";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                szYD_V_COL_GP = arrRT_ZONE_NO[0];
                szYD_V_BED_NO = arrRT_ZONE_NO[1];


//DONG_INSERT : 사용안하지만 OK
                //==================================================================
                // 대기베드순서를 차례로 조회한다. ??? k만큼 도는데 BED번호 변경???
                //==================================================================
                for(int k=1; k<=3; k++) {
                    //적치열과 베드번호로 적치단을 조회해서 재료가 권상대기 권하대기 적치중인것이 있는 것만 찾는다.빈베드가 적치가능한 대기베드임
                    szMsg = "가상베드 조회 - 적치열[" + szYD_V_COL_GP + "], 적치베드[" + szYD_V_BED_NO + "]";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                    recPara = JDTORecordFactory.getInstance().create();

//                  szYD_V_BED_NO = ydDaoUtils.stringPlusInt2(szYD_V_BED_NO, 1);
                    szYD_V_BED_NO = ydDaoUtils.stringPlusNext(szYD_V_BED_NO, 1);
                    recPara.setField("YD_STK_COL_GP", szYD_V_COL_GP);
                    recPara.setField("YD_STK_BED_NO", szYD_V_BED_NO);
                    /*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrByColGpBedNo*/
                    nRet = ydStkLyrDao.getYdStklyr(recPara, rsResult, 29);
                    if(nRet == 0) {
                        szMsg = "적치가능한 대기Bed를 찾았습니다. YD_STK_COL_GP(" + szYD_V_COL_GP + ")  YD_STK_BED_NO(" + szYD_V_BED_NO + ")";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        isExistBed = true;
                        break;
                    }
                }


                // 대기배드가 존재시 대기베드에 적치한다.
                if(isExistBed){
                    recTemp = JDTORecordFactory.getInstance().create();

                    for(int m=nMtlSH; m>0; m--){
                        //INSERT항목 레코드 생성
                        recPara = JDTORecordFactory.getInstance().create();

                        //==============================================
                        //적치단 재료상태가 적치 가능이면 재료 등록
                        //적치단 테이블 업데이트
                        //적치열구분 = 설비ID
                        //==============================================
                        recPara.setField("YD_STK_COL_GP",       szYD_V_COL_GP);
                        recPara.setField("YD_STK_BED_NO",       szYD_V_BED_NO);
                        recPara.setField("YD_STK_LYR_NO",       "00" + (m));
                        recPara.setField("MODIFIER",            szUser);
                        recPara.setField("YD_STK_LYR_MTL_STAT", "C");
                        recPara.setField("STL_NO",              recEdit.getField("STL_NO" + m));
                        nRet = ydStkLyrDao.updYdStklyr(recPara, 0);
                        if(nRet == 1) {
                            szMsg = "대기베드 적치단 Update 성공!";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        } else {
                            szMsg = "대기베드 적치단 Update 실패!";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                            return;
                        }


                        //==============================================
                        // PLATE공통 테이블에 야드위치와 BookOut위치 업데이트
                        //==============================================
                        recTemp = JDTORecordFactory.getInstance().create();
                        recTemp.setField("YD_BOOK_OUT_LOC", szPL_TRCK_ZONE_NO);
                        recTemp.setField("PLATE_NO"       , szPL_MTL_NO);
                        recTemp.setField("YD_STR_LOC"     , szYD_V_COL_GP);
                        nRet = ydStockDao.updYdPlateCommBookOutLoc(recTemp, 0);
                        if(nRet > 0){
                            szMsg = "PLATECOMM테이블에 PLATE_NO(" + szPL_MTL_NO + ") BookOut위치 'K_RTR_' 업데이트 성공";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        } else {
                            szMsg = "PLATECOMM테이블에 PLATE_NO(" + szPL_MTL_NO + ") BookOut위치 'K_RTR_' 업데이트 실채 nRet[" + nRet + "]";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        }

                        szMsg = "재료공통 업데이트 처리";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    }
                }
            }

            szMsg = "A후판 창고 야드 BOOK_IN 처리 수신 완료!";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        } catch (Exception e) {
            szMsg = "A후판 창고 야드 BOOK_IN 처리중 ERROR : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }
    } // end of procP2BookInReq()


    /**
     * 오퍼레이션명 : A후판 Book-Out실적 (PRYDJ006) [권오창 2009.12.11]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  msgRecord
     * @return
     * @throws JDTOException
     */
    public void procAPlBookOutWr(JDTORecord msgRecord) throws JDTOException  {
        // DAO 및 UTIL 객체 선언
        YdStockDao ydStockDao        = new YdStockDao();
        YdStkLyrDao ydStkLyrDao      = new YdStkLyrDao();
        YdPlateCommDAO  commDao      = new YdPlateCommDAO();

        // 레코드 선언
        JDTORecordSet rsResult       = null;
        JDTORecord recEdit           = null;
        JDTORecord recPara           = null;
        JDTORecord recGetVal         = null;
        JDTORecordSet rsOutRecSet        = null;

        // 변수 선언
        String szMethodName          = "procAPlBookOutWr";
        String szMsg                 = "";
        String szPL_L2_TRK_NO        = "";    // 후판L2제품번호
        String szPL_MTL_NO           = "";    // 후판재료번호
        String szPL_TRCK_ZONE_NO     = "";    // 후판트래킹존번호
        String szPL_MPL_LOC_ADJ1     = "";    // 후판날판위치조정
        String szPL_MPL_LOC_ADJ2     = "";    // 후판날판위치조정
        String szPL_MPL_LOC_ADJ3     = "";    // 후판날판위치조정
        String szPL_MEA_GDS_T        = "";    // 후판제촌제품두께
        String szPL_MEA_GDS_W        = "";    // 후판제촌제품폭
        String szPL_MEA_GDS_L        = "";    // 후판제촌제품길이
        String szPL_BOOK_OUT_LOC     = "";    // 후판북아웃위치
        String szPL_ROLLING_PASS_CNT = "";    // 후판압연패스수
        String szPL_BOOK_OUT_MOD     = "";    // 후판북아웃모드
        String szPL_BOOK_OUT_DATE    = "";    // 후판북아웃일시
        String szPL_BOOK_OUT_RSN     = "";    // 후판북아웃사유
        String szTemp                = "";
        String szYD_STR_LOC[]        = null;  // 후판야드저장위치
        String szPL_PLATE_NO         = "";
        String szPL_DIV_TRIM_GP_CD   = "";
        String szPL_BOOK_OUT_RSN_JJYD     = "";   // 후판북아웃사유

        int nRet                     = 0;
        int intRtnVal                = 0;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(msgRecord, "T");    	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "A후판 Book-Out실적 (" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        
        // TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode == null) {
            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            return;
        }

        try {
            //=============================================================
            // Log 테이블 등록
            //=============================================================
            szMsg = "[후판조업] A후판BookOut 실적수신";
            ydUtils.putLogMsg("K", YdConstant.YD_MONITORING_CHANNEL_K, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            szPL_L2_TRK_NO = ydDaoUtils.paraRecChkNull(msgRecord, "PL_L2_TRK_NO");
            if(szPL_L2_TRK_NO.trim().equals("")){
                szMsg = "후판L2제품번호(PL_L2_TRK_NO) 값이 없습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            }

            szPL_MTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO");
            if(szPL_MTL_NO.trim().equals("")){
                szMsg = "후판재료번호(PL_MTL_NO) 값이 없습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            }

            szPL_TRCK_ZONE_NO = ydDaoUtils.paraRecChkNull(msgRecord, "PL_TRCK_ZONE_NO");
            if(szPL_TRCK_ZONE_NO.trim().equals("")){
                szMsg = "후판트래킹존번호(PL_TRCK_ZONE_NO) 값이 없습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return;
            }

            szPL_BOOK_OUT_MOD = ydDaoUtils.paraRecChkNull(msgRecord, "PL_BOOK_OUT_MOD");
            if(szPL_BOOK_OUT_MOD.trim().equals("")){
                szMsg = "후판북아웃모드(PL_BOOK_OUT_MOD) 값이 없습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return;
            }

            szPL_BOOK_OUT_RSN = ydDaoUtils.paraRecChkNull(msgRecord, "PL_BOOK_OUT_RSN");
            if(szPL_BOOK_OUT_RSN.trim().equals("")){
                szMsg = "후판북아웃사유(PL_BOOK_OUT_RSN) 값이 없습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            }

            String sQACd = "";
            if(szPL_BOOK_OUT_RSN.length() >= 5){
                sQACd = szPL_BOOK_OUT_RSN.substring(3 , 5);

                if("29".equals(sQACd)){// 29 : QA검사장 북아웃 코드

                    this.procPlateQAYardInfo(getL3PlateNo(szPL_L2_TRK_NO,szPL_MTL_NO));
                    return;
                }
            }
            /*
             * 2014.03.11 윤재광
             * 1후판 정정야드 BOOK OUT 요구기능 삭제처리
             *
            szPL_BOOK_OUT_RSN_JJYD = ydDaoUtils.paraRecChkNull(msgRecord, "PL_BOOK_OUT_RSN");
            int strlen = szPL_BOOK_OUT_RSN_JJYD.length();

            if(strlen == 6)
            {
                if((szPL_TRCK_ZONE_NO.startsWith("3")||
                    szPL_TRCK_ZONE_NO.startsWith("4")||
                    szPL_TRCK_ZONE_NO.startsWith("53")||
                    szPL_TRCK_ZONE_NO.startsWith("54")||
                    szPL_TRCK_ZONE_NO.startsWith("5599")||
                    szPL_TRCK_ZONE_NO.startsWith("57"))){

                    szMsg = "후판정정야드 BOOK OUT 정보처리.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    //Private Method 호출
                    this.procPlateJJYardInfo(szPL_L2_TRK_NO,
                                             szPL_MTL_NO,
                                             szPL_TRCK_ZONE_NO,
                                             szPL_BOOK_OUT_RSN_JJYD);
                    return;
                }
            }
            */
            if("9".equals(szPL_BOOK_OUT_MOD)            //입고존 출발구분
            ||"10".equals(szPL_BOOK_OUT_MOD)            //입고존 도착구분
            ||"11".equals(szPL_BOOK_OUT_MOD)){          //C/R SCH 기동
            }else{
                szMsg = "####################################################";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                szMsg = "후판 제품창고 관련 후판북아웃모드(PL_BOOK_OUT_MOD)가 아닙니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                szMsg = "재료번호["+getL3PlateNo(szPL_L2_TRK_NO,szPL_MTL_NO)+"] 북아웃코드["+szPL_TRCK_ZONE_NO+"] 북아웃모드["+szPL_BOOK_OUT_MOD+"]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return;
            }

            if("58010".equals(szPL_TRCK_ZONE_NO)){  //오프라인 A동 R/T 존
                szMsg = "####################################################";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                szMsg = "후판 제품창고 A동 오프라인 북아웃대상 정보.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                szMsg = "재료번호["+getL3PlateNo(szPL_L2_TRK_NO,szPL_MTL_NO)+"] 북아웃코드["+szPL_TRCK_ZONE_NO+"] 북아웃모드["+szPL_BOOK_OUT_MOD+"]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return;
            }

            /*
             * L2 후판번호 > L3 후판번호 변환작업.
             */
            msgRecord.setField("PL_MTL_NO", getL3PlateNo(szPL_L2_TRK_NO,szPL_MTL_NO));

            szPL_MPL_LOC_ADJ1 = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MPL_LOC_ADJ1");
            if(szPL_MPL_LOC_ADJ1.trim().equals("")){
                szMsg = "후판날판위치조정(PL_MPL_LOC_ADJ1) 값이 없습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            }

            szPL_MPL_LOC_ADJ2 = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MPL_LOC_ADJ2");
            if(szPL_MPL_LOC_ADJ2.trim().equals("")){
                szMsg = "후판날판위치조정(PL_MPL_LOC_ADJ2) 값이 없습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            }

            szPL_MPL_LOC_ADJ3 = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MPL_LOC_ADJ3");
            if(szPL_MPL_LOC_ADJ3.trim().equals("")){
                szMsg = "후판날판위치조정(PL_MPL_LOC_ADJ3) 값이 없습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            }

            szPL_MEA_GDS_T = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MEA_GDS_T");
            if(szPL_MEA_GDS_T.trim().equals("")){
                szMsg = "후판제촌제품두께(PL_MEA_GDS_T) 값이 없습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            }

            szPL_MEA_GDS_W = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MEA_GDS_W");
            if(szPL_MEA_GDS_W.trim().equals("")){
                szMsg = "후판제촌제품폭(PL_MEA_GDS_W) 값이 없습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            }

            szPL_MEA_GDS_L = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MEA_GDS_L");
            if(szPL_MEA_GDS_L.trim().equals("")){
                szMsg = "후판제촌제품길이(PL_MEA_GDS_L) 값이 없습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            }

            szPL_BOOK_OUT_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "PL_BOOK_OUT_LOC");
            if(szPL_BOOK_OUT_LOC.trim().equals("")){
                szMsg = "후판북아웃위치(PL_BOOK_OUT_LOC) 값이 없습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            }

            szPL_ROLLING_PASS_CNT = ydDaoUtils.paraRecChkNull(msgRecord, "PL_ROLLING_PASS_CNT");
            if(szPL_ROLLING_PASS_CNT.trim().equals("")){
                szMsg = "후판압연패스수(PL_ROLLING_PASS_CNT) 값이 없습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            }

            szPL_BOOK_OUT_DATE = ydDaoUtils.paraRecChkNull(msgRecord, "PL_BOOK_OUT_DATE");
            if(szPL_BOOK_OUT_DATE.trim().equals("")){
                szMsg = "후판북아웃일시(PL_BOOK_OUT_DATE) 값이 없습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            }

            // A후판 Book-Out실적(PRYDJ006) 수신하면 전문의 후판트래킹존번호(PL_TRCK_ZONE_NO)의 앞 두자리를 읽어
            // 56이거나 58 일 경우 기존에 개발되어 있는 BookOut완료수신처리 (P2YDL002)를 호출하고 그 외의 경우에는
            // 기존에 개발되어 있었던 A후판 제품행선변경 실적처리(PRYDJ005)를 호출하여 처리를 하고 난 후 PLATE공통에 BookOut
            // 저장위치(YD_BOOK_OUT_LOC)항목에 해당하는 코드값, 야드위치(YD_STR_LOC)에는 코드를 업데이트 한다.

            if(szPL_TRCK_ZONE_NO.length() > 2){
                szTemp = szPL_TRCK_ZONE_NO.substring(0, 2);
            }
            szMsg = "후판트래킹존번호 앞 두자리 값 : " + szTemp;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            //--2012.03.28 수정 (3기) : szTemp.equals("59") 조건 추가
            //--2025.09.04 수정 (추관식) : szTemp.equals("60") 조건 추가 = G R/T 트래킹번호 정의에 따른 조건 추가
            if(szTemp.equals("56") ||
               szTemp.equals("58") ||
               szTemp.equals("59") ||
               szTemp.equals("60") || //G R/T 추가
               szPL_TRCK_ZONE_NO.startsWith("5599")){   // 가적장 추가.
                // 1. BookOut 완료수신처리 : procP2BookOutReq()
                szMsg = "A후판 Book-Out실적 (PRYDJ006) => BookOut완료수신처리";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                // 2. 56000 BOOK OUT ZONE 이벤트 처리
                // 2010.05.06 윤재광 모드항목으로 체크 변경
                //if("56000".equals(szPL_TRCK_ZONE_NO)){
                if("10".equals(szPL_BOOK_OUT_MOD)){ //입고존 도착구분 = 10

                    //----------------------------------------------------------------------------------------------------------------------
                    //  1후판정정야드 저장위치 삭제기능 추가
                    //----------------------------------------------------------------------------------------------------------------------
                    JPlateYdStkLyrDAO   tmpDao  = new JPlateYdStkLyrDAO();

                    // 해당 재료번호가 적치중일 경우 해당 적치 정보 삭제 기능 추가 ==============================================
                    JDTORecord  recDelPara  = JDTORecordFactory.getInstance().create();
                    JDTORecordSet rsDelInfo = JDTORecordFactory.getInstance().createRecordSet("rsdelInfo");

                    String szStlNo = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO");
                    recDelPara.setField("STL_NO"                , szStlNo);
                    recDelPara.setField("YD_STK_LYR_MTL_STAT"   , "");
                    recDelPara.setField("YD_GP"                 , "P");

                    int iRtnVal  = tmpDao.getYdStklyrByStlNoStat(recDelPara, rsDelInfo);        // intGp == 3

                    if (iRtnVal > 0) {

                        //정보 존재시 해당 Map Clear
                        rsDelInfo.first();

                        do {
                            recDelPara   =  JDTORecordFactory.getInstance().create();
                            recDelPara   =  rsDelInfo.getRecord();

                            recDelPara.setField("STL_NO",               "");
                            recDelPara.setField("YD_STK_LYR_MTL_STAT",  "E");

                            intRtnVal = tmpDao.updYdStklyrStat(recDelPara);     // intGp == 0

                            szMsg = "A후판 Book-Out실적 (PRYDJ006) => 1후판정정야드 저장위치 정보 삭제처리 +["+szStlNo+"]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                        } while(rsDelInfo.next());
                    }

                    //----------------------------------------------------------------------------------------------------------------------
                    //  입고존 도착시 동별 분산 로직 추가요청 2020.12.09 이명운
                    //  - R/T상 입고 제품 기준 E/F동 자동분산(두께 합산 50T미만)
                    //  - D/E R/T
                    //  - 제품 초단척 제품(길이그룹 U)
                    //----------------------------------------------------------------------------------------------------------------------
                    String sHmiStat         = "N";
                    String sQuery1          = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
                    JDTORecord wbJr         = (new com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO()).getData(sQuery1, new Object[]{ "2XRT01" });
                    if (wbJr != null){
                        sHmiStat    = StringHelper.evl(wbJr.getFieldString("HMI_STAT"), "");
                    }
                    if("Y".equals(sHmiStat)){

                        String sRtGp    = "";

                        if(szTemp.equals("58")){
                            sRtGp = "F";    // F R/T  - OFF-LINE
                        }else if(szTemp.equals("59")){
                            sRtGp = "D";    // D R/T  - #2 OFF-LINE
                        }else if(szTemp.equals("60")){
                        	sRtGp = "G";	// G R/T 추가
                        }else {
                            sRtGp = "E";    // E R/T  - On-LINE
                        }

                        EJBConnector ejbConn = new EJBConnector("default", "RcptWrkDmdSeEJB", this);
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// RcptWrkDmdSeEJB.procPreMainWrkBookOutLocForPlateYd call 시  inRecord 에 logId SET 추가 개선
//                      ejbConn.trx("procPreMainWrkBookOutLocForPlateYd", new  Class[] { String.class, String.class, String.class},
//                              new Object[] { ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO") , sRtGp,logId });
                        ejbConn.trx("procPreMainWrkBookOutLocForPlateYd", new  Class[] { String.class, String.class, String.class},
                                new Object[] { ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO") , sRtGp, logId });

// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
                    }
                    //----------------------------------------------------------------------------------------------------------------------
                    //  입고존 도착시 동별 분산 로직 추가 수행 종료
                    //----------------------------------------------------------------------------------------------------------------------

                    //----------------------------------------------------------------------------------------------------------------------
                    //  입고존 도착시 TO위치 결정로직 수행 시작
                    //----------------------------------------------------------------------------------------------------------------------
                    {
                        String sRtGp    = "";

                        if(szTemp.equals("58")){
                            sRtGp = "F";    // F R/T  - OFF-LINE
                        }else if(szTemp.equals("59")){
                            sRtGp = "D";    // D R/T  - #2 OFF-LINE
                        }else if(szTemp.equals("60")){
                        	sRtGp = "G";	// G R/T 추가
                        }else {
                            sRtGp = "E";    // E R/T  - On-LINE
                        }

                        EJBConnector ejbConn = new EJBConnector("default", "RcptWrkDmdSeEJB", this);
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ejbConn.trx("procPreMainWrkToLocForPlateYd", new  Class[] { String.class, String.class},
//                              new Object[] { ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO") , sRtGp });
                        ejbConn.trx("procPreMainWrkToLocForPlateYd", new  Class[] { String.class, String.class, String.class, String.class},
                                new Object[] { ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO") , sRtGp, "", logId });
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
                    }
                    //----------------------------------------------------------------------------------------------------------------------
                    //  입고존 도착시 TO위치 결정로직 수행 종료
                    //----------------------------------------------------------------------------------------------------------------------

                    /*
                     * A/B R/T Tracking Info로 활용
                     */
                    szMsg = "A후판 Book-Out실적 (PRYDJ006) => BOOK OUT 입고존 ZONE +["+szPL_MTL_NO+"]["+szPL_TRCK_ZONE_NO+"]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                    szMsg = "후판제품창고 A R/T 입고대기존 트래킹 정보";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    String sTrackingGbn = "";
                    if(szTemp.equals("58")){
                        sTrackingGbn = "2N";    // B(F) R/T 입고존 도착구분
                        //this.procOffLineStlInfo(getL3PlateNo(szPL_L2_TRK_NO,szPL_MTL_NO));

                        EJBConnector ejbConn = new EJBConnector("default", "RcptWrkDmdSeEJB", this);
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// procOffLineStlInfo argument 에 logId 항목 추가 개선
//                      ejbConn.trx("procOffLineStlInfo", new Class[] {String.class }, new Object[] { ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO")});
                        ejbConn.trx("procOffLineStlInfo", new Class[] {String.class, String.class }, new Object[] { ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO"), logId });
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

                    }else if(szTemp.equals("59")){ //--2012.03.28 수정 (3기)
                        sTrackingGbn = "2A";    // C(D) R/T 입고존 도착구분 : 원래 공정값은 'AO' 로  입고존 도착은 '2A', 입고존 출발은 '3A' 로 함

                        EJBConnector ejbConn = new EJBConnector("default", "RcptWrkDmdSeEJB", this);
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// procNo2DSLineStlInfo argument 에 logId 항목 추가 개선
//                      ejbConn.trx("procNo2DSLineStlInfo", new Class[] {String.class }, new Object[] { ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO")});
                        ejbConn.trx("procNo2DSLineStlInfo", new Class[] {String.class, String.class }, new Object[] { ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO"), logId });
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

                    }else if(szTemp.equals("60")){ //2025.10.14 G R/T 추가
                    	sTrackingGbn = "2P";   //G R/T 입고존 도착구분 : 입고존 도착은 '2P', 입고존 출발은 '3P' 로 함
                        EJBConnector ejbConn = new EJBConnector("default", "RcptWrkDmdSeEJB", this);
                        
                        ejbConn.trx("procNo3DSLineStlInfo", new Class[] {String.class, String.class }, new Object[] { ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO"), logId });
                    } else {
                        sTrackingGbn = "2O";    // A(E) R/T 입고존 도착구분

                        EJBConnector ejbConn = new EJBConnector("default", "RcptWrkDmdSeEJB", this);
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// procNo1DSLineStlInfo argument 에 logId 항목 추가 개선
//                      ejbConn.trx("procNo1DSLineStlInfo", new Class[] {String.class }, new Object[] { ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO")});
                        ejbConn.trx("procNo1DSLineStlInfo", new Class[] {String.class, String.class }, new Object[] { ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO"), logId });
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

                    }

                    //--------------------------------------------------------------
                    recEdit = JDTORecordFactory.getInstance().create();
                    recEdit.setField("STL_NO",          ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO"));
                    recEdit.setField("PL_RCPT_TRK_NO",  sTrackingGbn+YdUtils.getCurDate("yyyyMMddHHmmss"));
                    //--------------------------------------------------------------

                    intRtnVal = ydStockDao.updYdStock(recEdit,0);

                    intRtnVal = ydStockDao.update_Dm_Time(recEdit,2);

                    szMsg = "YD_STOCK[저장품] UPDATE 결과 :: [" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    /*
                     * 입고존 도착시점에 ON-OFF LINE에 따른 저장위치 강제 UPDATE
                     */
                    String sBayGp = "";
                    String sTempRT="";

                    if(szTemp.equals("58")){
                        sBayGp = "F"; //통합 전 B
                    }else if(szTemp.equals("59")){ //--2012.03.28 수정 (3기)
                        sBayGp = "D"; //통합 전 C
                    }else if(szTemp.equals("60")){
                    	sBayGp = "G";
                    }else{
                        sBayGp = "E"; //통합 전 A
                    }

//                  2022.08. 23  레일공사로 인한 임시 로직 변경
//                  요청사항  D동으로 오는 제품들을 F, C동으로 이전
//                  제품 SIZE MM : F동 ,
//                  제품 SIZE LM : C동, SM도 C동
/*=============================================================================================================================================*/
/*
                    szMsg = "[임시로직] 확인5 YD_BAY_GP : "+ sBayGp;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    if ("D".equals(sBayGp) && szPL_MTL_NO.startsWith("FC"))
                    {
                        recPara = JDTORecordFactory.getInstance().create();
                        rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");
                        recPara.setField("STL_NO", szPL_MTL_NO);

                        intRtnVal = commDao.select(recPara, rsOutRecSet, "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockSpecWL");

                        if(intRtnVal>0) {
                            rsOutRecSet.first();
                            recGetVal = rsOutRecSet.getRecord();

                            //제품 폭 길이 구분
                            String szYD_MTL_L_GP    = ydDaoUtils.paraRecChkNull(recGetVal, "YD_MTL_L_GP");          //크레인작업 최하단재료의 길이구분
                            String szYD_MTL_W_GP    = ydDaoUtils.paraRecChkNull(recGetVal, "YD_MTL_W_GP");          //크레인작업 최하단재료의 폭구분

                            szMsg = "[임시로직] 확인5 YD_BAY_GP : "+ sBayGp + " szYD_MTL_W_GP : "+ szYD_MTL_W_GP.substring(0, 1)+ " szYD_MTL_L_GP : "+szYD_MTL_L_GP.substring(0, 1);;
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


                            if ("M".equals(szYD_MTL_W_GP.substring(0, 1))&&"M".equals(szYD_MTL_L_GP.substring(0, 1)))
                            {
                                sBayGp = "F";
                            }
                            else if ("L".equals(szYD_MTL_W_GP.substring(0, 1))&&"M".equals(szYD_MTL_L_GP.substring(0, 1)))
                            {
                                sBayGp = "C";
                            }
                            else if ("S".equals(szYD_MTL_W_GP.substring(0, 1))&&"M".equals(szYD_MTL_L_GP.substring(0, 1)))
                            {
                                sBayGp = "C";
                            }
                        }
                    }
                    szMsg = "[임시로직] 확인6 YD_BAY_GP : "+ sBayGp;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
*/
/*=============================================================================================================================================*/
                    JDTORecord    setRecord         = JDTORecordFactory.getInstance().create();
                    setRecord.setField("YD_GP",             YdConstant.YD_GP_PLATE2_GDS_YARD);
                    setRecord.setField("YD_BAY_GP",         sBayGp);
                    setRecord.setField("YD_EQP_GP",         "RT");
                    setRecord.setField("YD_STK_COL_NO",     "XA"); //XA의미?  :1후판은 입고대기존 도착시, 현재 RT 위치를  TDRTXA, TERTXA, TFRTXA 형식으로 기입함.
                    setRecord.setField("YD_STK_BED_NO",     "");
                    setRecord.setField("YD_STK_LYR_NO",     "");
                    setRecord.setField("FNL_REG_PGM",       "PRYDJ004");
                    setRecord.setField("MODIFIER",          "PRYDJ004");
                    setRecord.setField("YD_STR_LOC_HIS1",   "") ;
                    setRecord.setField("YD_STR_LOC_HIS2",   "");
                    setRecord.setField("PLATE_NO",          ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO"));
                    setRecord.setField("YD_STR_LOC",        YdConstant.YD_GP_PLATE2_GDS_YARD + sBayGp + "RTXA");  //TDRTXA->TFRTXA되면서 입고모니터링에 안나옴.

                    intRtnVal = ydStockDao.updPtComm_LOC(setRecord, 1);

                }else if("9".equals(szPL_BOOK_OUT_MOD)){ //입고존 출발구분

                    String sTrackingGbn = "";
                    if(szTemp.equals("58")){
                        sTrackingGbn = "3N";    // B R/T 입고존 출발구분
                        //this.procOffLineStlInfo(getL3PlateNo(szPL_L2_TRK_NO,szPL_MTL_NO));

                        EJBConnector ejbConn = new EJBConnector("default", "RcptWrkDmdSeEJB", this);
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// procOffLineStlInfo argument 에 logId 항목 추가 개선
//                      ejbConn.trx("procOffLineStlInfo", new Class[] {String.class }, new Object[] { getL3PlateNo(szPL_L2_TRK_NO,szPL_MTL_NO)});
                        ejbConn.trx("procOffLineStlInfo", new Class[] {String.class, String.class }, new Object[] { getL3PlateNo(szPL_L2_TRK_NO,szPL_MTL_NO), logId });
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

                    }else if(szTemp.equals("59")){ //--2012.03.28 수정 (3기)
                        sTrackingGbn = "3A";    // C(D) R/T 입고존 도착구분 : 원래 공정값은 'AO' 로  입고존 도착은 '2A', 입고존 출발은 '3A' 로 함

                        EJBConnector ejbConn = new EJBConnector("default", "RcptWrkDmdSeEJB", this);
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// procNo2DSLineStlInfo argument 에 logId 항목 추가 개선
//                      ejbConn.trx("procNo2DSLineStlInfo", new Class[] {String.class }, new Object[] { getL3PlateNo(szPL_L2_TRK_NO,szPL_MTL_NO)});
                        ejbConn.trx("procNo2DSLineStlInfo", new Class[] {String.class, String.class }, new Object[] { getL3PlateNo(szPL_L2_TRK_NO,szPL_MTL_NO), logId });
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

                    }else if(szTemp.equals("60")){ //2025.10.14 G R/T 추가
                    	sTrackingGbn = "3P"; //G R/T 입고존 도착구분 : 입고존 도착은 '2P', 입고존 출발은 '3P' 로 함
                    	
                        EJBConnector ejbConn = new EJBConnector("default", "RcptWrkDmdSeEJB", this);

                        ejbConn.trx("procNo3DSLineStlInfo", new Class[] {String.class, String.class }, new Object[] { getL3PlateNo(szPL_L2_TRK_NO,szPL_MTL_NO), logId });
                    	
                    }else{
                        sTrackingGbn = "3O";    // A R/T 입고존 출발구분

                        EJBConnector ejbConn = new EJBConnector("default", "RcptWrkDmdSeEJB", this);
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// procNo1DSLineStlInfo argument 에 logId 항목 추가 개선
//                      ejbConn.trx("procNo1DSLineStlInfo", new Class[] {String.class }, new Object[] { getL3PlateNo(szPL_L2_TRK_NO,szPL_MTL_NO)});
                        ejbConn.trx("procNo1DSLineStlInfo", new Class[] {String.class, String.class }, new Object[] { getL3PlateNo(szPL_L2_TRK_NO,szPL_MTL_NO), logId });
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
                    }

                    //--------------------------------------------------------------
                    recEdit = JDTORecordFactory.getInstance().create();
                    recEdit.setField("STL_NO",          ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO"));
                    recEdit.setField("PL_RCPT_TRK_NO",  sTrackingGbn+YdUtils.getCurDate("yyyyMMddHHmmss"));
                    //--------------------------------------------------------------

                    intRtnVal = ydStockDao.updYdStock(recEdit,0);

                    intRtnVal = ydStockDao.update_Dm_Time(recEdit,6);

                    szMsg = "YD_STOCK[저장품] UPDATE 결과 :: [" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    //-------------------------------------------------------------------------------------------------start
                    //상단에서 아래 구문으로 msgRecord의 PL_MTL_NO 에 L3용 후판번호로 변환되어 있음으로 다시 읽어온 뒤 아래를 실행한다.
                    //msgRecord.setField("PL_MTL_NO", getL3PlateNo(szPL_L2_TRK_NO,szPL_MTL_NO));
                    szPL_MTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO");

                    rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                    recPara = JDTORecordFactory.getInstance().create();
                    recPara.setField("STL_NO", szPL_MTL_NO);
                    recPara.setField("YD_STK_LYR_MTL_STAT", "");
                    intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 3);

                    if( intRtnVal <= 0 ) {

                        //1후판에서는 낱장 단위로 R/T에 흐를 경우 BOOK-OUT될 가상버퍼에 미리 넣어 두지 못 함으로
                        //여기(입고존 출발)서 PILING 실적 처리를 해서 가상버퍼에 넣는 작업을 한다.
                        // 이후 이 낱장이 auto-pilier 에서 파일링 되면 파일링 실적처리 에서 가상버퍼에서 clear 해 준다.

                        //2013.05.21 -- 생산실적에서 파일링지시가 내려간 제품은 AutoPiler 에서 파일링 된 후 RT로 입고 됨으로
                        //              여기서 가상버퍼에 넣는 작업을 하지 않도록 한다.
                        //              Check방법 : 생산실적발생시 파일링 프로시져가 수행되면서 TB_YD_STOCK 의 SNDBK_RSN_CD 에
                        //                 PREPILING_MAX_SH 가 셋팅된다.
                        //                 SNDBK_RSN_CD 가 '1' 이상이면 AutoPilier 가 작업할 대상임으로 아래 작업을 skip 한다.
                        //              주의) 아래Query는 SNDBK_RSN_CD 가 '1' 보다 클때만 조회하는 것으로 결과 레코드 수가 0 일때
                        //                    AutoPiler 에 의해 piling 되지 않는다는 의미 이다. 이때는 RT 가상버퍼에 낱장을 삽입한다.

                        if(szTemp.equals("58")){
                            //1후판 OFF-LINE 입고일 경우는 낱장으로 입고 됨으로 무조건 RT 가상버퍼 에 삽입한다.

                            /*
                             * 1후판 PILING 실적 처리 백업
                             */
                            recPara = JDTORecordFactory.getInstance().create();
                            recPara.setField("JMS_TC_CD",       "P2YDL001");
                            recPara.setField("PL_PLATE_NO1",    szPL_MTL_NO);
                            recPara.setField("PL_GDS_PILNG_SH", "1");

                            this.procP2PillingWr(recPara);

                        } else {
                            // 그 외 1후판 입고는 Auto-piling 안 되는 경우만  RT 가상버퍼 에 삽입한다. (SNDBK_RSN_CD > 1 이면 Auto-pilier에 지시가 내려간 것 아래 쿼리에서 리턴값이 1이면 Auto-Pilier에 지시가 내려간 것
                            // 1보다 작으면 Auto-pilier 지시가 안내려 간 것이가나 Auto-Pilier 가 고장일 경우임,현재는 오토파일러가 없으므로 무조건 RT 가상버퍼삽입되도록 구현되어있음.)

                            recPara.setField("STL_NO", szPL_MTL_NO);
                            /*com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0066*/
                            /*
                                //SELECT * FROM TB_YD_STOCK
                                //WHERE  STL_NO = :V_STL_NO
                                //AND    SNDBK_RSN_CD > '1'

                                SELECT *
                                FROM TB_YD_STOCK A
                                     ,TB_YD_RULE B
                                WHERE  STL_NO = :V_STL_NO
                                AND    SNDBK_RSN_CD > '1'
                                AND    B.REPR_CD_GP = 'K00220' --오토파일러 고장여부
                                AND    B.ITEM1 = 'N' --N:정상
                             */
                            intRtnVal = commDao.select(recPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0066");

                            if(intRtnVal<1) {
                                //Auto-Pilier 에 지시가 내려가지 않은 낱장이나 내려 갔어도 Auto-Piler 가 고장인 경우는  procP2PillingWr 를 백업 처리해서 RT가상버퍼에 V_STL_NO 를 삽입한다. -> 그래야 Crane piling 이 가능하다.)
                                recPara = JDTORecordFactory.getInstance().create();
                                recPara.setField("JMS_TC_CD",       "P2YDL001");
                                recPara.setField("PL_PLATE_NO1",    szPL_MTL_NO);
                                recPara.setField("PL_GDS_PILNG_SH", "1");

                                /*
                                 * 1후판 PILING 실적 처리 백업
                                 */
                                this.procP2PillingWr(recPara);
                            }
                        }
                    }
                    //-------------------------------------------------------------------------------------------------end


                }else if("11".equals(szPL_BOOK_OUT_MOD)){ //C/R SCH(크레인 스케쥴

                    //상단에서 아래 구문으로 msgRecord의 PL_MTL_NO 에 L3용 후판번호로 변환되어 procP2BookOutReq 를 호출한다.
                    //msgRecord.setField("PL_MTL_NO", getL3PlateNo(szPL_L2_TRK_NO,szPL_MTL_NO));
                    this.procP2BookOutReq(msgRecord);

                }
            }
        } catch(Exception e) {
            szMsg = "A후판 Book-Out실적 처리 중 예외메세지 : " + e.getMessage();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);

            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
        szMsg = "A후판 Book-Out실적 (" + szMethodName + ") 완료";
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        
        return ;
    }

    /**
     *      [A] 오퍼레이션명 :OFF-LINE 입고대상재 정보실적 수신.
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     * @ejb.transaction type="RequiresNew"
     */
    public void procOffLineStlInfo(String sStlNo, String logId)throws JDTOException  {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// procOffLineStlInfo argument 에 logId 항목 추가 개선
// public void procOffLineStlInfo(String sStlNo)throws JDTOException  {
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        // 변수 선언
        String szMethodName       = "procOffLineStlInfo";
        String szMsg              = "";

        // DAO 및 UTIL 객체 생성
        YdStockDao ydStockDao     = new YdStockDao();

        JDTORecordSet rsOutRecSet   = null;
        JDTORecordSet rsGetStock1   = null;

        JDTORecord recIn            = null;
        JDTORecord recIF            = null;
        JDTORecord recGetVal        = null;

        String sBBookOutCd          = "";
        String sABookOutCd          = "";
        int intRtnVal               = 0;

        String szPL_ROUTE_NODE_NO_GROUP     = "";
        String szPL_ROUTE_NODE_TYPE_GROUP   = "";
        String Route_Loc                    = "";
        String Route_Type                   = "";
        long lnPL_TOT_ROUTE_CNT             = 0;

        double dYD_MTL_L                = 0;

        String sTempYn  = "N";

        
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// logId Empty 이면 logId 신규 생성 개선
if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "OFF-LINE 입고대상재 정보실적(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        
        try{

            rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");
            recIn = JDTORecordFactory.getInstance().create();
            recIn.setField("PLATE_NO", sStlNo);
            intRtnVal = ydStockDao.getYdStock(recIn, rsOutRecSet, 172);
            if(intRtnVal < 0){
                szMsg = "PLATECOMM[PLATE공통] Error :: STL_NO(" + sStlNo + ") [" + intRtnVal + "]" + "PARAMETER ERROR";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            } else if(intRtnVal == 0){
                szMsg = "PLATECOMM[PLATE공통] Error :: STL_NO(" + sStlNo + ") [" + intRtnVal + "]" + "DO NOT EXIST";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                return ;
            }

            rsOutRecSet.first();
            recGetVal = rsOutRecSet.getRecord();

            String sPlQaInspMtl = ydDaoUtils.paraRecChkNull(recGetVal,"PL_QA_INSP_MTL");
            if("1".equals(sPlQaInspMtl)){
                /*
                 * 품질검사재에 대해서는 58020도착시 라우팅지시를 내리지 않는다.
                 * 2014.02.13 윤재광
                 * 5820으로 입고존 변경에 따라 현재 체크로직 필요없다. 따라서 막음
                 * 2015.03.18
                 * 5840으로 입고존 변경
                 * 2015.12.21
                 */
                //return;
            }

            //제품최대길이
            dYD_MTL_L   = ydDaoUtils.paraRecChkNullDouble(recGetVal, "PL_MEA_GDS_L");
            sTempYn     = ydDaoUtils.paraRecChkNull(recGetVal,"TEMP_YN");

            sABookOutCd = ydDaoUtils.paraRecChkNull(recGetVal,"YD_BOOK_OUT_LOC");
            if(sABookOutCd.startsWith("56")) {
                sBBookOutCd = StringHelper.evl(YdCommonUtils.getY4ChgBBookOutLoc(sABookOutCd),""); //56->58
            } else if(sABookOutCd.startsWith("59")) {
                sBBookOutCd = StringHelper.evl(YdCommonUtils.getY4ChgC2BBookOutLoc(sABookOutCd),""); //59->58
            }

            if(sABookOutCd.startsWith("58")){
                sBBookOutCd = sABookOutCd;
            }else{
                if("".equals(sBBookOutCd)){
                    sBBookOutCd = sABookOutCd;
                    szMsg = "OFF-LINE 입고대상재 BOOK OUT CD 변경실패: ZONE ["+sABookOutCd+"]" ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                }

                //==============================================
                // PLATE공통 테이블에 야드위치와 BookOut위치 업데이트
                //==============================================
                recIn = JDTORecordFactory.getInstance().create();
                recIn.setField("YD_BOOK_OUT_LOC", sBBookOutCd);
                recIn.setField("PLATE_NO"       , sStlNo);
                recIn.setField("YD_STR_LOC"     , YdConstant.YD_GP_PLATE2_GDS_YARD + "FRTPA");
                intRtnVal = ydStockDao.updYdPlateCommBookOutLoc(recIn, 0);
                if(intRtnVal > 0){
                    szMsg = "PLATECOMM테이블에 PLATE_NO(" + sStlNo + ") BookOut위치 'KFRTPA' 업데이트 성공";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                } else {
                    szMsg = "PLATECOMM테이블에 PLATE_NO(" + sStlNo + ") BookOut위치 'KFRTPA' 업데이트 실채 nRet[" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                }

                //==============================================
                // 저장품테이블에  BookOut위치 업데이트
                //==============================================
                recIn = JDTORecordFactory.getInstance().create();
                recIn.setField("YD_BOOK_OUT_LOC", sBBookOutCd);
                recIn.setField("STL_NO"         , sStlNo);
                intRtnVal = ydStockDao.updYdStockBookOutLoc(recIn, 0);
                if(intRtnVal > 0){
                    szMsg = "저장품테이블에 STL_NO(" + sStlNo + ") BookOut위치  업데이트 성공";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                } else {
                    szMsg = "저장품테이블에 STL_NO(" + sStlNo + ") BookOut위치  업데이트 실채 nRet[" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                }
            }

            /* ===========================================================
             * 라우팅 지시 전 특이한 케이스의 경우 BookOut 코드를 수정한다.
             * 2016.09.29 윤재광 좌표계산식 변경에 따라 아래로직 막음
             * 2020.02.04 윤재광 D동 초단척대상 2번지로 라우팅지시 요청(노형준)
             */
            if("Y".equals(sTempYn)){// 임시항목 - 운영계 반영여부 체크
                if((sBBookOutCd.endsWith("070")||sBBookOutCd.endsWith("080"))  && dYD_MTL_L <= 6800) {
                    // D동 제품 길이가 초단척일경우 2베드 위치로 변경
                    sBBookOutCd = sBBookOutCd.substring(0,2) + "075";
                }
            }

            /*
             * 2016.07.04 윤재광
             * 오프라인 평탄도실적 불량여부 검사실적(1:불량,그 외:합격)
             * 평탄도 불합격재에 대해서 무조건 C동으로 라우팅지시
             */
            /*
             * 2017.04.28 윤재광
             * 노형준 요청사항으로 아래로직 막음 - 원 목적동에서 관리하기로 함.
             *
            String sOverallStampGrade = ydDaoUtils.paraRecChkNull(recGetVal,"OVERALL_STAMP_GRADE_PT");
            if("1".equals(sOverallStampGrade) && dYD_MTL_L <= 18600){
                sBBookOutCd = "58060";
            }

            szMsg = "sOverallStampGrade(" + sOverallStampGrade + ") ";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
             */

            /*
             * 2. 라우팅지시 전문 송신.
             */
            /*---------------------------------------------------------------
             * 1.후판Routing노드번호그룹(szPL_ROUTE_NODE_NO_GROUP)  = "58040"
             * 2.후판Routing노드Type그룹(PL_ROUTE_NODE_TYPE_GROUP)  = "00003"
             *   위 포인트를 시작으로 신규 생성한다.
             */
            szPL_ROUTE_NODE_TYPE_GROUP  = "00003";
            szPL_ROUTE_NODE_NO_GROUP    = "58040";
            lnPL_TOT_ROUTE_CNT          = 0;

            szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"00006"+"00013";
            szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP  +"58040"+sBBookOutCd;
            lnPL_TOT_ROUTE_CNT  = 3;

            //szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"0000700013";  //
            //szPL_ROUTE_NODE_NO_GROUP  = szPL_ROUTE_NODE_NO_GROUP+"56010"+sBBookOutCd; //


            szMsg = "[CT_후판RoutingLayout작업지시] YD_BOOK_OUT_LOC :: [" + sBBookOutCd + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            szMsg = "[CT_후판RoutingLayout작업지시] szPL_ROUTE_NODE_NO_GROUP :: [" + szPL_ROUTE_NODE_NO_GROUP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            szMsg = "[CT_후판RoutingLayout작업지시] szPL_ROUTE_NODE_TYPE_GROUP :: [" + szPL_ROUTE_NODE_TYPE_GROUP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            recIF = JDTORecordFactory.getInstance().create();
            /*
            PL_MPL_NO           날판번호
            PL_DIV_TRIM_GP_CD   분할코드
            PL_DIV_TRIM_GP_SEQ  1
            PL_L2_WO_SND_MD     3
            WO_SND_YN           N
            REGISTER            YDPRJ004
            REG_DDTT            SYSDATE
            */
            recIF.setField("PL_TOT_ROUTE_CNT",         ""+lnPL_TOT_ROUTE_CNT);       // 후판총Routing수
            recIF.setField("PL_ROUTE_NODE_NO_GROUP",   szPL_ROUTE_NODE_NO_GROUP);    // 후판Routing노드번호그룹
            recIF.setField("PL_ROUTE_NODE_TYPE_GROUP", szPL_ROUTE_NODE_TYPE_GROUP);  // 후판Routing노드Type그룹
            recIF.setField("PL_PLATE_NO",              sStlNo);                      // PLATE NO
            recIF.setField("PL_L2_WO_SND_MD",          "4");                         // 지시모드

            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Insert] lnPL_TOT_ROUTE_CNT :: [" + "" + lnPL_TOT_ROUTE_CNT + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Insert] szPL_ROUTE_NODE_NO_GROUP :: [" + szPL_ROUTE_NODE_NO_GROUP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Insert] szPL_ROUTE_NODE_TYPE_GROUP :: [" + szPL_ROUTE_NODE_TYPE_GROUP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Insert] szSTL_NO :: [" + sStlNo + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            //CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO) 조회
            /* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getTBCTNPLRTNGLAYOUTWO */
            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO) 조회] szSTL_NO :: [" + sStlNo + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            rsGetStock1 = JDTORecordFactory.getInstance().createRecordSet("");
            recIn     = JDTORecordFactory.getInstance().create();

            recIn.setField("PL_PLATE_NO", sStlNo);

            intRtnVal = ydStockDao.getYdStock(recIn, rsGetStock1, 194);

            if(intRtnVal < 0){
                szMsg = "CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO) SELECT Error :: [" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                //return ;
            } else if(intRtnVal == 0){

                szMsg = "CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO) NOT EXISTS 신규등록. :: [" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                intRtnVal = ydStockDao.insertTBCTCOMMON(recIF);
            } else{

                szMsg = "CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO) EXISTS UPDATE. :: [" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                intRtnVal = ydStockDao.updateTBCTCOMMON(recIF   ,0);
            }

            if(intRtnVal <= 0){
                szMsg = "CT_후판RoutingLayout작업지시 USRCTA.TB_CT_N_PLRTNGLAYOUTWO Error :: [" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            }

            szMsg = "CT_후판RoutingLayout작업지시 USRCTA.TB_CT_N_PLRTNGLAYOUTWO 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            //---------------------------------------------------------------

            //Routing Layout 재작업지시 송신
            this.procSmsSend(sStlNo ,0);

            szMsg = "Routing Layout 재작업지시 송신 :: [" + sStlNo + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
        }catch(Exception e){
            szMsg = "OFF-LINE 입고대상재 정보실적 수신   Exception Error: " + e.getMessage();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            throw new JDTOException(szMsg);
        }

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
szMsg = "OFF-LINE 입고대상재 정보실적(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        
    }// end of procOffLineStlInfo()

    /**
     *      [A] 오퍼레이션명 : 1후판 No2 DS-LINE 입고대상재 정보실적 수신 (Routing 지시 송신).
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     * @ejb.transaction type="RequiresNew"
     */
    public void procNo2DSLineStlInfo(String sStlNo, String logId)throws JDTOException  {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// procNo2DSLineStlInfo argument 에 logId 항목 추가 개선
// public void procNo2DSLineStlInfo(String sStlNo)throws JDTOException  {
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        // 변수 선언
        String szMethodName       = "procNo2DSLineStlInfo";
        String szMsg              = "";

        // DAO 및 UTIL 객체 생성
        YdStockDao ydStockDao     = new YdStockDao();

        JDTORecordSet rsOutRecSet   = null;
        JDTORecordSet rsGetStock1   = null;

        JDTORecord recIn            = null;
        JDTORecord recIF            = null;
        JDTORecord recGetVal        = null;

        String sCBookOutCd          = "";
        String sABookOutCd          = "";
        int intRtnVal               = 0;

        String szPL_ROUTE_NODE_NO_GROUP     = "";
        String szPL_ROUTE_NODE_TYPE_GROUP   = "";
        String Route_Loc                    = "";
        String Route_Type                   = "";
        long lnPL_TOT_ROUTE_CNT             = 0;
        double dYD_MTL_L                = 0;

        String sTempYn  = "N";
        
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// logId Empty 이면 logId 신규 생성 개선
if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "1후판 No2 DS-LINE 입고대상재 정보실적(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        try{

            rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");
            recIn = JDTORecordFactory.getInstance().create();
            recIn.setField("PLATE_NO", sStlNo);
            intRtnVal = ydStockDao.getYdStock(recIn, rsOutRecSet, 172);
            if(intRtnVal < 0){
                szMsg = "PLATECOMM[PLATE공통] Error :: STL_NO(" + sStlNo + ") [" + intRtnVal + "]" + "PARAMETER ERROR";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            } else if(intRtnVal == 0){
                szMsg = "PLATECOMM[PLATE공통] Error :: STL_NO(" + sStlNo + ") [" + intRtnVal + "]" + "DO NOT EXIST";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                return ;
            }

            rsOutRecSet.first();
            recGetVal = rsOutRecSet.getRecord();

            //제품최대길이
            dYD_MTL_L   = ydDaoUtils.paraRecChkNullDouble(recGetVal, "PL_MEA_GDS_L");
            sTempYn     = ydDaoUtils.paraRecChkNull(recGetVal,"TEMP_YN");

            sABookOutCd = ydDaoUtils.paraRecChkNull(recGetVal,"YD_BOOK_OUT_LOC");
            sCBookOutCd = StringHelper.evl(YdCommonUtils.getY4ChgCBookOutLoc(sABookOutCd),"");

            if(sABookOutCd.startsWith("59")){
                sCBookOutCd = sABookOutCd;
            }else{
                if("".equals(sCBookOutCd)){
                    sCBookOutCd = sABookOutCd;
                    szMsg = "1후판 No2 DS-LINE 입고대상재 BOOK OUT CD 변경실패: ZONE ["+sABookOutCd+"]" ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                }

                //==============================================
                // PLATE공통 테이블에 야드위치와 BookOut위치 업데이트
                //==============================================
                recIn = JDTORecordFactory.getInstance().create();
                recIn.setField("YD_BOOK_OUT_LOC", sCBookOutCd);
                recIn.setField("PLATE_NO"       , sStlNo);
                recIn.setField("YD_STR_LOC"     , YdConstant.YD_GP_PLATE2_GDS_YARD + "DRTPA");
                intRtnVal = ydStockDao.updYdPlateCommBookOutLoc(recIn, 0);
                if(intRtnVal > 0){
                    szMsg = "PLATECOMM테이블에 PLATE_NO(" + sStlNo + ") BookOut위치 'KDRTPA' 업데이트 성공";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                } else {
                    szMsg = "PLATECOMM테이블에 PLATE_NO(" + sStlNo + ") BookOut위치 'KDRTPA' 업데이트 실채 nRet[" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                }

                //==============================================
                // 저장품테이블에  BookOut위치 업데이트
                //==============================================
                recIn = JDTORecordFactory.getInstance().create();
                recIn.setField("YD_BOOK_OUT_LOC", sCBookOutCd);
                recIn.setField("STL_NO"         , sStlNo);
                intRtnVal = ydStockDao.updYdStockBookOutLoc(recIn, 0);
                if(intRtnVal > 0){
                    szMsg = "저장품테이블에 STL_NO(" + sStlNo + ") BookOut위치  업데이트 성공";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                } else {
                    szMsg = "저장품테이블에 STL_NO(" + sStlNo + ") BookOut위치  업데이트 실채 nRet[" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                }
            }

            /* ===========================================================
             * 라우팅 지시 전 특이한 케이스의 경우 BookOut 코드를 수정한다.
             * 2016.09.29 윤재광 좌표계산식 변경에 따라 아래로직 막음
             * 2020.02.04 윤재광 D동 초단척대상 2번지로 라우팅지시 요청(노형준)
             */
            if("Y".equals(sTempYn)){// 임시항목 - 운영계 반영여부 체크
                if((sCBookOutCd.endsWith("066")||sCBookOutCd.endsWith("068"))  && dYD_MTL_L <= 6800) {
                    // D동 제품 길이가 초단척일경우 2베드 위치로 변경
                    sCBookOutCd = sCBookOutCd.substring(0,2) + "067";
                }
            }

            /*
             * 2. 라우팅지시 전문 송신.
             */
            /*---------------------------------------------------------------
             * 1.후판Routing노드번호그룹(szPL_ROUTE_NODE_NO_GROUP)  = "59000"
             * 2.후판Routing노드Type그룹(PL_ROUTE_NODE_TYPE_GROUP)  = "00003"
             *   위 포인트를 시작으로 신규 생성한다.
             */
            szPL_ROUTE_NODE_TYPE_GROUP  = "00003";
            szPL_ROUTE_NODE_NO_GROUP    = "59020"; //2013.08.30 59000->59020 변경
            lnPL_TOT_ROUTE_CNT          = 0;

            if(sCBookOutCd.startsWith("5910")) {

                szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"0000700013";
                szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP+"59030"+sCBookOutCd;
                lnPL_TOT_ROUTE_CNT  = 3;

            } else if(sCBookOutCd.startsWith("5911")) {

                szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"0000700013";
                szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP+"59040"+sCBookOutCd;
                lnPL_TOT_ROUTE_CNT  = 3;

            } else {

                szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"00006"+"00013";
                szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP  +"59020"+sCBookOutCd; //2013.08.30 59000->59020 변경
                lnPL_TOT_ROUTE_CNT  = 3;

            }

            szMsg = "[CT_후판RoutingLayout작업지시] YD_BOOK_OUT_LOC :: [" + sCBookOutCd + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            szMsg = "[CT_후판RoutingLayout작업지시] szPL_ROUTE_NODE_NO_GROUP :: [" + szPL_ROUTE_NODE_NO_GROUP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            szMsg = "[CT_후판RoutingLayout작업지시] szPL_ROUTE_NODE_TYPE_GROUP :: [" + szPL_ROUTE_NODE_TYPE_GROUP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            recIF = JDTORecordFactory.getInstance().create();
            /*
            PL_MPL_NO           날판번호
            PL_DIV_TRIM_GP_CD   분할코드
            PL_DIV_TRIM_GP_SEQ  1
            PL_L2_WO_SND_MD     3
            WO_SND_YN           N
            REGISTER            YDPRJ004
            REG_DDTT            SYSDATE
            */
            recIF.setField("PL_TOT_ROUTE_CNT",         ""+lnPL_TOT_ROUTE_CNT);       // 후판총Routing수
            recIF.setField("PL_ROUTE_NODE_NO_GROUP",   szPL_ROUTE_NODE_NO_GROUP);    // 후판Routing노드번호그룹
            recIF.setField("PL_ROUTE_NODE_TYPE_GROUP", szPL_ROUTE_NODE_TYPE_GROUP);  // 후판Routing노드Type그룹
            recIF.setField("PL_PLATE_NO",              sStlNo);                      // PLATE NO
            recIF.setField("PL_L2_WO_SND_MD",          "4");                         // 지시모드

            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Insert] lnPL_TOT_ROUTE_CNT :: [" + "" + lnPL_TOT_ROUTE_CNT + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Insert] szPL_ROUTE_NODE_NO_GROUP :: [" + szPL_ROUTE_NODE_NO_GROUP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Insert] szPL_ROUTE_NODE_TYPE_GROUP :: [" + szPL_ROUTE_NODE_TYPE_GROUP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Insert] szSTL_NO :: [" + sStlNo + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            //CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO) 조회
            /* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getTBCTNPLRTNGLAYOUTWO */
            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO) 조회] szSTL_NO :: [" + sStlNo + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            rsGetStock1 = JDTORecordFactory.getInstance().createRecordSet("");
            recIn     = JDTORecordFactory.getInstance().create();

            recIn.setField("PL_PLATE_NO", sStlNo);

            intRtnVal = ydStockDao.getYdStock(recIn, rsGetStock1, 194);

            if(intRtnVal < 0){
                szMsg = "CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO) SELECT Error :: [" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                //return ;
            } else if(intRtnVal == 0){

                szMsg = "CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO) NOT EXISTS 신규등록. :: [" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                intRtnVal = ydStockDao.insertTBCTCOMMON(recIF);
            } else{

                szMsg = "CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO) EXISTS UPDATE. :: [" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                intRtnVal = ydStockDao.updateTBCTCOMMON(recIF   ,0);
            }

            if(intRtnVal <= 0){
                szMsg = "CT_후판RoutingLayout작업지시 USRCTA.TB_CT_N_PLRTNGLAYOUTWO Error :: [" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            }

            szMsg = "CT_후판RoutingLayout작업지시 USRCTA.TB_CT_N_PLRTNGLAYOUTWO 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            //---------------------------------------------------------------

            //Routing Layout 재작업지시 송신
            this.procSmsSend(sStlNo ,0);

            szMsg = "Routing Layout 재작업지시 송신 :: [" + sStlNo + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
        }catch(Exception e){
            szMsg = "1후판 No2 DS-LINE 입고대상재 정보실적 수신   Exception Error: " + e.getMessage();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            throw new JDTOException(szMsg);
        }

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
szMsg = "1후판 No2 DS-LINE 입고대상재 정보실적(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        
    }// end of procNo2DSLineStlInfo()


    /**
     *      [A] 오퍼레이션명 : 1후판 No1 DS-LINE 입고대상재 정보실적 수신 (Routing 지시 송신).
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     * @ejb.transaction type="RequiresNew"
     */
    public void procNo1DSLineStlInfo(String sStlNo, String logId)throws JDTOException  {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// procNo1DSLineStlInfo argument 에 logId 항목 추가 개선
// public void procNo1DSLineStlInfo(String sStlNo)throws JDTOException  {
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        // 변수 선언
        String szMethodName       = "procNo1DSLineStlInfo";
        String szMsg              = "";

        // DAO 및 UTIL 객체 생성
        YdStockDao ydStockDao     = new YdStockDao();

        JDTORecordSet rsOutRecSet   = null;
        JDTORecordSet rsGetStock1   = null;

        JDTORecord recEdit1         = null;
        JDTORecord recIn            = null;
        JDTORecord recIF            = null;
        JDTORecord recGetVal        = null;
        JDTORecord recUSRCTAEdit    = null;
        JDTORecord recGetVal1       = null;

        String sYdBookOutLoc        = "";
        String sABookOutCd          = "";
        int intRtnVal               = 0;
        int Str_Count               = 0;
        int index                   = 0;

        String sPrBookOutLoc = "";
        String szPL_ROUTE_NODE_NO_GROUP     = "";
        String szPL_ROUTE_NODE_TYPE_GROUP   = "";
        String Route_Loc                    = "";
        String Route_Type                   = "";
        long lnPL_TOT_ROUTE_CNT             = 0;
        double dYD_MTL_L                    = 0;

        String sTempYn  = "N";
        
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// logId Empty 이면 logId 신규 생성 개선
if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "1후판 No1 DS-LINE 입고대상재 정보실적(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        try{

            rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");
            recIn = JDTORecordFactory.getInstance().create();
            recIn.setField("PLATE_NO", sStlNo);
            intRtnVal = ydStockDao.getYdStock(recIn, rsOutRecSet, 172);
            if(intRtnVal < 0){
                szMsg = "PLATECOMM[PLATE공통] Error :: STL_NO(" + sStlNo + ") [" + intRtnVal + "]" + "PARAMETER ERROR";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            } else if(intRtnVal == 0){
                szMsg = "PLATECOMM[PLATE공통] Error :: STL_NO(" + sStlNo + ") [" + intRtnVal + "]" + "DO NOT EXIST";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                return ;
            }

            rsOutRecSet.first();
            recGetVal = rsOutRecSet.getRecord();

            //제품최대길이
            dYD_MTL_L   = ydDaoUtils.paraRecChkNullDouble(recGetVal, "PL_MEA_GDS_L");
            sTempYn     = ydDaoUtils.paraRecChkNull(recGetVal,"TEMP_YN");

            sABookOutCd = ydDaoUtils.paraRecChkNull(recGetVal,"YD_BOOK_OUT_LOC");
            sYdBookOutLoc = sABookOutCd;

            if(sABookOutCd.startsWith("59")){
                //59000 --> 56000 으로 변경
                sYdBookOutLoc = StringHelper.evl(YdCommonUtils.getY4ChgABookOutLoc(sABookOutCd),"");

                if("".equals(sYdBookOutLoc)){
                    sYdBookOutLoc = sABookOutCd;
                    szMsg = "1후판 No1 DS-LINE 입고대상재 BOOK OUT CD 변경실패: ZONE ["+sABookOutCd+"]" ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                }

                //==============================================
                // PLATE공통 테이블에 야드위치와 BookOut위치 업데이트
                //==============================================
                recIn = JDTORecordFactory.getInstance().create();
                recIn.setField("YD_BOOK_OUT_LOC", sYdBookOutLoc);
                recIn.setField("PLATE_NO"       , sStlNo);
                recIn.setField("YD_STR_LOC"     , YdConstant.YD_GP_PLATE2_GDS_YARD + "ERTPA");
                intRtnVal = ydStockDao.updYdPlateCommBookOutLoc(recIn, 0);
                if(intRtnVal > 0){
                    szMsg = "PLATECOMM테이블에 PLATE_NO(" + sStlNo + ") BookOut위치 'KERTPA' 업데이트 성공";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                } else {
                    szMsg = "PLATECOMM테이블에 PLATE_NO(" + sStlNo + ") BookOut위치 'KERTPA' 업데이트 실채 nRet[" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                }

                //==============================================
                // 저장품테이블에  BookOut위치 업데이트
                //==============================================
                recIn = JDTORecordFactory.getInstance().create();
                recIn.setField("YD_BOOK_OUT_LOC", sYdBookOutLoc);
                recIn.setField("STL_NO"         , sStlNo);
                intRtnVal = ydStockDao.updYdStockBookOutLoc(recIn, 0);
                if(intRtnVal > 0){
                    szMsg = "저장품테이블에 STL_NO(" + sStlNo + ") BookOut위치  업데이트 성공";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                } else {
                    szMsg = "저장품테이블에 STL_NO(" + sStlNo + ") BookOut위치  업데이트 실채 nRet[" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                }
            }

            /* ===========================================================
             * 라우팅 지시 전 특이한 케이스의 경우 BookOut 코드를 수정한다.
             * 2016.09.29 윤재광 좌표계산식 변경에 따라 아래로직 막음
             * 2020.02.04 윤재광 D동 초단척대상 2번지로 라우팅지시 요청(노형준)
             */
            if("Y".equals(sTempYn)){// 임시항목 - 운영계 반영여부 체크
                if((sYdBookOutLoc.endsWith("070")||sYdBookOutLoc.endsWith("080")) && dYD_MTL_L <= 6800) {
                    // D동  제품길이가 초단척일경우 2베드 위치로 변경
                    sYdBookOutLoc = sYdBookOutLoc.substring(0,2) + "075";
                }
            }

            /*
             * 2. 라우팅지시 전문 송신.
             */
            /*=====================================================================================
             * Louting 지시 전문 편집
             *  - 지시 없으면 INSERT
             *  - 지시 있으면 UPDATE
             * Routing 재작업지시 송신(procSmsSend)
             * 모듈명 : PlateSpecRegSeEJBBean  procSmsSend(String sPlateNo)
             =====================================================================================*/

            if(sYdBookOutLoc.startsWith("56")|| sYdBookOutLoc.startsWith("5599")){ // 가적장 BOOK OUT위치 추가.

                //CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO) 조회
                /* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getTBCTNPLRTNGLAYOUTWO */

                szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO) 조회] szSTL_NO :: [" + sStlNo + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                rsGetStock1 = JDTORecordFactory.getInstance().createRecordSet("");

                recEdit1     = JDTORecordFactory.getInstance().create();

                recEdit1.setField("PL_PLATE_NO",        sStlNo);

                /* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getTBCTNPLRTNGLAYOUTWO */
                intRtnVal = ydStockDao.getYdStock(recEdit1, rsGetStock1, 194);

                if(intRtnVal < 0){
                    szMsg = "CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO) SELECT Error :: [" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                    //return ;
                } else if(intRtnVal == 0){

                    szMsg = "CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO) NOT EXISTS 신규등록. :: [" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                    /*---------------------------------------------------------------
                     * 1.후판Routing노드번호그룹(szPL_ROUTE_NODE_NO_GROUP)  = "48500"
                     * 2.후판Routing노드Type그룹(PL_ROUTE_NODE_TYPE_GROUP)  = "00003"
                     *   위 포인트를 시작으로 신규 생성한다.
                     */
                    szPL_ROUTE_NODE_TYPE_GROUP  = "0000300006";
                    //szPL_ROUTE_NODE_NO_GROUP  = "4850048500";
                    szPL_ROUTE_NODE_NO_GROUP    = "5602056020"; //2013.08.30 DS1 라인 생산실적에서 전송하던 라운팅 지시를 56000 입고존에서 전송하도록 수정
                    lnPL_TOT_ROUTE_CNT          = 0;

                    if(sYdBookOutLoc.startsWith("561")||
                       sYdBookOutLoc.startsWith("562")||
                       sYdBookOutLoc.startsWith("5599")){
                        if(sYdBookOutLoc.startsWith("5610")){
                            szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"0000700013";
                            szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP+"56010"+sYdBookOutLoc;
                        }else if(sYdBookOutLoc.startsWith("5611")){
                            szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"0000700013";
                            szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP+"56020"+sYdBookOutLoc;
                        }else if(sYdBookOutLoc.startsWith("5620")){
                            szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"0000700013";
                            szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP+"56030"+sYdBookOutLoc;
                        }else if(sYdBookOutLoc.startsWith("5621")){
                            szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"0000700013";
                            szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP+"56040"+sYdBookOutLoc;
                        }else if(sYdBookOutLoc.startsWith("55991")){ // 가적장(3번지쪽)
                            szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"0000900013";
                            szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP+"56010"+sYdBookOutLoc;
                        }else if(sYdBookOutLoc.startsWith("55992")){ // 가적장(1번지쪽)
                            szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"0000900013";
                            szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP+"56020"+sYdBookOutLoc;
                        }
                        lnPL_TOT_ROUTE_CNT  = 3;
                    /*  F동 R/T 증설에 따라 막음
                     *  2014.04.18 윤재광
                    }else if(sYdBookOutLoc.startsWith("56089")||
                             sYdBookOutLoc.startsWith("56090")||
                             sYdBookOutLoc.startsWith("56091")||
                             sYdBookOutLoc.startsWith("56092")){

                        szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"00010"+"00004"+"00006"+"00013" ;
                        szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP+"56080"+"58080"+"58080"+ "58" + sYdBookOutLoc.substring(2, 5);
                        lnPL_TOT_ROUTE_CNT  = 6;
                    */
                    }else{
                        szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"00013";
                        szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP  +sYdBookOutLoc;
                        lnPL_TOT_ROUTE_CNT  = 3;
                    }

                    szMsg = "[CT_후판RoutingLayout작업지시] YD_BOOK_OUT_LOC :: [" + sYdBookOutLoc + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    szMsg = "[CT_후판RoutingLayout작업지시] szPL_ROUTE_NODE_NO_GROUP :: [" + szPL_ROUTE_NODE_NO_GROUP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    szMsg = "[CT_후판RoutingLayout작업지시] szPL_ROUTE_NODE_TYPE_GROUP :: [" + szPL_ROUTE_NODE_TYPE_GROUP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    recUSRCTAEdit     = JDTORecordFactory.getInstance().create();
                    /*
                    PL_MPL_NO           날판번호
                    PL_DIV_TRIM_GP_CD   분할코드
                    PL_DIV_TRIM_GP_SEQ  1
                    PL_L2_WO_SND_MD     3
                    WO_SND_YN           N
                    REGISTER            YDPRJ004
                    REG_DDTT            SYSDATE
                    */
                    recUSRCTAEdit.setField("PL_TOT_ROUTE_CNT",         ""+lnPL_TOT_ROUTE_CNT);       // 후판총Routing수
                    recUSRCTAEdit.setField("PL_ROUTE_NODE_NO_GROUP",   szPL_ROUTE_NODE_NO_GROUP);    // 후판Routing노드번호그룹
                    recUSRCTAEdit.setField("PL_ROUTE_NODE_TYPE_GROUP", szPL_ROUTE_NODE_TYPE_GROUP);  // 후판Routing노드Type그룹
                    recUSRCTAEdit.setField("PL_PLATE_NO",              sStlNo);                    // PLATE NO

                    szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Insert] lnPL_TOT_ROUTE_CNT :: [" + "" + lnPL_TOT_ROUTE_CNT + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                    szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Insert] szPL_ROUTE_NODE_NO_GROUP :: [" + szPL_ROUTE_NODE_NO_GROUP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                    szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Insert] szPL_ROUTE_NODE_TYPE_GROUP :: [" + szPL_ROUTE_NODE_TYPE_GROUP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                    szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Insert] szSTL_NO :: [" + sStlNo + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    intRtnVal = ydStockDao.insertTBCTCOMMON(recUSRCTAEdit);

                    if(intRtnVal <= 0){
                        szMsg = "CT_후판RoutingLayout작업지시 USRCTA.TB_CT_N_PLRTNGLAYOUTWO Insert Error :: [" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                        return ;
                    }

                    szMsg = "CT_후판RoutingLayout작업지시 USRCTA.TB_CT_N_PLRTNGLAYOUTWO Insert 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                    //---------------------------------------------------------------

                    //Routing Layout 재작업지시 송신
                    this.procSmsSend(sStlNo ,0);

                    szMsg = "Routing Layout 재작업지시 송신 :: [" + sStlNo + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                } else{

                    rsGetStock1.first();
                    recGetVal1 = rsGetStock1.getRecord();

                    lnPL_TOT_ROUTE_CNT          = ydDaoUtils.paraRecChkNullLong(recGetVal1,"PL_TOT_ROUTE_CNT") ;      //후판총Routing수
                    szPL_ROUTE_NODE_NO_GROUP    = ydDaoUtils.paraRecChkNull(recGetVal1,"PL_ROUTE_NODE_NO_GROUP") ;    //후판Routing노드번호그룹
                    szPL_ROUTE_NODE_TYPE_GROUP  = ydDaoUtils.paraRecChkNull(recGetVal1,"PL_ROUTE_NODE_TYPE_GROUP") ;  //후판Routing노드Type그룹

                    /*---------------------------------------------------------------
                     * 1.후판Routing노드번호그룹(szPL_ROUTE_NODE_NO_GROUP) 마지막 5자리를 짜른다
                     * 2.해당 데이타가 존재하면 수신한 Book-Out 위치와 비교한다.
                     * 3.후판Routing노드Type그룹(PL_ROUTE_NODE_TYPE_GROUP) = "00013"
                     */
                    szMsg = "[CT_후판RoutingLayout작업지시] PL_TOT_ROUTE_CNT :: [" + lnPL_TOT_ROUTE_CNT + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    szMsg = "[CT_후판RoutingLayout작업지시] PL_ROUTE_NODE_NO_GROUP :: [" + szPL_ROUTE_NODE_NO_GROUP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    int szPL_ROUTE_NODE_NO_GROUP_Length = szPL_ROUTE_NODE_NO_GROUP.length();

                    szMsg = "[CT_후판RoutingLayout작업지시] PL_ROUTE_NODE_NO_GROUP_Length :: [" + szPL_ROUTE_NODE_NO_GROUP_Length + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                    if (szPL_ROUTE_NODE_NO_GROUP_Length >= 5){  // DB에서 읽은 후판Routing노드번호그룹 데이타가 있을때만

                        Str_Count = szPL_ROUTE_NODE_NO_GROUP_Length / 5;                                  //5자리씩 자르면 Total Count

                        szMsg = "[CT_후판RoutingLayout작업지시] Str_Count :: [" + Str_Count + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                        for(int Loop_i = 0; Loop_i < Str_Count; Loop_i++){

                            Route_Loc   = szPL_ROUTE_NODE_NO_GROUP.substring(Loop_i*5,   (Loop_i+1)*5);     // 5개씩 자른 후판Routing노드번호그룹
                            Route_Type  = szPL_ROUTE_NODE_TYPE_GROUP.substring(Loop_i*5, (Loop_i+1)*5);     // 5개씩 자른 후판Routing노드Type그룹

                            szMsg = "[CT_후판RoutingLayout작업지시] Route_Loc :: [" + Route_Loc + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                            szMsg = "[CT_후판RoutingLayout작업지시] Route_Type :: [" + Route_Type + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                            if("00013".equals(Route_Type) &&
                               (Route_Loc.startsWith("56")||Route_Loc.startsWith("5599"))){

                                szMsg = "[LOUTINGLAYOUT I/F] 기존전문 최종위치만 정보변경=====";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                                sPrBookOutLoc = Route_Loc;

                                index = 0;

                                if(Route_Loc.startsWith("561")||Route_Loc.startsWith("562")||Route_Loc.startsWith("5599")){
                                    index = -1;
                                }else{
                                    index = 0;
                                }

                                if(sYdBookOutLoc.startsWith("561")||
                                   sYdBookOutLoc.startsWith("562")||
                                   sYdBookOutLoc.startsWith("5599")){
                                    if(sYdBookOutLoc.startsWith("5610")){
                                        szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP.substring(0,(Loop_i+index)*5)+"0000700013";
                                        szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP.substring(0,(Loop_i+index)*5)+"56010"+sYdBookOutLoc;
                                    }else if(sYdBookOutLoc.startsWith("5611")){
                                        szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP.substring(0,(Loop_i+index)*5)+"0000700013";
                                        szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP.substring(0,(Loop_i+index)*5)+"56020"+sYdBookOutLoc;
                                    }else if(sYdBookOutLoc.startsWith("5620")){
                                        szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP.substring(0,(Loop_i+index)*5)+"0000700013";
                                        szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP.substring(0,(Loop_i+index)*5)+"56030"+sYdBookOutLoc;
                                    }else if(sYdBookOutLoc.startsWith("5621")){
                                        szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP.substring(0,(Loop_i+index)*5)+"0000700013";
                                        szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP.substring(0,(Loop_i+index)*5)+"56040"+sYdBookOutLoc;
                                    }else if(sYdBookOutLoc.startsWith("55991")){
                                        szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP.substring(0,(Loop_i+index)*5)+"0000900013";
                                        szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP.substring(0,(Loop_i+index)*5)+"56010"+sYdBookOutLoc;
                                    }else if(sYdBookOutLoc.startsWith("55992")){
                                        szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP.substring(0,(Loop_i+index)*5)+"0000900013";
                                        szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP.substring(0,(Loop_i+index)*5)+"56020"+sYdBookOutLoc;
                                    }
                                    lnPL_TOT_ROUTE_CNT = lnPL_TOT_ROUTE_CNT + (1 + index);
                                }else{
                                    szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP.substring(0,(Loop_i+index)*5)+"00013";
                                    szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP.substring(0,(Loop_i+index)*5)+sYdBookOutLoc;
                                }
                                break;
                            }

                        } // End of for(int Loop_i = 0; Loop_i <= Str_Count; Loop_i++){

                        /*
                         * 2010.06.04 윤 재광
                         * 기존의 라우팅지시전문의 최종위치가 후판제품창고가 아닐경우.
                         * => 기존전문 무시하고 전문을 새로 만든다.(예: 열처리재..)
                         *
                         * 2011.09.31 윤 재광
                         * F동 온라인입고 ( 온라인 D동 > 오프라인 D동 > 오프라인 F동) 처리를 위해
                         * 아래의 IF문 막음(무조건 새로 생성한다.)
                         */
                        //if("".equals(sPrBookOutLoc)){
                        if(true){

                            szMsg = "[LOUTINGLAYOUT I/F] 기존전문 무시하고 신규로 전문생성=====";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                            /*---------------------------------------------------------------
                             * 1.후판Routing노드번호그룹(szPL_ROUTE_NODE_NO_GROUP)  = "48500"
                             * 2.후판Routing노드Type그룹(PL_ROUTE_NODE_TYPE_GROUP)  = "00003"
                             *   위 포인트를 시작으로 신규 생성한다.
                             */
                            szPL_ROUTE_NODE_TYPE_GROUP  = "0000300006";
                            //szPL_ROUTE_NODE_NO_GROUP  = "4850048500";
                            szPL_ROUTE_NODE_NO_GROUP    = "5602056020"; //2013.08.30 DS1 라인 생산실적에서 전송하던 라운팅 지시를 56000 입고존에서 전송하도록 수정
                            lnPL_TOT_ROUTE_CNT          = 0;

                            // 기존거
                            if(sYdBookOutLoc.startsWith("561")||
                               sYdBookOutLoc.startsWith("562")||
                               sYdBookOutLoc.startsWith("5599")){
                                if(sYdBookOutLoc.startsWith("5610")){
                                    szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"0000700013";
                                    szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP+"56010"+sYdBookOutLoc;
                                }else if(sYdBookOutLoc.startsWith("5611")){
                                    szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"0000700013";
                                    szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP+"56020"+sYdBookOutLoc;
                                }else if(sYdBookOutLoc.startsWith("5620")){
                                    szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"0000700013";
                                    szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP+"56030"+sYdBookOutLoc;
                                }else if(sYdBookOutLoc.startsWith("5621")){
                                    szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"0000700013";
                                    szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP+"56040"+sYdBookOutLoc;
                                }else if(sYdBookOutLoc.startsWith("55991")){ // 가적장(3번지쪽)
                                    szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"0000900013";
                                    szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP+"56010"+sYdBookOutLoc;
                                }else if(sYdBookOutLoc.startsWith("55992")){ // 가적장(1번지쪽)
                                    szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"0000900013";
                                    szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP+"56020"+sYdBookOutLoc;
                                }
                                lnPL_TOT_ROUTE_CNT  = 3;
                            /*  F동 R/T 증설에 따라 막음
                             *  2014.04.18 윤재광
                            }else if(sYdBookOutLoc.startsWith("56089")||
                                     sYdBookOutLoc.startsWith("56090")||
                                     sYdBookOutLoc.startsWith("56091")||
                                     sYdBookOutLoc.startsWith("56092")){

                                szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"00010"+"00004"+"00006"+"00013" ;
                                szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP+"56080"+"58080"+"58080"+ "58" + sYdBookOutLoc.substring(2, 5);
                                lnPL_TOT_ROUTE_CNT  = 6;
                            */
                            }else{
                                szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"00013";
                                szPL_ROUTE_NODE_NO_GROUP    = szPL_ROUTE_NODE_NO_GROUP  +sYdBookOutLoc;
                                lnPL_TOT_ROUTE_CNT  = 3;
                            }
                        }
                        szMsg = "[CT_후판RoutingLayout작업지시] YD_BOOK_OUT_LOC :: [" + sYdBookOutLoc + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                        szMsg = "[CT_후판RoutingLayout작업지시] szPL_ROUTE_NODE_NO_GROUP :: [" + szPL_ROUTE_NODE_NO_GROUP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                        szMsg = "[CT_후판RoutingLayout작업지시] szPL_ROUTE_NODE_TYPE_GROUP :: [" + szPL_ROUTE_NODE_TYPE_GROUP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                      ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                        //if(!sYdBookOutLoc.startsWith("56089")&&
                        //   !sYdBookOutLoc.startsWith("56090")&&
                        //   !sYdBookOutLoc.startsWith("56091")&&
                        //   !sYdBookOutLoc.startsWith("56092")&&
                        //    sYdBookOutLoc.equals(sPrBookOutLoc)){
                        //  szMsg = "Routing Layout 재작업지시 송신안함. :: [" + sYdBookOutLoc + "]";
                        //  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        //}else{
                            //---------------------------------------------------------------
                            // CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Update
                            /* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updTBCTNPLRTNGLAYOUTWO */
                            // PL_TOT_ROUTE_CNT             NUMBER(4)       후판총Routing수
                            // PL_ROUTE_NODE_NO_GROUP       VARCHAR2(100)   후판Routing노드번호그룹
                            // PL_ROUTE_NODE_TYPE_GROUP     VARCHAR2(100)   후판Routing노드Type그룹

                            recUSRCTAEdit     = JDTORecordFactory.getInstance().create();

                            recUSRCTAEdit.setField("PL_TOT_ROUTE_CNT",         ""+lnPL_TOT_ROUTE_CNT);       // 후판총Routing수
                            recUSRCTAEdit.setField("PL_ROUTE_NODE_NO_GROUP",   szPL_ROUTE_NODE_NO_GROUP);    // 후판Routing노드번호그룹
                            recUSRCTAEdit.setField("PL_ROUTE_NODE_TYPE_GROUP", szPL_ROUTE_NODE_TYPE_GROUP);  // 후판Routing노드Type그룹
                            recUSRCTAEdit.setField("PL_PLATE_NO",              sStlNo);                    // PLATE NO
                            recUSRCTAEdit.setField("PL_L2_WO_SND_MD",          "4");                         // 지시모드

                            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Update] lnPL_TOT_ROUTE_CNT :: [" + "" + lnPL_TOT_ROUTE_CNT + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Update] szPL_ROUTE_NODE_NO_GROUP :: [" + szPL_ROUTE_NODE_NO_GROUP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Update] szPL_ROUTE_NODE_TYPE_GROUP :: [" + szPL_ROUTE_NODE_TYPE_GROUP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Update] szSTL_NO :: [" + sStlNo + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                            intRtnVal = ydStockDao.updateTBCTCOMMON(recUSRCTAEdit   ,0);

                            if(intRtnVal <= 0){
                                szMsg = "CT_후판RoutingLayout작업지시 USRCTA.TB_CT_N_PLRTNGLAYOUTWO UPDATE Error :: [" + intRtnVal + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                                return ;
                            }

                            szMsg = "CT_후판RoutingLayout작업지시 USRCTA.TB_CT_N_PLRTNGLAYOUTWO UPDATE 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                            //---------------------------------------------------------------

                            //Routing Layout 재작업지시 송신
                            this.procSmsSend(sStlNo ,0);

                            szMsg = "Routing Layout 재작업지시 송신 :: [" + sStlNo + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//                          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                        //}
                    } // End of if (szPL_ROUTE_NODE_NO_GROUP.length() >= 5){
                } // End of if(intRtnVal < 0){
                //---------------------------------------------------------------
            } // End of if(szYD_BOOK_OUT_LOC1 != null){







        }catch(Exception e){
            szMsg = "1후판 No2 DS-LINE 입고대상재 정보실적 수신   Exception Error: " + e.getMessage();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            throw new JDTOException(szMsg);
        }


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
szMsg = "1후판 No1 DS-LINE 입고대상재 정보실적(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
        
    }// end of procNo1DSLineStlInfo()


	/**
     *      [A] 오퍼레이션명 : 1후판 No3 G R/T 입고대상재 정보실적 수신 (Routing 지시 송신).
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     * @ejb.transaction type="RequiresNew"
     */
    public void procNo3DSLineStlInfo(String sStlNo, String logId)throws JDTOException  {

        // 변수 선언
        String szMethodName       = "procNo3DSLineStlInfo";
        String szMsg              = "";

        // DAO 및 UTIL 객체 생성
        YdStockDao ydStockDao     = new YdStockDao();

        JDTORecordSet rsOutRecSet   = null;
        JDTORecordSet rsGetStock1   = null;

        JDTORecord recIn            = null;
        JDTORecord recIF            = null;
        JDTORecord recGetVal        = null;

        String sGBookOutCd          = "";
		
        int intRtnVal               = 0;

        String szPL_ROUTE_NODE_NO_GROUP     = "";
        String szPL_ROUTE_NODE_TYPE_GROUP   = "";
		
        long lnPL_TOT_ROUTE_CNT             = 0;
		
        String szYD_RCPT_PLN_STR_LOC 	= "";
    	String szYD_MTL_L               = ""; 
		int iYD_MTL_L         			= 0;

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		szMsg = "1후판 No3 DS-LINE 입고대상재 정보실적(" + szMethodName + ") 시작";
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
		
        try{

            rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");
            recIn = JDTORecordFactory.getInstance().create();
            recIn.setField("PLATE_NO", sStlNo);
            intRtnVal = ydStockDao.getYdStock(recIn, rsOutRecSet, 172);
			
            if(intRtnVal < 0){
                szMsg = "PLATECOMM[PLATE공통] Error :: STL_NO(" + sStlNo + ") [" + intRtnVal + "]" + "PARAMETER ERROR";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            } else if(intRtnVal == 0){
                szMsg = "PLATECOMM[PLATE공통] Error :: STL_NO(" + sStlNo + ") [" + intRtnVal + "]" + "DO NOT EXIST";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                return ;
            }

            rsOutRecSet.first();
            recGetVal = rsOutRecSet.getRecord();

			//60013으로 받음.
            sGBookOutCd = "60013";

			//==============================================
			// PLATE공통 테이블에 야드위치와 BookOut위치 업데이트
			//==============================================
			recIn = JDTORecordFactory.getInstance().create();
			recIn.setField("YD_BOOK_OUT_LOC", sGBookOutCd);
			recIn.setField("PLATE_NO"       , sStlNo);
			recIn.setField("YD_STR_LOC"     , YdConstant.YD_GP_PLATE2_GDS_YARD + "GRTPA");
			/** 
			UPDATE TB_PT_PLATECOMM
				SET YD_BOOK_OUT_LOC = ?     
				, YD_STR_LOC = ?
				,MODIFIER = 'BookOutLoc' 
				,MOD_DDTT = SYSDATE 
				WHERE PLATE_NO = ?				
			**/
			intRtnVal = ydStockDao.updYdPlateCommBookOutLoc(recIn, 0);
			if(intRtnVal > 0){
				szMsg = "PLATECOMM테이블에 PLATE_NO(" + sStlNo + ") BookOut위치 'TGRTPA' 업데이트 성공";
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
			} else {
				szMsg = "PLATECOMM테이블에 PLATE_NO(" + sStlNo + ") BookOut위치 'TGRTPA' 업데이트 실채 nRet[" + intRtnVal + "]";
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			}

			//==============================================
			// 저장품테이블에  BookOut위치 업데이트
			//==============================================
			recIn = JDTORecordFactory.getInstance().create();
			recIn.setField("YD_BOOK_OUT_LOC", sGBookOutCd);
			recIn.setField("STL_NO"         , sStlNo);
			/** 
			UPDATE TB_YD_STOCK
			SET YD_BOOK_OUT_LOC = :YD_BOOK_OUT_LOC
			   ,MODIFIER = 'BookOutLoc' 
				  ,MOD_DDTT = SYSDATE 
			WHERE STL_NO = :STL_NO
			**/
			intRtnVal = ydStockDao.updYdStockBookOutLoc(recIn, 0);
			
			if(intRtnVal > 0){
				szMsg = "저장품테이블에 STL_NO(" + sStlNo + ") BookOut위치  업데이트 성공";
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
			} else {
				szMsg = "저장품테이블에 STL_NO(" + sStlNo + ") BookOut위치  업데이트 실채 nRet[" + intRtnVal + "]";
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			}

            /*
             * 2. 라우팅지시 전문 송신.
             */
            /*---------------------------------------------------------------
             * 1.후판Routing노드번호그룹(szPL_ROUTE_NODE_NO_GROUP)  = "60012"
             * 2.후판Routing노드Type그룹(PL_ROUTE_NODE_TYPE_GROUP)  = "00003"
             *   위 포인트를 시작으로 신규 생성한다.
             */
            szPL_ROUTE_NODE_TYPE_GROUP  = "00003"; //확인필요
            szPL_ROUTE_NODE_NO_GROUP    = "60012"; // 1후판정정에서 SCPL 마지막 노드
			
			szPL_ROUTE_NODE_TYPE_GROUP  = szPL_ROUTE_NODE_TYPE_GROUP+"00006"+"00013"; //확인필요
			szPL_ROUTE_NODE_NO_GROUP    = "60008"+szPL_ROUTE_NODE_NO_GROUP  +"60012"+sGBookOutCd; //60008 60012 60012 60013
			lnPL_TOT_ROUTE_CNT  = 3;


            szMsg = "[CT_후판RoutingLayout작업지시] YD_BOOK_OUT_LOC :: [" + sGBookOutCd + "]";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            szMsg = "[CT_후판RoutingLayout작업지시] szPL_ROUTE_NODE_NO_GROUP :: [" + szPL_ROUTE_NODE_NO_GROUP + "]";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            szMsg = "[CT_후판RoutingLayout작업지시] szPL_ROUTE_NODE_TYPE_GROUP :: [" + szPL_ROUTE_NODE_TYPE_GROUP + "]";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            recIF = JDTORecordFactory.getInstance().create();
            /*
            PL_MPL_NO           날판번호
            PL_DIV_TRIM_GP_CD   분할코드
            PL_DIV_TRIM_GP_SEQ  1
            PL_L2_WO_SND_MD     4
            WO_SND_YN           N
            REGISTER            YDPRJ004
            REG_DDTT            SYSDATE
            */
            recIF.setField("PL_TOT_ROUTE_CNT",         ""+lnPL_TOT_ROUTE_CNT);       // 후판총Routing수
            recIF.setField("PL_ROUTE_NODE_NO_GROUP",   szPL_ROUTE_NODE_NO_GROUP);    // 후판Routing노드번호그룹
            recIF.setField("PL_ROUTE_NODE_TYPE_GROUP", szPL_ROUTE_NODE_TYPE_GROUP);  // 후판Routing노드Type그룹
            recIF.setField("PL_PLATE_NO",              sStlNo);                      // PLATE NO
            recIF.setField("PL_L2_WO_SND_MD",          "4");                         // 지시모드

            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Insert] lnPL_TOT_ROUTE_CNT :: [" + "" + lnPL_TOT_ROUTE_CNT + "]";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
			
            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Insert] szPL_ROUTE_NODE_NO_GROUP :: [" + szPL_ROUTE_NODE_NO_GROUP + "]";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
			
            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Insert] szPL_ROUTE_NODE_TYPE_GROUP :: [" + szPL_ROUTE_NODE_TYPE_GROUP + "]";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
			
            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Insert] szSTL_NO :: [" + sStlNo + "]";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            szMsg = "[CT_후판RoutingLayout작업지시 (USRCTA.TB_CT_N_PLRTNGLAYOUTWO) 조회] szSTL_NO :: [" + sStlNo + "]";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
			
            rsGetStock1 = JDTORecordFactory.getInstance().createRecordSet("");
            recIn     = JDTORecordFactory.getInstance().create();

            recIn.setField("PL_PLATE_NO", sStlNo);

            intRtnVal = ydStockDao.getYdStock(recIn, rsGetStock1, 194);

            if(intRtnVal < 0){
                szMsg = "CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO) SELECT Error :: [" + intRtnVal + "]";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            } else if(intRtnVal == 0){
                szMsg = "CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO) NOT EXISTS 신규등록. :: [" + intRtnVal + "]";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                intRtnVal = ydStockDao.insertTBCTCOMMON(recIF);
            } else{
                szMsg = "CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO) EXISTS UPDATE. :: [" + intRtnVal + "]";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                intRtnVal = ydStockDao.updateTBCTCOMMON(recIF   ,0);
            }

            if(intRtnVal <= 0){
                szMsg = "CT_후판RoutingLayout작업지시 USRCTA.TB_CT_N_PLRTNGLAYOUTWO Error :: [" + intRtnVal + "]";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return ;
            }

            szMsg = "CT_후판RoutingLayout작업지시 USRCTA.TB_CT_N_PLRTNGLAYOUTWO 완료";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            
            /**
             * 20251113 추관식
             * ==============================================
             * 재료입고예정위치가 B동이고 길이가 14000이상 초과시
             *  -> Rounting 지시 송신
             * 아닐시
             *  -> Rounting 지시 송신 하지 않음
             * ==============================================
             * **/
            rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");
            recIn = JDTORecordFactory.getInstance().create();
			recIn.setField("STL_NO"         , sStlNo);
            intRtnVal = ydStockDao.getYdStock(recIn, rsOutRecSet, 0);
            if(intRtnVal <= 0){
                szMsg = "CT_후판RoutingLayout작업지시(TB_YD_STOCK) SELECT Error :: [" + intRtnVal + "]";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return;
            } else {
                rsOutRecSet.first();
                recGetVal = rsOutRecSet.getRecord();
                
        		szYD_RCPT_PLN_STR_LOC = ydDaoUtils.paraRecChkNull(recGetVal, "YD_RCPT_PLN_STR_LOC");	// 입고예정위치
        		szYD_MTL_L = ydDaoUtils.paraRecChkNull(recGetVal, "YD_MTL_L");							// 제품길이
        		if( szYD_MTL_L.trim().equals("") ) szYD_MTL_L = "0";
        		iYD_MTL_L		= Integer.parseInt(szYD_MTL_L);
        		
        		if(szYD_RCPT_PLN_STR_LOC.startsWith("TB") && iYD_MTL_L > 14000) {
                    //Routing Layout 재작업지시 송신
                    this.procSmsSend(sStlNo ,0);

                    szMsg = "Routing Layout 재작업지시 송신 :: [" + sStlNo + "]";
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);        			
        		} else {
                    szMsg = "Routing Layout 재작업지시 송신 불가 :: ["+sStlNo+"] 입고예정위치 :: ["+szYD_RCPT_PLN_STR_LOC+"] 제품길이 ::["+szYD_MTL_L+"]";
                    ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
        		}
            }
        }catch(Exception e){
            szMsg = "1후판 No3 DS-LINE 입고대상재 정보실적 수신   Exception Error: " + e.getMessage();
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            throw new JDTOException(szMsg);
        }

		szMsg = "1후판 No3 DS-LINE 입고대상재 정보실적(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
        
    }// end of procNo3DSLineStlInfo()
    
    /**
     *      [A] 오퍼레이션명 :Routing Layout 재작업지시 송신
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procSmsSend(String sPlateNo,int intGbn)throws JDTOException  {
        YdDelegate ydDelegate = new YdDelegate();
        // DAO 및 UTIL 객체 생성
        YmEtcDao ydStockDao     = new YmEtcDao();

        // 레코드 선언
        JDTORecordSet rsOutRecSet = null;
        JDTORecord recIn          = null;
        JDTORecord recGetVal      = null;

        // 변수 선언
        String szMethodName       = "procSmsSend";
        String szMsg              = "";
        String szOperationName    = "SMS L2 재작업지시";
        int intRtnVal             = 0;

        try{

            // 레코드 생성
            rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");

            recIn = JDTORecordFactory.getInstance().create();
            recIn.setField("PL_PLATE_NO", sPlateNo);
            intRtnVal = ydStockDao.getYmEtcDao(recIn, rsOutRecSet, intGbn);

            if(intRtnVal < 0){
                szMsg = "PLATECOMM[PLATE작업지시] Error :: STL_NO(" + sPlateNo + ") [" + intRtnVal + "]" + "PARAMETER ERROR";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            } else if(intRtnVal == 0){
                szMsg = "PLATECOMM[PLATE작업지시] Error :: STL_NO(" + sPlateNo + ") [" + intRtnVal + "]" + "DO NOT EXIST";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                return ;
            }

            rsOutRecSet.first();
            recGetVal = rsOutRecSet.getRecord();

            szMsg = "==================== 수신전문 출력 시작 ====================";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.displayRecord(szOperationName, recGetVal);
            szMsg = "==================== 수신전문 출력 종료 ====================";

            if(intGbn == 0){
                String sMessage = recGetVal.getFieldString("TL3CRL");
                String sRetVal = ydDelegate.sndSms(sMessage,"PRP2L008");

                /*
                 * L2전단서버 분리에 따른 라우팅 재지시
                 */
                sMessage = "0035729108"+sMessage.substring(10);
                sRetVal  = ydDelegate.sndSms(sMessage,"PRP2L008");

                szMsg = "ROUTING LAYOUT 재작업지시  처리(" + szMethodName + ") 완료["+sRetVal+"]";
            }else if(intGbn == 1){
                String sMessage = recGetVal.getFieldString("TL3CP2");
                String sRetVal = ydDelegate.sndSms(sMessage,"PRP2L010");
                szMsg = "PRODUCTION INFOMATION 2 재작업지시  처리(" + szMethodName + ") 완료["+sRetVal+"]";
            }
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }catch(Exception e){
            szMsg = "SMSD 재작업지시   Exception Error: " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }
    }// end of procSmsSend()

    /**
     * 오퍼레이션명 : 스케줄 우선순위 추가
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     *
     * @param   String szStlNo 재료번호
     * @return boolean true(스케줄 우선순위 추가 성공), false(스케줄 우선순위항목 추가 실패)
     * @throws JDTOException
     */
    public boolean addYdSchPrior(JDTORecord recMsg)throws JDTOException  {
        //메세지
        String szMsg              = null;
        //메소드명
        String szMethodName       = "addYdSchPrior";
        String szOperationName    = "스케줄 우선순위 추가 ";

        //Return Value
        boolean blnRtnVal         = false;

        //DAO 객체 생성
        YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

        //레코드 생성
        JDTORecord recPara  = JDTORecordFactory.getInstance().create();
        JDTORecord recTemp  = JDTORecordFactory.getInstance().create();

        //레코드셋 생성
        JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");


        try {

            recPara.setField("YD_SCH_CD", recMsg.getField("YD_SCH_CD"));

            ydSchRuleDao.getYdSchrule(recPara, rsResult, 0);

            // WBOOK : YD_SCH_PRIOR  <----    SCH_RULE :YD_WRK_CRN_PRIOR
            //Key 조회 이므로 실제적으로 한번만 실행된다.
            rsResult.beforeFirst();
            while(rsResult.next())
            {

                recTemp = rsResult.getRecord();
                recMsg.setField("YD_SCH_PRIOR", recTemp.getField("YD_WRK_CRN_PRIOR"));

                ydUtils.displayRecord(szOperationName, recMsg);
                blnRtnVal = true;
            }

        } catch(Exception e) {
            blnRtnVal = false;
            szMsg = "작업 예약 정보에 야드 스케줄 추가 중 예외메세지 : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return blnRtnVal;
        }
        return blnRtnVal;

    } //end of addYdSchPrior


    /**
     * 오퍼레이션명 : 후판압연전단L2 재열재Take-Out 요구 (H1YDL002) [권오창 2009.08.27]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  msgRecord
     * @return
     * @throws JDTOException
     */
    public void procR2ReHeatTakeOutReq(JDTORecord msgRecord)throws JDTOException  {
        // 레코드 선언
        JDTORecord recPara        = null;
        JDTORecordSet rsResult    = null;

        // 객체생성
        YdDelegate ydDelegate     = new YdDelegate();

        // 메소드명
        String szMethodName       = "procR2ReHeatTakeOutReq";

        // 메세지
        String szMsg              = "";

        String szYD_EQP_ID        = "";
        String szYD_STK_BED_NO    = "";
        String szSTL_NO           = "";

        // TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode == null) {
            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }


        try {
            //=============================================================
            // Log 테이블 등록
            //=============================================================
            szMsg = "[C열연압연L2] 재열재Take-Out 요구 수신";
            ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

            // 레코드 및 레코드셋 생성
            recPara  = JDTORecordFactory.getInstance().create();
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");

            // 수신전문 항목 추출
            szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
            szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
            szSTL_NO =  ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO");

            // 델리게이트 호출을 위한 레코드 편집
            recPara.setField("JMS_TC_CD", "YDH1L002");  // TC-CODE
            recPara.setField("YD_GP"    , szSTL_NO);    // 재료번호

            // 델리게이트 호출
            ydDelegate.sendMsg(recPara);
        } catch(Exception e) {
            szMsg = "재열재 Take-Out 요구 처리 중 예외메세지 : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }
        return ;
    } //end of procR2ReHeatTakeOutReq


    private String getL3PlateNo(String sPlL2TrkNo,
                                String sPlPlateNo)throws DAOException{

        // DAO 및 UTIL 객체 선언
        YdStockDao ydStockDao        = new YdStockDao();

        // 레코드 선언
        JDTORecordSet rsGetStock     = null;
        JDTORecord recEdit           = null;
        JDTORecord recGetVal         = null;

        String sReturnVal   = "";
        String szMsg        = "";
        String szMethodName = "getL3PlateNo";
        int intRtnVal       = 0;

        String sPlDivTrimGpCd = "";
        try {
            /*-----------------------------------------------------------------------------------------------------
             *  2010.03.05 이영근
             *  수신한 전문에 후판재료번호는 날판번호여서
             *  후판날판번호(PL_MTL_NO) + 후판L2제품번호(szPL_L2_TRK_NO : 2010030401060001 - 13번째부터 4 Byte)로 후판번호를 추출할것
             *  com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getTBPRPLATEMAT
             *  select PL_PLATE_NO
             *    from USRPRA.TB_PR_PLATE_MAT
             *   where PL_MPL_NO         = :V_PL_MPL_NO
             *     and PL_DIV_TRIM_GP_CD = :V_PL_DIV_TRIM_GP_CD
             */

            if(sPlL2TrkNo.length() == 16){
                sPlDivTrimGpCd = sPlL2TrkNo.substring(12, 16);
            }else{
                szMsg = "후판L2제품번호(PL_L2_TRK_NO) 값이 없으므로 후판번호 추출 불가함 .";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return sPlPlateNo;
            }

            szMsg = "[A후판 Book-Out실적] 날판번호 :: [" + sPlPlateNo + "]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[A후판 Book-Out실적] 후판분할절단구분코드 :: [" + sPlDivTrimGpCd + "]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            rsGetStock  = JDTORecordFactory.getInstance().createRecordSet("");
            recEdit     = JDTORecordFactory.getInstance().create();

            recEdit.setField("PL_MPL_NO",         sPlPlateNo);      //후판날판번호
            recEdit.setField("PL_DIV_TRIM_GP_CD", sPlDivTrimGpCd);  //후판분할절단구분코드

            intRtnVal = ydStockDao.getYdStock(recEdit, rsGetStock, 199);

            if(intRtnVal < 0){
                szMsg = "A후판 Book-Out실적(USRPRA.TB_PR_PLATE_MAT) SELECT Error :: [" + intRtnVal + "]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            } else if(intRtnVal == 0){
                szMsg = "A후판 Book-Out실적(USRPRA.TB_PR_PLATE_MAT) SELECT Error :: [" + intRtnVal + "]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            } else{
                rsGetStock.first();
                recGetVal = rsGetStock.getRecord();

                sPlPlateNo  = ydDaoUtils.paraRecChkNull(recGetVal, "PL_PLATE_NO") ;      //후판번호

                szMsg = "A후판 Book-Out실적(USRPRA.TB_PR_PLATE_MAT) SELECT 완료  PL_PLATE_NO :: [" + sPlPlateNo + "]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            }
        } catch(Exception e) {
            szMsg = "L3 Plate번호 추출  중 예외메세지 : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new DAOException(szMsg);
        }
        /*-----------------------------------------------------------------------------------------------------*/
        return sPlPlateNo;
    }

    /**
     * 후판 바코드정보 수신(Y6YDL101/Y6YDL201)
     * > 2013.06.12 윤재광
     *   - 스캐너 설비로 교체 해당 I/F 변경
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @throws JDTOException
     */
    public void procAPlBarCodeInfo(JDTORecord inRecord)throws JDTOException  {

        String szMethodName = "procAPlBarCodeInfo";
        String szMsg = "";

        JDTORecord recPara  = JDTORecordFactory.getInstance().create();
        YdEqpDao dao        = new YdEqpDao();

        //전문받아서 szRcvTcCode에 저장
        String szRcvTcCode=ydUtils.getTcCode(inRecord);

        //수신한 전문이 null이라면 error
        if(szRcvTcCode==null){
            szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return ;
        }

        try{
            /*
                RCPT_ZONE_GP    입고존 구분 CHAR    1       'A':2후판 #2DS Line [TSRTRA]
                                                            'B':2후판 #1DS Line [TSRTRB]
                                                            'C':2후판 Off-line  [TSRTRC]
                                                            'D':1후판 #2DS Line [KSRTRC] -> KSRTRD
                                                            'E':1후판 #1DS Line [KSRTRA] -> KSRTRE
                                                            'F':1후판 Off-line  [KSRTRB] -> KSRTRF
                MTL_SH          재료매수        CHAR    2       01~06
                PLATE_ID11      PLATE_ID11  CHAR    10      1단 측면 제품번호
                PLATE_ID12      PLATE_ID12  CHAR    10      1단 상면 제품번호
                PLATE_ID21      PLATE_ID11  CHAR    10      2단 측면 제품번호
                PLATE_ID22      PLATE_ID12  CHAR    10      2단 상면 제품번호
                PLATE_ID31      PLATE_ID11  CHAR    10      3단 측면 제품번호
                PLATE_ID32      PLATE_ID12  CHAR    10      3단 상면 제품번호
                PLATE_ID41      PLATE_ID11  CHAR    10      4단 측면 제품번호
                PLATE_ID42      PLATE_ID12  CHAR    10      4단 상면 제품번호
                PLATE_ID51      PLATE_ID11  CHAR    10      5단 측면 제품번호
                PLATE_ID52      PLATE_ID12  CHAR    10      5단 상면 제품번호
             */
            String sEqpId   = "";
            // 입고존 구분
            String szRcptZoneGp = ydDaoUtils.paraRecChkNull(inRecord, "RCPT_ZONE_GP");

            if("".equals(szRcptZoneGp)){
                szMsg="[후판 스캐너정보 수신 ERROR] "+szSessionName+"::"+szMethodName+"() RCPT_ZONE_GP Error ("+szRcptZoneGp+")";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            if("A".equals(szRcptZoneGp)){       sEqpId = "TSRTRA";
            }else if("B".equals(szRcptZoneGp)){ sEqpId = "TSRTRB";
            }else if("C".equals(szRcptZoneGp)){ sEqpId = "TSRTRC";
            }else if("D".equals(szRcptZoneGp)){ sEqpId = "KSRTRD";
            }else if("E".equals(szRcptZoneGp)){ sEqpId = "KSRTRE";
            }else if("F".equals(szRcptZoneGp)){ sEqpId = "KSRTRF";
            }

            if("".equals(sEqpId)){
                szMsg="[후판 스캐너정보 수신 ERROR] "+szSessionName+"::"+szMethodName+"() sEqpId Error ("+sEqpId+")";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
            }

            String szPLATE_ID11 = ydDaoUtils.paraRecChkNull(inRecord, "PLATE_ID11");
            String szPLATE_ID12 = ydDaoUtils.paraRecChkNull(inRecord, "PLATE_ID12");
            String szPLATE_ID21 = ydDaoUtils.paraRecChkNull(inRecord, "PLATE_ID21");
            String szPLATE_ID22 = ydDaoUtils.paraRecChkNull(inRecord, "PLATE_ID22");
            String szPLATE_ID31 = ydDaoUtils.paraRecChkNull(inRecord, "PLATE_ID31");
            String szPLATE_ID32 = ydDaoUtils.paraRecChkNull(inRecord, "PLATE_ID32");
            String szPLATE_ID41 = ydDaoUtils.paraRecChkNull(inRecord, "PLATE_ID41");
            String szPLATE_ID42 = ydDaoUtils.paraRecChkNull(inRecord, "PLATE_ID42");
            String szPLATE_ID51 = ydDaoUtils.paraRecChkNull(inRecord, "PLATE_ID51");
            String szPLATE_ID52 = ydDaoUtils.paraRecChkNull(inRecord, "PLATE_ID52");

            szMsg="[후판 스캐너정보 수신] "+szSessionName+"::"+szMethodName+"() ("+sEqpId+")("+szPLATE_ID11+") ("+szPLATE_ID12+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            recPara.setField("YD_EQP_ID",         sEqpId);
            recPara.setField("PLATE_ID11",        szPLATE_ID11);
            recPara.setField("PLATE_ID12",        szPLATE_ID12);
            recPara.setField("PLATE_ID21",        szPLATE_ID21);
            recPara.setField("PLATE_ID22",        szPLATE_ID22);
            recPara.setField("PLATE_ID31",        szPLATE_ID31);
            recPara.setField("PLATE_ID32",        szPLATE_ID32);
            recPara.setField("PLATE_ID41",        szPLATE_ID41);
            recPara.setField("PLATE_ID42",        szPLATE_ID42);
            recPara.setField("PLATE_ID51",        szPLATE_ID51);
            recPara.setField("PLATE_ID52",        szPLATE_ID52);

            /* com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.updYdEqpBarCodeInfo */
            dao.updYdEqpDirect(recPara, 3);

        }catch(Exception e){
            szMsg="[후판 스캐너정보 수신]Exception Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);

        } // end of try-catch

    } // end of procAPlBarCodeInfo()


    /**
     * 후판정정야드 BOOK OUT 정보 수신처리
     *
     * @param inRecord
     * @throws JDTOException
     */
    private void procPlateJJYardInfo(String sPlL2TrkNo,
                                     String sPlMtlNo,
                                     String sPlTrckZoneNo,
                                     String splBookOutRsnJJYD)throws JDTOException  {

        String szMethodName = "procPlateJJYardInfo";
        String szMsg        = "";

        String sStlNo       = "";
        String sYdLocGp1    = "";
        String sYdLocGp2    = "";

        com.inisteel.cim.yd.jjyd.dao.PlateReviseDao dao
        = new com.inisteel.cim.yd.jjyd.dao.PlateReviseDao();

        com.inisteel.cim.pr.oprnmgmt.milloprnmgmt.dao.PRPlMtlProgDAO prDao
        = new com.inisteel.cim.pr.oprnmgmt.milloprnmgmt.dao.PRPlMtlProgDAO();

        JDTORecord recPara  = null;

        try{

            /*
             * 1. 재료정보 L3정보로 전환 - 후판조업 메소드 호출.
             */
            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("PL_MTL_NO",     sPlMtlNo);
            recPara.setField("PL_L2_TRK_NO",  sPlL2TrkNo);

            sStlNo = prDao.getMtlNo(recPara);

            szMsg="[후판정정야드 BOOK OUT 정보] STL_NO :" +sStlNo;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            /*
             * 2. 트래킹 존으로 운전실 정보 가져오기.
             */
            /*
            public static final String YD_PPLATE_LOC_GP_US              = "UST";
            public static final String YD_PPLATE_LOC_GP_CS              = "C/S";
            public static final String YD_PPLATE_LOC_GP_DSS             = "DSS";
            public static final String YD_PPLATE_LOC_GP_DS              = "D/S";
            public static final String YD_PPLATE_LOC_GP_HMD             = "열간교정운전실";
            public static final String YD_PPLATE_LOC_GP_CMD             = "냉간교정운전실";
            public static final String YD_PPLATE_LOC_GP_GT              = "정정분기";
            public static final String YD_PPLATE_LOC_GP_WT              = "열처리";
            public static final String YD_PPLATE_LOC_GP_SB              = "Shot blast";
            public static final String YD_PPLATE_LOC_GP_CR              = "검사실";

            열간교정운전실  33000 33005
                        35000 35005 35010 35015
                        36000 36005 36010 36015
            UST         42000 42005 42010
            C/S         42005 42010 42015 42020
                        42500
                        42900 42905
            DSS         45000 45005 45010 45015
                        45900 45905 45910
            D/S         48000 48005 48010
                        48505
                        48900 48905 48910 48915
            검사실      48920 48925 48930 48935
                        48981 48991
                        53000 53005 53010 53015
                        53500 53510 53520 53530
            정정분기        53000 53005 53010 53015
                        53500 53505 53510 53515 53520 53525 53530 53535
                        53900 53905 53910 53915
                        54000 54005 54010 54015 54020
                        54500 54505 54510 54515 54520 54525 54530 54535
                        54900 54905 54910 54915
            Shot blast  54900 54905 54910 54915
            냉간교정운전실 57000 57005
                        57900 57905 57910 57915
            열처리              Book-Out처리 수작업

            UST/CS              42005 42010
            검사실/정정분기     53000 53005 53010 53015
                                53500 53510 53520 53530
            정정분기/Shot blast     54900 54905 54910 54915
            */
                  if("33000".equals(sPlTrckZoneNo)||
                     "33005".equals(sPlTrckZoneNo)||
                     "35000".equals(sPlTrckZoneNo)||
                     "35005".equals(sPlTrckZoneNo)||
                     "35010".equals(sPlTrckZoneNo)||
                     "35015".equals(sPlTrckZoneNo)||
                     "36000".equals(sPlTrckZoneNo)||
                     "36005".equals(sPlTrckZoneNo)||
                     "36010".equals(sPlTrckZoneNo)||
                     "36015".equals(sPlTrckZoneNo)){
                sYdLocGp1 = "HMD";
                sYdLocGp2 = "";
            }else if("42000".equals(sPlTrckZoneNo)||
                     "38905".equals(sPlTrckZoneNo)){
                sYdLocGp1 = "US";
                sYdLocGp2 = "";
            }else if("42005".equals(sPlTrckZoneNo)||
                     "42010".equals(sPlTrckZoneNo)){
                sYdLocGp1 = "US";
                sYdLocGp2 = "CS";
            }else if("42015".equals(sPlTrckZoneNo)||
                     "42020".equals(sPlTrckZoneNo)||
                     "42500".equals(sPlTrckZoneNo)||
                     "42900".equals(sPlTrckZoneNo)||
                     "42905".equals(sPlTrckZoneNo)){
                sYdLocGp1 = "CS";
                sYdLocGp2 = "";
            }else if("45900".equals(sPlTrckZoneNo)||
                     "45905".equals(sPlTrckZoneNo)||
                     "45910".equals(sPlTrckZoneNo)||
                     "45000".equals(sPlTrckZoneNo)||
                     "45005".equals(sPlTrckZoneNo)||
                     "45010".equals(sPlTrckZoneNo)||
                     "45015".equals(sPlTrckZoneNo)){
                sYdLocGp1 = "DSS";
                sYdLocGp2 = "";
            }else if("48000".equals(sPlTrckZoneNo)||
                     "48005".equals(sPlTrckZoneNo)||
                     "48010".equals(sPlTrckZoneNo)||
                     "48505".equals(sPlTrckZoneNo)||
                     "48900".equals(sPlTrckZoneNo)||
                     "48905".equals(sPlTrckZoneNo)||
                     "48910".equals(sPlTrckZoneNo)||
                     "48915".equals(sPlTrckZoneNo)){
                sYdLocGp1 = "DS";
                sYdLocGp2 = "";
            }else if("48920".equals(sPlTrckZoneNo)||
                     "48925".equals(sPlTrckZoneNo)||
                     "48930".equals(sPlTrckZoneNo)||
                     "48935".equals(sPlTrckZoneNo)||
                     "48981".equals(sPlTrckZoneNo)||
                     "48991".equals(sPlTrckZoneNo)){
                sYdLocGp1 = "CR";
                sYdLocGp2 = "";
            }else if("53000".equals(sPlTrckZoneNo)||
                     "53005".equals(sPlTrckZoneNo)||
                     "53010".equals(sPlTrckZoneNo)||
                     "53015".equals(sPlTrckZoneNo)||
                     "53500".equals(sPlTrckZoneNo)||
                     "53510".equals(sPlTrckZoneNo)||
                     "53520".equals(sPlTrckZoneNo)||
                     "53530".equals(sPlTrckZoneNo)){
                sYdLocGp1 = "CR";
                sYdLocGp2 = "GT";
            }else if("53505".equals(sPlTrckZoneNo)||
                     "53515".equals(sPlTrckZoneNo)||
                     "53525".equals(sPlTrckZoneNo)||
                     "53535".equals(sPlTrckZoneNo)||
                     "53900".equals(sPlTrckZoneNo)||
                     "53905".equals(sPlTrckZoneNo)||
                     "53910".equals(sPlTrckZoneNo)||
                     "53915".equals(sPlTrckZoneNo)||
                     "54000".equals(sPlTrckZoneNo)||
                     "54005".equals(sPlTrckZoneNo)||
                     "54010".equals(sPlTrckZoneNo)||
                     "54015".equals(sPlTrckZoneNo)||
                     "54020".equals(sPlTrckZoneNo)||
                     "54500".equals(sPlTrckZoneNo)||
                     "54505".equals(sPlTrckZoneNo)||
                     "54510".equals(sPlTrckZoneNo)||
                     "54515".equals(sPlTrckZoneNo)||
                     "54520".equals(sPlTrckZoneNo)||
                     "54525".equals(sPlTrckZoneNo)||
                     "54530".equals(sPlTrckZoneNo)||
                     "54535".equals(sPlTrckZoneNo)||
                     "54900".equals(sPlTrckZoneNo)||
                     "54905".equals(sPlTrckZoneNo)||
                     "54910".equals(sPlTrckZoneNo)||
                     "54915".equals(sPlTrckZoneNo)){
                sYdLocGp1 = "GT";
                sYdLocGp2 = "";
            }else if("54900".equals(sPlTrckZoneNo)||
                     "54905".equals(sPlTrckZoneNo)||
                     "54910".equals(sPlTrckZoneNo)||
                     "54915".equals(sPlTrckZoneNo)){
                sYdLocGp1 = "GT";
                sYdLocGp2 = "SB";
            }else if("57000".equals(sPlTrckZoneNo)||
                     "57005".equals(sPlTrckZoneNo)||
                     "57900".equals(sPlTrckZoneNo)||
                     "57905".equals(sPlTrckZoneNo)||
                     "57910".equals(sPlTrckZoneNo)||
                     "57915".equals(sPlTrckZoneNo)){
                sYdLocGp1 = "CMD";
                sYdLocGp2 = "";
            }else if("55991".equals(sPlTrckZoneNo)||
                     "55992".equals(sPlTrckZoneNo)){
                sYdLocGp1 = "WH";
                sYdLocGp2 = "";
            }else{
                sYdLocGp1 = "";
                sYdLocGp2 = "";
            }

            szMsg="[후판정정야드 BOOK OUT 정보] YD_LOC_GP1 :" +sYdLocGp1+"/ YD_LOC_GP2 :" +sYdLocGp2;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            /*
             * 3. 정정야드 북아웃 테이블 등록
             */
            if(!"".equals(sYdLocGp1)){
                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("YD_LOC_GP",     sYdLocGp1);
                recPara.setField("STL_NO",        sStlNo);

                dao.insBookOutInfo(recPara);
            }
            if(!"".equals(sYdLocGp2)){
                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("YD_LOC_GP",     sYdLocGp2);
                recPara.setField("STL_NO",        sStlNo);

                dao.insBookOutInfo(recPara);
            }

            if("55991".equals(sPlTrckZoneNo)||
               "55992".equals(sPlTrckZoneNo)){

                EJBConnector ejbConn = new EJBConnector("default", "CrnSchSeEJB",   this);
                ejbConn.trx("pPlateCrnSchBookout", new Class[] {String.class,String.class},
                                                  new Object[] {sYdLocGp1,   sStlNo });
            }else
            {

                EJBConnector ejbConn = new EJBConnector("default", "CrnSchSeEJB",   this);
                ejbConn.trx("pPlateCrnSchBookout", new Class[] {String.class,String.class,String.class},
                                                  new Object[] {sYdLocGp1,   sStlNo, splBookOutRsnJJYD });
            }
        }catch(Exception e){
            szMsg="[후판정정야드 BOOK OUT 정보]Exception Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            //throw new JDTOException(szMsg);

        } // end of try-catch

    } // end of procPlateJJYardInfo()

    /**
     * 1후판정정야드 QA검사장 북아웃 처리
     *
     * @param inRecord
     * @throws JDTOException
     */
    private void procPlateQAYardInfo(String sPlateNo)throws JDTOException  {

        // DAO 및 UTIL 객체 생성
        YdStockDao ydStockDao           = new YdStockDao();
        YdPlateCommDAO  commDao         = new YdPlateCommDAO();

        String szMethodName = "procPlateQAYardInfo";
        String szMsg        = "";

        JDTORecord recIn            = JDTORecordFactory.getInstance().create();
        JDTORecordSet rsOutRecSet   = JDTORecordFactory.getInstance().createRecordSet("");

        JDTORecord recPara  = null;

        int intRtnVal       = 0;

        int iPL_MEA_GDS_L   = 0;

        try{

            /*
             * 1. 후판공통정보 가져오기(길이정보)
             */
            szMsg="[1후판정정야드 QA검사장 북아웃 처리] sPlateNo :" +sPlateNo;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            recIn.setField("PLATE_NO", sPlateNo);
            intRtnVal = ydStockDao.getYdStock(recIn, rsOutRecSet, 4);

            rsOutRecSet.first();
            recIn = rsOutRecSet.getRecord();

            iPL_MEA_GDS_L = ydDaoUtils.paraRecChkNullInt(recIn,"PL_MEA_GDS_L");

            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("STL_NO",              sPlateNo);

            if(iPL_MEA_GDS_L > 13000) {
                recPara.setField("YD_STK_COL_GP",   "PF0217");
            } else {
                recPara.setField("YD_STK_COL_GP",   "PF0216");
            }
            recPara.setField("YD_STK_BED_NO",   "01");

            intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0059");

            szMsg="[1후판정정야드 QA검사장 북아웃 처리] intRtnVal :" +intRtnVal;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }catch(Exception e){
            szMsg="[1후판정정야드 QA검사장 북아웃 처리]Exception Error:" +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            //throw new JDTOException(szMsg);

        } // end of try-catch

    } // end of procPlateQAYardInfo()

    /**
     * 오퍼레이션명 : 2후판전단정정S1 입고존도착정보 (2후판 전용:S1YDL014)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws DAOException
     */
    public void procS1RcptZoneArrInfo(JDTORecord tcRecord)throws DAOException  {

        // S1YDL014 입고존도착정보
        // PLATE_ID         VARCHAR2(10)    Top Plate id
        // SHIFT_CODE       VARCHAR2(2)     Code(ID) of the current shift
        // PLATE_ID1        VARCHAR2(10)
        // PLATE_ID2        VARCHAR2(10)
        // PLATE_ID3        VARCHAR2(10)
        // PLATE_ID4        VARCHAR2(10)
        // PLATE_ID5        VARCHAR2(10)
        // PLATE_ID6        VARCHAR2(10)
        // PLATE_ID7        VARCHAR2(10)
        // PLATE_ID8        VARCHAR2(10)
        // PLATE_ID9        VARCHAR2(10)
        // PLATE_ID10       VARCHAR2(10)
        // PLATE_ID11       VARCHAR2(10)
        // PLATE_ID12       VARCHAR2(10)
        // PLATE_ID13       VARCHAR2(10)
        // PLATE_ID14       VARCHAR2(10)
        // PLATE_ID15       VARCHAR2(10)
        // PLATE_ID16       VARCHAR2(10)
        // PLATE_ID17       VARCHAR2(10)
        // PLATE_ID18       VARCHAR2(10)
        // PLATE_ID19       VARCHAR2(10)
        // PLATE_ID20       VARCHAR2(10)
        // LINE_ARRIVED     VARCHAR2(1)     '1':1DS Line , '2':2DS Line , '3':Off-Line

        String szMsg                = "";
        String szMethodName         = "procS1RcptZoneArrInfo";
        String szOperationName      = "2후판전단정정S1 입고존도착정보 수신";

        YdDelegate  ydDelegate      = new YdDelegate();

        JDTORecord  recPara         = null;
        JDTORecordSet   rsResult    = null;
        JDTORecord      recInTemp   = null;

        String      szLINE_ARRIVED  = null;
        String      szSTL_NO        = null;
        String      szYD_RCPT_ARR_DT = null;
        String      szSTL_PROG_CD   = null;
        String      szCURR_PROG_CD  = null;
        String      szDIFF          = null;

        int intRtnVal   = 0;
        //재료 매수
        int iTOTAL_NUM              = 0;

        YdPlateCommDAO  commDao         = new YdPlateCommDAO(); //3기 후판제품공용dao


        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(tcRecord);

        //에러 리턴
        if(szRcvTcCode == null || "".equals(szRcvTcCode) ) {
            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }

        try{

            szMsg = "[" + szOperationName + "]  --------------------- 처리 시작 --------------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            recPara  = JDTORecordFactory.getInstance().create();

            //LINE_ARRIVED 변환작업  : '1':1DS Line --> 'B' , '2':2DS Line --> 'A' , '3':Off-Line --> 'C'
            szLINE_ARRIVED = ydDaoUtils.paraRecChkNull(tcRecord, "LINE_ARRIVED");
            if("1".equals(szLINE_ARRIVED)) {
                szLINE_ARRIVED = "B";
            } else if("2".equals(szLINE_ARRIVED)) {
                szLINE_ARRIVED = "A";
            } else if("3".equals(szLINE_ARRIVED)) {
                szLINE_ARRIVED = "C";
            } else {

                szMsg = "[2후판전단정정S1] 입고존도착정보 수신 처리중 LINE_ARRIVED 값  이상 : " + szLINE_ARRIVED;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            }

            //TOTAL_NUM (Total number of piling plates)를 읽어온다.
            iTOTAL_NUM = ydDaoUtils.paraRecChkNullInt(tcRecord, "TOTAL_NUM");

            recInTemp = JDTORecordFactory.getInstance().create();

            for(int Loop_i = 1; Loop_i <= 20; Loop_i++) {

                szSTL_NO = ydDaoUtils.paraRecChkNull(tcRecord, "PLATE_ID" + Loop_i );

                if( "".equals( szSTL_NO ) ) {
                    //PLATE_ID1 부터 PLATE_ID20 까지 읽어 값이 없을 때 for문을 빠져나간다.
                    break;
                }


                //-----------------------------------------------------------------------
                //2후판정정은 보수장에서 검사실적발생시 진도코드가 변경되지 않아 생산실적 받아도 STOCK의 진도코드가 변경 안되는 경우가 있다.
                //입고존 도착시 PLATE 공통의 진도코드로 STOCK의 진도코드를 UPDATE 한다. -- 2013.09.10
                //A)입고존 도착시 TB_PR_PLATE_MAT 의 CR_CORRMC_WRK_DT 후판냉간교정작업일시 를 읽어 2O분  이내면 YDY8L002 REHEAT_SLAB_GP 에 'Y'전송 -- 2013.11.01
                //위 A) 내용 취소 입고존 도착시 폭이 2900 이하 이면  YDY8L002 REHEAT_SLAB_GP 에 'Y'전송 (쿼리만 수정)-- 2013.11.18
                //  폭 2900 이하 --> 폭 3300 이하로 수정 -- 2014.01.02 (쿼리만 수정)
                rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                recInTemp.setField("STL_NO", szSTL_NO);

                intRtnVal = commDao.select(recInTemp, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0083");

                if(intRtnVal<1) {
                    szMsg="["+szOperationName+"] STOCK, PLATECOMM 조회시  오류발생 - intRtnVal : " + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    continue;
                }

                rsResult.first();
                recInTemp       = rsResult.getRecord();

                szYD_RCPT_ARR_DT    = ydDaoUtils.paraRecChkNull(recInTemp,"YD_RCPT_ARR_DT");
                szSTL_PROG_CD       = ydDaoUtils.paraRecChkNull(recInTemp,"STL_PROG_CD");
                szCURR_PROG_CD      = ydDaoUtils.paraRecChkNull(recInTemp,"CURR_PROG_CD");
                szDIFF              = ydDaoUtils.paraRecChkNull(recInTemp,"DIFF");

                if(!szSTL_PROG_CD.equals(szCURR_PROG_CD)) {

                    recInTemp.setField("STL_PROG_CD"    , szCURR_PROG_CD);
                    recInTemp.setField("MODIFIER"       , "S1YDL014");
                    recInTemp.setField("STL_NO"         , szSTL_NO);

                    intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0028");
                }


                //if("".equals(szYD_RCPT_ARR_DT)) {

                    //2후판제품야드로 저장품제원정보를 전송한다.
                    recPara.setField("MSG_ID",        "YDY8L002");
                    recPara.setField("YD_INFO_SYNC_CD",      "A");  //생산실적
                    recPara.setField("STL_NO",        szSTL_NO);
                    recPara.setField("YD_STK_COL_GP", YdConstant.YD_GP_PLATE2_GDS_YARD + "XRTR" + szLINE_ARRIVED);
                    recPara.setField("YD_STK_BED_NO", "01");
                    //recPara.setField("YD_STK_LYR_NO", ""+Loop_i);
                    recPara.setField("YD_STK_LYR_NO", ""+((iTOTAL_NUM-Loop_i)+1));
                    recPara.setField("REHEAT_SLAB_GP", szDIFF);

                    ydDelegate.sendMsg(recPara);
                //}

            }

            szMsg = "[" + szOperationName + "] --------------------- 정상 종료 --------------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        } catch (Exception e) {

            szMsg = "[2후판전단정정S1] 입고존도착정보 수신 처리중 ERROR : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(getClass().getName() + e.getMessage(), e);

        }   // end try catch
    } // end of procS1RcptZoneArrInfo()


    /**
     * 오퍼레이션명 : 2후판전단정정S1 파일링실적 (2후판 전용:S1YDL016)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws DAOException
     */
    public void procS1PilingWr(JDTORecord tcRecord)throws DAOException  {

        // S1YDL016 파일링실적
        // PLATE_ID         VARCHAR2(10)    Top Plate id
        // TOTAL_NUM        VARCHAR2(2)     Total number of piling plates
        // OP_ID1           VARCHAR2(10)
        // SEQ1             VARCHAR2(1)
        // OP_ID2           VARCHAR2(10)
        // SEQ2             VARCHAR2(1)
        // OP_ID3           VARCHAR2(10)
        // SEQ3             VARCHAR2(1)
        // OP_ID4           VARCHAR2(10)
        // SEQ4             VARCHAR2(1)
        // OP_ID5           VARCHAR2(10)
        // SEQ5             VARCHAR2(1)
        // OP_ID6           VARCHAR2(10)
        // SEQ6             VARCHAR2(1)
        // OP_ID7           VARCHAR2(10)
        // SEQ7             VARCHAR2(1)
        // OP_ID8           VARCHAR2(10)
        // SEQ8             VARCHAR2(1)
        // OP_ID9           VARCHAR2(10)
        // SEQ9             VARCHAR2(1)
        // OP_ID10          VARCHAR2(10)
        // SEQ10            VARCHAR2(1)
        // OP_ID11          VARCHAR2(10)
        // SEQ11            VARCHAR2(1)
        // OP_ID12          VARCHAR2(10)
        // SEQ12            VARCHAR2(1)
        // OP_ID13          VARCHAR2(10)
        // SEQ13            VARCHAR2(1)
        // OP_ID14          VARCHAR2(10)
        // SEQ14            VARCHAR2(1)
        // OP_ID15          VARCHAR2(10)
        // SEQ15            VARCHAR2(1)
        // OP_ID16          VARCHAR2(10)
        // SEQ16            VARCHAR2(1)
        // OP_ID17          VARCHAR2(10)
        // SEQ17            VARCHAR2(1)
        // OP_ID18          VARCHAR2(10)
        // SEQ18            VARCHAR2(1)
        // OP_ID19          VARCHAR2(10)
        // SEQ19            VARCHAR2(1)
        // OP_ID20          VARCHAR2(10)
        // SEQ20            VARCHAR2(1)
        // SHIFT_CODE       VARCHAR2(2)     Code(ID) of the current shift
        // PILE_END         VARCHAR2(1)     'Y':Pile end , 'N':Continue to pile

        String szMsg                = "";
        String szMethodName         = "procS1PilingWr";
        String szOperationName      = "2후판전단정정S1 파일링실적 수신";


        //적치단 DAO
        YdStkLyrDao ydStkLyrDao    = new YdStkLyrDao();
        //저장품 DAO
        YdStockDao ydStockDao      = new YdStockDao();


        JDTORecord  recPara         = null;

        String      szPILE_END      = null;
        //재료 매수
        int iTOTAL_NUM              = 0;
        //리턴값(int)
        int intRtnVal               = 0;

        YdPlateCommDAO  commDao         = new YdPlateCommDAO(); //3기 후판제품공용dao

        //TC CODE 추출
        String szRcvTcCode = ydUtils.getTcCode(tcRecord);

        //에러 리턴
        if(szRcvTcCode == null || "".equals(szRcvTcCode) ) {
            szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return;
        }

        try{
            szMsg = "[" + szOperationName + "]  --------------------- 처리 시작 --------------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //파일링 종료 구분을 읽어 온다.
            szPILE_END = ydDaoUtils.paraRecChkNull(tcRecord, "PILE_END");
            if(!"Y".equals(szPILE_END)&&!"N".equals(szPILE_END)) {

                szMsg = "[2후판전단정정S1] 파일링실적 수신 처리중 PILE_END 값  이상 : " + szPILE_END;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            }

            //TOTAL_NUM (Total number of piling plates)를 읽어온다.
            iTOTAL_NUM = ydDaoUtils.paraRecChkNullInt(tcRecord, "TOTAL_NUM");

            recPara = JDTORecordFactory.getInstance().create();

            if("N".equals(szPILE_END)) {
                //continue to pile 인 경우...
                for(int idx = 1; idx <= iTOTAL_NUM; idx++ ) {

                    recPara.setField("YD_STK_COL_GP",       YdConstant.YD_GP_PLATE2_GDS_YARD + "AAP01");
                    recPara.setField("YD_STK_BED_NO",       "01");
                    recPara.setField("YD_STK_LYR_NO",       "00" + ((iTOTAL_NUM-idx)+1));
                    recPara.setField("MODIFIER",            "S1YDL016");
                    recPara.setField("YD_STK_LYR_MTL_STAT", "C");
                    recPara.setField("STL_NO",              ydDaoUtils.paraRecChkNull(tcRecord, "OP_ID" + (idx)));

                    //YD_적치단 업데이트 실행
                    intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);

                    /*
                     * 2010.08.02 YJK
                     * 파일링 실적시간 관리 항목
                     */
                    //if(idx == 1){
                    //
                    //  recPara.setField("STL_NO",          ydDaoUtils.paraRecChkNull(tcRecord, "OP_ID" + (idx)));
                    //
                    //  szMsg = "[" + szOperationName + "] 재료정보 파일링실적 시간 등록]["+ydDaoUtils.paraRecChkNull(tcRecord, "OP_ID" + (idx))+"]";
                    //  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    //  intRtnVal = ydStockDao.update_Dm_Time(recPara,4);
                    //}
                    recPara.setField("STL_NO",          ydDaoUtils.paraRecChkNull(tcRecord, "OP_ID" + (idx)));
                    recPara.setField("COIL_CAR_NO",     "P"); //Piling 실적
                    intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0033");
                }

                return;
            }

            if("Y".equals(szPILE_END)) {
                //pile end 인 경우...
                for(int idx = 1; idx <= iTOTAL_NUM; idx++ ) {

                    recPara.setField("STL_NO",                  "");
                    recPara.setField("YD_STK_LYR_MTL_STAT",     "E");
                    recPara.setField("YD_STK_COL_GP",           YdConstant.YD_GP_PLATE2_GDS_YARD + "AAP01");
                    recPara.setField("STL_NO1",                 ydDaoUtils.paraRecChkNull(tcRecord, "OP_ID" + (idx)) );

                    //YD_적치단 업데이트 실행
                    intRtnVal = ydStkLyrDao.updYdStklyrWithColStock(recPara);


                    //RT상으로 내려놓을 때 프로시져 SP_YD_PLATE_PILING_CHANGE_PB 에서 검사대 통과 동일 파일링코드 매수에 카운트 됨으로
                    //오토파일링 지시에 오류가 발생할 수 있다.. 여기서  PLATE공통의 YD_STR_LOC 을 'T_RTPA'로 설정하여 카운트 안되도록 한다.
                    recPara.setField("PLATE_NO"     , ydDaoUtils.paraRecChkNull(tcRecord, "OP_ID" + (idx)));
                    intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0034");

                    /*
                     * 2010.08.02 YJK
                     * 파일링 실적시간 관리 항목
                     */
                    //if(idx == 1){
                    //
                    //  recPara.setField("STL_NO",          ydDaoUtils.paraRecChkNull(tcRecord, "OP_ID" + (idx)));
                    //
                    //  szMsg = "[" + szOperationName + "] 재료정보 파일링실적 시간 등록]["+ydDaoUtils.paraRecChkNull(tcRecord, "OP_ID" + (idx))+"]";
                    //  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    //  intRtnVal = ydStockDao.update_Dm_Time(recPara,4);
                    //}
                    recPara.setField("STL_NO",          ydDaoUtils.paraRecChkNull(tcRecord, "OP_ID" + (idx)));
                    if(idx == iTOTAL_NUM) {
                        recPara.setField("COIL_CAR_NO",     "R"); //Release 실적
                    } else {
                        recPara.setField("COIL_CAR_NO",     "P"); //Piling 실적
                    }

                    intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0033");
                }
            }

            if("P".equals(szPILE_END)) {
                // by pass 인 경우...

                for(int idx = 1; idx <= iTOTAL_NUM; idx++ ) {

                    recPara.setField("STL_NO"       , ydDaoUtils.paraRecChkNull(tcRecord, "OP_ID" + (idx)));
                    recPara.setField("SNDBK_RSN_CD" , "0");
                    recPara.setField("COIL_CAR_NO",   "B"); //By pass 실적

                    intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0029");

                }
            }
            szMsg = "[" + szOperationName + "] --------------------- 정상 종료 --------------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        } catch (Exception e) {

            szMsg = "[2후판전단정정S1] 파일링실적 수신 처리중 ERROR : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(getClass().getName() + e.getMessage(), e);

        }   // end try catch
    } // end of procS1PilingWr()


    /**
     * 2021.08.07 작업중(김광철)
     * 오퍼레이션명 : 후판제품(1후판) RT 입고가적배드(YD_STK_BED_WHIO_STAT = 'H')의 임시적치재료를
     *                입고예정위치(YD_RCPT_PLN_STR_LOC)로 옮긴다.
     *                -  ○ 입고 가적 BED 시스템 구축으로 입고 사이클 타임 단축
                      - 1차 : 가적 BED 입고, 2차 : 원위치 입고 (자동스케줄)
                      - 적용대상 : C동 ~ E동 (3개동)
                      - 2021.08.02 이명운
     *  입고가적베드이적작업(YDYDJ557)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws JDTOException
     * @ejb.transaction type="RequiresNew"
     */
    public JDTORecord procRcptTempToPlanStrMove(JDTORecord msgRecord) throws DAOException{
        String szMethodName         = "procRcptTempToPlanStrMove";
        String szLogMsg             = "";
        YdPlateCommDAO cmmDao = null;
        JDTORecordSet results = null;
        JDTORecordSet wrkMtls = null;

        JDTORecord rtnRecord = null;
        JDTORecordSet sendMsgRs_YdY8L004 = null;
        JDTORecord params =  null;
        JDTORecord row =  null;
        JDTORecord recordWrkBookMtl = null;
        YdWrkbookDao ydWrkbookDao = null;
        YdWrkbookMtlDao ydWrkbookMtlDao = null;

        szLogMsg="입고가적베드에서 입고예정위치 이적작업 지시 시작";
        ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        try{

            cmmDao = new YdPlateCommDAO();
            results = JDTORecordFactory.getInstance().createRecordSet("plateTmp");
            wrkMtls = JDTORecordFactory.getInstance().createRecordSet("plateTmp");
            params = JDTORecordFactory.getInstance().create();

            rtnRecord = JDTORecordFactory.getInstance().create();
            sendMsgRs_YdY8L004 = JDTORecordFactory.getInstance().createRecordSet("plateTmp");
            // 작업대상 크레인
            String szCrn = msgRecord.getFieldString("YD_EQP_ID");
            if("".equals(szCrn)){
                szLogMsg = "작업대상 크레인정보가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
                return rtnRecord;
            }

            results = JDTORecordFactory.getInstance().createRecordSet("plateTmp");
            params.setField("YD_EQP_ID", szCrn);
            if( cmmDao.select(params, results, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.chkRcptTempToPlanStrMoveDong")>0 ){
                szLogMsg = "전달받은 크레인["+ szCrn + "]은 ["+ results.getRecord(0).getFieldString("ERR_MSG") +"] 사유로 이적작업을 종료합니다.";
                ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
                return rtnRecord;
            }

            // 가능 크레인 1후판 C동 ~ E동 (3개동)개 크레인을 대상으로 진행한다.
//          params.setField("YD_BAY_GP", sYD_BAY_GP);
            params.setField("YD_EQP_ID", szCrn);
            results = JDTORecordFactory.getInstance().createRecordSet("plateYdTmp");
            if(cmmDao.select(params, results, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.chkWrkCrnByTempRecptBed") < 1 ){
                szLogMsg = "작업가능한 크레인["+szCrn+"] 상태가 아니거나, 입고가적베드 자동이적 적용여부가 N, 이적작업이 없는 경우입니다.";
                ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
                return rtnRecord;
//              throw new DAOException(szMethodName +">>"+ szLogMsg);
            }

            // 입고가적베드 대상건수 조회(크레인의 최대 흡착매수만큼만)
            // 맨위 상단기준으로 같은 파일링코드에 대해서 이적실시
            results = JDTORecordFactory.getInstance().createRecordSet("plateTmp");
            params.setField("YD_EQP_ID", szCrn);
            if( cmmDao.select(params, results, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getRcptTempToPlanStrMoveList")>0 ){

                // 작업예약 편성 시작
                // 크레인사양 검증
                // 크레인흡착매수 검증
                int nCnt = results.size();
                String sStlNo = "";
                String sYD_BAY_GP = results.getRecord(0).getFieldString("YD_BAY_GP");
                String sYD_RCPT_PLN_STR_LOC = "";

                // 크레인스케쥴 요청가능여부가 N이면 더이상 진행하지 않는다.
                String sCRN_REQ_YN = "";

                //재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
                //작업예약재료 등록 여부를 체크한다.
                JDTORecordSet outRecSet = null;
                JDTORecord inRec = JDTORecordFactory.getInstance().create();

                for (int i=0; i<nCnt; i++) {
                    row = results.getRecord(i);
                    sStlNo = row.getFieldString("STL_NO");
                    sCRN_REQ_YN = row.getFieldString("CRN_REQ_YN");

                    // 순차적으로 크레인스케쥴 요청 가능여부를 판단하여 불가시에는 반복문을  빠져나간다.
                    if("N".equals(sCRN_REQ_YN)){
                        break;
                    }

                    // 입고예정위치를 업데이트한다.
                    YdToLocDcsnUtil.procPreMainWrkToLocForPlateYd(sStlNo, "", sYD_BAY_GP);
                    // 저장된입고예정위치를 읽어 TO위치가이드 생성
                    outRecSet = JDTORecordFactory.getInstance().createRecordSet("plateTmp");
                    inRec.setField("STL_NO", sStlNo);
                    if(cmmDao.select(inRec, outRecSet, "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStock_PIDEV")>0){
                        row.setField("YD_RCPT_PLN_STR_LOC", outRecSet.getRecord(0).getFieldString("YD_RCPT_PLN_STR_LOC"));
                        sYD_RCPT_PLN_STR_LOC = row.getFieldString("YD_RCPT_PLN_STR_LOC");
                    }

                    // 입고예정위치가 없을 경우
                    if( sYD_RCPT_PLN_STR_LOC == null || "".equals(sYD_RCPT_PLN_STR_LOC)) {
                        szLogMsg = "재료번호["+ sStlNo + "] 입고예정위치를 찾지 못하여 이적작업을 패스합니다.";
                        ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
                        throw new DAOException(szMethodName +">>"+ szLogMsg);
                    }

                    wrkMtls.addRecord(row);
                }

                row = null;
                nCnt = wrkMtls.size();
                String sYD_WBOOK_ID = "";
                String sYD_SCH_CD = "";
                String sCRN_PRIOR = "";
                String sYD_STK_COL_GP = ""; // FROM
                for(int i=0; i<nCnt; i++){

                    row = wrkMtls.getRecord(i);
                    recordWrkBookMtl = JDTORecordFactory.getInstance().create();

                    // 작업예약생성
                    if(i==0){
                        szLogMsg="입고가적베드에서 입고예정위치 이적작업 작업예약생성한다.";
                        ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

                        sYD_STK_COL_GP = row.getFieldString("YD_STK_COL_GP");

                        // 크레인 선택
                        params = JDTORecordFactory.getInstance().create();
                        results = JDTORecordFactory.getInstance().createRecordSet("plateYdTmp");
                        params.setField("YD_STK_COL_GP",    sYD_STK_COL_GP);
                        if(cmmDao.select(params, results, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0135")>0){
                            sYD_SCH_CD = sYD_STK_COL_GP.substring(0, 2) +"YD"+results.getRecord(0).getFieldString("YD_CD_GP")+"MM";
                        }
                        else{
                            szLogMsg = "통합 크레인 스케줄 코드 존재안함 - ["+sYD_SCH_CD+"]";
                            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
                            throw new DAOException(szMethodName +">>"+ szLogMsg);
                        }


                        params.setField("YD_SCH_CD",    sYD_SCH_CD);
                        params.setField("YD_EQP_ID",    szCrn);
                        results = JDTORecordFactory.getInstance().createRecordSet("plateYdTmp");
                        if(cmmDao.select(params, results, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getRcptTempToPlanStrMove_getWrkCrn")>0){
                            sCRN_PRIOR = results.getRecord(0).getFieldString("CRN_PRIOR");
                        }
                        else{
                            szLogMsg = "크레인["+ szCrn + "] 이 IDLE상태가 아니거나 관련 스케쥴("+sYD_SCH_CD+") 정보를 찾지 못했습니다.";
                            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
                            throw new DAOException(szMethodName +">>"+ szLogMsg);
                        }

                        results = JDTORecordFactory.getInstance().createRecordSet("plateTmp");
                        if( getYdWbookId(results) ){
                            sYD_WBOOK_ID = results.getRecord(0).getFieldString("YD_WBOOK_ID");
                        }

                        recordWrkBookMtl.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
                        recordWrkBookMtl.setField("YD_SCH_CD", sYD_SCH_CD);
                        recordWrkBookMtl.setField("WRK_CRN",  szCrn);
                        recordWrkBookMtl.setField("YD_SCH_PRIOR",  sCRN_PRIOR);
                        recordWrkBookMtl.setField("YD_BAY_GP",  sYD_BAY_GP);
                        // To위치를 입고예정위치로
                        recordWrkBookMtl.setField("YD_TO_LOC_GUIDE", row.getFieldString("YD_RCPT_PLN_STR_LOC"));

                        //고객사 미지정으로 처리
                        recordWrkBookMtl.setField("YD_CTS_RELAY_YN" , "N");
                        recordWrkBookMtl.setField("SLAB_SH", ""+nCnt);

                        recordWrkBookMtl.setField("YD_GP", YdConstant.YD_GP_PLATE2_GDS_YARD);
                        recordWrkBookMtl.setField("YD_AIM_YD_GP", YdConstant.YD_GP_PLATE2_GDS_YARD);
                        recordWrkBookMtl.setField("YD_AIM_BAY_GP", sYD_SCH_CD.substring(1,2));
                        recordWrkBookMtl.setField("REGISTER", "wrkMove");
                        //REGISTER
                        ydWrkbookDao = new YdWrkbookDao();
                        if(ydWrkbookDao.insYdWrkbook(recordWrkBookMtl) < 1){
                            szLogMsg = "정상적으로 작업예약을 생성하지 못하였습니다."+ sYD_WBOOK_ID;
                            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
                            throw new DAOException(szMethodName +">>"+ szLogMsg);
                        }

                        // 작업 예약 편성 후 스케줄 기동을 위한 전문 셋팅
                        JDTORecord sendMsg_YdY8L004 = JDTORecordFactory.getInstance().create();
                        sendMsg_YdY8L004.setField("MSG_ID", "YDYDJ506");
                        sendMsg_YdY8L004.setField("YD_SCH_CD", sYD_SCH_CD);
                        sendMsg_YdY8L004.setField("YD_EQP_ID", szCrn);
                        sendMsgRs_YdY8L004.addRecord(sendMsg_YdY8L004);
                        ydWrkbookMtlDao = new YdWrkbookMtlDao();
                    }
                    recordWrkBookMtl.setField("STL_NO", row.getFieldString("STL_NO"));
                    recordWrkBookMtl.setField("YD_STK_COL_GP", row.getFieldString("YD_STK_COL_GP"));
                    recordWrkBookMtl.setField("YD_STK_BED_NO", row.getFieldString("YD_STK_BED_NO"));
                    recordWrkBookMtl.setField("YD_STK_LYR_NO", row.getFieldString("YD_STK_LYR_NO"));
                    recordWrkBookMtl.setField("YD_UP_COLL_SEQ", row.getFieldString("YD_UP_COLL_SEQ"));
                    recordWrkBookMtl.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
                    recordWrkBookMtl.setField("YD_ISPTOR", "");

                    //크레인사양과 저장품 사양을 체크(길이,폭,중량)
                    if(chkCrnSpecMtlSpec(recordWrkBookMtl.getFieldString("STL_NO"), szCrn) ){
                        //다른 작업예약에 재료가 등록되어있는지 체크한다.
                        if(!this.chkYdWrkBookMtl(recordWrkBookMtl.getFieldString("STL_NO"))){
                            szLogMsg = "재료번호["+ recordWrkBookMtl.getFieldString("STL_NO") + "] 작업예약정보가 존재합니다.";
                            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
                            throw new DAOException(szMethodName +">>"+ szLogMsg);
                        }

                        // 작업예약재료 테이블에 등록한다.
                        if(ydWrkbookMtlDao.insYdWrkbookmtl(recordWrkBookMtl) < 1){
                            szLogMsg = "정상적으로 작업예약재료["+ recordWrkBookMtl.getFieldString("STL_NO") +"]를 생성하지 못하였습니다."+ sYD_WBOOK_ID;
                            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
                            throw new DAOException(szMethodName +">>"+ szLogMsg);
                        }
                    }
                }
            } // end of 대상작업재

            rtnRecord.setField("SEND_DATA", sendMsgRs_YdY8L004);
            szLogMsg="입고가적베드에서 입고예정위치 이적작업 지시 종료";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        }catch(JDTOException jdte){
            szLogMsg = "Error : "+ jdte.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            throw new DAOException(szMethodName +">>"+ szLogMsg);
        }catch(DAOException dae){
            szLogMsg = "Error : "+ dae.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            throw new DAOException(szMethodName +">>"+ szLogMsg);
        }catch(Exception e){
            szLogMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            throw new DAOException(szMethodName +">>"+ szLogMsg);
        }

        return rtnRecord ;
    }
//---------------------------------------------------------------------------
} // end of class RcptWrkDmdSeEJBBean

