/**
 * @(#)PlateJspFaEJBBean.java
 *
 * @version         1.0
 * @author          현대제철
 * @date            2012/11/14
 *
 * @description     이클래스는 업무 화면의 메뉴를 관리하기 위한 Facade Session EJB클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/14   조병기      조병기      최초 등록
 * V1.01  2012/11/20   조병기      조병기      GridData 메소드 추가
 * V1.02  2024/10/21               updBedRuleData 메소드 추가
 * V1.03  2024/10/30               updPlateYdCarUppRuleT00031 메소드 추가 
 */

package com.inisteel.cim.yd.jsp.platejsp.session ;

import java.util.ArrayList;
import java.util.List;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.GridHeader;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.rule.GetBreRule6;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.jsp.common.YDComUtil;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;

/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Facade Session EJB클래스입니다.
 *
 * @ejb.bean name="PlateJspFaEJB" jndi-name="PlateJspFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class PlateJspFaEJBBean extends BaseSessionBean {

    /**
     *
     */
    private static final long serialVersionUID = -136535531365165323L;

    YDComUtil   ydComUtil = new YDComUtil();
    private YdUtils ydUtils = new YdUtils();
    YdDaoUtils  ydDaoUtils = new YdDaoUtils();
    CCommUtils commUtils = new CCommUtils();


    private String szSessionName = getClass().getName();

    /**
     * ejbCrate()
     *
     * @throws javax.ejb.CreateException
     */
    public void ejbCreate() throws javax.ejb.CreateException {
    }


    /**
     * 후판제품창고 제품상세 정보조회.
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdProdDtlInfo(GridData inDto) throws JDTOException {
        GridData        gdRes       = null;
        EJBConnector    ejbConn     = null;
        JDTORecordSet   recordSet   = null;
        String          szMethodName  = "";
        String          szMsg         = "";

        szMethodName =  "getPlateYdProdDtlInfo";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            szMsg = "[FACADE] 후판 제품상세 정보 조회[ " + szMethodName + " ] 전송처리 START \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdProdDtlInfo", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            szMsg = "[FACADE] 후판 제품상세 정보 조회[ " + szMethodName + " ] 전송처리 END \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 저장위치 좌표설정화면 조회
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdStkPosSet(GridData inDto) throws JDTOException {
        GridData        gdRes         = null;
        EJBConnector    ejbConn       = null;
        JDTORecordSet   recordSet     = null;
        String          szMethodName  = "getPlateYdStkPosSet";
        String          szMsg         = "";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);

            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStkPosSet", inRecord);

            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg =  e.getMessage() ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 저장위치 좌표설정화면 베드  조회
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdStkPosSetBed(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;
        String          szMethodName  = "getPlateYdStkPosSetBed";
        String          szMsg         = "";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);

            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStkPosSetBed", inRecord);

            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg =  e.getMessage() ;
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 저장위치 좌표설정화면 열 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdStkPosSet(GridData inDto) throws JDTOException {
        GridData gdRes          = null;
        EJBConnector ejbConn    = null;
        String[] szRtnMsg       = null;

        String szMethodName  = "updPlateYdStkPosSet";
        String szMsg         = "";
        String rtnMsg        = YdConstant.RETN_CD_SUCCESS;

        try{

            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
            szMsg = "[JSP Facade] [" + szMethodName + "] inRecord.length :" + inRecord.length ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            if(inRecord.length > 0) {

                ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
//              szRtnMsg = (String[])ejbConn.trx("updPlateYdStkPosSet", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
                // 넘어온 return message 중 성공 못한 message가 있을 경우 해당 message를 return
//              for(int Loop_i = 0; Loop_i < szRtnMsg.length; Loop_i++ ) {
//                  szMsg = "[JSP Facade] [" + Loop_i + "] 번째 처리 RETURN VALUE [ " + szRtnMsg[Loop_i] + " ]\n";
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//                  if( !szRtnMsg[Loop_i].equals(YdConstant.RETN_CD_SUCCESS) ) {
//                      rtnMsg = szRtnMsg[Loop_i];
//                      break;
//                  }
//              }

                ejbConn.trx("updPlateYdStkPosSet", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            }

            gdRes = OperateGridData.cloneResponseGridData(inDto);
            gdRes.setStatus("true");
            gdRes.setMessage(rtnMsg);
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName,szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 저장위치 좌표설정화면 열 등록
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData insPlateYdStkPosSet(GridData inDto) throws JDTOException {
        GridData        gdRes = null;
        EJBConnector    ejbConn = null;
        String          szMethodName  = "insPlateYdStkPosSet";
        String          szMsg         = "";


        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("insPlateYdStkPosSet",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 저장위치 좌표설정화면 열 삭제
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData delPlateYdStkPosSet(GridData inDto) throws JDTOException {
        GridData        gdRes         = null;
        EJBConnector    ejbConn       = null;
        String          szMethodName  = "delPlateYdStkPosSet";
        String          szMsg         = "";

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("insPlateYdStkPosSet",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName,szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } // end of delPlateYdStkPosSet

    /**
     * 저장위치 좌표설정화면 BED  등록
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData insPlateYdStkPosSetBed(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;
        String          szMethodName  = "insPlateYdStkPosSetBed";
        String          szMsg         = "";


        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("insPlateYdStkPosSetBed",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName ,szMsg ,YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 저장위치 좌표설정화면 BED 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdStkPosSetBed(GridData inDto) throws JDTOException {
        GridData        gdRes           = null;
        EJBConnector    ejbConn         = null;
        String[]        szRtnMsg        = null;
        String          szMethodName    = "updPlateYdStkPosSetBed";
        String          rtnMsg          = YdConstant.RETN_CD_SUCCESS;
        String          szMsg           = "";

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            if(inRecord.length > 0) {
                ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
//              szRtnMsg = (String[])ejbConn.trx("updPlateYdStkPosSetBed", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
//
//              // 넘어온 return message 중 성공 못한 message가 있을 경우 해당 message를 return
//              for(int Loop_i = 0; Loop_i < szRtnMsg.length; Loop_i++ ) {
//                  szMsg = "[JSP Facade] [" + Loop_i + "] 번째 처리 RETURN VALUE [ " + szRtnMsg[Loop_i] + " ]\n";
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//                  if( !szRtnMsg[Loop_i].equals(YdConstant.RETN_CD_SUCCESS) ) {
//                      rtnMsg = szRtnMsg[Loop_i];
//                      break;
//                  }
//              }

                ejbConn.trx("updPlateYdStkPosSetBed", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            }

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage(rtnMsg);
            return gdRes;
        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName , e.getMessage() , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     *  후판창고 베드금지 / 해제 조회
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdBedBanCnc(GridData inDto) throws JDTOException {
        GridData        gdRes           = null;
        EJBConnector    ejbConn         = null;
        JDTORecordSet   recordSet       = null;
        String          szMethodName    = "getPlateYdBedBanCnc";
        String          szMsg           = "";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);

            /*************************************
             * getPlateYdBedBanCnc1 Method 호출
             *************************************/
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdBedBanCnc1", inRecord);

            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg =  e.getMessage() ;
            ydUtils.putLog(szSessionName, szMethodName , e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     *  후판창고 베드금지 / 해제
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdBedBanCnc(GridData inDto) throws JDTOException {

        GridData        gdRes           = null;
        EJBConnector    ejbConn         = null;
        String          szMethodName    = "updPlateYdBedBanCnc";
        String          szMsg           = "";
        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("updPlateYdBedBanCnc",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     *  크레인 작업관리 조회
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdCrnWorkMgt(GridData inDto) throws JDTOException {
        GridData        gdRes           = null;
        EJBConnector    ejbConn         = null;
        JDTORecordSet   recordSet       = null;
        String          szMethodName    = "getPlateYdCrnWorkMgt";
        String          szMsg           = "";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdCrnWorkMgt", inRecord);

            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } // End of



    /**
     *  크레인 작업관리 조회 (후판제품 PDA 크레인현황)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdCrnWorkMgtPDA(GridData inDto) throws JDTOException {
        GridData        gdRes           = null;
        EJBConnector    ejbConn         = null;
        JDTORecordSet   recordSet       = null;
        String          szMethodName    = "getPlateYdCrnWorkMgtPDA";
        String          szMsg           = "";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn             = new EJBConnector("default", this);
            recordSet           = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdCrnWorkMgtPDA", inRecord);

            gdRes               = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } // End of getPlateYdCrnWorkMgtPDA


    /**
     * 후판창고 스케줄 기동관리 (조회)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdSchStirMgt(GridData inDto) throws JDTOException {

        GridData        gdRes = null;
        EJBConnector    ejbConn = null;
        JDTORecordSet   recordSet = null;
        String          szMethodName    = "getPlateYdCrnWorkMgt";
        String          szMsg           = "";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);

            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdSchStirMgt", inRecord);

            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdSchStirMgt

    /**
     * 후판창고 스케줄 기동관리 (수정)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData updPlateYdSchStirMgt(GridData gdReq) throws JDTOException {
        GridData        gdRes           = null;
        EJBConnector    ejbConn         = null;
        String          szMethodName    = "updPlateYdSchStirMgt";
        String          szMsg           = "";
        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("updPlateYdSchStirMgt",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }  //end of updPlateYdSchStirMgt

    /**
     *  후판창고 차량진행관리 조회
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdCarWorkList(GridData inDto) throws JDTOException {

        GridData        gdRes           = null;
        EJBConnector    ejbConn         = null;
        JDTORecordSet   recordSet       = null;
        String          szMethodName    = "getPlateYdCarWorkList";
        String          szMsg           = "";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdCarWorkList", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     *  후판창고 야드차량 상차정보 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdCarLiftInfo(GridData inDto) throws JDTOException {

        GridData        gdRes           = null;
        EJBConnector    ejbConn         = null;
        JDTORecordSet   recordSet       = null;
        String          szMethodName    = "getPlateYdCarLiftInfo";
        String          szMsg           = "";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);

            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdCarLiftInfo", inRecord);

            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     *  후판창고 저장위치 별 정보 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */

    public GridData getPlateYdStkLocInfoList(GridData inDto) throws JDTOException {

        GridData        gdRes = null;
        EJBConnector    ejbConn = null;
        JDTORecordSet   recordSet = null;
        String          szMethodName    = "getPlateYdCarLiftInfo";
        String          szMsg           = "";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);

            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStkLocInfoList", inRecord);

            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdStkLocInfoList

    /**
     *  후판창고 차량정지위치상태등록 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */


    public GridData getPlateYdCarStopLocStsReg(GridData inDto) throws JDTOException {

        GridData        gdRes           = null;
        EJBConnector    ejbConn         = null;
        JDTORecordSet   recordSet       = null;
        String          szMethodName    = "getPlateYdCarStopLocStsReg";
        String          szMsg           = "";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);

            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdCarStopLocStsReg", inRecord);

            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     *  후판창고 차량정지상태 등록 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdCarStopLocStsReg(GridData inDto) throws JDTOException {

        GridData        gdRes = null;
        EJBConnector    ejbConn = null;
        String          szMethodName    = "updPlateYdCarStopLocStsReg";
        String          szMsg           = "";
        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("updPlateYdCarStopLocStsReg",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }



    /**
     *  후판창고 크레인 상태설정 조회
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdCrnStsSetID(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;
        String          szMethodName    = "getPlateYdCrnStsSetID";
        String          szMsg           = "";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);

            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdCrnStsSetID", inRecord);

            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     *  후판창고 크레인 상태 수정(UPDATE)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdCrnStsSetCrnStat(GridData inDto) throws JDTOException {

        GridData        gdRes           = null;
        EJBConnector    ejbConn         = null;
        String          szMethodName    = "updPlateYdCrnStsSetCrnStat";
        String          szMsg           = "";
        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("updPlateYdCrnStsSetCrnStat",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     *  후판창고 크레인 운전모드 수정(UPDATE)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdCrnStsSetCrnMode(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        String          szMethodName    = "updPlateYdCrnStsSetCrnMode";
        String          szMsg           = "";

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("updPlateYdCrnStsSetCrnMode",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 설비사양설정 (조회)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdEqpSetSpec(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;
        String          szMethodName    = "getPlateYdEqpSetSpec";
        String          szMsg           = "";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);

            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdEqpSetSpec", inRecord);

            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdEqpSetSpec


    /**
     * 설비사양설정 (수정)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData updPlateYdEqpSetSpec(GridData gdReq) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;
        String          szMethodName    = "updPlateYdEqpSetSpec";
        String          szMsg           = "";
        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("updPlateYdEqpSetSpec",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of updPlateYdEqpSetSpec


    /**
     * 설비사양설정 (삭제)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData delPlateYdEqpSetSpec(GridData gdReq) throws JDTOException {
        GridData        gdRes           = null;
        EJBConnector    ejbConn         = null;
        String          szMethodName    = "delPlateYdEqpSetSpec";
        String          szMsg           = "";

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("delPlateYdEqpSetSpec",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of delPlateYdEqpSetSpec

    /**
     * 설비사양설정 (등록)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData insPlateYdEqpSetSpec(GridData gdReq) throws JDTOException {
        GridData        gdRes           = null;
        EJBConnector    ejbConn         = null;
        String          szMethodName    = "insPlateYdEqpSetSpec";
        String          szMsg           = "";
        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("insPlateYdEqpSetSpec",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of insPlateYdEqpSetSpec


    /**
     * 후판창고RollerTable재료조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdRTDetMatMonitor(GridData inDto) throws JDTOException {

        GridData        gdRes           = null;
        EJBConnector    ejbConn         = null;
        JDTORecordSet   recordSet       = null;
        String          szMethodName    = "getPlateYdRTDetMatMonitor";
        String          szMsg           = "";


        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);

            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdRTDetMatMonitor", inRecord);

            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdRTDetMatMonitor

    /**
     * 후판창고RollerTable재료조회 예정위치 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdRTDetMatMonitor(GridData inDto) throws JDTOException {
        GridData        gdRes           = null;
        EJBConnector    ejbConn         = null;
        String          szMethodName    = "updPlateYdRTDetMatMonitor";
        String          szMsg           = "";

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("updPlateYdRTDetMatMonitor", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   //end of updPlateYdRTDetMatMonitor

    /**
     * 후판제품야드일품별재고조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdDdArtclStkRef(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;
        String          szMethodName    = "getPlateYdDdArtclStkRef";
        String          szMsg           = "";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);

            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdDdArtclStkRef", inRecord);

            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdDdArtclStkRef


    /**
     * 후판제품야드주문별 재고조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdOrdInfoStkRef(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;
        String          szMethodName    = "getPlateYdOrdInfoStkRef";
        String          szMsg           = "";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);

            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdOrdInfoStkRef", inRecord);

            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdOrdInfoStkRef


    /**
     * 반납크레인대상조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdRetCrnReg(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;
        String          szMethodName    = "getPlateYdRetCrnReg";
        String          szMsg           = "";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);

            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdRetCrnReg", inRecord);

            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdRetCrnReg

    /**
     * 반납크레인 수정[공장공정코드]
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData updPlateYdRetCrnReg(GridData gdReq) throws JDTOException {
        //LOG
        String szMsg="";
        String szMethodName="updPlateYdRetCrnReg";

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("updPlateYdRetCrnReg",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(),e);
        }

    }  //end of updPlateYdRetCrnReg


    /**
     * 작업실적 일품조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getplateYdWrkWrDdArtclRef(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;

        String szMsg="";
        String szMethodName="getplateYdWrkWrDdArtclRef";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getplateYdWrkWrDdArtclRef", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);
            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;
        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getplateYdWrkWrDdArtclRef


    /**
     *동/SPAN/열별 모니터링 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdDongSpanLineRef(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;
        String szMsg="";
        String szMethodName="getPlateYdDongSpanLineRef";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdDongSpanLineRef", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdDongSpanLineRef


    /**
     * 야드/동/SPAN/열별 후판 제품 위치 조회 (PDA)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdDongSpanLineRefPDA(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;
        String szMsg="";
        String szMethodName="getPlateYdDongSpanLineRefPDA";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdDongSpanLineRefPDA", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdDongSpanLineRefPDA


    /**
     * 동/SPAN/열별 모니터링 수정
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData updPlateYdDongSpanLineRef(GridData gdReq) throws JDTOException {
        GridData gdRes       = null;
        EJBConnector ejbConn = null;

        //LOG
        String szMsg        = "";
        String szMethodName = "updPlateYdDongSpanLineRef";
        String[] szRtnMsg   = null;
        String rtnMsg       = YdConstant.RETN_CD_SUCCESS;

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg = (String[])ejbConn.trx("updPlateYdDongSpanLineRef", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            // 넘어온 return message 중 성공 못한 message가 있을 경우 해당 message를 return
            for(int Loop_i = 0; Loop_i < szRtnMsg.length; Loop_i++ ) {
                szMsg = "[JSP Facade] [" + Loop_i + "] 번째 처리 RETURN VALUE [ " + szRtnMsg[Loop_i] + " ]\n";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                if( !szRtnMsg[Loop_i].equals(YdConstant.RETN_CD_SUCCESS) ) {
                    rtnMsg = szRtnMsg[Loop_i];
                    break;
                }
            }
            gdRes.setStatus("true");
            gdRes.setMessage(rtnMsg);
            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName , e.getMessage() , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(),e);

        }

    }  //end of updPlateYdDongSpanLineRef


    /**
     * 후판제품야드 동/수주별 재고 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdDongOrdStkRef(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;

        String szMsg = "";
        String szMethodName="getPlateYdDongOrdStkRef";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdDongOrdStkRef", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdDongOrdStkRef



    /**
     * MAP 조회/수정 화면 조회 기능
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getplaetYdStkPosSet(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;
        String szMsg = "";
        String szMethodName="getplaetYdStkPosSet";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getplaetYdStkPosSet", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getplaetYdStkPosSet

    /**
     *  MAP 조회/수정 화면 수정기능
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData updplaetYdStkPosSet(GridData gdReq) throws JDTOException {
        //LOG
        String szMsg="";
        String szMethodName="updplaetYdStkPosSet";

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("updplaetYdStkPosSet",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            return gdRes;

        }catch(Exception e){
            throw new JDTOException(getClass().getName() + e.getMessage(),e);
        }

    }  //end of updplaetYdStkPosSet


    /**
     *  MAP 조회/수정 화면 등록 기능
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData insPlateYdMapStkCol(GridData gdReq) throws JDTOException {
        //LOG
        String szMsg="";
        String szMethodName="insPlateYdMapStkCol";

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("insPlateYdMapStkCol",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            return gdRes;

        }catch(Exception e){
            throw new JDTOException(getClass().getName() + e.getMessage(),e);
        }

    }  //end of insPlateYdMapStkCol

    /**
     *  MAP 조회/수정 화면 삭제 기능
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData delPlateYdMapStkCol(GridData gdReq) throws JDTOException {
        //LOG
        String szMsg="";
        String szMethodName="delPlateYdMapStkCol";

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("delPlateYdMapStkCol",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            return gdRes;

        }catch(Exception e){
            throw new JDTOException(getClass().getName() + e.getMessage(),e);
        }

    }  //end of delPlateYdMapStkCol


    /**
     * 동별 BED 사용 현황
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getplateYdBayBedUseStat(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String szMsg = "";
        String szMethodName="getplateYdBayBedUseStat";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getplateYdBayBedUseStat", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getplateYdBayBedUseStat

    /**
     * 후판제품야드 입고예정 모니터링
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdRcptPlnMonitor(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        String szMsg = "";
        String szMethodName="getPlateYdRcptPlnMonitor";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            szMsg = "[FACADE] 후판 입고예정 모니터링 정보 조회 [ " + szMethodName + " ] 전송처리 START \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdRcptPlnMonitor", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            szMsg = "[FACADE] 후판 입고예정 모니터링 정보 조회 [ " + szMethodName + " ] 전송처리 END \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 후판제품야드 입고예정 모니터링 예정위치 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdRcptPlnMonitor(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMethodName="updPlateYdRcptPlnMonitor";
        String[] szRtnMsg = null;
        String rtnMsg = YdConstant.RETN_CD_SUCCESS;
        String szMsg  = null;
        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg = (String[])ejbConn.trx("updPlateYdRcptPlnMonitor", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            // 넘어온 return message 중 성공 못한 message가 있을 경우 해당 message를 return
            for(int Loop_i = 0; Loop_i < szRtnMsg.length; Loop_i++ ) {
                szMsg = "[JSP Facade] [" + Loop_i + "] 번째 처리 RETURN VALUE [ " + szRtnMsg[Loop_i] + " ]\n";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                if( !szRtnMsg[Loop_i].equals(YdConstant.RETN_CD_SUCCESS) ) {
                    rtnMsg = szRtnMsg[Loop_i];
                    break;
                }
            }
            gdRes.setStatus("true");
            gdRes.setMessage(rtnMsg);
            return gdRes;
        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName , e.getMessage() , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 후판제품야드 입고예정 모니터링 예정위치 수정 (3기)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdRcptPlnMonitor3G(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMethodName="updPlateYdRcptPlnMonitor3G";
        String[] szRtnMsg = null;
        String rtnMsg = YdConstant.RETN_CD_SUCCESS;
        String szMsg  = null;
        String szSearchLoc = null;
        String szPTOP_PLNT_GP = "";

        try{

            szSearchLoc     = inDto.getParam("SEARCH_LOC");
            szPTOP_PLNT_GP  = inDto.getParam("PTOP_PLNT_GP");

            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg = (String[])ejbConn.trx("updPlateYdRcptPlnMonitor3G", new Class[] { JDTORecord[].class , String.class, String.class }, new Object[] { inRecord , szSearchLoc, szPTOP_PLNT_GP });
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            // 넘어온 return message 중 성공 못한 message가 있을 경우 해당 message를 return
            for(int Loop_i = 0; Loop_i < szRtnMsg.length; Loop_i++ ) {
                szMsg = "[JSP Facade] [" + Loop_i + "] 번째 처리 RETURN VALUE [ " + szRtnMsg[Loop_i] + " ]\n";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                if( !szRtnMsg[Loop_i].equals(YdConstant.RETN_CD_SUCCESS) ) {
                    rtnMsg = szRtnMsg[Loop_i];
                    break;
                }
            }
            gdRes.setStatus("true");
            gdRes.setMessage(rtnMsg);
            return gdRes;
        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName , e.getMessage() , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }



    /**
     * 후판제품야드 입고예정 모니터링 R/T변경 처리
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdRTChange(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMethodName="updPlateYdRTChange";
        String[] szRtnMsg = null;
        String rtnMsg = YdConstant.RETN_CD_SUCCESS;
        String szMsg  = null;
        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
            JDTORecord inRec = ydComUtil.genParamToJDTORecord(inDto);


            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg = (String[])ejbConn.trx("updPlateYdRTChange", new Class[] { JDTORecord[].class, JDTORecord.class }, new Object[] { inRecord ,inRec});
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            // 넘어온 return message 중 성공 못한 message가 있을 경우 해당 message를 return
            for(int Loop_i = 0; Loop_i < szRtnMsg.length; Loop_i++ ) {
                szMsg = "[JSP Facade] [" + Loop_i + "] 번째 처리 RETURN VALUE [ " + szRtnMsg[Loop_i] + " ]\n";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                if( !szRtnMsg[Loop_i].equals(YdConstant.RETN_CD_SUCCESS) ) {
                    rtnMsg = szRtnMsg[Loop_i];
                    break;
                }
            }
            gdRes.setStatus("true");
            gdRes.setMessage(rtnMsg);
            return gdRes;
        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName , e.getMessage() , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 2후판제품야드 입고예정 모니터링 R/T변경 처리 (1,2후판 공용)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdRTChange3G(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMethodName="updPlateYdRTChange3G";
        String[] szRtnMsg = null;
        String rtnMsg = YdConstant.RETN_CD_SUCCESS;
        String szMsg  = null;
        String szNewRtNo = null;
        String szYdGp = null;
        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
            szNewRtNo   = inDto.getParam("RET_RT_GP");
            szYdGp      = inDto.getParam("YD_GP");

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg = (String[])ejbConn.trx("updPlateYdRTChange3G", new Class[] { JDTORecord[].class, String.class , String.class }, new Object[] { inRecord ,szNewRtNo ,szYdGp});
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            // 넘어온 return message 중 성공 못한 message가 있을 경우 해당 message를 return
            for(int Loop_i = 0; Loop_i < szRtnMsg.length; Loop_i++ ) {
                szMsg = "[JSP Facade] [" + Loop_i + "] 번째 처리 RETURN VALUE [ " + szRtnMsg[Loop_i] + " ]\n";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                if( !szRtnMsg[Loop_i].equals(YdConstant.RETN_CD_SUCCESS) ) {
                    rtnMsg = szRtnMsg[Loop_i];
                    break;
                }
            }
            gdRes.setStatus("true");
            gdRes.setMessage(rtnMsg);
            return gdRes;
        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName , e.getMessage() , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * A후판 생산실적재등록
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData procAPlGdsPrdWr_Backup(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMethodName="procAPlGdsPrdWr_Backup";
        String[] szRtnMsg = null;
        String rtnMsg = YdConstant.RETN_CD_SUCCESS;
        String szMsg  = null;
        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg = (String[])ejbConn.trx("procAPlGdsPrdWr_Backup", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            // 넘어온 return message 중 성공 못한 message가 있을 경우 해당 message를 return
            for(int Loop_i = 0; Loop_i < szRtnMsg.length; Loop_i++ ) {
                szMsg = "[JSP Facade] [" + Loop_i + "] 번째 처리 RETURN VALUE [ " + szRtnMsg[Loop_i] + " ]\n";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                if( !szRtnMsg[Loop_i].equals(YdConstant.RETN_CD_SUCCESS) ) {
                    rtnMsg = szRtnMsg[Loop_i];
                    break;
                }
            }
            gdRes.setStatus("true");
            gdRes.setMessage(rtnMsg);
            return gdRes;
        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName , e.getMessage() , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 후판제품야드 선별작업 SIMULATION 결과
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdSortWrkSimRtl(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String szMsg = "";
        String szMethodName="getPlateYdSortWrkSimRtl";


        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            szMsg = "[ FACADE START ] 후판제품야드 선별작업 SIMULATION 결과  정보 조회 [ " + szMethodName + " ] 전송처리 \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdSortWrkSimRtl", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            szMsg = "[ FACADE END ] 후판제품야드 선별작업 SIMULATION 결과  정보 조회 [ " + szMethodName + " ] 전송처리 \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 후판제품야드 선별 대상제품 처리
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdSortWrkPdtProc(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String szMsg = "";
        String szMethodName="getPlateYdSortWrkSimRtl";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdSortWrkPdtProc", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 후판제품야드 선별 대상제품 처리 선별 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdSortWrkPdtProc(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMsg = "";
        String szMethodName="updPlateYdSortWrkPdtProc";


        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("updPlateYdSortWrkPdtProc", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 저장위치과부족현황 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdStrPosLackStats(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String szMsg = "";
        String szMethodName="getPlateYdStrPosLackStats";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStrPosLackStats", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }



    /**
     * 저장그룹 상세 조정 화면 코드 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getYdCodeSearch(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String szMsg = "";
        String szMethodName="getYdCodeSearch";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getYdCodeSearch", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 저장그룹 상세 조정 화면 조회 (combobox조건)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdSvGpInfoCombo(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String szMsg = "";
        String szMethodName="getPlateYdSvGpInfoCombo";


        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdSvGpInfoCombo", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 저장그룹 상세 조정 화면 조회 (주문일 조건)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdSvGpInfoDate(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String szMsg = "";
        String szMethodName="getPlateYdSvGpInfoDate";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdSvGpInfoDate", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 저장그룹 상세 조정 화면 조회 (ORDER LINE 조건)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdSvGpInfoOrder(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String szMsg = "";
        String szMethodName="getPlateYdSvGpInfoOrder";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdSvGpInfoOrder", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 저장그룹 상세 조정 화면 (수정)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData updPlateYdSvGpInfo(GridData gdReq) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;
        String szMsg = "";
        String szMethodName="updPlateYdSvGpInfo";

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("updPlateYdSvGpInfo",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(),e);
        }

    }  //end of updPlateYdSvGpInfo

    /**
     * 이적작업 진행관리 화면 띄울때 크레인작업지시 전체 갯수 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdCrnCnt(GridData inDto) throws JDTOException {
        GridData        gdRes           = null;
        EJBConnector    ejbConn         = null;
        JDTORecordSet   recordSet       = null;
        String          szMsg           = "";
        String          szMethodName    ="getPlateYdCrnCnt";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdCrnCnt", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 이적작업 진행관리 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdGdsMvWorkList(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        String          szMsg           = "";
        String          szMethodName    ="getPlateYdGdsMvWorkList";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdGdsMvWorkList", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     *  이적작업 진행관리 (이적작업 취소요구)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData procRmvWrkCancle(GridData gdReq) throws JDTOException {
        String szLogMsg           = "";
        String szMethodName       = "procRmvWrkCancle";
        String szR_msg            = "";

        GridData gdRes = null;
        EJBConnector ejbConn = null;

        try{
            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);

            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szR_msg = (String)ejbConn.trx("procRmvWrkCancle", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes.setMessage(szR_msg);
            ydUtils.putLog(szSessionName, szMethodName, szR_msg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            szLogMsg = "작업 취소 실패 - JDTOException ";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of procRmvWrkCancle

    /**
     * 후판제품야드 Piling 정보변경 및 입고처리
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdPilingDataChng(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdPilingDataChng";


        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdPilingDataChng", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 후판제품야드 R/T 모니터링 상세 내용 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdPilingDataChngDtl(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        String          szMsg           = "";
        String          szMethodName    ="getPlateYdPilingDataChngDtl";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdPilingDataChngDtl", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * Piling 정보 변경 재료 조회
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdPilingDataStlNo(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdPilingDataStlNo";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdPilingDataStlNo", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

             gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdPilingDataStlNo

    /**
     * 후판제품야드 R/T 모니터링 행 추가시 제품번호 정보 SEARCH
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdPilingStockData(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdPilingStockData";

        try{
            szMsg = "[FACADE] 후판 제품 Piling 정보변경 및 입고처리 제품번호 정보  [ " + szMethodName + " ] 전송처리 START \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdPilingStockData", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            szMsg = "[FACADE] 후판 제품 Piling 정보변경 및 입고처리 제품번호 정보  [ " + szMethodName + " ] 전송처리 END \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdPilingStockData

    /**
     * 후판제품야드 저장 Group 편성 스케줄
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdSvGpSchFm(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        String          szMsg           = "";
        String          szMethodName    ="getPlateYdSvGpSchFm";


        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdSvGpSchFm", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     *  후판제품야드 저장 Group 편성 스케줄 고객사 통합 스케줄
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdSvGpSchFmCuDe(GridData inDto) throws JDTOException {

        GridData        gdRes           = null;
        EJBConnector    ejbConn         = null;
        String          szMsg           = "";
        String          szMethodName    ="updPlateYdSvGpSchFmCuDe";

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("updPlateYdSvGpSchFmCuDe",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 후판제품야드 작업 실적 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdWrkRsltQty(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        String          szMsg           = "";
        String          szMethodName    ="getPlateYdWrkRsltQty";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdWrkRsltQty", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdWrkRsltQty

    /**
     * 후판제품야드 저장속성그룹편성 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdStkGrpMgt(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdStkGrpMgt";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStkGrpMgt", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdStkGrpMgt

    /**
     * 후판제품야드 저장속성그룹 기존 및 신규그룹 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdStrCharGrp(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdStrCharGrp";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStrCharGrp", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdStrCharGrp

    /**
     * 후판제품야드 OS신규주문속성 팝업정보 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdStrCharGrp2(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdStrCharGrp2";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStrCharGrp2", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdStrCharGrp2

    /**
     * 저장속성그룹과 저장속성을 연결/해제
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData procMatchORUnMatchStkChar(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;

        String szMsg            = "";
        String szMethodName     = "procMatchORUnMatchStkChar";
        String szOperationName  = "저장속성Match/UnMatch";
        String szRtnMsg         = null;
        try{
            JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
            gdRes = OperateGridData.cloneResponseGridData(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg = (String)ejbConn.trx("procMatchORUnMatchStkChar", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            szMsg = "[JSP Facade : "+ szOperationName +"] 실행성공 - 메세지 : " + szRtnMsg;
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);
            return gdRes;

        }catch(Exception e){
            szRtnMsg = e.getMessage();
            szMsg = "[JSP Facade : "+ szOperationName +"] 오류발생 - 메세지 : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of procMatchORUnMatchStkChar

    /**
     * 저장속성 Common 등록
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData insPlateYdStrChar(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;

        String szMsg            = "";
        String szMethodName     = "insPlateYdStrChar";
        String szOperationName  = "저장속성 Common 등록";
        String szRtnMsg         = null;
        String sRTN_CD          = null;
        String sRTN_MSG         = null;
        JDTORecord outRecord1   = JDTORecordFactory.getInstance().create();
        try{
            JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
            gdRes = OperateGridData.cloneResponseGridData(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            outRecord1  = (JDTORecord)ejbConn.trx("insPlateYdStrChar", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            szMsg = "[JSP Facade : "+ szOperationName +"] 실행성공 - 메세지 : " + szRtnMsg;
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
            gdRes.setStatus("true");
            gdRes.setMessage(sRTN_MSG);
            return gdRes;



        }catch(Exception e){
            szRtnMsg = e.getMessage();
            szMsg = "[JSP Facade : "+ szOperationName +"] 오류발생 - 메세지 : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of insPlateYdStrChar

    /**
     * 후판제품야드 저장속성그룹별 속성 상세 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdStrCharDetail(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdStrCharDetail";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStrCharDetail", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdStrCharDetail

    /**
     * 후판제품야드 저장속성그룹 속성 Mapping
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdCharMapping(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMsg = "";
        String szMethodName="updPlateYdCharMapping";

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("updPlateYdCharMapping", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   //end of updPlateYdCharMapping


    /**
     * 후판제품야드 OS신규주문속성 매핑작업
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdStrChar(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMsg = "";
        String szMethodName="updPlateYdStrChar";

        String sRTN_CD          = null;
        String sRTN_MSG         = null;
        JDTORecord outRecord1   = JDTORecordFactory.getInstance().create();

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            outRecord1 = (JDTORecord)ejbConn.trx("updPlateYdStrChar", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   //end of updPlateYdStrChar

    /**
     * 후판제품야드 저장속성 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData udtPlateYdStrChar(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMsg = "";
        String szMethodName="udtPlateYdStrChar";

        String sRTN_CD          = null;
        String sRTN_MSG         = null;
        JDTORecord outRecord1   = JDTORecordFactory.getInstance().create();

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            outRecord1 = (JDTORecord)ejbConn.trx("udtPlateYdStrChar", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   //end of udtPlateYdStrChar

    /**
     * 후판제품야드 주문속성 삭제
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData delPlateYdStrChar(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMsg = "";
        String szMethodName="delPlateYdStrChar";

        String sRTN_CD          = null;
        String sRTN_MSG         = null;
        JDTORecord outRecord1   = JDTORecordFactory.getInstance().create();

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            outRecord1 = (JDTORecord)ejbConn.trx("delPlateYdStrChar", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   //end of delPlateYdStrChar

    /**
     * 후판제품야드 OS신규주문속성 자동매핑작업
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateAutoMapping(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMsg = "";
        String szMethodName="updPlateAutoMapping";

        YdSlabUtils slabUtils = new YdSlabUtils();

        try{
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            int rowCnt = inDto.getHeader("CHECK").getRowCount();

            szMsg = "[JSP Facade] [" + szMethodName + "] inRecord.length :" + Integer.toString(rowCnt);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            String szORD_NO;
            String szORD_DTL;
            String szFINAL_YN;

            if (rowCnt > 0) {
                for (int i = 0; i < rowCnt; i++) {
                    szORD_NO = commUtils.getValue(inDto, "ORD_NO", i);
                    szORD_DTL = commUtils.getValue(inDto, "ORD_DTL", i);
                    szFINAL_YN = commUtils.getValue(inDto, "FINAL_YN", i);

                    JDTORecord recPara = JDTORecordFactory.getInstance().create();
                    recPara.setField("JMS_TC_CD", "PTYDJ004");
                    recPara.setField("ORD_INTERFACE_DATE", slabUtils.getDateTime14());
                    recPara.setField("ORD_NO", szORD_NO);
                    recPara.setField("ORD_DTL", szORD_DTL);
                    recPara.setField("FINAL_YN", szFINAL_YN);

                    ejbConn = new EJBConnector("default", "RtModRegSeEJB", this);

                    ejbConn.trx("procOrdInputHis", new Class[] { JDTORecord.class }, new Object[] { recPara });
                }
            }

            gdRes.setStatus("true");
            gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   //end of updPlateAutoMapping

    /**
     * 후판제품야드 저장속성그룹 속성 Mapping 취소
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdCharMappingCancle(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMsg = "";
        String szMethodName="updPlateYdCharMappingCancle";

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("updPlateYdCharMappingCancle", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }   //end of updPlateYdCharMappingCancle

    /**
     * 후판제품야드 수주 구분 별 저장속성등록 new
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData procStkCharInsertByOrdGp(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMsg = "";
        String szMethodName="procStkCharInsertByOrdGp";
        JDTORecord outRecord    = JDTORecordFactory.getInstance().create();
        String sRTN_CD      = "";
        String sRTN_MSG     = "";

        try{
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
//SJH
            outRecord   = (JDTORecord)ejbConn.trx("procStkCharInsertByOrdGp", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            sRTN_CD     = StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }
            gdRes.setMessage("정상적으로 등록처리 되었습니다.");
            return gdRes;

        }catch(Exception e){
            szMsg = "[JSP Facade] 오류발생 - " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   //end of procStkCharInsertByOrdGp

    /**
     * 후판제품야드 저장속성등록취소
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData procStkCharCancle(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMsg = "";
        String szMethodName="procStkCharCancle";

        try{
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("procStkCharCancle", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            szMsg = "[JSP Facade]Session Call Success";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = "[JSP Facade] 오류발생 - " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   //end of procStkCharCancle

    /**
     * 후판제품야드 기존 속성 Matching
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData procPlateYdCharMapping(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMsg = "";
        String szMethodName="procPlateYdCharMapping";

        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();

        try{
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            JDTORecord  inRecord_temp[] = ydComUtil.genJDTORecordSet(inDto);

            JDTORecord  inRecord = inRecord_temp[0];

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            outRecord1  = (JDTORecord)ejbConn.trx("procPlateYdCharMapping", new Class[] { JDTORecord.class }, new Object[] { inRecord });
            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            szMsg = "[JSP Facade]Session Call Success";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            gdRes.setStatus("true");
            gdRes.setMessage(sRTN_MSG);
            return gdRes;

        }catch(Exception e){
            szMsg = "[JSP Facade] 오류발생 - " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }


    }   //end of procPlateYdCharMapping

    /**
     * 후판제품야드 저장속성그룹 저장속성그룹명 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdCharGrpNm(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMsg = "";
        String szMethodName="updPlateYdCharGrpNm";

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("updPlateYdCharGrpNm", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);
            szMsg = "[JSP Facade]Session Call Success";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = "[JSP Facade] 오류발생 - " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   //end of updPlateYdCharGrpNm

    /**
     * OS공통테이블에서 정보를 조회 - 후판제품
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateOsInfo(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateOsInfo";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateOsInfo", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateOsInfo


    /**
     * OS공통테이블에서 1차위치등록정보를 조회 - 후판제품
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateOsInfoForOrdRcptPln(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateOsInfoForOrdRcptPln";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateOsInfoForOrdRcptPln", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateOsInfoForOrdRcptPln


    /**
     * 후판제품야드 저장그룹재고현황
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateOsInfoForRcptGrpPdList(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateOsInfoForRcptGrpPdList";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateOsInfoForRcptGrpPdList", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateOsInfoForRcptGrpPdList


    /**
     * 후판제품야드 PILING 코드 등록 - OS 공통테이블, 저장품 테이블
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlatePilingCode(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMsg = "";
        String szMethodName="updPlatePilingCode";

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("updPlatePilingCode", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);
            szMsg = "[JSP Facade]Session Call Success";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = "[JSP Facade] 오류발생 - " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   //end of procStkCharInsert

    /**
     * 저장위치 [산적 LOT 수정] 조회 (후판제품)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */

    public GridData getPlateYdStkPosFix(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdStkPosFix";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStkPosFix", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

             gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdStkPosFix



    /**
     * 저장위치 [산적 LOT 수정] 조회 (코일제품 임시옮길것!!)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */

    public GridData getPlateYdStkPosFix2(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdStkPosFix2";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStkPosFix2", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

             gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   //



    /**
     * 입고예정 위치등록 조회
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdRcptPlnLocReg(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdRcptPlnLocReg";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdRcptPlnLocReg", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

             gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdRcptPlnLocReg

    /**
     * 입고예정 위치등록 상세 정보 조회
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdRcptPlnLocRegRTop(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdRcptPlnLocRegRTop";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdRcptPlnLocRegRTop", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

             gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdRcptPlnLocRegRTop

    /**
     * 입고예정 위치등록 상세 정보 조회(재고, 혼적, 과부족)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdRcptPlnLocRegRBot(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdRcptPlnLocRegRBot";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdRcptPlnLocRegRBot", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

             gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdRcptPlnLocRegRBot

    /**
     *  후판제품창고 차량모니터링 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return JDTORecordSet
     * @throws JDTOException
     */
    public GridData getYdCarMonitoring(GridData inDto) throws JDTOException {
        //LOG
        String szMsg        = "";
        String szMethodName = "getYdCarMonitoring";

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getYdCarMonitoring", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getYdCarMonitoring

    /**
     *  후판제품창고 R/T 반납 작업
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData procPlateYdRTRetCrnReg(GridData inDto) throws JDTOException {
        //LOG
        String szMsg="";
        String szMethodName="procPlateYdRTRetCrnReg";

        GridData gdRes = null;
        EJBConnector ejbConn = null;

        try{
            JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("procPlateYdRTRetCrnReg", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);
            gdRes = CmUtil.copyGDParam(inDto, gdRes);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of procPlateYdRTRetCrnReg

    /**
     *  후판제품창고 R/T 동간이적 작업
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData procPlateYdRTMvCrnReg(GridData inDto) throws JDTOException {
        //LOG
        String szMsg="";
        String szMethodName="procPlateYdRTMvCrnReg";

        GridData gdRes = null;
        EJBConnector ejbConn = null;

        try{
            JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("procPlateYdRTMvCrnReg", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);
            gdRes = CmUtil.copyGDParam(inDto, gdRes);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of procPlateYdRTMvCrnReg

    /**
     *  후판제품창고 R/T 동간입고 작업
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData procPlateYdRTRcptCrnReg(GridData inDto) throws JDTOException {
        //LOG
        String szMsg="";
        String szMethodName="procPlateYdRTRcptCrnReg";

        GridData gdRes = null;
        EJBConnector ejbConn = null;

        try{
            JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("procPlateYdRTRcptCrnReg", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);
            gdRes = CmUtil.copyGDParam(inDto, gdRes);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of procPlateYdRTRcptCrnReg

    /**
     *  후판제품창고 T/R 반납 작업
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData procPlateYdCARRetCrnReg(GridData inDto) throws JDTOException {
        //LOG
        String szMsg="";
        String szMethodName="procPlateYdCARRetCrnReg";

        GridData gdRes = null;
        EJBConnector ejbConn = null;

        try{
            JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("procPlateYdCARRetCrnReg", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);
            gdRes = CmUtil.copyGDParam(inDto, gdRes);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of procPlateYdCARRetCrnReg

    /**
     * 후판제품야드 차량별 작업상태 관리
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdGdsCarWorkStatMgt(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        String szMsg = "";
        String szMethodName="getPlateYdGdsCarWorkStatMgt";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdGdsCarWorkStatMgt", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 후판제품야드 선별 대상제품 처리 저장속성 그룹코드 팝업
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdStrCharGrpPop(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        String szMsg = "";
        String szMethodName="getPlateYdGdsCarWorkStatMgt";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStrCharGrpPop", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     *  후판제품창고 선별 완료 전송 처리
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData procPlateYdShipMtCptSend(GridData inDto) throws JDTOException {
        //LOG
        String szMsg = "";
        String szMethodName="procPlateYdShipMtCptSend";

        GridData gdRes = null;
        EJBConnector ejbConn = null;

        try{
            JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);


            szMsg = "후판제품창고 선별완료 전송처리 시작 ==>";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);


            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("procPlateYdShipMtCptSend", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);
            gdRes = CmUtil.copyGDParam(inDto, gdRes);


            szMsg = "후판제품창고 선별완료 전송처리 ===> 끝";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of procPlateYdShipMtCptSend

    /**
     *  후판제품창고 차량별 작업상세 관리
     * @ejb.interface-method EJBDoclet을 생성하는 태그입리니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdCarDtlWorkMgt(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        String szMsg = "";
        String szMethodName="getPlateYdCarDtlWorkMgt";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdCarDtlWorkMgt", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of getPlateYdCarDtlWorkMgt




    /**
     *  후판제품창고 차량별 작업상세 관리 PDA 화면용
     * @ejb.interface-method EJBDoclet을 생성하는 태그입리니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdCarDtlWorkMgt_PDA(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        String szMsg = "";
        String szMethodName="getPlateYdCarDtlWorkMgt_PDA";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdCarDtlWorkMgt_PDA", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of getPlateYdCarDtlWorkMgt_PDA




    /**
     *  코일제품창고 차량별 작업상세 관리 PDA 화면용
     * @ejb.interface-method EJBDoclet을 생성하는 태그입리니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getCoilYdCarDtlWorkMgt_PDA(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        String szMsg = "";
        String szMethodName="getCoilYdCarDtlWorkMgt_PDA";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getCoilYdCarDtlWorkMgt_PDA", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of getCoilYdCarDtlWorkMgt_PDA



    /**
     *  후판제품창고 반납 크레인 차량 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입리니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdIdelCar(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        String szMsg = "";
        String szMethodName="getPlateYdIdelCar";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdIdelCar", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of getPlateYdIdelCar

    /**
     * 후판제품야드 저장위치 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdStkPosFix(GridData inDto) throws DAOException {

        GridData gdRes          = null;
        EJBConnector ejbConn    = null;

        String szMsg            = "";
        String szRtnMsg         = "";
        String szMethodName     = "updPlateYdStkPosFix";
        JDTORecord recTemp      = null;
        JDTORecord recPara      = null;
        JDTORecord recCheck      = null;

        JDTORecordSet recordSet1 = null;
        JDTORecordSet recordSet2 = null;
        JDTORecordSet recordSet3 = null;

        JDTORecordSet recordSet4 = null;
        JDTORecordSet recordSet5 = null;

        String szTemp            = null;

        int nRtnVal= 0;

        JDTORecordSet rsStock   = null;

        YdStockDao ydStockDao           = new YdStockDao();
        YdStkLyrDao ydStkLyrDao         = new YdStkLyrDao();
        YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
        
        boolean userCheck = false; //업무기준 사용자로 지정 여부 true : 지정, false : 미지정
        JDTORecord rsUserTemp = null;
        String szYdStkColGp = null;
        String szYdStkBedNo = null;
        		
        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
            // 20251119 : 추관식
            // 권한 체크
            /**
             * 업무기준 사용자로 지정된 경우, 
             * [기존과 동일]후판제품관리영역(A동 04~07스판 B동 01~03스판을 제외한 구역) + 후판제품관리 제외영역(A동 04~07스판 / B동 01~03스판)을 수정 할 수 있도록 하고,
			   업무기준 사용자로 지정되지 않았을 경우, 
			   후판제품관리 제외영역(A동 04~07스판 / B동 01~03스판)만 수정가능 하도록 변경
             */
            
            for (int nLoop = 0 ; nLoop < inRecord.length ; nLoop++){
                if(nLoop == 0) {
                	recTemp = JDTORecordFactory.getInstance().create();
                    recTemp = inRecord[nLoop];
                    
                    recPara  = JDTORecordFactory.getInstance().create();
                    recPara.setField("EMPNO", ydDaoUtils.paraRecChkNull(recTemp, "YD_USER_ID"));
                    rsStock = JDTORecordFactory.getInstance().createRecordSet("Yd");
                    
                    //신규 추가 - com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPtPlateCommLocUser
                	nRtnVal  = ydStockDao.getYdStock(recPara, rsStock, 229);
                	
                    if(nRtnVal <= 0){
                        szMsg = "[권한없음]_수정 조건_업무기준이 [A동 04~07스판 / B동 01~03스판]으로 제한됩니다.";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                        userCheck = false;
                    } else {
                    	szMsg = "[권한존재]_수정 조건_업무기준이 전체로 설정됩니다.";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                    	userCheck = true;
                    }
                	break;
                }

            }
            
            // 저장품에 있는 재료인지 CHECK
            for (int nLoop = 0 ; nLoop < inRecord.length ; nLoop++){

                recTemp = JDTORecordFactory.getInstance().create();
                recTemp = inRecord[nLoop];

                szTemp = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
                
                if(szTemp.trim().equals("")){
                    continue;
                }
                
                recPara  = JDTORecordFactory.getInstance().create();
                recPara.setField("STL_NO", szTemp);
                
                rsStock = JDTORecordFactory.getInstance().createRecordSet("Yd");
                nRtnVal  = ydStockDao.getYdStock(recPara, rsStock, 0);
                
                if(nRtnVal <=0){
                
                    szMsg = "저장품에 데이터가 없습니다";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                
                    gdRes = OperateGridData.cloneResponseGridData(inDto);
                    gdRes.setMessage( szMsg );
                
                    return gdRes;
                
                
                }
                
                //작업예약 재료확인
                recordSet1 = JDTORecordFactory.getInstance().createRecordSet("Yd");
                nRtnVal  = ydWrkbookMtlDao.getYdWrkbookmtl(recTemp, recordSet1, 2);
                
                if(nRtnVal > 0 ) {
                
                    szRtnMsg = "해당재료 ["+szTemp+"] 는 작업예약재료 입니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
                
                    gdRes = OperateGridData.cloneResponseGridData(inDto);
                    gdRes.setMessage(szRtnMsg);
                
                    return gdRes;
                }
                
                //스케쥴 재료확인
                recordSet2 = JDTORecordFactory.getInstance().createRecordSet("Yd");
                recTemp.setField("YD_STK_LYR_MTL_STAT", "U");
                nRtnVal  = ydStkLyrDao.getYdStklyr(recTemp, recordSet2, 102);
                
                if(nRtnVal > 0 ) {
                
                    szRtnMsg = "해당재료 ["+szTemp+"] 는 크레인작업재료 입니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
                
                    gdRes = OperateGridData.cloneResponseGridData(inDto);
                    gdRes.setMessage(szRtnMsg);
                
                    return gdRes;
                }
                
                recordSet3 = JDTORecordFactory.getInstance().createRecordSet("Yd");
                recTemp.setField("YD_STK_LYR_MTL_STAT", "D");
                nRtnVal  = ydStkLyrDao.getYdStklyr(recTemp, recordSet3, 102);
                
                if(nRtnVal > 0 ) {
                
                     szRtnMsg = "해당재료 ["+szTemp+"] 는 크레인작업재료 입니다.";
                     ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
                
                     gdRes = OperateGridData.cloneResponseGridData(inDto);
                     gdRes.setMessage(szRtnMsg);
                
                     return gdRes;
                }
                
                //20251119 : 추관식, 권한 체크 -- 로직
                recordSet4 = JDTORecordFactory.getInstance().createRecordSet("Yd");
                recTemp.setField("YD_STK_LYR_MTL_STAT", "");
                nRtnVal  = ydStkLyrDao.getYdStklyr(recTemp, recordSet4, 3); //재료정보 조회
                
                if(nRtnVal > 0) {
                	rsUserTemp = JDTORecordFactory.getInstance().create();
                	
                	recordSet4.first();
            		rsUserTemp = recordSet4.getRecord();
            		szYdStkColGp = ydDaoUtils.paraRecChkNull(rsUserTemp, "YD_STK_COL_GP");
            		
            		//20251217 - 후판제품야드일 경우만 체크한다.
            		if(szYdStkColGp.startsWith("T")){

                		if(!userCheck){
                			//후판제품관리 제외영역(A동 04~07스판 / B동 01~03스판)만 수정가능 하도록 변경
                			
                			//20251229 : 추관식, 여기서 쿼리로 조건 변경
                            recordSet5 = JDTORecordFactory.getInstance().createRecordSet("Yd");
                            
                            recCheck = JDTORecordFactory.getInstance().create();
                            recCheck.setField("YD_STK_COL_GP", szYdStkColGp);
                            //com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyryAllowCheckYdStkColGp
                            nRtnVal  = ydStkLyrDao.getYdStklyr(recCheck, recordSet5, 113); //조건 체크 조회
                            
                            if(nRtnVal > 0) {
                            	rsUserTemp = JDTORecordFactory.getInstance().create();
                            	
                            	recordSet5.first();
                        		rsUserTemp = recordSet5.getRecord();
                        		szYdStkColGp = ydDaoUtils.paraRecChkNull(rsUserTemp, "YD_STK_COL_GP");
                        		
                        		String bay = ydDaoUtils.paraRecChkNull(rsUserTemp, "BAY"); //BAY
                        		int span = Integer.parseInt(ydDaoUtils.paraRecChkNull(rsUserTemp, "SPAN")); //SPAN
                        		
                            	szMsg = "후판제품관리 제외영역 ["+bay+"동 "+span+" SPAN] 수정 가능";
                            	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
	                			//if(("A".equalsIgnoreCase(bay) && (span >= 4 && span <= 7 ) ) || ("B".equalsIgnoreCase(bay) && (span >= 1 && span <= 3 ))){
	                			//	szMsg = "후판제품관리 제외영역 ["+bay+"동 "+span+" SPAN] 수정 가능";
	                            //    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
                			} else {
                        		String bay = szYdStkColGp.substring(1, 2); //동
                        		int span = Integer.parseInt(szYdStkColGp.substring(2, 4)); //SPAN
                				szRtnMsg = "[권한없음]_수정 조건_["+bay+"동 "+span+" SPAN]를 수정할 수 없습니다.";
                                ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
                                gdRes = OperateGridData.cloneResponseGridData(inDto);
                                gdRes.setMessage(szRtnMsg);
                                return gdRes;
                			}
                		}
            		}
                }
            }

            ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
            ejbConn.trx("updPlateYdStkPosFix", new Class[] { JDTORecord[].class },
                                              new Object[] { inRecord });
            
            gdRes = OperateGridData.cloneResponseGridData(inDto);
            
            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new DAOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 이송대상재 POP(조회)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdRcptPlnMtl(GridData inDto) throws JDTOException {
        GridData        gdRes = null;
        EJBConnector    ejbConn = null;
        JDTORecordSet   recordSet = null;
        String          szMethodName  = "getPlateYdRcptPlnMtl";
        String          szMsg         = "";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            recordSet = (JDTORecordSet) ejbConn.trx("getPlateYdRcptPlnMtl",
                    new Class[] { JDTORecord.class }, new Object[] { inRecord });

            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }


    /**
     *  이송대상재 저장품 수정
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData updFrtoMoveMtlToStock(GridData inDto) throws JDTOException {
        //LOG
        String szMsg = "";
        String szMethodName="updFrtoMoveMtlToStock";
        String szOperationName = "이송대상재 저장품 수정";
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        try{
            JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            String szRtnMsg = (String)ejbConn.trx("updFrtoMoveMtlToStock", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);
            gdRes = CmUtil.copyGDParam(inDto, gdRes);
            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);
            szMsg = "[JSP Facade : " + szOperationName + "] 반환메세지 - " + szRtnMsg;
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of updFrtoMoveMtlToStock

    /**
     *  후판제품창고 R/T 모니터링 Piling 완료 처리
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData procPlateYdPilingCompletion(GridData inDto) throws JDTOException {
        //LOG
        String szMsg = "";
        String szMethodName="procPlateYdPilingCompletion";

        GridData gdRes = null;
        EJBConnector ejbConn = null;

        try{
            szMsg = "[FACADE] 후판 제품창고 Piling 완료 처리 [ " + szMethodName + " ] START \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("procPlateYdPilingCompletion", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);
            gdRes = CmUtil.copyGDParam(inDto, gdRes);

            szMsg = "[FACADE] 후판 제품창고 Piling 완료 처리 [ " + szMethodName + " ] END \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of procPlateYdPilingCompletion

    /**
     *  후판제품창고  R/T 모니터링 Book-Out 요구 처리
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData procPlateYdBookOutReq(GridData inDto) throws JDTOException {
        //LOG
        String szMsg = "";
        String szMethodName="procPlateYdBookOutReq";

        GridData gdRes = null;
        EJBConnector ejbConn = null;

        try{
            szMsg = "[FACADE] 후판 제품창고 Piling 완료 처리 [ " + szMethodName + " ] 메소드 시작";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
            //JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
            JDTORecord inRecord =  ydComUtil.genParamToJDTORecord(inDto);



            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("procPlateYdBookOutReq", new Class[] { JDTORecord.class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);
            gdRes = CmUtil.copyGDParam(inDto, gdRes);

            szMsg = "[FACADE] 후판 제품창고 Piling 완료 처리 [ " + szMethodName + " ] 메소드 끝";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of procPlateYdBookOutReq


    /**
     *  후판제품창고  R/T 모니터링 Book-Out재료 추가/수정
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData updBookOutStlChng(GridData inDto) throws JDTOException {
        //LOG
        String szMsg                    = "";
        String szMethodName             = "updBookOutStlChng";
        String szOperationName          = "Book-Out재료추가/수정";
        String szRtnMsg                 = "";

        GridData gdRes                  = null;
        EJBConnector ejbConn            = null;

        try{
            szMsg = "[Jsp Facade : "+ szOperationName +"] ----------------- 메소드 시작 -----------------";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            //gdRes = CmUtil.copyGDParam(inDto, gdRes);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            JDTORecord[] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg = (String)ejbConn.trx("updBookOutStlChng", new Class[] { JDTORecord[].class }, new Object[] { inRecord });


            if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
                gdRes.setMessage(szRtnMsg);

                szMsg = "[Jsp Facade : "+ szOperationName +"] PlateJspSeEJB.updBookOutStlChng 처리 성공";
                ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            }else{
                gdRes.setMessage(szRtnMsg);

                szMsg = "[Jsp Facade : "+ szOperationName +"] PlateJspSeEJB.updBookOutStlChng 처리 시 오류발생 - 메세지 : " + szRtnMsg;
                ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
            }

            szMsg = "[Jsp Facade : "+ szOperationName +"] ----------------- 메소드 끝 -----------------";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            gdRes.setStatus("true");
            return gdRes;

        }catch(Exception e){
            szMsg   = "[Jsp Facade : "+ szOperationName +"] 예외발생 - 메세지 : " + szRtnMsg;
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of updBookOutStlChng


    /**
     *  후판제품창고 R/T 모니터링 행 삭제 처리
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData delPlateYdPilingData(GridData inDto) throws JDTOException {
        //LOG
        String szMsg            = "";
        String szMethodName     = "delPlateYdPilingData";
        String szOperationName  = "Piling/Book-Out재료삭제";
        String szRtnMsg         = "";

        GridData gdRes = null;
        EJBConnector ejbConn = null;

        try{

            szMsg = "[Jsp Facade : "+ szOperationName +"] ----------------- 메소드 시작 -----------------";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            ejbConn     = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg    = (String)ejbConn.trx("delPlateYdPilingData", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            //gdRes = CmUtil.copyGDParam(inDto, gdRes);

            if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
                gdRes.setMessage(szRtnMsg);

                szMsg = "[Jsp Facade : "+ szOperationName +"] PlateJspSeEJB.delPlateYdPilingData 처리 성공";
                ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            }else{
                gdRes.setMessage(szRtnMsg);

                szMsg = "[Jsp Facade : "+ szOperationName +"] PlateJspSeEJB.delPlateYdPilingData 처리 시 오류발생 - 메세지 : " + szRtnMsg;
                ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
            }

            szMsg = "[Jsp Facade : "+ szOperationName +"] ----------------- 메소드 끝 -----------------";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            gdRes.setStatus("true");
            return gdRes;

        }catch(Exception e){
            szMsg   = "[Jsp Facade : "+ szOperationName +"] 예외발생 - 메세지 : " + szRtnMsg;
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of delPlateYdPilingData


    /**
     *  후판제품창고  R/T 모니터링 Book-Out 취소 처리
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData procBookOutCancel(GridData inDto) throws JDTOException {
        //LOG
        String szMsg            = "";
        String szMethodName     = "procBookOutCancel";
        String szOperationName  = "Book-Out취소";
        String szRtnMsg         = "";

        GridData gdRes = null;
        EJBConnector ejbConn = null;

        try{

            szMsg = "[Jsp Facade : "+ szOperationName +"] ----------------- 메소드 시작 -----------------";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            JDTORecord inRecord =  ydComUtil.genParamToJDTORecord(inDto);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            ejbConn     = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg    = (String)ejbConn.trx("procBookOutCancel", new Class[] { JDTORecord.class }, new Object[] { inRecord });

            //gdRes = CmUtil.copyGDParam(inDto, gdRes);

            if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
                gdRes.setMessage(szRtnMsg);

                szMsg = "[Jsp Facade : "+ szOperationName +"] PlateJspSeEJB.procBookOutCancel 처리 성공";
                ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            }else{
                gdRes.setMessage(szRtnMsg);

                szMsg = "[Jsp Facade : "+ szOperationName +"] PlateJspSeEJB.procBookOutCancel 처리 시 오류발생 - 메세지 : " + szRtnMsg;
                ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
            }

            szMsg = "[Jsp Facade : "+ szOperationName +"] ----------------- 메소드 끝 -----------------";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            gdRes.setStatus("true");
            return gdRes;

        }catch(Exception e){
            szMsg   = "[Jsp Facade : "+ szOperationName +"] 예외발생 - 메세지 : " + szRtnMsg;
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of procBookOutCancel

    /**
     * 후판제품창고 Piling 정보 변경 및 입고 처리 Book-Out 위치 변경
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdBooOutLocChng(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMsg = "";
        String szMethodName="updPlateYdBooOutLocChng";


        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
            JDTORecord  inRec =  CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("updPlateYdBooOutLocChng", new Class[] { JDTORecord[].class, JDTORecord.class}, new Object[] { inRecord, inRec});

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }   // end of updPlateYdBooOutLocChng

    /**
     * 후판제품창고 Piling 정보 변경 및 입고 처리 예정 위치 변경
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdRcptPlnLocChng(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMsg = "";
        String szMethodName="updPlateYdRcptPlnLocChng";


        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
            JDTORecord  inRec =  CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("updPlateYdRcptPlnLocChng", new Class[] { JDTORecord[].class, JDTORecord.class}, new Object[] { inRecord, inRec});

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }   // end of updPlateYdRcptPlnLocChng

    /**
     * 입고예정 정보 조회
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdRcptPln(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdRcptPln";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdRcptPln", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

             gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdRcptPln

    /**
     * 입고예정 BED 현황 조회(사용안함)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdStatusBed(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdStatusBed";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStatusBed", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdStatusBed

    /**
     * 입고예정 BED 현황 조회(OSCOMM추가)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdStatusBed_OSCOMM(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdStatusBed_OSCOMM";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStatusBed_OSCOMM", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdStatusBed_OSCOMM

    /**
     * 입고예정 BED 등록
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdRcptPlnStrLoc(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="updPlateYdRcptPlnStrLoc";

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
            JDTORecord  inRec =  CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("updPlateYdRcptPlnStrLoc", new Class[] { JDTORecord[].class, JDTORecord.class }, new Object[] { inRecord, inRec });
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of updPlateYdRcptPlnStrLoc

    /**
     * 저장속성그룹에 해당하는 파일링코드 조회.
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdPilingCdList(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdPilingCdList";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdPilingCdList", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdPilingCdList

    /**
     * OS, Plate공통, 야드저장품 Piling코드 변경.
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdPilingCdInfo(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="updPlateYdPilingCdInfo";

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
            JDTORecord  inRec =  CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("updPlateYdPilingCdInfo", new Class[] { JDTORecord[].class, JDTORecord.class }, new Object[] { inRecord, inRec });
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of updPlateYdPilingCdInfo


    /**
     * 후판제품야드 Marking 대상 List
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdMarkingPdList(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        String szMsg = "";
        String szMethodName="getPlateYdMarkingPdList";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            szMsg = "[ FACADE START ] 후판제품야드 Marking 대상 List 정보 조회 [ " + szMethodName + " ] 전송처리 \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdMarkingPdList", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            szMsg = "[ FACADE END ] 후판제품야드 Marking 대상 List 정보 조회 [ " + szMethodName + " ] 전송처리 \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdMarkingPdList

    /**
     * 후판제품야드 Marking 대상 List 변경 유무 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdMarkingPdList(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMethodName="updPlateYdMarkingPdList";
        String[] szRtnMsg = null;
        String rtnMsg = YdConstant.RETN_CD_SUCCESS;
        String szMsg  = null;
        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            szMsg = "[ FACADE START ] 후판제품야드 Marking 대상 List 변경 유무 [ " + szMethodName + " ] 수정 처리  \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg = (String[])ejbConn.trx("updPlateYdMarkingPdList", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            // 넘어온 return message 중 성공 못한 message가 있을 경우 해당 message를 return
            for(int Loop_i = 0; Loop_i < szRtnMsg.length; Loop_i++ ) {
                szMsg = "[JSP Facade] [" + Loop_i + "] 번째 처리 RETURN VALUE [ " + szRtnMsg[Loop_i] + " ]\n";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                if( !szRtnMsg[Loop_i].equals(YdConstant.RETN_CD_SUCCESS) ) {
                    rtnMsg = szRtnMsg[Loop_i];
                    break;
                }
            }

            szMsg = "[ FACADE END ] 후판제품야드 Marking 대상 List 변경 유무 [ " + szMethodName + " ] 수정 처리  \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            gdRes.setStatus("true");
            gdRes.setMessage(rtnMsg);
            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName , e.getMessage() , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }


    }   // end of updPlateYdMarkingPdList

    /**
     * 후판제품야드 이송재료 LIST
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getYdFrtoMoveMtlList(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        String szMsg = "";

        String szMethodName = "getYdFrtoMoveMtlList";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            szMsg = "[ FACADE START ] 후판제품야드 이송재료 LIST 정보 조회 [ " + szMethodName + " ] 전송처리 \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getYdFrtoMoveMtlList", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            szMsg = "[ FACADE END ] 후판제품야드 이송재료 LIST 정보 조회 [ " + szMethodName + " ] 전송처리 \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getYdFrtoMoveMtlList

    /**
     * 목표행선 / 목표야드 / 목표동 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdFrToMoveMtlList(GridData inDto) throws JDTOException {
        /*
         * 통합 슬라브 야드의 이송재료 LIST의 목표행선/목표야드/목표동 수정 기능과 같은 기능으로 슬라브 공통 수정처리 기능과 동일
         */
        String szMethodName="updPlateYdFrToMoveMtlList";
        String szRcvMsg = "";
        String szMsg = "";

        GridData gdRes = null;
        EJBConnector ejbConn = null;

        try{
            JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);

            szMsg = "[ FACADE START ] 후판제품야드 목표행선 / 목표야드 / 목표동 수정  [ " + szMethodName + " ] 처리  \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRcvMsg = (String)ejbConn.trx("updPlateYdFrToMoveMtlList",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage(szRcvMsg);

            szMsg = "[ FACADE END ] 후판제품야드 목표행선 / 목표야드 / 목표동 수정  [ " + szMethodName + " ] 처리  \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            gdRes.setStatus("updPlateYdFrToMoveMtlList");
            gdRes.setMessage(szRcvMsg);
            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } // end of updPlateYdFrToMoveMtlList


    /**
     * 이송대상재를 준비스케줄에 등록 - 수동
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData insYdPrepSchByManual(GridData inDto) throws JDTOException {
        /*
         * 업무기준 : 그리드에 선택된 대상재를 준비스케줄에 등록
         * 수정자 : 임춘수
         * 수정일 : 2009.09.28
         */
        String szMethodName = "insYdPrepSchByManual";
        String szRtnMsg     = null;
        String szLogMsg = "";
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        try{
            szLogMsg = "JSP-FACADE [이적대상재를 준비스케줄에 등록 - 수동] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            szRtnMsg = (String)ejbConn.trx("insYdPrepSchByManual",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);

            szLogMsg = "JSP-FACADE [이적대상재를 준비스케줄에 등록 - 수동] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of insYdPrepSchByManual

    /**
     * 후판제품야드 BED별 이적대상 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdMvPdtList(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String szMsg = "";
        String szMethodName="getPlateYdMvPdtList";


        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            szMsg = "[ FACADE START ] 후판제품야드 선별작업 SIMULATION 결과  정보 조회 [ " + szMethodName + " ] 전송처리 \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdMvPdtList", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            szMsg = "[ FACADE END ] 후판제품야드 선별작업 SIMULATION 결과  정보 조회 [ " + szMethodName + " ] 전송처리 \n";
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }




    /**
     * MAP 조회 및 수정화면[폭정보 변경]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updYdStkColWGp(GridData inDto) throws JDTOException {
        /*
         * 통합 슬라브 야드의 이송재료 LIST의 목표행선/목표야드/목표동 수정 기능과 같은 기능으로 슬라브 공통 수정처리 기능과 동일
         */
        String szMethodName="updYdStkColWGp";
        String szRcvMsg = "";
        String szMsg = "";
        String szOperationName = "MAP 조회 및 수정화면[폭정보 변경]";

        GridData gdRes = null;
        EJBConnector ejbConn = null;

        try{

            szMsg = "[Jsp-Facade "+szOperationName+" ]시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);



            JDTORecord [] inRecord = ydComUtil.genGridToJDTORecord(inDto);
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRcvMsg = (String)ejbConn.trx("updYdStkColWGp",new Class[] { JDTORecord[].class }, new Object[] { inRecord });


            gdRes.setStatus("true");
            gdRes.setMessage(szRcvMsg);

            szMsg = "[Jsp-Facade "+szOperationName+" ] 끝 ";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 이송대상재를 준비스케줄에 등록 - 수동, 크레인설비 등록
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData insYdPrepSchNCrnByManual(GridData inDto) throws JDTOException {
        /*
         * 업무기준 : 그리드에 선택된 대상재를 준비스케줄에 등록
         * 수정자 : 임춘수
         * 수정일 : 2009.09.28
         */
        String szMethodName = "insYdPrepSchNCrnByManual";
        String szRtnMsg     = null;
        String szLogMsg = "";
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        try{

            szLogMsg = "JSP-FACADE [이송대상재를 준비스케줄에 등록 - 수동, 크레인설비 등록] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            szRtnMsg = (String)ejbConn.trx("insYdPrepSchNCrnByManual",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);

            szLogMsg = "JSP-FACADE [이송대상재를 준비스케줄에 등록 - 수동, 크레인설비 등록] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of insYdPrepSchNCrnByManual

    /**
     *  후판야드 메뉴얼 작업지시 편성
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData PlateYdSortWrkManualReq(GridData gdReq) throws JDTOException {

        String szMethodName="PlateYdSortWrkManualReq";
        String szLogMsg = "";

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        try{

            szLogMsg = "JSP-FACADE [메뉴얼 작업지시 편성] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);


            JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(gdReq);

            szLogMsg = "JSP-FACADE [SE EJB 호출] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            szLogMsg = "JSP-FACADE [ejbConn.trx] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);


            ejbConn.trx("PlateYdSortWrkManualReq", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            szLogMsg = "JSP-FACADE [cloneResponseGridData] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            //gdRes = CmUtil.copyGDParam(gdReq, gdRes);

            szLogMsg = "JSP-FACADE [메뉴얼 작업지시 편성] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of PlateYdSortWrkManualReq

    /**
     *  후판야드 메뉴얼 작업지시 편성
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData PlateYdManualReq(GridData gdReq) throws JDTOException {

        String szMethodName="PlateYdManualReq";
        String szLogMsg = "";
        String szRtnMsg = null;

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        try{

            szLogMsg = "JSP-FACADE [메뉴얼 작업지시 편성] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);


            JDTORecord[] inRecord =  ydComUtil.genJDTORecordSet(gdReq);

            szLogMsg = "JSP-FACADE [SE EJB 호출] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg = (String)ejbConn.trx("PlateYdManualReq", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            szLogMsg = "JSP-FACADE [cloneResponseGridData] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            //gdRes = CmUtil.copyGDParam(gdReq, gdRes);

            szLogMsg = "JSP-FACADE [메뉴얼 작업지시 편성] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of PlateYdManualReq

    /**
     *  후판야드 긴급재선별작업지시 편성
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData PlateYdUgSelReq(GridData gdReq) throws JDTOException {

        String szMethodName="PlateYdUgSelReq";
        String szLogMsg = "";
        String szRtnMsg = null;

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        try{

            szLogMsg = "JSP-FACADE [긴급재선별작업지시 편성] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);


            JDTORecord[] inRecord =  ydComUtil.genJDTORecordSet(gdReq);

            szLogMsg = "JSP-FACADE [SE EJB 호출] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg = (String)ejbConn.trx("PlateYdUgSelReq", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            szLogMsg = "JSP-FACADE [cloneResponseGridData] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(gdReq);

            szLogMsg = "JSP-FACADE [메뉴얼 작업지시 편성] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of PlateYdUgSelReq

    /**
     *  후판야드 BED정리작업 편성
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData PlateYdBedClReq(GridData gdReq) throws JDTOException {

        String szMethodName="PlateYdBedClReq";
        String szLogMsg = "";
        String szRtnMsg = null;

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        try{

            szLogMsg = "JSP-FACADE [BED정리작업 편성] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord[] inRecord =  ydComUtil.genJDTORecordSet(gdReq);

            szLogMsg = "JSP-FACADE [SE EJB 호출] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg = (String)ejbConn.trx("PlateYdBedClReq", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            szLogMsg = "JSP-FACADE [cloneResponseGridData] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(gdReq);

            szLogMsg = "JSP-FACADE [BED정리작업 편성] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of PlateYdBedClReq


    /**
     * 준비스케줄LIST
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getYdPrepSchList(GridData inDto) throws JDTOException {
        /*
         * 업무기준 : 준비스케줄테이블에서 준비스케줄ID ASC 를 조회
         * 수정자 : 임춘수
         * 수정일 : 2009.09.28
         * 수정자 : 석창화
         * 수정일 : 2009.11.19
         */
        String szMethodName = "getYdPrepSchList";
        String szLogMsg = "";
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        try{

            szLogMsg = "JSP-FACADE [준비스케줄LIST] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getYdPrepSchList", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");

            szLogMsg = "JSP-FACADE [준비스케줄LIST] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getYdPrepSchList

    /**
     * 준비스케줄LIST
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getYdSortWrkPrepSchList(GridData inDto) throws JDTOException {
        /*
         * 업무기준 : 준비스케줄테이블에서 준비스케줄ID ASC 를 조회
         * 수정자 : 임춘수
         * 수정일 : 2009.09.28
         * 수정자 : 석창화
         * 수정일 : 2009.11.19
         */
        String szMethodName = "getYdSortWrkPrepSchList";
        String szLogMsg = "";
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        try{

            szLogMsg = "JSP-FACADE [선별스케줄LIST] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getYdSortWrkPrepSchList", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");

            szLogMsg = "JSP-FACADE [선별스케줄LIST] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getYdPrepSchList


    /**
     * 준비스케줄수정
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData uptYdPrepSch(GridData inDto) throws JDTOException {
        /*
         * 업무기준 : 1. 그리드로 넘겨진 준비스케줄수정
         * 수정자 : 임춘수
         * 수정일 : 2009.09.28
         */
        String szMethodName = "uptYdPrepSch";
        String szRtnMsg     = null;
        String szLogMsg = "";
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        try{

            szLogMsg = "JSP-FACADE [준비스케줄수정] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            szRtnMsg = (String)ejbConn.trx("uptYdPrepSch",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);

            szLogMsg = "JSP-FACADE [준비스케줄수정] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of uptYdPrepSch

    /**
     * 준비스케줄과 준비재료삭제
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData delYdPrepSch(GridData inDto) throws JDTOException {
        /*
         * 업무기준 : 그리드에 선택된 준비스케줄과 준비재료 삭제
         * 수정자 : 임춘수
         * 수정일 : 2009.09.28
         */
        String szMethodName = "delYdPrepSch";
        String szLogMsg = "";
        String szRtnMsg     = null;
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        try{

            szLogMsg = "JSP-FACADE [준비스케줄과 준비재료삭제] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            szRtnMsg = (String)ejbConn.trx("delYdPrepSch",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);

            szLogMsg = "JSP-FACADE [준비스케줄과 준비재료삭제] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of delYdPrepSch

    /**
     * 준비스케줄재료LIST
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getYdPrepSchMtlList(GridData inDto) throws JDTOException {
        /*
         * 업무기준 : 준비스케줄테이블에서 준비스케줄ID ASC 를 조회
         * 수정자 : 임춘수
         * 수정일 : 2009.09.28
         */
        String szMethodName = "getYdPrepSchMtlList";
        String szLogMsg = "";

        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        try{
            szLogMsg = "JSP-FACADE [준비스케줄재료LIST] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getYdPrepSchMtlList", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");

            szLogMsg = "JSP-FACADE [준비스케줄재료LIST] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getYdPrepSchMtlList

    /**
     * 준비재료삭제
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData delYdPrepMtl(GridData inDto) throws JDTOException {
        /*
         * 업무기준 : 그리드에 선택된 준비재료 삭제
         * 수정자 : 임춘수
         * 수정일 : 2009.09.28
         */
        String szMethodName = "delYdPrepMtl";
        String szRtnMsg     = null;
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        try{
            JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            szRtnMsg = (String)ejbConn.trx(szMethodName,
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);
            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of delYdPrepMtl


    /**
     * EVENT별 작업재료 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdEventWorkMatRef(GridData inDto) throws JDTOException {

        String szMethodName="getPlateYdEventWorkMatRef";
        String szLogMsg = "";

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;
        try{

            szLogMsg = "JSP-FACADE [EVENT별 작업재료 조회 ] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdEventWorkMatRef", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");

            szLogMsg = "JSP-FACADE [EVENT별 작업재료 조회 ] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdEventWorkMatRef





    /**
     * 후판제품 PDA 입고제품검수화면 단정보 변경
     * 2009.11.30
     * 권오창
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updChgPlateYdStkLyrNo(GridData inDto) throws JDTOException {
        String szMethodName="updChgPlateYdStkLyrNo";
        String szMsg = "";

        GridData gdRes = null;
        EJBConnector ejbConn = null;

        try{

            szMsg = "JSP-FACADE [저장위치 좌표설정화면 열 수정] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            ejbConn.trx("updChgPlateYdStkLyrNo", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");

            szMsg = "JSP-FACADE [저장위치 좌표설정화면 열 수정] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }


    /**
     * 저장위치별 재고 List
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdStkPosList(GridData inDto) throws JDTOException {

        String szMethodName = "getPlateYdStkPosList";
        String szLogMsg = "";

        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        try{

            szLogMsg = "JSP-FACADE [저장위치별 재고 List] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStkPosList", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            szLogMsg = "JSP-FACADE [저장위치별 재고 List] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdStkPosList

    /**
     * 후판 주문외제품 이송지시 List
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdDmFrList(GridData inDto) throws JDTOException {

        String szMethodName = "getPlateYdDmFrList";
        String szLogMsg = "";

        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        try{

            szLogMsg = "JSP-FACADE [후판 주문외제품 이송지시 List] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdDmFrList", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            szLogMsg = "JSP-FACADE [후판 주문외제품 이송지시 List] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdDmFrList

    /**
     * 후판 주문외제품 이송지시 등록
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData registerPlateYdDmFrList(GridData inDto) throws JDTOException {

        String szMethodName = "registerPlateYdDmFrList";
        String szLogMsg = "";
        String szRtnMsg = "";

        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        try{

            szLogMsg = "JSP-FACADE [후판 주문외제품 이송지시 등록] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord[] inRecord = CmUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg = (String)ejbConn.trx("registerPlateYdDmFrList", new  Class[] { JDTORecord[].class },
                                                                      new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);

            szLogMsg = "JSP-FACADE [후판 주문외제품 이송지시 등록] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of registerPlateYdDmFrList

    /**
     * 후판 주문외제품 이송지시 취소
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData cancelPlateYdDmFrList(GridData inDto) throws JDTOException {

        String szMethodName = "cancelPlateYdDmFrList";
        String szLogMsg = "";
        String szRtnMsg = "";

        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        try{

            szLogMsg = "JSP-FACADE [후판 주문외제품 이송지시 취소] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord[] inRecord = CmUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg = (String)ejbConn.trx("cancelPlateYdDmFrList", new  Class[] { JDTORecord[].class },
                                                                      new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);

            szLogMsg = "JSP-FACADE [후판 주문외제품 이송지시 취소] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of cancelPlateYdDmFrList

    /**
     * 후판 주문외제품 재료별 이송지시 취소
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData modifyPlateYdDmFrList(GridData inDto) throws JDTOException {

        String szMethodName = "modifyPlateYdDmFrList";
        String szLogMsg = "";
        String szRtnMsg = "";

        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        try{

            szLogMsg = "JSP-FACADE [후판 주문외제품  재료별 이송지시 취소] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord[] inRecord = CmUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg = (String)ejbConn.trx("modifyPlateYdDmFrList", new  Class[] { JDTORecord[].class },
                                                                      new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);

            szLogMsg = "JSP-FACADE [후판 주문외제품 재료별 이송지시 취소] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of modifyPlateYdDmFrList

    /**
     * 후판 주문외제품 저장위치별 재료 List
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdDmFrStlList(GridData inDto) throws JDTOException {

        String szMethodName = "getPlateYdDmFrStlList";
        String szLogMsg = "";

        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        try{

            szLogMsg = "JSP-FACADE [후판 주문외제품 저장위치별 재료 List] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdDmFrStlList", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            szLogMsg = "JSP-FACADE [후판 주문외제품 저장위치별 재료 List] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdDmFrStlList

    /**
     * 이송대상재를 준비스케줄에 등록
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData insYdPrepSch(GridData inDto) throws JDTOException {
        /*
         * 업무기준 : 이송지시테이블, 저장품, 적치단, 슬라브공통, 주편공통, 준비스케줄, 준비재료, 작업예약, 작업예약재료
         *           테이블을 조인해서 이송대상재를 조회해서 준비스케줄에 등록
         * 수정자 : 석창화
         * 수정일 : 2009.12.21 (Slab에서 가져옴)
         */
        String szMethodName = "insYdPrepSch";
        String szRtnMsg     = null;
        String szLogMsg = "";
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        try{
            szLogMsg = "JSP-FACADE [이송대상재를 준비스케줄에 등록] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            szRtnMsg = (String) ejbConn.trx("PlateJspSeEJB", "insYdPrepSch", inRecord);
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);

            szLogMsg = "JSP-FACADE [이송대상재를 준비스케줄에 등록] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of insYdPrepSch

    /**
     * 이송대상재를 준비스케줄에 등록 - 수동
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData insYdPrepSchByManualMonitor(GridData inDto) throws JDTOException {
        /*
         * 업무기준 : 그리드에 선택된 대상재를 준비스케줄에 등록
         * 수정자 : 석창화
         * 수정일 : 2009.12.21 (Slab에서 가져옴)
         */
        String szMethodName = "insYdPrepSchByManual";
        String szRtnMsg     = null;
        String szLogMsg = "";
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        try{
            szLogMsg = "JSP-FACADE [이송대상재를 준비스케줄에 등록 - 수동] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            szRtnMsg = (String)ejbConn.trx("insYdPrepSchByManualMonitor",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);

            szLogMsg = "JSP-FACADE [이송대상재를 준비스케줄에 등록 - 수동] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of insYdPrepSchByManualMonitor


    /**
     * 입고작업실적 BACKUP 전송
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData rcptWrkBackUp(GridData inDto) throws JDTOException {
        /*
         * 업무기준 : 그리드에서 넘겨진 정보를 가지고 출하로 전문전송하는기능
         * 수정자 : 이현성
         * 수정일 : 2009.12.21
         */
        String szOperationName = "입고작업실적 BACKUP 전송";
        String szMethodName = "rcptWrkBackUp";
        String szRtnMsg     = null;
        String szLogMsg     = null;
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        try{
            szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

            JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            szRtnMsg = (String)ejbConn.trx("rcptWrkBackUp",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);
            return gdRes;

        }catch(Exception e){
            szLogMsg = "[Jsp Facade : "+szOperationName+"] 오류발생 - 메세지 : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of rcptWrkBackUp



    /**
     * 베드정리작업
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData toBedClear(GridData inDto) throws JDTOException {
        /*
         * 업무기준 : 해당 베드 정보에 적치된 재료들이  다른 저장위치에 잘못등록된곳을 찾아 정리해주는 작업
         * 수정자 : 이현성
         * 수정일 : 2009.12.30
         */
        String szOperationName = "베드정리작업";
        String szMethodName = "toBedClear";
        String szRtnMsg     = null;
        String szLogMsg     = null;
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        try{
            szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

            JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            szRtnMsg = (String)ejbConn.trx("toBedClear",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);
            return gdRes;

        }catch(Exception e){
            szLogMsg = "[Jsp Facade : "+szOperationName+"] 오류발생 - 메세지 : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of toBedClear




    /**
     * 이적실적발생
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData toSendDmTcBakcUp(GridData inDto) throws JDTOException {
        /*
         * 업무기준 :
         * 수정자 : 이현성
         * 수정일 : 2009.12.30
         */
        String szOperationName = "이적실적발생";
        String szMethodName = "toSendDmTcBakcUp";
        String szRtnMsg     = null;
        String szLogMsg     = null;
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        try{
            szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

            JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            szRtnMsg = (String)ejbConn.trx("toSendDmTcBakcUp",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);
            return gdRes;

        }catch(Exception e){
            szLogMsg = "[Jsp Facade : "+szOperationName+"] 오류발생 - 메세지 : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of toSendDmTcBakcUp

    /**
     * 후판제품 스케줄기동
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData trxRunSchedule(GridData gdReq) throws JDTOException {
        // LOG
        String szMsg                    = "";
        String szMethodName             = "trxRunSchedule";
        String szOperationName          = "후판제품 스케줄기동";
        String szRtnMsg                 = null;
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
        String logId                    = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

        szMsg = "후판제품 스케줄기동(PlateJspFaEJBBean." + szMethodName + ") 시작";
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        try {

            szMsg = "[Jsp Facade : "+szOperationName+"] ------------------- 메소드 시작 -------------------";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

            gdRes = OperateGridData.cloneResponseGridData(gdReq);

            JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(gdReq);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// PlateJspSeEJB.trxRunSchedule call 시  inRecord[0] 에 logId SET 추가 개선
inRecord[0].setField("LOG_ID", logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

            szMsg = "[Jsp Facade : "+szOperationName+"] PlateJspSeEJB.trxRunSchedule 호출 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg = (String)ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },    new Object[] { inRecord });

            szMsg = "[Jsp Facade : "+szOperationName+"] PlateJspSeEJB.trxRunSchedule 호출 완료 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);

            szMsg = "[Jsp Facade : "+szOperationName+"] ------------------- 메소드 끝 -------------------";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

////////////////////////////////////////////////////////////////////////////////////////
//2024.09.?? 로그 개선  START
szMsg = "후판제품 스케줄기동(PlateJspFaEJBBean." + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
//2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

            return gdRes;

        } catch (Exception e) {
            szMsg = "[Jsp Facade : "+szOperationName+"] 예외발생 - 메세지 : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } // end of trxRunSchedule

    /**
     * 후판제품 스케줄점검
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData chkCrnSchRunnable(GridData gdReq) throws JDTOException {
        // LOG
        String szMsg                    = "";
        String szMethodName             = "chkCrnSchRunnable";
        String szOperationName          = "후판제품 스케줄점검";
        String szRtnMsg                 = null;

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        try {

            szMsg = "[Jsp Facade : "+szOperationName+"] ------------------- 메소드 시작 -------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(gdReq);

            JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            szMsg = "[Jsp Facade : "+szOperationName+"] PlateJspSeEJB.chkCrnSchRunnable 호출 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRtnMsg = (String)ejbConn.trx("chkCrnSchRunnable", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            szMsg = "[Jsp Facade : "+szOperationName+"] PlateJspSeEJB.chkCrnSchRunnable 호출 완료 - 메세지 : " + szRtnMsg;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            gdRes.setStatus("true");
            gdRes.setMessage(szRtnMsg);

            szMsg = "[Jsp Facade : "+szOperationName+"] ------------------- 메소드 끝 -------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            return gdRes;

        } catch (Exception e) {
            szMsg = "[Jsp Facade : "+szOperationName+"] 예외발생 - 메세지 : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } // end of chkCrnSchRunnable

    /**
     * 후판 사외이송처리
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdMoveMgt(GridData inDto) throws JDTOException {

        String szMethodName = "getPlateYdMoveMgt";
        String szLogMsg = "";

        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        try{

            szLogMsg = "JSP-FACADE [사외이송] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdMoveMgt", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            szLogMsg = "JSP-FACADE [사외이송] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdDmFrStlList

    /**
     * 후판 사외이송 - 후판조업 상찾정보 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdMoveMgtPop(GridData inDto) throws JDTOException {

        String szMethodName = "getPlateYdMoveMgtPop";
        String szLogMsg = "";

        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        try{

            szLogMsg = "JSP-FACADE [사외이송] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdMoveMgtPop", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            szLogMsg = "JSP-FACADE [사외이송] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdMoveMgtPop
    /**
     * 후판 사외이송처리
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdMoveMgt(GridData inDto) throws JDTOException {
        String szMethodName="updPlateYdMoveMgt";
        String szMsg = "";

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();

        try{

            szMsg = "JSP-FACADE [후판 사외이송처리 수정] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
    //SSSS
            //JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            outRecord1  = (JDTORecord)ejbConn.trx("updPlateYdMoveMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage("Success");

            szMsg = "JSP-FACADE [후판 사외이송처리 수정] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 후판 사외이송처리 - KA0101 01베드로 등록처리
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdMoveMgtPop(GridData inDto) throws JDTOException {
        String szMethodName="updPlateYdMoveMgtPop";
        String szMsg = "";

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();

        try{

            szMsg = "JSP-FACADE [후판 사외이송처리 수정] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);

            //JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            outRecord1  = (JDTORecord)ejbConn.trx("updPlateYdMoveMgtPop", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage("Success");

            szMsg = "JSP-FACADE [후판 사외이송처리 수정] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 후판 차량입고 하차처리
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdCarMoveMgt(GridData inDto) throws JDTOException {

        String szMethodName = "getPlateYdCarMoveMgt";
        String szLogMsg = "";

        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        try{

            szLogMsg = "JSP-FACADE [하차처리] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdCarMoveMgt", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            szLogMsg = "JSP-FACADE [하차처리] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdCarMoveMgt

    /**
     * 후판 차량입고 하차처리
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdCarMoveMgt(GridData inDto) throws JDTOException {
        String szMethodName="updPlateYdCarMoveMgt";
        String szMsg = "";

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();

        try{

            szMsg = "JSP-FACADE [후판 차량입고 하차처리  수정] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(inDto);
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            outRecord1  = (JDTORecord)ejbConn.trx("updPlateYdCarMoveMgt", inRecord);

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage("Success");

            szMsg = "JSP-FACADE [후판 차량입고 하차처리  수정] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }
    /**
     * 후판제품야드 저장속성그룹 기존 및 신규그룹 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdStrCharGrpXL(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdStrCharGrpXL";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStrCharGrpXL", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdStrCharGrpXL

    /**
     * 후판제품야드 파일링재고 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdPilingList(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdPilingList";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdPilingList", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdPilingList

    /**
     * 후판제품야드 SIZE 재고 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdSizeStkStat(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdSizeStkStat";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdSizeStkStat", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdSizeStkStat


    /**
     * 후판제품야드 주문재고 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdStkOrdList(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdStkOrdList";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStkOrdList", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdSizeStkStat


    /**
     * 후판제품야드 차상위치 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdCarUppRuleMgt(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdCarUppRuleMgt";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdCarUppRuleMgt", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdSizeStkStat

    /**
     * 후판 차량상차도 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdCarUppRuleMgt1(GridData inDto) throws JDTOException {
        String szMethodName="updPlateYdCarUppRuleMgt1";
        String szMsg = "";

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();

        try{

            szMsg = "JSP-FACADE [후판 차량상차도 수정] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            outRecord1  = (JDTORecord)ejbConn.trx("updPlateYdCarUppRuleMgt1", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage("정상적으로 수정  되었습니다.");

            szMsg = "JSP-FACADE [후판  차량상차도 수정] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 후판 집중입고 알람 매수기준 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateIntensiveIncome(GridData inDto) throws JDTOException {
        String szMethodName="updPlateIntensiveIncome";
        String szMsg = "";

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();

        try{

            szMsg = "JSP-FACADE [후판 집중입고 기준매수 수정] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            outRecord1  = (JDTORecord)ejbConn.trx("updPlateIntensiveIncome", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage("정상적으로 수정  되었습니다.");

            szMsg = "JSP-FACADE [후판 집중입고 기준매수 수정] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 후판 입고크레인할당범위 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdCarUppRuleMgt3(GridData inDto) throws JDTOException {
        String szMethodName="updPlateYdCarUppRuleMgt3";
        String szMsg = "";

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();

        try{

            szMsg = "JSP-FACADE [후판 입고크레인할당범위 수정] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            outRecord1  = (JDTORecord)ejbConn.trx("updPlateYdCarUppRuleMgt3", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage("정상적으로 수정  되었습니다.");

            szMsg = "JSP-FACADE [후판  차량상차도 수정] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }
    /**
     * 1후판 여재 입고예정위치 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdRcptPlnStrLocFor1Plate(GridData inDto) throws JDTOException {
        String szMethodName="updPlateYdRcptPlnStrLocFor1Plate";
        String szMsg = "";

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();

        try{

            szMsg = "JSP-FACADE [1후판 여재 입고예정위치 수정] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            outRecord1  = (JDTORecord)ejbConn.trx("updPlateYdRcptPlnStrLocFor1Plate", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage("정상적으로 수정  되었습니다.");

            szMsg = "JSP-FACADE [1후판 여재 입고예정위치 수정] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 후판 저장위치 변경 화면 단정리 작업
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdStkPosMgtDanZip(GridData inDto) throws JDTOException {

        String szMethodName="updPlateYdStkPosMgtDanZip";
        String szMsg = "";

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();
        String szRtnMsg = "";
        JDTORecord recTemp = null;
        JDTORecord recPara = null;
        JDTORecordSet recordSet = null;
        JDTORecordSet recordSet1 = null;
        JDTORecordSet recordSet2 = null;
        JDTORecordSet recordSet3 = null;
        String szTemp = null;
        int nRtnVal= 0;

        JDTORecordSet rsStock   = null;
        YdStockDao ydStockDao = new YdStockDao();
        YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
        YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();


        try{

            szMsg = "JSP-FACADE [후판 단정리 작업] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(inDto);
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            if(inRecord.length < 1){

                szMsg = "대상재가 없습니다.";
                 gdRes = OperateGridData.cloneResponseGridData(inDto);
                 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                 gdRes.setMessage(szMsg );
                 return gdRes;
            }

                // 저장품에 있는 재료인지 CHECK
            for (int nLoop = 0 ; nLoop < inRecord.length ; nLoop++){
                 recTemp    = JDTORecordFactory.getInstance().create();
                 recTemp    = inRecord[nLoop];
                 szTemp     = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");

                 if(szTemp.trim().equals("")){
                     continue;
                 }

                 recPara  = JDTORecordFactory.getInstance().create();
                 recPara.setField("STL_NO", szTemp);


                 rsStock = JDTORecordFactory.getInstance().createRecordSet("Yd");
                 nRtnVal  = ydStockDao.getYdStock(recPara, rsStock, 0);

                 if(nRtnVal ==0) {
                     szMsg = "저장품에 데이터가 없습니다";
                     ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                     gdRes = OperateGridData.cloneResponseGridData(inDto);
                     gdRes.setMessage( szMsg );
                     return gdRes;
                 }else if(nRtnVal <0){
                     szMsg = "저장품에 조회시 ERROR";
                     gdRes = OperateGridData.cloneResponseGridData(inDto);
                     ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                     gdRes.setMessage(szMsg );
                     return gdRes;
                 }



                //작업예약 재료확인
                 recordSet1 = JDTORecordFactory.getInstance().createRecordSet("Yd");
                 nRtnVal  = ydWrkbookMtlDao.getYdWrkbookmtl(recTemp, recordSet1, 2);

                 if(nRtnVal > 0 ) {
                    //해당정보는 권상재료 정보입니다.
                     szRtnMsg = "해당재료 ["+szTemp+"] 는 작업예약재료 입니다.";
                     ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
                     gdRes = OperateGridData.cloneResponseGridData(inDto);
                     gdRes.setMessage(szRtnMsg);

                     return gdRes;
                 }



                //스케쥴 재료확인
                 recordSet2 = JDTORecordFactory.getInstance().createRecordSet("Yd");
                 recTemp.setField("YD_STK_LYR_MTL_STAT", "U");
                 nRtnVal  = ydStkLyrDao.getYdStklyr(recTemp, recordSet2, 102);

                 if(nRtnVal > 0 ) {
                    //해당정보는 권상재료 정보입니다.
                     szRtnMsg = "해당재료 ["+szTemp+"] 는 크레인작업재료 입니다.";

                     ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
                     gdRes = OperateGridData.cloneResponseGridData(inDto);
                     gdRes.setMessage(szRtnMsg);

                     return gdRes;

                 }


                 recordSet3 = JDTORecordFactory.getInstance().createRecordSet("Yd");
                 recTemp.setField("YD_STK_LYR_MTL_STAT", "D");
                 nRtnVal  = ydStkLyrDao.getYdStklyr(recTemp, recordSet3, 102);

                 if(nRtnVal > 0 ) {
                     szRtnMsg = "해당재료 ["+szTemp+"] 는 크레인작업재료 입니다.";
                     ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
                     gdRes = OperateGridData.cloneResponseGridData(inDto);
                     gdRes.setMessage(szRtnMsg);

                     return gdRes;

                 }
            }


//          ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
//          outRecord1  = (JDTORecord)ejbConn.trx("updPlateYdStkPosFix", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            outRecord1  = (JDTORecord)ejbConn.trx("updPlateYdStkPosMgtDanZip", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage("정상적으로 정리 되었습니다.");

            szMsg = "JSP-FACADE [후판  단정리 작업] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     *  야드크레인 작업관리 (작업취소sjh)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     * @throws JDTOException
     */
    public GridData cancleWorkPlateYdCrnWorkMgt(GridData gdReq) throws JDTOException {

        String szLogMsg           = "";
        String szMethodName       = "cancleWorkPlateYdCrnWorkMgt";
        String szR_msg            = "";
        String szOperationName  = "작업관리 (작업취소)";

        //파라미터 스크링 변수

        String sYD_CRN_SCH_ID   = "";
        String sYD_SCH_CD       = "";
        String sYD_USER_ID      = "";
        JDTORecordSet rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
        JDTORecord recPara      = JDTORecordFactory.getInstance().create();
        JDTORecord recCheck         = JDTORecordFactory.getInstance().create();
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();
        JDTORecord recDelPara       = JDTORecordFactory.getInstance().create();

        String sRTN_CD  = "";
        String sRTN_MSG = "";

        String szJMS_TC_CD  = "";
        String szYD_EQP_ID  = "";
        String szYD_WRK_PROG_STAT = "";
        String szYD_SCH_CD  = "";
        String szRTN_SND    = "N";
        String sCANCEL_SEND = "N";
        YDDataUtil  yddatautil = new YDDataUtil();
        YdCrnSchDao  ydCrnSchDao        = new  YdCrnSchDao();
        GridData gdRes = null;
        EJBConnector ejbConn = null;
        int intRtnVal = 0;
        String szMsg = "";
        String szC_YD_WRK_PROG_STAT = "";
        try{

            szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);


            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            for(int x=0;x<inRecord.length;x++){

                sYD_CRN_SCH_ID  = yddatautil.setDataDefault(inRecord[x].getField("YD_CRN_SCH_ID"), "");
                sYD_SCH_CD      = yddatautil.setDataDefault(inRecord[x].getField("YD_SCH_CD"), "");
                sYD_USER_ID     = yddatautil.setDataDefault(inRecord[x].getField("YD_USER_ID"), "");



                if (sYD_CRN_SCH_ID.equals("")) {

                    szMsg="스케줄 취소 처리("+szMethodName+") 실패, YD_CRN_SCH_ID값이 없음";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    continue;
                }


                //파라미터 레코드 setting
                recPara  = JDTORecordFactory.getInstance().create();
                recPara.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID);
                recPara.setField("YD_SCH_CD",     sYD_SCH_CD);
                recPara.setField("DEL_YN",        "N");
                recPara.setField("MODIFIER",      sYD_USER_ID);
                /*
                 * 크레인 스케줄에서 선택된 스케줄 ID로 작업예약을 조회한 후
                 * 삭제되지 않는 작업예약에 포함된 크레인 스케줄중 첫번째 값이
                 * 2,3 인 경우 후처리를 하지않기 위해  Check Logic 반영
                 */

                rsRtnVal    = JDTORecordFactory.getInstance().createRecordSet("temRs");
                // com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCheckYdCrnSchId
                intRtnVal   = ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 36);

                if (intRtnVal < 1){
                    szMsg = "취소 작업을 완료 하였습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    continue;
                }

                rsRtnVal.first();
                recCheck = rsRtnVal.getRecord();

                szC_YD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recCheck, "YD_WRK_PROG_STAT");

                //2,3, 인 경우 와 혹시모를 4이면서도 스케줄 삭제가 되지 않은 경우
                if(szC_YD_WRK_PROG_STAT.equals("2") || szC_YD_WRK_PROG_STAT.equals("3") || szC_YD_WRK_PROG_STAT.equals("4")){
                    szMsg = "크레인 작업이 완료되지 않았습니다!!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    continue;
                }

                /*
                 * 2,3이 아닌 경우 스케줄 취소기능에  첫번째 크레인 스케줄 ID 정보를 전송
                 */
                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recCheck, "YD_CRN_SCH_ID"));
                recPara.setField("YD_SCH_CD",     ydDaoUtils.paraRecChkNull(recCheck, "YD_SCH_CD"));
                recPara.setField("DEL_YN",        ydDaoUtils.paraRecChkNull(recCheck, "DEL_YN"));
                recPara.setField("MODIFIER",      sYD_USER_ID);

//크레인스케줄 ID보다 이상인 ID 삭제  업데이트 실행(적치단정보까지 CLEAR)
//스케줄취소
//              szMsg = this.PlateSchCncl(recPara);

                szMsg = "스케쥴 취소 시작!!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
                outRecord1  = (JDTORecord)ejbConn.trx("PlateSchCncl", new Class[] { JDTORecord.class }, new Object[] { recPara });

                sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
                sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
                sCANCEL_SEND= StringHelper.evl(outRecord1.getFieldString("CANCEL_SEND"), "N");
                if ("0".equals(sRTN_CD)) {
                    gdRes.setMessage(sRTN_MSG);
                    m_ctx.setRollbackOnly();
                    return gdRes;
                }

                szMsg = "스케쥴 취소 종료!! 작업예약 취소 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
                outRecord1  = (JDTORecord)ejbConn.trx("PlateDelWBook", new Class[] { JDTORecord.class }, new Object[] { outRecord1 });

                szMsg = "작업예약 취소 종료!!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


                sRTN_CD             = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
                szJMS_TC_CD         = StringHelper.evl(outRecord1.getFieldString("MSG_ID"), "");
                szYD_EQP_ID         = StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
                szYD_WRK_PROG_STAT  = StringHelper.evl(outRecord1.getFieldString("YD_WRK_PROG_STAT"), "");
                szYD_SCH_CD         = StringHelper.evl(outRecord1.getFieldString("YD_SCH_CD"), "");
                szRTN_SND           = StringHelper.evl(outRecord1.getFieldString("RTN_SND"), "N");

                if ("0".equals(sRTN_CD)) {
                    gdRes.setMessage(sRTN_MSG);
                    m_ctx.setRollbackOnly();
                    return gdRes;
                }
                if(szRTN_SND.equals("Y") && sCANCEL_SEND.equals("Y")) {

                    YdDelegate ydDelegate = new YdDelegate();

                    szMsg = "[JSP Session : "+szOperationName+"] 크레인 작업지시 정보를 내부QUEUE로 송신 합니다";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    recDelPara   = JDTORecordFactory.getInstance().create();
                    recDelPara.setField("MSG_ID"                , szJMS_TC_CD        );
                    recDelPara.setField("YD_EQP_ID"             , szYD_EQP_ID            );
                    recDelPara.setField("YD_WRK_PROG_STAT"      , szYD_WRK_PROG_STAT);
                    recDelPara.setField("YD_SCH_CD"             , szYD_SCH_CD );
                    ydDelegate.sendMsg(recDelPara);
                }
            }
            szMsg = "JSP-SESSION [ 야드크레인 작업관리 (작업취소) ] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            gdRes.setMessage("정상적으로 취소 처리되었습니다.");

            ydUtils.putLog(szSessionName, szMethodName, szR_msg, YdConstant.INFO);

        }catch(JDTOException de) {

            gdRes.setMessage("Failure");
            szLogMsg = "작업 취소 실패 - DAO Exception ";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);

        }catch(Exception e){
            gdRes.setMessage("Failure");
            szLogMsg = "작업 취소 실패 - JDTOException ";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

        szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
        ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

        return gdRes;
    }

    /**
     *  야드크레인 작업관리 (스케줄 취소:NEW)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     *
     */
    public GridData cancleSchPlateYdCrnWorkMgt(GridData gdReq) throws JDTOException {

        String szMethodName="cancleSchPlateYdCrnWorkMgt";
        String szR_msg ="";
        String szLogMsg = "";
        String szOperationName  = "작업관리 (스케줄 취소)";

        //파라미터 스크링 변수

        String sYD_CRN_SCH_ID   = "";
        String sYD_SCH_CD       = "";
        String sYD_USER_ID      = "";
        JDTORecordSet rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
        JDTORecord recPara      = JDTORecordFactory.getInstance().create();
        JDTORecord recCheck         = JDTORecordFactory.getInstance().create();
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();
        JDTORecord recEqpPara       = JDTORecordFactory.getInstance().create();

        String sRTN_CD  = "";
        String sRTN_MSG = "";

        String szRtnMsg = "";
        String szYD_EQP_ID  = "";
        String sCANCEL_SEND = "N";
        YDDataUtil  yddatautil = new YDDataUtil();
        YdCrnSchDao  ydCrnSchDao        = new  YdCrnSchDao();
        GridData gdRes = null;
        EJBConnector ejbConn = null;
        int intRtnVal = 0;
        String szMsg = "";
        String szC_YD_WRK_PROG_STAT = "";
        try{

            szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);


            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            for(int x=0;x<inRecord.length;x++){

                sYD_CRN_SCH_ID  = yddatautil.setDataDefault(inRecord[x].getField("YD_CRN_SCH_ID"), "");
                sYD_SCH_CD      = yddatautil.setDataDefault(inRecord[x].getField("YD_SCH_CD"), "");
                sYD_USER_ID     = yddatautil.setDataDefault(inRecord[x].getField("YD_USER_ID"), "");

                if (sYD_CRN_SCH_ID.equals("")) {

                    szMsg="스케줄 취소 처리("+szMethodName+") 실패, YD_CRN_SCH_ID값이 없음";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    continue;
                }


                //파라미터 레코드 setting
                recPara  = JDTORecordFactory.getInstance().create();
                recPara.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID);
                recPara.setField("YD_SCH_CD",     sYD_SCH_CD);
                recPara.setField("DEL_YN",        "N");
                recPara.setField("MODIFIER",      sYD_USER_ID);
                /*
                 * 크레인 스케줄에서 선택된 스케줄 ID로 작업예약을 조회한 후
                 * 삭제되지 않는 작업예약에 포함된 크레인 스케줄중 첫번째 값이
                 * 2,3 인 경우 후처리를 하지않기 위해  Check Logic 반영
                 */

                rsRtnVal    = JDTORecordFactory.getInstance().createRecordSet("temRs");
                // com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCheckYdCrnSchId
                intRtnVal   = ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 36);

                if (intRtnVal < 1){
                    szMsg = "취소 작업을 완료 하였습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    continue;
                }

                rsRtnVal.first();
                recCheck = rsRtnVal.getRecord();

                szC_YD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recCheck, "YD_WRK_PROG_STAT");

                //2,3, 인 경우 와 혹시모를 4이면서도 스케줄 삭제가 되지 않은 경우
                if(szC_YD_WRK_PROG_STAT.equals("2") || szC_YD_WRK_PROG_STAT.equals("3") || szC_YD_WRK_PROG_STAT.equals("4")){
                    szMsg = "크레인 작업이 완료되지 않았습니다!!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    continue;
                }

                /*
                 * 2,3이 아닌 경우 스케줄 취소기능에  첫번째 크레인 스케줄 ID 정보를 전송
                 */
                recPara = JDTORecordFactory.getInstance().create();
                recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recCheck, "YD_CRN_SCH_ID"));
                recPara.setField("YD_SCH_CD",     ydDaoUtils.paraRecChkNull(recCheck, "YD_SCH_CD"));
                recPara.setField("DEL_YN",        ydDaoUtils.paraRecChkNull(recCheck, "DEL_YN"));
                recPara.setField("MODIFIER",      sYD_USER_ID);

//크레인스케줄 ID보다 이상인 ID 삭제  업데이트 실행(적치단정보까지 CLEAR)
//스케줄취소
//              szMsg = this.PlateSchCncl(recPara);

                szMsg = "스케쥴 취소 시작!!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
                outRecord1  = (JDTORecord)ejbConn.trx("PlateSchCncl", new Class[] { JDTORecord.class }, new Object[] { recPara });

                sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
                sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
                sCANCEL_SEND= StringHelper.evl(outRecord1.getFieldString("CANCEL_SEND"), "N");
                szYD_EQP_ID = StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");


                if ("0".equals(sRTN_CD)) {
                    gdRes.setMessage(sRTN_MSG);
                    m_ctx.setRollbackOnly();
                    return gdRes;
                }
// 스케쥴 취소는 명령선택 하지 안음
// 취소된거 명령선택될 경우 발생
// 취소전문 송신시 설비 정보 UPDATE

                if(sCANCEL_SEND.equals("Y")) {
                    //--------------------------------------------------------------------------------
                    // 설비가 고장 또는 OFF 라인 상태가 아닐경우
                    // 선택된 설비가 취소 되었으므로 해당설비의 설비 테이블정보에
                    // 작업대기 상태로 UPDATE 해준다.
                    //--------------------------------------------------------------------------------

                    szRtnMsg = YdCommonUtils.checkCrnStat(szYD_EQP_ID);

                    if(szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
                        recEqpPara   = JDTORecordFactory.getInstance().create();
                        recEqpPara.setField("YD_EQP_ID"     , szYD_EQP_ID);
                        recEqpPara.setField("YD_EQP_STAT"   , YdConstant.YD_EQP_STAT_IDLE);
                        recEqpPara.setField("MODIFIER"      ,sYD_USER_ID);

                        szMsg="[Jsp-Session " + szOperationName+ " ] 크레인("+ szYD_EQP_ID +") 설비상태 [" + YdConstant.YD_EQP_STAT_IDLE +"]로 변경 ------------------";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                        EJBConnector ejbConn2 = new EJBConnector("default","SlabJspSeEJB",this);
                        Boolean isSuccess = (Boolean)ejbConn2.trx("RequiresUpdYdEqp",new Class[]{JDTORecord.class}, new Object[]{recEqpPara});
                    }

                }
//              if(sCANCEL_SEND.equals("Y")) {
//
//                  YdDelegate ydDelegate = new YdDelegate();
//
//                  szMsg = "[JSP Session : "+szOperationName+"] 크레인 작업지시 정보를 내부QUEUE로 송신 합니다";
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//                  szYdGp = szYD_EQP_ID.substring(0,1);
//
//                  szLogMsg = "[JSP Session] - 작업예약 삭제 - 크레인 작업지시 : 야드구분[" + szYdGp + "]";
//                  ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//
//                  if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp)  ){                     //C연주 슬라브 야드 [A]
//                      szJMS_TC_CD = "YDYDJ640";
//                  }else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp)){            //A후판 슬라브야드[D]
//                      szJMS_TC_CD = "YDYDJ641";
//                  }else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp)){               //후판제품야드 [K]
//                      szJMS_TC_CD = "YDYDJ642";
//                  }else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)){          //C열연 코일야드[H]
//                      szJMS_TC_CD = "YDYDJ643";
//                  }else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)){           //C열연 제품야드[J]
//                      szJMS_TC_CD = "YDYDJ643";
//                  }   else if(YdConstant.YD_GP_INTGR_YARD.equals(szYdGp)){                    //통합제품야드[S]
//                      szJMS_TC_CD = "YDYDJ644";
//                  }
//
//                  recDelPara   = JDTORecordFactory.getInstance().create();
//                  recDelPara.setField("MSG_ID"                , szJMS_TC_CD        );
//                  recDelPara.setField("YD_EQP_ID"             , szYD_EQP_ID            );
//                  recDelPara.setField("YD_WRK_PROG_STAT"      , YdConstant.YD_EQP_STAT_DN_CMPL);
//                  recDelPara.setField("YD_SCH_CD"             , szYD_SCH_CD );
//                  ydDelegate.sendMsg(recDelPara);
//              }
            }
            szMsg = "JSP-SESSION [ 야드크레인 작업관리 (스케줄 취소) ] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


            gdRes.setMessage("정상적으로 스케줄 취소 처리되었습니다.");


            ydUtils.putLog(szSessionName, szMethodName, szR_msg, YdConstant.INFO);

        }catch(Exception e){
            throw new JDTOException(getClass().getName() + e.getMessage(),e);
        }
        return gdRes;
    }

    /**
     * 후판제품야드 수주 구분 별 저장속성등록 NEW
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData procStkCharInsertByOrdGpNew(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;

        String szMsg = "";
        String szMethodName="procStkCharInsertByOrdGpNew";
        JDTORecord outRecord    = JDTORecordFactory.getInstance().create();
        String sRTN_CD      = "";
        String sRTN_MSG     = "";

        try{
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
//SJH
            outRecord   = (JDTORecord)ejbConn.trx("procStkCharInsertByOrdGpNew", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            sRTN_CD     = StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }
            gdRes.setMessage("정상적으로 등록처리 되었습니다.");
            return gdRes;

        }catch(Exception e){
            szMsg = "[JSP Facade] 오류발생 - " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   //end of procStkCharInsertByOrdGpNew

    /**
     * 1후판제품야드 동별저장계획
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdBayLocPlnMgt(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdBayLocPlnMgt";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdBayLocPlnMgt", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdBayLocPlnMgt

    /**
     * 후판 동별저장계획 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdBayLocPlnMgt(GridData inDto) throws JDTOException {
        String szMethodName="updPlateYdBayLocPlnMgt";
        String szMsg = "";

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();

        try{

            szMsg = "JSP-FACADE [동별저장계획 수정] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            outRecord1  = (JDTORecord)ejbConn.trx("updPlateYdBayLocPlnMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage("정상적으로 수정  되었습니다.");

            szMsg = "JSP-FACADE [후판  동별저장계획 수정] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }
    /**
     * 1후판제품야드 동별저장계획코드
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdBayLocPlnMgtCode(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdBayLocPlnMgtCode";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdBayLocPlnMgtCode", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdBayLocPlnMgtCode

    /**
     * MAP 조회(E,F동 관련)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdStkPosMapSet(GridData inDto) throws JDTOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;
        String szMsg = "";
        String szMethodName="getPlateYdStkPosMapSet";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStkPosMapSet", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdStkPosMapSet

    /**
     *  MAP 조회/수정 화면 수정기능(E,F동 관련)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData updPlateYdStkPosMapSet(GridData gdReq) throws JDTOException {
        //LOG
        String szMsg="";
        String szMethodName="updPlateYdStkPosMapSet";

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("updPlateYdStkPosMapSet",
                        new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            return gdRes;

        }catch(Exception e){
            throw new JDTOException(getClass().getName() + e.getMessage(),e);
        }

    }  //end of updPlateYdStkPosMapSet

    /**
     * MAP 조회 및 수정화면[폭정보 변경]
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdStkPosMapWGp(GridData inDto) throws JDTOException {
        String szMethodName="updPlateYdStkPosMapWGp";
        String szRcvMsg = "";
        String szMsg = "";
        String szOperationName = "MAP 조회 및 수정화면[폭정보 변경]";

        GridData gdRes = null;
        EJBConnector ejbConn = null;

        try{

            szMsg = "[Jsp-Facade "+szOperationName+" ]시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);



            JDTORecord [] inRecord = ydComUtil.genGridToJDTORecord(inDto);
            gdRes = OperateGridData.cloneResponseGridData(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            szRcvMsg = (String)ejbConn.trx("updPlateYdStkPosMapWGp",new Class[] { JDTORecord[].class }, new Object[] { inRecord });


            gdRes.setStatus("true");
            gdRes.setMessage(szRcvMsg);

            szMsg = "[Jsp-Facade "+szOperationName+" ] 끝 ";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }
    /**
     * 후판 차량입고 하차처리(신)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdCarMoveMgtNew(GridData inDto) throws JDTOException {
        String szMethodName="updPlateYdCarMoveMgtNew";
        String szMsg = "";

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();
        JDTORecord inRecord2        = JDTORecordFactory.getInstance().create();

        try{

            szMsg = "JSP-FACADE [후판 차량입고 하차처리  수정] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(inDto);
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

            //-------------------------------------------------------------------------
            //입고 차량스케줄 등록
            outRecord1  = (JDTORecord)ejbConn.trx("updPlateYdCarMoveMgt3G",new Class[] { JDTORecord.class }, new Object[] { inRecord });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage("정상적으로 스케쥴 기동되었습니다.");

            szMsg = "JSP-FACADE [후판 차량입고 하차처리  수정] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 저장위치과부족현황 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdStrPosLackStatsNew(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String szMsg = "";
        String szMethodName="getPlateYdStrPosLackStatsNew";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStrPosLackStatsNew", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }


    /**
     * 후판제품야드 R/T 모니터링(E,F)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */

    public GridData getPlateYdPilingDataChngNew(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdPilingDataChngNew";


        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdPilingDataChngNew", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }
    /**
     * 후판제품야드 크레인 작업 제품단위 list
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdCrnStlNoList(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdCrnStlNoList";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdCrnStlNoList", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdCrnStlNoList

    /**
     * 후판제품야드 권역별 재고 list
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdStrPosAreaStats(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdStrPosAreaStats";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStrPosAreaStats", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdStrPosAreaStats
    /**
     * 후판제품야드 권역별 재고 list 상세
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdStrPosAreaStatsPop(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdStrPosAreaStatsPop";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStrPosAreaStatsPop", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdStrPosAreaStats



    /**
     * 후판제품야드 자동선별 작업지시(선별용)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdSelWrk(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdSelWrk";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdSelWrk", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdSelWrk

    /**
     * 후판제품야드 자동선별 작업지시상세(선별용)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdSelWrkDtl(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdSelWrkDtl";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdSelWrkDtl", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdSelWrkDtl

    /**
     * 후판제품야드 자동선별 동시작 등록(선별용)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdSelWrk(GridData inDto) throws JDTOException {
        String szMethodName="updPlateYdSelWrk";
        String szMsg = "";

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        String sSTATE               = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();
        JDTORecord outRecord2       = JDTORecordFactory.getInstance().create();

        try{


            gdRes = OperateGridData.cloneResponseGridData(inDto);
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            sSTATE  = StringHelper.evl(inRecord.getFieldString("STATE"), "0");

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            outRecord1  = (JDTORecord)ejbConn.trx("updPlateYdSelWrk",new Class[] { JDTORecord.class }, new Object[] { inRecord });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }
            //등록시 procedure 기동
            if(sSTATE.equals("S")) {
                ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
                outRecord2  = (JDTORecord)ejbConn.trx("updPlateYdSelWrkProcDong",new Class[] { JDTORecord.class }, new Object[] { inRecord });

                sRTN_CD     = StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
                sRTN_MSG    = StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");

                if ("0".equals(sRTN_CD)) {
                    gdRes.setMessage(sRTN_MSG);
                    m_ctx.setRollbackOnly();
                    return gdRes;
                }
            }

            gdRes.setStatus("true");

            if(sSTATE.equals("S")) {
                gdRes.setMessage("정상적으로 동별기동 되었습니다.");
            } else {
                gdRes.setMessage("정상적으로 동별기동종료 되었습니다.");
            }

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 후판제품야드 자동선별 동시작 등록(해송 선별용)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdShipSelWrk(GridData inDto) throws JDTOException {
        String szMethodName="updPlateYdShipSelWrk";
        String szMsg = "";

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        String sSTATE               = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();
        JDTORecord outRecord2       = JDTORecordFactory.getInstance().create();

        try{

            gdRes = OperateGridData.cloneResponseGridData(inDto);
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            sSTATE  = StringHelper.evl(inRecord.getFieldString("STATE"), "0");

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            outRecord1  = (JDTORecord)ejbConn.trx("updPlateYdShipSelWrk",new Class[] { JDTORecord.class }, new Object[] { inRecord });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }
            //등록시 procedure 기동
            if(sSTATE.equals("S")) {
                ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
                outRecord2  = (JDTORecord)ejbConn.trx("updPlateYdShipSelWrkProcDong",new Class[] { JDTORecord.class }, new Object[] { inRecord });

                sRTN_CD     = StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
                sRTN_MSG    = StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");

                if ("0".equals(sRTN_CD)) {
                    gdRes.setMessage(sRTN_MSG);
                    m_ctx.setRollbackOnly();
                    return gdRes;
                }
            }

            gdRes.setStatus("true");

            if(sSTATE.equals("S")) {
                gdRes.setMessage("정상적으로 동별기동 되었습니다.");
            } else {
                gdRes.setMessage("정상적으로 동별기동종료 되었습니다.");
            }

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 후판제품야드 선별 작업LIST(선별용)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData getPlateYdSelList(GridData inDto) throws JDTOException {
        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;

        String          szMsg           = "";
        String          szMethodName    ="getPlateYdSelList";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdSelList", inRecord);
            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateYdSelList

    /**
     * 후판  선별 작업LIST 등록(선별용)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdSelList(GridData inDto) throws JDTOException {
        String szMethodName="updPlateYdSelList";
        String szMsg = "";

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        String sYD_USER_ID              = "";
        String sYD_STK_COL_GP           = "";
        String sYD_STK_BED_NO           = "";
        String sCURR_YD_STK_BED_SEL_GP  = "";
        String sNEXT_YD_STK_BED_SEL_GP  = "";
        String sYD_STK_BED_WHIO_STAT    = "";
        String sPROC_FLAG               = "";

        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();
        YDDataUtil  yddatautil = new YDDataUtil();
        try{

            gdRes = OperateGridData.cloneResponseGridData(inDto);
            gdRes = CmUtil.copyGDParam(inDto, gdRes);

            // 일반 DATA + GRID
            JDTORecord [] inRecord = ydComUtil.genGridToJDTORecord(inDto);


            for(int x=0;x<inRecord.length;x++){

                sNEXT_YD_STK_BED_SEL_GP = yddatautil.setDataDefault(inRecord[x].getField("NEXT_YD_STK_BED_SEL_GP"), "");
                sCURR_YD_STK_BED_SEL_GP = yddatautil.setDataDefault(inRecord[x].getField("CURR_YD_STK_BED_SEL_GP"), "");
                sYD_STK_BED_WHIO_STAT   = yddatautil.setDataDefault(inRecord[x].getField("YD_STK_BED_WHIO_STAT"), "");
                sYD_USER_ID     = yddatautil.setDataDefault(inRecord[x].getField("YD_USER_ID"), "");
                sYD_STK_COL_GP  = yddatautil.setDataDefault(inRecord[x].getField("YD_STK_COL_GP"), "");
                sYD_STK_BED_NO  = yddatautil.setDataDefault(inRecord[x].getField("YD_STK_BED_NO"), "");
                sPROC_FLAG      = yddatautil.setDataDefault(inRecord[x].getField("PROC_FLAG"), "");

                if(sCURR_YD_STK_BED_SEL_GP.equals("S") &&
                        (sNEXT_YD_STK_BED_SEL_GP.equals("F")||sNEXT_YD_STK_BED_SEL_GP.equals("N")) ) {


                    JDTORecord      recPara     = JDTORecordFactory.getInstance().create();
                    recPara.setField("NEXT_YD_STK_BED_SEL_GP",  sNEXT_YD_STK_BED_SEL_GP);
                    recPara.setField("CURR_YD_STK_BED_SEL_GP",  sCURR_YD_STK_BED_SEL_GP);
                    recPara.setField("YD_STK_BED_WHIO_STAT",    sYD_STK_BED_WHIO_STAT);
                    recPara.setField("YD_USER_ID",              sYD_USER_ID);
                    recPara.setField("YD_STK_COL_GP",           sYD_STK_COL_GP);
                    recPara.setField("YD_STK_BED_NO",           sYD_STK_BED_NO);
                    recPara.setField("PROC_FLAG",               sPROC_FLAG);


                    ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
                    outRecord1  = (JDTORecord)ejbConn.trx("updPlateYdSelListChk", new Class[] { JDTORecord.class }, new Object[] { recPara });
                    sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
                    sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

                    if ("0".equals(sRTN_CD)) {
                        gdRes.setMessage(sRTN_MSG);
                        m_ctx.setRollbackOnly();
                        return gdRes;
                    }
                }
            }

            for(int x=0;x<inRecord.length;x++){

                sNEXT_YD_STK_BED_SEL_GP = yddatautil.setDataDefault(inRecord[x].getField("NEXT_YD_STK_BED_SEL_GP"), "");
                sCURR_YD_STK_BED_SEL_GP = yddatautil.setDataDefault(inRecord[x].getField("CURR_YD_STK_BED_SEL_GP"), "");
                sYD_STK_BED_WHIO_STAT   = yddatautil.setDataDefault(inRecord[x].getField("YD_STK_BED_WHIO_STAT"), "");
                sYD_USER_ID     = yddatautil.setDataDefault(inRecord[x].getField("YD_USER_ID"), "");
                sYD_STK_COL_GP  = yddatautil.setDataDefault(inRecord[x].getField("YD_STK_COL_GP"), "");
                sYD_STK_BED_NO  = yddatautil.setDataDefault(inRecord[x].getField("YD_STK_BED_NO"), "");
                sPROC_FLAG      = yddatautil.setDataDefault(inRecord[x].getField("PROC_FLAG"), "");

                JDTORecord      recPara     = JDTORecordFactory.getInstance().create();
                recPara.setField("NEXT_YD_STK_BED_SEL_GP",  sNEXT_YD_STK_BED_SEL_GP);
                recPara.setField("CURR_YD_STK_BED_SEL_GP",  sCURR_YD_STK_BED_SEL_GP);
                recPara.setField("YD_STK_BED_WHIO_STAT",    sYD_STK_BED_WHIO_STAT);
                recPara.setField("YD_USER_ID",              sYD_USER_ID);
                recPara.setField("YD_STK_COL_GP",           sYD_STK_COL_GP);
                recPara.setField("YD_STK_BED_NO",           sYD_STK_BED_NO);
                recPara.setField("PROC_FLAG",               sPROC_FLAG);

                ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
                outRecord1  = (JDTORecord)ejbConn.trx("updPlateYdSelList", new Class[] { JDTORecord.class }, new Object[] { recPara });
                sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
                sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

                if ("0".equals(sRTN_CD)) {
                    gdRes.setMessage(sRTN_MSG);
                    m_ctx.setRollbackOnly();
                    return gdRes;
                }
            }

            gdRes.setStatus("true");
            gdRes.setMessage("정상적으로 등록되었습니다.");

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

    /**
     * 후판 선별 작업 전체 출하송신
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdSelListAll(GridData inDto) throws JDTOException {

        String szMethodName="updPlateYdSelListAll";
        String szMsg = "";
        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        String sSTATE               = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();
        JDTORecord outRecord2       = JDTORecordFactory.getInstance().create();

        try{

            gdRes = OperateGridData.cloneResponseGridData(inDto);
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            outRecord2 = (JDTORecord)ejbConn.trx("updPlateYdSelListAll", new Class[] { JDTORecord.class }, new Object[] { inRecord });
            sRTN_CD     = StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage("정상적으로 선별작업편성 되었습니다.");

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 후판 선별 작업LIST Procr기동(선별용)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdSelListProcYd(GridData inDto) throws JDTOException {

        String szMethodName="updPlateYdSelListProcYd";
        String szMsg = "";
        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        String sSTATE               = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();
        JDTORecord outRecord2       = JDTORecordFactory.getInstance().create();

        try{

            gdRes = OperateGridData.cloneResponseGridData(inDto);
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            sSTATE  = StringHelper.evl(inRecord.getFieldString("STATE"), "0");

//          ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
//          outRecord1  = (JDTORecord)ejbConn.trx("updPlateYdSelListStaEndFlag",new Class[] { JDTORecord.class }, new Object[] { inRecord });
//          sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//          sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

//          if ("0".equals(sRTN_CD)) {
//              gdRes.setMessage(sRTN_MSG);
//              m_ctx.setRollbackOnly();
//              return gdRes;
//          }
            //등록시 procedure 기동
            if(sSTATE.equals("S")) {
                ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
                outRecord2 = (JDTORecord)ejbConn.trx("updPlateYdSelListProcYd", new Class[] { JDTORecord.class }, new Object[] { inRecord });
                sRTN_CD     = StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
                sRTN_MSG    = StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");

                if ("0".equals(sRTN_CD)) {
                    gdRes.setMessage(sRTN_MSG);
                    m_ctx.setRollbackOnly();
                    return gdRes;
                }

            }
            gdRes.setStatus("true");

            if(sSTATE.equals("S")) {
                gdRes.setMessage("정상적으로 선별기동 되었습니다.");
            } else {
                gdRes.setMessage("정상적으로 선별종료 되었습니다.");
            }


            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 후판 선별 작업LIST Procr기동(해송 선별용)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdShipSelListProcYd(GridData inDto) throws JDTOException {

        String szMethodName="updPlateYdShipSelListProcYd";
        String szMsg = "";
        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        String sSTATE               = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();
        JDTORecord outRecord2       = JDTORecordFactory.getInstance().create();

        try{

            gdRes = OperateGridData.cloneResponseGridData(inDto);
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
            sSTATE  = StringHelper.evl(inRecord.getFieldString("STATE"), "0");

            if(sSTATE.equals("S")) {
                ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
                outRecord2 = (JDTORecord)ejbConn.trx("updPlateYdShipSelListProcYd", new Class[] { JDTORecord.class }, new Object[] { inRecord });
                sRTN_CD     = StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
                sRTN_MSG    = StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");

                if ("0".equals(sRTN_CD)) {
                    gdRes.setMessage(sRTN_MSG);
                    m_ctx.setRollbackOnly();
                    return gdRes;
                }

            }
            gdRes.setStatus("true");

            if(sSTATE.equals("S")) {
                gdRes.setMessage("정상적으로 선별기동 되었습니다.");
            } else {
                gdRes.setMessage("정상적으로 선별종료 되었습니다.");
            }


            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 저장위치 좌표설정화면 베드  조회
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateYdStkPosSetBedNew(GridData inDto) throws JDTOException {
        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;
        String          szMethodName  = "getPlateYdStkPosSetBedNew";
        String          szMsg         = "";
        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);

            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStkPosSetBedNew", inRecord);

            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg =  e.getMessage() ;
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }
    /**
     * 저장위치 좌표설정화면 BED 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdStkPosSetBedNew(GridData inDto) throws JDTOException {
        GridData        gdRes           = null;
        EJBConnector    ejbConn         = null;
        String[]        szRtnMsg        = null;
        String          szMethodName    = "updPlateYdStkPosSetBed";
        String          rtnMsg          = YdConstant.RETN_CD_SUCCESS;
        String          szMsg           = "";

        try{
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

            if(inRecord.length > 0) {
                ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);

                ejbConn.trx("updPlateYdStkPosSetBedNew", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            }

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            gdRes.setStatus("true");
            gdRes.setMessage(rtnMsg);
            return gdRes;
        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName , e.getMessage() , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }

    /**
     * 가적BED 북인처리
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws DAOException
     */

    public GridData getPlateYdTempLocReg(GridData inDto) throws DAOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;
        String          szMethodName    = "getPlateYdTempLocReg";
        String          szMsg           = "";

        try{
            JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", this);

            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdTempLocReg", inRecord);

            gdRes = CmUtil.genGridData(inDto , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new DAOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdTempLocReg

    /**
     *  가적BED 북인 C/R 작업
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws DAOException
     */

    public GridData updPlateYdTempLocReg(GridData inDto) throws DAOException {
        //LOG
        String szMsg="";
        String szMethodName="updPlateYdTempLocReg";

        GridData gdRes = null;
        EJBConnector ejbConn = null;

        try{
            JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecord(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("updPlateYdTempLocReg", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            gdRes = OperateGridData.cloneResponseGridData(inDto);
            gdRes = CmUtil.copyGDParam(inDto, gdRes);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new DAOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of updPlateYdTempLocReg
    /**
     * 후판 차량상차도 수정
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws DAOException
     */
    public GridData updPlateYdCarUppRuleMgt2(GridData inDto) throws DAOException {
        String szMethodName="updPlateYdCarUppRuleMgt2";
        String szMsg = "";

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();

        try{

            szMsg = "JSP-FACADE [후판 차량상차도 수정] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            outRecord1  = (JDTORecord)ejbConn.trx("updPlateYdCarUppRuleMgt2", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage("정상적으로 수정  되었습니다.");

            szMsg = "JSP-FACADE [후판  차량상차도 수정] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new DAOException(getClass().getName() + e.getMessage(), e);
        }

    }

    //----------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------


    /**
     * 일관제철 3기 - 후판제품창고  야드MAP관리 - MAP 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inGridData
     * @return GridData
     * @throws DAOException
     */

    public GridData getPlateYdStkPosMapSet3G(GridData inGridData) throws DAOException {

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        JDTORecordSet recordSet = null;
        String szMsg = "";
        String szMethodName="getPlateYdStkPosMapSet3G";
        String szYD_GP;
        String szYD_COL_W_GP;
        String szYD_MILE;
        String szSwMaxVal;
        String szMwMaxVal;
        String szLwMaxVal;
        String szShMaxVal;
        String szMhMaxVal;
        String szLhMaxVal;
        String szUlMaxVal;
        String szSlMaxVal;
        String szMlMaxVal;
        String szLlMaxVal;
        String szXlMaxVal;
        String szU_MILE;
        String szS_MILE;
        String szM_MILE;
        String szL_MILE;
        String szX_MILE;


        try{
            //Grid date 를 JDTORecord data 로 변환
            JDTORecord inRecord = CmUtil.genJDTORecord(inGridData);

            //-------------------------------------------------------------------------------------------------
            //업무기준 : YDB671 (후판제품 SPAN별 야드구분 변환기준)
            if( GetBreRule6.getYDB671(inRecord) ) {
                szYD_GP         = StringHelper.evl(inRecord.getFieldString("YDB671_RV01_YD_GP"), "T");          // 업무기준 YDB671 반환값#1 야드구분
                szYD_COL_W_GP   = StringHelper.evl(inRecord.getFieldString("YDB671_RV02_YD_COL_W_GP"), "0000"); // 업무기준 YDB671 반환값#2 야드적치열폭구분
                szYD_MILE       = StringHelper.evl(inRecord.getFieldString("YDB671_RV03_YD_MILE"), "-1");       // 업무기준 YDB671 반환값#3 거리

                //SE EJB에서 필요한 업무기준 반환 값 JDTORecord에 설정한다.
                inRecord.setField("YD_GP", szYD_GP);

            } else {
                throw new Exception(getClass().getName() + "업무기준 YDB671 조회 실패 ! ");
            }

            //업무기준 : YDB672 (후판제품야드 제품 폭 MIN MAX 값 기준)
            inRecord.setField("YD_STK_BED_W_GP","S"); //협폭
            if( GetBreRule6.getYDB672(inRecord) ) {
                szSwMaxVal  = StringHelper.evl(inRecord.getFieldString("YDB672_RV02_W_MAX_VAL"), "0");  // 업무기준 YDB672 반환값#2 폭 MAX VAL
                szShMaxVal  = StringHelper.evl(inRecord.getFieldString("YDB672_RV03_COL_H_MAX"), "0");  // 업무기준 YDB671 반환값#3 열 높이 MAX
            } else {
                throw new Exception(getClass().getName() + "업무기준 YDB672 조회 실패 ! ");
            }

            inRecord.setField("YD_STK_BED_W_GP","M"); //중폭
            if( GetBreRule6.getYDB672(inRecord) ) {
                szMwMaxVal  = StringHelper.evl(inRecord.getFieldString("YDB672_RV02_W_MAX_VAL"), "0");  // 업무기준 YDB672 반환값#2 폭 MAX VAL
                szMhMaxVal  = StringHelper.evl(inRecord.getFieldString("YDB672_RV03_COL_H_MAX"), "0");  // 업무기준 YDB671 반환값#3 열 높이 MAX
            } else {
                throw new Exception(getClass().getName() + "업무기준 YDB672 조회 실패 ! ");
            }

            inRecord.setField("YD_STK_BED_W_GP","L"); //광폭
            if( GetBreRule6.getYDB672(inRecord) ) {
                szLwMaxVal  = StringHelper.evl(inRecord.getFieldString("YDB672_RV02_W_MAX_VAL"), "0");  // 업무기준 YDB672 반환값#2 폭 MAX VAL
                szLhMaxVal  = StringHelper.evl(inRecord.getFieldString("YDB672_RV03_COL_H_MAX"), "0");  // 업무기준 YDB671 반환값#3 열 높이 MAX
            } else {
                throw new Exception(getClass().getName() + "업무기준 YDB672 조회 실패 ! ");
            }

            //업무기준 : YDB673 (후판제품야드 제품 길이 MIN, MAX 기준)
            inRecord.setField("YD_STK_BED_L_GP","U"); //초단척
            if( GetBreRule6.getYDB673(inRecord) ) {
                szUlMaxVal  = StringHelper.evl(inRecord.getFieldString("YDB673_RV02_L_MAX_VAL"), "0");  // 업무기준 YDB672 반환값#2 길이 MAX VAL
                szU_MILE    = StringHelper.evl(inRecord.getFieldString("YDB673_RV03_L_MILE"), "-1");    // 업무기준 YDB673 반환값#3 거리
            } else {
                //throw new Exception(getClass().getName() + "업무기준 YDB673 조회 실패 ! ");
                szUlMaxVal = "6800";
                szU_MILE   = "400";
            }

            inRecord.setField("YD_STK_BED_L_GP","S"); //단척
            if( GetBreRule6.getYDB673(inRecord) ) {
                szSlMaxVal  = StringHelper.evl(inRecord.getFieldString("YDB673_RV02_L_MAX_VAL"), "0");  // 업무기준 YDB672 반환값#2 길이 MAX VAL
                szS_MILE    = StringHelper.evl(inRecord.getFieldString("YDB673_RV03_L_MILE"), "-1");    // 업무기준 YDB673 반환값#3 거리
            } else {
                throw new Exception(getClass().getName() + "업무기준 YDB673 조회 실패 ! ");
            }

            inRecord.setField("YD_STK_BED_L_GP","M"); //중척
            if( GetBreRule6.getYDB673(inRecord) ) {
                szMlMaxVal  = StringHelper.evl(inRecord.getFieldString("YDB673_RV02_L_MAX_VAL"), "0");  // 업무기준 YDB672 반환값#2 길이 MAX VAL
                szM_MILE    = StringHelper.evl(inRecord.getFieldString("YDB673_RV03_L_MILE"), "-1");    // 업무기준 YDB673 반환값#3 거리
            } else {
                throw new Exception(getClass().getName() + "업무기준 YDB673 조회 실패 ! ");
            }

            inRecord.setField("YD_STK_BED_L_GP","L"); //장척
            if( GetBreRule6.getYDB673(inRecord) ) {
                szLlMaxVal  = StringHelper.evl(inRecord.getFieldString("YDB673_RV02_L_MAX_VAL"), "0");  // 업무기준 YDB672 반환값#2 길이 MAX VAL
                szL_MILE    = StringHelper.evl(inRecord.getFieldString("YDB673_RV03_L_MILE"), "-1");    // 업무기준 YDB673 반환값#3 거리
            } else {
                throw new Exception(getClass().getName() + "업무기준 YDB673 조회 실패 ! ");
            }

            inRecord.setField("YD_STK_BED_L_GP","X"); //초장척
            if( GetBreRule6.getYDB673(inRecord) ) {
                szXlMaxVal  = StringHelper.evl(inRecord.getFieldString("YDB673_RV02_L_MAX_VAL"), "0");  // 업무기준 YDB672 반환값#2 길이 MAX VAL
                szX_MILE    = StringHelper.evl(inRecord.getFieldString("YDB673_RV03_L_MILE"), "-1");    // 업무기준 YDB673 반환값#3 거리
            } else {
                throw new Exception(getClass().getName() + "업무기준 YDB673 조회 실패 ! ");
            }

            //-------------------------------------------------------------------------------------------------
            //SE EJB 호출
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStkPosMapSet3G", inRecord);

            //UI로 반환 할 Grid data 를 생성
            gdRes = CmUtil.genGridData(inGridData , recordSet);

            //-------------------------------------------------------------------------------------------------
            //UI에서 필요한 업무기준 반환 값을 Grid에 addParam 한다.
            gdRes.addParam("YDB671_RV01_YD_GP",szYD_GP);
            gdRes.addParam("YDB671_RV02_YD_COL_W_GP",szYD_COL_W_GP);
            gdRes.addParam("YDB671_RV03_YD_MILE",szYD_MILE);

            gdRes.addParam("YDB672_RV02_S_W_MAX_VAL",szSwMaxVal);
            gdRes.addParam("YDB672_RV02_M_W_MAX_VAL",szMwMaxVal);
            gdRes.addParam("YDB672_RV02_L_W_MAX_VAL",szLwMaxVal);

            gdRes.addParam("YDB672_RV03_S_COL_H_MAX",szShMaxVal);
            gdRes.addParam("YDB672_RV03_M_COL_H_MAX",szMhMaxVal);
            gdRes.addParam("YDB672_RV03_L_COL_H_MAX",szLhMaxVal);

            gdRes.addParam("YDB673_RV02_L_GP_U_MAX_VAL",szUlMaxVal);
            gdRes.addParam("YDB673_RV02_L_GP_S_MAX_VAL",szSlMaxVal);
            gdRes.addParam("YDB673_RV02_L_GP_M_MAX_VAL",szMlMaxVal);
            gdRes.addParam("YDB673_RV02_L_GP_L_MAX_VAL",szLlMaxVal);
            gdRes.addParam("YDB673_RV02_L_GP_X_MAX_VAL",szXlMaxVal);

            gdRes.addParam("YDB673_RV02_L_GP_U_MILE",szU_MILE);
            gdRes.addParam("YDB673_RV02_L_GP_S_MILE",szS_MILE);
            gdRes.addParam("YDB673_RV02_L_GP_M_MILE",szM_MILE);
            gdRes.addParam("YDB673_RV02_L_GP_L_MILE",szL_MILE);
            gdRes.addParam("YDB673_RV02_L_GP_X_MILE",szX_MILE);


            gdRes.setStatus("true");
            gdRes.setMessage("Success");

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new DAOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdStkPosMapSet3G

    /**
     *  일관제철 3기 - 후판제품창고  야드MAP관리 - 수정기능
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws DAOException
     */
    public GridData updPlateYdStkPosMapSet3G(GridData gdReq) throws DAOException {
        //LOG
        String szMsg="";
        String szMethodName="updPlateYdStkPosMapSet3G";

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        try{
            //Grid date 를 JDTORecord data 로 변환
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            //SE EJB 호출
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("updPlateYdStkPosMapSet3G",
                    new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            //UI로 반환 할 Grid data 를 생성
            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new DAOException(getClass().getName() + e.getMessage(),e);
        }

    }  //end of updPlateYdStkPosMapSet3G


    /**
     *  후판제품창고  선박명 수정기능
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws DAOException
     */
    public GridData updPlateShipName(GridData gdReq) throws DAOException {
        //LOG
        String szMsg="";
        String szMethodName="updPlateShipName";

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        try{
            //Grid date 를 JDTORecord data 로 변환
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            //SE EJB 호출
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("updPlateShipName",
                    new Class[] { JDTORecord[].class }, new Object[] { inRecord });

            //UI로 반환 할 Grid data 를 생성
            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new DAOException(getClass().getName() + e.getMessage(),e);
        }

    }  //end of updPlateShipName


    /**
     * 일관제철 3기 - 저장위치별 재고 List - 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inGridData
     * @return GridData
     * @throws DAOException
     */
    public GridData getPlateYdStkPosList3G(GridData inGridData) throws DAOException {

        String szMethodName = "getPlateYdStkPosList3G";

        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        try{

            //Grid date 를 JDTORecord data 로 변환
            JDTORecord inRecord = CmUtil.genJDTORecord(inGridData);

            //-------------------------------------------------------------------------------------------------
            //SE EJB 호출
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateYdStkPosList3G", inRecord);

            //UI로 반환 할 Grid data 를 생성
            gdRes = CmUtil.genGridData(inGridData , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new DAOException(getClass().getName() + e.getMessage(), e);
        }

    } //end of getPlateYdStkPosList3G

    /**
     * 일관제철 3기 - 1차저장계획등록 - 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inGridData
     * @return GridData
     * @throws DAOException
     */
    public GridData getPlateOsInfoForOrdRcptPln3G(GridData inGridData) throws DAOException {

        String szMethodName = "getPlateOsInfoForOrdRcptPln3G";

        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        try{

            //Grid date 를 JDTORecord data 로 변환
            JDTORecord inRecord = CmUtil.genJDTORecord(inGridData);

            //-------------------------------------------------------------------------------------------------
            //SE EJB 호출
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getPlateOsInfoForOrdRcptPln3G", inRecord);

            //UI로 반환 할 Grid data 를 생성
            gdRes = CmUtil.genGridData(inGridData , recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new DAOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getPlateOsInfoForOrdRcptPln3G

    /**
     * 일관제철 3기 - GridData - 단순 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inGridData
     * @return GridData
     * @throws DAOException
     */
    public GridData getGridData(GridData inGridData) throws DAOException {

        String szMethodName = "getGridData";

        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        try{

            //Grid date 를 JDTORecord data 로 변환
            JDTORecord inRecord = CmUtil.genJDTORecord(inGridData);

            //SE EJB 호출
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getGridData", inRecord);

            //UI로 반환 할 Grid data 를 생성
            gdRes = CmUtil.genGridData(inGridData, recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new DAOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getGridData

    /**
     * 일관제철 3기 - GridData - 단순 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inGridData
     * @return GridData
     * @throws DAOException
     */
    public GridData getGridData2(GridData inGridData) throws DAOException {

        String szMethodName = "getGridData2";

        GridData      gdRes     = null;
        EJBConnector  ejbConn   = null;
        JDTORecordSet recordSet = null;
        try{

            //Grid date 를 JDTORecord data 로 변환
            JDTORecord inRecord = CmUtil.genJDTORecord(inGridData);

            //SE EJB 호출
            ejbConn = new EJBConnector("default", this);
            recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "getGridData", inRecord);

            //UI로 반환 할 Grid data 를 생성
            gdRes = CmUtil.genGridData(inGridData, recordSet);

            gdRes.setStatus("true");
            gdRes.setMessage("Success");

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
            throw new DAOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of getGridData2

    /**
     *  일관제철 3기 - GridData - 단순 Update
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inGridData
     * @return GridData
     * @throws DAOException
     */
    public GridData updGridData(GridData inGridData) throws DAOException {
        //LOG
        String szMsg="";
        String szMethodName="updGridData";

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        try{
            //Grid date 를 JDTORecord data 로 변환
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inGridData);
            JDTORecord inRecord2 = CmUtil.genJDTORecord(inGridData);

            String szQueryId = inRecord2.getFieldString("QUERY_ID");

            szMsg = ">>>>>>>>>>>>>>>>>>>>QUERY_ID:" + szQueryId;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            //SE EJB 호출
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("updGridData",  new Class[] { JDTORecord[].class , String.class }
                                     , new Object[] { inRecord , szQueryId});

            //UI로 반환 할 Grid data 를 생성
            gdRes = OperateGridData.cloneResponseGridData(inGridData);
            gdRes = CmUtil.copyGDParam(inGridData, gdRes);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new DAOException(getClass().getName() + e.getMessage(),e);
        }


    }  //end of updGridData

    /**
     *  일관제철 3기 - 기존 설비고장복구실적 휴지테이블 처리 호출
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws DAOException
     */
    public GridData callProcEqpPause3G(GridData gdReq) throws DAOException {
        //LOG
        String szMsg="";
        String szMethodName="callProcEqpPause3G";

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        try{
            //Grid date 를 JDTORecord data 로 변환
            JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
            String strRcvTcCode             = inRecord.getFieldString("RCV_TC_CODE");
            String szYD_EQP_ID              = inRecord.getFieldString("YD_EQP_ID");
            String szYD_EQP_STAT_UPD        = null;
            String szYD_EQP_PAUSE_CODE      = inRecord.getFieldString("YD_EQP_PAUSE_CODE");
            String szYD_EQP_TRBL_RCVR_DT_FR = inRecord.getFieldString("YD_EQP_TRBL_RCVR_DT_FR");
            String szYD_EQP_TRBL_RCVR_DT_TO = inRecord.getFieldString("YD_EQP_TRBL_RCVR_DT_TO");

            //SE EJB 호출
            ejbConn = new EJBConnector("default", "EqpTrackingSeEJB", this);

            szYD_EQP_STAT_UPD = "B";
            ejbConn.trx("ProcEqpPause",new Class[]{ String.class,String.class,String.class,String.class,String.class}
             ,new Object[]{ strRcvTcCode,szYD_EQP_ID,szYD_EQP_STAT_UPD,szYD_EQP_PAUSE_CODE,szYD_EQP_TRBL_RCVR_DT_FR});


            szYD_EQP_STAT_UPD = "R";
            ejbConn.trx("ProcEqpPause",new Class[]{ String.class,String.class,String.class,String.class,String.class}
             ,new Object[]{ strRcvTcCode,szYD_EQP_ID,szYD_EQP_STAT_UPD,szYD_EQP_PAUSE_CODE,szYD_EQP_TRBL_RCVR_DT_TO});

            //UI로 반환 할 Grid data 를 생성
            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
            throw new DAOException(getClass().getName() + e.getMessage(),e);
        }

    }  //end of updPlateYdStkPosMapSet3G

    /**
     * 2후판 #2D/S B동 크레인 파일링기능 셋팅
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData callProcBCraneEqpSch(GridData gdReq) throws JDTOException {

        YdSlabUtils slabUtils = new YdSlabUtils();

        //LOG
        String szMethodName = "[PlateJspFaEJBBean.callProcBCraneEqpSch]";
        String logId = slabUtils.getLogId();
        String szLogMsg = "";

        try {
            JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);

            szLogMsg = "JSP-FACADE [ " + szMethodName +"] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            EJBConnector ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("callProcBCraneEqpSch", new Class[] { JDTORecord.class }, new Object[] { inRecord });

            GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);

            szLogMsg = "JSP-FACADE [ " + szMethodName +"] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));
        }
    } //callProcBCraneEqpSch

    /**
     * 1후판 #F동 56020존 파일링 대상 크레인 선택
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData callProcACraneSelect(GridData gdReq) throws JDTOException {

        YdSlabUtils slabUtils = new YdSlabUtils();

        //LOG
        String szMethodName = "[PlateJspFaEJBBean.callProcACraneSelect]";
        String logId = slabUtils.getLogId();
        String szLogMsg = "";

        try {
            JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);

            szLogMsg = "JSP-FACADE [ " + szMethodName +"] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            EJBConnector ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("callProcACraneSelect", new Class[] { JDTORecord.class }, new Object[] { inRecord });

            GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);

            szLogMsg = "JSP-FACADE [ " + szMethodName +"] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));
        }
    } //callProcACraneSelect

    /**
     * 출고검수 - 육송출하고도화
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData distCheckProc(GridData inDto) throws JDTOException {
        GridData gdRes          = null;
        EJBConnector ejbConn    = null;
        String[] szRtnMsg       = null;

        String szMethodName  = "distCheckProc";
        String szMsg         = "";
        String rtnMsg        = YdConstant.RETN_CD_SUCCESS;

        try{

            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
            szMsg = "[JSP Facade] [" + szMethodName + "] inRecord.length :" + inRecord.length ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            if(inRecord.length > 0) {

                ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
//              szRtnMsg = (String[])ejbConn.trx("updPlateYdStkPosSet", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
                // 넘어온 return message 중 성공 못한 message가 있을 경우 해당 message를 return
//              for(int Loop_i = 0; Loop_i < szRtnMsg.length; Loop_i++ ) {
//                  szMsg = "[JSP Facade] [" + Loop_i + "] 번째 처리 RETURN VALUE [ " + szRtnMsg[Loop_i] + " ]\n";
//                  ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//                  if( !szRtnMsg[Loop_i].equals(YdConstant.RETN_CD_SUCCESS) ) {
//                      rtnMsg = szRtnMsg[Loop_i];
//                      break;
//                  }
//              }

                ejbConn.trx("distCheckProc", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
            }

            gdRes = OperateGridData.cloneResponseGridData(inDto);
            gdRes.setStatus("true");
            gdRes.setMessage(rtnMsg);
            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName,szMsg , YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }
    }   //end of distCheckProc

    /**
     * 후판제품야드 차량이송실적등록
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData regCarFrmvWr(GridData gdReq) throws JDTOException {

        YdSlabUtils slabUtils = new YdSlabUtils();

        //LOG
        String szMethodName = "[PlateJspFaEJBBean.regCarFrmvWr]";
        String logId = slabUtils.getLogId();
        String szLogMsg = "";

        try {

            szLogMsg = "JSP-FACADE [ " + szMethodName +"] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            EJBConnector ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("regCarFrmvWr", new Class[] { GridData.class }, new Object[] { gdReq });

            GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);

            szLogMsg = "JSP-FACADE [ " + szMethodName +"] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));
        }
    } //regCarFrmvWr

    /**
     * 후판제품야드 검수차량정보 삭제
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData delCarFrmvWr(GridData gdReq) throws JDTOException {

        YdSlabUtils slabUtils = new YdSlabUtils();

        //LOG
        String szMethodName = "[PlateJspFaEJBBean.delCarFrmvWr]";
        String logId = slabUtils.getLogId();
        String szLogMsg = "";

        try {

            szLogMsg = "JSP-FACADE [ " + szMethodName +"] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            EJBConnector ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("delCarFrmvWr", new Class[] { GridData.class }, new Object[] { gdReq });

            GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);

            szLogMsg = "JSP-FACADE [ " + szMethodName +"] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            return gdRes;

        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));
        }
    } //delCarFrmvWr


    ///////////////////////////////////////////////////////////////////////////////
    ///                          전사물류개선 프로젝트 2021.1.6                  ///
    ///////////////////////////////////////////////////////////////////////////////


    /**
     * IFTest EAI전송
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData
     * @return GridData
     * @throws DAOException
     */
    public GridData sndIfTest(GridData gdReq) throws DAOException {
        String methodNm =  "IFTest EAI전송[PlateJspFaEJB.sndIfTest]";

        YdSlabUtils slabUtils = new YdSlabUtils();
        String logId = slabUtils.getLogId();//(YmConstant.YD_GP_2);
//      GridData gdRes = null;

        try {

            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
            slabUtils.printLog(logId, methodNm, "F+", gdReq);


            EJBConnector ejbConn = new EJBConnector("default", "PlateJspSeEJB", this); //BSlabJspSeEJB

            //IFTest EAI전송
            ejbConn.trx("sndIfTest", new Class[] { GridData.class }, new Object[] { gdReq });
//          gdRes = OperateGridData.cloneResponseGridData(gdReq);
            slabUtils.printLog(logId, methodNm, "F-");

            //조회결과
            return gdReq;

        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }

    }   // end of sndIfTestEAI

    /**
     * IFTest EAI전송
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData
     * @return GridData
     * @throws DAOException
     */
    public GridData sndIfTestEAI(GridData gdReq) throws DAOException {

        YdSlabUtils slabUtils = new YdSlabUtils();

        String methodNm =  "IFTest EAI전송[PlateJspFaEJB.sndIfTestEAI]";
        String logId = slabUtils.getLogId();//(YmConstant.YD_GP_2);

        try {

            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
            slabUtils.printLog(logId, methodNm, "F+", gdReq);


            EJBConnector ejbConn = new EJBConnector("default", "PlateJspSeEJB", this); //BSlabJspSeEJB

            //IFTest EAI전송
            ejbConn.trx("sndIfTestEAI", new Class[] { GridData.class }, new Object[] { gdReq });

            slabUtils.printLog(logId, methodNm, "F-");

            //조회결과
            return gdReq;

        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }

    }   // end of sndIfTestEAI

    /**
     * 반품, 회송, 출고취소 하차등록
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData
     * @return GridData
     * @throws DAOException
     */
    public GridData regCarUdWrk(GridData gdReq) throws DAOException {
        String methodNm =  "반품, 회송, 출고취소 하차등록[PlateJspFaEJB.regCarUdWrk]";

        YdSlabUtils ydSlabUtils = new YdSlabUtils();
        String logId = ydSlabUtils.getLogId();//(YmConstant.YD_GP_2);
        GridData gdRet = null;

        try {

            ydSlabUtils.printLog(logId, methodNm, "F+", gdReq);


            EJBConnector ejbConn = new EJBConnector("default", "PlateJspSeEJB", this); //BSlabJspSeEJB

            //IFTest EAI전송
            gdRet = OperateGridData.cloneResponseGridData(gdReq);
            JDTORecord jrRst =  (JDTORecord) ejbConn.trx("regCarUdWrk", new Class[] { GridData.class }, new Object[] { gdReq });


            // 전문전송
            if( jrRst != null){
                if(jrRst.getField("SEND_DATA") != null){
                    List aSendList = (ArrayList)jrRst.getField("SEND_DATA");
                    int nSize = aSendList.size();
                    YdDelegate ydDelegate = new YdDelegate();
                    for(int i=0; i<nSize; i++){
                        ydDelegate.sendMsg((JDTORecord)aSendList.get(i));
                    }
                }
            }

            ydSlabUtils.printLog(logId, methodNm, "F-");
            //조회결과
            return gdRet;

        } catch(DAOException daoe) {
            throw daoe;
        } catch(Exception e) {
            throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
        }

    }   // end of regCarUdWrk


    /**
     * 차량예정정보 전송
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData
     * @return GridData
     * @throws DAOException
     */
    public GridData regCarUdExplainInfo(GridData gdReq) throws DAOException {
        String methodNm =  "하차등록[PlateJspFaEJB.regCarUdWrk]";

        YdSlabUtils ydSlabUtils = new YdSlabUtils();
        String logId = ydSlabUtils.getLogId();//(YmConstant.YD_GP_2);
        GridData gdRet = null;

        try {

            ydSlabUtils.printLog(logId, methodNm, "F+", gdReq);


            EJBConnector ejbConn = new EJBConnector("default", "PlateJspSeEJB", this); //BSlabJspSeEJB

            //IFTest EAI전송
            gdRet = OperateGridData.cloneResponseGridData(gdReq);
            JDTORecord jrRst =  (JDTORecord) ejbConn.trx("regCarUdExplainInfo", new Class[] { GridData.class }, new Object[] { gdReq });

            // 전문전송
            if( jrRst != null){
                if(jrRst.getField("SEND_DATA") != null){
                    List aSendList = (ArrayList)jrRst.getField("SEND_DATA");
                    int nSize = aSendList.size();
                    YdDelegate ydDelegate = new YdDelegate();
                    for(int i=0; i<nSize; i++){
                        ydDelegate.sendMsg_NoMakeTc((JDTORecord)aSendList.get(i));
                    }
                }
            }

            ydSlabUtils.printLog(logId, methodNm, "F-");
            //조회결과
            return gdRet;

        } catch(DAOException daoe) {
            throw daoe;
        } catch(Exception e) {
            throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
        }

    }   // end of regCarUdExplainInfo


    /**
     *  야드크레인 작업관리 (작업취소sjh)
     *  - 전사물류개선 2021.1.6 기존화면 분리(자동화크레인관련)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     * @throws JDTOException
     */
    public GridData cancleWorkPlateYdCrnWorkMgt4G(GridData gdReq) throws JDTOException {

        String szLogMsg           = "";
        String szMethodName       = "cancleWorkPlateYdCrnWorkMgt";
        String szR_msg            = "";
        String szOperationName  = "작업관리 (작업취소)";

        //파라미터 스크링 변수

        String sYD_CRN_SCH_ID   = "";
        String sYD_SCH_CD       = "";
        String sYD_USER_ID      = "";
        JDTORecordSet rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
        JDTORecord recPara      = JDTORecordFactory.getInstance().create();
        JDTORecord recCheck         = JDTORecordFactory.getInstance().create();
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();
        JDTORecord recDelPara       = JDTORecordFactory.getInstance().create();

        String sRTN_CD  = "";
        String sRTN_MSG = "";

        String szJMS_TC_CD  = "";
        String szYD_EQP_ID  = "";
        String szYD_WRK_PROG_STAT = "";
        String szYD_SCH_CD  = "";
        String szRTN_SND    = "N";
        String sCANCEL_SEND = "N";
        YDDataUtil  yddatautil = new YDDataUtil();
        YdCrnSchDao  ydCrnSchDao        = new  YdCrnSchDao();
        GridData gdRes = null;
        EJBConnector ejbConn = null;
        int intRtnVal = 0;
        String szMsg = "";
        String szC_YD_WRK_PROG_STAT = "";

        // 전사물류개선 2021.1.6 L9시스템 여부
        boolean isSendToEaiY9 = false;
        boolean isAutoCrnSendYn = false; // 권하위치변경 수정가능 여부 판단
        YdPlateCommDAO  commDao  = new YdPlateCommDAO();
        try{

            szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);


            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            for(int x=0;x<inRecord.length;x++){

                // 자동화크레인관련
                isAutoCrnSendYn = false;

                sYD_CRN_SCH_ID  = yddatautil.setDataDefault(inRecord[x].getField("YD_CRN_SCH_ID"), "");
                sYD_SCH_CD      = yddatautil.setDataDefault(inRecord[x].getField("YD_SCH_CD"), "");
                sYD_USER_ID     = yddatautil.setDataDefault(inRecord[x].getField("YD_USER_ID"), "");



                if (sYD_CRN_SCH_ID.equals("")) {

                    szMsg="스케줄 취소 처리("+szMethodName+") 실패, YD_CRN_SCH_ID값이 없음";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    continue;
                }


                //파라미터 레코드 setting
                recPara  = JDTORecordFactory.getInstance().create();
                recPara.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID);
                recPara.setField("YD_SCH_CD",     sYD_SCH_CD);
                recPara.setField("DEL_YN",        "N");
                recPara.setField("MODIFIER",      sYD_USER_ID);
                /*
                 * 크레인 스케줄에서 선택된 스케줄 ID로 작업예약을 조회한 후
                 * 삭제되지 않는 작업예약에 포함된 크레인 스케줄중 첫번째 값이
                 * 2,3 인 경우 후처리를 하지않기 위해  Check Logic 반영
                 */

                rsRtnVal    = JDTORecordFactory.getInstance().createRecordSet("temRs");
                // com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCheckYdCrnSchId
//              intRtnVal   = ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 36);
                intRtnVal   = commDao.select(recPara, rsRtnVal, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getCheckYdCrnSchId");

                if (intRtnVal < 1){
                    szMsg = "크레인스케쥴["+sYD_CRN_SCH_ID+"] 취소 작업을 완료 하였습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    continue;
                }


                rsRtnVal.first();
                recCheck = rsRtnVal.getRecord();

                szC_YD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recCheck, "YD_WRK_PROG_STAT");

                //2,3, 인 경우 와 혹시모를 4이면서도 스케줄 삭제가 되지 않은 경우
                if(szC_YD_WRK_PROG_STAT.equals("2") || szC_YD_WRK_PROG_STAT.equals("3") || szC_YD_WRK_PROG_STAT.equals("4")){
                    szMsg = "크레인스케쥴["+sYD_CRN_SCH_ID+"] 크레인 작업이 완료되지 않았습니다!!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    continue;
                }

                // 2021. 4. 3
                // 파일링중인 재료가 크레인작업지시가 내려간 상태면 취소금지
                if("Y".equals(ydDaoUtils.paraRecChkNull(recCheck, "PENDDING_PILING_YN"))){
                    szMsg = "크레인스케쥴["+sYD_CRN_SCH_ID+"] 현재 파일링 작업중이기 때문에 크레인스케쥴취소(작업취소)가 불가능합니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    continue;
                }


                //  전사물류개선 프로젝트
                szYD_EQP_ID = yddatautil.setDataDefault(inRecord[x].getField("YD_EQP_ID"), "");
                isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szYD_EQP_ID);
                if(isSendToEaiY9){

                    //일시정지-스케줄취소 적용여부
                    boolean sAPP030 = PlateGdsYdUtil.isApplyYn("자동크레인일시정지스케줄취소적용여부");


                    JDTORecordSet jsEqpInfo =  JDTORecordFactory.getInstance().createRecordSet("temRs");;

                    jsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("temp");
                    intRtnVal = YdCommonUtils.getYdEqp(szYD_EQP_ID, jsEqpInfo);

                    if(intRtnVal>0){

                        String szydEqpStat      = jsEqpInfo.getRecord(0).getFieldString("YD_EQP_STAT");   // 설비 상태
                        String szEqpAutoCrnMode= jsEqpInfo.getRecord(0).getFieldString("YD_EQP_AUTO_CRN_MODE"); // AutoCrn 상태
                        String szEqpAutoCrnYN   = jsEqpInfo.getRecord(0).getFieldString("YD_EQP_WRK_MODE2");   // AutoCrn 여부

//                      if (("A".equals(szEqpAutoCrnYN) || "R".equals(szEqpAutoCrnYN))) {
                        if ("A".equals(szEqpAutoCrnYN)) {

                            //일시정지-스케줄취소 적용여부 AT00 2023.02.04 보류일때도 작업취소 가능하게 변경
                            if(sAPP030){
                                if(!("W".equals(szC_YD_WRK_PROG_STAT) || "C".equals(szC_YD_WRK_PROG_STAT))){
                                    // 일시정지의 경우에 삭제하도록 수정조치함
                                    if (!("4".equals(szEqpAutoCrnMode) || "5".equals(szEqpAutoCrnMode))) {
//                              if (!"4".equals(szEqpAutoCrnMode) && !"B".equals(szydEqpStat)) { //4: 일시정지 B:고장
//                                  throw new Exception("무인크레인 [" + szYD_EQP_ID + "]이 일시정지이거나 고장상태가 아니면 취소할 수 없습니다.");
                                        szMsg="[Jsp-Session " + szOperationName+ " ] 무인크레인 [" + szYD_EQP_ID + "]이 일시정지(4)이거나 비상정지(5)가 아니면 취소할 수 없습니다.";
                                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//                                  throw new Exception("무인크레인 [" + szYD_EQP_ID + "]이 일시정지이거나 고장상태가 아니면 취소할 수 없습니다.");
                                        gdRes.setMessage("무인크레인 [" + szYD_EQP_ID + "]이 일시정지(4)이거나 비상정지(5)가 아니면 취소할 수 없습니다.");
                                        return gdRes;
                                    }
                                }
                            }

                            if(!("W".equals(szC_YD_WRK_PROG_STAT) || "C".equals(szC_YD_WRK_PROG_STAT) )){

                                JDTORecord jrParam = JDTORecordFactory.getInstance().create();
                                jrParam.setField("YD_L2_REQUEST_STAT", "X");//'스케쥴취소요청:응답대기중'
                                jrParam.setField("YD_CRN_SCH_ID"     , sYD_CRN_SCH_ID);
                                jrParam.setField("MODIFIER"          , sYD_USER_ID);
                                commDao.update(jrParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCrnSchProgStat");

                                JDTORecord msgRec   = JDTORecordFactory.getInstance().create();
                                msgRec.setField("MSG_ID",                       YdConstant.YDYDJ701);
                                msgRec.setField(YdConstant.BUFFER_TC_CD,        "YDY9L004");
                                msgRec.setField("YD_CRN_SCH_ID",    sYD_CRN_SCH_ID          );
                                msgRec.setField("YD_WRK_PROG_STAT", szC_YD_WRK_PROG_STAT    );   // 이모듈을 탈려면 항상 '1'의값이 들어옴
                                msgRec.setField("MSG_GP",           "D"                );

                                //S1 일시정지 후 스케줄취소
                                if(sAPP030){
                                    msgRec.setField("YD_CRN_SCH_RMD_CNT", "SD"  );
                                }else{
                                    msgRec.setField("YD_CRN_SCH_RMD_CNT", "D"  );
                                }

                                YdDelegate ydDelegate = new YdDelegate();
                                ydUtils.displayRecord(szOperationName, msgRec);
                                ydDelegate.sendMsg(msgRec);

                                // 자동화크레인관련
                                isAutoCrnSendYn = true;
                            }
                        }
                    }
                }

                if(!isAutoCrnSendYn){

                    /*
                     * 2,3이 아닌 경우 스케줄 취소기능에  첫번째 크레인 스케줄 ID 정보를 전송
                     */
                    recPara = JDTORecordFactory.getInstance().create();
                    recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recCheck, "YD_CRN_SCH_ID"));
                    recPara.setField("YD_SCH_CD",     ydDaoUtils.paraRecChkNull(recCheck, "YD_SCH_CD"));
                    recPara.setField("DEL_YN",        ydDaoUtils.paraRecChkNull(recCheck, "DEL_YN"));
                    recPara.setField("MODIFIER",      sYD_USER_ID);

//크레인스케줄 ID보다 이상인 ID 삭제  업데이트 실행(적치단정보까지 CLEAR)
//스케줄취소
//              szMsg = this.PlateSchCncl(recPara);

                    szMsg = "스케쥴 취소 시작!!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
                    outRecord1  = (JDTORecord)ejbConn.trx("PlateSchCncl4G", new Class[] { JDTORecord.class }, new Object[] { recPara });

                    sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
                    sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
                    sCANCEL_SEND= StringHelper.evl(outRecord1.getFieldString("CANCEL_SEND"), "N");
                    if ("0".equals(sRTN_CD)) {
                        gdRes.setMessage(sRTN_MSG);
                        m_ctx.setRollbackOnly();
                        return gdRes;
                    }

                    szMsg = "스케쥴 취소 종료!! 작업예약 취소 시작";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
                    outRecord1  = (JDTORecord)ejbConn.trx("PlateDelWBook", new Class[] { JDTORecord.class }, new Object[] { outRecord1 });

                    szMsg = "작업예약 취소 종료!!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


                    sRTN_CD             = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
                    szJMS_TC_CD         = StringHelper.evl(outRecord1.getFieldString("MSG_ID"), "");
                    szYD_EQP_ID         = StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
                    szYD_WRK_PROG_STAT  = StringHelper.evl(outRecord1.getFieldString("YD_WRK_PROG_STAT"), "");
                    szYD_SCH_CD         = StringHelper.evl(outRecord1.getFieldString("YD_SCH_CD"), "");
                    szRTN_SND           = StringHelper.evl(outRecord1.getFieldString("RTN_SND"), "N");

                    if ("0".equals(sRTN_CD)) {
                        gdRes.setMessage(sRTN_MSG);
                        m_ctx.setRollbackOnly();
                        return gdRes;
                    }

                    // 작업요구송신
                    if(szRTN_SND.equals("Y") && sCANCEL_SEND.equals("Y")) {

                        YdDelegate ydDelegate = new YdDelegate();

                        szMsg = "[JSP Session : "+szOperationName+"] 크레인 작업지시 정보를 내부QUEUE로 송신 합니다";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                        recDelPara   = JDTORecordFactory.getInstance().create();
                        recDelPara.setField("MSG_ID"                , szJMS_TC_CD        );
                        recDelPara.setField("YD_EQP_ID"             , szYD_EQP_ID            );
                        recDelPara.setField("YD_WRK_PROG_STAT"      , szYD_WRK_PROG_STAT);
                        recDelPara.setField("YD_SCH_CD"             , szYD_SCH_CD );
                        ydDelegate.sendMsg(recDelPara);
                    }
                }
            }
            szMsg = "JSP-SESSION [ 야드크레인 작업관리 (작업취소) ] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            gdRes.setMessage("정상적으로 취소 처리되었습니다.");

            ydUtils.putLog(szSessionName, szMethodName, szR_msg, YdConstant.INFO);

        }catch(JDTOException de) {

            gdRes.setMessage("Failure");
            szLogMsg = "작업 취소 실패 - DAO Exception ";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);

        }catch(Exception e){
            gdRes.setMessage("Failure");
            szLogMsg = "작업 취소 실패 - JDTOException ";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

        szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
        ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

        return gdRes;
    }

    /**
     *  야드크레인 작업관리 (스케줄 취소:NEW)
     *  - 전사물류개선 2021.1.6 기존화면 분리(자동화크레인관련)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     *
     */
    public GridData cancleSchPlateYdCrnWorkMgt4G(GridData gdReq) throws JDTOException {

        String szMethodName="cancleSchPlateYdCrnWorkMgt4G";
        String szR_msg ="";
        String szLogMsg = "";
        String szOperationName  = "작업관리 (스케줄 취소)";

        //파라미터 스크링 변수

        String sYD_CRN_SCH_ID   = "";
        String sYD_SCH_CD       = "";
        String sYD_USER_ID      = "";
        JDTORecordSet rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
        JDTORecord recPara      = JDTORecordFactory.getInstance().create();
        JDTORecord recCheck         = JDTORecordFactory.getInstance().create();
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();
        JDTORecord recEqpPara       = JDTORecordFactory.getInstance().create();

        String sRTN_CD  = "";
        String sRTN_MSG = "";

        String szRtnMsg = "";
        String szYD_EQP_ID  = "";
        String sCANCEL_SEND = "N";
        YDDataUtil  yddatautil = new YDDataUtil();
        YdCrnSchDao  ydCrnSchDao        = new  YdCrnSchDao();
        GridData gdRes = null;
        EJBConnector ejbConn = null;
        int intRtnVal = 0;
        String szMsg = "";
        String szC_YD_WRK_PROG_STAT = "";
        // 전사물류개선 2021.1.6 L9시스템 여부
        boolean isSendToEaiY9 = false;
        boolean isAutoCrnSendYn = false; // 권하위치변경 수정가능 여부 판단
        YdPlateCommDAO  commDao  = new YdPlateCommDAO();
        try{

            szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
            ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(gdReq);
            gdRes = CmUtil.copyGDParam(gdReq, gdRes);


            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

            for(int x=0;x<inRecord.length;x++){

                // 자동화크레인관련
                isAutoCrnSendYn = false;

                sYD_CRN_SCH_ID  = yddatautil.setDataDefault(inRecord[x].getField("YD_CRN_SCH_ID"), "");
                sYD_SCH_CD      = yddatautil.setDataDefault(inRecord[x].getField("YD_SCH_CD"), "");
                sYD_USER_ID     = yddatautil.setDataDefault(inRecord[x].getField("YD_USER_ID"), "");

                if (sYD_CRN_SCH_ID.equals("")) {

                    szMsg="스케줄 취소 처리("+szMethodName+") 실패, YD_CRN_SCH_ID값이 없음";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                    continue;
                }


                //파라미터 레코드 setting
                recPara  = JDTORecordFactory.getInstance().create();
                recPara.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID);
                recPara.setField("YD_SCH_CD",     sYD_SCH_CD);
                recPara.setField("DEL_YN",        "N");
                recPara.setField("MODIFIER",      sYD_USER_ID);
                /*
                 * 크레인 스케줄에서 선택된 스케줄 ID로 작업예약을 조회한 후
                 * 삭제되지 않는 작업예약에 포함된 크레인 스케줄중 첫번째 값이
                 * 2,3 인 경우 후처리를 하지않기 위해  Check Logic 반영
                 */

                rsRtnVal    = JDTORecordFactory.getInstance().createRecordSet("temRs");
                // com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCheckYdCrnSchId
//              intRtnVal   = ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 36);
                intRtnVal   = commDao.select(recPara, rsRtnVal, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getCheckYdCrnSchId");

                if (intRtnVal < 1){
                    szMsg = "취소 작업을 완료 하였습니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    continue;
                }

                rsRtnVal.first();
                recCheck = rsRtnVal.getRecord();

                szC_YD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recCheck, "YD_WRK_PROG_STAT");
                //2,3, 인 경우 와 혹시모를 4이면서도 스케줄 삭제가 되지 않은 경우
                if(szC_YD_WRK_PROG_STAT.equals("2") || szC_YD_WRK_PROG_STAT.equals("3") || szC_YD_WRK_PROG_STAT.equals("4")){
                    szMsg = "크레인 작업이 완료되지 않았습니다!!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    continue;
                }

                // 2021. 4. 3
                // 파일링중인 재료가 크레인작업지시가 내려간 상태면 취소금지
                if("Y".equals(ydDaoUtils.paraRecChkNull(recCheck, "PENDDING_PILING_YN"))){
                    szMsg = "크레인스케쥴["+sYD_CRN_SCH_ID+"] 현재 파일링 작업중이기 때문에 크레인스케쥴취소(작업취소)가 불가능합니다.";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    continue;
                }

                //  전사물류개선 프로젝트
                szYD_EQP_ID = yddatautil.setDataDefault(inRecord[x].getField("YD_EQP_ID"), "");
                isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szYD_EQP_ID);
                if(isSendToEaiY9){

                    //일시정지-스케줄취소 적용여부
                    boolean sAPP030 = PlateGdsYdUtil.isApplyYn("자동크레인일시정지스케줄취소적용여부");

                    commDao  = new YdPlateCommDAO();
                    JDTORecordSet jsEqpInfo =  null;

                    jsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("temp");
                    intRtnVal = YdCommonUtils.getYdEqp(szYD_EQP_ID, jsEqpInfo);

                    if(intRtnVal>0){

                        String szydEqpStat      = jsEqpInfo.getRecord(0).getFieldString("YD_EQP_STAT");   // 설비 상태
                        String szEqpAutoCrnMode= jsEqpInfo.getRecord(0).getFieldString("YD_EQP_AUTO_CRN_MODE"); // AutoCrn 상태
                        String szEqpAutoCrnYN   = jsEqpInfo.getRecord(0).getFieldString("YD_EQP_WRK_MODE2");   // AutoCrn 여부

//                      if (("A".equals(szEqpAutoCrnYN) || "R".equals(szEqpAutoCrnYN))) {
                        if ("A".equals(szEqpAutoCrnYN)) {

                            //일시정지-스케줄취소 적용여부
                            if(sAPP030){
                                if(!"W".equals(szC_YD_WRK_PROG_STAT)){
                                    // 일시정지거나 비상정지일 경우에만
                                    if (!("4".equals(szEqpAutoCrnMode) || "5".equals(szEqpAutoCrnMode))) {
                                        szMsg="[Jsp-Session " + szOperationName+ " ] 무인크레인 [" + szYD_EQP_ID + "]이 일시정지(4)이거나 비상정지(5)가 아니면 취소할 수 없습니다.";
                                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                                        gdRes.setMessage("무인크레인 [" + szYD_EQP_ID + "]이 일시정지(4)이거나 비상정지(5)가 아니면 취소할 수 없습니다.");
                                        return gdRes;
                                    }
                                }
                            }

                            if(!"W".equals(szC_YD_WRK_PROG_STAT)){
                                JDTORecord jrParam = JDTORecordFactory.getInstance().create();
                                jrParam.setField("YD_L2_REQUEST_STAT", "D");//'스케쥴취소요청:응답대기중'
                                jrParam.setField("YD_CRN_SCH_ID"     , sYD_CRN_SCH_ID);
                                jrParam.setField("MODIFIER"          , sYD_USER_ID);
                                commDao.update(jrParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCrnSchProgStat");

                                JDTORecord recDelPara   = JDTORecordFactory.getInstance().create();
                                recDelPara.setField("MSG_ID",                       YdConstant.YDYDJ701);
                                recDelPara.setField(YdConstant.BUFFER_TC_CD,        "YDY9L004");
                                recDelPara.setField("YD_CRN_SCH_ID",    sYD_CRN_SCH_ID          );
                                recDelPara.setField("YD_WRK_PROG_STAT", szC_YD_WRK_PROG_STAT    );   // 이모듈을 탈려면 항상 '1'의값이 들어옴
                                recDelPara.setField("MSG_GP",           "D"                );

                                //S1 일시정지 후 스케줄취소
                                if(sAPP030){
                                    recDelPara.setField("YD_CRN_SCH_RMD_CNT", "SD"  );
                                }else{
                                    recDelPara.setField("YD_CRN_SCH_RMD_CNT", "D"  );
                                }

                                YdDelegate ydDelegate = new YdDelegate();
                                ydUtils.displayRecord(szOperationName, recDelPara);
                                ydDelegate.sendMsg(recDelPara);

                                // 자동화크레인관련
                                isAutoCrnSendYn = true;
                            }
                        }
                    }
                }

                if(!isAutoCrnSendYn){

                    /*
                     * 2,3이 아닌 경우 스케줄 취소기능에  첫번째 크레인 스케줄 ID 정보를 전송
                     */
                    recPara = JDTORecordFactory.getInstance().create();
                    recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recCheck, "YD_CRN_SCH_ID"));
                    recPara.setField("YD_SCH_CD",     ydDaoUtils.paraRecChkNull(recCheck, "YD_SCH_CD"));
                    recPara.setField("DEL_YN",        ydDaoUtils.paraRecChkNull(recCheck, "DEL_YN"));
                    recPara.setField("MODIFIER",      sYD_USER_ID);

                    // 스케쥴단위취소
                    recPara.setField("IS_SCH_MTL",        "Y");
//크레인스케줄 ID보다 이상인 ID 삭제  업데이트 실행(적치단정보까지 CLEAR)
//스케줄취소
//              szMsg = this.PlateSchCncl(recPara);

                    szMsg = "스케쥴 취소 시작!!";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
                    outRecord1  = (JDTORecord)ejbConn.trx("PlateSchCncl4G", new Class[] { JDTORecord.class }, new Object[] { recPara });

                    sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
                    sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
                    sCANCEL_SEND= StringHelper.evl(outRecord1.getFieldString("CANCEL_SEND"), "N");
                    szYD_EQP_ID = StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");


                    if ("0".equals(sRTN_CD)) {
                        gdRes.setMessage(sRTN_MSG);
                        m_ctx.setRollbackOnly();
                        return gdRes;
                    }
// 스케쥴 취소는 명령선택 하지 안음
// 취소된거 명령선택될 경우 발생
// 취소전문 송신시 설비 정보 UPDATE

                    if(sCANCEL_SEND.equals("Y")) {
                        //--------------------------------------------------------------------------------
                        // 설비가 고장 또는 OFF 라인 상태가 아닐경우
                        // 선택된 설비가 취소 되었으므로 해당설비의 설비 테이블정보에
                        // 작업대기 상태로 UPDATE 해준다.
                        //--------------------------------------------------------------------------------

                        szRtnMsg = YdCommonUtils.checkCrnStat(szYD_EQP_ID);

                        if(szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
                            recEqpPara   = JDTORecordFactory.getInstance().create();
                            recEqpPara.setField("YD_EQP_ID"     , szYD_EQP_ID);
                            recEqpPara.setField("YD_EQP_STAT"   , YdConstant.YD_EQP_STAT_IDLE);
                            recEqpPara.setField("MODIFIER"      ,sYD_USER_ID);

                            szMsg="[Jsp-Session " + szOperationName+ " ] 크레인("+ szYD_EQP_ID +") 설비상태 [" + YdConstant.YD_EQP_STAT_IDLE +"]로 변경 ------------------";
                            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                            EJBConnector ejbConn2 = new EJBConnector("default","SlabJspSeEJB",this);
                            Boolean isSuccess = (Boolean)ejbConn2.trx("RequiresUpdYdEqp",new Class[]{JDTORecord.class}, new Object[]{recEqpPara});
                        }
                    }
                }
            }

            szMsg = "JSP-SESSION [ 야드크레인 작업관리 (스케줄 취소) ] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


            gdRes.setMessage("정상적으로 스케줄 취소 처리되었습니다.");


            ydUtils.putLog(szSessionName, szMethodName, szR_msg, YdConstant.INFO);

        }catch(Exception e){
            throw new JDTOException(getClass().getName() + e.getMessage(),e);
        }
        return gdRes;
    }

    /**
     * 설비상태 (변경 설비기준조회 )
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData
     * @return GridData
     * @throws DAOException
     */
    public GridData updEqpOprnStat(GridData gdReq) throws DAOException {
        String methodNm =  "updEqpOprnStat";
        String szLogMsg = "";
        String szOperationName = "크레인 상태 설정(설비기준조회)";

        try {

            szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
            ydUtils.putLog(szSessionName, methodNm, szLogMsg, YdConstant.DEBUG);

            EJBConnector ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            //설비상태 변경
            ejbConn.trx("updEqpOprnStat", new Class[] { GridData.class }, new Object[] { gdReq });

            szLogMsg = "[Jsp Facade : "+ szOperationName+"] 메소드 끝";
            ydUtils.putLog(szSessionName , methodNm , szLogMsg , YdConstant.DEBUG);

            //조회결과
            return gdReq;

        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(getClass().getName() + e.getMessage(), e);
        }

    }   // end of updEqpOprnStat


    /**
     * 크레인작업관리 지시변경작업
     *  - 명령선택단계의 있는 크레인스케쥴의 상태를 대기상태로 변경처리
     *
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData
     * @return GridData
     * @throws DAOException
     */
    public GridData cheWkrJob(GridData gdReq) throws DAOException {
        String methodNm =  "크레인작업관리 지시변경작업[PlateJspFaEJB.regCarUdWrk]";

        YdSlabUtils ydSlabUtils = new YdSlabUtils();
        String logId = ydSlabUtils.getLogId();//(YmConstant.YD_GP_2);
        GridData gdRet = null;

        try {

            ydSlabUtils.printLog(logId, methodNm, "F+", gdReq);


            EJBConnector ejbConn = new EJBConnector("default", "PlateJspSeEJB", this); //BSlabJspSeEJB

            //IFTest EAI전송
            gdRet = OperateGridData.cloneResponseGridData(gdReq);
            JDTORecord jrRst =  (JDTORecord) ejbConn.trx("cheWkrJob", new Class[] { GridData.class }, new Object[] { gdReq });


            // 전문전송
            if( jrRst != null){
                if(jrRst.getField("SEND_DATA") != null){
                    List aSendList = (ArrayList)jrRst.getField("SEND_DATA");
                    int nSize = aSendList.size();
                    YdDelegate ydDelegate = new YdDelegate();
                    for(int i=0; i<nSize; i++){
                        ydDelegate.sendMsg_NoMakeTc((JDTORecord)aSendList.get(i));
                    }
                }
                if(!"".equals(jrRst.getFieldString("MESSAGE"))){
                    gdRet.setMessage(jrRst.getFieldString("MESSAGE"));
                }
                else{
                    gdRet.setMessage("");
                }
            }

            ydSlabUtils.printLog(logId, methodNm, "F-");
            //조회결과
            return gdRet;

        } catch(DAOException daoe) {
            throw daoe;
        } catch(Exception e) {
            throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
        }

    }   // end of regCarUdWrk

    /**
     *  후판야드 긴급재선별작업지시 편성
     *   - 크레인스케쥴을 예약순서별로 생성처리해야해서
     *   - 1. 작업예약 만듬 -> 화면리턴 -> 크레인스케쥴 생성
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return GridData
     * @throws JDTOException
     */
    public GridData updYdUgSelReq(GridData gdReq) throws DAOException  {

        YdSlabUtils ydSlabUtils = new YdSlabUtils();
        String logId = ydSlabUtils.getLogId();//(YmConstant.YD_GP_2);
        GridData gdRet = null;
        String methodNm =  "후판야드 긴급재선별작업지시 편성";

        try {

            ydSlabUtils.printLog(logId, methodNm, "F+", gdReq);
            EJBConnector ejbConn = new EJBConnector("default", "PlateJspSeEJB", this); //BSlabJspSeEJB

            gdRet = OperateGridData.cloneResponseGridData(gdReq);

            String sJobType = gdReq.getParam("JOB_TYPE");
            gdRet.addParam("JOB_TYPE", sJobType);
            if("CRN_MAIN".equals(sJobType)){

                int rowCnt = Integer.parseInt(gdReq.getParam("WRKBOOK_CNT"));
                JDTORecord jdtoMakeCrnSch = JDTORecordFactory.getInstance().create();
                jdtoMakeCrnSch.setField("YD_SCH_CD", gdReq.getParam("YD_SCH_CD"));
                jdtoMakeCrnSch.setField("YD_EQP_ID", gdReq.getParam("YD_EQP_ID"));
                jdtoMakeCrnSch.setField("JMS_TC_CD","YDYDJ506");

                EJBConnector ydEjbCon = null;
                ydEjbCon = new EJBConnector("default", "CrnSchSeEJB", this);
                for(int i=0; i < rowCnt; i++){
                    jdtoMakeCrnSch.setField("YD_WBOOK_ID", gdReq.getParam("YD_WBOOK_ID_"+(i+1)));
                    ydEjbCon.trx("procY4CrnSchMain", new Class[] { JDTORecord.class }, new Object[] { jdtoMakeCrnSch });
                }
            }
            else if("REQ_WRKBOOK".equals(sJobType)){

                JDTORecord jrRst =  (JDTORecord) ejbConn.trx("updYdUgSelReq", new Class[] { GridData.class }, new Object[] { gdReq });
                // 전문전송
                int nSize = 0;
                if( jrRst != null){
                    if(jrRst.getField("SEND_DATA") != null){

                        JDTORecord jdtoSendMsg = null;
                        List aSendList = (ArrayList)jrRst.getField("SEND_DATA");
                        nSize = aSendList.size();
//                      YdDelegate ydDelegate = new YdDelegate();
//                      EJBConnector ydEjbCon = null;
//                      ydEjbCon = new EJBConnector("default", "CrnSchSeEJB", this);
                        for(int i=0; i<nSize; i++){
                            jdtoSendMsg = (JDTORecord)aSendList.get(i);
//                          ydDelegate.sendMsg_NoMakeTc((JDTORecord)aSendList.get(i));
                            // 직접호출
                            // New 트랜잭션이라 커밋이 되지 않아 작업예약을 읽을 수 없음
                            // 작업예약은 여러개 생성되며, 순차적으로 크레인스케쥴을 기동처리해야함
//                          ydEjbCon.trx("procY4CrnSchMain", new Class[] { JDTORecord.class }, new Object[] { jdtoSendMsg });
                            if(i==0){
                                gdRet.addParam("YD_EQP_ID", jdtoSendMsg.getFieldString("YD_EQP_ID"));
                                gdRet.addParam("YD_SCH_CD", jdtoSendMsg.getFieldString("YD_SCH_CD"));
                            }

                            gdRet.addParam("YD_WBOOK_ID_"+(i+1), jdtoSendMsg.getFieldString("YD_WBOOK_ID"));
                        }
                    }
                    if(!"".equals(jrRst.getFieldString("MESSAGE"))){
                        gdRet.setMessage(jrRst.getFieldString("MESSAGE"));
                    }
                    else{
                        gdRet.setMessage("");
                    }
                    gdRet.addParam("WRKBOOK_CNT", ""+nSize);
                }
            }

            ydSlabUtils.printLog(logId, methodNm, "F-");
            //조회결과
            return gdRet;

        } catch(DAOException daoe) {
            throw daoe;
        } catch(Exception e) {
            throw new DAOException(e.getMessage());
        }

    }  //end of PlateYdUgSelReq
    ///////////////////////////////////////////////////////////////////////////////
    ///                          전사물류개선 프로젝트 2021.1.6                  ///
    ///////////////////////////////////////////////////////////////////////////////
    /**
     * 1후판 RT 우선순위 조회 및 L2 전송
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData getPlateRtPriority(GridData inDto) throws JDTOException {
        String szMethodName="updPlateIntensiveIncome";
        String szMsg = "";

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();

        try{

            szMsg = "JSP-FACADE [RT우선순위 L2 전송] 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            //JDTORecord inRecord =  JDTORecordFactory.getInstance().create();
            JDTORecord inRecord =  CmUtil.genJDTORecord(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            //ejbConn.trx("getPlateRtPriority", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
              sRTN_MSG  = (String)ejbConn.trx("getPlateRtPriority", new Class[] { JDTORecord.class }, new Object[] { inRecord });
            //sRTN_MSG  = (String)ejbConn.trx("procBookOutCancel", new Class[] { JDTORecord.class }, new Object[] { inRecord });

            //sRTN_CD       = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            //sRTN_MSG  = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            //if ("0".equals(sRTN_CD)) {
            //  gdRes.setMessage(sRTN_MSG);
            //  m_ctx.setRollbackOnly();
            //  return gdRes;
            //}

            gdRes.setStatus("true");
            gdRes.setMessage("정상적으로 전송  되었습니다.");

            szMsg = "JSP-FACADE [RT우선순위 L2 전송] 끝";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }       //end of getPlateRtPriority

    /**
     * 저장위치과부족현황 화면 베드 기준 정보 Update (2024.10.21)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inGridData
     * @return GridData
     * @throws DAOException
     */
    public GridData updBedRuleData(GridData inGridData) throws DAOException {
        //LOG
        String szMsg			= "";
        String szMethodName		= "updBedRuleData";

        String logId            = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

        szMsg = "저장위치과부족현황 베드 기준 정보 UPDATE(" + szMethodName + ") 시작";
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

        GridData gdRes = null;
        EJBConnector ejbConn = null;
        try{
            //Grid date 를 JDTORecord data 로 변환
            JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inGridData);
            JDTORecord inRecord2 = CmUtil.genJDTORecord(inGridData);

            String szQueryId = inRecord2.getFieldString("QUERY_ID");

            szMsg = ">>>>>>>>>>>>>>>>>>>>QUERY_ID:" + szQueryId;
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

            //SE EJB 호출
            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            ejbConn.trx("updBedRuleData", new Class[]   { JDTORecord[].class ,  String.class ,  String.class    }
                                        , new Object[]  { inRecord ,            szQueryId,      logId           });
            // UI로 반환 할 Grid data 를 생성
            gdRes = OperateGridData.cloneResponseGridData(inGridData);
            gdRes = CmUtil.copyGDParam(inGridData, gdRes);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            szMsg = "저장위치과부족현황 베드 기준 정보 UPDATE(" + szMethodName + ") 종료";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

            return gdRes;

        }catch(Exception e){
            szMsg = e.getMessage();
            ydUtils.putLogNew(szSessionName, szMethodName , szMsg , YdConstant.ERROR, logId);
            throw new DAOException(getClass().getName() + e.getMessage(),e);
        }

    }  //end of updBedRuleData

    /**
     * 후판제품 육송상차통로결정기준 Update (2024.10.30)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updPlateYdCarUppRuleT00031(GridData inDto) throws JDTOException {
        String szMethodName			= "updPlateYdCarUppRuleT00031";
        String szMsg 				= "";

        String logId                = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

        szMsg = "후판제품 육송상차통로결정기준 UPDATE(" + szMethodName + ") 시작";
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();

        try{

            szMsg = "JSP-FACADE [후판제품 육송상차통로결정기준 UPDATE] 시작";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            outRecord1  = (JDTORecord)ejbConn.trx("updPlateYdCarUppRuleT00031", new Class[]  { JDTORecord[].class,  String.class }
                                                                              , new Object[] { inRecord,            logId            });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage("정상적으로 수정  되었습니다.");

            szMsg = "후판제품 육송상차통로결정기준 UPDATE(" + szMethodName + ") 완료";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLogNew(szSessionName, szMethodName, e.getMessage(), 1, logId);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }  //end of updPlateYdCarUppRuleT00031
    
    /**
     * 보조작업TO위치검색순서 T00071 기준 수정 2025.01.07 RITM0791916
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updYdRuleT00071(GridData inDto) throws JDTOException {
    	String szMethodName			= "updYdRuleT00071";
        String szMsg 				= "";

        String logId                = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

        szMsg = "후판제품 보조작업TO위치검색순서 T00071 기준 수정(" + szMethodName + ") 시작";
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();

        try{

            szMsg = "JSP-FACADE [후판제품 보조작업TO위치검색순서 T00071 기준 수정] 시작";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            outRecord1  = (JDTORecord)ejbConn.trx("updYdRuleT00071", new Class[]  { JDTORecord[].class,  String.class }
                                                                              , new Object[] { inRecord,            logId            });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage("정상적으로 수정  되었습니다.");

            szMsg = "후판제품 보조작업TO위치검색순서 T00071 기준 수정(" + szMethodName + ") 완료";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLogNew(szSessionName, szMethodName, e.getMessage(), 1, logId);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }
    
    /**
     * 주작업TO위치검색순서 T00072 기준 수정 2025.01.07 RITM1153724
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData updYdRuleT00072(GridData inDto) throws JDTOException {
    	String szMethodName			= "updYdRuleT00072";
        String szMsg 				= "";

        String logId                = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

        szMsg = "후판제품 주작업TO위치검색순서 T00071 기준 수정(" + szMethodName + ") 시작";
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();

        try{

            szMsg = "JSP-FACADE [후판제품 주작업TO위치검색순서 T00071 기준 수정] 시작";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            outRecord1  = (JDTORecord)ejbConn.trx("updYdRuleT00072", new Class[]  { JDTORecord[].class,  String.class }
                                                                              , new Object[] { inRecord,            logId            });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage("정상적으로 수정  되었습니다.");

            szMsg = "후판제품 주작업TO위치검색순서 T00072 기준 수정(" + szMethodName + ") 완료";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLogNew(szSessionName, szMethodName, e.getMessage(), 1, logId);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }
    
    
    /**
     * 차량 입동포인트 변경 25.08.07 임진후기사 요청 
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inDto
     * @return
     * @throws JDTOException
     */
    public GridData changeCarLoc(GridData inDto) throws JDTOException {
    	String szMethodName			= "changeCarLoc";
        String szMsg 				= "";

        String logId                = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

        String szOperationName  = "후판제품 차량 입동포인트변경";
        
        szMsg = "["+szOperationName + " ] (" + szMethodName + ") 시작";
        ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

        GridData gdRes              = null;
        EJBConnector ejbConn        = null;
        String sRTN_CD              = "";
        String sRTN_MSG             = "";
        JDTORecord outRecord1       = JDTORecordFactory.getInstance().create();

        try{

            szMsg = "["+szOperationName + " ] 시작";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

            gdRes = OperateGridData.cloneResponseGridData(inDto);

            ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
            outRecord1  = (JDTORecord)ejbConn.trx("changeCarLoc", new Class[]  { GridData.class,  String.class }
                                                                              , new Object[] { inDto,            logId            });

            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

            if ("0".equals(sRTN_CD)) {
                gdRes.setMessage(sRTN_MSG);
                m_ctx.setRollbackOnly();
                return gdRes;
            }

            gdRes.setStatus("true");
            gdRes.setMessage("정상적으로 수정  되었습니다.");

            szMsg = "["+szOperationName + " ] (" + szMethodName + ") 완료";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

            return gdRes;

        }catch(Exception e){
            ydUtils.putLogNew(szSessionName, szMethodName, e.getMessage(), 1, logId);
            throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }

    }

}

