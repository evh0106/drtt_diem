package com.inisteel.cim.yd.jsp.slabjsp.session ;

import java.util.HashMap;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.record.JDTOException;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.batch.log.Logger;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jsp.common.CmnUtil;
import com.inisteel.cim.yd.jsp.common.YDComUtil;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.yd.jsp.common.YDRuleBean;
import flex.messaging.io.ArrayList;

import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.dao.ptStlFrtoMoveDao.PtStlFrtoMoveDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;


/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Facade Session EJB클래스입니다.
 *
 * @ejb.bean name="SlabJspFaEJB" jndi-name="SlabJspFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class SlabJspFaEJBBean extends BaseSessionBean {

	
	private YdUtils ydUtils = new YdUtils();	
	private YdSlabUtils slabUtils = new YdSlabUtils();

	YDComUtil   ydComUtil = new YDComUtil();
	YdDaoUtils  ydDaoUtils = new YdDaoUtils();
	YDDataUtil  ydDataUtil = new YDDataUtil();
	
	private String szSessionName = getClass().getName();

	YDRuleBean yDRuleBean = new YDRuleBean();
		

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
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getSelectData(GridData gdReq) throws DAOException {
		
		String szMethodName="getSelectData";		
		String szLogMsg = "";
		String szOperationName	= "단순 조회";
		
		EJBConnector ejbConn = null;
		
		try {

			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			gdRet.setStatus("true");
			gdRet.setMessage("Success");
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			//조회결과
			return gdRet;
			
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}	// end of getSelectData	

	
	
	/**
	 * 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSelectData(JDTORecord recPara) throws DAOException {
		
		String szMethodName="getSelectData";		
		String szLogMsg = "";
		String szOperationName	= "단순 조회";
		
		EJBConnector ejbConn = null;
		
		try {

			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			JDTORecordSet recordSet = (JDTORecordSet)ejbConn.trx("getSelectData", new Class[] { JDTORecord.class }, new Object[] { recPara });

			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			//조회결과
			return recordSet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
	}	// end of getSelectData
	
	
	
	/**
	 * 저장품 데이터를 조회/검색하는 메소드이다.(검색어, 업무영역코드)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getYdStock(GridData inDto) throws JDTOException {
		
		//LOG 
		
		String szMethodName="getYdStock";		
		String szLogMsg = "";
		String szOperationName	= "저장품 데이터 조회";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getYdStock",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		
		}catch(Exception e){			
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		

		return gdRes;
	}
	 
	
	/**
	 * 저장위치별 재고 List(연주야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getcSlabYdStkPosList(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getcSlabYdStkPosList";
		String szLogMsg = "";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "JSP-FACADE [저장위치별 재고 List] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getcSlabYdStkPosList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			szLogMsg = "JSP-FACADE [저장위치별 재고 List] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getPlateYdStkPosList
	
	
	
	/**
	 * 저장위치별 재고 List(후판슬라브야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getaPlateYdStkPosList(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getaPlateYdStkPosList";
		String szLogMsg = "";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "JSP-FACADE [저장위치별 재고 List] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getaPlateYdStkPosList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			szLogMsg = "JSP-FACADE [저장위치별 재고 List] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getaPlateYdStkPosList
	
	
	
	
	
	/**
	 * [A] 오퍼레이션명: 보류/해제 등록
	 * 
	 * @ejb.interface-method
	 * @param inParam
	 * @return
	 * @throws JDTOException
	 */
	public GridData updateStlHoldstat(GridData inParam) throws JDTOException {
		EJBConnector ejbConn = null;
		try {
	
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			return (GridData)ejbConn.trx("updateStlHoldstat", new Class[] { GridData.class }, new Object[] { inParam });
		} catch (Exception e) {
			throw new JDTOException(getClass().getName() + " :: " + e.getMessage(), e);
		} finally {
		}
	}
	
	/**
	 * [A] 오퍼레이션명: 후판슬라브 이상재 보류해제등록
	 * 
	 * @ejb.interface-method
	 * @param inParam
	 * @return
	 * @throws JDTOException
	 */
	public GridData updateStlHoldstatPa(GridData inParam) throws JDTOException {
		EJBConnector ejbConn = null;
		try {
	
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			return (GridData)ejbConn.trx("updateStlHoldstatPa", new Class[] { GridData.class }, new Object[] { inParam });
		} catch (Exception e) {
			throw new JDTOException(getClass().getName() + " :: " + e.getMessage(), e);
		} finally {
		}
	}
	
	
	/**
	 * 슬라브공통을 조회/검색하는 메소드이다.(검색어, 업무영역코드)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getPtSlabComm(GridData inDto) throws JDTOException {
		
		//LOG
		
		String szMethodName="getPtSlabComm";
		String szLogMsg = "";
		String szOperationName	= "슬라브공통 조회";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		
		try{
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getPtSlabComm",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });

			gdRes = CmUtil.genGridData(inDto , recordSet);
						
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		

		return gdRes;
	}
		
	/**
	 * 저장위치 조회  메소드.()
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData getSlabYdStkLocInfo(GridData inDto) throws JDTOException {
		//LOG
		
		String szMethodName="getSlabYdStkLocInfo";	
		String szLogMsg = "";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		String szOperationName	= "저장위치 조회";
		
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			recordSet = (JDTORecordSet) ejbConn.trx("getSlabYdStkLocInfo",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){			
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);	
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	/**
	 * 저장위치 좌표설정화면 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData getSlabYdStkPosSet(GridData inDto) throws JDTOException {
		
		String szMethodName="getSlabYdStkPosSet";
		String szLogMsg = "";
		String szOperationName	= "저장위치 좌표설정화면 조회";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
			
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdStkPosSet", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);			
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);	
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	/**
	 * 저장위치 좌표설정화면 베드조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabYdStkPosSetBed(GridData inDto) throws JDTOException {
		 
		String szMethodName="getSlabYdStkPosSetBed";	
		String szLogMsg = "";
		String szOperationName	= "저장위치 좌표설정화면 베드조회";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdStkPosSetBed", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);	
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	/**
	 * 저장위치 좌표설정화면 열 수정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData updSlabYdStkPosSet(GridData inDto) throws JDTOException {
	 
		String szMethodName="updSlabYdStkPosSet";
		String szLogMsg = "";
		String szOperationName	= "저장위치 좌표설정화면 열 수정 ";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
			
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			ejbConn.trx("updSlabYdStkPosSet",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);	
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	
	
	/**
	 * 차상위치 정보화면 수정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData updateCarLiftInfo_Coil(GridData inDto) throws JDTOException {
	 
		String szMethodName="updateCarLiftInfo_Coil";
		String szLogMsg = "";
		String szOperationName	= "차상위치 정보화면 수정";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
			
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			ejbConn.trx("updateCarLiftInfo_Coil",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);	
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	

	
	/**
	 * 차상위치 정보화면 삭제 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData deleteCarLiftInfo_Coil(GridData inDto) throws JDTOException {
	 
		String szMethodName="deleteCarLiftInfo_Coil";
		String szLogMsg  = "";
		String szOperationName	= "차상위치 정보화면 삭제";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
			
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			ejbConn.trx("deleteCarLiftInfo_Coil",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);	
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	
	
	/**
	 * 저장위치 좌표설정화면 열 등록 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData insSlabYdStkPosSet(GridData inDto) throws JDTOException {
	 
		String szMethodName="insSlabYdStkPosSet";	
		String szLogMsg = "";
		String szOperationName	= "저장위치 좌표설정화면 열 등록";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		
		try{
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("insSlabYdStkPosSet", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}		
		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	
	/**
	 * 저장위치 좌표설정화면 열 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData delSlabYdStkPosSet(GridData inDto) throws JDTOException {
		 
		String szMethodName="delSlabYdStkPosSet";	
		String szLogMsg = "";
		String szOperationName	= "저장위치 좌표설정화면 열 삭제";
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;		
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("insSlabYdStkPosSet",new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}		
		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} // end of delSlabYdStkPosSet	
	
	
	/**
	 * 저장위치 좌표설정화면 BED  등록 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData insSlabYdStkPosSetBed(GridData inDto) throws JDTOException {
	 
		String szMethodName="insSlabYdStkPosSetBed";
		String szLogMsg = "";
		String szOperationName	= "저장위치 좌표설정화면 BED  등록";
			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		
		try{
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("insSlabYdStkPosSetBed", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}				
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	/**
	 * 저장위치 좌표설정화면 BED 수정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData updSlabYdStkPosSetBed(GridData inDto) throws JDTOException {
		 
		String szMethodName="updSlabYdStkPosSetBed";
		String szLogMsg = "";
		String szOperationName	= "저장위치 좌표설정화면 BED 수정";
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			// JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			// 수정부는 ydComUtil 로 바꾸어야한다.왜냐하면 콤보박스의 정보가 없을경우 
			// 공통에서 Null Point Exception 발생(개발계 발생, 운영계는 발생X)
			
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			ejbConn.trx("updSlabYdStkPosSetBed",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	/**
	 *  크레인 작업관리 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getSlabYdCrnWorkMgt(GridData inDto) throws JDTOException {
	 
		String szMethodName="getSlabYdCrnWorkMgt";
		String szLogMsg = "";
		String szOperationName	= "크레인 작업관리 조회";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdCrnWorkMgt", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	
	
	/**
	 *  슬라브 야드 베드금지 / 해제 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	
	public GridData getSlabYdBedBanCnc(GridData inDto) throws JDTOException {
	 
		String szMethodName="getSlabYdBedBanCnc";		
		String szLogMsg = "";
		String szOperationName	= "슬라브 야드 베드금지 / 해제 조회 ";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdBedBanCnc1", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}	
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	

	/**
	 *  슬라브 야드 베드금지 / 해제 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updSlabYdBedBanCnc(GridData inDto) throws JDTOException {
		 
		String szMethodName="updSlabYdBedBanCnc";	
		String szLogMsg = "";
		String szOperationName	= "슬라브 야드 베드금지 / 해제";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("updSlabYdBedBanCnc", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	/**
	 *  슬라브 야드 크레인 설비내용 조회 - 대차설비조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getslabYdCrnStsSet(GridData inDto) throws JDTOException {
		//LOG
		String szLogMsg = null;
		String szRtnMsg = null;
		String szMethodName="getslabYdCrnStsSet";	
		String szOperationName	= "대차설비조회";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getslabYdCrnStsSetID", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			//조회성공메세지를 설정
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);
		}catch(JDTOException e) {
			//에러메시지를 설정
			gdRes.setMessage(e.getMessage());
		}catch(Exception e){
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			szLogMsg = "[JSP FACADE]조회시 에러가 발생하였습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			gdRes.setMessage(szRtnMsg);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	/**
	 *  슬라브 야드 크레인 설비내용 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	
	public GridData getslabYdCrnStsSetById(GridData inDto) throws JDTOException {
		//LOG
		String szLogMsg = null;
		String szRtnMsg = null;
		String szMethodName="getslabYdCrnStsSetById";	
		String szOperationName	= "슬라브 야드 크레인 설비내용 조회";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getslabYdCrnStsSetById", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			//조회성공메세지를 설정
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);
		}catch(JDTOException e) {
			//에러메시지를 설정
			gdRes.setMessage(e.getMessage());
		}catch(Exception e){
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 조회시 ERROR발생 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			gdRes.setMessage(szRtnMsg);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	
	/**
	 *  슬라브 야드 슬라브야드 차량진행관리 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getslabYdCarWorkList(GridData gdReq) throws JDTOException {
 
		String szMethodName="getslabYdCarWorkList";
		String szLogMsg = "";
		String szOperationName	= "차량진행관리 조회";
		
		
		GridData returnGrid = null;	
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		GridData gdRes = null;
		
		try{
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			returnGrid = OperateGridData.cloneResponseGridData(gdReq);			
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getslabYdCarWorkList", inRecord);				

			//공통 PM 에서 날짜를 처리하기 위하여  필요한 UTil
		     gdRes = CmnUtil.jdtoRecordToGridData(returnGrid, recordSet, gdReq);		     
		     

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	/**
	 *  설비 고장/정상 설정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updSlabYdCrnStsSetCrnStat(GridData inDto) throws JDTOException {
	 
		String szMethodName="updSlabYdCrnStsSetCrnStat";
		String[] szRtnMsg = null;
		String rtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szLogMsg = "";
		String szOperationName	= "설비 고장/정상 설정";
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			szRtnMsg = (String[])ejbConn.trx("updSlabYdCrnStsSetCrnStat", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			for(int Loop_i = 0; Loop_i < szRtnMsg.length; Loop_i++ ) {
				if( !szRtnMsg[Loop_i].equals(YdConstant.RETN_CD_SUCCESS) ) {
					rtnMsg = szRtnMsg[Loop_i];
					break;
				}
			}
				
			gdRes.setMessage(rtnMsg);
			
		}catch(Exception e){
			rtnMsg = YdConstant.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			ydUtils.putLog(szSessionName, szMethodName, "[JSP-FACADE  - "+ szOperationName  +"] 설비고장/정상 설정 에러발생 : " + e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;	
	}
	

	/**
	 *  설비 On-Line/Off-Line 설정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updSlabYdCrnStsSetCrnMode(GridData inDto) throws JDTOException {
		//LOG
		String[] szRtnMsg = null;
		String rtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szMethodName="updSlabYdCrnStsSetCrnMode";
		String szLogMsg = "";
		String szOperationName	= "설비 On-Line/Off-Line 설정";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			szRtnMsg = (String[])ejbConn.trx("updSlabYdCrnStsSetCrnMode", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			for(int Loop_i = 0; Loop_i < szRtnMsg.length; Loop_i++ ) {
				if( !szRtnMsg[Loop_i].equals(YdConstant.RETN_CD_SUCCESS) ) {
					rtnMsg = szRtnMsg[Loop_i];
					break;
				}
			}
				
			gdRes.setMessage(rtnMsg);
		}catch(Exception e){
			rtnMsg = YdConstant.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			ydUtils.putLog(szSessionName, szMethodName, "[JSP-FACADE  - "+ szOperationName  + "]" + e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;	
	}
	
	/**
	 *  슬라브 야드 대차 상태 수정(UPDATE) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updslabYdTcarStsSetStat(GridData inDto) throws JDTOException {
 
		String szMethodName="updslabYdTcarStsSetStat";
		String szLogMsg = "";
		String szOperationName	= "대차 상태 수정(UPDATE) ";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("updslabYdTcarStsSetStat", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
				
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	
	}
	

	/**
	 *  슬라브 야드 대차  운전모드 수정(UPDATE) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updslabYdTcarStsSetMode(GridData inDto) throws JDTOException {
	 
		String szMethodName="updslabYdTcarStsSetMode";
		String szLogMsg = "";
		String szOperationName	= "운전모드 수정(UPDATE)";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("updslabYdTcarStsSetMode", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
				
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	
	}
	
	
	/**
	 *  슬라브야드 대차 이동실적 BACKUP 처리 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updSlabYdCrnStsSetTcarMove(GridData inDto) throws JDTOException {
		//LOG
		String szLogMsg = null;
		String szRtnMsg = null;
		String szMethodName="updSlabYdCrnStsSetTcarMove";
		String szOperationName	= "이동실적 BACKUP 처리";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx( szMethodName , new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			gdRes.setMessage(szRtnMsg);
				
		}catch(Exception e){
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] +  에러발생 : " + e.getMessage();
			gdRes.setMessage(YdConstant.RETN_CD_FAILURE);
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;
	}

	/**
	 *  슬라브 야드 대차  출발지시 수정(UPDATE) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updSlabYdCrnStsSetTcarOrd(GridData inDto) throws JDTOException {
	 
		String szMethodName="updSlabYdCrnStsSetTcarOrd";
		String  szLogMsg ="";
		String szOperationName	= "출발지시 수정";
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx( szMethodName , new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
				
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		

		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}	
	
	/**
	 *  슬라브 야드 차량정지위치상태등록 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getslabYdCarStopLocStsReg(GridData inDto) throws JDTOException {
	 
		String szMethodName="getslabYdCarStopLocStsReg";
		String szLogMsg = "";
		String szOperationName	= "차량정지위치상태등록 조회";
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getslabYdCarStopLocStsReg", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	
	/**
	 *  슬라브 야드 차량정지상태 등록 수정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updslabYdCarStopLocStsReg(GridData inDto) throws JDTOException {
	 
		String szMethodName="updslabYdCarStopLocStsReg";
		String szLogMsg = "";
		String szOperationName	= "차량정지상태 등록 수정";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("updslabYdCarStopLocStsReg", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;
	
	}
		

	/**
	 *  슬라브 야드차량 상차정보 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	
	public GridData getslabYdCarLiftInfo(GridData inDto) throws JDTOException {
	 
		String szMethodName="getslabYdCarLiftInfo";
		String szLogMsg = "";
		String szOperationName	= "슬라브 야드차량 상차정보 조회";
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		
		
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getslabYdCarLiftInfo", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;
	}
	
	
	/**
	 *  슬라브 저장위치 별 정보 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData getslabYdStkLocInfoList(GridData inDto) throws JDTOException {
	 
		String szMethodName="getslabYdStkLocInfoList";
		String szLogMsg = "";
		String szOperationName	= "저장위치 별 정보 조회";
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getslabYdStkLocInfoList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getslabYdStkLocInfoList
	
	/**
	 * 슬라브야드 스케줄 기동관리 (조회)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getslabYdSchStirMgt(GridData inDto) throws JDTOException {
		 
		String szMethodName ="getslabYdSchStirMgt";
		String szLogMsg = "";
		String szOperationName	= "스케줄 기동관리 (조회)";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getslabYdSchStirMgt", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getslabYdSchStirMgt
	
	
	
	/**
	 * 슬라브야드 스케줄 기동관리 (수정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updslabYdSchStirMgt(GridData gdReq) throws JDTOException {
	 
		String szMethodName="getslabYdSchStirMgt";
		String szLogMsg = "";
		String szOperationName	= "스케줄 기동관리 (수정)";
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("updslabYdSchStirMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;
	}  //end of updslabYdSchStirMgt
	
	
	/**
	 * EVENT별 작업재료 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getslabYdEventWorkMatRef(GridData inDto) throws JDTOException {
	 
		String szMethodName="getslabYdEventWorkMatRef";
		String szLogMsg = "";
		String szOperationName	= "EVENT별 작업재료 조회";
			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getslabYdEventWorkMatRef", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getslabYdEventWorkMatRef
	
	/**                                                                                                       
	 * 옥외 Slab 야드 일일 장비 처리 현황 조회                                                              
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.                                               
	 * @param inDto                                                                                         
	 * @return GridData                                                                                     
	 * @throws JDTOException                                                                                
	 * @throws JDTOException                                                                                
	 */	                                                                                                    
	public GridData getOuthouseSlabYdEqpHdStatList(GridData inDto) throws JDTOException {                   
	                                                                                                        
		String szMethodName="getOuthouseSlabYdEqpHdStatList";                                                 
		String szLogMsg = "";                                                                                 
		String szOperationName	= "옥외 Slab 야드 일일 장비 처리 현황 조회";                                  
			                                                                                                    
		                                                                                                      
		GridData gdRes = null;                                                                                
		EJBConnector ejbConn = null;                                                                          
		JDTORecordSet recordSet = null;
		JDTORecordSet recordSet2 = null;	// 현재고 조회용
		try{                                                                                                  
			                                                                                                    
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";                                        
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);                             
			                                                                      
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);                                                  
			ejbConn = new EJBConnector("default", this);                                                        
			recordSet  = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getOuthouseSlabYdEqpHdStatList"	, inRecord);
			recordSet2 = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getOuthouseSlabYdSlabWtSum"		, inRecord);
			
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			// 적치단 현재고 합계중량
			if(recordSet2.size() > 0){
				gdRes.addParam("SLAB_WT_TOT", recordSet2.getRecord(0).getFieldString("SLAB_WT_TOT"));
			}
		}catch(Exception e){                                                                                  
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);                      
			throw new JDTOException(getClass().getName() + e.getMessage(), e);                                  
		}                                                                                                     
		gdRes.setStatus("true");                                                                              
		gdRes.setMessage("Success");                                                                          
		                                                                                                      
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";                                            
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);                               
		                                                                                                      
		return gdRes;                                                                                         
	} //end of getOuthouseSlabYdEqpHdStatList
	
	
	/**                                                                                                       
	 * 옥외 Slab 야드 일일 근무자 조회                                                              
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.                                               
	 * @param inDto                                                                                         
	 * @return GridData                                                                                     
	 * @throws JDTOException                                                                                
	 * @throws JDTOException                                                                                
	 */	                                                                                                    
	public GridData getOuthouseSlabYdWorkerList(GridData inDto) throws JDTOException {                   
	                                                                                                        
		String szMethodName="getOuthouseSlabYdWorkerList";                                                 
		String szLogMsg = "";                                                                                 
		String szOperationName	= "옥외 Slab 야드 일일 근무자 조회";                                  
		                                                       
		GridData gdRes = null;                                                                                
		EJBConnector ejbConn = null;                                                                          
		JDTORecordSet recordSet = null;                                                                       
		try{                                                                                                  
			                                                                                                    
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";                                        
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);                             
			                                                                      
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);                                                  
			ejbConn = new EJBConnector("default", this);                                                        
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getOuthouseSlabYdWorkerList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);                                                      
			                                                                                                    
		}catch(Exception e){                                                                                  
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);                      
			throw new JDTOException(getClass().getName() + e.getMessage(), e);                                  
		}                                                                                                     
		gdRes.setStatus("true");                                                                              
		gdRes.setMessage("Success");                                                                          
		                                                                                                      
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";                                            
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);                               
		                                                                                                      
		return gdRes;                                                                                         
	} //end of getOuthouseSlabYdWorkerList
	
	
	/**
	 * 옥외 Slab 야드 일일 근무자 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData setOuthouseSlabYdWorker(GridData gdReq) throws JDTOException {
	
		String szMethodName = "setOuthouseSlabYdWorker";
		String szLogMsg = "";
		String szOperationName	= "옥외 Slab 야드 일일 근무자 등록";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecord(gdReq);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("setOuthouseSlabYdWorker", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			 
		}catch(Exception e){
			throw new JDTOException(getClass().getName() + e.getMessage(),e);
			//ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
		}		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of setOuthouseSlabYdWorker
	
	
	/**
	 * 압연지시관리 (조회)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData getslabYdRollCmdRef(GridData inDto) throws JDTOException {
		 
		String szMethodName="getslabYdRollCmdRef";
		String szLogMsg = "";
		String szOperationName	= "압연지시관리 (조회)";
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getslabYdRollCmdRef", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			 
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getslabYdRollCmdRef
	
	
	/**
	 * 설비사양설정 (조회)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData getslabYdEqpSetSpec(GridData inDto) throws JDTOException {
	 
		String szMethodName="getslabYdEqpSetSpec";
		String szLogMsg = "";			
		String szOperationName	= "설비사양설정 (조회))";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);			
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getslabYdEqpSetSpec", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			 
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;
	} //end of getslabYdEqpSetSpec
	
	
	/**
	 * 설비사양설정 (수정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updslabYdEqpSetSpec(GridData gdReq) throws JDTOException {
	 

		String szMethodName = "updslabYdEqpSetSpec";
		String szLogMsg = "";
		String szOperationName	= "설비사양설정 (수정)";
		
					
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			JDTORecord [] inRecord = ydComUtil.genGridToJDTORecord(gdReq);
			
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("updslabYdEqpSetSpec",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			 
		}catch(Exception e){
			throw new JDTOException(getClass().getName() + e.getMessage(),e);
			//ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
		}		
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of updslabYdEqpSetSpec
	
	
	/**
	 * 설비사양설정 (삭제)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData delslabYdEqpSetSpec(GridData gdReq) throws JDTOException {
	
		String szMethodName = "delslabYdEqpSetSpec";
		String szLogMsg = "";
		String szOperationName	= "설비사양설정 (삭제)";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("delslabYdEqpSetSpec", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			 
		}catch(Exception e){
			throw new JDTOException(getClass().getName() + e.getMessage(),e);
			//ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
		}		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of delslabYdEqpSetSpec
	
	
	

	/**
	 * 설비사양설정 (등록)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData insslabYdEqpSetSpec(GridData gdReq) throws JDTOException {
		
		String szMethodName = "insslabYdEqpSetSpec";
		String szLogMsg = "";
		String szOperationName	= "설비사양설정 (등록)";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("insslabYdEqpSetSpec", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			 
		}catch(Exception e){
			throw new JDTOException(getClass().getName() + e.getMessage(),e);
			//ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
		}
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of insslabYdEqpSetSpec
	
	
	

	/**
	 * 수불구변경등록 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	
	public GridData getSlabYdBedBanCnc2(GridData inDto) throws JDTOException {

		String szMethodName = "getSlabYdBedBanCnc2";
		String szLogMsg = "";
		String szOperationName	= "수불구변경등록 조회";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdBedBanCnc2", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			 
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}	
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}

	/**
	 *  수불구변경등록 (수정) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updSlabYdBedBanCnc2(GridData inDto) throws JDTOException {

		
		
		String szMethodName ="updSlabYdBedBanCnc2";
		String szLogMsg = "";
		String szOperationName	= "수불구변경등록 (수정)";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("updSlabYdBedBanCnc2", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			 
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	/**
	 * 저장위치 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData getSlabYdStkPos(GridData inDto) throws JDTOException {
		
		String szMethodName="getSlabYdStkPos";
		String szLogMsg ="";
		String szOperationName	= "저장위치 조회";
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdStkPos", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			 
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}	
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;
	}
	
	
	/**
	 * 저장위치수정 재료조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabYdStkPosMtl(GridData inDto) throws JDTOException {
		 
		String szMethodName="getSlabYdStkPosSetBed";	
		String szLogMsg = "";
		String szOperationName	= "저장위치수정 재료조회";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("STL_NO", inDto.getParam("STL_NO"));
			
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdStkPosMtl", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);	
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	
	/**
	 *  슬라브[C연주,후판슬라브] 현재 저장위치 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData chkSlabYdStkPosSlab(GridData inDto) throws DAOException {
		//LOG
		String szMsg			= "";
		String szMethodName		= "chkSlabYdStkPosSlab";
		String szOperationName	= "저장위치 수정";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		
		String szLogMsg 	= "";
		String szTemp 		= "";
		String szRtnMsg 	= YdConstant.RETN_CD_SUCCESS;
		
		JDTORecordSet recordSet 	= null;
		
		int nRtnVal = 0;
		
		try{
			szLogMsg = "JSP-FACADE ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
			JDTORecord recTemp     = CmUtil.genJDTORecord(inDto);
			
			YdStkLyrDao ydStkLyrDao 		= new YdStkLyrDao();
			
			//전  적치 야드
			for (int nLoop = 0 ; nLoop < inRecord.length ; nLoop++){
				
				 recTemp = JDTORecordFactory.getInstance().create();
				 recTemp = inRecord[nLoop];
				 
				 szTemp = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
				 
				 if(szTemp.trim().equals("")){
					 continue;
				 }
					 
				//이동하려고 하는 위치에 현재 슬라브가 존재하는지 확인
				recordSet = JDTORecordFactory.getInstance().createRecordSet("Yd");
				nRtnVal = ydStkLyrDao.getYdStklyr(recTemp, recordSet, 109);
				
				if(nRtnVal > 0 ) {
					 szRtnMsg = "To위치에 현재 슬라브가 존재합니다.";
					 ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					 
					 //gdRes = OperateGridData.cloneResponseGridData(inDto);
					 //gdRes.setMessage(szRtnMsg);
					 inDto.setMessage(szRtnMsg);
					
					 return inDto;
				}
			}
				
			//gdRes = OperateGridData.cloneResponseGridData(inDto);
			inDto.setStatus("true");
			inDto.setMessage(szRtnMsg);
				 
		}catch(Exception e){		
			inDto.setStatus("true");
			inDto.setMessage(e.getMessage());
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
			
		szLogMsg = "JSP-FACADE ["+ szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return inDto;
	}
	
	
	
	/**
	 *  슬라브[C연주,후판슬라브] 저장위치 수정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updSlabYdStkPosFix(GridData inDto) throws DAOException {
		
		//LOG
		String szMsg			= "";
		String szMethodName		= "updSlabYdStkPosFix";
		String szOperationName	= "저장위치 수정";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		//진행관리 - 이송지시
		PtStlFrtoMoveDao ptStlFrtoMoveDao 	= new PtStlFrtoMoveDao();
		
		String szFromYd 	= "";
		String szTemp 		= "";
		String szTemp1 		= "";
		String szTemp2 		= "";
		String szToYd 		= "";
		String szAimToyd 	= "";
		String szLogMsg 	= "";
		String szRtnMsg 	= YdConstant.RETN_CD_SUCCESS;
		String sMTL_MOV_UPD_FLAG= "";
		JDTORecordSet recordSet 	= null;
		JDTORecordSet recordSet1 	= null;
		JDTORecordSet recordSet2 	= null;
		JDTORecordSet recordSet3 	= null;
		JDTORecordSet rsStock   	= null;
		
		JDTORecord recPara 			= null;
		JDTORecord logRecord 		= null;
		JDTORecord recDelPara 		= null;
		
		int nRtnVal = 0;
		
		try{
			
			szLogMsg = "JSP-FACADE ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			sMTL_MOV_UPD_FLAG	= StringHelper.evl(inDto.getParam("MTL_MOV_UPD_FLAG"), "N");	// 이송하차인경우	
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
			JDTORecord recTemp     = CmUtil.genJDTORecord(inDto);
			
			YdStkLyrDao ydStkLyrDao 		= new YdStkLyrDao();
			YdStockDao ydStockDao 			= new YdStockDao();
			YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
			
			/***********************************************************************************************
			 * TO 위치의 정보가 원래 정보의 저장위치 정보에서 목표야드 또는 현야드가 아닐경우는 ERROR RETURN 하는 로직추가
			 * 전 저장위치 정보가 없을경우는 무시함
			 * 
			 **********************************************************************************************/
			//변경될 야드
			szTemp = ydDaoUtils.paraRecChkNull(recTemp, "STK_POS2");
			
			if(szTemp.equals("") || szTemp.length() != 8 ){
				
				szMsg = "TO 야드 정보가 올바르지 않습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				gdRes = OperateGridData.cloneResponseGridData(inDto);
				gdRes.setMessage(szMsg );
				
				 return gdRes;
				 
			}else{
				szToYd = szTemp.substring(0,1);
			}
				
			//전  적치 야드
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
				nRtnVal = ydStockDao.getYdStock(recPara, rsStock, 0);
				
				if(nRtnVal <= 0){	
					 
					szMsg = "저장품에 데이터가 없습니다";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					gdRes = OperateGridData.cloneResponseGridData(inDto);
					gdRes.setMessage( szMsg );
					 
					return gdRes;
					 
				}
				
				//작업예약 재료확인
				recordSet1 	= JDTORecordFactory.getInstance().createRecordSet("Yd");
				nRtnVal  	= ydWrkbookMtlDao.getYdWrkbookmtl(recTemp, recordSet1, 2);
				
				if(nRtnVal > 0 ){
					
					szRtnMsg = "해당재료 ["+szTemp+"] 는 작업예약재료 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					
					gdRes = OperateGridData.cloneResponseGridData(inDto);
					gdRes.setMessage(szRtnMsg);
					
					 return gdRes;
				}
				
				//스케쥴 재료확인 - 권상
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
				
				//스케쥴 재료확인 - 권하
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
				
				recordSet = JDTORecordFactory.getInstance().createRecordSet("Yd");	
			    recTemp.setField("YD_STK_LYR_MTL_STAT", "C");
				nRtnVal  = ydStkLyrDao.getYdStklyr(recTemp, recordSet, 3);
				
				if (nRtnVal == 0  ){
					//전 전치 야드 정보가 존재 하지 않으면 진행 한다.
					//continue;
				}else if(nRtnVal <0 ){					
					//DAO Exception
					continue;
					
				}else {
					
					recordSet.first();
					
					do{
						 recTemp = JDTORecordFactory.getInstance().create();
						 recTemp = recordSet.getRecord();
						 szTemp1 = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");
						 szTemp2 = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
						 
						 
						 if (szTemp1.equals("") || szTemp1.length() !=6 ){
							 
							 szMsg = "FROM  야드 정보가 올바르지 않습니다";
							 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							 
							 gdRes = OperateGridData.cloneResponseGridData(inDto);
							 gdRes.setMessage(szMsg);
							
							 return gdRes;
							 
						 }else{
							 szFromYd = szTemp1.substring(0,1);
						 }
						 
						 recPara = JDTORecordFactory.getInstance().create();
						 recPara.setField("STL_NO",recTemp);						 
						 
						 szMsg = "  현재야드 : "+ szFromYd  + "  TO 야드  :" + szToYd;
						 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						 if(szFromYd.equals(szToYd)){
							 continue;
						 }else{
							 
							 szMsg = "다른 야드에 적치된 재료"+szTemp2+"가 존재합니다";
							 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							 
							 gdRes = OperateGridData.cloneResponseGridData(inDto);
							 gdRes.setMessage(szMsg );
							
							 return gdRes;
						 }
						 
					}while(recordSet.next());
					
				}
				
				szMsg = "[JSP-SESSION] [ "  + szOperationName +  "]  기존 위치정보 삭제 시작";
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);
				
				String rtnVal = "";
				
				recDelPara  = JDTORecordFactory.getInstance().create();
				
				recDelPara.setField("STL_NO", szTemp);
				recDelPara.setField("YD_STK_COL_GP",  inRecord[nLoop].getField("YD_STK_COL_GP"));
				recDelPara.setField("YD_STK_BED_NO",  inRecord[nLoop].getField("YD_STK_BED_NO"));
				recDelPara.setField("YD_STK_LYR_NO",  inRecord[nLoop].getField("YD_STK_LYR_NO"));
				recDelPara.setField("MODIFIER",  	  inRecord[nLoop].getField("YD_USER_ID"));
				
				rtnVal =  ydDataUtil.updStkLyrClear(recDelPara);
				
				if(rtnVal.equals(YdConstant.RETN_CD_SUCCESS)){
					szMsg = "[JSP-SESSION] [ "  + szOperationName +  "]  기존재료 정보 삭제 성공";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);
					
				} else if(rtnVal.equals(YdConstant.RETN_CD_FAILURE)){
					szMsg = "[JSP-SESSION] [ "  + szOperationName +  "]  기존재료 정보 삭제 실패";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);
					
				}
			}
			/**********************************************************************************************/
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			ejbConn 	= new EJBConnector("default", "SlabJspSeEJB", this);
			szRtnMsg 	= (String)ejbConn.trx("updSlabYdStkPosFixBoth", new Class[] { JDTORecord[].class }, 
																	   new Object[] { inRecord });
			
			
			
			//이송하차실적 BACKUP처리 CHECK--------------------------------------------------------------------
			 if("Y".equals(sMTL_MOV_UPD_FLAG)){
				for (int nLoop = 0 ; nLoop < inRecord.length ; nLoop++){
					
					 recTemp = JDTORecordFactory.getInstance().create();
					 recTemp = inRecord[nLoop];
					 
					 szTemp = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
					 
					 if(szTemp.trim().equals("")){
						 continue;
					 }else{			
					 
						//이송지시 존재 유무 체크
						szMsg="[" + szOperationName + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 재료번호["+szTemp+"]로 조회 시작";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						JDTORecordSet rsResultTemp = JDTORecordFactory.getInstance().createRecordSet("");
						int intRtnVal = ptStlFrtoMoveDao.getPtStlFrtoMove(recTemp, rsResultTemp, 0);
						if( intRtnVal <= 0 ) {
							szMsg="[" + szOperationName + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 재료[" + szTemp + "]가 존재하지 않습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					 
						}else{
							/***********************************************************************************************
							 * 이송지시의 착지개소코드 야드구분이 To위치 야드구분과 같을 경우만 이송지시 실적처리 - 2013.09.10
							 **********************************************************************************************/
							String arrWlocCd = ydDaoUtils.paraRecChkNull(rsResultTemp.getRecord(0), "ARR_WLOC_CD");
							String ydGp = YdCommonUtils.getYdFromWlocCd(arrWlocCd);

							if(szToYd.equals(ydGp)){
								//진도코드 갱신	    	 
			    	        	ejbConn 	= new EJBConnector("default", "CraneUdHdSeEJB", this);
			    				ejbConn.trx("Y1SetProgCode", new Class[] { JDTORecord.class }, new Object[] { recTemp });
				    				
								 //이송지시 실적처리
								YdCommonUtils.procCarUnLoadCmplStlNo(szTemp);
							} else {
								szMsg="[" + szOperationName + "] 진행관리의 소재이송지시 재료[" + szTemp + "] 착지개소코드[" + arrWlocCd + "]가 To위치[" + szToYd + "]와 달라 이송지시를 수정하지 않습니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							}
						}
					}
				}
			 }
			//---------------------------------------------------------------------------------------------
			
			
			gdRes.setStatus("true");
			gdRes.setMessage(szRtnMsg);
			 
		}catch(Exception e){		
			gdRes.setStatus("true");
			gdRes.setMessage(e.getMessage());
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		
		szLogMsg = "JSP-FACADE ["+ szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	
	
	/**
	 *  저장위치 수정 
	 * 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updSlabYdStkPosFix_Tot(GridData inDto) throws JDTOException {
		//LOG
		String szMsg="";
		String szMethodName="updSlabYdStkPosFix_Tot";
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szOperationName	= "저장위치 수정";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szLogMsg ="";
		String szFromYd = "";
		String szTemp = "";
		String szToYd ="";
		String szAimToyd ="";
		
		JDTORecordSet recordSet = null;
		JDTORecordSet rsStock   = null;
		JDTORecord recPara = null;
		int nRtnVal = 0;
		try{
			
			szLogMsg = "JSP-FACADE [" + szOperationName + "]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
			JDTORecord recTemp     = CmUtil.genJDTORecord(inDto);
			
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			YdStockDao ydStockDao = new YdStockDao();
			
			/***********************************************************************************************
			 * TO 위치의 정보가 원래 정보의 저장위치 정보에서 목표야드 또는 현야드가 아닐경우는 ERROR RETURN 
			 * 하는 로직추가 전 저장위치 정보가 없을경우는 무시함
			 * 
			 **********************************************************************************************/
			//변경될 야드
			szTemp = ydDaoUtils.paraRecChkNull(recTemp, "STK_POS2");
			
			if(szTemp.equals("") || szTemp.length() != 8 ){
				
				szMsg = "TO 야드 정보가 올바르지 않습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				gdRes = OperateGridData.cloneResponseGridData(inDto);
				gdRes.setMessage(szMsg );
				
				 return gdRes;
				 
			}else{
				szToYd = szTemp.substring(0,1);
			}
			
		
			
			//전  적치 야드
		
			for (int nLoop = 0 ; nLoop < inRecord.length ; nLoop++){
				 recTemp     = JDTORecordFactory.getInstance().create();
				 recTemp = inRecord[nLoop];
				 
				 szTemp = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
				 
				 if(szTemp.trim().equals("")){
					 continue;
				 }
					 
				recPara  = JDTORecordFactory.getInstance().create();					
				recPara.setField("STL_NO", szTemp);
				
				
				rsStock = JDTORecordFactory.getInstance().createRecordSet("Yd");	
				nRtnVal  = ydStockDao.getYdStock(recPara, rsStock, 0);
				
				if(nRtnVal ==0)
				{	
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
				
				rsStock.first();
				recPara  = JDTORecordFactory.getInstance().create();	
				recPara =  rsStock.getRecord(0);					
				
				szAimToyd = ydDaoUtils.paraRecChkNull(recPara,"YD_AIM_YD_GP");
				
				
				
				
				//산적위치 수정위치가 권상정보이거나 권하정보를 가지고 있을경우는 
				//산적위치 정보를 수정할 수없다.
				//2009.11.02 수정
				
				
				recordSet = JDTORecordFactory.getInstance().createRecordSet("Yd");
				recTemp.setField("YD_STK_LYR_MTL_STAT", "U");
				nRtnVal  = ydStkLyrDao.getYdStklyr(recTemp, recordSet, 3);
				
				if(nRtnVal > 0 ) {
					//해당정보는 권상재료 정보입니다.
					szRtnMsg = "해당재료 ["+szTemp+"] 는 권상재료 정보입니다.";
					
					//gdRes = OperateGridData.cloneResponseGridData(inDto);
					
					inDto.setStatus("true");
					inDto.setMessage(szRtnMsg);					
					
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return inDto;
					
				}
				
				
				recordSet = JDTORecordFactory.getInstance().createRecordSet("Yd");
				recTemp.setField("YD_STK_LYR_MTL_STAT", "D");
				nRtnVal  = ydStkLyrDao.getYdStklyr(recTemp, recordSet, 3);
				
				if(nRtnVal > 0 ) {
					//해당정보는 권상재료 정보입니다.
					szRtnMsg = "해당재료 ["+szTemp+"] 는 권하재료 정보입니다.";
					
					//gdRes = OperateGridData.cloneResponseGridData(inDto);
					
					inDto.setStatus("true");
					inDto.setMessage(szRtnMsg);					
					
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return inDto;
					
				}
				
				
				recordSet = JDTORecordFactory.getInstance().createRecordSet("Yd");	
			    
			    recTemp.setField("YD_STK_LYR_MTL_STAT", "C");
				nRtnVal  = ydStkLyrDao.getYdStklyr(recTemp, recordSet, 3);
				
				ydUtils.putLog(szSessionName, szMethodName, "nRtnVal :"  + nRtnVal, YdConstant.DEBUG);
				
				
				if (nRtnVal == 0  ){
					//전 전치 야드 정보가 존재 하지 않으면 진행 한다.
					continue;
				}else if(nRtnVal <0 ){					
					//DAO Exception
					continue;
					
				}else {
					recordSet.first();
					do{
						 recTemp  = JDTORecordFactory.getInstance().create();
						 recTemp =  recordSet.getRecord();
						 szTemp = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");
						 
						 
						 if (szTemp.equals("") || szTemp.length() !=6 ){
							 szMsg = "FROM  야드 정보가 올바르지 않습니다";
							 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							 gdRes = OperateGridData.cloneResponseGridData(inDto);
							 gdRes.setMessage(szMsg);
							
							 return gdRes;
							 
						 } else if ("2".equals(szTemp.substring(0,1))) {
							 szMsg = "1열연 슬라브야드에 적치되어 있는 슬라브입니다";
							 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							 gdRes = OperateGridData.cloneResponseGridData(inDto);
							 gdRes.setMessage(szMsg);
							
							 return gdRes;
						 } else{
							 szFromYd = szTemp.substring(0,1);
						 }
						 
						 recPara = JDTORecordFactory.getInstance().create();
						 recPara.setField("STL_NO",recTemp);						 
						 
						 
						 szMsg = " 목표야드 : "+  szAimToyd + "  현재야드 : "+ szFromYd  + "  TO 야드  :" + szToYd;
						 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						 if(szFromYd.equals(szToYd)|| szAimToyd.equals(szToYd)){
							 continue;
						 }else{
							 szMsg = "지정되지않는 야드로 정보를 변경할수 없습니다";
							 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							 gdRes = OperateGridData.cloneResponseGridData(inDto);
							 gdRes.setMessage(szMsg );
							
							 return gdRes;
						 }
						 
					}while(recordSet.next());
					
				}
				
			}
			
			
			/**********************************************************************************************/
			
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("updSlabYdStkPosFix_Tot", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			 
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		
		szLogMsg = "JSP-FACADE [ " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;
	}
	
	
	
	
	
	/**
	 *  저장위치 수정(모바일용)
	 * 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public JDTORecord updSlabYdStkPosFix_Tot(JDTORecord [] inDto) throws JDTOException {
		//LOG
		String szMsg="";
		String szMethodName="updSlabYdStkPosFix_Tot";
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szOperationName	= "저장위치 수정";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szLogMsg ="";
		String szFromYd = "";
		String szTemp = "";
		String szToYd ="";
		String szAimToyd ="";
		
		JDTORecordSet recordSet = null;
		JDTORecordSet rsStock   = null;
		JDTORecord recPara = null;
		JDTORecord recReturn = JDTORecordFactory.getInstance().create();
		int nRtnVal = 0;
		try{
			
			szLogMsg = "JSP-FACADE [" + szOperationName + "]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			//JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
			JDTORecord recTemp     = inDto[0];//CmUtil.genJDTORecord(inDto);
			
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			YdStockDao ydStockDao = new YdStockDao();
			
			/***********************************************************************************************
			 * TO 위치의 정보가 원래 정보의 저장위치 정보에서 목표야드 또는 현야드가 아닐경우는 ERROR RETURN 
			 * 하는 로직추가 전 저장위치 정보가 없을경우는 무시함
			 * 
			 **********************************************************************************************/
			//변경될 야드
			szTemp = ydDaoUtils.paraRecChkNull(recTemp, "STK_POS2");
			
			if(szTemp.equals("") || szTemp.length() != 8 ){
				
				szMsg = "TO 야드 정보가 올바르지 않습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//gdRes = OperateGridData.cloneResponseGridData(inDto);
				//recReturn.setMessage(szMsg );
				recReturn.setField("Status" , "false");
				recReturn.setResultMsg(szMsg);
				
				 return recReturn;
				 
			}else{
				szToYd = szTemp.substring(0,1);
			}
			
		
			
			//전  적치 야드
			for(int nLoop = 0; nLoop < inDto.length ; nLoop++) {
			//for (int nLoop = 0 ; nLoop < inRecord.length ; nLoop++){
				 recTemp     = JDTORecordFactory.getInstance().create();
				 //recTemp = inRecord[nLoop];
				 recTemp = inDto[nLoop];
				 
				 szTemp = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
				 
				 if(szTemp.trim().equals("")){
					 continue;
				 }
					 
				recPara  = JDTORecordFactory.getInstance().create();					
				recPara.setField("STL_NO", szTemp);
				
				
				rsStock = JDTORecordFactory.getInstance().createRecordSet("Yd");	
				nRtnVal  = ydStockDao.getYdStock(recPara, rsStock, 0);
				
				if(nRtnVal ==0)
				{	
					 szMsg = "저장품에 데이터가 없습니다";
					 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					 recReturn.setField("Status" , "false");
					 recReturn.setResultMsg(szMsg);
						
					 return recReturn;
					 
					 
				}else if(nRtnVal <0){
					
					 szMsg = "저장품에 조회시 ERROR";
					 //gdRes = OperateGridData.cloneResponseGridData(inDto);
					 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					 recReturn.setField("Status" , "false");
					 recReturn.setResultMsg(szMsg);
						
					 return recReturn;
					 
				}
				
				rsStock.first();
				recPara  = JDTORecordFactory.getInstance().create();	
				recPara =  rsStock.getRecord(0);					
				
				szAimToyd = ydDaoUtils.paraRecChkNull(recPara,"YD_AIM_YD_GP");
				
				
				
				
				//산적위치 수정위치가 권상정보이거나 권하정보를 가지고 있을경우는 
				//산적위치 정보를 수정할 수없다.
				//2009.11.02 수정
				
				
				recordSet = JDTORecordFactory.getInstance().createRecordSet("Yd");
				recTemp.setField("YD_STK_LYR_MTL_STAT", "U");
				nRtnVal  = ydStkLyrDao.getYdStklyr(recTemp, recordSet, 3);
				
				if(nRtnVal > 0 ) {
					//해당정보는 권상재료 정보입니다.
					szRtnMsg = "해당재료 ["+szTemp+"] 는 권상재료 정보입니다.";
					
					//gdRes = OperateGridData.cloneResponseGridData(inDto);
					
					recReturn.setField("Status" , "true");
					//inDto.setStatus("true");
					//inDto.setMessage(szRtnMsg);					
					
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					recReturn.setResultMsg(szMsg);
					
					return recReturn;
					//return inDto;
					
				}
				
				
				recordSet = JDTORecordFactory.getInstance().createRecordSet("Yd");
				recTemp.setField("YD_STK_LYR_MTL_STAT", "D");
				nRtnVal  = ydStkLyrDao.getYdStklyr(recTemp, recordSet, 3);
				
				if(nRtnVal > 0 ) {
					//해당정보는 권상재료 정보입니다.
					szRtnMsg = "해당재료 ["+szTemp+"] 는 권하재료 정보입니다.";
					
					//gdRes = OperateGridData.cloneResponseGridData(inDto);
					
					//inDto.setStatus("true");
					//inDto.setMessage(szRtnMsg);					
					
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					//return inDto;
					
					recReturn.setField("Status" , "true");
					recReturn.setResultMsg(szMsg);
					
					return recReturn;
				}
				
				
				recordSet = JDTORecordFactory.getInstance().createRecordSet("Yd");	
			    
			    recTemp.setField("YD_STK_LYR_MTL_STAT", "C");
				nRtnVal  = ydStkLyrDao.getYdStklyr(recTemp, recordSet, 3);
				
				ydUtils.putLog(szSessionName, szMethodName, "nRtnVal :"  + nRtnVal, YdConstant.DEBUG);
				
				
				if (nRtnVal == 0  ){
					//전 전치 야드 정보가 존재 하지 않으면 진행 한다.
					continue;
				}else if(nRtnVal <0 ){					
					//DAO Exception
					continue;
					
				}else {
					recordSet.first();
					do{
						 recTemp  = JDTORecordFactory.getInstance().create();
						 recTemp =  recordSet.getRecord();
						 szTemp = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");
						 
						 
						 if (szTemp.equals("") || szTemp.length() !=6 ){
							 szMsg = "FROM  야드 정보가 올바르지 않습니다";
							 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							 //gdRes = OperateGridData.cloneResponseGridData(inDto);
							 //gdRes.setMessage(szMsg);
							
							 //return gdRes;
							 recReturn.setResultMsg(szMsg);
								
							 return recReturn;
							 
						 }else{
							 szFromYd = szTemp.substring(0,1);
						 }
						 
						 recPara = JDTORecordFactory.getInstance().create();
						 recPara.setField("STL_NO",recTemp);						 
						 
						 
						 szMsg = " 목표야드 : "+  szAimToyd + "  현재야드 : "+ szFromYd  + "  TO 야드  :" + szToYd;
						 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						 if(szFromYd.equals(szToYd)|| szAimToyd.equals(szToYd)){
							 continue;
						 }else{
							 szMsg = "지정되지않는 야드로 정보를 변경할수 없습니다";
							 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							 //gdRes = OperateGridData.cloneResponseGridData(inDto);
							 //gdRes.setMessage(szMsg );
							
							 //return gdRes;
							 recReturn.setResultMsg(szMsg);
								
							 return recReturn;
						 }
						 
					}while(recordSet.next());
					
				}
				
			}
			
			
			/**********************************************************************************************/
			
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("updSlabYdStkPosFix_Tot", new Class[] { JDTORecord[].class }, new Object[] { inDto /*inRecord*/ });
			//gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			 
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}		
		//gdRes.setStatus("true");
		//gdRes.setMessage(szRtnMsg);
		recReturn.setField("Status" , "true");
		recReturn.setResultMsg(szRtnMsg);
		
		szLogMsg = "JSP-FACADE [ " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		//return gdRes;
		
		return recReturn;
	}
	
	
	
	
	/**
	 * 크레인구분 조회 (화면:스케줄기준관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getCrnGp(GridData inDto) throws JDTOException {
		
	
		String szMethodName     = "getCrnGp";
		String szLogMsg = "";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{
			
			szLogMsg = "JSP-FACADE [크레인구분 조회 (화면:스케줄기준관리)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getCrnGp", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "JSP-FACADE [크레인구분 조회 (화면:스케줄기준관리] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		

		return gdRes;
	}  // End Of getCrnGp
	
	
	
	
	/**
	 * 스케줄기준 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getSlabYdSchStd(GridData inDto) throws JDTOException {
		
	
		String szMethodName     = "getSlabYdSchStd";
		String szLogMsg = "";
		String szOperationName	= "스케줄기준 조회";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{
			
			szLogMsg = "JSP-FACADE [ " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getSlabYdSchStd",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "JSP-FACADE [ " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		

		return gdRes;
	}  // End Of getSlabYdSchStd
	
	
	
	/**
	 * BookOut기준 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData BookoutMgtList(GridData inDto) throws JDTOException {
		
	
		String szMethodName     = "BookoutMgtList";
		String szLogMsg = "";
		String szOperationName	= "BookOut기준 조회";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{
			
			szLogMsg = "JSP-FACADE [ " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("BookoutMgtList",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			
		}catch(Exception e){
			
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "JSP-FACADE [ " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		

		return gdRes;
	}  // End Of getSlabYdSchStd
	
	
	/**
	 * 후판정정야드 운영기준 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getpPlateYdstkRuleMgt(GridData inDto) throws JDTOException {
		
	
		String szMethodName     = "getpPlateYdstkRuleMgt";
		String szLogMsg = "";
		String szOperationName	= "후판정정야드 운영기준 조회";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{
			
			szLogMsg = "JSP-FACADE [ " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getpPlateYdstkRuleMgt",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			
		}catch(Exception e){
			
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "JSP-FACADE [ " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		

		return gdRes;
	}  // End Of getSlabYdSchStd
	
	/**
	 * 스케줄기준 조회	(화면:스케줄기준관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getSlabYdSchStd_New(GridData inDto) throws JDTOException {
		
	
		String szMethodName     = "getSlabYdSchStd_New";
		String szLogMsg = "";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{
			
			szLogMsg = "JSP-FACADE [스케줄기준 조회 (화면:스케줄기준관리)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getSlabYdSchStd_New",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "JSP-FACADE [스케줄기준 조회 (화면:스케줄기준관리)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		

		return gdRes;
	}  // End Of getSlabYdSchStd_New
	
	/**
	 * 슬라브야드 스케줄 기준관리 (수정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updslabYdSchStdMgt(GridData gdReq) throws JDTOException {
	
		String szMethodName="updslabYdSchStdMgt";
		String szLogMsg = ""; 
		String szOperationName	= "스케줄 기준관리 (수정)";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "JSP-FACADE [ " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("updslabYdSchStdMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "JSP-FACADE [ " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of updslabYdSchStdMgt
	
	
	
	/**
	 * 후판정정야드 Bookout기준정보 (수정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updpPlateYdBookoutMgt(GridData gdReq) throws JDTOException {
	
		String szMethodName="updpPlateYdBookoutMgt";
		String szLogMsg = ""; 
		String szOperationName	= "Bookout기준정보 (수정)";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "JSP-FACADE [ " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("updpPlateYdBookoutMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "JSP-FACADE [ " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of updslabYdSchStdMgt
	
	/**
	 * 후판정정야드 운영기준정보 (수정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updpPlateYdStkMgt(GridData gdReq) throws JDTOException {
	
		String szMethodName="updpPlateYdStkMgt";
		String szLogMsg = ""; 
		String szOperationName	= "후판정정야드 운영기준정보 (수정)";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "JSP-FACADE [ " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("updpPlateYdStkMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "JSP-FACADE [ " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of updslabYdSchStdMgt
	
	/**
	 * 후판정정야드 운영기준정보 (Remark수정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updpPlateYdStkRemark(GridData gdReq) throws JDTOException {
	
		String szMethodName="updpPlateYdStkRemark";
		String szLogMsg = ""; 
		String szOperationName	= "후판정정야드 운영기준정보 (Remark수정)";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "JSP-FACADE [ " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("updpPlateYdStkRemark", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "JSP-FACADE [ " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of updslabYdSchStdMgt
	
	
	
	/**
	 * 스케줄기준 조회 - 크레인별
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getSlabYdSchStd1(GridData inDto) throws JDTOException {
		
	
		String szMethodName     = "getSlabYdSchStd1";
		String szLogMsg = "";
		String szOperationName	= "스케줄기준 조회 - 크레인별";

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getSlabYdSchStd1",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		

		return gdRes;
	}//end of getSlabYdSchStd1
	
	
	
	
	
	/**
	 * 슬라브야드 스케줄 기준관리 - 크레인별 (수정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updslabYdSchStdMgt1(GridData gdReq) throws JDTOException {
	
		String szMethodName="updslabYdSchStdMgt1";
		String szLogMsg = ""; 
		String szOperationName	= "스케줄 기준관리 - 크레인별 (수정)";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("updslabYdSchStdMgt1", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of updslabYdSchStdMgt1
	
	
	
	
	
	/**
	 * 적치단- 재료번호 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getSlabYdStkBedLyrList(GridData inDto) throws JDTOException {
	
		String szMethodName     = "getSlabYdStkBedLyrList";		
		String szLogMsg = "";
		String szOperationName	= "적치단- 재료번호 조회";

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getSlabYdStkBedLyrList",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	/**
	 *  슬라브야드 메뉴얼 작업지시 편성
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData slabYdManualReq(GridData gdReq) throws JDTOException {
	
		String szMethodName="slabYdManualReq";
		String szLogMsg = "";
		String szOperationName	= "메뉴얼 작업지시 편성";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			

			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("slabYdManualReq", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of slabYdManualReq
	
	/**
	 *  크레인 번호 Select
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getSlabYdCrNoComboList(GridData inDto) throws JDTOException {
	
		String szMethodName     = "getSlabYdCrNoComboList";	
		String szLogMsg = "";
		String szOperationName	= "크레인 번호 Select";

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getSlabYdCrNoComboList",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of getSlabYdCrNoComboList
	
	/**
	 *  준비이적대상재조회 팝업
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getSlabYdRedyTranReSrcPop(GridData inDto) throws JDTOException {
	
		String szMethodName = "getSlabYdRedyTranReSrcPop";		
		String szLogMsg = "";
		String szOperationName	= "준비이적대상재조회";

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getSlabYdRedyTranReSrcPop",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of getSlabYdRedyTranReSrcPop
	
	
	/**
	 *  스카핑 작업관리 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getScarfingMgt(GridData inDto) throws JDTOException {
	
		String szMethodName = "getScarfingMgt";		
		String szLogMsg = "";
		String szOperationName	= "스카핑 작업관리 조회";

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getScarfingMgt",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of getScarfingMgt
	
	
	/**
	 * PICKUP BED 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getPickUpBed(GridData inDto) throws JDTOException {
		 
		String szMethodName     = "getScarfingMgt";		

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		String szLogMsg = "";
		String szOperationName	= "PICKUP BED 조회";
		

		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getPickUpBed",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of getPickUpBed
	
	
	
	/**
	 * PICKUP BED  상세 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getPickUpBedDet(GridData inDto) throws JDTOException {
	
		String szMethodName     = "getPickUpBedDet";		
		String szLogMsg = "";
		String szOperationName	= "PICKUP BED  상세 조회";

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getPickUpBedDet",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of getPickUpBedDet
	
	/**
	 * 보급요구 처리(M-Scarfing, H-Scarfing ...)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 */
	public GridData procSupplyWrkDmd(GridData gdReq) throws JDTOException {
	
		String szMethodName     = "procScarfingWrkDmd";		
		String szRet = null;
		String szLogMsg = "";
		String szOperationName	= "보급요구 처리";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		
		try {
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			String szSupplyType = StringHelper.evl(gdReq.getParam("SUPPLY_TYPE"), "");
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			szRet = (String)ejbConn.trx("procSupplyWrkDmd", new Class[] { JDTORecord[].class, String.class }, new Object[] { inRecord, szSupplyType });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			//복사를 할 지 테스트 필요...
			if( szRet.equals("Success") ) {
				gdRes.setMessage("Success;" + szRet);
			}else{
				gdRes.setMessage("Failure;" + szRet);				//에러메세지코드로 변환할 필요있음
			}
		}catch(Exception e) {
			gdRes = gdReq;
			gdRes.setMessage("Failure;" + e.getMessage());			//e.getMessage() - 에러메세지코드를 변환할 필요가 있음
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}

	
	
	
	/**
	 * Takc-Out 처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 */
	public GridData insSlabYdTout(GridData gdReq) throws JDTOException {
	
		String szMethodName     = "insSlabYdTout";		
		String szRet = null;
		String szLogMsg = "";
		String szOperationName	= "Takc-Out 처리";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try {
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			JDTORecord [] inRecordSet =  ydComUtil.genGridToJDTORecordAll(gdReq);
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			szRet = (String)ejbConn.trx("insSlabYdTout", new Class[] { JDTORecord[].class, JDTORecord.class }, new Object[] { inRecordSet, inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			//복사를 할 지 테스트 필요...
			
			if( szRet.equals("Success") ) {
				gdRes.setMessage("Success;" + szRet);
			}else{
				gdRes.setMessage("Failure;" + szRet);				//에러메세지코드로 변환할 필요있음
			}
		}catch(Exception e) {
			gdRes = gdReq;
			gdRes.setMessage("Failure;" + e.getMessage());			//e.getMessage() - 에러메세지코드를 변환할 필요가 있음
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	/**
	 * 적치 삭제 처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 */
	public GridData delSlabYdTout(GridData gdReq) throws JDTOException {
		
		String szMethodName     = "delSlabYdTout";		
		String szLogMsg = "";
		String szOperationName	= "적치 삭제 처리";
		
		String szRet = null;
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try {
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord [] inRecordSet = ydComUtil.genJDTORecordSet(gdReq);
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			szRet = (String)ejbConn.trx("delSlabYdTout"	, new Class[] { JDTORecord[].class }, new Object[] { inRecordSet });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			//복사를 할 지 테스트 필요...
			
			if( szRet.equals("Success") ) {
				gdRes.setMessage("Success;" + szRet);
			}else{
				gdRes.setMessage("Failure;" + szRet);				//에러메세지코드로 변환할 필요있음
			}
		}catch(Exception e) {
			gdRes = gdReq;
			gdRes.setMessage("Failure;" + e.getMessage());			//e.getMessage() - 에러메세지코드를 변환할 필요가 있음
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	/**
	 * 정정 보급 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getShearMgt(GridData inDto) throws JDTOException {
	
		String szMethodName     = "getShearMgt";
		String szLogMsg = "";
		String szOperationName	= "정정 보급 조회";
		

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getShearMgt",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of getShearMgt
	
	
	
	
	/**
	 * BRE TEST
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData testBreRule(GridData inDto) throws JDTOException {
		
		String szMethodName     = "testBreRule";	
		String szLogMsg = "";
		String szOperationName	= "BRE TEST";
		

		try{
			

			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
			JDTORecord       retPara         = JDTORecordFactory.getInstance().create();
			
			
			//테스트 입니다 
			
			recPara.setField("YD_BOOK_OUT_LOC", "56010");
			System.out.println("북아웃 56010 위치는 ====");
			retPara = yDRuleBean.getRuleYD699(recPara);
			
			ydUtils.displayRecord(szOperationName, retPara);
			
			
			recPara.setField("YD_BOOK_OUT_LOC", "56115");
			System.out.println("북아웃 56115 위치는 ====");
			retPara = yDRuleBean.getRuleYD699(recPara);
			
			ydUtils.displayRecord(szOperationName, retPara);
			
		
						
				
			HashMap hmap = new HashMap();
			hmap.put("MSG_GP", "C");
			hmap.put("YD_EQP_ID", "AACRA1");
			hmap.put("YD_POS_X", "700");
			hmap.put("YD_POS_Y", "20");
			hmap.put("YD_MSG", "화면에서 보낸 이동--->");
 
			
			//FLAX_PUSH사용여부 체크 /////////////////////////////////////////////////
		 	//JDTORecord recPara =  JDTORecordFactory.getInstance().create();
	    	JDTORecord recInTemp =  JDTORecordFactory.getInstance().create();
	    	String CHK ="N";
			JDTORecordSet outRecSet = null;
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			
	    	outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			recPara =  JDTORecordFactory.getInstance().create();
			recPara.setField("YD_GP","A");
			/*com.inisteel.cim.yd.common.util.YdUtils.chklist*/
			int intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 999);
			
			if( intRtnVal > 0 ) {
				outRecSet.first();
				recInTemp = outRecSet.getRecord();
				CHK = recInTemp.getFieldString("CHK").trim();
			}
 
			ydUtils.putLog(szSessionName, szMethodName, "A"+":야드 FLAX PUSH 사용유무:"+CHK, YdConstant.INFO);
			/////////////////////////////////////////////////////////////////////////
	    	if(CHK.equals("Y")){
			ydUtils.pushToFlexClient(YdConstant.YD_MONITORING_CHANNEL_A, hmap);
	    	}
			
			
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		inDto.setStatus("true");
		inDto.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return inDto;
	}  //end of testBreRule
	

	/**
	 * 작업예약스케줄코드 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getWBookId(GridData inDto) throws JDTOException {
	
		String szMethodName = "getWBookId";		
		String szLogMsg = "";
		String szOperationName	= "작업예약스케줄코드 조회";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		
		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			recordSet = (JDTORecordSet) ejbConn.trx("getWBookId",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of getWBookId
	
	/**
	 * 작업예약스케줄 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getSlabYdWBookSchList(GridData inDto) throws JDTOException {
		
		String szMethodName = "getSlabYdWBookSchList";		
		String szLogMsg = "";
		String szOperationName	= "작업예약스케줄 조회";
		

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			recordSet = (JDTORecordSet) ejbConn.trx("getSlabYdWBookSchList",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of getSlabYdWBookSchList
	
	/**
	 * 크레인스케줄 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getSlabYdCrnSchList(GridData inDto) throws JDTOException {
	
		String szMethodName     = "getSlabYdCrnSchList";		
		String szLogMsg = "";
		String szOperationName	= "크레인스케줄 조회";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			recordSet = (JDTORecordSet) ejbConn.trx("getSlabYdCrnSchList",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of getSlabYdWBookSchList
	
	/**
	 * From Bed 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getSlabYdCrnSchFrmBed(GridData inDto) throws JDTOException {
		
		String szMethodName     = "getSlabYdCrnSchFrmBed";		
		String szLogMsg = "";
		String szOperationName	= "From Bed 조회";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			recordSet = (JDTORecordSet) ejbConn.trx("getSlabYdCrnSchFrmBed",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of getSlabYdCrnSchFrmBed
	
	/**
	 * To Bed 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getSlabYdCrnSchToBed(GridData inDto) throws JDTOException {
	
		String szMethodName     = "getSlabYdCrnSchToBed";		
		String szLogMsg = ""; 
		String szOperationName	= "From Bed 조회";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			recordSet = (JDTORecordSet) ejbConn.trx("getSlabYdCrnSchToBed",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(),YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of getSlabYdCrnSchToBed
	

	/**
	 * 설비입고예정위치 화면 heat_no코드 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData getYdHeatCodeSearch(GridData inDto) throws JDTOException {
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		String szLogMsg = "";
		String szMethodName = "getYdHeatCodeSearch";
		String szOperationName	= "heat_no코드 조회";
		
		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdHeatCodeSearch", inRecord);			                                                          
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog("SlabJspSeEJB", "getYdHeatCodeSearch()", e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	/**
	 * 설비입고예정위치 화면 machine코드 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData getYdMacCodeSearch(GridData inDto) throws JDTOException {
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		
		String szLogMsg = "";
		String szMethodName = "getYdMacCodeSearch";
		String szOperationName	= "machine코드 조회";
		
		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdMacCodeSearch", inRecord);			                                                          
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog("SlabJspSeEJB", "getYdMacCodeSearch()", e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	/**
	 * 설비입고예정위치 화면 SCH_CD코드 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData getYdSchCodeSearch(GridData inDto) throws JDTOException {
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		
		String szMethodName = "getYdSchCodeSearch";
		String szLogMsg = "";
		String szOperationName	= "설비입고예정위치 화면 SCH_CD코드 조회";
		
		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdSchCodeSearch", inRecord);			                                                          
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog("SlabJspSeEJB", "getYdSchCodeSearch()", e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	/**
	 * 설비입고예정위치 화면 CRN_NAME 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData getYdCrnSearch(GridData inDto) throws JDTOException {
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		
		String szLogMsg = "";
		String szMethodName = "getYdCrnSearch";
		String szOperationName	= "설비입고예정위치 화면 CRN_NAME 조회";
		
		
		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdCrnSearch", inRecord);			                                                          
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog("SlabJspSeEJB", "getYdCrnSearch()", e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	/**
	 * 설비입고예정위치 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabYdEqpInEstiLoc(GridData inDto) throws JDTOException {
		
		String szMethodName = "getSlabYdEqpInEstiLoc";
		String szLogMsg = "";
		String szOperationName	= "설비입고예정위치 조회";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdEqpInEstiLoc", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getSlabYdEqpInEstiLoc
	
	/**
	 * 설비입고예정위치  조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getYdSchLocSrc(GridData inDto) throws JDTOException {
		
		String szMethodName = "getYdSchLocSrc";
		String szLogMsg = "";
		String szOperationName	= "설비입고예정위치 조회";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdSchLocSrc", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getYdSchLocSrc
	
	/**
	 * 설비입고예정위치 화면 하단 오른쪽 그리드 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getYdStkLocSearch(GridData inDto) throws JDTOException {
		
		String szMethodName = "getYdStkLocSearch";
		String szLogMsg = "";
		String szOperationName	= "설비입고예정위치 조회";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdStkLocSearch", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getYdStkLocSearch
	
	
	
	
	
	/**
	 * 목표행선/ 목표동 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData updAimFix(GridData inDto) throws JDTOException {
	
		String szMethodName="updSlabYdStkPosSet";
		String szRcvMsg = "";
		String szLogMsg = "";
		String szOperationName	= "목표행선/ 목표동 수정";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
			
		try{
			
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			szRcvMsg = (String)ejbConn.trx("updAimFix",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			gdRes.setStatus("true");
			gdRes.setMessage(szRcvMsg);
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(),YdConstant.ERROR);	
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRcvMsg);
		
		szLogMsg = "[JSP-FACADE  " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	/**
	 * 크레인별 배차기준조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData getCarAsgnStdByCrn(GridData inDto) throws JDTOException {
		String szOperationName	= "크레인별 배차기준";
		String szMethodName 	= "getCarAsgnStdByCrn";
		String szRtnMsg			= YdConstant.RETN_CD_SUCCESS;
		String szLogMsg			= null;
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getCarAsgnStdByCrn", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}catch(Exception e){
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 오류발생 - 메세지 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		return gdRes;
	} //end of getCarAsgnStdByCrn
	
	/**
	 * 크레인별 배차기준수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData uptCarAsgnStdByCrn(GridData inDto) throws JDTOException {
		/*
		 * 업무기준 : 그리드로 넘겨진 배차기준 정보를 설비테이블에 수정 처리
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		String szOperationName = "크레인별 배차기준수정";
		String szMethodName = "uptCarAsgnStdByCrn";
		String szRtnMsg		= null;
		String szLogMsg		= null;
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			szRtnMsg = (String)ejbConn.trx("uptCarAsgnStdByCrn",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}catch(Exception e){
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 오류발생 - 메세지 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		return gdRes;
	} //end of uptCarAsgnStdByCrn
	
	/**
	 * 통합야드 이송재료 LIST
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabTotYdTransMtlList(GridData inDto) throws JDTOException {
	
		String szMethodName = "getSlabTotYdTransMtlList";
		String szLogMsg = "";
		String szOperationName = "이송재료 LIST";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabTotYdTransMtlList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getSlabTotYdTransMtlList
	
	/**
	 * 준비스케줄LIST(크레인별)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData getYdPrepSchListByCrn(GridData inDto) throws JDTOException {
		/*
		 * 업무기준 : 준비스케줄테이블에서 준비스케줄ID ASC 를 조회
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		String szMethodName = "getYdPrepSchListByCrn";
		String szLogMsg = "";
		String szOperationName = "준비스케줄LIST(크레인별)";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdPrepSchListByCrn", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		return gdRes;
	} //end of getYdPrepSchListByCrn
	
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
		 */
		String szMethodName = "getYdPrepSchList";
		String szLogMsg = "";
		String szOperationName = "준비스케줄LIST";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdPrepSchList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getYdPrepSchList
	
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
		String szOperationName = "준비스케줄재료LIST";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdPrepSchMtlList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getYdPrepSchMtlList
	
	
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
		String szOperationName = "준비스케줄과 준비재료삭제";
		
		String szRtnMsg		= null;
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			szRtnMsg = (String)ejbConn.trx("delYdPrepSch",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of delYdPrepSch
	
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
		String szRtnMsg		= null;
		String szLogMsg = "";
		String szOperationName = "준비재료삭제";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			
			JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			szRtnMsg = (String)ejbConn.trx(szMethodName,
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		return gdRes;
	} //end of delYdPrepMtl
	
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
		String szRtnMsg		= null;
		String szLogMsg = "";
		String szOperationName = "준비스케줄수정";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			szRtnMsg = (String)ejbConn.trx("uptYdPrepSch",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of uptYdPrepSch
	
	/**
	 * 준비스케줄ID LIST - 상차LOT편성 시 선택박스 표시
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData getYdPrepSchIdList(GridData inDto) throws JDTOException {
		/*
		 * 업무기준 : 준비스케줄테이블에서 준비스케줄ID ASC 를 조회
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		String szMethodName = "getYdPrepSchIdList";
		String szLogMsg = "";
		String szOperationName = "상차LOT편성 시 선택박스 표시";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdPrepSchIdList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getYdPrepSchIdList
	
	/**
	 * 준비스케줄재료LIST - 상차LOT편성 시 사용
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData getYdPrepmtlNStockByPrepSchId(GridData inDto) throws JDTOException {
		/*
		 * 업무기준 : 준비스케줄테이블에서 준비스케줄ID ASC 를 조회
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		String szMethodName = "getYdPrepmtlNStockByPrepSchId";
		String szLogMsg= "";
		String szOperationName = "상차LOT편성 시 사용";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdPrepmtlNStockByPrepSchId", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		
		return gdRes;
	} //end of getYdPrepmtlNStockByPrepSchId
	
	/**
	 * 이송재료LIST
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData getYdTransMtlList(GridData inDto) throws JDTOException {
		/*
		 * 업무기준 : 이송지시테이블, 저장품, 적치단, 슬라브공통, 주편공통, 준비스케줄 테이블을 조인해서 이송대상재를 조회
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		String szMethodName = "getYdTransMtlList";
		String szLogMsg = "";
		String szOperationName = "이송재료LIST";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdTransMtlList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getYdTransMtlList
	
	/**
	 * 이송대상재를 준비스케줄에 등록 - 크레인설비 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData insYdPrepSchNCrn(GridData inDto) throws JDTOException {
		/*
		 * 업무기준 : 이송지시테이블, 저장품, 적치단, 슬라브공통, 주편공통, 준비스케줄, 준비재료, 작업예약, 작업예약재료
		 * 			 테이블을 조인해서 이송대상재를 조회해서 준비스케줄에 등록
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		String szMethodName = "insYdPrepSchNCrn";
		String szRtnMsg = null;
		String szLogMsg = "";
		String szOperationName = "크레인설비 등록";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			szRtnMsg = (String) ejbConn.trx("SlabJspSeEJB", "insYdPrepSchNCrn", inRecord);
			gdRes = OperateGridData.cloneResponseGridData(inDto);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of insYdPrepSchNCrn
	
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
		String szRtnMsg		= null;
		String szLogMsg = "";
		String szOperationName = "수동, 크레인설비 등록";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			szRtnMsg = (String)ejbConn.trx("insYdPrepSchNCrnByManual",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of insYdPrepSchNCrnByManual
	
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
		 * 			 테이블을 조인해서 이송대상재를 조회해서 준비스케줄에 등록
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		String szMethodName = "insYdPrepSch";
		String szRtnMsg		= null;
		String szLogMsg = "";
		String szOperationName = "준비스케줄에 등록";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			szRtnMsg = (String) ejbConn.trx("SlabJspSeEJB", "insYdPrepSch", inRecord);
			gdRes = OperateGridData.cloneResponseGridData(inDto);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of insYdPrepSch
	
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
		String szRtnMsg		= null;
		String szLogMsg = "";
		String szOperationName = "준비스케줄에 등록 - 수동";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(inDto);
			
			JDTORecord recInfo = ydComUtil.genParamToJDTORecord(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			szRtnMsg = (String)ejbConn.trx("insYdPrepSchByManual",
						new Class[] { JDTORecord[].class , JDTORecord.class }, new Object[] { inRecord, recInfo });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of insYdPrepSchByManual
	
	/**
	 * 목표행선/목표야드/목표동 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData updSlabTotYdTransMtlList(GridData inDto) throws JDTOException {
		 
		String szMethodName="updSlabYdStkPosSet";
		String szRcvMsg = "";
		String szLogMsg = "";
		String szOperationName = "목표행선/목표야드/목표동 수정";
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
			
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			szRcvMsg = (String)ejbConn.trx("updSlabTotYdTransMtlList",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			gdRes.setStatus("true");
			gdRes.setMessage(szRcvMsg);
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);	
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("updSlabTotYdTransMtlList");
		gdRes.setMessage(szRcvMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	}
	
	/**
	 * 저장위치별 재고 List
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabTotYdStkPosList(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getSlabTotYdStkPosList";
		String szLogMsg = "";
		String szOperationName = "저장위치별 재고 List";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabTotYdStkPosList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getSlabTotYdStkPosList
	
	
	
	
	/**
	 * 선적대상재 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabShipTargetList(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getSlabShipTargetList";
		String szLogMsg = "";
		String szOperationName = "선적대상재 조회";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabShipTargetList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getSlabShipTargetList
	
	
	
	/**
	 * 선적대상재 조회 - 1차선적예정일 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData updSlabShipingSchDate(GridData inDto) throws JDTOException {
		 
		String szMethodName = "updSlabShipingSchDate";
		String szLogMsg = "";
		String szRcvMsg = "";
		String szOperationName = "선적대상재 조회 - 1차선적예정일 등록";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			szRcvMsg = (String)ejbConn.trx("updSlabShipingSchDate",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			gdRes.setStatus("true");
			gdRes.setMessage(szRcvMsg);
		
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		gdRes.setStatus("updSlabTotYdTransMtlList");
		gdRes.setMessage(szRcvMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of updSlabShipingSchDate
	
	
	
	/**
	 * 선적대상재 조회 - 2차선적예정일 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData updSlabShipingSchDate2(GridData inDto) throws JDTOException {
		 
		String szMethodName = "updSlabShipingSchDate2";
		String szLogMsg = "";
		String szRcvMsg = "";
		String szOperationName = "선적대상재 조회 - 2차선적예정일 등록";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			szRcvMsg = (String)ejbConn.trx("updSlabShipingSchDate2",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			gdRes.setStatus("true");
			gdRes.setMessage(szRcvMsg);
		
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		gdRes.setStatus("updSlabTotYdTransMtlList");
		gdRes.setMessage(szRcvMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of updSlabShipingSchDate2
	
	
	
	
	/**
	 * 1차 선적대상일 SMS 전송
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData sendSmsShipDate1(GridData inDto) throws JDTOException {
		 
		String szMethodName = "sendSmsShipDate1";
		String szLogMsg = "";
		String szRcvMsg = "";
		String szOperationName = "1차 선적대상일 SMS 전송";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			szRcvMsg = (String) ejbConn.trx("SlabJspSeEJB", "sendSmsShipDate1", inRecord);
		
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			gdRes.setStatus("true");
			gdRes.setMessage(szRcvMsg);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		return gdRes;
	} //end of sendSmsShipDate1
	
	
	
	
	/**
	 * 2차 선적대상일 SMS 전송
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData sendSmsShipDate2(GridData inDto) throws JDTOException {
		 
		String szMethodName = "sendSmsShipDate2";
		String szLogMsg = "";
		String szRcvMsg = "";
		String szOperationName = "2차 선적대상일 SMS 전송";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			szRcvMsg = (String) ejbConn.trx("SlabJspSeEJB", "sendSmsShipDate2", inRecord);
		
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			gdRes.setStatus("true");
			gdRes.setMessage(szRcvMsg);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		return gdRes;
	} //end of sendSmsShipDate2
	
	
	
	
	/**
	 * 1차 선적대상재 중량/매수 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabShipListInfo1(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getSlabShipListInfo1";
		String szLogMsg = "";
		String szOperationName = "1차 선적대상재 중량/매수 조회";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabShipListInfo1", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getSlabShipListInfo1
	
	
	
	/**
	 * 2차 선적대상재 중량/매수 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabShipListInfo2(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getSlabShipListInfo2";
		String szLogMsg = "";
		String szOperationName = "2차 선적대상재 중량/매수 조회";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabShipListInfo2", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getSlabShipListInfo2
	
	
	
	
	/**
	 *  입출고 현황(통합슬라브야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabTotYdInOutList(GridData inDto) throws JDTOException {
	 
		String szMethodName = "getSlabTotYdStkPosList";
		String szLogMsg = "";
		String szOperationName = "입출고 현황(통합슬라브야드)";
		
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : " + szOperationName + "] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabTotYdInOutList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(),YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getSlabTotYdInOutList
	
	
	/**
	 *  입출고 현황(통합슬라브야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabTotYdInOutListDong(GridData inDto) throws JDTOException {
	 
		String szMethodName = "getSlabTotYdInOutListDong";
		String szLogMsg = "";
		String szOperationName = "입출고 현황(통합슬라브야드)";
		
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : " + szOperationName + "] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabTotYdInOutListDong", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(),YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getSlabTotYdInOutList
	
	

	/**
	 *  작업예약 조회(슬라브야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getYdWrkbook_page(GridData inDto) throws JDTOException {
	 
		String szMethodName = "getYdWrkbook_page";
		String szLogMsg = "";
		String szOperationName = "작업예약 조회(슬라브야드)";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdWrkbook_page", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(),YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getYdWrkbook_page
	
	
	
	/**
	 *  작업예약 재료 조회(슬라브야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getYdWrkbook_dtl_page(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getYdWrkbook_dtl_page";
		String szLogMsg = "";
		String szOperationName = "작업예약 재료 조회(슬라브야드)";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdWrkbook_dtl_page", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getYdWrkbook_dtl_page
	
	/**
	 * 크레인작업예약관리 - 작업예약 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData delYdWrkbook(GridData inDto) throws JDTOException {
		String szOperationName			= "작업예약삭제";
		String szMethodName				= "delYdWrkbook";
		String rtnMsg 					= "";
		String szLogMsg 				= "";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
			
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			rtnMsg = (String)ejbConn.trx("delYdWrkbook",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			
			gdRes.setMessage(rtnMsg);
			
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝 - 반환메세지 : " + rtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		}catch(Exception e){
			szLogMsg	= "[Jsp Facade - "+szOperationName+"] 예외발생 - 메세지 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);	
			
			gdRes.setMessage(e.getMessage());
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		
		return gdRes;
	}
	
	
	/**
	 * C연주 슬라브야드 압연 지시 조회화면 ( 목표행선, 목표동 수정기능)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData updSlabYdRollCmdRef(GridData inDto) throws JDTOException {
		 
		String szMethodName="updSlabYdRollCmdRef";
		String rtnMsg = "";
		String szLogMsg = "";
		String szOperationName = "목표행선, 목표동 수정기능";
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
			
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			rtnMsg = (String)ejbConn.trx("updSlabYdRollCmdRef",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);	
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(rtnMsg);
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;
	}
	
	
	
	/**
	 *  슬라브 상세정보 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabYdStrlocIdInfojl(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getSlabYdStrlocIdInfojl";
		String szLogMsg = "";
		String szOperationName = "슬라브 상세정보 조회";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdStrlocIdInfojl", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getSlabYdStrlocIdInfojl

	
	/**
	 *  대차 스케줄  조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getEqpTCarSchInfo(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getEqpTCarSchInfo";
		String szLogMsg = "";
		String szOperationName = " 대차 스케줄  조회";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getEqpTCarSchInfo", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getEqpTCarSchInfo
	
	
	
	/**
	 *  대차 작업 대기 현황
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getTCarWrkWaitList(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getTCarWrkWaitList";
		String szLogMsg = "";
		String szOperationName = "대차 작업 대기 현황";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getTCarWrkWaitList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		
		return gdRes;
	} //end of getTCarWrkWaitList
	
	
	
	/**
	 *  대차작업 재료 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getTCarSchWrkMtl(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getTCarSchWrkMtl";
		String szOperationName = "대차작업 재료 ";
		String szLogMsg = "";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getTCarSchWrkMtl", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getTCarSchWrkMtl
	
	
	/**
	 * 통합야드 차량작업관리 배차내역  조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getSlabTotYdCarSch(GridData inDto) throws JDTOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getSlabTotYdCarSch";
		String szLogMsg = "";
		String szOperationName = "배차내역  조회";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabTotYdCarSch", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;
	} //end of getSlabTotYdCarSch
	
	/**
	 * 통합야드 차량작업관리 - 차량작업상세내역
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getSlabTotCarWork(GridData inDto) throws JDTOException {
		//LOG
		String szMsg        = null;
		String szMethodName = "getCoilYdGdsCarWork";
		String szOperationName = "차량작업관리 - 차량작업상세내역";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szMsg = "[JSP Facade] " + szOperationName + " 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabTotCarWork", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			szMsg = "[JSP Facade] " + szOperationName + " 완료 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			szMsg = "[JSP Facade] " + szOperationName + " 시 오류발생 - " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getSlabTotCarWork
	
	
	/**
	 * 구입슬라브의 제조사 목록 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getMakeNameList(GridData inDto) throws JDTOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getSlabTotYdCarSch";
		String szOperationName="구입슬라브제조사목록";
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szMsg = "[JSP Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getMakeNameList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			szMsg = "[JSP Facade : "+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			szMsg = "[JSP Facade : "+szOperationName+"] 예외발생 - 메세지 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getMakeNameList
/**
	 * 재료정보 및 유무 체크
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getCheckStlNo (GridData inDto) throws JDTOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCheckStlNo";
		String szLogMsg = "";
		String szOperationName = "재료정보 및 유무 체크";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szMsg = "[JSP Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getCheckStlNo", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szMsg = "[JSP Facade : "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
		return gdRes;
	} //end of getCheckStlNo
	
	
	
	
	
	/**
	 * 설비휴지이력조회 
	 *
	 * 권오창
	 * 2009.11.10
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData getslabYdEqpPauseHist(GridData inDto) throws JDTOException {
		GridData gdRes          = null;
		EJBConnector ejbConn    = null;
		JDTORecordSet recordSet = null;
	 
		
		String szMethodName     = "getslabYdEqpPauseHist";
		String szMsg            = "";			
		String szOperationName = "설비휴지이력조회";

		
		try{
			szMsg = "[JSP Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);			
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet)ejbConn.trx("SlabJspSeEJB", "getslabYdEqpPauseHist", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			 
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szMsg = "[JSP Facade : "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getslabYdEqpPauseHist
	
	
	
	
	
	/**
	 * 설비휴지이력조회 (삭제)
	 *
	 *	권오창
	 *  2009.11.11
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData delslabYdEqpPauseHist(GridData gdReq) throws JDTOException {
		GridData gdRes       = null;
		EJBConnector ejbConn = null;
	
		
		String szMethodName  = "delslabYdEqpPauseHist";
		String szMsg         = "";
		String szOperationName = "설비휴지이력조회 (삭제)";
		
		
		try{
			
			szMsg = "[JSP Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			ejbConn.trx("delslabYdEqpPauseHist", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			 
		}catch(Exception e){
			throw new JDTOException(getClass().getName() + e.getMessage(),e);
			//ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
		}		
		
		szMsg = "[JSP Facade : "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return gdRes;
	}

	
	
	
	
	/**
	 * 스카핑/정정보급재료LIST
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData getYdScarfShearSupMtlList(GridData inDto) throws JDTOException {
		String szOperationName	= "스카핑/정정보급재료LIST";
		String szMethodName 	= "getYdScarfShearSupMtlList";
		String szRtnMsg			= YdConstant.RETN_CD_SUCCESS;
		String szLogMsg			= null;
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdScarfShearSupMtlList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}catch(Exception e){
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 오류발생 - 메세지 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		return gdRes;
	} //end of getCarAsgnStdByCrn
	
	
	/**
	 * 	스카핑/정정보급LOT등록 - 자동
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData insCSlabSupPrepSchAuto(GridData inDto) throws JDTOException {
		/*
		 * 업무기준 : 스카핑/정정보급LOT등록(자동) 대상재를 쿼리로 조회하여 등록 처리
		 * 수정자 : 임춘수
		 * 수정일 : 2009.11.11
		 */
		String szMethodName = "insCSlabSupPrepSchAuto";
		String szOperationName = "스카핑/정정보급LOT등록(자동)";
		String szRtnMsg = null;
		String szLogMsg = "";
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			szRtnMsg = (String) ejbConn.trx("SlabJspSeEJB", szMethodName, inRecord);
			gdRes = OperateGridData.cloneResponseGridData(inDto);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of insCSlabSupPrepSchAuto
	
	/**
	 * 스카핑/정정보급LOT등록 - 수동
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData insCSlabSupPrepSchManual(GridData inDto) throws JDTOException {
		/*
		 * 업무기준 : 그리드에 선택된 대상재를 준비스케줄에 등록
		 * 수정자 : 임춘수
		 * 수정일 : 2009.11.11
		 */
		String szMethodName = "insCSlabSupPrepSchManual";
		String szOperationName = "스카핑/정정보급LOT등록(수동)";
		String szRtnMsg		= null;
		String szLogMsg = "";
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			szRtnMsg = (String)ejbConn.trx(szMethodName,
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of insCSlabSupPrepSchManual
	
	
	
	
	
	/**
	 * 설비휴지테이블에 등록 (팝업)
	 * 
	 * 권오창
	 * 2009.11.12
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */ 
	public GridData InsEqpPauseHist(GridData inDto) throws JDTOException {
		// 객체 선언
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		
		// 레코드 선언
		JDTORecordSet recordSet = null;
		
		// 변수 선언
		String szMethodName     = "InsEqpPauseHist";
		String szMsg            = "";
		String szOperationName = "설비휴지테이블에 등록 (팝업)";
		
		try{
			szMsg = "[Jsp Facade : "+szOperationName+"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			// 레코드 생성
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			
			// EJB Connector 생성 및 호출
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet)ejbConn.trx("SlabJspSeEJB", "InsEqpPauseHist", inRecord);
			
			// 호출 결과를 GridData 타입으로 반환
			gdRes = CmUtil.genGridData(inDto, recordSet);
			
			szMsg = "[Jsp Facade : "+szOperationName+"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		}catch(Exception e){
			szMsg = "[JSP Facade] " + szMethodName + "  오류발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);				
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}	
	
	
	/**
	 * 상하차 작업실적 등록화면 조회쿼리1 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData getYdCrnSchLdUdWrkMgt1(GridData inDto) throws JDTOException {
		String szOperationName	= "상하차 작업실적 등록화면 조회쿼리1";
		String szMethodName 	= "getYdCrnSchLdUdWrkMgt1";
		String szRtnMsg			= YdConstant.RETN_CD_SUCCESS;
		String szLogMsg			= null;
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdCrnSchLdUdWrkMgt1", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}catch(Exception e){
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 오류발생 - 메세지 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		return gdRes;
	} //end of getYdCrnSchLdUdWrkMgt1
	
	
	
	/**
	 * 상하차 작업실적 등록화면 조회쿼리2 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData getYdCrnSchLdUdWrkMgt2(GridData inDto) throws JDTOException {
		String szOperationName	= "상하차 작업실적 등록화면 조회쿼리2";
		String szMethodName 	= "getYdCrnSchLdUdWrkMgt2";
		String szRtnMsg			= YdConstant.RETN_CD_SUCCESS;
		String szLogMsg			= null;
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdCrnSchLdUdWrkMgt2", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}catch(Exception e){
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 오류발생 - 메세지 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		return gdRes;
	} //end of getYdCrnSchLdUdWrkMgt2
	
	
	
	/**
	 * 디파일러 베드 조회 (날판번호 포함)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData getDepilerBed(GridData inDto) throws JDTOException {
		String szOperationName	= "디파일러 베드 조회";
		String szMethodName 	= "getDepilerBed";
		String szRtnMsg			= YdConstant.RETN_CD_SUCCESS;
		String szLogMsg			= null;
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getDepilerBed", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}catch(Exception e){
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 오류발생 - 메세지 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		return gdRes;
	} //end of getDepilerBed
	
	
	
	
	/**
	 * A 후판 TAKE - IN 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData updTakeInPlateYd(GridData inDto) throws JDTOException {
		/*
		 * 업무기준 : 그리드에 선택된 대상재를 TAKE-IN
		 * 수정자 : 이현성
		 * 수정일 : 2009.12.24
		 */
		String szMethodName = "updTakeInPlateYd";
		String szOperationName = "A 후판 TAKE - IN";
		String szRtnMsg		= null;
		String szLogMsg = "";
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			szRtnMsg = (String)ejbConn.trx(szMethodName,
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of updTakeInPlateYd
	
	
	/**
	 * A 후판 보급요구 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData updSubReqPlateYd(GridData inDto) throws JDTOException {
		/*
		 * 업무기준 : 그리드에 선택된 대상재를 TAKE-IN
		 * 수정자 : 이현성
		 * 수정일 : 2009.12.24
		 */
		String szMethodName = "updSubReqPlateYd";
		String szOperationName = "A 후판 보급요구";
		String szRtnMsg		= null;
		String szLogMsg = "";
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			szRtnMsg = (String)ejbConn.trx(szMethodName,
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of updSubReqPlateYd
	
	
	/**
	 * 압연지시관리 (조회)_A후판슬라브야드
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData getslabYdRollCmdRef_aPlate(GridData inDto) throws JDTOException {
		 
		String szMethodName="getslabYdRollCmdRef_aPlate";
		String szLogMsg = "";
		String szOperationName	= "압연지시관리 (조회)_A후판슬라브야드";
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getslabYdRollCmdRef_aPlate", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			 
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getslabYdRollCmdRef_aPlate
	
	
	/**
	 * A 후판 준비LOT취소 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData delDepilerSupPrepSch(GridData inDto) throws JDTOException {
		/*
		 * 업무기준 : 그리드에 선택된 대상재를 TAKE-IN
		 * 수정자 : 이현성
		 * 수정일 : 2009.12.24
		 */
		String szMethodName = "delDepilerSupPrepSch";
		String szOperationName = "A 후판 준비LOT취소";
		String szRtnMsg		= null;
		String szLogMsg = "";
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			szRtnMsg = (String)ejbConn.trx(szMethodName,
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of delDepilerSupPrepSch
	
	
	/**
	 * A 후판 준비크레인변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData updDepilerSupPrepSchWrkPlnCrn(GridData inDto) throws JDTOException {
		/*
		 * 업무기준 : 그리드에 선택된 대상재를 TAKE-IN
		 * 수정자 : 이현성
		 * 수정일 : 2009.12.24
		 */
		String szMethodName = "updDepilerSupPrepSchWrkPlnCrn";
		String szOperationName = "A 후판 준비크레인변경";
		String szRtnMsg		= null;
		String szLogMsg = "";
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			szRtnMsg = (String)ejbConn.trx(szMethodName,
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of updDepilerSupPrepSchWrkPlnCrn
	
	
	/**
	 * 슬라브야드 Depilier 장입 메뉴얼 작업지시 편성
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData slabYdDepilerManualReq(GridData inDto) throws JDTOException {
		/*
		 * 업무기준 : 그리드에 선택된 대상재를 TAKE-IN
		 * 수정자 : 이현성
		 * 수정일 : 2009.12.24
		 */
		String szMethodName = "slabYdDepilerManualReq";
		String szOperationName = "A 후판 장입 메뉴얼 작업지시 편성";
		String szRtnMsg		= null;
		String szLogMsg = "";
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			szRtnMsg = (String)ejbConn.trx(szMethodName,
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of slabYdDepilerManualReq
	
	
	/**
	 *  슬라브야드 Dummy 이적 메뉴얼 작업지시 편성
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData slabYdDummyManualReq(GridData inDto) throws JDTOException {
		/*
		 * 업무기준 : 그리드에 선택된 대상재를 TAKE-IN
		 * 수정자 : 이현성
		 * 수정일 : 2009.12.24
		 */
		String szMethodName = "slabYdDummyManualReq";
		String szOperationName = "A 후판 Dummy 이적 메뉴얼 작업지시 편성";
		String szRtnMsg		= null;
		String szLogMsg = "";
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			szRtnMsg = (String)ejbConn.trx(szMethodName,
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of slabYdDummyManualReq
	
	
	/**
	 * 차량작업관리화면 상차완료처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData complCarLdLot(GridData inDto) throws JDTOException {
		//		LOG
		String szMsg = "";
		String szMethodName="complCarLdLot";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		
		try{
			JDTORecord inRecord =  ydComUtil.genParamToJDTORecord(inDto);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 상차완료처리 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("complCarLdLot", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 상차완료처리 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage(szRtnMsg);
		}catch(Exception e){
			szMsg = "[JSP Facade] 차량작업관리 화면 상차완료처리 오류발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
			try {
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(YdConstant.RETN_CD_FAILURE);
			}catch(Exception ex) {
				szMsg = "[JSP Facade] 차량작업관리 화면 상차완료처리 오류발생2 : " + ex.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			
		}
		return gdRes;
	}  //end of complCarLdLot
	
	/**
	 * 차량작업관리화면 상차완료처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData complCarLdLot2(GridData inDto) throws JDTOException {
		//		LOG
		String szMsg = "";
		String szMethodName="complCarLdLot2";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		
		try{
			JDTORecord inRecord =  ydComUtil.genParamToJDTORecord(inDto);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 상차완료처리 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("complCarLdLot2", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 상차완료처리 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage(szRtnMsg);
		}catch(Exception e){
			szMsg = "[JSP Facade] 차량작업관리 화면 상차완료처리 오류발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
			try {
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(YdConstant.RETN_CD_FAILURE);
			}catch(Exception ex) {
				szMsg = "[JSP Facade] 차량작업관리 화면 상차완료처리 오류발생2 : " + ex.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			
		}
		return gdRes;
	}  //end of complCarLdLot2
	
	
	/**
	 * 차량작업관리화면 하차완료처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData complCarUdLot2(GridData inDto) throws JDTOException {
		//		LOG
		String szMsg = "";
		String szMethodName="complCarUdLot2";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		
		try{
			JDTORecord inRecord =  ydComUtil.genParamToJDTORecord(inDto);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 하차완료처리 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("complCarUdLot2", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 하차완료처리 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage(szRtnMsg);
		}catch(Exception e){
			szMsg = "[JSP Facade] 차량작업관리 화면 하차완료처리 오류발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
			try {
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(YdConstant.RETN_CD_FAILURE);
			}catch(Exception ex) {
				szMsg = "[JSP Facade] 차량작업관리 화면 하차완료처리 오류발생2 : " + ex.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
				throw new JDTOException(getClass().getName() + e.getMessage(), e);
			}
			
		}
		return gdRes;
	}  //end of complCarUdLot2
	
	/**
	 * 차량 작업 관리 화면 :장비상태초기화 처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData updCarWrMgt(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="updCarWrMgt";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		JDTORecord outRecord2   	= JDTORecordFactory.getInstance().create();		
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 차량작업관리 초기화  전송처리 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			outRecord2 = (JDTORecord)ejbConn.trx("updCarWrMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			String sRTN_CD	= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
					
			
			szMsg = "[JSP Facade] 차량작업관리 초기화  수정  전송처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage("정상적으로 초기화 처리 되었습니다.");
		}catch(Exception e){
			szMsg = "[JSP Facade] 차량작업관리 초기화  오류발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
			try {
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(YdConstant.RETN_CD_FAILURE);
			}catch(Exception ex) {
				szMsg = "[JSP Facade] 차량작업관리 초기화  오류발생2 : " + ex.getMessage();
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
			}
			
		}
		return gdRes;
	}  //end of uptCarSch
	
	/**
	 * lot 대상 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData getSlabYdStkcar(GridData inDto) throws JDTOException {
		
		String szMethodName="getSlabYdStkcar";
		String szLogMsg ="";
		String szOperationName	= "LOT대상 조회";
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdStkcar", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			 
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}	
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;
	}
	
	/**
	 * 장비에 대한 저장위치 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData getSlabYdStkcar2(GridData inDto) throws JDTOException {
		
		String szMethodName="getSlabYdStkcar2";
		String szLogMsg ="";
		String szOperationName	= "장비저장위치 조회";
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdStkcar2", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			 
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}	
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;
	}
	
	
	/**
	 *  상차작업 저장위치 수정 
	 * 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updSlabYdStkcarFix_Tot(GridData inDto) throws JDTOException {
		//LOG
		String szMethodName="updSlabYdStkcarFix_Tot";
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szOperationName	= "상차작업 저장위치 수정";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szLogMsg ="";

		try{
			
			szLogMsg = "JSP-FACADE [" + szOperationName + "]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
			
			/***********************************************************************************************
			 * TO 위치의 정보가 원래 정보의 저장위치 정보에서 목표야드 또는 현야드가 아닐경우는 ERROR RETURN 
			 * 하는 로직추가 전 저장위치 정보가 없을경우는 무시함
			 * 
			 **********************************************************************************************/
						
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("updSlabYdStkcarFix_Tot", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			 
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		
		szLogMsg = "JSP-FACADE [ " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;
	}
	/**
	 * 통합야드 차량작업관리 배차내역  조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getSlabTotYdCarStlSch(GridData inDto) throws JDTOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getSlabTotYdCarStlSch";
		String szLogMsg = "";
		String szOperationName = "배차내역상세  조회";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabTotYdCarStlSch", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;
	} //end of getSlabTotYdCarStlSch
	
	/**
	 * 지연내용등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updSlabYdDelyRetMgt(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="updSlabYdDelyRetMgt";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		JDTORecord outRecord2   	= JDTORecordFactory.getInstance().create();		
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 지연내용등록  시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			outRecord2 = (JDTORecord)ejbConn.trx("updSlabYdDelyRetMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			String sRTN_CD	= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
					
			
			szMsg = "[JSP Facade] 지연내용등록 처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage("정상적으로 지연내용등록  처리 되었습니다.");
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}
		return gdRes;
	}  //end of updSlabYdDelyRetMgt
	
	
	
	
	/**
	 * 이송예상일조회 List
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabTotYdToMoveList(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getSlabTotYdToMoveList";
		String szLogMsg = "";
		String szOperationName = "이송예상일조회 List";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabTotYdToMoveList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getSlabTotYdToMoveList
	
	
	/**
	 * 슬라브이송지연사유등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabTotYdToMoveMgt(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getSlabTotYdToMoveMgt";
		String szLogMsg = "";
		String szOperationName = "슬라브이송지연사유등록 List";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabTotYdToMoveMgt", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getSlabTotYdToMoveMgt
	
	
	/**
	 * 벤딩처리  : 2016.04.27
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData updStockBendReg(GridData inDto) throws JDTOException {
		 
		String szMethodName = "updStockBendReg";
		String szLogMsg = "";
		String szOperationName = "벤딩처리(Fa)";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			return (GridData)ejbConn.trx("updStockBendReg", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
	} //end of updStockBendReg		
	
	
	
	/**
	 * 마킹처리  : 2019.07.18
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData updStockMarkReg(GridData inDto) throws JDTOException {
		 
		String szMethodName = "updStockMarkReg";
		String szLogMsg = "";
		String szOperationName = "마킹처리(Fa)";
		
		EJBConnector  ejbConn   = null;
		
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			return (GridData)ejbConn.trx("updStockMarkReg", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
	} //end of updStockMarkReg	 
	
	
	
	/**
	 * Q재등록  : 2017.09.22
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData updStockQslabReg(GridData inDto) throws JDTOException {
		 
		String szMethodName = "updStockQslabReg";
		String szLogMsg = "";
		String szOperationName = "Q재등록(Fa)";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			return (GridData)ejbConn.trx("updStockQslabReg", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
	} //end of updStockQslabReg	
	
	
	
	/**
	 * 마킹완료 실적조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException 
	 */	
	public GridData getYdSlabmarkingHist(GridData inDto) throws JDTOException {
	 
		String szMethodName="getYdSlabmarkingHist";
		String szLogMsg = "";
		String szOperationName	= "마킹완료 실적조회";
			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getYdSlabmarkingHist", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getYdSlabmarkingHist
	
	
	/**
	 * 마킹등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData updStockMarkingReg(GridData inDto) throws JDTOException {
		 
		String szMethodName = "updStockBendReg";
		String szLogMsg = "";
		String szOperationName = "마킹등록";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			return (GridData)ejbConn.trx("updStockMarkingReg", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
	} //end of updStockMarkingReg		
	
	
	
	/**
	 * 통합야드 업무일지 근무자정보 및 작업내용 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabYdDailyWorkInfo(GridData inDto) throws JDTOException {
		String szMethodName="getSlabYdDailyWorkInfo";
		String szLogMsg = "";
		String szOperationName	= "통합야드 업무일지 근무자정보 및 작업내용 조회";
			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdDailyWorkInfo", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getSlabYdDailyWorkInfo
	
	
	
	/**
	 * 통합야드 업무일지 근무자정보 및 작업내용 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData updSlabYdDailyWorkInfo(GridData inDto) throws JDTOException {
		String szMethodName = "updSlabYdDailyWorkInfo";
		String szLogMsg = "";
		String szOperationName = "통합야드 업무일지 근무자정보 및 작업내용 등록";
		
		
		//GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		//JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			return (GridData)ejbConn.trx("updSlabYdDailyWorkInfo", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
	}//end of updSlabYdDailyWorkInfo
	
	
	
	/**
	 * 통합야드 업무일지 재고현황 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabYdDailyCntByBay(GridData inDto) throws JDTOException {
		String szMethodName="getSlabYdDailyCntByBay";
		String szLogMsg = "";
		String szOperationName	= "통합야드 업무일지 재고현황 조회";
			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdDailyCntByBay", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}//end of getSlabYdDailyCntByBay
	
	
	/**
	 * 통합야드 업무일지_행선판매재고별현황
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabYdDailyCntByRt(GridData inDto) throws JDTOException {
		String szMethodName="getSlabYdDailyCntByRt";
		String szLogMsg = "";
		String szOperationName	= "통합야드 업무일지_행선판매재고별현황";
			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdDailyCntByRt", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}//end of getSlabYdDailyCntByRt
	/**
	 * 통합야드 업무일지_행선판매재고별현황 이송대기량 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabYdDailyCntByRtWait(GridData inDto) throws JDTOException {
		String szMethodName="getSlabYdDailyCntByRtWait";
		String szLogMsg = "";
		String szOperationName	= "통합야드 업무일지_행선판매재고별현황이송대기량";
			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdDailyCntByRtWait", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}//end of getSlabYdDailyCntByRtWait
	
	/**
	 * 통합야드 업무일지_행선판매재고별현황 이송대기량 과거데이터 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabYdDailyCntByRtWaitPast(GridData inDto) throws JDTOException {
		String szMethodName="getSlabYdDailyCntByRtWaitPast";
		String szLogMsg = "";
		String szOperationName	= "통합야드 업무일지_행선판매재고별현황 과거 이송대기량";
			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdDailyCntByRtWaitPast", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}//end of getSlabYdDailyCntByRtWait
	/**
	 * 통합야드 업무일지 장비별 작업현황 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabYdDailyCntByEQPID(GridData inDto) throws JDTOException {
		String szMethodName="getSlabYdDailyCntByEQPID";
		String szLogMsg = "";
		String szOperationName	= "통합야드 업무일지 장비별 작업현황 조회";
			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdDailyCntByEQPID", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}//end of getSlabYdDailyCntByEQPID
	
	/**
	 * 통합야드 업무일지 장비별 작업현황 비고 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabYdDailyEQPNote(GridData inDto) throws JDTOException {
		String szMethodName="getSlabYdDailyEQPNote";
		String szLogMsg = "";
		String szOperationName	= "통합야드 업무일지 장비별 작업현황 비고 조회";
			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdDailyEQPNote", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}//end of getSlabYdDailyEQPNote
	
	/**
	 * 통합야드 업무일지 외부판매 재고현황 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabYdDailySalStk(GridData inDto) throws JDTOException {
		String szMethodName="getSlabYdDailySalStk";
		String szLogMsg = "";
		String szOperationName	= "통합야드 업무일지 외부판매 재고현황 조회";
			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdDailySalStk", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getSlabYdDailySalStk

	
	
	/**
	 * 통합야드 업무일지 외부판매 재고현황 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData updSlabYdDailySalStk(GridData inDto) throws JDTOException {
		String szMethodName = "updSlabYdDailySalStk";
		String szLogMsg = "";
		String szOperationName = "통합야드 업무일지 외부판매 재고현황 등록";
		
		EJBConnector  ejbConn   = null;
		
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			return (GridData)ejbConn.trx("updSlabYdDailySalStk", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
	}//end of updSlabYdDailySalStk
	
	/**
	 * 통합야드 업무일지 판매재 적치 현황 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData updSlabYdDailySaleStk(GridData inDto) throws JDTOException {
		String szMethodName = "updSlabYdDailySaleStk";
		String szLogMsg = "";
		String szOperationName = "통합야드 업무일지 판매재 적치 현황 등록";
		
		EJBConnector  ejbConn   = null;
		
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			return (GridData)ejbConn.trx("updSlabYdDailySaleStk", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
	}//end of updSlabYdDailySaleStk
	/**
	 * 통합야드 업무일지 승인자 가져오기
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabYdDailyAcceptor(GridData inDto) throws JDTOException {
		String szMethodName="getSlabYdDailyAcceptor";
		String szLogMsg = "";
		String szOperationName	= "통합야드 업무일지 승인자 가져오기";
			
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabYdDailyAcceptor", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getSlabYdDailySalStk
	
	
	/**
	 *      [A] 오퍼레이션명 : 구내운송 회송처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData runTsRetHt(GridData gdReq) throws DAOException {
		
		String szMethodName = "runTsRetHt";
		String szLogMsg = "";
		String szOperationName = "구내운송 회송처리";

		try {
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("runTsRetHt", new Class[] { GridData.class }, new Object[] { gdReq });
			
			if("fail".equals(jrRst.getFieldString("isSuccess"))){
				szLogMsg = "회송처리 실패. 오류 메세지: "+jrRst.getFieldString("rtnMSG");
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
				gdRet.setMessage(jrRst.getFieldString("rtnMSG"));
				return gdRet;
				
			}
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				//jrRst.setResultCode(logId);
				jrRst.setResultMsg(szMethodName);

				EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			szLogMsg = "[Jsp Facade : "+ szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName , szMethodName , szLogMsg , YdConstant.DEBUG);

			//조회결과
			return gdRet;
			
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	} // end of runTsRetHt	
	
	/**
	 *  후판슬라브 테스트 슬라브 등록 
	 *  CASE 1. 붉은 바탕 슬라브- STKLYR엔 존재하지만, STOCK에는 존재하지 않는 슬라브. SLABCOMM 및 MSLABCOMM 에는 존재
	 *  CASE 2. 빈칸 슬라브(나타내지 않음) - STKLYR상은 존재하지 않지만, STOCK 상에는 존재하는 슬라브. SLABCOMM 및 MSLABCOMM 에는 존재
	 *  따라서, CASE 1의 경우 SLABCOMM 의 재료를 바탕으로 STOCK 에 삽입, CASE2 의 경우 존재하는 STOCK 정보를 STKLYR에 작업자가 INSERT 할 수 있게끔 기능
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData makePlateYdTestSlab(GridData inDto) throws DAOException {
		String szMethodName = "makePlateYdTestSlab";
		String szLogMsg = "";
		String szOperationName = "후판슬라브 테스트 슬라브 등록";
		
		try {
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
	
			EJBConnector ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			
			
			//JDTORecord jrRst = (JDTORecord)ejbConn.trx("makePlateYdTestSlab", new Class[] { GridData.class }, new Object[] { inDto });
				
			//조회
			//GridData gdRet = OperateGridData.cloneResponseGridData(inDto);
			GridData gdRet = (GridData)ejbConn.trx("makePlateYdTestSlab", new Class[] { GridData.class }, new Object[] { inDto });
			
			szLogMsg = "[Jsp Facade : "+ szOperationName+"] 메소드 결과";
			ydUtils.putLog(szSessionName , szMethodName , szLogMsg , YdConstant.DEBUG);
			int rowCnt = inDto.getHeader("CHECK").getRowCount();
			
			for (int i=0; i<rowCnt; i++){
				szLogMsg = gdRet.getParam("STL"+i);
				ydUtils.putLog(szSessionName , szMethodName , szLogMsg , YdConstant.DEBUG);
			}
			
			szLogMsg = "[Jsp Facade : "+ szOperationName+"] 메소드 종료";
			ydUtils.putLog(szSessionName , szMethodName , szLogMsg , YdConstant.DEBUG);
			
			//조회결과
			return gdRet;
			
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	/**
	 * 슬라브 이송 종합 모니터링 통합야드 상/하차 포인트 지정 대기 현황
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabFtmvPointWaitOccrDaily(GridData inDto) throws JDTOException {
		String szMethodName="getSlabFtmvPointWaitOccrDaily";
		String szLogMsg = "";
		String szOperationName	= "슬라브 이송 종합 모니터링 통합야드 상/하차 포인트 지정 대기 현황";
			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabFtmvPointWaitOccrDaily", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}//end of getSlabFtmvPointWaitOccrDaily
	
	/**
	 * 슬라브 이송 종합 모니터링 상/하차 대기 현황
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getSlabFtmvWaitOccrDaily(GridData inDto) throws JDTOException {
		String szMethodName="getSlabFtmvWaitOccrDaily";
		String szLogMsg = "";
		String szOperationName	= "슬라브 이송 종합 모니터링 상/하차 대기 현황";
			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getSlabFtmvWaitOccrDaily", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}//end of getSlabFtmvPointWaitOccrDaily
	
	/**
	 *      [A] 오퍼레이션명 : 슬라브야드 인터락 구역 설정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updSlabInterlockSect(GridData gdReq) throws DAOException {
		
		String szMethodName = "updSlabInterlockSect";
		String szLogMsg = "";
		String szOperationName = "슬라브야드 인터락 구역 설정";

		try {
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			String trtGp = slabUtils.trim(gdReq.getParam("TRT_GP"));
			String ejbMethod = "";

			/*int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for(int i=0; i<rowCnt; i++){
				String crud = slabUtils.trim(gdReq.getHeader("CRUD").getValue(i));
				
				if("C".equals(crud)) ejbMethod = "insSlabInterlockSect";
				//else if ("U".equals(crud))
			}*/
			
			if("ins".equals(trtGp)) ejbMethod = "insSlabInterlockSect";
			else if("del".equals(trtGp)) ejbMethod = "delSlabInterlockSect";
			else if("mod".equals(trtGp)) ejbMethod = "modSlabInterlockSect";
			else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });
			

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				//jrRst.setResultCode(logId);
				jrRst.setResultMsg(szMethodName);

				EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			szLogMsg = "[Jsp Facade : "+ szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName , szMethodName , szLogMsg , YdConstant.DEBUG);

			//조회결과
			return gdRet;
			
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	} // end of updSlabInterlockSect	
	
	/**
	 *      [A] 오퍼레이션명 : 통합슬라브야드 기준 관리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updSlabTotYdRuleMgt(GridData gdReq) throws DAOException {
		
		String szMethodName = "updSlabTotYdRuleMgt";
		String szLogMsg = "";
		String szOperationName = "통합슬라브야드 기준 관리";

		try {
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			String trtGp = slabUtils.trim(gdReq.getParam("TRT_GP"));
			String ejbMethod = "";

			if("insBay".equals(trtGp)) ejbMethod = "insSlabTotYdEqpWorkBay";
			else if("delBay".equals(trtGp)) ejbMethod = "delSlabTotYdEqpWorkBay";
			else if("updEqp".equals(trtGp)) ejbMethod = "updSlabTotYdEqpStat";
			else if("updEqpTrn".equals(trtGp)) ejbMethod = "updSlabTotYdEqpWrkTrnEqpCd";
			else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });
			
			/*if("fail".equals(jrRst.getFieldString("isSuccess"))){
				szLogMsg = "회송처리 실패. 오류 메세지: "+jrRst.getFieldString("rtnMSG");
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
				gdRet.setMessage(jrRst.getFieldString("rtnMSG"));
				return gdRet;
				
			}*/
			//전송할 Data가 있으면 전송 처리
					
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			szLogMsg = "[Jsp Facade : "+ szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName , szMethodName , szLogMsg , YdConstant.DEBUG);

			//조회결과
			return gdRet;
			
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	} // end of updSlabTotYdRuleMgt	

//---------------------------------------------------------------------------------------------------------------------------------------	
	/**
	 *      [A] 오퍼레이션명 : 표준시간 계산로직 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updSlabYdRuleMgt(GridData gdReq) throws DAOException {
		
		String szMethodName = "updSlabYdRuleMgt";
		String szLogMsg = "";
		String szOperationName = "표준시간 계산로직";

		try {
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			String trtGp = slabUtils.trim(gdReq.getParam("TRT_GP"));
			String ejbMethod = "";

			if("upd".equals(trtGp)) ejbMethod = "insYdGradRule";
			else if("dlt".equals(trtGp)) ejbMethod = "dltYdGradRule";
			else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });
			
			/*if("fail".equals(jrRst.getFieldString("isSuccess"))){
				szLogMsg = "회송처리 실패. 오류 메세지: "+jrRst.getFieldString("rtnMSG");
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
				gdRet.setMessage(jrRst.getFieldString("rtnMSG"));
				return gdRet;
				
			}*/
			//전송할 Data가 있으면 전송 처리
					
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			szLogMsg = "[Jsp Facade : "+ szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName , szMethodName , szLogMsg , YdConstant.DEBUG);

			//조회결과
			return gdRet;
			
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	} // end of updSlabYdRuleMgt	
	
//---------------------------------------------------------------------------------------------------------------------------------------	
	/**
	 *      [A] 오퍼레이션명 : MES모니터링 지표관리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updMESMonitorRule(GridData gdReq) throws DAOException {
		
		String szMethodName = "MES모니터링 지표관리 [SlabJspFaEJB.updMESMonitorRule]";
		String szLogMsg = "";
		//String szOperationName = "MES모니터링 지표관리";
		String logId = slabUtils.getLogId();
		try {
			szMethodName = szMethodName + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, szMethodName, "F+");
			
			gdReq.setNavigateValue(szMethodName); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			String trtGp = slabUtils.trim(gdReq.getParam("TRT_GP"));
			String ejbMethod = "";

			if("upd".equals(trtGp)) ejbMethod = "updMESMonitorRule";
			else if("dlt".equals(trtGp)) ejbMethod = "delMESMonitorRule";
			else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRst.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRst.getFieldString("RTN_MSG"), "");
					
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("지표기준 수정이 완료 됐습니다.");	
			}
			
			slabUtils.printLog(logId, szMethodName, "F-");
			//조회결과
			return gdRet;
			
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	} // end of updSlabYdRuleMgt
	
	/**
	 *      [A] 오퍼레이션명 : 통합슬라브야드 슬라브 장비 작업 저장위치 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public JDTORecord updSlabYdStkPosFixTotNew(JDTORecord [] inRecord,String logId) throws JDTOException {
		
		String szMethodName = "updSlabYdStkPosFixTotNew";
		String szLogMsg = "";
		String szOperationName = "통합슬라브야드 슬라브 장비 작업 저장위치 수정";
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		
		JDTORecord recReturn = JDTORecordFactory.getInstance().create();
		try {
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("updSlabYdStkPosFixTotNew", new Class[]{ JDTORecord[].class , String.class} , new Object[] { inRecord,logId });
			//gdRes = OperateGridData.cloneResponseGridData(gdReq);

			szLogMsg = "[Jsp Facade : "+ szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName , szMethodName , szLogMsg , YdConstant.DEBUG);

			
			recReturn.setField("Status" , "true");
			recReturn.setResultMsg(szRtnMsg);
			
			//조회결과
			return recReturn;
			
		} catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
	} // end of updSlabYdStkPosFixTotNew	
	
	
	/**
	 * 기준Heat번호 조회 (별적/입회재 관리화면)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getDefaultHeatNo(GridData gdReq) throws DAOException {

		EJBConnector  ejbConn   = null;

		String szLogMsg = "";
		String szMethodName = "getDefaultHeatNo";
		String szOperationName	= "기준Heat번호 조회 (별적/입회재 관리화면)";
			
		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			return (GridData)ejbConn.trx("getDefaultHeatNo", new Class[] { GridData.class }, new Object[] { gdReq });
			
		} catch (Exception e) {
			
			ydUtils.putLog("SlabJspSeEJB", "getDefaultHeatNo()", e.getMessage(), YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage());
			
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

		EJBConnector  ejbConn   = null;

		String szLogMsg = "";
		String szMethodName = "getDefaultHeatNoForCombo";
		String szOperationName	= "기준Heat번호로 앞뒤 Heat번호 조회 (별적/입회재 관리화면 Heat번호 콤보박스용)";
			
		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			return (GridData)ejbConn.trx("getDefaultHeatNoForCombo", new Class[] { GridData.class }, new Object[] { gdReq });
			
		} catch (Exception e) {
			
			ydUtils.putLog("SlabJspSeEJB", "getDefaultHeatNoForCombo()", e.getMessage(), YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage());
			
		}

	}
	
	
	/**
	 * 별적/입회재 관리 시퀀스 Nextval 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSeparateStackSeq(JDTORecord recPara) throws DAOException { 
		
		String szMethodName= "getSeparateStackSeq";		
		String szLogMsg = "";
		String szOperationName	= "별적/입회재 관리 시퀀스 Nextval 조회";
		
		EJBConnector ejbConn = null;
		
		try {

			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			JDTORecordSet recordSet = (JDTORecordSet)ejbConn.trx("getSeparateStackSeq", new Class[] { JDTORecord.class }, new Object[] { recPara });

			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			//조회결과
			return recordSet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
	}
	
	/**
	* @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * [A] 오퍼레이션명 : 별적/입회재 요청등록
	 * @exception
	 * @modelguid XDE가 생성한 ID를 위치시킨다.
	 * @author  1524711
	 * @date 	2025.02.18
	 */
	public Integer insSeparateStackReq(JDTORecord[] gdReq) {
		
		String szMethodName= "insSeparateStackReq";		
		String szLogMsg = "";
		String szOperationName	= "별적/입회재 요청등록";
		
		int result = 0;
		
		EJBConnector ejbConn = null;
		try {
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			result = (Integer)ejbConn.trx("insSeparateStackReq" , new Class[] { JDTORecord[].class }, new Object[] { gdReq });
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return result;
		} catch (Exception e) {
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

		EJBConnector  ejbConn   = null;

		String szLogMsg = "";
		String szMethodName = "getSeparateReqList";
		String szOperationName	= "별적요청 조회";
			
		try{
			
			szLogMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			return (GridData)ejbConn.trx("getSeparateReqList", new Class[] { GridData.class }, new Object[] { gdReq });
			
		} catch (Exception e) {
			
			ydUtils.putLog("SlabJspSeEJB", "getSeparateReqList()", e.getMessage(), YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage());
			
		}

	}
	
	/**
	* @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * [A] 오퍼레이션명 : 입회검사 요청/확정/완료처리
	 * @exception
	 * @modelguid XDE가 생성한 ID를 위치시킨다.
	 * @author  1524711
	 * @date 	2025.02.18
	 */
	public Integer updInspectionState(JDTORecord[] gdReq) {
		
		String szMethodName= "updInspectionState";		
		String szLogMsg = "";
		String szOperationName	= "입회검사 요청/확정/완료처리";
		
		EJBConnector ejbConn = null;
		int result = 0;
		/* V_SEPARATE_PROG_GP
		-- 1 : 별적요청
		-- 2 : 별적완료
		-- 3 : 입회검사요청
		-- 4 : 입회검사확정
		-- 5 : 입회검사완료
		-- 0 : 별적요청취소
		*/
		try {

			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
	
			result =  (Integer)ejbConn.trx("updInspectionState" , new Class[] { JDTORecord[].class }, new Object[] { gdReq });
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			//조회결과
			return result;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 별적/입회재 관련 메일발송
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 *      @param param
	 *      @return
	 *      @throws DAOException
	*/
	public GridData emailForSeparateStack(GridData gdReq) throws DAOException {
		
		String szMethodName= "emailForSeparateStack";		
		String szLogMsg = "";
		String szOperationName	= "별적/입회재 관련 메일발송";
		EJBConnector ejbConn = null;
		
		try {
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);

			return (GridData)ejbConn.trx("emailForSeparateStack", new Class[] { GridData.class }, new Object[] { gdReq });
		} catch (Exception e) {
			ydUtils.putLog("SlabJspSeEJB", "emailForSeparateStack()", e.getMessage(), YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}	
	}
	
}
