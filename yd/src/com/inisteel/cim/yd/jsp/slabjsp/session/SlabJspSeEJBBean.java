/**
 * @(#)SlabJspSeEJBBean.java
 * 
 * @version			1.0
 * @author 			현대제철
 * @date			2012/11/14
 * 
 * @description		이클래스는업무 화면의 메뉴를 관리하기 위한 Session Session EJB클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.01  2013/04/03   허철호      허철호      신규설비 추가
 * V1.02  2015/12/14   이준영      이준영      항만 신규설비 추가
 */

package com.inisteel.cim.yd.jsp.slabjsp.session;

//UTIL IMPORT
import java.util.Vector;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.ct.wplansetup.hotmtldemandsupplansetup.dao.HmtlLadleProgStatDAO;
import com.inisteel.cim.or.common.util.CmnUtil;
import com.inisteel.cim.yd.common.dao.YdCommDAO;
import com.inisteel.cim.yd.common.dao.ptMSlabCommDao.PtMSlabCommDao;
import com.inisteel.cim.yd.common.dao.ptSlabCommDao.PtSlabCommDao;
import com.inisteel.cim.yd.common.dao.ptStlFrtoMoveDao.PtStlFrtoMoveDao;
import com.inisteel.cim.yd.common.dao.qmBuySlabInfoDao.QmBuySlabInfoDao;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSpecDao.YdCrnSpecDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydEqpPauseDao.YdEqpPauseDao;
import com.inisteel.cim.yd.common.dao.ydPrepMtlDao.YdPrepMtlDao;
import com.inisteel.cim.yd.common.dao.ydPrepSchDao.YdPrepSchDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydWrkHistDao.YdWrkHistDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.rule.GetBreRule1;
import com.inisteel.cim.yd.common.rule.GetBreRule2;
import com.inisteel.cim.yd.common.rule.GetBreRule3;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.jplateyd.delegate.JPlateYdDelegate;
import com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao;
import com.inisteel.cim.yd.jsp.common.YDComUtil;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO;
import com.inisteel.cim.yd.ydEquipStat.EquipTrack.EqpTrackingSeEJBBean;
import com.inisteel.cim.yd.ydSch.CraneSch.CrnSchSeEJBBean;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.yd.ydWkAct.CraneUnloadWkrHd.CraneUdHdSeEJBBean;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO;

/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="SlabJspSeEJB" jndi-name="SlabJspSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */

public class SlabJspSeEJBBean extends BaseSessionBean {

	private YdUtils ydUtils = new YdUtils();
	private String szSessionName = getClass().getName();
	private YdSlabUtils  slabUtils = new YdSlabUtils();
	YDComUtil   ydComUtil = new YDComUtil();
    YDDataUtil  yddatautil = new YDDataUtil();
    private YdDaoUtils ydDaoUtils = new YdDaoUtils();
    private YdTcConst  ydTcConst =new YdTcConst();
    private YdCommDAO commDao = new YdCommDAO();
    private SlabYdCommDAO slabYdCommDao = new SlabYdCommDAO();
    private SlabYdL2RcvDAO rcv2Dao = new SlabYdL2RcvDAO();
 //PIDEV		
  	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	
	
	/**
	 * GridData - 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData getSelectData(GridData gdReq) throws DAOException {

		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		String      szMsg        = "";
		String      szMethodName = "getSelectData";
		String 	szOperationName = "단순 조회";
		
		try {
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//Grid date 를 JDTORecord data 로 변환
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			commDao.jspSelect(inRecord, outRecSet, inRecord.getFieldString("QUERY_ID"), szMethodName);	
			
			
			//UI로 반환 할 Grid data 를 생성 
			//GridData gdRet = CmUtil.genGridData(gdReq, outRecSet); -- old version
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			//GridData gdRet = commUtils.jdtoRecordToGridData(gdRtn, outRecSet.toList(), gdReq);
			GridData gdRet = CmUtil.genGridData(gdRtn , outRecSet);
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	} // end of getSelectData 
	
	
	/**
	 * 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSelectData(JDTORecord recPara) throws DAOException {

		String      szMsg        = "";
		String      szMethodName = "getSelectData";
		String 	szOperationName = "단순 조회";
		
		try {
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			commDao.jspSelect(recPara, outRecSet, recPara.getFieldString("QUERY_ID"), szMethodName);	
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return outRecSet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	} // end of getSelectData
	
	
	

	/**
	 *  저장위치별 재고 List(C연주 슬라브야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getcSlabYdStkPosList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		String      szMsg        = "";
		String      szMethodName = "getcSlabYdStkPosList";
		String 	szOperationName = "저장위치별 재고 List";
		YdStkBedDao YdStkBedDap  = new YdStkBedDao();
		String szYdDongGp        = "";
		String szYdSpanGp        = "";
		String szYdColGp         = "";
		String szYdBedGp         = "";
		String szHeatNo          = "";
		String szYdStrLocGp		 = "";
		int intRtnVal = 0;

		try {
			

			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		    //recPara.setField("YD_BAY_GP",    		ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP"));
		    //recPara.setField("YD_EQP_GP", 		    ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_GP"));
			
			
			szYdStrLocGp = ydDaoUtils.paraRecChkNull(inDto, "V_YD_STR_LOC");
			
			if(!"".equals(szYdStrLocGp) && szYdStrLocGp.length()==8){ 
				szYdDongGp = szYdStrLocGp.substring(1 , 2);
				szYdSpanGp = szYdStrLocGp.substring(2 , 4);
				szYdColGp  = szYdStrLocGp.substring(4 , 6);
				szYdBedGp  = szYdStrLocGp.substring(6 , 8);
			}else{
				szYdDongGp = ydDaoUtils.paraRecChkNull(inDto, "YD_DONG_GP");
				szYdSpanGp = ydDaoUtils.paraRecChkNull(inDto, "YD_SPAN_GP");
				szYdColGp  = ydDaoUtils.paraRecChkNull(inDto, "YD_COL_GP");
				szYdBedGp  = ydDaoUtils.paraRecChkNull(inDto, "YD_BED_GP");
			}
			
			szHeatNo  = ydDaoUtils.paraRecChkNull(inDto, "HEAT_NO");

			if (szYdDongGp.length() > 1){
				szYdDongGp = szYdDongGp.substring(1,2);
			}

			if (szYdBedGp.length() < 2){
				szYdBedGp = "%";
			}

			recPara.setField("YD_GP",    		    ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
		    recPara.setField("YD_BAY_GP",    		szYdDongGp);
		    recPara.setField("YD_EQP_GP", 		    szYdSpanGp);
		    recPara.setField("YD_COL_GP", 		    szYdColGp);
		    recPara.setField("YD_BED_GP",    		szYdBedGp);
		    recPara.setField("HEAT_NO",    		szHeatNo);
		    recPara.setField("HOLD_GP", yddatautil.setDataDefault(inDto.getField("HOLD_GP"), ""));

			recPara.setField("PAGE_CNT",            inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT",             inDto.getField("ROWCOUNT"));

			intRtnVal = YdStkBedDap.getYdStkbed(recPara, outRecSet, 38);

			if (intRtnVal < 0) {

				szMsg = "저장위치별 재고 List DAO 조회 !!!, ErrorCode:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				return outRecSet;
			} // end of if


			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getcSlabYdStkPosList




	/**
	 *  저장위치별 재고 List(후판슬라브야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getaPlateYdStkPosList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		String      szMsg        = "";
		String      szMethodName = "getaPlateYdStkPosList";
		String 	szOperationName = "저장위치별 재고 List";
		YdStkBedDao YdStkBedDap  = new YdStkBedDao();
		String szYdDongGp        = "";
		String szYdBedGp         = "";
		String szHeatNo          = "";

		int intRtnVal = 0;

		try {


			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		    //recPara.setField("YD_BAY_GP",    		ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP"));
		    //recPara.setField("YD_EQP_GP", 		    ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_GP"));
			szYdDongGp = ydDaoUtils.paraRecChkNull(inDto, "YD_DONG_GP");
			szYdBedGp  = ydDaoUtils.paraRecChkNull(inDto, "YD_BED_GP");
			szHeatNo  = ydDaoUtils.paraRecChkNull(inDto, "HEAT_NO");

			if (szYdDongGp.length() > 1){
				szYdDongGp = szYdDongGp.substring(1,2);
			}

			if (szYdBedGp.length() < 2){
				szYdBedGp = "%";
			}

			recPara.setField("YD_GP",    		    ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
		    recPara.setField("YD_BAY_GP",    		szYdDongGp);
		    recPara.setField("YD_EQP_GP", 		    ydDaoUtils.paraRecChkNull(inDto, "YD_SPAN_GP"));
		    recPara.setField("YD_COL_GP", 		    ydDaoUtils.paraRecChkNull(inDto, "YD_COL_GP"));
		    recPara.setField("YD_BED_GP",    		szYdBedGp);
		    recPara.setField("HEAT_NO",    		szHeatNo);
		    recPara.setField("HOLD_GP", yddatautil.setDataDefault(inDto.getField("HOLD_GP"), ""));

			recPara.setField("PAGE_CNT",            inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT",             inDto.getField("ROWCOUNT"));

			intRtnVal = YdStkBedDap.getYdStkbed(recPara, outRecSet, 39);

			if (intRtnVal < 0) {

				szMsg = "저장위치별 재고 List DAO 조회 !!!, ErrorCode:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				return outRecSet;
			} // end of if


			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getaPlateYdStkPosList



	/**
	 * [A] 오퍼레이션명: 보류해제등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public GridData updateStlHoldstat(GridData inParam) throws DAOException {
		GridData outGrid = null;

		YdStockDao dao = new YdStockDao();
		int result = 0;
		boolean spRet = false;

		try{
				outGrid = dao.updateStlHoldstat(inParam);
			return outGrid;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + " :: " + e.getMessage(), e);
		}
	}

	/**
	 * [A] 오퍼레이션명: 후판슬라브 이상재 보류해제등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 */
	public GridData updateStlHoldstatPa(GridData inParam) throws DAOException {

		GridData outGrid 		= null;
		GridData returnGrid 	= null;
		JDTORecord outRecord 	= JDTORecordFactory.getInstance().create();
		
		int rowCount 	= 0;
		int result 		= 0;
		
		String sABMTL_RSN_CD  			= "";
		String sABMTL_HD_MTD_CD  		= "";
		String sABMTL_GRD  				= "";
		String sABMTL_REM  				= "";
		String szSTL_NO					= "";
		String szYD_USER_ID				= "";
		
		try{
			rowCount 	= inParam.getHeader("CHECK").getRowCount();
			returnGrid 	= OperateGridData.cloneResponseGridData(inParam);
			
			for(int i = 0; i < rowCount; i++){

				szSTL_NO 			= inParam.getHeader("STL_NO").getValue(i);
				sABMTL_RSN_CD 		= CmnUtil.getComboList(inParam, "YD_ABMTL_RSN_CD",i);
				sABMTL_HD_MTD_CD 	= CmnUtil.getComboList(inParam, "YD_ABMTL_HD_MTD_CD",i);
				sABMTL_GRD 			= CmnUtil.getComboList(inParam, "YD_ABMTL_GRD",i);
				sABMTL_REM 			= yddatautil.setDataDefault(inParam.getHeader("YD_ABMTL_REM").getValue(i), "");
				szYD_USER_ID 		= inParam.getParam("YD_USER_ID");
				
				result = updateStlHoldstatPaSub( szSTL_NO,
						                         sABMTL_RSN_CD,
						                         sABMTL_HD_MTD_CD,
						                         sABMTL_GRD,
						                         sABMTL_REM,
						                         szYD_USER_ID);
			}
			if(result > 0){
				inParam.addParam("RESULT", "SUCCESS");
				outGrid = CmnUtil.jdtoRecordToGridData(returnGrid, outRecord, inParam);
			}else{
				inParam.addParam("RESULT", "FAILED");
				outGrid = CmnUtil.jdtoRecordToGridData(returnGrid, outRecord, inParam);
			}
			return outGrid;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + " :: " + e.getMessage(), e);
		}
	}
	
	/**
	 * [A] 오퍼레이션명: 후판슬라브 이상재 보류해제등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 */
	public int updateStlHoldstatPaSub(  String szSTL_NO,
			                            String sABMTL_RSN_CD) throws DAOException {
		return updateStlHoldstatPaSub(szSTL_NO,sABMTL_RSN_CD,"","","","SYSTEM");
		
	}
	/**
	 * [A] 오퍼레이션명: 후판슬라브 이상재 보류해제등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 */
	public int updateStlHoldstatPaSub( String szSTL_NO,
			                           String sABMTL_RSN_CD,
			                           String sABMTL_HD_MTD_CD,
			                           String sABMTL_GRD,
			                           String sABMTL_REM,
			                           String szYD_USER_ID) throws DAOException {
		
		JDTORecord 	inRecord1 	= null;
		JDTORecord 	outRec 		= null;
		JDTORecordSet rsResult 	= null;
		JDTORecord 	recInTemp 	= null;
		
		int intRtnVal 	= 0;
		
		YdStockDao dao = new YdStockDao();
		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();
		YdDelegate ydDelegate = new YdDelegate();
		YdStockDao ydStockDao = new YdStockDao();

		String sPRE_ABMTL_RSN_CD  	= "";
		String szSLAB_WO_RT_CD 		= "";
		String sPROCESS_GP			= "";
		
		String sStatus1 = "";
		String sStatus2 = "";
		String sCurrProgCd = "";
		
		try{
			
			String szCurDateTime 	= YdUtils.getCurDate("yyyyMMddHHmmss");
    		String szRECEIPT_DATE 	= szCurDateTime.substring(0, 8);
			
			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp   = JDTORecordFactory.getInstance().create();
			recInTemp.setField("STL_NO", szSTL_NO);

			intRtnVal = ydStockDao.getYdStock(recInTemp, rsResult, 0);

			rsResult.first();
			recInTemp = rsResult.getRecord();

			sPRE_ABMTL_RSN_CD = ydDaoUtils.paraRecChkNull(recInTemp,"YD_ABMTL_RSN_CD");
			szSLAB_WO_RT_CD	  = ydDaoUtils.paraRecChkNull(recInTemp,"SLAB_WO_RT_CD");
			
				if("".equals(sABMTL_RSN_CD)){

					sPROCESS_GP = "2";	// 이상재 해제
					
					/* com.inisteel.cim.yd.dao.ydstkbeddao.YdStockDao.updatecSlabYdStkHoldGp_02 */
					intRtnVal = dao.updateStlAbMtlRsnCd(szSTL_NO,
														szYD_USER_ID,
								   					    "",
								   					    "",
														"",
														"",
														"");
					
					inRecord1 	= JDTORecordFactory.getInstance().create();
				    inRecord1.setField("STL_NO"				, szSTL_NO);
				    inRecord1.setField("YD_ABMTL_REL_DD"	, szRECEIPT_DATE);
				    inRecord1.setField("REGISTER"			, szYD_USER_ID);
					 
				    /*com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.updYdAbSlabHist*/
				    intRtnVal = ydWrkHistDao.updYdAbSlabHist(inRecord1);
					
				}else{
					
					sPROCESS_GP = "1";  // 이상재 등록
					
					/*
					 * 2014.03.14 윤재광
					 * 보류 등록시 최신정보를 가져와서 다시 한번 체크한다.
					 * 등록불가면 그냥 리턴한다.
					 */
					{
						rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
						recInTemp   = JDTORecordFactory.getInstance().create();
						recInTemp.setField("STL_NO", szSTL_NO);
						
						/* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getStockTbCtMPlmplspec */
						/*
						SELECT 
						   A.CURR_PROG_CD  
						  ,B.CT_MILL_SPEC_WRK_STAT_GP
						  ,DECODE(B.CT_MILL_SCH_WRK_STAT_GP,'H','1',B.CT_MILL_SCH_WRK_STAT_GP) AS CT_MILL_SCH_WRK_STAT_GP 
						FROM  TB_PT_SLABCOMM A,
						      TB_CT_M_PLMPLSPEC B   --날판사양
						WHERE A.SLAB_NO = B.STL_NO(+)
						  AND A.SLAB_NO = :SLAB_NO
						*/  
						intRtnVal = ydStockDao.getYdStockTbCtMPlmplspec(recInTemp, rsResult);
						
						if(rsResult.size() > 0){
							rsResult.first();
							recInTemp = rsResult.getRecord();
							
							sCurrProgCd	= ydDaoUtils.paraRecChkNull(recInTemp,"CURR_PROG_CD");
							sStatus1  	= ydDaoUtils.paraRecChkNull(recInTemp,"CT_MILL_SPEC_WRK_STAT_GP");
							sStatus2  	= ydDaoUtils.paraRecChkNull(recInTemp,"CT_MILL_SCH_WRK_STAT_GP");
						}
						
						if(sCurrProgCd.equals("C")){
							return -1;
						}
						
						if(sCurrProgCd.equals("B")){
							if(sStatus1.equals("1") && sStatus2.equals("1")){
							}else{
								return -1;
							}
						}
					}
					
					/* com.inisteel.cim.yd.dao.ydstkbeddao.YdStockDao.updatecSlabYdStkHoldGp_02 */
					intRtnVal = dao.updateStlAbMtlRsnCd(szSTL_NO,
														szYD_USER_ID,
														sABMTL_RSN_CD,
														sABMTL_HD_MTD_CD,
														sABMTL_GRD,
														sABMTL_REM,
														szRECEIPT_DATE);
					
					inRecord1 	= JDTORecordFactory.getInstance().create();
				    inRecord1.setField("STL_NO"				, szSTL_NO);
				    inRecord1.setField("YD_ABMTL_RSN_CD"	, sABMTL_RSN_CD);
				    inRecord1.setField("YD_ABMTL_HD_MTD_CD"	, sABMTL_HD_MTD_CD);
				    inRecord1.setField("YD_ABMTL_GRD"		, sABMTL_GRD);
				    inRecord1.setField("YD_ABMTL_REM"		, sABMTL_REM);
				    inRecord1.setField("YD_ABMTL_ASGN_DD"	, szRECEIPT_DATE);
				    inRecord1.setField("REGISTER"			, szYD_USER_ID);
					
					/*com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdAbSlabHist*/
				    intRtnVal = ydWrkHistDao.insYdAbSlabHist(inRecord1);
				}

				outRec 	= JDTORecordFactory.getInstance().create();
   
				if("1".equals(sPROCESS_GP)){
					if(sABMTL_RSN_CD.startsWith("H")||
					   sABMTL_RSN_CD.startsWith("K")){
						outRec.setField("JMS_TC_CD"         , "YDCTJ035");
					}else{
						outRec.setField("MSG_ID"			, "YDYDJ298");
					}
				}else{
					if(sPRE_ABMTL_RSN_CD.startsWith("H")||
					   sPRE_ABMTL_RSN_CD.startsWith("K")){
						outRec.setField("JMS_TC_CD"         , "YDCTJ035");
					}else{
						outRec.setField("MSG_ID"			, "YDYDJ298");
					}
				}
				
				outRec.setField("SLAB_NO"           , szSTL_NO);
				outRec.setField("PTOP_PLNT_GP"      , szSLAB_WO_RT_CD);
				outRec.setField("AB_OCCR_RSN_CD"    , sABMTL_RSN_CD);
				outRec.setField("REGISTER"          , szYD_USER_ID);
				outRec.setField("PROCESS_GP"        , sPROCESS_GP);

				ydDelegate.sendMsg(outRec);
		
				return intRtnVal;
				
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + " :: " + e.getMessage(), e);
		}
	}



	/**
	 * 저장품 공통  항목을 조회한다.(검색어, 업무영역코드)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdStock(JDTORecord inDto) throws DAOException {

		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getSlabYdStkPosSet";
		String szOperationName = "저장품 공통  항목을 조회";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


			recPara.setField("STL_NO", 	ydDaoUtils.paraRecChkNull(inDto, "STL_NO"));

			YdStockDao ydStockDao = new YdStockDao();

			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 1);

			if (intRtnVal < 0) {
				szMsg = "[JSP Session : "+szOperationName + "] 조회중 ERROR ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				return outRecSet;

			 } // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


		return outRecSet;
	}


	/**
	 * 슬라브 공통  항목을 조회한다.(검색어, 업무영역코드)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getPtSlabComm(JDTORecord inDto) throws DAOException {

		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getYdStock";
		String szOperationName = "슬라브 공통 항목 조회";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet rSetStock = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recStock = JDTORecordFactory.getInstance().create();
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		String szMtlItem = "";
		String szYdGp = "";

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


			YdStockDao ydStockDao = new YdStockDao();

			szYdGp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");


			recPara.setField("STL_NO", 	ydDaoUtils.paraRecChkNull(inDto, "STL_NO"));



			//야드 저장품 테이블에서 품목코드를 읽어온다

			intRtnVal =ydStockDao.getYdStock(recPara, rSetStock, 0);


			if(intRtnVal < 0) {
				szMsg = "ydStockDao  조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;
			} else if(intRtnVal == 0){
				szMsg = "ydStockDao  조회 데이터 없음!!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				return outRecSet;

			}

			rSetStock.first();
			recStock = rSetStock.getRecord();

			szMtlItem = ydDaoUtils.paraRecChkNull(recStock, "YD_MTL_ITEM");


			if(szMtlItem.length()>0){
				szMtlItem = szMtlItem.substring(0, 1);
			}


			//품목데이터 시작이 'B'이면 주편 공통 테이블정보를 읽어와서 보여준다.

			recPara = JDTORecordFactory.getInstance().create();



			if (szYdGp.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)){
				//A후판 슬라브야드 일경우는 슬라브공통정보 및 생산통제정보를 읽어와서 보내준다
				//2009.08.19 수정 _이현성
				recPara.setField("STL_NO", 	inDto.getField("STL_NO"));
				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 117);

			}
			else if ("B".equals(szMtlItem))
			{
				recPara.setField("STL_NO", 	inDto.getField("STL_NO"));
				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 5);

			} else if ("S".equals(szMtlItem)){
				//품목 데이터 시작이 'S'이면 슬라브공통테이블 정보를 읽어와서 보여준다.
				recPara.setField("STL_NO", 	inDto.getField("STL_NO"));
				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 1);

			} else{
				recPara.setField("STL_NO", 	inDto.getField("STL_NO"));
				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 1);
			}


			if (intRtnVal < 0) {
				szMsg = "[JSP Session : "+szOperationName+"] 조회중 ERROR ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			} // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}


		szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return outRecSet;
	}


	/**
	 * 슬라브 공통  항목을 조회한다.(검색어, 업무영역코드)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getPtSlabComm_backup(JDTORecord inDto) throws DAOException {

		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getPtSlabComm_backup";
		String szOperationName = "슬라브 공통 항목 조회";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		try {


			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			YdStockDao ydStockDao = new YdStockDao();

			recPara.setField("STL_NO", 	ydDaoUtils.paraRecChkNull(inDto, "STL_NO"));

			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 1);

			if (intRtnVal < 0) {
				szMsg = "[JSP Session : "+szOperationName+"] 조회 ERROR ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		return outRecSet;
	}

	/**
	 *저장위치 좌표설정화면 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabYdStkPosSet(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getSlabYdStkPosSet";
		String szOperationName = "좌표설정화면 조회";

		JDTORecord recPara      = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		YdStkColDao ydStkcolDao = new YdStkColDao();


		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


			if (ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_NO").trim().equals("ALL"))

			{
				recPara.setField("YD_GP", 	      inDto.getField("YD_GP"));
				recPara.setField("YD_BAY_GP",     inDto.getField("YD_BAY_GP"));
				recPara.setField("YD_EQP_GP",     inDto.getField("YD_EQP_GP"));

				intRtnVal = ydStkcolDao.getYdStkcol(recPara,outRecSet,1);

			} else{
				recPara.setField("YD_GP", 		  inDto.getField("YD_GP"));
				recPara.setField("YD_BAY_GP", 	  inDto.getField("YD_BAY_GP"));
				recPara.setField("YD_EQP_GP", 	  inDto.getField("YD_EQP_GP"));
				recPara.setField("YD_STK_COL_NO", inDto.getField("YD_STK_COL_NO"));

				intRtnVal = ydStkcolDao.getYdStkcol(recPara,outRecSet,2);

			}

			if (intRtnVal < 0) {
				szMsg = "[JSP Session : "+szOperationName+"] 조회 ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


		return outRecSet;
	}	// end of getSlabYdStkPosSet



	/**
	 *저장위치 좌표설정화면 베드 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabYdStkPosSetBed(JDTORecord inDto) throws DAOException {

		//for Log
		String szMsg="";
		String szMethodName="getSlabYdStkPosSetBed";
		int intRtnVal = 0;
		String szOperationName = "저장위치 좌표설정화면 베드 조회";

		String szEditStkPos =null;
		String szEditStkCol =null;
		String szEditStkBed =null;


		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecord recEdit = JDTORecordFactory.getInstance().create();
		JDTORecordSet retRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		YdStkBedDao ydStkbedDao = new YdStkBedDao();

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


		   //적치열구분을 Parameter 로 Set
			recPara.setField("YD_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_GP"));

			intRtnVal = ydStkbedDao.getYdStkbed(recPara,outRecSet,1);


			if (intRtnVal < 0) {
				szMsg = "[JSP Session : "+szOperationName+"] 조회 ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;
			} else if  (intRtnVal == 0) {
				szMsg = "[JSP Session : "+szOperationName+"] 데이터 없음 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				return outRecSet;
			}

			//JDTORecordSet 에 첫 위치로 이동시킨다.
			outRecSet.first();

			do {

				//스케줄 코드를  설비구분을 분리하여 YD_EQP_GP 컬럼에 넣어준다.
				recEdit      = outRecSet.getRecord();
				szEditStkCol = yddatautil.setDataDefault(recEdit.getField("YD_STK_COL_GP"), "");
				szEditStkBed = yddatautil.setDataDefault(recEdit.getField("YD_STK_BED_NO"), "");
				szEditStkPos = szEditStkCol + "-"+ szEditStkBed;

				recEdit.setField("YD_STK_POS", szEditStkPos);

				retRecSet.addRecord(recEdit);

			}while(outRecSet.next());


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		return retRecSet;
	}	// end of getSlabYdStkPosSetBed



	/**
	 * 저장위치 좌표설정화면 열 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String updSlabYdStkPosSet(JDTORecord [] inDto) throws DAOException {


		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="updSlabYdStkPosSet";
		String szYdgp = "";
		String szRtnValue = YdConstant.RETN_CD_SUCCESS;
		String szOperationName = "저장위치 좌표설정화면 열 수정";
		String szMsgId = "";

		YdDelegate ydDelegate = new YdDelegate();

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdStkColDao ydStkcolDao = new YdStkColDao();

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			//메뉴 페이지 목록을 수정한다.
			for(int x=0;x<inDto.length;x++){


				//수정할 항목 SETTING

				//적치열번호
				recPara.setField("YD_STK_COL_NO",yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_NO"), ""));

				//활성상태
				recPara.setField("YD_STK_COL_ACT_STAT", yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_ACT_STAT"), ""));

				//기준 X축
				recPara.setField("YD_STK_COL_RULE_XAXIS",yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_RULE_XAXIS"), "0"));

				//기준Y축
				recPara.setField("YD_STK_COL_RULE_YAXIS",yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_RULE_YAXIS"), "0"));


				//폭
				recPara.setField("YD_STK_COL_W", yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_W"), "0"));


				//길이
				recPara.setField("YD_STK_COL_L", yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_L"), "0"));


				//적치열 구분 * 필수
				recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"), ""));


				intRtnVal = ydStkcolDao.updYdStkcol(recPara,0);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
				} // end of if



				// C연주 슬라브야드, A후판 슬라브야드 , 코일야드,  코일제품야드, 후판제품야드
				// L2 송신 정보 생성
				// 적치열 정보 수정후 야드별 L2 정보로 송신기능

				szYdgp = yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"), " ").substring(0,1);



				if(szYdgp.equals("")){
					// 적치열 정보가 맞지 않는경우는
					szMsg = "JSP-SESSION [저장위치 좌표설정화면 열 수정] 적치열 정보가 맞지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					szRtnValue = YdConstant.RETN_CD_FAILURE;
					//return szRtnValue;
					continue;
				}else if(szYdgp.equals(YdConstant.YD_GP_INTGR_YARD)){
					// 통합 슬라브야드는 L2 가 없으므로 송신하지 않는다.
					szMsg = "JSP-SESSION [저장위치 좌표설정화면 열 수정] 통합슬라브야드는 적치열 정보를 L2로 송신하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					//return szRtnValue;
					continue;

				}


				if(szYdgp.equals(YdConstant.YD_GP_C_SLAB_YARD)){
					szMsgId = "YDY1L001";
				} else if (szYdgp.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)){
					szMsgId = "YDY3L001";
				} else if (szYdgp.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD)){
					szMsgId = "YDY5L001";
				} else if (szYdgp.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD)){
					szMsgId = "YDY5L001";
				} else if (szYdgp.equals(YdConstant.YD_GP_PLATE_GDS_YARD)){
					szMsgId = "YDY4L001";
				} else if (szYdgp.equals(YdConstant.YD_GP_PORT_SLAB_YARD)){  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					szMsgId = "YDE7L001";
				}

				recPara = JDTORecordFactory.getInstance().create();

				recPara.setField("MSG_ID", szMsgId);
				recPara.setField("YD_INFO_SYNC_CD", "3");  //3 은 열정보 4는 베드 정보
				recPara.setField("YD_GP", 			szYdgp);
				recPara.setField("YD_STK_COL_GP", 	yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"), ""));
				recPara.setField("YD_STK_BED_NO", 	"");

				 szMsg = "[Jsp-Session "+szOperationName+" ] L2 로 적치열 수정된 정보 송신 시작" ;
				 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				 ydUtils.displayRecord(szOperationName, recPara);
				 ydDelegate.sendMsg(recPara);

				 szMsg = "[Jsp-Session "+szOperationName+" ] L2 로 적치열 수정된 정보 송신 완료" ;
				 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			}

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


			return szRtnValue;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}
	}	// end of updSlabYdStkPosSet



	/**
	 * 저장위치 좌표설정화면 열 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void insSlabYdStkPosSet(JDTORecord [] inDto) throws DAOException {


		int intRtnVal       = 0;
		String szMsg        = "";
		String szMethodName = "insSlabYdStkPosSet";
		String szOperationName = "좌표설정화면 열 등록";

		String szYdStkColGp = null;
		JDTORecord recPara  = JDTORecordFactory.getInstance().create();
		YdStkColDao ydStkcolDao = new YdStkColDao();

		try {


			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			//메뉴 페이지 목록을 수정한다.
			for(int x=0;x<inDto.length;x++){

				//등록  항목 SETTING

				//적치열번호 필수
				recPara.setField("YD_STK_COL_NO", inDto[x].getField("YD_STK_COL_NO"));


				//야드구분
				recPara.setField("YD_GP", inDto[x].getField("YD_GP"));

				//야드동구분
				recPara.setField("YD_BAY_GP", inDto[x].getField("YD_BAY_GP"));

				//야드설비구분
				recPara.setField("YD_EQP_GP", inDto[x].getField("YD_EQP_GP"));

				//활성상태
				recPara.setField("YD_STK_COL_ACT_STAT", inDto[x].getField("YD_STK_COL_ACT_STAT"));

				//기준 X축
				recPara.setField("YD_STK_COL_RULE_XAXIS", inDto[x].getField("YD_STK_COL_RULE_XAXIS"));


				//기준Y축
				recPara.setField("YD_STK_COL_RULE_YAXIS", inDto[x].getField("YD_STK_COL_RULE_YAXIS"));

				//폭
				recPara.setField("YD_STK_COL_W", inDto[x].getField("YD_STK_COL_W"));

				//길이
				recPara.setField("YD_STK_COL_L", inDto[x].getField("YD_STK_COL_L"));

				//적치열 구분 * 필수
				szYdStkColGp = inDto[x].getFieldString("YD_GP")
							+  inDto[x].getFieldString("YD_BAY_GP")
							+  inDto[x].getFieldString("YD_EQP_GP")
							+  inDto[x].getFieldString("YD_STK_COL_NO");
				recPara.setField("YD_STK_COL_GP", szYdStkColGp.trim());

				intRtnVal = ydStkcolDao.insYdStkcol(recPara);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
				} // end of if

			}


			szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}
	}// end of updSlabYdStkPosSet



	/**
	 *저장위치 좌표설정화면 열 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void delSlabYdStkPosSet(JDTORecord [] inDto) throws DAOException {


		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="delSlabYdStkPosSet";
		String szOperationName = "좌표설정화면 열 삭제";


		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdStkColDao ydStkcolDao = new YdStkColDao();

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//메뉴 페이지 목록을 수정한다.
			for(int x=0;x<inDto.length;x++){


				//삭제 KEY SETTING

				//적치열번호
				recPara.setField("YD_STK_COL_NO", inDto[x].getField("YD_STK_COL_NO"));

				//야드 구분
				recPara.setField("YD_GP", 		inDto[x].getField("YD_GP"));

				//동구분
				recPara.setField("YD_BAY_GP", 	inDto[x].getField("YD_BAY_GP"));

				//설비구분
				recPara.setField("YD_EQP_GP", 	inDto[x].getField("YD_EQP_GP"));


				//적치 BED 정보가 있는지 확인

				intRtnVal = ydStkcolDao.getYdStkcol(recPara,outRecSet,2);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
				} // end of if


				//적치 BED 정보가 없을경우 삭제
				if (intRtnVal == 0)
				{
					//삭제유무
					recPara.setField("DEL_YN", 	"Y");
					szMsg = "[JSP Session : "+szOperationName+"]적치열 정보 삭제 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


					intRtnVal = ydStkcolDao.updYdStkcol(recPara,0);



				}
			}



			szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}
	}	// end of delSlabYdStkPosSet




	/**
	 * 저장위치 좌표설정화면 BED 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void insSlabYdStkPosSetBed(JDTORecord [] inDto) throws DAOException {


		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="insSlabYdStkPosSetBed";
		String szOperationName = "저장위치 좌표설정화면 BED 등록";

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdStkBedDao ydStkbedDao = new YdStkBedDao();
		try {
			//저장위치 좌표설정화면 열 등록.

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			for(int x=0;x<inDto.length;x++){

				//등록  항목 SETTING

				//적치열구분 필수
				recPara.setField("YD_STK_COL_GP", inDto[x].getField("YD_STK_COL_GP").toString().trim());


				//적치열번호 필수
				recPara.setField("YD_STK_BED_NO", inDto[x].getField("YD_STK_BED_NO"));

				//야드저장집합코드 NOT NULL
				recPara.setField("YD_STR_GTR_CD","TESTYD");

				recPara.setField("YD_STK_BED_TP",	     inDto[x].getField("YD_STK_BED_TP"));
				recPara.setField("YD_STK_BED_L_GP",      inDto[x].getField("YD_STK_BED_L_GP"));
				recPara.setField("YD_STK_BED_W_GP",      inDto[x].getField("YD_STK_BED_W_GP"));
				recPara.setField("YD_STK_BED_DIR_GP",    inDto[x].getField("YD_STK_BED_DIR_GP"));
				recPara.setField("YD_STK_BED_ACT_STAT",  inDto[x].getField("YD_STK_BED_ACT_STAT"));
				recPara.setField("YD_STK_BED_WHIO_STAT", inDto[x].getField("YD_STK_BED_WHIO_STAT"));
				recPara.setField("YD_STK_BED_XAXIS",     inDto[x].getField("YD_STK_BED_XAXIS"));
				recPara.setField("YD_STK_BED_YAXIS",     inDto[x].getField("YD_STK_BED_YAXIS"));
				recPara.setField("YD_STK_BED_ZAXIS",     inDto[x].getField("YD_STK_BED_ZAXIS"));
				recPara.setField("YD_STK_BED_LYR_MAX",   inDto[x].getField("YD_STK_BED_LYR_MAX"));
				recPara.setField("YD_STK_BED_WT_MAX",    inDto[x].getField("YD_STK_BED_WT_MAX"));
				recPara.setField("YD_STK_BED_H_MAX",     inDto[x].getField("YD_STK_BED_H_MAX"));
				recPara.setField("YD_STK_BED_L_MAX",     inDto[x].getField("YD_STK_BED_L_MAX"));
				recPara.setField("YD_STK_BED_W_MAX",     inDto[x].getField("YD_STK_BED_W_MAX"));
				recPara.setField("YD_STR_GTR_CD",        inDto[x].getField("YD_STR_GTR_CD"));


				intRtnVal = ydStkbedDao.insYdStkbed(recPara);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
				} // end of if


			}


			szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}
	}// end of insSlabYdStkPosSetBed




	/**
	 * 저장위치 좌표설정화면 BED 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public String updSlabYdStkPosSetBed(JDTORecord [] inDto)  throws DAOException  {


		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="updSlabYdStkPosSetBed";
		String szYdgp = "";
		String szRtnValue = YdConstant.RETN_CD_SUCCESS;
		String szMsgId = "";
		String szOperationName = "좌표설정화면 BED 수정";
		YdDelegate ydDelegate = new YdDelegate();

		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		YdStkBedDao ydStkbedDao = new YdStkBedDao();
		try {
			//저장위치 좌표설정화면 BED 수정

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			for(int x=0;x<inDto.length;x++){
				//수정할 항목 SETTING

				ydUtils.displayRecord(szOperationName, inDto[x]);

				//적치열구분 필수

				recPara.setField("YD_STK_COL_GP",yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"), ""));
				recPara.setField("YD_STK_BED_NO",yddatautil.setDataDefault(inDto[x].getField("YD_STK_BED_NO"), ""));


				recPara.setField("YD_STK_COL_GP"         ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_COL_GP"));
				recPara.setField("YD_STK_BED_NO"         ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_NO"));
				recPara.setField("YD_STR_GTR_CD"         ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STR_GTR_CD"));
				recPara.setField("YD_STK_BED_TP"         ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_TP"));
				recPara.setField("YD_STK_BED_L_GP"       ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_L_GP"));
				recPara.setField("YD_STK_BED_W_GP"       ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_W_GP"));
				recPara.setField("YD_STK_BED_DIR_GP"     ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_DIR_GP"));
				recPara.setField("YD_STK_BED_ACT_STAT"   ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_ACT_STAT"));
				recPara.setField("YD_STK_BED_WHIO_STAT"  ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_WHIO_STAT"));
				recPara.setField("YD_STK_BED_XAXIS"      ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_XAXIS"));
				recPara.setField("YD_STK_BED_YAXIS"      ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_YAXIS"));
				recPara.setField("YD_STK_BED_ZAXIS"      ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_ZAXIS"));
				recPara.setField("YD_STK_BED_LYR_MAX"    ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_LYR_MAX"));
				recPara.setField("YD_STK_BED_WT_MAX"     ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_WT_MAX"));
				recPara.setField("YD_STK_BED_H_MAX"      ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_H_MAX"));
				recPara.setField("YD_STK_BED_L_MAX"      ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_L_MAX"));
				recPara.setField("YD_STK_BED_W_MAX"      ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_W_MAX"));

				recPara.setField("YD_STK_BED_XAXIS_TOL"      ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_XAXIS_TOL"));
				recPara.setField("YD_STK_BED_YAXIS_TOL"      ,ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_YAXIS_TOL"));
				
				intRtnVal = ydStkbedDao.updYdStkbed(recPara,0);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
				} // end of if


				// C연주 슬라브야드, A후판 슬라브야드 , 코일야드,  코일제품야드, 후판제품야드
				// L2 송신 정보 생성
				// 적치베드 정보 수정후 야드별 L2 정보로 송신기능

				szYdgp = yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"), " ").substring(0,1);

				if(szYdgp.equals("")){
					// 적치열 정보가 맞지 않는경우는
					szMsg = "JSP-SESSION [저장위치 좌표설정화면 열 수정] 적치열 정보가 맞지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

					//다음 베드 처리를 계속 진행한다.
					continue;
				}else if(szYdgp.equals(YdConstant.YD_GP_INTGR_YARD)){
					// 통합 슬라브야드는 L2 가 없으므로 송신하지 않는다.
					szMsg = "JSP-SESSION [저장위치 좌표설정화면 열 수정] 통합슬라브야드는 적치열 정보를 L2로 송신하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					//return szRtnValue;
					//다음 베드 처리를 계속 진행한다.
					continue;
				}

				if(szYdgp.equals(YdConstant.YD_GP_C_SLAB_YARD)){
					szMsgId = "YDY1L001";
				} else if (szYdgp.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)){
					szMsgId = "YDY3L001";
				} else if (szYdgp.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD)){
					szMsgId = "YDY5L001";
				} else if (szYdgp.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD)){
					szMsgId = "YDY5L001";
				} else if (szYdgp.equals(YdConstant.YD_GP_PLATE_GDS_YARD)){
					szMsgId = "YDY4L001";
				} else if (szYdgp.equals(YdConstant.YD_GP_PORT_SLAB_YARD)){   //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					szMsgId = "YDE7L001";
				}

				recPara = JDTORecordFactory.getInstance().create();

				recPara.setField("MSG_ID", szMsgId);
				recPara.setField("YD_INFO_SYNC_CD", "4");  //3 은 열정보 4는 베드 정보
				recPara.setField("YD_GP", 			szYdgp);
				recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"), ""));
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(inDto[x], "YD_STK_BED_NO"));

				 szMsg = "[Jsp-Session "+szOperationName+" ] L2 로 적치베드 수정된 정보 송신 시작" ;
				 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				 ydUtils.displayRecord(szOperationName, recPara);
				 ydDelegate.sendMsg(recPara);

				 szMsg = "[Jsp-Session "+szOperationName+" ] L2 로 적치베드 수정된 정보 송신 완료" ;
				 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			}

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			return szRtnValue;

		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);

		} finally{
		}


		return szRtnValue;

	}	// end of updSlabYdStkPosSetBed



	/**
	 * 크레인 관리 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabYdCrnWorkMgt(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
		String szMethodName="getSlabYdCrnWorkMgt";
		String szOperationName = "크레인 관리 조회";

		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);



			recPara.setField("YD_GP", 	    inDto.getField("YD_GP"));
			recPara.setField("YD_BAY_GP", 	inDto.getField("YD_BAY_GP"));
			recPara.setField("YD_SCH_CD", 	inDto.getField("YD_SCH_CD"));
			recPara.setField("YD_EQP_ID", 	inDto.getField("YD_EQP_ID"));
			recPara.setField("PAGE_CNT1",   inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",   inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",    inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",    inDto.getField("ROWCOUNT"));

			//대표 재료만 조회하기 위하여 수정함
			intRtnVal = ydCrnschDao.getYdCrnsch(recPara, outRecSet, 20);


			if (intRtnVal < 0) {
				szMsg = "[JSP Session : "+szOperationName+"] 조회 ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			} // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}	// end of getSlabYdCrnWorkMgt



	/**
	 * 적치열 베드 금지 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabYdBedBanCnc(JDTORecord inDto) throws DAOException {
		int intRtnVal       = 0;
		String szMsg        = "";
		String szYdStkColGp = null;
		String szMethodName = "getSlabYdBedBanCnc";
		String szOperationName = "적치열 베드 금지 조회 ";


		JDTORecord recPara      = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		YdStkBedDao ydStkbedDao = new YdStkBedDao();

		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			//적치열 구분 * 필수
			szYdStkColGp = inDto.getFieldString("YD_GP")
						+  inDto.getFieldString("YD_BAY_GP")
						+  inDto.getFieldString("YD_EQP_GP")
						+  inDto.getFieldString("YD_STK_COL_NO");

			//적치열구분 필수
			recPara.setField("YD_STK_COL_GP",szYdStkColGp);


			//적치 베드
			recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(inDto, "YD_STK_BED_NO"));

			intRtnVal = ydStkbedDao.getYdStkbed(recPara, outRecSet, 0);


			ydUtils.displayRecord(szOperationName, recPara);

			if (intRtnVal < 0) {
				szMsg = "[JSP Session : "+szOperationName+"] 조회 ERROR ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			} // end of if


			szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		return outRecSet;
	}	// end of getSlabYdBedBanCnc




	/**
	 * 적치열 베드 금지 조회 [수정 - 화면에서 야드/동/설비/베드 정보 인자 제거시]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto (저장위치 =  적치열구분(야드,동,스판)+베드번호)
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabYdBedBanCnc1(JDTORecord inDto) throws DAOException {
		int intRtnVal        = 0;
		String szMsg         = "";
		String szStkPos      = null;
		String szYdStkColGp  = null;
		String szYdStkBedNo  = null;
		String szMethodName  ="getSlabYdBedBanCnc1";
		String szOperationName = "적치열 베드 금지 조회";




		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara      = JDTORecordFactory.getInstance().create();

		YdStkBedDao ydStkbedDao = new YdStkBedDao();

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//적치열 구분 * 필수
			szStkPos = ydDaoUtils.paraRecChkNull(inDto, "STKPOS");

			//추가 - 전체길이 체크
			if ( "".equals(szStkPos) ||szStkPos.length()!=8)
			{
				szMsg = "[JSP Session : "+szOperationName+"] 저장위치 정보가 맞지 않습니다. ";
				ydUtils.putLog(szSessionName, szMethodName,szMsg , YdConstant.ERROR);

				return outRecSet ;
			}


			//추가 - 서브 길이 체크 (적치열구분 6자리, 적치베드 2자리)s

			szYdStkColGp = szStkPos.substring(0, 6);
			szYdStkBedNo = szStkPos.substring(6, 8);


			//적치열구분 필수
			recPara.setField("YD_STK_COL_GP",szYdStkColGp);

			//적치 베드
			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);


			ydUtils.displayRecord(szOperationName, recPara);

			intRtnVal = ydStkbedDao.getYdStkbed(recPara, outRecSet, 0);

			if (intRtnVal < 0) {
				szMsg = "[JSP Session : "+szOperationName+"] 조회 ERROR ";

				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			} // end of if

			szMsg = "JSP-SESSION [적치열 베드 금지 조회] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		return outRecSet;
	}	// end of getSlabYdBedBanCnc




	/**
	 * 적치열 베드 금지 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updSlabYdBedBanCnc(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg = "";
		String szMethodName = "updSlabYdStkPosSetBed";

		String szOperationName = "적치열 베드 금지 수정";


		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdStkBedDao ydStkbedDao = new YdStkBedDao();

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			// 저장위치 좌표설정화면 BED 수정
			for (int x = 0; x < inDto.length; x++) {

				// 적치열구분 필수
				recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(
						inDto[x].getField("YD_STK_COL_GP"), ""));

				// 적치 베드
				recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(
						inDto[x].getField("YD_STK_BED_NO"), ""));

				// 활성상태
				recPara.setField("YD_STK_BED_ACT_STAT", yddatautil
						.setDataDefault(inDto[x]
								.getField("YD_STK_BED_ACT_STAT"), ""));

				// 입출입 상태
				recPara.setField("YD_STK_BED_WHIO_STAT", yddatautil
						.setDataDefault(inDto[x]
								.getField("YD_STK_BED_WHIO_STAT"), ""));

				ydStkbedDao.updYdStkbed(recPara, 0);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg,
								YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg,
								YdConstant.ERROR);
					}

				} // end of if

			}


			szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	} // end of updSlabYdBedBanCnc




	/**
	 * 슬라브야드 크레인작업범위등록 조회 (야드/동/설비구분/설비번호를 받을때) - 대차설비 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getslabYdCrnStsSet(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szRtnMsg = null;
		String szLogMsg = null;
		String szPara = null;
		YdEqpDao ydEqpDao = new YdEqpDao();

		String szMethodName="getslabYdCrnStsSet";
		String szOperationName = "크레인작업범위등록 조회";
		String szMsg = "";


		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			// 저장위치 좌표설정화면 BED 조회
			szPara = inDto.getFieldString("YD_GP")
					+ inDto.getFieldString("YD_BAY_GP")
					+ inDto.getFieldString("YD_EQP_GP")
					+ inDto.getFieldString("YD_EQP_NO");

			szLogMsg = "[JSP Session]대차설비 조회 : " + szPara;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg,
					YdConstant.DEBUG);
			// 설비ID
			recPara.setField("YD_EQP_ID", szPara);

			intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 2);

			// 에러체크
			if (intRtnVal == 0) { // 설비가 없는 경우
				szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
				szLogMsg = "[JSP Session]해당 대차설비[" + szPara + "]가 존재하지 않습니다!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg,
						YdConstant.ERROR);
				throw new DAOException(szRtnMsg);
			} else if (intRtnVal < 0) { // 조회시 에러가 발생한 경우
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				szLogMsg = "[JSP Session]해당 대차설비[" + szPara + "]조회시 에러가 발생";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg,
						YdConstant.ERROR);
				throw new DAOException(szRtnMsg);
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			szLogMsg = "[JSP Session]" + getClass().getName()
					+ " : 조회시 에러가 발생 - " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg,
					YdConstant.ERROR);
			throw new DAOException(szRtnMsg);
		} finally {

		}


		szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	} // end of getslabYdCrnStsSet




	/**
	 * 슬라브야드 크레인작업범위등록 조회 (설비 ID 를 받는경우) - 대차설비 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getslabYdCrnStsSetID(JDTORecord inDto)
			throws DAOException {
		int intRtnVal = 0;
		String szRtnMsg = null;
		String szLogMsg = null;
		String szMethodName = "getslabYdCrnStsSetID";
		String szYD_EQP_ID = null;
		String szOperationName = "대차설비 조회";
		String szMsg ="";

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		YdEqpDao ydEqpDao = new YdEqpDao();

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			szYD_EQP_ID = yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"), "");

			szLogMsg = "[JSP Session : "+szOperationName+ "]대차 : " +  szYD_EQP_ID;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

			// 설비ID
			recPara.setField("YD_EQP_ID", szYD_EQP_ID);
			intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 3);

			// 에러체크
			if (intRtnVal == 0) { // 설비가 없는 경우
				szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
				szLogMsg = "[JSP Session]해당 대차설비[" + szYD_EQP_ID	+ "]가 존재하지 않습니다!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new DAOException(szRtnMsg);
			} else if (intRtnVal < 0) { // 조회시 에러가 발생한 경우
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				szLogMsg = "[JSP Session]해당 대차설비[" + szYD_EQP_ID
						+ "]조회시 에러가 발생";
				;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg,
						YdConstant.ERROR);
				throw new DAOException(szRtnMsg);
			}

			szLogMsg = "[JSP Session]대차설비 조회 성공 : " + szYD_EQP_ID;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg,
					YdConstant.DEBUG);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			// throw new DAOException(getClass().getName() + e.getMessage(),e);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			szLogMsg = "[JSP Session]" + getClass().getName()
					+ " : 조회시 에러가 발생 - " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(szRtnMsg);
		} finally {
		}

		szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	} // end of getslabYdCrnStsSetID


	/**
	 * 슬라브야드 크레인상태 조회 (설비 ID 를 받는경우)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getslabYdCrnStsSetById(JDTORecord inDto) throws DAOException {
		int intRtnVal      = 0;
		String szRtnMsg = null;
		String szLogMsg = null;
		String szMethodName= "getslabYdCrnStsSetById";
		String szOperationName = "크레인상태 조회";
		String szYD_EQP_ID = null;
		String szMsg = "";

		JDTORecord recPara      = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		YdEqpDao ydEqpDao = new YdEqpDao();


		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			szYD_EQP_ID	= yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"), "");

			szLogMsg =  "[JSP Session : "+szOperationName+"] 크레인 : " + szYD_EQP_ID;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

			//설비ID
			recPara.setField("YD_EQP_ID", szYD_EQP_ID);
			intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 6);

			//에러체크
			if( intRtnVal == 0 ) {				//설비가 없는 경우
				szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
				szLogMsg =  "[JSP Session : "+szOperationName+"] 크레인 : " + szYD_EQP_ID + "]가 존재하지 않습니다!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new DAOException(szRtnMsg);
			}else if( intRtnVal < 0 ) {			//조회시 에러가 발생한 경우
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				szLogMsg =  "[JSP Session : "+szOperationName+"] 크레인 : " + szYD_EQP_ID + "]조회시 에러 발생";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new DAOException(szRtnMsg);
			}

			szLogMsg = "[JSP Session]크레인설비 조회 성공: " + szYD_EQP_ID;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			//throw new DAOException(getClass().getName() + e.getMessage(),e);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			szLogMsg = "[JSP Session]" + getClass().getName() + " : 조회시 에러가 발생 - " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(szRtnMsg);
		} finally {

		}
		szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		return outRecSet;
	}	// end of getslabYdCrnStsSetById



	/**
	 *  슬라브야드 차량진행관리 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getslabYdCarWorkList(JDTORecord inDto) throws DAOException {

		JDTORecord recPara      = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		YdCarSchDao ydCarschDao = new YdCarSchDao();
		JDTORecordSet retRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		String szMsg         = "";
		String szMethodName  = "getslabYdCarWorkList";
		String szOperationName = "차량진행관리 조회";

		JDTORecord revRec    = JDTORecordFactory.getInstance().create();

		String chkWorkStat   = null;
		String carUseGp      = null;
		String out_plant     = null;
		String arr_plant     = null;


		int intRtnVal = 0;

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("YD_GP1",yddatautil.    setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_GP2",yddatautil.    setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_EQP_GP",yddatautil. setDataDefault(inDto.getField("CAR_GP"), ""));
			recPara.setField("PAGE_CNT1",            inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",            inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",             inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",             inDto.getField("ROWCOUNT"));

			intRtnVal = ydCarschDao.getYdCarsch(recPara, outRecSet, 2);

			if (intRtnVal <= 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				else if(intRtnVal == 0) {
						szMsg = "데이터가 없습니다:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				}
				else
				{
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return outRecSet;
			} // end of if


			//레코드셋이 없을때까지 반복한다.
			outRecSet.first();
			do
			{
				revRec = outRecSet.getRecord();

				//차량 사용구분에 따른 차량번호 사용

				carUseGp      =  yddatautil.setDataDefault(revRec.getField("YD_CAR_USE_GP"), "");
				out_plant     =  yddatautil.setDataDefault(revRec.getField("YD_CARLD_STOP_LOC"), " ").toString().substring(0, 1); //불출공장
				arr_plant     =  yddatautil.setDataDefault(revRec.getField("YD_CARUD_STOP_LOC"), " ").toString().substring(0, 1); //착지공장


				//불출공장
				revRec.setField("OUT_PLANT",out_plant);

				//착지공장
				revRec.setField("ARR_PLANT",arr_plant);


				if ("".equals(carUseGp)){
					revRec.setField("CAR_NO", "차량사용유무없음");

				}else if(YdConstant.YD_CAR_USE_GP_TS.equals(carUseGp)){
					//구내운송차량
					revRec.setField("CAR_NO", revRec.getField("TRN_EQP_CD"));

				}else if(YdConstant.YD_CAR_USE_GP_DM.equals(carUseGp)){
					//출하 차량
					//처리 하지않아도 된다.
				}

				chkWorkStat = ydDaoUtils.paraRecChkNull(revRec, "YD_CAR_PROG_STAT");


				//상차 정보 SETTING
				if ("1".equals(chkWorkStat)||"2".equals(chkWorkStat) ||"3".equals(chkWorkStat)
						|| "4".equals(chkWorkStat) || "5".equals(chkWorkStat))
					{
						revRec.setField("T_STOP_LOC",revRec.getField("YD_CARLD_STOP_LOC"));
						revRec.setField("T_LEV_DT",  revRec.getField("YD_CARLD_LEV_DT"));
						revRec.setField("T_ARR_DT",  revRec.getField("YD_CARLD_ARR_DT"));
						revRec.setField("T_ST_DT",   revRec.getField("YD_CARLD_ST_DT"));
						revRec.setField("T_CMPL_DT", revRec.getField("YD_CARLD_CMPL_DT"));

				} //하차 정보 세팅상차 정보 SETTING
				else if("A".equals(chkWorkStat) || "B".equals(chkWorkStat) || "C".equals(chkWorkStat)
						|| "D".equals(chkWorkStat) || "E".equals(chkWorkStat)) {
						revRec.setField("T_STOP_LOC", 	revRec.getField("YD_CARUD_STOP_LOC"));
						revRec.setField("T_LEV_DT",   	revRec.getField("YD_CARUD_LEV_DT"));
						revRec.setField("T_ARR_DT", 	revRec.getField("YD_CARUD_ARR_DT"));
						revRec.setField("T_ST_DT", 		revRec.getField("YD_CARUD_ST_DT"));
						revRec.setField("T_CMPL_DT",	revRec.getField("YD_CARUD_CMPL_DT"));
				}
				else{
					// 야드 설비작업상태  맞지않을경우!!!
				}

				//RECORDSET에 ADD
				retRecSet.addRecord(revRec);

			}while(outRecSet.next());

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return retRecSet;
	}





	/**
	 * 설비 고장/정상설정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String[]
	 * @throws DAOException
	 */
	public String[] updSlabYdCrnStsSetCrnStat(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szLogMsg = null;
		String[] szRtnMsg = null;

		String szMethodName="updSlabYdCrnStsSetCrnStat";
		String szYD_EQP_ID = null;
		String szYD_EQP_STAT = null;
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		JDTORecord recEqpInfo = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("rsEqpInfo");


		YdEqpDao ydEqpDao = new YdEqpDao();
		szRtnMsg = new String[inDto.length];

		String szRcvTcCode = "";
		EJBConnector ejbConn = null;
		String szYdGp = "";
		String szEjbConName ="";
		String szYD_EQP_STAT_Temp = "";

		String szOperationName = "설비 고장/정상설정";
		String szMsg = "";
		String szYD_EQP_STAT_Comp = "";


		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
				szYD_EQP_ID = yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), "");
				if(szYD_EQP_ID.equals("")){
					szLogMsg = "[" + x + "] 설비ID값이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					continue ;
				}

				szYD_EQP_STAT = yddatautil.setDataDefault(inDto[x].getField("YD_EQP_STAT"), "");
				if(szYD_EQP_STAT.equals("")){
					szLogMsg = "[" + x + "] 설비상태 값이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					continue ;
				}

				recPara.setField("MSG_ID"   , "YD_JSP");
				recPara.setField("YD_EQP_ID", szYD_EQP_ID);




				if(szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_IDLE)){
					szYD_EQP_STAT_Temp = YdConstant.YD_EQP_STAT_NORM;
					recPara.setField("YD_EQP_PAUSE_CODE", "0000");
				}else if(szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)){
					szYD_EQP_STAT_Temp = YdConstant.YD_EQP_STAT_BREAK;
					recPara.setField("YD_EQP_PAUSE_CODE", "STOP");
				} else{
					szYD_EQP_STAT_Temp = YdConstant.YD_EQP_STAT_NORM;
					recPara.setField("YD_EQP_PAUSE_CODE", "0000");
				}



				//------------------------------------------------------------------------------------------------
				// 현 DB 정보와 CHECK  : 처리사유는 현재 설비상태가 진도코드와 중복하여 사용하므로
				//                       정상인 상태에서 두번처리하여 진도코드가 초기화 될 우려가 있음
				// 1. 고장이고 넘겨온 정보가 고장일때 => 처리할 필요없음
				// 2. 고장이 아니고 넘겨온 정보가 고장이 아닐때 => 처리할 필요없음
				//
				//------------------------------------------------------------------------------------------------

				recEqpInfo = JDTORecordFactory.getInstance().create();
				rsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("rsEqpInfo");

				intRtnVal = ydEqpDao.getYdEqp(recPara, rsEqpInfo, 0);


				if(intRtnVal < 0 ){
					szMsg = "[JSP Session : "+szOperationName+"] 해당 설비 조회시 ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

					szRtnMsg[x] = "해당 설비 조회시 ERROR";
					return szRtnMsg;


				}else if(intRtnVal == 0){
					szMsg = "[JSP Session : "+szOperationName+"] 해당 설비가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

					szRtnMsg[x] = "해당 설비가 존재하지 않습니다.";
					return szRtnMsg;

				}

				rsEqpInfo.first();
				recEqpInfo = rsEqpInfo.getRecord();


				szYD_EQP_STAT_Comp = ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT");


				if(szYD_EQP_STAT_Comp.equals(YdConstant.YD_EQP_STAT_BREAK) && szYD_EQP_STAT_Temp.equals(YdConstant.YD_EQP_STAT_BREAK)){
					szMsg = "[JSP Session : "+szOperationName+"] 변경된 내용이 없습니다. ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					szRtnMsg[x] = "변경된 내용이 없습니다.";
					return szRtnMsg;

				}else if( (!szYD_EQP_STAT_Comp.equals(YdConstant.YD_EQP_STAT_BREAK) ) && ( !szYD_EQP_STAT_Temp.equals(YdConstant.YD_EQP_STAT_BREAK)) ){
					szMsg = "[JSP Session : "+szOperationName+"] 변경된 내용이 없습니다. ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					szRtnMsg[x] =  "변경된 내용이 없습니다.";
					return szRtnMsg;
				}


				//------------------------------------------------------------------------------------------------


				recPara.setField("YD_EQP_STAT"        , szYD_EQP_STAT_Temp);
				recPara.setField("YD_EQP_TRBL_RCVR_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));


				//------------------------------------------------------------------------------------------------
				//복구 리스케줄 호출
				// C연주 슬라브야드 L2 : Y1YDL004 rcvY1EqpTrblRcvrWr  EqpTrackingFaEJB
				// 항만 슬라브야드 L2 : E7YDL004 rcvY1EqpTrblRcvrWr  EqpTrackingFaEJB
				// A후판 슬라브야드 L2 : Y3YDL004 rcvY3EqpTrblRcvrWr  EqpTrackingFaEJB
				// 후판 제품야드 	 L2 : Y4YDL004 rcvY4EqpTrblRcvrWr  EqpTrackingFaEJB
				// C열연 코일야드   L2 : Y5YDL004 rcvY5EqpTrblRcvrWr  EqpTrackingFaEJB
				//------------------------------------------------------------------------------------------------


				//야드구분
				szYdGp = szYD_EQP_ID.substring(0,1);


				szLogMsg = "[JSP SESSION -  (야드구분  :  " + szYdGp +") ] 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

				if(szYdGp.equals(YdConstant.YD_GP_C_SLAB_YARD)){
					//직접 EJB호출
					szRcvTcCode  = "Y1YDL004";
					szEjbConName = "procY1EqpTrblRcvrWr";
				}else if( szYdGp.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)){
					szRcvTcCode  = "Y3YDL004";
					szEjbConName = "procY3EqpTrblRcvrWr";
				}else if( szYdGp.equals(YdConstant.YD_GP_PLATE_GDS_YARD)){
					szRcvTcCode  = "Y4YDL004";
					szEjbConName = "procY4EqpTrblRcvrWr";
				}else if( szYdGp.equals(YdConstant.YD_GP_PLATE2_GDS_YARD)){
					szRcvTcCode  = "Y8YDL004";
					szEjbConName = "procY4EqpTrblRcvrWr";					
				}else if( szYdGp.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD) || szYdGp.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD)){
					szRcvTcCode  = "Y5YDL004";
					szEjbConName = "procY5EqpTrblRcvrWr";
				}else if( szYdGp.equals(YdConstant.YD_GP_PORT_SLAB_YARD)){  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					szRcvTcCode  = "E7YDL004";
					szEjbConName = "procY1EqpTrblRcvrWr";
				}



				if(szYdGp.equals(YdConstant.YD_GP_C_SLAB_YARD) || szYdGp.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)
				|| szYdGp.equals(YdConstant.YD_GP_PLATE_GDS_YARD) || szYdGp.equals(YdConstant.YD_GP_PLATE2_GDS_YARD) || szYdGp.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD)
				|| szYdGp.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD)
				|| szYdGp.equals(YdConstant.YD_GP_PORT_SLAB_YARD)  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
				){
					//통합슬라브의 경우는 복구 리스케줄 정보강없음 L2와 전문전송을 하지 않는다.
					szLogMsg = "[JSP SESSION -  (복구리스케줄     "+ szEjbConName +")을 호출  시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);


					//해당 메소드는 void 형이라 리턴이 없습니다.

					recPara.setField("JMS_TC_CD", szRcvTcCode);
				   	ejbConn = new EJBConnector("default", this);
				 	ejbConn.trx("EqpTrackingSeEJB", szEjbConName, recPara);


					szLogMsg = "[JSP SESSION - (복구리스케줄     "+ szEjbConName +")을 호출  끝";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);


					//------------------------------------------------------------------------------------------------

				}else{


					//고장이력 테이블에 Log
					EqpTrackingSeEJBBean eqpTrackingSeEjb = new EqpTrackingSeEJBBean();
				 	eqpTrackingSeEjb.ProcEqpPause(recPara);



				 	//통합야드는 설비 UPDATE
					intRtnVal = ydEqpDao.updYdEqp(recPara, 0);

					if (intRtnVal > 0) {
						szRtnMsg[x] = YdConstant.RETN_CD_SUCCESS;
						szLogMsg = "[JSP Session]설비고장/정상설정 - 해당 설비[" + szYD_EQP_ID + "]의 야드설비상태[" + szYD_EQP_STAT + "]가 성공적으로 수정되었습니다!";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					}else if (intRtnVal == 0) {
						szRtnMsg[x] = YdConstant.RETN_CD_NOTEXIST;
						szLogMsg = "[JSP Session]설비고장/정상설정 - 해당 설비[" + szYD_EQP_ID + "]가 존재하지 않습니다!";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					}else if (intRtnVal < 0) {
						if (intRtnVal == -1) {
							szLogMsg = "routine error!!!, ErrorCode:" + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						} else {
							szLogMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						}
						szRtnMsg[x] = YdConstant.RETN_CD_FAILURE;
					} // end of if

					//------------------------------------------------------------------
			        // 고장/정상  Flex 실시간 처리
			        //------------------------------------------------------------------
			        JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
				 	recFlex.setField("YD_GP",  YdConstant.YD_GP_INTGR_YARD);
				 	recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
				 	ydUtils.putYdFlexCrnWrk("", recFlex);


				}
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}


		szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		return szRtnMsg;
	}	// end of updSlabYdCrnStsSetCrnStat





	/**
	 * 설비 ON_LINE, OFF_LINE 설정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String[]
	 * @throws DAOException
	 */
	public String[] updSlabYdCrnStsSetCrnMode(JDTORecord[] inDto) throws DAOException {
		int intRtnVal  = 0;

		String[] szRtnMsg = new String[inDto.length];

		String szLogMsg   = null;
		String szMethodName = "updSlabYdCrnStsSetCrnMode";
		String szYD_EQP_ID = null;
		String szYD_EQP_WRK_MODE = null;
		String szYdGp = null;
		String szEjbConName = "";
		String szRcvTcCode = "";
		String szOperationName = "설비 ON_LINE, OFF_LINE 설정";

		JDTORecord recPara  = JDTORecordFactory.getInstance().create();
		YdEqpDao ydEqpDao   = new YdEqpDao();
		EJBConnector ejbConn = null;



		try {
			//설비 ON_LINE, OFF_LINE 설정

			szLogMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			for(int x=0;x<inDto.length;x++){
				szYD_EQP_ID = yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), "");
				szYD_EQP_WRK_MODE = yddatautil.setDataDefault(inDto[x].getField("YD_EQP_WRK_MODE"), "");
				//입출입  상태
				recPara.setField("YD_EQP_ID", szYD_EQP_ID);
				recPara.setField("YD_EQP_WRK_MODE", szYD_EQP_WRK_MODE);

				intRtnVal = ydEqpDao.updYdEqp(recPara, 0);


				if (intRtnVal > 0) {
					szRtnMsg[x] = YdConstant.RETN_CD_SUCCESS;
					szLogMsg = "[JSP Session]설비 ON_LINE/OFF_LINE설정 - 해당 설비[" + szYD_EQP_ID + "]의 야드설비작업Mode[" + szYD_EQP_WRK_MODE + "]가 성공적으로 수정되었습니다!";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if (intRtnVal == 0) {
					szRtnMsg[x] = YdConstant.RETN_CD_NOTEXIST;
					szLogMsg = "[JSP Session]설비 ON_LINE/OFF_LINE설정 - 해당 설비[" + szYD_EQP_ID + "]가 존재하지 않습니다!";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				}else if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szLogMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					} else {
						szLogMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					}
					szRtnMsg[x] = YdConstant.RETN_CD_FAILURE;
				} // end of if

			}


			/* 해당 설비 UPDATE 후 설비 운전모드 전환 호출
			 *
			 *  C연주 슬라브야드 L2 : Y1YDL003, rcvY1EqpDrvMdTurnov  EqpTrackingFaEJB
			 *  항만 슬라브야드 L2 : E7YDL003, rcvY1EqpDrvMdTurnov  EqpTrackingFaEJB
			 *  항만  슬라브야드 L2 :  E7YDL003 rcvY1EqpDrvMdTurnov  EqpTrackingFaEJB
			 *  A후판 슬라브야드 L2 : Y3YDL003 rcvY3EqpDrvMdTurnov  EqpTrackingFaEJB
			 *  후판 제품야드 	 L2 : Y4YDL003 rcvY4EqpDrvMdTurnov  EqpTrackingFaEJB
			 *  C열연 코일야드   L2 : Y5YDL003 rcvY5EqpDrvMdTurnov  EqpTrackingFaEJB
			 *
			 */

			szYdGp = szYD_EQP_ID.substring(0,1);


			szLogMsg = "[JSP SESSION -  (야드구분  :  " + szYdGp +") ] 입니다";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

			if(szYdGp.equals(YdConstant.YD_GP_C_SLAB_YARD)){
				//직접 EJB호출
				szRcvTcCode  = "Y1YDL003";
				szEjbConName = "procY1EqpDrvMdTurnov";
			}else if( szYdGp.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)){
				szRcvTcCode  = "Y3YDL003";
				szEjbConName = "procY3EqpDrvMdTurnov";
			}else if( szYdGp.equals(YdConstant.YD_GP_PLATE_GDS_YARD)){
				szRcvTcCode  = "Y4YDL003";
				szEjbConName = "procY4EqpDrvMdTurnov";
			}else if( szYdGp.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD) || szYdGp.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD)){
				szRcvTcCode  = "Y5YDL003";
				szEjbConName = "procY5EqpDrvMdTurnov";
			}else if( szYdGp.equals(YdConstant.YD_GP_PORT_SLAB_YARD)) {  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
				szRcvTcCode  = "E7YDL003";
				szEjbConName = "procY1EqpDrvMdTurnov";
			}



			if(szYdGp.equals(YdConstant.YD_GP_C_SLAB_YARD) || szYdGp.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)
			|| szYdGp.equals(YdConstant.YD_GP_PLATE_GDS_YARD) || szYdGp.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD)
			|| szYdGp.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD)
			|| szYdGp.equals(YdConstant.YD_GP_PORT_SLAB_YARD) //수정 (2015.12.14) : 항만야드  By LeeJY
			){
				//통합슬라브의 경우는 복구 리스케줄 정보강없음 L2와 전문전송을 하지 않는다.
				szLogMsg = "[JSP SESSION -  (설비 운전모드 전환     "+ szEjbConName +")을 호출  시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);


				//해당 메소드는 void 형이라 리턴이 없습니다.

				recPara.setField("JMS_TC_CD", szRcvTcCode);
			   	ejbConn = new EJBConnector("default", this);
			 	ejbConn.trx("EqpTrackingSeEJB", szEjbConName, recPara);


				szLogMsg = "[JSP SESSION - (설비 운전모드 전환     "+ szEjbConName +")을 호출  끝";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}

			szLogMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		return szRtnMsg;
	}	// end of updSlabYdCrnStsSetCrnMode



	/**
	 * 슬라브야드 대차 상태 설정(고장/정상)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updslabYdTcarStsSetStat(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName = "updslabYdTcarStsSetStat";
		JDTORecord recPara  = JDTORecordFactory.getInstance().create();
		YdEqpDao ydEqpDao   = new YdEqpDao();

		String szOperationName = "대차 상태 설정(고장/정상)";


		try {


			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){

				recPara.setField("YD_EQP_ID", yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), ""));
				recPara.setField("YD_EQP_STAT", yddatautil.setDataDefault(inDto[x].getField("YD_EQP_STAT"), ""));
				intRtnVal = ydEqpDao.updYdEqp(recPara, 0);
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

				} // end of if

			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


	}	// end of updslabYdTcarStsSetStat


	/**
	 * 슬라브야드 대차 모드  설정 (ON_LINE, OFF_LINE)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updslabYdTcarStsSetMode(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";

		String szMethodName = "updslabYdTcarStsSetMode";
		JDTORecord recPara  = JDTORecordFactory.getInstance().create();
		YdEqpDao ydEqpDao   = new YdEqpDao();

		String szOperationName = "대차 모드  설정 (ON_LINE, OFF_LINE)";



		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){

				//입출입  상태
				recPara.setField("YD_EQP_ID",       yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), ""));
				recPara.setField("YD_EQP_WRK_MODE", yddatautil.setDataDefault(inDto[x].getField("YD_EQP_WRK_MODE"), ""));


				intRtnVal = ydEqpDao.updYdEqp(recPara, 0);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					//return outRecSet;
				} // end of if

			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

	}	// end of updslabYdTcarStsSetMode



	/**
	 * 슬라브야드 대차 이동실적 BACKUP 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public String updSlabYdCrnStsSetTcarMove(JDTORecord[] inDto) throws DAOException {
		Integer objRtn	= null;
		String szLogMsg          = null;
		String szRtnMsg = null;
		String szMethodName   = "updSlabYdCrnStsSetTcarMove";
		String szOperationName = " 대차 이동실적 BACKUP 처리";
		JDTORecord recPara    = null;
		//YdDelegate ydDelegate = new YdDelegate();
		EJBConnector ejbConn = null;

		try {
			szLogMsg = "JSP-SESSION [슬라브야드 대차 이동실적 BACKUP 처리] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			String ydEqpId = yddatautil.setDataDefault(inDto[0].getField("YD_EQP_ID"), "");
			String jmsTcCd = "";
			String methodNm = "";

			if ("A".equals(ydEqpId.substring(0, 1))) {
				jmsTcCd = "YDYDJ620";
				methodNm = "procC3TcarMvWr";
			//} else if ("M".equals(ydEqpId.substring(0, 1))) {  //항만슬라브야드 불필요 - 2015.12.30 LeeJY
			//	jmsTcCd = "YDYDJ620";
			//	methodNm = "procC3TcarMvWr";
			} else {
				jmsTcCd = "YDYDJ622";
				methodNm = "procY3TcarMvWr";
			}

			ejbConn = new EJBConnector("default", "TcarMvHdSeEJB", this);
			//저장위치 좌표설정화면 BED 수정
			//for(int x=0;x<inDto.length;x++){

				recPara = JDTORecordFactory.getInstance().create();

				// 이동실적 BACKUP 처리
				// TC CODE
				// YDYDJ620
				recPara.setField("JMS_TC_CD",  jmsTcCd);
				recPara.setField("YD_EQP_ID",  yddatautil.setDataDefault(inDto[0].getField("YD_EQP_ID"), ""));
				recPara.setField("YD_BAY_GP",  yddatautil.setDataDefault(inDto[0].getField("YD_CURR_BAY_GP"), ""));
				recPara.setField("YD_MOVE_GP", yddatautil.setDataDefault(inDto[0].getField("YD_MOVE_GP"), ""));

				szLogMsg = "[JSP Session]슬라브야드 대차 이동실적 메소드 콜 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				//슬라브야드 대차 이동실적 BACKUP 전송
				//ydDelegate.sendMsg(recPara);

				ydUtils.displayRecord(szOperationName, recPara);
				objRtn = (Integer)ejbConn.trx( methodNm , new Class[] { JDTORecord.class }, new Object[] { inDto[0] });
				szLogMsg = "[JSP Session]슬라브야드 대차 이동실적 메소드 콜 끝 : 리턴값 = " + objRtn;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			    if( objRtn.intValue() == YdConstant.RETN_INT_SUCCESS.intValue() ) {		//성공
			    	szRtnMsg = YdConstant.RETN_CD_SUCCESS;
			    	szLogMsg = "[JSP Session]슬라브야드 대차 이동실적 처리 성공";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			    }else{			//실패
			    	szRtnMsg = YdConstant.RETN_CD_FAILURE;
			    	szLogMsg = "[JSP Session]슬라브야드 대차 이동실적 처리 실패";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			    }
			//}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			//throw new DAOException(getClass().getName() + e.getMessage(),e);
			//Exception을 던질 지를 고려...
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			szLogMsg = "[JSP Session]슬라브야드 대차 이동실적 처리 시 에러발생";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
		} finally {

		}
		szLogMsg = "JSP-SESSION [슬라브야드 대차 이동실적 BACKUP 처리] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		return szRtnMsg;
	}	// end of updSlabYdCrnStsSetTcarMove

	/**
	 * 슬라브야드 대차 이동실적 BACKUP 처리 (출발지시)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updSlabYdCrnStsSetTcarOrd(JDTORecord[] inDto) throws DAOException {

		String szLogMsg="";
		String szMethodName="updSlabYdCrnStsSetTcarOrd";
		String szOperationName = "대차 이동실적 BACKUP 처리 (출발지시)";
		JDTORecord recPara       = null;

		YdDelegate ydDelegate    = new YdDelegate();
		//String 	   ydEqpWrkStat  = "";
		//int        intRtnVal     = 0;
		//String     wrkBook1      = "";
		//String     wrkBook2      = "";
		//JDTORecord recPara2      = null;

		//YdWrkbookDao ydWrkBookDao = new YdWrkbookDao();
		//JDTORecordSet tmpRecSet   = JDTORecordFactory.getInstance().createRecordSet("yd");
		
		//=====항만야드는 대차정보 관리안함 LeeJY

		try {

			szLogMsg = "JSP-SESSION [슬라브야드 대차 이동실적 BACKUP 처리 (출발지시)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			String ydEqpId = yddatautil.setDataDefault(inDto[0].getField("YD_EQP_ID"), "");
			String jmsTcCd = "";

			if ("A".equals(ydEqpId.substring(0, 1))) { 
				jmsTcCd = "YDYDJ520";
			} else {
				jmsTcCd = "YDYDJ522";
			}

			// 대차 이동실적 BACKUP 처리
			for(int x=0;x<inDto.length;x++){

				recPara  = JDTORecordFactory.getInstance().create();
				//recPara2 = JDTORecordFactory.getInstance().create();

				// 이동실적 BACKUP 처리
				// TC CODE
				// YDYDJ620
				//recPara.setField("JMS_TC_CD","YDC3L006");
				recPara.setField("JMS_TC_CD", jmsTcCd);

				recPara.setField("YD_EQP_ID",           yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), ""));
				recPara.setField("YD_EQP_WRK_STAT",     yddatautil.setDataDefault(inDto[x].getField("YD_EQP_WRK_STAT"), ""));
				recPara.setField("YD_TCAR_AIM_AREA_GP", yddatautil.setDataDefault(inDto[x].getField("YD_TCAR_AIM_AREA_GP"), ""));

				recPara.setField("YD_CARLD_STOP_LOC",   yddatautil.setDataDefault(inDto[x].getField("YD_CARLD_STOP_LOC"), ""));
				recPara.setField("YD_CARUD_STOP_LOC",   yddatautil.setDataDefault(inDto[x].getField("YD_CARUD_STOP_LOC"), ""));

//				recPara.setField("YD_GP",           yddatautil.setDataDefault(inDto[x].getField("YD_GP"), ""));
//				recPara.setField("YD_TCAR_SCH_ID",  yddatautil.setDataDefault(inDto[x].getField("YD_TCAR_SCH_ID"), ""));

				/*
				// 1)작업 스케줄 코드 조회  시작 ~~~~~~~~~~~~~~~~~
				recPara2.setField("YD_CARUD_WRK_BOOK_ID",      yddatautil.setDataDefault(inDto[x].getField("YD_CARUD_WRK_BOOK_ID"), ""));
				recPara2.setField("YD_CARLD_WRK_BOOK_ID",      yddatautil.setDataDefault(inDto[x].getField("YD_CARLD_WRK_BOOK_ID"), ""));
				wrkBook1 = recPara2.getFieldString("YD_CARUD_WRK_BOOK_ID"); //하차작업예약ID
				wrkBook2 = recPara2.getFieldString("YD_CARLD_WRK_BOOK_ID"); //상차작업예약ID
				// 상차작업예약ID와 하차작업예약ID가 모두 ""이 아닐 경우 하차작업예약ID로 작업스케줄코드 조회
				if(!"".equals(wrkBook1) && !"".equals(wrkBook2)) {
					recPara2.setField("YD_WBOOK_ID", wrkBook1);
				}
				// 상차작업예약ID가 ""이 아니고 하차작업예약ID가 ""일 경우 상차작업예약ID로 작업스케줄코드 조회
				else if("".equals(wrkBook1) && !"".equals(wrkBook2)) {
					recPara2.setField("YD_WBOOK_ID", wrkBook2);
				}
				// 작업예약ID에서 스케줄코드 조회
				intRtnVal = ydWrkBookDao.getYdWrkbook(recPara2, tmpRecSet, 0);

				if(intRtnVal > 0) {
					recPara.setField("YD_SCH_CD", tmpRecSet.getRecord(0).getFieldString("YD_SCH_CD"));
				}
				*/
				// 작업 스케줄 코드 조회 끝 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

				// 2) 출발지시 처리

				//Delegate
				//슬라브야드 대차 이동실적 BACKUP 전송

				ydUtils.displayRecord(szOperationName, recPara);

				ydDelegate.sendMsg(recPara);



			}

			szLogMsg = "JSP-SESSION [슬라브야드 대차 이동실적 BACKUP 처리 (출발지시)] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updSlabYdCrnStsSetTcarOrd

	/**
	 * 슬라브야드 차량정지위치 상태등록  조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto (차량정지위치:적치열구분)
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getslabYdCarStopLocStsReg(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";

		YdStkColDao ydStkcolDao = new YdStkColDao();

		String szMethodName="getslabYdCarStopLocStsReg";

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		try {
			//슬라브야드 차량정지위치 상태등록  조회
			szMsg = "JSP-SESSION [슬라브야드 차량정지위치 상태등록  조회 ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//설비ID
			recPara.setField("YD_STK_COL_GP",yddatautil.setDataDefault(inDto.getField("YD_STK_COL_GP"), ""));


			intRtnVal = ydStkcolDao.getYdStkcol(recPara, outRecSet, 0);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if



		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [슬라브야드 차량정지위치 상태등록  조회 ] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		return outRecSet;
	}	// end of getslabYdCarStopLocStsReg



	/**
	 * 슬라브야드 차량정지위치 상태 등록 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updslabYdCarStopLocStsReg(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";

		String szMethodName="updslabYdCarStopLocStsReg";

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdStkColDao ydStkcolDao = new YdStkColDao();

		try {

			szMsg = "JSP-SESSION [슬라브야드 차량정지위치 상태 수정] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){

				//입출입  상태
				recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"), ""));
				recPara.setField("YD_STK_COL_ACT_STAT", yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_ACT_STAT"), ""));


				intRtnVal = ydStkcolDao.updYdStkcol(recPara, 0);
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					//return outRecSet;
				} // end of if
			}

			szMsg = "JSP-SESSION [슬라브야드 차량정지위치 상태 수정] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updslabYdCarStopLocStsReg



	/**
	 *  슬라브야드 차량 상차정보 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getslabYdCarLiftInfo(JDTORecord inDto) throws DAOException {

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		YdCarFtmvMtlDao  ydCarftmvmtlDao = new YdCarFtmvMtlDao();


		String szMsg="";
		String szMethodName="getslabYdCarLiftInfo";


		int intRtnVal = 0;

		try {
			szMsg = "JSP-SESSION [슬라브야드 차량 상차정보 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("YD_GP",yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_EQP_GP",yddatautil.setDataDefault(inDto.getField("CAR_GP"), ""));
			recPara.setField("CAR_NO",yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));

			recPara.setField("PAGE_CNT1",inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",inDto.getField("ROWCOUNT"));

			intRtnVal = ydCarftmvmtlDao.getYdCarftmvmtl(recPara, outRecSet, 2);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		szMsg = "JSP-SESSION [슬라브야드 차량 상차정보 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getslabYdCarLiftInfo



	/**
	 *  슬라브야드 저장위치별 정보 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getslabYdStkLocInfoList(JDTORecord inDto) throws DAOException {

		JDTORecord recPara      = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		YdStkLyrDao ydStklyrDao = new YdStkLyrDao();

		String szMsg        = "";
		String szMethodName = "getslabYdStkLocInfoList";
		String szStockPos   = null;
		String szYdGp       = null;

		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION [ 슬라브야드 저장위치별 정보 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			szStockPos = yddatautil.setDataDefault(inDto.getField("STOCK_POS"), "RETURN");

			//길이체크  8자리 미만이면 RETRURN
			if ( szStockPos.length() < 8)
				return  outRecSet ;



			//적치열 구분과 적치베드 번호로 분리
			szYdGp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");



			recPara.setField("YD_STK_COL_GP", szStockPos.substring(0, 6));
			recPara.setField("YD_STK_BED_NO", szStockPos.substring(6, 8));
			recPara.setField("PAGE_CNT1",inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",inDto.getField("ROWCOUNT"));

			if (szYdGp.equals("D")){
				//A후판 슬라브야드인경우
				intRtnVal = ydStklyrDao.getYdStklyr(recPara, outRecSet, 69);

			}else{
				intRtnVal = ydStklyrDao.getYdStklyr(recPara, outRecSet, 2);
			}

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return outRecSet;
			} // end of if


			outRecSet.first();


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [ 슬라브야드 저장위치별 정보 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getslabYdStkLocInfoList



	/**
	 *  슬라브야드 스케줄 기동관리 (조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getslabYdSchStirMgt(JDTORecord inDto) throws DAOException {

		//Log Message 용
		String szMsg            = "";
		String szEdit           = "";
		String szMethodName     = "getslabYdSchStirMgt";

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecord    recEdit   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet retRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		YdSchRuleDao ydSchruleDao = new YdSchRuleDao();

		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION [ 슬라브야드 스케줄 기동관리 (조회)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			//적치열 구분과 적치베드 번호로 분리

			recPara.setField("YD_GP",     inDto.getField("YD_GP"));
			recPara.setField("YD_SCH_CD", inDto.getField("YD_SCH_CD"));
			recPara.setField("YD_BAY_GP", inDto.getField("YD_BAY_GP"));

			recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));

			intRtnVal = ydSchruleDao.getYdSchrule(recPara, outRecSet, 1);



			if (intRtnVal < 0) {
				szMsg = "ydSchruleDao  조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				return outRecSet;

			 } // end of if

			else if (intRtnVal == 0) {
				szMsg = "ydSchruleDao  조회된 데이터가 없습니다. !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				return outRecSet;

			 } // end of if

			outRecSet.first();
			do {

				//스케줄 코드를  설비구분을 분리하여 YD_EQP_GP 컬럼에 넣어준다.
				recEdit = outRecSet.getRecord();
				szEdit  = recEdit.getFieldString("YD_SCH_CD").trim().substring(4, 6);
				recEdit.setField("YD_EQP_GP", szEdit);
				retRecSet.addRecord(recEdit);

			}while(outRecSet.next());

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {


		}

		szMsg = "JSP-SESSION [슬라브야드 스케줄 기동관리 (조회)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return retRecSet;
	}//end of getslabYdSchStirMgt



	/**
	 * 슬라브야드 스케줄 기동관리 (수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updslabYdSchStirMgt(JDTORecord[] inDto) throws DAOException {
		int intRtnVal       = 0;
		String szMsg        = "";
		String szMethodName ="updslabYdSchStirMgt";

		JDTORecord    recPara     = JDTORecordFactory.getInstance().create();
		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

		try {

			szMsg = "JSP-SESSION [슬라브야드 스케줄 기동관리 (수정)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			// 슬라브야드 스케줄 기동관리 (수정)
			for(int x=0;x<inDto.length;x++){

				//스케줄 코드
				recPara.setField("YD_SCH_CD",            yddatautil.setDataDefault(inDto[x].getField("YD_SCH_CD"), ""));

				//금지/해제
				recPara.setField("YD_SCH_PROH_EXN",      yddatautil.setDataDefault(inDto[x].getField("YD_SCH_PROH_EXN"), "N"));


				intRtnVal = ydSchRuleDao.updYdSchrule(recPara, 0);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

				} // end of if
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [슬라브야드 스케줄 기동관리 (수정)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

	}	// end of updslabYdSchStirMgt



	/**
	 *  EVENT별 작업재료 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getslabYdEventWorkMatRef(JDTORecord inDto) throws DAOException {

		// Log Message
		String szMsg        = "";
		String szMethodName = "getslabYdEventWorkMatRef";

		String szYdGp = "";

		// ERROR CHECK
		int intRtnVal = 0;

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		try {

			szMsg = "JSP-SESSION [EVENT별 작업재료 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			szYdGp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");

			if(szYdGp.equals(YdConstant.YD_GP_C_SLAB_YARD)
					|| szYdGp.equals(YdConstant.YD_GP_PORT_SLAB_YARD)  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					|| szYdGp.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)
					|| szYdGp.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD)

			){
				recPara.setField("YD_GP",             inDto.getField("YD_GP"));
				recPara.setField("DATE_FROM",         inDto.getField("DATE_FROM"));
				recPara.setField("DATE_TO",           inDto.getField("DATE_TO"));
				recPara.setField("YD_WRK_DUTY",       inDto.getField("YD_WRK_DUTY"));
				recPara.setField("YD_AID_WRK_YN",     inDto.getField("YD_AID_WRK_YN"));

				recPara.setField("YD_GNT_GP",       inDto.getField("YD_GNT_GP"));
				recPara.setField("YD_EQP_ID",       inDto.getField("YD_EQP_ID"));
				recPara.setField("STL_PROG_CD",       inDto.getField("CURR_PROG_CD"));
				recPara.setField("PAGE_CNT",          inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT",           inDto.getField("ROWCOUNT"));

				// com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkHistDaoEventSearchTot_PIDEV
				
				intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 11);



			}
			else if (szYdGp.equals(YdConstant.YD_GP_INTGR_YARD)) {
				recPara.setField("YD_GP",             inDto.getField("YD_GP"));
				recPara.setField("DATE_FROM",         inDto.getField("DATE_FROM"));
				recPara.setField("DATE_TO",           inDto.getField("DATE_TO"));
				recPara.setField("YD_WRK_DUTY",       inDto.getField("YD_WRK_DUTY"));
				recPara.setField("YD_AID_WRK_YN",     inDto.getField("YD_AID_WRK_YN"));

				recPara.setField("YD_GNT_GP",       inDto.getField("YD_GNT_GP"));
				recPara.setField("YD_EQP_ID",       inDto.getField("YD_EQP_ID"));
				recPara.setField("STL_PROG_CD",       inDto.getField("CURR_PROG_CD"));
				recPara.setField("FROM_BAY_GP",       inDto.getField("FROM_BAY_GP"));
				recPara.setField("TO_BAY_GP",       inDto.getField("TO_BAY_GP"));
				recPara.setField("TO_ARR_GP",       inDto.getField("TO_ARR_GP"));
				recPara.setField("TO_CAR_GP",       inDto.getField("TO_CAR_GP"));
				recPara.setField("TRN_EQP_CD",       inDto.getField("TRN_EQP_CD2"));
				recPara.setField("PAGE_CNT",          inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT",           inDto.getField("ROWCOUNT"));

				// com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkHistDaoEventSearchTotBayGp2_PIDEV
				intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 302);

			}

			else if (szYdGp.equals(YdConstant.YD_GP_PLATE_GDS_YARD)) {
				recPara.setField("YD_GP",             inDto.getField("YD_GP"));
				recPara.setField("DATE_FROM",         inDto.getField("DATE_FROM"));
				recPara.setField("DATE_TO",           inDto.getField("DATE_TO"));
				recPara.setField("YD_WRK_DUTY",       inDto.getField("YD_WRK_DUTY"));
				recPara.setField("YD_AID_WRK_YN",     inDto.getField("YD_AID_WRK_YN"));

				recPara.setField("YD_GNT_GP",       inDto.getField("YD_GNT_GP"));
				recPara.setField("YD_EQP_ID",       inDto.getField("YD_EQP_ID"));
				recPara.setField("STL_PROG_CD",       inDto.getField("CURR_PROG_CD"));
				recPara.setField("PAGE_CNT",          inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT",           inDto.getField("ROWCOUNT"));

				// com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkHistDaoEventSearchPlateGds_PIDEV
				intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 12);

			}
			else{

				recPara.setField("DATE_FROM",         inDto.getField("DATE_FROM"));
				recPara.setField("DATE_TO",           inDto.getField("DATE_TO"));
				recPara.setField("YD_GP",             inDto.getField("YD_GP"));
				recPara.setField("YD_WRK_DUTY",       inDto.getField("YD_WRK_DUTY"));
				recPara.setField("YD_SCH_CD",         inDto.getField("YD_SCH_CD"));
				recPara.setField("PAGE_CNT",          inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT",           inDto.getField("ROWCOUNT"));

				// com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkHistDaoEventSearch
				intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 10);

			}

			if (intRtnVal < 0) {
				szMsg = "ydSchruleDao  조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;

			 } // end of if



		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [EVENT별 작업재료 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getslabYdEventWorkMatRef
	
	
	/**
	 *  옥외 Slab 야드 일일 장비 처리 현황 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getOuthouseSlabYdEqpHdStatList(JDTORecord inDto) throws DAOException {

		// Log Message
		String szMsg        = "";
		String szMethodName = "getOuthouseSlabYdEqpHdStatList";

		String szYdGp = "";

		// ERROR CHECK
		int intRtnVal = 0;

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		try {

			szMsg = "JSP-SESSION [옥외 Slab 야드 일일 장비 처리 현황 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, inDto.toString(), YdConstant.INFO);
			
			recPara.setField("DATE_FROM",         inDto.getFieldString("DATE_FROM"));

			// com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getOuthouseSlabYdEqpHdStatList
			intRtnVal = ydWrkHistDao.getOuthouseSlabYdEqpHdStatList(recPara, outRecSet, 20);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [옥외 Slab 야드 일일 장비 처리 현황 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getOuthouseSlabYdEqpHdStatList
	
	
	/**
	 *  옥외 Slab 야드 일일 장비 처리 현황 - 현재고 조회(비고)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getOuthouseSlabYdSlabWtSum(JDTORecord inDto) throws DAOException {

		// Log Message
		String szMsg        = "";
		String szMethodName = "getOuthouseSlabYdSlabWtSum";

		// ERROR CHECK
		int intRtnVal = 0;

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		try {

			szMsg = "JSP-SESSION [옥외 Slab 야드 일일 장비 처리 현황 - 현재고 조회(비고)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, inDto.toString(), YdConstant.INFO);
			
			//recPara.setField("DATE_FROM",         inDto.getFieldString("DATE_FROM"));

			// com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getOuthouseSlabYdSlabWtSum
			intRtnVal = ydWrkHistDao.getOuthouseSlabYdSlabWtSum(recPara, outRecSet, 20);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [옥외 Slab 야드 일일 장비 처리 현황 - 현재고 조회(비고)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getOuthouseSlabYdEqpHdStatList
	
	
	/**
	 *  옥외 Slab 야드 일일 근무자 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getOuthouseSlabYdWorkerList(JDTORecord inDto) throws DAOException {

		// Log Message
		String szMsg        = "";
		String szMethodName = "getOuthouseSlabYdWorkerList";

		// ERROR CHECK
		int intRtnVal = 0;

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		try {

			szMsg = "JSP-SESSION [옥외 Slab 야드 일일 근무자 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, inDto.toString(), YdConstant.INFO);
			
			recPara.setField("DATE_FROM",         inDto.getFieldString("DATE_FROM"));

			// com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getOuthouseSlabYdEqpHdStatList
			intRtnVal = ydWrkHistDao.getOuthouseSlabYdWorkerList(recPara, outRecSet, 21);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [옥외 Slab 야드 일일 근무자 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getOuthouseSlabYdWorkerList
	
	/**
	 * 옥외 Slab 야드 일일 근무자 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void setOuthouseSlabYdWorker(JDTORecord[] inDto) throws DAOException {

		int intRtnVal           = 0;
		String szMsg            = "";
		String szMethodName     = "setOuthouseSlabYdWorker";

		//DAO 셍성
		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();
		try {

			szMsg = "JSP-SESSION [옥외 Slab 야드 일일 근무자 등록] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			for(int x=0;x<inDto.length;x++){

				// 옥외 Slab 야드 일일 근무자 등록
				intRtnVal = ydWrkHistDao.updOuthouseSlabYdWorker(inDto[x]);
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

				} // end of if
			}

			szMsg = "JSP-SESSION [옥외 Slab 야드 일일 근무자 등록] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of setOuthouseSlabYdWorker
	
	/**
	 * 압연지시관리 (조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getslabYdRollCmdRef(JDTORecord inDto) throws DAOException {

		// ERROR CHECK
		int intRtnVal = 0;

		// Log Message
		String szMsg ="";
		String szMethodName="getslabYdRollCmdRef";



		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		YdStockDao ydStockDao = new YdStockDao();

		//조회되는 공장과 압연지시 공장구분이 다르므로 변환하여줌

		try {

			szMsg = "JSP-SESSION [압연지시관리 (조회)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			String szMillPlntGp = inDto.getFieldString("MILL_PLNT_GP");
			String szYD_BAY_GP = ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP");

			if (szMillPlntGp.equals(YdConstant.YD_GP_C_SLAB_YARD))   //C연주 슬라브 공장 선택시
				//szMillPlntGp.equals(YdConstant.YD_GP_PORT_SLAB_YARD))  //항만슬라브야드 불필요 - 2015.12.30 LeeJY
			{
				recPara.setField("PTOP_PLNT_GP",  "HC" ); //C열연
				recPara.setField("YD_STK_COL_GP",  	   szMillPlntGp+szYD_BAY_GP); //저장위치
				recPara.setField("PAGE_CNT1",          inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",           inDto.getField("PAGE_SIZE"));
				recPara.setField("PAGE_CNT2",          inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT2",           inDto.getField("PAGE_SIZE"));

				//if( szYD_BAY_GP.equals("") ) {
					intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 9);
				//}else{
				//	recPara.setField("YD_GP",          		szMillPlntGp);
				//	recPara.setField("YD_BAY_GP",           szYD_BAY_GP);
				//	intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 104);
				//}
			}
			else if ("DA".equals(szMillPlntGp) || "D".equals(szMillPlntGp)) //1후판 슬라브야드 선택시
			{
			    recPara.setField("YD_GP",              szMillPlntGp );
				recPara.setField("PAGE_CNT1",          inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",           inDto.getField("PAGE_SIZE"));
				recPara.setField("PAGE_CNT2",          inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT2",           inDto.getField("PAGE_SIZE"));

				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 71);
			}
			else if ("DB".equals(szMillPlntGp)) //2후판 슬라브야드 선택시
			{
			    recPara.setField("YD_GP",              szMillPlntGp );
				recPara.setField("PAGE_CNT1",          inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",           inDto.getField("PAGE_SIZE"));
				recPara.setField("PAGE_CNT2",          inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT2",           inDto.getField("PAGE_SIZE"));

				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 722);
			}
			else
			{
				recPara.setField("PTOP_PLNT_GP",       "B" ); //공정정보에 데이터가 있는 B열연 정보를 보여준다.
     			recPara.setField("PAGE_CNT1",          inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",           inDto.getField("PAGE_SIZE"));
				recPara.setField("PAGE_CNT2",          inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT2",           inDto.getField("PAGE_SIZE"));
				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 9);
			}

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return outRecSet;
			} // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [압연지시관리 (조회)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		return outRecSet;
	}//end of getslabYdRollCmdRef


	/**
	 *  설비사양설정 (조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getslabYdEqpSetSpec(JDTORecord inDto) throws DAOException {

		JDTORecord recPara        = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet   = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		YdCrnSpecDao ydCrnSpecDao = new YdCrnSpecDao();

		String szMsg              = "";
		String szMethodName       = "getslabYdEqpSetSpec";
		int intRtnVal             = 0;

		try {

			szMsg = "JSP-SESSION [설비사양설정 (조회)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			//야드 구분, 설비 ID, 페이지 설정
			recPara.setField("YD_GP",     yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_EQP_ID", yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"), "%"));

			//페이징설정
			recPara.setField("PAGE_CNT1",          inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",          inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",           inDto.getField("PAGE_SIZE"));
			recPara.setField("ROW_CNT2",           inDto.getField("PAGE_SIZE"));

			// 야드구분과 설비번호를 전달인자로 넘겨준다.
			intRtnVal = ydCrnSpecDao.getYdCrnspec(recPara, outRecSet, 1);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return outRecSet;
			} // end of if

			outRecSet.first();

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		szMsg = "JSP-SESSION [설비사양설정 (조회)] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		return outRecSet;
	}//end of getslabYdEqpSetSpec


	/**
	 * 설비사양설정 (수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updslabYdEqpSetSpec(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg = "";
		String szMethodName = "updslabYdEqpSetSpec";
		String szOperationName = "설비사양설정 (수정)";


		JDTORecord   recPara      = JDTORecordFactory.getInstance().create();
		YdCrnSpecDao ydCrnspecDao = new YdCrnSpecDao();
		YdEqpDao 	 ydEqpDao     = new YdEqpDao();

		try {
			szMsg = "JSP-SESSION [설비사양설정 (수정] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){


				//설비 ID 및 수정항목 세팅
				recPara.setField("YD_EQP_ID",            yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"),           ""));
				recPara.setField("YD_EQP_NAME",          yddatautil.setDataDefault(inDto[x].getField("YD_EQP_NAME"),         ""));
				recPara.setField("YD_CRN_GRAB_TP",       yddatautil.setDataDefault(inDto[x].getField("YD_CRN_GRAB_TP"),      ""));
				recPara.setField("YD_CRN_TONG_H",        yddatautil.setDataDefault(inDto[x].getField("YD_CRN_TONG_H"),       "0"));
				recPara.setField("YD_CRN_TONG_L",        yddatautil.setDataDefault(inDto[x].getField("YD_CRN_TONG_L"),       "0"));
				recPara.setField("YD_CRN_TONG_INTVL_W",  yddatautil.setDataDefault(inDto[x].getField("YD_CRN_TONG_INTVL_W"), "0"));
				recPara.setField("YD_CRN_TONG_END_T",    yddatautil.setDataDefault(inDto[x].getField("YD_CRN_TONG_END_T"),   "0"));
				recPara.setField("YD_CRN_TONG_W_TOL",    yddatautil.setDataDefault(inDto[x].getField("YD_CRN_TONG_W_TOL"),   "0"));
				recPara.setField("YD_WRK_ABLE_L",        yddatautil.setDataDefault(inDto[x].getField("YD_WRK_ABLE_L"),       "0"));
				recPara.setField("YD_WRK_ABLE_W",        yddatautil.setDataDefault(inDto[x].getField("YD_WRK_ABLE_W"),       "0"));
				recPara.setField("YD_WRK_ABLE_SH",       yddatautil.setDataDefault(inDto[x].getField("YD_WRK_ABLE_SH"),      "0"));
				recPara.setField("YD_WRK_ABLE_WT",       yddatautil.setDataDefault(inDto[x].getField("YD_WRK_ABLE_WT"),      "0"));

				//fix 20081230 수정자 부분추가
				recPara.setField("MODIFIER",             yddatautil.setDataDefault(inDto[x].getField("SZUSERID"),            ""));




			    ydUtils.displayRecord(szOperationName, recPara);

				//크레인 SPEC UPDATE
				intRtnVal = ydCrnspecDao.updYdCrnspec(recPara, 0);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

				} // end of if

				//야드 설비 UPDATE
				intRtnVal  = ydEqpDao.updYdEqp(recPara, 0);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
				} // end of if

			}

			szMsg = "JSP-SESSION [설비사양설정 (수정] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updslabYdEqpSetSpec


	/**
	 * 설비사양설정 (등록)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void insslabYdEqpSetSpec(JDTORecord[] inDto) throws DAOException {

		int intRtnVal = 0;

		String szMsg=  "";
		String szMethodName = "insslabYdEqpSetSpec";

		String szydEqpId = null;
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		YdCrnSpecDao ydCrnspecDao = new YdCrnSpecDao();
		YdEqpDao 	 ydEqpDao     = new YdEqpDao();


		try {

			szMsg = "JSP-SESSION [설비사양설정 (등록)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){

				// 설비 ID
				szydEqpId =  yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), "");

				// 설비 ID체크
				if (szydEqpId.length() < 6 )
				{
					szMsg = "설비ID가 올바르지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}

				recPara.setField("YD_EQP_ID",szydEqpId );

				// 설비ID로 야드/동/설비구분/설비번호 입력
				recPara.setField("YD_GP",     szydEqpId.substring(0, 1));
				recPara.setField("YD_BAY_GP", szydEqpId.substring(1, 2));
				recPara.setField("YD_EQP_GP", szydEqpId.substring(2, 4));
				recPara.setField("YD_EQP_NO", szydEqpId.substring(4, 6));

				//설비명 외 항목 세팅
				recPara.setField("YD_EQP_NAME",          yddatautil.setDataDefault(inDto[x].getField("YD_EQP_NAME"),         ""));
				recPara.setField("YD_CRN_GRAB_TP",       yddatautil.setDataDefault(inDto[x].getField("YD_CRN_GRAB_TP"),      ""));
				recPara.setField("YD_CRN_TONG_H",        yddatautil.setDataDefault(inDto[x].getField("YD_CRN_TONG_H"),       "0"));
				recPara.setField("YD_CRN_TONG_L",        yddatautil.setDataDefault(inDto[x].getField("YD_CRN_TONG_L"),       "0"));
				recPara.setField("YD_CRN_TONG_INTVL_W",  yddatautil.setDataDefault(inDto[x].getField("YD_CRN_TONG_INTVL_W"), "0"));
				recPara.setField("YD_CRN_TONG_END_T",    yddatautil.setDataDefault(inDto[x].getField("YD_CRN_TONG_END_T"),   "0"));
				recPara.setField("YD_CRN_TONG_W_TOL",    yddatautil.setDataDefault(inDto[x].getField("YD_CRN_TONG_W_TOL"),   "0"));
				recPara.setField("YD_WRK_ABLE_L",        yddatautil.setDataDefault(inDto[x].getField("YD_WRK_ABLE_L"),       "0"));
				recPara.setField("YD_WRK_ABLE_W",        yddatautil.setDataDefault(inDto[x].getField("YD_WRK_ABLE_W"),       "0"));
				recPara.setField("YD_WRK_ABLE_SH",       yddatautil.setDataDefault(inDto[x].getField("YD_WRK_ABLE_SH"),      "0"));
				recPara.setField("YD_WRK_ABLE_WT",       yddatautil.setDataDefault(inDto[x].getField("YD_WRK_ABLE_WT"),      "0"));


				//야드 설비 INSERT
				intRtnVal  = ydEqpDao.insYdEqp(recPara);

				//ERROR CHECK
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

				} // end of if


				//전달 인자 LOG출력 (DEBUG 용)
				szMsg = recPara.toString();


				//크레인 SPEC INSERT
				intRtnVal = ydCrnspecDao.insYdCrnspec(recPara);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

				} // end of if


			}

			szMsg = "JSP-SESSION [설비사양설정 (등록)] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of insslabYdEqpSetSpec

	/**
	 * 설비사양설정 (삭제)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void delslabYdEqpSetSpec(JDTORecord[] inDto) throws DAOException {

		int intRtnVal           = 0;
		String szMsg            = "";
		String szMethodName     = "delslabYdEqpSetSpec";
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();

		//DAO 셍성
		YdEqpDao 	 ydEqpDao     = new YdEqpDao();
		YdCrnSpecDao ydCrnspecDao = new YdCrnSpecDao();

		try {

			szMsg = "JSP-SESSION [설비사양설정 (삭제)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){

				//입출입  상태
				recPara.setField("YD_EQP_ID",  yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), ""));
				recPara.setField("DEL_YN",     "Y");

				//크레인 SPEC UPDATE
				intRtnVal = ydCrnspecDao.updYdCrnspec(recPara, 0);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

				} // end of if

				//야드 설비 UPDATE
				intRtnVal  = ydEqpDao.updYdEqp(recPara, 0);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
				} // end of if

			}


			szMsg = "JSP-SESSION [설비사양설정 (삭제)] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of delslabYdEqpSetSpec


	/**
	 * 수불구 변경등록 (조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto (저장위치 =  적치열구분(야드,동,스판)+베드번호)
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabYdBedBanCnc2(JDTORecord inDto) throws DAOException {

		int intRtnVal = 0;
		String szStkPos=null;
		String szYdStkColGp = null;
		String szYdStkBedNo = null;

		String szMsg="";
		String szMethodName="getSlabYdBedBanCnc2";

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		//DAO 생성
		YdStkBedDao ydStkbedDao = new YdStkBedDao();

		try {

			szMsg = "JSP-SESSION [수불구 변경등록 (조회)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//적치열 구분 * 필수
			szStkPos = yddatautil.setDataDefault(inDto.getField("STKPOS"), "");

			//추가 - 전체길이 체크
			if (szStkPos==null || "".equals(szStkPos) ||szStkPos.length()!=8)
			{
				return outRecSet ;
			}

			//추가 - 서브 길이 체크 (적치열구분 6자리, 적치베드 2자리)
			szYdStkColGp = szStkPos.substring(0, 6);
			szYdStkBedNo = szStkPos.substring(6, 8);


			//적치열구분 필수
			recPara.setField("YD_STK_COL_GP",szYdStkColGp);
			//적치 베드
			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);

			ydStkbedDao.getYdStkbed(recPara, outRecSet, 0);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return outRecSet;
			} // end of if

			szMsg = "JSP-SESSION [수불구 변경등록 (조회)] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		return outRecSet;
	}	// end of getSlabYdBedBanCnc2


	/**
	 * 수불구 변경등록 (수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updSlabYdBedBanCnc2(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="updSlabYdBedBanCnc2";
		JDTORecord recPara = JDTORecordFactory.getInstance().create();


		YdStkBedDao ydStkbedDao = new YdStkBedDao();
		try {

			szMsg = "JSP-SESSION [ 수불구 변경등록 (수정)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//수불구 변경등록 (수정)
			for(int x=0;x<inDto.length;x++){


				//적치열구분 필수
				recPara.setField("YD_STK_COL_GP",yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"), ""));

				//적치 베드
				recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto[x].getField("YD_STK_BED_NO"), ""));

				//야드적치BED용도 구분
				recPara.setField("YD_STK_BED_USG_GP", yddatautil.setDataDefault(inDto[x].getField("YD_STK_BED_USG_GP"), ""));

				ydStkbedDao.updYdStkbed(recPara,0);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

				} // end of if

			}

			szMsg = "JSP-SESSION [ 수불구 변경등록 (수정)] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updSlabYdBedBanCnc2

	/**
	 * 저장위치  조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */

	public JDTORecordSet getSlabYdStkPos(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		String szStkCol = null;
		String szStkBed = null;
		String temp = null;

		String szOperationName	= "저장위치  조회";

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		String szMsg        = "";
		String szMethodName = "getSlabYdStkPos";
		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION [" + szOperationName + " ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			temp = inDto.getFieldString("STKPOS");
			szStkCol = temp.substring(0, 6);
			szStkBed = temp.substring(6, 8);

			recPara.setField("YD_STK_COL_GP", szStkCol);
			recPara.setField("YD_STK_BED_NO", szStkBed);

			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 8);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if

			outRecSet.first();

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			//if( ydb != null ) ydb.close();
		}
		szMsg = "JSP-SESSION [" + szOperationName + " ] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}	// end of getSlabYdStkPos

	
	/**
	 * 저장위치수정 재료조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */

	public JDTORecordSet getSlabYdStkPosMtl(JDTORecord inDto) throws DAOException {
		JDTORecordSet	outRecSet	= JDTORecordFactory.getInstance().createRecordSet("retTmp");

		String szOperationName	= "저장위치수정 재료조회";

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		String szMsg        = "";
		String szMethodName = "getSlabYdStkPosMtl";
		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION [" + szOperationName + " ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			intRtnVal = ydStkLyrDao.getYdStklyr(inDto, outRecSet, 622);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if

			outRecSet.first();

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			//if( ydb != null ) ydb.close();
		}
		szMsg = "JSP-SESSION [" + szOperationName + " ] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}	// end of getSlabYdStkPos

	
	/**
	 * 저장위치수정  - 통합슬라브야드 [통합슬라브야드는 저장품 관련 L2전송부분이 존재하지 않는다]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String updSlabYdStkPosFix_Tot(JDTORecord [] inDto) throws DAOException {

		int intRtnVal = 0;
		String szMsg  ="";
		String szMethodName="updSlabYdStkPosFix_Tot";
		String szOperationName = "저장위치수정";
		String szStlNo = null;
		String szOldStlNo = null;
		String szStkColGp = null;
		String szStkBedNo = null;
		String szStkLyrNo = null;
		String szModifier = null;
		String szMtlItem = "";
		JDTORecordSet rsDelInfo = null;
		JDTORecordSet rsStockInfo = null;
		JDTORecordSet rsStockHistInfo = null;

		JDTORecord recPara = null;
		JDTORecord newPara = null;
		JDTORecord getRecord = null;
		JDTORecord setRecord= null;
		JDTORecord logRecord = null;

		JDTORecordSet slabCommRecSet = null;
		JDTORecordSet rsTemp  = null;
		JDTORecordSet getRecSet = null;

		YdStockDao ydStockDao = new YdStockDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		SlabYdCommDAO slabYdCommDao = new SlabYdCommDAO();
		
		String szSTL_NO = "";
		String szRtnMsg = "";
		String szLogMsg = "";
		String szYD_GP = "";
		String szPT_TB_COMM = "";
		String szYdStrLoc ="";
		String szYdStrLocHis1 = "";
    	String szYdGp = "";
    	String szMobileYn = "";
    	String szTempStlNo = "";
    	
    	JDTORecordSet rsStockPrevLoc = null;
    	
    	boolean bHistFlag = false;




		try{
			szMsg = "JSP-SESSION [산적 위치 수정  - 통합슬라브야드] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			/*********************************************************
			 * UPDATE 될 위치의 적치단이 없을 경우 먼저 체크해서
			 * 리턴해준다.(첫번째 온 정보(최상단 정보먼저 체크한다)
			 ********************************************************/


			// 처리 할 필요없는 경우
			if (inDto.length < 1 ){
				szRtnMsg = "적치단  정보가 없습니다";
				return szRtnMsg;
			}

			recPara = JDTORecordFactory.getInstance().create();
			recPara = inDto[0];


			rsTemp = JDTORecordFactory.getInstance().createRecordSet("YdSlabTemp");
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsTemp, 0);

			//단정보가 존재 하지않거나  단데이터 생성이 되지않는경우
			if(intRtnVal  < 1){
				szRtnMsg = "적치단  정보가 생성되지 않았습니다";
				return szRtnMsg;
			}
			//이전 저장위치 백업 -- 수정대상이 다른 수정대상의 위치를 덮어씌우면 권상위치 기록 안되는 문제 -- 2022.04.19 REQ202203392588
			rsStockPrevLoc = JDTORecordFactory.getInstance().createRecordSet("YdSlabTemp");
			
			for(int iLoop=0; iLoop<inDto.length; iLoop++){
				szStlNo = yddatautil.setDataDefault(inDto[iLoop].getField("STL_NO"), "").trim();
				String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO"				, szStlNo);
				recPara.setField("YD_STK_LYR_MTL_STAT"	, "");
				
				rsTemp = slabYdCommDao.select(recPara, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO", logId, szMethodName, "적치단 정보 select");
				
				if(rsTemp != null && rsTemp.size() >0) {
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_STK_COL_GP", rsTemp.getRecord(0).getFieldString("YD_STK_COL_GP"));
					recPara.setField("YD_STK_BED_NO", rsTemp.getRecord(0).getFieldString("YD_STK_BED_NO"));
					recPara.setField("YD_STK_LYR_NO", rsTemp.getRecord(0).getFieldString("YD_STK_LYR_NO"));
					recPara.setField("STL_NO"		, rsTemp.getRecord(0).getFieldString("STL_NO"));
					rsStockPrevLoc.addRecord(recPara);
				}
				else {
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_STK_COL_GP", "");
					recPara.setField("YD_STK_BED_NO", "");
					recPara.setField("YD_STK_LYR_NO", "");
					recPara.setField("STL_NO"		, szStlNo);
					rsStockPrevLoc.addRecord(recPara);
				}
			}
			
			
			for(int iLoop=0; iLoop<inDto.length; iLoop++){

				szStlNo = yddatautil.setDataDefault(inDto[iLoop].getField("STL_NO"), "").trim();
				szMobileYn = inDto[iLoop].getFieldString("MOBILE_YN");
				
				/*
				 *  현 저장위치정보에  이미 재료번호가 다른것이  들어 있을경우에
				 *  그 재료번호에 해당하는 공통테이블의 저장위치를 Clear 해주어야한다.
				 */


				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP", inDto[iLoop].getField("YD_STK_COL_GP"));
				recPara.setField("YD_STK_BED_NO", inDto[iLoop].getField("YD_STK_BED_NO"));
				recPara.setField("YD_STK_LYR_NO", inDto[iLoop].getField("YD_STK_LYR_NO"));

				rsDelInfo	=JDTORecordFactory.getInstance().createRecordSet("rsdelInfo");
				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsDelInfo, 0);


				if (intRtnVal < 1){
					szMsg = "";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.ERROR);



				}else{
					recPara = JDTORecordFactory.getInstance().create();
					rsDelInfo.first();
					recPara = rsDelInfo.getRecord();
					szTempStlNo = ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim();

					if("".equals(szTempStlNo)){
						szMsg = "[JSP-SESSION]기존 재료정보가 없는경우 입니다";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

						//기존정보가 없고 현재 재료정보도 없는경우

						if("".equals(szStlNo)){
							bHistFlag = false;
						}else{
							bHistFlag = true;
						}



					} else if (ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim().equals(szStlNo)){
						bHistFlag = false;
						szMsg = "[JSP-SESSION]기존 재료정보가 현 적치 정보와 같은 경우 입니다";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);



					} else{
						
						if("Y".equals(szMobileYn) && "".equals(szStlNo)) {
							//bHistFlag = false;
							szMsg = "[JSP-SESSION]적치 위치에 재료정보가 존재하며 수정하려는 재료 정보가 없는 경우 입니다(모바일)";
							ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);
							
							continue;
						}
						
						bHistFlag = true;
						szMsg = "[JSP-SESSION]기존 재료정보가 현 적치 정보와 다른 경우 입니다";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);


						szMsg = "[JSP-SESSION]공통 정보의 기존 적치 위치를 CLEAR 합니다";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);



						szOldStlNo = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

						getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

						szRtnMsg = YdCommonUtils.getPtCommStock(szOldStlNo, getRecSet);



						if(YdConstant.RETN_CD_FAILURE.equals(szRtnMsg)){

							szMsg = "[JSP-SESSION]공통 정보가 존재 하지 않습니다 - PASS 합니다";
							ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);


						} else if(YdConstant.RETN_CD_SUCCESS.equals(szRtnMsg)){
						   	getRecSet.absolute(1);
				        	getRecord = JDTORecordFactory.getInstance().create();
				        	getRecord = getRecSet.getRecord();

				        	szYdStrLoc = ydDaoUtils.paraRecChkNull(getRecord,"YD_STR_LOC");
				        	szYdStrLocHis1 = ydDaoUtils.paraRecChkNull(getRecord,"YD_STR_LOC_HIS1");
				        	szPT_TB_COMM = ydDaoUtils.paraRecChkNull(getRecord, "PT_TB_COMM");
				        	szStkColGp = recPara.getFieldString("YD_STK_COL_GP");


				        	setRecord = JDTORecordFactory.getInstance().create();
				        	setRecord.setField("YD_GP",         szStkColGp.substring(0,1));
				        	setRecord.setField("YD_BAY_GP",     "");
				        	setRecord.setField("YD_EQP_GP",     "");
				        	setRecord.setField("YD_STK_COL_NO", "");
				        	setRecord.setField("YD_STK_BED_NO", "");
				        	setRecord.setField("YD_STK_LYR_NO", "");
				        	setRecord.setField("FNL_REG_PGM",   "updSlabYdStkPosFixBoth");
				        	setRecord.setField("MODIFIER",  yddatautil.setDataDefault( inDto[iLoop].getField("YD_USER_ID"),"YD"));


				        	//전저장위치 = szYdStrLoc,	 전전저장위치 = szYdStrLocHis1
					        if("".equals(szYdStrLoc)){
					        	setRecord.setField("YD_STR_LOC_HIS1", 	"");
					        }else{ 
					        	setRecord.setField("YD_STR_LOC_HIS1", 	szYdStrLoc);
					        }

					        if("".equals(szYdStrLocHis1)){
					        	setRecord.setField("YD_STR_LOC_HIS2", 	"");
					        }else{
					        	setRecord.setField("YD_STR_LOC_HIS2", 	szYdStrLocHis1);
					        }


				        	//현재 저장위치 = 실적위치+실적단 주편일경우와 슬라브일경우


					        szMsg = "전 저장위치 : " + szYdStrLoc + " , 전전 저장위치 : " + szYdStrLocHis1 + ", 권하실적 단 정보 : " + szStkLyrNo;
				            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				        	if("B".equals(szPT_TB_COMM)){

				        		setRecord.setField("MSLAB_NO",   szOldStlNo);
				        		setRecord.setField("YD_STR_LOC", "");

				        		intRtnVal = this.updY1YdStock(setRecord, 2);
				        		if(intRtnVal<0) {
				                    szMsg = "주편공통Table 저장위치 등록 실패";
				                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				        		}

				        	}else if ("S".equals(szPT_TB_COMM)) {

				        		setRecord.setField("SLAB_NO",   szOldStlNo);
				        		setRecord.setField("YD_STR_LOC","");

				        		//슬라브 공통 업데이트
				        		intRtnVal = this.updY1YdStock(setRecord,  0);
				        		if(intRtnVal<0) {
				                    szMsg = "슬라브공통Table 저장위치 등록 실패";
				                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				        		}

				         	}

						}
					}
				}




				recPara = JDTORecordFactory.getInstance().create();
				ydUtils.putLog("SlabJspSeEJB", "updSlabYdStkPosFixBoth", " 적치단 정보 READ=============", 4);
				// 1. 적치 단 정보 UPDATE

				recPara.setField("STL_NO", szStlNo);
				//적치열구분
				recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"), ""));
				//적치베드
				recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_BED_NO"), ""));
				//적치단
				recPara.setField("YD_STK_LYR_NO", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_LYR_NO"), ""));

				szStkColGp = recPara.getFieldString("YD_STK_COL_GP");
				szStkBedNo = recPara.getFieldString("YD_STK_BED_NO");
				szStkLyrNo = recPara.getFieldString("YD_STK_LYR_NO");
				szModifier = inDto[iLoop].getFieldString("MODIFIER");

				recPara.setField("MODIFIER", szModifier);


				//적치 상태 [재료번호가 존재 :   "C" , 미존재 : "E"]
				if ( "".equals(szStlNo)){
					recPara.setField("YD_STK_LYR_MTL_STAT", "E");
				} else {
					recPara.setField("YD_STK_LYR_MTL_STAT", "C");
				}


			/*
			 * 이력정보 추가
			 */

			if(bHistFlag){
				JDTORecord      recOldPos      = 	JDTORecordFactory.getInstance().create();
				JDTORecord      recStockInfo      = 	JDTORecordFactory.getInstance().create();

				rsDelInfo		=	JDTORecordFactory.getInstance().createRecordSet("rsdelInfo");

				recOldPos.setField("STL_NO", szStlNo); 
				recOldPos.setField("YD_STK_LYR_MTL_STAT", "C");


				//	재료관련 정보로 이력정보를 추가적으로 넣을부분이 존재하면 재료정보를 조회한 후 이부분에 추가해줌


				rsStockInfo = JDTORecordFactory.getInstance().createRecordSet("rsStockInfo");
				rsStockHistInfo = JDTORecordFactory.getInstance().createRecordSet("rsStockHistInfo");

				szMsg = "=====통합야드 저장위치수정 시 외판출고 실적 삭제시작=====";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				intRtnVal = ydStockDao.getYdStock(recOldPos, rsStockHistInfo, 403);

//				 재료 정보 이력정보에 추가 (2010.06.14)
				if(intRtnVal > 0){
					//출고처리 실적 존재 시 삭제
					szMsg = "=====통합야드 저장위치수정 시 외판출고 실적 삭제시작===== 출고실적 존재함";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					JDTORecord      recDelHistPara      = 	JDTORecordFactory.getInstance().create();

					recDelHistPara.setField("STL_NO", szStlNo);
					int intRtn = ydCarFtmvMtlDao.updYdCarftmvmtl(recDelHistPara, 7);

					if(intRtn > 0){
						szMsg = "=====통합야드 저장위치수정 시 외판출고 실적 삭제완료===== 출고실적 존재함";
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
				}

				intRtnVal = ydStockDao.getYdStock(recOldPos, rsStockInfo, 0);


				// 재료 정보 이력정보에 추가 (2009.11.09)
				if(intRtnVal > 0){
					//STOCK 정보가 존재할 경우

					rsStockInfo.first();
					recStockInfo= rsStockInfo.getRecord();

				}
				/*
				intRtnVal  = ydStkLyrDao.getYdStklyr(recOldPos, rsDelInfo, 3);


				

				//작업이력 - 권상정보 관련 입력
				if(intRtnVal > 0 ){
					rsDelInfo.first();

					recOldPos      = 	JDTORecordFactory.getInstance().create();
					recOldPos = rsDelInfo.getRecord();

					recStockInfo.setField("YD_UP_WR_LOC", ydDaoUtils.paraRecChkNull(recOldPos, "YD_STK_COL_GP")+  ydDaoUtils.paraRecChkNull(recOldPos, "YD_STK_BED_NO"));
					recStockInfo.setField("YD_UP_WR_LAYER", ydDaoUtils.paraRecChkNull(recOldPos, "YD_STK_LYR_NO") );
					//권상완료일시 --현재시간 구하는 정보
					recStockInfo.setField("YD_UP_CMPL_DT",YdUtils.getCurDate("yyyyMMddHHmmss") );



				} else {
					recStockInfo.setField("YD_UP_WO_LOC", "");
					recStockInfo.setField("YD_UP_WO_LAYER", "");
				}*/
				
				//작업이력 - 권상정보 관련 입력 2022.04.19 NEW
				if(szStlNo.equals(rsStockPrevLoc.getRecord(iLoop).getFieldString("STL_NO")) && !"".equals(rsStockPrevLoc.getRecord(iLoop).getFieldString("YD_STK_COL_GP"))){
					recStockInfo.setField("YD_UP_WR_LOC", ydDaoUtils.paraRecChkNull(rsStockPrevLoc.getRecord(iLoop), "YD_STK_COL_GP")+  ydDaoUtils.paraRecChkNull(rsStockPrevLoc.getRecord(iLoop), "YD_STK_BED_NO"));
					recStockInfo.setField("YD_UP_WR_LAYER", ydDaoUtils.paraRecChkNull(rsStockPrevLoc.getRecord(iLoop), "YD_STK_LYR_NO") );
					//권상완료일시 --현재시간 구하는 정보
					recStockInfo.setField("YD_UP_CMPL_DT",YdUtils.getCurDate("yyyyMMddHHmmss") );

				} else {
					recStockInfo.setField("YD_UP_WO_LOC", "");
					recStockInfo.setField("YD_UP_WO_LAYER", "");
				}

				//공통슬라브야드이기때문에 S라고 사용해도 상관없다고 판단은 됨
				recStockInfo.setField("YD_GP", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"), " ").substring(0, 1));
				
				if("".equals(szStlNo)) {
					recStockInfo.setField("STL_NO", szTempStlNo);
				} else {
					recStockInfo.setField("STL_NO", szStlNo);
				}
				
				//////////////////////////////////////////////////////////////////////////////

				// 작업이력 - 권하정보 입력
				recStockInfo.setField("YD_DN_WR_LOC",    yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"),"") +  yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_BED_NO"),"") );
				recStockInfo.setField("YD_DN_WR_LAYER",  yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_LYR_NO"),""));
				//권하완료일시 --현재시간 구하는 정보
				recStockInfo.setField("YD_DN_CMPL_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));


				// 야드 스케줄 코드 - YD_SCH_CD
				recStockInfo.setField("YD_SCH_CD", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"),"  ").substring(0, 2) +"YD" + "01" +"MM");

				// 야드보조작업여부 - YD_AID_WRK_YN
				recStockInfo.setField("YD_AID_WRK_YN" , "N");

				// 야드크레인 호기
				recStockInfo.setField("YD_EQP_ID" , yddatautil.setDataDefault(inDto[iLoop].getField("YD_EQP_ID"),"SACRA1") ); 

				// 야드수불구분 - YD_GNT_GP
				recStockInfo.setField("YD_GNT_GP", YdConstant.YD_GNT_GP_MVSTK);

				// 야드스케줄 기동 구분 "B" 로 넣어준다. -2009.12.10
				recStockInfo.setField("YD_SCH_ST_GP", "B");



				//이력정보 남기기

				intRtnVal  = ydWrkHistDao.insYdWrkHistPosFix(recStockInfo);
				//ydUtils.putYdFlexCrnWrk("", recStockInfo);
				
				//슬라브 종합 이송 모니터링용, 통합야드 장비 현재동 수정(권하위치 동 기준)
				setRecord = JDTORecordFactory.getInstance().create();
	        	setRecord.setField("V_MODIFIER",         szModifier);
	        	
	        	String curBay = yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"),"");
	        	
	        	if("".equals(curBay)) curBay = "A";
	        	else{
	        		curBay = curBay.substring(1, 2);
	        	}
	        	setRecord.setField("V_YD_CURR_BAY_GP",         curBay);
	        	setRecord.setField("V_YD_EQP_ID",        yddatautil.setDataDefault(inDto[iLoop].getField("YD_EQP_ID"),"SACRA1"));
	        	
				intRtnVal  = slabYdCommDao.updSlabYd("EqpCurrBay",setRecord);
			}


				// 해당 재료번호가 적치중일 경우 해당 적치 정보 삭제 기능 추가




				JDTORecord      recDelPara      = 	JDTORecordFactory.getInstance().create();
				rsDelInfo		=	JDTORecordFactory.getInstance().createRecordSet("rsdelInfo");
				if("".equals(szStlNo.trim())){
					//해당작업 필요없음

				}else{
					recDelPara.setField("STL_NO", szStlNo);
					recDelPara.setField("YD_STK_LYR_MTL_STAT", "C");

					int nRtnVal  = ydStkLyrDao.getYdStklyr(recDelPara, rsDelInfo, 3);


					if(nRtnVal == 0 ){
						//해당 작업 필요없음

					}else if(nRtnVal > 0 ){

						//정보 존재시 해당 Map Clear
						rsDelInfo.first();

						do{
							recDelPara   = 	JDTORecordFactory.getInstance().create();
							recDelPara   =  rsDelInfo.getRecord();

							recDelPara.setField("STL_NO", "");
							recDelPara.setField("YD_STK_LYR_MTL_STAT", "E");

							ydStkLyrDao.updYdStklyr(recDelPara, 0);

						}while(rsDelInfo.next());

						logRecord = JDTORecordFactory.getInstance().create();
						logRecord.setField("YD_GP", szYdGp);
						logRecord.setField("YD_UP_WR_LOC", szStkColGp+szStkBedNo);
						ydUtils.displayRecord(szOperationName, logRecord);
						ydUtils.putYdFlexCrnWrk("", logRecord);
					}
				}

				
				
//				rsDelInfo		=	JDTORecordFactory.getInstance().createRecordSet("rsdelInfo");
//				int nRtnVal  = ydStkLyrDao.getYdStklyr(recPara, rsDelInfo, 0);
//				if(nRtnVal > 0 ){
//					rsDelInfo.first();
//					recDelPara   = 	JDTORecordFactory.getInstance().create();
//					recDelPara   =  rsDelInfo.getRecord();
//
//				 	String szSTL_NO_CHK = ydDaoUtils.paraRecChkNull(recDelPara,"STL_NO");
//				 	
//				 	if(!szSTL_NO_CHK.equals("")){
//				 		szRtnMsg = "적치단에 제료번호가 이미 존재 합니다.:"+szSTL_NO_CHK;
//				 		this.m_ctx.setRollbackOnly();
//						return szRtnMsg;
//				 	}
//				}
				
				szMsg = "적치단 정보 UPDATE===================================== ";
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);
				intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);


				logRecord = JDTORecordFactory.getInstance().create();
				logRecord.setField("YD_GP", szYdGp);
				logRecord.setField("YD_UP_WR_LOC", szStkColGp+szStkBedNo);
				ydUtils.displayRecord(szOperationName, logRecord);
				//ydUtils.putYdFlexCrnWrk("", logRecord);





				//**************************************************************
				// 재료 품목에 따른 공통테이블 정보 변경

				newPara = JDTORecordFactory.getInstance().create();
				newPara.setField("STL_NO", szStlNo);

				//ydUtils.putLog("SlabJspSeEJB", szMethodName , " 재료정보 :::== "+szStlNo, 4);


				szMsg =  " 재료정보 =======> [ "+szStlNo +"]";
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);


				//재료정보가 없으면 조회할 필요없이 Continue

				if(szStlNo.equals("")){
					continue;
				}


				slabCommRecSet =	JDTORecordFactory.getInstance().createRecordSet("YdSlabTemp");
				intRtnVal =ydStockDao.getYdStock(newPara, slabCommRecSet, 0);

				slabCommRecSet.first();

				if(slabCommRecSet.size() <1 ) {

					//공통 테이블 UPDATE 안됨
					continue;

				}


				newPara = slabCommRecSet.getRecord(0);
				szMtlItem =  yddatautil.setDataDefault(newPara.getField("YD_MTL_ITEM"),"");

	        	szSTL_NO = szStlNo;
	        	getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	        	szRtnMsg = YdCommonUtils.getPtCommStock(szSTL_NO, getRecSet);


	        	if( !YdConstant.RETN_CD_SUCCESS.equals(szRtnMsg)) {
	        		szLogMsg = "[저장위치 Setting - Y1setYdStrLoc]재료[" + szSTL_NO + "]를 공통테이블에서 조회 시 오류발생";
	                ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
	                //return -100;
	        	}

	        	getRecSet.absolute(1);
	        	getRecord      = JDTORecordFactory.getInstance().create();
	        	getRecord 	   = getRecSet.getRecord();


	        	szYdStrLoc 	   = ydDaoUtils.paraRecChkNull(getRecord,"YD_STR_LOC").trim();
	        	szYdStrLocHis1 = ydDaoUtils.paraRecChkNull(getRecord,"YD_STR_LOC_HIS1").trim();
	        	szPT_TB_COMM = ydDaoUtils.paraRecChkNull(getRecord, "PT_TB_COMM");

	        	//임춘수 2009.05.04 수정


	        	setRecord = JDTORecordFactory.getInstance().create();
	        	setRecord.setField("YD_GP",         szStkColGp.substring(0,1));
	        	setRecord.setField("YD_BAY_GP",     szStkColGp.substring(1,2));
	        	setRecord.setField("YD_EQP_GP",     szStkColGp.substring(2,4));
	        	setRecord.setField("YD_STK_COL_NO", szStkColGp.substring(4,6));
	        	setRecord.setField("YD_STK_BED_NO", szStkBedNo);
	        	setRecord.setField("YD_STK_LYR_NO", szStkLyrNo);
	        	setRecord.setField("FNL_REG_PGM",   "updSlabYdStkPosFixBoth");



	        	/*
	        	 * 권하위치의 야드구분이 다른 경우에는 입고일자와 입고시각을 업데이트 처리
	        	 * 주편공통, 슬라브공통 : 입고일자, 입고시각
	        	 * PLATE공통 : 입고일자
	        	 * 수정자 : 임춘수
	        	 * 일자 : 2009.07.14
	        	 */
	        	//공통테이블에 저장되어 있는 야드구분

	        	szYdGp =  szStkColGp.substring(0,1);

	        	szYD_GP	= ydDaoUtils.paraRecChkNull(getRecord,"YD_GP");
	        	if( !szYD_GP.equals(szYdGp) ) {
	        		String szCurDateTime = YdUtils.getCurDate("yyyyMMddHHmmss");
	        		String szRECEIPT_DATE = szCurDateTime.substring(0, 8);
	        		String RECEIPT_TIME = szCurDateTime.substring(8);
	        		if("B".equals(szMtlItem) || "S".equals(szMtlItem) ) {
	        			setRecord.setField("RECEIPT_DATE", 	szRECEIPT_DATE);				//입고일자
	        			setRecord.setField("RECEIPT_TIME", 	RECEIPT_TIME);					//입고시각
	        		}else if ( szMtlItem.equals("P") ) {
	        			setRecord.setField("RECEIPT_DATE", 	szRECEIPT_DATE);				//입고일자
	        		}
	        	}

	        	setRecord.setField("MODIFIER",  yddatautil.setDataDefault( inDto[iLoop].getField("MODIFIER"),"YD"));


	        	//전저장위치 = szYdStrLoc,	 전전저장위치 = szYdStrLocHis1
		        if("".equals(szYdStrLoc)){
		        	setRecord.setField("YD_STR_LOC_HIS1", 	"");
		        }else{
		        	setRecord.setField("YD_STR_LOC_HIS1", 	szYdStrLoc);
		        }

		        if("".equals(szYdStrLocHis1)){
		        	setRecord.setField("YD_STR_LOC_HIS2", 	"");
		        }else{
		        	setRecord.setField("YD_STR_LOC_HIS2", 	szYdStrLocHis1);
		        }


	        	//현재 저장위치 = 실적위치+실적단 주편일경우와 슬라브일경우


		        szMsg = "전 저장위치 : " + szYdStrLoc + " , 전전 저장위치 : " + szYdStrLocHis1 + ", 권하실적 단 정보 : " + szStkLyrNo;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        	if("B".equals(szPT_TB_COMM)){

	        		setRecord.setField("MSLAB_NO",   szStlNo);
	        		setRecord.setField("YD_STR_LOC", szStkColGp+ szStkBedNo+szStkLyrNo.substring(1,3));

	        		intRtnVal = this.updY1YdStock(setRecord, 2);
	        		if(intRtnVal<0) {
	                    szMsg = "주편공통Table 저장위치 등록 실패";
	                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        			//return intRtnVal ;
	        		}

	        	}else if (szPT_TB_COMM.equals("S")) {

	        		setRecord.setField("SLAB_NO",   szStlNo);
	        		setRecord.setField("YD_STR_LOC", szStkColGp+ szStkBedNo+szStkLyrNo.substring(1,3));

	        		//슬라브 공통 업데이트
	        		intRtnVal = this.updY1YdStock(setRecord,  0);
	        		if(intRtnVal<0) {
	                    szMsg = "슬라브공통Table 저장위치 등록 실패";
	                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        		//	return intRtnVal ;
	        		}



	         	}


	        	szRtnMsg =  yddatautil.sendYDPRJ003(szStlNo);

				if(YdConstant.RETN_CD_SUCCESS.equals(szRtnMsg)){
					szMsg = "[JSP-SESSION]  [ "  + szOperationName +  "] 이송완료실적처리 호출성공";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);
				}else {
					szMsg = "[JSP-SESSION]  [ "  + szOperationName +  "] 이송완료실적처리시 ERROR";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.ERROR);
				}


	        	// 3. 적치단 정보 높이 갱신

				//적치베드에 적치단 정보  Z 축 갱신
				newPara = JDTORecordFactory.getInstance().create();
				newPara.setField("YD_STK_COL_GP", szStkColGp);
				newPara.setField("YD_STK_BED_NO", szStkBedNo);


				intRtnVal = this.updStkBedZPosFix(newPara);

				if (intRtnVal < 0 )
				{
					//return;
				}

				//추후 에러처리 추가

				szMsg =" 적치베드에 적치단 정보  Z 축 갱신================================ ";
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);


				szMsg = "JSP-SESSION [산적 위치 수정  - 통합슬라브야드] 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			}

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{

		}



		return szRtnMsg;
	}	// end of updSlabYdStkPosFixBoth

	/**
	 * 저장위치수정  - [C연주 슬라브야드, A후판 슬라브야드 ]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String updSlabYdStkPosFixBoth(JDTORecord [] inDto) throws DAOException {

		int intRtnVal 			= 0;
		String szMsg  			= "";
		String szMethodName 	= "updSlabYdStkPosFixBoth";
		String szOperationName	= "저장위치수정";
		String szStlNo 			= null;
		String szOldStlNo 		= null;
		String szStkColGp 		= null;
		String szStkBedNo 		= null;
		String szStkLyrNo 		= null;
		String szModifier 		= null;

		JDTORecordSet 	rsDelInfo 	= null;
		JDTORecordSet 	rsStockInfo = null;

		JDTORecord      recPara      	= null;
		JDTORecord      newPara      	= null;
		JDTORecord 		recL2Para      	= null;
		JDTORecordSet   slabCommRecSet  = null;
		JDTORecordSet   rsTemp  		= null;

		YdStkLyrDao ydStkLyrDao 	= new YdStkLyrDao();
		YdStockDao ydStockDao 		= new YdStockDao();
		YdDelegate ydDelegate 		= new YdDelegate();
		YdWrkHistDao ydWrkHistDao 	= new YdWrkHistDao();
		YdSchRuleDao ydSchRuleDao 	= new YdSchRuleDao();

		String szRtnMsg 	= "";
		String szLogMsg 	= "";
		String szYD_GP 		= "";

		JDTORecordSet getRecSet = null;
		JDTORecord 	  getRecord = null;
		JDTORecord 	  setRecord = null;
		JDTORecord 	  logRecord = null;

		String szPT_TB_COMM 	= "";
		String szYdStrLoc 		= "";
		String szYdStrLocHis1 	= "";
    	String szYdGp 			= "";
    	String sPosition 		= "";
    	
    	boolean bHistFlag 		= false;

    	String rtnMsg = YdConstant.RETN_CD_SUCCESS;

		try{

			szMsg = "JSP-SESSION [ " + szOperationName + " ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			/*********************************************************
			 * UPDATE 될 위치의 적치단이 없을 경우 먼저 체크해서
			 * 리턴해준다.(첫번째 온 정보(최상단 정보먼저 체크한다)
			 ********************************************************/
			if (inDto.length < 1 ){
				szMsg = "JSP-SESSION [ "  + szOperationName +   "처리할 필요가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

				return YdConstant.RETN_CD_SUCCESS;
			}

			/*
			 * 적치단 위치정보를 가지고와서 적치단 정보가있는지 체크하는 부분
			 */
			recPara 	= JDTORecordFactory.getInstance().create();
			logRecord 	= JDTORecordFactory.getInstance().create();
			recPara 	= inDto[0];
			rsTemp 		= JDTORecordFactory.getInstance().createRecordSet("YdSlabTemp");
			intRtnVal 	= ydStkLyrDao.getYdStklyr(recPara, rsTemp, 0);

			//단정보가 존재 하지않거나  단데이터 생성이 되지않는경우
			if(intRtnVal  < 1){
				rtnMsg = "적치단 정보가 존재하지 않습니다.";
				return  rtnMsg;
			}

			for(int iLoop=0; iLoop<inDto.length; iLoop++){

				szStlNo = yddatautil.setDataDefault(inDto[iLoop].getField("STL_NO"), "");

				szMsg = "JSP-SESSION [ "  + szOperationName +   iLoop + "번째  ] 재료번호 [" + szStlNo  +"] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				/*
				 * recPara : 입력된 적치열 위치 정보조회 ParaMeter Setting
				 */

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(inDto[iLoop], "YD_STK_COL_GP") );
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(inDto[iLoop], "YD_STK_BED_NO") );
				recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(inDto[iLoop], "YD_STK_LYR_NO") );

				/*
				 * rsDelInfo : 적치단 정보를 가지고 온다.
				 */
				rsDelInfo = JDTORecordFactory.getInstance().createRecordSet("rsdelInfo");
				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsDelInfo, 0);

				/*
				 * 해당 적치열 정보로 적치단 정보를 조회한다.
				 */
				if (intRtnVal < 1){

					szMsg = "JSP-SESSION [ "  + szOperationName +  "] 해당 적치단" + inDto[iLoop].getField("YD_STK_LYR_NO") + "이 존재하지않습니다" ;
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.ERROR);

				}else{

					/*
					 * 해당 적치단 정보가 존재하는 경우. (1건존재함)
					 * 해당 정보를 recPara 정보에 넣어놓는다.
					 */
					recPara = JDTORecordFactory.getInstance().create();
					rsDelInfo.first();
					recPara = rsDelInfo.getRecord();

					/*
					 * 조회된 정보에서 저장이력에 남기는 상태에대하여 체크한다.
					 * 1. 기존재료 없고 입력된 재료가 없는경우 - 불필요
					 * 2. 기존재료번호가 현적치된 정보랑 같은경우 - 불필요
					 * 3. 신규위치 재료정보가 없는경우 -  불필요
					 * 4. 이외의 경우 - 필요
					 * */
					if(ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim().equals("") && szStlNo.equals("") ){

						//기존정보가 없고 현재 재료정보도 없는경우
						bHistFlag = false;

					} else if (ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim().equals(szStlNo)){

						//기존재료번호가 현적치된 정보랑 같은경우
						bHistFlag = false;

					} else if(szStlNo.trim().equals("")){

						//신규 위치 재료정보가 없는경우
						bHistFlag = false;

					} else{

						// 신규위치 정보가 존재하는 경우.
						bHistFlag = true;

						// 기존 재료번호에 대한 공통정보 업데이트 하지 않는다.(최종락 이사님)

						szMsg = "[JSP-SESSION] [ "  + szOperationName +  "] 기존 재료정보가 현 적치 정보와 다른 경우 입니다";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

						/*
						 * 전 재료정보의 공통정보의 변경하기전  재료번호 존재 여부 CHECK
						 */
						szOldStlNo 	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");              //변경하기전 재료번호
						getRecSet 	= JDTORecordFactory.getInstance().createRecordSet("temp");	// 초기화
						szRtnMsg 	= YdCommonUtils.getPtCommStock(szOldStlNo, getRecSet);

						if(szRtnMsg.equals(YdConstant.RETN_CD_FAILURE)){

							szMsg = "[JSP-SESSION] [ "  + szOperationName +  "] 기존 재료정보["+ szOldStlNo +"] 가 공통에 존재하지않습니다 " ;
							ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

						} else if(szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){

				        	/*
							 * 신규위치에 기존 재료번호가 존재할경우
							 * 전 재료번호에대한 제원정보를 요구한다.
							 */
							getRecSet.absolute(1);                                //포인트 이동
				        	getRecord = JDTORecordFactory.getInstance().create(); //초기화
				        	getRecord = getRecSet.getRecord();                    //DB재료정보

				        	szMsg = "[JSP-SESSION] [ "  + szOperationName +  "]  위치정보가 지워진 저장품 제원정보를 내려보내준다.";
							ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

				        	//JMS Call
				        	recL2Para = JDTORecordFactory.getInstance().create();
				        	szStkColGp = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");

				        	if(szStkColGp.trim().equals("")){
				        		szMsg = "[JSP-SESSION] [ "  + szOperationName +  "]  기존 적치열정보가 올바르지 않아 L2 로 전송할지 않는다.";
								ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);
				        	}else{

				        		/*
								 * 적치열 정보로 야드 구분 판단.
								 */
				        		szYdGp = szStkColGp.substring(0,1);

					        	String szMsgId = "";

					        	if(szYdGp.equals(YdConstant.YD_GP_C_SLAB_YARD)){
					        		szMsgId = "YDY1L002";
					        	} else if(szYdGp.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)){
					        		szMsgId = "YDY3L002";
					        	} else if(szYdGp.equals(YdConstant.YD_GP_PORT_SLAB_YARD)){  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					        		szMsgId = "YDE7L002";
					        	}

					        	/*
								 * 야드별  제원정보를 요구
								 */
					        	if (!szMsgId.equals("")){

					        		szMsg = "[JSP-SESSION] [ "  + szOperationName +  "]  L2 [" + szMsgId+ "] 전송합니다";
									ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

					        		recL2Para.setField("MSG_ID"         , szMsgId);
						        	recL2Para.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
						        	recL2Para.setField("STL_NO"         , szOldStlNo);
						        	recL2Para.setField("YD_STK_COL_GP"  , "");
						        	recL2Para.setField("YD_STK_BED_NO"  , "");

						        	ydDelegate.sendMsg(recL2Para);
					        	}
				        	}
						}
					}
				}


				/*
				 *  작업 이력정보에 이력을 남긴다.
				 */

				if(bHistFlag){

					szMsg = "[JSP-SESSION] [ "  + szOperationName +  "]   이력정보추가 시작 ";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

					//초기화
					JDTORecord recOldPos 		= JDTORecordFactory.getInstance().create();
					JDTORecord recStockInfo 	= JDTORecordFactory.getInstance().create();
					JDTORecord recWrkHistPara 	= JDTORecordFactory.getInstance().create();
					JDTORecord recWrkHistInfo 	= JDTORecordFactory.getInstance().create();
					JDTORecordSet outRecSet 	= null;

					rsDelInfo		=	JDTORecordFactory.getInstance().createRecordSet("rsdelInfo");
					String lszYdSchCd = "";

					recOldPos.setField("STL_NO", szStlNo);
					recOldPos.setField("YD_STK_LYR_MTL_STAT", "C");

					/*
					 *  저장품(STOCK) 에 데이터가 있는지 체크한다.
					 */
					rsStockInfo = JDTORecordFactory.getInstance().createRecordSet("rsStockInfo");
					intRtnVal 	= ydStockDao.getYdStock(recOldPos, rsStockInfo, 0);

					// 재료 정보 이력정보에 추가 (2009.11.09)
					if(intRtnVal > 0){
						//STOCK 정보가 존재할 경우
						szMsg = "[JSP-SESSION] [ "  + szOperationName +  "]  저장품 정보가 존재하므로 이력정보를 추가 가능합니다 ";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

						rsStockInfo.first();
						recStockInfo= rsStockInfo.getRecord();

					} else{

						szMsg = "[JSP-SESSION] [ "  + szOperationName +  "]  저장품 정보가 없어 이력정보를 생성하지 않습니다 ";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.ERROR);

						break;
					}

					/*
					 *  적치중인 단 정보를 조회한다.
					 */
					intRtnVal  = ydStkLyrDao.getYdStklyr(recOldPos, rsDelInfo, 3);

					//작업이력 - 권상정보 관련 입력
					if(intRtnVal > 0 ){

						szMsg = "[JSP-SESSION] [ "  + szOperationName +  "]  기존 저장위치 정보가 있을경우는 FROM(권상)정보를 추가합니다 ";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

						rsDelInfo.first();

						recOldPos =	JDTORecordFactory.getInstance().create();
						recOldPos = rsDelInfo.getRecord();

						recStockInfo.setField("YD_UP_WR_LOC", 	ydDaoUtils.paraRecChkNull(recOldPos, "YD_STK_COL_GP")+
																ydDaoUtils.paraRecChkNull(recOldPos, "YD_STK_BED_NO"));
						recStockInfo.setField("YD_UP_WR_LAYER", ydDaoUtils.paraRecChkNull(recOldPos, "YD_STK_LYR_NO") );
						//권상완료일시 --현재시간 구하는 정보
						recStockInfo.setField("YD_UP_CMPL_DT",YdUtils.getCurDate("yyyyMMddHHmmss") );

					} else {
						szMsg = "[JSP-SESSION] [ "  + szOperationName +  "]  기존 저장위치 없으므로 FROM(권상)정보는 넣을수 없습니다. ";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

						recStockInfo.setField("YD_UP_WO_LOC", "");
						recStockInfo.setField("YD_UP_WO_LAYER", "");
					}

					//야드구분은 적치열 정보에서 가지고 온다.
					recStockInfo.setField("YD_GP", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"), " ").substring(0, 1));
					recStockInfo.setField("STL_NO", szStlNo);

					// 작업이력 - 권하정보 입력
					recStockInfo.setField("YD_DN_WR_LOC",    yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"),"") +
															 yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_BED_NO"),"") );
					recStockInfo.setField("YD_DN_WR_LAYER",  yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_LYR_NO"),""));
					//권하완료일시 --현재시간 구하는 정보
					recStockInfo.setField("YD_DN_CMPL_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));

					//이적 작업 스케줄 코드를 얻는다.
					// STOCK 정보의 입고일자가 없을경우 입고스케줄로 편성
					lszYdSchCd = ydUtils.getMakeSchCdMM(  yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"),"      ").substring(0, 1) ,
						yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"),"      ").substring(1, 2),
						yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"),"      ").substring(2, 4) );

					szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 해당 이적작업 스케줄 구하기.["+ lszYdSchCd +"]" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

					recStockInfo.setField("YD_SCH_CD", lszYdSchCd ); // 야드 스케줄 코드 - YD_SCH_CD
					recStockInfo.setField("YD_AID_WRK_YN" , "N");    // 야드보조작업여부 - YD_AID_WRK_YN

					outRecSet = JDTORecordFactory.getInstance().createRecordSet("outRecSet");
					//해당 스케줄 정보로 주작업 크레인 정보를 가지고 온다.

					szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 해당 스케줄 코드에 대한  기준정보 조회 ["+ lszYdSchCd +"]" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

					recWrkHistPara.setField("YD_SCH_CD", lszYdSchCd);
					intRtnVal = ydSchRuleDao.getYdSchrule(recWrkHistPara, outRecSet, 0);

					if(intRtnVal > 0){
						outRecSet.first();
						recWrkHistInfo = outRecSet.getRecord();

					}else {
						szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 해당 스케줄에 대한 기준정보가 존재 하지 않습니다.["+ lszYdSchCd +"]" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

					//스케줄 기준의 작업 크레인 정보를 넣어준다.
					recStockInfo.setField("YD_EQP_ID" , ydDaoUtils.paraRecChkNull(recWrkHistInfo, "YD_WRK_CRN"));

					// 야드수불구분 - YD_GNT_GP
					// 입고 : L , 이적 : M , 출고 : U
					if(lszYdSchCd.length() > 0){
						recStockInfo.setField("YD_GNT_GP", lszYdSchCd.substring(6, 7) );
					}else{
						recStockInfo.setField("YD_GNT_GP", "");
					}

					recStockInfo.setField("YD_SCH_ST_GP", "B");		// 야드스케줄 기동 구분 "B" 로 넣어준다.
					
					
					/*******************************추가*********************************/
					szModifier = yddatautil.setDataDefault(inDto[iLoop].getField("YD_USER_ID"), "");
					recStockInfo.setField("MODIFIER", szModifier); //수정자 정보 남기기
					
					//이력정보 남기기
					intRtnVal = ydWrkHistDao.insYdWrkHistPosFix(recStockInfo);
					
					

					if(intRtnVal > 0){
						szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 이력정보를 로깅하였습니다." + intRtnVal ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					}else{
						szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 이력정보를 로깅 실패 하였습니다" + intRtnVal ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
				}

				szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 적치단 정보 UPDATE ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


				// 1. UPDATE할 재료정보 RECORD 생성
				recPara.setField("STL_NO", szStlNo); // 재료번호
				recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"), "")); //적치열구분
				recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_BED_NO"), "")); //적치베드
				recPara.setField("YD_STK_LYR_NO", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_LYR_NO"), "")); //적치단

				szStkColGp = recPara.getFieldString("YD_STK_COL_GP");
				szStkBedNo = recPara.getFieldString("YD_STK_BED_NO");
				szStkLyrNo = recPara.getFieldString("YD_STK_LYR_NO");

				//szModifier = yddatautil.setDataDefault(inDto[iLoop].getField("YD_USER_ID"), "");
				recPara.setField("MODIFIER", szModifier);

				//적치 상태 [재료번호가 존재 :   "C" , 미존재 : "E"]
				if ( "".equals(szStlNo)){
					recPara.setField("YD_STK_LYR_MTL_STAT", "E");
				} else {
					recPara.setField("YD_STK_LYR_MTL_STAT", "C");
				}

				intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);

				if(intRtnVal > 0 ){

					// FLEX 화면으로 전송
		        	logRecord.setField("YD_GP", szYdGp);
					logRecord.setField("YD_UP_WR_LOC", szStkColGp+szStkBedNo);
					//ydUtils.putYdFlexCrnWrk("", logRecord);

					szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 적치단 정보 UPDATE 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

				}else if(intRtnVal == 0 ){
					szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 적치단 정보 UPDATE 할 정보가 없습니다";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

				}else {
					szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 적치단 정보 UPDATE 때 문제가 발생하였습니다";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}

				//재료가 적치된 저장품 데이터에서 재료 품목을 조회한다.

				if(szStlNo.equals("")){

					//재료정보가 없는곳으로 PASS 합니다.
					szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 저장품에서 재료정보가 없는곳으로 PASS 합니다.";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

				}else{

					newPara = JDTORecordFactory.getInstance().create();
					newPara.setField("STL_NO", szStlNo);

					szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 저장품에서 재료정보 [" +szStlNo +"] 를 품목을 얻기위해 조회한다.";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

					slabCommRecSet = JDTORecordFactory.getInstance().createRecordSet("YdSlabTemp");
					intRtnVal = ydStockDao.getYdStock(newPara, slabCommRecSet, 0);

					if(slabCommRecSet.size() <1 ) {
						szLogMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 저장품에서 재료정보 [" +szStlNo +"] 를 품목을 얻기위해 조회된 결과가 없을경우 .";
		                ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
		                throw new DAOException(YdConstant.RETN_CD_NOTEXIST);
					}

					slabCommRecSet.first();
					newPara 	= slabCommRecSet.getRecord(0);

					szMsg = "[JSP-SESSION]공통 정보에 해당재료 [" + szStlNo +  " ] 가 있는지 검색 한다";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

					getRecSet 	= JDTORecordFactory.getInstance().createRecordSet("temp");
					szRtnMsg 	= YdCommonUtils.getPtCommStock(szStlNo, getRecSet);


		        	if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) {
		        		szLogMsg = "[저장위치 Setting - Y1setYdStrLoc]재료[" + szStlNo + "]를 공통테이블에서 조회 시 오류발생";
		                ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
		                throw new DAOException(YdConstant.RETN_CD_NOTEXIST);
		            }

		        	getRecSet.absolute(1);
		        	getRecord = JDTORecordFactory.getInstance().create();
		        	getRecord = getRecSet.getRecord();

		        	szYdStrLoc 	   	= ydDaoUtils.paraRecChkNull(getRecord,"YD_STR_LOC").trim();
		        	szYdStrLocHis1 	= ydDaoUtils.paraRecChkNull(getRecord,"YD_STR_LOC_HIS1").trim();
		        	szPT_TB_COMM 	= ydDaoUtils.paraRecChkNull(getRecord,"PT_TB_COMM");

		        	setRecord = JDTORecordFactory.getInstance().create();
		        	setRecord.setField("YD_GP",         szStkColGp.substring(0,1));
		        	setRecord.setField("YD_BAY_GP",     szStkColGp.substring(1,2));
		        	setRecord.setField("YD_EQP_GP",     szStkColGp.substring(2,4));
		        	setRecord.setField("YD_STK_COL_NO", szStkColGp.substring(4,6));
		        	setRecord.setField("YD_STK_BED_NO", szStkBedNo);
		        	setRecord.setField("YD_STK_LYR_NO", szStkLyrNo);
		        	setRecord.setField("FNL_REG_PGM",   "updSlabYdStkPosFixBoth");

		        	/*
		        	 * 권하위치의 야드구분이 다른 경우에는 입고일자와 입고시각을 업데이트 처리
		        	 * 주편공통, 슬라브공통 : 입고일자, 입고시각
		        	 */
		        	//공통테이블에 저장되어 있는 야드구분
		        	szYdGp 	=  szStkColGp.substring(0,1);
		        	szYD_GP	= ydDaoUtils.paraRecChkNull(getRecord,"YD_GP");

		        	if( !szYD_GP.equals(szYdGp) ) {

		        		String szCurDateTime 	= YdUtils.getCurDate("yyyyMMddHHmmss");

		        		setRecord.setField("RECEIPT_DATE", 	szCurDateTime.substring(0, 8));	//입고일자
		        		setRecord.setField("RECEIPT_TIME", 	szCurDateTime.substring(8));	//입고시각
		        	}
		        	setRecord.setField("MODIFIER",  yddatautil.setDataDefault( inDto[iLoop].getField("YD_USER_ID"),"YD"));

		        	//전저장위치 = szYdStrLoc,	 전전저장위치 = szYdStrLocHis1
			        if(szYdStrLoc.equals("")){
			        	setRecord.setField("YD_STR_LOC_HIS1", 	"");
			        }else{
			        	setRecord.setField("YD_STR_LOC_HIS1", 	szYdStrLoc);
			        }

			        if(szYdStrLocHis1.equals("")){
			        	setRecord.setField("YD_STR_LOC_HIS2", 	"");
			        }else{
			        	setRecord.setField("YD_STR_LOC_HIS2", 	szYdStrLocHis1);
			        }

		        	//현재 저장위치 = 실적위치+실적단 주편일경우와 슬라브일경우
		        	if(szPT_TB_COMM.equals("B")){

		        		setRecord.setField("MSLAB_NO",   szStlNo);
		        		setRecord.setField("YD_STR_LOC", ydUtils.ParsingStkColGpBedLyr(szStkColGp, szStkBedNo, szStkLyrNo));

		        		szMsg = "[JSP-SESSION]  [ "  + szOperationName +  "] 주편 공통테이블을 UPDATE 합니다";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

		        		intRtnVal = this.updY1YdStock(setRecord, 2);
		        		if(intRtnVal < 0) {
		        			szMsg = "[JSP-SESSION]  [ "  + szOperationName +  "] 주편 공통테이블을 UPDATE 실패 하였습니다";
							ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.ERROR);
		        		}

		        	}else if (szPT_TB_COMM.equals("S")) {

		        		szMsg = "[JSP-SESSION]  [ "  + szOperationName +  "] 슬라브 공통테이블을 UPDATE 합니다";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

		        		setRecord.setField("SLAB_NO",   szStlNo);
		        		setRecord.setField("YD_STR_LOC", ydUtils.ParsingStkColGpBedLyr(szStkColGp, szStkBedNo, szStkLyrNo));

		        		//슬라브 공통 업데이트
		        		intRtnVal = this.updY1YdStock(setRecord,  0);
		        		if(intRtnVal<0) {
		        			szMsg = "[JSP-SESSION]  [ "  + szOperationName +  "] 슬라브 공통테이블을 UPDATE 실패 하였습니다";
							ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.ERROR);
		        		}
		        	}
				}
				/*
				 * L2 정보송신
				 */
				// 저장위치정보 송신
	        	szYdGp 		= szStkColGp.substring(0,1);
	        	recL2Para 	= JDTORecordFactory.getInstance().create();

	        	if(szYdGp.equals(YdConstant.YD_GP_C_SLAB_YARD)){
		        	recL2Para.setField("MSG_ID", "YDY1L001");
	        	} else if(szYdGp.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)){
		        	recL2Para.setField("MSG_ID", "YDY3L001");
	        	} else if(szYdGp.equals(YdConstant.YD_GP_PORT_SLAB_YARD)){  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
		        	recL2Para.setField("MSG_ID", "YDE7L001");
	        	}

	        	recL2Para.setField("YD_INFO_SYNC_CD", "4");
	        	recL2Para.setField("YD_STK_COL_GP"  , szStkColGp);
	        	recL2Para.setField("YD_STK_BED_NO"  , szStkBedNo);  
	        	ydDelegate.sendMsg(recL2Para);

	        	// FLEX 화면으로 전송
	        	logRecord.setField("YD_GP", szYdGp);
				logRecord.setField("YD_UP_WR_LOC", szStkColGp+szStkBedNo);
				//ydUtils.putYdFlexCrnWrk("", logRecord);

	        	// 저장품제원정보 송신
	        	recL2Para = JDTORecordFactory.getInstance().create();
	        	if(szYdGp.equals(YdConstant.YD_GP_C_SLAB_YARD)){
		        	recL2Para.setField("MSG_ID", "YDY1L002");
	        	} else if(szYdGp.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)){
		        	recL2Para.setField("MSG_ID", "YDY3L002");
	        	} else if(szYdGp.equals(YdConstant.YD_GP_PORT_SLAB_YARD)){  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
		        	recL2Para.setField("MSG_ID", "YDE7L002");
	        	}

	        	recL2Para.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
	        	recL2Para.setField("STL_NO"         , szStlNo);
	        	recL2Para.setField("YD_STK_COL_GP"  , "");
	        	recL2Para.setField("YD_STK_BED_NO"  , "");
				ydDelegate.sendMsg(recL2Para);
				
				/*
				 * 2013.12.25 그라인딩 저장위치때 연주L3에 보급완료 전문송신 			
				 */
	        	if( "A".equals(szStkColGp.substring(0,1))&&
	        	   "DP".equals(szStkColGp.substring(2,4))&&
	        	   "04".equals(szStkColGp.substring(4,6))){
	        		 
					szMsg = "[Jsp Session  -  " + szOperationName +"] YDCSJ001 전문전송";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					newPara = JDTORecordFactory.getInstance().create();
					newPara.setField("JMS_TC_CD", "YDCSJ001");
					newPara.setField("STL_NO", szStlNo);
					
					if("B".equals(szStkColGp.substring(1,2))&&"01".equals(szStkBedNo)){
						sPosition = "1";
					}else if("B".equals(szStkColGp.substring(1,2))&&"02".equals(szStkBedNo)){
						sPosition = "2";
					}else if("A".equals(szStkColGp.substring(1,2))&&"01".equals(szStkBedNo)){
						sPosition = "3";
					}else if("A".equals(szStkColGp.substring(1,2))&&"02".equals(szStkBedNo)){
						sPosition = "4";
					}
					newPara.setField("POSITION", sPosition);
					
	        	}
	        	
				/*
				 * 2011.05.02 윤재광
				 * 아래 메소드 나중에 다시 검토요함-엉망.
				 */
				szRtnMsg =  yddatautil.sendYDPRJ003(szStlNo);

				if(szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
					szMsg = "[JSP-SESSION]  [ "  + szOperationName +  "] 이송완료실적처리 호출성공";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);
				}else {
					szMsg = "[JSP-SESSION]  [ "  + szOperationName +  "] 이송완료실적처리시 ERROR";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.ERROR);
				}

	        	// 3. 적치단 정보 높이 갱신
				//적치베드에 적치단 정보  Z 축 갱신
				/*
				newPara = JDTORecordFactory.getInstance().create();
				newPara.setField("YD_STK_COL_GP", szStkColGp);
				newPara.setField("YD_STK_BED_NO", szStkBedNo);

				szMsg = "[JSP-SESSION]  [ "  + szOperationName +  "] 적치단 Z축 UPDAET 모듈을 호출합니다";
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

				intRtnVal = this.updStkBedZPosFix(newPara);
				*/
			}

		} catch (DAOException e) {

			ydUtils.putLog("SlabJspSeEJB", szMethodName,e.getMessage(), YdConstant.DEBUG);
			throw e;
		} catch (Exception e) {
			ydUtils.putLog("SlabJspSeEJB", szMethodName, e.getMessage(), YdConstant.DEBUG);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{

		}

		szMsg = "JSP-SESSION [산적 위치 수정 ] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		rtnMsg = YdConstant.RETN_CD_SUCCESS;
		return rtnMsg;


	}	// end of updSlabYdStkPosFixBoth




	/**
	 * 후판제품야드 저장위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public String updPlateYdStkPosFix(JDTORecord [] inDto) {

		int intRtnVal 			= 0;
		String szMsg  			="";
		String szMethodName		="updPlateYdStkPosFix";
		String szOperationName	= "저장위치 수정";
		String szStlNo 			= null;
		String szDeletedStlNo	= null;
		String szStkColGpFrom 	= null;
		String szStkBedNoFrom 	= null;
		String szStkLyrNoFrom 	= null;
		String szStkColGp 		= null;
		String szStkBedNo 		= null;
		String szStkLyrNo 		= null;
		String szModifier 		= null;
		String szSTL_PROG_CD 	= null;
		String szUserId 		= null; //작업이력 테이블 [ REGISTER, MODIFIER ]에 필요한 로그인 아이디
		
		JDTORecord      recPara      = null;
		JDTORecord      newPara      = null;
		JDTORecord      recL2Para    = null;
		JDTORecord      recTemp      = null;
		JDTORecord      recStock     = null;

		JDTORecordSet   slabCommRecSet  = null;
		JDTORecordSet   rsTemp  		= null;
		JDTORecordSet   rsBefoLyrInfo  	= null;

		YdStkLyrDao ydStkLyrDao 	= new YdStkLyrDao();
		YdStockDao ydStockDao 		= new YdStockDao();
		YdDelegate ydDelegate 		= new YdDelegate();
		YdWrkHistDao ydWrkHistDao 	= new YdWrkHistDao();
		YdSchRuleDao ydSchRuleDao  	= new YdSchRuleDao();

		boolean bHistFlag 			= false;
		JDTORecordSet rsDelInfo 	= null;
		JDTORecordSet rsStockInfo 	= null;
		JDTORecordSet rsCommInfo 	= null;

		JDTORecord recBefoLyrInfo  	= null;
		JDTORecord recCommInfo 		= null;
		JDTORecord logRecord 		= null;

		String szRtnValue = YdConstant.RETN_CD_SUCCESS;

		String szYdGp 		= null;
		String szLoc 		= null;
		String szLocColGp 	= null;
		String szLocLyrNo 	= null;

		try{
			szMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작   ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			/*********************************************************
			 * 추가 로직(후판 제품 용)
			 * UPDATE 될 위치의 적치단이 없을 경우 먼저 체크해서
			 * 리턴해준다.(첫번째 온 정보(최상단 정보먼저 체크한다)
			 ********************************************************/

			// 처리 할 필요없는 경우
			if (inDto.length < 1 ){

				szMsg = "[Jsp Session : "+szOperationName+"] 변경된 정보가 없습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnValue = "변경된 정보가 없습니다";
				return szRtnValue ;
			}

			recPara = JDTORecordFactory.getInstance().create();
			recPara = inDto[0];
			
			szUserId = yddatautil.setDataDefault(recPara.getField("YD_USER_ID"), ""); //파라미터에서 꺼내옴.
			
			rsTemp = JDTORecordFactory.getInstance().createRecordSet("YdSlabTemp");

			szMsg = "[Jsp Session : "+szOperationName+"] 적치단정보 조회";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsTemp, 0);

			//단정보가 존재 하지않거나  단데이터 생성이 되지않는경우
			if(intRtnVal  < 1){

				szMsg = "[Jsp Session : "+szOperationName+"] 적치단 정보가 존재하지 않습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnValue = "적치단 정보가 존재하지 않습니다";
				return szRtnValue;
			}

			/*********************(Check End)***************************************/

			for(int iLoop=0; iLoop<inDto.length; iLoop++){
				
				szDeletedStlNo = "";

				recPara = JDTORecordFactory.getInstance().create();
				ydUtils.putLog("SlabJspSeEJB", "updSlabYdStkPosFixBoth", " 적치단 정보 READ=============", 4);
				// 1. 적치 단 정보 UPDATE
				szStlNo = yddatautil.setDataDefault(inDto[iLoop].getField("STL_NO"), "");
				recPara.setField("STL_NO", szStlNo);
				//적치열구분
				recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"), ""));
				//적치베드
				recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_BED_NO"), ""));
				//적치단
				recPara.setField("YD_STK_LYR_NO", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_LYR_NO"), ""));

				szStkColGp = recPara.getFieldString("YD_STK_COL_GP");
				szStkBedNo = recPara.getFieldString("YD_STK_BED_NO");
				szStkLyrNo = recPara.getFieldString("YD_STK_LYR_NO");

				szMsg = "신규 재료["+szStlNo+"] 위치 정보 : 열["+szStkColGp+"], 베드["+szStkBedNo+"], 단[" + szStkLyrNo + "]";
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

				//여기가 문제네...
				szModifier = inDto[iLoop].getFieldString("MODIFIER");

				recPara.setField("MODIFIER", szModifier);

				szYdGp =  szStkColGp.substring(0,1); //야드구분

				//적치 상태 [재료번호가 존재 :   "C" , 미존재 : "E"]
				if ( "".equals(szStlNo)){

					bHistFlag = false;
					recPara.setField("YD_STK_LYR_MTL_STAT", "E");
				} else {

					bHistFlag = true;
					recPara.setField("YD_STK_LYR_MTL_STAT", "C");
				}

				// 해당 베드의 위치정보가 기존정보의 재료정보랑 같은 경우는 PASS한다.[2009.12.01 이현성]
				rsBefoLyrInfo  = JDTORecordFactory.getInstance().createRecordSet("rsBefoLyrInfo");

				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsBefoLyrInfo, 0);

				if(intRtnVal > 0){

					rsBefoLyrInfo.first();
					recBefoLyrInfo =  rsBefoLyrInfo.getRecord();

					if(ydDaoUtils.paraRecChkNull(recBefoLyrInfo, "STL_NO").equals(szStlNo)){
						//기존재료 정보와 해당 정보는 같은 정보 이므로 UPDATE 하지 않는다.

						szMsg = "변경되지  재료 정보는 UPDATE 하지 않습니다.";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

						bHistFlag = false;

						//후판제품창고 입고실적 전송부분을 저장위치가 변경되지 않더라도
						//출하로 전송하는 기능을 요청하였음(2009.12.21)
						//버튼을 따로 하나 사용하여 기능을 분리하는게 좋다고 생각함
						//이사님께 확인하고 넣을것 !!
						//이 기능은 후판제품의 저장위치별 재고 LIST  화면기능에 추가한다. (최종락 이사 -2009.12.21)

						continue;
					}
					
					//szStlNo 가 "" 인것은 삭제된 것으로 plate공통을 수정하기 위해서는 여기서 삭제된 제품의 번호를 변수에 저장한다.
					if("".equals(szStlNo)){
						szDeletedStlNo = ydDaoUtils.paraRecChkNull(recBefoLyrInfo, "STL_NO");
					}

				}

				/*
				 *  이적작업  이력정보에 추가 - 2009.11.19 .
				 */

				// bHistFlag - 이력정보 생성 유무 Flag
				if(bHistFlag){

					JDTORecord recOldPos = 	JDTORecordFactory.getInstance().create();
					JDTORecord recStockInfo = JDTORecordFactory.getInstance().create();

					JDTORecord recWrkHistPara = JDTORecordFactory.getInstance().create();
					JDTORecord recWrkHistInfo = JDTORecordFactory.getInstance().create();
					JDTORecordSet outRecSet = null;

					rsDelInfo		=	JDTORecordFactory.getInstance().createRecordSet("rsdelInfo");
					String lszYdSchCd = "";

					recOldPos.setField("STL_NO", szStlNo);
					recOldPos.setField("YD_STK_LYR_MTL_STAT", "C");

					//	재료관련 정보로 이력정보를 추가적으로 넣을부분이 존재하면 재료정보를 조회한 후 이부분에 추가해줌
					rsStockInfo = JDTORecordFactory.getInstance().createRecordSet("rsStockInfo");

					intRtnVal = ydStockDao.getYdStock(recOldPos, rsStockInfo, 0);

					// 재료 정보 이력정보에 추가 (2009.11.09)
					if(intRtnVal > 0){
						//STOCK 정보가 존재할 경우
						rsStockInfo.first();
						recStockInfo= rsStockInfo.getRecord();
					}

					intRtnVal  = ydStkLyrDao.getYdStklyr(recOldPos, rsDelInfo, 3);

					//작업이력 - 권상정보 관련 입력(적치단에 정보가 있을경우)
					if(intRtnVal > 0 ){
						rsDelInfo.first();

						recOldPos      = 	JDTORecordFactory.getInstance().create();
						recOldPos = rsDelInfo.getRecord();

						recStockInfo.setField("YD_UP_WR_LOC", ydDaoUtils.paraRecChkNull(recOldPos, "YD_STK_COL_GP")+  ydDaoUtils.paraRecChkNull(recOldPos, "YD_STK_BED_NO"));
						recStockInfo.setField("YD_UP_WR_LAYER", ydDaoUtils.paraRecChkNull(recOldPos, "YD_STK_LYR_NO") );
						//권상완료일시 --현재시간 구하는 정보
						recStockInfo.setField("YD_UP_CMPL_DT",YdUtils.getCurDate("yyyyMMddHHmmss") );
					} else {
						// 작업이력 - 적치단에 정보가없을경우는 야드별 공통 정보에서 권상정보를 가지고 온다. -2010.01.05 (후판제품에 적용하라고 지시받음)
						szLocColGp = "";
						szLocLyrNo = "";

						recCommInfo = JDTORecordFactory.getInstance().create();
						rsCommInfo 	= JDTORecordFactory.getInstance().createRecordSet("rsCommInfo");
						recCommInfo.setField("PLATE_NO", szStlNo);

						intRtnVal = ydStockDao.getYdStock(recCommInfo, rsCommInfo, 4);

						if(intRtnVal < 0){
							szMsg = "[Jsp Session : "+szOperationName+"] PLATE 공통 조회 ERROR  ";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else if(intRtnVal == 0){
							szMsg = "[Jsp Session : "+szOperationName+"] 조회된 PLATE 공통정보가 없습니다";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

						}else{

							rsCommInfo.first();
							recCommInfo = JDTORecordFactory.getInstance().create();
							recCommInfo = rsCommInfo.getRecord();

							szLoc = ydDaoUtils.paraRecChkNull(recCommInfo, "YD_STR_LOC");

							if(  (!szLoc.equals("")) &&  (szLoc.length() <= 8)  ){
								szLocColGp = szLoc;
								szLocLyrNo = "";
							}
							else if(szLoc.equals("") || szLoc.length() != 10){

								szMsg = "[Jsp Session : "+szOperationName+"]  PLATE 공통정보의 저장위치 정보가 맞지 않습니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

							}else{

								szLocColGp = szLoc.substring(0,6) + "0"  +szLoc.substring(6,7);
								szLocLyrNo = szLoc.substring(7,10);

								szMsg = "[Jsp Session : "+szOperationName+"]  PLATE 공통정보의 권상위치" + szLoc  ;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

								//권상완료일시 --현재시간 구하는 정보
								recStockInfo.setField("YD_UP_CMPL_DT",YdUtils.getCurDate("yyyyMMddHHmmss") );

							}
						}
						recStockInfo.setField("YD_UP_WR_LOC", szLocColGp);
						recStockInfo.setField("YD_UP_WR_LAYER",szLocLyrNo);
					}
					recStockInfo.setField("YD_GP", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"), " ").substring(0, 1));
					recStockInfo.setField("STL_NO", szStlNo);

					//////////////////////////////////////////////////////////////////////////////

					// 작업이력 - 권하정보 입력
					recStockInfo.setField("YD_DN_WR_LOC",    yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"),"") +  yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_BED_NO"),"") );
					recStockInfo.setField("YD_DN_WR_LAYER",  yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_LYR_NO"),""));
					//권하완료일시 --현재시간 구하는 정보
					recStockInfo.setField("YD_DN_CMPL_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));

					//이적 작업 스케줄 코드를 얻는다.
					lszYdSchCd = ydUtils.getMakeSchCdMM(  yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"),"      ").substring(0, 1) ,
						yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"),"      ").substring(1, 2),
						yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"),"      ").substring(2, 4) );

					szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 해당 이적작업 스케줄 구하기.["+ lszYdSchCd +"]" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

					// 야드 스케줄 코드 - YD_SCH_CD
					recStockInfo.setField("YD_SCH_CD", lszYdSchCd );

					// 야드보조작업여부 - YD_AID_WRK_YN
					recStockInfo.setField("YD_AID_WRK_YN" , "N");

					outRecSet = JDTORecordFactory.getInstance().createRecordSet("outRecSet");

					//해당 스케줄 정보로 주작업 크레인 정보를 가지고 온다.
					recWrkHistPara.setField("YD_SCH_CD", lszYdSchCd);
					intRtnVal = ydSchRuleDao.getYdSchrule(recWrkHistPara, outRecSet, 0);

					if(intRtnVal > 0){
						outRecSet.first();
						recWrkHistInfo = outRecSet.getRecord();

					}else if (intRtnVal ==0){
						szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 해당 스케줄에 대한 기준정보가 존재 하지 않습니다.["+ lszYdSchCd +"]" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

					//스케줄 기준의 작업 크레인 정보를 넣어준다.
					recStockInfo.setField("YD_EQP_ID" , ydDaoUtils.paraRecChkNull(recWrkHistInfo, "YD_WRK_CRN"));

					// 야드수불구분 - YD_GNT_GP

					// 스케줄 코드로 판단하지 않고 STOCK 내용을 참조하여 입고일자가 없는경우는 'L' :입고 정보로
					// 그렇지 않은경우는 이적 이력정보를 남겨줍니다. (2009.12.28 이현성 수정)
					if(ydDaoUtils.paraRecChkNull(recStockInfo, "YD_RCPT_DATE").equals("")){

						szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 이력정보구분 입고 [ " + szStlNo +"]" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
						recStockInfo.setField("YD_GNT_GP", YdConstant.YD_GNT_GP_RCPT);

						//입고일자가 없는경우 이므로 STOCK에 입고일자를 UPDATE 해준다.
						szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 입고일자 UPDATE [ " + szStlNo +"]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

						recStock = JDTORecordFactory.getInstance().create();
						String curDate = YdUtils.getCurDate("yyyyMMddHHmmss");

						recStock.setField("STL_NO", szStlNo);
						recStock.setField("YD_RCPT_DATE", curDate.substring(0, 8));
						recStock.setField("MODIFIER", szModifier);

						ydUtils.displayRecord("저장위치[산적위치]수정", recStock);

						int nRtnVal = ydStockDao.updYdStock(recStock, 0);

						if(nRtnVal < 0 ){
							szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 입고일자 UPDATE ERROR [ " + szStlNo +"][" + nRtnVal + "]" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else if (nRtnVal == 0 ){
							 szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 입고일자 UPDATE ERROR [ " + szStlNo +"][" + nRtnVal + "]" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

						}else{
							 szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 입고일자 UPDATE 성공 [ " + szStlNo +"][" + nRtnVal + "]" ;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}

						String szRtnMsg = null;

						szRtnMsg =  yddatautil.sendYDPRJ003(szStlNo);

						if(szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
							szMsg = "[JSP-SESSION]  [ "  + szOperationName +  "] 이송완료실적처리 호출성공";
							ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);
						}else {
							szMsg = "[JSP-SESSION]  [ "  + szOperationName +  "] 이송완료실적처리시 ERROR";
							ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.ERROR);
						}
					}else{
						szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 이력정보구분 이적"  + szStlNo +"]" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
						recStockInfo.setField("YD_GNT_GP", YdConstant.YD_GNT_GP_MVSTK);
					}


					// 야드스케줄 기동 구분 "B" 로 넣어준다. -2009.12.10
					recStockInfo.setField("YD_SCH_ST_GP", "B");

					// 2025-09-15 추관식 : 저장위치 수정시 TB_YD_WRKHIST 의 REGISTER 부분에 수정자 사번이 남도록 수정
					recStockInfo.setField("MODIFIER", szUserId);
					
					//이력정보 남기기
					intRtnVal = ydWrkHistDao.insYdWrkHistPosFix(recStockInfo);

					//ydUtils.putYdFlexCrnWrk("", recStockInfo);

					if(intRtnVal > 0){
						szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 이력정보를 로깅하였습니다." ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					}else{
						szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 이력정보를 로깅 실패 하였습니다" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
				}


				// 해당 재료번호가 적치중일 경우 해당 적치 정보 삭제 기능 추가 ==============================================
				JDTORecord recDelPara 	= JDTORecordFactory.getInstance().create();
				rsDelInfo				= JDTORecordFactory.getInstance().createRecordSet("rsdelInfo");

				if("".equals(szStlNo.trim())){
					//해당작업 필요없음

				}else{

					recDelPara.setField("STL_NO", szStlNo);
					recDelPara.setField("YD_STK_LYR_MTL_STAT", "");

					int nRtnVal  = ydStkLyrDao.getYdStklyr(recDelPara, rsDelInfo, 3);

					if(nRtnVal == 0 ){
						//해당 작업 필요없음

					}else if(nRtnVal > 0 ){

						//정보 존재시 해당 Map Clear
						rsDelInfo.first();

						do{
							recDelPara   = 	JDTORecordFactory.getInstance().create();
							recDelPara   =  rsDelInfo.getRecord();

							szStkColGpFrom = yddatautil.setDataDefault(recDelPara.getField("YD_STK_COL_GP"), "");
							szStkBedNoFrom = yddatautil.setDataDefault(recDelPara.getField("YD_STK_BED_NO"), "");
							szStkLyrNoFrom = yddatautil.setDataDefault(recDelPara.getField("YD_STK_LYR_NO"), "");

							szMsg = "기존 재료 위치 정보 : 열["+szStkColGpFrom+"], 베드["+szStkBedNoFrom+"], 단[" + szStkLyrNoFrom + "]";
							ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);
							recDelPara.setField("STL_NO", "");
							recDelPara.setField("YD_STK_LYR_MTL_STAT", "E");

							ydStkLyrDao.updYdStklyr(recDelPara, 0);

							//-------------------------------------------------------------------------------------------------------
				            // 2010.04.22 윤재광 추가.
							// 저장위치 수정시 From위치에 (운송지시대기, 운송대기)인 제품이 없으면 Bed정보를 완산에서 입출고가능으로 변경
				            //-------------------------------------------------------------------------------------------------------
				            YdCommonUtils.procChangeBedTypeForPlateGds(szStkColGpFrom+szStkBedNoFrom,
																	   szMethodName);

						}while(rsDelInfo.next());

					}
				}

				//적치단 정보 UPDATE
				intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);

				logRecord = JDTORecordFactory.getInstance().create();
				logRecord.setField("YD_GP", szYdGp);
				logRecord.setField("YD_UP_WR_LOC", szStkColGp+szStkBedNo);

				ydUtils.putYdFlexCrnWrk("", logRecord);


				// 2. 공통 TABLE 정보 UPDATE================================================
				newPara = JDTORecordFactory.getInstance().create();
				if("".equals(szStlNo)){ 
					szStlNo =  szDeletedStlNo;
				} 
				newPara.setField("STL_NO", szStlNo);
				

				ydUtils.putLog("SlabJspSeEJB", "updPlateYdStkPosFix", " 재료정보 :::== "+szStlNo, YdConstant.DEBUG);

				slabCommRecSet =	JDTORecordFactory.getInstance().createRecordSet("YdSlabTemp");
				//intRtnVal =ydStockDao.getYdStock(newPara, slabCommRecSet, 0);
				/*
				 * 설명 : 재료진도코드의 데이타 정합성문제로 후판 공통테이블을 조회
				 */
				intRtnVal =ydStockDao.getYdStock(newPara, slabCommRecSet, 3);

				slabCommRecSet.first();

				if(slabCommRecSet.size() <1 ) {
					//공통 테이블 UPDATE 안됨
					continue;
				}

				newPara = slabCommRecSet.getRecord(0);

				szSTL_PROG_CD = yddatautil.setDataDefault(newPara.getField("CURR_PROG_CD"),"");

				//후판 공통 UPDATE
				newPara = JDTORecordFactory.getInstance().create();

				if(!"".equals(szDeletedStlNo)) {
					//삭제되는 경우임으로 PLATE 공통의 YD_STR_LOC 를 Clear(Default:차상위치) 한다.
					newPara.setField("YD_GP",           szStkColGp.substring(0,1));
					newPara.setField("YD_BAY_GP",       szStkColGp.substring(1,2));
					newPara.setField("YD_EQP_GP",       szStkColGp.substring(2,4));
					newPara.setField("YD_STK_COL_NO",   szStkColGp.substring(4,6));
					newPara.setField("YD_STK_BED_NO",   recPara.getField("YD_STK_BED_NO"));
					newPara.setField("YD_STK_LYR_NO",   recPara.getField("YD_STK_LYR_NO"));
					newPara.setField("YD_STR_LOC",      szStkColGp.substring(0,1)+szStkColGp.substring(1,2)+"PT011001"); 	 						
				} else {
					newPara.setField("YD_GP",           szStkColGp.substring(0,1));
					newPara.setField("YD_BAY_GP",       szStkColGp.substring(1,2));
					newPara.setField("YD_EQP_GP",       szStkColGp.substring(2,4));
					newPara.setField("YD_STK_COL_NO",   szStkColGp.substring(4,6));
					newPara.setField("YD_STK_BED_NO",   recPara.getField("YD_STK_BED_NO"));
					newPara.setField("YD_STK_LYR_NO",   recPara.getField("YD_STK_LYR_NO"));
					newPara.setField("YD_STR_LOC",      ydUtils.ParsingStkColGpBedLyr(szStkColGp, szStkBedNo, szStkLyrNo));					
				}

				newPara.setField("MODIFIER",        yddatautil.setDataDefault( inDto[iLoop].getField("MODIFIER"),"YD"));
				newPara.setField("FNL_REG_PGM",     "updPlateYdStkPosFix" );
				newPara.setField("PLATE_NO", 		szStlNo);

				if(szStlNo.equals("")){
					//재료정보가 없을경우는 공통정보를 UPDATE 하지않는다. 20091221
					szMsg = "해당위치에 재료정보가 없는 경우는 공통정보를 UPDAT 하지않는다.";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);
				}else{
					szMsg = "해당재료[" + szStlNo  +"] 에대한 후판 공통 UPDATE 작업시작" ;
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

					intRtnVal = ydStockDao.updPtComm_LOC(newPara, 1);

					if(intRtnVal< 0) {
						szMsg = "공통 UPDATE ERROR";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.ERROR);
						//return ;
					}else if (intRtnVal == 0){
						szMsg = "후판 공통 UPDATE 해야할 데이터가 없습니다.";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.ERROR);
					}else{
						szMsg = "후판 공통 UPDATE 성공 재료번호[" + szStlNo + "]";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);
					}
				}

				/*
			 	 * 2011.05.02 윤재광
			 	 * 후판제품 OVER ROLL 체크기능 추가.
			 	 */
				if(szSTL_PROG_CD.equals(YdConstant.PROG_CD_RCPT_WAIT)) {

					JDTORecord recPtPara = JDTORecordFactory.getInstance().create();
					recPtPara.setField("JMS_TC_CD",          "YDYDJ297");
					recPtPara.setField("YD_STK_BED_STL_SH",  "1");
					recPtPara.setField("STL_NO1",			 szStlNo);

					//전문 송신
					ydDelegate.sendMsg_NoMakeTc(recPtPara);

					szMsg = "A후판 창고 야드 BOOK_OUT 처리 완료후 오버롤 체크완료!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}


			   /*
			    * L2 전문송신
			    */
				// 저장위치 정보
	        	recL2Para = JDTORecordFactory.getInstance().create();
	        	//--2013.03.07 수정 (3기)
	        	if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) {
	        		//2후판
		        	recL2Para.setField("MSG_ID"         , "YDY8L001");
		        	//전사물류개선 2021. 4. 3(2022.01.27 Y8,Y9 별도 송신 목적으로 수정) 
		        	//if(PlateGdsYdUtil.isSendToEaiY9_ydStkColGp(szStkColGp)){
		        	//	recL2Para.setField("MSG_ID"         , "YDY9L001");
		        	//}
	        	} else if("4".equals(szYdGp)){
	        		//사외창고 지정(후판->사외창고)시, L2 전송 X
	        	} else {
	        		//1후판
		        	recL2Para.setField("MSG_ID"         , "YDY4L001");
	        	}
	        	recL2Para.setField("YD_INFO_SYNC_CD", "4");
	        	recL2Para.setField("YD_STK_COL_GP"  , szStkColGp);
	        	recL2Para.setField("YD_STK_BED_NO"  , szStkBedNo);
	        	
	        	if(!"4".equals(szYdGp)){  //사외창고 지정(후판->사외창고)시, L2 전송 X
	        		ydDelegate.sendMsg(recL2Para);
	        	}
	        	//전사물류개선 2021. 4. 3(2022.01.27 Y8,Y9 별도 송신 목적으로 수정) 
	        	if(PlateGdsYdUtil.isSendToEaiY9_ydStkColGp(szStkColGp)){
	        		recL2Para.setField("MSG_ID"         , "YDY9L001");
	        		recL2Para.setField("YD_INFO_SYNC_CD", "4");
		        	recL2Para.setField("YD_STK_COL_GP"  , szStkColGp);
		        	recL2Para.setField("YD_STK_BED_NO"  , szStkBedNo);
		        	ydDelegate.sendMsg(recL2Para);
	        	}
	        	
	        	// 저장품제원정보 요구
	        	recL2Para = JDTORecordFactory.getInstance().create();
	        	//--2013.03.07 수정 (3기)
	        	if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) {
	        		//2후판
		        	recL2Para.setField("MSG_ID"         , "YDY8L002");
		        	//전사물류개선 2021. 4. 3(2022.01.27 Y8,Y9 별도 송신 목적으로 수정) 
		        	//if(PlateGdsYdUtil.isSendToEaiY9_ydStkColGp(szStkColGp)){
		        	//	recL2Para.setField("MSG_ID"         , "YDY9L002");
		        	//}
	        	} else if("4".equals(szYdGp)){
	        		//사외창고 지정(후판->사외창고)시, L2 전송 X
	        	}  else {
	        		//1후판
		        	recL2Para.setField("MSG_ID"         , "YDY4L002");
	        	}
	        	recL2Para.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
	        	recL2Para.setField("STL_NO"         , szStlNo);
	        	recL2Para.setField("YD_STK_COL_GP"  , "");
	        	recL2Para.setField("YD_STK_BED_NO"  , "");
	        	
	        	if(!"4".equals(szYdGp)){  //사외창고 지정(후판->사외창고)시, L2 전송 X
	        		ydDelegate.sendMsg(recL2Para);
	        	}
	        	//전사물류개선 2021. 4. 3(2022.01.27 Y8,Y9 별도 송신 목적으로 수정) 
	        	if(PlateGdsYdUtil.isSendToEaiY9_ydStkColGp(szStkColGp)){
	        		recL2Para.setField("MSG_ID"         , "YDY9L002");
	        		recL2Para.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
		        	recL2Para.setField("STL_NO"         , szStlNo);
		        	recL2Para.setField("YD_STK_COL_GP"  , "");
		        	recL2Para.setField("YD_STK_BED_NO"  , "");
		        	ydDelegate.sendMsg(recL2Para);
	        	}
	        	
				/*
				 * 후판제품 저장위치수정 시 출하로 입고작업실적송신 - 임시용으로 사용됨
				 * 재료진도코드가 입고대기인 경우에만 전송처리
				 */
				JDTORecord outRec  	= JDTORecordFactory.getInstance().create();
				String curDate 		= YdUtils.getCurDate("yyyyMMddHHmmss");


				szMsg ="[JSP Session 저장위치 수정 updPlateYdStkPosFix]후판제품[K] 저장위치수정 시 입고실적송신 전 재료진도코드[입고대기-H, 판정보류-F, 종합판정대기-G] 판단 - " + szSTL_PROG_CD;
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

				if(szSTL_PROG_CD.equals(YdConstant.PROG_CD_OVALL_STMP_WAIT) ||
				   szSTL_PROG_CD.equals("2") ||
				   szSTL_PROG_CD.equals(YdConstant.PROG_CD_STMP_HOLD) ||
				   szSTL_PROG_CD.equals(YdConstant.PROG_CD_RCPT_WAIT)) {

					//후판제품 : 열(6) + 베드(1) + 단(3)
					String szSTORE_LOC = ydUtils.ParsingStkColGpBedLyr(	szStkColGp,
																		recPara.getFieldString("YD_STK_BED_NO"),
																		recPara.getFieldString("YD_STK_LYR_NO"));

					//후판제품공통테이블 조회 - PROD_ITEM_CODE 조회
					rsTemp	= JDTORecordFactory.getInstance().createRecordSet("");
					recTemp	= JDTORecordFactory.getInstance().create();
					recTemp.setField("PLATE_NO", szStlNo);

					intRtnVal = ydStockDao.getYdStock(recTemp, rsTemp, 4);			//후판공통테이블 조회
					if( intRtnVal == 0 ) {
						szMsg ="[JSP Session 저장위치 수정 updPlateYdStkPosFix]후판제품 공통테이블 조회 시 재료["+szStlNo+"]가 존재하지 않음";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.ERROR);
						continue;
					}else if( intRtnVal < 0 ) {
						szMsg ="[JSP Session 저장위치 수정 updPlateYdStkPosFix]후판제품 공통테이블 조회 시 오류발생";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.ERROR);
						continue;
					}
					rsTemp.absolute(1);
					recTemp = rsTemp.getRecord();

					String szPROD_ITEM_CODE = yddatautil.setDataDefault( recTemp.getField("PRD_ITM_CD"),"");


					//		1.	인터페이스ID			   TC_CODE				VARCHAR2(8)		YDDMR002
					//		2.	전송일시				   TC_CREATE_DDTT		VARCHAR2(14)	YYYYMMDDHHMMSS
					//		3.	제품 번호				   GOODS_NO				VARCHAR2(11)	STL_NO
					//		4.	입고 일자				   RECEIPT_DATE			VARCHAR2(8)		YD_DN_RSLT_DT(1:8)
					//		5.	입고 시각				   RECEIPT_TIME			VARCHAR2(6)		YD_DN_RSLT_DT(9:6)
					//		6.	YARD 구분			   YD_GP				VARCHAR2(1)
					//		7.	야드저장위치			   STORE_LOC			VARCHAR2(10)
					//		8.  ITEMCODE			   PROD_ITEM_CODE		VARCHAR2(25)
					if(  "TC0101".equals(szStkColGp)||"TC0102".equals(szStkColGp)||
	    			     "TC0103".equals(szStkColGp)||"TC0104".equals(szStkColGp)||
	    				 "TC0105".equals(szStkColGp)||
	    				 szStkColGp.startsWith("TB01")||
	    				 szStkColGp.startsWith("TB02")||
	    				 szStkColGp.startsWith("TB03")){
						/*
		        		 * #2UT 베드에 적치시 입고실적 미송신
		        		 */
						
						if(  "TC0101".equals(szStkColGp)||"TC0102".equals(szStkColGp)||
		    			     "TC0103".equals(szStkColGp)||"TC0104".equals(szStkColGp)||
		    				 "TC0105".equals(szStkColGp))
						{
			        		/*
				    		 * 2015.09.09 윤재광
				    		 * 2후판 제품창고 #2UT야드 적치시 후판조업L3로 전문송신
				    		 */	
			        		JPlateYdDelegate jDelegate = new JPlateYdDelegate();
			        		
			        		szMsg = "["+ szOperationName +"] 후판조업으로 저장위치 변경이력 실적 전송 .. 시작";
			        		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
			        		JDTORecord jrecPara = JDTORecordFactory.getInstance().create();
			        		jrecPara.setField("MSG_ID", 		"YDPPJ011");
			        		jrecPara.setField("YD_STK_COL_FR", 	szStkColGpFrom);	// From적치열
			        		jrecPara.setField("YD_STK_BED_FR", 	"01");				// From적치BED
			        		jrecPara.setField("YD_STK_COL_TO", 	szStkColGp);		// TO적치열
			        		jrecPara.setField("YD_STK_BED_TO", 	"01");				// TO적치BED
			        		jrecPara.setField("YD_EQP_WRK_SH", 	"");				// 야드설비작업매수
			        		jrecPara.setField("ARR_STL_NO", 	szStlNo);
			    	        
			        		jDelegate.sendMsg(jrecPara);

							szMsg = "["+ szOperationName +"] 후판조업으로 저장위치 변경이력 실적 전송 .. 완료>>>>";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									
						}
						
					}else{
						if(szSTL_PROG_CD.equals(YdConstant.PROG_CD_RCPT_WAIT) ||szSTL_PROG_CD.equals("2")){  //입고실적은 진도코드 H이거나 2(소재진도)일경우만 전송되도록 수정
						
						//PIDEV			
//						String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APPPI0", "*", "*");
//						if("Y".equals(sApplyYnPI)) {
							
							outRec.setField("MQ_TC_CD"        , "M10YDLMJ1012");
							outRec.setField("MQ_TC_CREATE_DDTT" , new String(curDate));
							
							outRec.setField("YD_GP"          , szStkColGp.substring(0,1));
							outRec.setField("DIST_GOODS_GP"  , "P");
							outRec.setField("YARD_GP" 		 , "");
							outRec.setField("GOODS_NO"       , szStlNo);
							outRec.setField("STORE_LOC_CD"      , szSTORE_LOC);
							
							outRec.setField("RECEIPT_DATE"   , curDate.substring(0, 8));
							outRec.setField("RECEIPT_TIME"   , curDate.substring(8, 14));
							
//						} else {
//							
//							outRec.setField("TC_CODE"        , "YDDMR002");
//							outRec.setField("TC_CREATE_DDTT" , new String(curDate));
//							outRec.setField("GOODS_NO"       , szStlNo);
//							outRec.setField("RECEIPT_DATE"   , curDate.substring(0, 8));
//							outRec.setField("RECEIPT_TIME"   , curDate.substring(8, 14));
//							outRec.setField("YD_GP"          , szStkColGp.substring(0,1));
//							outRec.setField("STORE_LOC"      , szSTORE_LOC);
//							outRec.setField("PROD_ITEM_CODE" , szPROD_ITEM_CODE);
//							
//						}
						ydDelegate.sendMsg_NoMakeTc(outRec);
						
//PIDVE_QM						
						szMsg = "["+ szOperationName +"] 후판품질으로입고실적 전송 .. 시작YDQMJ601>>>>";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

//						if("Y".equals(sApplyYnPI)) {
							szMsg = "["+ szOperationName +"] 후판품질으로입고실적 전송 .. 시작YDQMJ601>>>>";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							JDTORecord outRec1 = JDTORecordFactory.getInstance().create();
							outRec1.setField("JMS_TC_CD"          , "YDQMJ601");
							outRec1.setField("JMS_TC_CREATE_DDTT" , new String(curDate));
							outRec1.setField("STL_NO"             , szStlNo);
							ydDelegate.sendMsg_NoMakeTc(outRec1);

							szMsg = "["+ szOperationName +"] 후판품질으로입고실적 전송 .. 완료YDQMJ601>>>>";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
//						}
					  }
					}
				}else{

					String szBEFO_STORE_LOC = ydUtils.ParsingStkColGpBedLyr(szStkColGpFrom, szStkBedNoFrom, szStkLyrNoFrom );
					String szTO_STORE_LOC 	= ydUtils.ParsingStkColGpBedLyr(szStkColGp, 	szStkBedNo, 	szStkLyrNo );

					//		이적작업실적 전송
					//		1.	인터페이스ID			TC_CODE					VARCHAR2(8)		YDDMR005
					//		2.	전송일시				TC_CREATE_DDTT			VARCHAR2(14)	YYYYMMDDHHMMSS
					//		3.	제품 번호				GOODS_NO				VARCHAR2(11)	이적 주작업, 타 작업의 보조작업으로 권하실적처리 시점
					//		4.	FROM 저장위치			BEFO_STORE_LOC			VARCHAR2(11)	권상(From)위치
					//		5.	TO 저장위치			TO_STORE_LOC			VARCHAR2(11)	권하(To)위치
					//		6.	이적 일자				MOVENSTACK_DATE			VARCHAR2(8)		YD_DN_RSLT_DT(1:8)
					//		7.	이적 시각				MOVENSTACK_TIME			VARCHAR2(6)		YD_DN_RSLT_DT(9:6)

			    	// PIDEV
//					String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APPPI0", "*", "*");
			    		
//					if("Y".equals(sApplyYnPI)) {
						
						outRec.setField("MQ_TC_CD"        , "M10YDLMJ1032");
						outRec.setField("MQ_TC_CREATE_DDTT" , curDate);
						outRec.setField("YD_GP"          , szStkColGp.substring(0,1));						
						outRec.setField("DIST_GOODS_GP"  , "P");
						outRec.setField("YARD_GP" 		 , "");	
						outRec.setField("GOODS_NO"       , szStlNo);						
						outRec.setField("STORE_LOC_CD_FROM" , szBEFO_STORE_LOC);
						outRec.setField("STORE_LOC_CD_TO"   , szTO_STORE_LOC);
						outRec.setField("MOVENSTACK_DATE", curDate.substring(0, 8));
						outRec.setField("MOVENSTACK_TIME", curDate.substring(8, 14));
						
//					} else {
//						
//						outRec.setField("TC_CODE"        , "YDDMR005");
//						outRec.setField("TC_CREATE_DDTT" , curDate);
//						outRec.setField("GOODS_NO"       , szStlNo);
//						outRec.setField("BEFO_STORE_LOC" , szBEFO_STORE_LOC);
//						outRec.setField("TO_STORE_LOC"   , szTO_STORE_LOC);
//						outRec.setField("MOVENSTACK_DATE", curDate.substring(0, 8));
//						outRec.setField("MOVENSTACK_TIME", curDate.substring(8, 14));
//						
//					}
					
					ydDelegate.sendMsg_NoMakeTc(outRec);
				}


				// 3. 적치단 정보 높이 갱신
				//적치베드에 적치단 정보  Z 축 갱신
				/* 2011.05.02 윤재광 - 사용안함.
				newPara = JDTORecordFactory.getInstance().create();
				newPara.setField("YD_STK_COL_GP", szStkColGp);
				newPara.setField("YD_STK_BED_NO", szStkBedNo);

				intRtnVal = this.updStkBedZPosFix(newPara);

				szMsg =" 적치베드에 적치단 정보  Z 축 갱신================================ ";
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);
				*/

			}

			szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			return szRtnValue;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{

		}
	}	// end of updPlateYdStkPosFix

	/**
	 * 적치단 정보 Z축 수정
	 * 적치단 정보의 Z축값을 재계산 하여 넣어준다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public int updStkBedZPosFix(JDTORecord  msgRec) throws DAOException {

		//JDTORecord 정보를 받으며 ㅣ
		//Filed 정보에는 적치열 구분 , 적치베드 정보가 담겨 있어야 한다.

		//적치베드 기준 Z 축
		int stdZPos = 1;
		int intGp = 0;

		int intMtlT =0;
		String  szMethodName = "updStkBedZPosFix";
		String szOperationName = "적치단 정보 Z축 수정";


		String szMsg ="";
		szMsg        = "적치단 정보 Z축 수정 기능 호출됨 ";
		ydUtils.putLog("SlabJspSeEJB", szMethodName,szMsg, YdConstant.DEBUG);

		JDTORecord 		stkBedRec      = JDTORecordFactory.getInstance().create();
		JDTORecordSet   stkBedRecSet   = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord 		stkLyrRec      = JDTORecordFactory.getInstance().create();
		JDTORecordSet   stkLyrRecSet   = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord 		newRec         = JDTORecordFactory.getInstance().create();
		JDTORecord 		stockRec       = JDTORecordFactory.getInstance().create();
		JDTORecordSet   stockRecSet    = JDTORecordFactory.getInstance().createRecordSet("retTmp");



		String szStlNo =null;

		YdStkBedDao ydStkBedDao  = new YdStkBedDao();
		YdStkLyrDao ydStkLyrDao  = new YdStkLyrDao();
		YdStockDao ydStockDao    = new YdStockDao();
		try {


			szMsg = "JSP-SESSION [적치단 정보 Z축 수정 ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);



			ydUtils.displayRecord(szOperationName, msgRec);
			intGp = ydStkBedDao.getYdStkbed(msgRec, stkBedRecSet, 0);


			if (intGp <1 )
			{


				szMsg = "해당 베드 정보가 존재하지않습니다============================ ";
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.ERROR);

				return intGp;
			}


			stkBedRecSet.first();
			stkBedRec = stkBedRecSet.getRecord();


			//stdZPos = stkBedRec.getFieldInt("YD_STK_BED_ZAXIS");
			stdZPos = ydDaoUtils.paraRecChkNullInt(stkBedRec, "YD_STK_BED_ZAXIS");

			//정리되어 오지만 추후에 정리 안될수 있으므로 정렬작업 필요할 수 도 있음

			intGp = ydStkLyrDao.getYdStklyr(msgRec, stkLyrRecSet, 1);

			if (intGp==0 ){
				return intGp;
			}
			else if (intGp <0 )
			{

				szMsg = "단정보 READ 오류발생 ======================================== ";
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.WARNING);

				return intGp;
			}

			stkLyrRecSet.first();
			do
			{
				stkLyrRec = stkLyrRecSet.getRecord();

				//szStlNo = stkLyrRec.getFieldString("STL_NO");
				szStlNo = ydDaoUtils.paraRecChkNull(stkLyrRec, "STL_NO");


				szMsg = "STL_NO :"+szStlNo;
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.WARNING);


				//재료번호가 존재하지않을경우는 건너뛴다
				if (szStlNo.trim().equals(""))
				{
					szMsg = "재료번호 미존재      ======================================== ";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.WARNING);
					continue;
				}


				//재료번호 존재시에는 제품정보에서 재료정보의 두께를 읽어온다.
				newRec    =    JDTORecordFactory.getInstance().create();
				newRec.setField("STL_NO", szStlNo);
				intGp    =      ydStockDao.getYdStock(newRec, stockRecSet, 0);

				//저장품에 재료정보가 없을경우도 건너뛴다.
				if (intGp ==0 )
				{
					szMsg = "저장품에 재료번호 미존재================================== ";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.WARNING);
					continue;

				}

				stockRecSet.first();
				stockRec = stockRecSet.getRecord();


				szMsg = "높이  계산 시작================================== ";
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.INFO);





				//intMtlT = stockRec.getFieldInt("YD_MTL_T");
				intMtlT = ydDaoUtils.paraRecChkNullInt(stockRec, "YD_MTL_T1");

				szMsg = "현재료의 두께는  '"+intMtlT+"' 입니다";
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.INFO);


				stdZPos += intMtlT;

				//해당 단 정보에 Update 하여준다.
				stkLyrRec.setField("YD_STK_LYR_ZAXIS", new Integer(stdZPos));

				ydStkLyrDao.updYdStklyr(stkLyrRec, 0);

				szMsg = "단정보 높이를  '"+stdZPos+"' 설정하였습니다";
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.INFO);

			}while(stkLyrRecSet.next());

			return intGp;

		} catch (JDTOException e) {

			e.printStackTrace();
		}
		szMsg = "JSP-SESSION [적치단 정보 Z축 수정 ] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		return intGp;

	}

	/**
	 * 크레인구분 조회 (화면:스케줄기준관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCrnGp(JDTORecord inDto) throws DAOException {

		int    intRtnVal    = 0;
		String szMsg        = "";
		String szMethodName = "getCrnGp";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara 	= JDTORecordFactory.getInstance().create();
		try {

			szMsg = "JSP-SESSION [크레인구분 조회 (화면:스케줄기준관리)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			recPara.setField("YD_BAY_GP", 	yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"),""));
			recPara.setField("GBN", 		yddatautil.setDataDefault(inDto.getField("GBN"),""));

			YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

			intRtnVal = ydSchRuleDao.getYdSchrule(recPara, outRecSet, 300);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return outRecSet;

			 } // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		szMsg = "JSP-SESSION [크레인구분 조회 (화면:스케줄기준관리)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}

	/**
	 * 스케줄기준 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabYdSchStd(JDTORecord inDto) throws DAOException {

		int    intRtnVal    = 0;
		String szMsg        = "";
		String szMethodName = "getSlabYdSchStd";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		try {


			szMsg = "JSP-SESSION [스케줄기준 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			recPara.setField("YD_GP",    	inDto.getField("YD_GP"));
			recPara.setField("YD_BAY_GP", 	inDto.getField("YD_BAY_GP"));
			recPara.setField("YD_SCH_CD", 	yddatautil.setDataDefault(inDto.getField("YD_SCH_CD"),   ""));
			//!A 크래인명 항목 추가 (박지열 2010/03/30)
			recPara.setField("YD_EQP_ID", 	yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"),   ""));
			recPara.setField("YD_EQP_ID2", 	yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"),   ""));
			recPara.setField("CD_CONTENTS", yddatautil.setDataDefault(inDto.getField("CD_CONTENTS"), ""));
			recPara.setField("PAGE_CNT1",   inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",   inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",    inDto.getField("PAGE_SIZE"));
			recPara.setField("ROW_CNT2",    inDto.getField("PAGE_SIZE"));



			YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

			intRtnVal = ydSchRuleDao.getYdSchrule(recPara, outRecSet, 3);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}

				return outRecSet;

			 } // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		szMsg = "JSP-SESSION [스케줄기준 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}


	/**
	 * BookOut기준 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet BookoutMgtList(JDTORecord inDto) throws DAOException {

		int    intRtnVal    = 0;
		String szMsg        = "";
		String szMethodName = "getSlabYdSchStd";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		try {


			szMsg = "JSP-SESSION [BookOut기준 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);



			YdEqpDao  	ydEqpDao  			= new YdEqpDao();

			intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet,17);


			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}

				return outRecSet;

			 } // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		szMsg = "JSP-SESSION [BookOut기준 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}



	/**
	 * 후판정정야드 운영기준 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getpPlateYdstkRuleMgt(JDTORecord inDto) throws DAOException {

		int    intRtnVal    = 0;
		String szMsg        = "";
		String szMethodName = "getpPlateYdstkRuleMgt";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		try {


			szMsg = "JSP-SESSION [후판정정야드 운영기준 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);



			YdEqpDao  	ydEqpDao  			= new YdEqpDao();

			intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet,18);


			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}

				return outRecSet;

			 } // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		szMsg = "JSP-SESSION [후판정정야드 운영기준 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}

	/**
	 * 스케줄기준 조회 (화면:스케줄기준관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabYdSchStd_New(JDTORecord inDto) throws DAOException {

		int    intRtnVal    = 0;
		String szMsg        = "";
		String szMethodName = "getSlabYdSchStd_New";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		try {
			szMsg = "JSP-SESSION [스케줄기준 조회 (화면:스케줄기준관리)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("YD_GP",    	inDto.getField("YD_GP"));
			recPara.setField("YD_BAY_GP", 	inDto.getField("YD_BAY_GP"));
			recPara.setField("YD_SCH_CD", 	yddatautil.setDataDefault(inDto.getField("YD_SCH_CD"),   ""));
			recPara.setField("YD_EQP_ID", 	yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"),   ""));
			recPara.setField("YD_EQP_ID2", 	yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"),   ""));
			recPara.setField("CD_CONTENTS", yddatautil.setDataDefault(inDto.getField("CD_CONTENTS"), ""));
			recPara.setField("PAGE_CNT1",   inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",   inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",    inDto.getField("PAGE_SIZE"));
			recPara.setField("ROW_CNT2",    inDto.getField("PAGE_SIZE"));

			YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

			intRtnVal = ydSchRuleDao.getYdSchrule(recPara, outRecSet, 301);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}

				return outRecSet;

			 } // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		szMsg = "JSP-SESSION [스케줄기준 조회 (화면:스케줄기준관리)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}



	/**
	 * 슬라브야드 스케줄 기준관리 (수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updslabYdSchStdMgt(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;


		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();



		szMsg        = "";
		szMethodName = "updslabYdSchStdMgt";


		try {


			szMsg = "JSP-SESSION [슬라브야드 스케줄 기준관리 (수정)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){

				intRtnVal = ydSchRuleDao.updYdSchrule(inDto[x], 0);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

				} // end of if


			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [슬라브야드 스케줄 기준관리 (수정)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

	}	// end of updslabYdSchStdMgt


	/**
	 * 후판정정야드Bookout기준정보 (수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updpPlateYdBookoutMgt(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;


		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		String szRcvMsg = "";


		szMsg        = "";
		szMethodName = "updpPlateYdBookoutMgt";


		try {


			szMsg = "JSP-SESSION [후판정정야드Bookout기준정보 (수정)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			for(int x=0;x<inDto.length;x++){

				//수정할 항목 SETTING
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_PRIOR", yddatautil.setDataDefault(inDto[x].getField("YD_PRIOR"), ""));
				recPara.setField("YD_USER_ID", yddatautil.setDataDefault(inDto[x].getField("YD_USER_ID"), ""));
				recPara.setField("YD_LOC_GP", yddatautil.setDataDefault(inDto[x].getField("YD_LOC_GP"), ""));
				recPara.setField("YD_EQP_GP", yddatautil.setDataDefault(inDto[x].getField("YD_EQP_GP"), ""));

				intRtnVal = ydSchRuleDao.updBookoutRule(recPara);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

				} // end of if


				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

				} // end of if


			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [후판정정야드Bookout기준정보(수정)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

	}	// end of updslabYdSchStdMgt


	/**
	 * 후판정정야드 운영기준정보 (수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updpPlateYdStkMgt(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;


		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		String szRcvMsg = "";


		szMsg        = "";
		szMethodName = "updpPlateYdStkMgt";


		try {


			szMsg = "JSP-SESSION [후판정정야드 운영기준정보 (수정)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			for(int x=0;x<inDto.length;x++){

				//수정할 항목 SETTING
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WRK_GBN1", yddatautil.setDataDefault(inDto[x].getField("YD_WRK_GBN1"), ""));
				recPara.setField("YD_WRK_GBN2", yddatautil.setDataDefault(inDto[x].getField("YD_WRK_GBN2"), ""));
				recPara.setField("YD_WRK_GBN3", yddatautil.setDataDefault(inDto[x].getField("YD_WRK_GBN3"), ""));
				recPara.setField("YD_WRK_GBN4", yddatautil.setDataDefault(inDto[x].getField("YD_WRK_GBN4"), ""));
				recPara.setField("YD_WRK_GBN5", yddatautil.setDataDefault(inDto[x].getField("YD_WRK_GBN5"), ""));
				recPara.setField("YD_WRK_GBN6", yddatautil.setDataDefault(inDto[x].getField("YD_WRK_GBN6"), ""));
				recPara.setField("CHANGE_TERM", yddatautil.setDataDefault(inDto[x].getField("CHANGE_TERM"), ""));
				recPara.setField("YD_EQP_GP", yddatautil.setDataDefault(inDto[x].getField("YD_EQP_GP"), ""));
				recPara.setField("YD_USER_ID", yddatautil.setDataDefault(inDto[x].getField("YD_USER_ID"), ""));
				intRtnVal = ydSchRuleDao.updpPlateYdStkRule(recPara);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

				} // end of if


				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

				} // end of if


			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [후판정정야드 운영기준정보(수정)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

	}	// end of updslabYdSchStdMgt


	/**
	 * 후판정정야드 운영기준정보 (Remark수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updpPlateYdStkRemark(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;


		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		String szRcvMsg = "";


		szMsg        = "";
		szMethodName = "updpPlateYdStkRemark";


		try {


			szMsg = "JSP-SESSION [후판정정야드 운영기준정보 (Remark수정)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			for(int x=0;x<inDto.length;x++){

				//수정할 항목 SETTING
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("REMARK", yddatautil.setDataDefault(inDto[x].getField("REMARK"), ""));
				recPara.setField("YD_EQP_GP", yddatautil.setDataDefault(inDto[x].getField("YD_EQP_GP"), ""));
				recPara.setField("YD_USER_ID", yddatautil.setDataDefault(inDto[x].getField("YD_USER_ID"), ""));
				intRtnVal = ydSchRuleDao.updpPlateYdStkRemark(recPara);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

				} // end of if


				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

				} // end of if


			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [후판정정야드 운영기준정보(Remark수정)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

	}	// end of updslabYdSchStdMgt




	/**
	 * 스케줄기준 조회 - 크레인별
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabYdSchStd1(JDTORecord inDto) throws DAOException {

		int    intRtnVal    = 0;
		String szMsg        = "";
		String szMethodName = "getSlabYdSchStd1";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		try {


			szMsg = "JSP-SESSION [스케줄기준 조회 - 크레인별] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			recPara.setField("YD_GP",    	inDto.getField("YD_GP"));
			recPara.setField("YD_BAY_GP", 	inDto.getField("YD_BAY_GP"));
			recPara.setField("YD_WRK_CRN", 	inDto.getField("YD_EQP_ID"));

			//recPara.setField("YD_SCH_CD", 	yddatautil.setDataDefault(inDto.getField("YD_SCH_CD"),   ""));
			//recPara.setField("CD_CONTENTS", yddatautil.setDataDefault(inDto.getField("CD_CONTENTS"), ""));
			recPara.setField("PAGE_CNT1",   inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",   inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",    inDto.getField("PAGE_SIZE"));
			recPara.setField("ROW_CNT2",    inDto.getField("PAGE_SIZE"));



			YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

			intRtnVal = ydSchRuleDao.getYdSchrule(recPara, outRecSet, 8);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}

				return outRecSet;

			 } // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		szMsg = "JSP-SESSION [스케줄기준 조회 - 크레인별] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}	// end of getSlabYdSchStd1



	/**
	 * 슬라브야드 스케줄 기준관리 - 크레인별(수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updslabYdSchStdMgt1(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;


		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();



		szMsg        = "";
		szMethodName = "updslabYdSchStdMgt1";


		try {


			szMsg = "JSP-SESSION [슬라브야드 스케줄 기준관리 -크레인별 (수정)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){

				intRtnVal = ydSchRuleDao.updYdSchrule(inDto[x], 0);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

				} // end of if


			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [슬라브야드 스케줄 기준관리 -크레인별 (수정)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

	}	// end of updslabYdSchStdMgt1






	/**
	 * 베드의 단과 재료번호를 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */

	public JDTORecordSet getSlabYdStkBedLyrList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		String szMsg        = "";
		String szMethodName = "getSlabYdStkBedLyrList";
		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION [베드의 단과 재료번호를 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(inDto.getField("YD_STK_COL_GP"), ""));
			recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto.getField("YD_STK_BED_NO"), ""));

			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 28);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if

			outRecSet.first();

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			//if( ydb != null ) ydb.close();
		}

		szMsg = "JSP-SESSION [베드의 단과 재료번호를 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}	// end of getSlabYdStkPos


	/**
	 * 슬라브야드 메뉴얼 작업지시 편성
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void slabYdManualReq(JDTORecord[] inDto) throws DAOException {


		int intSh             = 0;
		String szMsg          = null;
		String szMethodName   = null;
		String [] strArrStlNo = null;

		JDTORecord    recPara = JDTORecordFactory.getInstance().create();
		szMsg        = "";
		szMethodName = "slabYdManualReq";
		String szOperationName = "메뉴얼 작업지시 편성";


		try {

			szMsg = "JSP-SESSION [슬라브야드 메뉴얼 작업지시 편성] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){

				//TC 미정
				strArrStlNo = inDto[x].getFieldString("STL_NO").split(";");

				//YD_SCH_CD
				recPara.setField("YD_SCH_CD", inDto[x].getField("YD_SCH_CD"));

				//YD_STK_COL_GP
				recPara.setField("YD_STK_COL_GP", inDto[x].getFieldString("FROM_BED").substring(0, 6));


				//YD_STK_BED_NO
				recPara.setField("YD_STK_BED_NO", inDto[x].getFieldString("FROM_BED").substring(6, 8));

				//YD_SH [매수]
				recPara.setField("SLAB_SH", inDto[x].getField("SLAB_SH"));

				intSh = inDto[x].getFieldInt("SLAB_SH");

				for(int Loopi=0 ; Loopi<intSh ;Loopi++){
					//재료번호
					//STL_NO []
					recPara.setField("STL_NO"+(Loopi+1), strArrStlNo[Loopi]);

					//권상 모음순서
					//YD_UP_COLL_SEQ []
					recPara.setField("YD_UP_COLL_SEQ"+(Loopi+1),""+(Loopi+1));
				}

				ydUtils.displayRecord(szOperationName, recPara);

				//내부 Process 연결
				EJBConnector ejbConn = null;
				ejbConn = new EJBConnector("default", this);
				ejbConn.trx("IssueWrkDmdFaEJB", "ydManualReq", recPara);


			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}


		szMsg = "JSP-SESSION [슬라브야드 메뉴얼 작업지시 편성] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
	}	// end of slabYdManualReq



	/**
	 * 크레인 번호 Select
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabYdCrNoComboList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		String szMsg        = "";
		String szMethodName = "getSlabYdCrNoComboList";
		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION [크레인 번호 Select] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 31);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if

			outRecSet.first();

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [크레인 번호 Select] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}	// end of getSlabYdCrNoComboList

	/**
	 * 준비이적대상재조회 팝업
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabYdRedyTranReSrcPop(JDTORecord inDto) throws DAOException {

		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		YdStockDao ydStockDao = new YdStockDao();
		String szMsg          = "";
		String szMethodName   = "getSlabYdRedyTranReSrcPop";
		int intRtnVal         = 0;

		try {

			szMsg = "JSP-SESSION [준비이적대상재조회 팝업] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(inDto.getField("YD_STK_COL_GP"), ""));
			recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto.getField("YD_STK_BED_NO"), ""));

			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 62);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if

			outRecSet.first();

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [준비이적대상재조회 팝업] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}	// end of getSlabYdRedyTranReSrcPop


	/**
	 * 스카핑 작업 관리 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getScarfingMgt(JDTORecord inDto) throws DAOException {

		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		String szMsg          = "";
		String szMethodName   = "getScarfingMgt";
		int intRtnVal         = 0;

		try {

			szMsg = "JSP-SESSION [스카핑 작업 관리 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("YD_BAY_GP", yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), ""));
			recPara.setField("YD_MTL_ITEM", yddatautil.setDataDefault(inDto.getField("YD_MTL_ITEM"), ""));
			recPara.setField("WO_MSLAB_RPR_MTD", yddatautil.setDataDefault(inDto.getField("WO_MSLAB_RPR_MTD"), ""));

			recPara.setField("PAGE_CNT1",   inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",   inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",    inDto.getField("PAGE_SIZE"));
			recPara.setField("ROW_CNT2",    inDto.getField("PAGE_SIZE"));


			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 48);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if



		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [스카핑 작업 관리 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}	// end of getScarfingMgt


	/**
	 * PICKUP BED 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getPickUpBed(JDTORecord inDto) throws DAOException {

		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		String szMsg          = "";
		String szMethodName   = "getPickUpBed";
		int intRtnVal         = 0;

		try {

			szMsg = "JSP-SESSION [PICKUP BED 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(inDto.getField("YD_STK_COL_GP"), ""));
			recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto.getField("BED_NO"), ""));



			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 49);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if



		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [PICKUP BED 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}	// end of getPickUpBed


	/**
	 * PICKUP BED  상세 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getPickUpBedDet(JDTORecord inDto) throws DAOException {

		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		String szMsg          = "";
		String szMethodName   = "getPickUpBedDet";
		int intRtnVal         = 0;

		String szSearchGp = "";

		try {
			szMsg = "JSP-SESSION [PICKUP BED  상세 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			szSearchGp = yddatautil.setDataDefault(inDto.getField("SEARCH_GP"), "");

			if("ASLAB".equals(szSearchGp)){

				recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(inDto.getField("YD_STK_COL_GP"), ""));
				recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto.getField("BED_NO"), ""));

				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 57);

			}else{
				recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(inDto.getField("YD_STK_COL_GP"), ""));
				recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto.getField("BED_NO"), ""));

				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 50);
			}

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if



		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [PICKUP BED  상세 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}	// end of getPickUpBedDet


	/**
	 * 보급요구 처리(M-Scarfing, H-Scarfing ,정정 보급)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @param szSupplyType 보급요구 종류(H_SCARF, M_SCARF ...) - 화면에서 전송되는 값임
	 * @return
	 * @throws DAOException
	 */
	public String procSupplyWrkDmd(JDTORecord[] inRecord, String szSupplyType) throws DAOException {
		//메세지
		String szMsg          = "";
		//메소드명
		String szMethodName   = "procSupplyWrkDmd";
		//리턴메세지
		String szRet = "Success";

		//유틸리티 객체
		YdDaoUtils ydDaoUtils = new YdDaoUtils();

		// DAO 객체
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();				//작업예약
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();	//작업예약재료
		YdStockDao ydStockDao = new YdStockDao();					//저장품

		//Delegate
		YdDelegate ydDelegate = new YdDelegate();

		//야드구분
		String szYD_GP = null;
		//야드 동구분
		String szYD_BAY_GP = null;
		//야드스판번호
		String szYD_SPAN_NO = "";
		//야드 스판번호
		String szPrev_YD_SPAN_NO = "";
		//적치열 구분
		String szYD_STK_COL_GP = null;
		//설비 구분
		String szYD_EQP_ID = "";
		//작업그룹을 저장할 벡터
		Vector vWrkGroup = new Vector();
		//스케줄코드
		String szYD_SCH_CD		= null;
		//스케줄코드
		String szPrev_YD_SCH_CD = "";
		//재료의 현재 폭
		long lngCurrWidth			= 0;
		//재료의 현재 중량
		long lngCurrWt				= 0;
		//누적중량
		long lngSumWt 				= 0;
		//크레인작업가능매수
		int intMtlSh				= 0;
		//크레인작업가능 폭
		long lngMaxWidth			= 0;
		//크레인 설비ID 벡터
		Vector vCrnId				= new Vector();
		//스케줄코드 벡터
		Vector vYD_SCH_CD			= new Vector();
		//크레인설비ID
		String szCrnId				= null;
		//재료번호
		String szSTL_NO				= null;
		//사용자ID
		String szUserId				= null;

		//리턴값
		int intRetVal			= 0;
		//레코드셋
		JDTORecordSet rsTemp = null;
		JDTORecordSet rsResult = null;
		//레코드
		JDTORecord recResult = null;
		JDTORecord recPara = null;

		//작업예약ID
		String szYD_WBOOK_ID	= null;
		try {


			szMsg = "JSP-SESSION [보급요구 처리(M-Scarfing, H-Scarfing ,정정 보급)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			/*
			 * [전재조건] - 야드와 동은 동일
			*/
			// ------------------------ 대상재를 그룹으로 분리 --------------------------
			for(int Loop_i = 0; Loop_i < inRecord.length; Loop_i++) {
				szMsg = (Loop_i + 1) + " : YD_STK_COL_GP : " + inRecord[Loop_i].getFieldString("YD_STK_COL_GP");
				szMsg += ", YD_STK_BED_NO : " + inRecord[Loop_i].getFieldString("YD_STK_BED_NO");
				szMsg += ", YD_STK_LYR_NO : " + inRecord[Loop_i].getFieldString("YD_STK_LYR_NO");
				szMsg += ", STL_NO : " + inRecord[Loop_i].getFieldString("STL_NO");
				szMsg += ", SUPPLY_TYPE : " + szSupplyType;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				szYD_STK_COL_GP = inRecord[Loop_i].getFieldString("YD_STK_COL_GP");
				if( Loop_i == 0 ) {
					//야드와 동구분을 구함
					szYD_GP = szYD_STK_COL_GP.substring(0, 1);
					szYD_BAY_GP = szYD_STK_COL_GP.substring(1, 2);
					szYD_EQP_ID = inRecord[Loop_i].getFieldString("YD_EQP_ID"); //야드설비구분
				}

				szYD_SPAN_NO = szYD_STK_COL_GP.substring(2, 4);

				//1. 스케줄코드 정의
				if(szSupplyType.equals("H_SCARF") && "B".equals(szYD_BAY_GP)) {
					//Hand Scarfing (B동)
					szYD_SCH_CD = "ABSB01UM";
				} else if(szSupplyType.equals("M_SCARF") && ("B".equals(szYD_BAY_GP) || "C".equals(szYD_BAY_GP))) {
					//Machine Scarfing (B, C동)
					if("B".equals(szYD_BAY_GP)){
						szYD_SCH_CD = "ABDP03UM";
					} else if("C".equals(szYD_BAY_GP)){
						szYD_SCH_CD = "ACDP01UM";
					}
				} else if(szSupplyType.equals("SHEAR") && ("A".equals(szYD_BAY_GP) || "C".equals(szYD_BAY_GP))) {
					//2차절단 (A, C동)
					if("A".equals(szYD_BAY_GP)){
						//A동 2차절단 보급 스케줄
			    		if ("AAPUP9".equals(szYD_EQP_ID)) {
							szYD_SCH_CD = "AAPU09UM";	//#2 2차절단
			    		} else if ("AAPUPA".equals(szYD_EQP_ID)) {
							szYD_SCH_CD = "AAPU10UM";	//#3 2차절단
			    		} else {
							szYD_SCH_CD = "AADP02UM";	//#1 2차절단
			    		}
			    		
			    		//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
			    		if (szYD_GP.equals("M")) {
							szYD_SCH_CD = "MADP01UM";
			    		}
					} else if("C".equals(szYD_BAY_GP)){
						szYD_SCH_CD = "ACDP01UM";
					}
				} else {
					//기타 - 동내이적
					szYD_SCH_CD = szYD_GP + szYD_BAY_GP + "YD" + szYD_SPAN_NO + "MM";
				}
/** 2012.09.12 위의 Logic으로 대체
				//1. 스판별 스케줄코드 정의 - 01, 02스판은 보급스케줄, 03, 04스판은 동내이적 스케줄로 정의
				if( szYD_SPAN_NO.equals("03") || szYD_SPAN_NO.equals("04") ) {
					//03, 04 스판
					if( szSupplyType.equals("H_SCARF")) {		//H-SCARFING
						szYD_SCH_CD = szYD_GP + szYD_BAY_GP + "SB01UM";
					}else if( szSupplyType.equals("M_SCARF")) {
						szYD_SCH_CD = szYD_GP + szYD_BAY_GP + "DP01UM";
					}else if( szSupplyType.equals("SHEAR")) {

						//2009.06.29  수정자_이현성
						//정정보급에서 A동일때는 DP02UM , C동일때는 DP01UM으로 생성해준다.

						if( "A".equals(szYD_BAY_GP)){
							szYD_SCH_CD = szYD_GP + szYD_BAY_GP + "DP02UM";
						} else if( "C".equals(szYD_BAY_GP)){
							szYD_SCH_CD = szYD_GP + szYD_BAY_GP + "DP01UM";
						}

						//////////////////////////////////////////////////////////

					}
				}else{
					//01, 02 스판 - 보급 동내이적
					szYD_SCH_CD = szYD_GP + szYD_BAY_GP + "YD" + szYD_SPAN_NO + "MM";
				}
**/
				//스케줄기준 검색 - 스케줄금지 판단, 작업크레인, 대체크레인 정보 추출
				if( !szPrev_YD_SCH_CD.equals(szYD_SCH_CD) ) {
					recResult = JDTORecordFactory.getInstance().create();
					intRetVal = YdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recResult);
					if( intRetVal == -1 ) {		//스케쥴금지
						szMsg = "스케줄["+ szYD_SCH_CD +"] 스케줄금지가 되어있습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						//스케줄금지 메세지 -
						return szMsg;//"MSG_SCH_EXN";
					}else if( intRetVal < -1 ){
						szMsg = "스케줄기준 조회시 기타에러발생";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return szMsg;//"MSG_SCH_ERR";
					}
					//크레인설비ID, 스케줄코드 저장
					szCrnId = ydDaoUtils.paraRecChkNull(recResult, "YD_WRK_CRN");
					if( !szYD_SCH_CD.equals("") && !vYD_SCH_CD.contains(szYD_SCH_CD) ) {
						vCrnId.addElement(szCrnId);
						vYD_SCH_CD.addElement(szYD_SCH_CD);
					}
				}
				inRecord[Loop_i].setField("YD_SCH_CD", szYD_SCH_CD);				//스케줄코드
				inRecord[Loop_i].setField("YD_SCH_PRIOR", ydDaoUtils.paraRecChkNull(recResult, "YD_SCH_PRIOR"));		//스케줄우선순위

				szPrev_YD_SCH_CD = szYD_SCH_CD;

				szMsg = "스케줄코드 " + szYD_SCH_CD;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				//2. 대상재를 스판번호별로 크레인사양별로 작업그룹을 생성한다.
				if( !szPrev_YD_SPAN_NO.equals(szYD_SPAN_NO) ) {			//스판번호
					rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
					vWrkGroup.add(rsTemp);
					//누적중량 초기화
					lngSumWt = 0;
					//크레인작업가능 매수
					intMtlSh = 1;

					//재료의 현재 폭
    				lngCurrWidth = ydDaoUtils.paraRecChkNullLong(inRecord[Loop_i], "YD_MTL_W");
    				//재료의 현재 중량
    				lngCurrWt = ydDaoUtils.paraRecChkNullLong(inRecord[Loop_i], "YD_MTL_WT");
    				//대상재중에서 최대폭
    				lngMaxWidth = lngCurrWidth;

    				szMsg = "스판번호별로 작업그룹 생성 : " + vWrkGroup.size() + " 그룹";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else{
					//크레인사양별로 작업그룹을 새로 생성한다.
					//재료의 현재 폭
    				lngCurrWidth = ydDaoUtils.paraRecChkNullLong(inRecord[Loop_i], "YD_MTL_W");
    				//재료의 현재 중량
    				lngCurrWt = ydDaoUtils.paraRecChkNullLong(inRecord[Loop_i], "YD_MTL_WT");
    				//누적중량
    				lngSumWt = lngSumWt + lngCurrWt;
    				//크레인작업가능 매수
    				intMtlSh++;

    				if( intMtlSh == 1 ) {
    					//대상재중에서 최대폭
    					lngMaxWidth = lngCurrWidth;
    				}else{
    					szMsg = "크레인사양별로 작업그룹 생성가능 체크 : " + intMtlSh + "매, 누적중량 - " + lngSumWt;
        				szMsg += ", 재료의 현재 폭 - " + lngCurrWidth + ", 재료의 현재 중량 - " + lngCurrWt;
        				szMsg += ", 크레인설비ID : " + ydDaoUtils.paraRecChkNull(recResult, "YD_WRK_CRN");
        				szMsg += ", 크레인 집게허용 오차 : " + ydDaoUtils.paraRecChkNullInt(recResult,  "YD_CRN_TONG_W_TOL");
        				szMsg += ", 크레인 작업가능 중량 : " + ydDaoUtils.paraRecChkNullInt(recResult,  "YD_WRK_ABLE_WT");
        				szMsg += ", 크레인 작업가능 매수 : " + ydDaoUtils.paraRecChkNullInt(recResult,  "YD_WRK_ABLE_SH");
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

    					//크레인사양비교
    					intRetVal = YdCommonUtils.chkGetCrnspec(lngCurrWidth, lngMaxWidth, lngSumWt, intMtlSh, recResult);
    					//최대폭을 지정
    					if( lngCurrWidth > lngMaxWidth ) lngMaxWidth = lngCurrWidth;
    					//크레인사양을 넘어가는 경우에 새그룹으로 편성
    					if( intRetVal == -1 || intRetVal == -2 || intRetVal == -3 ) {	//작업그룹을 새로 생성
    						rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
    						vWrkGroup.add(rsTemp);
    						//누적중량
    	    				lngSumWt = 0;
    	    				//크레인작업가능 매수
    	    				intMtlSh = 0;

    	    				szMsg = "크레인사양별로 작업그룹 생성 : " + vWrkGroup.size() + " 그룹";
    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    					}
    				}
				}
				szPrev_YD_SPAN_NO = szYD_SPAN_NO;

				rsTemp.addRecord(inRecord[Loop_i]);
			}

			szMsg = "보급요구 작업그룹 갯수 : " + vWrkGroup.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			//----------------------------- 작업요구 --------------------------------
			//3. 작업그룹별로 작업요구 등록
			for(int Loop_i = 0; Loop_i < vWrkGroup.size(); Loop_i++) {
				rsTemp = (JDTORecordSet)vWrkGroup.get(Loop_i);
				for(int Loop_j = 1; Loop_j <= rsTemp.size(); Loop_j++) {
					rsTemp.absolute(Loop_j);
					recResult = rsTemp.getRecord();
					if( Loop_j == 1 ) {
						//사용자ID
						szUserId = ydDaoUtils.paraRecChkNull(recResult, "YD_USER_ID");
						//작업예약테이블 등록
						szYD_WBOOK_ID = ydWrkbookDao.getYdWrkbookId();
						//INSERT할 항목 SET
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID);
						recPara.setField("YD_GP", 			szYD_GP);
						recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);
						recPara.setField("YD_SCH_CD", 		ydDaoUtils.paraRecChkNull(recResult, "YD_SCH_CD"));
						recPara.setField("YD_SCH_PRIOR", 	ydDaoUtils.paraRecChkNull(recResult, "YD_SCH_PRIOR"));
						recPara.setField("YD_AIM_YD_GP", 	szYD_GP);
						recPara.setField("YD_AIM_BAY_GP", 	szYD_BAY_GP);
						recPara.setField("REGISTER", 		szUserId);

						//작업예약 INSERT
						intRetVal = ydWrkbookDao.insYdWrkbook(recPara);
						if (intRetVal < 1) {
							szMsg = "작업예약 데이터 등록 중 에러";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new DAOException(szMsg);
						}
					}

					//작업예약재료 등록
					//조회항목 record 생성
					recPara = JDTORecordFactory.getInstance().create();

					recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
					recPara.setField("REGISTER", 	  ydDaoUtils.paraRecChkNull(recResult, "YD_USER_ID"));

					//리턴 recordSet 생성
					//rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
					//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
					//blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
					//if (!blnRtnVal) return;

					//레코드추출
					//rsResult.first();
					//recStkPara = rsResult.getRecord();

					szSTL_NO = ydDaoUtils.paraRecChkNull(recResult, "STL_NO");

					//다른 작업예약에 재료가 등록되어있는지 체크한다.
					//재료번호로 작업예약재료 테이블을 읽어온다.
					rsResult = JDTORecordFactory.getInstance().createRecordSet("");
					intRetVal = ydWrkbookMtlDao.getYdWrkbookmtl(recResult, rsResult, 2);
					//리턴값 메세지처리
					if (intRetVal > 0) {
						szMsg = "재료번호(" + szSTL_NO + ")에 대한 작업예약재료 데이터가 이미 등록되어 있습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(szMsg);
					}


					//재료번호
					recPara.setField("STL_NO", 		   szSTL_NO);
					//적치열구분
					recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recResult, "YD_STK_COL_GP"));
					//적치BED번호
					recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recResult, "YD_STK_BED_NO"));
					//적치단번호
					recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recResult, "YD_STK_LYR_NO"));
					//권상모음순서
					recPara.setField("YD_UP_COLL_SEQ", String.valueOf(Loop_j));

					// 작업예약재료 테이블에 등록한다.
					intRetVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

					if (intRetVal < 1) {
						szMsg = "작업예약재료 데이터 등록 중 에러";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(szMsg);
					}

					//저장품테이블에 작업예약ID와 스케줄코드 업데이트 처리
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO",				szSTL_NO);
					recPara.setField("YD_WBOOK_ID", 	   szYD_WBOOK_ID);
					recPara.setField("YD_SCH_CD", 		   ydDaoUtils.paraRecChkNull(recResult, "YD_SCH_CD"));
					recPara.setField("MODIFIER", 		   szUserId);
					intRetVal = ydStockDao.updYdStock(recPara, 0);
					if (intRetVal < 1) {
						szMsg = "저장품[" + ydDaoUtils.paraRecChkNull(recResult, "STL_NO") + "] 데이터에 작업예약ID와 스케줄코드 업데이트중 에러 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						// 예외를 발생시켜 롤백 시킴
						throw new DAOException(szMsg);
						// return;
					}
				}
			}

			/*
			 * 크레인이 작업중이면 크레인 작업지시를 보내지 않고 작업예약만 등록처리함
			 * 크레인 설비의 작업상태를 확인하는 모듈 호출
			 */
			for(int Loop_i = 0; Loop_i < vYD_SCH_CD.size(); Loop_i++ ) {
				szCrnId = (String)vCrnId.get(Loop_i);
				szYD_SCH_CD  = (String)vYD_SCH_CD.get(Loop_i);
				String szYD_EQP_STAT = YdCommonUtils.getYdEqpStat(szCrnId);

				szMsg = " 크레인의 작업상태가 " + szYD_EQP_STAT + " 이므로 " + (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_IDLE) ? "크레인 스케줄 생성 가능합니다." : "크레인 스케줄 생성 불가능합니다.");
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				if( szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_IDLE)) {			//크레인이 IDLE인 상태인 경우에만 크레인 스케줄메인을 호출
					recPara = JDTORecordFactory.getInstance().create();
					if( szYD_GP.equals("A") ) {						//C연주슬라브야드
						recPara.setField("JMS_TC_CD", "YDYDJ500");
						szMsg = "C연주슬라브야드 크레인스케줄메인 호출 ";
					}else if(szYD_GP.equals("M")) {					//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
						recPara.setField("JMS_TC_CD", "YDYDJ500");
						szMsg = "항만슬라브야드 크레인스케줄메인 호출 ";
					}else if(szYD_GP.equals("D")) {					//A후판슬라브야드 - 사용안됨
						recPara.setField("JMS_TC_CD", "YDYDJ503");
						szMsg = "A후판슬라브야드 크레인스케줄메인 호출 ";
					}else if(szYD_GP.equals("K")) {					//후판제품야드 - 사용안됨
						recPara.setField("JMS_TC_CD", "YDYDJ506");
						szMsg = "후판제품야드 크레인스케줄메인 호출 ";
					}

					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					recPara.setField("YD_SCH_CD", szYD_SCH_CD);
					recPara.setField("YD_EQP_ID", szCrnId);

					ydDelegate.sendMsg(recPara);
				}
			}

		}catch(JDTOException e) {
			throw new DAOException(e);
		}


		szMsg = "JSP-SESSION [보급요구 처리(M-Scarfing, H-Scarfing ,정정 보급)] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return szRet;
	}




	/**
	 * 설비인출보급
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String  insSlabYdTout(JDTORecord[] RecSet , JDTORecord rec) throws JDTOException {

		String szMsg			= null;
		String szMethodName 	= null;
		String szType 			= "";

		JDTORecord recPara  	= JDTORecordFactory.getInstance().create();

		EJBConnector ejbConn 	= null;

		szMsg        			= "";
		szMethodName 			= "insSlabYdTout";
		String szOperationName 	= "설비인출보급";

		/*
		 * 문제점 : 현재 각 베드 적치가능 단정보를 하드코딩함. 연주:5, 후판:4
		 */
		try {

			szMsg = "JSP-SESSION [Take Out / Carry OUt] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			szType = rec.getFieldString("TYPE");

			//TAKE- OUT
			if ("T".equals(szType)){

				String szEqp = yddatautil.setDataDefault(rec.getField("YD_STK_COL_GP"), "");
				if(szEqp.equals("")){
					return "적치열 정보가 맞지 않습니다";
				}

				szMsg = "[JSP-SESSION] TAKE- OUT : "+ szEqp ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				szEqp = szEqp.substring(0, 1);

				if(szEqp.equals("D")){

					recPara   = JDTORecordFactory.getInstance().create();
					recPara.setField("TC_CODE", 			"Y3YDL012");
					recPara.setField("YD_EQP_ID", 			rec.getField("YD_STK_COL_GP"));
					recPara.setField("YD_STK_BED_NO",       rec.getField("BED_NO"));
					recPara.setField("STL_NO",              RecSet[0].getField("STL_NO"));
					recPara.setField("YD_EMG_STK_LOC",      "미사용");
					recPara.setField("CARRY_OUT_REQ_GP",    "N");

					for(int x=1 ; x <= 4  ; x++ ){
						if(x<=  RecSet.length){
							recPara.setField("STL_NO"+x,yddatautil.setDataDefault(RecSet[RecSet.length- x].getField("STL_NO"),""));
						}else{
							recPara.setField("STL_NO"+x,"");
						}
					}
					recPara.setField("YD_STK_BED_STL_SH",    new Integer(RecSet.length));

					ejbConn = new EJBConnector("default", this);
					ejbConn.trx("RcptWrkDmdSeEJB", "procY3TakeOutCmpl", recPara);


				}else if(szEqp.equals("A") ||
						 szEqp.equals("M")) {   //항만슬라브야드 기능추가 - 2015.12.30 LeeJY

					/***************************************************************
					 * TAKE OUT  시점에 재료 공통에 머신값과 일치 하는지 판단 조건이 필요함
					 * C 연주인 경우에
					 * 각 머신중 1,2,3 머신에 들어갈수 있는것은 체크 해준다.
					 * *************************************************************/
					/***********선언부*********/
					String szColGp 		= ydDaoUtils.paraRecChkNull(rec, "YD_STK_COL_GP");
					String szMcCd  		= "";
					String szMslabMcCd 	= "";
					JDTORecordSet rsTemp= null;

					int nRtnGp = 0;
					YdStockDao ydStockDao = new YdStockDao();

					/***********Machine************************/
					if (szColGp.equals("ADPUP1") || szColGp.equals("ACPUP2") || szColGp.equals("ADPUP3") || szColGp.equals("ABPUP6")||
						szColGp.equals("ACPI01") || szColGp.equals("AAPUP4") || szColGp.equals("ACPI03") ||
						szColGp.equals("ACPUP7") || szColGp.equals("ADPI04") ||
						szColGp.equals("ADPUP8") || szColGp.equals("ACPI05") ||
						szColGp.equals("MBPU01")    //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
						)
					{
						if(szColGp.equals("ADPUP1") || szColGp.equals("ACPI01")) {
							szMcCd = "1";
						} else if(szColGp.equals("ACPUP2") || szColGp.equals("AAPUP4") || szColGp.equals("ABPUP6")) {
							szMcCd = "2";
						} else if(szColGp.equals("ADPUP3") || szColGp.equals("ACPI03")) {
							szMcCd = "3";
						} else if(szColGp.equals("ACPUP7") || szColGp.equals("ADPI04")) {
							szMcCd = "4";
						} else if(szColGp.equals("ADPUP8") || szColGp.equals("ACPI05")) {
							szMcCd = "5";
						} else if(szColGp.equals("MBPU01")) {  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
							szMcCd = "1";
						}

						/*****************************************************
						 * TAKE IN 재료 정보의 주편 공통정보에서 머신코드를 읽어 온다
						 * ***************************************************/

					    szMsg = "선택된 설비  : " +  szColGp ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

						for (int nLoop = 0 ;  nLoop< RecSet.length ; nLoop++ ){

							recPara   = JDTORecordFactory.getInstance().create();
							rsTemp    = JDTORecordFactory.getInstance().createRecordSet("");

							recPara.setField("MSLAB_NO", RecSet[nLoop].getField("STL_NO"));

							nRtnGp =  ydStockDao.getYdStock(recPara, rsTemp, 6);

							recPara   = JDTORecordFactory.getInstance().create();

							if (nRtnGp < 1 ){
								//주편정보에 없으면 비교자체를 할수 없음
								//return  YdConstant.RETN_CD_FAILURE;
								return  "공통정보에 재료가 없습니다";
							}

							rsTemp.first();
							recPara   = rsTemp.getRecord();

							szMslabMcCd = ydDaoUtils.paraRecChkNull(recPara, "CC_CCM_NO");

							if (szMslabMcCd.equals(szMcCd)){
								szMsg = "선택된 설비와 머신코드가 일치  : " + szMslabMcCd;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

								continue;
							}else{
								/*****************************************
								 * 머신번호가 맞지 않을 경우
								 ****************************************/
								szMsg = "선택된 설비와 머신코드가 다름  : " + szMslabMcCd+":::::::"+ szMcCd;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
								return  "머신번호가 맞지 않습니다";
							}
						}
					       /*************************머신 체크 END   ************************************/
					 }

					 recPara   = JDTORecordFactory.getInstance().create();
					 recPara.setField("TC_CODE", 			"C3YDL004");  //Take-Out완료
					 if(szColGp.equals("MBPU01")) {   //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
						 recPara.setField("TC_CODE", 		"E9YDL004");  //Take-Out완료
					 }
					 recPara.setField("YD_EQP_ID", 			rec.getField("YD_STK_COL_GP"));
					 recPara.setField("YD_STK_BED_NO",       rec.getField("BED_NO"));
					 recPara.setField("STL_NO",              RecSet[0].getField("STL_NO"));
					 recPara.setField("YD_EMG_STK_LOC",      "미사용");
					 recPara.setField("CARRY_OUT_REQ_GP",    "N");

					 for(int x=1 ; x <= 5  ; x++ ){
						if(x<=  RecSet.length){
							recPara.setField("STL_NO"+x,         yddatautil.setDataDefault(RecSet[RecSet.length- x].getField("STL_NO"),""));
						}else{
							recPara.setField("STL_NO"+x,         "");
						}
					 }


					 recPara.setField("YD_STK_BED_STL_SH",    new Integer(RecSet.length));

					 ejbConn = new EJBConnector("default", this);
					 ejbConn.trx("RcptWrkDmdSeEJB", "procC3TakeOutCmpl", recPara);
				 }

			//CARRY OUT
			}else if("C".equals(szType)){

				String szEqp = yddatautil.setDataDefault(rec.getField("YD_STK_COL_GP"),"");

				if(szEqp.equals("")){
					return  "적치열정보가 맞지 않습니다.";
				}

				szMsg = "[JSP-SESSION] CARRY OUT : "+ szEqp ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				szEqp = szEqp.substring(0, 1);

				//A후판 슬라브야드  CARRY-OUT
				if(szEqp.equals("D")){

				    recPara   = JDTORecordFactory.getInstance().create();
					recPara.setField("TC_CODE", 			"YDYDJ202");
					recPara.setField("YD_EQP_ID", 			rec.getField("YD_STK_COL_GP"));
					recPara.setField("YD_STK_BED_NO",       rec.getField("BED_NO"));
					recPara.setField("TAKE_OUT_STL_NO",     RecSet[0].getField("STL_NO"));
					recPara.setField("CARRY_OUT_REQ_GP",    "Y");

					for(int x=1 ; x <= 4  ; x++ ){
						if(x<=  RecSet.length){
							recPara.setField("STL_NO"+x,         yddatautil.setDataDefault(RecSet[RecSet.length- x].getField("STL_NO"),""));
						}else{
							recPara.setField("STL_NO"+x,         "");
						}
					}
					recPara.setField("YD_STK_BED_STL_SH",    new Integer(RecSet.length));

					ejbConn = new EJBConnector("default", this);
					ejbConn.trx("RcptWrkDmdSeEJB", "procY3CarryOutReq", recPara);

				//C연주 슬라브야드 CARRY - OUT 요구
				}else if(szEqp.equals("A") ||
						 szEqp.equals("M")){  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY

						recPara   = JDTORecordFactory.getInstance().create();
						recPara.setField("TC_CODE", 			"YDYDJ201");
						recPara.setField("YD_EQP_ID", 			rec.getField("YD_STK_COL_GP"));
						recPara.setField("YD_STK_BED_NO",       rec.getField("BED_NO"));
						recPara.setField("TAKE_OUT_STL_NO",     RecSet[0].getField("STL_NO"));
						recPara.setField("CARRY_OUT_REQ_GP",    "Y");

						int nMax = 0;

						nMax =  RecSet.length;

						for(int x=1 ; x <= 5  ; x++ ){
							if(x<=  nMax){
								recPara.setField("STL_NO"+x,    yddatautil.setDataDefault(RecSet[nMax- x].getField("STL_NO"),""));
							}else{
								recPara.setField("STL_NO"+x,    "");
							}
						}

						recPara.setField("YD_STK_BED_STL_SH",    new Integer(nMax));
						recPara.setField("YD_CARRY_OUT_SH",      new Integer(nMax));

						ejbConn = new EJBConnector("default", this);
						ejbConn.trx("RcptWrkDmdSeEJB", "procCCsExtSectCarryOutReq", recPara);
				}else{
					return "적치열정보가 맞지 않습니다.";
				}

		    //TAKE IN
			}else if ("TI".equals(szType)){

				String szEqp = yddatautil.setDataDefault(rec.getField("YD_STK_COL_GP"),"");

				szMsg = "[JSP-SESSION] TAKE IN : "+ szEqp ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				if(szEqp.equals("")){
					return "적치열정보가 맞지 않습니다.";
				}

				szEqp = szEqp.substring(0, 1);

				if(szEqp.equals("D")){

					recPara   = JDTORecordFactory.getInstance().create();
					recPara.setField("TC_CODE", 			"Y3YDL013");
					recPara.setField("YD_EQP_ID", 			rec.getField("YD_STK_COL_GP"));
					recPara.setField("YD_STK_BED_NO",       rec.getField("BED_NO"));
					recPara.setField("STL_NO",              RecSet[0].getField("STL_NO"));
					recPara.setField("CARRY_IN_REQ_GP",     "N");

					for(int x=1 ; x <= 4  ; x++ ){
						if(x<  RecSet.length){
							recPara.setField("STL_NO"+x,    yddatautil.setDataDefault(RecSet[RecSet.length- x].getField("STL_NO"),""));
						}else{
							recPara.setField("STL_NO"+x,    "");
						}
					}
					recPara.setField("YD_STK_BED_STL_SH",    new Integer(RecSet.length-1));

					ejbConn = new EJBConnector("default", this);
					ejbConn.trx("IssueWrkDmdSeEJB", "procY3TakeInCmpl", recPara);
				}
				else if(szEqp.equals("A") ||
						szEqp.equals("M")){  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY

					recPara   = JDTORecordFactory.getInstance().create();
					recPara.setField("TC_CODE", 			"C3YDL005");  //Take-In완료(재료정보)
					//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					if (szEqp.equals("M")){
						recPara.setField("TC_CODE", 		"E9YDL005");  //Take-In완료(재료정보)
					}
					recPara.setField("YD_EQP_ID", 			rec.getField("YD_STK_COL_GP"));
					recPara.setField("YD_STK_BED_NO",       rec.getField("BED_NO"));
					recPara.setField("STL_NO",              RecSet[0].getField("STL_NO"));
					recPara.setField("CARRY_IN_REQ_GP",     "N");

					for(int x=1 ; x <= 5  ; x++ ){
						if(x<  RecSet.length){
							recPara.setField("STL_NO"+x,         yddatautil.setDataDefault(RecSet[RecSet.length- x].getField("STL_NO"),""));
						}else{
							recPara.setField("STL_NO"+x,         "");
						}
					}
					recPara.setField("YD_STK_BED_STL_SH",    new Integer(RecSet.length-1));

					ejbConn = new EJBConnector("default", this);
					ejbConn.trx("IssueWrkDmdSeEJB", "procC3TakeInCmpl", recPara);
				}
			//CARRY IN
			}else if ("CI".equals(szType)){

				//(가열로, 스카핑, 정정) 설비로 보급
				String szEqp = yddatautil.setDataDefault(rec.getField("YD_STK_COL_GP"),"");

				szMsg = "[JSP-SESSION] CARRY IN : "+ szEqp ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				if(szEqp.equals("AAPUP4")||
				   szEqp.equals("ABPUP6")||
				   szEqp.equals("ACPUP2")){

					// 가열로 보급대
					recPara   = JDTORecordFactory.getInstance().create();
					recPara.setField("TC_CODE", 			   "YDYDJ231");
					recPara.setField("YD_EQP_ID", 			   rec.getField("YD_STK_COL_GP"));
					recPara.setField("YD_STK_BED_NO",          rec.getField("BED_NO"));
					recPara.setField("YD_AIM_RT_GP",           YdConstant.AR_WRK_WAIT_C_MILL);
					recPara.setField("YD_STK_BED_WHIO_STAT",   ""); //적치Bed입출고상태 - 필요없슴.

					ejbConn = new EJBConnector("default", this);
					ejbConn.trx("IssueWrkDmdSeEJB", "procCCsCHrSupLotComp", recPara);
				}
				else if (szEqp.equals("ACDP01") || szEqp.equals("ABDP03")){

					//스카핑
					recPara   = JDTORecordFactory.getInstance().create();
					recPara.setField("TC_CODE", 			"YDYDJ232");
					recPara.setField("YD_EQP_ID", 			rec.getField("YD_STK_COL_GP"));
					recPara.setField("YD_STK_BED_NO",       rec.getField("BED_NO"));
					recPara.setField("YD_AIM_RT_GP",        YdConstant.AR_CORRCETION_WRK_WAIT_A_BP);
					recPara.setField("YD_STK_BED_WHIO_STAT",""); //적치Bed입출고상태 - 필요없슴.

					ejbConn = new EJBConnector("default", this);
					ejbConn.trx("IssueWrkDmdSeEJB", "procCCsMScarfingSupLotComp", recPara);

				}else if(szEqp.equals("AADP02") || szEqp.equals("AAPUP9") || szEqp.equals("AAPUPA") ||
						 szEqp.equals("MADP01")  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
						){

					//정정
					recPara   = JDTORecordFactory.getInstance().create();
					recPara.setField("TC_CODE", 			"YDYDJ233");
					recPara.setField("YD_EQP_ID", 			rec.getField("YD_STK_COL_GP"));
					recPara.setField("YD_STK_BED_NO",       rec.getField("BED_NO"));
					recPara.setField("YD_AIM_RT_GP",        YdConstant.AR_CORRCETION_WRK_WAIT_A_BP);
					recPara.setField("YD_STK_BED_WHIO_STAT",""); //적치Bed입출고상태 - 필요없슴.

					ejbConn = new EJBConnector("default", this);
					ejbConn.trx("IssueWrkDmdSeEJB", "procCCsShearSupLotComp", recPara);

				}else if(szEqp.equals("DAPU01")){

					recPara   = JDTORecordFactory.getInstance().create();
					recPara.setField("TC_CODE", 			"YDYDJ237");
					recPara.setField("YD_EQP_ID", 			rec.getField("YD_STK_COL_GP"));
					recPara.setField("YD_STK_BED_NO",       rec.getField("BED_NO"));
					recPara.setField("YD_AIM_RT_GP",        YdConstant.AR_WRK_WAIT_A_MILL);
					recPara.setField("YD_STK_BED_WHIO_STAT",""); //적치Bed입출고상태 - 상태값 확인후 전송할것

					ejbConn = new EJBConnector("default", this);
					ejbConn.trx("IssueWrkDmdSeEJB", "procAPlRefurSupLotComp", recPara);
				}else if(szEqp.equals("DBPU05")){

					recPara   = JDTORecordFactory.getInstance().create();
					recPara.setField("TC_CODE", 			"YDYDJ497");
					recPara.setField("YD_EQP_ID", 			rec.getField("YD_STK_COL_GP"));
					recPara.setField("YD_STK_BED_NO",       rec.getField("BED_NO"));
					recPara.setField("YD_AIM_RT_GP",        "C5");
					recPara.setField("YD_STK_BED_WHIO_STAT",""); //적치Bed입출고상태 - 상태값 확인후 전송할것

					ejbConn = new EJBConnector("default", this);
					ejbConn.trx("IssueWrkDmdSeEJB", "procBPlRefurSupLotComp", recPara);
				}
				else{
					return "CARRY IN 이 불가능한 베드입니다";
				}
			}
			else{
				return "작업을 수행할 수 없습니다";
			}

			szMsg = "JSP-SESSION [Take Out / Carry OUt]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		return "Success";
	}	// end of insSlabYdTout



	/**
	 * 저장위치 삭제(Carry Out, Take Out )
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String  delSlabYdTout(JDTORecord[] RecSet) throws DAOException {
		int intRtnVal = 0;


		String szMethodName = null;
		String szType = "";
		String szRtnval = "";
		String szMsg = "";

		JDTORecord   recPara   = JDTORecordFactory.getInstance().create();
		JDTORecord   tmpPara   = JDTORecordFactory.getInstance().create();


		szMethodName = "delSlabYdTout";


		try {


			szMsg = "JSP-SESSION [저장위치 삭제(Carry Out, Take Out )] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			for (int i = 0 ; i < RecSet.length ;){
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP", RecSet[i].getField("YD_STK_COL_GP"));
				recPara.setField("YD_STK_BED_NO", RecSet[i].getField("YD_STK_BED_NO"));
				recPara.setField("YD_STK_LYR_NO", RecSet[i].getField("YD_STK_LYR_NO"));
				recPara.setField("STL_NO", "");
				recPara.setField("YD_STK_LYR_ACT_STAT", "E");
				recPara.setField("YD_STK_LYR_MTL_STAT", "E");

				YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

				intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);

				if ( intRtnVal < 0){

					return "Failure";

				}

				//
				//
				EJBConnector ejbConn = null;
				ejbConn = new EJBConnector("default", this);
				szRtnval = (String)ejbConn.trx("YdJspCommonSeEJB", "bedSortMgt", RecSet[0]);
				return szRtnval ;

			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {


		}

		szMsg = "JSP-SESSION [저장위치 삭제(Carry Out, Take Out )] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		return "Success";
	}	// end of delSlabYdTout








	/**
	 * 정정 보급  조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getShearMgt(JDTORecord inDto) throws DAOException {

		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		String szMsg          = "";
		String szMethodName   = "getShearMgt";
		String szOperationName = "정정 보급  조회";

		String szCallGp  = "";
		int intRtnVal         = 0;

		try {

			szMsg = "JSP-SESSION [정정 보급  조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("YD_BAY_GP", yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), ""));
			szCallGp = yddatautil.setDataDefault(inDto.getField("SEARCH_GP"), "");


			recPara.setField("PAGE_CNT1",   inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",   inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",    inDto.getField("PAGE_SIZE"));
			recPara.setField("ROW_CNT2",    inDto.getField("PAGE_SIZE"));


			if ("HCR".equals(szCallGp)){
				recPara.setField("HCR_GP", "H");

				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 53);

			}else if ("DATE".equals(szCallGp)){
				recPara.setField("HCR_GP", "");
				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 53);

			}else if ("HEAT".equals(szCallGp)){
				recPara.setField("HCR_GP", "");
				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 54);

			}else{
				return outRecSet;
			}



			ydUtils.displayRecord(szOperationName, recPara);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [정정 보급  조회] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}	// end of getShearMgt


	/**
	 * 작업예약스케줄코드 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getWBookId(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();

		String szMsg            = "";
		String szMethodName     = "getWBookId";
		int intRtnVal           = 0;
		String szOperationName = "작업예약스케줄코드 조회";

		try {

			szMsg = "JSP-SESSION [작업예약스케줄코드 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("YD_GP", yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, outRecSet, 14);
			ydUtils.displayRecord(szOperationName, recPara);
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [작업예약스케줄코드 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}	// end of getWBookId

	/**
	 * 작업예약스케줄 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabYdWBookSchList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();

		String szMsg            = "";
		String szMethodName     = "getSlabYdWBookSchList";
		String szOperationName = "작업예약스케줄 조회";
		int intRtnVal           = 0;

		try {

			szMsg = "JSP-SESSION [작업예약스케줄 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("YD_WBOOK_ID", yddatautil.setDataDefault(inDto.getField("YD_WBOOK_ID"), ""));
			intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, outRecSet, 15);
			ydUtils.displayRecord(szOperationName, recPara);
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [작업예약스케줄 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		return outRecSet;
	}	// end of getSlabYdWBookSchList

	/**
	 * 크레인스케줄 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabYdCrnSchList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();

		String szMsg            = "";
		String szMethodName     = "getSlabYdCrnSchList";
		String szOperationName  = "크레인스케줄 조회";
		int intRtnVal           = 0;

		try {

			szMsg = "JSP-SESSION [크레인스케줄 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			recPara.setField("YD_WBOOK_ID", yddatautil.setDataDefault(inDto.getField("YD_WBOOK_ID"), ""));
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 34);
			ydUtils.displayRecord(szOperationName, recPara);
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [크레인스케줄 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		return outRecSet;
	}	// end of getSlabYdCrnSchList

	/**
	 * From Bed 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabYdCrnSchFrmBed(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();

		String szMsg            = "";
		String szMethodName     = "getSlabYdCrnSchFrmBed";
		String szOperationName = "From Bed 조회";
		int intRtnVal           = 0;

		try {

			szMsg = "JSP-SESSION [From Bed 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("YD_CRN_SCH_ID",  yddatautil.setDataDefault(inDto.getField("YD_CRN_SCH_ID"), ""));
			recPara.setField("YD_STK_COL_GP",  yddatautil.setDataDefault(inDto.getField("YD_STK_COL_GP"), ""));
			recPara.setField("YD_STK_BED_NO",  yddatautil.setDataDefault(inDto.getField("YD_STK_BED_NO"), ""));
			intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 9);
			ydUtils.displayRecord(szOperationName, recPara);
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [From Bed 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}	// end of getSlabYdCrnSchFrmBed

	/**
	 * To Bed 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabYdCrnSchToBed(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();

		String szMsg            = "";
		String szMethodName     = "getSlabYdCrnSchToBed";
		String szOperationName  = "To Bed 조회";
		int intRtnVal           = 0;

		try {

			szMsg = "JSP-SESSION [To Bed 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto.getField("YD_CRN_SCH_ID"), ""));
			recPara.setField("YD_STK_COL_GP",  yddatautil.setDataDefault(inDto.getField("YD_STK_COL_GP"), ""));
			recPara.setField("YD_STK_BED_NO",  yddatautil.setDataDefault(inDto.getField("YD_STK_BED_NO"), ""));
			intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 10);

			ydUtils.displayRecord(szOperationName, recPara);
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [To Bed 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}	// end of getSlabYdCrnSchToBed

	/**
	 * 설비입고예정위치 화면 heat_no코드 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public JDTORecordSet getYdHeatCodeSearch(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getYdHeatCodeSearch";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet rSetStock = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();

		try {

			szMsg = "JSP-SESSION [설비입고예정위치 화면 heat_no코드 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("CT_PLN_WO_MC_NO",	    inDto.getField("CT_PLN_WO_MC_NO"));

			//품질점검으로 삭제
			//System.out.println(recPara);
			//야드 저장품 테이블에서 품목코드를 읽어온다

			intRtnVal = ydStockDao.getYdStock(recPara, rSetStock, 95);

			rSetStock.first();

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [설비입고예정위치 화면 heat_no코드 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return rSetStock;
	}


	/**
	 * 설비입고예정위치 화면 machine코드 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public JDTORecordSet getYdMacCodeSearch(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getYdMacCodeSearch";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet rSetStock = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();

		try {


			szMsg = "JSP-SESSION [설비입고예정위치 화면 machine코드 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("MACHINE",	    inDto.getField("MACHINE"));

			//품질점검으로 삭제
			//System.out.println(recPara);
			//야드 저장품 테이블에서 품목코드를 읽어온다

			intRtnVal = ydStockDao.getYdStock(recPara, rSetStock, 96);

			rSetStock.first();

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [설비입고예정위치 화면 machine코드 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return rSetStock;
	}


	/**
	 * 설비입고예정위치 화면 SCH_CD코드 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public JDTORecordSet getYdSchCodeSearch(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getYdSchCodeSearch";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet rSetStock = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();

		try {

			szMsg = "JSP-SESSION [설비입고예정위치 화면 SCH_CD코드 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("MACHINE",	    inDto.getField("MACHINE"));

			//품질점검으로 삭제
			//System.out.println(recPara);
			//야드 저장품 테이블에서 품목코드를 읽어온다

			intRtnVal = ydStockDao.getYdStock(recPara, rSetStock, 97);

			rSetStock.first();

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [설비입고예정위치 화면 SCH_CD코드 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return rSetStock;
	}

	/**
	 * 설비입고예정위치 화면 CRN_NAME 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public JDTORecordSet getYdCrnSearch(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getYdCrnSearch";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet rSetStock = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();

		try {
			szMsg = "JSP-SESSION [설비입고예정위치 화면 CRN_NAME 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("SCH_CODE",	    inDto.getField("SCH_CODE"));

			//품질점검으로 삭제
			//System.out.println(recPara);
			//야드 저장품 테이블에서 품목코드를 읽어온다

			intRtnVal = ydStockDao.getYdStock(recPara, rSetStock, 99);

			rSetStock.first();

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [설비입고예정위치 화면 CRN_NAME 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return rSetStock;
	}

	/**
	 *  설비입고예정위치 화면 상단 그리드 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabYdEqpInEstiLoc(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		String      szMsg        = "";
		String      szMethodName = "getSlabYdEqpInEstiLoc";
		String szOperationName = "설비입고예정위치 조회";
		YdStockDao  ydStockDao  = new YdStockDao();

		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION [설비입고예정위치 화면 상단 그리드 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("CT_PLN_WO_MC_NO",	 yddatautil.setDataDefault(inDto.getField("MACHINE"), ""));
			recPara.setField("PLAN_HEAT_NO",     yddatautil.setDataDefault(inDto.getField("HEAT_NO"), ""));

			szMsg = "[설비입고예정위치 화면 상단 그리드 조회]getSlabYdEqpInEstiLoc 파라미터 정보 보기 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			ydUtils.displayRecord(szOperationName, recPara);

			szMsg = "[설비입고예정위치 화면 상단 그리드 조회]getSlabYdEqpInEstiLoc 파라미터 정보 보기 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 98);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [설비입고예정위치 화면 상단 그리드 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getSlabYdEqpInEstiLoc


	/**
	 *  설비입고예정위치 화면 하단 왼쪽 그리드 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdSchLocSrc(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		String      szMsg        = "";
		String      szMethodName = "getYdSchLocSrc";
		YdStockDao  ydStockDao  = new YdStockDao();

		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION [설비입고예정위치 화면 하단 왼쪽 그리드 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("SCH_CODE",	    inDto.getField("SCH_CODE"));

			//품질점검으로 삭제
			//System.out.println("recPara : ==>"+ recPara);
			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 100);

			if (intRtnVal <= 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [설비입고예정위치 화면 하단 왼쪽 그리드 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getYdSchLocSrc


	/**
	 *  설비입고예정위치 화면 하단 오른쪽 그리드 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdStkLocSearch(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		String      szMsg        = "";
		String      szMethodName = "getYdStkLocSearch";
		YdStockDao  ydStockDao  = new YdStockDao();

		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION [설비입고예정위치 화면 하단 오른쪽 그리드 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("YD_STR_GTR_CD",	    inDto.getField("YD_STR_GTR_CD"));

			//품질점검으로 삭제
			//System.out.println("recPara : ==>"+ recPara);
			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 101);

			if (intRtnVal <= 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [설비입고예정위치 화면 하단 오른쪽 그리드 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getYdStkLocSearch




    /**
     * 오퍼레이션명 : 저장품 Update
     *
     * @param msgRecord, intGp(0: 대차스케줄ID)
     * @return int execution count(성공), 0:no data found, -1:dup_val_on_index, -2:parameter error, -3:execution failed
     * @throws
     */
    public int updY1YdStock (JDTORecord msgRecord, int intGp){
    	YdStockDao ydStockDao = new YdStockDao();

    	int intRtnVal 			= 0 ;
        String szMsg            = "";
        String szMethodName     = "updY1YdStock";

        try{

        	szMsg = "JSP-SESSION [저장품 Update] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

        	intRtnVal = ydStockDao.updPtComm_LOC(msgRecord, intGp);
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
            ydUtils.putLog(szSessionName, szMethodName, "Error : "+ e.getLocalizedMessage(), YdConstant.ERROR);
        }//end of try~catch

        szMsg = "JSP-SESSION [저장품 Update] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

        return intRtnVal ;

    }//end of updY1YdStock()

    
    /**
	 * 목표행선/ 목표동 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String updAimFix(JDTORecord [] inDto) throws DAOException {


		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="updAimFix";
		String szRcvMsg = "";


		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdStockDao ydStockDao = new YdStockDao();

		try {

			 szMsg = "JSP-SESSION [목표행선/ 목표동 수정] 시작";
			 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//메뉴 페이지 목록을 수정한다.
			for(int x=0;x<inDto.length;x++){


				//수정할 항목 SETTING
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", yddatautil.setDataDefault(inDto[x].getField("STL_NO"), ""));
				recPara.setField("YD_AIM_RT_GP", yddatautil.setDataDefault(inDto[x].getField("YD_AIM_RT_GP"), ""));
				recPara.setField("YD_AIM_BAY_GP", yddatautil.setDataDefault(inDto[x].getField("YD_AIM_BAY_GP"), ""));
				intRtnVal = ydStockDao.updYdStock(recPara, 0);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

					szRcvMsg = "수정시DAO ERROR 발생";
					return  szRcvMsg;

				} // end of if

			}

			 szMsg = "JSP-SESSION [목표행선/ 목표동 수정] 끝";
			 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			return YdConstant.RETN_CD_SUCCESS;


		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}


	}	// end of updAimFix


	/**
	 *  크레인별 배차기준조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCarAsgnStdByCrn(JDTORecord inDto) throws DAOException {
		/*
		 * 업무기준 : 1. 야드구분으로 설비테이블로부터 배차기준정보를 조회하여 반환
		 */
		JDTORecord       recPara        = null;
		JDTORecordSet    outRecSet      = null;

		String		szOperationName		= "크레인별 배차기준조회";
		String      szLogMsg        		= "";
		String      szMethodName 		= "getCarAsgnStdByCrn";

		YdEqpDao  	ydEqpDao  			= new YdEqpDao();

		String		szYD_GP				 = null;

		int intRtnVal = 0;

		try {

			szLogMsg = "JSP-SESSION [크레인별 배차기준조회] 시작";
			 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);


			szYD_GP = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");

			szLogMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작  - 야드구분 : " + szYD_GP;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
		    recPara.setField("YD_GP",     szYD_GP);
			intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 9);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szLogMsg = "["+szOperationName+"] 조회 시 오류발생[1] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				} else {
					szLogMsg = "["+szOperationName+"] 조회 시 오류발생[2] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if

			szLogMsg = "[Jsp Session : "+szOperationName+"] 메소드 끝 - 조회 건수 : " + outRecSet.size();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			szLogMsg = "["+szOperationName+"] 예외발생 - 메세지 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szLogMsg = "JSP-SESSION [크레인별 배차기준조회] 끝";
		 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getCarAsgnStdByCrn



	/**
	 *  후판정정야드 BookOut 기준조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getBookoutMgt() throws DAOException {
		/*
		 * 업무기준 : 1. 야드구분으로 설비테이블로부터 배차기준정보를 조회하여 반환
		 */
		JDTORecord       recPara        = null;
		JDTORecordSet    outRecSet      = null;

		String		szOperationName		= "BookOut 기준조회";
		String      szLogMsg        		= "";
		String      szMethodName 		= "getBookoutMgt";

		YdEqpDao  	ydEqpDao  			= new YdEqpDao();


		int intRtnVal = 0;

		try {

			szLogMsg = "JSP-SESSION [BookOut 기준조회] 시작";
			 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);


			szLogMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet,17);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szLogMsg = "["+szOperationName+"] 조회 시 오류발생[1] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				} else {
					szLogMsg = "["+szOperationName+"] 조회 시 오류발생[2] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if

			szLogMsg = "[Jsp Session : "+szOperationName+"] 메소드 끝 - 조회 건수 : " + outRecSet.size();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			szLogMsg = "["+szOperationName+"] 예외발생 - 메세지 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szLogMsg = "JSP-SESSION [크레인별 배차기준조회] 끝";
		 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getBookoutMgt


	/**
	 *  후판정정야드 BookOut 기준조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getpPlateYdStkMgt() throws DAOException {
		/*
		 * 업무기준 : 1. 야드구분으로 설비테이블로부터 배차기준정보를 조회하여 반환
		 */
		JDTORecord       recPara        = null;
		JDTORecordSet    outRecSet      = null;

		String		szOperationName		= "후판정정야드 운영기준관리 기준조회";
		String      szLogMsg        		= "";
		String      szMethodName 		= "getpPlateYdStkMgt";

		YdEqpDao  	ydEqpDao  			= new YdEqpDao();


		int intRtnVal = 0;

		try {

			szLogMsg = "JSP-SESSION [후판정정야드 운영기준관리조회] 시작";
			 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);


			szLogMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet,18);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szLogMsg = "["+szOperationName+"] 조회 시 오류발생[1] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				} else {
					szLogMsg = "["+szOperationName+"] 조회 시 오류발생[2] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if

			szLogMsg = "[Jsp Session : "+szOperationName+"] 메소드 끝 - 조회 건수 : " + outRecSet.size();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			szLogMsg = "["+szOperationName+"] 예외발생 - 메세지 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szLogMsg = "JSP-SESSION [후판정정야드 운영기준관리조회] 끝";
		 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getpPlateYdStkMgt


	/**
	 * 크레인별 배차기준수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String uptCarAsgnStdByCrn(JDTORecord[] inDto) throws DAOException {
		/*
		 * 업무기준 : 1. 그리드로 넘겨진 배차기준 정보를 설비테이블에 수정 처리
		 *
		 * 수정자 : 임춘수
		 * 수정일 : 2009.10.13
		 */
		//JDTO변수 정의
		JDTORecord       recPara        = null;
		//DAO 변수 정의
		YdEqpDao	ydEqpDao	= new YdEqpDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "uptCarAsgnStdByCrn";
		String		szOperationName		= "크레인별 배차기준수정";
		String		szRtnMsg			= YdConstant.RETN_CD_SUCCESS;
		//로컬변수 정의
	    int		intLOT_SH				= 0;
		String		szYD_EQP_ID	= null;
		String		szYD_CURR_BAY_GP	= null;
		String		szYD_CRN_USE_SEQ	= null;
		String		szYD_CRN_CONT_CARASGN_CNT	= null;
		String		szYD_CRN_CONT_CARASGN_WR	= null;
		String		szUserId			= null;
		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION [크레인별 배차기준수정] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			intLOT_SH = inDto.length;
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 - 수정할 배차기준 정보 건수["+intLOT_SH+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			for(int i = 0; i < inDto.length; i++ ) {

				szYD_EQP_ID  					= ydDaoUtils.paraRecChkNull(inDto[i], "YD_EQP_ID");
				szYD_CURR_BAY_GP  				= ydDaoUtils.paraRecChkNull(inDto[i], "YD_CURR_BAY_GP");
				szYD_CRN_USE_SEQ  				= ydDaoUtils.paraRecChkNull(inDto[i], "YD_CRN_USE_SEQ");
				szYD_CRN_CONT_CARASGN_CNT  		= ydDaoUtils.paraRecChkNull(inDto[i], "YD_CRN_CONT_CARASGN_CNT");
				szYD_CRN_CONT_CARASGN_WR  		= ydDaoUtils.paraRecChkNull(inDto[i], "YD_CRN_CONT_CARASGN_WR");
				szUserId  						= ydDaoUtils.paraRecChkNull(inDto[i], "YD_USER_ID");
				szMsg = "[JSP Session : "+szOperationName+"] 설비테이블["+szYD_EQP_ID+"]의 배차기준 - 현재동["+szYD_CURR_BAY_GP+"], 배차순서["+szYD_CRN_USE_SEQ+"], 배차대수["+szYD_CRN_CONT_CARASGN_CNT+"] 수정 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_EQP_ID",   		szYD_EQP_ID);
				recPara.setField("YD_CURR_BAY_GP",   	szYD_CURR_BAY_GP);
				recPara.setField("YD_CRN_USE_SEQ",   	szYD_CRN_USE_SEQ);
				recPara.setField("YD_CRN_CONT_CARASGN_CNT",   	szYD_CRN_CONT_CARASGN_CNT);
				recPara.setField("YD_CRN_CONT_CARASGN_WR",   	szYD_CRN_CONT_CARASGN_WR);
				//recPara.setField("MODIFIER",   			szUserId);
				//배차기준 수정
				intRtnVal =  ydEqpDao.updYdEqp(recPara, 0);
				szMsg = "[JSP Session : "+szOperationName+"] 설비ID["+szYD_EQP_ID+"] 수정 성공 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			szMsg = "[JSP Session : "+szOperationName+"] 설비ID 수정 성공 - 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [크레인별 배차기준수정] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return szRtnMsg;
	}//end of uptCarAsgnStdByCrn

	/**
	 *  통합야드 이송재료 LIST
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabTotYdTransMtlList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		String		szOperationName		= "통합야드 이송재료 LIST";
		String      szMsg        		= "";
		String      szMethodName 		= "getSlabTotYdTransMtlList";
		YdStockDao  ydStockDao  		= new YdStockDao();

	    String      szYdGp       		="";





		String szInOutGp = "";
		String szGaeSoCd = "";
		String szState   = "";

		String szSPos    = "";
		String szAPos    = "";


		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION [통합야드 이송재료 LIST] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			szInOutGp = ydDaoUtils.paraRecChkNull(inDto, "IN_OUT_GP");
			szGaeSoCd = ydDaoUtils.paraRecChkNull(inDto, "WLOC_CD");
			szState   = ydDaoUtils.paraRecChkNull(inDto, "WO_STATE");


			szYdGp    = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");

			if(szInOutGp.equals("1")){ //입고일 경우
				szSPos = szGaeSoCd;

				if(szYdGp.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)){
					szAPos = "DKY21";

				}else if(szYdGp.equals(YdConstant.YD_GP_INTGR_YARD)){
					szAPos = "DJY25";
				}else{
					szAPos = "DJY25";
				}


			}else{ //출고일경우


				if(szYdGp.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)){
					szAPos = "DKY21";

				}else if(szYdGp.equals(YdConstant.YD_GP_INTGR_YARD)){
					szSPos = "DJY25";
				}else{
					szSPos = "DJY25";
				}

				szAPos = szGaeSoCd;
			}


		    recPara.setField("WO_STATE",     szState);
		    recPara.setField("SPOS_WLOC_CD", szSPos);
		    recPara.setField("ARR_WLOC_CD",  szAPos);
		    recPara.setField("DATE_FROM",    ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM"));
		    recPara.setField("DATE_TO",      ydDaoUtils.paraRecChkNull(inDto, "DATE_TO"));
			recPara.setField("PAGE_CNT",            inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT",             inDto.getField("ROWCOUNT"));

			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 123);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "["+szOperationName+"] 조회 시 오류발생1 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "["+szOperationName+"] 조회 시 오류발생2 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if





		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			szMsg = "["+szOperationName+"] 예외발생 - 메세지 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [통합야드 이송재료 LIST] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getSlabTotYdTransMtlList

	/**
	 * 준비스케줄LIST(크레인별)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getYdPrepSchListByCrn(JDTORecord inDto) throws DAOException {
		/*
		 * 업무기준 : 준비스케줄테이블에서 준비스케줄ID ASC 를 조회
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		//JDTO변수 정의
		JDTORecord       recPara        = null;
		JDTORecordSet    outRecSet      = null;
		//DAO 변수 정의
		YdPrepSchDao  ydPrepSchDao  		= new YdPrepSchDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "getYdPrepSchListByCrn";
		String		szOperationName		= "준비스케줄LIST(크레인별)";
		//로컬변수 정의
	    //String		szARR_WLOC_CD		= null;
		//String		szDATE_FROM			= null;
		//String 		szDATE_TO			= null;
		String 		szYD_DONG_GP		= null;
		String		szYD_GP				= null;
		String		szYD_WRK_PLAN_CRN	= null;
		String		szYD_PREP_WK_ST		= null;
		String		szCAR_GP			= null;
		String		szSPOS_WLOC_CD		= null;

		int intRtnVal = 0;

		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 : 파라미터 확인 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			ydUtils.displayRecord(szOperationName, inDto);

			szYD_GP = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			szYD_DONG_GP = ydDaoUtils.paraRecChkNull(inDto, "YD_DONG_GP");
			szYD_WRK_PLAN_CRN = ydDaoUtils.paraRecChkNull(inDto, "YD_WRK_PLAN_CRN");
			szCAR_GP = ydDaoUtils.paraRecChkNull(inDto, "CAR_GP");
			szYD_PREP_WK_ST	= "L";
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(inDto, "SPOS_WLOC_CD");
			//szDATE_FROM   = ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM");
			//szDATE_TO   = ydDaoUtils.paraRecChkNull(inDto, "DATE_TO");


			outRecSet      = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara        = JDTORecordFactory.getInstance().create();
			recPara.setField("SPOS_WLOC_CD",     		szSPOS_WLOC_CD);
		    recPara.setField("YD_GP",     		szYD_GP);
		    recPara.setField("YD_SCH_CD",  		szYD_DONG_GP);
		    recPara.setField("YD_WRK_PLAN_CRN", szYD_WRK_PLAN_CRN);
		    recPara.setField("YD_PREP_WK_ST",   szYD_PREP_WK_ST);
		    recPara.setField("CAR_GP",   		szCAR_GP);
			recPara.setField("PAGE_NO",      	inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT",      	inDto.getField("ROWCOUNT"));
			recPara.setField("TO_ARR_GP",    	inDto.getField("TO_ARR_GP"));
			//com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschNWordCancelListByCrnPage
			intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 10);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session : "+szOperationName+"] routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "[JSP Session : "+szOperationName+"] parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if





		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [준비스케줄LIST(크레인별)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getYdPrepSchListByCrn


	/**
	 * 준비스케줄LIST
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getYdPrepSchList(JDTORecord inDto) throws DAOException {
		/*
		 * 업무기준 : 준비스케줄테이블에서 준비스케줄ID ASC 를 조회
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		//JDTO변수 정의
		JDTORecord       recPara        = null;
		JDTORecordSet    outRecSet      = null;
		//DAO 변수 정의
		YdPrepSchDao  ydPrepSchDao  		= new YdPrepSchDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "getYdPrepSchList";
		String		szOperationName		= "준비스케줄LIST";
		//로컬변수 정의
	    String		szYD_AIM_RT_GP		= null;
		String		szDATE_FROM			= null;
		String 		szDATE_TO			= null;
		String 		szYD_DONG_GP		= null;
		String		szYD_GP				= null;
		String		szYD_PREP_WK_ST		= null;
		String		szQueryType			= null;
		String		szYD_SCH_CD			= null;
		String		szYD_WRK_PLAN_CRN	= null;
		String		szCAR_GP			= null;

		int intRtnVal = 0;

		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 : 파라미터 확인 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			ydUtils.displayRecord(szOperationName, inDto);

			szQueryType 		= ydDaoUtils.paraRecChkNull(inDto, "QUERY_TYPE");
			szYD_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			szYD_DONG_GP 		= ydDaoUtils.paraRecChkNull(inDto, "YD_DONG_GP");
			szDATE_FROM   		= ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM");
			szDATE_TO   		= ydDaoUtils.paraRecChkNull(inDto, "DATE_TO");
			szYD_PREP_WK_ST 	= "L";

			szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_CD");
			szYD_WRK_PLAN_CRN 	=  ydDaoUtils.paraRecChkNull(inDto, "YD_WRK_PLAN_CRN");
			szCAR_GP 			=  ydDaoUtils.paraRecChkNull(inDto, "CAR_GP");

			szYD_AIM_RT_GP		=  ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_RT_GP");

			outRecSet      = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara        = JDTORecordFactory.getInstance().create();
		    recPara.setField("YD_GP",     		szYD_GP);
		    recPara.setField("PAGE_NO",      	inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT",      	inDto.getField("ROWCOUNT"));
		    if( szQueryType.equals("SUPPLY")) {									//스카핑/정정보급LOT LIST
		    	if( szYD_SCH_CD.equals("")) {
		    		recPara.setField("SCH_SEARCH_GP",  		"1");
		    	}else{
		    		recPara.setField("SCH_SEARCH_GP",  		"2");
		    	}
		    	recPara.setField("YD_BAY_GP",  				szYD_DONG_GP);
		    	recPara.setField("YD_SCH_CD",  				szYD_SCH_CD);
			    recPara.setField("YD_PREP_WK_ST",   		"S");
			    recPara.setField("YD_WRK_PLAN_CRN",		 	szYD_WRK_PLAN_CRN);
			    recPara.setField("CAR_GP", 					szCAR_GP);
			    recPara.setField("YD_AIM_RT_GP", 			szYD_AIM_RT_GP);
			    //intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 13);
			    intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 22);			//스카핑/정정보급LOT LIST
		    }else{
		    	recPara.setField("YD_SCH_CD",  		szYD_DONG_GP);
			    recPara.setField("YD_PREP_WK_ST",   szYD_PREP_WK_ST);
			    recPara.setField("DATE_FROM",    	szDATE_FROM);
			    recPara.setField("DATE_TO",      	szDATE_TO);
			    intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 6);
		    }

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session : "+szOperationName+"] routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "[JSP Session : "+szOperationName+"] parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if





		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getYdPrepSchList

	/**
	 * 준비스케줄ID LIST - 상차LOT편성 시 선택박스 표시
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getYdPrepSchIdList(JDTORecord inDto) throws DAOException {
		/*
		 * 업무기준 : 준비스케줄테이블에서 준비스케줄ID ASC 를 조회
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		//JDTO변수 정의
		JDTORecord       recPara        = null;
		JDTORecordSet    outRecSet      = null;
		//DAO 변수 정의
		YdPrepSchDao  ydPrepSchDao  		= new YdPrepSchDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "getYdPrepSchIdList";
		String		szOperationName		= "준비스케줄ID LIST";
		//로컬변수 정의
	 //   String		szARR_WLOC_CD		= null;
		String		szYD_GP				= null;
		String 		szYD_CAR_STOP_LOC	= null;
		String		szYD_CAR_PROG_STAT	= null;
		String		szTRN_EQP_CD		= null;
		String		szCAR_GP			= null;

		int intRtnVal = 0;

		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 : 파라미터 확인 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			ydUtils.displayRecord(szOperationName, inDto);

			szYD_GP = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			szYD_CAR_STOP_LOC = ydDaoUtils.paraRecChkNull(inDto, "YD_CAR_STOP_LOC");
			szYD_CAR_PROG_STAT = ydDaoUtils.paraRecChkNull(inDto, "YD_CAR_PROG_STAT");
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(inDto, "TRN_EQP_CD");
			outRecSet      = JDTORecordFactory.getInstance().createRecordSet("YD");

			if( szTRN_EQP_CD.equals("") ) {
				szMsg = "JSP-SESSION [준비스케줄ID LIST] 운송장비코드가 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;
			}else{
				if( szTRN_EQP_CD.substring(1, 3).equals("PT") ) {
					szCAR_GP = "P";
				}else if( szTRN_EQP_CD.substring(1, 3).equals("TR") ) {
					szCAR_GP = "T";
				}else{
					szMsg = "JSP-SESSION [준비스케줄ID LIST] 운송장비코드["+szTRN_EQP_CD+"]은 이송LOT편성이 존재하지 않는 차량종류입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return outRecSet;
				}
			}


			recPara        = JDTORecordFactory.getInstance().create();
		    recPara.setField("YD_GP",     	szYD_GP);
		    if( szYD_CAR_PROG_STAT.equals(YdConstant.YD_CARLD_ARR) && !szYD_CAR_STOP_LOC.equals("") ) {
		    	recPara.setField("YD_SCH_CD",     	szYD_CAR_STOP_LOC.substring(0, 2));
		    }else{
		    	recPara.setField("YD_SCH_CD",     	"");
		    }
		    recPara.setField("CAR_GP",   szCAR_GP);
		    recPara.setField("YD_PREP_WK_ST",   "L");

			//intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 7);
		    intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 11);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session : "+szOperationName+"] routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "[JSP Session : "+szOperationName+"] parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if





		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [준비스케줄ID LIST] 끝 [Row Count : "+outRecSet.size()+" ]";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getYdPrepSchIdList

	/**
	 * 준비스케줄재료LIST - 상차LOT편성 시 사용
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getYdPrepmtlNStockByPrepSchId(JDTORecord inDto) throws DAOException {
		/*
		 * 업무기준 : 준비스케줄테이블에서 준비스케줄ID ASC 를 조회
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		//JDTO변수 정의
		JDTORecord       recPara        = null;
		JDTORecordSet    outRecSet      = null;
		//DAO 변수 정의
		YdPrepMtlDao  yPrepMtlDao  		= new YdPrepMtlDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "getYdPrepmtlNStockByPrepSchId";
		String		szOperationName		= "준비스케줄재료LIST - 상차LOT편성";
		//로컬변수 정의
	//    String		szARR_WLOC_CD		= null;
		String		szYD_PREP_SCH_ID			= null;
	//	String 		szDATE_TO			= null;
	//	String		szYD_GP				= null;


		int intRtnVal = 0;

		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 : 파라미터 확인 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			ydUtils.displayRecord(szOperationName, inDto);

			szYD_PREP_SCH_ID   = ydDaoUtils.paraRecChkNull(inDto, "YD_PREP_SCH_ID");


			outRecSet      = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara        = JDTORecordFactory.getInstance().create();
		    recPara.setField("YD_PREP_SCH_ID",    	szYD_PREP_SCH_ID);
		    intRtnVal = yPrepMtlDao.getYdPrepmtl(recPara, outRecSet, 2);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session : "+szOperationName+"] routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "[JSP Session : "+szOperationName+"] parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if





		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [준비스케줄재료LIST - 상차LOT편성] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getYdPrepmtlNStockByPrepSchId

	/**
	 * 준비스케줄재료LIST
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getYdPrepSchMtlList(JDTORecord inDto) throws DAOException {
		/*
		 * 업무기준 : 준비스케줄테이블에서 준비스케줄ID ASC 를 조회
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		//JDTO변수 정의
		JDTORecord       recPara        = null;
		JDTORecordSet    outRecSet      = null;
		//DAO 변수 정의
		YdPrepMtlDao  yPrepMtlDao  		= new YdPrepMtlDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "getYdPrepSchMtlList";
		String		szOperationName		= "준비스케줄재료LIST";
		//로컬변수 정의
	    //String		szARR_WLOC_CD		= null;
		String		szYD_PREP_SCH_ID			= null;
		//String 		szDATE_TO			= null;
		//String		szYD_GP				= null;
		String		szSPOS_WLOC_CD		= null;
		String		szQueryType			= null;

		int intRtnVal = 0;

		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 : 파라미터 확인 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			ydUtils.displayRecord(szOperationName, inDto);

			szYD_PREP_SCH_ID   = ydDaoUtils.paraRecChkNull(inDto, "YD_PREP_SCH_ID");
			szSPOS_WLOC_CD   = ydDaoUtils.paraRecChkNull(inDto, "SPOS_WLOC_CD");

			szQueryType = ydDaoUtils.paraRecChkNull(inDto, "QUERY_TYPE");

			outRecSet      = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara        = JDTORecordFactory.getInstance().create();
			recPara.setField("SPOS_WLOC_CD",    	szSPOS_WLOC_CD);
		    recPara.setField("YD_PREP_SCH_ID",    	szYD_PREP_SCH_ID);
		    //intRtnVal = yPrepMtlDao.getYdPrepmtl(recPara, outRecSet, 1);
		    if( szQueryType.equals("SUPPLY")) {
		    	//intRtnVal = yPrepMtlDao.getYdPrepmtl(recPara, outRecSet, 4);		//보급 이송LOT재료
		    	intRtnVal = yPrepMtlDao.getYdPrepmtl(recPara, outRecSet, 6);		//스카핑/정정보급LOT재료
		    }else{
		    	intRtnVal = yPrepMtlDao.getYdPrepmtl(recPara, outRecSet, 3);		//통합야드 이송LOT재료
		    }

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session : "+szOperationName+"] routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "[JSP Session : "+szOperationName+"] parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if





		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getYdPrepSchMtlList


	/**
	 * 준비스케줄과 준비재료삭제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String delYdPrepSch(JDTORecord[] inDto) throws DAOException {
		/*
		 * 업무기준 : 1. 그리드로 넘겨진 준비스케줄/준비재료 삭제
		 *
		 *
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		//JDTO변수 정의
		JDTORecord       recPara        = null;
		//DAO 변수 정의
		YdPrepMtlDao	ydPrepMtlDao	= new YdPrepMtlDao();
		YdPrepSchDao	ydPrepSchDao	= new YdPrepSchDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "delYdPrepSch";
		String		szOperationName		= "준비스케줄/준비재료삭제";
		String		szRtnMsg			= YdConstant.RETN_CD_SUCCESS;
		//로컬변수 정의
	    int		intLOT_SH				= 0;
		String		szYD_PREP_SCH_ID	= null;
		int intRtnVal = 0;

		try {
			intLOT_SH = inDto.length;
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 - 삭제할 준비스케줄 건수["+intLOT_SH+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			recPara         = JDTORecordFactory.getInstance().create();
			for(int i = 0; i < inDto.length; i++ ) {

				szYD_PREP_SCH_ID  		= ydDaoUtils.paraRecChkNull(inDto[i], "YD_PREP_SCH_ID");

				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				recPara.setField("YD_PREP_SCH_ID",   szYD_PREP_SCH_ID);
				//준비재료 삭제처리
				intRtnVal = ydPrepMtlDao.delYdPrepmtlByPrepSchId(recPara, 1);
				szMsg = "[JSP Session : "+szOperationName+"] 준비재료["+szYD_PREP_SCH_ID+"] 삭제 성공 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//준비스케줄 삭제처리
				intRtnVal =  ydPrepSchDao.delYdPrepsch(recPara);
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 성공 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 삭제 성공 - 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of delYdPrepSch

	/**
	 * 준비재료삭제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String delYdPrepMtl(JDTORecord[] inDto) throws DAOException {
		/*
		 * 업무기준 : 1. 그리드로 넘겨진 준비재료 삭제
		 *
		 *
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		//JDTO변수 정의
		JDTORecord      recPara        = null;
		JDTORecord      recTemp        = null;
		JDTORecordSet	outRecSet		= null;
		//DAO 변수 정의
		YdPrepMtlDao	ydPrepMtlDao	= new YdPrepMtlDao();
		YdPrepSchDao	ydPrepSchDao	= new YdPrepSchDao();
		YdStockDao		ydStockDao		= new YdStockDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "delYdPrepMtl";
		String		szOperationName		= "준비재료삭제";
		String		szRtnMsg			= YdConstant.RETN_CD_SUCCESS;
		//로컬변수 정의
	    int			intLOT_SH			= 0;
		String		szYD_PREP_SCH_ID	= null;
		String		szSTL_NO			= null;
		int			intYD_EQP_WRK_SH	= 0;
		int			intYD_INV_SUM_WT	= 0;
		int			intYD_MTL_WT		= 0;
		String		szYD_USER_ID		= null;
		int intRtnVal = 0;

		try {
			intLOT_SH = inDto.length;
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 - 삭제할 준비스케줄 건수["+intLOT_SH+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			szYD_PREP_SCH_ID  		= ydDaoUtils.paraRecChkNull(inDto[0], "YD_PREP_SCH_ID");
			szYD_USER_ID  		= ydDaoUtils.paraRecChkNull(inDto[0], "YD_USER_ID");

			outRecSet =  JDTORecordFactory.getInstance().createRecordSet("");
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_PREP_SCH_ID",   szYD_PREP_SCH_ID);

			intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 0);

			if( intRtnVal < 1 ) {
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 조회 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}else if( intRtnVal == 0 ) {
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 준비재료["+szSTL_NO+"]를 저장품 조회 시 재료가 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else{
				outRecSet.first();
				recTemp = outRecSet.getRecord();
				intYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNullInt(recTemp, "YD_EQP_WRK_SH");
				intYD_INV_SUM_WT = ydDaoUtils.paraRecChkNullInt(recTemp, "YD_INV_SUM_WT");
			}

			for(int i = 0; i < inDto.length; i++ ) {

				szSTL_NO		  		= ydDaoUtils.paraRecChkNull(inDto[i], "STL_NO");

				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 준비재료["+szSTL_NO+"] 삭제 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				recPara.setField("STL_NO",   szSTL_NO);

				outRecSet =  JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 0);

				if( intRtnVal < 1 ) {
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 준비재료["+szSTL_NO+"]를 저장품 조회 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					//return YdConstant.RETN_CD_FAILURE;
				}else if( intRtnVal == 0 ) {
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 준비재료["+szSTL_NO+"]를 저장품 조회 시 재료가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					//return YdConstant.RETN_CD_NOTEXIST;
				}else if( intRtnVal > 0 ) {
					outRecSet.first();
					recTemp = outRecSet.getRecord();
					intYD_MTL_WT = ydDaoUtils.paraRecChkNullInt(recTemp, "YD_MTL_WT");
				}


				intYD_INV_SUM_WT -= intYD_MTL_WT;

				//준비재료 삭제처리
				intRtnVal = ydPrepMtlDao.delYdPrepmtlByPrepSchId(recPara, 0);
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 준비재료["+szSTL_NO+"] 삭제 성공 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			}
			intYD_EQP_WRK_SH -= inDto.length;

			if( intYD_EQP_WRK_SH > 0 ) {
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 매수["+intYD_EQP_WRK_SH+"]가 0보다 크므로 수정 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_PREP_SCH_ID",   szYD_PREP_SCH_ID);
				recPara.setField("YD_EQP_WRK_SH",    "" + intYD_EQP_WRK_SH);
				recPara.setField("YD_INV_SUM_WT",    "" + intYD_INV_SUM_WT);
				recPara.setField("MODIFIER",    	 szYD_USER_ID);

				intRtnVal = ydPrepSchDao.updYdPrepsch(recPara, 0);

				if( intRtnVal == 1 ) {
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 작업매수["+intYD_EQP_WRK_SH+"], 총중량["+intYD_INV_SUM_WT+"] 수정 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else{
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 작업매수["+intYD_EQP_WRK_SH+"], 총중량["+intYD_INV_SUM_WT+"] 수정 실패";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
			}else{
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 매수["+intYD_EQP_WRK_SH+"]가 0이므로 삭제 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//준비스케줄 삭제처리
				intRtnVal =  ydPrepSchDao.delYdPrepsch(recPara);
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 성공 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}

			szMsg = "[JSP Session : "+szOperationName+"] 준비재료 삭제 성공 - 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of delYdPrepMtl

	/**
	 * 준비스케줄수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String uptYdPrepSch(JDTORecord[] inDto) throws DAOException {
		/*
		 * 업무기준 : 1. 그리드로 넘겨진 준비스케줄수정
		 *
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		//JDTO변수 정의
		JDTORecord       recPara        = null;
		//DAO 변수 정의
		YdPrepSchDao	ydPrepSchDao	= new YdPrepSchDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "uptYdPrepSch";
		String		szOperationName		= "준비스케줄수정";
		String		szRtnMsg			= YdConstant.RETN_CD_SUCCESS;
		//로컬변수 정의
	    int		intLOT_SH				= 0;
		String		szYD_PREP_SCH_ID	= null;
		String		szYD_CARASGN_SEQ	= null;
		String		szUserId			= null;
		String		szYD_WRK_PLAN_CRN	= null;
		String		szYD_SCH_CD			= null;
		String		szYD_AIM_BAY_GP		= null;
		String		szQueryType			= null;
		int intRtnVal = 0;

		try {
			intLOT_SH = inDto.length;
			szQueryType = ydDaoUtils.paraRecChkNull(inDto[0], "QUERY_TYPE");

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 - 수정할 준비스케줄 건수["+intLOT_SH+"] : 쿼리타입["+szQueryType+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			for(int i = 0; i < inDto.length; i++ ) {
				szYD_SCH_CD				= ydDaoUtils.paraRecChkNull(inDto[i], "YD_SCH_CD");
				szYD_PREP_SCH_ID  		= ydDaoUtils.paraRecChkNull(inDto[i], "YD_PREP_SCH_ID");
				szYD_AIM_BAY_GP  		= ydDaoUtils.paraRecChkNull(inDto[i], "YD_AIM_BAY_GP");
				szYD_CARASGN_SEQ  		= ydDaoUtils.paraRecChkNull(inDto[i], "YD_CARASGN_SEQ");
				szYD_WRK_PLAN_CRN  		= ydDaoUtils.paraRecChkNull(inDto[i], "YD_WRK_PLAN_CRN");
				szUserId  		= ydDaoUtils.paraRecChkNull(inDto[i], "YD_USER_ID");
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 수정 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_PREP_SCH_ID",   	szYD_PREP_SCH_ID);
				if( szQueryType.equals("SUPPLY") ) {
					recPara.setField("YD_CARASGN_SEQ",   	szYD_CARASGN_SEQ);
				}else{

					if( !szYD_WRK_PLAN_CRN.equals("") )	{
						//recPara.setField("YD_SCH_CD",   		szYD_SCH_CD.substring(0, 5) + szYD_WRK_PLAN_CRN.substring(5) + szYD_SCH_CD.substring(6));
						recPara.setField("YD_WRK_PLAN_CRN",   	szYD_WRK_PLAN_CRN);
					}

					recPara.setField("YD_AIM_BAY_GP",   	szYD_AIM_BAY_GP);
					recPara.setField("YD_CARASGN_SEQ",   	szYD_CARASGN_SEQ);

				}
				recPara.setField("MODIFIER",   			szUserId);
				//준비스케줄 수정
				intRtnVal =  ydPrepSchDao.updYdPrepsch(recPara, 0);
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 수정 성공 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 수정 성공 - 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of uptYdPrepSch

	/**
	 * 이송재료LIST
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getYdTransMtlList(JDTORecord inDto) throws DAOException {
		/*
		 * 업무기준 : 이송지시테이블, 저장품, 적치단, 슬라브공통, 주편공통, 준비스케줄, 준비재료, 작업예약, 작업예약재료, 구입슬라브정보
		 * 			 테이블을 조인해서 이송대상재를 조회
		 * 			1. 입고인 경우는 해당야드가 이송지시테이블의 착지개소코드로 등록된 경우를 조회
		 * 				1-1. 입고일 경우에는 조회조건의 동/스판/열구분은 의미가 없도록 처리
		 * 			2. 출고인 경우는 해당야드가 이송지시테이블의 발지개소코드로 등록된 경우를 조회
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		//JDTO변수 정의
		JDTORecord       recPara        = null;
		JDTORecordSet    outRecSet      = null;
		//DAO 변수 정의
		YdStockDao  ydStockDao  		= new YdStockDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "getYdTransMtlList";
		String		szOperationName		= "이송재료LIST";
		//로컬변수 정의
	    String      szYD_AIM_YD_GP		= null;
	    String      szYD_AIM_BAY_GP 	= null;
	    String 		szSPOS_WLOC_CD		= null;
		String		szARR_WLOC_CD		= null;
		String		szDATE_FROM			= null;
		String 		szDATE_TO			= null;
		String		szWO_STATE			= null;
		String		szIN_OUT_GP			= null;
		String		szYD_GP				= null;
		String		szYD_DONG_GP		= null;
		String		szYD_SPAN_GP		= null;
		String		szYD_COL_GP			= null;
		String		szYD_BED_GP			= null;
		String		szYD_STK_COL_GP		= null;
		String		szYD_AIM_RT_GP		= null;
		String 		szMAKER_NAME        = null;

		String		szRD_DATE_ALL		= null;
	    String      szPRODUCT_GP        = null;
	    String      szPRODUCT_GP1        = null;
	    String      szPRODUCT_GP2        = null;
	    String      szPRODUCT_GP3        = null;
	    String      szPRODUCT_GP4        = null;

	    String      szSLAB_RT_GP        = null;
	    String		szCR_FTMV_GP_YN		= null;
	    String 		szYD_AREA_GP        = null;
		int intRtnVal = 0;

		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 : 파라미터 확인 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			ydUtils.displayRecord(szOperationName, inDto);

			szIN_OUT_GP 			= ydDaoUtils.paraRecChkNull(inDto, "IN_OUT_GP");			//입고/출고구분
			szYD_DONG_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_DONG_GP");
			szYD_SPAN_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_SPAN_GP");
			szYD_COL_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_COL_GP");
			szYD_BED_GP				= ydDaoUtils.paraRecChkNull(inDto, "YD_BED_GP");	
			
			
			if( szIN_OUT_GP.equals("1")) {					//입고
				szARR_WLOC_CD 			= ydDaoUtils.paraRecChkNull(inDto, "SPOS_WLOC_CD");
				szSPOS_WLOC_CD 			= ydDaoUtils.paraRecChkNull(inDto, "ARR_WLOC_CD");
				szYD_STK_COL_GP 		= "";
			}else{											//출고
				szSPOS_WLOC_CD 			= ydDaoUtils.paraRecChkNull(inDto, "SPOS_WLOC_CD");
				szARR_WLOC_CD 			= ydDaoUtils.paraRecChkNull(inDto, "ARR_WLOC_CD");
				szYD_STK_COL_GP 		= szYD_DONG_GP + szYD_SPAN_GP + szYD_COL_GP;
			}

			szDATE_FROM   			= ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM");
			szDATE_TO   			= ydDaoUtils.paraRecChkNull(inDto, "DATE_TO");
			szWO_STATE   			= ydDaoUtils.paraRecChkNull(inDto, "WO_STATE");
			szYD_AIM_RT_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_RT_GP");
			szYD_AIM_YD_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_YD_GP");
			szYD_AIM_BAY_GP 		= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_BAY_GP");
			szMAKER_NAME 			= ydDaoUtils.paraRecChkNull(inDto, "MAKER_NAME");
			szYD_GP 				= ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			szPRODUCT_GP   			= ydDaoUtils.paraRecChkNull(inDto, "PRODUCT_GP");
			szSLAB_RT_GP            = ydDaoUtils.paraRecChkNull(inDto, "SLAB_RT_GP");
			szCR_FTMV_GP_YN			= ydDaoUtils.paraRecChkNull(inDto, "CR_FTMV_GP_YN");
			szYD_AREA_GP			= ydDaoUtils.paraRecChkNull(inDto, "YD_AREA_GP");

			if(szPRODUCT_GP.equals("G"))
			{
				szPRODUCT_GP1 = "G";
				szPRODUCT_GP2 = "G";
				szPRODUCT_GP3 = "G";
				szPRODUCT_GP4 = "G";
			}
			else if(szPRODUCT_GP.equals("M"))
			{
				szPRODUCT_GP1 = "M";
				szPRODUCT_GP2 = "H";
				szPRODUCT_GP3 = "J";
				szPRODUCT_GP4 = "M";
			}
			else if(szYD_GP.equals("S") && szPRODUCT_GP.equals("A")) {
				szPRODUCT_GP1 = "";
				szPRODUCT_GP2 = "";
				szPRODUCT_GP3 = "";
				szPRODUCT_GP4 = "";
			}
			else if(szPRODUCT_GP.equals("A"))
			{
				szPRODUCT_GP1 = "G";
				szPRODUCT_GP2 = "H";
				szPRODUCT_GP3 = "J";
				szPRODUCT_GP4 = "M";
			}
			

			//------------------------------------------------------------------------
			//	날짜를 전체로 조회할 것인 지를 판단하는 변수 추가
			//	수정자 : 임춘수
			//	수정일 : 2010.01.29
			//------------------------------------------------------------------------
			szRD_DATE_ALL			= ydDaoUtils.paraRecChkNull(inDto, "RD_DATE_ALL");

			if( szRD_DATE_ALL.equals("Y")) {
				szDATE_FROM				= "00000000";
				szDATE_TO				= "99999999";
			}

			//------------------------------------------------------------------------

			outRecSet      = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara        = JDTORecordFactory.getInstance().create();
		    recPara.setField("WO_STATE",     	szWO_STATE);
		    recPara.setField("SPOS_WLOC_CD", 	szSPOS_WLOC_CD);
		    recPara.setField("ARR_WLOC_CD",  	szARR_WLOC_CD);
		    recPara.setField("YD_GP",  			szYD_GP);
		    recPara.setField("DATE_FROM",    	szDATE_FROM);
		    recPara.setField("DATE_TO",      	szDATE_TO);
		    recPara.setField("YD_STK_COL_GP",    szYD_STK_COL_GP);
		    recPara.setField("YD_AIM_RT_GP",    szYD_AIM_RT_GP);
		    recPara.setField("YD_AIM_YD_GP",    szYD_AIM_YD_GP);
		    recPara.setField("YD_AIM_BAY_GP",   szYD_AIM_BAY_GP);
		    recPara.setField("MAKER_NAME",     	szMAKER_NAME);
		    recPara.setField("SLAB_RT_GP",     	szSLAB_RT_GP);
		    recPara.setField("CR_FTMV_GP_YN",   szCR_FTMV_GP_YN);
		    recPara.setField("PRODUCT_GP1",     	szPRODUCT_GP1);
		    recPara.setField("PRODUCT_GP2",     	szPRODUCT_GP2);
		    recPara.setField("PRODUCT_GP3",     	szPRODUCT_GP3);
		    recPara.setField("PRODUCT_GP4",     	szPRODUCT_GP4);
		    recPara.setField("YD_PREP_WK_ST",   "L");
			recPara.setField("PAGE_NO",      	inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT",      	inDto.getField("ROWCOUNT"));
			recPara.setField("YD_BED_GP" , szYD_BED_GP);
			recPara.setField("YD_AREA_GP" , szYD_AREA_GP);
			
			if(szIN_OUT_GP.equals("4")) {
				if(szWO_STATE.equals("1")) {
					intRtnVal = ydStockDao.getYdStock(recPara , outRecSet , 227);
				}
			}
			else {
				if( szWO_STATE.equals("1")) {										//지시
						/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdFrtoMoveOrdMtlListPage_PIDEV*/
					intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 153);
				}else if( szWO_STATE.equals("2")){									//완료
					intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 142);
				}else if( szWO_STATE.equals("3")){									//이송LOT편성
					intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 154);
				}
			}
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session : "+szOperationName+"] 오류발생[1] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "[JSP Session : "+szOperationName+"] 파라미터 오류 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if


			szMsg = "[JSP Session : "+szOperationName+"] 조회 성공 : 대상재건수[" + intRtnVal + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getYdTransMtlList

	/**
	 * 이송대상재를 준비스케줄에 등록 - 자동, 크레인 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String insYdPrepSchNCrn(JDTORecord inDto) throws DAOException {
		/*
		 * 업무기준 : 이송지시테이블, 저장품, 적치단, 슬라브공통, 주편공통, 준비스케줄, 준비재료, 작업예약, 작업예약재료, 구입슬라브정보
		 * 			 테이블을 조인해서 이송대상재를 조회해서 준비스케줄에 등록
		 * 			1. 자동/수동이송LOT편성인 지 판단
		 * 				1-1. 자동이송LOT편성이면
		 * 					1-1-0. BRE Rule에서 Pallet/Trailer에 따른 야드설비작업매수를 조회
		 * 					1-1-1. 작업예약에 차량상차스케줄로 등록된 대상재는 제외
		 * 					1-1-2. 기존의 준비스케줄에 등록된 대상재는 제외
		 * 					1-1-3. 이송지시테이블의 이송상차일자[FRTOMOVE_CARLOAD_DATE]가 등록된 대상재는 제외
		 * 					1-1-3. 동별로 준비스케줄을 분리해서 등록되도록 처리
		 *
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		//JDTO변수 정의
		JDTORecord       recPara        = null;
		JDTORecord       jdtoRcd        = null;
		JDTORecord       recTemp        = null;
		JDTORecord       recInTemp        = null;
		JDTORecordSet    outRecSet      = null;
		//DAO 변수 정의
		YdPrepMtlDao	ydPrepMtlDao	= new YdPrepMtlDao();
		YdPrepSchDao	ydPrepSchDao	= new YdPrepSchDao();
		YdStockDao  ydStockDao  		= new YdStockDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "insYdPrepSchNCrn";
		String		szOperationName		= "이송대상재-준비스케줄등록(자동)[크레인]";
		String		szRtnMsg			= YdConstant.RETN_CD_SUCCESS;
		//로컬변수 정의
	    String      szYD_AIM_YD_GP		= null;
	    String      szYD_AIM_BAY_GP 	= null;
	    String 		szSPOS_WLOC_CD		= null;
		String		szARR_WLOC_CD		= null;
		String		szDATE_FROM			= null;
		String 		szDATE_TO			= null;
		int		intFRTOMOVE_LOT_COUNT	= 0;
		int		intLOT_SH				= 0;
		int		intREAL_LOT_SH				= 0;
		String		szIN_OUT_GP			= null;
		String 		szYD_GP				= null;
		String		szYD_STK_COL_GP		= null;
		String		szPREV_YD_STK_COL_GP		= null;
		String		szYD_PREP_SCH_ID	= null;
		String		szUserId			= null;
		String		szYD_SCH_CD			= null;
		String		szSTL_NO			= null;
		String 		szYD_STK_BED_NO		= null;
		String		szYD_STK_LYR_NO 	= null;
		String		szYD_DONG_GP		= null;
		String		szYD_SPAN_GP		= null;
		String		szYD_COL_GP			= null;
		String		szYD_AIM_RT_GP		= null;
		String		szYD_EQP_ID			= null;
		String		szMAKER_NAME		= null;
		String		szCAR_GP			= null;
		long		lngYD_MTL_WT		= 0;
		long		lngYD_MTL_WT_SUM	= 0;
		boolean		bRtnVal				= false;
		boolean	bIsLoopable				= true;
		int intRtnVal = 0;
		int intRowNo					= 0;

		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 : 파라미터 확인 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			ydUtils.displayRecord(szOperationName, inDto);

			szUserId 		= ydDaoUtils.paraRecChkNull(inDto, "YD_USER_ID");
			szIN_OUT_GP 	= ydDaoUtils.paraRecChkNull(inDto, "IN_OUT_GP");
			szYD_DONG_GP 	= ydDaoUtils.paraRecChkNull(inDto, "YD_DONG_GP");
			szYD_SPAN_GP 	= ydDaoUtils.paraRecChkNull(inDto, "YD_SPAN_GP");
			szYD_COL_GP 	= ydDaoUtils.paraRecChkNull(inDto, "YD_COL_GP");
			szYD_EQP_ID		= ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_ID");			//크레인설비ID
			szCAR_GP		= ydDaoUtils.paraRecChkNull(inDto, "CAR_GP");				//차량구분

			if( szIN_OUT_GP.equals("1")) {					//입고 -- 실제적으로 호출되지 않음
				szARR_WLOC_CD 		= ydDaoUtils.paraRecChkNull(inDto, "SPOS_WLOC_CD");
				szSPOS_WLOC_CD 		= ydDaoUtils.paraRecChkNull(inDto, "ARR_WLOC_CD");
				szYD_STK_COL_GP 	= "";
			}else{											//출고
				szSPOS_WLOC_CD 		= ydDaoUtils.paraRecChkNull(inDto, "SPOS_WLOC_CD");
				szARR_WLOC_CD 		= ydDaoUtils.paraRecChkNull(inDto, "ARR_WLOC_CD");
				szYD_STK_COL_GP 	= szYD_DONG_GP + szYD_SPAN_GP + szYD_COL_GP;
			}
			szYD_AIM_RT_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_RT_GP");
			szDATE_FROM   			= ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM");
			szDATE_TO   			= ydDaoUtils.paraRecChkNull(inDto, "DATE_TO");
			//화면에서 넘겨지는 배차대수 제거 ==> 조회된 전체 대상재를 대상으로 편성 2009.10.21 임춘수 수정
			//intFRTOMOVE_LOT_COUNT   = ydDaoUtils.paraRecChkNullInt(inDto, "FRTOMOVE_LOT_COUNT");
			//화면에서 넘겨지는 매수 제거 ==> BRE Rule로 전환 2009.10.21 임춘수 수정
			//intLOT_SH   = ydDaoUtils.paraRecChkNullInt(inDto, "LOT_SH");
			szYD_AIM_YD_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_YD_GP");
			szYD_AIM_BAY_GP 		= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_BAY_GP");
			//제조사 - 임춘수 추가 2009.10.21
			szMAKER_NAME 			= ydDaoUtils.paraRecChkNull(inDto, "MAKER_NAME");
			//야드구분
			szYD_GP 				= ydDaoUtils.paraRecChkNull(inDto, "YD_GP");

			outRecSet      = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara        = JDTORecordFactory.getInstance().create();
		    recPara.setField("SPOS_WLOC_CD", 	szSPOS_WLOC_CD);
		    recPara.setField("ARR_WLOC_CD",  	szARR_WLOC_CD);
		    recPara.setField("YD_GP",  			szYD_GP);
		    recPara.setField("DATE_FROM",    	szDATE_FROM);
		    recPara.setField("DATE_TO",      	szDATE_TO);
		    recPara.setField("YD_STK_COL_GP",   szYD_STK_COL_GP);
		    recPara.setField("YD_AIM_RT_GP",    szYD_AIM_RT_GP);
		    recPara.setField("YD_AIM_YD_GP",    szYD_AIM_YD_GP);
		    recPara.setField("YD_AIM_BAY_GP",   szYD_AIM_BAY_GP);
		    recPara.setField("MAKER_NAME",      szMAKER_NAME);
		    recPara.setField("YD_PREP_WK_ST",   "L");

			//intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 138);
		    intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 158);
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session : "+szOperationName+"] 조회 시 오류발생[1] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "[JSP Session : "+szOperationName+"] 조회 시 오류발생[2] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return YdConstant.RETN_CD_FAILURE;
			} // end of if

			if( intRtnVal == 0 ) {
				return YdConstant.RETN_CD_NOTEXIST;
			}
			jdtoRcd = JDTORecordFactory.getInstance().create();
			/*
			 * 1-1-0. BRE Rule에서 Pallet/Trailer에 따른 야드설비작업매수를 조회
			 */
			//BRE Rule에서 데이터 가져오기
			if( szCAR_GP.equals("P") ) {
		    	bRtnVal = GetBreRule3.getYDB397(jdtoRcd);
		    	if( bRtnVal ) {
		    		intLOT_SH = ydDaoUtils.paraRecChkNullInt(jdtoRcd, "YD_EQP_WRK_SH");
		    		szMsg = "[JSP Session : "+szOperationName+"] 차량구분["+szCAR_GP+"]에대한 야드설비작업매수 BRE Rule 조회 성공 - 야드설비작업매수["+intLOT_SH+"] ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	}else{
		    		intLOT_SH = 6;
		    		szMsg = "[JSP Session : "+szOperationName+"] 차량구분["+szCAR_GP+"]에대한 야드설비작업매수 BRE Rule 조회 시 오류발생 - 야드설비작업매수 기본값[6] 사용";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    	}
			}else if( szCAR_GP.equals("T") ) {
				bRtnVal = GetBreRule3.getYDB398(jdtoRcd);
		    	if( bRtnVal ) {
		    		intLOT_SH = ydDaoUtils.paraRecChkNullInt(jdtoRcd, "YD_EQP_WRK_SH");
		    		szMsg = "[JSP Session : "+szOperationName+"] 차량구분["+szCAR_GP+"]에대한 야드설비작업매수 BRE Rule 조회 성공 - 야드설비작업매수["+intLOT_SH+"] ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	}else{
		    		intLOT_SH = 2;
		    		szMsg = "[JSP Session : "+szOperationName+"] 차량구분["+szCAR_GP+"]에대한 야드설비작업매수 BRE Rule 조회 시 오류발생 - 야드설비작업매수 기본값[2] 사용";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    	}
			}

			//화면에서 넘겨지는 배차대수를 대상재의 건수로 대체
			intFRTOMOVE_LOT_COUNT = outRecSet.size();
			intRowNo = 1;
			for(int i = 1; i <= intFRTOMOVE_LOT_COUNT; i++ ) {
				intREAL_LOT_SH = 0;
				lngYD_MTL_WT_SUM = 0;
				for(int j = 1; j <= intLOT_SH; j++ ) {
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 대상재 로우번호["+intRowNo+"] ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					if( outRecSet.size() < intRowNo ) {
						bIsLoopable = false;
						szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 대상재 로우번호["+intRowNo+"]가 대상재 건수["+outRecSet.size()+"]보다 큽니다. ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						break;
					}
					outRecSet.absolute(intRowNo);
					recPara = outRecSet.getRecord();
					szSTL_NO  = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
					szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
					szYD_GP = szYD_STK_COL_GP.substring(0, 1);
					szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
					szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
					szARR_WLOC_CD = ydDaoUtils.paraRecChkNull(recPara, "ARR_WLOC_CD");
					lngYD_MTL_WT = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");

					szYD_AIM_YD_GP = YdCommonUtils.getYdFromWlocCd(szARR_WLOC_CD);

					lngYD_MTL_WT_SUM += lngYD_MTL_WT;
					//szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";
					szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT0" + szYD_EQP_ID.substring(5, 6) + "UM";

					if( j == 1 ) {
						szYD_PREP_SCH_ID = ydPrepSchDao.getYdPrepschId();
						recTemp = JDTORecordFactory.getInstance().create();
						//준비스케줄 등록
						recTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
						recTemp.setField("YD_SCH_CD", szYD_SCH_CD);
						recTemp.setField("REGISTER", szUserId);
						recTemp.setField("YD_GP", szYD_GP);
						recTemp.setField("YD_PREP_WK_ST", "L");
						recTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD);
						recTemp.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP);
						recTemp.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
						recTemp.setField("YD_CARASGN_SEQ", YdConstant.YD_CARASGN_SEQ_AUTO_DEFAULT);
						//recTemp.setField("YD_EQP_WRK_SH", "" + intLOT_SH);
						recTemp.setField("YD_WRK_PLAN_CRN", szYD_EQP_ID);
						recTemp.setField("CAR_GP", szCAR_GP);

						intRtnVal = ydPrepSchDao.insYdPrepsch(recTemp);

						if( intRtnVal < 0 ) {
							szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else if( intRtnVal == 0 ) {
							szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else{
							szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"] 등록 성공 ";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}

					}else{
						if( !szYD_STK_COL_GP.substring(0, 2).equals(szPREV_YD_STK_COL_GP.substring(0, 2)) ) {
							break;
						}
					}
					//준비재료 등록
					recTemp = JDTORecordFactory.getInstance().create();
					recTemp.setField("STL_NO", szSTL_NO);
					recTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
					recTemp.setField("REGISTER", szUserId);
					recTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
					recTemp.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
					recTemp.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);

					intRtnVal = ydPrepMtlDao.insYdPrepmtl(recTemp);

					if( intRtnVal < 0 ) {
						szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+j+"]번재료["+szSTL_NO+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if( intRtnVal == 0 ) {
						szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+j+"]번재료["+szSTL_NO+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+j+"]번재료["+szSTL_NO+"] 등록 성공 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					intREAL_LOT_SH++;
					szPREV_YD_STK_COL_GP = szYD_STK_COL_GP;
					intRowNo++;
				}


				if( intREAL_LOT_SH > 0 ) {
					recInTemp = JDTORecordFactory.getInstance().create();
					//준비스케줄수정
					recInTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
					recInTemp.setField("YD_EQP_WRK_SH", "" + intREAL_LOT_SH);
					recInTemp.setField("YD_INV_SUM_WT", "" + lngYD_MTL_WT_SUM);
					intRtnVal = ydPrepSchDao.updYdPrepsch(recInTemp, 0);
				}

				if( !bIsLoopable ) break;
			}

			szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 등록 성공 - 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of insYdPrepSchNCrn

	/**
	 * 이송대상재를 준비스케줄에 등록 - 수동, 크레인 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String insYdPrepSchNCrnByManual(JDTORecord[] inDto) throws DAOException {
		/*
		 * 업무기준 : 1. 그리드로 넘겨진 대상재들을 준비스케줄에 등록
		 * 				1-1. 준비스케줄 등록
		 * 				1-2. 준비재료 등록
		 *
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		//JDTO변수 정의
		JDTORecord       recTemp        = null;
		//DAO 변수 정의
		YdPrepMtlDao	ydPrepMtlDao	= new YdPrepMtlDao();
		YdPrepSchDao	ydPrepSchDao	= new YdPrepSchDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "insYdPrepSchNCrnByManual";
		String		szOperationName		= "이송대상재-준비스케줄등록(수동)[크레인]";
		String		szRtnMsg			= YdConstant.RETN_CD_SUCCESS;
		//로컬변수 정의
		String		szYD_AIM_YD_GP		= null;
	    String      szYD_AIM_BAY_GP 	= null;
	    String		szARR_WLOC_CD		= null;
		int		intLOT_SH				= 0;
		String 		szYD_GP				= null;
		String		szYD_STK_COL_GP		= null;
		String		szYD_PREP_SCH_ID	= null;
		String		szUserId			= null;
		String		szYD_SCH_CD			= null;
		String		szSTL_NO			= null;
		String 		szYD_STK_BED_NO		= null;
		String		szYD_STK_LYR_NO 	= null;
		String		szYD_EQP_ID			= null;
		String		szCAR_GP			= null;
		String		szDATE_WORD			= null;
		
		long		lngYD_MTL_WT		= 0;
		long		lngYD_MTL_WT_SUM	= 0;

		int intRtnVal = 0;

		try {
			intLOT_SH = inDto.length;
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 - 대상재건수["+intLOT_SH+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			szUserId 		= ydDaoUtils.paraRecChkNull(inDto[0], "YD_USER_ID");
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(inDto[0], "YD_STK_COL_GP");
			szARR_WLOC_CD 	= ydDaoUtils.paraRecChkNull(inDto[0], "ARR_WLOC_CD");
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(inDto[0], "YD_AIM_BAY_GP");
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(inDto[0], "YD_AIM_YD_GP");
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(inDto[0], "YD_EQP_ID");
			szCAR_GP		= ydDaoUtils.paraRecChkNull(inDto[0], "CAR_GP");				//차량구분
			szDATE_WORD		= ydDaoUtils.paraRecChkNull(inDto[0], "DATE_WORD");			//이송예정일자  
			

			szMsg = "[JSP Session : "+szOperationName+"] 설비ID(크레인ID)["+szYD_EQP_ID+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			szYD_GP = szYD_STK_COL_GP.substring(0, 1);
			//szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";
			szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT0" + szYD_EQP_ID.substring(5, 6) + "UM";
			//준비스케줄 등록
			szYD_PREP_SCH_ID = ydPrepSchDao.getYdPrepschId();
			recTemp = JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
			recTemp.setField("YD_SCH_CD", szYD_SCH_CD);
			recTemp.setField("REGISTER", szUserId);
			recTemp.setField("YD_GP", szYD_GP);
			recTemp.setField("YD_PREP_WK_ST", "L");
			recTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD);
			recTemp.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP);
			recTemp.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
			recTemp.setField("YD_CARASGN_SEQ", YdConstant.YD_CARASGN_SEQ_MAN_DEFAULT);
			recTemp.setField("YD_EQP_WRK_SH", "" + intLOT_SH);
			recTemp.setField("YD_WRK_PLAN_CRN", szYD_EQP_ID);
			recTemp.setField("CAR_GP", szCAR_GP);

			intRtnVal = ydPrepSchDao.insYdPrepsch(recTemp);

			if( intRtnVal < 0 ) {
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}else if( intRtnVal == 0 ) {
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}else{
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 등록 성공 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}

			for(int i = 0; i < inDto.length; i++ ) {

				szSTL_NO  		= ydDaoUtils.paraRecChkNull(inDto[i], "STL_NO");
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(inDto[i], "YD_STK_COL_GP");
				szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inDto[i], "YD_STK_BED_NO");
				szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(inDto[i], "YD_STK_LYR_NO");
				lngYD_MTL_WT 	= ydDaoUtils.paraRecChkNullLong(inDto[i], "YD_MTL_WT");

				lngYD_MTL_WT_SUM += lngYD_MTL_WT;

				//준비재료 등록
				recTemp = JDTORecordFactory.getInstance().create();
				recTemp.setField("STL_NO", szSTL_NO);
				recTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
				recTemp.setField("REGISTER", szUserId);
				recTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
				recTemp.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
				recTemp.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
				recTemp.setField("FROMTOMOVE_PLN_DATE", szDATE_WORD);
				
				intRtnVal = ydPrepMtlDao.insYdPrepmtl(recTemp);

				if( intRtnVal < 0 ) {
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+(i + 1)+"]번재료["+szSTL_NO+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if( intRtnVal == 0 ) {
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+(i + 1)+"]번재료["+szSTL_NO+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+(i + 1)+"]번재료["+szSTL_NO+"] 등록 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}

			recTemp = JDTORecordFactory.getInstance().create();
			//준비스케줄수정
			recTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
			recTemp.setField("YD_INV_SUM_WT", "" + lngYD_MTL_WT_SUM);
			intRtnVal = ydPrepSchDao.updYdPrepsch(recTemp, 0);

			szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 등록 성공 - 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of insYdPrepSchNCrnByManual

	/**
	 * 이송대상재를 준비스케줄에 등록 - 자동
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String insYdPrepSch(JDTORecord inDto) throws DAOException {
		/*
		 * 업무기준 : 이송지시테이블, 저장품, 적치단, 슬라브공통, 주편공통, 준비스케줄, 준비재료, 작업예약, 작업예약재료
		 * 			 테이블을 조인해서 이송대상재를 조회해서 준비스케줄에 등록
		 * 			1. 자동/수동이송LOT편성인 지 판단
		 * 				1-1. 자동이송LOT편성이면
		 * 					1-1-1. 작업예약에 차량상차스케줄로 등록된 대상재는 제외
		 * 					1-1-2. 기존의 준비스케줄에 등록된 대상재는 제외
		 * 					1-1-3. 이송지시테이블의 이송상차일자[FRTOMOVE_CARLOAD_DATE]가 등록된 대상재는 제외
		 * 					1-1-3. 동별로 준비스케줄을 분리해서 등록되도록 처리
		 *
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		//JDTO변수 정의
		JDTORecord       recPara        = null;
		JDTORecord       recTemp        = null;
		JDTORecord       recInTemp      = null;
		JDTORecord       jdtoRcd		= null;
		JDTORecordSet    outRecSet      = null;
		//DAO 변수 정의
		YdPrepMtlDao	ydPrepMtlDao	= new YdPrepMtlDao();
		YdPrepSchDao	ydPrepSchDao	= new YdPrepSchDao();
		YdStockDao  ydStockDao  		= new YdStockDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "insYdPrepSch";
		String		szOperationName		= "이송대상재-준비스케줄등록(자동)";
		String		szRtnMsg			= YdConstant.RETN_CD_SUCCESS;
		//로컬변수 정의
	    String      szYD_AIM_YD_GP		= null;
	    String      szYD_AIM_BAY_GP 	= null;
	    String 		szSPOS_WLOC_CD		= null;
		String		szARR_WLOC_CD		= null;
		String		szDATE_FROM			= null;
		String 		szDATE_TO			= null;
		int		intFRTOMOVE_LOT_COUNT	= 0;
		int		intLOT_SH				= 0;
		int		intREAL_LOT_SH				= 0;
		String		szIN_OUT_GP			= null;
		String 		szYD_GP				= null;
		String		szYD_STK_COL_GP		= null;
		String		szPREV_YD_STK_COL_GP		= null;
		String		szYD_PREP_SCH_ID	= null;
		String		szUserId			= null;
		String		szYD_SCH_CD			= null;
		String		szSTL_NO			= null;
		String 		szYD_STK_BED_NO		= null;
		String		szYD_STK_LYR_NO 	= null;
		String		szYD_DONG_GP		= null;
		String		szYD_SPAN_GP		= null;
		String		szYD_COL_GP			= null;
		String		szYD_AIM_RT_GP		= null;
		String		szCAR_GP			= null;
		String		szMAKER_NAME		= null;
		boolean		bRtnVal				= false;
		long		lngYD_MTL_WT		= 0;
		long		lngYD_MTL_WT_SUM	= 0;

		String		szRD_DATE_ALL		= null;
		String 		szVIA_GP			= "S";
		boolean	bIsLoopable				= true;
		int intRtnVal = 0;
		int intRowNo					= 0;
		int intLOOP_CNT					= 0;

		try {
			//----------------------------------------------------------------------------------------------
			//	파라미터 확인
			//----------------------------------------------------------------------------------------------


			szMsg = "[JSP Session : "+szOperationName+"] -------------------- 메소드 시작 : 파라미터 확인 --------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			ydUtils.displayRecord(szOperationName, inDto);

			szUserId 				= ydDaoUtils.paraRecChkNull(inDto, "YD_USER_ID");
			szIN_OUT_GP 			= ydDaoUtils.paraRecChkNull(inDto, "IN_OUT_GP");			//입출고구분
			szYD_DONG_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_DONG_GP");			//야드와 동구분
			szYD_SPAN_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_SPAN_GP");			//스판구분
			szYD_COL_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_COL_GP");			//열구분

			if( szIN_OUT_GP.equals("1")) {					//입고 -- 실제적으로 호출되지 않음
				szARR_WLOC_CD 		= ydDaoUtils.paraRecChkNull(inDto, "SPOS_WLOC_CD");
				szSPOS_WLOC_CD 		= ydDaoUtils.paraRecChkNull(inDto, "ARR_WLOC_CD");
				szYD_STK_COL_GP 	= "";
			}else{											//출고
				szSPOS_WLOC_CD 		= ydDaoUtils.paraRecChkNull(inDto, "SPOS_WLOC_CD");			//발지개소코드
				szARR_WLOC_CD 		= ydDaoUtils.paraRecChkNull(inDto, "ARR_WLOC_CD");			//착지개소코드
				szYD_STK_COL_GP 	= szYD_DONG_GP + szYD_SPAN_GP + szYD_COL_GP;
			}
			szYD_AIM_RT_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_RT_GP");			//목표행선
			szDATE_FROM   			= ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM");			//시작일자
			szDATE_TO   			= ydDaoUtils.paraRecChkNull(inDto, "DATE_TO");				//종료일자
			//intFRTOMOVE_LOT_COUNT   = ydDaoUtils.paraRecChkNullInt(inDto, "FRTOMOVE_LOT_COUNT");
			//intLOT_SH   			= ydDaoUtils.paraRecChkNullInt(inDto, "LOT_SH");
			szYD_AIM_YD_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_YD_GP");			//목표야드
			szYD_AIM_BAY_GP 		= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_BAY_GP");		//목표동
			szCAR_GP				= ydDaoUtils.paraRecChkNull(inDto, "CAR_GP");				//차량구분

			//제조사 - 임춘수 추가 2009.10.21
			szMAKER_NAME 			= ydDaoUtils.paraRecChkNull(inDto, "MAKER_NAME");
			//야드구분
			szYD_GP 				= ydDaoUtils.paraRecChkNull(inDto, "YD_GP");

			//----------------------------------------------------------------------------------------------

			//------------------------------------------------------------------------
			//	날짜를 전체로 조회할 것인 지를 판단하는 변수 추가
			//	수정자 : 임춘수
			//	수정일 : 2010.01.29
			//------------------------------------------------------------------------
			szRD_DATE_ALL			= ydDaoUtils.paraRecChkNull(inDto, "RD_DATE_ALL");

			if( szRD_DATE_ALL.equals("Y")) {
				szDATE_FROM				= "00000000";
				szDATE_TO				= "99999999";
			}

			//------------------------------------------------------------------------

			outRecSet      = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara        = JDTORecordFactory.getInstance().create();
		    recPara.setField("SPOS_WLOC_CD", 	szSPOS_WLOC_CD);
		    recPara.setField("ARR_WLOC_CD",  	szARR_WLOC_CD);
		    recPara.setField("YD_GP",  			szYD_GP);
		    recPara.setField("DATE_FROM",    	szDATE_FROM);
		    recPara.setField("DATE_TO",      	szDATE_TO);
		    recPara.setField("YD_STK_COL_GP",   szYD_STK_COL_GP);
		    recPara.setField("YD_AIM_RT_GP",    szYD_AIM_RT_GP);
		    recPara.setField("YD_AIM_YD_GP",    szYD_AIM_YD_GP);
		    recPara.setField("YD_AIM_BAY_GP",   szYD_AIM_BAY_GP);
		    recPara.setField("MAKER_NAME",      szMAKER_NAME);
		    recPara.setField("YD_PREP_WK_ST",   "L");

//			outRecSet      = JDTORecordFactory.getInstance().createRecordSet("YD");
//			recPara        = JDTORecordFactory.getInstance().create();
//
//		    recPara.setField("SPOS_WLOC_CD", 	szSPOS_WLOC_CD);
//		    recPara.setField("ARR_WLOC_CD",  	szARR_WLOC_CD);
//		    recPara.setField("DATE_FROM",    	szDATE_FROM);
//		    recPara.setField("DATE_TO",      	szDATE_TO);
//		    recPara.setField("YD_STK_COL_GP",   szYD_STK_COL_GP);
//		    recPara.setField("YD_AIM_RT_GP",    szYD_AIM_RT_GP);
//		    recPara.setField("YD_AIM_YD_GP",    szYD_AIM_YD_GP);
//		    recPara.setField("YD_AIM_BAY_GP",   szYD_AIM_BAY_GP);


		    //----------------------------------------------------------------------------------------------
			//	LOT편성할 대상재 조회
			//----------------------------------------------------------------------------------------------

			szMsg = "[JSP Session : "+szOperationName+"] LOT편성할 대상재 조회 시작 - 야드구분["+szYD_GP+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdFrtoMoveOrdMtlList*/
			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 158);

			szMsg = "[JSP Session : "+szOperationName+"] LOT편성할 대상재 조회 완료 - 반환값  : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session : "+szOperationName+"] [1] LOT편성할 대상재 조회 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "[JSP Session : "+szOperationName+"] [2] LOT편성할 대상재 조회 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return YdConstant.RETN_CD_FAILURE;
			} // end of if

			if( intRtnVal == 0 ) {
				return YdConstant.RETN_CD_NOTEXIST;
			}

			//----------------------------------------------------------------------------------------------

			intFRTOMOVE_LOT_COUNT			= outRecSet.size();

			//----------------------------------------------------------------------------------------------
			//	BRE Rule에서 Pallet/Trailer에 따른 야드설비작업매수를 조회
			//----------------------------------------------------------------------------------------------

			jdtoRcd = JDTORecordFactory.getInstance().create();
			//BRE Rule에서 데이터 가져오기
			if( szCAR_GP.equals("P")||szCAR_GP.equals("Y") ) {
				if( szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD)) {				//C연주슬라브야드
					bRtnVal = GetBreRule1.getYDB193(jdtoRcd);
				}else if( szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD)) {	//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					bRtnVal = GetBreRule1.getYDB193(jdtoRcd);
				}else if( szYD_GP.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)) {	//A후판슬라브야드
					bRtnVal = GetBreRule2.getYDB296(jdtoRcd);
				}
		    	if( bRtnVal ) {
		    		intLOT_SH = ydDaoUtils.paraRecChkNullInt(jdtoRcd, "YD_EQP_WRK_SH");
		    		szMsg = "[JSP Session : "+szOperationName+"] 야드구분["+szYD_GP+"] - 차량구분["+szCAR_GP+"]에대한 야드설비작업매수 BRE Rule 조회 성공 - 야드설비작업매수["+intLOT_SH+"] ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	}else{
		    		intLOT_SH = 6;
		    		szMsg = "[JSP Session : "+szOperationName+"] 야드구분["+szYD_GP+"] - 차량구분["+szCAR_GP+"]에대한 야드설비작업매수 BRE Rule 조회 시 오류발생 - 야드설비작업매수 기본값[6] 사용";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    	}
		    //szCAR_GP- T:100ton이하 , B:100ton이상 
			}else if( szCAR_GP.equals("T")  ) {
				if( szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD)) {				//C연주슬라브야드
					bRtnVal = GetBreRule1.getYDB194(jdtoRcd);
				}else if( szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD)) {	//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					bRtnVal = GetBreRule1.getYDB194(jdtoRcd);
				}else if( szYD_GP.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)) {	//A후판슬라브야드
					bRtnVal = GetBreRule2.getYDB297(jdtoRcd);
				}
		    	if( bRtnVal ) {
		    		intLOT_SH = ydDaoUtils.paraRecChkNullInt(jdtoRcd, "YD_EQP_WRK_SH");
		    		szMsg = "[JSP Session : "+szOperationName+"] 야드구분["+szYD_GP+"] - 차량구분["+szCAR_GP+"]에대한 야드설비작업매수 BRE Rule 조회 성공 - 야드설비작업매수["+intLOT_SH+"] ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	}else{
		    		intLOT_SH = 2;
		    		szMsg = "[JSP Session : "+szOperationName+"] 야드구분["+szYD_GP+"] - 차량구분["+szCAR_GP+"]에대한 야드설비작업매수 BRE Rule 조회 시 오류발생 - 야드설비작업매수 기본값[2] 사용";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    	}
		   //szCAR_GP- T:100ton이하 , B:100ton이상 
			}else if(szCAR_GP.equals("B")){
				intLOT_SH = 3;
	    		szMsg = "[JSP Session : "+szOperationName+"] 야드구분["+szYD_GP+"] - 차량구분["+szCAR_GP+"]에대한 야드설비작업매수 BRE Rule 조회 성공 - 야드설비작업매수["+intLOT_SH+"] ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}

			//----------------------------------------------------------------------------------------------


			intLOOP_CNT				= intFRTOMOVE_LOT_COUNT	/ intLOT_SH;

			if( intFRTOMOVE_LOT_COUNT % intLOT_SH > 0 ) intLOOP_CNT++;

			intRowNo = 1;
			//for(int i = 1; i <= intFRTOMOVE_LOT_COUNT; i++ ) {
			for(int i = 1; i <= intLOOP_CNT; i++ ) {
				intREAL_LOT_SH 			= 0;
				lngYD_MTL_WT_SUM 		= 0;
				for(int j = 1; j <= intLOT_SH; j++ ) {
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 대상재 로우번호["+intRowNo+"] ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					if( outRecSet.size() < intRowNo ) {
						bIsLoopable = false;
						szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 대상재 로우번호["+intRowNo+"]가 대상재 건수["+outRecSet.size()+"]보다 큽니다. ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						break;
					}
					outRecSet.absolute(intRowNo);
					recPara = outRecSet.getRecord();

					if(szVIA_GP.equals("S")){
						szVIA_GP  			= ydDaoUtils.paraRecChkNull(recPara, "VIA_GP");
					}

					ydUtils.putLog(szSessionName, szMethodName,intRowNo+"::::"+ szVIA_GP+"VIA_GP:"+ydDaoUtils.paraRecChkNull(recPara, "VIA_GP"), YdConstant.DEBUG);

					if(!szVIA_GP.equals(ydDaoUtils.paraRecChkNull(recPara, "VIA_GP"))){

						szMsg = "[JSP Session : "+szOperationName+"] 경유대상재와 함께 이송LOT편성을 할 수 없습니다. ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						this.m_ctx.setRollbackOnly();
						return szMsg;
					}

					intRowNo++;
				}


			}




			//----------------------------------------------------------------------------------------------
			//	준비스케줄 등록
			//----------------------------------------------------------------------------------------------

			intLOOP_CNT				= intFRTOMOVE_LOT_COUNT	/ intLOT_SH;

			if( intFRTOMOVE_LOT_COUNT % intLOT_SH > 0 ) intLOOP_CNT++;

			intRowNo = 1;
			//for(int i = 1; i <= intFRTOMOVE_LOT_COUNT; i++ ) {
			for(int i = 1; i <= intLOOP_CNT; i++ ) {
				intREAL_LOT_SH 			= 0;
				lngYD_MTL_WT_SUM 		= 0;
				for(int j = 1; j <= intLOT_SH; j++ ) {
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 대상재 로우번호["+intRowNo+"] ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					if( outRecSet.size() < intRowNo ) {
						bIsLoopable = false;
						szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 대상재 로우번호["+intRowNo+"]가 대상재 건수["+outRecSet.size()+"]보다 큽니다. ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						break;
					}
					outRecSet.absolute(intRowNo);
					recPara = outRecSet.getRecord();
					szSTL_NO  			= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
					szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
					szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
					szYD_STK_LYR_NO 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
					szYD_GP 			= szYD_STK_COL_GP.substring(0, 1);
					szYD_AIM_BAY_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
					szYD_AIM_YD_GP 		= ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
					szARR_WLOC_CD 		= ydDaoUtils.paraRecChkNull(recPara, "ARR_WLOC_CD");
					lngYD_MTL_WT 		= ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");


//					if(szVIA_GP.equals("S")){
//						szVIA_GP  			= ydDaoUtils.paraRecChkNull(recPara, "VIA_GP");
//					}
//
//					if(!szVIA_GP.equals(ydDaoUtils.paraRecChkNull(recPara, "VIA_GP"))){
//
//						szMsg = "[JSP Session : "+szOperationName+"] 경유대상재와 함께 이송LOT편성을 할 수 없습니다. ";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//						this.m_ctx.setRollbackOnly();
//						return szMsg;
//					}

					lngYD_MTL_WT_SUM += lngYD_MTL_WT;

					szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";
					if( j == 1 ) {
						szYD_PREP_SCH_ID = ydPrepSchDao.getYdPrepschId();
						recTemp = JDTORecordFactory.getInstance().create();
						//준비스케줄 등록
						recTemp.setField("YD_PREP_SCH_ID", 			szYD_PREP_SCH_ID);
						recTemp.setField("YD_SCH_CD", 				szYD_SCH_CD);
						recTemp.setField("REGISTER", 				szUserId);
						recTemp.setField("YD_GP", 					szYD_GP);
						recTemp.setField("YD_PREP_WK_ST", 			"L");
						recTemp.setField("ARR_WLOC_CD", 			szARR_WLOC_CD);
						recTemp.setField("YD_AIM_YD_GP", 			szYD_AIM_YD_GP);
						recTemp.setField("YD_AIM_BAY_GP", 			szYD_AIM_BAY_GP);
						recTemp.setField("YD_CARASGN_SEQ", 			YdConstant.YD_CARASGN_SEQ_AUTO_DEFAULT);
						//recTemp.setField("YD_EQP_WRK_SH", 		"" + intLOT_SH);
						recTemp.setField("CAR_GP", 					szCAR_GP);

						intRtnVal = ydPrepSchDao.insYdPrepsch(recTemp);

						if( intRtnVal < 0 ) {
							szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else if( intRtnVal == 0 ) {
							szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else{
							szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"] 등록 성공 ";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}

					}else{
						if( !szYD_STK_COL_GP.substring(0, 2).equals(szPREV_YD_STK_COL_GP.substring(0, 2)) ) {
							break;
						}
					}
					//준비재료 등록
					recTemp = JDTORecordFactory.getInstance().create();
					recTemp.setField("STL_NO", 				szSTL_NO);
					recTemp.setField("YD_PREP_SCH_ID", 		szYD_PREP_SCH_ID);
					recTemp.setField("REGISTER", 			szUserId);
					recTemp.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);
					recTemp.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
					recTemp.setField("YD_STK_LYR_NO", 		szYD_STK_LYR_NO);

					intRtnVal = ydPrepMtlDao.insYdPrepmtl(recTemp);

					if( intRtnVal < 0 ) {
						szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+j+"]번재료["+szSTL_NO+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if( intRtnVal == 0 ) {
						szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+j+"]번재료["+szSTL_NO+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+j+"]번재료["+szSTL_NO+"] 등록 성공 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					intREAL_LOT_SH++;
					szPREV_YD_STK_COL_GP = szYD_STK_COL_GP;
					intRowNo++;
				}

				szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"]의 매수["+intREAL_LOT_SH+"], 중량["+lngYD_MTL_WT_SUM+"] 수정 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				recInTemp = JDTORecordFactory.getInstance().create();
				//준비스케줄수정
				recInTemp.setField("YD_PREP_SCH_ID", 		szYD_PREP_SCH_ID);
				recInTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intREAL_LOT_SH));
				recInTemp.setField("YD_INV_SUM_WT", 		String.valueOf(lngYD_MTL_WT_SUM));
				intRtnVal = ydPrepSchDao.updYdPrepsch(recInTemp, 0);

				szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"]의 매수["+intREAL_LOT_SH+"], 중량["+lngYD_MTL_WT_SUM+"] 수정 성공 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				if( !bIsLoopable ) break;

			}

			//----------------------------------------------------------------------------------------------

			szMsg = "[JSP Session : "+szOperationName+"] -------------------- 메소드 끝 --------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of insYdPrepSch

	/**
	 * 이송대상재를 준비스케줄에 등록 - 수동
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String insYdPrepSchByManual(JDTORecord[] inDto ,JDTORecord inRec) throws DAOException {
		/*
		 * 업무기준 : 1. 그리드로 넘겨진 대상재들을 준비스케줄에 등록
		 * 				1-1. 준비스케줄 등록
		 * 				1-2. 준비재료 등록
		 *
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		//JDTO변수 정의
		JDTORecord       recTemp        = null;
		//DAO 변수 정의
		YdPrepMtlDao	ydPrepMtlDao	= new YdPrepMtlDao();
		YdPrepSchDao	ydPrepSchDao	= new YdPrepSchDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "insYdPrepSchByManual";
		String		szOperationName		= "이송대상재-준비스케줄등록(수동)";
		String		szRtnMsg			= YdConstant.RETN_CD_SUCCESS;
		//로컬변수 정의
		String		szYD_AIM_YD_GP		= null;
	    String      szYD_AIM_BAY_GP 	= null;
	    String		szARR_WLOC_CD		= null;
		int		intLOT_SH				= 0;
		String 		szYD_GP				= null;
		String		szYD_STK_COL_GP		= null;
		String		szYD_PREP_SCH_ID	= null;
		String		szUserId			= null;
		String		szYD_SCH_CD			= null;
		String		szSTL_NO			= null;
		String 		szYD_STK_BED_NO		= null;
		String		szYD_STK_LYR_NO 	= null;
		long		lngYD_MTL_WT		= 0;
		long		lngYD_MTL_WT_SUM	= 0;
		int intRtnVal = 0;
		String		szYD_CARASGN_SEQ 	=  "";

		try {
			intLOT_SH = inDto.length;
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 - 대상재건수["+intLOT_SH+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			szUserId 		= ydDaoUtils.paraRecChkNull(inDto[0], "YD_USER_ID");
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(inDto[0], "YD_STK_COL_GP");
			szARR_WLOC_CD 	= ydDaoUtils.paraRecChkNull(inDto[0], "ARR_WLOC_CD");
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(inDto[0], "YD_AIM_BAY_GP");
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(inDto[0], "YD_AIM_YD_GP");
			szYD_GP = szYD_STK_COL_GP.substring(0, 1);
			szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";
			szYD_CARASGN_SEQ = ydDaoUtils.paraRecChkNull(inRec, "YD_CARASGN_SEQ");
			
			//준비스케줄 등록
			szYD_PREP_SCH_ID = ydPrepSchDao.getYdPrepschId();
			recTemp = JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
			recTemp.setField("YD_SCH_CD", szYD_SCH_CD);
			recTemp.setField("REGISTER", szUserId);
			recTemp.setField("YD_GP", szYD_GP);
			recTemp.setField("YD_PREP_WK_ST", "L");
			recTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD);
			recTemp.setField("YD_AIM_YD_GP",    szYD_AIM_YD_GP);
			recTemp.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
			/**
			 * 2024.06.04 이송재료 LIST 화면에서 이송LOT편성시 우선등록 할수있게 기능개선 --REQ202405572890
			 * 
			 */
			
			System.out.println("szYD_CARASGN_SEQ:"+szYD_CARASGN_SEQ);
			if("".equals(szYD_CARASGN_SEQ)) {
				recTemp.setField("YD_CARASGN_SEQ", YdConstant.YD_CARASGN_SEQ_MAN_DEFAULT);
			}
			else {
				recTemp.setField("YD_CARASGN_SEQ", szYD_CARASGN_SEQ);
			}
			
			recTemp.setField("YD_EQP_WRK_SH", "" + intLOT_SH);

			//// 2010.01.22 이현성 CAR_GP 추가
			recTemp.setField("CAR_GP", ydDaoUtils.paraRecChkNull(inRec, "CAR_GP"));


			//////////////////////////////////////////

			intRtnVal = ydPrepSchDao.insYdPrepsch(recTemp);

			if( intRtnVal < 0 ) {
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}else if( intRtnVal == 0 ) {
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}else{
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 등록 성공 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}

			for(int i = 0; i < inDto.length; i++ ) {

				szSTL_NO  		= ydDaoUtils.paraRecChkNull(inDto[i], "STL_NO");
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(inDto[i], "YD_STK_COL_GP");
				szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inDto[i], "YD_STK_BED_NO");
				szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(inDto[i], "YD_STK_LYR_NO");
				lngYD_MTL_WT	= ydDaoUtils.paraRecChkNullLong(inDto[i], "YD_MTL_WT");

				lngYD_MTL_WT_SUM += lngYD_MTL_WT;

				//준비재료 등록
				recTemp = JDTORecordFactory.getInstance().create();
				recTemp.setField("STL_NO", szSTL_NO);
				recTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
				recTemp.setField("REGISTER", szUserId);
				recTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
				recTemp.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
				recTemp.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);

				intRtnVal = ydPrepMtlDao.insYdPrepmtl(recTemp);

				if( intRtnVal < 0 ) {
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+(i + 1)+"]번재료["+szSTL_NO+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if( intRtnVal == 0 ) {
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+(i + 1)+"]번재료["+szSTL_NO+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+(i + 1)+"]번재료["+szSTL_NO+"] 등록 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}

			recTemp = JDTORecordFactory.getInstance().create();
			//준비스케줄수정
			recTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
			recTemp.setField("YD_INV_SUM_WT", "" + lngYD_MTL_WT_SUM);
			intRtnVal = ydPrepSchDao.updYdPrepsch(recTemp, 0);

			szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 등록 성공 - 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of insYdPrepSchByManual


   /**
	 * 목표행선/목표야드 /  목표동 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String updSlabTotYdTransMtlList(JDTORecord [] inDto) throws DAOException {


		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="updSlabTotYdTransMtlList";
		String szRcvMsg = "";
		String		szDATE_WORD			= null;


		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdStockDao ydStockDao = new YdStockDao();

		try {
			//메뉴 페이지 목록을 수정한다.
			for(int x=0;x<inDto.length;x++){


				//수정할 항목 SETTING
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", yddatautil.setDataDefault(inDto[x].getField("STL_NO"), ""));
				recPara.setField("YD_AIM_RT_GP",  yddatautil.setDataDefault(inDto[x].getField("YD_AIM_RT_GP"),  "").toUpperCase());
				recPara.setField("YD_AIM_YD_GP",  yddatautil.setDataDefault(inDto[x].getField("YD_AIM_YD_GP"),  "").toUpperCase());
				recPara.setField("YD_AIM_BAY_GP", yddatautil.setDataDefault(inDto[x].getField("YD_AIM_BAY_GP"), "").toUpperCase());
				recPara.setField("YD_DLVRDD_RULE_DD", yddatautil.setDataDefault(inDto[x].getField("DATE_WORD"), ""));
		 
				intRtnVal = ydStockDao.updYdStock(recPara, 0);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

					szRcvMsg = "수정시DAO ERROR 발생";
					return  szRcvMsg;

				} // end of if

			}
			return YdConstant.RETN_CD_SUCCESS;


		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}
	}	// end of updSlabTotYdTransMtlList


	/**
	 *  저장위치별 재고 List
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabTotYdStkPosList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		String      szMsg        = "";
		String      szMethodName = "getSlabTotYdStkPosList";
		String 	szOperationName = "저장위치별 재고 List";
		YdStkBedDao YdStkBedDap  = new YdStkBedDao();
		String szYdDongGp        = "";
		String szYdBedGp         = "";

		int intRtnVal = 0;

		try {


			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		    //recPara.setField("YD_BAY_GP",    		ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP"));
		    //recPara.setField("YD_EQP_GP", 		    ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_GP"));
			szYdDongGp = ydDaoUtils.paraRecChkNull(inDto, "YD_DONG_GP");
			szYdBedGp  = ydDaoUtils.paraRecChkNull(inDto, "YD_BED_GP");

			if (szYdDongGp.length() > 1){
				szYdDongGp = szYdDongGp.substring(1,2);
			}

			if (szYdBedGp.length() < 2){
				szYdBedGp = "%";
			}

			recPara.setField("YD_GP",  ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
		    recPara.setField("YD_BAY_GP",    		szYdDongGp);
		    recPara.setField("YD_EQP_GP", 		    ydDaoUtils.paraRecChkNull(inDto, "YD_SPAN_GP"));
		    recPara.setField("YD_COL_GP", 		    ydDaoUtils.paraRecChkNull(inDto, "YD_COL_GP"));
		    recPara.setField("YD_BED_GP",    		szYdBedGp);

			recPara.setField("PAGE_CNT",            inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT",             inDto.getField("ROWCOUNT"));

			recPara.setField("CURR_PROG_CD",        inDto.getField("CURR_PROG_CD"));
			recPara.setField("SPEC_ABBSYM",         inDto.getField("SPEC_ABBSYM"));
			recPara.setField("YD_W",                inDto.getField("YD_W"));
			recPara.setField("MSLAB_CREDATE",       inDto.getField("MSLAB_CREDATE"));
			recPara.setField("RECEIPT_DATE",       inDto.getField("RECEIPT_DATE"));
			recPara.setField("MSLAB_ASGN_GP",       inDto.getField("MSLAB_ASGN_GP"));

			String syd_gp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");

			if(syd_gp.equals("S")){
				intRtnVal = YdStkBedDap.getYdStkbed(recPara, outRecSet, 308);
			}else{
				intRtnVal = YdStkBedDap.getYdStkbed(recPara, outRecSet, 21);
			}

			if (intRtnVal < 0) {

				szMsg = " 저장위치별 재고 List DAO 조회 !!!, ErrorCode:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				return outRecSet;
			} // end of if


			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getSlabTotYdStkPosList
	
	
	
	/**
	 *  선적대상재 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabShipTargetList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		String      szMsg        = "";
		String      szMethodName = "getSlabShipTargetList";
		String 	szOperationName = "선적대상재 조회";
		YdStkBedDao YdStkBedDap  = new YdStkBedDao();
		String szYdDongGp        = "";
		String szYdBedGp         = "";

		int intRtnVal = 0;

		try {


			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			szYdDongGp = ydDaoUtils.paraRecChkNull(inDto, "YD_DONG_GP");
			szYdBedGp  = ydDaoUtils.paraRecChkNull(inDto, "YD_BED_GP");

			if (szYdDongGp.length() > 1){
				szYdDongGp = szYdDongGp.substring(1,2);
			}

			if (szYdBedGp.length() < 2){
				szYdBedGp = "%";
			}

			recPara.setField("YD_GP",  ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
		    recPara.setField("YD_BAY_GP",    		szYdDongGp);
		    recPara.setField("YD_EQP_GP", 		    ydDaoUtils.paraRecChkNull(inDto, "YD_SPAN_GP"));
		    recPara.setField("YD_COL_GP", 		    ydDaoUtils.paraRecChkNull(inDto, "YD_COL_GP"));
		    recPara.setField("YD_BED_GP",    		szYdBedGp);

			recPara.setField("PAGE_CNT",            inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT",             inDto.getField("ROWCOUNT"));

			recPara.setField("CURR_PROG_CD",        inDto.getField("CURR_PROG_CD"));
			recPara.setField("SPEC_ABBSYM",         inDto.getField("SPEC_ABBSYM"));
			recPara.setField("YD_W",                inDto.getField("YD_W"));
			recPara.setField("MSLAB_CREDATE",       inDto.getField("MSLAB_CREDATE"));
			recPara.setField("RECEIPT_DATE",       inDto.getField("RECEIPT_DATE"));
			recPara.setField("MSLAB_ASGN_GP",       inDto.getField("MSLAB_ASGN_GP"));
			
			recPara.setField("DEMANDER_CD",  		inDto.getField("DEMANDER_CD"));
			recPara.setField("SHPM_SCH_DD",         inDto.getField("SEARCH_DATE"));
			recPara.setField("YD_SHIP_GP",          inDto.getField("YD_SHIP_GP"));
			recPara.setField("STL_NOS",             inDto.getField("STL_NOS"));
			
			String syd_gp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");

			if(syd_gp.equals("S")){
				intRtnVal = YdStkBedDap.getYdStkbed(recPara, outRecSet, 321);
			}else{
				intRtnVal = YdStkBedDap.getYdStkbed(recPara, outRecSet, 21);
			}

			if (intRtnVal < 0) {

				szMsg = " 선적대상재  DAO 조회 !!!, ErrorCode:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				return outRecSet;
			} // end of if


			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getSlabShipTargetList
	

	
	
	/**
	 * 저장위치별 재고 List - 1차선적예정일 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String updSlabShipingSchDate(JDTORecord [] inDto) throws DAOException {


		int intRtnVal = 0;
		String szMsg="선적대상재 조회 - 1차선적예정일 등록";
		String szMethodName="updSlabShipingSchDate";
		String szRcvMsg = "";

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdStockDao ydStockDao = new YdStockDao();

		try {
			
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//메뉴 페이지 목록을 수정한다.
			for(int x=0;x<inDto.length;x++){

				//수정할 항목 SETTING
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", yddatautil.setDataDefault(inDto[x].getField("STL_NO"), ""));
				recPara.setField("SHPM_SCH_DD",  yddatautil.setDataDefault(inDto[x].getField("SHPM_SCH_DD"),  ""));
				recPara.setField("USER_ID", yddatautil.setDataDefault(inDto[x].getField("YD_USER_ID"), ""));
				
				if("".equals(inDto[x].getField("SHPM_SCH_DD"))) {
					recPara.setField("STEP_YN", "N");
				} else {
					recPara.setField("STEP_YN", "Y");
				}
		 
				intRtnVal = ydStockDao.updYdStockShipDD(recPara, 0);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

					szRcvMsg = "수정시DAO ERROR 발생";
					return  szRcvMsg;

				} else if (intRtnVal == 0) {
					szMsg = "재료 번호 error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} // end of if
			}
			return YdConstant.RETN_CD_SUCCESS;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}
	}	// end of updSlabShipingSchDate
	
	
	
	/**
	 * 저장위치별 재고 List - 2차선적예정일 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String updSlabShipingSchDate2(JDTORecord [] inDto) throws DAOException {


		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="updSlabShipingSchDate2";
		String szRcvMsg = "";

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdStockDao ydStockDao = new YdStockDao();

		try {
			//메뉴 페이지 목록을 수정한다.
			for(int x=0;x<inDto.length;x++){

				//수정할 항목 SETTING
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", yddatautil.setDataDefault(inDto[x].getField("STL_NO"), ""));
				recPara.setField("SHPM_SCH_DD",  yddatautil.setDataDefault(inDto[x].getField("SHPM_SCH_DD"),  ""));
				recPara.setField("USER_ID", yddatautil.setDataDefault(inDto[x].getField("YD_USER_ID"), ""));
		 
				if("".equals(inDto[x].getField("SHPM_SCH_DD"))) {
					recPara.setField("STEP_YN", "N");
				} else {
					recPara.setField("STEP_YN", "Y");
				}
				
				intRtnVal = ydStockDao.updYdStockShipDD(recPara, 1);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

					szRcvMsg = "수정시DAO ERROR 발생";
					return  szRcvMsg;

				} else if (intRtnVal == 0) {
					szMsg = "재료 번호 error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} // end of if
			}
			return YdConstant.RETN_CD_SUCCESS;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}
	}	// end of updSlabShipingSchDate2
	
	
	
	
	/**
	 * 		[A] 오퍼레이션명 : 1차 선적예정일 SMS 전송
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	 */
	public String sendSmsShipDate1(JDTORecord gdReq) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		String      szMsg        = "";
		String      szMethodName = "sendSmsShipDate1";
		String 	szOperationName = "1차 선적예정일 SMS 전송";
		YdStkBedDao YdStkBedDap  = new YdStkBedDao();

		int intRtnVal = 0;
		
		try {
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			/**********************************************************
			* 1. SMS 전송 목록 조회
			**********************************************************/
			recPara.setField("GP", "1");  //1차,2차 구분
			intRtnVal = YdStkBedDap.getYdStkbed(recPara, outRecSet, 330);
	
			if (intRtnVal < 0) {
	
				szMsg = "1차선적예상일 sms 대상 조회 !!!, ErrorCode:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	
				return "FAIL";
			}
	
			/**********************************************************
			* 2. SMS 전송
			**********************************************************/
			if(outRecSet != null && outRecSet.size() > 0) {
				String rtnMsg = "";
				
				for(int i=0; i<outRecSet.size(); i++) {
					
					JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
					recPara1.setField("FROM_PHONE_NO", "0416806403");	
					recPara1.setField("TO_PHONE_NO"  , outRecSet.getRecord(i).getFieldString("HANDPHONE_NO")); // 010-XXXX-XXXX
					recPara1.setField("TO_CONTENT"   , gdReq.getField("V_SMS_CONTENTS"));
					recPara1.setField("TO_SUBJECT"   , gdReq.getField("V_SMS_TITLE"));
					rtnMsg = PlateGdsYdUtil.updMmsMsgSend(recPara1); // SMS 송신 
				}
			}
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			return "SUCCESS";
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}	
	} // end of sendSmsShipDate1
	
	
	
	/**
	 * 		[A] 오퍼레이션명 : 2차 선적예정일 SMS 전송
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	 */
	public String sendSmsShipDate2(JDTORecord gdReq) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		String      szMsg        = "";
		String      szMethodName = "sendSmsShipDate2";
		String 	szOperationName = "2차 선적예정일 SMS 전송";
		YdStkBedDao YdStkBedDap  = new YdStkBedDao();

		int intRtnVal = 0;
		
		try {
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			/**********************************************************
			* 1. SMS 전송 목록 조회
			**********************************************************/
			recPara.setField("GP", "2");  //1차,2차 구분
			intRtnVal = YdStkBedDap.getYdStkbed(recPara, outRecSet, 330);
	
			if (intRtnVal < 0) {
	
				szMsg = "2차선적예상일 sms 대상 조회 !!!, ErrorCode:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	
				return "FAIL";
			}
	
			/**********************************************************
			* 2. SMS 전송
			**********************************************************/
			if(outRecSet != null && outRecSet.size() > 0) {
				String rtnMsg = "";
				
				for(int i=0; i<outRecSet.size(); i++) {
					
					JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
					recPara1.setField("FROM_PHONE_NO", "0416806403");	
					recPara1.setField("TO_PHONE_NO"  , outRecSet.getRecord(i).getFieldString("HANDPHONE_NO")); // 010-XXXX-XXXX
					recPara1.setField("TO_CONTENT"   , gdReq.getField("V_SMS_CONTENTS"));
					recPara1.setField("TO_SUBJECT"   , gdReq.getField("V_SMS_TITLE"));
					rtnMsg = PlateGdsYdUtil.updMmsMsgSend(recPara1); // SMS 송신 
				}
			}
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			return "SUCCESS";
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}	
	} // end of sendSmsShipDate2
	
	
	
	/**
	 *  1차 선적대상재 중량/매수 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabShipListInfo1(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		String      szMsg        = "";
		String      szMethodName = "getSlabShipListInfo1";
		String 	szOperationName = "1차 선적대상재 중량/매수 조회";
		YdStockDao ydStockDao  = new YdStockDao();
		String szShipDate       = "";
		String szDemanderCd         = "";

		int intRtnVal = 0;

		try {


			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			szShipDate = ydDaoUtils.paraRecChkNull(inDto, "SHIP_DATE");
			szDemanderCd  = ydDaoUtils.paraRecChkNull(inDto, "DEMANDER_CD");

			recPara.setField("SHIP_DATE", szShipDate);
			recPara.setField("DEMANDER_CD", szDemanderCd);

			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 802);
			
			if (intRtnVal < 0) {

				szMsg = " 1차 선적대상재 중량/매수 DAO 조회 !!!, ErrorCode:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				return outRecSet;
			} // end of if

			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getSlabShipListInfo1
	
	
	
	
	/**
	 *  2차 선적대상재 중량/매수 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabShipListInfo2(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		String      szMsg        = "";
		String      szMethodName = "getSlabShipListInfo2";
		String 	szOperationName = "2차 선적대상재 중량/매수 조회";
		YdStockDao ydStockDao  = new YdStockDao();
		String szShipDate       = "";
		String szDemanderCd         = "";

		int intRtnVal = 0;

		try {


			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			szShipDate = ydDaoUtils.paraRecChkNull(inDto, "SHIP_DATE");
			szDemanderCd  = ydDaoUtils.paraRecChkNull(inDto, "DEMANDER_CD");

			recPara.setField("SHIP_DATE", szShipDate);
			recPara.setField("DEMANDER_CD", szDemanderCd);

			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 803);
			
			if (intRtnVal < 0) {

				szMsg = " 2차 선적대상재 중량/매수 DAO 조회 !!!, ErrorCode:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				return outRecSet;
			} // end of if

			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getSlabShipListInfo2
	
	

	/**
	 *  입출고 현황(통합슬라브야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabTotYdInOutList(JDTORecord inDto) throws DAOException {

		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");


		String      szMsg        = "";
		String      szMethodName = "getSlabTotYdInOutList";
		String	szOperationName = "입출고 현황(통합슬라브야드)";

		YdCarSchDao ydCarSchDao = new YdCarSchDao();

		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			  recPara.setField("DATE_FROM",    		ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM"));
			  recPara.setField("DATE_TO", 		    ydDaoUtils.paraRecChkNull(inDto, "DATE_TO"));
			  recPara.setField("YD_WRK_DUTY", 	    ydDaoUtils.paraRecChkNull(inDto, "YD_WRK_DUTY"));

			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 29);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		return outRecSet;






	}//end of getSlabTotYdInOutList

	/**
	 *  입출고 현황(통합슬라브야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabTotYdInOutListDong(JDTORecord inDto) throws DAOException {

		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");


		String      szMsg        = "";
		String      szMethodName = "getSlabTotYdInOutListDong";
		String	szOperationName = "입출고 현황(통합슬라브야드)";

		YdCarSchDao ydCarSchDao = new YdCarSchDao();

		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			  recPara.setField("DATE_FROM",    		ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM"));
			  recPara.setField("DATE_TO", 		    ydDaoUtils.paraRecChkNull(inDto, "DATE_TO"));
			  recPara.setField("YD_WRK_DUTY", 	    ydDaoUtils.paraRecChkNull(inDto, "YD_WRK_DUTY"));

			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 100);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		return outRecSet;






	}//end of getSlabTotYdInOutList



	/**
	 * 작업예약 조회(슬라브야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdWrkbook_page(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");


		String szMsg = "";
		String szMethodName = "getYdWrkbook_page";
		String szOperationName = "작업예약 조회(슬라브야드)";


		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();

		int intRtnVal = 0;

		String szDEL_YN				= null;
		String szDATE_FROM			= null;
		String szDATE_TO			= null;

		try {


			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			szDEL_YN		= ydDaoUtils.paraRecChkNull(inDto, "DEL_YN");

			szDATE_FROM		= ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM");
			szDATE_TO			= ydDaoUtils.paraRecChkNull(inDto, "DATE_TO");

		    recPara.setField("YD_GP",        		ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
		    recPara.setField("YD_BAY_GP",    		ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP"));
		    recPara.setField("YD_SCH_CD",    		ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_CD"));
		    recPara.setField("DEL_YN",       		szDEL_YN);

			recPara.setField("PAGE_CNT",            inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT",             inDto.getField("ROWCOUNT"));

			//intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, outRecSet, 19);

			if( szDEL_YN.equals("N"))	{										//미완료된 작업예약 조회 : 날짜와 상관없이 모두 조회되도록 처리
				szDATE_FROM				= "00000000";
				szDATE_TO				= "99999999";
			}

			recPara.setField("DATE_FROM",    		szDATE_FROM);
		    recPara.setField("DATE_TO",      		szDATE_TO);

			intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, outRecSet, 26);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getYdWrkbook_page




	/**
	 * 작업예약 재료 조회(슬라브야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdWrkbook_dtl_page(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");


		String      szMsg        = "";
		String      szMethodName = "getYdWrkbook_dtl_page";
		String      szYdGp = "";
		String szOperationName = "작업예약 재료 조회(슬라브야드)";


		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		    recPara.setField("YD_WBOOK_ID",        ydDaoUtils.paraRecChkNull(inDto, "YD_WBOOK_ID"));


		    szYdGp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");



		    YdWrkbookMtlDao ydWrkbookmtlDao = new YdWrkbookMtlDao();


		    if(szYdGp.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)  ||
		       szYdGp.equals(YdConstant.YD_GP_INTGR_YARD) ||  
		       szYdGp.equals(YdConstant.YD_GP_PORT_SLAB_YARD) ||  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
		       szYdGp.equals(YdConstant.YD_GP_C_SLAB_YARD)){
		    	ydWrkbookmtlDao.getYdWrkbookmtl(inDto, outRecSet, 29);
		    }else if(szYdGp.equals(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD)){
		    	ydWrkbookmtlDao.getYdWrkbookmtl(inDto, outRecSet, 46);
		    }else{ 
		    	ydWrkbookmtlDao.getYdWrkbookmtl(inDto, outRecSet, 28);
		    }


			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getYdWrkbook_dtl_page


	/**
	 *  크레인작업예약관리 - 작업예약 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String delYdWrkbook(JDTORecord [] inDto) throws DAOException {
		String 		szOperationName			= "작업예약삭제";
		String		szMethodName			= "delYdWrkbook";
		String		szMsg					= null;
		String		szRtnMsg				= null;

		JDTORecord	recPara					= null;
		JDTORecordSet rsResult				= null;

		String		szYD_WBOOK_ID			= null;
		String		szYD_USER_ID			= null;

		try {
			szMsg = "[Jsp Session : "+ szOperationName +"] 메소드 시작 - 삭제대상재 건수["+inDto.length+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara  = JDTORecordFactory.getInstance().create();
			for( int i = 0 ; i < inDto.length; i++ ) {
				//------------------------------------------------------------------------------------------------
				//	크레인스케줄이 존재하는 지 먼저 확인
				//------------------------------------------------------------------------------------------------
				szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(inDto[i], "YD_WBOOK_ID");
				szYD_USER_ID = ydDaoUtils.paraRecChkNull(inDto[i], "YD_USER_ID");

				recPara.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);

				rsResult       = JDTORecordFactory.getInstance().createRecordSet("");

				szRtnMsg = DaoManager.getYdCrnsch(recPara, rsResult, 28);

				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) && !szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
					szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]으로 크레인스케줄 조회 시 오류발생 : " + szRtnMsg;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					continue;
				}else{
					if( rsResult.size() > 0 ) {
						szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]으로 크레인스케줄이 존재하므로 작업예약을 삭제하지 않습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						return YdConstant.RETN_CD_FAILURE;
						//continue;
					}else{
						szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]으로 크레인스케줄이 존재하지 않으므로 작업예약 삭제 가능합니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
				}
				//------------------------------------------------------------------------------------------------


				//------------------------------------------------------------------------------------------------
				//	차량 / 대차 작업과 관계있는 작업 Clear (2010.01.12 이현성 추가)
				//------------------------------------------------------------------------------------------------
				recPara.setField("MODIFIER", szYD_USER_ID);


				szRtnMsg = yddatautil.delWBookBefoCarOrTCar(recPara);

				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"] 대차/차량 스케줄 Clear성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				}else if(szRtnMsg.equals(YdConstant.RETN_CD_FAILURE)){
					szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"] 대차/차량 스케줄 Clear 실패 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				}else{
					szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]  " + szRtnMsg ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				}

				//------------------------------------------------------------------------------------------------



				//------------------------------------------------------------------------------------------------
				//	작업예약/재료 삭제
				//------------------------------------------------------------------------------------------------

				szRtnMsg = YdCommonUtils.delYdWrkbookNMtl(szYD_WBOOK_ID, szYD_USER_ID);

				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]삭제 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else{
					szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]삭제 실패 - 메세지 : " + szRtnMsg;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}

				//------------------------------------------------------------------------------------------------
			}
			szMsg = "[Jsp Session : "+ szOperationName +"] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		} catch (Exception e) {
			szMsg = "[Jsp Session : "+ szOperationName +"] 예외발생 - 메세지 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(YdConstant.RETN_CD_FAILURE);
		}
		return YdConstant.RETN_CD_SUCCESS;
	}


	/**
	 *  크레인작업예약관리 - 작업예약 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String delYdWrkbook(JDTORecord  inDto) throws DAOException {
		String 		szOperationName			= "작업예약삭제";
		String		szMethodName			= "delYdWrkbook";
		String		szMsg					= null;
		String		szRtnMsg				= null;

		JDTORecord	recPara					= null;
		JDTORecordSet rsResult				= null;

		String		szYD_WBOOK_ID			= null;
		String		szYD_USER_ID			= null;

		try {
			szMsg = "[Jsp Session : "+ szOperationName +"] 메소드 시작 - 삭제대상재 건수["+ 1 +"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara  = JDTORecordFactory.getInstance().create();
			//------------------------------------------------------------------------------------------------
			//	크레인스케줄이 존재하는 지 먼저 확인
			//------------------------------------------------------------------------------------------------
			szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(inDto, "YD_WBOOK_ID");
			szYD_USER_ID = ydDaoUtils.paraRecChkNull(inDto, "YD_USER_ID");

			recPara.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);

			rsResult       = JDTORecordFactory.getInstance().createRecordSet("");

			szRtnMsg = DaoManager.getYdCrnsch(recPara, rsResult, 28);

			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) && !szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
				szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]으로 크레인스케줄 조회 시 오류발생 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szMsg;
			}else{
				if( rsResult.size() > 0 ) {
					szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]으로 크레인스케줄이 존재하므로 작업예약을 삭제하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return YdConstant.RETN_CD_FAILURE;
				}else{
					szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]으로 크레인스케줄이 존재하지 않으므로 작업예약 삭제 가능합니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}
			//------------------------------------------------------------------------------------------------


			//------------------------------------------------------------------------------------------------
			//	차량 / 대차 작업과 관계있는 작업 Clear (2010.01.12 이현성 추가)
			//------------------------------------------------------------------------------------------------
			recPara.setField("MODIFIER", szYD_USER_ID);

			szMsg = "[Jsp Session : "+ szOperationName +"] 수정자["+szYD_USER_ID+"] 로 작업예약 clear";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szRtnMsg = yddatautil.delWBookBefoCarOrTCar(recPara);

			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"] 대차/차량 스케줄 Clear성공 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			}else if(szRtnMsg.equals(YdConstant.RETN_CD_FAILURE)){
				szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"] 대차/차량 스케줄 Clear 실패 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			}else{
				szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]  " + szRtnMsg ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			}

			//------------------------------------------------------------------------------------------------



			//------------------------------------------------------------------------------------------------
			//	작업예약/재료 삭제
			//------------------------------------------------------------------------------------------------

			szRtnMsg = YdCommonUtils.delYdWrkbookNMtl(szYD_WBOOK_ID, szYD_USER_ID);

			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]삭제 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]삭제 실패 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}

			//------------------------------------------------------------------------------------------------
			szMsg = "[Jsp Session : "+ szOperationName +"] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		} catch (Exception e) {
			szMsg = "[Jsp Session : "+ szOperationName +"] 예외발생 - 메세지 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(YdConstant.RETN_CD_FAILURE);
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}



	/**
	 *  C연주 슬라브야드 압연 지시 조회화면 ( 목표행선, 목표동 수정기능)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String updSlabYdRollCmdRef(JDTORecord [] inDto) throws DAOException {


		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="updSlabYdRollCmdRef";
		String szStlno = "";
		String szOperationName ="C연주 슬라브야드 압연 지시 조회화면 ( 목표행선, 목표동 수정기능)";

		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();
		try {

			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//메뉴 페이지 목록을 수정한다.
			for(int x=0;x<inDto.length;x++){

				szStlno =  yddatautil.setDataDefault(inDto[x].getField("STL_NO"),  "");

				if(szStlno.equals("")){
					return "재료번호가 존재하지않습니다";

				}

				//수정할 항목 SETTING
				recPara.setField("STL_NO",        szStlno);
				recPara.setField("YD_AIM_BAY_GP", yddatautil.setDataDefault(inDto[x].getField("YD_AIM_BAY_GP"),   ""));
				recPara.setField("YD_AIM_RT_GP",  yddatautil.setDataDefault(inDto[x].getField("YD_AIM_RT_GP"),    ""));


				intRtnVal = ydStockDao.updYdStock(recPara, 0);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return "저장품 UPDATE 가 실패하였습니다";
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
				} // end of if



			}

			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			return YdConstant.RETN_CD_SUCCESS;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}
	}	// end of updSlabYdRollCmdRef





	/**
	 * 슬라브 상세정보 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabYdStrlocIdInfojl(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");


		String szMsg = "";
		String szMethodName = "getSlabYdStrlocIdInfojl";
		String szOperationName = "슬라브 상세정보 조회";



	    YdStockDao ydStockDao = new YdStockDao();

		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			szMsg = "JSP-SESSION ["+ szOperationName +"] 재료번호 [:" + ydDaoUtils.paraRecChkNull(inDto, "V_SLAB_NO") +"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		    recPara.setField("SLAB_NO",      ydDaoUtils.paraRecChkNull(inDto, "V_SLAB_NO"));


		    intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 132);

		    //건수가 0 일경우는 슬라브정보의 MSLAB_NO 로 조회한다.

		    if(intRtnVal == 0){

		    	szMsg = "JSP-SESSION ["+ szOperationName +"] 재료번호 [:" + ydDaoUtils.paraRecChkNull(inDto, "V_SLAB_NO") +"] 의 슬라브정보가 없어"
		    	        + "슬라브의 주편정보로 조회";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	 intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 162);

		    }

		    szMsg = "JSP-SESSION ["+ szOperationName +"] 조회건수  [:" + intRtnVal ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		return outRecSet;
	}//end of getSlabYdStrlocIdInfojl



	/**
	 * 대차 스케줄  조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getEqpTCarSchInfo(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");


		String      szMsg        = "";
		String      szMethodName = "getEqpTCarSchInfo";
		String szOperationName = "대차 스케줄  조회";
	    YdEqpDao ydEqpDao = new YdEqpDao();

		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		    recPara.setField("YD_GP",      ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
		    intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 8);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);;
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);;
				}
				//return outRecSet;
				return outRecSet;
			} // end of if

			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getEqpTCarSchInfo




	/**
	 * 대차  작업 대기 현황
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getTCarWrkWaitList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");


		String      szMsg        = "";
		String      szMethodName = "getTCarWrkWaitList";
		String szOperationName="대차  작업 대기 현황";

	    YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();

		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		    recPara.setField("YD_WRK_PLAN_TCAR",      ydDaoUtils.paraRecChkNull(inDto, "YD_WRK_PLAN_TCAR"));
		    intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, outRecSet, 23);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);;
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);;
				}
				//return outRecSet;
				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getTCarWrkWaitList




	/**
	 * 대차 작업 재료
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getTCarSchWrkMtl(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");


		String      szMsg        = "";
		String      szMethodName = "getTCarSchWrkMtl";
	    String szOperationName = "대차 작업 재료" ;

	    YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();

		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		    recPara.setField("YD_WBOOK_ID",      ydDaoUtils.paraRecChkNull(inDto, "YD_WBOOK_ID"));
		    intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 35);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);;
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);;
				}

				szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

				return outRecSet;
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getTCarSchWrkMtl


	/////////////////


	/**
	 * 오퍼레이션명 : 스케줄 취소
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procSchCancle(JDTORecord msgRecord)throws JDTOException  {

		//크레인스케줄 DAO
		YdCrnSchDao ydCrnSchDao  = new  YdCrnSchDao();
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		//재료 DAO
		YdStockDao ydStockDao = new YdStockDao();

		//설비 DAO (2009.10.06 추가-이현성)
		YdEqpDao ydEqpDao = new YdEqpDao();

		//크레인 작업재료 DAO
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();

		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();

		//wan
		//파라미터 레코드 생성
		JDTORecord recPara   = JDTORecordFactory.getInstance().create();
		JDTORecord recDelPara   = JDTORecordFactory.getInstance().create();
		JDTORecord recParaStock   = JDTORecordFactory.getInstance().create();
		JDTORecord recEqpPara   = JDTORecordFactory.getInstance().create();

		//크레인스케줄 데이터  레코드셋 생성
		JDTORecordSet rsGetCrnSch = null;
		//크레인스케줄 레코드
		JDTORecord recGetCrnSch   = null;

		//크레인작업재료 데이터  레코드셋 생성
		JDTORecordSet rsGetCrnMtl = JDTORecordFactory.getInstance().createRecordSet("temp");

		JDTORecordSet rsGetBedInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet rsCrnSchInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet rsWbookMtlInfo = JDTORecordFactory.getInstance().createRecordSet("YD");

		//크레인작업재료 레코드
		JDTORecord recGetCrnMtl   = null;
		//적치단 업데이트 레코드
		JDTORecord recSetStkLyr = JDTORecordFactory.getInstance().create();

		//적치베드 정보 UPDATE 레코드
		JDTORecord recSetStkBed = JDTORecordFactory.getInstance().create();

		JDTORecord inRec =null;

		int intRtnVal = 0;
		int intRsGetCrnMtlSize = 0;
		String szStkLyrPlus = null;

		//파라미터 string
		String szV_YD_CRN_SCH_ID  = null;
		String szV_YD_SCH_CD      = null;
		String szV_DEL_YN         = null;
		String szV_MODIFIER       = null;
		String szV_YD_UP_WO_LOC   = null;
		String szV_YD_UP_WO_LAYER = null;
		String szV_YD_DN_WO_LOC   = null;
		String szV_YD_DN_WO_LAYER = null;

		String szMsg			= "";
		String szMethodName		= "procSchCancle";

		String szJMS_TC_CD 		= "";
		String szYdSchId 		= "";
		String szYdWrkProgStat 	= "";
		String szLogMsg  		= "";
		String szYdGp 			= "";
		String szEqpId 			= "";
		String szOperationName 	= "스케줄 취소";

		String szWbookId 		= "";
		String szUpdEqpstat 	= "";

		String szRtnMsg 		= "";
		String szCancleGp 		= "";

		EJBConnector ejbConn 	= null;

		try{

		szLogMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

		//========크레인스케줄 삭제==========//

		//파라미터 null 체크
		szV_YD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
		szV_YD_SCH_CD     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
		szV_DEL_YN        = ydDaoUtils.paraRecChkNull(msgRecord, "DEL_YN");
		szV_MODIFIER      = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");

		//해당 스케줄만 삭제 할경우 1 , 스케줄을 포함한 하위스케줄 삭제시에는 ""
		szCancleGp  = ydDaoUtils.paraRecChkNull(msgRecord, "CANCLE_GP");

		//파라미터 레코드 편집
		recPara.setField("YD_CRN_SCH_ID", szV_YD_CRN_SCH_ID);
		recPara.setField("YD_SCH_CD",     szV_YD_SCH_CD);
		recPara.setField("DEL_YN",        szV_DEL_YN);
		recPara.setField("MODIFIER",      szV_MODIFIER);

		rsGetCrnSch = JDTORecordFactory.getInstance().createRecordSet("YD");

		if(szCancleGp.equals("1")){
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsGetCrnSch, 0);
		}else{
			//스케줄코드가 동일하고 크레인스케줄 ID이상인 크레인스케줄ID를 SELECT ( 추가 : 스케줄 ID에 포함된 같은 작업예약정보에서만 추출)
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsGetCrnSch, 5);
		}

		//더 이상 삭제 작업이 없는경우
		if (intRtnVal < 1) {
			//szMsg="("+szMethodName+") 실패! 해당 데이터 없음" + " intRtnVal: " +intRtnVal;
			szMsg="삭제 작업이 완료되었습니다";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			return szMsg;
		}

		//레코드셋을 역순으로
		szMsg="레코드셋을 역순으로 정렬";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		rsGetCrnSch.reverseOrder();
		//레코드셋의 커서를 처음으로

		szMsg="레코드셋처음으로 이동";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		rsGetCrnSch.first();

		szMsg="작업매수 :"+ rsGetCrnSch.size() + "개의 크레인 스케줄 작업을 처리" ;
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		//크레인스케줄 데이터 만큼 루프를 돌아서 크레인스케줄ID에 편성된 재료를 찾아 적치단을 CLEAR한다.
		for (int Loop_i = 0; Loop_i < rsGetCrnSch.size(); Loop_i++) {

			//크레인스케줄 데이터의 레코드를 추출(작업상태 체크를 위해 미리 추출)
			//ADD
			recGetCrnSch = JDTORecordFactory.getInstance().create();
			recGetCrnSch = rsGetCrnSch.getRecord(Loop_i);

			recPara = JDTORecordFactory.getInstance().create();

			szYdSchId =  ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_CRN_SCH_ID");
			recPara.setField("YD_CRN_SCH_ID",szYdSchId);

			szWbookId =  ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_WBOOK_ID");

			szMsg="크레인스케줄 데이터의 레코드를 추출(작업상태 체크를 위해 미리 추출)";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_WRK_PROG_STAT");

			/*********************************************************************************************/
			/*  수정일 : 2010년 02월 12일
			 *  수정자 : 석창화
			 *  수정내용 : 스케줄 진행상태에 관계없이 스케줄취소 진행하도록 수정 (최이사님 지시)
			 */

			// 크레인 스케줄 진행상태가 작업대기 또는 작업선택(권상지시)상태인지 체크한다.
			//if ((!szYdWrkProgStat.equals("W")) && (!szYdWrkProgStat.equals("1")) ) {
			//	szMsg="적치단 초기화 ("+szMethodName+") 실패! 상태가 (W,1)가 아님" + " 진행상태: " +   ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_WRK_PROG_STAT");

			//	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//	return szMsg;
			//}

			/*********************************************************************************************/
			// 설비번호를 얻는다.(YD_EQP_ID) => 설비상태를  변경하기 위함
			szEqpId =  ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_EQP_ID");

			//권상 지시위치
			szV_YD_UP_WO_LOC   = ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_UP_WO_LOC");
			//권상 지시단
			szV_YD_UP_WO_LAYER = ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_UP_WO_LAYER");
			//권하 지시위치
			szV_YD_DN_WO_LOC   = ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_DN_WO_LOC");
			//권하 지시단
			szV_YD_DN_WO_LAYER = ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_DN_WO_LAYER");

			//해당 크레인스케줄ID로 크레인작업재료를 SELECT
			//ADD
			rsGetCrnMtl = JDTORecordFactory.getInstance().createRecordSet("temp");
			intRtnVal = ydCrnSchDao.getYdCrnsch(recGetCrnSch, rsGetCrnMtl, 3);

			//에러리턴
			if (intRtnVal < 0) {
				szMsg="("+szMethodName+") 실패! 해당 작업재료 조회 ERROR" + " intRtnVal: " +intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return szMsg;
			}

			szMsg="권상지시위치 "+szV_YD_UP_WO_LOC+"\n";
			szMsg+="권상 지시단 "+szV_YD_UP_WO_LAYER+"\n";
			szMsg+="권하 지시위치 "+szV_YD_DN_WO_LOC+"\n";
			szMsg+="권하 지시단 "+szV_YD_DN_WO_LAYER+"\n";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			//스케줄이 선택된 상태에서는 작업지시 취소 전문을 발생시켜준다
			if( szYdWrkProgStat.equals("1") ){

				/*
				 * 작업지시 취소 전문
				 *
				 * YDY1L004 - C연주 슬라브 야드
				 * YDY3L004 - 후판 슬라브 야드
				 * YDY4L004 - 후판제품 야드
				 * YDY5L004 - 코일 소재/제품 야드
				 */
				szYdGp = szV_YD_SCH_CD.substring(0,1);

				szLogMsg = "[JSP Session]작업취소   : 야드구분[" + szYdGp + "]";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

				if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp)  ){				//C연주 슬라브 야드 [A]
					szJMS_TC_CD = "YDY1L004";

				}else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp)){	//A후판 슬라브야드[D]

					szJMS_TC_CD = "YDY3L004";

				}else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp)){		//후판제품야드 [K]

					szJMS_TC_CD = "YDY4L004";

				}else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)){	//C열연 코일야드[H]

					szJMS_TC_CD = "YDY5L004";

				}else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)){	//C열연 제품야드[J]

					szJMS_TC_CD = "YDY5L004";

				}else if(YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYdGp)){		//항만슬라브야드 기능추가 - 2015.12.30 LeeJY

					szJMS_TC_CD = "YDE7L004";

				}

				if(szYdGp.equals(YdConstant.YD_GP_C_SLAB_YARD)
						|| YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYdGp)  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
						|| YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp)
						|| YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp)
						|| YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)
						|| YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)	){

					recDelPara   = JDTORecordFactory.getInstance().create();
					recDelPara.setField("MSG_ID",           szJMS_TC_CD        );
					recDelPara.setField("YD_CRN_SCH_ID",    szYdSchId          );
					recDelPara.setField("YD_WRK_PROG_STAT", szYdWrkProgStat    );   // 이모듈을 탈려면 항상 '1'의값이 들어옴
					recDelPara.setField("MSG_GP",           "D"                );

					YdDelegate ydDelegate = new YdDelegate();
					ydDelegate.sendMsg(recDelPara);
				}

				//설비별 보급 스케줄이 잡힌 상태
				// T/C 열연정정보급완료실적 (YDHRJ001) : 보급취소
				if(szV_YD_SCH_CD.equals("HDFE03UM") ||
						szV_YD_SCH_CD.equals("HEDE01UM") ||
						szV_YD_SCH_CD.equals("HFFE02UM") ||
						szV_YD_SCH_CD.equals("HGFE01UM") ||
						szV_YD_SCH_CD.equals("HHKE01UM")     ){

					// 해당 스케줄일 경우 HR로 전문을 전송시킨다.
					// MSG_ID : YDHRJ001, STL_NO : 재료번호, TREAT_GP : 2

					szLogMsg = "[JSP Session] 작업취소 중 보급취소 : 스케줄 코드 [ "+ szV_YD_SCH_CD +"] 이므로 보급취소 전문을 전송한다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

					inRec = JDTORecordFactory.getInstance().create();
					inRec.setField("YD_WBOOK_ID", szWbookId);

					JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("temRs");
					intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(inRec, outRecSet, 1);

					if(intRtnVal< 0){
						szLogMsg = "[JSP Session] 작업취소 : 분기 작업 작업예약 조회시 ERROR";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					}else if (intRtnVal == 0){
						szLogMsg = "[JSP Session] 작업취소 : 분기 작업 작업예약 조회시 작업예약 DATA 없음";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					}
					else{
						szLogMsg = "[JSP Session] 작업취소 : 분기 작업 작업예약 조회시 작업예약 DATA 존재함";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

						outRecSet.first();
						inRec = JDTORecordFactory.getInstance().create();
						inRec = outRecSet.getRecord();

						String szTempStlNo = null;
						szTempStlNo = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");

						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("MSG_ID", "YDHRJ001");
						recPara.setField("STL_NO",  szTempStlNo);
						recPara.setField("TREAT_GP","2");

						if(szV_YD_DN_WO_LOC.length() != 8){
							szLogMsg = "[JSP Session]  권하지시위치정보 보급취소 전문을 전송할 수 없습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						}else{
							recPara.setField("YD_EQP_ID",szV_YD_DN_WO_LOC.substring(0,6));
							recPara.setField("YD_STK_BED_NO",szV_YD_DN_WO_LOC.substring(6,8));

							YdDelegate ydDelegate = new YdDelegate();

							ydUtils.displayRecord("스케줄 취소중 보급취소전문", recPara);
							ydDelegate.sendMsg(recPara);
						}
					}
				}  // 보급 취소 끝

				//--------------------------------------------------------------------------------
				// 설비가 고장 또는 OFF 라인 상태가 아닐경우
				// 선택된 설비가 취소 되었으므로 해당설비의 설비 테이블정보에
				// 작업대기 상태로 UPDATE 해준다.
				//--------------------------------------------------------------------------------

				szRtnMsg = YdCommonUtils.checkCrnStat(szEqpId);

				if(szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
					recEqpPara   = JDTORecordFactory.getInstance().create();
					recEqpPara.setField("YD_EQP_ID", szEqpId);

					recEqpPara.setField("YD_EQP_STAT", YdConstant.YD_EQP_STAT_IDLE);
					recEqpPara.setField("MODIFIER",szV_MODIFIER);

					szMsg="[Jsp-Session " + szOperationName+ " ] 크레인("+ szEqpId +") 설비상태 [" + YdConstant.YD_EQP_STAT_IDLE +"]로 변경 ------------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				    EJBConnector ejbConn2 = new EJBConnector("default","SlabJspSeEJB",this);
				    Boolean isSuccess = (Boolean)ejbConn2.trx("RequiresUpdYdEqp",new Class[]{JDTORecord.class}, new Object[]{recEqpPara});
				}
			}

			//---------------------------------------------------------------------------------------------
			// 권하위치 되돌리기/ 권상위치 되돌리기를 사용하지않는 방법(검증되지 않아 현재 상태유지중)
			//
			// 작업예약 정보만을 가지고 재료정보를 초기화 시킨다.
			// 해당 스케줄의 작업예약 정보의 재료를 가지고
			// 			1 .적치단정보가 권상정보의 재료정보는 적치중으로 변경하고
			//			2. 적치단정보가 권하정보의 재료정보는 적치가능으로 수정한다.
			//          3. 적치단정보가 적치중인 재료는 SKIP 한다.

			//---------------------------------------------------------------------------------------------
			//1. 크레인 스케줄 ID에 속해 있는 작업예약 아이디 정보로 작업재료 조회
			//2. 재료별 적치단 정보가 'U' 로 세팅된 적치단은 'C'로 업데이트
			//3. 재료별 적치단 정보가 'D' 로 세팅된 적치단은 'E'로 업데이트 한다.
			//4. 스케줄 작업재료를 삭제한다.
			//5. 스케줄을 삭제 한다.
			//---------------------------------------------------------------------------------------------

			//---------------------------------------------------------------------------------------------
			//권하위치 되돌리기
			//---------------------------------------------------------------------------------------------
			// 2009.10.07 스케줄/작업 취소시 권하위치가 XX010101 BED로 잡혀있는경우나
			// 권하 지시위치 정보가 올바르게 들어있지않는 정보는 돌려줄 수 없다.
			// 권하위치 되돌리기
			if (!( szV_YD_DN_WO_LOC.equals("") || szV_YD_DN_WO_LAYER.equals("")  || szV_YD_DN_WO_LOC.equals("XX010101") )  ){

				//레코드의 커서를 처음으로
				szMsg = "재료개수 : " + rsGetCrnMtl.size();
				ydUtils.putLog(szSessionName, szMethodName, szMsg ,4);

				rsGetCrnMtl.first();

				//레코드 갯수를 구한다.
				intRsGetCrnMtlSize = rsGetCrnMtl.size();

				//크레인스케줄의 작업 재료 만큼 루프를 돌아 권상권하 대기 정보를 초기화한다.
				for (int Loop_j = 0; Loop_j < intRsGetCrnMtlSize; Loop_j++) {

					//크레인작업재료 데이터의 레코드를 추출
					recGetCrnMtl = JDTORecordFactory.getInstance().create();
					recGetCrnMtl = rsGetCrnMtl.getRecord(Loop_j);
					/*
					recSetStkLyr = JDTORecordFactory.getInstance().create();
					//권하지시 적치열구분 (권하지시위치 = 적치열(6) + 적치BED(2))
					recSetStkLyr.setField("YD_STK_COL_GP",       szV_YD_DN_WO_LOC.substring(0, 6));
					//권하지시 적치BED번호
					recSetStkLyr.setField("YD_STK_BED_NO",       szV_YD_DN_WO_LOC.substring(6, 8));
					//권하지시 적치단
					szStkLyrPlus = ydDaoUtils.stringPlusInt(szV_YD_DN_WO_LAYER, Loop_j);
					recSetStkLyr.setField("YD_STK_LYR_NO",       szStkLyrPlus);
					//재료번호 CLEAR
					recSetStkLyr.setField("STL_NO",              "");
					//적치단재료상태 적치가능("E")으로 SET
					recSetStkLyr.setField("YD_STK_LYR_MTL_STAT", "E");

					szMsg="권하 재료 정보 복원";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.displayRecord(szOperationName, recSetStkLyr);

					//적치단 테이블에 권하지시 CLEAR 업데이트
					intRtnVal = ydStkLyrDao.updYdStklyr(recSetStkLyr, 0);
					*/
					// 기존 지시위치 에 쌓여 있는 정보 Clear
					recSetStkLyr = JDTORecordFactory.getInstance().create();
					recSetStkLyr.setField("YD_STK_COL_GP",       szV_YD_DN_WO_LOC.substring(0, 6));
					recSetStkLyr.setField("YD_STK_BED_NO",       szV_YD_DN_WO_LOC.substring(6, 8));
					/*
	                 * 2011.03.02 슬라브 권상모음 스케쥴 삭제시 문제발생
	                 * 아래 파라미터 막음.
	                 */
	                //recSetStkLyr.setField("YD_STK_LYR_MTL_STAT", "D");
					recSetStkLyr.setField("STL_NO",              yddatautil.setDataDefault(recGetCrnMtl.getField("STL_NO"),""));

					intRtnVal = ydStkLyrDao.updYdStklyrWithColStockStat(recSetStkLyr);

					szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear 성공 [ " + intRtnVal + " ] ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

					//에러리턴
					if (intRtnVal < 1) {
						szMsg="("+szMethodName+") 실패! 적치단 권상 CLEAR 업데이트 실패!" + " intRtnVal: " +intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}

					//2009.11.07  - 베드정보중 'YD_STK_BED_WHIO_STAT' 정보가 'F'로 세팅된 정보는 'E'로 바꾸어준다.(START)
					szMsg= "[Jsp Session] 권하위치 Bed 정보 조회.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

					rsGetBedInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
					intRtnVal =  ydStkBedDao.getYdStkbed(recSetStkLyr, rsGetBedInfo, 0);

					if(intRtnVal > 0){
						// 권하위치 정보의 BED정보를 읽는다 .
						rsGetBedInfo.first();
						recSetStkBed = rsGetBedInfo.getRecord();

						if("F".equals(ydDaoUtils.paraRecChkNull(recSetStkBed, "YD_STK_BED_WHIO_STAT")) ){
							recSetStkBed.setField("YD_STK_BED_WHIO_STAT", "E");
							recSetStkBed.setField("MODIFIER", szV_MODIFIER);

							intRtnVal = ydStkBedDao.updYdStkbed(recSetStkBed, 0);

							if(intRtnVal < 0 ){
								szMsg= "[Jsp Session] 야드적치Bed입출고상태 변경 UPDATE ERROR 발생 .";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							}else{
								szMsg= "[Jsp Session] 야드적치Bed입출고상태 'E' 로 변경처리 함.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							}
						}
					}else{
						//해당 베드의 정보가 존재 하지 않습니다.
						szMsg= "해당 베드의 정보가 존재 하지 않습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
				}
			}else{
				//권하위치가 올바르게 잡혀있지 않을때 에러처리를 원한다면
				//이 부분에 관련 내용을 기술한다.
			}
			//---------------------------------------------------------------------------------------------

			//---------------------------------------------------------------------------------------------
			//권상위치 되돌리기
			//---------------------------------------------------------------------------------------------
			//2009.10.07 (데이터 정보가 확실하게 들어있어야 한다.)
			if (!( szV_YD_UP_WO_LOC.equals("") || szV_YD_UP_WO_LAYER.equals("") )){

				//레코드의 커서를 처음으로

				rsGetCrnMtl.first();
				//String szStl_no = "";

				//레코드 갯수를 구한다.
				intRsGetCrnMtlSize = rsGetCrnMtl.size();

				//크레인스케줄의 작업 재료 만큼 루프를 돌아 권상권하 대기 정보를 초기화한다.
				for (int Loop_j = 0; Loop_j < intRsGetCrnMtlSize; Loop_j++) {

					//크레인작업재료 데이터의 레코드를 추출

					//ADD
					recGetCrnMtl = JDTORecordFactory.getInstance().create();
					recGetCrnMtl = rsGetCrnMtl.getRecord(Loop_j);

					recSetStkLyr = JDTORecordFactory.getInstance().create();
					//권상지시 적치열구분 (권상지시위치 = 적치열(6) + 적치BED(2))
					recSetStkLyr.setField("YD_STK_COL_GP",       szV_YD_UP_WO_LOC.substring(0, 6));

					//권상지시 적치BED번호
					recSetStkLyr.setField("YD_STK_BED_NO",       szV_YD_UP_WO_LOC.substring(6, 8));

					//권상지시 적치단
					szStkLyrPlus = ydDaoUtils.stringPlusInt(szV_YD_UP_WO_LAYER, Loop_j);
					recSetStkLyr.setField("YD_STK_LYR_NO",       szStkLyrPlus);

					recSetStkLyr.setField("STL_NO", recGetCrnMtl.getField("STL_NO"));
					recSetStkLyr.setField("YD_STK_LYR_MTL_STAT", "C");

					szMsg="권상 재료 정보 복원";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					ydUtils.displayRecord(szOperationName, recSetStkLyr);

					JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp1");
					intRtnVal = ydStkLyrDao.getYdStklyr(recSetStkLyr, outRecSet, 0);
					outRecSet.absolute(1);
					JDTORecord recReturnData = JDTORecordFactory.getInstance().create();
		        	recReturnData.setRecord(outRecSet.getRecord());

					String sLyrStat = recReturnData.getFieldString("YD_STK_LYR_MTL_STAT");

					szMsg="("+szMethodName+") 적치단 권상 STATUS: " +sLyrStat;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					if("D".equals(sLyrStat)){
						// 권상위치가 권하대기인 경우는 SKIP
						intRtnVal = 1;
					}else{
						//적치단 테이블에 권상지시 CLEAR 업데이트 ('U' -> 'C')
						intRtnVal = ydStkLyrDao.updYdStklyr(recSetStkLyr, 0);
					}

					//에러리턴
					if (intRtnVal < 1) {
						szMsg="("+szMethodName+") 실패! 적치단 권상 CLEAR 업데이트 실패!" + " intRtnVal: " +intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						//return;
					}
					//다음 스케줄 작업 재료 레코드로가기 전 작업한 스케줄 재료를 삭제 처리 해준다.
					szMsg="++++++++++++++크레인스케줄 작업 재료 삭제처리++++++++++++++++++++++";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					recPara.setField("YD_CRN_SCH_ID",szYdSchId);
					recPara.setField("DEL_YN", "Y");
					recPara.setField("MODIFIER",szV_MODIFIER);
					recPara.setField("STL_NO", recGetCrnMtl.getField("STL_NO"));

					intRtnVal = ydCrnWrkMtlDao.updYdCrnwrkmtl(recPara, 0);



					// 스케줄  작업 재료 삭제시 저장품에 등록된 작업예약 ID, 스케줄 CD Clear
					recParaStock.setField("STL_NO",  recGetCrnMtl.getField("STL_NO"));
					recParaStock.setField("MODIFIER",szV_MODIFIER);
					recParaStock.setField("YD_WBOOK_ID","" );
					recParaStock.setField("YD_SCH_CD","" );

					szMsg="++++++++++스케줄  작업 재료 삭제시 저장품에 등록된 작업예약 ID, 스케줄 CD Clear ++++++++++++++++++";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					ydStockDao.updYdStock(recParaStock, 0);


				}


				//크레인스케줄 삭제처리

				recPara.setField("DEL_YN", "Y");
				recPara.setField("MODIFIER", szV_MODIFIER);


				szLogMsg = "JSP-SESSION ["+ szOperationName +"]크레인스케줄 삭제처리 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

				intRtnVal = ydCrnSchDao.updYdCrnsch(recPara, 0);

				if(intRtnVal < 0){
					szLogMsg = "JSP-SESSION ["+ szOperationName +"]크레인스케줄 삭제처리 실패  ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);

				}else if (intRtnVal == 0){
					szLogMsg = "JSP-SESSION ["+ szOperationName +"] 삭제할 크레인 스케줄이 존재 하지 않습니다. ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

				}else {
					szLogMsg = "JSP-SESSION ["+ szOperationName +"] 크레인 스케줄 삭제 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

				}


				// 2009.12.14  (이현성)
				// 설비 상태를 진행상태에 맞도록 변경 시킨다.
				// 해당 작업 예약 ID으로 스케줄 정보 조회시에 하나도 존재 하지 않을경우에
				// - 해당 스케줄 코드로 전체 스케줄 조회시 남은스케줄 첫번째 진행상태 정보로 UPDATE
				// - 해당 스케줄 코드로 전체 스케줄 조회시 남아있는것이 없을경우는 대기상태로 UPDAT 해준다.

				recEqpPara   = JDTORecordFactory.getInstance().create();
				rsCrnSchInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
				recEqpPara.setField("YD_WBOOK_ID", szWbookId);

				intRtnVal = ydCrnSchDao.getYdCrnsch(recEqpPara, rsCrnSchInfo, 28);

				//설비 상태 UPDATE 유무 체크 FLAG
				boolean lb_updEqpFlag  = false;

				if(intRtnVal < 0 ){
					szMsg="해당 작업예약 정보에  남은 스케줄 조회시 ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					lb_updEqpFlag  = false;

				} else if (intRtnVal ==0){
					szMsg="해당 작업예약 정보에  남은 스케줄 정보가 없으므로 작업대기 상태로 UPDATE 합니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


					//해당 스케줄 코드로 같은 편성된 스케줄이 없는지 확인한다.(다른작업예약 ID가 편성되었을경우)
					recEqpPara   = JDTORecordFactory.getInstance().create();
					rsCrnSchInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
					recEqpPara.setField("YD_SCH_CD", szV_YD_SCH_CD);


					intRtnVal = ydCrnSchDao.getYdCrnsch(recEqpPara, rsCrnSchInfo, 6);



					if(intRtnVal < 0 ){
						szMsg="남은 스케줄코드로 스케줄 조회시 ERROR";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						lb_updEqpFlag  = false;

					}  else if (intRtnVal == 0){
						szMsg="남은 스케줄 정보가 없으므로 작업대기 상태로 UPDATE 합니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						szUpdEqpstat = YdConstant.YD_EQP_STAT_IDLE;
						lb_updEqpFlag  = true;
					} else{
						szMsg="해당 스케줄 코드에 스케줄 정보가 남아 있어 설비정보는 남은스케줄 첫번째 진행상태 정보로 UPDATE 합니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						recEqpPara   = JDTORecordFactory.getInstance().create();
						rsCrnSchInfo.first();
						recEqpPara = rsCrnSchInfo.getRecord();
						szUpdEqpstat = ydDaoUtils.paraRecChkNull(recEqpPara, "YD_WRK_PROG_STAT");
						lb_updEqpFlag  = true;

					}





				} else{

					szMsg="해당 작업예약 정보에 스케줄 정보가 남아 있어 설비정보는 UPDATE 하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					lb_updEqpFlag  = false;

				}





				szMsg="크레인 스케줄 취소 처리("+szMethodName+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);





				if(lb_updEqpFlag){

					//설비정보 업데이트 하기전에 설비상태 체크해준다.
					JDTORecord recInfo   = JDTORecordFactory.getInstance().create();

					szRtnMsg = YdCommonUtils.checkCrnStat(szEqpId, recInfo);


					if(szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){


						if( ydDaoUtils.paraRecChkNull(recInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_UP_WO)
							|| ydDaoUtils.paraRecChkNull(recInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_UP_CMPL)
							|| ydDaoUtils.paraRecChkNull(recInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_DN_WO)
							|| ydDaoUtils.paraRecChkNull(recInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_DN_CMPL)
							){

							szMsg="설비상태가 대기 상태가 아닌 작업상태이기때문에 값을 변경 할수 없습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


						}else{

							recEqpPara   = JDTORecordFactory.getInstance().create();
							recEqpPara.setField("YD_EQP_ID", szEqpId);
							recEqpPara.setField("YD_EQP_STAT", szUpdEqpstat);
							recEqpPara.setField("MODIFIER",szV_MODIFIER);

							szMsg="++++++++++ 해당 스케줄 크레인("+ szEqpId +") 설비상태 [" + szUpdEqpstat +"]로 변경 ++++++++++++++++++";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

							ydUtils.displayRecord(szOperationName, recEqpPara);
							intRtnVal = ydEqpDao.updYdEqp(recEqpPara, 0);

							if(intRtnVal < 0 ){
								szMsg=szEqpId +"설비정보를 변경 실패 하였습니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.ERROR);

							}
						}
					}
				}
			}

			//---------------------------------------------------------------------------------------------





		}



		szMsg= "스케줄 취소 작업완료";

		//작업 예약 /재료 삭제 하지않음으로 업무 변경됨

		szLogMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

		return szMsg;

		 } catch (DAOException daoe) {
	            throw daoe;
	        } catch (Exception e) {
	            throw new EJBServiceException(e);
	        }

	}// end of procSchCancle()



	/**
	 *  통합야드 차량작업관리 배차내역  조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabTotYdCarSch(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		String szMsg        		= "";
		//String szTemp       		= "";
		String szWLOC_CD       		= "";
		String szMethodName	 		= "getSlabTotYdCarSch";
		String szOperationName		= "통합야드배차내역";
		String szYD_CAR_STOP_LOC = "";

		YdCarSchDao ydCarSchDao = new YdCarSchDao();

		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			szWLOC_CD = yddatautil.setDataDefault(inDto.getField("WLOC_CD2"), "");

			if(szWLOC_CD.equals("") || szWLOC_CD.length() != 5 ){
//				recPara.setField("BAY",szWLOC_CD);
//				recPara.setField("BAY",yddatautil.setDataDefault(inDto.getField("BAY2"), ""));
//				recPara.setField("CAR_POINT",yddatautil.setDataDefault(inDto.getField("CAR_POINT"), ""));
//
//
//				intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 22);
//
//				if (intRtnVal <= 0) {
//					if (intRtnVal == -1) {
//						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					} else {
//						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					}
//					//return outRecSet;
//					return outRecSet;
//				} // end of if
				return outRecSet;
			}else if(szWLOC_CD.length() == 5){

				szYD_CAR_STOP_LOC = yddatautil.setDataDefault(inDto.getField("CAR_POINT"), "");
				if( szYD_CAR_STOP_LOC.equals("") ) {
					szYD_CAR_STOP_LOC = yddatautil.setDataDefault(inDto.getField("BAY2"), "");
					if( szYD_CAR_STOP_LOC.length() < 2 ) {
						szYD_CAR_STOP_LOC = "";
					}
				}
				recPara.setField("WLOC_CD", szWLOC_CD);
				recPara.setField("YD_CAR_STOP_LOC", szYD_CAR_STOP_LOC);
				//recPara.setField("BAY",yddatautil.setDataDefault(inDto.getField("BAY2"), ""));
				//recPara.setField("CAR_POINT",yddatautil.setDataDefault(inDto.getField("CAR_POINT"), ""));


				intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 31);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "["+szOperationName+"] 오류발생[1] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if (intRtnVal == -2) {
						szMsg = "["+szOperationName+"] 오류발생[2] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						szMsg = "["+szOperationName+"] 오류발생[3] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					//return outRecSet;
					return outRecSet;
				}else if(intRtnVal == 0) {
					szMsg = "["+szOperationName+"] 대상재가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}// end of if
			}


			szMsg = "["+szOperationName+"] 대상재가 존재합니다. - 건수 : " + outRecSet.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getSlabTotYdCarSch





	/**
	 *  통합야드 차량작업관리 - 차량작업상세내역
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabTotCarWork(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();

		YdUtils ydUtils = new YdUtils();

		String szMsg        = "";
		String szMethodName = "getSlabTotCarWork";

		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();

		int intRtnVal = 0;

		try {
			recPara.setField("YD_CAR_USE_GP",yddatautil.setDataDefault(inDto.getField("YD_CAR_USE_GP"), ""));
			recPara.setField("TRN_EQP_CD",yddatautil.setDataDefault(inDto.getField("TRN_EQP_CD"), ""));
			recPara.setField("CAR_NO",yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));
			recPara.setField("CARD_NO",yddatautil.setDataDefault(inDto.getField("CARD_NO"), ""));
//			recPara.setField("PAGE_CNT1",inDto.getField("PAGE_NO"));
//			recPara.setField("PAGE_CNT2",inDto.getField("PAGE_NO"));
//			recPara.setField("ROW_CNT1",inDto.getField("ROWCOUNT"));
//			recPara.setField("ROW_CNT2",inDto.getField("ROWCOUNT"));

			recPara.setField("YD_CAR_SCH_ID",yddatautil.setDataDefault(inDto.getField("YD_CAR_SCH_ID"), ""));

//			intRtnVal  = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 38);
			intRtnVal	= ydCarFtmvMtlDao.getYdCarftmvmtl(recPara, outRecSet, 12);

			if (intRtnVal <= 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session]차량작업관리 작업재료 조회 오류발생 1 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if (intRtnVal == 0) {
					szMsg = "[JSP Session]차량작업관리 작업재료 조회 대상재가 존재하지 않음 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				} else {
					szMsg = "[JSP Session]차량작업관리 작업재료 조회 오류발생 2 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if

			szMsg = "[JSP Session]차량작업관리 작업재료 조회 성공 : 레코드 수 - " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			//레코드셋이 없을때까지 반복한다.
			//outRecSet.first();



		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getSlabTotCarWork

	/**
	 * 구입슬라브의 제조사 목록 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getMakeNameList(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getMakeNameList";
		String szOperationName="구입슬라브제조사목록";
		JDTORecordSet outRecSet = null;
		JDTORecord recPara = null;

		QmBuySlabInfoDao QmBuySlabInfoDao = new QmBuySlabInfoDao();
		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			recPara = JDTORecordFactory.getInstance().create();
			intRtnVal = QmBuySlabInfoDao.getQmBuySlabInfo(recPara, outRecSet, 1);

			if (intRtnVal < 0) {
				szMsg = "[JSP Session : "+szOperationName+"] 조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				return outRecSet;

			 } // end of if
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	} //getMakeNameList
	
	/**
	 * 이송재료list 수요가 목록 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getMakeNameList2(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getMakeNameList";
		String szOperationName="이송재료list 수요가 목록";
		JDTORecordSet outRecSet = null;
		JDTORecord recPara = null;

		QmBuySlabInfoDao QmBuySlabInfoDao = new QmBuySlabInfoDao();
		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			recPara = JDTORecordFactory.getInstance().create();
			intRtnVal = QmBuySlabInfoDao.getQmBuySlabInfo(recPara, outRecSet, 2);

			if (intRtnVal < 0) {
				szMsg = "[JSP Session : "+szOperationName+"] 조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				return outRecSet;

			 } // end of if
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	} //getMakeNameList

	/**
	 *  재료정보 및 유무 체크
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCheckStlNo(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		String szMsg        		= "";
		String szMethodName	 		= "getCheckStlNo";
		String szOperationName		= "재료정보 및 유무 체크";
		YdStockDao ydStockDao = new YdStockDao();

		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

				recPara.setField("STL_NO", ydDaoUtils.paraRecChkNull(inDto, "STL_NO") );

				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 157);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "["+szOperationName+"] 오류발생[1] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if (intRtnVal == -2) {
						szMsg = "["+szOperationName+"] 오류발생[2] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						szMsg = "["+szOperationName+"] 오류발생[3] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					//return outRecSet;
					return outRecSet;
				}else if(intRtnVal == 0) {
					szMsg = "["+szOperationName+"] 대상재가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}// end of if


			szMsg = "["+szOperationName+"] 대상재가 존재합니다. - 건수 : " + outRecSet.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			} catch (Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
		}
		return outRecSet;
	}//end of getSlabTotYdCarSch





	/**
	 *  설비휴지이력조회 (조회)
	 *
	 *	권오창
	 *  2009.11.10
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getslabYdEqpPauseHist(JDTORecord inDto) throws DAOException {
		// DAO 및 UTIL 객체 생성
		YdEqpPauseDao ydEqpPauseDao = new YdEqpPauseDao();

		// 레코드 선언
		JDTORecord recPara          = null;
		JDTORecordSet outRecSet     = null;

		// 변수 선언
		String szMethodName         = "getslabYdEqpPauseHist";
		String szMsg                = "";
		int nRet                    = 0;

		try {
			// 레코드 생성
			recPara   = JDTORecordFactory.getInstance().create();
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");


			szMsg = "JSP-SESSION [설비휴지이력조회 (조회)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			// 야드 구분, 설비 ID, 페이지 설정
			recPara.setField("YD_GP" , yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_GP1", yddatautil.setDataDefault(inDto.getField("YD_GP1"), ""));
			recPara.setField("YD_GP2", yddatautil.setDataDefault(inDto.getField("YD_GP2"), ""));


			// 페이징설정
			recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1" , inDto.getField("PAGE_SIZE"));
			recPara.setField("ROW_CNT2" , inDto.getField("PAGE_SIZE"));


			// 야드구분과 설비번호를 전달인자로 넘겨준다.
			recPara.setField("YD_EQP_ID", inDto.getField("YD_EQP_ID"));
			nRet = ydEqpPauseDao.getYdEqppause(recPara, outRecSet, 2);
			if(nRet < 0) {
				if (nRet == -1) {
					szMsg = "routine error!!!, ErrorCode:" + nRet;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + nRet;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}

				return outRecSet;
			}

			outRecSet.first();
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}

		szMsg = "JSP-SESSION [설비휴지이력조회 (조회)] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		return outRecSet;
	} // end of getslabYdEqpPauseHist





	/**
	 * 설비휴지이력조회 (삭제)
	 *
	 *	권오창
	 *  2009.11.11
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void delslabYdEqpPauseHist(JDTORecord[] inDto) throws DAOException {
		//DAO 및 UTIL 객체 생성
		YdEqpPauseDao ydEqpPauseDao = new YdEqpPauseDao();

		// 레코드 선언
		JDTORecord recPara          = null;

		// 변수 선언
		String szMethodName         = "delslabYdEqpPauseHist";
		String szMsg                = "";
		int nRet                    = 0;


		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			szMsg = "JSP-SESSION [설비휴지이력조회 (삭제)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			// 화면에서 삭제하기 위해 체크된 것들을 반복하면서 삭제처리
			for(int x=0; x<inDto.length; x++){
				recPara.setField("YD_EQP_ID"            , yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), ""));
				recPara.setField("YD_EQP_PAUSE_OCCR_SEQ", yddatautil.setDataDefault(inDto[x].getField("YD_EQP_PAUSE_OCCR_SEQ"), ""));
				recPara.setField("DEL_YN"               , "Y");

				//야드 설비 UPDATE
				nRet = ydEqpPauseDao.updYdEqppause(recPara, 0);
				if(nRet < 0){
					if (nRet == -1) {
						szMsg = "Routine Error!!!, ErrorCode :" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "Parameter Error!!!, ErrorCode :" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
				}
			}


			szMsg = "JSP-SESSION [설비휴지이력조회 (삭제)] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}	// end of delslabYdEqpPauseHist





	/**
	 * 스카핑/정정보급재료LIST
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdScarfShearSupMtlList(JDTORecord inDto) throws DAOException {
		/*
		 * 업무기준 : 1. C연주 스카핑/정정보급재료 LIST 조회
		 */
		JDTORecord       recPara        = null;
		JDTORecordSet    outRecSet      = null;

		String		szOperationName		= "스카핑/정정보급재료LIST";
		String      szLogMsg        		= "";
		String      szMethodName 		= "getYdScarfShearSupMtlList";

		YdStockDao  	ydStockDao  			= new YdStockDao();

		String		szYD_WRK_GP			= null;
		String		szYD_EQP_ID			= null;
		String		szYD_GP				= null;
		String		szYD_BAY_GP			= null;
		String		szYD_SPAN_GP		= null;
		String		szYD_COL_GP		    = null;
		String		szSPAN_SEARCH_GP	= null;
		String		szYD_AIM_RT_GP		= null;
		String		szRT_SEARCH_GP		= null;
		String		szWO_MSLAB_RPR_MTD	= null;
		String		szLOT_YN			= null;
		String 		szSHEAR_SEARCH_GP	= null;
		String		szDATE_FROM			= null;
		String		szDATE_TO			= null;
		String		szHEAT_NO			= null;
		String		szSEQ				= null;
		String		szSCARF_UGNT_MTL_GP 	= null;		//긴급재구분
		String		szSCARF_REQ_DATE_FROM 	= null;		//일자
		String		szSCARF_REQ_DATE_TO 	= null;		//일자
		String		szSCARF_APR_PRIORITY 	= null;		//긴급재우선순위
		String		szSCARF_PLAN_PLNT_GP 	= null;		//스카핑공장
		String		szORD_YEOJAE_GP 		= null;			//주여구분
		String		szCURR_PROG_CD 			= null;				//진도코드
		String      szCOIL_NO				= null;
		String		szRD_DATE_ALL		= null;
		String		szVO_MATCH_YN		= null;
		int intRtnVal = 0;

		try {

			szLogMsg = "[Jsp Session : " + szOperationName + "] --------------------- 메소드 시작 : 파라미터 확인---------------------";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			szYD_WRK_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_WRK_GP");				//작업구분
			szYD_EQP_ID 			= ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_ID");				//야드설비구분
			szYD_GP 				= ydDaoUtils.paraRecChkNull(inDto, "YD_GP");					//야드구분
			szYD_BAY_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP");				//동구분
			szYD_SPAN_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_SPAN_GP");				//스판구분
			szYD_COL_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_COL_GP");				//열구분
			szYD_AIM_RT_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_RT_GP");				//야드목표행선
			szWO_MSLAB_RPR_MTD 		= ydDaoUtils.paraRecChkNull(inDto, "WO_MSLAB_RPR_MTD");			//주편손질방법
			szLOT_YN 				= ydDaoUtils.paraRecChkNull(inDto, "LOT_YN");					//LOT편성유무
			szSHEAR_SEARCH_GP 		= ydDaoUtils.paraRecChkNull(inDto, "SHEAR_SEARCH_GP");			//정정조회방법
			szDATE_FROM 			= ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM");				//FROM일자
			szDATE_TO 				= ydDaoUtils.paraRecChkNull(inDto, "DATE_TO");					//TO일자
			szHEAT_NO 				= ydDaoUtils.paraRecChkNull(inDto, "HEAT_NO");					//강번
			szSEQ 					= ydDaoUtils.paraRecChkNull(inDto, "YD_SEQ");					//스카핑순위

			szSCARF_UGNT_MTL_GP 	= ydDaoUtils.paraRecChkNull(inDto, "SCARF_UGNT_MTL_GP");		//긴급재구분
			szSCARF_REQ_DATE_FROM 	= ydDaoUtils.paraRecChkNull(inDto, "SCARF_REQ_DATE_FROM");		//일자
			szSCARF_REQ_DATE_TO 	= ydDaoUtils.paraRecChkNull(inDto, "SCARF_REQ_DATE_TO");		//일자
			szSCARF_APR_PRIORITY 	= ydDaoUtils.paraRecChkNull(inDto, "SCARF_APR_PRIORITY");		//긴급재우선순위
			szSCARF_PLAN_PLNT_GP 	= ydDaoUtils.paraRecChkNull(inDto, "SCARF_PLAN_PLNT_GP");		//스카핑공장
			szORD_YEOJAE_GP 		= ydDaoUtils.paraRecChkNull(inDto, "ORD_YEOJAE_GP");			//주여구분
			szCURR_PROG_CD 			= ydDaoUtils.paraRecChkNull(inDto, "CURR_PROG_CD");				//진도코드
			szCOIL_NO 				= ydDaoUtils.paraRecChkNull(inDto, "COIL_NO");				//진도코드
			szVO_MATCH_YN			= ydDaoUtils.paraRecChkNull(inDto, "VO_MATCH_YN");				//자외판구분

			//---------------------------------------------------------------------------
			//	날짜를 전체 조회할 것인 지 판단하는 변수(Y:전체,N:지정일자) 추가
			//	수정자 : 임춘수
			//	수정일 : 2010.01.29
			//---------------------------------------------------------------------------
			szRD_DATE_ALL			= ydDaoUtils.paraRecChkNull(inDto, "RD_DATE_ALL");
			//---------------------------------------------------------------------------


			if( !szYD_BAY_GP.equals("") ) {
				szYD_BAY_GP				= szYD_BAY_GP.substring(1, 2);
			}

			if( szYD_SPAN_GP.equals("") ) {
				szSPAN_SEARCH_GP = "1";
			}else{
				szSPAN_SEARCH_GP = "2";
			}

			if( szYD_AIM_RT_GP.equals("") ) {
				szRT_SEARCH_GP = "1";
			}else{
				szRT_SEARCH_GP = "2";
			}

			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
		    recPara.setField("YD_GP",     			szYD_GP);							//야드
		    recPara.setField("YD_BAY_GP",     		szYD_BAY_GP);						//동
		    recPara.setField("YD_SPAN_GP",     		szYD_SPAN_GP);						//스판
		    recPara.setField("YD_COL_GP",     		szYD_COL_GP);						//열
		    recPara.setField("SPAN_SEARCH_GP",     	szSPAN_SEARCH_GP);					//스판조회구분
		    recPara.setField("YD_AIM_RT_GP",     	szYD_AIM_RT_GP);					//목표행선
		    recPara.setField("RT_SEARCH_GP",     	szRT_SEARCH_GP);					//목표행선조회구분
		    recPara.setField("YD_SEQ",     			szSEQ);								//스카핑순위

		    recPara.setField("SCARF_UGNT_MTL_GP"	,szSCARF_UGNT_MTL_GP);			//긴급재구분
		    recPara.setField("SCARF_REQ_DATE_FROM"	,szSCARF_REQ_DATE_FROM);		//일자
		    recPara.setField("SCARF_REQ_DATE_TO"	,szSCARF_REQ_DATE_TO );			//일자
		    recPara.setField("SCARF_APR_PRIORITY"	,szSCARF_APR_PRIORITY );		//긴급재우선순위
		    recPara.setField("SCARF_PLAN_PLNT_GP"	,szSCARF_PLAN_PLNT_GP );		//스카핑공장
		    recPara.setField("ORD_YEOJAE_GP"		,szORD_YEOJAE_GP );				//주여구분
		    recPara.setField("CURR_PROG_CD"			,szCURR_PROG_CD);				//진도코드
		    recPara.setField("COIL_NO"			,szCOIL_NO);
		    recPara.setField("VO_MATCH_YN"			,szVO_MATCH_YN);
		    recPara.setField("PAGE_NO",   			inDto.getField("PAGE_NO"));
		    recPara.setField("ROW_CNT",    			inDto.getField("PAGE_SIZE"));

		    if( szYD_WRK_GP.equals("SCARF")) {
		    	recPara.setField("WO_MSLAB_RPR_MTD",    szWO_MSLAB_RPR_MTD);				//주편손질방법
		    	if( szLOT_YN.equals("N")) {
		    		szLogMsg = "["+szOperationName+"] 스카핑대상재 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

					/* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getScarfingMtlListPage2  */
		    		//intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 163);		//스카핑대상재조회
		    		intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 186);		//스카핑대상재조회

		    		szLogMsg = "["+szOperationName+"] 스카핑대상재 조회 완료 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		    	}else{
		    		szLogMsg = "["+szOperationName+"] 보급LOT편성된 스카핑대상재 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

					/* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getScarfingSupLotMtlListPage2  */
		    		//intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 165);		//보급LOT편성된 스카핑대상재 조회
		    		intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 188);		//보급LOT편성된 스카핑대상재 조회

		    		szLogMsg = "["+szOperationName+"] 보급LOT편성된 스카핑대상재 조회 완료 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		    	}
		    }else{
		    	if( szSHEAR_SEARCH_GP.equals("DATE")) {					//생산기한일자로 조회

		    		//---------------------------------------------------------------------------
					//	날짜를 전체 조회할 것인 지 판단하는 변수(Y:전체,N:지정일자) 추가
					//	수정자 : 임춘수
					//	수정일 : 2010.01.29
					//---------------------------------------------------------------------------
		    		if( szRD_DATE_ALL.equals("Y") ) {
		    			szDATE_FROM				= "00000000";
		    			szDATE_TO				= "99999999";
		    		}
		    		//---------------------------------------------------------------------------

		    		recPara.setField("DATE_FROM",    	szDATE_FROM);
		    		recPara.setField("DATE_TO",    		szDATE_TO);
		    		recPara.setField("SEARCH_GP",    	"2");
		    	}else if( szSHEAR_SEARCH_GP.equals("HEAT")){			//HEAT_NO로 조회
		    		recPara.setField("HEAT_NO",    		szHEAT_NO);
		    		recPara.setField("SEARCH_GP",    	"3");
		    	}else if( szSHEAR_SEARCH_GP.equals("HCR")){				//HCR로 조회
		    		recPara.setField("HCR_GP",    		"H");
		    		recPara.setField("SEARCH_GP",    	"1");
		    	}

		    	if( szLOT_YN.equals("N")) {
		    		szLogMsg = "["+szOperationName+"] 정정대상재 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

		    		//intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 164);		//정정대상재 조회
		    		intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 187);		//정정대상재 조회

		    		szLogMsg = "["+szOperationName+"] 정정대상재 조회 완료 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		    	}else{
					if( szYD_BAY_GP.equals("A") ) {
						//A동 2차절단 보급 스케줄
			    		if ("AAPUP9".equals(szYD_EQP_ID)) {
			    			recPara.setField("YD_SCH_CD",    		"AAPU09UM");	//#2 2차절단
			    			recPara.setField("SCH_SEARCH_GP",    	"2");
			    		} else if ("AAPUPA".equals(szYD_EQP_ID)) {
			    			recPara.setField("YD_SCH_CD",    		"AAPU10UM");	//#3 2차절단
			    			recPara.setField("SCH_SEARCH_GP",    	"2");
			    		} else {
			    			recPara.setField("YD_SCH_CD",    		"AADP02UM");	//#1 2차절단
			    			recPara.setField("SCH_SEARCH_GP",    	"2");
			    		}
			    		//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
			    		if (szYD_GP.equals("M")) {
			    			recPara.setField("YD_SCH_CD",    		"MADP01UM");	//Scarfer 
			    			recPara.setField("SCH_SEARCH_GP",    	"1");
			    		}
					}else if( szYD_BAY_GP.equals("C") ) {
						//C동 2차절단 보급 스케줄 (S/F 보급과 동일한 Depiler)
		    			recPara.setField("YD_SCH_CD",    		"ACDP01UM");		//#1 Scarfer(2차절단 공용)
		    			recPara.setField("SCH_SEARCH_GP",    	"2");
		    		} else {
		    			recPara.setField("SCH_SEARCH_GP",    	"1");
		    		}

		    		szLogMsg = "["+szOperationName+"] 보급LOT편성된 정정대상재 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

		    		//intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 166);		//보급LOT편성된 정정대상재 조회
		    		intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 189);		//보급LOT편성된 정정대상재 조회

		    		szLogMsg = "["+szOperationName+"] 보급LOT편성된 정정대상재 조회 완료 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		    	}
		    }

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szLogMsg = "["+szOperationName+"] 조회 시 오류발생[1] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				} else {
					szLogMsg = "["+szOperationName+"] 조회 시 오류발생[2] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if

			szLogMsg = "[Jsp Session : "+szOperationName+"] ------------------------- 메소드 끝 - 조회 건수 : " + outRecSet.size() + "-------------------------";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			szLogMsg = "["+szOperationName+"] 예외발생 - 메세지 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		return outRecSet;
	}//end of getYdScarfShearSupMtlList

	/**
	 * 스카핑/정정보급LOT등록 - 자동
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String insCSlabSupPrepSchAuto(JDTORecord inDto) throws DAOException {
		/*
		 * 업무기준 : 스카핑/정정보급LOT등록(자동) 대상재를 쿼리로 조회하여 등록 처리
		 *
		 * 수정자 : 임춘수
		 * 수정일 : 2009.11.11
		 */
		//JDTO변수 정의
		JDTORecord       recPara        = null;
		JDTORecord       jdtoRcd        = null;
		JDTORecord       recTemp        = null;
		JDTORecord       recInTemp        = null;
		JDTORecordSet    outRecSet      = null;
		//DAO 변수 정의
		YdPrepMtlDao	ydPrepMtlDao	= new YdPrepMtlDao();
		YdPrepSchDao	ydPrepSchDao	= new YdPrepSchDao();
		YdStockDao  ydStockDao  		= new YdStockDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "insCSlabSupPrepSchAuto";
		String		szOperationName		= "스카핑/정정보급LOT등록(자동)";
		String		szRtnMsg			= YdConstant.RETN_CD_SUCCESS;
		//로컬변수 정의
	    String      szYD_AIM_YD_GP		= null;
	    String      szYD_AIM_BAY_GP 	= null;
	    //String 		szSPOS_WLOC_CD		= null;
		//String		szARR_WLOC_CD		= null;
		int		intSUP_LOT_COUNT	= 0;
		int		intLOT_SH				= 0;
		int		intREAL_LOT_SH				= 0;
		String		szYD_STK_COL_GP		= null;
		String		szPREV_YD_STK_COL_GP		= null;
		String		szYD_PREP_SCH_ID	= null;
		String		szUserId			= null;
		String		szYD_SCH_CD			= null;
		String		szSTL_NO			= null;
		String 		szYD_STK_BED_NO		= null;
		String		szYD_STK_LYR_NO 	= null;
		//String		szYD_DONG_GP		= null;
		//String		szYD_COL_GP			= null;
		//String		szYD_EQP_ID			= null;
		//String		szMAKER_NAME		= null;
		//String		szCAR_GP			= null;
		long		lngYD_MTL_WT		= 0;
		long		lngYD_MTL_WT_SUM	= 0;
		boolean		bRtnVal				= false;
		boolean	bIsLoopable				= true;
		int intRtnVal = 0;
		int intRowNo					= 0;

		String		szYD_WRK_GP			= null;
		String		szYD_EQP_ID			= null;
		String		szYD_GP				= null;
		String		szYD_BAY_GP			= null;
		String		szYD_SPAN_GP		= null;
		String		szSPAN_SEARCH_GP	= null;
		String		szYD_AIM_RT_GP		= null;
		String		szRT_SEARCH_GP		= null;
		String		szWO_MSLAB_RPR_MTD	= null;
		//String		szLOT_YN			= null;
		String 		szSHEAR_SEARCH_GP	= null;
		String		szDATE_FROM			= null;
		String		szDATE_TO			= null;
		String		szHEAT_NO			= null;

		String		szRD_DATE_ALL		= null;

		try {
			szMsg = "[JSP Session : "+szOperationName+"] --------------------------- 메소드 시작 : 파라미터 확인 ---------------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			ydUtils.displayRecord(szOperationName, inDto);

			szUserId 				= ydDaoUtils.paraRecChkNull(inDto, "YD_USER_ID");
			szYD_WRK_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_WRK_GP");
			szYD_EQP_ID 			= ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_ID");				//야드설비구분
			szYD_GP 				= ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			szYD_BAY_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP");
			szYD_SPAN_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_SPAN_GP");
			szYD_AIM_RT_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_RT_GP");
			szWO_MSLAB_RPR_MTD 		= ydDaoUtils.paraRecChkNull(inDto, "WO_MSLAB_RPR_MTD");
			//szLOT_YN 				= ydDaoUtils.paraRecChkNull(inDto, "LOT_YN");
			szSHEAR_SEARCH_GP 		= ydDaoUtils.paraRecChkNull(inDto, "SHEAR_SEARCH_GP");
			szDATE_FROM 			= ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM");
			szDATE_TO 				= ydDaoUtils.paraRecChkNull(inDto, "DATE_TO");
			szHEAT_NO 				= ydDaoUtils.paraRecChkNull(inDto, "HEAT_NO");

			szRD_DATE_ALL			= ydDaoUtils.paraRecChkNull(inDto, "RD_DATE_ALL");

			if( !szYD_BAY_GP.equals("") ) {
				szYD_BAY_GP			= szYD_BAY_GP.substring(1, 2);
			}

			if( szYD_SPAN_GP.equals("") ) {
				szSPAN_SEARCH_GP = "1";
			}else{
				szSPAN_SEARCH_GP = "2";
			}

			if( szYD_AIM_RT_GP.equals("") ) {
				szRT_SEARCH_GP = "1";
			}else{
				szRT_SEARCH_GP = "2";
			}

			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
		    recPara.setField("YD_GP",     			szYD_GP);							//야드
		    recPara.setField("YD_BAY_GP",     		szYD_BAY_GP);						//동
		    recPara.setField("YD_SPAN_GP",     		szYD_SPAN_GP);						//스판
		    recPara.setField("SPAN_SEARCH_GP",     	szSPAN_SEARCH_GP);					//스판조회구분
		    recPara.setField("YD_AIM_RT_GP",     	szYD_AIM_RT_GP);					//목표행선
		    recPara.setField("RT_SEARCH_GP",     	szRT_SEARCH_GP);					//목표행선조회구분
		    recPara.setField("PAGE_NO",   			inDto.getField("PAGE_NO"));
		    recPara.setField("ROW_CNT",    			inDto.getField("PAGE_SIZE"));

		    if( szYD_WRK_GP.equals("SCARF")) {
		    	recPara.setField("WO_MSLAB_RPR_MTD",    szWO_MSLAB_RPR_MTD);				//주편손질방법
		    	intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 167);		//스카핑대상재조회
		    }else{
		    	if( szSHEAR_SEARCH_GP.equals("DATE")) {					//생산기한일자로 조회

		    		//------------------------------------------------------------------------
					//	날짜를 전체로 조회할 것인 지를 판단하는 변수 추가
					//	수정자 : 임춘수
					//	수정일 : 2010.01.29
					//------------------------------------------------------------------------
					szRD_DATE_ALL			= ydDaoUtils.paraRecChkNull(inDto, "RD_DATE_ALL");

					if( szRD_DATE_ALL.equals("Y")) {
						szDATE_FROM				= "00000000";
						szDATE_TO				= "99999999";
					}

					//------------------------------------------------------------------------

		    		recPara.setField("DATE_FROM",    	szDATE_FROM);
		    		recPara.setField("DATE_TO",    		szDATE_TO);
		    		recPara.setField("SEARCH_GP",    	"2");
		    	}else if( szSHEAR_SEARCH_GP.equals("HEAT")){			//HEAT_NO로 조회
		    		recPara.setField("HEAT_NO",    		szHEAT_NO);
		    		recPara.setField("SEARCH_GP",    	"3");
		    	}else if( szSHEAR_SEARCH_GP.equals("HCR")){				//HCR로 조회
		    		recPara.setField("HCR",    		"H");
		    		recPara.setField("SEARCH_GP",    	"1");
		    	}

		    	intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 168);		//정정대상재 조회

		    }
		    //---------
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session : "+szOperationName+"] 조회 시 오류발생[1] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "[JSP Session : "+szOperationName+"] 조회 시 오류발생[2] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return YdConstant.RETN_CD_FAILURE;
			} // end of if

			if( intRtnVal == 0 ) {
				return YdConstant.RETN_CD_NOTEXIST;
			}
			jdtoRcd = JDTORecordFactory.getInstance().create();
			/*
			 * 1-1-0. BRE Rule에서 스카핑/정정작업매수 조회
			 */
			//BRE Rule에서 데이터 가져오기
			 if( szYD_WRK_GP.equals("SCARF") ) {
				if( szYD_BAY_GP.equals("B") ) {
					//B동 스카핑보급 스케줄
					szYD_SCH_CD = "ABDP03UM";
				}else if( szYD_BAY_GP.equals("C") ) {
					//C동 스카핑보급 스케줄
					szYD_SCH_CD = "ACDP01UM";
				}
				
	    		//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
	    		if (szYD_GP.equals("M")) {
					szYD_SCH_CD = "MADP01UM";
	    		}

		    	bRtnVal = GetBreRule1.getYDB198(jdtoRcd);
		    	if( bRtnVal ) {
		    		intLOT_SH = ydDaoUtils.paraRecChkNullInt(jdtoRcd, "YD_EQP_WRK_SH");
		    		szMsg = "[JSP Session : "+szOperationName+"] 자동으로 스카핑보급LOT편성 시 야드설비작업매수 BRE Rule 조회 성공 - 야드설비작업매수["+intLOT_SH+"] ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	}else{
		    		intLOT_SH = 4;
		    		szMsg = "[JSP Session : "+szOperationName+"] 자동으로 스카핑보급LOT편성 시 야드설비작업매수 BRE Rule 조회 시 오류발생 - 야드설비작업매수 기본값["+intLOT_SH+"] 사용";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    	}
			} else {
				if ( szYD_BAY_GP.equals("A") ) {
					//A동 2차절단 보급 스케줄
		    		if ("AAPUP9".equals(szYD_EQP_ID)) {
						szYD_SCH_CD = "AAPU09UM";	//#2 2차절단
		    		} else if ("AAPUPA".equals(szYD_EQP_ID)) {
						szYD_SCH_CD = "AAPU10UM";	//#3 2차절단
		    		} else {
						szYD_SCH_CD = "AADP02UM";	//#1 2차절단
		    		}
		    		
		    		//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
		    		if (szYD_GP.equals("M")) {
						szYD_SCH_CD = "MADP01UM";
		    		}
				} else if( szYD_BAY_GP.equals("C") ) {
					//C동 2차절단 보급 스케줄
					szYD_SCH_CD = "ACDP01UM";		//#1 Scarfer(2차절단 공용)
				}

				bRtnVal = GetBreRule1.getYDB197(jdtoRcd);
		    	if( bRtnVal ) {
		    		intLOT_SH = ydDaoUtils.paraRecChkNullInt(jdtoRcd, "YD_EQP_WRK_SH");
		    		szMsg = "[JSP Session : "+szOperationName+"] 자동으로 정정보급LOT편성 시 야드설비작업매수 BRE Rule 조회 성공 - 야드설비작업매수["+intLOT_SH+"] ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	}else{
		    		intLOT_SH = 4;
		    		szMsg = "[JSP Session : "+szOperationName+"] 자동으로 정정보급LOT편성 시 야드설비작업매수 BRE Rule 조회 시 오류발생 - 야드설비작업매수 기본값[["+intLOT_SH+"]] 사용";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    	}
			}

			//화면에서 넘겨지는 배차대수를 대상재의 건수로 대체
			intSUP_LOT_COUNT = outRecSet.size();
			intRowNo = 1;
			for(int i = 1; i <= intSUP_LOT_COUNT; i++ ) {
				intREAL_LOT_SH = 0;
				lngYD_MTL_WT_SUM = 0;
				for(int j = 1; j <= intLOT_SH; j++ ) {
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 대상재 로우번호["+intRowNo+"] ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					if( outRecSet.size() < intRowNo ) {
						bIsLoopable = false;
						szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 대상재 로우번호["+intRowNo+"]가 대상재 건수["+outRecSet.size()+"]보다 큽니다. ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						break;
					}
					outRecSet.absolute(intRowNo);
					recPara = outRecSet.getRecord();
					szSTL_NO  = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
					szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
					//szYD_GP = szYD_STK_COL_GP.substring(0, 1);
					szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
					szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
					//szARR_WLOC_CD = ydDaoUtils.paraRecChkNull(recPara, "ARR_WLOC_CD");
					lngYD_MTL_WT = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");

					//szYD_AIM_YD_GP = YdCommonUtils.getYdFromWlocCd(szARR_WLOC_CD);

					lngYD_MTL_WT_SUM += lngYD_MTL_WT;
					//szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";
					//szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT0" + szYD_EQP_ID.substring(5, 6) + "UM";

					if( j == 1 ) {
						szYD_PREP_SCH_ID = ydPrepSchDao.getYdPrepschId();
						recTemp = JDTORecordFactory.getInstance().create();
						//준비스케줄 등록
						recTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
						recTemp.setField("YD_SCH_CD", szYD_SCH_CD);
						recTemp.setField("REGISTER", szUserId);
						recTemp.setField("YD_GP", szYD_GP);
						recTemp.setField("YD_PREP_WK_ST", "S");
						//recTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD);
						recTemp.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP);
						recTemp.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
						recTemp.setField("YD_CARASGN_SEQ", YdConstant.YD_CARASGN_SEQ_AUTO_DEFAULT);
						//recTemp.setField("YD_EQP_WRK_SH", "" + intLOT_SH);
						//recTemp.setField("YD_WRK_PLAN_CRN", szYD_EQP_ID);
						//recTemp.setField("CAR_GP", szCAR_GP);

						intRtnVal = ydPrepSchDao.insYdPrepsch(recTemp);

						if( intRtnVal < 0 ) {
							szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else if( intRtnVal == 0 ) {
							szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else{
							szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"] 등록 성공 ";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}

					}else{
						if( !szYD_STK_COL_GP.substring(0, 2).equals(szPREV_YD_STK_COL_GP.substring(0, 2)) ) {
							break;
						}
					}
					//준비재료 등록
					recTemp = JDTORecordFactory.getInstance().create();
					recTemp.setField("STL_NO", szSTL_NO);
					recTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
					recTemp.setField("REGISTER", szUserId);
					recTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
					recTemp.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
					recTemp.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);

					intRtnVal = ydPrepMtlDao.insYdPrepmtl(recTemp);

					if( intRtnVal < 0 ) {
						szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+j+"]번재료["+szSTL_NO+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if( intRtnVal == 0 ) {
						szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+j+"]번재료["+szSTL_NO+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+j+"]번재료["+szSTL_NO+"] 등록 성공 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					intREAL_LOT_SH++;
					szPREV_YD_STK_COL_GP = szYD_STK_COL_GP;
					intRowNo++;
				}


				if( intREAL_LOT_SH > 0 ) {
					recInTemp = JDTORecordFactory.getInstance().create();
					//준비스케줄수정
					recInTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
					recInTemp.setField("YD_EQP_WRK_SH", "" + intREAL_LOT_SH);
					recInTemp.setField("YD_INV_SUM_WT", "" + lngYD_MTL_WT_SUM);
					intRtnVal = ydPrepSchDao.updYdPrepsch(recInTemp, 0);
				}

				if( !bIsLoopable ) break;
			}

			szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 등록 성공 - 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of insCSlabSupPrepSchAuto

	/**
	 * 스카핑/정정보급LOT등록 - 수동
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String insCSlabSupPrepSchManual(JDTORecord[] inDto) throws DAOException {
		/*
		 * 업무기준 : 1. 그리드로 넘겨진 대상재들을 준비스케줄에 등록
		 * 				1-1. 준비스케줄 등록
		 * 				1-2. 준비재료 등록
		 *
		 * 수정자 : 임춘수
		 * 수정일 : 2009.11.11
		 */
		//JDTO변수 정의
		JDTORecord       recTemp        = null;
		//DAO 변수 정의
		YdPrepMtlDao	ydPrepMtlDao	= new YdPrepMtlDao();
		YdPrepSchDao	ydPrepSchDao	= new YdPrepSchDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "insCSlabSupPrepSchManual";
		String		szOperationName		= "스카핑/정정보급LOT등록(수동)";
		String		szRtnMsg			= YdConstant.RETN_CD_SUCCESS;
		//로컬변수 정의
		String		szYD_AIM_YD_GP		= null;
	    String      szYD_AIM_BAY_GP 	= null;
	    String		szARR_WLOC_CD		= null;
		int		intLOT_SH				= 0;
		String 		szYD_GP				= null;
		String		szYD_STK_COL_GP		= null;
		String		szYD_PREP_SCH_ID	= null;
		String		szUserId			= null;
		String		szYD_SCH_CD			= null;
		String		szSTL_NO			= null;
		String 		szYD_STK_BED_NO		= null;
		String		szYD_STK_LYR_NO 	= null;
		String		szYD_EQP_ID			= null;
		String		szCAR_GP			= null;
		long		lngYD_MTL_WT		= 0;
		long		lngYD_MTL_WT_SUM	= 0;
		String		szYD_WRK_GP			= null;
		String		szYD_BAY_GP			= null;

		int intRtnVal = 0;

		try {
			intLOT_SH = inDto.length;
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 - 대상재건수["+intLOT_SH+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			szUserId 		= ydDaoUtils.paraRecChkNull(inDto[0], "YD_USER_ID");
			szYD_WRK_GP = ydDaoUtils.paraRecChkNull(inDto[0], "YD_WRK_GP");
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(inDto[0], "YD_BAY_GP");
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(inDto[0], "YD_STK_COL_GP");
			//szARR_WLOC_CD 	= ydDaoUtils.paraRecChkNull(inDto[0], "ARR_WLOC_CD");
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(inDto[0], "YD_AIM_BAY_GP");
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(inDto[0], "YD_AIM_YD_GP");
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(inDto[0], "YD_EQP_ID");
			//szCAR_GP		= ydDaoUtils.paraRecChkNull(inDto[0], "CAR_GP");				//차량구분

			if( !szYD_BAY_GP.equals("") ) {
				szYD_BAY_GP			= szYD_BAY_GP.substring(1, 2);
			}

			if( szYD_WRK_GP.equals("SCARF") ) {
				if( szYD_BAY_GP.equals("B") ) {
					//B동 스카핑 보급 스케줄
					szYD_SCH_CD = "ABDP03UM";
				}else if( szYD_BAY_GP.equals("C") ) {
					//C동 스카핑 보급 스케줄
					szYD_SCH_CD = "ACDP01UM";
				}
			} else {
				if ( szYD_BAY_GP.equals("A") ) {
					//A동 2차절단 보급 스케줄
		    		if ("AAPUP9".equals(szYD_EQP_ID)) {
						szYD_SCH_CD = "AAPU09UM";	//#2 2차절단
		    		} else if ("AAPUPA".equals(szYD_EQP_ID)) {
						szYD_SCH_CD = "AAPU10UM";	//#3 2차절단
		    		} else {
						szYD_SCH_CD = "AADP02UM";	//#1 2차절단
		    		}
		    		
		    		//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
		    		if (szYD_GP.equals("M")) {
						szYD_SCH_CD = "MADP01UM";
		    		}
				} else if( szYD_BAY_GP.equals("C") ) {
					//C동 2차절단 보급 스케줄
					szYD_SCH_CD = "ACDP01UM";		//#1 Scarfer(2차절단 공용)
				}
			 }

			szMsg = "[JSP Session : "+szOperationName+"] 작업구분["+szYD_WRK_GP+"], 동구분["+szYD_BAY_GP+"]에 따른 스케줄코드["+szYD_SCH_CD+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			szYD_GP = szYD_STK_COL_GP.substring(0, 1);
			//szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";
			//szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT0" + szYD_EQP_ID.substring(5, 6) + "UM";
			//준비스케줄 등록
			szYD_PREP_SCH_ID = ydPrepSchDao.getYdPrepschId();
			recTemp = JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
			recTemp.setField("YD_SCH_CD", szYD_SCH_CD);
			recTemp.setField("REGISTER", szUserId);
			recTemp.setField("YD_GP", szYD_GP);
			recTemp.setField("YD_PREP_WK_ST", "S");
			//recTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD);
			recTemp.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP);
			recTemp.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
			recTemp.setField("YD_CARASGN_SEQ", YdConstant.YD_CARASGN_SEQ_MAN_DEFAULT);
			recTemp.setField("YD_EQP_WRK_SH", "" + intLOT_SH);
			//recTemp.setField("YD_WRK_PLAN_CRN", szYD_EQP_ID);
			//recTemp.setField("CAR_GP", szCAR_GP);

			intRtnVal = ydPrepSchDao.insYdPrepsch(recTemp);

			if( intRtnVal < 0 ) {
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}else if( intRtnVal == 0 ) {
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}else{
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 등록 성공 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}

			for(int i = 0; i < inDto.length; i++ ) {

				szSTL_NO  		= ydDaoUtils.paraRecChkNull(inDto[i], "STL_NO");
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(inDto[i], "YD_STK_COL_GP");
				szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inDto[i], "YD_STK_BED_NO");
				szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(inDto[i], "YD_STK_LYR_NO");
				lngYD_MTL_WT 	= ydDaoUtils.paraRecChkNullLong(inDto[i], "YD_MTL_WT");

				lngYD_MTL_WT_SUM += lngYD_MTL_WT;

				//준비재료 등록
				recTemp = JDTORecordFactory.getInstance().create();
				recTemp.setField("STL_NO", szSTL_NO);
				recTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
				recTemp.setField("REGISTER", szUserId);
				recTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
				recTemp.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
				recTemp.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);

				intRtnVal = ydPrepMtlDao.insYdPrepmtl(recTemp);

				if( intRtnVal < 0 ) {
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+(i + 1)+"]번재료["+szSTL_NO+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if( intRtnVal == 0 ) {
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+(i + 1)+"]번재료["+szSTL_NO+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+(i + 1)+"]번재료["+szSTL_NO+"] 등록 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}

			recTemp = JDTORecordFactory.getInstance().create();
			//준비스케줄수정
			recTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
			recTemp.setField("YD_INV_SUM_WT", "" + lngYD_MTL_WT_SUM);
			intRtnVal = ydPrepSchDao.updYdPrepsch(recTemp, 0);

			szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 등록 성공 - 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of insCSlabSupPrepSchManual





	/**
	 *  설비휴지테이블에 등록 (팝업)
	 *
	 *	권오창
	 *  2009.11.12
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet InsEqpPauseHist(JDTORecord inDto) throws DAOException {
		// DAO 및 UTIL 객체 생성
		YdEqpDao ydEqpDao               = new YdEqpDao();
		YdEqpPauseDao ydEqpPauseDao     = new YdEqpPauseDao();


		// 레코드 선언
		JDTORecord recPara              = null;
		JDTORecord recUpPara            = null;
		JDTORecord recGetVal            = null;
		JDTORecord setCrnschRecord      = null;
		JDTORecordSet rsResult          = null;
		JDTORecordSet outRecSet         = null;


		// 변수 선언
		String szMethodName             = "InsEqpPauseHist";
		String szMsg                    = "";
		String szYD_EQP_ID              = "";
		String szYD_EQP_STAT            = "";
		String szYD_EQP_CURR_STAT       = "";
		String szYD_EQP_PAUSE_CODE      = "";
		String szYD_EQP_PAUSE_RCVR_CNTS = "";
		String szYD_EQP_PAUSE_OCC_DT    = "";
		String szYD_EQP_TRBL_RCVR_DT    = "";
		String szYD_EQP_PAUSE_OCCR_SEQ  = "";
		String szREGISTER               = "";
		int nRet                        = 0;


		try {
			// 레코드 생성
			recPara         = JDTORecordFactory.getInstance().create();
			recUpPara       = JDTORecordFactory.getInstance().create();
			setCrnschRecord = JDTORecordFactory.getInstance().create();
			rsResult        = JDTORecordFactory.getInstance().createRecordSet("");
			outRecSet       = JDTORecordFactory.getInstance().createRecordSet("");


			szMsg = "JSP-SESSION [설비 휴지테이블에 등록] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			// 항목 정합성 검사
			szYD_EQP_ID              = yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"), "");
			if(szYD_EQP_ID.equals("")){
				szMsg = "설비휴지테이블에 등록처리 에러 : 설비ID값이 않습니다. YD_EQP_ID(" + szYD_EQP_ID + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;
			}

			szYD_EQP_STAT            = yddatautil.setDataDefault(inDto.getField("YD_EQP_STAT"), "");
			if(szYD_EQP_STAT.equals("")){
				szMsg = "설비휴지테이블에 등록처리 에러 : 설비상태 값이 않습니다. YD_EQP_STAT(" + szYD_EQP_STAT + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;
			}

			szYD_EQP_PAUSE_CODE      = yddatautil.setDataDefault(inDto.getField("YD_EQP_PAUSE_CODE"), "");
			if(szYD_EQP_PAUSE_CODE.trim().equals("")){
				szYD_EQP_PAUSE_CODE = szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK) ? "STOP" : "0000";
			}

			szYD_EQP_PAUSE_RCVR_CNTS = yddatautil.setDataDefault(inDto.getField("YD_EQP_PAUSE_RCVR_CNTS"), "");
			szREGISTER               = yddatautil.setDataDefault(inDto.getField("YD_USER_ID"), "");
			szYD_EQP_TRBL_RCVR_DT    = YdUtils.getCurDate("yyyyMMddHHmmss");


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
	        } else if(nRet == 0){
    			// 1차
    			szYD_EQP_PAUSE_OCCR_SEQ = ydUtils.IncreaseStrToInt("0", 1, 18);

    			// 레코드 편성
    			setCrnschRecord = JDTORecordFactory.getInstance().create();
                setCrnschRecord.setField("REGISTER"              , szREGISTER);                  // 등록자
                setCrnschRecord.setField("MODIFIER"              , szREGISTER);                  // 수정자
                setCrnschRecord.setField("DEL_YN"                , "N");                         // 삭제유무
                setCrnschRecord.setField("YD_EQP_ID"             , szYD_EQP_ID);                 // 설비ID
    	        setCrnschRecord.setField("YD_EQP_PAUSE_OCCR_SEQ" , szYD_EQP_PAUSE_OCCR_SEQ);     // 1차
    	        setCrnschRecord.setField("YD_EQP_PAUSE_CODE"     , szYD_EQP_PAUSE_CODE);         // 설비휴지코드
    	        setCrnschRecord.setField("YD_EQP_PAUSE_OCC_DT"   , szYD_EQP_TRBL_RCVR_DT);       // 야드설비휴지발생일시
    	        setCrnschRecord.setField("YD_EQP_PAUSE_RCVR_CNTS", szYD_EQP_PAUSE_RCVR_CNTS);    // 야드설비휴지복구내용

    	        // 설비휴지테이블 ISNERT
    	        nRet = ydEqpPauseDao.insYdEqppause(setCrnschRecord);
    			if(nRet < 0){
    	        	szMsg = "설비 휴지테이블 INSERT 중  Error : " + nRet + " : YD_EQP_ID(" + szYD_EQP_ID + ") YD_EQP_PAUSE_OCCR_SEQ(" + szYD_EQP_PAUSE_OCCR_SEQ + ") YD_EQP_PAUSE_CODE(" + szYD_EQP_PAUSE_CODE + ") YD_EQP_TRBL_RCVR_DT(" + szYD_EQP_TRBL_RCVR_DT + ")";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    	        }
	        } else {
		        rsResult.first();
		        recGetVal = rsResult.getRecord();


		        szYD_EQP_PAUSE_OCCR_SEQ = ydDaoUtils.paraRecChkNull(recGetVal, "YD_EQP_PAUSE_OCCR_SEQ");
		        szYD_EQP_CURR_STAT      = ydDaoUtils.paraRecChkNull(recGetVal, "YD_EQP_STAT");
		        szYD_EQP_PAUSE_OCC_DT   = ydDaoUtils.paraRecChkNull(recGetVal, "YD_EQP_PAUSE_OCC_DT");


				if(szYD_EQP_STAT.equals(szYD_EQP_CURR_STAT) || (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_NORM) && (szYD_EQP_CURR_STAT.equals(YdConstant.YD_EQP_STAT_NORM) || szYD_EQP_CURR_STAT.equals(YdConstant.YD_EQP_STAT_IDLE))) || (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_IDLE) && (szYD_EQP_CURR_STAT.equals(YdConstant.YD_EQP_STAT_NORM) || szYD_EQP_CURR_STAT.equals(YdConstant.YD_EQP_STAT_IDLE)))){
					// 현재 고장인 상태인데 고장으로 설정했을 경우는 시간은 업데이트하면 안되고 일단 사유만 업데이트
					// 현재 복구인 상태인데 복구로 설정했을 경우는 시간은 업데이트하면 안되고 일단 사유만 업데이트
	                setCrnschRecord.setField("YD_EQP_ID"             , szYD_EQP_ID);                // 설비ID
	                setCrnschRecord.setField("MODIFIER"              , szREGISTER);                 // 수정자
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_OCCR_SEQ" , szYD_EQP_PAUSE_OCCR_SEQ);    // 해당 차수
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_RCVR_CNTS", szYD_EQP_PAUSE_RCVR_CNTS);   // 야드설비휴지복구내용

	    	        // 설비휴지테이블 UPDATE
	        		nRet = ydEqpPauseDao.updYdEqppause(setCrnschRecord, 0);
	    	        if(nRet <= 0){
	                    szMsg = "설비 휴지테이블 업데이트 중  Error : " + nRet;
	                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    	        }
				} else if(szYD_EQP_CURR_STAT.equals(YdConstant.YD_EQP_STAT_BREAK) && (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_NORM) || szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_IDLE))){
					// 현재 고장인 상태인데 복구로 변경할 경우 해당차수에 업데이트
	                setCrnschRecord.setField("YD_EQP_ID"             , szYD_EQP_ID);                // 설비ID
	                setCrnschRecord.setField("MODIFIER"              , szREGISTER);                 // 수정자
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_OCCR_SEQ" , szYD_EQP_PAUSE_OCCR_SEQ);    // 해당 차수
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_CODE"     , szYD_EQP_PAUSE_CODE);        // 설비휴지코드
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_END_DT"   , szYD_EQP_TRBL_RCVR_DT);      // 야드설비휴지종료일시
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_OCC_DT"   , szYD_EQP_PAUSE_OCC_DT);      // 야드설비휴지발생일시(차를 계산하기 위함)
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_RCVR_CNTS", szYD_EQP_PAUSE_RCVR_CNTS);   // 야드설비휴지복구내용

	    	        // 설비휴지테이블 UPDATE
	        		nRet = ydEqpPauseDao.updYdEqpPauseRepair(setCrnschRecord);
	    	        if(nRet <= 0){
	                    szMsg = "설비 휴지테이블 업데이트 중  Error : " + nRet;
	                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    	        }
				} else if((szYD_EQP_CURR_STAT.equals(YdConstant.YD_EQP_STAT_NORM) || szYD_EQP_CURR_STAT.equals(YdConstant.YD_EQP_STAT_IDLE)) && szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)){
					// 현재 복구인 상태인데 고장로 변경할 경우 해당차수에 업데이트

					// 해당차수를 1증가 처리
	    			szYD_EQP_PAUSE_OCCR_SEQ = ydUtils.IncreaseStrToInt(szYD_EQP_PAUSE_OCCR_SEQ, 1, 18);

	    			// 레코드 편성
	                setCrnschRecord.setField("REGISTER"              , szREGISTER);                 // 등록자
	                setCrnschRecord.setField("MODIFIER"              , szREGISTER);                 // 수정자
	                setCrnschRecord.setField("DEL_YN"                , "N");                        // 삭제유무
	                setCrnschRecord.setField("YD_EQP_ID"             , szYD_EQP_ID);                // 설비ID
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_OCCR_SEQ" , szYD_EQP_PAUSE_OCCR_SEQ);    // 차수 + 1
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_CODE"     , szYD_EQP_PAUSE_CODE);        // 설비휴지코드
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_OCC_DT"   , szYD_EQP_TRBL_RCVR_DT);      // 야드설비휴지발생일시
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_RCVR_CNTS", szYD_EQP_PAUSE_RCVR_CNTS);   // 야드설비휴지복구내용

	    	        // 설비휴지테이블 ISNERT
	    	        nRet = ydEqpPauseDao.insYdEqppause(setCrnschRecord);
	    			if(nRet < 0){
	    	        	szMsg = "설비 휴지테이블 INSERT 중  Error : " + nRet + " : YD_EQP_ID(" + szYD_EQP_ID + ") YD_EQP_PAUSE_OCCR_SEQ(" + szYD_EQP_PAUSE_OCCR_SEQ + ") YD_EQP_PAUSE_CODE(" + szYD_EQP_PAUSE_CODE + ") YD_EQP_TRBL_RCVR_DT(" + szYD_EQP_TRBL_RCVR_DT + ")";
	                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    	        }
				}
	        }

	        outRecSet.addRecord(setCrnschRecord);


			//============================================================================
	        // 설비테이블에 야드설비상태 업데이트
			//============================================================================
			ydUtils.putLog(szSessionName, szMethodName, "설비 테이블에 업데이트 처리", YdConstant.DEBUG);
			recUpPara.setField("YD_EQP_ID"  , szYD_EQP_ID);       // 설비ID
	        recUpPara.setField("YD_EQP_STAT", szYD_EQP_STAT);     // "B": 고장, "N": 복구
	        recUpPara.setField("MODIFIER"	, szREGISTER);
    		nRet = ydEqpDao.updYdEqp(recUpPara, 0);
			switch(nRet){
				case 0 :
				    szMsg = "No Data Found!!!, ErrorCode:" + nRet;
				    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				case -1	:
				    szMsg = "Dup_val_on_index!!!, ErrorCode:" + nRet;
				    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				case -2	:
				    szMsg = "Parameter Error!!!, ErrorCode:" + nRet;
				    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				case -3	:
				    szMsg = "Execution Failed!!!, ErrorCode:" + nRet;
			        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szSessionName + e.getMessage(), e);
		} finally {
		}

		szMsg = "JSP-SESSION [설비 휴지테이블에 등록] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	} // end of InsEqpPauseHist


	/**
	 * 상하차 작업실적 등록화면 조회쿼리 1
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdCrnSchLdUdWrkMgt1(JDTORecord inDto) throws DAOException {

		int intRtnVal = 0;
		String szMsg ="";
		String szLogMsg = "";
		String szMethodName ="getYdCrnSchLdUdWrkMgt1";
		String szOperationName = "상하차 작업실적 등록화면 조회쿼리 1";

		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		try {
			szLogMsg = "JSP-SESSION ["+ szOperationName +" ]  시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);


			recPara.setField("YD_GP", 	ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
			recPara.setField("YD_EQP_ID", 	ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_ID"));
			recPara.setField("YD_SCH_CD", 	ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_CD"));
			recPara.setField("PAGE_CNT", 	ydDaoUtils.paraRecChkNull(inDto, "PAGE_NO"));
			recPara.setField("ROW_CNT", 	ydDaoUtils.paraRecChkNull(inDto, "ROWCOUNT"));


			YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();

			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 49);


			if (intRtnVal < 0) {
				szMsg = szOperationName +" 조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				return outRecSet;

			 } // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szLogMsg = "JSP-SESSION ["+ szOperationName +" ]  끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		return outRecSet;
	}


	/**
	 * 상하차 작업실적 등록화면 조회쿼리 2
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdCrnSchLdUdWrkMgt2(JDTORecord inDto) throws DAOException {

		int intRtnVal = 0;
		String szMsg ="";
		String szLogMsg = "";
		String szMethodName ="getYdCrnSchLdUdWrkMgt2";
		String szOperationName = "상하차 작업실적 등록화면 조회쿼리 2";

		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		try {
			szLogMsg = "JSP-SESSION ["+ szOperationName +" ]  시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			recPara.setField("YD_WBOOK_ID", 	ydDaoUtils.paraRecChkNull(inDto, "YD_WBOOK_ID"));

			YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 50);

			if (intRtnVal < 0) {
				szMsg = szOperationName +" 조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				return outRecSet;

			 } // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szLogMsg = "JSP-SESSION ["+ szOperationName +" ]  끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		return outRecSet;
	}


	/**
	 * 디파일러 베드 조회 (날판번호 포함)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getDepilerBed(JDTORecord inDto) throws DAOException {

		int intRtnVal = 0;
		String szMsg ="";
		String szLogMsg = "";
		String szMethodName = "getDepilerBed";
		String szOperationName = "디파일러 베드 조회";

		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		try {
			szLogMsg = "JSP-SESSION ["+ szOperationName +" ]  시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			recPara.setField("YD_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_GP"));
			recPara.setField("YD_STK_BED_NO", 	ydDaoUtils.paraRecChkNull(inDto, "BED_NO"));


			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 84);

			if (intRtnVal < 0) {
				szMsg =  "JSP-SESSION ["+  szOperationName +" 조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				return outRecSet;

			 } // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szLogMsg = "JSP-SESSION ["+ szOperationName +" ]  끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		return outRecSet;
	}


	/**
	 * A 후판 TAKE - IN
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String updTakeInPlateYd(JDTORecord[] inDto) throws DAOException {
		/*
		 *
		 *
		 *
		 */
		//JDTO변수 정의

		JDTORecord       recPara        = null;

		EJBConnector ejbConn = null;


		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "updTakeInPlateYd";
		String		szOperationName		= "A 후판 TAKE - IN";
		String		szRtnMsg			= YdConstant.RETN_CD_SUCCESS;

		int intLOT_SH = 0;
		int nCount = 0;
		String szStlNo = null;

		try {

			intLOT_SH = inDto.length;

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


			//A 후판 TAKE - IN 전문 편성
			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("TC_CODE", 			"Y3YDL013");
			recPara.setField("YD_EQP_ID", 			inDto[0].getField("YD_STK_COL_GP"));
			recPara.setField("YD_STK_BED_NO",       inDto[0].getField("YD_STK_BED_NO"));
			//recPara.setField("STL_NO",              inDto[0].getField("STL_NO"));
			recPara.setField("CARRY_IN_REQ_GP",     "N");


			//재료번호 전문 형식 편성
			for(int x = 1 ; x <=  4  ; x++ ){  //상수작업

				if(x < intLOT_SH){  //들어온 데이터 개수
					szStlNo = ydDaoUtils.paraRecChkNull(inDto[inDto.length- x], "STL_NO");

					if(!szStlNo.equals("")){
						//재료번호가 있는 가장 상단 데이터 정보를 재료번호로 넣어준다.
						//마지막 재료가 들어가게된다.
						 recPara.setField("STL_NO", szStlNo);

						//재료 번호가 있는 경우에만 매수를 증가시켜준다.
						nCount++;

					}

					recPara.setField("STL_NO" + x,          szStlNo);

				}else{
					//전문개수보다 적개 들어올경우 빈 재료번호를 넣기 위함
					recPara.setField("STL_NO" + x,         "");
				}
			}

			//TAKE - IN 전문은 설비에 들어가지 않은 재료남은부분에 대해서만 전송해준다.

			if (nCount == 0 ){

				szMsg = "[JSP Session : "+szOperationName+"] TAKE 재료가 존재하지 않습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return szMsg;
			}
			recPara.setField("YD_STK_BED_STL_SH",    new Integer(nCount - 1));

			szMsg = "[JSP Session : "+szOperationName+"] 전문편성완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



			ydUtils.displayRecord(szOperationName, recPara);

			szMsg = "[JSP Session : "+szOperationName+"] EJB 호출 [ procY3TakeInCmpl] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


			ejbConn = new EJBConnector("default", this);
			ejbConn.trx("IssueWrkDmdSeEJB", "procY3TakeInCmpl", recPara);


			szMsg = "[JSP Session : "+szOperationName+"] EJB 호출 [ procY3TakeInCmpl] 완료(Return Void) ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


			szMsg = "[JSP Session : "+szOperationName+"]  메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of updTakeInPlateYd



	/**
	 * A 후판 보급요구
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String updSubReqPlateYd(JDTORecord[] inDto) throws DAOException {

		//JDTO변수 정의
		JDTORecord       recPara        = null;
		JDTORecord       recTemp        = null;

		//JDTORecordSet 변수 정의
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

		EJBConnector ejbConn = null;

		// 적치 베드 DAO
		YdStkBedDao ydStkBedDao = new YdStkBedDao();

		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "updSubReqPlateYd";
		String		szOperationName		= "후판 보급요구";
		String		szRtnMsg			= YdConstant.RETN_CD_SUCCESS;
		String szBedWhioStat = null;

		int nRtnVal = 0;

		try {


			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			String szYD_BAY_GP = ydDaoUtils.paraRecChkNull(inDto[0], "YD_STK_COL_GP");
			szYD_BAY_GP = szYD_BAY_GP.substring(1, 2);
			
			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", 		inDto[0].getField("YD_STK_COL_GP"));
			recPara.setField("YD_STK_BED_NO",       inDto[0].getField("YD_STK_BED_NO"));

			nRtnVal = ydStkBedDao.getYdStkbed(recPara, outRecSet, 0);

			if(nRtnVal < 0){
				szMsg = "[JSP Session : "+szOperationName+"] 베드정보 조회 ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				return szRtnMsg;

			} else if( nRtnVal == 0){
				szMsg = "[JSP Session : "+szOperationName+"] 베드정보 조회된 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				return szRtnMsg;
			} else{
				szMsg = "[JSP Session : "+szOperationName+"] 베드정보 조회 정보 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			}


			recTemp = JDTORecordFactory.getInstance().create();

			outRecSet.first();
			recTemp = outRecSet.getRecord();

			szBedWhioStat = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT");

			szMsg = "[JSP Session : "+szOperationName+"] 입출고 상태는[ " + szBedWhioStat +"] 입니다. ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			//A 후판 보급요구 전문 편성
			recPara   = JDTORecordFactory.getInstance().create();
			if ("B".equals(szYD_BAY_GP)) {
				recPara.setField("TC_CODE", 			 "YDYDJ497");
			} else {
				recPara.setField("TC_CODE", 			 "YDYDJ237");
			}
			recPara.setField("YD_EQP_ID", 			 inDto[0].getField("YD_STK_COL_GP"));
			recPara.setField("YD_STK_BED_NO",        inDto[0].getField("YD_STK_BED_NO"));
			recPara.setField("YD_AIM_RT_GP",         YdConstant.AR_WRK_WAIT_A_MILL);
			recPara.setField("YD_STK_BED_WHIO_STAT", szBedWhioStat);

			szMsg = "[JSP Session : "+szOperationName+"] 전문편성완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			ejbConn = new EJBConnector("default", this);
			if ("B".equals(szYD_BAY_GP)) {
				ejbConn.trx("IssueWrkDmdSeEJB", "procBPlRefurSupLotComp", recPara);
			} else {
				ejbConn.trx("IssueWrkDmdSeEJB", "procAPlRefurSupLotComp", recPara);
			}

			szMsg = "[JSP Session : "+szOperationName+"]  메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of updSubReqPlateYd

	/**
	 * 압연지시관리 (조회)_A후판슬라브야드
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getslabYdRollCmdRef_aPlate(JDTORecord inDto) throws DAOException {

		// ERROR CHECK
		int intRtnVal = 0;

		// Log Message
		String szMsg ="";
		String szMethodName="getslabYdRollCmdRef_aPlate";



		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		YdStockDao ydStockDao = new YdStockDao();

		//조회되는 공장과 압연지시 공장구분이 다르므로 변환하여줌

		try {

			szMsg = "JSP-SESSION [압연지시관리 (조회)_A후판슬라브야드] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		    //recPara.setField("YD_GP",              szMillPlntGp );
			recPara.setField("PAGE_CNT1",          inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",           inDto.getField("PAGE_SIZE"));
			recPara.setField("PAGE_CNT2",          inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT2",           inDto.getField("PAGE_SIZE"));

			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 181);


			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return outRecSet;
			} // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [압연지시관리 (조회)_A후판슬라브야드] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		return outRecSet;
	}//end of getslabYdRollCmdRef_aPlate



	/**
	 * A 후판 준비LOT취소
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String delDepilerSupPrepSch(JDTORecord[] inDto) throws DAOException {

		//JDTO변수 정의
		JDTORecord       recPara        = null;
		JDTORecordSet 	 outRecSet 		= null;
		JDTORecord       recPrep        = null;
		// 준비스케줄  DAO
		YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();
		YdPrepMtlDao ydPrepMtlDao = new YdPrepMtlDao();
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		YdCrnSchDao  ydCrnSchDao  = new YdCrnSchDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();

		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "delDepilerSupPrepSch";
		String		szOperationName		= "A 후판 준비LOT취소";
		String		szRtnMsg			= YdConstant.RETN_CD_SUCCESS;

		String      szYdWbookId         = "";

		int nRtnVal = 0;

		try {


			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


			//준비스케줄 삭제


			recPara   = JDTORecordFactory.getInstance().create();
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

			szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 조회";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			recPara.setField("YD_PREP_SCH_ID", 		inDto[0].getField("YD_PREP_SCH_ID"));

			nRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 0);

			if(nRtnVal < 0){
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 조회 ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				return szRtnMsg;

			} else if( nRtnVal == 0){
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				return szRtnMsg;
			} else{
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 조회 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			}

			recPrep   = JDTORecordFactory.getInstance().create();
			outRecSet.first();
			recPrep = outRecSet.getRecord();

			szYdWbookId = ydDaoUtils.paraRecChkNull(recPrep, "YD_WBOOK_ID");

			if (!"".equals(szYdWbookId)) {

				szMsg = "[JSP Session : "+szOperationName+"] 작업예약이 존재하여 작업예약 삭제시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID", 		szYdWbookId);

				outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

				// com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbook
				nRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 28);

				if(nRtnVal < 0){
					szMsg = "[JSP Session : "+szOperationName+"] 작업예약조회 ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					szRtnMsg = szMsg;
					return szRtnMsg;

				} else if( nRtnVal == 0){
					szMsg = "[JSP Session : "+szOperationName+"] 스케줄 편성이 되지않았습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				} else{
					szMsg = "[JSP Session : "+szOperationName+"] 스케줄이 이미 편성되었습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					szRtnMsg = "준비스케줄이 이미 크레인스케줄 편성되었습니다.";
					return szRtnMsg;
				}

				//작업예약 재료 삭제
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID", szYdWbookId);
				recPara.setField("MODIFIER",inDto[0].getField("YD_USER_ID"));
				recPara.setField("DEL_YN","Y");
				nRtnVal = ydWrkbookMtlDao.updYdWrkbookmtl1(recPara);

				if(nRtnVal < 0){
					szMsg="작업예약재료  삭제시 오류 발생 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return szMsg;
				}




				//작업예약 삭제

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID", szYdWbookId);
				recPara.setField("MODIFIER",inDto[0].getField("YD_USER_ID"));
				recPara.setField("DEL_YN","Y");
				nRtnVal = ydWrkbookDao.updYdWrkbook(recPara, 0);

				if(nRtnVal < 0){
					szMsg="작업예약  삭제시 오류 발생 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return szMsg;
				}

				szMsg = "[JSP Session : "+szOperationName+"] 작업예약이 존재하여 작업예약 삭제완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


			}

			szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 삭제시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_PREP_SCH_ID", 		inDto[0].getField("YD_PREP_SCH_ID"));
			recPara.setField("YD_WBOOK_ID", szYdWbookId);
			recPara.setField("MODIFIER",inDto[0].getField("YD_USER_ID"));
			recPara.setField("DEL_YN","Y");

			nRtnVal = ydPrepSchDao.updDelYdPrepsch(recPara);

			if(nRtnVal < 0){
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 삭제 ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				return szRtnMsg;

			} else if( nRtnVal == 0){
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				return szRtnMsg;
			} else{
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 삭제 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			}

			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_PREP_SCH_ID", 		inDto[0].getField("YD_PREP_SCH_ID"));
			recPara.setField("MODIFIER",inDto[0].getField("YD_USER_ID"));
			recPara.setField("DEL_YN","Y");

			nRtnVal = ydPrepMtlDao.uptDelYdPrepmtlByPrepSchId(recPara);

			if(nRtnVal < 0){
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄재료 삭제 ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				return szRtnMsg;

			} else if( nRtnVal == 0){
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄재료 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				return szRtnMsg;
			} else{
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄재료 삭제 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			}

			szMsg = "[JSP Session : "+szOperationName+"]  메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of delDepilerSupPrepSch


	/**
	 * A 후판 준비크레인변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String updDepilerSupPrepSchWrkPlnCrn(JDTORecord[] inDto) throws DAOException {

		//JDTO변수 정의
		JDTORecord       recPara        = null;

		// 준비스케줄  DAO
		YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();
		YdPrepMtlDao ydPrepMtlDao = new YdPrepMtlDao();


		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "updDepilerSupPrepSchWrkPlnCrn";
		String		szOperationName		= "A 후판 준비크레인변경";
		String		szRtnMsg			= YdConstant.RETN_CD_SUCCESS;

		int nRtnVal = 0;

		try {


			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


			//준비크레인 변경

			szMsg = "[JSP Session : "+szOperationName+"] 준비크레인 변경";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_PREP_SCH_ID", 		    inDto[0].getField("YD_PREP_SCH_ID"));
			recPara.setField("YD_WRK_PLAN_CRN", 		inDto[0].getField("YD_WRK_PLAN_CRN"));
			recPara.setField("MODIFIER",                inDto[0].getField("YD_USER_ID"));

			nRtnVal = ydPrepSchDao.updYdPrepsch(recPara, 0);

			if(nRtnVal < 0){
				szMsg = "[JSP Session : "+szOperationName+"] 준비크레인 변경 ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				return szRtnMsg;

			} else if( nRtnVal == 0){
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				return szRtnMsg;
			} else{
				szMsg = "[JSP Session : "+szOperationName+"] 준비크레인 변경 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			}


			szMsg = "[JSP Session : "+szOperationName+"]  메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of updDepilerSupPrepSchWrkPlnCrn

	/**
	 * 슬라브야드 Depilier 장입 메뉴얼 작업지시 편성
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String slabYdDepilerManualReq(JDTORecord[] inDto) throws DAOException {

		// 준비스케줄  DAO
		YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();

		int intSh             = 0;
		String szMsg          = null;
		String szMethodName   = null;
		String [] strArrStlNo = null;
		String [] strPlMplNo  = null;
		String szRtnMsg       = YdConstant.RETN_CD_SUCCESS;
		String szYD_PREP_SCH_ID = null;

		JDTORecord    recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		JDTORecord    recPrep = JDTORecordFactory.getInstance().create();

		int nRtnVal = 0;

		szMsg        = "";
		szMethodName = "slabYdDepilerManualReq";
		String szOperationName = "Depilier 장입 메뉴얼 작업지시 편성";
		String strTemp = "";

		try {

			szMsg = "JSP-SESSION [슬라브야드 Depilier 장입 메뉴얼 작업지시 편성] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//저장위치 좌표설정화면 BED 수정

			strArrStlNo = new String[inDto.length];
			strPlMplNo  = new String[inDto.length];

			for(int x=0;x<inDto.length;x++){
				szYD_PREP_SCH_ID = inDto[x].getFieldString("YD_PREP_SCH_ID");
				strArrStlNo[x] = inDto[x].getFieldString("STL_NO");
				strPlMplNo[x] = inDto[x].getFieldString("REFUR_CHG_PLN_SERNO");
			}

			//장입일련번호순으로 Sort
			for(int k=0;k<inDto.length-1;k++){
				for(int j=0;j<inDto.length - (k + 1);j++){
					if (j+1 < inDto.length) {
						if(Integer.parseInt(strPlMplNo[j].substring(2)) > Integer.parseInt(strPlMplNo[j+1].substring(2))) {
							strTemp = strArrStlNo[j];
							strArrStlNo[j] = strArrStlNo[j+1];
							strArrStlNo[j+1] = strTemp;

							strTemp = strPlMplNo[j];
							strPlMplNo[j] = strPlMplNo[j+1];
							strPlMplNo[j+1] = strTemp;
						}
					}
				}
			}

			recPara.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);

			nRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 23);

			if(nRtnVal < 0){
				szMsg = "[JSP Session : "+szMethodName+"] 준비스케줄 정보 ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				return szRtnMsg;

			} else if( nRtnVal == 0){
				szMsg = "[JSP Session : "+szMethodName+"] 준비스케줄 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				return szRtnMsg;
			} else{
				szMsg = "[JSP Session : "+szMethodName+"] 준비크레인 정보 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			}

			outRecSet.first();
			recPrep = outRecSet.getRecord();

			//YD_SCH_CD
			recPara.setField("YD_SCH_CD", ydDaoUtils.paraRecChkNull(recPrep, "YD_SCH_CD"));

			//YD_STK_COL_GP
			recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recPrep, "YD_STK_COL_GP"));

			//YD_STK_BED_NO
			recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(recPrep, "YD_STK_BED_NO"));

			//YD_SH [매수]
			//recPara.setField("SLAB_SH", ydDaoUtils.paraRecChkNull(recPrep, "YD_EQP_WRK_SH"));
			recPara.setField("SLAB_SH", String.valueOf(inDto.length));

			//intSh = Integer.valueOf(ydDaoUtils.paraRecChkNull(recPrep, "YD_EQP_WRK_SH")).intValue();
			intSh = inDto.length;

			ydUtils.displayRecord(szOperationName, recPara);

			for(int Loopi=0 ; Loopi<intSh ;Loopi++){
				//재료번호
				//STL_NO []
				recPara.setField("STL_NO"+(Loopi+1), strArrStlNo[Loopi]);

				//권상 모음순서
				//YD_UP_COLL_SEQ []
				recPara.setField("YD_UP_COLL_SEQ"+(Loopi+1),""+(intSh-Loopi));
			}


			ydUtils.displayRecord(szOperationName, recPara);

			this.ydManualReq(recPara, true);


			//내부 Process 연결
			//EJBConnector ejbConn = null;
			//ejbConn = new EJBConnector("default", this);
			//ejbConn.trx("IssueWrkDmdFaEJB", "ydManualReq", recPara);



		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}


		szMsg = "JSP-SESSION [슬라브야드 Depilier 장입 메뉴얼 작업지시 편성] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return szRtnMsg;
	}	// end of slabYdManualReq



	/**
	 * 슬라브야드 Dummy 이적 메뉴얼 작업지시 편성
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String slabYdDummyManualReq(JDTORecord[] inDto) throws JDTOException {

		// 준비스케줄  DAO
		YdPrepSchDao ydPrepSchDao 	= new YdPrepSchDao();
		YdStkLyrDao  ydStkLyrDao 	= new YdStkLyrDao();

		int intSh             	= 0;
		String szMsg          	= null;
		String szMethodName   	= null;
		String [] strArrStlNo 	= null;
		String [] strPlMplNo  	= null;
		String szRtnMsg       	= YdConstant.RETN_CD_SUCCESS;
		String szYD_PREP_SCH_ID = null;
		String szYD_STK_POS     = null;
		String szYD_STK_LYR_NO  = null;
		String szYD_STK_COL_GP 	= null;
		String szYD_STK_BED_NO 	= null;
		String szYD_SCH_CD     	= null;

		JDTORecord    recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		JDTORecord    recDummy = JDTORecordFactory.getInstance().create();

		int nRtnVal 			= 0;

		szMsg        			= "";
		szMethodName 			= "slabYdDummyManualReq";
		String szOperationName 	= "Dummy 이적 메뉴얼 작업지시 편성";
		String strTemp 			= "";

		try {

			szMsg = "JSP-SESSION [Dummy 이적 메뉴얼 작업지시 편성] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			for(int x = 0; x < inDto.length; x++){
				szYD_STK_POS 	= inDto[x].getFieldString("YD_STK_POS");
				szYD_STK_LYR_NO = inDto[x].getFieldString("YD_STK_LYR_NO");

				if (!"".equals(szYD_STK_POS)) {
					szYD_STK_COL_GP = szYD_STK_POS.substring(0, 6);
					szYD_STK_BED_NO = szYD_STK_POS.substring(6, 8);
				}

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
				recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
				recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);

				nRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 99);

				if(nRtnVal < 0){
					szMsg = "[JSP Session : "+szMethodName+"] DUMMY 이적재료 정보 ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					szRtnMsg = YdConstant.RETN_CD_FAILURE;
					return szRtnMsg;

				} else if( nRtnVal == 0){
					szMsg = "[JSP Session : "+szMethodName+"] DUMMY 이적재료 정보가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					szRtnMsg = YdConstant.RETN_CD_FAILURE;
					return szRtnMsg;
				} else{
					szMsg = "[JSP Session : "+szMethodName+"] DUMMY 이적재료 정보 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				}

				recPara = JDTORecordFactory.getInstance().create();

				szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "YD" + szYD_STK_COL_GP.substring(2, 4) + "MM";

				//YD_SCH_CD
				recPara.setField("YD_SCH_CD", szYD_SCH_CD);

				//YD_STK_COL_GP
				recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);

				//YD_STK_BED_NO
				recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);

				//YD_SH [매수]
				recPara.setField("SLAB_SH", String.valueOf(outRecSet.size()));

				intSh = outRecSet.size();

				for(int Loopi = 0; Loopi < intSh; Loopi++){
					//재료번호
					outRecSet.absolute(Loopi + 1);
					recDummy = outRecSet.getRecord();
					//STL_NO []
					recPara.setField("STL_NO"+(Loopi+1), ydDaoUtils.paraRecChkNull(recDummy, "STL_NO"));
					//권상 모음순서
					//YD_UP_COLL_SEQ []
					recPara.setField("YD_UP_COLL_SEQ"+(Loopi+1),"");
					//recPara.setField("YD_UP_COLL_SEQ"+(Loopi+1),""+(Loopi + 1));
				}

				if (x < inDto.length-1) {
					this.ydManualReq(recPara, false);
				} else {
					this.ydManualReq(recPara, true);
				}

			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}


		szMsg = "JSP-SESSION [Dummy 이적 메뉴얼 작업지시 편성] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return szRtnMsg;
	}	// end of slabYdManualReq

	/**
	 * 오퍼레이션명 : 메뉴얼 작업지시 요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void ydManualReq(JDTORecord msgRecord, boolean bEndFlag)throws JDTOException  {

		//스케줄기준 DAO
		YdWrkbookDao 	ydWrkbookDao    = new YdWrkbookDao();
		//작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//저장품 DAO
		YdStockDao 		ydStockDao 		= new YdStockDao();
		//준비스케줄 DAO
		YdPrepSchDao    ydPrepSchDao    = new YdPrepSchDao();

		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();

		YdDelegate ydDelegate = new YdDelegate();

		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "ydManualReq";
		//사용자
		String szUser          = ydDaoUtils.paraRecChkNull(msgRecord, "REGISTER");

		//레코드 선언
		JDTORecord recPara     	= null;
		JDTORecord recStkPara  	= null;
		//레코드셋 선언
		JDTORecordSet rsResult 	= null;

		//설비ID(열구분)
		String szYD_STK_COL_GP     = null;
		//적치BED번호
		String szYD_STK_BED_NO     = null;
		//재료매수(int)
		int intMtlCnt              = 0;
		//재료번호
		String[] szSTL_NO          = null;
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN   = null;
		//작업크레인
		String szYD_WRK_CRN        = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN     = null;
		//대체크레인
		String szYD_ALT_CRN        = null;
		//선택크레인
		String szCrn               = null;
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		//목표야드구분
		String szYD_AIM_YD_GP      = null;
		//목표동구분
		String szYD_AIM_BAY_GP     = null;
		//스케줄우선순위
		String szYD_SCH_PRIOR      = null;

		//야드 구분
		String szYdGp = "";

		String szYD_PREP_SCH_ID    = "";

		try {

			//받은 전문 편집
			//스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				szMsg = "[데이터 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if(szYD_STK_COL_GP.equals("")){
				szMsg = "[데이터 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if(szYD_STK_BED_NO.equals("")){
				szMsg = "[데이터 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "SLAB_SH");
			if(intMtlCnt == 0){
				szMsg = "[데이터 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			szYD_PREP_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_PREP_SCH_ID");

			//재료번호
			szSTL_NO          = new String[intMtlCnt + 1];
			//권상모음순서
			szYD_UP_COLL_SEQ  = new String[intMtlCnt + 1];

			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				//재료번호
				szSTL_NO[Loop_i] 		= ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i]= ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
			}

			//리턴 recordSet 생성
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
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			//스케줄우선순위
			szYD_SCH_PRIOR    = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");

			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if(szYD_SCH_PROH_EXN.equals("Y")){

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal){

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")){

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal){

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

			//재료매수만큼 루프를 돌아서 작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){

				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if(!blnRtnVal) return;
			}

			// 리턴 RecordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");

			// 저장품테이블 조회
			blnRtnVal = this.chkGetStock(szSTL_NO[1], rsResult);
			if(!blnRtnVal) return ;

			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 야드목표야드구분
			szYD_AIM_YD_GP  = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
			// 야드목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");


			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");

			//작업예약ID 생성
			blnRtnVal = this.getYdWbookId(rsResult);
			if(!blnRtnVal) return;

			//레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();

			//작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			//INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			//야드구분
			String szYD_GP       = szYD_SCH_CD.substring(0,1);
			//동구분
			String szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);

			//INSERT할 항목 SET (더 추가할항목이 있다고함 // 김진욱에게 재확인할것 )
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		  szYD_GP);
			recPara.setField("YD_BAY_GP", 	  szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
			recPara.setField("REGISTER", 	  szUser);

			/////////////////////////////////////////////////////////////////////////////////////////////
			/////////////////////////2010.05.18 윤재광 > 아래부분 셋팅의 의미가 파악안됨//////////////////////////
			/////////////////////////////////////////////////////////////////////////////////////////////
			recPara.setField("YD_AIM_YD_GP",  szYD_AIM_YD_GP);
			recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
			recPara.setField("YD_SCH_PRIOR",  szYD_SCH_PRIOR);

			//To위치 정보  To Guide 위치 및 To 위치 결정방법 'F': 지정위치 추가
			recPara.setField("YD_TO_LOC_GUIDE",  ydDaoUtils.paraRecChkNull(msgRecord,"YD_TO_LOC_GUIDE"));

			if(!"".equals(ydDaoUtils.paraRecChkNull(msgRecord,"YD_TO_LOC_GUIDE")))
				recPara.setField("YD_TO_LOC_DCSN_MTD",  "F");

			//계획대차
			recPara.setField("YD_WRK_PLAN_TCAR",  ydDaoUtils.paraRecChkNull(msgRecord,"YD_WRK_PLAN_TCAR"));

			/////////////////////////////////////////////////////////////////////////////////////////////

			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);

			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){

				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");

				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);

				//레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				//재료번호
				recPara.setField("STL_NO", 		   szSTL_NO[Loop_i]);
				//적치열구분
				recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
				//적치BED번호
				recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
				//적치단번호
				recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
				//권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
			}

			//준비스케줄 Update
			if(!"".equals(szYD_PREP_SCH_ID)) {
				JDTORecord updPara = JDTORecordFactory.getInstance().create();

				updPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
				updPara.setField("MODIFIER", 	  szUser);
				updPara.setField("YD_PREP_SCH_ID",   szYD_PREP_SCH_ID);

				intRtnVal = ydPrepSchDao.updYdPrepsch(updPara, 0);
			}

			//작업 예약 편성 후 스케줄 기동
			if (bEndFlag == true) {
				recPara = JDTORecordFactory.getInstance().create();

				if("A".equals(szYD_GP)){
					//C연주 슬라브 야드
					recPara.setField("JMS_TC_CD","YDYDJ500");
				} else if("M".equals(szYD_GP)){   //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					//항만 슬라브 야드
					recPara.setField("JMS_TC_CD","YDYDJ500");
				} else if("D".equals(szYD_GP)){
					//A후판 슬라브야드
					recPara.setField("JMS_TC_CD","YDYDJ503");
				} else if("K".equals(szYD_GP)){
					//A후판 제품창고
					recPara.setField("JMS_TC_CD","YDYDJ506");
				} else if("J".equals(szYdGp)){
					//C열연 제품야드
					recPara.setField("JMS_TC_CD","YDYDJ509");
				}  else if("H".equals(szYD_GP)){
					//C열연 소재야드
					recPara.setField("JMS_TC_CD","YDYDJ509");
				} else if("S".equals(szYD_GP)){
					//통합야드
					recPara.setField("JMS_TC_CD","YDYDJ512");
				}

				recPara.setField("YD_SCH_CD", szYD_SCH_CD);
				//작업크레인 정보를 설비에 넣어준다.
				recPara.setField("YD_EQP_ID",szCrn );
				ydDelegate.sendMsg(recPara);
			}

		} catch(Exception e){
			szMsg = "메뉴얼 작업지시 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
	} // end of ydManualReq()

	/**
	 * 오퍼레이션명 : 재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환
	 *
	 * @param  String        szStlNo   재료번호
	 *         String        szMtlStat 적치단재료상태
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public int chkGetStlStkLyr(String szStlNo, String szMtlStat, JDTORecordSet rsResult)throws JDTOException  {

		//적치단 DAO
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		String szMsg        = null;
		String szMethodName = "chkGetStlStkLyr";
		int intRtnVal       = 0;
		boolean blnRtnVal   = false;
		JDTORecord recPara  = null;

		try {

			//조회 항목  record 생성
			recPara = JDTORecordFactory.getInstance().create();

			//조회 파라미터 레코드 set
			recPara.setField("STL_NO",              szStlNo);
			recPara.setField("YD_STK_LYR_MTL_STAT", szMtlStat);

			//적치단정보 조회
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 3);

			//리턴값 메세지처리
			if(intRtnVal > 1){

				szMsg = "재료번호("      + szStlNo   + ")," +
				        "적치단재료상태(" + szMtlStat + ")," +
				        " 에 대한 적치단 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new JDTOException(szMsg);
			} else if(intRtnVal == 1){

				//blnRtnVal = true;
				return intRtnVal;
			} else if(intRtnVal == 0){
				szMsg = "재료번호("      + szStlNo   + ")," +
		                "적치단재료상태(" + szMtlStat + ")," +
						" 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new JDTOException(szMsg);
			} else if(intRtnVal == -2){
				szMsg = "재료번호("      + szStlNo   + ")," +
                        "적치단재료상태(" + szMtlStat + ")," +
				        "로 적치단 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new JDTOException(szMsg);
			} else {
				szMsg = "재료번호("      + szStlNo   + ")," +
                        "적치단재료상태(" + szMtlStat + ")," +
				        " 로 적치단 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new JDTOException(szMsg);
			}
		} catch(Exception e){
			szMsg = "재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
	} //end of chkGetStlStkLyr

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
			if(intRtnVal > 1){

				szMsg = "작업예약ID 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if(intRtnVal == 1){

				blnRtnVal = true;

			} else if(intRtnVal == 0){

				szMsg = "작업예약ID를 구하지 못했습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if(intRtnVal == -2){

				szMsg = "작업예약ID 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "작업예약ID 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch(Exception e){
			szMsg = "작업예약ID생성 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		return blnRtnVal;
	} //end of getYdWbookId

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
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 0);

			//리턴값 메세지처리
			if(intRtnVal > 1){

				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if(intRtnVal == 1){
				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터 조회 성공!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				blnRtnVal = true;

			} else if(intRtnVal == 0){

				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if(intRtnVal == -2){

				szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch(Exception e){
			szMsg = "저장품유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		return blnRtnVal;
	} //end of chkGetStock

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
		boolean blnRtnVal 		  = false;
		//리턴값(int)
		int intRtnVal = 0;
		//레코드 선언
		JDTORecord recPara     	  = null;
		//레코드셋 선언
		JDTORecordSet rsResult 	  = null;

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
			if(intRtnVal > 0){

				szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 이미 등록되어 있습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if(intRtnVal == 0){

				szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 등록 가능합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				blnRtnVal = true;

			} else if(intRtnVal == -2){

				szMsg = "재료번호(" + szStlNo + ")로 작업예약재료 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "재료번호(" + szStlNo + ")로 작업예약재료 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch(Exception e){
			szMsg = "작업예약재료 등록여부 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		return blnRtnVal;

	} //end of chkYdWrkBookMtl

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
			if(intRtnVal > 1){

				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if(intRtnVal == 1){

				blnRtnVal = true;

			} else if(intRtnVal == 0){

				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if(intRtnVal == -2){

				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch(Exception e){
			szMsg = "설비 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		return blnRtnVal;
	} //end of chkGetEqp


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

			//설비 체크 및 데이터 조회
			blnRtnVal = this.chkGetEqp(szEqpId, rsResult);
			if(!blnRtnVal) return blnRtnVal;

			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			//설비상태
			szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");

			if(szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)){

				szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT + ") 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				blnRtnVal = true;

			}
		} catch(Exception e){
			szMsg = "설비상태 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
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
			if(intRtnVal > 1){

				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if(intRtnVal == 1){

				blnRtnVal = true;

			} else if(intRtnVal == 0){

				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if(intRtnVal == -2){

				szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch(Exception e){
			szMsg = "스케줄기준 체크 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		return blnRtnVal = true;

	} //end of chkGetSchRule

	/**
	 * 차량작업관리화면 상차완료처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @return
	 * @return
	 * @throws DAOException
	 */
	public String complCarLdLot(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecord       recOutTemp         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    rsResult        = JDTORecordFactory.getInstance().createRecordSet("");
		YdCarSchDao ydCarSchDao = new YdCarSchDao();

		String szMsg        			= "";
		String szMethodName 			= "complCarLdLot";
		String szOperationName 			= "상차완료처리";
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;

		YdDelegate ydDelegate = new YdDelegate();

		String szYD_CAR_SCH_ID = null;
		String szTRN_EQP_CD    = null;
		String szYD_CAR_PROG_STAT    = null;
		int intRtnVal          = 0;
		try {

			szYD_CAR_SCH_ID				= ydDaoUtils.paraRecChkNull(inDto, "YD_CAR_SCH_ID");
			szTRN_EQP_CD                = ydDaoUtils.paraRecChkNull(inDto, "TRN_EQP_CD");

			recPara.setField("TRN_EQP_CD", szTRN_EQP_CD);
	
			//
			//2021.09.08 HJW 추가. 
			//
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschDaoTrnEqpCd*/
	    	intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult, 7);
	    	if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="["+szOperationName+"] 운송장비코드["+szTRN_EQP_CD+"]로 차량스케줄 조회 시 : data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if(intRtnVal == -2) {
					szMsg="["+szOperationName+"] 운송장비코드["+szTRN_EQP_CD+"]로 차량스케줄 조회 시 : parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return YdConstant.RETN_CD_FAILURE;
			}
	    	if(intRtnVal > 0) {
		    	rsResult.absolute(1);
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsResult.getRecord());
		
		    	szYD_CAR_PROG_STAT 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_PROG_STAT");
		    	
		    	//상차상태가 아닌 경우에  상차완료처리시 에러.
		    	if(!(YdConstant.YD_CARLD_LEV.equals(szYD_CAR_PROG_STAT)
	    	        ||YdConstant.YD_CARLD_ARR.equals(szYD_CAR_PROG_STAT)
	    	        ||YdConstant.YD_CARLD_CHK.equals(szYD_CAR_PROG_STAT)
	    	        ||YdConstant.YD_CARLD_ST.equals(szYD_CAR_PROG_STAT)
	    	        ||YdConstant.YD_CARLD_CMPL.equals(szYD_CAR_PROG_STAT) )) {
		    		
		    		szMsg="["+szOperationName+"] 운송장비코드["+szTRN_EQP_CD+"] 차랑상태 ["+szYD_CAR_PROG_STAT+"] 상차완료처리 불가";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    		return YdConstant.RETN_CD_FAILURE;
		    	}
	    	        		

	       }
	    	
	    	recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recPara, 8);
			
			
			if(intRtnVal == 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 완료시간 업데이트 할  작업이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
			}
			else{
				szMsg = "[Jsp Session  -  " + szOperationName +"] 상차완료시간 업데이트 하였습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}

			//-------------------------------------------------------------
			//  이적상차시, 수동으로 상차완료작업 따라서, 크레인 권하실적 때 상차완료 처리 후 상차완료차량에 대하여 적용하는 로직들 돌려줘야한다.
			//-------------------------------------------------------------
			
			//이적상차인지 검사
			JDTORecordSet    temp       = null;
			YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
			
			temp       = JDTORecordFactory.getInstance().createRecordSet("");
			recPara.setField("TRN_EQP_CD", inDto.getFieldString("TRN_EQP_CD"));
			
			intRtnVal  = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, temp, 272);
			if(intRtnVal>0){
				String manualYN = temp.getRecord(0).getFieldString("M_WRK_GP");
				szMsg = "[JSP Session - "+szOperationName+"] 이적상차 여부 : "+ manualYN ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if("Y".equals(manualYN)){
					recPara.setField("YD_CAR_SCH_ID",		szYD_CAR_SCH_ID);
					//recPara.setField("YD_CAR_PROG_STAT", "5");
					//차량스케줄 상차완료상태로 변경만 하면됨 , 수정자..?
					intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recPara, 90);
					
					if(intRtnVal == 0 ){
						szMsg = "[Jsp Session  -  " + szOperationName +"] 이적 차량스케줄 "+ szYD_CAR_SCH_ID + " 상차완료상태 변경 불가";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
					}
					else{
						szMsg = "[Jsp Session  -  " + szOperationName +"] 이적 차량스케줄 "+ szYD_CAR_SCH_ID + " 상차완료상태 변경 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}

					//이후 크레인권하실적 상차 소재이송지시 수정 - com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009StlMoveLd
					
					String currDt = slabUtils.getDateTime14(); //현재시각
					recPara.setField("V_WR_DT"           , currDt      ); //실적일시
					recPara.setField("V_YD_CAR_SCH_ID",		szYD_CAR_SCH_ID);
					recPara.setField("V_MODIFIER",		inDto.getFieldString("YD_USER_ID"));
					
					szMsg = "[Jsp Session  -  " + szOperationName +"] 크레인권하실적 상차 소재이송지시 수정. 일시: "+currDt+", 스케줄ID: "+ szYD_CAR_SCH_ID + ", 수정자: "+inDto.getFieldString("YD_USER_ID");
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					rcv2Dao.updY1YDL009("StlMoveLd", recPara);
					//getYdCarftmvmtl12						
					//intRtnVal	= ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 271);
				}
			}
	
			
			//--------------------------------------------------------------
			//	구내운송으로 상차완료 전문 송신
			//--------------------------------------------------------------
			//상차작업완료 송신 YDTSJ008 (구내운송 상차완료)


			szMsg="[Jsp Session:"+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 구내운송으로 상차작업완료 송신 시작 : YDTSJ008";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			recPara.setField("MSG_ID",        "YDTSJ008");
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);

			ydDelegate.sendMsg(recPara);


			szMsg="[Jsp Session:"+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 구내운송으로 상차작업완료 송신 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//-------------------------------------------------------------
		}catch(JDTOException ex) {

		}

		return szRtnMsg;
	}


	/**
	 * 오퍼레이션명 : A후판더미이적작업요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procAPlDummyMvReq(JDTORecord msgRecord) throws JDTOException {

		// 스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// METHOD명
		String szMethodName = "procAPlDummyMvReq";
		// 사용자
		String szUser = "SYSTEM";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recStkPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 설비ID(열구분)
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// 재료매수(int)
		int intMtlCnt = 0;
		// 재료번호
		String[] szSTL_NO = null;
		// 권상모음순서
		String[] szYD_UP_COLL_SEQ = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = null;
		// 작업크레인
		String szYD_WRK_CRN = null;
		// 대체크레인유무
		String szYD_ALT_CRN_YN = null;
		// 대체크레인
		String szYD_ALT_CRN = null;
		// 선택크레인
		String szCrn = null;
		// 작업예약ID
		String szYD_WBOOK_ID = null;

		String szRtnVal = YdConstant.RETN_CD_SUCCESS;


		try {

			// 받은 전문 편집
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if (szYD_SCH_CD.equals("")) {

				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szMsg;

			}
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szMsg;

			}
			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {

				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szMsg;

			}
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LOT_GP_SH");
			if (intMtlCnt == 0) {

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szMsg;

			}
			// 재료번호
			szSTL_NO = new String[intMtlCnt + 1];
			// 권상모음순서
			szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];
			;

			// 재료번호, 권상모음순서
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {

					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return szMsg;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return szMsg;

				}
			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) {
				szMsg = "스케줄 기준 체크 오류입니다.";
       			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
       			return szMsg;
			}


			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 스케줄CD 체크
			// 스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_SCH_PROH_EXN");
			// 작업크레인
			szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			// 대체크레인유무
			szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_ALT_CRN_YN");
			// 대체크레인
			szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");

			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szMsg;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return szMsg;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return szMsg;

				} else {
					// 대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;

				}
			} else {
				// 작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;

			}

			// 재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			// 작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = this.chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal) {
					szMsg = "크레인사양과 저장품 사양을 체크(길이,폭,중량) 이상입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return szMsg;
				}

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) {
					szMsg = "재료번호 [" + szSTL_NO[Loop_i] + "] 다른 작업예약에 재료가 등록되어 있습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return szMsg;
				}

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) {
				szMsg = "작업예약ID 생성을 실패하였습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szMsg;
			}
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			// INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			// 야드구분
			String szYD_GP = szYD_SCH_CD.substring(0, 1);
			// 동구분
			String szYD_BAY_GP = szYD_SCH_CD.substring(1, 2);

			// INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", szYD_GP);
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			recPara.setField("REGISTER", szUser);

			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szMsg;
			}

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			// recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if (intRtnVal != 1) {
					szMsg = "재료번호[" + szSTL_NO[Loop_i] + "] 적치단 조회 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return szMsg;
				}

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO", szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return szMsg;
				}
			}



		} catch (Exception e) {
			szMsg = "A후판 더미이적 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

		return szRtnVal;

	} // end of procAPlDummyMvReq()


	/**
	 * 오퍼레이션명 : 크레인작업가능사양과 재료사양을 체크
	 *
	 * @param String
	 *            szStlNo 재료번호 String szEqpId 크레인 설비ID
	 * @return boolean true(크레인재료이송가능), false(크레인재료이송불가)
	 * @throws JDTOException
	 */
	public boolean chkCrnSpecMtlSpec(String szStlNo, String szEqpId)
			throws JDTOException {

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 메세지
		String szMsg = null;
		// 메소드명
		String szMethodName = "chkCrnSpecMtlSpec";
		// 레코드 선언
		JDTORecord recPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		try {
			// 레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 저장품 유무 체크
			blnRtnVal = this.chkGetStock(szStlNo, rsResult);
			if (!blnRtnVal)
				return blnRtnVal;

			// 결과 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 폭
			double lngMtlW = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_MTL_W");
			// 길이
			long lngMtlL = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_L");
			// 중량
			long lngMtlWt = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");

			// 레코드셋 재생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 크레인사양 체크 및 조회
			blnRtnVal = this.chkGetCrnSpec(szEqpId, rsResult);
			if (!blnRtnVal)
				return blnRtnVal;

			// 크레인사양 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 크레인 작업 능력
			// 작업가능길이
			long lngAbleL = ydDaoUtils.paraRecChkNullLong(recPara,
					"YD_WRK_ABLE_L");
			// 작업가능폭
			double lngAbleW = ydDaoUtils.paraRecChkNullDouble(recPara,
					"YD_WRK_ABLE_W");
			// 작업가능중량
			long lngAbleWt = ydDaoUtils.paraRecChkNullLong(recPara,
					"YD_WRK_ABLE_WT");

			// 크레인 작업가능 길이와 재료의 길이 비교
			if (lngAbleL < lngMtlL) {
				szMsg = "크레인 작업가능 길이(" + lngAbleL + ") 보다 재료의 길이(" + lngMtlL
						+ ")가 더 큽니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;
			}

			// 크레인 작업가능 폭과 재료의 폭 비교
			if (lngAbleW < lngMtlW) {
				szMsg = "크레인 작업가능 폭(" + lngAbleW + ")보다 재료의 폭(" + lngMtlW
						+ ")이 더 큽니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;
			}

			// 크레인 작업가능 폭과 재료의 폭 비교
			if (lngAbleWt < lngMtlWt) {
				szMsg = "크레인 작업가능 중량(" + lngAbleWt + ")보다 재료의 중량(" + lngMtlWt
						+ ")이 더 큽니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;
			}

		} catch (Exception e) {
			szMsg = "크레인작업가능사양과 재료사양을 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal = true;
	} // end of chkCrnSpecMtlSpec



	/**
	 * 오퍼레이션명 : 크레인사양 유무체크 및 조회결과 데이터 반환
	 *
	 * @param String
	 *            szEqpId 설비ID JDTORecordSet rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCrnSpec(String szEqpId, JDTORecordSet rsResult)
			throws JDTOException {

		// 크레인사양 DAO
		YdCrnSpecDao ydCrnSpecDao = new YdCrnSpecDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetCrnSpec";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 크레인 설비ID
			recPara.setField("YD_EQP_ID", szEqpId);

			// 크레인사양 조회
			intRtnVal = ydCrnSpecDao.getYdCrnspec(recPara, rsResult, 0);

			// 리턴값 메세지처리
			if (intRtnVal > 1) {

				szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "크레인설비ID(" + szEqpId
						+ ")로 크레인사양 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "크레인설비ID(" + szEqpId + ")로 크레인사양 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "크레인사양 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetCrnSpec

	/**
	 * 차량 작업 관리 화면 : 초기화 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @return
	 * @return
	 * @throws DAOException
	 */

	public JDTORecord updCarWrMgt(JDTORecord [] inDto) throws DAOException {
		int       intRtnVal    = 0;
		JDTORecordSet outRecSet	= null;
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		JDTORecord RecOutRec 	= null;
		JDTORecord RecOutRec2 	= null;
		JDTORecord recPara 		= null;
		String szMsg        	= "";
		String szMethodName 	= "updCarWrMgt";
		String szYD_CAR_SCH_ID 	= null;
		String sYD_USER_ID			= "";

		String sYD_CAR_SCH_ID 		= "";
		String sYD_CARLD_STOP_LOC	= "";
		String sYD_CARUD_STOP_LOC	= "";
		String sYD_CAR_PROG_STAT 	= "";
		String sYD_CRN_SCH_ID       = "";
		String sYD_CARLD_YD_WBOOK_ID	= "";
		String sYD_CARUD_YD_WBOOK_ID 	= "";
		String sYD_SCH_CD			= "";
		String sRTN_CD				= "";
		String sRTN_MSG				= "";
		String sCANCEL_SEND			= "";
		String szYD_EQP_ID			= "";
		String szRtnMsg	= "";
		String sTRN_EQP_CD = "";
		
		JDTORecord outRecord = JDTORecordFactory.getInstance().create(); //
		JDTORecord inRecord = JDTORecordFactory.getInstance().create(); //
		JDTORecord recInTemp = JDTORecordFactory.getInstance().create(); //
		JDTORecord inRecord4 = JDTORecordFactory.getInstance().create(); //
		JDTORecord outRecord5 = JDTORecordFactory.getInstance().create(); //
		JDTORecord outRecord6 = JDTORecordFactory.getInstance().create(); //
		JDTORecord recDelPara = JDTORecordFactory.getInstance().create(); //
		JDTORecord recEqpPara 		= JDTORecordFactory.getInstance().create();

		EJBConnector ejbConn = null;
		YdDelegate		ydDelegate 		= new YdDelegate();
		JDTORecord[] 	inRecordarr   		= null;
		CoilGdsJspDao dao = new CoilGdsJspDao();
		JDTORecord outRecord1 		= JDTORecordFactory.getInstance().create();

		try {
			szMsg = "[JSP Session - 초기화] 메소드 시작 - 로우건수 : " + inDto.length;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			szYD_CAR_SCH_ID = yddatautil.setDataDefault(inDto[0].getField("YD_CAR_SCH_ID"), "");
			sYD_USER_ID		= yddatautil.setDataDefault(inDto[0].getField("YD_USER_ID"), "");

			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("V_YD_CAR_SCH_ID",szYD_CAR_SCH_ID);
			/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getCarWrMgt*/
			outRecSet = dao.getCarWrMgt(recPara);

			if (outRecSet == null || outRecSet.size() == 0) {
				szMsg = "해당정보가 없습니다." + szYD_CAR_SCH_ID;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");
				outRecord.setField("RTN_MSG" 	, szMsg);
				return outRecord;

			}
			outRecSet.first();
			RecOutRec = outRecSet.getRecord();

			sYD_CAR_SCH_ID 			= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_CAR_SCH_ID");
			sYD_CARLD_STOP_LOC		= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_CARLD_STOP_LOC");
			sYD_CARUD_STOP_LOC		= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_CARUD_STOP_LOC");

			sYD_CAR_PROG_STAT 		= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_CAR_PROG_STAT");
			sYD_CRN_SCH_ID 			= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_CRN_SCH_ID");
			sYD_CARLD_YD_WBOOK_ID	= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_CARLD_YD_WBOOK_ID");
			sYD_CARUD_YD_WBOOK_ID 	= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_CARUD_YD_WBOOK_ID");
			sYD_SCH_CD				= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_SCH_CD");
			sTRN_EQP_CD             = ydDaoUtils.paraRecChkNull(RecOutRec, "TRN_EQP_CD");
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "YD_CAR_SCH_ID			"+ sYD_CAR_SCH_ID 		  	,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "YD_CARLD_STOP_LOC		"+ sYD_CARLD_STOP_LOC	  	,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "YD_CAR_PROG_STAT 		"+ sYD_CAR_PROG_STAT 	  	,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "YD_CRN_SCH_ID 			"+ sYD_CRN_SCH_ID 	  		,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "YD_CARLD_YD_WBOOK_ID 	"+ sYD_CARLD_YD_WBOOK_ID 	,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "YD_CARUD_YD_WBOOK_ID 	"+ sYD_CARUD_YD_WBOOK_ID 	,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "sYD_CARUD_STOP_LOC		"+ sYD_CARUD_STOP_LOC	  	,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "sYD_SCH_CD				"+ sYD_SCH_CD	  			,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "sTRN_EQP_CD			"+ sTRN_EQP_CD	  			,YdConstant.DEBUG);


			if(!sYD_CRN_SCH_ID.equals("")) {
//				outRecord.setField("RTN_CD" 	, "0");
//				outRecord.setField("RTN_MSG" 	, "크레인 스케쥴이 있습니다. 작업취소후 처리 하세요");
//				return outRecord;

				inRecord4   	= JDTORecordFactory.getInstance().create();
				inRecord4.setField("YD_CRN_SCH_ID"	,sYD_CRN_SCH_ID);
				inRecord4.setField("YD_SCH_CD"		,sYD_SCH_CD);
				inRecord4.setField("DEL_YN"			,"Y");
				inRecord4.setField("MODIFIER"		,sYD_USER_ID);

				szMsg = "스케쥴 취소 시작!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
				outRecord1 	= (JDTORecord)ejbConn.trx("PlateSchCncl", new Class[] { JDTORecord.class }, new Object[] { inRecord4 });

				sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				sCANCEL_SEND= StringHelper.evl(outRecord1.getFieldString("CANCEL_SEND"), "N");
				if (sRTN_CD.equals("0")) {
					outRecord.setField("RTN_CD" 	, "0");
					outRecord.setField("RTN_MSG" 	, sRTN_MSG);
					return outRecord;
				}

				szMsg = "스케쥴 취소 종료!! 작업예약 취소 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);
				outRecord1 	= (JDTORecord)ejbConn.trx("PlateDelWBook", new Class[] { JDTORecord.class }, new Object[] { outRecord1 });

				szMsg = "작업예약 취소 종료!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


				sRTN_CD						= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				String szJMS_TC_CD			= StringHelper.evl(outRecord1.getFieldString("MSG_ID"), "");
				szYD_EQP_ID					= StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
				String szYD_WRK_PROG_STAT	= StringHelper.evl(outRecord1.getFieldString("YD_WRK_PROG_STAT"), "");
				String szYD_SCH_CD			= StringHelper.evl(outRecord1.getFieldString("YD_SCH_CD"), "");
				String szRTN_SND			= StringHelper.evl(outRecord1.getFieldString("RTN_SND"), "N");

				if (sRTN_CD.equals("0")) {
					outRecord.setField("RTN_CD" 	, "0");
					outRecord.setField("RTN_MSG" 	, "스케줄 취소 도중 오류 발생 ");
					return outRecord;
				}
				if(szRTN_SND.equals("Y") && sCANCEL_SEND.equals("Y")) {

					//YdDelegate ydDelegate = new YdDelegate();

					szMsg = "[JSP Session : 작업관리 (작업취소)] 크레인 작업지시 정보를 내부QUEUE로 송신 합니다";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					recDelPara   = JDTORecordFactory.getInstance().create();
					recDelPara.setField("MSG_ID"				, szJMS_TC_CD        );
					recDelPara.setField("YD_EQP_ID"				, szYD_EQP_ID            );
					recDelPara.setField("YD_WRK_PROG_STAT"		, szYD_WRK_PROG_STAT);
					recDelPara.setField("YD_SCH_CD"				, szYD_SCH_CD );
					ydDelegate.sendMsg(recDelPara);
				}

			}else{
				if(!sYD_CARLD_YD_WBOOK_ID.equals("")) {
	//				outRecord.setField("RTN_CD" 	, "0");
	//				outRecord.setField("RTN_MSG" 	, "작업예약이 있습니다. 삭제후 처리 하세요");
	//				return outRecord;

					// 작업예약취소  호출
					inRecordarr = new JDTORecord[1];

					inRecordarr[0] = JDTORecordFactory.getInstance().create();
					inRecordarr[0].setField("YD_WBOOK_ID"		, sYD_CARLD_YD_WBOOK_ID);
					inRecordarr[0].setField("YD_USER_ID"	    , sYD_USER_ID);

					ejbConn = new EJBConnector("default", "SlabJspFaEJB", this);
					String rtnMsg = (String)ejbConn.trx("delYdWrkbook",
							new Class[] { JDTORecord[].class }, new Object[] { inRecordarr });

					if (!rtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) {
						outRecord.setField("RTN_CD" 	, "0");
						outRecord.setField("RTN_MSG" 	, "상차 작업 예약 취소 중 오류 발생");
						return outRecord;
					}



				}
				if(!sYD_CARUD_YD_WBOOK_ID.equals("")) {
	//				outRecord.setField("RTN_CD" 	, "0");
	//				outRecord.setField("RTN_MSG" 	, "작업예약이 있습니다. 삭제후 처리 하세요");
	//				return outRecord;
					inRecordarr = new JDTORecord[1];

					inRecordarr[0] = JDTORecordFactory.getInstance().create();
					inRecordarr[0].setField("YD_WBOOK_ID"		, sYD_CARUD_YD_WBOOK_ID);
					inRecordarr[0].setField("YD_USER_ID"	    , sYD_USER_ID);

					ejbConn = new EJBConnector("default", "SlabJspFaEJB", this);
					String rtnMsg = (String)ejbConn.trx("delYdWrkbook",
							new Class[] { JDTORecord[].class }, new Object[] { inRecordarr });

					if (!rtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) {
						outRecord.setField("RTN_CD" 	, "0");
						outRecord.setField("RTN_MSG" 	, "하차 작업 예약 취소 중 오류 발생");
						return outRecord;
					}
				}

			}

			if(!sYD_CAR_SCH_ID.equals("")) {
				szMsg = "차량번호로 모든 차량스케줄 SELECT 하여 초기화";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				JDTORecordSet    outRecSet2       = JDTORecordFactory.getInstance().createRecordSet("retTmp");

				
				inRecord4   	= JDTORecordFactory.getInstance().create();
				inRecord4.setField("TRN_EQP_CD"	,sTRN_EQP_CD);
				inRecord4.setField("CAR_NO"		,sTRN_EQP_CD);
				
				intRtnVal = ydCarSchDao.getYdCarsch(inRecord4, outRecSet2, 10);
				
				
				if (intRtnVal<=0) {
					szMsg = "해당차량의 스케줄정보가 없습니다." + szYD_CAR_SCH_ID;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" 	, "0");
					outRecord.setField("RTN_MSG" 	, szMsg);
					return outRecord;

				}
				for(int i=1; i<=outRecSet2.size(); i++){
					outRecSet2.absolute(i);
					RecOutRec2 = outRecSet2.getRecord();
				
					String tempCarSchId = StringHelper.evl(RecOutRec2.getFieldString("YD_CAR_SCH_ID"), "");
					
					if("".equals(tempCarSchId)) continue;
					
					szMsg = "차량번호["+sTRN_EQP_CD+"]의 스케줄id ["+tempCarSchId+"]차량스케줄 clear 시작" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					inRecord = JDTORecordFactory.getInstance().create(); //
					inRecord.setField("V_MODIFIER"		, sYD_USER_ID);
					inRecord.setField("V_YD_CAR_SCH_ID"	, tempCarSchId);
					
				    /* com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.delCarWrMgtCarSchMtl */
				    intRtnVal = dao.delCarWrMgtCarSchMtl(inRecord);

				    /* com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.delCarWrMgtCarSch */
				    intRtnVal = dao.delCarWrMgtCarSch(inRecord);
					if (intRtnVal <= 0) {
						szMsg = "차량SCH 삭제중 ERROR 발생 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");
						outRecord.setField("RTN_MSG" 	, szMsg);
						return outRecord;
					}
	
				}


			}

			if(sYD_CAR_PROG_STAT.equals("2")||sYD_CAR_PROG_STAT.equals("3")||sYD_CAR_PROG_STAT.equals("4")||sYD_CAR_PROG_STAT.equals("5")){

				if(!sYD_CARLD_STOP_LOC.equals("")) {

					inRecord = JDTORecordFactory.getInstance().create(); //
					inRecord.setField("V_MODIFIER"		, sYD_USER_ID);
					inRecord.setField("V_YD_STK_COL_GP"	, sYD_CARLD_STOP_LOC);

				    /* com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCarWrMgtStkcol */
				    intRtnVal = dao.updCarWrMgtStkcol(inRecord);
					if (intRtnVal <= 0) {
						szMsg = "적치열  수정중 ERROR 발생 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");
						outRecord.setField("RTN_MSG" 	, szMsg);
						return outRecord;
					}



					/*
					 * 적치베드 상태비활성화등록
					 */
					szMsg= "[" + szSessionName + "] 출발야드의 적치열["+sYD_CARLD_STOP_LOC+"]의 베드를 비활성상태와 BED중량MAX기본값으로 변경처리 시작 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_STK_BED_WT_MAX", YdConstant.YD_STK_BED_WT_MAX_DEFAULT);
					recInTemp.setField("YD_STK_COL_GP", sYD_CARLD_STOP_LOC);
					recInTemp.setField("YD_STK_BED_ACT_STAT", "C");

					intRtnVal = ydStkBedDao.updYdStkbedYdStkColGp(recInTemp, 300);

					if(intRtnVal == 0) {
						szMsg = "[" + szSessionName + "] 출발야드의 적치열["+sYD_CARLD_STOP_LOC+"]의 베드를 비활성상태 변경처리 시 적치베드가 존재하지 않습니다 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");
						outRecord.setField("RTN_MSG" 	, szMsg);
						return outRecord;

					}else if(intRtnVal < 0) {
						szMsg = "[" + szSessionName + "] 출발야드의 적치열["+sYD_CARLD_STOP_LOC+"]의 베드를 비활성상태 변경처리 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");
						outRecord.setField("RTN_MSG" 	, szMsg);
						return outRecord;
					}

					/*
					 * 적치단 비활성화
					 */
					szMsg= "[" + szSessionName + "] 출발야드의 적치열["+sYD_CARLD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시작 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_STK_COL_GP", sYD_CARLD_STOP_LOC);
					recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
					recInTemp.setField("STL_NO", "");
					recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");

					intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGp(recInTemp);
					if(intRtnVal == 0) {
						szMsg = "[" + szSessionName + "] 출발야드의 적치열["+sYD_CARLD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시 적치단이 존재하지 않습니다. - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");
						outRecord.setField("RTN_MSG" 	, szMsg);
						return outRecord;

					}else if(intRtnVal < 0) {
						szMsg = "[" + szSessionName + "] 출발야드의 적치열["+sYD_CARLD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");
						outRecord.setField("RTN_MSG" 	, szMsg);
						return outRecord;
					}else if(intRtnVal > 1000) {
						szMsg = "[" + szSessionName + "] 출발야드의 적치열["+sYD_CARLD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");
						outRecord.setField("RTN_MSG" 	, szMsg);
						return outRecord;
					}

					szMsg= "[" + szSessionName + "] 출발야드의 적치열["+sYD_CARLD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		    		// 업무기준 : 차량 출발 시 저장위치 제원 야드L2로 전송
			    	//=======================================================================
			    	recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_INFO_SYNC_CD"	, "3");							    // 1:동,2:SPAN,3:열,4:BED
					recInTemp.setField("YD_GP"          	, sYD_CARLD_STOP_LOC.substring(0, 1));
					recInTemp.setField("YD_STK_COL_GP"  	, sYD_CARLD_STOP_LOC);
					recInTemp.setField("YD_CAR_PROG_STAT"	, "S");
					recInTemp.setField("YD_EQP_WRK_STAT" 	, "L");
					YdCommonUtils.sndStrPosSpecToL2(recInTemp);

				}
			}
			if(sYD_CAR_PROG_STAT.equals("B")||sYD_CAR_PROG_STAT.equals("C")||sYD_CAR_PROG_STAT.equals("D")||sYD_CAR_PROG_STAT.equals("E")){

				if(!sYD_CARUD_STOP_LOC.equals("")) {

					inRecord = JDTORecordFactory.getInstance().create(); //
					inRecord.setField("V_MODIFIER"		, sYD_USER_ID);
					inRecord.setField("V_YD_STK_COL_GP"	, sYD_CARUD_STOP_LOC);

				    /* com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCarWrMgtStkcol */
				    intRtnVal = dao.updCarWrMgtStkcol(inRecord);
					if (intRtnVal <= 0) {
						szMsg = "적치열  수정중 ERROR 발생 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");
						outRecord.setField("RTN_MSG" 	, szMsg);
						return outRecord;
					}



					/*
					 * 적치베드 상태비활성화등록
					 */
					szMsg= "[" + szSessionName + "] 도착야드의 적치열["+sYD_CARUD_STOP_LOC+"]의 베드를 비활성상태와 BED중량MAX기본값으로 변경처리 시작 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_STK_BED_WT_MAX", YdConstant.YD_STK_BED_WT_MAX_DEFAULT);
					recInTemp.setField("YD_STK_COL_GP", sYD_CARUD_STOP_LOC);
					recInTemp.setField("YD_STK_BED_ACT_STAT", "C");

					intRtnVal = ydStkBedDao.updYdStkbedYdStkColGp(recInTemp, 300);

					if(intRtnVal == 0) {
						szMsg = "[" + szSessionName + "] 도착야드의 적치열["+sYD_CARUD_STOP_LOC+"]의 베드를 비활성상태 변경처리 시 적치베드가 존재하지 않습니다 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");
						outRecord.setField("RTN_MSG" 	, szMsg);
						return outRecord;

					}else if(intRtnVal < 0) {
						szMsg = "[" + szSessionName + "] 도착야드의 적치열["+sYD_CARUD_STOP_LOC+"]의 베드를 비활성상태 변경처리 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");
						outRecord.setField("RTN_MSG" 	, szMsg);
						return outRecord;
					}

					/*
					 * 적치단 비활성화
					 */
					szMsg= "[" + szSessionName + "] 도착야드의 적치열["+sYD_CARUD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시작 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_STK_COL_GP", sYD_CARUD_STOP_LOC);
					recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
					recInTemp.setField("STL_NO", "");
					recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");

					intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGp(recInTemp);
					if(intRtnVal == 0) {
						szMsg = "[" + szSessionName + "] 도착야드의 적치열["+sYD_CARUD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시 적치단이 존재하지 않습니다. - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");
						outRecord.setField("RTN_MSG" 	, szMsg);
						return outRecord;

					}else if(intRtnVal < 0) {
						szMsg = "[" + szSessionName + "] 도착야드의 적치열["+sYD_CARUD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");
						outRecord.setField("RTN_MSG" 	, szMsg);
						return outRecord;
					}else if(intRtnVal > 1000) {
						szMsg = "[" + szSessionName + "] 도착야드의 적치열["+sYD_CARUD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");
						outRecord.setField("RTN_MSG" 	, szMsg);
						return outRecord;
					}

					szMsg= "[" + szSessionName + "] 도착야드의 적치열["+sYD_CARUD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		    		// 업무기준 : 차량 출발 시 저장위치 제원 야드L2로 전송
			    	//=======================================================================
			    	recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_INFO_SYNC_CD"	, "3");							    // 1:동,2:SPAN,3:열,4:BED
					recInTemp.setField("YD_GP"          	, sYD_CARUD_STOP_LOC.substring(0, 1));
					recInTemp.setField("YD_STK_COL_GP"  	, sYD_CARUD_STOP_LOC);
					recInTemp.setField("YD_CAR_PROG_STAT"	, "S");
					recInTemp.setField("YD_EQP_WRK_STAT" 	, "L");
					YdCommonUtils.sndStrPosSpecToL2(recInTemp);

				}
			}

			outRecord.setField("RTN_CD" 	, "1");
			return outRecord;

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}//end of uptCarSch


	/**
	 * 오퍼레이션명 :
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public boolean RequiresUpdYdEqp(JDTORecord recEqpPara){

		boolean isSuccess = false;
		int iReq = -1;
		int intRtnVal =0;
		String stkQueryId ="";
		YdEqpDao ydEqpDao = new YdEqpDao();
		try{

			intRtnVal = ydEqpDao.updYdEqp(recEqpPara, 0);

			if(intRtnVal > 0 ){
			isSuccess = true;
			}

	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}


	/**
	 * LOT대상 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */

	public JDTORecordSet getSlabYdStkcar(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		String szOperationName	= "LOT대상  조회";

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		String szMsg        = "";
		String szMethodName = "getSlabYdStkcar";
		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION [" + szOperationName + " ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("LOT_ID", inDto.getFieldString("LOT_ID"));
			recPara.setField("TRN_EQP_CD", inDto.getFieldString("TRN_EQP_CD"));
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 608);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if

			outRecSet.first();

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			//if( ydb != null ) ydb.close();
		}
		szMsg = "JSP-SESSION [" + szOperationName + " ] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}	// end of getSlabYdStkcar


	/**
	 * 장비저장위치 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */

	public JDTORecordSet getSlabYdStkcar2(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		String szOperationName	= "장비저장위치  조회";

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		String szMsg        = "";
		String szMethodName = "getSlabYdStkcar2";
		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION [" + szOperationName + " ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("CHK_CD", inDto.getFieldString("CHK_CD"));
			recPara.setField("TRN_EQP_CD", inDto.getFieldString("TRN_EQP_CD"));

			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 609);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if

			outRecSet.first();

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			//if( ydb != null ) ydb.close();
		}
		szMsg = "JSP-SESSION [" + szOperationName + " ] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}	// end of getSlabYdStkcar



	/**
	 * 상차완료 저장위치수정  - 통합슬라브야드 [통합슬라브야드는 저장품 관련 L2전송부분이 존재하지 않는다]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String updSlabYdStkcarFix_Tot(JDTORecord [] inDto) throws DAOException {

		int intRtnVal = 0;
		String szMsg  ="";
		String szMethodName="updSlabYdStkcarFix_Tot";
		String szOperationName = "상차완료 저장위치수정";
		String szStlNo = null;
		String szOldStlNo = null;
		String szStkColGp = null;
		String szStkBedNo = null;
		String szStkLyrNo = null;
		String szModifier = null;
		String szMtlItem = "";
		JDTORecordSet rsDelInfo = null;
		JDTORecordSet rsStockInfo = null;
		JDTORecordSet rsStockHistInfo = null;

		JDTORecord recPara = null;
		JDTORecord newPara = null;
		JDTORecord getRecord = null;
		JDTORecord setRecord= null;
		JDTORecord logRecord = null;

		JDTORecordSet slabCommRecSet = null;
		JDTORecordSet rsTemp  = null;
		JDTORecordSet getRecSet = null;

		YdStockDao ydStockDao = new YdStockDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		JDTORecordSet returnSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet returnSet2 = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet rsStockPrevLoc = null;
		
		String szSTL_NO = "";
		String szRtnMsg = "";
		String szLogMsg = "";
		String szYD_GP = "";
		String szPT_TB_COMM = "";
		String szYdStrLoc ="";
		String szYdStrLocHis1 = "";
    	String szYdGp = "";
    	String szArrWlocCd ="";
    	String szChkCd = "";
    	String szYD_CAR_SCH_ID ="";
    	boolean bHistFlag = false;




		try{
			szMsg = "JSP-SESSION [산적 위치 수정  - 통합슬라브야드] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			/*********************************************************
			 * UPDATE 될 위치의 적치단이 없을 경우 먼저 체크해서
			 * 리턴해준다.(첫번째 온 정보(최상단 정보먼저 체크한다)
			 ********************************************************/


			// 처리 할 필요없는 경우
			if (inDto.length < 1 ){
				szRtnMsg = "적치단  정보가 없습니다";
				return szRtnMsg;
			}

			recPara = JDTORecordFactory.getInstance().create();
			recPara = inDto[0];


			rsTemp = JDTORecordFactory.getInstance().createRecordSet("YdSlabTemp");
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsTemp, 0);

			//단정보가 존재 하지않거나  단데이터 생성이 되지않는경우
			if(intRtnVal  < 1){
				szRtnMsg = "적치단  정보가 생성되지 않았습니다";
				return szRtnMsg;
			}
			
	
			//이전 저장위치 백업 -- 수정대상이 다른 수정대상의 위치를 덮어씌우면 권상위치 기록 안되는 문제 -- 2022.04.19 REQ202203392588
			rsStockPrevLoc = JDTORecordFactory.getInstance().createRecordSet("YdSlabTemp");
			
			for(int iLoop=0; iLoop<inDto.length; iLoop++){
				szStlNo = yddatautil.setDataDefault(inDto[iLoop].getField("STL_NO"), "").trim();
				String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO"				, szStlNo);
				recPara.setField("YD_STK_LYR_MTL_STAT"	, "");
				
				rsTemp = slabYdCommDao.select(recPara, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO", logId, szMethodName, "적치단 정보 select");
				
				if(rsTemp != null && rsTemp.size() >0) {
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_STK_COL_GP", rsTemp.getRecord(0).getFieldString("YD_STK_COL_GP"));
					recPara.setField("YD_STK_BED_NO", rsTemp.getRecord(0).getFieldString("YD_STK_BED_NO"));
					recPara.setField("YD_STK_LYR_NO", rsTemp.getRecord(0).getFieldString("YD_STK_LYR_NO"));
					recPara.setField("STL_NO"		, rsTemp.getRecord(0).getFieldString("STL_NO"));
					rsStockPrevLoc.addRecord(recPara);
				}
				else {
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_STK_COL_GP", "");
					recPara.setField("YD_STK_BED_NO", "");
					recPara.setField("YD_STK_LYR_NO", "");
					recPara.setField("STL_NO"		, szStlNo);
					rsStockPrevLoc.addRecord(recPara);
				}
				
			}

			for(int iLoop=0; iLoop<inDto.length; iLoop++){

				szStlNo = yddatautil.setDataDefault(inDto[iLoop].getField("STL_NO"), "");
				szArrWlocCd = yddatautil.setDataDefault(inDto[iLoop].getField("ARR_WLOC_CD"), "");

				/*
				 *  현 저장위치정보에  이미 재료번호가 다른것이  들어 있을경우에
				 *  그 재료번호에 해당하는 공통테이블의 저장위치를 Clear 해주어야한다.
				 */


				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP", inDto[iLoop].getField("YD_STK_COL_GP"));
				recPara.setField("YD_STK_BED_NO", inDto[iLoop].getField("YD_STK_BED_NO"));
				recPara.setField("YD_STK_LYR_NO", inDto[iLoop].getField("YD_STK_LYR_NO"));

				rsDelInfo	=JDTORecordFactory.getInstance().createRecordSet("rsdelInfo");
				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsDelInfo, 0);

				if (intRtnVal >= 1) {
					recPara = JDTORecordFactory.getInstance().create();
					rsDelInfo.first();
					recPara = rsDelInfo.getRecord();


					if(ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim().equals("")){
						szMsg = "[JSP-SESSION]기존 재료정보가 없는경우 입니다";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

						//기존정보가 없고 현재 재료정보도 없는경우

						if(szStlNo.equals("")){
							bHistFlag = false;
							continue;
						}else{
							bHistFlag = true;
						}



					} else if (ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim().equals(szStlNo)){
						bHistFlag = false;
						szMsg = "[JSP-SESSION]기존 재료정보가 현 적치 정보와 같은 경우 입니다";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);
						continue;


					} else{
						bHistFlag = true;
						szMsg = "[JSP-SESSION]기존 재료정보가 현 적치 정보와 다른 경우 입니다";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);


						szMsg = "[JSP-SESSION]공통 정보의 기존 적치 위치를 CLEAR 합니다";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);



						szOldStlNo = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

						getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

						szRtnMsg = YdCommonUtils.getPtCommStock(szOldStlNo, getRecSet);



						if(szRtnMsg.equals(YdConstant.RETN_CD_FAILURE)){

							szMsg = "[JSP-SESSION]공통 정보가 존재 하지 않습니다 - PASS 합니다";
							ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);


						} else if(szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
						   	getRecSet.absolute(1);
				        	getRecord = JDTORecordFactory.getInstance().create();
				        	getRecord = getRecSet.getRecord();

				        	szYdStrLoc = ydDaoUtils.paraRecChkNull(getRecord,"YD_STR_LOC");
				        	szYdStrLocHis1 = ydDaoUtils.paraRecChkNull(getRecord,"YD_STR_LOC_HIS1");
				        	szPT_TB_COMM = ydDaoUtils.paraRecChkNull(getRecord, "PT_TB_COMM");
				        	szStkColGp = recPara.getFieldString("YD_STK_COL_GP");


				        	setRecord = JDTORecordFactory.getInstance().create();
				        	setRecord.setField("YD_GP",         szStkColGp.substring(0,1));
				        	setRecord.setField("YD_BAY_GP",     "");
				        	setRecord.setField("YD_EQP_GP",     "");
				        	setRecord.setField("YD_STK_COL_NO", "");
				        	setRecord.setField("YD_STK_BED_NO", "");
				        	setRecord.setField("YD_STK_LYR_NO", "");
				        	setRecord.setField("FNL_REG_PGM",   "updSlabYdStkPosFixBoth");
				        	setRecord.setField("MODIFIER",  yddatautil.setDataDefault( inDto[iLoop].getField("YD_USER_ID"),"YD"));


				        	//전저장위치 = szYdStrLoc,	 전전저장위치 = szYdStrLocHis1
					        if(szYdStrLoc.equals("")){
					        	setRecord.setField("YD_STR_LOC_HIS1", 	"");
					        }else{
					        	setRecord.setField("YD_STR_LOC_HIS1", 	szYdStrLoc);
					        }

					        if(szYdStrLocHis1.equals("")){
					        	setRecord.setField("YD_STR_LOC_HIS2", 	"");
					        }else{
					        	setRecord.setField("YD_STR_LOC_HIS2", 	szYdStrLocHis1);
					        }


				        	//현재 저장위치 = 실적위치+실적단 주편일경우와 슬라브일경우


					        szMsg = "전 저장위치 : " + szYdStrLoc + " , 전전 저장위치 : " + szYdStrLocHis1 + ", 권하실적 단 정보 : " + szStkLyrNo;
				            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				        	if(szPT_TB_COMM.equals("B")){

				        		setRecord.setField("MSLAB_NO",   szOldStlNo);
				        		setRecord.setField("YD_STR_LOC", "");

				        		intRtnVal = this.updY1YdStock(setRecord, 2);
				        		if(intRtnVal<0) {
				                    szMsg = "주편공통Table 저장위치 등록 실패";
				                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				        		}

				        	}else if (szPT_TB_COMM.equals("S")) {

				        		setRecord.setField("SLAB_NO",   szOldStlNo);
				        		setRecord.setField("YD_STR_LOC","");

				        		//슬라브 공통 업데이트
				        		intRtnVal = this.updY1YdStock(setRecord,  0);
				        		if(intRtnVal<0) {
				                    szMsg = "슬라브공통Table 저장위치 등록 실패";
				                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				        		}

				         	}

						} //RETN_CD_SUCCESS
					}
				}




				recPara = JDTORecordFactory.getInstance().create();
				ydUtils.putLog("SlabJspSeEJB", "updSlabYdStkPosFixBoth", " 적치단 정보 READ=============", 4);
				// 1. 적치 단 정보 UPDATE

				recPara.setField("STL_NO", szStlNo);
				//적치열구분
				recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"), ""));
				//적치베드
				recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_BED_NO"), ""));
				//적치단
				recPara.setField("YD_STK_LYR_NO", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_LYR_NO"), ""));

				szStkColGp = recPara.getFieldString("YD_STK_COL_GP");
				szStkBedNo = recPara.getFieldString("YD_STK_BED_NO");
				szStkLyrNo = recPara.getFieldString("YD_STK_LYR_NO");
				szModifier = inDto[iLoop].getFieldString("YD_USER_ID");

				recPara.setField("MODIFIER", szModifier);


				//적치 상태 [재료번호가 존재 :   "C" , 미존재 : "E"]
				if ( "".equals(szStlNo)){
					recPara.setField("YD_STK_LYR_MTL_STAT", "E");
				} else {
					recPara.setField("YD_STK_LYR_MTL_STAT", "C");
				}

				recPara.setField("YD_STK_LYR_ACT_STAT", "E");

				// 해당 재료번호가 적치중일 경우 해당 적치 정보 삭제 기능 추가
				JDTORecord      recDelPara      = 	JDTORecordFactory.getInstance().create();
				rsDelInfo		=	JDTORecordFactory.getInstance().createRecordSet("rsdelInfo");
				if("".equals(szStlNo.trim())){
					//해당작업 필요없음					
					int nRtnVal  = ydStkLyrDao.getYdStklyr(recPara, rsDelInfo, 0);
					if(nRtnVal > 0 ){
						recDelPara   = 	JDTORecordFactory.getInstance().create();
						recDelPara   =  rsDelInfo.getRecord();

					 	String szSTL_NO_CHK = ydDaoUtils.paraRecChkNull(recDelPara,"STL_NO");
					 	
					 	if(!szSTL_NO_CHK.equals("")){
					 		szRtnMsg = "적치단에 제료번호가 이미 존재 합니다.:"+szSTL_NO_CHK;
					 		this.m_ctx.setRollbackOnly();
							return szRtnMsg;
					 	}
					}
				}else{
					recDelPara.setField("STL_NO", szStlNo);
					recDelPara.setField("YD_STK_LYR_MTL_STAT", "C");

					int nRtnVal  = ydStkLyrDao.getYdStklyr(recDelPara, rsDelInfo, 3);


					if(nRtnVal == 0 ){
						//해당 작업 필요없음

					}else if(nRtnVal > 0 ){

						//정보 존재시 해당 Map Clear
						rsDelInfo.first();

						do{
							recDelPara   = 	JDTORecordFactory.getInstance().create();
							recDelPara   =  rsDelInfo.getRecord();

							recDelPara.setField("STL_NO", "");
							recDelPara.setField("YD_STK_LYR_MTL_STAT", "E");
							ydStkLyrDao.updYdStklyr(recDelPara, 0);

						}while(rsDelInfo.next());
					}
				}




				szMsg = "적치단 정보 UPDATE===================================== "+szStlNo+":"+szChkCd;

				ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);
				intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);

				szChkCd = yddatautil.setDataDefault(inDto[0].getField("CHK_CD"), "");


				if(!szStlNo.equals("") && szChkCd.equals("1")){

					returnSet = JDTORecordFactory.getInstance().createRecordSet("YD");
					setRecord = JDTORecordFactory.getInstance().create();
		    		setRecord.setField("YD_CAR_SCH_ID",	yddatautil.setDataDefault(inDto[iLoop].getField("YD_CAR_SCH_ID"), "") );
		    		setRecord.setField("STL_NO",		szStlNo);
		    		//com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getYdCarftmvmtl
					int nRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(setRecord, returnSet, 0);

	//				차량 이송재료 등록
		    		if(nRtnVal == 1) {
		    			setRecord = JDTORecordFactory.getInstance().create();
		    			setRecord.setField("MODIFIER",	szModifier);
			    		setRecord.setField("DEL_YN",        "N");
			    		setRecord.setField("YD_STK_BED_NO", szStkBedNo) ;
			    		setRecord.setField("YD_STK_LYR_NO", szStkLyrNo) ;
			    		setRecord.setField("HCR_GP",	    yddatautil.setDataDefault(inDto[iLoop].getField("HCR_GP"), "") );
			    		setRecord.setField("STL_PROG_CD",	yddatautil.setDataDefault(inDto[iLoop].getField("STL_PROG_CD"), "") );
			    		setRecord.setField("YD_MTL_ITEM",	yddatautil.setDataDefault(inDto[iLoop].getField("YD_MTL_ITEM"), "") );
			    		setRecord.setField("YD_ROUTE_GP",	yddatautil.setDataDefault(inDto[iLoop].getField("YD_ROUTE_GP"), "") );
			    		setRecord.setField("YD_CAR_SCH_ID",	yddatautil.setDataDefault(inDto[iLoop].getField("YD_CAR_SCH_ID"), "") );
			    		setRecord.setField("STL_NO",		szStlNo);

			    		/*com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.updYdCarftmvmtl*/
			    		intRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(setRecord, 0) ;
			    		if(intRtnVal<0) {
		                    szMsg = "YdCarftmvmtl Table 저장위치 수정 실패";
		                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		        		}

				    //차량 이송재료 등록
		    		}else if(nRtnVal == 0) {
		    			setRecord = JDTORecordFactory.getInstance().create();
		    			setRecord.setField("REGISTER",	szModifier);
			    		setRecord.setField("DEL_YN",        "N");
			    		setRecord.setField("YD_STK_BED_NO", szStkBedNo) ;
			    		setRecord.setField("YD_STK_LYR_NO", szStkLyrNo) ;
			    		setRecord.setField("HCR_GP",	    yddatautil.setDataDefault(inDto[iLoop].getField("HCR_GP"), "") );
			    		setRecord.setField("STL_PROG_CD",	yddatautil.setDataDefault(inDto[iLoop].getField("STL_PROG_CD"), "") );
			    		setRecord.setField("YD_MTL_ITEM",	yddatautil.setDataDefault(inDto[iLoop].getField("YD_MTL_ITEM"), "") );
			    		setRecord.setField("YD_ROUTE_GP",	yddatautil.setDataDefault(inDto[iLoop].getField("YD_ROUTE_GP"), "") );
			    		setRecord.setField("YD_CAR_SCH_ID",	yddatautil.setDataDefault(inDto[iLoop].getField("YD_CAR_SCH_ID"), "") );
			    		setRecord.setField("STL_NO",		szStlNo);

			    		/*com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.insYdCarftmvmtl*/
			    		intRtnVal = ydCarFtmvMtlDao.insYdCarftmvmtl(setRecord) ;
			    		if(intRtnVal<0) {
		                    szMsg = "YdCarftmvmtl Table 저장위치 등록 실패";
		                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		        		}
		    		}


				} else if(!szStlNo.equals("") && szChkCd.equals("2")) {
					
					returnSet2 = JDTORecordFactory.getInstance().createRecordSet("YD");
					setRecord = JDTORecordFactory.getInstance().create();
		    		setRecord.setField("YD_CAR_SCH_ID",	yddatautil.setDataDefault(inDto[iLoop].getField("YD_CAR_SCH_ID"), "") );
		    		setRecord.setField("STL_NO",		szStlNo);
		    		
		    		//com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getYdCarftmvmtl
					int nRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(setRecord, returnSet, 0);
					
					if(nRtnVal == 1) {
		    			setRecord = JDTORecordFactory.getInstance().create();
		    			setRecord.setField("MODIFIER",	szModifier);
			    		setRecord.setField("DEL_YN",        "N");
			    		setRecord.setField("YD_CAR_UPP_LOC_CD", "*");
			    		setRecord.setField("YD_CAR_SCH_ID",	yddatautil.setDataDefault(inDto[iLoop].getField("YD_CAR_SCH_ID"), "") );
			    		setRecord.setField("STL_NO",		szStlNo);

			    		/*com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.updYdCarftmvmtlUppLocCd*/
			    		intRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(setRecord, 12) ;
			    		if(intRtnVal<0) {
		                    szMsg = "YdCarftmvmtl Table 저장위치 변경 표시 실패(통합야드)";
		                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		        		}
					}
					
				}


				//**************************************************************
				// 재료 품목에 따른 공통테이블 정보 변경

				newPara = JDTORecordFactory.getInstance().create();
				newPara.setField("STL_NO", szStlNo);

				//재료정보가 없으면 조회할 필요없이 Continue
				if(szStlNo.equals("")){
					continue;
				}

				slabCommRecSet =	JDTORecordFactory.getInstance().createRecordSet("YdSlabTemp");
				intRtnVal =ydStockDao.getYdStock(newPara, slabCommRecSet, 0);

				slabCommRecSet.first();

				if(slabCommRecSet.size() <1 ) {

					//공통 테이블 UPDATE 안됨
					continue;

				}


				newPara = slabCommRecSet.getRecord(0);
				szMtlItem =  yddatautil.setDataDefault(newPara.getField("YD_MTL_ITEM"),"");

	        	szSTL_NO = szStlNo;
	        	getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	        	szRtnMsg = YdCommonUtils.getPtCommStock(szSTL_NO, getRecSet);


	        	if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) {
	        		szLogMsg = "[저장위치 Setting - Y1setYdStrLoc]재료[" + szSTL_NO + "]를 공통테이블에서 조회 시 오류발생";
	                ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
	                //return -100;
	                continue;
	        	}

	        	getRecSet.absolute(1);
	        	getRecord      = JDTORecordFactory.getInstance().create();
	        	getRecord 	   = getRecSet.getRecord();


	        	szYdStrLoc 	   = ydDaoUtils.paraRecChkNull(getRecord,"YD_STR_LOC").trim();
	        	szYdStrLocHis1 = ydDaoUtils.paraRecChkNull(getRecord,"YD_STR_LOC_HIS1").trim();
	        	szPT_TB_COMM = ydDaoUtils.paraRecChkNull(getRecord, "PT_TB_COMM");



	        	/*
	        	 * 권하위치의 야드구분이 다른 경우에는 입고일자와 입고시각을 업데이트 처리
	        	 * 주편공통, 슬라브공통 : 입고일자, 입고시각
	        	 * PLATE공통 : 입고일자
	        	 * 수정자 : 임춘수
	        	 * 일자 : 2009.07.14
	        	 */
	        	//공통테이블에 저장되어 있는 야드구분

	        	setRecord = JDTORecordFactory.getInstance().create();
	        	setRecord.setField("YD_GP",         szStkColGp.substring(0,1));
	        	setRecord.setField("YD_BAY_GP",     szStkColGp.substring(1,2));
	        	setRecord.setField("YD_EQP_GP",     szStkColGp.substring(2,4));
	        	setRecord.setField("YD_STK_COL_NO", szStkColGp.substring(4,6));
	        	setRecord.setField("YD_STK_BED_NO", szStkBedNo);
	        	setRecord.setField("YD_STK_LYR_NO", szStkLyrNo);
	        	setRecord.setField("FNL_REG_PGM",   "updSlabYdStkcarFix");

	        	szYdGp =  szStkColGp.substring(0,1);

	        	szYD_GP	= ydDaoUtils.paraRecChkNull(getRecord,"YD_GP");
	        	if( !szYD_GP.equals(szYdGp) ) {
	        		String szCurDateTime = YdUtils.getCurDate("yyyyMMddHHmmss");
	        		String szRECEIPT_DATE = szCurDateTime.substring(0, 8);
	        		String RECEIPT_TIME = szCurDateTime.substring(8);
	        		if(szMtlItem.equals("B") || szMtlItem.equals("S") ) {
	        			setRecord.setField("RECEIPT_DATE", 	szRECEIPT_DATE);				//입고일자
	        			setRecord.setField("RECEIPT_TIME", 	RECEIPT_TIME);					//입고시각
	        		}else if ( szMtlItem.equals("P") ) {
	        			setRecord.setField("RECEIPT_DATE", 	szRECEIPT_DATE);				//입고일자
	        		}
	        	}

	        	setRecord.setField("MODIFIER",  yddatautil.setDataDefault( inDto[iLoop].getField("MODIFIER"),"YD"));


	        	//전저장위치 = szYdStrLoc,	 전전저장위치 = szYdStrLocHis1
		        if(szYdStrLoc.equals("")){
		        	setRecord.setField("YD_STR_LOC_HIS1", 	"");
		        }else{
		        	setRecord.setField("YD_STR_LOC_HIS1", 	szYdStrLoc);
		        }

		        if(szYdStrLocHis1.equals("")){
		        	setRecord.setField("YD_STR_LOC_HIS2", 	"");
		        }else{
		        	setRecord.setField("YD_STR_LOC_HIS2", 	szYdStrLocHis1);
		        }


	        	//현재 저장위치 = 실적위치+실적단 주편일경우와 슬라브일경우


		        szMsg = "전 저장위치 : " + szYdStrLoc + " , 전전 저장위치 : " + szYdStrLocHis1 + ", 권하실적 단 정보 : " + szStkLyrNo;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        	if(szPT_TB_COMM.equals("B")){

	        		setRecord.setField("MSLAB_NO",   szStlNo);
	        		setRecord.setField("YD_STR_LOC", szStkColGp+ szStkBedNo+szStkLyrNo.substring(1,3));

	        		//주편 공통 업데이트
	        		intRtnVal = this.updY1YdStock(setRecord, 2);
	        		if(intRtnVal<0) {
	                    szMsg = "주편공통Table 저장위치 등록 실패";
	                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        			//return intRtnVal ;
	        		}

//	        		//주편정정마감EVENT (YMCSJ001) 2012.01.05
//	        		EJBConnector ejbConn2 = new EJBConnector("default","CraneUdHdSeEJB",this);
//					ejbConn2.trx("procYMCSJ001",new Class[]{String.class}, new Object[]{szStlNo});

	        	}else if (szPT_TB_COMM.equals("S")) {

	        		setRecord.setField("SLAB_NO",   szStlNo);
	        		setRecord.setField("YD_STR_LOC", szStkColGp+ szStkBedNo+szStkLyrNo.substring(1,3));

	        		//슬라브 공통 업데이트
	        		intRtnVal = this.updY1YdStock(setRecord,  0);
	        		if(intRtnVal<0) {
	                    szMsg = "슬라브공통Table 저장위치 등록 실패";
	                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        		//	return intRtnVal ;
	        		}
	         	}




			}

			szMsg = "JSP-SESSION [updSlabYdStkcarFix_Tot - 통합슬라브야드] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{

		}
		return YdConstant.RETN_CD_SUCCESS;
	}	// end of updSlabYdStkcarFix_Tot




	/**
	 * 오퍼레이션명 : 차량 작업 진행관리 (상하차 작업)-사용안함
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procSydCarWrkStatCtr(JDTORecord msgRecord)throws JDTOException  {
		//TC_CODE :YDYDJ630

		YdDelegate ydDelegate = new YdDelegate();
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		YdCrnSchDao     ydCrnSchDao     = new YdCrnSchDao();
		YdCarSchDao     ydCarSchDao     = new YdCarSchDao();


		JDTORecordSet rsResult          = null;
		JDTORecordSet rsWrkBookMtl      = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recFirst          = null;
		JDTORecord    recLast           = null;

	    int intRtnVal 		   = 0 ;

	    String szMsg           = "";
	    String szMethodName    = "procSydCarWrkStatCtr";

	    String szCAR_LDUD_GP   = "";
	    String szYD_WBOOK_ID   = "";
	    String szYD_SCH_CD      = "";
	    String szYD_GP          = "";
	    String szYD_CAR_SCH_ID  = "";
	    String szYD_CAR_USE_GP  = "";
	    String szYD_STK_COL_GP  = "";


	    try{
	    	//상하차구분 플래그, 작업예약id, 크레인스케줄id
	    	szCAR_LDUD_GP   = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_LDUD_GP");
	    	szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_ID");
	    	szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
	    	szYD_GP			= szYD_STK_COL_GP.substring(0,1);

//	    	//작업예약id로 크레인 스케줄을 조회
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//	    	recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
////	    	recInTemp.setField("YD_EQP_GP", szYD_DN_WR_LOC.substring(2, 4));
//
//	    	if(szCAR_LDUD_GP.equals("L")){
//	    		intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 31);
//		    	if(intRtnVal <= 0) {
//					szMsg="작업예약재료로 크레인 스케줄을 조회 중 Error 상차인경우";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					throw new JDTOException("<procY0CarWrkStatCtr> getYdCrnsch" + szMsg);
//		    	}
//	    	}else{
//	    		intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 32);
//		    	if(intRtnVal <= 0) {
//					szMsg="작업예약재료로 크레인 스케줄을 조회 중 Error 하차인경우";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					throw new JDTOException("<procY0CarWrkStatCtr> getYdCrnsch" + szMsg);
//		    	}
//	    	}
//
//			szMsg = "rsResult 사이즈 : " + rsResult.size();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//	    	rsResult.first();
//	    	recFirst = JDTORecordFactory.getInstance().create();
//	    	recFirst.setRecord(rsResult.getRecord());
//	    	szFST_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(recFirst, "YD_CRN_SCH_ID");
//
//			szMsg = "첫번째 스케줄id : " + szFST_CRN_SCH_ID;
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//	    	rsResult.last();
//	    	recLast = JDTORecordFactory.getInstance().create();
//	    	recLast.setRecord(rsResult.getRecord());
//	    	szLST_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(recLast, "YD_CRN_SCH_ID");
//
//			szMsg = "마지막 스케줄id : " + szLST_CRN_SCH_ID;
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//			szMsg = "전문 스케줄id : " + szYD_CRN_SCH_ID;
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//	    	szYD_SCH_CD      = ydDaoUtils.paraRecChkNull(recFirst, "YD_SCH_CD");
//	    	szYD_GP          = szYD_SCH_CD.substring(0,1);

	    	//플래그가 상차인경우
	    	if(szCAR_LDUD_GP.equals("U")) {
	    		//차량스케줄 id를 조회
	    		recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    		ydCarSchDao.getYdCarsch(recInTemp, rsResult, 0);
	    		rsResult.absolute(1);
	    		recOutTemp = JDTORecordFactory.getInstance().create();
	    		recOutTemp.setRecord(rsResult.getRecord());
	    		szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_USE_GP");

    			//동일하면 차량스케줄에 상차완료일시 등록, 설비작업상태 = '영차'
    			recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_CAR_PROG_STAT", "5");
    			recInTemp.setField("YD_EQP_WRK_STAT", "L");
    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
    			intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 1);
    			if(intRtnVal <= 0) {
					szMsg="차량스케줄에 상차 완료 등록시 Error!! Code : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}

	    		//출하차량인 경우에만 적용한다.
	    		if(szYD_CAR_USE_GP.equals("G")){

					szMsg="일품 상차실적 송신 YDDMR013 (외판슬라브일품출하상차실적)";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("MSG_ID",        "YDDMR013");

					recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
					recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recInTemp.setField("YD_GP",         szYD_GP);

					ydDelegate.sendMsg(recInTemp);
	    		}

    			recInTemp = JDTORecordFactory.getInstance().create();
    			//구내운송
    			if(szYD_CAR_USE_GP.equals("L")){							//구내운송 - 임춘수 수정 2009.06.15
    				//--------------- 상차완료시 공통테이블 업데이트 처리 시작 2009.06.08 임춘수 추가 ----------------------
    				intRtnVal = YdCommonUtils.procCarLoadCmpl(szYD_CAR_SCH_ID);
    				if( intRtnVal <= 0 ) {
    					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
    					szMsg="상차완료시 공통테이블 업데이트 처리 실패";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    				}else{
    					szMsg="상차완료시 공통테이블 업데이트 처리 성공";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				}
    				//--------------- 상차완료시 공통테이블 업데이트 처리 끝 ----------------------

	    			//상차작업완료 송신 YDTSJ008 (구내운송 상차완료)
	    			recInTemp.setField("MSG_ID",        "YDTSJ008");
					szMsg="구내운송 상차작업완료 송신 : YDTSJ008";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}else{
	    			//상차작업완료 송신 YDDMR017 (외판슬라브출하상차완료)
	    			recInTemp.setField("MSG_ID",        "YDDMR017");
					szMsg="외판슬라브출하상차완료 송신 : YDDMR017";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}

    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
    			recInTemp.setField("YD_GP",         szYD_GP);

    			ydDelegate.sendMsg(recInTemp);


				szMsg="상차작업완료 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


	    	//플래그가 하차인 경우
	    	}else if(szCAR_LDUD_GP.equals("L"))	{
	    		//차량스케줄 id를 조회
	    		recInTemp  = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szYD_WBOOK_ID);
    			rsResult   = JDTORecordFactory.getInstance().createRecordSet("");
	    		ydCarSchDao.getYdCarsch(recInTemp, rsResult, 3);
	    		rsResult.absolute(1);
	    		recOutTemp = JDTORecordFactory.getInstance().create();
	    		recOutTemp.setRecord(rsResult.getRecord());
	    		szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_SCH_ID");

	    		recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
    			recInTemp.setField("YD_GP",         szYD_GP);

    			//동일하면 차량스케줄에 하차완료일시 등록
    			recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_EQP_WRK_STAT", "U");
    			recInTemp.setField("YD_CAR_PROG_STAT", "E");
    			recInTemp.setField("DEL_YN",           "N");
    			recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szYD_WBOOK_ID);
    			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recInTemp, 3);
    			if(intRtnVal <= 0) {
					szMsg="차량스케줄에 하차완료일시  등록시 Error!! Code : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}

    			//---------------- 하차완료시 공통테이블 업데이트 시작 2009.06.08 임춘수 추가
				szMsg="szYD_CAR_SCH_ID = " + szYD_CAR_SCH_ID;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			intRtnVal = YdCommonUtils.procCarUnLoadCmpl(szYD_CAR_SCH_ID);
				if( intRtnVal <= 0 ) {
					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
					szMsg="하차완료시 공통테이블 업데이트 처리 실패";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					szMsg="하차완료시 공통테이블 업데이트 처리 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
    			//-----------------하차완료시 공통테이블 업데이트 시작 2009.06.08 임춘수 끝

    			//하차작업완료 송신 YDTSJ010 - 구내운송 전송
				recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("MSG_ID",        "YDTSJ010");
    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
    			recInTemp.setField("YD_GP",         szYD_GP);
    			ydDelegate.sendMsg(recInTemp);
    			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    	         * 			진행관리 슬라브소재이송완료실적전송  - YDPTJ001
    	         * 업무기준 Desc : 1. 하차완료시
    	         * 스케줄코드 :  1. 차량하차 스케줄 : PT(팔렛트), TR(트레일러), LM(하차)
    	         * 기능 추가 : 임춘수
    	         * 일자 : 2009.06.16
    	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    			recInTemp.setField("MSG_ID",        "YDPTJ001");
    			ydDelegate.sendMsg(recInTemp);
    	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

				szMsg="하차작업완료 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


	    	}else{
				szMsg="상차 및 하차 구분 플래그 Error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new JDTOException("<procY0CarWrkStatCtr> " + szMsg);
	    	}

		}catch(Exception e){

			szMsg="차량 작업 진행관리 처리 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException("<procY0CarWrkStatCtr> getYdCrnsch" + szMsg);
		}

		szMsg="차량 작업 진행관리 처리"+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return szYD_CAR_SCH_ID;
	} //end of procSydCarWrkStatCtr()


	/**
	 * 차량작업관리화면 상차완료처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String complCarLdLot2(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara        = JDTORecordFactory.getInstance().create();
		JDTORecord recInTemp			= JDTORecordFactory.getInstance().create();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		String szMsg        			= "";
		String szMethodName 			= "complCarLdLot2";
		String szOperationName 			= "상차완료처리";
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
		
		ydUtils.putLog(szSessionName, szMethodName, "SlabJspSeEJBBean.complCarLdLot2 시작", YdConstant.DEBUG);

		YdDelegate ydDelegate = new YdDelegate();

		String szYD_CAR_SCH_ID 	= null;
		String szARR_WLOC_CD   	="";
		String szYD_EQP_ID  	="";
		String szYD_CAR_USE_GP  	="";
		String szYD_PREP_SCH_ID  	="";
		int intRtnVal =  0;
		//회송처리 수정자 
		String szMODIFIER = "";
		try {

			szYD_CAR_SCH_ID				= ydDaoUtils.paraRecChkNull(inDto, "YD_CAR_SCH_ID");
			szARR_WLOC_CD				= ydDaoUtils.paraRecChkNull(inDto, "ARR_WLOC_CD");
			szYD_EQP_ID					= ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_ID");
			szYD_CAR_USE_GP				= ydDaoUtils.paraRecChkNull(inDto, "YD_CAR_USE_GP");
			szYD_PREP_SCH_ID			= ydDaoUtils.paraRecChkNull(inDto, "LOT_ID");
			szMODIFIER				= ydDaoUtils.paraRecChkNull(inDto, "MODIFIER");
			
			if(szYD_CAR_SCH_ID.equals("") && szARR_WLOC_CD.equals("")&& szYD_CAR_USE_GP.equals("")){
				return YdConstant.RETN_CD_FAILURE ;
			}
//PIDEV			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APPPI0", "S", "*");
			if("PIDEV".equals("PIDEV")) {
				return complCarLdLot2_PIDEV(inDto);
			}	
			//--------------------------------------------------------------
			//	차량정보 상차완료 처리
			//--------------------------------------------------------------
			JDTORecord setRecord = JDTORecordFactory.getInstance().create();
			setRecord.setField("YD_CAR_PROG_STAT",		"5"); //상차완료
			if(!szARR_WLOC_CD.equals("")){
				setRecord.setField("ARR_WLOC_CD",	szARR_WLOC_CD );
			}
			setRecord.setField("YD_EQP_WRK_STAT",		"L");
			setRecord.setField("YD_CAR_SCH_ID",	szYD_CAR_SCH_ID);
			setRecord.setField("YD_CARLD_CMPL_DT", 	ydUtils.getCurDate("yyyyMMddHHmmss"));
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarsch*/
			intRtnVal = ydCarSchDao.updYdCarsch(setRecord, 0);
			if(intRtnVal <= 0) {
				szMsg="차량스케줄에 상차 완료 등록시 Error!! Code : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}


    		if(szYD_CAR_USE_GP.equals("G")){
    			//--------------------------------------------------------------
    			//상차작업개시 송신 YDDMR009 (외판슬라브출하상차개시)
    			//--------------------------------------------------------------
    			szMsg="[권상실적처리]상차작업개시 송신 YDDMR009 (외판슬라브출하상차개시) 송신 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				setRecord = JDTORecordFactory.getInstance().create();
				setRecord.setField("MSG_ID",        "YDDMR009");
				setRecord.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				setRecord.setField("YD_GP",         "S");

				ydDelegate.sendMsg(setRecord);

    			//--------------------------------------------------------------
    			//일품 상차실적 송신 YDDMR013 (외판슬라브일품출하상차실적)
    			//--------------------------------------------------------------
				szMsg="일품 상차실적 송신 YDDMR013 (외판슬라브일품출하상차실적)";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				setRecord = JDTORecordFactory.getInstance().create();
				setRecord.setField("MSG_ID",        "YDDMR013");
				setRecord.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				setRecord.setField("YD_GP",         "S");

				ydDelegate.sendMsg(setRecord);

				//--------------------------------------------------------------
    			//상차작업완료 송신 YDDMR017 (외판슬라브출하상차완료)
				//--------------------------------------------------------------

				szMsg="외판슬라브출하상차완료 송신 : YDDMR017";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				setRecord = JDTORecordFactory.getInstance().create();
				setRecord.setField("MSG_ID",        "YDDMR017");
				setRecord.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				setRecord.setField("YD_GP",         "S");

				ydDelegate.sendMsg(setRecord);

    		}else{

				//--------------------------------------------------------------
				//	구내운송으로 상차작업개시완료 송신 YDTSJ007 (구내운송 상차개시)
				//--------------------------------------------------------------
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MSG_ID",        "YDTSJ007");
				recInTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD); //착지개소코드
				recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);

				//ydDelegate.sendMsg(recInTemp);
				
				//Thread.sleep(3000);		//구내운송 상차개시 TC 호출 후 자체 재조회 로직 시간 대기.(3초 여유)
				
				szMsg="상차작업개시 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


				//--------------------------------------------------------------
				//	구내운송으로 상차작업완료 송신 YDTSJ008 (구내운송 상차완료)
				//--------------------------------------------------------------
				//--------------- 상차완료시 공통테이블 업데이트 처리 시작 ----------------------
				intRtnVal = YdCommonUtils.procCarLoadCmpl(szYD_CAR_SCH_ID);
				if( intRtnVal <= 0 ) {
					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
					szMsg="상차완료시 공통테이블 업데이트 처리 실패";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					szMsg="상차완료시 공통테이블 업데이트 처리 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				//--------------- 상차완료시 공통테이블 업데이트 처리 끝 ----------------------

				JDTORecord inRec = JDTORecordFactory.getInstance().create();
				inRec.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);

				intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(inRec, 8);
				if(intRtnVal == 0 ){
					szMsg = "[Jsp Session  -  " + szOperationName +"] 완료시간 업데이트 할  작업이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}
				else{
					szMsg = "[Jsp Session  -  " + szOperationName +"] 상차완료시간 업데이트 하였습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}

				recPara.setField("MSG_ID",        "YDTSJ008");
				recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);

				ydDelegate.sendMsg(recPara);


				szMsg="[Jsp Session:"+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 구내운송으로 상차작업완료 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//-------------------------------------------------------------


				//준비스케줄 삭제처리
				String szReturnMsg = YdCommonUtils.deletePreSch(szYD_PREP_SCH_ID, "", szMethodName);

    		}


			/*
			 * 이력테이블등록호출
			 */
			{
				CrnSchSeEJBBean crnSchSeEJBBean = new CrnSchSeEJBBean();

				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_EQP_ID",             szYD_EQP_ID);
				recInTemp.setField("YD_CAR_SCH_ID",         szYD_CAR_SCH_ID);
				recInTemp.setField("YD_PREP_SCH_ID",        szYD_PREP_SCH_ID);

				// 이력테이블에 INSERT
				//com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdWrkHistbackup
				intRtnVal = ydWrkHistDao.insYdWrkHistS(recInTemp);

				if(intRtnVal<0) {
					szMsg = "(" + szYD_CAR_SCH_ID + ")에 대한 INSERT가 실패하였습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE ;
				}


				szMsg="[" + szOperationName + "] 이력테이블등록호출";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			/**********************************************************
			* 연주야드 적치 최적화 통합야드 출고 이력 추가
			**********************************************************/
			recInTemp.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID); //크레인스케줄ID
			recInTemp.setField("EQP_CD"			, "SS"); //설비코드 --통합야드
			recInTemp.setField("EQP_GP"   		, "SS"  ); //설비구분 --통합야드
			recInTemp.setField("MODIFIER" 		, szMODIFIER  ); //수정자
			
			slabYdCommDao.update(recInTemp, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdEqpIOHistByCarSch", logId, szMethodName, "통합야드 출고이력");
			
			
			

			ydUtils.putLog(szSessionName, szMethodName, "SlabJspSeEJBBean.complCarLdLot2 종료", YdConstant.DEBUG);

		}catch(JDTOException e) {
			szMsg="차량작업관리화면 상차완료 처리 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return szRtnMsg;
	}


	/**
	 * 차량작업관리화면 하차완료처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String complCarUdLot2(JDTORecord inDto) throws DAOException {
		JDTORecord  recPara         = JDTORecordFactory.getInstance().create();
		JDTORecord	recInTemp		= JDTORecordFactory.getInstance().create();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		
		JDTORecordSet outRecSet		=JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord 	  getTcarRecord 		= JDTORecordFactory.getInstance().create();
		String szMsg        			= "";
		String szMethodName 			= "complCarUdLot2";
		String szOperationName 			= "하차완료처리";
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;

		YdDelegate ydDelegate = new YdDelegate();

		String szYD_CAR_SCH_ID 	= null;
		String szARR_WLOC_CD   	="";
		String szYD_EQP_ID  	="";
		String szYD_CAR_USE_GP  ="";
		String szTRN_EQP_CD		="";
		String szYD_CARUD_STOP_LOC ="SA";
		String szMODIFIER = "";
		String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
		int intRtnVal =  0;
		int intCarMtlRtnVal = 0;
		try {
			///**********  플래그 표시한 재료수와 전체 재료수 비교
			szYD_CAR_SCH_ID				= ydDaoUtils.paraRecChkNull(inDto, "YD_CAR_SCH_ID");
			szARR_WLOC_CD				= ydDaoUtils.paraRecChkNull(inDto, "ARR_WLOC_CD");
			szYD_EQP_ID					= ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_ID");
			szYD_CAR_USE_GP				= ydDaoUtils.paraRecChkNull(inDto, "YD_CAR_USE_GP");
			szTRN_EQP_CD				= ydDaoUtils.paraRecChkNull(inDto, "TRN_EQP_CD");
			szYD_CARUD_STOP_LOC			= slabUtils.nvl(ydDaoUtils.paraRecChkNull(inDto, "STKPOS"),"SA");
			
			//회송처리 수정자 
			szMODIFIER				= ydDaoUtils.paraRecChkNull(inDto, "MODIFIER");

			if(szYD_CAR_SCH_ID.equals("") && szARR_WLOC_CD.equals("")&& szYD_CAR_USE_GP.equals("")){

				JDTORecord setRecord = JDTORecordFactory.getInstance().create();
				setRecord.setField("TRN_EQP_CD",	szTRN_EQP_CD);
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschDaoTrnEqpCd*/
				intRtnVal = ydCarSchDao.getYdCarsch(setRecord,outRecSet, 7);
				if(intRtnVal <= 0) {
					szMsg=" 장비번호에 해당하는 차량 정보가 존재 안함: "+szTRN_EQP_CD ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return YdConstant.RETN_CD_FAILURE ;
				}

				// 차량스케줄 Data
		    	outRecSet.first() ;
		    	getTcarRecord = outRecSet.getRecord() ;
		    	// 차량스케줄 ID를 추출한다
		    	szYD_CAR_SCH_ID 	= ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_CAR_SCH_ID");
		    	szARR_WLOC_CD 		= ydDaoUtils.paraRecChkNull(getTcarRecord, "ARR_WLOC_CD");

			}

			//--------------------------------------------------------------
			//	차량정보에서 마지막 하차완료  유무 체크
			//--------------------------------------------------------------
			JDTORecord setRecord = JDTORecordFactory.getInstance().create();
			setRecord.setField("YD_CAR_SCH_ID",	szYD_CAR_SCH_ID);
			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdCarSchhackaCheck*/
			intCarMtlRtnVal = ydCarSchDao.getYdCarsch(setRecord,outRecSet, 402);
			if(intCarMtlRtnVal > 0) {
				szMsg="차량정보에서 마지막 하차완료  유무 체크 남은 하차대상카운트: " + intCarMtlRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				// ** 회송시스템 개발을 위한 임시 주석처리
				/*getTcarRecord 		= JDTORecordFactory.getInstance().create();
				outRecSet.first() ;
		    	getTcarRecord = outRecSet.getRecord() ;

				if(szTRN_EQP_CD.equals("") || !szYD_CAR_SCH_ID.equals("")){
					return YdConstant.RETN_CD_SUCCESS ;
				}else{
					return YdConstant.RETN_CD_FAILURE ;
				}*/
			}


			if(intCarMtlRtnVal == 0) {
				//--------------------------------------------------------------
				//	차량정보 하차완료 처리
				//--------------------------------------------------------------
				setRecord = JDTORecordFactory.getInstance().create();
				setRecord.setField("YD_CAR_PROG_STAT",		"E"); //하차완료
				
				//동에 따른 개소코드 변경 기준화 -- REQ202206405934 통합야드 U동.V동. 스판 추가요청
				recPara.setField("YD_BAY_GP", szYD_CARUD_STOP_LOC.substring(1 , 2));
				JDTORecordSet jsWlocCd = slabYdCommDao.select(recPara, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getSlabTotYdWlocCdByBay", logId, szMethodName, "동에따른 개소코드GET");
				
				if(jsWlocCd != null && jsWlocCd.size() >0) {
					String wlocCd = jsWlocCd.getRecord(0).getFieldString("WLOC_CD");
					
					szMsg="하차완료처리시 검색된 개소코드 : " + wlocCd;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					if(wlocCd == null || "".equals(wlocCd)) setRecord.setField("ARR_WLOC_CD",	"DJY25" ); //값이 없는경우 통합야드 기본 개소코드
					
					setRecord.setField("ARR_WLOC_CD",	wlocCd );
				}
				else setRecord.setField("ARR_WLOC_CD",	"DJY25" );
				
				setRecord.setField("YD_CAR_SCH_ID",	szYD_CAR_SCH_ID);
				setRecord.setField("YD_CARUD_ST_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
				setRecord.setField("YD_CARUD_CMPL_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarsch*/
				intRtnVal = ydCarSchDao.updYdCarsch(setRecord, 0);
				
				
				if(intRtnVal <= 0) {
					szMsg="차량스케줄에 상차 완료 등록시 Error!! Code : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
	
				
				//통합야드 회송처리 기능. 통합야드에서 보냈다가 회송되어 들어오는 차량 회송이력테이블 처리
				JDTORecord jrParam = JDTORecordFactory.getInstance().create();
				SlabYdL2RcvDAO rcv2Dao = new SlabYdL2RcvDAO();
				if("".equals(szMODIFIER)) szMODIFIER = "complCarUd";
				jrParam.setField("V_MODIFIER"         , szMODIFIER); 
				jrParam.setField("V_YD_CAR_SCH_ID"    ,szYD_CAR_SCH_ID); 
				
				intRtnVal = rcv2Dao.updY1YDL009("RethtHist", jrParam);
				
				if(intRtnVal <=0){
					szMsg="차량 스케쥴 ["+szYD_CAR_SCH_ID+"] 에 대한 회송처리 할 부분 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				else {
					szMsg="차량 스케쥴 ["+szYD_CAR_SCH_ID+"] 에 대한 회송완료처리 건수: " + intRtnVal+" 건";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				
				/**********************************************************
				* 연주야드 적치 최적화 통합야드 입고 이력 추가
				**********************************************************/
				jrParam.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID); //크레인스케줄ID
				jrParam.setField("EQP_CD"			, "SS"); //설비코드
				jrParam.setField("EQP_GP"   		, "SS"  ); //설비구분
				jrParam.setField("MODIFIER" 		, szMODIFIER  ); //수정자
				
				slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.insSlabYdEqpIOHistByCarSch", logId, szMethodName, "통합야드 입고이력");
				
				
				if(szYD_CAR_USE_GP.equals("G")){
	
	
				}else{
					//--------------------------------------------------------------
					//	구내운송으로 하차작업개시완료 송신 YDTSJ009 (구내운송 하차개시)
					//--------------------------------------------------------------
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID",        "YDTSJ009");
					recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recInTemp.setField("YD_CARUD_ST_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
	
					ydDelegate.sendMsg(recInTemp);
	
					szMsg="하차작업개시 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
	
					//--------------------------------------------------------------
					//	구내운송으로 하차작업완료 송신 YDTSJ010 (구내운송 하차완료)
					//--------------------------------------------------------------
					//---------------- 하차완료시 공통테이블 업데이트 시작 2009.06.08 임춘수 추가
					szMsg="szYD_CAR_SCH_ID = " + szYD_CAR_SCH_ID;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					intRtnVal = this.procCarUnLoadCmpl2(szYD_CAR_SCH_ID,szMODIFIER);
					if( intRtnVal <= 0 ) {
						//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
						szMsg="하차완료시 공통테이블 업데이트 처리 실패";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						szMsg="하차완료시 공통테이블 업데이트 처리 성공";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					//-----------------하차완료시 공통테이블 업데이트 시작 2009.06.08 임춘수 끝
	
					//하차작업완료 송신 YDTSJ010 - 구내운송 전송
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID",        "YDTSJ010");
					recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					ydDelegate.sendMsg(recInTemp);
					/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			         * 			진행관리 슬라브소재이송완료실적전송  - YDPTJ001
			         * 업무기준 Desc : 1. 하차완료시
			         * 스케줄코드 :  1. 차량하차 스케줄 : PT(팔렛트), TR(트레일러), LM(하차)
			         * 기능 추가 : 임춘수
			         * 일자 : 2009.06.16
			         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
					recInTemp.setField("MSG_ID",        "YDPTJ001");
					ydDelegate.sendMsg(recInTemp);
			        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	
	
					szMsg="[Jsp Session:"+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 구내운송으로 하차작업완료 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					//-------------------------------------------------------------
	
				}
			} else if(intCarMtlRtnVal > 0){
				//하차 대상재가 남아있는 경우
				//이미 하차한 대상재는 먼저 공통테이블 수정
				
				//--------------------------------------------------------------
				//	차량정보 하차개시 처리
				//--------------------------------------------------------------
				setRecord = JDTORecordFactory.getInstance().create();
				setRecord.setField("YD_CAR_PROG_STAT",		"D"); //하차개시
				
				
				//동에 따른 개소코드 변경 기준화 -- REQ202206405934 통합야드 U동.V동. 스판 추가요청
				recPara.setField("YD_BAY_GP", szYD_CARUD_STOP_LOC.substring(1 , 2));
				JDTORecordSet jsWlocCd = slabYdCommDao.select(recPara, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getSlabTotYdWlocCdByBay", logId, szMethodName, "인터락설정ID Select");
				
				if(jsWlocCd != null && jsWlocCd.size() >0) {
					String wlocCd = jsWlocCd.getRecord(0).getFieldString("WLOC_CD");
					
					szMsg="하차완료처리시 검색된 개소코드 : " + wlocCd;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					if(wlocCd == null || "".equals(wlocCd)) setRecord.setField("ARR_WLOC_CD",	"DJY25" ); //값이 없는경우 통합야드 기본 개소코드
					
					setRecord.setField("ARR_WLOC_CD",	wlocCd );
				}
				else setRecord.setField("ARR_WLOC_CD",	"DJY25" );					
				
				/*
				if("U".equals(szYD_CARUD_STOP_LOC.substring(1 , 2)) || "V".equals(szYD_CARUD_STOP_LOC.substring(1 , 2)) ||"W".equals(szYD_CARUD_STOP_LOC.substring(1 , 2)) ){
					setRecord.setField("ARR_WLOC_CD",	"DYY15" ); //2냉연 슬라브 야드 (현재 통합야드 U, V, W동)
				}else if("X".equals(szYD_CARUD_STOP_LOC.substring(1 , 2))){
					setRecord.setField("ARR_WLOC_CD",	"BSY01" ); //서문 슬라브 야드 (건설 제작장 부지)
				}else if("Y".equals(szYD_CARUD_STOP_LOC.substring(1 , 2))){
					setRecord.setField("ARR_WLOC_CD",	"BSY02" ); //특수강 정정 옥외 슬라브 야드
				}else if("Z".equals(szYD_CARUD_STOP_LOC.substring(1 , 2))){
					setRecord.setField("ARR_WLOC_CD",	"BSY03" ); //철분말 공장 슬라브 야드
				}else{
					setRecord.setField("ARR_WLOC_CD",	"DJY25" ); //(비상야드추가)
				}*/
				
				setRecord.setField("YD_CAR_SCH_ID",	szYD_CAR_SCH_ID);
				setRecord.setField("YD_CARUD_ST_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
				//setRecord.setField("YD_CARUD_CMPL_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
				
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarsch*/
				intRtnVal = ydCarSchDao.updYdCarsch(setRecord, 0);
				if(intRtnVal <= 0) {
					szMsg="차량스케줄에 상차 완료 등록시 Error!! Code : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				
				
				if(szYD_CAR_USE_GP.equals("G")){
					
				}else{
					//--------------------------------------------------------------
					//	구내운송으로 하차작업개시완료 송신 YDTSJ009 (구내운송 하차개시)
					//--------------------------------------------------------------
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID",        "YDTSJ009");
					recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recInTemp.setField("YD_CARUD_ST_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
	
					ydDelegate.sendMsg(recInTemp);
	
					szMsg="하차작업개시 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
	
				
				//--------------------------------------------------------------
				//	부분하차 공통테이블 업데이트 시작
				//--------------------------------------------------------------
				szMsg="szYD_CAR_SCH_ID = " + szYD_CAR_SCH_ID + " 부분하차 공통테이블 업데이트 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				intRtnVal = this.procCarUnLoadCmpl2(szYD_CAR_SCH_ID,szMODIFIER);
				
				if( intRtnVal <= 0 ) {
					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
					szMsg="하차완료시 공통테이블 업데이트 처리 실패";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					szMsg="하차완료시 공통테이블 업데이트 처리 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}


			/*
			 * 이력테이블등록호출
			 */
			if(intCarMtlRtnVal == 0) {

				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_EQP_ID",             szYD_EQP_ID);
				recInTemp.setField("YD_CAR_SCH_ID",         szYD_CAR_SCH_ID);


				// 이력테이블에 INSERT
				//com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdWrkHistbackup
				intRtnVal = ydWrkHistDao.insYdWrkHistS(recInTemp);

				if(intRtnVal<0) {
					szMsg = "(" + szYD_CAR_SCH_ID + ")에 대한 INSERT가 실패하였습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE ;
				}


				szMsg="[" + szOperationName + "] 이력테이블등록호출";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			} else if(intCarMtlRtnVal > 0) {
				
				JDTORecord setRecord2 = JDTORecordFactory.getInstance().create();
				setRecord2.setField("YD_CAR_SCH_ID",	szYD_CAR_SCH_ID);
				/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdCarSchhackaCheck*/
				intRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(setRecord2,outRecSet, 13);
				
				if(intRtnVal > 0) {
					//정보 존재시 이력테이블에 INSERT
					outRecSet.first();

					do{
						recInTemp   = 	JDTORecordFactory.getInstance().create();
						recInTemp   =  outRecSet.getRecord();

						recInTemp.setField("YD_EQP_ID",             szYD_EQP_ID);
						recInTemp.setField("YD_CAR_SCH_ID",         szYD_CAR_SCH_ID);
						//com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdWrkHistCarMvMtl
						intRtnVal = ydWrkHistDao.insYdWrkHistSC(recInTemp);
						
						if(intRtnVal<0) {
							szMsg = "(" + szYD_CAR_SCH_ID + ")에 대한 INSERT가 실패하였습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE ;
						}

					}while(outRecSet.next());
					
				} else if(intRtnVal < 0){
					szMsg = "(" + szYD_CAR_SCH_ID + ")에 대한 저장위치 변경 슬라브 SELECT가 실패하였습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE ;
				}
				
				szMsg="[" + szOperationName + "] 이력테이블등록호출";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}

		}catch(JDTOException e) {
			szMsg="차량작업관리화면 상차완료 처리 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}

		return szRtnMsg;
	}


	/**
	 * 하차완료시 공통업무처리 - 진행관리[PT] 공통테이블 업데이트
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szYD_CAR_SCH_ID
	 * @return
	 * @throws DAOException
	 */
	public int procCarUnLoadCmpl2(String szYD_CAR_SCH_ID, String szMODIFIER) throws DAOException {
		//리턴값
		int intRtnVal 						= 0;
		//메세지
		String szMsg 						= null;
		String szRtnMsg 					= null;
		//메소드명
		String szMethodName    				= "procCarUnLoadCmpl";
		String szOperationName 				= "하차완료시 공통업무처리";
		//레코드셋
		JDTORecordSet rsResult 				= null;
		JDTORecordSet rsResultTemp 			= null;
		JDTORecordSet getRecSet 			= null;
		//레코드
		JDTORecord recInTemp 				= null;
		JDTORecord recTemp 					= null;
		JDTORecord recOutTemp 				= null;
		JDTORecord recSend 					= null;
		JDTORecord recSendPR 				= null;
		//차량이송재료 DAO
		YdCarFtmvMtlDao ydCarFtmvMtlDao 	= new YdCarFtmvMtlDao();
		//진행관리 - 이송지시
		PtStlFrtoMoveDao ptStlFrtoMoveDao 	= new PtStlFrtoMoveDao();
		//진행관리 - 주편공통DAO
		PtMSlabCommDao ptMSlabCommDao 		= new PtMSlabCommDao();
		//진행관리 - 슬라브공통DAO
		PtSlabCommDao ptSlabCommDao 		= new PtSlabCommDao();
		//권하 20090616.김진욱
		CraneUdHdSeEJBBean craneUdHdSeEJBBean = new CraneUdHdSeEJBBean();
		//재료번호
		String szSTL_NO 					= null;
		//재료품목 20090616.김진욱
		String szYD_MTL_ITEM 				= null;
		//이전저장위치 20090616.김진욱
		String szYD_STR_LOC 				= null;
		String szPT_TB_COMM 				= null;
		//야드구분
		String szYD_GP						= null;
		//슬라브지시행선
		String szSLAB_WO_RT_CD				= null;
		//재열재구분
		String szREHEAT_SLAB_GP				= null;
		//발지개소코드
		String szSPOS_WLOC_CD				= null;

		try {
			//--------------------------------------------------------------------------------------------------------------
			//	차량이송재료 조회
			//--------------------------------------------------------------------------------------------------------------

			szMsg="[" + szOperationName + "] 메소드 시작 - 차량스케줄[" + szYD_CAR_SCH_ID + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			// 1. 차량 이송재료를 조회

			szMsg="[" + szOperationName + "] 차량스케줄["+szYD_CAR_SCH_ID+"]로 차량이송재료 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			intRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recInTemp, rsResult, 4);   //*************************쿼리수정(회송테이블 join)
			if(intRtnVal <= 0) {
				szMsg="[" + szOperationName + "] 차량스케줄에 이송재료 가 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal;
			}

			szMsg="[" + szOperationName + "] 차량스케줄["+szYD_CAR_SCH_ID+"]로 차량이송재료 조회 완료 - 대상재건수["+intRtnVal+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			//--------------------------------------------------------------------------------------------------------------


			//--------------------------------------------------------------------------------------------------------------
			//	공통테이블 조회 후 공통테이블 업데이트, 이송지시테이블 업데이트,
			//	C연주/A후판슬라브야드인 경우에 생산통제로 이송하차실적 전송
			//--------------------------------------------------------------------------------------------------------------
			recSendPR		= JDTORecordFactory.getInstance().create();
			recSend 		= JDTORecordFactory.getInstance().create();
			recInTemp 		= JDTORecordFactory.getInstance().create();
			for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				rsResult.absolute(Loop_i);
				JDTORecord recOutTemp1 = rsResult.getRecord();
				szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(recOutTemp1, "YD_MTL_ITEM");
				szSTL_NO = ydDaoUtils.paraRecChkNull(recOutTemp1, "STL_NO");

				//--------------------------------------------------------------------------------------------------------------
				//	공통테이블 조회 - 주편/슬라브 공통테이블 조회
				//--------------------------------------------------------------------------------------------------------------

				szMsg="[" + szOperationName + "] 재료번호["+szSTL_NO+"]로 주편/슬라브공통테이블 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				getRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				szRtnMsg = YdCommonUtils.getPtCommStock(szSTL_NO, getRecSet);

				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					szMsg="[" + szOperationName + "] 진행관리의 공통테이블에 재료번호[" + szSTL_NO + "] 조회 실패 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					continue;
				}

				szMsg="[" + szOperationName + "] 재료번호["+szSTL_NO+"]로 주편/슬라브공통테이블 조회 완료 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				getRecSet.first();
				recTemp = getRecSet.getRecord();

				//--------------------------------------------------------------------------------------------------------------


				//--------------------------------------------------------------------------------------------------------------
				//	주편/슬라브공통테이블에 소재인수일시 업데이트
				//--------------------------------------------------------------------------------------------------------------

				szYD_STR_LOC = ydDaoUtils.paraRecChkNull(recTemp, "YD_STR_LOC");
				//주편공통테이블과 슬라브공통테이블중 어느 테이블을 조회했는 지를 결정
				szPT_TB_COMM = ydDaoUtils.paraRecChkNull(recTemp, "PT_TB_COMM");

				intRtnVal = 0;
				if( szPT_TB_COMM.equals("B") )	{				//주편공통테이블 업데이트 - 소재인수일시
					szMsg="[" + szOperationName + "] 주편공통테이블에 재료번호["+szSTL_NO+"]로 소재인수일시 업데이트 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					recInTemp.setField("MSLAB_NO", szSTL_NO);
					intRtnVal = ptMSlabCommDao.updPtMSlabComm(recInTemp, 1);
				}else if( szPT_TB_COMM.equals("S") )	{		//슬라브공통테이블업데이트 - 소재인수일시

					szMsg="[" + szOperationName + "] 슬라브공통테이블에 재료번호["+szSTL_NO+"]로 소재인수일시 업데이트 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					recInTemp.setField("SLAB_NO", szSTL_NO);
					intRtnVal = ptSlabCommDao.updPtSlabComm(recInTemp, 1);
				}

				if(intRtnVal <= 0) {
					szMsg="[" + szOperationName + "] 진행관리의 " +(szPT_TB_COMM.equals("B") ? "주편" : (szPT_TB_COMM.equals("S") ? "슬라브" : "") )
					+ "공통테이블에 재료번호[" + szSTL_NO + "(" + szYD_MTL_ITEM + ")] 하차완료시점[소재인수일시] 업데이트 실패 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					//return intRtnVal;
					//continue;
				}

				szMsg="[" + szOperationName + "] 주편/슬라브공통테이블에 재료번호["+szSTL_NO+"]로 소재인수일시 업데이트 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				//--------------------------------------------------------------------------------------------------------------


				//--------------------------------------------------------------------------------------------------------------
				//	이송지시테이블 조회
				//--------------------------------------------------------------------------------------------------------------

				// 2. PT_소재이송지시에 이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드를 업데이트 처리
				recInTemp.setField("STL_NO", szSTL_NO);

				szMsg="[" + szOperationName + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 재료번호["+szSTL_NO+"]로 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				rsResultTemp = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal = ptStlFrtoMoveDao.getPtStlFrtoMove(recInTemp, rsResultTemp, 0);
				if( intRtnVal <= 0 ) {
					szMsg="[" + szOperationName + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 재료[" + szSTL_NO + "]가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					//return intRtnVal;
				}else{

					szMsg="[" + szOperationName + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 재료번호["+szSTL_NO+"]로 조회 완료 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					rsResultTemp.first();
					recOutTemp = rsResultTemp.getRecord();

					//--------------------------------------------------------------------------------------------------------------


					//--------------------------------------------------------------------------------------------------------------
					//	이송지시테이블 업데이트
					//--------------------------------------------------------------------------------------------------------------

					//야드재료예정저장To위치코드 20090616.김진욱
					recOutTemp.setField("YD_MTL_PLN_STR_TO_LOC_CD", szYD_STR_LOC);
					//20090618.김진욱 이송상태코드(이송완료)
					recOutTemp.setField("FRTOMOVE_STAT_CD", "*");
					//20210506 입고자 체크를 위한 수정자 id 지정
					recOutTemp.setField("MODIFIER", szMODIFIER);
					 
					
					this.updPtStlFrtoMoveTC(szSTL_NO , recOutTemp);
					
//					intRtnVal = ptStlFrtoMoveDao.updPtStlFrtoMove(recOutTemp, 1);
//					if( intRtnVal <= 0 ) {
//						szMsg="[" + szOperationName + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에  이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드 업데이트 실패";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//						//return intRtnVal;
//					}
					szMsg="[" + szOperationName + "]  진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]- 재료번호["
						+ ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO") + "], 이송지시차수["
						+ ydDaoUtils.paraRecChkNull(recOutTemp, "TRANSWORD_SEQNO") + "]에  이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드 업데이트 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					//--------------------------------------------------------------------------------------------------------------

				}

				//--------------------------------------------------------------------------------------------------------------
				//	진도코드 갱신  업데이트
				//--------------------------------------------------------------------------------------------------------------
				recInTemp 		= JDTORecordFactory.getInstance().create();
				recInTemp.setField("STL_NO", szSTL_NO);
				intRtnVal = craneUdHdSeEJBBean.Y0SetProgCode(recInTemp) ;

				//--------------------------------------------------------------------------------------------------------------
			}

		}catch(JDTOException e) {
			szMsg = "[" + szOperationName + "] 공통업무 처리시 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szSessionName + " : " + szMethodName + e.getMessage(), e);
		}
		return intRtnVal;
	}
	/**
	 *  통합야드 차량작업관리 배차내역  조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabTotYdCarStlSch(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		String szMsg        		= "";
		//String szTemp       		= "";
		String szWLOC_CD       		= "";
		String szMethodName	 		= "getSlabTotYdCarStlSch";
		String szOperationName		= "통합야드배차내역상세";
		String szYD_CAR_STOP_LOC = "";

		YdCarSchDao ydCarSchDao = new YdCarSchDao();

		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			szWLOC_CD = yddatautil.setDataDefault(inDto.getField("WLOC_CD2"), "");

			if(szWLOC_CD.equals("") || szWLOC_CD.length() != 5 ){
				return outRecSet;
			}else if(szWLOC_CD.length() == 5){

				szYD_CAR_STOP_LOC = yddatautil.setDataDefault(inDto.getField("CAR_POINT"), "");
				if( szYD_CAR_STOP_LOC.equals("") ) {
					szYD_CAR_STOP_LOC = yddatautil.setDataDefault(inDto.getField("BAY2"), "");
					if( szYD_CAR_STOP_LOC.length() < 2 ) {
						szYD_CAR_STOP_LOC = "";
					}
				}
				recPara.setField("WLOC_CD", szWLOC_CD);
				recPara.setField("YD_CAR_STOP_LOC", szYD_CAR_STOP_LOC);

				//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD", yddatautil.setDataDefault(inDto.getField("PI_YD"), "*"));
				recPara.setField("PI_YD1", yddatautil.setDataDefault(inDto.getField("PI_YD1"), "*"));
				
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarAsgnInfoNPrepSchByWlocCdStl_PIDEV */
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 320);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "["+szOperationName+"] 오류발생[1] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if (intRtnVal == -2) {
						szMsg = "["+szOperationName+"] 오류발생[2] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						szMsg = "["+szOperationName+"] 오류발생[3] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					//return outRecSet;
					return outRecSet;
				}else if(intRtnVal == 0) {
					szMsg = "["+szOperationName+"] 대상재가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}// end of if
			}


			szMsg = "["+szOperationName+"] 대상재가 존재합니다. - 건수 : " + outRecSet.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getSlabTotYdCarStlSch

	/**
	 * 지연내용등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord[]
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord updSlabYdDelyRetMgt(JDTORecord[] inDto) throws DAOException {
		int intRtnVal 		= 0;
		String szMsg		= null;
		String szMethodName = null;
 		YdStockDao ydStockDao = new YdStockDao();
		JDTORecord recPara  = JDTORecordFactory.getInstance().create();
 		JDTORecord retRrd   = JDTORecordFactory.getInstance().create();

 		szMsg        					= "";
 		szMethodName 					= "updSlabYdDelyRetMgt";
		String szSTL_NO 				= "";
		String szSTEP_NO 				= "";
		String szSCARF_DELY_REGISTER 	= "";
		String szSCARF_DELY_REG_CNTS	= "";

		try {


			szMsg = "JSP-SESSION [지연내용등록] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){

				szSTL_NO 				= yddatautil.setDataDefault(inDto[x].getField("STL_NO"),"");
				szSTEP_NO			 	= yddatautil.setDataDefault(inDto[x].getField("STEP_NO"),"");
  				szSCARF_DELY_REGISTER 	= yddatautil.setDataDefault(inDto[x].getField("SCARF_DELY_REGISTER"),"");
				szSCARF_DELY_REG_CNTS	= yddatautil.setDataDefault(inDto[x].getField("SCARF_DELY_REG_CNTS"),"");


				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO"      			, szSTL_NO);
				recPara.setField("STEP_NO"      		, szSTEP_NO);
				recPara.setField("SCARF_DELY_REGISTER"  , szSCARF_DELY_REGISTER);
				recPara.setField("SCARF_DELY_REG_CNTS" 	, szSCARF_DELY_REG_CNTS);

				/**com.inisteel.cim.yd.jsp.slabjsp.dao.slabJspDao.updYd_SlabScarfDelyReg*/
				intRtnVal = ydStockDao.updYd_SlabScarfDelyReg(recPara);
				if (intRtnVal <= 0) {
					szMsg = "지연내용등록 시 ERROR 발생 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					retRrd.setField("RTN_CD" 	, "0");
					retRrd.setField("RTN_MSG" 	, szMsg);
					return retRrd;
				}
			}

			retRrd.setField("RTN_CD", "1");
			retRrd.setField("RTN_MSG", "정상 처리 되었습니다.");



		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [지연내용등록] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return retRrd;

	}	// end of updSlabYdDelyRetMgt
	
	
	/**
	 * 이송실적 처리 및 정정마감tc 전송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String updPtStlFrtoMoveTC(String szSTL_NO , JDTORecord recOutTemp) throws DAOException {
		String szMsg		= null;
		String szMethodName = null;
		String retRrd="N";
		int intRtnVal =0;
		PtStlFrtoMoveDao ptStlFrtoMoveDao 	= new PtStlFrtoMoveDao();
		try {			
			
			intRtnVal = ptStlFrtoMoveDao.updPtStlFrtoMove(recOutTemp, 1);
			if( intRtnVal <= 0 ) {
				szMsg="[updPtStlFrtoMoveTC] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에  이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드 업데이트 실패";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return retRrd;
			}
			
			
			//주편정정마감EVENT (YMCSJ001) 2012.01.05
    		EJBConnector ejbConn2 = new EJBConnector("default","CraneUdHdSeEJB",this);
			ejbConn2.trx("procYMCSJ001",new Class[]{String.class}, new Object[]{szSTL_NO});
			
			
			retRrd="Y";
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [updPtStlFrtoMoveTC] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return retRrd;

	}	// end of updSlabYdDelyRetMgt
	
	
	/**
	 *  이송예상일조회 List
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabTotYdToMoveList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		String      szMsg        = "";
		String      szMethodName = "getSlabTotYdToMoveList";
		String 	szOperationName = "이송예상일조회 List";
		YdStkBedDao YdStkBedDap  = new YdStkBedDao();
		String szYdDongGp        = "";
		String szYdBedGp         = "";

		int intRtnVal = 0;

		try {


			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
 

			recPara.setField("PAGE_CNT",            inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT",             inDto.getField("ROWCOUNT"));
			recPara.setField("ARR_YD_GP",        	inDto.getField("ARR_YD_GP"));
			recPara.setField("SLAB_WO_RT_CD",       inDto.getField("SLAB_WO_RT_CD"));
			recPara.setField("STL_APPEAR_GP",       inDto.getField("STL_APPEAR_GP"));
			recPara.setField("ORD_YEOJAE_GP",       inDto.getField("ORD_YEOJAE_GP"));
			recPara.setField("FROMTOMOVE_PLN_DATE", inDto.getField("FROMTOMOVE_PLN_DATE"));
 
 
			/*ym.steelinfo.steelinforecv.dao.YdStockDAO.slabTotYdToMoveList*/
			intRtnVal = YdStkBedDap.getYdStkbed(recPara, outRecSet, 317);
			if (intRtnVal < 0) {

				szMsg = " 이송예상일조회 List DAO 조회 !!!, ErrorCode:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				return outRecSet;
			} // end of if


			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getSlabTotYdToMoveList
	
	
	/**
	 *  슬라브이송지연사유등록 List
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSlabTotYdToMoveMgt(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");

		String      szMsg        = "";
		String      szMethodName = "getSlabTotYdToMoveMgt";
		String 	szOperationName = "슬라브이송지연사유등록 List";
		YdStkBedDao YdStkBedDap  = new YdStkBedDao();
		String szYdDongGp        = "";
		String szYdBedGp         = "";

		int intRtnVal = 0;

		try {


			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
 

			recPara.setField("PAGE_CNT",            inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT",             inDto.getField("PAGE_SIZE"));
			recPara.setField("ARR_YD_GP",        	inDto.getField("ARR_YD_GP"));
			recPara.setField("SLAB_WO_RT_CD",       inDto.getField("SLAB_WO_RT_CD"));
			recPara.setField("STL_APPEAR_GP",       inDto.getField("STL_APPEAR_GP"));
			recPara.setField("ORD_YEOJAE_GP",       inDto.getField("ORD_YEOJAE_GP"));
			recPara.setField("FRTOMOVE_WORD_FROM", 	inDto.getField("DATE_FROM"));
			recPara.setField("FRTOMOVE_WORD_TO", 	inDto.getField("DATE_TO"));
			
			recPara.setField("YD_BAY_GP", 			inDto.getField("YD_DONG_GP"));
			recPara.setField("YD_EQP_GP", 			inDto.getField("YD_SPAN_GP"));
			recPara.setField("YD_STK_COL_NO", 		inDto.getField("YD_COL_GP"));
			recPara.setField("YD_DELAY_GP", 		inDto.getField("YD_DELAY_GP"));
 
 
			/*ym.steelinfo.steelinforecv.dao.YdStockDAO.slabTotYdToMoveMgt*/
			intRtnVal = YdStkBedDap.getYdStkbed(recPara, outRecSet, 318);
			if (intRtnVal < 0) {

				szMsg = " 슬라브이송지연사유등록 List DAO 조회 !!!, ErrorCode:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				return outRecSet;
			} // end of if


			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getSlabTotYdToMoveMgt
	
	/**
	 * [A] 오퍼레이션명: 벤딩처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public GridData updStockBendReg(GridData inParam) throws DAOException {
		GridData outGrid = null;
		String szMethodName = "updStockBendReg";
		String szLogMsg = "";
		String szOperationName = "벤딩처리(Se)";

		YdStockDao dao = new YdStockDao();
		int result = 0;
		boolean spRet = false;

		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			outGrid = dao.updStockBendReg(inParam);
			
			//bend 이력 등록 관리 추가 (chito 2016.09.01)
			outGrid = dao.inStockBendReg(inParam);
			
			
			return outGrid;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + " :: " + e.getMessage(), e);
		}
	}
	
	
	
	/**
	 * [A] 오퍼레이션명: 마킹처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public GridData updStockMarkReg(GridData inParam) throws DAOException {
		GridData outGrid = null;
		String szMethodName = "updStockMarkReg";
		String szLogMsg = "";
		String szOperationName = "마킹처리(Se)";

		YdStockDao dao = new YdStockDao();
		int result = 0;
		boolean spRet = false;

		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			outGrid = dao.updStockMarkReg(inParam);
			
			return outGrid;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + " :: " + e.getMessage(), e);
		}
	}
	
	
	
	/**
	 * [A] 오퍼레이션명: Q재등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public GridData updStockQslabReg(GridData inParam) throws DAOException {
		GridData outGrid = null;
		String szMethodName = "updStockQslabReg";
		String szLogMsg = "";
		String szOperationName = "Q재등록(Se)";

		YdStockDao dao = new YdStockDao();
		int result = 0;
		boolean spRet = false;

		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			outGrid = dao.updStockQslabReg(inParam);
			
			return outGrid;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + " :: " + e.getMessage(), e);
		}
	}
	
	
	
	/**
	 *  마킹완료 실적조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdSlabmarkingHist(JDTORecord inDto) throws DAOException {

		// Log Message
		String szMsg        = "";
		String szMethodName = "getYdSlabmarkingHist";

		String szYdGp = "";

		// ERROR CHECK
		int intRtnVal = 0;

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		try {

			szMsg = "JSP-SESSION [마킹완료 실적조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			szYdGp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			String szYD_DONG_GP =ydDaoUtils.paraRecChkNull(inDto, "YD_DONG_GP"); 
			String szYD_SPAN_GP =ydDaoUtils.paraRecChkNull(inDto, "YD_SPAN_GP"); 
			String szYD_COL_GP  =ydDaoUtils.paraRecChkNull(inDto, "YD_COL_GP"); 
			String szYD_BED_GP  =ydDaoUtils.paraRecChkNull(inDto, "YD_BED_GP"); 

			recPara.setField("DATE_FROM",         inDto.getField("DATE_FROM"));
			recPara.setField("DATE_TO",           inDto.getField("DATE_TO"));			 
			recPara.setField("YD_WRK_DUTY",       inDto.getField("YD_WRK_DUTY"));
			recPara.setField("DEMANDER_CD",       inDto.getField("DEMANDER_CD"));
			recPara.setField("YD_AIM_RT_GP",      inDto.getField("YD_AIM_RT_GP"));
			recPara.setField("YD_UP_WR_LOC",      szYD_DONG_GP+szYD_SPAN_GP+szYD_COL_GP+szYD_BED_GP);
			recPara.setField("PAGE_CNT",          inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT",           inDto.getField("ROWCOUNT"));

			//com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdSlabmarkingHist_PIDEV
			intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 304);

			if (intRtnVal < 0) {
				szMsg = "ydSchruleDao  조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;

			 } // end of if
 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [마킹완료 실적조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}//end of getYdSlabmarkingHist
	

	/**
	 * [A] 오퍼레이션명: 마킹관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public GridData updStockMarkingReg(GridData inParam) throws DAOException {
		GridData outGrid = null;
		String szMethodName = "updStockMarkingReg";
		String szLogMsg = "";
		String szOperationName = "마킹관리";

		YdStockDao dao = new YdStockDao();
		int result = 0;
		boolean spRet = false;

		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG); 
			
			//마킹관리 이력 등록 관리 추가 (chito 2016.09.01)
			outGrid = dao.inStockMarkingReg(inParam);
			
			
			return outGrid;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + " :: " + e.getMessage(), e);
		}
	}
	
	
	/**
	 * [A] 오퍼레이션명: 통합야드 업무일지 근무자정보 및 작업내용 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecordSet getSlabYdDailyWorkInfo(JDTORecord inDto) throws DAOException {
		// Log Message
		String szMsg        = "";
		String szMethodName = "getSlabYdDailyWorkInfo";

		String szYdGp = "";

		// ERROR CHECK
		int intRtnVal = 0;

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		try {

			szMsg = "JSP-SESSION [통합야드 업무일지 근무자정보 및 작업내용 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			szYdGp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			recPara.setField("WORK_DATE",       inDto.getField("V_WORK_DATE"));
			recPara.setField("YD_GP",           inDto.getField("V_YD_GP"));			

			//com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyWork
			intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 305);

			if (intRtnVal < 0) {
				szMsg = "ydSchruleDao  조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;

			 } // end of if
 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [통합야드 업무일지 근무자정보 및 작업내용 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	} // end of getSlabYdDailyWorkInfo
	
	
	/**
	 * [A] 오퍼레이션명: 통합야드 업무일지 근무자정보 및 작업내용 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public GridData updSlabYdDailyWorkInfo(GridData inParam) throws DAOException {
		GridData outGrid = null;
		String szMethodName = "updSlabYdDailyWorkInfo";
		String szLogMsg = "";
		String szOperationName = "통합야드 업무일지 근무자정보 및 작업내용 등록";

		//YdStockDao dao = new YdStockDao();
		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();
		
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG); 
			
			outGrid = ydWrkHistDao.updSlabYdDailyWorkInfo(inParam);
			
			return outGrid;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + " :: " + e.getMessage(), e);
		}
	}
	
	
	/**
	 * [A] 오퍼레이션명: 통합야드 업무일지 재고현황 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecordSet getSlabYdDailyCntByBay(JDTORecord inDto) throws DAOException {
		// Log Message
		String szMsg        = "";
		String szMethodName = "getSlabYdDailyCntByBay";

		String szYdGp = "";

		// ERROR CHECK
		int intRtnVal = 0;

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		try {

			szMsg = "JSP-SESSION [통합야드 업무일지 재고현황 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("DATE",       inDto.getField("V_WORK_DATE"));

			//com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyCntByBay
			intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 306);

			if (intRtnVal < 0) {
				szMsg = "ydSchruleDao  조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;

			 } // end of if
 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [통합야드 업무일지 재고현황 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}
	
	
	/**
	 * [A] 오퍼레이션명: 통합야드 업무일지_행선판매재고별현황
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecordSet getSlabYdDailyCntByRt(JDTORecord inDto) throws DAOException {
		// Log Message
		String szMsg        = "";
		String szMethodName = "getSlabYdDailyCntByRt";

		String szYdGp = "";

		// ERROR CHECK
		int intRtnVal = 0;

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		try {

			szMsg = "JSP-SESSION [통합야드 업무일지_행선판매재고별현황] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("DATE",       inDto.getField("V_WORK_DATE"));

			//com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyCntByRt
			intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 307);

			if (intRtnVal < 0) {
				szMsg = "ydSchruleDao  조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;

			 } // end of if
 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [통합야드 업무일지_행선판매재고별현황] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}
	/**
	 * [A] 오퍼레이션명: 통합야드 업무일지_행선판매재고별현황 이송대기량 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecordSet getSlabYdDailyCntByRtWait(JDTORecord inDto) throws DAOException {
		// Log Message
		String szMsg        = "";
		String szMethodName = "getSlabYdDailyCntByRtWait";

		String szYdGp = "";

		// ERROR CHECK
		int intRtnVal = 0;

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		try {

			szMsg = "JSP-SESSION [통합야드 업무일지_행선판매재고별현황 이송대기량조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("DATE",       inDto.getField("V_WORK_DATE"));

			//com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyCntByRt
			intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 312);

			if (intRtnVal < 0) {
				szMsg = "ydSchruleDao  조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;

			 } // end of if
 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [통합야드 업무일지_행선판매재고별현황 이송대기량조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}
	/**
	 * [A] 오퍼레이션명: 통합야드 업무일지_행선판매재고별현황 과거 이송 대기량 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecordSet getSlabYdDailyCntByRtWaitPast(JDTORecord inDto) throws DAOException {
		// Log Message
		String szMsg        = "";
		String szMethodName = "getSlabYdDailyCntByRtWaitPast";

		String szYdGp = "";

		// ERROR CHECK
		int intRtnVal = 0;

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		try {

			szMsg = "JSP-SESSION [통합야드 업무일지_행선판매재고별현황 과거 이송대기량조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("DATE",       inDto.getField("V_WORK_DATE"));

			//com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyCntByRt
			intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 313);

			if (intRtnVal < 0) {
				szMsg = "ydSchruleDao  조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;

			 } // end of if
 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [통합야드 업무일지_행선판매재고별현황 과거 이송대기량조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}
	/**
	 * [A] 오퍼레이션명: 통합야드 업무일지 장비별 작업현황 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecordSet getSlabYdDailyCntByEQPID(JDTORecord inDto) throws DAOException {
		// Log Message
		String szMsg        = "";
		String szMethodName = "getSlabYdDailyCntByEQPID";

		String szYdGp = "";

		// ERROR CHECK
		int intRtnVal = 0;

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		try {

			szMsg = "JSP-SESSION [통합야드 업무일지 장비별 작업현황 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("DATE",       inDto.getField("V_WORK_DATE"));

			//com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyCntByBay
			intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 309);

			if (intRtnVal < 0) {
				szMsg = "ydSchruleDao  조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;

			 } // end of if
 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [통합야드 업무일지 장비별 작업현황 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}
	/**
	 * [A] 오퍼레이션명: 통합야드 업무일지 장비별 작업현황 비고 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecordSet getSlabYdDailyEQPNote(JDTORecord inDto) throws DAOException {
		// Log Message
		String szMsg        = "";
		String szMethodName = "getSlabYdDailyEQPNote";

		String szYdGp = "";

		// ERROR CHECK
		int intRtnVal = 0;

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		try {

			szMsg = "JSP-SESSION [통합야드 업무일지 장비별 작업현황 비고 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("DATE",       inDto.getField("V_WORK_DATE"));
			szMsg = inDto.getField("V_WORK_DATE").toString()+"비고조회날짜";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyCntByBay
			intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 311);

			if (intRtnVal < 0) {
				szMsg = "ydSchruleDao  조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;

			 } // end of if
 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [통합야드 업무일지 장비별 작업현황 비고 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}
	/**
	 * [A] 오퍼레이션명: 통합야드 업무일지 외부판매 재고현황 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecordSet getSlabYdDailySalStk(JDTORecord inDto) throws DAOException {
		// Log Message
		String szMsg        = "";
		String szMethodName = "getSlabYdDailySalStk";

		String szYdGp = "";

		// ERROR CHECK
		int intRtnVal = 0;

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		try {

			szMsg = "JSP-SESSION [통합야드 업무일지 외부판매 재고현황 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			szYdGp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			recPara.setField("WORK_DATE",       inDto.getField("V_WORK_DATE"));
			recPara.setField("YD_GP",           inDto.getField("V_YD_GP"));			

			//com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailySalStk
			intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 308);

			if (intRtnVal < 0) {
				szMsg = "ydSchruleDao  조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;

			 } // end of if
 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [통합야드 업무일지 외부판매 재고현황 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	} // end of getSlabYdDailyWorkInfo
	
	
	
	/**
	 * [A] 오퍼레이션명: 통합야드 업무일지 외부판매 재고현황 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public GridData updSlabYdDailySalStk(GridData inParam) throws DAOException {
		GridData outGrid = null;
		String szMethodName = "updSlabYdDailySalStk";
		String szLogMsg = "";
		String szOperationName = "통합야드 업무일지 외부판매 재고현황 등록";

		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();
		
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG); 
			
			outGrid = ydWrkHistDao.updSlabYdDailySalStk(inParam);
			
			return outGrid;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + " :: " + e.getMessage(), e);
		}
	} // end of updSlabYdDailySalStk
	
	/**
	 * [A] 오퍼레이션명: 통합야드 업무일지 판매재 적치 현황 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public GridData updSlabYdDailySaleStk(GridData inParam) throws DAOException {
		GridData outGrid = null;
		String szMethodName = "updSlabYdDailySaleStk";
		String szLogMsg = "";
		String szOperationName = "통합야드 업무일지 판매재 적치 현황 등록";

		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG); 
			
			int result = 0;
			//기존데이터 처리
			result = ydWrkHistDao.updSlabYdDailySaleStk(inParam);
			
			//새로운 데이터 insert
			recPara.setField("WORK_DATE", inParam.getParam("WORK_DATE"));
			recPara.setField("YD_GP", inParam.getParam("YD_GP"));
			recPara.setField("userid", inParam.getParam("userid"));
			
			for(int i=1; i<=8; i++){
				String DmCust = inParam.getParam("DmCust"+Integer.toString(i));
				String DmSpec = inParam.getParam("DmSpec"+Integer.toString(i));
				String DmCnt = inParam.getParam("DmCnt"+Integer.toString(i));
				String DmWt = inParam.getParam("DmWt"+Integer.toString(i));
				String DmBay = inParam.getParam("DmBay"+Integer.toString(i)); 
				
				szLogMsg = "판매재 적치 현황 내수 입력 데이터: 고객: "+ DmCust + ",규격약호: "+DmSpec + "갯수: "+DmCnt+", 중량: "+DmWt+"동: "+DmBay;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG); 
				
				if("".equals(DmCust)) continue;
				
				recPara.setField("DEMANDER", DmCust);
				recPara.setField("SPEC_ABBSYM", DmSpec);
				recPara.setField("CNT", DmCnt);
				recPara.setField("WT", DmWt);
				recPara.setField("YD_BAY_GP", DmBay);
				recPara.setField("SALE_GP", "D");
				
				result =  ydWrkHistDao.insSlabYdDailySaleStk(recPara);
			}
			
			for(int i=1; i<=8; i++){
				String DmCust = inParam.getParam("OsCust"+Integer.toString(i));
				String DmSpec = inParam.getParam("OsSpec"+Integer.toString(i));
				String DmCnt = inParam.getParam("OsCnt"+Integer.toString(i));
				String DmWt = inParam.getParam("OsWt"+Integer.toString(i));
				String DmBay = inParam.getParam("OsBay"+Integer.toString(i)); 
				
				szLogMsg = "판매재 적치 현황 외부판매 입력 데이터: 고객: "+ DmCust + ",규격약호: "+DmSpec + "갯수: "+DmCnt+", 중량: "+DmWt+"동: "+DmBay;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG); 
				
				if("".equals(DmCust)) continue;
				
				recPara.setField("DEMANDER", DmCust);
				recPara.setField("SPEC_ABBSYM", DmSpec);
				recPara.setField("CNT", DmCnt);
				recPara.setField("WT", DmWt);
				recPara.setField("YD_BAY_GP", DmBay);
				recPara.setField("SALE_GP", "O");
				
				result =  ydWrkHistDao.insSlabYdDailySaleStk(recPara);
			}
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG); 
			return inParam;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + " :: " + e.getMessage(), e);
		}
	} // end of updSlabYdDailySalStk
	
	/**
	 * [A] 오퍼레이션명: 통합야드 업무일지 승인자 가져오기
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecordSet getSlabYdDailyAcceptor(JDTORecord inDto) throws DAOException {
		// Log Message
		String szMsg        = "";
		String szMethodName = "getSlabYdDailyAcceptor";

		String szYdGp = "";

		// ERROR CHECK
		int intRtnVal = 0;

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		try {

			szMsg = "JSP-SESSION [통합야드 업무일지 승인자 가져오기] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			//com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyCntByBay
			intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 310);

			if (intRtnVal < 0) {
				szMsg = "ydSchruleDao  조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;

			 } // end of if
 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION [통합야드 업무일지 승인자 가져오기] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}
	
	/**
	 *      [A] 오퍼레이션명 : 구내운송 회송처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord runTsRetHt(GridData gdReq) throws DAOException {
		String szMethodName = "runTsRetHt";
		String szLogMsg = "";
		String szOperationName = "구내운송 회송처리";
		String logId = gdReq.getIPAddress();
		
		YdDelegate ydDelegate           = new YdDelegate();

		try {
			szLogMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//Return Value
			JDTORecord jrRtn = null;
			String currDate   = slabUtils.getDateTime14();	//현재시각
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, szMethodName, slabUtils.trim(gdReq.getParam("userid")));
			
			slabUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 유저아이디 : " + gdReq.getParam("userid"), "");

			
			
			String sRetHt_ID = "";
			
			//회송ID생성
			sRetHt_ID = slabYdCommDao.getSeqId(logId, szMethodName, "RetHt");

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			int iHsCnt = 0;
			String returnMsg = null;
			slabUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ rowCnt : " + rowCnt, "");
			
			/**********************************************************
			* 1. 회송 대상재 이송하차 작업예약 삭제처리
			**********************************************************/
			slabUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 회송 대상재 이송하차 작업예약 삭제처리 시작", "");
			for (int ii = 0; ii < rowCnt; ii++) {
				
				if("HS".equals(gdReq.getHeader("CMPL_GP").getComboHiddenValues()[gdReq.getHeader("CMPL_GP").getSelectedIndex(ii)])) { //회송인 경우만 처리..
			
					jrParam.setField("YD_WBOOK_ID", gdReq.getHeader("YD_WBOOK_ID").getValue(ii));
					jrParam.setField("YD_USER_ID", gdReq.getParam("userid"));
					returnMsg = this.delYdWrkbook(jrParam);
					//크레인스케쥴이 있는 작업재료가 있는경우 회송처리 불가.
					if(YdConstant.RETN_CD_FAILURE.equals(returnMsg)){
						slabUtils.printLog(logId, gdReq.getHeader("STL_NO").getValue(ii) +" 재료는 회송불가(크레인작업예약)", "");
						JDTORecord rtnMsg = JDTORecordFactory.getInstance().create();
						rtnMsg.setField("rtnMSG", "Error: "+ gdReq.getHeader("STL_NO").getValue(ii) +" 재료는 크레인작업예약이 있어 회송이 불가합니다. 작업예약을 삭제해주세요.");
						rtnMsg.setField("isSuccess", "fail");
						return rtnMsg;
					}
					iHsCnt++;
				}
			}
			
			if(iHsCnt == 0) {
				throw new Exception("회송 대상재가 없습니다! 회송처리 비정상 종료");
			}
			slabUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 회송 대상재 이송하차 작업예약 삭제처리 종료 ", "");
			
			
			
			/**********************************************************
			* 2. 기존 차량 스케줄 종료 처리
			**********************************************************/
			slabUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 기존 차량 스케줄 종료 처리 시작 ", "");
			
			jrParam.setField("YD_CAR_SCH_ID", gdReq.getHeader("YD_CAR_SCH_ID").getValue(0)); //기존 차량스케줄ID
			slabYdCommDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.delCarschID", logId, szMethodName, "기존 차량스케줄정보삭제");
			slabYdCommDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarSchMtl", logId, szMethodName, "기존 차량스케줄재료 삭제");
			
			slabUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 기존 차량 스케줄 종료 처리 종료 ", "");
			
			//2-1 기존 차량스케줄의 소재 이송지시에 상차날짜 초기화
			jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
			slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updStlFrToMoveRetHt", logId, szMethodName, "기존 차량스케줄정보삭제");
			
			/**********************************************************
			* 3. 신규 회송 차량 스케줄 생성
			**********************************************************/
			slabUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 신규 회송 차량 스케줄 생성 시작 ", "");
			
			String sYdCarSchId = "";
			
			//차량스케줄ID
			sYdCarSchId = slabYdCommDao.getSeqId(logId, szMethodName, "CarSch");
			
			jrParam.setField("YD_CAR_SCH_ID"		, sYdCarSchId);
			jrParam.setField("OLD_YD_CAR_SCH_ID"	, gdReq.getHeader("YD_CAR_SCH_ID").getValue(0)); //기존 차량스케줄ID
			jrParam.setField("REGISTER"				, "runTsRetHt"); //Pallet조회(B)에서 회송 표시를 하기위해 REGISTER 에 "runTsRetHt"를 입력한다.
			
			slabYdCommDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insRetHtCarSch", logId, szMethodName, "회송 차량스케줄 INSERT");
			
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				if("HS".equals(gdReq.getHeader("CMPL_GP").getComboHiddenValues()[gdReq.getHeader("CMPL_GP").getSelectedIndex(ii)])) { //회송인 경우만 처리..
					
					jrParam.setField("YD_CAR_SCH_ID"		, sYdCarSchId);
					jrParam.setField("STL_NO"				, gdReq.getHeader("STL_NO").getValue(ii)); 
					jrParam.setField("DEL_YN"				, "N");
					jrParam.setField("YD_STK_BED_NO"		, "01");
					jrParam.setField("YD_STK_LYR_NO"		, gdReq.getHeader("YD_STK_LYR_NO").getValue(ii));  //적치단
					
					slabYdCommDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarFtMvMtl", logId, szMethodName, "회송 차량스케줄 재료 INSERT");
				}
			}
			slabUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 신규 회송 차량 스케줄 생성 종료 ", "");
			
			
			/**********************************************************
			* 4. 회송이력 테이블 (TB_YD_RETHTHIST) 테이블 INSERT
			**********************************************************/
			slabUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 회송이력 테이블 (TB_YD_RETHTHIST) 테이블 INSERT 시작 ", "");
			for (int ii = 0; ii < rowCnt; ii++) {
				
				if("HS".equals(gdReq.getHeader("CMPL_GP").getComboHiddenValues()[gdReq.getHeader("CMPL_GP").getSelectedIndex(ii)])) { //회송인 경우만 처리..
					
					jrParam.setField("YD_RETHT_HIST_ID"		, sRetHt_ID);
					jrParam.setField("STL_NO"				, gdReq.getHeader("STL_NO").getValue(ii));
					jrParam.setField("YD_RETHT_EMPNO"		, gdReq.getParam("userid"));
					jrParam.setField("YD_RETHT_REQ_DT"		, currDate);
					jrParam.setField("YD_RETHT_RSN_CD"		, gdReq.getParam("RTNHT_RSN_CD"));
					jrParam.setField("YD_RETHT_RSN_CNTS"	, gdReq.getParam("RTNHT_RSN_MSG"));
					jrParam.setField("YD_RETHT_STAT_CD"		, "1");
					jrParam.setField("SPOS_WLOC_CD"			, gdReq.getHeader("SPOS_WLOC_CD").getValue(ii)); 
					jrParam.setField("ARR_WLOC_CD"			, gdReq.getHeader("ARR_WLOC_CD").getValue(ii)); 
					jrParam.setField("TRN_EQP_CD"			, gdReq.getHeader("TRN_EQP_CD").getValue(ii)); 
					jrParam.setField("YD_CAR_SCH_ID"		, sYdCarSchId /*gdReq.getHeader("YD_CAR_SCH_ID").getValue(ii)*/); 
					jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
					jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
					
					slabYdCommDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSalbDAO.insRetHtHist", logId, szMethodName, "회송이력 테이블 INSERT");
					
				}
			}
			
			slabUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 회송이력 테이블 (TB_YD_RETHTHIST) 테이블 INSERT 종료 ", "");
			
			
			/**********************************************************
			* 5. 구내운송 회송하차 완료실적 전문 편집
			**********************************************************/
			slabUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 구내운송 회송하차 완료실적 전문 편집 시작 ", "");
			
			JDTORecord jrYDTSJ016 = JDTORecordFactory.getInstance().create();
			jrYDTSJ016.setField("JMS_TC_CD", "YDTSJ016"); //야드작업예약ID
			jrYDTSJ016.setField("JMS_TC_CREATE_DDTT", currDate); //JMSTC생성일시	
			
			jrYDTSJ016.setField("TRN_EQP_CD", gdReq.getHeader("TRN_EQP_CD").getValue(0)); //운송장비코드
			jrYDTSJ016.setField("ARR_WLOC_CD", gdReq.getHeader("ARR_WLOC_CD").getValue(0)); //착지개소코드
			jrYDTSJ016.setField("ARR_YD_PNT_CD", gdReq.getHeader("YD_PNT_CD3").getValue(0)); //착지야드포인트코드
			jrYDTSJ016.setField("CARUD_CMPL_DT", currDate); //하차완료일시
			jrYDTSJ016.setField("CARLD_SH", gdReq.getHeader("CARLD_SH").getValue(0)); //상차매수
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				jrYDTSJ016.setField("STL_NO" + (ii+1), gdReq.getHeader("STL_NO").getValue(ii)); //재료번호n
				jrYDTSJ016.setField("RETHT_CARUD_CMPL_GP" + (ii+1), gdReq.getHeader("CMPL_GP").getComboHiddenValues()[gdReq.getHeader("CMPL_GP").getSelectedIndex(ii)]); //회송하차완료구분n
			}
			
			jrRtn = slabUtils.addSndData(jrRtn, jrYDTSJ016);
			//jrRtn = commUtils.addSndData(jrRtn, jrYDTSJ016);
			//ydDelegate.sendMsg(jrYDTSJ016);
			
			slabUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 구내운송 회송하차 완료실적 전문 편집 종료 ", "");
			
			slabUtils.printLog(logId, szMethodName, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));
		}	
	} // end of runTsRetHt
	/**
	 *      [A] 오퍼레이션명 : 후판슬라브 테스트 슬라브 등록 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public GridData makePlateYdTestSlab(GridData inDto) throws DAOException {
		String szMethodName = "makePlateYdTestSlab";
		String szLogMsg = "";
		String szOperationName = "후판슬라브 테스트 슬라브 등록";
		String logId = inDto.getIPAddress();
		String szRtnMsg 	= YdConstant.RETN_CD_SUCCESS;
		GridData gdRes = null;
		try {
			szLogMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			JDTORecord recTemp     = CmUtil.genJDTORecord(inDto);
			
			int rowCnt = inDto.getHeader("CHECK").getRowCount();
			JDTORecord jrParam = slabUtils.getParam(logId, szMethodName, slabUtils.trim(inDto.getParam("userid")));
			
			JDTORecord recPara 			= null;
			JDTORecordSet rsStock   	= null;
			JDTORecordSet recordSet1 	= null;
			JDTORecordSet recordSet2 	= null;
			JDTORecordSet recordSet3 	= null;
			
			int isInStock = 0;
			int isInLYR = 0;
			int nRtnVal = 0;
			YdStockDao ydStockDao 			= new YdStockDao();
			YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
			YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			for(int i=0; i<rowCnt; i++){
				//재료번호를 넣지 않은곳이 체크되어 있을 경우 
				if("".equals(inDto.getHeader("STL_NO").getValue(i))){
					continue;
				}
				
				
				
				recTemp = inRecord[i];
				recPara  = JDTORecordFactory.getInstance().create();					
				recPara.setField("STL_NO", inDto.getHeader("STL_NO").getValue(i));
				rsStock = JDTORecordFactory.getInstance().createRecordSet("Yd");
				
				//슬라브 공통에 존재하는 슬라브 번호인지 검사
				szRtnMsg = YdCommonUtils.getPtCommStock(inDto.getHeader("STL_NO").getValue(i), rsStock);
				
				if(szRtnMsg.equals(YdConstant.RETN_CD_FAILURE)){
					szRtnMsg = "[JSP-SESSION] [ "  + szOperationName +  "] 기존 재료정보["+ inDto.getHeader("STL_NO").getValue(i) +"] 가 공통에 존재하지않습니다 " ;
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szRtnMsg, YdConstant.DEBUG);
					gdRes.addParam("STL"+i, szRtnMsg);
					continue;

				}
				//STOCK 및 STKLYR 존재여부 검사
				slabUtils.printLog(logId, "저장품 존재 여부 검사 "+inDto.getHeader("STL_NO").getValue(i), "");
				isInStock = ydStockDao.getYdStock(recPara, rsStock, 0);
				slabUtils.printLog(logId, "검사결과: "+isInStock, "");
				
				slabUtils.printLog(logId, "적치단 존재 여부 검사 "+inDto.getHeader("STL_NO").getValue(i), "");
				isInLYR  = ydStklyrDao.getYdStklyr(recPara, rsStock, 3);
				slabUtils.printLog(logId, "검사결과: "+isInLYR, "");
				
				//저장품 적치단에 둘다 있는경우 테스트재로 생성 X
				if(isInStock >0 && isInLYR >0){
					szRtnMsg = "이 재료번호 "+inDto.getHeader("STL_NO").getValue(i)+" 는 테스트재로 사용할 수 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					gdRes.addParam("STL"+i, szRtnMsg);
					continue;
				}
				else if (isInStock <=0 && isInLYR <=0){
					slabUtils.printLog(logId, "저장품 및 적치단 둘다 존재하지 않으므로 저장품 및 적치단 insert", "");
					
					//저장품 insert
					jrParam.setField("V_STL_NO", inDto.getHeader("STL_NO").getValue(i));
					slabUtils.printLog(logId, inDto.getHeader("STL_NO").getValue(i), "");
					
					jrParam.setField("V_MODIFIER", inDto.getParam("YD_USER_ID"));
					slabUtils.printLog(logId, inDto.getParam("YD_USER_ID"), "");
					
					nRtnVal =slabYdCommDao.insSlabYd("Stock", jrParam);
					
					//yd_stock insert 실패시
					if(nRtnVal <=0){
						szRtnMsg = "재료번호 "+inDto.getHeader("STL_NO").getValue(i)+" 저장품 insert 실패";
						gdRes.addParam("STL"+i, szRtnMsg);
						continue;
					}
					//적치단 insert
					//updSlabYdStkPosFix 함수의 검사 조건과 동일 
					//작업예약 재료확인
					recordSet1 	= JDTORecordFactory.getInstance().createRecordSet("Yd");
					nRtnVal  	= ydWrkbookMtlDao.getYdWrkbookmtl(recTemp, recordSet1, 2);
					
					if(nRtnVal > 0 ){
						//szRtnMsg = "해당재료 ["+inDto.getHeader("STL_NO").getValue(i)+"] 는 작업예약재료 입니다.";
						//ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
						
						szRtnMsg = "재료번호 "+inDto.getHeader("STL_NO").getValue(i)+" 는 작업 예약재료 입니다.";
						gdRes.addParam("STL"+i, szRtnMsg);
						continue;
						//gdRes = OperateGridData.cloneResponseGridData(inDto);
						//gdRes.setMessage(szRtnMsg);
						
						 //return gdRes;
					}
					
					//스케쥴 재료확인 - 권상
					recordSet2 = JDTORecordFactory.getInstance().createRecordSet("Yd");
					recTemp.setField("YD_STK_LYR_MTL_STAT", "U");
					nRtnVal  = ydStklyrDao.getYdStklyr(recTemp, recordSet2, 102);
					
					if(nRtnVal > 0 ) {
						
						//szRtnMsg = "해당재료 ["+inDto.getHeader("STL_NO").getValue(i)+"] 는 크레인작업재료 입니다.";
						//ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
						
						szRtnMsg = "재료번호 "+inDto.getHeader("STL_NO").getValue(i)+" 는 크레인작업재료 입니다.";
						gdRes.addParam("STL"+i, szRtnMsg);
						continue;
						//gdRes = OperateGridData.cloneResponseGridData(inDto);
						//gdRes.setMessage(szRtnMsg);
						
						//return gdRes;
					}
					
					//스케쥴 재료확인 - 권하
					recordSet3 = JDTORecordFactory.getInstance().createRecordSet("Yd");
					recTemp.setField("YD_STK_LYR_MTL_STAT", "D");
					nRtnVal  = ydStklyrDao.getYdStklyr(recTemp, recordSet3, 102);
					
					if(nRtnVal > 0 ) {
						 //szRtnMsg = "해당재료 ["+inDto.getHeader("STL_NO").getValue(i)+"] 는 크레인작업재료 입니다.";
						 //ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
						szRtnMsg = "재료번호 "+inDto.getHeader("STL_NO").getValue(i)+" 는 크레인작업재료 입니다.";
						gdRes.addParam("STL"+i, szRtnMsg);
						continue;
						// gdRes = OperateGridData.cloneResponseGridData(inDto);
						 //gdRes.setMessage(szRtnMsg);
						
						// return gdRes;
					}
					
					
					slabUtils.printLog(logId, "적치단에 존재하지 않으므로 적지단 insert", "");
					
					jrParam.setField("MODIFIER", inDto.getParam("YD_USER_ID"));
					slabUtils.printLog(logId, inDto.getParam("YD_USER_ID"), "");
					
					jrParam.setField("STL_NO", inDto.getHeader("STL_NO").getValue(i));
					slabUtils.printLog(logId, inDto.getHeader("STL_NO").getValue(i), "");
					
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C");
					slabUtils.printLog(logId, inDto.getHeader("YD_STK_LYR_MTL_STAT").getValue(i), "");
					
					jrParam.setField("YD_STK_COL_GP", inDto.getHeader("YD_STK_COL_GP").getValue(i));
					jrParam.setField("YD_STK_BED_NO", inDto.getHeader("YD_STK_BED_NO").getValue(i));
					jrParam.setField("YD_STK_LYR_NO", inDto.getHeader("YD_STK_LYR_NO").getValue(i));
					slabUtils.printLog(logId, inDto.getHeader("YD_STK_COL_GP").getValue(i), "");
					slabUtils.printLog(logId, inDto.getHeader("YD_STK_BED_NO").getValue(i), "");
					slabUtils.printLog(logId, inDto.getHeader("YD_STK_LYR_NO").getValue(i), "");
					
		
					
					nRtnVal =ydStklyrDao.updYdStklyrNEWTX(jrParam, 303);
					
					if(nRtnVal <=0){
						szRtnMsg = "재료번호 "+inDto.getHeader("STL_NO").getValue(i)+" 적치단 삽입 실패";
						gdRes.addParam("STL"+i, szRtnMsg);
						continue;
					}
					
					jrParam.setField("SNDBK_GP", "T");
					jrParam.setField("MODIFIER", inDto.getParam("YD_USER_ID"));
					jrParam.setField("SNDBK_GP_ETC", inDto.getHeader("SNDBK_GP_ETC").getValue(i));
					jrParam.setField("STL_NO", inDto.getHeader("STL_NO").getValue(i));
					
					slabUtils.printLog(logId, "메모 삽입: "+inDto.getHeader("SNDBK_GP_ETC").getValue(i), "");
					nRtnVal = ydStockDao.updYdStockMessage(jrParam);
					
					if(nRtnVal <=0){
						szRtnMsg = "재료번호 "+inDto.getHeader("STL_NO").getValue(i)+" 메모 삽입 실패";
						gdRes.addParam("STL"+i, szRtnMsg);
						continue;
					}
					szRtnMsg = "재료번호 "+inDto.getHeader("STL_NO").getValue(i)+" 저장품 insert 성공";
					gdRes.addParam("STL"+i, szRtnMsg);
				}
				else if(isInStock <=0 && isInLYR >0){
					slabUtils.printLog(logId, "저장품이 존재하지 않으므로 저장품 insert", "");
					jrParam.setField("V_STL_NO", inDto.getHeader("STL_NO").getValue(i));
					slabUtils.printLog(logId, inDto.getHeader("STL_NO").getValue(i), "");
					
					jrParam.setField("V_MODIFIER", inDto.getParam("YD_USER_ID"));
					slabUtils.printLog(logId, inDto.getParam("YD_USER_ID"), "");
					
					nRtnVal =slabYdCommDao.insSlabYd("Stock", jrParam);
					//yd_stock insert 실패시
					if(nRtnVal <=0){
						szRtnMsg = "재료번호 "+inDto.getHeader("STL_NO").getValue(i)+" 저장품 insert 실패";
						gdRes.addParam("STL"+i, szRtnMsg);
						continue;
					}
					jrParam.setField("SNDBK_GP", "T");
					jrParam.setField("MODIFIER", inDto.getParam("YD_USER_ID"));
					jrParam.setField("SNDBK_GP_ETC", inDto.getHeader("SNDBK_GP_ETC").getValue(i));
					jrParam.setField("STL_NO", inDto.getHeader("STL_NO").getValue(i));
					
					slabUtils.printLog(logId, "메모 삽입: "+inDto.getHeader("SNDBK_GP_ETC").getValue(i), "");
					nRtnVal = ydStockDao.updYdStockMessage(jrParam);
					
					if(nRtnVal <=0){
						szRtnMsg = "재료번호 "+inDto.getHeader("STL_NO").getValue(i)+" 메모 삽입 실패";
						gdRes.addParam("STL"+i, szRtnMsg);
						continue;
					}
					szRtnMsg = "재료번호 "+inDto.getHeader("STL_NO").getValue(i)+" 저장품 insert 성공";
					gdRes.addParam("STL"+i, szRtnMsg);
				}
				else if(isInStock >0 && isInLYR <=0){
					
					//updSlabYdStkPosFix 함수의 검사 조건과 동일 
					//작업예약 재료확인
					recordSet1 	= JDTORecordFactory.getInstance().createRecordSet("Yd");
					nRtnVal  	= ydWrkbookMtlDao.getYdWrkbookmtl(recTemp, recordSet1, 2);
					
					if(nRtnVal > 0 ){
						//szRtnMsg = "해당재료 ["+inDto.getHeader("STL_NO").getValue(i)+"] 는 작업예약재료 입니다.";
						//ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
						
						szRtnMsg = "재료번호 "+inDto.getHeader("STL_NO").getValue(i)+" 는 작업 예약재료 입니다.";
						gdRes.addParam("STL"+i, szRtnMsg);
						continue;
						//gdRes = OperateGridData.cloneResponseGridData(inDto);
						//gdRes.setMessage(szRtnMsg);
						
						 //return gdRes;
					}
					
					//스케쥴 재료확인 - 권상
					recordSet2 = JDTORecordFactory.getInstance().createRecordSet("Yd");
					recTemp.setField("YD_STK_LYR_MTL_STAT", "U");
					nRtnVal  = ydStklyrDao.getYdStklyr(recTemp, recordSet2, 102);
					
					if(nRtnVal > 0 ) {
						
						//szRtnMsg = "해당재료 ["+inDto.getHeader("STL_NO").getValue(i)+"] 는 크레인작업재료 입니다.";
						//ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
						
						szRtnMsg = "재료번호 "+inDto.getHeader("STL_NO").getValue(i)+" 는 크레인작업재료 입니다.";
						gdRes.addParam("STL"+i, szRtnMsg);
						continue;
						//gdRes = OperateGridData.cloneResponseGridData(inDto);
						//gdRes.setMessage(szRtnMsg);
						
						//return gdRes;
					}
					
					//스케쥴 재료확인 - 권하
					recordSet3 = JDTORecordFactory.getInstance().createRecordSet("Yd");
					recTemp.setField("YD_STK_LYR_MTL_STAT", "D");
					nRtnVal  = ydStklyrDao.getYdStklyr(recTemp, recordSet3, 102);
					
					if(nRtnVal > 0 ) {
						 //szRtnMsg = "해당재료 ["+inDto.getHeader("STL_NO").getValue(i)+"] 는 크레인작업재료 입니다.";
						 //ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
						szRtnMsg = "재료번호 "+inDto.getHeader("STL_NO").getValue(i)+" 는 크레인작업재료 입니다.";
						gdRes.addParam("STL"+i, szRtnMsg);
						continue;
						// gdRes = OperateGridData.cloneResponseGridData(inDto);
						 //gdRes.setMessage(szRtnMsg);
						
						// return gdRes;
					}
					
					
					slabUtils.printLog(logId, "적치단에 존재하지 않으므로 적지단 insert", "");
					
					jrParam.setField("MODIFIER", inDto.getParam("YD_USER_ID"));
					slabUtils.printLog(logId, inDto.getParam("YD_USER_ID"), "");
					
					jrParam.setField("STL_NO", inDto.getHeader("STL_NO").getValue(i));
					slabUtils.printLog(logId, inDto.getHeader("STL_NO").getValue(i), "");
					
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C");
					slabUtils.printLog(logId, inDto.getHeader("YD_STK_LYR_MTL_STAT").getValue(i), "");
					
					jrParam.setField("YD_STK_COL_GP", inDto.getHeader("YD_STK_COL_GP").getValue(i));
					jrParam.setField("YD_STK_BED_NO", inDto.getHeader("YD_STK_BED_NO").getValue(i));
					jrParam.setField("YD_STK_LYR_NO", inDto.getHeader("YD_STK_LYR_NO").getValue(i));
					slabUtils.printLog(logId, inDto.getHeader("YD_STK_COL_GP").getValue(i), "");
					slabUtils.printLog(logId, inDto.getHeader("YD_STK_BED_NO").getValue(i), "");
					slabUtils.printLog(logId, inDto.getHeader("YD_STK_LYR_NO").getValue(i), "");
					
		
					
					nRtnVal =ydStklyrDao.updYdStklyrNEWTX(jrParam, 303);
					
					if(nRtnVal <=0){
						szRtnMsg = "재료번호 "+inDto.getHeader("STL_NO").getValue(i)+" 적치단 삽입 실패";
						gdRes.addParam("STL"+i, szRtnMsg);
						continue;
					}
					
					jrParam.setField("SNDBK_GP", "T");
					jrParam.setField("SNDBK_GP_ETC", inDto.getHeader("SNDBK_GP_ETC").getValue(i));
					slabUtils.printLog(logId, "메모 삽입: "+inDto.getHeader("SNDBK_GP_ETC").getValue(i), "");
					
					nRtnVal = ydStockDao.updYdStockMessage(jrParam);
					
					if(nRtnVal <=0){
						szRtnMsg = "재료번호 "+inDto.getHeader("STL_NO").getValue(i)+" 메모 삽입 실패";
						gdRes.addParam("STL"+i, szRtnMsg);
						continue;
					}
					
					szRtnMsg = "재료번호 "+inDto.getHeader("STL_NO").getValue(i)+" 적치단 insert 성공";
					gdRes.addParam("STL"+i, szRtnMsg);
				}
				
			}
			//JDTORecord jrParam = slabUtils.getParam(logId, szMethodName, slabUtils.trim(inDto.getParam("stl_no")));
			//jrParam.setField("V_MODIFIER", modifier);
			//jrParam.setField("V_STL_NO", modifier);
			//slabYdCommDao.insSlabYd("Stock", jrParam);
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));
		}	
	} // end of runTsRetHt
	
	/**
	 * [A] 오퍼레이션명: 슬라브 이송 종합 모니터링 통합야드 상/하차 포인트 지정 대기 현황
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecordSet getSlabFtmvPointWaitOccrDaily(JDTORecord inDto) throws DAOException {
		// Log Message
		String szMsg        = "";
		String szMethodName = "getSlabFtmvPointWaitOccrDaily";

		String szYdGp = "";

		// ERROR CHECK
		int intRtnVal = 0;

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		try {

			szMsg = "JSP-SESSION ["+ szMethodName  + "] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("DATE",       inDto.getField("V_WORK_DATE"));

			//com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyCntByBay
			intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 401);

			if (intRtnVal < 0) {
				szMsg = "ydSchruleDao  조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;

			 } // end of if
 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION ["+ szMethodName  + "] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}
	/**
	 * [A] 오퍼레이션명: 슬라브 이송 종합 모니터링 상/하차 대기 현황
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecordSet getSlabFtmvWaitOccrDaily(JDTORecord inDto) throws DAOException {
		// Log Message
		String szMsg        = "";
		String szMethodName = "getSlabFtmvWaitOccrDaily";

		String szYdGp = "";

		// ERROR CHECK
		int intRtnVal = 0;

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		try {

			szMsg = "JSP-SESSION ["+ szMethodName  + "] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("DATE",       inDto.getField("V_WORK_DATE"));

			//com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyCntByBay
			intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 402);

			if (intRtnVal < 0) {
				szMsg = "ydSchruleDao  조회중  ERROR !!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return outRecSet;

			 } // end of if
 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "JSP-SESSION ["+ szMethodName  + "] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return outRecSet;
	}
	
	/**
	 *      [A] 오퍼레이션명 : 슬라브야드 인터락 구역 설정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord insSlabInterlockSect(GridData gdReq) throws DAOException {
		String szMethodName = "insSlabInterlockSect";
		String szLogMsg = "";
		String szOperationName = "슬라브야드 인터락 구역 설정";
		String logId = gdReq.getIPAddress();
		
		YdDelegate ydDelegate           = new YdDelegate();

		try {
			szLogMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//Return Value
			JDTORecord jrRtn = null;
			
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String userId = gdReq.getParam("userid");
			String ydGp = "";//gdReq.getParam("YD_GP");
			String ydBayGp = "";//gdReq.getParam("YD_BAY_GP");
			String ydStkBedXaxis = ""; //gdReq.getParam("YD_STK_BED_XAXIS");
			String interlockId = "";
			String interlockSeq = "";
			//체크한 갯수만큼
			for(int i=0; i<rowCnt; i++){
				//인터락 구역 설정 ID SELECT 
				JDTORecord jrParam = slabUtils.getParam(logId, szMethodName, userId);
				JDTORecordSet jsInterlockId = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getYDY1L007YdInterlockId", logId, szMethodName, "인터락설정ID Select");
				interlockId = jsInterlockId.getRecord(0).getFieldString("YD_INTERLOCK_ID");
				

				
				/**********************************************************
				* 1. 인터락 구역 추가
				* 	1-1. 인터락 설정 시퀀스 Get
				*   1-2. 인터락 설정 시퀀스 사용여부 'Y' 처리
				*   1-3. 인터락 설정 테이블 insert
				**********************************************************/
				ydGp = slabUtils.trim(gdReq.getHeader("YD_GP").getValue(i));
				ydBayGp = slabUtils.nvl(slabUtils.trim(gdReq.getHeader("YD_BAY_GP").getComboHiddenValues()[gdReq.getHeader("YD_BAY_GP").getSelectedIndex(i)]),"");
				ydStkBedXaxis = slabUtils.trim(gdReq.getHeader("YD_STK_BED_XAXIS").getValue(i));
				
				if("".equals(ydGp) || "".equals(ydBayGp)||"".equals(ydStkBedXaxis)) continue;
				
				//인터락 설정 시퀀스 GET
				jrParam.setField("YD_BAY_GP"		, ydBayGp);
				JDTORecordSet jsInterlockSeq = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getYDY1L007YdInterlockSeq", logId, szMethodName, "인터락설정 시퀀스 Select");
				interlockSeq = jsInterlockSeq.getRecord(0).getFieldString("REPR_CD_GP");
				
				
				//파라미터 확인
				slabUtils.printLog(logId, "updSlabInterlockSect 파라미터 확인","");
				slabUtils.printLog(logId,"생성할 인터락 ID:"+interlockId+"| 시퀀스ID: "+interlockSeq+"| 유저: "+userId+"| 야드구분: "+ydGp+"| 동 구분: "+ydBayGp+" |설정좌표 "+ydStkBedXaxis , "");
				
				
				//인터락 시퀀스 사용처리 (L2 관리용 시퀀스)
				jrParam.setField("ITEM1"		, "Y");
				jrParam.setField("REPR_CD_GP"		, interlockSeq);
				jrParam.setField("CD_GP"		, "*");
				jrParam.setField("ITEM"		, interlockSeq.substring(5));
				jrParam.setField("MODIFIER"		, userId);
				//com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updPlateYdCarUppRuleMgt  
				slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updPlateYdCarUppRuleMgt", logId, szMethodName, "인터락시퀀스 업데이트");
				
				slabUtils.printLog(logId, "updSlabInterlockSect 인터락 구역 설정","");
				//파라미터 설정
				//DAO Parameter - Log ID, Method, 수정자 Set
				jrParam.setField("YD_INTERLOCK_ID"	, interlockId);
				jrParam.setField("REGISTER"		, userId);
				jrParam.setField("YD_GP"		, ydGp);
				jrParam.setField("YD_STK_BED_XAXIS"		, ydStkBedXaxis);
				jrParam.setField("YD_INTERLOCK_SEQ"		, interlockSeq);
				
				slabYdCommDao.insert(jrParam, "com.inisteel.cim.yd.slabyd.dao.insSlabInterlockSect", logId, szMethodName, "인터락구역 INSERT");

				
				/**********************************************************
				* 2. 연주슬라브야드L2(Y1) 으로 인터락 구역 설정 전문 전송.
				**********************************************************/
				jrParam.setField("INTERLOCK_YN"		, "Y");
				jrRtn = slabUtils.addSndData(jrRtn,slabYdCommDao.getMsgL2("YDY1L007", jrParam));
			}
			slabUtils.printLog(logId, szMethodName, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));
		}	
	} // end of insSlabInterlockSect
	
	/**
	 *      [A] 오퍼레이션명 : 슬라브야드 인터락 구역 해제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord delSlabInterlockSect(GridData gdReq) throws DAOException {
		String szMethodName = "delSlabInterlockSect";
		String szLogMsg = "";
		String szOperationName = "슬라브야드 인터락 구역 해제";
		String logId = gdReq.getIPAddress();
		
		YdDelegate ydDelegate           = new YdDelegate();

		try {
			szLogMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//Return Value
			JDTORecord jrRtn = null;
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String userId = gdReq.getParam("userid");
			String ydInterlockId =  "";
			String interlockSeq =  "";
			
			//체크한 갯수만큼
			for(int i=0; i<rowCnt; i++){
				/**********************************************************
				* 1. 인터락 구역 삭제
				**********************************************************/
				ydInterlockId = slabUtils.trim(gdReq.getHeader("YD_INTERLOCK_ID").getValue(i));
				interlockSeq = slabUtils.trim(gdReq.getHeader("YD_INTERLOCK_SEQ").getValue(i));

				if("".equals(ydInterlockId)) continue;
				//파라미터 확인
				
				slabUtils.printLog(logId, "updSlabInterlockSect 파라미터 확인","");
				slabUtils.printLog(logId,"유저: "+userId+"| 삭제할 인터락ID : "+ydInterlockId, "");
				//파라미터 설정
				//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord jrParam = slabUtils.getParam(logId, szMethodName, userId);
				jrParam.setField("MODIFIER"		, userId);
				jrParam.setField("YD_INTERLOCK_ID"		, ydInterlockId);
				slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.delSlabInterlockSect", logId, szMethodName, "인터락구역 update(delete)");
				
				
				//인터락 시퀀스 사용해제 (L2 관리용 시퀀스)
				jrParam.setField("ITEM1"		, "N");
				jrParam.setField("REPR_CD_GP"		, interlockSeq);
				jrParam.setField("CD_GP"		, "*");
				jrParam.setField("ITEM"		, interlockSeq.substring(5));
				jrParam.setField("MODIFIER"		, userId);
				//com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updPlateYdCarUppRuleMgt  
				slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updPlateYdCarUppRuleMgt", logId, szMethodName, "인터락시퀀스 업데이트");
				
				
				/**********************************************************
				* 2. 연주슬라브야드L2(Y1) 으로 인터락 구역 해제 전문 전송.
				**********************************************************/
				jrParam.setField("INTERLOCK_YN"		, "N");
				jrRtn = slabUtils.addSndData(jrRtn,slabYdCommDao.getMsgL2("YDY1L007", jrParam));
			}
			
			slabUtils.printLog(logId, szMethodName, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));
		}	
	} // end of insSlabInterlockSect
	
	/**
	 *      [A] 오퍼레이션명 : 슬라브야드 인터락 구역 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord modSlabInterlockSect(GridData gdReq) throws DAOException {
		String szMethodName = "modSlabInterlockSect";
		String szLogMsg = "";
		String szOperationName = "슬라브야드 인터락 구역 수정";
		String logId = gdReq.getIPAddress();
		
		YdDelegate ydDelegate           = new YdDelegate();

		try {
			szLogMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//Return Value
			JDTORecord jrRtn = null;
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String userId = gdReq.getParam("userid");
			String ydInterlockId =  "";
			String interlockSeq =  "";
			
			//체크한 갯수만큼
			for(int i=0; i<rowCnt; i++){
				/**********************************************************
				* 1. 인터락 구역 삭제
				**********************************************************/
				ydInterlockId = slabUtils.trim(gdReq.getHeader("YD_INTERLOCK_ID").getValue(i));
				interlockSeq = slabUtils.trim(gdReq.getHeader("YD_INTERLOCK_SEQ").getValue(i));
				JDTORecord jrParam = slabUtils.getParam(logId, szMethodName, userId);
				if(!"".equals(ydInterlockId)) {
					//파라미터 확인
					
					slabUtils.printLog(logId, "updSlabInterlockSect 파라미터 확인","");
					slabUtils.printLog(logId,"유저: "+userId+"| 삭제할 인터락ID : "+ydInterlockId, "");
					//파라미터 설정
					//DAO Parameter - Log ID, Method, 수정자 Set
					jrParam.setField("MODIFIER"		, userId);
					jrParam.setField("YD_INTERLOCK_ID"		, ydInterlockId);
					slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.delSlabInterlockSect", logId, szMethodName, "인터락구역 update(delete)");
					
					
					//인터락 시퀀스 사용해제 (L2 관리용 시퀀스)
					jrParam.setField("ITEM1"		, "N");
					jrParam.setField("REPR_CD_GP"		, interlockSeq);
					jrParam.setField("CD_GP"		, "*");
					jrParam.setField("ITEM"		, interlockSeq.substring(5));
					jrParam.setField("MODIFIER"		, userId);
					//com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updPlateYdCarUppRuleMgt  
					slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updPlateYdCarUppRuleMgt", logId, szMethodName, "인터락시퀀스 업데이트");
					
					
					/**********************************************************
					* 2. 연주슬라브야드L2(Y1) 으로 인터락 구역 해제 전문 전송.
					**********************************************************/
					jrParam.setField("INTERLOCK_YN"		, "N");
					jrRtn = slabUtils.addSndData(jrRtn,slabYdCommDao.getMsgL2("YDY1L007", jrParam));
				
				}
				//인터락 추가
				
				//인터락 구역 설정 ID SELECT 
				JDTORecordSet jsInterlockId = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getYDY1L007YdInterlockId", logId, szMethodName, "인터락설정ID Select");
				String interlockId = jsInterlockId.getRecord(0).getFieldString("YD_INTERLOCK_ID");
				

				
				/**********************************************************
				* 1. 인터락 구역 추가
				* 	1-1. 인터락 설정 시퀀스 Get
				*   1-2. 인터락 설정 시퀀스 사용여부 'Y' 처리
				*   1-3. 인터락 설정 테이블 insert
				**********************************************************/
				String ydGp = slabUtils.trim(gdReq.getHeader("YD_GP").getValue(i));
				String ydBayGp = slabUtils.nvl(slabUtils.trim(gdReq.getHeader("YD_BAY_GP").getComboHiddenValues()[gdReq.getHeader("YD_BAY_GP").getSelectedIndex(i)]),"");
				String ydStkBedXaxis = slabUtils.trim(gdReq.getHeader("YD_STK_BED_XAXIS").getValue(i));
				
				if("".equals(ydGp) || "".equals(ydBayGp)||"".equals(ydStkBedXaxis)) continue;
				
				//인터락 설정 시퀀스 GET
				jrParam.setField("YD_BAY_GP"		, ydBayGp);
				JDTORecordSet jsInterlockSeq = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getYDY1L007YdInterlockSeq", logId, szMethodName, "인터락설정 시퀀스 Select");
				interlockSeq = jsInterlockSeq.getRecord(0).getFieldString("REPR_CD_GP");
				
				
				//파라미터 확인
				slabUtils.printLog(logId, "updSlabInterlockSect 파라미터 확인","");
				slabUtils.printLog(logId,"생성할 인터락 ID:"+interlockId+"| 시퀀스ID: "+interlockSeq+"| 유저: "+userId+"| 야드구분: "+ydGp+"| 동 구분: "+ydBayGp+" |설정좌표 "+ydStkBedXaxis , "");
				
				
				//인터락 시퀀스 사용처리 (L2 관리용 시퀀스)
				jrParam.setField("ITEM1"		, "Y");
				jrParam.setField("REPR_CD_GP"		, interlockSeq);
				jrParam.setField("CD_GP"		, "*");
				jrParam.setField("ITEM"		, interlockSeq.substring(5));
				jrParam.setField("MODIFIER"		, userId);
				//com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updPlateYdCarUppRuleMgt  
				slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updPlateYdCarUppRuleMgt", logId, szMethodName, "인터락시퀀스 업데이트");
				
				slabUtils.printLog(logId, "updSlabInterlockSect 인터락 구역 설정","");
				//파라미터 설정
				//DAO Parameter - Log ID, Method, 수정자 Set
				jrParam.setField("YD_INTERLOCK_ID"	, interlockId);
				jrParam.setField("REGISTER"		, userId);
				jrParam.setField("YD_GP"		, ydGp);
				jrParam.setField("YD_STK_BED_XAXIS"		, ydStkBedXaxis);
				jrParam.setField("YD_INTERLOCK_SEQ"		, interlockSeq);
				
				slabYdCommDao.insert(jrParam, "com.inisteel.cim.yd.slabyd.dao.insSlabInterlockSect", logId, szMethodName, "인터락구역 INSERT");

				
				/**********************************************************
				* 2. 연주슬라브야드L2(Y1) 으로 인터락 구역 설정 전문 전송.
				**********************************************************/
				jrParam.setField("INTERLOCK_YN"		, "Y");
				jrRtn = slabUtils.addSndData(jrRtn,slabYdCommDao.getMsgL2("YDY1L007", jrParam));
				
				
				
				
				
			}
			
			slabUtils.printLog(logId, szMethodName, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));
		}	
	} // end of insSlabInterlockSect
	
	/**
	 *      [A] 오퍼레이션명 : 통합슬라브야드 판매재 작업동 지정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord insSlabTotYdEqpWorkBay(GridData gdReq) throws DAOException {
		String szMethodName = "insSlabTotYdEqpWorkBay";
		String szLogMsg = "";
		String szOperationName = "통합슬라브야드 판매재 작업동 지정";
		String logId = gdReq.getIPAddress();
		
		try {
			szLogMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//Return Value
			JDTORecord jrRtn = null;
			
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String userId = gdReq.getParam("userid");
			
			String reprCdGp = "";
			String cdGp = "";
			String item = "";
			String item1 = "";
			String dtlItem1 = "";
			String dtlItem2 = "";
			String dtlItem3 = "";
			String dtlItem4 = "";
			String dtlItem5 = "";
			String dtlItem6 = "";
			
			
			//체크한 갯수만큼 
			for(int i=0; i<rowCnt; i++){
				JDTORecord jrParam = slabUtils.getParam(logId, szMethodName, userId);
				
				//INSERT / UPDATE 구분
				reprCdGp = slabUtils.trim(gdReq.getHeader("REPR_CD_GP").getValue(i));
				cdGp = slabUtils.trim(gdReq.getHeader("CD_GP").getValue(i));
				item = slabUtils.trim(gdReq.getHeader("ITEM").getValue(i));
				slabUtils.printLog(logId, "판매재 작업동 지정 KEY- REPR_CD_GP["+reprCdGp+"], CD_GP["+cdGp+"], item["+item+"]","SL");
				
				//INSERT 경우 // 설정 ID SELECT 
				if("".equals(cdGp) || "".equals(item)){
					slabUtils.printLog(logId, "판매재 작업동 지정 INSERT 이므로 CD_GP, ITEM SELECT","SL");
					JDTORecordSet rsResult = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getSlabTotEqpWorkBayKey", logId, szMethodName, "인터락설정ID Select");
					cdGp = slabUtils.trim(rsResult.getRecord(0).getFieldString("CD_GP"));
					item = slabUtils.trim(rsResult.getRecord(0).getFieldString("ITEM"));
					slabUtils.printLog(logId, "판매재 작업동 지정 KEY NEW- REPR_CD_GP["+reprCdGp+"], CD_GP["+cdGp+"], item["+item+"]","SL");
				}

				item1 = slabUtils.trim(gdReq.getHeader("ITEM1").getValue(i));
				dtlItem1 = slabUtils.trim(gdReq.getHeader("DTL_ITEM1").getValue(i));
				dtlItem2 = slabUtils.trim(gdReq.getHeader("DTL_ITEM2").getValue(i));
				dtlItem3 = slabUtils.trim(gdReq.getHeader("DTL_ITEM3").getValue(i));
				dtlItem4 = slabUtils.trim(gdReq.getHeader("DTL_ITEM4").getValue(i));
				dtlItem5 = slabUtils.trim(gdReq.getHeader("DTL_ITEM5").getValue(i));
				dtlItem6 = slabUtils.trim(gdReq.getHeader("DTL_ITEM6").getValue(i));
				
				dtlItem1 = dtlItem1.replaceAll("[.: ]", "");//날짜 . : 공백 제거
				dtlItem2 = dtlItem2.replaceAll("[.: ]", "");//날짜 . : 공백 제거
				
				
				if("".equals(reprCdGp) || "".equals(cdGp) || "".equals(item)||"".equals(item1)) continue;
				
				slabUtils.printLog(logId, "판매재 작업동 지정동["+item1+"], 지정기간from["+dtlItem1+"], 지정기간to["+dtlItem2+"]" +
						", 고객사["+dtlItem3+"], 규격약호["+dtlItem4+"], 두께["+dtlItem5+"], 주문행번["+dtlItem6+"]","SL");
				//파라미터 설정
				//DAO Parameter - Log ID, Method, 수정자 Set
				jrParam.setField("REPR_CD_GP"		, reprCdGp);
				jrParam.setField("CD_GP"			, cdGp);
				jrParam.setField("ITEM"				, item);
				jrParam.setField("ITEM1"			, item1);
				jrParam.setField("DTL_ITEM1"		, dtlItem1);
				jrParam.setField("DTL_ITEM2"		, dtlItem2);
				jrParam.setField("DTL_ITEM3"		, dtlItem3);
				jrParam.setField("DTL_ITEM4"		, dtlItem4);
				jrParam.setField("DTL_ITEM5"		, dtlItem5);
				jrParam.setField("DTL_ITEM6"		, dtlItem6);
						
				jrParam.setField("MODIFIER"			, userId);
				jrParam.setField("REPR_CD_CONTENTS"		, "통합야드 판매재 작업동 지정");
		
				slabYdCommDao.insert(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabTotEqpWorkBay", logId, szMethodName, "작업동 지정 UPSERT");

			}
			slabUtils.printLog(logId, szMethodName, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));
		}	
	} // end of insSlabTotYdEqpWorkBay
	
	/**
	 *      [A] 오퍼레이션명 : 통합슬라브야드 판매재 작업동 지정 삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord delSlabTotYdEqpWorkBay(GridData gdReq) throws DAOException {
		String szMethodName = "delSlabTotYdEqpWorkBay";
		String szLogMsg = "";
		String szOperationName = "통합슬라브야드 판매재 작업동 지정 삭제";
		String logId = gdReq.getIPAddress();
		
		try {
			szLogMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//Return Value
			JDTORecord jrRtn = null;
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String userId = gdReq.getParam("userid");
			String reprCdGp = "";
			String cdGp = "";
			String item = "";
			

			//체크한 갯수만큼
			for(int i=0; i<rowCnt; i++){

				reprCdGp = slabUtils.trim(gdReq.getHeader("REPR_CD_GP").getValue(i));
				cdGp = slabUtils.trim(gdReq.getHeader("CD_GP").getValue(i));
				item = slabUtils.trim(gdReq.getHeader("ITEM").getValue(i));
				
				//파라미터 확인
				if("".equals(reprCdGp) || "".equals(cdGp) || "".equals(item)) continue;
				slabUtils.printLog(logId,"CD_GP["+cdGp+"], ITEM["+item+"] 작업동 기준 삭제", "SL");
				//파라미터 설정
				//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord jrParam = slabUtils.getParam(logId, szMethodName, userId);
				jrParam.setField("MODIFIER"		, userId);
				jrParam.setField("REPR_CD_GP"	, reprCdGp);
				jrParam.setField("CD_GP"		, cdGp);
				jrParam.setField("ITEM"			, item);
				slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.delSlabTotEqpWorkBay", logId, szMethodName, "작업동지정 update(delete)");

			}
			
			slabUtils.printLog(logId, szMethodName, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));
		}	
	} // end of delSlabTotYdEqpWorkBay
	
	/**
	 *      [A] 오퍼레이션명 : 통합슬라브야드 장비별 상태 설정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updSlabTotYdEqpStat(GridData gdReq) throws DAOException {
		String szMethodName = "updSlabTotYdEqpStat";
		String szLogMsg = "";
		String szOperationName = "통합슬라브야드 장비별 상태 설정";
		String logId = gdReq.getIPAddress();
		
		try {
			szLogMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//Return Value
			JDTORecord jrRtn = null;
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String userId = gdReq.getParam("userid");
			
			
			String ydEqpStat = "";
			String ydEqpId = "";

			//체크한 갯수만큼
			for(int i=0; i<rowCnt; i++){
				JDTORecord jrParam = slabUtils.getParam(logId, szMethodName, userId);
				
				ydEqpId = slabUtils.trim(gdReq.getHeader("YD_EQP_ID").getValue(i));
				ydEqpStat = slabUtils.trim(gdReq.getHeader("YD_EQP_STAT").getComboHiddenValues()[gdReq.getHeader("YD_EQP_STAT").getSelectedIndex(i)]);
				slabUtils.printLog(logId,"YD_EQP_ID["+ydEqpId+"], YD_EQP_STAT["+ydEqpStat+"] 장비 상태 변경", "SL");
				
				if("".equals(ydEqpId) || "".equals(ydEqpStat)) continue;
				jrParam.setField("MODIFIER"		, userId);
				jrParam.setField("YD_EQP_STAT"	, ydEqpStat);
				jrParam.setField("YD_EQP_ID"	, ydEqpId);
				slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.updEqpOprnStat", logId, szMethodName, "장비별 상태 update");
				

			}
			
			slabUtils.printLog(logId, szMethodName, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));
		}	
	} // end of updSlabTotYdEqpStat
	
//------------------------------------------------------------------------------------	
	/**
	 *      [A] 오퍼레이션명 : 표준시간 계산로직
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord insYdGradRule(GridData gdReq) throws DAOException {
		String szMethodName = "insYdGradRule";
		String szLogMsg = "";
		String szOperationName = "표준시간 계산로직 지정";
		String logId = gdReq.getIPAddress();
		
		try {
			szLogMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//Return Value
			JDTORecord jrRtn = null;
			
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String userId = gdReq.getParam("userid");
			
			String reprCdGp = "";
			String cdGp = "";
			String item = "";
			String itemValue1 = "";
			String dtlItem1 = "";
			String dtlItem2 = "";
			
			//체크한 갯수만큼 
			for(int i=0; i<rowCnt; i++){
				JDTORecord jrParam = slabUtils.getParam(logId, szMethodName, userId);
				
				//INSERT / UPDATE 구분
				reprCdGp = slabUtils.trim(gdReq.getHeader("REPR_CD_GP").getValue(i));
				cdGp = slabUtils.trim(gdReq.getHeader("CD_GP").getValue(i));
				item = slabUtils.trim(gdReq.getHeader("ITEM").getValue(i));
				slabUtils.printLog(logId, "표준시간 계산로직 KEY- REPR_CD_GP["+reprCdGp+"], CD_GP["+cdGp+"], item["+item+"]","SL");
				
				//INSERT 경우 // 설정 ID SELECT 
				if("".equals(cdGp) || "".equals(item)){
					slabUtils.printLog(logId, "표준시간 계산로직 INSERT 이므로 CD_GP, ITEM SELECT","SL");
					JDTORecordSet rsResult = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getSlabTotEqpWorkBayKey", logId, szMethodName, "인터락설정ID Select");
					cdGp = slabUtils.trim(rsResult.getRecord(0).getFieldString("CD_GP"));
					item = slabUtils.trim(rsResult.getRecord(0).getFieldString("ITEM"));
					slabUtils.printLog(logId, "표준시간 계산로직  KEY NEW- REPR_CD_GP["+reprCdGp+"], CD_GP["+cdGp+"], item["+item+"]","SL");
				}

				itemValue1 = slabUtils.trim(gdReq.getHeader("ITEM_VALUE1").getValue(i));
				dtlItem1 = slabUtils.trim(gdReq.getHeader("DTL_ITEM1").getValue(i));
				dtlItem2 = slabUtils.trim(gdReq.getHeader("DTL_ITEM2").getValue(i));
				
				if("".equals(reprCdGp) || "".equals(cdGp) || "".equals(item)||"".equals(itemValue1)) continue;
				
				slabUtils.printLog(logId, "적용 스케줄["+itemValue1+"], 기울기["+dtlItem1+"], Y절편["+dtlItem2+"]","SL");
				//파라미터 설정
				//DAO Parameter - Log ID, Method, 수정자 Set
				jrParam.setField("REPR_CD_GP"		, reprCdGp);
				jrParam.setField("CD_GP"			, cdGp);
				jrParam.setField("ITEM"				, item);
				jrParam.setField("ITEM_VALUE1"		, itemValue1);
				jrParam.setField("DTL_ITEM1"		, dtlItem1);
				jrParam.setField("DTL_ITEM2"		, dtlItem2);
						
				jrParam.setField("MODIFIER"			, userId);
				jrParam.setField("REPR_CD_CONTENTS"	, "스케줄코드별 회귀분석값");
		
				slabYdCommDao.insert(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updYdGradRule", logId, szMethodName, "작업동 지정 UPSERT");

			}
			//slabUtils.printLog(logId, szMethodName, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));
		}	
	} // end of insYdGradRule
	
	/**
	 *      [A] 오퍼레이션명 : 표준시간 계산로직 삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord dltYdGradRule(GridData gdReq) throws DAOException {
		String szMethodName = "dltYdGradRule";
		String szLogMsg = "";
		String szOperationName = "표준시간 계산로직 삭제";
		String logId = gdReq.getIPAddress();
		
		try {
			szLogMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//Return Value
			JDTORecord jrRtn = null;
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String userId = gdReq.getParam("userid");
			String reprCdGp = "";
			String cdGp = "";
			String item = "";

			//체크한 갯수만큼
			for(int i=0; i<rowCnt; i++){

				reprCdGp = slabUtils.trim(gdReq.getHeader("REPR_CD_GP").getValue(i));
				cdGp = slabUtils.trim(gdReq.getHeader("CD_GP").getValue(i));
				item = slabUtils.trim(gdReq.getHeader("ITEM").getValue(i));
				
				//파라미터 확인
				if("".equals(reprCdGp) || "".equals(cdGp) || "".equals(item)) continue;
				slabUtils.printLog(logId,"CD_GP["+cdGp+"], ITEM["+item+"] 표준시간 계산로직 삭제", "SL");
				//파라미터 설정
				//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord jrParam = slabUtils.getParam(logId, szMethodName, userId);
				jrParam.setField("MODIFIER"		, userId);
				jrParam.setField("REPR_CD_GP"	, reprCdGp);
				jrParam.setField("CD_GP"		, cdGp);
				jrParam.setField("ITEM"			, item);
				slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.dltYdGradRule", logId, szMethodName, "표준시간 계산로직 update(delete)");

			}
			
			//slabUtils.printLog(logId, szMethodName, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));
		}	
	} // end of dltYdGradRule
//----------------------------------------------------------------------------------------------------	
	
	/**
	 *      [A] 오퍼레이션명 : MES모니터링 지표관리 수정/추가
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updMESMonitorRule(GridData gdReq) throws DAOException {
		String mthdNm = "MES모니터링 지표관리 수정/추가[SlabJspSeEJB.updMESMonitorRule] < " + gdReq.getNavigateValue();
		String logId  = gdReq.getIPAddress();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			slabUtils.printLog(logId, mthdNm, "S+");
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String userId = gdReq.getParam("userid");
			//수정대상 별 UPDATE/INSERT
			for(int i=0; i<rowCnt; i++){
				String mtId = slabUtils.trim(gdReq.getHeader("MT_ID").getValue(i));
				String sortOrder 	 = slabUtils.trim(gdReq.getHeader("SORT_ORDER").getValue(i));
				String mtRunInterval = slabUtils.trim(gdReq.getHeader("MT_RUN_INTERVAL").getValue(i));
				String prId 		 = slabUtils.trim(gdReq.getHeader("PR_ID").getValue(i));
				String refcol	 	 = slabUtils.trim(gdReq.getHeader("REFCOL").getValue(i));
				String refcolDtl	 = slabUtils.trim(gdReq.getHeader("REFCOL_DTL").getValue(i));
				String thrshldL1 	 = slabUtils.trim(gdReq.getHeader("THRSHLD_L1").getValue(i));
				String thrshldL2 	 = slabUtils.trim(gdReq.getHeader("THRSHLD_L2").getValue(i));
				String weight	 	 = slabUtils.trim(gdReq.getHeader("WEIGHT").getValue(i));
				String expUseYn	 	 = slabUtils.trim(gdReq.getHeader("EXP_USE_YN").getValue(i));
				String expression	 = slabUtils.trim(gdReq.getHeader("EXPRESSION").getValue(i));
				String chargerEmpno	 = slabUtils.trim(gdReq.getHeader("CHARGER_EMPNO").getValue(i));
			
				JDTORecord jrParam = slabUtils.getParam(logId, mthdNm, "");
				jrParam.setField("MT_ID"			, mtId);
				jrParam.setField("SORT_ORDER"		, sortOrder);
				jrParam.setField("MT_RUN_INTERVAL"	, mtRunInterval);
				jrParam.setField("PR_ID"			, prId);
				jrParam.setField("REFCOL"			, refcol);
				jrParam.setField("REFCOL_DTL"		, refcolDtl);
				jrParam.setField("THRSHLD_L1"		, thrshldL1);
				jrParam.setField("THRSHLD_L2"		, thrshldL2);
				jrParam.setField("WEIGHT"			, weight);
				jrParam.setField("EXP_USE_YN"		, expUseYn);
				jrParam.setField("EXPRESSION"		, expression);
				jrParam.setField("CHARGER_EMPNO"	, chargerEmpno);
				jrParam.setField("MODIFIER"			, userId);
				
				slabUtils.printParam(logId + "지표 upsert 대상", jrParam);
				
				slabYdCommDao.insert(jrParam, "com.inisteel.cim.yd.slab.dao.YdCommDAO.MESMonitorRuleMng.insMTWeightRule", logId, mthdNm, "지표 upsert");
				//slabYdCommDao.insert(jrParam, "com.inisteel.cim.yd.slab.dao.YdCommDAO.MESMonitorRuleMng.insMTWeightRuleHist", logId, mthdNm, "지표 Hist insert");
			}
			
			String prIds = "";
			
			//수정된 공정 ID 별 VERSION UPDATE 및 HISTORY INSERT
			for(int i=0; i<rowCnt; i++){
				String prId = slabUtils.trim(gdReq.getHeader("PR_ID").getValue(i));
				System.out.println(logId+ prId+"지표 확인");			
				//이미 확인한 공정id는 PASS
				if(!prIds.contains(prId)){
					System.out.println(logId+ prId+"확인한 공정ID가 아님");	
					prIds += prId +",";
					
					//공정 id별 version update
					int nextVer = getNextVerPRID(prId, logId);
					
					JDTORecord jrParam = slabUtils.getParam(logId, mthdNm, "");
					jrParam.setField("PR_ID"		, prId);
					jrParam.setField("MT_VER"		, nextVer);
					jrParam.setField("MODIFIER"		, userId);
					
					slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.slab.dao.YdCommDAO.MESMonitorRuleMng.updMTWeightRuleVer", logId, mthdNm, "VERSION 업데이트");
					slabYdCommDao.insert(jrParam, "com.inisteel.cim.yd.slab.dao.YdCommDAO.MESMonitorRuleMng.insMTWeightRuleHistByRuleTable", logId, mthdNm, "지표 Hist insert");
					
				}
			}
					

			jrRtn.setField("RTN_CD"	, "1");
				
		
			slabUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, mthdNm, e));
		}	
	} // end of updMESMonitorRule
	

	/**
	 *      [A] 오퍼레이션명 : MES모니터링 공정 다음 버전 확인 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param String mtId, String logId
	 *      @return boolean
	 *      @throws DAOException
	*/
	public int getNextVerPRID(String prId, String logId) throws DAOException {
		String mthdNm = "MES모니터링 공정 다음 버전 확인 [SlabJspSeEJB.getNextVerPRID] < " + "[SlabJspSeEJB.updMESMonitorRule] ";

		try {
			slabUtils.printLog(logId, mthdNm, "S+");
			
			JDTORecord jrParam = slabUtils.getParam(logId, mthdNm, "");
			JDTORecordSet jrResultSet = JDTORecordFactory.getInstance().createRecordSet("YD");
			int nextVer = 0;
			
			System.out.println(logId+ prId+"지표존재여부 확인쿼리 실행");	
			jrParam.setField("PR_ID", prId);
			jrResultSet = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.slab.dao.YdCommDAO.MESMonitorRuleMng.getNextVerPRID", logId, mthdNm, "공정 다음버전 확인");
			
			//조회 데이터 존재시 RETURN 
			if(jrResultSet != null && jrResultSet.size() >0) {
				nextVer = Integer.parseInt(jrResultSet.getRecord(0).getFieldString("NEXT_VER"));
			}
			else {
				nextVer = 1;
			}
			
			slabUtils.printLog(logId, mthdNm, "S-");

			return nextVer;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, mthdNm, e));
		}	
	} // end of getNextVerPRID
	
	/**
	 *      [A] 오퍼레이션명 : MES모니터링 지표관리 삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord delMESMonitorRule(GridData gdReq) throws DAOException {
		String mthdNm = "MES모니터링 지표관리 삭제[SlabJspSeEJB.delMESMonitorRule] < " + gdReq.getNavigateValue();
		String logId  = gdReq.getIPAddress();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			slabUtils.printLog(logId, mthdNm, "S+");
			
			String userId = gdReq.getParam("userid");
			//삭제대상 지표 삭제
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			for(int i=0; i<rowCnt; i++){
				String mtId = slabUtils.trim(gdReq.getHeader("MT_ID").getValue(i));
				slabUtils.printLog(logId, "MT_ID ["+mtId+"] 에 해당하는 지표 삭제", "SL");
				System.out.println(logId+ "MT_ID ["+mtId+"] 에 해당하는 지표 삭제");	
				JDTORecord jrParam = slabUtils.getParam(logId, mthdNm, "");
				jrParam.setField("MT_ID", mtId);
				slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.slab.dao.YdCommDAO.MESMonitorRuleMng.delMTWeightRule", logId, mthdNm, "지표삭제1row");

			}
			
			String prIds = "";
			
			//수정된 공정 ID 별 VERSION UPDATE 및 HISTORY INSERT
			for(int i=0; i<rowCnt; i++){
				String prId = slabUtils.trim(gdReq.getHeader("PR_ID").getValue(i));
				System.out.println(logId+ prId+"지표 확인");			
				//이미 확인한 공정id는 PASS
				if(!prIds.contains(prId)){
					System.out.println(logId+ prId+"확인한 공정ID가 아님");	
					prIds += prId +",";
					
					//공정 id별 version update
					int nextVer = getNextVerPRID(prId, logId);
					
					JDTORecord jrParam = slabUtils.getParam(logId, mthdNm, "");
					jrParam.setField("PR_ID"		, prId);
					jrParam.setField("MT_VER"		, nextVer);
					jrParam.setField("MODIFIER"		, userId);
					
					slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.slab.dao.YdCommDAO.MESMonitorRuleMng.updMTWeightRuleVer", logId, mthdNm, "VERSION 업데이트");
					slabYdCommDao.insert(jrParam, "com.inisteel.cim.yd.slab.dao.YdCommDAO.MESMonitorRuleMng.insMTWeightRuleHistByRuleTable", logId, mthdNm, "지표 Hist insert");
					
				}
			}
			

			jrRtn.setField("RTN_CD"	, "1");
				
		
			slabUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, mthdNm, e));
		}	
	} // end of updMESMonitorRule
	

	
	/**
	 *      [A] 오퍼레이션명 : 통합슬라브야드 슬라브 장비 작업 저장위치 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public String updSlabYdStkPosFixTotNew(JDTORecord [] inRecord, String logId) throws DAOException {
		/*
		 * 1. TO 위치에 재료가 있는경우
		 * 	1-1 재료적치정보를 삭제하는경우
		 * 		재료 적치정보 CLEAR, 공통테이블 UPDATE
		 * 
		 *  1-2 신규 재료로 UPDATE
		 *  	기존 적치정보 CLEAR, 공통테이블 UPDATE
		 *  	신규 재료의 기존 적치 정보 CLEAR, 신규 재료 적치정보 UPDATE ,공통테이블 UPDATE
		 *  	
		 *  
		 * 2. TO 위치에 재료가 없는경우
		 *  	신규 재료의 기존 적치 정보 CLEAR, 신규 재료 적치정보 UPDATE ,공통테이블 UPDATE
		 * 
		 * 
		 * TO위치 적치정보 존재시 CLEAR 처리
		 * FROM위치 적치정보 존재시 CLEAR 처리
		 * TO위치에 신규 적치정보 입력
		 */
		String szMethodName = "updSlabYdStkPosFixTotNew";
		//String szOperationName = "통합슬라브야드 슬라브 장비 작업 저장위치 수정";
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;

		JDTORecordSet jrResultSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord jrResult =  JDTORecordFactory.getInstance().create();
		int intRtnVal = 0;
		
		//차량작업용
		String actionCode = "";
		String ydCarSchId = "";
		String arrWlocCd  = "";
		String modifier   = "";
		String ydCarUdStopLoc = "";
		JDTORecord jrParam = slabUtils.getParam(logId, szMethodName, modifier);
		try {			
	//JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			
			// 처리 할 필요없는 경우
			if (inRecord.length < 1 ){
				szRtnMsg = "입력 정보가 없습니다.";
				return szRtnMsg;
			}
			//String logId = inRecord[0].getResultCode();
			slabUtils.printLog(logId,"[Jsp Session : "+szMethodName+"] 메소드 시작", "S+");
			
			
			for(int i=0; i<inRecord.length ; i++){
				String inputStlNo 		=  inRecord[i].getFieldString("STL_NO").trim(); //앞뒤 공백을 잘못 입력한 경우를 대비해 trim. 재료번호가 공백일시 to위치의 재료정보 clear
				String inputYdStkColGp 	=  inRecord[i].getFieldString("YD_STK_COL_GP"); //재료 to위치 적치열 SA0101
				String inputYdStkBedNo 	=  inRecord[i].getFieldString("YD_STK_BED_NO"); //재료 to위치 베드   01
				String inputYdStkLyrNo 	=  inRecord[i].getFieldString("YD_STK_LYR_NO"); //재료 to위치 단     001
				String ydEqpId			=  inRecord[i].getFieldString("YD_EQP_ID");     //장비코드 
				
				modifier   		=  inRecord[i].getFieldString("MODIFIER");      		//1521612
				actionCode   	=  inRecord[i].getFieldString("ACTIONCODE");    		//updPosfix
				ydCarSchId   	=  inRecord[i].getFieldString("YD_CAR_SCH_ID"); 		//차량스케줄id(차량작업용)
				arrWlocCd  	 	=  inRecord[i].getFieldString("ARR_WLOC_CD");   		//착지개소코드(차량작업용)
				ydCarUdStopLoc  =  inRecord[i].getFieldString("YD_CARUD_STOP_LOC");  	//차량하자위치(차량작업용)	
				
				String inputStlPrevYdStkColGp = "";
				String inputStlPrevYdStkBedNo = "";
				String inputStlPrevYdStkLyrNo = "";
				
				//입력재료 적치정보 검사하여 다른야드에 있을시 처리불가
				jrParam = slabUtils.getParam(logId, szMethodName, modifier);
				jrParam.setField("STL_NO"				, inputStlNo);
				jrParam.setField("YD_STK_LYR_MTL_STAT"	, "");
				
				jrResultSet = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO", logId, szMethodName, "적치단 정보 select");
				
				if(jrResultSet != null && jrResultSet.size() >0) {
					inputStlPrevYdStkColGp = jrResultSet.getRecord(0).getFieldString("YD_STK_COL_GP");
					inputStlPrevYdStkBedNo = jrResultSet.getRecord(0).getFieldString("YD_STK_BED_NO");
					inputStlPrevYdStkLyrNo = jrResultSet.getRecord(0).getFieldString("YD_STK_LYR_NO");
					
					if(!inputStlPrevYdStkColGp.startsWith("S")){
						slabUtils.printLog(logId, "["+inputStlNo+"]가 다른야드 ["+inputStlPrevYdStkColGp+"]에 적치정보가 있어 처리 불가" , "SL");
						continue;
					}
					//통합야드에 적치정보가 있다면 적치정보 clear
					else{
						slabUtils.printLog(logId, "["+inputStlNo+"] 통합야드 ["+inputStlPrevYdStkColGp+inputStlPrevYdStkBedNo+inputStlPrevYdStkLyrNo+"]에 적치정보 CLEAR" , "SL");
						
						jrParam.setField("YD_STK_COL_GP"		, inputStlPrevYdStkColGp);
						jrParam.setField("YD_STK_BED_NO"		, inputStlPrevYdStkBedNo);
						jrParam.setField("YD_STK_LYR_NO"		, inputStlPrevYdStkLyrNo);
						jrParam.setField("STL_NO"				, "");
						jrParam.setField("YD_STK_LYR_MTL_STAT"	, "E");
						jrParam.setField("MODIFIER"				, modifier);
						
						slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo", logId, szMethodName, "적치정보 update");
						
					}
				}
				
				//to 위치 저장위치에 기존 적치정보 확인. 있다면 공통적치정보 CLEAR
				jrParam.setField("YD_STK_COL_GP"		, inputYdStkColGp);
				jrParam.setField("YD_STK_BED_NO"		, inputYdStkBedNo);
				jrParam.setField("YD_STK_LYR_NO"		, inputYdStkLyrNo);
				
				jrResultSet = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyr", logId, szMethodName, "적치단 정보 select");
				
				if(jrResultSet == null || jrResultSet.size() <=0) {
					slabUtils.printLog(logId, "["+inputYdStkColGp+inputYdStkBedNo+inputYdStkLyrNo+"] 적치단정보 없음", "SL");
					continue;
				}
				String prevStlNo = jrResultSet.getRecord(0).getFieldString("STL_NO");
				
				if(!"".equals(prevStlNo)){
					slabUtils.printLog(logId, "["+inputYdStkColGp+inputYdStkBedNo+inputYdStkLyrNo+"]에 ["+prevStlNo+"] 적치정보가 있어 공통정보 clear처리" , "SL");
					
					szRtnMsg = YdCommonUtils.getPtCommStock(prevStlNo, jrResultSet);
					
					if(YdConstant.RETN_CD_FAILURE.equals(szRtnMsg)){
						slabUtils.printLog(logId, "["+prevStlNo+"] 공통정보 존재하지 않음." , "SL");
					}
					else{
						slabUtils.printLog(logId, "["+prevStlNo+"] 공통정보 존재" , "SL");
						
						jrResultSet.absolute(1);
			        	jrResult = jrResultSet.getRecord();

			        	String szYdStrLoc     = ydDaoUtils.paraRecChkNull(jrResult,"YD_STR_LOC");
			        	String ydStrLocHis1   = ydDaoUtils.paraRecChkNull(jrResult,"YD_STR_LOC_HIS1");
			        	String prevStlMorS    = ydDaoUtils.paraRecChkNull(jrResult, "PT_TB_COMM");//주편 슬라브 구분

			        	jrParam.setField("MSLAB_NO",   prevStlNo);
			        	jrParam.setField("SLAB_NO",   prevStlNo);
						jrParam.setField("YD_GP",         inputYdStkColGp.substring(0,1));
			        	jrParam.setField("YD_BAY_GP",     "");
			        	jrParam.setField("YD_EQP_GP",     "");
			        	jrParam.setField("YD_STK_COL_NO", "");
			        	jrParam.setField("YD_STK_BED_NO", "");
			        	jrParam.setField("YD_STK_LYR_NO", "");
			        	jrParam.setField("YD_STR_LOC", "");
			        	jrParam.setField("FNL_REG_PGM",   "stkPosFixTotNew");
			        	jrParam.setField("MODIFIER",  modifier);
			        	jrParam.setField("YD_STR_LOC_HIS1",  szYdStrLoc);
			        	jrParam.setField("YD_STR_LOC_HIS2", 	ydStrLocHis1);
			        	
			        	if("B".equals(prevStlMorS)){
			        		intRtnVal = this.updY1YdStock(jrParam, 2);
			        		if(intRtnVal<0) slabUtils.printLog(logId, "["+prevStlNo+"] 주편공통Table 저장위치 등록 실패" , "SL");
			        	}else if ("S".equals(prevStlMorS)) {
			        		intRtnVal = this.updY1YdStock(jrParam,  0);
			        		if(intRtnVal<0) slabUtils.printLog(logId, "["+prevStlNo+"] 슬라브공통Table 저장위치 등록 실패" , "SL");
			         	}
			        	
					}//if(YdConstant.RETN_CD_FAILURE.equals(szRtnMsg)) else 
					
				}//if(!"".equals(prevStlNo))
				
				//				
				slabUtils.printLog(logId, "["+inputYdStkColGp+inputYdStkBedNo+inputYdStkLyrNo+"]에 ["+inputStlNo+"] 적치정보 Update" , "SL");
				
				jrParam.setField("YD_STK_COL_GP"		, inputYdStkColGp);
				jrParam.setField("YD_STK_BED_NO"		, inputYdStkBedNo);
				jrParam.setField("YD_STK_LYR_NO"		, inputYdStkLyrNo);
				jrParam.setField("STL_NO"				, inputStlNo);
				jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C");
				jrParam.setField("MODIFIER"				, modifier);
				
				slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo", logId, szMethodName, "적치정보 update");
				
				
				slabUtils.printLog(logId, "["+inputStlNo+"] 공통정보 Update" , "SL");
				
				jrResultSet = JDTORecordFactory.getInstance().createRecordSet("YD");
				szRtnMsg = YdCommonUtils.getPtCommStock(inputStlNo, jrResultSet);
		
				if(YdConstant.RETN_CD_FAILURE.equals(szRtnMsg)){
					slabUtils.printLog(logId, "["+inputStlNo+"] 공통정보 존재하지 않음." , "SL");
				}
				else{
					slabUtils.printLog(logId, "["+inputStlNo+"] 공통정보 존재" , "SL");
					
					jrResultSet.absolute(1);
		        	jrResult = jrResultSet.getRecord();

		        	String szYdStrLoc     = ydDaoUtils.paraRecChkNull(jrResult,"YD_STR_LOC");
		        	String ydStrLocHis1   = ydDaoUtils.paraRecChkNull(jrResult,"YD_STR_LOC_HIS1");
		        	String inputStlMorS   = ydDaoUtils.paraRecChkNull(jrResult, "PT_TB_COMM");//주편 슬라브 구분
		        	String ydGp           = ydDaoUtils.paraRecChkNull(jrResult, "YD_GP");
		        	
		        	jrParam.setField("MSLAB_NO",   inputStlNo);
		        	jrParam.setField("SLAB_NO" ,   inputStlNo);
					jrParam.setField("YD_GP",         inputYdStkColGp.substring(0,1));
		        	jrParam.setField("YD_BAY_GP",     inputYdStkColGp.substring(1,2));
		        	jrParam.setField("YD_EQP_GP",     inputYdStkColGp.substring(2,4));
		        	jrParam.setField("YD_STK_COL_NO", inputYdStkColGp.substring(4,6));
		        	jrParam.setField("YD_STK_BED_NO", inputYdStkBedNo);
		        	jrParam.setField("YD_STK_LYR_NO", inputYdStkLyrNo);
		        	jrParam.setField("YD_STR_LOC", inputYdStkColGp+inputYdStkBedNo+inputYdStkLyrNo.substring(1,3));
		        	jrParam.setField("FNL_REG_PGM",   "stkPosFixTotNew");
		        	jrParam.setField("MODIFIER",  modifier);
		        	jrParam.setField("YD_STR_LOC_HIS1",  szYdStrLoc);
		        	jrParam.setField("YD_STR_LOC_HIS2", 	ydStrLocHis1);
		        	
		        	//권하위치의 야드구분이 다른 경우에는 입고일자와 입고시각을 업데이트 처리
		        	if(inputYdStkColGp.substring(0,1).equals(ydGp)){
		        		String szCurDateTime = YdUtils.getCurDate("yyyyMMddHHmmss");
		        		String szRECEIPT_DATE = szCurDateTime.substring(0, 8);
		        		String RECEIPT_TIME = szCurDateTime.substring(8);

		        		jrParam.setField("RECEIPT_DATE", 	szRECEIPT_DATE);				//입고일자
		        		jrParam.setField("RECEIPT_TIME", 	RECEIPT_TIME);					//입고시각
		        	}
		        	
		        	slabUtils.printLog(logId, "["+inputStlNo+"] ["+inputStlMorS+"]공통정보 저장위치["+inputYdStkColGp+inputYdStkBedNo+inputYdStkLyrNo+"] 전 저장위치 ["+szYdStrLoc+"] 전전 저장위치["+ydStrLocHis1+"] 업데이트" , "SL");
					
		        	if("B".equals(inputStlMorS)){
		        		intRtnVal = this.updY1YdStock(jrParam, 2);
		        		if(intRtnVal<0) slabUtils.printLog(logId, "["+prevStlNo+"] 주편공통Table 저장위치 등록 실패" , "SL");
		        	}else if ("S".equals(inputStlMorS)) {
		        		intRtnVal = this.updY1YdStock(jrParam,  0);
		        		if(intRtnVal<0) slabUtils.printLog(logId, "["+prevStlNo+"] 슬라브공통Table 저장위치 등록 실패" , "SL");
		         	}
		        	
				}//if(YdConstant.RETN_CD_FAILURE.equals(szRtnMsg)) else 
				
				//외판출고실적 있을시 삭제
				slabUtils.printLog(logId, "["+inputStlNo+"] 외판출고 실적 있을시 삭제 " , "SL");
				
				jrResultSet = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkHisttoPort", logId, szMethodName, "외판출고 이력 select");
				if(jrResultSet != null && jrResultSet.size() >0) {
					slabUtils.printLog(logId, "["+inputStlNo+"] 외판출고 실적 존재하여 삭제 " , "SL");
					slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.deleteWorkPorthist", logId, szMethodName, "외판출고실적 삭제");
				}
				
				/*
				 * 이적/상차/하차 에 따라 분기처리
				 * 
				 * 이적 - 작업이력 추가
				 * 상차 - 작업이력 추가 및 TB_YD_CARFTMVMTL 상차재료 등록
				 * 하차 - 작업이력 추가 및 TB_YD_CARFTMVMTL 부분하차처리
				 * 
				 * 
				 * */
				
				if("updPosfix".equals(actionCode)){
					slabUtils.printLog(logId, "["+inputStlNo+"] 이적 작업이력 추가" , "SL");
					
					
					//작업이력 추가하기 위해 재료 정보 select
					jrResultSet = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStock_PIDEV", logId, szMethodName, "재료정보 select");
					if(jrResultSet != null && jrResultSet.size() >0) {
						jrResultSet.first();
						jrParam= jrResultSet.getRecord();
					}
					jrParam.setField("STL_NO", inputStlNo);
					jrParam.setField("MODIFIER", modifier);
					//입력재료 이전 적치정보 존재시, 권상정보 입력.
					if(!"".equals(inputStlPrevYdStkColGp)){
						jrParam.setField("YD_UP_WR_LOC"	  , inputStlPrevYdStkColGp+inputStlPrevYdStkBedNo);
						jrParam.setField("YD_UP_WR_LAYER" , inputStlPrevYdStkLyrNo );
						jrParam.setField("YD_UP_CMPL_DT"  , YdUtils.getCurDate("yyyyMMddHHmmss"));
					}
					
					jrParam.setField("YD_GP"			  , inputYdStkColGp.substring(0,1));
					jrParam.setField("YD_DN_WR_LOC"		  , inputYdStkColGp+inputYdStkBedNo);
					jrParam.setField("YD_DN_WR_LAYER"	  , inputYdStkLyrNo);
					jrParam.setField("YD_DN_CMPL_DT"	  , YdUtils.getCurDate("yyyyMMddHHmmss"));
					jrParam.setField("YD_SCH_CD"		  , inputYdStkColGp.substring(0, 2) +"YD" + "01" +"MM");
					jrParam.setField("YD_AID_WRK_YN" , "N");
					jrParam.setField("YD_EQP_ID" , ydEqpId); 
					jrParam.setField("YD_GNT_GP", YdConstant.YD_GNT_GP_MVSTK);
					jrParam.setField("YD_SCH_ST_GP", "B");
					
					slabYdCommDao.insert(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.insYdWrkHistDaoPosFix", logId, szMethodName, "작업이력 insert");
				}
				else if("updCarLoad".equals(actionCode)){
					slabUtils.printLog(logId, "["+inputStlNo+"] 상차 작업재료 INSERT/UPDATE" , "SL");
					
					jrParam.setField("YD_CAR_SCH_ID", 	ydCarSchId);
					jrParam.setField("DEL_YN"		, 	"N");

					jrResultSet = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getYdCarftmvmtl", logId, szMethodName, "차량작업재료select");
				
					//입력재료가 이미 상차된 재료일시 UPDATE 처리
					if(jrResultSet != null && jrResultSet.size() >0) {
						slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 차량작업재료["+inputStlNo+"] BedNo["+inputYdStkBedNo+"] LyrNo["+inputYdStkLyrNo+"] 변경 " , "SL");
						slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.updYdCarftmvmtl", logId, szMethodName, "차량이송재료 변경처리");
					}
					else {
						jrParam.setField("REGISTER"		, 	modifier);
						slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 차량작업재료["+inputStlNo+"] BedNo["+inputYdStkBedNo+"] LyrNo["+inputYdStkLyrNo+"] 삽입 " , "SL");
						slabYdCommDao.insert(jrParam, "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.insYdCarftmvmtl", logId, szMethodName, "차량이송재료 삽입처리");
					}
				}
				else if("updCarUnLoad".equals(actionCode)){
					slabUtils.printLog(logId, "["+inputStlNo+"] 하차 작업재료 UPDATE" , "SL");
					
					jrParam.setField("YD_CAR_SCH_ID", 	ydCarSchId);
					jrResultSet = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getYdCarftmvmtl", logId, szMethodName, "차량작업재료select");
				
					if(jrResultSet != null && jrResultSet.size() >0) {
						jrParam.setField("YD_CAR_UPP_LOC_CD", 	"*");
						slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 차량작업재료["+inputStlNo+"] 저장위치 변경 표시 처리" , "SL");
						slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.updYdCarftmvmtlUppLocCd", logId, szMethodName, "차량이송재료 저장위치변경 여부 표시");
					}
				
				}
				
				
				
				//슬라브 종합 이송 모니터링용, 통합야드 장비 현재동 수정(권하위치 동 기준)
				slabUtils.printLog(logId, "["+ydEqpId+"] 현재동 수정" , "SL");

	        	String curBay = inputYdStkColGp.substring(1, 2);	        	
	        	jrParam.setField("YD_CURR_BAY_GP",         curBay);
	        	jrParam.setField("YD_EQP_ID"	,         ydEqpId);
	        	slabYdCommDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updSlabYdEqpCurrBay", logId, szMethodName, "장비 현재동 update");
	        	
	        	
	        	//이송완료 실적처리
	        	szRtnMsg =  yddatautil.sendYDPRJ003(inputStlNo);
				
	        	//적치베드에 적치단 정보  Z 축 갱신
	        	jrParam = JDTORecordFactory.getInstance().create();
	        	jrParam.setField("YD_STK_COL_GP", inputYdStkColGp);
	        	jrParam.setField("YD_STK_BED_NO", inputYdStkBedNo);

				intRtnVal = this.updStkBedZPosFix(jrParam);
			}//for(int i=0; i<inRecord.length ; i++)
						

			slabUtils.printLog(logId,"[Jsp Session : "+szMethodName+"] 메소드 끝", "S-");

			return szRtnMsg;
			
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	} // end of updSlabYdStkPosFixTotNew	
	/**
	 * 슬라브장비작업 상차완료처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException
	 */
	public String complCarLdLotNew(JDTORecord inRecord,String logId) throws DAOException {

		String szMsg        			= "";
		String szMethodName 			= "complCarLdLotNew";
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		JDTORecordSet jrResultSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet jrResultSet2 = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord jrResult =  JDTORecordFactory.getInstance().create();
		JDTORecord  jrParam         = JDTORecordFactory.getInstance().create();
		
		//진행관리 - 주편공통DAO
		PtMSlabCommDao ptMSlabCommDao 		= new PtMSlabCommDao();
		//진행관리 - 슬라브공통DAO
		PtSlabCommDao ptSlabCommDao 		= new PtSlabCommDao();
		
		YdDelegate ydDelegate = new YdDelegate();
		
		CraneUdHdSeEJBBean craneUdHdSeEJBBean = new CraneUdHdSeEJBBean();
		int intRtnVal = 0;
		
		try {
			
			slabUtils.printLog(logId,"[Jsp Session : "+szMethodName+"] 메소드 시작", "S+");
			
			String ydCarSchId 	= inRecord.getFieldString("YD_CAR_SCH_ID");
			String ydPrepSchId  = inRecord.getFieldString("YD_PREP_SCH_ID");
			String ydEqpId 		= inRecord.getFieldString("YD_EQP_ID");
			String modifier 	= inRecord.getFieldString("MODIFIER");
			String arrWlocCd 	= inRecord.getFieldString("ARR_WLOC_CD");
			
			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
			
			jrResultSet = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarsch", logId, szMethodName, "차량스케줄 정보 select");
			
			if(jrResultSet != null && jrResultSet.size() >0) {
				//차량스케줄 정보 SET
				jrResultSet.absolute(1);
	        	jrResult = jrResultSet.getRecord();
	        	
	        	//String arrWlocCd      = jrResult.getFieldString("ARR_WLOC_CD");
	        	String ydCarUseGp	  = jrResult.getFieldString("YD_CAR_USE_GP");

	        	jrResult.setField("YD_CAR_PROG_STAT",		"5"); //상차완료
	        	jrResult.setField("ARR_WLOC_CD"		,	arrWlocCd );
	        	jrResult.setField("YD_EQP_WRK_STAT"	,		"L");
	        	jrResult.setField("YD_CAR_SCH_ID"	,	ydCarSchId);
	        	jrResult.setField("MODIFIER"		,	modifier);
	        	jrResult.setField("YD_CARLD_CMPL_DT", 	YdUtils.getCurDate("yyyyMMddHHmmss"));
	        	
	        	slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 착지개소코드 ["+arrWlocCd+"] 상차완료처리" , "SL");
	        	
	        	slabYdCommDao.update(jrResult, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarsch", logId, szMethodName, "차량스케줄 UPDATE");
	        	
	        	if(ydCarUseGp.equals("G")){
	    			//--------------------------------------------------------------
	    			//상차작업개시 송신 YDDMR009 (외판슬라브출하상차개시)
	    			//--------------------------------------------------------------
	        		slabUtils.printLog(logId, "상차작업개시 송신 YDDMR009 (슬라브장비작업 상차완료처리모바일) 송신 시작" , "SL");
	        		jrResult = JDTORecordFactory.getInstance().create();
//PIDEV
//	        		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szMethodName, "APPPI0", "S", "*");

//	        		if("Y".equals(sApplyYnPI)) {
	        			jrResult.setField("MQ_TC_CD",      "M10YDLMJ1073");
//	        		} else {
//	        			jrResult.setField("MSG_ID",        "YDDMR009");
//	        		}
					jrResult.setField("YD_CAR_SCH_ID", ydCarSchId);
					jrResult.setField("YD_GP",         "S");

					ydDelegate.sendMsg(jrResult);

	    			//--------------------------------------------------------------
	    			//일품 상차실적 송신 YDDMR013 (외판슬라브일품출하상차실적)
	    			//--------------------------------------------------------------
					slabUtils.printLog(logId, "일품 상차실적 송신 YDDMR013 (외판슬라브일품출하상차실적)" , "SL");
					jrResult = JDTORecordFactory.getInstance().create();
//PIDEV	        		
//					if("Y".equals(sApplyYnPI)) {				
	        			jrResult.setField("MQ_TC_CD",        "M10YDLMJ1083");
//	        		} else {
//	        			jrResult.setField("MSG_ID",        "YDDMR013");
//	        		}
					jrResult.setField("YD_CAR_SCH_ID", ydCarSchId);
					jrResult.setField("YD_GP",         "S");

					ydDelegate.sendMsg(jrResult);

					//--------------------------------------------------------------
	    			//상차작업완료 송신 YDDMR017 (외판슬라브출하상차완료)
					//--------------------------------------------------------------
					slabUtils.printLog(logId, "외판슬라브출하상차완료 송신 : YDDMR017" , "SL");
					jrResult = JDTORecordFactory.getInstance().create();
//PIDEV
//					if("Y".equals(sApplyYnPI)) {				
	        			jrResult.setField("MQ_TC_CD",        "M10YDLMJ1093");
//	        		} else {
//	        			jrResult.setField("MSG_ID",        "YDDMR017");
//	        		}
					jrResult.setField("YD_CAR_SCH_ID", ydCarSchId);
					jrResult.setField("YD_GP",         "S");

					ydDelegate.sendMsg(jrResult);

	    		}else{

	    			slabUtils.printLog(logId, "구내운송 상차완료 송신 : YDTSJ008" , "SL");
	    			jrResult = JDTORecordFactory.getInstance().create();
	    			jrResult.setField("MSG_ID",        "YDTSJ008");
	    			jrResult.setField("ARR_WLOC_CD"		, arrWlocCd); //착지개소코드
	    			jrResult.setField("YD_CAR_SCH_ID", ydCarSchId);

	    			ydDelegate.sendMsg(jrResult);
					

	    			slabUtils.printLog(logId, " 상차완료시 공통테이블 업데이트 처리 시작" , "SL");
					
	    			
					intRtnVal = YdCommonUtils.procCarLoadCmpl(ydCarSchId);
					if( intRtnVal <= 0 ) slabUtils.printLog(logId, "상차완료시 공통테이블 업데이트 처리 실패" , "SL");
					else slabUtils.printLog(logId, "상차완료시 공통테이블 업데이트 처리 성공" , "SL");

					//--------------- 상차완료시 공통테이블 업데이트 처리 끝 ----------------------


					//준비스케줄 삭제처리
					String szReturnMsg = YdCommonUtils.deletePreSch(ydPrepSchId, "", szMethodName);

	    		}
	        	
	        	slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 상차완료 이력테이블 등록" , "SL");
				
				jrResult.setField("YD_EQP_ID"       ,ydEqpId);
				jrResult.setField("YD_CAR_SCH_ID"   ,ydCarSchId);
				jrResult.setField("YD_PREP_SCH_ID"	,ydPrepSchId);
				slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 작업장비["+ydEqpId+"] LOT_ID["+ydPrepSchId+"] 상차완료 이력테이블 등록" , "SL");
				slabYdCommDao.insert(jrResult, "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdWrkHistbackup", logId, szMethodName, "상차완료 이력테이블 등록");

			}
			
			
			slabUtils.printLog(logId,"[Jsp Session : "+szMethodName+"] 메소드 끝", "S-");
			
			return szRtnMsg;
			
		}catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}// end of complCarLdLotNew
	/**
	 * 슬라브장비작업 하차완료처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException
	 */
	public String complCarUdLotNew(JDTORecord inRecord,String logId) throws DAOException {

		String szMsg        			= "";
		String szMethodName 			= "complCarUdLotNew";
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		JDTORecordSet jrResultSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord jrResult =  JDTORecordFactory.getInstance().create();
		JDTORecord  jrParam         = JDTORecordFactory.getInstance().create();
		

		YdDelegate ydDelegate = new YdDelegate();

		int intRtnVal = 0;
		
		try {
			
			slabUtils.printLog(logId,"[Jsp Session : "+szMethodName+"] 메소드 시작", "S+");
			
			String ydCarSchId 	= inRecord.getFieldString("YD_CAR_SCH_ID");
			String ydEqpId 		= inRecord.getFieldString("YD_EQP_ID");
			String modifier 	= inRecord.getFieldString("MODIFIER");
			
			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
			
			jrResultSet = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarsch", logId, szMethodName, "차량스케줄 정보 select");
			
			if(jrResultSet != null && jrResultSet.size() >0) {
				//차량스케줄 정보 SET
				jrResultSet.absolute(1);
	        	jrResult = jrResultSet.getRecord();
	        	
	        	String ydCarUdStopLoc = jrResult.getFieldString("YD_CARUD_STOP_LOC");
	        	String arrWlocCd      = jrResult.getFieldString("ARR_WLOC_CD");
	        	String ydCarUseGp	  = jrResult.getFieldString("YD_CAR_USE_GP");
	        	String ydBay 		  = ydCarUdStopLoc.substring(1,2) ; //동 구분
	        	
	        	
	        	jrParam.setField("YD_BAY_GP", ydBay);
				JDTORecordSet jsWlocCd = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getSlabTotYdWlocCdByBay", logId, szMethodName, "인터락설정ID Select");
				
				if(jsWlocCd != null && jsWlocCd.size() >0) {
					arrWlocCd = jsWlocCd.getRecord(0).getFieldString("WLOC_CD");
					
					slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 검색된 개소코드 ["+arrWlocCd+"]" , "SL");
					
					if(arrWlocCd == null || "".equals(arrWlocCd)) arrWlocCd = "DJY25"; //값이 없는경우 통합야드 기본 개소코드
				}
				else arrWlocCd = "DJY25";
	        	
	        	/*if("U".equals(ydBay) || "V".equals(ydBay) || "W".equals(ydBay)) arrWlocCd = "DYY15";
				else if("X".equals(ydBay)) arrWlocCd = "BSY01";
				else if("Y".equals(ydBay)) arrWlocCd = "BSY02";
				else if("Z".equals(ydBay)) arrWlocCd = "BSY03";*/
				
	        	jrResult.setField("ARR_WLOC_CD"		, 	arrWlocCd);

	        	jrResultSet = JDTORecordFactory.getInstance().createRecordSet("YD");  	
	        	jrResultSet = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdCarSchhackaCheck", logId, szMethodName, "마지막하차완료유무CHECK");
	        	
	        	//하차대상재가 남아있는경우
				if(jrResultSet != null && jrResultSet.size() >0) {
					jrResult.setField("YD_CAR_PROG_STAT", 	"D");
					jrResult.setField("MODIFIER", 	modifier);
					jrResult.setField("YD_CARUD_ST_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
					
					slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 하차개시 처리" , "SL");
					slabYdCommDao.update(jrResult, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarsch", logId, szMethodName, "차량스케줄 UPDATE");
				
					//출하차량 아닌 경우 구내운송 하차개시 송신
					if(!"G".equals(ydCarUseGp)){
						//--------------------------------------------------------------
						//	구내운송으로 하차작업개시완료 송신 YDTSJ009 (구내운송 하차개시)
						//--------------------------------------------------------------
						jrParam = JDTORecordFactory.getInstance().create();
						jrParam.setField("MSG_ID",        "YDTSJ009");
						jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
						jrParam.setField("YD_CARUD_ST_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
		
						ydDelegate.sendMsg(jrParam);
		
						szMsg="하차작업개시 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					
					slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 부분하차 공통테이블 업데이트" , "SL");
				
					slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 부분하차 공통테이블 업데이트" , "SL");
					intRtnVal = this.procCarUnLoadCmpl2(ydCarSchId,modifier);
					
					if( intRtnVal <= 0 ) slabUtils.printLog(logId, "부분하차시 공통테이블 업데이트 처리 실패" , "SL");
					else slabUtils.printLog(logId, "부분하차시 공통테이블 업데이트 처리 성공" , "SL");
				
					
					slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 부분하차 이력테이블 등록" , "SL");
					jrResultSet = JDTORecordFactory.getInstance().createRecordSet("YD");  	
		        	jrResultSet = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getYdCarftmvmtlUppLocCd", logId, szMethodName, "차량이송재료조회");
		        	
		        	if(jrResultSet != null && jrResultSet.size() >0) {
		        		for(int i=0; i<jrResultSet.size() ; i++){
			        		jrResultSet.absolute(i);
							jrResult = jrResultSet.getRecord();
							
							jrResult.setField("YD_EQP_ID",             ydEqpId);
							jrResult.setField("YD_CAR_SCH_ID",         ydCarSchId);
							slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 작업장비["+ydEqpId+"] 부분하차 이력테이블 등록" , "SL");
							slabYdCommDao.insert(jrResult, "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdWrkHistCarMvMtl", logId, szMethodName, "부분하차 이력테이블 등록");
		        		}
		        		
		        	}
				
				}
				//부분하차 아닌 경우
				else{
					jrResult.setField("MODIFIER", 	modifier);
					jrResult.setField("YD_CAR_PROG_STAT", 	"E");
					jrResult.setField("YD_CARUD_ST_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
					jrResult.setField("YD_CARUD_CMPL_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
					slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 하차완료 처리" , "SL");
					slabYdCommDao.update(jrResult, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarsch", logId, szMethodName, "차량스케줄 UPDATE");
				
					
					slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 회송스케줄일시 회송테이블 이력 처리" , "SL");
					jrParam = JDTORecordFactory.getInstance().create();
					SlabYdL2RcvDAO rcv2Dao = new SlabYdL2RcvDAO();
					jrParam.setField("V_MODIFIER"         , modifier); 
					jrParam.setField("V_YD_CAR_SCH_ID"    , ydCarSchId); 
					intRtnVal = rcv2Dao.updY1YDL009("RethtHist", jrParam);
					
					if(intRtnVal <=0) slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 회송 X" , "SL");
					else  slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 대한 회송완료처리 건수: " + intRtnVal+" 건" , "SL");
					
					
					//출하차량 아닌 경우 구내운송 하차개시 송신
					if(!"G".equals(ydCarUseGp)){
						//--------------------------------------------------------------
						//	구내운송으로 하차작업개시완료 송신 YDTSJ009 (구내운송 하차개시)
						//--------------------------------------------------------------
						slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 대한 구내운송 하차개시 송신" , "SL");
						jrParam = JDTORecordFactory.getInstance().create();
						jrParam.setField("MSG_ID",        "YDTSJ009");
						jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
						jrParam.setField("YD_CARUD_ST_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
		
						ydDelegate.sendMsg(jrParam);
		
						
						slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 대한 구내운송 하차완료 송신" , "SL");
						jrParam = JDTORecordFactory.getInstance().create();
						jrParam.setField("MSG_ID",        "YDTSJ010");
						jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
						jrParam.setField("YD_CARUD_ST_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
		
						ydDelegate.sendMsg(jrParam);
					}
					
					slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 하차완료 공통테이블 업데이트" , "SL");
					intRtnVal = this.procCarUnLoadCmpl2(ydCarSchId,modifier);
					
					if( intRtnVal <= 0 ) slabUtils.printLog(logId, "하차완료시 공통테이블 업데이트 처리 실패" , "SL");
					else slabUtils.printLog(logId, "하차완료시 공통테이블 업데이트 처리 성공" , "SL");
				
					slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 하차완료 이력테이블 등록" , "SL");
					
					jrResult.setField("YD_EQP_ID",             ydEqpId);
					jrResult.setField("YD_CAR_SCH_ID",         ydCarSchId);
					slabUtils.printLog(logId, "차량스케줄["+ydCarSchId+"] 작업장비["+ydEqpId+"] 하차완료 이력테이블 등록" , "SL");
					slabYdCommDao.insert(jrResult, "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdWrkHistbackup", logId, szMethodName, "하차완료 이력테이블 등록");
					
					
				}
	        		        	

			}
			
			
			slabUtils.printLog(logId,"[Jsp Session : "+szMethodName+"] 메소드 끝", "S-");
			
			return szRtnMsg;
			
		}catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}// end of complCarUdLotNew

	
	
	/**
	 * 차량작업관리화면 상차완료처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String complCarLdLot2_PIDEV(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara        = JDTORecordFactory.getInstance().create();
		JDTORecord recInTemp			= JDTORecordFactory.getInstance().create();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

		String szMsg        			= "";
		String szMethodName 			= "complCarLdLot2";
		String szOperationName 			= "상차완료처리(PI)";
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
		
		ydUtils.putLog(szSessionName, szMethodName, "SlabJspSeEJBBean.complCarLdLot2_PIDEV 시작", YdConstant.DEBUG);

		YdDelegate ydDelegate = new YdDelegate();

		String szYD_CAR_SCH_ID 	= null;
		String szARR_WLOC_CD   	="";
		String szYD_EQP_ID  	="";
		String szYD_CAR_USE_GP  	="";
		String szYD_PREP_SCH_ID  	="";
		String szMODIFIER  	="";
		int intRtnVal =  0;
		try {

			szYD_CAR_SCH_ID				= ydDaoUtils.paraRecChkNull(inDto, "YD_CAR_SCH_ID");
			szARR_WLOC_CD				= ydDaoUtils.paraRecChkNull(inDto, "ARR_WLOC_CD");
			szYD_EQP_ID					= ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_ID");
			szYD_CAR_USE_GP				= ydDaoUtils.paraRecChkNull(inDto, "YD_CAR_USE_GP");
			szYD_PREP_SCH_ID			= ydDaoUtils.paraRecChkNull(inDto, "LOT_ID");
			szMODIFIER				= ydDaoUtils.paraRecChkNull(inDto, "MODIFIER");

			if(szYD_CAR_SCH_ID.equals("") && szARR_WLOC_CD.equals("")&& szYD_CAR_USE_GP.equals("")){
				return YdConstant.RETN_CD_FAILURE ;
			}
			//--------------------------------------------------------------
			//	차량정보 상차완료 처리
			//--------------------------------------------------------------
			JDTORecord setRecord = JDTORecordFactory.getInstance().create();
			setRecord.setField("YD_CAR_PROG_STAT",		"5"); //상차완료
			if(!szARR_WLOC_CD.equals("")){
				setRecord.setField("ARR_WLOC_CD",	szARR_WLOC_CD );
			}
			setRecord.setField("YD_EQP_WRK_STAT",		"L");
			setRecord.setField("YD_CAR_SCH_ID",	szYD_CAR_SCH_ID);
			setRecord.setField("YD_CARLD_CMPL_DT", 	ydUtils.getCurDate("yyyyMMddHHmmss"));
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarsch*/
			intRtnVal = ydCarSchDao.updYdCarsch(setRecord, 0);
			if(intRtnVal <= 0) {
				szMsg="차량스케줄에 상차 완료 등록시 Error!! Code : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}


    		if(szYD_CAR_USE_GP.equals("G")){
    			//--------------------------------------------------------------
    			//상차작업개시 송신 YDDMR009 (외판슬라브출하상차개시)
    			//--------------------------------------------------------------
    			szMsg="[권상실적처리]상차작업개시 송신 YDDMR009 (외판슬라브출하상차개시) 송신 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				setRecord = JDTORecordFactory.getInstance().create();
//				setRecord.setField("MSG_ID",        "YDDMR009");
			  	setRecord.setField("MQ_TC_CD"      , "M10YDLMJ1073");
				setRecord.setField("YD_CAR_SCH_ID" , szYD_CAR_SCH_ID);
				setRecord.setField("YD_GP"         , "S");

				ydDelegate.sendMsg(setRecord);

    			//--------------------------------------------------------------
    			//일품 상차실적 송신 YDDMR013 (외판슬라브일품출하상차실적)
    			//--------------------------------------------------------------
				szMsg="일품 상차실적 송신 YDDMR013 (외판슬라브일품출하상차실적)";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				setRecord = JDTORecordFactory.getInstance().create();
				//setRecord.setField("MSG_ID",        "YDDMR013");
				setRecord.setField("MQ_TC_CD"      , "M10YDLMJ1083");
				setRecord.setField("YD_CAR_SCH_ID" , szYD_CAR_SCH_ID);
				setRecord.setField("YD_GP"         , "S");

				ydDelegate.sendMsg(setRecord);

				//--------------------------------------------------------------
    			//상차작업완료 송신 YDDMR017 (외판슬라브출하상차완료)
				//--------------------------------------------------------------

				szMsg="외판슬라브출하상차완료 송신 : YDDMR017";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				setRecord = JDTORecordFactory.getInstance().create();
//				setRecord.setField("MSG_ID",        "YDDMR017");
			  	setRecord.setField("MQ_TC_CD"      , "M10YDLMJ1093");
				setRecord.setField("YD_CAR_SCH_ID" , szYD_CAR_SCH_ID);
				setRecord.setField("YD_GP"         , "S");

				ydDelegate.sendMsg(setRecord);

    		}else{

				//--------------------------------------------------------------
				//	구내운송으로 상차작업개시완료 송신 YDTSJ007 (구내운송 상차개시)
				//--------------------------------------------------------------
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MSG_ID",        "YDTSJ007");
				recInTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD); //착지개소코드
				recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);

				//ydDelegate.sendMsg(recInTemp);
				
				//Thread.sleep(3000);		//구내운송 상차개시 TC 호출 후 자체 재조회 로직 시간 대기.(3초 여유)
				
				szMsg="상차작업개시 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


				//--------------------------------------------------------------
				//	구내운송으로 상차작업완료 송신 YDTSJ008 (구내운송 상차완료)
				//--------------------------------------------------------------
				//--------------- 상차완료시 공통테이블 업데이트 처리 시작 ----------------------
				intRtnVal = YdCommonUtils.procCarLoadCmpl(szYD_CAR_SCH_ID);
				if( intRtnVal <= 0 ) {
					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
					szMsg="상차완료시 공통테이블 업데이트 처리 실패";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					szMsg="상차완료시 공통테이블 업데이트 처리 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				//--------------- 상차완료시 공통테이블 업데이트 처리 끝 ----------------------

				JDTORecord inRec = JDTORecordFactory.getInstance().create();
				inRec.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);

				intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(inRec, 8);
				if(intRtnVal == 0 ){
					szMsg = "[Jsp Session  -  " + szOperationName +"] 완료시간 업데이트 할  작업이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}
				else{
					szMsg = "[Jsp Session  -  " + szOperationName +"] 상차완료시간 업데이트 하였습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}

				recPara.setField("MSG_ID",        "YDTSJ008");
				recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);

				ydDelegate.sendMsg(recPara);


				szMsg="[Jsp Session:"+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 구내운송으로 상차작업완료 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//-------------------------------------------------------------


				//준비스케줄 삭제처리
				String szReturnMsg = YdCommonUtils.deletePreSch(szYD_PREP_SCH_ID, "", szMethodName);

    		}


			/*
			 * 이력테이블등록호출
			 */
			{
				CrnSchSeEJBBean crnSchSeEJBBean = new CrnSchSeEJBBean();

				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_EQP_ID",             szYD_EQP_ID);
				recInTemp.setField("YD_CAR_SCH_ID",         szYD_CAR_SCH_ID);
				recInTemp.setField("YD_PREP_SCH_ID",        szYD_PREP_SCH_ID);

				// 이력테이블에 INSERT
				//com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdWrkHistbackup
				intRtnVal = ydWrkHistDao.insYdWrkHistS(recInTemp);

				if(intRtnVal<0) {
					szMsg = "(" + szYD_CAR_SCH_ID + ")에 대한 INSERT가 실패하였습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE ;
				}


				szMsg="[" + szOperationName + "] 이력테이블등록호출";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			/**********************************************************
			* 연주야드 적치 최적화 통합야드 출고 이력 추가
			**********************************************************/
			recInTemp.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID); //크레인스케줄ID
			recInTemp.setField("EQP_CD"			, "SS"); //설비코드 --통합야드
			recInTemp.setField("EQP_GP"   		, "SS"  ); //설비구분 --통합야드
			recInTemp.setField("MODIFIER" 		, szMODIFIER  ); //수정자
			
			slabYdCommDao.update(recInTemp, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdEqpIOHistByCarSch", logId, szMethodName, "통합야드 출고이력");
			

			ydUtils.putLog(szSessionName, szMethodName, "SlabJspSeEJBBean.complCarLdLot2_PIDEV 종료", YdConstant.DEBUG);

		}catch(JDTOException e) {
			szMsg="차량작업관리화면 상차완료 처리 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return szRtnMsg;
	}
	
	
	/**
	 * 기준Heat번호 조회 (별적/입회재 관리화면)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getDefaultHeatNo(GridData gdReq) throws DAOException {

		String      szMsg        = "";
		String      szMethodName = "getDefaultHeatNo";
		String 		szOperationName = "기준Heat번호 조회 (별적/입회재 관리화면)";
		
		try {
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			SlabYdJspDAO dao = new SlabYdJspDAO();
			JDTORecordSet jrs = null;
			jrs = dao.getDefaultHeatNo(gdReq);	
		
			//리턴할 GridData 생성
			GridData rtnGrd = new GridData();
			if(jrs != null) {
				//JDTORecordSet를 GridData로 변환
				rtnGrd = slabUtils.jdtoRecordToGridData(gdReq, jrs.toList());
			}
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return rtnGrd;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}
	
	/**
	 * 기준Heat번호로 앞뒤 Heat번호 조회 (별적/입회재 관리화면 Heat번호 콤보박스용)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getDefaultHeatNoForCombo(GridData gdReq) throws DAOException {

		String      szMsg        = "";
		String      szMethodName = "getDefaultHeatNoForCombo";
		String 		szOperationName = "기준Heat번호로 앞뒤 Heat번호 조회 (별적/입회재 관리화면 Heat번호 콤보박스용)";
		
		try {
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			SlabYdJspDAO dao = new SlabYdJspDAO();
			JDTORecordSet jrs = null;
			jrs = dao.getDefaultHeatNoForCombo(gdReq);	
		
			//리턴할 GridData 생성
			GridData rtnGrd = new GridData();
			if(jrs != null) {
				//JDTORecordSet를 GridData로 변환
				rtnGrd = slabUtils.jdtoRecordToGridData(gdReq, jrs.toList());
			}
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return rtnGrd;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}

	/**
	 * 별적/입회재 관리 시퀀스 Nextval 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSeparateStackSeq(JDTORecord recPara) throws DAOException { 

		String      szMsg        	= "";
		String      szMethodName 	= "getSeparateStackSeq";
		String 		szOperationName = "별적/입회재 관리 시퀀스 Nextval 조회";
		String 		jspeed_query_id = "";
		Object[]	sObj			= null;
		
		try {
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			SlabYdJspDAO dao = new SlabYdJspDAO();
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getSeparateStackSeq";
			sObj = new Object[]{
									};
			outRecSet = dao.getSeparateStackSeq(jspeed_query_id, sObj);	
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return outRecSet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}
	
	/**
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * [A] 오퍼레이션명 : 별적/입회재 요청등록
	 * [B] 처리 개요 : 재료번호별 INSERT
	 * @exception
	 * @modelguid XDE가 생성한 ID를 위치시킨다.
	 * @author  1524711
	 * @date 	2025.02.18
	 */
	public int insSeparateStackReq(JDTORecord[] gdReq) {
		
		String      szMsg        	= "";
		String      szMethodName 	= "insSeparateStackReq";
		String 		szOperationName = "별적/입회재 요청등록";
		String 		jspeed_query_id = "";
		
		Object[]			sObj				= null;	//조회 파라미터 보관 오브젝트 배열		

		int		cnt			= 0;
		
		try {
			SlabYdJspDAO dao = new SlabYdJspDAO();
			for(int ii=0; ii<gdReq.length; ii++){

					sObj = new Object[]{
							 CmnUtil.nvl(gdReq[ii].getFieldString("V_SEQ"),"")
							,CmnUtil.nvl(gdReq[ii].getFieldString("V_STL_NO"),"")
							,CmnUtil.nvl(gdReq[ii].getFieldString("V_REGISTER"),"")
							,CmnUtil.nvl(gdReq[ii].getFieldString("V_REQ_USER_ID"),"")
							,CmnUtil.nvl(gdReq[ii].getFieldString("V_REQ_DEPT"),"")
							,CmnUtil.nvl(gdReq[ii].getFieldString("V_REQ_USER_NAME"),"")
							,CmnUtil.nvl(gdReq[ii].getFieldString("V_SCARFING_CD"),"")
							,CmnUtil.nvl(gdReq[ii].getFieldString("V_REQ_REASON"),"")
							,CmnUtil.nvl(gdReq[ii].getFieldString("V_SEPARATE_PROG_GP"),"")
					};
					
					jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.insSeparateStackReq";
					cnt += dao.insSeparateStackReq(jspeed_query_id, sObj);

			}
			
			return cnt;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	/**
	 * 별적요청 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getSeparateReqList(GridData gdReq) throws DAOException {

		String      szMsg        = "";
		String      szMethodName = "getSeparateReqList";
		String 		szOperationName = "별적요청 조회";
		
		try {
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			SlabYdJspDAO dao = new SlabYdJspDAO();
			JDTORecordSet jrs = null;
			jrs = dao.getSeparateReqList(gdReq);	
		
			//리턴할 GridData 생성
			GridData rtnGrd = new GridData();
			if(jrs != null) {
				//JDTORecordSet를 GridData로 변환
				rtnGrd = slabUtils.jdtoRecordToGridData(gdReq, jrs.toList());
			}
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return rtnGrd;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}
	
	/**
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * [A] 오퍼레이션명 : 입회검사 요청/확정/완료처리
	 * [B] 처리 개요 : 유형별 쿼리 호출
	 * @exception
	 * @modelguid XDE가 생성한 ID를 위치시킨다.
	 * @author  1524711
	 * @date 	2025.02.18
	 */
	public int updInspectionState(JDTORecord[] gdReq) {
		
		String      szMsg        	= "";
		String      szMethodName 	= "updInspectionState";
		String 		szOperationName = "입회검사 요청/확정/완료처리";
		Object[]	sObj			= null;
		int result = 0;
		String jspeed_query_id = "";
		/* V_SEPARATE_PROG_GP
		-- 1 : 별적요청
		-- 2 : 별적완료
		-- 3 : 입회검사요청
		-- 4 : 입회검사확정
		-- 5 : 입회검사완료
		-- 0 : 별적요청취소
		*/
		try {
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			SlabYdJspDAO dao = new SlabYdJspDAO();
			
			for(int ii=0; ii<gdReq.length; ii++){
				
				if(gdReq[ii].getFieldString("V_SEQ") == null || gdReq[ii].getFieldString("V_SEQ").equals("")) {
					
					return result;
					
				} else {
					
					if(CmnUtil.nvl(gdReq[ii].getFieldString("V_SEPARATE_PROG_GP"),"").equals("1")) {
						if(CmnUtil.nvl(gdReq[ii].getFieldString("V_CANCEL_YN"),"").equals("Y")) {
							sObj = new Object[]{
									 CmnUtil.nvl(gdReq[ii].getFieldString("V_REQ_DEPT"),"")
									,CmnUtil.nvl(gdReq[ii].getFieldString("V_REQ_USER_ID"),"")
									,CmnUtil.nvl(gdReq[ii].getFieldString("V_REQ_USER_NAME"),"")
									,CmnUtil.nvl(gdReq[ii].getFieldString("V_MODIFIER"),"")
									,CmnUtil.nvl(gdReq[ii].getFieldString("V_SEQ"),"")
							};
							jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updInspectionStateSeparateReqCancel";	
						}
					
					} else if (CmnUtil.nvl(gdReq[ii].getFieldString("V_SEPARATE_PROG_GP"),"").equals("2")) {

						sObj = new Object[]{
								 CmnUtil.nvl(gdReq[ii].getFieldString("V_CANCEL_YN"),"N")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_SEPARATE_PROG_GP"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_MODIFIER"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_SEQ"),"")
						};
						jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updInspectionStateSEPARATE";
						
					} else if(CmnUtil.nvl(gdReq[ii].getFieldString("V_SEPARATE_PROG_GP"),"").equals("3")) {

						sObj = new Object[]{
								 CmnUtil.nvl(gdReq[ii].getFieldString("V_INSPECTION_REQ_DEPT"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_INSPECTION_REQ_USER_ID"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_INSPECTION_REQ_USER_NAME"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_CANCEL_YN"),"N")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_SEPARATE_PROG_GP"),"3")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_MODIFIER"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_SEQ"),"")
						};
						jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updInspectionStateREQ";

					} else if (CmnUtil.nvl(gdReq[ii].getFieldString("V_SEPARATE_PROG_GP"),"").equals("4")) {

						sObj = new Object[]{
								 CmnUtil.nvl(gdReq[ii].getFieldString("V_INSPECTION_PLAN_DDTT"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_INSPECTION_PLAN_DEPT"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_INSPECTION_PLAN_USER_ID"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_INSPECTION_PLAN_USER_NAME"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_INSPECTION_PLAN_LOCATION"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_CANCEL_YN"),"N")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_SEPARATE_PROG_GP"),"4")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_MODIFIER"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_SEQ"),"")
						};
						jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updInspectionStatePLAN";


					} else if (CmnUtil.nvl(gdReq[ii].getFieldString("V_SEPARATE_PROG_GP"),"").equals("5")){

						sObj = new Object[]{

								 CmnUtil.nvl(gdReq[ii].getFieldString("V_INSPECTION_DONE_DDTT"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_INSPECTION_DEPT"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_INSPECTION_USER_ID"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_INSPECTION_USER_NAME"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_CANCEL_YN"),"N")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_SEPARATE_PROG_GP"),"5")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_MODIFIER"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_SEQ"),"")
						};
						jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updInspectionStateDONE";

					} else {
						sObj = new Object[]{
								CmnUtil.nvl(gdReq[ii].getFieldString("V_SEPARATE_PROG_GP"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_MODIFIER"),"")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_DEL_YN"),"N")
								,CmnUtil.nvl(gdReq[ii].getFieldString("V_SEQ"),"")

						};
						jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updInspectionState";
					}		
					
				}
				
			}

			result = dao.updInspectionState(jspeed_query_id, sObj);
			szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return result;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}
	
	
	/**
	 * 
	 * [A] 오퍼레이션명 : 별적/입회재 관련 메일발송
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * @param 파라메터
	 * @return List
	 * @exception
	 * @modelguid XDE가 생성한 ID를 위치시킨다.
	 * 
	 */
	public GridData emailForSeparateStack(GridData grd) throws Exception
	{
		String      szMsg        	= "";
		String      szMethodName 	= "emailForSeparateStack";
		String 		szOperationName = "별적/입회재 관련 메일발송";
		
		String emailsToP = "";
		String namesToP = "";
		try {
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);			
			SlabYdJspDAO dao = new SlabYdJspDAO();

			//파라미터 사라짐을 막기위해 복사
			GridData gdReq = OperateGridData.cloneResponseGridData(grd);
			
			//메일내용
			JDTORecordSet jrs = dao.getEmailContentsForSeparateStack(grd);
			
			if(jrs != null) {
				jrs.first();
				JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp = jrs.getRecord();

				String emailFrom = StringHelper.evl(recInTemp.getFieldString("EMAILFROM"), ""); //보내는 사람 메일 주소  
				String emailFromName = StringHelper.evl(recInTemp.getFieldString("EMAILFROMNAME"), ""); //보내는 사람 이름
				String mailTitle = StringHelper.evl(recInTemp.getFieldString("MAILTITLE"), ""); //메일 제목
				String magHTML = StringHelper.evl(recInTemp.getFieldString("MAGHTML"), ""); //메일 내용 
				
				//수신자 정보
				JDTORecordSet jrs2 = dao.getEmailReceiverForSeparateStack(grd);
				
				for(int Loop_i = 1; Loop_i <= jrs2.size(); Loop_i++) {
					jrs2.absolute(Loop_i);
		    		recInTemp  = JDTORecordFactory.getInstance().create();
		    		recInTemp.setRecord(jrs2.getRecord());
		    		namesToP = namesToP + StringHelper.evl(recInTemp.getFieldString("CHARGER_NAME"), "") + ';'; //수신자명
		    		emailsToP = emailsToP + StringHelper.evl(recInTemp.getFieldString("ADDRESS1"), "") + ';'; //수신자 메일주소
				}
				
				szMsg = "JSP-SESSION ["+ szOperationName +"] 수신자명 : " + namesToP;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				szMsg = "JSP-SESSION ["+ szOperationName +"] 수신자 메일주소 : " + emailsToP;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				szMsg = "JSP-SESSION ["+ szOperationName +"] 제목 : " + mailTitle + ", 메일을 송신합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				JDTORecord paramR = JDTORecordFactory.getInstance().create();
				paramR.setField("SENDER_ADDR", emailFrom);
				paramR.setField("SENDER_NAME", emailFromName);
				paramR.setField("SUBJECT", mailTitle);
				paramR.setField("CONTENT", magHTML);
				paramR.setField("RECEVER_ADDR", emailsToP.split(";"));
				paramR.setField("RECEVER", namesToP.split(";"));
	
				try{
					ydUtils.sendMail(paramR);
				}catch(Exception e){
					throw new DAOException(getClass().getName() + e.getMessage(),e);
				}
			}else{
				szMsg = "JSP-SESSION ["+ szOperationName +"] 수신대상이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			}

			gdReq.addParam("Rtn", "1");			
			gdReq.addParam("rtnCode", 	"MSG0160");
			gdReq.addParam("rtnParam", "별적/입회재 관련 메일발송이 완료 되었습니다.");
			return gdReq;
			
		} catch (DAOException e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	
	}
	
}
